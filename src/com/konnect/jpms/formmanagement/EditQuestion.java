package com.konnect.jpms.formmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EditQuestion extends ActionSupport implements ServletRequestAware, IStatements{

	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	String formId;
	String sectionId;
	String sectionQuestId;
	String totalQuestWeightage;
	String questBankId;

	String userscreen;
	String navigationId;
	String toPage;

	public String execute(){
		session= request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		 
		String operation = (String) request.getParameter("operation");
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(uF.parseToInt(getFormId()) > 0 && uF.parseToInt(getSectionId()) > 0 && uF.parseToInt(getSectionQuestId()) > 0 && uF.parseToInt(getQuestBankId()) > 0 && operation !=null && operation.trim().equalsIgnoreCase("U")){
			editQuestion(uF);
			return SUCCESS;
		}
		getQuestionDetails(uF);
		
		return loadAddFormList(uF);
	} 

	private void editQuestion(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			String question = (String)request.getParameter("question");
			String orientt = (String)request.getParameter("orientt");
			String addFlag = (String)request.getParameter("status");
			String weightage = (String)request.getParameter("weightage");
			String optiona = (String)request.getParameter("optiona");
			String optionb = (String)request.getParameter("optionb");
			String optionc = (String)request.getParameter("optionc");
			String optiond = (String)request.getParameter("optiond");
			
			String[] correct = request.getParameterValues("correct" + orientt);
			StringBuilder option = new StringBuilder();

			for (int ab = 0; correct != null && ab < correct.length; ab++) {
				option.append(correct[ab] + ",");
			}
			
			pst = con.prepareStatement("update question_bank set question_text=?,option_a=?,option_b=?,option_c=?,option_d=?," +
					"correct_ans=?,is_add=? where question_bank_id=?");
			pst.setString(1, question);
			pst.setString(2, (optiona != null && !optiona.trim().equals("") ? optiona : ""));
			pst.setString(3, (optionb != null && !optionb.trim().equals("") ? optionb : ""));
			pst.setString(4, (optionc != null && !optionc.trim().equals("") ? optionc : ""));
			pst.setString(5, (optiond != null && !optiond.trim().equals("") ? optiond : ""));
			pst.setString(6, option.toString());
			pst.setBoolean(7, uF.parseToBoolean(addFlag));
			pst.setInt(8, uF.parseToInt(getQuestBankId()));
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0){
				pst = con.prepareStatement("update form_question_details set weightage=?,added_by=?,entry_date=?" +
						" where form_id=? and section_id=? and question_bank_id=? and form_question_id=?");
				pst.setDouble(1, uF.parseToDouble(weightage));
				pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(4, uF.parseToInt(getFormId()));
				pst.setInt(5, uF.parseToInt(getSectionId()));
				pst.setInt(6, uF.parseToInt(getQuestBankId()));
				pst.setInt(7, uF.parseToInt(getSectionQuestId()));
				pst.execute();
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

	private void getQuestionDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmAnswerTypes = CF.getAnswerTypeMap(con);
			if(hmAnswerTypes == null) hmAnswerTypes = new HashMap<String, String>();
			
			Map<String, String> hmEmployeeNameMap = new HashMap<String, String>();
			Map<String, String> hmEmployeeCodeMap = new HashMap<String, String>();
			CF.getEmpNameCodeMap(con, null, null, hmEmployeeCodeMap, hmEmployeeNameMap);
				
			Map<String, String> hmQuestionBank = CF.getQuestionBank(con, uF);
			if(hmQuestionBank == null) hmQuestionBank = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from form_question_details where form_id=? and section_id=? and form_question_id=?");
			pst.setInt(1, uF.parseToInt(getFormId()));
			pst.setInt(2, uF.parseToInt(getSectionId()));
			pst.setInt(3, uF.parseToInt(getSectionQuestId()));
			rs = pst.executeQuery();
			Map<String, String> hmSecQuestion = new HashMap<String, String>();
			int getQueid = 0;
			while(rs.next()){
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
				
				getQueid = uF.parseToInt(rs.getString("question_bank_id"));		
				
				setQuestBankId(rs.getString("question_bank_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmSecQuestion", hmSecQuestion);
			
			
			pst = con.prepareStatement("select * from question_bank where question_bank_id =?");
			pst.setInt(1, getQueid);
			rs = pst.executeQuery();
			List<String> queList = new ArrayList<String>();
			String quetitle="";
			String quekey="";
			while (rs.next()) {
				queList.add(rs.getString("question_bank_id"));
				queList.add(rs.getString("question_text"));
				queList.add(rs.getString("option_a"));
				queList.add(rs.getString("option_b"));
				queList.add(rs.getString("option_c"));
				queList.add(rs.getString("option_d"));
				queList.add(rs.getString("correct_ans"));
				queList.add(rs.getString("question_type"));
				quekey = rs.getString("question_bank_id");
				quetitle = rs.getString("question_text");
			}
			rs.close();
			pst.close();
			
//			System.out.println("option===="+sb.toString());
			request.setAttribute("queList", queList);
			request.setAttribute("quekey", quekey);
			request.setAttribute("quetitle", quetitle);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private String loadAddFormList(UtilityFunctions uF) {
		
		return LOAD;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getSectionQuestId() {
		return sectionQuestId;
	}

	public void setSectionQuestId(String sectionQuestId) {
		this.sectionQuestId = sectionQuestId;
	}

	public String getTotalQuestWeightage() {
		return totalQuestWeightage;
	}

	public void setTotalQuestWeightage(String totalQuestWeightage) {
		this.totalQuestWeightage = totalQuestWeightage;
	}

	public String getQuestBankId() {
		return questBankId;
	}

	public void setQuestBankId(String questBankId) {
		this.questBankId = questBankId;
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