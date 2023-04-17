package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DeleteTrainingFeedbackQuestion extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request; 
	String strUserType = null;
	String strSessionEmpId = null;

	private CommonFunctions CF;
	private String ID;
	private String operation;
	private String step;
	private String queID;
	
	UtilityFunctions uF = new UtilityFunctions();
	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
			deleteTrainingFeedbackQue(uF);
		
				return LOAD;
	}
	
	
	public void deleteTrainingFeedbackQue(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
//			System.out.println("Step ===> "+getStep());
//			System.out.println("QueID ===> "+getQueID());
//			System.out.println("ID ===> "+getID());
//			System.out.println("Operation ===> "+getOperation());
				pst = con.prepareStatement("delete from training_question_details where question_id = ?");
				pst.setInt(1, uF.parseToInt(getQueID()));
//				System.out.println("pst1 ===> "+pst);
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from training_question_bank where training_question_bank_id = ?");
				pst.setInt(1, uF.parseToInt(getQueID()));
//				System.out.println("pst2 ===> "+pst);
				pst.executeUpdate();
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


	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	

}
