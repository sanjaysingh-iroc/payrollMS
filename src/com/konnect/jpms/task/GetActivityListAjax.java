package com.konnect.jpms.task;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetActivityListAjax extends ActionSupport implements IStatements,
		ServletRequestAware, ServletResponseAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	List<FillActivityList> activitydetailslist;
	String activityID;
	String activityName; 
	String pro_id;
	UtilityFunctions uF = new UtilityFunctions();

	public String execute() {

//		activitydetailslist = new FillActivityList(request).fillActivityDetailsByProject(uF.parseToInt(pro_id));

		return SUCCESS;
	}

	

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
		

	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	public List<FillActivityList> getActivitydetailslist() {
		return activitydetailslist;
	}

	public void setActivitydetailslist(
			List<FillActivityList> activitydetailslist) {
		this.activitydetailslist = activitydetailslist;
	}

	public String getActivityID() {
		return activityID;
	}

	public void setActivityID(String activityID) {
		this.activityID = activityID;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getPro_id() {
		return pro_id;
	}

	public void setPro_id(String pro_id) {
		this.pro_id = pro_id;
	}

}
