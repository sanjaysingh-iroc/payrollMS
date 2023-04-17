package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddLTA extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF=null;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		String operation = request.getParameter("operation");
		if (operation.equals("D")) {
			return deleteLTA();
		}
		if (operation.equals("U")) { 
				return updateLTA();
		}
		if (operation.equals("A")) {
				return insertLTA();
		}
		
		return SUCCESS;
		
	}

	public String loadValidateLTA() {
		return LOAD;
	}

	public String insertLTA() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(insertLTA);
			pst.setDate(1, uF.getDateFormat(getLtaFrom(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getLtaTo(), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getLtaLimit()));
			pst.execute();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String updateLTA() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int columnId = Integer.parseInt(request.getParameter("columnId"));
		String columnName=null;
		
		switch(columnId) {
			
			case 0 : columnName = "lta_from"; break;
			case 1 : columnName = "lta_to"; break;
			case 2 : columnName = "lta_limit"; break;
		
		}
		String updateLTA = "UPDATE lta_details SET "+columnName+"=? WHERE lta_id=?";
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateLTA);
			if(columnId==2)
				pst.setInt(1, uF.parseToInt(request.getParameter("value")));
			else
				pst.setDate(1, uF.getDateFormat(request.getParameter("value"), CF.getStrReportDateFormat()));
			
			pst.setInt(2, uF.parseToInt(request.getParameter("id")));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;

	}
	
	public String deleteLTA() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteLTA);
			pst.setInt(1, uF.parseToInt(request.getParameter("id")));
			pst.execute();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	String ltaId;
	String ltaFrom;
	String ltaTo;
	String ltaLimit;
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getLTAId() {
		return ltaId;
	}

	public void setLTAId(String ltaId) {
		this.ltaId = ltaId;
	}

	public String getLtaFrom() {
		return ltaFrom;
	}

	public void setLtaFrom(String ltaFrom) {
		this.ltaFrom = ltaFrom;
	}

	public String getLtaTo() {
		return ltaTo;
	}

	public void setLtaTo(String ltaTo) {
		this.ltaTo = ltaTo;
	}

	public String getLtaLimit() {
		return ltaLimit;
	}

	public void setLtaLimit(String ltaLimit) {
		this.ltaLimit = ltaLimit;
	}
}