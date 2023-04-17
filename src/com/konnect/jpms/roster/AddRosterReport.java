package com.konnect.jpms.roster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;

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

public class AddRosterReport  extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpID;
	CommonFunctions CF = null;

	String f_org;
	String f_strWLocation;
	String f_department;
	String f_service;
	String f_level;
	String strMonth;
	String strYear;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF == null) return LOGIN;
		
		
			    
	    String _to = request.getParameter("_to");
	    String _from = request.getParameter("_from");
	    String rosterDate = request.getParameter("rosterDate");
	    String empId = request.getParameter("empId");
	    String serviceId = request.getParameter("service");
	    
	   
	      
	     
	     	Connection con = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			Database db = new Database();
			db.setRequest(request);
			UtilityFunctions uF = new UtilityFunctions();
			
			Time startTime = uF.getTimeFormat(_from, CF.getStrReportTimeFormat());
			Time endTime = uF.getTimeFormat(_to, CF.getStrReportTimeFormat());
			
			double dblTotalRosterTime = 0d;
			long lStartTime = startTime.getTime();
			long lEndTime = endTime.getTime();
			
			dblTotalRosterTime = uF.parseToDouble(uF.getTimeDiffInHoursMins(lStartTime, lEndTime));
			
	     try {
	    	 
	    	 	con = db.makeConnection(con);
	    	 
	    	 
	    		 pst = con.prepareStatement(insertRosterDetails);
	    		 
		    	 pst.setDate(1, uF.getDateFormat(rosterDate, CF.getStrReportDateFormat()));
		    	 pst.setInt(2, uF.parseToInt(empId));
		    	 pst.setInt(3, uF.parseToInt(serviceId));
		    	 pst.setTime(4, uF.getTimeFormat(_from, CF.getStrReportTimeFormat()));
		    	 pst.setTime(5, uF.getTimeFormat(_to, CF.getStrReportTimeFormat()));
		    	 pst.setDouble(6, dblTotalRosterTime);
		    	 pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
		    	 pst.execute();
		    	 pst.close();
	    	 
	    	 
	    	 
	    	 
	    	 
	    	 
	    	 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	     
	     return SUCCESS;

		
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;

	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String getF_department() {
		return f_department;
	}

	public void setF_department(String f_department) {
		this.f_department = f_department;
	}

	public String getF_service() {
		return f_service;
	}

	public void setF_service(String f_service) {
		this.f_service = f_service;
	}

	public String getF_level() {
		return f_level;
	}

	public void setF_level(String f_level) {
		this.f_level = f_level;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getStrYear() {
		return strYear;
	}

	public void setStrYear(String strYear) {
		this.strYear = strYear;
	}


}
