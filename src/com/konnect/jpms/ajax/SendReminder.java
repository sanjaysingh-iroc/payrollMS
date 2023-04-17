package com.konnect.jpms.ajax;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.INotificationConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SendReminder extends ActionSupport implements IStatements, ServletRequestAware {

	
	String strEmpId;
	String strSessionEmpId;
	String strNotificationId;
	HttpSession session;
	CommonFunctions CF;
	
	String type;
	private String mailType;
	
	public String execute()	{
		
		session = request.getSession();
		CF= (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		System.out.println("getMailType() ===>> " +getMailType());
		if(getMailType() != null && getMailType().equals("ITR_FILLING_MAIL")) {
			sendITRFillingRequest();
		} else {
			sendReminder();
		}
		return SUCCESS;

	}

	private String sendITRFillingRequest() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);

			String strLevelId = CF.getEmpLevelId(con, strSessionEmpId
					);
			Map<String, Map<String, String>> hmInnerActualCTC = CF.getSalaryCalculation(con, uF.parseToInt(strSessionEmpId), 30, 0, 0, 30, 0, 0, strLevelId, uF, CF, uF.getCurrentDate(CF.getStrTimeZone())+"");
			
			double salaryGross = 0;
			double salaryDeduction = 0;
			Iterator<String> it = hmInnerActualCTC.keySet().iterator();
			while (it.hasNext()) {
				String strSalaryId = it.next();

				Map<String, String> hm = hmInnerActualCTC.get(strSalaryId);
				if (hm.get("EARNING_DEDUCTION").equals("E")) {
					salaryGross += uF.parseToDouble(hm.get("AMOUNT"));
				} else if (hm.get("EARNING_DEDUCTION").equals("D")) {
					salaryDeduction += uF.parseToDouble(hm.get("AMOUNT"));
				}
			}
			salaryGross = salaryGross * 12;
			
			String strDomain = request.getServerName().split("\\.")[0];
			Notifications nF = new Notifications(N_ITR_FILLING_REQUEST, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			nF.setStrEmpId(strSessionEmpId);
			nF.setEmailTemplate(true);
			
			nF.setStrEmpEmail("rahul.patil@workrig.com");
			nF.setStrEmailTo("rahul.patil@workrig.com");
			nF.setStrSalaryAmount(uF.formatIntoTwoDecimal(salaryGross));
			
			nF.sendNotifications();
			
			request.setAttribute("STATUS_MSG", SUCCESSM+"Your ITR Filling request is sent successfully. Our consultant will contact you."+END);
			
			/*pst = con.prepareStatement("select * from settings where options=?");
			pst.setString(1, "EMAIL_NOTIFICATIONS");
			rs = pst.executeQuery();
			boolean isEmailNotification = false;
			while(rs.next()){
				isEmailNotification = uF.parseToBoolean(rs.getString("value"));
			}
            rs.close();
            pst.close();*/
			
		} catch (Exception e) {
			request.setAttribute("STATUS_MSG",	"Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return "comment";

	}

	public String sendReminder() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from settings where options=?");
			pst.setString(1, "EMAIL_NOTIFICATIONS");
			rs = pst.executeQuery();
			boolean isEmailNotification = false;
			while(rs.next()){
				isEmailNotification = uF.parseToBoolean(rs.getString("value"));
			}
            rs.close();
            pst.close();
			
			
			String strSessionId = session.getId();
			
			if(isEmailNotification && uF.parseToInt(getStrNotificationId())== INotificationConstants.N_NEW_EMPLOYEE_JOINING){
				strSessionId = URLEncoder.encode(strSessionId);
				pst = con.prepareStatement("update employee_personal_details set session_id =?, _timestamp=?, added_by=? where emp_per_id=?");
				pst.setString(1, strSessionId);
				pst.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.setInt(3, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setInt(4, uF.parseToInt(getStrEmpId()));
				pst.execute();
	            pst.close();
			}
			
			
			if(isEmailNotification){
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(uF.parseToInt(getStrNotificationId()), CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrEmpId(getStrEmpId());
				if(getType()!=null && getType().equals("people")){
					
					nF.request = request;
					nF.setStrOrgId((String)session.getAttribute(ORGID));
					nF.setEmailTemplate(true);					
					nF.setStrAddPeopleLink("?empId="+getStrEmpId()+"&sessionId="+strSessionId);
				} else {
					nF.setStrAddEmpLink("?empId="+getStrEmpId()+"&sessionId="+strSessionId);
				}
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setEmailTemplate(true);
				nF.sendNotifications();
				
				 
				pst = con.prepareStatement("INSERT INTO reminder_details(_date, emp_id, reminder_type) values(?,?,?)");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(2, uF.parseToInt(getStrEmpId()));
				pst.setString(3, getStrNotificationId());
				pst.execute();
	            pst.close();
				
				 /*request.setAttribute("STATUS_MSG",	"<img src=\"images1/tick.png\" title=\"Reminder Sent\">");*/
	            request.setAttribute("STATUS_MSG",	"<i class=\"fa fa-check checknew\" aria-hidden=\"true\"></i>");
	            
			}else{
				request.setAttribute("STATUS_MSG",	"Notification is disabled");
			}
			
			
			

		} catch (Exception e) {
			request.setAttribute("STATUS_MSG",	"Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return "comment";

	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getStrNotificationId() {
		return strNotificationId;
	}

	public void setStrNotificationId(String strNotificationId) {
		this.strNotificationId = strNotificationId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMailType() {
		return mailType;
	}

	public void setMailType(String mailType) {
		this.mailType = mailType;
	}

}