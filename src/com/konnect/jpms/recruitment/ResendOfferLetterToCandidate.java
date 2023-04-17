package com.konnect.jpms.recruitment;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.document.HeaderFooterPageEvent;
import com.konnect.jpms.util.CandidateNotifications;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ResendOfferLetterToCandidate extends ActionSupport implements ServletRequestAware,IConstants {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;

	String strSessionEmpId = null;

	String depart_id;
	String candidateId;
	String recruitId;
	String candiApplicationId;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(TITLE, "Resend Onboarding Form To Candidate");
		request.setAttribute(PAGE, "/jsp/recruitment/ResendOnboardingFormToCandidate.jsp");
		
		sendMail();
		
		return SUCCESS;

	}
	
	
	public void sendMail() {

		Connection con = null;
		ResultSet rst = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmCandiInfo = getCandiInfoMap(con, false);

			CandidateMyProfilePopup candidateMyProfilePopup = new CandidateMyProfilePopup();
			candidateMyProfilePopup.request = request;
			candidateMyProfilePopup.session = session;
			candidateMyProfilePopup.CF = CF;
			candidateMyProfilePopup.setCandID(getCandidateId());
			candidateMyProfilePopup.setRecruitId(getRecruitId());
			
//			StringBuilder sbCandiSalTable = candidateMyProfilePopup.getCandiSalaryDetails(con);
//			if(sbCandiSalTable == null) sbCandiSalTable = new StringBuilder();
//			System.out.println("sbCandiSalTable=====>"+sbCandiSalTable.toString());
			
			Map<String, String> hmCandiInner = hmCandiInfo.get(getCandidateId());
			
			pst = con.prepareStatement("select e.candi_application_deatils_id,e.candidate_id,r.org_id,d.designation_code,d.designation_name from recruitment_details r, designation_details d, " +
					"candidate_application_details e where r.recruitment_id = e.recruitment_id and r.designation_id=d.designation_id and candidate_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, String> hmCandiDesig = new HashMap<String, String>();
			Map<String, String> hmCandiOrg = new HashMap<String, String>();
			while(rst.next()){				
				hmCandiDesig.put(rst.getString("candidate_id"), rst.getString("designation_name"));
				hmCandiOrg.put(rst.getString("candidate_id"), rst.getString("org_id"));
				setCandiApplicationId(rst.getString("candi_application_deatils_id"));
				
			}
			rst.close();
			pst.close();
			
			Map<String, Map<String, String>> hmHeader=new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmFooter=new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select * from document_collateral");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				if(rst.getString("_type").equals("H")){
					Map<String, String> hmInner=new HashMap<String, String>();
					hmInner.put("COLLATERAL_ID", rst.getString("collateral_id"));
					hmInner.put("COLLATERAL_PATH", rst.getString("collateral_image"));
					hmInner.put("COLLATERAL_IMG_ALIGN", rst.getString("image_align"));
					hmInner.put("COLLATERAL_TEXT", uF.showData(rst.getString("collateral_text"),""));
					
					hmHeader.put(rst.getString("collateral_id"), hmInner);
				}else{
					Map<String, String> hmInner=new HashMap<String, String>();
					hmInner.put("COLLATERAL_ID", rst.getString("collateral_id"));
					hmInner.put("COLLATERAL_PATH", rst.getString("collateral_image"));
					hmInner.put("COLLATERAL_IMG_ALIGN", rst.getString("image_align"));
					hmInner.put("COLLATERAL_TEXT", uF.showData(rst.getString("collateral_text"),""));
					
					hmFooter.put(rst.getString("collateral_id"), hmInner);
				}
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select * from document_comm_details where trigger_nodes like '%,"+NODE_CANDIDATE_OFFER_ID+",%' and status=1 and org_id=? order by document_id desc limit 1");
			pst.setInt(1, uF.parseToInt(hmCandiOrg.get(getCandidateId())));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String strDocumentName = null;
			String strDocumentContent = null;
			String strDocumentHeader = null;
			String strDocumentFooter = null;
			String strHeader = null;
			String strFooter = null;
			String strHeaderImageAlign="";
			String strHeaderCollateralText="";
			String strHeaderTextAlign="";
			String strFooterImageAlign="";
			String strFooterCollateralText="";
			String strFooterTextAlign="";
			
			while (rst.next()) {  
				strDocumentName = rst.getString("document_name");
				strDocumentContent = rst.getString("document_text");
				
				if(rst.getString("collateral_header")!=null && !rst.getString("collateral_header").equals("") && hmHeader.get(rst.getString("collateral_header"))!=null){
					Map<String, String> hmInner=hmHeader.get(rst.getString("collateral_header"));
					strHeader = uF.showData(hmInner.get("COLLATERAL_PATH"),"");
					strHeaderImageAlign=uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"),"");
					strHeaderCollateralText=uF.showData(hmInner.get("COLLATERAL_TEXT"),"");
					strHeaderTextAlign=uF.showData(hmInner.get("COLLATERAL_TEXT_ALIGN"),"");
				}
				if(rst.getString("collateral_footer")!=null && !rst.getString("collateral_footer").equals("") && hmFooter.get(rst.getString("collateral_footer"))!=null){
					Map<String, String> hmInner=hmFooter.get(rst.getString("collateral_footer"));
					strFooter = uF.showData(hmInner.get("COLLATERAL_PATH"),"");
					strFooterImageAlign=uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"),"");
					strFooterCollateralText=uF.showData(hmInner.get("COLLATERAL_TEXT"),"");
					strFooterTextAlign=uF.showData(hmInner.get("COLLATERAL_TEXT_ALIGN"),"");
				}
			}
			rst.close();
			pst.close();
			
			if(strDocumentName!=null){
				strDocumentName = strDocumentName.replace(" ", "");
			}
			
			String strDomain = request.getServerName().split("\\.")[0];
			
			CandidateNotifications nF = new CandidateNotifications(N_CANDI_JOINING_OFFER_CTC, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.CF = CF;
			nF.setStrEmpId(getCandiApplicationId());
			nF.setStrRecruitmentId(getRecruitId());
//			System.out.println("getRecruitId() ================ -- ===>> " + getRecruitId());
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
//			nF.setStrContextPath(request.getServletPath());
			nF.setStrContextPath(request.getContextPath());
			 
			nF.setStrCandiFname(hmCandiInner.get("FNAME"));
			nF.setStrCandiLname(hmCandiInner.get("LNAME"));
			nF.setStrCandiCTC(hmCandiInner.get("OFFERED_CTC"));
			nF.setStrCandiAnnualCTC(hmCandiInner.get("OFFERED_ANNUAL_CTC"));
			nF.setStrCandiJoiningDate(hmCandiInner.get("JOINING_DATE"));
			nF.setStrRecruitmentDesignation(hmCandiDesig.get(getCandidateId()));
			nF.setStrCandidateId(hmCandiInner.get("CANDI_ID"));//Created By Dattatray Date : 05-10-21
			//Started by Dattatray Date:29-09-21
			String name = uF.showData(nF.getStrCandiSalutation(), "")+" "+uF.showData(nF.getStrCandiFname(),"")+" "+uF.showData(nF.getStrCandiLname(),"");
			Map<String, String> hmCandiSalDetails = candidateMyProfilePopup.getCandiSalaryDetails(con, name, nF.getStrRecruitmentDesignation(), nF.getStrRecruitmentWLocation(), nF.getStrCandiJoiningDate(), nF.getStrRecruitmentLevel(), nF.getStrRecruitmentGrade(),nF.getStrLegalEntityName());;
			nF.setStrOfferedSalaryStructure(hmCandiSalDetails.get("OFFERED_SALARY_STRUCTURE"));
			//Ended by Dattatray Date:29-09-21
			
			nF.setOfferAcceptData("?candidateID="+getCandidateId()+"&recruitID="+getRecruitId()+"&candiOfferAccept=yes&updateRemark=Update");
			
			Map<String, String> hmParsedContent = null;

//			Document document = new Document(PageSize.A4);
			Document document = new Document(PageSize.A4,40, 40, 10, 60); 
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			StringBuilder sbHeader = new StringBuilder();
			StringBuilder sbFooter = new StringBuilder();
			String strDocName = null;
			String strDocContent = null;
			if(strDocumentContent!=null){ 
				
//				hmParsedContent  = nF.parseContent(strDocumentContent, "", "");
				hmParsedContent  = nF.parseContent(strDocumentContent, "", "");
				
				
				strDocName = strDocumentName;
				strDocContent = hmParsedContent.get("MAIL_BODY");
				String strDocument = hmParsedContent.get("MAIL_BODY");
				//Start By Dattatray Date:07-10-21
				String brTag="";
				if(nF.getStrCandiAddress().trim().isEmpty()) {
					brTag = "<br/><br/><br/>";
				}
				if (nF.getIntAddressLineCnt() == 1) {
					brTag = "<br/><br/>";
				}else if (nF.getIntAddressLineCnt() == 2) {
					brTag = "<br/>";
				}
				//Ended By Dattatray Date:07-10-21
				if(strDocument!=null) {
//					strDocument = strDocument.replaceAll("<br/>", "");
					
					//Satrt Dattatray Date : 31-07-21  
					if (strDocument.contains("<pre style=\"text-align:justify\">") || strDocument.contains("<pre style=\"text-align:justify;\">") || strDocument.contains("<pre style=\"text-align: justify;\">") || strDocument.contains("<pre style=\"text-align: justify\">")) {
//						System.out.println("if");
						if (strDocument.contains("<pre ")) {
							strDocument = strDocument.replaceAll("<pre ", "<p ");
						}
						 if(strDocument.contains("<pre>") ){
							 strDocument = strDocument.replaceAll("<pre>", "<p>");
						 }
						//
						/*if (strDocument.contains("><span")) {
							strDocument = strDocument.replaceAll("><span ", "><p style=\"text-align: justify\"><span ");
						}
						if (strDocument.contains("</span>")) {
							strDocument = strDocument.replaceAll("</span>", "</span></p>");
						}*/

						if (strDocument.contains("</pre>")) {
							strDocument = strDocument.replaceAll("</pre>", "</p>");
						}
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p>", "<br/><p style=\"text-align: justify\">", true, true, "<p>");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<li>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
//						strDocument = replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>", "<p style=\"text-align: justify\">", true, true, "<p style=\"text-align: justify\">");
//						strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
//						strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</p>", true, true, "<p>");
//						strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>"); 
						//Created By Dattatray Date:07-10-21
						if(strDocument.contains("Director of People")) {
							strDocument = uF.replaceBetweenTwoString(strDocument, "Director of ", "People", true, true,"Director of People"+brTag);//Created By Dattatray Date:05-10-21
						}
					}else {
						System.out.println("Else");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<li>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<p>", true, true, "<p>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", " </li>	", true, true, "</li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<pre>", true, true, "<pre>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<pre>", "<br/>", true, true, "<pre>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
						//Created By Dattatray Date:07-10-21
						if(strDocument.contains("Director of People")) {
							strDocument = uF.replaceBetweenTwoString(strDocument, "Director of ", "People", true, true,"Director of People"+brTag);//Created By Dattatray Date:05-10-21
						}
					}
					//End Dattatray Date : 31-07-21 
				}
				
				String headerPath="";
				if(strHeader!=null && !strHeader.equals("")){
//					headerPath=CF.getStrDocRetriveLocation()+strHeader;
					if(CF.getStrDocRetriveLocation()==null) { 
						headerPath =  DOCUMENT_LOCATION + strHeader;
					} else { 
						headerPath = CF.getStrDocSaveLocation() +I_COLLATERAL+"/"+I_IMAGE+"/"+strHeader;
					}
				}
				
				
//				if(headerPath != null && !headerPath.equals("")) {
//					//sbHeader.append("<table><tr><td><img height=\"60\" src=\""+strDocumentHeader+"\"></td></tr></table>");
//					sbHeader.append("<table style=\"width: 100%;\"><tr>");
//					if(strHeaderImageAlign!=null && strHeaderImageAlign.equals("R")) { 
//						sbHeader.append("<td width=\"70%\" valign=\"middle\" style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>" +
//								"<td align=\"right\">");
//						if(headerPath != null && !headerPath.equals("")) {
//							sbHeader.append("<img height=\"30\" src=\""+headerPath+"\">");
//						}
//						sbHeader.append("</td>");						
//					
//					} else if(strHeaderImageAlign !=null && strHeaderImageAlign.equals("C")) { 
//						sbHeader.append("<td colspan=\"2\" align=\"Center\">");
//						if(headerPath != null && !headerPath.equals("")) {
//							sbHeader.append("<img height=\"30\" src=\""+headerPath+"\"><br/>");
//						}
//						sbHeader.append(""+strHeaderCollateralText+"</td>");
//					} else {
//						sbHeader.append("<td>");
//						if(headerPath != null && !headerPath.equals("")) {
//							sbHeader.append("<img height=\"30\" src=\""+headerPath+"\">");
//						}
//						sbHeader.append("</td> <td valign=\"middle\" style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>");
//					}
//					sbHeader.append("</tr></table>");
//					
//				} else {
//					
//					sbHeader.append("<table style=\"width: 100%;\"><tr>");
//					if(strHeaderTextAlign!=null && strHeaderTextAlign.equals("R")) { 
//						sbHeader.append("<td colspan=\"2\" align=\"right\" valign=\"middle\" style=\"padding-right: 50px;\">"+strHeaderCollateralText+"</td>");
//						
//					} else if(strHeaderTextAlign!=null && strHeaderTextAlign.equals("C")) { 
//						sbHeader.append("<td colspan=\"2\" align=\"center\" valign=\"middle\">"+strHeaderCollateralText+"</td>");
//					} else { 
//						sbHeader.append("<td colspan=\"2\" valign=\"middle\" style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>");
//					}
//					sbHeader.append("</tr></table>");
//				
//				}
				
				if(headerPath != null && !headerPath.equals("")) {
					sbHeader.append("<table style=\"width: 100%;\"><tr>");
					sbHeader.append("<td>");
					if(strHeader!=null && !strHeader.equals("")) {
						sbHeader.append("<img src=\""+headerPath+"\">");
					}
					sbHeader.append("</td>");	
					sbHeader.append("</tr></table>");
				}
				
				
//				String footerPath="";   
//				if(strFooter!=null && !strFooter.equals("")){
////					footerPath=CF.getStrDocRetriveLocation()+strFooter;
//					if(CF.getStrDocRetriveLocation()==null) { 
//						footerPath =  DOCUMENT_LOCATION + strFooter;
//					} else { 
//						footerPath = CF.getStrDocRetriveLocation() +I_COLLATERAL+"/"+I_IMAGE+"/"+strFooter;
//					}
//				}
//				
//				
//				if(footerPath != null && !footerPath.equals("")) {
//					//sbFooter.append("<table><tr><td><img height=\"60\" src=\""+strDocumentFooter+"\"></td></tr></table>");
//					
//					sbFooter.append("<table><tr>");
//					if(strFooterImageAlign!=null && strFooterImageAlign.equals("R")) { 
//						sbFooter.append("<td width=\"70%\" valign=\"middle\" style=\"padding-left: 50px;\">"+strFooterCollateralText+"</td> <td align=\"right\">");
//						if(footerPath != null && !footerPath.equals("")) {
//							sbFooter.append("<img height=\"60\" src=\""+footerPath+"\">");
//						}
//						sbFooter.append("</td>");						
//					
//					} else if(strFooterImageAlign!=null && strFooterImageAlign.equals("C")) { 
//						sbFooter.append("<td align=\"Center\">");
//						if(footerPath != null && !footerPath.equals("")) {
//							sbFooter.append("<img height=\"60\" src=\""+footerPath+"\"><br/>");
//						}
//						sbFooter.append(""+strFooterCollateralText+"</td>");
//					} else { 
//						sbFooter.append("<td>");
//						if(footerPath != null && !footerPath.equals("")) {
//							sbFooter.append("<img height=\"60\" src=\""+footerPath+"\">");
//						}
//						sbFooter.append("</td> <td valign=\"middle\" style=\"padding-left: 50px;\">"+strFooterCollateralText+"</td>");
//					}
//					sbFooter.append("</tr></table>");
//				} else {
//
//					sbFooter.append("<table><tr>");
//					if(strFooterTextAlign!=null && strFooterTextAlign.equals("R")) { 
//						sbFooter.append("<td colspan=\"2\" align=\"right\" valign=\"middle\" style=\"padding-right: 50px;\">"+strFooterCollateralText+"</td>");
//					
//					} else if(strFooterTextAlign!=null && strFooterTextAlign.equals("C")) { 
//						sbFooter.append("<td colspan=\"2\" align=\"center\" valign=\"middle\">"+strFooterCollateralText+"</td>");
//					} else { 
//						sbFooter.append("<td colspan=\"2\" valign=\"middle\" style=\"padding-right: 50px;\">"+strFooterCollateralText+"</td>");
//					}
//					sbFooter.append("</tr></table>");
//				
//				}
				
				PdfWriter writer = PdfWriter.getInstance(document, buffer);
				HeaderFooterPageEvent event = new HeaderFooterPageEvent(sbHeader.toString(),strFooterCollateralText);
			    writer.setPageEvent(event);
				document.open();
//				System.out.println("strDocument ====> " +strDocument);
				HTMLWorker hw = new HTMLWorker(document);
//				hw.parse(new StringReader(sbHeader.toString())); 
				hw.parse(new StringReader(strDocument));
//				hw.parse(new StringReader(sbFooter.toString()));
				document.close();  
			
			}
		
			byte[] bytes = buffer.toByteArray();			
			/*System.out.println("bytes==>"+bytes.length);
			System.out.println("strDocumemntName==>"+strDocumentName);*/
			if(strDocumentContent!=null) {
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName(strDocumentName+".pdf");
			}
			
			String strMailSubject = nF.getStrEmailSubject();
			String strMailBody = nF.getStrNewEmailBody();
			
			nF.sendNotifications();
	
			saveDocumentActivity(con, uF, CF, strDocName, sbHeader.toString(), strDocContent, strFooterCollateralText, strMailSubject, strMailBody);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void saveDocumentActivity(Connection con, UtilityFunctions uF,CommonFunctions CF, String strDocumentName, String strDocumentHeader, String strDocumentContent, String strDocumentFooter, String strMailSubject, String strMailBody){
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("insert into document_activities (document_name, document_content, effective_date, entry_date, user_id, candi_id, mail_subject, mail_body, document_header, document_footer) values (?,?,?,?,?,?,?,?,?,?)");
			pst.setString(1, strDocumentName);
			pst.setString(2, strDocumentContent);
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(5, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(6, uF.parseToInt(getCandidateId()));
			pst.setString(7, strMailSubject);
			pst.setString(8, strMailBody);
			pst.setString(9, strDocumentHeader);
			pst.setString(10, strDocumentFooter);
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
	}


	public Map<String, Map<String, String>> getCandiInfoMap(Connection con, boolean isFamilyInfo) {
		Map<String, Map<String, String>> hmCandiInfo = new HashMap<String, Map<String, String>>();
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			Map<String, String> hmCandiInner = new HashMap<String, String>();
			if(isFamilyInfo) {
				pst = con.prepareStatement("select * from candidate_family_members order by emp_id");
				rs = pst.executeQuery();
				while(rs.next()) {
					hmCandiInner = hmCandiInfo.get(rs.getString("emp_id"));
					if(hmCandiInner==null)hmCandiInner=new HashMap<String, String>();
					
					hmCandiInner.put(rs.getString("member_type"), rs.getString("member_name"));
					hmCandiInfo.put(rs.getString("emp_id"), hmCandiInner);
				}
				rs.close();
				pst.close();
			}
			
			pst = con.prepareStatement("SELECT cpd.emp_per_id, cpd.emp_fname, cpd.emp_lname, cpd.empcode, cpd.emp_image, cpd.emp_email, " +
					"cpd.emp_date_of_birth, cad.candidate_joining_date, cpd.emp_gender, cpd.marital_status, cad.ctc_offered,cad.annual_ctc_offered FROM " +
					"candidate_personal_details cpd, candidate_application_details cad where cpd.emp_per_id = cad.candidate_id order by emp_per_id");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				if (rs.getInt("emp_per_id") < 0) {
					continue;
				}
				hmCandiInner = hmCandiInfo.get(rs.getString("emp_per_id"));
				if(hmCandiInner==null)hmCandiInner=new HashMap<String, String>();

				hmCandiInner.put("FNAME", rs.getString("emp_fname"));
				hmCandiInner.put("LNAME", rs.getString("emp_lname"));
				hmCandiInner.put("FULLNAME", rs.getString("emp_lname")+" "+rs.getString("emp_lname"));
				hmCandiInner.put("EMPCODE", rs.getString("empcode"));
				hmCandiInner.put("IMAGE", rs.getString("emp_image"));
				hmCandiInner.put("EMAIL", rs.getString("emp_email"));
				hmCandiInner.put("DOB", rs.getString("emp_date_of_birth"));
				if(rs.getString("candidate_joining_date") != null) {
				hmCandiInner.put("JOINING_DATE", uF.getDateFormat(rs.getString("candidate_joining_date"), DBDATE, CF.getStrReportDateFormat()));
				} else {
					hmCandiInner.put("JOINING_DATE", "-");
				}
				hmCandiInner.put("GENDER", rs.getString("emp_gender"));
				hmCandiInner.put("MARITAL_STATUS", rs.getString("marital_status"));
				hmCandiInner.put("OFFERED_CTC", rs.getString("ctc_offered"));
				hmCandiInner.put("OFFERED_ANNUAL_CTC", rs.getString("annual_ctc_offered"));
				
				hmCandiInfo.put(rs.getString("emp_per_id"), hmCandiInner);
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
		return hmCandiInfo;
	}
	
	public String getDepart_id() {
		return depart_id;
	}

	public void setDepart_id(String depart_id) {
		this.depart_id = depart_id;
	}

	public String getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
	}

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}


	public String getCandiApplicationId() {
		return candiApplicationId;
	}


	public void setCandiApplicationId(String candiApplicationId) {
		this.candiApplicationId = candiApplicationId;
	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}