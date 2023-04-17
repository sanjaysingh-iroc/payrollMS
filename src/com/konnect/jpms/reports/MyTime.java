package com.konnect.jpms.reports;

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

public class MyTime extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	
	String callFrom;
	String strSessionEmpId;
	String strAction;
	String strBaseUserType = null;
	
	private String alertID;		//added by parvez date: 06-09-20222
	
	public String execute() throws Exception {
      
		session = request.getSession(); 
		strSessionEmpId = (String) session.getAttribute(EMPID);//Created by dattatray15-06-2022
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);//Created By Dattatray 15-06-2022
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		  
		request.setAttribute(TITLE, "My Time");
		request.setAttribute(PAGE, "/jsp/reports/MyTime.jsp");
		
		if(strUserType==null || (strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE))) {
			loadPageVisitAuditTrail();//Created By Dattatray 15-06-2022
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied); 
			return ACCESS_DENIED;
		}
		
		//Created By Dattatray 15-06-2022
				strAction = request.getServletPath();
				if(strAction!=null) {
					strAction = strAction.replace("/","");
				}
				
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-clock-o\"></i><a href=\"MyTime.action\" style=\"color: #3c8dbc;\"> My Time</a></li>");
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
				Map<String, String> hmEmpProfile = CF.getEmpNameMap(con,null,strSessionEmpId);
				StringBuilder builder = new StringBuilder();
				builder.append(hmEmpProfile.get(strSessionEmpId) +" tring to accessed "+strAction);
				
				CF.pageVisitAuditTrail(con,CF,uF, strSessionEmpId, strAction, strBaseUserType, builder.toString());
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
