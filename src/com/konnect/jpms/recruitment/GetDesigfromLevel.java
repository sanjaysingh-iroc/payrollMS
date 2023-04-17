package com.konnect.jpms.recruitment;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillGrade;
import com.opensymphony.xwork2.ActionSupport;

public class GetDesigfromLevel extends ActionSupport implements ServletRequestAware {
	private static final long serialVersionUID = 1L;
	String strLevel;
	String strDesig;
	String pagefrom;

	List<FillDesig> desigList;
	List<FillGrade> gradeList;
	
	List<FillDesig> designationList;
	List<FillDesig> designationList1;
	
	

	public String execute() {

		if (getStrLevel() != null && getPagefrom()!=null && getPagefrom().equalsIgnoreCase("RR") ) {
			designationList1 = new FillDesig(request).fillDesigFromLevel(getStrLevel());
		} else if (getStrLevel() != null && getPagefrom()!=null && getPagefrom().equalsIgnoreCase("updateJobProfile") ) {
			designationList = new FillDesig(request).fillDesigFromLevel(getStrLevel());
		} else if (getStrLevel() != null) {
			desigList = new FillDesig(request).fillDesigFromLevel(getStrLevel());
		}
		if(getPagefrom() != null && getPagefrom().equals("RRequest")) {
			desigList.add(new FillDesig("0", "Add New Designation"));
		}
		if (getStrDesig() != null) {
			gradeList = new FillGrade(request).fillGradeFromDesignation(getStrDesig());
		}

		return SUCCESS;

	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public String getPagefrom() {
		return pagefrom;
	}

	public void setPagefrom(String pagefrom) {
		this.pagefrom = pagefrom;
	}

	public List<FillDesig> getDesignationList() {
		return designationList;
	}

	public void setDesignationList(List<FillDesig> designationList) {
		this.designationList = designationList;
	}
	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getStrDesig() {
		return strDesig;
	}

	public void setStrDesig(String strDesig) {
		this.strDesig = strDesig;
	}

	public List<FillDesig> getDesignationList1() {
		return designationList1;
	}

	public void setDesignationList1(List<FillDesig> designationList1) {
		this.designationList1 = designationList1;
	}
	
}
