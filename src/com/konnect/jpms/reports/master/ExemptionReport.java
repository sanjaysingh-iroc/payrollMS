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
import com.konnect.jpms.select.FillUnderSection;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ExemptionReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	private static Logger log = Logger.getLogger(ExemptionReport.class);
	
	private List<FillUnderSection> underSection10and16List;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	private String financialYear;
	private List<FillFinancialYears> financialYearList;
	
	
	public String execute() throws Exception { 
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		request.setAttribute(PAGE, PExemption);
		request.setAttribute(TITLE, TExemption);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		viewExemption(uF);	
		
		return loadExemption(uF);

	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("FINANCIALYEAR");
		String[] strFinancialYears = null;
		if (getFinancialYear() != null && !getFinancialYear().equals("") && !getFinancialYear().equals("null")) {
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
		StringBuilder sbFilter=new StringBuilder("<strong>Filter Summary:&nbsp;&nbsp; </strong>");
		
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
	
	
	public String loadExemption(UtilityFunctions uF) {
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		
		underSection10and16List=new FillUnderSection().fillUnderSection10and16();
		request.setAttribute("underSection10and16List", underSection10and16List);
		
		String underSectionId;
		String underSectionName;
		int i=0;
		if(underSection10and16List.size()!=0) { 
			StringBuilder sbunderSectionList = new StringBuilder();
			sbunderSectionList.append("{");
		    for(; i<underSection10and16List.size()-1;i++ ) {
		    	underSectionId = (underSection10and16List.get(i)).getUnderSectionId();
		    	underSectionName = underSection10and16List.get(i).getUnderSectionName();
		    	sbunderSectionList.append("\""+ underSectionId+"\":\""+underSectionName+"\",");
		    }
		    underSectionId = (underSection10and16List.get(i)).getUnderSectionId();
		    underSectionName = underSection10and16List.get(i).getUnderSectionName();
		    sbunderSectionList.append("\""+ underSectionId+"\":\""+underSectionName+"\"");	
		    sbunderSectionList.append("}");
		    request.setAttribute("sbunderSectionList", sbunderSectionList.toString());
		    
		}
		
		StringBuilder sbInInvestmentForm = new StringBuilder();
		sbInInvestmentForm.append("{");
		sbInInvestmentForm.append("\"True\":\"True\",");
		sbInInvestmentForm.append("\"False\":\"False\"");
		sbInInvestmentForm.append("}");
		request.setAttribute("sbInInvestmentForm", sbInInvestmentForm.toString());
		
		getSelectedFilter(uF);
		
		return "load";
	}
	
	
	
	public String viewExemption(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		

		try {

			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null && !getFinancialYear().equals("") && !getFinancialYear().equals("null")) {
				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			
			} else {
				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
				
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
				 
			}
			
			con = db.makeConnection(con);
			
			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			
			StringBuilder sbSalaryHeads = new StringBuilder();
//			pst = con.prepareStatement("select distinct(salary_head_name), earning_deduction from salary_details order by earning_deduction, salary_head_name");
			pst = con.prepareStatement("select distinct(salary_head_id), salary_head_name, earning_deduction from salary_details where salary_head_id not in ("+GROSS+","+CTC+") order by earning_deduction, salary_head_name");
			rs = pst.executeQuery();
			sbSalaryHeads.append("{");
			int count =0;
			List alSalaryHeads = new ArrayList();
			List<FillSalaryHeads> salaryHeadList = new ArrayList<FillSalaryHeads>();
			while(rs.next()){
				if(count++>0){
					sbSalaryHeads.append(",");		
				}
//				sbSalaryHeads.append("\"" + rs.getString("salary_head_name") + "\":\"" + rs.getString("salary_head_name")+"["+rs.getString("earning_deduction")+"]"+ "\"");
				sbSalaryHeads.append("\"" + rs.getString("salary_head_id") + "\":\"" + rs.getString("salary_head_name")+"["+rs.getString("earning_deduction")+"]"+ "\"");
				salaryHeadList.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
				alSalaryHeads.add(rs.getString("salary_head_id"));
			}
			rs.close();
			pst.close();
			sbSalaryHeads.append("}");
			request.setAttribute("sbSalaryHeads", sbSalaryHeads.toString());    
			request.setAttribute("alSalaryHeads", alSalaryHeads);
			request.setAttribute("salaryHeadList", salaryHeadList);
			
			List<FillUnderSection> underSec10and16List = new FillUnderSection().fillUnderSection10and16();
			
			pst = con.prepareStatement(selectExemption);
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				alInner = new ArrayList<String>();
				alInner.add(Integer.toString(rs.getInt("exemption_id")));
				alInner.add(rs.getString("exemption_code"));
				alInner.add(rs.getString("exemption_name"));
				alInner.add(rs.getString("exemption_description"));
				alInner.add(rs.getString("exemption_limit"));
				
				String strUnderSection = "";
				if(uF.parseToInt(rs.getString("under_section")) > 0){
					for(int i=0; i<underSec10and16List.size();i++ ) {
						String underSectionId = (underSec10and16List.get(i)).getUnderSectionId();
				    	String underSectionName = underSec10and16List.get(i).getUnderSectionName();
				    	if(uF.parseToInt(underSectionId) == uF.parseToInt(rs.getString("under_section"))){
				    		strUnderSection = underSectionName;
				    	}
					}
				}
				alInner.add(strUnderSection);
				
				alInner.add(uF.parseToBoolean(rs.getString("investment_form")) ? "True" : "False");
				alInner.add(uF.showData(uF.getAlignedSlabTypeName(uF.parseToInt(rs.getString("slab_type"))), "")); //7
				al.add(alInner);
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("SELECT max(entry_date) as entry_date, user_id FROM exemption_details where exemption_from=? and exemption_to=? group by user_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				request.setAttribute("UPDATED_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				request.setAttribute("UPDATED_NAME", hmEmpName.get(rs.getString("user_id")));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", al);
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
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

	public List<FillUnderSection> getUnderSection10and16List() {
		return underSection10and16List;
	}

	public void setUnderSection10and16List(List<FillUnderSection> underSection10and16List) {
		this.underSection10and16List = underSection10and16List;
	}

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}

}
