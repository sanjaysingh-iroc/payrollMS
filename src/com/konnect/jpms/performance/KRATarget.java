package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.employee.EmpDashboardData;
import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class KRATarget extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId;
	String strEmpOrgId;
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF;
	private String strID;
	
	private String alertStatus;
	private String alert_type;
	
	private String dataType;
	
	private String proPage;
	private String minLimit;
	private String alertID;
	private String fromPage;
	
	private String appId;
	private String appFreqId;
	private String strComment;
	private String operation;
	private String discussionId;
	private String apStatus;
	
	public String execute() {
//		System.out.println("In kraTarget.java");
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
//		System.out.println("empId===>"+strSessionEmpId);
		
		strEmpOrgId = (String) session.getAttribute(ORGID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		setStrID(strEmpOrgId);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) 
			return "login";
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, "/jsp/performance/KRATarget.jsp");
		request.setAttribute(TITLE, TGoalsKrasTargetsReviews);
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		if(getFromPage() == null || getFromPage().equals("") || getFromPage().equalsIgnoreCase("null" )){
			sbpageTitleNaviTrail.append("<li><i class=\"fa fa-group\"></i><a href=\"KRATarget.action\" style=\"color: #3c8dbc;\">My HR</a></li>" +
			"<li class=\"active\">Goals, KRAs, Targets, Reviews</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		}
		
		if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")) {
			setDataType("L");
		}
		
		if(CF != null) {
			CF.getOrientationMemberDetails(request);
		}
		/* created by seema */
		if(request.getParameter("download")!=null && !request.getParameter("download").isEmpty()){
			request.setAttribute("download", request.getParameter("download"));			
		}
		/* created by seema */
		
	//===start parvez date: 21-07-2022===	
		if(getOperation() != null && getOperation().equals("update")){
			insertUserRatingComment(uF);
		} else if(getOperation() != null && getOperation().equals("SO")){
			signOffUserDiscussion(uF);
		}
	//===end parvez date: 21-07-2022===	
		
		getEmployeeAssignedKRAAndGoalTarget(uF);
		
		getPeerGoalKRATargetDetails(uF);
//		getEmpKRATargetDetails(uF);
		getEmpImage(uF);
		unPublishOldReview();
		getAppraisalReport(uF);
		getAppraisalDetails();
		getOneOneDiscussionDetails(uF);		//added by parvez date: 21-07-2022
		getAppraisalFinalScore(uF);			//added by parvez date: 23-03-2023
//===start parvez date: 06-07-2022===
		getAppraisalReopenDetails(uF);
//===end parvez date: 06-07-2022===		
		
		viewSelfReviewPublishApproval(uF);
		
		getOrientationMember();
		getAppriesalSections();
		getExistUsersInAQA();
		getOrientTypeWiseIds();
		
		if(strSessionEmpId != null && uF.parseToInt(strSessionEmpId) > 0) {
			List<String> empList = new ArrayList<String>();
			empList.add(strSessionEmpId);
			getInitiativeList(uF,empList);
			
		
		}
			
		checkGoalStatus(uF);
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		
//		if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(GOAL_KRA_TARGET_ALERT)) {
//			updateUserAlerts(GOAL_KRA_TARGET_ALERT);
//		}
//		
//		if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(SELF_REVIEW_APPROVAL_ALERT)) {
//			updateUserAlerts(SELF_REVIEW_APPROVAL_ALERT);
//		}
		
		if(getFromPage() != null && getFromPage().equalsIgnoreCase("MyHR") ) {
			return VIEW;
		}
		return SUCCESS;
	}
	
	
	private void getEmployeeAssignedKRAAndGoalTarget(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		// boolean flag = false;
		try {
			con = db.makeConnection(con);
			Map<String, List<String>> hmEmpKRAIds = new HashMap<String, List<String>>();
			String kraTypes = INDIVIDUAL_GOAL+","+INDIVIDUAL_KRA+","+EMPLOYEE_KRA;
			pst = con.prepareStatement("select * from goal_kras where goal_type in ("+kraTypes+")"); // and emp_ids like '%,"+empId+",%'
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while (rs.next()) {
				List<String> alEmpIds = new ArrayList<String>();
				if(rs.getString("emp_ids") !=null) {
					alEmpIds = Arrays.asList(rs.getString("emp_ids").split(","));
				}
				for(int i=0; alEmpIds!=null && i<alEmpIds.size(); i++) {
					if(uF.parseToInt(alEmpIds.get(i))>0) {
						List<String> alKRAIds = hmEmpKRAIds.get(alEmpIds.get(i));
						if(alKRAIds==null) alKRAIds = new ArrayList<String>();
						alKRAIds.add(rs.getString("goal_kra_id"));
						hmEmpKRAIds.put(alEmpIds.get(i), alKRAIds);
					}
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmpKRAIds", hmEmpKRAIds);
			
			Map<String, List<String>> hmEmpGoarTargetIds = new HashMap<String, List<String>>();
			pst = con.prepareStatement("select * from goal_details"); //where emp_ids like '%,"+empId+",%'
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> alEmpIds = new ArrayList<String>();
				if(rs.getString("emp_ids") !=null) {
					alEmpIds = Arrays.asList(rs.getString("emp_ids").split(","));
				}
				for(int i=0; alEmpIds!=null && i<alEmpIds.size(); i++) {
					if(uF.parseToInt(alEmpIds.get(i))>0) {
						List<String> alGoarTargetIds = hmEmpGoarTargetIds.get(alEmpIds.get(i));
						if(alGoarTargetIds==null) alGoarTargetIds = new ArrayList<String>();
						alGoarTargetIds.add(rs.getString("goal_id"));
						hmEmpGoarTargetIds.put(alEmpIds.get(i), alGoarTargetIds);
					}
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmpGoarTargetIds", hmEmpGoarTargetIds);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getInitiativeList(UtilityFunctions uF,List<String> empList){
		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmPerspectiveData = new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("Select * from bsc_perspective_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String> hm = new HashMap<String, String>();
				hm.put("PERSPECTIVE_ID", rs.getString("bsc_perspective_id"));
				hm.put("PERSPECTIVE_NAME", rs.getString("bsc_perspective_name"));
				hm.put("PERSPECTIVE_WEIGHTAGE", rs.getString("weightage"));
				hm.put("PERSPECTIVE_DESCRIPTION", rs.getString("perspective_description"));
				hm.put("PERSPECTIVE_COLOR", rs.getString("perspective_color"));
	
				hmPerspectiveData.put(rs.getString("bsc_perspective_id"), hm);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmPerspectiveData", hmPerspectiveData);
				rs.close();
				pst.close();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	private void getPeerGoalKRATargetDetails(UtilityFunctions uF) {
		// TODO Auto-generated method stub
		
		GoalKRA kra = new GoalKRA();
		kra.session = session;
		kra.CF = CF;
		kra.setServletRequest(request);
		kra.setDataType(getDataType());
//		kra.execute();
		kra.strSessionEmpId = strSessionEmpId;
		kra.strEmpOrgId = strEmpOrgId;
		kra.strUserType = strSessionUserType;
		kra.setFromPage("MYGKT");
//		List<String> empList1 = kra.getEmployeeList(uF);
		List<String> empList1 = getEmployeeList();
		empList1.add(strSessionEmpId);
//		System.out.println("empList1 ===>> " + empList1);
		kra.setStrEmpId(strSessionEmpId);
		kra.getEmpGoalKRADetails(uF,empList1);
		kra.getEmpKRADetails(uF,empList1);
		kra.getKRARatingAndCompletionStatus(uF, empList1);
		kra.getEmpTargetDetails(uF, empList1);
		kra.checkGoalKRATargetStatus(uF);
		kra.checkGoalKRATargetAlognedWithAllowance(uF);
		kra.getActualAchievedGoal(uF);
		
		request.setAttribute("empList", empList1);
	
	}

	public List<String> getEmployeeList() {
		List<String> empList = new ArrayList<String>(); 
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		try {
//			System.out.println("self=====>"+self);
//			System.out.println("type=====>"+type);
//			if (type == 4) {
				pst = con.prepareStatement("select emp_id from employee_official_details where supervisor_emp_id in (select supervisor_emp_id " +
					" from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true " +
					" and emp_id in ("+strSessionEmpId+") and supervisor_emp_id!=0) and emp_id not in ("+strSessionEmpId+") and emp_id >0");
//				pst=con.prepareStatement("select emp_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and wlocation_id in("+strsb5+") and grade_id in("+sb4.toString()+") group by emp_id");
				rs = pst.executeQuery();
				while(rs.next()) {
					if(!empList.contains(rs.getString("emp_id").trim())) {
						empList.add(rs.getString("emp_id").trim());	
					}
				}
				rs.close();
				pst.close();
				
//			} else if (type == 0) {
//				
//				pst = con.prepareStatement("select wlocation_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and emp_id in("+strSessionEmpId+") group by wlocation_id");
//				rs = pst.executeQuery();
//				StringBuilder sb5 = new StringBuilder();
//				int cnt = 0;
//				while(rs.next()) {
//					if(cnt == 0) {
//						sb5.append(rs.getString("wlocation_id").trim());
//					} else {
//						sb5.append(","+rs.getString("wlocation_id").trim());
//					}
//					cnt++;
//				}
//				rs.close();
//				pst.close();
//				
//				//String strsb5 = (sb5 != null ? sb5.toString().substring(1, sb5.toString().length()-1) : "");
//				pst=con.prepareStatement("select emp_id from employee_official_details eod, employee_personal_details epd where " +
//					" epd.emp_per_id = eod.emp_id and is_alive = true and emp_id in ("+ strSessionEmpId+ ") and emp_id>0 ");
////				pst=con.prepareStatement("select emp_per_id from employee_official_details eod,user_details ud, employee_personal_details epd where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=? and wlocation_id in("+sb5.toString()+")");
////				pst.setInt(1, 7);
//				rs=pst.executeQuery();
//				while(rs.next()) {
//					if(!empList.contains(rs.getString("emp_id").trim())) {
//						empList.add(rs.getString("emp_id").trim());	
//					}
//				}
//				rs.close();
//				pst.close();
//				
//			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return empList;
	}

	private void checkGoalStatus(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			List<String> alCheckList = new ArrayList<String>();
			pst = con.prepareStatement("select goal_kra_target_id from appraisal_question_details where goal_kra_target_id is not null");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(!alCheckList.contains(rs.getString("goal_kra_target_id"))) {
					alCheckList.add(rs.getString("goal_kra_target_id"));
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("alCheckList=======>"+alCheckList.toString());
			request.setAttribute("alCheckList", alCheckList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void viewSelfReviewPublishApproval(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			String strEmpName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select effective_id from work_flow_details where effective_type='"+WORK_FLOW_SELF_REVIEW+"'" +
				" and (is_approved = 0 or is_approved = -1) and effective_id in (select appraisal_details_id from appraisal_details  where my_review_status = 1 and " +
				" self_ids like '%,"+strSessionEmpId+",%' and publish_is_approved != 1) group by effective_id ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();			
			Map<String, String> hmReviewId = new HashMap<String, String>();	
			while(rs.next()) {
				hmReviewId.put(rs.getString("effective_id"), rs.getString("effective_id"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("strEmpName", strEmpName);
			request.setAttribute("hmReviewId", hmReviewId);
			
		} catch (Exception e) {
			e.printStackTrace(); 
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	/*private void viewEmployeeLoanByApproval(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
  
			con = db.makeConnection(con);
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmEmpNamMap = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
			String locationID=hmEmpWlocationMap.get(strSessionEmpId);
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			
			
//			pst = con.prepareStatement("select effective_id,max(member_position) as member_position from work_flow_details wf where is_approved=0 " +
//					"and effective_type='"+WORK_FLOW_LOAN+"' group by effective_id");
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,max(member_position) as member_position from work_flow_details wf where is_approved=0 " +
					"and effective_type='"+WORK_FLOW_SELF_REVIEW+"' and effective_id in (select appraisal_details_id from appraisal_details " +
					" where my_review_status = 1 and self_ids like '%,"+strSessionEmpId+",%' ) group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()){
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select effective_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
//					" and is_approved=0 and effective_type='"+WORK_FLOW_LOAN+"' group by effective_id ");
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
					" and is_approved=0 and effective_type='"+WORK_FLOW_SELF_REVIEW+"' and effective_id in (select appraisal_details_id from appraisal_details " +
					" where my_review_status = 1 and self_ids like '%,"+strSessionEmpId+",%' ) group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId));			
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			while(rs.next()){
				hmMemNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_LOAN+"' group by effective_id");
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_SELF_REVIEW+"'" +
					" and effective_id in (select appraisal_details_id from appraisal_details " +
					" where my_review_status = 1 and self_ids like '%,"+strSessionEmpId+",%' ) group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()){
				if(!deniedList.contains(rs.getString("effective_id"))){
					deniedList.add(rs.getString("effective_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
					" and effective_type='"+WORK_FLOW_SELF_REVIEW+"' and effective_id in (select appraisal_details_id from appraisal_details " +
					" where my_review_status = 1 and self_ids like '%,"+strSessionEmpId+",%' ) group by effective_id,is_approved");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproval = new HashMap<String, String>();			
			while(rs.next()){
				hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
					" and effective_type='"+WORK_FLOW_SELF_REVIEW+"' and effective_id in (select appraisal_details_id from appraisal_details " +
					" where my_review_status = 1 and self_ids like '%,"+strSessionEmpId+",%' ) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();	
			Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();
			while(rs.next()){
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
				" and effective_type='"+WORK_FLOW_SELF_REVIEW+"' and effective_id in (select appraisal_details_id from appraisal_details " +
					" where my_review_status = 1 and self_ids like '%,"+strSessionEmpId+",%' ) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();	
			while(rs.next()){
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_LOAN+"' order by effective_id,member_position");
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_SELF_REVIEW+"'" +
					" and effective_id in (select appraisal_details_id from appraisal_details " +
					" where my_review_status = 1 and self_ids like '%,"+strSessionEmpId+",%' ) group by effective_id,member_position");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	
			while(rs.next()){
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select ud.emp_id from user_details ud,employee_official_details eod,employee_personal_details epd where " +
					" ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'");
			pst.setInt(1, uF.parseToInt(locationID));
			rs = pst.executeQuery();			
			Map<String, String> hmEmpByLocation = new HashMap<String, String>();			
			while(rs.next()){
				hmEmpByLocation.put(rs.getString("emp_id"), rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
	
			
			sbQuery = new StringBuilder();
			sbQuery.append("select appraisal_details_id from appraisal_details where my_review_status = 1 and self_ids " +
				" like '%,"+strSessionEmpId+",%' ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			List<List<String>> alLoanReport = new ArrayList<List<String>>();
			int count=0;
			while(rs.next()) {
				
				List<String> checkEmpList = hmCheckEmp.get(rs.getString("loan_applied_id"));
				if(checkEmpList == null) checkEmpList = new ArrayList<String>();
				
				if(!checkEmpList.contains(strSessionEmpId) && !strSessionUserType.equalsIgnoreCase(ADMIN) && !strSessionUserType.equalsIgnoreCase(HRMANAGER)){ 
					continue;
				}
				
				if(strSessionUserType.equalsIgnoreCase(HRMANAGER) && hmEmpByLocation.get(rs.getString("emp_id"))==null){
					continue;
				}
				
				String strCurrency = "";
				if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp_id"))) > 0){
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp_id")));
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				} 
				
				List<String> alLoanInner = new ArrayList<String>();
				alLoanInner.add(rs.getString("loan_applied_id"));
				alLoanInner.add(uF.showData(hmEmpNamMap.get(rs.getString("emp_id")),""));
				alLoanInner.add(rs.getString("loan_code"));
				alLoanInner.add(uF.showData(uF.getDateFormat(rs.getString("applied_date"), DBDATE, CF.getStrReportDateFormat()), ""));
				alLoanInner.add(uF.showData(hmEmpNamMap.get(rs.getString("approved_by")),""));
				alLoanInner.add(uF.showData(uF.getDateFormat(rs.getString("approved_date"), DBDATE, CF.getStrReportDateFormat()), ""));
				alLoanInner.add(rs.getString("duration_months")+" months");
				alLoanInner.add(uF.showData(rs.getString("loan_acc_no"), ""));
				alLoanInner.add(strCurrency+ uF.formatIntoComma(uF.parseToDouble(rs.getString("amount_paid"))));
				
				
				double dblAmount = uF.getEMI(uF.parseToDouble(rs.getString("amount_paid")), uF.parseToDouble(rs.getString("loan_interest")), uF.parseToInt(rs.getString("duration_months")));
				
				if(rs.getString("balance_amount")!=null){
					alLoanInner.add(strCurrency+uF.formatIntoComma(uF.parseToDouble(rs.getString("balance_amount"))));
				}else{
					alLoanInner.add("-"); 
				}
				alLoanInner.add(strCurrency+uF.formatIntoTwoDecimal(dblAmount / uF.parseToInt(rs.getString("duration_months"))));
				
				if(deniedList.contains(rs.getString("loan_applied_id"))){
					alLoanInner.add("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");
				}else if(rs.getInt("is_approved")==1){							
					alLoanInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");
				}else if(uF.parseToInt(hmAnyOneApproval.get(rs.getString("loan_applied_id")))==1 && uF.parseToInt(hmAnyOneApproval.get(rs.getString("loan_applied_id")))==rs.getInt("is_approved")){
					alLoanInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");
				}else if(uF.parseToInt(hmNextApproval.get(rs.getString("loan_applied_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("loan_applied_id"))) && uF.parseToInt(hmNextApproval.get(rs.getString("loan_applied_id")))>0){
					alLoanInner.add("<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to approve this request?')) window.location='ApproveLoan.action?loanAppId="+rs.getString("loan_applied_id")+"&approvalStatus=1';\"><img title=\"Approved\" src=\"images1/icons/icons/approve_icon.png\" border=\"0\" /></a> " +
							" <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to denied this request?')) window.location='ApproveLoan.action?loanAppId="+rs.getString("loan_applied_id")+"&approvalStatus=-1';\"><img title=\"Denied\" src=\"images1/icons/icons/close_button_icon.png\" border=\"0\" style=\"width: 16px;\"/></a> ");
				}else if(uF.parseToInt(hmNextApproval.get(rs.getString("loan_applied_id")))>uF.parseToInt(hmMemNextApproval.get(rs.getString("loan_applied_id"))) || (uF.parseToInt(hmNextApproval.get(rs.getString("loan_applied_id")))==0 && uF.parseToInt(hmNextApproval.get(rs.getString("loan_applied_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("loan_applied_id"))))){
					
					if(!checkEmpList.contains(strSessionEmpId) && (strSessionUserType.equalsIgnoreCase(ADMIN) || strSessionUserType.equalsIgnoreCase(HRMANAGER))){ 
						if(rs.getInt("is_approved")==0){
							if(strSessionUserType.equalsIgnoreCase(ADMIN)){
								alLoanInner.add("<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to approve this request?')) window.location='ApproveLoan.action?loanAppId="+rs.getString("loan_applied_id")+"&approvalStatus=1';\"><img title=\"Approved\" src=\"images1/icons/icons/approve_icon.png\" border=\"0\" /></a> " +
										" <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to denied this request?')) window.location='ApproveLoan.action?loanAppId="+rs.getString("loan_applied_id")+"&approvalStatus=-1';\"><img title=\"Denied\" src=\"images1/icons/icons/close_button_icon.png\" border=\"0\" style=\"width: 16px;\"/></a> ");
							}else{
								alLoanInner.add("<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to approve this request?')) window.location='ApproveLoan.action?loanAppId="+rs.getString("loan_applied_id")+"&approvalStatus=1';\"><img title=\"Approved\" src=\"images1/icons/icons/approve_icon.png\" border=\"0\" /></a> " +
										" <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to denied this request?')) window.location='ApproveLoan.action?loanAppId="+rs.getString("loan_applied_id")+"&approvalStatus=-1';\"><img title=\"Denied\" src=\"images1/icons/icons/close_button_icon.png\" border=\"0\" style=\"width: 16px;\"/></a> ");
							}
						}else if(rs.getInt("is_approved")==1){							
							alLoanInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");
						}else{
							alLoanInner.add("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");
						}
						
					}else{
						alLoanInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");
					}
				}else{
					alLoanInner.add("<img title=\"Cancel Leave\" src=\"images1/icons/pullout.png\" border=\"0\" />");
				}
				
				
				if(rs.getInt("is_approved")==1 && !uF.parseToBoolean(rs.getString("is_paid")) && uF.parseToInt(rs.getString("approved_by"))>0 && strSessionUserType!=null && !strSessionUserType.equals(EMPLOYEE)  && !strSessionUserType.equals(CONSULTANT)  && !strSessionUserType.equals(ARTICLE)  && !strSessionUserType.equals(MANAGER)){ 
					alLoanInner.add("<a href=\"javascript:none(0)\" onclick=\"payLoan("+rs.getString("loan_applied_id")+")\">Pay</a> ");
				}else{

					if(rs.getInt("is_approved")==0){
						alLoanInner.add("");
					}else if(rs.getInt("is_approved")==1 && uF.parseToBoolean(rs.getString("is_completed"))){
						alLoanInner.add("<a href=\"javascript:none(0)\" onclick=\"viewLoanDetails("+rs.getString("loan_applied_id")+")\">View Details</a>");
					}else if(rs.getInt("is_approved")==1 && !uF.parseToBoolean(rs.getString("is_completed")) && strSessionUserType!=null && !strSessionUserType.equals(EMPLOYEE)  && !strSessionUserType.equals(CONSULTANT)  && !strSessionUserType.equals(ARTICLE)  && !strSessionUserType.equals(MANAGER)) {
						alLoanInner.add("<a href=\"javascript:none(0)\" onclick=\"viewLoanDetails("+rs.getString("loan_applied_id")+")\">View Details</a> | <a href=\"javascript:void();\" onclick=\"viewPayments("+rs.getString("emp_id")+","+rs.getString("loan_id")+","+rs.getString("loan_applied_id")+");\">Payments</a>");
					}else if(rs.getInt("is_approved")==1 && !uF.parseToBoolean(rs.getString("is_completed"))){
						alLoanInner.add("<a href=\"javascript:none(0)\" onclick=\"viewLoanDetails("+rs.getString("loan_applied_id")+")\">View Details</a>");
					}else{
						alLoanInner.add("");
					}
				}
				
				if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("loan_applied_id"))!=null){
					String approvedby=hmAnyOneApproeBy.get(rs.getString("loan_applied_id"));
					String strUserTypeName = uF.parseToInt(hmWorkFlowUserTypeId.get(rs.getString("loan_applied_id"))) > 0 ? " ("+uF.showData(hmUserTypeMap.get(hmWorkFlowUserTypeId.get(rs.getString("loan_applied_id"))), "")+")" : "";
					alLoanInner.add(hmEmpNamMap.get(approvedby)+strUserTypeName);
				} else{
					if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("loan_applied_id"))!=null){
						alLoanInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("loan_applied_id")+"','"+hmEmpNamMap.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
					} else{
						alLoanInner.add("");
					}
				}
				
				alLoanReport.add(alLoanInner);
				
				count++;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alLoanReport", alLoanReport);
			
		} catch (Exception e) {
			e.printStackTrace(); 
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}*/
	
	
	public void getAppraisalReport(UtilityFunctions uF) {
		
		List<List<String>> allAppraisalreport = new ArrayList<List<String>>();	
		Connection con=null;
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst=null;
		ResultSet rst=null;
	    try {	
	    	con=db.makeConnection(con);
	    	Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
	    	Map<String,String> orientationMp= CF.getOrientationValue(con);
	    	
	    //===start parvez date: 30-03-2023===	
	    	Map<String, String> orientationMemberMp = new HashMap<String, String>();
	    	pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
	    	rst = pst.executeQuery();
			while (rst.next()) {
				//orientationMemberMp.put(rs.getString("orientation_member_id"), rs.getString("member_name"));
				orientationMemberMp.put(rst.getString("member_id"), rst.getString("member_name"));
			}
			rst.close();
			pst.close();
		//===end parvez date: 30-03-2023===	
	    	
	    	Map<String, String> hmFrequency = new HashMap<String, String>();
//	    	Map<String, String> hmAppraisalCount = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rst = pst.executeQuery();
			while (rst.next()) {
				hmFrequency.put(rst.getString("appraisal_frequency_id"), rst.getString("frequency_name"));
			}
			rst.close();
			pst.close();
			
			StringBuilder sbAppId = null;
			StringBuilder sbAppFreqId = null;
			
			pst = con.prepareStatement("select appraisal_id,appraisal_freq_id from appraisal_final_sattlement where emp_id = ? ");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			
			rst = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while (rst.next()) {
				if(sbAppId == null) {
					sbAppId = new StringBuilder();
					sbAppId.append(rst.getString("appraisal_id"));
				} else {
					sbAppId.append(","+rst.getString("appraisal_id"));
				}
				
				if(sbAppFreqId == null) {
					sbAppFreqId = new StringBuilder();
					sbAppFreqId.append(rst.getString("appraisal_freq_id"));
				} else {
					sbAppFreqId.append(","+rst.getString("appraisal_freq_id"));
				}
				
			}
			rst.close();
			pst.close();
			
			
			if(sbAppId != null && !sbAppId.toString().equals("") && sbAppFreqId!= null && !sbAppFreqId.equals("")) {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from appraisal_details a, appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id and "
					+" (adf.is_delete is null or adf.is_delete = false ) and my_review_status = 0 and appraisal_details_id in("+sbAppId.toString()+") and appraisal_freq_id in ("+sbAppFreqId.toString()+") ");
			if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and is_appraisal_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and is_appraisal_close = true ");
			}
			sbQuery.append(" order by is_appraisal_close, is_appraisal_publish desc, freq_publish_expire_status desc, freq_end_date");
			pst=con.prepareStatement(sbQuery.toString());
			rst=pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			int count=0;
			StringBuilder sbIcons = new StringBuilder();
			while(rst.next()) {
				count++;
				sbIcons.replace(0, sbIcons.length(), "");
				
				List<String> appraisal_info =new ArrayList<String>(); 
				appraisal_info.add(rst.getString("appraisal_details_id"));//0
				//System.out.println("empList ===> "+empList );
				
				
				if(uF.parseToBoolean(rst.getString("is_appraisal_close"))) {
					appraisal_info.add("<a href=\"javascript:void(0);\" onclick=\"openAppraisalPreview('"+rst.getString("appraisal_details_id")+"','"+rst.getString("appraisal_freq_id")+"')\" > <i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i> </a>");//1
					
				} else if(rst.getInt("freq_publish_expire_status") == 1) {
					appraisal_info.add("<img src=\"images1/icons/hd_tick_20x20.png\" /><a href=\"javascript: void(0)\" onclick=\"openAppraisalPreview('"+rst.getString("appraisal_details_id")+"','"+rst.getString("appraisal_freq_id")+"')\">"+rst.getString("appraisal_name")+"</a>");//1
				} else  if(uF.parseToBoolean(rst.getString("is_appraisal_publish"))) {
					appraisal_info.add("<a href=\"javascript:void(0);\" onclick=\"openAppraisalPreview('"+rst.getString("appraisal_details_id")+"','"+rst.getString("appraisal_freq_id")+"')\" > <i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i> </a>");//1
					
				} else {
					appraisal_info.add("<a href=\"javascript:void(0);\" onclick=\"openAppraisalPreview('"+rst.getString("appraisal_details_id")+"','"+rst.getString("appraisal_freq_id")+"')\" > <i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\"></i></a>");
				}
				
				appraisal_info.add(rst.getString("appraisal_type"));//2
				appraisal_info.add(orientationMp.get(rst.getString("oriented_type"))+"&deg;");//3
				appraisal_info.add(uF.showData(hmFrequency.get(rst.getString("frequency")), ""));//4
				appraisal_info.add(uF.getDateFormat(rst.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));//5
				if(uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_REVIEW_BALANCE_SCORE_IN_MYSELF))) {
					sbIcons.append("<a href=\"AppraisalStatus.action?id="+rst.getString("appraisal_details_id")+"&type=KRATARGET&appFreqId="+rst.getString("appraisal_freq_id")+"\"><img src=\"images1/icons/icons/status_icon.png\" title=\"Score Card\" /></a>");
				}
			//===start parvez date: 30-03-2023===	
				if(uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
					List<String> alMemberList1 = new ArrayList<String>();
					if (rst.getString("usertype_member") != null && !rst.getString("usertype_member").equals("")) {
						alMemberList1 = Arrays.asList(rst.getString("usertype_member").split(","));
						if(alMemberList1!=null && alMemberList1.contains("2")){
							sbIcons.append("<a href=\"javascript:void(0);\" onclick=\"getMemberData('2','"+strSessionEmpId+"','"+rst.getString("appraisal_details_id")+"','"+orientationMemberMp.get("2")+"','"+rst.getString("appraisal_freq_id")+"');\"><img src=\"images1/icons/icons/status_icon.png\" title=\"Manager Score Card\" /></a>");
						}
					}
					
				}
			//===end parvez date: 30-03-2023===	
				if(uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_REVIEW_FEEDBACK_IN_PDF_FORMAT))) {
					sbIcons.append("<a href=\"GenerateReviewPdfReport.action?appId="+rst.getString("appraisal_details_id")+"&appFreqId="+rst.getString("appraisal_freq_id")+"&strEmpId="+strSessionEmpId+"\" class=\"fa fa-file-pdf-o\"></a>");
				}
//		
				appraisal_info.add(sbIcons.toString());//6
				appraisal_info.add(rst.getString("appraisal_freq_id"));//7
				appraisal_info.add(uF.getDateFormat(rst.getString("freq_end_date"), DBDATE, CF.getStrReportDateFormat()));//8
				allAppraisalreport.add(appraisal_info);
			}
			rst.close();
			pst.close();
	
	    }
	    
		} catch (Exception e){
				e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("allAppraisalreport", allAppraisalreport);
	}

	
	private void unPublishOldReview() {
		Connection con=null;
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst=null;
		UtilityFunctions uF = new UtilityFunctions();
	    try {	
	    	con=db.makeConnection(con);
			pst = con.prepareStatement("update appraisal_details set is_publish = FALSE, publish_expire_status=1 where to_date < ? and " +
					"is_publish = TRUE and my_review_status = 1");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst ===== >>> " +pst);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("update appraisal_details_frequency set is_appraisal_publish = FALSE, freq_publish_expire_status=1 where appraisal_due_date < ? and " +
			"is_appraisal_publish = TRUE ");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst ===== >>> " +pst);
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e){
				e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void updateUserAlerts(String alertType) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
		try {
			con = db.makeConnection(con);
			String strDomain = request.getServerName().split("\\.")[0];
			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
			userAlerts.setStrDomain(strDomain);
			userAlerts.setStrEmpId(""+nEmpId);
			userAlerts.set_type(alertType);
			userAlerts.setStatus(UPDATE_ALERT);
			Thread t = new Thread(userAlerts);
			t.run();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getEmpKRATargetDetails(UtilityFunctions uF) {
	
		GoalKRA kra = new GoalKRA();
		kra.session = session;
		kra.CF = CF;
		kra.setServletRequest(request);
		kra.setDataType(getDataType());
//		kra.execute();
		kra.strSessionEmpId = strSessionEmpId;
		kra.strEmpOrgId = strEmpOrgId;
		kra.strUserType = strSessionUserType;
//		List<String> empList1 = kra.getEmployeeList(uF);
		List<String> empList1 = new ArrayList<String>();
		empList1.add(strSessionEmpId);
		kra.getEmpGoalKRADetails(uF,empList1);
		kra.getEmpKRADetails(uF,empList1);
		kra.getKRARatingAndCompletionStatus(uF, empList1);
		kra.getEmpTargetDetails(uF, empList1);
		kra.checkGoalKRATargetStatus(uF);
		
		
//		MyGoal myGoal = new MyGoal();
//		myGoal.session = session;
//		myGoal.CF = CF;
//		myGoal.setServletRequest(request);
//		myGoal.setDataType(getDataType());
////		myGoal.execute();
//		myGoal.strSessionEmpId = strSessionEmpId;
//		myGoal.strEmpOrgId = strEmpOrgId;
//		myGoal.getIndividualDetails();
//		myGoal.getGoalTypeDetails();
//		myGoal.getTargetDetails();
//		myGoal.getGoalKRADetails();
//		myGoal.getPersonalGoalAndTarget();
//		myGoal.getGoalRating();
//		myGoal.getKRARating();
//		myGoal.getTeamGoalAverageDetails();
//		myGoal.getManagerGoalAverageDetails();
		
		
//		TeamGoal teamGoal = new TeamGoal();
//		teamGoal.session = session;
//		teamGoal.CF = CF;
//		teamGoal.setServletRequest(request);
//		teamGoal.setDataType(getDataType());
////		teamGoal.execute();
//		teamGoal.strSessionEmpId = strSessionEmpId;
//		teamGoal.strEmpOrgId = strEmpOrgId;
//		teamGoal.strSessionUserType = strSessionUserType;
//		teamGoal.getTeamDetails();  
		
		
//		ManagerGoal managerGoal= new ManagerGoal();
//		managerGoal.session=session;
//		managerGoal.CF=CF;
//		managerGoal.setServletRequest(request);
//		managerGoal.setDataType(getDataType());
////		managerGoal.execute();
//		managerGoal.strSessionEmpId = strSessionEmpId;
//		managerGoal.strEmpOrgId = strEmpOrgId;
//		managerGoal.getGoalTypeDetails(uF);
//		managerGoal.getGoalSummary(uF);
		
	}

	
	
	private void getEmpImage(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			CF.getEmpUserTypeId(con,request, strSessionEmpId);
			
			String []arrEnabledModules = CF.getArrEnabledModules();
			
			EmpDashboardData dashboardData = new EmpDashboardData(request, session, CF, uF, con, strSessionEmpId);
			dashboardData.viewProfile(strSessionEmpId);
			dashboardData.getClockEntries();
			dashboardData.getEmpSkills(uF);
		
			if(arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, MODULE_PERFORMANCE_MANAGEMENT+"")>=0){
				CF.getElementList(con, request);
				CF.getAttributes(con, request, strSessionEmpId);
				dashboardData.getMyTeam();
				dashboardData.getMyTeamRating();
				dashboardData.getMyKRA();
				dashboardData.getMyGoalAchieve();
				dashboardData.getMyTeamGoalAchieve();
				dashboardData.getMyManagerGoalAchieve();
				dashboardData.getMyTargetAchieve();
//				dashboardData.getMyTeamTargetAchieve(); 
				
			}
			
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			request.setAttribute("hmEmpName", hmEmpName);
			pst=con.prepareStatement("select emp_image,emp_per_id from employee_personal_details ");
			rs=pst.executeQuery();
			Map<String,String> empImageMap=new HashMap<String,String>();
			while(rs.next()){
				empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
			}
			rs.close();
			pst.close();
			request.setAttribute("empImageMap", empImageMap);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	
private void getEmpScoreAvgAppraisalWise(Connection con, UtilityFunctions uF) {
		
//		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
//		UtilityFunctions uF = new UtilityFunctions();
		try {
//			Map<String, String> empLevelMap = CF.getEmpLevelMap(con);
//			String empLevel = empLevelMap.get((String) session.getAttribute("EMPID"));
			
			Map<String, String> hmEmpAggregateMap = new HashMap<String, String>();
			pst = con.prepareStatement(" select *,((marks*100/weightage)/20) as average from (select sum(marks) as marks, sum(weightage) as weightage," +
				"appraisal_attribute,emp_id from appraisal_question_answer aqw where weightage>0 and emp_id=? group by appraisal_attribute,emp_id) as a"); 
			//and aqw.appraisal_attribute = ?  where user_id=? and user_type_id=? 
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {
//				System.out.println("key==>"+rs.getString("appraisal_id")+"_"+rs.getString("emp_id")+"===>value==>"+rs.getString("average"));
				hmEmpAggregateMap.put(rs.getString("appraisal_attribute"), uF.showData(uF.getRoundOffValue(2,uF.parseToDouble(rs.getString("average"))), "0"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmpAggregateMap", hmEmpAggregateMap);
			
			Map<String, String> hmScoreAggregateMap = new HashMap<String, String>();
			Map<String, String> hmUserTypeID = CF.getUserTypeIdMap(con);
			pst = con.prepareStatement(" select *,(marks*100/weightage) as average from (select sum(marks) as marks, sum(weightage) as weightage," +
				"appraisal_id,emp_id from appraisal_question_answer aqw where weightage>0 group by appraisal_id,emp_id) as a"); 
			//and aqw.appraisal_attribute = ?  where user_id=? and user_type_id=? 
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//			pst.setInt(2, uF.parseToInt(hmUserTypeID.get(EMPLOYEE)));
//			System.out.println("pst =====> "+ pst);
			rs = pst.executeQuery();
			while (rs.next()) {
//				System.out.println("key==>"+rs.getString("appraisal_id")+"_"+rs.getString("emp_id")+"===>value==>"+rs.getString("average"));
				hmScoreAggregateMap.put(rs.getString("appraisal_id")+"_"+rs.getString("emp_id"), uF.showData(uF.getRoundOffValue(2,uF.parseToDouble(rs.getString("average"))), "0"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmScoreAggregateMap =====> "+ hmScoreAggregateMap);
			request.setAttribute("hmScoreAggregateMap", hmScoreAggregateMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	
	private void getAppraisalDetails() {
		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String oriented_type=null;
		try {
			con = db.makeConnection(con);
			getEmpScoreAvgAppraisalWise(con, uF);
			Map<String, String> hmDesignation = CF.getDesigMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmLevelMap = getLevelMap(con);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			Map<String, String> orientationMp = getOrientationValue(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
			
			List<String> appraisalFinalList = CF.getAppraisalFinalStatus(con);
			List<String> appQueAnsStatusList = CF.getAppraisalQueAnsStatus(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			
			if(appraisalFinalList==null) appraisalFinalList = new ArrayList<String>();
			if(appQueAnsStatusList==null) appQueAnsStatusList = new ArrayList<String>();
			
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			Map<String, String> hmFreq = new HashMap<String, String>();
			while (rs.next()) {
				hmFreq.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("getDataType() ===>> " + getDataType());
			StringBuilder sbquery = new StringBuilder();
			sbquery.append("select * from appraisal_details a,appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id and "
					+" (adf.is_delete is null or adf.is_delete = false) and (is_appraisal_publish=true or (is_appraisal_publish = false and my_review_status = 1)) ");
			if(getDataType() != null && getDataType().equals("L")) {
				sbquery.append(" and is_appraisal_close = false ");
			}
			if(getDataType() != null && getDataType().equals("C")) {
				sbquery.append(" and is_appraisal_close = true ");
			}
			sbquery.append(" order by freq_end_date DESC");
			pst = con.prepareStatement(sbquery.toString());
			rs = pst.executeQuery();
			// String appraisalee = null;

			// StringBuilder sbSelfEmp = new StringBuilder();
			Map<String, Map<String, String>> appraisalDetails = new HashMap<String, Map<String, String>>();
			List<String> appraisalIdList = new ArrayList<String>();

			while (rs.next()) {
				Map<String, String> appraisalMp = new HashMap<String, String>();
				appraisalIdList.add(rs.getString("appraisal_details_id")+"_"+rs.getString("appraisal_freq_id"));
				appraisalMp.put("ID", rs.getString("appraisal_details_id"));
				appraisalMp.put("APPRAISAL", rs.getString("appraisal_name"));
				String appFreqName = " ("+rs.getString("appraisal_freq_name")+")";
				appraisalMp.put("APP_FREQ_NAME", appFreqName);//2				
				appraisalMp.put("ORIENT", orientationMp.get(rs.getString("oriented_type")));
				appraisalMp.put("EMPLOYEE", uF.showData(getAppendData(rs.getString("self_ids"), hmEmpName), ""));
				appraisalMp.put("EMPLOYEE_IDS", rs.getString("employee_id"));
				appraisalMp.put("LEVEL", uF.showData(hmLevelMap.get(rs.getString("level_id")), ""));
				appraisalMp.put("DESIG", hmDesignation.get(rs.getString("desig_id")));
				appraisalMp.put("GRADE", hmGradeMap.get(rs.getString("grade_id")));
				appraisalMp.put("WLOCATION", rs.getString("wlocation_id"));
				appraisalMp.put("PEER", rs.getString("peer_ids"));
				appraisalMp.put("ANYONE", rs.getString("other_ids"));
				appraisalMp.put("SELFID", rs.getString("self_ids"));
				appraisalMp.put("SUPERVISORID", rs.getString("supervisor_id"));
				appraisalMp.put("HRID", rs.getString("hr_ids"));
				appraisalMp.put("FREQUENCY", hmFreq.get(rs.getString("frequency").trim()));
				appraisalMp.put("FROM", uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalMp.put("TO", uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalMp.put("REVIEW_TYPE", rs.getString("appraisal_type"));
				appraisalMp.put("MY_REVIEW_STATUS", rs.getString("my_review_status"));
				appraisalMp.put("IS_PUBLISH", rs.getString("is_publish"));
				appraisalMp.put("ORIENTED_TYPE", rs.getString("oriented_type"));
				appraisalMp.put("IS_CLOSE", rs.getString("is_close"));
				appraisalMp.put("CEOID", rs.getString("ceo_ids"));
				appraisalMp.put("HODID", rs.getString("hod_ids"));
				appraisalMp.put("APP_FREQ_ID", rs.getString("appraisal_freq_id"));
				appraisalMp.put("FREQ_START_DATE", rs.getString("freq_start_date"));
				appraisalMp.put("FREQ_END_DATE", rs.getString("freq_end_date"));
				appraisalMp.put("FREQ_IS_PUBLISH", rs.getString("is_appraisal_publish"));
				appraisalMp.put("FREQ_IS_CLOSE", rs.getString("is_appraisal_close"));
				appraisalMp.put("FREQ_PUBLISH_EXPIRE_STATUS", rs.getString("freq_publish_expire_status"));
				appraisalMp.put("FREQ_IS_CLOSE_REASON", rs.getString("close_reason"));
				appraisalMp.put("APP_FREQ_NAME", rs.getString("appraisal_freq_name"));
				
				boolean flagFinal = false;
				if(appraisalFinalList != null && appraisalFinalList.contains(rs.getString("appraisal_details_id"))) {
					flagFinal = true;
				}
				
				boolean flagQA = false;
				if(appQueAnsStatusList != null && appQueAnsStatusList.contains(rs.getString("appraisal_details_id")+"_"+rs.getString("appraisal_freq_id"))) {
					flagQA = true;
				}
				
				appraisalMp.put("APP_FINAL_STATUS",flagFinal+"");//33
				appraisalMp.put("APP_STATUS",flagQA+"");
				appraisalDetails.put(rs.getString("appraisal_details_id")+"_"+rs.getString("appraisal_freq_id"), appraisalMp);
				oriented_type=rs.getString("oriented_type");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("appraisalIdList", appraisalIdList);
			request.setAttribute("appraisalDetails", appraisalDetails);

			Map<String, Map<String, Map<String, String>>> appraisalStatusMp = getEmployeeStatus(uF);
			request.setAttribute("appraisalStatusMp", appraisalStatusMp);
			getNextStepUserFillFeedback(uF);
			
			Map<String, Map<String, List<String>>> empMpDetails = new HashMap<String, Map<String, List<String>>>();
			Map<String, Map<String, List<String>>> hmReviewedEmpDetails = new HashMap<String, Map<String, List<String>>>();
			Map<String, Map<String, List<String>>> hmRemainOrientDetailsAppWise = new LinkedHashMap<String, Map<String,List<String>>>();
//			Map<String, Map<String, List<String>>> hmRemainOrientDetailsForSelfAppWise = new LinkedHashMap<String, Map<String,List<String>>>();
//			Map<String, Map<String, List<String>>> hmRemainOrientDetailsForPeerAppWise = new LinkedHashMap<String, Map<String,List<String>>>();
			Map<String, Map<String,List<String>>> hmExistOrientTypeAQAAppWise = new HashMap<String, Map<String,List<String>>>();

			Map<String, List<String>> hmEmpKRAIds = (Map<String, List<String>>) request.getAttribute("hmEmpKRAIds");
			Map<String, List<String>> hmEmpGoarTargetIds = (Map<String, List<String>>) request.getAttribute("hmEmpGoarTargetIds");
			
			for (int i = 0; i < appraisalIdList.size(); i++) {
				Map<String, String> appraisalMp = appraisalDetails.get(appraisalIdList.get(i));
				Map<String, List<String>> empMp = new HashMap<String, List<String>>();
				Map<String, List<String>> hmReviewedEmp = new HashMap<String, List<String>>();
				List<String> hmOrientationMemberIds = CF.getOrientationMemberDetails(con, uF.parseToInt(appraisalMp.get("ORIENTED_TYPE")));
//				System.out.println("KRAT/1177---appraisalIdList.get(i)==="+appraisalIdList.get(i)+"---hmOrientationMemberIds=="+hmOrientationMemberIds);
//				System.out.println("hmOrientMemberID=="+hmOrientMemberID);
				Map<String, Map<String, String>> userTypeMp = appraisalStatusMp.get(appraisalIdList.get(i));
				if(userTypeMp==null)userTypeMp=new HashMap<String, Map<String, String>>();
				
				Map<String, List<String>> hmExistSectionID = new HashMap<String, List<String>>(); 
				pst = con.prepareStatement("select distinct (section_id),emp_id,appraisal_id,appraisal_freq_id,user_type_id from appraisal_question_answer " +
						"where is_submit=true and user_id = ? group by section_id,emp_id,appraisal_id,user_id,user_type_id,appraisal_freq_id"); // and user_type_id = ?
//				pst.setInt(1, uF.parseToInt(appraisalIdList.get(i)));
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
//				pst.setInt(2, uF.parseToInt(strUserTypeId));
				rs = pst.executeQuery();
//				System.out.println("pst ::: "+pst);
				while (rs.next()) {
					List<String> sectionIDList = hmExistSectionID.get(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("user_type_id")+"_"+rs.getString("emp_id"));
					if(sectionIDList==null) sectionIDList = new ArrayList<String>();				
					sectionIDList.add(rs.getString("section_id"));
					hmExistSectionID.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("user_type_id")+"_"+rs.getString("emp_id"), sectionIDList);
				}
				rs.close();
				pst.close();
				
				request.setAttribute("hmExistSectionID", hmExistSectionID);
			
			//===start parvez date: 16-03-2023===	
				pst = con.prepareStatement("select * from appraisal_reviewee_details where appraisal_id =? and (peer_ids like '%,"+strSessionEmpId+",%' or other_peer_ids like '%,"+strSessionEmpId+",%')");
				pst.setInt(1, uF.parseToInt(appraisalMp.get("ID")));
				List<String> tempPeerIds = new ArrayList<String>();
				List<String> tempOtherPeerIds = new ArrayList<String>();
//				System.out.println("KRAT/1211---pst==>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					String[] strPeers = null;
					String[] strOtherPeers = null;
					if(rs.getString("peer_ids")!=null){
						strPeers = rs.getString("peer_ids").split(",");
					}
					if(rs.getString("other_peer_ids")!=null){
						strOtherPeers = rs.getString("other_peer_ids").split(",");
					}
					for(int k=0; strPeers!=null && k<strPeers.length; k++){
						tempPeerIds.add(strPeers[k]);
					}
					for(int k1=0; strOtherPeers!=null && k1<strOtherPeers.length; k1++){
						tempOtherPeerIds.add(strOtherPeers[k1]);
					}
				}
				rs.close();
				pst.close();
			//===end parvez date: 16-03-2023===	
				int sectionCount = 0;
//				pst = con.prepareStatement("select count(main_level_id) as sectionCnt from appraisal_main_level_details where appraisal_id =?");
			//===start parvez date: 16-03-2023===	
				
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) && 
						((tempPeerIds!=null && tempPeerIds.contains(strSessionEmpId)) || (tempOtherPeerIds!=null && tempOtherPeerIds.contains(strSessionEmpId)))){
					pst = con.prepareStatement("select count(main_level_id) as sectionCnt from appraisal_main_level_details where appraisal_id =? " +
							"and main_level_id not in (select main_level_id from appraisal_level_details where appraisal_id=? and appraisal_system!=2)");
					pst.setInt(2, uF.parseToInt(appraisalMp.get("ID")));
				} else{
					pst = con.prepareStatement("select count(main_level_id) as sectionCnt from appraisal_main_level_details where appraisal_id =?");
				}
			//===end parvez date: 16-03-2023===	
				pst.setInt(1, uF.parseToInt(appraisalMp.get("ID")));
				rs = pst.executeQuery();
//				System.out.println("KRAT.jsp/1245---pst==>"+pst);
				while (rs.next()) {
					sectionCount = rs.getInt("sectionCnt");
				}
				rs.close();
				pst.close();
//				System.out.println("KRAT/1209---sectionCount="+sectionCount);
				
//				List<String> alKRAIds = (List<String>)request.getAttribute("alKRAIds");
//				List<String> alGoarTargetIds = (List<String>)request.getAttribute("alGoarTargetIds");
				
				Map<String, String> hmQueCount = new HashMap<String, String>();
//				pst = con.prepareStatement("select count(appraisal_question_details_id) as queCnt, appraisal_id from appraisal_question_details group by appraisal_id");
			//===start parvez date: 16-03-2023===	
				/*pst = con.prepareStatement("select appraisal_question_details_id, aqd.appraisal_id,reviewee_id,app_system_type,kra_id,goal_kra_target_id" +
					" from appraisal_question_details aqd, appraisal_reviewee_details ard where aqd.appraisal_id=ard.appraisal_id");*/
				pst = con.prepareStatement("select appraisal_question_details_id, aqd.appraisal_id,reviewee_id,app_system_type,kra_id,goal_kra_target_id,peer_ids,other_peer_ids" +
							" from appraisal_question_details aqd, appraisal_reviewee_details ard where aqd.appraisal_id=ard.appraisal_id");
			//===end parvez date: 16-03-2023===	
				rs = pst.executeQuery();
				while (rs.next()) {
				//===start parvez date: 16-03-2023===	
					List<String> tempPeerIdsList = new ArrayList<String>();
					List<String> tempOtherPeerIdsList = new ArrayList<String>();
					if(rs.getString("peer_ids")!=null){
						tempPeerIdsList = Arrays.asList(rs.getString("peer_ids").split(","));
					}
					if(rs.getString("other_peer_ids")!=null){
						tempOtherPeerIdsList = Arrays.asList(rs.getString("other_peer_ids").split(","));
					}
				//===end parvez date: 16-03-2023===	
					List<String> alKRAIds = hmEmpKRAIds.get(rs.getString("reviewee_id"));
					if(rs.getInt("app_system_type")==4 && alKRAIds!=null && rs.getString("kra_id")!=null && !alKRAIds.contains(rs.getString("kra_id"))) {
						continue;
					}
					List<String> alGoarTargetIds = hmEmpGoarTargetIds.get(rs.getString("reviewee_id"));
					if((rs.getInt("app_system_type")==3 || rs.getInt("app_system_type")==5) && alGoarTargetIds!=null && rs.getString("goal_kra_target_id")!=null && !alGoarTargetIds.contains(rs.getString("goal_kra_target_id"))) {
						continue;
					}
					
				//===start parvez date: 16-03-2023===	
					if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) && rs.getInt("app_system_type")!=0 && rs.getInt("app_system_type")!=2
							 && ((tempPeerIdsList!=null && tempPeerIdsList.contains(strSessionEmpId)) || (tempOtherPeerIdsList!=null && tempOtherPeerIdsList.contains(strSessionEmpId)))){
						continue;
					}
				//===end parvez date: 16-03-2023===	
					
					int intCnt = uF.parseToInt(hmQueCount.get(rs.getString("appraisal_id")+"_"+rs.getString("reviewee_id")));
					intCnt++;
					hmQueCount.put(rs.getString("appraisal_id")+"_"+rs.getString("reviewee_id"), intCnt+"");
				}
				rs.close();
				pst.close();
				
				/*int queCount = 0;
//				pst = con.prepareStatement("select count(appraisal_question_details_id) as queCnt from appraisal_question_details where appraisal_id =?");
				pst = con.prepareStatement("select * from appraisal_question_details where appraisal_id =?");
				pst.setInt(1, uF.parseToInt(appraisalMp.get("ID")));
				rs = pst.executeQuery();
				while (rs.next()) {
					if(rs.getInt("app_system_type")==4 && alKRAIds!=null && rs.getString("kra_id")!=null && !alKRAIds.contains(rs.getString("kra_id"))) {
						continue;
					}
					if((rs.getInt("app_system_type")==3 || rs.getInt("app_system_type")==5) && alGoarTargetIds!=null && rs.getString("goal_kra_target_id")!=null && !alGoarTargetIds.contains(rs.getString("goal_kra_target_id"))) {
						continue;
					}
					queCount++;
				}
				rs.close();
				pst.close();*/
				
				Map<String, Integer> hmExistSectionCount = new HashMap<String, Integer>(); 
				pst = con.prepareStatement("select count(distinct section_id) as existSectionCnt,emp_id,appraisal_id,appraisal_freq_id,user_type_id from appraisal_question_answer " +
						"where appraisal_id =? and user_id =? group by emp_id,appraisal_id,appraisal_freq_id,user_type_id"); //and user_type_id = ?
				pst.setInt(1, uF.parseToInt(appraisalMp.get("ID")));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
//				pst.setInt(3, uF.parseToInt(strUserTypeId));
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					hmExistSectionCount.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("user_type_id")+"_"+rs.getString("emp_id"), rs.getInt("existSectionCnt"));
				}
				rs.close();
				pst.close();
				
				
				Map<String, Integer> hmExistQueCount = new HashMap<String, Integer>(); 
				pst = con.prepareStatement("select count(distinct appraisal_question_details_id) as existQueCnt,emp_id,appraisal_id,appraisal_freq_id,user_type_id from appraisal_question_answer " +
					"where is_submit=true and appraisal_id=? and user_id=? and section_comment is not null and section_comment != '' group by emp_id,appraisal_id,appraisal_freq_id,user_type_id"); //and user_type_id = ?
				pst.setInt(1, uF.parseToInt(appraisalMp.get("ID")));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
//				pst.setInt(3, uF.parseToInt(strUserTypeId));
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					hmExistQueCount.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("user_type_id")+"_"+rs.getString("emp_id"), rs.getInt("existQueCnt"));
				}
				rs.close();
				pst.close();
				
				
				Map<String, List<String>> hmRevieweeAndUsersIds = new HashMap<String, List<String>>(); 
				pst = con.prepareStatement("select * from appraisal_reviewee_details where appraisal_id=? and (subordinate_ids like '%,"+strSessionEmpId+",%' " +
					"or peer_ids like '%,"+strSessionEmpId+",%' or other_peer_ids like '%,"+strSessionEmpId+",%' or other_ids like '%,"+strSessionEmpId+",%' or reviewee_id = "+strSessionEmpId+") ");
				pst.setInt(1, uF.parseToInt(appraisalMp.get("ID")));
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("subordinate_ids"));
					innerList.add(rs.getString("peer_ids"));
					innerList.add(rs.getString("other_peer_ids"));
					innerList.add(rs.getString("other_ids"));
					innerList.add(rs.getString("reviewee_id"));
					hmRevieweeAndUsersIds.put(rs.getString("reviewee_id"), innerList);
				}
				rs.close();
				pst.close();
//				System.out.println("hmRevieweeAndUsersIds ===>> " + hmRevieweeAndUsersIds);
				Map<String, List<String>> hmRemainOrientDetails = new HashMap<String, List<String>>() ;
				List<String> employeeList = new ArrayList<String>();
				List<String> reviewedEmpList = new ArrayList<String>();
				Iterator<String> it = hmRevieweeAndUsersIds.keySet().iterator();
				while (it.hasNext()) {
					String StrRevieweeId = it.next();
					List<String> innerList = hmRevieweeAndUsersIds.get(StrRevieweeId);
					
					List<String> subordinateIdsList = new ArrayList<String>();
					List<String> peerIdsList = new ArrayList<String>();
					List<String> otherPeerIdsList = new ArrayList<String>();
					List<String> otherIdsList = new ArrayList<String>();
					
					if(innerList.get(0) != null) {
						subordinateIdsList = Arrays.asList(innerList.get(0).split(","));
					}
					if(innerList.get(1) != null) {
						peerIdsList = Arrays.asList(innerList.get(1).split(","));
					}
					if(innerList.get(2) != null) {
						otherPeerIdsList = Arrays.asList(innerList.get(2).split(","));
					}
					if(innerList.get(3) != null) {
						otherIdsList = Arrays.asList(innerList.get(3).split(","));
					}
					
//					System.out.println("strSessionUserType ===>> " + strSessionUserType +" --- StrRevieweeId ===>> " + StrRevieweeId);
					
					if(strSessionUserType != null && strSessionUserType.equalsIgnoreCase(EMPLOYEE)) {
						if(subordinateIdsList != null && subordinateIdsList.size() > 0) {
//							System.out.println("subordinateIdsList ===>> " + subordinateIdsList);
							if(subordinateIdsList != null && subordinateIdsList.contains(strSessionEmpId)) {
//								System.out.println("subordinateIdsList 1 ===>> " + subordinateIdsList);
								Map<String, String> empstatusMp = userTypeMp.get(hmOrientMemberID.get("Sub-ordinate"));
								if(empstatusMp==null)empstatusMp = new HashMap<String, String>();
//								System.out.println("empstatusMp ===>> " + empstatusMp);
								employeeList = empMp.get(hmOrientMemberID.get("Sub-ordinate"));
								if(employeeList == null) employeeList = new ArrayList<String>();
								reviewedEmpList = hmReviewedEmp.get(hmOrientMemberID.get("Sub-ordinate"));
								if(reviewedEmpList == null) reviewedEmpList = new ArrayList<String>();
								
//								System.out.println("hmExistSectionCount ===>> " + hmExistSectionCount);
								if (empstatusMp!=null && empstatusMp.get(StrRevieweeId+"_"+strSessionEmpId) == null) {
//									System.out.println("===>> in 0");
									employeeList.add(StrRevieweeId);
								} else if(hmExistSectionCount != null && !hmExistSectionCount.isEmpty()) {
//									System.out.println("in 1");
									if(hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Sub-ordinate")+"_"+StrRevieweeId) != null && sectionCount != hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Sub-ordinate")+"_"+StrRevieweeId)) {
										employeeList.add(StrRevieweeId);
									} else if(hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Sub-ordinate")+"_"+StrRevieweeId) == null || (uF.parseToInt(hmQueCount.get(appraisalMp.get("ID")+"_"+StrRevieweeId)) != hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Sub-ordinate")+"_"+StrRevieweeId))) {
										employeeList.add(StrRevieweeId);
									} else {
//										System.out.println("in 1 else ");
										reviewedEmpList.add(StrRevieweeId);
									}
								}
			//-----------------------------------------------------------------------------------
								getSecondMaxOrientNameAndIDs(con, 5, appraisalMp.get("ID"), StrRevieweeId, uF, hmRemainOrientDetails);
								getExistOrientTypeInAQA(appraisalMp.get("ID"),appraisalMp.get("APP_FREQ_ID"), uF);
								empMp.put(hmOrientMemberID.get("Sub-ordinate"), employeeList);
								hmReviewedEmp.put(hmOrientMemberID.get("Sub-ordinate"), reviewedEmpList);
							}
						}
//						System.out.println("hmReviewedEmp ===>> " + hmReviewedEmp);
						if(peerIdsList != null && peerIdsList.size() > 0) {
							if(peerIdsList != null && peerIdsList.contains(strSessionEmpId)) {
								Map<String, String> empstatusMp = userTypeMp.get(hmOrientMemberID.get("Peer"));
								if(empstatusMp==null)empstatusMp = new HashMap<String, String>();
								employeeList = empMp.get(hmOrientMemberID.get("Peer"));
								if(employeeList == null) employeeList = new ArrayList<String>();
								reviewedEmpList = hmReviewedEmp.get(hmOrientMemberID.get("Peer"));
								if(reviewedEmpList == null) reviewedEmpList = new ArrayList<String>();
//								System.out.println("appraisalId - Peer - StrRevieweeId ===>> " + appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Peer")+"_"+StrRevieweeId);
//								System.out.println("empstatusMp ===>> " + empstatusMp);
//								System.out.println("sectionCount ===>> " + sectionCount+ " -- hmExistSectionCount ===>> " + hmExistSectionCount);
//								System.out.println("hmQueCount ===>> " + hmQueCount);
//								System.out.println(" -- hmExistQueCount ===>> " + hmExistQueCount);
								if (empstatusMp!=null && empstatusMp.get(StrRevieweeId+"_"+strSessionEmpId) == null) {
									employeeList.add(StrRevieweeId);
									if(StrRevieweeId.equals("138")) {
										System.out.println("0");
									}
								} else if(hmExistSectionCount != null && !hmExistSectionCount.isEmpty()) {
									if(hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Peer")+"_"+StrRevieweeId) != null && sectionCount != hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Peer")+"_"+StrRevieweeId)) {
										employeeList.add(StrRevieweeId);
										if(StrRevieweeId.equals("138")) {
											System.out.println("1");
										}
									} else if(hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Peer")+"_"+StrRevieweeId) == null || (uF.parseToInt(hmQueCount.get(appraisalMp.get("ID")+"_"+StrRevieweeId)) != hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Peer")+"_"+StrRevieweeId))) {
										employeeList.add(StrRevieweeId);
										if(StrRevieweeId.equals("138")) {
											System.out.println("2");
										}
									} else {
										reviewedEmpList.add(StrRevieweeId);
										if(StrRevieweeId.equals("138")) {
											System.out.println("3");
										}
									}
								}
			//-----------------------------------------------------------------------------------
								getSecondMaxOrientNameAndIDs(con, 3, appraisalMp.get("ID"), StrRevieweeId, uF, hmRemainOrientDetails);
								getExistOrientTypeInAQA(appraisalMp.get("ID"),appraisalMp.get("APP_FREQ_ID"), uF);
								empMp.put(hmOrientMemberID.get("Peer"), employeeList);
								hmReviewedEmp.put(hmOrientMemberID.get("Peer"), reviewedEmpList);
							}
						}
						
						if(otherPeerIdsList != null && otherPeerIdsList.size() > 0) {
							if(otherPeerIdsList != null && otherPeerIdsList.contains(strSessionEmpId)) {
								Map<String, String> empstatusMp = userTypeMp.get(hmOrientMemberID.get("Other Peer"));
								if(empstatusMp==null)empstatusMp = new HashMap<String, String>();
								employeeList = empMp.get(hmOrientMemberID.get("Other Peer"));
								if(employeeList == null) employeeList = new ArrayList<String>();
								reviewedEmpList = hmReviewedEmp.get(hmOrientMemberID.get("Other Peer"));
								if(reviewedEmpList == null) reviewedEmpList = new ArrayList<String>();
								
								if (empstatusMp!=null && empstatusMp.get(StrRevieweeId+"_"+strSessionEmpId) == null) {
									employeeList.add(StrRevieweeId);
								} else if(hmExistSectionCount != null && !hmExistSectionCount.isEmpty()) {
									if(hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Other Peer")+"_"+StrRevieweeId) != null && sectionCount != hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Other Peer")+"_"+StrRevieweeId)) {
										employeeList.add(StrRevieweeId);
									} else if(hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Other Peer")+"_"+StrRevieweeId) == null || (uF.parseToInt(hmQueCount.get(appraisalMp.get("ID")+"_"+StrRevieweeId)) != hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Other Peer")+"_"+StrRevieweeId))) {
										employeeList.add(StrRevieweeId);
									} else {
										reviewedEmpList.add(StrRevieweeId);
									}
								}
			//-----------------------------------------------------------------------------------
								getSecondMaxOrientNameAndIDs(con, 10, appraisalMp.get("ID"), StrRevieweeId, uF, hmRemainOrientDetails);
								getExistOrientTypeInAQA(appraisalMp.get("ID"),appraisalMp.get("APP_FREQ_ID"), uF);
								empMp.put(hmOrientMemberID.get("Other Peer"), employeeList);
								hmReviewedEmp.put(hmOrientMemberID.get("Other Peer"), reviewedEmpList);
							}
						}
						
						if(otherIdsList != null && otherIdsList.size() > 0) {
							if(otherIdsList != null && otherIdsList.contains(strSessionEmpId)) {
								Map<String, String> empstatusMp = userTypeMp.get(hmOrientMemberID.get("Anyone"));
								if(empstatusMp==null)empstatusMp = new HashMap<String, String>();
								employeeList = empMp.get(hmOrientMemberID.get("Anyone"));
								if(employeeList == null) employeeList = new ArrayList<String>();
								reviewedEmpList = hmReviewedEmp.get(hmOrientMemberID.get("Anyone"));
								if(reviewedEmpList == null) reviewedEmpList = new ArrayList<String>();
								
								if (empstatusMp!=null && empstatusMp.get(StrRevieweeId+"_"+strSessionEmpId) == null) {
									employeeList.add(StrRevieweeId);
								} else if(hmExistSectionCount != null && !hmExistSectionCount.isEmpty()) {
									if(hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Anyone")+"_"+StrRevieweeId) != null && sectionCount != hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Anyone")+"_"+StrRevieweeId)) {
										employeeList.add(StrRevieweeId);
									} else if(hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Anyone")+"_"+StrRevieweeId) == null || (uF.parseToInt(hmQueCount.get(appraisalMp.get("ID")+"_"+StrRevieweeId)) != hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Anyone")+"_"+StrRevieweeId))) {
										employeeList.add(StrRevieweeId);
									} else {
										reviewedEmpList.add(StrRevieweeId);
									}
								}
			//-----------------------------------------------------------------------------------
//								getSecondMaxOrientNameAndIDsForPeer(con, appraisalMp.get("ID"), uF);
//								getExistOrientTypeInAQA(appraisalMp.get("ID"),appraisalMp.get("APP_FREQ_ID"), uF);
								empMp.put(hmOrientMemberID.get("Anyone"), employeeList);
								hmReviewedEmp.put(hmOrientMemberID.get("Anyone"), reviewedEmpList);
							}
						}
						
