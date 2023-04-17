package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;

public class AppraisalScoreStatus implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF;
	private String id;
	private String empid;
	private String type;
	private String memberId;
	private String appFreqId;
	private String userId;
	private String role;
	private String fromPage;
	private String operation;
	private String reopenComment;
	private String approveComment;	//created by parvez date: 08-07-2022

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";

		// System.out.println("getOperation() ======>> " + getOperation());
		if (getOperation() != null && getOperation().equalsIgnoreCase("Reopen")) {
			revokeUserFeedback();
			return "status";
		} else if (getOperation() != null && getOperation().equalsIgnoreCase("Revoke")) {
			revokeUserFeedback();
			return "status";
//===start parvez date: 08-07-2022===			
		} else if (getOperation() != null && getOperation().equalsIgnoreCase("Approve")) {
			approveUserFeedback();
			return "status";
		} else {
//===end parvez date: 08-07-2022===			
			getEmployeeAssignedKRAAndGoalTarget();
			checkCorrectionOrReviewerFeedback();
			getOrientationMember();
			request.setAttribute(PAGE, "/jsp/performance/AppraisalScoreStatus.jsp");
			request.setAttribute(TITLE, "Appraisal Status");

			getAppraisalQuestionsAnswers();
			getAppraisalQuestionAnswersExport();
			getAppraisalFinalStatus();
			// System.out.println("AppraisalScoreStatus java appFreqId==>"+getAppFreqId());
			request.setAttribute("empid", getEmpid());

			if (type != null && type.equals("popup")) {
				return "popup";
			}
			return "success";
		}
	}

	private void getEmployeeAssignedKRAAndGoalTarget() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		// boolean flag = false;
		try {
			con = db.makeConnection(con);
		
		//===start parvez date: 03-04-2023===	
			Map<String, String> hmDepartmentMap = CF.getEmpDepartmentMap(con); 
			request.setAttribute("hmDepartmentMap", hmDepartmentMap);
		//===end parvez date: 03-04-2023===	
			
			List<String> alKRAIds = new ArrayList<String>();
			String kraTypes = INDIVIDUAL_GOAL + "," + INDIVIDUAL_KRA + "," + EMPLOYEE_KRA;
			pst = con.prepareStatement("select * from goal_kras where goal_type in (" + kraTypes + ") and emp_ids like '%," + getEmpid() + ",%'");
			rs = pst.executeQuery();
			// System.out.println("pst ===>> " + pst);
			while (rs.next()) {
				alKRAIds.add(rs.getString("goal_kra_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("alKRAIds", alKRAIds);

			List<String> alGoarTargetIds = new ArrayList<String>();
			pst = con.prepareStatement("select * from goal_details where emp_ids like '%," + getEmpid() + ",%'");
			rs = pst.executeQuery();
			// System.out.println("pst ===>> " + pst);
			while (rs.next()) {
				alGoarTargetIds.add(rs.getString("goal_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("alGoarTargetIds", alGoarTargetIds);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void checkCorrectionOrReviewerFeedback() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			Map<String, String> existUserIds = new HashMap<String, String>();
			con = db.makeConnection(con);
			boolean flag = false;
			pst = con.prepareStatement("select * from appraisal_question_answer where emp_id=? and appraisal_id=? and user_type_id=? " + // and
																																			// user_id=?
					"and appraisal_freq_id=? and reviewer_or_appraiser=? and reviewer_id>0 and reviewer_user_type_id>0 ");
			pst.setInt(1, uF.parseToInt(getEmpid()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getMemberId()));
			pst.setInt(4, uF.parseToInt(getAppFreqId()));
			pst.setInt(5, 0);
			// System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				existUserIds.put(rs.getString("user_id"), rs.getString("user_id"));
			}
			rs.close();
			pst.close();
			// System.out.println("existUserIds ===>> " + existUserIds);
			request.setAttribute("existUserIds", existUserIds);

			pst = con.prepareStatement("select * from appraisal_question_answer where emp_id=? and appraisal_id=? and appraisal_freq_id=? " + // and
																																				// user_type_id=?
																																				// and
																																				// user_id=?
					"and reviewer_or_appraiser=?");
			pst.setInt(1, uF.parseToInt(getEmpid()));
			pst.setInt(2, uF.parseToInt(getId()));
			// pst.setInt(3, uF.parseToInt(getMemberId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			pst.setInt(4, 1);
			// System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();

			// System.out.println("flag ===>> " + flag);
			request.setAttribute("flag", flag);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void revokeUserFeedback() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			if (getOperation() != null && getOperation().equals("Reopen")) {
				pst = con.prepareStatement("insert into review_feedback_reopen_details (review_id,review_freq_id,emp_id,user_id,user_type_id,"
						+ "reviewer_or_appraiser,reopen_comment,reopened_by,entry_date) values (?,?,?,?, ?,?,?,?, ?)");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.setInt(2, uF.parseToInt(getAppFreqId()));
				pst.setInt(3, uF.parseToInt(getEmpid()));
				pst.setInt(4, uF.parseToInt(getUserId()));
				pst.setInt(5, uF.parseToInt(getMemberId()));
				pst.setInt(6, 0);
				pst.setString(7, getReopenComment());
				pst.setInt(8, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(9, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE + DBTIME));
				// System.out.println("pst1 ===>> " + pst);
				pst.execute();
				pst.close();
			}
			
	//===start parvez date: 06-07-2022===	
			
			Map<String,String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			String reviewName = CF.getReviewNameById(con, uF, getId());
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			String strDomain = request.getServerName().split("\\.")[0];
			
			if(uF.parseToBoolean(hmFeatureStatus.get(F_REVIEW_REOPEN_BY_HR_GHR_FOR_UPDATE_FEEDBACK)) && getOperation() != null && getOperation().equals("Reopen")){
				
				pst = con.prepareStatement("update appraisal_question_answer set is_submit=? where emp_id=? and appraisal_id=? and user_id=? and user_type_id=? "
						+ "and appraisal_freq_id=? and reviewer_or_appraiser=?");
				pst.setBoolean(1, false);
				pst.setInt(2, uF.parseToInt(getEmpid()));
				pst.setInt(3, uF.parseToInt(getId()));
				pst.setInt(4, uF.parseToInt(getUserId()));
				pst.setInt(5, uF.parseToInt(getMemberId()));
				pst.setInt(6, uF.parseToInt(getAppFreqId()));
				pst.setInt(7, 0);
				int x = pst.executeUpdate();
				pst.close();
//				System.out.println("role="+getRole());
				if(x > 0){
					String alertData = "<div style=\"float: left;\"> A Review ("+reviewName+") has reopen for role <b>Peer</b> by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
					String alertAction = "MyHR.action?pType=WR";
					
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(getUserId());
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					if(getRole()!=null && getRole().equalsIgnoreCase("Manager")){
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(MANAGER));
					} else if(getRole()!=null && getRole().equalsIgnoreCase("Peer")){
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
					} else if(getRole()!=null && getRole().equalsIgnoreCase("HR")){
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(HRMANAGER));
					} else if(getRole()!=null && getRole().equalsIgnoreCase("CEO")){
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(CEO));
					} else if(getRole()!=null && getRole().equalsIgnoreCase("HOD")){
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(HOD));
					} else {
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
					}
					
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
				}
				
			} else {
				pst = con.prepareStatement("delete from appraisal_question_answer where emp_id=? and appraisal_id=? and user_id=? and user_type_id=? "
						+ "and appraisal_freq_id=? and reviewer_or_appraiser=?");
				pst.setInt(1, uF.parseToInt(getEmpid()));
				pst.setInt(2, uF.parseToInt(getId()));
				pst.setInt(3, uF.parseToInt(getUserId()));
				pst.setInt(4, uF.parseToInt(getMemberId()));
				pst.setInt(5, uF.parseToInt(getAppFreqId()));
				pst.setInt(6, 0);
				// System.out.println("pst1 ===>> " + pst);
				pst.execute();
				pst.close();
			}

			/*pst = con.prepareStatement("delete from appraisal_question_answer where emp_id=? and appraisal_id=? and user_id=? and user_type_id=? "
					+ "and appraisal_freq_id=? and reviewer_or_appraiser=?");
			pst.setInt(1, uF.parseToInt(getEmpid()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getUserId()));
			pst.setInt(4, uF.parseToInt(getMemberId()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			pst.setInt(6, 0);
			// System.out.println("pst1 ===>> " + pst);
			pst.execute();
			pst.close();*/
	//===end parvez date: 06-07-2022===		
			
			if (getOperation() != null && getOperation().equals("Reopen")) {
				request.setAttribute("STATUS_MSG", SUCCESSM + "Feedback reopened successfully." + END);
			} else {
				request.setAttribute("STATUS_MSG", SUCCESSM + "Feedback revoked successfully." + END);
			}
		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", ERRORM + "Feedback not revoked, please try again." + END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getAppraisalQuestionAnswersExport() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String apprasialName = "";
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);

			Map<String, String> hmorientationMembers = new HashMap<String, String>();
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);

			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id = ? ");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				apprasialName = rs.getString("appraisal_name");
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from orientation_member");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmorientationMembers.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();

			request.setAttribute("hmorientationMembers", hmorientationMembers);
			request.setAttribute("hmEmpName", hmEmpName);
			List<String> sectionIdsList = new ArrayList<String>();
			Map<String, String> hmPriorUser = new HashMap<String, String>();
		//===start parvez date: 16-03-2023===	
