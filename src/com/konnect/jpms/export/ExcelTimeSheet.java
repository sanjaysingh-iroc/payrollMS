package com.konnect.jpms.export;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class ExcelTimeSheet implements ServletRequestAware, ServletResponseAware, IStatements, IConstants {

	UtilityFunctions UF;
	CommonFunctions CF;
	HttpSession session;
	HttpServletRequest request;
	HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {

		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	HSSFSheet monthly_time_sheet;
	HSSFSheet conveyance_sheet;
	HSSFWorkbook workbook;

	{
		workbook = new HSSFWorkbook();
		monthly_time_sheet = workbook.createSheet("Time Report");
		conveyance_sheet = workbook.createSheet("Conveyance Sheet");
	}

	public String execute() {

		UF = new UtilityFunctions();
		if(CF==null){
//			CF = new CommonFunctions(request);
		}
		System.out.println("printing dates" + getDatefrom() + "toooo" + getDateto());
		Map<String, Map<String, String>> projectdetails = new HashMap<String, Map<String, String>>();
		List<String> nameheaderlist = new ArrayList<String>();
		List<Integer> leavesdaylist = new ArrayList<Integer>();
		List<Integer> holidayslist = new ArrayList<Integer>();
		HashMap<String, ArrayList<String>> finaltask = new HashMap<String, ArrayList<String>>();
		List<String> idList = new ArrayList<String>();
		Map<String, List<String>> mapconveyancetravel = new HashMap<String, List<String>>();
		Map<String, List<String>> mapconveyanceothers = new HashMap<String, List<String>>();
		try {
			fillsheet(projectdetails, nameheaderlist, leavesdaylist, holidayslist, finaltask, idList, mapconveyancetravel, mapconveyanceothers);
			createExcelFile(projectdetails, nameheaderlist, leavesdaylist, holidayslist, finaltask, idList, mapconveyancetravel, mapconveyanceothers);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {

			e.printStackTrace();
		}

		return null;
	}

	String datefrom;

	public String getDatefrom() {
		return datefrom;
	}

	public void setDatefrom(String reportdatefrom) {
		this.datefrom = reportdatefrom;
	}

	String dateto;

	public String getDateto() {
		return dateto;
	}

	public void setDateto(String reportdateto) {
		this.dateto = reportdateto;
	}

	String empid;

	public String getEmpid() {
		return empid;
	}

	public void setEmpid(String empid) {
		this.empid = empid;
	}

	public void createExcelFile(Map<String, Map<String, String>> projectdetails, List<String> nameheaderlist, List<Integer> leavesdaylist, List<Integer> holidayslist,
			HashMap<String, ArrayList<String>> finaltask, List<String> idList, Map<String, List<String>> mapconveyancetravel, Map<String, List<String>> mapconveyanceothers)
			throws IOException {
		FileOutputStream fos = null;
		try {
			writeTimeSheetReport(projectdetails, nameheaderlist, leavesdaylist, holidayslist, finaltask, idList);
			writeConveyanceSheetReport(nameheaderlist, mapconveyancetravel, mapconveyanceothers);
			String reportName = getReportName(); // get report name as per
													// client requirement.

			/*
			 * fos = new FileOutputStream(new File("/home/dailyhrz/Desktop/" +
			 * reportName + ".xls")); workbook.write(fos);
			 */

			FileOutputStream fileOut = null;
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			try {
				workbook.write(buffer);
				buffer.close();

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			response.setHeader("Content-Disposition", "attachment; filename=\"" + reportName + ".xls\"");
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());

			try {
				ServletOutputStream op = response.getOutputStream();
				op = response.getOutputStream();
				op.write(buffer.toByteArray());
				op.flush();
				op.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	// // filling all cell values from database ....
	public void fillsheet(Map<String, Map<String, String>> projectdetails, List<String> nameheaderlist, List<Integer> leavesdaylist, List<Integer> holidayslist,
			HashMap<String, ArrayList<String>> finaltask, List<String> idList, Map<String, List<String>> mapconveyancetravel, Map<String, List<String>> mapconveyanceothers)
			throws SQLException {

		Connection con = null;
		PreparedStatement pStatement = null;
		PreparedStatement preparedStatement = null;
		PreparedStatement statement = null;
		PreparedStatement statementothers = null;
		PreparedStatement preptask = null;
		
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet resultSet = null;
		ResultSet resulttask = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);

			session = request.getSession();
			CF = (CommonFunctions) session.getAttribute("CF");

			// ******** LEAVES.......

//			Map<String, Map<String, String>> hmleaves = CF.getLeaveDates(con,getDatefrom(), getDateto(), CF, null, true, null);
			Map<String, Map<String, String>> hmleaves = CF.getActualLeaveDates(con, CF, uF, getDatefrom(), getDateto(),  null, true, null);
			System.out.println("leaves dates.hmleaves." + hmleaves);
			Map<String, String> innerleaves = null;

			Iterator iterator = hmleaves.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry pairs = (Map.Entry) iterator.next();
				innerleaves = (Map<String, String>) pairs.getValue();
			}

			Iterator iterator2 = innerleaves.entrySet().iterator();
			while (iterator2.hasNext()) {
				Map.Entry pairs = (Map.Entry) iterator2.next();
				leavesdaylist.add(Integer.parseInt(pairs.getKey().toString().substring(0, 2)));
			}
			// System.out.println("leaves list" + leavesdaylist);

			// ********** HOLIDAYS.....
			Map<String, String> hmholidaylist = new HashMap<String, String>();
			Map<String, String> hmholidays = new HashMap<String, String>();
			CF.getHolidayList(con,request,getDatefrom(), getDateto(), CF, hmholidaylist, hmholidays, true);
			// System.out.println("Listing holidays" + hmholidaylist);

			Iterator iterator3 = hmholidaylist.entrySet().iterator();
			while (iterator3.hasNext()) {
				Map.Entry pairs = (Map.Entry) iterator3.next();
				String stringday = pairs.getKey().toString().substring(0, 2);
				if (holidayslist.contains(Integer.parseInt(stringday))) {

				} else {
					holidayslist.add(Integer.parseInt(stringday));
				}
			}
			// System.out.println("holidays list.. "+holidayslist);

			StringBuilder strQuery = new StringBuilder();
			String str = "select epd.emp_fname,epd.empcode,emp_bank_acct_nbr,emp_pan_no,di.dept_name from employee_personal_details epd join employee_official_details eod on(epd.emp_per_id=eod.emp_id) join department_info di on(eod.depart_id=di.dept_id) where epd.emp_per_id=?";

			pStatement = con.prepareStatement(str);
			pStatement.setInt(1, UF.parseToInt(getEmpid()));
			rs1 = pStatement.executeQuery();

			while (rs1.next()) {
				nameheaderlist.add(rs1.getString("emp_fname"));
				nameheaderlist.add(rs1.getString("empcode"));
				nameheaderlist.add(rs1.getString("emp_bank_acct_nbr"));
				nameheaderlist.add(rs1.getString("emp_pan_no"));
				nameheaderlist.add(rs1.getString("dept_name"));
			}
			rs1.close();
			pStatement.close();

			// strQuery.append("select client_name,task_date,sum(actual_hrs) as actual_hrs from(select client_name,activity_name,task_date,SUM(actual_hrs) as actual_hrs from(select clnt.client_name,pmnt.client_id ,ai.pro_id,ai.activity_name,ta.activity_id,ta.task_date,ta.actual_hrs from task_activity ta  join activity_info ai on ta.activity_id=ai.task_id  join projectmntnc pmnt on ai.pro_id=pmnt.pro_id join client_details clnt on clnt.client_id=pmnt.client_id where ta.emp_id='1207') as a   group by client_name,activity_name,task_date order by client_name)as c group by client_name,task_date order by client_name");
			// System.out.println(strQuery.toString());
			
			preparedStatement = con.prepareStatement("select client_name,task_date,sum(actual_hrs) as actual_hrs from(select client_name,activity_name,task_date,SUM(actual_hrs) as actual_hrs from(select clnt.client_name,pmnt.client_id ,ai.pro_id,ai.activity_name,ta.activity_id,ta.task_date,ta.actual_hrs from task_activity ta  join activity_info ai on ta.activity_id=ai.task_id  join projectmntnc pmnt on ai.pro_id=pmnt.pro_id join client_details clnt on clnt.client_id=pmnt.client_id where ta.emp_id=?) as a   group by client_name,activity_name,task_date order by client_name)as c where task_date>=? and task_date<=? group by client_name,task_date order by client_name");
			preparedStatement.setInt(1, UF.parseToInt(getEmpid()));
			preparedStatement.setDate(2, UF.getDateFormat(getDatefrom(), DATE_FORMAT));
			preparedStatement.setDate(3, UF.getDateFormat(getDateto(), DATE_FORMAT));
			rs = preparedStatement.executeQuery();
			while (rs.next()) {

				if (idList.contains(rs.getString("client_name"))) {

					String clientname = rs.getString("client_name");
					Map<String, String> innerpro = projectdetails.get(clientname);
					innerpro.put(rs.getString("task_date"), rs.getString("actual_hrs"));
					projectdetails.put(clientname, innerpro);

				} else {

					Map<String, String> innerpro = new HashMap<String, String>();
					innerpro.put(rs.getString("task_date"), rs.getString("actual_hrs"));
					String clientname = rs.getString("client_name");
					projectdetails.put(clientname, innerpro);
					idList.add(rs.getString("client_name"));
					// System.out.println(clientname);

				}

			}
			rs.close();
			preparedStatement.close();
			
			int no_records = 0;

			// conveyance sheet statement
//			PreparedStatement statement = con
//					.prepareStatement("select is_billable,reimbursement_amount,travel_mode,no_person,travel_from,travel_to,no_days,travel_distance,travel_rate ,reimbursement_info,client_name  from (select * from emp_reimbursement  ereim where  approval_1=1 and approval_2=1 and ispaid=false  and entry_date>=? and entry_date<=? and reimbursement_info='Travel'  and ereim.emp_id=?) as a join  projectmntnc pmnt on cast(pmnt.pro_id as text)=a.reimbursement_type join client_details using(client_id) ");
			 
			
			statement = con.prepareStatement("select reimbursement_id,is_billable,reimbursement_amount,travel_mode,no_person,travel_from, travel_to,no_days,travel_distance,travel_rate ,reimbursement_info,client_name from (select * from emp_reimbursement  ereim where  approval_1=1 and approval_2=1 and from_date=? and to_date=? and (reimbursement_info='Travel' OR reimbursement_info='Conveyance Bill' ) and reimbursement_type1='P' and ereim.emp_id=?) as a join  projectmntnc pmnt on  cast(pmnt.pro_id as text)=a.reimbursement_type join client_details using(client_id) ");
			statement.setDate(1, UF.getDateFormat(getDatefrom(), DATE_FORMAT));
			statement.setDate(2, UF.getDateFormat(getDateto(), DATE_FORMAT));
			statement.setInt(3, UF.parseToInt(getEmpid()));

			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				no_records++;
				List<String> conveyancelist = new ArrayList<String>();
				conveyancelist.add(resultSet.getString("travel_mode"));
				conveyancelist.add(resultSet.getString("no_person"));
				if (resultSet.getString("is_billable").equals("t"))
					conveyancelist.add("YES");
				else {
					conveyancelist.add("NO");
				}
				conveyancelist.add(resultSet.getString("travel_from"));
				conveyancelist.add(resultSet.getString("travel_to"));
				conveyancelist.add(resultSet.getString("no_days"));
				conveyancelist.add(resultSet.getString("travel_distance"));
				conveyancelist.add(resultSet.getString("travel_rate"));
				conveyancelist.add(resultSet.getString("reimbursement_amount"));

				mapconveyancetravel.put(no_records + resultSet.getString("client_name"), conveyancelist);

				System.out.println("printing travel" + mapconveyancetravel);
			}
			resultSet.close();
			statement.close();

			// making conveyance others"""""
//			PreparedStatement statementothers = con
//					.prepareStatement("select is_billable,reimbursement_amount,travel_mode,no_person,travel_from,travel_to,no_days,travel_distance,travel_rate ,reimbursement_info from emp_reimbursement where approval_1=1 and approval_2=1 and (reimbursement_type1!='P' or reimbursement_info!='Travel') and entry_date>=? and entry_date<=? and emp_id=? ");
			
			statementothers = con.prepareStatement("select reimbursement_id, is_billable,reimbursement_amount,travel_mode,no_person,travel_from, travel_to,no_days,travel_distance,travel_rate ,reimbursement_info from emp_reimbursement where approval_1=1 and approval_2=1 and reimbursement_type1!='P' and reimbursement_info!='Travel' and reimbursement_info!='Conveyance Bill' and from_date=? and to_date=? and emp_id=?");
			statementothers.setDate(1, UF.getDateFormat(getDatefrom(), DATE_FORMAT));
			statementothers.setDate(2, UF.getDateFormat(getDateto(), DATE_FORMAT));
			statementothers.setInt(3, UF.parseToInt(getEmpid()));

			resultSet = statementothers.executeQuery();
			while (resultSet.next()) {

				List<String> conveyancelist = new ArrayList<String>();
				conveyancelist.add(resultSet.getString("travel_mode"));
				conveyancelist.add(resultSet.getString("no_person"));
				conveyancelist.add("NO");
				conveyancelist.add(resultSet.getString("travel_from"));
				conveyancelist.add(resultSet.getString("travel_to"));
				conveyancelist.add(resultSet.getString("no_days"));
				conveyancelist.add(resultSet.getString("travel_distance"));
				conveyancelist.add(resultSet.getString("travel_rate"));
				conveyancelist.add(resultSet.getString("reimbursement_amount"));

				if (resultSet.getString("reimbursement_info").equalsIgnoreCase("Travel") || resultSet.getString("reimbursement_info").equalsIgnoreCase("Conveyance Bill")) {
					mapconveyancetravel.put(no_records + resultSet.getString("reimbursement_info"), conveyancelist);
				} else {
					mapconveyanceothers.put(no_records + resultSet.getString("reimbursement_info"), conveyancelist);
				}
			}
			resultSet.close();
			statementothers.close();

			if (projectdetails != null) {
				List checklist = new ArrayList<String>();
				String strquerytask = "select client_name,client_id,pro_id,activity_name from activity_info ai join projectmntnc using(pro_id) join client_details using(client_id) where ai.emp_id=? order by client_name";
				preptask = con.prepareStatement(strquerytask);
				preptask.setInt(1, UF.parseToInt(getEmpid()));
				resulttask = preptask.executeQuery();
				while (resulttask.next()) {
					// if(idList.get(0)==resulttask.getString("client_name"));
					if (checklist.contains(resulttask.getString("client_name"))) {
						List tasklist = finaltask.get(resulttask.getString("client_name"));
						tasklist.add(resulttask.getString("activity_name"));
						finaltask.put(resulttask.getString("client_name"), (ArrayList<String>) tasklist);
					} else {
						List tasklist = new ArrayList<String>();
						tasklist.add(resulttask.getString("activity_name"));
						checklist.add(resulttask.getString("client_name"));
						finaltask.put(resulttask.getString("client_name"), (ArrayList<String>) tasklist);
					}
				}
				resulttask.close();
				preptask.close();
				// System.out.println(finaltask);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeResultSet(rs1);
			db.closeResultSet(resulttask);
			db.closeResultSet(resultSet);
			db.closeStatements(pStatement);
			db.closeStatements(statementothers);
			db.closeStatements(statement);
			db.closeStatements(preparedStatement);
			db.closeStatements(preptask);
			db.closeConnection(con);

		}

	}

	public void writeTimeSheetReport(Map<String, Map<String, String>> projectdetails, List<String> nameheaderlist, List<Integer> leavesdaylist, List<Integer> holidayslist,
			HashMap<String, ArrayList<String>> finaltask, List<String> idList) {
		try {
			Row firmNameRow = monthly_time_sheet.createRow(5);
			firmNameRow.setHeight((short) 450);
			monthly_time_sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
			for (int i = 0; i < 37; i++) {
				HSSFCellStyle styleForFirmName = workbook.createCellStyle();
				Font firmNameFont = workbook.createFont();
				firmNameFont.setFontHeight((short) 320);
				firmNameFont.setBoldweight((short) 1000);
				styleForFirmName.setFont(firmNameFont);
				styleForFirmName.setBorderTop(CellStyle.BORDER_THIN);
				Cell firmNameCell = firmNameRow.createCell(i);
				if (i == 0) {
					styleForFirmName.setBorderLeft(CellStyle.BORDER_THIN);
					firmNameCell.setCellValue(" KIRTANE AND PANDIT");
				}
				if (i == 36) {
					styleForFirmName.setBorderRight(CellStyle.BORDER_THIN);
				}
				firmNameCell.setCellStyle(styleForFirmName);
			}

			Row firmTypeRow = monthly_time_sheet.createRow(6);
			monthly_time_sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 10));
			for (int i = 0; i < 37; i++) {
				HSSFCellStyle styleForFirmType = workbook.createCellStyle();
				Font firmTypeFont = workbook.createFont();
				firmTypeFont.setFontHeight((short) 225);
				firmTypeFont.setBoldweight((short) 1000);
				styleForFirmType.setFont(firmTypeFont);
				styleForFirmType.setBorderBottom(CellStyle.BORDER_THIN);
				Cell firmTypeCell = firmTypeRow.createCell(i);
				if (i == 0) {
					styleForFirmType.setBorderLeft(CellStyle.BORDER_THIN);
					firmTypeCell.setCellValue(" CHARTERED ACCOUNTANTS");
				}
				if (i == 36) {
					styleForFirmType.setBorderRight(CellStyle.BORDER_THIN);
				}
				firmTypeCell.setCellStyle(styleForFirmType);
			}

			Row firstBlankRow = monthly_time_sheet.createRow(7);
			forBlankRowLeftAndRightBorder(firstBlankRow);

			Row clientNameRow = monthly_time_sheet.createRow(8);
			HSSFCellStyle rightBorderForClientSummary = workbook.createCellStyle();
			rightBorderForClientSummary.setBorderRight(CellStyle.BORDER_THIN);
			HSSFCellStyle clientSummary = workbook.createCellStyle();

			Font clientNameCellFont = workbook.createFont();
			clientNameCellFont.setFontHeight((short) 230);

			Cell clientNameCell = clientNameRow.createCell(0);
			clientNameCell.setCellValue(" Name : " + nameheaderlist.get(0));
			clientSummary.setFont(clientNameCellFont);
			clientSummary.setBorderLeft(CellStyle.BORDER_THIN);
			clientNameCell.setCellStyle(clientSummary);

			clientSummary.setFont(clientNameCellFont);
			clientSummary.setBorderLeft(CellStyle.BORDER_THIN);

			Cell clientBankACNoCell = clientNameRow.createCell(30);
			clientBankACNoCell.setCellValue(" Bank A/C No.: " + nameheaderlist.get(2));

			Cell lastCellOfClientSummary1 = clientNameRow.createCell(36);
			lastCellOfClientSummary1.setCellStyle(rightBorderForClientSummary);

			Row divisionRow = monthly_time_sheet.createRow(9);
			divisionRow.setHeight((short) 300);
			Cell clientDivisionCell = divisionRow.createCell(0);
			clientDivisionCell.setCellValue(" Department : " + nameheaderlist.get(4));
			clientDivisionCell.setCellStyle(clientSummary);

			HSSFCellStyle cellStyleforReportName = workbook.createCellStyle();
			Font reportNameFont = workbook.createFont();
			reportNameFont.setFontHeight((short) 280);
			reportNameFont.setBoldweight((short) 1000);
			cellStyleforReportName.setFont(reportNameFont);

			Cell reportNameCell = divisionRow.createCell(14);
			reportNameCell.setCellValue("MONTHLY TIME REPORT");
			reportNameCell.setCellStyle(cellStyleforReportName);

			Cell clientPANNoCell = divisionRow.createCell(30);
			clientPANNoCell.setCellValue(" PAN : " + nameheaderlist.get(3));

			Cell lastCellOfClientSummary2 = divisionRow.createCell(36);
			lastCellOfClientSummary2.setCellStyle(rightBorderForClientSummary);

			Row empCodeRow = monthly_time_sheet.createRow(10);
			Cell clientEmpIdCell = empCodeRow.createCell(0);
			clientEmpIdCell.setCellValue(" Employee Code : " + nameheaderlist.get(1));
			clientEmpIdCell.setCellStyle(clientSummary);

			Cell clientWorkingHrsCell = empCodeRow.createCell(30);
			clientWorkingHrsCell.setCellValue(" ( Standard Hours per Day  :  8 ) : ");

			Cell lastCellOfClientSummary3 = empCodeRow.createCell(36);
			lastCellOfClientSummary3.setCellStyle(rightBorderForClientSummary);

			Row secondBlankRow = monthly_time_sheet.createRow(11);
			forBlankRowLeftAndRightBorder(secondBlankRow);

			Row headingRowDesc = monthly_time_sheet.createRow(12);
			headingRowDesc.setHeight((short) 500);
			HSSFCellStyle headingStyle = workbook.createCellStyle();
			headingStyle.setBorderLeft(CellStyle.BORDER_THIN);
			headingStyle.setBorderRight(CellStyle.BORDER_THIN);
			headingStyle.setBorderTop(CellStyle.BORDER_THIN);
			headingStyle.setAlignment(CellStyle.ALIGN_CENTER);
			headingStyle.setWrapText(true);
			headingStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
			Font headingFont = workbook.createFont();
			headingFont.setBoldweight((short) 1000);
			headingStyle.setFont(headingFont);

			monthly_time_sheet.setColumnWidth(0, 1000);
			Cell srNoCell = headingRowDesc.createCell(0);
			srNoCell.setCellValue(" Sr. ");
			srNoCell.setCellStyle(headingStyle);

			Cell nameOfClient = headingRowDesc.createCell(1);
			nameOfClient.setCellValue("  Name of Client  ");

			monthly_time_sheet.autoSizeColumn((short) 1);
			nameOfClient.setCellStyle(headingStyle);
			monthly_time_sheet.addMergedRegion(new CellRangeAddress(12, 12, 1, 2));

			Cell assignment = headingRowDesc.createCell(3);
			assignment.setCellValue("  Assignment  ");
			monthly_time_sheet.autoSizeColumn((short) 3);
			assignment.setCellStyle(headingStyle);

			Cell totalDays = headingRowDesc.createCell(4);
			totalDays.setCellValue("Total Days");
			totalDays.setCellStyle(headingStyle);

			Cell conveyExp = headingRowDesc.createCell(5);
			conveyExp.setCellValue("Conv. Exp.");
			conveyExp.setCellStyle(headingStyle);

			for (int i = 6; i < 37; i++) {
				Cell cell = headingRowDesc.createCell(i);
				HSSFCellStyle cellStyleHeadingMonth = workbook.createCellStyle();
				Font font = workbook.createFont();
				font.setBoldweight((short) 1000);
				cellStyleHeadingMonth.setBorderTop(CellStyle.BORDER_THIN);
				cellStyleHeadingMonth.setFont(font);
				if (i > 4) {
					cellStyleHeadingMonth.setBorderBottom(CellStyle.BORDER_THIN);
				}
				if (i == 5 || i == 31) {
					cellStyleHeadingMonth.setBorderLeft(CellStyle.BORDER_THIN);
				}
				if (i == 16 || i == 33) {
					cell.setCellValue("Month :");
					cell.setCellStyle(cellStyleHeadingMonth);
				}
				if (i == 36) {
					cellStyleHeadingMonth.setBorderRight(CellStyle.BORDER_THIN);
				}
				cell.setCellStyle(cellStyleHeadingMonth);
			}

			Row headingRowNums = monthly_time_sheet.createRow(13);
			headingRowNums.setHeight((short) 700);

			for (int i = 0; i < 6; i++) {
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
				cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
				if (i == 2) {
					cellStyle.setBorderLeft(CellStyle.BORDER_NONE);
				}
				Cell cell = headingRowNums.createCell(i);
				cell.setCellStyle(cellStyle);
			}

			for (int i = 6, j = 1; i < 37; i++, j++) {
				Cell cell = headingRowNums.createCell(i);
				HSSFCellStyle cellStyleHeadingMonth = workbook.createCellStyle();
				cellStyleHeadingMonth.setBorderTop(CellStyle.BORDER_THIN);
				cellStyleHeadingMonth.setBorderBottom(CellStyle.BORDER_THIN);
				cellStyleHeadingMonth.setBorderLeft(CellStyle.BORDER_THIN);
				cellStyleHeadingMonth.setBorderRight(CellStyle.BORDER_THIN);
				cellStyleHeadingMonth.setAlignment(CellStyle.ALIGN_CENTER);
				cellStyleHeadingMonth.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
				cell.setCellValue(j);
				monthly_time_sheet.setColumnWidth(i, 1200);
				cell.setCellStyle(cellStyleHeadingMonth);
			}

			Set<String> clientnameset = finaltask.keySet();
			int linecount = 14 + (projectdetails.keySet().size() * 2);
			Iterator<String> iter = clientnameset.iterator();
			int procount = 0;
			for (int i = 14, k = 1; i < linecount; i++, k++) {
				Map<String, String> mapdate = new HashMap<String, String>();
				int flag = 0;
				Row row = monthly_time_sheet.createRow(i);
				String prname = null;
				Map finalmap = new HashMap();
				for (int j = 0; j < 37; j++) {
					HSSFCellStyle cellStyle = workbook.createCellStyle();
					cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
					cellStyle.setBorderTop(CellStyle.BORDER_THIN);
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cellStyle.setBorderRight(CellStyle.BORDER_THIN);

					Cell cell = row.createCell(j);

					if (i % 2 == 0 && j == 0) {
						cell.setCellValue(k);
						cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
						k = k - 1;
					}
					if (i % 2 != 0 && j == 0) {
						cellStyle.setBorderTop(CellStyle.BORDER_NONE);
					}
					if (i % 2 == 0 && j == 2) {
						cell.setCellValue("Onsite");
						cell.setCellStyle(cellStyle);
					}
					if (i % 2 != 0 && j == 2) {
						cell.setCellValue("Office");
						cell.setCellStyle(cellStyle);
					}

					if (i % 2 != 0 && j == 1) {
						cellStyle.setBorderTop(CellStyle.BORDER_NONE);
					}
					if (i % 2 == 0 && j == 1) {
						cell.setCellValue(idList.get(k));
						cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
						mapdate = projectdetails.get(idList.get(k));
						flag = 1;
					}
					if (i % 2 == 0 && j == 3) {
						String cellval = null;
						List celllist = finaltask.get(idList.get(k));
						for (int p = 0; p < celllist.size(); p++) {
							if (p == 0) {
								cellval = (String) celllist.get(p);
							} else {
								cellval += "," + celllist.get(p);
							}

						}
						cell.setCellValue(cellval);

					}
					if (i % 2 == 0 && j == 4) {

						cell.setCellValue(mapdate.keySet().size());

					}
					if (i % 2 != 0 && j > 5) {
						cellStyle.setBorderTop(CellStyle.BORDER_NONE);
					}
					int totaldays = 0;

					if (i % 2 == 0 && j > 5) {
						if (flag == 1) {
							Iterator iterator = mapdate.entrySet().iterator();
							java.util.Date date1 = null;

							Calendar ca1 = Calendar.getInstance();
							while (iterator.hasNext()) {
								Map.Entry mapEntry = (Map.Entry) iterator.next();
								String day = (String) mapEntry.getKey();
								SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
								date1 = format.parse(day);
								ca1.setTime(date1);
								int intdate = ca1.get(Calendar.DATE);
								finalmap.put(intdate, mapEntry.getValue().toString());
								System.out.println("printing finalmap" + finalmap);

							}
							j = j - 1;
						} else {
							// System.out.println(finalmap.keySet());

							if (finalmap.keySet().contains(j - 5)) {
								cell.setCellValue((String) finalmap.get(j - 5));
								totaldays++;
							} else {

								cell.setCellValue("");

							}
						}
						flag = 0;
					}

					cell.setCellStyle(cellStyle);

				}
			}

			Row totalHrsSpendWithClientsRow = monthly_time_sheet.createRow(linecount);
			for (int i = 0; i < 37; i++) {
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setBorderTop(CellStyle.BORDER_THIN);
				cellStyle.setBorderBottom(CellStyle.BORDER_DOUBLE);
				if (i > 5) {
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cellStyle.setBorderRight(CellStyle.BORDER_THIN);
				}
				Cell cell = totalHrsSpendWithClientsRow.createCell(i);
				if (i == 0) {
					Font font = workbook.createFont();
					font.setBoldweight((short) 1000);
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cell.setCellValue(" 1.  Total  Hours spent for client ");
					cellStyle.setFont(font);
					cell.setCellStyle(cellStyle);

				}
				cell.setCellStyle(cellStyle);
			}
			linecount++;
			List<String> activities = getActivitiesList();
			for (int i = linecount, k = 1, m = 0; i < linecount + 6; i++, k++, m++) {
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
				cellStyle.setBorderTop(CellStyle.BORDER_THIN);
				cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
				cellStyle.setBorderRight(CellStyle.BORDER_THIN);

				Row row = monthly_time_sheet.createRow(i);
				for (int j = 0; j < 37; j++) {
					Cell cell = row.createCell(j);
					cell.setCellStyle(cellStyle);
					if (j == 0) {
						cell.setCellValue(k);
					}
					if (j == 1) {
						cell.setCellValue(activities.get(m));
					}
					if (k == 5 && j == 4) {
						cell.setCellValue(leavesdaylist.size());
					}
					if (k == 5 && j > 5) {
						if (leavesdaylist.contains((j - 5))) {
							cell.setCellValue("L");

						} else {
							cell.setCellValue("");

						}
					}
					if (k == 6 && j == 4) {
						cell.setCellValue(holidayslist.size());
					}
					if (k == 6 && j > 5) {
						if (holidayslist.contains((j - 5))) {
							cell.setCellValue("H");
						}
					}
				}
			}

			linecount = linecount + 6;
			Row totalHrsSpendWithOtherActivityRow = monthly_time_sheet.createRow(linecount);
			for (int i = 0; i < 37; i++) {
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setBorderTop(CellStyle.BORDER_THIN);
				cellStyle.setBorderBottom(CellStyle.BORDER_DOUBLE);
				if (i > 3) {
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cellStyle.setBorderRight(CellStyle.BORDER_THIN);
				}
				Cell cell = totalHrsSpendWithOtherActivityRow.createCell(i);
				if (i == 0) {
					Font font = workbook.createFont();
					font.setBoldweight((short) 1000);
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cell.setCellValue(" 2.  Total Hours spent on other activities");
					cellStyle.setFont(font);
					cell.setCellStyle(cellStyle);
				}
				if (i == 4) {
					cellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
					cell.setCellValue("0");
					cell.setCellStyle(cellStyle);
				}
				cell.setCellStyle(cellStyle);
			}
			linecount++;
			Row thirdBlankRow = monthly_time_sheet.createRow(linecount);
			forBlankRowLeftAndRightBorder(thirdBlankRow);
			linecount++;
			Row grandTotRow = monthly_time_sheet.createRow(linecount);
			for (int i = 0; i < 37; i++) {
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setBorderTop(CellStyle.BORDER_THIN);
				cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
				if (i > 3) {
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cellStyle.setBorderRight(CellStyle.BORDER_THIN);
				}
				Cell cell = grandTotRow.createCell(i);
				if (i == 0) {
					Font font = workbook.createFont();
					font.setBoldweight((short) 1000);
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cell.setCellValue(" 3.  Grand Total  ( 1 + 2)");
					cellStyle.setFont(font);
					cell.setCellStyle(cellStyle);
				}
				cell.setCellStyle(cellStyle);
			}
			linecount++;
			Row extraWorkingDaysRow = monthly_time_sheet.createRow(linecount);
			extraWorkingDaysRow.setHeight((short) 750);
			for (int i = 0; i < 37; i++) {
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setBorderTop(CellStyle.BORDER_THIN);
				cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
				cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
				if (i > 3) {
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cellStyle.setBorderRight(CellStyle.BORDER_THIN);
				}
				Cell cell = extraWorkingDaysRow.createCell(i);
				if (i == 0) {
					Font font = workbook.createFont();
					font.setBoldweight((short) 1000);
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cell.setCellValue(" 4.    Extra Working days");
					cellStyle.setFont(font);
					cell.setCellStyle(cellStyle);
				}
				cell.setCellStyle(cellStyle);
			}
			linecount++;
			Row fourthBlankRow = monthly_time_sheet.createRow(linecount);
			forBlankRowLeftAndRightBorder(fourthBlankRow);
			linecount++;
			Row noteRow = monthly_time_sheet.createRow(linecount);
			HSSFCellStyle leftBorderOnly = workbook.createCellStyle();
			leftBorderOnly.setBorderLeft(CellStyle.BORDER_THIN);
			Cell noteCell = noteRow.createCell(0);
			noteCell.setCellValue("Note : Time report must be submitted on or before 26th Day of Every Month.");
			noteCell.setCellStyle(leftBorderOnly);

			Cell lastCellOfNote = noteRow.createCell(36);
			lastCellOfNote.setCellStyle(rightBorderForClientSummary);
			linecount++;
			Row fifthBlankRow = monthly_time_sheet.createRow(linecount);
			forBlankRowLeftAndRightBorder(fifthBlankRow);
			linecount++;
			Row approvalRow = monthly_time_sheet.createRow(linecount);

			Cell firstCellOfApprovalRow = approvalRow.createCell(0);
			firstCellOfApprovalRow.setCellStyle(leftBorderOnly);

			Cell preparedByCell = approvalRow.createCell(11);
			preparedByCell.setCellValue("Prepared By :");

			Cell checkedByCell = approvalRow.createCell(20);
			checkedByCell.setCellValue("Checked By :");

			Cell approvedByCell = approvalRow.createCell(29);
			approvedByCell.setCellValue("Approved by :");

			Cell lastCellOfApproval = approvalRow.createCell(36);
			lastCellOfApproval.setCellStyle(rightBorderForClientSummary);
			linecount++;
			Row lastRow = monthly_time_sheet.createRow(linecount);
			for (int i = 0; i < 37; i++) {
				HSSFCellStyle cellStyleForLastRow = workbook.createCellStyle();
				cellStyleForLastRow.setBorderBottom(CellStyle.BORDER_THIN);
				if (i == 0) {
					cellStyleForLastRow.setBorderLeft(CellStyle.BORDER_THIN);
				}
				if (i == 36) {
					cellStyleForLastRow.setBorderRight(CellStyle.BORDER_THIN);
				}
				Cell cell = lastRow.createCell(i);
				cell.setCellStyle(cellStyleForLastRow);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeConveyanceSheetReport(List<String> nameheaderlist, Map<String, List<String>> mapconveyancetravel, Map<String, List<String>> mapconveyanceothers) {
		Iterator iter = mapconveyanceothers.entrySet().iterator();
		Iterator iterator = mapconveyancetravel.entrySet().iterator();
		List<String> listconven = new ArrayList<String>();
		List<String> listothers = new ArrayList<String>();
		while (iterator.hasNext()) {
			Entry entry = (Entry) iterator.next();
			listconven.add((String) entry.getKey());
		}

		while (iter.hasNext()) {
			Map.Entry pairs = (Map.Entry) iter.next();
			listothers.add((String) pairs.getKey());
		}
		try {
			conveyance_sheet.setColumnWidth(0, 1500);
			conveyance_sheet.setColumnWidth(1, 7500);
			conveyance_sheet.setColumnWidth(2, 4000);
			conveyance_sheet.setColumnWidth(3, 3000);
			conveyance_sheet.setColumnWidth(4, 3000);
			conveyance_sheet.setColumnWidth(5, 3000);
			conveyance_sheet.setColumnWidth(6, 3000);
			conveyance_sheet.setColumnWidth(7, 2000);
			conveyance_sheet.setColumnWidth(8, 3000);
			conveyance_sheet.setColumnWidth(9, 3000);
			conveyance_sheet.setColumnWidth(10, 3000);

			Row firmNameRow = conveyance_sheet.createRow(0);
			firmNameRow.setHeight((short) 450);
			conveyance_sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
			for (int i = 0; i < 11; i++) {
				HSSFCellStyle styleForFirmName = workbook.createCellStyle();
				Font firmNameFont = workbook.createFont();
				firmNameFont.setFontHeight((short) 320);
				firmNameFont.setBoldweight((short) 1000);
				styleForFirmName.setFont(firmNameFont);
				styleForFirmName.setBorderTop(CellStyle.BORDER_THIN);
				Cell firmNameCell = firmNameRow.createCell(i);
				if (i == 0) {
					styleForFirmName.setBorderLeft(CellStyle.BORDER_THIN);
					firmNameCell.setCellValue(" KIRTANE AND PANDIT");
				}
				if (i == 10) {
					styleForFirmName.setBorderRight(CellStyle.BORDER_THIN);
				}
				firmNameCell.setCellStyle(styleForFirmName);
			}

			Row firmTypeRow = conveyance_sheet.createRow(1);
			conveyance_sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 10));
			for (int i = 0; i < 11; i++) {
				HSSFCellStyle styleForFirmType = workbook.createCellStyle();
				Font firmTypeFont = workbook.createFont();
				firmTypeFont.setFontHeight((short) 225);
				firmTypeFont.setBoldweight((short) 1000);
				styleForFirmType.setFont(firmTypeFont);
				styleForFirmType.setBorderBottom(CellStyle.BORDER_THIN);
				Cell firmTypeCell = firmTypeRow.createCell(i);
				if (i == 0) {
					styleForFirmType.setBorderLeft(CellStyle.BORDER_THIN);
					firmTypeCell.setCellValue(" CHARTERED ACCOUNTANTS");
				}
				if (i == 10) {
					styleForFirmType.setBorderRight(CellStyle.BORDER_THIN);
				}
				firmTypeCell.setCellStyle(styleForFirmType);
			}

			Row reportNameRow = conveyance_sheet.createRow(3);
			applyLeftAndRightBorderTable(reportNameRow);
			reportNameRow.setHeight((short) 300);

			HSSFCellStyle cellStyleforReportName = workbook.createCellStyle();
			Font reportNameFont = workbook.createFont();
			reportNameFont.setFontHeight((short) 280);
			reportNameFont.setBoldweight((short) 1000);
			cellStyleforReportName.setFont(reportNameFont);

			Cell reportNameCell = reportNameRow.createCell(3);
			reportNameCell.setCellValue("MONTHLY TIME REPORT (Expense Statement)");
			reportNameCell.setCellStyle(cellStyleforReportName);

			Row blankRow = conveyance_sheet.createRow(2);
			applyLeftAndRightBorderTable(blankRow);

			Row clientNameRow = conveyance_sheet.createRow(4);
			applyLeftAndRightBorderTable(clientNameRow);
			HSSFCellStyle clientSummary = workbook.createCellStyle();
			Font clientNameCellFont = workbook.createFont();
			clientNameCellFont.setFontHeight((short) 230);

			Cell clientNameCell = clientNameRow.createCell(0);
			clientNameCell.setCellValue(" Name : " + nameheaderlist.get(0));
			clientSummary.setFont(clientNameCellFont);
			clientSummary.setBorderLeft(CellStyle.BORDER_THIN);
			clientNameCell.setCellStyle(clientSummary);
			Cell currentDate = clientNameRow.createCell(8);
			currentDate.setCellValue(" Date : " + UF.getCurrentDate(CF.getStrTimeZone()));
			// currentDate.setCellValue(" Date : " + getDateto());

			Row divisionRow = conveyance_sheet.createRow(5);
			applyLeftAndRightBorderTable(divisionRow);
			Cell clientDivisionCell = divisionRow.createCell(0);
			clientDivisionCell.setCellValue(" Department : " + nameheaderlist.get(4));
			clientDivisionCell.setCellStyle(clientSummary);

			Row empCodeRow = conveyance_sheet.createRow(6);
			applyLeftAndRightBorderTable(empCodeRow);
			Cell clientEmpIdCell = empCodeRow.createCell(0);
			clientEmpIdCell.setCellValue(" Employee Code : " + nameheaderlist.get(1));
			clientEmpIdCell.setCellStyle(clientSummary);

			Row partisionRow = conveyance_sheet.createRow(7);
			forBlankRowsWithBorder(partisionRow);

			Row blankRow3 = conveyance_sheet.createRow(8);
			applyLeftAndRightBorderTable(blankRow3);

			Row statementNameRow = conveyance_sheet.createRow(9);
			applyLeftAndRightBorderTable(statementNameRow);

			HSSFCellStyle headingNameEffect = workbook.createCellStyle();
			Font headingName = workbook.createFont();
			headingName.setBoldweight((short) 1000);
			headingName.setFontHeight((short) 220);
			headingNameEffect.setFont(headingName);
			headingNameEffect.setBorderLeft(CellStyle.BORDER_THIN);
			Cell statementNameCell = statementNameRow.createCell(0);
			statementNameCell.setCellValue(" Conveyance and Other Expenses Statement for the month ending on " + getDateto());
			statementNameCell.setCellStyle(headingNameEffect);

			Row blankRow4 = conveyance_sheet.createRow(10);
			for (int i = 0; i < 11; i++) {
				HSSFCellStyle styleForPlaceHead = workbook.createCellStyle();
				if (i == 0) {
					styleForPlaceHead.setBorderLeft(CellStyle.BORDER_THIN);
				} else if (i == 6) {
					styleForPlaceHead.setBorderBottom(CellStyle.BORDER_THIN);
				} else if (i == 10) {
					styleForPlaceHead.setBorderRight(CellStyle.BORDER_THIN);
				} else {
					continue;
				}
				Cell cell = blankRow4.createCell(i);
				cell.setCellStyle(styleForPlaceHead);
			}

			Row statementHeadingRow = conveyance_sheet.createRow(11);
			statementHeadingRow.setHeight((short) 750);
			conveyance_sheet.addMergedRegion(new CellRangeAddress(11, 11, 5, 6));

			HSSFCellStyle headingEffect = workbook.createCellStyle();
			Font headings = workbook.createFont();
			headings.setBoldweight((short) 1000);
			headings.setFontHeight((short) 200);
			headingEffect.setFont(headings);
			headingEffect.setBorderTop(CellStyle.BORDER_THIN);
			headingEffect.setBorderLeft(CellStyle.BORDER_THIN);
			headingEffect.setBorderRight(CellStyle.BORDER_THIN);
			headingEffect.setAlignment(CellStyle.ALIGN_CENTER);
			headingEffect.setVerticalAlignment(CellStyle.VERTICAL_TOP);
			headingEffect.setWrapText(true);

			headingNameEffect.setFont(headings);

			List<String> heading = getHeading();
			for (int i = 0, j = 0; j < heading.size(); i++, j++) {
				if (i == 6) {
					i = i + 1;
				}
				Cell cell = statementHeadingRow.createCell(i);
				// conveyance_sheet.autoSizeColumn((short)200);
				cell.setCellValue(heading.get(j));
				cell.setCellStyle(headingEffect);
			}

			Row fromToForPlace = conveyance_sheet.createRow(12);
			for (int i = 0; i < 11; i++) {
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
				cellStyle.setBorderRight(CellStyle.BORDER_THIN);
				cellStyle.setBorderTop(CellStyle.BORDER_NONE);

				/*
				 * if(i== 5 || i==6){ continue; }
				 */
				Cell cell = fromToForPlace.createCell(i);
				/*
				 * if(i==2 || i==4){
				 * //conveyance_sheet.autoSizeColumn((int)i,true); }
				 */
				cell.setCellStyle(cellStyle);
			}

			int k = 0, linecountothers, linecountconveyance, o = 0, linetotal = 0, amountcon = 0, amountothr = 0;
			Cell fromCell = fromToForPlace.createCell(5);
			fromCell.setCellValue("From");
			fromCell.setCellStyle(headingEffect);

			Cell toCell = fromToForPlace.createCell(6);
			toCell.setCellValue("To");
			toCell.setCellStyle(headingEffect);
			linecountconveyance = mapconveyancetravel.keySet().size();
			linecountothers = mapconveyanceothers.keySet().size();

			linetotal = 32 + linecountconveyance + linecountothers;
			System.out.println(linetotal);
			for (int i = 13; i < linetotal; i++) {
				HSSFCellStyle designForParticular = workbook.createCellStyle();
				designForParticular.setBorderLeft(CellStyle.BORDER_THIN);
				designForParticular.setBorderRight(CellStyle.BORDER_THIN);
				Row row = conveyance_sheet.createRow(i);
				List<String> innervallist = null;
				if (i == 13) {
					designForParticular.setBorderTop(CellStyle.BORDER_THIN);
				}
				for (int j = 0; j < 11; j++) {
					Cell cell = row.createCell(j);
					cell.setCellStyle(designForParticular);

					if (i == 13 && j == 0) {
						HSSFCellStyle srNosA = workbook.createCellStyle();
						Font srNoFont = workbook.createFont();
						srNoFont.setBoldweight((short) 1000);
						srNosA.setAlignment(CellStyle.ALIGN_CENTER);
						srNosA.setBorderTop(CellStyle.BORDER_THIN);
						srNosA.setBorderLeft(CellStyle.BORDER_THIN);
						srNosA.setFont(srNoFont);
						cell.setCellValue("A");
						cell.setCellStyle(srNosA);
					}
					if (i == 13 && j == 1) {
						HSSFCellStyle conveyance = workbook.createCellStyle();
						Font fontForConveyance = workbook.createFont();
						fontForConveyance.setBoldweight((short) 1000);
						fontForConveyance.setUnderline((byte) 1);
						conveyance.setBorderTop(CellStyle.BORDER_THIN);
						conveyance.setBorderLeft(CellStyle.BORDER_THIN);
						conveyance.setFont(fontForConveyance);
						cell.setCellStyle(conveyance);
						cell.setCellValue("Conveyance:-");
						// conveyance_sheet.autoSizeColumn((short)j);
					}
					if (i >= 15 && i < 15 + linecountconveyance && j < 11) {

						if (j == 0) {
							k++;
							HSSFCellStyle srNosA = workbook.createCellStyle();
							Font srNoFont = workbook.createFont();
							srNoFont.setBoldweight((short) 1000);
							srNosA.setAlignment(CellStyle.ALIGN_CENTER);
							srNosA.setBorderLeft(CellStyle.BORDER_THIN);
							srNosA.setFont(srNoFont);
							cell.setCellValue(k);
							cell.setCellStyle(srNosA);
							// System.out.println("in first if j==0" + k);
							// System.out.println(mapconveyancetravel);

						} else if (j == 1) {
							System.out.println(listconven);
							cell.setCellValue(listconven.get(k - 1).substring(1));
							innervallist = mapconveyancetravel.get(listconven.get(k - 1));
							amountcon = amountcon + UF.parseToInt(innervallist.get(8));
							System.out.println(amountcon);
						} else if (j > 1 && j < 11) {
							HSSFCellStyle srNosA = workbook.createCellStyle();
							srNosA.setAlignment(CellStyle.ALIGN_CENTER);
							srNosA.setBorderLeft(CellStyle.BORDER_THIN);
							srNosA.setBorderRight(CellStyle.BORDER_THIN);
							cell.setCellValue(innervallist.get(j - 2));
							cell.setCellStyle(srNosA);
						} else {

						}
					}

					System.out.println("amount other====amountcon" + amountothr + "con   " + amountcon);
					if (i == 23 + linecountconveyance && j == 0) {
						HSSFCellStyle srNosA = workbook.createCellStyle();
						Font srNoFont = workbook.createFont();
						srNoFont.setBoldweight((short) 1000);
						srNosA.setAlignment(CellStyle.ALIGN_CENTER);
						srNosA.setBorderLeft(CellStyle.BORDER_THIN);
						srNosA.setFont(srNoFont);
						cell.setCellStyle(srNosA);
						cell.setCellValue("B");
					}
					if (i == 23 + linecountconveyance && j == 1) {
						HSSFCellStyle conveyance = workbook.createCellStyle();
						Font fontForConveyance = workbook.createFont();
						fontForConveyance.setBoldweight((short) 1000);
						fontForConveyance.setUnderline((byte) 1);
						conveyance.setBorderLeft(CellStyle.BORDER_THIN);
						conveyance.setFont(fontForConveyance);
						cell.setCellStyle(conveyance);
						cell.setCellValue("Other Charges:-");
					}
					// conveyance_sheet.autoSizeColumn((int)j);
					if (i >= 25 + linecountconveyance && i < (25 + linecountconveyance + linecountothers) && j < 11) {
						if (j == 0) {
							o++;
							HSSFCellStyle srNosA = workbook.createCellStyle();
							Font srNoFont = workbook.createFont();
							srNoFont.setBoldweight((short) 1000);
							srNosA.setAlignment(CellStyle.ALIGN_CENTER);
							srNosA.setBorderLeft(CellStyle.BORDER_THIN);
							srNosA.setFont(srNoFont);
							cell.setCellValue(o);
							cell.setCellStyle(srNosA);

						} else if (j == 1) {
							System.out.println(listothers);
							cell.setCellValue(listothers.get(o - 1).substring(1));
							innervallist = mapconveyanceothers.get(listothers.get(o - 1));

							amountothr = amountothr + UF.parseToInt(innervallist.get(8));
							System.out.println("amount other==" + amountothr + "con   " + amountcon);

						} else if (j > 1 && j < 11) {
							HSSFCellStyle srNosA = workbook.createCellStyle();
							srNosA.setAlignment(CellStyle.ALIGN_CENTER);
							srNosA.setBorderLeft(CellStyle.BORDER_THIN);
							srNosA.setBorderRight(CellStyle.BORDER_THIN);
							cell.setCellValue(innervallist.get(j - 2));
							cell.setCellStyle(srNosA);
						} else {

						}

					}
				}
			}

			System.out.println("printing line total" + linetotal);
			Row grandTotalRow = conveyance_sheet.createRow(linetotal);
			Font styleForGrandTotal = workbook.createFont();
			styleForGrandTotal.setBoldweight((short) 1000);
			for (int i = 0; i < 11; i++) {
				Cell cell = grandTotalRow.createCell(i);
				HSSFCellStyle styleForTotalDes = workbook.createCellStyle();
				styleForTotalDes.setBorderBottom(CellStyle.BORDER_THIN);
				styleForTotalDes.setBorderTop(CellStyle.BORDER_THIN);
				styleForTotalDes.setFont(styleForGrandTotal);
				if (i == 0 || i == 10) {
					styleForTotalDes.setBorderLeft(CellStyle.BORDER_THIN);
				}
				if (i == 8) {
					cell.setCellValue("Grand Total");
				}
				if (i == 10) {
					cell.setCellValue(amountothr + amountcon);
					styleForTotalDes.setAlignment(CellStyle.ALIGN_RIGHT);
					styleForTotalDes.setBorderRight(CellStyle.BORDER_THIN);
				}
				cell.setCellStyle(styleForTotalDes);
			}
			linetotal++;
			System.out.println("printing line total 22    " + linetotal);
			Row blankRow5 = conveyance_sheet.createRow(linetotal);
			applyLeftAndRightBorderTable(blankRow5);
			linetotal++;
			Row noteRow = conveyance_sheet.createRow(linetotal);
			applyLeftAndRightBorderTable(noteRow);
			Cell noteCell = noteRow.createCell(0);
			HSSFCellStyle styleForNote = workbook.createCellStyle();
			styleForNote.setBorderLeft(CellStyle.BORDER_THIN);
			noteCell.setCellValue(" note");
			noteCell.setCellStyle(styleForNote);
			linetotal++;
			Row toAndFroOffice = conveyance_sheet.createRow(linetotal);
			applyLeftAndRightBorderTable(toAndFroOffice);
			Cell toAndFromOffCell = toAndFroOffice.createCell(0);
			toAndFromOffCell.setCellValue("  * -> To and Fro Office");
			toAndFromOffCell.setCellStyle(styleForNote);
			linetotal++;
			Row rule1 = conveyance_sheet.createRow(linetotal);
			applyLeftAndRightBorderTable(rule1);
			Cell rule1Cell = rule1.createCell(0);
			rule1Cell.setCellValue(" 1) Distance should be calculated from office to Client Place / Pickup Point or Home to Client Place / Pickup Point, whichever is less.");
			rule1Cell.setCellStyle(styleForNote);
			linetotal++;
			Row rule2 = conveyance_sheet.createRow(linetotal);
			applyLeftAndRightBorderTable(rule2);
			Cell rule2Cell = rule2.createCell(0);
			rule2Cell.setCellValue(" 2) Rickshaw Charges are not allowed except under exceptional situations and with Partners' Approval only.");
			rule2Cell.setCellStyle(styleForNote);
			linetotal++;
			for (int i = linetotal; i < linetotal + 6; i++) {
				Row blankRowAfterRules = conveyance_sheet.createRow(i);
				applyLeftAndRightBorderTable(blankRowAfterRules);
			}
			linetotal = linetotal + 6;
			Row signatureRow = conveyance_sheet.createRow(linetotal);
			applyLeftAndRightBorderTable(signatureRow);
			Cell preparedBy = signatureRow.createCell(1);
			preparedBy.setCellValue("Prepared By : ");

			Cell checkedBy = signatureRow.createCell(4);
			checkedBy.setCellValue("Checked By : ");

			Cell approvedBy = signatureRow.createCell(8);
			approvedBy.setCellValue("Approved By : ");

			linetotal++;
			Row lastRow = conveyance_sheet.createRow(linetotal);
			forBlankRowsWithBorder(lastRow);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// conveyance sheet
	public void applyLeftAndRightBorderTable(Row blankRow) {
		Cell firstCellOfBlankRow = blankRow.createCell(0);
		HSSFCellStyle blankRowStyle1 = workbook.createCellStyle();
		blankRowStyle1.setBorderLeft(CellStyle.BORDER_THIN);
		firstCellOfBlankRow.setCellStyle(blankRowStyle1);

		Cell lastCellOfBlankRow = blankRow.createCell(10);
		HSSFCellStyle blankRowStyle2 = workbook.createCellStyle();
		blankRowStyle2.setBorderRight(CellStyle.BORDER_THIN);
		lastCellOfBlankRow.setCellStyle(blankRowStyle2);

	}

	// conveyance sheet
	public void forBlankRowsWithBorder(Row lastRow) {
		for (int i = 0; i < 11; i++) {
			HSSFCellStyle bottomBorder = workbook.createCellStyle();
			bottomBorder.setBorderBottom(CellStyle.BORDER_THIN);
			if (i == 0) {
				bottomBorder.setBorderLeft(CellStyle.BORDER_THIN);
			}
			if (i == 10) {
				bottomBorder.setBorderRight(CellStyle.BORDER_THIN);
			}
			Cell cell = lastRow.createCell(i);
			cell.setCellStyle(bottomBorder);
		}
	}

	// conveyance sheet
	public List<String> getHeading() {

		List<String> heading = new ArrayList<String>();
		heading.add("Sr.No");
		heading.add("Client");
		heading.add("Mode of Travel");
		heading.add("No. of Persons");
		heading.add("Chargeable to Client(Y/N)");
		heading.add("Place");
		heading.add("Days");
		heading.add("KM / Day *");
		heading.add("Rate / KM");
		heading.add("Amount Rs.");

		return heading;
	}

	// Time line sheet
	public List<String> getActivitiesList() {
		List<String> activities = new ArrayList<String>();
		activities.add(" Office Work :");
		activities.add(" Others(specify)");
		activities.add(" Training");
		activities.add(" Idle Time");
		activities.add(" Leave");
		activities.add(" Office Hoildays");

		return activities;
	}

	// Time line sheet
	public void forBlankRowLeftAndRightBorder(Row row) {
		for (int i = 0; i < 37; i++) {
			HSSFCellStyle bottomBorder = workbook.createCellStyle();
			if (i == 0) {
				bottomBorder.setBorderLeft(CellStyle.BORDER_THIN);
			}
			if (i == 36) {
				bottomBorder.setBorderRight(CellStyle.BORDER_THIN);
			}
			Cell cell = row.createCell(i);
			cell.setCellStyle(bottomBorder);
		}
	}

	public String getReportName() {
		String name = "New-Time Report Format";
		return name;
	}

}
