package com.konnect.jpms.util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.joda.time.LocalDate;

import com.konnect.jpms.master.OvertimeApproval;
import com.konnect.jpms.performance.GoalScheduler;
import com.konnect.jpms.task.ProjectScheduler;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;


public class SearchData extends ActionSupport implements IStatements,ServletRequestAware {
	private static final long serialVersionUID = 1L;

	public CommonFunctions CF;
	String strSessionEmpId; 
	String strUserType = null;
	String strBaseUserType = null;
	String strProductType;

	private String strSearchJob;
	
	private String strSearchData;

	public HttpServletRequest request; 
	public HttpSession session;
	private Map<String,String>hmLinks;

	public Map<String, String> getHmLinks() {
		return hmLinks;
	}

	public void setHmLinks(Map<String, String> hmLinks) {
		this.hmLinks = hmLinks;
	}

	public String execute() throws Exception {
		//System.out.println("In strSearchJob:");
		//System.out.println("strSearchJob:"+strSearchJob);
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		return getSearchingData();
//		return "load";
	}
	
	public String getSearchingData() {
		//System.out.println("getSearchingData:");
//		List<String> searchingData = new ArrayList<String>();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
//		EncryptionUtils encryption = new EncryptionUtils();// Created By Dattatray Date : 21-July-2021 Note : Encryption
		try {
			con = db.makeConnection(con);
			strSessionEmpId = (String)session.getAttribute(EMPID);
			boolean poFlag = false;
			pst = con.prepareStatement("select project_owner from projectmntnc where project_owner = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
	//		System.out.println("pst =====> "+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				poFlag = true;
			}
			rs.close();
			pst.close();
			
			strProductType = (String)session.getAttribute(PRODUCT_TYPE);
			strUserType  = (String)session.getAttribute(USERTYPE);
			strBaseUserType  = (String)session.getAttribute(BASEUSERTYPE);
			String []arrEnabledModules = (String[])session.getAttribute("arrEnabledModules");
//			CF.setSearchingData("");
//			System.out.println("strProductType ===>> " + strProductType);
//			System.out.println("arrEnabledModules ===>> " + arrEnabledModules!=null ? arrEnabledModules.length : "--");
			Map<String,String> hmLinksList = new HashMap<String,String>();
			if(uF.parseToInt(strProductType)==2) {
				if(strUserType != null && strUserType.equals(EMPLOYEE)) {
					if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, MODULE_LEAVE_MANAGEMENT+"")>=0) {
						hmLinksList.put("Apply Leave", "MyPay.action?callFrom=MyDashLeaveSummary");
					}
					if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, MODULE_EXPENSE_MANAGEMENT+"")>=0) {
						hmLinksList.put("Apply Reimbursement", "MyPay.action?callFrom=MyDashReimbursements");
					}
					if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, MODULE_COMPENSATION_MANAGEMENT+"")>=0) {
						hmLinksList.put("Check Payroll", "MyPay.action");
					}
					hmLinksList.put("My Dashboard", "MyDashboard.action");
					hmLinksList.put("My Profile", "MyProfile.action");
				}
//				hmLinksList.put("My Team", "OrganisationalChart.action");
			} else if(uF.parseToInt(strProductType)==3) {
				if(poFlag || strBaseUserType != null && strBaseUserType.equals(MANAGER)) {
					hmLinksList.put("Go to Projects", "Login.action?role=3&product=3&userscreen=myProjects");
				} else if(strBaseUserType != null && (strBaseUserType.equals(HRMANAGER) || strBaseUserType.equals(ADMIN))) {
					hmLinksList.put("Go to Projects", "Login.action?role=3&product=3&userscreen=allProjects");
				}
	//			hmLinksList.put("My Project Dashboard", "Login.action");
				hmLinksList.put("My Work", "Login.action?role=3&product=3&userscreen=myWorkTasks");
			}
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			if(hmFeatureStatus == null)
				hmFeatureStatus = new HashMap<String,String>();
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
//			System.out.println("hmLinksList ===>> " + hmLinksList);
			
			//Employee List
			Set<String> setEmpList = new HashSet<String>();
			Iterator<String> it = hmLinksList.keySet().iterator();
			while (it.hasNext()) {
//				System.out.println("it.next() ===>> " + it.next());
				setEmpList.add(it.next());
			}
