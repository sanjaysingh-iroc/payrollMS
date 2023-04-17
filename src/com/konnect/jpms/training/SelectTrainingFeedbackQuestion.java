package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SelectTrainingFeedbackQuestion extends ActionSupport implements
		ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;
	private String count;
	private String ansType;
	private String callFrom;
	public String getAnsType() {
		return ansType;
	}

	public void setAnsType(String ansType) {
		this.ansType = ansType;
	}

//	List<FillOrientation> orientationList;
		
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		//System.out.println("count "+ count);
		//System.out.println("count "+ getCount());
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		//orientationList = new FillOrientation().fillOrientation();
		getTrainingFeedbackQuestionList();
		
		return LOAD;

	}
	
	public void getTrainingFeedbackQuestionList() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF=new UtilityFunctions();
		try {
			StringBuilder sb = new StringBuilder("");
//			System.out.println("Answer Type : "+ansType);
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from training_question_bank where question_type = ?"); // and question_type=? is_add=true and 
			pst.setInt(1, uF.parseToInt(ansType));
			rs = pst.executeQuery();
//			System.out.println("Answer Type pst ::::: "+pst);
			while (rs.next()) {
				sb.append("<option value=\"" + rs.getString("training_question_bank_id")
						+ "\">" + rs.getString("question_text").replace("'", "") + "</option>");
			}
			rs.close();
			pst.close();
			//sb.append("<option value=\"0\">Add new Question</option>");
			request.setAttribute("option", sb.toString());} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getCallFrom() {
		return callFrom;
	}

	public void setCallFrom(String callFrom) {
		this.callFrom = callFrom;
	}

	
	
}
