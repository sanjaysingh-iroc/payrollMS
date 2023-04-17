package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

public class AppraisalRemark extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId;
	String strUserType;
	String strUserTypeId; 
	String strSessionWLocation;
	String strSessionOrgId;
	CommonFunctions CF;
	
	private String id;
	private String empid;
	private String remark;
	private String areasOfStrength; 
	private String areasOfDevelopment;
	private String thumbsFlag;
	private String remarktype;	
	private String sendtoGapStatus;
	
	private String[] salary_head_id;
	private String[] salary_head_value;
	private String[] isDisplay;
	private String[] hideIsDisplay;
	private String[] emp_salary_id;	 	
	private String f_strWLocation;
	private String f_department;
	private String f_level;	
	private String strEmpId;
	private String strEmpId2;
	private String effectiveDate;
	private String strActivity;
	private String strOrganisation;
	private String strWLocation;
	private String strSBU;
	private String strDepartment;
	private String strLevel;
	private String strDesignation;
	private String strDesignationUpdate;
	private String strGrade;
	private String empGrade;
	private String strNoticePeriod;
	private String strProbationPeriod;
	private String strExtendProbationDays;
	private String strIncrementType;
	private String strIncrementPercentage;	
	private String strNewStatus;
	private String strReason;	
	private String strJoiningDate;
	private boolean emailNotification;
	private String strUpdate;
	private String f_org;
	private String appraisal_id;
	private String empChangeGrade;
	private String appraisal_freq; 
	private List<FillSalaryHeads> salaryHeadList;
	private List<List<String>> al = new ArrayList<List<String>>();
	
	private List<FillOrganisation> organisationList;
	private List<FillOrganisation> organisationList1;
	private List<FillActivity> activityList;
	private List<FillWLocation> wLocationList;
	private List<FillWLocation> wLocationList1;
	private List<FillDepartment> departmentList;
	private List<FillDepartment> departmentList1;
	private List<FillServices> serviceList1;
	private List<FillLevel> levelList;
	private List<FillLevel> levelList1;
	private List<FillDesig> desigList;
	private List<FillGrade> gradeList;
	private List<FillGrade> gradeChangeList;
	private List<FillEmpStatus> empStatusList;
	private List<FillEmployee> empList;
	
	private String dataType;   
	private String strUpdateDocument;
	
	private String strTransferType;
	private String appFreqId;
	private String cancel;
	private String fromPage;
	private String recommendationOrFinalization;
	private String[] learningIds;
	
	private boolean disableSalaryStructure;
	
	String anscomment;	//added by parvez date: 21-03-2023
	
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

		request.setAttribute(PAGE, "/jsp/performance/AppraisalRemark.jsp");
		request.setAttribute(TITLE, "Finalization");
		request.setAttribute("empid", getStrEmpId());
		
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		
		if(getF_strWLocation()==null){
			setF_strWLocation((String)session.getAttribute(WLOCATIONID));
		}
		
		if(getF_org()==null){
			setF_org(strSessionOrgId);
		}
		
//		System.out.println("getRecommendationOrFinalization() ===>> " + getRecommendationOrFinalization());
		if(getDataType()!=null && getDataType().equals("UFR")){
			insertFinalReviewRating(uF);
			return "success";
		}
		
		if(getRecommendationOrFinalization() !=null && getRecommendationOrFinalization().equals("RECOMMENDATION")) {
			getAppraisalData();
			getAppraiseeDetailsAndScore();
			String submit = request.getParameter("submit");
			if (submit != null) {
				boolean flag = insertRecommendation();
				if(flag) {
					return "success";
				} else {
					return ERROR;
				}
			} else {
				getRecommendation();
				if(uF.parseToInt(getRemarktype()) == 1) {
					return "view";
				} else {	
					return LOAD;
				}
			}
			
		} else {
			String submit = request.getParameter("submit");
			String submit1 = request.getParameter("submit1");
	
			if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")) {
				setDataType("A");    
			}
			
			loadValidateEmpActivity();
	//		System.out.println("EmpId==>"+getEmpid()+"==>getStrEmpId==>"+getStrEmpId()+"==>getStrEmpId2==>"+getStrEmpId2());		
			getRecommendation();
			getAppraisalData();
			getAppraiseeDetailsAndScore();
			getOneOneDiscussionDetails(uF);
			Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
			if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
			
			if (submit != null || submit1 != null) {
				boolean flag = insertComment();
				if(flag) {
					if(uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_ACTIVITY_REVIEW_FINALIZE))) {
						insertEmployeeActivity();
					}
					return "success";
				} else {
					return ERROR;
				}
			} else {
				getRemarks();
				if(uF.parseToInt(getRemarktype()) == 1) {
					return "view";
				} else {	
					return LOAD;
				}
			}
		}
		
	}
	
	

	private void getRecommendation() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		Database db = new Database();
		db.setRequest(request);
		String managerRemark = null;
		String areasOfStrength = null;
		String areasOfDevelopment = null;
		List<String> alLearningPlans = new ArrayList<String>();
		String strRecommendBy = null;
		boolean flag = false;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("select areas_of_strength,areas_of_development,recommendation_comment,user_id,emp_fname,emp_mname, emp_lname " +
				" from review_final_recommendation afs, employee_personal_details epd where afs.user_id = epd.emp_per_id and emp_id=? and review_id=? and review_freq_id =?");
