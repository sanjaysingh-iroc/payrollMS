package com.konnect.jpms.location;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.zkoss.web.servlet.dsp.action.ForEach;

import com.konnect.jpms.select.FillWeekDays;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetRemainingWeeklyOffDays extends ActionSupport implements
		ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;
	

	String wOffDay1;
	String wOffDay2;
	String wOffDay3;
	List<FillWeekDays> weeklyOffList1;
	List<FillWeekDays> weeklyOffList2;
	List<FillWeekDays> weeklyOffList3;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		getRemainingWeekDays();
		return SUCCESS;

	}
	
	public void getRemainingWeekDays() {
		weeklyOffList1 = new FillWeekDays().fillWeekDays(wOffDay2, wOffDay3);
		weeklyOffList2 = new FillWeekDays().fillWeekDays(wOffDay1, wOffDay3);
		weeklyOffList3 = new FillWeekDays().fillWeekDays(wOffDay1, wOffDay2);
		
		StringBuilder alldata = new StringBuilder();
		alldata.append("<select id=\"weeklyOff1\" name=\"weeklyOff1\" style=\"float: left; margin-right: 7px; width: 100px;\" onchange=\"getRemainingWeekDays(this.value, '1');\">");
		alldata.append("<option value=\"\">Select Day</option>");
		for(int i=0; weeklyOffList1 != null && i< weeklyOffList1.size(); i++) {
			alldata.append("<option value=\""+weeklyOffList1.get(i).getWeekDayId()+"\">"+ weeklyOffList1.get(i).getWeekDayName()+"</option>");
		}
		alldata.append("</select> ");
		alldata.append("::::");
		alldata.append("<select id=\"weeklyOff2\" name=\"weeklyOff2\" style=\"float: left; margin-right: 7px; width: 100px;\" onchange=\"getRemainingWeekDays(this.value, '2');\">");
				alldata.append("<option value=\"\">Select Day</option>");
		for(int i=0; weeklyOffList2 != null && i< weeklyOffList2.size(); i++) {
			alldata.append("<option value=\""+weeklyOffList2.get(i).getWeekDayId()+"\">"+ weeklyOffList2.get(i).getWeekDayName()+"</option>");
		}
		alldata.append("</select> ");
		alldata.append("::::");
		alldata.append("<select id=\"weeklyOff3\" name=\"weeklyOff3\" style=\"float: left; margin-right: 7px; width: 100px;\" onchange=\"getRemainingWeekDays(this.value, '3');\">");
				alldata.append("<option value=\"\">Select Day</option>");
		for(int i=0; weeklyOffList3 != null && i< weeklyOffList3.size(); i++) {
			alldata.append("<option value=\""+weeklyOffList3.get(i).getWeekDayId()+"\">"+ weeklyOffList3.get(i).getWeekDayName()+"</option>");
		}
		alldata.append("</select> ");
//		System.out.println("alldata :: "+alldata);
		request.setAttribute("alldata", alldata);
	}
	
	public String getwOffDay1() {
		return wOffDay1;
	}

	public void setwOffDay1(String wOffDay1) {
		this.wOffDay1 = wOffDay1;
	}

	public String getwOffDay2() {
		return wOffDay2;
	}

	public void setwOffDay2(String wOffDay2) {
		this.wOffDay2 = wOffDay2;
	}

	public String getwOffDay3() {
		return wOffDay3;
	}

	public void setwOffDay3(String wOffDay3) {
		this.wOffDay3 = wOffDay3;
	}

	public List<FillWeekDays> getWeeklyOffList1() {
		return weeklyOffList1;
	}

	public void setWeeklyOffList1(List<FillWeekDays> weeklyOffList1) {
		this.weeklyOffList1 = weeklyOffList1;
	}

	public List<FillWeekDays> getWeeklyOffList2() {
		return weeklyOffList2;
	}

	public void setWeeklyOffList2(List<FillWeekDays> weeklyOffList2) {
		this.weeklyOffList2 = weeklyOffList2;
	}

	public List<FillWeekDays> getWeeklyOffList3() {
		return weeklyOffList3;
	}

	public void setWeeklyOffList3(List<FillWeekDays> weeklyOffList3) {
		this.weeklyOffList3 = weeklyOffList3;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}


}
