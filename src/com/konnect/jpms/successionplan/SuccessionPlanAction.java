package com.konnect.jpms.successionplan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.employee.EmployeeActivity;
import com.konnect.jpms.select.FillActivity;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmpStatus;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SuccessionPlanAction extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId;
	String strUserType;
	String strUserTypeId; 
	String strSessionWLocation;
	String strSessionOrgId;
	CommonFunctions CF;
	
	private String empid;
	private String remark;
	private String areasOfStrength;
	private String areasOfDevelopment;
	private String remarktype;	
	private String sendtoGapStatus;
	
	private String strDesignation;
	private String strReason;	

	private String fromPage;
	private String[] learningIds;
	private String[] reviewIds;
	
	private String strIncumbentEmpId;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strSessionWLocation = (String) session.getAttribute(WLOCATIONID);
		strSessionOrgId = (String)session.getAttribute(ORGID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();

		request.setAttribute(PAGE, "/jsp/successionplan/SuccessionPlanAction.jsp");
		request.setAttribute(TITLE, "Finalization");
		
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		
		String submit = request.getParameter("submit");
		String submit1 = request.getParameter("submit1");

		getLearningAndReviewData();
//		System.out.println("EmpId==>"+getEmpid()+"==>getEmpid==>"+getEmpid()+"==>getEmpid2==>"+getEmpid2());		
		
		Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
		if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
		
		if (submit != null || submit1 != null ) {
			boolean flag = insertComment();
			
			if(flag) {
				insertEmployeeActivity();
				return SUCCESS;
			} else {
				return ERROR;
			}
		} else {
			getRemarks();
			return LOAD;
		}
	}
	
	
	private void insertEmployeeActivity() {
		
		try {
			
			StringBuilder sbLearningIds = null;
			if(getLearningIds() !=null) {
				for(int i=0; i<getLearningIds().length; i++) {
					if(sbLearningIds==null) {
						sbLearningIds = new StringBuilder();
						sbLearningIds.append(","+getLearningIds()[i]+",");
					} else {
						sbLearningIds.append(getLearningIds()[i]+",");
					}
				}
			}
			
			StringBuilder sbReviewIds = null;
			if(getReviewIds() !=null) {
				for(int i=0; i<getReviewIds().length; i++) {
					if(sbReviewIds==null) {
						sbReviewIds = new StringBuilder();
						sbReviewIds.append(","+getReviewIds()[i]+",");
					} else {
						sbReviewIds.append(getReviewIds()[i]+",");
					}
				}
			}
			if(sbLearningIds==null) {
				sbLearningIds = new StringBuilder();
			}
			if(sbReviewIds==null) {
				sbReviewIds = new StringBuilder();
			}
			
			EmployeeActivity activity=new EmployeeActivity();
			activity.request=request;
			activity.session = session;
			activity.CF = CF;
			activity.setStrEmpId2(getEmpid());
//			activity.setSalaryHeadList(salaryHeadList); 
			activity.setStrActivity(ACTIVITY_SUCCESSION_PLAN_ID);
			activity.setStrDesignation(getStrDesignation());
			activity.setStrEmpId(null);
			activity.setStrReason(getStrReason());
			activity.setStrUpdate(null);
			activity.setFromPage("SP");
			activity.setStrLearningIds(sbLearningIds.toString());
			activity.setStrReviewIds(sbReviewIds.toString());
			activity.setStrIncumbentEmpId(getStrIncumbentEmpId());
			activity.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}




	private void getRemarks() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		Database db = new Database();
		db.setRequest(request);
		String remark = null;
		String areasOfStrength = null;
		String areasOfDevelopment = null;
		String strLearningPlans = "";
		String strAlignedReview = "";
		String strApprovedBy = null;
		boolean flag = false;
		try {
			con = db.makeConnection(con);
			
			String appraiseeName = CF.getEmpNameMapByEmpId(con, getEmpid());
			request.setAttribute("appraiseeName", appraiseeName);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("select areas_of_strength,areas_of_development,learning_ids,review_ids,sattlement_comment,if_approved,user_id, emp_fname,emp_mname, emp_lname, " +
				"activity_ids from appraisal_final_sattlement afs,employee_personal_details epd where afs.user_id = epd.emp_per_id and emp_id=? and incumbent_emp_id=? and desig_id =?");
//			pst=con.prepareStatement(selectFinalSattlement);
			pst.setInt(1, uF.parseToInt(getEmpid()));
			pst.setInt(2, uF.parseToInt(getStrIncumbentEmpId()));
			pst.setInt(3, uF.parseToInt(getStrDesignation()));
//			System.out.println("getRemarks pst==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				remark = rs.getString("sattlement_comment");
				if (remark != null) {
					remark = remark.replace("\n", "<br/>");
				}
				areasOfStrength = rs.getString("areas_of_strength");
				if (areasOfStrength != null) {
					areasOfStrength = areasOfStrength.replace("\n", "<br/>");
				}
				areasOfDevelopment = rs.getString("areas_of_development");
				if (areasOfDevelopment != null) {
					areasOfDevelopment = areasOfDevelopment.replace("\n", "<br/>");
				}
				if (rs.getString("learning_ids") != null && !rs.getString("learning_ids").equals("")) {
					strLearningPlans = getLearningPlanName(con, rs.getString("learning_ids"));
				}
				if (rs.getString("review_ids") != null && !rs.getString("review_ids").equals("")) {
					strAlignedReview = getReviewName(con, rs.getString("review_ids"));
				}
				
				flag = uF.parseToBoolean(rs.getString("if_approved"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				strApprovedBy = rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hrremark", remark);
			request.setAttribute("areasOfStrength", areasOfStrength);
			request.setAttribute("areasOfDevelopment", areasOfDevelopment);
			request.setAttribute("strLearningPlans", strLearningPlans);
			request.setAttribute("strAlignedReview", strAlignedReview);
			request.setAttribute("flag", flag);
			request.setAttribute("strApprovedBy", strApprovedBy);
			
			if(!flag) {
				
				viewProfile(getEmpid());
				viewEmpActivity(); 
				
			} else {
				String strReson = null;
				pst = con.prepareStatement("select reason from employee_activity_details where emp_id=? and incumbent_emp_id=? and desig_id =? order by emp_activity_id desc limit 1");
				pst.setInt(1, uF.parseToInt(getEmpid()));
				pst.setInt(2, uF.parseToInt(getStrIncumbentEmpId()));
				pst.setInt(3, uF.parseToInt(getStrDesignation()));
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				if (rs.next()) {
					strReson = uF.showData(rs.getString("reason"), "");
				}
				request.setAttribute("strReson", strReson);
				/*
				Map<String, String> hmActivity = CF.getActivityName(con);
				if(hmActivity == null) hmActivity = new HashMap<String, String>();
				
				Map<String, String> hmGradeMap = CF.getGradeMap(con);
				if (hmGradeMap == null) hmGradeMap = new HashMap<String, String>();
				Map<String, String> hmLevelMap = CF.getLevelMap(con);
				if(hmLevelMap == null) hmLevelMap = new HashMap<String, String>();
				Map<String, String> hmDesig = CF.getDesigMap(con);
				if(hmDesig == null) hmDesig = new HashMap<String, String>();
				Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con);
				if(hmWorkLocation == null) hmWorkLocation = new HashMap<String, Map<String, String>>();
				Map<String, String> hmDepartment = CF.getDepartmentMap(con, null, null);
				if(hmDepartment == null) hmDepartment = new HashMap<String, String>();
				Map<String, Map<String, String>> hmOrgMap = CF.getOrgDetails(con, uF);
				if(hmOrgMap == null) hmOrgMap = new HashMap<String, Map<String, String>>();
				Map<String, String> hmServices = CF.getServicesMap(con, false);
				if(hmServices == null) hmServices = new HashMap<String, String>();
				
				pst = con.prepareStatement("select * from employee_activity_details where emp_id=? and appraisal_id=? and appraisal_freq_id =? order by emp_activity_id desc limit 1");
				pst.setInt(1, uF.parseToInt(getEmpid()));
				pst.setInt(2, uF.parseToInt(getId()));
				pst.setInt(3, uF.parseToInt(getAppFreqId()));
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				Map<String, String> hmActivityMap = new HashMap<String, String>(); 
				if (rs.next()) {
					hmActivityMap.put("ACTIVITY_ID", rs.getString("activity_id"));
					hmActivityMap.put("ACTIVITY_NAME", uF.showData(hmActivity.get(rs.getString("activity_id")), ""));
					hmActivityMap.put("EFFECTIVE_DATE", uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()));
					
					String strNoOfDays = "";
					if(uF.parseToInt(rs.getString("activity_id")) == uF.parseToInt(ACTIVITY_EXTEND_PROBATION_ID)){
						strNoOfDays = uF.showData(rs.getString("extend_probation_period"), "0");
					} else if(uF.parseToInt(rs.getString("activity_id")) == uF.parseToInt(ACTIVITY_NOTICE_PERIOD_ID)){
						strNoOfDays = uF.showData(rs.getString("notice_period"), "0");
					} else if(uF.parseToInt(rs.getString("activity_id")) == uF.parseToInt(ACTIVITY_PROBATION_ID)){
						strNoOfDays = uF.showData(rs.getString("probation_period"), "0");
					}
					hmActivityMap.put("NO_OF_DAYS", strNoOfDays);
					hmActivityMap.put("REASON", uF.showData(rs.getString("reason"), ""));
					hmActivityMap.put("INCREMENT_PERCENTAGE", uF.showData(rs.getString("increment_percent"), "0"));
					hmActivityMap.put("GRADE_NAME", uF.showData(hmGradeMap.get(rs.getString("grade_id")), ""));
					
					String strIncrType = "";
					if(uF.parseToInt(rs.getString("increment_type")) == 1){
						strIncrType = "Single";
					} else if(uF.parseToInt(rs.getString("increment_type")) == 2){
						strIncrType = "Double";
					} 
					hmActivityMap.put("INCREMENT_TYPE", strIncrType);
					hmActivityMap.put("LEVEL_NAME", uF.showData(hmLevelMap.get(rs.getString("level_id")), ""));
					hmActivityMap.put("DESIG_NAME", uF.showData(hmDesig.get(rs.getString("desig_id")), ""));
					
					String strTransType = "";
					if(rs.getString("transfer_type") != null && rs.getString("transfer_type").equals("WL")){
						strTransType = "Work Location";
					} else if(rs.getString("transfer_type") != null && rs.getString("transfer_type").equals("DEPT")){
						strTransType = "Department";
					} else if(rs.getString("transfer_type") != null && rs.getString("transfer_type").equals("LE")){
						strTransType = "Legal Entity";
					}
					
					hmActivityMap.put("TRANSFER_TYPE", rs.getString("transfer_type"));
					hmActivityMap.put("TRANSFER_TYPE_NAME", strTransType);
					Map<String, String> hm =  hmWorkLocation.get(rs.getString("wlocation_id"));
					if(hm == null) hm = new HashMap<String, String>();					
					hmActivityMap.put("WORK_LOCATION_NAME", uF.showData(hm.get("WL_NAME"), ""));
					hmActivityMap.put("DEPARTMENT_NAME", uF.showData(hmDepartment.get(rs.getString("department_id")), ""));
					Map<String, String> hmOrg =  hmOrgMap.get(rs.getString("org_id"));
					if(hmOrg == null) hmOrg = new HashMap<String, String>();
					hmActivityMap.put("ORG_NAME", uF.showData(hmOrg.get("ORG_NAME"), ""));
					String serviceId = rs.getString("service_id")!= null && rs.getString("service_id").contains(",") ? rs.getString("service_id").substring(1,rs.getString("service_id").length()-1) : "";
					hmActivityMap.put("SERVICE_NAME", uF.showData(hmServices.get(serviceId), ""));
				}
				rs.close();
				pst.close();
//				System.out.println("hmActivityMap==>"+hmActivityMap);
				request.setAttribute("hmActivityMap", hmActivityMap);
			*/}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getLearningAndReviewData() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			pst = con.prepareStatement("select * from appraisal_details where ? between from_date and to_date");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
//			System.out.println("pst ========> "+pst);
			StringBuilder sbReviewOptions = new StringBuilder();
			List<String> memberList = new ArrayList<String>();
			while (rs.next()) {
				sbReviewOptions.append("<option value=\""+rs.getString("appraisal_details_id")+"\">"+rs.getString("appraisal_name")+"</option>");
			}
			rs.close();
			pst.close();

			StringBuilder sbLearningOptions = new StringBuilder();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select learning_plan_id,learning_plan_name from learning_plan_details where learning_plan_id in (select learning_plan_id from learning_plan_stage_details where (? between from_date and to_date) or from_date > ?)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
//				System.out.println("pst ========> " + pst);
			while (rs.next()) {
				sbLearningOptions.append("<option value=\""+rs.getString("learning_plan_id")+"\">"+rs.getString("learning_plan_name")+"</option>");
			}
			rs.close();
			pst.close();
			request.setAttribute("sbReviewOptions", sbReviewOptions.toString());
			request.setAttribute("sbLearningOptions", sbLearningOptions.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private String getLearningPlanName(Connection con, String lPlanIds) {
		PreparedStatement pst = null;
		ResultSet rs = null;
//		List<String> alLPlanName = new ArrayList<String>();
		StringBuilder sbLearningName = null;
		try {
			lPlanIds = lPlanIds.substring(1, lPlanIds.length()-1);
			pst = con.prepareStatement("select learning_plan_name from learning_plan_details where learning_plan_id in ("+lPlanIds+") ");
			rs = pst.executeQuery();
			System.out.println("pst ===>> " + pst);
			while(rs.next()) {
				if(sbLearningName==null) {
					sbLearningName = new StringBuilder();
					sbLearningName.append(rs.getString("learning_plan_name"));
				} else {
					sbLearningName.append(", "+rs.getString("learning_plan_name"));
				}
//				alLPlanName.add(rs.getString("learning_plan_name"));
			}
			rs.close();
			pst.close();
			if(sbLearningName==null) {
				sbLearningName = new StringBuilder();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbLearningName.toString();
	}
	
	
	private String getReviewName(Connection con, String reviewIds) {
		PreparedStatement pst = null;
		ResultSet rs = null;
//		List<String> reviewName = new ArrayList<String>();
		StringBuilder sbReviewName = null;
		try {
			if(reviewIds !=null && reviewIds.length()>0) {
				reviewIds = reviewIds.substring(1, reviewIds.length()-1);
			}
			pst = con.prepareStatement("select appraisal_name from appraisal_details where appraisal_details_id in ("+reviewIds+") ");
			rs = pst.executeQuery();
			System.out.println("pst ===>> " + pst);
			while(rs.next()) {
				if(sbReviewName==null) {
					sbReviewName = new StringBuilder();
					sbReviewName.append(rs.getString("appraisal_name"));
				} else {
					sbReviewName.append(", "+rs.getString("appraisal_name"));
				}
//				reviewName.add(rs.getString("appraisal_name"));
			}
			rs.close();
			pst.close();
			if(sbReviewName==null) {
				sbReviewName = new StringBuilder();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbReviewName.toString();
	}


	public void viewProfile(String strEmpIdReq) {
	
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
	
		try {
	
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpProfile =  CF.getEmpProfileDetail(con, request, session, CF, uF, null, strEmpIdReq);
			if(hmEmpProfile== null) hmEmpProfile = new HashMap<String, String>();
			request.setAttribute(TITLE, hmEmpProfile.get("NAME")+"'s Finalization");
			
			List<List<String>> alSkills = new ArrayList<List<String>>();
			alSkills = CF.selectSkills(con, uF.parseToInt(strEmpIdReq));
			request.setAttribute("alSkills", alSkills);
			
	//		request.setAttribute("alActivityDetails", alActivityDetails);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	//	return SUCCESS;
	}
	

	public String viewEmpActivity() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
	
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmServiceName = CF.getServicesMap(con, false);
			String educationsName = CF.getEmpEducations(con, getEmpid());
			pst = con.prepareStatement("select * from status_details s right join(select a.*,activity_code,activity_name from activity_details ad right join(" +
					"select a.*, org_name from org_details od right join(select a.*,wlocation_name from work_location_info wl right join(" +
					"select a.*,dept_name,dept_code from department_info d right join(select a.*,level_code,level_name from level_details ld right join(" +
					"select a.*,dd.designation_id,designation_code,designation_name,level_id from designation_details dd right join(" +
					"select * from grades_details gd right join(select a.activity_id,a.effective_date,a.emp_status_code,a.reason,a.entry_date," +
					"a.probation_period,a.notice_period,epd.*,eod.* from(select activity_id,effective_date,reason,emp_id,emp_status_code,entry_date," +
					"probation_period,notice_period from employee_activity_details where emp_id= ? and emp_activity_id=(select max(emp_activity_id) " +
					"from employee_activity_details where emp_id= ?)) as a ,employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id=eod.emp_id and a.emp_id=eod.emp_id) a on a.grade_id=gd.grade_id) a on a.designation_id=dd.designation_id" +
					") a on a.level_id=ld.level_id) a on a.depart_id=d.dept_id) a on a.wlocation_id=wl.wlocation_id) a on a.org_id = od.org_id" +
					") a on a.activity_id = ad.activity_id) a on a.emp_status_code=s.status_code order by effective_date desc, entry_date desc");
			pst.setInt(1, uF.parseToInt(getEmpid()));
			pst.setInt(2, uF.parseToInt(getEmpid()));
			rst = pst.executeQuery();
			String serviceIds = null;
			String joining_date = null;
			Map<String, String> hmEmpActivityDetails = new HashMap<String, String>();
			Map<String, String> hmEmpLevel = CF.getEmpLevelMap(con);
			while (rst.next()) {
				serviceIds = rst.getString("service_id");
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rst.getString("emp_mname");
					}
				}
				
				hmEmpActivityDetails.put("FULL_NAME", rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname"));
				hmEmpActivityDetails.put("EFFECTIVE_DATE", uF.getDateFormat(rst.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()));
				hmEmpActivityDetails.put("JOINING_DATE", uF.getDateFormat(rst.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));
				hmEmpActivityDetails.put("LEVEL_ID", hmEmpLevel.get(getEmpid()));
				hmEmpActivityDetails.put("LEVEL", "["+uF.showData(rst.getString("level_code"), "")+"] "+uF.showData(rst.getString("level_name"), ""));
				hmEmpActivityDetails.put("GRADE", "["+uF.showData(rst.getString("grade_code"), "")+"] "+uF.showData(rst.getString("grade_name"), ""));
				hmEmpActivityDetails.put("DESIGNATION", "["+uF.showData(rst.getString("designation_code"), "")+"] "+uF.showData(rst.getString("designation_name"), ""));
				hmEmpActivityDetails.put("WLOCATION", rst.getString("wlocation_name"));
				hmEmpActivityDetails.put("ORG_NAME", rst.getString("org_name"));
				hmEmpActivityDetails.put("DEPT", rst.getString("dept_name"));
				hmEmpActivityDetails.put("STATUS", rst.getString("status_name"));
	//			hmEmpActivityDetails.put("ACTIVITY_STATUS", rst.getString("activity_name"));
				hmEmpActivityDetails.put("REASON", rst.getString("reason"));
				hmEmpActivityDetails.put("PROBATION_PERIOD", rst.getString("probation_period"));
				hmEmpActivityDetails.put("NOTICE_PERIOD", rst.getString("notice_period"));
				
				joining_date = rst.getString("joining_date");
			}
			rst.close();
			pst.close();
			
			hmEmpActivityDetails.put("EMP_ID", getEmpid());
			
			request.setAttribute("educationsName", educationsName);
			request.setAttribute("hmEmpActivityDetails", hmEmpActivityDetails);
						
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	

	private boolean insertComment() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
 
		Database db = new Database();
		db.setRequest(request);
		boolean flag = false;
		try {
			con = db.makeConnection(con);    

			StringBuilder sbLearningIds = null;
			if(getLearningIds() !=null) {
				for(int i=0; i<getLearningIds().length; i++) {
					if(sbLearningIds==null) {
						sbLearningIds = new StringBuilder();
						sbLearningIds.append(","+getLearningIds()[i]+",");
					} else {
						sbLearningIds.append(getLearningIds()[i]+",");
					}
				}
			}
			
			StringBuilder sbReviewIds = null;
			if(getReviewIds() !=null) {
				for(int i=0; i<getReviewIds().length; i++) {
					if(sbReviewIds==null) {
						sbReviewIds = new StringBuilder();
						sbReviewIds.append(","+getReviewIds()[i]+",");
					} else {
						sbReviewIds.append(getReviewIds()[i]+",");
					}
				}
			}
			if(sbLearningIds==null) {
				sbLearningIds = new StringBuilder();
			}
			if(sbReviewIds==null) {
				sbReviewIds = new StringBuilder();
			}
			
			pst = con.prepareStatement("insert into appraisal_final_sattlement(emp_id,incumbent_emp_id,user_id,sattlement_comment,if_approved,_date,activity_id1,desig_id," +
					"areas_of_strength,areas_of_development,learning_ids,review_ids)values(?,?,?,? ,?,?,?,? ,?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getEmpid()));
			pst.setInt(2, uF.parseToInt(getStrIncumbentEmpId()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setString(4, getRemark());
			pst.setBoolean(5, true);
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(7, uF.parseToInt(ACTIVITY_SUCCESSION_PLAN_ID));
			pst.setInt(8, uF.parseToInt(getStrDesignation()));
			pst.setString(9, getAreasOfStrength());
			pst.setString(10, getAreasOfDevelopment());
			pst.setString(11, sbLearningIds.toString());
			pst.setString(12, sbReviewIds.toString());
//			System.out.println("remark pst==>"+pst);
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0){
				flag = true;
			}
			
//			System.out.println("flag==>"+flag+"==>getSendtoGapStatus==>"+ uF.parseToBoolean(getSendtoGapStatus()));
			
			/*if(flag) {
				Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
				String reviewName = CF.getReviewNameById(con, uF, null);
				
				String alertData = "<div style=\"float: left;\"> A Review ("+reviewName+") is finalized of ("+CF.getEmpNameMapByEmpId(con, getEmpid())+") by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
				String alertAction = "Reviews.action?pType=WR";
				String strDomain = request.getServerName().split("\\.")[0];
				UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(getEmpid());
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
				userAlerts.setCurrUserTypeID(hmUserTypeId.get(ADMIN));
				userAlerts.setStatus(INSERT_WR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return flag;
	}
	
	
	
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
		
	}
	
	public String getSendtoGapStatus() {
		return sendtoGapStatus;
	}

	public void setSendtoGapStatus(String sendtoGapStatus) {
		this.sendtoGapStatus = sendtoGapStatus;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getEmpid() {
		return empid;
	}

	public void setEmpid(String empid) {
		this.empid = empid;
	}


	String strDomain;
	public void setDomain(String strDomain) {
		this.strDomain=strDomain;
	}

	public String getStrDomain() {
		return strDomain;
	}

	public String getRemarktype() {
		return remarktype;
	}

	public void setRemarktype(String remarktype) {
		this.remarktype = remarktype;
	}
	
	
	public String getStrDesignation() {
		return strDesignation;
	}
	public void setStrDesignation(String strDesignation) {
		this.strDesignation = strDesignation;
	}
	
	public String getStrReason() {
		return strReason;
	}
	public void setStrReason(String strReason) {
		this.strReason = strReason;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getAreasOfStrength() {
		return areasOfStrength;
	}

	public void setAreasOfStrength(String areasOfStrength) {
		this.areasOfStrength = areasOfStrength;
	}

	public String getAreasOfDevelopment() {
		return areasOfDevelopment;
	}

	public void setAreasOfDevelopment(String areasOfDevelopment) {
		this.areasOfDevelopment = areasOfDevelopment;
	}

	public String[] getLearningIds() {
		return learningIds;
	}

	public void setLearningIds(String[] learningIds) {
		this.learningIds = learningIds;
	}

	public String getStrIncumbentEmpId() {
		return strIncumbentEmpId;
	}

	public void setStrIncumbentEmpId(String strIncumbentEmpId) {
		this.strIncumbentEmpId = strIncumbentEmpId;
	}

	public String[] getReviewIds() {
		return reviewIds;
	}

	public void setReviewIds(String[] reviewIds) {
		this.reviewIds = reviewIds;
	}

}