//			pst=con.prepareStatement(selectFinalSattlement);
			pst.setInt(1, uF.parseToInt(getEmpid()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
//			System.out.println("getRemarks pst ==> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				managerRemark = rs.getString("recommendation_comment");
				if (managerRemark != null) {
					managerRemark = managerRemark.replace("\n", "<br/>");
				}
				areasOfStrength = rs.getString("areas_of_strength");
				if (areasOfStrength != null) {
					areasOfStrength = areasOfStrength.replace("\n", "<br/>");
				}
				areasOfDevelopment = rs.getString("areas_of_development");
				if (areasOfDevelopment != null) {
					areasOfDevelopment = areasOfDevelopment.replace("\n", "<br/>");
				}
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				strRecommendBy = rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname");
				flag = true;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("managerRemark", managerRemark);
			request.setAttribute("areasOfStrength", areasOfStrength);
			request.setAttribute("areasOfDevelopment", areasOfDevelopment);
			request.setAttribute("alLearningPlans", alLearningPlans);
			request.setAttribute("flag", flag);
			request.setAttribute("strRecommendBy", strRecommendBy);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}



	private boolean insertRecommendation() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
 
		Database db = new Database();
		db.setRequest(request);
		boolean flag = false;
		try {
			con = db.makeConnection(con);    

			pst = con.prepareStatement("delete from review_final_recommendation where emp_id=? and review_id=? and review_freq_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpid()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("insert into review_final_recommendation (emp_id,review_id,user_id,recommendation_comment,entry_date,review_freq_id," +
					"areas_of_strength,areas_of_development)values(?,?,?,? ,?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getEmpid()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setString(4, getRemark());
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt(getAppFreqId()));
			pst.setString(7, getAreasOfStrength());
			pst.setString(8, getAreasOfDevelopment());
//			System.out.println("remark pst==>"+pst);
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0) {
				flag = true;
			}
			
//			System.out.println("flag==>"+flag+"==>getSendtoGapStatus==>"+ uF.parseToBoolean(getSendtoGapStatus()));
			
			/*if(flag) {
				Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
				String reviewName = CF.getReviewNameById(con, uF, getId());
				
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



	private void getAppraisalData() {
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
			
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
//			System.out.println("pst ========> "+pst);
			String oriented_type="";
			List<String> memberList = new ArrayList<String>();
			while (rs.next()) {
				oriented_type = rs.getString("oriented_type");
				memberList = Arrays.asList(rs.getString("usertype_member").split(","));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select distinct(attribute_id) as attribute_id from appraisal_main_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
//			System.out.println("pst ========> "+pst);
			List<String> attributeIds = new ArrayList<String>();
			while (rs.next()) {
				attributeIds.add(rs.getString("attribute_id"));
			}
			rs.close();
			pst.close();

			StringBuilder sbOptions = new StringBuilder();
			if(attributeIds != null && attributeIds.size()>0) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select learning_plan_id,learning_plan_name from learning_plan_details where learning_plan_id > 0 ");
                sbQuery.append(" and (");
                for(int i=0; i<attributeIds.size(); i++) {
                    sbQuery.append(" attribute_id like '%,"+attributeIds.get(i)+",%'");
                    if(i<attributeIds.size()-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
//				System.out.println("pst ========> " + pst);
				while (rs.next()) {
					sbOptions.append("<option value=\""+rs.getString("learning_plan_id")+"\">"+rs.getString("learning_plan_name")+"</option>");
				}
				rs.close();
				pst.close();
			}
			
			StringBuilder sbQuery = new StringBuilder();
//			@DT:29-07-21 Rahul Patil - user_type_id=2 
			sbQuery.append("select * from reviewee_strength_improvements where user_type_id=2 and review_id=? and review_freq_id=? and emp_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			pst.setInt(3, uF.parseToInt(getEmpid()));
			rs = pst.executeQuery();
//			System.out.println("pst ========> " + pst);
			StringBuilder sbAreaOfStrength = null;
			StringBuilder sbAreaOfImprovement = null;
			while (rs.next()) {
				if(sbAreaOfStrength==null) {
					sbAreaOfStrength = new StringBuilder();
					sbAreaOfStrength.append(rs.getString("areas_of_strength"));
				} else {
					sbAreaOfStrength.append("\n"+rs.getString("areas_of_strength"));
				}
				if(sbAreaOfImprovement==null) {
					sbAreaOfImprovement = new StringBuilder();
					sbAreaOfImprovement.append(rs.getString("areas_of_improvement"));
				} else {
					sbAreaOfImprovement.append("\n"+rs.getString("areas_of_improvement"));
				}
				
			}
			rs.close();
			pst.close();
			if(sbAreaOfStrength==null) {
				sbAreaOfStrength = new StringBuilder();
			}
			if(sbAreaOfImprovement==null) {
				sbAreaOfImprovement = new StringBuilder();
			}
			setAreasOfStrength(sbAreaOfStrength.toString());
			setAreasOfDevelopment(sbAreaOfImprovement.toString());
			
//			List<String> memberList = CF.getOrientationMemberDetails(con,uF.parseToInt(oriented_type));
			Map<String, String> orientationMemberMp = getOrientationMember();
//			System.out.println("memberList ==>" + memberList);
			// request.setAttribute("hmUserTypeID", hmUserTypeID);
			request.setAttribute("memberList", memberList);
			request.setAttribute("orientationMemberMp", orientationMemberMp);
			request.setAttribute("sbOptions", sbOptions.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	private Map<String, String> getOrientationMember() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> orientationMemberMp = new HashMap<String, String>();
		try {
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
		return orientationMemberMp;
	}
	
	
	private void getAppraiseeDetailsAndScore() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			String appraiseeName = CF.getEmpNameMapByEmpId(con, getEmpid());
			
	//===start parvez date: 21-03-2022===
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con,request);
			
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
			List<String> alFeatureUserTypeId = hmFeatureUserTypeId.get(F_ENABLE_BALANCE_SCORECARD_CALCULATION_USERTYPE);
			
			boolean isUserTypeRating = uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_BALANCE_SCORECARD_CALCULATION_USERTYPE));
			
			boolean isSelfRating = uF.parseToBoolean(hmFeatureStatus.get(F_DISABLE_SELF_APPRAISAL_RATING_DURING_FINAL_RATING_CALCULATION));
			
			Map<String, String> hmRevieweeId = CF.getAppraisalRevieweesId(con, uF);
			if(hmRevieweeId == null) hmRevieweeId = new HashMap<String, String>();
	//===end parvez date: 21-03-2022===	
			
			/*pst = con.prepareStatement("select *,(marks*100/weightage) as average from(select sum(marks) as marks ,sum(weightage) as weightage," +
					" user_type_id,emp_id from appraisal_question_answer where appraisal_id=? and appraisal_freq_id=? and emp_id=? and weightage>0 " +
					" group by user_type_id,emp_id) as a order by emp_id ");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			pst.setInt(3, uF.parseToInt(getEmpid()));
			rs = pst.executeQuery();
		//	System.out.println("pst ========> "+pst);
			Map<String, String> outerMp = new HashMap<String, String>();
			double dblTotalMarks = 0; 
			double dblTotalWeightage = 0;
			while (rs.next()) {
				dblTotalMarks += uF.parseToDouble(rs.getString("marks"));
		//		System.out.println("dblTotalMarks"+dblTotalMarks);
				dblTotalWeightage += uF.parseToDouble(rs.getString("weightage"));
				outerMp.put(rs.getString("user_type_id"), uF.formatIntoTwoDecimal(rs.getDouble("average")));
				if(dblTotalWeightage>0){
					outerMp.put("AGGREGATE", uF.formatIntoTwoDecimal((dblTotalMarks * 100)/dblTotalWeightage));
				}
			}
			rs.close();
			pst.close();*/
			
			
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 01-03-2023===	
			/*sbQuery.append("select *,(marks*100/weightage) as average from (select sum(aqa.marks) as marks, sum(aqa.weightage) as weightage,aqa.user_type_id,"
					+ " aqa.emp_id,amld.main_level_id,section_weightage "
					+ " from appraisal_question_answer aqa ,appraisal_main_level_details amld where aqa.appraisal_id=? and aqa.appraisal_freq_id=? and aqa.weightage>0 and "
					+ " aqa.is_submit=true and aqa.section_id=amld.main_level_id");*/
			/*sbQuery.append("select *,(marks*100/weightage) as average from (select sum(aqa.marks) as marks, sum(aqa.weightage) as weightage,aqa.user_type_id,"
					+ " aqa.emp_id,aqa.appraisal_id,amld.main_level_id,section_weightage,amld.hr,amld.manager,amld.peer,amld.hod,amld.ceo,amld.subordinate,amld.other_peer "
					+ " from appraisal_question_answer aqa ,appraisal_main_level_details amld where aqa.appraisal_id=? and aqa.appraisal_freq_id=? and aqa.weightage>0 and "
					+ " aqa.is_submit=true and aqa.section_id=amld.main_level_id");
			sbQuery.append(" and emp_id in (" + getEmpid() + ")");
			sbQuery.append(" group by aqa.user_type_id,aqa.emp_id,aqa.appraisal_id,amld.main_level_id) as a order by emp_id,main_level_id,user_type_id");*/
			
			sbQuery.append("select *,(marks*100/weightage) as average from (select sum(aqa.marks) as marks, sum(aqa.weightage) as weightage,aqa.user_type_id,aqa.user_id,"
					+ " aqa.emp_id,aqa.appraisal_id,amld.main_level_id,section_weightage,amld.hr,amld.manager,amld.peer,amld.hod,amld.ceo,amld.subordinate,amld.other_peer,score_calculation_basis "
					+ " from appraisal_question_answer aqa ,appraisal_main_level_details amld,appraisal_question_details aqd where aqa.appraisal_id=? and aqa.appraisal_freq_id=? and aqa.weightage>0 and "
					+ " aqa.is_submit=true and aqa.section_id=amld.main_level_id");
			
			sbQuery.append(" and emp_id in (" + getEmpid() + ") and aqa.appraisal_question_details_id=aqd.appraisal_question_details_id");
			sbQuery.append(" group by aqa.user_type_id,aqa.user_id,aqa.emp_id,aqa.appraisal_id,amld.main_level_id, score_calculation_basis) as a order by emp_id,main_level_id,user_type_id");
		//===end parvez date: 01-03-2023===	
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
//			System.out.println("ApRm/573---pst2 ====> " + pst);
			rs = pst.executeQuery();
			
//			Map<String, Map<String, List<String>>> outerMp1 = new HashMap<String, Map<String, List<String>>>();
			Map<String, Map<String, Map<String, List<String>>>> outerMp1 = new HashMap<String, Map<String, Map<String, List<String>>>>();
			
			Map<String, String> hmPriorUser = new HashMap<String, String>();
			
			while (rs.next()) {
			//===start parvez date: 18-03-2023===	
				Map<String, Map<String, List<String>>> hmTempLevel = outerMp1.get(rs.getString("user_type_id"));
				if (hmTempLevel == null)
					hmTempLevel = new HashMap<String, Map<String, List<String>>>();
				
//				Map<String, List<String>> hmMainLevel = outerMp1.get(rs.getString("user_type_id"));
//				if(hmMainLevel==null) hmMainLevel = new HashMap<String, List<String>>();
				
				Map<String, List<String>> hmMainLevel = hmTempLevel.get(rs.getString("user_id"));
				if(hmMainLevel==null) hmMainLevel = new HashMap<String, List<String>>();
			//===end parvez date: 18-03-2023===	
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("average"));
			//===start parvez date: 17-03-2023===	
//				innerList.add(rs.getString("section_weightage"));
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) 
						&&(uF.parseToInt(rs.getString("user_type_id"))==4 || uF.parseToInt(rs.getString("user_type_id"))==14 || uF.parseToInt(rs.getString("user_type_id"))==13)){
					if(uF.parseToInt(rs.getString("section_weightage"))<100){
						innerList.add(100+"");
					} else{
						innerList.add(rs.getString("section_weightage"));
					}
				} else{
					innerList.add(rs.getString("section_weightage"));
				}
			//===end parvez date: 17-03-2023===	
				
				innerList.add(rs.getString("score_calculation_basis"));		//added by parvez date:01-03-2023
				hmMainLevel.put(rs.getString("main_level_id"), innerList);
				
			//===start parvez date: 18-03-2023===	
				hmTempLevel.put(rs.getString("user_id"), hmMainLevel);
			//===end parvez date: 18-03-2023===	
				
		//===start parvez date: 21-03-2022===		
				List<String> alPriorUserList = new ArrayList<String>();
				
				if(hmRevieweeId.get(rs.getString("appraisal_id")+"_"+rs.getString("emp_id")+"_SUPERVISOR") != null && !hmRevieweeId.get(rs.getString("appraisal_id")+"_"+rs.getString("emp_id")+"_SUPERVISOR").equals("")){
					alPriorUserList.add(rs.getString("manager"));
				}
				if(hmRevieweeId.get(rs.getString("appraisal_id")+"_"+rs.getString("emp_id")+"_HOD") != null && !hmRevieweeId.get(rs.getString("appraisal_id")+"_"+rs.getString("emp_id")+"_HOD").equals("")){
					alPriorUserList.add(rs.getString("hod"));
				}
				if(hmRevieweeId.get(rs.getString("appraisal_id")+"_"+rs.getString("emp_id")+"_CEO") != null && !hmRevieweeId.get(rs.getString("appraisal_id")+"_"+rs.getString("emp_id")+"_CEO").equals("")){
					alPriorUserList.add(rs.getString("ceo"));
				}
				if(hmRevieweeId.get(rs.getString("appraisal_id")+"_"+rs.getString("emp_id")+"_HR") != null && !hmRevieweeId.get(rs.getString("appraisal_id")+"_"+rs.getString("emp_id")+"_HR").equals("")){
					alPriorUserList.add(rs.getString("hr"));
				}
				if(hmRevieweeId.get(rs.getString("appraisal_id")+"_"+rs.getString("emp_id")+"_PEER") != null && !hmRevieweeId.get(rs.getString("appraisal_id")+"_"+rs.getString("emp_id")+"_PEER").equals("")){
					alPriorUserList.add(rs.getString("peer"));
				}
				if(hmRevieweeId.get(rs.getString("appraisal_id")+"_"+rs.getString("emp_id")+"_OTHER_PEER") != null && !hmRevieweeId.get(rs.getString("appraisal_id")+"_"+rs.getString("emp_id")+"_OTHER_PEER").equals("")){
					alPriorUserList.add(rs.getString("other_peer"));
				}
				if(hmRevieweeId.get(rs.getString("appraisal_id")+"_"+rs.getString("emp_id")+"_SUBORDINATE") != null && !hmRevieweeId.get(rs.getString("appraisal_id")+"_"+rs.getString("emp_id")+"_SUBORDINATE").equals("")){
					alPriorUserList.add(rs.getString("subordinate"));
				}
				
				Collections.sort(alPriorUserList,Collections.reverseOrder());
				
