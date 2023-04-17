package com.konnect.jpms.performance;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class CohortsReportManager implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	HttpSession session;
	String strEmpId;
	String appId;
	String appFreqId;
	
	String sessionEmpId;
	CommonFunctions CF; 
	String type;
	private String cohortsType;

	HSSFSheet firstSheet;	
	//Collection<File> files;
	HSSFWorkbook workbook;
	//File exactFile;
	HSSFCellStyle cellStyleForHeader;
	HSSFCellStyle cellStyleForData;
	HSSFCellStyle cellStyleForReportName;	

	{
		workbook = new HSSFWorkbook();
		firstSheet = workbook.createSheet("Reports");
//		Row headerRow = firstSheet.createRow(0);
//		headerRow.setHeightInPoints(40);
		HSSFPalette pallet = workbook.getCustomPalette();
		pallet.setColorAtIndex(HSSFColor.GREY_25_PERCENT.index, (byte)230, (byte)225, (byte)225);
	}
	
	public void execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return;

		sessionEmpId = (String) session.getAttribute(EMPID);

		if(getType() != null && getType().equals("EXCEL")) {
			try {
				if(getCohortsType() != null && getCohortsType().equalsIgnoreCase("EMPWISE")) {
					createEmployeewiseCohortExcelFile(workbook);
				} else {
					createExcelFile(workbook);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		session.removeAttribute("pro_id");
	}
	

	private void createEmployeewiseCohortExcelFile(HSSFWorkbook workbook) {

		FileOutputStream fileOut = null;
		
		generateEmployeewiseCohortReportOfFeebbackExcel();
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			workbook.write(buffer);
			buffer.close();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		response.setHeader("Content-Disposition", "attachment; filename=\"EmployeewiseCohortsReport.xls\"");
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

	}


	private void generateEmployeewiseCohortReportOfFeebbackExcel() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			Map<String, List<String>> hmMainLevelData = new LinkedHashMap<String, List<String>>();
			pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id=? order by main_level_id");
			pst.setInt(1, uF.parseToInt(getAppId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("main_level_id"));
				innerList.add(rs.getString("level_title"));
				innerList.add(rs.getString("short_description"));
				innerList.add(rs.getString("long_description"));
				innerList.add(rs.getString("attribute_id"));
				innerList.add(rs.getString("section_weightage"));
				
				hmMainLevelData.put(rs.getString("main_level_id"), innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmMainLevelData", hmMainLevelData);
//			System.out.println("hmMainLevelData ===>> " + hmMainLevelData);
			
			Map<String, List<List<String>>> hmAppSubSectionData = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alAppSubSectionData = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id=? order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getAppId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				alAppSubSectionData = hmAppSubSectionData.get(rs.getString("main_level_id"));
				if(alAppSubSectionData == null)alAppSubSectionData = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_level_id"));
				innerList.add(rs.getString("level_title"));
				innerList.add(rs.getString("short_description"));
				innerList.add(rs.getString("long_description"));
				innerList.add(rs.getString("subsection_weightage"));
				alAppSubSectionData.add(innerList);
				hmAppSubSectionData.put(rs.getString("main_level_id"), alAppSubSectionData);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmAppSubSectionData", hmAppSubSectionData);
//			System.out.println("hmAppSubSectionData ===>> " + hmAppSubSectionData);
			
			Map<String, List<List<String>>> hmAppCompetencyData = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alAppCompetencyData = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from appraisal_scorecard_details where appraisal_id=? order by scorecard_id");
			pst.setInt(1, uF.parseToInt(getAppId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				alAppCompetencyData = hmAppCompetencyData.get(rs.getString("level_id"));
				if(alAppCompetencyData == null)alAppCompetencyData = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(rs.getString("scorecard_section_name"));
				innerList.add(rs.getString("scorecard_description"));
				innerList.add(rs.getString("scorecard_weightage"));
				alAppCompetencyData.add(innerList);
				hmAppCompetencyData.put(rs.getString("level_id"), alAppCompetencyData);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmAppCompetencyData", hmAppCompetencyData);
//			System.out.println("hmAppCompetencyData ===>> " + hmAppCompetencyData);
			
			Map<String, List<List<String>>> hmAppMeasureData = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alAppMeasureData = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from appraisal_measure_details where appraisal_id=? order by measure_id");
			pst.setInt(1, uF.parseToInt(getAppId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				alAppMeasureData = hmAppMeasureData.get(rs.getString("scorecard_id"));
				if(alAppMeasureData == null)alAppMeasureData = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("measure_section_name"));
				innerList.add(rs.getString("measure_description"));
				innerList.add(rs.getString("weightage"));
				alAppMeasureData.add(innerList);
				hmAppMeasureData.put(rs.getString("scorecard_id"), alAppMeasureData);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmAppMeasureData", hmAppMeasureData);
//			System.out.println("hmAppMeasureData ===>> " + hmAppMeasureData);
			
			Map<String, List<List<String>>> hmAppQuestionData = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alAppQuestionData = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from appraisal_question_details aqd, question_bank qb where qb.question_bank_id = aqd.question_id " +
				"and appraisal_id=? order by appraisal_question_details_id");
			pst.setInt(1, uF.parseToInt(getAppId()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				alAppQuestionData = hmAppQuestionData.get(rs.getString("measure_id"));
				if(alAppQuestionData == null)alAppQuestionData = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("question_text").trim());
				innerList.add(rs.getString("weightage"));
				alAppQuestionData.add(innerList);
				hmAppQuestionData.put(rs.getString("measure_id"), alAppQuestionData);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmAppQuestionData", hmAppQuestionData);
//			System.out.println("hmAppQuestionData ===>> " + hmAppQuestionData);

			Map<String, Map<String, String>> hmQueDetails = new LinkedHashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select * from question_bank where question_bank_id in (select question_id from appraisal_question_answer where user_type_id=2 " +
				"and appraisal_id=?)");
			pst.setInt(1, uF.parseToInt(getAppId()));
	//		System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String> hmQueData = hmQueDetails.get(rs.getString("question_bank_id"));
				if(hmQueData == null)hmQueData = new LinkedHashMap<String, String>();
				
				hmQueData.put("a,", rs.getString("rate_option_a"));
				hmQueData.put("b,", rs.getString("rate_option_b"));
				hmQueData.put("c,", rs.getString("rate_option_c"));
				hmQueData.put("d,", rs.getString("rate_option_d"));
				hmQueData.put("e,", rs.getString("rate_option_e"));

				hmQueDetails.put(rs.getString("question_bank_id"), hmQueData);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmQueDetails", hmQueDetails);
//			System.out.println("hmQueDetails ===>> " + hmQueDetails);
			
			StringBuilder sbQueId = null;
			Map<String, Map<String, String>> hmQueAnsData = new LinkedHashMap<String, Map<String, String>>();
//			List<List<String>> alQueAnsData = new ArrayList<List<String>>();
			pst = con.prepareStatement("select question_id, answer, emp_id from appraisal_question_answer where user_type_id=2 " +
				"and appraisal_id=? group by question_id,answer,emp_id order by question_id,answer");
			pst.setInt(1, uF.parseToInt(getAppId()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String>  hmQueAnsDetails = hmQueAnsData.get(rs.getString("emp_id"));
				if(hmQueAnsDetails == null)hmQueAnsDetails = new LinkedHashMap<String, String>();
				
//				Map<String, String> hmQueData = hmQueDetails.get(rs.getString("question_id"));
//				if(hmQueData == null)hmQueData = new LinkedHashMap<String, String>();
				
				hmQueAnsDetails.put(rs.getString("question_id"), rs.getString("answer"));
				hmQueAnsData.put(rs.getString("emp_id"), hmQueAnsDetails);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmQueAnsData", hmQueAnsData);
//			System.out.println("hmQueAnsData ===>> " + hmQueAnsData);
//			System.out.println("hmExistQueAns ===>> " + hmExistQueAns);
			
			Row headingRowDesc = firstSheet.createRow(0);
			headingRowDesc.setHeight((short) 500);
			
			Row headingRowDesc1 = firstSheet.createRow(1);
			headingRowDesc1.setHeight((short) 500);
			
			HSSFCellStyle headingStyle = workbook.createCellStyle();
			headingStyle.setBorderLeft(CellStyle.BORDER_THIN);
			headingStyle.setBorderRight(CellStyle.BORDER_THIN);
			headingStyle.setBorderTop(CellStyle.BORDER_THIN);
			headingStyle.setAlignment(CellStyle.ALIGN_CENTER);
			headingStyle.setWrapText(true);
			headingStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
			HSSFFont headingFont = workbook.createFont();
			headingFont.setBoldweight((short) 1000);
			headingStyle.setFont(headingFont);

			firstSheet.setColumnWidth(0, 1000);
			Cell srNoCell = headingRowDesc1.createCell(0);
			srNoCell.setCellValue("Sr. No.");
			srNoCell.setCellStyle(headingStyle);
			
			firstSheet.setColumnWidth(0, 1000);
			srNoCell = headingRowDesc1.createCell(1);
			srNoCell.setCellValue("Employee Code");
			srNoCell.setCellStyle(headingStyle);
			
			firstSheet.setColumnWidth(0, 1000);
			srNoCell = headingRowDesc1.createCell(2);
			srNoCell.setCellValue("Employee Name");
			srNoCell.setCellStyle(headingStyle);
			
			firstSheet.setColumnWidth(0, 1000);
			srNoCell = headingRowDesc1.createCell(3);
			srNoCell.setCellValue("Department");
			srNoCell.setCellStyle(headingStyle);
			
			firstSheet.setColumnWidth(0, 1000);
			srNoCell = headingRowDesc1.createCell(4);
			srNoCell.setCellValue("Designation");
			srNoCell.setCellStyle(headingStyle);
			
			firstSheet.setColumnWidth(0, 1000);
			srNoCell = headingRowDesc1.createCell(5);
			srNoCell.setCellValue("Level");
			srNoCell.setCellStyle(headingStyle);
			
			
			
			Iterator<String> it1 = hmMainLevelData.keySet().iterator();
			int colCnt1 = 6;
			while (it1.hasNext()) {
				String mainLevelId = it1.next();
				List<List<String>> subSectionInnList = hmAppSubSectionData.get(mainLevelId);
				if(subSectionInnList != null && !subSectionInnList.isEmpty()) {
					for(int i=0; i<subSectionInnList.size(); i++) {
						List<String> alInn = subSectionInnList.get(i);
						
						List<List<String>> competencyInnList = hmAppCompetencyData.get(alInn.get(0));
						if(competencyInnList != null && !competencyInnList.isEmpty()) {
							for(int j=0; j<competencyInnList.size(); j++) {
								List<String> alInn1 = competencyInnList.get(j);
								List<List<String>> measureInnList = hmAppMeasureData.get(alInn1.get(0));
								if(measureInnList != null && !measureInnList.isEmpty()) {
									for(int k=0; k<measureInnList.size(); k++) {
										List<String> alInn2 = measureInnList.get(k);
										List<List<String>> questionInnList = hmAppQuestionData.get(alInn2.get(0));
										for(int l=0; l<questionInnList.size(); l++) {
											List<String> alInn3 = questionInnList.get(l);
											
											srNoCell = headingRowDesc1.createCell((colCnt1));
											srNoCell.setCellValue(alInn3.get(2));
											firstSheet.autoSizeColumn((short) colCnt1);
											srNoCell.setCellStyle(headingStyle);
											colCnt1++;
										}
									}
								}
								
							}
						}
					}
				}
			}
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			Map<String, String> hmEmpDesigName = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpDepart = CF.getEmpDepartmentMap(con);
			Map<String, String> hmEmpLevel = CF.getEmpLevelMap(con);
			
			Map<String, String> hmDepartName = CF.getDeptMap(con);
			Map<String, String> hmLevelName = CF.getLevelMap(con);
			
			int rowCnt=1;
			Iterator<String> itEmp = hmQueAnsData.keySet().iterator();
			while (itEmp.hasNext()) {
				rowCnt++;
				Row dataRowDesc = firstSheet.createRow(rowCnt);
				dataRowDesc.setHeight((short) 500);
				
				String empId = itEmp.next();
				Map<String, String>  hmQueAnsDetails = hmQueAnsData.get(empId);
				
				firstSheet.setColumnWidth(0, 1000);
				Cell dataCell = dataRowDesc.createCell(0);
				dataCell.setCellValue(""+(rowCnt-1));
//				dataCell.setCellStyle(headingStyle);
				
				firstSheet.setColumnWidth(0, 1000);
				dataCell = dataRowDesc.createCell(1);
				dataCell.setCellValue(hmEmpCode.get(empId));
//				dataCell.setCellStyle(headingStyle);
				
				firstSheet.setColumnWidth(0, 1000);
				dataCell = dataRowDesc.createCell(2);
				dataCell.setCellValue(hmEmpName.get(empId));
//				dataCell.setCellStyle(headingStyle);
				
				firstSheet.setColumnWidth(0, 1000);
				dataCell = dataRowDesc.createCell(3);
				dataCell.setCellValue(hmDepartName.get(hmEmpDepart.get(empId)));
//				dataCell.setCellStyle(headingStyle);
				
				firstSheet.setColumnWidth(0, 1000);
				dataCell = dataRowDesc.createCell(4);
				dataCell.setCellValue(hmEmpDesigName.get(empId));
//				dataCell.setCellStyle(headingStyle);
				
				firstSheet.setColumnWidth(0, 1000);
				dataCell = dataRowDesc.createCell(5);
				dataCell.setCellValue(hmLevelName.get(hmEmpLevel.get(empId)));
//				dataCell.setCellStyle(headingStyle);
				
				Iterator<String> it = hmMainLevelData.keySet().iterator();
				int colCnt = 6;
				while (it.hasNext()) {
					String mainLevelId = it.next();
					
					List<List<String>> subSectionInnList = hmAppSubSectionData.get(mainLevelId);
					if(subSectionInnList != null && !subSectionInnList.isEmpty()) {
						for(int i=0; i<subSectionInnList.size(); i++) {
							List<String> alInn = subSectionInnList.get(i);
							
							List<List<String>> competencyInnList = hmAppCompetencyData.get(alInn.get(0));
							if(competencyInnList != null && !competencyInnList.isEmpty()) {
								for(int j=0; j<competencyInnList.size(); j++) {
									List<String> alInn1 = competencyInnList.get(j);
									List<List<String>> measureInnList = hmAppMeasureData.get(alInn1.get(0));
									if(measureInnList != null && !measureInnList.isEmpty()) {
										for(int k=0; k<measureInnList.size(); k++) {
											List<String> alInn2 = measureInnList.get(k);
											List<List<String>> questionInnList = hmAppQuestionData.get(alInn2.get(0));
											for(int l=0; l<questionInnList.size(); l++) {
												List<String> alInn3 = questionInnList.get(l);
												Map<String, String> hmQueData = hmQueDetails.get(alInn3.get(1));
												
												srNoCell = dataRowDesc.createCell((colCnt));
												srNoCell.setCellValue(hmQueData.get(hmQueAnsDetails.get(alInn3.get(1))));
												firstSheet.autoSizeColumn((short) colCnt);
	//											row2Cell.setCellStyle(headingStyle);
												
												colCnt++;
											}
										}
									}
									
								}
							}
						}
					}
				}
			}
			
//			System.out.println("hmReadUnreadCount =====> "+ hmReadUnreadCount);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("STATUS_MSG", ERRORM+"Reminder mails not sent, Please try again."+END);
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public void createExcelFile(HSSFWorkbook workbook) throws Exception {

		FileOutputStream fileOut = null;
		
		generateCohortReportOfManagersFeebbackExcel();
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			workbook.write(buffer);
			buffer.close();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		response.setHeader("Content-Disposition", "attachment; filename=\"CohortsReport.xls\"");
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

	}

	
	private void generateCohortReportOfManagersFeebbackExcel() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			Map<String, List<String>> hmMainLevelData = new LinkedHashMap<String, List<String>>();
			pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id=? order by main_level_id");
			pst.setInt(1, uF.parseToInt(getAppId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("main_level_id"));
				innerList.add(rs.getString("level_title"));
				innerList.add(rs.getString("short_description"));
				innerList.add(rs.getString("long_description"));
				innerList.add(rs.getString("attribute_id"));
				innerList.add(rs.getString("section_weightage"));
				
				hmMainLevelData.put(rs.getString("main_level_id"), innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmMainLevelData", hmMainLevelData);
//			System.out.println("hmMainLevelData ===>> " + hmMainLevelData);
			
			Map<String, List<List<String>>> hmAppSubSectionData = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alAppSubSectionData = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id=? order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getAppId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				alAppSubSectionData = hmAppSubSectionData.get(rs.getString("main_level_id"));
				if(alAppSubSectionData == null)alAppSubSectionData = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_level_id"));
				innerList.add(rs.getString("level_title"));
				innerList.add(rs.getString("short_description"));
				innerList.add(rs.getString("long_description"));
				innerList.add(rs.getString("subsection_weightage"));
				alAppSubSectionData.add(innerList);
				hmAppSubSectionData.put(rs.getString("main_level_id"), alAppSubSectionData);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmAppSubSectionData", hmAppSubSectionData);
//			System.out.println("hmAppSubSectionData ===>> " + hmAppSubSectionData);
			
			Map<String, List<List<String>>> hmAppCompetencyData = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alAppCompetencyData = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from appraisal_scorecard_details where appraisal_id=? order by scorecard_id");
			pst.setInt(1, uF.parseToInt(getAppId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				alAppCompetencyData = hmAppCompetencyData.get(rs.getString("level_id"));
				if(alAppCompetencyData == null)alAppCompetencyData = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(rs.getString("scorecard_section_name"));
				innerList.add(rs.getString("scorecard_description"));
				innerList.add(rs.getString("scorecard_weightage"));
				alAppCompetencyData.add(innerList);
				hmAppCompetencyData.put(rs.getString("level_id"), alAppCompetencyData);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmAppCompetencyData", hmAppCompetencyData);
//			System.out.println("hmAppCompetencyData ===>> " + hmAppCompetencyData);
			
			Map<String, List<List<String>>> hmAppMeasureData = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alAppMeasureData = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from appraisal_measure_details where appraisal_id=? order by measure_id");
			pst.setInt(1, uF.parseToInt(getAppId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				alAppMeasureData = hmAppMeasureData.get(rs.getString("scorecard_id"));
				if(alAppMeasureData == null)alAppMeasureData = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("measure_section_name"));
				innerList.add(rs.getString("measure_description"));
				innerList.add(rs.getString("weightage"));
				alAppMeasureData.add(innerList);
				hmAppMeasureData.put(rs.getString("scorecard_id"), alAppMeasureData);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmAppMeasureData", hmAppMeasureData);
//			System.out.println("hmAppMeasureData ===>> " + hmAppMeasureData);
			
			
			Map<String, List<List<String>>> hmAppQuestionData = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alAppQuestionData = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from appraisal_question_details aqd, question_bank qb where qb.question_bank_id = aqd.question_id " +
				"and appraisal_id=? order by appraisal_question_details_id");
			pst.setInt(1, uF.parseToInt(getAppId()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				alAppQuestionData = hmAppQuestionData.get(rs.getString("measure_id"));
				if(alAppQuestionData == null)alAppQuestionData = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("question_text").trim());
				innerList.add(rs.getString("weightage"));
				alAppQuestionData.add(innerList);
				hmAppQuestionData.put(rs.getString("measure_id"), alAppQuestionData);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmAppQuestionData", hmAppQuestionData);
//			System.out.println("hmAppQuestionData ===>> " + hmAppQuestionData);

			Map<String, Map<String, String>> hmQueDetails = new LinkedHashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select * from question_bank where question_bank_id in (select question_id from appraisal_question_answer where user_type_id=2 " +
				"and appraisal_id=?)");
			pst.setInt(1, uF.parseToInt(getAppId()));
	//		System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String> hmQueData = hmQueDetails.get(rs.getString("question_bank_id"));
				if(hmQueData == null)hmQueData = new LinkedHashMap<String, String>();
				
				hmQueData.put("a,", rs.getString("rate_option_a"));
				hmQueData.put("b,", rs.getString("rate_option_b"));
				hmQueData.put("c,", rs.getString("rate_option_c"));
				hmQueData.put("d,", rs.getString("rate_option_d"));
				hmQueData.put("e,", rs.getString("rate_option_e"));

				hmQueDetails.put(rs.getString("question_bank_id"), hmQueData);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmQueDetails", hmQueDetails);
//			System.out.println("hmQueDetails ===>> " + hmQueDetails);
			
			StringBuilder sbQueId = null;
			Map<String, Map<String, String>> hmQueAnsData = new LinkedHashMap<String, Map<String, String>>();
//			List<List<String>> alQueAnsData = new ArrayList<List<String>>();
			pst = con.prepareStatement("select question_id, count(answer) as ansCnt, answer from appraisal_question_answer where user_type_id=2 " +
				"and appraisal_id=? group by question_id,answer order by question_id,answer");
			pst.setInt(1, uF.parseToInt(getAppId()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String>  hmQueAnsDetails = hmQueAnsData.get(rs.getString("question_id"));
				if(hmQueAnsDetails == null)hmQueAnsDetails = new LinkedHashMap<String, String>();
				
				Map<String, String> hmQueData = hmQueDetails.get(rs.getString("question_id"));
				if(hmQueData == null)hmQueData = new LinkedHashMap<String, String>();
				
				hmQueAnsDetails.put(hmQueData.get(rs.getString("answer").trim()), rs.getString("ansCnt"));
				hmQueAnsData.put(rs.getString("question_id"), hmQueAnsDetails);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmQueAnsData", hmQueAnsData);
//			System.out.println("hmQueAnsData ===>> " + hmQueAnsData);
			
			
//			System.out.println("hmExistQueAns ===>> " + hmExistQueAns);
			
			Row headingRowDesc = firstSheet.createRow(0);
			headingRowDesc.setHeight((short) 500);
			Row row1 = firstSheet.createRow(1);
			row1.setHeight((short) 500);
			Row row2 = firstSheet.createRow(2);
			row2.setHeight((short) 500);
			Row row3 = firstSheet.createRow(3);
			row3.setHeight((short) 500);
			Row row4 = firstSheet.createRow(4);
			row4.setHeight((short) 500);
			Row row5 = firstSheet.createRow(5);
			row5.setHeight((short) 500);
			Row row6 = firstSheet.createRow(6);
			row6.setHeight((short) 500);
			Row row7 = firstSheet.createRow(7);
			row7.setHeight((short) 500);
			Row row8 = firstSheet.createRow(8);
			row8.setHeight((short) 500);
			Row row9 = firstSheet.createRow(9);
			row9.setHeight((short) 500);
			
			HSSFCellStyle headingStyle = workbook.createCellStyle();
			headingStyle.setBorderLeft(CellStyle.BORDER_THIN);
			headingStyle.setBorderRight(CellStyle.BORDER_THIN);
			headingStyle.setBorderTop(CellStyle.BORDER_THIN);
			headingStyle.setAlignment(CellStyle.ALIGN_CENTER);
			headingStyle.setWrapText(true);
			headingStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
			HSSFFont headingFont = workbook.createFont();
			headingFont.setBoldweight((short) 1000);
			headingStyle.setFont(headingFont);

			firstSheet.setColumnWidth(0, 1000);
			Cell srNoCell = headingRowDesc.createCell(0);
			srNoCell.setCellValue("Category");
			srNoCell.setCellStyle(headingStyle);
			
			firstSheet.setColumnWidth(0, 1000);
			Cell row1Cell1 = row1.createCell(0);
			row1Cell1.setCellValue("Competency");
			row1Cell1.setCellStyle(headingStyle);
			
			firstSheet.setColumnWidth(0, 1000);
			Cell row2Cell1 = row2.createCell(0);
			row2Cell1.setCellValue("Element");
			row2Cell1.setCellStyle(headingStyle);
			
			firstSheet.setColumnWidth(0, 1000);
			Cell row3Cell1 = row3.createCell(0);
			row3Cell1.setCellValue("1");
			row3Cell1.setCellStyle(headingStyle);
			
			firstSheet.setColumnWidth(0, 1000);
			Cell row4Cell1 = row4.createCell(0);
			row4Cell1.setCellValue("2");
			row4Cell1.setCellStyle(headingStyle);
			
			firstSheet.setColumnWidth(0, 1000);
			Cell row5Cell1 = row5.createCell(0);
			row5Cell1.setCellValue("3");
			row5Cell1.setCellStyle(headingStyle);
			
			firstSheet.setColumnWidth(0, 1000);
			Cell row6Cell1 = row6.createCell(0);
			row6Cell1.setCellValue("4");
			row6Cell1.setCellStyle(headingStyle);
			
			firstSheet.setColumnWidth(0, 1000);
			Cell row7Cell1 = row7.createCell(0);
			row7Cell1.setCellValue("5");
			row7Cell1.setCellStyle(headingStyle);
			
			Iterator<String> it = hmMainLevelData.keySet().iterator();
			int colCnt = 1;
			while (it.hasNext()) {
				String mainLevelId = it.next();
				List<String> mainLevelInnList = hmMainLevelData.get(mainLevelId);
//				colCnt++;
//				System.out.println("colCnt ===>> " + colCnt);
				Cell nameOfReviewee = headingRowDesc.createCell(colCnt);
				nameOfReviewee.setCellValue(mainLevelInnList.get(1));
				firstSheet.autoSizeColumn((short) colCnt);
				nameOfReviewee.setCellStyle(headingStyle);
				
				List<List<String>> subSectionInnList = hmAppSubSectionData.get(mainLevelId);
				if(subSectionInnList != null && !subSectionInnList.isEmpty()) {
					for(int i=0; i<subSectionInnList.size(); i++) {
						List<String> alInn = subSectionInnList.get(i);
						
						List<List<String>> competencyInnList = hmAppCompetencyData.get(alInn.get(0));
						if(competencyInnList != null && !competencyInnList.isEmpty()) {
							for(int j=0; j<competencyInnList.size(); j++) {
								List<String> alInn1 = competencyInnList.get(j);
								List<List<String>> measureInnList = hmAppMeasureData.get(alInn1.get(0));
								if(measureInnList != null && !measureInnList.isEmpty()) {
									for(int k=0; k<measureInnList.size(); k++) {
										List<String> alInn2 = measureInnList.get(k);
										String strMeasureName = alInn2.get(1);
//										colCnt++;
										Cell row1Cell = row1.createCell(colCnt);
										row1Cell.setCellValue(alInn2.get(1));
										firstSheet.autoSizeColumn((short) colCnt);
										row1Cell.setCellStyle(headingStyle);
										
										double dblAllQueRate1Cnt = 0;
										double dblAllQueRate2Cnt = 0;
										double dblAllQueRate3Cnt = 0;
										double dblAllQueRate4Cnt = 0;
										double dblAllQueRate5Cnt = 0;
										
										int qCnt = 0;
										List<List<String>> questionInnList = hmAppQuestionData.get(alInn2.get(0));
										for(int l=0; l<questionInnList.size(); l++) {
											List<String> alInn3 = questionInnList.get(l);
											qCnt++;
											if(l<(questionInnList.size()-1)) {
												nameOfReviewee = headingRowDesc.createCell((colCnt+1));
												nameOfReviewee.setCellValue(""); // +qCnt
												firstSheet.autoSizeColumn((short) (colCnt+1));
												nameOfReviewee.setCellStyle(headingStyle);
												
												row1Cell = row1.createCell((colCnt+1));
												row1Cell.setCellValue(""); // +qCnt
												firstSheet.autoSizeColumn((short) (colCnt+1));
												row1Cell.setCellStyle(headingStyle);
											}
											Cell row2Cell = row2.createCell((colCnt));
											row2Cell.setCellValue(alInn3.get(2));
											firstSheet.autoSizeColumn((short) colCnt);
//											row2Cell.setCellStyle(headingStyle);
											
											Map<String, String> hmQueInner = hmQueDetails.get(alInn3.get(1));
											Map<String, String> hmInner = hmQueAnsData.get(alInn3.get(1));
											
											String strRate1 = hmInner.get("1");
											String strRate2 = hmInner.get("2");
											String strRate3 = hmInner.get("3");
											String strRate4 = hmInner.get("4");
											String strRate5 = hmInner.get("5");
											
											dblAllQueRate1Cnt += uF.parseToDouble(strRate1);
											dblAllQueRate2Cnt += uF.parseToDouble(strRate2);
											dblAllQueRate3Cnt += uF.parseToDouble(strRate3);
											dblAllQueRate4Cnt += uF.parseToDouble(strRate4);
											dblAllQueRate5Cnt += uF.parseToDouble(strRate5);
											
											Cell row3Cell = row3.createCell((colCnt));
											row3Cell.setCellValue(uF.showData(strRate1, "0"));
											firstSheet.autoSizeColumn((short) colCnt);
											
											Cell row4Cell = row4.createCell((colCnt));
											row4Cell.setCellValue(uF.showData(strRate2, "0"));
											firstSheet.autoSizeColumn((short) colCnt);
											
											Cell row5Cell = row5.createCell((colCnt));
											row5Cell.setCellValue(uF.showData(strRate3, "0"));
											firstSheet.autoSizeColumn((short) colCnt);
											
											Cell row6Cell = row6.createCell((colCnt));
											row6Cell.setCellValue(uF.showData(strRate4, "0"));
											firstSheet.autoSizeColumn((short) colCnt);
											
											Cell row7Cell = row7.createCell((colCnt));
											row7Cell.setCellValue(uF.showData(strRate5, "0"));
											firstSheet.autoSizeColumn((short) colCnt);
											
											colCnt++;
										}
//										colCnt--;
										
										nameOfReviewee = headingRowDesc.createCell(colCnt);
										nameOfReviewee.setCellValue("");
										firstSheet.autoSizeColumn((short) colCnt);
										nameOfReviewee.setCellStyle(headingStyle);
										
										row1Cell = row1.createCell(colCnt);
										row1Cell.setCellValue("");
										firstSheet.autoSizeColumn((short) colCnt);
										row1Cell.setCellStyle(headingStyle);
										
										Cell row2Cell = row2.createCell((colCnt));
										row2Cell.setCellValue("Avg: " + strMeasureName);
										firstSheet.autoSizeColumn((short) colCnt);
										row2Cell.setCellStyle(headingStyle);
										
										
										Cell row3Cell = row3.createCell((colCnt));
										row3Cell.setCellValue(Math.round(dblAllQueRate1Cnt/qCnt));
										firstSheet.autoSizeColumn((short) colCnt);
										row3Cell.setCellStyle(headingStyle);
										
										Cell row4Cell = row4.createCell((colCnt));
										row4Cell.setCellValue(Math.round(dblAllQueRate2Cnt/qCnt));
										firstSheet.autoSizeColumn((short) colCnt);
										row4Cell.setCellStyle(headingStyle);
										
										Cell row5Cell = row5.createCell((colCnt));
										row5Cell.setCellValue(Math.round(dblAllQueRate3Cnt/qCnt));
										firstSheet.autoSizeColumn((short) colCnt);
										row5Cell.setCellStyle(headingStyle);
										
										Cell row6Cell = row6.createCell((colCnt));
										row6Cell.setCellValue(Math.round(dblAllQueRate4Cnt/qCnt));
										firstSheet.autoSizeColumn((short) colCnt);
										row6Cell.setCellStyle(headingStyle);
										
										Cell row7Cell = row7.createCell((colCnt));
										row7Cell.setCellValue(Math.round(dblAllQueRate5Cnt/qCnt));
										firstSheet.autoSizeColumn((short) colCnt);
										row7Cell.setCellStyle(headingStyle);
										
										colCnt++;
									}
									colCnt--;
								}
								
							}
						}
					}
				}
				colCnt++;
			}
			
			
//			Cell nameOfReviewee = headingRowDesc.createCell(1);
//			nameOfReviewee.setCellValue("  Name of Reviewee ");
//			firstSheet.autoSizeColumn((short) 1);
//			nameOfReviewee.setCellStyle(headingStyle);
//
//			Cell nameOfManager = headingRowDesc.createCell(2);
//			nameOfManager.setCellValue(" Manager ");
//			firstSheet.autoSizeColumn((short) 2);
//			nameOfManager.setCellStyle(headingStyle);
//			
//			Cell nameOfPeer = headingRowDesc.createCell(3);
//			nameOfPeer.setCellValue(" Peer ");
//			firstSheet.autoSizeColumn((short) 3);
//			nameOfPeer.setCellStyle(headingStyle);
//			
//			
//			Cell nameOfSubOrdinate = headingRowDesc.createCell(4);
//			nameOfSubOrdinate.setCellValue(" Sub-ordinate ");
//			firstSheet.autoSizeColumn((short) 4);
//			nameOfSubOrdinate.setCellStyle(headingStyle);
//
//			Cell nameOfOtherPeer = headingRowDesc.createCell(5);
//			nameOfOtherPeer.setCellValue(" Other Peer ");
//			firstSheet.autoSizeColumn((short) 5);
//			nameOfOtherPeer.setCellStyle(headingStyle);
//
//			Cell nameOfGroupHead = headingRowDesc.createCell(6);
//			nameOfGroupHead.setCellValue(" GroupHead ");
//			firstSheet.autoSizeColumn((short) 6);
//			nameOfGroupHead.setCellStyle(headingStyle);
//			
//			
//			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
//			Iterator<String> it = hmRevieweewiseAppraiser.keySet().iterator();
//			int srCnt=0;
//			while(it.hasNext()) {
//				srCnt++;
//				headingRowDesc = firstSheet.createRow(srCnt);
//				String strRevieweeId = it.next();
//				Map<String, String> hmRevieweeNameData = hmEmpInfo.get(strRevieweeId);
//				
//				Map<String, String> hmRevieweeData = hmRevieweewiseAppraiser.get(strRevieweeId);
//				List<List<String>> allIdList = new ArrayList<List<String>>();
//				firstSheet.setColumnWidth(0, 1000);
//				Cell srNoCell1 = headingRowDesc.createCell(0);
//				srNoCell1.setCellValue(""+srCnt);
//				srNoCell1.setCellStyle(headingStyle);
//
//				Cell nameOfReviewee1 = headingRowDesc.createCell(1);
//				nameOfReviewee1.setCellValue(hmRevieweeNameData.get("FNAME")+" " +hmRevieweeNameData.get("LNAME"));
//				firstSheet.autoSizeColumn((short) 1);
//
//				
//				StringBuilder strManagersName = null;
//				if(hmRevieweeData.get("REVIEW_MANAGERID") != null && !hmRevieweeData.get("REVIEW_MANAGERID").equals("")) {
//					List<String> managerID = Arrays.asList(hmRevieweeData.get("REVIEW_MANAGERID").split(",")); 
//					for (int i = 0; managerID != null && i < managerID.size(); i++) {
//						if(managerID.get(i) != null && !managerID.get(i).equals("")) {
//							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("Manager")+"_"+managerID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Manager")+"_"+managerID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
//								if(strManagersName == null) { 
//									strManagersName = new StringBuilder();
//									strManagersName.append(hmEmpName.get(managerID.get(i)));
//								} else {
//									strManagersName.append(", "+ hmEmpName.get(managerID.get(i)));
//								}
//							}
//						}
//					}
//				}
//				Cell nameOfManager1 = headingRowDesc.createCell(2);
//				nameOfManager1.setCellValue((strManagersName != null) ? strManagersName.toString() : "");
//				firstSheet.autoSizeColumn((short) 2);
//				
//				StringBuilder strPeersName = null;
//				if(hmRevieweeData.get("REVIEW_PEERID") != null && !hmRevieweeData.get("REVIEW_PEERID").equals("")) {
//					List<String> peerID = Arrays.asList(hmRevieweeData.get("REVIEW_PEERID").split(",")); 
//					for (int i = 0; peerID != null && i < peerID.size(); i++) {
//						if(peerID.get(i) != null && !peerID.get(i).equals("")) {
//							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("Peer")+"_"+peerID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Peer")+"_"+peerID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
//								if(strPeersName == null) { 
//									strPeersName = new StringBuilder();
//									strPeersName.append(hmEmpName.get(peerID.get(i)));
//								} else {
//									strPeersName.append(", "+ hmEmpName.get(peerID.get(i)));
//								}
//							}
//						}
//					}
//				}
//				Cell nameOfPeer1 = headingRowDesc.createCell(3);
//				nameOfPeer1.setCellValue((strPeersName != null) ? strPeersName.toString() : "");
//				firstSheet.autoSizeColumn((short) 3);
//				
//				StringBuilder strSubOrdinateName = null;
//				if(hmRevieweeData.get("REVIEW_SUBORDINATEID") != null && !hmRevieweeData.get("REVIEW_SUBORDINATEID").equals("")) {
//					List<String> subordinateID = Arrays.asList(hmRevieweeData.get("REVIEW_SUBORDINATEID").split(",")); 
//					for (int i = 0; subordinateID != null && i < subordinateID.size(); i++) {
//						if(subordinateID.get(i) != null && !subordinateID.get(i).equals("")) {
//							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("Sub-ordinate")+"_"+subordinateID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Sub-ordinate")+"_"+subordinateID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
//								if(strSubOrdinateName == null) { 
//									strSubOrdinateName = new StringBuilder();
//									strSubOrdinateName.append(hmEmpName.get(subordinateID.get(i)));
//								} else {
//									strSubOrdinateName.append(", "+ hmEmpName.get(subordinateID.get(i)));
//								}
//							}
//						}
//					}
//				}
//				Cell nameOfSubOrdinate1 = headingRowDesc.createCell(4);
//				nameOfSubOrdinate1.setCellValue((strSubOrdinateName != null) ? strSubOrdinateName.toString() : "");
//				firstSheet.autoSizeColumn((short) 4);
//				
//				StringBuilder strOtherPeersName = null;
//				if(hmRevieweeData.get("REVIEW_OTHERPEERID") != null && !hmRevieweeData.get("REVIEW_OTHERPEERID").equals("")) {
//					List<String> otherPeerID = Arrays.asList(hmRevieweeData.get("REVIEW_OTHERPEERID").split(",")); 
//					for (int i = 0; otherPeerID != null && i < otherPeerID.size(); i++) {
//						if(otherPeerID.get(i) != null && !otherPeerID.get(i).equals("")) {
//							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("Other Peer")+"_"+otherPeerID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Other Peer")+"_"+otherPeerID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
//								if(strOtherPeersName == null) { 
//									strOtherPeersName = new StringBuilder();
//									strOtherPeersName.append(hmEmpName.get(otherPeerID.get(i)));
//								} else {
//									strOtherPeersName.append(", "+ hmEmpName.get(otherPeerID.get(i)));
//								}
//							}
//						}
//					}
//				}
//				Cell nameOfOtherPeer1 = headingRowDesc.createCell(5);
//				nameOfOtherPeer1.setCellValue((strOtherPeersName != null) ? strOtherPeersName.toString() : "");
//				firstSheet.autoSizeColumn((short) 5);
//				
//				StringBuilder strGroupHeadName = null;
//				if(hmRevieweeData.get("REVIEW_GRANDSUPERVISORID") != null && !hmRevieweeData.get("REVIEW_GRANDSUPERVISORID").equals("")) {
//					List<String> gSupervisorID = Arrays.asList(hmRevieweeData.get("REVIEW_GRANDSUPERVISORID").split(",")); 
//					for (int i = 0; gSupervisorID != null && i < gSupervisorID.size(); i++) {
//						if(gSupervisorID.get(i) != null && !gSupervisorID.get(i).equals("")) {
//							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("GroupHead")+"_"+gSupervisorID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("GroupHead")+"_"+gSupervisorID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
//								if(strGroupHeadName == null) { 
//									strGroupHeadName = new StringBuilder();
//									strGroupHeadName.append(hmEmpName.get(gSupervisorID.get(i)));
//								} else {
//									strGroupHeadName.append(", "+ hmEmpName.get(gSupervisorID.get(i)));
//								}
//							}
//						}
//					}
//				}
//				Cell nameOfGroupHead1 = headingRowDesc.createCell(6);
//				nameOfGroupHead1.setCellValue((strGroupHeadName != null) ? strGroupHeadName.toString() : "");
//				firstSheet.autoSizeColumn((short) 6);
//				
//				if(hmRevieweeData.get("REVIEW_HRID") != null && !hmRevieweeData.get("REVIEW_HRID").equals("")) {
//					List<String> hrID = Arrays.asList(hmRevieweeData.get("REVIEW_HRID").split(",")); 
//					for (int i = 0; hrID != null && i < hrID.size(); i++) {
//						if(hrID.get(i) != null && !hrID.get(i).equals("")) {
//							
//							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("HR")+"_"+hrID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("HR")+"_"+hrID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
//								List<String> innerList = new ArrayList<String>();
//								innerList.add(hrID.get(i));
//								innerList.add("HR");
//								allIdList.add(innerList);
//							}
//						}
//					}
//				}
//				
//				if(hmRevieweeData.get("REVIEW_CEOID") != null && !hmRevieweeData.get("REVIEW_CEOID").equals("")) {
//					List<String> ceoID = Arrays.asList(hmRevieweeData.get("REVIEW_CEOID").split(",")); 
//					for (int i = 0; ceoID != null && i < ceoID.size(); i++) {
//						if(ceoID.get(i) != null && !ceoID.get(i).equals("")) {
//							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("CEO")+"_"+ceoID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("CEO")+"_"+ceoID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
//								List<String> innerList = new ArrayList<String>();
//								innerList.add(ceoID.get(i));
//								innerList.add("CEO");
//								allIdList.add(innerList);
//							}
//						}
//					}
//				}
//				
//				if(hmRevieweeData.get("REVIEW_HODID") != null && !hmRevieweeData.get("REVIEW_HODID").equals("")) {
//					List<String> hodID = Arrays.asList(hmRevieweeData.get("REVIEW_HODID").split(",")); 
//					for (int i = 0; hodID != null && i < hodID.size(); i++) {
//						if(hodID.get(i) != null && !hodID.get(i).equals("")) {
//							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("HOD")+"_"+hodID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("HOD")+"_"+hodID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
//								List<String> innerList = new ArrayList<String>();
//								innerList.add(hodID.get(i));
//								innerList.add("HOD");
//								allIdList.add(innerList);
//							}
//						}
//					}
//				}
//				
//				if(hmRevieweeData.get("REVIEW_OTHERID") != null && !hmRevieweeData.get("REVIEW_OTHERID").equals("")) {
//					List<String> otherID = Arrays.asList(hmRevieweeData.get("REVIEW_OTHERID").split(",")); 
//					for (int i = 0; otherID != null && i < otherID.size(); i++) {
//						if(otherID.get(i) != null && !otherID.get(i).equals("")) {
//							if(hmExistQueAns == null || hmExistQueAns.get(hmOrientData.get("Anyone")+"_"+otherID.get(i)+"_"+strRevieweeId) == null || uF.parseToInt(hmExistQueAns.get(hmOrientData.get("Anyone")+"_"+otherID.get(i)+"_"+strRevieweeId)) != uF.parseToInt(strRevieweeId)) {
//								if(strOtherPeersName == null) { 
//									strOtherPeersName = new StringBuilder();
//									strOtherPeersName.append(hmEmpName.get(otherID.get(i)));
//								} else {
//									strOtherPeersName.append(", "+ hmEmpName.get(otherID.get(i)));
//								}
//							}
//						}
//					}
//				}
//				
//				request.setAttribute("STATUS_MSG", SUCCESSM+"Reminder mails sent successfully."+END);
//			}
			
//			System.out.println("hmReadUnreadCount =====> "+ hmReadUnreadCount);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("STATUS_MSG", ERRORM+"Reminder mails not sent, Please try again."+END);
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
	}

	
	
	private Map<String, Map<String, String>> getRevieweewiseAppraiser(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, Map<String, String>> hmRevieweewiseAppraiser = new HashMap<String, Map<String, String>>();
		try {
			pst = con.prepareStatement("select * from appraisal_reviewee_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getAppId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String> hmRevieweeData = new HashMap<String, String>();	
				
				hmRevieweeData.put("REVIEW_SELFID", rs.getString("reviewee_id"));
				hmRevieweeData.put("REVIEW_PEERID", rs.getString("peer_ids"));
				hmRevieweeData.put("REVIEW_MANAGERID", rs.getString("supervisor_ids"));
				hmRevieweeData.put("REVIEW_HRID", rs.getString("hr_ids"));
				hmRevieweeData.put("REVIEW_OTHERID", rs.getString("other_ids"));
				hmRevieweeData.put("REVIEW_CEOID", rs.getString("ceo_ids"));
				hmRevieweeData.put("REVIEW_HODID", rs.getString("hod_ids"));
				hmRevieweeData.put("REVIEW_SUBORDINATEID", rs.getString("subordinate_ids"));
				hmRevieweeData.put("REVIEW_GRANDSUPERVISORID", rs.getString("grand_supervisor_ids"));
				hmRevieweeData.put("REVIEW_OTHERPEERID", rs.getString("other_peer_ids"));
				
				hmRevieweewiseAppraiser.put(rs.getString("reviewee_id"), hmRevieweeData);
			}
			rs.close();
			pst.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hmRevieweewiseAppraiser;
	}


	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	private HttpServletRequest request;

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

	public String getCohortsType() {
		return cohortsType;
	}

	public void setCohortsType(String cohortsType) {
		this.cohortsType = cohortsType;
	}

}
