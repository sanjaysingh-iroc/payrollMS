package com.konnect.jpms.leave;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
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

public class ImportLeaveRegularisation extends ActionSupport implements ServletRequestAware,ServletResponseAware, IConstants, IStatements {
	private static final long serialVersionUID = 1L;

	File fileUpload;
	String fileUploadFileName;
	
	HttpSession session;
	CommonFunctions CF;
	String strSessionEmpId;
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	String f_org;
	String exceldownload;
	String strEffectiveDate;
	String strUserType = null;
	boolean flag = true;
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
				genrateexcel();
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

	void genrateexcel()
	{
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		db.setRequest(request);
		
		List<List<String>> reportList = new ArrayList<List<String>>();
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			
			Map<String, String> hmLeaveType = CF.getLeaveTypeMap(con);
			if(hmLeaveType == null) hmLeaveType = new HashMap<String, String>();
			Map<String, String> hmOrg = CF.getOrgName(con);
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			if(hmEmpLevelMap == null) hmEmpLevelMap = new HashMap<String, String>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_leave_type elt, leave_type lt where elt.leave_type_id=lt.leave_type_id and " +
					"elt.leave_type_id in (select leave_type_id from leave_type where is_compensatory = false and is_work_from_home=false");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(") and elt.is_constant_balance=false ");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and elt.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and elt.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}	
			sbQuery.append(" and (lt.is_leave_opt_holiday is null or lt.is_leave_opt_holiday=false) order by elt.level_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			List<String> alLeaveType = new ArrayList<String>();
			Map<String, List<String>> hmLeavesType = new HashMap<String, List<String>>();
			while(rs.next()){
				if(uF.parseToInt(rs.getString("leave_type_id"))>0){
					if(!alLeaveType.contains(rs.getString("leave_type_id"))){
						alLeaveType.add(rs.getString("leave_type_id"));
					}

					List<String> alLeave = hmLeavesType.get(rs.getString("level_id")+"_"+rs.getString("wlocation_id"));
					if(alLeave == null) alLeave = new ArrayList<String>();
					alLeave.add(rs.getString("leave_type_id"));
					
					hmLeavesType.put(rs.getString("level_id")+"_"+rs.getString("wlocation_id"), alLeave);
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod, probation_policy pp " +
						"where epd.emp_per_id=eod.emp_id and epd.emp_per_id=pp.emp_id and eod.emp_id=pp.emp_id and is_alive = true " +
						"and emp_per_id>0 and (employment_end_date is null OR employment_end_date <= ?)");
			if(getStrLevel()!=null && getStrLevel().length() >0){
                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+getStrLevel()+") ) ");
            }
            if(getStrDepartment()!=null && getStrDepartment().length()>0){
                sbQuery.append(" and eod.depart_id in ("+getStrDepartment()+")");
            }
            
            /*if(getStrSbu()!=null && getStrSbu().length()>0){
                sbQuery.append(" and (");
                for(int i=0; i<getStrSbu().length(); i++){
                	sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
            } */
            if(getStrLocation()!=null && getStrLocation().length()>0){
                sbQuery.append(" and eod.wlocation_id in ("+getStrLocation()+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            sbQuery.append(" and (pp.leaves_types_allowed is not null and pp.leaves_types_allowed !='') " +
					"and epd.joining_date is not null order by eod.emp_id");
		    pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
		   // System.out.println("pst====>"+pst); 
		    rs = pst.executeQuery();
			List<String> alEmp = new ArrayList<String>();
			Map<String, List<String>> hmEmpLeaves = new HashMap<String, List<String>>();
			while(rs.next()){
				int nLevelId = uF.parseToInt(hmEmpLevelMap.get(rs.getString("emp_id")));
				
				List<String> alLeave = hmLeavesType.get(nLevelId+"_"+rs.getString("wlocation_id"));
				if(alLeave == null) alLeave = new ArrayList<String>();
				
				String strAllowedLeaves = rs.getString("leaves_types_allowed");
				if(strAllowedLeaves!=null && strAllowedLeaves.length()>0){
					List<String> al = Arrays.asList(strAllowedLeaves.split(","));
					for(String leaveTypeId : al){
 						if(uF.parseToInt(leaveTypeId) > 0 && alLeaveType.contains(leaveTypeId) && alLeave.contains(leaveTypeId)){
							if(!alEmp.contains(rs.getString("emp_id"))){
								alEmp.add(rs.getString("emp_id"));
							}
							
							List<String> alEmpLeave = hmEmpLeaves.get(rs.getString("emp_id"));
							if(alEmpLeave == null) alEmpLeave = new ArrayList<String>();
							alEmpLeave.add(leaveTypeId);
							
							hmEmpLeaves.put(rs.getString("emp_id"), alEmpLeave);
						}
					}
				}				
			}
			rs.close();
			pst.close();
			
			int nAlEmp = alEmp.size();
			for(int i = 0; i < nAlEmp; i++){
				int nEmpId = uF.parseToInt(alEmp.get(i));
				
				List<String> alEmpLeave = hmEmpLeaves.get(alEmp.get(i));
				if(alEmpLeave == null) alEmpLeave = new ArrayList<String>(); 
				
				for(int j = 0; j < alLeaveType.size(); j++){
					int nLeaveTypeId = uF.parseToInt(alLeaveType.get(j));
					if(!alEmpLeave.contains(""+nLeaveTypeId)){
						continue;
					}
					
					pst = con.prepareStatement("select leave_type_id, balance from leave_register1 where register_id in(select max(register_id) from leave_register1 " +
							"where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?)" +
							" group by emp_id,leave_type_id) and leave_type_id=?");
		            pst.setInt(1, nEmpId);
		            pst.setInt(2, nLeaveTypeId);
		            pst.setInt(3, nLeaveTypeId);
		            rs = pst.executeQuery();
		            Map<String, String> hmMainBalance=new HashMap<String, String>();
		            while (rs.next()) {
		                hmMainBalance.put(rs.getString("leave_type_id"), rs.getString("balance"));
		            }
		            rs.close();
		            pst.close();
		            
		            pst = con.prepareStatement("select sum(accrued) as accrued,a.leave_type_id from (select max(_date) as daa,leave_type_id " +
		            		"from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) " +
		            		"group by emp_id,leave_type_id )as a,leave_register1 lr where emp_id=? and _type!='C' and a.leave_type_id=lr.leave_type_id " +
		            		"and a.daa<=lr._date and lr.leave_type_id=? group by a.leave_type_id");
		            pst.setInt(1, nEmpId);
		            pst.setInt(2, nLeaveTypeId);
		            pst.setInt(3, nEmpId);
		            pst.setInt(4, nLeaveTypeId);
		            rs = pst.executeQuery();
		            Map<String, String> hmAccruedBalance=new HashMap<String, String>();
		            while (rs.next()) {
		            	hmAccruedBalance.put(rs.getString("leave_type_id"), rs.getString("accrued"));                
		            }
					rs.close();
					pst.close();
					
		            pst = con.prepareStatement("select sum(leave_no) as count,leave_type_id from(select a.daa,lar.* from (select max(_date) as daa,leave_type_id " +
		            		"from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) " +
		            		"group by emp_id,leave_type_id) as a,leave_application_register lar where emp_id=? and is_paid=true and (is_modify is null or is_modify=false) " +
		            		"and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date and lar.leave_type_id=?) as a group by leave_type_id");
		            pst.setInt(1, nEmpId);
		            pst.setInt(2, nLeaveTypeId);
		            pst.setInt(3, nEmpId);
		            pst.setInt(4, nLeaveTypeId);
		            rs = pst.executeQuery();
		            Map<String, String> hmPaidBalance=new HashMap<String, String>();
		            while (rs.next()) {
		            	hmPaidBalance.put(rs.getString("leave_type_id"), rs.getString("count"));
		            }
					rs.close();
					pst.close();
					
					double dblBalance = uF.parseToDouble(hmMainBalance.get(""+nLeaveTypeId));
					dblBalance += uF.parseToDouble(hmAccruedBalance.get(""+nLeaveTypeId));
					
					double dblPaidBalance = uF.parseToDouble(hmPaidBalance.get(""+nLeaveTypeId));
					
					if(dblBalance > 0 && dblBalance >= dblPaidBalance){
			            dblBalance = dblBalance - dblPaidBalance; 
			        }
					
					List<String> alInner = new ArrayList<String>();
					alInner.add(""+nEmpId);//0
					alInner.add(hmEmpCode.get(""+nEmpId));//1
					alInner.add(hmEmpName.get(""+nEmpId));//2
					alInner.add(uF.showData(hmLeaveType.get(""+nLeaveTypeId), ""));//3
					alInner.add(""+nLeaveTypeId);//4
					alInner.add(""+dblBalance);//5
					
					reportList.add(alInner);
				}
			}
			request.setAttribute("reportList", reportList);

			if(!getExceldownload().equalsIgnoreCase("null") && getExceldownload()!=null)
			{
				generateLeaveRegulariseExcel(uF,"Leave_Regularization",reportList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
private void generateLeaveRegulariseExcel(UtilityFunctions uF ,String filename , List<List<String>> reportList ) {
		
		try {	
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Leave Regularise");
			List<DataStyle> header = new ArrayList<DataStyle>();
			header.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Leave Type",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Leave Balance",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Effective From ",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY)); 
			List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();
			
			for (int i = 0; i < reportList.size(); i++) {
				List<String> cinnerlist = (List<String>) reportList.get(i);
				List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle(cinnerlist.get(0),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(cinnerlist.get(1),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(cinnerlist.get(2),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(cinnerlist.get(3),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(cinnerlist.get(5),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(getStrEffectiveDate(),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				reportData.add(alInnerExport);
			}
	
			ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
//			sheetDesign.getExcelSheetDesignData(workbook, sheet, header, reportData);
			sheetDesign.generateDefualtExcelSheet(workbook, sheet, header, reportData);
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=LeaveRegularise.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}
	
	
	
	public void loadExcel(File file) throws IOException {
		Database db = new Database();
		db.setRequest(request);
		
		Connection con=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		ResultSet rsEm = null;
		UtilityFunctions uF = new UtilityFunctions();
		boolean flag = true;
		try {
			con = db.makeConnection(con);
			con.setAutoCommit(false);
			
			FileInputStream fis = new FileInputStream(file);
			List<List> dataList = new ArrayList<List>();
			HSSFWorkbook wb = new HSSFWorkbook(fis);
			HSSFSheet sheet=wb.getSheetAt(0);
			HSSFRow row; 
			HSSFCell cell;
			Iterator rows = sheet.rowIterator();
			while (rows.hasNext()) {
				
				row = (HSSFRow) rows.next();
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
				//System.out.println();
			}
//			System.out.println("ListSize Export=====>"+dataList.size());
			
			if(dataList.size()>0){
				for (int i = 1; i < dataList.size(); i++) {
					List cellList =dataList.get(i);
					int leavetypeId=0;
					String employeeId="";
					String employeeCode = (String) cellList.get(1);
					String leaveType = (String) cellList.get(3);
					String balance = (String) cellList.get(4);
					String effectiveDate = (String) cellList.get(5);
					
					pst = con.prepareStatement("select * from employee_personal_details where empcode = ? ");
					pst.setString(1, employeeCode);
//					System.out.println("Pst====>"+pst);
					rsEm = pst.executeQuery();
					if(rsEm.next()) {
						employeeId = rsEm.getString("emp_per_id");	
//						System.out.println("emp_per_id is for employeeCode ===>"+ rsEm.getString("emp_per_id")+"===>"+employeeCode);
					}
					pst.close();
					rsEm.close();
					
					String empOrgId = CF.getEmpOrgId(con, uF, employeeId);
					pst = con.prepareStatement("select * from leave_type where leave_type_name = ? and org_id=?");
					pst.setString(1, leaveType);
					pst.setInt(2, uF.parseToInt(empOrgId));
//					System.out.println("Pst====>"+pst);
					rsEm = pst.executeQuery();
					if(rsEm.next()) {
						leavetypeId = rsEm.getInt("leave_type_id");	
//						System.out.println("Leve Type is for leave ===>"+ rsEm.getString("leave_type_id")+"===>"+leaveType);
					}
					pst.close();
					rsEm.close();
					
					if(uF.parseToInt(employeeId) > 0 && leavetypeId > 0) {
						addRegulariseBalance(uF,employeeId,leavetypeId,balance,effectiveDate,con);
						flag = true;
					} else {
						flag = false;
					}
				
				}
			} else {
				flag= false;
			}
			
			if(flag) {
				con.commit();
				session.setAttribute(MESSAGE, SUCCESSM+ "Leave Regularisation imported successfully."+ END);
			} else {
				con.rollback();
				session.setAttribute(MESSAGE, ERRORM+ "Leave Regularisation not imported. Please check imported file."+ END);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				session.setAttribute(MESSAGE,ERRORM+ "Leave Regularisation not imported. Please check imported file."+ END);
			}
			session.setAttribute(MESSAGE,ERRORM+ "Leave Regularisation not imported. Please check imported file."+ END);
		} finally {
			db.closeResultSet(rsEm);
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public boolean addRegulariseBalance(UtilityFunctions uF , String strEmpId, int LeaveTypeId ,String strLeaveBalance, String strEffectiveDate , Connection con){
		boolean flag=false;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			java.util.Date currDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),DBDATE, DATE_FORMAT),DATE_FORMAT );
			java.util.Date effectiveDate = uF.getDateFormatUtil(strEffectiveDate, DATE_FORMAT,DATE_FORMAT);
			
				if(uF.parseToInt(strEmpId) > 0 && LeaveTypeId > 0) {	
			//===start parvez date: 19-11-2021===
//					pst=con.prepareStatement("delete from leave_register1 where emp_id=? and _date=? and leave_type_id=?");
					pst=con.prepareStatement("delete from leave_register1 where emp_id=? and _date>=? and leave_type_id=?");
			//===end parvez date: 19-11-2021===
					pst.setInt(1,  uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getDateFormat(strEffectiveDate, DATE_FORMAT));
					pst.setInt(3, LeaveTypeId);
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id," +
							" _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
					pst.setDouble(1, 0);
					pst.setDouble(2, 0);
					pst.setDouble(3, 0);
					pst.setInt(4, uF.parseToInt(strEmpId));
					pst.setInt(5, LeaveTypeId);
					pst.setDate(6, uF.getDateFormat(strEffectiveDate, DATE_FORMAT));
					pst.setInt(7, 1);
					pst.setString(8, "C");
					pst.setDouble(9, uF.parseToDouble(strLeaveBalance));
					pst.setInt(10, 0);
					int x = pst.executeUpdate();
					pst.close();
					
					if(x > 0) {
						
						String strEmpOrgId = CF.getEmpOrgId(con, uF, strEmpId);
						String strEmpWLocationId = CF.getEmpWlocationId(con, uF, strEmpId);
						String strEmpLevelId = CF.getEmpLevelId(con, strEmpId);
						
						pst = con.prepareStatement("select * from emp_leave_type where leave_type_id in (select leave_type_id from leave_type " +
								"where is_compensatory = false and is_work_from_home=false and leave_type_id=? and org_id=?) and is_constant_balance=false and leave_type_id=? " +
								"and org_id=? and is_leave_accrual=true and accrual_type=2 and is_accrued_cal_days=true and level_id=? " +
								"and wlocation_id=?");
						pst.setInt(1, LeaveTypeId);
						pst.setInt(2, uF.parseToInt(strEmpOrgId));
						pst.setInt(3, LeaveTypeId);
						pst.setInt(4, uF.parseToInt(strEmpOrgId));
						pst.setInt(5, uF.parseToInt(strEmpLevelId));
						pst.setInt(6, uF.parseToInt(strEmpWLocationId));
						rs = pst.executeQuery();
						boolean isAccrueCalDays = false;
						double dblAccrueBalance = 0.0d;
						while(rs.next()){
							isAccrueCalDays = true;
							dblAccrueBalance = uF.parseToDouble(rs.getString("no_of_leave_monthly"));
						}
						rs.close();
						pst.close();
						
						if(isAccrueCalDays) {
							pst = con.prepareStatement("delete from leave_register1 where emp_id=? and _date > ? and leave_type_id=?");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getDateFormat(strEffectiveDate, DATE_FORMAT));
							pst.setInt(3, LeaveTypeId);
							pst.execute();
							pst.close();
							
							if(currDate.after(effectiveDate)) {
								java.sql.Date tomorrowDate =  uF.getFutureDate(effectiveDate, 1);
								
								String strDateDiff = uF.dateDifference(uF.getDateFormat(""+tomorrowDate, DBDATE, DATE_FORMAT), DATE_FORMAT, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), DATE_FORMAT,CF.getStrTimeZone());
			                 	strDateDiff = strDateDiff !=null && !strDateDiff.trim().equals("") && !strDateDiff.trim().equalsIgnoreCase("NULL") ? strDateDiff.trim() : "0"; 
			                    int dayCnt = uF.parseToInt(strDateDiff);
			                    
	//		                    System.out.println("tomorrowDate==>"+tomorrowDate+"--dayCnt==>"+dayCnt);
			                    
			                    Calendar cal = GregorianCalendar.getInstance();
			                    cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(""+tomorrowDate, DBDATE, "dd")));
			                    cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(""+tomorrowDate, DBDATE, "MM")) - 1);
			                    cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(""+tomorrowDate, DBDATE, "yyyy")));
			                    
			                    for (int j = 0; j < dayCnt; j++) {
			                    	Date dtCurrent = uF.getDateFormat(cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR), DATE_FORMAT);
									cal.add(Calendar.DATE, 1);
									
									pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id," +
											" _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
									pst.setDouble(1, 0);
									pst.setDouble(2, 0);
									pst.setDouble(3, dblAccrueBalance);
									pst.setInt(4, uF.parseToInt(strEmpId));
									pst.setInt(5, LeaveTypeId);
									pst.setDate(6, dtCurrent);
									pst.setInt(7, 1);
									pst.setString(8, "A");
									pst.setDouble(9, 0.0d);
									pst.setInt(10, 0);
									pst.execute();
									pst.close();
			                    }
			                    
							}
						}
					}					
				}
				
		} catch (Exception e) {
			e.printStackTrace();
			return false;
			
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	
		return flag;
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

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public String getStrSbu() {
		return strSbu;
	}

	public void setStrSbu(String strSbu) {
		this.strSbu = strSbu;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getExceldownload() {
		return exceldownload;
	}

	public void setExceldownload(String exceldownload) {
		this.exceldownload = exceldownload;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getStrEffectiveDate() {
		return strEffectiveDate;
	}

	public void setStrEffectiveDate(String strEffectiveDate) {
		this.strEffectiveDate = strEffectiveDate;
	}

	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

}