//				System.out.println("ApR/604--alPriorUserList="+alPriorUserList);
				int priorUserTypeId = 0;
				
			//===start parvez date: 23-03-2022===	
				if(alPriorUserList != null && !alPriorUserList.isEmpty()){
					if(uF.parseToInt(rs.getString("manager")) == uF.parseToInt(alPriorUserList.get(0))){
						priorUserTypeId = 2;
					} else if(uF.parseToInt(rs.getString("hod")) == uF.parseToInt(alPriorUserList.get(0))){
						priorUserTypeId = 13;
					} else if(uF.parseToInt(rs.getString("ceo")) == uF.parseToInt(alPriorUserList.get(0))){
						priorUserTypeId = 5;
					} else if(uF.parseToInt(rs.getString("hr")) == uF.parseToInt(alPriorUserList.get(0))){
						priorUserTypeId = 7;
					} else if(uF.parseToInt(rs.getString("peer")) == uF.parseToInt(alPriorUserList.get(0))){
						priorUserTypeId = 4;
					} else if(uF.parseToInt(rs.getString("other_peer")) == uF.parseToInt(alPriorUserList.get(0))){
						priorUserTypeId = 14;
					} else if(uF.parseToInt(rs.getString("subordinate")) == uF.parseToInt(alPriorUserList.get(0))){
						priorUserTypeId = 6;
					}
				}
		//===end parvez date: 23-03-2022===	
				
				
				hmPriorUser.put(rs.getString("user_type_id"), priorUserTypeId+"");
		//===end parvez date: 21-03-2022===		
			
			//===start parvez date: 18-03-2023===	
//				outerMp1.put(rs.getString("user_type_id"), hmMainLevel);
				outerMp1.put(rs.getString("user_type_id"), hmTempLevel);
			//===end parvez date: 18-03-2023===	
			}
			rs.close();
			pst.close();
//			System.out.println(" JSP outerMp1 ===>> " + outerMp1);
			
			Map<String, String> hmFinalScore = new HashMap<String, String>();
			
			//===start parvez date: 18-03-2023===
			if(uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) || uF.parseToBoolean(hmFeatureStatus.get(F_HR_GHR_APPROVAL_FOR_FINAL_RATING_AND_COMMENT))){
				sbQuery = new StringBuilder();
				sbQuery.append("select distinct(user_type_id) as user_type_id,user_id,rfd.emp_id,reviewer_comment,reviewer_marks,section_weightage,main_level_id from " +
						" reviewer_feedback_details rfd,appraisal_main_level_details amld where rfd.appraisal_id=amld.appraisal_id and rfd.appraisal_id=? and rfd.appraisal_freq_id=? ");
				sbQuery.append(" and emp_id in (" + getEmpid() + ")");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getId()));
				pst.setInt(2, uF.parseToInt(getAppFreqId()));
//				System.out.println("ApR/701---pst=="+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					
					Map<String, Map<String, List<String>>> hmTempLevel = outerMp1.get(rs.getString("user_type_id"));
					if (hmTempLevel == null)
						hmTempLevel = new HashMap<String, Map<String, List<String>>>();
					
//					Map<String, List<String>> hmMainLevel = outerMp1.get(rs.getString("user_type_id"));
//					if(hmMainLevel==null) hmMainLevel = new HashMap<String, List<String>>();
					
					Map<String, List<String>> hmMainLevel = hmTempLevel.get(rs.getString("user_id"));
					if(hmMainLevel==null) hmMainLevel = new HashMap<String, List<String>>();
					
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("reviewer_marks"));
					innerList.add(rs.getString("section_weightage"));
					hmMainLevel.put(rs.getString("main_level_id"), innerList);
					
				//===start parvez date: 18-03-2023===	
					hmTempLevel.put(rs.getString("user_id"), hmMainLevel);