//			pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id = ? ");
			if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) && (getRole()!=null 
					&& (getRole().equalsIgnoreCase("Peer") || getRole().equalsIgnoreCase("Other Peer") || getRole().equalsIgnoreCase("HOD")))){
				pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id = ? " +
						" and main_level_id not in (select main_level_id from appraisal_level_details where appraisal_id=? and appraisal_system!=2)");
				pst.setInt(2, uF.parseToInt(getId()));
			} else{
				pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id = ? ");
			}
		//===end parvez date: 16-03-2023===	
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				sectionIdsList.add(rs.getString("main_level_id"));
		//===start parvez date: 07-07-2022===		
				hmPriorUser.put("7", rs.getString("hr"));
				hmPriorUser.put("2", rs.getString("manager"));
				hmPriorUser.put("4", rs.getString("peer"));
				hmPriorUser.put("6", rs.getString("subordinate"));
				hmPriorUser.put("8", rs.getString("grouphead"));
				hmPriorUser.put("9", rs.getString("vendor"));
				hmPriorUser.put("11", rs.getString("client"));
				hmPriorUser.put("13", rs.getString("hod"));
				hmPriorUser.put("5", rs.getString("ceo"));
				hmPriorUser.put("14", rs.getString("other_peer"));
		//===end parvez date: 07-07-2022===		
			}
			rs.close();
			pst.close();
			request.setAttribute("sectionIdsList", sectionIdsList);
	//===start parvez date: 07-07-2022===		
			request.setAttribute("hmPriorUser", hmPriorUser);
	//===end parvez date: 07-07-2022===		

			pst = con.prepareStatement("select distinct aqd.appraisal_level_id,main_level_id,other_id,scorecard_id,app_system_type from "
					+ "appraisal_question_details aqd, appraisal_level_details ald where aqd.appraisal_level_id = ald.appraisal_level_id "
					+ "and aqd.appraisal_id = ? order by aqd.appraisal_level_id");

			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, String> hmAppSystemType = new HashMap<String, String>();

			Map<String, List<String>> hmSubsectionIds = new LinkedHashMap<String, List<String>>();
			while (rs.next()) {
				hmAppSystemType.put(rs.getString("appraisal_level_id"), rs.getString("app_system_type"));

				List<String> alLevelScore = hmSubsectionIds.get(rs.getString("main_level_id") + "SCR");
				if (alLevelScore == null)
					alLevelScore = new ArrayList<String>();

				List<String> alLevelOther = hmSubsectionIds.get(rs.getString("main_level_id") + "OTHR");
				if (alLevelOther == null)
					alLevelOther = new ArrayList<String>();

				List<String> alLevelGoalTargetKRA = hmSubsectionIds.get(rs.getString("main_level_id") + "GTK");
				if (alLevelGoalTargetKRA == null)
					alLevelGoalTargetKRA = new ArrayList<String>();

				if (uF.parseToInt(rs.getString("other_id")) > 0 && !alLevelOther.contains(rs.getString("appraisal_level_id"))) {
					alLevelOther.add(rs.getString("appraisal_level_id"));
				} else if (uF.parseToInt(rs.getString("scorecard_id")) > 0 && !alLevelScore.contains(rs.getString("appraisal_level_id"))) {
					alLevelScore.add(rs.getString("appraisal_level_id"));
				} else if (!alLevelGoalTargetKRA.contains(rs.getString("appraisal_level_id"))) {
					alLevelGoalTargetKRA.add(rs.getString("appraisal_level_id"));
				} else {
				}
				hmSubsectionIds.put(rs.getString("main_level_id") + "SCR", alLevelScore);
				hmSubsectionIds.put(rs.getString("main_level_id") + "OTHR", alLevelOther);
				hmSubsectionIds.put(rs.getString("main_level_id") + "GTK", alLevelGoalTargetKRA);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmSubsectionIds", hmSubsectionIds);
			request.setAttribute("hmAppSystemType", hmAppSystemType);

			pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id = ?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, String> hmSectionDetails = new HashMap<String, String>();
			double appWeightage = 0;
			while (rs.next()) {
				hmSectionDetails.put(rs.getString("main_level_id"), rs.getString("level_title"));
				hmSectionDetails.put(rs.getString("main_level_id") + "_SD", rs.getString("short_description"));
				hmSectionDetails.put(rs.getString("main_level_id") + "_LD", rs.getString("long_description"));
				
				appWeightage += uF.parseToDouble(rs.getString("section_weightage"));
			}
			rs.close();
			pst.close();

			request.setAttribute("hmSectionDetails", hmSectionDetails);

			Map hmScoreQuestionsMap = new HashMap();
			Map hmOtherQuestionsMap = new HashMap();
			Map hmGoalTargetKraQuestionsMap = new HashMap();
			Map hmLevelScoreMap = new HashMap();

			Map hmQuestionMarks = new HashMap();
			Map hmQuestionWeightage = new HashMap();

			List alRoles = new ArrayList();
			List rolesUserIds = new ArrayList();
			Map<String, List<String>> hmOuterpeerAppraisalDetails = new HashMap<String, List<String>>();

			Map hmQuestionAnswerReport = new HashMap();
			Map hmQuestionRemak = new HashMap();

			List<String> alKRAIds = (List<String>) request.getAttribute("alKRAIds");
			List<String> alGoarTargetIds = (List<String>) request.getAttribute("alGoarTargetIds");

			// ===start parvez date: 18-07-2022===
			Map<String, String> hmSectionComment = new HashMap<String, String>();
			Map hmReviewerMarksComment = new HashMap();
			// ===end parvez date: 18-07-2022===

			List alQuestion = new ArrayList();
			pst = con.prepareStatement("select * from appraisal_question_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getInt("app_system_type") == 4 && alKRAIds != null && rs.getString("kra_id") != null && !alKRAIds.contains(rs.getString("kra_id"))) {
					continue;
				}
				if ((rs.getInt("app_system_type") == 3 || rs.getInt("app_system_type") == 5) && alGoarTargetIds != null
						&& rs.getString("goal_kra_target_id") != null && !alGoarTargetIds.contains(rs.getString("goal_kra_target_id"))) {
					continue;
				}
				if (uF.parseToInt(rs.getString("other_id")) > 0) {
					alQuestion = (List) hmOtherQuestionsMap.get(rs.getString("appraisal_level_id"));
					if (alQuestion == null)
						alQuestion = new ArrayList();
					if (!alQuestion.contains(rs.getString("question_id"))) {
						alQuestion.add(rs.getString("question_id"));
					}
					hmOtherQuestionsMap.put(rs.getString("appraisal_level_id"), alQuestion);
				} else if (uF.parseToInt(rs.getString("scorecard_id")) > 0) {
					alQuestion = (List) hmScoreQuestionsMap.get(rs.getString("scorecard_id"));
					if (alQuestion == null)
						alQuestion = new ArrayList();
					if (!alQuestion.contains(rs.getString("question_id"))) {
						alQuestion.add(rs.getString("question_id"));
					}
					hmScoreQuestionsMap.put(rs.getString("scorecard_id"), alQuestion);

					List alScore = (List) hmLevelScoreMap.get(rs.getString("appraisal_level_id"));
					if (alScore == null)
						alScore = new ArrayList();
					if (!alScore.contains(rs.getString("scorecard_id"))) {
						alScore.add(rs.getString("scorecard_id"));
					}
					hmLevelScoreMap.put(rs.getString("appraisal_level_id"), alScore);
				} else {
					alQuestion = (List) hmGoalTargetKraQuestionsMap.get(rs.getString("appraisal_level_id"));
					if (alQuestion == null)
						alQuestion = new ArrayList();
					if (!alQuestion.contains(rs.getString("question_id"))) {
						alQuestion.add(rs.getString("question_id"));
					}
					hmGoalTargetKraQuestionsMap.put(rs.getString("appraisal_level_id"), alQuestion);
				}
				hmQuestionWeightage.put(rs.getString("question_id"), rs.getString("weightage"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from question_bank");
			rs = pst.executeQuery();
			Map<String, String> hmQueAnsType = new HashMap<String, String>();
			while (rs.next()) {
				hmQueAnsType.put(rs.getString("question_bank_id"), rs.getString("question_type"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from appraisal_answer_type_sub");
			rs = pst.executeQuery();
			Map<String, String> hmAnsTypeAnswer = new HashMap<String, String>();
			while (rs.next()) {
				hmAnsTypeAnswer.put(rs.getString("answer_type_id") + "_" + rs.getString("score"), rs.getString("score_label"));
			}
			rs.close();
			pst.close();

			if (uF.parseToInt(getMemberId()) > 0) {

				if (uF.parseToInt(getMemberId()) == 4 || uF.parseToInt(getMemberId()) == 10) {

					String avgMarks = "", avgWeightage = "";
					String avgMarksReviewer = "";
					String allAns = "", allRemark = "";
					int cnt = 0;
					String remark = "", ans = "";
					/*
					 * pst = con.prepareStatement(
					 * "select * from appraisal_question_answer where appraisal_id = ? and user_type_id=? and emp_id=?"
					 * );
					 */
					pst = con.prepareStatement("select *,(marks/weightcnt) as avgmarks,(reviewer_marks/weightcnt) as avgreviewer_marks FROM "
							+ "(select user_type_id,user_id,question_id,sum(marks) as marks,sum(reviewer_marks) as reviewer_marks,sum(weightage) as weightage,"
							+ "(sum(weightage)/COUNT(weightage)) as avgweightage,COUNT(weightage) as weightcnt from appraisal_question_answer where "
							+ "appraisal_id=? and user_type_id=? and emp_id=? and appraisal_freq_id=? and reviewer_or_appraiser=? "
							+ "group by user_type_id,user_id,question_id order by question_id)  as a");
					pst.setInt(1, uF.parseToInt(getId()));
					pst.setInt(2, uF.parseToInt(getMemberId()));
					pst.setInt(3, uF.parseToInt(getEmpid()));
					pst.setInt(4, uF.parseToInt(getAppFreqId()));
					if (getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
						pst.setInt(5, 1);
					} else {
						pst.setInt(5, 0);
					}
					rs = pst.executeQuery();
					// System.out.println("pst marks == " + pst);
					while (rs.next()) {
						List<String> peerAppraisalDetails = new ArrayList<String>();
						avgMarks = uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("avgmarks"));
						avgMarksReviewer = uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("avgreviewer_marks"));
						avgWeightage = uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("avgweightage"));

						if (!alRoles.contains(rs.getString("user_type_id"))) {
							alRoles.add(rs.getString("user_type_id"));
						}

						if (!rolesUserIds.contains(rs.getString("user_id"))) {
							rolesUserIds.add(rs.getString("user_id"));
						}
						peerAppraisalDetails.add(avgMarks);
						peerAppraisalDetails.add(avgWeightage);
						peerAppraisalDetails.add(avgMarksReviewer);
						hmOuterpeerAppraisalDetails.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
								peerAppraisalDetails);
						// System.out.println("peerAppraisalDetails == "+peerAppraisalDetails
						// + " remark == "+remark);
					}
					rs.close();
					pst.close();

					// System.out.println("hmOuterpeerAppraisalDetails ===> " +
					// hmOuterpeerAppraisalDetails);

					String que_id = "";
					pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id = ? and user_type_id=? and emp_id=? "
							+ "and appraisal_freq_id = ? and reviewer_or_appraiser=? order by question_id");
					pst.setInt(1, uF.parseToInt(getId()));
					pst.setInt(2, uF.parseToInt(getMemberId()));
					pst.setInt(3, uF.parseToInt(getEmpid()));
					pst.setInt(4, uF.parseToInt(getAppFreqId()));
					if (getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
						pst.setInt(5, 1);
					} else {
						pst.setInt(5, 0);
					}
					rs = pst.executeQuery();
					Map<String, String> hmOuterpeerAnsDetailsreport = new HashMap<String, String>();
					while (rs.next()) {
						// List<String> peerAnsDetails = new
						// ArrayList<String>();
						String ansType = hmQueAnsType.get(rs.getString("question_id"));
						String answer = null;
						if (uF.parseToInt(ansType) == 4) {
							answer = hmAnsTypeAnswer.get(ansType + "_" + rs.getString("answer")) + " (" + rs.getString("answer") + ")";
						} else if (uF.parseToInt(ansType) == 5 || uF.parseToInt(ansType) == 6) {
							String getans = rs.getString("answer").length() > 0 ? rs.getString("answer").substring(0, rs.getString("answer").length() - 1) : "";
							answer = hmAnsTypeAnswer.get(ansType + "_" + getans);
						} else if (uF.parseToInt(ansType) == 3) {
							String getans = rs.getString("marks");
							answer = getans;
						} else if (uF.parseToInt(ansType) == 11) {
						//===start parvez date: 10-03-2023===	
//							double getans = (rs.getDouble("marks") * 5) / rs.getDouble("weightage");
//							answer = uF.formatIntoOneDecimal(getans) + " / 5";
							double getans = 0;
							if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
								getans = (rs.getDouble("marks") * 10) / rs.getDouble("weightage");
								answer = uF.formatIntoOneDecimal(getans) + " / 10";
							} else{
								getans = (rs.getDouble("marks") * 5) / rs.getDouble("weightage");
								answer = uF.formatIntoOneDecimal(getans) + " / 5";
							}
						//===end parvez date: 10-03-2023===	
						} else {
							answer = rs.getString("answer");
						}
						String QuestionAns = hmOuterpeerAnsDetailsreport.get(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_"
								+ rs.getString("user_id"));
						// if(QuestionAns==null)QuestionAns=
						// useNameMP.get(rs.getString("user_id"))+ " : " +
						// uF.showData(answer,"")+"<span style=\"margin-right: 20px;font-size: 12px; float: right;\"> </span>";
						if (QuestionAns == null) {
							QuestionAns = uF.showData(answer, "No answer") + "-by " + hmEmpName.get(rs.getString("user_id"))
									+ uF.showData(" Comment-" + rs.getString("answers_comment"), "No comment");
						} else {
							// QuestionAns+="<br/> "+
							// useNameMP.get(rs.getString("user_id"))+ " : " +
							// uF.showData(answer,"");
							QuestionAns += uF.showData(answer, "No answer") + "by " + hmEmpName.get(rs.getString("user_id"))
									+ uF.showData(" Comment-" + rs.getString("answers_comment"), "No comment");
						}

						if (rs.getString("remark") != null) {
							remark += rs.getString("remark");
						}
						hmOuterpeerAnsDetailsreport.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
								QuestionAns);

						// ===start parvez date: 16-04-2022===
						hmSectionComment.put(rs.getString("section_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
								rs.getString("section_comment"));
						// ===end parvez date: 16-04-2022===

					}
					rs.close();
					pst.close();

					// System.out.println("hmOuterpeerAnsDetails == "+hmOuterpeerAnsDetails
					// );
					request.setAttribute("hmOuterpeerAnsDetailsreport", hmOuterpeerAnsDetailsreport);

				} else {
				//===start parvez date: 01-03-2023===	
					/*pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id=? and user_type_id=? and emp_id=? "
							+ " and appraisal_freq_id=? and reviewer_or_appraiser=?");*/
					pst = con.prepareStatement("select aqa.*,score_calculation_basis from appraisal_question_answer aqa,appraisal_question_details aqd " +
							"where aqa.appraisal_id=? and aqa.user_type_id=? and aqa.emp_id=? and aqa.appraisal_freq_id=? and aqa.reviewer_or_appraiser=? " +
							" and aqa.appraisal_question_details_id=aqd.appraisal_question_details_id");
				//===end parvez date: 01-03-2023===	
					pst.setInt(1, uF.parseToInt(getId()));
					pst.setInt(2, uF.parseToInt(getMemberId()));
					pst.setInt(3, uF.parseToInt(getEmpid()));
					pst.setInt(4, uF.parseToInt(getAppFreqId()));
					if (getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
						pst.setInt(5, 1);
					} else {
						pst.setInt(5, 0);
					}
//					System.out.println("AScSt/672---pst==>"+pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						String ansType = hmQueAnsType.get(rs.getString("question_id"));
						String answer = null, answerWithUser = null;
						if (uF.parseToInt(ansType) == 4) {
							answer = hmAnsTypeAnswer.get(ansType + "_" + rs.getString("answer")) + " (" + rs.getString("answer") + ")";
						} else if (uF.parseToInt(ansType) == 5 || uF.parseToInt(ansType) == 6) {
							String getans = rs.getString("answer").length() > 0 ? rs.getString("answer").substring(0, rs.getString("answer").length() - 1) : "";
							answer = hmAnsTypeAnswer.get(ansType + "_" + getans);
						} else if (uF.parseToInt(ansType) == 3) {
							String getans = rs.getString("marks");
							answer = getans;
						} else if (uF.parseToInt(ansType) == 11) {
						//===start parvez date: 10-03-2023===	
//							double getans = (rs.getDouble("marks") * 5) / rs.getDouble("weightage");
//							answer = uF.formatIntoOneDecimal(getans) + " / 5";
							double getans = 0;
							if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
								getans = (rs.getDouble("marks") * 10) / rs.getDouble("weightage");
								answer = uF.formatIntoOneDecimal(getans) + " / 10";
							}else{
								getans = (rs.getDouble("marks") * 5) / rs.getDouble("weightage");
								answer = uF.formatIntoOneDecimal(getans) + " / 5";
							}
						//===end parvez date: 10-03-2023===	
							
						} else {
							answer = rs.getString("answer");
						}

					//===start parvez date: 01-03-2023===	
//						hmQuestionMarks.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
//								uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("marks")));
						if(uF.parseToBoolean(rs.getString("score_calculation_basis"))){
							hmQuestionMarks.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
									uF.formatIntoOneDecimal((rs.getDouble("marks") * 5) / rs.getDouble("weightage")));
						} else{
							hmQuestionMarks.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
									uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("marks")));
						}
					//===end parvez date: 01-03-2023===	
						
						hmQuestionWeightage.put(rs.getString("question_id"), rs.getString("weightage"));

						if (rs.getString("answer") != null || rs.getString("marks") != null) {
							// System.out.println("useNameMP ===> " +
							// useNameMP+" User ID ===> "+rs.getString("user_id"));
							answerWithUser = uF.showData(answer, "No answer") + "- by " + hmEmpName.get(rs.getString("user_id"))
									+ uF.showData(" Comment-" + rs.getString("answers_comment"), "No comment");
							hmQuestionAnswerReport.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
									answerWithUser);
						}
						if (rs.getString("remark") != null) {
							hmQuestionRemak.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
									rs.getString("remark"));
						}
						if (!alRoles.contains(rs.getString("user_type_id"))) {
							alRoles.add(rs.getString("user_type_id"));
						}

						if (!rolesUserIds.contains(rs.getString("user_id"))) {
							rolesUserIds.add(rs.getString("user_id"));
						}

						// ===start parvez date: 13-04-2022===
						hmSectionComment.put(rs.getString("section_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
								rs.getString("section_comment"));
						// ===end parvez date: 13-04-2022===
					}
					rs.close();
					pst.close();
					
					//===start parvez date: 18-07-2022===
					if(uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) && uF.parseToInt(getMemberId()) == 13){
						pst = con.prepareStatement("select * from reviewer_feedback_details where appraisal_id=? and user_type_id=? and emp_id=? and "
								+ "appraisal_freq_id=? ");
						pst.setInt(1, uF.parseToInt(getId()));
						pst.setInt(2, uF.parseToInt(getMemberId()));
						pst.setInt(3, uF.parseToInt(getEmpid()));
						pst.setInt(4, uF.parseToInt(getAppFreqId()));
						rs = pst.executeQuery();
//						System.out.println("ASS/728---pst="+pst);
						while (rs.next()) {
							
							double getans = (rs.getDouble("reviewer_marks") * 5) / appWeightage;
							String answer = uF.formatIntoOneDecimal(getans) + " / 5";
							
							String answerWithUser = "<div style=\"line-height: 13px;\">" + uF.showData(answer, "No answer")
							+ "<span style=\"margin-right: 10px;font-size: 10px; float: right; font-style: italic;\">- by "
							+ hmEmpName.get(rs.getString("user_id")) + "</span></div>"
							+ "<div style=\"line-height: 13px; width: 100%; font-style: italic; font-size: 11px;\">"
							+ uF.showData(rs.getString("reviewer_comment"), "No comment") + "</div>";
							
							hmReviewerMarksComment.put(rs.getString("user_type_id") + "_" + rs.getString("user_id"), answerWithUser);
							hmReviewerMarksComment.put(rs.getString("user_type_id") + "_" + rs.getString("user_id")+"_MARKS", rs.getString("reviewer_marks"));
							hmReviewerMarksComment.put(rs.getString("user_type_id") + "_" + rs.getString("user_id")+"_WEIGHTAGE", appWeightage+"");
							
							if (!alRoles.contains(rs.getString("user_type_id"))) {
								alRoles.add(rs.getString("user_type_id"));
							}
							
							if (!rolesUserIds.contains(rs.getString("user_id"))) {
								rolesUserIds.add(rs.getString("user_id"));
							}
						}
						rs.close();
						pst.close();
					}
				//===end parvez date: 18-07-2022===
				}
				// System.out.println("alRoles ========> "+alRoles);
			} else {
			//===start parvez date: 02-03-2023===	
				/*pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id=? and emp_id=? and appraisal_freq_id=? "
						+ " and reviewer_or_appraiser=? order by user_type_id");*/
				pst = con.prepareStatement("select aqa.*,score_calculation_basis from appraisal_question_answer aqa,appraisal_question_details aqd " +
						" where aqa.appraisal_id=? and aqa.emp_id=? and aqa.appraisal_freq_id=? and aqa.reviewer_or_appraiser=? " +
						" and aqa.appraisal_question_details_id=aqd.appraisal_question_details_id order by user_type_id");
			//===end parvez date: 02-03-2023===	
				pst.setInt(1, uF.parseToInt(getId()));
				pst.setInt(2, uF.parseToInt(getEmpid()));
				pst.setInt(3, uF.parseToInt(getAppFreqId()));
				if (getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
					pst.setInt(4, 1);
				} else {
					pst.setInt(4, 0);
				}
//				System.out.println("AScSt/774---pst==>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					String ansType = hmQueAnsType.get(rs.getString("question_id"));
					String answer = null, answerWithUser = null;
					if (uF.parseToInt(ansType) == 4) {
						answer = hmAnsTypeAnswer.get(ansType + "_" + rs.getString("answer")) + " (" + rs.getString("answer") + ")";
					} else if (uF.parseToInt(ansType) == 5 || uF.parseToInt(ansType) == 6) {
						String getans = rs.getString("answer").length() > 0 ? rs.getString("answer").substring(0, rs.getString("answer").length() - 1) : "";
						answer = hmAnsTypeAnswer.get(ansType + "_" + getans);
					} else if (uF.parseToInt(ansType) == 3) {
						String getans = rs.getString("marks");
						answer = getans;
					} else if (uF.parseToInt(ansType) == 11) {
//						double getans = (rs.getDouble("marks") * 5) / rs.getDouble("weightage");
//						answer = uF.formatIntoOneDecimal(getans) + " / 5";
						double getans = 0;
						if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
							getans = (rs.getDouble("marks") * 10) / rs.getDouble("weightage");
							answer = uF.formatIntoOneDecimal(getans) + " / 10";
						} else{
							getans = (rs.getDouble("marks") * 5) / rs.getDouble("weightage");
							answer = uF.formatIntoOneDecimal(getans) + " / 5";
						}
					} else {
						answer = rs.getString("answer");
					}

				//===start parvez date: 02-03-2023===	
