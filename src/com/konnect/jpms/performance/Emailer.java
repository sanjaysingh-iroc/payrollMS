package com.konnect.jpms.performance;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Emailer implements Runnable {
	
	private String aToEmailAddr;
	private String aSubject;
	private String aBody;

	public Emailer(String aToEmailAddr, String aSubject, String aBody) {
		super();
		this.aToEmailAddr = aToEmailAddr;
		this.aSubject = aSubject;
		this.aBody = aBody;
	}

	@Override
	public void run() {
		sendEmail5();
	}
	
	public static void main(String[] arg){
		
//		String strBody = "<div style=\"width:100%;font-size: 12px;font-family:calibri;\" align=\"center\"><table style=\"width:60%;font-family:calibri;border:1px solid #C5C5C5;\">" +
//				"<tbody><tr><td colspan=\"2\"><div style=\"margin:0px 0px 0px 20px;float:left;height:50px;\"><img height=\"60\" src=\"https://myworkrig.com/Workrig/EGovTech/Organisation/Image/null\"/>" +
//				"</div><div style=\"float: left; margin: 31px 0px 0px;\"><div style=\"float: left; font-weight: bold; font-size: 10px; margin-top: 14px;\">Powered By </div><div style=\"float: left;\">" +
//				"<img src=\"http://raksys-india.myworkrig.com/images1/icons/icons/workrig.png\" style=\"width: 90px;\" height=\"40\"></div></div></td><td align=\"right\">" +
//				"<a href=\"http://raksys-india.myworkrig.com/Login.action\"><input type=\"button\" class=\"input_button\" value=\"Login\" style=\"margin-right:50px;background-repeat: repeat-x;border: 1px solid #5d862b;background-color:#339900;color: #FFFFFF;font-family: Verdana, arial, helvetica, sans-serif;font-weight: bold;font-size: 12px;height: 25px;padding-left: 4px;padding-top: 0px;cursor: pointer;-moz-border-radius: 3px;-webkit-border-radius: 3px;border-radius: 3px;width: 125px;width: 20%;padding: 3px 50px;outline: 0;margin: 10px 0px 0px 0px;\"/></a></td></tr>" +
//				"<tr><td style=\"border-bottom:1px solid #C5C5C5;\" colspan=\"3\">&nbsp;<<br/>RAKSYS INDIA LLP  </td></tr><tr><td style=\"border-bottom:1px solid #C5C5C5;\" colspan=\"3\">&nbsp;</td></tr>" +
//				"<tr><td colspan=\"3\" style=\"text-aRlign:center;background-color:#707070;color:white;font-size: 12px;font-family:calibri;\"><p>You have received this mails since you are subscribed to Workrig HCM-PPM package.</p><p>If you have any problems in the mail, kindly write to us at support@workrig.com<br/>or call us at +91 20 41202831 or check us at www.workrig.com</p></td></tr></tbody></table>" +
//				"<div style=\"width:60%;font-size: 12px;font-family:calibri;\" align=\"center\"><p><strong>Our Mail address is:</strong></p><p>Taskrig Solutions Pvt. Ltd. | Office No. 1, 3rd Floor, Kothari Plaza, Lulla Nagar, Pune - 411040, India</p><p>" +
//				"<img src=\"http://raksys-india.myworkrig.com/images1/icons/icons/workrig.png\" style=\"width: 90px;\" height=\"40\"></p><p><a href=\"http://raksys-india.myworkrig.com/Login.action\">View in a browser</a></p></div></div>";
		
		String strBody = "<div style=\"width:100%;font-size: 12px;font-family:calibri;\" align=\"center\"><table style=\"width:60%;font-family:calibri;border:1px solid #C5C5C5;\">" +
				"<tbody><tr><td colspan=\"2\"><div style=\"margin:0px 0px 0px 20px;float:left;height:50px;\"><img height=\"60\" src=\"https://myworkrig.com/Workrig/EGovTech/Organisation/Image/null\"/>" +
				"</div><div style=\"float: left; margin: 31px 0px 0px;\"><div style=\"float: left; font-weight: bold; font-size: 10px; margin-top: 14px;\">Powered By </div><div style=\"float: left;\">" +
				"<img src=\"http://raksys-india.myworkrig.com/images1/icons/icons/workrig.png\" style=\"width: 90px;\" height=\"40\"></div></div></td><td align=\"right\">" +
				"<a href=\"http://raksys-india.myworkrig.com/Login.action\"><input type=\"button\" class=\"input_button\" value=\"Login\" style=\"margin-right:50px;background-repeat: repeat-x;border: 1px solid #5d862b;background-color:#339900;color: #FFFFFF;font-family: Verdana, arial, helvetica, sans-serif;font-weight: bold;font-size: 12px;height: 25px;padding-left: 4px;padding-top: 0px;cursor: pointer;-moz-border-radius: 3px;-webkit-border-radius: 3px;border-radius: 3px;width: 125px;width: 20%;padding: 3px 50px;outline: 0;margin: 10px 0px 0px 0px;\"/></a></td></tr>" +
				"<tr><td style=\"border-bottom:1px solid #C5C5C5;\" colspan=\"3\">&nbsp;<br/>RAKSYS INDIA LLP  </td></tr><tr><td style=\"border-bottom:1px solid #C5C5C5;\" colspan=\"3\">&nbsp;</td></tr>" +
				"<tr><td colspan=\"3\" style=\"text-align:center;background-color:#707070;color:white;font-size: 12px;font-family:calibri;\"><p>You have received this mail since you are subscribed to Workrig HCM-PPM package.</p><p>If you have any problems in the mail, kindly write to us at support@workrig.com<br/>or call us at +91 20 2683 2117 or check us at www.workrig.com</p></td></tr></tbody></table>" +
				"<div style=\"width:60%;font-size: 12px;font-family:calibri;\" align=\"center\"><p><strong>Our Mail address is:</strong></p><p>Workrig | Office No. 1, 3rd Floor, Kothari Plaza, Lulla Nagar, Pune - 411040, India</p><p>" +
				"<img src=\"http://raksys-india.myworkrig.com/images1/icons/icons/workrig.png\" style=\"width: 90px;\" height=\"40\"></p><p><a href=\"http://abc.myworkrig.com/Login.action\">View in a browser</a></p></div></div>";
		Emailer emailer=new Emailer("ta.blr@raksys.in", "Welcome", strBody); //Taskrig Solutions Pvt. Ltd.
        Thread t = new Thread(emailer);
        t.start(); 
//		try {
//			sendMail3("leonette.henry@nichelive.com", "sandesh.date@konnecttechnologies.com", "hello", "aaa asda ");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (MessagingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public void sendEmail1() {

        try {
        	final String fromEmail = "leonette.henry@nichelive.com"; //requires valid gmail id
            final String password = "Denis1212"; // correct password for gmail id
            final String toEmail = "sandesh.date@konnecttechnologies.com"; // can be any email id 

            System.out.println("TLSEmail Start");
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.googlemail.com"); //SMTP Host
            props.put("mail.smtp.port", "587"); //TLS Port
            props.put("mail.smtp.auth", "true"); //enable authentication
            props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

                //create Authenticator object to pass in Session.getInstance argument
            Authenticator auth = new Authenticator() {
                //override the getPasswordAuthentication method
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            };
            Session session = Session.getInstance(props, auth);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

            System.out.println("Mail Check 2");

            message.setSubject("Oil Error Report");
            message.setText(aBody);

            System.out.println("Mail Check 3");

            Transport.send(message);
            System.out.println("Mail Sent");
        } catch (AddressException ex) {
            Logger.getLogger(Emailer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(Emailer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	
	public void sendEmail() {

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

            props.put("mail.smtp.host", "smtpout.secureserver.net");
//            props.put("mail.smtp.port", "25");
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

            InternetAddress from = new InternetAddress("info@konnectconsultancy.com", "Rp");
            InternetAddress to = new InternetAddress(aToEmailAddr);

            message.setSentDate(new Date());
            message.setFrom(from);
            message.addRecipient(Message.RecipientType.TO, to);
            
            message.setSubject(aSubject);
            message.setContent(aBody, "text/html");

            Transport tr = s.getTransport("smtp");
            tr.connect("smtpout.secureserver.net", "info@konnectconsultancy.com", "789enigma123");
            message.saveChanges();
            tr.sendMessage(message, message.getAllRecipients());
            tr.close();

        } catch (AddressException ex) {
            Logger.getLogger(Emailer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(Emailer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Emailer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	
	public void sendEmail2() {

        try {
        	 String to = "sandesh.date@konnecttechnologies.com";
    	    String subject = "subject";
    	    String msg ="email text....";
    	    final String from ="leonette.henry@nichelive.com";
    	    final  String password ="Denis1212";


    	    Properties props = new Properties();  
    	    props.setProperty("mail.transport.protocol", "smtps");     
    	    props.setProperty("mail.host", "smtp.googlemail.com");  
    	    props.put("mail.smtp.auth", "true");  
    	    props.put("mail.smtp.port", "465");  
    	    props.put("mail.debug", "true");  
//    	    props.put("mail.smtp.socketFactory.port", "465");  
    	    props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");  
    	    props.put("mail.smtp.socketFactory.fallback", "false");  
    	   
    	    Session session = Session.getDefaultInstance(props,  
    	    new javax.mail.Authenticator() {
    	       protected PasswordAuthentication getPasswordAuthentication() {  
    	       return new PasswordAuthentication(from,password);  
    	   }  
    	   });  

    	   //session.setDebug(true);  
    	   Transport transport = session.getTransport();  
    	   InternetAddress addressFrom = new InternetAddress(from);  

    	   MimeMessage message = new MimeMessage(session);  
    	   message.setSender(addressFrom);  
    	   message.setSubject(subject);  
    	   message.setContent(msg, "text/html");  
    	   message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));  

    	   transport.connect();  
    	   Transport.send(message);  
    	   transport.close();

        } catch (AddressException ex) {
            Logger.getLogger(Emailer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(Emailer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	
	public static void sendMail3(String senderEmail, String recipientEmail, String subject, String message) throws MessagingException, UnsupportedEncodingException {

        // SSL // I USED THIS METHOD            
        Properties propsSSL = new Properties();

        // EVEN IF YOU SKIP THESE TWO PROP IT WOULD WORK
        propsSSL.put("mail.transport.protocol", "smtps");
        propsSSL.put("mail.smtps.host", "smtp.googlemail.com");

        // THIS IS THE MOST IMPORTANT PROP --> "mail.smtps.auth"
        propsSSL.put("mail.smtps.auth", "true");

        Session sessionSSL = Session.getInstance(propsSSL);
        sessionSSL.setDebug(true);

        Message messageSSL = new MimeMessage(sessionSSL);
        messageSSL.setFrom(new InternetAddress("leonette.henry@nichelive.com", "Mlungisi Sincuba"));
        messageSSL.setRecipients(Message.RecipientType.TO, InternetAddress.parse("sandesh.date@konnecttechnologies.com")); // real recipient
        messageSSL.setSubject("Test mail using SSL");
        messageSSL.setText("This is test email sent to Your account using SSL.");

        Transport transportSSL = sessionSSL.getTransport();
        // EVEN IF YOU SKIP PORT NUMBER , IT WOULD WORK
        transportSSL.connect("smtp.googlemail.com", "leonette.henry@nichelive.com", "Denis1212"); // account used
        transportSSL.sendMessage(messageSSL, messageSSL.getAllRecipients());
        transportSSL.close();

        System.out.println("SSL done.");
    }
    
	public void sendEmail4() {

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

            props.put("mail.smtp.host", "smtp.postmarkapp.com");
//            props.put("mail.smtp.port", "25");
//            props.put("mail.smtp.port", "465");
            props.put("mail.debug", "true");
            props.put("mail.smtp.auth", "true");
//            props.put("mail.smtp.starttls.enable", "true");
            
//            props.put("mail.smtp.socketFactory.port", "465");
//            props.put("mail.smtp.socketFactory.port", "25");
//    		props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
    		
            Session s = Session.getDefaultInstance(props,  
        	    new javax.mail.Authenticator() {
        	       protected PasswordAuthentication getPasswordAuthentication() {  
//        	       return new PasswordAuthentication("rahul.patil@konnecttechnologies.com","123temple");  
        	    	   return new PasswordAuthentication("4cba0701-e7f2-4d90-9e32-123be691b192", "4cba0701-e7f2-4d90-9e32-123be691b192");	   
        	   }  
        	   }); 
            
//            Session s = Session.getInstance(props, null);
//            s.setDebug(true);

            MimeMessage message = new MimeMessage(s);

            InternetAddress from = new InternetAddress("rahul.patil@konnecttechnologies.com", "Rp");
            InternetAddress to = new InternetAddress(aToEmailAddr);

            message.setSentDate(new Date());
            message.setFrom(from);
            message.addRecipient(Message.RecipientType.TO, to);
            
            message.setSubject(aSubject);
            message.setContent(aBody, "text/html");
//            message.setContent(aBody, "text/plain");
            
            Transport tr = s.getTransport("smtp");
//            tr.connect("smtp.postmarkapp.com", "4cba0701-e7f2-4d90-9e32-123be691b192", "4cba0701-e7f2-4d90-9e32-123be691b192");
            tr.connect();
            message.saveChanges();
            tr.sendMessage(message, message.getAllRecipients());
            tr.close();

        } catch (AddressException ex) {
            Logger.getLogger(Emailer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(Emailer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Emailer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	
	public void sendEmail5() {

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

            props.put("mail.smtp.host", "email-smtp.us-east-1.amazonaws.com");
//            props.put("mail.smtp.port", "25");
//            props.put("mail.smtp.port", "465");
            props.put("mail.debug", "true");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            
//            props.put("mail.smtp.socketFactory.port", "465");
//            props.put("mail.smtp.socketFactory.port", "25");
//    		props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
    		
            Session s = Session.getDefaultInstance(props,  
        	    new javax.mail.Authenticator() {
        	       protected PasswordAuthentication getPasswordAuthentication() {  
//        	       return new PasswordAuthentication("rahul.patil@konnecttechnologies.com","123temple");  
        	    	   return new PasswordAuthentication("AKIAJILX4P4R6WHO2DAA", "Amu8j+Irg+KM83TrRQEh523Di69VXUm6yUsUnfMP+r+x");	   
        	   }  
        	   }); 
            
//            Session s = Session.getInstance(props, null);
//            s.setDebug(true);

            MimeMessage message = new MimeMessage(s);

            InternetAddress from = new InternetAddress("no-reply@workrig.com", "");
            InternetAddress to = new InternetAddress(aToEmailAddr);

            message.setSentDate(new Date());
            message.setFrom(from);
            message.addRecipient(Message.RecipientType.TO, to);
            
            message.setSubject(aSubject);
            message.setContent(aBody, "text/html");
//            message.setContent(aBody, "text/plain");
            
            Transport tr = s.getTransport("smtp");
//            tr.connect("smtp.postmarkapp.com", "4cba0701-e7f2-4d90-9e32-123be691b192", "4cba0701-e7f2-4d90-9e32-123be691b192");
            tr.connect("email-smtp.us-east-1.amazonaws.com", "no-reply@workrig.com", "123temple");
//            tr.connect();
//            message.saveChanges();
            tr.sendMessage(message, message.getAllRecipients());
            tr.close();

        } catch (AddressException ex) {
            Logger.getLogger(Emailer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(Emailer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Emailer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//    public static void main(String[] args) throws MessagingException, UnsupportedEncodingException {
//        System.out.println("Hello World!");
//        sendMail(null, null, null, null);
//    }
}
