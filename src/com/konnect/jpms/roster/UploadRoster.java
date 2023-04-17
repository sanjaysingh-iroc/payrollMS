package com.konnect.jpms.roster;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UploadRoster extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpSessionId;
	String strUserType = null;
	String strEmpID;
	CommonFunctions CF = null;
	
	private File fileUpload;
	private File fileUpload2;
	private String fileUploadContentType;
	private String fileUploadFileName;
	String wLocation;
	String f_org;
	String paycycle;
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	List<FillPayCycles> paycycleList;
	public String execute() throws Exception {
		session = request.getSession();
		if(session==null)return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF == null)return LOGIN;
		strEmpSessionId = (String) session.getAttribute("EMPID");
		strUserType = (String) session.getAttribute(USERTYPE);
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, PUploadRosterEntries);
		request.setAttribute(TITLE, TImportRoster);
		
		if(strUserType==null || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(ACCOUNTANT) && !strUserType.equalsIgnoreCase(RECRUITER)
				&& !strUserType.equalsIgnoreCase(MANAGER) && !strUserType.equalsIgnoreCase(HRMANAGER) && !strUserType.equalsIgnoreCase(CEO))) {
		 	
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied); 
			return ACCESS_DENIED;
		}
		
		orgList = new FillOrganisation(request).fillOrganisation();
		if(orgList!=null && orgList.size()>0) {
			if(getF_org() != null) {
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

		if(fileUpload != null) {
			uploadRoster(fileUpload, uF);
		} else if(fileUpload2 != null) {
			uploadRoster2(fileUpload2, uF);
		}
		
		return SUCCESS;
	} 

	
	private void uploadRoster2(File path, UtilityFunctions uF) {
		
		//System.out.println("format2Attendance ===>> ");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		List<String> alReport = new ArrayList<String>();
		// ImportAttendance importAttendance = new ImportAttendance();

		FileInputStream fis = null;
		List<String> alErrorList = new ArrayList<String>();
		try {
			//System.out.println("in try block====");
			con = db.makeConnection(con);
			con.setAutoCommit(false);

			fis = new FileInputStream(path);
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet rosterSheet = workbook.getSheetAt(0);
			
			List<String> dateList = new ArrayList<String>();
			List<List<String>> outerList = new ArrayList<List<String>>();
			Iterator rows = rosterSheet.rowIterator();
			int l = 0;
			int x11 =1;
			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				Iterator cells = row.cellIterator();
				if (l == 0) {
					while (cells.hasNext()) {
						String cell = cells.next().toString();
						dateList.add(cell);
					}
					l++;
					continue;
				}
				
				List<String> cellList = new ArrayList<String>();
				while (cells.hasNext()) {
					cellList.add(cells.next().toString());
				}
				outerList.add(cellList);
			}
			
//			System.out.println("outerList======>"+outerList.toString());
			
			boolean flag = false;
			boolean flagInn = false;
			if (dateList.size() < 4 || outerList.size() == 0) {
				alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">No Data Available in Sheet.</li>");
				flag = false;
			} else {
				String strDate = null;
				String strEndDate = null;
				if (dateList != null) {
					strDate = dateList.get(3);
					strEndDate = dateList.get(dateList.size() - 1);
				}
				
				Map<String, Map<String, String>> hmShiftTime = CF.getShiftTime(con);
				
				Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
				
				pst = con.prepareStatement("select wlocation_start_time,wlocation_end_time,wlocation_id from work_location_info");
				rs = pst.executeQuery();
				Map<String, List<String>> wLocationMp = new HashMap<String, List<String>>();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("wlocation_start_time"));
					innerList.add(rs.getString("wlocation_end_time"));
					wLocationMp.put(rs.getString("wlocation_id"), innerList);
				}
				rs.close();
				pst.close();

				
				pst = con.prepareStatement("select * from shift_details order by shift_id desc");
				rs = pst.executeQuery();
				Map<String, String> hmShiftDetails = new HashMap<String, String>();
				while (rs.next()) {
					hmShiftDetails.put(rs.getString("shift_code"), rs.getString("shift_id"));
				}
				rs.close();
				pst.close();

				
				pst = con.prepareStatement("select * from roster_weeklyoff_policy order by roster_weeklyoff_id desc");
				rs = pst.executeQuery();
				Map<String, String> hmRosterWeekOff = new HashMap<String, String>();
				Map<String, List<String>> hmRosterWeekOffDetails = new HashMap<String, List<String>>();
				while (rs.next()) {
					hmRosterWeekOff.put(rs.getString("weeklyoff_name"), rs.getString("roster_weeklyoff_id"));
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("weeklyoff_name"));
					innerList.add(rs.getString("weeklyoff_type"));
					innerList.add(rs.getString("weeklyoff_day"));
					innerList.add(rs.getString("weeklyoff_weekno"));
					hmRosterWeekOffDetails.put(rs.getString("roster_weeklyoff_id"), innerList);
				}
				rs.close();
				pst.close();
				
				
				Map<String, List<String>> hmRoster = new HashMap<String, List<String>>();
				pst = con.prepareStatement("select roster_id,emp_id, _date,_from,_to,service_id,actual_hours from roster_details where _date between ? and ?");
				pst.setDate(1, uF.getDateFormat(strDate, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strEndDate, DATE_FORMAT));
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList1 = new ArrayList<String>();
					innerList1.add(rs.getString("roster_id"));
					innerList1.add(rs.getString("_from"));
					innerList1.add(rs.getString("_to"));
					innerList1.add(rs.getString("service_id"));
					innerList1.add(rs.getString("actual_hours"));

					hmRoster.put(rs.getString("emp_id") + "_"+ uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), innerList1);
				}
				rs.close();
				pst.close();
				
				
				Map<String, String> hmEmpRosterWeekOff = new HashMap<String, String>();
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select emp_id,weekoff_date from roster_weekly_off where weekoff_date between ? and ? order by weekoff_date,emp_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strDate, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strEndDate, DATE_FORMAT));
				rs=pst.executeQuery();
				while(rs.next()) {
					hmEmpRosterWeekOff.put(rs.getString("emp_id")+"_"+uF.getDateFormat(rs.getString("weekoff_date"), DBDATE, DATE_FORMAT), rs.getString("emp_id"));
				}
				rs.close();
				pst.close();
				
				
				//System.out.println("outerList======>"+outerList.toString());
				for (int k = 0; k < outerList.size(); k++) {
					List<String> innerList = outerList.get(k);
					
					if(innerList != null && innerList.size() > 0) {
						String empcode = uF.getStringValue(innerList.get(1));
//						System.out.println("empcode ===>> " + empcode);
						if(empcode == null || empcode.equals("")) {
							flag = true;
							break;
						}
						int emp_per_id = 0;
						pst = con.prepareStatement("select emp_per_id,empcode,org_id,service_id,wlocation_id from employee_personal_details epd, employee_official_details eod " +
							" where epd.emp_per_id=eod.emp_id and empcode=? and eod.org_id > 0 and epd.is_alive = true");
						pst.setString(1, empcode);
						int servic_id = 0;
						int org_id = 0;
						String wlocation = null;
						rs = pst.executeQuery();
//					   System.out.println("pst for empid ===>> " + pst);
						while (rs.next()) {
							//System.out.println("in while of empid");
							emp_per_id = uF.parseToInt(rs.getString("emp_per_id"));
							org_id = uF.parseToInt(rs.getString("org_id"));
							if (rs.getString("service_id") != null && !rs.getString("service_id").trim().equals("") && !rs.getString("service_id").trim().equalsIgnoreCase("NULL")) {
								String[] str = rs.getString("service_id").split(",");
								for (int z = 0; str != null && z < str.length; z++) {
									if (uF.parseToInt(str[z]) > 0) {
										servic_id = uF.parseToInt(str[z]);
										break;
									}
								}
							}
							wlocation = rs.getString("wlocation_id");
						}
						rs.close();
						pst.close();
	
//						System.out.println("emp_per_id ===>> " + emp_per_id);
						
						if (emp_per_id > 0) {
							flag = uF.isThisDateValid(strDate, DATE_FORMAT);
							if (!flag) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check first date format for employee code '"+ empcode+ "' on "+ strDate+ ".</li>");
								break;
							}
//							System.out.println("flag 1 ===>> " + flag);
							flag = uF.isThisDateValid(strEndDate, DATE_FORMAT);
							if (!flag) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check last date format for employee code '"+ empcode+ "' on "+ strEndDate+ ".</li>");
								break;
							}
//							System.out.println("flag 2 ===>> " + flag);
							pst = con.prepareStatement("select count(*) as attendance_count,emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd') as attendance_date from attendance_details where emp_id=? "
								+ "and to_date(in_out_timestamp::text,'yyyy-MM-dd') between ? and ? group by emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd')");
							pst.setInt(1, emp_per_id);
							pst.setDate(2, uF.getDateFormat(strDate, DATE_FORMAT));
							pst.setDate(3,uF.getDateFormat(strEndDate, DATE_FORMAT));
							rs = pst.executeQuery();
							Map<String, String> empAttendanceMp = new HashMap<String, String>();
							while (rs.next()) {
								empAttendanceMp.put(rs.getString("attendance_date"),rs.getString("attendance_count"));
							}
							rs.close();
							pst.close();
	
							// ===========================================================================================================
							
							for (int j = 3; j < innerList.size(); j++) {
								String dataType = innerList.get(j).trim();
								String tmpData[] = dataType.split("/");
								
								String shiftId = hmShiftDetails.get(tmpData[0]);
								String weekOffId = "";
								if(tmpData.length>1) {
									weekOffId = hmRosterWeekOff.get(tmpData[1]);
								}
//								System.out.println(dateList.get(j) + " -- tmpData ===>> "+tmpData[0] + " --- weekOffId ===>> " + weekOffId);
								if (weekOffId == null || weekOffId.equalsIgnoreCase("null")) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">"+ empcode+ "'s on "+dateList.get(j)+" Week off code is not found.</li>");
									flagInn = false;
									break;
								} else if(uF.parseToInt(weekOffId)>1) {
									String strDay = uF.getDateFormat(dateList.get(j), DATE_FORMAT, "EEEE");
									if(strDay!=null) strDay = strDay.toUpperCase();
									List<String> innList = hmRosterWeekOffDetails.get(weekOffId);
//									System.out.println("innList ===>> " + innList);
									List<String> alWeekOffDays = Arrays.asList(innList.get(2).split(","));
//									System.out.println("alWeekOffDays ===>> " + alWeekOffDays +" -- strDay ===>> " + strDay);
									if(alWeekOffDays == null || !alWeekOffDays.contains(strDay)) {
										alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">"+ empcode+ "'s on "+dateList.get(j)+" Week off code is not correct.</li>");
										flagInn = false;
										break;
									}
								}
								
