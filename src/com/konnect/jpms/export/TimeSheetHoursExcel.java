package com.konnect.jpms.export;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class TimeSheetHoursExcel implements ServletRequestAware, ServletResponseAware, IStatements{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static int nInitialRow = 10;
	HSSFWorkbook hwb = null;
	HSSFSheet sheet = null;
	String filePath;
	
	CommonFunctions CF = null;
	UtilityFunctions uF = null;
	HttpSession session;
	
	
	String year;
	String month;
	String wLocation;
	String f_department;
	String f_service;
	
	

	
	public void execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		
		uF = new UtilityFunctions();
		
		
//		String paycycle = request.getParameter("paycycle");
//		String wLocation = request.getParameter("wLocation");
//		String f_department = request.getParameter("f_department");
//		String f_service = request.getParameter("f_service");
//		String level = request.getParameter("level");
		
		
		
		
		
//		SalaryPaidExcel sb = new SalaryPaidExcel("/home/konnect/Desktop/", "Salary");
		
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.MONTH, uF.parseToInt(getMonth())-1);
		cal.set(Calendar.YEAR, uF.parseToInt(getYear()));
		int nActualMin = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
		int nActualMax = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		String strStartDate = nActualMin + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"+ cal.get(Calendar.YEAR);
		String strEndDate = nActualMax + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"+ cal.get(Calendar.YEAR);
		
	
		
		init("Time Sheet"+uF.getDateFormat(strStartDate, DATE_FORMAT, "ddMMyy")+"-"+uF.getDateFormat(strEndDate, DATE_FORMAT, "ddMMyy"));
		
		
		List<String> alHead = new ArrayList<String>();
		Map hmEmpTimeData = new HashMap();
		Map hmWorkingDays = new HashMap();
		Map hmWorkingHours = new HashMap();
		
		alHead.add("Sr. No");
		alHead.add("Emp Name");
		alHead.add("Designation");
		alHead.add("Worked Days");
		alHead.add("Worked Hours");
		
		
		
		getData(hmEmpTimeData, hmWorkingDays, hmWorkingHours, uF);
		addHeaders(alHead, CF, uF);
		
		
		
		
		writeDateFile(hmEmpTimeData, hmWorkingDays, hmWorkingHours, CF, uF );
		
		addLogoImage(CF);
		writeExcelFile(response, "TimeSheet_"+uF.getDateFormat(strStartDate, DATE_FORMAT, "ddMMyy")+"_"+uF.getDateFormat(strEndDate, DATE_FORMAT, "ddMMyy")+".xls");
		
//		sb.writeExcelFile(null, "/home/konnect/Desktop/test.xls");
	
