package com.konnect.jpms.export;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PaySlips extends ActionSupport implements ServletRequestAware, IStatements {
 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strD1;
	String strD2;
	CommonFunctions CF = null;
	
	String strUserType;
	String strSessionEmpId;
	
//	Map<String, String> hmEmpFullNameMap = null;
//	Map<String, String> hmEmpCodeMap = null;
//	Map<String, String> hmEmpEmailMap = null;
//	Map<String, String> hmEmpContactMap = null;
	 
	public String execute(String strD1, String strD2) throws Exception {
		 session = request.getSession(true);

		this.strD1 = strD1;
		this.strD2 = strD2;
		
		request.setAttribute(PAGE, PGeneratePaySlip);
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
//		CF = new CommonFunctions();
//		CF.setStrTimeZone("Australia/Sydney");
//		CF.setStrCURRENCY_SHORT("$");
//		CF.setStrCURRENCY_FULL("$");
//		CF.setStrReportDateFormat("dd/MM/yyyy");
//		CF.setStrReportTimeFormat("HH:mm");
//		CF.setStrReportDayFormat("EEEE");
//		CF.setStrReportTimeAM_PMFormat("hh:mma");
		
		
//		hmEmpFullNameMap = CF.getEmpNameMap(con,strUserType, strSessionEmpId);
//		hmEmpCodeMap = CF.getEmpCodeMap(con);
//		hmEmpEmailMap = CF.getEmpEmailMap(con);
//		hmEmpContactMap = CF.getEmpContactNoMap(con);
		
		
//		HSSFWorkbook hwb = new HSSFWorkbook();
//		HSSFSheet sheet = null;
//		Excelstyle eStyle = null;
//
//		
//		hwb = new HSSFWorkbook();
//		eStyle = new Excelstyle(hwb);
//		ExcelSheetData(eStyle, hwb, sheet, false);
//		
//		hwb = new HSSFWorkbook();
//		eStyle = new Excelstyle(hwb);
//		ExcelSheetData(eStyle, hwb, sheet, true);
//		
		
		
		
		
		return SUCCESS;
	}
/*
	private void addLogoImage(HSSFWorkbook hwb, HSSFSheet sheet, int logoPositionColumn) {
		try {
 
			String filePath = request.getRealPath("/export/");
			
//			String filePath = null;
//			if(request==null){
//				filePath = "C:\\temp\\";
//			}


			InputStream is = new FileInputStream(filePath+File.separator+"logo1.png");
			
			byte[] bytes = IOUtils.toByteArray(is);
			int pictureIdx = hwb.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
			is.close();

			CreationHelper helper = hwb.getCreationHelper();
			Drawing drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = helper.createClientAnchor();
			anchor.setCol1(logoPositionColumn);
			anchor.setRow1(0);
			Picture pict = drawing.createPicture(anchor, pictureIdx);
			pict.resize();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addSummaryHeader(Excelstyle eStyle, HSSFSheet sheet){
		
		HSSFRow rowhead = sheet.createRow(5);
		HSSFCell cell = rowhead.createCell(1);
		cell.setCellValue("Level 1/456 Spencer Street");
		cell.setCellStyle(eStyle.headRowcellStyle());
		
		rowhead = sheet.createRow(6);
		cell = rowhead.createCell(1);
		cell.setCellValue("West Melbourne");
		cell.setCellStyle(eStyle.headRowcellStyle());
		
		rowhead = sheet.createRow(7);
		cell = rowhead.createCell(1);
		cell.setCellValue("Melbourne Victoria 3003");
		cell.setCellStyle(eStyle.headRowcellStyle());
		
			
	}
	
	

	private void putStyle(Excelstyle eStyle, HSSFWorkbook hwb, HSSFSheet sheet, int additionalColumn) {

		
		HSSFCellStyle dayRowcellStyle = eStyle.dayRowStyle();
		HSSFCellStyle dateRowcellStyle = eStyle.dateRowStyle();
		HSSFCellStyle startimeRowcellStyle = eStyle.startimeRowStyle();
		HSSFCellStyle TotalWorkRowcellStyle = eStyle.TotalWorkRowStyle();
		HSSFCellStyle addedWorkRowcellStyle = eStyle.addedWorkRowStyle();
		HSSFCellStyle setWorkHourStyle = eStyle.setWorkHourStyle();
		HSSFCellStyle PayRowStyle = eStyle.PayPerDayRowStyle();
		HSSFCellStyle HeadingRowStyle = eStyle.HeadingRowStyle();
		HSSFCellStyle headcellStyle = eStyle.headRowcellStyle();
		HSSFCellStyle FootRowStyle = eStyle.FootRowcellStyle();
		HSSFCellStyle FootRowStyleBold = eStyle.FootRowcellStyleBold();
		HSSFCellStyle finalcell = eStyle.FinalCellStyle();
		HSSFCellStyle signcell = eStyle.SigncellStyle();
		
		HSSFCellStyle publicHoliday = eStyle.startTimeRowStylePublicHoliday();
		HSSFCellStyle sickLeave = eStyle.startTimeRowStyleSickLeave();
		HSSFCellStyle annualLeave = eStyle.startTimeRowStyleAnnualLeave();
		
		
		

		Iterator<Row> rows = sheet.rowIterator();
		while (rows.hasNext()) {
			HSSFRow row = (HSSFRow) rows.next();
			int rownum = row.getRowNum();

			Iterator<Cell> cells = row.cellIterator();
			while (cells.hasNext()) {

				HSSFCell cellcount = (HSSFCell) cells.next();
				int countcell = cellcount.getCellNum();

				if (((rownum == 9 || rownum == 16) && countcell > 3 && countcell <= 10)) {
					cellcount.setCellStyle(dayRowcellStyle);
					cellcount.setCellValue("Thursday");
				}
				if ((rownum == 9 || rownum == 16) && countcell == 5) {
					cellcount.setCellStyle(dayRowcellStyle);
					cellcount.setCellValue("Friday");
				}
				if ((rownum == 9 || rownum == 16) && countcell == 6) {
					cellcount.setCellStyle(dayRowcellStyle);
					cellcount.setCellValue("Saturday");
				}
				if ((rownum == 9 || rownum == 16) && countcell == 7) {
					cellcount.setCellStyle(dayRowcellStyle);
					cellcount.setCellValue("Sunday");
				}
				if ((rownum == 9 || rownum == 16) && countcell == 8) {
					cellcount.setCellStyle(dayRowcellStyle);
					cellcount.setCellValue("Monday");
				}
				if ((rownum == 9 || rownum == 16) && countcell == 9) {
					cellcount.setCellStyle(dayRowcellStyle);
					cellcount.setCellValue("Tuesday");
				}
				if ((rownum == 9 || rownum == 16) && countcell == 10) {
					cellcount.setCellStyle(dayRowcellStyle);
					cellcount.setCellValue("Wednesday");
				}
				if ((rownum == 10 || rownum == 17) && countcell > 3 && countcell <= 10) {
					cellcount.setCellStyle(dateRowcellStyle);

				}
				if ((rownum == 11 || rownum == 12 || rownum == 18 || rownum == 19) && countcell > 3 && countcell <= 10) {
					cellcount.setCellStyle(startimeRowcellStyle);
				}
				if (((rownum > 12 && rownum < 16) || (rownum > 19 && rownum < 23)) && countcell > 3 && countcell <= 10) {
					cellcount.setCellStyle(TotalWorkRowcellStyle);

				}
				if ((rownum == 15 || rownum == 22) && (countcell > 3 && countcell <= 10)) {
					cellcount.setCellStyle(PayRowStyle);
				}
				if ((rownum == 11 || rownum == 18) && countcell == 3) {
					cellcount.setCellStyle(dayRowcellStyle);
					cellcount.setCellValue("Start Time");
				}
				if ((rownum == 12 || rownum == 19) && countcell == 3) {
					cellcount.setCellStyle(dayRowcellStyle);
					cellcount.setCellValue("End Time");
				}
				if ((rownum == 13 || rownum == 20) && countcell == 3) {
					cellcount.setCellStyle(dayRowcellStyle);
					cellcount.setCellValue("Daily Hours");
				}
				if ((rownum == 14 || rownum == 21) && countcell == 3) {
					cellcount.setCellStyle(dayRowcellStyle);
					cellcount.setCellValue("Rate Per Hour");
				}
				if ((rownum == 15 || rownum == 22) && countcell == 3) {
					cellcount.setCellStyle(dayRowcellStyle);
					cellcount.setCellValue("Pay Per Day");
				}
				if ((rownum == 11) && countcell == 11 + additionalColumn) {
//					cellcount.setCellStyle(setWorkHourStyle);
					cellcount.setCellStyle(dayRowcellStyle);
					
					cellcount.setCellValue("Total Hours Week 1");
				}
				if ((rownum == 18) && countcell == 11 + additionalColumn) {
//					cellcount.setCellStyle(setWorkHourStyle);
					cellcount.setCellStyle(dayRowcellStyle);
					cellcount.setCellValue("Total Hours Week 2");
				}
				if ((rownum == 13 || rownum == 15 || rownum == 20 || rownum == 22) && countcell == 11 + additionalColumn) {
					cellcount.setCellStyle(addedWorkRowcellStyle);
					cellcount.setCellValue("50.00");
				}
				if ((rownum == 14) && countcell == 11+additionalColumn) {
//					cellcount.setCellStyle(setWorkHourStyle);
					cellcount.setCellStyle(dayRowcellStyle);
					cellcount.setCellValue("Total Pay Week 1");
				}
				if ((rownum == 21) && countcell == 11+additionalColumn) {
//					cellcount.setCellStyle(setWorkHourStyle);
					cellcount.setCellStyle(dayRowcellStyle);
					cellcount.setCellValue("Total Pay Week 2");
				}
				if (rownum == 1 && countcell == 6) {
					cellcount.setCellStyle(HeadingRowStyle);
					cellcount.setCellValue("Biweekly Time Sheet");
					
				}
				if (rownum == 5 && countcell == 3) {
					cellcount.setCellStyle(headcellStyle);
					cellcount.setCellValue("Level 1/456 Spencer Street");
				}
				if (rownum == 6 && countcell == 3) {
					cellcount.setCellStyle(headcellStyle);
					cellcount.setCellValue("West Melbourne");
					
				}
				if (rownum == 7 && countcell == 3) {
					cellcount.setCellStyle(headcellStyle);
					cellcount.setCellValue("Melbourne Victoria 3003");
				}
				if (rownum == 5 && countcell == 7) {
					cellcount.setCellStyle(headcellStyle);
					cellcount.setCellValue("Employee Name: ");
					
				}
				if (rownum == 5 && countcell == 10) {
					cellcount.setCellStyle(headcellStyle);
					cellcount.setCellValue("Week Starting: ");
				}

				if (rownum == 6 && countcell == 7) {
					cellcount.setCellStyle(headcellStyle);
					cellcount.setCellValue("Manager Name:________________");
				}
				if (rownum == 6 && countcell == 10) {
					cellcount.setCellStyle(headcellStyle);
					cellcount.setCellValue("Week Ending: ");
				}
				if (rownum == 7 && countcell == 7) {
					cellcount.setCellStyle(headcellStyle);
					cellcount.setCellValue("Return to Summary Page:___________________________");
				}
				if (rownum == 25 && countcell == 7+additionalColumn) {
					cellcount.setCellStyle(FootRowStyle);
					cellcount.setCellValue("TOTAL HOURS");
				}
				if (rownum == 25 && countcell == 9+additionalColumn) {
					cellcount.setCellStyle(FootRowStyle);

				}
				if (rownum == 25 && countcell == 10+additionalColumn) {
					cellcount.setCellStyle(FootRowStyle);
					cellcount.setCellValue("GROSS AMOUNT");
				}
				if (rownum == 25 && countcell == 11+additionalColumn) {
					cellcount.setCellStyle(FootRowStyle);

				}
				if (rownum == 26 && countcell == 10+additionalColumn) {
					cellcount.setCellStyle(FootRowStyle);
					cellcount.setCellValue("LESS TAX");
				}
				if (rownum == 26 && countcell == 11+additionalColumn) {
					cellcount.setCellStyle(FootRowStyle);

				}
				
				if (rownum == 27 && countcell == 10+additionalColumn) {
					cellcount.setCellStyle(FootRowStyle);
					cellcount.setCellValue("ALLOWANCE");
				}
				if (rownum == 27 && countcell == 11+additionalColumn) {
					cellcount.setCellStyle(FootRowStyle);

				}

				if (rownum == 28 && countcell == 10+additionalColumn) {
					cellcount.setCellStyle(FootRowStyleBold);
					cellcount.setCellValue("NETT AMOUNT");
				}
				if (rownum == 28 && countcell == 11+additionalColumn) {
					cellcount.setCellStyle(FootRowStyle);

				}
				if ((rownum == 25 || rownum == 26 || rownum == 27) && countcell == 13+additionalColumn) {
					cellcount.setCellStyle(finalcell);

				}
				if (rownum == 29 && countcell == 3) {
					cellcount.setCellStyle(signcell);
					cellcount.setCellValue("___________________________________________");
				}
				if (rownum == 30 && countcell == 3) {
					cellcount.setCellStyle(signcell);
					cellcount.setCellValue("Employee Signature                           Date");
				}
				if (rownum == 29 && countcell == 8) {
					cellcount.setCellStyle(signcell);
					cellcount.setCellValue("___________________________________________");
				}
				if (rownum == 30 && countcell == 8) {
					cellcount.setCellStyle(signcell);
					cellcount.setCellValue("Manager Signature                           Date");
				}
				
				if (rownum == 2 && countcell == 13) {
					cellcount.setCellStyle(headcellStyle);
					cellcount.setCellValue("Sick");
				}
				if (rownum == 2 && countcell == 14) {
					cellcount.setCellStyle(sickLeave);
				}
				
				if (rownum == 3 && countcell == 13) {
					cellcount.setCellStyle(headcellStyle);
					cellcount.setCellValue("Annual");
				}
				if (rownum == 3 && countcell == 14) {
					cellcount.setCellStyle(annualLeave);
				}
				if (rownum == 4 && countcell == 13) {
					cellcount.setCellStyle(headcellStyle);
					cellcount.setCellValue("Public Holiday");
				}
				if (rownum == 4 && countcell == 14) {
					cellcount.setCellStyle(publicHoliday);
				}
				
			}
		}
	}
	
	
	
	

	private void createTimeSheet(Excelstyle eStyle, HSSFWorkbook hwb, HSSFSheet sheet, int serviceCount, int additionalColumn) {
		try {
			// hwb = new HSSFWorkbook();
			// sheet = hwb.createSheet("new sheet");
			sheet.setDefaultColumnWidth(12);
			sheet.setColumnWidth(0, 2 * 256);
			sheet.setColumnWidth(2, 3 * 256);
			sheet.setColumnWidth(1, 4 * 256);
			sheet.setColumnWidth(3, 17 * 256);
//			sheet.setColumnWidth(12, 5 * 256);
			sheet.setColumnWidth(13, 20 * 256);
			sheet.setColumnWidth(11+additionalColumn, 20 * 256);
			sheet.setColumnWidth(12+additionalColumn, 5 * 256);

			sheet.addMergedRegion(new CellRangeAddress(11, 12, 11+additionalColumn, 11+additionalColumn));
			sheet.addMergedRegion(new CellRangeAddress(18, 19, 11+additionalColumn, 11+additionalColumn));
			// sheet.addMergedRegion(new CellRangeAddress(30,3,30,7));
			// sheet.addMergedRegion(new CellRangeAddress(31,3,31,7));

			HSSFCellStyle datacellStyle = eStyle.setDataRowStyle();
			HSSFCellStyle MainRowStyle = eStyle.setMainRowStyle();
			
			for (int i = 0; i < 50; i++) {
				HSSFRow rowhead = sheet.createRow(i);

				if ((i > 1 && i < 8) || i == 10 || i == 17 || (i > 23 && i < 29)) {
					rowhead.setHeightInPoints(4 * 4);
				} else if (i == 30)
					rowhead.setHeightInPoints(3 * 3);
				else
					rowhead.setHeightInPoints(6 * 5);
				int j;
				for (j = 0; j <= (serviceCount + 60); j++) {
					
					if ((j > 0 && j < (13 + additionalColumn)) && (i > 7 && i < 24))
						rowhead.createCell(j).setCellStyle(datacellStyle);
					else
						rowhead.createCell(j).setCellStyle(MainRowStyle);
				}
			}
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
	
	
	private void ExcelSheetData(Excelstyle eStyle, HSSFWorkbook hwb, HSSFSheet sheet, boolean isAdmin) {
		
		Connection con = null;
		ResultSet rs = null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();

		PreparedStatement pst = null;
		try {
			
			
			Map<String, String> hmFirstAidAllowance = new HashMap<String, String>();
			Map<String, Map<String, String>> hmLeavesMap = new HashMap<String, Map<String, String>>();
			hmLeavesMap = CF.getLeaveDates(strD1, strD2, CF);
			Map<String, String> hmLeaves = null;
			Map<String, String> hmLeavesColour = new HashMap<String, String>();
			new CommonFunctions().getAllowanceMap(hmFirstAidAllowance);
			new CommonFunctions().getLeavesColour(hmLeavesColour);
			
			
			
			con = db.makeConnection(con);

//			pst = con.prepareStatement("select * from (SELECT * FROM alldates where _date < (SELECT MAX(generate_date) from payroll )" + "order by _date desc   LIMIT 14 ) as payroll order by _date");
			
			pst = con.prepareStatement("SELECT * from (SELECT * FROM alldates WHERE _date BETWEEN ? AND ? order by _date desc LIMIT 14 ) as payroll order by _date");
			pst.setDate(1, uF.getDateFormat(strD1, CF.getStrReportDateFormat()));
			pst.setDate(2, uF.getDateFormat(strD2, CF.getStrReportDateFormat()));
			rs = pst.executeQuery();

			List _alDate = new ArrayList();
			List _alDay = new ArrayList();
			while (rs.next()) {
				_alDate.add(uF.getDateFormat(rs.getString("_date"), "yyyy-MM-dd", CF.getStrReportDateFormat()));
				_alDay.add(uF.getDateFormat(rs.getString("_date"), "yyyy-MM-dd", CF.getStrReportDayFormat()));
			}

			String url = "select * from (SELECT epd.emp_fname as man_fname, epd.emp_lname as man_lname,a.* from employee_personal_details epd, ( select emp_per_id,prl.service_id,emp_fname as emp_fname ,emp_lname as emp_lname,supervisor_emp_id,_date,_in,_out,rate,total_time,loading, generate_date, pay_mode  FROM employee_personal_details epd,employee_official_details eod,payroll prl WHERE epd.emp_per_id=eod.emp_id AND eod.emp_id=prl.emp_id and generate_date = (select max(generate_date) as generate_date from payroll) ORDER BY emp_per_id,_date) a where a.supervisor_emp_id = epd.emp_per_id ) a order by emp_fname,emp_lname, _date desc";

			PreparedStatement st = con.prepareStatement(url);
			rs = st.executeQuery();

			String date = null;
			int dateEndColumn = 10;
			int dateStartColumn = 4;
			int row9 = 9, row10 = 10, row11 = 11, row12 = 12, row13 = 13, row14 = 14, row15 = 15;
			int columnCount = 0;

			String strOldEmpId = null;
			String strNewEmpId = null;

			int i = 0;
			int nWeek = 1;

			Map hm = new LinkedHashMap();
			Map hmInner = new HashMap();
			Map hmInner1 = new HashMap();
			int count=0;
			List alService = new ArrayList();
			
			int nColumnCounterW1 = 0;
			int nColumnCounterW2 = 0;
			
			while (rs.next()) {
				columnCount = 4;
				
				
				
				strNewEmpId = rs.getString("emp_per_id");

				if (strNewEmpId != null && !strNewEmpId.equalsIgnoreCase(strOldEmpId)) {
					hmInner = new HashMap();
					count=0;
					nColumnCounterW1 = 0;
					nColumnCounterW2 = 0;
				}

				hmInner1 = new HashMap();
//				if(strNewEmpId!=null && !strNewEmpId.equalsIgnoreCase("251")){
//					continue;
//				}
				
				hmInner1.put("RATE", rs.getString("rate"));
				hmInner1.put("TOT_TIME", rs.getString("total_time"));
				hmInner1.put("DATE", uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInner1.put("DAY", uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()));
				hmInner1.put("IN", rs.getString("_in"));
				hmInner1.put("OUT", rs.getString("_out"));
				hmInner1.put("GEN_DATE", rs.getString("generate_date"));
				hmInner1.put("HOURS", uF.formatIntoTwoDecimal(rs.getDouble("total_time")));
				hmInner1.put("LOADING", rs.getString("loading"));
				hmInner1.put("SERVICE_ID", rs.getString("service_id"));
				hmInner1.put("PAYMODE", rs.getString("pay_mode"));
				hmInner.put("PAYMODE", rs.getString("pay_mode"));
				hmInner.put("COUNT", ""+count++);
				
				
				
				
//				hmInner1.put("MON_LOADING", rs.getString("loading_mon"));
//				hmInner1.put("TUE_LOADING", rs.getString("loading_tue"));
//				hmInner1.put("WED_LOADING", rs.getString("loading_wed"));
//				hmInner1.put("THURS_LOADING", rs.getString("loading_thurs"));
//				hmInner1.put("FRI_LOADING", rs.getString("loading_fri"));
//				hmInner1.put("SAT_LOADING", rs.getString("loading_sat"));
//				hmInner1.put("SUN_LOADING", rs.getString("loading_sun"));
				
				
				alService = (List)hmInner.get(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
				
				
				if(alService==null){
					alService = new ArrayList();
				}
				
						
				
				if(!alService.contains(rs.getString("service_id"))){
					alService.add(rs.getString("service_id"));
				}
				
				java.util.Date dtDates =  uF.getDateFormatUtil((String)_alDate.get(7), CF.getStrReportDateFormat());
				java.util.Date serviceDate =  uF.getDateFormatUtil(rs.getString("_date"), DBDATE);
				
				
				
				
				if(serviceDate.before(dtDates) && alService.size()>1){
//					System.out.println(strNewEmpId+"  = "+rs.getString("_date")+" dtDates="+dtDates+" serviceDate="+serviceDate+" alService="+alService);
					nColumnCounterW1++;
				}else if(alService.size()>1){
					nColumnCounterW2++;
				}
				
				
				
				hmInner.put("nColumnCounterW1", nColumnCounterW1+"");
				hmInner.put("nColumnCounterW2", nColumnCounterW2+"");
				
				hmInner.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), alService);

				hmInner.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())+"_"+rs.getString("service_id"), hmInner1);
				hmInner.put("EMP_NAME", rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
				hmInner.put("MANAGER_NAME", rs.getString("man_fname")+" "+rs.getString("man_lname"));
				hmInner.put("WEEK_START", (String)_alDate.get(0));
				hmInner.put("WEEK_END", (String)_alDate.get(13));
				hmInner.put("GEN_DATE", rs.getString("generate_date"));
				hmInner.put("PAYMODE", rs.getString("pay_mode"));
				hmInner.put("FRATE", rs.getString("rate"));

				hm.put(strNewEmpId, hmInner);
				strOldEmpId = strNewEmpId;

			}

			
			
			Map hmHolidays = new CommonFunctions(CF).getHolidayList();
			Map hmServiceMap = CF.getServicesMap(true); 
			Map hmPayrollFT = new HashMap();
			Map hmPayrollPT = new HashMap();
			
			new CommonFunctions().getDailyRates(hmPayrollFT, hmPayrollPT);
			
			Set set = hm.keySet();
			Iterator it = set.iterator();

			int ii = 0;
			int counter = 0;

			double dailyHours = 0;
			double dailyRate = 0;
			double dailyHourRate = 0;

			double totalHourRate1 = 0;
			double totalHours1 = 0;

			double totalHourRate2 = 0;
			double totalHours2 = 0;
			double dedAmount = 0;
			double dblAllowance = 0;

			double totalPayment = 0;
			
			boolean isWeek2 = false;
			Map hmTemp = null; 
			
			
			Map hmEmployeeSummaryContainer = new HashMap();
			Map hmEmployeeSummaryDetails = new HashMap();
			Map hmEmployeeSummaryService = new HashMap();
			
			
			double dblTaxDeduction = 0.0;
			double dblGross = 0.0;
			
			
			while (it.hasNext()) {
				
				int columnDefaultDateCount = 13;	
				boolean isFixedAdded = false;
				
				hmEmployeeSummaryDetails = new HashMap();
				
				
//				HSSFCellStyle dayRowcellStyle = eStyle.dayRowStyle(hwb);
//				HSSFCellStyle dateRowcellStyle = eStyle.dateRowStyle(hwb);
//				HSSFCellStyle startimeRowcellStyle = eStyle.startimeRowStyle(hwb);
//				HSSFCellStyle TotalWorkRowcellStyle = eStyle.TotalWorkRowStyle(hwb);
//				HSSFCellStyle addedWorkRowcellStyle = eStyle.addedWorkRowStyle(hwb);
//				HSSFCellStyle setWorkHourStyle = eStyle.setWorkHourStyle(hwb);
//				HSSFCellStyle PayRowStyle = eStyle.PayPerDayRowStyle(hwb);
//				HSSFCellStyle HeadingRowStyle = eStyle.HeadingRowStyle(hwb);
//				HSSFCellStyle headcellStyle = eStyle.HeadRowcellStyle(hwb);
//				HSSFCellStyle FootRowStyle = eStyle.FootRowcellStyle(hwb);
//				HSSFCellStyle FootRowStyleBold = eStyle.FootRowcellStyleBold(hwb);
//				HSSFCellStyle finalcell = eStyle.FinalCellStyle(hwb);
//				HSSFCellStyle signcell = eStyle.SigncellStyle(hwb);
				
				
				String strEmpId = (String) it.next();
				hmLeaves = hmLeavesMap.get(strEmpId);
				if(hmLeaves==null){
					hmLeaves = new HashMap<String, String>();
				}
				
				
				 
				
				
				ii = 0;
				counter = 0;
				columnCount = 0;
				nWeek = 1;
				totalHourRate1 = 0;
				totalHours1 = 0;

				totalHourRate2 = 0;
				totalHours2 = 0;
				dedAmount = 0;
				dblAllowance = 0;
				
				hmTemp = (Map) hm.get(strEmpId);

				int serviceCount  = uF.parseToInt((String)hmTemp.get("COUNT"));
				
				nColumnCounterW1 = uF.parseToInt((String)hmTemp.get("nColumnCounterW1"));
				nColumnCounterW2 = uF.parseToInt((String)hmTemp.get("nColumnCounterW2"));
				int additionalColumnCount  = Math.max(nColumnCounterW1, nColumnCounterW2);
				columnDefaultDateCount += additionalColumnCount;
				
				
//				System.out.println("strEmpId="+strEmpId);
//				System.out.println("nColumnCounterW1="+nColumnCounterW1);
//				System.out.println("nColumnCounterW2="+nColumnCounterW2);
//				System.out.println("additionalColumnCount="+additionalColumnCount);
//				System.out.println("columnDefaultDateCount="+columnDefaultDateCount);
				
				
				
				if(!isAdmin){
					hwb = new HSSFWorkbook();
					eStyle = new Excelstyle(hwb);
				}
				sheet = hwb.createSheet((String)hmTemp.get("EMP_NAME"));
				

				

				createTimeSheet(eStyle, hwb, sheet, serviceCount, additionalColumnCount);
				putStyle(eStyle, hwb, sheet, additionalColumnCount);
				addLogoImage(hwb, sheet, 2);

				
				Iterator<Row> rows = sheet.rowIterator();
				
				while (rows.hasNext()) {

					HSSFRow row = (HSSFRow) rows.next();
					int rownum = row.getRowNum();
					Iterator<Cell> cells = row.cellIterator();

					columnCount = 0;
					counter = 0;
					if (rownum >= 16) {
						nWeek = 2;
						counter = 7;

					}

					boolean isBreak = false;
					
					while (cells.hasNext()) {
						if(counter>=_alDate.size()){
							
							isBreak = true;
							
							//break;	
						}
					
						String strDate = null;
						if(counter<_alDate.size()){
							strDate = (String) _alDate.get(counter);
						}
						
						
						List alServiceTemp = (List)hmTemp.get(strDate);
						
						
						
						
						
						
						
						if(alServiceTemp==null){
							alServiceTemp = new ArrayList();
							
							alServiceTemp.add("");
						}
						int nServiceNumber = alServiceTemp.size();
						 
						
						for(int k=0; k<alServiceTemp.size(); k++){
							String strServiceId = (String)alServiceTemp.get(k);
							
							
							
							
//							System.out.println("columnCount===>"+columnCount);	
							
							if(!cells.hasNext()){
								continue;
							}

						HSSFCell cellcount = (HSSFCell) cells.next();					
						int countcell = cellcount.getCellNum();

						
						
						
						
						
						columnCount++;
						
						
						
						
//						if ( nWeek==1 && rownum == row15 && columnCount>11 && columnCount < 12+additionalColumnCount) {
//							
//							System.out.println(strDate+"=strDate 15 WEEK "+nWeek+" "+"rownum="+rownum+"countcell=====>"+countcell);
//							
//							Map hmTemp1 = (Map) hmTemp.get(strDate+"_"+strServiceId);
//							if (hmTemp1 == null) {
//								hmTemp1 = new HashMap();
//							}
//							
//							
//							double dblRate = 0d;
//							double dblLoading = 0d;
//							dailyHourRate = 0d;
//
//							String strPayMode = (String) hmTemp1.get("PAYMODE");
//							if(strPayMode!=null && strPayMode.equalsIgnoreCase("H")){
//							
//								dblRate = uF.parseToDouble((String) hmTemp1.get("RATE"));
////								dblLoading = uF.parseToDouble((String) hmTemp1.get(new CommonFunctions().getLoadingWeekDayCode((String) hmTemp1.get("DAY"))));
//								dblLoading = uF.parseToDouble((String) hmTemp1.get("LOADING"));
//								
//								if(dblLoading>0){
//									dblRate += dblRate * dblLoading / 100;
//								}
//								
//								dailyHourRate = dblRate * uF.parseToDouble((String) hmTemp1.get("HOURS"));
//								totalHourRate1 += dailyHourRate;
//								
//							}
//							
//							
//							cellcount.setCellValue( uF.formatIntoTwoDecimal(dailyHourRate));
//							cellcount.setCellStyle(eStyle.PayPerDayRowStyle(hwb));
//						}
//
//						if ( nWeek==1 && rownum == row15 && countcell == 11 + additionalColumnCount) {
//							
//							System.out.println(" 15 WEEK "+nWeek+" "+"rownum="+rownum+"countcell=====>"+countcell);
//							
//							
//							cellcount.setCellValue( uF.formatIntoTwoDecimal(totalHourRate1));
//							cellcount.setCellStyle(eStyle.addedWorkRowStyle(hwb));
//						}
						
						
						
						if (nWeek==1 && rownum == row13 && countcell == 11+additionalColumnCount) {
							cellcount.setCellValue(uF.formatIntoTwoDecimal(totalHours1));
							cellcount.setCellStyle(eStyle.dateRowStyleBold());
							
						}
						
						if (nWeek==1 && rownum == row15 && countcell == 11 + additionalColumnCount) {
							cellcount.setCellValue( uF.formatIntoTwoDecimal(totalHourRate1));
							cellcount.setCellStyle(eStyle.dateRowStyleBoldBlue());
						}
						
						
						
						
						if (isBreak && nWeek==2 &&  rownum == 20 && countcell == 11+additionalColumnCount) {
//							System.out.println("break   rownum=======>"+rownum+"  columnCount===>"+columnCount+" totalHours2="+totalHours2);
							cellcount.setCellValue(uF.formatIntoTwoDecimal(totalHours2));
							cellcount.setCellStyle(eStyle.dateRowStyleBold());
							break;
						}else if(isBreak && nWeek==2 &&  rownum == 22 && countcell == 11 + additionalColumnCount) {
								cellcount.setCellValue( uF.formatIntoTwoDecimal(totalHourRate2));
								cellcount.setCellStyle(eStyle.dateRowStyleBoldBlue());
								break;
						}else if(isBreak){
							break;
						}
						

						if (rownum == 5 && countcell == 8) {
							 cellcount.setCellValue((String)hmTemp.get("EMP_NAME"));
							 cellcount.setCellStyle(eStyle.HeadRowcellDataStyle());
							 
						}

						if (rownum == 5 && countcell == 11) {
							cellcount.setCellValue((String)hmTemp.get("WEEK_START"));
							cellcount.setCellStyle(eStyle.HeadRowcellDataStyle());
						}

						if (rownum == 6 && countcell == 8) {
							cellcount.setCellValue((String)hmTemp.get("MANAGER_NAME"));
							cellcount.setCellStyle(eStyle.HeadRowcellDataStyle());
						}

						if (rownum == 6 && countcell == 11) {
							cellcount.setCellValue((String)hmTemp.get("WEEK_END"));
							cellcount.setCellStyle(eStyle.HeadRowcellDataStyle());
						}

						if (rownum == 7 && countcell == 9) {
							cellcount.setCellValue("SUMMARY");
							cellcount.setCellStyle(eStyle.hyperLinkStyle());
							
							CreationHelper createHelper = hwb.getCreationHelper();
							org.apache.poi.ss.usermodel.Hyperlink link = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
							link.setAddress("'Summary'!A1");
							cellcount.setHyperlink(link);
						}
						
						
						
						java.util.Date dtDates = uF.getDateFormatUtil((String) _alDate.get(7), CF.getStrReportDateFormat());
						java.util.Date dtDailyDates = uF.getDateFormatUtil(strDate, CF.getStrReportDateFormat());
						
						
						// WEEK 1
						Map hmTemp1 = null;
						
//						System.out.println(" 15 WEEK "+ rownum +" "+columnCount+" nWeek="+nWeek+" "+dtDailyDates.before(dtDates)+" dtDailyDates="+dtDailyDates+"  dtDates="+dtDates );
						
						if (nWeek == 1 && dtDailyDates.before(dtDates)) {
//						if (nWeek == 1) {
						
//							columnDefaultDateCount += nColumnCounterW1;
							
							
							if (rownum == row9 && columnCount > 4 && columnCount < 12+additionalColumnCount) {
//								System.out.println("hmTemp===>"+hmTemp);
//								System.out.println("strDate===>"+strDate);
//								System.out.println("alServiceTemp===>"+alServiceTemp);
								cellcount.setCellValue((String) _alDay.get(counter));
								cellcount.setCellStyle(eStyle.dayRowStyle());
								if(k == nServiceNumber-1){
									counter++;
								}
							}

							if (rownum == row10 && columnCount > 4 && columnCount < 12+additionalColumnCount) {
								cellcount.setCellValue((String) _alDate.get(counter));
								cellcount.setCellStyle(eStyle.dateRowStyle());
								if(k == nServiceNumber-1){
									counter++;
								}
							}

							if (rownum == row11 && columnCount > 4 && columnCount < 12+additionalColumnCount) {
								hmTemp1 = (Map) hmTemp.get((String) _alDate.get(counter)+"_"+strServiceId);
								if (hmTemp1 == null) {
									hmTemp1 = new HashMap();
								}								
								cellcount.setCellValue(  ((String) hmTemp1.get("IN") != null ? uF.getDateFormat((String) hmTemp1.get("IN"), DBTIME, CF.getStrReportTimeAM_PMFormat()):"")) ;
								if(hmLeaves.containsKey(strDate)){
									String strColour  = hmLeavesColour.get(hmLeaves.get(strDate));
									if(strColour.equalsIgnoreCase("A")){
										cellcount.setCellStyle(eStyle.startTimeRowStyleAnnualLeave());	
									}else if(strColour.equalsIgnoreCase("S")){
										cellcount.setCellStyle(eStyle.startTimeRowStyleSickLeave());
									}
								}else if(hmHolidays.containsKey(strDate)){
									cellcount.setCellStyle(eStyle.startTimeRowStylePublicHoliday());
								}else{
									cellcount.setCellStyle(eStyle.startimeRowStyle());
								}
								if(k == nServiceNumber-1){
									counter++;
								}
							}
							if (rownum == row12 && columnCount > 4 && columnCount < 12+additionalColumnCount) {
								hmTemp1 = (Map) hmTemp.get((String) _alDate.get(counter)+"_"+strServiceId);
								if (hmTemp1 == null) {
									hmTemp1 = new HashMap();
								}
								cellcount.setCellValue(  ((String) hmTemp1.get("IN") != null ? uF.getDateFormat((String) hmTemp1.get("OUT"), DBTIME, CF.getStrReportTimeAM_PMFormat()):"")) ;
								if(hmLeaves.containsKey(strDate)){
									String strColour  = hmLeavesColour.get(hmLeaves.get(strDate));
									if(strColour.equalsIgnoreCase("A")){
										cellcount.setCellStyle(eStyle.startTimeRowStyleAnnualLeave());	
									}else if(strColour.equalsIgnoreCase("S")){
										cellcount.setCellStyle(eStyle.startTimeRowStyleSickLeave());
									}
								}else if(hmHolidays.containsKey(strDate)){
									cellcount.setCellStyle(eStyle.startTimeRowStylePublicHoliday());
								}else{
									cellcount.setCellStyle(eStyle.startimeRowStyle());
								}
								if(k == nServiceNumber-1){
									counter++;
								}
							}

							if (rownum == row13 && columnCount > 4 && columnCount < 12+additionalColumnCount) {
								hmTemp1 = (Map) hmTemp.get((String) _alDate.get(counter)+"_"+strServiceId);
								if (hmTemp1 == null) {
									hmTemp1 = new HashMap();
								}
								
								dailyHours = uF.parseToDouble((String) hmTemp1.get("HOURS"));
								if(dailyHours==0 && hmHolidays.containsKey(strDate)){
									Map hm1 = (Map)hmPayrollFT.get("D"+strEmpId+"S");
									double dblHRate = uF.parseToDouble((String)hm1.get(uF.getDateFormat(strDate, CF.getStrReportDateFormat(), CF.getStrReportDayFormat()).toUpperCase()));
									dailyHours = 7.5;
									cellcount.setCellValue(dailyHours);
									cellcount.setCellStyle(eStyle.TotalWorkRowStyle());
								}else{
									cellcount.setCellValue((String) hmTemp1.get("HOURS") != null ? (String) hmTemp1.get("HOURS") : "0.0");
									cellcount.setCellStyle(eStyle.TotalWorkRowStyle());
								}
								
								cellcount.setCellValue((String) hmTemp1.get("HOURS") != null ? (String) hmTemp1.get("HOURS") : "0.0");
								cellcount.setCellStyle(eStyle.TotalWorkRowStyle());
								
								if(k == nServiceNumber-1){
									counter++;
								}
								totalHours1 += dailyHours;
								
								
								
//								HSSFPatriarch  patr = sheet.createDrawingPatriarch();
//								Comment c = patr.createComment(new HSSFClientAnchor(100, 100, 100, 100, (short)(columnCount-2), rownum, (short) (columnCount-1), rownum+1));
//
//								c.setColumn((short)columnCount);
//								c.setRow((short)rownum);
//								c.setString(new HSSFRichTextString("Service"));
//								c.setVisible(true);
//								cellcount.setCellComment(c);

							}

//							if (rownum == row13 && countcell == 11+additionalColumnCount) {
//								cellcount.setCellValue(uF.formatIntoOneDecimal(totalHours1));
//								cellcount.setCellStyle(eStyle.dateRowStyleBold());
//								
//							}

							if (rownum == row14 && columnCount > 4 && columnCount < 12+additionalColumnCount) {
								hmTemp1 = (Map) hmTemp.get((String) _alDate.get(counter)+"_"+strServiceId);
								if (hmTemp1 == null) {
									hmTemp1 = new HashMap();
								}
								
								if(k == nServiceNumber-1){
									counter++;
								}
								
								double dblRate = 0d;
								double dblLoading = 0d;

								
								String strPayMode = (String) hmTemp1.get("PAYMODE");
								
								if(strPayMode!=null && strPayMode.equalsIgnoreCase("H")){
								
									dblRate = uF.parseToDouble((String) hmTemp1.get("RATE"));
//									dblLoading = uF.parseToDouble((String) hmTemp1.get(new CommonFunctions().getLoadingWeekDayCode((String) hmTemp1.get("DAY"))));
									dblLoading = uF.parseToDouble((String) hmTemp1.get("LOADING"));
									
									
									
									if(hmHolidays.containsKey(strDate) && dblLoading>0){
										dblRate += dblRate * dblLoading / 100;
									}
									cellcount.setCellValue( uF.formatIntoTwoDecimal(dblRate));
								}else if(strPayMode!=null && strPayMode.equalsIgnoreCase("X")){
									cellcount.setCellValue("Fixed");									
								}else if(hmHolidays.containsKey(strDate)){
									Map hm1 = (Map)hmPayrollFT.get("D"+strEmpId+"S");
									double dblHRate = uF.parseToDouble((String)hm1.get(uF.getDateFormat(strDate, CF.getStrReportDateFormat(), CF.getStrReportDayFormat()).toUpperCase()));
									cellcount.setCellValue( uF.formatIntoTwoDecimal(dblHRate));
								}
								else{
									cellcount.setCellValue( uF.formatIntoTwoDecimal(dblRate));
								}
								
								cellcount.setCellStyle(eStyle.TotalWorkRowStyle());
//								cellcount.setCellValue((String) hmTemp1.get("RATE") != null ? (String) hmTemp1.get("RATE") : "0");

							}

//							if (rownum == row15 && columnCount - 1 == countcell && countcell >= dateStartColumn && countcell <12+additionalColumnCount) {
							if (rownum == row15 && columnCount > 4 && columnCount < 12+additionalColumnCount) {
								
								hmTemp1 = (Map) hmTemp.get((String) _alDate.get(counter)+"_"+strServiceId);
								if (hmTemp1 == null) {
									hmTemp1 = new HashMap();
								}
								if(k == nServiceNumber-1){
									counter++;
								}
								
								double dblRate = 0d;
								double dblLoading = 0d;
								dailyHourRate = 0d;

								String strPayMode = (String) hmTemp1.get("PAYMODE");
								if(strPayMode!=null && strPayMode.equalsIgnoreCase("H")){
								
									dblRate = uF.parseToDouble((String) hmTemp1.get("RATE"));
//									dblLoading = uF.parseToDouble((String) hmTemp1.get(new CommonFunctions().getLoadingWeekDayCode((String) hmTemp1.get("DAY"))));
									dblLoading = uF.parseToDouble((String) hmTemp1.get("LOADING"));
									
									if(dblLoading>0){
										dblRate += dblRate * dblLoading / 100;
									}
									
									dailyHourRate = dblRate * uF.parseToDouble((String) hmTemp1.get("HOURS"));
									totalHourRate1 += dailyHourRate;
									
									setServiceDetails(strEmpId, strServiceId, dailyHourRate, uF, hmEmployeeSummaryContainer, hmEmployeeSummaryDetails);
									
								}else if(strPayMode!=null && strPayMode.equalsIgnoreCase("X")){
									dblRate = uF.parseToDouble((String) hmTemp1.get("RATE"));
									if(!isFixedAdded){
										totalHourRate1 += dblRate;	
										setServiceDetails(strEmpId, strServiceId, dblRate, uF, hmEmployeeSummaryContainer, hmEmployeeSummaryDetails);
									}
									isFixedAdded = true;
									
								}
								
								
								dailyHours = uF.parseToDouble((String) hmTemp1.get("HOURS"));
								
								if(dailyHours==0 && hmHolidays.containsKey(strDate)){
									Map hm1 = (Map)hmPayrollFT.get("D"+strEmpId+"S");
									double dblHRate = uF.parseToDouble((String)hm1.get(uF.getDateFormat(strDate, CF.getStrReportDateFormat(), CF.getStrReportDayFormat()).toUpperCase()));
									dailyHours = 7.5;
									cellcount.setCellValue( uF.formatIntoTwoDecimal(dblHRate * dailyHours));
									cellcount.setCellStyle(eStyle.PayPerDayRowStyle());
									totalHourRate1 += dblHRate * dailyHours;
								}else{
									cellcount.setCellValue( uF.formatIntoTwoDecimal(dailyHourRate));
									cellcount.setCellStyle(eStyle.PayPerDayRowStyle());
								}
								
								cellcount.setCellValue( uF.formatIntoTwoDecimal(dailyHourRate));
								cellcount.setCellStyle(eStyle.PayPerDayRowStyle());
								
								
							}

//							if (rownum == row15 && countcell == 11 + additionalColumnCount) {
//								
////								System.out.println(" 15 WEEK "+nWeek+" "+"rownum="+rownum+"countcell=====>"+countcell);
//								
//								
//								cellcount.setCellValue( uF.formatIntoTwoDecimal(totalHourRate1));
//								cellcount.setCellStyle(eStyle.dateRowStyleBoldBlue());
//							}

						}

//						System.out.println("WEEK "+nWeek+" "+"rownum="+rownum+"countcell=====>"+countcell);
						
						// WEEK 2
						if (nWeek == 2) {
							

							if (rownum == 16 && columnCount > 4 && columnCount < columnDefaultDateCount) {

//								System.out.println("hmTemp===>"+hmTemp);
//								System.out.println("strDate===>"+strDate);
//								System.out.println("alServiceTemp===>"+alServiceTemp);
								
								cellcount.setCellValue((String) _alDay.get(counter));
								cellcount.setCellStyle(eStyle.dayRowStyle());
								
								if(k == nServiceNumber-1){
									counter++;
								}
							}

							if (rownum == 17 && columnCount > 4 && columnCount < columnDefaultDateCount) {
								cellcount.setCellValue((String) _alDate.get(counter));
								cellcount.setCellStyle(eStyle.dateRowStyle());
								if(k == nServiceNumber-1){
									counter++;
								}
							}

							if (rownum == 18 && columnCount > 4 && columnCount < columnDefaultDateCount) {
								hmTemp1 = (Map) hmTemp.get((String) _alDate.get(counter)+"_"+strServiceId);
								if (hmTemp1 == null) {
									hmTemp1 = new HashMap();
								}
								cellcount.setCellValue(  ((String) hmTemp1.get("IN") != null ? uF.getDateFormat((String) hmTemp1.get("IN"), DBTIME, CF.getStrReportTimeAM_PMFormat()):"")) ;
								
								if(hmLeaves.containsKey(strDate)){
									String strColour  = hmLeavesColour.get(hmLeaves.get(strDate));
									if(strColour.equalsIgnoreCase("A")){
										cellcount.setCellStyle(eStyle.startTimeRowStyleAnnualLeave());	
									}else if(strColour.equalsIgnoreCase("S")){
										cellcount.setCellStyle(eStyle.startTimeRowStyleSickLeave());
									}
								}else if(hmHolidays.containsKey(strDate)){
									cellcount.setCellStyle(eStyle.startTimeRowStylePublicHoliday());
								}else{
									cellcount.setCellStyle(eStyle.startimeRowStyle());
								}
								
								
								if(k == nServiceNumber-1){
									counter++;
								}
							}
							if (rownum == 19 && columnCount > 4 && columnCount < columnDefaultDateCount) {
								hmTemp1 = (Map) hmTemp.get((String) _alDate.get(counter)+"_"+strServiceId);
								if (hmTemp1 == null) {
									hmTemp1 = new HashMap();
								}
								cellcount.setCellValue(  ((String) hmTemp1.get("OUT") != null ? uF.getDateFormat((String) hmTemp1.get("OUT"), DBTIME, CF.getStrReportTimeAM_PMFormat()):"")) ;
								if(hmLeaves.containsKey(strDate)){
									String strColour  = hmLeavesColour.get(hmLeaves.get(strDate));
									if(strColour.equalsIgnoreCase("A")){
										cellcount.setCellStyle(eStyle.startTimeRowStyleAnnualLeave());	
									}else if(strColour.equalsIgnoreCase("S")){
										cellcount.setCellStyle(eStyle.startTimeRowStyleSickLeave());
									}
								}else if(hmHolidays.containsKey(strDate)){
									cellcount.setCellStyle(eStyle.startTimeRowStylePublicHoliday());
								}else{
									cellcount.setCellStyle(eStyle.startimeRowStyle());
								}
								if(k == nServiceNumber-1){
									counter++;
								}
							}

							if (rownum == 20 && columnCount > 4 && columnCount < columnDefaultDateCount) {
								hmTemp1 = (Map) hmTemp.get((String) _alDate.get(counter)+"_"+strServiceId);
								if (hmTemp1 == null) {
									hmTemp1 = new HashMap();
								}
								
								dailyHours = uF.parseToDouble((String) hmTemp1.get("HOURS"));
								
								if(dailyHours==0 && hmHolidays.containsKey(strDate)){
									Map hm1 = (Map)hmPayrollFT.get("D"+strEmpId+"S");
									if(hm1==null){
										hm1 = new HashMap();
									}
									double dblHRate = uF.parseToDouble((String)hm1.get(uF.getDateFormat(strDate, CF.getStrReportDateFormat(), CF.getStrReportDayFormat()).toUpperCase()));
									dailyHours = 7.5;
									cellcount.setCellValue(dailyHours);
									cellcount.setCellStyle(eStyle.TotalWorkRowStyle());
								}else{
									cellcount.setCellValue((String) hmTemp1.get("HOURS") != null ? (String) hmTemp1.get("HOURS") : "0.0");
									cellcount.setCellStyle(eStyle.TotalWorkRowStyle());
								}
								cellcount.setCellValue((String) hmTemp1.get("HOURS") != null ? (String) hmTemp1.get("HOURS") : "0.0");
								cellcount.setCellStyle(eStyle.TotalWorkRowStyle());
								
								totalHours2 += dailyHours;
								
								
								
								if(k == nServiceNumber-1){
									counter++;
								}

							}

							
								
							if (rownum == 20 && countcell == 11+additionalColumnCount) {
								
								cellcount.setCellValue(uF.formatIntoOneDecimal(totalHours2));
								cellcount.setCellStyle(eStyle.dateRowStyleBold());
								
							}
							

							if (rownum == 21 && columnCount > 4 && columnCount < columnDefaultDateCount) {
								hmTemp1 = (Map) hmTemp.get((String) _alDate.get(counter)+"_"+strServiceId);
								if (hmTemp1 == null) {
									hmTemp1 = new HashMap();
								}
								
								if(k == nServiceNumber-1){
									counter++;
								}
								
								double dblRate = 0d;
								double dblLoading = 0d;

								String strPayMode = (String) hmTemp1.get("PAYMODE");
								

//								System.out.println("strPayMode====>"+strPayMode);
//								System.out.println("hmTemp====>"+hmTemp);
//								System.out.println("hmTemp1====>"+hmTemp1);
								
								
								if(strPayMode!=null && strPayMode.equalsIgnoreCase("H")){
								
									dblRate = uF.parseToDouble((String) hmTemp1.get("RATE"));
//									dblLoading = uF.parseToDouble((String) hmTemp1.get(new CommonFunctions().getLoadingWeekDayCode((String) hmTemp1.get("DAY"))));
									dblLoading = uF.parseToDouble((String) hmTemp1.get("LOADING"));
									
//									System.out.println("dblRate====>"+dblRate);
									
									if(dblLoading>0){
										dblRate += dblRate * dblLoading / 100;
									}
									
//									System.out.println("dblRate====>"+dblRate);

									cellcount.setCellValue( uF.formatIntoTwoDecimal(dblRate));
									
								}else if(strPayMode!=null && strPayMode.equalsIgnoreCase("X")){
									cellcount.setCellValue("Fixed");
								
								}else if(hmHolidays.containsKey(strDate)){
									Map hm1 = (Map)hmPayrollFT.get("D"+strEmpId+"S");
									if(hm1==null){
										hm1 = new HashMap();
									}
									double dblHRate = uF.parseToDouble((String)hm1.get(uF.getDateFormat(strDate, CF.getStrReportDateFormat(), CF.getStrReportDayFormat()).toUpperCase()));
									cellcount.setCellValue( uF.formatIntoTwoDecimal(dblHRate));
								}
								
								else{
									cellcount.setCellValue( uF.formatIntoTwoDecimal(dblRate));
								}
								
								
								
								cellcount.setCellStyle(eStyle.TotalWorkRowStyle());
//								cellcount.setCellValue((String) hmTemp1.get("RATE") != null ? (String) hmTemp1.get("RATE") : "0");

							}

							if (rownum == 22 && columnCount - 1 == countcell && countcell >= dateStartColumn && countcell < columnDefaultDateCount) {
								hmTemp1 = (Map) hmTemp.get((String) _alDate.get(counter)+"_"+strServiceId);
								if (hmTemp1 == null) {
									hmTemp1 = new HashMap();
								}
								
								if(k == nServiceNumber-1){
									counter++;
								}
								
								
								double dblRate = 0d;
								double dblLoading = 0d;
								dailyHourRate = 0d;

								String strPayMode = (String) hmTemp1.get("PAYMODE");
								if(strPayMode!=null && strPayMode.equalsIgnoreCase("H")){
								
									dblRate = uF.parseToDouble((String) hmTemp1.get("RATE"));
//									dblLoading = uF.parseToDouble((String) hmTemp1.get(new CommonFunctions().getLoadingWeekDayCode((String) hmTemp1.get("DAY"))));
									dblLoading = uF.parseToDouble((String) hmTemp1.get("LOADING"));
									
									if(dblLoading>0){
										dblRate += dblRate * dblLoading / 100;
									}
									
									dailyHourRate = dblRate * uF.parseToDouble((String) hmTemp1.get("HOURS"));
									totalHourRate2 += dailyHourRate;
									
									setServiceDetails(strEmpId, strServiceId, dailyHourRate, uF, hmEmployeeSummaryContainer, hmEmployeeSummaryDetails);
									
									
									
									
								}else if(strPayMode!=null && strPayMode.equalsIgnoreCase("X")){
									dblRate = uF.parseToDouble((String) hmTemp1.get("RATE"));
									if(!isFixedAdded){
										totalHourRate1 += dblRate;
										setServiceDetails(strEmpId, strServiceId, dblRate, uF, hmEmployeeSummaryContainer, hmEmployeeSummaryDetails);
									}
									isFixedAdded = true;
									
								}
								
								dailyHours = uF.parseToDouble((String) hmTemp1.get("HOURS"));
								
								if(dailyHours==0 && hmHolidays.containsKey(strDate)){
									Map hm1 = (Map)hmPayrollFT.get("D"+strEmpId+"S");
									if(hm1==null){
										hm1 = new HashMap();
									}
									double dblHRate = uF.parseToDouble((String)hm1.get(uF.getDateFormat(strDate, CF.getStrReportDateFormat(), CF.getStrReportDayFormat()).toUpperCase()));
									dailyHours = 7.5;
									cellcount.setCellValue( uF.formatIntoTwoDecimal(dblHRate * dailyHours));
									cellcount.setCellStyle(eStyle.PayPerDayRowStyle());
									totalHourRate2 += dblHRate * dailyHours;
								}else{
									cellcount.setCellValue( uF.formatIntoTwoDecimal(dailyHourRate));
									cellcount.setCellStyle(eStyle.PayPerDayRowStyle());
								}
								cellcount.setCellValue( uF.formatIntoTwoDecimal(dailyHourRate));
								cellcount.setCellStyle(eStyle.PayPerDayRowStyle());
								
							}

//							if (rownum == 22 && countcell == 11 + additionalColumnCount) {
//								cellcount.setCellValue( uF.formatIntoOneDecimal(totalHourRate2));
//								
//							}

						}

						if (rownum == 25 && countcell == 8+additionalColumnCount) {
							cellcount.setCellValue(uF.formatIntoOneDecimal(totalHours1 + totalHours2));
							
						}
						if (rownum == 25 && countcell == 11+additionalColumnCount) {
							double dblRate = 0d;
							String strPayMode = (String) hmTemp.get("PAYMODE");
							
//							if(strPayMode!=null && strPayMode.equalsIgnoreCase("X")){
//								dblRate = uF.parseToDouble((String) hmTemp.get("FRATE"));
//								
//								cellcount.setCellValue( uF.formatIntoTwoDecimal(dblRate));
//							}else{
//								cellcount.setCellValue( uF.formatIntoTwoDecimal(totalHourRate1 + totalHourRate2));	
//							}
							
							dblRate = uF.parseToDouble((String) hmTemp.get("FRATE"));
							
							cellcount.setCellValue( uF.formatIntoTwoDecimal(totalHourRate1 + totalHourRate2));
							
						}

						if (rownum == 26 && countcell == 11+additionalColumnCount) {
							double dblRate = 0d;
							String strPayMode = (String) hmTemp.get("PAYMODE");
							dblRate = uF.parseToDouble((String) hmTemp.get("FRATE"));
							
							
								cellcount.setCellValue( uF.formatIntoTwoDecimal(totalHourRate1 + totalHourRate2));
								dedAmount = new CommonFunctions().getDeductionAmountMap(totalHourRate1 + totalHourRate2);
							
							
//							if(strPayMode!=null && strPayMode.equalsIgnoreCase("X")){
//								dblRate = uF.parseToDouble((String) hmTemp.get("FRATE"));
//								cellcount.setCellValue(dblRate);
//								dedAmount = new CommonFunctions().getDeductionAmountMap(dblRate);
//							}else{
//								dedAmount = new CommonFunctions().getDeductionAmountMap(totalHourRate1 + totalHourRate2);	
//							}
							
							cellcount.setCellValue( uF.formatIntoTwoDecimal(dedAmount));
						}
						
						
						
						
						if (rownum == 27 && countcell == 11+additionalColumnCount) {
							
//							System.out.println("hmFirstAidAllowance======>"+hmFirstAidAllowance);
//							System.out.println("strEmpId======>"+strEmpId);
							
							
							if (hmFirstAidAllowance.containsKey(strEmpId)) {
								
								double dblRate = 0d;
								String strPayMode = (String) hmTemp.get("PAYMODE");
								dblRate = uF.parseToDouble((String) hmTemp.get("FRATE"));
//								if(strPayMode!=null && strPayMode.equalsIgnoreCase("X")){
//									dblRate = uF.parseToDouble((String) hmTemp.get("FRATE"));
//									dblAllowance = new CommonFunctions().getAllowanceValue(totalHours1 + totalHours2, uF.parseToInt(strEmpId));
//									dblAllowance = (dblRate) * dblAllowance / 100;
//								}else{
//									dblAllowance = new CommonFunctions().getAllowanceValue(totalHours1 + totalHours2, uF.parseToInt(strEmpId));
//									dblAllowance = (totalHourRate1 + totalHourRate2) * dblAllowance / 100;	
//								}
								
								
//									cellcount.setCellValue( uF.formatIntoTwoDecimal(totalHourRate1 + totalHourRate2));
									dblAllowance = new CommonFunctions().getAllowanceValue(totalHours1 + totalHours2, uF.parseToInt(strEmpId));
									dblAllowance = (totalHourRate1 + totalHourRate2) * dblAllowance / 100;
								
								
							}
							cellcount.setCellValue( uF.formatIntoTwoDecimal(dblAllowance));
						}
						
						if (rownum == 28 && countcell == 11+additionalColumnCount) {
							double dblRate = 0d;
							String strPayMode = (String) hmTemp.get("PAYMODE");
							dblRate = uF.parseToDouble((String) hmTemp.get("FRATE"));
//							if(strPayMode!=null && strPayMode.equalsIgnoreCase("X")){
//								dblRate = uF.parseToDouble((String) hmTemp.get("FRATE"));
//								cellcount.setCellValue( uF.formatIntoTwoDecimal((dblRate - dedAmount + dblAllowance)));
//								cellcount.setCellStyle(eStyle.FootRowcellStyleBold(hwb));
//							}else{
//								cellcount.setCellValue( uF.formatIntoTwoDecimal((totalHourRate1 + totalHourRate2 - dedAmount + dblAllowance)));
//								cellcount.setCellStyle(eStyle.FootRowcellStyleBold(hwb));
//								
//							}
							
							
//							System.out.println("totalHourRate2 ===>"+totalHourRate2 );
							
							
								cellcount.setCellValue( uF.formatIntoTwoDecimal(totalHourRate1 + totalHourRate2 - dedAmount + dblAllowance));
								cellcount.setCellStyle(eStyle.FootRowcellStyleBold());
							
							
						}
						
						if (rownum == 25 && countcell == 13+additionalColumnCount) {
							double dblRate = 0d;
							String strPayMode = (String) hmTemp.get("PAYMODE");
							dblRate = uF.parseToDouble((String) hmTemp.get("FRATE"));
//							if(strPayMode!=null && strPayMode.equalsIgnoreCase("X")){
//								dblRate = uF.parseToDouble((String) hmTemp.get("FRATE"));
//								cellcount.setCellValue( uF.formatIntoTwoDecimal((dblRate - dedAmount + dblAllowance)));
//							}else{
//								cellcount.setCellValue( uF.formatIntoTwoDecimal((totalHourRate1 + totalHourRate2 - dedAmount + dblAllowance)));	
//							}
							
							
								cellcount.setCellValue( uF.formatIntoTwoDecimal(totalHourRate1 + totalHourRate2 - dedAmount + dblAllowance));
								
							
							
						}
						if (rownum == 26 && countcell == 13+additionalColumnCount) {
							cellcount.setCellValue( uF.formatIntoTwoDecimal(dedAmount));
						}
						if (rownum == 27 && countcell == 13+additionalColumnCount) {
							cellcount.setCellValue( uF.formatIntoTwoDecimal(dblAllowance));
						}

						
						hmEmployeeSummaryDetails.put("NAME", hmEmpFullNameMap.get(strEmpId));
						hmEmployeeSummaryDetails.put("TAX", uF.formatIntoTwoDecimal(dedAmount));
						hmEmployeeSummaryDetails.put("NET", uF.formatIntoTwoDecimal(totalHourRate1 + totalHourRate2 - dedAmount + dblAllowance));
						hmEmployeeSummaryDetails.put("GROSS", uF.formatIntoTwoDecimal(totalHourRate1 + totalHourRate2));
						
						
						hmEmployeeSummaryContainer.put(strEmpId, hmEmployeeSummaryDetails);
					}
					
					
				}

				}  

				String filePath = request.getRealPath("/export/");
				
				
//				String filePath = null;
//				if(request==null){
//					filePath = "C:\\temp\\";
//				}
				
				if(!isAdmin){
					writeExcelFile(filePath + File.separator+(String)hmTemp.get("GEN_DATE") + "_" + strEmpId + ".xls", hwb, sheet, isAdmin, strEmpId);
				}else{
					writeExcelFile(filePath + File.separator+(String)hmTemp.get("GEN_DATE")+".xls", hwb, sheet, isAdmin, strEmpId);
				}
				
//				System.out.println("hmEmployeeSummaryContainer====>"+hmEmployeeSummaryContainer);
			}
			
			
			
			if(isAdmin){
				
				

				String filePath = request.getRealPath("/export/");
				
//				String filePath = null;
//				if(request==null){
//					filePath = "C:\\temp\\";
//				}
				
				

				

				
				sheet = hwb.createSheet("Summary");
				
				addSummaryHeader(eStyle, sheet);
				sheet.setDefaultColumnWidth(12);
				
				HSSFRow rowhead1 = sheet.createRow(0);				
				rowhead1.setHeightInPoints(6 * 5);
				
				rowhead1 = sheet.createRow(1);				
				rowhead1.setHeightInPoints(4 * 5);
				
				rowhead1 = sheet.createRow(2);				
				rowhead1.setHeightInPoints(4 * 5);
				
				rowhead1 = sheet.createRow(3);				
				rowhead1.setHeightInPoints(4 * 5);
				
				rowhead1 = sheet.createRow(4);				
				rowhead1.setHeightInPoints(4 * 5);
				
				
				Set setSummary = hmEmployeeSummaryContainer.keySet();
				Iterator itSummary = setSummary.iterator();
				int rowNumSummary = 10;
				int nSerialNumber = 0;
				
				double dblTaxSum = 0;
				double dblGrossSum = 0;
				double dblNetSum = 0;
				
				HSSFRow rowhead = sheet.createRow(rowNumSummary);				
				HSSFCell cell = rowhead.createCell(1);				
				cell.setCellValue("EMPLOYEE");
				cell.setCellStyle(eStyle.bold(false, false, false, 0));
				sheet.setColumnWidth(1, 40 * 256);
				
				addLogoImage(hwb, sheet, 1);
				
				cell = rowhead.createCell(2);
				cell.setCellValue("NET");
				cell.setCellStyle(eStyle.bold(false, false, true, 0));
				sheet.setColumnWidth(2, 20 * 256);
				
				cell = rowhead.createCell(3);
				cell.setCellValue("TAX");
				cell.setCellStyle(eStyle.bold(false, false, true, 0));
				sheet.setColumnWidth(3, 15 * 256);
				
				
				cell = rowhead.createCell(4);
				cell.setCellValue("GROSS");
				cell.setCellStyle(eStyle.bold(false, false, true, 0));
				sheet.setColumnWidth(4, 20 * 256);
				
				cell = rowhead.createCell(5);
				cell.setCellValue("COST CENTRE");
				cell.setCellStyle(eStyle.bold(false, false, false, 0));
				sheet.setColumnWidth(5, 25 * 256);
				
				cell = rowhead.createCell(7);
				cell.setCellValue("NET");
				cell.setCellStyle(eStyle.bold(false, false, true, 0));
				sheet.setColumnWidth(7, 20 * 256);
				
				cell = rowhead.createCell(8);
				cell.setCellValue("TAX");
				cell.setCellStyle(eStyle.bold(false, false, true, 0));
				sheet.setColumnWidth(8, 15 * 256);
				
				cell = rowhead.createCell(9);
				cell.setCellValue("GROSS");
				cell.setCellStyle(eStyle.bold(false, false, true, 0));
				sheet.setColumnWidth(9, 20 * 256);
				
				
				while(itSummary.hasNext()){
					String strEmpId = (String)itSummary.next();
					Map hmSummary = (Map)hmEmployeeSummaryContainer.get(strEmpId);
					int nServiceCount = 0;
					int nServiceSize = 0;
					++nSerialNumber;
					
					Map hmService = (Map)hmSummary.get("COST_CENTRE");
					
					if(hmService!=null){
						nServiceSize = hmService.size();
					}
					
					
					HSSFCellStyle cellServiceColour = null;
					
					Set setServices = null;
					if(hmService!=null){
						setServices = hmService.keySet();
						Iterator itService = setServices.iterator();
						
						
						
						
						while(itService.hasNext()){
							String strServiceId = (String)itService.next();
							Map hmTemp1 = (Map)hmService.get(strServiceId);
							nServiceCount++;
							
							
							
							
							
							HSSFCellStyle serviceColour = null;
							HSSFCellStyle serviceColourRightAlign = null;
							
							
							if(nServiceSize>1 && nServiceCount==1){
								serviceColour = getServiceColour(strServiceId, eStyle, true, false, false);
								serviceColourRightAlign = getServiceColour(strServiceId, eStyle, true, false, true);
							}else if(nServiceCount == nServiceSize){
								serviceColour = getServiceColour(strServiceId, eStyle, false, false, false);
								serviceColourRightAlign = getServiceColour(strServiceId, eStyle, false, false, true);
							}
//							if(nServiceCount == nServiceSize && nServiceCount>1){
							
							rowhead = sheet.createRow(++rowNumSummary);
							
							cell = rowhead.createCell(0);
							cell.setCellValue(nSerialNumber);
							cell.setCellStyle(serviceColour);
							
							cell = rowhead.createCell(1);
							
							cell.setCellValue((String)hmSummary.get("NAME"));
							cell.setCellStyle(serviceColour);
							
							CreationHelper createHelper = hwb.getCreationHelper();
							org.apache.poi.ss.usermodel.Hyperlink link = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
							link.setAddress("'"+(String)hmSummary.get("NAME")+"'!A1");
							cell.setHyperlink(link);

							
							cell = rowhead.createCell(2);
							cell.setCellValue((String)hmTemp1.get("GROSS"));
							cell.setCellStyle(serviceColourRightAlign);
							dblGrossSum += uF.parseToDouble((String)hmTemp1.get("GROSS"));
							
							if(nServiceCount == 1){
								cell = rowhead.createCell(3);
								cell.setCellValue((String)hmSummary.get("TAX"));
								cell.setCellStyle(serviceColourRightAlign);
								
								dblTaxSum += uF.parseToDouble((String)hmSummary.get("TAX"));
								
								
								cell = rowhead.createCell(4);
								cell.setCellValue((String)hmSummary.get("NET"));
								cell.setCellStyle(serviceColourRightAlign);
								dblNetSum += uF.parseToDouble((String)hmSummary.get("NET"));
							}else{
								cell = rowhead.createCell(3);
								cell.setCellStyle(serviceColourRightAlign);
								
								cell = rowhead.createCell(4);
								cell.setCellStyle(serviceColourRightAlign);
							}
							
							
							Map hmTemp2 = (Map)hmEmployeeSummaryService.get(strServiceId);
							if(hmTemp2==null){
								hmTemp2 = new HashMap();
							}
							double dblGrossSummary = uF.parseToDouble((String)hmTemp2.get("GROSS"));
							dblGrossSummary += uF.parseToDouble((String)hmTemp1.get("GROSS"));
							hmTemp2.put("GROSS", uF.formatIntoTwoDecimal(dblGrossSummary));
							hmEmployeeSummaryService.put(strServiceId, hmTemp2);
							
							
							cell = rowhead.createCell(5);
							cell.setCellValue((String)hmServiceMap.get(strServiceId));
							cell.setCellStyle(serviceColour);
							
//							rowhead.setRowStyle(serviceColour);
							
							if(nServiceCount == nServiceSize && nServiceCount>1){
								rowhead = sheet.createRow(++rowNumSummary);
								
								serviceColour = getServiceColour(strServiceId, eStyle, false, true, false);
								rowhead.setRowStyle(serviceColour);
								
								
								cell = rowhead.createCell(0);
								cell.setCellValue(nSerialNumber);
								cell.setCellStyle(eStyle.bold(false, true, false, 2));
								
								cell = rowhead.createCell(1);
								cell.setCellValue(((String)hmSummary.get("NAME")).toUpperCase()+ " TOTAL");
								cell.setCellStyle(eStyle.bold(false, true, false, 2));
								
								createHelper = hwb.getCreationHelper();
								link = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
								link.setAddress("'"+((String)hmSummary.get("NAME"))+ "'!A1");
								cell.setHyperlink(link);
								
								
								cell = rowhead.createCell(2);
								cell.setCellStyle(eStyle.bold(false, true, true, 2));
								
								cell = rowhead.createCell(3);
								cell.setCellStyle(eStyle.bold(false, true, true, 2));
								
								cell = rowhead.createCell(4);
								cell.setCellStyle(eStyle.bold(false, true, true, 2));
								
								cell = rowhead.createCell(5);
								cell.setCellStyle(eStyle.bold(false, true, true, 2));
								
								cell = rowhead.createCell(6);
								cell.setCellStyle(eStyle.bold(false, true, true, 2));
								
								
								cell = rowhead.createCell(7);
								cell.setCellValue((String)hmSummary.get("GROSS"));
								cell.setCellStyle(eStyle.bold(false, true, true, 2));
								
								cell = rowhead.createCell(8);
								cell.setCellValue((String)hmSummary.get("TAX"));
								cell.setCellStyle(eStyle.bold(false, true, true, 2));
								
								cell = rowhead.createCell(9);						    
								cell.setCellValue((String)hmSummary.get("NET"));
								cell.setCellStyle(eStyle.bold(false, true, true, 2));
								
							}
							
							
							
							if(nServiceCount == 1 && nServiceSize>1){
								
								cell = rowhead.createCell(6);
								cell.setCellStyle(eStyle.bold(true, false, true, 2));
								
								cell = rowhead.createCell(7);
								cell.setCellStyle(eStyle.bold(true, false, true, 2));
								
								cell = rowhead.createCell(8);
								cell.setCellStyle(eStyle.bold(true, false, true, 2));
								
								cell = rowhead.createCell(9);						    
								cell.setCellStyle(eStyle.bold(true, false, true, 2));
								
							}
							
							
							
						}
					}
				}
				
				
				*//**
				 * 
				 * Total Row at the bottom
				 * 
				 * *//*
				
				++rowNumSummary;
				rowhead = sheet.createRow(++rowNumSummary);
				
				
				cell = rowhead.createCell(1);
				cell.setCellValue("Total");
				cell.setCellStyle(eStyle.bold(false, false, false, 0));
				
				cell = rowhead.createCell(2);
				cell.setCellValue(uF.formatIntoTwoDecimal(dblGrossSum));
				cell.setCellStyle(eStyle.bold(false, false, true, 0));
				
				cell = rowhead.createCell(3);
				cell.setCellValue(uF.formatIntoTwoDecimal(dblTaxSum));
				cell.setCellStyle(eStyle.bold(false, false, true, 0));
				
				cell = rowhead.createCell(4);
				cell.setCellValue(uF.formatIntoTwoDecimal(dblNetSum));
				cell.setCellStyle(eStyle.bold(false, false, true, 0));
				
				
				
				*//**
				 * 
				 * Total Table in summary format at the bottom of the sheet
				 * 
				 * *//*
				
				
				Map hmServicePrevSummary = getPrevServiceSummary();
				
				HSSFCellStyle serviceColour = null;
				HSSFCellStyle serviceColourRightAlign = null;
				
				
				
				
				++rowNumSummary;
				rowhead = sheet.createRow(++rowNumSummary);
				
				
				cell = rowhead.createCell(1);
				cell.setCellValue("COST CENTRE TOTALS");
				cell.setCellStyle(eStyle.bold(true, true, false, 1));
				
				cell = rowhead.createCell(2);
				cell.setCellStyle(eStyle.bold(true, true, true, 1));
				
				cell = rowhead.createCell(3);
				cell.setCellValue("Week Ending\n"+ (String)hmServicePrevSummary.get("END_DATE"));
				cell.setCellStyle(eStyle.bold(true, true, true, 1));
				
				cell = rowhead.createCell(4);
				cell.setCellValue("% Change");
				cell.setCellStyle(eStyle.bold(true, true, true, 1));
				
				
				
				Set setServiceSummary = hmEmployeeSummaryService.keySet();
				Iterator itServiceSummary = setServiceSummary.iterator();
				
				double dblCurrentTotal = 0;
				double dblPrevTotal = 0;
				double dblChangeTotal = 0;
				
				
				while(itServiceSummary.hasNext()){
					String strServiceId = (String)itServiceSummary.next();
					Map hmTemp3 = (Map)hmEmployeeSummaryService.get(strServiceId);
					String strGrossPrev = (String)hmServicePrevSummary.get(strServiceId);
					if(hmTemp3==null){
						hmTemp3 = new HashMap();
					}
					
					serviceColour = getServiceColour(strServiceId, eStyle, false, false, false);
					serviceColourRightAlign = getServiceColour(strServiceId, eStyle, false, false, true);
					
					
					System.out.println("strServiceId===>"+strServiceId+"===");
					
					rowhead = sheet.createRow(++rowNumSummary);
					cell = rowhead.createCell(1);
					cell.setCellValue((String)hmServiceMap.get(strServiceId));
					cell.setCellStyle(serviceColour);
					
					
					cell = rowhead.createCell(2);
					cell.setCellValue((String)hmTemp3.get("GROSS"));
					cell.setCellStyle(serviceColourRightAlign);
					dblCurrentTotal += uF.parseToDouble((String)hmTemp3.get("GROSS")); 
					
					
					cell = rowhead.createCell(3);
					cell.setCellValue(uF.formatIntoTwoDecimal(uF.parseToDouble(strGrossPrev)));
					cell.setCellStyle(serviceColourRightAlign);
					dblPrevTotal += uF.parseToDouble(strGrossPrev);
					
					
					double dblCurrent = uF.parseToDouble((String)hmTemp3.get("GROSS"));
					double dblPrevious = uF.parseToDouble(strGrossPrev);
					double dblChange = (dblCurrent - dblPrevious) / dblCurrent;
					double dblChangPerCent = dblChange * 100;
					dblChangeTotal += dblChangPerCent; 
					
					cell = rowhead.createCell(4);
					cell.setCellValue(uF.formatIntoOneDecimal(dblChangPerCent)+"%");
					cell.setCellStyle(serviceColourRightAlign);
					
//					rowhead.setRowStyle(serviceColour);
					
				}
				
				
				++rowNumSummary;
				rowhead = sheet.createRow(++rowNumSummary);
				cell = rowhead.createCell(1);
				cell.setCellValue("COMBINED COST CENTRE TOTALS");
				cell.setCellStyle(eStyle.redColour(true, true, false));
				
				cell = rowhead.createCell(2);
				cell.setCellValue(uF.formatIntoTwoDecimal(dblCurrentTotal));
				cell.setCellStyle(eStyle.redColour(true, true, true));
				
				cell = rowhead.createCell(3);
				cell.setCellValue(uF.formatIntoTwoDecimal(dblPrevTotal));
				cell.setCellStyle(eStyle.redColour(true, true, true));
				
				cell = rowhead.createCell(4);
				cell.setCellValue(uF.formatIntoOneDecimal(dblChangeTotal)+"%");
				cell.setCellStyle(eStyle.redColour(true, true, true));
				
				writeExcelFile(filePath + File.separator+(String)hmTemp.get("GEN_DATE")+".xls", hwb, sheet, isAdmin, null);
			}
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

	private void writeExcelFile(String filename, HSSFWorkbook hwb, HSSFSheet sheet, boolean isAdmin, String strEmpId) {

		FileOutputStream fileOut = null;
		try {

			fileOut = new FileOutputStream(filename);
			hwb.write(fileOut);
			fileOut.close();
			
			
//			System.out.println("======= Calling MAIL FUNCTION =======isAdmin="+isAdmin);
//			System.out.println("======= FILE PATH ="+filename);
			
			if(!isAdmin){
				
//				Notifications nF = new Notifications(N_NEW_SALARY_GENERATION);
//				nF.setStrEmailTo((String)hmEmpEmailMap.get(strEmpId));
//				nF.setStrEmpMobileNo((String)hmEmpFullNameMap.get(strEmpId));
//				nF.setStrEmpCode((String)hmEmpCodeMap.get(strEmpId));
//				nF.setStrEmpFullNamename((String)hmEmpFullNameMap.get(strEmpId));
//				if(fileOut!=null){
//					nF.setStrAttachmentFileSource(filename);
//				}
//				System.out.println("======= SENDING MAIL TO "+(String)hmEmpEmailMap.get(strEmpId)+"=======");
//				nF.sendNotifications();
				
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	*/
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public static void main(String args[]) {

		try {

			PaySlips p = new PaySlips();
			p.execute("06/10/2011","19/10/2011");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getStrD1() {
		return strD1;
	}

	public void setStrD1(String strD1) {
		this.strD1 = strD1;
	}

	public String getStrD2() {
		return strD2;
	}

	public void setStrD2(String strD2) {
		this.strD2 = strD2;
	}
	
	
	
	void setServiceDetails(String strEmpId, String strServiceId, double dailyHourRate, UtilityFunctions uF, Map hmEmployeeSummaryContainer, Map hmEmployeeSummaryDetails){
		
		Map hmEmployeeSummaryService = null;
		try {
			
			
			// Service for Summary
			
			Map hmSumaryDetails  = (Map)hmEmployeeSummaryContainer.get(strEmpId);
			if(hmSumaryDetails==null){
				hmSumaryDetails = new HashMap();
			}
			hmEmployeeSummaryService = (Map)hmSumaryDetails.get("COST_CENTRE");
			if(hmEmployeeSummaryService==null){
				hmEmployeeSummaryService = new HashMap();
			}
			Map hmServiceTemp = (Map)hmEmployeeSummaryService.get(strServiceId);
			if(hmServiceTemp==null){
				hmServiceTemp = new HashMap();
			}
			
			double dblServiceGross = uF.parseToDouble((String)hmServiceTemp.get("GROSS"));
			dblServiceGross += dailyHourRate;
			
			hmServiceTemp.put("GROSS", uF.formatIntoTwoDecimal(dblServiceGross));
			if(uF.parseToInt(strServiceId)>0){
				hmEmployeeSummaryService.put(strServiceId, hmServiceTemp);
			}
			hmEmployeeSummaryDetails.put("COST_CENTRE", hmEmployeeSummaryService);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
//	public HSSFCellStyle getServiceColour(String serviceId, Excelstyle eStyle, boolean isTop, boolean isBottom, boolean isRightAlign){
// 		
//		HSSFCellStyle serviceColour = null;
//		
// 		try {
//	
// 			
// 			Map ServiceColourMap = CF.getServiceColour();
// 			
// 			String strColour = (String)ServiceColourMap.get(serviceId);
// 			
// 			if(strColour!=null && strColour.equalsIgnoreCase("YELLOW")){
// 				serviceColour = eStyle.yellowColour(isTop, isBottom, isRightAlign);
// 			}else if(strColour!=null && strColour.equalsIgnoreCase("PINK")){
// 				serviceColour = eStyle.pinkColour(isTop, isBottom, isRightAlign);
// 			}else if(strColour!=null && strColour.equalsIgnoreCase("GREY")){
// 				serviceColour = eStyle.greyColour(isTop, isBottom, isRightAlign);
// 			}else{
// 				serviceColour = eStyle.whiteColour(isTop, isBottom, isRightAlign);
// 			}
// 			
// 			
// 			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
// 		
//		return serviceColour;
// 	}
	
	
	
	

	public Map<String, String> getPrevServiceSummary() {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		Map hmServicePrevGross = new HashMap();
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from payroll where generate_date in (select generate_date from ( select distinct(generate_date) from payroll order by generate_date desc limit 2 ) a order by a.generate_date limit 1 ) order by generate_date, emp_id");
			rs = pst.executeQuery();
			
			String strServiceId = null;
			String strEmpNewId = null;
			String strEmpOldId = null;
			boolean isFixed = false;
			double dblGross = 0;
			while (rs.next()) {
				
				strEmpNewId = rs.getString("emp_id");
				strServiceId = rs.getString("service_id");
				if(uF.parseToInt(strServiceId)==0){
					continue;
				}
				
				if(strEmpNewId!=null && !strEmpNewId.equalsIgnoreCase(strEmpOldId)){
					isFixed = false;
				}
				
				dblGross = uF.parseToDouble((String)hmServicePrevGross.get(strServiceId));
				if(!isFixed){
					dblGross += rs.getDouble("income_amount");
					if(rs.getString("pay_mode")!=null && rs.getString("pay_mode").equalsIgnoreCase("X")){
						isFixed = true;
					}
				}
				
				hmServicePrevGross.put(strServiceId, uF.formatIntoTwoDecimal(dblGross));
				hmServicePrevGross.put("END_DATE", uF.getDateFormat(rs.getString("date_to"), DBDATE, CF.getStrReportDateFormat()));
				strEmpOldId  = strEmpNewId ;
			}
		rs.close();
		pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
		return hmServicePrevGross;
	}

}
