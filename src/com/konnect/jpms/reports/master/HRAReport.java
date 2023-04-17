package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class HRAReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	private static Logger log = Logger.getLogger(HRAReport.class);
	
	String financialYear;
	String[] strSalaryHeadId;
	
	String strCond1;
	String strCond2;
	String strCond3;
	String hraUpdate;
	
	List<FillFinancialYears> financialYearList;
	List<FillSalaryHeads> salaryHeadList;
	public String execute() throws Exception {		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, PReportHRA);
		request.setAttribute(TITLE, "HRA Settings");
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		String []strFinancialDates = null;
		if (getFinancialYear() != null && !getFinancialYear().equals("") && !getFinancialYear().equals("null")) {
//			System.out.println(" ================>> 1");
			strFinancialDates = getFinancialYear().split("-");
//			System.out.println(" ================>> 2");
			setFinancialYear(strFinancialDates[0] + "-" + strFinancialDates[1]);
//			System.out.println(" ================>> 3");
		} else {
//			System.out.println(" ================>> else 1");
			strFinancialDates = new FillFinancialYears(request).fillLatestFinancialYears();
//			System.out.println(" ================>> else 2");
			setFinancialYear(strFinancialDates[0] + "-" + strFinancialDates[1]);
//			System.out.println(" ================>> else 3");
		}
		
		if(getHraUpdate()!=null){
			updateHRASetting(uF, strFinancialDates);
		}
		viewHRASetting(uF, strFinancialDates);
		
