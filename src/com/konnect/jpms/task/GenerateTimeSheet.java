package com.konnect.jpms.task;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class GenerateTimeSheet extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;

	String emp_id;
	String task_date;
	String emptype;
	String empname;
	String strUserType;
	String isOld;
	int pro_id; 
	String taskId;
	String mailAction;  
	Map session;
	HttpServletRequest request;
	private HttpServletResponse response;
	CommonFunctions CF;

	public String execute() {
		session = ActionContext.getContext().getSession();
		strUserType = (String) session.get(BASEUSERTYPE);
		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();
		
		if (getEmp_id() == null) {
			emp_id = (String) session.get(EMPID);
		}
		if (getTask_date() == null && strUserType != null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
			setTask_date(uF.getDateFormat(uF.getCurrentDate(CF.getStrReportDateFormat()) + "", DBDATE, DATE_FORMAT));
		}
		empname = getEmpName(emp_id);

//		System.out.println("mailAction===>" + mailAction);
//		System.out.println("getTask_date===>" + getTask_date());
		if (getMailAction() != null && getMailAction().equalsIgnoreCase("sendMail")) {
			setTask_date(uF.getDateFormat(getTask_date(), DATE_FORMAT, DBDATE));
//			System.out.println("getTask_date===>" + getTask_date());
			insertSendReportStatus();
		}

		/*
		 * if(strUserType.equalsIgnoreCase(ADMIN) ||
		 * strUserType.equalsIgnoreCase(MANAGER)) { if(getPro_id()!=0) {
		 * generateProjectSumarryReport(); }else{
		 * generateProjectReportForAdmin(); } }else{ generatePdfReport(); //
		 * generateExcelReport(); }
		 */

		generatePdfReport();

		return SUCCESS;
	}

	public void insertSendReportStatus() {
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("insert into task_activity(task_date, start_time,end_time,emp_id,activity,_comment,sent,is_billable,issent_report) values(?,?,?,?,?,?,'n',?,?)");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setTime(2, uF.getCurrentTime(CF.getStrTimeZone()));
			pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt(getEmp_id()));
			pst.setString(5, "Report Sent");
			pst.setString(6, "Report sent to ");
			pst.setBoolean(7, false);
			pst.setBoolean(8, true);
			pst.executeUpdate();
			pst.close();

			pst = con.prepareStatement("select max(task_id) as taskId from task_activity");
			rs = pst.executeQuery();
			while (rs.next()) {

				setTaskId(rs.getString("taskId"));
			}
			rs.close();
			pst.close();

			// getTaskId();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);

		}
	}

	public void generatePdfReport() {
		Document document = new Document();
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();		
		db.setRequest(request);
		double dblTotalNonBillableTime = 0;
		double dblTotalBillableTime = 0;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
			
			Map hmServiceMap = CF.getServicesMap(con, false);
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			Map<String, String> hmClientName =new FillTaskRelatedMap(request).getClientNameMap();
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, buffer);
			document.open();

			String strCompanyLogo = CF.getStrOrgLogo();
			String filePathCompanyLOgo = request.getRealPath("/userImages/" + strCompanyLogo + "");
