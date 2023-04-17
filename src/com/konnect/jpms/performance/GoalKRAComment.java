package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GoalKRAComment extends ActionSupport implements ServletRequestAware, IStatements{

	public HttpSession session;
	private String empId;
	private String goalid;
	private String goalFreqId;
	private String kraId; 
	private String kraTaskId;
	private String goalType;
	private String feedbackUserType;
	private String dataType;
	private String currUserType;
	private String pagefrom;

	public String execute() {
	
		session = request.getSession();
		String str = getDataType();
		
		request.setAttribute(PAGE, "/jsp/performance/GoalKRAComment.jsp");
		request.setAttribute("pageFrom",pagefrom);
		return "success";
	}


	public String getEmpId() {
		return empId;
	}


	public void setEmpId(String empId) {
		this.empId = empId;
	}


	public String getGoalid() {
		return goalid;
	}


	public void setGoalid(String goalid) {
		this.goalid = goalid;
	}


	public String getGoalFreqId() {
		return goalFreqId;
	}


	public void setGoalFreqId(String goalFreqId) {
		this.goalFreqId = goalFreqId;
	}


	public String getKraId() {
		return kraId;
	}


	public void setKraId(String kraId) {
		this.kraId = kraId;
	}


	public String getKraTaskId() {
		return kraTaskId;
	}


	public void setKraTaskId(String kraTaskId) {
		this.kraTaskId = kraTaskId;
	}


	public String getGoalType() {
		return goalType;
	}


	public void setGoalType(String goalType) {
		this.goalType = goalType;
	}


	public String getFeedbackUserType() {
		return feedbackUserType;
	}


	public void setFeedbackUserType(String feedbackUserType) {
		this.feedbackUserType = feedbackUserType;
	}


	public String getCurrUserType() {
		return currUserType;
	}
	
	
	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	HttpServletRequest request;
	
	
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getPagefrom() {
		return pagefrom;
	}


	public void setPagefrom(String pagefrom) {
		this.pagefrom = pagefrom;
	}


	
	

}
