package com.konnect.jpms.task;

	import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
	import javax.mail.Message;
import javax.mail.Multipart;
	import javax.mail.PasswordAuthentication;
	import javax.mail.Session;
	import javax.mail.Transport;
	import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
	import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.opensymphony.xwork2.ActionSupport;

	public class SendTimeSheetMail extends ActionSupport {

	   /**
		 * 
		 */
		private static final long serialVersionUID = -7396868126505300254L;
	private String from;
	   private String password;
	   private String to;
	   private String subject;
	   private String body;

	   static Properties properties = new Properties();
	   static
	   {
	      properties.put("mail.smtp.host", "smtp.gmail.com");
	      properties.put("mail.smtp.socketFactory.port", "465");
	      properties.put("mail.smtp.socketFactory.class",
	                     "javax.net.ssl.SSLSocketFactory");
	      properties.put("mail.smtp.auth", "true");
	      properties.put("mail.smtp.port", "465");
	   }

	   public String execute() 
	   {
	      String ret = "";
	      try
	      {
	         Session session = Session.getDefaultInstance(properties,  
	            new javax.mail.Authenticator() {
	            protected PasswordAuthentication 
	            getPasswordAuthentication() {
	            return new 
	            PasswordAuthentication("vishwajit.konnect@gmail.com", "123templeIT25");
	            }});

	         Message message = new MimeMessage(session);
	         message.setFrom(new InternetAddress("vishwajit.konnect@gmail.com"));
	         message.setRecipients(Message.RecipientType.TO, 
	            InternetAddress.parse("vishwajit.dhule@konnecttechnologies.com"));
	         message.setSubject("Mail Testing ");
//	         message.setText("Testing");
	         
	         MimeBodyPart messagePart = new MimeBodyPart();
	            messagePart.setText("Mail Testing");
	         MimeBodyPart attachmentPart = new MimeBodyPart();
	            FileDataSource fileDataSource = new FileDataSource("/home/konnect/Desktop/vishwajit/work/PayrollMS/WebContent/TaskTimesheet/TimeSheet.pdf") {
	                @Override
	                public String getContentType() {
	                    return "application/octet-stream";
	                }
	            };
	            attachmentPart.setDataHandler(new DataHandler(fileDataSource));
	            attachmentPart.setFileName(fileDataSource.getName());
	 
	            Multipart multipart = new MimeMultipart();
	            multipart.addBodyPart(messagePart);
	            multipart.addBodyPart(attachmentPart);
	 
	            message.setContent(multipart);
	         
	         
	         Transport.send(message);
	      }
	      catch(Exception e)
	      {
	         
	         e.printStackTrace();
	      }
	      return SUCCESS;
	   }

	   public String getFrom() {
	      return from;
	   }

	   public void setFrom(String from) {
	      this.from = from;
	   }

	   public String getPassword() {
	      return password;
	   }

	   public void setPassword(String password) {
	      this.password = password;
	   }

	   public String getTo() {
	      return to;
	   }

	   public void setTo(String to) {
	      this.to = to;
	   }

	   public String getSubject() {
	      return subject;
	   }

	   public void setSubject(String subject) {
	      this.subject = subject;
	   }

	   public String getBody() {
	      return body;
	   }

	   public void setBody(String body) {
	      this.body = body;
	   }

	   public static Properties getProperties() {
	      return properties;
	   }

	   public static void setProperties(Properties properties) {
		   SendTimeSheetMail.properties = properties;
	   }
	}
