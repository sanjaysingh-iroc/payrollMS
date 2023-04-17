package com.konnect.jpms.document;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;

public class DocumentPreview implements ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String strSessionUserId;

	public void execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		// if(CF==null)return "login";
		strSessionUserId = (String) session.getAttribute(EMPID);

		downloadDocument();

		loadDownloadDocument();
		// return "load";
	}

	String doc_id;

	public String loadDownloadDocument() {
		return "load";
	}

	public String downloadDocument() {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);

			Map<String, Map<String, String>> hmHeader = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmFooter = new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select * from document_collateral");
			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getString("_type").equals("H")) {
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("COLLATERAL_ID", rs.getString("collateral_id"));
					hmInner.put("COLLATERAL_PATH", rs.getString("collateral_image"));
					hmInner.put("COLLATERAL_IMG_ALIGN", rs.getString("image_align"));
					hmInner.put("COLLATERAL_TEXT", uF.showData(rs.getString("collateral_text"), ""));
					hmInner.put("COLLATERAL_TEXT_ALIGN", rs.getString("text_align"));

					hmHeader.put(rs.getString("collateral_id"), hmInner);
				} else {
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("COLLATERAL_ID", rs.getString("collateral_id"));
					hmInner.put("COLLATERAL_PATH", rs.getString("collateral_image"));
					hmInner.put("COLLATERAL_IMG_ALIGN", rs.getString("image_align"));
					hmInner.put("COLLATERAL_TEXT", uF.showData(rs.getString("collateral_text"), ""));
					hmInner.put("COLLATERAL_TEXT_ALIGN", rs.getString("text_align"));

					hmFooter.put(rs.getString("collateral_id"), hmInner);
				}
			}
			rs.close();
			pst.close();

			// pst = con.prepareStatement("insert into document_download_history
			// (document_id, time_stamp, emp_id) values (?,?,?)");
			// pst.setInt(1, uF.parseToInt(getDoc_id()));
			// pst.setTimestamp(2,
			// uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()),
			// DBDATE+DBTIME));
			// pst.setInt(3, uF.parseToInt(strSessionUserId));
			// pst.execute();

			pst = con.prepareStatement("select * from document_comm_details where document_id =?");
			pst.setInt(1, uF.parseToInt(getDoc_id()));

			rs = pst.executeQuery();

			String strDocumentName = null;
			String strDocumentContent = null;
			String strDocumentHeader = "";
			String strDocumentFooter = "";
			String strHeader = null;
			String strFooter = null;
			String strHeaderImageAlign = "";
			String strHeaderCollateralText = "";
			String strHeaderTextAlign = "";
			String strFooterImageAlign = "";
			String strFooterCollateralText = "";
			String strFooterTextAlign = "";

			while (rs.next()) {
				strDocumentName = rs.getString("document_name");
				strDocumentContent = rs.getString("document_text");

				if (rs.getString("collateral_header") != null && !rs.getString("collateral_header").equals("")
						&& hmHeader.get(rs.getString("collateral_header")) != null) {
					Map<String, String> hmInner = hmHeader.get(rs.getString("collateral_header"));
					strHeader = uF.showData(hmInner.get("COLLATERAL_PATH"), "");
					strHeaderImageAlign = uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"), "");
					strHeaderCollateralText = uF.showData(hmInner.get("COLLATERAL_TEXT"), "");
					strHeaderTextAlign = uF.showData(hmInner.get("COLLATERAL_TEXT_ALIGN"), "");
				}
				if (rs.getString("collateral_footer") != null && !rs.getString("collateral_footer").equals("")
						&& hmFooter.get(rs.getString("collateral_footer")) != null) {
					Map<String, String> hmInner = hmFooter.get(rs.getString("collateral_footer"));
					strFooter = uF.showData(hmInner.get("COLLATERAL_PATH"), "");
					strFooterImageAlign = uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"), "");
					strFooterCollateralText = uF.showData(hmInner.get("COLLATERAL_TEXT"), "");
					strFooterTextAlign = uF.showData(hmInner.get("COLLATERAL_TEXT_ALIGN"), "");
				}

			}
			rs.close();
			pst.close();

			if (strDocumentName != null) {
				strDocumentName = strDocumentName.replace(" ", "");
			}
			String strDomain = request.getServerName().split("\\.")[0];
			Notifications nF = new Notifications(0, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			nF.setEmailTemplate(false);
			Map<String, String> hmParsedContent = null;

			//// Document document = new Document(PageSize.A4);
			// Document document = new Document(PageSize.A4,40, 40, 10, 60);
			//// String style="p {font-size: 80%;}";
			//// document.setHtmlStyleClass(style);
			//
			// ByteArrayOutputStream buffer = new ByteArrayOutputStream();

//			System.out.println("strDocumentContent ===>> " + strDocumentContent);
			if (strDocumentContent != null) {

				hmParsedContent = nF.parseContent(strDocumentContent, "", "");
				String strDocument = hmParsedContent.get("MAIL_BODY");
				if (strDocument != null) {
//					System.out.println("before strDocument ===>> " + strDocument);
					//Satrt Dattatray Date : 28-07-21  
					if (strDocument.contains("<pre style=\"text-align:justify\">") || strDocument.contains("<pre style=\"text-align:justify;\">") || strDocument.contains("<pre style=\"text-align: justify;\">") || strDocument.contains("<pre style=\"text-align: justify\">")) {
						System.out.println("if");
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
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>", "<p style=\"text-align: justify\">", true, true, "<p style=\"text-align: justify\">");
					}else {
						System.out.println("Else");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<li>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<p>", true, true, "<p>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", " </li>	", true, true, "</li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<pre>", true, true, "<pre>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<pre>", "<br/>", true, true, "<pre>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
					}
					//End Dattatray Date : 28-07-21 
//					System.out.println("strDocument ====>>> " + strDocument);
					
					// strDocument = strDocument.replaceAll("<br/>", "\n");
					// System.out.println("strDocument ====>>> " + strDocument);
				}

				String headerPath = "";
				if (strHeader != null && !strHeader.equals("")) {
					// headerPath=CF.getStrDocRetriveLocation()+strHeader;

					if (CF.getStrDocRetriveLocation() == null) {
						headerPath = DOCUMENT_LOCATION + strHeader;
					} else {
						// headerPath = CF.getStrDocRetriveLocation()
						// +I_COLLATERAL+"/"+I_IMAGE+"/"+strHeader;
						headerPath = CF.getStrDocSaveLocation() + I_COLLATERAL + "/" + I_IMAGE + "/" + strHeader;
					}
				}

				// Created by Dattatray Note: Created footerPath for display
				// image in footer section
				String footerPath = "";
				if (strFooter != null && !strFooter.equals("")) {

					if (CF.getStrDocRetriveLocation() == null) {
						footerPath = DOCUMENT_LOCATION + strFooter;
					} else {
						footerPath = CF.getStrDocSaveLocation() + I_COLLATERAL + "/" + I_IMAGE + "/" + strFooter;
					}
				}

				StringBuilder sbHeader = new StringBuilder();
				StringBuilder sbFooter = new StringBuilder();// Created Dattatray

				// System.out.println("headerPath ===>>> " + headerPath);

				// if(headerPath != null && !headerPath.equals("")) {
				// sbHeader.append("<table><tr><td><img height=\"60\"
				// src=\""+strDocumentHeader+"\"></td></tr></table>");
				// sbHeader.append("<table style=\"width: 100%;\">");
				// if(strHeaderImageAlign!=null &&
				// strHeaderImageAlign.equals("R")) {
				// sbHeader.append("<tr><td
				// valign=\"middle\">"+strHeaderCollateralText+"</td>" +
				// "<td align=\"right\">");
				// if(headerPath != null && !headerPath.equals("")) {
				// sbHeader.append("<img height=\"25\"
				// src=\""+headerPath+"\">");
				// }
				// sbHeader.append("</td></tr>");
				//
				// } else if(strHeaderImageAlign !=null &&
				// strHeaderImageAlign.equals("C")) {
				// sbHeader.append("<tr><td colspan=\"2\" align=\"Center\">");
				// if(headerPath != null && !headerPath.equals("")) {
				// sbHeader.append("<img height=\"25\"
				// src=\""+headerPath+"\"><br/>");
				// }
				// sbHeader.append(""+strHeaderCollateralText+"</td></tr>");
				// } else {
				// sbHeader.append("<tr><td>");
				// if(headerPath != null && !headerPath.equals("")) {
				// sbHeader.append("<img height=\"25\"
				// src=\""+headerPath+"\">");
				// }
				// sbHeader.append("</td> <td
				// valign=\"middle\">"+strHeaderCollateralText+"</td></tr>");
				// }
				// sbHeader.append("</table>");

				/*
				 * sbHeader.append("<div style=\"float: left; width: 100%;\">");
				 * if(strHeaderImageAlign!=null &&
				 * strHeaderImageAlign.equals("R")) { sbHeader.
				 * append("<span style=\"float: left; padding-top: 10px; padding-left: 50px;\">"
				 * +strHeaderCollateralText+"</span>" +
				 * "<span style=\"float: right; padding-left: 50px;\">");
				 * if(headerPath != null && !headerPath.equals("")) {
				 * sbHeader.append("<img height=\"30\" src=\""+headerPath+"\">")
				 * ; } sbHeader.append("</span>");
				 * 
				 * } else if(strHeaderImageAlign !=null &&
				 * strHeaderImageAlign.equals("C")) { sbHeader.
				 * append("<div style=\"float: left; width: 100%; text-align: center;\">"
				 * ); if(headerPath != null && !headerPath.equals("")) {
				 * sbHeader.append("<img height=\"30\" src=\""+headerPath+
				 * "\"><br/>"); }
				 * sbHeader.append(""+strHeaderCollateralText+"</div>"); } else
				 * { sbHeader.
				 * append("<div style=\"float: left; padding-right: 5px;\">");
				 * if(headerPath != null && !headerPath.equals("")) {
				 * sbHeader.append("<img height=\"30\" src=\""+headerPath+"\">")
				 * ; } sbHeader.
				 * append("</div> <div style=\"float: left; padding-top: 10px; padding-left: 50px;\">"
				 * +strHeaderCollateralText+"</div>"); }
				 * sbHeader.append("</div>");
				 */

				if (headerPath != null && !headerPath.equals("")) {
					sbHeader.append("<table style=\"width: 100%;\"><tr>");
					sbHeader.append("<td>");
					if (strHeader != null && !strHeader.equals("")) {
						sbHeader.append("<img src=\"" + headerPath + "\">");
					}
					sbHeader.append("</td>");
					sbHeader.append("</tr></table>");
				}

				// Created by Dattatray
				if (footerPath != null && !footerPath.equals("")) {
					if (strFooter != null && !strFooter.equals("")) {
						sbFooter.append(footerPath);
					}
				}

				/*
				 * sbHeader.append("<table><tr>"); if(strHeaderImageAlign!=null
				 * && strHeaderImageAlign.equals("R")) { sbHeader.
				 * append("<td width=\"60%\" valign=\"middle\" style=\"padding-left: 150px;\">"
				 * +strHeaderCollateralText+"</td>" +
				 * "<td width=\"40%\" ><div style=\"width: 100%; text-align: right;\">"
				 * ); if(headerPath != null && !headerPath.equals("")) {
				 * sbHeader.append("<img height=\"35\" src=\""+headerPath+"\">")
				 * ; } sbHeader.append("</div></td>");
				 * 
				 * } else if(strHeaderImageAlign !=null &&
				 * strHeaderImageAlign.equals("C")) {
				 * sbHeader.append("<td colspan=\"2\" align=\"Center\">");
				 * if(headerPath != null && !headerPath.equals("")) {
				 * sbHeader.append("<img height=\"30\" src=\""+headerPath+
				 * "\"><br/>"); }
				 * sbHeader.append(""+strHeaderCollateralText+"</td>"); } else {
				 * sbHeader.append("<td>"); if(headerPath != null &&
				 * !headerPath.equals("")) {
				 * sbHeader.append("<img height=\"30\" src=\""+headerPath+"\">")
				 * ; } sbHeader.
				 * append("</td> <td valign=\"middle\" style=\"padding-left: 50px;\">"
				 * +strHeaderCollateralText+"</td>"); }
				 * sbHeader.append("</tr></table>");
				 * 
				 * } else {
				 * 
				 * sbHeader.append("<table style=\"width: 100%;\"><tr>");
				 * if(strHeaderTextAlign!=null &&
				 * strHeaderTextAlign.equals("R")) { sbHeader.
				 * append("<td colspan=\"2\" align=\"right\" valign=\"middle\">"
				 * +strHeaderCollateralText+"</td>");
				 * 
				 * } else if(strHeaderTextAlign!=null &&
				 * strHeaderTextAlign.equals("C")) { sbHeader.
				 * append("<td colspan=\"2\" align=\"center\" valign=\"middle\">"
				 * +strHeaderCollateralText+"</td>"); } else {
				 * sbHeader.append("<td colspan=\"2\" valign=\"middle\">"
				 * +strHeaderCollateralText+"</td>"); }
				 * sbHeader.append("</tr></table>");
				 * 
				 * }
				 */

				// System.out.println("sbHeader=====>"+sbHeader.toString());

				// String footerPath="";
				// if(strFooter!=null && !strFooter.equals("")) {
				//// footerPath=CF.getStrDocRetriveLocation()+strFooter;
				// if(CF.getStrDocRetriveLocation()==null) {
				// footerPath = DOCUMENT_LOCATION + strFooter;
				// } else {
				// footerPath = CF.getStrDocRetriveLocation()
				// +I_COLLATERAL+"/"+I_IMAGE+"/"+strFooter;
				// }
				// }
				// System.out.println("footerPath ===>>> " + footerPath);

				// StringBuilder sbFooter = new StringBuilder();
				//
				// if(footerPath != null && !footerPath.equals("")) {
				// //sbFooter.append("<table><tr><td><img height=\"60\"
				// src=\""+strDocumentFooter+"\"></td></tr></table>");
				//
				// sbFooter.append("<table style=\"width: 100%;\">");
				// if(strFooterImageAlign!=null &&
				// strFooterImageAlign.equals("R")) {
				// sbFooter.append("<tr><td
				// valign=\"middle\">"+strFooterCollateralText+"</td> <td
				// align=\"right\">");
				// if(footerPath != null && !footerPath.equals("")) {
				// sbFooter.append("<img height=\"25\"
				// src=\""+footerPath+"\">");
				// }
				// sbFooter.append("</td></tr>");
				//
				// } else if(strFooterImageAlign!=null &&
				// strFooterImageAlign.equals("C")) {
				// sbFooter.append("<tr><td align=\"Center\">");
				// if(footerPath != null && !footerPath.equals("")) {
				// sbFooter.append("<img height=\"25\"
				// src=\""+footerPath+"\"><br/>");
				// }
				// sbFooter.append(""+strFooterCollateralText+"</td></tr>");
				// } else {
				// sbFooter.append("<tr><td>");
				// if(footerPath != null && !footerPath.equals("")) {
				// sbFooter.append("<img height=\"25\"
				// src=\""+footerPath+"\">");
				// }
				// sbFooter.append("</td> <td
				// valign=\"middle\">"+strFooterCollateralText+"</td></tr>");
				// }
				// sbFooter.append("</table>");
				// } else {
				//
				// sbFooter.append("<table style=\"width: 100%;\"><tr>");
				// if(strFooterTextAlign!=null &&
				// strFooterTextAlign.equals("R")) {
				// sbFooter.append("<td colspan=\"2\" align=\"right\"
				// valign=\"middle\">"+strFooterCollateralText+"</td>");
				//
				// } else if(strFooterTextAlign!=null &&
				// strFooterTextAlign.equals("C")) {
				// sbFooter.append("<td colspan=\"2\" align=\"center\"
				// valign=\"middle\">"+strFooterCollateralText+"</td>");
				// } else {
				// sbFooter.append("<td colspan=\"2\"
				// valign=\"middle\">"+strFooterCollateralText+"</td>");
				// }
				// sbFooter.append("</tr></table>");
				//
				// }

				// Document document = new Document(PageSize.A4);
				Document document = new Document(PageSize.A4, 40, 40, 10, 60);
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				PdfWriter writer = PdfWriter.getInstance(document, buffer);
				HeaderFooterPageEvent event = new HeaderFooterPageEvent(sbHeader.toString(), sbFooter.toString());// Created by Dattatray Note : sbFooter added
				writer.setPageEvent(event);

				document.open();
				// System.out.println("sbHeader ===>>> " + sbHeader.toString());
				// System.out.println("sbFooter ===>>> " + sbFooter.toString());
				HTMLWorker hw = new HTMLWorker(document);
				// hw.parse(new StringReader(sbHeader.toString()));
				hw.parse(new StringReader(strDocument));
				// hw.parse(new StringReader(sbFooter.toString()));

				/**
				 * Xml Parser
				 */

				// StringReader reader = new StringReader(strDocument);
				// XMLWorkerHelper.getInstance().parseXHtml(writer, document,
				// reader);
				/**
				 * Xml Parser end
				 */

				document.close();

				response.setContentType("application/pdf");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=" + strDocumentName + ".pdf");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush();
				buffer.close();
				out.close();

			}

			/*
			 * if(strDocumentName!=null) {
			 * response.setContentType("application/pdf");
			 * response.setContentLength(buffer.size());
			 * response.setHeader("Content-Disposition", "attachment; filename="
			 * + strDocumentName + ".pdf"); ServletOutputStream out =
			 * response.getOutputStream(); buffer.writeTo(out); out.flush();
			 * buffer.close(); out.close(); }
			 */

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "update";
	}

	
	/*// Function to insert string
	public static String insertString(String strDocument, String originalString, String stringToBeInserted, int index) {

		// Create a new string
		String newString = new String();

		for (int i = 0; i < originalString.length(); i++) {

			// Insert the original string character
			// into the new string
			newString += originalString.charAt(i);

			if (i == index) {

				// Insert the string to be inserted
				// into the new string
				newString += stringToBeInserted;
			}
		}

		// return the modified String
		strDocument = newString;
		return strDocument;
	}*/
	
	
	// public String downloadDocument() {
	//
	// Connection con = null;
	// ResultSet rs = null;
	// PreparedStatement pst = null;
	// Database db = new Database();
	// db.setRequest(request);
	// UtilityFunctions uF = new UtilityFunctions();
	//
	// try {
	//
	// con = db.makeConnection(con);
	//
	// Map<String, Map<String, String>> hmHeader=new HashMap<String, Map<String,
	// String>>();
	// Map<String, Map<String, String>> hmFooter=new HashMap<String, Map<String,
	// String>>();
	// pst = con.prepareStatement("select * from document_collateral");
	// rs = pst.executeQuery();
	// while(rs.next()) {
	// if(rs.getString("_type").equals("H")) {
	// Map<String, String> hmInner=new HashMap<String, String>();
	// hmInner.put("COLLATERAL_ID", rs.getString("collateral_id"));
	// hmInner.put("COLLATERAL_PATH", rs.getString("collateral_image"));
	// hmInner.put("COLLATERAL_IMG_ALIGN", rs.getString("image_align"));
	// hmInner.put("COLLATERAL_TEXT",
	// uF.showData(rs.getString("collateral_text"),""));
	// hmInner.put("COLLATERAL_TEXT_ALIGN", rs.getString("text_align"));
	//
	// hmHeader.put(rs.getString("collateral_id"), hmInner);
	// } else {
	// Map<String, String> hmInner=new HashMap<String, String>();
	// hmInner.put("COLLATERAL_ID", rs.getString("collateral_id"));
	// hmInner.put("COLLATERAL_PATH", rs.getString("collateral_image"));
	// hmInner.put("COLLATERAL_IMG_ALIGN", rs.getString("image_align"));
	// hmInner.put("COLLATERAL_TEXT",
	// uF.showData(rs.getString("collateral_text"),""));
	// hmInner.put("COLLATERAL_TEXT_ALIGN", rs.getString("text_align"));
	//
	// hmFooter.put(rs.getString("collateral_id"), hmInner);
	// }
	// }
	// rs.close();
	// pst.close();
	//
	//
	//// pst = con.prepareStatement("insert into document_download_history
	// (document_id, time_stamp, emp_id) values (?,?,?)");
	//// pst.setInt(1, uF.parseToInt(getDoc_id()));
	//// pst.setTimestamp(2,
	// uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()),
	// DBDATE+DBTIME));
	//// pst.setInt(3, uF.parseToInt(strSessionUserId));
	//// pst.execute();
	//
	// pst = con.prepareStatement("select * from document_comm_details where
	// document_id =?");
	// pst.setInt(1, uF.parseToInt(getDoc_id()));
	//
	// rs = pst.executeQuery();
	//
	// String strDocumentName = null;
	// String strDocumentContent = null;
	// String strDocumentHeader = "";
	// String strDocumentFooter = "";
	// String strHeader = null;
	// String strFooter = null;
	// String strHeaderImageAlign="";
	// String strHeaderCollateralText="";
	// String strHeaderTextAlign="";
	// String strFooterImageAlign="";
	// String strFooterCollateralText="";
	// String strFooterTextAlign="";
	//
	// while (rs.next()) {
	// strDocumentName = rs.getString("document_name");
	// strDocumentContent = rs.getString("document_text");
	//
	// if(rs.getString("collateral_header")!=null &&
	// !rs.getString("collateral_header").equals("") &&
	// hmHeader.get(rs.getString("collateral_header"))!=null) {
	// Map<String, String>
	// hmInner=hmHeader.get(rs.getString("collateral_header"));
	// strHeader = uF.showData(hmInner.get("COLLATERAL_PATH"),"");
	// strHeaderImageAlign=uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"),"");
	// strHeaderCollateralText=uF.showData(hmInner.get("COLLATERAL_TEXT"),"");
	// strHeaderTextAlign=uF.showData(hmInner.get("COLLATERAL_TEXT_ALIGN"),"");
	// }
	// if(rs.getString("collateral_footer")!=null &&
	// !rs.getString("collateral_footer").equals("") &&
	// hmFooter.get(rs.getString("collateral_footer"))!=null) {
	// Map<String, String>
	// hmInner=hmFooter.get(rs.getString("collateral_footer"));
	// strFooter = uF.showData(hmInner.get("COLLATERAL_PATH"),"");
	// strFooterImageAlign=uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"),"");
	// strFooterCollateralText=uF.showData(hmInner.get("COLLATERAL_TEXT"),"");
	// strFooterTextAlign=uF.showData(hmInner.get("COLLATERAL_TEXT_ALIGN"),"");
	// }
	//
	// }
	// rs.close();
	// pst.close();
	//
	// if(strDocumentName!=null) {
	// strDocumentName = strDocumentName.replace(" ", "");
	// }
	// String strDomain = request.getServerName().split("\\.")[0];
	// Notifications nF = new Notifications(0, CF);
	// nF.setDomain(strDomain);
	//
	// Map<String, String> hmParsedContent = null;
	//
	// Document document = new Document(PageSize.A4);
	//
	// ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	//
	// if(strDocumentContent!=null) {
	//
	//// hmParsedContent = nF.parseContent(strDocumentContent, "", "");
	// hmParsedContent = nF.parseContent(strDocumentContent, "", "");
	//
	// PdfWriter.getInstance(document, buffer);
	// document.open();
	//
	// String strDocument = hmParsedContent.get("MAIL_BODY");
	// if(strDocument!=null) {
	// strDocument = strDocument.replaceAll("<br/>", "");
	// }
	//
	// String headerPath="";
	// if(strHeader!=null && !strHeader.equals("")) {
	//// headerPath=CF.getStrDocRetriveLocation()+strHeader;
	//
	// if(CF.getStrDocRetriveLocation()==null) {
	// headerPath = DOCUMENT_LOCATION + strHeader;
	// } else {
	// headerPath = CF.getStrDocRetriveLocation()
	// +I_COLLATERAL+"/"+I_IMAGE+"/"+strHeader;
	// }
	// }
	//
	// StringBuilder sbHeader = new StringBuilder();
	//
	//// System.out.println("headerPath ===>>> " + headerPath);
	//
	// if(headerPath != null && !headerPath.equals("")) {
	// //sbHeader.append("<table><tr><td><img height=\"60\"
	// src=\""+strDocumentHeader+"\"></td></tr></table>");
	// sbHeader.append("<table style=\"width: 100%;\"><tr>");
	// if(strHeaderImageAlign!=null && strHeaderImageAlign.equals("R")) {
	// sbHeader.append("<td width=\"70%\" valign=\"middle\"
	// style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>" +
	// "<td align=\"right\">");
	// if(headerPath != null && !headerPath.equals("")) {
	// sbHeader.append("<img height=\"60\" src=\""+headerPath+"\">");
	// }
	// sbHeader.append("</td>");
	//
	// } else if(strHeaderImageAlign !=null && strHeaderImageAlign.equals("C"))
	// {
	// sbHeader.append("<td colspan=\"2\" align=\"Center\">");
	// if(headerPath != null && !headerPath.equals("")) {
	// sbHeader.append("<img height=\"60\" src=\""+headerPath+"\"><br/>");
	// }
	// sbHeader.append(""+strHeaderCollateralText+"</td>");
	// } else {
	// sbHeader.append("<td>");
	// if(headerPath != null && !headerPath.equals("")) {
	// sbHeader.append("<img height=\"60\" src=\""+headerPath+"\">");
	// }
	// sbHeader.append("</td> <td valign=\"middle\" style=\"padding-left:
	// 50px;\">"+strHeaderCollateralText+"</td>");
	// }
	// sbHeader.append("</tr></table>");
	//
	// } else {
	//
	// sbHeader.append("<table style=\"width: 100%;\"><tr>");
	// if(strHeaderTextAlign!=null && strHeaderTextAlign.equals("R")) {
	// sbHeader.append("<td colspan=\"2\" align=\"right\" valign=\"middle\"
	// style=\"padding-right: 50px;\">"+strHeaderCollateralText+"</td>");
	//
	// } else if(strHeaderTextAlign!=null && strHeaderTextAlign.equals("C")) {
	// sbHeader.append("<td colspan=\"2\" align=\"center\"
	// valign=\"middle\">"+strHeaderCollateralText+"</td>");
	// } else {
	// sbHeader.append("<td colspan=\"2\" valign=\"middle\"
	// style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>");
	// }
	// sbHeader.append("</tr></table>");
	//
	// }
	//
	//
	// String footerPath="";
	// if(strFooter!=null && !strFooter.equals("")) {
	//// footerPath=CF.getStrDocRetriveLocation()+strFooter;
	// if(CF.getStrDocRetriveLocation()==null) {
	// footerPath = DOCUMENT_LOCATION + strFooter;
	// } else {
	// footerPath = CF.getStrDocRetriveLocation()
	// +I_COLLATERAL+"/"+I_IMAGE+"/"+strFooter;
	// }
	// }
	//// System.out.println("footerPath ===>>> " + footerPath);
	//
	// StringBuilder sbFooter = new StringBuilder();
	//
	// if(footerPath != null && !footerPath.equals("")) {
	// //sbFooter.append("<table><tr><td><img height=\"60\"
	// src=\""+strDocumentFooter+"\"></td></tr></table>");
	//
	// sbFooter.append("<table><tr>");
	// if(strFooterImageAlign!=null && strFooterImageAlign.equals("R")) {
	// sbFooter.append("<td width=\"70%\" valign=\"middle\"
	// style=\"padding-left: 50px;\">"+strFooterCollateralText+"</td> <td
	// align=\"right\">");
	// if(footerPath != null && !footerPath.equals("")) {
	// sbFooter.append("<img height=\"60\" src=\""+footerPath+"\">");
	// }
	// sbFooter.append("</td>");
	//
	// } else if(strFooterImageAlign!=null && strFooterImageAlign.equals("C")) {
	// sbFooter.append("<td align=\"Center\">");
	// if(footerPath != null && !footerPath.equals("")) {
	// sbFooter.append("<img height=\"60\" src=\""+footerPath+"\"><br/>");
	// }
	// sbFooter.append(""+strFooterCollateralText+"</td>");
	// } else {
	// sbFooter.append("<td>");
	// if(footerPath != null && !footerPath.equals("")) {
	// sbFooter.append("<img height=\"60\" src=\""+footerPath+"\">");
	// }
	// sbFooter.append("</td> <td valign=\"middle\" style=\"padding-left:
	// 50px;\">"+strFooterCollateralText+"</td>");
	// }
	// sbFooter.append("</tr></table>");
	// } else {
	//
	// sbFooter.append("<table><tr>");
	// if(strFooterTextAlign!=null && strFooterTextAlign.equals("R")) {
	// sbFooter.append("<td colspan=\"2\" align=\"right\" valign=\"middle\"
	// style=\"padding-right: 50px;\">"+strFooterCollateralText+"</td>");
	//
	// } else if(strFooterTextAlign!=null && strFooterTextAlign.equals("C")) {
	// sbFooter.append("<td colspan=\"2\" align=\"center\"
	// valign=\"middle\">"+strFooterCollateralText+"</td>");
	// } else {
	// sbFooter.append("<td colspan=\"2\" valign=\"middle\"
	// style=\"padding-right: 50px;\">"+strFooterCollateralText+"</td>");
	// }
	// sbFooter.append("</tr></table>");
	//
	// }
	//// System.out.println("sbHeader ===>>> " + sbHeader.toString());
	//// System.out.println("sbFooter ===>>> " + sbFooter.toString());
	// HTMLWorker hw = new HTMLWorker(document);
	// hw.parse(new StringReader(sbHeader.toString()));
	// hw.parse(new StringReader(strDocument));
	// hw.parse(new StringReader(sbFooter.toString()));
	// document.close();
	//
	// }
	//
	// if(strDocumentName!=null) {
	// response.setContentType("application/pdf");
	// response.setContentLength(buffer.size());
	// response.setHeader("Content-Disposition", "attachment; filename=" +
	// strDocumentName + ".pdf");
	// ServletOutputStream out = response.getOutputStream();
	// buffer.writeTo(out);
	// out.flush();
	// buffer.close();
	// out.close();
	// }
	//
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	//
	// db.closeResultSet(rs);
	// db.closeStatements(pst);
	// db.closeConnection(con);
	// }
	// return "update";
	// }

	public void validate() {

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

	public String getDoc_id() {
		return doc_id;
	}

	public void setDoc_id(String doc_id) {
		this.doc_id = doc_id;
	}

}