package com.konnect.jpms.leave;

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

public class Absence extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	
	String callFrom;
	
	String strEmpId = null;
	String strBaseUserType = null;
	private String strAction = null;
	
	public String execute() throws Exception {
      
		session = request.getSession(); 
		strEmpId = (String) session.getAttribute(EMPID);//Created By Dattatray 14-06-2022
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);//Created By Dattatray 14-04-2022
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		  
		request.setAttribute(TITLE, "Absence");
		request.setAttribute(PAGE, "/jsp/leave/Absence.jsp");
		
		//Created By Dattatray 14-06-2022
		strAction = request.getServletPath();
		if(strAction!=null) {
			strAction = strAction.replace("/","");
		}
		if(strUserType==null || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(ACCOUNTANT) && !strUserType.equalsIgnoreCase(RECRUITER)
			&& !strUserType.equalsIgnoreCase(MANAGER) && !strUserType.equalsIgnoreCase(HRMANAGER) && !strUserType.equalsIgnoreCase(CEO))) {
			loadPageVisitAuditTrail();//Created By Dattatray 14-06-2022
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied); 
			return ACCESS_DENIED;
		}
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-umbrella\"></i><a href=\"Absence.action\" style=\"color: #3c8dbc;\"> Absence</a></li>" +
			"<li class=\"active\">Absence</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		return LOAD;
	}
	
	//Created By Dattatray 14-06-2022
	private void loadPageVisitAuditTrail() {
		Connection con=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpProfile = CF.getEmpNameMap(con,null,strEmpId);
			StringBuilder builder = new StringBuilder();
			builder.append("Alert: "+hmEmpProfile.get(strEmpId) +" trying to access "+strAction);
			
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

}