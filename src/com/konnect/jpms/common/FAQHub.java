package com.konnect.jpms.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
import com.konnect.jpms.util.CommonFunctions;
public class FAQHub extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final String LOGIN = null;
	
	public HttpServletRequest request;
	public String fadId;
	
	public String strQuestion;
	public String strSessionEmpId;
	public String strAnswer;
	private CommonFunctions CF;
	private String curr_date;
	private String strSessionOrgId;
	private String operation;
	


	HttpSession session; 
	boolean Flag=false;
	boolean AddFlag;
	
	UtilityFunctions uF = new UtilityFunctions();
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(getStrQuestion()!= null && !(getStrQuestion().equals("")) && getStrAnswer()!=null && !(getStrAnswer().equals(" ")) )
		{
			Flag = addnewQA();
		}
		
		return "success";
	}
	
	
	public Boolean addnewQA() {
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		
		int empid = uF.parseToInt(strSessionEmpId);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst= con.prepareStatement("insert into faq_details(faq_question,faq_answer,added_by,entry_date,org_id)values(?,?,?,?,?)");
			pst.setString(1, strQuestion);
			pst.setString(2, strAnswer);
			pst.setInt(3, empid);
			pst.setTimestamp(4,uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) +"" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			//pst.setInt(5,  empid);
			//pst.setTimestamp(6,  uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) +"" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setString(5,  strSessionOrgId);
			pst.execute();
			Flag = true;
			pst.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return Flag;
	}
	
	public String getStrQuestion() {
		return strQuestion;
	}
	
	public void setStrQuestion(String strQuestion) {
		this.strQuestion = strQuestion;
	}
	
	public String getStrAnswer() {
		return strAnswer;
	}
	
	public void setStrAnswer(String strAnswer) {
		this.strAnswer = strAnswer;
	}
	public String getFadId() {
		return fadId;
	}
	public void setFadId(String fadId) {
		this.fadId = fadId;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}


}
