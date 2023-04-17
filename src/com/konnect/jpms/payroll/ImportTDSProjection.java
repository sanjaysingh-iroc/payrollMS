package com.konnect.jpms.payroll;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class ImportTDSProjection extends ActionSupport implements ServletRequestAware, ServletResponseAware, IConstants, IStatements {
	private static final long serialVersionUID = 1L;

	File fileUpload;
	String fileUploadFileName;
	
	String strgrade,strLevel,strdepartment,strLocation,strorg,strpaymentmode,exceldownload;
	String strUserType=null;
	HttpSession session;
	CommonFunctions CF;
	//String strSessionEmpId;
	String financialYear;
	boolean flag = true;
	public String execute() throws Exception {
		session = request.getSession();
		UtilityFunctions uF = new UtilityFunctions();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		strUserType = (String)session.getAttribute(USERTYPE);
		if(CF==null) {
			return LOGIN;
		}
		
		if(getExceldownload()!=null && !getExceldownload().equalsIgnoreCase("null")) {
			//System.out.println("getExceldownload");
			if(getExceldownload().equalsIgnoreCase("true")) {
			//	System.out.println("getExceldownload1");
				genratedexcel(uF,null);
			}
		}
		
		if (getFileUpload() != null) {
			loadExcel(getFileUpload());
			return SUCCESS;
		}
		return LOAD;
	}

	public void loadExcel(File file) throws IOException {
		Database db = new Database();
		db.setRequest(request);
		
		Connection con=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		ResultSet rsEm = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			String[] strFinancialYearDates = null;

			if (getFinancialYear() != null) {
				strFinancialYearDates = getFinancialYear().split("-");
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
			}
			
//			System.out.println("loadExcel ===>");
//			System.out.println("Cal===>"+getFinancialYear());
			FileInputStream fis = new FileInputStream(file);
			List<List> dataList = new ArrayList<List>();
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
						cellList.add(String.valueOf(cell.getNumericCellValue()));
						
					} else if(cell.getCellType()== HSSFCell.CELL_TYPE_BOOLEAN) {
						cellList.add(String.valueOf(cell.getBooleanCellValue()));
					}
				}
				dataList.add(cellList);
			}
			
			con = db.makeConnection(con);
			con.setAutoCommit(false);
			if (dataList.size() == 0) {
				flag = false;
			} else {
			for (int i = 1; i < dataList.size(); i++) {
				List cellList =dataList.get(i);
				int nmonth=0;
				String employeeCode =  (String) cellList.get(1);
				String amount = (String) cellList.get(3);
				String month = (String) cellList.get(4);
				nmonth = uF.getShortMonthInNumber(month);
//				System.out.println("nmonth ===>> " + nmonth );
				 if(nmonth>0 && nmonth<13) {
				if (employeeCode.contains(".")) {
					employeeCode = employeeCode.substring(0, employeeCode.indexOf("."));
				}
				int employee_id=0;
				pst = con.prepareStatement("Select * from employee_personal_details where upper(empcode) = ? ");
				pst.setString(1, employeeCode.toUpperCase().trim());
				rsEm = pst.executeQuery();
				if(rsEm.next()) {
					employee_id = rsEm.getInt("emp_per_id");	
				} else {
					flag= false;
					session.setAttribute(MESSAGE, ERRORM+ "Check Employee Code" +employeeCode.toUpperCase().trim()+ END);
					break;
				}
				rsEm.close();
				pst.close();
				if(employee_id == 0) {
					continue;
				}
//				System.out.println("employee_id ===>> " + employee_id );
				updateTDSPayroll(uF,nmonth,employee_id,amount);
			} else {
				flag= false;
				session.setAttribute(MESSAGE, ERRORM+ "TDS Projection imported not imported Please Check the Month Column of sheet" + END);
			}
			if(flag) {
				con.commit();
				session.setAttribute(MESSAGE, SUCCESSM+ "TDS Projection imported Successfully!" + END);
				//System.out.println("Sucessfully import");
			}else{
				con.rollback();
				//session1.setAttribute(MESSAGE,ERRORM+ "TDS Projection imported not imported. Please check imported file."+ END);
				//System.out.println("error  import");
			}
		  }
		}
			
		}catch (Exception e) {
			e.printStackTrace();
			flag =false;
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				session.setAttribute(MESSAGE,ERRORM+ "TDS Projection imported not imported. Please check imported file."+ END);
			}
			session.setAttribute(MESSAGE,ERRORM+ "TDS Projection imported not imported. Please check imported file."+ END);
		} finally {
			db.closeResultSet(rsEm);
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	
public void updateTDSPayroll(UtilityFunctions uF,int month, int empId , String amount) {
		
		Connection con = null;
		PreparedStatement pst=null;		
		Database db = new Database();
		db.setRequest(request);
		
		try {
			String[] strFinancialYearDates = null;

			if (getFinancialYear() != null) {
				strFinancialYearDates = getFinancialYear().split("-");
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
			}
//			System.out.println("updateTDSPayroll getFinancialYear() ===>> " + getFinancialYear());
			con = db.makeConnection(con);
				pst = con.prepareStatement(updateTDSProjection);
				pst.setDouble(1, uF.parseToDouble(amount));
				pst.setInt(2,(Double.valueOf(month)).intValue());
				pst.setInt(3, empId);
				pst.setDate(4, uF.getDateFormat(strFinancialYearDates[0], DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearDates[1], DATE_FORMAT));
				System.out.println("ITDSP/226--update pst ===>> " + pst);
				int x = pst.executeUpdate();
				pst.close();
				if(x==0) {
					pst = con.prepareStatement(insertTDSProjection);
					pst.setDouble(1, uF.parseToDouble(amount));
					pst.setInt(2,(Double.valueOf(month)).intValue());
					pst.setInt(3, empId);
					pst.setInt(4, TDS);
					pst.setDate(5, uF.getDateFormat(strFinancialYearDates[0], DATE_FORMAT));
					pst.setDate(6, uF.getDateFormat(strFinancialYearDates[1], DATE_FORMAT));
					System.out.println("ITDSP/226--pst ===>> " + pst);
					pst.execute();
					pst.close();
				}
				request.setAttribute("STATUS_MSG", "Updated");
			
		} catch (Exception e) {
			e.printStackTrace();
			
			flag =false;
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				session.setAttribute(MESSAGE,ERRORM+ "TDS Projection imported not imported. Please check imported file."+ END);
			}
			session.setAttribute(MESSAGE,ERRORM+ "TDS Projection imported not imported. Please check imported file."+ END);
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void genratedexcel(UtilityFunctions uF,String empId)
	{
		System.out.println("in generatedsxcel file");
		Connection con = null;
		PreparedStatement pst=null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
				setFinancialYear(getFinancialYear());
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}
			request.setAttribute("strD1", strFinancialYearDates[0]);
			request.setAttribute("strD2", strFinancialYearDates[1]);
			
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpMap = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmEmpCodeMap = CF.getEmpCodeMap(con);
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM"))-1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			
			List<String> alMonth = new ArrayList<String>();
			for(int i=0; i<12;i++) {
				String strDate = cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR);
				alMonth.add(uF.getDateFormat(strDate, DATE_FORMAT, "MM"));
				cal.add(Calendar.MONTH, 1);
			}					
			List<String> alEmp = new ArrayList<String>();
			Map<String, String> hmTDSEmp = new HashMap<String, String>();
			if(empId!=null) {
				pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and emp_per_id=?");
				pst.setInt(1,uF.parseToInt(empId));

			}else{
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id");
				/*if(getF_level()!=null && getF_level().length>0) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	            }*/
				  if(getStrgrade()!=null && getStrgrade().length()>0 && getStrLevel()!=null && getStrLevel().length()>0)
		            {
		            	sbQuery.append(" and grade_id in ("+getStrgrade()+" ) ");
		            	//sbQuery.append(" and grade_id in ( " +StringUtils.join(getF_grade(), ",")+" ) and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");	
		            }else {
		            	 if(getStrLevel()!=null && getStrLevel().length()>0) {
		                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+getStrLevel()+") ) ");
		                 }
		            	 if(getStrgrade()!=null && getStrgrade().length()>0) {
		                     sbQuery.append(" and grade_id in ("+getStrgrade()+" ) ");
		                 }
					}	
	            if(getStrdepartment()!=null && getStrdepartment().length()>0) {
	                sbQuery.append(" and depart_id in ("+getStrdepartment()+") ");
	            }
	            
	           /* if(getF_service()!=null && getF_service().length>0) {
	                sbQuery.append(" and (");
	                for(int i=0; i<getF_service().length; i++) {
	                    sbQuery.append(" eod.service_id like '%"+getF_service()[i]+",%'");
	                    
	                    if(i<getF_service().length-1) {
	                        sbQuery.append(" OR "); 
	                    }
	                }
	                sbQuery.append(" ) ");
	            } */
	            
	            if(getStrLocation()!=null && getStrLocation().length()>0) {
	                sbQuery.append(" and wlocation_id in ("+getStrLocation()+") ");
	            }else if(getStrUserType()!=null && !getStrUserType().equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            
	            if(uF.parseToInt(getStrorg())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getStrorg()));
				}else if(getStrUserType()!=null && !getStrUserType().equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and org_id in ("+session.getAttribute(ORG_ACCESS)+")");
				}
				
				if(uF.parseToInt(getStrpaymentmode())>0) {
					sbQuery.append(" and payment_mode = "+uF.parseToInt(getStrpaymentmode()));	
				}
				sbQuery.append(" and eod.emp_id in(select emp_id from emp_salary_details where salary_head_id="+TDS+" and isdisplay=true and is_approved=true)");
				sbQuery.append(" and (epd.employment_end_date is null or epd.employment_end_date<=?)");
				sbQuery.append(" order by emp_fname, emp_lname");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			}
