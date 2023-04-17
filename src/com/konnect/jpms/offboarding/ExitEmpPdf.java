package com.konnect.jpms.offboarding;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class ExitEmpPdf implements ServletRequestAware, ServletResponseAware, IStatements{
	
	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	CommonFunctions CF;
	
	private String strEmpId;
	private String resignId;
	private String type;
	
	private String formId;
	
	
	public String execute() throws Exception { 
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return "login";
		
		UtilityFunctions uF = new UtilityFunctions();
		
		getEmployeeDetails(uF);
		getanswerTypeMap(uF);
		getQuestionSubType(uF);
		getFormDetails(uF);
		getFormQuestionMap(uF);
		getApprovalStatus();
		generatePdf(uF);
		
		return "";
	}
	
	private void getApprovalStatus() {

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
			
			pst = con.prepareStatement("select * from work_flow_details where effective_id=? and (effective_type='"+WORK_FLOW_RESIGN+"' or effective_type='"+WORK_FLOW_TERMINATION+"') order by member_position");
			pst.setInt(1, uF.parseToInt(getResignId()));
//			pst.setString(2, effectivetype);
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
						strApproveMemTick = "(Approved)";
					} else if (uF.parseToInt(rs.getString("is_approved")) == -1){
						strApproveMemTick = "(Denied)";
					}
					if(rs.getString("reason")!=null && !rs.getString("reason").trim().equals("") && !rs.getString("reason").trim().equalsIgnoreCase("NULL")){
						strApproveMemReason = "[Reason: "+rs.getString("reason").trim()+"]";
					}
				} else if (uF.parseToInt(rs.getString("is_approved")) == 0){
					flag = true;
				}
				
