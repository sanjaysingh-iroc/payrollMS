package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class OptionalHolidayLimit extends ActionSupport implements ServletRequestAware, IStatements {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	String optionalLeaveLimit;
	String calendarYear;
	String orgId;
	String strWlocation;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF == null) return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		if (uF.parseToInt(getOptionalLeaveLimit()) <= 0 && getCalendarYear()!=null) {
			request.setAttribute("STATUS_MSG","Please select the proper date or enter the proper limit.");
			return SUCCESS;
		} 
		
		validateCode(uF);
		
		return SUCCESS;
	}


	public void validateCode(UtilityFunctions uF){
		
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		
		try {
			String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;
			if (getCalendarYear() != null) {
				strCalendarYearDates = getCalendarYear().split("-");
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			}
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT count(*) as cnt FROM holidays WHERE is_optional_holiday=true and _date between ? and ? " +
					"and org_id=? and wlocation_id=?");			
			pst.setDate(1, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getOrgId()));
			pst.setInt(4, uF.parseToInt(getStrWlocation()));
			rs = pst.executeQuery();
//			System.out.println("pst=="+pst);
			int cnt = 0;
			while(rs.next()){
				cnt = uF.parseToInt(rs.getString("cnt"));
			}
            rs.close();
            pst.close();
			
            if(uF.parseToInt(getOptionalLeaveLimit()) > cnt){
            	request.setAttribute("STATUS_MSG","Sorry You have enter limit more than "+cnt+".");
            }
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}


	public String getOptionalLeaveLimit() {
		return optionalLeaveLimit;
	}


	public void setOptionalLeaveLimit(String optionalLeaveLimit) {
		this.optionalLeaveLimit = optionalLeaveLimit;
	}


	public String getCalendarYear() {
		return calendarYear;
	}


	public void setCalendarYear(String calendarYear) {
		this.calendarYear = calendarYear;
	}


	public String getOrgId() {
		return orgId;
	}


	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}


	public String getStrWlocation() {
		return strWlocation;
	}


	public void setStrWlocation(String strWlocation) {
		this.strWlocation = strWlocation;
	}

}
