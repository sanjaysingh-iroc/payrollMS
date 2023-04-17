package com.konnect.jpms.ajax;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.apache.poi.ss.usermodel.Row;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeSalaryHeadDetails extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements, IConstants {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	 
	CommonFunctions CF = null; 

	HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {

		this.response = response;
	}

	HSSFSheet employeeSalaryHead;
	HSSFWorkbook workbook;

	{
		workbook = new HSSFWorkbook();
		employeeSalaryHead = workbook.createSheet("Employee Salary Head Report");
	}	
	String strDownload;
	
	public String execute() throws Exception {
		session = request.getSession();if(session==null)return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE); 
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/ajax/EmployeeSalaryHeadDetails.jsp");
		request.setAttribute(TITLE, "Employee Salary Head Details");
		
		UtilityFunctions uF = new UtilityFunctions();
		viewEmployeeSalaryHeadDetail();

		if(getStrDownload() != null && getStrDownload().equals("download")) {
			return createExcelFile();
		}
		
		return loadGratia();
	}
	
	
	private String createExcelFile() {
		// TODO Auto-generated method stub

		try {
			writeEmployeeSalaryHeadReport();
			String reportName = "Employee Salary Head Report"; // get report name as per

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
	
		return null;
	}


	private void writeEmployeeSalaryHeadReport() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		int lessspace = 0;

		try {
			con=db.makeConnection(con);	

			Map<String, List<String>> hmEmpDetail = (Map<String, List<String>>)request.getAttribute("hmEmpDetail");
			Map<String, List<List<String>>> hmEmpSalaryData = (Map<String, List<List<String>>>)request.getAttribute("hmEmpSalaryData");
			
//			Row firmNameRow = employeeSalaryHead.createRow(5);
//			firmNameRow.setHeight((short) 450);
//			employeeSalaryHead.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
//			for (int i = 0; i < 8 + coloumncountmain; i++) {
//				HSSFCellStyle styleForFirmName = workbook.createCellStyle();
//				Font firmNameFont = workbook.createFont();
//				firmNameFont.setFontHeight((short) 320);
//				firmNameFont.setBoldweight((short) 1000);
//				styleForFirmName.setFont(firmNameFont);
//				styleForFirmName.setBorderTop(CellStyle.BORDER_THIN);
//				Cell firmNameCell = firmNameRow.createCell(i);
//				if (i == 0) {
//					styleForFirmName.setBorderLeft(CellStyle.BORDER_THIN);
//					firmNameCell.setCellValue(" " + CF.getStrOrgName());
//				}
//				if (i == 7 + coloumncountmain) {
//					styleForFirmName.setBorderRight(CellStyle.BORDER_THIN);
//				}
//				firmNameCell.setCellStyle(styleForFirmName);
//			}

			Row headingRowDesc = employeeSalaryHead.createRow(1);
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

			employeeSalaryHead.setColumnWidth(0, 1000);
			Cell srNoCell = headingRowDesc.createCell(0);
			srNoCell.setCellValue(" Sr. No. ");
			srNoCell.setCellStyle(headingStyle);

			Cell empCode = headingRowDesc.createCell(1);
			empCode.setCellValue("  Employee Code  ");
			employeeSalaryHead.autoSizeColumn((short) 1);
			empCode.setCellStyle(headingStyle);

			Cell empName = headingRowDesc.createCell(2);
			empName.setCellValue("  Employee Name  ");
			employeeSalaryHead.autoSizeColumn((short) 2);
			empName.setCellStyle(headingStyle);
			
			Cell level = headingRowDesc.createCell(3);
			level.setCellValue("  Level  ");
			employeeSalaryHead.autoSizeColumn((short) 3);
			level.setCellStyle(headingStyle);
			
			
			Cell desig = headingRowDesc.createCell(4);
			desig.setCellValue("  Designation  ");
			employeeSalaryHead.autoSizeColumn((short) 4);
			desig.setCellStyle(headingStyle);

			Cell grade = headingRowDesc.createCell(5);
			grade.setCellValue("  Grade  ");
			grade.setCellStyle(headingStyle);

			Cell salHeadName = headingRowDesc.createCell(6);
			salHeadName.setCellValue(" Salary Head Name  ");
			salHeadName.setCellStyle(headingStyle);

			Cell amount = headingRowDesc.createCell(7);
			amount.setCellValue("  Amount  ");
			amount.setCellStyle(headingStyle);
			
			Cell salHeadAmountType = headingRowDesc.createCell(8);
			salHeadAmountType.setCellValue("  Salary Head Amount Type  ");
			salHeadAmountType.setCellStyle(headingStyle);
			
			Cell payType = headingRowDesc.createCell(9);
			payType.setCellValue("  Pay Type  ");
			payType.setCellStyle(headingStyle);
			
			Cell isDisplay = headingRowDesc.createCell(10);
			isDisplay.setCellValue("  Is Display  ");
			isDisplay.setCellStyle(headingStyle);
			
			Cell isApproved = headingRowDesc.createCell(11);
			isApproved.setCellValue("  Is Approved  ");
			isApproved.setCellStyle(headingStyle);
			
			Cell effectiveDate = headingRowDesc.createCell(12);
			effectiveDate.setCellValue("  Effective Date  ");
			effectiveDate.setCellStyle(headingStyle);
			
			Cell earningDeduction = headingRowDesc.createCell(13);
			earningDeduction.setCellValue("  Earning / Deduction");
			earningDeduction.setCellStyle(headingStyle);
			
			Cell salaryTpe = headingRowDesc.createCell(14);
			salaryTpe.setCellValue("  Salary Type  ");
			salaryTpe.setCellStyle(headingStyle);
			
			Iterator<String> it = hmEmpDetail.keySet().iterator();
			int cnt = 0;
			int cntRow = 1;
			while(it.hasNext()) {
				String strEmpId = it.next();
				List<String> empDataList = hmEmpDetail.get(strEmpId);
				cnt++;
				cntRow++;
				Row dataRow = employeeSalaryHead.createRow(cntRow);
				
				Cell cell0 = dataRow.createCell(0);
				cell0.setCellValue(""+cnt);
				
				Cell cell1 = dataRow.createCell(1);
				cell1.setCellValue(empDataList.get(1));
				
				Cell cell2 = dataRow.createCell(2);
				cell2.setCellValue(empDataList.get(2));
				
				Cell cell3 = dataRow.createCell(3);
				cell3.setCellValue(empDataList.get(3));
				
				Cell cell4 = dataRow.createCell(4);
				cell4.setCellValue(empDataList.get(4));
				
				Cell cell5 = dataRow.createCell(5);
				cell5.setCellValue(empDataList.get(5));
				
				Cell cell6 = dataRow.createCell(6);
				cell6.setCellValue("");
				
				Cell cell7 = dataRow.createCell(7);
				cell7.setCellValue("");
				
				Cell cell8 = dataRow.createCell(8);
				cell8.setCellValue("");
				
				Cell cell9 = dataRow.createCell(9);
				cell9.setCellValue("");
				
				Cell cell10 = dataRow.createCell(10);
				cell10.setCellValue("");
				
				Cell cell11 = dataRow.createCell(11);
				cell11.setCellValue("");
				
				Cell cell12 = dataRow.createCell(12);
				cell12.setCellValue("");
				
				Cell cell13 = dataRow.createCell(13);
				cell13.setCellValue("");
				
				Cell cell14 = dataRow.createCell(14);
				cell14.setCellValue("");

				List<List<String>> empSalHeadDataList = hmEmpSalaryData.get(strEmpId);
				for(int i=0; empSalHeadDataList != null && i<empSalHeadDataList.size(); i++) {
					List<String> innerList = empSalHeadDataList.get(i);
				
					cntRow++;
					Row dataSubRow = employeeSalaryHead.createRow(cntRow);
					
					Cell cellSub0 = dataSubRow.createCell(0);
					cellSub0.setCellValue("");
					
					Cell cellSub1 = dataSubRow.createCell(1);
					cellSub1.setCellValue("");
					
					Cell cellSub2 = dataSubRow.createCell(2);
					cellSub2.setCellValue("");
					
					Cell cellSub3 = dataSubRow.createCell(3);
					cellSub3.setCellValue("");
					
					Cell cellSub4 = dataSubRow.createCell(4);
					cellSub4.setCellValue("");
					
					Cell cellSub5 = dataSubRow.createCell(5);
					cellSub5.setCellValue("");
					
					Cell cellSub6 = dataSubRow.createCell(6);
					cellSub6.setCellValue(innerList.get(1));
					
					Cell cellSub7 = dataSubRow.createCell(7);
					cellSub7.setCellValue(innerList.get(2));
					
					Cell cellSub8 = dataSubRow.createCell(8);
					cellSub8.setCellValue(innerList.get(3));
					
					Cell cellSub9 = dataSubRow.createCell(9);
					cellSub9.setCellValue(innerList.get(4));
					
					Cell cellSub10 = dataSubRow.createCell(10);
					cellSub10.setCellValue(innerList.get(5));
					
					Cell cellSub11 = dataSubRow.createCell(11);
					cellSub11.setCellValue(innerList.get(6));
					
					Cell cellSub12 = dataSubRow.createCell(12);
					cellSub12.setCellValue(innerList.get(7));
					
					Cell cellSub13 = dataSubRow.createCell(13);
					cellSub13.setCellValue(innerList.get(8));
					
					Cell cellSub14 = dataSubRow.createCell(14);
					cellSub14.setCellValue(innerList.get(9));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private String loadGratia() {
		return LOAD;
	}

	
	
	public void viewEmployeeSalaryHeadDetail(){
		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
			if(hmEmpCodeName==null) hmEmpCodeName = new HashMap<String, String>();
			Map<String, List<String>> hmEmpDetail = new HashMap<String, List<String>>();
			
			pst = con.prepareStatement("select epd.emp_per_id,epd.empcode,epd.salutation,epd.emp_fname,epd.emp_mname,epd.emp_lname,ld.level_id,ld.level_name,dd.designation_name,gd.grade_name,gd.grade_id from employee_personal_details epd,employee_official_details eod" +
				" left join grades_details gd on gd.grade_id = eod.grade_id " +
				" left join designation_details dd on dd.designation_id = gd.designation_id" +
				" left join level_details ld on ld.level_id = dd.level_id" +
				" left join department_info di on di.dept_id = eod.depart_id" +
				" left join employee_personal_details epsud on epsud.emp_per_id=eod.supervisor_emp_id" +
				" left join employee_personal_details ephod on ephod.emp_per_id=eod.hod_emp_id" +
				" left join employee_personal_details ephrd on ephrd.emp_per_id=eod.emp_hr" +
				" where epd.emp_per_id > 1 and epd.emp_per_id=eod.emp_id order by epd.emp_fname,epd.emp_mname,epd.emp_lname");
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery(); 
			while(rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("emp_per_id"));
				innerList.add(uF.showData(rs.getString("empcode"), ""));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				innerList.add(uF.showData(rs.getString("emp_fname"), "") +strEmpMName+" " + uF.showData(rs.getString("emp_lname"), ""));
				innerList.add(uF.showData(rs.getString("level_name"), ""));
				innerList.add(uF.showData(rs.getString("designation_name"), ""));
				innerList.add(uF.showData(rs.getString("grade_name"), ""));
				
				hmEmpDetail.put(rs.getString("emp_per_id"), innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmpDetail", hmEmpDetail);
			
			Map<String, List<String>> hmSalHeadData = new HashMap<String, List<String>>();
			pst = con.prepareStatement("select salary_head_id, salary_head_name, salary_head_amount_type, sub_salary_head_id from salary_details");
			rs = pst.executeQuery(); 
			while(rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("salary_head_id"));
				innerList.add(rs.getString("salary_head_name"));
				innerList.add((rs.getString("salary_head_amount_type") != null && rs.getString("salary_head_amount_type").equals("A")) ? "Amount" : "Percentage");
				innerList.add(rs.getString("sub_salary_head_id"));
				
				hmSalHeadData.put(rs.getString("salary_head_id"), innerList);
			}
			rs.close();
			pst.close();
				
//			System.out.println("hmSalHeadData ===>> " + hmSalHeadData);
			
			Map<String, List<List<String>>> hmEmpSalaryData = new HashMap<String, List<List<String>>>();
//			List<List<String>> alSalHeadData = new ArrayList<List<String>>();
			pst = con.prepareStatement("select emp_id,salary_head_id,amount,pay_type,isdisplay,is_approved,effective_date,earning_deduction,salary_type " +
				" from emp_salary_details esd where effective_date in (select max(effective_date) as effective_date from emp_salary_details esd group by emp_id) " +
				" and emp_id > 0 order by emp_id,salary_head_id");
			rs = pst.executeQuery(); 
			while(rs.next()) {
//				System.out.println("salary_head_id ===>> " + rs.getString("salary_head_id"));
				List<String> salHeadDataList = hmSalHeadData.get(rs.getString("salary_head_id"));
//				System.out.println("salHeadDataList ===>> " + salHeadDataList);
				if(salHeadDataList == null) salHeadDataList = new ArrayList<String>();
				if(salHeadDataList.size() == 0) {
					continue;
				}
				List<List<String>> alSalHeadData = hmEmpSalaryData.get(rs.getString("emp_id"));
				if(alSalHeadData == null) alSalHeadData = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("salary_head_id"));
				innerList.add(uF.showData(salHeadDataList.get(1), ""));
				innerList.add(rs.getString("amount"));
				innerList.add(salHeadDataList.get(2));
				innerList.add(rs.getString("pay_type"));
				innerList.add(rs.getString("isdisplay"));
				innerList.add(rs.getString("is_approved"));
				innerList.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT_STR));
				innerList.add(rs.getString("earning_deduction"));
				innerList.add(rs.getString("salary_type"));
				alSalHeadData.add(innerList);
				
				hmEmpSalaryData.put(rs.getString("emp_id"), alSalHeadData);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmpSalaryData", hmEmpSalaryData);
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public String getStrDownload() {
		return strDownload;
	}

	public void setStrDownload(String strDownload) {
		this.strDownload = strDownload;
	}

	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}