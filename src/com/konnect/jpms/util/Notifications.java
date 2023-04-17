package com.konnect.jpms.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

/**
 * @author swara
 *
 */
public class Notifications extends Thread implements IStatements,ServletRequestAware {
	 
//	static final String CONFIGSET = "EmailTestconfiguuration";
	public Notifications(int notificationId) {
		setnNotificationCode(notificationId);
	}
	
	CommonFunctions CF;
	public Notifications(int notificationId, CommonFunctions CF) {
		setnNotificationCode(notificationId);
		this.CF = CF;
	}
	
	public HttpServletRequest request; 
	public HttpSession session;
	
	public void sendNotifications() {
		if(!isAlive()) {
			start();
		}
	}
	
	boolean isTextNotifications = false;
	boolean isEmailNotifications = false;
	boolean isEmailTemplate = false;
	
	String strOrgId;
	String strOrgImage;
	
	boolean isRequiredAuthentication;
	String strEmailAuthUsername;
	String strEmailAuthPassword;
	
	public void run(){
//	public void sendMail(){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		db.setDomain(strDomain);
		UtilityFunctions uF = new UtilityFunctions();
	

		try {
			
			con = db.makeConnection(con);
		//	System.out.println("in sendMail");
			pst = con.prepareStatement(selectSettings);
			rst = pst.executeQuery();
//			System.out.println("Not/87-- pst="+pst);
			while(rst.next()) {
				if(rst.getString("options").equalsIgnoreCase(O_EMAIL_NOTIFICATIONS)) {
					isEmailNotifications = uF.parseToBoolean(rst.getString("value"));
//					System.out.println("isEmailNotifications=="+isEmailNotifications);
				} else if(rst.getString("options").equalsIgnoreCase(O_TEXT_NOTIFICATIONS)) {
					isTextNotifications = uF.parseToBoolean(rst.getString("value"));
				} else if(rst.getString("options").equalsIgnoreCase(O_EMAIL_HOST)) {
					setStrHost(rst.getString("value"));
				} else if(rst.getString("options").equalsIgnoreCase(O_EMAIL_FROM)) {
					setStrEmailFrom(rst.getString("value"));
				} else if(rst.getString("options").equalsIgnoreCase(O_TEXT_FROM)) {
					setStrTextFrom(rst.getString("value"));
				} else if(rst.getString("options").equalsIgnoreCase(O_HOST_PASSWORD)) {
					setStrHostPassword(rst.getString("value"));
				} else if(rst.getString("options").equalsIgnoreCase(O_IS_REQUIRED_AUTHENTICATION)) {
					setIsRequiredAuthentication(uF.parseToBoolean(rst.getString("value")));
				} else if(rst.getString("options").equalsIgnoreCase(O_EMAIL_AUTHENTICATION_USER)) {
					setStrEmailAuthUsername(rst.getString("value"));
				} else if(rst.getString("options").equalsIgnoreCase(O_EMAIL_AUTHENTICATION_PASSWORD)) {
					setStrEmailAuthPassword(rst.getString("value"));
				}
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select * from org_details where org_id=?");
			pst.setInt(1, uF.parseToInt(getStrOrgId()));
			rst = pst.executeQuery();
			while(rst.next()) {
				setStrOrgImage(rst.getString("org_logo"));
			}
			rst.close();
			pst.close();
			
			backgroundImgforMail(con, uF);

//			System.out.println("before isEmailNotifications ===>> " + isEmailNotifications +" -- isTextNotifications ===>> " + isTextNotifications);
			int x = 0;
			if(isEmailNotifications || isTextNotifications) {
				x = getNotificationsMessage(con, uF);
			}
			System.out.println("isEmailNotifications ===>> " + isEmailNotifications +" -- isTextNotifications ===>> " + isTextNotifications);
			
			if(isEmailNotifications && x>0 && getStrEmailBody()!=null && !getStrEmailBody().trim().equals("") && !getStrEmailBody().trim().equalsIgnoreCase("NULL")){
				sendEmailNotifications();
			}
			
			if(isTextNotifications) {
				if(getStrTextContent()!=null && !getStrTextContent().trim().equals("") && !getStrTextContent().equalsIgnoreCase("null")) {
					CallSmscApi sms = new CallSmscApi();
					sms.sendSMSToUsers(getStrEmpMobileNo(), getStrTextContent());
				}
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private int getNotificationsMessage(Connection con,UtilityFunctions uF){
		PreparedStatement pst = null;
		ResultSet rst = null;
		int x= 0;
		try {
			
			pst = con.prepareStatement(selectNotifications);
			pst.setInt(1, getnNotificationCode()); 
			rst = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while(rst.next()){
				setStrEmailBody(rst.getString("email_notification"));
				setStrEmailSubject("Workrig Notification - "+uF.showData(rst.getString("email_subject"), ""));
				setStrTextContent(rst.getString("text_notification"));
				if(isEmailNotifications) {
				 isEmailNotifications = uF.parseToBoolean(rst.getString("isemail"));
				}
				if(isTextNotifications) {
					isTextNotifications = uF.parseToBoolean(rst.getString("istext"));
				}
				x++;
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rst!=null){
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		if(x==0){
			System.out.println("Notification constant not defined..");
			return x;
		}
		
		if((getStrEmailBody()!=null && !getStrEmailBody().trim().equals("") && !getStrEmailBody().trim().equalsIgnoreCase("NULL")) || (getStrTextContent()!=null && !getStrTextContent().trim().equals("") && !getStrTextContent().trim().equalsIgnoreCase("NULL"))) {
			parseContent(uF.showData(getStrEmailBody(), ""), uF.showData(getStrTextContent(), ""), uF.showData(getStrEmailSubject(), ""));
			setContentType("MAIL_BODY");
		}
		return x;
	}
	
	public Map<String, String> parseContent(String strEmailBody, String strTextContent, String strSubject){
		UtilityFunctions uF = new UtilityFunctions();
//		String strLink = "http://"+strHostAddress+":8080"+strContextPath;
		String strPort = strHostPort!=null && !strHostPort.equals("") ? ":"+strHostPort : "";
		String strLink = "https://"+strHostAddress+strPort; //"http://"+  -- +strContextPath
		String strLinkImg = "http://"+strHostAddress+strPort+strContextPath; //
//		System.out.println("strHostAddress ===>> " + strHostAddress +" -- strPort ===>> " + strPort +" -- strContextPath ===>> " + strContextPath);
		String strLoginAction = "/Login.action";
		String strAddEmployeeAction = "/AddEmployee.action";
		String strAddPeopleAction = "/AddPeople.action";
		String strCustRegisterAction = "/CustomerRegistration.action";
		
		if(strEmailBody!=null) {
			
			strEmailBody = strEmailBody.replace(R_LOGIN_LINK, "<a href=\""+strLink+strLoginAction+"\">"+strLink+strLoginAction+"</a>");
			strTextContent = strTextContent.replace(R_LOGIN_LINK, strLink+strLoginAction);
			
			if(getStrEmpCode()!=null && !getStrEmpCode().trim().equals("")){
				strEmailBody = strEmailBody.replace(R_EMPCODE, uF.showData(getStrEmpCode(),""));  
				strSubject = strSubject.replace(R_EMPCODE, uF.showData(getStrEmpCode(),""));
				strTextContent = strTextContent.replace(R_EMPCODE, uF.showData(getStrEmpCode(),""));
			}
			
			if(getStrSalutation()!=null){
				strEmailBody = strEmailBody.replace(R_SALUTATION, uF.showData(getStrSalutation(),""));
				strSubject = strSubject.replace(R_SALUTATION, uF.showData(getStrSalutation(),""));
				strTextContent = strTextContent.replace(R_SALUTATION, uF.showData(getStrSalutation(),""));
			}
			if(getStrEmpFname()!=null){
				strEmailBody = strEmailBody.replace(R_EMPFNAME, uF.showData(getStrEmpFname(),""));
				strSubject = strSubject.replace(R_EMPFNAME, uF.showData(getStrEmpFname(),""));
				strTextContent = strTextContent.replace(R_EMPFNAME, uF.showData(getStrEmpFname(),""));
			}
			if(getStrEmpMname()!=null){
				strEmailBody = strEmailBody.replace(R_EMPMNAME, uF.showData(getStrEmpMname(),""));
				strSubject = strSubject.replace(R_EMPMNAME, uF.showData(getStrEmpMname(),""));
				strTextContent = strTextContent.replace(R_EMPMNAME, uF.showData(getStrEmpMname(),""));
			} else {
				strEmailBody = strEmailBody.replace(R_EMPMNAME, uF.showData(getStrEmpMname(),""));
				strSubject = strSubject.replace(R_EMPMNAME, uF.showData(getStrEmpMname(),""));
				strTextContent = strTextContent.replace(R_EMPMNAME, uF.showData(getStrEmpMname(),""));
			}
			if(getStrEmpLname()!=null){
				strEmailBody = strEmailBody.replace(R_EMPLNAME, uF.showData(getStrEmpLname(),""));
				strSubject = strSubject.replace(R_EMPLNAME, uF.showData(getStrEmpLname(),""));
				strTextContent = strTextContent.replace(R_EMPLNAME, uF.showData(getStrEmpLname(),""));
			}
			
			if(getStrEmpEmailID()!=null){
				strEmailBody = strEmailBody.replace(R_EMP_EMAIL_ID, uF.showData(getStrEmpEmailID(),""));
				strSubject = strSubject.replace(R_EMP_EMAIL_ID, uF.showData(getStrEmpEmailID(),""));
				strTextContent = strTextContent.replace(R_EMP_EMAIL_ID, uF.showData(getStrEmpEmailID(),""));
			}
			if(getStrEmpContactNo()!=null){
				strEmailBody = strEmailBody.replace(R_EMP_CONTACT_NO, uF.showData(getStrEmpContactNo(),""));
				strSubject = strSubject.replace(R_EMP_CONTACT_NO, uF.showData(getStrEmpContactNo(),""));
				strTextContent = strTextContent.replace(R_EMP_CONTACT_NO, uF.showData(getStrEmpContactNo(),""));
			}
			if(getStrEmpPancardNo()!=null){
				strEmailBody = strEmailBody.replace(R_EMP_PANCARD_NO, uF.showData(getStrEmpPancardNo(),""));
				strSubject = strSubject.replace(R_EMP_PANCARD_NO, uF.showData(getStrEmpPancardNo(),""));
				strTextContent = strTextContent.replace(R_EMP_PANCARD_NO, uF.showData(getStrEmpPancardNo(),""));
			}
			if(getStrEmpDateOfBirth()!=null){
//				System.out.println("NF/270--getStrEmpDateOfBirth="+getStrEmpDateOfBirth());
				strEmailBody = strEmailBody.replace(R_EMP_DATE_OF_BIRTH, uF.showData(getStrEmpDateOfBirth(),""));
				strSubject = strSubject.replace(R_EMP_DATE_OF_BIRTH, uF.showData(getStrEmpDateOfBirth(),""));
				strTextContent = strTextContent.replace(R_EMP_DATE_OF_BIRTH, uF.showData(getStrEmpDateOfBirth(),""));
			}
			if(getStrEmpEmergencyContactDetails()!=null){
				strEmailBody = strEmailBody.replace(R_EMP_EMERGENCY_CONTACT_DETAILS, uF.showData(getStrEmpEmergencyContactDetails(),""));
				strSubject = strSubject.replace(R_EMP_EMERGENCY_CONTACT_DETAILS, uF.showData(getStrEmpEmergencyContactDetails(),""));
				strTextContent = strTextContent.replace(R_EMP_EMERGENCY_CONTACT_DETAILS, uF.showData(getStrEmpEmergencyContactDetails(),""));
			}
			
			if(getStrEmpAddress()!=null){
				strEmailBody = strEmailBody.replace(R_EMPADDRESS, uF.showData(getStrEmpAddress(),""));
				strSubject = strSubject.replace(R_EMPADDRESS, uF.showData(getStrEmpAddress(),""));
				strTextContent = strTextContent.replace(R_EMPADDRESS, uF.showData(getStrEmpAddress(),""));
			}
			if(getStrEmpDesignation()!=null){
				strEmailBody = strEmailBody.replace(R_DESIGNATION, uF.showData(getStrEmpDesignation(),""));
				strSubject = strSubject.replace(R_DESIGNATION, uF.showData(getStrEmpDesignation(),""));
				strTextContent = strTextContent.replace(R_DESIGNATION, uF.showData(getStrEmpDesignation(),""));
			}
			if(getStrEmpLevel()!=null){
				strEmailBody = strEmailBody.replace(R_LEVEL, uF.showData(getStrEmpLevel(),""));
				strSubject = strSubject.replace(R_LEVEL, uF.showData(getStrEmpLevel(),""));
				strTextContent = strTextContent.replace(R_LEVEL, uF.showData(getStrEmpLevel(),""));
			}
			if(getStrEmpGrade()!=null){
				strEmailBody = strEmailBody.replace(R_GRADE, uF.showData(getStrEmpGrade(),""));
				strSubject = strSubject.replace(R_GRADE, uF.showData(getStrEmpGrade(),""));
				strTextContent = strTextContent.replace(R_GRADE, uF.showData(getStrEmpGrade(),""));
			}
			if(getStrEmpWLocation()!=null){
				strEmailBody = strEmailBody.replace(R_WLOCATION, uF.showData(getStrEmpWLocation(),""));
				strSubject = strSubject.replace(R_WLOCATION, uF.showData(getStrEmpWLocation(),""));
				strTextContent = strTextContent.replace(R_WLOCATION, uF.showData(getStrEmpWLocation(),""));
			}
			if(getStrUserName()!=null){
				strEmailBody = strEmailBody.replace(R_USERNAME, uF.showData(getStrUserName(),""));
				strSubject = strSubject.replace(R_USERNAME, uF.showData(getStrUserName(),""));
				strTextContent = strTextContent.replace(R_USERNAME, uF.showData(getStrUserName(),""));
			}
			if(getStrPassword()!=null){
				strEmailBody = strEmailBody.replace(R_PASSWORD, uF.showData(getStrPassword(),""));
				strSubject = strSubject.replace(R_PASSWORD, uF.showData(getStrPassword(),""));
				strTextContent = strTextContent.replace(R_PASSWORD, uF.showData(getStrPassword(),""));
			}
			
			if(getStrEmpLeaveFrom()!=null) {
				strEmailBody = strEmailBody.replace(R_LEAVE_FROM, uF.showData(getStrEmpLeaveFrom(),""));
				strSubject = strSubject.replace(R_LEAVE_FROM, uF.showData(getStrEmpLeaveFrom(),""));
				strTextContent = strTextContent.replace(R_LEAVE_FROM, uF.showData(getStrEmpLeaveFrom(),""));
			}
			
			if(getStrEmpLeaveTo()!=null){
				strEmailBody = strEmailBody.replace(R_LEAVE_TO, uF.showData(getStrEmpLeaveTo(),""));
				strSubject = strSubject.replace(R_LEAVE_TO, uF.showData(getStrEmpLeaveTo(),""));
				strTextContent = strTextContent.replace(R_LEAVE_TO, uF.showData(getStrEmpLeaveTo(),""));
			}
			if(getStrEmpLeaveNoOfDays()!=null){
				strEmailBody = strEmailBody.replace(R_LEAVE_NO_DAYS, uF.showData(""+getStrEmpLeaveNoOfDays(),""));
				strSubject = strSubject.replace(R_LEAVE_NO_DAYS, uF.showData(getStrEmpLeaveNoOfDays(),""));
				strTextContent = strTextContent.replace(R_LEAVE_NO_DAYS, uF.showData(getStrEmpLeaveNoOfDays(),""));
			}
			if(getStrEmpLeaveReason()!=null){
				strEmailBody = strEmailBody.replace(R_LEAVE_REASON, uF.showData(getStrEmpLeaveReason(),""));
				strSubject = strSubject.replace(R_LEAVE_REASON, uF.showData(getStrEmpLeaveReason(),""));
				strTextContent = strTextContent.replace(R_LEAVE_REASON, uF.showData(getStrEmpLeaveReason(),""));
			}
		//===start parvez date: 18-03-2023===
			if(getStrLeaveEmpBackup()!=null){
				strEmailBody = strEmailBody.replace(R_BACKUP_EMP_LEAVE, uF.showData(getStrLeaveEmpBackup(),""));
				strSubject = strSubject.replace(R_BACKUP_EMP_LEAVE, uF.showData(getStrLeaveEmpBackup(),""));
				strTextContent = strTextContent.replace(R_BACKUP_EMP_LEAVE, uF.showData(getStrLeaveEmpBackup(),""));
			}
		//===end parvez date: 18-03-2023===	
			
			if(getStrSupervisorName()!=null){
				strEmailBody = strEmailBody.replace(R_MGRNAME, uF.showData(getStrSupervisorName(),""));
				strSubject = strSubject.replace(R_MGRNAME, uF.showData(getStrSupervisorName(),""));
				strTextContent = strTextContent.replace(R_MGRNAME, uF.showData(getStrSupervisorName(),""));
			}
			if(getStrManagerName()!=null){
				strEmailBody = strEmailBody.replace(R_MGRNAME, uF.showData(getStrManagerName(),""));
				strSubject = strSubject.replace(R_MGRNAME, uF.showData(getStrManagerName(),""));
				strTextContent = strTextContent.replace(R_MGRNAME, uF.showData(getStrManagerName(),""));
			}			
			if(getStrLeaveCancelReason()!=null){
				strEmailBody = strEmailBody.replace(R_MGR_COMMENT, uF.showData(getStrLeaveCancelReason(),""));
				strSubject = strSubject.replace(R_MGR_COMMENT, uF.showData(getStrLeaveCancelReason(),""));
				strTextContent = strTextContent.replace(R_MGR_COMMENT, uF.showData(getStrLeaveCancelReason(),""));
			}
			if(getStrManagerLeaveReason()!=null){
				strEmailBody = strEmailBody.replace(R_MGR_COMMENT, uF.showData(getStrManagerLeaveReason(),""));
				strSubject = strSubject.replace(R_MGR_COMMENT, uF.showData(getStrManagerLeaveReason(),""));
				strTextContent = strTextContent.replace(R_MGR_COMMENT, uF.showData(getStrManagerLeaveReason(),""));
			}
			if(getStrLeaveTypeName()!=null){
				strEmailBody = strEmailBody.replace(R_LEAVE_TYPE, uF.showData(getStrLeaveTypeName(),""));
				strSubject = strSubject.replace(R_LEAVE_TYPE, uF.showData(getStrLeaveTypeName(),""));
				strTextContent = strTextContent.replace(R_LEAVE_TYPE, uF.showData(getStrLeaveTypeName(),""));
			}
			if(getStrApprvedDenied()!=null){
				strEmailBody = strEmailBody.replace(R_APPR_DENY, uF.showData(getStrApprvedDenied(),""));
				strSubject = strSubject.replace(R_APPR_DENY, uF.showData(getStrApprvedDenied(),""));
				strTextContent = strTextContent.replace(R_APPR_DENY, uF.showData(getStrApprvedDenied(),""));
			}
			
			if(getStrEmpReimbursementFrom()!=null){
				strEmailBody = strEmailBody.replace(RMB_FROM, uF.showData(getStrEmpReimbursementFrom(),""));
				strSubject = strSubject.replace(RMB_FROM, uF.showData(getStrEmpReimbursementFrom(),""));
				strTextContent = strTextContent.replace(RMB_FROM, uF.showData(getStrEmpReimbursementFrom(),""));
			}
			if(getStrEmpReimbursementTo()!=null){
				strEmailBody = strEmailBody.replace(RMB_TO, uF.showData(getStrEmpReimbursementTo(),""));
				strSubject = strSubject.replace(RMB_TO, uF.showData(getStrEmpReimbursementTo(),""));
				strTextContent = strTextContent.replace(RMB_TO, uF.showData(getStrEmpReimbursementTo(),""));
			}
			if(getStrEmpReimbursementPurpose()!=null){
				strEmailBody = strEmailBody.replace(RMB_PURPOSE, uF.showData(getStrEmpReimbursementPurpose(),""));
				strSubject = strSubject.replace(RMB_PURPOSE, uF.showData(getStrEmpReimbursementPurpose(),""));
				strTextContent = strTextContent.replace(RMB_PURPOSE, uF.showData(getStrEmpReimbursementPurpose(),""));
			}
			if(getStrEmpReimbursementType() !=null){
				strEmailBody = strEmailBody.replace(RMB_TYPE, uF.showData(getStrEmpReimbursementType(),""));
				strSubject = strSubject.replace(RMB_TYPE, uF.showData(getStrEmpReimbursementType(),""));
				strTextContent = strTextContent.replace(RMB_TYPE, uF.showData(getStrEmpReimbursementType(),""));
			}
			if(getStrEmpReimbursementAmount()!=null){
				strEmailBody = strEmailBody.replace(RMB_AMOUNT, uF.showData(getStrEmpReimbursementAmount(),""));
				strSubject = strSubject.replace(RMB_AMOUNT, uF.showData(getStrEmpReimbursementAmount(),""));
				strTextContent = strTextContent.replace(RMB_AMOUNT, uF.showData(getStrEmpReimbursementAmount(),""));
			}
			if(getStrEmpReimbursementDate()!=null){
				strEmailBody = strEmailBody.replace(RMB_DATE, uF.showData(getStrEmpReimbursementDate(),""));
				strSubject = strSubject.replace(RMB_DATE, uF.showData(getStrEmpReimbursementDate(),""));
				strTextContent = strTextContent.replace(RMB_DATE, uF.showData(getStrEmpReimbursementDate(),""));
			}
			if(getStrEmpReimbursementCurrency()!=null){
				strEmailBody = strEmailBody.replace(RMB_CURRENCY, uF.showData(getStrEmpReimbursementCurrency(),""));
				strSubject = strSubject.replace(RMB_CURRENCY, uF.showData(getStrEmpReimbursementCurrency(),""));
				strTextContent = strTextContent.replace(RMB_CURRENCY, uF.showData(getStrEmpReimbursementCurrency(),""));
			}
			
			if(getStrEmpReqPurpose()!=null){
				strEmailBody = strEmailBody.replace(REQ_PURPOSE, uF.showData(getStrEmpReqPurpose(),""));
				strSubject = strSubject.replace(REQ_PURPOSE, uF.showData(getStrEmpReqPurpose(),""));
				strTextContent = strTextContent.replace(REQ_PURPOSE, uF.showData(getStrEmpReqPurpose(),""));
			}
			if(getStrEmpReqType() !=null){
				strEmailBody = strEmailBody.replace(REQ_TYPE, uF.showData(getStrEmpReqType(),""));
				strSubject = strSubject.replace(REQ_TYPE, uF.showData(getStrEmpReqType(),""));
				strTextContent = strTextContent.replace(REQ_TYPE, uF.showData(getStrEmpReqType(),""));
			}
			if(getStrEmpReqMode()!=null){
				strEmailBody = strEmailBody.replace(REQ_MODE, uF.showData(getStrEmpReqMode(),""));
				strSubject = strSubject.replace(REQ_MODE, uF.showData(getStrEmpReqMode(),""));
				strTextContent = strTextContent.replace(REQ_MODE, uF.showData(getStrEmpReqMode(),""));
			}
			if(getStrEmpReqFrom()!=null){
				strEmailBody = strEmailBody.replace(REQ_FROM, uF.showData(getStrEmpReqFrom(),""));
				strSubject = strSubject.replace(REQ_FROM, uF.showData(getStrEmpReqFrom(),""));
				strTextContent = strTextContent.replace(REQ_FROM, uF.showData(getStrEmpReqFrom(),""));
			}
			if(getStrEmpReqTo()!=null){
				strEmailBody = strEmailBody.replace(REQ_TO, uF.showData(getStrEmpReqTo(),""));
				strSubject = strSubject.replace(REQ_TO, uF.showData(getStrEmpReqTo(),""));
				strTextContent = strTextContent.replace(REQ_TO, uF.showData(getStrEmpReqTo(),""));
			}
			if(getStrPassword()!=null){
				strEmailBody = strEmailBody.replace(R_PASSWORD, uF.showData(getStrPassword(),""));
				strSubject = strSubject.replace(R_PASSWORD, uF.showData(getStrPassword(),""));
				strTextContent = strTextContent.replace(R_PASSWORD, uF.showData(getStrPassword(),""));
			}
			if(getStrNewPassword()!=null){
				strEmailBody = strEmailBody.replace(R_NEW_PASSWORD, uF.showData(getStrNewPassword(),""));
				strSubject = strSubject.replace(R_NEW_PASSWORD, uF.showData(getStrNewPassword(),""));
				strTextContent = strTextContent.replace(R_NEW_PASSWORD, uF.showData(getStrNewPassword(),""));
			}
			
			if(getStrAnnouncementHeading()!=null){
				strEmailBody = strEmailBody.replace(AN_HEADING, uF.showData(getStrAnnouncementHeading(),""));
				strSubject = strSubject.replace(AN_HEADING, uF.showData(getStrAnnouncementHeading(),""));
				strTextContent = strTextContent.replace(AN_HEADING, uF.showData(getStrAnnouncementHeading(),""));
			}
			if(getStrAnnouncementBody()!=null){
				strEmailBody = strEmailBody.replace(AN_BODY, uF.showData(getStrAnnouncementBody(),""));
				strSubject = strSubject.replace(AN_BODY, uF.showData(getStrAnnouncementBody(),""));
				strTextContent = strTextContent.replace(AN_BODY, uF.showData(getStrAnnouncementBody(),""));
			}
			if(getStrNewEmailFrom()!=null){
				strEmailBody = strEmailBody.replace(MAIL_FROM, uF.showData(getStrNewEmailFrom(),""));
				strSubject = strSubject.replace(MAIL_FROM, uF.showData(getStrNewEmailFrom(),""));
				strTextContent = strTextContent.replace(MAIL_FROM, uF.showData(getStrNewEmailFrom(),""));
			}
			if(getStrNewEmailTo()!=null){
				strEmailBody = strEmailBody.replace(MAIL_TO, uF.showData(getStrNewEmailTo(),""));
				strSubject = strSubject.replace(MAIL_TO, uF.showData(getStrNewEmailTo(),""));
				strTextContent = strTextContent.replace(MAIL_TO, uF.showData(getStrNewEmailTo(),""));
			}
			if(getStrNewEmailSubject()!=null){
				strEmailBody = strEmailBody.replace(MAIL_SUBJECT, uF.showData(getStrNewEmailSubject(),""));
				strSubject = strSubject.replace(MAIL_SUBJECT, uF.showData(getStrNewEmailSubject(),""));
				strTextContent = strTextContent.replace(MAIL_SUBJECT, uF.showData(getStrNewEmailSubject(),""));
			}
			if(getStrNewEmailBody()!=null){
				strEmailBody = strEmailBody.replace(MAIL_BODY, uF.showData(getStrNewEmailBody(),""));
				strSubject = strSubject.replace(MAIL_BODY, uF.showData(getStrNewEmailBody(),""));
				strTextContent = strTextContent.replace(MAIL_BODY, uF.showData(getStrNewEmailBody(),""));
			}
			if(getStrSalaryAmount()!=null){
				strEmailBody = strEmailBody.replace(SAL_AMOUNT, uF.showData(getStrSalaryAmount(),""));
				strSubject = strSubject.replace(SAL_AMOUNT, uF.showData(getStrSalaryAmount(),""));
				strTextContent = strTextContent.replace(SAL_AMOUNT, uF.showData(getStrSalaryAmount(),""));
			}
			if(getStrTextSalaryAmount()!=null){
				strEmailBody = strEmailBody.replace(TEXT_SAL_AMOUNT, uF.showData(getStrTextSalaryAmount(),""));
				strSubject = strSubject.replace(TEXT_SAL_AMOUNT, uF.showData(getStrTextSalaryAmount(),""));
				strTextContent = strTextContent.replace(TEXT_SAL_AMOUNT, uF.showData(getStrTextSalaryAmount(),""));
			}
			if(getStrPaycycle()!=null){
				strEmailBody = strEmailBody.replace(PAYCYCLE, uF.showData(getStrPaycycle(),""));
				strSubject = strSubject.replace(PAYCYCLE, uF.showData(getStrPaycycle(),""));
				strTextContent = strTextContent.replace(PAYCYCLE, uF.showData(getStrPaycycle(),""));
			}
			if(getStrAccountNo()!=null){
				strEmailBody = strEmailBody.replace(ACC_NO, uF.showData(getStrAccountNo(),""));
				strSubject = strSubject.replace(ACC_NO, uF.showData(getStrAccountNo(),""));
				strTextContent = strTextContent.replace(ACC_NO, uF.showData(getStrAccountNo(),""));
			}
			if(getStrAddEmpLink()!=null){
				strEmailBody = strEmailBody.replace(ADD_EMP_LINK, "<a href=\""+strLink+strAddEmployeeAction+uF.showData(getStrAddEmpLink(),"")+"\">"+strLink+strAddEmployeeAction+uF.showData(getStrAddEmpLink(),"")+"</a>");
				strSubject = strSubject.replace(ADD_EMP_LINK, "<a href=\""+strLink+strAddEmployeeAction+uF.showData(getStrAddEmpLink(),"")+"\">"+strLink+strAddEmployeeAction+uF.showData(getStrAddEmpLink(),"")+"</a>");
				strTextContent = strTextContent.replace(ADD_EMP_LINK, strLink+strAddEmployeeAction+uF.showData(getStrAddEmpLink(),""));
			}
			if(getStrDate()!=null){
				strEmailBody = strEmailBody.replace(DATE, uF.showData(getStrDate(),""));
				strSubject = strSubject.replace(DATE, uF.showData(getStrDate(),""));
				strTextContent = strTextContent.replace(DATE, uF.showData(getStrDate(),""));
			}
			
			if(getStrJoiningDate()!=null){
				strEmailBody = strEmailBody.replace(JOINING_DATE, uF.showData(getStrJoiningDate(),""));
				strSubject = strSubject.replace(JOINING_DATE, uF.showData(getStrJoiningDate(),""));
				strTextContent = strTextContent.replace(JOINING_DATE, uF.showData(getStrJoiningDate(),""));
			}
			
			if(getStrEmpCTC()!=null){
				strEmailBody = strEmailBody.replace(EMP_CTC, uF.showData(getStrEmpCTC(),""));
				strSubject = strSubject.replace(EMP_CTC, uF.showData(getStrEmpCTC(),""));
				strTextContent = strTextContent.replace(EMP_CTC, uF.showData(getStrEmpCTC(),""));
			}
			
			if(getStrEmpKRAs()!=null){
				strEmailBody = strEmailBody.replace(EMP_KRAS, uF.showData(getStrEmpKRAs(),""));
				strSubject = strSubject.replace(EMP_KRAS, uF.showData(getStrEmpKRAs(),""));
				strTextContent = strTextContent.replace(EMP_KRAS, uF.showData(getStrEmpKRAs(),""));
			}
			
			if(getStrInvestmentAmount()!=null){
				strEmailBody = strEmailBody.replace(INVESTMENT_AMOUNT, uF.showData(getStrInvestmentAmount(),""));
				strSubject = strSubject.replace(INVESTMENT_AMOUNT, uF.showData(getStrInvestmentAmount(),""));
				strTextContent = strTextContent.replace(INVESTMENT_AMOUNT, uF.showData(getStrInvestmentAmount(),""));
			}
			
			if(getStrSectionCode()!=null){
				strEmailBody = strEmailBody.replace(SECTION_CODE, uF.showData(getStrSectionCode(),""));
				strSubject = strSubject.replace(SECTION_CODE, uF.showData(getStrSectionCode(),""));
				strTextContent = strTextContent.replace(SECTION_CODE, uF.showData(getStrSectionCode(),""));
			}
			
			
			// ---- Recruitment ---- 
			if(getStrRecruitmentDesignation()!=null){
				strEmailBody = strEmailBody.replace(RECRUITMENT_DESIG, uF.showData(getStrRecruitmentDesignation(),""));
				strSubject = strSubject.replace(RECRUITMENT_DESIG, uF.showData(getStrRecruitmentDesignation(),""));
				strTextContent = strTextContent.replace(RECRUITMENT_DESIG, uF.showData(getStrRecruitmentDesignation(),""));
			}
			
			if(getStrRecruitmentGrade()!=null){
				strEmailBody = strEmailBody.replace(RECRUITMENT_GRADE, uF.showData(getStrRecruitmentGrade(),""));
				strSubject = strSubject.replace(RECRUITMENT_GRADE, uF.showData(getStrRecruitmentGrade(),""));
				strTextContent = strTextContent.replace(RECRUITMENT_GRADE, uF.showData(getStrRecruitmentGrade(),""));
			}
			
			if(getStrRecruitmentLevel()!=null){
				strEmailBody = strEmailBody.replace(RECRUITMENT_LEVEL, uF.showData(getStrRecruitmentLevel(),""));
				strSubject = strSubject.replace(RECRUITMENT_LEVEL, uF.showData(getStrRecruitmentLevel(),""));
				strTextContent = strTextContent.replace(RECRUITMENT_LEVEL, uF.showData(getStrRecruitmentLevel(),""));
			}
			
			if(getStrRecruitmentPosition()!=null){
				strEmailBody = strEmailBody.replace(RECRUITMENT_POSITIONS, uF.showData(getStrRecruitmentPosition(),""));
				strSubject = strSubject.replace(RECRUITMENT_POSITIONS, uF.showData(getStrRecruitmentPosition(),""));
				strTextContent = strTextContent.replace(RECRUITMENT_POSITIONS, uF.showData(getStrRecruitmentPosition(),""));
			}
			
			if(getStrRecruitmentProfile()!=null){
				strEmailBody = strEmailBody.replace(RECRUITMENT_PROFILE, uF.showData(getStrRecruitmentProfile(),""));
				strSubject = strSubject.replace(RECRUITMENT_PROFILE, uF.showData(getStrRecruitmentProfile(),""));
				strTextContent = strTextContent.replace(RECRUITMENT_PROFILE, uF.showData(getStrRecruitmentProfile(),""));
			}
			
			if(getStrRecruitmentSkill()!=null){
				strEmailBody = strEmailBody.replace(RECRUITMENT_SKILL, uF.showData(getStrRecruitmentSkill(),""));
				strSubject = strSubject.replace(RECRUITMENT_SKILL, uF.showData(getStrRecruitmentSkill(),""));
				strTextContent = strTextContent.replace(RECRUITMENT_SKILL, uF.showData(getStrRecruitmentSkill(),""));
			}
			
			if(getStrRecruitmentWLocation()!=null){
				strEmailBody = strEmailBody.replace(RECRUITMENT_WLOCATION, uF.showData(getStrRecruitmentWLocation(),""));
				strSubject = strSubject.replace(RECRUITMENT_WLOCATION, uF.showData(getStrRecruitmentWLocation(),""));
				strTextContent = strTextContent.replace(RECRUITMENT_WLOCATION, uF.showData(getStrRecruitmentWLocation(),""));
			}
			
			if(getStrCandiInterviewDateTime()!=null){
				strEmailBody = strEmailBody.replace(CANDI_INTRVIEW_DATE, uF.showData(getStrCandiInterviewDateTime(),""));
				strSubject = strSubject.replace(CANDI_INTRVIEW_DATE, uF.showData(getStrCandiInterviewDateTime(),""));
				strTextContent = strTextContent.replace(CANDI_INTRVIEW_DATE, uF.showData(getStrCandiInterviewDateTime(),""));
			}
			
			if(getStrCandiUsername()!=null){
				strEmailBody = strEmailBody.replace(CANDI_USERNAME, uF.showData(getStrCandiUsername(),""));
				strSubject = strSubject.replace(CANDI_USERNAME, uF.showData(getStrCandiUsername(),""));
				strTextContent = strTextContent.replace(CANDI_USERNAME, uF.showData(getStrCandiUsername(),""));
			}
			
			if(getStrCandiPassword()!=null){
				strEmailBody = strEmailBody.replace(CANDI_PASSWORD, uF.showData(getStrCandiPassword(),""));
				strSubject = strSubject.replace(CANDI_PASSWORD, uF.showData(getStrCandiPassword(),""));
				strTextContent = strTextContent.replace(CANDI_PASSWORD, uF.showData(getStrCandiPassword(),""));
			}
			// ---- /Recruitment ----			
			
			
			//confirmation
			if(getStrEmpOrganization()!=null){
				strEmailBody = strEmailBody.replace(EMP_ORGANIZATION, uF.showData(getStrEmpOrganization(),""));
				strSubject = strSubject.replace(EMP_ORGANIZATION, uF.showData(getStrEmpOrganization(),""));
				strTextContent = strTextContent.replace(EMP_ORGANIZATION, uF.showData(getStrEmpOrganization(),""));
			}
			if(getStrEmpProbationEndDate()!=null){
				strEmailBody = strEmailBody.replace(PROBATION_END_DATE, uF.showData(getStrEmpProbationEndDate(),""));
				strSubject = strSubject.replace(PROBATION_END_DATE, uF.showData(getStrEmpProbationEndDate(),""));
				strTextContent = strTextContent.replace(PROBATION_END_DATE, uF.showData(getStrEmpProbationEndDate(),""));
			}
			
			if(getStrEmpConfirmationDate()!=null){
				strEmailBody = strEmailBody.replace(CONFIRMATION_DATE, uF.showData(getStrEmpConfirmationDate(),""));
				strSubject = strSubject.replace(CONFIRMATION_DATE, uF.showData(getStrEmpConfirmationDate(),""));
				strTextContent = strTextContent.replace(CONFIRMATION_DATE, uF.showData(getStrEmpConfirmationDate(),""));
			}
			
			if(getStrOfferAcceptanceLastDate()!=null){
				strEmailBody = strEmailBody.replace(OFFER_ACCEPTANCE_LAST_DATE, uF.showData(getStrOfferAcceptanceLastDate(),""));
				strSubject = strSubject.replace(OFFER_ACCEPTANCE_LAST_DATE, uF.showData(getStrOfferAcceptanceLastDate(),""));
				strTextContent = strTextContent.replace(OFFER_ACCEPTANCE_LAST_DATE, uF.showData(getStrOfferAcceptanceLastDate(),""));
			}
			
			
			// ---- Review ----
			
			//Review Publish
			if(getStrReviewName()!=null){
				strEmailBody = strEmailBody.replace(REVIEW_NAME, uF.showData(getStrReviewName(),""));
				strSubject = strSubject.replace(REVIEW_NAME, uF.showData(getStrReviewName(),""));
				strTextContent = strTextContent.replace(REVIEW_NAME, uF.showData(getStrReviewName(),""));
			}
			
			if(getStrRevieweeName()!=null){
				strEmailBody = strEmailBody.replace(REVIEWEE_NAME, uF.showData(getStrRevieweeName(),""));
				strSubject = strSubject.replace(REVIEWEE_NAME, uF.showData(getStrRevieweeName(),""));
				strTextContent = strTextContent.replace(REVIEWEE_NAME, uF.showData(getStrRevieweeName(),""));
			}
			
			if(getStrRoleType()!=null){
				strEmailBody = strEmailBody.replace(ROLE_TYPE, uF.showData(getStrRoleType(),""));
				strSubject = strSubject.replace(ROLE_TYPE, uF.showData(getStrRoleType(),""));
				strTextContent = strTextContent.replace(ROLE_TYPE, uF.showData(getStrRoleType(),""));
			}
			
			if(getStrFinalizerName()!=null){
				strEmailBody = strEmailBody.replace(FINALIZER_NAME, uF.showData(getStrFinalizerName(),""));
				strSubject = strSubject.replace(FINALIZER_NAME, uF.showData(getStrFinalizerName(),""));
				strTextContent = strTextContent.replace(FINALIZER_NAME, uF.showData(getStrFinalizerName(),""));
			}
			
			if(getStrGoalName()!=null){
				strEmailBody = strEmailBody.replace(GOAL_NAME, uF.showData(getStrGoalName(),""));
				strSubject = strSubject.replace(GOAL_NAME, uF.showData(getStrGoalName(),""));
				strTextContent = strTextContent.replace(GOAL_NAME, uF.showData(getStrGoalName(),""));
			}
			
			if(getStrGoalAssignerName()!=null){
				strEmailBody = strEmailBody.replace(GOAL_ASSIGNER_NAME, uF.showData(getStrGoalAssignerName(),""));
				strSubject = strSubject.replace(GOAL_ASSIGNER_NAME, uF.showData(getStrGoalAssignerName(),""));
				strTextContent = strTextContent.replace(GOAL_ASSIGNER_NAME, uF.showData(getStrGoalAssignerName(),""));
			}
			
			if(getStrKRAName()!=null){
				strEmailBody = strEmailBody.replace(KRA_NAME, uF.showData(getStrKRAName(),""));
				strSubject = strSubject.replace(KRA_NAME, uF.showData(getStrKRAName(),""));
				strTextContent = strTextContent.replace(KRA_NAME, uF.showData(getStrKRAName(),""));
			}

			if(getStrTargetName()!=null){
				strEmailBody = strEmailBody.replace(TARGET_NAME, uF.showData(getStrTargetName(),""));
				strSubject = strSubject.replace(TARGET_NAME, uF.showData(getStrTargetName(),""));
				strTextContent = strTextContent.replace(TARGET_NAME, uF.showData(getStrTargetName(),""));
			}
			
			if(getStrTargetValue()!=null){
				strEmailBody = strEmailBody.replace(TARGET_NAME, uF.showData(getStrTargetValue(),""));
				strSubject = strSubject.replace(TARGET_NAME, uF.showData(getStrTargetValue(),""));
				strTextContent = strTextContent.replace(TARGET_NAME, uF.showData(getStrTargetValue(),""));
			}
			
			if(getStrReviewStartdate()!=null){
				strEmailBody = strEmailBody.replace(REVIEW_STARTDATE, uF.showData(getStrReviewStartdate(),""));
				strSubject = strSubject.replace(REVIEW_STARTDATE, uF.showData(getStrReviewStartdate(),""));
				strTextContent = strTextContent.replace(REVIEW_STARTDATE, uF.showData(getStrReviewStartdate(),""));
			}
			
			if(getStrReviewEnddate()!=null){
				strEmailBody = strEmailBody.replace(REVIEW_ENDDATE, uF.showData(getStrReviewEnddate(),""));
				strSubject = strSubject.replace(REVIEW_ENDDATE, uF.showData(getStrReviewEnddate(),""));
				strTextContent = strTextContent.replace(REVIEW_ENDDATE, uF.showData(getStrReviewEnddate(),""));
			}
			
			if(getStrCandiFname()!=null){
				strEmailBody = strEmailBody.replace(R_CANDIFNAME, uF.showData(getStrCandiFname(),""));
				strSubject = strSubject.replace(R_CANDIFNAME, uF.showData(getStrCandiFname(),""));
				strTextContent = strTextContent.replace(R_CANDIFNAME, uF.showData(getStrCandiFname(),""));
			}
			
			if(getStrCandiLname()!=null){
				strEmailBody = strEmailBody.replace(R_CANDILNAME, uF.showData(getStrCandiLname(),""));
				strSubject = strSubject.replace(R_CANDILNAME, uF.showData(getStrCandiLname(),""));
				strTextContent = strTextContent.replace(R_CANDILNAME, uF.showData(getStrCandiLname(),""));
			}
			
			if(getStrCandiEmailID()!=null){
				strEmailBody = strEmailBody.replace(R_CANDI_EMAIL_ID, uF.showData(getStrCandiEmailID(),""));
				strSubject = strSubject.replace(R_CANDI_EMAIL_ID, uF.showData(getStrCandiEmailID(),""));
				strTextContent = strTextContent.replace(R_CANDI_EMAIL_ID, uF.showData(getStrCandiEmailID(),""));
			}
			if(getStrCandiContactNo()!=null){
				strEmailBody = strEmailBody.replace(R_CANDI_CONTACT_NO, uF.showData(getStrCandiContactNo(),""));
				strSubject = strSubject.replace(R_CANDI_CONTACT_NO, uF.showData(getStrCandiContactNo(),""));
				strTextContent = strTextContent.replace(R_CANDI_CONTACT_NO, uF.showData(getStrCandiContactNo(),""));
			}
			
			if(getStrAttributeName()!=null){
				strEmailBody = strEmailBody.replace(ATTRIBUTE_NAME, uF.showData(getStrAttributeName(),""));
				strSubject = strSubject.replace(ATTRIBUTE_NAME, uF.showData(getStrAttributeName(),""));
				strTextContent = strTextContent.replace(ATTRIBUTE_NAME, uF.showData(getStrAttributeName(),""));
			}
			
			// ---- /Review ----
			
			
			// ---- Learning ----
			
			if(getStrLearningPlanName()!=null){
				strEmailBody = strEmailBody.replace(LEARNING_PLAN_NAME, uF.showData(getStrLearningPlanName(),""));
				strSubject = strSubject.replace(LEARNING_PLAN_NAME, uF.showData(getStrLearningPlanName(),""));
				strTextContent = strTextContent.replace(LEARNING_PLAN_NAME, uF.showData(getStrLearningPlanName(),""));
			}
			
			if(getStrLearningPlanStartdate()!=null){
				strEmailBody = strEmailBody.replace(LEARNING_PLAN_STARTDATE, uF.showData(getStrLearningPlanStartdate(),""));
				strSubject = strSubject.replace(LEARNING_PLAN_STARTDATE, uF.showData(getStrLearningPlanStartdate(),""));
				strTextContent = strTextContent.replace(LEARNING_PLAN_STARTDATE, uF.showData(getStrLearningPlanStartdate(),""));
			}
			
			if(getStrLearningPlanEnddate()!=null){
				strEmailBody = strEmailBody.replace(LEARNING_PLAN_ENDDATE, uF.showData(getStrLearningPlanEnddate(),""));
				strSubject = strSubject.replace(LEARNING_PLAN_ENDDATE, uF.showData(getStrLearningPlanEnddate(),""));
				strTextContent = strTextContent.replace(LEARNING_PLAN_ENDDATE, uF.showData(getStrLearningPlanEnddate(),""));
			}
			
			if(getStrLearnersName()!=null){
				strEmailBody = strEmailBody.replace(LEARNERS_NAME, uF.showData(getStrLearnersName(),""));
				strSubject = strSubject.replace(LEARNERS_NAME, uF.showData(getStrLearnersName(),""));
				strTextContent = strTextContent.replace(LEARNERS_NAME, uF.showData(getStrLearnersName(),""));
			}
			
			if(getStrTrainingName()!=null){
				strEmailBody = strEmailBody.replace(TRAINING_NAME, uF.showData(getStrTrainingName(),""));
				strSubject = strSubject.replace(TRAINING_NAME, uF.showData(getStrTrainingName(),""));
				strTextContent = strTextContent.replace(TRAINING_NAME, uF.showData(getStrTrainingName(),""));
			}
			
			if(getStrAssessmentName()!=null){
				strEmailBody = strEmailBody.replace(ASSESSMENT_NAME, uF.showData(getStrAssessmentName(),""));
				strSubject = strSubject.replace(ASSESSMENT_NAME, uF.showData(getStrAssessmentName(),""));
				strTextContent = strTextContent.replace(ASSESSMENT_NAME, uF.showData(getStrAssessmentName(),""));
			}
			
			if(getStrCourseName()!=null){
				strEmailBody = strEmailBody.replace(COURSE_NAME, uF.showData(getStrCourseName(),""));
				strSubject = strSubject.replace(COURSE_NAME, uF.showData(getStrCourseName(),""));
				strTextContent = strTextContent.replace(COURSE_NAME, uF.showData(getStrCourseName(),""));
			}
			
			if(getStrTrainerName()!=null){
				strEmailBody = strEmailBody.replace(TRAINER_NAME, uF.showData(getStrTrainerName(),""));
				strSubject = strSubject.replace(TRAINER_NAME, uF.showData(getStrTrainerName(),""));
				strTextContent = strTextContent.replace(TRAINER_NAME, uF.showData(getStrTrainerName(),""));
			}
			
			// ---- /Learning ----
			
			if(getStrActivityName()!=null){
				strEmailBody = strEmailBody.replace(ACTIVITY_NAME, uF.showData(getStrActivityName(),""));
				strSubject = strSubject.replace(ACTIVITY_NAME, uF.showData(getStrActivityName(),""));
				strTextContent = strTextContent.replace(ACTIVITY_NAME, uF.showData(getStrActivityName(),""));
			}
			
			
//			----- Project Management ------
			if(getStrCustFName()!=null){
				strEmailBody = strEmailBody.replace(CUST_FNAME, uF.showData(getStrCustFName(),""));
				strSubject = strSubject.replace(CUST_FNAME, uF.showData(getStrCustFName(),""));
				strTextContent = strTextContent.replace(CUST_FNAME, uF.showData(getStrCustFName(),""));
			}
			
			if(getStrCustLName()!=null){
				strEmailBody = strEmailBody.replace(CUST_LNAME, uF.showData(getStrCustLName(),""));
				strSubject = strSubject.replace(CUST_LNAME, uF.showData(getStrCustLName(),""));
				strTextContent = strTextContent.replace(CUST_LNAME, uF.showData(getStrCustLName(),""));
			}
			
			if(getStrProjectName()!=null){
				strEmailBody = strEmailBody.replace(PROJECT_NAME, uF.showData(getStrProjectName(),""));
				strSubject = strSubject.replace(PROJECT_NAME, uF.showData(getStrProjectName(),""));
				strTextContent = strTextContent.replace(PROJECT_NAME, uF.showData(getStrProjectName(),""));
			}
			
			if(getStrProjectDescription()!=null){
				strEmailBody = strEmailBody.replace(PROJECT_DESCRIPTION, uF.showData(getStrProjectDescription(),""));
				strSubject = strSubject.replace(PROJECT_DESCRIPTION, uF.showData(getStrProjectDescription(),""));
				strTextContent = strTextContent.replace(PROJECT_DESCRIPTION, uF.showData(getStrProjectDescription(),""));
			}
			
			if(getStrProjectOwnerName()!=null){
				strEmailBody = strEmailBody.replace(PROJECT_OWNER, uF.showData(getStrProjectOwnerName(),""));
				strSubject = strSubject.replace(PROJECT_OWNER, uF.showData(getStrProjectOwnerName(),""));
				strTextContent = strTextContent.replace(PROJECT_OWNER, uF.showData(getStrProjectOwnerName(),""));
			}
			
			if(getStrOrgName()!=null){
				strEmailBody = strEmailBody.replace(ORG_NAME, uF.showData(getStrOrgName(),""));
				strSubject = strSubject.replace(ORG_NAME, uF.showData(getStrOrgName(),""));
				strTextContent = strTextContent.replace(ORG_NAME, uF.showData(getStrOrgName(),""));
			}
			
			if(getStrClientName()!=null){
				strEmailBody = strEmailBody.replace(CLIENT_NAME, uF.showData(getStrClientName(),""));
				strSubject = strSubject.replace(CLIENT_NAME, uF.showData(getStrClientName(),""));
				strTextContent = strTextContent.replace(CLIENT_NAME, uF.showData(getStrClientName(),""));
			}
			
			if(getStrTaskName()!=null){
				strEmailBody = strEmailBody.replace(TASK_NAME, uF.showData(getStrTaskName(),""));
				strSubject = strSubject.replace(TASK_NAME, uF.showData(getStrTaskName(),""));
				strTextContent = strTextContent.replace(TASK_NAME, uF.showData(getStrTaskName(),""));
			}
			
			if(getStrResourceFName()!=null){
				strEmailBody = strEmailBody.replace(RESOURCE_FNAME, uF.showData(getStrResourceFName(),""));
				strSubject = strSubject.replace(RESOURCE_FNAME, uF.showData(getStrResourceFName(),""));
				strTextContent = strTextContent.replace(RESOURCE_FNAME, uF.showData(getStrResourceFName(),""));
			}
			
			if(getStrResourceLName()!=null){
				strEmailBody = strEmailBody.replace(RESOURCE_LNAME, uF.showData(getStrResourceLName(),""));
				strSubject = strSubject.replace(RESOURCE_LNAME, uF.showData(getStrResourceLName(),""));
				strTextContent = strTextContent.replace(RESOURCE_LNAME, uF.showData(getStrResourceLName(),""));
			}
			
			if(getStrTeamLeader()!=null){
				strEmailBody = strEmailBody.replace(TEAM_LEADER, uF.showData(getStrTeamLeader(),""));
				strSubject = strSubject.replace(TEAM_LEADER, uF.showData(getStrTeamLeader(),""));
				strTextContent = strTextContent.replace(TEAM_LEADER, uF.showData(getStrTeamLeader(),""));
			}
			
			if(getStrDocumentName()!=null){
				strEmailBody = strEmailBody.replace(DOCUMENT_NAME, uF.showData(getStrDocumentName(),""));
				strSubject = strSubject.replace(DOCUMENT_NAME, uF.showData(getStrDocumentName(),""));
				strTextContent = strTextContent.replace(DOCUMENT_NAME, uF.showData(getStrDocumentName(),""));
			}
			
			if(getStrDoneBy()!=null){
				strEmailBody = strEmailBody.replace(DONE_BY, uF.showData(getStrDoneBy(),""));
				strSubject = strSubject.replace(DONE_BY, uF.showData(getStrDoneBy(),""));
				strTextContent = strTextContent.replace(DONE_BY, uF.showData(getStrDoneBy(),""));
			}
			
			if(getStrFromDate()!=null){
				strEmailBody = strEmailBody.replace(FROM_DATE, uF.showData(getStrFromDate(),""));
				strSubject = strSubject.replace(FROM_DATE, uF.showData(getStrFromDate(),""));
				strTextContent = strTextContent.replace(FROM_DATE, uF.showData(getStrFromDate(),""));
			}
			
			if(getStrToDate()!=null){
				strEmailBody = strEmailBody.replace(TO_DATE, uF.showData(getStrToDate(),""));
				strSubject = strSubject.replace(TO_DATE, uF.showData(getStrToDate(),""));
				strTextContent = strTextContent.replace(TO_DATE, uF.showData(getStrToDate(),""));
			}
			
			if(getStrProjectFreqName()!=null){
				strEmailBody = strEmailBody.replace(PROJECT_FREQUENCY_NAME, uF.showData(getStrProjectFreqName(),""));
				strSubject = strSubject.replace(PROJECT_FREQUENCY_NAME, uF.showData(getStrProjectFreqName(),""));
				strTextContent = strTextContent.replace(PROJECT_FREQUENCY_NAME, uF.showData(getStrProjectFreqName(),""));
			}
			
			if(getStrInvoiceNo()!=null){
				strEmailBody = strEmailBody.replace(INVOICE_NO, uF.showData(getStrInvoiceNo(),""));
				strSubject = strSubject.replace(INVOICE_NO, uF.showData(getStrInvoiceNo(),""));
				strTextContent = strTextContent.replace(INVOICE_NO, uF.showData(getStrInvoiceNo(),""));
			}
			
			if(getStrAddPeopleLink()!=null){
				strEmailBody = strEmailBody.replace(ADD_EMP_LINK, "<a href=\""+strLink+strAddPeopleAction+uF.showData(getStrAddPeopleLink(),"")+"\">"+strLink+strAddPeopleAction+uF.showData(getStrAddPeopleLink(),"")+"</a>");
				strSubject = strSubject.replace(ADD_EMP_LINK, "<a href=\""+strLink+strAddPeopleAction+uF.showData(getStrAddPeopleLink(),"")+"\">"+strLink+strAddPeopleAction+uF.showData(getStrAddPeopleLink(),"")+"</a>");
				strTextContent = strTextContent.replace(ADD_EMP_LINK, strLink+strAddPeopleAction+uF.showData(getStrAddPeopleLink(),""));
			}
			
			if(getStrCustomerRegisterLink()!=null){
				strEmailBody = strEmailBody.replace(CUSTOMER_REGISTER_LINK, "<a href=\""+strLink + strCustRegisterAction + uF.showData(getStrCustomerRegisterLink(),"")+"\">"+strLink + strCustRegisterAction + uF.showData(getStrCustomerRegisterLink(),"")+"</a>");
				strSubject = strSubject.replace(CUSTOMER_REGISTER_LINK, "<a href=\""+strLink + strCustRegisterAction + uF.showData(getStrCustomerRegisterLink(),"")+"\">"+strLink + strCustRegisterAction + uF.showData(getStrCustomerRegisterLink(),"")+"</a>");
				strTextContent = strTextContent.replace(CUSTOMER_REGISTER_LINK, strLink+strCustRegisterAction+uF.showData(getStrCustomerRegisterLink(),""));
			}
//			----- /Project Management ------
			
			
			/**new tag
			 * */
			if(getStrOrganisationAddress()!=null){
				strEmailBody = strEmailBody.replace(ORGANISATION_ADDRESS, uF.showData(getStrOrganisationAddress(),""));
				strSubject = strSubject.replace(ORGANISATION_ADDRESS, uF.showData(getStrOrganisationAddress(),""));
				strTextContent = strTextContent.replace(ORGANISATION_ADDRESS, uF.showData(getStrOrganisationAddress(),""));
			}
			if(getStrLegalEntityName()!=null){
				strEmailBody = strEmailBody.replace(LEGAL_ENTITY_NAME, uF.showData(getStrLegalEntityName(),""));
				strSubject = strSubject.replace(LEGAL_ENTITY_NAME, uF.showData(getStrLegalEntityName(),""));
				strTextContent = strTextContent.replace(LEGAL_ENTITY_NAME, uF.showData(getStrLegalEntityName(),""));
			}
			if(getStrLegalEntityAddress()!=null){
				strEmailBody = strEmailBody.replace(LEGAL_ENTITY_ADDRESS, uF.showData(getStrLegalEntityAddress(),""));
				strSubject = strSubject.replace(LEGAL_ENTITY_ADDRESS, uF.showData(getStrLegalEntityAddress(),""));
				strTextContent = strTextContent.replace(LEGAL_ENTITY_ADDRESS, uF.showData(getStrLegalEntityAddress(),""));
			}
			if(getStrDepartmentName()!=null){
				strEmailBody = strEmailBody.replace(DEPARTMENT_NAME, uF.showData(getStrDepartmentName(),""));
				strSubject = strSubject.replace(DEPARTMENT_NAME, uF.showData(getStrDepartmentName(),""));
				strTextContent = strTextContent.replace(DEPARTMENT_NAME, uF.showData(getStrDepartmentName(),""));
			}
			if(getStrSbuName()!=null){
				strEmailBody = strEmailBody.replace(SBU_NAME, uF.showData(getStrSbuName(),""));
				strSubject = strSubject.replace(SBU_NAME, uF.showData(getStrSbuName(),""));
				strTextContent = strTextContent.replace(SBU_NAME, uF.showData(getStrSbuName(),""));
			}
			if(getStrReportEmpDesignation()!=null){
				strEmailBody = strEmailBody.replace(R_REPORT_EMP_DESIGNATION, uF.showData(getStrReportEmpDesignation(),""));
				strSubject = strSubject.replace(R_REPORT_EMP_DESIGNATION, uF.showData(getStrReportEmpDesignation(),""));
				strTextContent = strTextContent.replace(R_REPORT_EMP_DESIGNATION, uF.showData(getStrReportEmpDesignation(),""));
			}
			if(getStrSkills()!=null){
				strEmailBody = strEmailBody.replace(SKILLS, uF.showData(getStrSkills(),""));
				strSubject = strSubject.replace(SKILLS, uF.showData(getStrSkills(),""));
				strTextContent = strTextContent.replace(SKILLS, uF.showData(getStrSkills(),""));
			}
			if(getStrEffectiveDate()!=null){
				strEmailBody = strEmailBody.replace(EFFECTIVE_DATE, uF.showData(getStrEffectiveDate(),""));
				strSubject = strSubject.replace(EFFECTIVE_DATE, uF.showData(getStrEffectiveDate(),""));
				strTextContent = strTextContent.replace(EFFECTIVE_DATE, uF.showData(getStrEffectiveDate(),""));
			}
			if(getStrPromotionGrade()!=null){
				strEmailBody = strEmailBody.replace(PROMOTION_GRADE, uF.showData(getStrPromotionGrade(),""));
				strSubject = strSubject.replace(PROMOTION_GRADE, uF.showData(getStrPromotionGrade(),""));
				strTextContent = strTextContent.replace(PROMOTION_GRADE, uF.showData(getStrPromotionGrade(),""));
			}
			if(getStrPromotionLevel()!=null){
				strEmailBody = strEmailBody.replace(PROMOTION_LEVEL, uF.showData(getStrPromotionLevel(),""));
				strSubject = strSubject.replace(PROMOTION_LEVEL, uF.showData(getStrPromotionLevel(),""));
				strTextContent = strTextContent.replace(PROMOTION_LEVEL, uF.showData(getStrPromotionLevel(),""));
			}
			if(getStrPromotionDesignation()!=null){
				strEmailBody = strEmailBody.replace(PROMOTION_DESIGNATION, uF.showData(getStrPromotionDesignation(),""));
				strSubject = strSubject.replace(PROMOTION_DESIGNATION, uF.showData(getStrPromotionDesignation(),""));
				strTextContent = strTextContent.replace(PROMOTION_DESIGNATION, uF.showData(getStrPromotionDesignation(),""));
			}
			if(getStrPromotionDate()!=null){
				strEmailBody = strEmailBody.replace(PROMOTION_DATE, uF.showData(getStrPromotionDate(),""));
				strSubject = strSubject.replace(PROMOTION_DATE, uF.showData(getStrPromotionDate(),""));
				strTextContent = strTextContent.replace(PROMOTION_DATE, uF.showData(getStrPromotionDate(),""));
			}
			
			if(getStrCloseDate()!=null){
				strEmailBody = strEmailBody.replace(CLOSE_DATE, uF.showData(getStrCloseDate(),""));
				strSubject = strSubject.replace(CLOSE_DATE, uF.showData(getStrCloseDate(),""));
				strTextContent = strTextContent.replace(CLOSE_DATE, uF.showData(getStrCloseDate(),""));
			}
			
			
			if(getStrIncrementPercent()!=null){
				strEmailBody = strEmailBody.replace(INCREMENT_PERCENT, uF.showData(getStrIncrementPercent(),""));
				strSubject = strSubject.replace(INCREMENT_PERCENT, uF.showData(getStrIncrementPercent(),""));
				strTextContent = strTextContent.replace(INCREMENT_PERCENT, uF.showData(getStrIncrementPercent(),""));
			}
			if(getStrTerminateDate()!=null){
				strEmailBody = strEmailBody.replace(TERMINATION_DATE, uF.showData(getStrTerminateDate(),""));
				strSubject = strSubject.replace(TERMINATION_DATE, uF.showData(getStrTerminateDate(),""));
				strTextContent = strTextContent.replace(TERMINATION_DATE, uF.showData(getStrTerminateDate(),""));
			}
			if(getStrLastDateAtOffice()!=null){
				strEmailBody = strEmailBody.replace(LAST_DATE_AT_OFFICE, uF.showData(getStrLastDateAtOffice(),""));
				strSubject = strSubject.replace(LAST_DATE_AT_OFFICE, uF.showData(getStrLastDateAtOffice(),""));
				strTextContent = strTextContent.replace(LAST_DATE_AT_OFFICE, uF.showData(getStrLastDateAtOffice(),""));
			}
			if(getStrSalaryStructure()!=null){
				strEmailBody = strEmailBody.replace(SALARY_STRUCTURE, uF.showData(getStrSalaryStructure(),""));
				strSubject = strSubject.replace(SALARY_STRUCTURE, uF.showData(getStrSalaryStructure(),""));
				strTextContent = strTextContent.replace(SALARY_STRUCTURE, uF.showData(getStrSalaryStructure(),""));
			}
			
			if(getStrAnnualBonusStructure()!=null){
				strEmailBody = strEmailBody.replace(ANNUAL_BONUS_STRUCTURE, uF.showData(getStrAnnualBonusStructure(),""));
				strSubject = strSubject.replace(ANNUAL_BONUS_STRUCTURE, uF.showData(getStrAnnualBonusStructure(),""));
				strTextContent = strTextContent.replace(ANNUAL_BONUS_STRUCTURE, uF.showData(getStrAnnualBonusStructure(),""));
			}
//			if(getStrPayStructure()!=null){
//				strEmailBody = strEmailBody.replace(PAY_STRUCTURE, uF.showData(getStrPayStructure(),""));
//				strSubject = strSubject.replace(PAY_STRUCTURE, uF.showData(getStrPayStructure(),""));
//				strTextContent = strTextContent.replace(PAY_STRUCTURE, uF.showData(getStrPayStructure(),""));
//			}
			if(getStrPayrollAmount()!=null){
				strEmailBody = strEmailBody.replace(PAYROLL_AMOUNT, uF.showData(getStrPayrollAmount(),""));
				strSubject = strSubject.replace(PAYROLL_AMOUNT, uF.showData(getStrPayrollAmount(),""));
				strTextContent = strTextContent.replace(PAYROLL_AMOUNT, uF.showData(getStrPayrollAmount(),""));
			}
			if(getStrPayrollAmountWords()!=null){
				strEmailBody = strEmailBody.replace(PAYROLL_AMOUNT_WORDS, uF.showData(getStrPayrollAmountWords(),""));
				strSubject = strSubject.replace(PAYROLL_AMOUNT_WORDS, uF.showData(getStrPayrollAmountWords(),""));
				strTextContent = strTextContent.replace(PAYROLL_AMOUNT_WORDS, uF.showData(getStrPayrollAmountWords(),""));
			}
			if(getStrPayYear()!=null){
				strEmailBody = strEmailBody.replace(PAY_YEAR, uF.showData(getStrPayYear(),""));
				strSubject = strSubject.replace(PAY_YEAR, uF.showData(getStrPayYear(),""));
				strTextContent = strTextContent.replace(PAY_YEAR, uF.showData(getStrPayYear(),""));
			}
			if(getStrPayMonth()!=null){
				strEmailBody = strEmailBody.replace(PAY_MONTH, uF.showData(getStrPayMonth(),""));
				strSubject = strSubject.replace(PAY_MONTH, uF.showData(getStrPayMonth(),""));
				strTextContent = strTextContent.replace(PAY_MONTH, uF.showData(getStrPayMonth(),""));
			}
			if(getStrAllowanceAmount()!=null){
				strEmailBody = strEmailBody.replace(ALLOWANCE_AMOUNT, uF.showData(getStrAllowanceAmount(),""));
				strSubject = strSubject.replace(ALLOWANCE_AMOUNT, uF.showData(getStrAllowanceAmount(),""));
				strTextContent = strTextContent.replace(ALLOWANCE_AMOUNT, uF.showData(getStrAllowanceAmount(),""));
			}
			if(getStrAllowanceAmountWords()!=null){
				strEmailBody = strEmailBody.replace(ALLOWANCE_AMOUNT_WORDS, uF.showData(getStrAllowanceAmountWords(),""));
				strSubject = strSubject.replace(ALLOWANCE_AMOUNT_WORDS, uF.showData(getStrAllowanceAmountWords(),""));
				strTextContent = strTextContent.replace(ALLOWANCE_AMOUNT_WORDS, uF.showData(getStrAllowanceAmountWords(),""));
			}
			if(getStrBanks()!=null){
				strEmailBody = strEmailBody.replace(BANKS, uF.showData(getStrBanks(),""));
				strSubject = strSubject.replace(BANKS, uF.showData(getStrBanks(),""));
				strTextContent = strTextContent.replace(BANKS, uF.showData(getStrBanks(),""));
			}
			if(getStrBankCode()!=null){
				strEmailBody = strEmailBody.replace(BANK_CODE, uF.showData(getStrBankCode(),""));
				strSubject = strSubject.replace(BANK_CODE, uF.showData(getStrBankCode(),""));
				strTextContent = strTextContent.replace(BANK_CODE, uF.showData(getStrBankCode(),""));
			}
			if(getStrCandiAddress()!=null){
				strEmailBody = strEmailBody.replace(R_CANDIADDRESS, uF.showData(getStrCandiAddress(),""));
				strSubject = strSubject.replace(R_CANDIADDRESS, uF.showData(getStrCandiAddress(),""));
				strTextContent = strTextContent.replace(R_CANDIADDRESS, uF.showData(getStrCandiAddress(),""));
			}
			if(getStrPrevMonthCTC()!=null){
				strEmailBody = strEmailBody.replace(PREVIOUS_MONTH_CTC, uF.showData(getStrPrevMonthCTC(),""));
				strSubject = strSubject.replace(PREVIOUS_MONTH_CTC, uF.showData(getStrPrevMonthCTC(),""));
				strTextContent = strTextContent.replace(PREVIOUS_MONTH_CTC, uF.showData(getStrPrevMonthCTC(),""));
			}
			if(getStrPrevAnnualCTC()!=null){
				strEmailBody = strEmailBody.replace(PREVIOUS_ANNUAL_CTC, uF.showData(getStrPrevAnnualCTC(),""));
				strSubject = strSubject.replace(PREVIOUS_ANNUAL_CTC, uF.showData(getStrPrevAnnualCTC(),""));
				strTextContent = strTextContent.replace(PREVIOUS_ANNUAL_CTC, uF.showData(getStrPrevAnnualCTC(),""));
			}
			if(getStrIncrementAmtMonth()!=null){
				strEmailBody = strEmailBody.replace(INCREMENT_AMOUNT_MONTH, uF.showData(getStrIncrementAmtMonth(),""));
				strSubject = strSubject.replace(INCREMENT_AMOUNT_MONTH, uF.showData(getStrIncrementAmtMonth(),""));
				strTextContent = strTextContent.replace(INCREMENT_AMOUNT_MONTH, uF.showData(getStrIncrementAmtMonth(),""));
			}
			if(getStrIncrementAmtAnnual()!=null){
				strEmailBody = strEmailBody.replace(INCREMENT_AMOUNT_ANNUAL, uF.showData(getStrIncrementAmtAnnual(),""));
				strSubject = strSubject.replace(INCREMENT_AMOUNT_ANNUAL, uF.showData(getStrIncrementAmtAnnual(),""));
				strTextContent = strTextContent.replace(INCREMENT_AMOUNT_ANNUAL, uF.showData(getStrIncrementAmtAnnual(),""));
			}
			if(getStrEmpAnnualCTC()!=null){
				strEmailBody = strEmailBody.replace(EMP_ANNUAL_CTC, uF.showData(getStrEmpAnnualCTC(),""));
				strSubject = strSubject.replace(EMP_ANNUAL_CTC, uF.showData(getStrEmpAnnualCTC(),""));
				strTextContent = strTextContent.replace(EMP_ANNUAL_CTC, uF.showData(getStrEmpAnnualCTC(),""));
			}
			 
			if(getStrProfileImage()!=null && !getStrProfileImage().trim().equals("")) {
				strEmailBody = strEmailBody.replace(PROFILE_IMAGE, uF.showData(getStrProfileImage(), ""));
				strSubject = strSubject.replace(PROFILE_IMAGE, uF.showData(getStrProfileImage(), ""));
				strTextContent = strTextContent.replace(PROFILE_IMAGE, uF.showData(getStrProfileImage(), ""));
			}

			if (getStrAge() != null && !getStrAge().trim().equals("")) {
				strEmailBody = strEmailBody.replace(R_AGE, uF.showData(getStrAge(), ""));
				strSubject = strSubject.replace(R_AGE, uF.showData(getStrAge(), ""));
				strTextContent = strTextContent.replace(R_AGE, uF.showData(getStrAge(), ""));
			}

			if (getStrHrName() != null && !getStrHrName().trim().equals("")) {
				strEmailBody = strEmailBody.replace(R_HRNAME, uF.showData(getStrHrName(), ""));
				strSubject = strSubject.replace(R_HRNAME, uF.showData(getStrHrName(), ""));
				strTextContent = strTextContent.replace(R_HRNAME, uF.showData(getStrHrName(), ""));
			}

			if (getStrFatherName() != null && !getStrFatherName().trim().equals("")) {
				strEmailBody = strEmailBody.replace(FATHER_NAME, uF.showData(getStrFatherName(), ""));
				strSubject = strSubject.replace(FATHER_NAME, uF.showData(getStrFatherName(), ""));
				strTextContent = strTextContent.replace(FATHER_NAME, uF.showData(getStrFatherName(), ""));
			}

			if (getStrBloodGroup() != null && !getStrBloodGroup().trim().equals("")) {
				strEmailBody = strEmailBody.replace(R_BLOODGROUP, uF.showData(getStrBloodGroup(), ""));
				strSubject = strSubject.replace(R_BLOODGROUP, uF.showData(getStrBloodGroup(), ""));
				strTextContent = strTextContent.replace(R_BLOODGROUP, uF.showData(getStrBloodGroup(), ""));
			}

			if (getStrstate() != null && !getStrstate().trim().equals("")) {
				strEmailBody = strEmailBody.replace(R_STATE, uF.showData(getStrstate(), ""));
				strSubject = strSubject.replace(R_STATE, uF.showData(getStrstate(), ""));
				strTextContent = strTextContent.replace(R_STATE, uF.showData(getStrstate(), ""));
			}

			if (getStrResignationDate() != null && !getStrResignationDate().trim().equals("")) {
				strEmailBody = strEmailBody.replace(RESIGNATION_DATE, uF.showData(getStrResignationDate(), ""));
				strSubject = strSubject.replace(RESIGNATION_DATE, uF.showData(getStrResignationDate(), ""));
				strTextContent = strTextContent.replace(RESIGNATION_DATE, uF.showData(getStrResignationDate(), ""));
			}

			if (getStrResignationReason() != null && !getStrResignationReason().trim().equals("")) {
				strEmailBody = strEmailBody.replace(RESIGNATION_REASON, uF.showData(getStrResignationReason(), ""));
				strSubject = strSubject.replace(RESIGNATION_REASON, uF.showData(getStrResignationReason(), ""));
				strTextContent = strTextContent.replace(RESIGNATION_REASON, uF.showData(getStrResignationReason(), ""));
			}

			if (getStrManagerComment() != null) {
				strEmailBody = strEmailBody.replace(R_MGR_COMMENT, uF.showData(getStrManagerComment(), ""));
				strSubject = strSubject.replace(R_MGR_COMMENT, uF.showData(getStrManagerComment(), ""));
				strTextContent = strTextContent.replace(R_MGR_COMMENT, uF.showData(getStrManagerComment(), ""));
			}

			if (getStrRecipientName() != null) {
				strEmailBody = strEmailBody.replace(RECIPIENT_NAME, uF.showData(getStrRecipientName(), ""));
				strSubject = strSubject.replace(RECIPIENT_NAME, uF.showData(getStrRecipientName(), ""));
				strTextContent = strTextContent.replace(RECIPIENT_NAME, uF.showData(getStrRecipientName(), ""));
			}

			if (getStrNoOfYearsWorking() != null) {
				strEmailBody = strEmailBody.replace(NO_OF_YEARS_WORKING, uF.showData(getStrNoOfYearsWorking(), ""));
				strSubject = strSubject.replace(NO_OF_YEARS_WORKING, uF.showData(getStrNoOfYearsWorking(), ""));
				strTextContent = strTextContent.replace(NO_OF_YEARS_WORKING, uF.showData(getStrNoOfYearsWorking(), ""));
			}

			// System.out.println("
			// getStrEvent::"+getStrEvent()+"setStrEventDate::"+getStrEventDate()+"setStrEventTime::"+getStrEventTime()+"setStrAddedBy::"+getStrAddedBy());
			if (getStrEvent() != null) {
				strSubject = strSubject.replace(EVENT, uF.showData(getStrEvent(), ""));
				strEmailBody = strEmailBody.replace(EVENT, uF.showData(getStrEvent(), ""));
				strTextContent = strTextContent.replace(EVENT, uF.showData(getStrEvent(), ""));
			}

			if (getStrEventDesc() != null) {
				strSubject = strSubject.replace(EVENT_DESC, uF.showData(getStrEventDesc(), ""));
				strEmailBody = strEmailBody.replace(EVENT_DESC, uF.showData(getStrEventDesc(), ""));
				strTextContent = strTextContent.replace(EVENT_DESC, uF.showData(getStrEventDesc(), ""));
			}

			if (getStrEventDate() != null) {
				strSubject = strSubject.replace(EVENT_DATE, uF.showData(getStrEventDate(), ""));
				strEmailBody = strEmailBody.replace(EVENT_DATE, uF.showData(getStrEventDate(), ""));
				strTextContent = strTextContent.replace(EVENT_DATE, uF.showData(getStrEventDate(), ""));
			}

			if (getStrEventTime() != null) {
				strSubject = strSubject.replace(EVENT_TIME, uF.showData(getStrEventTime(), ""));
				strEmailBody = strEmailBody.replace(EVENT_TIME, uF.showData(getStrEventTime(), ""));
				strTextContent = strTextContent.replace(EVENT_TIME, uF.showData(getStrEventTime(), ""));
			}

			if (getStrAddedBy() != null) {
				// System.out.println("getStrAddedBy:::"+getStrAddedBy());
				strSubject = strSubject.replace(ADDED_BY, uF.showData(getStrAddedBy(), ""));
				strEmailBody = strEmailBody.replace(ADDED_BY, uF.showData(getStrAddedBy(), ""));
				strTextContent = strTextContent.replace(ADDED_BY, uF.showData(getStrAddedBy(), ""));
			}

			if (getStrLocation() != null) {
				strSubject = strSubject.replace(LOCATION, uF.showData(getStrLocation(), ""));
				strEmailBody = strEmailBody.replace(LOCATION, uF.showData(getStrLocation(), ""));
				strTextContent = strTextContent.replace(LOCATION, uF.showData(getStrLocation(), ""));
			}

			if (getStrDepartment() != null) {
				strSubject = strSubject.replace(EVENT_DEPARTMENT, uF.showData(getStrDepartment(), ""));
				strEmailBody = strEmailBody.replace(EVENT_DEPARTMENT, uF.showData(getStrDepartment(), ""));
				strTextContent = strTextContent.replace(EVENT_DEPARTMENT, uF.showData(getStrDepartment(), ""));
			}

			if (getStrEmpName() != null) {
				// System.out.println("getStrEmpName::"+getStrEmpName());
				strSubject = strSubject.replace(EMP_NAME, uF.showData(getStrEmpName(), ""));
				strEmailBody = strEmailBody.replace(EMP_NAME, uF.showData(getStrEmpName(), ""));
				strTextContent = strTextContent.replace(EMP_NAME, uF.showData(getStrEmpName(), ""));
			}

			if (getStrAnnouncement() != null) {
				strSubject = strSubject.replace(ANNOUNCEMENT, uF.showData(getStrAnnouncement(), ""));
				strEmailBody = strEmailBody.replace(ANNOUNCEMENT, uF.showData(getStrAnnouncement(), ""));
				strTextContent = strTextContent.replace(ANNOUNCEMENT, uF.showData(getStrAnnouncement(), ""));
			}

			if (getStrAnnDate() != null) {
				strSubject = strSubject.replace(ANNOUNCEMENT_DATE, uF.showData(getStrAnnDate(), ""));
				strEmailBody = strEmailBody.replace(ANNOUNCEMENT_DATE, uF.showData(getStrAnnDate(), ""));
				strTextContent = strTextContent.replace(ANNOUNCEMENT_DATE, uF.showData(getStrAnnDate(), ""));
			}

			if (getStrAnnouncementDesc() != null) {
				strSubject = strSubject.replace(ANNOUNCEMENT_CONTENT, uF.showData(getStrAnnouncementDesc(), ""));
				strEmailBody = strEmailBody.replace(ANNOUNCEMENT_CONTENT, uF.showData(getStrAnnouncementDesc(), ""));
				strTextContent = strTextContent.replace(ANNOUNCEMENT_CONTENT, uF.showData(getStrAnnouncementDesc(), ""));
			}

			if (getStrAttendanceApproveEmpCount() != null) {
				strSubject = strSubject.replace(ATTENDANCE_APPROVE_EMP_COUNT, uF.showData(getStrAttendanceApproveEmpCount(), "0"));
				strEmailBody = strEmailBody.replace(ATTENDANCE_APPROVE_EMP_COUNT, uF.showData(getStrAttendanceApproveEmpCount(), "0"));
				strTextContent = strTextContent.replace(ATTENDANCE_APPROVE_EMP_COUNT, uF.showData(getStrAttendanceApproveEmpCount(), "0"));
			}

			if (getStrAttendancePendingEmpCount() != null) {
				strSubject = strSubject.replace(ATTENDANCE_PENDING_EMP_COUNT, uF.showData(getStrAttendancePendingEmpCount(), "0"));
				strEmailBody = strEmailBody.replace(ATTENDANCE_PENDING_EMP_COUNT, uF.showData(getStrAttendancePendingEmpCount(), "0"));
				strTextContent = strTextContent.replace(ATTENDANCE_PENDING_EMP_COUNT, uF.showData(getStrAttendancePendingEmpCount(), "0"));
			}

			if (getStrSalaryApproveEmpCount() != null) {
				strSubject = strSubject.replace(SALARY_APPROVE_EMP_COUNT, uF.showData(getStrSalaryApproveEmpCount(), "0"));
				strEmailBody = strEmailBody.replace(SALARY_APPROVE_EMP_COUNT, uF.showData(getStrSalaryApproveEmpCount(), "0"));
				strTextContent = strTextContent.replace(SALARY_APPROVE_EMP_COUNT, uF.showData(getStrSalaryApproveEmpCount(), "0"));
			}

			if (getStrSalaryPendingEmpCount() != null) {
				strSubject = strSubject.replace(SALARY_PENDING_EMP_COUNT, uF.showData(getStrSalaryPendingEmpCount(), "0"));
				strEmailBody = strEmailBody.replace(SALARY_PENDING_EMP_COUNT, uF.showData(getStrSalaryPendingEmpCount(), "0"));
				strTextContent = strTextContent.replace(SALARY_PENDING_EMP_COUNT, uF.showData(getStrSalaryPendingEmpCount(), "0"));
			}

			if (getStrPaycycleMonthAndYear() != null) {
				strSubject = strSubject.replace(PAYCYCLE_MONTH_YEAR, uF.showData(getStrPaycycleMonthAndYear(), "0"));
				strEmailBody = strEmailBody.replace(PAYCYCLE_MONTH_YEAR, uF.showData(getStrPaycycleMonthAndYear(), "0"));
				strTextContent = strTextContent.replace(PAYCYCLE_MONTH_YEAR, uF.showData(getStrPaycycleMonthAndYear(), "0"));
			}
			
			// Start Dattatray Date : 05-10-21
			if (getStrJobTitle() != null) {
				strSubject = strSubject.replace(CANDI_JOB_TITLE, uF.showData(getStrJobTitle(), ""));
				strEmailBody = strEmailBody.replace(CANDI_JOB_TITLE, uF.showData(getStrJobTitle(), ""));
				strTextContent = strTextContent.replace(CANDI_JOB_TITLE, uF.showData(getStrJobTitle(), ""));
			}// End Dattatray Date : 05-10-21
			// System.out.println("strEmailBody:::::"+strEmailBody);
			
		//===start parvez date: 15-02-2023===	
			if (getStrPublishDate() != null) {
				strSubject = strSubject.replace(PUBLISH_DATE, uF.showData(getStrPublishDate(), ""));
				strEmailBody = strEmailBody.replace(PUBLISH_DATE, uF.showData(getStrPublishDate(), ""));
				strTextContent = strTextContent.replace(PUBLISH_DATE, uF.showData(getStrPublishDate(), ""));
			}
		//===end parvez date: 15-02-2023===	

			if (getContentType() != null && getContentType().equals("MAIL_BODY")) {
				if (strEmailBody != null && strEmailBody.contains("[") || strEmailBody.contains("]")) {
//					System.out.println("Emasil contains [ or ]");
					if (strEmailBody.indexOf("[") > 0) {
						strEmailBody = strEmailBody.replace("[", " ");
					}
					if (strEmailBody.indexOf("]") > 0) {
						strEmailBody = strEmailBody.replace("]", "  ");
					}
				}
			}
			// System.out.println("strEmailBody:::::"+strEmailBody);

			if (strEmailBody.contains(NDA_DETAILS)) {
				StringBuilder sb = new StringBuilder();
				sb.append("<table border=\"1\" cellpadding=\"4\" cellspacing=\"0\" style=\"border-collapse: collapse; height:300px;width:100%\"\">");

				sb.append("<tr>");
				sb.append("<th >" + getStrSbuName() + "     </th>");
				sb.append("<th>" + getStrEmpOrganization() + "    </th>");
				sb.append("</tr>");

				sb.append("<tr>");
				sb.append("<td rowspan=" + "2" + ">Designation: Authorized Signatory <br><br>    <br>    <br> <br></td>");
				sb.append("<td rowspan=" + "2" + "></td>");
				sb.append("</tr>");

				sb.append("<tr>");

				sb.append("</tr>");

				sb.append("<tr>");
				sb.append("<td>    </td>");
				sb.append("<td>Name :- " + getStrEmpFullNamename() + "     </td>");
				sb.append("</tr>");
				sb.append("</table>");

				strEmailBody = strEmailBody.replace("[NDA_DETAILS]", sb.toString());

			}

			/**
			 * end
			 */

			strEmailBody = strEmailBody.replace("\n", "<br/>");
			if (getContentType() != null && getContentType().equals("MAIL_BODY")) {
				strEmailBody = strEmailBody.replace("<p>", "");
				strEmailBody = strEmailBody.replace("</p>", "");
			}

			String filepath = null;
			if (CF.getStrDocRetriveLocation() == null) {
				filepath = DOCUMENT_LOCATION;
			} else {
				filepath = CF.getStrDocRetriveLocation();
			}
			String background_img = filepath + getBackground_img();
			// System.out.println("background_img ===>> " + background_img);
			// strEmailBody = "<table background=\""+background_img+"\"
			// width=\"100%\" height=\"100%\">" +
			// "<tr><td>"+strEmailBody+"</td></tr></table>"; //background-color:
			// #FFFFFF;
			strEmailBody = "<div style=\"padding: 5px; background-image:url('" + background_img
					+ "'); background-repeat: no-repeat; background-size: 100% 100%;\">" + strEmailBody + "</div>";
			// System.out.println("strEmailBody ===>> " + strEmailBody);
			if (isEmailTemplate()) {
				String strMailTemplate = getMailtemplate(strPort, strLink, strLinkImg, strLoginAction);
				if (strMailTemplate != null && !strMailTemplate.trim().equals("")) {
					strEmailBody = strMailTemplate.replace(MAIL_BODY_TEMPLATE, strEmailBody);
				}
			}
			// System.out.println("strEmailBody=====>"+strEmailBody);

			setStrEmailBody(strEmailBody);
			setStrTextContent(strTextContent);
			setStrEmailSubject(strSubject);
		}

		Map<String, String> hmParsedContent = new HashMap<String, String>();
		hmParsedContent.put("MAIL_BODY", strEmailBody);
		hmParsedContent.put("TEXT_BODY", strTextContent);
		hmParsedContent.put("MAIL_SUBJECT", strSubject);

		return hmParsedContent;
	}

	private String getMailtemplate(String strPort, String strLink, String strLinkImg, String strLoginAction) {
		// System.out.println("In
		// getMailtemplate................................");

		StringBuilder sb = new StringBuilder();
		// String strPowerByImage =
		// strLinkImg+"/images1/icons/icons/taskrig.png";
		String strPowerByImage = strLinkImg + "/images1/icons/icons/workrig.png";
		// System.out.println("strPowerByImage=====>"+strPowerByImage);

		// String filepath = null;
		// if (CF.getStrDocRetriveLocation() == null) {
		// filepath = DOCUMENT_LOCATION;
		// } else {
		// filepath = CF.getStrDocRetriveLocation();
		// }
		// String background_img = filepath + getBackground_img();
		// System.out.println("background_img ===>> " + background_img);
		// background-image:url(\""+background_img+"\");
		sb.append("<div style=\"width:100%; font-size: 12px; font-family:calibri;\" align=\"center\">"
				+ "<table style=\"width:60%; font-family:calibri;border:1px solid #C5C5C5;\">" + "<tbody>" + "<tr>" + "<td colspan=\"2\">"
				+ "<div style=\"margin:0px 0px 0px 20px;float:left;height:50px;\">" + "<img height=\"60\" src=\"" + CF.getStrDocRetriveLocation()
				+ IConstants.I_ORGANISATION + "/" + IConstants.I_IMAGE + "/" + getStrOrgImage() + "\"/>" + "</div>"
				+ "<div style=\"float: left; margin: 31px 0px 0px;\">"
				+ "<div style=\"float: left; font-weight: bold; font-size: 10px; margin-top: 14px;\">Powered By </div>"
				+ "<div style=\"float: left;\"><img src=\"" + strPowerByImage + "\" style=\"width: 90px;\" height=\"40\"></div></div>" + "</td>"
				+ "<td align=\"right\"><a href=\"" + strLink + strLoginAction + "\"><input type=\"button\" class=\"input_button\" value=\"Login\""
				+ " style=\"margin-right:50px;background-repeat: repeat-x;border: 1px solid #5d862b;background-color:#339900;color: #FFFFFF;"
				+ "font-family: Verdana, arial, helvetica, sans-serif;font-weight: bold;font-size: 12px;height: 25px;padding-left: 4px;padding-top: 0px;"
				+ "cursor: pointer;-moz-border-radius: 3px;-webkit-border-radius: 3px;border-radius: 3px;width: 125px;width: 20%;"
				+ "padding: 3px 50px;outline: 0;margin: 10px 0px 0px 0px;\"/></a>" + "</td>" + "</tr>"
				+ "<tr><td style=\"border-bottom:1px solid #C5C5C5;\" colspan=\"3\">&nbsp;</td></tr>"
				+ "<tr><td colspan=\"3\" style=\"padding:10px;font-size: 12px;font-family:calibri;\"> [MAIL_BODY_TEMPLATE] </td></tr>"
				+ "<tr><td style=\"border-bottom:1px solid #C5C5C5;\" colspan=\"3\">&nbsp;</td></tr>" + "<tr>"
				+ "<td colspan=\"3\" style=\"text-align:center;background-color:#707070;color:white;font-size: 12px;font-family:calibri;\">"
				+ "<p>You have received this mail since you are subscribed to Workrig HCM-PPM package.</p>"
				+ "<p>If you have any problems in the mail, kindly write to us at support@workrig.com<br/>"
				+ "or call us at +91 20 2683 2117 or check us at www.workrig.com</p>" + "</td>" + "</tr>" + "</tbody>" + "</table>"
				+ "<div style=\"width:60%;font-size: 12px;font-family:calibri;\" align=\"center\">" + "<p><strong>Our Mail address is:</strong></p>"
				+ "<p>Workrig | Office No. 1, 3rd Floor, Kothari Plaza, Lulla Nagar, Pune - 411040, India</p>" + "<p><img src=\"" + strPowerByImage
				+ "\" style=\"width: 90px;\" height=\"40\"></p>" + "<p><a href=\"" + strLink + strLoginAction + "\">View in a browser</a></p>" + "</div>"
				+ "</div>"); // Taskrig Solutions Pvt. Ltd.

		// System.out.println("sb===>"+sb.toString());
		return sb.toString();
	}

	public void sendEmailNotifications() {

		if (CF != null && getStrHost() != null && getStrHost().indexOf("gmail") > 0) {
//			 System.out.println("NF/1380--- in sendEmailNotificationsFromGmail.....");
			sendEmailNotificationsFromGmail();
		} else if (CF != null && getIsRequiredAuthentication()) {
//			 System.out.println("NF/1383--- in sendEmailAuthenticationFromServer.....");
			sendEmailAuthenticationFromServer();
		} else {
//			 System.out.println("NF/1386--- in sendEmailNotificationsFromServer.....");
			sendEmailNotificationsFromServer();
		}

		// sendEmailNotificationsFromGmail();

	}

	private void sendEmailAuthenticationFromServer() {
		Transport t = null;

		try {
			Properties props = System.getProperties();
			props.put("mail.transport.protocol", "smtps");
			props.put("mail.smtp.port", 25);
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.starttls.required", "true");

			Session session = Session.getDefaultInstance(props);

			// Instantiatee a message
			MimeMessage msg = new MimeMessage(session);
			Multipart multipart = new MimeMultipart();
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			MimeBodyPart attachmentPart = new MimeBodyPart();

			// Set message attributes
			msg.setFrom(new InternetAddress(getStrEmailFrom()));
			// setStrEmailTo("priyanka.tekale@workrig.com");

			if (getStrEmailTo() != null) {
				InternetAddress[] address = null;
				address = new InternetAddress[1];
				// setStrEmailTo("priyanka.tekale@workrig.com");

				address[0] = new InternetAddress(getStrEmailTo());

				msg.setRecipients(Message.RecipientType.TO, address);
				msg.setSubject(getStrEmailSubject());
				msg.setSentDate(new Date());
				// msg.setHeader("X-SES-CONFIGURATION-SET", CONFIGSET);
				// Set message content
				messageBodyPart.setContent(getStrEmailBody(), "text/html");
				multipart.addBodyPart(messageBodyPart);

				if (getStrAttachmentFileSource() != null) {
					DataSource source = new FileDataSource(getStrAttachmentFileSource());
					attachmentPart.setDataHandler(new DataHandler(source));
					attachmentPart.setFileName(source.getName());
					if (getStrAttachmentFileName() != null) {
						attachmentPart.setFileName(getStrAttachmentFileName());
					}
					multipart.addBodyPart(attachmentPart);
				}

				if (pdf_data != null) {

					DataSource dataSource = new ByteArrayDataSource(pdf_data, "application/pdf");
					MimeBodyPart pdfBodyPart = new MimeBodyPart();
					pdfBodyPart.setDataHandler(new DataHandler(dataSource));
					pdfBodyPart.setFileName(getStrAttachmentFileName());

					multipart.addBodyPart(pdfBodyPart);
				}
				if (xls_data != null) {

					DataSource dataSource = new ByteArrayDataSource(xls_data, "application/xls");
					MimeBodyPart pdfBodyPart = new MimeBodyPart();
					pdfBodyPart.setDataHandler(new DataHandler(dataSource));
					pdfBodyPart.setFileName(getStrAttachmentFileName());

					multipart.addBodyPart(pdfBodyPart);

				}

				// Associate multi-part with message
				msg.setContent(multipart);
//				 System.out.println("mail sent to ====>" + getStrEmailTo());

				t = session.getTransport();
				t.connect(getStrHost(), getStrEmailAuthUsername(), getStrEmailAuthPassword());
//				 System.out.println("mail sent to ====>" + getStrEmailTo());
				t.sendMessage(msg, msg.getAllRecipients());
			}

			session = null;

		} catch (MessagingException mex) {
			// Prints all nested (chained) exceptions as well
			mex.printStackTrace();
		} finally {
			if (t != null) {
				try {
					t.close();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// private void sendEmailAuthenticationFromServer() {
	//// Database db=new Database();
	//// db.setRequest(request);
	//// Connection con=null;
	//
	// try {
	//// con=db.makeConnection(con);
	// Properties props = new Properties();
	//
	// props.put("mail.smtp.host", getStrHost());
	// props.put("mail.debug", "true");
	// props.put("mail.smtp.auth", "true");
	//
	// Session session = Session.getInstance(props,
	// new javax.mail.Authenticator() {
	// protected PasswordAuthentication getPasswordAuthentication() {
	// return new PasswordAuthentication(getStrEmailAuthUsername(),
	// getStrEmailAuthPassword());
	// }
	// });
	//
	//// Properties props = new Properties();
	//// props.setProperty("mail.transport.protocol", "smtp");
	//// props.setProperty("mail.smtp.auth", "true");
	//// props.setProperty("mail.host", "email-smtp.us-east-1.amazonaws.com");
	//// props.setProperty("mail.user", getStrEmailAuthUsername());
	//// props.setProperty("mail.password", getStrEmailAuthPassword());
	////
	//// Session session = Session.getDefaultInstance(props, new
	// Authenticator(){
	//// public PasswordAuthentication getPasswordAuthentication() {
	//// String username = getStrEmailAuthUsername();
	//// String password = getStrEmailAuthPassword();
	//// return new PasswordAuthentication(username, password);
	//// }
	//// });
	//
	// // Instantiatee a message
	// MimeMessage msg = new MimeMessage(session);
	// Multipart multipart = new MimeMultipart();
	// MimeBodyPart messageBodyPart = new MimeBodyPart();
	// MimeBodyPart attachmentPart = new MimeBodyPart();
	//
	// //Set message attributes
	// msg.setFrom(new InternetAddress(getStrEmailFrom()));
	//
	// InternetAddress[] address = null;
	//// if(getStrEmailTo()!=null &&
	// !getStrEmailTo().equalsIgnoreCase(EVERYONE)){
	// address = new InternetAddress[1];
	// address[0] = new InternetAddress(getStrEmailTo());
	//// }else if(getStrEmailTo()!=null &&
	// getStrEmailTo().equalsIgnoreCase(EVERYONE)){
	////
	//// Map<String, String> hmEmpEmail = CF.getEmpEmailMap(con);
	//// if(hmEmpEmail==null){
	//// hmEmpEmail = new HashMap<String, String>();
	//// }
	////
	//// address = new InternetAddress[hmEmpEmail.size()];
	//// Set set = hmEmpEmail.keySet();
	//// Iterator it = set.iterator();
	//// int count=0;
	//// while(it.hasNext()){
	//// String strEmail = (String)hmEmpEmail.get((String)it.next());
	//// if(strEmail!=null){
	//// address[count++] = new InternetAddress(strEmail);
	//// }
	//// }
	//// }
	//
	// msg.setRecipients(Message.RecipientType.TO, address);
	// msg.setSubject(getStrEmailSubject());
	// msg.setSentDate(new Date());
	//
	// // Set message content
	// messageBodyPart.setContent(getStrEmailBody(), "text/html");
	// multipart.addBodyPart(messageBodyPart);
	//
	//
	// if(getStrAttachmentFileSource()!=null){
	// DataSource source = new FileDataSource(getStrAttachmentFileSource());
	// attachmentPart.setDataHandler(new DataHandler(source));
	// attachmentPart.setFileName(source.getName());
	// if(getStrAttachmentFileName()!=null){
	// attachmentPart.setFileName(getStrAttachmentFileName());
	// }
	// multipart.addBodyPart(attachmentPart);
	// }
	//
	//
	// if(pdf_data!=null){
	//
	// DataSource dataSource = new ByteArrayDataSource(pdf_data,
	// "application/pdf");
	// MimeBodyPart pdfBodyPart = new MimeBodyPart();
	// pdfBodyPart.setDataHandler(new DataHandler(dataSource));
	// pdfBodyPart.setFileName(getStrAttachmentFileName());
	//
	// multipart.addBodyPart(pdfBodyPart);
	// }
	// if(xls_data!=null){
	//
	// DataSource dataSource = new ByteArrayDataSource(xls_data,
	// "application/xls");
	// MimeBodyPart pdfBodyPart = new MimeBodyPart();
	// pdfBodyPart.setDataHandler(new DataHandler(dataSource));
	// pdfBodyPart.setFileName(getStrAttachmentFileName());
	//
	// multipart.addBodyPart(pdfBodyPart);
	// }
	//
	// // Associate multi-part with message
	// msg.setContent(multipart);
	//
	//
	// // Send message
	//// Transport.send(msg);
	// Transport t = session.getTransport("smtp");
	// t.connect(getStrHost(), getStrEmailFrom(), getStrHostPassword());
	// t.sendMessage(msg, msg.getAllRecipients());
	//// t.close();
	////
	//// session = null;
	//
	// }
	// catch (MessagingException mex) {
	// // Prints all nested (chained) exceptions as well
	// mex.printStackTrace();
	// } finally {
	//// db.closeConnection(con);
	// }
	//
	// }

	private void sendEmailNotificationsFromServer() {

		// Create properties, get Session
		Properties props = new Properties();
		// String PORT = "80";
		// If using static Transport.send(),
		// need to specify which host to send it to
		props.put("mail.smtp.host", getStrHost());
		// To see what is going on behind the scene
		props.put("mail.debug", "true");
		// props.put("mail.smtp.port", PORT);
		// props.put("mail.smtp.ssl.enable", "true");
		// System.out.println("other server mail port");
		// props.put("mail.smtp.auth", "true");
		// props.put("mail.smtp.port", "587");
		if (CF.isOffice365Smtp()) {
			props.put("mail.smtp.starttls.enable", "true");
		}
		Session session = Session.getInstance(props);
		// Database db=new Database();
		// db.setRequest(request);
		// Connection con=null;
		// con=db.makeConnection(con);
		try {
			// Instantiatee a message
			MimeMessage msg = new MimeMessage(session);
			Multipart multipart = new MimeMultipart();
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			MimeBodyPart attachmentPart = new MimeBodyPart();

			// Set message attributes
			msg.setFrom(new InternetAddress(getStrEmailFrom()));

			InternetAddress[] address = null;
			// if(getStrEmailTo()!=null &&
			// !getStrEmailTo().equalsIgnoreCase(EVERYONE)){
			address = new InternetAddress[1];
			address[0] = new InternetAddress(getStrEmailTo());
			// }else if(getStrEmailTo()!=null &&
			// getStrEmailTo().equalsIgnoreCase(EVERYONE)){
			//
			// Map<String, String> hmEmpEmail = CF.getEmpEmailMap(con);
			// if(hmEmpEmail==null){
			// hmEmpEmail = new HashMap<String, String>();
			// }
			//
			// address = new InternetAddress[hmEmpEmail.size()];
			// Set set = hmEmpEmail.keySet();
			// Iterator it = set.iterator();
			// int count=0;
			// while(it.hasNext()){
			// String strEmail = (String)hmEmpEmail.get((String)it.next());
			// if(strEmail!=null){
			// address[count++] = new InternetAddress(strEmail);
			// }
			// }
			// }

			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject(getStrEmailSubject());
			msg.setSentDate(new Date());

			// Set message content
			messageBodyPart.setContent(getStrEmailBody(), "text/html");
			multipart.addBodyPart(messageBodyPart);

			if (getStrAttachmentFileSource() != null) {
				DataSource source = new FileDataSource(getStrAttachmentFileSource());
				attachmentPart.setDataHandler(new DataHandler(source));
				attachmentPart.setFileName(source.getName());
				if (getStrAttachmentFileName() != null) {
					attachmentPart.setFileName(getStrAttachmentFileName());
				}
				multipart.addBodyPart(attachmentPart);
			}

			if (pdf_data != null) {

				DataSource dataSource = new ByteArrayDataSource(pdf_data, "application/pdf");
				MimeBodyPart pdfBodyPart = new MimeBodyPart();
				pdfBodyPart.setDataHandler(new DataHandler(dataSource));
				pdfBodyPart.setFileName(getStrAttachmentFileName());

				multipart.addBodyPart(pdfBodyPart);
			}
			if (xls_data != null) {

				DataSource dataSource = new ByteArrayDataSource(xls_data, "application/xls");
				MimeBodyPart pdfBodyPart = new MimeBodyPart();
				pdfBodyPart.setDataHandler(new DataHandler(dataSource));
				pdfBodyPart.setFileName(getStrAttachmentFileName());

				multipart.addBodyPart(pdfBodyPart);
			}

			// Associate multi-part with message
			msg.setContent(multipart);

			// Send message
			// Transport.send(msg);
			Transport t = session.getTransport("smtp");
			t.connect(getStrHost(), getStrEmailFrom(), getStrHostPassword());
			t.sendMessage(msg, msg.getAllRecipients());
			t.close();

		} catch (MessagingException mex) {
			// Prints all nested (chained) exceptions as well
			mex.printStackTrace();
		} finally {
			// db.closeConnection(con);
		}

	}

	private void sendEmailNotificationsFromGmail() {

		// Create properties, get Session
		// Properties props = new Properties();
		//
		// // If using static Transport.send(),
		// // need to specify which host to send it to
		// props.put("mail.smtp.host", getStrHost());
		// // To see what is going on behind the scene
		// props.put("mail.debug", "true");

		Properties props = new Properties();
		props.put("mail.smtp.host", getStrHost());
		// props.put("mail.smtp.port", "587");
		// props.put("mail.smtp.port", "465");
		props.put("mail.debug", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		// props.put("mail.smtp.socketFactory.port", "465");
		// props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");

		Session session = Session.getInstance(props);
		// Database db=new Database();
		// db.setRequest(request);
		// Connection con=null;
		// con=db.makeConnection(con);
		try {
			// Instantiatee a message
			MimeMessage msg = new MimeMessage(session);
			Multipart multipart = new MimeMultipart();
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			MimeBodyPart attachmentPart = new MimeBodyPart();

			// Set message attributes
			msg.setFrom(new InternetAddress(getStrEmailFrom()));

			InternetAddress[] address = null;

			// System.out.println("getStrEmailTo()===>"+getStrEmailTo());

			if (getStrEmailTo() != null && !getStrEmailTo().equalsIgnoreCase(EVERYONE)) {
				address = new InternetAddress[1];
				address[0] = new InternetAddress(getStrEmailTo());
			} else if (getStrSupervisorEmail() != null && isSupervisor()) {
				address = new InternetAddress[1];
				address[0] = new InternetAddress(getStrSupervisorEmail());
			} /*
				 * else if(getStrEmailTo()!=null &&
				 * getStrEmailTo().equalsIgnoreCase(EVERYONE)){ Map<String,
				 * String> hmEmpEmail = CF.getEmpEmailMap(con);
				 * if(hmEmpEmail==null){ hmEmpEmail = new HashMap<String,
				 * String>(); }
				 * 
				 * 
				 * address = new InternetAddress[hmEmpEmail.size()];
				 * 
				 * Set set = hmEmpEmail.keySet(); Iterator it = set.iterator();
				 * int count=0; while(it.hasNext()){ String strEmail =
				 * (String)hmEmpEmail.get((String)it.next());
				 * if(strEmail!=null){ address[count++] = new
				 * InternetAddress(strEmail); } } }
				 */
			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject(getStrEmailSubject());
			msg.setSentDate(new Date());

			// Set message content
			messageBodyPart.setContent(getStrEmailBody(), "text/html");
			multipart.addBodyPart(messageBodyPart);

			// System.out.println("messageBodyPart===>"+getStrEmailBody());

			if (getStrAttachmentFileSource() != null) {

				DataSource source = new FileDataSource(getStrAttachmentFileSource());
				attachmentPart.setDataHandler(new DataHandler(source));
				attachmentPart.setFileName(source.getName());
				if (getStrAttachmentFileName() != null) {
					attachmentPart.setFileName(getStrAttachmentFileName());
				}

				multipart.addBodyPart(attachmentPart);
			}

			if (pdf_data != null) {

				DataSource dataSource = new ByteArrayDataSource(pdf_data, "application/pdf");
				MimeBodyPart pdfBodyPart = new MimeBodyPart();
				pdfBodyPart.setDataHandler(new DataHandler(dataSource));
				pdfBodyPart.setFileName(getStrAttachmentFileName());
				multipart.addBodyPart(pdfBodyPart);
			}
			if (xls_data != null) {

				DataSource dataSource = new ByteArrayDataSource(xls_data, "application/xls");
				MimeBodyPart pdfBodyPart = new MimeBodyPart();
				pdfBodyPart.setDataHandler(new DataHandler(dataSource));
				pdfBodyPart.setFileName(getStrAttachmentFileName());
				multipart.addBodyPart(pdfBodyPart);
			}

			// Associate multi-part with message
			msg.setContent(multipart);

			// Send message
			// Transport.send(msg);
			Transport t = session.getTransport("smtp");
			t.connect(getStrHost(), getStrEmailFrom(), getStrHostPassword());
			t.sendMessage(msg, msg.getAllRecipients());
			t.close();

		} catch (MessagingException mex) {
			// Prints all nested (chained) exceptions as well
			mex.printStackTrace();
		} finally {
			// db.closeConnection(con);
		}

	}

	byte[] pdf_data;
	public void setPdfData(byte[] pdf_data) {
		this.pdf_data = pdf_data;
	}

	byte[] xls_data;
	public void setXlsData(byte[] xls_data) {
		this.xls_data = xls_data;
	}

	private void setEmpDetails() {
		String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		// System.out.println("N strDomain ===>> " + strDomain);
		// System.out.println("N request ===>> " + request);
		db.setDomain(strDomain);
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;

		try {

			if (CF == null)
				CF = new CommonFunctions(request);

			con = db.makeConnection(con);

			setStrDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, CF.getStrReportDateFormat()));

			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
			Map hmWLocationMap = CF.getWorkLocationMap(con);

			Map<String, String> hmOrg = CF.getEmpOrgDetails(con, uF, getStrEmpId());
			Map<String, String> hmDepartment = CF.getDepartmentMap(con, null, null);
			if (hmDepartment == null)
				hmDepartment = new HashMap<String, String>();
			Map<String, String> hmEmpDepartment = CF.getEmpDepartmentMap(con);
			if (hmEmpDepartment == null)
				hmEmpDepartment = new HashMap<String, String>();

			Map<String, String> hmSBUMap = CF.getServicesMap(con, false);

			Map<String, String> hmSkillName = CF.getSkillNameMap(con);
			if (hmSkillName == null)
				hmSkillName = new HashMap<String, String>();

			List<String> empSkillsList = CF.getEmpSkillsList(con, uF, getStrEmpId());
			if (empSkillsList == null)
				empSkillsList = new ArrayList<String>();

			Map<String, String> hmEmpProbationEnd = CF.getEmpProbationEndDateMap(con, uF);
			String EmpProbationEnd = hmEmpProbationEnd.get(getStrEmpId().trim());
			Date probationEndDate = uF.getDateFormat(EmpProbationEnd, DATE_FORMAT);
			setStrEmpProbationEndDate(
					probationEndDate != null && !probationEndDate.equals("") ? "" + uF.getDateFormat("" + probationEndDate, DBDATE, "dd/MMM/yyyy") : "");

			if (probationEndDate != null && !probationEndDate.equals("")) {
				Date confirmationDate = uF.getFutureDate(probationEndDate, 1);
				setStrEmpConfirmationDate(
						confirmationDate != null && !confirmationDate.equals("") ? uF.getDateFormat("" + confirmationDate, DBDATE, "dd/MMM/yyyy") : "");
			} else {
				setStrEmpConfirmationDate("");
			}
			Map<String, String> hmEmpOrg = new HashMap<String, String>();
			pst = con.prepareStatement("select * from org_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmEmpOrg.put(rs.getString("org_id"), rs.getString("org_name"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement(selectEmpDetails1);
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			int nSupervisorId = 0;
			int nHrId = 0;
			int nCount = 0;
			String serviceID = "0";
			// System.out.println("set Emailpstt===>"+pst);
			while (rs.next()) {
				nSupervisorId = rs.getInt("supervisor_emp_id");
				nHrId = rs.getInt("emp_hr");
				setStrEmpCode(rs.getString("empcode"));
				setStrSalutation(uF.showData(rs.getString("salutation"), ""));
				serviceID = (rs.getString("service_id") != null && rs.getString("service_id").length() > 0)
						? rs.getString("service_id").substring(1, rs.getString("service_id").length() - 1)
						: "";

				if (rs.getString("emp_email_sec") != null && rs.getString("emp_email_sec").indexOf("@") > 0) {
					setStrEmpEmail(rs.getString("emp_email_sec"));
					setStrEmailTo(rs.getString("emp_email_sec"));
					// System.out.println("In set Email1");
					setStrEmpEmailID(rs.getString("emp_email_sec"));
				} else {
					setStrEmpEmail(rs.getString("emp_email"));
					setStrEmailTo(rs.getString("emp_email"));

					setStrEmpEmailID(rs.getString("emp_email"));
				}
				// System.out.println("setStrEmpEmail::"+getStrEmpEmail()+"setStrEmailTo::"+getStrEmailTo()+"setStrEmpEmailID::"+getStrEmpEmailID());
				setStrBloodGroup(rs.getString("blood_group"));
				setStrEmpFname(rs.getString("emp_fname"));
				// seteventEmpLName(rs.getString("emp_lname"));
				setStrEmpLname(rs.getString("emp_lname"));
				setStrEmpPancardNo(uF.showData(rs.getString("emp_pan_no"), ""));
				setStrEmpDateOfBirth(rs.getString("emp_date_of_birth") != null
						? uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, CF.getStrReportDateFormat())
						: "-");
				setStrEmpEmergencyContactDetails("Name:" + uF.showData(rs.getString("emergency_contact_name"), "") + " Contact No.: "
						+ uF.showData(rs.getString("emergency_contact_no"), ""));
				setStrEmpContactNo(
						rs.getString("emp_contactno_mob") != null ? rs.getString("emp_contactno_mob") : uF.showData(rs.getString("emp_contactno"), "-"));
				String imageUrl = rs.getString("emp_image");

				String Link = "";
				// imageUrl="http://108.163.186.66:8080/WR/Impact-Infotech/userImages/"+rs.getString("emp_image");
				if (rs.getString("emp_image") != null) {
					imageUrl = docRetriveLocation + IConstants.I_PEOPLE + "/" + IConstants.I_IMAGE + "/" + rs.getString("emp_per_id") + "/"
							+ IConstants.I_100x100 + "/" + rs.getString("emp_image");
					Link = "<img src=\"" + imageUrl + "\">";
				} else {
					imageUrl = "";
					Link = "";
				}
				String strAge = "0";
				if (rs.getString("emp_date_of_birth") != null && !rs.getString("emp_date_of_birth").equals("")) {
					strAge = uF.getTimeDurationBetweenDatesInYearsOnly(rs.getString("emp_date_of_birth"), DBDATE, "" + uF.getCurrentDate(CF.getStrTimeZone()),
							DBDATE, CF, uF, request);
				}
				setStrAge(strAge);
				setStrProfileImage(Link);
				setStrEmpAddress(uF.showData(rs.getString("emp_address1"), "") + (!uF.showData(rs.getString("emp_address1"), "").equals("") ? "," : "")
						+ uF.showData(rs.getString("emp_address2"), "") + (!uF.showData(rs.getString("emp_address2"), "").equals("") ? ", " : "")
						+ uF.showData(rs.getString("emp_city_id"), ""));
				setStrEmpFullNamename(uF.showData(rs.getString("emp_fname"), "") + " " + uF.showData(rs.getString("emp_lname"), ""));
				setStrEmpMobileNo(rs.getString("emp_contactno_mob"));
				setStrAccountNo(rs.getString("emp_bank_acct_nbr"));
				setStrUserName(rs.getString("username"));
				setStrPassword(rs.getString("password"));
				setStrJoiningDate(uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));
				if (rs.getString("joining_date") != null) {
					setStrOfferAcceptanceLastDate(uF.getDateFormat(uF.getPrevDate(uF.getDateFormatUtil(rs.getString("joining_date"), DBDATE), 5) + "", DBDATE,
							CF.getStrReportDateFormat()));
				}
				setStrEmpLevel(uF.showData(hmLevelMap.get(hmEmpLevelMap.get(rs.getString("emp_per_id"))), ""));
				setStrEmpDesignation(uF.showData(hmEmpDesigMap.get(rs.getString("emp_per_id")), ""));
				setStrEmpGrade(uF.showData(hmGradeMap.get(rs.getString("grade_id")), ""));

				Map<String, String> hmLocation = (Map) hmWLocationMap.get(rs.getString("wlocation_id"));
				if (hmLocation == null)
					hmLocation = new HashMap<String, String>();
				setStrEmpWLocation(hmLocation.get("WL_NAME"));
				setStrEmpOrganization(hmEmpOrg.get(rs.getString("org_id")));
				if (uF.parseToInt(getStrOrgId()) == 0) {
					setStrOrgId(rs.getString("org_id"));
				}
				setStrOrganisationAddress(uF.showData(hmOrg.get("ORG_ADDRESS"), ""));
				setStrLegalEntityName(uF.showData(hmEmpOrg.get(rs.getString("org_id")), ""));
				setStrLegalEntityAddress(uF.showData(hmOrg.get("ORG_ADDRESS"), ""));
				setStrDepartmentName(uF.showData(hmDepartment.get(hmEmpDepartment.get(rs.getString("emp_per_id"))), ""));

				List<String> sbuIdList = null;
				if (rs.getString("service_id") != null) {
					sbuIdList = Arrays.asList(rs.getString("service_id").split(","));
				}
				setStrSbuName(CF.getAppendNames(sbuIdList, hmSBUMap));

				setStrSkills(CF.getAppendNames(empSkillsList, hmSkillName));

				setStrPromotionGrade(uF.showData(hmGradeMap.get(rs.getString("grade_id")), ""));
				setStrPromotionLevel(uF.showData(hmLevelMap.get(hmEmpLevelMap.get(rs.getString("emp_per_id"))), ""));
				setStrPromotionDesignation(uF.showData(hmEmpDesigMap.get(rs.getString("emp_per_id")), ""));

				setStrTerminateDate(uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, CF.getStrReportDateFormat()));
				setStrLastDateAtOffice(uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, CF.getStrReportDateFormat()));

				setStrEmpCTC(uF.formatIntoComma(uF.parseToDouble(rs.getString("month_ctc"))));
				setStrEmpAnnualCTC(uF.formatIntoComma(uF.parseToDouble(rs.getString("annual_ctc"))));
				setStrPrevMonthCTC(uF.formatIntoComma(uF.parseToDouble(rs.getString("prev_month_ctc"))));
				setStrPrevAnnualCTC(uF.formatIntoComma(uF.parseToDouble(rs.getString("prev_annual_ctc"))));
				setStrIncrementAmtMonth(uF.formatIntoComma(uF.parseToDouble(rs.getString("incre_month_amount"))));
				setStrIncrementAmtAnnual(uF.formatIntoComma(uF.parseToDouble(rs.getString("incre_annual_amount"))));

				nCount++;

			}
			rs.close();
			pst.close();

			/*
			 * if(getStrLastDateAtOffice()==null ||
			 * getStrLastDateAtOffice().equals("") ||
			 * getStrLastDateAtOffice().equals("-")) { pst = con.
			 * prepareStatement("select * from emp_off_board where approved_1=1 and approved_2=1 and emp_id=?"
			 * ); pst.setInt(1, uF.parseToInt(getStrEmpId())); rs =
			 * pst.executeQuery(); while (rs.next()) {
			 * setStrTerminateDate(uF.getDateFormat(rs.getString("last_day_date"
			 * ), DBDATE, CF.getStrReportDateFormat()));
			 * setStrLastDateAtOffice(uF.getDateFormat(rs.getString(
			 * "last_day_date"), DBDATE, CF.getStrReportDateFormat())); }
			 * rs.close(); pst.close(); }
			 */

			if (nCount == 0) {
				pst = con.prepareStatement("select * from employee_personal_details where emp_per_id=?");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				rs = pst.executeQuery();
				// System.out.println("Email pst ===> "+pst);
				while (rs.next()) {

					if (rs.getString("emp_email_sec") != null && rs.getString("emp_email_sec").indexOf("@") > 0) {
						setStrEmpEmail(rs.getString("emp_email_sec"));
						setStrEmailTo(rs.getString("emp_email_sec"));
						setStrEmpEmailID(rs.getString("emp_email_sec"));
					} else {
						setStrEmpEmail(rs.getString("emp_email"));
						setStrEmailTo(rs.getString("emp_email"));
						setStrEmpEmailID(rs.getString("emp_email"));
					}
					/*
					 * setStrEmpEmail(rs.getString("emp_email"));
					 * setStrEmailTo(rs.getString("emp_email"));
					 */
					setStrEmpFname(rs.getString("emp_fname"));
					setStrEmpLname(rs.getString("emp_lname"));
					setStrEmpContactNo(
							rs.getString("emp_contactno_mob") != null ? rs.getString("emp_contactno_mob") : uF.showData(rs.getString("emp_contactno"), "-"));
					setStrEmpFullNamename(uF.showData(rs.getString("emp_fname"), "") + " " + uF.showData(rs.getString("emp_lname"), ""));
				}
				rs.close();
				pst.close();
			}

			pst = con.prepareStatement("select * from goal_kras where emp_ids like '%," + getStrEmpId() + ",%' and goal_type = " + EMPLOYEE_KRA
					+ " and is_assign = true and effective_date = (select max(effective_date) from goal_kras where effective_date <= ? and emp_ids like '%,"
					+ getStrEmpId() + ",%' and goal_type = " + EMPLOYEE_KRA + ")");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			StringBuilder sbKRAs = new StringBuilder();
			while (rs.next()) {
				if (rs.getString("kra_description") != null && rs.getString("kra_description").length() > 0) {
					sbKRAs.append("- " + rs.getString("kra_description").replace("\n", "<br/>") + "<br/>");
				}
			}
			rs.close();
			pst.close();
			setStrEmpKRAs(sbKRAs.toString());

			/*
			 * if(getStrMgrId()!=null && !isSupervisor()){ nSupervisorId =
			 * uF.parseToInt(getStrMgrId()); setSupervisor(true); }
			 */

			pst = con.prepareStatement(
					"select state_name from state where state_id =(select emp_state_id_tmp from employee_personal_details where emp_per_id =? )");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				setStrstate(rs.getString("state_name"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select service_name from services where service_id = ?");
			pst.setInt(1, uF.parseToInt(serviceID));
			rs = pst.executeQuery();
			while (rs.next()) {
				setStrEmpSbuName(rs.getString("service_name"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from emp_family_members where emp_id=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getString("member_type").equalsIgnoreCase("FATHER")) {
					setStrFatherName(rs.getString("member_name"));
				}
			}
			rs.close();
			pst.close();
			// System.out.println("getStrFatherName ===>> " +
			// getStrFatherName());

			if (nHrId > 0) {
				pst = con.prepareStatement(
						"select emp_fname , emp_lname from employee_personal_details where emp_per_id  = (select emp_hr from employee_official_details  where emp_id = ? )");
				pst.setInt(1, nHrId);
				rs = pst.executeQuery();
				while (rs.next()) {
					setStrHrName(rs.getString("emp_fname") + " " + rs.getString("emp_lname"));
				}
				rs.close();
				pst.close();
			}

			if (nSupervisorId > 0 && isSupervisor()) {
				pst = con.prepareStatement(selectEmpDetails1);
				pst.setInt(1, nSupervisorId);
				rs = pst.executeQuery();
				while (rs.next()) {
					setStrSupervisorEmail(rs.getString("emp_email"));
					setStrSupervisorName(rs.getString("emp_fname") + " " + rs.getString("emp_lname"));
					setStrSupervisorContactNo(
							rs.getString("emp_contactno_mob") != null ? rs.getString("emp_contactno_mob") : uF.showData(rs.getString("emp_contactno"), "-"));
					if (rs.getString("emp_email_sec") != null && rs.getString("emp_email_sec").indexOf("@") > 0) {
						setStrEmpEmail(rs.getString("emp_email_sec"));
						setStrEmailTo(rs.getString("emp_email_sec"));
					} else {
						setStrEmpEmail(rs.getString("emp_email"));
						setStrEmailTo(rs.getString("emp_email"));
					}
				}
				rs.close();
				pst.close();
			} else if (nSupervisorId > 0) {
				pst = con.prepareStatement(selectEmpDetails1);
				pst.setInt(1, nSupervisorId);
				rs = pst.executeQuery();
				while (rs.next()) {
					setStrSupervisorEmail(rs.getString("emp_email"));
					setStrSupervisorName(rs.getString("emp_fname") + " " + rs.getString("emp_lname"));
					setStrSupervisorContactNo(
							rs.getString("emp_contactno_mob") != null ? rs.getString("emp_contactno_mob") : uF.showData(rs.getString("emp_contactno"), "-"));
				}
				rs.close();
				pst.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	// private void setEmpDetails(Connection con) {
	// String docRetriveLocation = (String)
	// request.getAttribute("DOC_RETRIVE_LOCATION");
	// PreparedStatement pst = null;
	// UtilityFunctions uF = new UtilityFunctions();
	// ResultSet rs=null;
	//
	// try {
	//
	// if(CF==null) CF = new CommonFunctions(request);
	//
	// setStrDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"",
	// DBDATE, CF.getStrReportDateFormat()));
	//
	// Map<String, String> hmGradeMap = CF.getGradeMap(con);
	// Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
	// Map<String, String> hmLevelMap = CF.getLevelMap(con);
	// Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
	// Map hmWLocationMap = CF.getWorkLocationMap(con);
	//
	// Map<String, String> hmOrg = CF.getEmpOrgDetails(con, uF, getStrEmpId());
	// Map<String, String> hmDepartment = CF.getDepartmentMap(con, null, null);
	// if(hmDepartment == null) hmDepartment = new HashMap<String, String>();
	// Map<String, String> hmEmpDepartment = CF.getEmpDepartmentMap(con);
	// if(hmEmpDepartment == null) hmEmpDepartment = new HashMap<String,
	// String>();
	//
	// Map<String, String> hmSBUMap = CF.getServicesMap(con, false);
	//
	// Map<String,String> hmSkillName = CF.getSkillNameMap(con);
	// if(hmSkillName == null) hmSkillName = new HashMap<String, String>();
	//
	// List<String> empSkillsList = CF.getEmpSkillsList(con, uF, getStrEmpId());
	// if(empSkillsList == null) empSkillsList = new ArrayList<String>();
	//
	// Map<String, String> hmEmpProbationEnd = CF.getEmpProbationEndDateMap(con,
	// uF);
	// String EmpProbationEnd=hmEmpProbationEnd.get(getStrEmpId().trim());
	// Date probationEndDate=uF.getDateFormat(EmpProbationEnd, DATE_FORMAT);
	// setStrEmpProbationEndDate(probationEndDate!=null &&
	// !probationEndDate.equals("")? ""+uF.getDateFormat(""+probationEndDate,
	// DBDATE, "dd/MMM/yyyy"): "");
	//
	// if(probationEndDate!=null && !probationEndDate.equals("")){
	// Date confirmationDate=uF.getFutureDate(probationEndDate, 1);
	// setStrEmpConfirmationDate(confirmationDate!=null &&
	// !confirmationDate.equals("")? uF.getDateFormat(""+confirmationDate,
	// DBDATE, "dd/MMM/yyyy"): "");
	// }else{
	// setStrEmpConfirmationDate("");
	// }
	// Map<String, String> hmEmpOrg = new HashMap<String, String>();
	// pst = con.prepareStatement("select * from org_details");
	// rs = pst.executeQuery();
	// while (rs.next()) {
	// hmEmpOrg.put(rs.getString("org_id"), rs.getString("org_name"));
	// }
	// rs.close();
	// pst.close();
	//
	// pst = con.prepareStatement(selectEmpDetails1);
	// pst.setInt(1, uF.parseToInt(getStrEmpId()));
	// rs = pst.executeQuery();
	//
	// int nSupervisorId = 0;
	// int nHrId =0;
	// int nCount = 0;
	// String serviceID ="0";
	// while(rs.next()){
	// nSupervisorId = rs.getInt("supervisor_emp_id");
	// nHrId = rs.getInt("emp_hr");
	// setStrEmpCode(rs.getString("empcode"));
	// serviceID = (rs.getString("service_id") != null &&
	// rs.getString("service_id").length()>0) ?
	// rs.getString("service_id").substring(1,
	// rs.getString("service_id").length()-1) : "";
	//
	// if(rs.getString("emp_email_sec")!=null &&
	// rs.getString("emp_email_sec").indexOf("@")>0) {
	// setStrEmpEmail(rs.getString("emp_email_sec"));
	// setStrEmailTo(rs.getString("emp_email_sec"));
	// } else {
	// setStrEmpEmail(rs.getString("emp_email"));
	// setStrEmailTo(rs.getString("emp_email"));
	// }
	// setStrBloodGroup(rs.getString("blood_group"));
	// setStrEmpFname(rs.getString("emp_fname"));
	// seteventEmpLName(rs.getString("emp_lname"));
	// String imageUrl=rs.getString("emp_image");
	// String Link="";
	// //imageUrl="http://108.163.186.66:8080/WR/Impact-Infotech/userImages/"+rs.getString("emp_image");
	// if(rs.getString("emp_image")!=null){
	// imageUrl=docRetriveLocation+IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+IConstants.I_100x100+"/"+rs.getString("emp_image");
	// Link ="<img src=\""+imageUrl+"\">";
	// }else{
	// imageUrl="";
	// Link ="";
	// }
	// String strAge = "0";
	// if(rs.getString("emp_date_of_birth") != null &&
	// !rs.getString("emp_date_of_birth").equals("")) {
	// strAge =
	// uF.getTimeDurationBetweenDatesInYearsOnly(rs.getString("emp_date_of_birth"),
	// DBDATE, ""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF, uF,
	// request);
	// }
	// setStrAge(strAge);
	// setStrProfileImage(Link);
	// setStrEmpAddress(uF.showData(rs.getString("emp_address1"), "") +
	// (!uF.showData(rs.getString("emp_address1"), "").equals("") ? "," : "") +
	// uF.showData(rs.getString("emp_address2"),
	// "")+(!uF.showData(rs.getString("emp_address2"), "").equals("") ? ", " :
	// "") +uF.showData(rs.getString("emp_city_id"), ""));
	// setStrEmpFullNamename(uF.showData(rs.getString("emp_fname"), "")+"
	// "+uF.showData(rs.getString("emp_lname"), ""));
	// setStrEmpMobileNo(rs.getString("emp_contactno"));
	// setStrAccountNo(rs.getString("emp_bank_acct_nbr"));
	// setStrUserName(rs.getString("username"));
	// setStrPassword(rs.getString("password"));
	// setStrJoiningDate(uF.getDateFormat(rs.getString("joining_date"), DBDATE,
	// CF.getStrReportDateFormat()));
	//
	// setStrEmpLevel(uF.showData(hmLevelMap.get(hmEmpLevelMap.get(rs.getString("emp_per_id"))),
	// ""));
	// setStrEmpDesignation(uF.showData(hmEmpDesigMap.get(rs.getString("emp_per_id")),
	// ""));
	// setStrEmpGrade(uF.showData(hmGradeMap.get(rs.getString("grade_id")),
	// ""));
	//
	// Map<String, String> hmLocation =
	// (Map)hmWLocationMap.get(rs.getString("wlocation_id"));
	// if(hmLocation==null)hmLocation=new HashMap<String, String>();
	// setStrEmpWLocation(hmLocation.get("WL_NAME"));
	// setStrEmpOrganization(hmEmpOrg.get(rs.getString("org_id")));
	// if(uF.parseToInt(getStrOrgId()) == 0){
	// setStrOrgId(rs.getString("org_id"));
	// }
	// setStrOrganisationAddress(uF.showData(hmOrg.get("ORG_ADDRESS"), ""));
	// setStrLegalEntityName(uF.showData(hmEmpOrg.get(rs.getString("org_id")),""));
	// setStrLegalEntityAddress(uF.showData(hmOrg.get("ORG_ADDRESS"), ""));
	// setStrDepartmentName(uF.showData(hmDepartment.get(hmEmpDepartment.get(rs.getString("emp_per_id"))),
	// ""));
	//
	// List<String> sbuIdList = null;
	// if(rs.getString("service_id") != null) {
	// sbuIdList = Arrays.asList(rs.getString("service_id").split(","));
	// }
	// setStrSbuName(CF.getAppendNames(sbuIdList, hmSBUMap));
	//
	// setStrSkills(CF.getAppendNames(empSkillsList, hmSkillName));
	//
	// setStrPromotionGrade(uF.showData(hmGradeMap.get(rs.getString("grade_id")),
	// ""));
	// setStrPromotionLevel(uF.showData(hmLevelMap.get(hmEmpLevelMap.get(rs.getString("emp_per_id"))),
	// ""));
	// setStrPromotionDesignation(uF.showData(hmEmpDesigMap.get(rs.getString("emp_per_id")),
	// ""));
	//
	// setStrTerminateDate(uF.getDateFormat(rs.getString("employment_end_date"),
	// DBDATE, CF.getStrReportDateFormat()));
	// setStrLastDateAtOffice(uF.getDateFormat(rs.getString("employment_end_date"),
	// DBDATE, CF.getStrReportDateFormat()));
	//
	// setStrEmpCTC(uF.formatIntoComma(uF.parseToDouble(rs.getString("month_ctc"))));
	// setStrEmpAnnualCTC(uF.formatIntoComma(uF.parseToDouble(rs.getString("annual_ctc"))));
	// setStrPrevMonthCTC(uF.formatIntoComma(uF.parseToDouble(rs.getString("prev_month_ctc"))));
	// setStrPrevAnnualCTC(uF.formatIntoComma(uF.parseToDouble(rs.getString("prev_annual_ctc"))));
	// setStrIncrementAmtMonth(uF.formatIntoComma(uF.parseToDouble(rs.getString("incre_month_amount"))));
	// setStrIncrementAmtAnnual(uF.formatIntoComma(uF.parseToDouble(rs.getString("incre_annual_amount"))));
	//
	// nCount++;
	//
	// }
	// rs.close();
	// pst.close();
	//
	// if(nCount==0){
	// pst = con.prepareStatement("select * from employee_personal_details where
	// emp_per_id=?");
	// pst.setInt (1, uF.parseToInt(getStrEmpId()));
	// rs = pst.executeQuery();
	//// System.out.println("pst ===> "+pst);
	// while(rs.next()){
	// setStrEmpEmail(rs.getString("emp_email"));
	// setStrEmailTo(rs.getString("emp_email"));
	// setStrEmpFname(rs.getString("emp_fname"));
	// seteventEmpLName(rs.getString("emp_lname"));
	// setStrEmpFullNamename(uF.showData(rs.getString("emp_fname"), "")+"
	// "+uF.showData(rs.getString("emp_lname"), ""));
	// }
	// rs.close();
	// pst.close();
	// }
	//
	// pst = con.prepareStatement("select * from goal_kras where emp_ids like
	// '%,"+getStrEmpId()+",%' and goal_type = "+EMPLOYEE_KRA+" and is_assign =
	// true and effective_date = (select max(effective_date) from goal_kras
	// where effective_date <= ? and emp_ids like '%,"+getStrEmpId()+",%' and
	// goal_type = "+EMPLOYEE_KRA+")");
	// pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
	// rs = pst.executeQuery();
	// StringBuilder sbKRAs = new StringBuilder();
	// while(rs.next()) {
	// if(rs.getString("kra_description")!=null &&
	// rs.getString("kra_description").length()>0){
	// sbKRAs.append("- "+rs.getString("kra_description").replace("\n",
	// "<br/>")+"<br/>");
	// }
	// }
	// rs.close();
	// pst.close();
	// setStrEmpKRAs(sbKRAs.toString());
	//
	// /*if(getStrMgrId()!=null && !isSupervisor()){
	// nSupervisorId = uF.parseToInt(getStrMgrId());
	// setSupervisor(true);
	// }*/
	//
	// pst = con.prepareStatement("select state_name from state where state_id
	// =(select emp_state_id_tmp from employee_personal_details where emp_per_id
	// =? )");
	// pst.setInt(1, uF.parseToInt(getStrEmpId()));
	// rs = pst.executeQuery();
	// while(rs.next()) {
	// setStrstate(rs.getString("state_name"));
	// }
	// rs.close();
	// pst.close();
	//
	// pst = con.prepareStatement("select service_name from services where
	// service_id = ?");
	// pst.setInt(1,uF.parseToInt(serviceID));
	// rs = pst.executeQuery();
	// while(rs.next()) {
	// setStrEmpSbuName(rs.getString("service_name"));
	// }
	// rs.close();
	// pst.close();
	//
	//
	// pst = con.prepareStatement("select * from emp_family_members where emp_id
	// = ?");
	// pst.setInt(1,uF.parseToInt(getStrEmpId()));
	// rs = pst.executeQuery();
	// while(rs.next()) {
	// if(rs.getString("member_type").equalsIgnoreCase("FATHER")) {
	// setStrFatherName(rs.getString("member_name"));
	// }
	// }
	// rs.close();
	// pst.close();
	//
	// if(nHrId>0) {
	// pst = con.prepareStatement("select emp_fname , emp_lname from
	// employee_personal_details where emp_per_id = (select emp_hr from
	// employee_official_details where emp_id = ? )");
	// pst.setInt(1, nHrId);
	// rs = pst.executeQuery();
	// while(rs.next()) {
	// setStrHrName(rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
	// }
	// rs.close();
	// pst.close();
	// }
	//
	//
	// if(nSupervisorId>0 && isSupervisor()) {
	// pst = con.prepareStatement(selectEmpDetails1);
	// pst.setInt(1, nSupervisorId);
	// rs = pst.executeQuery();
	// while(rs.next()) {
	// setStrSupervisorEmail(rs.getString("emp_email"));
	// setStrSupervisorName(rs.getString("emp_fname")+"
	// "+rs.getString("emp_lname"));
	// setStrSupervisorContactNo(rs.getString("emp_contactno"));
	// if(rs.getString("emp_email_sec")!=null &&
	// rs.getString("emp_email_sec").indexOf("@")>0) {
	// setStrEmpEmail(rs.getString("emp_email_sec"));
	// setStrEmailTo(rs.getString("emp_email_sec"));
	// } else {
	// setStrEmpEmail(rs.getString("emp_email"));
	// setStrEmailTo(rs.getString("emp_email"));
	// }
	// }
	// rs.close();
	// pst.close();
	// } else if(nSupervisorId>0) {
	// pst = con.prepareStatement(selectEmpDetails1);
	// pst.setInt(1, nSupervisorId);
	// rs = pst.executeQuery();
	// while(rs.next()) {
	// setStrSupervisorEmail(rs.getString("emp_email"));
	// setStrSupervisorName(rs.getString("emp_fname")+"
	// "+rs.getString("emp_lname"));
	// setStrSupervisorContactNo(rs.getString("emp_contactno"));
	// }
	// rs.close();
	// pst.close();
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	void backgroundImgforMail(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		// UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		try {
			// notificationId
			pst = con.prepareStatement("select * from notifications where notification_code = ?");
			pst.setInt(1, getnNotificationCode());
			rs = pst.executeQuery();
			while (rs.next()) {
				setBackground_img(rs.getString("background_image"));
			}

			// System.out.println(getBackground_img());
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// pst.setInt(1, uF.parseToInt(getStrEmpId()));
		// rs = pst.executeQuery();
		//
	}

	int nNotificationCode;
	String strHostAddress;
	String strContextPath;
	String strHostPort;

	String strDate;
	String background_img;
	String contentType;

	public String getBackground_img() {
		return background_img;
	}

	public void setBackground_img(String background_img) {
		this.background_img = background_img;
	}

	boolean isSupervisor;
	String strMgrId;
	String strEmpId;
	String strSupervisorName;
	String strSupervisorEmail;
	String strSupervisorContactNo;
	String strEmpCode;
	String strEmpFullNamename;
	String strSalutation;
	String strEmpFname;
	String strEmpMname;
	String strEmpLname;
	String strEmpEmailID;
	String strEmpContactNo;
	String strEmpPancardNo;
	String strEmpDateOfBirth;
	String strEmpEmergencyContactDetails;

	String strEmpAddress;
	String strJoiningDate;
	String strEmpCTC;
	String strEmpKRAs;

	String strSectionCode;
	String strInvestmentAmount;

	String strEmpLevel;
	String strEmpDesignation;
	String strEmpGrade;
	String strEmpWLocation;

	String strEmpEmail;
	String strEmpMobileNo;
	String strAddEmpLink;
	String strAddPeopleLink;

	String strEmpLeaveFrom;
	String strEmpLeaveTo;
	String strEmpLeaveNoOfDays;
	String strEmpLeaveReason;
	String strManagerLeaveReason;
	String strApprvedDenied;
	String strLeaveEmpBackup;	//added by parvez date: 18-03-2023

	String strEmpReimbursementFrom;
	String strEmpReimbursementTo;
	String strEmpReimbursementPurpose;
	String strEmpReimbursementAmount;
	String strEmpReimbursementType;
	String strEmpReimbursementDate;
	String strEmpReimbursementCurrency;

	String strEmpReqPurpose;
	String strEmpReqMode;
	String strEmpReqType;
	String strEmpReqFrom;
	String strEmpReqTo;

	String strSalaryAmount;
	String strTextSalaryAmount;
	String strPaycycle;
	String strAccountNo;

	String strAnnouncementHeading;
	String strAnnouncementBody;

	String strNewEmailSubject;
	String strNewEmailTo;
	String strNewEmailFrom;
	String strNewEmailBody;

	String strEmailSubject;
	String strEmailTo;
	String strEmailBody;

	String strTextFrom;
	String strTextContent;
	String strUserName;
	String strPassword;
	String strNewPassword;

	String strHost;
	String strEmailFrom;
	String strHostPassword;
	String strAttachmentFileSource;
	String strAttachmentFileName;

	String strRecruitmentPosition;
	String strRecruitmentNotificationCOde;
	String strRecruitmentLevel;
	String strRecruitmentDesignation;
	String strRecruitmentGrade;
	String strRecruitmentWLocation;
	String strRecruitmentProfile;
	String strRecruitmentSkill;

	String strEmpOrganization;
	String strEmpSbuName;
	String strEmpProbationEndDate;
	String strEmpConfirmationDate;

	String strOfferAcceptanceLastDate;

	String strCandiInterviewDateTime;
	String strCandiUsername;
	String strCandiPassword;

	String strReviewName;
	String strReviewStartdate;
	String strReviewEnddate;
	String strRevieweeName;
	String strRoleType;
	String strFinalizerName;
	String strAttributeName;
	String strGoalName;
	String strKRAName;
	String strTargetName;
	String strGoalAssignerName;
	String strTargetValue;

	String strCandiFname;
	String strCandiLname;
	String strCandiEmailID;
	String strCandiContactNo;

	String strLearningPlanName;
	String strLearningPlanStartdate;
	String strLearningPlanEnddate;
	String strLearnersName;
	String strTrainingName;
	String strAssessmentName;
	String strCourseName;
	String strTrainerName;

	String strSalaryStructure;
	String strAnnualBonusStructure;
	String strActivityName;

	String strProjectName;
	String strCustFName;
	String strCustLName;
	String strProjectOwnerName;
	String strProjectDescription;
	String strOrgName;
	String strClientName;
	String strTaskName;

	String strResourceFName;
	String strResourceLName;
	String strTeamLeader;
	String strDocumentName;
	String strDoneBy;
	String strFromDate;
	String strToDate;
	String strProjectFreqName;
	String strInvoiceNo;

	String strCategoryName;
	String strCustomerRegisterLink;

	String strOrganisationAddress;
	String strLegalEntityName;
	String strLegalEntityAddress;
	String strDepartmentName;
	String strSbuName;
	String strReportEmpDesignation;
	String strSkills;
	String strEffectiveDate;
	String strPromotionGrade;
	String strPromotionLevel;
	String strPromotionDesignation;
	String strPromotionDate;
	// String strIncrementAmount;
	String strIncrementPercent;
	String strTerminateDate;
	String strLastDateAtOffice;
	// String strPayStructure;
	String strPayrollAmount;
	String strPayrollAmountWords;
	String strPayYear;
	String strPayMonth;
	String strAllowanceAmount;
	String strAllowanceAmountWords;
	String strBanks;
	String strBankCode;
	String strCandiAddress;
	String strPrevMonthCTC;
	String strPrevAnnualCTC;
	String strIncrementAmtMonth;
	String strIncrementAmtAnnual;
	String strEmpAnnualCTC;

	String strLeaveCancelReason;
	String strManagerName;
	String strLeaveTypeName;

	String strProfileImage;

	String strHrName;
	String strBloodGroup;
	String strstate;
	String strAge;
	String StrFatherName;

	String strCloseDate;

	String strResignationDate;
	String strResignationReason;
	String strManagerComment;
	String strRecipientName;
	String strNoOfYearsWorking;

	String strEmpName;
	String strEvent;
	String strEventDesc;
	String strAddedBy;
	String strEventDate;
	String strEventTime;
	String strLocation;
	String strDepartment;

	String strAnnouncement;
	String strAnnDate;
	String strAnnouncementDesc;
	
	String strPublishDate;	//added by parvez date: 15-02-2023

	String strAttendanceApproveEmpCount;
	String strAttendancePendingEmpCount;
	String strSalaryApproveEmpCount;
	String strSalaryPendingEmpCount;
	String strPaycycleMonthAndYear;

	String strJobTitle;

	public String getStrPaycycleMonthAndYear() {
		return strPaycycleMonthAndYear;
	}

	public void setStrPaycycleMonthAndYear(String strPaycycleMonthAndYear) {
		this.strPaycycleMonthAndYear = strPaycycleMonthAndYear;
	}

	public String getStrAttendanceApproveEmpCount() {
		return strAttendanceApproveEmpCount;
	}

	public void setStrAttendanceApproveEmpCount(String strAttendanceApproveEmpCount) {
		this.strAttendanceApproveEmpCount = strAttendanceApproveEmpCount;
	}

	public String getStrAttendancePendingEmpCount() {
		return strAttendancePendingEmpCount;
	}

	public void setStrAttendancePendingEmpCount(String strAttendancePendingEmpCount) {
		this.strAttendancePendingEmpCount = strAttendancePendingEmpCount;
	}

	public String getStrSalaryApproveEmpCount() {
		return strSalaryApproveEmpCount;
	}

	public void setStrSalaryApproveEmpCount(String strSalaryApproveEmpCount) {
		this.strSalaryApproveEmpCount = strSalaryApproveEmpCount;
	}

	public String getStrSalaryPendingEmpCount() {
		return strSalaryPendingEmpCount;
	}

	public void setStrSalaryPendingEmpCount(String strSalaryPendingEmpCount) {
		this.strSalaryPendingEmpCount = strSalaryPendingEmpCount;
	}

	public String getStrAnnouncementDesc() {
		return strAnnouncementDesc;
	}

	public void setStrAnnouncementDesc(String strAnnouncementDesc) {
		this.strAnnouncementDesc = strAnnouncementDesc;
	}

	public String getStrAnnouncement() {
		return strAnnouncement;
	}

	public void setStrAnnouncement(String strAnnouncement) {
		this.strAnnouncement = strAnnouncement;
	}

	public String getStrAnnDate() {
		return strAnnDate;
	}

	public void setStrAnnDate(String strAnnDate) {
		this.strAnnDate = strAnnDate;
	}

	/*
	 * public String geteventEmpLName() { return eventEmpLName; }
	 * 
	 * 
	 * public void seteventEmpLName(String eventEmpLName) { this.eventEmpLName =
	 * eventEmpLName; }
	 */

	public String getStrEvent() {
		return strEvent;
	}

	public void setStrEvent(String strEvent) {
		this.strEvent = strEvent;
	}

	public String getStrAddedBy() {
		return strAddedBy;
	}

	public void setStrAddedBy(String strAddedBy) {
		this.strAddedBy = strAddedBy;
	}

	public String getStrEventDate() {
		return strEventDate;
	}

	public void setStrEventDate(String strEventDate) {
		this.strEventDate = strEventDate;
	}

	public String getStrEventTime() {
		return strEventTime;
	}

	public void setStrEventTime(String strEventTime) {
		this.strEventTime = strEventTime;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public String getStrProfileImage() {
		return strProfileImage;
	}

	public void setStrProfileImage(String strProfileImage) {
		this.strProfileImage = strProfileImage;
	}

	public String getStrHrName() {
		return strHrName;
	}

	public void setStrHrName(String strHrName) {
		this.strHrName = strHrName;
	}

	public String getStrBloodGroup() {
		return strBloodGroup;
	}

	public void setStrBloodGroup(String strBloodGroup) {
		this.strBloodGroup = strBloodGroup;
	}

	public String getStrstate() {
		return strstate;
	}

	public void setStrstate(String strstate) {
		this.strstate = strstate;
	}

	public String getStrAge() {
		return strAge;
	}

	public void setStrAge(String strAge) {
		this.strAge = strAge;
	}

	public String getStrFatherName() {
		return StrFatherName;
	}

	public void setStrFatherName(String strFatherName) {
		StrFatherName = strFatherName;
	}

	public String getStrLeaveCancelReason() {
		return strLeaveCancelReason;
	}

	public void setStrLeaveCancelReason(String strLeaveCancelReason) {
		this.strLeaveCancelReason = strLeaveCancelReason;
	}

	public String getStrManagerName() {
		return strManagerName;
	}

	public void setStrManagerName(String strManagerName) {
		this.strManagerName = strManagerName;
	}

	public String getStrLeaveTypeName() {
		return strLeaveTypeName;
	}

	public void setStrLeaveTypeName(String strLeaveTypeName) {
		this.strLeaveTypeName = strLeaveTypeName;
	}

	public String getStrEmpAnnualCTC() {
		return strEmpAnnualCTC;
	}

	public void setStrEmpAnnualCTC(String strEmpAnnualCTC) {
		this.strEmpAnnualCTC = strEmpAnnualCTC;
	}

	public String getStrPrevMonthCTC() {
		return strPrevMonthCTC;
	}

	public void setStrPrevMonthCTC(String strPrevMonthCTC) {
		this.strPrevMonthCTC = strPrevMonthCTC;
	}

	public String getStrPrevAnnualCTC() {
		return strPrevAnnualCTC;
	}

	public void setStrPrevAnnualCTC(String strPrevAnnualCTC) {
		this.strPrevAnnualCTC = strPrevAnnualCTC;
	}

	public String getStrInvoiceNo() {
		return strInvoiceNo;
	}

	public void setStrInvoiceNo(String strInvoiceNo) {
		this.strInvoiceNo = strInvoiceNo;
	}

	public String getStrClientName() {
		return strClientName;
	}

	public void setStrClientName(String strClientName) {
		this.strClientName = strClientName;
	}

	public String getStrTaskName() {
		return strTaskName;
	}

	public void setStrTaskName(String strTaskName) {
		this.strTaskName = strTaskName;
	}

	public String getStrResourceFName() {
		return strResourceFName;
	}

	public void setStrResourceFName(String strResourceFName) {
		this.strResourceFName = strResourceFName;
	}

	public String getStrResourceLName() {
		return strResourceLName;
	}

	public void setStrResourceLName(String strResourceLName) {
		this.strResourceLName = strResourceLName;
	}

	public String getStrTeamLeader() {
		return strTeamLeader;
	}

	public void setStrTeamLeader(String strTeamLeader) {
		this.strTeamLeader = strTeamLeader;
	}

	public String getStrDocumentName() {
		return strDocumentName;
	}

	public void setStrDocumentName(String strDocumentName) {
		this.strDocumentName = strDocumentName;
	}

	public String getStrDoneBy() {
		return strDoneBy;
	}

	public void setStrDoneBy(String strDoneBy) {
		this.strDoneBy = strDoneBy;
	}

	public String getStrFromDate() {
		return strFromDate;
	}

	public void setStrFromDate(String strFromDate) {
		this.strFromDate = strFromDate;
	}

	public String getStrToDate() {
		return strToDate;
	}

	public void setStrToDate(String strToDate) {
		this.strToDate = strToDate;
	}

	public String getStrProjectFreqName() {
		return strProjectFreqName;
	}

	public void setStrProjectFreqName(String strProjectFreqName) {
		this.strProjectFreqName = strProjectFreqName;
	}

	public String getStrOrgName() {
		return strOrgName;
	}

	public void setStrOrgName(String strOrgName) {
		this.strOrgName = strOrgName;
	}

	public String getStrProjectName() {
		return strProjectName;
	}

	public void setStrProjectName(String strProjectName) {
		this.strProjectName = strProjectName;
	}

	public String getStrCustFName() {
		return strCustFName;
	}

	public void setStrCustFName(String strCustFName) {
		this.strCustFName = strCustFName;
	}

	public String getStrCustLName() {
		return strCustLName;
	}

	public void setStrCustLName(String strCustLName) {
		this.strCustLName = strCustLName;
	}

	public String getStrProjectOwnerName() {
		return strProjectOwnerName;
	}

	public void setStrProjectOwnerName(String strProjectOwnerName) {
		this.strProjectOwnerName = strProjectOwnerName;
	}

	public String getStrProjectDescription() {
		return strProjectDescription;
	}

	public void setStrProjectDescription(String strProjectDescription) {
		this.strProjectDescription = strProjectDescription;
	}

	public String getStrActivityName() {
		return strActivityName;
	}

	public void setStrActivityName(String strActivityName) {
		this.strActivityName = strActivityName;
	}

	public String getStrSalaryStructure() {
		return strSalaryStructure;
	}

	public void setStrSalaryStructure(String strSalaryStructure) {
		this.strSalaryStructure = strSalaryStructure;
	}

	public String getStrTargetValue() {
		return strTargetValue;
	}

	public void setStrTargetValue(String strTargetValue) {
		this.strTargetValue = strTargetValue;
	}

	public String getStrGoalName() {
		return strGoalName;
	}

	public void setStrGoalName(String strGoalName) {
		this.strGoalName = strGoalName;
	}

	public String getStrKRAName() {
		return strKRAName;
	}

	public void setStrKRAName(String strKRAName) {
		this.strKRAName = strKRAName;
	}

	public String getStrTargetName() {
		return strTargetName;
	}

	public void setStrTargetName(String strTargetName) {
		this.strTargetName = strTargetName;
	}

	public String getStrGoalAssignerName() {
		return strGoalAssignerName;
	}

	public void setStrGoalAssignerName(String strGoalAssignerName) {
		this.strGoalAssignerName = strGoalAssignerName;
	}

	public String getStrFinalizerName() {
		return strFinalizerName;
	}

	public void setStrFinalizerName(String strFinalizerName) {
		this.strFinalizerName = strFinalizerName;
	}

	public String getStrTrainerName() {
		return strTrainerName;
	}

	public void setStrTrainerName(String strTrainerName) {
		this.strTrainerName = strTrainerName;
	}

	public String getStrTrainingName() {
		return strTrainingName;
	}

	public void setStrTrainingName(String strTrainingName) {
		this.strTrainingName = strTrainingName;
	}

	public String getStrAssessmentName() {
		return strAssessmentName;
	}

	public void setStrAssessmentName(String strAssessmentName) {
		this.strAssessmentName = strAssessmentName;
	}

	public String getStrCourseName() {
		return strCourseName;
	}

	public void setStrCourseName(String strCourseName) {
		this.strCourseName = strCourseName;
	}

	public String getStrCandiUsername() {
		return strCandiUsername;
	}

	public void setStrCandiUsername(String strCandiUsername) {
		this.strCandiUsername = strCandiUsername;
	}

	public String getStrCandiPassword() {
		return strCandiPassword;
	}

	public void setStrCandiPassword(String strCandiPassword) {
		this.strCandiPassword = strCandiPassword;
	}

	public String getStrCandiInterviewDateTime() {
		return strCandiInterviewDateTime;
	}

	public void setStrCandiInterviewDateTime(String strCandiInterviewDateTime) {
		this.strCandiInterviewDateTime = strCandiInterviewDateTime;
	}

	public String getStrEmpFname() {
		return strEmpFname;
	}

	public void setStrEmpFname(String strEmpFname) {
		this.strEmpFname = strEmpFname;
	}

	public String getStrEmpLname() {
		return strEmpLname;
	}

	public void setStrEmpLname(String strEmpLname) {
		this.strEmpLname = strEmpLname;
	}

	public String getStrEmpEmail() {
		return strEmpEmail;
	}

	public void setStrEmpEmail(String strEmpEmail) {
		this.strEmpEmail = strEmpEmail;
	}

	private String getStrEmpMobileNo() {
		return strEmpMobileNo;
	}

	public void setStrEmpMobileNo(String strEmpMobileNo) {
		this.strEmpMobileNo = strEmpMobileNo;
	}

	public String getStrEmailSubject() {
		return strEmailSubject;
	}

	public void setStrEmailSubject(String strEmailSubject) {
		this.strEmailSubject = strEmailSubject;
	}

	public String getStrEmailTo() {
		return strEmailTo;
	}

	public void setStrEmailTo(String strEmailTo) {
		this.strEmailTo = strEmailTo;
	}

	private String getStrEmailBody() {
		return strEmailBody;
	}

	public void setStrEmailBody(String strEmailBody) {
		this.strEmailBody = strEmailBody;
	}

	private String getStrTextFrom() {
		return strTextFrom;
	}

	private void setStrTextFrom(String strTextFrom) {
		this.strTextFrom = strTextFrom;
	}

	private String getStrTextContent() {
		return strTextContent;
	}

	private void setStrTextContent(String strTextContent) {
		this.strTextContent = strTextContent;
	}

	private String getStrUserName() {
		return strUserName;
	}

	public void setStrUserName(String strUserName) {
		this.strUserName = strUserName;
	}

	public String getStrPassword() {
		return strPassword;
	}

	public void setStrPassword(String strPassword) {
		this.strPassword = strPassword;
	}

	private String getStrEmpCode() {
		return strEmpCode;
	}

	public void setStrEmpCode(String strEmpCode) {
		this.strEmpCode = strEmpCode;
	}

	private String getStrHost() {
		return strHost;
	}

	private void setStrHost(String strHost) {
		this.strHost = strHost;
	}

	private String getStrEmailFrom() {
		return strEmailFrom;
	}

	public void setStrEmailFrom(String strEmailFrom) {
		this.strEmailFrom = strEmailFrom;
	}

	public String getStrAttachmentFileName() {
		return strAttachmentFileName;
	}

	public void setStrAttachmentFileName(String strAttachmentFileName) {
		this.strAttachmentFileName = strAttachmentFileName;
	}

	public String getStrEmpFullNamename() {
		return strEmpFullNamename;
	}

	public void setStrEmpFullNamename(String strEmpFullNamename) {
		this.strEmpFullNamename = strEmpFullNamename;
	}

	public int getnNotificationCode() {
		return nNotificationCode;
	}

	public void setnNotificationCode(int nNotificationCode) {
		this.nNotificationCode = nNotificationCode;
	}

	public String getStrEmpLeaveFrom() {
		return strEmpLeaveFrom;
	}

	public void setStrEmpLeaveFrom(String strEmpLeaveFrom) {
		this.strEmpLeaveFrom = strEmpLeaveFrom;
	}

	public String getStrEmpLeaveTo() {
		return strEmpLeaveTo;
	}

	public void setStrEmpLeaveTo(String strEmpLeaveTo) {
		this.strEmpLeaveTo = strEmpLeaveTo;
	}

	public String getStrEmpLeaveNoOfDays() {
		return strEmpLeaveNoOfDays;
	}

	public void setStrEmpLeaveNoOfDays(String strEmpLeaveNoOfDays) {
		this.strEmpLeaveNoOfDays = strEmpLeaveNoOfDays;
	}

	public String getStrEmpLeaveReason() {
		return strEmpLeaveReason;
	}

	public void setStrEmpLeaveReason(String strEmpLeaveReason) {
		this.strEmpLeaveReason = strEmpLeaveReason;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
		if (strEmpId != null && strEmpId.equalsIgnoreCase(EVERYONE)) {
			setStrEmailTo(EVERYONE);
		} else {
			setEmpDetails();
		}

	}

	// public void setStrEmpId(String strEmpId,Connection con) {
	// this.strEmpId = strEmpId;
	// if(strEmpId!=null && strEmpId.equalsIgnoreCase(EVERYONE)){
	// setStrEmailTo(EVERYONE);
	// }else if(con !=null ){
	// setEmpDetails(con);
	// }
	// }

	public String getStrSupervisorName() {
		return strSupervisorName;
	}

	public void setStrSupervisorName(String strSupervisorName) {
		this.strSupervisorName = strSupervisorName;
	}

	public String getStrSupervisorEmail() {
		return strSupervisorEmail;
	}

	public void setStrSupervisorEmail(String strSupervisorEmail) {
		this.strSupervisorEmail = strSupervisorEmail;
	}

	public String getStrSupervisorContactNo() {
		return strSupervisorContactNo;
	}

	public void setStrSupervisorContactNo(String strSupervisorContactNo) {
		this.strSupervisorContactNo = strSupervisorContactNo;
	}

	public boolean isSupervisor() {
		return isSupervisor;
	}

	public void setSupervisor(boolean isSupervisor) {
		this.isSupervisor = isSupervisor;
	}

	public String getStrHostAddress() {
		return strHostAddress;
	}

	public void setStrHostAddress(String strHostAddress) {
		this.strHostAddress = strHostAddress;
	}

	public String getStrContextPath() {
		return strContextPath;
	}

	public void setStrContextPath(String strContextPath) {
		this.strContextPath = strContextPath;
	}

	public String getStrManagerLeaveReason() {
		return strManagerLeaveReason;
	}

	public void setStrManagerLeaveReason(String strManagerLeaveReason) {
		this.strManagerLeaveReason = strManagerLeaveReason;
	}

	public String getStrApprvedDenied() {
		return strApprvedDenied;
	}

	public void setStrApprvedDenied(String strApprvedDenied) {
		this.strApprvedDenied = strApprvedDenied;
	}

	public String getStrEmpReimbursementFrom() {
		return strEmpReimbursementFrom;
	}

	public void setStrEmpReimbursementFrom(String strEmpReimbursementFrom) {
		this.strEmpReimbursementFrom = strEmpReimbursementFrom;
	}

	public String getStrEmpReimbursementTo() {
		return strEmpReimbursementTo;
	}

	public void setStrEmpReimbursementTo(String strEmpReimbursementTo) {
		this.strEmpReimbursementTo = strEmpReimbursementTo;
	}

	public String getStrEmpReimbursementPurpose() {
		return strEmpReimbursementPurpose;
	}

	public void setStrEmpReimbursementPurpose(String strEmpReimbursementPurpose) {
		this.strEmpReimbursementPurpose = strEmpReimbursementPurpose;
	}

	public String getStrAttachmentFileSource() {
		return strAttachmentFileSource;
	}

	public void setStrAttachmentFileSource(String strAttachmentFileSource) {
		this.strAttachmentFileSource = strAttachmentFileSource;
	}

	public String getStrEmpReimbursementAmount() {
		return strEmpReimbursementAmount;
	}

	public void setStrEmpReimbursementAmount(String strEmpReimbursementAmount) {
		this.strEmpReimbursementAmount = strEmpReimbursementAmount;
	}

	public String getStrEmpReimbursementType() {
		return strEmpReimbursementType;
	}

	public void setStrEmpReimbursementType(String strEmpReimbursementType) {
		this.strEmpReimbursementType = strEmpReimbursementType;
	}

	public String getStrEmpReqPurpose() {
		return strEmpReqPurpose;
	}

	public void setStrEmpReqPurpose(String strEmpReqPurpose) {
		this.strEmpReqPurpose = strEmpReqPurpose;
	}

	public String getStrEmpReqMode() {
		return strEmpReqMode;
	}

	public void setStrEmpReqMode(String strEmpReqMode) {
		this.strEmpReqMode = strEmpReqMode;
	}

	public String getStrEmpReqType() {
		return strEmpReqType;
	}

	public void setStrEmpReqType(String strEmpReqType) {
		this.strEmpReqType = strEmpReqType;
	}

	public String getStrEmpReqFrom() {
		return strEmpReqFrom;
	}

	public void setStrEmpReqFrom(String strEmpReqFrom) {
		this.strEmpReqFrom = strEmpReqFrom;
	}

	public String getStrEmpReqTo() {
		return strEmpReqTo;
	}

	public void setStrEmpReqTo(String strEmpReqTo) {
		this.strEmpReqTo = strEmpReqTo;
	}

	public String getStrAnnouncementHeading() {
		return strAnnouncementHeading;
	}

	public void setStrAnnouncementHeading(String strAnnouncementHeading) {
		this.strAnnouncementHeading = strAnnouncementHeading;
	}

	public String getStrAnnouncementBody() {
		return strAnnouncementBody;
	}

	public void setStrAnnouncementBody(String strAnnouncementBody) {
		this.strAnnouncementBody = strAnnouncementBody;
	}

	public String getStrNewEmailSubject() {
		return strNewEmailSubject;
	}

	public void setStrNewEmailSubject(String strNewEmailSubject) {
		this.strNewEmailSubject = strNewEmailSubject;
	}

	public String getStrNewEmailTo() {
		return strNewEmailTo;
	}

	public void setStrNewEmailTo(String strNewEmailTo) {
		this.strNewEmailTo = strNewEmailTo;
	}

	public String getStrNewEmailFrom() {
		return strNewEmailFrom;
	}

	public void setStrNewEmailFrom(String strNewEmailFrom) {
		this.strNewEmailFrom = strNewEmailFrom;
	}

	public String getStrNewEmailBody() {
		return strNewEmailBody;
	}

	public void setStrNewEmailBody(String strNewEmailBody) {
		this.strNewEmailBody = strNewEmailBody;
	}

	public String getStrSalaryAmount() {
		return strSalaryAmount;
	}

	public void setStrSalaryAmount(String strSalaryAmount) {
		this.strSalaryAmount = strSalaryAmount;
	}

	public String getStrTextSalaryAmount() {
		return strTextSalaryAmount;
	}

	public void setStrTextSalaryAmount(String strTextSalaryAmount) {
		this.strTextSalaryAmount = strTextSalaryAmount;
	}

	public String getStrPaycycle() {
		return strPaycycle;
	}

	public void setStrPaycycle(String strPaycycle) {
		this.strPaycycle = strPaycycle;
	}

	public String getStrAccountNo() {
		return strAccountNo;
	}

	public void setStrAccountNo(String strAccountNo) {
		this.strAccountNo = strAccountNo;
	}

	public String getStrNewPassword() {
		return strNewPassword;
	}

	public void setStrNewPassword(String strNewPassword) {
		this.strNewPassword = strNewPassword;
	}

	public String getStrAddEmpLink() {
		return strAddEmpLink;
	}

	public void setStrAddEmpLink(String strAddEmpLink) {
		this.strAddEmpLink = strAddEmpLink;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	public String getStrHostPassword() {
		return strHostPassword;
	}

	public void setStrHostPassword(String strHostPassword) {
		this.strHostPassword = strHostPassword;
	}

	public String getStrEmpLevel() {
		return strEmpLevel;
	}

	public void setStrEmpLevel(String strEmpLevel) {
		this.strEmpLevel = strEmpLevel;
	}

	public String getStrEmpDesignation() {
		return strEmpDesignation;
	}

	public void setStrEmpDesignation(String strEmpDesignation) {
		this.strEmpDesignation = strEmpDesignation;
	}

	public String getStrEmpGrade() {
		return strEmpGrade;
	}

	public void setStrEmpGrade(String strEmpGrade) {
		this.strEmpGrade = strEmpGrade;
	}

	public String getStrEmpWLocation() {
		return strEmpWLocation;
	}

	public void setStrEmpWLocation(String strEmpWLocation) {
		this.strEmpWLocation = strEmpWLocation;
	}

	public String getStrJoiningDate() {
		return strJoiningDate;
	}

	public void setStrJoiningDate(String strJoiningDate) {
		this.strJoiningDate = strJoiningDate;
	}

	public String getStrEmpCTC() {
		return strEmpCTC;
	}

	public void setStrEmpCTC(String strEmpCTC) {
		this.strEmpCTC = strEmpCTC;
	}

	public String getStrEmpKRAs() {
		return strEmpKRAs;
	}

	public void setStrEmpKRAs(String strEmpKRAs) {
		this.strEmpKRAs = strEmpKRAs;
	}

	public String getStrRecruitmentPosition() {
		return strRecruitmentPosition;
	}

	public void setStrRecruitmentPosition(String strRecruitmentPosition) {
		this.strRecruitmentPosition = strRecruitmentPosition;
	}
	public String getStrRecruitmentLevel() {
		return strRecruitmentLevel;
	}
	public void setStrRecruitmentLevel(String strRecruitmentLevel) {
		this.strRecruitmentLevel = strRecruitmentLevel;
	}
	public String getStrRecruitmentDesignation() {
		return strRecruitmentDesignation;
	}
	public void setStrRecruitmentDesignation(String strRecruitmentDesignation) {
		this.strRecruitmentDesignation = strRecruitmentDesignation;
	}
	public String getStrRecruitmentGrade() {
		return strRecruitmentGrade;
	}
	public void setStrRecruitmentGrade(String strRecruitmentGrade) {
		this.strRecruitmentGrade = strRecruitmentGrade;
	}
	public String getStrRecruitmentWLocation() {
		return strRecruitmentWLocation;
	}
	public void setStrRecruitmentWLocation(String strRecruitmentWLocation) {
		this.strRecruitmentWLocation = strRecruitmentWLocation;
	}
	public String getStrRecruitmentProfile() {
		return strRecruitmentProfile;
	}
	public void setStrRecruitmentProfile(String strRecruitmentProfile) {
		this.strRecruitmentProfile = strRecruitmentProfile;
	}
	public String getStrRecruitmentSkill() {
		return strRecruitmentSkill;
	}
	public void setStrRecruitmentSkill(String strRecruitmentSkill) {
		this.strRecruitmentSkill = strRecruitmentSkill;
	}
	public String getStrEmpAddress() {
		return strEmpAddress;
	}
	public void setStrEmpAddress(String strEmpAddress) {
		this.strEmpAddress = strEmpAddress;
	}

	public String getStrEmpOrganization() {
		return strEmpOrganization;
	}

	public void setStrEmpOrganization(String strEmpOrganization) {
		this.strEmpOrganization = strEmpOrganization;
	}

	public String getStrEmpProbationEndDate() {
		return strEmpProbationEndDate;
	}

	public void setStrEmpProbationEndDate(String strEmpProbationEndDate) {
		this.strEmpProbationEndDate = strEmpProbationEndDate;
	}

	public String getStrEmpConfirmationDate() {
		return strEmpConfirmationDate;
	}

	public void setStrEmpConfirmationDate(String strEmpConfirmationDate) {
		this.strEmpConfirmationDate = strEmpConfirmationDate;
	}

	public String getStrReviewName() {
		return strReviewName;
	}

	public void setStrReviewName(String strReviewName) {
		this.strReviewName = strReviewName;
	}

	public String getStrReviewStartdate() {
		return strReviewStartdate;
	}

	public void setStrReviewStartdate(String strReviewStartdate) {
		this.strReviewStartdate = strReviewStartdate;
	}

	public String getStrReviewEnddate() {
		return strReviewEnddate;
	}

	public void setStrReviewEnddate(String strReviewEnddate) {
		this.strReviewEnddate = strReviewEnddate;
	}

	public String getStrRevieweeName() {
		return strRevieweeName;
	}

	public void setStrRevieweeName(String strRevieweeName) {
		this.strRevieweeName = strRevieweeName;
	}

	public String getStrRoleType() {
		return strRoleType;
	}

	public void setStrRoleType(String strRoleType) {
		this.strRoleType = strRoleType;
	}

	public String getStrAttributeName() {
		return strAttributeName;
	}

	public void setStrAttributeName(String strAttributeName) {
		this.strAttributeName = strAttributeName;
	}

	public String getStrCandiFname() {
		return strCandiFname;
	}

	public void setStrCandiFname(String strCandiFname) {
		this.strCandiFname = strCandiFname;
	}

	public String getStrCandiLname() {
		return strCandiLname;
	}

	public void setStrCandiLname(String strCandiLname) {
		this.strCandiLname = strCandiLname;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	String strDomain;
	public void setDomain(String strDomain) {
		this.strDomain = strDomain;
	}

	public String getStrSectionCode() {
		return strSectionCode;
	}

	public void setStrSectionCode(String strSectionCode) {
		this.strSectionCode = strSectionCode;
	}

	public String getStrInvestmentAmount() {
		return strInvestmentAmount;
	}

	public String getStrLearnersName() {
		return strLearnersName;
	}

	public void setStrLearnersName(String strLearnersName) {
		this.strLearnersName = strLearnersName;
	}

	public String getStrLearningPlanName() {
		return strLearningPlanName;
	}

	public void setStrLearningPlanName(String strLearningPlanName) {
		this.strLearningPlanName = strLearningPlanName;
	}

	public String getStrLearningPlanStartdate() {
		return strLearningPlanStartdate;
	}

	public void setStrLearningPlanStartdate(String strLearningPlanStartdate) {
		this.strLearningPlanStartdate = strLearningPlanStartdate;
	}

	public String getStrLearningPlanEnddate() {
		return strLearningPlanEnddate;
	}

	public void setStrLearningPlanEnddate(String strLearningPlanEnddate) {
		this.strLearningPlanEnddate = strLearningPlanEnddate;
	}

	public void setStrInvestmentAmount(String strInvestmentAmount) {
		this.strInvestmentAmount = strInvestmentAmount;
	}

	public String getStrMgrId() {
		return strMgrId;
	}

	public void setStrMgrId(String strMgrId) {
		this.strMgrId = strMgrId;
	}

	public String getStrHostPort() {
		return strHostPort;
	}

	public void setStrHostPort(String strHostPort) {
		this.strHostPort = strHostPort;
	}

	public String getStrAddPeopleLink() {
		return strAddPeopleLink;
	}

	public void setStrAddPeopleLink(String strAddPeopleLink) {
		this.strAddPeopleLink = strAddPeopleLink;
	}

	public String getStrOrgId() {
		return strOrgId;
	}

	public void setStrOrgId(String strOrgId) {
		this.strOrgId = strOrgId;
	}

	public String getStrOrgImage() {
		return strOrgImage;
	}

	public void setStrOrgImage(String strOrgImage) {
		this.strOrgImage = strOrgImage;
	}

	public boolean isEmailTemplate() {
		return isEmailTemplate;
	}

	public void setEmailTemplate(boolean isEmailTemplate) {
		this.isEmailTemplate = isEmailTemplate;
	}

	public String getStrCategoryName() {
		return strCategoryName;
	}

	public void setStrCategoryName(String strCategoryName) {
		this.strCategoryName = strCategoryName;
	}

	public String getStrCustomerRegisterLink() {
		return strCustomerRegisterLink;
	}

	public void setStrCustomerRegisterLink(String strCustomerRegisterLink) {
		this.strCustomerRegisterLink = strCustomerRegisterLink;
	}

	public String getStrRecruitmentNotificationCOde() {
		return strRecruitmentNotificationCOde;
	}

	public void setStrRecruitmentNotificationCOde(String strRecruitmentNotificationCOde) {
		this.strRecruitmentNotificationCOde = strRecruitmentNotificationCOde;
	}

	public String getStrOrganisationAddress() {
		return strOrganisationAddress;
	}

	public void setStrOrganisationAddress(String strOrganisationAddress) {
		this.strOrganisationAddress = strOrganisationAddress;
	}

	public String getStrLegalEntityName() {
		return strLegalEntityName;
	}

	public void setStrLegalEntityName(String strLegalEntityName) {
		this.strLegalEntityName = strLegalEntityName;
	}

	public String getStrLegalEntityAddress() {
		return strLegalEntityAddress;
	}

	public void setStrLegalEntityAddress(String strLegalEntityAddress) {
		this.strLegalEntityAddress = strLegalEntityAddress;
	}

	public String getStrDepartmentName() {
		return strDepartmentName;
	}

	public void setStrDepartmentName(String strDepartmentName) {
		this.strDepartmentName = strDepartmentName;
	}

	public String getStrSbuName() {
		return strSbuName;
	}

	public void setStrSbuName(String strSbuName) {
		this.strSbuName = strSbuName;
	}

	public String getStrReportEmpDesignation() {
		return strReportEmpDesignation;
	}

	public void setStrReportEmpDesignation(String strReportEmpDesignation) {
		this.strReportEmpDesignation = strReportEmpDesignation;
	}

	public String getStrSkills() {
		return strSkills;
	}

	public void setStrSkills(String strSkills) {
		this.strSkills = strSkills;
	}

	public String getStrEffectiveDate() {
		return strEffectiveDate;
	}

	public void setStrEffectiveDate(String strEffectiveDate) {
		this.strEffectiveDate = strEffectiveDate;
	}

	public String getStrPromotionGrade() {
		return strPromotionGrade;
	}

	public void setStrPromotionGrade(String strPromotionGrade) {
		this.strPromotionGrade = strPromotionGrade;
	}

	public String getStrPromotionLevel() {
		return strPromotionLevel;
	}

	public void setStrPromotionLevel(String strPromotionLevel) {
		this.strPromotionLevel = strPromotionLevel;
	}

	public String getStrPromotionDesignation() {
		return strPromotionDesignation;
	}

	public void setStrPromotionDesignation(String strPromotionDesignation) {
		this.strPromotionDesignation = strPromotionDesignation;
	}

	public String getStrPromotionDate() {
		return strPromotionDate;
	}

	public void setStrPromotionDate(String strPromotionDate) {
		this.strPromotionDate = strPromotionDate;
	}

	// public String getStrIncrementAmount() {
	// return strIncrementAmount;
	// }
	//
	//
	// public void setStrIncrementAmount(String strIncrementAmount) {
	// this.strIncrementAmount = strIncrementAmount;
	// }

	public String getStrCloseDate() {
		return strCloseDate;
	}

	public void setStrCloseDate(String strCloseDate) {
		this.strCloseDate = strCloseDate;
	}

	public String getStrIncrementPercent() {
		return strIncrementPercent;
	}

	public void setStrIncrementPercent(String strIncrementPercent) {
		this.strIncrementPercent = strIncrementPercent;
	}

	public String getStrTerminateDate() {
		return strTerminateDate;
	}

	public void setStrTerminateDate(String strTerminateDate) {
		this.strTerminateDate = strTerminateDate;
	}

	public String getStrLastDateAtOffice() {
		return strLastDateAtOffice;
	}

	public void setStrLastDateAtOffice(String strLastDateAtOffice) {
		this.strLastDateAtOffice = strLastDateAtOffice;
	}

	// public String getStrPayStructure() {
	// return strPayStructure;
	// }
	//
	// public void setStrPayStructure(String strPayStructure) {
	// this.strPayStructure = strPayStructure;
	// }

	public String getStrPayrollAmount() {
		return strPayrollAmount;
	}

	public void setStrPayrollAmount(String strPayrollAmount) {
		this.strPayrollAmount = strPayrollAmount;
	}

	public String getStrPayrollAmountWords() {
		return strPayrollAmountWords;
	}

	public void setStrPayrollAmountWords(String strPayrollAmountWords) {
		this.strPayrollAmountWords = strPayrollAmountWords;
	}

	public String getStrPayYear() {
		return strPayYear;
	}

	public void setStrPayYear(String strPayYear) {
		this.strPayYear = strPayYear;
	}

	public String getStrPayMonth() {
		return strPayMonth;
	}

	public void setStrPayMonth(String strPayMonth) {
		this.strPayMonth = strPayMonth;
	}

	public String getStrAllowanceAmount() {
		return strAllowanceAmount;
	}

	public void setStrAllowanceAmount(String strAllowanceAmount) {
		this.strAllowanceAmount = strAllowanceAmount;
	}

	public String getStrAllowanceAmountWords() {
		return strAllowanceAmountWords;
	}

	public void setStrAllowanceAmountWords(String strAllowanceAmountWords) {
		this.strAllowanceAmountWords = strAllowanceAmountWords;
	}

	public String getStrBanks() {
		return strBanks;
	}

	public void setStrBanks(String strBanks) {
		this.strBanks = strBanks;
	}

	public String getStrBankCode() {
		return strBankCode;
	}

	public void setStrBankCode(String strBankCode) {
		this.strBankCode = strBankCode;
	}

	public String getStrCandiAddress() {
		return strCandiAddress;
	}

	public void setStrCandiAddress(String strCandiAddress) {
		this.strCandiAddress = strCandiAddress;
	}

	public String getStrDomain() {
		return strDomain;
	}

	public void setStrDomain(String strDomain) {
		this.strDomain = strDomain;
	}

	public boolean getIsRequiredAuthentication() {
		return isRequiredAuthentication;
	}

	public void setIsRequiredAuthentication(boolean isRequiredAuthentication) {
		this.isRequiredAuthentication = isRequiredAuthentication;
	}

	public String getStrEmailAuthUsername() {
		return strEmailAuthUsername;
	}

	public void setStrEmailAuthUsername(String strEmailAuthUsername) {
		this.strEmailAuthUsername = strEmailAuthUsername;
	}

	public String getStrEmailAuthPassword() {
		return strEmailAuthPassword;
	}

	public void setStrEmailAuthPassword(String strEmailAuthPassword) {
		this.strEmailAuthPassword = strEmailAuthPassword;
	}

	public String getStrEmpReimbursementDate() {
		return strEmpReimbursementDate;
	}

	public void setStrEmpReimbursementDate(String strEmpReimbursementDate) {
		this.strEmpReimbursementDate = strEmpReimbursementDate;
	}

	public String getStrEmpReimbursementCurrency() {
		return strEmpReimbursementCurrency;
	}

	public void setStrEmpReimbursementCurrency(String strEmpReimbursementCurrency) {
		this.strEmpReimbursementCurrency = strEmpReimbursementCurrency;
	}

	public String getStrIncrementAmtMonth() {
		return strIncrementAmtMonth;
	}

	public void setStrIncrementAmtMonth(String strIncrementAmtMonth) {
		this.strIncrementAmtMonth = strIncrementAmtMonth;
	}

	public String getStrIncrementAmtAnnual() {
		return strIncrementAmtAnnual;
	}

	public void setStrIncrementAmtAnnual(String strIncrementAmtAnnual) {
		this.strIncrementAmtAnnual = strIncrementAmtAnnual;
	}

	public String getStrEmpSbuName() {
		return strEmpSbuName;
	}

	public void setStrEmpSbuName(String strEmpSbuName) {
		this.strEmpSbuName = strEmpSbuName;
	}

	public String getStrResignationDate() {
		return strResignationDate;
	}

	public void setStrResignationDate(String strResignationDate) {
		this.strResignationDate = strResignationDate;
	}

	public String getStrResignationReason() {
		return strResignationReason;
	}

	public void setStrResignationReason(String strResignationReason) {
		this.strResignationReason = strResignationReason;
	}

	public String getStrManagerComment() {
		return strManagerComment;
	}

	public void setStrManagerComment(String strManagerComment) {
		this.strManagerComment = strManagerComment;
	}

	public String getStrRecipientName() {
		return strRecipientName;
	}

	public void setStrRecipientName(String strRecipientName) {
		this.strRecipientName = strRecipientName;
	}

	public String getStrNoOfYearsWorking() {
		return strNoOfYearsWorking;
	}

	public void setStrNoOfYearsWorking(String strNoOfYearsWorking) {
		this.strNoOfYearsWorking = strNoOfYearsWorking;
	}

	public String getStrEmpEmailID() {
		return strEmpEmailID;
	}

	public void setStrEmpEmailID(String strEmpEmailID) {
		this.strEmpEmailID = strEmpEmailID;
	}

	public String getStrEmpContactNo() {
		return strEmpContactNo;
	}

	public void setStrEmpContactNo(String strEmpContactNo) {
		this.strEmpContactNo = strEmpContactNo;
	}

	public String getStrCandiEmailID() {
		return strCandiEmailID;
	}

	public void setStrCandiEmailID(String strCandiEmailID) {
		this.strCandiEmailID = strCandiEmailID;
	}

	public String getStrCandiContactNo() {
		return strCandiContactNo;
	}

	public void setStrCandiContactNo(String strCandiContactNo) {
		this.strCandiContactNo = strCandiContactNo;
	}

	public String getStrOfferAcceptanceLastDate() {
		return strOfferAcceptanceLastDate;
	}

	public void setStrOfferAcceptanceLastDate(String strOfferAcceptanceLastDate) {
		this.strOfferAcceptanceLastDate = strOfferAcceptanceLastDate;
	}

	public String getStrEmpPancardNo() {
		return strEmpPancardNo;
	}

	public void setStrEmpPancardNo(String strEmpPancardNo) {
		this.strEmpPancardNo = strEmpPancardNo;
	}

	public String getStrEmpDateOfBirth() {
		return strEmpDateOfBirth;
	}

	public void setStrEmpDateOfBirth(String strEmpDateOfBirth) {
		this.strEmpDateOfBirth = strEmpDateOfBirth;
	}

	public String getStrEmpEmergencyContactDetails() {
		return strEmpEmergencyContactDetails;
	}

	public void setStrEmpEmergencyContactDetails(String strEmpEmergencyContactDetails) {
		this.strEmpEmergencyContactDetails = strEmpEmergencyContactDetails;
	}

	public String getStrEmpMname() {
		return strEmpMname;
	}

	public void setStrEmpMname(String strEmpMname) {
		this.strEmpMname = strEmpMname;
	}

	public String getStrAnnualBonusStructure() {
		return strAnnualBonusStructure;
	}

	public void setStrAnnualBonusStructure(String strAnnualBonusStructure) {
		this.strAnnualBonusStructure = strAnnualBonusStructure;
	}

	public String getStrSalutation() {
		return strSalutation;
	}

	public void setStrSalutation(String strSalutation) {
		this.strSalutation = strSalutation;
	}
	public String getStrEmpName() {
		return strEmpName;
	}

	public void setStrEmpName(String strEmpName) {
		this.strEmpName = strEmpName;
	}

	public String getStrEventDesc() {
		return strEventDesc;
	}

	public void setStrEventDesc(String strEventDesc) {
		this.strEventDesc = strEventDesc;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getStrJobTitle() {
		return strJobTitle;
	}

	public void setStrJobTitle(String strJobTitle) {
		this.strJobTitle = strJobTitle;
	}

//===start parvez date: 15-02-2023===
	public String getStrPublishDate() {
		return strPublishDate;
	}

	public void setStrPublishDate(String strPublishDate) {
		this.strPublishDate = strPublishDate;
	}
//===end parvez date: 15-02-2023===

//===start parvez date: 18-03-2023===
	public String getStrLeaveEmpBackup() {
		return strLeaveEmpBackup;
	}

	public void setStrLeaveEmpBackup(String strLeaveEmpBackup) {
		this.strLeaveEmpBackup = strLeaveEmpBackup;
	}
//===end parvez date: 18-03-2023===	
}