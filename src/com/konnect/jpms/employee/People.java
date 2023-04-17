package com.konnect.jpms.employee;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class People extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strBaseUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	
	String callFrom;
	private String alertID;
	
	String strEmpId = null;
	private String strAction = null;
	
	public String execute() throws Exception {
      
		session = request.getSession(); 
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strEmpId = (String) session.getAttribute(EMPID);//Created By Dattatray 15-06-2022
		request.setAttribute(TITLE, "People");
		request.setAttribute(PAGE, "/jsp/employee/People.jsp");
		
		//Created By Dattatray 15-06-2022
				strAction = request.getServletPath();
				if(strAction!=null) {
					strAction = strAction.replace("/","");
				}
	
	//===start parvez date 09-02-2023===			
		if(strBaseUserType!=null && !strBaseUserType.equalsIgnoreCase(ADMIN) && !strBaseUserType.equalsIgnoreCase(ACCOUNTANT) 
			&& !strBaseUserType.equalsIgnoreCase(HRMANAGER) && !strBaseUserType.equalsIgnoreCase(CEO) && !strBaseUserType.equalsIgnoreCase(RECRUITER) && !strBaseUserType.equalsIgnoreCase(OTHER_HR)) { // && !strBaseUserType.equalsIgnoreCase(MANAGER) && !strBaseUserType.equalsIgnoreCase(HOD)
			loadPageVisitAuditTrail();//Created By Dattatray 15-06-2022
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied); 
			return ACCESS_DENIED;
		}
	//===end parvez date 09-02-2023===	
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-group\"></i><a href=\"People.action\" style=\"color: #3c8dbc;\"> People</a></li>" +
			"<li class=\"active\">People</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		return LOAD;
	}
	
	//Created By Dattatray 15-06-2022
	private void loadPageVisitAuditTrail() {
		Connection con=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpProfile = CF.getEmpNameMap(con,null,strEmpId);
			StringBuilder builder = new StringBuilder();
			builder.append("Alert : "+hmEmpProfile.get(strEmpId) +" trying to access "+strAction);
			
			
			
			CF.pageVisitAuditTrail(con,CF,uF, strEmpId, strAction, strBaseUserType, builder.toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
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

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

}