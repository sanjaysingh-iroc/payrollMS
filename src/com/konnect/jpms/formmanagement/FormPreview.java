package com.konnect.jpms.formmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class FormPreview extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strSessionUserTypeID;
	CommonFunctions CF;
	
	String formId;
	String strOrg;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strSessionUserTypeID = (String) session.getAttribute(USERTYPEID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		getFormDetails(uF);
		getSectionDetails(uF);
		getFormQuestionMap(uF);
		getQuestionSubType(uF);
		return LOAD;
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
	
	private void getSectionDetails(UtilityFunctions uF) {
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
			
			pst = con.prepareStatement("select * from form_section_details where form_id=? order by form_section_id");
			pst.setInt(1, uF.parseToInt(getFormId()));
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
			
			if(alSectionId.size() > 0){
				String strSectionIds = StringUtils.join(alSectionId.toArray(),",");
				
				Map<String, String> hmQuestionBank = CF.getQuestionBank(con, uF);
				if(hmQuestionBank == null) hmQuestionBank = new HashMap<String, String>();
				
				pst = con.prepareStatement("select * from form_question_details where form_id=? and section_id in ("+strSectionIds+") order by section_id; ");
				pst.setInt(1, uF.parseToInt(getFormId()));
				rs = pst.executeQuery();
				Map<String, List<Map<String, String>>> hmSectionQuestion = new HashMap<String, List<Map<String,String>>>();
				while(rs.next()){
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
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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
			
			pst = con.prepareStatement("select * from form_management_details where form_id=?");
			pst.setInt(1, uF.parseToInt(getFormId()));
			rs = pst.executeQuery();
			Map<String, String> hmForm = new HashMap<String, String>();
			while(rs.next()){
				hmForm.put("FORM_ID", rs.getString("form_id"));
				hmForm.put("FORM_NAME", uF.showData(rs.getString("form_name"), ""));
				hmForm.put("FORM_NODE_ID", uF.showData(rs.getString("node_id"), ""));
				hmForm.put("FORM_NODE", uF.showData(hmNodes.get(rs.getString("node_id")), ""));
				hmForm.put("FORM_ORG_ID", uF.showData(rs.getString("org_id"), ""));
				hmForm.put("FORM_ORG_NAME", uF.showData(hmOrg.get(rs.getString("org_id")), ""));
				hmForm.put("FORM_ADDED_BY", uF.showData(hmEmployeeNameMap.get(rs.getString("added_by")), ""));
				hmForm.put("FORM_ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				setStrOrg(rs.getString("org_id"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmForm", hmForm);

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

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}
	
}
