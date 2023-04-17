package com.konnect.jpms.tms;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.leave.ManagerLeaveApproval;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ImportLeave extends ActionSupport implements ServletRequestAware, IConstants,IStatements {

	private static final long serialVersionUID = 1L;
	private File fileUpload;

	private String fileUploadContentType;
	private String fileUploadFileName;
	

	List<String> alReport = new ArrayList<String>();
	CommonFunctions CF;
	HttpSession session;
	private HttpServletRequest request;
	


	public String execute() {
		try {

			session = request.getSession();
			UtilityFunctions uF = new UtilityFunctions();
			CF  = (CommonFunctions)session.getAttribute(CommonFunctions);
			if(CF==null) {
				return LOGIN;
			}
			
//			boolean isView  = CF.getAccess(session, request, uF);
//			if(!isView) {
//				request.setAttribute(PAGE, PAccessDenied);
//				request.setAttribute(TITLE, TAccessDenied);
//				return ACCESS_DENIED;
//			}
			
			request.setAttribute(PAGE, "/jsp/tms/ImportAttendance.jsp");
			request.setAttribute(TITLE, "Import Leave");
			
			if(fileUpload!=null) {
				importFileFormatA0001(fileUpload, uF);
			}
			
         
		} catch (Exception e) {
			e.printStackTrace();
		}

		return SUCCESS;
	}
	
	public void importFileFormatA0001(File path,UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs=null;
		String dateFormat="dd/MM/yyyy";
		String timeFormat="HH:mm:ss";
		FileInputStream fis=null;
		List<String> alErrorList = new ArrayList<String>();
		try {
			con = db.makeConnection(con);
			con.setAutoCommit(false);
			Map<String,String> hmEmpLevel = CF.getEmpLevelMap(con);
			fis = new FileInputStream(path);
			HSSFWorkbook workbook = new HSSFWorkbook(fis);

			//System.out.println("Start Reading Excelsheet.... ");
//			XSSFSheet attendanceSheet = workbook.getSheet("Sheet1");
			HSSFSheet attendanceSheet = workbook.getSheetAt(0);
			List<List<String>> dataList = new ArrayList<List<String>>();
			Iterator rows = attendanceSheet.rowIterator();

			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();

				Iterator cells = row.cellIterator();
				List<String> cellList = new ArrayList<String>();

				while (cells.hasNext()) {
					HSSFCell cell =(HSSFCell)cells.next();
					cellList.add(cell.toString());
				}
				dataList.add(cellList);
			}

			boolean flag = false;
			
			//System.out.println("dataList.size() ===>> " + dataList.size());
			//System.out.println("dataList ===>> " + dataList);
			
			for (int i = 1; i < dataList.size(); i++) {
				int emp_per_id = 0;
				int org_id = 0;
				String wlocation = null;
				
				List<String> cellList =  dataList.get(i);
				
				String empcode =  cellList.get(1);
				String strDate =  cellList.get(2);
				String strLeaveCode =  cellList.get(3);
				String strFdHd =  cellList.get(4);

				if (empcode.contains(".")) {
					empcode = empcode.substring(0, empcode.indexOf("."));

				}

				// Select Employ ID
				pst = con.prepareStatement("select emp_per_id,org_id,wlocation_id from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id and empcode=?");
				pst.setString(1, empcode.trim());
				rs = pst.executeQuery();
				while (rs.next()) {
					emp_per_id = rs.getInt(1);
					org_id = uF.parseToInt(rs.getString("org_id"));
					wlocation = rs.getString("wlocation_id");
				}
				rs.close();
				pst.close();
				if(emp_per_id > 0) {
					flag = uF.isThisDateValid(strDate, DATE_FORMAT);
					
					if(!flag) {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check date format for employee code '"+empcode+"' on "+strDate+".</li>");
						break;
					}
					
					boolean checkSalaryFlag = CF.checkSalaryForImportAttendance(con, CF, uF, emp_per_id, strDate, strDate, DATE_FORMAT);
					if(checkSalaryFlag) {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Salary already processed for employee code '" + empcode + "' on "+strDate+".</li>");
						flag = false;
						break;
					}
					
					boolean checkAttendanceApproveFlag = CF.checkAttendanceApproveForImportAttendance(con, CF, uF, emp_per_id, strDate, strDate, DATE_FORMAT);
					if(checkAttendanceApproveFlag) {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Attendance already approved for employee code '" + empcode + "' on "+strDate+".</li>");
						flag = false;
						break;
					}
					
					boolean checkLeaveFlag = CF.checkLeaveForImportAttendance(con, CF, uF, emp_per_id, strDate, strDate, DATE_FORMAT);
					if(checkLeaveFlag) {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Leave already applied for employee code '" + empcode + "' on "+strDate+".</li>");
						flag = false;
						break;
					}
					
					boolean checkAttendanceFlag = CF.checkAttendanceForImportAttendance(con, CF, uF, emp_per_id, strDate, strDate, DATE_FORMAT);
					if(checkAttendanceFlag) {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Attendance already existed for employee code '" + empcode + "' on "+strDate+".</li>");
						flag = false;
						break;
					}
					
					pst = con.prepareStatement("select leave_type_id,is_compensatory,leave_type_code,org_id from leave_type");
					rs = pst.executeQuery();
					Map<String,Map<String,List<String>>> leaveTypeOrgMp1=new HashMap<String,Map<String,List<String>>>();
					while (rs.next()) {
						Map<String,List<String>> leaveTypeOrgMp=leaveTypeOrgMp1.get(rs.getString("org_id"));
						if(leaveTypeOrgMp==null)leaveTypeOrgMp=new HashMap<String,List<String>>();
						
						List<String> innerList=new ArrayList<String>();
						innerList.add(rs.getString("leave_type_id"));
						innerList.add(rs.getString("is_compensatory"));
						
						leaveTypeOrgMp.put(rs.getString("leave_type_code"),innerList);
						
						leaveTypeOrgMp1.put(rs.getString("org_id"), leaveTypeOrgMp);
					}
					rs.close();
					pst.close();
					
					String leaveTypeId=null;
					boolean is_compensatory=false;
					Map<String,List<String>> leaveTypeOrgMp=leaveTypeOrgMp1.get(org_id+"");
					List<String> leaveInnerList=leaveTypeOrgMp.get(strLeaveCode);
					if(leaveInnerList==null) {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check leave("+strLeaveCode+") for employee code '"+empcode+"' on "+strDate+".</li>");
						flag = false;
						break;
					}
					if(leaveInnerList!=null) {
						leaveTypeId=leaveInnerList.get(0);
						is_compensatory=uF.parseToBoolean(leaveInnerList.get(1));
					}
						
					if(leaveTypeId!=null) {
						if(strFdHd.contains("HD")) {
							
							pst = con.prepareStatement("select * from emp_leave_type where level_id = (select level_id from  designation_details dd, grades_details gd where gd.designation_id = dd.designation_id and gd.grade_id = (select grade_id from employee_official_details where emp_id = ?)) and leave_type_id = ? and org_id=? and wlocation_id=?");
							pst.setInt(1, emp_per_id);
							pst.setInt(2, uF.parseToInt(leaveTypeId));
							pst.setInt(3, org_id);
							pst.setInt(4, uF.parseToInt(wlocation));
							rs = pst.executeQuery();
							boolean isCompensate=false;
							boolean isPaid = false;
							while(rs.next()) {
								isPaid = uF.parseToBoolean(rs.getString("is_paid"));
								isCompensate=uF.parseToBoolean(rs.getString("is_compensatory"));
							}
							rs.close();
							pst.close();
							
							pst=con.prepareStatement("INSERT INTO emp_leave_entry (emp_id,leave_from,leave_to,entrydate,emp_no_of_leave," +
									"leave_type_id,reason,approval_from,approval_to_date, ishalfday, session_no, " +
									"document_attached, is_approved, ispaid,is_compensate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
							pst.setInt(1, emp_per_id);
							pst.setDate(2, uF.getDateFormat(strDate, DATE_FORMAT));
							pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
							pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setDouble(5, 0.5);
							pst.setInt(6, uF.parseToInt(leaveTypeId));
							pst.setString(7,"Import Leave Data");
							pst.setDate(8, uF.getDateFormat(strDate, DATE_FORMAT));
							pst.setDate(9, uF.getDateFormat(strDate, DATE_FORMAT));
							pst.setBoolean(10, true);
							pst.setString(11, null);
							pst.setString(12, null);
							pst.setInt(13, 1);
							pst.setBoolean(14, isPaid);
							pst.setBoolean(15, isCompensate);
							int x = pst.executeUpdate();
							//System.out.println("HD pst ===>> " + pst);
							pst.close();
							
							if(x > 0) {
								String leave_id=null;
								pst = con.prepareStatement("select max(leave_id)as leave_id from emp_leave_entry");
								rs=pst.executeQuery();
								while(rs.next()) {
									leave_id=rs.getString("leave_id");
								}
								rs.close();
								pst.close();
								
								if(uF.parseToInt(leave_id)>0) {
									
									ManagerLeaveApproval leaveApproval = new ManagerLeaveApproval();
									leaveApproval.setServletRequest(request);
									leaveApproval.setLeaveId(leave_id);
									leaveApproval.setTypeOfLeave(""+uF.parseToInt(leaveTypeId));
									leaveApproval.setEmpId(""+emp_per_id);
									leaveApproval.setIsapproved(1);
									leaveApproval.setApprovalFromTo(strDate);
									leaveApproval.setApprovalToDate(strDate);
									leaveApproval.setIsHalfDay(true);
									leaveApproval.insertLeaveBalance(con,pst,rs,uF,uF.parseToInt(hmEmpLevel.get(emp_per_id+"")),CF);
								} 
							} else {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check Leave Type code and FD/HD for employee code '"+empcode+"' on "+strDate+".</li>");
								flag = false;
								break;
							}
						} else {
							
//							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check Leave Type code for employee code '"+empcode+"' on "+strDate+".</li>");
							pst = con.prepareStatement("select * from emp_leave_type where level_id = (select level_id from  designation_details dd, grades_details gd where gd.designation_id = dd.designation_id and gd.grade_id = (select grade_id from employee_official_details where emp_id = ?)) and leave_type_id = ? and org_id=? and wlocation_id=?");
							pst.setInt(1, emp_per_id);
							pst.setInt(2, uF.parseToInt(leaveTypeId));
							pst.setInt(3, org_id);
							pst.setInt(4, uF.parseToInt(wlocation));
							rs = pst.executeQuery();
							boolean isCompensate=false;
							boolean isPaid = false;
							while(rs.next()) {
								isPaid = uF.parseToBoolean(rs.getString("is_paid"));
								isCompensate=uF.parseToBoolean(rs.getString("is_compensatory"));
							}
							rs.close();
							pst.close();
							
							pst=con.prepareStatement("INSERT INTO emp_leave_entry (emp_id,leave_from,leave_to,entrydate,emp_no_of_leave," +
									"leave_type_id,reason,approval_from,approval_to_date, ishalfday, session_no, " +
									"document_attached, is_approved, ispaid,is_compensate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
							pst.setInt(1, emp_per_id);
							pst.setDate(2, uF.getDateFormat(strDate, DATE_FORMAT));
							pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
							pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
							int nAppliedDays = uF.parseToInt(uF.dateDifference(strDate,DATE_FORMAT, strDate, DATE_FORMAT,CF.getStrTimeZone()));
							pst.setInt(5, nAppliedDays);
							pst.setInt(6, uF.parseToInt(leaveTypeId));
							pst.setString(7,"Import Leave Data");
							pst.setDate(8, uF.getDateFormat(strDate, DATE_FORMAT));
							pst.setDate(9, uF.getDateFormat(strDate, DATE_FORMAT));
							pst.setBoolean(10, false);
							pst.setString(11, null);
							pst.setString(12, null);
							pst.setInt(13, 1);
							pst.setBoolean(14, isPaid);
							pst.setBoolean(15, isCompensate);
							int x = pst.executeUpdate();
							//System.out.println("FD pst ===>> " + pst);
							pst.close();
							
							if(x > 0) {
							
								String leave_id=null;
								pst = con.prepareStatement("select max(leave_id)as leave_id from emp_leave_entry");
								rs=pst.executeQuery();
								while(rs.next()) {
									leave_id=rs.getString("leave_id");
								}
								rs.close();
								pst.close();
								
								if(uF.parseToInt(leave_id)>0) {
									ManagerLeaveApproval leaveApproval = new ManagerLeaveApproval();
									leaveApproval.setServletRequest(request);
									leaveApproval.setLeaveId(leave_id);
									leaveApproval.setTypeOfLeave(""+uF.parseToInt(leaveTypeId));
									leaveApproval.setEmpId(""+emp_per_id);
									leaveApproval.setIsapproved(1);
									leaveApproval.setApprovalFromTo(strDate);
									leaveApproval.setApprovalToDate(strDate);
									leaveApproval.setIsHalfDay(false);				
									leaveApproval.insertLeaveBalance(con,pst,rs,uF,uF.parseToInt(hmEmpLevel.get(emp_per_id+"")),CF);
								}
							} else {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check Leave Type code and FD/HD for employee code '"+empcode+"' on "+strDate+".</li>");
								flag = false;
								break;
							}
						}
						flag =true;
					}
					
				} else {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the employee code '"+empcode+"'.</li>");
					flag = false;
					break;
				}
			} // end main for loop
//			System.out.println("flag==>"+flag);
			if(flag) {
				con.commit();
				session.setAttribute(MESSAGE, SUCCESSM+"Leave Imported Successfully!"+END);
			} else {
				con.rollback();
				if(alErrorList.size()>0) {
					alReport.add(alErrorList.get(alErrorList.size()-1));
				}
				session.setAttribute("alReport", alReport);
				session.setAttribute(MESSAGE, ERRORM+"Leave not imported. Please check imported file."+END);
			}
			
		}catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if(alErrorList.size()>0) {
				alReport.add(alErrorList.get(alErrorList.size()-1));
			}
			session.setAttribute("alReport", alReport);
			session.setAttribute(MESSAGE, ERRORM+"Leave not imported. Please check imported file."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
			try{
				fis.close();
			}catch(Exception ex) {
				
			}
			System.gc();
		}
	}

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

	public String getFileUploadContentType() {
		return fileUploadContentType;
	}

	public void setFileUploadContentType(String fileUploadContentType) {
		this.fileUploadContentType = fileUploadContentType;
	}

	public String getFileUploadFileName() {
		return fileUploadFileName;
	}

	public void setFileUploadFileName(String fileUploadFileName) {
		this.fileUploadFileName = fileUploadFileName;
	}
	
}