//			System.out.println("Pst===>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				String strEmpId = rs.getString("emp_per_id");
				alEmp.add(strEmpId);
			}
			rs.close();
			pst.close();
			try {
				HSSFWorkbook workbook = new HSSFWorkbook();
				HSSFSheet sheet = workbook.createSheet("TDS Projection");
				List<DataStyle> header = new ArrayList<DataStyle>();
				header.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				header.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				header.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				header.add(new DataStyle("Amount",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				header.add(new DataStyle("Month",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();
				for (int i = 0; i < alEmp.size(); i++) {			
					 List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
					    alInnerExport.add(new DataStyle(String.valueOf(i+1),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle(hmEmpCodeMap.get(alEmp.get(i)),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle(hmEmpMap.get(alEmp.get(i)),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("0",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					//===start parvez date: 16-08-2022===	
//						alInnerExport.add(new DataStyle(" ",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					//===end parvez date: 16-08-2022===	
//						
						reportData.add(alInnerExport);
				}
				ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
				sheetDesign.generateDefualtExcelSheet(workbook, sheet, header, reportData);		
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				workbook.write(buffer);
				response.setContentType("application/vnd.ms-excel:UTF-8");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=ImportTdsProjection"+".xls");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush();
				buffer.close();
				out.close();
			} catch (Exception e) {
				// TODO: handle exception
			}  finally {
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeResultSet(rs);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	
	
	
public void updateTDSPayroll(UtilityFunctions uF,String month, int empId , String amount) {
		
		Connection con = null;
		PreparedStatement pst=null;		
		Database db = new Database();
		db.setRequest(request);
		
		try {
			String[] strFinancialYearDates = null;

			if (getFinancialYear() != null) {
				strFinancialYearDates = getFinancialYear().split("-");
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
			}
			
			con = db.makeConnection(con);
				pst = con.prepareStatement(updateTDSProjection);
				pst.setDouble(1, uF.parseToDouble(amount));
				pst.setInt(2,(Double.valueOf(month)).intValue());
				pst.setInt(3, empId);
				pst.setDate(4, uF.getDateFormat(strFinancialYearDates[0], DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearDates[1], DATE_FORMAT));
				//System.out.println("pst===>"+pst);
				int x = pst.executeUpdate();
				pst.close();
				if(x==0) {
					pst = con.prepareStatement(insertTDSProjection);
					pst.setDouble(1, uF.parseToDouble(amount));
					pst.setInt(2,(Double.valueOf(month)).intValue());
					pst.setInt(3, empId);
					pst.setInt(4, TDS);
					pst.setDate(5, uF.getDateFormat(strFinancialYearDates[0], DATE_FORMAT));
					pst.setDate(6, uF.getDateFormat(strFinancialYearDates[1], DATE_FORMAT));
					//System.out.println("pst===>"+pst);
					pst.execute();
					pst.close();
				}
				request.setAttribute("STATUS_MSG", "Updated");
			
		} catch (Exception e) {
			e.printStackTrace();
			
			flag =false;
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				session.setAttribute(MESSAGE,ERRORM+ "TDS Projection imported not imported. Please check imported file."+ END);
			}
			session.setAttribute(MESSAGE,ERRORM+ "TDS Projection imported not imported. Please check imported file."+ END);
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getStrgrade() {
		return strgrade;
	}

	public void setStrgrade(String strgrade) {
		this.strgrade = strgrade;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getStrdepartment() {
		return strdepartment;
	}

	public void setStrdepartment(String strdepartment) {
		this.strdepartment = strdepartment;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrorg() {
		return strorg;
	}

	public void setStrorg(String strorg) {
		this.strorg = strorg;
	}

	public String getStrpaymentmode() {
		return strpaymentmode;
	}

	public void setStrpaymentmode(String strpaymentmode) {
		this.strpaymentmode = strpaymentmode;
	}

	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public String getExceldownload() {
		return exceldownload;
	}

	public void setExceldownload(String exceldownload) {
		this.exceldownload = exceldownload;
	}	
	
}