//					hmQuestionMarks.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
//							uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("marks")));
					if(uF.parseToBoolean(rs.getString("score_calculation_basis"))){
						hmQuestionMarks.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
								uF.formatIntoOneDecimal((rs.getDouble("marks") * 5) / rs.getDouble("weightage")));
					}else{
						hmQuestionMarks.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
								uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("marks")));
					}
				//===end parvez date: 02-03-2023===	
					hmQuestionWeightage.put(rs.getString("question_id"), rs.getString("weightage"));

					if (rs.getString("answer") != null || rs.getString("marks") != null) {
						// System.out.println("questionId==>"+rs.getString("question_id")+"==>answer==>"+answer+"==>Marks==>"+rs.getString("marks")+"==>comment==> "+rs.getString("answers_comment"));
						answerWithUser = uF.showData(answer, "No answer") + "- by " + hmEmpName.get(rs.getString("user_id"))
								+ uF.showData(" Comment-" + rs.getString("answers_comment"), "No comment");
						hmQuestionAnswerReport.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
								answerWithUser);
					}
					if (rs.getString("remark") != null) {
						hmQuestionRemak.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
								rs.getString("remark"));
					}

					if (!alRoles.contains(rs.getString("user_type_id"))) {
						alRoles.add(rs.getString("user_type_id"));
					}

					if (!rolesUserIds.contains(rs.getString("user_id"))) {
						rolesUserIds.add(rs.getString("user_id"));
					}

					// ===start parvez date: 13-04-2022===
					hmSectionComment.put(rs.getString("section_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
							rs.getString("section_comment"));
					// ===end parvez date: 13-04-2022===
				}
				rs.close();
				pst.close();
				// System.out.println("alRoles else ========> "+alRoles);
				//===start parvez date: 18-07-2022===
				if(uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) && uF.parseToInt(getMemberId()) == 13){
					pst = con.prepareStatement("select * from reviewer_feedback_details where appraisal_id=? and user_type_id=? and emp_id=? and "
							+ "appraisal_freq_id=? ");
					pst.setInt(1, uF.parseToInt(getId()));
					pst.setInt(2, uF.parseToInt(getMemberId()));
					pst.setInt(3, uF.parseToInt(getEmpid()));
					pst.setInt(4, uF.parseToInt(getAppFreqId()));
					rs = pst.executeQuery();
//					System.out.println("ASS/811---pst="+pst);
					while (rs.next()) {
						
						double getans = (rs.getDouble("reviewer_marks") * 5) / appWeightage;
						String answer = uF.formatIntoOneDecimal(getans) + " / 5";
						
						String answerWithUser = "<div style=\"line-height: 13px;\">" + uF.showData(answer, "No answer")
						+ "<span style=\"margin-right: 10px;font-size: 10px; float: right; font-style: italic;\">- by "
						+ hmEmpName.get(rs.getString("user_id")) + "</span></div>"
						+ "<div style=\"line-height: 13px; width: 100%; font-style: italic; font-size: 11px;\">"
						+ uF.showData(rs.getString("reviewer_comment"), "No comment") + "</div>";
						
						hmReviewerMarksComment.put(rs.getString("user_type_id") + "_" + rs.getString("user_id"), answerWithUser);
						hmReviewerMarksComment.put(rs.getString("user_type_id") + "_" + rs.getString("user_id")+"_MARKS", rs.getString("reviewer_marks"));
						hmReviewerMarksComment.put(rs.getString("user_type_id") + "_" + rs.getString("user_id")+"_WEIGHTAGE", appWeightage+"");
						
						if (!alRoles.contains(rs.getString("user_type_id"))) {
							alRoles.add(rs.getString("user_type_id"));
						}
						
						if (!rolesUserIds.contains(rs.getString("user_id"))) {
							rolesUserIds.add(rs.getString("user_id"));
						}
					}
					rs.close();
					pst.close();
				}
			//===end parvez date: 18-07-2022===
			}

			// System.out.println("hmQuestionMarks ===> "+hmQuestionMarks);
			// System.out.println("hmQuestionWeightage ===> "+hmQuestionWeightage);

			pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id = ?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, String> hmLevel = new HashMap<String, String>();
			while (rs.next()) {
				hmLevel.put(rs.getString("appraisal_level_id"), rs.getString("level_title"));
				hmLevel.put(rs.getString("appraisal_level_id") + "_SD", rs.getString("short_description"));
				hmLevel.put(rs.getString("appraisal_level_id") + "_LD", rs.getString("long_description"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from appraisal_scorecard_details where appraisal_id = ?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, String> hmScoreCard = new HashMap<String, String>();
			while (rs.next()) {
				hmScoreCard.put(rs.getString("scorecard_id"), rs.getString("scorecard_section_name"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from question_bank");
			rs = pst.executeQuery();
			Map<String, String> hmQuestions = new HashMap<String, String>();
			Map<String, List<String>> hmOptions = new HashMap<String, List<String>>();

			while (rs.next()) {
				hmQuestions.put(rs.getString("question_bank_id"), rs.getString("question_text"));

				List alOptions = new ArrayList();
				alOptions.add(rs.getString("option_a"));// 0
				alOptions.add(rs.getString("option_b"));// 1
				alOptions.add(rs.getString("option_c"));// 2
				alOptions.add(rs.getString("option_d"));// 3
				alOptions.add(rs.getString("question_type"));// 4

				hmOptions.put(rs.getString("question_bank_id"), alOptions);
			}
			rs.close();
			pst.close();

			// ===start parvez date: 13-04-2022===
			Map<String, String> hmStrengthImprovements = new HashMap<String, String>();
			pst = con.prepareStatement("select * from reviewee_strength_improvements where emp_id=? and review_id=? and review_freq_id=? and user_type_id=?");
			pst.setInt(1, uF.parseToInt(getEmpid()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			pst.setInt(4, uF.parseToInt(getMemberId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmStrengthImprovements.put(rs.getString("user_id") + "_STRENGTH", rs.getString("areas_of_strength"));
				hmStrengthImprovements.put(rs.getString("user_id") + "_IMPROVEMENT", rs.getString("areas_of_improvement"));
			}
			rs.close();
			pst.close();
			// ===end parvez date: 13-04-2022===

			request.setAttribute("hmLevel", hmLevel);
			request.setAttribute("hmScoreCard", hmScoreCard);
			request.setAttribute("hmQuestions", hmQuestions);
			request.setAttribute("hmOptions", hmOptions);

//			System.out.println("ApSSt/948--hmQuestionMarks=="+hmQuestionMarks);
			request.setAttribute("hmQuestionMarks", hmQuestionMarks);
			request.setAttribute("hmQuestionWeightage", hmQuestionWeightage);
			request.setAttribute("hmQuestionAnswerReport", hmQuestionAnswerReport);
			request.setAttribute("hmQuestionRemak", hmQuestionRemak);
			request.setAttribute("alRoles", alRoles);
			request.setAttribute("rolesUserIds", rolesUserIds);
			request.setAttribute("hmOuterpeerAppraisalDetails", hmOuterpeerAppraisalDetails);

			request.setAttribute("hmScoreQuestionsMap", hmScoreQuestionsMap);
			request.setAttribute("hmOtherQuestionsMap", hmOtherQuestionsMap);
			request.setAttribute("hmLevelScoreMap", hmLevelScoreMap);
			request.setAttribute("hmGoalTargetKraQuestionsMap", hmGoalTargetKraQuestionsMap);

			request.setAttribute("hmSectionComment", hmSectionComment);
			request.setAttribute("hmStrengthImprovements", hmStrengthImprovements);
			
			request.setAttribute("hmReviewerMarksComment", hmReviewerMarksComment);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();

		Map<String, String> hmorientationMembers = (Map) request.getAttribute("hmorientationMembers");
		Map hmScoreQuestionsMap = (Map) request.getAttribute("hmScoreQuestionsMap");
		Map hmOtherQuestionsMap = (Map) request.getAttribute("hmOtherQuestionsMap");
		Map hmGoalTargetKraQuestionsMap = (Map) request.getAttribute("hmGoalTargetKraQuestionsMap");
		Map hmLevelScoreMap = (Map) request.getAttribute("hmLevelScoreMap");
		Map hmLevel = (Map) request.getAttribute("hmLevel");
		Map hmQuestions = (Map) request.getAttribute("hmQuestions");
		Map hmScoreCard = (Map) request.getAttribute("hmScoreCard");
		Map hmOptions = (Map) request.getAttribute("hmOptions");
		String memberId = (String) request.getAttribute("memberId");
		Map hmQuestionMarks = (Map) request.getAttribute("hmQuestionMarks");
		Map hmQuestionWeightage = (Map) request.getAttribute("hmQuestionWeightage");
		Map hmQuestionAnswer = (Map) request.getAttribute("hmQuestionAnswerReport");
		Map hmQuestionRemak = (Map) request.getAttribute("hmQuestionRemak");
		List alRoles = (List) request.getAttribute("alRoles");
		List rolesUserIds = (List) request.getAttribute("rolesUserIds");
		Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
		if (hmEmpName == null)
			hmEmpName = new HashMap<String, String>();
		Map<String, List<String>> hmOuterpeerAppraisalDetails = (Map<String, List<String>>) request.getAttribute("hmOuterpeerAppraisalDetails");
		Map<String, String> hmOuterpeerAnsDetails = (Map<String, String>) request.getAttribute("hmOuterpeerAnsDetailsreport");
		List<String> sectionIdsList = (List<String>) request.getAttribute("sectionIdsList");
		if (sectionIdsList == null)
			sectionIdsList = new ArrayList<String>();
		Map<String, List<String>> hmSubsectionIds = (Map<String, List<String>>) request.getAttribute("hmSubsectionIds");
		if (hmSubsectionIds == null)
			hmSubsectionIds = new LinkedHashMap<String, List<String>>();
		Map<String, String> hmSectionDetails = (Map<String, String>) request.getAttribute("hmSectionDetails");

		// ===start parvez date: 16-04-2022===
		Map<String, String> hmSectionComment = (Map<String, String>) request.getAttribute("hmSectionComment");
		if (hmSectionComment == null)
			hmSectionComment = new HashMap<String, String>();
		Map<String, String> hmStrengthImprovements = (Map<String, String>) request.getAttribute("hmStrengthImprovements");
		if (hmStrengthImprovements == null)
			hmStrengthImprovements = new HashMap<String, String>();
		// ===end parvez date: 16-04-2022===

		// Map<String, String> empNameMP = CF.getEmpNameMap(con, null,
		// getEmpid());
		// System.out.println("Name==>"+empNameMP.get(getEmpid()));
		if (hmSectionDetails == null)
			hmSectionDetails = new HashMap<String, String>();

		if (rolesUserIds != null && !rolesUserIds.isEmpty() && rolesUserIds.size() > 0) {
			for (int m = 0; rolesUserIds != null && m < rolesUserIds.size(); m++) {
				alInnerExport.add(new DataStyle(hmEmpName.get(getEmpid()) + "_" + apprasialName + "_" + hmorientationMembers.get(memberId),
						Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(hmEmpName.get(rolesUserIds.get(m)) + " (" + hmorientationMembers.get(memberId) + ")", Element.ALIGN_CENTER,
						"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				reportListExport.add(alInnerExport);
				for (int a = 0; sectionIdsList != null && !sectionIdsList.isEmpty() && a < sectionIdsList.size(); a++) {
					List<String> alLevelScore = hmSubsectionIds.get(sectionIdsList.get(a) + "SCR");
					int cnt = 0;
					alInnerExport = new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(a + 1 + ") " + hmSectionDetails.get(sectionIdsList.get(a)), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",
							BaseColor.LIGHT_GRAY));
					reportListExport.add(alInnerExport);
					// System.out.println("SEction"+uF.showData(hmSectionDetails.get(sectionIdsList.get(a)+"_SD"),
					// ""));
					// System.out.println("SEction"+uF.showData(hmSectionDetails.get(sectionIdsList.get(a)+"_LD"),
					// ""));
					alInnerExport = new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(uF.showData(hmSectionDetails.get(sectionIdsList.get(a) + "_SD"), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6,
							"0", "0", BaseColor.LIGHT_GRAY));
					reportListExport.add(alInnerExport);
					alInnerExport = new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(uF.showData(hmSectionDetails.get(sectionIdsList.get(a) + "_LD"), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6,
							"0", "0", BaseColor.LIGHT_GRAY));
					reportListExport.add(alInnerExport);
					for (int i = 0; alLevelScore != null && i < alLevelScore.size(); i++) {
						cnt++;
						List alScore = (List) hmLevelScoreMap.get((String) alLevelScore.get(i));
						if (alScore != null && !alScore.isEmpty()) {

							alInnerExport = new ArrayList<DataStyle>();
							alInnerExport.add(new DataStyle(a + 1 + ") " + hmSectionDetails.get(sectionIdsList.get(a)), Element.ALIGN_CENTER, "NEW_ROMAN", 6,
									"0", "0", BaseColor.LIGHT_GRAY));
							reportListExport.add(alInnerExport);
							alInnerExport.add(new DataStyle(uF.showData(hmSectionDetails.get(sectionIdsList.get(a) + "_SD"), ""), Element.ALIGN_CENTER,
									"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport = new ArrayList<DataStyle>();
							alInnerExport.add(new DataStyle(uF.showData(hmSectionDetails.get(sectionIdsList.get(a) + "_LD"), ""), Element.ALIGN_CENTER,
									"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							reportListExport.add(alInnerExport);
							// System.out.println("new"+a+1+"."+cnt+" "+hmLevel.get((String)alLevelScore.get(i)));
							// System.out.println(""+uF.showData((String)hmLevel.get((String)alLevelScore.get(i)+"_SD"),
							// ""));
							// System.out.println(""+uF.showData((String)hmLevel.get((String)alLevelScore.get(i)+"_LD"),
							// ""));
							alInnerExport = new ArrayList<DataStyle>();
							alInnerExport.add(new DataStyle("Competencies", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle("Question", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							for (int r = 0; alRoles != null && r < alRoles.size(); r++) {
								// System.out.print("Marks Role"+hmorientationMembers.get((String)alRoles.get(r)));
								alInnerExport.add(new DataStyle("Marks Role" + hmorientationMembers.get((String) alRoles.get(r)), Element.ALIGN_CENTER,
										"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							}
							alInnerExport.add(new DataStyle("Weightage %", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							reportListExport.add(alInnerExport);
							for (int s = 0; alScore != null && s < alScore.size(); s++) {
								// single row
								List alQuestions = (List) hmScoreQuestionsMap.get(alScore.get(s));
								for (int q = 0; alQuestions != null && q < alQuestions.size(); q++) {
									List alOptions = (List) hmOptions.get((String) alQuestions.get(q));
									alInnerExport = new ArrayList<DataStyle>();
									// System.out.println(""+hmScoreCard.get((String)alScore.get(s)));
									// System.out.println(""+q+1+")"+hmQuestions.get((String)alQuestions.get(q)));
									alInnerExport.add(new DataStyle(hmScoreCard.get((String) alScore.get(s)).toString(), Element.ALIGN_CENTER, "NEW_ROMAN", 6,
											"0", "0", BaseColor.LIGHT_GRAY));
									// alInnerExport.add(new
									// DataStyle(q+1+" "+hmQuestions.get((String)alQuestions.get(q)),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

									alInnerExport.add(new DataStyle(q + 1 + ")" + hmQuestions.get((String) alQuestions.get(q)), Element.ALIGN_CENTER,
											"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));

									for (int rr = 0; alRoles != null && rr < alRoles.size(); rr++) {
										if (uF.parseToInt(alRoles.get(rr).toString()) == 4 || uF.parseToInt(alRoles.get(rr).toString()) == 10) {
											List<String> innList = hmOuterpeerAppraisalDetails.get(alQuestions.get(q) + "_" + alRoles.get(rr) + "_"
													+ rolesUserIds.get(m));
											if (innList == null)
												innList = new ArrayList<String>();
											if (innList != null && !innList.isEmpty()) {

												alInnerExport.add(new DataStyle(uF.showData((String) innList.get(0), "Not Rated"), Element.ALIGN_CENTER,
														"NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
												alInnerExport.add(new DataStyle(uF.showData((String) innList.get(1), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6,
														"0", "0", BaseColor.WHITE));
												// System.out.println(uF.showData((String)innList.get(0),
												// "Not Rated"));
												// System.out.println(uF.showData((String)innList.get(1),
												// ""));
											} else {
												alInnerExport.add(new DataStyle("Not Rated", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
												// System.out.println("Not Rated");
											}
										} else {
											// System.out.println(uF.showData((String)hmQuestionMarks.get((String)alQuestions.get(q)+"_"+alRoles.get(rr)+"_"+rolesUserIds.get(m)),
											// "Not Rated"));
											alInnerExport.add(new DataStyle(uF.showData(
													(String) hmQuestionMarks.get((String) alQuestions.get(q) + "_" + alRoles.get(rr) + "_"
															+ rolesUserIds.get(m)), "Not Rated"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",
													BaseColor.WHITE));
											// System.out.println(hmQuestionWeightage.get((String)alQuestions.get(q)));
											alInnerExport.add(new DataStyle("" + hmQuestionWeightage.get((String) alQuestions.get(q)), Element.ALIGN_CENTER,
													"NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
										}
									}
									reportListExport.add(alInnerExport);
									StringBuilder sb = new StringBuilder();
									alInnerExport = new ArrayList<DataStyle>();
									alInnerExport.add(new DataStyle(" ", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
									if (alOptions != null) {

										int nOptionType = uF.parseToInt((String) alOptions.get(4));
										switch (nOptionType) {
											case 1 :
												sb.append(" " + "a)" + (String) alOptions.get(0));
												sb.append(" " + "b)" + (String) alOptions.get(1));
												sb.append(" " + "c)" + (String) alOptions.get(2));
												sb.append(" " + "d)" + (String) alOptions.get(3));
												// System.out.println("a)"+(String)alOptions.get(0));
												// System.out.println("b)"+(String)alOptions.get(1));
												// System.out.println("c)"+(String)alOptions.get(2));
												// System.out.println("d)"+(String)alOptions.get(3));
												break;
											case 2 :
												sb.append(" " + "a)" + (String) alOptions.get(0));
												sb.append(" " + "b)" + (String) alOptions.get(1));
												sb.append(" " + "c)" + (String) alOptions.get(2));
												sb.append(" " + "d)" + (String) alOptions.get(3));
												// System.out.println("a)"+(String)alOptions.get(0));
												// System.out.println("b)"+(String)alOptions.get(1));
												// System.out.println("c)"+(String)alOptions.get(2));
												// System.out.println("d)"+(String)alOptions.get(3));
												break;
											case 3 :
												break;
											case 4 :
												break;
											case 5 :
												sb.append(" " + "a)" + (String) alOptions.get(0));
												sb.append(" " + "b)" + (String) alOptions.get(1));

												// System.out.println("a)"+(String)alOptions.get(0));
												// System.out.println("b)"+(String)alOptions.get(1));
												break;

											case 6 :
												sb.append(" " + "a)" + (String) alOptions.get(0));
												sb.append(" " + "b)" + (String) alOptions.get(1));

												// System.out.println("a)"+(String)alOptions.get(0));
												// System.out.println("b)"+(String)alOptions.get(1));
												break;
											case 7 :
												break;
											case 8 :
												sb.append(" " + "a)" + (String) alOptions.get(0));
												sb.append(" " + "b)" + (String) alOptions.get(1));
												sb.append(" " + "c)" + (String) alOptions.get(2));
												sb.append(" " + "d)" + (String) alOptions.get(3));
												// System.out.println("a)"+(String)alOptions.get(0));
												// System.out.println("b)"+(String)alOptions.get(1));
												// System.out.println("c)"+(String)alOptions.get(2));
												// System.out.println("d)"+(String)alOptions.get(3));
												break;

											case 9 :
												sb.append(" " + "a)" + (String) alOptions.get(0));
												sb.append(" " + "b)" + (String) alOptions.get(1));
												sb.append(" " + "c)" + (String) alOptions.get(2));
												sb.append(" " + "d)" + (String) alOptions.get(3));
												// System.out.println("a)"+(String)alOptions.get(0));
												// System.out.println("b)"+(String)alOptions.get(1));
												// System.out.println("c)"+(String)alOptions.get(2));
												// System.out.println("d)"+(String)alOptions.get(3));
												break;

											case 10 :
												break;

											case 11 :
												break;
											case 12 :
												break;
										}
									}

									for (int r = 0; alRoles != null && r < alRoles.size(); r++) {
										if (r == 0) {

											sb = sb.append(" Answer/Comments:");
											// System.out.println("Answer/Comments:");
										}
										if (uF.parseToInt(alRoles.get(0).toString()) == 4 || uF.parseToInt(alRoles.get(0).toString()) == 10) {
											String queAns = hmOuterpeerAnsDetails.get(alQuestions.get(q) + "_" + alRoles.get(r) + "_" + rolesUserIds.get(m));
											// System.out.println(uF.showData(queAns,
											// ""));
											alInnerExport.add(new DataStyle(uF.showData(queAns, ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",
													BaseColor.WHITE));

										} else {
											if (hmQuestionAnswer.containsKey((String) alQuestions.get(q) + "_" + alRoles.get(r) + "_" + rolesUserIds.get(m))) {
												String strAns = ((String) hmQuestionAnswer.get((String) alQuestions.get(q) + "_" + alRoles.get(r) + "_"
														+ rolesUserIds.get(m)));
												if (strAns != null) {
													strAns = strAns.replace(":_:", "<br/>");
													// System.out.println(uF.showData(strAns,
													// ""));

													// System.out.println("answer"+uF.showData(strAns,
													// ""));
													// sb
													// =sb+uF.showData(strAns,
													// "");
													sb.append(uF.showData(strAns, ""));
													// System.out.println("sb"+sb.toString());
													alInnerExport.add(new DataStyle(uF.showData(sb.toString(), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0",
															"0", BaseColor.WHITE));
													reportListExport.add(alInnerExport);
												}
											}
										}
									}

								}

								// single row end
							}
						// ===start parvez date: 16-04-2022===
							StringBuilder strCSb = new StringBuilder();
							strCSb.append("Section Comment: ");
							strCSb.append(uF.showData((String) hmSectionComment.get(sectionIdsList.get(a) + "_" + memberId + "_" + rolesUserIds.get(m)), "")
									+ "");
							alInnerExport = new ArrayList<DataStyle>();
							alInnerExport
									.add(new DataStyle(uF.showData(strCSb.toString(), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
							reportListExport.add(alInnerExport);
						// ===end parvez date: 16-04-2022===

						}
					}

					// ---------------
					List<String> alLevelOther = hmSubsectionIds.get(sectionIdsList.get(a) + "OTHR");
					for (int i = 0; alLevelOther != null && i < alLevelOther.size(); i++) {
						cnt++;
						List alQuestions = (List) hmOtherQuestionsMap.get((String) alLevelOther.get(i));
						alInnerExport = new ArrayList<DataStyle>();
						alInnerExport.add(new DataStyle(a + 1 + "." + cnt + " " + hmLevel.get((String) alLevelOther.get(i)), Element.ALIGN_CENTER, "NEW_ROMAN",
								6, "0", "0", BaseColor.WHITE));
						reportListExport.add(alInnerExport);
						// System.out.println(a+1+"."+cnt+" "+hmLevel.get((String)alLevelOther.get(i)));
						alInnerExport = new ArrayList<DataStyle>();
						alInnerExport.add(new DataStyle(uF.showData((String) hmLevel.get((String) alLevelOther.get(i) + "_SD"), ""), Element.ALIGN_CENTER,
								"NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
						reportListExport.add(alInnerExport);
						// System.out.println(uF.showData((String)hmLevel.get((String)alLevelOther.get(i)+"_SD"),
						// ""));
						alInnerExport = new ArrayList<DataStyle>();
						alInnerExport.add(new DataStyle(uF.showData((String) hmLevel.get((String) alLevelOther.get(i) + "_LD"), ""), Element.ALIGN_CENTER,
								"NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
						reportListExport.add(alInnerExport);
						// System.out.println(uF.showData((String)hmLevel.get((String)alLevelOther.get(i)+"_LD"),
						// ""));
						// System.out.println("Question");
						alInnerExport = new ArrayList<DataStyle>();
						alInnerExport.add(new DataStyle("Question", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));

						for (int r = 0; alRoles != null && r < alRoles.size(); r++) {
							// System.out.println("Marks Role:"+hmorientationMembers.get((String)alRoles.get(r)));
							alInnerExport.add(new DataStyle("Marks Role:" + hmorientationMembers.get((String) alRoles.get(r)), Element.ALIGN_CENTER,
									"NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
						}
						alInnerExport.add(new DataStyle("Weightage %", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
						// System.out.println("Weightage %");
						reportListExport.add(alInnerExport);
						for (int q = 0; alQuestions != null && q < alQuestions.size(); q++) {
							// System.out.println(q+1
							// +" "+hmQuestions.get((String)alQuestions.get(q)));
							alInnerExport = new ArrayList<DataStyle>();
							alInnerExport.add(new DataStyle(q + 1 + ")" + hmQuestions.get((String) alQuestions.get(q)), Element.ALIGN_CENTER, "NEW_ROMAN", 6,
									"0", "0", BaseColor.WHITE));
							for (int ii = 0; alRoles != null && ii < alRoles.size(); ii++) {
								if (uF.parseToInt(alRoles.get(0).toString()) == 4 || uF.parseToInt(alRoles.get(0).toString()) == 10) {
									List<String> innList = hmOuterpeerAppraisalDetails.get(alQuestions.get(q) + "_" + alRoles.get(ii) + "_"
											+ rolesUserIds.get(m));
									if (innList == null)
										innList = new ArrayList<String>();
									if (innList != null && !innList.isEmpty()) {
										// System.out.println("Marks"+uF.showData((String)innList.get(0),
										// "Not Rated"));
										// System.out.println("Marks"+uF.showData((String)innList.get(1),
										// ""));
									} else {
										// alInnerExport = new
										// ArrayList<DataStyle>();
										alInnerExport.add(new DataStyle("Not Rated", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
										alInnerExport.add(new DataStyle(uF.showData(hmQuestionWeightage.get((String) alQuestions.get(q)).toString(), ""),
												Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
										// System.out.println("Not Rated");
										// System.out.println("marks"+hmQuestionWeightage.get((String)alQuestions.get(q)));
									}
								} else {
									// alInnerExport = new
									// ArrayList<DataStyle>();
									alInnerExport.add(new DataStyle(uF.showData(
											(String) hmQuestionMarks.get((String) alQuestions.get(q) + "_" + alRoles.get(ii) + "_" + rolesUserIds.get(m)),
											"Not Rated"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
									// System.out.println("marks"+uF.showData((String)hmQuestionMarks.get((String)alQuestions.get(q)+"_"+alRoles.get(ii)+"_"+rolesUserIds.get(m)),
									// "Not Rated"));
									// System.out.println("marks"+hmQuestionWeightage.get((String)alQuestions.get(q)));
									// alInnerExport = new
									// ArrayList<DataStyle>();
									alInnerExport.add(new DataStyle(hmQuestionWeightage.get((String) alQuestions.get(q)).toString(), Element.ALIGN_CENTER,
											"NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
								}
							}

							reportListExport.add(alInnerExport);
							String sb = "";
							for (int r = 0; alRoles != null && r < alRoles.size(); r++) {
								if (r == 0) {
									alInnerExport = new ArrayList<DataStyle>();
									// alInnerExport.add(new
									// DataStyle("Answer/Comments:"+hmQuestions.get((String)alQuestions.get(q)),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.WHITE));

									sb = "Answer/Comments:";
									// System.out.println("Answer/Comments:");
								}
								if (uF.parseToInt(alRoles.get(0).toString()) == 4 || uF.parseToInt(alRoles.get(0).toString()) == 10) {
									String queAns = hmOuterpeerAnsDetails.get(alQuestions.get(q) + "_" + memberId + "_" + rolesUserIds.get(m));
									// System.out.println("ans"+uF.showData(queAns,
									// ""));

									// alInnerExport = new
									// ArrayList<DataStyle>();
									alInnerExport.add(new DataStyle(uF.showData(queAns, ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
									// reportListExport.add(alInnerExport);
								} else {
									if (hmQuestionAnswer.containsKey((String) alQuestions.get(q) + "_" + memberId + "_" + rolesUserIds.get(m))) {
										String strAns = ((String) hmQuestionAnswer
												.get((String) alQuestions.get(q) + "_" + memberId + "_" + rolesUserIds.get(m)));
										if (strAns != null) {
											strAns = strAns.replace(":_:", "<br/>");
											// alInnerExport = new
											// ArrayList<DataStyle>();
											// alInnerExport.add(new
											// DataStyle(uF.showData(strAns,
											// ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.WHITE));
											// reportListExport.add(alInnerExport);
											// System.out.println("answer"+uF.showData(strAns,
											// ""));
											sb = sb + uF.showData(strAns, "");
											alInnerExport.add(new DataStyle(uF.showData(sb, ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",
													BaseColor.WHITE));
											reportListExport.add(alInnerExport);
										}
									}
								}
							}
						}
						// reportListExport.add(alInnerExport);
						
					// ===start parvez date: 16-04-2022===
						StringBuilder strCSb = new StringBuilder();
						strCSb.append(" Section Comment: ");
						strCSb.append(uF.showData((String) hmSectionComment.get(sectionIdsList.get(a) + "_" + memberId + "_" + rolesUserIds.get(m)), "") + "");
						alInnerExport = new ArrayList<DataStyle>();
						alInnerExport.add(new DataStyle(uF.showData(strCSb.toString(), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
						reportListExport.add(alInnerExport);
					// ===end parvez date: 16-04-2022===

					}

					Map<String, String> hmAppSystemType = (Map<String, String>) request.getAttribute("hmAppSystemType");
					List<String> alLevelGoalTargetKRA = hmSubsectionIds.get(sectionIdsList.get(a) + "GTK");
					// System.out.println("alLevelGoalTargetKRA ===> "+alLevelGoalTargetKRA);
					for (int i = 0; alLevelGoalTargetKRA != null && i < alLevelGoalTargetKRA.size(); i++) {
						cnt++;
						String appSystemType = hmAppSystemType.get((String) alLevelGoalTargetKRA.get(i));
						// System.out.println("hmGoalTargetKraQuestionsMap ===> "+hmGoalTargetKraQuestionsMap);
						List alQuestions = (List) hmGoalTargetKraQuestionsMap.get((String) alLevelGoalTargetKRA.get(i));
						String strSystemType = "";
						if (appSystemType != null && appSystemType.equals("3")) {
							strSystemType = "Goal";
						} else if (appSystemType != null && appSystemType.equals("4")) {
							strSystemType = "KRA";
						} else if (appSystemType != null && appSystemType.equals("5")) {
							strSystemType = "Target";
						}

						alInnerExport = new ArrayList<DataStyle>();
						alInnerExport.add(new DataStyle(a + 1 + "." + cnt + ") " + hmLevel.get((String) alLevelGoalTargetKRA.get(i)), Element.ALIGN_CENTER,
								"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						reportListExport.add(alInnerExport);
						// System.out.println(a+1+"."+cnt
						// +") "+hmLevel.get((String)alLevelGoalTargetKRA.get(i)));

						alInnerExport = new ArrayList<DataStyle>();
						alInnerExport.add(new DataStyle(uF.showData((String) hmLevel.get((String) alLevelGoalTargetKRA.get(i) + "_SD"), ""),
								Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						reportListExport.add(alInnerExport);

						alInnerExport = new ArrayList<DataStyle>();
						alInnerExport.add(new DataStyle(uF.showData((String) hmLevel.get((String) alLevelGoalTargetKRA.get(i) + "_LD"), ""),
								Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						reportListExport.add(alInnerExport);

						// System.out.println(uF.showData((String)hmLevel.get((String)alLevelGoalTargetKRA.get(i)+"_SD"),
						// ""));
						// System.out.println(uF.showData((String)hmLevel.get((String)alLevelGoalTargetKRA.get(i)+"_LD"),
						// ""));
						// System.out.println(strSystemType);
						alInnerExport = new ArrayList<DataStyle>();
						alInnerExport.add(new DataStyle(strSystemType, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));

						for (int r = 0; alRoles != null && r < alRoles.size(); r++) {
							alInnerExport.add(new DataStyle("Marks Role: " + hmorientationMembers.get((String) alRoles.get(r)), Element.ALIGN_CENTER,
									"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							// System.out.println("Marks Role: "+hmorientationMembers.get((String)alRoles.get(r)));
						}
						// System.out.println("Weightage %");
						alInnerExport.add(new DataStyle("Weightage %", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						reportListExport.add(alInnerExport);
						
			// ===start parvez date: 16-04-2022===
						
						for (int q = 0; alQuestions != null && q < alQuestions.size(); q++) {
							alInnerExport = new ArrayList<DataStyle>();
							alInnerExport.add(new DataStyle(q + 1 + ")" + hmQuestions.get((String) alQuestions.get(q)), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
							for (int ii = 0; alRoles != null && ii < alRoles.size(); ii++) {

								if (uF.parseToInt(alRoles.get(0).toString()) == 4 || uF.parseToInt(alRoles.get(0).toString()) == 10) {
									List<String> innList = hmOuterpeerAppraisalDetails.get(alQuestions.get(q) + "_" + alRoles.get(ii) + "_"
											+ rolesUserIds.get(m));
									if (innList == null)
										innList = new ArrayList<String>();
									if (innList != null && !innList.isEmpty()) {
										alInnerExport = new ArrayList<DataStyle>();
										// alInnerExport.add(new
										// DataStyle("Answer/Comments:"+hmQuestions.get((String)alQuestions.get(q)),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.WHITE));
										alInnerExport.add(new DataStyle(uF.showData((String) innList.get(0), "Not Rated"), Element.ALIGN_CENTER, "NEW_ROMAN",
												6, "0", "0", BaseColor.WHITE));
										alInnerExport.add(new DataStyle(uF.showData((String) innList.get(1), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0",
												"0", BaseColor.WHITE));
										
									} else {
//										alInnerExport = new ArrayList<DataStyle>();
										
										alInnerExport.add(new DataStyle("" + hmQuestionWeightage.get((String) alQuestions.get(q)), Element.ALIGN_CENTER,
												"NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
									}
								} else {
//									alInnerExport = new ArrayList<DataStyle>();
									alInnerExport.add(new DataStyle(uF.showData(
											(String) hmQuestionMarks.get((String) alQuestions.get(q) + "_" + alRoles.get(ii) + "_" + rolesUserIds.get(m)),
											"Not Rated"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
									
									alInnerExport.add(new DataStyle("" + hmQuestionWeightage.get((String) alQuestions.get(q)), Element.ALIGN_CENTER,
											"NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
								}
//								reportListExport.add(alInnerExport);
								//need changes
							}
							
							reportListExport.add(alInnerExport);
							
							String sb = "";
							for (int r = 0; alRoles != null && r < alRoles.size(); r++) {
								if (r == 0) {
									// alInnerExport.add(new
									// DataStyle(uF.showData(hmQuestionWeightage.get((String)alQuestions.get(q)).toString(),
									// ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.WHITE));
									// System.out.println("Answer/Comments:");
									alInnerExport = new ArrayList<DataStyle>();
									sb = "Answer/Comments:";

								}

								if (uF.parseToInt(alRoles.get(0).toString()) == 4 || uF.parseToInt(alRoles.get(0).toString()) == 10) {
									String queAns = hmOuterpeerAnsDetails.get(alQuestions.get(q) + "_" + memberId + "_" + rolesUserIds.get(m));
									// System.out.println(uF.showData(queAns,
									// ""));
									alInnerExport.add(new DataStyle(uF.showData(queAns, ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
								} else {

									if (hmQuestionAnswer.containsKey((String) alQuestions.get(q) + "_" + memberId + "_" + rolesUserIds.get(m))) {
										String strAns = ((String) hmQuestionAnswer
												.get((String) alQuestions.get(q) + "_" + memberId + "_" + rolesUserIds.get(m)));
										if (strAns != null) {
											strAns = strAns.replace(":_:", "<br/>");
											// System.out.println(uF.showData(strAns,
											// ""));
											sb = sb + uF.showData(strAns, "");
//											alInnerExport.add(new DataStyle(uF.showData(strAns, ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
											alInnerExport.add(new DataStyle(uF.showData(sb, ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
											reportListExport.add(alInnerExport);
										}
									}
								}
							}
//							reportListExport.add(alInnerExport);

							/*for (int ii = 0; alRoles != null && ii < alRoles.size(); ii++) {

								if (uF.parseToInt(alRoles.get(0).toString()) == 4 || uF.parseToInt(alRoles.get(0).toString()) == 10) {
									List<String> innList = hmOuterpeerAppraisalDetails.get(alQuestions.get(q) + "_" + alRoles.get(ii) + "_"
											+ rolesUserIds.get(m));
									if (innList == null)
										innList = new ArrayList<String>();
									if (innList != null && !innList.isEmpty()) {
										alInnerExport = new ArrayList<DataStyle>();
										// alInnerExport.add(new
										// DataStyle("Answer/Comments:"+hmQuestions.get((String)alQuestions.get(q)),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.WHITE));
										alInnerExport.add(new DataStyle(uF.showData((String) innList.get(0), "Not Rated"), Element.ALIGN_CENTER, "NEW_ROMAN",
												6, "0", "0", BaseColor.WHITE));
										alInnerExport.add(new DataStyle(uF.showData((String) innList.get(1), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0",
												"0", BaseColor.WHITE));
										// System.out.println(uF.showData((String)innList.get(0),
										// "Not Rated"));
										// System.out.println(uF.showData((String)innList.get(1),
										// ""));
									} else {
										alInnerExport = new ArrayList<DataStyle>();
										// System.out.println("Not Rated");
										// System.out.println(hmQuestionWeightage.get((String)alQuestions.get(q)));
										alInnerExport.add(new DataStyle("" + hmQuestionWeightage.get((String) alQuestions.get(q)), Element.ALIGN_CENTER,
												"NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
									}
								} else {
									alInnerExport = new ArrayList<DataStyle>();
									alInnerExport.add(new DataStyle(uF.showData(
											(String) hmQuestionMarks.get((String) alQuestions.get(q) + "_" + alRoles.get(ii) + "_" + rolesUserIds.get(m)),
											"Not Rated"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
									// System.out.println(uF.showData((String)hmQuestionMarks.get((String)alQuestions.get(q)+"_"+alRoles.get(ii)+"_"+rolesUserIds.get(m)),
									// "Not Rated"));
									// System.out.println(hmQuestionWeightage.get((String)alQuestions.get(q)));
									alInnerExport.add(new DataStyle("" + hmQuestionWeightage.get((String) alQuestions.get(q)), Element.ALIGN_CENTER,
											"NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
								}
								reportListExport.add(alInnerExport);
								//need changes
							}*/
						}

					
						StringBuilder strCSb = new StringBuilder();
						strCSb.append(" Section Comment: ");
						strCSb.append(uF.showData((String) hmSectionComment.get(sectionIdsList.get(a) + "_" + memberId + "_" + rolesUserIds.get(m)), "") + "");
						alInnerExport = new ArrayList<DataStyle>();
						alInnerExport.add(new DataStyle(uF.showData(strCSb.toString(), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
						reportListExport.add(alInnerExport);
		// ===end parvez date: 16-04-2022===
					}
				}
				
		//===start parvez date: 16-04-2022===
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle("Areas of Strength:", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((String)hmStrengthImprovements.get(rolesUserIds.get(m)+"_STRENGTH"), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
				alInnerExport.add(new DataStyle("Areas of Improvement:", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
				alInnerExport.add(new DataStyle(uF.showData((String)hmStrengthImprovements.get(rolesUserIds.get(m)+"_IMPROVEMENT"), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.WHITE));
				reportListExport.add(alInnerExport);
		//===end parvez date: 16-04-2022===		
				
			}
		}

		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Appraiser Comments", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		reportListExport.add(alInnerExport);
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle(uF.showData((String) request.getAttribute("strFinalComments"), "Not Commented yet"), Element.ALIGN_CENTER, "NEW_ROMAN",
				6, "0", "0", BaseColor.LIGHT_GRAY));
		reportListExport.add(alInnerExport);

		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Appraised by -" + uF.showData((String) request.getAttribute("strAppraisedBy"), ""), Element.ALIGN_CENTER, "NEW_ROMAN",
				6, "0", "0", BaseColor.LIGHT_GRAY));
		reportListExport.add(alInnerExport);

		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("on -" + uF.showData((String) request.getAttribute("strAppraisedOn"), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0",
				"0", BaseColor.LIGHT_GRAY));
		reportListExport.add(alInnerExport);
		session.setAttribute("reportListExport", reportListExport);
	}

	private void getAppraisalQuestionsAnswers() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			
			Map<String, String> useNameMP = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmAnswerType = CF.getAnswerTypeMap(con);
			request.setAttribute("hmAnswerType", hmAnswerType);
			
			Map<String, String> hmorientationMembers = new HashMap<String, String>();
			pst = con.prepareStatement("select * from orientation_member");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmorientationMembers.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();

			request.setAttribute("hmorientationMembers", hmorientationMembers);
			request.setAttribute("useNameMP", useNameMP);
			List<String> sectionIdsList = new ArrayList<String>();
//			pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id = ? ");
			if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) && (getRole()!=null 
					&& (getRole().equalsIgnoreCase("Peer") || getRole().equalsIgnoreCase("Other Peer") || getRole().equalsIgnoreCase("HOD")))){
				pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id = ? " +
						" and main_level_id not in (select main_level_id from appraisal_level_details where appraisal_id=? and appraisal_system!=2)");
				pst.setInt(2, uF.parseToInt(getId()));
			} else{
				pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id = ? ");
			}
			pst.setInt(1, uF.parseToInt(getId()));
//			System.out.println("AScS/1688--pst ===> "+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				sectionIdsList.add(rs.getString("main_level_id"));
			}
			rs.close();
			pst.close();

			// System.out.println("sectionIdsList ===> "+sectionIdsList);
			request.setAttribute("sectionIdsList", sectionIdsList);

			pst = con.prepareStatement("select distinct aqd.appraisal_level_id,main_level_id,other_id,scorecard_id,app_system_type from "
					+ "appraisal_question_details aqd, appraisal_level_details ald where aqd.appraisal_level_id = ald.appraisal_level_id "
					+ "and aqd.appraisal_id = ? order by aqd.appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, String> hmAppSystemType = new HashMap<String, String>();
			Map<String, List<String>> hmSubsectionIds = new LinkedHashMap<String, List<String>>();
			while (rs.next()) {
				hmAppSystemType.put(rs.getString("appraisal_level_id"), rs.getString("app_system_type"));

				List<String> alLevelScore = hmSubsectionIds.get(rs.getString("main_level_id") + "SCR");
				if (alLevelScore == null)
					alLevelScore = new ArrayList<String>();

				List<String> alLevelOther = hmSubsectionIds.get(rs.getString("main_level_id") + "OTHR");
				if (alLevelOther == null)
					alLevelOther = new ArrayList<String>();

				List<String> alLevelGoalTargetKRA = hmSubsectionIds.get(rs.getString("main_level_id") + "GTK");
				if (alLevelGoalTargetKRA == null)
					alLevelGoalTargetKRA = new ArrayList<String>();

				if (uF.parseToInt(rs.getString("other_id")) > 0 && !alLevelOther.contains(rs.getString("appraisal_level_id"))) {
					alLevelOther.add(rs.getString("appraisal_level_id"));
				} else if (uF.parseToInt(rs.getString("scorecard_id")) > 0 && !alLevelScore.contains(rs.getString("appraisal_level_id"))) {
					alLevelScore.add(rs.getString("appraisal_level_id"));
				} else if (!alLevelGoalTargetKRA.contains(rs.getString("appraisal_level_id"))) {
					alLevelGoalTargetKRA.add(rs.getString("appraisal_level_id"));
				} else {
				}
				hmSubsectionIds.put(rs.getString("main_level_id") + "SCR", alLevelScore);
				hmSubsectionIds.put(rs.getString("main_level_id") + "OTHR", alLevelOther);
				hmSubsectionIds.put(rs.getString("main_level_id") + "GTK", alLevelGoalTargetKRA);
			}
			rs.close();
			pst.close();

			// System.out.println("hmSubsectionIds ===> "+hmSubsectionIds);
			request.setAttribute("hmSubsectionIds", hmSubsectionIds);
			request.setAttribute("hmAppSystemType", hmAppSystemType);

			double appWeightage =0;
			pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id = ?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, String> hmSectionDetails = new HashMap<String, String>();
			while (rs.next()) {
				hmSectionDetails.put(rs.getString("main_level_id"), rs.getString("level_title"));
				hmSectionDetails.put(rs.getString("main_level_id") + "_SD", rs.getString("short_description"));
				hmSectionDetails.put(rs.getString("main_level_id") + "_LD", rs.getString("long_description"));
				
				appWeightage += uF.parseToDouble(rs.getString("section_weightage"));
			}
			rs.close();
			pst.close();

			// System.out.println("hmSectionDetails ===> "+hmSectionDetails);
			request.setAttribute("hmSectionDetails", hmSectionDetails);

			Map hmScoreQuestionsMap = new HashMap();
			Map hmOtherQuestionsMap = new HashMap();
			Map hmGoalTargetKraQuestionsMap = new HashMap();
			Map hmLevelScoreMap = new HashMap();

			Map hmQuestionMarks = new HashMap();
			Map hmQuestionWeightage = new HashMap();

			List alRoles = new ArrayList();
			List rolesUserIds = new ArrayList();
			Map<String, List<String>> hmOuterpeerAppraisalDetails = new HashMap<String, List<String>>();

			List<String> alKRAIds = (List<String>) request.getAttribute("alKRAIds");
			List<String> alGoarTargetIds = (List<String>) request.getAttribute("alGoarTargetIds");

			Map hmQuestionAnswer = new HashMap();
			Map<String, String> hmSectionComment = new HashMap<String, String>();
		
		// ===start parvez date: 15-07-2022===
			Map<String, String> hmSubmitApprovedStatus = new HashMap<String, String>();
			Map hmReviewerMarksComment = new HashMap();
		// ===end parvez date: 15-07-2022===
			List alQuestion = new ArrayList();
			pst = con.prepareStatement("select * from appraisal_question_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getInt("app_system_type") == 4 && alKRAIds != null && rs.getString("kra_id") != null && !alKRAIds.contains(rs.getString("kra_id"))) {
					continue;
				}
				if ((rs.getInt("app_system_type") == 3 || rs.getInt("app_system_type") == 5) && alGoarTargetIds != null
						&& rs.getString("goal_kra_target_id") != null && !alGoarTargetIds.contains(rs.getString("goal_kra_target_id"))) {
					continue;
				}
				if (uF.parseToInt(rs.getString("other_id")) > 0) {
					alQuestion = (List) hmOtherQuestionsMap.get(rs.getString("appraisal_level_id"));
					if (alQuestion == null)
						alQuestion = new ArrayList();
					if (!alQuestion.contains(rs.getString("question_id"))) {
						alQuestion.add(rs.getString("question_id"));
					}
					hmOtherQuestionsMap.put(rs.getString("appraisal_level_id"), alQuestion);
				} else if (uF.parseToInt(rs.getString("scorecard_id")) > 0) {
					alQuestion = (List) hmScoreQuestionsMap.get(rs.getString("scorecard_id"));
					if (alQuestion == null)
						alQuestion = new ArrayList();
					if (!alQuestion.contains(rs.getString("question_id"))) {
						alQuestion.add(rs.getString("question_id"));
					}
					hmScoreQuestionsMap.put(rs.getString("scorecard_id"), alQuestion);

					List alScore = (List) hmLevelScoreMap.get(rs.getString("appraisal_level_id"));
					if (alScore == null)
						alScore = new ArrayList();
					if (!alScore.contains(rs.getString("scorecard_id"))) {
						alScore.add(rs.getString("scorecard_id"));
					}
					hmLevelScoreMap.put(rs.getString("appraisal_level_id"), alScore);
				} else {
					alQuestion = (List) hmGoalTargetKraQuestionsMap.get(rs.getString("appraisal_level_id"));
					if (alQuestion == null)
						alQuestion = new ArrayList();
					if (!alQuestion.contains(rs.getString("question_id"))) {
						alQuestion.add(rs.getString("question_id"));
					}
					hmGoalTargetKraQuestionsMap.put(rs.getString("appraisal_level_id"), alQuestion);
				}
				hmQuestionWeightage.put(rs.getString("question_id"), rs.getString("weightage"));
			}
			rs.close();
			pst.close();
			// System.out.println("hmQuestionWeightage 111 ===> "+hmQuestionWeightage);

			pst = con.prepareStatement("select * from question_bank");
			rs = pst.executeQuery();
			Map<String, String> hmQueAnsType = new HashMap<String, String>();
			while (rs.next()) {
				hmQueAnsType.put(rs.getString("question_bank_id"), rs.getString("question_type"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from appraisal_answer_type_sub");
			rs = pst.executeQuery();
			Map<String, String> hmAnsTypeAnswer = new HashMap<String, String>();
			while (rs.next()) {
				hmAnsTypeAnswer.put(rs.getString("answer_type_id") + "_" + rs.getString("score"), rs.getString("score_label"));
			}
			rs.close();
			pst.close();

			// System.out.println("Member ID == "+
			// uF.parseToInt(getMemberId()));

			if (uF.parseToInt(getMemberId()) > 0) {
				// System.out.println("Member ID in if ==> "+
				// uF.parseToInt(getMemberId()));
				if (uF.parseToInt(getMemberId()) == 4 || uF.parseToInt(getMemberId()) == 10) {
					String avgMarks = "", avgWeightage = "";
					String allAns = "", allRemark = "";
					int cnt = 0;
					String remark = "", ans = "";
					pst = con.prepareStatement("select *,(marks/weightcnt) as avgmarks FROM (select user_type_id,user_id,question_id,sum(marks) "
							+ "as marks,sum(weightage) as weightage,(sum(weightage)/COUNT(weightage)) as avgweightage,COUNT(weightage) as weightcnt from "
							+ "appraisal_question_answer where appraisal_id = ? and user_type_id=? and emp_id=? and appraisal_freq_id = ? and "
							+ "reviewer_or_appraiser=? group by user_type_id,user_id,question_id order by question_id)  as a");
					pst.setInt(1, uF.parseToInt(getId()));
					pst.setInt(2, uF.parseToInt(getMemberId()));
					pst.setInt(3, uF.parseToInt(getEmpid()));
					pst.setInt(4, uF.parseToInt(getAppFreqId()));
					if (getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
						pst.setInt(5, 1);
					} else {
						pst.setInt(5, 0);
					}
					rs = pst.executeQuery();
					// System.out.println("pst marks == " + pst);
					while (rs.next()) {
						List<String> peerAppraisalDetails = new ArrayList<String>();
						avgMarks = uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("avgmarks"));
						avgWeightage = uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("avgweightage"));

						if (!alRoles.contains(rs.getString("user_type_id"))) {
							alRoles.add(rs.getString("user_type_id"));
						}

						if (!rolesUserIds.contains(rs.getString("user_id"))) {
							rolesUserIds.add(rs.getString("user_id"));
						}
						peerAppraisalDetails.add(avgMarks);
						peerAppraisalDetails.add(avgWeightage);
						hmOuterpeerAppraisalDetails.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
								peerAppraisalDetails);
						// System.out.println("peerAppraisalDetails == "+peerAppraisalDetails
						// + " remark == "+remark);
					}
					rs.close();
					pst.close();

					// System.out.println("hmOuterpeerAppraisalDetails ===> " +
					// hmOuterpeerAppraisalDetails);

					String que_id = "";
					pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id=? and user_type_id=? and emp_id=? and "
							+ "appraisal_freq_id=? and reviewer_or_appraiser=? order by question_id");
					pst.setInt(1, uF.parseToInt(getId()));
					pst.setInt(2, uF.parseToInt(getMemberId()));
					pst.setInt(3, uF.parseToInt(getEmpid()));
					pst.setInt(4, uF.parseToInt(getAppFreqId()));
					if (getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
						pst.setInt(5, 1);
					} else {
						pst.setInt(5, 0);
					}
					rs = pst.executeQuery();
					Map<String, String> hmOuterpeerAnsDetails = new HashMap<String, String>();
					while (rs.next()) {
						// List<String> peerAnsDetails = new
						// ArrayList<String>();
						String ansType = hmQueAnsType.get(rs.getString("question_id"));
						String answer = null;
						if (uF.parseToInt(ansType) == 4) {
							answer = hmAnsTypeAnswer.get(ansType + "_" + rs.getString("answer")) + " (" + rs.getString("answer") + ")";
						} else if (uF.parseToInt(ansType) == 5 || uF.parseToInt(ansType) == 6) {
							String getans = rs.getString("answer").length() > 0 ? rs.getString("answer").substring(0, rs.getString("answer").length() - 1) : "";
							answer = hmAnsTypeAnswer.get(ansType + "_" + getans);
						} else if (uF.parseToInt(ansType) == 3) {
							String getans = rs.getString("marks");
							answer = getans;
						} else if (uF.parseToInt(ansType) == 11) {
						//===start parvez date: 10-03-2023===	
//							double getans = (rs.getDouble("marks") * 5) / rs.getDouble("weightage");
//							answer = uF.formatIntoOneDecimal(getans) + " / 5";
							double getans = 0;
							if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
								getans = (rs.getDouble("marks") * 10) / rs.getDouble("weightage");
								answer = uF.formatIntoOneDecimal(getans) + " / 10";
							} else{
								getans = (rs.getDouble("marks") * 5) / rs.getDouble("weightage");
								answer = uF.formatIntoOneDecimal(getans) + " / 5";
							}
						//===end parvez date: 10-03-2023===	
						} else {
							answer = rs.getString("answer");
						}
						String QuestionAns = hmOuterpeerAnsDetails.get(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_"
								+ rs.getString("user_id"));
						if (QuestionAns == null) {
							String strRemark = "";
							if (rs.getString("remark") != null) {
								strRemark = "<div style=\"line-height: 13px; width: 100%; font-size: 11px;\">Remark: "
										+ uF.showData(rs.getString("remark"), "") + "</div>";
							}
							QuestionAns = "<div style=\"line-height: 13px;\">" + uF.showData(answer, "No answer")
									+ "<span style=\"margin-right: 10px;font-size: 10px; float: right; font-style: italic;\">- by "
									+ useNameMP.get(rs.getString("user_id")) + "</span></div>" + strRemark
									+ "<div style=\"line-height: 13px; width: 100%; font-style: italic; font-size: 11px;\">"
									+ uF.showData(rs.getString("answers_comment"), "No comment") + "</div>";
						} else {
							String strRemark = "";
							if (rs.getString("remark") != null) {
								strRemark = "<div style=\"line-height: 13px; width: 100%; font-size: 11px;\">Remark: "
										+ uF.showData(rs.getString("remark"), "") + "</div>";
							}
							QuestionAns += "<br/> <div style=\"line-height: 13px;\">" + uF.showData(answer, "No answer")
									+ "<span style=\"margin-right: 10px;font-size: 10px; float: right; font-style: italic;\">- by "
									+ useNameMP.get(rs.getString("user_id")) + "</span></div>" + strRemark
									+ "<div style=\"line-height: 13px; width: 100%; font-style: italic; font-size: 11px;\">"
									+ uF.showData(rs.getString("answers_comment"), "No comment") + "</div>";
						}
						hmOuterpeerAnsDetails
								.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"), QuestionAns);

						// ===start parvez date: 13-04-2022===
						hmSectionComment.put(rs.getString("section_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
								rs.getString("section_comment"));
						// ===end parvez date: 13-04-2022===
						
				//===start parvez date: 15-07-2022===		
						hmSubmitApprovedStatus.put(rs.getString("user_type_id")+ "_" + rs.getString("user_id")+"_"+rs.getString("emp_id")+"_Submit", rs.getString("is_submit"));
						hmSubmitApprovedStatus.put(rs.getString("user_type_id")+ "_" + rs.getString("user_id")+"_"+rs.getString("emp_id")+"_Approved", rs.getString("hr_approval"));
				//===end parvez date: 15-07-2022===		
						
					}
					rs.close();
					pst.close();

					// System.out.println("hmOuterpeerAnsDetails == "+hmOuterpeerAnsDetails
					// );
					request.setAttribute("hmOuterpeerAnsDetails", hmOuterpeerAnsDetails);

				} else {
				////===start parvez date: 02-03-2023===	
					/*pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id=? and user_type_id=? and emp_id=? and "
							+ "appraisal_freq_id=? and reviewer_or_appraiser=?");*/
					pst = con.prepareStatement("select aqa.*,score_calculation_basis from appraisal_question_answer aqa,appraisal_question_details aqd " +
							"where aqa.appraisal_id=? and aqa.user_type_id=? and aqa.emp_id=? and aqa.appraisal_freq_id=? and aqa.reviewer_or_appraiser=? " +
							" and aqa.appraisal_question_details_id=aqd.appraisal_question_details_id");
			////===end parvez date: 02-03-2023===		
					pst.setInt(1, uF.parseToInt(getId()));
					pst.setInt(2, uF.parseToInt(getMemberId()));
					pst.setInt(3, uF.parseToInt(getEmpid()));
					pst.setInt(4, uF.parseToInt(getAppFreqId()));
					if (getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
						pst.setInt(5, 1);
					} else {
						pst.setInt(5, 0);
					}
					rs = pst.executeQuery();
//					System.out.println("ASSt/1944---pst=="+pst);
					while (rs.next()) {
						String ansType = hmQueAnsType.get(rs.getString("question_id"));
						String answer = null, answerWithUser = null;
						if (uF.parseToInt(ansType) == 4) {
							answer = hmAnsTypeAnswer.get(ansType + "_" + rs.getString("answer")) + " (" + rs.getString("answer") + ")";
						} else if (uF.parseToInt(ansType) == 5 || uF.parseToInt(ansType) == 6) {
							String getans = rs.getString("answer").length() > 0 ? rs.getString("answer").substring(0, rs.getString("answer").length() - 1) : "";
							answer = hmAnsTypeAnswer.get(ansType + "_" + getans);
						} else if (uF.parseToInt(ansType) == 3) {
							String getans = rs.getString("marks");
							answer = getans;
						} else if (uF.parseToInt(ansType) == 11) {
						//===start parvez date: 10-03-2023===	
//							double getans = (rs.getDouble("marks") * 5) / rs.getDouble("weightage");
//							answer = uF.formatIntoOneDecimal(getans) + " / 5";
							double getans = 0;
							if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
								getans = (rs.getDouble("marks") * 10) / rs.getDouble("weightage");
								answer = uF.formatIntoOneDecimal(getans) + " / 10";
							} else{
								getans = (rs.getDouble("marks") * 5) / rs.getDouble("weightage");
								answer = uF.formatIntoOneDecimal(getans) + " / 5";
							}
						//===end parvez date: 10-03-2023===	
						} else {
							answer = rs.getString("answer");
						}

					//===start parvez date: 01-03-2023===
//						hmQuestionMarks.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
//								uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("marks")));
					
						if(uF.parseToBoolean(rs.getString("score_calculation_basis"))){
							hmQuestionMarks.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
									uF.formatIntoOneDecimal((rs.getDouble("marks") * 5) / rs.getDouble("weightage")));
//							System.out.println("marks---"+uF.formatIntoTwoDecimalWithOutComma((rs.getDouble("marks") * 5) / rs.getDouble("weightage")));
						}else{
							hmQuestionMarks.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
									uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("marks")));
						}
					//===start parvez date: 01-03-2023===	
						
						hmQuestionWeightage.put(rs.getString("question_id"), rs.getString("weightage"));

						if (rs.getString("answer") != null || rs.getString("marks") != null) {
							// System.out.println("useNameMP ===> " +
							// useNameMP+" User ID ===> "+rs.getString("user_id"));
							String strRemark = "";
							if (rs.getString("remark") != null) {
								strRemark = "<div style=\"line-height: 13px; width: 100%; font-size: 11px;\">Remark: "
										+ uF.showData(rs.getString("remark"), "") + "</div>";
							}
							answerWithUser = "<div style=\"line-height: 13px;\">" + uF.showData(answer, "No answer")
									+ "<span style=\"margin-right: 10px;font-size: 10px; float: right; font-style: italic;\">- by "
									+ useNameMP.get(rs.getString("user_id")) + "</span></div>" + strRemark
									+ "<div style=\"line-height: 13px; width: 100%; font-style: italic; font-size: 11px;\">"
									+ uF.showData(rs.getString("answers_comment"), "No comment") + "</div>";
							hmQuestionAnswer.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
									answerWithUser);
						}
						if (!alRoles.contains(rs.getString("user_type_id"))) {
							alRoles.add(rs.getString("user_type_id"));
						}

						if (!rolesUserIds.contains(rs.getString("user_id"))) {
							rolesUserIds.add(rs.getString("user_id"));
						}
						// ===start parvez date: 13-04-2022===
						hmSectionComment.put(rs.getString("section_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
								rs.getString("section_comment"));
						// ===end parvez date: 13-04-2022===
				
				//===start parvez date: 15-07-2022===		
						hmSubmitApprovedStatus.put(rs.getString("user_type_id")+ "_" + rs.getString("user_id")+"_"+rs.getString("emp_id")+"_Submit", rs.getString("is_submit"));
						hmSubmitApprovedStatus.put(rs.getString("user_type_id")+ "_" + rs.getString("user_id")+"_"+rs.getString("emp_id")+"_Approved", rs.getString("hr_approval"));
				//===end parvez date: 15-07-2022===	

					}
					rs.close();
					pst.close();
					
				//===start parvez date: 18-07-2022===
					if(uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) && uF.parseToInt(getMemberId()) == 13){
						pst = con.prepareStatement("select * from reviewer_feedback_details where appraisal_id=? and user_type_id=? and emp_id=? and "
								+ "appraisal_freq_id=? ");
						pst.setInt(1, uF.parseToInt(getId()));
						pst.setInt(2, uF.parseToInt(getMemberId()));
						pst.setInt(3, uF.parseToInt(getEmpid()));
						pst.setInt(4, uF.parseToInt(getAppFreqId()));
						rs = pst.executeQuery();
//						System.out.println("pst="+pst);
						while (rs.next()) {
							
							double getans = (rs.getDouble("reviewer_marks") * 5) / appWeightage;
							String answer = uF.formatIntoOneDecimal(getans) + " / 5";
							
							String answerWithUser = "<div style=\"line-height: 13px;\">" + uF.showData(answer, "No answer")
							+ "<span style=\"margin-right: 10px;font-size: 10px; float: right; font-style: italic;\">- by "
							+ useNameMP.get(rs.getString("user_id")) + "</span></div>"
							+ "<div style=\"line-height: 13px; width: 100%; font-style: italic; font-size: 11px;\">"
							+ uF.showData(rs.getString("reviewer_comment"), "No comment") + "</div>";
							
							hmReviewerMarksComment.put(rs.getString("user_type_id") + "_" + rs.getString("user_id"), answerWithUser);
							hmReviewerMarksComment.put(rs.getString("user_type_id") + "_" + rs.getString("user_id")+"_MARKS", rs.getString("reviewer_marks"));
							hmReviewerMarksComment.put(rs.getString("user_type_id") + "_" + rs.getString("user_id")+"_WEIGHTAGE", appWeightage+"");
							
							if (!alRoles.contains(rs.getString("user_type_id"))) {
								alRoles.add(rs.getString("user_type_id"));
							}
							
							if (!rolesUserIds.contains(rs.getString("user_id"))) {
								rolesUserIds.add(rs.getString("user_id"));
							}
						}
						rs.close();
						pst.close();
					}
				//===end parvez date: 18-07-2022===	
					
				}
				// System.out.println("alRoles ========> "+alRoles);
			} else {
			//===start parvez date: 02-03-2023===	
				/*pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id=? and emp_id=? and appraisal_freq_id=? "
						+ " and reviewer_or_appraiser=? order by user_type_id");*/
				pst = con.prepareStatement("select aqa.*,score_calculation_basis from appraisal_question_answer aqa,appraisal_question_details aqd " +
						" where aqa.appraisal_id=? and aqa.emp_id=? and aqa.appraisal_freq_id=? and aqa.reviewer_or_appraiser=? " +
						" and aqa.appraisal_question_details_id=aqd.appraisal_question_details_id order by user_type_id");
				
			//===end parvez date: 02-03-2023===	
				pst.setInt(1, uF.parseToInt(getId()));
				pst.setInt(2, uF.parseToInt(getEmpid()));
				pst.setInt(3, uF.parseToInt(getAppFreqId()));
				if (getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
					pst.setInt(4, 1);
				} else {
					pst.setInt(4, 0);
				}
				rs = pst.executeQuery();
				while (rs.next()) {
					String ansType = hmQueAnsType.get(rs.getString("question_id"));
					String answer = null, answerWithUser = null;
					if (uF.parseToInt(ansType) == 4) {
						answer = hmAnsTypeAnswer.get(ansType + "_" + rs.getString("answer")) + " (" + rs.getString("answer") + ")";
					} else if (uF.parseToInt(ansType) == 5 || uF.parseToInt(ansType) == 6) {
						String getans = rs.getString("answer").length() > 0 ? rs.getString("answer").substring(0, rs.getString("answer").length() - 1) : "";
						answer = hmAnsTypeAnswer.get(ansType + "_" + getans);
					} else if (uF.parseToInt(ansType) == 3) {
						String getans = rs.getString("marks");
						answer = getans;
					} else if (uF.parseToInt(ansType) == 11) {
					//===start parvez date: 10-03-2023===	
//						double getans = (rs.getDouble("marks") * 5) / rs.getDouble("weightage");
//						answer = uF.formatIntoOneDecimal(getans) + " / 5";
						double getans = 0;
						if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
							getans = (rs.getDouble("marks") * 10) / rs.getDouble("weightage");
							answer = uF.formatIntoOneDecimal(getans) + " / 10";
						} else{
							getans = (rs.getDouble("marks") * 5) / rs.getDouble("weightage");
							answer = uF.formatIntoOneDecimal(getans) + " / 5";
						}
					//===end parvez date: 10-03-2023===	
					} else {
						answer = rs.getString("answer");
					}

				//===start parvez date: 02-03-2023===	
//					hmQuestionMarks.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
//							uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("marks")));
					if(uF.parseToBoolean(rs.getString("score_calculation_basis"))){
						hmQuestionMarks.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
								uF.formatIntoOneDecimal((rs.getDouble("marks") * 5) / rs.getDouble("weightage")));
						
					} else{
						hmQuestionMarks.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
								uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("marks")));
					}
				//===end parvez date: 02-03-2023===	
					hmQuestionWeightage.put(rs.getString("question_id"), rs.getString("weightage"));

					if (rs.getString("answer") != null || rs.getString("marks") != null) {
						// System.out.println("questionId==>"+rs.getString("question_id")+"==>answer==>"+answer+"==>Marks==>"+rs.getString("marks")+"==>comment==> "+rs.getString("answers_comment"));
						String strRemark = "";
						if (rs.getString("remark") != null) {
							strRemark = "<div style=\"line-height: 13px; width: 100%; font-size: 11px;\">Remark: " + uF.showData(rs.getString("remark"), "")
									+ "</div>";
						}
						answerWithUser = "<div style=\"line-height: 13px;\">" + uF.showData(answer, "No answer")
								+ "<span style=\"margin-right: 10px;font-size: 10px; float: right; font-style: italic;\">- by "
								+ useNameMP.get(rs.getString("user_id")) + "</span></div>" + strRemark
								+ "<div style=\"line-height: 13px; width: 100%; font-style: italic; font-size: 11px;\">"
								+ uF.showData(rs.getString("answers_comment"), "No comment") + "</div>";
						hmQuestionAnswer.put(rs.getString("question_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"), answerWithUser);
					}
					/*
					 * if(rs.getString("remark")!=null) {
					 * hmQuestionRemak.put(rs.
					 * getString("question_id")+"_"+rs.getString
					 * ("user_type_id")+"_"+rs.getString("user_id"),
					 * rs.getString("remark")); }
					 */

					if (!alRoles.contains(rs.getString("user_type_id"))) {
						alRoles.add(rs.getString("user_type_id"));
					}

					if (!rolesUserIds.contains(rs.getString("user_id"))) {
						rolesUserIds.add(rs.getString("user_id"));
					}

					// ===start parvez date: 13-04-2022===
					hmSectionComment.put(rs.getString("section_id") + "_" + rs.getString("user_type_id") + "_" + rs.getString("user_id"),
							rs.getString("section_comment"));
					// ===end parvez date: 13-04-2022===
					
			//===start parvez date: 15-07-2022===		
					hmSubmitApprovedStatus.put(rs.getString("user_type_id")+ "_" + rs.getString("user_id")+"_"+rs.getString("emp_id")+"_Submit", rs.getString("is_submit"));
					hmSubmitApprovedStatus.put(rs.getString("user_type_id")+ "_" + rs.getString("user_id")+"_"+rs.getString("emp_id")+"_Approved", rs.getString("hr_approval"));
			//===end parvez date: 15-07-2022===	
					
				}
				rs.close();
				pst.close();
				// System.out.println("alRoles else ========> "+alRoles);
				
			//===start parvez date: 18-07-2022===
				if(uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) && uF.parseToInt(getMemberId()) == 13){
					pst = con.prepareStatement("select * from reviewer_feedback_details where appraisal_id=? and user_type_id=? and emp_id=? and "
							+ "appraisal_freq_id=? ");
					pst.setInt(1, uF.parseToInt(getId()));
					pst.setInt(2, uF.parseToInt(getMemberId()));
					pst.setInt(3, uF.parseToInt(getEmpid()));
					pst.setInt(4, uF.parseToInt(getAppFreqId()));
					rs = pst.executeQuery();
//					System.out.println("ASS/2007---pst="+pst);
					while (rs.next()) {
						
						double getans = (rs.getDouble("reviewer_marks") * 5) / appWeightage;
						String answer = uF.formatIntoOneDecimal(getans) + " / 5";
						
						String answerWithUser = "<div style=\"line-height: 13px;\">" + uF.showData(answer, "No answer")
						+ "<span style=\"margin-right: 10px;font-size: 10px; float: right; font-style: italic;\">- by "
						+ useNameMP.get(rs.getString("user_id")) + "</span></div>"
						+ "<div style=\"line-height: 13px; width: 100%; font-style: italic; font-size: 11px;\">"
						+ uF.showData(rs.getString("reviewer_comment"), "No comment") + "</div>";
						
						hmReviewerMarksComment.put(rs.getString("user_type_id") + "_" + rs.getString("user_id"), answerWithUser);
						hmReviewerMarksComment.put(rs.getString("user_type_id") + "_" + rs.getString("user_id")+"_MARKS", rs.getString("reviewer_marks"));
						hmReviewerMarksComment.put(rs.getString("user_type_id") + "_" + rs.getString("user_id")+"_WEIGHTAGE", appWeightage+"");
						
						if (!alRoles.contains(rs.getString("user_type_id"))) {
							alRoles.add(rs.getString("user_type_id"));
						}
						
						if (!rolesUserIds.contains(rs.getString("user_id"))) {
							rolesUserIds.add(rs.getString("user_id"));
						}
					}
					rs.close();
					pst.close();
				}
			//===end parvez date: 18-07-2022===
			}

			// System.out.println("hmQuestionMarks ===> "+hmQuestionMarks);
			// System.out.println("hmQuestionWeightage ===> "+hmQuestionWeightage);

			pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, String> hmLevel = new HashMap<String, String>();
			while (rs.next()) {
				hmLevel.put(rs.getString("appraisal_level_id"), rs.getString("level_title"));
				hmLevel.put(rs.getString("appraisal_level_id") + "_SD", rs.getString("short_description"));
				hmLevel.put(rs.getString("appraisal_level_id") + "_LD", rs.getString("long_description"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from appraisal_scorecard_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, String> hmScoreCard = new HashMap<String, String>();
			while (rs.next()) {
				hmScoreCard.put(rs.getString("scorecard_id"), rs.getString("scorecard_section_name"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from question_bank");
			rs = pst.executeQuery();
			Map<String, String> hmQuestions = new HashMap<String, String>();
			Map<String, List<String>> hmOptions = new HashMap<String, List<String>>();

			while (rs.next()) {
				hmQuestions.put(rs.getString("question_bank_id"), rs.getString("question_text"));

				List alOptions = new ArrayList();
				alOptions.add(rs.getString("option_a"));// 0
				alOptions.add(rs.getString("option_b"));// 1
				alOptions.add(rs.getString("option_c"));// 2
				alOptions.add(rs.getString("option_d"));// 3
				alOptions.add(rs.getString("question_type"));// 4

				hmOptions.put(rs.getString("question_bank_id"), alOptions);
			}
			rs.close();
			pst.close();

			// ===start parvez date: 13-04-2022===
			Map<String, String> hmStrengthImprovements = new HashMap<String, String>();
			pst = con.prepareStatement("select * from reviewee_strength_improvements where emp_id=? and review_id=? and review_freq_id=? and user_type_id=?");
			pst.setInt(1, uF.parseToInt(getEmpid()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			pst.setInt(4, uF.parseToInt(getMemberId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmStrengthImprovements.put(rs.getString("user_id") + "_STRENGTH", rs.getString("areas_of_strength"));
				hmStrengthImprovements.put(rs.getString("user_id") + "_IMPROVEMENT", rs.getString("areas_of_improvement"));
			}
			rs.close();
			pst.close();
			// ===end parvez date: 13-04-2022===

			request.setAttribute("hmLevel", hmLevel);
			request.setAttribute("hmScoreCard", hmScoreCard);
			request.setAttribute("hmQuestions", hmQuestions);
			request.setAttribute("hmOptions", hmOptions);

			request.setAttribute("hmQuestionMarks", hmQuestionMarks);
//			System.out.println("ApSSt/2242--hmQuestionMarks=="+hmQuestionMarks);
			request.setAttribute("hmQuestionWeightage", hmQuestionWeightage);
			request.setAttribute("hmQuestionAnswer", hmQuestionAnswer);
			// request.setAttribute("hmQuestionRemak", hmQuestionRemak);
			request.setAttribute("alRoles", alRoles);
			request.setAttribute("rolesUserIds", rolesUserIds);
			//System.out.println("rolesUserIds"+rolesUserIds);
			request.setAttribute("hmOuterpeerAppraisalDetails", hmOuterpeerAppraisalDetails);

			request.setAttribute("hmScoreQuestionsMap", hmScoreQuestionsMap);
			request.setAttribute("hmOtherQuestionsMap", hmOtherQuestionsMap);
			request.setAttribute("hmLevelScoreMap", hmLevelScoreMap);
			request.setAttribute("hmGoalTargetKraQuestionsMap", hmGoalTargetKraQuestionsMap);
			// ===start parvez date: 13-04-2022===
			request.setAttribute("hmSectionComment", hmSectionComment);
			request.setAttribute("hmStrengthImprovements", hmStrengthImprovements);
			// ===end parvez date: 13-04-2022===
			
		//===start parvez date: 15-07-2022===	
			request.setAttribute("hmSubmitApprovedStatus", hmSubmitApprovedStatus);
			request.setAttribute("hmReviewerMarksComment", hmReviewerMarksComment);
		//===end parvez date: 15-07-2022===	

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	/*
	 * private void getAppraisalQuestionsAnswers() { Connection con = null;
	 * PreparedStatement pst = null; ResultSet rs = null; Database db = new
	 * Database(); UtilityFunctions uF = new UtilityFunctions(); Map<String,
	 * String> useNameMP = new HashMap<String, String>();
	 * 
	 * try {
	 * 
	 * con = db.makeConnection(con);
	 * 
	 * 
	 * Map<String, String> hmorientationMembers = new HashMap<String, String>();
	 * pst = con.prepareStatement("select * from orientation_member"); rs =
	 * pst.executeQuery(); while (rs.next()) {
	 * //hmorientationMembers.put(rs.getString("orientation_member_id"),
	 * rs.getString("member_name"));
	 * hmorientationMembers.put(rs.getString("member_id"),
	 * rs.getString("member_name")); }
	 * 
	 * request.setAttribute("hmorientationMembers", hmorientationMembers);
	 * 
	 * 
	 * 
	 * pst = con.prepareStatement(
	 * "select * from appraisal_question_details where appraisal_id = ?");
	 * pst.setInt(1, uF.parseToInt(getId())); rs = pst.executeQuery();
	 * 
	 * 
	 * 
	 * List alLevelScore = new ArrayList(); List alLevelOther = new ArrayList();
	 * while(rs.next()) {
	 * 
	 * 
	 * System.out.println(rs.getString("other_id")+" "+alLevelOther);
	 * 
	 * 
	 * 
	 * if(uF.parseToInt(rs.getString("other_id"))>0 &&
	 * !alLevelOther.contains(rs.getString("appraisal_level_id"))) {
	 * 
	 * System.out.println("======== 1 ==========="+rs.getString("other_id")+" "+
	 * alLevelOther);
	 * 
	 * 
	 * alLevelOther.add(rs.getString("appraisal_level_id")); } else
	 * if(!alLevelScore.contains(rs.getString("appraisal_level_id"))) {
	 * 
	 * System.out.println("======== 2 ==========="+" "+alLevelOther);
	 * 
	 * alLevelScore.add(rs.getString("appraisal_level_id")); } else {
	 * System.out.println("======== 3 ==========="); }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * }
	 * 
	 * 
	 * 
	 * System.out.println("alLevelOther==="+alLevelOther);
	 * 
	 * 
	 * Map hmScoreQuestionsMap = new HashMap(); Map hmOtherQuestionsMap = new
	 * HashMap(); Map hmLevelScoreMap = new HashMap();
	 * 
	 * Map hmQuestionMarks = new HashMap(); Map hmQuestionWeightage = new
	 * HashMap();
	 * 
	 * List alRoles = new ArrayList();
	 * 
	 * List<List<String>> outerpeerAppraisalDetails = new
	 * ArrayList<List<String>>(); List<List<String>> outerpeerAnsDetails = new
	 * ArrayList<List<String>>();
	 * 
	 * Map hmQuestionAnswer = new HashMap(); Map hmQuestionRemak = new
	 * HashMap();
	 * 
	 * List alQuestion = new ArrayList();
	 * 
	 * pst = con.prepareStatement(
	 * "select * from appraisal_question_details where appraisal_id = ?");
	 * pst.setInt(1, uF.parseToInt(getId())); rs = pst.executeQuery();
	 * 
	 * while(rs.next()) {
	 * 
	 * if(uF.parseToInt(rs.getString("other_id"))>0) { alQuestion =
	 * (List)hmOtherQuestionsMap.get(rs.getString("appraisal_level_id"));
	 * if(alQuestion==null)alQuestion=new ArrayList();
	 * if(!alQuestion.contains(rs.getString("question_id"))) {
	 * alQuestion.add(rs.getString("question_id")); }
	 * hmOtherQuestionsMap.put(rs.getString("appraisal_level_id"), alQuestion);
	 * } else { alQuestion =
	 * (List)hmScoreQuestionsMap.get(rs.getString("scorecard_id"));
	 * if(alQuestion==null)alQuestion=new ArrayList();
	 * if(!alQuestion.contains(rs.getString("question_id"))) {
	 * alQuestion.add(rs.getString("question_id")); }
	 * hmScoreQuestionsMap.put(rs.getString("scorecard_id"), alQuestion);
	 * 
	 * 
	 * 
	 * List alScore =
	 * (List)hmLevelScoreMap.get(rs.getString("appraisal_level_id"));
	 * if(alScore==null)alScore=new ArrayList();
	 * if(!alScore.contains(rs.getString("scorecard_id"))) {
	 * alScore.add(rs.getString("scorecard_id")); }
	 * hmLevelScoreMap.put(rs.getString("appraisal_level_id"), alScore); }
	 * hmQuestionWeightage.put(rs.getString("question_id"),
	 * rs.getString("weightage")); }
	 * 
	 * 
	 * 
	 * if(uF.parseToInt(getMemberId())>0) { if(uF.parseToInt(getMemberId())==4)
	 * {
	 * 
	 * pst = con.prepareStatement(
	 * "select emp_per_id,emp_fname from employee_personal_details"); rs =
	 * pst.executeQuery();
	 * 
	 * while(rs.next()) { useNameMP.put(rs.getString("emp_per_id"),
	 * rs.getString("emp_fname")); }
	 * 
	 * String avgMarks ="",avgWeightage=""; String allAns="",allRemark=""; int
	 * cnt=0; String remark ="",ans=""; pst = con.prepareStatement(
	 * "select * from appraisal_question_answer where appraisal_id = ? and user_type_id=? and emp_id=?"
	 * ); pst = con.prepareStatement(
	 * "select *,(marks*100/weightage) as avgmarks FROM (select user_type_id,question_id,sum(marks) "
	 * +
	 * "as marks,sum(weightage) as weightage,(sum(weightage)/COUNT(weightage)) as avgweightage from "
	 * +
	 * "appraisal_question_answer where appraisal_id = ? and user_type_id=? and emp_id=? group by user_type_id,question_id order by question_id)  as a"
	 * );
	 * 
	 * pst.setInt(1, uF.parseToInt(getId())); pst.setInt(2,
	 * uF.parseToInt(getMemberId())); pst.setInt(3, uF.parseToInt(getEmpid()));
	 * rs = pst.executeQuery(); while(rs.next()) { List<String>
	 * peerAppraisalDetails = new ArrayList<String>(); avgMarks =
	 * uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("avgmarks"));
	 * avgWeightage =
	 * uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("avgweightage"));
	 * 
	 * if(!alRoles.contains(rs.getString("user_type_id"))) {
	 * alRoles.add(rs.getString("user_type_id")); }
	 * peerAppraisalDetails.add(avgMarks);
	 * peerAppraisalDetails.add(avgWeightage);
	 * outerpeerAppraisalDetails.add(peerAppraisalDetails); //
	 * System.out.println("peerAppraisalDetails == "+peerAppraisalDetails +
	 * " remark == "+remark); } String que_id=""; pst = con.prepareStatement(
	 * "select * from appraisal_question_answer where appraisal_id = ? and user_type_id=? and emp_id=? order by question_id"
	 * ); pst.setInt(1, uF.parseToInt(getId())); pst.setInt(2,
	 * uF.parseToInt(getMemberId())); pst.setInt(3, uF.parseToInt(getEmpid()));
	 * rs = pst.executeQuery();
	 * 
	 * while(rs.next()) { System.out.println("User id ======== "+
	 * rs.getString("user_id"));
	 * 
	 * List<String> peerAnsDetails = new ArrayList<String>();
	 * if(rs.getString("answer")!=null) { if(que_id.equals("")) que_id=
	 * rs.getString("question_id"); if(!que_id.equals("") &&
	 * !rs.getString("question_id").equals(que_id)) {
	 * peerAnsDetails.add(allAns);
	 * 
	 * outerpeerAnsDetails.add(peerAnsDetails); allAns ="";que_id=""; } else
	 * if(rs.isLast()) { allAns =allAns + "<br/> "+
	 * useNameMP.get(rs.getString("user_id"))+ " : " + rs.getString("answer");
	 * peerAnsDetails.add(allAns);
	 * 
	 * outerpeerAnsDetails.add(peerAnsDetails); allAns ="";que_id=""; }
	 * if(allAns.equals("")) { allAns =allAns +
	 * useNameMP.get(rs.getString("user_id"))+ " : " + rs.getString("answer"); }
	 * else { allAns =allAns + "<br/> " +
	 * useNameMP.get(rs.getString("user_id"))+ " : " + rs.getString("answer"); }
	 * } if(rs.getString("remark")!=null) { remark += rs.getString("remark"); }
	 * }
	 * 
	 * 
	 * 
	 * // peerAppraisalDetails.add(allAns); //
	 * outerpeerAppraisalDetails.add(peerAppraisalDetails); //
	 * System.out.println
	 * ("outerpeerAppraisalDetails == "+outerpeerAppraisalDetails +
	 * " remark == "+remark);
	 * System.out.println("outerpeerAnsDetails == "+outerpeerAnsDetails );
	 * 
	 * // peerAppraisalDetails.add(allRemark);
	 * 
	 * 
	 * } else { pst = con.prepareStatement(
	 * "select * from appraisal_question_answer where appraisal_id = ? and user_type_id=? and emp_id=?"
	 * ); pst.setInt(1, uF.parseToInt(getId())); pst.setInt(2,
	 * uF.parseToInt(getMemberId())); pst.setInt(3, uF.parseToInt(getEmpid()));
	 * rs = pst.executeQuery(); while(rs.next()) {
	 * hmQuestionMarks.put(rs.getString
	 * ("question_id")+"_"+rs.getString("user_type_id"), rs.getString("marks"));
	 * hmQuestionWeightage
	 * .put(rs.getString("question_id")+"_"+rs.getString("user_type_id"),
	 * rs.getString("weightage"));
	 * 
	 * 
	 * if(rs.getString("answer")!=null) {
	 * hmQuestionAnswer.put(rs.getString("question_id"
	 * )+"_"+rs.getString("user_type_id"), rs.getString("answer")); }
	 * if(rs.getString("remark")!=null) {
	 * hmQuestionRemak.put(rs.getString("question_id"
	 * )+"_"+rs.getString("user_type_id"), rs.getString("remark")); }
	 * if(!alRoles.contains(rs.getString("user_type_id"))) {
	 * alRoles.add(rs.getString("user_type_id")); } } } } else { pst =
	 * con.prepareStatement(
	 * "select * from appraisal_question_answer where appraisal_id = ? and emp_id=? order by user_type_id"
	 * ); pst.setInt(1, uF.parseToInt(getId())); pst.setInt(2,
	 * uF.parseToInt(getEmpid())); rs = pst.executeQuery();
	 * 
	 * 
	 * while(rs.next()) {
	 * hmQuestionMarks.put(rs.getString("question_id")+"_"+rs.
	 * getString("user_type_id"), rs.getString("marks"));
	 * hmQuestionWeightage.put
	 * (rs.getString("question_id")+"_"+rs.getString("user_type_id"),
	 * rs.getString("weightage"));
	 * 
	 * 
	 * if(rs.getString("answer")!=null) {
	 * hmQuestionAnswer.put(rs.getString("question_id"
	 * )+"_"+rs.getString("user_type_id"), rs.getString("answer")); }
	 * if(rs.getString("remark")!=null) {
	 * hmQuestionRemak.put(rs.getString("question_id"
	 * )+"_"+rs.getString("user_type_id"), rs.getString("remark")); }
	 * 
	 * 
	 * if(!alRoles.contains(rs.getString("user_type_id"))) {
	 * alRoles.add(rs.getString("user_type_id")); } } }
	 * 
	 * 
	 * 
	 * 
	 * pst = con.prepareStatement(
	 * "select * from appraisal_level_details where appraisal_id = ?");
	 * pst.setInt(1, uF.parseToInt(getId())); rs = pst.executeQuery();
	 * Map<String, String> hmLevel = new HashMap<String, String>();
	 * while(rs.next()) { hmLevel.put(rs.getString("appraisal_level_id"),
	 * rs.getString("level_title"));
	 * hmLevel.put(rs.getString("appraisal_level_id")+"_SD",
	 * rs.getString("short_description"));
	 * hmLevel.put(rs.getString("long_description")+"_LD",
	 * rs.getString("long_description")); }
	 * 
	 * 
	 * pst = con.prepareStatement(
	 * "select * from appraisal_scorecard_details where appraisal_id = ?");
	 * pst.setInt(1, uF.parseToInt(getId())); rs = pst.executeQuery();
	 * Map<String, String> hmScoreCard = new HashMap<String, String>();
	 * while(rs.next()) { hmScoreCard.put(rs.getString("scorecard_id"),
	 * rs.getString("scorecard_section_name")); }
	 * 
	 * 
	 * pst = con.prepareStatement("select * from question_bank"); rs =
	 * pst.executeQuery(); Map<String, String> hmQuestions = new HashMap<String,
	 * String>(); Map<String, List<String>> hmOptions = new HashMap<String,
	 * List<String>>();
	 * 
	 * 
	 * while(rs.next()) { hmQuestions.put(rs.getString("question_bank_id"),
	 * rs.getString("question_text"));
	 * 
	 * 
	 * List alOptions = new ArrayList();
	 * alOptions.add(rs.getString("option_a"));
	 * alOptions.add(rs.getString("option_b"));
	 * alOptions.add(rs.getString("option_c"));
	 * alOptions.add(rs.getString("option_d"));
	 * alOptions.add(rs.getString("question_type"));
	 * 
	 * hmOptions.put(rs.getString("question_bank_id"), alOptions); }
	 * 
	 * 
	 * 
	 * 
	 * request.setAttribute("hmLevel", hmLevel);
	 * request.setAttribute("hmScoreCard", hmScoreCard);
	 * request.setAttribute("hmQuestions", hmQuestions);
	 * request.setAttribute("hmOptions", hmOptions);
	 * 
	 * request.setAttribute("hmQuestionMarks", hmQuestionMarks);
	 * request.setAttribute("hmQuestionWeightage", hmQuestionWeightage);
	 * request.setAttribute("hmQuestionAnswer", hmQuestionAnswer);
	 * request.setAttribute("hmQuestionRemak", hmQuestionRemak);
	 * request.setAttribute("alRoles", alRoles);
	 * request.setAttribute("outerpeerAppraisalDetails",
	 * outerpeerAppraisalDetails); request.setAttribute("outerpeerAnsDetails",
	 * outerpeerAnsDetails);
	 * 
	 * 
	 * 
	 * request.setAttribute("hmScoreQuestionsMap", hmScoreQuestionsMap);
	 * request.setAttribute("hmOtherQuestionsMap", hmOtherQuestionsMap);
	 * request.setAttribute("hmLevelScoreMap", hmLevelScoreMap);
	 * 
	 * request.setAttribute("alLevelScore", alLevelScore);
	 * request.setAttribute("alLevelOther", alLevelOther);
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } finally {
	 * db.closeConnection(con); db.closeResultSet(rs); db.closeStatements(pst);
	 * } }
	 */

	private void getAppraisalFinalStatus() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		// Map<String,String> orientationMp=getOrientationValue();

		try {

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
		//===start parvez date: 07-07-2022===	
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
		//===end parvez date: 07-07-2022===	
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));

			// Map<String, String> hmFrequency = new HashMap<String, String>();
			// pst = con.prepareStatement("select * from appraisal_frequency");
			// rs = pst.executeQuery();
			// while (rs.next()) {
			// hmFrequency.put(rs.getString("appraisal_frequency_id"),
			// rs.getString("frequency_name"));
			// }
			// Map<String, String> hmDesignation = CF.getDesigMap();
			// Map<String, String> hmGradeMap = CF.getGradeMap();
			// Map<String, String> hmLevelMap = getLevelMap();
			// Map<String, String> hmEmpName = CF.getEmpNameMap(null, null);
			// pst = con
			// .prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			// pst.setInt(1, uF.parseToInt(id));
			// rs = pst.executeQuery();

			// Map<String, String> appraisalMp = new HashMap<String, String>();

			// while (rs.next()) {
			// String
			// memberName=getOrientationMemberDetails(con,rs.getInt("oriented_type"));
			// appraisalMp.put("ID", rs.getString("appraisal_details_id"));
			// appraisalMp.put("APPRAISAL", rs.getString("appraisal_name"));
			// appraisalMp.put("ORIENT",
			// orientationMp.get(rs.getString("oriented_type"))+"&deg( "+memberName+" )");
			// appraisalMp.put("EMPLOYEE",
			// uF.showData(getAppendData(rs.getString("self_ids"),
			// hmEmpName),""));
			// appraisalMp.put("LEVEL",
			// uF.showData(hmLevelMap.get(rs.getString("level_id")), ""));
			// appraisalMp.put("DESIG",hmDesignation.get(rs.getString("desig_id")));
			// appraisalMp.put("GRADE",hmGradeMap.get(rs.getString("grade_id")));
			// appraisalMp.put("WLOCATION", rs.getString("wlocation_id"));
			// appraisalMp.put("PEER", rs.getString("peer_ids"));
			// appraisalMp.put("SELFID", rs.getString("self_ids"));
			// appraisalMp.put("SUPERVISORID", rs.getString("supervisor_id"));
			//
			// appraisalMp.put("FREQUENCY",
			// uF.showData(hmFrequency.get(rs.getString("frequency")),""));
			// appraisalMp.put("FROM",uF.getDateFormat(rs.getString("from_date"),
			// DBDATE,CF.getStrReportDateFormat()));
			// appraisalMp.put("TO",uF.getDateFormat(rs.getString("to_date"),
			// DBDATE,CF.getStrReportDateFormat()));
			//
			//
			// }

			// request.setAttribute("appraisalMp", appraisalMp);

			Map<String, String> hmAppLevelName = new LinkedHashMap<String, String>();
			// List<String> levelList=new ArrayList<String>();
			pst = con.prepareStatement("select appraisal_level_id,level_title from appraisal_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			while (rs.next()) {
				// levelList.add(rs.getString(1));
				hmAppLevelName.put(rs.getString("appraisal_level_id"), rs.getString("level_title"));

			}
			rs.close();
			pst.close();

			request.setAttribute("hmAppLevelName", hmAppLevelName);

			// Map<String,String> hmScoreName=new HashMap<String, String>();
			// pst=con.prepareStatement("select scorecard_id,scorecard_section_name from appraisal_scorecard_details where appraisal_id=?");
			// pst.setInt(1,uF.parseToInt(id));
			// rs=pst.executeQuery();
			// while(rs.next()) {
			// hmScoreName.put(rs.getString(1), rs.getString(2));
			//
			// }

			// Map<String, String> hmUserTypeID = new HashMap<String, String>();
			// pst = con
			// .prepareStatement("select user_type_id,user_type from user_type");
			// rs = pst.executeQuery();
			// while (rs.next()) {
			// hmUserTypeID.put(rs.getString(2),rs.getString(1));
			// }

			Map<String, Map<String, String>> hmScoremarks = new HashMap<String, Map<String, String>>();

			if (memberId != null) {
				pst = con
						.prepareStatement("select *,(marks*100/weightage) as average from(select sum(marks)as marks, sum(weightage) as weightage,scorecard_id "
								+ "from appraisal_question_answer where appraisal_id=? and emp_id=? and user_type_id=? and appraisal_freq_id = ? and scorecard_id!=0 and weightage>0 group by emp_id, scorecard_id)as a");
				pst.setInt(1, uF.parseToInt(id));
				pst.setInt(2, uF.parseToInt(empid));
				pst.setInt(4, uF.parseToInt(getAppFreqId()));
				pst.setInt(3, uF.parseToInt(memberId));

			} else {
				pst = con
						.prepareStatement("select *,(marks*100/weightage) as average from(select sum(marks)as marks, sum(weightage) as weightage,scorecard_id "
								+ "from appraisal_question_answer where appraisal_id=? and emp_id=? and appraisal_freq_id = ? and scorecard_id!=0 and weightage>0 group by emp_id, scorecard_id) as a");
				pst.setInt(1, uF.parseToInt(id));
				pst.setInt(2, uF.parseToInt(empid));
				pst.setInt(3, uF.parseToInt(getAppFreqId()));
			}

			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String> innerMap = new HashMap<String, String>();
				innerMap.put("MARKS", rs.getString("marks"));
				innerMap.put("WEIGHTAGE", rs.getString("weightage"));
				innerMap.put("AVERAGE", uF.parseToDouble(rs.getString("average")) / 20 + "");

				hmScoremarks.put(rs.getString("scorecard_id"), innerMap);

			}
			rs.close();
			pst.close();

			// request.setAttribute("hmScoremarks", hmScoremarks);

			pst = con.prepareStatement("select * from appraisal_scorecard_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(id));

			rs = pst.executeQuery();
			Map<String, List<Map<String, String>>> scoreMp = new HashMap<String, List<Map<String, String>>>();
			// String scorecard_id = null;
			// int j = 0;
			while (rs.next()) {
				/*
				 * if (j == 0) { scorecard_id = rs.getString("scorecard_id"); }
				 * else { scorecard_id += "," + rs.getString("scorecard_id"); }
				 * j++;
				 */
				// hmScoreName.put(rs.getString("scorecard_id"),
				// rs.getString("scorecard_section_name"));
				// List<String> innerList = new ArrayList<String>();
				Map<String, String> innerMap = hmScoremarks.get(rs.getString("scorecard_id"));
				if (innerMap == null)
					innerMap = new HashMap<String, String>();
				innerMap.put("SCORECARD", rs.getString("scorecard_section_name"));
				innerMap.put("SCORE_WEIGHTAGE", rs.getString("scorecard_weightage"));
				innerMap.put("LEVEL_ID", rs.getString("level_id"));
				innerMap.put("SCORE_ID", rs.getString("scorecard_id"));

				List<Map<String, String>> outerList = scoreMp.get(rs.getString("level_id"));
				if (outerList == null)
					outerList = new ArrayList<Map<String, String>>();
				outerList.add(innerMap);
				scoreMp.put(rs.getString("level_id"), outerList);
			}
			rs.close();
			pst.close();

			Map<String, Map<String, String>> hmScoremarks1 = new HashMap<String, Map<String, String>>();

			if (memberId != null) {
				pst = con
						.prepareStatement("select *,(marks*100/weightage) as average from(select sum(marks)as marks, sum(weightage) as weightage,other_id "
								+ "from appraisal_question_answer where appraisal_id=? and emp_id=? and user_type_id=? and appraisal_freq_id = ? and scorecard_id=0  and other_id!=0 and weightage>0 group by emp_id, other_id) as a");
				pst.setInt(1, uF.parseToInt(id));
				pst.setInt(2, uF.parseToInt(empid));
				pst.setInt(4, uF.parseToInt(getAppFreqId()));
				pst.setInt(3, uF.parseToInt(memberId));
			} else {
				pst = con
						.prepareStatement("select *,(marks*100/weightage) as average from(select sum(marks)as marks, sum(weightage) as weightage,other_id "
								+ "from appraisal_question_answer where appraisal_id=? and emp_id=? and scorecard_id=0  and appraisal_freq_id = ? and other_id!=0 and weightage>0 group by emp_id, other_id) as a");
				pst.setInt(1, uF.parseToInt(id));
				pst.setInt(2, uF.parseToInt(empid));
				pst.setInt(3, uF.parseToInt(getAppFreqId()));
			}
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String> innerMap = new HashMap<String, String>();
				innerMap.put("MARKS", rs.getString("marks"));
				innerMap.put("WEIGHTAGE", rs.getString("weightage"));
				innerMap.put("AVERAGE", uF.parseToDouble(rs.getString("average")) / 20 + "");

				hmScoremarks1.put(rs.getString("other_id"), innerMap);

			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from appraisal_other_question_type_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(id));

			rs = pst.executeQuery();
			while (rs.next()) {

				Map<String, String> innerMap = hmScoremarks1.get(rs.getString("othe_question_type_id"));
				if (innerMap == null)
					innerMap = new HashMap<String, String>();
				innerMap.put("SCORECARD", rs.getString("other_answer_type"));
				innerMap.put("SCORE_WEIGHTAGE", rs.getString("other_question_type"));
				innerMap.put("LEVEL_ID", rs.getString("level_id"));
				innerMap.put("SCORE_ID", rs.getString("othe_question_type_id"));

				List<Map<String, String>> outerList = scoreMp.get(rs.getString("level_id"));
				if (outerList == null)
					outerList = new ArrayList<Map<String, String>>();
				outerList.add(innerMap);
				scoreMp.put(rs.getString("level_id"), outerList);

			}
			rs.close();
			pst.close();

			request.setAttribute("scoreMp", scoreMp);
			// request.setAttribute("hmScoreName", hmScoreName);

			pst = con
					.prepareStatement("Select sattlement_comment,if_approved,user_id, emp_fname,emp_mname, emp_lname, _date from appraisal_final_sattlement afs,employee_personal_details epd where afs.user_id = epd.emp_per_id and emp_id=? and appraisal_id=? and appraisal_freq_id= ?");
			// pst = con.prepareStatement(selectFinalSattlement);
			pst.setInt(1, uF.parseToInt(getEmpid()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			// System.out.println("pst===>"+pst);
			String strFinalComments = null;
			String strAppraisedBy = null;
			String strAppraisedOn = null;
			while (rs.next()) {
				strFinalComments = rs.getString("sattlement_comment");
				if (strFinalComments != null) {
					strFinalComments = strFinalComments.replace("\n", "<br/>");
				}

				String strEmpMName = "";
				if (flagMiddleName) {
					if (rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length() > 0) {
						strEmpMName = " " + rs.getString("emp_mname");
					}
				}

				strAppraisedBy = rs.getString("emp_fname") + strEmpMName + " " + rs.getString("emp_lname");
				strAppraisedOn = uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat());
			}
			rs.close();
			pst.close();

			request.setAttribute("strFinalComments", strFinalComments);
			request.setAttribute("strAppraisedBy", strAppraisedBy);
			request.setAttribute("strAppraisedOn", strAppraisedOn);
			// System.out.println("strAppraisedBy==>"+strAppraisedBy+"==>strAppraisedOn==>"+strAppraisedOn+"==>strFinalComments==>"+strFinalComments);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	// private Map<String,String> getOrientationValue() {
	// Connection con = null;
	// PreparedStatement pst = null;
	// Database db = new Database();
	// ResultSet rs = null;
	//
	// Map<String,String> orientationMp=new HashMap<String,String>();
	// try {
	//
	// con = db.makeConnection(con);
	//
	// pst = con.prepareStatement("select * from apparisal_orientation");
	// rs=pst.executeQuery();
	// while(rs.next()) {
	// orientationMp.put(rs.getString("apparisal_orientation_id"),rs.getString("orientation_name"));
	// }
	//
	// request.setAttribute("orientationMp", orientationMp);
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// db.closeConnection(con);
	// db.closeStatements(pst);
	// }
	// return orientationMp;
	// }

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
			// pst = con.prepareStatement(selectOrientationMember);
			rs = pst.executeQuery();
			while (rs.next()) {
				// orientationMemberMp.put(rs.getString("orientation_member_id"),
				// rs.getString("member_name"));
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

	// private String getOrientationMemberDetails(int id) {
	// Connection con = null;
	// PreparedStatement pst = null;
	// Database db = new Database();
	// ResultSet rs = null;
	// Map<String,String>
	// orientationMp=(Map<String,String>)request.getAttribute("orientationMemberMp");
	// StringBuilder sb=new StringBuilder();
	// try {
	// List<String> memberList=new ArrayList<String>();
	// con = db.makeConnection(con);
	//
	// pst =
	// con.prepareStatement("select * from orientation_details where orientation_id=?");
	// pst.setInt(1,id);
	// rs=pst.executeQuery();
	//
	// while(rs.next()) {
	// sb.append(orientationMp.get(rs.getString("member_id"))+",");
	// memberList.add(rs.getString("member_id"));
	// }
	//
	// request.setAttribute("memberList", memberList);
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// db.closeConnection(con);
	// db.closeStatements(pst);
	// }
	// return sb.toString();
	// }

	// private String getAppendData(String strID, Map<String, String> mp) {
	// StringBuilder sb = new StringBuilder();
	//
	// if (strID != null && !strID.equals("")) {
	//
	// if (strID.contains(",")) {
	//
	// String[] temp = strID.split(",");
	//
	// for (int i = 0; i < temp.length; i++) {
	// if (i == 0) {
	// sb.append(mp.get(temp[i].trim()));
	// } else {
	// sb.append("," + mp.get(temp[i].trim()));
	// }
	// }
	// } else {
	// return mp.get(strID);
	// }
	//
	// } else {
	// return null;
	// }
	// return sb.toString();
	// }

	public Map<String, String> getLevelMap() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new HashMap<String, String>();
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectLevel);
			rs = pst.executeQuery();

			while (rs.next()) {
				hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name") + "[" + rs.getString("level_code") + "]");
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmLevelMap;
	}
	
//===created by parvez date: 08-07-2022===
	//===start parvez=== 
	private void approveUserFeedback() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update appraisal_question_answer set hr_approval=?,hr_approval_comment=? where emp_id=? and appraisal_id=? and user_id=? and user_type_id=? "
						+ "and appraisal_freq_id=? and reviewer_or_appraiser=?");
			pst.setBoolean(1, true);
			pst.setString(2, getApproveComment());
			pst.setInt(3, uF.parseToInt(getEmpid()));
			pst.setInt(4, uF.parseToInt(getId()));
			pst.setInt(5, uF.parseToInt(getUserId()));
			pst.setInt(6, uF.parseToInt(getMemberId()));
			pst.setInt(7, uF.parseToInt(getAppFreqId()));
			pst.setInt(8, 0);
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0){
				request.setAttribute("STATUS_MSG", SUCCESSM + "Feedback Approved successfully." + END);
			}
			
		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", ERRORM + "Feedback approval failed, please try again." + END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	//===end===

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

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

	public String getReopenComment() {
		return reopenComment;
	}

	public void setReopenComment(String reopenComment) {
		this.reopenComment = reopenComment;
	}
//===start parvez date: 08-07-2022===
	public String getApproveComment() {
		return approveComment;
	}

	public void setApproveComment(String approveComment) {
		this.approveComment = approveComment;
	}
//===end parvez date: 08-07-2022===
}