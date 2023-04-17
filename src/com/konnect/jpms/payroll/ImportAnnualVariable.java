package com.konnect.jpms.payroll;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.export.payroll.ExcelSheetDesign;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ImportAnnualVariable extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	String strUserType;
	String strSessionEmpId;
	
	String financialYear;
	String strOrg;
	String strLevel;
	String strSalaryHeadId;
	
	String formType;
	String callFrom;
	
	File uploadFile;
	
	String strAction = null;
	String strBaseUserType = null;
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF == null) {
			return LOGIN;
		}
		 
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);//Created By Dattatray 13-6-2022
		
		UtilityFunctions uF = new UtilityFunctions();
		//Created By Dattatray 10-06-2022
		strAction = request.getServletPath();
		if(strAction!=null) {
			strAction = strAction.replace("/","");
		}		
//		System.out.println("callFrom =======>>> " + getCallFrom());
		if (getUploadFile()!= null && !getUploadFile().equals("") && !getUploadFile().equals("NULL")) {
			importAnnualVariable(uF,getUploadFile());
			return SUCCESS;
		} else if(getFormType() !=null && getFormType().trim().equalsIgnoreCase("download")){
			generateExcel(uF);
			return null;
		}
		
		return loadImportAnnualVariable(uF);
	}

	private void importAnnualVariable(UtilityFunctions uF, File path) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		List<String> alErrorList = new ArrayList<String>();
		StringBuilder sbMessage = new StringBuilder("");
		
		try {
			
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") 
					&& !getFinancialYear().trim().equalsIgnoreCase("NULL") && !getFinancialYear().trim().equalsIgnoreCase("NULL-NULL")) {
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}
			
			con = db.makeConnection(con);
			con.setAutoCommit(false);
			
			FileInputStream fis = new FileInputStream(path);
			HSSFWorkbook workbook = new HSSFWorkbook(fis);
			fis.close();
