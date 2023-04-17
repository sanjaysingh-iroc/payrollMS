package com.konnect.jpms.ajax;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDishes;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetDishes extends ActionSupport implements IStatements,ServletRequestAware{
	
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	
	public CommonFunctions CF;
	
	private String mealType;
	private String startDate;
	private String endDate;
	private String strDishIds;
	
	private List<FillDishes> dishList;
	private static final long serialVersionUID = 1L;

	public String execute() {
		session = request.getSession();
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		UtilityFunctions uF = new UtilityFunctions();
		/*System.out.println("mealType==>"+getMealType()+"startDate==>"+getStartDate()+"==>endDate==>"+getEndDate());*/
		dishList = new ArrayList<FillDishes>();
		if(getMealType() != null && !getMealType().equals("")) {
			if(getStartDate() != null && !getStartDate().equals("") && getEndDate() != null && !getEndDate().equals("")) {
				if(strUserType != null && strUserType.equals(ADMIN)) {
					dishList = new FillDishes(request).fillDishes("",getMealType(),uF.getDateFormat(getStartDate(),DATE_FORMAT,DBDATE), uF.getDateFormat(getEndDate(),DATE_FORMAT,DBDATE),strUserType,CF);
				} else if(strUserType != null && strUserType.equals(HRMANAGER)) {
					if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
						dishList = new FillDishes(request).fillDishes((String)session.getAttribute(WLOCATION_ACCESS),getMealType(),uF.getDateFormat(getStartDate(),DATE_FORMAT,DBDATE), uF.getDateFormat(getEndDate(),DATE_FORMAT,DBDATE),strUserType,CF);
					} else {
						dishList = new FillDishes(request).fillDishes((String)session.getAttribute(WLOCATIONID),getMealType(),uF.getDateFormat(getStartDate(),DATE_FORMAT,DBDATE), uF.getDateFormat(getEndDate(),DATE_FORMAT,DBDATE),strUserType,CF);
					}
				} else {
					dishList = new FillDishes(request).fillDishes((String)session.getAttribute(WLOCATIONID),getMealType(),uF.getDateFormat(getStartDate(),DATE_FORMAT,DBDATE), uF.getDateFormat(getEndDate(),DATE_FORMAT,DBDATE),strUserType,CF);
				}
			}else {
				dishList = new ArrayList<FillDishes>();
			}
		} 
		
		return SUCCESS;
	}
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	public List<FillDishes> getDishList() {
		return dishList;
	}
	public void setDishList(List<FillDishes> dishList) {
		this.dishList = dishList;
	}
	public String getMealType() {
		return mealType;
	}
	public void setMealType(String mealType) {
		this.mealType = mealType;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getStrDishIds() {
		return strDishIds;
	}
	public void setStrDishIds(String strDishIds) {
		this.strDishIds = strDishIds;
	}
	
}
