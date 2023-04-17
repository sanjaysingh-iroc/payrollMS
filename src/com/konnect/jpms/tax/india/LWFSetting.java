package com.konnect.jpms.tax.india;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LWFSetting extends ActionSupport implements ServletRequestAware, IStatements {

	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
 
	CommonFunctions CF = null;
	
	String strOrg;
	List<FillOrganisation> orgList;
	private List<FillFinancialYears> financialYearList;
	private String financialYear;
	private List<FillSalaryHeads> salaryHeadList;
	private List<FillMonth> monthList;
	private String[] strMonth;
	private List<FillState> stateList;
	private String state;
	private String[] strSalaryHeadId;
	private String[] eelwfContribution;
	private String[] erlwfContribution;
	private String[] elwfMaxLimit;
	private String[] elwfMinLimit;
	private String lwfUpdate;

	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		if (session == null) 
			return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(PAGE, PLWFSetting);
		request.setAttribute(TITLE, TLWFSetting);

		/*
		 * boolean isView = CF.getAccess(session, request, uF); if(!isView){
		 * request.setAttribute(PAGE, PAccessDenied);
		 * request.setAttribute(TITLE, TAccessDenied); return ACCESS_DENIED; }
		 */

		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if(uF.parseToInt(getStrOrg()) == 0  && orgList!=null && orgList.size()>0){
				setStrOrg(orgList.get(0).getOrgId());
			}
		} else {
			if(uF.parseToInt(getStrOrg()) == 0){
				setStrOrg((String) session.getAttribute(ORGID));
			}
			orgList = new FillOrganisation(request).fillOrganisation();
		}
		
		String []strPayCycleDates = null;
		if (getFinancialYear() != null && !getFinancialYear().equals("") && !getFinancialYear().equals("null")) {
//			System.out.println("=========>> 1");
			strPayCycleDates = getFinancialYear().split("-");
			setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
//			System.out.println("=========>> 2");
		} else {
//			System.out.println("=========>> else 1");
			strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
			setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
//			System.out.println("=========>> else 2");
		}
		
		if (getLwfUpdate() != null) {
			updateLWFSetting(strPayCycleDates);
		}
		viewLWFSetting(uF, strPayCycleDates);

		monthList = new FillMonth().fillMonth();
		salaryHeadList = new FillSalaryHeads(request).fillSalaryHeadsWithoutCTC("-1");
