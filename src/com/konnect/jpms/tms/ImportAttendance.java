package com.konnect.jpms.tms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.leave.ManagerLeaveApproval;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ImportAttendance extends ActionSupport implements ServletRequestAware, IConstants, IStatements {

	private static final long serialVersionUID = 1L;
	private File fileUpload, fileUpload1, fileUpload2, fileUpload3, fileUpload4, fileUpload5, fileUpload6, fileUpload7, fileUpload8, fileUploadNew3,
			fileUploadNew4, fileUploadZicon;

	public File getFileUpload8() {
		return fileUpload8;
	}

	public void setFileUpload8(File fileUpload8) {
		this.fileUpload8 = fileUpload8;
	}

	private String fileUploadContentType, fileUpload1ContentType, fileUpload2ContentType, fileUpload3ContentType, fileUpload4ContentType,
			fileUpload5ContentType, fileUpload6ContentType;
	private String fileUploadFileName, fileUpload1FileName, fileUpload2FileName, fileUpload3FileName, fileUpload4FileName, fileUpload5FileName,
			fileUpload6FileName;

	List<String> alReport = new ArrayList<String>();
	CommonFunctions CF;
	HttpSession session;
	private HttpServletRequest request;
	
	private String strLocation;
	private String strDepartment;
	private String strSbu;
	private String strLevel;
	private String strGrade;
	private String strEmployeType;
	String fromPage;

	private String paycycle;
	private String f_org;
	
	List<FillPayCycles> paycycleList;
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	
	String wLocation;

	String pageFrom;
	String strSessionEmpId;

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
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

	public String getStrGrade() {
		return strGrade;
	}

	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}

	public String getStrEmployeType() {
		return strEmployeType;
	}

	public void setStrEmployeType(String strEmployeType) {
		this.strEmployeType = strEmployeType;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
	
	public String execute() {

		try {
			session = request.getSession();
			strSessionEmpId = (String) session.getAttribute(EMPID);
			UtilityFunctions uF = new UtilityFunctions();
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if (CF == null) {
				return LOGIN;
			}

			//System.out.println("paycycle="+paycycle+"f_org="+f_org+"strDepartment="+strDepartment+"strLocation="+strLocation
			//+"strSbu="+strSbu+"strLevel="+strLevel+"strGrade="+strGrade+"strEmployeType="+strEmployeType);
			
			// System.out.println("pageFrom ===>>> " + pageFrom);
			/*boolean isView = CF.getAccess(session, request, uF);
			if (!isView) {
				request.setAttribute(PAGE, PAccessDenied);
				request.setAttribute(TITLE, TAccessDenied);
				return ACCESS_DENIED;
			}*/
			

			orgList = new FillOrganisation(request).fillOrganisation();

			if (orgList != null && orgList.size() > 0) {
				if (getF_org() != null) {
					wLocationList = new FillWLocation(request).fillWLocation(getF_org());
					paycycleList = new FillPayCycles(request).fillPayCycles(CF, uF.parseToInt(getF_org()));
				} else {
					wLocationList = new FillWLocation(request).fillWLocation(orgList.get(0).getOrgId());
					paycycleList = new FillPayCycles(request).fillPayCycles(CF, uF.parseToInt(orgList.get(0).getOrgId()));
				}

			} else {
				wLocationList = new FillWLocation(request).fillWLocation();
				paycycleList = new FillPayCycles(request).fillPayCycles(CF);

			}
			request.setAttribute(PAGE, "/jsp/tms/ImportAttendance.jsp");
			request.setAttribute(TITLE, "Import Attendance");

			
			if (fileUpload != null) {
				//System.out.println("fileUpload"+fileUpload);
				importFileFormatA0001(fileUpload, uF);
				return SUCCESS;
			}
			// if(fileUpload1!=null){
			// format4Attendance(fileUpload1,uF);
			// }
			
		
			if (fileUpload2 != null) {
				//System.out.println("fileUpload2"+fileUpload2);

				ImportFormatCodeA0004(fileUpload2, uF);
				return SUCCESS;
			}
			if (fileUpload3 != null) {
				//System.out.println("fileUpload3"+fileUpload3);
				ImportFormatCodeA0003(fileUpload3, uF);
				return SUCCESS;
			}
			if (fileUpload4 != null) {
				//System.out.println("fileUpload4"+fileUpload4);
				ImportFormatCodeA0006(fileUpload4, uF);
				return SUCCESS;
			}
			if (fileUpload5 != null) {
				//System.out.println("fileUpload5"+fileUpload5);
				ImportFormatCodeA0005(fileUpload5, uF);
				return SUCCESS;
			}
			if (fileUpload6 != null) {
				//System.out.println("fileUpload6"+fileUpload6);
				ImportFormatCodeA0009(fileUpload6, uF);
				return SUCCESS;
			}
			if (fileUpload7 != null) {
				//System.out.println("fileUpload7"+fileUpload7);
				ImportFormatCodeA0011(fileUpload7, uF);
				return SUCCESS;
			}
			if (fileUpload8 != null) {
				//System.out.println("fileUpload8"+fileUpload8);
				ImportFormatCodeA0012(fileUpload8, uF);
				return SUCCESS;
			}
			if (fileUploadNew3 != null) {
				//System.out.println("fileUpload3"+fileUpload3);
				ImportFormatCodeNew(fileUploadNew3, uF);
				return SUCCESS;
			}
			if (fileUploadNew4 != null) {
				//System.out.println("fileUpload4"+fileUpload4);
				ImportCodeNewFourthFormat(fileUploadNew4, uF);
				return SUCCESS;
			}

			if (fileUploadZicon != null) {
				//System.out.println("fileUploadZicon"+fileUploadZicon);
				importFileFormatZicon(fileUploadZicon, uF);
				return SUCCESS;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return LOAD;
	}

	// --------------------Added by
	// M@yuri.B---------------------------------------------
	private void ImportCodeNewFourthFormat(File path, UtilityFunctions uF) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		PreparedStatement pst1 = null;
		ResultSet rs1 = null;
		String dateFormat = "dd/MM/yyyy";
		String timeFormat = "HH:mm:ss";
		FileInputStream fis = null;
		List<String> alErrorList = new ArrayList<String>();
		try {
			con = db.makeConnection(con);
			con.setAutoCommit(false);
			fis = new FileInputStream(path);
			Map<String, String> hmEmpLevel = CF.getEmpLevelMap(con);
			HSSFWorkbook workbook = new HSSFWorkbook(fis);

			//System.out.println("Start Reading Excelsheet.... ");
			HSSFSheet attendanceSheet = workbook.getSheetAt(0);
			List<List<String>> dataList = new ArrayList<List<String>>();
			Iterator rows = attendanceSheet.rowIterator();

			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();

				Iterator cells = row.cellIterator();
				List<String> cellList = new ArrayList<String>();

				while (cells.hasNext()) {
					HSSFCell cell = (HSSFCell) cells.next();
					cellList.add(cell.toString());
				}
				dataList.add(cellList);
			}

			
			pst = con.prepareStatement("select leave_type_id,is_compensatory,leave_type_code,org_id from leave_type");
			rs = pst.executeQuery();
			Map<String, Map<String, List<String>>> leaveTypeOrgMp1 = new HashMap<String, Map<String, List<String>>>();
			while (rs.next()) {
				Map<String, List<String>> leaveTypeOrgMp = leaveTypeOrgMp1.get(rs.getString("org_id"));
				if (leaveTypeOrgMp == null)
					leaveTypeOrgMp = new HashMap<String, List<String>>();

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("leave_type_id"));
				innerList.add(rs.getString("is_compensatory"));

				leaveTypeOrgMp.put(rs.getString("leave_type_code"), innerList);

				leaveTypeOrgMp1.put(rs.getString("org_id"), leaveTypeOrgMp);
			}
			rs.close();
			pst.close();

			String strDate = null;
			String strEndDate = null;
			//System.out.println("datalist for fileformat 4--"+dataList);
			
			if (dataList != null) {
				strDate = dataList.get(1).get(2).toString();
				strEndDate = dataList.get(dataList.size() - 1).get(2).toString();
			}

			boolean flag = false;

			for (int i = 1; i < dataList.size(); i++) {
				int emp_per_id = 0;
				int servic_id = 0;
				int org_id = 0;
				String wlocation = null;
				List<String> cellList = dataList.get(i);

				String cell1 = cellList.get(1);
				String cell2 = cellList.get(2);
				String cell3 = cellList.get(3);
				String cell4 = cellList.get(4);

				String date = cell2.toString();
				String dataType = cell3.toString();
				String empcode = cell1.toString();

				//System.out.println("empcode---------------->" + cell1.toString());
				//System.out.println("date---------------->" + cell2.toString());
				//System.out.println("empcode---------------->" + cell1.toString());

				if (empcode.contains(".")) {
					empcode = empcode.substring(0, empcode.indexOf("."));
				}
				//System.out.println("empcode123----->" + empcode);

				if (empcode != null && !empcode.trim().equals("") && !empcode.trim().equalsIgnoreCase("NULL")) {
					// Select Employ ID
					pst = con.prepareStatement("select emp_per_id,service_id,org_id,wlocation_id "
							+ "from employee_personal_details epd,employee_official_details eod "
							+ "where epd.emp_per_id=eod.emp_id and epd.emp_per_id>0 and empcode=? " + "and (empcode is not null or empcode!='')");
					pst.setString(1, empcode.trim());
					//System.out.println("pst empcode------->" + pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						emp_per_id = rs.getInt(1);
						if (rs.getString("service_id") != null && !rs.getString("service_id").trim().equals("") && !rs.getString("service_id").trim().equalsIgnoreCase("NULL")) {
							String[] str = rs.getString("service_id").split(",");
							for (int k = 0; str != null && k < str.length; k++) {
								if (uF.parseToInt(str[k]) > 0) {
									servic_id = uF.parseToInt(str[k]);
									break;
								}
							}
						}
						org_id = rs.getInt("org_id");
						wlocation = rs.getString("wlocation_id");
					}
					rs.close();
					pst.close();

					//System.out.println("emp_per_id---------->" + emp_per_id);

					if (emp_per_id > 0) {
						flag = uF.isThisDateValid(cell2, dateFormat);
						//System.out.println("isDate valid----->" + cell2);
						// flag = uF.isThisDatePatternMatch(cell2);

						//System.out.println("isDate valid----->" + flag);

						if (!flag) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check date format for employee code '" + empcode + "' on "
									+ cell2 + ".</li>");
							break;
						}

						if (servic_id == 0) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check SBU for employee code '" + empcode + "'.</li>");
							flag = false;
							break;
						}
						
						boolean checkSalaryFlag = CF.checkSalaryForImportAttendance(con, CF, uF, emp_per_id, cell2, cell2, dateFormat);
						if(checkSalaryFlag){
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Salary already processed for employee code '" + empcode + "' on "+cell2+".</li>");
							flag = false;
							break;
						}
						
						boolean checkAttendanceApproveFlag = CF.checkAttendanceApproveForImportAttendance(con, CF, uF, emp_per_id, cell2, cell2, dateFormat);
						if(checkAttendanceApproveFlag){
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Attendance already approved for employee code '" + empcode + "' on "+cell2+".</li>");
							flag = false;
							break;
						}
						
						boolean checkLeaveFlag = CF.checkLeaveForImportAttendance(con, CF, uF, emp_per_id, cell2, cell2, dateFormat);
						if(checkLeaveFlag){
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Leave already applied for employee code '" + empcode + "' on "+cell2+".</li>");
							flag = false;
							break;
						}
						
						boolean checkAttendanceFlag = CF.checkAttendanceForImportAttendance(con, CF, uF, emp_per_id, cell2, cell2, dateFormat);
						if(checkAttendanceFlag){
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Attendance already existed for employee code '" + empcode + "' on "+cell2+".</li>");
							flag = false;
							break;
						}

						SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
						flag = uF.isThisTimeValid(dataType, format);

						if (!flag) {

							if (!dataType.equalsIgnoreCase("P") && !dataType.equalsIgnoreCase("H") && !dataType.equalsIgnoreCase("WO")
									&& !dataType.equalsIgnoreCase("A")) {
								String dType = dataType;
								if (dataType.contains("/")) {
									dType = dataType.split("/")[0];
								}
								String leaveTypeId = null;
								boolean is_compensatory = false;
								Map<String, List<String>> leaveTypeOrgMp = leaveTypeOrgMp1.get(org_id + "");
								List<String> leaveInnerList = leaveTypeOrgMp.get(dType);
								if (leaveInnerList == null) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" + empcode + "  's leave is not set.</li>");
									flag = false;
									break;
								}
								if (leaveInnerList != null) {
									leaveTypeId = leaveInnerList.get(0);
									is_compensatory = uF.parseToBoolean(leaveInnerList.get(1));
								}

								if (leaveTypeId != null) {
									if (dataType.contains("HD")) {

										alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check Leave Type code for employee code '"
												+ empcode + "' on " + date + ".</li>");
										pst = con
												.prepareStatement("select * from emp_leave_type where level_id = (select level_id from  designation_details dd, grades_details gd where gd.designation_id = dd.designation_id and gd.grade_id = (select grade_id from employee_official_details where emp_id = ?)) and leave_type_id = ? and org_id=? and wlocation_id=?");
										pst.setInt(1, emp_per_id);
										pst.setInt(2, uF.parseToInt(leaveTypeId));
										pst.setInt(3, org_id);
										pst.setInt(4, uF.parseToInt(wlocation));
										rs = pst.executeQuery();
										boolean isCompensate = false;
										boolean isPaid = false;
										while (rs.next()) {
											isPaid = uF.parseToBoolean(rs.getString("is_paid"));
											isCompensate = uF.parseToBoolean(rs.getString("is_compensatory"));
										}
										rs.close();
										pst.close();

										pst = con.prepareStatement("INSERT INTO emp_leave_entry (emp_id,leave_from,leave_to,entrydate,emp_no_of_leave,"
												+ "leave_type_id,reason,approval_from,approval_to_date, ishalfday, session_no, "
												+ "document_attached, is_approved, ispaid,is_compensate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
										pst.setInt(1, emp_per_id);
										pst.setDate(2, uF.getDateFormat(date, DATE_FORMAT));
										pst.setDate(3, uF.getDateFormat(date, DATE_FORMAT));
										pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
										pst.setDouble(5, 0.5);
										pst.setInt(6, uF.parseToInt(leaveTypeId));
										pst.setString(7, "Import attendance Data");
										pst.setDate(8, uF.getDateFormat(date, DATE_FORMAT));
										pst.setDate(9, uF.getDateFormat(date, DATE_FORMAT));
										pst.setBoolean(10, true);
										pst.setString(11, null);
										pst.setString(12, null);
										pst.setInt(13, 1);
										pst.setBoolean(14, isPaid);
										pst.setBoolean(15, isCompensate);
										int x = pst.executeUpdate();
										pst.close();

										if (x > 0) {
											String leave_id = null;
											pst = con.prepareStatement("select max(leave_id)as leave_id from emp_leave_entry");
											rs = pst.executeQuery();
											while (rs.next()) {
												leave_id = rs.getString("leave_id");
											}
											rs.close();
											pst.close();

											if (uF.parseToInt(leave_id) > 0) {

												ManagerLeaveApproval leaveApproval = new ManagerLeaveApproval();
												leaveApproval.setServletRequest(request);
												leaveApproval.setLeaveId(leave_id);
												leaveApproval.setTypeOfLeave("" + uF.parseToInt(leaveTypeId));
												leaveApproval.setEmpId("" + emp_per_id);
												leaveApproval.setIsapproved(1);
												leaveApproval.setApprovalFromTo(date);
												leaveApproval.setApprovalToDate(date);
												leaveApproval.setIsHalfDay(true);
												leaveApproval.insertLeaveBalance(con, pst, rs, uF, uF.parseToInt(hmEmpLevel.get(emp_per_id + "")), CF);
											}
										} else {
											alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check Leave Type code for employee code '"
													+ empcode + "' on " + strDate + ".</li>");
											flag = false;
											break;
										}
									} else {

										alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check Leave Type code for employee code '"
												+ empcode + "' on " + date + ".</li>");
										pst = con
												.prepareStatement("select * from emp_leave_type where level_id = (select level_id from  designation_details dd, grades_details gd where gd.designation_id = dd.designation_id and gd.grade_id = (select grade_id from employee_official_details where emp_id = ?)) and leave_type_id = ? and org_id=? and wlocation_id=?");
										pst.setInt(1, emp_per_id);
										pst.setInt(2, uF.parseToInt(leaveTypeId));
										pst.setInt(3, org_id);
										pst.setInt(4, uF.parseToInt(wlocation));
										rs = pst.executeQuery();
										boolean isCompensate = false;
										boolean isPaid = false;
										while (rs.next()) {
											isPaid = uF.parseToBoolean(rs.getString("is_paid"));
											isCompensate = uF.parseToBoolean(rs.getString("is_compensatory"));
										}
										rs.close();
										pst.close();

										pst = con.prepareStatement("INSERT INTO emp_leave_entry (emp_id,leave_from,leave_to,entrydate,emp_no_of_leave,"
												+ "leave_type_id,reason,approval_from,approval_to_date, ishalfday, session_no, "
												+ "document_attached, is_approved, ispaid,is_compensate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
										pst.setInt(1, emp_per_id);
										pst.setDate(2, uF.getDateFormat(date, DATE_FORMAT));
										pst.setDate(3, uF.getDateFormat(date, DATE_FORMAT));
										pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
										int nAppliedDays = uF.parseToInt(uF.dateDifference(date, DATE_FORMAT, date, DATE_FORMAT,CF.getStrTimeZone()));
										pst.setInt(5, nAppliedDays);
										pst.setInt(6, uF.parseToInt(leaveTypeId));
										pst.setString(7, "Import attendance Data");
										pst.setDate(8, uF.getDateFormat(date, DATE_FORMAT));
										pst.setDate(9, uF.getDateFormat(date, DATE_FORMAT));
										pst.setBoolean(10, false);
										pst.setString(11, null);
										pst.setString(12, null);
										pst.setInt(13, 1);
										pst.setBoolean(14, isPaid);
										pst.setBoolean(15, isCompensate);
										int x = pst.executeUpdate();
										pst.close();

										if (x > 0) {
											String leave_id = null;
											pst = con.prepareStatement("select max(leave_id)as leave_id from emp_leave_entry");
											rs = pst.executeQuery();
											while (rs.next()) {
												leave_id = rs.getString("leave_id");
											}
											rs.close();
											pst.close();

											if (uF.parseToInt(leave_id) > 0) {

												ManagerLeaveApproval leaveApproval = new ManagerLeaveApproval();
												leaveApproval.setServletRequest(request);
												leaveApproval.setLeaveId(leave_id);
												leaveApproval.setTypeOfLeave("" + uF.parseToInt(leaveTypeId));
												leaveApproval.setEmpId("" + emp_per_id);
												leaveApproval.setIsapproved(1);
												leaveApproval.setApprovalFromTo(date);
												leaveApproval.setApprovalToDate(date);
												leaveApproval.setIsHalfDay(false);
												leaveApproval.insertLeaveBalance(con, pst, rs, uF, uF.parseToInt(hmEmpLevel.get(emp_per_id + "")), CF);
											}
										} else {
											alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check Leave Type code for employee code '"
													+ empcode + "' on " + strDate + ".</li>");
											flag = false;
											break;
										}
									}
								}
								flag = true;
							}
						} else {

							flag = uF.isThisTimeValid(dataType, format);
							//System.out.println("ATtendance date forat------>" + cell2);
							// flag = uF.isThisDatePatternMatch(cell2);
							//System.out.println("ATtendance date forat------>" + flag);
							if (!flag) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check date format for employee code '" + empcode + "' on "
										+ cell2 + ".</li>");
								break;
							}

							String _fromTime = cell3.toString();
							String _toTime = cell4.toString();

							String[] a = getRosterEntry(con, pst, rs, uF, emp_per_id, cell2, servic_id, dateFormat);
							String rosterInTime = a[0];
							String rosterOutTime = a[1];

							if (rosterInTime == null || rosterOutTime == null) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check roster time for employee code '" + empcode + "' on "
										+ cell2 + ".</li>");
								flag = false;
								break;
							} else {
								/**
								 * In Entry
								 * */
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check start time format for employee code '" + empcode
										+ "' on " + cell2 + ".</li>");
								pst1 = con
										.prepareStatement("Select atten_id from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=?");
								pst1.setInt(1, emp_per_id);
								pst1.setDate(2, uF.getDateFormat(cell2, dateFormat));
								pst1.setString(3, "IN");
								rs1 = pst1.executeQuery();
								if (rs1.next()) {
									//System.out.println("User Already Attended the Office");
								} else {
									long lStart = uF.getTimeFormat(cell2 + " " + _fromTime, dateFormat + " " + timeFormat).getTime();
									long in = uF.getTimeFormat(cell2 + " " + rosterInTime, dateFormat + " " + timeFormat).getTime();

									//System.out.println("lStart------->" + lStart);
									//System.out.println("in------->" + in);
									pst = con
											.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
													+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
									pst.setInt(1, emp_per_id);
									pst.setTimestamp(2, uF.getTimeStamp(cell2 + " " + _fromTime, dateFormat + " " + timeFormat));
									pst.setString(3, " ");
									pst.setString(4, "IN");
									pst.setInt(5, 1);
									pst.setString(6, " ");
									pst.setNull(7, java.sql.Types.DOUBLE);
									pst.setTimestamp(8, uF.getTimeStamp(cell2 + " " + _fromTime, dateFormat + " " + timeFormat));
									pst.setInt(9, servic_id);

									if (in > 0 && in > lStart) {
										pst.setDouble(10, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));

									} else if (lStart > 0 && lStart > in) {
										pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));

									} else {
										pst.setDouble(10, 0);

									}
									pst.executeUpdate();
									pst.close();
								}
								rs1.close();
								pst1.close();

								/**
								 * Out Entry
								 * */
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check end time format for employee code '" + empcode
										+ "' on " + cell2 + ".</li>");
								pst1 = con
										.prepareStatement("Select atten_id from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=? ");
								pst1.setInt(1, emp_per_id);
								pst1.setDate(2, uF.getDateFormat(cell2, dateFormat));
								pst1.setString(3, "OUT");

								rs1 = pst1.executeQuery();
								if (rs1.next()) {
								} else {

									long lStart = uF.getTimeFormat(cell2 + " " + _toTime, dateFormat + " " + timeFormat).getTime();
									long in = uF.getTimeFormat(cell2 + " " + rosterOutTime, dateFormat + " " + timeFormat).getTime();
									long lEnd = uF.getTimeFormat(cell2 + " " + _fromTime, dateFormat + " " + timeFormat).getTime();

									pst = con
											.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
													+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
									pst.setInt(1, emp_per_id);
									pst.setTimestamp(2, uF.getTimeStamp(cell2 + " " + _toTime, dateFormat + " " + timeFormat));
									pst.setString(3, " ");
									pst.setString(4, "OUT");
									pst.setInt(5, 1);
									pst.setString(6, " ");
									pst.setDouble(7, uF.parseToDouble(uF.getTimeDiffInHoursMins(lEnd, lStart)));
									pst.setTimestamp(8, uF.getTimeStamp(cell2 + " " + _toTime, dateFormat + " " + timeFormat));
									pst.setInt(9, servic_id);

									if (in > 0 && in > lStart) {
										pst.setDouble(10, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));

									} else if (lStart > 0 && lStart > in) {
										pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));

									} else {
										pst.setDouble(10, 0);

									}

									pst.executeUpdate();
									pst.close();
								}
								rs1.close();
								pst1.close();
								flag = true;
							}
						}

					} else {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the employee code '" + empcode + "'.</li>");
						flag = false;
						break;
					}

				} else {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the employee code '" + empcode + "'.</li>");
					flag = false;
					break;
				} // end main for loop
			}
			if (flag) {
				//System.out.println("Commit--------------------->");
				con.commit();
				session.setAttribute(MESSAGE, SUCCESSM + "Attendance Imported Successfully!" + END);
			} else {
				//System.out.println("Rollback--------------------->");
				con.rollback();
				if (alErrorList.size() > 0) {
					alReport.add(alErrorList.get(alErrorList.size() - 1));
				}
				session.setAttribute("alReport", alReport);
				session.setAttribute(MESSAGE, ERRORM + "Attendance not imported. Please check imported file." + END);
			}

		} catch (Exception e) {
			try {
				//System.out.println("Rollback--------------------->");
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if (alErrorList.size() > 0) {
				//System.out.println("In ErrorList--------------------->");
				alReport.add(alErrorList.get(alErrorList.size() - 1));
			}
			session.setAttribute("alReport", alReport);
			session.setAttribute(MESSAGE, ERRORM + "Attendance not imported. Please check imported file." + END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeResultSet(rs);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);
			try {
				fis.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			System.gc();
		}

	}

	private void ImportFormatCodeNew(File path, UtilityFunctions uF) {
		System.out.println("format 3");

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		String dateFormat = "dd/MM/yyyy";
		String timeFormat = "HH:mm:ss";
		FileInputStream fis = null;
		List<String> alErrorList = new ArrayList<String>();
		try {

			con = db.makeConnection(con);
			con.setAutoCommit(false);
			fis = new FileInputStream(path);

			HSSFWorkbook workbook = new HSSFWorkbook(fis);

			HSSFSheet attendanceSheet = workbook.getSheetAt(0);
			List<List<String>> dataList = new ArrayList<List<String>>();
			Iterator rows = attendanceSheet.rowIterator();

			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();

				Iterator cells = row.cellIterator();
				List<String> cellList = new ArrayList<String>();

				while (cells.hasNext()) {
					HSSFCell cell = (HSSFCell) cells.next();
					cellList.add(cell.toString());
				}
				dataList.add(cellList);
			}

			boolean flag = false;
			if (dataList.size() == 0) {
				alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">No Data Available in Sheet.</li>");
				flag = false;
			}else{
				for (int i = 1; i < dataList.size(); i++) {
					List<String> cellList = dataList.get(i);
					
					String cell0 = cellList.get(0).toString();
					//System.out.println("cell0------"+cellList.get(0).toString());
					
					String cell1 = cellList.get(1).toString();
					//System.out.println("cell1------"+cellList.get(1).toString());

					String cell2 = cellList.get(2).toString();
					//System.out.println("cell2------"+cellList.get(2).toString());
					
					String strDate = cellList.get(3).toString();
//					System.out.println("cell3------"+cellList.get(3).toString());
					
					String endDate = cellList.get(4).toString();
					//System.out.println("cell4------"+cellList.get(4).toString());
					
					String strTotalDays = cellList.get(5).toString(); 					
					//ystem.out.println("cell5 :"+uF.parseToDouble(strTotalDays));
					
					String strTotalLeaves = cellList.get(6).toString(); 					
					//System.out.println("cell6 :"+uF.parseToDouble(strTotalLeaves));
					
					String empcode = cell1.toString();
					if (empcode.contains(".")) {
						empcode = empcode.substring(0, empcode.indexOf("."));
						//System.out.println("empcode------"+empcode);
					}
					
					// select EmpCode
					
					pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and upper(epd.empcode)=?");
					pst.setString(1, empcode.trim());
					rs = pst.executeQuery();
					//System.out.println("pst1----------------->"+pst);
					String	org_id = null;
					String empCodeD = null;
					int emp_id = 0;
					while (rs.next()) {
						org_id = rs.getString("org_id");
						empCodeD = rs.getString("empcode");
						emp_id = rs.getInt("emp_id");
					}
					rs.close();
					pst.close();
					
					if (emp_id > 0) {
						String strFinancialYearEnd = null;
						String strFinancialYearStart = null;
						
						flag = uF.isThisDateValid(strDate, DATE_FORMAT);
						if (!flag) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check date format for employee code '"+ empcode+ "' on "+ strDate+ ".</li>");
							break;
						}
						
						flag = uF.isThisDateValid(endDate, DATE_FORMAT);
						if (!flag) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check last date format for employee code '"+ empcode+ "' on "+ endDate+ ".</li>");
							break;
						}
						
						if (uF.parseToDouble(strTotalDays) < 0) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check no. of days for employee code '"+ empcode+ "' on "+ strTotalDays+ ".</li>");
							flag =false;
							break;
						}
						
						if (uF.parseToDouble(strTotalLeaves) < 0) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check no. of leaves for employee code '"+ empcode+ "' on "+ strTotalLeaves+ ".</li>");
							flag =false;
							break;
						}
						
						String[] strPayCycleDate = CF.getPayCycleFromDate(con,uF.getDateFormat(strDate, DATE_FORMAT, DATE_FORMAT), CF.getStrTimeZone(), CF, org_id);
						if(strPayCycleDate !=null && strPayCycleDate[0]!=null && !strPayCycleDate[0].trim().equals("")  && !strPayCycleDate[0].trim().equalsIgnoreCase("NULL")
								 && strPayCycleDate[1]!=null && !strPayCycleDate[1].trim().equals("")  && !strPayCycleDate[1].trim().equalsIgnoreCase("NULL")
								 && strPayCycleDate[2]!=null && !strPayCycleDate[2].trim().equals("")  && !strPayCycleDate[2].trim().equalsIgnoreCase("NULL")){
							String strStartDate=strPayCycleDate[0];
							String strEndDate=strPayCycleDate[1];
							String strPaycycle=strPayCycleDate[2];
							
							boolean checkSalaryFlag = CF.checkSalaryForImportAttendance(con, CF, uF, emp_id, strStartDate, strEndDate, DATE_FORMAT);
							if(checkSalaryFlag){
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
										"Salary already processed for employee code '" + empcode + "' on "+strDate+".</li>");
								flag = false;
								break;
							}
							
							boolean checkAttendanceApproveFlag = CF.checkAttendanceApproveForImportAttendance(con, CF, uF, emp_id, strStartDate, strEndDate, DATE_FORMAT);
							if(checkAttendanceApproveFlag){
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
										"Attendance already approved for employee code '" + empcode + "' on "+strDate+".</li>");
								flag = false;
								break;
							}
							
							/*boolean checkLeaveFlag = CF.checkLeaveForImportAttendance(con, CF, uF, emp_id, strStartDate, strEndDate, DATE_FORMAT);
							if(checkLeaveFlag){
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
										"Leave already applied for employee code '" + empcode + "' on "+strDate+".</li>");
								flag = false;
								break;
							}*/
							
							/*boolean checkAttendanceFlag = CF.checkAttendanceForImportAttendance(con, CF, uF, emp_id, strStartDate, strEndDate, DATE_FORMAT);
							if(checkAttendanceFlag){
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
										"Attendance already existed for employee code '" + empcode + "' on "+strDate+".</li>");
								flag = false;
								break;
							}*/
							
							String []strFinancialYear = CF.getFinancialYear(con, strStartDate, CF, uF);
							if(strFinancialYear!=null){
								strFinancialYearStart = strFinancialYear[0];
								strFinancialYearEnd = strFinancialYear[1];
							}
							
							Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
							cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strStartDate, DATE_FORMAT, "dd")));
							cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strStartDate, DATE_FORMAT, "MM"))-1);
							cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strStartDate, DATE_FORMAT, "yyyy")));
							double dblTotalNumberOfDays = cal.getActualMaximum(Calendar.DATE);
							double dblAbsent = dblTotalNumberOfDays - uF.parseToDouble(strTotalDays) - uF.parseToDouble(strTotalLeaves);
							double dblPaidDays = uF.parseToDouble(strTotalDays) + uF.parseToDouble(strTotalLeaves);
							
							pst = con.prepareStatement("select * from approve_attendance where approve_from =? and approve_to=? and  paycycle=? and emp_id=?");
							pst.setDate(1, uF.getDateFormat(strStartDate, DATE_FORMAT));
							pst.setDate(2, uF.getDateFormat(strEndDate, DATE_FORMAT));
							pst.setInt(3, uF.parseToInt(strPaycycle));
							pst.setInt(4, emp_id);
