package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CheckDateApprovedStatus extends ActionSupport implements ServletRequestAware, IConstants {
	
	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	private HttpServletRequest request;
	UtilityFunctions uF = new UtilityFunctions();
	
	HttpSession session;
	
	String strDate;
	public String execute() { 
	
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		String strSessionEmpId = (String)session.getAttribute(EMPID);
		checkDateApprovedStatus(strSessionEmpId);
		
		return SUCCESS;
	}
	
	String tId;
	public void checkDateApprovedStatus(String strSessionEmpId) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		try {
			
//			System.out.println("getStrDate() ===>> " + getStrDate());
			boolean flag = false;
			con = db.makeConnection(con);
			StringBuilder sbquery = new StringBuilder();
			sbquery.append("select activity_id, is_approved from task_activity where activity_id in (select task_id from activity_info where " +
				"resource_ids like '%,"+strSessionEmpId+",%') and task_date=? and is_approved=2 ");
			/*if(uF.parseToInt(gettId())> 0) {
				sbquery.append(" and activity_id ="+uF.parseToInt(gettId())+"");
			} else if(gettId().length() > 0) {
				sbquery.append(" and activity = "+gettId()+"");
			}*/
			pst = con.prepareStatement(sbquery.toString());
			if(getStrDate() != null && !getStrDate().equals("")) {
				pst.setDate(1, uF.getDateFormat(getStrDate(), DATE_FORMAT));
			} else {
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			}
			//System.out.println("pst ===>> " + pst);
			
			rs = pst.executeQuery();
			while(rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("flag", flag);
//			System.out.println("flag===> " + flag);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}


	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	public String gettId() {
		return tId;
	}

	public void settId(String tId) {
		this.tId = tId;
	}


}
