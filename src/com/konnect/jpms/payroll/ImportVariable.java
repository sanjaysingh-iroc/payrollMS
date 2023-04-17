package com.konnect.jpms.payroll;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import com.konnect.jpms.reports.MyProfile;
import com.konnect.jpms.salary.EmpSalaryApproval;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class ImportVariable extends ActionSupport implements ServletRequestAware,ServletResponseAware, IConstants, IStatements {
	private static final long serialVersionUID = 1L;

	File fileUpload;
	String fileUploadFileName;
	
	HttpSession session;
	String paycycle;
	CommonFunctions CF;
	String f_salaryhead;
	String f_Org;
	String strSessionEmpId;
	String status;
	String strEmpId;
	String location;
	String strDepartment;
	String strLevel;
	String strEmptype;
	String strGrade;
	String exceldownload;
	String salaryheadname;
	String strPaycycleDuration;
	String strUserType =null;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		strUserType = (String) session.getAttribute(USERTYPE);
		if(CF==null){
			return LOGIN;
		}
		UtilityFunctions uF = new UtilityFunctions();
		try {
			if(getExceldownload()!=null) {
				generatevaraibleExcel(uF);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if (getFileUpload() != null) {
			loadExcel(getFileUpload());
			return SUCCESS;
		}
		return LOAD;
		
	}

	private void generatevaraibleExcel(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {	
				
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmOtherearning = new HashMap<String, String>();
			Map<String, String> hmOtherearningId = new HashMap<String, String>();
			Map<String, String> hmOtherearningValue = new HashMap<String, String>();
			Map<String, String> hmempCode = new HashMap<String, String>();
			Map<String, String> hmempName = new HashMap<String, String>();
			String[] strPayCycleDates;			
			if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
//				strPayCycleDates = getPaycycle().split("-");
				String str = URLDecoder.decode(getPaycycle());
				strPayCycleDates = str.split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycleUsingDurationByOrg(con, CF.getStrTimeZone(), CF, getF_Org(),getStrPaycycleDuration(), request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
			}   
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive = true and joining_date<= ? ");
			if(getStrPaycycleDuration()!=null && getStrPaycycleDuration().length()>0){
				sbQuery.append(" and eod.paycycle_duration ='"+getStrPaycycleDuration()+"'");
			}			
			if (getStrEmptype() != null && getStrEmptype().length() > 0) {
				sbQuery.append(" and eod.emptype in ( '" + getStrEmptype() + "') ");
			}
			/*if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }*/
			if(getStrGrade()  !=null && getStrGrade().length()>0 && getStrLevel()!=null && getStrLevel().length()>0){
	            	sbQuery.append(" and eod.grade_id in ("+getStrGrade()+" ) ");
	            	//sbQuery.append(" and grade_id in ( " +StringUtils.join(getF_grade(), ",")+" ) and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");	
	            }else {
	            	 if(getStrLevel()!=null && getStrLevel().length()>0){
	                     sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+getStrLevel()+") ) ");
	                 }
	            	 if(getStrGrade()  !=null && getStrGrade().length()>0 ){
	                     sbQuery.append(" and eod.grade_id in ("+getStrGrade()+" ) ");
	                 }
			}
            if(getStrDepartment()!=null && getStrDepartment().length()>0){
                sbQuery.append(" and depart_id in ("+getStrDepartment()+") ");
            }
            /*if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
            } */
            if(getLocation()!=null && getLocation().length()>0){
                sbQuery.append(" and wlocation_id in ("+getLocation()+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_Org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_Org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			
//			System.out.println("pst===>" + pst); 
			rs = pst.executeQuery();
			List<List<String>> alEmpReport = new ArrayList<List<String>>();
			List<String> alEmp = new ArrayList<String>();
			
			while (rs.next()) {
				List<String> alEmpReportInner = new ArrayList<String>();
				alEmpReportInner.add(rs.getString("emp_per_id"));
			//	String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				String strEmpName = rs.getString("emp_fname") +strEmpMName+ " " +rs.getString("emp_lname");
				alEmpReportInner.add(strEmpName);				
				alEmpReportInner.add(rs.getString("empcode"));
				alEmpReport.add(alEmpReportInner);				
				if(!alEmp.contains(rs.getString("emp_per_id")) && uF.parseToInt(rs.getString("emp_per_id")) > 0){
					alEmp.add(rs.getString("emp_per_id"));		
					hmempName.put(rs.getString("emp_per_id"), strEmpName);
					hmempCode.put(rs.getString("emp_per_id"), rs.getString("empcode"));
				}				
			}
			rs.close();
			pst.close();
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Variable Head Structure");
			List<DataStyle> header = new ArrayList<DataStyle>();
			header.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Variable Amount",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Status",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();
			for (int i = 0; i < alEmp.size(); i++) {
				pst = con.prepareStatement("select * from otherearning_individual_details where emp_id = ? and pay_paycycle=? and salary_head_id=?");
				pst.setInt(1, uF.parseToInt(alEmp.get(i)));
				pst.setInt(2, uF.parseToInt(strPayCycleDates[2])-1);
				pst.setInt(3, uF.parseToInt(getF_salaryhead()));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				 List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
				    alInnerExport.add(new DataStyle(String.valueOf(i+1),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(hmempCode.get(alEmp.get(i)),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(hmempName.get(alEmp.get(i)),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));					
					if(rs.next())
					{
						if(rs.getDouble("pay_amount")>0)
						{
							alInnerExport.add(new DataStyle(rs.getString("pay_amount"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle("TRUE",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						}else{
							alInnerExport.add(new DataStyle("0",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle("TRUE",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						}												
					}else{
						alInnerExport.add(new DataStyle("0",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("False",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
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
			String strSalHeadName = getSalaryheadname().replace(" ", "_");
			response.setHeader("Content-Disposition", "attachment; filename=Variableheads_"+strSalHeadName+".xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
			//System.out.println("ReportData==>"+reportData);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}
	
	
	
	public void loadExcel(File file) throws IOException {
		Database db = new Database();
		db.setRequest(request);
		List<String> alErrorList = new ArrayList<String>();
		Connection con=null;
		con = db.makeConnection(con);
		PreparedStatement pst = null;
		ResultSet rs = null;
		ResultSet rsEm = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con.setAutoCommit(false);
			String[] strPayCycleDates = null;			
			if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
				strPayCycleDates = getPaycycle().split("-");
				String str = URLDecoder.decode(getPaycycle());
				strPayCycleDates = str.split("-");
			} else {
				//strPayCycleDates = CF.getCurrentPayCycleUsingDurationByOrg(con, CF.getStrTimeZone(), CF, getF_Org(),getStrPaycycleDuration());
				//setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
			}   
			
			pst = con.prepareStatement("select * from salary_details where salary_head_id=? limit 1");
			pst.setInt(1, uF.parseToInt(getF_salaryhead()));
			rs = pst.executeQuery();
			String sHeadType="";
			while(rs.next()){
				sHeadType=rs.getString("earning_deduction");
			}
			rs.close();
			pst.close();
			FileInputStream fis = new FileInputStream(file);
			List<List<String>> dataList = new ArrayList<List<String>>();
			HSSFWorkbook wb = new HSSFWorkbook(fis);
			HSSFSheet sheet=wb.getSheetAt(0);
			HSSFRow row; 
			HSSFCell cell;
			Iterator rows = sheet.rowIterator();
			while (rows.hasNext()) {
				row=(HSSFRow) rows.next();
				Iterator cells = row.cellIterator();
				List<String> cellList = new ArrayList<String>();
				while (cells.hasNext()) {
					cell=(HSSFCell) cells.next();
					if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
						cellList.add(cell.toString().trim());
						
					} else if(cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
						System.out.print(cell.getNumericCellValue()+"");
						cellList.add(String.valueOf(cell.getNumericCellValue()));
						
					} else if(cell.getCellType()== HSSFCell.CELL_TYPE_BOOLEAN) {
						System.out.println(cell.getBooleanCellValue());
						cellList.add(String.valueOf(cell.getBooleanCellValue()));
					}
				}
				dataList.add(cellList);
			}
//			System.out.println("dataList ===>> " + dataList);
			
			String []arrPaycycle=null;
			int nPaycycle = 0;
			if(getPaycycle()!=null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")){
				arrPaycycle = getPaycycle().split("-");
				nPaycycle = uF.parseToInt(arrPaycycle[2]);
				
				if (dataList.size() == 0) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">No Data Available in Sheet.</li>");
					flag = false;
				} else {
					for (int i = 1; i < dataList.size(); i++) {
						List<String> cellList =dataList.get(i);
						String cell1 =  cellList.get(1);
						String cell2 = cellList.get(2);
						String cell4 = cellList.get(4);
						String amount = cellList.get(3);
						System.out.println("Cell4"+cell4);
						if(cell4.equalsIgnoreCase("true")) {
						if (cell1.contains(".")) {
							cell1 = cell1.substring(0, cell1.indexOf("."));
						}
						int employee_id=0;	
						pst = con.prepareStatement("Select * from employee_personal_details where upper(empcode) = ? ");
						pst.setString(1, cell1.toUpperCase().trim());
						rsEm = pst.executeQuery();
						
						if(rsEm.next()) {
							employee_id = rsEm.getInt("emp_per_id");	
							System.out.println("present empid");
						} else {
							flag= false;
							session.setAttribute(MESSAGE, ERRORM+ "Check Employee Code" +cell1.toUpperCase().trim()+ END);
							System.out.println("no emp code");
							break;
						}
						rsEm.close();
						pst.close();
						if(employee_id == 0) {
							continue;
						}
//						System.out.println("employee_id ======>> " + employee_id);					
						pst = con.prepareStatement("insert into otherearning_individual_details (emp_id,pay_paycycle,amount,pay_amount,added_by,entry_date," +
							" paid_from,paid_to,is_approved,approved_by,approved_date,salary_head_id,earning_deduction) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
						pst.setInt(1,employee_id);
						pst.setInt(2, nPaycycle);
						pst.setDouble(3, uF.parseToDouble(amount));
						pst.setDouble(4, uF.parseToDouble(amount));
						pst.setInt(5, uF.parseToInt(strSessionEmpId));
						pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setDate(7, uF.getDateFormat(arrPaycycle[0], DATE_FORMAT));
						pst.setDate(8, uF.getDateFormat(arrPaycycle[1], DATE_FORMAT));
						pst.setInt(9, 1);
						pst.setInt(10, uF.parseToInt(strSessionEmpId));
						pst.setDate(11, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(12, uF.parseToInt(getF_salaryhead()));
						pst.setString(13, sHeadType);
//						System.out.println("pst ======>> " + pst);
						pst.execute();
						pst.close();
						
						setStatus("1");
						setStrEmpId(String.valueOf(employee_id));
//						System.out.println("getStatus() ===>> " + getStatus());
						updateEmpSalaryApproval(arrPaycycle);
						
						} else if(cell4.equalsIgnoreCase("false")) {
							int employee_id=0;	
							pst = con.prepareStatement("Select * from employee_personal_details where upper(empcode)=? ");
							pst.setString(1, cell1.toUpperCase().trim());
							rsEm = pst.executeQuery();
							
							if(rsEm.next()) {
								employee_id = rsEm.getInt("emp_per_id");	
								System.out.println("present empid");
							} else {
								flag= false;
								session.setAttribute(MESSAGE, ERRORM+ "Check Employee Code " +cell1.toUpperCase().trim()+ END);
								System.out.println("no emp code");
								break;
							}
							rsEm.close();
							pst.close();
							if(employee_id == 0) {
								continue;
							}
							pst = con.prepareStatement("delete from otherearning_individual_details where emp_id=? and pay_paycycle=? and " +
								"paid_from=? and paid_to=? and salary_head_id=?");		
							pst.setInt(1,employee_id);
							pst.setInt(2, nPaycycle);
							pst.setDate(3, uF.getDateFormat(arrPaycycle[0], DATE_FORMAT));
							pst.setDate(4, uF.getDateFormat(arrPaycycle[1], DATE_FORMAT));
							pst.setInt(5, uF.parseToInt(getF_salaryhead()));
//							System.out.println("pst======>"+pst);
							pst.execute();
							pst.close();
							setStatus("0");
							setStrEmpId(String.valueOf(employee_id));
//							System.out.println("getStatus() ===>> " + getStatus());
							updateEmpSalaryApproval(arrPaycycle);
							
						} else {
							flag=false;
							break;
						}
					}					
				}
			}
			
			if(flag) {
				con.commit();
				session.setAttribute(MESSAGE, SUCCESSM+ "Variable Imported Successfully!" + END);
				System.out.println("Sucessfully import");
			} else {
				con.rollback();
				session.setAttribute(MESSAGE,ERRORM+ "Variable not imported. Please check imported file."+ END);
				System.out.println("error  import");
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag =false;
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			session.setAttribute(MESSAGE,ERRORM+ "Variable not imported. Please check imported file."+ END);
		} finally {
			db.closeResultSet(rsEm);
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	
	
	public void updateEmpSalaryApproval(String []arrPaycycle){

		Connection con = null;
		PreparedStatement pst = null, pst1 = null, pst2 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			String[] strPayCycleDates = null;	
			if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
				strPayCycleDates = getPaycycle().split("-");
				String str = URLDecoder.decode(getPaycycle());
				strPayCycleDates = str.split("-");
			} else {
				//strPayCycleDates = CF.getCurrentPayCycleUsingDurationByOrg(con, CF.getStrTimeZone(), CF, getF_Org(),getStrPaycycleDuration());
				//setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
			} 
			con = db.makeConnection(con);
			pst = con.prepareStatement("update emp_salary_details set is_approved=true, isdisplay=?, approved_by=?, approved_date=? where emp_id=? and " +
				"effective_date = (select max(effective_date) from emp_salary_details where emp_id=? and salary_head_id=? and effective_date <?) and salary_head_id=?" );
			if(uF.parseToInt(getStatus()) == 1) {
				pst.setBoolean(1, true);
			} else {
				pst.setBoolean(1, false);
			}
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt(getStrEmpId()));
			//pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(5, uF.parseToInt(getStrEmpId()));
			pst.setInt(6, uF.parseToInt(getF_salaryhead()));
			pst.setDate(7, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(8, uF.parseToInt(getF_salaryhead()));
			System.out.println("pst ===>> " + pst);
			int x = pst.executeUpdate(); 
            pst.close();
			
			if(x > 0 && uF.parseToInt(getStatus()) == 1) {
				/**
				 * Calaculate CTC
				 * */
				Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, null, getStrEmpId());
				
				MyProfile myProfile = new MyProfile();
				myProfile.session = session;
				myProfile.request = request;
				myProfile.CF = CF;
				int intEmpIdReq = uF.parseToInt(getStrEmpId());
				myProfile.getSalaryHeadsforEmployee(con, uF, intEmpIdReq, hmEmpProfile);
				
				double grossAmount = 0.0d;
				double grossYearAmount = 0.0d;
				double deductAmount = 0.0d;
				double deductYearAmount = 0.0d;
				double netAmount = 0.0d;
				double netYearAmount = 0.0d;
				
				List<List<String>> salaryHeadDetailsList = (List<List<String>>) request.getAttribute("salaryHeadDetailsList");
				for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
					List<String> innerList = salaryHeadDetailsList.get(i);
					if(innerList.get(1).equals("E")) {
						grossAmount +=uF.parseToDouble(innerList.get(2));
						grossYearAmount +=uF.parseToDouble(innerList.get(3));
					} else if(innerList.get(1).equals("D")) {
						double dblDeductMonth = 0.0d;
						double dblDeductAnnual = 0.0d;
						if(uF.parseToInt(innerList.get(4)) == EMPLOYEE_ESI){
							dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
							dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
						} else if(uF.parseToInt(innerList.get(4)) == EMPLOYER_ESI){
							dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
							dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
						} else {
							dblDeductMonth += Math.round(uF.parseToDouble(innerList.get(2)));
							dblDeductAnnual += Math.round(uF.parseToDouble(innerList.get(3)));
						}
						deductAmount += dblDeductMonth;
						deductYearAmount += dblDeductAnnual;
					}
				}
				
				Map<String,String> hmContribution = (Map<String,String>) request.getAttribute("hmContribution");
				if(hmContribution == null) hmContribution = new HashMap<String, String>();
				double dblMonthContri = 0.0d;
				double dblAnnualContri = 0.0d;
				boolean isEPF = uF.parseToBoolean((String)request.getAttribute("isEPF"));
				boolean isESIC = uF.parseToBoolean((String)request.getAttribute("isESIC"));
				boolean isLWF = uF.parseToBoolean((String)request.getAttribute("isLWF"));
				if(isEPF || isESIC || isLWF){
					if(isEPF){
						double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
						double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
						dblMonthContri += dblEPFMonth;
						dblAnnualContri += dblEPFAnnual;
					}
					if(isESIC){
						double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
						double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
						dblMonthContri += dblESIMonth;
						dblAnnualContri += dblESIAnnual;
					}
					if(isLWF){
						double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
						double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
						dblMonthContri += dblLWFMonth;
						dblAnnualContri += dblLWFAnnual;
					}
				}
				
				double dblCTCMonthly = grossAmount + dblMonthContri;
				double dblCTCAnnualy = grossYearAmount + dblAnnualContri;
				
				List<List<String>> salaryAnnualVariableDetailsList = (List<List<String>>)request.getAttribute("salaryAnnualVariableDetailsList");
				if(salaryAnnualVariableDetailsList == null) salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
				int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
				if(nAnnualVariSize > 0){
					double grossAnnualAmount = 0.0d;
					double grossAnnualYearAmount = 0.0d;
					for(int i = 0; i < nAnnualVariSize; i++){
						List<String> innerList = salaryAnnualVariableDetailsList.get(i);
						double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
						double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
						grossAnnualAmount += dblEarnMonth;
						grossAnnualYearAmount += dblEarnAnnual;
					}
					dblCTCMonthly += grossAnnualAmount;
					dblCTCAnnualy += grossAnnualYearAmount;
				}
				
				netAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCMonthly));							 
				netYearAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCAnnualy));
	            
				EmpSalaryApproval salaryApproval = new EmpSalaryApproval();
				salaryApproval.request = request;
				salaryApproval.session = session;
				salaryApproval.CF = CF;
				Map<String, String> hmPrevCTC = salaryApproval.getPrevCTCDetails(con, uF, getStrEmpId());
				
				if(hmPrevCTC == null) hmPrevCTC = new HashMap<String, String>();
				double dblIncrementMonthAmt = netAmount - uF.parseToDouble(hmPrevCTC.get("PREV_MONTH_CTC"));
				double dblIncrementAnnualAmt = netYearAmount - uF.parseToDouble(hmPrevCTC.get("PREV_ANNUAL_CTC"));
	            
				pst = con.prepareStatement("update employee_official_details set month_ctc=?,annual_ctc=?,prev_month_ctc=?," +
						"prev_annual_ctc=?,incre_month_amount=?,incre_annual_amount=? where emp_id=?");
				pst.setDouble(1, netAmount);
				pst.setDouble(2, netYearAmount);
				pst.setDouble(3, uF.parseToDouble(hmPrevCTC.get("PREV_MONTH_CTC")));
				pst.setDouble(4, uF.parseToDouble(hmPrevCTC.get("PREV_ANNUAL_CTC")));
				pst.setDouble(5, dblIncrementMonthAmt);
				pst.setDouble(6, dblIncrementAnnualAmt);
				pst.setInt(7, uF.parseToInt(getStrEmpId()));
				pst.execute();
				pst.close();
			}   

		} catch (Exception e) {
			flag =false;
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			session.setAttribute(MESSAGE,ERRORM+ "Variable not imported. Please check imported file."+ END);
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeStatements(pst2);
			db.closeConnection(con);
		}

	}

	public String getF_salaryhead() {
		return f_salaryhead;
	}

	public void setF_salaryhead(String f_salaryhead) {
		this.f_salaryhead = f_salaryhead;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	

	
	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public File getFileUpload() {
		return fileUpload;
	}

	public void setFileUpload(File fileUpload) {
		this.fileUpload = fileUpload;
	}

	public String getFileUploadFileName() {
		return fileUploadFileName;
	}

	public void setFileUploadFileName(String fileUploadFileName) {
		this.fileUploadFileName = fileUploadFileName;
	}
	
	public String getStringValue(String str){
		
		try{
			str = String.valueOf(Double.valueOf(str).longValue());
		}catch(Exception ex){
			
		}
		return str;
	}

	public String getF_Org() {
		return f_Org;
	}

	public void setF_Org(String f_Org) {
		this.f_Org = f_Org;
	}
	
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getStrEmptype() {
		return strEmptype;
	}

	public void setStrEmptype(String strEmptype) {
		this.strEmptype = strEmptype;
	}

	public String getStrGrade() {
		return strGrade;
	}

	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}

	boolean flag = true;
	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExceldownload() {
		return exceldownload;
	}

	public void setExceldownload(String exceldownload) {
		this.exceldownload = exceldownload;
	}

	public String getSalaryheadname() {
		return salaryheadname;
	}

	public void setSalaryheadname(String salaryheadname) {
		this.salaryheadname = salaryheadname;
	}

	public String getStrPaycycleDuration() {
		return strPaycycleDuration;
	}

	public void setStrPaycycleDuration(String strPaycycleDuration) {
		this.strPaycycleDuration = strPaycycleDuration;
	}

	
}
