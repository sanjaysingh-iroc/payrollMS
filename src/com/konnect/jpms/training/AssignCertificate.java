package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AssignCertificate extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	private int empId;  

	CommonFunctions CF=null;

	private static Logger log = Logger.getLogger(AssignCertificate.class);
	
	public String execute() {
		
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			return LOGIN;
		}
		
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
	
		insertCertificate();

	//	assignCertificate();
		
		return SUCCESS;
	}


//	private void assignCertificate() {
//		
//
//		Connection con = null;
//		ResultSet rs = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//	db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//
//		try {
//
//			
///*			System.out.println("Subject:"+getStrSubject());
//			System.out.println("Mail body:"+getStrMailBody());
//			System.out.println("Document:"+getStrDocument());
//			System.out.println("getEmp_id:"+getEmp_id());
//			*/
//			
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select certificate_name,certificate_desc from training_certificate join training_plan using(certificate_id) where plan_id=?");
//			pst.setInt(1, uF.parseToInt(getPlanID()));
//			rs = pst.executeQuery();
//
//			String strDocumentName = null;
//			String strDocumentContent = null;
//			while (rs.next()) {
//				strDocumentName = rs.getString("certificate_name");
//				strDocumentContent = rs.getString("certificate_desc");
//			}
//			
//			
//
//			
//			if(strDocumentName!=null){
//				strDocumentName = strDocumentName.replace(" ", "");
//			}
//			
//			
//			Notifications NF = new Notifications(0, CF);
//			Map<String, String> hmParsedContent = null;
//			
//			
//			NF.setStrEmpId(getEmpID());
//			NF.setSupervisor(true);
//			
//			/*pst = con.prepareStatement(selectEmpDetails1);
//			pst.setInt	(1, uF.parseToInt(getEmp_id()));
//			rs = pst.executeQuery();
//			
//			System.out.println("pst===>"+pst);
//			
//			
//			int nSupervisorId = 0;
//			while(rs.next()){
//				nSupervisorId = rs.getInt("supervisor_emp_id");
//				NF.setStrEmpCode(rs.getString("empcode"));
//				NF.setStrEmpEmail(rs.getString("emp_email"));
//				NF.setStrEmailTo(rs.getString("emp_email"));
//				NF.setStrEmpFname(rs.getString("emp_fname"));
//				NF.setStrEmpLname(rs.getString("emp_lname"));
//				NF.setStrEmpFullNamename(rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
//				NF.setStrEmpMobileNo(rs.getString("emp_contactno"));
//				NF.setStrAccountNo(rs.getString("emp_bank_acct_nbr"));
//				NF.setStrUserName(rs.getString("username"));
//				NF.setStrPassword(rs.getString("password"));
//			}
//			if(nSupervisorId>0 && NF.isSupervisor()){
//				pst = con.prepareStatement(selectEmpDetails1);
//				pst.setInt	(1, nSupervisorId);
//				rs = pst.executeQuery();
//				
//				while(rs.next()){
//					NF.setStrSupervisorEmail(rs.getString("emp_email"));					
//					NF.setStrSupervisorName(rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
//					NF.setStrSupervisorContactNo(rs.getString("emp_contactno"));
//				}
//			}
//			*/
//			
//			
//			Document document = new Document();
//			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//			
//			
//			String strDocName = null;
//			String strDocContent = null;
//			
//			StringBuilder sb = new StringBuilder();
//			
//			
//			if(strDocumentContent!=null){
//				
//		//		hmParsedContent  = NF.parseContent(strDocumentContent, "", "");
//				strDocName = strDocumentName;
//		//		strDocContent = hmParsedContent.get("MAIL_BODY");
//				
//				getContents();
//		
//				strDocContent= parseDocContent(con, strDocumentContent);
//				
//				NF.setStrDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF.getStrReportDateFormat()));
//				
//				
//				PdfWriter.getInstance(document, buffer);
//				document.open();
//				
//				
//				
//				
//			/*	
//				sb.append("<table width=\"100%\"><tr>" +
//						"<td align=\"left\"><img height=\"60\" src=\"http://"+CF.getStrEmailLocalHost()+":8080"+request.getContextPath()+"/userImages/"+CF.getStrOrgLogo()+"\"></td>" +
//						"<td align=\"center\">"+CF.getStrOrgAddress()+"</td>" +
//						"</tr></table>");
//				
//				List<Element> supList = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
//				Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
//				phrase.add(supList.get(0));
//				document.add(phrase);
//				
//				
//				supList = HTMLWorker.parseToList(new StringReader(hmParsedContent.get("MAIL_BODY")), null);
//				phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
//				phrase.add(supList.get(0));
//				document.add(phrase);*/
//				
//				document.close();
//				
//			}
//			
//			
//			
//			
//			
//			
///*			hmParsedContent  = NF.parseContent(getStrMailBody(), "", getStrSubject());
//			String strMailSubject = getStrSubject();
//			String strMailBody = hmParsedContent.get("MAIL_BODY");*/
//			
//			
//			byte[] bytes = buffer.toByteArray();
///*			NF.setStrEmailSubject(getStrSubject());
//			NF.setStrEmailBody(hmParsedContent.get("MAIL_BODY"));*/
//			
//			if(strDocumentContent!=null){
//				NF.setPdfData(bytes);
//				NF.setStrAttachmentFileName(strDocumentName+".pdf");
//			}
//			
//			
//		//	saveDocumentActivity(con, uF, CF, strDocName,strDocContent,"", "");
//			
//			
//			NF.sendNotifications();
//			
//			
//			  
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//		}
//		
//	}

	// getting Parsing Data ***********
	