//			System.out.println("Start Reading Excelsheet.... ");
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
			for (int k=0;k<outerList.size();k++) {
				List<String> innerList=outerList.get(k);	
				
				String empcode =innerList.get(1);
				String strAmount = innerList.get(3);
//				System.out.println("empcode==>"+empcode+"--strAmount==>"+strAmount);
				String strEmpId = null;
				if (empcode != null && !empcode.equals("") && !empcode.equalsIgnoreCase("NULL")) {
					pst = con.prepareStatement("select emp_per_id from employee_personal_details  where upper(empcode)=?");
					pst.setString(1, empcode.toUpperCase().trim());
					rs = pst.executeQuery();
					while (rs.next()) {
						strEmpId = rs.getString("emp_per_id");
					}
					rs.close();
					pst.close();
				} else {
					alErrorList.add("Please check the Employee code on Row no-"+(k+6));
					flag = false;
					break;
				}
				if(uF.parseToInt(strEmpId) == 0){
					alErrorList.add("Employee code does not exits on Row no-"+(k+6));
					flag = false;
					break;					
				}
				
				pst = con.prepareStatement("update annual_variable_details set variable_amount=?,updated_by=?,updated_date=? " +
						"where salary_head_id=? and level_id=? and org_id=? and financial_year_start=? and financial_year_end=? and emp_id=?");
				pst.setDouble(1, uF.parseToDouble(strAmount));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(4, uF.parseToInt(getStrSalaryHeadId()));
				pst.setInt(5, uF.parseToInt(getStrLevel()));
				pst.setInt(6, uF.parseToInt(getStrOrg()));
				pst.setDate(7, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(8, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(9, uF.parseToInt(strEmpId));
				int x = pst.executeUpdate();
				pst.close();
				
				if(x == 0){
					pst = con.prepareStatement("insert into annual_variable_details(salary_head_id,variable_amount,level_id,org_id," +
							"financial_year_start,financial_year_end,added_by,added_date,emp_id) values(?,?,?,?, ?,?,?,?, ?)");
					pst.setInt(1, uF.parseToInt(getStrSalaryHeadId()));
					pst.setDouble(2, uF.parseToDouble(strAmount));
					pst.setInt(3, uF.parseToInt(getStrLevel()));
					pst.setInt(4, uF.parseToInt(getStrOrg()));
					pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(7, uF.parseToInt(strSessionEmpId));
					pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(9, uF.parseToInt(strEmpId));
					pst.execute();
					pst.close();
					flag=true;
				}
				if( x > 0){
					flag=true;
				}
			}
					
			if(flag){
				con.commit();
				session.setAttribute(MESSAGE, SUCCESSM+"Annual Variable Imported Successfully!"+END);
				request.setAttribute("sbMessage", sbMessage.toString());
			} else {
				con.rollback();
				if(alErrorList.size()>0){
					sbMessage.append(alErrorList.get(alErrorList.size()-1));
				}
				session.setAttribute(MESSAGE, ERRORM+"Annual Variable not imported, "+sbMessage.toString()+END);
				request.setAttribute("sbMessage", sbMessage.toString());
			}
		
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			session.setAttribute(MESSAGE, ERRORM+"Annual Variable imported failed."+END);
			e.printStackTrace();			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void generateExcel(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") 
					&& !getFinancialYear().trim().equalsIgnoreCase("NULL") && !getFinancialYear().trim().equalsIgnoreCase("NULL-NULL")) {
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con, uF.parseToInt(getStrLevel()));
			if(hmSalaryHeadsMap == null) hmSalaryHeadsMap = new HashMap<String, String>();
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet(uF.showData(hmSalaryHeadsMap.get(getStrSalaryHeadId()), "Annual Variable"));
			
			List<DataStyle> header = new ArrayList<DataStyle>();
//			header.add(new DataStyle(uF.showData(hmSalaryHeadsMap.get(getStrSalaryHeadId()), "")+" for financial Year- "+uF.showData(strFinancialYearStart, "")+"-"+uF.showData(strFinancialYearEnd, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			header.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Amount",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
					"and is_alive = true and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd " +
					"where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id=?) and org_id=? " +
					"and eod.emp_id in (select esd.emp_id from emp_salary_details esd, (select max(effective_date) as max_date, emp_id " +
					"from emp_salary_details where isdisplay = true and is_approved=true and salary_head_id=? group by emp_id ) as b " +
					"where esd.effective_date = b.max_date and b.emp_id = esd.emp_id and isdisplay= true and is_approved=true " +
					"and esd.salary_head_id=?) order by epd.emp_fname,epd.emp_mname,epd.emp_lname");
			pst.setInt(1, uF.parseToInt(getStrLevel()));
			pst.setInt(2, uF.parseToInt(getStrOrg()));
			pst.setInt(3, uF.parseToInt(getStrSalaryHeadId()));
			pst.setInt(4, uF.parseToInt(getStrSalaryHeadId()));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alEmp = new ArrayList<Map<String,String>>();
			List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();
			int cnt = 0;
			while (rs.next()) {
				cnt++;
				
				//String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				String strEmpName = rs.getString("emp_fname") +strEmpMName+ " " +rs.getString("emp_lname");
		   		
		   		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle(uF.showData(""+cnt, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(rs.getString("empcode"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
				alInnerExport.add(new DataStyle(uF.showData(strEmpName, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("0",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				reportData.add(alInnerExport);
			}
			rs.close();
			pst.close();
			
			ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
//			sheetDesign.getExcelSheetDesignData(workbook, sheet, header, reportData);
			sheetDesign.generateDefualtExcelSheet(workbook, sheet, header, reportData);

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=ImportAnnualVariable.xls");
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

	public String loadImportAnnualVariable(UtilityFunctions uF) {
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

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getStrSalaryHeadId() {
		return strSalaryHeadId;
	}

	public void setStrSalaryHeadId(String strSalaryHeadId) {
		this.strSalaryHeadId = strSalaryHeadId;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public File getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(File uploadFile) {
		this.uploadFile = uploadFile;
	}

	public String getCallFrom() {
		return callFrom;
	}

	public void setCallFrom(String callFrom) {
		this.callFrom = callFrom;
	}
	
}