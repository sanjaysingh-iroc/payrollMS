package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;


public class SearchEmployeeReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId; 
	CommonFunctions CF = null;
	String strUserType = null;
	String strBaseUserType = null;
	private String strFirstName;
	private String strLastName;
	private String strSearchJob;
	private String fromPage;
	
	private static Logger log = Logger.getLogger(SearchEmployeeReport.class);
	
	public String execute() throws Exception { 

		session = request.getSession();if(session==null)return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, PSearchEmployeeReport);
		request.setAttribute(TITLE, TSearchEmployee);
		UtilityFunctions uF = new UtilityFunctions();
		
		if(strUserType != null && !strUserType.equals(EMPLOYEE)) {
			getSearchAutoCompleteData(uF);
		} else if(getFromPage()!=null && getFromPage().equals("COMMUNICATION")) {
			getSearchAutoCompleteData(uF);
		}
		
		searchEmployee();
	
		return SUCCESS;
}

	private void getSearchAutoCompleteData(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmEmpSupervisorId = CF.getEmpSupervisorIdMap(con);
			SortedSet<String> setEmpList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select epd.*, eod.supervisor_emp_id, eod.wlocation_id from employee_personal_details epd, employee_official_details eod " +
			"where epd.emp_per_id = eod.emp_id  and approved_flag = true and is_alive = true");
			if(strBaseUserType != null && strBaseUserType.equals(HOD) && strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery.append(" and (supervisor_emp_id = "+uF.parseToInt(strSessionEmpId)+" or hod_emp_id = "+uF.parseToInt(strSessionEmpId)+" or eod.emp_id = "+uF.parseToInt(strSessionEmpId)+")");
			} else if(strBaseUserType != null && strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery.append(" and (supervisor_emp_id = "+uF.parseToInt(strSessionEmpId)+" or eod.emp_id = "+uF.parseToInt(strSessionEmpId)+")");
			} else if(strBaseUserType != null && strUserType != null && strUserType.equals(EMPLOYEE) && getFromPage()!=null && getFromPage().equals("COMMUNICATION")) {
				sbQuery.append(" and supervisor_emp_id = "+uF.parseToInt(hmEmpSupervisorId.get(strSessionEmpId)));
			}
			/*if(getStrSearchJob()!= null && !getStrSearchJob().equals("") && !getStrSearchJob().equalsIgnoreCase("null")) {
				sbQuery.append(" and upper(epd.emp_fname)|| ' '||upper(epd.emp_lname) like '%"+getStrSearchJob().trim().toUpperCase()+"%'");
			}*/
			sbQuery.append(" order by emp_fname, emp_lname ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1===> "+ pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				setEmpList.add(rs.getString("emp_fname") + strEmpMName+" " + rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbData = null;
			Iterator<String> it = setEmpList.iterator();
			while (it.hasNext()) {
				String strData = it.next();
				if(sbData == null) {
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null) {
				sbData = new StringBuilder();
			}
			
			request.setAttribute("sbData", sbData.toString());
				
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	public void searchEmployee(){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
//		EncryptionUtility eU = new EncryptionUtility();
		try {
		
			con = db.makeConnection(con);
			
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpDepartment = CF.getDepartmentMap(con,null,null); 
			Map<String, Map<String, String>> hmWLocation = CF.getWorkLocationMap(con);
			Map<String, String> hmEmpType = CF.getEmpTypeMap(); 
			
//			log.debug("strUserType===>"+strUserType);
//			log.debug("strSessionEmpId===>"+strSessionEmpId);
			Map<String, String> hmEmpMap = CF.getEmpNameMap(con, null, null);
			List<String> alWloc = new ArrayList<String>();
			
			String strWLOCATION_ACCESS = (String)session.getAttribute(WLOCATION_ACCESS);
			if(strWLOCATION_ACCESS != null && strWLOCATION_ACCESS.length()>0) {
				alWloc = Arrays.asList(strWLOCATION_ACCESS.split(","));
			} else {
				alWloc.add((String)session.getAttribute(WLOCATIONID));
			}
			
			Map<String, String> hmEmpSupervisorId = CF.getEmpSupervisorIdMap(con);
			List<List<String>> alReport = new ArrayList<List<String>>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod " +
			"where epd.emp_per_id = eod.emp_id  and is_alive = true and approved_flag = true");
			if(strBaseUserType != null && strBaseUserType.equals(HOD) && strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery.append(" and (supervisor_emp_id = "+uF.parseToInt(strSessionEmpId)+" or hod_emp_id = "+uF.parseToInt(strSessionEmpId)+" or eod.emp_id = "+uF.parseToInt(strSessionEmpId)+")");
			} else if(strBaseUserType != null && strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery.append(" and (supervisor_emp_id = "+uF.parseToInt(strSessionEmpId)+" or eod.emp_id = "+uF.parseToInt(strSessionEmpId)+")");
			} else if(strBaseUserType != null && strUserType != null && strUserType.equals(EMPLOYEE) && getFromPage()!=null && getFromPage().equals("COMMUNICATION")) {
				sbQuery.append(" and supervisor_emp_id = "+uF.parseToInt(hmEmpSupervisorId.get(strSessionEmpId)));
			}
			
			if(getStrSearchJob()!= null && !getStrSearchJob().equals("") && !getStrSearchJob().equalsIgnoreCase("null")) {
				sbQuery.append(" and upper(epd.emp_fname)|| ' '||upper(epd.emp_lname) like '%"+getStrSearchJob().trim().toUpperCase()+"%'");
			}
			sbQuery.append(" order by emp_fname, emp_lname ");
		    pst = con.prepareStatement(sbQuery.toString());
		//System.out.println("pst2==>"+pst);
			rs = pst.executeQuery();
			
//			log.debug("pst===>"+pst);
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				
				alInner.add(rs.getString("emp_image"));//0
				alInner.add(rs.getString("emp_fname"));//1
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				alInner.add(strEmpMName);//2
				
				alInner.add(rs.getString("emp_lname"));//2
				alInner.add(hmDesignation.get(rs.getString("emp_per_id")));		//3		
				alInner.add(rs.getString("emp_contactno_mob"));//4
				
				String strWLocationId = rs.getString("wlocation_id");
				Map<String, String> hm = hmWLocation.get(strWLocationId);
				if(hm==null)hm=new HashMap<String, String>();
				alInner.add(hm.get("WL_CITY"));//5
				alInner.add(hm.get("WL_COUNTRY"));//6
				
				if(rs.getString("emp_email_sec") != null && !rs.getString("emp_email_sec").equals("null") && !rs.getString("emp_email_sec").equals("")) {
					alInner.add(rs.getString("emp_email_sec"));//7
				} else {
					alInner.add(rs.getString("emp_email"));//7
				}
				
//				String encodeEmpId = eU.encode(rs.getString("emp_per_id"));
				String encodeEmpId = rs.getString("emp_per_id");
//				EncryptionUtils encryption = new EncryptionUtils();
				if(uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("emp_per_id")) && strUserType != null && strUserType.equalsIgnoreCase(EMPLOYEE) ) {
					alInner.add("<a class=\"factsheet\" href=\"MyProfile.action?empId=" + encodeEmpId + "\" > </a>");//8 encryption.encrypt(encodeEmpId)
				} else if((uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("supervisor_emp_id")) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("emp_per_id"))) && strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strUserType.equalsIgnoreCase(CEO))) {
					alInner.add("<a class=\"factsheet\" href=\"MyProfile.action?empId=" + encodeEmpId + "\" > </a>");//8
				} else if(alWloc.contains(rs.getString("wlocation_id")) && strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
					alInner.add("<a class=\"factsheet\" href=\"MyProfile.action?empId=" + encodeEmpId + "\" > </a>");//8
				} else if(strUserType != null && strUserType.equalsIgnoreCase(ADMIN)) {
					alInner.add("<a class=\"factsheet\" href=\"MyProfile.action?empId=" + encodeEmpId + "\" > </a>");//8
				} else { 
					alInner.add("");//8
				}
				alInner.add(rs.getString("emp_per_id"));//9
				alInner.add(uF.showData(hm.get("WL_NAME"),"-"));//10 location
				alInner.add(uF.showData(rs.getString("emp_status"),"-"));//11 status
				alInner.add(uF.showData(hmEmpDepartment.get(rs.getString("depart_id")),"-"));//12 depart
				alInner.add(uF.showData(hmEmpMap.get(rs.getString("supervisor_emp_id")),"-"));//13 reporting mgr
				alInner.add(uF.showData(uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()),"-"));//14  joining date
				alInner.add(uF.showData(hmEmpType.get(rs.getString("emptype")),"-"));//15 emp type
				alInner.add(uF.showData(rs.getString("empcode"),"-"));//16 emp code
				alReport.add(alInner);
			}
			rs.close();
			pst.close();
				
			request.setAttribute("SEARCH_EMP", alReport);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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

	public String getStrFirstName() {
		return strFirstName;
	}

	public void setStrFirstName(String strFirstName) {
		if(strFirstName!=null){
			this.strFirstName = strFirstName.toUpperCase();
		}
		
	}

	public String getStrLastName() {
		return strLastName;
	}

	public void setStrLastName(String strLastName) {
		if(strLastName!=null){
			this.strLastName = strLastName.toUpperCase();
		}
	}

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
	
}
