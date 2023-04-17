package com.konnect.jpms.training;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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

import org.apache.struts2.views.jsp.SetTag;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.konnect.jpms.util.Way2Sms;

public class NotificationContent extends Thread implements IStatements {
	 
	
	public NotificationContent(int N_NEW_EMPLOYEE){
		setnNotificationCode(N_NEW_EMPLOYEE);		
	}
	CommonFunctions CF;
	public NotificationContent(int N_NEW_EMPLOYEE, CommonFunctions CF){
		setnNotificationCode(N_NEW_EMPLOYEE);
		this.CF = CF;
	}
	
	
	HttpServletRequest request;
	
	public void sendNotifications(){
		start(); 
	} 
	
	
	boolean isTextNotifications = false;
	boolean isEmailNotifications = false;
	
	public void run(){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectSettings);
			rst = pst.executeQuery();
			
			while(rst.next()){
				
				if(rst.getString("options").equalsIgnoreCase(O_EMAIL_NOTIFICATIONS)){
					isEmailNotifications = uF.parseToBoolean(rst.getString("value"));
				}else if(rst.getString("options").equalsIgnoreCase(O_TEXT_NOTIFICATIONS)){
					isTextNotifications = uF.parseToBoolean(rst.getString("value"));
				}else if(rst.getString("options").equalsIgnoreCase(O_EMAIL_HOST)){
					setStrHost(rst.getString("value"));
				}else if(rst.getString("options").equalsIgnoreCase(O_EMAIL_FROM)){
					setStrEmailFrom(rst.getString("value"));
				}else if(rst.getString("options").equalsIgnoreCase(O_TEXT_FROM)){
					setStrTextFrom(rst.getString("value"));
				}else if(rst.getString("options").equalsIgnoreCase(O_HOST_PASSWORD)){
					setStrHostPassword(rst.getString("value"));
				}
				
			}
			rst.close();
			pst.close();
			
			
			
			if(isEmailNotifications || isTextNotifications){
				getNotificationsMessage();
			}
			
			if(isEmailNotifications){
				sendEmailNotifications();
			}
			
			
			if(isTextNotifications){
				/*Way2Sms sms = new Way2Sms();
				sms.sendSMS(getStrEmpMobileNo(), getStrTextContent());*/
			}
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getNotificationsMessage(){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectNotifications);
			pst.setInt(1, getnNotificationCode());
			rst = pst.executeQuery();
			
			while(rst.next()){
				
				setStrEmailBody(rst.getString("email_notification"));
				setStrEmailSubject(rst.getString("email_subject"));
				setStrTextContent(rst.getString("text_notification"));
				isEmailNotifications = uF.parseToBoolean(rst.getString("isemail"));
				isTextNotifications = uF.parseToBoolean(rst.getString("istext"));
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
		parseContent(getStrEmailBody(), getStrTextContent(), getStrEmailSubject());
	}
	
