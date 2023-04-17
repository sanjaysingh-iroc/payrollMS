


package com.konnect.jpms.recruitment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.document.HeaderFooterPageEvent;
import com.konnect.jpms.util.CandidateNotifications;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class CandiOfferLetterPreview implements ServletRequestAware,ServletResponseAware, IStatements {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String strSessionUserId;
	
	private String depart_id;
	private String candidateId;
	private String recruitId;
	private String operation;
	String candiApplicationId;
	
	public String execute() throws Exception {
//  System.out.println("CandiOfferLetterPreview Calling....");
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		
		strSessionUserId = (String)session.getAttribute(EMPID);
		
		downloadDocument();
		
		if(getOperation()!=null && getOperation().equals("preview")){
			request.setAttribute(PAGE, "/jsp/recruitment/ViewCandiOfferLetter.jsp");
			request.setAttribute(TITLE, "Candidate Offer Letter"); 
			return "load";
		} else{
			return null;
		}
		
	}

	public String  loadDownloadDocument(){
		return "load";
	}
	
	public void downloadDocument() {

		Connection con = null;
		ResultSet rst = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmCandiInfo = CF.getCandiInfoMap(con, false);

			CandidateMyProfilePopup candidateMyProfilePopup = new CandidateMyProfilePopup();
			candidateMyProfilePopup.request = request;
			candidateMyProfilePopup.session = session;
			candidateMyProfilePopup.CF = CF;
			candidateMyProfilePopup.setCandID(getCandidateId());
			candidateMyProfilePopup.setRecruitId(getRecruitId());
			
			
//			System.out.println("hmCandiSalDetails ===>> " + hmCandiSalDetails);
//			StringBuilder sbCandiSalTable = candidateMyProfilePopup.getCandiSalaryDetails(con);
//			if(sbCandiSalTable == null) sbCandiSalTable = new StringBuilder();
//			System.out.println("sbCandiSalTable ===>> " + sbCandiSalTable.toString());
			
			
			
			Map<String, String> hmCandiInner = hmCandiInfo.get(getCandidateId());
			
	//===start parvez date: 11-01-2022===	
			
			boolean isJoiningBonus = CF.getFeatureManagementStatus(request, uF, F_ENABLE_JOINING_BONUS_DETAILS);
			
//			pst = con.prepareStatement("select e.candi_application_deatils_id,e.candidate_id,r.org_id,d.designation_code,d.designation_name from recruitment_details r, designation_details d, " +
//				"candidate_application_details e where r.recruitment_id = e.recruitment_id and r.designation_id=d.designation_id and candidate_id=?");
			if(isJoiningBonus){
				pst = con.prepareStatement("select e.candi_application_deatils_id,e.candidate_id,e.joining_bonus_amount_details,r.org_id,d.designation_code,d.designation_name from recruitment_details r, designation_details d, " +
							"candidate_application_details e where r.recruitment_id = e.recruitment_id and r.designation_id=d.designation_id and candidate_id=?");
			} else{
				pst = con.prepareStatement("select e.candi_application_deatils_id,e.candidate_id,r.org_id,d.designation_code,d.designation_name from recruitment_details r, designation_details d, " +
				"candidate_application_details e where r.recruitment_id = e.recruitment_id and r.designation_id=d.designation_id and candidate_id=?");
			}
	//===end parvez date: 11-01-2022===	
			pst.setInt(1, uF.parseToInt(getCandidateId()));
//			System.out.println("pst1 ===> " + pst);
			rst = pst.executeQuery();			
			Map<String, String> hmCandiDesig = new HashMap<String, String>();
			Map<String, String> hmCandiOrg = new HashMap<String, String>();
			Map<String, String> hmCandiJoiningBonusAmountD = new HashMap<String, String>();	//Created By Parvez Date : 11-01-2022
			while(rst.next()){				
				hmCandiDesig.put(rst.getString("candidate_id"), rst.getString("designation_name"));
				hmCandiOrg.put(rst.getString("candidate_id"), rst.getString("org_id"));
				setCandiApplicationId(rst.getString("candi_application_deatils_id"));
		//===start parvez date: 11-01-2022===		
				if(isJoiningBonus){
					hmCandiJoiningBonusAmountD.put(rst.getString("candidate_id"), rst.getString("joining_bonus_amount_details"));
				}
		//===end parvez date: 11-01-2022===
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select * from document_signature where org_id =?");
			pst.setInt(1, uF.parseToInt(hmCandiOrg.get(getCandidateId())));
			rst = pst.executeQuery();
			String strAuthSign = null;
			String strHrSign = null;
			String strRecruiterSign = null;
			while (rst.next()) {
				if(rst.getInt("signature_type") == 1) {
					strAuthSign = rst.getString("signature_image");
				}
				if(rst.getInt("signature_type") == 2) {
					strHrSign = rst.getString("signature_image");
				}
				if(rst.getInt("signature_type") == 3) {
					if(rst.getInt("user_id") == uF.parseToInt(strSessionUserId)) {
						strRecruiterSign = rst.getString("signature_image");
					}
				}
			}
			rst.close();
			pst.close();
			
			Map<String, Map<String, String>> hmHeader=new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmFooter=new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select * from document_collateral");
//			System.out.println("pst2 ===> " + pst);
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
//			System.out.println("pst3 ===> " + pst);
			rst = pst.executeQuery();
			
			String strDocumentName = null;
			String strDocumentContent = null;
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
			nF.setStrRecruitmentId(getRecruitId());
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
//			nF.setStrContextPath(request.getServletPath());
			nF.setStrContextPath(request.getContextPath());
			nF.setStrEmpId(getCandidateId());
			 
			nF.setStrCandiFname(hmCandiInner.get("FNAME"));
			nF.setStrCandiLname(hmCandiInner.get("LNAME"));
			nF.setStrCandiEmail(hmCandiInner.get("EMAIL"));
			nF.setStrCandiMobileNo(hmCandiInner.get("CONTACT_NO"));
			nF.setStrCandiCTC(hmCandiInner.get("OFFERED_CTC"));
			nF.setStrCandiAnnualCTC(hmCandiInner.get("OFFERED_ANNUAL_CTC"));
			nF.setStrCandiJoiningDate(hmCandiInner.get("JOINING_DATE"));
			nF.setStrOfferAcceptanceLastDate(hmCandiInner.get("OFFER_ACCEPTANCE_LAST_DATE"));
			nF.setStrRecruitmentDesignation(hmCandiDesig.get(getCandidateId()));
			nF.setStrCandidateId(hmCandiInner.get("CANDI_ID"));//Created By Dattatray Date : 05-10-21
//			System.out.println("OFFERED_SALARY_STRUCTURE ===>> " + hmCandiSalDetails.get("OFFERED_SALARY_STRUCTURE"));
			//Started by Dattatray Date:29-09-21
			String name = uF.showData(nF.getStrCandiSalutation(), "")+" "+uF.showData(nF.getStrCandiFname(),"")+" "+uF.showData(nF.getStrCandiLname(),"");
			Map<String, String> hmCandiSalDetails = candidateMyProfilePopup.getCandiSalaryDetails(con, name, nF.getStrRecruitmentDesignation(), nF.getStrRecruitmentWLocation(), nF.getStrCandiJoiningDate(), nF.getStrRecruitmentLevel(), nF.getStrRecruitmentGrade(),nF.getStrLegalEntityName());
			nF.setStrOfferedSalaryStructure(hmCandiSalDetails.get("OFFERED_SALARY_STRUCTURE"));
			//Ended by Dattatray Date:29-09-21
			
			nF.setOfferAcceptData("?candidateID="+getCandidateId()+"&recruitID="+getRecruitId()+"&candiOfferAccept=yes&updateRemark=Update");
			
	//===start parvez date: 11-01-2022===
			if(hmCandiJoiningBonusAmountD.get(getCandidateId()) != null){
				String[] strJoiningbonusAmountDetails = hmCandiJoiningBonusAmountD.get(getCandidateId()).split("::");
				nF.setStrJoiningBonusAmount(strJoiningbonusAmountDetails[1].trim());
				nF.setStrAdditionalComment(strJoiningbonusAmountDetails[2].trim());
			}
			
	//===end parvez date: 11-01-2022===
			
			Map<String, String> hmParsedContent = null;

//			Document document = new Document(PageSize.A4);
			Document document = new Document(PageSize.A4, 40, 40, 10, 60); 
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
				if(nF.getStrCandiAddress() ==null || nF.getStrCandiAddress().trim().isEmpty()) {
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
				

				if(headerPath != null && !headerPath.equals("")) {
					sbHeader.append("<table style=\"width: 100%;\"><tr>");
					sbHeader.append("<td>");
					if(strHeader!=null && !strHeader.equals("")) {
						sbHeader.append("<img src=\""+headerPath+"\">");
					}
					sbHeader.append("</td>");	
					sbHeader.append("</tr></table>");
				}
				
				PdfWriter writer = PdfWriter.getInstance(document, buffer);
				HeaderFooterPageEvent event = new HeaderFooterPageEvent(sbHeader.toString(),strFooterCollateralText);
			    writer.setPageEvent(event);
				document.open();
//				System.out.println("strDocument ====> " +strDocument);
				HTMLWorker hw = new HTMLWorker(document);
//				hw.parse(new StringReader(sbHeader.toString()));
				if(strDocument.contains(HR_SIGNATURE)) {
			//===start parvez date: 11-01-2022===		
					if (strHrSign !=null) {
						String imageUrl=CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+hmCandiOrg.get(getCandidateId())+"/"+I_DOC_SIGN+"/"+strHrSign;
						String 	strSignature ="<img src=\""+imageUrl+"\">";
						strDocument = strDocument.replace(HR_SIGNATURE, strSignature);
					}
			//===end parvez date: 11-01-20222===		
					
				}
			
				if(strDocument.contains(AUTHORITY_SIGNATURE)) {
				    String imageUrl=CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+hmCandiOrg.get(getCandidateId())+"/"+I_DOC_SIGN+"/"+strAuthSign;
					String 	strSignature ="<img src=\""+imageUrl+"\">";
					strDocument = strDocument.replace(AUTHORITY_SIGNATURE, strSignature);
				}
				
//				System.out.println("strDocument ===>> " + strDocument);
				if(strDocument.contains(RECRUITER_SIGNATURE)) {
				    String imageUrl=CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+hmCandiOrg.get(getCandidateId())+"/"+I_DOC_SIGN+"/"+strSessionUserId+"/"+strRecruiterSign;
					String 	strSignature ="<img src=\""+imageUrl+"\">";
//					System.out.println("strSignature ===>> " + strSignature);
					strDocument = strDocument.replace(RECRUITER_SIGNATURE, strSignature);
				}
				
				hw.parse(new StringReader(strDocument));
//				hw.parse(new StringReader(sbFooter.toString()));
				document.close();  
			}
		
			byte[] bytes = buffer.toByteArray();			
//			System.out.println("strDocumentName==>"+strDocumentName+"\n=>bytes==>"+bytes);
			if(strDocumentName != null) {
				
				if(getOperation()!=null && getOperation().equalsIgnoreCase("preview")){
					String directory = CF.getStrDocSaveLocation()+I_TEMP+"/"; 
					FileUtils.forceMkdir(new File(directory));
					File f = File.createTempFile("tmp", ".pdf", new File(directory));
					FileOutputStream fileOuputStream = new FileOutputStream(f); 
					fileOuputStream.write(bytes);
					
					String filePath = CF.getStrDocRetriveLocation()+I_TEMP+"/"+f.getName();
					//System.out.println("COLPfilePath==>"+filePath);
					request.setAttribute("filePath",filePath);
				}else{
					
					response.setContentType("application/pdf");
					response.setContentLength(buffer.size());
					response.setHeader("Content-Disposition", "attachment; filename=" + strDocumentName + ".pdf");
					ServletOutputStream out = response.getOutputStream();
					buffer.writeTo(out);
					out.flush(); 
					buffer.close();
					out.close();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public Map<String, Map<String, String>> getCandiInfoMap(Connection con, boolean isFamilyInfo) {
		Map<String, Map<String, String>> hmCandiInfo = new HashMap<String, Map<String, String>>();
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmCandiInner = new HashMap<String, String>();
			if(isFamilyInfo) {
				pst = con.prepareStatement("select * from candidate_family_members order by emp_id");
				rs = pst.executeQuery();
				while(rs.next()) {
					hmCandiInner = hmCandiInfo.get(rs.getString("emp_id"));
					if(hmCandiInner==null) hmCandiInner=new HashMap<String, String>();
					
					hmCandiInner.put(rs.getString("member_type"), rs.getString("member_name"));
					hmCandiInfo.put(rs.getString("emp_id"), hmCandiInner);
				}
				rs.close();
				pst.close();
			}
			
			pst = con.prepareStatement("SELECT cpd.emp_per_id, cpd.emp_fname,cpd.emp_mname,cpd.emp_lname,cpd.emp_address1,cpd.emp_address2,cpd.empcode," +
				"cpd.emp_image,cpd.emp_email,cpd.emp_date_of_birth,cad.candidate_joining_date,cpd.emp_gender,cpd.marital_status,cad.ctc_offered,cad.annual_ctc_offered," +
				"cpd.emp_contactno FROM candidate_personal_details cpd, candidate_application_details cad where cpd.emp_per_id = cad.candidate_id order by emp_per_id");
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
			
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmCandiInner.put("FULLNAME", rs.getString("emp_lname")+strEmpMName+" "+rs.getString("emp_lname"));
				hmCandiInner.put("EMPCODE", rs.getString("empcode"));
				hmCandiInner.put("IMAGE", rs.getString("emp_image"));
				hmCandiInner.put("EMAIL", rs.getString("emp_email"));
				hmCandiInner.put("CONTACT_NO", rs.getString("emp_contactno"));
				hmCandiInner.put("DOB", rs.getString("emp_date_of_birth"));
				if(rs.getString("candidate_joining_date") != null) {
					hmCandiInner.put("JOINING_DATE", uF.getDateFormat(rs.getString("candidate_joining_date"), DBDATE, CF.getStrReportDateFormat()));
					hmCandiInner.put("OFFER_ACCEPTANCE_LAST_DATE", uF.getDateFormat(uF.getPrevDate(uF.getDateFormatUtil(rs.getString("candidate_joining_date"), DBDATE), 5)+"", DBDATE, CF.getStrReportDateFormat()));
				} else {
					hmCandiInner.put("JOINING_DATE", "-");
					hmCandiInner.put("OFFER_ACCEPTANCE_LAST_DATE", "-");
				}
				hmCandiInner.put("GENDER", rs.getString("emp_gender"));
				hmCandiInner.put("MARITAL_STATUS", rs.getString("marital_status"));
				
//				System.out.println("CTCOffered==>"+rs.getString("ctc_offered"));
				
				hmCandiInner.put("OFFERED_CTC", rs.getString("ctc_offered"));
				hmCandiInner.put("OFFERED_ANNUAL_CTC", rs.getString("annual_ctc_offered"));
				
				StringBuilder address = new StringBuilder();
				if(rs.getString("emp_address1") != null && !rs.getString("emp_address1").equals("")) {
					address.append(rs.getString("emp_address1")+",");
				}
				
				/*if(rs.getString("emp_address2") != null && !rs.getString("emp_address2").equals("")) {
					address.append(rs.getString("emp_address1")+",");
				}*/
				
//				System.out.println("address==>"+address.toString());
				hmCandiInner.put("ADDRESS",address.toString());
				
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
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
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

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getCandiApplicationId() {
		return candiApplicationId;
	}

	public void setCandiApplicationId(String candiApplicationId) {
		this.candiApplicationId = candiApplicationId;
	}

	
}