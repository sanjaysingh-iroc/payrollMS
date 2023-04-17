package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class EditTrainingFeedbackQuestion implements ServletRequestAware, SessionAware, IStatements {
	Map session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;

	private String ID;
	private String operation;
	private String step;
	private String queID;
	private String queAnstype;
	private String trainingType;
	private String queno;
	
		public String execute() {
		strUserType = (String) session.get(USERTYPE);
		strSessionEmpId = (String) session.get(EMPID);

		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return "login";

	/*	request.setAttribute(PAGE, "/jsp/training/EditTrainingFeedbackQuestion.jsp");
		request.setAttribute(TITLE, "Edit Question"); */

//		System.out.println("ID====>"+ID);
//		System.out.println("operation====>"+operation);
//		System.out.println("trainingType====>"+trainingType);
//		System.out.println("queID====>"+queID);
		
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

//		return LOAD;
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
		ResultSet rst = null;
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

		String[] correct = request.getParameterValues("correct"+ orientt);
		StringBuilder option = new StringBuilder();

		for (int ab = 0; correct != null && ab < correct.length; ab++) {
			option.append(correct[ab] + ",");
		}
		
		try {
			con = db.makeConnection(con);

//					System.out.println("hidequeid[l] ;;;;;;;;;;;; " + hidequeid[l]);
			
			pst = con.prepareStatement("insert into training_question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
			pst.setString(1, question);
			pst.setString(2, optiona);
			pst.setString(3, optionb);
			pst.setString(4, optionc);
			pst.setString(5, optiond);
			pst.setString(6, option.toString());
			pst.setBoolean(7, uF.parseToBoolean(addFlag));
			pst.setInt(8, uF.parseToInt(ansType));
			pst.executeUpdate();
			pst.close();
			
			int question_id =0;
			pst = con.prepareStatement("select max(training_question_bank_id) from training_question_bank");
			rst = pst.executeQuery();
			while (rst.next()) {
				question_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
	
			pst = con.prepareStatement("update training_question_details set question_id=? where question_id=? and plan_id=?");
			pst.setInt(1, question_id);
			pst.setInt(2, uF.parseToInt(questionID));
			pst.setInt(3, uF.parseToInt(getID()));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
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
			pst = con.prepareStatement("select * from training_question_bank where training_question_bank_id = ? ");
			pst.setInt(1, uF.parseToInt(getQueID()));
			rs = pst.executeQuery();
//			System.out.println("pst ==== > "+pst);
			List<String> queDetailsList = new ArrayList<String>();
			while (rs.next()) {
				queDetailsList.add(rs.getString("training_question_bank_id"));//0
				queDetailsList.add(rs.getString("question_text"));//1
				queDetailsList.add(rs.getString("option_a"));//2
				queDetailsList.add(rs.getString("option_b"));//3
				queDetailsList.add(rs.getString("option_c"));//4
				queDetailsList.add(rs.getString("option_d"));//5
				queDetailsList.add(rs.getString("correct_ans"));//6
				queDetailsList.add(rs.getString("question_type"));//7
				queDetailsList.add(rs.getString("is_add"));//8

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

	public String getTrainingType() {
		return trainingType;
	}

	public void setTrainingType(String trainingType) {
		this.trainingType = trainingType;
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

}