//								System.out.println(dataType+" dateList.get(j)=====>"+dateList.get(j)); 
								
								flagInn = uF.isThisDateValid(dateList.get(j),DATE_FORMAT);
								if (!flagInn) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check date format for employee code '"+ empcode+ "' on "+ dateList.get(j) + ".</li>");
									break;
								}
//								System.out.println("flagInn 3 ===>> " + flagInn);
								boolean checkSalaryFlag = CF.checkSalaryForImportAttendance(con, CF, uF, emp_per_id, dateList.get(j), dateList.get(j), DATE_FORMAT);
								if(checkSalaryFlag) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Salary already processed for employee code '" + empcode + "' on "+dateList.get(j)+".</li>");
									flagInn = false;
									break;
								}
//								System.out.println("flagInn 4 ===>> " + flagInn);
								boolean checkAttendanceApproveFlag = CF.checkAttendanceApproveForImportAttendance(con, CF, uF, emp_per_id, dateList.get(j), dateList.get(j), DATE_FORMAT);
//								System.out.println("checkAttendanceApproveFlag ===>> " + checkAttendanceApproveFlag);
								if(checkAttendanceApproveFlag){
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Attendance already approved for employee code '" + empcode + "' on "+dateList.get(j)+".</li>");
									flagInn = false;
									break;
								}