//			System.out.println("setEmpList ===>> " + setEmpList);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select epd.*, eod.supervisor_emp_id, eod.wlocation_id from employee_personal_details epd, employee_official_details eod " +
			"where epd.emp_per_id = eod.emp_id  and approved_flag = true and is_alive = true");
			if(strBaseUserType != null && strBaseUserType.equals(HOD) && strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery.append(" and (supervisor_emp_id = "+uF.parseToInt(strSessionEmpId)+" or hod_emp_id = "+uF.parseToInt(strSessionEmpId)+")");
			} else if(strBaseUserType != null && strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery.append(" and supervisor_emp_id = "+uF.parseToInt(strSessionEmpId));
			} else if(strBaseUserType != null && strUserType != null && strUserType.equals(EMPLOYEE)) {
				sbQuery.append(" and emp_per_id = "+uF.parseToInt(strSessionEmpId));
			} 
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(getStrSearchData()!= null && !getStrSearchData().equals("") && !getStrSearchData().equalsIgnoreCase("null")) {
				sbQuery.append(" and upper(epd.emp_fname)|| ' '||upper(epd.emp_lname) like '%"+getStrSearchData().trim().toUpperCase()+"%'");
			}
			sbQuery.append(" order by emp_fname, emp_lname ");
			pst = con.prepareStatement(sbQuery.toString());
	//		System.out.println("pst1===> "+ pst);
			rs = pst.executeQuery();
			while (rs.next()) {
			
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				setEmpList.add(rs.getString("emp_fname") + strEmpMName+" " + rs.getString("emp_lname"));
				if(uF.parseToInt(strProductType)==2) { //encryption.encrypt(rs.getString("emp_per_id"))
					hmLinksList.put(rs.getString("emp_fname") + strEmpMName+" " + rs.getString("emp_lname"), "MyProfile.action?fromPage=P&empId="+rs.getString("emp_per_id"));
				} else if(uF.parseToInt(strProductType)==3) {
					hmLinksList.put(rs.getString("emp_fname") + strEmpMName+" " + rs.getString("emp_lname"), "PeopleProfile.action?empId="+rs.getString("emp_per_id"));
				}
			}
			rs.close();
			pst.close();
//			System.out.println("setEmpList:"+setEmpList);
			
//			CF.setListLinks(hmLinksList);
		
//			searchingData.addAll(hmLinksList.keySet());
//			searchingData.addAll(setEmpList);
			StringBuilder sbSearchData = null;
			Iterator<String> it1 = setEmpList.iterator();
			while (it1.hasNext()) {
			String strData = it1.next();
			if(sbSearchData == null) {
				sbSearchData = new StringBuilder();
				sbSearchData.append("\""+strData+"\"");
				} else {
					sbSearchData.append(",\""+strData+"\"");
				}
			}
		
			if(sbSearchData == null) {
				sbSearchData = new StringBuilder();
			}
//			System.out.println("sbSearchData ===>> " + sbSearchData);
			session.setAttribute("sbSearchData", sbSearchData.toString());
//			CF.setSearchingData(sbSearchData.toString());
			
			if(strSearchJob!=null) {
				//System.out.println("strserachJOB is not null");
				String action = null;
				if(hmLinksList.containsKey(strSearchJob)) {
				 	action = hmLinksList.get(strSearchJob);
				 	// System.out.println("action:"+action);
//					request.setAttribute("action", action);
//					request.setAttribute("Searchaction", hmLinksList);
					request.setAttribute("STATUS_MSG", action);
//					System.out.println("action ===>> " + action);
					return "ajax";
				} else {
					return null;
				}
			} else {
				return null;
			}
			
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}



	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	public String getStrSearchData() {
		return strSearchData;
	}

	public void setStrSearchData(String strSearchData) {
		this.strSearchData = strSearchData;
	}

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}
	

}