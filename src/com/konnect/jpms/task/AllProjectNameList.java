package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author workrig
 *
 */
public class AllProjectNameList extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpServletRequest request;
	String strSessionEmpId;
	String strSessionOrgId;

	String approve;
	String blocked;
	String working;
	String operation;
	int ID; 
	int singleProid;
	int emp_id;
	String[] pro_id;
	HttpSession session;
	int task_id;
	boolean isSingle = false;
	private String cb;
	private String approvePr;
	String taskId; 
	
	CommonFunctions CF;
	
	List<Integer> projectidlist = new ArrayList<Integer>();
	List<String> projectlist = new ArrayList<String>();
	List<String> alInner = new ArrayList<String>();
	Map<Integer, List<String>> activity_al = new HashMap<Integer, List<String>>();

	// Comes from ViewAllProject.action to view all project
	List<Integer> index = new ArrayList<Integer>();
	List<String> time1 = new ArrayList<String>();
	List<String> taskStatus1 = new ArrayList<String>();
	List<String> totalhrz1 = new ArrayList<String>();
	Map<Integer,String> EmpNameMap=new HashMap<Integer,String>();
	Map<Integer, List<String>> al = new HashMap<Integer, List<String>>();
//	Map<String,String> 	hmServiceDesc=new HashMap<String, String>();
	
	private String strSBU;
	private String strService;
	private String strProjectId;
	private String strManagerId;
	private String strClient;
	
	String strUserType;
	String[] managerId;
	String[] client;
	String projectID;
	String projectName;
	String[] f_service;
	String[] f_sbu;
	String service_id;

	String proType;
	String pageType;
	String proPage;
	String minLimit;
	
	String strDivCount;
	
	String proId;
	
	String proStatus;
	String assignedBy;
	String recurrOrMiles;
	String sortBy;
	
	String strSearchJob;
	
	String submitType;
	
	public String execute() {
	
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		
//		System.out.println("getPageType() ===>> " + getPageType());
		if(getPageType() == null || getPageType().equals("") || getPageType().equals("null")) {
			setPageType(null);
		}
		
		if(getProPage() == null || getProPage().equals("") || getProPage().equals("null")) {
			setProPage("1");
		}
		
		if(getStrSBU() != null && !getStrSBU().equals("")) {
			setF_sbu(getStrSBU().split(","));
		} else {
			setF_sbu(null);
		}
//		System.out.println("getStrSBU() ===>> " + getStrSBU());
		if(getStrService() != null && !getStrService().equals("")) {
			setF_service(getStrService().split(","));
		} else {
			setF_service(null);
		}
//		System.out.println("getStrService() ===>> " + getStrService());
		if(getStrProjectId() != null && !getStrProjectId().equals("")) {
			setPro_id(getStrProjectId().split(","));
		} else {
			setPro_id(null);
		}
		if(getStrManagerId() != null && !getStrManagerId().equals("")) {
			setManagerId(getStrManagerId().split(","));
		} else {
			setManagerId(null);
		}
		if(getStrClient() != null && !getStrClient().equals("")) {
			setClient(getStrClient().split(","));
		} else {
			setClient(null);
		}
		
//		System.out.println("getProType() 1 ===>> " + getProType());
		
		if(getProType() == null || getProType().equals("") || getProType().equals("null") || getProType().equals("L")) {
			
			request.setAttribute(TITLE, "Working Projects");
		} else if(getProType() != null && getProType().equals("C")) {
			
//				getCompletedProjectDetails();
			request.setAttribute(TITLE, "Completed Projects");
		} else if(getProType() != null && getProType().equals("B")) {
			
//				getBlockedProjectDetails();
			request.setAttribute(TITLE, "Blocked Projects");
		}
		
		if(getSortBy() == null || getSortBy().equals("")) {
			setSortBy("1");
		}
		
		if(getOperation() !=null && getOperation().equals("D")) {
			deleteProject();
		}
		
//		System.out.println("approve ===>> " +approve);
//		System.out.println("getBlocked ===>> " +getBlocked());
		if (getApprove() != null && !getApprove().equals("")) {
			approveProject();
		}
		
		if(getBlocked() != null && !getBlocked().equals("")) {
			blockProject();
		}
		
		if(getWorking() != null && !getWorking().equals("")) {
			workingProject();
		}

		getSearchAutoCompleteData(uF);
		getProjectDetails();
//			getActivity(uF);
		return LOAD;
	}
	
	private void workingProject() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		UtilityFunctions uF = new UtilityFunctions();
		if (approvePr != null && !approvePr.equals("")) {
			try {
				
				Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
				
				String strDomain = request.getServerName().split("\\.")[0];
				
				pst = con.prepareStatement("UPDATE projectmntnc SET approve_status='blocked', approve_date=? WHERE pro_id in ("+approvePr+")");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setInt(2, uF.parseToInt((approvePr[i])));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("UPDATE activity_info SET approve_status='blocked', end_date=? WHERE pro_id in ("+approvePr+")");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.executeUpdate();
//				System.out.println("pst ===>> " + pst);
				pst.close();
				
				List<String> alProList = Arrays.asList(approvePr.split(","));
				for(int i = 0; alProList!=null && i < alProList.size(); i++) {
					String strProId = alProList.get(i);
					Map<String, String> hmProDetails = CF.getProjectDetailsByProId(con, strProId.trim());
					
					Set<String> setEmp = new HashSet<String>(); 
					if(uF.parseToInt(hmProDetails.get("PRO_OWNER_ID")) > 0){
						setEmp.add(hmProDetails.get("PRO_OWNER_ID"));
					}
					
					Map<String, String> hmTaskProData = CF.getTaskProInfo(con, null, strProId);
					
					Notifications nF = new Notifications(N_PROJECT_BLOCKED, CF); 
					nF.setDomain(strDomain);
					
					nF.request = request;
					nF.setStrOrgId(strSessionOrgId);
					nF.setEmailTemplate(true);
					
					nF.setStrEmpId(hmProDetails.get("PRO_OWNER_ID"));
					nF.setStrProjectName(hmTaskProData.get("PRO_NAME"));
					nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.sendNotifications();
					
					if(uF.parseToInt(hmProDetails.get("PRO_CUST_SPOC_ID")) > 0) {
						
						nF = new Notifications(N_PROJECT_BLOCKED, CF); 
						nF.setDomain(strDomain);
						nF.request = request;
						nF.setStrOrgId(strSessionOrgId);
						nF.setEmailTemplate(true);
						
						pst = con.prepareStatement("select * from client_poc where poc_id = ?");
						pst.setInt(1, uF.parseToInt(hmProDetails.get("PRO_CUST_SPOC_ID")));
						rs = pst.executeQuery();
						boolean flg=false;
						while(rs.next()) {
							nF.setStrEmpFname(rs.getString("contact_fname"));
							nF.setStrEmpLname(rs.getString("contact_lname"));
							nF.setStrEmpMobileNo(rs.getString("contact_number"));
							if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
								nF.setStrEmpEmail(rs.getString("contact_email"));
								nF.setStrEmailTo(rs.getString("contact_email"));
							}
							flg = true;
						}
						rs.close();
						pst.close();
						
						if(flg) {
							nF.setStrHostAddress(CF.getStrEmailLocalHost());
							nF.setStrHostPort(CF.getStrHostPort());
							nF.setStrContextPath(request.getContextPath());
							nF.setStrProjectName(hmTaskProData.get("PRO_NAME"));
							nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
							nF.sendNotifications();
						}
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.closeStatements(pst);
				db.closeConnection(con);
			}
		}
	}

	private void getSearchAutoCompleteData(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmProjectClientMap = CF.getProjectClientMap(con, uF);
			if(hmProjectClientMap == null) hmProjectClientMap = new HashMap<String, String>();

			SortedSet<String> setSearchList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from projectmntnc where pro_id > 0 ");
			if(getProType() == null || getProType().equals("") || getProType().equals("null") || getProType().equals("L")) {
				sbQuery.append(" and approve_status='n' ");
			} else if(getProType() != null && getProType().equals("C")) {
				sbQuery.append(" and approve_status='approved' ");
			} else if(getProType() != null && getProType().equals("B")) {
				sbQuery.append(" and approve_status='blocked' ");
			} else if(getProType() != null && getProType().equals("P")) {
				sbQuery.append(" and approve_status='pipelined' ");
			}
			
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			
//			if(getLocation()!=null && getLocation().length>0) {
//	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getLocation(), ",")+")");
//	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
//				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//			}
			
			if(getF_sbu()!=null && getF_sbu().length > 0 && !getF_sbu()[0].trim().equals("")) {
				sbQuery.append(" and sbu_id in ("+StringUtils.join(getF_sbu(), ",")+") ");
			}
			
			if(getF_service()!=null && getF_service().length > 0) {
				String service = getConcateData(getF_service());
				sbQuery.append(" and service in ("+service+") ");
			}
			
			if(getClient()!=null && getClient().length > 0 && !getClient()[0].trim().equals("")) {
				sbQuery.append(" and client_id in ("+StringUtils.join(getClient(), ",")+") ");
			}
			
			if(getPro_id()!=null && getPro_id().length > 0 && !getPro_id()[0].trim().equals("")) {
				sbQuery.append(" and pro_id in ("+StringUtils.join(getPro_id(), ",")+") ");
			}
			
	//===start parvez date: 13-10-2022===		
			if(getManagerId()!=null && getManagerId().length > 0 && !getManagerId()[0].trim().equals("")) {
//				sbQuery.append(" and project_owner in ("+StringUtils.join(getManagerId(), ",")+") ");
				sbQuery.append(" and (");
				for(int ii=0;ii<getManagerId().length; ii++){
					if(ii == 0){
						sbQuery.append("project_owners like '%,"+getManagerId()[ii]+",%' ");
					}else{
						sbQuery.append(" or project_owners like '%,"+getManagerId()[ii]+",%' ");
					}
				}
				sbQuery.append(" )");
			}
	//===end parvez date: 13-10-2022===		
			
			/*if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && (getPageType() == null || !getPageType().equals("MP"))) {
				sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
			}*/
			
//			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && getPageType() != null && getPageType().equals("MP")) {
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
		//===start parvez date: 13-10-2022===		
				sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
//					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
					+" or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%' ) ");
		//===end parvez date: 13-10-2022===		
			} else if(getPageType() != null && getPageType().equals("MP")) {
		//===start parvez date: 13-10-2022===	
				sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//					+uF.parseToInt((String)session.getAttribute(EMPID))+") or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
						+uF.parseToInt((String)session.getAttribute(EMPID))+") or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%') ");
		//===end parvez date: 13-10-2022===	
			} else if((getPageType() != null && getPageType().equals("MYPRO")) || ((getPageType() == null || getPageType().equals("")) && strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE))) {
				sbQuery.append(" and pro_id in (select pro_id from project_emp_details where emp_id = " + uF.parseToInt((String)session.getAttribute(EMPID)) + ") ");
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(CUSTOMER)) {
				sbQuery.append(" and poc="+uF.parseToInt((String)session.getAttribute(EMPID))+" ");
			}
			
			if(getProStatus() != null && uF.parseToInt(getProStatus()) == 1) {
				sbQuery.append(" and completed>0 and deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			} else if(getProStatus() != null && uF.parseToInt(getProStatus()) == 2) {
				sbQuery.append(" and (completed = 0 or completed is null) and deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			} else if(getProStatus() != null && uF.parseToInt(getProStatus()) == 3) {
				sbQuery.append(" and deadline < '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			}
			
			if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 1) {
				sbQuery.append(" and billing_type != 'F' ");
			} else if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 2) {
				sbQuery.append(" and billing_type = 'F' ");
			}
			
			if(getAssignedBy() != null && uF.parseToInt(getAssignedBy()) >0) {
				sbQuery.append(" and added_by = "+uF.parseToInt(getAssignedBy())+" ");
			}
			
			if(getSortBy() != null && uF.parseToInt(getSortBy()) == 1) {
				sbQuery.append(" order by start_date desc ");
			} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 2) {
				sbQuery.append(" order by start_date ");
			} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 3) {
				sbQuery.append(" order by pro_name ");
			} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 4) {
				sbQuery.append(" order by pro_name desc ");
			} else {
				sbQuery.append(" order by start_date desc ");
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst===> "+ pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				setSearchList.add(rs.getString("pro_name"));
				String strClientName = hmProjectClientMap.get(rs.getString("client_id"));
				if(uF.parseToInt(rs.getString("client_id")) > 0 && strClientName!=null && !strClientName.trim().equals("")){
					setSearchList.add(strClientName.trim());
				}
			}
			rs.close();
			pst.close();
			
			StringBuilder sbData = null;
			Iterator<String> it = setSearchList.iterator();
			while (it.hasNext()){
				String strData = it.next();
				if(sbData == null){
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null){
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
	
	
	public void deleteProject() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("DELETE FROM work_flow_details where effective_id in (select task_id FROM task_activity WHERE activity_id in (select task_id from activity_info WHERE pro_id=?))");
			pst.setInt(1, ID);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM task_activity WHERE activity_id in (select task_id from activity_info WHERE pro_id=?)");
			pst.setInt(1, ID);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM projectmntnc WHERE pro_id=?");
			pst.setInt(1, ID);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM activity_info WHERE pro_id=?");
			pst.setInt(1, ID);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM project_skill_details WHERE pro_id=?");
			pst.setInt(1, ID);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM project_emp_details WHERE pro_id=?");
			pst.setInt(1, ID);
			pst.executeUpdate(); 
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM project_document_details WHERE pro_id=?");
			pst.setInt(1, ID);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM project_milestone_details WHERE pro_id=?");
			pst.setInt(1, ID);
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void approveProject() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			int x = 0;
			String strDomain = request.getServerName().split("\\.")[0];
			if (cb != null && !cb.equals("")) {
				pst = con.prepareStatement("UPDATE activity_info SET approve_status='approved', end_date=?, completed=100 WHERE task_id in ("+cb+")");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				x = pst.executeUpdate();
				pst.close();
			}
			
			if (approvePr != null && !approvePr.equals("")) {
				pst = con.prepareStatement("UPDATE projectmntnc SET approve_status='approved', approve_date=?, completed=100 WHERE pro_id in ("+approvePr+")");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setInt(2, uF.parseToInt((approvePr[i])));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("UPDATE activity_info SET approve_status='approved', end_date=?, completed=100 WHERE pro_id in ("+approvePr+")");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.executeUpdate();
//					System.out.println("pst ===>> " + pst);
				pst.close();
				
				List<String> alProList = Arrays.asList(approvePr.split(","));
				for(int i = 0; alProList!=null && i < alProList.size(); i++) {
					String strProId = alProList.get(i);
					Map<String, String> hmProDetails = CF.getProjectDetailsByProId(con, strProId.trim());
					
					Set<String> setEmp = new HashSet<String>(); 
					if(uF.parseToInt(hmProDetails.get("PRO_OWNER_ID")) > 0){
						setEmp.add(hmProDetails.get("PRO_OWNER_ID"));
					}
					
					Map<String, String> hmTaskProData = CF.getTaskProInfo(con, null, strProId);
					
					Notifications nF = new Notifications(N_PROJECT_COMPLETED, CF); 
					nF.setDomain(strDomain);
					
					nF.request = request;
					nF.setStrOrgId(strSessionOrgId);
					nF.setEmailTemplate(true);
					
					nF.setStrEmpId(hmProDetails.get("PRO_OWNER_ID"));
					nF.setStrProjectName(hmTaskProData.get("PRO_NAME"));
					nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.sendNotifications();
					
					pst = con.prepareStatement("select emp_id from project_emp_details where pro_id=? and _isteamlead = true");
					pst.setInt(1, uF.parseToInt(strProId.trim()));
					rs = pst.executeQuery();
					while(rs.next()){
						if(uF.parseToInt(rs.getString("emp_id")) > 0){
							setEmp.add(rs.getString("emp_id"));
						}
					}
					rs.close();
					pst.close();
					
					String alertData = "<div style=\"float: left;\"><b>"+hmTaskProData.get("PRO_NAME")+"</b> project has been completed by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
					String alertAction = "ViewAllProjects.action";
					for(String strEmp : setEmp){
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strEmp);
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
//						userAlerts.set_type(PRO_COMPLETED_ALERT);
						userAlerts.setStatus(INSERT_TR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
					}
					
					if(uF.parseToInt(hmProDetails.get("PRO_CUST_SPOC_ID")) > 0) {
						
						nF = new Notifications(N_PROJECT_COMPLETED, CF); 
						nF.setDomain(strDomain);
						
						nF.request = request;
						nF.setStrOrgId(strSessionOrgId);
						nF.setEmailTemplate(true);
						
						pst = con.prepareStatement("select * from client_poc where poc_id = ?");
						pst.setInt(1, uF.parseToInt(hmProDetails.get("PRO_CUST_SPOC_ID")));
						rs = pst.executeQuery();
						boolean flg=false;
						while(rs.next()) {
							nF.setStrEmpFname(rs.getString("contact_fname"));
							nF.setStrEmpLname(rs.getString("contact_lname"));
							nF.setStrEmpMobileNo(rs.getString("contact_number"));
							if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
								nF.setStrEmpEmail(rs.getString("contact_email"));
								nF.setStrEmailTo(rs.getString("contact_email"));
							}
							flg = true;
						}
						rs.close();
						pst.close();
						
						if(flg) {
							nF.setStrHostAddress(CF.getStrEmailLocalHost());
							nF.setStrHostPort(CF.getStrHostPort());
							nF.setStrContextPath(request.getContextPath());
							nF.setStrProjectName(hmTaskProData.get("PRO_NAME"));
							nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
							nF.sendNotifications();
						}
						
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(hmProDetails.get("PRO_CUST_SPOC_ID"));
						userAlerts.setStrOther("other");
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
//						userAlerts.set_type(PRO_COMPLETED_ALERT);
						userAlerts.setStatus(INSERT_TR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
					}
				}
			}
			
			if(x >0){
				List<String> alTaskList = Arrays.asList(cb.split(","));
				for(int i = 0; alTaskList!=null && i < alTaskList.size(); i++) {
					String strTaskId = alTaskList.get(i);
					int nProId = 0;
					String taskName = null;
					pst = con.prepareStatement("select pro_id,resource_ids,activity_name from activity_info where task_id=?");
					pst.setInt(1, uF.parseToInt(strTaskId));
					rs = pst.executeQuery();
					while(rs.next()){
						nProId = rs.getInt("pro_id");
						taskName = rs.getString("activity_name");
					}
					rs.close();
					pst.close();
					
					Map<String, String> hmProDetails = CF.getProjectDetailsByProId(con, ""+nProId);
					
					Set<String> setEmp = new HashSet<String>(); 
					if(uF.parseToInt(hmProDetails.get("PRO_OWNER_ID")) > 0){
						setEmp.add(hmProDetails.get("PRO_OWNER_ID"));
					}
					
					pst = con.prepareStatement("select emp_id from project_emp_details where pro_id=? and _isteamlead = true");
					pst.setInt(1, nProId);
					rs = pst.executeQuery();
					while(rs.next()){
						if(uF.parseToInt(rs.getString("emp_id")) > 0){
							setEmp.add(rs.getString("emp_id"));
						}
					}
					rs.close();
					pst.close();
					
					String alertData = "<div style=\"float: left;\"> <b>"+taskName+"</b> in <b>"+hmProDetails.get("PRO_NAME")+"</b> project has been completed by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
					String alertAction = "ViewAllProjects.action";
					for(String strEmp : setEmp) {
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strEmp);
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
//						userAlerts.set_type(TASK_COMPLETED_ALERT);
						userAlerts.setStatus(INSERT_TR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
					}
					
					if(uF.parseToInt(hmProDetails.get("PRO_CUST_SPOC_ID")) > 0) {
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(hmProDetails.get("PRO_CUST_SPOC_ID"));
						userAlerts.setStrOther("other");
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
//						userAlerts.set_type(TASK_COMPLETED_ALERT);
						userAlerts.setStatus(INSERT_TR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
					}
					
//					pst = con.prepareStatement("select * from project_milestone_details where pro_task_id=? and pro_id=? and (send_milestone_mail is null or send_milestone_mail=0)");
//					pst.setInt(1, uF.parseToInt(strTaskId));
//					pst.setInt(2, nProId);
//					rs = pst.executeQuery();
//					StringBuilder sbMilestoneId = null;
//					while(rs.next()){
//						if(sbMilestoneId == null){
//							sbMilestoneId = new StringBuilder();
//							sbMilestoneId.append(rs.getString("project_milestone_id"));
//						} else {
//							sbMilestoneId.append(","+rs.getString("project_milestone_id"));
//						}
//					}
//					rs.close();
//					pst.close();
					
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void blockProject() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		UtilityFunctions uF = new UtilityFunctions();
		if (approvePr != null && !approvePr.equals("")) {
			try {
				
				Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
				
				String strDomain = request.getServerName().split("\\.")[0];
				
				pst = con.prepareStatement("UPDATE projectmntnc SET approve_status='n', approve_date=? WHERE pro_id in ("+approvePr+")");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setInt(2, uF.parseToInt((approvePr[i])));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("UPDATE activity_info SET approve_status='n' WHERE pro_id in ("+approvePr+")");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.executeUpdate();
//				System.out.println("pst ===>> " + pst);
				pst.close();
				
				List<String> alProList = Arrays.asList(approvePr.split(","));
				for(int i = 0; alProList!=null && i < alProList.size(); i++) {
					String strProId = alProList.get(i);
					Map<String, String> hmProDetails = CF.getProjectDetailsByProId(con, strProId.trim());
					
					Set<String> setEmp = new HashSet<String>(); 
					if(uF.parseToInt(hmProDetails.get("PRO_OWNER_ID")) > 0){
						setEmp.add(hmProDetails.get("PRO_OWNER_ID"));
					}
					
					Map<String, String> hmTaskProData = CF.getTaskProInfo(con, null, strProId);
					
					Notifications nF = new Notifications(N_PROJECT_RE_OPENED, CF); 
					nF.setDomain(strDomain);
					
					nF.request = request;
					nF.setStrOrgId(strSessionOrgId);
					nF.setEmailTemplate(true);
					
					nF.setStrEmpId(hmProDetails.get("PRO_OWNER_ID"));
					nF.setStrProjectName(hmTaskProData.get("PRO_NAME"));
					nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.sendNotifications();
					
					if(uF.parseToInt(hmProDetails.get("PRO_CUST_SPOC_ID")) > 0) {
						
						nF = new Notifications(N_PROJECT_RE_OPENED, CF); 
						nF.setDomain(strDomain);
						nF.request = request;
						nF.setStrOrgId(strSessionOrgId);
						nF.setEmailTemplate(true);
						
						pst = con.prepareStatement("select * from client_poc where poc_id = ?");
						pst.setInt(1, uF.parseToInt(hmProDetails.get("PRO_CUST_SPOC_ID")));
						rs = pst.executeQuery();
						boolean flg=false;
						while(rs.next()) {
							nF.setStrEmpFname(rs.getString("contact_fname"));
							nF.setStrEmpLname(rs.getString("contact_lname"));
							nF.setStrEmpMobileNo(rs.getString("contact_number"));
							if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
								nF.setStrEmpEmail(rs.getString("contact_email"));
								nF.setStrEmailTo(rs.getString("contact_email"));
							}
							flg = true;
						}
						rs.close();
						pst.close();
						
						if(flg) {
							nF.setStrHostAddress(CF.getStrEmailLocalHost());
							nF.setStrHostPort(CF.getStrHostPort());
							nF.setStrContextPath(request.getContextPath());
							nF.setStrProjectName(hmTaskProData.get("PRO_NAME"));
							nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
							nF.sendNotifications();
						}
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.closeStatements(pst);
				db.closeConnection(con);
			}
		}
	}
	
	
	public void getProjectDetails() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
//			System.out.println("getProType ===>> " + getProType());
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			StringBuilder sbQuery11 = new StringBuilder();
			sbQuery11.append("select count(*) as proCount, approve_status from projectmntnc where pro_id > 0 ");
			
			if(uF.parseToInt(getProId()) > 0) {
				sbQuery11.append(" and pro_id = "+uF.parseToInt(getProId())+" ");
			}
			
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery11.append(" and (org_id in ("+(String)session.getAttribute(ORG_ACCESS)+") ");
				if(getPageType() != null && getPageType().equals("MP")) {
			//===start parvez date: 13-10-2022===		
					sbQuery11.append(" or ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = " 
//						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
							+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or project_owners  like '%,"+(String)session.getAttribute(EMPID)+",%') ");
			//===end parvez date: 13-10-2022===		
				}
				sbQuery11.append(") ");
			}
			
//			if(getLocation()!=null && getLocation().length>0) {
//	            sbQuery11.append(" and wlocation_id in ("+StringUtils.join(getLocation(), ",")+") ");
//	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
//				sbQuery11.append(" and (wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") ");
//				if(getPageType() != null && getPageType().equals("MP")) {
//					sbQuery11.append(" or ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
//				}
//				sbQuery11.append(") ");
//			}
			
			if(getF_sbu()!=null && getF_sbu().length > 0 && !getF_sbu()[0].trim().equals("")) {
				sbQuery11.append(" and sbu_id in ("+StringUtils.join(getF_sbu(), ",")+") ");
			}
			
			if(getF_service()!=null && getF_service().length > 0) {
				String service = getConcateData(getF_service());
				sbQuery11.append(" and service in ("+service+") ");
			}
			
			if(getClient()!=null && getClient().length > 0 && !getClient()[0].trim().equals("")) {
				sbQuery11.append(" and client_id in ("+StringUtils.join(getClient(), ",")+") ");
			}
			
			if(getPro_id()!=null && getPro_id().length > 0 && !getPro_id()[0].trim().equals("")) {
				sbQuery11.append(" and pro_id in ("+StringUtils.join(getPro_id(), ",")+") ");
			}
			
			if(getManagerId()!=null && getManagerId().length > 0 && !getManagerId()[0].trim().equals("")) {
		//===start parvez date: 13-10-2022===		
//				sbQuery11.append(" and project_owner in ("+StringUtils.join(getManagerId(), ",")+") ");
				sbQuery11.append(" and (");
				for(int ii=0;ii<getManagerId().length; ii++){
					if(ii == 0){
						sbQuery11.append("project_owners like '%,"+getManagerId()[ii]+",%' ");
					}else{
						sbQuery11.append(" or project_owners like '%,"+getManagerId()[ii]+",%' ");
					}
				}
				sbQuery11.append(" )");
		//===end parvez date: 13-10-2022===		
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && (getPageType() == null || !getPageType().equals("MP"))) {
		//===start parvez date: 13-10-2022===		
				sbQuery11.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
//					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
					+" or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%' ) ");
		//===end parvez date: 13-10-2022===		
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && getPageType() != null && getPageType().equals("MP")) {
		//===start parvez date: 13-10-2022===		
				sbQuery11.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
//					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
					+" or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%' ) ");
		//===end parvez date: 13-10-2022===		
				
			} else if(getPageType() != null && getPageType().equals("MP")) {
		//===start parvez date: 13-10-2022===		
				sbQuery11.append(" and (pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//					+uF.parseToInt((String)session.getAttribute(EMPID))+") or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
					+uF.parseToInt((String)session.getAttribute(EMPID))+") or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%') ");
		//===end parvez date: 13-10-2022===		
			} else if(getPageType() != null && getPageType().equals("MYPRO")) {
				sbQuery11.append(" and pro_id in (select pro_id from project_emp_details where emp_id = " + uF.parseToInt((String)session.getAttribute(EMPID)) + ") ");
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(CUSTOMER)) {
				sbQuery11.append(" and poc="+uF.parseToInt((String)session.getAttribute(EMPID))+" ");
			}
			
			if(getProStatus() != null && uF.parseToInt(getProStatus()) == 1) {
				sbQuery11.append(" and completed>0 and deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			} else if(getProStatus() != null && uF.parseToInt(getProStatus()) == 2) {
				sbQuery11.append(" and (completed = 0 or completed is null) and deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			} else if(getProStatus() != null && uF.parseToInt(getProStatus()) == 3) {
				sbQuery11.append(" and deadline < '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			}
			
			if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 1) {
				sbQuery11.append(" and billing_type != 'F' ");
			} else if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 2) {
				sbQuery11.append(" and billing_type = 'F' ");
			}
			
			if(getAssignedBy() != null && uF.parseToInt(getAssignedBy()) >0) {
				sbQuery11.append(" and added_by = "+uF.parseToInt(getAssignedBy())+" ");
			}
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
				sbQuery11.append(" and (upper(pro_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or client_id in (select client_id from client_details where upper(client_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'))");
			}
			sbQuery11.append(" group by approve_status");
			pst = con.prepareStatement(sbQuery11.toString());
//			System.out.println("pst ===>> " + pst);
			int wProCnt = 0;
			int cProCnt = 0;
			int bProCnt = 0;
			int pProCnt = 0;
			rs = pst.executeQuery();
			while(rs.next()) {
				if(rs.getString("approve_status") !=null && rs.getString("approve_status").equals("n")) {
					wProCnt = rs.getInt("proCount");
				} else if(rs.getString("approve_status") !=null && rs.getString("approve_status").equals("approved")) {
					cProCnt = rs.getInt("proCount");
				} else if(rs.getString("approve_status") !=null && rs.getString("approve_status").equals("blocked")) {
					bProCnt = rs.getInt("proCount");
				} else if(rs.getString("approve_status") !=null && rs.getString("approve_status").equals("pipelined")) {
					pProCnt = rs.getInt("proCount");
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("wProCnt", wProCnt+"");
			request.setAttribute("cProCnt", cProCnt+"");
			request.setAttribute("bProCnt", bProCnt+"");
			request.setAttribute("pProCnt", pProCnt+"");
			
			int proCount = 0;
			int proCnt = 0;
			if(getProType() == null || getProType().equals("") || getProType().equals("null") || getProType().equals("L")) {
				proCnt = wProCnt;
				proCount = wProCnt/10;
				if(wProCnt%10 != 0) {
					proCount++;
				}
			} else if(getProType() != null && getProType().equals("C")) {
				proCnt = cProCnt;
				proCount = cProCnt/10;
				if(cProCnt%10 != 0) {
					proCount++;
				}
			} else if(getProType() != null && getProType().equals("B")) {
				proCnt = bProCnt;
				proCount = bProCnt/10;
				if(bProCnt%10 != 0) {
					proCount++;
				}
			}
			request.setAttribute("proCount", proCount+"");
			request.setAttribute("proCnt", proCnt+"");
			
			
			/*StringBuilder sbQuery1 = new StringBuilder();
			sbQuery1.append("select count(*) as proCount from projectmntnc where pro_id > 0 ");
			if(getProType() == null || getProType().equals("") || getProType().equals("null") || getProType().equals("L")) {
				sbQuery1.append(" and approve_status='n' ");
			} else if(getProType() != null && getProType().equals("C")) {
				sbQuery1.append(" and approve_status='approved' ");
			} else if(getProType() != null && getProType().equals("B")) {
				sbQuery1.append(" and approve_status='blocked' ");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery1.append(" and (org_id in ("+(String)session.getAttribute(ORG_ACCESS)+") ");
				if(getPageType() != null && getPageType().equals("MP")) {
					sbQuery1.append(" or ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
				}
				sbQuery1.append(") ");
			}
			if(getF_sbu()!=null && getF_sbu().length > 0 && !getF_sbu()[0].trim().equals("")) {
				sbQuery1.append(" and sbu_id in ("+StringUtils.join(getF_sbu(), ",")+") ");
			}
			
			if(getF_service()!=null && getF_service().length > 0) {
				String service = getConcateData(getF_service());
				sbQuery1.append(" and service in ("+service+") ");
			}
			
			if(getClient()!=null && getClient().length > 0 && !getClient()[0].trim().equals("")) {
				sbQuery1.append(" and client_id in ("+StringUtils.join(getClient(), ",")+") ");
			}
			
			if(getPro_id()!=null && getPro_id().length > 0 && !getPro_id()[0].trim().equals("")) {
				sbQuery1.append(" and pro_id in ("+StringUtils.join(getPro_id(), ",")+") ");
			}
			
			if(getManagerId()!=null && getManagerId().length > 0 && !getManagerId()[0].trim().equals("")) {
				sbQuery1.append(" and project_owner in ("+StringUtils.join(getManagerId(), ",")+") ");
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && (getPageType() == null || !getPageType().equals("MP"))) {
				sbQuery1.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && getPageType() != null && getPageType().equals("MP")) {
				sbQuery1.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
				
			} else if(getPageType() != null && getPageType().equals("MP")) {
				sbQuery1.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt((String)session.getAttribute(EMPID))+") or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
			} else if(getPageType() != null && getPageType().equals("MYPRO")) {
				sbQuery1.append(" and pro_id in (select pro_id from project_emp_details where emp_id = " + uF.parseToInt((String)session.getAttribute(EMPID)) + ") ");
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(CUSTOMER)) {
				sbQuery1.append(" and poc="+uF.parseToInt((String)session.getAttribute(EMPID))+" ");
			}
			
			if(getProStatus() != null && uF.parseToInt(getProStatus()) == 1) {
				sbQuery1.append(" and completed>0 and deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			} else if(getProStatus() != null && uF.parseToInt(getProStatus()) == 2) {
				sbQuery1.append(" and (completed = 0 or completed is null) and deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			} else if(getProStatus() != null && uF.parseToInt(getProStatus()) == 3) {
				sbQuery1.append(" and deadline < '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			}
			
			if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 1) {
				sbQuery1.append(" and billing_type != 'F' ");
			} else if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 2) {
				sbQuery1.append(" and billing_type = 'F' ");
			}
			
			if(getAssignedBy() != null && uF.parseToInt(getAssignedBy()) >0) {
				sbQuery1.append(" and added_by = "+uF.parseToInt(getAssignedBy())+" ");
			}
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
				sbQuery1.append(" and (upper(pro_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or client_id in (select client_id from client_details where upper(client_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'))");
			}
			pst = con.prepareStatement(sbQuery1.toString());
			rs = pst.executeQuery();
			while(rs.next()) {
				proCnt = rs.getInt("proCount");
				proCount = rs.getInt("proCount")/10;
				if(rs.getInt("proCount")%10 != 0) {
					proCount++;
				}
			}
			rs.close();
			pst.close();*/
			
			
			
			
			List<String> alAddedBy = new ArrayList<String>();
			StringBuilder sbQuery2 = new StringBuilder();
			sbQuery2.append("select distinct(added_by) as added_by from projectmntnc where pro_id > 0 ");
			if(getProType() == null || getProType().equals("") || getProType().equals("null") || getProType().equals("L")) {
				sbQuery2.append(" and approve_status='n' ");
			} else if(getProType() != null && getProType().equals("C")) {
				sbQuery2.append(" and approve_status='approved' ");
			} else if(getProType() != null && getProType().equals("B")) {
				sbQuery2.append(" and approve_status='blocked' ");
			} else if(getProType() != null && getProType().equals("P")) {
				sbQuery2.append(" and approve_status='pipelined' ");
			}
			
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery2.append(" and (org_id in ("+(String)session.getAttribute(ORG_ACCESS)+") ");
				if(getPageType() != null && getPageType().equals("MP")) {
				//===start parvez date: 13-10-2022===	
					sbQuery2.append(" or ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%') ");
				//===end parvez date: 13-10-2022===	
				}
				sbQuery2.append(") ");
			}
//			if(getLocation()!=null && getLocation().length>0) {
//	            sbQuery2.append(" and wlocation_id in ("+StringUtils.join(getLocation(), ",")+") ");
//	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
//				sbQuery2.append(" and (wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") ");
//				if(getPageType() != null && getPageType().equals("MP")) {
//					sbQuery2.append(" or ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
//				}
//				sbQuery2.append(") ");
//			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && (getPageType() == null || !getPageType().equals("MP"))) {
			//===start parvez date: 13-10-2022===	
				sbQuery2.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
//					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
					+" or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%' ) ");
			//===end parvez date: 13-10-2022===	
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && getPageType() != null && getPageType().equals("MP")) {
			//===start parvez date: 13-10-2022===	
				sbQuery2.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
//					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
					+" or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%' ) ");
			//===end parvez date: 13-10-2022===	
				
			} else if(getPageType() != null && getPageType().equals("MP")) {
		//===start parvez date: 13-10-2022===		
				sbQuery2.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//					+uF.parseToInt((String)session.getAttribute(EMPID))+") or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
					+uF.parseToInt((String)session.getAttribute(EMPID))+") or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%') ");
		//===end parvez date: 13-10-2022===		
			} else if(getPageType() != null && getPageType().equals("MYPRO")) {
				sbQuery2.append(" and pro_id in (select pro_id from project_emp_details where emp_id = " + uF.parseToInt((String)session.getAttribute(EMPID)) + ") ");
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(CUSTOMER)) {
				sbQuery2.append(" and poc="+uF.parseToInt((String)session.getAttribute(EMPID))+" ");
			}
			pst = con.prepareStatement(sbQuery2.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				alAddedBy.add(rs.getString("added_by"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbAddedbyOption = new StringBuilder();
			for(int i=0; alAddedBy != null && !alAddedBy.isEmpty() && i<alAddedBy.size(); i++) {
				if(uF.parseToInt(alAddedBy.get(i)) > 0) {
					if(uF.parseToInt(getAssignedBy()) == uF.parseToInt(alAddedBy.get(i))) {
						sbAddedbyOption.append("<option value='"+alAddedBy.get(i)+"' selected>"+hmEmpName.get(alAddedBy.get(i))+"</option>");
					} else {
						sbAddedbyOption.append("<option value='"+alAddedBy.get(i)+"'>"+hmEmpName.get(alAddedBy.get(i))+"</option>");
					}
				}
			}
			request.setAttribute("sbAddedbyOption", sbAddedbyOption.toString());
			
			Map<String, List<String>> hmProject = new LinkedHashMap<String, List<String>>();
			Map<String, List<List<String>>> hmTasks = new LinkedHashMap<String, List<List<String>>>();
			Map<String, List<List<String>>> hmSubTasks = new LinkedHashMap<String, List<List<String>>>();
			
//			Map<String, String> hmEmployee = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmServicemap = CF.getProjectServicesMap(con, true);
			Map<String, String> hmClientName = CF.getProjectClientMap(con, uF);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from projectmntnc where pro_id > 0 ");
			
			if(uF.parseToInt(getProId()) > 0) {
				sbQuery.append(" and pro_id = "+uF.parseToInt(getProId())+" ");
			}
			if(getProType() == null || getProType().equals("") || getProType().equals("null") || getProType().equals("L")) {
				sbQuery.append(" and approve_status='n' ");
			} else if(getProType() != null && getProType().equals("C")) {
				sbQuery.append(" and approve_status='approved' ");
			} else if(getProType() != null && getProType().equals("B")) {
				sbQuery.append(" and approve_status='blocked' ");
			} else if(getProType() != null && getProType().equals("P")) {
				sbQuery.append(" and approve_status='pipelined' ");
			}
			
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and (org_id in ("+(String)session.getAttribute(ORG_ACCESS)+") ");
				if(getPageType() != null && getPageType().equals("MP")) {
			//===start parvez date: 13-10-2022===		
					sbQuery.append(" or ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%') ");
			//===end parvez date: 13-10-2022===		
				}
				sbQuery.append(") ");
			}
//			if(getLocation()!=null && getLocation().length>0) {
//	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getLocation(), ",")+") ");
//	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
//				sbQuery.append(" and (wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") ");
//				if(getPageType() != null && getPageType().equals("MP")) {
//					sbQuery.append(" or ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
//				}
//				sbQuery.append(") ");
//			}
			
			if(getF_sbu()!=null && getF_sbu().length > 0 && !getF_sbu()[0].trim().equals("")) {
				sbQuery.append(" and sbu_id in ("+StringUtils.join(getF_sbu(), ",")+") ");
			}
			
			if(getF_service()!=null && getF_service().length > 0) {
				String service = getConcateData(getF_service());
				sbQuery.append(" and service in ("+service+") ");
			}
			
			if(getClient()!=null && getClient().length > 0 && !getClient()[0].trim().equals("")) {
				sbQuery.append(" and client_id in ("+StringUtils.join(getClient(), ",")+") ");
			}
			
			if(getPro_id()!=null && getPro_id().length > 0 && !getPro_id()[0].trim().equals("")) {
				sbQuery.append(" and pro_id in ("+StringUtils.join(getPro_id(), ",")+") ");
			}
			
			if(getManagerId()!=null && getManagerId().length > 0 && !getManagerId()[0].trim().equals("")) {
			//===start parvez date: 13-10-2022===	
//				sbQuery.append(" and project_owner in ("+StringUtils.join(getManagerId(), ",")+") ");
				sbQuery.append(" and (");
				for(int ii=0;ii<getManagerId().length; ii++){
					if(ii == 0){
						sbQuery.append("project_owners like '%,"+getManagerId()[ii]+",%' ");
					}else{
						sbQuery.append(" or project_owners like '%,"+getManagerId()[ii]+",%' ");
					}
				}
				sbQuery.append(" )");
				
			//===end parvez date: 13-10-2022===	
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && (getPageType() == null || !getPageType().equals("MP"))) {
		//===start parvez date: 13-10-2022===		
				sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
//					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
					+" or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%' ) ");
		//===end parvez date: 13-10-2022===		
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && getPageType() != null && getPageType().equals("MP")) {
		//===start parvez date: 13-10-2022===		
				sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
//					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
					+" or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%' ) ");
		//===end parvez date: 13-10-2022===		
				
			} else if(getPageType() != null && getPageType().equals("MP")) {
		//===start parvez date: 13-10-2022===		
				sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//					+uF.parseToInt((String)session.getAttribute(EMPID))+") or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
					+uF.parseToInt((String)session.getAttribute(EMPID))+") or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%') ");
		//===end parvez date: 13-10-2022===		
			} else if(getPageType() != null && getPageType().equals("MYPRO")) {
				sbQuery.append(" and pro_id in (select pro_id from project_emp_details where emp_id = " + uF.parseToInt((String)session.getAttribute(EMPID)) + ") ");
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(CUSTOMER)) {
				sbQuery.append(" and poc="+uF.parseToInt((String)session.getAttribute(EMPID))+" ");
			}
			
			if(getProStatus() != null && uF.parseToInt(getProStatus()) == 1) {
				sbQuery.append(" and completed>0 and deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			} else if(getProStatus() != null && uF.parseToInt(getProStatus()) == 2) {
				sbQuery.append(" and (completed = 0 or completed is null) and deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			} else if(getProStatus() != null && uF.parseToInt(getProStatus()) == 3) {
				sbQuery.append(" and deadline < '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			}
			
			if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 1) {
				sbQuery.append(" and billing_type != 'F' ");
			} else if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 2) {
				sbQuery.append(" and billing_type = 'F' ");
			}
			
			if(getAssignedBy() != null && uF.parseToInt(getAssignedBy()) >0) {
				sbQuery.append(" and added_by = "+uF.parseToInt(getAssignedBy())+" ");
			}
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
				sbQuery.append(" and (upper(pro_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or client_id in (select client_id from client_details where upper(client_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'))");
			}
			if(getSortBy() != null && uF.parseToInt(getSortBy()) == 1) {
				sbQuery.append(" order by start_date desc ");
			} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 2) {
				sbQuery.append(" order by start_date ");
			} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 3) {
				sbQuery.append(" order by pro_name ");
			} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 4) {
				sbQuery.append(" order by pro_name desc ");
			} else {
				sbQuery.append(" order by start_date desc ");
			}
			int intOffset = uF.parseToInt(minLimit);
			sbQuery.append(" limit 10 offset "+intOffset+"");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst View All Projects ===>> " + pst);