//								System.out.println("flagInn 5.0 ===>> " + flagInn);
								/*boolean checkLeaveFlag = CF.checkLeaveForImportAttendance(con, CF, uF, emp_per_id, dateList.get(j), dateList.get(j), DATE_FORMAT);
								if(checkLeaveFlag){
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
											"Leave already applied for employee code '" + empcode + "' on "+dateList.get(j)+".</li>");
									flagInn = false;
									break;
								}*/
								if(uF.parseToBoolean(hmFeatureStatus.get(F_AUTO_DELETE_ATTENDANCE_AT_ROSTER_IMPORT))) {
									boolean attDeleteFlag = deleteEmployeeAttendanceForDate(con, uF, emp_per_id, dateList.get(j));
								}
								boolean checkAttendanceFlag = CF.checkAttendanceForImportAttendance(con, CF, uF, emp_per_id, dateList.get(j), dateList.get(j), DATE_FORMAT);
//								System.out.println("checkAttendanceFlag ===>> " + checkAttendanceFlag);
								if(checkAttendanceFlag){
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\"> Attendance already existed for employee code '" + empcode + "' on "+dateList.get(j)+".</li>");
									flagInn = false;
									break;
								}
//								System.out.println("flagInn 5 ===>> " + flagInn);
								if (tmpData[0] != null && (tmpData[0].equals("") || tmpData[0].equals("L"))) {
									continue;
								} else if (hmShiftDetails.containsKey(tmpData[0])) {
									//System.out.println("in datatype.equals(P || HD) ");
									if (hmRoster.get(emp_per_id+ "_"+ uF.getDateFormat(dateList.get(j),DATE_FORMAT, DBDATE)) != null) {
										//System.out.println("in rosterMp---");
									} else {
										//System.out.println("in else part of rosterMp");
										String workInTime = null;
										String workOuttime = null;
										
//										System.out.println(emp_per_id+ " -- dateList.get(j) ===>> " + dateList.get(j) + " -- shiftId ===>> " + shiftId);
										if(uF.parseToInt(shiftId)==1) {
											List<String> workTime = wLocationMp.get(wlocation);
											if (workTime == null) {
												alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">"+ empcode+ " 's Work Location is not found.</li>");
												flagInn = false;
												break;
											}
											if (workTime != null) {
												workInTime = workTime.get(0);
												workOuttime = workTime.get(1);
											}
										} else {
											Map<String, String> shiftMap = hmShiftTime.get(shiftId); 
											if(shiftMap == null) shiftMap = new HashMap<String, String>();
											workInTime = shiftMap.get("FROM");
											workOuttime = shiftMap.get("TO");
										}
//										System.out.println("flagInn 6 ===>> " + flagInn);
										
										pst = con.prepareStatement("update roster_details set _from=?, _to=?, isapproved=?, user_id=?, actual_hours=?, attended=?, " +
											"is_lunch_ded=?, shift_id=?, entry_date=?, roster_weeklyoff_id=? where emp_id=? and _date=? and service_id=?");
										pst.setTime(1, new java.sql.Time(uF.getDateFormat(workInTime, DBTIME).getTime()));
										pst.setTime(2, new java.sql.Time(uF.getDateFormat(workOuttime, DBTIME).getTime()));
										pst.setBoolean(3, false);
										pst.setInt(4, uF.parseToInt(strEmpSessionId));
										pst.setDouble(5, uF.parseToDouble(uF.getTimeDiffInHoursMins(uF.getDateFormat(workInTime, DBTIME).getTime(), uF.getDateFormat(workOuttime, DBTIME).getTime())));
										pst.setInt(6, 0);
										pst.setBoolean(7, false);
										pst.setInt(8, uF.parseToInt(shiftId));
										pst.setDate(9, new java.sql.Date(System.currentTimeMillis()));
										pst.setInt(10, uF.parseToInt(weekOffId));
										pst.setInt(11, emp_per_id);
										pst.setDate(12, uF.getDateFormat(dateList.get(j), DATE_FORMAT));
										pst.setInt(13, servic_id);
//										System.out.println("pst -- update 1 =====>> " + pst);
										int x1 = pst.executeUpdate();
										pst.close();
									
										if(x1==0) {
											pst = con.prepareStatement("insert into roster_details (emp_id, _date, _from, _to, isapproved, user_id, service_id, actual_hours, attended, " +
												"is_lunch_ded, shift_id, entry_date, roster_weeklyoff_id) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
											pst.setInt(1, emp_per_id);
											pst.setDate(2, uF.getDateFormat(dateList.get(j), DATE_FORMAT));
											pst.setTime(3, new java.sql.Time(uF.getDateFormat(workInTime, DBTIME).getTime()));
											pst.setTime(4, new java.sql.Time(uF.getDateFormat(workOuttime, DBTIME).getTime()));
											pst.setBoolean(5, false);
											pst.setInt(6, uF.parseToInt(strEmpSessionId));
											pst.setInt(7, servic_id); // service id
											pst.setDouble(8, uF.parseToDouble(uF.getTimeDiffInHoursMins(uF.getDateFormat(workInTime, DBTIME).getTime(), uF.getDateFormat(workOuttime, DBTIME).getTime())));
											pst.setInt(9, 0);
											pst.setBoolean(10, false);
											pst.setInt(11, uF.parseToInt(shiftId));
											pst.setDate(12, new java.sql.Date(System.currentTimeMillis()));
											pst.setInt(13, uF.parseToInt(weekOffId));
//											System.out.println("pst -- insert 1 =====>> " + pst);
											pst.executeUpdate();
											pst.close();
										}
										
										List<String> innerList1 = new ArrayList<String>();
										innerList1.add("0");
										innerList1.add(workInTime);
										innerList1.add(workOuttime);
										innerList1.add(servic_id + "");
										innerList1.add(uF.getTimeDiffInHoursMins(uF.getDateFormat(workInTime, DBTIME).getTime(), uF.getDateFormat(workOuttime, DBTIME).getTime()));
										hmRoster.put(emp_per_id+ "_"+ uF.getDateFormat(dateList.get(j), DATE_FORMAT, DBDATE), innerList1);
										
										
										if(uF.parseToInt(weekOffId)>0) {
											pst = con.prepareStatement("update roster_weekly_off set roster_weeklyoff_id=?, shift_id=? where emp_id=? and weekoff_date=? and service_id=?");
											pst.setInt(1, uF.parseToInt(weekOffId));
											pst.setInt(2, uF.parseToInt(shiftId));
											pst.setInt(3, emp_per_id);
											pst.setDate(4,  uF.getDateFormat(dateList.get(j), DATE_FORMAT));
											pst.setInt(5, servic_id);
											int x = pst.executeUpdate();
											pst.close();
											
											if (x == 0) {
												pst = con.prepareStatement("insert into roster_weekly_off (emp_id, weekoff_date, service_id, roster_weeklyoff_id, shift_id) values(?,?,?,?,?)");
												pst.setInt(1, emp_per_id);
												pst.setDate(2,  uF.getDateFormat(dateList.get(j), DATE_FORMAT));
												pst.setInt(3, servic_id);
												pst.setInt(4, uF.parseToInt(weekOffId));
												pst.setInt(5, uF.parseToInt(shiftId));
												pst.executeUpdate();
												pst.close();
											}
										}
									}
	
								} else {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">"+ empcode+ "'s on "+dateList.get(j)+" Shift code is not found.</li>");
									flagInn = false;
