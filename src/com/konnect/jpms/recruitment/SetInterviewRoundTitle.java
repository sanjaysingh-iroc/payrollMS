package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SetInterviewRoundTitle extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(SetInterviewRoundTitle.class);
	
	String strUserType;
	String strSessionEmpId;
	HttpSession session;
	
	CommonFunctions CF;
	private String interviewRoundId;
	private String recruitId;
	private String roundName;
	private String operation;
	
	public String execute() throws Exception {
		
		session = request.getSession();		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/recruitment/SetInterviewRoundTitle.jsp");
		request.setAttribute(TITLE, "Set Round Title");
//		System.out.println("getMailID()===> "+getMailID());
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("operation ======> " + operation);
//		System.out.println("interviewRoundId ======> " + interviewRoundId);
		if(getOperation() != null && getOperation().equals("A")){
			addRoundTitle(uF);
			return getRoundTitle(uF);
		}
		getRoundTitle(uF);
		
		return LOAD;
	}

//Filter variables **************

private String addRoundTitle(UtilityFunctions uF) {

	Connection con = null;
	PreparedStatement pst = null;
	Database db = new Database();
	db.setRequest(request);
	
	try {
		con = db.makeConnection(con);

			pst = con.prepareStatement("update panel_interview_details set round_name = ? where round_id = ? and recruitment_id = ? and panel_emp_id is null");
			pst.setString(1, getRoundName());
			pst.setInt(2, uF.parseToInt(getInterviewRoundId()));
			pst.setInt(3, uF.parseToInt(getRecruitId()));
			pst.executeUpdate();
			pst.close();
			
	} catch (Exception e) {
		e.printStackTrace();
		log.error(e.getClass() + ": " + e.getMessage(), e);
	} finally {
		
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	return SUCCESS;

}

public String getRoundTitle(UtilityFunctions uF) {

	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rst = null;
	Database db = new Database();
	db.setRequest(request);
	
	try {
		con = db.makeConnection(con);

		pst = con.prepareStatement("select round_name from panel_interview_details where round_id = ? and recruitment_id = ? and panel_emp_id is null");
		pst.setInt(1, uF.parseToInt(getInterviewRoundId()));
		pst.setInt(2, uF.parseToInt(getRecruitId()));
		rst = pst.executeQuery();
		while (rst.next()) {
			setRoundName(rst.getString("round_name"));
		}
		rst.close();
		pst.close();
		
	} catch (Exception e) {
		e.printStackTrace();
		log.error(e.getClass() + ": " + e.getMessage(), e);
	} finally {
		db.closeResultSet(rst);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	return SUCCESS;

}

	
	
	public String getInterviewRoundId() {
		return interviewRoundId;
	}
	
	public void setInterviewRoundId(String interviewRoundId) {
		this.interviewRoundId = interviewRoundId;
	}

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getRoundName() {
		return roundName;
	}

	public void setRoundName(String roundName) {
		this.roundName = roundName;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	private HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
}
