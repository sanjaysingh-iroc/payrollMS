package com.konnect.jpms.performance;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillGrade;
import com.opensymphony.xwork2.ActionSupport;

public class GetGradeList extends ActionSupport implements ServletRequestAware{

	private String strDesignation;

	private List<FillGrade> gradeList;
	private String page;
	
	private static final long serialVersionUID = 1L;

	public String execute() {
		
		try {
			if(strDesignation==null || strDesignation.equals("")) {
			gradeList = new FillGrade(request).fillGrade();
			} else {
				gradeList = new FillGrade(request).fillGradeFromMultipleDesignation(getStrDesignation());
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



	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
		
	}

	
}