//									System.out.println("flagInn 7 ===>> " + flagInn);
//									System.out.println("alErrorList ===>> " + alErrorList);
									break;
								}
							}
//							System.out.println("outer flagInn ===>> " + flagInn);
							if(!flagInn) {
								flag= false;
								break;
							}
							//System.out.println("hmLeaveDates==>"+hmLeaveDates);
						} else {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the employee code '"+ empcode + "'.</li>");
							flag = false;
//							System.out.println("flag 8 ===>> " + flag);
							break;
						}
					} else {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the excel sheet.</li>");
//						System.out.println("flag 9 ===>> " + flag);
						flag = false;
						break;
					}
					//System.out.println("inner loop ends ");
				}// end main for loop
				
			}
			System.out.println("flag ===>> " + flag);
			if (flag) {
				con.commit();
				session.setAttribute(MESSAGE, SUCCESSM+ "Roster Imported Successfully!" + END);
			} else {
				System.out.println("in rollback");
				con.rollback();
				if (alErrorList.size() > 0) {
					alReport.add(alErrorList.get(0));
				}
				System.out.println("alReport ===>> " + alReport);
				
				request.setAttribute("alReport", alReport);
				session.setAttribute(MESSAGE,ERRORM+ "Roster not imported. Please check imported file."+ END);
			}
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			System.out.println("in catch rollback");
			if (alErrorList.size() > 0) {
//				alReport.add(alErrorList.get(alErrorList.size() - 1));
				alReport.add(alErrorList.get(0));
			}
			request.setAttribute("alReport", alReport);
			session.setAttribute(MESSAGE, ERRORM+ "Roster not imported. Please check imported file."+ END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
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


	private boolean deleteEmployeeAttendanceForDate(Connection con, UtilityFunctions uF, int nEmpId, String strDate) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean deleteAttendanceFlag = false;
		try {
			if(nEmpId > 0 && uF.isThisDateValid(strDate, DATE_FORMAT)) {
				pst = con.prepareStatement("delete from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd') between ? and ?");
				pst.setInt(1, nEmpId);
				pst.setDate(2, uF.getDateFormat(strDate, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
				int x = pst.executeUpdate();
		//		System.out.println("pst=====>"+pst);
				pst.close();
				if (x>0) {
					deleteAttendanceFlag = true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return deleteAttendanceFlag;
	}


	public String uploadRoster(File fileUpload, UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		List<String> alErrorList = new ArrayList<String>();
		try {
			con = db.makeConnection(con);
			con.setAutoCommit(false);

			FileInputStream fis = new FileInputStream(fileUpload);
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			System.out.println("Start Reading Excelsheet.... ");
			XSSFSheet attendanceSheet = workbook.getSheetAt(0);

			List<List<String>> outerList = new ArrayList<List<String>>();

			Iterator rows = attendanceSheet.rowIterator();
			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				Iterator cells = row.cellIterator();
				List<String> cellList = new ArrayList<String>();
				while (cells.hasNext()) {
					cellList.add(cells.next().toString());
				}
				outerList.add(cellList);
			}
			
			pst = con.prepareStatement("select * from shift_details");
			rs = pst.executeQuery();
			Map<String,List<String>> shiftMap = new HashMap<String,List<String>>();
			while (rs.next()) {
				List<String> shift = new ArrayList<String>();
				shift.add(rs.getString("shift_id"));
				shift.add(rs.getString("_from"));
				shift.add(rs.getString("_to"));
				
				shiftMap.put(rs.getString("shift_code").toUpperCase(), shift);
			}	
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from roster_weeklyoff_policy");
			rs = pst.executeQuery();
			Map<String,String> hmRosterWeekOff= new HashMap<String, String>();
			while (rs.next()) {
				hmRosterWeekOff.put(rs.getString("weeklyoff_name").toUpperCase(), rs.getString("roster_weeklyoff_id"));
			}	
			rs.close();
			pst.close();
					
					
			SimpleDateFormat timeparser = new SimpleDateFormat(DBTIME);
			boolean flag = false;
			int ii = 0;
			for (int k = 1; k < outerList.size(); k++) {
				List<String> cellList = outerList.get(k);
				
				if(cellList.size() == 0 || cellList.get(1) == null || cellList.get(1).trim().equals("") || cellList.get(1).trim().equalsIgnoreCase("NULL")) {
					continue;
				} else if(cellList.size() < 6) {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the columns.</li>");
					flag = false;
					break;
				}
				ii++;
				
				String empcode=cellList.get(1);
				if (empcode.contains(".")) {
					empcode = empcode.substring(0, empcode.indexOf("."));
				}
				
				pst = con.prepareStatement("select emp_per_id,empcode,org_id,service_id from employee_personal_details epd," +
						"employee_official_details eod where epd.emp_per_id=eod.emp_id and upper(empcode) = ?");
				pst.setString(1, empcode.toUpperCase().trim());
//				System.out.println("pst=="+pst);
				rs = pst.executeQuery();
				int empId = 0;
				int empServiceId = 0;
				String []arrServiceId = null;
				while (rs.next()) {
					empId = rs.getInt("emp_per_id");
					if(rs.getString("service_id") != null) {
						arrServiceId = rs.getString("service_id").split(",");
					}
				}	 	
				rs.close();
				pst.close();
				
				for(int j=0; arrServiceId!=null && j<arrServiceId.length; j++) {
					if(uF.parseToInt(arrServiceId[j].trim()) > 0) {
						empServiceId = uF.parseToInt(arrServiceId[j].trim());
					}
				}
				
				if(empId > 0) {
					if(empServiceId > 0) {
						String shiftCode = cellList.get(4);
						List<String> shift = shiftMap.get(shiftCode.toUpperCase());
						
						String rosterWkOffCode = cellList.get(5);
						int rosterWkOffId = uF.parseToInt(hmRosterWeekOff.get(rosterWkOffCode.toUpperCase()));
//						System.out.println("empId ===>> " + empId + " -- empServiceId ===>> " + empServiceId + " -- shiftCode ===>> " + shiftCode + " -- shift ===>> " + shift);
//						System.out.println("rosterWkOffCode ===>> " + rosterWkOffCode + " -- hmRosterWeekOff ===>> " + hmRosterWeekOff);
						if(shift != null && shift.size() > 0) {
							if(rosterWkOffId > 0) {
								String strDate=cellList.get(2);
								String strDate2=cellList.get(3);
								
								int diff=uF.parseToInt(uF.dateDifference(strDate, DATE_FORMAT, strDate2, DATE_FORMAT,CF.getStrTimeZone()));
								Calendar cal=Calendar.getInstance();
								cal.setTime(uF.getDateFormatUtil(strDate, DATE_FORMAT));
//								System.out.println("diff=="+diff);
								while(diff>0) {
									pst = con.prepareStatement("UPDATE roster_details set  _from=?, _to=?, actual_hours=?,shift_id=?,roster_weeklyoff_id=?," +
											"service_id=?,user_id=? where _date=? and emp_id=?  ");
									pst.setTime(1, new java.sql.Time(timeparser.parse(shift.get(1)).getTime()));
									pst.setTime(2, new java.sql.Time(timeparser.parse(shift.get(2)).getTime()));
									pst.setDouble(3, uF.parseToDouble(uF.getTimeDiffInHoursMins(timeparser.parse(shift.get(1)).getTime(), timeparser.parse(shift.get(2)).getTime())));
									pst.setInt(4, uF.parseToInt(shift.get(0)));
									pst.setInt(5, rosterWkOffId);
									pst.setInt(6, empServiceId);
									pst.setInt(7, uF.parseToInt(strEmpSessionId));
									pst.setDate(8, new java.sql.Date(cal.getTime().getTime()));
									pst.setInt(9, empId);
									int x=pst.executeUpdate();	
									pst.close();
									flag = true;
									if(x==0){
										pst = con.prepareStatement("insert into roster_details (emp_id, _date, _from, _to, isapproved, user_id, service_id, " +
											"actual_hours, attended,is_lunch_ded,shift_id,entry_date,roster_weeklyoff_id)values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)"); 
										pst.setInt(1, empId);
										pst.setDate(2, new java.sql.Date(cal.getTime().getTime()));
										pst.setTime(3, new java.sql.Time(timeparser.parse(shift.get(1)).getTime()));
										pst.setTime(4, new java.sql.Time(timeparser.parse(shift.get(2)).getTime()));
										pst.setBoolean(5, true);
										pst.setInt(6, uF.parseToInt(strEmpSessionId));
										pst.setInt(7, empServiceId);
										pst.setDouble(8, uF.parseToDouble(uF.getTimeDiffInHoursMins(timeparser.parse(shift.get(1)).getTime(), timeparser.parse(shift.get(2)).getTime())));
										pst.setInt(9, 0);
										pst.setBoolean(10, false);
										pst.setInt(11, uF.parseToInt(shift.get(0)));
										pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
										pst.setInt(13, rosterWkOffId);
										pst.execute();	
										pst.close();
										flag = true;
									}
									diff--;
									cal.add(Calendar.DATE, 1);
								}
							} else {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check Roster Weekly Off Code '"+rosterWkOffCode+"' for employee code '"+ empcode+ "'.</li>");
								flag = false;
								break;
							}
						} else {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check Shift Code '"+shiftCode+"' for employee code '"+ empcode+ "'.</li>");
							flag = false;
							break;
						}
					} else {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check SBU for employee code '"+ empcode+ "'.</li>");
						flag = false;
						break;
					}
				} else {
					alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the employee code '"+ empcode + "'.</li>");
					flag = false;
					break;
				}
			}
			
			if(ii == 0){
				con.rollback();
				
				session.setAttribute(MESSAGE, E_RosterData);
				
				StringBuilder sbMessage = new StringBuilder("<ul style=\"margin:0px\">");
				sbMessage.append("<li class=\"msg_error\" style=\"margin:0px\">Roster not imported. Please check imported file.</li>");
				sbMessage.append("</ul>");
				session.setAttribute("sbMessage", sbMessage.toString());
			} else {
				if(flag){
					con.commit();
					
					session.setAttribute(MESSAGE, S_RosterData);
				} else {
					con.rollback();
					
					session.setAttribute(MESSAGE, E_RosterData);
					
					StringBuilder sbMessage = new StringBuilder("<ul style=\"margin:0px\">");
					if(alErrorList.size()>0){
						sbMessage.append(alErrorList.get(alErrorList.size()-1));
					}
					sbMessage.append("</ul>");
					session.setAttribute("sbMessage", sbMessage.toString());
				}
			}

		} catch (Exception e) {
			session.setAttribute(MESSAGE, E_RosterData);
			
			StringBuilder sbMessage = new StringBuilder("<ul style=\"margin:0px\">");
			if(alErrorList.size()>0){
				sbMessage.append(alErrorList.get(alErrorList.size()-1));
			}
			sbMessage.append("</ul>");
			session.setAttribute("sbMessage", sbMessage.toString());
			
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public File getFileUpload2() {
		return fileUpload2;
	}

	public void setFileUpload2(File fileUpload2) {
		this.fileUpload2 = fileUpload2;
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

	public String getwLocation() {
		return wLocation;
	}

	public void setwLocation(String wLocation) {
		this.wLocation = wLocation;
	}

	public String getF_org() {
		return f_org;
	}


	public void setF_org(String f_org) {
		this.f_org = f_org;
	}


	public String getPaycycle() {
		return paycycle;
	}


	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
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

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

//	public String getRosterFileName() {
//		return rosterFileName;
//	}
//
//	public void setRosterFileName(String rosterFileName) {
//		this.rosterFileName = rosterFileName;
//	}
//
//	public File getRoster() {
//		return roster;
//	}
//
//	public void setRoster(File roster) {
//		this.roster = roster;
//	}

//	public static void main(String args[]) {
//
//		UploadRoster up = new UploadRoster();
//		up.uploadRoster1();
//	}

}