//				System.out.println("strApproveMemReason==>"+strApproveMemReason);

				if (memberStep == null) {
					memberStep =  strUserTypeName + ": " + hmEmployeeNameMap.get(rs.getString("emp_id").trim())+strApproveMemReason+" "+ strApproveMemTick;
					strMemberPosition = rs.getString("member_position");
					
					
				} else {
					if (strMemberPosition != null && strMemberPosition.equals(rs.getString("member_position"))) {
						memberStep += ", "+ strApproveMemTick  + hmEmployeeNameMap.get(rs.getString("emp_id").trim()) + strUserTypeName + strApproveMemReason;
						
					} else {
						step++;
						strMemberPosition = rs.getString("member_position");
					
						// memberStep+=" ======> "+hmEmployeeNameMap.get(rs.getString("emp_id").trim())+strUserTypeName;
						//memberStep += "<br/><strong>Step " + step + ".</strong> "+ strApproveMemTick + hmEmployeeNameMap.get(rs.getString("emp_id").trim()) + strUserTypeName + strApproveMemReason;
						memberStep += "<br/>"+strUserTypeName +  ": " + hmEmployeeNameMap.get(rs.getString("emp_id").trim()) + strApproveMemReason+" "+ strApproveMemTick;
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
				
			//	if(effectivetype.equals(WORK_FLOW_RESIGN)  || effectivetype.equals(WORK_FLOW_TERMINATION) ) {
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
							strApproveMemTick = "(Approved)";
						} else if (uF.parseToInt(rs.getString("approved_1")) == -1){
							strApproveMemTick = "(Denied)";
						}
						if(rs.getString("approved_1_reason")!=null && !rs.getString("approved_1_reason").trim().equals("") && !rs.getString("approved_1_reason").trim().equalsIgnoreCase("NULL")){
							strApproveMemReason = "[Reason: "+rs.getString("approved_1_reason").trim()+"]";
						}
						
						
						resigAcceptedBy.append(uF.showData(hmEmployeeNameMap.get(rs.getString("approved_1_by").trim()), "-"));
						
						String strUserTypeName = uF.parseToInt(hmEmpUserId.get(rs.getString("approved_1_by"))) > 0 ? " ("+ uF.showData(hmUserTypeMap.get(hmEmpUserId.get(rs.getString("approved_1_by"))), "")+")"  : "";
						
						//sbReason.append(strApproveMemTick+uF.showData(hmEmployeeNameMap.get(rs.getString("approved_1_by").trim()), "-")+ strUserTypeName +strApproveMemReason);
						sbReason.append(strUserTypeName+":"+uF.showData(hmEmployeeNameMap.get(rs.getString("approved_1_by").trim()), "-")+strApproveMemReason+" "+strApproveMemTick);
					}	
				//} 
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
			
			pst = con.prepareStatement("select * from form_question_answer where emp_id=? and form_id=? and resign_id=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getFormId()));
			pst.setInt(3, uF.parseToInt(getResignId()));
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
			pst = con.prepareStatement("select count(distinct(section_id)) as section_id from form_question_answer where emp_id=? and form_id=? and resign_id=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getFormId()));
			pst.setInt(3, uF.parseToInt(getResignId()));
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
			
			Map<String, String> hmAnswerTypes = CF.getAnswerTypeMap(con);
			if(hmAnswerTypes == null) hmAnswerTypes = new HashMap<String, String>();
			Map<String, String> hmNodes = CF.getNodes(con);
			Map<String, String> hmEmployeeNameMap = new HashMap<String, String>();
			Map<String, String> hmEmployeeCodeMap = new HashMap<String, String>();
			CF.getEmpNameCodeMap(con, null, null, hmEmployeeCodeMap, hmEmployeeNameMap);
			Map<String, String> hmOrg = CF.getOrgName(con);
			
			pst = con.prepareStatement("select * from form_management_details where node_id=? and org_id in (select eod.org_id " +
					"from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id " +
					"and eod.emp_id=?);");
			pst.setInt(1, uF.parseToInt(getType()));
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
				
				if(alSectionId.size() > 0){
					String strSectionIds = StringUtils.join(alSectionId.toArray(),",");
					
					Map<String, String> hmQuestionBank = CF.getQuestionBank(con, uF);
					if(hmQuestionBank == null) hmQuestionBank = new HashMap<String, String>();
					
					StringBuilder sb = new StringBuilder("select * from form_question_details where form_id=? and section_id in ("+strSectionIds+") ");
					pst = con.prepareStatement(sb.toString());
					pst.setInt(1, uF.parseToInt(getFormId()));
					rs = pst.executeQuery();
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
	
	private void generatePdf(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		try {

			Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
			if (hmEmpProfile == null) hmEmpProfile = new HashMap<String, String>();
			Map<String, String> empMap = (Map<String, String>) request.getAttribute("empDetailsMp");
			if (empMap == null) empMap = new HashMap<String, String>();
			String probationRemaining = (String) request.getAttribute("PROBATION_REMAINING");
			String noticePeriod = (String) request.getAttribute("NOTICE_PERIOD");
			Map<String, String> hmForm = (Map<String, String>)request.getAttribute("hmForm");
			if(hmForm == null) hmForm = new HashMap<String, String>();
			List<Map<String, String>> alSection = (List<Map<String, String>>)request.getAttribute("alSection");
			if(alSection == null) alSection = new ArrayList<Map<String,String>>();
			Map<String, List<Map<String, String>>> hmSectionQuestion = (Map<String, List<Map<String, String>>>) request.getAttribute("hmSectionQuestion");
			if(hmSectionQuestion == null) hmSectionQuestion = new HashMap<String, List<Map<String,String>>>();
			Map<String, List<String>> hmQuestion = (Map<String, List<String>>) request.getAttribute("hmQuestion");
			if(hmQuestion == null) hmQuestion = new HashMap<String, List<String>>();
			Map<String, Map<String, String>> questionanswerMp = (Map<String, Map<String, String>>) request.getAttribute("questionanswerMp");
			if(questionanswerMp == null) questionanswerMp = new HashMap<String, Map<String, String>>();
			List<String> answerTypeList = new ArrayList<String>();
			Map<String, List<List<String>>> answertypeSub = (Map<String, List<List<String>>>) request.getAttribute("answertypeSub");
			if(answertypeSub == null) answertypeSub = new HashMap<String, List<List<String>>>();
			Map<String, List<List<String>>> hmQuestionanswerType = (Map<String, List<List<String>>>) request.getAttribute("hmQuestionanswerType");
			if(hmQuestionanswerType == null) hmQuestionanswerType = new HashMap<String, List<List<String>>>();
			String resigAcceptedBy = (String) request.getAttribute("resigAcceptedBy");
			Map<String,String> hmApprovalStatus =(Map<String,String>)request.getAttribute("hmApprovalStatus");  
			if(hmApprovalStatus == null) hmApprovalStatus = new HashMap<String,String>();
			String strReason=(String)request.getAttribute("strReason");
			
			Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
			Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
			Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
			Font small = new Font(Font.FontFamily.HELVETICA,7);
			Font smallBold = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD);
			Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	        Document document = new Document(PageSize.A4);
	        PdfWriter.getInstance(document,buffer);
	        document.open();
	        
	        PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);   
			
			//New Row
			PdfPCell row =new PdfPCell(new Paragraph("",small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(6);
		    table.addCell(row);
		    
		    //New Row
		    row =new PdfPCell(new Paragraph(uF.showData((String) hmEmpProfile.get("NAME"), "-")+" ["+uF.showData((String) hmEmpProfile.get("EMPCODE"), "-")+"]",small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(3);
		    table.addCell(row);
		    
		    row =new PdfPCell(new Paragraph("Employee Type:",smallBold));
		    row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    table.addCell(row);
		    
		    row =new PdfPCell(new Paragraph(uF.showData((String) hmEmpProfile.get("EMP_TYPE"), "-"),small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(2);
		    table.addCell(row);
		    
		  //New Row
		    row =new PdfPCell(new Paragraph(uF.showData((String) hmEmpProfile.get("DESIGNATION_NAME"), "-")+" ["+uF.showData((String) hmEmpProfile.get("LEVEL_NAME"), "-")+"] ["+uF.showData((String) hmEmpProfile.get("GRADE_NAME"), "-")+"]",small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(3);
		    table.addCell(row);
		    
		    row =new PdfPCell(new Paragraph("Date of Joining:",smallBold));
		    row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    table.addCell(row);
		    
		    row =new PdfPCell(new Paragraph(uF.showData((String) hmEmpProfile.get("JOINING_DATE"), "-"),small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(2);
		    table.addCell(row);
		    
		  //New Row
		    row =new PdfPCell(new Paragraph(uF.showData((String) hmEmpProfile.get("DEPARTMENT_NAME"), "-")+" ["+uF.showData((String) hmEmpProfile.get("SBU_NAME"), "-")+"] ["+uF.showData((String) hmEmpProfile.get("WLOCATION_NAME"), "-")+"]",small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(3);
		    table.addCell(row);
		  
		    row =new PdfPCell(new Paragraph("Probation Status:",smallBold));
		    row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    table.addCell(row);
		    
		    String strProbation = "";
			if(probationRemaining != null) {
		    	if(uF.parseToInt(probationRemaining) > 0) {
		    		strProbation = probationRemaining+ " days remaining.";
		    	} else {
		    		strProbation = "Probation completed.";
		    	}	
		    } else {
		    	strProbation = "No probation.";
		    }
		    row =new PdfPCell(new Paragraph(strProbation,small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(2);
		    table.addCell(row);
		    
		  //New Row
		    row =new PdfPCell(new Paragraph(uF.showData((String) hmEmpProfile.get("ORG_NAME"), "-"),small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(3);
		    table.addCell(row);
		  
		    row =new PdfPCell(new Paragraph("Notice Period:",smallBold));
		    row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    table.addCell(row);
		    
		    row =new PdfPCell(new Paragraph(uF.showData(noticePeriod, "0")+" days",small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(2);
		    table.addCell(row);
		    
		  //New Row
		    row =new PdfPCell(new Paragraph("",small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(3);
		    table.addCell(row);
		  
		    row =new PdfPCell(new Paragraph("Total Experience:",smallBold));
		    row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    table.addCell(row);
		    
		    row =new PdfPCell(new Paragraph(uF.showData((String) hmEmpProfile.get("TOTAL_EXP"), "-"),small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(2);
		    table.addCell(row);
		    
		  //New Row
		    row =new PdfPCell(new Paragraph("",small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(3);
		    table.addCell(row);
		  
		    row =new PdfPCell(new Paragraph("Exp with Current Org:",smallBold));
		    row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    table.addCell(row);
		    
		    row =new PdfPCell(new Paragraph(uF.showData((String)hmEmpProfile.get("TIME_DURATION"), "-"),small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(2);
		    table.addCell(row);
		    
		  //New Row
		    row =new PdfPCell(new Paragraph("",small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(3);
		    table.addCell(row);
		  
		    row =new PdfPCell(new Paragraph("Education Qualification:",smallBold));
		    row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    table.addCell(row);
		    
		    row =new PdfPCell(new Paragraph(uF.showData((String)request.getAttribute("educationsName"), "-"),small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(2);
		    table.addCell(row);
		    
		  //New Row
		    row =new PdfPCell(new Paragraph("",small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(3);
		    table.addCell(row);
		  
		    row =new PdfPCell(new Paragraph("Skills:",smallBold));
		    row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    table.addCell(row);
		    
		    row =new PdfPCell(new Paragraph(uF.showData((String) hmEmpProfile.get("SKILLS_NAME"), "-"),small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(2);
		    table.addCell(row);
		    
		    //New Row
		    StringBuilder sb = new StringBuilder();
			sb.append("Last Day Date: "+uF.showData(empMap.get("LAST_DAY_DATE"),""));
			sb.append("      Off Board Type: "+uF.showData(empMap.get("OFF_BOARD_TYPE"),""));
			sb.append("      Date of Resignation: "+uF.showData(empMap.get("ENTRY_DATE"),""));
			sb.append("      Resignation Accepted by: "+uF.showData(empMap.get("ACCEPTED_BY"),""));
		    row =new PdfPCell(new Paragraph(sb.toString(),small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.TOP);
		    row.setPadding(2.5f);
		    row.setColspan(6);
		    table.addCell(row);

		    //New Row
		    sb = new StringBuilder();
			sb.append("Resignation Reason: "+uF.showData(empMap.get("EMP_RESIGN_REASON"),""));
		    row =new PdfPCell(new Paragraph(sb.toString(),small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(6);
		    table.addCell(row);
		    
		    
		 if(hmApprovalStatus!=null && hmApprovalStatus.size()>0 && !hmApprovalStatus.isEmpty()){
				Set approvedSet  = hmApprovalStatus.keySet();
				Iterator<String> it = approvedSet.iterator();
				while(it.hasNext()){
					String workFlowId = it.next();
					//New Row
				    sb = new StringBuilder();
					sb.append(uF.showData(hmApprovalStatus.get(workFlowId.trim()),""));
				    row =new PdfPCell(new Paragraph(sb.toString(),small));
				    row.setHorizontalAlignment(Element.ALIGN_LEFT);
			        row.setBorder(Rectangle.NO_BORDER);
				    row.setPadding(2.5f);
				    row.setColspan(6);
				    table.addCell(row);
			}
		  } 
		 
		 if(strReason!=null && !strReason.trim().equals("") && !strReason.trim().equalsIgnoreCase("NULL")) { 
			//New Row
			    sb = new StringBuilder();
				sb.append(uF.showData(strReason,""));
			    row =new PdfPCell(new Paragraph(sb.toString(),small));
			    row.setHorizontalAlignment(Element.ALIGN_LEFT);
		        row.setBorder(Rectangle.NO_BORDER);
			    row.setPadding(2.5f);
			    row.setColspan(6);
			    table.addCell(row);
		 } 
		   /* 
		  //New Row
		    sb = new StringBuilder();
			sb.append("Manager Approval Reason: "+uF.showData(empMap.get("MANAGER_APPROVE_REASON"),""));
		    row =new PdfPCell(new Paragraph(sb.toString(),small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(6);
		    table.addCell(row);
		    
		  //New Row
		    sb = new StringBuilder();
			sb.append("HR Manager Approval Reason: "+uF.showData(empMap.get("HR_MANAGER_APPROVE_REASON"),""));
		    row =new PdfPCell(new Paragraph(sb.toString(),small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(6);
		    table.addCell(row);
		    */
		  //New Row
		    row =new PdfPCell(new Paragraph(uF.showData(hmForm.get("FORM_NAME"),""),smallBold));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.TOP);
		    row.setPadding(2.5f);
		    row.setColspan(6);
		    table.addCell(row);
		    
		    for(int i = 0; i < alSection.size(); i++){
		    	Map<String, String> hmSection = (Map<String, String>) alSection.get(i); 
		    	if(hmSection == null) hmSection = new HashMap<String, String>();
		    	
		    	 String strSectionId = hmSection.get("SECTION_ID");
		    	
		    	//New Row
			    row =new PdfPCell(new Paragraph((i+1)+"."+uF.showData(hmSection.get("SECTION_NAME"), ""),smallBold));
			    row.setHorizontalAlignment(Element.ALIGN_LEFT);
		        row.setBorder(Rectangle.NO_BORDER);
//			    row.setPadding(2.5f);
			    row.setColspan(6);
			    table.addCell(row);
			    
			    //New Row
			    List<Element> al = HTMLWorker.parseToList(new StringReader(uF.showData(hmSection.get("SECTION_SHORT_DESCRIPTION"), "")), null);
				Paragraph pr = new Paragraph("",small);
				pr.addAll(al);
				row =new PdfPCell(pr);
			    row.setHorizontalAlignment(Element.ALIGN_LEFT);
		        row.setBorder(Rectangle.NO_BORDER);
//			    row.setPadding(2.5f);
			    row.setColspan(6);
			    table.addCell(row);
			    
			    //New Row
			    al = HTMLWorker.parseToList(new StringReader(uF.showData(hmSection.get("SECTION_LONG_DESCRIPTION"), "")), null);
				pr = new Paragraph("",small);
				pr.addAll(al);
				row =new PdfPCell(pr);
			    row.setHorizontalAlignment(Element.ALIGN_LEFT);
		        row.setBorder(Rectangle.NO_BORDER);
			    row.setPadding(2.5f);
			    row.setColspan(6);
			    table.addCell(row);
			    
			    //New Row
			    row =new PdfPCell(new Paragraph("  Answer Type : "+uF.showData(hmSection.get("SECTION_ANSWER_TYPE"),""),smallBold));
			    row.setHorizontalAlignment(Element.ALIGN_LEFT);
		        row.setBorder(Rectangle.NO_BORDER);
			    row.setPadding(2.5f);
			    row.setColspan(6);
			    table.addCell(row);
			    
				List<Map<String, String>> alSecQueList = (List<Map<String, String>>) hmSectionQuestion.get(strSectionId);
			    
				for (int j = 0; alSecQueList != null && j < alSecQueList.size(); j++) {
					Map<String, String> hmSecQuestion = (Map<String, String>) alSecQueList.get(j);
					List<String> questioninnerList = hmQuestion.get(hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID"));
					
					Map<String, String> innerMp = questionanswerMp.get(strSectionId + "question" + hmSecQuestion.get("SECTION_QUEST_QUESTION_BANK_ID"));
					if (innerMp == null) innerMp = new HashMap<String, String>();
					
					//New Row
					row =new PdfPCell(new Paragraph("   "+(i + 1)+"."+(j + 1)+"  "+questioninnerList.get(1),small));
				    row.setHorizontalAlignment(Element.ALIGN_LEFT);
			        row.setBorder(Rectangle.NO_BORDER);
				    row.setPadding(2.5f);
				    row.setColspan(6);
				    table.addCell(row);
				    
				    if (uF.parseToInt(questioninnerList.get(8)) == 1) {
				    	if(!answerTypeList.contains("1")){		
							answerTypeList.add("1");
						}
				    	//New Row
						row =new PdfPCell(new Paragraph("        a) "+questioninnerList.get(2),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(3);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("b) "+questioninnerList.get(3),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(3);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("        c) "+questioninnerList.get(4),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(3);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("d) "+questioninnerList.get(5),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(3);
					    table.addCell(row);
					    
					    //New Row
					    String strAns = getAppendData(uF, innerMp);
						row =new PdfPCell(new Paragraph("        Ans: "+strAns,small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("        Comment:\n        "+uF.showData(innerMp.get("ANSWERCOMMENT"),""),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
				    	
				    } else if (uF.parseToInt(questioninnerList.get(8)) == 2) {
				 		if(!answerTypeList.contains("2")){		
				 			answerTypeList.add("2");
						}
				 		
				 		//New Row
						row =new PdfPCell(new Paragraph("        a) "+questioninnerList.get(2),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(3);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("b) "+questioninnerList.get(3),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(3);
					    table.addCell(row);
					    
					  //New Row
						row =new PdfPCell(new Paragraph("        c) "+questioninnerList.get(4),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(3);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("d) "+questioninnerList.get(5),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(3);
					    table.addCell(row);
					    
					    //New Row
					    String strAns = getAppendData(uF, innerMp);
						row =new PdfPCell(new Paragraph("        Ans: "+strAns,small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("        Comment:\n        "+uF.showData(innerMp.get("ANSWERCOMMENT"),""),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
				    } else if (uF.parseToInt(questioninnerList.get(8)) == 3) {
				 		if(!answerTypeList.contains("3")){		
							answerTypeList.add("3");
						}
				 		//New Row
						row =new PdfPCell(new Paragraph("        Ans: "+uF.showData(innerMp.get("MARKS"), "0")+"/"+uF.showData(hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE"), "0"),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
					    //New Row
						row =new PdfPCell(new Paragraph("        Comment:\n        "+uF.showData(innerMp.get("ANSWERCOMMENT"),""),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
				    } else if (uF.parseToInt(questioninnerList.get(8)) == 4) {
				 		if(!answerTypeList.contains("4")){		
							answerTypeList.add("4");
						}
				 		List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
				 		StringBuilder sbOuter = null;
				 		for (int k = 0; k < outer.size(); k++) {
							List<String> inner = outer.get(k);
							if(sbOuter == null){
								sbOuter = new StringBuilder();
								sbOuter.append(inner.get(1));
							} else {
								sbOuter.append(","+inner.get(1));
							}
				 		}
				 		if(sbOuter == null){
				 			sbOuter = new StringBuilder();
				 		}	
				 		//New Row
						row =new PdfPCell(new Paragraph("             "+sbOuter.toString(),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
					    //New Row
						row =new PdfPCell(new Paragraph("        Comment:\n        "+uF.showData(innerMp.get("ANSWERCOMMENT"),""),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
				    } else if (uF.parseToInt(questioninnerList.get(8)) == 5) {
				 		if(!answerTypeList.contains("5")){		
							answerTypeList.add("5");
						}
				 		List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
				 		StringBuilder sbOuter = null;
				 		for (int k = 0; k < outer.size(); k++) {
							List<String> inner = outer.get(k);
							if(sbOuter == null){
								sbOuter = new StringBuilder();
								sbOuter.append(inner.get(1));
							} else {
								sbOuter.append(","+inner.get(1));
							}
				 		}
				 		if(sbOuter == null){
				 			sbOuter = new StringBuilder();
				 		}	
				 		//New Row
						row =new PdfPCell(new Paragraph("             "+sbOuter.toString(),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
					    //New Row
						row =new PdfPCell(new Paragraph("        Comment:\n        "+uF.showData(innerMp.get("ANSWERCOMMENT"),""),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
				    } else if (uF.parseToInt(questioninnerList.get(8)) == 6) {
				 		if(!answerTypeList.contains("6")){		
							answerTypeList.add("6");
						}
				 		List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
				 		StringBuilder sbOuter = null;
				 		for (int k = 0; k < outer.size(); k++) {
							List<String> inner = outer.get(k);
							if(sbOuter == null){
								sbOuter = new StringBuilder();
								sbOuter.append(inner.get(1));
							} else {
								sbOuter.append(","+inner.get(1));
							}
				 		}
				 		if(sbOuter == null){
				 			sbOuter = new StringBuilder();
				 		}	
				 		//New Row
						row =new PdfPCell(new Paragraph("             "+sbOuter.toString(),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
					    //New Row
						row =new PdfPCell(new Paragraph("        Comment:\n        "+uF.showData(innerMp.get("ANSWERCOMMENT"),""),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
				    } else if (uF.parseToInt(questioninnerList.get(8)) == 7) {
				 		if(!answerTypeList.contains("7")){		
							answerTypeList.add("7");
						}
				 		
				 		//New Row
						row =new PdfPCell(new Paragraph("        Ans: "+uF.showData(innerMp.get("MARKS"), "0")+"/"+uF.showData(hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE"), "0"),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
					  //New Row
						row =new PdfPCell(new Paragraph("        "+uF.showData(innerMp.get("ANSWER"), ""),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
					    //New Row
						row =new PdfPCell(new Paragraph("        Comment:\n        "+uF.showData(innerMp.get("ANSWERCOMMENT"),""),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
				 	
				    } else if (uF.parseToInt(questioninnerList.get(8)) == 8) {
				 		if(!answerTypeList.contains("8")){		
							answerTypeList.add("8");
						}
				 		//New Row
						row =new PdfPCell(new Paragraph("        a) "+questioninnerList.get(2),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(3);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("b) "+questioninnerList.get(3),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(3);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("        c) "+questioninnerList.get(4),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(3);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("d) "+questioninnerList.get(5),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(3);
					    table.addCell(row);

					    //New Row
					    String strAns = getAppendData(uF, innerMp);
						row =new PdfPCell(new Paragraph("        Ans: "+strAns,small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("        Comment:\n        "+uF.showData(innerMp.get("ANSWERCOMMENT"),""),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
				    } else if (uF.parseToInt(questioninnerList.get(8)) == 9) {
				 		if(!answerTypeList.contains("9")){		
							answerTypeList.add("9");
						}
				 		
				 		//New Row
						row =new PdfPCell(new Paragraph("        a) "+questioninnerList.get(2),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(3);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("b) "+questioninnerList.get(3),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(3);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("        c) "+questioninnerList.get(4),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(3);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("d) "+questioninnerList.get(5),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(3);
					    table.addCell(row);
					    
					    //New Row
					    String strAns = getAppendData(uF, innerMp);
						row =new PdfPCell(new Paragraph("        Ans: "+strAns,small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("        Comment:\n        "+uF.showData(innerMp.get("ANSWERCOMMENT"),""),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
				    } else if (uF.parseToInt(questioninnerList.get(8)) == 10) {
				 		if(!answerTypeList.contains("10")){		
							answerTypeList.add("10");
						} 
				 		String[] a = null;
		 				if (innerMp.get("ANSWER") != null) {
		 					a = innerMp.get("ANSWER").split(":_:");
		 				}
		 				
		 				//New Row
						row =new PdfPCell(new Paragraph("        a) "+a[0],small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("        b) "+a[1],small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("        c) "+a[2],small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("        d) "+a[3],small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
					    
					    //New Row
					    String strAns = getAppendData(uF, innerMp);
						row =new PdfPCell(new Paragraph("        Ans: "+strAns+"   "+uF.showData(innerMp.get("MARKS"), "0")+"/"+uF.showData(hmSecQuestion.get("SECTION_QUEST_WEIGHTAGE"), "0"),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("        Comment:\n        "+uF.showData(innerMp.get("ANSWERCOMMENT"),""),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
				    } else if (uF.parseToInt(questioninnerList.get(8)) == 11) {
				 		if(!answerTypeList.contains("11")){		
							answerTypeList.add("11");
						}
				 		double weightage = uF.parseToInt(innerMp.get("WEIGHTAGE"));
						double starweight = weightage*20/100;  
						 //New Row
						row =new PdfPCell(new Paragraph("        Ans: "+(innerMp.get("MARKS") != null ? uF.parseToDouble(innerMp.get("MARKS")) / starweight + "" : "0")+"/5",small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("        Comment:\n        "+uF.showData(innerMp.get("ANSWERCOMMENT"),""),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
				    } else if (uF.parseToInt(questioninnerList.get(8)) == 12) { 
				 		if(!answerTypeList.contains("12")){		
							answerTypeList.add("12");
						}   
				 		//New Row
						row =new PdfPCell(new Paragraph("        Ans: "+uF.showData(innerMp.get("ANSWER"),""),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
					    
					    //New Row
						row =new PdfPCell(new Paragraph("        Comment:\n        "+uF.showData(innerMp.get("ANSWERCOMMENT"),""),small));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);
				    }
				}
		    }
		    
		    //New Row
			row =new PdfPCell(new Paragraph("",small));
		    row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
		    row.setPadding(2.5f);
		    row.setColspan(6);
		    row.setRowspan(2);
		    table.addCell(row);
		    
		    if(answerTypeList.contains("4") || answerTypeList.contains("5") || answerTypeList.contains("6")) {
		    	//New Row
				row =new PdfPCell(new Paragraph("Answer Type Structure",smallBold));
			    row.setHorizontalAlignment(Element.ALIGN_LEFT);
		        row.setBorder(Rectangle.TOP);
			    row.setPadding(2.5f);
			    row.setColspan(6);
			    table.addCell(row);
			    
			    int k = 1;
				for (int i = 0; i < answerTypeList.size(); i++) {
					List<List<String>> outerList = hmQuestionanswerType.get(answerTypeList.get(i));
					
					for (int j = 0; outerList != null && j < outerList.size(); j++) {
						List<String> innerlist = (List<String>) outerList.get(j);
						String str="";
						if (j == 0) {
							k++;
							str = ""+k;
						}
						//New Row
						row =new PdfPCell(new Paragraph("    "+str+"). "+innerlist.get(0)+" - "+innerlist.get(1),smallBold));
					    row.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row.setBorder(Rectangle.NO_BORDER);
					    row.setPadding(2.5f);
					    row.setColspan(6);
					    table.addCell(row);						
					}
				}
		    }
		    
		    document.add(table);
	        
	        document.close();
		    
			String strFileName = "";
			if(getType()!=null && getType().equals(NODE_EXIT_FORM_ID)){
				strFileName = "feedback_"+getStrEmpId();
			} else if(getType()!=null && getType().equals(NODE_CLEARANCE_FORM_ID)){
				strFileName = "clearance_"+getStrEmpId();
			}
			
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename="+strFileName+".pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}

	}


	private String getAppendData(UtilityFunctions uF, Map<String, String> innerMp) {
		StringBuilder strAns = null;
		try {
			if (innerMp.get("ANSWER") != null) {
				if (innerMp.get("ANSWER").contains("a")) {
					if (strAns == null) {
						strAns = new StringBuilder();
						strAns.append("a");
					} else {
						strAns.append(", a");
					}
				}
				if (innerMp.get("ANSWER").contains("b")) {
					if (strAns == null) {
						strAns = new StringBuilder();
						strAns.append("b");
					} else {
						strAns.append(", b");
					}
				}
				if (innerMp.get("ANSWER").contains("c")) {
					if (strAns == null) {
						strAns = new StringBuilder();
						strAns.append("c");
					} else {
						strAns.append(", c");
					}
				}
				if (innerMp.get("ANSWER").contains("d")) {
					if (strAns == null) {
						strAns = new StringBuilder();
						strAns.append("d");
					} else {
						strAns.append(", d");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strAns.toString();
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
			pst.close();
			rs.close();
			
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
	HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response=response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	
}
