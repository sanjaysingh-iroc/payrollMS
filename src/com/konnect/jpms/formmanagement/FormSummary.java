package com.konnect.jpms.formmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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

public class FormSummary extends ActionSupport implements ServletRequestAware, IStatements{

	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	private String strOrg;
	private String formId;
	
	private String sectionTitle;
	private String shortDesrciption;
	private String longDesrciption;
	private String sectionWeightage;
	private String ansType;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	public String execute(){
		session= request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		 
		request.setAttribute(PAGE, "/jsp/formmanagement/FormSummary.jsp");
		request.setAttribute(TITLE, "Form Summary");
		UtilityFunctions uF = new UtilityFunctions();
		
		String operation = (String) request.getParameter("operation");
		String sectionId = (String) request.getParameter("sectionId");
		String questionId = (String) request.getParameter("questionId");
				
		if(uF.parseToInt(getFormId()) > 0 && uF.parseToInt(sectionId) > 0 && operation !=null && operation.trim().equalsIgnoreCase("QU")){
			addQuestion(uF,sectionId);
		} else if(uF.parseToInt(getFormId()) > 0 && uF.parseToInt(questionId) > 0 && operation !=null && operation.trim().equalsIgnoreCase("D")){
			deleteQuestion(uF,questionId);
		} else if(uF.parseToInt(getFormId()) > 0 && uF.parseToInt(sectionId) > 0 && operation !=null && operation.trim().equalsIgnoreCase("D")){
			deleteSection(uF,sectionId);
		} else if(uF.parseToInt(getFormId()) > 0 && operation !=null && operation.trim().equalsIgnoreCase("U")){
			addSection(uF);
		}
		
		getFormDetails(uF);
		getSectionDetails(uF);
		getAnsType(uF);
		
		return SUCCESS;
	} 
	
	private void addQuestion(UtilityFunctions uF, String sectionId) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			String[] question = request.getParameterValues("question");
			String[] orientt = request.getParameterValues("orientt");
			String[] addFlag = request.getParameterValues("status");
			String[] weightage = request.getParameterValues("weightage");
			String[] optiona = request.getParameterValues("optiona");
			String[] optionb = request.getParameterValues("optionb");
			String[] optionc = request.getParameterValues("optionc");
			String[] optiond = request.getParameterValues("optiond");
			