//						System.out.println(strSessionEmpId + " --- StrRevieweeId ===>> " + StrRevieweeId +" -- hmOrientationMemberIds ===>> " + hmOrientationMemberIds+" -- hmOrientMemberID ===>> " + hmOrientMemberID.get("Self"));
						if(uF.parseToInt(StrRevieweeId) == uF.parseToInt(strSessionEmpId) && hmOrientationMemberIds.contains(hmOrientMemberID.get("Self"))) {
							Map<String, String> empstatusMp = userTypeMp.get(hmOrientMemberID.get("Self"));
							if(empstatusMp==null)empstatusMp = new HashMap<String, String>();
							employeeList = empMp.get(hmOrientMemberID.get("Self"));
							if(employeeList == null) employeeList = new ArrayList<String>();
							reviewedEmpList = hmReviewedEmp.get(hmOrientMemberID.get("Self"));
							if(reviewedEmpList == null) reviewedEmpList = new ArrayList<String>();
							
							if (empstatusMp!=null && empstatusMp.get(StrRevieweeId+"_"+strSessionEmpId) == null) {
								employeeList.add(StrRevieweeId);
							} else if(hmExistSectionCount != null && !hmExistSectionCount.isEmpty()) {
								if(hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Self")+"_"+StrRevieweeId) != null && sectionCount != hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Self")+"_"+StrRevieweeId)) {
									employeeList.add(StrRevieweeId);
								} else if(hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Self")+"_"+StrRevieweeId) == null || (uF.parseToInt(hmQueCount.get(appraisalMp.get("ID")+"_"+StrRevieweeId)) != hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Self")+"_"+StrRevieweeId))) {
									employeeList.add(StrRevieweeId);
								} else {
									reviewedEmpList.add(StrRevieweeId);
								}
							}
		//-----------------------------------------------------------------------------------
							getSecondMaxOrientNameAndIDs(con, 2, appraisalMp.get("ID"), StrRevieweeId, uF, hmRemainOrientDetails);
							getExistOrientTypeInAQA(appraisalMp.get("ID"),appraisalMp.get("APP_FREQ_ID"), uF);
							empMp.put(hmOrientMemberID.get("Self"), employeeList);
							hmReviewedEmp.put(hmOrientMemberID.get("Self"), reviewedEmpList);
						}
						
					}
					
				}


				
				if(strSessionUserType != null && strSessionUserType.equalsIgnoreCase(EMPLOYEE)) {
//				if(appraisalMp.get("PEER") != null && appraisalMp.get("PEER").length() > 0) {
//					List<String> peerIdsList = Arrays.asList(appraisalMp.get("PEER").split(","));
////					System.out.println("peerIdsList =====> "+ peerIdsList.toString());
////					System.out.println("peerIdsList self =====> "+ self);
//					if(peerIdsList != null && peerIdsList.contains(strSessionEmpId)) {
//					Map<String, String> empstatusMp = userTypeMp.get(hmOrientMemberID.get("Peer"));
//					if(empstatusMp==null)empstatusMp = new HashMap<String, String>();
//					employeeList = new ArrayList<String>();
//					if(self != null && !self.equals("")) {
//						pst = con.prepareStatement("select emp_id from employee_official_details where emp_id in(" + self + ")"); // and wlocation_id=? and grade_id=?
//						rs = pst.executeQuery();
//						while (rs.next()) {
//							if(uF.parseToInt(rs.getString("emp_id")) == uF.parseToInt(strSessionEmpId)) {
//								continue;
//							}
//							if (empstatusMp!=null && empstatusMp.get(rs.getString("emp_id")+"_"+strSessionEmpId) == null) {
//								employeeList.add(rs.getString("emp_id"));
//							} else if(hmExistSectionCount != null && !hmExistSectionCount.isEmpty()) {
//								if(hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_id")) != null && sectionCount != hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_id"))) {
//									employeeList.add(rs.getString("emp_id"));
//								}
//							}
//						}
//						rs.close();
//						pst.close();
//					}
//					
////-----------------------------------------------------------------------------------
//					getSecondMaxOrientNameAndIDsForPeer(con, appraisalMp.get("ID"), uF);
//					getExistOrientTypeInAQA(appraisalMp.get("ID"),appraisalMp.get("APP_FREQ_ID"), uF);
//					empMp.put(hmOrientMemberID.get("Peer"), employeeList);
//				}
//				}
				
				
//				if (appraisalMp.get("ANYONE") != null && appraisalMp.get("ANYONE").length() > 0) {
////					System.out.println("appraisalIdList.get(i) ===>> " + appraisalIdList.get(i) + " strSessionEmpId =====> " + strSessionEmpId + "  appraisalMp.get(ANYONE) =====> " + appraisalMp.get("ANYONE"));
//					List<String> anyoneIdsList = Arrays.asList(appraisalMp.get("ANYONE").split(","));
//					if (anyoneIdsList != null && anyoneIdsList.contains(strSessionEmpId)) {
////						System.out.println("in if anyoneIdsList =====> " + anyoneIdsList);
////						System.out.println("anyoneIdsList self =====> " + self);
//					Map<String, String> empstatusMp = userTypeMp.get(hmOrientMemberID.get("Anyone"));
//					if(empstatusMp==null)empstatusMp=new HashMap<String, String>();
//					employeeList = new ArrayList<String>();
//					if(self != null && !self.equals("")) {
//						pst = con.prepareStatement("select emp_id from employee_official_details where emp_id in(" + self + ")");
//						rs = pst.executeQuery();
//						while (rs.next()) {
//							if (empstatusMp!=null && empstatusMp.get(rs.getString("emp_id")+"_"+strSessionEmpId) == null){
//								employeeList.add(rs.getString("emp_id"));
//							} else if(hmExistSectionCount != null && !hmExistSectionCount.isEmpty() ){
//								if(hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_id")) != null && sectionCount != hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_id"))){
//								employeeList.add(rs.getString("emp_id"));
//							}
//						}
//						}
//						rs.close();
//						pst.close();
//					}
////					System.out.println("employeeList AnyOne =====> " + employeeList);
////-----------------------------------------------------------------------------------
//					empMp.put(hmOrientMemberID.get("Anyone"), employeeList);
//				}
//				}
//			}

				/*if(self != null && !self.equals("")) {
					pst = con.prepareStatement("select emp_id from employee_official_details where emp_id in(" + self + ") and emp_id=?");
					pst.setInt(1, uF.parseToInt(strSessionEmpId));
					rs = pst.executeQuery();
					employeeList = new ArrayList<String>();
					while (rs.next()) {
						employeeList.add(rs.getString("emp_id"));
					}
					rs.close();
					pst.close();
				}
//---------------------------------------------------------------------------------
				getSecondMaxOrientNameAndIDsForSelf(con, appraisalMp.get("ID"), uF);
				getExistOrientTypeInAQA(appraisalMp.get("ID"),appraisalMp.get("APP_FREQ_ID"), uF);
				empMp.put(hmOrientMemberID.get("Self"), employeeList);*/
				
			}