//	private void getContents() {
//	
//
//		Connection con = null;
//		ResultSet rs = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//	db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//
//		try {
//
//		
//			
//			
//			
//	} catch (Exception e) {
//		e.printStackTrace();
//	} finally {
//		db.closeConnection(con);
//		db.closeResultSet(rs);
//		db.closeStatements(pst);
//	}
//	}


	// parsing function  ******************
	
//	private String parseDocContent(Connection con, String strDocumentContent) {
//		
///*		[EMP_CODE]<br />  [EMP_FNAME]<br />[ORG_NAME]<br /> [ORG_IMG]<br />
//		[DATE]<br /> [TRAINING_NAME]<br /> [DESIGNATION]<br /> [LEVEL]<br /> [GRADE]<br />
//		[AUTH_SIGN]<br /> <br />    */
//	   Map<String,String>  hmEmpCode=CF.getEmpCodeMap(con);
//	   Map<String,String>  hmEmpName=CF.getEmpNameMap(con, null, null);
//	   
//	   	strDocumentContent.replace(R_EMPCODE, hmEmpCode.get(getEmpID()));
//	 //  strDocumentContent.replace(R_EMPNAME, hmEmpName.get(getEmpID()));
//	   
//		
//		return strDocumentContent;
//	}


	
//	public void saveDocumentActivity(Connection con, UtilityFunctions uF,CommonFunctions CF, String strDocumentName, String strDocumentContent, String strMailSubject, String strMailBody){
//		
//		PreparedStatement pst = null;
//		
//		try {
//			
//		
//			pst = con.prepareStatement("insert into document_activities (document_name, document_content, effective_date, entry_date, user_id, emp_id, mail_subject, mail_body) values (?,?,?,?,?,?,?,?)");
//			pst.setString(1, strDocumentName);
//			pst.setString(2, strDocumentContent);
//			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.setInt(5, uF.parseToInt((String)session.getAttribute(EMPID)));
//			pst.setInt(6, uF.parseToInt(getEmpID()));
//			pst.setString(7, strMailSubject);
//			pst.setString(8, strMailBody);
//			pst.execute();
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	


	private void insertCertificate() {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;

		UtilityFunctions uF=new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);

			pst=con.prepareStatement("update training_learnings set is_certificate_given=?" +
					" where plan_id=? and emp_id=? ");
			pst.setInt(1,1);
			pst.setInt(2,uF.parseToInt(getPlanID()));
			pst.setInt(3,uF.parseToInt(getEmpID()));
			pst.execute();
			pst.close();
			
			request.setAttribute("current_date",uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT));
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}


	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}


private String empID;
private String planID;

public String getEmpID() {
	return empID;
}


public String getPlanID() {
	return planID;
}


public void setEmpID(String empID) {
	this.empID = empID;
}


public void setPlanID(String planID) {
	this.planID = planID;
}
	

String empCODE;
String empNAME;


}