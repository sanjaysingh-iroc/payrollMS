package com.konnect.jpms.offboarding;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ResigFeedbackForm extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strSessionUserTypeID;
	String strUserType =  null;
	CommonFunctions CF;
	
	private String strEmpId;
	private String resignId;
	private String formId;
	
	private String levelCount;
	private String currentLevel;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)  return "login";
		
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strSessionUserTypeID = (String) session.getAttribute(USERTYPEID);
		strUserType = (String)session.getAttribute(USERTYPE);
		
		request.setAttribute("arrEnabledModules", CF.getArrEnabledModules());
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		UtilityFunctions uF = new UtilityFunctions();
//		EncryptionUtility eU = new EncryptionUtility();
		getApprovalStatus(WORK_FLOW_RESIGN);
		getanswerTypeMap(uF);
		getQuestionSubType(uF);
		getEmployeeDetails(uF);
		getFormDetails(uF);
		getFormQuestionMap(uF);
		
		if(levelCount == null){
			levelCount = "1";
		}else{
			int cnt = uF.parseToInt(levelCount);
			cnt++;
			setLevelCount(""+cnt);
		}
		
		String submit = request.getParameter("submit");
		String btnfinish = request.getParameter("btnfinish");
		String levelAppSystem=request.getParameter("levelAppSystem");
		/*System.out.println("submit == > "+submit);
		System.out.println("btnfinish ========= > "+btnfinish);
		System.out.println("levelAppSystem ========= > "+levelAppSystem);
		System.out.println("levelCount ========= > "+levelCount);*/
		if (submit == null && btnfinish == null) {
			return getLevelQuestion(uF);
		} else {
			getPreviousLevelData(uF);
//			if(levelAppSystem !=null ){
			if((submit != null && submit.equals("Next")) || (btnfinish != null && btnfinish.equals("Finish"))){
				insertMarks(uF);
			}
			if(uF.parseToInt(levelCount)>2) {
				if(swapLevelId(uF)) {
					if(btnfinish != null && btnfinish.equals("Finish")) {
						/*if(uF.parseToInt(getStrEmpId()) > 0) {
							String encodeEmpId = eU.encode(getStrEmpId());
							setStrEmpId(encodeEmpId);
						}
						if(uF.parseToInt(getResignId()) > 0) {
							String encodeResignId = eU.encode(getResignId());
							setResignId(encodeResignId);
						}*/
						return "finish";
					} else {
						return "update";
					}
				}
			}
		}	
		
		return getLevelQuestion(uF);
	}
	
	private void getApprovalStatus(String effectivetype) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);

			Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if (hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmEmpUserId = CF.getEmpUserIdMap(con);
			if(hmEmpUserId == null) hmEmpUserId = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from work_flow_details where effective_id=? and effective_type=? order by member_position");
			pst.setInt(1, uF.parseToInt(getResignId()));
			pst.setString(2, effectivetype);
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmApprovalStatus = new HashMap<String, String>();
			String strMemberPosition = null;
			int step = 1;
			boolean flag = false;
			StringBuilder resigAcceptedBy = null;
			while (rs.next()) {

				String memberStep = hmApprovalStatus.get(rs.getString("effective_id"));
				String strUserTypeName = uF.parseToInt(rs.getString("user_type_id")) > 0 ?  uF.showData(hmUserTypeMap.get(rs.getString("user_type_id")), "") : "";
				String strApproveMemTick = "";
				String strApproveMemReason = "";
			
				
				if (uF.parseToInt(rs.getString("is_approved")) == 1 || uF.parseToInt(rs.getString("is_approved")) == -1) {
					if (uF.parseToInt(rs.getString("is_approved")) == 1){
						 /*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
						strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
						
					} else if (uF.parseToInt(rs.getString("is_approved")) == -1){
						/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
						strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Denied\"></i>";
						
					}
					if(rs.getString("reason")!=null && !rs.getString("reason").trim().equals("") && !rs.getString("reason").trim().equalsIgnoreCase("NULL")){
						strApproveMemReason = "[Reason: "+rs.getString("reason").trim()+"]";
					}
				} else if (uF.parseToInt(rs.getString("is_approved")) == 0){
					flag = true;
				}
				
//				System.out.println("strApproveMemReason==>"+strApproveMemReason);

				if (memberStep == null) {
					memberStep = "<strong>" + strUserTypeName + ":</strong> " + hmEmployeeNameMap.get(rs.getString("emp_id").trim())+strApproveMemReason+" "+ strApproveMemTick;
					strMemberPosition = rs.getString("member_position");
					
					
				} else {
					if (strMemberPosition != null && strMemberPosition.equals(rs.getString("member_position"))) {
						memberStep += ", "+ strApproveMemTick  + hmEmployeeNameMap.get(rs.getString("emp_id").trim()) + strUserTypeName + strApproveMemReason;
						
					} else {
						step++;
						strMemberPosition = rs.getString("member_position");
					
						// memberStep+=" ======> "+hmEmployeeNameMap.get(rs.getString("emp_id").trim())+strUserTypeName;
						//memberStep += "<br/><strong>Step " + step + ".</strong> "+ strApproveMemTick + hmEmployeeNameMap.get(rs.getString("emp_id").trim()) + strUserTypeName + strApproveMemReason;
						memberStep += "<br/><strong>"+strUserTypeName +  ":</strong> " + hmEmployeeNameMap.get(rs.getString("emp_id").trim()) + strApproveMemReason+" "+ strApproveMemTick;
					}
				}
				if (uF.parseToInt(rs.getString("is_approved")) == 1 || uF.parseToInt(rs.getString("is_approved")) == -1) {
					if(resigAcceptedBy == null) {
						resigAcceptedBy = new StringBuilder();
						resigAcceptedBy.append(hmEmployeeNameMap.get(rs.getString("emp_id").trim()));
					} else {
						resigAcceptedBy.append(", "+hmEmployeeNameMap.get(rs.getString("emp_id").trim()));
					}
				
				}
//				System.out.println("effective_id==>"+rs.getString("effective_id")+"==>memberStep==>"+memberStep+"==>flag==>"+flag);
				hmApprovalStatus.put(rs.getString("work_flow_id"), memberStep);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmApprovalStatus", hmApprovalStatus);
			
			StringBuilder sbReason = new StringBuilder();;
			if(flag){
				
				if(effectivetype.equals(WORK_FLOW_RESIGN)) {
					if(resigAcceptedBy == null) {
						resigAcceptedBy = new StringBuilder();
					}
					pst = con.prepareStatement("select * from emp_off_board where off_board_id=? and approved_1 > 0 and approved_1_by > 0");
					pst.setInt(1, uF.parseToInt(getResignId()));
					rs = pst.executeQuery();
					while (rs.next()) {
						String strApproveMemTick = "";
						String strApproveMemReason = "";
						if (uF.parseToInt(rs.getString("approved_1")) == 1){
							/*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
							strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
							
						} else if (uF.parseToInt(rs.getString("approved_1")) == -1){
							/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
							strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Denied\"></i>";
							
						}
						if(rs.getString("approved_1_reason")!=null && !rs.getString("approved_1_reason").trim().equals("") && !rs.getString("approved_1_reason").trim().equalsIgnoreCase("NULL")){
							strApproveMemReason = "[Reason: "+rs.getString("approved_1_reason").trim()+"]";
						}
						
						
						resigAcceptedBy.append(uF.showData(hmEmployeeNameMap.get(rs.getString("approved_1_by").trim()), "-"));
						
						String strUserTypeName = uF.parseToInt(hmEmpUserId.get(rs.getString("approved_1_by"))) > 0 ? " (<strong>"+ uF.showData(hmUserTypeMap.get(hmEmpUserId.get(rs.getString("approved_1_by"))), "") + "</strong>)" : "";
						
						//sbReason.append(strApproveMemTick+uF.showData(hmEmployeeNameMap.get(rs.getString("approved_1_by").trim()), "-")+ strUserTypeName +strApproveMemReason);
						sbReason.append(strUserTypeName+":"+uF.showData(hmEmployeeNameMap.get(rs.getString("approved_1_by").trim()), "-")+strApproveMemReason+" "+strApproveMemTick);
					}	
				} 
			}
			
			
//			System.out.println("java resigAcceptedBy==>"+resigAcceptedBy.toString());
			request.setAttribute("resigAcceptedBy", resigAcceptedBy.toString());
			request.setAttribute("strReason", sbReason.toString());
//			System.out.println("sbReason====>"+sbReason.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getPreviousLevelData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmQuestionBank = CF.getQuestionBank(con, uF);
			if(hmQuestionBank == null) hmQuestionBank = new HashMap<String, String>();
			Map<String, String> hmAnswerTypes = CF.getAnswerTypeMap(con);
			if(hmAnswerTypes == null) hmAnswerTypes = new HashMap<String, String>();
			Map<String, String> hmEmployeeNameMap = new HashMap<String, String>();
			Map<String, String> hmEmployeeCodeMap = new HashMap<String, String>();
			CF.getEmpNameCodeMap(con, null, null, hmEmployeeCodeMap, hmEmployeeNameMap);
			
			StringBuilder sb = new StringBuilder("select * from form_question_details where form_id=? and section_id=? ");
			pst = con.prepareStatement(sb.toString());
			pst.setInt(1, uF.parseToInt(getFormId()));
			pst.setInt(2, uF.parseToInt(currentLevel));
			rs = pst.executeQuery();
//			System.out.println("Get Questions LevelWise pst ===> "+pst);
			Map<String, List<Map<String, String>>> hmSectionQuestion = new HashMap<String, List<Map<String,String>>>();
			while (rs.next()) {
				List<Map<String, String>> alSecQueList = (List<Map<String, String>>) hmSectionQuestion.get(rs.getString("section_id"));
				if(alSecQueList == null) alSecQueList = new ArrayList<Map<String,String>>();
				
				Map<String, String> hmSecQuestion = new HashMap<String, String>();
				hmSecQuestion.put("SECTION_QUEST_ID", rs.getString("form_question_id"));
				hmSecQuestion.put("SECTION_QUEST_FORM_ID", rs.getString("form_id"));
				hmSecQuestion.put("SECTION_QUEST_SECTION_ID", rs.getString("section_id"));
				hmSecQuestion.put("SECTION_QUEST_QUESTION_BANK_ID", rs.getString("question_bank_id"));
				hmSecQuestion.put("SECTION_QUEST_QUESTION_NAME", uF.showData(hmQuestionBank.get(rs.getString("question_bank_id")), ""));
				hmSecQuestion.put("SECTION_QUEST_ANSWER_TYPE_ID", rs.getString("answer_type"));
				hmSecQuestion.put("SECTION_QUEST_ANSWER_TYPE", uF.showData(hmAnswerTypes.get(rs.getString("answer_type")), ""));
				hmSecQuestion.put("SECTION_QUEST_WEIGHTAGE", rs.getString("weightage"));
				hmSecQuestion.put("SECTION_QUEST_ADDED_BY", uF.showData(hmEmployeeNameMap.get(rs.getString("added_by")), ""));
				hmSecQuestion.put("SECTION_QUEST_ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				alSecQueList.add(hmSecQuestion);
				
				hmSectionQuestion.put(rs.getString("section_id"), alSecQueList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmSectionQuestion", hmSectionQuestion);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void insertMarks(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		
		try {
			Map<String, List<Map<String, String>>> hmSectionQuestion = (Map<String, List<Map<String, String>>>) request.getAttribute("hmSectionQuestion");
			if(hmSectionQuestion == null) hmSectionQuestion = new HashMap<String, List<Map<String,String>>>();
			Map<String, List<String>> hmQuestion = (Map<String, List<String>>) request.getAttribute("hmQuestion");
			if(hmQuestion == null) hmQuestion = new HashMap<String, List<String>>();
			
			pst = con.prepareStatement("delete from form_question_answer where emp_id=? and form_id=? and user_id=? and section_id=? and resign_id=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getFormId()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(currentLevel));
			pst.setInt(5, uF.parseToInt(getResignId()));
			pst.execute();
			pst.close();
			
			Iterator<String> it = hmSectionQuestion.keySet().iterator();
			while(it.hasNext()){
				String strSectionId = it.next();
				List<Map<String, String>> alSecQueList = (List<Map<String, String>>) hmSectionQuestion.get(strSectionId);
						
				for (int i = 0; alSecQueList != null && i < alSecQueList.size(); i++) {
					Map<String, String> hmSecQuestion = (Map<String, String>) alSecQueList.get(i);
					List<String> questioninnerList = hmQuestion.get(hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID"));
					
					String weightage = hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE");
					String givenAnswer = null;
					double marks = 0;
					String remark = null;
					String ansComment = null;
					
					if (uF.parseToInt(questioninnerList.get(8)) == 1) {
						ansComment = request.getParameter("anscomment" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						String[] correct = request.getParameterValues("correct" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						remark = request.getParameter("" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						String correctanswer = questioninnerList.get(6);
						for (int k = 0; correct != null && k < correct.length; k++) {
							if (k == 0) {
								givenAnswer = correct[k] + ",";
							} else {
								givenAnswer += correct[k] + ",";
							}
						}
						if (correctanswer != null && givenAnswer != null && givenAnswer.equals(correctanswer)) {
							marks = uF.parseToDouble(weightage);
						}

					} else if (uF.parseToInt(questioninnerList.get(8)) == 2) {
						ansComment = request.getParameter("anscomment" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						String[] correct = request.getParameterValues("correct" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						for (int k = 0; correct != null && k < correct.length; k++) {
							if (k == 0) {
								givenAnswer = correct[k] + ",";
							} else {
								givenAnswer += correct[k] + ",";
							}
						}
						String correctanswer = questioninnerList.get(6);

						if (correctanswer != null && givenAnswer != null && givenAnswer.equals(correctanswer)) {
							marks = uF.parseToDouble(weightage);
						} else {
							marks = 0;
						}

					} else if (uF.parseToInt(questioninnerList.get(8)) == 3) { 
						ansComment = request.getParameter("anscomment" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
//						System.out.println("MArks OF ANSTYPE 3 ===> " + request.getParameter("marks" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9)));
//						System.out.println("ANSTYPE 3 ID ===> " + "marks" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						marks = uF.parseToDouble(request.getParameter("marks" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9)));
						
					} else if (uF.parseToInt(questioninnerList.get(8)) == 4) {
						ansComment = request.getParameter("anscomment" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						givenAnswer = request.getParameter("" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						marks = (uF.parseToDouble(givenAnswer) * uF.parseToDouble(weightage)) / 100;

					} else if (uF.parseToInt(questioninnerList.get(8)) == 5) {
						ansComment = request.getParameter("anscomment" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						givenAnswer = request.getParameter("" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9)) + ",";
						String answer = questioninnerList.get(6);
						if (givenAnswer != null && answer != null && givenAnswer.equals(answer)) {
							marks = uF.parseToDouble(weightage);
						}
						
					} else if (uF.parseToInt(questioninnerList.get(8)) == 6) {
						ansComment = request.getParameter("anscomment" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						givenAnswer = request.getParameter("" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9)) + ",";
						String answer = questioninnerList.get(6);
						if (givenAnswer != null && answer != null && givenAnswer.equals(answer)) {
							marks = uF.parseToDouble(weightage);
						}

					} else if (uF.parseToInt(questioninnerList.get(8)) == 7) {
						ansComment = request.getParameter("anscomment" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						givenAnswer = request.getParameter("" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
//						System.out.println("MArks OF ANSTYPE 7 ===> " + request.getParameter("marks" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9)));
//						System.out.println("ANSTYPE 7 ID ===> " + "marks" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						marks = uF.parseToDouble(request.getParameter("marks" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9)));
						weightage = request.getParameter("outofmarks" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));

					} else if (uF.parseToInt(questioninnerList.get(8)) == 8) {
						ansComment = request.getParameter("anscomment" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						givenAnswer = request.getParameter("correct" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9)) + ",";
						String correctanswer = questioninnerList.get(6);
						if (givenAnswer != null && correctanswer != null && givenAnswer.equals(correctanswer)) {
							marks = uF.parseToDouble(weightage);
						}

					} else if (uF.parseToInt(questioninnerList.get(8)) == 9) {
						ansComment = request.getParameter("anscomment" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						String[] correct = request.getParameterValues("correct" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						for (int k = 0; correct != null && k < correct.length; k++) {
							if (k == 0) {
								givenAnswer = correct[k] + ",";
							} else {
								givenAnswer += correct[k] + ",";
							}
						}
						String correctanswer = questioninnerList.get(6);
//						System.out.println("correctanswer ===> " + correctanswer +" givenAnswer ===> "+givenAnswer);
						if (correctanswer != null && givenAnswer != null && givenAnswer.equals(correctanswer)) {
//							System.out.println("in if correctanswer ===> " + correctanswer +" givenAnswer ===> "+givenAnswer);
							marks = uF.parseToDouble(weightage);
						}
					} else if (uF.parseToInt(questioninnerList.get(8)) == 10) {
						ansComment = request.getParameter("anscomment" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
//						System.out.println("MArks OF ANSTYPE 10 ===> " + request.getParameter("marks" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9)));
//						System.out.println("ANSTYPE 10 ID ===> " + "marks" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						marks = uF.parseToDouble(request.getParameter("marks" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9)));
						String a = request.getParameter("a" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						String b = request.getParameter("b" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						String c = request.getParameter("c" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						String d = request.getParameter("d" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));

						givenAnswer = uF.showData(a, "") + " :_:" + uF.showData(b, "") + " :_:" + uF.showData(c, "") + " :_: " + uF.showData(d, "");

					} else if (uF.parseToInt(questioninnerList.get(8)) == 11) {
						ansComment = request.getParameter("anscomment" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						String rating = request.getParameter("gradewithrating" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						marks = uF.parseToDouble(rating) * uF.parseToDouble(weightage) / 5;

					}else if (uF.parseToInt(questioninnerList.get(8)) == 12) {
						ansComment = request.getParameter("anscomment" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
						givenAnswer = request.getParameter("" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")+"_"+questioninnerList.get(9));
					}
					
					pst = con.prepareStatement("insert into form_question_answer(emp_id,answer,form_id,section_id,question_id," +
							"user_id,attempted_on,remark,marks,weightage,answers_comment,resign_id)values(?,?,?,?, ?,?,?,?, ?,?,?,?)");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
					pst.setString(2, givenAnswer);
					pst.setInt(3, uF.parseToInt(getFormId()));
					pst.setInt(4, uF.parseToInt(currentLevel));
					pst.setInt(5, uF.parseToInt(hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID")));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(8, remark);
					pst.setDouble(9, marks);
					pst.setDouble(10, uF.parseToDouble(weightage));
					pst.setString(11, ansComment);
					pst.setInt(12, uF.parseToInt(getResignId()));
//					System.out.println("pst==>"+pst);
					pst.execute();
					pst.close();
					
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

	public void getanswerTypeMap(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, List<List<String>>> hmQuestionanswerType = new HashMap<String, List<List<String>>>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_answer_type_sub ");
			rs = pst.executeQuery();
			while (rs.next()) {

				List<List<String>> outerList = hmQuestionanswerType.get(rs.getString("answer_type_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("score_label"));
				innerList.add(rs.getString("score"));
				outerList.add(innerList);
				hmQuestionanswerType.put(rs.getString("answer_type_id"), outerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmQuestionanswerType", hmQuestionanswerType);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getQuestionSubType(UtilityFunctions uF) {
		Map<String, List<List<String>>> answertypeSub = new HashMap<String, List<List<String>>>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);

		con = db.makeConnection(con);
		try {
			pst = con.prepareStatement("select * from appraisal_answer_type_sub order by appraisal_answer_type_sub_id");
			rst = pst.executeQuery();
			while (rst.next()) {
				List<List<String>> outerList = answertypeSub.get(rst.getString("answer_type_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();

				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("score"));
				innerList.add(rst.getString("score_label"));
				outerList.add(innerList);
				answertypeSub.put(rst.getString("answer_type_id"), outerList);

			}
			rst.close();
			pst.close();
			
			request.setAttribute("answertypeSub", answertypeSub);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private String getLevelQuestion(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		// boolean flag = false;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmQuestionBank = CF.getQuestionBank(con, uF);
			if(hmQuestionBank == null) hmQuestionBank = new HashMap<String, String>();
			Map<String, String> hmAnswerTypes = CF.getAnswerTypeMap(con);
			if(hmAnswerTypes == null) hmAnswerTypes = new HashMap<String, String>();
			Map<String, String> hmEmployeeNameMap = new HashMap<String, String>();
			Map<String, String> hmEmployeeCodeMap = new HashMap<String, String>();
			CF.getEmpNameCodeMap(con, null, null, hmEmployeeCodeMap, hmEmployeeNameMap);

			Map<String, String> hmSection = new HashMap<String, String>();
			int sectionCnt=0;
			if (currentLevel == null) {
				pst = con.prepareStatement("select * from form_section_details where form_id=? order by form_section_id");
			} else {
				pst = con.prepareStatement("select * from form_section_details where form_id=? and form_section_id=? order by form_section_id");
				pst.setInt(2, uF.parseToInt(currentLevel));
			}
			pst.setInt(1, uF.parseToInt(getFormId()));
			rs = pst.executeQuery();
			String section_level_id = null;
			while (rs.next()) {
				sectionCnt++;
				section_level_id = rs.getString("form_section_id");
				hmSection.put("SECTION_ID", rs.getString("form_section_id"));
				hmSection.put("SECTION_NAME", uF.showData(rs.getString("section_name"), ""));
				hmSection.put("SECTION_SHORT_DESCRIPTION", uF.showData(rs.getString("short_description"), ""));
				hmSection.put("SECTION_LONG_DESCRIPTION", uF.showData(rs.getString("long_description"), ""));
				hmSection.put("SECTION_WEIGHTAGE", uF.showData(rs.getString("weightage"), ""));
				hmSection.put("SECTION_ANSWER_TYPE_ID", uF.showData(rs.getString("answer_type"), ""));
				hmSection.put("SECTION_ANSWER_TYPE", uF.showData(hmAnswerTypes.get(rs.getString("answer_type")), ""));
				hmSection.put("SECTION_FORM_ID", rs.getString("form_id"));
				hmSection.put("SECTION_ADDED_BY", uF.showData(hmEmployeeNameMap.get(rs.getString("added_by")), ""));
				hmSection.put("SECTION_ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				break;
			}
			rs.close();
			pst.close();
			
			if (section_level_id == null) {
				return "update";
			}
			request.setAttribute("hmSection", hmSection);

			setCurrentLevel(section_level_id);
			getCurrentLevelAnswer(con, uF, uF.parseToInt(currentLevel));

			StringBuilder sb = new StringBuilder("select * from form_question_details where form_id=? and section_id=? ");
			pst = con.prepareStatement(sb.toString());
			pst.setInt(1, uF.parseToInt(getFormId()));
			pst.setInt(2, uF.parseToInt(currentLevel));
			rs = pst.executeQuery();
//			System.out.println("Get Questions LevelWise pst ===> "+pst);
			Map<String, List<Map<String, String>>> hmSectionQuestion = new HashMap<String, List<Map<String,String>>>();
			while (rs.next()) {
				List<Map<String, String>> alSecQueList = (List<Map<String, String>>) hmSectionQuestion.get(rs.getString("section_id"));
				if(alSecQueList == null) alSecQueList = new ArrayList<Map<String,String>>();
				
				Map<String, String> hmSecQuestion = new HashMap<String, String>();
				hmSecQuestion.put("SECTION_QUEST_ID", rs.getString("form_question_id"));
				hmSecQuestion.put("SECTION_QUEST_FORM_ID", rs.getString("form_id"));
				hmSecQuestion.put("SECTION_QUEST_SECTION_ID", rs.getString("section_id"));
				hmSecQuestion.put("SECTION_QUEST_QUESTION_BANK_ID", rs.getString("question_bank_id"));
				hmSecQuestion.put("SECTION_QUEST_QUESTION_NAME", uF.showData(hmQuestionBank.get(rs.getString("question_bank_id")), ""));
				hmSecQuestion.put("SECTION_QUEST_ANSWER_TYPE_ID", rs.getString("answer_type"));
				hmSecQuestion.put("SECTION_QUEST_ANSWER_TYPE", uF.showData(hmAnswerTypes.get(rs.getString("answer_type")), ""));
				hmSecQuestion.put("SECTION_QUEST_WEIGHTAGE", rs.getString("weightage"));
				hmSecQuestion.put("SECTION_QUEST_ADDED_BY", uF.showData(hmEmployeeNameMap.get(rs.getString("added_by")), ""));
				hmSecQuestion.put("SECTION_QUEST_ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				alSecQueList.add(hmSecQuestion);
				
				hmSectionQuestion.put(rs.getString("section_id"), alSecQueList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmSectionQuestion", hmSectionQuestion);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "success";
	}

	public void getCurrentLevelAnswer(Connection con, UtilityFunctions uF, int level) {

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement("select * from form_question_answer where emp_id=? and form_id=? and user_id=? and section_id=? and resign_id=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getFormId()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(currentLevel));
			pst.setInt(5, uF.parseToInt(getResignId()));
			rs = pst.executeQuery();
			Map<String, Map<String, String>> questionanswerMp = new HashMap<String, Map<String, String>>();
			while (rs.next()) {

				Map<String, String> innerMp = new HashMap<String, String>();
				innerMp.put("ANSWER", rs.getString("answer"));
				innerMp.put("REMARK", rs.getString("remark"));
				innerMp.put("MARKS", rs.getString("marks"));
				innerMp.put("APP_QUE_ANS_ID", rs.getString("form_question_answer_id"));
				innerMp.put("WEIGHTAGE", rs.getString("weightage"));
				innerMp.put("ANSWERCOMMENT", rs.getString("answers_comment"));
				
				questionanswerMp.put(rs.getString("section_id") + "question" + rs.getString("question_id"), innerMp);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("questionanswerMp", questionanswerMp);
			
			String sectionCount="0";
			pst = con.prepareStatement("select count(distinct(section_id)) as section_id from form_question_answer where emp_id=? and form_id=? and user_id=? and resign_id=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getFormId()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getResignId()));
			rs = pst.executeQuery();
//			System.out.println("pst === > "+pst);
			while (rs.next()) {
				sectionCount = rs.getString("section_id");
			}
			rs.close();
			pst.close();
			
//			System.out.println("sectionCount ===> " + sectionCount);
			request.setAttribute("sectionCount", sectionCount);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
	}
	
	private void getFormQuestionMap(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, List<String>> hmQuestion = new HashMap<String, List<String>>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from question_bank qb, form_question_details fqd where qb.question_bank_id = fqd.question_bank_id and form_id =?");
			pst.setInt(1, uF.parseToInt(getFormId()));
			rs = pst.executeQuery();
			List<List<String>> outerList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("question_bank_id"));
				innerList.add(rs.getString("question_text"));
				innerList.add(rs.getString("option_a"));
				innerList.add(rs.getString("option_b"));
				innerList.add(rs.getString("option_c"));
				innerList.add(rs.getString("option_d"));
				innerList.add(rs.getString("correct_ans"));
				innerList.add(rs.getString("is_add"));
				innerList.add(rs.getString("question_type"));
				innerList.add(rs.getString("form_question_id"));
				
				outerList.add(innerList);
				hmQuestion.put(rs.getString("question_bank_id"), innerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmQuestion", hmQuestion);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public boolean swapLevelId(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		boolean flag = true;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from form_section_details where form_id in (select form_id from " +
					"form_management_details where node_id=? and org_id in (select eod.org_id from employee_personal_details epd, " +
					"employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.emp_id=?)) and form_section_id > ?");
			pst.setInt(1, uF.parseToInt(IConstants.NODE_EXIT_FORM_ID));
			pst.setInt(2, uF.parseToInt(getStrEmpId()));
			pst.setInt(3, uF.parseToInt(getCurrentLevel()));
			rs = pst.executeQuery();
//			System.out.println("pst ====> "+pst);
			while (rs.next()) {
				flag = false;
				setCurrentLevel(rs.getString("form_section_id"));
				break;
			}
			rs.close();
			pst.close();
//			System.out.println("flag ====> "+flag);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}
	
	private void getFormDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmNodes = CF.getNodes(con);
			Map<String, String> hmEmployeeNameMap = new HashMap<String, String>();
			Map<String, String> hmEmployeeCodeMap = new HashMap<String, String>();
			CF.getEmpNameCodeMap(con, null, null, hmEmployeeCodeMap, hmEmployeeNameMap);
			Map<String, String> hmOrg = CF.getOrgName(con);
			
			pst = con.prepareStatement("select * from form_management_details where node_id=? and org_id in (select eod.org_id " +
					"from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id " +
					"and eod.emp_id=?);");
			pst.setInt(1, uF.parseToInt(IConstants.NODE_EXIT_FORM_ID));
			pst.setInt(2, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			Map<String, String> hmForm = new HashMap<String, String>();
			int nFormId = 0;
			while(rs.next()){
				hmForm.put("FORM_ID", rs.getString("form_id"));
				hmForm.put("FORM_NAME", uF.showData(rs.getString("form_name"), ""));
				hmForm.put("FORM_NODE_ID", uF.showData(rs.getString("node_id"), ""));
				hmForm.put("FORM_NODE", uF.showData(hmNodes.get(rs.getString("node_id")), ""));
				hmForm.put("FORM_ORG_ID", uF.showData(rs.getString("org_id"), ""));
				hmForm.put("FORM_ORG_NAME", uF.showData(hmOrg.get(rs.getString("org_id")), ""));
				hmForm.put("FORM_ADDED_BY", uF.showData(hmEmployeeNameMap.get(rs.getString("added_by")), ""));
				hmForm.put("FORM_ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				nFormId = uF.parseToInt(rs.getString("form_id"));
				setFormId(rs.getString("form_id"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmForm", hmForm);
			
			if(nFormId > 0){
				getSectionDetails(con,uF,nFormId);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getSectionDetails(Connection con, UtilityFunctions uF, int nFormId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			Map<String, String> hmAnswerTypes = CF.getAnswerTypeMap(con);
			if(hmAnswerTypes == null) hmAnswerTypes = new HashMap<String, String>();
			
			Map<String, String> hmEmployeeNameMap = new HashMap<String, String>();
			Map<String, String> hmEmployeeCodeMap = new HashMap<String, String>();
			CF.getEmpNameCodeMap(con, null, null, hmEmployeeCodeMap, hmEmployeeNameMap);
			
			pst = con.prepareStatement("select * from form_section_details where form_id=? order by form_section_id");
			pst.setInt(1, nFormId);
			rs = pst.executeQuery();
			List<Map<String, String>> alSection = new ArrayList<Map<String,String>>();
			List<String> alSectionId = new ArrayList<String>();
			while(rs.next()){
				Map<String, String> hmSection = new HashMap<String, String>();
				hmSection.put("SECTION_ID", rs.getString("form_section_id"));
				hmSection.put("SECTION_NAME", uF.showData(rs.getString("section_name"), ""));
				hmSection.put("SECTION_SHORT_DESCRIPTION", uF.showData(rs.getString("short_description"), ""));
				hmSection.put("SECTION_LONG_DESCRIPTION", uF.showData(rs.getString("long_description"), ""));
				hmSection.put("SECTION_WEIGHTAGE", uF.showData(rs.getString("weightage"), ""));
				hmSection.put("SECTION_ANSWER_TYPE_ID", uF.showData(rs.getString("answer_type"), ""));
				hmSection.put("SECTION_ANSWER_TYPE", uF.showData(hmAnswerTypes.get(rs.getString("answer_type")), ""));
				hmSection.put("SECTION_FORM_ID", rs.getString("form_id"));
				hmSection.put("SECTION_ADDED_BY", uF.showData(hmEmployeeNameMap.get(rs.getString("added_by")), ""));
				hmSection.put("SECTION_ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				alSection.add(hmSection);
				
				alSectionId.add(rs.getString("form_section_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("alSection", alSection);
			request.setAttribute("alSectionId", alSectionId);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
	}
	
	private void getEmployeeDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);

		try {
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("select emp_id,off_board_type,entry_date,approved_1_by,emp_fname,emp_mname,emp_lname,emp_reason,approved_1_reason," +
					"approved_2_reason,last_day_date from emp_off_board eob,employee_personal_details epd where epd.emp_per_id=eob.approved_1_by and approved_1=? and approved_2=? and emp_id=?");
			pst.setInt(1, 1);
			pst.setInt(2, 1);
			pst.setInt(3, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			Map<String, String> empMap = new HashMap<String, String>();
			while (rs.next()) {
				empMap.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat()));
				empMap.put("LAST_DAY_DATE", uF.getDateFormat(rs.getString("last_day_date"), DBDATE, CF.getStrReportDateFormat()));
				empMap.put("OFF_BOARD_TYPE", rs.getString("off_board_type"));
				empMap.put("EMP_ID", rs.getString("emp_id"));
				empMap.put("EMP_RESIGN_REASON", uF.parseToHTML(rs.getString("emp_reason")));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				empMap.put("ACCEPTED_BY", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				empMap.put("MANAGER_APPROVE_REASON", rs.getString("approved_1_reason"));
				empMap.put("HR_MANAGER_APPROVE_REASON", rs.getString("approved_2_reason"));
			}
			rs.close();
			pst.close();
			
			CF.getEmpProfileDetail(con, request, session, CF, uF, strSessionUserType, getStrEmpId());
			
			request.setAttribute("empDetailsMp", empMap);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}

	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getResignId() {
		return resignId;
	}

	public void setResignId(String resignId) {
		this.resignId = resignId;
	}

	public String getLevelCount() {
		return levelCount;
	}

	public void setLevelCount(String levelCount) {
		this.levelCount = levelCount;
	}

	public String getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(String currentLevel) {
		this.currentLevel = currentLevel;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

}
