package com.konnect.jpms.task;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
public class ExportToExcelTimeSheet extends ActionSupport implements
		ServletRequestAware, ServletResponseAware, IStatements {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	Map session;
	HSSFWorkbook hwb = new HSSFWorkbook();
	List<List<String>> outerList1 = new ArrayList<List<String>>();
	CommonFunctions CF;
	String emp_id;
	String task_date;    
	String emptype;
	int pro_id;
	String taskId;

	public String execute() {

		session = ActionContext.getContext().getSession();
		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		generateExcelReport();
		
		return "";
	}

	public String getSkillDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String skill = "";
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select skill_id from skills_description where emp_id=?");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
			while(rs.next())
			{
				skill=CF.getSkillNameBySkillId(con, rs.getString("skill_id"));
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
	public void generateExcelReport(){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try{

			con = db.makeConnection(con);

			Map hmServiceMap = CF.getServicesMap(con, false);
			Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null,null);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			Map<String, String> hmClientName =new FillTaskRelatedMap(request).getClientNameMap();
			
			/*HSSFWorkbook hwb = new HSSFWorkbook();
			HSSFSheet sheet = hwb.createSheet("New Sheet");
			
			HSSFCellStyle style = hwb.createCellStyle();
	        style.setBorderTop((short) 6); // double lines border
	        style.setBorderBottom((short) 1); // single line border
	        style.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
			
	        HSSFFont font = hwb.createFont();
	        font.setFontName(HSSFFont.FONT_ARIAL);
	        font.setFontHeightInPoints((short) 20);
	        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	        font.setColor(HSSFColor.BLUE.index);
	        style.setFont(font);
						
			HSSFRow rowhead = sheet.createRow((short) 2);
			rowhead.createCell((short) 0).setCellValue("");
			HSSFCell cell =rowhead.createCell((short) 1);
			cell.setCellValue("Client Name");
			cell.setCellStyle(style);        
			rowhead.createCell((short) 2).setCellValue("Activity");
			rowhead.createCell((short) 3).setCellValue("Billable (Hrs)");
			*/
			HSSFWorkbook hwb = new HSSFWorkbook();
			HSSFSheet sheet = hwb.createSheet("Daily Report");
			HSSFCellStyle style = hwb.createCellStyle();
	        HSSFFont font = hwb.createFont();
	        font.setFontName(HSSFFont.FONT_ARIAL);
	        font.setFontHeightInPoints((short) 10);
	        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	        font.setColor(HSSFColor.BLACK.index);
	        style.setFont(font);
	        style.setAlignment(CellStyle.ALIGN_LEFT);
	        HSSFRow rowheada =null;
	        
	         rowheada = sheet.createRow((int) 2);
			rowheada.createCell((int) 0).setCellValue("");
			HSSFCell cella1 =rowheada.createCell((int) 1);
			cella1.setCellValue("Employee Name");
			cella1.setCellStyle(style);
			HSSFCell cella2 =rowheada.createCell((int) 2);
			cella2.setCellValue(hmEmpCodeName.get(getEmp_id()));
			
//			cella2.setCellStyle(style);  
			 rowheada = sheet.createRow((int) 3);
			HSSFCell cellb1 =rowheada.createCell((int) 1);
			cellb1.setCellValue("Employee Code");
			cellb1.setCellStyle(style);
			HSSFCell cellb2 =rowheada.createCell((int) 2);
			cellb2.setCellValue(hmEmpCode.get(getEmp_id()));
			
			 rowheada = sheet.createRow((int) 4);
			HSSFCell cellc1 =rowheada.createCell((int) 1);
			cellc1.setCellValue("Skills");
			cellc1.setCellStyle(style);
			HSSFCell cellc2 =rowheada.createCell((int) 2);
			cellc2.setCellValue(getSkillDetails());
			
			 rowheada = sheet.createRow((int) 5);
			HSSFCell celld1 =rowheada.createCell((int) 1);
			celld1.setCellValue("Date");
			celld1.setCellStyle(style);
			HSSFCell celld2 =rowheada.createCell((int) 2);
			celld2.setCellValue(uF.getDateFormat(getTask_date(), DBDATE, CF.getStrReportDateFormat()));
			
			HSSFRow rowhead = sheet.createRow((int) 8);
			rowhead.createCell((int) 0).setCellValue("");
			
			HSSFCell cell1 =rowhead.createCell((short) 1);
			cell1.setCellValue("Task / Activity");
			cell1.setCellStyle(style);   
			sheet.setColumnWidth(1, 30 * 256);
			
			
			HSSFCell cell2 =rowhead.createCell((short) 2);
			cell2.setCellValue("Project Description");
			cell2.setCellStyle(style);
			
			sheet.setColumnWidth(2, 30 * 256);
			
			HSSFCell cell3 =rowhead.createCell((short) 3);
			cell3.setCellValue("Service");
			cell3.setCellStyle(style);
			
			sheet.setColumnWidth(3, 20 * 256);
			
			HSSFCell cell4 =rowhead.createCell((short) 4);
			cell4.setCellValue("Billable (Hrs)");
			cell4.setCellStyle(style);
			sheet.setColumnWidth(4, 20 * 256);
			
			
			HSSFCell cell5 =rowhead.createCell((short) 5);
			cell5.setCellValue("Non Billable (Hrs)");
			cell5.setCellStyle(style);
			sheet.setColumnWidth(5, 20 * 256);
			
			
			
			Map hmProjectMap = CF.getProjectNameMap(con);
			
			
			pst = con.prepareStatement("select *, ta.service_id as serviceid,ta.start_time as startTime,ta.end_time as endTime from task_activity ta left join activity_info ai on ai.task_id = ta.activity_id and ai.emp_id = ta.emp_id where ta.emp_id=? and task_date=? and ta.task_id<?");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			pst.setDate(2, uF.getDateFormat(getTask_date(),DBDATE));
			pst.setInt(3, uF.parseToInt(getTaskId()));
			
			rs = pst.executeQuery();
			int row = 10;
			
			double dblTotalBillableTime = 0;
			double dblTotalNonBillableTime = 0;
			
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
				StringBuilder sb = new StringBuilder();
				// String task_id=rs.getString("task_id");
				HSSFRow rowhead1 = sheet.createRow((short) row);
				int a_id = uF.parseToInt(rs.getString("activity_id"));
				// int pid=new FillProjectList().getProjectId(a_id);
				String ac_name = new FillActivityDetails(request).getActivitName(a_id);
				if (a_id > 0) {
					if (rs.getString("endTime") == null) {
						sb.append("I am working on " + ac_name + " from "
								+ uF.getDateFormat(rs.getString("startTime"), DBTIME, CF.getStrReportTimeFormat()));
					} else {
						sb.append("I was working on " + ac_name + " from "
								+ uF.getDateFormat(rs.getString("startTime"), DBTIME, CF.getStrReportTimeFormat()) + " till "
								+ uF.getDateFormat(rs.getString("endTime"), DBTIME, CF.getStrReportTimeFormat()));
					}
				} else {
					if(rs.getBoolean("issent_report")){
						sb.append("" + rs.getString("activity") + " at "
								+ uF.getDateFormat(rs.getString("startTime"), DBTIME, CF.getStrReportTimeFormat()));
					}else {
						sb.append("In " + rs.getString("activity")   +uF.showData(withPerson,"")+ " at "
							+ uF.getDateFormat(rs.getString("startTime"), DBTIME, CF.getStrReportTimeFormat())+ 
							((uF.getDateFormat(rs.getString("endTime"), DBTIME, CF.getStrReportTimeFormat()) != null)?" " +
									"till " + uF.getDateFormat(rs.getString("endTime"), DBTIME, CF.getStrReportTimeFormat()):"")+uF.showData(isManual,""));
					}
				}
				rowhead1.createCell((short) 0).setCellValue("");
				rowhead1.createCell((short) 1).setCellValue(sb.toString());
				sheet.autoSizeColumn(2,true);
				rowhead1.createCell((short) 2).setCellValue(uF.showData((String)hmProjectMap.get(rs.getString("pro_id")), ""));
				rowhead1.createCell((short) 3).setCellValue(uF.showData((String)hmServiceMap.get(rs.getString("serviceid")), ""));
				
				
				if (rs.getBoolean("is_billable")) {
					

					double dblTime = 0;
					if(rs.getTime("startTime")!=null && rs.getTime("endTime")!=null){
						dblTime = uF.parseToDouble(uF.getTimeDiffInHoursMins(rs.getTime("startTime")
								.getTime(), rs.getTime("endTime")
								.getTime()));
					}
					
					dblTotalBillableTime += dblTime;
					
					rowhead1.createCell((short) 4).setCellValue(dblTime);

					rowhead1.createCell((short) 5).setCellValue("");
				} else {
					
					double dblTime = 0;
					if(rs.getTime("startTime")!=null && rs.getTime("endTime")!=null){
						dblTime = uF.parseToDouble(uF.getTimeDiffInHoursMins(rs.getTime("startTime")
								.getTime(), rs.getTime("endTime")
								.getTime()));
					}
					
					
					dblTotalNonBillableTime += dblTime;
					
					rowhead1.createCell((short) 4).setCellValue("");
					rowhead1.createCell((short) 5).setCellValue(dblTime);
				}
				row++;
			}
			rs.close();
			pst.close();
			/*pst = con
					.prepareStatement("select sum(actual_hrs) as actual_hrs from task_activity where emp_id=? and is_billable=true and task_date=? ");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			pst.setDate(2, uF.getDateFormat(uF.getDateFormat(
					uF.getCurrentDate(CF.getStrReportDateFormat()) + "",
					DBDATE, DATE_FORMAT), DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {
				HSSFRow rowhead3 = sheet.createRow((short) row + 1);
				rowhead3.createCell((short) 0).setCellValue("");
				rowhead3.createCell((short) 1).setCellValue("");
				rowhead3.createCell((short) 2).setCellValue("Total");
				rowhead3.createCell((short) 3).setCellValue(
						rs.getDouble("actual_hrs"));
				
				
				HSSFRow rowhead3 = sheet.createRow((short) row + 1);
				rowhead3.createCell((short) 0).setCellValue("");
				rowhead3.createCell((short) 1).setCellValue("");
				
				HSSFCell cell7 =rowhead3.createCell((short) 2);
				cell7.setCellValue("Total (Hrs)");
				cell7.setCellStyle(style);       
				
				HSSFCell cell8 =rowhead3.createCell((short) 3);
				cell8.setCellValue(rs.getDouble("actual_hrs"));
				cell8.setCellStyle(style); 
			}
			
			*/
			
			
			
			HSSFRow rowhead3 = sheet.createRow((short) row + 1);
			rowhead3.createCell((short) 0).setCellValue("");
			rowhead3.createCell((short) 1).setCellValue("");
			
			HSSFCell cell7 =rowhead3.createCell((short) 3);
			cell7.setCellValue("Total (Hrs)");
			cell7.setCellStyle(style);       
			
			HSSFCell cell8 =rowhead3.createCell((short) 4);
			cell8.setCellValue(uF.roundOffInTimeInHoursMins(dblTotalBillableTime));
			cell8.setCellStyle(style); 
			
			
			HSSFCell cell9 =rowhead3.createCell((short) 5);
			cell9.setCellValue(uF.roundOffInTimeInHoursMins(dblTotalNonBillableTime));
			cell9.setCellStyle(style);
			
			hwb.write(baos);
//			String filename="Timesheet_"+getEmpName(getEmp_id())+"_"+getTask_date()+".xls";
			String filename="Timesheet_"+hmEmpCodeName.get(getEmp_id())+"_"+getTask_date()+".xls";
			filename=filename.replace(" ","");
			System.out.println("timesheet name===>"+filename);

			response.setContentType("application/xls");
			response.setContentLength(baos.size());
			response.setHeader("Content-Disposition", "attachment; filename="+filename+"");

			ServletOutputStream out = response.getOutputStream();
			baos.writeTo(out);
			out.flush();
		
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	public void generateExcelReportFromAdmin() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			con = db.makeConnection(con);

			HSSFWorkbook hwb = new HSSFWorkbook();
			HSSFSheet sheet = hwb.createSheet("New Sheet");
			HSSFCellStyle style = hwb.createCellStyle();
	        HSSFFont font = hwb.createFont();
	        font.setFontName(HSSFFont.FONT_ARIAL);
	        font.setFontHeightInPoints((short) 10);
	        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	        font.setColor(HSSFColor.BLACK.index);
	        style.setFont(font);
	        style.setAlignment(CellStyle.ALIGN_LEFT);
			
			HSSFRow rowhead = sheet.createRow((short) 2);
			rowhead.createCell((short) 0).setCellValue("");
			HSSFCell cell =rowhead.createCell((short) 1);
			cell.setCellValue("Client Name");
			cell.setCellStyle(style);        
			HSSFCell cell2 =rowhead.createCell((short) 2);
			cell2.setCellValue("Activity");
			cell2.setCellStyle(style);
			HSSFCell cell3 =rowhead.createCell((short) 3);
			cell3.setCellValue("Billable (Hrs)");
			cell3.setCellStyle(style);
			
			pst = con.prepareStatement("select * from task_activity where emp_id=? and task_date=? and is_billable=true");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			pst.setDate(2, uF.getDateFormat(getTask_date(), DBDATE));
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
						sb.append("I am working on " + ac_name + " from "
								+ rs.getString("start_time"));
					} else {
						sb.append("I was working on " + ac_name + " from "
								+ rs.getString("start_time") + " till "
								+ rs.getString("end_time"));
					}
				} else {
					sb.append("In " + rs.getString("activity") + " at "
							+ rs.getString("start_time"));
					if (rs.getString("end_time") == null) {
					} else {
						sb.append(" till " + rs.getString("end_time"));
					}
				}
				rowhead1.createCell((short) 0).setCellValue("");
				rowhead1.createCell((short) 1).setCellValue("");
				rowhead1.createCell((short) 2).setCellValue(sb.toString());
				if (rs.getBoolean("is_billable")
						&& rs.getString("end_time") != null) {
					rowhead1.createCell((short) 3).setCellValue(
							uF.getTimeDiffInHoursMins(rs.getTime("start_time")
									.getTime(), rs.getTime("end_time")
									.getTime()));

				} else {
					rowhead1.createCell((short) 3).setCellValue("");
				}
				row++;
			}
			rs.close();
			pst.close();
			
			pst = con
					.prepareStatement("select sum(actual_hrs) as actual_hrs from task_activity where emp_id=? and is_billable=true and task_date=? ");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			pst.setDate(2, uF.getDateFormat(uF.getDateFormat(
					uF.getCurrentDate(CF.getStrReportDateFormat()) + "",
					DBDATE, DATE_FORMAT), DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {
				/*HSSFRow rowhead3 = sheet.createRow((short) row + 1);
				rowhead3.createCell((short) 0).setCellValue("");
				rowhead3.createCell((short) 1).setCellValue("");
				rowhead3.createCell((short) 2).setCellValue("Total");
				rowhead3.createCell((short) 3).setCellValue(
						rs.getDouble("actual_hrs"));*/
				HSSFRow rowhead3 = sheet.createRow((short) row + 1);
				rowhead3.createCell((short) 0).setCellValue("");
				rowhead3.createCell((short) 1).setCellValue("");
				HSSFCell cell4 =rowhead3.createCell((short) 2);
				cell4.setCellValue("Total (Hrs)");
				cell4.setCellStyle(style);       
				HSSFCell cell5 =rowhead3.createCell((short) 3);
				cell5.setCellValue(rs.getDouble("actual_hrs"));
				cell5.setCellStyle(style);  
			}
			rs.close();
			pst.close();
			hwb.write(baos);

			String sheetname = "TimeSheet_"
					+ uF.getDateFormat(
							uF.getDateFormat(
									uF.getCurrentDate(CF
											.getStrReportDateFormat()) + "",
									DBDATE, DATE_FORMAT), DATE_FORMAT);
			response.setContentType("application/xls");
			response.setContentLength(baos.size());
			response.setHeader("Content-Disposition", "attachment; filename=TimeSheet.xls");

			ServletOutputStream out = response.getOutputStream();
			baos.writeTo(out);
			out.flush();
		} catch (Exception ex) {

		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	//===============================================================
	public void generateProjectSumarryExcelReport() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			con = db.makeConnection(con);

			HSSFWorkbook hwb = new HSSFWorkbook();
			HSSFSheet sheet = hwb.createSheet("New Sheet");
			HSSFCellStyle style = hwb.createCellStyle();
	        HSSFFont font = hwb.createFont();
	        font.setFontName(HSSFFont.FONT_ARIAL);
	        font.setFontHeightInPoints((short) 10);
	        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	        font.setColor(HSSFColor.BLACK.index);
	        style.setFont(font);
	        style.setAlignment(CellStyle.ALIGN_LEFT);
			
			HSSFRow rowhead = sheet.createRow((short) 2);
			rowhead.createCell((short) 0).setCellValue("");
			HSSFCell cell =rowhead.createCell((short) 1);
			cell.setCellValue("Client Name");
			cell.setCellStyle(style);        
			HSSFCell cell2 =rowhead.createCell((short) 2);
			cell2.setCellValue("Activity");
			cell2.setCellStyle(style);
			HSSFCell cell3 =rowhead.createCell((short) 3);
			cell3.setCellValue("Billable (Hrs)");
			cell3.setCellStyle(style);
			
			/*pst = con.prepareStatement("select * from task_activity where emp_id=? and task_date=? and is_billable=true");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			pst.setDate(2, uF.getDateFormat(getTask_date(), DBDATE));*/
			pst = con.prepareStatement("select * from task_activity ta,activity_info ai where " +
					"ta.emp_id=ai.emp_id and " +
					"ta.task_date=? and ta.activity_id >0 and ai.pro_id=? and ta.emp_id=?");
			
			pst.setDate(1, uF.getDateFormat(getTask_date(), DBDATE));
			pst.setInt(2,getPro_id());
			pst.setInt(3, uF.parseToInt(getEmp_id()));
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
						sb.append("I am working on " + ac_name + " from "
								+ rs.getString("start_time"));
					} else {
						sb.append("I was working on " + ac_name + " from "
								+ rs.getString("start_time") + " till "
								+ rs.getString("end_time"));
					}
				} else {
					sb.append("In " + rs.getString("activity") + " at "
							+ rs.getString("start_time"));
					if (rs.getString("end_time") == null) {
					} else {
						sb.append(" till " + rs.getString("end_time"));
					}
				}
				rowhead1.createCell((short) 0).setCellValue("");
				rowhead1.createCell((short) 1).setCellValue("");
				rowhead1.createCell((short) 2).setCellValue(sb.toString());
				if (rs.getBoolean("is_billable")
						&& rs.getString("end_time") != null) {
					rowhead1.createCell((short) 3).setCellValue(
							uF.getTimeDiffInHoursMins(rs.getTime("start_time")
									.getTime(), rs.getTime("end_time")
									.getTime()));

				} else {
					rowhead1.createCell((short) 3).setCellValue("");
				}
				row++;
			}
			rs.close();
			pst.close();
			/*pst = con
					.prepareStatement("select sum(actual_hrs) as actual_hrs from task_activity where emp_id=? and is_billable=true and task_date=? ");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			pst.setDate(2, uF.getDateFormat(uF.getDateFormat(
					uF.getCurrentDate(CF.getStrReportDateFormat()) + "",
					DBDATE, DATE_FORMAT), DATE_FORMAT));*/
			pst = con.prepareStatement("select sum(actual_hrs) as actual_hrs from task_activity ta,activity_info ai where " +
					"ta.emp_id=ai.emp_id and " +
					"ta.task_date=? and ta.activity_id >0 and ai.pro_id=? and ta.emp_id=? ");
			pst.setDate(1, uF.getDateFormat(getTask_date(), DBDATE));
			pst.setInt(2,getPro_id());
			pst.setInt(3, uF.parseToInt(getEmp_id()));
			
			rs = pst.executeQuery();
			while (rs.next()) {
				/*HSSFRow rowhead3 = sheet.createRow((short) row + 1);
				rowhead3.createCell((short) 0).setCellValue("");
				rowhead3.createCell((short) 1).setCellValue("");
				rowhead3.createCell((short) 2).setCellValue("Total");
				rowhead3.createCell((short) 3).setCellValue(
						rs.getDouble("actual_hrs"));*/
				HSSFRow rowhead3 = sheet.createRow((short) row + 1);
				rowhead3.createCell((short) 0).setCellValue("");
				rowhead3.createCell((short) 1).setCellValue("");
				HSSFCell cell4 =rowhead3.createCell((short) 2);
				cell4.setCellValue("Total (Hrs)");
				cell4.setCellStyle(style);       
				HSSFCell cell5 =rowhead3.createCell((short) 3);
				cell5.setCellValue(rs.getDouble("actual_hrs"));
				cell5.setCellStyle(style);  
			}
			rs.close();
			pst.close();
			hwb.write(baos);

			String sheetname = "TimeSheet_"
					+ uF.getDateFormat(
							uF.getDateFormat(
									uF.getCurrentDate(CF
											.getStrReportDateFormat()) + "",
									DBDATE, DATE_FORMAT), DATE_FORMAT);
			response.setContentType("application/xls");
			response.setContentLength(baos.size());
			response.setHeader("Content-Disposition", "attachment; filename=TimeSheet.xls");

			ServletOutputStream out = response.getOutputStream();
			baos.writeTo(out);
			out.flush();
		} catch (Exception ex) {

		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	//====================================================
	public int getPro_id() {
		return pro_id;
	}

	public void setPro_id(int pro_id) {
		this.pro_id = pro_id;
	}
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	public Map getSession() {
		return session;
	}

	public void setSession(Map session) {
		this.session = session;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getTask_date() {
		return task_date;
	}

	public void setTask_date(String task_date) {
		this.task_date = task_date;
	}

	public String getEmptype() {
		return emptype;
	}

	public void setEmptype(String emptype) {
		this.emptype = emptype;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
}