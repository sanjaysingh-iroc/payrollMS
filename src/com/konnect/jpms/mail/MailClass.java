package com.konnect.jpms.mail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.charts.BarChart;
import com.konnect.jpms.charts.PieCharts;
import com.konnect.jpms.reports.HolidayReport;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MailClass extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId;
	CommonFunctions CF = null;
	HttpServletRequest request;
	String myName = "";
	String myEmpId = "";
	
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	UtilityFunctions uF = new UtilityFunctions();

	public String execute() throws Exception {
		session = request.getSession();
		if (session == null)
			return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		String empType = (String) session.getAttribute(USERTYPE);
		strEmpId = (String) session.getAttribute("EMPID");

		request.setAttribute(PAGE, PMail);
		request.setAttribute(TITLE, TMail);

		assign();  

		if (CF == null)
			return LOGIN; 

		return SUCCESS;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setMyName(String myName) {
		this.myName = myName;
	}

	public String getMyName() {
		return myName;
	}

	public void assign() {
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			pst = con.prepareStatement(selectEmployee1Details);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();

			if (rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				myName = rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname") + "(" + rs.getString("empcode") + ")";				
			}
			rs.close();
			pst.close();
			myEmpId = strEmpId;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String getMyEmpId() {
		return myEmpId;
	}

	public void setMyEmpId(String myEmpId) {
		this.myEmpId = myEmpId;
	}
}
