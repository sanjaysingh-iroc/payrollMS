package com.konnect.jpms.formmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EditSection extends ActionSupport implements ServletRequestAware, IStatements{

	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	
	
	String formId;
	String sectionId;
	String totalWeightage;
	
	String sectionTitle;
	String shortDesrciption;
	String longDesrciption;
	String sectionWeightage;
	String ansType;
	
	String userscreen;
	String navigationId;
	String toPage;

	public String execute(){
		session= request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		 
		String operation = (String) request.getParameter("operation");
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(uF.parseToInt(getFormId()) > 0 && operation !=null && operation.trim().equalsIgnoreCase("U")){
			editSection(uF);
			return SUCCESS;
		}
		
		getSectionDetails(uF);
		getAnsType(uF);
		
		return loadAddFormList(uF);
	} 
	
	

	private void editSection(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update form_section_details set section_name=?,short_description=?,long_description=?" +
					",weightage=?,answer_type=?,added_by=?,entry_date=? where form_id=? and form_section_id=?");
			pst.setString(1, getSectionTitle());
			pst.setString(2, getShortDesrciption());
			pst.setString(3, getLongDesrciption());
			pst.setDouble(4, uF.parseToDouble(getSectionWeightage()));
			pst.setInt(5, uF.parseToInt(getAnsType()));
			pst.setInt(6, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(8, uF.parseToInt(getFormId()));
			pst.setInt(9, uF.parseToInt(getSectionId()));
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
			
			pst = con.prepareStatement("select * from form_section_details where form_id=? and form_section_id=?");
			pst.setInt(1, uF.parseToInt(getFormId()));
			pst.setInt(2, uF.parseToInt(getSectionId()));
			rs = pst.executeQuery();
			Map<String, String> hmSection = new HashMap<String, String>();
			while(rs.next()){
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
				
				request.setAttribute("answerType", rs.getString("answer_type"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmSection", hmSection);
			
			pst = con.prepareStatement("select * from form_question_details where form_id=? and section_id=?");
			pst.setInt(1, uF.parseToInt(getFormId()));
			pst.setInt(2, uF.parseToInt(getSectionId()));
			rs = pst.executeQuery();
			boolean ansFlag = false;
			while(rs.next()){
				ansFlag = true;
			}
			request.setAttribute("ansFlag", ""+ansFlag);
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

	public String getTotalWeightage() {
		return totalWeightage;
	}

	public void setTotalWeightage(String totalWeightage) {
		this.totalWeightage = totalWeightage;
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