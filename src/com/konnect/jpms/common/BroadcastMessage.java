package com.konnect.jpms.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class BroadcastMessage extends ActionSupport implements ServletRequestAware, IStatements {
 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	private static Logger log = Logger.getLogger(BroadcastMessage.class);
	
	public String execute() throws Exception {
		
		session = request.getSession(true);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)
			return LOGIN;
		request.setAttribute(PAGE, PBroadcastMessage);
		request.setAttribute(TITLE, TBroadcastMessage);
		
		String strUserType = (String)session.getAttribute(USERTYPE);
		String strSessionEmpId = (String)session.getAttribute(EMPID);

		if(strUserType== null || (strUserType!= null && strUserType.equalsIgnoreCase(EMPLOYEE))){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		empList = new FillEmployee(request).fillEmployeeName(strUserType, strSessionEmpId);
		
		if(getStrTitle()!=null){
			sendMessage(strSessionEmpId);
		}
		
		loadBroadcastMessage();
		return SUCCESS;
		
	}
	  
	public void loadBroadcastMessage(){
		setStrTitle(null);
		setStrMessage(null);
		setStrEmpId(null);
	}
	
	List<FillEmployee> empList;
	String strTitle;
	String strMessage;
	List strEmpId;
	
	public void sendMessage(String strSessionEmpId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpEmail = CF.getEmpEmailMap(con);
			
			pst = con.prepareStatement("insert into broadcast_desk (heading, body, entry_date, emp_id, to_emp_id) values (?,?,?,?,?)");
			pst.setString(1, getStrTitle());
			pst.setString(2, getStrMessage());
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setString(5, ((getStrEmpId()!=null)?getStrEmpId().toString():""));
			pst.execute();
			pst.close();
			
			
			
			
			String strDomain = request.getServerName().split("\\.")[0];
			Notifications nF = new Notifications(0, CF); 
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			nF.setStrEmailFrom(hmEmpEmail.get(strSessionEmpId));
			
			for(int i=0; getStrEmpId()!=null && i<getStrEmpId().size(); i++){
				nF.setStrEmailTo(hmEmpEmail.get(getStrEmpId().get(i)));
				nF.setStrEmailSubject(getStrTitle());
				nF.setStrEmailBody(getStrMessage());
				nF.setEmailTemplate(true);
				nF.sendNotifications();
			}
			
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
 
	
	
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrTitle() {
		return strTitle;
	}

	public void setStrTitle(String strTitle) {
		this.strTitle = strTitle;
	}

	public String getStrMessage() {
		return strMessage;
	}

	public void setStrMessage(String strMessage) {
		this.strMessage = strMessage;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public List getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(List strEmpId) {
		this.strEmpId = strEmpId;
	}

}