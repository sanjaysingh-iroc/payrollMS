package com.konnect.jpms.salary;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetGradeListByLevel extends ActionSupport implements ServletRequestAware {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8332662183574453647L;
	
	String strOrg;
	String levelId;	
	List<FillGrade> gradeList;

	public String execute() {
		UtilityFunctions uF = new UtilityFunctions();
		if (uF.parseToInt(getStrOrg()) > 0 && uF.parseToInt(getLevelId()) > 0) {
			gradeList = new FillGrade(request).fillGradeByOrgLevel(uF.parseToInt(getStrOrg()),uF.parseToInt(getLevelId()));
		} else {
			gradeList = new ArrayList<FillGrade>();
		}

		return SUCCESS;
	}

	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getLevelId() {
		return levelId;
	}

	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}
}