//			System.out.println("pst date ==========>> " + new Date());
			rs = pst.executeQuery();
			
//			request.setAttribute("strLimit", "10"); 
//			request.setAttribute("strDivCount", "1");
			GetPriorityList objGP = new GetPriorityList();
			
			StringBuilder sbProIds = null;
			while(rs.next()) {
				if(sbProIds == null) {
					sbProIds = new StringBuilder();
					sbProIds.append(rs.getString("pro_id"));
				} else {
					sbProIds.append(","+rs.getString("pro_id"));
				}
				List<String> alInner = new ArrayList<String>();
				
				Map<String, String> hmProFreqStartEndDate = getProjectCurrentFreqStartEndDate(con, uF, rs.getString("pro_id"));
				Map<String, String> hmProjectData = new HashMap<String, String>();
				hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
				hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
					
				Map<String, String> hmProAWDaysAndHrs = CF.getProjectActualAndBillableEfforts(con, rs.getString("pro_id"), hmProjectData);
				
				alInner.add(rs.getString("pro_id"));
				alInner.add(rs.getString("pro_name")); //1
				alInner.add(uF.showData(objGP.getPriority(uF.parseToInt(rs.getString("priority"))), "")); //2
				
				alInner.add(uF.showData(hmServicemap.get(rs.getString("service")), "")); //3
				if(hmProFreqStartEndDate != null && !hmProFreqStartEndDate.isEmpty()) {
					alInner.add(uF.getDateFormat(hmProFreqStartEndDate.get("FREQ_START_DATE"), DBDATE, CF.getStrReportDateFormat())); //4
					alInner.add(uF.getDateFormat(hmProFreqStartEndDate.get("FREQ_END_DATE"), DBDATE, CF.getStrReportDateFormat())); //5
				} else {
					alInner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, CF.getStrReportDateFormat())); //4
					alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat())); //5
				}
				double proDealineCompletePercent = 0;
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime")))+" days"); //6
					String actualTime = hmProAWDaysAndHrs.get("ACTUAL_DAYS");
					proDealineCompletePercent = uF.parseToDouble(actualTime);
					alInner.add(actualTime+"::::days"); //7
				} else if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime")))+" months"); //6
					String actualTime = hmProAWDaysAndHrs.get("ACTUAL_DAYS");
					double actualMonths = uF.parseToDouble(actualTime) / 30;
					proDealineCompletePercent = actualMonths;
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(actualMonths)+"::::months"); //7
				} else {
					alInner.add(uF.getTotalTimeMinutes100To60(rs.getString("idealtime"))+" hrs");//6
					String actualTime = hmProAWDaysAndHrs.get("ACTUAL_HRS");
					proDealineCompletePercent = uF.parseToDouble(uF.getTotalTimeMinutes100To60(actualTime));
					alInner.add(uF.getTotalTimeMinutes100To60(actualTime)+"::::hrs"); //7
				}
				
				
				alInner.add(rs.getString("completed")); //8
				
				String strProDeadlineColor = "";
				if(proDealineCompletePercent < uF.parseToDouble(rs.getString("idealtime"))) {
					strProDeadlineColor = "green";
				} else if(proDealineCompletePercent == uF.parseToDouble(rs.getString("idealtime"))) {
					strProDeadlineColor = "yellow";
				} else if(proDealineCompletePercent > uF.parseToDouble(rs.getString("idealtime"))) {
					strProDeadlineColor = "red";
				} else {
					strProDeadlineColor = "";
				}
				
				java.util.Date dtEntryDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE);
				java.util.Date dtPrevDate = uF.getPrevDate(CF.getStrTimeZone(), 7);
				
				alInner.add(strProDeadlineColor); //9
				
				alInner.add(uF.showData(hmClientName.get(rs.getString("client_id")), "")); //10
				if(dtEntryDate!=null && dtEntryDate.after(dtPrevDate)) {
					alInner.add("1"); //11 show new icon on new projects	
				} else {
					alInner.add("0"); //11
				}
				
				alInner.add(rs.getString("actual_calculation_type")); //12
				alInner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT)); //13
				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT)); //14
				
				double deadLinePercent = 0;
				String days = null;
				String currdays = null;
				if(hmProFreqStartEndDate != null && !hmProFreqStartEndDate.isEmpty()) {
					days = uF.dateDifference(hmProFreqStartEndDate.get("FREQ_START_DATE"), DBDATE, hmProFreqStartEndDate.get("FREQ_END_DATE"), DBDATE);
					if(rs.getString("approve_status") != null && !rs.getString("approve_status").equals("n")) {
						currdays = uF.dateDifference(rs.getString("start_date"), DBDATE, rs.getString("approve_date"), DBDATE);
					} else {
						currdays = uF.dateDifference(hmProFreqStartEndDate.get("FREQ_START_DATE"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
					} 
				} else {
					days = uF.dateDifference(rs.getString("start_date"), DBDATE, rs.getString("deadline"), DBDATE);
					if(rs.getString("approve_status") != null && !rs.getString("approve_status").equals("n")) {
						currdays = uF.dateDifference(rs.getString("start_date"), DBDATE, rs.getString("approve_date"), DBDATE);
					} else {
						currdays = uF.dateDifference(rs.getString("start_date"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
					}
				}
				if(uF.parseToDouble(days) > 0) {
					deadLinePercent = (uF.parseToDouble(currdays) / uF.parseToDouble(days)) * 100;
				}
				String proDeadlinePercentColor = "";
				if(deadLinePercent <= 75) {
					proDeadlinePercentColor = "green";
				} else if(deadLinePercent > 75 && deadLinePercent < 100) {
					proDeadlinePercentColor = "yellow";
				} else if(deadLinePercent >= 100) {
					proDeadlinePercentColor = "red";
				} else {
					proDeadlinePercentColor = "";
				}
				alInner.add(proDeadlinePercentColor); //15
				alInner.add(rs.getString("project_owner")); //16
				alInner.add(rs.getString("added_by")); //17
				
				hmProject.put(rs.getString("pro_id"), alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmProject", hmProject);

			if(sbProIds != null || getSingleProid() != 0) {
				
				Map<String, String> hmPDocumentCounter = new HashMap<String, String>();
				pst = con.prepareStatement("select count(pro_document_id) as count, pro_id from project_document_details where pro_id in ("+sbProIds.toString()+") and file_size is not null group by pro_id");
				rs = pst.executeQuery();
				while (rs.next()) {
					hmPDocumentCounter.put(rs.getString("pro_id"), rs.getString("count"));
				}
				rs.close();
				pst.close();
				
	//			System.out.println("hmPDocumentCounter ====>>> " + hmPDocumentCounter);
				request.setAttribute("hmPDocumentCounter", hmPDocumentCounter);
				
				getProMilestoneAndCompletedMilestone(con, uF, sbProIds.toString());
			}
			request.setAttribute("hmSubTasks", hmSubTasks);
			request.setAttribute("hmTasks", hmTasks);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	Map<String, String> getProjectCurrentFreqStartEndDate(Connection con, UtilityFunctions uF, String proId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmProFreqStartEndDate = new HashMap<String, String>();
		try {
			
			pst = con.prepareStatement("select freq_start_date, freq_end_date from projectmntnc_frequency where freq_end_date >= ? and pro_id=? order by freq_end_date limit 1");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, uF.parseToInt(proId));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmProFreqStartEndDate.put("FREQ_START_DATE", rs.getString("freq_start_date"));
				hmProFreqStartEndDate.put("FREQ_END_DATE", rs.getString("freq_end_date"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmProFreqStartEndDate;
	}

	private void getProMilestoneAndCompletedMilestone(Connection con, UtilityFunctions uF, String proId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select ai.*, pmc.start_date as pmc_start_date, pmc.deadline as pmc_deadline, pmc.bill_days_type, pmc.hours_for_bill_day, " +
				"pmc.actual_calculation_type, pmc.billing_type from activity_info ai, projectmntnc pmc where ai.pro_id=pmc.pro_id and " +
				"ai.parent_task_id = 0 and task_accept_status != -1 and pmc.pro_id in ("+proId+")");
			pst = con.prepareStatement(sbQuery.toString()); //and ai.pro_id=?
//			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst======main==="+pst);
			rs = pst.executeQuery();
			Map<String, String> hmProIds = new HashMap<String, String>();
			Map<String, String> hmProTaskData = new HashMap<String, String>();
			
			
			while (rs.next()) {
				
				double taskIdealTime = uF.parseToDouble(hmProTaskData.get(rs.getString("pro_id")+"_IDEAL_TIME"));
				taskIdealTime = taskIdealTime + uF.parseToDouble(rs.getString("idealtime"));
				double taskWoredTime = uF.parseToDouble(hmProTaskData.get(rs.getString("pro_id")+"_WORKED_TIME"));
				double actWorkTime = (uF.parseToDouble(rs.getString("idealtime")) * uF.parseToDouble(rs.getString("completed"))) / 100;
				taskWoredTime = taskWoredTime + actWorkTime;
				
				hmProTaskData.put(rs.getString("pro_id")+"_IDEAL_TIME", taskIdealTime+"");
				hmProTaskData.put(rs.getString("pro_id")+"_WORKED_TIME", taskWoredTime+"");
				
				hmProIds.put(rs.getString("pro_id"), rs.getString("pro_id"));
				
				/*double taskCompPercent = uF.parseToDouble(hmProTaskCompletePercent.get(rs.getString("pro_id")));
				taskCompPercent = taskCompPercent + uF.parseToDouble(rs.getString("completed"));
				hmProTaskCompletePercent.put(rs.getString("pro_id"), taskCompPercent+"");
				
				int taskCount = uF.parseToInt(hmProTaskCount.get(rs.getString("pro_id")));
					taskCount++;
				hmProTaskCount.put(rs.getString("pro_id"), taskCount+"");*/
				
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = hmProIds.keySet().iterator();
			
			Map<String, String> hmProCompPercent = new HashMap<String, String>();
			Map<String, Map<String, String>> hmProMileAndCMileCnt = new HashMap<String, Map<String, String>>();
			while (it.hasNext()) {
				Map<String, String> hmMilestoneAndCompletedMilestone = new HashMap<String, String>();
				String strProId = it.next();
				double dblProCompletePercent = 0.0d;
				if(uF.parseToDouble(hmProTaskData.get(strProId+"_IDEAL_TIME")) > 0) {
					dblProCompletePercent = (uF.parseToDouble(hmProTaskData.get(strProId+"_WORKED_TIME")) * 100) / uF.parseToDouble(hmProTaskData.get(strProId+"_IDEAL_TIME"));
				}
				hmProCompPercent.put(strProId, dblProCompletePercent+"");
				
				int milestoneCount = 0;
				int completedMilestoneCount = 0;
				pst = con.prepareStatement("select pmd.*, p.milestone_dependent_on from project_milestone_details pmd, projectmntnc p where pmd.pro_id= p.pro_id and pmd.pro_id=?");
				pst.setInt(1, uF.parseToInt(strProId));
				rs = pst.executeQuery();
				while (rs.next()) {
					if(uF.parseToInt(rs.getString("milestone_dependent_on")) == 2) {
						int intcomplete = getProTaskCompleted(con, uF, rs.getString("pro_task_id"));
						completedMilestoneCount += intcomplete;
					} else if(uF.parseToInt(rs.getString("milestone_dependent_on")) == 1) {
						if(rs.getDouble("pro_completion_percent") <= uF.parseToDouble(hmProCompPercent.get(strProId))) {
							completedMilestoneCount++;
						}
					}
					
					milestoneCount++;
				}
				rs.close();
				pst.close();
				
				hmMilestoneAndCompletedMilestone.put("MILESTONE_COUNT", milestoneCount+"");
				hmMilestoneAndCompletedMilestone.put("COMPLETED_MILESTONE_COUNT", completedMilestoneCount+"");
				hmProMileAndCMileCnt.put(strProId, hmMilestoneAndCompletedMilestone);
			}
			
			request.setAttribute("hmProCompPercent", hmProCompPercent);
			request.setAttribute("hmProMileAndCMileCnt", hmProMileAndCMileCnt);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private int getProTaskCompleted(Connection con, UtilityFunctions uF, String taskId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int intTaskCompleted = 0;
		try {
			
			pst = con.prepareStatement("select task_id from activity_info where task_id =? and approve_status = 'approved' and task_accept_status != -1");
			pst.setInt(1, uF.parseToInt(taskId));
			rs = pst.executeQuery();
			while (rs.next()) {
				intTaskCompleted = 1;
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}
		return intTaskCompleted;
	}
	
	
	private String getConcateData(String[] data) {
		StringBuilder sb = new StringBuilder();
//		StringBuilder sb1 = new StringBuilder();
		for(int i=0;i<data.length;i++) {
			if(i==0) {
//				sb1.append(data[i]);
				sb.append("'"+data[i]+"'");
			} else {
//				sb1.append(" ,"+data[i]);
				sb.append(",'"+data[i]+"'");
			}
		}
//		setService_id(sb1.toString());
		return sb.toString();
	}
	

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public List<String> getTime1() {
		return time1;
	}

	public void setTime1(List<String> time1) {
		this.time1 = time1;
	}

	public List<String> getTaskStatus1() {
		return taskStatus1;
	}

	public void setTaskStatus1(List<String> taskStatus1) {
		this.taskStatus1 = taskStatus1;
	}

	public List<String> getTotalhrz1() {
		return totalhrz1;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public void setTotalhrz1(List<String> totalhrz1) {
		this.totalhrz1 = totalhrz1;
	}
	
	public int getTask_id() {
		return task_id;
	}

	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}

	public String getProjectID() {
		return projectID;
	}

	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Map<Integer, List<String>> getActivity_al() {
		return activity_al;
	}

	public void setActivity_al(Map<Integer, List<String>> activity_al) {
		this.activity_al = activity_al;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public List<Integer> getIndex() {
		return index;
	}

	public void setIndex(List<Integer> index) {
		this.index = index;
	}

	public Map<Integer, List<String>> getAl() {
		return al;
	}

	public void setAl(Map<Integer, List<String>> al) {
		this.al = al;
	}

	public List<String> getAlInner() {
		return alInner;
	}

	public void setAlInner(List<String> alInner) {
		this.alInner = alInner;
	}

	public List<Integer> getProjectidlist() {
		return projectidlist;
	}

	public void setProjectidlist(List<Integer> projectidlist) {
		this.projectidlist = projectidlist;
	}

	public List<String> getProjectlist() {
		return projectlist;
	}

	public void setProjectlist(List<String> projectlist) {
		this.projectlist = projectlist;
	}

	public int getSingleProid() {
		return singleProid;
	}

	public void setSingleProid(int singleProid) {
		this.singleProid = singleProid;
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
	}
	
	public String[] getPro_id() {
		return pro_id;
	}
	
	public void setPro_id(String[] pro_id) {
		this.pro_id = pro_id;
	}
	
	public String[] getManagerId() {
		return managerId;
	}
	
	public void setManagerId(String[] managerId) {
		this.managerId = managerId;
	}
	
	public String[] getClient() {
		return client;
	}
	
	public void setClient(String[] client) {
		this.client = client;
	}
	
	public String[] getF_service() {
		return f_service;
	}
	
	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}
	
	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

	public String getStrDivCount() {
		return strDivCount;
	}

	public void setStrDivCount(String strDivCount) {
		this.strDivCount = strDivCount;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getProPage() {
		return proPage;
	}

	public void setProPage(String proPage) {
		this.proPage = proPage;
	}

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}

	public String getService_id() {
		return service_id;
	}

	public void setService_id(String service_id) {
		this.service_id = service_id;
	}

	public String getCb() {
		return cb;
	}

	public void setCb(String cb) {
		this.cb = cb;
	}


	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}


	public String getProStatus() {
		return proStatus;
	}

	public void setProStatus(String proStatus) {
		this.proStatus = proStatus;
	}

	public String getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(String assignedBy) {
		this.assignedBy = assignedBy;
	}

	public String getRecurrOrMiles() {
		return recurrOrMiles;
	}

	public void setRecurrOrMiles(String recurrOrMiles) {
		this.recurrOrMiles = recurrOrMiles;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}


	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	public String[] getF_sbu() {
		return f_sbu;
	}

	public void setF_sbu(String[] f_sbu) {
		this.f_sbu = f_sbu;
	}

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}

	public String getStrSBU() {
		return strSBU;
	}

	public void setStrSBU(String strSBU) {
		this.strSBU = strSBU;
	}

	public String getStrService() {
		return strService;
	}

	public void setStrService(String strService) {
		this.strService = strService;
	}

	public String getStrProjectId() {
		return strProjectId;
	}

	public void setStrProjectId(String strProjectId) {
		this.strProjectId = strProjectId;
	}

	public String getStrManagerId() {
		return strManagerId;
	}

	public void setStrManagerId(String strManagerId) {
		this.strManagerId = strManagerId;
	}

	public String getStrClient() {
		return strClient;
	}

	public void setStrClient(String strClient) {
		this.strClient = strClient;
	}

	public String getApprove() {
		return approve;
	}

	public void setApprove(String approve) {
		this.approve = approve;
	}

	public String getBlocked() {
		return blocked;
	}

	public void setBlocked(String blocked) {
		this.blocked = blocked;
	}

	public String getApprovePr() {
		return approvePr;
	}

	public void setApprovePr(String approvePr) {
		this.approvePr = approvePr;
	}

	public String getSubmitType() {
		return submitType;
	}

	public void setSubmitType(String submitType) {
		this.submitType = submitType;
	}

	public String getWorking() {
		return working;
	}

	public void setWorking(String working) {
		this.working = working;
	}

}