//					outerMp1.put(rs.getString("user_type_id"), hmMainLevel);
					outerMp1.put(rs.getString("user_type_id"), hmTempLevel);
				//===end parvez date: 18-03-2023===	
					hmFinalScore.put(rs.getString("emp_id")+"_MARK", rs.getString("reviewer_marks"));
					hmFinalScore.put(rs.getString("emp_id")+"_COMMENT", rs.getString("reviewer_comment"));
					
				}
				rs.close();
				pst.close();
			}
	//===end parvez date: 18-03-2023===
			
			pst = con.prepareStatement("select reviewee_id,subordinate_weightage,peer_weightage,other_peer_weightage,supervisor_weightage,grand_supervisor_weightage,hod_weightage,ceo_weightage,hr_weightage,other_weightage " + 
					" from appraisal_reviewee_details where appraisal_id=? and reviewee_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpid()));
//			System.out.println("PST === > "+pst.toString());
			rs = pst.executeQuery();
			Map<String, String> hmUsertypeW = new HashMap<String, String>();
			while (rs.next()) {
				if (uF.parseToInt(rs.getString("supervisor_weightage")) >0) {
					hmUsertypeW.put("2", rs.getString("supervisor_weightage"));
				}
				if (uF.parseToInt(rs.getString("peer_weightage")) >0) {
					hmUsertypeW.put("4", rs.getString("peer_weightage"));
				}
				if (uF.parseToInt(rs.getString("ceo_weightage")) >0) {
					hmUsertypeW.put("5", rs.getString("ceo_weightage"));
				}
				if (uF.parseToInt(rs.getString("subordinate_weightage")) >0) {
					hmUsertypeW.put("6", rs.getString("subordinate_weightage"));
				}
				if (uF.parseToInt(rs.getString("hr_weightage")) >0) {
					hmUsertypeW.put("7", rs.getString("hr_weightage"));
				}
				if (uF.parseToInt(rs.getString("grand_supervisor_weightage")) >0) {
					hmUsertypeW.put("8", rs.getString("grand_supervisor_weightage"));
				}
				if (uF.parseToInt(rs.getString("other_weightage")) >0) {
					hmUsertypeW.put("10", rs.getString("other_weightage"));
				}
				if (uF.parseToInt(rs.getString("hod_weightage")) >0) {
					hmUsertypeW.put("13", rs.getString("hod_weightage"));
				}
				if (uF.parseToInt(rs.getString("other_peer_weightage")) >0) {
					hmUsertypeW.put("14", rs.getString("other_peer_weightage"));
				}
			}
			rs.close();
			pst.close();
			
			Map<String, String> outerMp = new HashMap<String, String>();
			Iterator<String> it1 = outerMp1.keySet().iterator();
			double dblAllUsertypeAvg = 0;
			int intUsertypeCnt=0;
			boolean actualCalBasis = false;
		//===start parvez date: 18-03-2023===	
			/*while (it1.hasNext()) {
				String strUsertypeId = it1.next();
				Map<String, List<String>> hmMainLevel = outerMp1.get(strUsertypeId);
				Iterator<String> it2 = hmMainLevel.keySet().iterator();
				double dblUsertypeAvg = 0;
				while (it2.hasNext()) {
					String strLevelId = it2.next();
					List<String> innList = hmMainLevel.get(strLevelId);
					String strAvg = innList.get(0);
					String strWtg = innList.get(1);
					double dblLevelAvg = (uF.parseToDouble(strAvg)*uF.parseToDouble(strWtg)) /100;
					dblUsertypeAvg += dblLevelAvg;
				//===start parvez date: 01-03-2023===	
					if(innList.size()>2 && innList.get(2)!=null && uF.parseToBoolean(innList.get(2))){
						actualCalBasis = true;
					}else{
						actualCalBasis = false;
					}
				//===end parvez date: 01-03-2023===	
				}
				outerMp.put(strUsertypeId, uF.formatIntoTwoDecimal(dblUsertypeAvg));
				
				outerMp.put("ACTUAL_CAL_BASIS", actualCalBasis+"");
				
				
//				if(uF.parseToInt(strUsertypeId) != 3) {
//					System.out.println("ApR/630--strUsertypeId="+strUsertypeId);
//					if(hmUsertypeW!=null && uF.parseToDouble(hmUsertypeW.get(strUsertypeId)) > 0) {
//						dblAllUsertypeAvg += (dblUsertypeAvg * uF.parseToDouble(hmUsertypeW.get(strUsertypeId))) / 100;
//					} else {
//						dblAllUsertypeAvg += dblUsertypeAvg;
//						intUsertypeCnt++;
//					}
//				}
				
				if(uF.parseToInt(strUsertypeId) != 3 || !isSelfRating) {
					
					if(!isUserTypeRating || (isUserTypeRating && alFeatureUserTypeId.contains(strUsertypeId) && uF.parseToInt(strUsertypeId) == uF.parseToInt(hmPriorUser.get(strUsertypeId)))){
//						System.out.println("ApR/696--strUsertypeId="+strUsertypeId+"---priorUser="+hmPriorUser.get(strUsertypeId));
						if(hmUsertypeW!=null && uF.parseToDouble(hmUsertypeW.get(strUsertypeId)) > 0) {
							dblAllUsertypeAvg += (dblUsertypeAvg * uF.parseToDouble(hmUsertypeW.get(strUsertypeId))) / 100;
						} else {
							dblAllUsertypeAvg += dblUsertypeAvg;
							intUsertypeCnt++;
						}
					}
					
				}
				
			}*/
			
			boolean markApprovedFlag = false;
			while (it1.hasNext()) {
				String strUsertypeId = it1.next();
				
				Map<String, Map<String, List<String>>> hmTempLevel = outerMp1.get(strUsertypeId);
				Iterator<String> it2 = hmTempLevel.keySet().iterator();
				double dblUsertypeAvg = 0;
				int intUserCnt = 0;
				while (it2.hasNext()) {
					String strUserId = it2.next();
					Map<String, List<String>> hmMainLevel = hmTempLevel.get(strUserId);
					Iterator<String> it3 = hmMainLevel.keySet().iterator();
					
					while (it3.hasNext()) {
						String strLevelId = it3.next();
						List<String> innList = hmMainLevel.get(strLevelId);
						String strAvg = innList.get(0);
						String strWtg = innList.get(1);
						double dblLevelAvg = (uF.parseToDouble(strAvg)*uF.parseToDouble(strWtg)) /100;
						dblUsertypeAvg += dblLevelAvg;
					//===start parvez date: 01-03-2023===	
						if(innList.size()>2 && innList.get(2)!=null && uF.parseToBoolean(innList.get(2))){
							actualCalBasis = true;
						}else{
							actualCalBasis = false;
						}
					//===end parvez date: 01-03-2023===	
					}
					
					intUserCnt++;
					if (uF.parseToInt(strUsertypeId) != 3 || !isSelfRating) {
						if(!isUserTypeRating || (isUserTypeRating && alFeatureUserTypeId.contains(strUsertypeId) && uF.parseToInt(strUsertypeId) == uF.parseToInt(hmPriorUser.get(strUsertypeId)))){
							intUsertypeCnt++;
						}
					}
				}
				
//				outerMp.put(strUsertypeId, uF.formatIntoTwoDecimal(dblUsertypeAvg));
				
				if(intUserCnt==0){
					outerMp.put(strUsertypeId, uF.formatIntoTwoDecimal(dblUsertypeAvg));
				} else{
					double agDblUsertypeAvg = dblUsertypeAvg / intUserCnt;
					outerMp.put(strUsertypeId, uF.formatIntoTwoDecimal(agDblUsertypeAvg));
				}
				
				outerMp.put("ACTUAL_CAL_BASIS", actualCalBasis+"");
				
				
//				if(uF.parseToInt(strUsertypeId) != 3) {
//					System.out.println("ApR/630--strUsertypeId="+strUsertypeId);
//					if(hmUsertypeW!=null && uF.parseToDouble(hmUsertypeW.get(strUsertypeId)) > 0) {
//						dblAllUsertypeAvg += (dblUsertypeAvg * uF.parseToDouble(hmUsertypeW.get(strUsertypeId))) / 100;
//					} else {
//						dblAllUsertypeAvg += dblUsertypeAvg;
//						intUsertypeCnt++;
//					}
//				}
				
				if(uF.parseToInt(strUsertypeId) != 3 || !isSelfRating) {
					
					if(!isUserTypeRating || (isUserTypeRating && alFeatureUserTypeId.contains(strUsertypeId) && uF.parseToInt(strUsertypeId) == uF.parseToInt(hmPriorUser.get(strUsertypeId)))){
//						System.out.println("ApR/696--strUsertypeId="+strUsertypeId+"---priorUser="+hmPriorUser.get(strUsertypeId));
						if(hmUsertypeW!=null && uF.parseToDouble(hmUsertypeW.get(strUsertypeId)) > 0) {
							dblAllUsertypeAvg += (dblUsertypeAvg * uF.parseToDouble(hmUsertypeW.get(strUsertypeId))) / 100;
						} else {
							dblAllUsertypeAvg += dblUsertypeAvg;
//							intUsertypeCnt++;
						}
					}
					
				}
				
			}
		//===end parvez date: 18-03-2023===	
			
		//===start parvez date: 21-03-2023===	
//			System.out.println("intUsertypeCnt=="+intUsertypeCnt);
			/*if(intUsertypeCnt==0) {
				outerMp.put("AGGREGATE", uF.formatIntoTwoDecimal(dblAllUsertypeAvg));
			} else {
				double dblAggregate = dblAllUsertypeAvg / intUsertypeCnt;
				outerMp.put("AGGREGATE", uF.formatIntoTwoDecimal(dblAggregate));
			}*/
			
			if(hmFinalScore!=null && uF.parseToInt(hmFinalScore.get(getEmpid()+"_MARK"))>0){
				outerMp.put("AGGREGATE", uF.formatIntoTwoDecimal(uF.parseToDouble(hmFinalScore.get(getEmpid()+"_MARK"))));
				setAnscomment(hmFinalScore.get(getEmpid()+"_COMMENT"));
				markApprovedFlag = true;
			}else{
				if(intUsertypeCnt==0) {
					
	//				System.out.println("dblAllUsertypeAvg ===>> " + dblAllUsertypeAvg);
					outerMp.put("AGGREGATE", uF.formatIntoTwoDecimal(dblAllUsertypeAvg));
				} else {
					double dblAggregate = dblAllUsertypeAvg / intUsertypeCnt;
//					System.out.println("else dblAllUsertypeAvg ===>> " + dblAllUsertypeAvg + " -- intUsertypeCnt ===>> " + intUsertypeCnt);
					outerMp.put("AGGREGATE", uF.formatIntoTwoDecimal(dblAggregate));
//					System.out.println("dblAggregate ===>> " + dblAggregate);
				}
			}
		//===end parvez date: 21-03-2023===	
			
		//	System.out.println("outerMp==>"+outerMp);
			// request.setAttribute("hmUserTypeID", hmUserTypeID);
			request.setAttribute("appraiseeName", appraiseeName);
			request.setAttribute("outerMp", outerMp);
			request.setAttribute("markApprovedFlag", markApprovedFlag);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}



	private void insertEmployeeActivity() {
		
		try {
			EmployeeActivity activity=new EmployeeActivity();
			activity.request=request;
			activity.session = session;
			activity.CF = CF;
			activity.setStrEmpId2(getEmpid());
			activity.setActivityList(activityList);
			activity.setAl(al);
			activity.setAppraisal_id(getId());
			activity.setDataType(getDataType());
			activity.setDepartmentList1(departmentList1);
			activity.setDesigList(desigList);
			activity.setEffectiveDate(getEffectiveDate());
			activity.setEmailNotification(getEmailNotification());
			activity.setEmp_salary_id(getEmp_salary_id());
			activity.setEmpChangeGrade(getEmpChangeGrade());
			activity.setEmpGrade(getEmpGrade());
			activity.setEmpList(empList);
			activity.setF_department(getF_department());
			activity.setF_level(getF_level());
			activity.setF_org(getF_org());
			activity.setF_strWLocation(getF_strWLocation());
			activity.setGradeChangeList(gradeChangeList);
			activity.setGradeList(gradeList);
			activity.setHideIsDisplay(getHideIsDisplay());
			activity.setIsDisplay(getIsDisplay());
			activity.setLevelList(levelList);
			activity.setLevelList1(levelList1);
			activity.setOrganisationList(organisationList);
			activity.setOrganisationList1(organisationList1);
			activity.setSalary_head_id(getSalary_head_id());
			activity.setSalary_head_value(getSalary_head_value());
//			activity.setSalaryHeadList(salaryHeadList); 
			activity.setServiceList1(serviceList1);
			activity.setStrActivity(getStrActivity());
			activity.setStrDepartment(getStrDepartment());
			activity.setStrDesignation(getStrDesignation());
			activity.setStrDesignationUpdate(getStrDesignationUpdate());
			activity.setStrEmpId(null);
			activity.setStrExtendProbationDays(getStrExtendProbationDays());
			activity.setStrGrade(getStrGrade());
			activity.setStrIncrementPercentage(getStrIncrementPercentage());
			activity.setStrIncrementType(getStrIncrementType());
			activity.setStrJoiningDate(getStrJoiningDate());
			activity.setStrLevel(getStrLevel());
			activity.setStrNewStatus(getStrNewStatus());
			activity.setStrNoticePeriod(getStrNoticePeriod());
			activity.setStrOrganisation(getStrOrganisation());
			activity.setStrProbationPeriod(getStrProbationPeriod());
			activity.setStrReason(getStrReason());
			activity.setStrSBU(getStrSBU());
			activity.setStrTransferType(getStrTransferType());
			activity.setStrUpdate(null);
			activity.setStrUpdateDocument("updateDocument");
			activity.setStrWLocation(getStrWLocation());
			activity.setwLocationList1(wLocationList1);
			activity.setFromPage("AR");
			activity.setAppFreqId(getAppFreqId());
			activity.setDisableSalaryStructure(getDisableSalaryStructure());
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
		List<String> alLearningPlans = new ArrayList<String>();
		String strApprovedBy = null;
		boolean flag = false;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("select areas_of_strength,areas_of_development,learning_ids,sattlement_comment,if_approved,user_id, emp_fname,emp_mname, emp_lname, " +
				" activity_ids from appraisal_final_sattlement afs,employee_personal_details epd where afs.user_id = epd.emp_per_id and emp_id=? and appraisal_id=? and appraisal_freq_id =?");
//			pst=con.prepareStatement(selectFinalSattlement);
			pst.setInt(1, uF.parseToInt(getEmpid()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
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
					alLearningPlans = getLearningPlanName(con, rs.getString("learning_ids"));
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
			request.setAttribute("alLearningPlans", alLearningPlans);
			request.setAttribute("flag", flag);
			request.setAttribute("strApprovedBy", strApprovedBy);
			
			if(!flag){
				
				setStrEmpId(getEmpid());
				
				loadValidateEmpActivity();
				
				int nEmpLevelId = CF.getEmpLevelId(getEmpid(), request);
				salaryHeadList = new ArrayList<FillSalaryHeads>();
//				salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads();
				salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads(""+nEmpLevelId);
				
				viewUpdateEmployeeSalaryDetails();
				
				viewProfile(getStrEmpId());
				viewEmpActivity(); 
				getLeaveDetails(uF);
			} else {
				
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
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	private List<String> getLearningPlanName(Connection con, String lPlanIds) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> alLPlanName = new ArrayList<String>();
		try {
			lPlanIds = lPlanIds.substring(1, lPlanIds.length()-1);
			pst = con.prepareStatement("select learning_plan_name from learning_plan_details where learning_plan_id in ("+lPlanIds+") ");
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while(rs.next()) {
				alLPlanName.add(rs.getString("learning_plan_name"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return alLPlanName;
	}



	private void getLeaveDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			String levelId = CF.getEmpLevelId(con, getStrEmpId());
			List<List<String>> leaveTypeListWithBalance = CF.getLevelLeaveTypeBalanceForEmp(con, levelId, ""+getStrEmpId(), CF);
			
			request.setAttribute("leaveTypeListWithBalance", leaveTypeListWithBalance);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	public void loadValidateEmpActivity() {

		UtilityFunctions uF = new UtilityFunctions();
		if(getDataType() == null || getDataType().equals("A")) {
			activityList = new FillActivity(request).fillActivityByNode(true, false,uF.parseToInt(getEmpid()));
		} else if(getDataType() == null || getDataType().equals("D")) {
			activityList = new FillActivity(request).fillActivityByNode(true, true,uF.parseToInt(getEmpid()));
		}
		/*if(getDataType() == null || getDataType().equals("A")) {
			activityList = new FillActivity(request).fillActivityByNode(true, false);
		} else if(getDataType() == null || getDataType().equals("D")) {
			activityList = new FillActivity(request).fillActivityByNode(true, true);
		}*/
		
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		desigList = new ArrayList<FillDesig>();
		gradeList = new ArrayList<FillGrade>();
		empStatusList = new FillEmpStatus(request).fillEmpStatus();
		empList = new FillEmployee(request).fillEmployeeName(strUserType, strSessionEmpId, session);
		
		organisationList = new FillOrganisation(request).fillOrganisation();
		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		organisationList1 = new FillOrganisation(request).fillOrganisationWithoutCurrentOrgId(getF_org());
		wLocationList1 = new FillWLocation(request).fillWLocationWithoutCurrentLocation(uF.parseToInt(getStrEmpId()));
		departmentList1 = new FillDepartment(request).fillDepartmentWithoutCurrentDepartment(uF.parseToInt(getStrEmpId()));
		serviceList1 = new FillServices(request).fillServicesWithoutCurrentService(uF.parseToInt(getStrEmpId()));
		levelList1 = new FillLevel(request).fillLevelWithoutCurrentLevel(uF.parseToInt(getStrEmpId()));
		gradeChangeList = new FillGrade(request).fillGradeFromEmpDesignationWithoutCurrentGrade(uF.parseToInt(getStrEmpId()));
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
	}
	
private boolean viewUpdateEmployeeSalaryDetails() {
	
	Connection con = null;
	PreparedStatement pst=null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	UtilityFunctions uF= new UtilityFunctions();
	boolean flag = false;
	try {
		String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
		String strFinancialYearStart = strFinancialYearDates[0];
		String strFinancialYearEnd = strFinancialYearDates[1];
		
		con = db.makeConnection(con);
//		System.out.println("in viewUpdateEmployeeSalaryDetails ...");
		Map hmEmpMertoMap = new HashMap();
		Map hmEmpWlocationMap = new HashMap();
		Map hmEmpStateMap = new HashMap();
		CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
		
		String levelId = CF.getEmpLevelId(con, getStrEmpId());
		String strOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
		
		String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
		String[] strPayCycleDate = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrgId);
		double dblReimbursementCTC = 0.0d;
		double dblReimbursementCTCOptional = 0.0d;
		if(strPayCycleDate !=null && strPayCycleDate.length > 0){
			String startDate = strPayCycleDate[0];
			String endDate = strPayCycleDate[1];
			String strPC = strPayCycleDate[2];
		
			dblReimbursementCTC = CF.getReimbursementCTCHeadTotalAmount(con, uF, uF.parseToInt(getStrEmpId()), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(levelId));
			request.setAttribute("dblReimbursementCTC", ""+dblReimbursementCTC);
			
			dblReimbursementCTCOptional = CF.getReimbursementCTCOptinalHeadTotalAmount(con, uF, uF.parseToInt(getStrEmpId()), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(levelId));
			request.setAttribute("dblReimbursementCTCOptional", ""+dblReimbursementCTCOptional);
		}
		
		setDisableSalaryStructure(CF.getEmpDisableSalaryCalculation(con,uF,getStrEmpId()));
		
		pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id = ? and is_annual_variable=true and (is_delete is null or is_delete=false) order by weight");
		pst.setInt(1, uF.parseToInt(levelId));
		rs = pst.executeQuery();
		List<String> alAnnualSalaryHead = new ArrayList<String>();
		while(rs.next()) {
			if(!alAnnualSalaryHead.contains(rs.getString("salary_head_id"))){
				alAnnualSalaryHead.add(rs.getString("salary_head_id"));
			}
		}
		rs.close();
		pst.close();
		
		Map<String, String> hmEmpAnnualVarPolicyAmount = CF.getEmpAnnualVariablePolicyMonthAmount(con, uF, getStrEmpId(), strFinancialYearStart, strFinancialYearEnd);
		if(hmEmpAnnualVarPolicyAmount == null) hmEmpAnnualVarPolicyAmount = new HashMap<String, String>();
		request.setAttribute("hmEmpAnnualVarPolicyAmount", hmEmpAnnualVarPolicyAmount);
		
		String strStateId = (String)hmEmpStateMap.get(getStrEmpId());
		
		List<List<String>> alE = new ArrayList<List<String>>();
		List<String> alInner = new ArrayList<String>();
		
		
		Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con);
		
		pst = con.prepareStatement("SELECT weight,isdisplay,pay_type,user_id,entry_date,amount," +
				"emp_salary_id,salary_head_amount,sd.earning_deduction,salary_head_amount_type," +
				"sub_salary_head_id, sd.salary_head_id as salary_head_id,multiple_calculation," +
				"salary_calculate_amount FROM (SELECT * FROM emp_salary_details WHERE emp_id = ? " +
				"AND service_id = ? AND effective_date = (SELECT MAX(effective_date) " +
				"FROM emp_salary_details WHERE emp_id = ? and is_approved=true and level_id = ?) " +
				"AND effective_date <= ? and level_id = ?) asd RIGHT JOIN salary_details sd " +
				"ON sd.salary_head_id = asd.salary_head_id WHERE sd.level_id = ? " +
				"and (sd.is_delete is null or sd.is_delete=false) order by sd.earning_deduction desc, weight");
		pst.setInt(1, uF.parseToInt(getStrEmpId()) );
		pst.setInt(2, 0);  // Default Service Id
		pst.setInt(3, uF.parseToInt(getStrEmpId()) );
		pst.setInt(4, uF.parseToInt(levelId) );
		pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
		pst.setInt(6, uF.parseToInt(levelId) );
		pst.setInt(7, uF.parseToInt(levelId) );
		rs = pst.executeQuery();
//		System.out.println("pst viewUpdateEmployeeSalaryDetails ===>> " + pst);
		
		String alHeadId = "";
		Map<String, String> hmSalaryAmountMap = new HashMap<String, String>();
		List alSalaryDuplicationTracer = new ArrayList();
		Map<String, String> hmTotal = new HashMap<String, String>();
		
		while(rs.next()) {
			
			hmSalaryAmountMap.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));
			
			
			alInner = new ArrayList<String>();
			alInner.add(uF.parseToInt(rs.getString("emp_salary_id"))+"");	//0
			String rsHeadId = rs.getString("salary_head_id");
			alInner.add(rsHeadId);	//1
			
			alInner.add(uF.showData(hmSalaryMap.get(rsHeadId), ""));	//2
			
			alInner.add(rs.getString("earning_deduction"));	//3
			alInner.add(rs.getString("salary_head_amount_type")); //4
			rsHeadId = rs.getString("sub_salary_head_id");	
			alInner.add(rsHeadId);	//5
			
			alInner.add("");	//6
			
			
			alInner.add(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount")))); //7
			 
			double dblAmount = 0;
			if(rs.getString("amount")==null){
				String strAmountType = rs.getString("salary_head_amount_type");
				if(strAmountType!=null && strAmountType.equalsIgnoreCase("P")){
					dblAmount = rs.getDouble("salary_calculate_amount");
				}else if(strAmountType!=null && strAmountType.equalsIgnoreCase("A")){
					dblAmount = rs.getDouble("salary_head_amount");
				}
			}else{
				dblAmount = rs.getDouble("amount") ;
			}
			
//			System.out.println("dblAmount=="+dblAmount);
			
			StringBuilder sbMulcalType = new StringBuilder();
			if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))){
				String strMulCal = rs.getString("multiple_calculation");
				CF.appendMultiplePercentageCalType(uF,sbMulcalType,strMulCal,hmSalaryMap,alAnnualSalaryHead); 
				
				alInner.add(uF.formatIntoOneDecimalWithOutComma(dblAmount)); //8
			} else {
				alInner.add(uF.formatIntoOneDecimalWithOutComma(dblAmount)); //8
			}
			
			
			alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));	//9
			alInner.add(rs.getString("user_id"));	//10
			alInner.add(rs.getString("pay_type"));	//11
			alInner.add(uF.parseToBoolean(rs.getString("isdisplay"))+"");	//12
			alInner.add(rs.getString("weight"));	//13
			alInner.add(rs.getString("multiple_calculation"));	//14
			alInner.add(sbMulcalType.toString());	//15
			
			
			int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
			
			if(index>=0){
				alE.remove(index);
				alE.add(index, alInner);
			}else{
				alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
				alE.add(alInner);
			}
			
//			System.out.println("alInner ===>> " + alInner);
			flag = true;	
		}
		rs.close();
		pst.close();
		
		setEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
		
		
//		System.out.println("reportList alE ===>> " + alE);
		request.setAttribute("reportList", alE);
		
	} catch (Exception e) {
		e.printStackTrace();
		
	}finally{
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	return flag;
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
//		CF.getEmpInfoMap(con, false);
		
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
		boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
		Map<String, String> hmServiceName = CF.getServicesMap(con, false);
		String educationsName = CF.getEmpEducations(con, getStrEmpId());
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
		pst.setInt(1, uF.parseToInt(getStrEmpId()));
		pst.setInt(2, uF.parseToInt(getStrEmpId()));
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
			hmEmpActivityDetails.put("LEVEL_ID", hmEmpLevel.get(getStrEmpId()));
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
			setEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
//			setStrActivity(rst.getString("activity_id"));
//			setStrLevel(rst.getString("level_id"));
//			setStrDesignation(rst.getString("desig_id"));
//			setStrGrade(rst.getString("grade_id"));
//			setEmpGrade(rst.getString("grade_id"));
//			setStrWLocation(rst.getString("wlocation_id"));
//			setStrDepartment(rst.getString("department_id"));
//			setStrNewStatus(rst.getString("emp_status_code"));
//			setStrNoticePeriod(rst.getString("notice_period"));
//			setStrProbationPeriod(rst.getString("probation_period"));
			
//			setStrReason(rst.getString("reason"));
			
		}
		rst.close();
		pst.close();
		
		if(joining_date != null && !joining_date.equals("")) {
			uF.getTimeDuration(joining_date, CF, uF, request); // expWithUs
		}
		
		
//		List<String> serviceIdList = Arrays.asList(serviceIds.split(","));  
//		String servicesName = CF.getAppendData(serviceIdList, hmServiceName);
		setStrEmpId2(getStrEmpId());
		
		hmEmpActivityDetails.put("EMP_ID", getStrEmpId());
		
		request.setAttribute("educationsName", educationsName);
//		request.setAttribute("totExp", totExp);
//		request.setAttribute("servicesName", servicesName);
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

			pst = con.prepareStatement("delete from appraisal_final_sattlement where emp_id=? and appraisal_id=? and appraisal_freq_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpid()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			pst.execute();
			pst.close();
			
			String learningPlanIds = "";
			StringBuilder sbLPlanIds = null;
			if(getLearningIds() != null) {
				for(int i=0; i<getLearningIds().length; i++) {
					if(uF.parseToInt(getLearningIds()[i])>0) {
						if(learningPlanIds.equals("")) {
							learningPlanIds = ","+getLearningIds()[i]+",";
							sbLPlanIds = new StringBuilder();
							sbLPlanIds.append(getLearningIds()[i]);
						} else {
							learningPlanIds += getLearningIds()[i]+",";
							sbLPlanIds.append(","+getLearningIds()[i]);
						}
					}
				}
			}
			
			if(sbLPlanIds == null) {
				sbLPlanIds = new StringBuilder();
			}
			
			pst = con.prepareStatement("insert into appraisal_final_sattlement(emp_id,appraisal_id,user_id,sattlement_comment,if_approved,_date,activity_id1,appraisal_freq_id," +
					"areas_of_strength,areas_of_development,learning_ids)values(?,?,?,? ,?,?,?,? ,?,?,?)");
			pst.setInt(1, uF.parseToInt(getEmpid()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setString(4, getRemark());
			pst.setBoolean(5, true);
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(7, 0);
			pst.setInt(8, uF.parseToInt(getAppFreqId()));
			pst.setString(9, getAreasOfStrength());
			pst.setString(10, getAreasOfDevelopment());
			pst.setString(11, learningPlanIds);
//			System.out.println("remark pst==>"+pst);
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0){
				flag = true;
			}
			
//			System.out.println("flag==>"+flag+"==>getSendtoGapStatus==>"+ uF.parseToBoolean(getSendtoGapStatus()));
			if (flag && uF.parseToBoolean(getSendtoGapStatus())) {
				sendToLearningGap(con, sbLPlanIds.toString());
				
//				String strDomain = request.getServerName().split("\\.")[0];
//				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//				userAlerts.setStrDomain(strDomain);
//				userAlerts.setStrEmpId(strSessionEmpId);
//				userAlerts.set_type(REVIEW_FINALIZATION_ALERT);
//				userAlerts.setStatus(INSERT_ALERT);
//				Thread t = new Thread(userAlerts);
//				t.run();
			}
			
			if(flag) {
				Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
				String reviewName = CF.getReviewNameById(con, uF, getId());
				
				sendMail(con, getId(), "", uF.parseToBoolean(getSendtoGapStatus()), getEmpid());
				
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
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return flag;
	}
	
	
	public void sendMail(Connection con, String appraisalId, String activityIds, Boolean sendToGap , String empId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		UtilityFunctions uF = new UtilityFunctions();

		try {
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
			StringBuilder appraisalName = new StringBuilder();
		//	pst = con.prepareStatement("select appraisal_name, from appraisal_details where appraisal_details_id = ?");
			pst = con.prepareStatement("select appraisal_name,appraisal_freq_name from appraisal_details ad, appraisal_details_frequency adf"
				+" where ad.appraisal_details_id = adf.appraisal_id and (adf.is_delete is null or adf.is_delete = false ) and ad.appraisal_details_id = ? and appraisal_freq_id = ?");
			pst.setInt(1, uF.parseToInt(appraisalId));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rst = pst.executeQuery();
			while (rst.next()) {
				appraisalName.append(rst.getString("appraisal_name"));
				if(rst.getString("appraisal_freq_name") != null && !rst.getString("appraisal_freq_name").equals("")) {
					appraisalName.append(" ("+ rst.getString("appraisal_freq_name") +")");
				}
			}
			rst.close();
			pst.close();
			
			if(appraisalName != null && !appraisalName.equals("")) {
				Map<String, String> hmEmpInner1 = hmEmpInfo.get(empId);
				StringBuilder sbRevieweeName = new StringBuilder();
				sbRevieweeName.append(hmEmpInner1.get("FNAME")+" " +hmEmpInner1.get("LNAME"));
				
				Map<String, String> hmEmpInner = hmEmpInfo.get(strSessionEmpId);
				String strDomain = request.getServerName().split("\\.")[0];
								 
				StringBuilder sbFinalizerName = new StringBuilder();
				sbFinalizerName.append(hmEmpInner.get("FNAME")+" " +hmEmpInner.get("LNAME"));
				
				Notifications nF1 = new Notifications(N_REVIEW_FINALIZATION_FOR_EMP, CF); 
				nF1.setDomain(strDomain);
				nF1.request = request;
				nF1.setStrEmpId(empId);
				nF1.setStrHostAddress(CF.getStrEmailLocalHost());
				nF1.setStrHostPort(CF.getStrHostPort()); 
				nF1.setStrContextPath(request.getContextPath());
				nF1.setStrFinalizerName(sbFinalizerName.toString());
				nF1.setStrReviewName(appraisalName.toString());
				nF1.setStrEmpFname(hmEmpInner1.get("FNAME"));
				nF1.setStrEmpLname(hmEmpInner1.get("LNAME"));
				nF1.setEmailTemplate(true);
				nF1.sendNotifications();	 
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst != null){
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void sendToLearningGap(Connection con, String lPlanIds) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			Map<String, String> hmDesignation = CF.getEmpDesigMapId(con);
//			Map<String, String> hmWlocationMap = CF.getEmpWlocationMap(con);
			String wLocationId = CF.getEmpWlocationId(con, uF, getEmpid());
			
			Map<String, String> hmAttributeThreshhold = new HashMap<String, String>();
			pst = con.prepareStatement("select attribute_id,threshhold from appraisal_attribute_level");
//			pst=con.prepareStatement(selectAttribute);
			rs = pst.executeQuery(); 
			while (rs.next()) {
				hmAttributeThreshhold.put(rs.getString("attribute_id"),rs.getString("threshhold"));
			}
			rs.close();
			pst.close();
			
			double dblTotalMarks = 0;
			double dblTotalWeightage = 0;
			double dblTotalAggregate = 0;
			Map<String, String> hmScoreAggregateMap = new HashMap<String, String>();

			pst = con.prepareStatement("select sum(marks) as marks, sum(weightage) as weightage, aqw.appraisal_attribute from appraisal_question_answer aqw where aqw.appraisal_id=? and emp_id=? and appraisal_freq_id = ? group by aqw.appraisal_attribute");
			pst.setInt(1, uF.parseToInt(id));
			pst.setInt(2, uF.parseToInt(getEmpid()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
//			System.out.println("sendToLearningGap pst==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {

				dblTotalMarks = uF.parseToDouble(rs.getString("marks"));
				dblTotalWeightage = uF.parseToDouble(rs.getString("weightage"));
				dblTotalAggregate = uF.parseToDouble(uF.formatIntoTwoDecimal(((dblTotalMarks / dblTotalWeightage) * 100)));
				hmScoreAggregateMap.put(rs.getString("appraisal_attribute"), uF.showData("" + dblTotalAggregate, "0"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbHrIds = new StringBuilder();
			StringBuilder sbManagerIds = new StringBuilder();
			StringBuilder sbCeoIds = new StringBuilder();
			StringBuilder sbHodIds = new StringBuilder();
			pst = con.prepareStatement("select supervisor_id,hr_ids,ceo_ids,hod_ids from appraisal_details where appraisal_details_id = ?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery(); 
			while (rs.next()) {
				sbManagerIds.append(rs.getString("supervisor_id"));
				sbHrIds.append(rs.getString("hr_ids"));
				sbCeoIds.append(rs.getString("ceo_ids"));
				sbHodIds.append(rs.getString("hod_ids"));
			}
			rs.close();
			pst.close();
			
			Map<String, List<String>> hmLearningAttributes = new HashMap<String, List<String>>();
			if(lPlanIds != null && !lPlanIds.equals("")) {
				pst = con.prepareStatement("update learning_plan_details set learner_ids = learner_ids||'"+getEmpid()+"'||',' where learning_plan_id in ("+lPlanIds+")");
				pst.execute();
//				System.out.println("pst ===>> " + pst);
				pst.close();
	
				pst = con.prepareStatement("select * from learning_plan_details where learning_plan_id in ("+lPlanIds+")");
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList = Arrays.asList(rs.getString("attribute_id").split(","));
					hmLearningAttributes.put(rs.getString("learning_plan_id"), innerList);
				}
				rs.close();
				pst.close();
			}			
			
			if (!hmScoreAggregateMap.isEmpty()) {
				Iterator<String> it = hmScoreAggregateMap.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					double aggregate = uF.parseToDouble(hmScoreAggregateMap.get(key));
					
//					if (aggregate < uF.parseToDouble(hmAttributeThreshhold.get(key))) {
					if (!hmLearningAttributes.isEmpty()) {
						Iterator<String> itL = hmLearningAttributes.keySet().iterator();
						int learningCnt=0;
						while (itL.hasNext()) {
							String lPlanId = itL.next();
							List<String> attribList = hmLearningAttributes.get(lPlanId);
							if(attribList.contains(key)) {
								pst = con.prepareStatement("insert into training_gap_details(emp_id,designation_id,wlocation_id,attribute_id,appraisal_id,"
									+ "actual_score,required_score,training_completed_status,is_training_schedule,added_by,entry_date, appraisal_freq_id," +
									"assign_learning_plan_id)" +
									" values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
		//						pst=con.prepareStatement(insertTrainingGap);
								pst.setInt(1,uF.parseToInt(getEmpid()));
								pst.setInt(2,uF.parseToInt(hmDesignation.get(getEmpid())));
		//						pst.setInt(3,uF.parseToInt(hmWlocationMap.get(empid)));
								pst.setInt(3,uF.parseToInt(wLocationId));
								pst.setInt(4, uF.parseToInt(key));
								pst.setInt(5, uF.parseToInt(id));
								pst.setDouble(6, aggregate);
								pst.setDouble(7, uF.parseToDouble(hmAttributeThreshhold.get(key)));
								pst.setBoolean(8, false);
								pst.setBoolean(9, true);
								pst.setInt(10, uF.parseToInt(strSessionEmpId));
								pst.setDate(11, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(12, uF.parseToInt(getAppFreqId()));
								pst.setInt(13, uF.parseToInt(lPlanId));
								pst.execute();
								pst.close();
//								System.out.println("pst with lplan ===>> " + pst);
								learningCnt++;
							} else if(learningCnt == 0) {
								pst = con.prepareStatement("insert into training_gap_details(emp_id,designation_id,wlocation_id,attribute_id,appraisal_id,"
									+ "actual_score,required_score,training_completed_status,is_training_schedule,added_by,entry_date, appraisal_freq_id)" +
									" values(?,?,?,?, ?,?,?,?, ?,?,?,?)");
		//						pst=con.prepareStatement(insertTrainingGap);
								pst.setInt(1,uF.parseToInt(getEmpid()));
								pst.setInt(2,uF.parseToInt(hmDesignation.get(getEmpid())));
		//						pst.setInt(3,uF.parseToInt(hmWlocationMap.get(empid)));
								pst.setInt(3,uF.parseToInt(wLocationId));
								pst.setInt(4, uF.parseToInt(key));
								pst.setInt(5, uF.parseToInt(id));
								pst.setDouble(6, aggregate);
								pst.setDouble(7, uF.parseToDouble(hmAttributeThreshhold.get(key)));
								pst.setBoolean(8, false);
								pst.setBoolean(9, false);
								pst.setInt(10, uF.parseToInt(strSessionEmpId));
								pst.setDate(11, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(12, uF.parseToInt(getAppFreqId()));
								pst.execute();
								pst.close();
//								System.out.println("pst without lplan ===>> " + pst);
							}
						}
					} else {
						pst = con.prepareStatement("insert into training_gap_details(emp_id,designation_id,wlocation_id,attribute_id,appraisal_id,"
							+ "actual_score,required_score,training_completed_status,is_training_schedule,added_by,entry_date, appraisal_freq_id)" +
							" values(?,?,?,?, ?,?,?,?, ?,?,?,?)");
//						pst=con.prepareStatement(insertTrainingGap);
						pst.setInt(1,uF.parseToInt(getEmpid()));
						pst.setInt(2,uF.parseToInt(hmDesignation.get(getEmpid())));
//						pst.setInt(3,uF.parseToInt(hmWlocationMap.get(empid)));
						pst.setInt(3,uF.parseToInt(wLocationId));
						pst.setInt(4, uF.parseToInt(key));
						pst.setInt(5, uF.parseToInt(id));
						pst.setDouble(6, aggregate);
						pst.setDouble(7, uF.parseToDouble(hmAttributeThreshhold.get(key)));
						pst.setBoolean(8, false);
						pst.setBoolean(9, false);
						pst.setInt(10, uF.parseToInt(strSessionEmpId));
						pst.setDate(11, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(12, uF.parseToInt(getAppFreqId()));
						pst.execute();
						pst.close();
//						System.out.println("pst else ===>> " + pst);
					}
//					}
					
					Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
					
					List<String> hrIdList = Arrays.asList(sbHrIds.toString().split(","));
					String strDomain = request.getServerName().split("\\.")[0];
					for(int i=0; hrIdList!= null && !hrIdList.isEmpty() && i<hrIdList.size(); i++) {
						if(!hrIdList.get(i).equals("") && uF.parseToInt(hrIdList.get(i)) > 0) {
							String alertData = "<div style=\"float: left;\"> Learning Gap has emerged for ("+CF.getEmpNameMapByEmpId(con, strEmpId)+") by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
							String alertAction = "Learnings.action?callFrom=LA&pType=WR";
							
							UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(hrIdList.get(i));
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmUserTypeId.get(HRMANAGER));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
//							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//							userAlerts.setStrDomain(strDomain);
//							userAlerts.setStrEmpId(hrIdList.get(i));
//							userAlerts.set_type(HR_LEARNING_GAPS_ALERT);
//							userAlerts.setStatus(INSERT_ALERT);
//							Thread t = new Thread(userAlerts);
//							t.run();
						}
					}
					
					List<String> managerIdList = Arrays.asList(sbManagerIds.toString().split(","));
					for(int i=0; managerIdList!= null && !managerIdList.isEmpty() && i<managerIdList.size(); i++) {
						if(!managerIdList.get(i).equals("") && uF.parseToInt(managerIdList.get(i)) > 0) {
							String alertData = "<div style=\"float: left;\"> Learning Gap has emerged for ("+CF.getEmpNameMapByEmpId(con, strEmpId)+") by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
							String alertAction = "Learnings.action?callFrom=LA&pType=WR";
							
							UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(managerIdList.get(i));
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmUserTypeId.get(MANAGER));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
//							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//							userAlerts.setStrDomain(strDomain);
//							userAlerts.setStrEmpId(managerIdList.get(i));
//							userAlerts.set_type(HR_LEARNING_GAPS_ALERT);
//							userAlerts.setStatus(INSERT_ALERT);
//							Thread t = new Thread(userAlerts);
//							t.run();
						}
					}
					
					List<String> ceoIdList = Arrays.asList(sbCeoIds.toString().split(","));
					for(int i=0; ceoIdList!= null && !ceoIdList.isEmpty() && i<ceoIdList.size(); i++) {
						if(!ceoIdList.get(i).equals("") && uF.parseToInt(ceoIdList.get(i)) > 0) {
							String alertData = "<div style=\"float: left;\"> Learning Gap has emerged for ("+CF.getEmpNameMapByEmpId(con, strEmpId)+") by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
							String alertAction = "Learnings.action?callFrom=LA&pType=WR";
							
							UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(ceoIdList.get(i));
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmUserTypeId.get(CEO));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
//							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//							userAlerts.setStrDomain(strDomain);
//							userAlerts.setStrEmpId(ceoIdList.get(i));
//							userAlerts.set_type(HR_LEARNING_GAPS_ALERT);
//							userAlerts.setStatus(INSERT_ALERT);
//							Thread t = new Thread(userAlerts);
//							t.run();
						}
					}
					
					List<String> hodIdList = Arrays.asList(sbHodIds.toString().split(","));
					for(int i=0; hodIdList!= null && !hodIdList.isEmpty() && i<hodIdList.size(); i++) {
						if(!hodIdList.get(i).equals("") && uF.parseToInt(hodIdList.get(i)) > 0) {
							String alertData = "<div style=\"float: left;\"> Learning Gap has emerged for ("+CF.getEmpNameMapByEmpId(con, strEmpId)+") by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
							String alertAction = "Learnings.action?callFrom=LA&pType=WR";
							
							UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(hodIdList.get(i));
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmUserTypeId.get(HOD));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
//							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//							userAlerts.setStrDomain(strDomain);
//							userAlerts.setStrEmpId(hodIdList.get(i));
//							userAlerts.set_type(HR_LEARNING_GAPS_ALERT);
//							userAlerts.setStatus(INSERT_ALERT);
//							Thread t = new Thread(userAlerts);
//							t.run();
						}
					} 
					sendMail(con, hrIdList, id, key, empid);
					sendMail(con, managerIdList, id, key, empid);
					sendMail(con, ceoIdList, id, key, empid);
					sendMail(con, hodIdList, id, key, empid);
				}
			}
			
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
	}
	
	
	public void sendMail(Connection con, List<String> hrIdList, String appraisalId, String attribId , String empId) {

		ResultSet rs = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
//			System.out.println("Req id is ========= "+getStrId());
			StringBuilder reviewName = new StringBuilder();;
//			pst = con.prepareStatement("select appraisal_name from appraisal_details where appraisal_details_id = ?");
			pst = con.prepareStatement("select appraisal_name,appraisal_freq_name from appraisal_details ad, appraisal_details_frequency adf"
					+" where ad.appraisal_details_id = adf.appraisal_id and (adf.is_delete is null or adf.is_delete = false ) and ad.appraisal_details_id = ? and appraisal_freq_id = ?");
			pst.setInt(1, uF.parseToInt(appraisalId));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				reviewName.append(rs.getString("appraisal_name"));
				if(rs.getString("appraisal_freq_name") != null && !rs.getString("appraisal_freq_name").equals("")) {
					reviewName.append(" ("+rs.getString("appraisal_freq_name")+")");
				}
			}
			rs.close();
			pst.close();
			
			if(reviewName != null && !reviewName.equals("")) {
				Map<String, String> hmEmpInner1 = hmEmpInfo.get(strSessionEmpId);
				StringBuilder sbRevieweeName = new StringBuilder();
				sbRevieweeName.append(hmEmpInner1.get("FNAME")+" " +hmEmpInner1.get("LNAME"));
				
				String attributeName = CF.getAttributeNameByAttributeId(con, attribId);
				
				for(int i=0; hrIdList!= null && i<hrIdList.size(); i++) {
					
					Map<String, String> hmEmpInner = hmEmpInfo.get(hrIdList.get(i));
					if(hrIdList.get(i) != null && !hrIdList.get(i).equals("")){
						String strDomain = request.getServerName().split("\\.")[0];	
						Notifications nF = new Notifications(N_LEARNING_GAP_FOR_HR, CF); 
						nF.setDomain(strDomain);
						nF.request = request;
						nF.setStrEmpId(hrIdList.get(i));
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrReviewName(reviewName.toString());
						nF.setStrRevieweeName(sbRevieweeName.toString());
						nF.setStrAttributeName(attributeName);
						nF.setStrEmpFname(hmEmpInner.get("FNAME"));
						nF.setStrEmpLname(hmEmpInner.get("LNAME"));
						nF.setEmailTemplate(true);
						nF.sendNotifications();
					}
				}
				
			}
			
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
	}
	
	public void insertFinalReviewRating(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			String levelComment = request.getParameter("anscomment");
			String rating = request.getParameter("gradewithrating");
			
			double weightage = 0;
			pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while(rs.next()){
				weightage += uF.parseToDouble(rs.getString("section_weightage")); 
			}
			rs.close();
			pst.close();
//			double marks = uF.parseToDouble(rating) * weightage / 5;
			double marks = 0;
			if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
				marks = uF.parseToDouble(rating) * weightage / 10;
			} else{
				marks = uF.parseToDouble(rating) * weightage / 5;
			}
//			System.out.println("ApR/2169---marks="+marks);
			
			pst = con.prepareStatement("delete from reviewer_feedback_details where emp_id=? and appraisal_id=? and appraisal_freq_id=?");
			pst.setInt(1, uF.parseToInt(getEmpid()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("insert into reviewer_feedback_details (emp_id,appraisal_id,user_id,user_type_id,appraisal_freq_id,reviewer_marks,reviewer_comment,entry_date,is_submit) values(?,?,?,?, ?,?,?,?, ?)");
			pst.setInt(1, uF.parseToInt(getEmpid()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(strUserTypeId));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			pst.setDouble(6, marks);
			pst.setString(7, levelComment);
			pst.setTimestamp(8, uF.getTimeStamp(""+uF.getCurrentDate(CF.getStrTimeZone())+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setBoolean(9, true);
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getOneOneDiscussionDetails(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			List<String> alOneOnOneDiscussion = new ArrayList<String>();
			
			pst = con.prepareStatement("select * from emp_one_one_discussion_details where emp_id=? and appraisal_id=? and appraisal_freq_id=? " +
					" and emp_sign_off=true and user_approval=true order by emp_one_one_discussion_details_id desc limit 1");
			pst.setInt(1, uF.parseToInt(getEmpid()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
//			System.out.println("pst=="+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				alOneOnOneDiscussion.add(rs.getString("emp_one_one_discussion_details_id"));		//0
				alOneOnOneDiscussion.add(uF.showData(rs.getString("emp_remark"), ""));				//1
				alOneOnOneDiscussion.add(rs.getString("user_remark"));								//2
				alOneOnOneDiscussion.add(rs.getString("start_time"));				//3
				alOneOnOneDiscussion.add(rs.getString("end_time"));				//4
				alOneOnOneDiscussion.add(uF.getDateFormat(rs.getString("discussion_date"), DBDATE, DATE_FORMAT));				//5
				alOneOnOneDiscussion.add(rs.getString("total_time_spent"));				//6
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("alOneOnOneDiscussion", alOneOnOneDiscussion);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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
	
	public String getThumbsFlag() {
		return thumbsFlag;
	}

	public String getSendtoGapStatus() {
		return sendtoGapStatus;
	}

	public void setSendtoGapStatus(String sendtoGapStatus) {
		this.sendtoGapStatus = sendtoGapStatus;
	}

	public void setThumbsFlag(String thumbsFlag) {
		this.thumbsFlag = thumbsFlag;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public String getStrActivity() {
		return strActivity;
	}
	public void setStrActivity(String strActivity) {
		this.strActivity = strActivity;
	}
	
	public String getStrOrganisation() {
		return strOrganisation;
	}

	public void setStrOrganisation(String strOrganisation) {
		this.strOrganisation = strOrganisation;
	}

	public String getStrWLocation() {
		return strWLocation;
	}
	public void setStrWLocation(String strWLocation) {
		this.strWLocation = strWLocation;
	}
	
	public String getStrSBU() {
		return strSBU;
	}

	public void setStrSBU(String strSBU) {
		this.strSBU = strSBU;
	}

	public String getStrDepartment() {
		return strDepartment;
	}
	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}
	public String getStrLevel() {
		return strLevel;
	}
	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}
	public String getStrDesignation() {
		return strDesignation;
	}
	public void setStrDesignation(String strDesignation) {
		this.strDesignation = strDesignation;
	}
	public String getStrGrade() {
		return strGrade;
	}
	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}
	public String getStrNewStatus() {
		return strNewStatus;
	}
	public void setStrNewStatus(String strNewStatus) {
		this.strNewStatus = strNewStatus;
	}
	public String getStrReason() {
		return strReason;
	}
	public void setStrReason(String strReason) {
		this.strReason = strReason;
	}
	public List<FillLevel> getLevelList() {
		return levelList;
	}
	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}
	public List<FillGrade> getGradeList() {
		return gradeList;
	}
	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}
	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}
	
	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}
	
	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public List<FillEmpStatus> getEmpStatusList() {
		return empStatusList;
	}
	public List<FillActivity> getActivityList() {
		return activityList;
	}
	public void setActivityList(List<FillActivity> activityList) {
		this.activityList = activityList;
	}
	public String getStrEmpId() {
		return strEmpId;
	}
	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}
	public List<FillEmployee> getEmpList() {
		return empList;
	}
	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}
	
	public String getStrEmpId2() {
		return strEmpId2;
	}
	public void setStrEmpId2(String strEmpId2) {
		this.strEmpId2 = strEmpId2;
	}
	public String getStrJoiningDate() {
		return strJoiningDate;
	}
	public void setStrJoiningDate(String strJoiningDate) {
		this.strJoiningDate = strJoiningDate;
	}
	public boolean getEmailNotification() {
		return emailNotification;
	}
	public void setEmailNotification(boolean emailNotification) {
		this.emailNotification = emailNotification;
	}
	public String getStrUpdate() {
		return strUpdate;
	}
	public void setStrUpdate(String strUpdate) {
		this.strUpdate = strUpdate;
	}
	public String getStrDesignationUpdate() {
		return strDesignationUpdate;
	}
	public void setStrDesignationUpdate(String strDesignationUpdate) {
		this.strDesignationUpdate = strDesignationUpdate;
	}
	public String getEmpGrade() {
		return empGrade;
	}
	public void setEmpGrade(String empGrade) {
		this.empGrade = empGrade;
	}
	public String getF_strWLocation() {
		return f_strWLocation;
	}
	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}
	public String getF_department() {
		return f_department;
	}
	public void setF_department(String f_department) {
		this.f_department = f_department;
	}
	public String getF_level() {
		return f_level;
	}
	public void setF_level(String f_level) {
		this.f_level = f_level;
	}
	public String getStrNoticePeriod() {
		return strNoticePeriod;
	}
	public void setStrNoticePeriod(String strNoticePeriod) {
		this.strNoticePeriod = strNoticePeriod;
	}
	public String getStrProbationPeriod() {
		return strProbationPeriod;
	}
	public void setStrProbationPeriod(String strProbationPeriod) {
		this.strProbationPeriod = strProbationPeriod;
	}

	public String getStrExtendProbationDays() {
		return strExtendProbationDays;
	}

	public void setStrExtendProbationDays(String strExtendProbationDays) {
		this.strExtendProbationDays = strExtendProbationDays;
	}

	public String getStrIncrementType() {
		return strIncrementType;
	}

	public void setStrIncrementType(String strIncrementType) {
		this.strIncrementType = strIncrementType;
	}

	public String getStrIncrementPercentage() {
		return strIncrementPercentage;
	}

	public void setStrIncrementPercentage(String strIncrementPercentage) {
		this.strIncrementPercentage = strIncrementPercentage;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public List<FillOrganisation> getOrganisationList1() {
		return organisationList1;
	}

	public void setOrganisationList1(List<FillOrganisation> organisationList1) {
		this.organisationList1 = organisationList1;
	}

	public List<FillWLocation> getwLocationList1() {
		return wLocationList1;
	}

	public void setwLocationList1(List<FillWLocation> wLocationList1) {
		this.wLocationList1 = wLocationList1;
	}

	public List<FillDepartment> getDepartmentList1() {
		return departmentList1;
	}

	public void setDepartmentList1(List<FillDepartment> departmentList1) {
		this.departmentList1 = departmentList1;
	}

	public List<FillServices> getServiceList1() {
		return serviceList1;
	}

	public void setServiceList1(List<FillServices> serviceList1) {
		this.serviceList1 = serviceList1;
	}

	public List<FillLevel> getLevelList1() {
		return levelList1;
	}

	public void setLevelList1(List<FillLevel> levelList1) {
		this.levelList1 = levelList1;
	}

	public String getAppraisal_id() {
		return appraisal_id;
	}

	public void setAppraisal_id(String appraisal_id) {
		this.appraisal_id = appraisal_id;
	}

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}

	public List<List<String>> getAl() {
		return al;
	}

	public void setAl(List<List<String>> al) {
		this.al = al;
	}

	public String[] getSalary_head_id() {
		return salary_head_id;
	}

	public void setSalary_head_id(String[] salary_head_id) {
		this.salary_head_id = salary_head_id;
	}

	public String[] getSalary_head_value() {
		return salary_head_value;
	}

	public void setSalary_head_value(String[] salary_head_value) {
		this.salary_head_value = salary_head_value;
	}

	public String[] getIsDisplay() {
		return isDisplay;
	}

	public void setIsDisplay(String[] isDisplay) {
		this.isDisplay = isDisplay;
	}

	public String[] getEmp_salary_id() {
		return emp_salary_id;
	}

	public void setEmp_salary_id(String[] emp_salary_id) {
		this.emp_salary_id = emp_salary_id;
	}

	public String[] getHideIsDisplay() {
		return hideIsDisplay;
	}

	public void setHideIsDisplay(String[] hideIsDisplay) {
		this.hideIsDisplay = hideIsDisplay;
	}

	public List<FillGrade> getGradeChangeList() {
		return gradeChangeList;
	}

	public void setGradeChangeList(List<FillGrade> gradeChangeList) {
		this.gradeChangeList = gradeChangeList;
	}

	public String getEmpChangeGrade() {
		return empChangeGrade;
	}

	public void setEmpChangeGrade(String empChangeGrade) {
		this.empChangeGrade = empChangeGrade;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getStrUpdateDocument() {
		return strUpdateDocument;
	}

	public void setStrUpdateDocument(String strUpdateDocument) {
		this.strUpdateDocument = strUpdateDocument;
	}

	public String getStrTransferType() {
		return strTransferType;
	}

	public void setStrTransferType(String strTransferType) {
		this.strTransferType = strTransferType;
	}

	public String getAppraisal_freq() {
		return appraisal_freq;
	}

	public void setAppraisal_freq(String appraisal_freq) {
		this.appraisal_freq = appraisal_freq;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}
	public String getCancel() {
		return cancel;
	}

	public void setCancel(String cancel) {
		this.cancel = cancel;
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
	
	public boolean getDisableSalaryStructure() {
		return disableSalaryStructure;
	}

	public void setDisableSalaryStructure(boolean disableSalaryStructure) {
		this.disableSalaryStructure = disableSalaryStructure;
	}

	public String getRecommendationOrFinalization() {
		return recommendationOrFinalization;
	}

	public void setRecommendationOrFinalization(String recommendationOrFinalization) {
		this.recommendationOrFinalization = recommendationOrFinalization;
	}

//===start parvez date: 21-03-2023===
	public String getAnscomment() {
		return anscomment;
	}

	public void setAnscomment(String anscomment) {
		this.anscomment = anscomment;
	}
//===end parvez date: 21-03-2023===	
}