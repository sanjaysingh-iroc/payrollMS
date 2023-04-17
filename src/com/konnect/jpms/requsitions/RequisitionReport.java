package com.konnect.jpms.requsitions;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.document.HeaderFooterPageEvent;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class RequisitionReport extends ActionSupport  implements ServletRequestAware, ServletResponseAware, IStatements {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	
	CommonFunctions CF;
	private static Logger log = Logger.getLogger(RequisitionReport.class);
	
	String strStartDate;
	String strEndDate;
	
	String alertStatus;
	String alert_type;
	
	public String execute() {
	
		session = request.getSession(); 
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		request.setAttribute(PAGE, "/jsp/requisitions/RequisitionsReport.jsp");
		request.setAttribute(TITLE, "My Requisitions");
		UtilityFunctions uF = new UtilityFunctions();
		
		String operation = (String) request.getParameter("operation");
		String strRequiId = (String) request.getParameter("strRequiId");
		if(operation != null && operation.equalsIgnoreCase("pdf") && uF.parseToInt(strRequiId) > 0){
			generatePdf(uF,strRequiId);
		}
		
		if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(REQUISITION_APPROVAL_ALERT)){
			String strDomain = request.getServerName().split("\\.")[0];
			CF.updateUserAlerts(CF,request,strSessionEmpId,strDomain,REQUISITION_APPROVAL_ALERT,UPDATE_ALERT);
		} 
		
		viewReport(uF);
		return SUCCESS;
	}
	
	private void generatePdf(UtilityFunctions uF, String strRequiId) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		ResultSet rst = null;

		try {
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
			
			pst = con.prepareStatement("select * from requisition_details where requisition_id=?");
			pst.setInt(1, uF.parseToInt(strRequiId));
			rst = pst.executeQuery();
			int nEmpId = 0;
			int nActivityId = 0;
			while (rst.next()){
				nEmpId = rst.getInt("emp_id");
				nActivityId = rst.getInt("document_id");
			}
			rst.close();
			pst.close();
			
			Map<String, String>  hmActivity = CF.getActivityName(con);
			if(hmActivity == null) hmActivity = new HashMap<String, String>();
			
			String strActivityName = uF.showData(hmActivity.get(""+nActivityId),"");
			Map<String, String> hmEmpInner = hmEmpInfo.get(""+nEmpId);		
			
			StringBuilder sbEmpSalTable = CF.getEmployeeSalaryDetails(con, CF, uF, ""+nEmpId, request, session);
			if(sbEmpSalTable == null) sbEmpSalTable = new StringBuilder();
			
			String empOrgId = CF.getEmpOrgId(con, uF, ""+nEmpId);
			
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
			
			pst=con.prepareStatement("select * from nodes");
			rst = pst.executeQuery();
			Map<String, String> hmMapActivityNode = new HashMap<String, String>();
			while(rst.next()){
				hmMapActivityNode.put(rst.getString("mapped_activity_id"), rst.getString("node_id"));
			}
			rst.close();
			pst.close();
			
			int nTriggerNode = uF.parseToInt(hmMapActivityNode.get(""+nActivityId));
			
			pst = con.prepareStatement("select * from document_comm_details where trigger_nodes like '%,"+nTriggerNode+",%' and status=1 and org_id=? order by document_id desc limit 1");
			pst.setInt(1, uF.parseToInt(empOrgId));
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
			
			Notifications nF = new Notifications(N_NEW_ACTIVITY, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrEmpId(""+nEmpId);  
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
//			nF.setStrContextPath(request.getServletPath());
			nF.setStrContextPath(request.getContextPath());
			 
			nF.setStrEmpFname(hmEmpInner.get("FNAME"));
			nF.setStrEmpLname(hmEmpInner.get("LNAME"));
			nF.setStrSalaryStructure(sbEmpSalTable.toString());
			nF.setStrActivityName(strActivityName); 
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
				if(strDocument!=null) {
					strDocument = strDocument.replaceAll("<br/>", "");
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
				
				HTMLWorker hw = new HTMLWorker(document);
//				hw.parse(new StringReader(sbHeader.toString())); 
				hw.parse(new StringReader(strDocument));
//				hw.parse(new StringReader(sbFooter.toString()));
				document.close();  
			
			}
			
//			byte[] bytes = buffer.toByteArray();			
			
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename="+strActivityName+"_"+nEmpId+".pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
			
			
		} catch (Exception e) {  
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void viewReport(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		
		List<List<String>> reportList = new ArrayList<List<String>>();
		try{
			
			con = db.makeConnection(con);
			Map<String, String> hmEmpMap = CF.getEmpNameMap(con, null, null);
			
			
			pst = con.prepareStatement("select activity_id,activity_name from activity_details ad, nodes n where ad.activity_id=n.mapped_activity_id and ad.isactivity=? order by activity_name");
			pst.setBoolean(1, true);
			rs = pst.executeQuery();
			Map<String, String> hmDocTypeMap = new HashMap<String, String>();
			while(rs.next()){
				hmDocTypeMap.put(rs.getString("activity_id"), rs.getString("activity_name"));				
			}	 
			rs.close();
			pst.close();
//			System.out.println("hmDocTypeMap=====>"+hmDocTypeMap);
			
			pst = con.prepareStatement("select * from infrastructure_type order by infra_type_id");
			rs = pst.executeQuery();
			Map<String, String> hmInfraTypeMap = new HashMap<String, String>();
			while (rs.next()) {
				hmInfraTypeMap.put(rs.getString("infra_type_id"), rs.getString("infra_type"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from requisition_details where emp_id = ? ");
			if((getStrStartDate()!=null && !getStrStartDate().equals("")) && (getStrEndDate()!=null && !getStrEndDate().equals(""))){
				sbQuery.append(" and requisition_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"'");
			}
			sbQuery.append(" order by requisition_date desc");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			System.out.println("pst=="+pst);
			rs = pst.executeQuery();  
			int nCount = 0;
			while(rs.next()){
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("requisition_id"));
				alInner.add(rs.getString("emp_id"));
				
				if(rs.getInt("is_approved") == 0){
					StringBuilder sb = new StringBuilder();
					/*sb.append("<img src=\"images1/icons/pending.png\" border=\"0\" title=\"Pending\" >");	*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\"  title=\"Pending\"></i>");	
					
					/*sb.append("&nbsp;<a href=\"javascript:void(0);\" onclick=\"((confirm('Are You sure you want to cancel this request?'))?getContent('myDiv_"+nCount+"','AddNewRequisition.action?operation=P&strRequiId="+rs.getString("requisition_id")+"') :'')\">"+"<img title=\"Pull Out\" src=\""+request.getContextPath()+"/images1/icons/pullout.png\" border=\"0\">" +"</a>");*/
					sb.append("&nbsp;<a href=\"javascript:void(0);\" onclick=\"((confirm('Are You sure you want to cancel this request?'))?getContent('myDiv_"+nCount+"','AddNewRequisition.action?operation=P&strRequiId="+rs.getString("requisition_id")+"') :'')\"> <i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" title=\"Pull Out\"></i> </a>");
					
					alInner.add(sb.toString());
				} else if(rs.getInt("is_approved") == 1 && uF.parseToBoolean(rs.getString("is_received"))){
					alInner.add("<img title=\"Received\" src=\"images1/icons/act_now.png\" border=\"0\">");
				} else if(rs.getInt("is_approved") == 1 && !uF.parseToBoolean(rs.getString("is_received"))){
					/* alInner.add("<img src=\"images1/icons/approved.png\" border=\"0\" title=\"Approved\" >");*/
					 alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					 
					 
				} else if(rs.getInt("is_approved") == -1){
					/*alInner.add("<img src=\"images1/icons/denied.png\" border=\"0\" title=\"Denied\" >");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>>");
					
				} else if(rs.getInt("is_approved") == -2){
					/*alInner.add("<img src=\"images1/icons/pullout.png\" border=\"0\" title=\"Pulled out\" >");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" title=\"Pulled out\"></i>");
					
				}
				
				String strRequitype = "";
				String strDoctype = "";
				String strInfraType = "";
				if(rs.getInt("requi_type") == R_N_REQUI_DOCUMENT){
					strRequitype = R_S_REQUI_DOCUMENT;
					strDoctype = uF.showData(hmDocTypeMap.get(rs.getString("document_id")), "");
				} else if(rs.getInt("requi_type") == R_N_REQUI_INFRASTRUCTURE){
					strRequitype = R_S_REQUI_INFRASTRUCTURE;					
					strInfraType = uF.showData(hmInfraTypeMap.get(rs.getString("infra_type")), "");
				} else if(rs.getInt("requi_type") == R_N_REQUI_OTHER){
					strRequitype = R_S_REQUI_OTHER;
				}
				alInner.add(strRequitype);
				alInner.add(strDoctype);
				alInner.add(strInfraType);
				alInner.add(rs.getString("purpose"));
				alInner.add(uF.getDateFormat(rs.getString("requisition_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(uF.getDateFormat(rs.getString("requi_from"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(uF.getDateFormat(rs.getString("requi_to"), DBDATE, CF.getStrReportDateFormat()));
				
				String strDownloadLink = "";
				if(rs.getInt("is_approved") == 1 && rs.getInt("requi_type") == R_N_REQUI_DOCUMENT){
					strDownloadLink ="<a href=\"MyRequests.action?operation=pdf&strRequiId="+rs.getString("requisition_id")+"\"><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>"; 
				}
				alInner.add(strDownloadLink);
				
				String strReceiveLink = "";
				if(rs.getInt("is_approved") == 1 && !uF.parseToBoolean(rs.getString("is_received"))){
					strReceiveLink ="<a href=\"javascript:void(0);\" onclick=\"((confirm('Are you sure, you want to receive this?'))?getContent('myDiv_"+nCount+"','AddNewRequisition.action?operation=R&strRequiId="+rs.getString("requisition_id")+"') :'')\">Receive</a>";
				}
				alInner.add(strReceiveLink);
				
				reportList.add(alInner);
				
				nCount++;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", reportList);
						
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getStrStartDate() {
		return strStartDate;
	}

	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}

	public String getStrEndDate() {
		return strEndDate;
	}

	public void setStrEndDate(String strEndDate) {
		this.strEndDate = strEndDate;
	}

	public String getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	
	
}
