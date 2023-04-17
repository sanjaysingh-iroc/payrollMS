package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class MyReviewScoreStatus implements ServletRequestAware, IStatements {

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
		getOrientationMember();
		request.setAttribute(PAGE, "/jsp/performance/MyReviewScoreStatus.jsp");
		request.setAttribute(TITLE, "My Review Score Status");
		
		getAppraisalQuestionsAnswers();
		
		getAppraisalFinalStatus();
		
		request.setAttribute("empid", getEmpid());
		if (type != null && type.equals("popup"))
			return "popup";
		return "success";
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
			Map<String, String> useNameMP = CF.getEmpNameMap(con, null, null);
			request.setAttribute("useNameMP", useNameMP);
			
			Map<String, String> hmorientationMembers = new HashMap<String, String>();
			pst = con.prepareStatement("select * from orientation_member");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmorientationMembers.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmorientationMembers", hmorientationMembers);
			
			List<String> sectionIdsList = new ArrayList<String>();
			pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id = ? ");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				sectionIdsList.add(rs.getString("main_level_id"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("sectionIdsList ===> "+sectionIdsList);
			request.setAttribute("sectionIdsList", sectionIdsList);
			
			/*pst = con.prepareStatement("select * from appraisal_question_details where appraisal_id = ?");*/
			pst = con.prepareStatement("select distinct aqd.appraisal_level_id,main_level_id,other_id from " +
					"appraisal_question_details aqd, appraisal_level_details ald where aqd.appraisal_level_id = ald.appraisal_level_id " +
					"and aqd.appraisal_id = ? order by aqd.appraisal_level_id");
			
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			
			Map<String, List<String>> hmSubsectionIds = new LinkedHashMap<String, List<String>>();
			while(rs.next()){
				List<String> alLevelScore = hmSubsectionIds.get(rs.getString("main_level_id")+"SCR");
				if(alLevelScore==null)alLevelScore = new ArrayList<String>();
				
				List<String> alLevelOther = hmSubsectionIds.get(rs.getString("main_level_id")+"OTHR");
				if(alLevelOther==null)alLevelOther = new ArrayList<String>();
				
				if(uF.parseToInt(rs.getString("other_id"))>0 && !alLevelOther.contains(rs.getString("appraisal_level_id"))) {
					alLevelOther.add(rs.getString("appraisal_level_id"));
				}else if(!alLevelScore.contains(rs.getString("appraisal_level_id"))){
					alLevelScore.add(rs.getString("appraisal_level_id"));
				}else{
				}
				hmSubsectionIds.put(rs.getString("main_level_id")+"SCR", alLevelScore);
				hmSubsectionIds.put(rs.getString("main_level_id")+"OTHR", alLevelOther);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmSubsectionIds ===> "+hmSubsectionIds);
			request.setAttribute("hmSubsectionIds", hmSubsectionIds);
		
			
			pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id = ?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, String> hmSectionDetails = new HashMap<String, String>();
			while(rs.next()){
				hmSectionDetails.put(rs.getString("main_level_id"), rs.getString("level_title"));
				hmSectionDetails.put(rs.getString("main_level_id")+"_SD", rs.getString("short_description"));
				hmSectionDetails.put(rs.getString("main_level_id")+"_LD", rs.getString("long_description"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmSectionDetails ===> "+hmSectionDetails);
			request.setAttribute("hmSectionDetails", hmSectionDetails);

			Map<String, List<String>> hmScoreQuestionsMap = new HashMap<String, List<String>>();
			Map<String, List<String>> hmOtherQuestionsMap = new HashMap<String, List<String>>();
			Map<String, List<String>> hmLevelScoreMap = new HashMap<String, List<String>>();

			Map<String, String> hmQuestionMarks = new HashMap<String, String>();
			Map<String, String> hmQuestionWeightage = new HashMap<String, String>();
			
			List<String> alRoles = new ArrayList<String>();
			List rolesUserIds = new ArrayList();
			
			Map<String, List<String>> hmOuterpeerAppraisalDetails = new HashMap<String, List<String>>();
			
			
			Map<String, String> hmQuestionAnswer = new HashMap<String, String>();
			Map<String, String> hmQuestionRemak = new HashMap<String, String>(); 
			
			List<String> alQuestion = new ArrayList<String>();
			
			pst = con.prepareStatement("select * from appraisal_question_details where appraisal_id = ?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			
			while(rs.next()){
				
				if(uF.parseToInt(rs.getString("other_id"))>0){
					alQuestion =hmOtherQuestionsMap.get(rs.getString("appraisal_level_id"));
					if(alQuestion==null)alQuestion=new ArrayList<String>();
					if(!alQuestion.contains(rs.getString("question_id"))){
						alQuestion.add(rs.getString("question_id"));
					}
					hmOtherQuestionsMap.put(rs.getString("appraisal_level_id"), alQuestion);
				}else{
					alQuestion = hmScoreQuestionsMap.get(rs.getString("scorecard_id"));
					if(alQuestion==null)alQuestion=new ArrayList<String>();
					if(!alQuestion.contains(rs.getString("question_id"))){
						alQuestion.add(rs.getString("question_id"));
					}
					hmScoreQuestionsMap.put(rs.getString("scorecard_id"), alQuestion);
					
					List<String> alScore = hmLevelScoreMap.get(rs.getString("appraisal_level_id"));
					if(alScore==null)alScore=new ArrayList<String>();
					if(!alScore.contains(rs.getString("scorecard_id"))){
						alScore.add(rs.getString("scorecard_id"));
					}
					hmLevelScoreMap.put(rs.getString("appraisal_level_id"), alScore);
				}
				hmQuestionWeightage.put(rs.getString("question_id"), rs.getString("weightage"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmQuestionWeightage 111 ===> "+hmQuestionWeightage);
			
			pst = con.prepareStatement("select * from question_bank");
			rs = pst.executeQuery();
			Map<String, String> hmQueAnsType = new HashMap<String, String>();
			while(rs.next()){
				hmQueAnsType.put(rs.getString("question_bank_id"), rs.getString("question_type"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_answer_type_sub");
			rs = pst.executeQuery();
			Map<String, String> hmAnsTypeAnswer = new HashMap<String, String>();
			while(rs.next()){
				hmAnsTypeAnswer.put(rs.getString("answer_type_id")+"_"+rs.getString("score"), rs.getString("score_label"));
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(getMemberId())>0){
				
				if(uF.parseToInt(getMemberId())==4){
					
					pst = con.prepareStatement("select emp_per_id,emp_fname from employee_personal_details");
					rs = pst.executeQuery();
					while(rs.next()){
						useNameMP.put(rs.getString("emp_per_id"), rs.getString("emp_fname"));
					}
					rs.close();
					pst.close();
					
					String avgMarks ="",avgWeightage="";
					String remark ="";
					/*pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id = ? and user_type_id=? and emp_id=?");*/
					pst = con.prepareStatement("select *,(marks/weightcnt) as avgmarks FROM (select user_type_id,question_id,sum(marks) " +
							"as marks,sum(weightage) as weightage,(sum(weightage)/COUNT(weightage)) as avgweightage,COUNT(weightage) as weightcnt from " +
							"appraisal_question_answer where appraisal_id = ? and user_type_id=? and emp_id=? and appraisal_freq_id = ? group by user_type_id,question_id order by question_id)  as a");
					pst.setInt(1, uF.parseToInt(getId()));
					pst.setInt(2, uF.parseToInt(getMemberId()));
					pst.setInt(3, uF.parseToInt(getEmpid()));
					pst.setInt(4, uF.parseToInt(getAppFreqId()));
					rs = pst.executeQuery();
//					System.out.println("pst marks == " + pst);
					while(rs.next()){
						List<String> peerAppraisalDetails = new ArrayList<String>();
						avgMarks = uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("avgmarks"));
						avgWeightage = uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("avgweightage"));
						
						if(!alRoles.contains(rs.getString("user_type_id"))){
							alRoles.add(rs.getString("user_type_id"));
						}
						
						if(!rolesUserIds.contains(rs.getString("user_id"))){
							rolesUserIds.add(rs.getString("user_id"));
						}
						
					peerAppraisalDetails.add(avgMarks);
					peerAppraisalDetails.add(avgWeightage);
					hmOuterpeerAppraisalDetails.put(rs.getString("question_id"),peerAppraisalDetails);
//					System.out.println("peerAppraisalDetails == "+peerAppraisalDetails + " remark == "+remark);
				}
					rs.close();
					pst.close();
					
//					System.out.println("hmOuterpeerAppraisalDetails ===> " + hmOuterpeerAppraisalDetails);
					
					pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id = ? and user_type_id=? and emp_id=? and appraisal_freq_id = ? order by question_id");
					pst.setInt(1, uF.parseToInt(getId()));
					pst.setInt(2, uF.parseToInt(getMemberId()));
					pst.setInt(3, uF.parseToInt(getEmpid()));
					pst.setInt(4, uF.parseToInt(getAppFreqId()));
					rs = pst.executeQuery();
//					System.out.println("pst ans ======== "+pst);
					Map<String, String> hmOuterpeerAnsDetails = new HashMap<String, String>();
					while(rs.next()){
//						List<String> peerAnsDetails = new ArrayList<String>();
						String ansType = hmQueAnsType.get(rs.getString("question_id"));
						String answer = null;
						if(uF.parseToInt(ansType) == 4 ){
							answer = hmAnsTypeAnswer.get(ansType+"_"+rs.getString("answer"))+" ("+rs.getString("answer")+")";
						}else if(uF.parseToInt(ansType) == 5 || uF.parseToInt(ansType) == 6){
							String getans = rs.getString("answer").length()>0 ? rs.getString("answer").substring(0,rs.getString("answer").length()-1) : "";
							answer = hmAnsTypeAnswer.get(ansType+"_"+getans);
						}else{ 
							answer = rs.getString("answer");
						}
						String QuestionAns=hmOuterpeerAnsDetails.get(rs.getString("question_id")+"_"+rs.getString("user_type_id")+"_"+rs.getString("user_id"));
						if(QuestionAns==null) {
//							QuestionAns= useNameMP.get(rs.getString("user_id"))+ " : " + uF.showData(answer,"");
							QuestionAns= "<div style=\"line-height: 13px;\">" + uF.showData(answer,"No answer")+"<span style=\"margin-right: 10px;font-size: 10px; float: right; font-style: italic;\">- by "+useNameMP.get(rs.getString("user_id"))+"</span></div>"+
							"<div style=\"line-height: 13px; width: 100%; font-style: italic; font-size: 11px;\">" + uF.showData(rs.getString("answers_comment"), "No comment") +"</div>";
						}else{
							QuestionAns+="<br/> <div style=\"line-height: 13px;\">" + uF.showData(answer,"No answer")+"<span style=\"margin-right: 10px;font-size: 10px; float: right; font-style: italic;\">- by "+useNameMP.get(rs.getString("user_id"))+"</span></div>"+
							"<div style=\"line-height: 13px; width: 100%; font-style: italic; font-size: 11px;\">" + uF.showData(rs.getString("answers_comment"), "No comment") +"</div>";
//							 QuestionAns+="<br/> "+ useNameMP.get(rs.getString("user_id"))+ " : " + uF.showData(answer,"");
						}
						
						if(rs.getString("remark")!=null){
							remark += rs.getString("remark");
						}
						hmOuterpeerAnsDetails.put(rs.getString("question_id")+"_"+rs.getString("user_type_id")+"_"+rs.getString("user_id"), QuestionAns);
					}
					rs.close();
					pst.close();
					
//					System.out.println("hmOuterpeerAnsDetails == "+hmOuterpeerAnsDetails );
					request.setAttribute("hmOuterpeerAnsDetails", hmOuterpeerAnsDetails);
					
				}else{ 
				pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id = ? and user_type_id=? and emp_id=? and appraisal_freq_id = ?");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.setInt(2, uF.parseToInt(getMemberId()));
				pst.setInt(3, uF.parseToInt(getEmpid()));
				pst.setInt(4, uF.parseToInt(getAppFreqId()));
				rs = pst.executeQuery();
				while(rs.next()){
					String ansType = hmQueAnsType.get(rs.getString("question_id"));
					String answer = null;
					if(uF.parseToInt(ansType) == 4 ){
						answer = hmAnsTypeAnswer.get(ansType+"_"+rs.getString("answer"))+" ("+rs.getString("answer")+")";
					}else if(uF.parseToInt(ansType) == 5 || uF.parseToInt(ansType) == 6){
						String getans = rs.getString("answer").length()>0 ? rs.getString("answer").substring(0,rs.getString("answer").length()-1) : "";
						answer = hmAnsTypeAnswer.get(ansType+"_"+getans);
					}else{ 
						answer = rs.getString("answer");
					}
					
					hmQuestionMarks.put(rs.getString("question_id")+"_"+rs.getString("user_type_id")+"_"+rs.getString("user_id"), rs.getString("marks"));
					hmQuestionWeightage.put(rs.getString("question_id"), rs.getString("weightage"));
					
					if(rs.getString("answer")!=null  || rs.getString("marks")!= null){
						answer = "<div style=\"line-height: 13px;\">" + uF.showData(answer,"No answer")+"<span style=\"margin-right: 10px;font-size: 10px; float: right; font-style: italic;\">- by "+useNameMP.get(rs.getString("user_id"))+"</span></div>"+
						"<div style=\"line-height: 13px; width: 100%; font-style: italic; font-size: 11px;\">" + uF.showData(rs.getString("answers_comment"), "No comment") +"</div>";
						hmQuestionAnswer.put(rs.getString("question_id")+"_"+rs.getString("user_type_id")+"_"+rs.getString("user_id"), answer);
						
					}
					if(rs.getString("remark")!=null){
						hmQuestionRemak.put(rs.getString("question_id")+"_"+rs.getString("user_type_id")+"_"+rs.getString("user_id"), rs.getString("remark"));
					}
					if(!alRoles.contains(rs.getString("user_type_id"))){
						alRoles.add(rs.getString("user_type_id"));
					}
					if(!rolesUserIds.contains(rs.getString("user_id"))){
						rolesUserIds.add(rs.getString("user_id"));
					}
				}
				rs.close();
				pst.close();
				
			}
		}else{
				pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id = ? and emp_id=? and appraisal_freq_id = ? order by user_type_id");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.setInt(2, uF.parseToInt(getEmpid()));
				pst.setInt(3, uF.parseToInt(getAppFreqId()));
				rs = pst.executeQuery();
				while(rs.next()){
					String ansType = hmQueAnsType.get(rs.getString("question_id"));
					String answer = null;
					if(uF.parseToInt(ansType) == 4 ){
						answer = hmAnsTypeAnswer.get(ansType+"_"+rs.getString("answer"))+" ("+rs.getString("answer")+")";
					}else if(uF.parseToInt(ansType) == 5 || uF.parseToInt(ansType) == 6){
						String getans = rs.getString("answer").length()>0 ? rs.getString("answer").substring(0,rs.getString("answer").length()-1) : "";
						answer = hmAnsTypeAnswer.get(ansType+"_"+getans);
					}else{ 
						answer = rs.getString("answer");
					}
					
					hmQuestionMarks.put(rs.getString("question_id")+"_"+rs.getString("user_type_id")+"_"+rs.getString("user_id"), rs.getString("marks"));
					hmQuestionWeightage.put(rs.getString("question_id"), rs.getString("weightage"));
					
					if(rs.getString("answer")!=null || rs.getString("marks")!= null){
						answer = "<div style=\"line-height: 13px;\">" + uF.showData(answer,"No answer")+"<span style=\"margin-right: 10px;font-size: 10px; float: right; font-style: italic;\">- by "+useNameMP.get(rs.getString("user_id"))+"</span></div>"+
						"<div style=\"line-height: 13px; width: 100%; font-style: italic; font-size: 11px;\">" + uF.showData(rs.getString("answers_comment"), "No comment") +"</div>";
						
						hmQuestionAnswer.put(rs.getString("question_id")+"_"+rs.getString("user_type_id")+"_"+rs.getString("user_id"), answer);
					}
					if(rs.getString("remark")!=null){
						hmQuestionRemak.put(rs.getString("question_id")+"_"+rs.getString("user_type_id")+"_"+rs.getString("user_id"), rs.getString("remark"));
					}
					
					if(!alRoles.contains(rs.getString("user_type_id"))){
						alRoles.add(rs.getString("user_type_id"));
					}
					
					if(!rolesUserIds.contains(rs.getString("user_id"))){
						rolesUserIds.add(rs.getString("user_id"));
					}
				}
				rs.close();
				pst.close();
			}
			
//			System.out.println("hmQuestionMarks ===> "+hmQuestionMarks);
//			System.out.println("hmQuestionWeightage ===> "+hmQuestionWeightage);
//			System.out.println("alRoles ===> "+alRoles);
//			System.out.println("rolesUserIds ===> "+rolesUserIds);
			
			pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id = ?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, String> hmLevel = new HashMap<String, String>();
			while(rs.next()){
				hmLevel.put(rs.getString("appraisal_level_id"), rs.getString("level_title"));
				hmLevel.put(rs.getString("appraisal_level_id")+"_SD", rs.getString("short_description"));
				hmLevel.put(rs.getString("long_description")+"_LD", rs.getString("long_description"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_scorecard_details where appraisal_id = ?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, String> hmScoreCard = new HashMap<String, String>();
			while(rs.next()){
				hmScoreCard.put(rs.getString("scorecard_id"), rs.getString("scorecard_section_name"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from question_bank");
			rs = pst.executeQuery();
			Map<String, String> hmQuestions = new HashMap<String, String>();
			Map<String, List<String>> hmOptions = new HashMap<String, List<String>>();
			
			while(rs.next()){
				hmQuestions.put(rs.getString("question_bank_id"), rs.getString("question_text"));
				
				List<String> alOptions = new ArrayList<String>();
				alOptions.add(rs.getString("option_a"));
				alOptions.add(rs.getString("option_b"));
				alOptions.add(rs.getString("option_c"));
				alOptions.add(rs.getString("option_d"));
				alOptions.add(rs.getString("question_type"));
				
				hmOptions.put(rs.getString("question_bank_id"), alOptions);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmLevel", hmLevel);
			request.setAttribute("hmScoreCard", hmScoreCard);
			request.setAttribute("hmQuestions", hmQuestions);
			request.setAttribute("hmOptions", hmOptions);
			
			request.setAttribute("hmQuestionMarks", hmQuestionMarks);
			request.setAttribute("hmQuestionWeightage", hmQuestionWeightage);
			request.setAttribute("hmQuestionAnswer", hmQuestionAnswer);
			request.setAttribute("hmQuestionRemak", hmQuestionRemak);
			request.setAttribute("alRoles", alRoles);
			request.setAttribute("rolesUserIds", rolesUserIds);
			request.setAttribute("hmOuterpeerAppraisalDetails", hmOuterpeerAppraisalDetails);
			
			request.setAttribute("hmScoreQuestionsMap", hmScoreQuestionsMap);
			request.setAttribute("hmOtherQuestionsMap", hmOtherQuestionsMap);
			request.setAttribute("hmLevelScoreMap", hmLevelScoreMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
		
	private void getAppraisalFinalStatus() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		// Map<String,String> orientationMp=getOrientationValue();
		con = db.makeConnection(con);

		try {
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmAppLevelName = new LinkedHashMap<String, String>();
			pst = con.prepareStatement("select appraisal_level_id,level_title from appraisal_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmAppLevelName.put(rs.getString("appraisal_level_id"), rs.getString("level_title"));
			}
			rs.close();
			pst.close();

			request.setAttribute("hmAppLevelName", hmAppLevelName);

			Map<String, Map<String, String>> hmScoremarks = new HashMap<String, Map<String, String>>();

			if (memberId != null) {
				pst = con.prepareStatement("select *,(marks*100/weightage) as average from (select sum(marks)as marks, sum(weightage) as weightage,scorecard_id "
						+ "from appraisal_question_answer where appraisal_id=? and emp_id=? and user_type_id=? and appraisal_freq_id = ? and scorecard_id!=0 and weightage>0 group by emp_id, scorecard_id)as a");
				pst.setInt(1, uF.parseToInt(id));
				pst.setInt(2, uF.parseToInt(empid));
				pst.setInt(3, uF.parseToInt(memberId));
				pst.setInt(4, uF.parseToInt(getAppFreqId()));

			} else {
				pst = con.prepareStatement("select *,(marks*100/weightage) as average from(select sum(marks)as marks, sum(weightage) as weightage,scorecard_id "
						+ "from appraisal_question_answer where appraisal_id=? and emp_id=? and appraisal_freq_id = ? and scorecard_id!=0 and weightage>0 group by emp_id, scorecard_id)as a");
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

			pst = con.prepareStatement("select * from appraisal_scorecard_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			Map<String, List<Map<String, String>>> scoreMp = new HashMap<String, List<Map<String, String>>>();
			while (rs.next()) {
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
				pst = con.prepareStatement("select *,(marks*100/weightage) as average from(select sum(marks)as marks, sum(weightage) as weightage,other_id "
						+ "from appraisal_question_answer where appraisal_id=? and emp_id=? and user_type_id=? and appraisal_freq_id = ? and scorecard_id=0  and other_id!=0 and weightage>0 group by emp_id, other_id)as a");
				pst.setInt(1, uF.parseToInt(id));
				pst.setInt(2, uF.parseToInt(empid));
				pst.setInt(3, uF.parseToInt(memberId));
				pst.setInt(4, uF.parseToInt(getAppFreqId()));
			} else {
				pst = con.prepareStatement("select *,(marks*100/weightage) as average from(select sum(marks)as marks, sum(weightage) as weightage,other_id "
						+ "from appraisal_question_answer where appraisal_id=? and emp_id=? and appraisal_freq_id = ? and scorecard_id=0  and other_id!=0 and weightage>0 group by emp_id, other_id)as a");
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

			pst = con.prepareStatement("Select sattlement_comment,if_approved,user_id, emp_fname, emp_mname,emp_lname, _date from appraisal_final_sattlement afs,employee_personal_details epd where afs.user_id = epd.emp_per_id and emp_id=? and appraisal_id=? and appraisal_freq_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpid()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			String strFinalComments = null;
			String strAppraisedBy = null;
			String strAppraisedOn = null;
			while(rs.next()){
				strFinalComments = rs.getString("sattlement_comment");
				if(strFinalComments!=null){
					strFinalComments = strFinalComments.replace("\n", "<br/>");
				}
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				
				strAppraisedBy = rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname");
				strAppraisedOn = uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat());
			}
			rs.close();
			pst.close();
			
			request.setAttribute("strFinalComments", strFinalComments);
			request.setAttribute("strAppraisedBy", strAppraisedBy);
			request.setAttribute("strAppraisedOn", strAppraisedOn);
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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
//			pst = con.prepareStatement(selectOrientationMember);
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
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
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
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
		return hmLevelMap;
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

	
}
