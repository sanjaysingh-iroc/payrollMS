package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeLeaveEntryDetailsReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strUserType = null;
	String strSessionEmpId = null;
	private static Logger log = Logger.getLogger(EmployeeLeaveEntryDetailsReport.class);
	
	public String execute() throws Exception {
		session=request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, PEmployeeLeaveEntryDetailsReport);
		request.setAttribute(TITLE, TViewEmployeeLeaveEntry);
		String strLID = request.getParameter("LID");
		String strEmpID = request.getParameter("EMPID");
		
		viewEmployeeLeaveEntry(strLID, strEmpID);			
		return loadEmployeeLeaveEntry();

	}
	
	public String loadEmployeeLeaveEntry(){	
		return "load";
	}
	
	public String viewEmployeeLeaveEntry(String strLID, String strEmpID){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);

		try {

			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
			UtilityFunctions uF=new UtilityFunctions();
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectEmployeeLeaveEntryVP);
			pst.setInt(1, uF.parseToInt(strLID));
			pst.setInt(2, uF.parseToInt(strEmpID));
			rs = pst.executeQuery();
			
			while(rs.next()){
				alInner = new ArrayList<String>();
				alInner.add(uF.getDateFormat(rs.getString("entrydate"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(uF.getDateFormat(rs.getString("leave_from"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(uF.getDateFormat(rs.getString("leave_to"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(rs.getString("emp_no_of_leave")+  ((uF.parseToBoolean(rs.getString("is_modify")))?"<div title=\"Modified\" class=\"leftearly\">&nbsp;</div>":""));
				alInner.add(uF.showData(rs.getString("reason"),""));
				alInner.add(uF.showData(rs.getString("manager_reason"),""));
				alInner.add(uF.showData(hmEmployeeNameMap.get(rs.getString("approvedby")), ""));
				alInner.add(CF.getApproveDenyFlag(rs.getInt("is_approved"), true));
				if(rs.getInt("is_approved")==0){
					alInner.add("<a href="+request.getContextPath()+"/EmployeeLeaveEntry.action?E="+rs.getString("leave_id")+">Edit</a> | <a onclick=\"return confirm('"+ConfirmDelete+"');\" href="+request.getContextPath()+"/EmployeeLeaveEntry.action?D="+rs.getString("leave_id")+">Delete</a>");
				}else{
					alInner.add("");
				}
				
				al.add(alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", al);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
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

}
