package com.konnect.jpms.requsitions;

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

public class TeamRequests extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strBaseUserType = null;
	String strUserTypeId = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	String alertID;
	String callFrom;
	private String currUserType;
	String strEmpId = null;
	private String strAction = null;
	public String execute() throws Exception {
      
		session = request.getSession(); 
		strEmpId = (String) session.getAttribute(EMPID);//Created By Dattatray  15-06-2022
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);//Created By Dattatray 15-06-2022
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strUserType = (String) session.getAttribute(USERTYPE);
		
		strUserTypeId = (String) session.getAttribute(USERTYPEID); 
		
		request.setAttribute(TITLE, "Team Requests");
		request.setAttribute(PAGE, "/jsp/requisitions/TeamRequests.jsp");
		
		//Created By Dattatray 15-06-2022
				strAction = request.getServletPath();
				if(strAction!=null) {
					strAction = strAction.replace("/","");
				}
				
//		System.out.println("CurrentUsertype====>"+getCurrUserType());
//		System.out.println("alertID==>"+getAlertID());
		loadPageVisitAuditTrail();
		if(strUserType==null || (strUserType!=null && !strUserType.equalsIgnoreCase(MANAGER))) {
			
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied); 
			return ACCESS_DENIED;
		}
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-bell\"></i><a href=\"TeamRequests.action\" style=\"color: #3c8dbc;\"> Requests</a></li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
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

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}
}