package com.konnect.jpms.salary;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.export.payroll.ExcelSheetDesign;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ImportSalaryStructure extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strUsertypeId;
	String level;
	String strOrg;
	String operation;
    List<FillLevel> levelList;

    File uploadFile;
    
    String[] strGrade;	
	List<FillGrade> gradeList;    
	
	private String downloadFile;
	private String submit;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute("salaryStructure", CF.getStrSalaryStructure());
		
		int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
		if(nSalaryStrucuterType == S_GRADE_WISE){
			if (uploadFile!= null && getSubmit() != null && !getSubmit().trim().equals("") && !getSubmit().trim().equalsIgnoreCase("")) {
				importSalaryStructureByGrade(uF,uploadFile);
				return SUCCESS;
			} else if(getStrGrade() != null && getStrGrade().length > 0 && getDownloadFile() != null && !getDownloadFile().trim().equals("") && !getDownloadFile().trim().equalsIgnoreCase("")){			
				generateSalaryExcelByGrade(uF);
				return null; 
			}
		} else {
			if (uploadFile!= null && getSubmit() != null && !getSubmit().trim().equals("") && !getSubmit().trim().equalsIgnoreCase("")) {
				importSalaryStructure(uF,uploadFile);
				return SUCCESS;
			} else if(uF.parseToInt(getLevel()) > 0 && getDownloadFile() != null && !getDownloadFile().trim().equals("") && !getDownloadFile().trim().equalsIgnoreCase("")){			
				generateSalaryExcel(uF,getLevel());
				return null; 
			}
		}
		
		return loadValidateLevel(uF);
	}
	
	private void importSalaryStructureByGrade(UtilityFunctions uF, File path) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		List<String> alErrorList = new ArrayList<String>();
		StringBuilder sbMessage = new StringBuilder("");
		
		try {
			con = db.makeConnection(con);
			con.setAutoCommit(false);
			
			List<String> headIdList= new ArrayList<String>();
			Map<String, String> hmSalaryHeadsMap = new LinkedHashMap<String, String>();
			Map<String, String> hmSalaryHeadsEarningDeduction = new LinkedHashMap<String, String>();
			pst = con.prepareStatement("select * from salary_details where grade_id = ? and (is_delete is null or is_delete=false) order by  earning_deduction desc, weight");
			pst.setInt(1, uF.parseToInt(getStrGrade()[0]));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				hmSalaryHeadsMap.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
				hmSalaryHeadsEarningDeduction.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = hmSalaryHeadsMap.keySet().iterator();
		   	while(it.hasNext()){
		   		String strSalaryHeadId = it.next();
		   		headIdList.add(strSalaryHeadId);
		   	}
			
			FileInputStream fis = new FileInputStream(path);
			HSSFWorkbook workbook = new HSSFWorkbook(fis);
			fis.close();
			System.out.println("Start Reading Excelsheet.... ");
			HSSFSheet attendanceSheet = workbook.getSheetAt(0);

			List<List<String>> outerList=new ArrayList<List<String>>();

			Iterator rows = attendanceSheet.rowIterator();    
			int l=0;
			while (rows.hasNext()) {

				HSSFRow row = (HSSFRow) rows.next();
				Iterator cells = row.cellIterator();
				
				if(l>0){
					List<String> cellList = new ArrayList<String>();
					while (cells.hasNext()) {
						cellList.add(cells.next().toString());
					}
					outerList.add(cellList);
				}
				l++;
			}
			boolean flag = false;
//			System.out.println("outerList.size()====>"+outerList.size());
			for (int k=0;k<outerList.size();k++) {
				List<String> innerList=outerList.get(k);	
				
				String empcode = null;
				String effictiveDate = null;
				if(innerList != null && innerList.size() >0){
					empcode=innerList.get(1);
					effictiveDate=innerList.get(3);
				}
				
				// emp_per_id from emp name
			
				String empPerID = "";
//				System.out.println("empcode====>"+empcode);
				if (empcode != null && !empcode.equals("")) {	
					
					if (empcode.contains(".")) {
						empcode = empcode.substring(0, empcode.indexOf("."));
					}
					
					pst = con.prepareStatement("select emp_per_id from employee_personal_details  where trim(upper(empcode))=?");
					pst.setString(1, empcode.toUpperCase().trim());
//					System.out.println("pst====>"+pst);
					rs = pst.executeQuery();					
					while (rs.next()) {
						empPerID = rs.getString("emp_per_id");
					}
					rs.close();
					pst.close();
					
				} else {					
					alErrorList.add("Please check the Employee code '"+empcode+"' on Row no-"+(k+1));
					flag = false;
					break;
				}
//				System.out.println("empPerID====>"+empPerID);
				if( uF.parseToInt(empPerID)==0){
					alErrorList.add("Employee code '"+empcode+"' does not exist on Row no-"+(k+1));
					flag = false;
					break;					
				}
				
				String strEmpGradeId = CF.getEmpGradeId(con, empPerID);
				
				pst = con.prepareStatement("select * from emp_salary_details where effective_date = ? and emp_id = ?");
				pst.setDate(1, uF.getDateFormat(effictiveDate, DATE_FORMAT));
				pst.setInt(2, uF.parseToInt(empPerID));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				boolean isDateExist = false;
				while(rs.next()){
					isDateExist = true;
				}
				rs.close();
				pst.close();
				
				
				if(isDateExist){
					pst = con.prepareStatement("delete from emp_salary_details where effective_date = ? and emp_id = ?");
					pst.setDate(1, uF.getDateFormat(effictiveDate, DATE_FORMAT));
					pst.setInt(2, uF.parseToInt(empPerID));
					pst.execute();
					pst.close();
				}  
				
				int i = 3;
				int z = 4;
				pst = con.prepareStatement("insert into emp_salary_details (emp_id,salary_head_id, amount, entry_date, user_id, pay_type, " +
						"isdisplay, service_id, effective_date, earning_deduction, salary_type,is_approved,approved_by,approved_date,grade_id) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
				for (int j=0;j<headIdList.size() && !headIdList.isEmpty() && headIdList.size()>0;j++){
					i++;
					z++;
					
					pst.setInt(1, uF.parseToInt(empPerID));
					pst.setInt(2, uF.parseToInt(headIdList.get(j)));
					pst.setDouble(3, uF.parseToDouble(innerList.get(i)));
					pst.setDate	(4, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(5, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setString(6, "M");
					pst.setBoolean(7, uF.parseToBoolean(innerList.get(z)));
					pst.setInt(8, 0);
					pst.setDate	(9, uF.getDateFormat(effictiveDate, DATE_FORMAT));
					pst.setString(10, hmSalaryHeadsEarningDeduction.get(headIdList.get(j)));
					pst.setString(11, "M");
					pst.setBoolean(12, true);
					pst.setInt(13, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setDate	(14, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(15, uF.parseToInt(strEmpGradeId));
//					System.out.println("pst------>"+pst); 
					pst.addBatch();
					
					flag=true;
					i++;
					z++;
				}
				
				CF.updateNextEmpSalaryEffectiveDate(con, uF, uF.parseToInt(empPerID), effictiveDate, DATE_FORMAT);
				
				if(flag){
					pst.executeBatch();
					pst.clearBatch();
				}
				pst.close();
				 
								
			}
					
			if(flag){
				con.commit();
				session.setAttribute(MESSAGE, SUCCESSM+"Salary Structure Imported Successfully!"+END);
//				sbMessage.append("</ul>");
				request.setAttribute("sbMessage", sbMessage.toString());
			} else {
				con.rollback();
				if(alErrorList.size()>0){
					sbMessage.append(alErrorList.get(alErrorList.size()-1));
				}
				session.setAttribute(MESSAGE, ERRORM+"Salary Structure not imported, "+sbMessage.toString()+END);
//				sbMessage.append("</ul>");
				request.setAttribute("sbMessage", sbMessage.toString());
			}
		
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			session.setAttribute(MESSAGE, ERRORM+"Salary Structure imported failed."+END);
			e.printStackTrace();			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void generateSalaryExcelByGrade(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			if(getStrGrade() != null && getStrGrade().length > 0){
				String strGradeIds = StringUtils.join(getStrGrade(),",");
				List<String> alGradesId = Arrays.asList(getStrGrade()[0].split(","));
//				System.out.println(alGradesId.get(0)+"----strGradeIds----->"+strGradeIds); 
				
				List<String> alEmpGradesId = new ArrayList<String>();
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select eod.emp_id,epd.empcode,epd.emp_fname,epd.emp_mname,epd.emp_lname,eod.grade_id from employee_personal_details epd, employee_official_details eod " +
						"where epd.emp_per_id = eod.emp_id and is_alive = true and grade_id in (select gd.grade_id from grades_details gd, level_details ld, " +
						"designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and gd.grade_id in ("+strGradeIds+")) " +
						"and eod.org_id=? order by epd.emp_fname,epd.emp_mname,epd.emp_lname");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getStrOrg()));
//				System.out.println("pst----->"+pst);
				rs = pst.executeQuery();
				Map<String, String> hmEmp = new HashMap<String, String>();
				Map<String, String> hmEmpCode = new HashMap<String, String>();
				Map<String, String> hmEmpGrade = new HashMap<String, String>();
				while(rs.next()){
				
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
				
					
					hmEmp.put(rs.getString("emp_id"), rs.getString("emp_fname") + strEmpMName+" " + uF.showData(rs.getString("emp_mname"), "") + " " + rs.getString("emp_lname"));
					hmEmpCode.put(rs.getString("emp_id"), rs.getString("empcode"));
					hmEmpGrade.put(rs.getString("emp_id"), rs.getString("grade_id"));
					
					if(!alEmpGradesId.contains(rs.getString("grade_id")) && uF.parseToInt(rs.getString("grade_id")) > 0){
						alEmpGradesId.add(rs.getString("grade_id"));
					}
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmSalaryHeadsMap = new LinkedHashMap<String, String>();
				Map<String, String> hmSalaryHeadsValue = new LinkedHashMap<String, String>();
				pst = con.prepareStatement("select * from salary_details where grade_id = ? and (is_delete is null or is_delete=false) order by earning_deduction desc, weight");
				pst.setInt(1, uF.parseToInt(alGradesId.get(0).trim()));
//				System.out.println("pst----->"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					hmSalaryHeadsMap.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
					if(rs.getString("salary_head_amount_type").equalsIgnoreCase("A")){
						hmSalaryHeadsValue.put(rs.getString("salary_head_id"), rs.getString("salary_head_amount"));
						
					}else{
						hmSalaryHeadsValue.put(rs.getString("salary_head_id"), "0");
					}
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmBasicFitment = new HashMap<String, String>();
				if(alEmpGradesId != null && alEmpGradesId.size() > 0){
					String strEmpGradeIds = StringUtils.join(alEmpGradesId.toArray(),",");
					pst = con.prepareStatement("select * from basic_fitment_details where grade_id in ("+strEmpGradeIds+") and trail_status=1");
//					System.out.println("pst==>"+pst);
					rs = pst.executeQuery();				
					while (rs.next()) {
						hmBasicFitment.put(rs.getString("grade_id"), rs.getString("AMOUNT"));
					}
					rs.close();
					pst.close();
				}
				
				HSSFWorkbook workbook = new HSSFWorkbook();
				HSSFSheet sheet = workbook.createSheet("Salary Structure");
				
				List<DataStyle> header = new ArrayList<DataStyle>();
	//			header.add(new DataStyle("Salary Structure ",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				header.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				header.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				header.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				header.add(new DataStyle("Effective Date",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
			   	Iterator<String> it = hmSalaryHeadsMap.keySet().iterator();
			   	while(it.hasNext()){
			   		String strSalaryHeadId = it.next();
			   		String strSalaryHeadName = hmSalaryHeadsMap.get(strSalaryHeadId);
			   		
			   		header.add(new DataStyle(uF.showData(strSalaryHeadName, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			   		header.add(new DataStyle(uF.showData("isDisplay", ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			   	}
				
			   	List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();
			   	Iterator<String> it1 = hmEmp.keySet().iterator();
			   	int cnt = 0;
			   	while(it1.hasNext()){
			   		String strEmpId = it1.next();
			   		String strEmpName = hmEmp.get(strEmpId);
			   		cnt++;
			   		
			   		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(uF.showData(""+cnt, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(hmEmpCode.get(strEmpId), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
					alInnerExport.add(new DataStyle(uF.showData(strEmpName, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					
					String strEmpGrade = hmEmpGrade.get(strEmpId);
					
					Iterator<String> it2 = hmSalaryHeadsValue.keySet().iterator();
				   	while(it2.hasNext()){
				   		String strSalaryHeadId = it2.next();
				   		String strSalaryHeadValue = hmSalaryHeadsValue.get(strSalaryHeadId);
				   	//	String strIsDisplay = hmSalaryIsdisplay.get(strSalaryHeadId);
				   		
				   		if(uF.parseToInt(strSalaryHeadId) == BASIC){
				   			strSalaryHeadValue = ""+uF.parseToDouble(hmBasicFitment.get(strEmpGrade));
				   		}
				   		
				   		alInnerExport.add(new DataStyle(uF.showData(strSalaryHeadValue, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				   		alInnerExport.add(new DataStyle(uF.showData("true", ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				   	}
				   	reportData.add(alInnerExport);
			   		
			   	}
				
				ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
	//			sheetDesign.getExcelSheetDesignData(workbook, sheet, header, reportData);
				sheetDesign.generateDefualtExcelSheet(workbook, sheet, header, reportData);
				
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				workbook.write(buffer);
				response.setContentType("application/vnd.ms-excel:UTF-8");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=SalaryStructure.xls");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush();
				buffer.close();
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);			
			db.closeConnection(con);
		}
	}

	private void importSalaryStructure(UtilityFunctions uF, File path) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		List<String> alErrorList = new ArrayList<String>();
		StringBuilder sbMessage = new StringBuilder("");
		
		try {
			con = db.makeConnection(con);
			con.setAutoCommit(false);
			
			List<String> headIdList= new ArrayList<String>();
			Map<String, String> hmSalaryHeadsMap = new LinkedHashMap<String, String>();
			Map<String, String> hmSalaryHeadsEarningDeduction = new LinkedHashMap<String, String>();
			pst = con.prepareStatement("select * from salary_details where level_id = ? and (is_delete is null or is_delete=false) order by  earning_deduction desc, weight");
			pst.setInt(1, uF.parseToInt(getLevel()));
			rs = pst.executeQuery();
			while(rs.next()){
				hmSalaryHeadsMap.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
				hmSalaryHeadsEarningDeduction.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmEmpId = new HashMap<String, String>();
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("Select upper(empcode) as empcode,emp_per_id, emp_fname,emp_mname,emp_lname,usertype_id from employee_personal_details ep, employee_official_details ef, " +
				" user_details ud where ef.emp_id=ep.emp_per_id and ep.emp_per_id = ud.emp_id and ep.is_alive=true ");
			pst=con.prepareStatement(sbQuery.toString());
			//System.out.println(" pst 1 for loading all detail==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname").trim();
					}
				}
				hmEmpId.put(rs.getString("emp_fname").toUpperCase().trim() +strEmpMName.toUpperCase()+" "+rs.getString("emp_lname").toUpperCase().trim(), rs.getString("emp_per_id"));				
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpId ===>> " + hmEmpId);
			
			
			Iterator<String> it = hmSalaryHeadsMap.keySet().iterator();
		   	while(it.hasNext()){
		   		String strSalaryHeadId = it.next();
		   		headIdList.add(strSalaryHeadId);
		   	}
			
			FileInputStream fis = new FileInputStream(path);
			HSSFWorkbook workbook = new HSSFWorkbook(fis);
			String dateFormat = "dd/MM/yyyy";
			String timeFormat = "HH:mm:ss";
//			fis.close();
//			System.out.println("Start Reading Excelsheet.... ");
			HSSFSheet attendanceSheet = workbook.getSheetAt(0);
			HSSFRow row; 
			HSSFCell cell;
			List<List<String>> outerList=new ArrayList<List<String>>();

			Iterator rows = attendanceSheet.rowIterator();    
			int l=0;
			while (rows.hasNext()) {
				row = (HSSFRow) rows.next();
				Iterator cells = row.cellIterator();
				if(l>0){
					List<String> cellList = new ArrayList<String>();
					while (cells.hasNext()) {
						cell = (HSSFCell) cells.next();
						cellList.add(uF.getCellString(cell, workbook, dateFormat, timeFormat));
						cellList.add(cells.next().toString());
					}
					outerList.add(cellList);
				}
				l++;
			}
			boolean flag = false;
//			System.out.println("outerList.size()====>"+outerList.size());
			for (int k=0;k<outerList.size();k++) {
				List<String> innerList=outerList.get(k);
//				System.out.println("innerList.size()====>"+innerList.size());
//				System.out.println("headIdList.size()====>"+headIdList.size());
				
				String empcode = null;
				String empName = null;
				String effictiveDate = null;
				if(innerList != null && innerList.size() >0){
					empcode=innerList.get(1);
					empName=innerList.get(2);
					effictiveDate=innerList.get(3);
				}
//				System.out.println("empName ===>> " + empName);
				// emp_per_id from emp name
			
				String empPerID = "";
//				System.out.println("empcode====>"+empcode);
				if (empcode != null && !empcode.trim().equals("")) {
					
					  if (empcode.contains(".")) { empcode =
					  empcode.trim().substring(0, empcode.indexOf(".")); }
					 
					
					pst = con.prepareStatement("select emp_per_id from employee_personal_details  where trim(upper(empcode))=? and is_alive=true");
					pst.setString(1, empcode.toUpperCase().trim());
//					System.out.println("pst====>"+pst);
					rs = pst.executeQuery();					
					while (rs.next()) {
						empPerID = rs.getString("emp_per_id");
					}
					rs.close();
					pst.close();
					
				} else if (uF.parseToInt(hmEmpId.get(empName.trim().toUpperCase()))>0) {
					
					empPerID = hmEmpId.get(empName.trim().toUpperCase());
//					System.out.println("else if empPerID ===>> " + empPerID);
					
				} else {
					alErrorList.add("Please check the Employee code or name '("+empcode+"/ "+empName+")' on Row no-"+(k+1));
					flag = false;
					break;
				}
//				System.out.println("empPerID====>"+empPerID);
				if( uF.parseToInt(empPerID)==0){
					alErrorList.add("Employee code or name '("+empcode+"/ "+empName+")' does not exist on Row no-"+(k+1));
					flag = false;
					break;
				}
				
				pst = con.prepareStatement("select * from emp_salary_details where effective_date=? and emp_id=?");
				pst.setDate(1, uF.getDateFormat(effictiveDate, DATE_FORMAT));
				pst.setInt(2, uF.parseToInt(empPerID));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				boolean isDateExist = false;
				while(rs.next()){
					isDateExist = true;
				}
				rs.close();
				pst.close();
				
				
				if(isDateExist){
					pst = con.prepareStatement("delete from emp_salary_details where effective_date = ? and emp_id = ?");
					pst.setDate(1, uF.getDateFormat(effictiveDate, DATE_FORMAT));
					pst.setInt(2, uF.parseToInt(empPerID));
					pst.execute();
					pst.close();
				}  
				
				int i = 3;
				int z = 4;
				for (int j=0;j<headIdList.size() && !headIdList.isEmpty() && headIdList.size()>0;j++){
					i++;
					z++;
//					System.out.println(z+"---headIdList=="+hmSalaryHeadsMap.get(headIdList.get(j)));
//					System.out.println(i+"---innerList=="+innerList.get(25));
				
					pst = con.prepareStatement("insert into emp_salary_details (emp_id,salary_head_id, amount, entry_date, user_id, pay_type, " +
							"isdisplay, service_id, effective_date, earning_deduction, salary_type,is_approved,approved_by,approved_date,level_id) " +
							"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
					pst.setInt(1, uF.parseToInt(empPerID));
					pst.setInt(2, uF.parseToInt(headIdList.get(j)));
					pst.setDouble(3, uF.parseToDouble(innerList.get(i)));
					pst.setDate	(4, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(5, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setString(6, "M");
					pst.setBoolean(7, uF.parseToBoolean(innerList.get(z)));
					pst.setInt(8, 0);
					pst.setDate	(9, uF.getDateFormat(effictiveDate, DATE_FORMAT));
					pst.setString(10, hmSalaryHeadsEarningDeduction.get(headIdList.get(j)));
					pst.setString(11, "M");
					pst.setBoolean(12, true);
					pst.setInt(13, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setDate	(14, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(15, uF.parseToInt(getLevel()));
					pst.execute();
//					System.out.println("pst------>"+pst);
					pst.close();
					flag=true;
					i++;
					z++;
				}
				 
				CF.updateNextEmpSalaryEffectiveDate(con, uF, uF.parseToInt(empPerID), effictiveDate, DATE_FORMAT);				
			}
					
			if(flag){
				con.commit();
				session.setAttribute(MESSAGE, SUCCESSM+"Salary Structure Imported Successfully!"+END);
//				sbMessage.append("</ul>");
				request.setAttribute("sbMessage", sbMessage.toString());
			} else {
				con.rollback();
				if(alErrorList.size()>0){
					sbMessage.append(alErrorList.get(alErrorList.size()-1));
				}
				session.setAttribute(MESSAGE, ERRORM+"Salary Structure not imported, "+sbMessage.toString()+END);
//				sbMessage.append("</ul>");
				request.setAttribute("sbMessage", sbMessage.toString());
			}
		
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			session.setAttribute(MESSAGE, ERRORM+"Salary Structure imported failed."+END);
			e.printStackTrace();			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void generateSalaryExcel(UtilityFunctions uF, String strLevel) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
					"and is_alive = true and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd " +
					"where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id=?) and eod.org_id=? " +
					"order by epd.emp_fname,epd.emp_mname,epd.emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strLevel));
			pst.setInt(2, uF.parseToInt(getStrOrg()));
//			System.out.println("pst----->"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmEmp = new HashMap<String, String>();
			Map<String, String> hmEmpCode = new HashMap<String, String>();
			while(rs.next()){
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				hmEmp.put(rs.getString("emp_id"), rs.getString("emp_fname") + strEmpMName+" " + uF.showData(rs.getString("emp_mname"), "") + " " + rs.getString("emp_lname"));
				hmEmpCode.put(rs.getString("emp_id"), rs.getString("empcode"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmSalaryHeadsMap = new LinkedHashMap<String, String>();
			Map<String, String> hmSalaryHeadsValue = new LinkedHashMap<String, String>();
			pst = con.prepareStatement("select * from salary_details where level_id = ? and (is_delete is null or is_delete=false) order by earning_deduction desc, weight");
			pst.setInt(1, uF.parseToInt(strLevel));
			rs = pst.executeQuery();
			while(rs.next()){
				hmSalaryHeadsMap.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
				if(rs.getString("salary_head_amount_type").equalsIgnoreCase("A")){
					hmSalaryHeadsValue.put(rs.getString("salary_head_id"), rs.getString("salary_head_amount"));
					
				}else{
					hmSalaryHeadsValue.put(rs.getString("salary_head_id"), "0");
				}
			}
			rs.close();
			pst.close();
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Salary Structure");
			
			List<DataStyle> header = new ArrayList<DataStyle>();
//			header.add(new DataStyle("Salary Structure ",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			header.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Effective Date",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
		   	Iterator<String> it = hmSalaryHeadsMap.keySet().iterator();
		   	while(it.hasNext()){
		   		String strSalaryHeadId = it.next();
		   		String strSalaryHeadName = hmSalaryHeadsMap.get(strSalaryHeadId);
		   		
		   		header.add(new DataStyle(uF.showData(strSalaryHeadName, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   		header.add(new DataStyle(uF.showData("isDisplay", ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	}
			
		   	List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();
		   	Iterator<String> it1 = hmEmp.keySet().iterator();
		   	int cnt = 0;
		   	while(it1.hasNext()){
		   		String strEmpId = it1.next();
		   		String strEmpName = hmEmp.get(strEmpId);
		   		cnt++;
		   		
		   		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle(uF.showData(""+cnt, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(hmEmpCode.get(strEmpId), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
				alInnerExport.add(new DataStyle(uF.showData(strEmpName, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				Iterator<String> it2 = hmSalaryHeadsValue.keySet().iterator();
			   	while(it2.hasNext()){
			   		String strSalaryHeadId = it2.next();
			   		String strSalaryHeadValue = hmSalaryHeadsValue.get(strSalaryHeadId);
			   	//	String strIsDisplay = hmSalaryIsdisplay.get(strSalaryHeadId);
			   		
			   		alInnerExport.add(new DataStyle(uF.showData(strSalaryHeadValue, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			   		alInnerExport.add(new DataStyle(uF.showData("true", ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			   	}
			   	reportData.add(alInnerExport);
		   		
		   	}
			
			ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
//			sheetDesign.getExcelSheetDesignData(workbook, sheet, header, reportData);
			sheetDesign.generateDefualtExcelSheet(workbook, sheet, header, reportData);
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=SalaryStructure.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);			
			db.closeConnection(con);
		}
	}

	public String loadValidateLevel(UtilityFunctions uF) {
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrg()));
		gradeList = new ArrayList<FillGrade>();
		return LOAD;
	}
	
	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
		
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}


	public String getStrOrg() {
		return strOrg;
	}


	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}


	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}


	public String getOperation() {
		return operation;
	}


	public void setOperation(String operation) {
		this.operation = operation;
	}

	public File getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(File uploadFile) {
		this.uploadFile = uploadFile;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String[] getStrGrade() {
		return strGrade;
	}

	public void setStrGrade(String[] strGrade) {
		this.strGrade = strGrade;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public String getDownloadFile() {
		return downloadFile;
	}

	public void setDownloadFile(String downloadFile) {
		this.downloadFile = downloadFile;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}
	
}