//		viewHRA(uF);
		
		return loadHRA(uF);
	}
	
	public String loadHRA(UtilityFunctions uF){
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		salaryHeadList = new FillSalaryHeads(request).fillSalaryHeadsWithoutCTC("-1");
		
		getSelectedFilter(uF);
		
		return LOAD; 
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("FINANCIALYEAR");
		String[] strFinancialYears = null;
		if (getFinancialYear() != null) {
			strFinancialYears = getFinancialYear().split("-");
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
			
		} else {
			strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
			
		}
		hmFilter.put("FINANCIALYEAR", uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		
		String selectedFilter = getSelectedFilter(uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	public String getSelectedFilter(UtilityFunctions uF, List<String> alFilter, Map<String, String> hmFilter) {
		StringBuilder sbFilter=new StringBuilder("<strong>HRA Administration for FY:&nbsp;&nbsp; </strong>");
		
		for(int i=0;alFilter!=null && i<alFilter.size();i++) {
			if(i>0) {
				sbFilter.append(", ");
			}
			
			if(alFilter.get(i).equals("FINANCIALYEAR")) {
				sbFilter.append("<strong>FINANCIAL YEAR:</strong> ");
				sbFilter.append(hmFilter.get("FINANCIALYEAR"));
			
			}
			
		}
		return sbFilter.toString();
	}
	
	
	public void updateHRASetting(UtilityFunctions uF, String []strFinancialDates){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		
	try {
		
		con = db.makeConnection(con);
		
		pst = con.prepareStatement("update hra_exemption_details set condition1=?, condition1_type=?, condition2=?, condition2_type=?, condition3=?, " +
					"condition3_type=?, entry_date=?, user_id=?,salary_head_id=? where financial_year_from=? and financial_year_to=? ");
		
		
		pst.setDouble(1, uF.parseToDouble(getStrCond1()));
		pst.setString(2, "P");
		pst.setDouble(3, uF.parseToDouble(getStrCond2()));
		pst.setString(4, "P");
		pst.setDouble(5, uF.parseToDouble(getStrCond3()));
		pst.setString(6, "P");
		pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
		pst.setInt(8, uF.parseToInt((String)session.getAttribute(EMPID)));
		
		StringBuilder sb = new StringBuilder();
		List<String> alSalaryHeads = new ArrayList<String>();
		for(int i=0; getStrSalaryHeadId()!=null && i<getStrSalaryHeadId().length; i++){
			if(!alSalaryHeads.contains(getStrSalaryHeadId()[i])){
				sb.append(getStrSalaryHeadId()[i]+",");
				alSalaryHeads.add(getStrSalaryHeadId()[i]);
			}
		}
		pst.setString(9, sb.toString());			
		pst.setDate(10, uF.getDateFormat(strFinancialDates[0], DATE_FORMAT));
		pst.setDate(11, uF.getDateFormat(strFinancialDates[1], DATE_FORMAT));
		int nRow = pst.executeUpdate();	
		pst.close();
		
		if(nRow==0){
			
			pst = con.prepareStatement("INSERT INTO hra_exemption_details (condition1, condition1_type, condition2, condition2_type, condition3, " +
					"condition3_type, financial_year_from, financial_year_to, entry_date, user_id,salary_head_id) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
			pst.setDouble(1, uF.parseToDouble(getStrCond1()));
			pst.setString(2, "P");
			pst.setDouble(3, uF.parseToDouble(getStrCond2()));
			pst.setString(4, "P");
			pst.setDouble(5, uF.parseToDouble(getStrCond3()));
			pst.setString(6, "P");
			pst.setDate(7, uF.getDateFormat(strFinancialDates[0], DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(strFinancialDates[1], DATE_FORMAT));
			pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(10, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setString(11, sb.toString());
			pst.execute();
			pst.close();
			
		}
		
		
		session.setAttribute(MESSAGE, SUCCESSM+"HRA policy saved successfully."+END);
		
		}catch (Exception e) {
			e.printStackTrace();
		} finally {  
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void viewHRASetting(UtilityFunctions uF, String []strFinancialDates){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> hmHRASettings = new HashMap<String, String>();
		List<List<String>> alHRASettings = new ArrayList<List<String>> ();
		
	try {
		   
		con = db.makeConnection(con); 
		
		Map<String, String> hmSalaryHeads  = CF.getSalaryHeadsMap(con);
		Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
		
		pst = con.prepareStatement(selectHRA);
		pst.setDate(1, uF.getDateFormat(strFinancialDates[0], DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialDates[1], DATE_FORMAT));
		rs = pst.executeQuery();
		while(rs.next()){

			setStrCond1(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("condition1"))));
			setStrCond2(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("condition2"))));
			setStrCond3(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("condition3"))));			
			
			setStrSalaryHeadId(rs.getString("salary_head_id").split(","));
			
			request.setAttribute("UPDATED_NAME", hmEmpName.get(rs.getString("user_id")));
			request.setAttribute("UPDATED_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
		}	
		rs.close();
		pst.close();
		
		
		pst = con.prepareStatement("select * from hra_exemption_details order by financial_year_from desc");
		rs = pst.executeQuery();
		while(rs.next()){
			
			List<String> alInner = new ArrayList<String>();
			
			alInner.add(uF.getDateFormat(rs.getString("financial_year_from"), DBDATE, "yyyy")+"-"+uF.getDateFormat(rs.getString("financial_year_to"), DBDATE, "yy"));
			alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("condition1"))));
			alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("condition2"))));
			alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("condition3"))));
			
			StringBuilder sb = new StringBuilder();
			if(rs.getString("salary_head_id")!=null){
				String []arr = rs.getString("salary_head_id").split(",");
				for(int i=0; i<arr.length; i++){
					sb.append((String)hmSalaryHeads.get(arr[i])+",");
				}
			}
			alInner.add(sb.toString());
			
			
			alHRASettings.add(alInner);
			
		}	
		rs.close();
		pst.close();
		
		
		request.setAttribute("alHRASettings", alHRASettings);
		request.setAttribute("hmHRASettings", hmHRASettings);
		
		
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public String[] getStrSalaryHeadId() {
		return strSalaryHeadId;
	}

	public void setStrSalaryHeadId(String[] strSalaryHeadId) {
		this.strSalaryHeadId = strSalaryHeadId;
	}

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}

	public String getStrCond1() {
		return strCond1;
	}

	public void setStrCond1(String strCond1) {
		this.strCond1 = strCond1;
	}

	public String getStrCond2() {
		return strCond2;
	}

	public void setStrCond2(String strCond2) {
		this.strCond2 = strCond2;
	}

	public String getStrCond3() {
		return strCond3;
	}

	public void setStrCond3(String strCond3) {
		this.strCond3 = strCond3;
	}

	public String getHraUpdate() {
		return hraUpdate;
	}

	public void setHraUpdate(String hraUpdate) {
		this.hraUpdate = hraUpdate;
	}

}
