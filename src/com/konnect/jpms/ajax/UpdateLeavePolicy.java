package com.konnect.jpms.ajax;

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

public class UpdateLeavePolicy extends ActionSupport implements IStatements, ServletRequestAware{

	private String strLeavePolicyId;
	private String strLeaveStatus;
	private String strType;
	
	String strSessionEmpId;
	HttpSession session;
	CommonFunctions CF;
	
	public String execute() throws Exception {
		 
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		
		strLeavePolicyId = request.getParameter("LPID");
		strLeaveStatus = request.getParameter("CS");
		strType = request.getParameter("type");
		
		
		request.setAttribute("STATUS_MSG", "<b><font color=\"red\">Sorry, the policy could not be updated.</font></b>");
		updateLeavePolicy(strLeavePolicyId, strLeaveStatus, strType);
		return SUCCESS;
	
	}

	public void updateLeavePolicy(String strLeavePolicyId, String strLeaveStatus, String strType){

		Connection con = null;
		PreparedStatement pst = null, pst1 = null, pst2 = null;
		
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);

			if(strType!=null && strType.equalsIgnoreCase("H")){
				pst = con.prepareStatement("update emp_leave_type set is_holiday_compensation=? where emp_leave_type_id=?");
				pst.setBoolean(1, uF.parseToBoolean(strLeaveStatus));
				pst.setInt(2, uF.parseToInt(strLeavePolicyId));
				pst.execute();
	            pst.close();
				request.setAttribute("STATUS_MSG", "<b><font color=\"blue\">Policy updated successfully.</font></b>");
			}else if(strType!=null && strType.equalsIgnoreCase("W")){
				pst = con.prepareStatement("update emp_leave_type set is_weekly_compensation=? where emp_leave_type_id=?");
				pst.setBoolean(1, uF.parseToBoolean(strLeaveStatus));
				pst.setInt(2, uF.parseToInt(strLeavePolicyId));
				pst.execute();
	            pst.close();
				request.setAttribute("STATUS_MSG", "<b><font color=\"blue\">Policy updated successfully.</font></b>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeStatements(pst2);
			db.closeConnection(con);
		}

	}
	
	
	
	
	
	
	
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}


}