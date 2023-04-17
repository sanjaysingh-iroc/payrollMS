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

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycleDuration;
import com.konnect.jpms.select.FillSalaryCalculationTypes;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PayrollSetting extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(PayrollSetting.class);
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	
	String strOrg;
	List<FillOrganisation> orgList;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		
		strUserType = (String) session.getAttribute(USERTYPE);
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/reports/master/PayrollSetting.jsp");
		request.setAttribute(TITLE, "Payroll Settings");
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if(uF.parseToInt(getStrOrg()) == 0  && orgList!=null && orgList.size()>0) {
				setStrOrg(orgList.get(0).getOrgId());
			}
		} else {
			if(uF.parseToInt(getStrOrg()) == 0){
				setStrOrg((String) session.getAttribute(ORGID));
			}
			orgList = new FillOrganisation(request).fillOrganisation();
		}
		
		viewPayrollSettings(uF);
		viewVDARateDetails(uF);
		viewVDAIndexDetails(uF);
		getSelectedFilter(uF);
		return LOAD;
 
	}
	

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if(getStrOrg()!=null) {
			String strOrg="";
			for(int i=0;orgList!=null && i<orgList.size();i++) {
				if(getStrOrg().equals(orgList.get(i).getOrgId())) {
					strOrg=orgList.get(i).getOrgName();
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organizations");
			}
		} else {
			hmFilter.put("ORGANISATION", "All Organizations");
		}
		
		String selectedFilter = CF.getSelectedFilter(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
private void viewVDAIndexDetails(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmVDAIndexData = new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select * from designation_details where level_id in (select level_id from level_details where org_id=?)");
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs=pst.executeQuery();
			while (rs.next()) {
				Map<String, String> hm = new HashMap<String, String>();
				hm.put("DESIG_NAME", rs.getString("designation_name") + "[" + rs.getString("designation_code") + "]");
				hm.put("VDA_INDEX_PROBATION", uF.showData(rs.getString("vda_index_probation"), "-"));
				hm.put("VDA_INDEX_PERMANENT", uF.showData(rs.getString("vda_index_permanent"), "-"));
				hm.put("VDA_INDEX_TEMPORARY", uF.showData(rs.getString("vda_index_temporary"), "-"));
				hmVDAIndexData.put(rs.getString("designation_id"), hm);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmVDAIndexData", hmVDAIndexData);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void viewVDARateDetails(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmVDAData = new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select * from vda_rate_details where org_id=? and (desig_id is null or desig_id=0)");
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs=pst.executeQuery();
			while (rs.next()) {
				Map<String, String> hm = new HashMap<String, String>();
				hm.put("ORG_ID", rs.getString("org_id"));
				String strPaycycle = "Paycycle "+rs.getString("paycycle")+", "+uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT)+" - " + uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT);
				hm.put("PAYCYCLE", strPaycycle);
				hm.put("VDA_RATE", rs.getString("vda_rate")); 
				
				hmVDAData.put(rs.getString("vda_rate_id"), hm);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmVDAData", hmVDAData);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}



	public String viewPayrollSettings(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			List<FillPayCycleDuration> paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();
			if(paycycleDurationList == null) paycycleDurationList = new ArrayList<FillPayCycleDuration>();
			List<FillSalaryCalculationTypes> salaryCalculationList = new FillSalaryCalculationTypes().fillSalaryCalculationTypes();
			if(salaryCalculationList == null) salaryCalculationList = new ArrayList<FillSalaryCalculationTypes>();
			
			Map<String, Map<String, String>> hmOrg = new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select * from org_details where org_id=?");
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs=pst.executeQuery();
			while (rs.next()) {
				Map<String, String> hm = new HashMap<String, String>();
				hm.put("ORG_ID", rs.getString("org_id"));
				hm.put("ORG_NAME", rs.getString("org_code"));
				hm.put("ORG_CODE", rs.getString("org_name"));
				hm.put("ORG_START_PAYCYCLE", uF.getDateFormat(rs.getString("start_paycycle"), DBDATE, CF.getStrReportDateFormat()));
				hm.put("ORG_DISPLAY_PAYCYCLE", rs.getString("display_paycycle"));
			//	hm.put("ORG_PAYSLIP_FORMAT", rs.getString("payslip_format"));
				
				if(uF.parseToInt(rs.getString("payslip_format"))== 2) {
					hm.put("ORG_PAYSLIP_FORMAT", "Format 2");
					hm.put("ORG_PAYSLIP_FORMAT_ID", rs.getString("payslip_format"));
				} else if(uF.parseToInt(rs.getString("payslip_format"))== 3) {
					hm.put("ORG_PAYSLIP_FORMAT", "Format 3");
					hm.put("ORG_PAYSLIP_FORMAT_ID", rs.getString("payslip_format"));
				} else if(uF.parseToInt(rs.getString("payslip_format"))== 4) {
					hm.put("ORG_PAYSLIP_FORMAT", "Format 4");
					hm.put("ORG_PAYSLIP_FORMAT_ID", rs.getString("payslip_format"));
				} else if(uF.parseToInt(rs.getString("payslip_format"))== 5) {
					hm.put("ORG_PAYSLIP_FORMAT", "Format 5");
					hm.put("ORG_PAYSLIP_FORMAT_ID", rs.getString("payslip_format"));
				} else if(uF.parseToInt(rs.getString("payslip_format"))== 6) {
					hm.put("ORG_PAYSLIP_FORMAT", "Format 6");
					hm.put("ORG_PAYSLIP_FORMAT_ID", rs.getString("payslip_format"));
				} else if(uF.parseToInt(rs.getString("payslip_format"))== 7) {
					hm.put("ORG_PAYSLIP_FORMAT", "Format 7");
					hm.put("ORG_PAYSLIP_FORMAT_ID", rs.getString("payslip_format"));
		//===start parvez date: 02-09-2022===			
				} else if(uF.parseToInt(rs.getString("payslip_format"))== 8) {
					hm.put("ORG_PAYSLIP_FORMAT", "Format 8");
					hm.put("ORG_PAYSLIP_FORMAT_ID", rs.getString("payslip_format"));
				} else {
		//===end parvez date: 02-09-2022===			
					hm.put("ORG_PAYSLIP_FORMAT", "Format 1");
					hm.put("ORG_PAYSLIP_FORMAT_ID", "1");
				}
				
					
				String strDuration = "";
				if(rs.getString("duration_paycycle")!=null){
					for(FillPayCycleDuration paycycleDuration : paycycleDurationList){
						if(paycycleDuration.getPaycycleDurationId().equals(rs.getString("duration_paycycle"))){
							strDuration = paycycleDuration.getPaycycleDurationName(); 
						}
					}
				}
				hm.put("ORG_DURATION_PAYCYCLE", strDuration);
				
				String strSalCalBasis = "";
				if(rs.getString("salary_cal_basis")!=null){
					for(FillSalaryCalculationTypes calculationTypes : salaryCalculationList){
						if(calculationTypes.getSalaryCalcId().equals(rs.getString("salary_cal_basis"))){
							strSalCalBasis = calculationTypes.getSalaryCalcName(); 
						}
					}
				}
				hm.put("ORG_SALARY_CAL_BASIS", strSalCalBasis);
				hm.put("ORG_SALARY_FIX_DAYS", rs.getString("salary_fix_days"));
				
				hmOrg.put(rs.getString("org_id"), hm);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmOrg", hmOrg);
		} catch (Exception e) {
			e.printStackTrace();
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

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
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
