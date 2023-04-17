package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author workrig
 *
 */
public class MyWorks extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strBaseUserType = null;
	String strProductType =  null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	
	String callFrom;
	String taskId;
	String proType;
	
	public String execute() throws Exception {
       
		session = request.getSession(); 
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strUserType = (String) session.getAttribute(USERTYPE);
		strProductType = (String)session.getAttribute(PRODUCT_TYPE);
		
		UtilityFunctions uF = new UtilityFunctions();
		if(uF.parseToInt(strProductType) != 3) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
//		System.out.println("strBaseUserType ===>> " + strBaseUserType+"--strUserType="+strUserType);
		request.setAttribute(TITLE, "My Work");
		request.setAttribute(PAGE, "/jsp/task/MyWorks.jsp");
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-table\"></i><a href=\"MyWork.action\" style=\"color: #3c8dbc;\"> My Work</a></li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		/*if(strBaseUserType==null || (strBaseUserType!=null && strBaseUserType.equalsIgnoreCase(EMPLOYEE))) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied); 
			return ACCESS_DENIED;
		}*/
		checkProjectOwner(uF);
		
		return LOAD;
	}
	
	
	
	private void checkProjectOwner(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			boolean tlOrPoFlag = false;
			pst = con.prepareStatement("select emp_id from project_emp_details where emp_id=? and _isteamlead=true");
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
//			System.out.println("pst =====> "+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				tlOrPoFlag = true;
			}
			rs.close();
			pst.close();
			
	//===start parvez date: 13-10-2022===		
//			pst = con.prepareStatement("select project_owner from projectmntnc where project_owner = ?");
//			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst = con.prepareStatement("select project_owners from projectmntnc where project_owners like '%,"+(String)session.getAttribute(EMPID)+",%'");
	//===end parvez date: 13-10-2022===		
//			System.out.println("pst =====> "+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				tlOrPoFlag = true;
			}
			rs.close();
			pst.close();
			
			if(!tlOrPoFlag) {
			//===start parvez date: 29-11-2022===	
//				pst = con.prepareStatement("select emp_id from user_details where emp_id=? and usertype_id=4");
				pst = con.prepareStatement("select emp_id from user_details where emp_id=? and (usertype_id=4 OR usertype_id=2)");
			//===end parvez date: 29-11-2022===	
				pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
//				System.out.println("pst =====> "+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					tlOrPoFlag = true;
				}
				rs.close();
				pst.close();
			}
			
			request.setAttribute("tlOrPoFlag", ""+tlOrPoFlag);
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
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

	public String getCallFrom() {
		return callFrom;
	}

	public void setCallFrom(String callFrom) {
		this.callFrom = callFrom;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

}
