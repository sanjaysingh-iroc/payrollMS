package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.performance.FillAnswerType;
import com.konnect.jpms.performance.FillAttribute;
import com.konnect.jpms.performance.FillFrequency;
import com.konnect.jpms.performance.FillOrientation;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class EditLearningFeedbackQuestion implements ServletRequestAware, SessionAware, IStatements {
	Map session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;

	private String ID;
	private String operation;
	private String step;
	private String queID;
	private String queAnstype;
//	String totWeightage;
	private String queno;
	private String fromPage;
		public String execute() {
		strUserType = (String) session.get(USERTYPE);
		strSessionEmpId = (String) session.get(EMPID);

		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return "login";

		request.setAttribute(PAGE, "/jsp/training/EditLearningFeedbackQuestion.jsp");
		request.setAttribute(TITLE, "Edit Question");

//		System.out.println("levelID====>"+levelID);
		String submit = request.getParameter("submit");
//		System.out.println("subsectionID =====> " + subsectionID);
			if (submit != null && submit.equals("Save")) {
				editFeedbackQuestion();
				return "load";
			} else {
//				System.out.println("IN AppSystem 2 getQuestions ......... ");
				getQuestions();
				getAnsType();
				return "success";
			}

		
	}

	
private void getAnsType() {

	Connection con = null;
	PreparedStatement pst = null;
	Database db = new Database();
	db.setRequest(request);
	ResultSet rs = null;
	UtilityFunctions uF = new UtilityFunctions();

	try {
		con = db.makeConnection(con);
		
		StringBuilder sb = new StringBuilder("");
		
		pst = con.prepareStatement("select * from training_answer_type");
		rs = pst.executeQuery();
		while (rs.next()) {
			if (uF.parseToInt(rs.getString("answer_type")) == 9) {
				sb.append("<option value=\"" + rs.getString("answer_type") + "\" selected>"
						+ rs.getString("answer_type_name") + "</option>");
			} else {
				sb.append("<option value=\"" + rs.getString("answer_type") + "\">"
						+ rs.getString("answer_type_name") + "</option>");
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
	

	private void editFeedbackQuestion() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		String questionID = request.getParameter("questionID");
//		String weightage = request.getParameter("weightage");
		String question = request.getParameter("question");
		String ansType = request.getParameter("ansType");
		
		String addFlag = request.getParameter("status");
		String optiona = request.getParameter("optiona");
		String optionb = request.getParameter("optionb");
		String optionc = request.getParameter("optionc");
		String optiond = request.getParameter("optiond");
		String orientt = request.getParameter("orientt");
		String isAdd = request.getParameter("addFlag");
		String[] correct = request.getParameterValues("correct"+ orientt);
		StringBuilder option = new StringBuilder();

		for (int ab = 0; correct != null && ab < correct.length; ab++) {
			option.append(correct[ab] + ",");
		}
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update learning_plan_question_bank set learning_plan_question_text=?,weightage=?,option_a=?," +
					"option_b=?,option_c=?,option_d=?,correct_ans=?,answer_type=?,is_add = ? where learning_plan_question_bank_id=?");
			pst.setString(1, question);
			pst.setDouble(2, uF.parseToDouble("0"));
			pst.setString(3, optiona);
			pst.setString(4, optionb);
			pst.setString(5, optionc);
			pst.setString(6, optiond);
			pst.setString(7, option.toString());
			pst.setInt(8, uF.parseToInt(ansType));
			pst.setBoolean(9, uF.parseToBoolean(isAdd));
			pst.setInt(10, uF.parseToInt(questionID));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	

	private void getQuestions() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from learning_plan_question_bank where learning_plan_question_bank_id = ? ");
			pst.setInt(1, uF.parseToInt(getQueID()));
			rs = pst.executeQuery();
//			System.out.println("pst ==== > "+pst);
			List<String> queDetailsList = new ArrayList<String>();
			while (rs.next()) {
				queDetailsList.add(rs.getString("learning_plan_question_bank_id"));//0
				queDetailsList.add(rs.getString("learning_plan_question_text"));//1
				queDetailsList.add(rs.getString("weightage"));//2
				queDetailsList.add(rs.getString("learning_plan_id"));//3
				queDetailsList.add(rs.getString("option_a"));//4
				queDetailsList.add(rs.getString("option_b"));//5
				queDetailsList.add(rs.getString("option_c"));//6
				queDetailsList.add(rs.getString("option_d"));//7
				queDetailsList.add(rs.getString("correct_ans"));//8
				queDetailsList.add(rs.getString("answer_type"));//9
				queDetailsList.add(rs.getString("is_add"));//10
			}
			rs.close();
			pst.close();
			request.setAttribute("queDetailsList", queDetailsList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getQueID() {
		return queID;
	}

	public void setQueID(String queID) {
		this.queID = queID;
	}

	public String getQueAnstype() {
		return queAnstype;
	}

	public void setQueAnstype(String queAnstype) {
		this.queAnstype = queAnstype;
	}

	public String getQueno() {
		return queno;
	}

	public void setQueno(String queno) {
		this.queno = queno;
	}


	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	@Override
	public void setSession(Map session) {
		this.session = session;
	}


	public String getFromPage() {
		return fromPage;
	}


	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	
}