//				System.out.println("empMp =====> " + empMp);
				empMpDetails.put(appraisalIdList.get(i), empMp);
				hmReviewedEmpDetails.put(appraisalIdList.get(i), hmReviewedEmp);
				
//				Map<String, List<String>> hmRemainOrientDetails = (Map<String, List<String>>)request.getAttribute("hmRemainOrientDetails");
				hmRemainOrientDetailsAppWise.put(appraisalIdList.get(i), hmRemainOrientDetails);
				
//				Map<String, List<String>> hmRemainOrientDetailsForSelf = (Map<String, List<String>>)request.getAttribute("hmRemainOrientDetailsForSelf");
//				hmRemainOrientDetailsForSelfAppWise.put(appraisalIdList.get(i), hmRemainOrientDetailsForSelf);
//				
//				Map<String, List<String>> hmRemainOrientDetailsForPeer = (Map<String, List<String>>)request.getAttribute("hmRemainOrientDetailsForPeer");
//				hmRemainOrientDetailsForPeerAppWise.put(appraisalIdList.get(i), hmRemainOrientDetailsForPeer);
				
				Map<String,List<String>> hmExistOrientTypeAQA =  (Map<String, List<String>>)request.getAttribute("hmExistOrientTypeAQA");
				hmExistOrientTypeAQAAppWise.put(appraisalIdList.get(i), hmExistOrientTypeAQA);
				
