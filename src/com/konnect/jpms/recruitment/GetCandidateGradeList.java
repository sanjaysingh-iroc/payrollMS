package com.konnect.jpms.recruitment;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetCandidateGradeList extends ActionSupport implements ServletRequestAware{

	String strDesignation;
	String strDesignationUpdate;
	String strGrade;
	String strGradeUpdate;
	
	List<FillGrade> gradeList;
	
	private static final long serialVersionUID = 1L;

	public String execute() {
		
		
		String strDesigId = request.getParameter("DId");
		UtilityFunctions uF=new UtilityFunctions();
		try {
//			System.out.println("");
			if(getStrDesignationUpdate()!=null && uF.parseToInt(getStrDesignationUpdate())> 0) {
				gradeList = new FillGrade(request).fillGradeFromDesignation(getStrDesignationUpdate());
			}else if(getStrDesignation()!=null && uF.parseToInt(getStrDesignation()) > 0) {
				if(getStrGradeUpdate()==null || getStrGradeUpdate().length() == 0) {
					setStrGradeUpdate(getStrGrade());
				}
				gradeList = new FillGrade(request).fillGradeFromDesignation(getStrDesignation());
			}else if(strDesigId!=null && uF.parseToInt(strDesigId)>0){
				gradeList = new FillGrade(request).fillGradeFromDesignation(strDesigId);
			}else {
				gradeList = new FillGrade(request).fillGrade();
			}
				
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
		
	}


	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}


	public String getStrDesignation() {
		return strDesignation;
	}

	public void setStrDesignation(String strDesignation) {
		this.strDesignation = strDesignation;
	}


	public String getStrGrade() {
		return strGrade;
	}


	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}

	public String getStrDesignationUpdate() {
		return strDesignationUpdate;
	}

	public void setStrDesignationUpdate(String strDesignationUpdate) {
		this.strDesignationUpdate = strDesignationUpdate;
	}


	public String getStrGradeUpdate() {
		return strGradeUpdate;
	}


	public void setStrGradeUpdate(String strGradeUpdate) {
		this.strGradeUpdate = strGradeUpdate;
	}

	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
		
	}

	
}
