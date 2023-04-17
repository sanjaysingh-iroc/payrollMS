package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class AddQuestionOfLearningFeedback implements ServletRequestAware, SessionAware, IStatements {
	Map session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;

	
	private String ID;
	private String operation;
	private String step;
	private String queID;

	private String strSubmit;
	private String strCancel;
	public String execute() {
		
		strUserType = (String) session.get(USERTYPE);
		strSessionEmpId = (String) session.get(EMPID);

		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return "login";

		AddFeedbackQuestion();
		return "load";
	}
		
	private void AddFeedbackQuestion() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		String questionID = request.getParameter("questionID");
		String weightage = request.getParameter("weightage");
		String question = request.getParameter("question");
		String ansType = request.getParameter("ansType");
		
		String addFlag = request.getParameter("status");
		String optiona = request.getParameter("optiona");
		String optionb = request.getParameter("optionb");
		String optionc = request.getParameter("optionc");
		String optiond = request.getParameter("optiond");
		String queCount = request.getParameter("queCount");
		String isAdd = request.getParameter("addFlag");
		String[] correct = request.getParameterValues("correct"+ queCount);
		StringBuilder option = new StringBuilder();

		for (int ab = 0; correct != null && ab < correct.length; ab++) {
			option.append(correct[ab] + ",");
		}
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("insert into learning_plan_question_bank(learning_plan_question_text,weightage,option_a," +
					"option_b,option_c,option_d,correct_ans,answer_type,learning_plan_id,added_by,entry_date,is_add) values(?,?,?,?,?,?,?,?,?,?,?,?)");
			pst.setString(1, question);
			pst.setDouble(2, uF.parseToDouble(weightage));
			pst.setString(3, optiona);
			pst.setString(4, optionb);
			pst.setString(5, optionc);
			pst.setString(6, optiond);
			pst.setString(7, option.toString());
			pst.setInt(8, uF.parseToInt(ansType));
			pst.setInt(9, uF.parseToInt(getID()));
			pst.setInt(10, uF.parseToInt(strSessionEmpId));
			pst.setDate(11, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setBoolean(12, uF.parseToBoolean(isAdd));
//			System.out.println("pst==>"+pst);
			pst.execute();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
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


	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	@Override
	public void setSession(Map session) {
		this.session = session;
	}	

	public String getStrSubmit() {
		return strSubmit;
	}


	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}

}