//							System.out.println("pst2------------"+pst);
							rs = pst.executeQuery();
							boolean flag1 = false;
							if(rs.next()) {
								flag1 = true;
							}
							rs.close();
							pst.close();
//							System.out.println("flag1 ===>> " + flag1);
							
							if(!flag1) {
								pst = con.prepareStatement("insert into approve_attendance (emp_id,financial_year_start,financial_year_end,approve_from," +
										"approve_to,paycycle,total_days,paid_days,present_days,paid_leaves,absent_days,approve_by,approve_date) " +
										"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
								pst.setInt(1, emp_id);
								pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
								pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
								pst.setDate(4, uF.getDateFormat(strStartDate, DATE_FORMAT));
								pst.setDate(5, uF.getDateFormat(strEndDate, DATE_FORMAT));
								pst.setInt(6, uF.parseToInt(strPaycycle));
								pst.setDouble(7, dblTotalNumberOfDays);
								pst.setDouble(8, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblPaidDays)));
								pst.setDouble(9, uF.parseToDouble(strTotalDays));
								pst.setDouble(10, uF.parseToDouble(strTotalLeaves));
								pst.setDouble(11, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAbsent)));
								pst.setInt(12, uF.parseToInt(strSessionEmpId));
								pst.setDate(13, uF.getCurrentDate(CF.getStrTimeZone()));
								//System.out.println("pst Insert------------"+pst);
								pst.executeUpdate();
								pst.close();
								
								flag = true;
							} else {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Attendace already exist for these dates for employee code '"+ empcode+ "' "+ strDate+ " to "+ endDate+ ".</li>");
								flag =false;
								break;
							}
						}
					
					} else {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the employee code '"+ empcode + "'.</li>");
						flag = false;
						break;
					}
			   } // end main for loop
		   }
			
			System.out.println("import Att 3 flag ===>> " + flag);
			if (flag) {
				con.commit();
				session.setAttribute(MESSAGE, SUCCESSM+ "Attendance Imported Successfully!" + END);
			} else {
				con.rollback();
				if (alErrorList.size() > 0) {
					alReport.add(alErrorList.get(alErrorList.size() - 1));
				}
				session.setAttribute("alReport", alReport);
				session.setAttribute(MESSAGE,ERRORM+ "Attendance not imported. Please check imported file."+ END);
			}
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if (alErrorList.size() > 0) {
				alReport.add(alErrorList.get(alErrorList.size() - 1));
			}
			session.setAttribute("alReport", alReport);
			session.setAttribute(MESSAGE,ERRORM+ "Attendance not imported. Please check imported file."+ END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
			try {
				fis.close();
			} catch (Exception ex) {

			}
		}
		
	}

	public void ImportFormatCodeA0012(File path, UtilityFunctions uF) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		PreparedStatement pst1 = null;
		ResultSet rs1 = null;
		BufferedReader br = null;
		try {

			con = db.makeConnection(con);

			int emp_per_id = 0;
			int servic_id = 0;
			br = new BufferedReader(new FileReader(path));
			String line = null;
			String dateformat = "MM/dd/yyyy";
			String timeFormat = "HH:mm:ss";
			while ((line = br.readLine()) != null) {
				try {
					List<String> cellList = Arrays.asList(line.split(";"));

					if (cellList.size() < 14) {
						continue;
					}
					String cell1 = cellList.get(1);

					String cell2 = cellList.get(9);
					String cell3 = cellList.get(10);
					String cell4 = cellList.get(12);
					String cell5 = cellList.get(13);

					String empcode = cell1.toString();
					if (empcode.contains(".")) {
						empcode = empcode.substring(0, empcode.indexOf("."));
					}

					// Select Employ ID
					pst = con.prepareStatement("Select emp_per_id from employee_personal_details where empcode=?");
					pst.setString(1, uF.parseToInt(empcode.trim()) + "");
					rs = pst.executeQuery();
					while (rs.next()) {
						emp_per_id = rs.getInt(1);
					}
					rs.close();
					pst.close();

					// =================================================================================================================================
					String inTime = null;
					String outTime = null;
					pst = con.prepareStatement("Select * from roster_details where emp_id=? and _date=?");
					pst.setInt(1, emp_per_id);
					pst.setDate(2, uF.getDateFormat(cell2, dateformat));
					rs = pst.executeQuery();
					while (rs.next()) {
						inTime = rs.getString("_from");
						outTime = rs.getString("_to");
					}
					rs.close();
					pst.close();

					// =================================================================================================================================
					pst = con.prepareStatement("Select service_id from employee_official_details where emp_id=?");
					pst.setInt(1, emp_per_id);
					rs = pst.executeQuery();
					if (rs.next()) {
						if (rs.getString("service_id") != null && !rs.getString("service_id").trim().equals("") && !rs.getString("service_id").trim().equalsIgnoreCase("NULL")) {
							String[] str = rs.getString("service_id").split(",");
							for (int k = 0; str != null && k < str.length; k++) {
								if (uF.parseToInt(str[k]) > 0) {
									servic_id = uF.parseToInt(str[k]);
									break;
								}
							}
						}
					}
					rs.close();
					pst.close();

					pst1 = con.prepareStatement("Select atten_id from attendance_details where emp_id=? and in_out_timestamp=? and in_out='IN'");
					pst1.setInt(1, emp_per_id);
					pst1.setDate(2, uF.getDateFormat(cell2, dateformat));
					rs1 = pst.executeQuery();
					if (rs1.next()) {
						alReport.add(cell1.toString() + "Already IN Attand the Office");
					} else {

						long lStart = uF.getTimeFormat(cell3, timeFormat).getTime();

						long in = 0;

						in = uF.getTimeFormat(inTime, CF.getStrReportTimeFormat()).getTime();

						pst = con.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
								+ "in_out_timestamp_actual,service_id,early_late,atten_id)values(?,?,?,?,?,?,?,?,?,?,?)");
						pst.setInt(1, emp_per_id);
						pst.setTimestamp(2, uF.getTimeStamp(cell2 + " " + cell3, dateformat + " " + timeFormat));
						pst.setString(3, " ");
						pst.setString(4, "IN");
						pst.setInt(5, 0);
						pst.setString(6, " ");
						pst.setNull(7, java.sql.Types.DOUBLE);
						pst.setTimestamp(8, uF.getTimeStamp(cell2 + " " + cell3, dateformat + " " + timeFormat));
						pst.setInt(9, servic_id);
						pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));
						pst.executeUpdate();
						pst.close();

						alReport.add(cell1.toString() + " IN Attendance Inserted Successfully !!");

					}
					rs1.close();
					pst1.close();

					pst1 = con.prepareStatement("Select atten_id from attendance_details where emp_id=? and in_out_timestamp=? and in_out='OUT'");
					pst1.setInt(1, emp_per_id);
					pst1.setDate(2, uF.getDateFormat(cell2, dateformat));
					rs1 = pst1.executeQuery();
					if (rs1.next()) {
						alReport.add(cell1.toString() + "Already OUT Attand the Office");
					} else {
						long lStart = uF.getTimeFormat(cell3, timeFormat).getTime();
						// long lStart = getTimeFormat(cell3, "HH:mm:ss");
						long lEnd = uF.getTimeFormat(cell5, timeFormat).getTime();

						long out = 0;

						out = uF.getTimeFormat(outTime, CF.getStrReportTimeFormat()).getTime();

						pst = con.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
								+ "in_out_timestamp_actual,service_id,early_late,atten_id)values(?,?,?,?,?,?,?,?,?,?,?)");
						pst.setInt(1, emp_per_id);
						pst.setTimestamp(2, uF.getTimeStamp(cell4 + " " + cell5, dateformat + " " + timeFormat));
						pst.setString(3, " ");
						pst.setString(4, "OUT");
						pst.setInt(5, 0);
						pst.setString(6, " ");
						pst.setDouble(7, uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, lEnd)));
						pst.setTimestamp(8, uF.getTimeStamp(cell4 + " " + cell5, dateformat + " " + timeFormat));
						pst.setInt(9, servic_id);
						pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(out, lEnd)));
						pst.executeUpdate();
						pst.close();

						alReport.add(cell1.toString() + " OUT Attendance Inserted Successfully !!");

					}
					rs1.close();
					pst1.close();

					session.setAttribute("alReport", alReport);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null) {
						try {
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					if (rs1 != null) {
						try {
							rs1.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					if (pst != null) {
						try {
							pst.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					if (pst1 != null) {
						try {
							pst1.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			} // end main for loop
			br.close();
		}// try block end

		catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeResultSet(rs);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);

			System.gc();
		}

	}

	public File getFileUpload7() {
		return fileUpload7;
	}

	public void setFileUpload7(File fileUpload7) {
		this.fileUpload7 = fileUpload7;
	}

	public void ImportFormatCodeA0011(File path, UtilityFunctions uF) {
		// System.out.println("ImportFormatCodeA0011====");
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		PreparedStatement prepareStatement = null;
		ResultSet rs = null;
		String dateFormat = "yyyy-MM-dd";
		String timeFormat = "HH:mm:ss";
		SimpleDateFormat smft = new SimpleDateFormat(dateFormat);
		ImportAttendance1 importAttendance1 = new ImportAttendance1();
		importAttendance1.CF = CF;
		BufferedReader br = null;
		try {
			String[] strPayCycleDates = null;
			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
			}
			String strD1 = strPayCycleDates[0];
			String strD2 = strPayCycleDates[1];
			java.sql.Date D1 = uF.getDateFormat(strD1, DATE_FORMAT);
			java.sql.Date D2 = uF.getDateFormat(strD2, DATE_FORMAT);
			java.util.Date dt1 = new java.util.Date(D1.getTime());
			java.util.Date dt2 = new java.util.Date(D2.getTime());
			con = db.makeConnection(con);
			prepareStatement = con.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
					+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");

			pst = con
					.prepareStatement("select emp_per_id,empcode,org_id,service_id,wlocation_id,joining_date,employment_end_date,biometrix_id from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id and org_id=? and wlocation_id=?  order by org_id");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			Map<String, String> empMp = new HashMap<String, String>();
			Map<String, String> empServiceMp = new HashMap<String, String>();
			Map<String, String> empCodeMp = new HashMap<String, String>();
			rs = pst.executeQuery();
			while (rs.next()) {
				empMp.put(rs.getString("empcode"), rs.getString("emp_per_id"));
				
				String strEmpServiceId = "0";
				if (rs.getString("service_id") != null && !rs.getString("service_id").trim().equals("") && !rs.getString("service_id").trim().equalsIgnoreCase("NULL")) {
					String[] str = rs.getString("service_id").split(",");
					for (int z = 0; str != null && z < str.length; z++) {
						if (uF.parseToInt(str[z]) > 0) {
							strEmpServiceId = str[z];
						}
					}
				}				
				empServiceMp.put(rs.getString("emp_per_id"),strEmpServiceId);
				
				empCodeMp.put(rs.getString("emp_per_id"), rs.getString("empcode"));
			}
			rs.close();
			pst.close();

			// Map<String, String> hmEmpLevelMap = new HashMap<String,
			// String>();
			//
			// pst =
			// con.prepareStatement("select * from level_details ld right join (select * from designation_details dd right join (select *, gd.designation_id as designationid from employee_official_details eod, grades_details gd where gd.grade_id=eod.grade_id and org_id=? and wlocation_id=?) a on a.designationid=dd.designation_id) a on a.level_id=ld.level_id");
			// pst.setInt(1,uF.parseToInt(getF_org()));
			// pst.setInt(2,uF.parseToInt(getwLocation()));
			// rs = pst.executeQuery();
			//
			// while (rs.next()) {
			// hmEmpLevelMap.put(rs.getString("emp_id"),
			// rs.getString("level_id"));
			// }

			pst = con
					.prepareStatement("select wlocation_start_time,wlocation_end_time,wlocation_weeklyoff1,wlocation_weeklyoff2,wlocation_weeklyoff3,wlocation_weeklyofftype1,wlocation_weeklyofftype2,wlocation_weeklyofftype3 from work_location_info where  org_id=? and wlocation_id=?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			rs = pst.executeQuery();
			List<String> wlocationData = new ArrayList<String>();
			while (rs.next()) {
				wlocationData.add(rs.getString("wlocation_start_time"));
				wlocationData.add(rs.getString("wlocation_end_time"));

			}
			rs.close();
			pst.close();

			Map<String, List<String>> RosterMp = new HashMap<String, List<String>>();

			pst = con
					.prepareStatement("Select roster_id,rd.emp_id, _date,_from,_to,rd.service_id,actual_hours from roster_details rd,employee_official_details eod where rd.emp_id=eod.emp_id and org_id=? and wlocation_id=? and _date between ? and ?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			pst.setDate(3, D1);
			pst.setDate(4, D2);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("roster_id"));
				innerList.add(rs.getString("_from"));
				innerList.add(rs.getString("_to"));
				innerList.add(rs.getString("service_id"));
				innerList.add(rs.getString("actual_hours"));

				RosterMp.put(rs.getString("emp_id") + "_" + rs.getString("_date"), innerList);

			}
			rs.close();
			pst.close();

			pst = con
					.prepareStatement("Select count(*) as attendance_count,ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd') as attendance_date from attendance_details "
							+ " ad,employee_official_details eod where ad.emp_id=eod.emp_id and org_id=? and wlocation_id=? "
							+ "and to_date(in_out_timestamp::text,'yyyy-MM-dd') between  ?  and ?"
							+ " group by ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd')");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			pst.setDate(3, D1);
			pst.setDate(4, D2);
			rs = pst.executeQuery();
			Map<String, String> empAttendanceMp = new HashMap<String, String>();
			while (rs.next()) {
				empAttendanceMp.put(rs.getString("emp_id") + "_" + rs.getString("attendance_date"), rs.getString("attendance_count"));
			}
			rs.close();
			pst.close();

			// StringBuilder sb=new
			// StringBuilder("Select in_out_timestamp,ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd') as attendance_date,,to_char(in_out_timestamp,'HH24:MI:SS') as attendancetime from attendance_details "
			// +
			// " ad,employee_official_details eod where in_out='IN' and ad.emp_id=eod.emp_id  "
			// +
			// "and to_date(in_out_timestamp::text,'yyyy-MM-dd') between  ?  and ?"
			// +
			// " ");
			//
			// if(uF.parseToInt(getF_org())>0){
			// sb.append(" and org_id= "+getF_org());
			// }
			// if(uF.parseToInt(getwLocation())>0){
			// sb.append(" and wlocation_id= "+getwLocation());
			// }

			pst = con
					.prepareStatement("Select in_out_timestamp,ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd') as attendance_date,,to_char(in_out_timestamp,'HH24:MI:SS') as attendancetime from attendance_details "
							+ " ad,employee_official_details eod where in_out='IN' and ad.emp_id=eod.emp_id  and org_id=? and wlocation_id=?"
							+ "and to_date(in_out_timestamp::text,'yyyy-MM-dd') between  ?  and ?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			pst.setDate(3, D1);
			pst.setDate(4, D2);
			Map<String, String> empAttendanceMp1 = new HashMap<String, String>();
			while (rs.next()) {
				empAttendanceMp1.put(rs.getString("emp_id") + "_" + rs.getString("attendance_date"), rs.getString("attendancetime"));
			}
			rs.close();
			pst.close();

			br = new BufferedReader(new FileReader(path));
			String line = null;
			// int cnt=0;
			while ((line = br.readLine()) != null) {
				// if(cnt==0){
				// cnt++;
				// continue;
				// }
				try {
					List<String> cellList = Arrays.asList(line.split("\t"));
					if (cellList.size() < 2) {
						cellList = Arrays.asList(line.split(","));
					}
					if (cellList.size() < 2) {
						cellList = Arrays.asList(line.split(" "));
					}
					if (cellList.size() < 2) {
						continue;
					};

					String Code = cellList.get(0);
					String strDate = Code.substring(14, 18) + "-" + Code.substring(12, 14) + "-" + Code.substring(10, 12);
					if (!uF.isDateBetween(dt1, dt2, smft.parse(strDate))) {
						continue;
					}

					String strInTime = Code.substring(4, 6) + ":" + Code.substring(6, 8) + ":" + Code.substring(8, 10);
					String empcode = Code.substring(18, Code.length());
					String emp_per_id = empMp.get(empcode);
					// System.out.println("empcode===>"+empcode);
					// System.out.println("strDate===>"+strDate);
					// System.out.println("strInTime===>"+strInTime);

					// pst = con
					// .prepareStatement("Select emp_per_id,service_id,wlocation_id from employee_personal_details,employee_official_details where emp_per_id=emp_id and empcode=?");
					// pst.setString(1,empcode.trim());
					// rs = pst.executeQuery();
					// while (rs.next()) {
					// emp_per_id = rs.getString("emp_per_id");
					//
					// String temp = rs.getString("service_id");
					//
					// if (temp.contains(",")) {
					// String str[] = temp.split(",");
					// servic_id = uF.parseToInt(str[0]);
					//
					// } else {
					// servic_id = uF.parseToInt(rs.getString(1));
					// }
					//
					// }

					if (emp_per_id == null) {
						continue;
					}
					int servic_id = uF.parseToInt(empServiceMp.get(emp_per_id));
					// =================================================================================================================================
					List<String> roster = RosterMp.get(emp_per_id + "_" + strDate);
					// String[] a= getRosterEntry(con,pst,rs,uF,
					// uF.parseToInt(emp_per_id), strDate,servic_id,dateFormat);
					String rosterInTime = null;
					String rosterOutTime = null;
					if (roster != null) {
						rosterInTime = roster.get(1);
						rosterOutTime = roster.get(2);
					} else {
						rosterInTime = wlocationData.get(0);
						rosterOutTime = wlocationData.get(1);
					}

					if (rosterInTime == null || rosterOutTime == null)
						continue;

					int att = uF.parseToInt(empAttendanceMp.get(emp_per_id + "_" + strDate));
					// System.out.println("att==="+att);
					// int att=0;
					// pst=con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and emp_id=?  ");
					// pst.setDate(1,uF.getDateFormat(strDate, dateFormat));
					// pst.setInt(2,uF.parseToInt(emp_per_id));
					// rs=pst.executeQuery();
					// while(rs.next()){
					// att++;
					// }
					if (att == 0) {
						empAttendanceMp.put(emp_per_id + "_" + strDate, "1");
						empAttendanceMp1.put(emp_per_id + "_" + strDate, strInTime);
						importAttendance1.insertINEntry(con, prepareStatement, rs, uF, uF.parseToInt(emp_per_id), servic_id, strDate, strInTime, rosterInTime,
								dateFormat, timeFormat);

					} else if (att == 1) {
						String inTime = empAttendanceMp1.get(emp_per_id + "_" + strDate);
						empAttendanceMp.put(emp_per_id + "_" + strDate, "2");
						importAttendance1.insertOUTEntry(con, prepareStatement, rs, uF, uF.parseToInt(emp_per_id), servic_id, strDate, strInTime,
								rosterOutTime, inTime, dateFormat, timeFormat);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			} // end main for loop
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeStatements(prepareStatement);
			db.closeConnection(con);
		}

	}

	public File getFileUpload6() {
		return fileUpload6;
	}

	public void setFileUpload6(File fileUpload6) {
		this.fileUpload6 = fileUpload6;
	}

	public String getFileUpload6ContentType() {
		return fileUpload6ContentType;
	}

	public void setFileUpload6ContentType(String fileUpload6ContentType) {
		this.fileUpload6ContentType = fileUpload6ContentType;
	}

	public String getFileUpload6FileName() {
		return fileUpload6FileName;
	}

	public void setFileUpload6FileName(String fileUpload6FileName) {
		this.fileUpload6FileName = fileUpload6FileName;
	}

	public void ImportFormatCodeA0009(File path, UtilityFunctions uF) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		// String dateFormat="dd/MM/yyyy";
		String timeFormat = "HH:mm:ss";
		String dateFormat1 = "dd/MM/yyyy HH:mm:ss";
		BufferedReader br = null;
		try {

			con = db.makeConnection(con);
			con.setAutoCommit(true);
			br = new BufferedReader(new FileReader(path));
			String line = null;
			// int cnt=0;
			// String strDate1=null;
			// String strEndDate1=null;

			String[] strPayCycleDates = null;
			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
				strPayCycleDates = CF.getPrevPayCycle(con, strPayCycleDates[1], CF.getStrTimeZone(), CF);
			}
			String strD1 = strPayCycleDates[0];
			String strD2 = strPayCycleDates[1];

			// =============================================================================================

			Map<String, String> empMp = new HashMap<String, String>();
			Map<String, String> empOrgMp = new HashMap<String, String>();
			Map<String, String> empjoiningDateMp = new HashMap<String, String>();
			Map<String, String> empEndMp = new HashMap<String, String>();
			Map<String, String> empServiceMp = new HashMap<String, String>();

			Map<String, String> empCodeMp = new HashMap<String, String>();
			List<String> empList = new ArrayList<String>();

			// pst =
			// con.prepareStatement("select emp_per_id,empcode,org_id,service_id,wlocation_id,joining_date,employment_end_date from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id "
			// +
			// "" +
			// "and org_id=? and wlocation_id=?  order by org_id");
			StringBuilder sb = new StringBuilder(
					"select emp_per_id,empcode,org_id,service_id,wlocation_id,joining_date,employment_end_date from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id ");

			if (uF.parseToInt(getF_org()) > 0) {
				sb.append(" and org_id= " + getF_org());
			}
			if (uF.parseToInt(getwLocation()) > 0) {
				sb.append(" and wlocation_id= " + getwLocation());
			}

			pst = con.prepareStatement(sb.toString());

			// pst.setInt(1,uF.parseToInt(getF_org()));
			// pst.setInt(2,uF.parseToInt(getwLocation()));

			rs = pst.executeQuery();
			while (rs.next()) {
				empList.add(rs.getString("emp_per_id"));
				empMp.put(rs.getString("empcode"), rs.getString("emp_per_id"));
				empOrgMp.put(rs.getString("emp_per_id"), rs.getString("org_id"));
				
				String strEmpServiceId = "0";
				if (rs.getString("service_id") != null && !rs.getString("service_id").trim().equals("") && !rs.getString("service_id").trim().equalsIgnoreCase("NULL")) {
					String[] str = rs.getString("service_id").split(",");
					for (int z = 0; str != null && z < str.length; z++) {
						if (uF.parseToInt(str[z]) > 0) {
							strEmpServiceId = str[z];
						}
					}
				}				
				empServiceMp.put(rs.getString("emp_per_id"),strEmpServiceId);
				
				empEndMp.put(rs.getString("emp_per_id"), rs.getString("employment_end_date"));
				empjoiningDateMp.put(rs.getString("emp_per_id"), rs.getString("joining_date"));
				empCodeMp.put(rs.getString("emp_per_id"), rs.getString("empcode"));
			}
			rs.close();
			pst.close();

			// sb=new
			// StringBuilder("select _date from holidays where _date between ? and ?");
			// if(uF.parseToInt(getF_org())>0){
			// sb.append(" and org_id= "+getF_org());
			// }
			// if(uF.parseToInt(getwLocation())>0){
			// sb.append(" and wlocation_id= "+getwLocation());
			// }
			//

			// pst =
			// con.prepareStatement("select _date from holidays where org_id=? and wlocation_id=? and _date between ? and ?");
			// pst.setInt(1,uF.parseToInt(getF_org()));
			// pst.setInt(2,uF.parseToInt(getwLocation()));
			// pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			// pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			// rs = pst.executeQuery();
			// while (rs.next()) {
			// List<String> holidayList=new ArrayList<String>();
			// holidayList.add(rs.getString("_date"));
			// }
			// System.out.println("pst====>>"+pst);
			// System.out.println("holidayList==>"+holidayList);
			// Map<String,String> leaveMp=new HashMap<String,String>();
			//
			// pst =
			// con.prepareStatement("select leave_type_id,leave_type_name from leave_type");
			// rs = pst.executeQuery();
			// while (rs.next()) {
			// leaveMp.put(rs.getString("leave_type_name"),rs.getString("leave_type_id"));
			//
			// }

			// pst =
			// con.prepareStatement("select wlocation_start_time,wlocation_end_time,wlocation_weeklyoff1,wlocation_weeklyoff2,wlocation_weeklyoff3,wlocation_weeklyofftype1,wlocation_weeklyofftype2,wlocation_weeklyofftype3 from work_location_info where  org_id=? and wlocation_id=?");
			// pst.setInt(1,uF.parseToInt(getF_org()));
			// pst.setInt(2,uF.parseToInt(getwLocation()));
			// rs = pst.executeQuery();
			// String weeklyoff1=null;
			// String weeklyoff2=null;
			// String weeklyoff3=null;
			//
			// String weeklyoff1type=null;
			// String weeklyoff2type=null;
			// String weeklyoff3type=null;
			// List<String> wlocationData=new ArrayList<String>();
			// while (rs.next()) {
			// wlocationData.add(rs.getString("wlocation_start_time"));
			// wlocationData.add(rs.getString("wlocation_end_time"));
			// weeklyoff1=rs.getString("wlocation_weeklyoff1");
			// weeklyoff2=rs.getString("wlocation_weeklyoff2");
			// weeklyoff3=rs.getString("wlocation_weeklyoff3");
			//
			// weeklyoff1type=rs.getString("wlocation_weeklyofftype1");
			// weeklyoff2type=rs.getString("wlocation_weeklyofftype2");
			// weeklyoff3type=rs.getString("wlocation_weeklyofftype3");

			// }

			Map<String, List<String>> RosterMp = new HashMap<String, List<String>>();

			sb = new StringBuilder(
					"Select roster_id,rd.emp_id, _date,_from,_to,rd.service_id,actual_hours from roster_details rd,employee_official_details eod where _date between ? and ? ");

			if (uF.parseToInt(getF_org()) > 0) {
				sb.append(" and org_id= " + getF_org());
			}
			if (uF.parseToInt(getwLocation()) > 0) {
				sb.append(" and wlocation_id= " + getwLocation());
			}

			pst = con.prepareStatement(sb.toString());

			// pst =
			// con.prepareStatement("Select roster_id,rd.emp_id, _date,_from,_to,rd.service_id,actual_hours from roster_details rd,employee_official_details eod where rd.emp_id=eod.emp_id and org_id=? and wlocation_id=? and _date between ? and ?");
			// pst.setInt(1,uF.parseToInt(getF_org()));
			// pst.setInt(2,uF.parseToInt(getwLocation()));
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));

			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("roster_id"));
				innerList.add(rs.getString("_from"));
				innerList.add(rs.getString("_to"));
				innerList.add(rs.getString("service_id"));
				innerList.add(rs.getString("actual_hours"));

				RosterMp.put(rs.getString("emp_id") + "_" + rs.getString("_date"), innerList);

			}
			rs.close();
			pst.close();

			// pst =
			// con.prepareStatement("Select count(*) as attendance_count,ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd') as attendance_date from attendance_details "
			// +
			// " ad,employee_official_details eod where ad.emp_id=eod.emp_id and org_id=? and wlocation_id=? "
			// +
			// "and to_date(in_out_timestamp::text,'yyyy-MM-dd') between  ?  and ?"
			// +
			// " group by ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd')");

			sb = new StringBuilder("Select in_out_timestamp,ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd') as attendance_date from attendance_details "
					+ " ad,employee_official_details eod where in_out='IN' and ad.emp_id=eod.emp_id  "
					+ "and to_date(in_out_timestamp::text,'yyyy-MM-dd') between  ?  and ?" + " ");

			if (uF.parseToInt(getF_org()) > 0) {
				sb.append(" and org_id= " + getF_org());
			}
			if (uF.parseToInt(getwLocation()) > 0) {
				sb.append(" and wlocation_id= " + getwLocation());
			}
			pst = con.prepareStatement(sb.toString());
			// pst =
			// con.prepareStatement("Select in_out_timestamp,ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd') as attendance_date from attendance_details "
			// +
			// " ad,employee_official_details eod where in_out='IN' and ad.emp_id=eod.emp_id and org_id=? and wlocation_id=? "
			// +
			// "and to_date(in_out_timestamp::text,'yyyy-MM-dd') between  ?  and ?"
			// +
			// " ");
			// pst.setInt(1,uF.parseToInt(getF_org()));
			// pst.setInt(2,uF.parseToInt(getwLocation()));
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String, String> empAttendanceMp = new HashMap<String, String>();
			while (rs.next()) {
				empAttendanceMp.put(rs.getString("emp_id") + "_" + rs.getString("attendance_date"), rs.getString("in_out_timestamp"));
			}
			rs.close();
			pst.close();
			// System.out.println("empAttendanceMp==="+empAttendanceMp);
			// Map<String,String> empAttendanceMp1=new HashMap<String,String>();
			// ========================================================================================================================

			// int cnt=0;

			// br.readLine();
			while ((line = br.readLine()) != null) {

				// if(cnt<2000){
				// cnt++;
				// continue;
				// }
				// cnt++;

				List<String> cellList = Arrays.asList(line.split("\t"));
				// System.out.println("cellList=="+cellList.size());
				// try {

				if (cellList.size() < 4) {
					cellList = Arrays.asList(line.split(","));
				}
				if (cellList.size() < 4) {
					continue;
				}
				String empcode = cellList.get(1).trim();

				// System.out.p/rintln("empcode=="+empcode);
				int emp_per_id = uF.parseToInt(empMp.get(empcode));

				if (emp_per_id == 0) {
					continue;
				}
				// if(emp_per_id!=97 || emp_per_id!=91 )
				// continue;

				// System.out.println("emp_per_id==="+emp_per_id);
				// System.out.println("empcode==="+empcode);
				// System.out.println("cnt===>"+cnt);
				String strDate = cellList.get(2).trim();
				String strInTime = uF.getDateFormat(strDate, dateFormat1, timeFormat);
				strDate = uF.getDateFormat(strDate, dateFormat1, DBDATE);

				if (!uF.isDateBetween(uF.getDateFormat(strD1, DATE_FORMAT), uF.getDateFormat(strD2, DATE_FORMAT), uF.getDateFormatUtil(strDate, DBDATE))) {
					continue;
				}

				String type = cellList.get(3).trim();
				int servic_id = uF.parseToInt(empServiceMp.get(empcode));
				// =================================================================================================================================

				String rosterInTime = null;
				String rosterOutTime = null;

				List<String> rosterList = RosterMp.get(emp_per_id + "_" + strDate);
				if (rosterList != null) {
					rosterInTime = rosterList.get(1);
					rosterOutTime = rosterList.get(2);
				} else {
					String[] a = getRosterEntry1(con, pst, rs, uF, emp_per_id, strDate, servic_id, DBDATE);
					rosterInTime = a[0];
					rosterOutTime = a[1];
				}
				// String[] a= getRosterEntry(con,pst,rs,uF, emp_per_id,
				// strDate,servic_id,dateFormat);

				if (rosterInTime == null || rosterOutTime == null)
					continue;

				String inDate = empAttendanceMp.get(emp_per_id + "_" + strDate);

				if (type.equalsIgnoreCase("in")) {
					insertINEntry1(con, pst, rs, uF, emp_per_id, servic_id, strDate, strInTime, rosterInTime, DBDATE, timeFormat);
					empAttendanceMp.put(emp_per_id + "_" + strDate, strDate + " " + strInTime);
				} else if (inDate != null && type.equalsIgnoreCase("out")) {
					insertOUTEntry1(con, pst, rs, uF, emp_per_id, servic_id, strDate, strInTime, rosterOutTime,
							uF.getDateFormat(inDate, DBTIMESTAMP, timeFormat), DBDATE, timeFormat);
				}

				// } catch (Exception e) {
				// e.printStackTrace();
				// }
			} // end main for loop
				// System.out.println("empAttendanceMp1="+empAttendanceMp);
			br.close();
		}// try block end

		catch (Exception e) {

			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);

		}

	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public String getwLocation() {
		return wLocation;
	}

	public void setwLocation(String wLocation) {
		this.wLocation = wLocation;
	}

	public void ImportFormatCodeA0006(File path, UtilityFunctions uF) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		String dateFormat = "dd-MM-yyyy";
		String timeFormat = "HH:mm:ss";
		BufferedReader br = null;
		try {

			con = db.makeConnection(con);
			con.setAutoCommit(true);
			br = new BufferedReader(new FileReader(path));
			String line = null;
			// int cnt=0;
			while ((line = br.readLine()) != null) {
				// if(cnt==0){
				// cnt++;
				// continue;
				// }
				try {
					List<String> cellList = Arrays.asList(line.split(","));
					// if(cnt<4){
					// System.out.println("cellList=="+cellList.size());
					// System.out.println("cellList="+cellList);
					//
					// System.out.println("cellList="+cellList.get(10));
					// System.out.println("cellList="+cellList.get(11));
					//
					// cnt++;
					// }
					if (cellList.size() < 11) {
						continue;
					}
					String empcode = cellList.get(1);
					String strDate = cellList.get(0);
					String strInTime = cellList.get(10);
					String strOutTime = cellList.get(11);
					String emp_per_id = null;
					int servic_id = 0;
					pst = con
							.prepareStatement("Select emp_per_id,service_id,wlocation_id from employee_personal_details,employee_official_details where emp_per_id=emp_id and empcode=?");
					pst.setString(1, empcode.trim());
					rs = pst.executeQuery();
					while (rs.next()) {
						emp_per_id = rs.getString("emp_per_id");

						if (rs.getString("service_id") != null && !rs.getString("service_id").trim().equals("") && !rs.getString("service_id").trim().equalsIgnoreCase("NULL")) {
							String[] str = rs.getString("service_id").split(",");
							for (int k = 0; str != null && k < str.length; k++) {
								if (uF.parseToInt(str[k]) > 0) {
									servic_id = uF.parseToInt(str[k]);
									break;
								}
							}
						}
					}
					rs.close();
					pst.close();

					if (emp_per_id == null) {
						continue;
					}
					// =================================================================================================================================

					String[] a = getRosterEntry(con, pst, rs, uF, uF.parseToInt(emp_per_id), strDate, servic_id, dateFormat);

					String rosterInTime = a[0];
					String rosterOutTime = a[1];

					if (rosterInTime == null || rosterOutTime == null) {
						continue;
					}

					insertINEntry(con, pst, rs, uF, uF.parseToInt(emp_per_id), servic_id, strDate, strInTime, rosterInTime, dateFormat, timeFormat);
					insertOUTEntry(con, pst, rs, uF, uF.parseToInt(emp_per_id), servic_id, strDate, strOutTime, rosterOutTime, strInTime, dateFormat,
							timeFormat);

				} catch (Exception e) {
					e.printStackTrace();
				}
			} // end main for loop
			br.close();
		}// try block end

		catch (Exception e) {

			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);

		}

	}

	public void ImportFormatCodeA0003(File path, UtilityFunctions uF) {

		System.out.println("in ImportFormatCodeA0003===");
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		String dateFormat = "dd/MM/yyyy";
		String timeFormat = "HH:mm:ss";
		FileInputStream fis = null;
		try {

			con = db.makeConnection(con);

			fis = new FileInputStream(path);

			XSSFWorkbook workbook = new XSSFWorkbook(fis);

			XSSFSheet attendanceSheet = workbook.getSheetAt(0);
			List<List<String>> dataList = new ArrayList<List<String>>();
			Iterator rows = attendanceSheet.rowIterator();

			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();

				Iterator cells = row.cellIterator();
				List<String> cellList = new ArrayList<String>();

				while (cells.hasNext()) {
					XSSFCell cell = (XSSFCell) cells.next();
					cellList.add(cell.toString());
				}
				dataList.add(cellList);
			}

			int emp_per_id = 0;
			int servic_id = 0;

			for (int i = 1; i < dataList.size(); i++) {
				try {
					List<String> cellList = dataList.get(i);

					if (cellList.size() < 14) {
						continue;
					}

					String cell1 = cellList.get(1).toString();

					String cell2 = cellList.get(9).toString();
					String cell3 = cellList.get(10).toString();
					// String cell4 = cellList.get(12).toString();
					String cell5 = cellList.get(13).toString();

					String empcode = cell1.toString();
					if (empcode.contains(".")) {
						empcode = empcode.substring(0, empcode.indexOf("."));

					}

					// Select Employ ID
					pst = con.prepareStatement("Select emp_per_id from employee_personal_details where empcode=?");
					pst.setString(1, empcode.trim());
					rs = pst.executeQuery();
					while (rs.next()) {
						emp_per_id = rs.getInt(1);
					}
					rs.close();
					pst.close();

					// System.out.println("emp_per_id===" + emp_per_id);

					// =================================================================================================================================
					String[] a = getRosterEntry(con, pst, rs, uF, emp_per_id, cell2, servic_id, dateFormat);
					String rosterInTime = a[0];
					String rosterOutTime = a[1];

					if (rosterInTime == null || rosterOutTime == null) {
						continue;
					}

					// =================================================================================================================================
					pst = con.prepareStatement("Select service_id from employee_official_details where emp_id=?");
					pst.setInt(1, emp_per_id);
					rs = pst.executeQuery();
					if (rs.next()) {
						if (rs.getString("service_id") != null && !rs.getString("service_id").trim().equals("") && !rs.getString("service_id").trim().equalsIgnoreCase("NULL")) {
							String[] str = rs.getString("service_id").split(",");
							for (int k = 0; str != null && k < str.length; k++) {
								if (uF.parseToInt(str[k]) > 0) {
									servic_id = uF.parseToInt(str[k]);
									break;
								}
							}
						}
					}
					rs.close();
					pst.close();

					String _fromTime = cell3.toString();
					String _toTime = cell5.toString();

					insertINEntry(con, pst, rs, uF, emp_per_id, servic_id, cell2, _fromTime, rosterInTime, dateFormat, timeFormat);

					insertOUTEntry(con, pst, rs, uF, emp_per_id, servic_id, cell2, _toTime, rosterOutTime, _fromTime, dateFormat, timeFormat);

					// alReport.add(cell1.toString()
					// + " Attendance Inserted Successfully !!");
					alReport.add("<li class=\"msg_success\" style=\"margin:0px\">" + empcode + " Attendance Inserted Successfully.</li>");
					session.setAttribute("alReport", alReport);

					// }// end if-else
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null) {
						try {
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					if (pst != null) {
						try {
							pst.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			} // end main for loop

		}// try block end

		catch (Exception e) {

			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
			try {
				fis.close();
			} catch (Exception ex) {

			}
		}

	}

	public void ImportFormatCodeA0005(File path, UtilityFunctions uF) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		// FileInputStream fis = null;
		try {

			con = db.makeConnection(con);
			con.setAutoCommit(true);

			FileInputStream fis = new FileInputStream(path);

			XSSFWorkbook workbook = new XSSFWorkbook(fis);

			// System.out.println("Start Reading Excelsheet.... ");
			XSSFSheet attendanceSheet = workbook.getSheetAt(0);
			List<List<String>> dataList = new ArrayList<List<String>>();
			Iterator rows = attendanceSheet.rowIterator();

			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();

				Iterator cells = row.cellIterator();
				List<String> cellList = new ArrayList<String>();

				while (cells.hasNext()) {
					String cell = cells.next().toString();
					cellList.add(cell);
				}
				dataList.add(cellList);
			}
			fis.close();
			String dateformat = "dd-MMM-yyyy";
			String timeformat = "HH:mm";
			for (int i = 9; i < dataList.size(); i++) {
				List<String> cellList = dataList.get(i);

				if (cellList.size() >= 14) {

					String empcode = cellList.get(1).trim();
					alReport.add("<li class=\"msg_success\" style=\"margin:0px\">" + empcode + " Attendance Inserted Successfully.</li>");

					// alReport.add(empcode+" Has been Imported.");
					String strDate = cellList.get(5);
					String strInTime = cellList.get(8);
					String strOutTime = cellList.get(9);
					// System.out.println("=====>>>"+strDate +" "+strInTime);
					int cnt = 0;
					try {
						uF.getTimeFormat(strDate + " " + strInTime, dateformat + " " + timeformat).getTime();
					} catch (Exception e) {
						cnt++;
					}

					try {
						uF.getTimeFormat(strDate + " " + strOutTime, dateformat + " " + timeformat).getTime();
					} catch (Exception e) {
						cnt++;
					}
					if (cnt == 2)
						continue;
					pst = con
							.prepareStatement("Select emp_per_id,service_id,wlocation_id from employee_personal_details,employee_official_details where emp_per_id=emp_id and empcode=?");
					pst.setString(1, empcode.trim());
					rs = pst.executeQuery();
					// String strWLocation=null;
					String emp_per_id = null;
					int servic_id = 0;
					while (rs.next()) {
						emp_per_id = rs.getString("emp_per_id");
						// strWLocation = rs.getString("wlocation_id");

						if (rs.getString("service_id") != null && !rs.getString("service_id").trim().equals("") && !rs.getString("service_id").trim().equalsIgnoreCase("NULL")) {
							String[] str = rs.getString("service_id").split(",");
							for (int k = 0; str != null && k < str.length; k++) {
								if (uF.parseToInt(str[k]) > 0) {
									servic_id = uF.parseToInt(str[k]);
									break;
								}
							}
						}
					}
					rs.close();
					pst.close();

					String[] a = getRosterEntry(con, pst, rs, uF, uF.parseToInt(emp_per_id), strDate, servic_id, dateformat);

					String rosterInTime = a[0];
					String rosterOutTime = a[1];

					if (rosterInTime == null || rosterOutTime == null) {
						continue;
					}

					insertINEntry(con, pst, rs, uF, uF.parseToInt(emp_per_id), servic_id, strDate, strInTime, rosterInTime, dateformat, timeformat);
					insertOUTEntry(con, pst, rs, uF, uF.parseToInt(emp_per_id), servic_id, strDate, strOutTime, rosterOutTime, strInTime, dateformat,
							timeformat);
				}

			} // end main for loop
		}// try block end

		catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
			try {
				// fis.close();
			} catch (Exception ex) {

			}
		}
		session.setAttribute("alReport", alReport);
	}

	public String getDayName(java.util.Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			return "Sat";
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
			return "Fri";
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
			return "Thu";
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
			return "Wed";
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
			return "Tue";
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
			return "Mon";
		} else {
			return "Sun";
		}

	}

	public void ImportFormatCodeA0004(File path, UtilityFunctions uF) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		String dateFormat = "dd/MM/yyyy";
		String timeFormat = "HH:mm";
		BufferedReader br = null;
		try {

			con = db.makeConnection(con);
			con.setAutoCommit(true);

			br = new BufferedReader(new FileReader(path));
			String line = null;

			while ((line = br.readLine()) != null) {
				try {

					List<String> cellList = Arrays.asList(line.split(","));

					String empcode = null;
					String strStartDate = cellList.get(4).replace("\"", "");
					String strEndDate = cellList.get(6).replace("\"", "");
					// System.out.println("strStartDate===>>"+strStartDate);
					// System.out.println("strEndDate==="+strEndDate);
					List<String> attendanceStatus = new ArrayList<String>();
					List<String> shiftList = new ArrayList<String>();
					List<String> inTimeList = new ArrayList<String>();
					List<String> outTimeList = new ArrayList<String>();
					List<String> lateList = new ArrayList<String>();
					List<String> earlyList = new ArrayList<String>();
					List<String> overtimeList = new ArrayList<String>();
					List<String> dateList = new ArrayList<String>();

					boolean flag = false, flag1 = false, flag2 = false, flag4 = false, flag5 = false, flag6 = false, flag7 = false, flag8 = false;
					int cnt = 0;
					int cnt1 = 0;

					for (int i = 0; i < cellList.size(); i++) {

						if (flag || cellList.get(i).replace("\"", "").equals("Page No.:")) {
							flag = true;

							if (cellList.get(i + 1).replace("\"", "").equalsIgnoreCase(getDayName(uF.getDateFormat(strStartDate, DATE_FORMAT)))) {
								flag = false;
							}
							if (flag) {
								dateList.add(cellList.get(i + 1).replace("\"", ""));
							}
						} else if (cellList.get(i).replace("\"", "").equals("Employee Code")) {
							empcode = cellList.get(i + 1).replace("\"", "");
						} else if (flag1 || cellList.get(i).replace("\"", "").equals("Status")) {
							flag1 = true;

							if (cellList.get(i + 1).replace("\"", "").equalsIgnoreCase("Shift")) {
								flag1 = false;
							}
							if (flag1) {
								attendanceStatus.add(cellList.get(i + 1).replace("\"", ""));
							}

						} else if (flag2 || cellList.get(i).replace("\"", "").equals("Shift")) {
							flag2 = true;

							if (cellList.get(i + 1).replace("\"", "").equalsIgnoreCase("Time In")) {
								flag2 = false;
							}
							if (flag2) {
								shiftList.add(cellList.get(i + 1).replace("\"", ""));
							}

						} else if (flag4 || cellList.get(i).replace("\"", "").equals("Time In")) {
							flag4 = true;

							if (cellList.get(i + 1).replace("\"", "").equalsIgnoreCase("Time Out")) {
								flag4 = false;
							}
							if (flag4) {
								inTimeList.add(cellList.get(i + 1).replace("\"", ""));
							}
						} else if (flag5 || cellList.get(i).replace("\"", "").equals("Time Out")) {
							flag5 = true;

							if (cellList.get(i + 1).replace("\"", "").equalsIgnoreCase("Late")) {
								flag5 = false;
							}
							if (flag5) {
								outTimeList.add(cellList.get(i + 1).replace("\"", ""));
							}
						} else if (flag6 || cellList.get(i).replace("\"", "").equals("Late")) {
							if (cellList.get(i).replace("\"", "").equals("Late")) {
								if (cnt == 0) {
									cnt++;
								} else {
									continue;
								}
							}
							flag6 = true;

							if (cellList.get(i + 1).replace("\"", "").equalsIgnoreCase("Early")) {
								flag6 = false;
							}
							if (flag6) {
								lateList.add(cellList.get(i + 1).replace("\"", ""));
							}
						} else if (flag7 || cellList.get(i).replace("\"", "").equals("Early")) {
							if (cellList.get(i).replace("\"", "").equals("Early")) {
								if (cnt1 == 0) {
									cnt1++;
								} else {
									continue;
								}
							}
							flag7 = true;

							if (cellList.get(i + 1).replace("\"", "").equalsIgnoreCase("OverTime")) {
								flag7 = false;
							}
							if (flag7) {
								earlyList.add(cellList.get(i + 1).replace("\"", ""));
							}
						} else if (flag8 || cellList.get(i).replace("\"", "").equals("OverTime")) {
							flag8 = true;

							if (cellList.get(i + 1).replace("\"", "").equalsIgnoreCase("WorkHours")) {
								flag8 = false;
							}
							if (flag8) {
								overtimeList.add(cellList.get(i + 1).replace("\"", ""));
							}

						}

					}

					// String strWLocation = null;
					if (empcode.contains(".")) {
						empcode = empcode.substring(0, empcode.indexOf("."));

					}

					// // Select Employ ID
					String emp_per_id = null;
					int servic_id = 0;
					pst = con
							.prepareStatement("Select emp_id,service_id,wlocation_id from employee_official_details eod,employee_personal_details epd where eod.emp_id=epd.emp_per_id and empcode=?");
					pst.setString(1, empcode.trim());
					rs = pst.executeQuery();
					while (rs.next()) {
						emp_per_id = rs.getString("emp_id");
						// strWLocation = rs.getString("wlocation_id");

						if (rs.getString("service_id") != null && !rs.getString("service_id").trim().equals("") && !rs.getString("service_id").trim().equalsIgnoreCase("NULL")) {
							String[] str = rs.getString("service_id").split(",");
							for (int k = 0; str != null && k < str.length; k++) {
								if (uF.parseToInt(str[k]) > 0) {
									servic_id = uF.parseToInt(str[k]);
									break;
								}
							}
						}
					}
					rs.close();
					pst.close();

					if (emp_per_id == null) {
						System.out.println("empcode===" + empcode);

						continue;
					}

					String datediff = uF.dateDifference(strStartDate, DATE_FORMAT, strEndDate, DATE_FORMAT,CF.getStrTimeZone());
					Date startDate = uF.getDateFormat(strStartDate, DATE_FORMAT);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(startDate);
					if (uF.parseToInt(datediff) == dateList.size()) {

						for (int i = 0; i < dateList.size(); i++) {

							if (attendanceStatus.get(i) != null
									&& (attendanceStatus.get(i).equalsIgnoreCase("AB") || attendanceStatus.get(i).equalsIgnoreCase("WO"))) {
								calendar.add(Calendar.DATE, 1);
								continue;
							}
							String strCurrentDBDate = calendar.get(Calendar.DATE) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/"
									+ calendar.get(Calendar.YEAR);
							// System.out.println("===" + strCurrentDBDate);
							// System.out.println("dateList==" +
							// dateList.get(i));
							// System.out.println("inTimeList.get(i)===>"+inTimeList.get(i));
							String inTime = null;
							String outTime = null;

							pst = con.prepareStatement("select * from shift_details where shift_code=?");
							pst.setString(1, shiftList.get(i));
							rs = pst.executeQuery();
							while (rs.next()) {
								inTime = rs.getString("_from");
								outTime = rs.getString("_to");
							}
							rs.close();
							pst.close();

							if (inTime == null && outTime == null) {
								continue;
							}

							insertINEntry(con, pst, rs, uF, uF.parseToInt(emp_per_id), servic_id, strCurrentDBDate, inTimeList.get(i), inTime, dateFormat,
									timeFormat);

							// }
							insertOUTEntry(con, pst, rs, uF, uF.parseToInt(emp_per_id), servic_id, strCurrentDBDate, outTimeList.get(i), outTime,
									inTimeList.get(i), dateFormat, timeFormat);

							calendar.add(Calendar.DATE, 1);
							// alReport.add(empcode.toString());
							alReport.add("<li class=\"msg_success\" style=\"margin:0px\">" + empcode + " Attendance Inserted Successfully.</li>");

							session.setAttribute("alReport", alReport);
						}

					}

					// }// end if-else
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null) {
						try {
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					if (pst != null) {
						try {
							pst.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			} // end main for loop
			br.close();
		}// try block end

		catch (Exception e) {

			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);

		}

	}

	// public void format4Attendance(File path,UtilityFunctions uF) {
	//
	// if (!fileUpload1ContentType.contains("text/plain")) {
	// alReport.add("<li class=\"msg_success\" style=\"margin:0px\">Please Check File format.This must be a .txt file</li>");
	//
	// // alReport.add("Please Check File format.This must be a .txt file");
	// return;
	// }
	// Connection con = null;
	// Database db = new Database();
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	// String dateFormat="dd/MM/yyyy";
	// String timeFormat="HH:mm:ss";
	// try {
	//
	// con = db.makeConnection(con);
	// con.setAutoCommit(true);
	//
	// BufferedReader br = new BufferedReader(new FileReader(path));
	// String line = null;
	//
	// while ((line = br.readLine()) != null) {
	// try {
	// List<String> cellList = Arrays.asList(line.split(" "));
	//
	// if (cellList.size() < 6)
	// continue;
	// String empcode = cellList.get(1);
	//
	// String in_out = cellList.get(2);
	// String strDate = cellList.get(4);
	// String strTime = cellList.get(5);
	// System.out.println("empcode==="+empcode);
	// System.out.println("in_out==="+in_out);
	// System.out.println("strDate==="+strDate);
	// System.out.println("strTime==="+strTime);
	//
	// String strWLocation = null;
	// if (empcode.contains(".")) {
	// empcode = empcode.substring(0, empcode.indexOf("."));
	//
	// }
	//
	// // Select Employ ID
	// String emp_per_id = null;
	// int servic_id = 0;
	// pst = con
	// .prepareStatement("Select emp_id,service_id,wlocation_id from employee_official_details where biometrix_id=?");
	// pst.setInt(1, uF.parseToInt(empcode.trim()));
	// rs = pst.executeQuery();
	// while (rs.next()) {
	// emp_per_id = rs.getString("emp_id");
	// strWLocation = rs.getString("wlocation_id");
	//
	// String temp = rs.getString("service_id");
	//
	// if (temp.contains(",")) {
	// String str[] = temp.split(",");
	// servic_id = uF.parseToInt(str[0]);
	//
	// } else {
	// servic_id = uF.parseToInt(rs.getString(1));
	// }
	//
	// }
	//
	// //
	// =================================================================================================================================
	// String[] a= getRosterEntry(con,pst,rs,uF, uF.parseToInt(emp_per_id),
	// strDate,servic_id,dateFormat);
	//
	// String rosterInTime=a[0];
	// String rosterOutTime=a[1];
	//
	//
	// if(rosterInTime==null || rosterOutTime==null )
	// continue;
	//
	//
	// pst =
	// con.prepareStatement("Select atten_id from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=? ");
	// pst.setInt(1,uF.parseToInt( emp_per_id));
	// pst.setDate(2, uF.getDateFormat(strDate, "dd-MM-yyyy"));
	// if(in_out.equalsIgnoreCase("I")){
	// pst.setString(3,"IN");
	//
	// }else if(in_out.equalsIgnoreCase("O")){
	// pst.setString(3,"OUT");
	//
	// }
	// rs = pst.executeQuery();
	// if (rs.next()) {
	// System.out.println("User Already Attand the Office");
	// } else {
	//
	//
	// long lStart = uF.getTimeFormat(strDate +" "+strTime,
	// "dd-MM-yyyy HH:mm").getTime();
	//
	// long in = 0;
	//
	// try {
	//
	// } catch (Exception e) {
	//
	// }
	//
	// if(in_out.equalsIgnoreCase("I")){
	// in =
	// uF.getTimeFormat(strDate+" "+rosterInTime,"dd-MM-yyyy HH:mm:ss").getTime();
	// long lIn = uF.getTimeFormat(strDate +" "+ strTime,
	// "dd-MM-yyyy HH:mm").getTime();
	// long lIn1 = uF.getTimeFormat(strDate +" "+ rosterInTime,
	// "dd-MM-yyyy HH:mm").getTime();
	// System.out.println("lIn=="+lIn);
	// System.out.println("lIn1=="+lIn1);
	// if(lIn1<lIn){
	// updateBreakRegisters(emp_per_id,uF, con, "IN", strDate, rosterInTime,
	// lIn, strTime, strWLocation);
	// }
	//
	//
	// insertINEntry(con,pst,rs,uF, uF.parseToInt( emp_per_id), servic_id,
	// strDate, strTime,rosterInTime,dateFormat,timeFormat);
	//
	// }else if(in_out.equalsIgnoreCase("O")){
	//
	// in =
	// uF.getTimeFormat(strDate+" "+rosterOutTime,"dd-MM-yyyy HH:mm:ss").getTime();
	//
	// long lOut = uF.getTimeFormat(strDate +" "+ strTime,
	// "dd-MM-yyyy HH:mm").getTime();
	// System.out.println("lOut=="+lOut);
	// System.out.println("lOut1=="+in);
	// if(lOut>in){
	// updateBreakRegisters(emp_per_id,uF, con, "OUT", strDate, rosterOutTime,
	// lOut, strTime, strWLocation);
	// }
	// // String _toTime = null;
	// pst =
	// con.prepareStatement("Select * from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=? ");
	// pst.setInt(1,uF.parseToInt( emp_per_id));
	// pst.setDate(2, uF.getDateFormat(strDate, "dd-MM-yyyy"));
	// pst.setString(3,"IN");
	//
	// rs = pst.executeQuery();
	// while (rs.next()) {
	// System.out.println("sjkdsfkjdsf===="+rs.getString("in_out_timestamp"));
	// // _toTime=rs.getString("in_out_timestamp");
	// }
	// // long lEnd =0;
	// // try{
	// // lEnd = uF.getTimeFormat(_toTime,DBTIMESTAMP).getTime();
	// // }catch(Exception e){}
	// pst.close();
	//
	// insertOUTEntry(con,pst,rs,uF, uF.parseToInt( emp_per_id), servic_id,
	// strDate, strTime, rosterOutTime,strTime,dateFormat,timeFormat);
	//
	//
	// System.out.println("lStart="+new Date(lStart));
	// System.out.println("in="+new Date(in));
	//
	// }
	// // alReport.add(empcode.toString());
	// alReport.add("<li class=\"msg_success\" style=\"margin:0px\">" + empcode
	// + " Attendance Inserted Successfully.</li>");
	//
	// request.setAttribute("alReport", alReport);
	//
	// }// end if-else
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// } // end main for loop
	// br.close();
	// }// try block end
	//
	// catch (Exception e) {
	//
	// e.printStackTrace();
	// } finally {
	// db.closeConnection(con);
	//
	// }
	//
	// }

	public void importFileFormatA0001(File path, UtilityFunctions uF) {
//		System.out.println("in importFileFormatA0001==");
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		PreparedStatement pst1 = null;
		ResultSet rs1 = null;
		String dateFormat = "dd/MM/yyyy";
		String timeFormat = "HH:mm:ss";
		FileInputStream fis = null;
		List<String> alErrorList = new ArrayList<String>();
		try {
			con = db.makeConnection(con);
			con.setAutoCommit(false);
			fis = new FileInputStream(path);
			
			HSSFWorkbook workbook = new HSSFWorkbook(fis);

//			System.out.println("Start Reading Excelsheet.... ");
			// XSSFSheet attendanceSheet = workbook.getSheet("Sheet1");
			HSSFSheet attendanceSheet = workbook.getSheetAt(0);
			List<List<String>> dataList = new ArrayList<List<String>>();
			Iterator rows = attendanceSheet.rowIterator();

			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();

				Iterator cells = row.cellIterator();
				List<String> cellList = new ArrayList<String>();

				while (cells.hasNext()) {
					HSSFCell cell = (HSSFCell) cells.next();
					cellList.add(uF.getCellString(cell, workbook,dateFormat,timeFormat));// @author Dattatray 
//					cellList.add(cell.toString());
				}
				dataList.add(cellList);
			}

			boolean flag = false;
			
//			System.out.println("dataList ===>> " + dataList);
			for (int i = 1; i < dataList.size(); i++) {
				int emp_per_id = 0;
				int servic_id = 0;
				List<String> cellList = dataList.get(i);

				String cell1 = cellList.get(1);
				String cell2 = cellList.get(2);
				String cell4 = cellList.get(3);
				String cell5 = cellList.get(4);

				String empcode = cell1.toString();
				if (empcode.contains(".")) {
					empcode = empcode.substring(0, empcode.indexOf("."));
				}

				// Select Employ ID
				pst = con.prepareStatement("select emp_per_id,service_id from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id and empcode=?");
				pst.setString(1, empcode.trim());
				rs = pst.executeQuery();
				while (rs.next()) {
					emp_per_id = rs.getInt(1);
					
					if (rs.getString("service_id") != null && !rs.getString("service_id").trim().equals("") && !rs.getString("service_id").trim().equalsIgnoreCase("NULL")) {
						String[] str = rs.getString("service_id").split(",");
						for (int k = 0; str != null && k < str.length; k++) {
							if (uF.parseToInt(str[k]) > 0) {
								servic_id = uF.parseToInt(str[k]);
								break;
							}
						}
					}					
				}
				rs.close();
				pst.close();
				
				if (emp_per_id > 0) {
					flag = uF.isThisDateValid(cell2, dateFormat);

					if (!flag) {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check date format for employee code '" + empcode + "' on " + cell2
								+ ".</li>");
						break;
					}

					// System.out.println("empcode===="+empcode+"  empid====>"+emp_per_id+" ======ServiceId=====>"+servic_id);
					if (servic_id == 0) {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check SBU for employee code '" + empcode + "'.</li>");
						flag = false;
						break;
					}
					
					boolean checkSalaryFlag = CF.checkSalaryForImportAttendance(con, CF, uF, emp_per_id, cell2, cell2, dateFormat);
					if(checkSalaryFlag){
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Salary already processed for employee code '" + empcode + "' on "+cell2+".</li>");
						flag = false;
						break;
					}
					
					boolean checkAttendanceApproveFlag = CF.checkAttendanceApproveForImportAttendance(con, CF, uF, emp_per_id, cell2, cell2, dateFormat);
					if(checkAttendanceApproveFlag){
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Attendance already approved for employee code '" + empcode + "' on "+cell2+".</li>");
						flag = false;
						break;
					}
					
					/*boolean checkLeaveFlag = CF.checkLeaveForImportAttendance(con, CF, uF, emp_per_id, cell2, cell2, dateFormat);
					if(checkLeaveFlag){
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Leave already applied for employee code '" + empcode + "' on "+cell2+".</li>");
						flag = false;
						break;
					}*/
					
			//===start parvez date: 04-08-2022===		
					if(i==1){
//						System.out.println("IA/2857---cell2="+cell2);
						String strMonthMinMaxDate = uF.getCurrentMonthMinMaxDate(cell2, dateFormat);
						String[] arrMonthDates = strMonthMinMaxDate.split("::::");
						
						boolean checkAttendanceFlag1 = CF.checkAttendanceForImportAttendance(con, CF, uF, emp_per_id, arrMonthDates[0], arrMonthDates[1], dateFormat);
						if(checkAttendanceFlag1){
							pst = con.prepareStatement("delete from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd') between ? and ? ");
							pst.setInt(1, emp_per_id);
							pst.setDate(2, uF.getDateFormat(arrMonthDates[0], dateFormat));
							pst.setDate(3, uF.getDateFormat(arrMonthDates[1], dateFormat));
							pst.executeUpdate();
							pst.close();
						}
					}
			//===end parvez date: 04-08-2022===		
					
					boolean checkAttendanceFlag = CF.checkAttendanceForImportAttendance(con, CF, uF, emp_per_id, cell2, cell2, dateFormat);
					if(checkAttendanceFlag){
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Attendance already existed for employee code '" + empcode + "' on "+cell2+".</li>");
						flag = false;
						break;
					}		

					String _fromTime = cell4.toString();
					String _toTime = cell5.toString();

//					System.out.println("_fromTime ===>> " + _fromTime + " -- _toTime ===>> " + _toTime);
					String[] a = getRosterEntry(con, pst, rs, uF, emp_per_id, cell2, servic_id, dateFormat);
					String rosterInTime = a[0];
					String rosterOutTime = a[1];

					if (rosterInTime == null || rosterOutTime == null) {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check roster time for employee code '" + empcode + "' on " + cell2
								+ ".</li>");
						flag = false;
						break;
					} else {
						/**
						 * In Entry
						 * */
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check start time format for employee code '" + empcode + "' on "
								+ cell2 + ".</li>");
						pst1 = con
								.prepareStatement("Select atten_id from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=?");
						pst1.setInt(1, emp_per_id);
						pst1.setDate(2, uF.getDateFormat(cell2, dateFormat));
						pst1.setString(3, "IN");
						rs1 = pst1.executeQuery();
						if (rs1.next()) {
//							System.out.println("User Already Attended the Office");
						} else {
							long lStart = uF.getTimeFormat(cell2 + " " + _fromTime, dateFormat + " " + timeFormat).getTime();
							long in = uF.getTimeFormat(cell2 + " " + rosterInTime, dateFormat + " " + timeFormat).getTime();

							pst = con.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
									+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
							pst.setInt(1, emp_per_id);
							pst.setTimestamp(2, uF.getTimeStamp(cell2 + " " + _fromTime, dateFormat + " " + timeFormat));
							pst.setString(3, " ");
							pst.setString(4, "IN");
							pst.setInt(5, 1);
							pst.setString(6, " ");
							pst.setNull(7, java.sql.Types.DOUBLE);
							pst.setTimestamp(8, uF.getTimeStamp(cell2 + " " + _fromTime, dateFormat + " " + timeFormat));
							pst.setInt(9, servic_id);

							if (in > 0 && in > lStart) {
								pst.setDouble(10, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));

							} else if (lStart > 0 && lStart > in) {
								pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));

							} else {
								pst.setDouble(10, 0);

							}
							pst.executeUpdate();
							pst.close();
						}
						rs1.close();
						pst1.close();

						/**
						 * Out Entry
						 * */
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check end time format for employee code '" + empcode + "' on "
								+ cell2 + ".</li>");
						pst1 = con
								.prepareStatement("Select atten_id from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=? ");
						pst1.setInt(1, emp_per_id);
						pst1.setDate(2, uF.getDateFormat(cell2, dateFormat));
						pst1.setString(3, "OUT");

						rs1 = pst1.executeQuery();
						if (rs1.next()) {
						} else {

							long lStart = uF.getTimeFormat(cell2 + " " + _toTime, dateFormat + " " + timeFormat).getTime();
							long in = uF.getTimeFormat(cell2 + " " + rosterOutTime, dateFormat + " " + timeFormat).getTime();
							long lEnd = uF.getTimeFormat(cell2 + " " + _fromTime, dateFormat + " " + timeFormat).getTime();

							pst = con.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
									+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
							pst.setInt(1, emp_per_id);
							pst.setTimestamp(2, uF.getTimeStamp(cell2 + " " + _toTime, dateFormat + " " + timeFormat));
							pst.setString(3, " ");
							pst.setString(4, "OUT");
							pst.setInt(5, 1);
							pst.setString(6, " ");
							pst.setDouble(7, uF.parseToDouble(uF.getTimeDiffInHoursMins(lEnd, lStart)));
							pst.setTimestamp(8, uF.getTimeStamp(cell2 + " " + _toTime, dateFormat + " " + timeFormat));
							pst.setInt(9, servic_id);

							if (in > 0 && in > lStart) {
								pst.setDouble(10, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));

							} else if (lStart > 0 && lStart > in) {
								pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));

							} else {
								pst.setDouble(10, 0);

							}

							pst.executeUpdate();
							pst.close();
						}
						rs1.close();
						pst1.close();
						flag = true;
					}
				} else {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the employee code '" + empcode + "'.</li>");
					flag = false;
					break;
				}
			} // end main for loop
			if (flag) {
				con.commit();
				session.setAttribute(MESSAGE, SUCCESSM + "Attendance Imported Successfully!" + END);
				alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Attendance Imported Successfully!</li>");
				if (alErrorList.size() > 0) {
					alReport.add(alErrorList.get(alErrorList.size() - 1));
				}
				session.setAttribute("alReport", alReport);
			} else {
				con.rollback();
				if (alErrorList.size() > 0) {
					alReport.add(alErrorList.get(alErrorList.size() - 1));
				}
				session.setAttribute("alReport", alReport);
				session.setAttribute(MESSAGE, ERRORM + "Attendance not imported. Please check imported file." + END);
			}

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if (alErrorList.size() > 0) {
				alReport.add(alErrorList.get(alErrorList.size() - 1));
			}
			session.setAttribute("alReport", alReport);
			session.setAttribute(MESSAGE, ERRORM + "Attendance not imported. Please check imported file." + END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeResultSet(rs);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);
			try {
				fis.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			System.gc();
		}

	}

	public void importFileFormatZicon(File path, UtilityFunctions uF) {
		
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		PreparedStatement pst1 = null;
		ResultSet rs1 = null;
		String dateFormat = "dd/MM/yyyy";
		String timeFormat = "HH:mm:ss";
		FileInputStream fis = null;
		List<String> alErrorList = new ArrayList<String>();
		try {
			con = db.makeConnection(con);
			con.setAutoCommit(false);
			fis = new FileInputStream(path);

			List<List<String>> dataList = new ArrayList<List<String>>();
			HSSFWorkbook wb = new HSSFWorkbook(fis);
			HSSFSheet sheet = wb.getSheetAt(0);
			HSSFRow row;
			HSSFCell cell;
			Iterator rows = sheet.rowIterator();

			while (rows.hasNext()) {
				row = (HSSFRow) rows.next();
				Iterator cells = row.cellIterator();
				List<String> cellList = new ArrayList<String>();
				while (cells.hasNext()) {
					cell = (HSSFCell) cells.next();

					if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
						SimpleDateFormat DtFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

						if (HSSFDateUtil.isCellDateFormatted(cell)) {
							System.out.println(DtFormat.format(cell.getDateCellValue()).toString());
							System.out.println("CellData Date===>" + cell.getDateCellValue());
							cellList.add(DtFormat.format(cell.getDateCellValue()).toString());
						} else {
							cellList.add(cell.toString());
						}
					} else {
						cellList.add(cell.toString());
					}
					System.out.println("CellData===>" + cell.toString());
				}
				dataList.add(cellList);
			}

			boolean flag = false;

			for (int i = 1; i < dataList.size(); i++) {
				int emp_per_id = 0;
				int servic_id = 0;
				String empcode = "";
				List<String> cellList = dataList.get(i);
				String cell0 = cellList.get(0);
				String cell1 = cellList.get(1);
				String cell2 = cellList.get(2);

				System.out.println("Cell5 ===>" + cell2);
				String empbiometricid = cell0.toString();
				if (empbiometricid.contains(".")) {
					empbiometricid = empbiometricid.substring(0, empbiometricid.indexOf("."));
					// System.out.println("Biometric ID ===>"+empbiometricid);
				}
				pst = con
						.prepareStatement("select empcode from employee_personal_details epd inner join employee_official_details eod on eod.emp_id = epd.emp_per_id where eod.biometrix_id=?");
				pst.setInt(1, uF.parseToInt(empbiometricid.trim()));
				System.out.println("pst===>" + pst);
				rs = pst.executeQuery();
				if (rs.next()) {
					empcode = rs.getString(1);
					// Select Employ ID
					pst = con
							.prepareStatement("select emp_per_id,service_id from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id and empcode=?");
					pst.setString(1, empcode.trim());
					rs = pst.executeQuery();
					System.out.println("Pst==>" + pst);

					while (rs.next()) {
						emp_per_id = rs.getInt(1);
						
						if (rs.getString("service_id") != null && !rs.getString("service_id").trim().equals("") && !rs.getString("service_id").trim().equalsIgnoreCase("NULL")) {
							String[] str = rs.getString("service_id").split(",");
							for (int k = 0; str != null && k < str.length; k++) {
								if (uF.parseToInt(str[k]) > 0) {
									servic_id = uF.parseToInt(str[k]);
									break;
								}
							}
						}
					}
					rs.close();
					pst.close();
					if (emp_per_id > 0) {
						flag = uF.isThisDateValid(cell2, DBTIMESTAMP_STR);

						if (!flag) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check date format for employee code '" + empcode + "' on "
									+ cell2 + ".</li>");
							break;
						}

						// System.out.println("empcode===="+empcode+"  empid====>"+emp_per_id+" ======ServiceId=====>"+servic_id);
						if (servic_id == 0) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check SBU for employee code '" + empcode + "'.</li>");
							flag = false;
							break;
						}
						
						boolean checkSalaryFlag = CF.checkSalaryForImportAttendance(con, CF, uF, emp_per_id, cell1, cell2, DBTIMESTAMP_STR);
						if(checkSalaryFlag){
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Salary already processed for employee code '" + empcode + "' on "+cell1+".</li>");
							flag = false;
							break;
						}
						
						boolean checkAttendanceApproveFlag = CF.checkAttendanceApproveForImportAttendance(con, CF, uF, emp_per_id, cell1, cell2, DBTIMESTAMP_STR);
						if(checkAttendanceApproveFlag){
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Attendance already approved for employee code '" + empcode + "' on "+cell1+".</li>");
							flag = false;
							break;
						}
						
						boolean checkLeaveFlag = CF.checkLeaveForImportAttendance(con, CF, uF, emp_per_id, cell1, cell2, DBTIMESTAMP_STR);
						if(checkLeaveFlag){
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Leave already applied for employee code '" + empcode + "' on "+cell1+".</li>");
							flag = false;
							break;
						}
						
						boolean checkAttendanceFlag = CF.checkAttendanceForImportAttendance(con, CF, uF, emp_per_id, cell1, cell2, DBTIMESTAMP_STR);
						if(checkAttendanceFlag){
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Attendance already existed for employee code '" + empcode + "' on "+cell1+".</li>");
							flag = false;
							break;
						}
						
						// convert the datetime to time format
						//
						String _fromTime = uF.getDateFormat(cell1.toString(), DBTIMESTAMP_STR, DBTIME);
						String _toTime = uF.getDateFormat(cell2.toString(), DBTIMESTAMP_STR, DBTIME);

						System.out.println("_FromTime===>" + _fromTime);
						System.out.println("_FromTime===>" + _toTime);
						String[] a = getRosterEntry(con, pst, rs, uF, emp_per_id, cell2, servic_id, dateFormat);
						String rosterInTime = a[0];
						String rosterOutTime = a[1];

						if (rosterInTime == null || rosterOutTime == null) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check roster time for employee code '" + empcode + "' on "
									+ cell2 + ".</li>");
							flag = false;
							break;
						} else {
							/**
							 * In Entry
							 * */

							if (!_fromTime.equalsIgnoreCase("00:00")) {

								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check start time format for employee code '" + empcode
										+ "' on " + cell2 + ".</li>");
								pst1 = con
										.prepareStatement("Select atten_id from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=?");
								pst1.setInt(1, emp_per_id);
								pst1.setDate(2, uF.getDateFormat(cell1, dateFormat));
								pst1.setString(3, "IN");
								rs1 = pst1.executeQuery();
								if (rs1.next()) {
									System.out.println("User Already Attended the Office");
								} else {
									long lStart = uF.getTimeFormat(cell1 + " " + _fromTime, dateFormat + " " + timeFormat).getTime();
									long in = uF.getTimeFormat(cell1 + " " + rosterInTime, dateFormat + " " + timeFormat).getTime();

									pst = con
											.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
													+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
									pst.setInt(1, emp_per_id);
									pst.setTimestamp(2, uF.getTimeStamp(cell1 + " " + _fromTime, dateFormat + " " + timeFormat));
									pst.setString(3, " ");
									pst.setString(4, "IN");
									pst.setInt(5, 1);
									pst.setString(6, " ");
									pst.setNull(7, java.sql.Types.DOUBLE);
									pst.setTimestamp(8, uF.getTimeStamp(cell1 + " " + _fromTime, dateFormat + " " + timeFormat));
									pst.setInt(9, servic_id);

									if (in > 0 && in > lStart) {
										pst.setDouble(10, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));

									} else if (lStart > 0 && lStart > in) {
										pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));

									} else {
										pst.setDouble(10, 0);

									}
									System.out.println("pst===>" + pst);
									pst.executeUpdate();
									pst.close();
								}
								rs1.close();
								pst1.close();

							}
							/**
							 * Out Entry
							 * */
							if (!_fromTime.equalsIgnoreCase("00:00")) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check end time format for employee code '" + empcode
										+ "' on " + cell2 + ".</li>");
								pst1 = con
										.prepareStatement("Select atten_id from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=? ");
								pst1.setInt(1, emp_per_id);
								pst1.setDate(2, uF.getDateFormat(cell2, dateFormat));
								pst1.setString(3, "OUT");

								rs1 = pst1.executeQuery();
								if (rs1.next()) {
								} else {

									long lStart = uF.getTimeFormat(cell2 + " " + _toTime, dateFormat + " " + timeFormat).getTime();
									long in = uF.getTimeFormat(cell2 + " " + rosterOutTime, dateFormat + " " + timeFormat).getTime();
									long lEnd = uF.getTimeFormat(cell2 + " " + _fromTime, dateFormat + " " + timeFormat).getTime();

									pst = con
											.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
													+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
									pst.setInt(1, emp_per_id);
									pst.setTimestamp(2, uF.getTimeStamp(cell2 + " " + _toTime, dateFormat + " " + timeFormat));
									pst.setString(3, " ");
									pst.setString(4, "OUT");
									pst.setInt(5, 1);
									pst.setString(6, " ");
									pst.setDouble(7, uF.parseToDouble(uF.getTimeDiffInHoursMins(lEnd, lStart)));
									pst.setTimestamp(8, uF.getTimeStamp(cell2 + " " + _toTime, dateFormat + " " + timeFormat));
									pst.setInt(9, servic_id);

									if (in > 0 && in > lStart) {
										pst.setDouble(10, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));

									} else if (lStart > 0 && lStart > in) {
										pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));

									} else {
										pst.setDouble(10, 0);

									}
									System.out.println("pst===>" + pst);
									pst.executeUpdate();
									pst.close();
								}
								rs1.close();
								pst1.close();
								flag = true;
							}
						}
					} else {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the employee code '" + empcode + "'.</li>");
						flag = false;
						break;
					}

				} else {

					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the employee Biometric ID '" + empbiometricid + "'.</li>");
					flag = false;
					break;
				}
				rs.close();
				pst.close();
			} // end main for loop
			if (flag) {
				con.commit();
				session.setAttribute(MESSAGE, SUCCESSM + "Attendance Imported Successfully!" + END);
			} else {
				con.rollback();
				if (alErrorList.size() > 0) {
					alReport.add(alErrorList.get(alErrorList.size() - 1));
				}
				session.setAttribute("alReport", alReport);
				session.setAttribute(MESSAGE, ERRORM + "Attendance not imported. Please check imported file." + END);
			}

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if (alErrorList.size() > 0) {
				alReport.add(alErrorList.get(alErrorList.size() - 1));
			}
			session.setAttribute("alReport", alReport);
			session.setAttribute(MESSAGE, ERRORM + "Attendance not imported. Please check imported file." + END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeResultSet(rs);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);
			try {
				fis.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			System.gc();
		}

	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	private void updateBreakRegisters(String strEmpId, UtilityFunctions uF, Connection con, String strMode, String strDate2, String strStart, long lIn,
			String strActualTime, String strWLocation) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			String[] arr = CF.getCurrentPayCycle(con, CF.getStrTimeZone(), uF.getDateFormatUtil(strDate2, "dd-MM-yyyy"), CF);

			Map<String, String> hmBreakBalance = new HashMap<String, String>();
			Map<String, String> hmBreakTaken = new HashMap<String, String>();
			Map<String, String> hmBreakUnPaid = new HashMap<String, String>();

			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			String levelid = hmEmpLevelMap.get(strEmpId);

			pst = con
					.prepareStatement("select a.emp_id, br.balance, a.break_type_id, br.taken_paid, br.taken_unpaid from break_register br, ( select max(register_id) as register_id,emp_id, break_type_id from break_register where _date <= ? group by emp_id,break_type_id ) a where br.register_id = a.register_id and br.emp_id = a.emp_id and br.break_type_id = a.break_type_id and a.emp_id = ?");
			pst.setDate(1, uF.getDateFormat(arr[1], DATE_FORMAT));
			pst.setInt(2, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmBreakBalance.put(rs.getString("break_type_id"), rs.getString("balance"));
				hmBreakTaken.put(rs.getString("break_type_id"), rs.getString("taken_paid"));
				hmBreakUnPaid.put(rs.getString("break_type_id"), rs.getString("taken_unpaid"));
			}
			rs.close();
			pst.close();
			// System.out.println("=======>"+pst);

			Map<String, String> hmEmpBreakTaken = new HashMap<String, String>();
			pst = con
					.prepareStatement("select break_type_id,sum(leave_no)as no_of_leaves from break_application_register where emp_id=? and is_paid = true group by break_type_id");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmEmpBreakTaken.put(rs.getString("break_type_id"), rs.getString("no_of_leaves"));
				hmBreakTaken.put(rs.getString("break_type_id"), rs.getString("no_of_leaves"));
			}
			rs.close();
			pst.close();

			pst = con
					.prepareStatement("select a.break_type_id,days from break_policy a,emp_leave_break_type elt where a.break_type_id=elt.break_type_id and a.wlocation_id=elt.wlocation_id and a.wlocation_id=? and level_id=?");
			pst.setInt(1, uF.parseToInt(strWLocation));
			pst.setInt(2, uF.parseToInt(levelid));
			rs = pst.executeQuery();
			while (rs.next()) {
				double totalBalance = uF.parseToDouble(rs.getString("days")) - uF.parseToDouble(hmEmpBreakTaken.get(rs.getString("break_type_id")));
				hmBreakBalance.put(rs.getString("break_type_id"), "" + totalBalance);

			}
			rs.close();
			pst.close();

			// System.out.println("=======>"+pst);

			long lIn1 = uF.getTimeFormat(strDate2 + " " + strStart, "dd-MM-yyyy HH:mm:ss").getTime();
			long tDiff = (lIn - lIn1);

			if (strMode != null && strMode.equalsIgnoreCase("IN")) {
				tDiff = (lIn - lIn1);
			} else {
				tDiff = (lIn - lIn1);
			}

			long diffMinutes = 0;
			if (tDiff > 0 || tDiff < 0) {
				// long diffHours = tDiff / (1000 * 60 * 60);
				diffMinutes = Math.abs((tDiff) / 60000);
			}

			if ((strMode.equalsIgnoreCase("IN") && tDiff < 0) || strMode.equalsIgnoreCase("OUT") && tDiff > 0) {
				return;
			}

			pst = con.prepareStatement("select * from  break_policy where wlocation_id = ? and time_value>=? and _mode like ?  order by time_value limit 1");
			pst.setInt(1, uF.parseToInt(strWLocation));
			pst.setDouble(2, diffMinutes);
			pst.setString(3, "%" + strMode + "%");
			rs = pst.executeQuery();
			String strBreakPolicyId = null;
			String strTimeValue = null;
			boolean isAvailable = false;

			// System.out.println("strBreakPolicyId=1==>"+strBreakPolicyId);

			while (rs.next()) {
				strBreakPolicyId = rs.getString("break_type_id");
				strTimeValue = rs.getString("time_value");

				isAvailable = true;
				// System.out.println("strBreakPolicyId=2==>"+strBreakPolicyId);
			}
			rs.close();
			pst.close();

			// System.out.println("pst===>"+pst);

			double dblBalance = uF.parseToDouble(hmBreakBalance.get(strBreakPolicyId));
			double dblTakenPaid = uF.parseToDouble(hmBreakTaken.get(strBreakPolicyId));
			double dblTakenUnPaid = uF.parseToDouble(hmBreakUnPaid.get(strBreakPolicyId));

			// System.out.println("dblBalance==>"+dblBalance);
			// System.out.println(strBreakPolicyId+" hmBreakBalance==>"+hmBreakBalance);

			int k = 0;
			for (k = 0; k < 5 && dblBalance == 0 && isAvailable; k++) {

				if (dblBalance == 0) {
					pst = con
							.prepareStatement("select * from  break_policy where wlocation_id = ? and time_value > ? and _mode like ? order by time_value limit 1");
					pst.setInt(1, uF.parseToInt(strWLocation));
					pst.setDouble(2, uF.parseToDouble(strTimeValue));
					pst.setString(3, "%" + strMode + "%");
					rs = pst.executeQuery();
					while (rs.next()) {
						strBreakPolicyId = rs.getString("break_type_id");
						strTimeValue = rs.getString("time_value");
					}
					rs.close();
					pst.close();

					dblBalance = uF.parseToDouble(hmBreakBalance.get(strBreakPolicyId));
					dblTakenPaid = uF.parseToDouble(hmBreakTaken.get(strBreakPolicyId));
					dblTakenUnPaid = uF.parseToDouble(hmBreakUnPaid.get(strBreakPolicyId));

					// System.out.println("dblBalance= k="+k+" =>"+dblBalance+" pst="+pst);
				}

			}

			if (diffMinutes < 120 && dblBalance == 0) {

				strBreakPolicyId = "-2";
				dblBalance = uF.parseToDouble(hmBreakBalance.get(strBreakPolicyId));
				dblTakenPaid = uF.parseToDouble(hmBreakTaken.get(strBreakPolicyId));
				dblTakenUnPaid = uF.parseToDouble(hmBreakUnPaid.get(strBreakPolicyId));

				dblTakenUnPaid += 1;
				dblTakenPaid = 0;

			} else if (dblBalance == 0) {
				strBreakPolicyId = "-1";
				dblBalance = uF.parseToDouble(hmBreakBalance.get(strBreakPolicyId));
				dblTakenPaid = uF.parseToDouble(hmBreakTaken.get(strBreakPolicyId));
				dblTakenUnPaid = uF.parseToDouble(hmBreakUnPaid.get(strBreakPolicyId));

				dblTakenUnPaid += 1;
				dblTakenPaid = 0;
			} else {
				dblTakenPaid += 1;
				dblTakenUnPaid = 0;
			}

			pst = con
					.prepareStatement("insert into break_application_register (_date, emp_id, break_type_id, leave_no, is_paid, balance, _type) values (?,?,?,?,?,?,?)");
			pst.setDate(1, uF.getDateFormat(strDate2, "dd-MM-yyyy"));
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setInt(3, uF.parseToInt(strBreakPolicyId));
			pst.setInt(4, 1);
			if (dblBalance == 0) {
				pst.setBoolean(5, false);
			} else {
				pst.setBoolean(5, true);
			}

			if (dblBalance > 0) {
				pst.setDouble(6, (dblBalance - 1));
			} else {
				pst.setDouble(6, dblBalance);
			}
			pst.setString(7, strMode);
			pst.execute();
			pst.close();

			pst = con.prepareStatement("update break_register set taken_paid =?,taken_unpaid =?, balance=? where break_type_id =? and _date=? and emp_id =?");
			pst.setDouble(1, (dblTakenPaid));
			pst.setDouble(2, (dblTakenUnPaid));

			if (dblBalance > 0) {
				pst.setDouble(3, (dblBalance - 1));
			} else {
				pst.setDouble(3, dblBalance);
			}

			pst.setInt(4, uF.parseToInt(strBreakPolicyId));
			pst.setDate(5, uF.getDateFormat(strDate2, "dd-MM-yyyy"));
			pst.setInt(6, uF.parseToInt(strEmpId));
			int x = pst.executeUpdate();
			pst.close();

			// System.out.println("== update =>"+pst);

			if (x == 0) {
				pst = con.prepareStatement("insert into break_register (_date, emp_id, taken_paid, balance, taken_unpaid, break_type_id) values (?,?,?,?,?,?)");

				pst.setDate(1, uF.getDateFormat(strDate2, "dd-MM-yyyy"));
				pst.setInt(2, uF.parseToInt(strEmpId));
				pst.setDouble(3, dblTakenPaid);
				if (dblBalance > 0) {
					pst.setDouble(4, (dblBalance - 1));
				} else {
					pst.setDouble(4, dblBalance);
				}

				pst.setDouble(5, dblTakenUnPaid);
				pst.setInt(6, uF.parseToInt(strBreakPolicyId));
				pst.execute();
				pst.close();

				// System.out.println("== insert =>"+pst);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void insertINEntry(Connection con, PreparedStatement pst, ResultSet rs, UtilityFunctions uF, int empId, int serviceId, String strDate,
			String strTime, String rosterTime, String dateFormat, String timeFormat) {
		PreparedStatement pst1 = null;
		ResultSet rs1 = null;
		try {

			pst1 = con
					.prepareStatement("Select atten_id from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=?");
			pst1.setInt(1, empId);
			pst1.setDate(2, uF.getDateFormat(strDate, dateFormat));
			pst.setString(3, "IN");
			rs1 = pst1.executeQuery();
			if (rs1.next()) {
				System.out.println("User Already Attand the Office");
			} else {
				long lStart = uF.getTimeFormat(strDate + " " + strTime, dateFormat + " " + timeFormat).getTime();
				long in = uF.getTimeFormat(strDate + " " + rosterTime, dateFormat + " " + timeFormat).getTime();

				pst = con.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
						+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
				pst.setInt(1, empId);
				pst.setTimestamp(2, uF.getTimeStamp(strDate + " " + strTime, dateFormat + " " + timeFormat));
				pst.setString(3, " ");
				pst.setString(4, "IN");
				pst.setInt(5, 1);
				pst.setString(6, " ");
				pst.setNull(7, java.sql.Types.DOUBLE);
				pst.setTimestamp(8, uF.getTimeStamp(strDate + " " + strTime, dateFormat + " " + timeFormat));
				pst.setInt(9, serviceId);

				if (in > 0 && in > lStart) {
					pst.setDouble(10, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));

				} else if (lStart > 0 && lStart > in) {
					pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));

				} else {
					pst.setDouble(10, 0);

				}
				pst.executeUpdate();
				pst.close();
			}
			rs1.close();
			pst1.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst1 != null) {
				try {
					pst1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void insertINEntry1(Connection con, PreparedStatement pst, ResultSet rs, UtilityFunctions uF, int empId, int serviceId, String strDate,
			String strTime, String rosterTime, String dateFormat, String timeFormat) {
		try {

			boolean flag = false;
			Date d1 = null;
			pst = con
					.prepareStatement("Select atten_id,in_out_timestamp from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=?");
			pst.setInt(1, empId);
			pst.setDate(2, uF.getDateFormat(strDate, dateFormat));
			pst.setString(3, "IN");
			rs = pst.executeQuery();
			if (rs.next()) {
				flag = true;
				d1 = uF.getDateFormatUtil(rs.getString("in_out_timestamp"), DBTIMESTAMP);
				// System.out.println("User Already Attand the Office");
			}
			rs.close();
			pst.close();

			if (empId == 63) {
				// System.out.println("d1=="+d1);
			}
			if (flag && d1.compareTo(uF.getDateFormatUtil(strDate + " " + strTime, dateFormat + " " + timeFormat)) > 0) {

				long lStart = uF.getTimeFormat(strDate + " " + strTime, dateFormat + " " + timeFormat).getTime();
				long in = uF.getTimeFormat(strDate + " " + rosterTime, dateFormat + " " + timeFormat).getTime();

				pst = con.prepareStatement("update attendance_details set in_out_timestamp=?,reason=?,in_out=?,approved=?,comments=?,hours_worked=?,"
						+ "in_out_timestamp_actual=?,service_id=?,early_late=? where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=?");

				pst.setTimestamp(1, uF.getTimeStamp(strDate + " " + strTime, dateFormat + " " + timeFormat));
				pst.setString(2, " ");
				pst.setString(3, "IN");
				pst.setInt(4, 1);
				pst.setString(5, " ");
				pst.setNull(6, java.sql.Types.DOUBLE);
				pst.setTimestamp(7, uF.getTimeStamp(strDate + " " + strTime, dateFormat + " " + timeFormat));
				pst.setInt(8, serviceId);

				if (in > 0 && in > lStart) {
					pst.setDouble(9, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));

				} else if (lStart > 0 && lStart > in) {
					pst.setDouble(9, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));

				} else {
					pst.setDouble(9, 0);

				}
				pst.setInt(10, empId);
				pst.setDate(11, uF.getDateFormat(strDate, dateFormat));
				pst.setString(12, "IN");

				pst.executeUpdate();
				pst.close();

			} else if (!flag) {
				long lStart = uF.getTimeFormat(strDate + " " + strTime, dateFormat + " " + timeFormat).getTime();
				long in = uF.getTimeFormat(strDate + " " + rosterTime, dateFormat + " " + timeFormat).getTime();

				pst = con.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
						+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
				pst.setInt(1, empId);
				pst.setTimestamp(2, uF.getTimeStamp(strDate + " " + strTime, dateFormat + " " + timeFormat));
				pst.setString(3, " ");
				pst.setString(4, "IN");
				pst.setInt(5, 1);
				pst.setString(6, " ");
				pst.setNull(7, java.sql.Types.DOUBLE);
				pst.setTimestamp(8, uF.getTimeStamp(strDate + " " + strTime, dateFormat + " " + timeFormat));
				pst.setInt(9, serviceId);

				if (in > 0 && in > lStart) {
					pst.setDouble(10, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));

				} else if (lStart > 0 && lStart > in) {
					pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));

				} else {
					pst.setDouble(10, 0);

				}

				pst.executeUpdate();
				pst.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void insertOUTEntry(Connection con, PreparedStatement pst, ResultSet rs, UtilityFunctions uF, int empId, int serviceId, String strDate,
			String strTime, String rosterTime, String strInTime, String dateFormat, String timeformat) {
		PreparedStatement pst1 = null;
		ResultSet rs1 = null;
		try {
			pst1 = con
					.prepareStatement("Select atten_id from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=? ");
			pst1.setInt(1, empId);
			pst1.setDate(2, uF.getDateFormat(strDate, dateFormat));
			pst1.setString(3, "OUT");

			rs1 = pst1.executeQuery();
			if (rs1.next()) {
			} else {

				long lStart = uF.getTimeFormat(strDate + " " + strTime, dateFormat + " " + timeformat).getTime();
				long in = uF.getTimeFormat(strDate + " " + rosterTime, dateFormat + " " + timeformat).getTime();
				long lEnd = uF.getTimeFormat(strDate + " " + strInTime, dateFormat + " " + timeformat).getTime();

				pst = con.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
						+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
				pst.setInt(1, empId);
				pst.setTimestamp(2, uF.getTimeStamp(strDate + " " + strTime, dateFormat + " " + timeformat));
				pst.setString(3, " ");
				pst.setString(4, "OUT");
				pst.setInt(5, 1);
				pst.setString(6, " ");
				pst.setDouble(7, uF.parseToDouble(uF.getTimeDiffInHoursMins(lEnd, lStart)));
				pst.setTimestamp(8, uF.getTimeStamp(strDate + " " + strTime, dateFormat + " " + timeformat));
				pst.setInt(9, serviceId);

				if (in > 0 && in > lStart) {
					pst.setDouble(10, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));

				} else if (lStart > 0 && lStart > in) {
					pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));
				} else {
					pst.setDouble(10, 0);
				}
				pst.executeUpdate();
				pst.close();
			}
			rs1.close();
			pst1.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst1 != null) {
				try {
					pst1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void insertOUTEntry1(Connection con, PreparedStatement pst, ResultSet rs, UtilityFunctions uF, int empId, int serviceId, String strDate,
			String strTime, String rosterTime, String strInTime, String dateFormat, String timeformat) {
		try {
			Date d1 = null;
			pst = con
					.prepareStatement("Select atten_id,in_out_timestamp from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=? ");
			pst.setInt(1, empId);
			pst.setDate(2, uF.getDateFormat(strDate, dateFormat));
			pst.setString(3, "OUT");
			boolean flag = false;
			rs = pst.executeQuery();
			if (rs.next()) {
				flag = true;
				d1 = uF.getDateFormatUtil(rs.getString("in_out_timestamp"), DBTIMESTAMP);
			}
			rs.close();
			pst.close();
			if (flag && d1.compareTo(uF.getDateFormatUtil(strDate + " " + strTime, dateFormat + " " + timeformat)) < 0) {

				long lStart = uF.getTimeFormat(strDate + " " + strTime, dateFormat + " " + timeformat).getTime();
				long in = uF.getTimeFormat(strDate + " " + rosterTime, dateFormat + " " + timeformat).getTime();
				long lEnd = uF.getTimeFormat(strDate + " " + strInTime, dateFormat + " " + timeformat).getTime();
				pst = con.prepareStatement("update attendance_details set in_out_timestamp=?,approved=?,comments=?,hours_worked=?,"
						+ "in_out_timestamp_actual=?,service_id=?,early_late=? where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=?");

				pst.setTimestamp(1, uF.getTimeStamp(strDate + " " + strTime, dateFormat + " " + timeformat));
				pst.setInt(2, 1);
				pst.setString(3, " ");
				pst.setDouble(4, uF.parseToDouble(uF.getTimeDiffInHoursMins(lEnd, lStart)));
				pst.setTimestamp(5, uF.getTimeStamp(strDate + " " + strTime, dateFormat + " " + timeformat));
				pst.setInt(6, serviceId);

				if (in > 0 && in > lStart) {
					pst.setDouble(7, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));

				} else if (lStart > 0 && lStart > in) {
					pst.setDouble(7, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));
				} else {
					pst.setDouble(7, 0);

				}

				pst.setInt(8, empId);
				pst.setDate(9, uF.getDateFormat(strDate, dateFormat));
				pst.setString(10, "OUT");
				pst.executeUpdate();
				pst.close();

			} else if (!flag) {

				long lStart = uF.getTimeFormat(strDate + " " + strTime, dateFormat + " " + timeformat).getTime();
				long in = uF.getTimeFormat(strDate + " " + rosterTime, dateFormat + " " + timeformat).getTime();
				long lEnd = uF.getTimeFormat(strDate + " " + strInTime, dateFormat + " " + timeformat).getTime();

				pst = con.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
						+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
				pst.setInt(1, empId);
				pst.setTimestamp(2, uF.getTimeStamp(strDate + " " + strTime, dateFormat + " " + timeformat));
				pst.setString(3, " ");
				pst.setString(4, "OUT");
				pst.setInt(5, 1);
				pst.setString(6, " ");
				pst.setDouble(7, uF.parseToDouble(uF.getTimeDiffInHoursMins(lEnd, lStart)));
				pst.setTimestamp(8, uF.getTimeStamp(strDate + " " + strTime, dateFormat + " " + timeformat));
				pst.setInt(9, serviceId);

				if (in > 0 && in > lStart) {
					pst.setDouble(10, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));

				} else if (lStart > 0 && lStart > in) {
					pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));
				} else {
					pst.setDouble(10, 0);
				}
				pst.executeUpdate();
				pst.close();

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String[] getRosterEntry1(Connection con, PreparedStatement pst, ResultSet rs, UtilityFunctions uF, int empId, String strDate, int serviceId,
			String dateFormat) {
		try {

			String[] arr = new String[2];

			// pst =
			// con.prepareStatement("Select * from roster_details where emp_id=? and _date=?");
			// pst.setInt(1, empId);
			// pst.setDate(2, uF.getDateFormat(strDate,dateFormat));
			// rs = pst.executeQuery();
			// if (rs.next()) {
			// arr[0]=rs.getString("_from");
			// arr[1]=rs.getString("_to");
			//
			// return arr;
			//
			// } else {
			String workInTime = null;
			String workOuttime = null;
			pst = con
					.prepareStatement("select * from work_location_info where wlocation_id=(select wlocation_id from employee_official_details where emp_id=?)");
			pst.setInt(1, empId);
			rs = pst.executeQuery();
			if (rs.next()) {

				workInTime = rs.getString("wlocation_start_time");
				workOuttime = rs.getString("wlocation_end_time");

				arr[0] = workInTime;
				arr[1] = workOuttime;

			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("Insert into roster_details (emp_id,_date,_from,_to,isapproved,user_id,service_id,actual_hours,attended,"
					+ "is_lunch_ded,shift_id,entry_date,roster_weeklyoff_id)values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
			pst.setInt(1, empId);
			pst.setDate(2, uF.getDateFormat(strDate, dateFormat));
			pst.setTime(3, new java.sql.Time(uF.getDateFormat(workInTime, "HH:mm:ss").getTime()));
			pst.setTime(4, new java.sql.Time(uF.getDateFormat(workOuttime, "HH:mm:ss").getTime()));
			pst.setBoolean(5, false);
			pst.setInt(6, 310);
			pst.setInt(7, serviceId); // service id
			pst.setDouble(8, 8);
			pst.setInt(9, 0);
			pst.setBoolean(10, false);
			pst.setInt(11, 1);
			pst.setDate(12, new java.sql.Date(System.currentTimeMillis()));
			pst.setInt(13, 1);
			pst.executeUpdate();
			pst.close();

			return arr;

			// }

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public String[] getRosterEntry(Connection con, PreparedStatement pst, ResultSet rs, UtilityFunctions uF, int empId, String strDate, int serviceId,
			String dateFormat) {
		PreparedStatement pst1 = null;
		ResultSet rs1 = null;
		try {

			String[] arr = new String[2];

			pst1 = con.prepareStatement("Select * from roster_details where emp_id=? and _date=?");
			pst1.setInt(1, empId);
			pst1.setDate(2, uF.getDateFormat(strDate, dateFormat));
			rs1 = pst1.executeQuery();
			if (rs1.next()) {
				arr[0] = rs1.getString("_from");
				arr[1] = rs1.getString("_to");

				return arr;

			} else {
				// String workInTime=null;
				// String workOuttime=null;
				pst = con
						.prepareStatement("select * from work_location_info where wlocation_id=(select wlocation_id from employee_official_details where emp_id=?)");
				pst.setInt(1, empId);
				rs = pst.executeQuery();
				if (rs.next()) {

					// workInTime=rs.getString("wlocation_start_time");
					// workOuttime=rs.getString("wlocation_end_time");

					arr[0] = rs.getString("wlocation_start_time");
					arr[1] = rs.getString("wlocation_end_time");

				}
				rs.close();
				pst.close();

				// System.out.println("arr[0]==>>"+arr[0]);
				// System.out.println("arr[1]==>>"+arr[1]);

				pst = con.prepareStatement("Insert into roster_details (emp_id,_date,_from,_to,isapproved,user_id,service_id,actual_hours,"
						+ "attended,is_lunch_ded,shift_id,entry_date,roster_weeklyoff_id) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
				pst.setInt(1, empId);
				pst.setDate(2, uF.getDateFormat(strDate, dateFormat));
				pst.setTime(3, new java.sql.Time(uF.getDateFormat(arr[0], "HH:mm:ss").getTime()));
				pst.setTime(4, new java.sql.Time(uF.getDateFormat(arr[1], "HH:mm:ss").getTime()));
				pst.setBoolean(5, false);
				pst.setInt(6, 310);
				pst.setInt(7, serviceId); // service id
				pst.setDouble(8, 8);
				pst.setInt(9, 0);
				pst.setBoolean(10, false);
				pst.setInt(11, 1);
				pst.setDate(12, new java.sql.Date(System.currentTimeMillis()));
				pst.setInt(13, 1);
				pst.executeUpdate();
				pst.close();
				return arr;

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst1 != null) {
				try {
					pst1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	// public void getData(Connection con,PreparedStatement
	// pst,Map<String,String> empServiceMp,Map<String,String> empCodeMp,String
	// strD1,String strD2,String ordId,String wLocation,Map<String,List<String>>
	// wLocationMp){
	// try{
	//
	//
	// }catch(Exception e){
	// e.printStackTrace();
	// }
	// }
	//

	public File getFileUpload3() {
		return fileUpload3;
	}

	public void setFileUpload3(File fileUpload3) {
		this.fileUpload3 = fileUpload3;
	}

	public String getFileUpload3ContentType() {
		return fileUpload3ContentType;
	}

	public void setFileUpload3ContentType(String fileUpload3ContentType) {
		this.fileUpload3ContentType = fileUpload3ContentType;
	}

	public String getFileUpload3FileName() {
		return fileUpload3FileName;
	}

	public void setFileUpload3FileName(String fileUpload3FileName) {
		this.fileUpload3FileName = fileUpload3FileName;
	}

	public File getFileUpload2() {
		return fileUpload2;
	}

	public void setFileUpload2(File fileUpload2) {
		this.fileUpload2 = fileUpload2;
	}

	public String getFileUpload2ContentType() {
		return fileUpload2ContentType;
	}

	public void setFileUpload2ContentType(String fileUpload2ContentType) {
		this.fileUpload2ContentType = fileUpload2ContentType;
	}

	public String getFileUpload2FileName() {
		return fileUpload2FileName;
	}

	public void setFileUpload2FileName(String fileUpload2FileName) {
		this.fileUpload2FileName = fileUpload2FileName;
	}

	public File getFileUpload1() {
		return fileUpload1;
	}

	public void setFileUpload1(File fileUpload1) {
		this.fileUpload1 = fileUpload1;
	}

	public String getFileUpload1ContentType() {
		return fileUpload1ContentType;
	}

	public void setFileUpload1ContentType(String fileUpload1ContentType) {
		this.fileUpload1ContentType = fileUpload1ContentType;
	}

	public String getFileUpload1FileName() {
		return fileUpload1FileName;
	}

	public void setFileUpload1FileName(String fileUpload1FileName) {
		this.fileUpload1FileName = fileUpload1FileName;
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

	public File getFileUpload4() {
		return fileUpload4;
	}

	public void setFileUpload4(File fileUpload4) {
		this.fileUpload4 = fileUpload4;
	}

	public String getFileUpload4ContentType() {
		return fileUpload4ContentType;
	}

	public void setFileUpload4ContentType(String fileUpload4ContentType) {
		this.fileUpload4ContentType = fileUpload4ContentType;
	}

	public String getFileUpload4FileName() {
		return fileUpload4FileName;
	}

	public void setFileUpload4FileName(String fileUpload4FileName) {
		this.fileUpload4FileName = fileUpload4FileName;
	}

	public File getFileUpload5() {
		return fileUpload5;
	}

	public void setFileUpload5(File fileUpload5) {
		this.fileUpload5 = fileUpload5;
	}

	public String getFileUpload5ContentType() {
		return fileUpload5ContentType;
	}

	public void setFileUpload5ContentType(String fileUpload5ContentType) {
		this.fileUpload5ContentType = fileUpload5ContentType;
	}

	public String getFileUpload5FileName() {
		return fileUpload5FileName;
	}

	public void setFileUpload5FileName(String fileUpload5FileName) {
		this.fileUpload5FileName = fileUpload5FileName;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}

	public File getFileUploadNew3() {
		return fileUploadNew3;
	}

	public void setFileUploadNew3(File fileUploadNew3) {
		this.fileUploadNew3 = fileUploadNew3;
	}

	public File getFileUploadNew4() {
		return fileUploadNew4;
	}

	public void setFileUploadNew4(File fileUploadNew4) {
		this.fileUploadNew4 = fileUploadNew4;
	}

	public File getFileUploadZicon() {
		return fileUploadZicon;
	}

	public void setFileUploadZicon(File fileUploadZicon) {
		this.fileUploadZicon = fileUploadZicon;
	}

	

}