//		return "success";
	
	}
	
	public void getData(Map hmEmpTimeData, Map hmWorkingDays, Map hmWorkingHours, UtilityFunctions uF){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;	
		try {
			
			con = db.makeConnection(con);
			
			
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getMonth()) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(getYear()));
			int nActualMin = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
			int nActualMax = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			String strStartDate = nActualMin + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"+ cal.get(Calendar.YEAR);
			String strEndDate = nActualMax + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"+ cal.get(Calendar.YEAR);
			
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from attendance_details at, employee_official_details eod where eod.emp_id = at.emp_id  and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? ");
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and depart_id = "+uF.parseToInt(getF_department()));
			}
			if(uF.parseToInt(getwLocation())>0){
				sbQuery.append(" and wlocation_id = "+uF.parseToInt(getwLocation()));
			}
			
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and at.service_id = "+uF.parseToInt(getF_service()));
			}
			/*if(uF.parseToInt(getLevel())>0){
				sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id = "+uF.parseToInt(getLevel())+")");
			}*/
			
			sbQuery.append(" order by at.emp_id " );
					
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strStartDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strEndDate, DATE_FORMAT));
			rs = pst.executeQuery();
			
			String strEmpIdOld = null;
			String strEmpIdNew = null;
			Map hmTimeData = new HashMap();
			Map hmHoursData = new HashMap();
			
			List alWorkingDays = new ArrayList();
			double dblTotalHours = 0;
			
			while(rs.next()){
				
				strEmpIdNew = rs.getString("emp_id");

				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					hmTimeData = new HashMap();
					hmHoursData = new HashMap();
					alWorkingDays = new ArrayList();
					dblTotalHours = 0;
				}
				
				
				if(rs.getString("in_out").equalsIgnoreCase("IN")){
					hmTimeData.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_S", uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
				}else if(rs.getString("in_out").equalsIgnoreCase("OUT")){ 
					hmTimeData.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_E", uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
					double dblTime = uF.parseToDouble((String)hmHoursData.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)));
					dblTime += uF.parseToDouble(rs.getString("hours_worked"));
					dblTotalHours += uF.parseToDouble(rs.getString("hours_worked")); 
					hmHoursData.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), uF.formatIntoOneDecimal(dblTime));
				}
				
				
				hmHoursData.put("TOTAL_HOURS", uF.formatIntoOneDecimal(dblTotalHours)); 
				
				
				if(!alWorkingDays.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))){
					alWorkingDays.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
				}
				
				
				hmEmpTimeData.put(strEmpIdNew, hmTimeData);
				hmWorkingDays.put(strEmpIdNew, alWorkingDays);
				hmWorkingHours.put(strEmpIdNew, hmHoursData);
				
				
				strEmpIdOld = strEmpIdNew;
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
	
	
	HSSFFont dayRowBoldfont;
	HSSFFont dayRowNormalfont;
	
	public void init(String sheetName){
		this.hwb = new HSSFWorkbook();		
		this.sheet = hwb.createSheet(sheetName);
		dayRowBoldfont = hwb.createFont();;
		dayRowNormalfont = hwb.createFont();;
	}
	
	public void addLogoImage(CommonFunctions CF) {
		try {
 
//			InputStream is = new FileInputStream(filePath+File.separator+"logo1.jpeg");
//			InputStream is = new FileInputStream("http:////localhost:8080/PayrollMS/userImages/"+CF.getStrOrgLogo());
			
			URL url = new URL("http://"+CF.getStrEmailLocalHost()+":8080"+request.getContextPath()+"/userImages/"+CF.getStrOrgLogo());
			URLConnection urlcon = url.openConnection();
			InputStream is = urlcon.getInputStream();
			
			
			
			byte[] bytes = IOUtils.toByteArray(is);
			int pictureIdx = hwb.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
			is.close();

			CreationHelper helper = hwb.getCreationHelper();
			Drawing drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = helper.createClientAnchor();
			anchor.setCol1(1);
			anchor.setRow1(1);
			Picture pict = drawing.createPicture(anchor, pictureIdx);
			pict.resize();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void writeDateFile(Map hmEmpTimeData, Map hmWorkingDays, Map hmWorkingHours, CommonFunctions CF, UtilityFunctions uF ) {
		Database db=new Database();
		db.setRequest(request);
		Connection con=null;
		try {
			
			con=db.makeConnection(con);
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getMonth()) -1);
			cal.set(Calendar.YEAR, uF.parseToInt(getYear()));
			int nActualMin = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
			int nActualMax = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			String strStartDate = nActualMin + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"+ cal.get(Calendar.YEAR);
			String strEndDate = nActualMax + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"+ cal.get(Calendar.YEAR);
			
			
			
			Map hmEmpNames = CF.getEmpNameMap(con,null, null);
			Map hmEmpDesig = CF.getEmpDesigMap(con);
			Map hmWeekEnd = CF.getWeekEndList(con);
			Map hmEmpWLocation = CF.getEmpWlocationMap(con);
			Map hmHolidays = new HashMap();
			Map hmHolidayDates = new HashMap();
			CF.getHolidayList(con,request,strStartDate, strEndDate, CF, hmHolidayDates, hmHolidays, true);
			
			
			HSSFRow rowhead = null;				
			HSSFCell cell = null;
			Excelstyle  estyle = new Excelstyle(hwb);
			
			
			Set set = hmEmpTimeData.keySet();
			Iterator it = set.iterator();
			int nRowCount = 0;
			
			
			Map hmTotal = new HashMap();
			StringBuilder sbHoursData = new StringBuilder();
			
			while(it.hasNext()){
				String strEmpId = (String)it.next();
				Map hmHoursData = (Map)hmWorkingHours.get(strEmpId) ;
				List alWorkingDays = (List)hmWorkingDays.get(strEmpId) ;
				if(alWorkingDays==null)alWorkingDays=new ArrayList();
				rowhead = sheet.createRow(nInitialRow+2+2+nRowCount++);
				
				int count = 1;
				cell = rowhead.createCell(count);
				cell.setCellValue(nRowCount);
				sheet.setColumnWidth(count++, 10 * 256);
				cell.setCellStyle(getStyle(true, false, true, true, true, true, true, false, false, false));
				
				
				cell = rowhead.createCell(count);
				cell.setCellValue((String)hmEmpNames.get(strEmpId));
				sheet.setColumnWidth(count++, 25 * 256);
				cell.setCellStyle(getStyle(false, false, true, true, true, true, true, false, false, false));
				
				cell = rowhead.createCell(count);
				cell.setCellValue((String)hmEmpDesig.get(strEmpId));
				sheet.setColumnWidth(count++, 25 * 256);
				cell.setCellStyle(getStyle(false, false, true, true, true, true, true, false, false, false));
				
				cell = rowhead.createCell(count);
				cell.setCellValue(alWorkingDays.size());
				sheet.setColumnWidth(count++, 20 * 256);
				cell.setCellStyle(getStyle(false, true, true, true, true, true, true, false, false, false));
				
				cell = rowhead.createCell(count);
				cell.setCellValue(uF.parseToDouble((String)hmHoursData.get("TOTAL_HOURS")));
				sheet.setColumnWidth(count++, 20 * 256);
				cell.setCellStyle(getStyle(false, true, true,  true, true, true, true, false, false, false));
				
				for(int x=nActualMin; x<=nActualMax; x++){
					
					sbHoursData.append(x + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"+ cal.get(Calendar.YEAR));
					
					cell = rowhead.createCell(count+x);
					if(uF.parseToDouble((String)hmHoursData.get(uF.getDateFormat(sbHoursData.toString(), DATE_FORMAT, DATE_FORMAT)))==0){
						cell.setCellValue("");
					}else{
						cell.setCellValue(uF.parseToDouble((String)hmHoursData.get(uF.getDateFormat(sbHoursData.toString(), DATE_FORMAT, DATE_FORMAT))));
					}
					
					sheet.setColumnWidth(count+x, 10 * 256);
					
					if(hmWeekEnd.containsKey(uF.getDateFormat(sbHoursData.toString(), DATE_FORMAT, "EEEE").toUpperCase()+"_"+(String)hmEmpWLocation.get(strEmpId))){
						cell.setCellStyle(getStyle(true, false, false, true, true, true, true, true, false, false));
					}else if(uF.parseToDouble((String)hmHoursData.get(uF.getDateFormat(sbHoursData.toString(), DATE_FORMAT, DATE_FORMAT)))==0){
						cell.setCellStyle(getStyle(true, false, false, true, true, true, true, false, true, false));
					}else if(hmHolidayDates.containsKey(uF.getDateFormat(sbHoursData.toString(), DATE_FORMAT, CF.getStrReportDateFormat()))){
						cell.setCellStyle(getStyle(true, false, false, true, true, true, true, false, false, true));
					}else{
						cell.setCellStyle(getStyle(true, false, false, true, true, true, true, false, false, false));
					}

					
					sbHoursData.replace(0, sbHoursData.length(), "");
					
				}
				
			}
			
			
			
			rowhead = sheet.createRow(nInitialRow+2+3+nRowCount++);
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}
	
	
	public void writeExcelFile(HttpServletResponse response, String fileName) {

		FileOutputStream fileOut = null;
		try {

			
			/*
			fileOut = new FileOutputStream(fileName);
			hwb.write(fileOut);
			fileOut.close();*/
			
			
//			ServletOutputStream op = response.getOutputStream();
//			response.setContentType("application/vnd.ms-excel:UTF-8");
//			response.setContentLength(hwb.getBytes().length);
//			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
//			op.write(hwb.getBytes());
//			op.flush();
//			op.close();
			
			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			hwb.write(bout);
			bout.close();
			
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(bout.size());
			
			ServletOutputStream op = response.getOutputStream();
			op.write(bout.toByteArray());
			op.flush();
			bout.close();
			op.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	public void addHeaders(List<String> alHead, CommonFunctions CF, UtilityFunctions uF){
		
		try {
			
			
			HSSFRow rowhead = null;				
			HSSFCell cell = null;
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getMonth()) -1);
			cal.set(Calendar.YEAR, uF.parseToInt(getYear()));
			int nActualMin = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
			int nActualMax = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			String strStartDate = nActualMin + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"+ cal.get(Calendar.YEAR);
			String strEndDate = nActualMax + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"+ cal.get(Calendar.YEAR);
			
			
			rowhead = sheet.createRow(nInitialRow);
			cell = rowhead.createCell(5);
			cell.setCellValue("Timesheet from "+uF.getDateFormat(strStartDate, DATE_FORMAT, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(strEndDate, DATE_FORMAT, CF.getStrReportDateFormat()));
			cell.setCellStyle(getStyle(true, false, true, false, false, false, false, false, false, false));		
					
					
			rowhead = sheet.createRow(nInitialRow+2);
			int x=0;
			for(; x<alHead.size(); x++){
				cell = rowhead.createCell(x+1);
				cell.setCellValue(alHead.get(x));
				cell.setCellStyle(getStyle(true, false, true, true, true, true, true, false, false, false));
			}
			
			
			for(int y=nActualMin; y<=nActualMax; y++){
				cell = rowhead.createCell(x+y+1);
				cell.setCellValue(y);
				cell.setCellStyle(getStyle(true, false, true, true, true, true, true, false, false, false));
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public HSSFCellStyle getStyle(boolean isCenterAlign, boolean isRightAlign, boolean isBold, boolean isTopBorder, boolean isBottomBorder, boolean isRightBorder, boolean isLeftBorder, boolean isWeekEnd, boolean isNoWork, boolean isHoliday){
		
		HSSFCellStyle cellStyle= hwb.createCellStyle();
		
		
		if(isBold){
			dayRowBoldfont.setFontName("Verdana");
			dayRowBoldfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			cellStyle.setFont(dayRowBoldfont);
		}else{
			dayRowBoldfont.setFontName("Verdana");
			dayRowNormalfont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			cellStyle.setFont(dayRowNormalfont);
		}
		
		
		cellStyle.setWrapText(false);
		
		if(isRightAlign){
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		}
		if(isCenterAlign){
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		}

		if(isTopBorder){
			cellStyle.setBorderTop((short) 1);
		}
		
		if(isBottomBorder){
			cellStyle.setBorderBottom((short) 1);
		}
		
		if(isRightBorder){
			cellStyle.setBorderRight((short) 1);
		}
		
		if(isLeftBorder){
			cellStyle.setBorderLeft((short) 1);
		}
		
		if(isWeekEnd){
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
			cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		}
		if(isNoWork){
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
			cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
		}
		if(isHoliday){
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
			cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
		}
		
		return cellStyle;
		
	}

	HttpServletRequest request;
	HttpServletResponse response;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
		
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getwLocation() {
		return wLocation;
	}

	public void setwLocation(String wLocation) {
		this.wLocation = wLocation;
	}

	public String getF_department() {
		return f_department;
	}

	public void setF_department(String f_department) {
		this.f_department = f_department;
	}

	public String getF_service() {
		return f_service;
	}

	public void setF_service(String f_service) {
		this.f_service = f_service;
	}

	
}