	public Map<String, String> parseContent(String strEmailBody, String strTextContent, String strSubject){
		
		
		if(strEmailBody!=null){
			
	        /// Training variables------------------

			if(getTraining_start_date()!=null){

				strEmailBody = strEmailBody.replace("[START_DATE]", getTraining_start_date());
				strSubject = strSubject.replace("[START_DATE]", getTraining_start_date());
				strTextContent = strTextContent.replace("[START_DATE]", getTraining_start_date());

			}
			if(getTraining_end_date()!=null){
			
				strEmailBody = strEmailBody.replace("[END_DATE]",getTraining_end_date());
				strSubject = strSubject.replace("[END_DATE]", getTraining_end_date());
				strTextContent = strTextContent.replace("[END_DATE]", getTraining_end_date());
				
				}
			if(getTraining_title()!=null){
				
				strEmailBody = strEmailBody.replace("[TRAINING_NAME]", getTraining_title());
				strSubject = strSubject.replace("[TRAINING_NAME]", getTraining_title());
				strTextContent = strTextContent.replace("[TRAINING_NAME]", getTraining_title());
				
			}
			if(getTraining_attribute()!=null){
				strEmailBody = strEmailBody.replace("[TRAINING_ATTRIBUTE]",getTraining_attribute());
				strSubject = strSubject.replace("[TRAINING_ATTRIBUTE]", getTraining_attribute());
				strTextContent = strTextContent.replace("[TRAINING_ATTRIBUTE]", getTraining_attribute());
			}
			if(getTraining_certificate_name()!=null){				
				strEmailBody = strEmailBody.replace("[CERTIFICATE_NAME]", getTraining_certificate_name());
				strSubject = strSubject.replace("[CERTIFICATE_NAME]", getTraining_certificate_name());
				strTextContent = strTextContent.replace("[CERTIFICATE_NAME]", getTraining_certificate_name());
				
			}
			if(getTraining_trainer_name()!=null){
				strEmailBody = strEmailBody.replace("[TRAINING_TRAINER]",getTraining_trainer_name());
				strSubject = strSubject.replace("[TRAINING_TRAINER]", getTraining_trainer_name());
				strTextContent = strTextContent.replace("[TRAINING_TRAINER]", getTraining_trainer_name());
			}			
		
			if(getTraining_location()!=null){
				strEmailBody = strEmailBody.replace("[TRAINING_LOCATION]",getTraining_location());
				strSubject = strSubject.replace("[TRAINING_LOCATION]", getTraining_location());
				strTextContent = strTextContent.replace("[TRAINING_LOCATION]", getTraining_location());
			}

			
			
			/// Recruitment variables ----------------------------
			
			if(getStrRecruitmentDesignation()!=null){
				strEmailBody = strEmailBody.replace(RECRUITMENT_DESIG, getStrRecruitmentDesignation());
				strSubject = strSubject.replace(RECRUITMENT_DESIG, getStrRecruitmentDesignation());
				strTextContent = strTextContent.replace(RECRUITMENT_DESIG, getStrRecruitmentDesignation());
			}
			if(getStrRecruitmentGrade()!=null){
				strEmailBody = strEmailBody.replace(RECRUITMENT_GRADE, getStrRecruitmentGrade());
				strSubject = strSubject.replace(RECRUITMENT_GRADE, getStrRecruitmentGrade());
				strTextContent = strTextContent.replace(RECRUITMENT_GRADE, getStrRecruitmentGrade());
			}
			if(getStrRecruitmentLevel()!=null){
				strEmailBody = strEmailBody.replace(RECRUITMENT_LEVEL, getStrRecruitmentLevel());
				strSubject = strSubject.replace(RECRUITMENT_LEVEL, getStrRecruitmentLevel());
				strTextContent = strTextContent.replace(RECRUITMENT_LEVEL, getStrRecruitmentLevel());
			}
			if(getStrRecruitmentPosition()!=null){
				strEmailBody = strEmailBody.replace(RECRUITMENT_POSITIONS, getStrRecruitmentPosition());
				strSubject = strSubject.replace(RECRUITMENT_POSITIONS, getStrRecruitmentPosition());
				strTextContent = strTextContent.replace(RECRUITMENT_POSITIONS, getStrRecruitmentPosition());
			}
			if(getStrRecruitmentProfile()!=null){
				strEmailBody = strEmailBody.replace(RECRUITMENT_PROFILE, getStrRecruitmentProfile());
				strSubject = strSubject.replace(RECRUITMENT_PROFILE, getStrRecruitmentProfile());
				strTextContent = strTextContent.replace(RECRUITMENT_PROFILE, getStrRecruitmentProfile());
			}
			if(getStrRecruitmentSkill()!=null){
				strEmailBody = strEmailBody.replace(RECRUITMENT_SKILL, getStrRecruitmentSkill());
				strSubject = strSubject.replace(RECRUITMENT_SKILL, getStrRecruitmentSkill());
				strTextContent = strTextContent.replace(RECRUITMENT_SKILL, getStrRecruitmentSkill());
			}
			if(getStrRecruitmentWLocation()!=null){
				strEmailBody = strEmailBody.replace(RECRUITMENT_WLOCATION, getStrRecruitmentWLocation());
				strSubject = strSubject.replace(RECRUITMENT_WLOCATION, getStrRecruitmentWLocation());
				strTextContent = strTextContent.replace(RECRUITMENT_WLOCATION, getStrRecruitmentWLocation());
			}
			
			
//			System.out.println("printing variables======="+getJoiningDate()+getCtcOffered()+getCandidateName());
			
			
			if(getJobDescription()!=null){
				strEmailBody = strEmailBody.replace("[JOB_DESC]", getJobDescription());
				strSubject = strSubject.replace("[JOB_DESC]", getJobDescription());
				strTextContent = strTextContent.replace("[JOB_DESC]", getJobDescription());
			}
			if(getInterviewSchedule()!=null){
				strEmailBody = strEmailBody.replace("[INTERVIEW_SCHEDULE]", getInterviewSchedule());
				strSubject = strSubject.replace("[INTERVIEW_DATE]", getInterviewSchedule());
				strTextContent = strTextContent.replace("[INTERVIEW_DATE]", getInterviewSchedule());
			}			

			if(getJoiningDate()!=null){
				strEmailBody = strEmailBody.replace("[JOINING_DATE]", getJoiningDate());
				strSubject = strSubject.replace("[JOINING_DATE]", getJoiningDate());
				strTextContent = strTextContent.replace("[JOINING_DATE]", getJoiningDate());
			}
			if(getReportingToManager()!=null){
				strEmailBody = strEmailBody.replace("[REPORTING_MANAGER]", getReportingToManager());
				strSubject = strSubject.replace("[REPORTING_MANAGER]", getReportingToManager());
				strTextContent = strTextContent.replace("[REPORTING_MANAGER]", getReportingToManager());
			}			
			if(getCtcOffered()!=null){
				strEmailBody = strEmailBody.replace("[CTC_OFFERED]", getCtcOffered());
				strSubject = strSubject.replace("[CTC_OFFERED]", getCtcOffered());
				strTextContent = strTextContent.replace("[CTC_OFFERED]", getCtcOffered());
			}
			if(getJobCode()!=null){
				strEmailBody = strEmailBody.replace("[JOB_CODE]", getJobCode());
				strSubject = strSubject.replace("[JOB_CODE]", getJobCode());
				strTextContent = strTextContent.replace("[JOB_CODE]", getJobCode());
			}

			if(getCandidateName()!=null){
				strEmailBody = strEmailBody.replace("[CANDIDATE_NAME]", getCandidateName());
				strSubject = strSubject.replace("[CANDIDATE_NAME]", getCandidateName());
				strTextContent = strTextContent.replace("[CANDIDATE_NAME]", getCandidateName());
			}

//                 System.out.println("printing body ======="+strEmailBody);
			
			strEmailBody = strEmailBody.replace("\n", "<br/>");
			
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
	public void sendEmailNotifications(){ 

//		if(CF!=null && CF.getStrEmailLocalHost()!=null && CF.getStrEmailLocalHost().indexOf("localhost")>0){
//			sendEmailNotificationsFromGmail(); 	
//		}else{
//			sendEmailNotificationsFromServer();
//		}
//		
		sendEmailNotificationsFromGmail(); 
	        
	}
	
	
	private void sendEmailNotificationsFromServer(){ 

        // Create properties, get Session
        Properties props = new Properties();

        // If using static Transport.send(),
        // need to specify which host to send it to
        props.put("mail.smtp.host", getStrHost());
        // To see what is going on behind the scene
        props.put("mail.debug", "true");
        
        Session session = Session.getInstance(props);

        Database db=new Database();
		db.setRequest(request);
        Connection con=null;
        con=db.makeConnection(con);
        try {
            // Instantiatee a message
            MimeMessage msg = new MimeMessage(session);
            Multipart multipart = new MimeMultipart();
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            MimeBodyPart attachmentPart = new MimeBodyPart();
            
            //Set message attributes
            msg.setFrom(new InternetAddress(getStrEmailFrom()));
            
            InternetAddress[] address = null;
            if(getStrEmailTo()!=null && !getStrEmailTo().equalsIgnoreCase(EVERYONE)){
            	address = new InternetAddress[1];
            	address[0] = new InternetAddress(getStrEmailTo());	
            }else if(getStrEmailTo()!=null && getStrEmailTo().equalsIgnoreCase(EVERYONE)){
            	
            	Map<String, String> hmEmpEmail = CF.getEmpEmailMap(con);
            	if(hmEmpEmail==null){
            		hmEmpEmail = new HashMap<String, String>();
            	}
            	
        		address = new InternetAddress[hmEmpEmail.size()];
        		Set set = hmEmpEmail.keySet();
        		Iterator it = set.iterator();
        		int count=0;
        		while(it.hasNext()){
        			String strEmail = (String)hmEmpEmail.get((String)it.next());
        			if(strEmail!=null){
    					address[count++] = new InternetAddress(strEmail);
    				}
        		}
            }
            
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(getStrEmailSubject());
            msg.setSentDate(new Date());

            // Set message content
            messageBodyPart.setContent(getStrEmailBody(), "text/html");
            multipart.addBodyPart(messageBodyPart); 

            
            if(getStrAttachmentFileSource()!=null){
                DataSource source = 
                  new FileDataSource(getStrAttachmentFileSource());
                attachmentPart.setDataHandler(new DataHandler(source));
                attachmentPart.setFileName(source.getName());
                if(getStrAttachmentFileName()!=null){
                	attachmentPart.setFileName(getStrAttachmentFileName());	
                }
                multipart.addBodyPart(attachmentPart);
            }
            
            
            if(pdf_data!=null){
            	
            	DataSource dataSource = new ByteArrayDataSource(pdf_data, "application/pdf");
            	MimeBodyPart pdfBodyPart = new MimeBodyPart();
            	pdfBodyPart.setDataHandler(new DataHandler(dataSource));
            	pdfBodyPart.setFileName(getStrAttachmentFileName());
            	
            	multipart.addBodyPart(pdfBodyPart);           
            }
            if(xls_data!=null){
            	
            	DataSource dataSource = new ByteArrayDataSource(xls_data, "application/xls");
            	MimeBodyPart pdfBodyPart = new MimeBodyPart();
            	pdfBodyPart.setDataHandler(new DataHandler(dataSource));
            	pdfBodyPart.setFileName(getStrAttachmentFileName());
            	
            	multipart.addBodyPart(pdfBodyPart);           
            }
 
            // Associate multi-part with message
            msg.setContent(multipart);

            // Send message
            Transport.send(msg);
//            Transport t = session.getTransport("smtps");
//			 t.connect(getStrHost(), "ehrportal", "123temple");
//			 t.sendMessage(msg, msg.getAllRecipients());
            
        }
        catch (MessagingException mex) {
            // Prints all nested (chained) exceptions as well
            mex.printStackTrace();
        }
        
	}
	
	private void sendEmailNotificationsFromGmail(){ 

        // Create properties, get Session
        Properties props = new Properties();

        // If using static Transport.send(),
        // need to specify which host to send it to
        props.put("mail.smtp.host", getStrHost());
        // To see what is going on behind the scene
        props.put("mail.debug", "true");
        
        Session session = Session.getInstance(props);
        Database db=new Database();
        Connection con=null;
        con=db.makeConnection(con);
        try {
            // Instantiatee a message
            MimeMessage msg = new MimeMessage(session);
            Multipart multipart = new MimeMultipart();
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            MimeBodyPart attachmentPart = new MimeBodyPart();
            
            //Set message attributes
            msg.setFrom(new InternetAddress(getStrEmailFrom()));
            
            InternetAddress[] address = null;
            
            
//            System.out.println("getStrEmailTo()===>"+getStrEmailTo());
            
            if(getStrEmailTo()!=null && !getStrEmailTo().equalsIgnoreCase(EVERYONE)){
            	address = new InternetAddress[1];
            	address[0] = new InternetAddress(getStrEmailTo());
            }else if(getStrSupervisorEmail()!=null && isSupervisor()){
            	address = new InternetAddress[1];
            	address[0] = new InternetAddress(getStrSupervisorEmail());
            }else if(getStrEmailTo()!=null && getStrEmailTo().equalsIgnoreCase(EVERYONE)){
            	Map<String, String> hmEmpEmail = CF.getEmpEmailMap(con);
            	if(hmEmpEmail==null){
            		hmEmpEmail = new HashMap<String, String>();
            	}
            	
            	
        		address = new InternetAddress[hmEmpEmail.size()];
        		
        		Set set = hmEmpEmail.keySet();
        		Iterator it = set.iterator();
        		int count=0;
        		while(it.hasNext()){
        			String strEmail = (String)hmEmpEmail.get((String)it.next());
        			if(strEmail!=null){
        				address[count++] = new InternetAddress(strEmail);
    				}
        		}
            }
            
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(getStrEmailSubject());
            msg.setSentDate(new Date());

            // Set message content
            messageBodyPart.setContent(getStrEmailBody(), "text/html");
            multipart.addBodyPart(messageBodyPart); 

            
//            System.out.println("messageBodyPart===>"+getStrEmailBody());
            
            if(getStrAttachmentFileSource()!=null){ 
            	
                DataSource source = 
                  new FileDataSource(getStrAttachmentFileSource());
                attachmentPart.setDataHandler(new DataHandler(source));
                attachmentPart.setFileName(source.getName());
                if(getStrAttachmentFileName()!=null){
                	attachmentPart.setFileName(getStrAttachmentFileName());	
                }
                
                multipart.addBodyPart(attachmentPart);
            }
            
            
            if(pdf_data!=null){
            	
            	DataSource dataSource = new ByteArrayDataSource(pdf_data, "application/pdf");
            	MimeBodyPart pdfBodyPart = new MimeBodyPart();
            	pdfBodyPart.setDataHandler(new DataHandler(dataSource));
            	pdfBodyPart.setFileName(getStrAttachmentFileName());
            	multipart.addBodyPart(pdfBodyPart);           
            }
            if(xls_data!=null){
            	
            	DataSource dataSource = new ByteArrayDataSource(xls_data, "application/xls");
            	MimeBodyPart pdfBodyPart = new MimeBodyPart();
            	pdfBodyPart.setDataHandler(new DataHandler(dataSource));
            	pdfBodyPart.setFileName(getStrAttachmentFileName());
            	multipart.addBodyPart(pdfBodyPart);           
            }
            
            
            
            // Associate multi-part with message
            msg.setContent(multipart);

            // Send message
//            Transport.send(msg);
            Transport t = session.getTransport("smtps");
			 t.connect(getStrHost(), getStrEmailFrom(), getStrHostPassword());
			 t.sendMessage(msg, msg.getAllRecipients());
              
        }
        catch (MessagingException mex) {
            // Prints all nested (chained) exceptions as well
            mex.printStackTrace();
        }
        
	}
	
	byte[] pdf_data;
	public void setPdfData(byte[] pdf_data){
		this.pdf_data = pdf_data;
	}
	
	byte[] xls_data;
	public void setXlsData(byte[] xls_data){
		this.xls_data = xls_data;
	}
	
	private void setEmpDetails(){


		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			
			if(CF==null)CF = new CommonFunctions(request);

			setStrDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF.getStrReportDateFormat()));
			
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
			Map hmWLocationMap = CF.getWorkLocationMap(con);
			
			pst = con.prepareStatement(selectEmpDetails1);
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			
//			System.out.println("pst===>"+pst);
			
			
			int nSupervisorId = 0;
			int nCount = 0;
			while(rs.next()){
				nSupervisorId = rs.getInt("supervisor_emp_id");
				setStrEmpCode(rs.getString("empcode"));
				
				if(rs.getString("emp_email_sec")!=null && rs.getString("emp_email_sec").indexOf("@")>0){
					setStrEmpEmail(rs.getString("emp_email_sec"));
					setStrEmailTo(rs.getString("emp_email_sec"));
				}else{
					setStrEmpEmail(rs.getString("emp_email"));
					setStrEmailTo(rs.getString("emp_email"));
				}
				
				
				setStrEmpFname(rs.getString("emp_fname"));
				setStrEmpLname(rs.getString("emp_lname"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				setStrEmpFullNamename(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				setStrEmpMobileNo(rs.getString("emp_contactno"));
				setStrAccountNo(rs.getString("emp_bank_acct_nbr"));
				setStrUserName(rs.getString("username"));
				setStrPassword(rs.getString("password"));
				setStrJoiningDate(uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));
				
				
				setStrEmpLevel(uF.showData(hmLevelMap.get(hmEmpLevelMap.get(rs.getString("emp_per_id"))), ""));
				setStrEmpDesignation(uF.showData(hmEmpDesigMap.get(rs.getString("emp_per_id")), ""));
				setStrEmpGrade(uF.showData(hmGradeMap.get(rs.getString("grade_id")), ""));
				
				Map<String, String> hmLocation = (Map)hmWLocationMap.get(rs.getString("wlocation_id"));
				if(hmLocation==null)hmLocation=new HashMap<String, String>();
				setStrEmpWLocation(hmLocation.get("WL_NAME"));
				nCount++;
			}
			rs.close();
			pst.close();
			
			
			if(nCount==0){
				pst = con.prepareStatement("select * from employee_personal_details where emp_per_id=?");
				pst.setInt	(1, uF.parseToInt(getStrEmpId()));
				rs = pst.executeQuery();
				
//				System.out.println("pst ===> "+pst);
				while(rs.next()){
					setStrEmpEmail(rs.getString("emp_email"));
					setStrEmailTo(rs.getString("emp_email"));
					setStrEmpFname(rs.getString("emp_fname"));
					setStrEmpLname(rs.getString("emp_lname"));
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					setStrEmpFullNamename(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				}
				rs.close();
				pst.close();
				
			}
			
			pst = con.prepareStatement("select emp_id, sum(amount) as gross from (select sd.emp_id, " +
					"amount from emp_salary_details sd, (select max(entry_date) as entry_date, " +
					"emp_id from emp_salary_details where entry_date <= ? and emp_id=? group by emp_id) a " +
					"where sd.emp_id = a.emp_id and sd.entry_date = a.entry_date " +
					"and salary_head_id in (select salary_head_id from salary_details " +
					"where earning_deduction = 'E' and isdisplay= true) order by sd.emp_id) b " +
					"where emp_id = ?  group by emp_id ");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, uF.parseToInt(getStrEmpId()));
			pst.setInt(3, uF.parseToInt(getStrEmpId()));
			rs= pst.executeQuery();
			while(rs.next()){
				setStrEmpCTC(uF.formatIntoComma(uF.parseToDouble(rs.getString("gross"))));
			}
			rs.close();
			pst.close();
			
			
//			pst = con.prepareStatement("select * from emp_kras where emp_id = ? and effective_date = (select max(effective_date) from emp_kras where effective_date <= ? and emp_id = ?)");
			pst = con.prepareStatement("select * from goal_kras where emp_ids like '%,"+getStrEmpId()+",%' and goal_type = "+EMPLOYEE_KRA+" and is_assign = true and effective_date = (select max(effective_date) from goal_kras where effective_date <= ? and emp_ids like '%,"+getStrEmpId()+",%' and goal_type = "+EMPLOYEE_KRA+")");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			StringBuilder sbKRAs = new StringBuilder();
			while(rs.next()) {
				if(rs.getString("kra_description")!=null && rs.getString("kra_description").length()>0) {
					sbKRAs.append("- "+rs.getString("kra_description").replace("\n", "<br/>")+"<br/>");
				}
			}
			rs.close();
			pst.close();
			
			setStrEmpKRAs(sbKRAs.toString());
			
			
			if(nSupervisorId>0 && isSupervisor()) {
				pst = con.prepareStatement(selectEmpDetails1);
				pst.setInt	(1, nSupervisorId);
				rs = pst.executeQuery();
				while(rs.next()) {
					setStrSupervisorEmail(rs.getString("emp_email"));
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					setStrSupervisorName(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
					setStrSupervisorContactNo(rs.getString("emp_contactno"));
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
	
	
	
	int nNotificationCode;
	String strHostAddress;
	String strContextPath;
	
	
	String strDate;
	
	boolean isSupervisor;
	String strEmpId;
	String strSupervisorName;
	String strSupervisorEmail;
	String strSupervisorContactNo;
	String strEmpCode;
	String strEmpFullNamename;
	String strEmpFname;
	String strEmpLname;
	String strJoiningDate;
	String strEmpCTC;
	String strEmpKRAs;
	
	String strEmpLevel;
	String strEmpDesignation;
	String strEmpGrade;
	String strEmpWLocation;
	
	
	String strEmpEmail;
	String strEmpMobileNo;
	String strAddEmpLink;
	
	
	
	String strEmpLeaveFrom;
	String strEmpLeaveTo;
	String strEmpLeaveNoOfDays;
	String strEmpLeaveReason;
	String strManagerLeaveReason;
	String strApprvedDenied;
	
	
	String strEmpReimbursementFrom;
	String strEmpReimbursementTo;
	String strEmpReimbursementPurpose;
	String strEmpReimbursementAmount;
	String strEmpReimbursementType;
	
	String strEmpReqPurpose;
	String strEmpReqMode;
	String strEmpReqType;
	String strEmpReqFrom;
	String strEmpReqTo;
	
	
	String strSalaryAmount;
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
		if(strEmpId!=null && strEmpId.equalsIgnoreCase(EVERYONE)){
			setStrEmailTo(EVERYONE);
		}else{
			setEmpDetails();
		}
		
	}


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
	
	
	
	// Training variables ***************************
	
	
	public String training_attribute;
	public String training_title;
	public String training_start_date;
	public String training_end_date;
	
	
	public String getTraining_attribute() {
		return training_attribute;
	}


	public void setTraining_attribute(String training_attribute) {
		this.training_attribute = training_attribute;
	}


	public String getTraining_title() {
		return training_title;
	}


	public void setTraining_title(String training_title) {
		this.training_title = training_title;
	}


	public String getTraining_start_date() {
		return training_start_date;
	}


	public void setTraining_start_date(String training_start_date) {
		this.training_start_date = training_start_date;
	}


	public String getTraining_end_date() {
		return training_end_date;
	}


	public void setTraining_end_date(String training_end_date) {
		this.training_end_date = training_end_date;
	}

	
	String plan_id;
	public String getPlan_id() {
		return plan_id;
	}


	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}
	
	
	String training_certificate_name;
	String training_trainer_name;
	String training_schedule_period;
	String training_location;
	
	public String getTraining_location() {
		return training_location;
	}


	public void setTraining_location(String training_location) {
		this.training_location = training_location;
	}


	public String getTraining_schedule_period() {
		return training_schedule_period;
	}


	public void setTraining_schedule_period(String training_schedule_period) {
		this.training_schedule_period = training_schedule_period;
	}


	public String getTraining_certificate_name() {
		return training_certificate_name;
	}


	public void setTraining_certificate_name(String training_certificate_name) {
		this.training_certificate_name = training_certificate_name;
	}


	public String getTraining_trainer_name() {
		return training_trainer_name;
	}


	public void setTraining_trainer_name(String training_trainer_name) {
		this.training_trainer_name = training_trainer_name;
	}
	
	
	// Recruitment variables *********************
	
	String jobDescription;
	String ctcOffered;
	String joiningDate;
	String reportingToManager;
	String interviewSchedule;
	String jobCode;
	String candidateName;
	
	
	public String getCandidateName() {
		return candidateName;
	}


	public void setCandidateName(String candidateName) {
		this.candidateName = candidateName;
	}


	public String getJobDescription() {
		return jobDescription;
	}


	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}


	public String getCtcOffered() {
		return ctcOffered;
	}


	public void setCtcOffered(String ctcOffered) {
		this.ctcOffered = ctcOffered;
	}


	public String getJoiningDate() {
		return joiningDate;
	}


	public void setJoiningDate(String joiningDate) {
		this.joiningDate = joiningDate;
	}


	public String getReportingToManager() {
		return reportingToManager;
	}


	public void setReportingToManager(String reportingToManager) {
		this.reportingToManager = reportingToManager;
	}


	public String getInterviewSchedule() {
		return interviewSchedule;
	}


	public void setInterviewSchedule(String interviewSchedule) {
		this.interviewSchedule = interviewSchedule;
	}


	public String getJobCode() {
		return jobCode;
	}


	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}

	
}