//		stateList = new FillState(request).fillState("1"); // 1 is for India
		stateList = new FillState(request).fillWLocationStates();
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		
		getSelectedFilter(uF);
		
		return LOAD;
	}

	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if (getStrOrg() != null) {
			String strOrg = "";
			int k = 0;
			for (int i = 0; orgList != null && i < orgList.size(); i++) {
				if (getStrOrg().equals(orgList.get(i).getOrgId())) {
					if (k == 0) {
						strOrg = orgList.get(i).getOrgName();
					} else {
						strOrg += ", " + orgList.get(i).getOrgName();
					}
					k++;
				}
			}
			if (strOrg != null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
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
		StringBuilder sbFilter=new StringBuilder("<strong>LWF Administration for FY:&nbsp;&nbsp; </strong>");
		
		for(int i=0;alFilter!=null && i<alFilter.size();i++) {
			if(i>0) {
				sbFilter.append(",");
			}
			
			if(alFilter.get(i).equals("ORGANISATION")) {
				sbFilter.append("<strong>ORG:</strong> ");
				sbFilter.append(hmFilter.get("ORGANISATION"));
			}
			
			if(alFilter.get(i).equals("FINANCIALYEAR")) {
				sbFilter.append("<strong>FINANCIAL YEAR:</strong> ");
				sbFilter.append(hmFilter.get("FINANCIALYEAR"));
			}
		}
		return sbFilter.toString();
	}
	

	public void updateLWFSetting(String []strPayCycleDates) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		//Map<String, String> hmEPFSettings = new HashMap<String, String>();
		//List<List<String>> alEPFSettings = new ArrayList<List<String>>();

		try {

			con = db.makeConnection(con);

			
			StringBuilder sb = new StringBuilder();
			for(int i=0; getStrSalaryHeadId()!=null && i<getStrSalaryHeadId().length; i++){
				sb.append(getStrSalaryHeadId()[i]+",");
			}

			StringBuilder sbMonth = new StringBuilder();
			for(int i=0; getStrMonth()!=null && i<getStrMonth().length; i++){
				sbMonth.append(getStrMonth()[i]+",");
			}
			
			pst = con.prepareStatement("delete from lwf_details where financial_year_start=? and financial_year_end=? and state_id=? and org_id=?");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getState()));
			pst.setInt(4, uF.parseToInt(getStrOrg()));
			pst.execute();	
			pst.close();
			
			if(uF.parseToInt(getState())>0) {
				for (int i = 0; getEelwfContribution()!=null && i < getEelwfContribution().length; i++) {
					pst = con.prepareStatement("insert into lwf_details (eelfw_contribution, erlfw_contribution, min_limit,  max_limit, financial_year_start, " +
						" financial_year_end, user_id, entry_timestamp, salary_head_id, state_id, months, org_id) values (?,?,?,?, ?,?,?,?, ?,?,?,?)");
					pst.setDouble(1, uF.parseToDouble(getEelwfContribution()[i]));
					pst.setDouble(2, uF.parseToDouble(getErlwfContribution()[i]));
					pst.setDouble(3, uF.parseToDouble(getElwfMinLimit()[i]));
					pst.setDouble(4, uF.parseToDouble(getElwfMaxLimit()[i]));
					pst.setDate(5, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
					pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
					pst.setInt(7, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(8, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
					pst.setString(9, sb.toString());
					pst.setInt(10, uF.parseToInt(getState()));
					pst.setString(11, sbMonth.toString());
					pst.setInt(12, uF.parseToInt(getStrOrg()));
					pst.executeUpdate();	
					pst.close();
				}
			}

			session.setAttribute(MESSAGE, SUCCESSM + "LWF policy saved successfully." + END);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void viewLWFSetting(UtilityFunctions uF, String []strPayCycleDates) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		LinkedHashMap hmESISettings = new LinkedHashMap();
		List<List<String>> alESISettings = new ArrayList<List<String>>();

		try {

			con = db.makeConnection(con);

			Map<String, String> hmSalaryHeads = CF.getSalaryHeadsMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmStateMap = CF.getStateMap(con);

			pst = con.prepareStatement("select * from lwf_details where financial_year_start =? and financial_year_end =? and state_id=? and org_id=?");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getState()));
			pst.setInt(4, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
			List<List<String>> alOuter = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				if(rs.getString("salary_head_id")!=null){
					setStrSalaryHeadId(rs.getString("salary_head_id").split(","));
				}
				if(rs.getString("months")!=null){
					setStrMonth(rs.getString("months").split(","));	
				}
				
				setState(rs.getString("state_id"));
				
				alInner.add(rs.getString("eelfw_contribution"));
				alInner.add(rs.getString("erlfw_contribution"));
				alInner.add(rs.getString("min_limit"));
				alInner.add(rs.getString("max_limit"));
				
				alOuter.add(alInner);
			}	
			rs.close();
			pst.close();
			
			
			request.setAttribute("alOuter", alOuter);

			/*
			 * setEesiContribution(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.
			 * getString("eesi_contribution"))));
			 * setErsiContribution(uF.formatIntoTwoDecimal
			 * (uF.parseToDouble(rs.getString("ersi_contribution"))));
			 * setEsiMaxLimit(rs.getString("max_limit"));
			 */
			

			pst = con.prepareStatement("select * from lwf_details where org_id=? order by state_id, financial_year_start desc");
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
			String strStateNew = null;
			String strStateOld = null;
			List<String> alInner = new ArrayList<String>();

			while (rs.next()) {
				strStateNew = rs.getString("state_id");
				alInner = new ArrayList<String>();
				if (strStateNew != null && !strStateNew.equalsIgnoreCase(strStateOld)) {
					alESISettings = new ArrayList<List<String>>();
				}

				alInner.add(uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, "yyyy") + "-" + uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, "yy"));
				alInner.add(rs.getString("eelfw_contribution"));
				alInner.add(rs.getString("erlfw_contribution"));
				alInner.add(rs.getString("min_limit"));
				alInner.add(rs.getString("max_limit"));

				StringBuilder sb = new StringBuilder();
				if (rs.getString("salary_head_id") != null) {
					String[] arr = rs.getString("salary_head_id").split(",");
					for (int i = 0; i < arr.length; i++) {
						sb.append((String) hmSalaryHeads.get(arr[i]) + ",");
						
					}
				}
				alInner.add(sb.toString());
				
				
				sb.replace(0, sb.length(), "");
				if (rs.getString("months") != null) {
					String[] arr = rs.getString("months").split(",");
					for (int i = 0; i < arr.length; i++) {
						sb.append(uF.getMonth(uF.parseToInt(arr[i])) + ",");
					}
				}
				alInner.add(sb.toString());

				alESISettings.add(alInner);

				hmESISettings.put(hmStateMap.get(strStateNew), alESISettings);

				strStateOld = strStateNew;

				request.setAttribute("UPDATED_NAME", hmEmpName.get(rs.getString("user_id")));
				request.setAttribute("UPDATED_DATE", uF.getDateFormat(rs.getString("entry_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()));
			}	
			rs.close();
			pst.close();

			request.setAttribute("alESISettings", alESISettings);
			request.setAttribute("hmESISettings", hmESISettings);

		} catch (Exception e) {
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

	public String getStrSessionEmpId() {
		return strSessionEmpId;
	}

	public void setStrSessionEmpId(String strSessionEmpId) {
		this.strSessionEmpId = strSessionEmpId;
	}

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}

	public String[] getStrSalaryHeadId() {
		return strSalaryHeadId;
	}

	public void setStrSalaryHeadId(String[] strSalaryHeadId) {
		this.strSalaryHeadId = strSalaryHeadId;
	}

	public List<FillState> getStateList() {
		return stateList;
	}

	public void setStateList(List<FillState> stateList) {
		this.stateList = stateList;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String[] getEelwfContribution() {
		return eelwfContribution;
	}

	public void setEelwfContribution(String[] eelwfContribution) {
		this.eelwfContribution = eelwfContribution;
	}

	public String[] getErlwfContribution() {
		return erlwfContribution;
	}

	public void setErlwfContribution(String[] erlwfContribution) {
		this.erlwfContribution = erlwfContribution;
	}

	public String[] getElwfMaxLimit() {
		return elwfMaxLimit;
	}

	public void setElwfMaxLimit(String[] elwfMaxLimit) {
		this.elwfMaxLimit = elwfMaxLimit;
	}

	public String[] getElwfMinLimit() {
		return elwfMinLimit;
	}

	public void setElwfMinLimit(String[] elwfMinLimit) {
		this.elwfMinLimit = elwfMinLimit;
	}

	public String getLwfUpdate() {
		return lwfUpdate;
	}

	public void setLwfUpdate(String lwfUpdate) {
		this.lwfUpdate = lwfUpdate;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public String[] getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String[] strMonth) {
		this.strMonth = strMonth;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

}
