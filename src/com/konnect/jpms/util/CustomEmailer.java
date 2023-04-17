package com.konnect.jpms.util;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

public class CustomEmailer extends Thread implements IStatements,ServletRequestAware {
	
	String aToEmailAddr;
	String aEmailHost;
	String aFromEmail;
	String aFromPassword;
	String aSubject;
	String aBody;
	boolean isEmailNotifications = false;
	boolean isTextNotifications = false;
	
	public CustomEmailer(String aToEmailAddr, String aSubject, String aBody, String strDomain) {
		super();
		this.aToEmailAddr = aToEmailAddr;
		this.aSubject = aSubject;
		this.aBody = aBody;
		this.strDomain = strDomain;
	}
	
	public void sendCustomEmail() {
		if(!isAlive()){
			start();
		}
	}

	@Override
	public void run() {
		//System.out.println("in run method==");
		getFromEmailAndPassword();
		
	}
	
	private void getFromEmailAndPassword() {
		//System.out.println("in getFromEmailAndPassword==");
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		db.setDomain(strDomain);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			//System.out.println("IN sendNotifications Run");
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectSettings);
			rst = pst.executeQuery();
			while(rst.next()) {
				if(rst.getString("options").equalsIgnoreCase(O_EMAIL_NOTIFICATIONS)) {
					isEmailNotifications = uF.parseToBoolean(rst.getString("value"));
				} else if(rst.getString("options").equalsIgnoreCase(O_TEXT_NOTIFICATIONS)) {
					isTextNotifications = uF.parseToBoolean(rst.getString("value"));
				} else  if(rst.getString("options").equalsIgnoreCase(O_EMAIL_HOST)) {
					setaEmailHost(rst.getString("value"));
				} else if(rst.getString("options").equalsIgnoreCase(O_EMAIL_FROM)) {
					setaFromEmail(rst.getString("value"));
				} else if(rst.getString("options").equalsIgnoreCase(O_HOST_PASSWORD)) {
					setaFromPassword(rst.getString("value"));
				}
			}
			rst.close();
			pst.close();
			
			if(isEmailNotifications) {
				sendEmail();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
//	public static void main(String[] arg){
//		Emailer emailer=new Emailer("rahulpatil979@gmail.com", "Welcome to MyCatch fellow angler!", "Hello it is test mail ........");
//        Thread t = new Thread(emailer);
//        t.start(); 
//	}
	
	public void sendEmail() {
		//System.out.println("in sendEmail");

        try {
            Properties props = new Properties();
//            props.put("mail.smtp.host", "smtp.gmail.com");
//            props.put("mail.smtp.port", "587");
////            props.put("mail.smtp.port", "465");
//            props.put("mail.debug", "true");
//            props.put("mail.smtp.auth", "true");
//            props.put("mail.smtp.starttls.enable", "true");
//            //props.put("mail.smtp.localhost", "http://www.mycatchapp.com");
//            
////            props.put("mail.smtp.socketFactory.port", "465");
//            props.put("mail.smtp.socketFactory.port", "587");
//    		props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");

//            props.put("mail.smtp.host", aEmailHost);
//            // To see what is going on behind the scene
//            props.put("mail.debug", "true");
//            if(CF.isOffice365Smtp()) {
//            	props.put("mail.smtp.starttls.enable", "true");  
//            } 
            
            props.put("mail.smtp.host", aEmailHost);
            props.put("mail.smtp.port", "25");
//            props.put("mail.smtp.port", "465");
            props.put("mail.debug", "true");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            //props.put("mail.smtp.localhost", "http://www.mycatchapp.com");
            
//            props.put("mail.smtp.socketFactory.port", "465");
//            props.put("mail.smtp.socketFactory.port", "25");
//    		props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
    		
            Session s = Session.getInstance(props, null);
            s.setDebug(true);

            MimeMessage message = new MimeMessage(s);

            InternetAddress from = new InternetAddress(aFromEmail, "");
            InternetAddress to = new InternetAddress(aToEmailAddr);

            message.setSentDate(new Date());
            message.setFrom(from);
            message.addRecipient(Message.RecipientType.TO, to);
            
            message.setSubject(aSubject);
            message.setContent(aBody, "text/html");

            Transport tr = s.getTransport("smtp");
            tr.connect(aEmailHost, aFromEmail, aFromPassword);
            message.saveChanges();
            tr.sendMessage(message, message.getAllRecipients());
            tr.close();

        } catch (AddressException ex) {
        	
        } catch (MessagingException ex) {
        	
        } catch (UnsupportedEncodingException ex) {
        	
        }
    }

	public String getaEmailHost() {
		return aEmailHost;
	}

	public void setaEmailHost(String aEmailHost) {
		this.aEmailHost = aEmailHost;
	}

	public String getaFromEmail() {
		return aFromEmail;
	}

	public void setaFromEmail(String aFromEmail) {
		this.aFromEmail = aFromEmail;
	}

	public String getaFromPassword() {
		return aFromPassword;
	}

	public void setaFromPassword(String aFromPassword) {
		this.aFromPassword = aFromPassword;
	}

	String strDomain;
	public void setDomain(String strDomain) {
		this.strDomain=strDomain;
	}
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
}
