package com.konnect.jpms.recruitment;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillGrade;
import com.opensymphony.xwork2.ActionSupport;

public class GetGradefromDesig extends ActionSupport implements ServletRequestAware{

  String strDesignation;
  List<FillGrade> gradeList;


	private static final long serialVersionUID = 1L;

	public String execute() {
		
	gradeList=new FillGrade(request).fillGradeFromDesignation(getStrDesignation());
//	gradeList.add(new FillGrade("",""));  
	
		return SUCCESS;
		
	}


	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
		
	}

	public String getStrDesignation() {
		return strDesignation;
	}

	public void setStrDesignation(String strDesignation) {
		this.strDesignation = strDesignation;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}	
	
	String pagefrom;

	public String getPagefrom() {
		return pagefrom;
	}

	public void setPagefrom(String pagefrom) {
		this.pagefrom = pagefrom;
	}
	
}