//			String filePathCompanyLOgodefault = request.getRealPath("/userImages/logo_new.png");
			String filePathCompanyLOgodefault = "";

			try {
				String img = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"right\"><img src=\"" + filePathCompanyLOgo
						+ "\" width=\"50%\"/> </td></tr></table>";
				List<Element> supList = HTMLWorker.parseToList(new StringReader(img), null);
				Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase.add(supList.get(0));
				document.add(phrase);
			} catch (Exception e) {
				e.printStackTrace();
				String img = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"right\"><img src=\"" + filePathCompanyLOgodefault
						+ "\" width=\"50%\"/> </td></tr></table>";
				List<Element> supList = HTMLWorker.parseToList(new StringReader(img), null);
				Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase.add(supList.get(0));
				document.add(phrase);
			}

			String h = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" + "<tr><td><font size=\"1\">Employee Name : </font></td><td><font size=\"1\"><b>"
					+ hmEmpCodeName.get(getEmp_id())
					+ "</b></font></td><td><font size=\"1\">&nbsp;</font></td><td><font size=\"1\">&nbsp;</font></td><td><font size=\"1\">&nbsp;</font></td></tr>"
					+ "<tr><td><font size=\"1\">Employee Code : </font></td><td><font size=\"1\"><b>" + hmEmpCode.get(getEmp_id())
					+ "</b></font></td><td><font size=\"1\">&nbsp;</font></td><td><font size=\"1\">&nbsp;</font></td><td><font size=\"1\">&nbsp;</font></td></tr>"
					+ "<tr><td><font size=\"1\">Skills : </font></td><td><font size=\"1\"><b>" + getSkillDetails()
					+ "</b></font></td><td><font size=\"1\">&nbsp;</font></td><td><font size=\"1\">&nbsp;</font></td><td><font size=\"1\">&nbsp;</font></td></tr>"
					+ "<tr><td><font size=\"1\">Date : </font></td><td><font size=\"1\"><b>" + uF.getDateFormat(getTask_date(), DBDATE, CF.getStrReportDateFormat())
					+ "</b></font></td><td><font size=\"1\">&nbsp;</font></td><td><font size=\"1\">&nbsp;</font></td><td><font size=\"1\">&nbsp;</font></td></tr>"
					+ "</table><br/>";

			// String h =
			// "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td><b>"+empname+"'s Timesheet Report</b></td></tr><tr><td></td></tr></table><br/>";
			List<Element> supList0 = HTMLWorker.parseToList(new StringReader(h), null);
			Phrase phrase0 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase0.add(supList0.get(0));
			document.add(phrase0);

			String a = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"
					+ "<tr><td bgcolor=\"#D8D8D8 \" colspan=\"2\"><font size=\"1\"><b>&nbsp;Task/Activities</b></font></td>"
					+ "<td bgcolor=\"#D8D8D8 \" align=\"center\"><font size=\"1\"><b>&nbsp;Project Desc.</b></font></td>"
					+ "<td bgcolor=\"#D8D8D8 \" align=\"center\"><font size=\"1\"><b>&nbsp;Service</b></font></td>"
					+ "<td bgcolor=\"#D8D8D8 \" align=\"center\"><font size=\"1\"><b>&nbsp;Billable<br/> (Hrs)</b></font></td>"
					+ "<td bgcolor=\"#D8D8D8 \" align=\"center\"><font size=\"1\"><b>&nbsp;Non Billable<br/> (Hrs)</b></font></td>" + "</tr></table>";
			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(a), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase1.add(supList1.get(0));
			document.add(phrase1);

			StringBuilder sb = new StringBuilder();
			sb.append("<table>");
			
			
			Map hmProjectMap = CF.getProjectNameMap(con);
			
			
			pst = con
					.prepareStatement("select *, ta.service_id as serviceid,ta.start_time as startTime,ta.end_time as endTime from task_activity ta left join activity_info ai on ai.task_id = ta.activity_id and ai.emp_id = ta.emp_id where ta.emp_id=? and task_date=? and ta.task_id<?");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			pst.setDate(2, uF.getDateFormat(getTask_date(), DBDATE));
			pst.setInt(3, uF.parseToInt(getTaskId()));

			rs = pst.executeQuery();
			while (rs.next()) {
				String withPerson=null;
				String isManual=null;
				if(rs.getBoolean("is_manual"))
				{
					isManual=" 'M'";
				}
				if(rs.getInt("client_id")>0){
					withPerson=" with Client: "+hmClientName.get(rs.getString("client_id"));
				}
				
				int a_id = uF.parseToInt(rs.getString("activity_id"));
				String ac_name = new FillActivityDetails(request).getActivitName(a_id);
				if (a_id > 0) {
					if (rs.getString("end_time") == null) {
						sb.append("<tr><td colspan=\"2\"><font size=\"1\">I am working on " + ac_name + " from "
								+ uF.getDateFormat(rs.getString("startTime"), DBTIME, CF.getStrReportTimeFormat()));
						sb.append("</font></td>");
						sb.append("<td><font size=\"1\">" + uF.showData((String) hmProjectMap.get(rs.getString("pro_id")), "") + "</font></td>");
						sb.append("<td><font size=\"1\">" + uF.showData((String) hmServiceMap.get(rs.getString("serviceid")), "") + "</font></td>");

					} else {
						sb.append("<tr><td colspan=\"2\"><font size=\"1\">I was working on " + ac_name + " from "
								+ uF.getDateFormat(rs.getString("startTime"), DBTIME, CF.getStrReportTimeFormat()) + " till "
								+ uF.getDateFormat(rs.getString("endTime"), DBTIME, CF.getStrReportTimeFormat()));
						sb.append("</font></td>");
						sb.append("<td><font size=\"1\">" + uF.showData((String) hmProjectMap.get(rs.getString("pro_id")), "") + "</font></td>");
						sb.append("<td><font size=\"1\">" + uF.showData((String) hmServiceMap.get(rs.getString("serviceid")), "") + "</font></td>");
					}
				} else {
					if (rs.getBoolean("issent_report")) {
						sb.append("<tr><td colspan=\"2\"><font size=\"1\">" + rs.getString("activity") + " at "
								+ uF.getDateFormat(rs.getString("startTime"), DBTIME, CF.getStrReportTimeFormat()));
					} else {
						sb.append("<tr><td colspan=\"2\"><font size=\"1\">In " + rs.getString("activity")  +uF.showData(withPerson,"")+ " at "
								+ uF.getDateFormat(rs.getString("startTime"), DBTIME, CF.getStrReportTimeFormat()));
						if (rs.getString("endTime") != null) {
							sb.append(" till " + rs.getString("endTime")+uF.showData(isManual,""));
						}
					}
					sb.append("</font></td>");
					sb.append("<td><font size=\"1\">" + uF.showData((String) hmProjectMap.get(rs.getString("pro_id")), "") + "</font></td>");
					sb.append("<td><font size=\"1\">" + uF.showData((String) hmServiceMap.get(rs.getString("serviceid")), "") + "</font></td>");

				}

				if (rs.getBoolean("is_billable")) {

					double dblTime = 0;
					if (rs.getTime("startTime") != null && rs.getTime("endTime") != null) {
						dblTime = uF.parseToDouble(uF.getTimeDiffInHoursMins(rs.getTime("startTime").getTime(), rs.getTime("endTime").getTime()));
					}

					dblTotalBillableTime += dblTime;
					sb.append("<td align=\"right\"><font size=\"1\">" + dblTime + "</font></td>");
					sb.append("<td><font size=\"1\">&nbsp;</font></td></tr>");

				} else {

					double dblTime = 0;
					if (rs.getTime("startTime") != null && rs.getTime("endTime") != null) {
						dblTime = uF.parseToDouble(uF.getTimeDiffInHoursMins(rs.getTime("startTime").getTime(), rs.getTime("endTime").getTime()));
					}

					dblTotalNonBillableTime += dblTime;
					sb.append("<td><font size=\"1\">&nbsp;</font></td>");
					sb.append("<td  align=\"right\"><font size=\"1\">" + dblTime + "</font></td></tr>");
				}

			}
			rs.close();
			pst.close();

			sb.append("</table>");
			List<Element> supList2 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
			Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase2.add(supList2.get(0));
			document.add(phrase2);

			String c = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" + "<td bgcolor=\"#D8D8D8 \" colspan=\"2\"><font size=\"1\"><b>&nbsp;Total</b></font></td>"
					+ "<td bgcolor=\"#D8D8D8 \"><font size=\"1\"><b>&nbsp;</b></font></td>" + "<td bgcolor=\"#D8D8D8 \" align=\"right\"><font size=\"1\"><b>&nbsp;</b></font></td>"
					+ "<td bgcolor=\"#D8D8D8 \" align=\"right\"><font size=\"1\"><b>&nbsp;" + uF.roundOffInTimeInHoursMins(dblTotalBillableTime) + "</b></font></td>"
					+ "<td bgcolor=\"#D8D8D8 \" align=\"right\"><font size=\"1\"><b>&nbsp;" + uF.roundOffInTimeInHoursMins(dblTotalNonBillableTime) + "</b></font></td>"
					+ "</tr></table>";
			List<Element> supList3 = HTMLWorker.parseToList(new StringReader(c), null);
			Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase3.add(supList3.get(0));
			document.add(phrase3);

			/*
			 * while (rs.next()) {
			 * 
			 * String task_id = rs.getString("task_id");
			 * 
			 * StringBuilder time = new StringBuilder();
			 * if(rs.getTime("start_time")!=null &&
			 * rs.getTime("end_time")!=null){ double dblTime = 0; dblTime =
			 * uF.parseToDouble
			 * (uF.getTimeDiffInHoursMins(rs.getTime("start_time").getTime(),
			 * rs.getTime("end_time").getTime()));
			 * 
			 * dblTotalBillableTime += dblTime;
			 * 
			 * 
			 * 
			 * time.append(dblTime); time.append(""); } else { double dblTime =
			 * 0; if(rs.getTime("start_time")!=null &&
			 * rs.getTime("end_time")!=null){ dblTime =
			 * uF.parseToDouble(uF.getTimeDiffInHoursMins
			 * (rs.getTime("start_time") .getTime(), rs.getTime("end_time")
			 * .getTime())); }
			 * 
			 * dblTotalNonBillableTime += dblTime; time.append(""); }
			 * 
			 * int a_id = uF.parseToInt(rs.getString("activity_id")); int pid =
			 * new FillProjectList().getProjectId(a_id); String ac_name = new
			 * FillActivityDetails().getActivitName(a_id); if (a_id > 0) {
			 * sb.append("<table><tr><td><font size=\"1\">"); if
			 * (rs.getString("end_time") == null) {
			 * 
			 * sb.append("I am working on " + ac_name + " from " +
			 * rs.getString("start_time"));
			 * sb.append("</font></td></tr></table>");
			 * 
			 * sb.append("I am working on " + ac_name + " from " +
			 * rs.getString("start_time")); } else {
			 * 
			 * sb.append("I was working on " + ac_name + " from " +
			 * rs.getString("start_time")); sb.append(" till " +
			 * rs.getString("end_time") +
			 * "</font></td><td align=\"right\"><font size=\"1\">" +
			 * time.toString() + "</font></td></tr></table>");
			 * 
			 * 
			 * sb.append("I was working on " + ac_name + " from " +
			 * rs.getString("start_time") + " till " +
			 * rs.getString("end_time")); } } else {
			 * 
			 * sb.append("<table><tr><td><font size=\"1\">"); sb.append("In " +
			 * rs.getString("activity") + " at " + rs.getString("startTime"));
			 * if (rs.getString("endTime") == null) {
			 * sb.append("</font></td></tr></table> "); } else {
			 * sb.append(" till " + rs.getString("endTime") +
			 * "</td><td align=\"right\"><font size=\"1\">" + time.toString() +
			 * "</font></td></tr></table>"); }
			 * 
			 * 
			 * 
			 * 
			 * sb.append("<table><tr><td><font size=\"1\">"); sb.append("In " +
			 * rs.getString("activity") + " at " + rs.getString("startTime"));
			 * 
			 * sb.append("In " + rs.getString("activity") + " at " +
			 * rs.getString("startTime")); if (rs.getString("endTime") == null)
			 * { } else { sb.append(" till " + rs.getString("endTime")); }
			 * 
			 * } List<Element> supList2 = HTMLWorker.parseToList( new
			 * StringReader(sb.toString()), null); Phrase phrase2 = new
			 * Phrase("", FontFactory.getFont( FontFactory.HELVETICA, 11));
			 * phrase2.add(supList2.get(0)); document.add(phrase2);
			 * 
			 * 
			 * if (rs.getBoolean("is_billable")) {
			 * 
			 * 
			 * double dblTime = 0; if(rs.getTime("start_time")!=null &&
			 * rs.getTime("end_time")!=null){ dblTime =
			 * uF.parseToDouble(uF.getTimeDiffInHoursMins
			 * (rs.getTime("start_time") .getTime(), rs.getTime("end_time")
			 * .getTime())); }
			 * 
			 * dblTotalBillableTime += dblTime; } else {
			 * 
			 * double dblTime = 0; if(rs.getTime("start_time")!=null &&
			 * rs.getTime("end_time")!=null){ dblTime =
			 * uF.parseToDouble(uF.getTimeDiffInHoursMins
			 * (rs.getTime("start_time") .getTime(), rs.getTime("end_time")
			 * .getTime())); }
			 * 
			 * 
			 * dblTotalNonBillableTime += dblTime; String c =
			 * "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
			 * "<tr><td bgcolor=\"#D8D8D8 \"><font size=\"2\"><b>&nbsp;Total</b></font></td>"
			 * +
			 * "<td bgcolor=\"#D8D8D8 \" align=\"right\"><font size=\"2\"><b>&nbsp;"
			 * +dblTotalBillableTime +
			 * "</b></font></td><td bgcolor=\"#D8D8D8 \" align=\"right\"><font size=\"2\"><b>&nbsp;"
			 * +dblTotalNonBillableTime + "</b></font></td></tr></table>";
			 * 
			 * List<Element> supList3 = HTMLWorker.parseToList( new
			 * StringReader(c), null); Phrase phrase3 = new Phrase("",
			 * FontFactory.getFont( FontFactory.HELVETICA, 8));
			 * phrase3.add(supList3.get(0)); document.add(phrase3); } }
			 */

			/*
			 * while (rs.next()) { StringBuilder sb = new StringBuilder();
			 * String task_id = rs.getString("task_id");
			 * 
			 * StringBuilder time = new StringBuilder(); if
			 * (rs.getBoolean("is_billable") && rs.getString("end_time") !=
			 * null) { time.append(uF.getTimeDiffInHoursMins(
			 * rs.getTime("start_time").getTime(),
			 * rs.getTime("end_time").getTime())); } else { time.append(""); }
			 * 
			 * int a_id = uF.parseToInt(rs.getString("activity_id")); int pid =
			 * new FillProjectList().getProjectId(a_id); String ac_name = new
			 * FillActivityDetails().getActivitName(a_id); if (a_id > 0) {
			 * 
			 * sb.append("<table><tr><td><font size=\"1\">"); if
			 * (rs.getString("end_time") == null) { sb.append("I am working on "
			 * + ac_name + " from " + rs.getString("start_time"));
			 * sb.append("</font></td></tr></table>"); } else {
			 * sb.append("I was working on " + ac_name + " from " +
			 * rs.getString("start_time")); sb.append(" till " +
			 * rs.getString("end_time") +
			 * "</font></td><td align=\"right\"><font size=\"1\">" +
			 * time.toString() + "</font></td></tr></table>"); } } else {
			 * sb.append("<table><tr><td><font size=\"1\">"); sb.append("In " +
			 * rs.getString("activity") + " at " + rs.getString("startTime"));
			 * if (rs.getString("endTime") == null) {
			 * sb.append("</font></td></tr></table> "); } else {
			 * sb.append(" till " + rs.getString("endTime") +
			 * "</td><td align=\"right\"><font size=\"1\">" + time.toString() +
			 * "</font></td></tr></table>"); }
			 * 
			 * } List<Element> supList2 = HTMLWorker.parseToList( new
			 * StringReader(sb.toString()), null); Phrase phrase2 = new
			 * Phrase("", FontFactory.getFont( FontFactory.HELVETICA, 11));
			 * phrase2.add(supList2.get(0)); document.add(phrase2); } pst = con
			 * .prepareStatement(
			 * "select sum(actual_hrs) as actual_hrs from task_activity where emp_id=? and is_billable=true and task_date=? "
			 * ); pst.setInt(1, uF.parseToInt(getEmp_id())); if(isOld!=null &&
			 * isOld.equals("true")) { pst.setDate(2,
			 * uF.getDateFormat(uF.getDateFormat(
			 * uF.getCurrentDate(CF.getStrReportDateFormat()) + "", DBDATE,
			 * DATE_FORMAT), DATE_FORMAT)); } else{ pst.setDate(2,
			 * uF.getDateFormat(getTask_date(), DATE_FORMAT)); } rs =
			 * pst.executeQuery(); while (rs.next()) { String c =
			 * "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td bgcolor=\"#D8D8D8 \"><font size=\"2\"><b>&nbsp;Total</b></font></td><td bgcolor=\"#D8D8D8 \" align=\"right\"><font size=\"2\"><b>&nbsp;"
			 * + rs.getDouble("actual_hrs") + "</b></font></td></tr></table>";
			 * List<Element> supList3 = HTMLWorker.parseToList( new
			 * StringReader(c), null); Phrase phrase3 = new Phrase("",
			 * FontFactory.getFont( FontFactory.HELVETICA, 8));
			 * phrase3.add(supList3.get(0)); document.add(phrase3); }
			 */
			// document.add(phrase2);
			document.close();

			/*
			 * File newFile = new File(
			 * "/home/konnect/Desktop/vishwajit/work/PayrollMS/WebContent/TaskTimesheet/TimeSheet.pdf"
			 * ); FileOutputStream fos = new FileOutputStream(newFile);
			 * buffer.writeTo(fos);
			 */
			String filename = "Timesheet_" + hmEmpCodeName.get(getEmp_id()) + "_" + getTask_date() + ".pdf";
			filename = filename.replace(" ", "");
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=" + filename + "");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();

			if (getMailAction() != null && getMailAction().equalsIgnoreCase("sendMail")) {

				byte[] bytes = buffer.toByteArray();
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_GENERATED_TIMESHEET, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setSupervisor(true);
				nF.setStrEmpId(getEmp_id());
				nF.setSupervisor(true);
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName(filename);
				nF.setEmailTemplate(true);
				nF.sendNotifications();
			}

			/*
			 * byte[] bytes = buffer.toByteArray(); Notifications nF = new
			 * Notifications(N_GENERATED_TIMESHEET); nF.setSupervisor(true);
			 * nF.setStrEmpId(getEmp_id()); nF.setSupervisor(true);
			 * nF.setPdfData(bytes);
			 * nF.setStrAttachmentFileName("TimesheetReport.pdf");
			 * nF.sendNotifications();
			 */

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);

		}
	}

	public String getSkillDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String skill = "";
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select skill_id from skills_description where emp_id=?");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
			while (rs.next()) {
				skill = CF.getSkillNameBySkillId(con, rs.getString("skill_id"));
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
		return skill;
	}

	public void generateProjectReportForAdmin() {
		Document document = new Document();
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, buffer);
			document.open();
			String strCompanyLogo = CF.getStrOrgLogo();
			String filePathCompanyLOgo = request.getRealPath("/userImages/" + strCompanyLogo + "");
			String filePathCompanyLOgodefault = request.getRealPath("/userImages/logo_new.png");

			try {
				String img = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"right\"><img src=\"" + filePathCompanyLOgo
						+ "\" width=\"50%\"/> </td></tr></table>";
				List<Element> supList = HTMLWorker.parseToList(new StringReader(img), null);
				Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase.add(supList.get(0));
				document.add(phrase);
			} catch (Exception e) {
				e.printStackTrace();
				String img = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"right\"><img src=\"" + filePathCompanyLOgodefault
						+ "\" width=\"50%\"/> </td></tr></table>";
				List<Element> supList = HTMLWorker.parseToList(new StringReader(img), null);
				Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase.add(supList.get(0));
				document.add(phrase);
			}

			String h = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td><b>" + empname + "'s Timesheet Report</b></td></tr><tr><td></td></tr></table><br/>";
			List<Element> supList0 = HTMLWorker.parseToList(new StringReader(h), null);
			Phrase phrase0 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase0.add(supList0.get(0));
			document.add(phrase0);

			String a = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td bgcolor=\"#D8D8D8 \"><font size=\"2\"><b>&nbsp;Activities</b></font></td><td bgcolor=\"#D8D8D8 \" align=\"right\"><font size=\"2\"><b>&nbsp;Billable (Hrs)</b></font></td></tr></table>";
			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(a), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase1.add(supList1.get(0));
			document.add(phrase1);

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from task_activity where emp_id=? and task_date=? and task_id < ? and is_billable=true");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			pst.setDate(2, uF.getDateFormat(getTask_date(), DBDATE));
			pst.setInt(3, uF.parseToInt(getTaskId()));
			rs = pst.executeQuery();

			while (rs.next()) {
				StringBuilder sb = new StringBuilder();
				String task_id = rs.getString("task_id");

				StringBuilder time = new StringBuilder();
				if (rs.getBoolean("is_billable") && rs.getString("end_time") != null) {
					time.append(uF.getTimeDiffInHoursMins(rs.getTime("start_time").getTime(), rs.getTime("end_time").getTime()));
				} else {

					time.append("");
				}

				int a_id = uF.parseToInt(rs.getString("activity_id"));
				int pid = new FillProjectList(request).getProjectId(a_id);
				String ac_name = new FillActivityDetails(request).getActivitName(a_id);
				if (a_id > 0) {

					sb.append("<table><tr><td><font size=\"1\">");
					if (rs.getString("end_time") == null) {
						sb.append("I am working on " + ac_name + " from " + rs.getString("start_time"));
						sb.append("</font></td></tr></table>");
					} else {
						sb.append("I was working on " + ac_name + " from " + rs.getString("start_time"));
						sb.append(" till " + rs.getString("end_time") + "</font></td><td align=\"right\"><font size=\"1\">" + time.toString() + "</font></td></tr></table>");
					}
				} else {
					sb.append("<table><tr><td><font size=\"1\">");
					sb.append("In " + rs.getString("activity") + " at " + rs.getString("start_time"));
					if (rs.getString("end_time") == null) {
						sb.append("</font></td></tr></table> ");
					} else {
						sb.append(" till " + rs.getString("end_time") + "</td><td align=\"right\"><font size=\"1\">" + time.toString() + "</font></td></tr></table>");
					}

				}
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase2.add(supList2.get(0));
				document.add(phrase2);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select sum(actual_hrs) as actual_hrs from task_activity where emp_id=? and is_billable=true and task_date=? ");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			pst.setDate(2, uF.getDateFormat(uF.getDateFormat(uF.getCurrentDate(CF.getStrReportDateFormat()) + "", DBDATE, DATE_FORMAT), DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {
				String c = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td bgcolor=\"#D8D8D8 \"><font size=\"2\"><b>&nbsp;Total</b></font></td><td bgcolor=\"#D8D8D8 \" align=\"right\"><font size=\"2\"><b>&nbsp;"
						+ rs.getDouble("actual_hrs") + "</b></font></td></tr></table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(c), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
				phrase3.add(supList3.get(0));
				document.add(phrase3);
			}
			rs.close();
			pst.close();

			// document.add(phrase2);
			document.close();

			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=TimesheetReport.pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	// =============================================================
	public void generateProjectSumarryReport() {
		Document document = new Document();
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, buffer);
			document.open();
			String strCompanyLogo = CF.getStrOrgLogo();
			String filePathCompanyLOgo = request.getRealPath("/userImages/" + strCompanyLogo + "");
			String filePathCompanyLOgodefault = request.getRealPath("/userImages/logo_new.png");

			String img = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"right\"><img src=\"" + filePathCompanyLOgodefault
					+ "\" width=\"50%\"/> </td></tr></table>";
			List<Element> supList = HTMLWorker.parseToList(new StringReader(img), null);
			Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase.add(supList.get(0));
			document.add(phrase);

			String h = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td><b>" + empname + "'s Timesheet Report</b></td></tr><tr><td></td></tr></table><br/>";
			List<Element> supList0 = HTMLWorker.parseToList(new StringReader(h), null);
			Phrase phrase0 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase0.add(supList0.get(0));
			document.add(phrase0);

			String a = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td bgcolor=\"#D8D8D8 \"><font size=\"2\"><b>&nbsp;Activities</b></font></td><td bgcolor=\"#D8D8D8 \" align=\"right\"><font size=\"2\"><b>&nbsp;Billable (Hrs)</b></font></td></tr></table>";
			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(a), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase1.add(supList1.get(0));
			document.add(phrase1);

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from task_activity ta,activity_info ai where " + "ta.emp_id=ai.emp_id and "
					+ "ta.task_date=? and ta.activity_id >0 and ai.pro_id=? and ta.emp_id=?");

			pst.setDate(1, uF.getDateFormat(getTask_date(), DBDATE));
			pst.setInt(2, getPro_id());
			pst.setInt(3, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();

			while (rs.next()) {
				StringBuilder sb = new StringBuilder();
				String task_id = rs.getString("task_id");

				StringBuilder time = new StringBuilder();
				if (rs.getBoolean("is_billable") && rs.getString("end_time") != null) {
					time.append(uF.getTimeDiffInHoursMins(rs.getTime("start_time").getTime(), rs.getTime("end_time").getTime()));
				} else {

					time.append("");
				}

				int a_id = uF.parseToInt(rs.getString("activity_id"));
				int pid = new FillProjectList(request).getProjectId(a_id);
				String ac_name = new FillActivityDetails(request).getActivitName(a_id);
				if (a_id > 0) {

					sb.append("<table><tr><td><font size=\"1\">");
					if (rs.getString("end_time") == null) {
						sb.append("I am working on " + ac_name + " from " + rs.getString("start_time"));
						sb.append("</font></td></tr></table>");
					} else {
						sb.append("I was working on " + ac_name + " from " + rs.getString("start_time"));
						sb.append(" till " + rs.getString("end_time") + "</font></td><td align=\"right\"><font size=\"1\">" + time.toString() + "</font></td></tr></table>");
					}
				} else {
					sb.append("<table><tr><td><font size=\"1\">");
					sb.append("In " + rs.getString("activity") + " at " + rs.getString("start_time"));
					if (rs.getString("end_time") == null) {
						sb.append("</font></td></tr></table> ");
					} else {
						sb.append(" till " + rs.getString("end_time") + "</td><td align=\"right\"><font size=\"1\">" + time.toString() + "</font></td></tr></table>");
					}

				}
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase2.add(supList2.get(0));
				document.add(phrase2);
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select sum(actual_hrs) as actual_hrs from task_activity ta,activity_info ai where " + "ta.emp_id=ai.emp_id and "
					+ "ta.task_date=? and ta.activity_id >0 and ai.pro_id=? and ta.emp_id=? ");
			pst.setDate(1, uF.getDateFormat(getTask_date(), DBDATE));
			pst.setInt(2, getPro_id());
			pst.setInt(3, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
			while (rs.next()) {
				String c = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td bgcolor=\"#D8D8D8 \"><font size=\"2\"><b>&nbsp;Total</b></font></td><td bgcolor=\"#D8D8D8 \" align=\"right\"><font size=\"2\"><b>&nbsp;"
						+ rs.getDouble("actual_hrs") + "</b></font></td></tr></table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(c), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
				phrase3.add(supList3.get(0));
				document.add(phrase3);
			}
			rs.close();
			pst.close();

			// document.add(phrase2);
			document.close();

			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=TimesheetReport.pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	// =================================================
	/*
	 * public void generatePdfReport() { Document document =
	 * new Document(); ResultSet rs = null; Connection con =
	 * null; PreparedStatement pst = null; Database db = new Database(); try {
	 * 
	 * ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	 * PdfWriter.getInstance(document, buffer);
	 * document.open();
	 * 
	 * String strCompanyLogo=CF.getStrOrgLogo(); String filePathCompanyLOgo =
	 * request.getRealPath("/userImages/"+strCompanyLogo+""); String
	 * filePathCompanyLOgodefault
	 * =request.getRealPath("/userImages/logo_new.png");
	 * 
	 * try{ String img =
	 * "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"right\"><img src=\""
	 * +filePathCompanyLOgo+"\" width=\"50%\"/> </td></tr></table>";
	 * List<Element> supList = HTMLWorker.parseToList( new StringReader(img),
	 * null); Phrase phrase = new Phrase("",
	 * FontFactory.getFont(FontFactory.HELVETICA, 11));
	 * phrase.add(supList.get(0)); document.add(phrase); }catch (Exception e) {
	 * // TODO: handle exception e.printStackTrace(); String img =
	 * "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"right\"><img src=\""
	 * +filePathCompanyLOgodefault+"\" width=\"50%\"/> </td></tr></table>";
	 * List<Element> supList = HTMLWorker.parseToList( new StringReader(img),
	 * null); Phrase phrase = new Phrase("", FontFactory.getFont(
	 * FontFactory.HELVETICA, 11)); phrase.add(supList.get(0));
	 * document.add(phrase); }
	 * 
	 * String h =
	 * "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td><b>"
	 * +empname
	 * +"'s Timesheet Report</b></td></tr><tr><td></td></tr></table><br/>"; //
	 * String h =
	 * "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td><b>"
	 * +empname
	 * +"'s Timesheet Report</b></td></tr><tr><td></td></tr></table><br/>";
	 * List<Element> supList0 = HTMLWorker.parseToList( new StringReader(h),
	 * null); Phrase phrase0 = new Phrase("", FontFactory.getFont(
	 * FontFactory.HELVETICA, 8)); phrase0.add(supList0.get(0));
	 * document.add(phrase0);
	 * 
	 * 
	 * 
	 * String a =
	 * "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th bgcolor=\"#D8D8D8 \"><b>&nbsp;Activities</b></th><th bgcolor=\"#D8D8D8 \" align=\"right\"><b>&nbsp;Billable (Hrs)</b></th></tr></table>"
	 * ; List<Element> supList1 = HTMLWorker.parseToList( new StringReader(a),
	 * null); Phrase phrase1 = new Phrase("", FontFactory.getFont(
	 * FontFactory.HELVETICA, 11)); phrase1.add(supList1.get(0));
	 * document.add(phrase1); con = db.makeConnection(con); pst = con
	 * .prepareStatement(
	 * "select * from task_activity where emp_id=? and task_date=? and is_billable=true"
	 * ); pst.setInt(1, uF.parseToInt(getEmp_id())); if(isOld!=null &&
	 * isOld.equals("true")){
	 * 
	 * pst.setDate(2, uF.getDateFormat(uF.getDateFormat(
	 * uF.getCurrentDate(CF.getStrReportDateFormat()) + "", DBDATE,
	 * DATE_FORMAT), DATE_FORMAT)); }else{ pst.setDate(2,
	 * uF.getDateFormat(getTask_date(), DATE_FORMAT)); } rs =
	 * pst.executeQuery(); while (rs.next()) { StringBuilder sb = new
	 * StringBuilder(); String task_id = rs.getString("task_id");
	 * 
	 * StringBuilder time = new StringBuilder(); if
	 * (rs.getBoolean("is_billable") && rs.getString("end_time") != null) {
	 * time.append(uF.getTimeDiffInHoursMins(
	 * rs.getTime("start_time").getTime(), rs.getTime("end_time").getTime())); }
	 * else { time.append(""); }
	 * 
	 * int a_id = uF.parseToInt(rs.getString("activity_id")); int pid = new
	 * FillProjectList().getProjectId(a_id); String ac_name = new
	 * FillActivityDetails().getActivitName(a_id); if (a_id > 0) {
	 * 
	 * sb.append("<table><tr><td><font size=\"1\">"); if
	 * (rs.getString("end_time") == null) { sb.append("I am working on " +
	 * ac_name + " from " + rs.getString("start_time"));
	 * sb.append("</font></td></tr></table>"); } else {
	 * sb.append("I was working on " + ac_name + " from " +
	 * rs.getString("start_time")); sb.append(" till " +
	 * rs.getString("end_time") +
	 * "</font></td><td align=\"right\"><font size=\"1\">" + time.toString() +
	 * "</font></td></tr></table>"); } } else {
	 * sb.append("<table><tr><td><font size=\"1\">"); sb.append("In " +
	 * rs.getString("activity") + " at " + rs.getString("start_time")); if
	 * (rs.getString("end_time") == null) {
	 * sb.append("</font></td></tr></table> "); } else { sb.append(" till " +
	 * rs.getString("end_time") + "</td><td align=\"right\"><font size=\"1\">" +
	 * time.toString() + "</font></td></tr></table>"); }
	 * 
	 * } List<Element> supList2 = HTMLWorker.parseToList( new
	 * StringReader(sb.toString()), null); Phrase phrase2 = new Phrase("",
	 * FontFactory.getFont( FontFactory.HELVETICA, 11));
	 * phrase2.add(supList2.get(0)); document.add(phrase2); } pst = con
	 * .prepareStatement(
	 * "select sum(actual_hrs) as actual_hrs from task_activity where emp_id=? and is_billable=true and task_date=? "
	 * ); pst.setInt(1, uF.parseToInt(getEmp_id())); if(isOld!=null &&
	 * isOld.equals("true")) { pst.setDate(2, uF.getDateFormat(uF.getDateFormat(
	 * uF.getCurrentDate(CF.getStrReportDateFormat()) + "", DBDATE,
	 * DATE_FORMAT), DATE_FORMAT)); } else{ pst.setDate(2,
	 * uF.getDateFormat(getTask_date(), DATE_FORMAT)); } rs =
	 * pst.executeQuery(); while (rs.next()) { String c =
	 * "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td bgcolor=\"#D8D8D8 \"><font size=\"2\"><b>&nbsp;Total</b></font></td><td bgcolor=\"#D8D8D8 \" align=\"right\"><font size=\"2\"><b>&nbsp;"
	 * + rs.getDouble("actual_hrs") + "</b></font></td></tr></table>";
	 * List<Element> supList3 = HTMLWorker.parseToList( new StringReader(c),
	 * null); Phrase phrase3 = new Phrase("", FontFactory.getFont(
	 * FontFactory.HELVETICA, 8)); phrase3.add(supList3.get(0));
	 * document.add(phrase3); }
	 * 
	 * // document.add(phrase2); document.close();
	 * 
	 * 
	 * 
	 * 
	 * pst = con.prepareStatement(
	 * "insert into task_activity(task_date, start_time,end_time,emp_id,activity,_comment,sent,is_billable,issent_report) values(?,?,?,?,?,?,'n',?,?)"
	 * ); pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone())); pst.setTime(2,
	 * uF.getCurrentTime(CF.getStrTimeZone())); pst.setTime(3,
	 * uF.getCurrentTime(CF.getStrTimeZone()));
	 * pst.setInt(4,uF.parseToInt(getEmp_id())); pst.setString(5,"Report Sent");
	 * pst.setString(6,"Report sent to "); pst.setBoolean(7,false);
	 * pst.setBoolean(8,true); pst.executeUpdate();
	 * 
	 * 
	 * 
	 * File newFile = new File(
	 * "/home/konnect/Desktop/vishwajit/work/PayrollMS/WebContent/TaskTimesheet/TimeSheet.pdf"
	 * ); FileOutputStream fos = new FileOutputStream(newFile);
	 * buffer.writeTo(fos); response.setContentType("application/pdf");
	 * response.setContentLength(buffer.size());
	 * response.setHeader("Content-Disposition",
	 * "attachment; filename=TimesheetReport.pdf"); ServletOutputStream out =
	 * response.getOutputStream(); buffer.writeTo(out); out.flush();
	 * 
	 * 
	 * byte[] bytes = buffer.toByteArray(); Notifications nF = new
	 * Notifications(N_GENERATED_TIMESHEET); nF.setSupervisor(true);
	 * nF.setStrEmpId(getEmp_id()); nF.setSupervisor(true);
	 * nF.setPdfData(bytes); nF.setStrAttachmentFileName("TimesheetReport.pdf");
	 * nF.sendNotifications();
	 * 
	 * 
	 * 
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }finally {
	 * db.closeConnection(con); db.closeStatements(pst);
	 * 
	 * } }
	 */
	public void generateExcelReport() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);

			HSSFWorkbook hwb = new HSSFWorkbook();
			HSSFSheet sheet = hwb.createSheet("New Sheet");
			HSSFRow rowhead = sheet.createRow((short) 2);
			rowhead.createCell((short) 0).setCellValue("");
			rowhead.createCell((short) 1).setCellValue("Client Name");
			rowhead.createCell((short) 2).setCellValue("Activity");
			rowhead.createCell((short) 3).setCellValue("Billable (Hrs)");
			pst = con.prepareStatement("select * from task_activity where emp_id=? and task_date=? and is_billable=true");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			pst.setDate(2, uF.getDateFormat(uF.getDateFormat(uF.getCurrentDate(CF.getStrReportDateFormat()) + "", DBDATE, DATE_FORMAT), DATE_FORMAT));
			rs = pst.executeQuery();
			int row = 3;
			while (rs.next()) {
				StringBuilder sb = new StringBuilder();
				// String task_id=rs.getString("task_id");
				HSSFRow rowhead1 = sheet.createRow((short) row);
				int a_id = uF.parseToInt(rs.getString("activity_id"));
				// int pid=new FillProjectList().getProjectId(a_id);
				String ac_name = new FillActivityDetails(request).getActivitName(a_id);
				if (a_id > 0) {
					if (rs.getString("end_time") == null) {
						sb.append("I am working on " + ac_name + " from " + rs.getString("start_time"));
					} else {
						sb.append("I was working on " + ac_name + " from " + rs.getString("start_time") + " till " + rs.getString("end_time"));
					}
				} else {
					sb.append("In " + rs.getString("activity") + " at " + rs.getString("start_time"));
					if (rs.getString("end_time") == null) {
					} else {
						sb.append(" till " + rs.getString("end_time"));
					}
				}
				rowhead1.createCell((short) 0).setCellValue("");
				rowhead1.createCell((short) 1).setCellValue("");
				rowhead1.createCell((short) 2).setCellValue(sb.toString());
				if (rs.getBoolean("is_billable") && rs.getString("end_time") != null) {
					rowhead1.createCell((short) 3).setCellValue(uF.getTimeDiffInHoursMins(rs.getTime("start_time").getTime(), rs.getTime("end_time").getTime()));

				} else {
					rowhead1.createCell((short) 3).setCellValue("");
				}
				row++;
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select sum(actual_hrs) as actual_hrs from task_activity where emp_id=? and is_billable=true and task_date=? ");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			pst.setDate(2, uF.getDateFormat(uF.getDateFormat(uF.getCurrentDate(CF.getStrReportDateFormat()) + "", DBDATE, DATE_FORMAT), DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {
				HSSFRow rowhead3 = sheet.createRow((short) row + 1);
				rowhead3.createCell((short) 0).setCellValue("");
				rowhead3.createCell((short) 1).setCellValue("");
				rowhead3.createCell((short) 2).setCellValue("Total");
				rowhead3.createCell((short) 3).setCellValue(rs.getDouble("actual_hrs"));
			}
			rs.close();
			pst.close();
			hwb.write(baos);

			String sheetname = "Timesheet1_" + uF.getDateFormat(uF.getDateFormat(uF.getCurrentDate(CF.getStrReportDateFormat()) + "", DBDATE, DATE_FORMAT), DATE_FORMAT);
			response.setContentType("application/xls");
			response.setContentLength(baos.size());
			response.setHeader("Content-Disposition", "attachment; filename=" + sheetname + ".xls");

			ServletOutputStream out = response.getOutputStream();
			baos.writeTo(out);
			out.flush();

			byte[] bytes = baos.toByteArray();
			// Notifications nF = new Notifications(N_NEW_PAYSLIP_GENERATED);
			String strDomain = request.getServerName().split("\\.")[0];
			Notifications nF = new Notifications(N_GENERATED_TIMESHEET, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			nF.setSupervisor(true);
			nF.setStrEmpId(getEmp_id());
			nF.setSupervisor(true);
			nF.setXlsData(bytes);
			// nF.setPdfData(bytes);
			nF.setStrAttachmentFileName("TimesheetReport.xls");
			nF.setEmailTemplate(true);
			nF.sendNotifications();

		} catch (Exception ex) {

		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);

		}

	}

	public String getEmpName(String empid) {
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		String ename = "";
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			pst = con.prepareStatement("select emp_fname,emp_mname,emp_lname from employee_personal_details where emp_per_id=?");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
			while (rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				ename = rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname");
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
		return ename;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getEmptype() {
		return emptype;
	}

	public void setEmptype(String emptype) {
		this.emptype = emptype;
	}

	public String getTask_date() {
		return task_date;
	}

	public void setTask_date(String task_date) {
		this.task_date = task_date;
	}

	public String getMailAction() {
		return mailAction;
	}

	public void setMailAction(String mailAction) {
		this.mailAction = mailAction;
	}

	public String getIsOld() {
		return isOld;
	}

	public void setIsOld(String isOld) {
		this.isOld = isOld;
	}

	public int getPro_id() {
		return pro_id;
	}

	public void setPro_id(int pro_id) {
		this.pro_id = pro_id;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}
