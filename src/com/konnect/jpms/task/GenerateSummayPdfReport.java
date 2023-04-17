package com.konnect.jpms.task;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GenerateSummayPdfReport extends ActionSupport implements ServletRequestAware,ServletResponseAware,IStatements{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	HttpSession session;
	int pro_id;
	String operation;
	int teamsize; 
	String sessionEmpId;
	CommonFunctions CF;
//	List<String> reportList=new ArrayList<String>();
	List<String> alTeamActivities = new ArrayList<String>();
	List<String> alGanttChart = new ArrayList<String>();
	List<String> alClientDetails = new ArrayList<String>();
//	Map<String,String> 	hmServiceDesc=new HashMap<String, String>();
	List<String> proreportList = new ArrayList<String>();
	public String execute()
	{
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();
		if(pro_id==0){
		pro_id=uF.parseToInt((String)session.getAttribute("pro_id"));
		}
		sessionEmpId=(String)session.getAttribute(EMPID);
		getTeamSize();
		getProjectDetails();
		generateProjectReport();
		session.removeAttribute("pro_id");
		return null;
	}
	
	
	public void getTeamSize() {
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select count(distinct(emp_id)) as team from project_emp_details where pro_id=?");
			pst.setInt(1, pro_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				teamsize = uF.parseToInt(rs.getString("team"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	public void getProjectDetails()
	{
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpNameMap = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmServicemap = CF.getProjectServicesMap(con, true);
			
			
			pst = con.prepareStatement("select * from activity_info ai right join project_emp_details ped on ai.pro_id= ped.pro_id and ai.emp_id = ped.emp_id where  ped.pro_id=? order by deadline desc");
			pst.setInt(1, pro_id);
			rs = pst.executeQuery();
			while (rs.next()) {

				
				alTeamActivities.add(uF.showData(hmEmpNameMap.get(rs.getString("emp_id")), "") + ((rs.getBoolean("_isteamlead")) ? " [TL]" : ""));
				alTeamActivities.add(uF.showData(rs.getString("activity_name"),"N/A"));
				alTeamActivities.add(uF.getDateFormat(rs.getString("deadline"),DBDATE, CF.getStrReportDateFormat()));
				alTeamActivities.add(uF.showData(rs.getString("completed"), "0") + "%");
				alTeamActivities.add(uF.showData(rs.getString("color_code"), "white"));
				
			}
			rs.close();
			pst.close();
			
			
				
			pst = con.prepareStatement("select * from projectmntnc p, client_poc cp where p.poc = cp.poc_id and pro_id = ?");
			pst.setInt(1, pro_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				
				alClientDetails.add(uF.showData(rs.getString("contact_name"), "N/A"));
				alClientDetails.add(uF.showData(rs.getString("contact_desig"), "N/A"));
				alClientDetails.add(uF.showData(rs.getString("contact_department"), "N/A"));
				alClientDetails.add(uF.showData(rs.getString("contact_email"), "N/A"));
				alClientDetails.add(uF.showData(rs.getString("contact_number"), "N/A"));
			}
			rs.close();
			pst.close();
			
//			hmServiceDesc=CF.getServiceDesc();
			
			pst = con.prepareStatement("select * from projectmntnc where pro_id=?");
			pst.setInt(1, pro_id);
			rs = pst.executeQuery();
			while (rs.next()) {

//				int p_id = rs.getInt("pro_id");
				
				String delivery_status = rs.getString("approve_status");
				
				proreportList.add(rs.getString("pro_name"));
				proreportList.add(uF.showData(hmServicemap.get(rs.getString("service")), ""));
				proreportList.add(rs.getString("description"));
				
				proreportList.add(teamsize+"");
				proreportList.add(uF.removeNull(rs.getString("idealtime")));
				proreportList.add(uF.roundOffInTimeInHoursMins(uF.parseToDouble(rs.getString("already_work"))));
				proreportList.add(uF.getDateFormat(rs.getString("deadline"), DBDATE,CF.getStrReportDateFormat()));
				if (delivery_status.equals("n"))
					proreportList.add("Working");
				else
					proreportList.add("Completed");
				if (delivery_status.equals("n"))
					proreportList.add("Not Delivered");
				else
					proreportList.add("Delivered to Client");
				proreportList.add(uF.getDateFormat(rs.getString("start_date"), DBDATE,CF.getStrReportDateFormat()));
				if(rs.getBoolean("ismonthly")){
					proreportList.add("Monthly");
				}else{
					proreportList.add("Fixed");
				}
				
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from activity_info ai right join project_emp_details ped on ai.pro_id= ped.pro_id and ai.emp_id = ped.emp_id where  ped.pro_id=? order by deadline desc");
			pst.setInt(1, pro_id);
			rs = pst.executeQuery();
			String strEmpIdNew=null;
			String strEmpIdOld=null;
			while (rs.next()) {
				strEmpIdNew = rs.getString("activity_name");
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					alGanttChart.add(uF.showData(hmEmpNameMap.get(rs.getString("emp_id")), "") + ((rs.getBoolean("_isteamlead")) ? " [TL]" : ""));
					alGanttChart.add(uF.showData(rs.getString("activity_name"),"N/A"));
					alGanttChart.add(uF.getDateFormat(rs.getString("deadline"),DBDATE, CF.getStrReportDateFormat()));
					alGanttChart.add(uF.showData(rs.getString("completed"), "0") + "%");
					alGanttChart.add(uF.showData(rs.getString("color_code"), "white"));
				}
				strEmpIdOld=strEmpIdNew;
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void generateProjectReport() {
		UtilityFunctions uF = new UtilityFunctions();
		Document document = new Document();
		try {
			List<Element> supList1 =null;
			Phrase phrase1 = null;
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, buffer);
			document.open();

			String a = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"1\" color=\"#346897\"><b>Project Summary</b></font></td></tr></table>";
			supList1 = HTMLWorker.parseToList(new StringReader(a), null);
			phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase1.add(supList1.get(0));
			document.add(phrase1);
			
			a = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td>______________________________________________________________________________</td></tr></table>";
			supList1 = HTMLWorker.parseToList(new StringReader(a), null);
			phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA,11));
			phrase1.add(supList1.get(0));
			document.add(phrase1);
			
			document.add(new Paragraph(" "));	
			StringBuilder sb=new StringBuilder();
			sb.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"  width=\"100%\">" +
					"<tr><td width=\"40%\"><font size=\"1\"><b>Project Details</b></font></td><td width=\"20%\">&nbsp;</td><td width=\"40%\"><font size=\"1\"><b>Client Details</b></font></td></tr>" +
					"<tr><td width=\"40%\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"1\">");

			if(proreportList.size()>7){
					
					sb.append("<tr><td bgcolor=\"#D8D8D8 \"><font size=\"1\"><b>&nbsp;Project Name </b></font></td><td><font size=\"1\">&nbsp;"+uF.showData(proreportList.get(0),"")+"</font></td></tr>"
					+ "<tr><td bgcolor=\"#D8D8D8 \"><font size=\"1\"><b>&nbsp;Service</b></font></td><td><font size=\"1\">&nbsp;"+uF.showData(proreportList.get(1),"")+"</font></td></tr>"
//					+ "<tr><td bgcolor=\"#D8D8D8 \"><font size=\"1\"><b>&nbsp;Description</b></font></td><td><font size=\"1\">&nbsp;"+uF.showData(proreportList.get(2),"")+"</font></td></tr>"
					+ "<tr><td bgcolor=\"#D8D8D8 \"><font size=\"1\"><b>&nbsp;Team Size</b></font></td><td><font size=\"1\">&nbsp;"+uF.showData(proreportList.get(3),"")+"</font></td></tr>"
					+ "<tr><td bgcolor=\"#D8D8D8 \"><font size=\"1\"><b>&nbsp;Estimated Hrs</b></font></td><td><font size=\"1\">&nbsp;"+uF.showData(proreportList.get(4),"")+"Hrs</font></td></tr>"
					+ "<tr><td bgcolor=\"#D8D8D8\"><font size=\"1\"><b>&nbsp;Actual Hours (AH) &nbsp;</b></font></td><td><font size=\"1\">&nbsp;"+uF.showData(proreportList.get(5),"")+"Hrs</font></td></tr>"
					+ "<tr><td bgcolor=\"#D8D8D8\"><font size=\"1\"><b>&nbsp;Project Deadline</b></font></td><td><font size=\"1\">&nbsp;"+uF.showData(proreportList.get(6),"")+"</font></td></tr>"
					+ "<tr><td bgcolor=\"#D8D8D8\"><font size=\"1\"><b>&nbsp;Completion Status &nbsp;</b></font></td><td><font size=\"1\">&nbsp;"+uF.showData(proreportList.get(7),"")+"</font></td></tr>"
					+ "<tr><td bgcolor=\"#D8D8D8\"><font size=\"1\"><b>&nbsp;Delivery Summary &nbsp;</b></font></td><td><font size=\"1\">&nbsp;"+uF.showData(proreportList.get(8),"")+"</font></td></tr>" 
					+ "<tr><td bgcolor=\"#D8D8D8\"><font size=\"1\"><b>&nbsp;Project Type &nbsp;</b></font></td><td><font size=\"1\">&nbsp;"+uF.showData(proreportList.get(10),"")+"</font></td></tr>"
							+"</table></td>");
					
					}
					
					sb.append("<td width=\"20%\">&nbsp;</td>" +
							
					"<td width=\"40%\" valign=\"top\">" +
					"");
					if(alClientDetails!=null && alClientDetails.size()>4){
					sb.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\">" +
					"<tr><td bgcolor=\"#D8D8D8 \"><font size=\"1\"><b>&nbsp;Contact Name </b></font></td><td><font size=\"1\">&nbsp;"+uF.showData(alClientDetails.get(0),"")+"</font></td></tr>"
					+ "<tr><td bgcolor=\"#D8D8D8 \"><font size=\"1\"><b>&nbsp;Designation</b></font></td><td><font size=\"1\">&nbsp;"+uF.showData(alClientDetails.get(1),"")+"</font></td></tr>"
					+ "<tr><td bgcolor=\"#D8D8D8 \"><font size=\"1\"><b>&nbsp;Department</b></font></td><td><font size=\"1\">&nbsp;"+uF.showData(alClientDetails.get(2),"")+"</font></td></tr>"
					+ "<tr><td bgcolor=\"#D8D8D8 \"><font size=\"1\"><b>&nbsp;Contact No</b></font></td><td><font size=\"1\">&nbsp;"+uF.showData(alClientDetails.get(3),"")+"</font></td></tr>"
					+ "<tr><td bgcolor=\"#D8D8D8 \"><font size=\"1\"><b>&nbsp;Email</b></font></td><td><font size=\"1\">&nbsp;"+uF.showData(alClientDetails.get(4),"")+"</font></td></tr>"
					+ "</table>");
					}
					sb.append("</td></tr></table>");
			
			supList1 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
			phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA,11));
			phrase1.add(supList1.get(0));
			document.add(phrase1);
			
			document.add(new Paragraph(" "));
			
			 sb=new StringBuilder();
				sb.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"  width=\"100%\">" +
						"<tr><td valign=\"top\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" align=\"left\"><tr><td><font size=\"1\"><b>Gantt Chart</b></font></td></tr></table></td></tr>" +
						"<tr><td align=\"left\">" +
						"<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\" width=\"100%\" align=\"left\">" +
						"<tr><td bgcolor=\"#CEE3F6\"><font size=\"1\"><b>&nbsp;Project Name</b></font></td>" +
						"<td bgcolor=\"#CEE3F6\"><font size=\"1\"><b>&nbsp;Resource</b></font></td>" +
						"<td colspan=\"2\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\"><tr><td align=\"left\"><font size=\"1\">&nbsp;"+uF.showData(proreportList.get(9),"")+"</font></td><td align=\"right\"><font size=\"1\">&nbsp;"+uF.showData(proreportList.get(6),"")+"&nbsp;</font></td></tr></table></td></tr>" +
						"<tr><td bgcolor=\"#CEE3F6\"><font size=\"1\"><b>&nbsp;"+uF.showData(proreportList.get(0),"")+"</b></font></td>" +
						"<td bgcolor=\"#CEE3F6\"><font size=\"1\"><b>&nbsp;</b></font></td>" +
						"<td colspan=\"2\"><font size=\"1\"><b>&nbsp;</b></font></td></tr>");
				for(int i=0; alGanttChart!=null && i<alGanttChart.size(); i+=5){
					if(!alGanttChart.get(i+1).equals("N/A")){
						sb.append("<tr><td bgcolor=\"#CEE3F6\"><font size=\"1\">&nbsp;"+uF.showData(alGanttChart.get(i+1),"")+"</font></td>" +
						"<td bgcolor=\"#CEE3F6\"><font size=\"1\">&nbsp;"+uF.showData(alGanttChart.get(i+0),"")+"</font></td>" +
						"<td colspan=\"2\"><table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"1\" bgcolor=\"white\">" +
						"<tr><td border-width=\"1\" bordercolor=\"white\" bgcolor="+uF.showData(alGanttChart.get(i+4),"white")+">&nbsp;</td></tr></table></td></tr>");
					}
				}
						sb.append("</table></td></tr></table>");
				supList1 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
				phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA,11));
				phrase1.add(supList1.get(0));
				document.add(phrase1);
			
				
				/*sb=new StringBuilder();
				sb.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"  width=\"100%\">" +
						"<tr><td valign=\"top\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"70%\" align=\"left\"><tr><td><font size=\"1\"><b>Gantt Chart</b></font></td></tr></table></td></tr>" +
						"<tr><td align=\"left\">" +
						"<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\" width=\"100%\" align=\"left\">" +
						"<tr><td bgcolor=\"#CEE3F6\"><font size=\"1\"><b>&nbsp;Project Name</b></font></td>" +
						"<td bgcolor=\"#CEE3F6\"><font size=\"1\"><b>&nbsp;Resource</b></font></td>" +
						"<td><font size=\"1\"><b>&nbsp;"+uF.showData(proreportList.get(9),"")+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+uF.showData(proreportList.get(6),"")+"</b></font></td></tr>" +
						"<tr><td bgcolor=\"#CEE3F6\"><font size=\"1\"><b>&nbsp;"+uF.showData(proreportList.get(0),"")+"</b></font></td>" +
						"<td bgcolor=\"#CEE3F6\"><font size=\"1\"><b>&nbsp;</b></font></td>" +
						"<td><font size=\"1\"><b>&nbsp;</b></font></td></tr>");
				for(int i=0; alTeamActivities!=null && i<alTeamActivities.size(); i+=5){
					if(!alTeamActivities.get(i+1).equals("N/A")){
						sb.append("<tr><td bgcolor=\"#CEE3F6\"><font size=\"1\">&nbsp;"+uF.showData(alTeamActivities.get(i+1),"")+"</font></td>" +
						"<td bgcolor=\"#CEE3F6\"><font size=\"1\">&nbsp;"+uF.showData(alTeamActivities.get(i+0),"")+"</font></td>" +
						"<td bgcolor="+uF.showData(alTeamActivities.get(i+4),"")+"><font size=\"1\">&nbsp;</font></td></tr>");
					}
				}
						sb.append("</table></td></tr></table>");
				supList1 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
				phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA,11));
				phrase1.add(supList1.get(0));
				document.add(phrase1);*/
			document.add(new Paragraph(" "));
			sb=new StringBuilder();
			sb.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"  width=\"100%\">" +
					"<tr><td valign=\"top\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"70%\" align=\"left\"><tr><td><font size=\"1\"><b>Resource Details</b></font></td></tr></table></td></tr>" +
					"<tr><td align=\"left\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"1\" width=\"70%\" align=\"left\">" +
					"<tr><td bgcolor=\"#D8D8D8 \"><font size=\"1\"><b>&nbsp;Resource </b></font></td>" +
					"<td bgcolor=\"#D8D8D8 \"><font size=\"1\"><b>&nbsp;Task </b></font></td>" +
					"<td bgcolor=\"#D8D8D8 \"><font size=\"1\"><b>&nbsp;Deadline </b></font></td>" +
					"<td bgcolor=\"#D8D8D8 \"><font size=\"1\"><b>&nbsp;Completion Status</b></font></td></tr>");
			for(int i=0; alTeamActivities!=null && i<alTeamActivities.size(); i+=5){			
					sb.append("<tr><td><font size=\"1\">&nbsp;"+uF.showData(alTeamActivities.get(i+0),"")+"</font></td>" +
					"<td><font size=\"1\">&nbsp;"+uF.showData(alTeamActivities.get(i+1),"")+"</font></td>" +
					"<td><font size=\"1\">&nbsp;"+uF.showData(alTeamActivities.get(i+2),"")+"</font></td>" +
					"<td><font size=\"1\">&nbsp;"+uF.showData(alTeamActivities.get(i+3),"")+"</font></td></tr>");
			}
					sb.append("</table></td></tr></table>");
			supList1 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
			phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA,11));
			phrase1.add(supList1.get(0));
			document.add(phrase1);
			document.close();
			
			if (getOperation() != null && getOperation().equalsIgnoreCase("pdfDwld")) {

				response.setContentType("application/pdf");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition",
						"attachment; filename=ProjectSummary.pdf");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush();
				
			} else {
				byte[] bytes = buffer.toByteArray();
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_GENERATED_TIMESHEET, CF);
				nF.request = request;
				nF.setDomain(strDomain);
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setSupervisor(true);
//				nF.setStrEmpId(sessionEmpId);
				nF.setStrEmailTo("");
				nF.setSupervisor(true);
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName("ProjectSummary.pdf");
				nF.sendNotifications();
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public int getPro_id() {
		return pro_id;
	}
	public void setPro_id(int pro_id) {
		this.pro_id = pro_id;
	}
	public String getOperation() {
		return operation;
	}


	public void setOperation(String operation) {
		this.operation = operation;
	}


	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response=response;
	}
	private HttpServletRequest request;
	
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
}