//				System.out.println("empMpDetails ::::: "+ appraisalIdList.get(i)+" - " + empMpDetails);
			}
//			System.out.println("empMpDetails ::::: "+empMpDetails);
			
//			System.out.println("hmExistOrientTypeAQAAppWise ::::: "+hmExistOrientTypeAQAAppWise);
//			System.out.println("hmRemainOrientDetailsAppWise ::::: "+hmRemainOrientDetailsAppWise);
//			System.out.println("hmRemainOrientDetailsForSelfAppWise ::::: "+hmRemainOrientDetailsForSelfAppWise);
//			System.out.println("hmRemainOrientDetailsForPeerAppWise ::::: "+hmRemainOrientDetailsForPeerAppWise);
			
			sbquery = new StringBuilder();
			Map<String, List<String>> hmAppraisalDetails = new HashMap<String, List<String>>();
//			pst = con.prepareStatement("select * from appraisal_details ad,appraisal_details_frequency adf where ad.appraisal_details_id=adf.appraisal_id " +
//					" and adf.is_appraisal_publish=true and self_ids like '%,"+strSessionEmpId+",%'" );
			sbquery.append("select * from appraisal_details ad,appraisal_details_frequency adf where ad.appraisal_details_id=adf.appraisal_id " +
					" and adf.is_appraisal_publish=true and self_ids like '%,"+strSessionEmpId+",%'" );
			if(getDataType() != null && getDataType().equals("L")) {
				sbquery.append(" and is_appraisal_close = false ");
			}
			if(getDataType() != null && getDataType().equals("C")) {
				sbquery.append(" and is_appraisal_close = true ");
			}
			pst = con.prepareStatement(sbquery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> appraisalList = new ArrayList<String>();
				
				/*String memberName = "";
				if(rs.getString("usertype_member") != null) {
					List<String> memberList = Arrays.asList(rs.getString("usertype_member").split(","));
					for (int i = 0; i < memberList.size(); i++) {
						if (i == 0) {
							memberName += orientationMemberMp.get(memberList.get(i));
						} else {
							memberName += ", " + orientationMemberMp.get(memberList.get(i));
						}
					}
				}
				if(memberName == null || memberName.equals("null")) {
					memberName = "Anyone"; 
				}
				*/
				appraisalList.add(uF.showData(rs.getString("appraisal_details_id"), ""));//0
				appraisalList.add(uF.showData(rs.getString("appraisal_name"), ""));//1
//				appraisalList.add(uF.showData(orientationMp.get(rs.getString("oriented_type")) + "&deg( " + memberName + " )", ""));//2
				appraisalList.add(uF.showData(hmFreq.get(rs.getString("frequency").trim()), ""));//3
				appraisalList.add(uF.showData(rs.getString("appraisal_type"), ""));//4
				appraisalList.add(uF.showData(rs.getString("appraisal_description"), ""));//5
				appraisalList.add(uF.showData(rs.getString("appraisal_instruction"), ""));//6
				appraisalList.add(uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));//7
				appraisalList.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));//8
				appraisalList.add(""+uF.parseToBoolean(rs.getString("is_appraisal_close")));// 9
				appraisalList.add(uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, CF.getStrReportDateFormat()));// 10
				appraisalList.add(uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, CF.getStrReportDateFormat()));// 11
				appraisalList.add(uF.showData(rs.getString("appraisal_freq_id"), ""));//12
				hmAppraisalDetails.put(rs.getString("appraisal_details_id")+"_"+rs.getString("appraisal_freq_id"), appraisalList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmAppraisalDetails", hmAppraisalDetails);
			
			request.setAttribute("empMpDetails", empMpDetails);
			request.setAttribute("hmReviewedEmpDetails", hmReviewedEmpDetails);
			
			request.setAttribute("hmRemainOrientDetailsAppWise", hmRemainOrientDetailsAppWise);
//			request.setAttribute("hmRemainOrientDetailsForSelfAppWise", hmRemainOrientDetailsForSelfAppWise);
//			request.setAttribute("hmRemainOrientDetailsForPeerAppWise", hmRemainOrientDetailsForPeerAppWise);
			request.setAttribute("hmExistOrientTypeAQAAppWise", hmExistOrientTypeAQAAppWise);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
//===created by parvez date: 21-07-2022===
	//===start===
	private void getOneOneDiscussionDetails(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			Map<String,List<String>> hmDiscussionDetails = new HashMap<String, List<String>>();
			/*pst = con.prepareStatement("SELECT  edd.* FROM emp_one_one_discussion_details edd INNER JOIN (SELECT  appraisal_id, MAX(user_entry_date) AS user_entry_date,MAX(emp_entry_date) AS emp_entry_date " +
					" FROM emp_one_one_discussion_details GROUP BY appraisal_id,emp_id ) maxedd ON edd.appraisal_id = maxedd.appraisal_id AND (edd.user_entry_date = maxedd.user_entry_date OR edd.emp_entry_date = maxedd.emp_entry_date) " +
					"and edd.emp_id=?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("emp_one_one_discussion_details_id"));		//0
//				innerList.add(rs.getString("emp_id"));
				innerList.add(rs.getString("user_rating"));								//1
				innerList.add(rs.getString("user_remark"));								//2
				innerList.add(uF.showData(rs.getString("emp_remark"), ""));				//3
				innerList.add(uF.showData(rs.getString("emp_sign_off"), ""));				//4
				hmDiscussionDetails.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id"), innerList);
			}
			rs.close();
			pst.close();*/
			
			
			/*Map<String,String> hmAppWiseFreqId = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_details ad,appraisal_details_frequency adf where ad.appraisal_details_id=adf.appraisal_id " +
					" and adf.is_appraisal_publish=true and self_ids like '%,"+strSessionEmpId+",%'" );
			rs = pst.executeQuery();
			while (rs.next()) {
				hmAppWiseFreqId.put(rs.getString("appraisal_details_id"), rs.getString("appraisal_freq_id"));
			}
			rs.close();
			pst.close();
			
			Map<String,List<String>> hmDiscussionDetails = new HashMap<String, List<String>>();
			Iterator<String> itr = hmAppWiseFreqId.keySet().iterator();
			while(itr.hasNext()){
				String strId = itr.next();
				String StrFreqId = hmAppWiseFreqId.get(strId);
				pst = con.prepareStatement("select * from emp_one_one_discussion_details where emp_id=? and user_id=? and user_type_id=? and appraisal_id=? and appraisal_freq_id=? order by emp_one_one_discussion_details_id desc limit 1");
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setInt(3, uF.parseToInt(strUserTypeId));
				pst.setInt(4, uF.parseToInt(strId));
				pst.setInt(5, uF.parseToInt(StrFreqId));
//				System.out.println("pst=="+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("emp_one_one_discussion_details_id"));		//0
//					innerList.add(rs.getString("emp_id"));
					innerList.add(rs.getString("emp_rating"));								//1
					innerList.add(rs.getString("emp_remark"));								//2
					innerList.add(hmEmpName.get(rs.getString("user_id")));								//3
					hmDiscussionDetails.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id"), innerList);
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from emp_one_one_discussion_details where emp_id=? and user_id not in("+strSessionEmpId+") " +
						"and appraisal_id=? and appraisal_freq_id=? order by emp_one_one_discussion_details_id desc limit 1");
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setInt(2, uF.parseToInt(strId));
				pst.setInt(3, uF.parseToInt(StrFreqId));
		//		System.out.println("pst=="+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
//					System.out.println(rs.getString("emp_entry_date"));
					innerList.add(rs.getString("emp_one_one_discussion_details_id"));
					innerList.add(rs.getString("emp_remark"));								//2
					innerList.add(hmEmpName.get(rs.getString("user_id")));								//2
					hmDiscussionDetails.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_MANAGER", innerList);
				}
				rs.close();
				pst.close();
				
			}*/
			
			StringBuilder sbQuery = new StringBuilder();
			
			Map<String,String> hmAppWiseFreqId = new HashMap<String, String>();
//			pst = con.prepareStatement("select appraisal_details_id,appraisal_freq_id from appraisal_details ad,appraisal_details_frequency adf where ad.appraisal_details_id=adf.appraisal_id " +
//					" and adf.is_appraisal_publish=true and self_ids like '%,"+strSessionEmpId+",%'" );
			sbQuery.append("select appraisal_details_id,appraisal_freq_id from appraisal_details ad,appraisal_details_frequency adf where ad.appraisal_details_id=adf.appraisal_id " +
					" and adf.is_appraisal_publish=true and self_ids like '%,"+strSessionEmpId+",%'");
			if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and is_appraisal_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and is_appraisal_close = true ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			System.out.println("pst="+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmAppWiseFreqId.put(rs.getString("appraisal_details_id"), rs.getString("appraisal_freq_id"));
			}
			rs.close();
			pst.close();
			
			Iterator<String> itr = hmAppWiseFreqId.keySet().iterator();
			while(itr.hasNext()){
				String strId = itr.next();
				String StrFreqId = hmAppWiseFreqId.get(strId);
				pst = con.prepareStatement("select * from emp_one_one_discussion_details where emp_id=? and appraisal_id=? and appraisal_freq_id=? order by emp_one_one_discussion_details_id desc limit 1");
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setInt(2, uF.parseToInt(strId));
				pst.setInt(3, uF.parseToInt(StrFreqId));
//				System.out.println("pst=="+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("emp_one_one_discussion_details_id"));		//0
//					innerList.add(rs.getString("emp_id"));
					innerList.add(rs.getString("user_rating"));								//1
					innerList.add(rs.getString("user_remark"));								//2
					innerList.add(uF.showData(rs.getString("emp_remark"), ""));				//3
					innerList.add(uF.showData(rs.getString("emp_sign_off"), ""));				//4
					hmDiscussionDetails.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id"), innerList);
				}
				rs.close();
				pst.close();
			}
			
			request.setAttribute("hmDiscussionDetails", hmDiscussionDetails);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getAppraisalFinalScore(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmAppraisalFinalScore = new HashMap<String, String>();
			pst = con.prepareStatement("select * from reviewer_feedback_details where emp_id=?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmAppraisalFinalScore.put(rs.getString("appraisal_id"), rs.getString("reviewer_marks"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmAppraisalFinalScore", hmAppraisalFinalScore);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void insertUserRatingComment(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
//			pst = con.prepareStatement("insert into emp_one_one_discussion_details(emp_id,user_id,user_type_id,appraisal_id,appraisal_freq_id,emp_remark,emp_entry_date) " +
//					"values(?,?,?,?, ?,?,?)" );
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			pst.setInt(2, uF.parseToInt(strSessionEmpId));
//			pst.setInt(3, uF.parseToInt(strUserTypeId));
//			pst.setInt(4, uF.parseToInt(getAppId()));
//			pst.setInt(5, uF.parseToInt(getAppFreqId()));
//			pst.setString(6, getStrComment());
//			pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
//			pst.executeUpdate();
//			pst.close();
			
			
			pst = con.prepareStatement("update emp_one_one_discussion_details set emp_remark=?, emp_entry_date=? where emp_id=? and appraisal_id=? and appraisal_freq_id=? and emp_one_one_discussion_details_id=? " );
			pst.setString(1, getStrComment());
			pst.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getAppId()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			pst.setInt(6, uF.parseToInt(getDiscussionId()));
//			System.out.println("pst="+pst);
			int x = pst.executeUpdate();
			pst.close();
			
			if(x==0){
				pst = con.prepareStatement("insert into emp_one_one_discussion_details(emp_id,user_id,user_type_id,appraisal_id,appraisal_freq_id,emp_remark,emp_entry_date) " +
						"values(?,?,?,?, ?,?,?)" );
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setInt(3, uF.parseToInt(strUserTypeId));
				pst.setInt(4, uF.parseToInt(getAppId()));
				pst.setInt(5, uF.parseToInt(getAppFreqId()));
				pst.setString(6, getStrComment());
				pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.executeUpdate();
//				System.out.println("KRAT/1843---pst="+pst);
				pst.close();
			}
			
			session.setAttribute(MESSAGE, SUCCESSM+"Request submitted successfully!"+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void signOffUserDiscussion(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
//			System.out.println("KRAT/1961---apStatus=="+uF.parseToBoolean(getApStatus()));
			
			pst = con.prepareStatement("update emp_one_one_discussion_details set emp_sign_off=?, emp_remark=? where emp_id=? and appraisal_id=? and appraisal_freq_id=? and emp_one_one_discussion_details_id=? " );
			pst.setBoolean(1, uF.parseToBoolean(getApStatus()));
			pst.setString(2, getStrComment());
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getAppId()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			pst.setInt(6, uF.parseToInt(getDiscussionId()));
//			System.out.println("pst="+pst);
			int x = pst.executeUpdate();
			pst.close();
			
			if(x==0){
				pst = con.prepareStatement("insert into emp_one_one_discussion_details(emp_id,user_id,user_type_id,appraisal_id,appraisal_freq_id,emp_remark,emp_entry_date,emp_sign_off) " +
						"values(?,?,?,?, ?,?,?,?)" );
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setInt(3, uF.parseToInt(strUserTypeId));
				pst.setInt(4, uF.parseToInt(getAppId()));
				pst.setInt(5, uF.parseToInt(getAppFreqId()));
				pst.setString(6, getStrComment());
				pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.setBoolean(8, uF.parseToBoolean(getApStatus()));
				pst.executeUpdate();
//				System.out.println("KRAT/1843---pst="+pst);
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
//===end===	
	
	private void getOrientTypeWiseIds() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		Map<String,List<String>> hmOrientTypewiseID = new HashMap<String, List<String>>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select appraisal_details_id,reviewee_id,ard.subordinate_ids,ard.peer_ids,ard.other_peer_ids,ard.supervisor_ids," +
				"ard.grand_supervisor_ids,ard.hod_ids,ard.ceo_ids,ard.hr_ids,ard.other_ids from appraisal_details ad, " +
				"appraisal_reviewee_details ard where ad.appraisal_details_id=ard.appraisal_id and ad.is_close = false");
//			pst.setInt(1, uF.parseToInt(appid));
			rs = pst.executeQuery();
			while (rs.next()) {
//				List<String> existUsersAQAList = hmExistUsersAQA.get(rs.getString("section_id")+"_"+rs.getString("user_type_id"));
				List<String> al = hmOrientTypewiseID.get(rs.getString("appraisal_details_id")+"_7");
				if(al==null) al = new ArrayList<String>();
				al.addAll(getListData(rs.getString("hr_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_7", al);
				
				al = hmOrientTypewiseID.get(rs.getString("appraisal_details_id")+"_4");
				if(al==null) al = new ArrayList<String>();
				al.addAll(getListData(rs.getString("peer_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_4", al);
				
				al = hmOrientTypewiseID.get(rs.getString("appraisal_details_id")+"_3");
				if(al==null) al = new ArrayList<String>();
				al.addAll(getListData(rs.getString("reviewee_id")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_3", al);
				
				al = hmOrientTypewiseID.get(rs.getString("appraisal_details_id")+"_2");
				if(al==null) al = new ArrayList<String>();
				al.addAll(getListData(rs.getString("supervisor_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_2", al);
				
				al = hmOrientTypewiseID.get(rs.getString("appraisal_details_id")+"_5");
				if(al==null) al = new ArrayList<String>();
				al.addAll(getListData(rs.getString("ceo_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_5", al);
				
				al = hmOrientTypewiseID.get(rs.getString("appraisal_details_id")+"_13");
				if(al==null) al = new ArrayList<String>();
				al.addAll(getListData(rs.getString("hod_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_13", al);
				
				al = hmOrientTypewiseID.get(rs.getString("appraisal_details_id")+"_6");
				if(al==null) al = new ArrayList<String>();
				al.addAll(getListData(rs.getString("subordinate_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_6", al);
				
				al = hmOrientTypewiseID.get(rs.getString("appraisal_details_id")+"_8");
				if(al==null) al = new ArrayList<String>();
				al.addAll(getListData(rs.getString("grand_supervisor_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_8", al);
				
				al = hmOrientTypewiseID.get(rs.getString("appraisal_details_id")+"_14");
				if(al==null) al = new ArrayList<String>();
				al.addAll(getListData(rs.getString("other_peer_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_14", al);
				
				al = hmOrientTypewiseID.get(rs.getString("appraisal_details_id")+"_10");
				if(al==null) al = new ArrayList<String>();
				al.addAll(getListData(rs.getString("other_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_10", al);
				
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmOrientTypewiseID ========== > "+hmOrientTypewiseID);
			request.setAttribute("hmOrientTypewiseID", hmOrientTypewiseID);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private List<String> getListData(String Ids){
		List<String> listIds = new ArrayList<String>();
		if(Ids != null && !Ids.equals("") && Ids.length()>1){
			Ids = Ids.substring(1, Ids.length());
			
			if (Ids.contains(",")) {
				String[] temp = Ids.split(",");
				for (int i = 0; i < temp.length; i++) {
					listIds.add(temp[i]);
				}
			}else{
				listIds.add(Ids);
			}
		}
		return listIds;
	}
	
	private void getExistUsersInAQA() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		Map<String,List<String>> hmExistUsersAQA = new HashMap<String, List<String>>();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select distinct(user_type_id),appraisal_id,section_id,user_id,emp_id from appraisal_question_answer where is_submit=true");
//			pst.setInt(1, uF.parseToInt(appid));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> existUsersAQAList = hmExistUsersAQA.get(rs.getString("emp_id")+"_"+rs.getString("section_id")+"_"+rs.getString("user_type_id"));
				if(existUsersAQAList==null) existUsersAQAList = new ArrayList<String>();				
				existUsersAQAList.add(rs.getString("user_id"));
				hmExistUsersAQA.put(rs.getString("emp_id")+"_"+rs.getString("section_id")+"_"+rs.getString("user_type_id"), existUsersAQAList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmExistUsersAQA ========== > "+hmExistUsersAQA);
			request.setAttribute("hmExistUsersAQA", hmExistUsersAQA);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getExistOrientTypeInAQA(String appid,String appFreqId, UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		Map<String,List<String>> hmExistOrientTypeAQA = new HashMap<String, List<String>>();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select distinct(user_type_id),appraisal_id,section_id,emp_id from appraisal_question_answer where is_submit=true and appraisal_id=? and appraisal_freq_id=?");
			pst.setInt(1, uF.parseToInt(appid));
			pst.setInt(2, uF.parseToInt(appFreqId));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> existOrientTypeAQAList = hmExistOrientTypeAQA.get(rs.getString("section_id")+"_"+rs.getString("emp_id"));
				if(existOrientTypeAQAList==null) existOrientTypeAQAList = new ArrayList<String>();				
				existOrientTypeAQAList.add(rs.getString("user_type_id"));
				hmExistOrientTypeAQA.put(rs.getString("section_id")+"_"+rs.getString("emp_id"), existOrientTypeAQAList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmExistOrientTypeAQA ========== > "+appid+" -- "+hmExistOrientTypeAQA);
			request.setAttribute("hmExistOrientTypeAQA", hmExistOrientTypeAQA);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	 
	/*private void getSecondMaxOrientNameAndIDsForSelf(Connection con, String appid, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> appSections = new ArrayList<String>();
		
		try {
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
			
			pst = con.prepareStatement("select main_level_id from appraisal_main_level_details where appraisal_id = ? order by main_level_id");
			pst.setInt(1, uF.parseToInt(appid));
			rs = pst.executeQuery();
			while (rs.next()) {
				appSections.add(rs.getString("main_level_id"));
			}
			rs.close();
			pst.close();
			
			Map<String, List<String>> hmRemainOrientDetailsForSelf = new HashMap<String, List<String>>() ;
			for(int a=0; appSections != null && a<appSections.size();a++){
				List<String> allOrientName = new ArrayList<String>();
				List<String> allOrientID = new ArrayList<String>();
				getOrientPositions(appSections.get(a));
//				System.out.println("appSections.get(a) :::::::: "+appSections.get(a));
			
			List<String> orientPositionList = (List<String>)request.getAttribute("orientPositionList");
			String orientName[] = {"HR","Manager","Self","Peer","Client","Sub-ordinate","GroupHead","Vendor","CEO","HOD","Other Peer"};
			if(orientPositionList != null && !orientPositionList.isEmpty()){
//			System.out.println("orientPositionList.get(id) :::::::: "+orientPositionList.get(id));
			int position = uF.parseToInt(orientPositionList.get(2));
			int cnt=1;
				for(int i = position; i>=1; i--){
					if(allOrientID == null || allOrientID.isEmpty()){
						for(int j = 0; orientPositionList!=null && j<orientPositionList.size(); j++){
								if(uF.parseToInt(orientPositionList.get(2)) > 1 && (uF.parseToInt(orientPositionList.get(2))-cnt) == uF.parseToInt(orientPositionList.get(j))){
									allOrientName.add(orientName[j]);
									allOrientID.add(hmOrientMemberID.get(orientName[j]));
								}
							}
//						System.out.println("allOrientNameForSelf :::::::: "+allOrientName);
//						System.out.println("allOrientIDForSelf :::::::: "+allOrientID);
					}
//					System.out.println("allOrientName 1 :::::::: "+allOrientName);
//					System.out.println("allOrientID 1 :::::::: "+allOrientID);
				cnt++;
				}
			}
			hmRemainOrientDetailsForSelf.put(appSections.get(a)+"NAME", allOrientName);
			hmRemainOrientDetailsForSelf.put(appSections.get(a)+"ID", allOrientID);
		}
//			System.out.println("hmRemainOrientDetailsForSelf :::::::: "+hmRemainOrientDetailsForSelf);
			request.setAttribute("hmRemainOrientDetailsForSelf", hmRemainOrientDetailsForSelf);
//			System.out.println("allOrientName :::::::: "+allOrientName.toString());
//			System.out.println("allOrientID :::::::: "+allOrientID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	private void getSecondMaxOrientNameAndIDs(Connection con, int id, String appid, String empId, UtilityFunctions uF, Map<String, List<String>> hmRemainOrientDetails) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> appSections = new ArrayList<String>();
		
		try {
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
			
			Map<String, String> hmRevieweeAndUsersIds = new HashMap<String, String>(); 
			pst = con.prepareStatement("select * from appraisal_reviewee_details where appraisal_id=? and reviewee_id=? ");
			pst.setInt(1, uF.parseToInt(appid));
			pst.setInt(2, uF.parseToInt(empId));
//				System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmRevieweeAndUsersIds.put("6", rs.getString("subordinate_ids"));
				hmRevieweeAndUsersIds.put("4", rs.getString("peer_ids"));
				hmRevieweeAndUsersIds.put("14", rs.getString("other_peer_ids"));
				hmRevieweeAndUsersIds.put("10", rs.getString("other_ids"));
				hmRevieweeAndUsersIds.put("3", ","+rs.getString("reviewee_id")+",");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select main_level_id from appraisal_main_level_details where appraisal_id = ? order by main_level_id");
			pst.setInt(1, uF.parseToInt(appid));
			rs = pst.executeQuery();
			while (rs.next()) {
				appSections.add(rs.getString("main_level_id"));
			}
			rs.close();
			pst.close();
			
//			Map<String, List<String>> hmRemainOrientDetails = new HashMap<String, List<String>>() ;
			for(int a=0; appSections != null && a<appSections.size();a++){
				List<String> allOrientName = new ArrayList<String>();
				List<String> allOrientID = new ArrayList<String>();
				getOrientPositions(appSections.get(a));
//				System.out.println("appSections.get(a) :::::::: "+appSections.get(a));
			
			List<String> orientPositionList = (List<String>)request.getAttribute("orientPositionList");
			String orientName[] = {"HR","Manager","Self","Peer","Client","Sub-ordinate","GroupHead","Vendor","CEO","HOD","Other Peer"};
			if(orientPositionList != null && !orientPositionList.isEmpty()){
//			System.out.println("orientPositionList.get(id) :::::::: "+orientPositionList.get(id));
			int position = uF.parseToInt(orientPositionList.get(id));
			int cnt=1;
				for(int i = position; i>=1; i--){
					if(allOrientID == null || allOrientID.isEmpty()){
						for(int j = 0; orientPositionList!=null && j<orientPositionList.size(); j++){
								if(uF.parseToInt(orientPositionList.get(id)) > 1 && (uF.parseToInt(orientPositionList.get(id))-cnt) == uF.parseToInt(orientPositionList.get(j))){
									if(hmRevieweeAndUsersIds.get(hmOrientMemberID.get(orientName[j]))!=null && hmRevieweeAndUsersIds.get(hmOrientMemberID.get(orientName[j])).length()>1) {
										allOrientName.add(orientName[j]);
										allOrientID.add(hmOrientMemberID.get(orientName[j]));
									}
								}
							}
//						System.out.println("allOrientNameForPeer :::::::: "+allOrientName);
//						System.out.println("allOrientIDForPeer :::::::: "+allOrientID);
					}
//					System.out.println("allOrientName 1 :::::::: "+allOrientName);
//					System.out.println("allOrientID 1 :::::::: "+allOrientID);
				cnt++;
				}
			}
			hmRemainOrientDetails.put(empId+"_"+appSections.get(a)+"NAME", allOrientName);
			hmRemainOrientDetails.put(empId+"_"+appSections.get(a)+"ID", allOrientID);
		}
//			System.out.println("hmRemainOrientDetailsForPeer :::::::: "+hmRemainOrientDetailsForPeer);
			request.setAttribute("hmRemainOrientDetails", hmRemainOrientDetails);
//			System.out.println("allOrientName :::::::: "+allOrientName.toString());
//			System.out.println("allOrientID :::::::: "+allOrientID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void getOrientPositions(String mainLevelID) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		List<String> orientPositionList = new ArrayList<String>();
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from appraisal_main_level_details where main_level_id=? limit 1");
			pst.setInt(1, uF.parseToInt(mainLevelID));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				orientPositionList.add(rs.getString("hr"));
				orientPositionList.add(rs.getString("manager"));
				orientPositionList.add(rs.getString("self"));
				orientPositionList.add(rs.getString("peer"));
				orientPositionList.add(rs.getString("client"));
				orientPositionList.add(rs.getString("subordinate"));
				orientPositionList.add(rs.getString("grouphead"));
				orientPositionList.add(rs.getString("vendor"));
				orientPositionList.add(rs.getString("ceo"));
				orientPositionList.add(rs.getString("hod"));
				orientPositionList.add(rs.getString("other_peer"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("orientPositionList", orientPositionList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private Map<String, List<List<String>>> getAppriesalSections() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, List<List<String>>> hmAppraisalSectins = new LinkedHashMap<String, List<List<String>>>();
		UtilityFunctions uF = new UtilityFunctions();
		try {
//			List<Map<String, String>> listAppSectionIDs = new ArrayList<Map<String, String>>();
			con = db.makeConnection(con);
			
		//===start parvez date: 15-03-2023===
			pst = con.prepareStatement("select * from appraisal_level_details order by appraisal_id,main_level_id,appraisal_level_id");
			rs = pst.executeQuery();
			Map<String, String> hmAppSystemType = new HashMap<String, String>();
			while (rs.next()) {
				hmAppSystemType.put(rs.getString("appraisal_id")+"_"+rs.getString("main_level_id"), rs.getString("appraisal_system"));
			}
			rs.close();
			pst.close();
		//===end parvez date: 15-03-2023===	
			
			pst = con.prepareStatement("select * from appraisal_main_level_details order by appraisal_id,main_level_id");
			rs = pst.executeQuery();
			String appID="";
			while (rs.next()) {
				appID = rs.getString("appraisal_id");
				List<List<String>> outerList = hmAppraisalSectins.get(appID);
				if(outerList==null)outerList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("main_level_id"));
				innerList.add(rs.getString("level_title"));
				innerList.add(hmAppSystemType.get(rs.getString("appraisal_id")+"_"+rs.getString("main_level_id")));
				outerList.add(innerList);
				hmAppraisalSectins.put(appID, outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(*) as cnt, user_type_id,appraisal_id,appraisal_freq_id,reviewer_or_appraiser,emp_id,section_id " +
				" from appraisal_question_answer where is_submit=true and user_id=? group by user_type_id,appraisal_id,appraisal_freq_id,reviewer_or_appraiser,emp_id,section_id");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
//			System.out.println("pst === > "+pst);
			Map<String, String> hmSectionGivenQueCnt = new HashMap<String, String>();
			while (rs.next()) {
				hmSectionGivenQueCnt.put(rs.getString("user_type_id")+"_"+rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")
					+"_"+rs.getString("reviewer_or_appraiser")+"_"+rs.getString("emp_id")+"_"+rs.getString("section_id"), rs.getInt("cnt")+"");
			}
			rs.close();
			pst.close();
			
//			List<String> alKRAIds = (List<String>)request.getAttribute("alKRAIds");
//			List<String> alGoarTargetIds = (List<String>)request.getAttribute("alGoarTargetIds");
			
			Map<String, List<String>> hmEmpKRAIds = (Map<String, List<String>>) request.getAttribute("hmEmpKRAIds");
			Map<String, List<String>> hmEmpGoarTargetIds = (Map<String, List<String>>) request.getAttribute("hmEmpGoarTargetIds");
			
			/*pst = con.prepareStatement("select count(*) as cnt,b.main_level_id,a.appraisal_id from appraisal_question_details a, appraisal_level_details b " +
				"where a.appraisal_level_id = b.appraisal_level_id group by a.appraisal_id,b.main_level_id order by a.appraisal_id,b.main_level_id");*/
			pst = con.prepareStatement("select appraisal_question_details_id,reviewee_id,b.main_level_id,a.appraisal_id,a.app_system_type,a.kra_id,a.goal_kra_target_id " +
					"from appraisal_question_details a, appraisal_level_details b, " +
				"appraisal_reviewee_details ard where a.appraisal_id=ard.appraisal_id and a.appraisal_level_id = b.appraisal_level_id order by a.appraisal_id,b.main_level_id");
			rs = pst.executeQuery();
	//		System.out.println("pst === > "+pst);
			Map<String, String> hmSectionQueCnt = new HashMap<String, String>();
			while (rs.next()) {
				List<String> alKRAIds = hmEmpKRAIds.get(rs.getString("reviewee_id"));
				if(rs.getInt("app_system_type")==4 && alKRAIds!=null && rs.getString("kra_id")!=null && !alKRAIds.contains(rs.getString("kra_id"))) {
					continue;
				}
				List<String> alGoarTargetIds = hmEmpGoarTargetIds.get(rs.getString("reviewee_id"));
				if((rs.getInt("app_system_type")==3 || rs.getInt("app_system_type")==5) && alGoarTargetIds!=null && rs.getString("goal_kra_target_id")!=null && !alGoarTargetIds.contains(rs.getString("goal_kra_target_id"))) {
					continue;
				}
				int intCnt = uF.parseToInt(hmSectionQueCnt.get(rs.getString("appraisal_id")+"_"+rs.getString("reviewee_id")+"_"+rs.getString("main_level_id")));
				intCnt++;
				hmSectionQueCnt.put(rs.getString("appraisal_id")+"_"+rs.getString("reviewee_id")+"_"+rs.getString("main_level_id"), intCnt+"");
			}
			rs.close();
			pst.close();
			
			/*pst = con.prepareStatement("select a.*,b.main_level_id from appraisal_question_details a, appraisal_level_details b " +
			"where a.appraisal_level_id = b.appraisal_level_id order by a.appraisal_id,b.main_level_id");
			rs = pst.executeQuery();
//			System.out.println("pst === > "+pst);
			Map<String, String> hmSectionQueCnt = new HashMap<String, String>();
			while (rs.next()) {
				if(rs.getInt("app_system_type")==4 && alKRAIds!=null && rs.getString("kra_id")!=null && !alKRAIds.contains(rs.getString("kra_id"))) {
					continue;
				}
				if((rs.getInt("app_system_type")==3 || rs.getInt("app_system_type")==5) && alGoarTargetIds!=null && rs.getString("goal_kra_target_id")!=null && !alGoarTargetIds.contains(rs.getString("goal_kra_target_id"))) {
					continue;
				}
				int intCnt = uF.parseToInt(hmSectionQueCnt.get(rs.getString("appraisal_id")+"_"+rs.getString("main_level_id")));
				hmSectionQueCnt.put(rs.getString("appraisal_id")+"_"+rs.getString("main_level_id"), intCnt+"");
			}
			rs.close();
			pst.close();*/
				
			request.setAttribute("hmSectionGivenQueCnt", hmSectionGivenQueCnt);
			request.setAttribute("hmSectionQueCnt", hmSectionQueCnt);
//			System.out.println("hmSectionGivenQueCnt =====>"+hmSectionGivenQueCnt);
//			System.out.println("hmSectionQueCnt =====>"+hmSectionQueCnt);
		
//			System.out.println("hmAppraisalSectins =====>"+hmAppraisalSectins);
			request.setAttribute("hmAppraisalSectins", hmAppraisalSectins);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmAppraisalSectins;
	}
	
	
	
	private Map<String, String> getOrientMemberID(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> orientationMemberMp = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMemberMp.put(rs.getString("member_name"), rs.getString("member_id"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("memberid=====>"+orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMemberMp;
	}

	
	
	public Map<String, Map<String, Map<String, String>>> getEmployeeStatus(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, Map<String, Map<String, String>>> appraisalMp = new HashMap<String, Map<String, Map<String, String>>>();
		con = db.makeConnection(con);
		try {
			pst = con.prepareStatement("select emp_id,appraisal_id,user_type_id,user_id,appraisal_freq_id from appraisal_question_answer where is_submit=true group by emp_id,appraisal_id,user_type_id,user_id,"
					+" appraisal_freq_id order by emp_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				
				Map<String, Map<String, String>> userTypeMp = appraisalMp.get(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id"));
				if (userTypeMp == null)userTypeMp = new HashMap<String, Map<String, String>>();
				Map<String, String> empMp = userTypeMp.get(rs.getString("user_type_id"));
				if (empMp == null)empMp = new HashMap<String, String>();
				
//				empMp.put(rs.getString("emp_id")+""+rs.getString("user_id")+""+rs.getString("user_type_id"), rs.getString("emp_id"));
				
//				if(uF.parseToInt(rs.getString("user_type_id"))==4 || uF.parseToInt(rs.getString("user_type_id"))==10){
					empMp.put(rs.getString("emp_id")+"_"+rs.getString("user_id"), rs.getString("emp_id"));
				/*}else{
					empMp.put(rs.getString("emp_id"), rs.getString("emp_id"));
				}*/
				userTypeMp.put(rs.getString("user_type_id"), empMp);
//				System.out.println("userTypeMp :: "+userTypeMp);
				appraisalMp.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id"), userTypeMp);
//				System.out.println("appraisalMp :: "+appraisalMp);
			}
			rs.close();
			pst.close();
//			System.out.println("appraisalMp ===>> " + appraisalMp);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return appraisalMp;
	}
	
	
	public void getNextStepUserFillFeedback(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		try {
			
			Map<String, String> hmUsertypeId = CF.getUserTypeIdMap(con);
				
			pst = con.prepareStatement("select amld.*,a.appraisal_freq_id from appraisal_main_level_details amld, (select adf.appraisal_freq_id, adf.appraisal_id from " +
				" appraisal_details_frequency adf, appraisal_details ad where ad.appraisal_details_id = adf.appraisal_id and adf.is_appraisal_publish = true) a " +
				" where a.appraisal_id = amld.appraisal_id ");
			rs = pst.executeQuery();
			Map<String, List<String>> hmAppraisalNextStep = new HashMap<String, List<String>>();
			while (rs.next()) {
				List<String> innerList = hmAppraisalNextStep.get(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id"));
				if(innerList == null)innerList = new ArrayList<String>();
				if(rs.getInt("self")>0 && rs.getInt("hr") >= (rs.getInt("self"))) {
					innerList.add(hmUsertypeId.get(HRMANAGER));
				}
//				if(rs.getInt("self")>0 && rs.getInt("manager") == (rs.getInt("self")+1)) {
				if(rs.getInt("self")>0 && rs.getInt("manager") >= (rs.getInt("self"))) {
					innerList.add(hmUsertypeId.get(MANAGER));
				}
				if(rs.getInt("self")>0 && rs.getInt("peer") >= (rs.getInt("self"))) {
					innerList.add("4");
				}
				if(rs.getInt("self")>0 && rs.getInt("client") >= (rs.getInt("self"))) {
					innerList.add("11");
				}
				if(rs.getInt("self")>0 && rs.getInt("hod") >= (rs.getInt("self"))) {
					innerList.add(hmUsertypeId.get(HOD));
				}
				if(rs.getInt("self")>0 && rs.getInt("ceo") >= (rs.getInt("self"))) {
					innerList.add(hmUsertypeId.get(CEO));
				}
				if(rs.getInt("self")>0 && rs.getInt("subordinate") >= (rs.getInt("self"))) {
					innerList.add("6");
				}
				if(rs.getInt("self")>0 && rs.getInt("grouphead") >= (rs.getInt("self"))) {
					innerList.add("8");
				}
				if(rs.getInt("self")>0 && rs.getInt("other_peer") >= (rs.getInt("self"))) {
					innerList.add("14");
				}
				hmAppraisalNextStep.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id"), innerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select emp_id,appraisal_id,user_type_id,user_id,appraisal_freq_id from appraisal_question_answer group by emp_id,appraisal_id,user_type_id,user_id,"
					+" appraisal_freq_id order by emp_id");
			rs = pst.executeQuery();
			Map<String, List<String>> hmNextStepFillEmpIds = new HashMap<String, List<String>>(); 
			while (rs.next()) {
				List<String> innerList = hmNextStepFillEmpIds.get(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("user_type_id"));
				if(innerList == null)innerList = new ArrayList<String>();
				innerList.add(rs.getString("emp_id"));
				
				hmNextStepFillEmpIds.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("user_type_id"), innerList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmAppraisalNextStep ===>> " + hmAppraisalNextStep);
			request.setAttribute("hmAppraisalNextStep", hmAppraisalNextStep);
			request.setAttribute("hmNextStepFillEmpIds", hmNextStepFillEmpIds);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private String getAppendData(String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();

		if (strID != null && !strID.equals("")) {
			strID = strID.substring(1, strID.length()-1);
			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 1; i < temp.length; i++) {
					if (i == 1) {
						sb.append(mp.get(temp[i].trim()));
					} else {
						sb.append("," + mp.get(temp[i].trim()));
					}
				}
			} else {
				return mp.get(strID);
			}
		} else {
			return null;
		}
		return sb.toString();
	}

	
	public Map<String, String> getLevelMap(Connection con) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new HashMap<String, String>();
		try {
			pst = con.prepareStatement(selectLevel);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name") + "[" + rs.getString("level_code") + "]");
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmLevelMap;
	}

	private void getOrientationMember() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
			Map<String, String> orientationMemberMp = new HashMap<String, String>();
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				//orientationMemberMp.put(rs.getString("orientation_member_id"), rs.getString("member_name"));
				orientationMemberMp.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();

			request.setAttribute("orientationMemberMp", orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private Map<String, String> getOrientationValue(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		Map<String, String> orientationMp = new HashMap<String, String>();
		try {

			pst = con.prepareStatement("select * from apparisal_orientation");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMp.put(rs.getString("apparisal_orientation_id"), rs.getString("orientation_name"));
			}
			rs.close();
			pst.close();

			request.setAttribute("orientationMp", orientationMp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMp;
	}
	
//===created by parvez date: 06-07-2022===
	//===start===
	private void getAppraisalReopenDetails(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			
			con = db.makeConnection(con);
			
			Map<String, List<String>> hmUsersFeedbackReopenComment = new HashMap<String, List<String>>();
			
			pst = con.prepareStatement("select * from review_feedback_reopen_details where user_id =? ");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
//			System.out.println("hmExistSectionCount pst2 ::: "+pst);
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("reopen_comment"));
				innerList.add(rs.getString("reopened_by"));
				hmUsersFeedbackReopenComment.put(rs.getString("review_id")+"_"+rs.getString("review_freq_id")+"_"+rs.getString("user_type_id")+"_"+rs.getString("emp_id"), innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmUsersFeedbackReopenComment", hmUsersFeedbackReopenComment);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}
	//===end===
	
	public String getStrID() {
		return strID;
	}

	public void setStrID(String strID) {
		this.strID = strID;
	}

	public String getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
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

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getFromPage() {
		return fromPage;
	}


	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}


	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

	public String getStrComment() {
		return strComment;
	}

	public void setStrComment(String strComment) {
		this.strComment = strComment;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}


	public String getDiscussionId() {
		return discussionId;
	}


	public void setDiscussionId(String discussionId) {
		this.discussionId = discussionId;
	}
//====start parvez date: 23-03-2023===
	public String getApStatus() {
		return apStatus;
	}

	public void setApStatus(String apStatus) {
		this.apStatus = apStatus;
	}
	
//===end parvez date: 23-03-2023===	


	
}