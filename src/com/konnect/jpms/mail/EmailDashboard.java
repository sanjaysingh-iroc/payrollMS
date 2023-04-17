package com.konnect.jpms.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmailDashboard extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	boolean isEmpUserType = false;
	String strEmpOrgId = null;
	CommonFunctions CF = null;
	
	String emailNo;
	String emailFrom;
	String emailTo;
	String emailCC;
	String emailBCC;
	String emailSubject;
	String emailBody;
	String emailReceivedDate;
	List<String> alAttachments;
	
	String strHost;
	String strEmailAuthName;
	String strEmailAuthPassword;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strEmpOrgId  = (String) session.getAttribute(ORGID);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/mail/EmailDashboard.jsp");
		request.setAttribute(TITLE, "Email Dashboard");
		

//		String hostval = "pop.gmail.com";
		/*String hostval = "imap.gmail.com";
		String mailStrProt = "imap";
//		String mailStrProt = "pop3";
		String uname = "mudassir.workrig@gmail.com";
//		String password= "!Parvez1314";
		String password= "tkisxmruosmkcfem";*/
		
		getServerDetails();
//		checkMail(hostval,mailStrProt,uname,password);
		if(getStrHost()!=null){
			checkMail();
			saveEmails();
		}
			
		return LOAD;

	}
	
	private void getServerDetails(){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectSettings);
			rs = pst.executeQuery();
			while(rs.next()) {
				if(rs.getString("options").equalsIgnoreCase(O_EMAIL_INBOX_HOST)) {
					setStrHost(rs.getString("value"));
				} else if(rs.getString("options").equalsIgnoreCase(O_EMAIL_INBOX_AUTHENTICATION_USER)) {
					setStrEmailAuthName(rs.getString("value"));
				} else if(rs.getString("options").equalsIgnoreCase(O_EMAIL_INBOX_AUTHENTICATION_PASSWORD)) {
					setStrEmailAuthPassword(rs.getString("value"));
				}
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void checkMail() {
		try{
//			System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
			Properties props = new Properties();
			props.put("mail.imap.host", getStrHost());
			props.put("mail.imap.auth", "true");
			props.put("mail.imap.starttls.enable", "true");
			props.put("mail.store.protocol", "imaps");
//			props.put("mail.debug", "true");
			/*props.put("mail.pop3.host", getStrHost());
			props.put("mail.pop3.auth", "true");
			props.put("mail.pop3.starttls.enable", "true");
			props.put("mail.store.protocol", "pop3s");*/
//			props.put("mail.debug", "true");
//			Session session = Session.getInstance(props);
			Session mailSession = Session.getDefaultInstance(props);
//			Store store = mailSession.getStore("pop3s");
			Store store = mailSession.getStore("imaps"); 
			store.connect(getStrHost(),getStrEmailAuthName(),getStrEmailAuthPassword());
			Folder folder = store.getFolder("INBOX");
			folder.open(Folder.READ_ONLY);
			Message[] messages = folder.getMessages();
			
			/*for(int i=0; i<messages.length; i++){
				Message indMessage = messages[i];
				System.out.println("Printing Individual Messages");
				System.out.println("No#" + (i+1));
				System.out.println("Email Subject: " + indMessage.getSubject());
				System.out.println("Sender: " + indMessage.getFrom()[0]);
				System.out.println("Content: " + indMessage.getContent().toString());
			}*/
			int msgSize = messages.length-1;
			Message indMessage = messages[msgSize];
			setEmailNo((msgSize+1)+"");
			
			getBodyContent(indMessage);
			
			/*System.out.println("No#" + getEmailNo());
		    System.out.println("Email Subject: " + getEmailSubject());
			System.out.println("Sender: " + getEmailFrom());
			System.out.println("Recipients To: " + getEmailTo());
			System.out.println("Recipients CC: " + getEmailCC());
			System.out.println("Received Date: " + getEmailReceivedDate());
			System.out.println("Contenet: " + getEmailBody());*/
			
			folder.close(false);
			store.close();
			
			mailSession = null;
		} catch(NoSuchProviderException npe){
			npe.printStackTrace();
		} catch (MessagingException me) {
			me.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void getBodyContent(Part part) throws IOException, MessagingException, Exception{
		
		if(part instanceof Message){
			getEmailInfo((Message) part);
		}
		
//		System.out.println("----------------------------");
//	    System.out.println("CONTENT-TYPE: " + part.getContentType());
	    
	    if(part.isMimeType("text/plain")){
	    	setEmailBody((String) part.getContent());
	    	System.out.println((String) part.getContent());
	    } else if(part.isMimeType("multipart/*")){
	    	Multipart mp = (Multipart) part.getContent();
	    	int count = mp.getCount();
	    	for(int i =0; i<count; i++){
	    		List<String> alAttachDoc = getAlAttachments();
	    		if(alAttachDoc == null){
	    			alAttachDoc = new ArrayList<String>();
	    		}
	    		MimeBodyPart bPart  = (MimeBodyPart) mp.getBodyPart(i);
		    	if (Part.ATTACHMENT.equalsIgnoreCase(bPart.getDisposition())) {
		    	    String strFileName = null;
		    	    InputStream stream = (InputStream) bPart.getInputStream();
		    	    if (CF.getStrDocSaveLocation() == null) {
//		    	    	bPart.saveFile(DOCUMENT_LOCATION + File.separator + bPart.getFileName());
		    	    	strFileName = uploadImageDocuments(DOCUMENT_LOCATION, stream, bPart.getFileName(), bPart.getFileName());
		    	    } else{
//		    	    	bPart.saveFile(CF.getStrDocSaveLocation() + I_INBOX_ATTACHMENT + "/" + I_DOCUMENT + File.separator + bPart.getFileName());
		    	    	strFileName = uploadImageDocuments(CF.getStrDocSaveLocation() + I_INBOX_ATTACHMENT + "/" + I_DOCUMENT, stream, bPart.getFileName(), bPart.getFileName());
		    	    }
		    	    alAttachDoc.add(strFileName);
		    	} else{
		    		getBodyContent(mp.getBodyPart(i));
		    	}
		    	if(getAlAttachments() == null){
		    		alAttachments = new ArrayList<String>();
		    	}
		    	setAlAttachments(alAttachDoc);
	    	}
	    } else if(part.isMimeType("APPLICATION/*")){
	    	
	    	
	    } else if(part.isMimeType("IMAGE/JPEG")){
	    	
	    }
	}
	
	public void getEmailInfo(Message msg) throws Exception{
		
		setEmailSubject(msg.getSubject());
		
		StringBuilder sbEmailAddress = new StringBuilder();
		Address[] fromAddress = msg.getFrom();
		for(int i=0; i<fromAddress.length; i++){
			if(i == 0){
				sbEmailAddress.append(fromAddress[i]);
			} else{
				sbEmailAddress.append("," + fromAddress[i]);
			}
		}
		setEmailFrom(sbEmailAddress.toString());
		
		if(msg.getRecipients(Message.RecipientType.TO) != null){
			Address[] toAddress = msg.getRecipients(Message.RecipientType.TO);
			sbEmailAddress = new StringBuilder();
			for(int i=0; i<toAddress.length; i++){
				if(i == 0){
					sbEmailAddress.append(toAddress[i]);
				} else{
					sbEmailAddress.append("," + toAddress[i]);
				}
			}
			setEmailTo(sbEmailAddress.toString());
		} else{
			setEmailTo(null);
		}
		
		if(msg.getRecipients(Message.RecipientType.CC) != null){
			Address[] ccAddress = msg.getRecipients(Message.RecipientType.CC);
			sbEmailAddress = new StringBuilder();
			for(int i=0; i<ccAddress.length; i++){
				if(i == 0){
					sbEmailAddress.append(ccAddress[i]);
				} else{
					sbEmailAddress.append("," + ccAddress[i]);
				}
			}
			setEmailCC(sbEmailAddress.toString());
		} else{
			setEmailCC(null);
		}
		
		SimpleDateFormat smft = new SimpleDateFormat(DBTIMESTAMP_STR);
		
		if(msg.getReceivedDate()!=null){
			setEmailReceivedDate(smft.format(msg.getReceivedDate()));
		} else{
			setEmailReceivedDate(null);
		}
		
//		setEmailReceivedDate(msg.getReceivedDate()+"");
		
	}
	
	public void saveEmails(){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from email_details where email_no=? and email_subject=? and email_from=? and to_date(email_received_date::text, 'YYYY-MM-DD')=?");
			pst.setInt(1, uF.parseToInt(getEmailNo()));
			pst.setString(2, getEmailSubject());
			pst.setString(3, getEmailFrom());
//			pst.setTimestamp(4, uF.getTimeStamp(getEmailReceivedDate(), DBTIMESTAMP_STR));
			pst.setDate(4, uF.getDateFormat(getEmailReceivedDate(), DATE_FORMAT));
//			System.out.println("REN/227--"+pst);
			rs = pst.executeQuery();
			boolean flag = false;
			while(rs.next()){
				flag = true;
			}
			rs.close();
            pst.close();
//            System.out.println("getAlAttachments=="+getAlAttachments());
            
            StringBuilder sbDoc = null;
            for(int j=0; getAlAttachments() != null && j< getAlAttachments().size(); j++){
            	if(sbDoc == null){
            		sbDoc = new StringBuilder();
            		sbDoc.append(getAlAttachments().get(j));
            	} else{
            		sbDoc.append("," + getAlAttachments().get(j));
            	}
            }
			
			if(!flag && getEmailFrom() != null){
				pst = con.prepareStatement("insert into email_details (email_subject, email_body, email_from, email_to, emp_id, email_no, email_received_date, email_cc, email_attachment) " +
						"values (?,?,?,?, ?,?,?,?, ?)");
				pst.setString(1, getEmailSubject());
				pst.setString(2, getEmailBody());
				pst.setString(3, getEmailFrom());
				pst.setString(4, getEmailTo());
				pst.setInt(5, uF.parseToInt(strSessionEmpId));
				pst.setInt(6, uF.parseToInt(getEmailNo()));
				pst.setTimestamp(7, getEmailReceivedDate()!=null ? uF.getTimeStamp(getEmailReceivedDate(), DBTIMESTAMP_STR) : null);
				pst.setString(8, getEmailCC());
				pst.setString(9, sbDoc!=null?sbDoc.toString(): null);
				pst.execute();
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
	
	public String uploadImageDocuments(String strFolderWithLocation, InputStream inputStream, String fileFileName, String strFileName) throws Exception {
		  
		String uploadDir = strFolderWithLocation+"/";
		
		// write the file to the file specified
//		System.out.println("strFolderWithLocation ===>>> " +strFolderWithLocation);
		File dirPath = new File(uploadDir);
		if (!dirPath.exists()) {
			dirPath.mkdirs(); 
		} else {
//			System.out.println("Folder Already Available ....... !");
		}
		
		int random = new Random().nextInt();
		strFileName = random+strFileName;
		
		// retrieve the file data
		InputStream stream = inputStream;

		// write the file to the file specified
//		System.out.println("uploadDir + strFileName ===>>> " +uploadDir + strFileName);
		
		OutputStream bos = new FileOutputStream(uploadDir + strFileName);
		int bytesRead;
		byte[] buffer = new byte[8192];

		while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
			bos.write(buffer, 0, bytesRead);
//			System.out.println("insert Image ===>>> ");
		}

		bos.close();
		stream.close();

		// place the data into the request for retrieval on next page
		request.setAttribute("location", dirPath.getAbsolutePath() + "/" + strFileName);

		String link = request.getContextPath() + "/" + strFolderWithLocation + "/";

		request.setAttribute("link", link + strFileName);
//		System.out.println("strFileName ===>>> " + strFileName);
		return strFileName;
		
	}
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getEmailNo() {
		return emailNo;
	}

	public void setEmailNo(String emailNo) {
		this.emailNo = emailNo;
	}

	public String getEmailFrom() {
		return emailFrom;
	}

	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}

	public String getEmailTo() {
		return emailTo;
	}

	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}

	public String getEmailCC() {
		return emailCC;
	}

	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}

	public String getEmailBCC() {
		return emailBCC;
	}

	public void setEmailBCC(String emailBCC) {
		this.emailBCC = emailBCC;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailBody() {
		return emailBody;
	}

	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}

	public String getEmailReceivedDate() {
		return emailReceivedDate;
	}

	public void setEmailReceivedDate(String emailReceivedDate) {
		this.emailReceivedDate = emailReceivedDate;
	}

	public List<String> getAlAttachments() {
		return alAttachments;
	}

	public void setAlAttachments(List<String> alAttachments) {
		this.alAttachments = alAttachments;
	}

	public String getStrHost() {
		return strHost;
	}

	public void setStrHost(String strHost) {
		this.strHost = strHost;
	}

	public String getStrEmailAuthName() {
		return strEmailAuthName;
	}

	public void setStrEmailAuthName(String strEmailAuthName) {
		this.strEmailAuthName = strEmailAuthName;
	}

	public String getStrEmailAuthPassword() {
		return strEmailAuthPassword;
	}

	public void setStrEmailAuthPassword(String strEmailAuthPassword) {
		this.strEmailAuthPassword = strEmailAuthPassword;
	}
	
}