			int questionLength = question!=null ? question.length : 0;
			for (int i = 0; i < questionLength; i++) {
				
				String[] correct = request.getParameterValues("correct" + orientt[i]);
				StringBuilder option = new StringBuilder();

				for (int ab = 0; correct != null && ab < correct.length; ab++) {
					option.append(correct[ab] + ",");
				}

				pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
				pst.setString(1, question[i]);
				pst.setString(2, (optiona != null && optiona.length > i ? optiona[i]: ""));
				pst.setString(3, (optionb != null && optionb.length > i ? optionb[i]: ""));
				pst.setString(4, (optionc != null && optionc.length > i ? optionc[i]: ""));
				pst.setString(5, (optiond != null && optiond.length > i ? optiond[i]: ""));
				pst.setString(6, option.toString());
				pst.setBoolean(7, uF.parseToBoolean(addFlag[i]));
				pst.setInt(8, uF.parseToInt(ansType));
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("select max(question_bank_id) from question_bank");
				rs = pst.executeQuery();
				int question_id = 0;
				while (rs.next()) {
					question_id = rs.getInt(1);
				}
				rs.close();
				pst.close();
				
				if(question_id > 0){
					pst = con.prepareStatement("insert into form_question_details (form_id,section_id,question_bank_id," +
							"answer_type,weightage,added_by,entry_date) values(?,?,?,?, ?,?,?)");
					pst.setInt(1, uF.parseToInt(getFormId()));
					pst.setInt(2, uF.parseToInt(sectionId));
					pst.setInt(3, question_id);
					pst.setInt(4, uF.parseToInt(getAnsType()));
					pst.setDouble(5, uF.parseToDouble(weightage[i]));
					pst.setInt(6, uF.parseToInt((String)session.getAttribute(EMPID)));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
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

	private void deleteQuestion(UtilityFunctions uF, String questionId) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from form_question_details where form_id=? and form_question_id=?");
			pst.setInt(1, uF.parseToInt(getFormId()));
			pst.setInt(2, uF.parseToInt(questionId));
			pst.execute();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void deleteSection(UtilityFunctions uF, String sectionId) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			con.setAutoCommit(false);	
			
			pst = con.prepareStatement("delete from form_section_details where form_id=? and form_section_id=?");
			pst.setInt(1, uF.parseToInt(getFormId()));
			pst.setInt(2, uF.parseToInt(sectionId));
			int y = pst.executeUpdate();
			pst.close();
			
			if(y > 0){
				pst = con.prepareStatement("delete from form_question_details where form_id=? and section_id=?");
				pst.setInt(1, uF.parseToInt(getFormId()));
				pst.setInt(2, uF.parseToInt(sectionId));
				pst.execute();
				pst.close();
			}
			con.commit();			
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void addSection(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			if(uF.parseToInt(getFormId()) > 0){
				insertSectionDetails(con,uF);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void insertSectionDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("insert into form_section_details (section_name,short_description,long_description,weightage," +
					"answer_type,form_id,added_by,entry_date) values(?,?,?,?, ?,?,?,?)");
			pst.setString(1, getSectionTitle());
			pst.setString(2, getShortDesrciption());
			pst.setString(3, getLongDesrciption());
			pst.setDouble(4, uF.parseToDouble(getSectionWeightage()));
			pst.setInt(5, uF.parseToInt(getAnsType()));
			pst.setInt(6, uF.parseToInt(getFormId()));
			pst.setInt(7, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0 ){
				int nSectionId = 0;
				pst = con.prepareStatement("select max(form_section_id) as form_section_id from form_section_details");
				rs = pst.executeQuery();
				while (rs.next()) {
					nSectionId = uF.parseToInt(rs.getString("form_section_id"));
				}
				rs.close();
				pst.close();
				
				if(nSectionId > 0){
					String[] question = request.getParameterValues("question");
					String[] orientt = request.getParameterValues("orientt");
					String[] addFlag = request.getParameterValues("status");
					String[] weightage = request.getParameterValues("weightage");
					String[] optiona = request.getParameterValues("optiona");
					String[] optionb = request.getParameterValues("optionb");
					String[] optionc = request.getParameterValues("optionc");
					String[] optiond = request.getParameterValues("optiond");
					
					
					int questionLength = question!=null ? question.length : 0;
					for (int i = 0; i < questionLength; i++) {
						
						String[] correct = request.getParameterValues("correct" + orientt[i]);
						StringBuilder option = new StringBuilder();

						for (int ab = 0; correct != null && ab < correct.length; ab++) {
							option.append(correct[ab] + ",");
						}

						pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
						pst.setString(1, question[i]);
						pst.setString(2, (optiona != null && optiona.length > i ? optiona[i]: ""));
						pst.setString(3, (optionb != null && optionb.length > i ? optionb[i]: ""));
						pst.setString(4, (optionc != null && optionc.length > i ? optionc[i]: ""));
						pst.setString(5, (optiond != null && optiond.length > i ? optiond[i]: ""));
						pst.setString(6, option.toString());
						pst.setBoolean(7, uF.parseToBoolean(addFlag[i]));
						pst.setInt(8, uF.parseToInt(ansType));
						pst.execute();
						pst.close();
						
						pst = con.prepareStatement("select max(question_bank_id) from question_bank");
						rs = pst.executeQuery();
						int question_id = 0;
						while (rs.next()) {
							question_id = rs.getInt(1);
						}
						rs.close();
						pst.close();
						
						if(question_id > 0){
							pst = con.prepareStatement("insert into form_question_details (form_id,section_id,question_bank_id," +
									"answer_type,weightage,added_by,entry_date) values(?,?,?,?, ?,?,?)");
							pst.setInt(1, uF.parseToInt(getFormId()));
							pst.setInt(2, nSectionId);
							pst.setInt(3, question_id);
							pst.setInt(4, uF.parseToInt(getAnsType()));
							pst.setDouble(5, uF.parseToDouble(weightage[i]));
							pst.setInt(6, uF.parseToInt((String)session.getAttribute(EMPID)));
							pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.execute();
							pst.close();
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
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
				System.out.println("pst==>"+pst);
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

	private void getAnsType(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
			StringBuilder sb = new StringBuilder("");

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_answer_type");
			rs = pst.executeQuery();
			while (rs.next()) {
				if (uF.parseToInt(rs.getString("appraisal_answer_type_id")) == 9) {
					sb.append("<option value=\""+ rs.getString("appraisal_answer_type_id")+ "\" selected>"+ rs.getString("appraisal_answer_type_name")+ "</option>");
				} else {
					sb.append("<option value=\""+ rs.getString("appraisal_answer_type_id") + "\">"+ rs.getString("appraisal_answer_type_name")+ "</option>");
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("anstype", sb.toString());

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
		this.request=request;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

	public void setSectionTitle(String sectionTitle) {
		this.sectionTitle = sectionTitle;
	}

	public String getShortDesrciption() {
		return shortDesrciption;
	}

	public void setShortDesrciption(String shortDesrciption) {
		this.shortDesrciption = shortDesrciption;
	}

	public String getLongDesrciption() {
		return longDesrciption;
	}

	public void setLongDesrciption(String longDesrciption) {
		this.longDesrciption = longDesrciption;
	}

	public String getSectionWeightage() {
		return sectionWeightage;
	}

	public void setSectionWeightage(String sectionWeightage) {
		this.sectionWeightage = sectionWeightage;
	}

	public String getAnsType() {
		return ansType;
	}

	public void setAnsType(String ansType) {
		this.ansType = ansType;
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