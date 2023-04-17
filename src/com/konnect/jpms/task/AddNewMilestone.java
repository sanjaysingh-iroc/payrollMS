package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddNewMilestone extends ActionSupport implements
		ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	HttpSession session;
	CommonFunctions CF;
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	
	String milestone_name;
	String comment; 
	String startDate;
	String deadline;
	String pro_id;
	String operation;
	UtilityFunctions uF = new UtilityFunctions();
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		if(getOperation()!=null && getOperation().equals("I")){
			insertMilestoneDetails();
		}
			return SUCCESS;
	

	}
	public void insertMilestoneDetails() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		try {
			
			con = db.makeConnection(con);
			pst = con
					.prepareStatement("insert into activity_info(pro_id,activity_name,_comment,start_date,deadline,is_milestone) values(?,?,?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			pst.setString(2, getMilestone_name());
			pst.setString(3, getComment());
			pst.setDate(4, uF.getDateFormat(getStartDate(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getDeadline(), DATE_FORMAT));
			pst.setBoolean(6, true);
			pst.executeUpdate();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	public String getMilestone_name() {
		return milestone_name;
	}


	public void setMilestone_name(String milestone_name) {
		this.milestone_name = milestone_name;
	}


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	public String getStartDate() {
		return startDate;
	}


	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}


	public String getDeadline() {
		return deadline;
	}


	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}


	public String getPro_id() {
		return pro_id;
	}


	public void setPro_id(String pro_id) {
		this.pro_id = pro_id;
	}


	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request=request;
	}

}
