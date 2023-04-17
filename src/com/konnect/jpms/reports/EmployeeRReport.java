package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeRReport extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF;
	private static Logger log = Logger.getLogger(EmployeeRReport.class);
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId= (String) session.getAttribute(EMPID);
		
		
		
		viewEmployeeReport();
		
		return LOAD;
	}

	public String loadEmployee() {
	
		return LOAD;
	}

	public String viewEmployeeReport() {

		Connection con = null;
		PreparedStatement pst = null, pst_sid = null;
		ResultSet rs = null, rs_sid = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			EmployeeReport objERR = new EmployeeReport();
			objERR.setServletRequest(request);
			objERR.execute();
			
			
			request.setAttribute(PAGE, PEmployeeRReport);
			request.setAttribute(TITLE, TViewEmployee);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs_sid);
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeStatements(pst_sid);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}
