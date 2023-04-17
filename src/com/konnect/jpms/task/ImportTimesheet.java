package com.konnect.jpms.task;


import java.io.File;
import java.io.FileInputStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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


import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ImportTimesheet  extends ActionSupport implements ServletRequestAware, IConstants, IStatements {
	private static final long serialVersionUID = 1L;

	HttpServletRequest request;
	CommonFunctions CF;
	UtilityFunctions uF; 
	HttpSession session;
	private File fileUpload1;
	
	public String execute() {

		session = request.getSession();
		uF = new UtilityFunctions();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		request.setAttribute(PAGE, "/jsp/task/ImportTimesheet.jsp");
		request.setAttribute(TITLE, "Import Timesheet");
		
		if(fileUpload1 != null) {
			ImportTimeSheet(fileUpload1, uF);
			return SUCCESS;
		}
		return LOAD;
	}
	
	
	private void ImportTimeSheet(File path,UtilityFunctions uF) {
//		System.out.println("formatTimeSheet====");
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
		List<String> alReport = new ArrayList<String>();
		


		FileInputStream fis = null;
		List<String> alErrorList = new ArrayList<String>();
		try {
			con = db.makeConnection(con);
			con.setAutoCommit(false);


			fis = new FileInputStream(path);
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			System.out.println("Start Reading Excelsheet.... ");
			XSSFSheet attendanceSheet = workbook.getSheetAt(0);

			List<String> dateList = new ArrayList<String>();
			List<String> onOffList = new ArrayList<String>();
			List<List<String>> outerList = new ArrayList<List<String>>();
			
			Iterator rows = attendanceSheet.rowIterator();
			int l = 0;
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
				if (l == 1) {
					while (cells.hasNext()) {
						String cell = cells.next().toString();
						onOffList.add(cell);
						
					}
					
					l++;
					continue;
				}
				
				List<String> cellList = new ArrayList<String>();
				int x = 0;
				while (cells.hasNext()) {

					cellList.add(cells.next().toString());
				}
				outerList.add(cellList);
				

			}

			boolean flag = false;
			if (dateList.size() < 6 || outerList.size() == 0) {
				alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">No Data Available in Sheet.</li>");
				flag = false;
			} else {
									
				for (int k = 0; k < outerList.size(); k++) {
													
					List<String> innerList = outerList.get(k);
					
					
					String empName= innerList.get(0);
									
					// Get Employee Id from employee code
					String empGetCode=innerList.get(1);

					String empID="";
					if (empGetCode != null && !empGetCode.equals("")) {
						pst = con.prepareStatement("select emp_per_id FROM employee_personal_details  where upper(empcode)=?");
						pst.setString(1, empGetCode.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							empID = rs.getString("emp_per_id");
						}
						rs.close();
						pst.close();
					} else {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Employee Code"+ " on Row no-"+(k+3)+".</li>");
						flag = false;
						break;
					}
					
					if(uF.parseToInt(empID)==0) {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"  Employee code does not exists,check the Employee code"+ " on Row no-"+(k+3)+".</li>");
						flag = false;
						break;
					}
					
				
					
					//Get Client Id from Client Name
					
					String nameOfClient=innerList.get(2);
					String clientId="";
					
					if (nameOfClient != null && !nameOfClient.equals("") && !nameOfClient.equals("-")) {
						pst = con.prepareStatement("select client_id FROM client_details where upper(LTRIM(RTRIM(client_name)))=? and isdisabled != true");
						pst.setString(1, nameOfClient.toUpperCase().trim());
						rs = pst.executeQuery();
//						System.out.println("pst==>"+pst);
						while (rs.next()) {
							clientId = rs.getString("client_id");
						}
						rs.close();
						pst.close();
						
						if(uF.parseToInt(clientId) == 0) {
							pst = con.prepareStatement("select client_id FROM client_details where upper(LTRIM(RTRIM(client_name)))=? and isdisabled = true");
							pst.setString(1, nameOfClient.toUpperCase().trim());
							rs = pst.executeQuery();
//							System.out.println("pst==>"+pst);
							while (rs.next()) {
								clientId = rs.getString("client_id");
							}
							rs.close();
							pst.close();
						}
						
						if(uF.parseToInt(clientId) == 0) {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" + "Please check the Client Name"+ " on Row no-"+(k+3)+".</li>");
							flag = false;
							break;
						}
					} /*else {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Client Name"+ " on Row no-"+(k)+".</li>");
						flag = false;
						break;
					}*/
/*
					if(uF.parseToInt(clientId)==0){
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"  Client name does not exists,check the Client name"+ " on Row no-"+(k+3)+".</li>");
						flag = false;
						break;
					}*/
					 
					
					// Get Pro id From Project Name
					String proName=innerList.get(3);

						String proId="";
						if (proName != null && !proName.equals("") && !proName.equals("-")) {
							pst = con.prepareStatement("select pro_id from projectmntnc  where upper(LTRIM(RTRIM(pro_name)))=? and client_id=?");
							pst.setString(1, proName.toUpperCase().trim());
							pst.setInt(2, uF.parseToInt(clientId));
							rs = pst.executeQuery();
//							System.out.println("pst==>"+pst);
							while (rs.next()) {
								proId = rs.getString("pro_id");
							}
							rs.close();
							pst.close();
							if(uF.parseToInt(proId) == 0) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" + "Please check the Project name"+ " on Row no-"+(k+3)+".</li>");
								flag = false;
								break;
							}
						} /*else {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
									"Please check the Project name"+ " on Row no-"+(k+3)+".</li>");
							flag = false;
							break;
						}*/
										
					//Get Task Id from Task Name
					String nameOfTask=innerList.get(4);
					
					String taskId="";
					if (nameOfTask != null && !nameOfTask.equals("")) {
						pst = con.prepareStatement("select task_id FROM activity_info where upper(LTRIM(RTRIM(activity_name)))=? and resource_ids like ? and pro_id=?");
						pst.setString(1, nameOfTask.toUpperCase().trim());
						pst.setString(2, "%" + "," + empID + "," + "%");
						pst.setInt(3, uF.parseToInt(proId));
//						System.out.println("Pst task==>"+pst);
						
						rs = pst.executeQuery();
						while (rs.next()) {
							taskId = rs.getString("task_id");
							
						}
						rs.close();
						pst.close();
					} else {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Task is not Assigned against this Employee"+ " on Row no-"+(k+3)+".</li>");
						flag = false;
						break;
					}
					
					if(uF.parseToInt(taskId)==0 && (uF.parseToInt(clientId)>0 || uF.parseToInt(proId)>0)) {
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
							" Name of Task does not exists,check the Task name"+ " on Row no-"+(k+3)+".</li>");
						flag = false;
						break;
					}

					//Get Approve by Id from Approve by Id Code
					String approvedBy=innerList.get(5);
					
					String approvedById="";
					
					if (approvedBy != null && !approvedBy.equals("")) {
						pst = con.prepareStatement("SELECT emp_per_id FROM employee_personal_details  where upper(empcode)=?");
						pst.setString(1, approvedBy.toUpperCase().trim());
						rs = pst.executeQuery();
						while (rs.next()) {
							approvedById = rs.getString("emp_per_id");
						}
						rs.close();
						pst.close();
					} else {
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								"Please check the Approve by code"+ " on Row no-"+(k+3)+".</li>");
						flag = false;
						break;
					}
					
					if(uF.parseToInt(approvedById)==0) {
						
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
								" Approve by code does not exists,check the Approve by code"+ " on Row no-"+(k+3)+".</li>");
						flag = false;
						break;
					}
					
					System.out.println("empID ===>> " + empID);
					System.out.println("taskId ===>> " + taskId);
					
					for (int j = 6; j <innerList.size(); j++) {
//						System.out.println("Uploding please wait.........");
						String dateRoster = null;	
					    pst = con.prepareStatement("select * from roster_details where emp_id =? and to_date(_date::text, 'YYYY-MM-DD') =?");
						pst.setInt(1, uF.parseToInt(empID));
						if(dateList.get(j).equals("")) {
							dateRoster = (dateList.get((j)-1));
						} else {
							dateRoster = dateList.get(j);
						}
						pst.setDate(2, uF.getDateFormat(dateRoster, DATE_FORMAT));
						rs = pst.executeQuery();
						String startTime1=null;
						String endTime1=null;
						while (rs.next()) {
							startTime1=rs.getString("_from");
							endTime1=rs.getString("_to");
						}
						rs.close();
						pst.close();
						
						if(startTime1 == null || startTime1.equals("") || endTime1 == null || endTime1.equals("")) {
							pst = con.prepareStatement("delete from roster_details where emp_id =? and  to_date(_date::text, 'YYYY-MM-DD') =?");
							pst.setInt(1, uF.parseToInt(empID));
							if(dateList.get(j).equals("")) {
								dateRoster=(dateList.get((j)-1));
							} else {
								dateRoster=dateList.get(j);
							}
							pst.setDate(2, uF.getDateFormat(dateRoster, DATE_FORMAT));
							pst.executeUpdate();
							pst.close();
						}
						
						//Get Pay Cycle
						
						String[] payCycle =CF.getCurrentPayCycle(con,CF.getStrTimeZone(), uF.getDateFormatUtil(dateRoster, DATE_FORMAT), CF);
						String fromDate = payCycle[0];
						String toDate = payCycle[1];
						
//						System.out.println("fromDate==>"+fromDate+"==toDate==>"+toDate);
						// Service Id
						pst = con.prepareStatement("select service_id from roster_details where emp_id =? order by _date desc limit 1");
						pst.setInt(1, uF.parseToInt(empID));
						rs = pst.executeQuery();
						String service_id = null;
						while (rs.next()) {
							service_id = rs.getString("service_id");
						}
						rs.close();
						pst.close();
						
						if(uF.parseToInt(service_id)==0) {
							pst = con.prepareStatement("select service_id from employee_official_details where emp_id=?");
							pst.setInt(1, uF.parseToInt(empID));
							rs = pst.executeQuery();
							String serviceids = null;
							while (rs.next()) {
								serviceids = rs.getString("service_id");
							}
							rs.close();
							pst.close();
							
							if(serviceids!=null && !serviceids.equals("")) {
								String[] temp=serviceids.split(",");
								service_id=temp[1];
							}
						}
						// Set Roster Time	for hole month					
						if(startTime1 ==null || startTime1.equals("") || endTime1==null || endTime1.equals("")){
														
							pst = con.prepareStatement("select * from roster_details where emp_id =? and  to_date(_date::text, 'YYYY-MM-DD') between ? and ?");
							pst.setInt(1, uF.parseToInt(empID));
							pst.setDate(2, uF.getDateFormat(fromDate, DATE_FORMAT));
							pst.setDate(3, uF.getDateFormat(toDate, DATE_FORMAT));
							rs = pst.executeQuery();
//							System.out.println("pst===>"+pst);
							Map<String, String> hmRoster = new HashMap<String, String>();
							while (rs.next()) {
								String roster_date = uF.getDateFormat(rs.getString("_date"),DBDATE, DATE_FORMAT);
								hmRoster.put(roster_date, rs.getString("_from")+ "::::" + rs.getString("_to"));
							}
							rs.close();
							pst.close();
							
							// get Worklocation id from emp id
							
							Map<String, String> hmEmpLocation = CF.getEmpWlocationMap(con);
							String userlocation = hmEmpLocation.get(empID);

							pst = con.prepareStatement("select wlocation_id,wlocation_start_time,wlocation_end_time from work_location_info where wlocation_id =?");
							pst.setInt(1, uF.parseToInt(userlocation));
							rs = pst.executeQuery();
							String locationstarttime = null;
							String locationendtime = null;
							while (rs.next()) {
								locationstarttime = rs.getString("wlocation_start_time");
								locationendtime = rs.getString("wlocation_end_time");
							}
							rs.close();
							pst.close();
														
							int nDateDiff = uF.parseToInt(uF.dateDifference(fromDate, DATE_FORMAT, toDate ,DATE_FORMAT,CF.getStrTimeZone()));
							StringBuilder sbRosterQuery=new StringBuilder();
							sbRosterQuery.append("insert into roster_details (emp_id, _date, _from, _to, isapproved, user_id, service_id, actual_hours, " +
									"attended,is_lunch_ded,shift_id,entry_date,roster_weeklyoff_id) values(?,?,?,?,?,(select user_id from user_details where emp_id=?),?,?,?,?,?,?,?)");
							pst = con.prepareStatement(sbRosterQuery.toString()); 
//							System.out.println("nDateDiff===>"+nDateDiff);
							boolean flag11 = false;
							
							for(int i=0;i<nDateDiff;i++) {
								String _date = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(fromDate, DATE_FORMAT), i), DBDATE, DATE_FORMAT);
								
//								System.out.println("_date===>"+_date);
								if(hmRoster!=null && !hmRoster.containsKey(_date)) {
									Time t = uF.getTimeFormat(locationstarttime, DBTIME);
									long long_startTime = t.getTime();

									Time t1 = uF.getTimeFormat(locationendtime, DBTIME);
									long long_endTime = t1.getTime();

									double total_time = uF.parseToDouble(uF.getTimeDiffInHoursMins(long_startTime,long_endTime));
									pst.setInt(1, uF.parseToInt(empID));
									pst.setDate(2,  uF.getDateFormat(_date,DATE_FORMAT));
									pst.setTime(3, t);
									pst.setTime(4, t1);
									pst.setBoolean(5, true);
									pst.setInt(6, uF.parseToInt(empID));
									pst.setInt(7, uF.parseToInt(service_id));
									pst.setDouble(8, total_time);
									pst.setInt(9, 0);
									pst.setBoolean(10, false);
									pst.setInt(11, 1);
									pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(13, 1);
									pst.addBatch();
									flag11 = true;
								}
							}
							
							if(flag11) {
								int x[] = pst.executeBatch();
								pst.close();
							} else {
								if(pst != null) {
									pst.close();
								}
							}
						}
						
						
						// Again Get Roster  Time
						 	pst = con.prepareStatement("select * from roster_details where emp_id =? and  to_date(_date::text, 'YYYY-MM-DD') =?");
							pst.setInt(1, uF.parseToInt(empID));
							pst.setDate(2, uF.getDateFormat(dateRoster, DATE_FORMAT));
							rs = pst.executeQuery();
//							System.out.println("pst=>"+pst);
							String startTime=null;
							String endTime=null;
							while (rs.next()) {
								
								startTime=rs.getString("_from");
								endTime=rs.getString("_to");
							}
							rs.close();
							pst.close();
						
						if(innerList.get(j) != null && !innerList.get(j).equals("") && !innerList.get(j).equals("-") && !innerList.get(j).toUpperCase().equals("A") && !innerList.get(j).toUpperCase().equals("WO") && !innerList.get(j).toUpperCase().equals("H")) {
							
							String taskTime = ""+innerList.get(j);
							
							// addition of offsite and onsite hours for attendance
							

							/*String strStringValue = ""+innerList.get(j);
							double strDoubleValue=0;
							if(!strStringValue.equals("") && strStringValue!=null && !strStringValue.equals("-") && !strStringValue.equals("WO") && !strStringValue.equals("A") && !strStringValue.equals("H")){
								 strDoubleValue = Double.parseDouble(strStringValue);
							}
							
							String taskTime=""+strDoubleValue;
							
							
							// addition of offsite and onsite hours for attendance
							
							if(onOffList.get(j)!=null && !onOffList.get(j).equals("-") && !onOffList.get(j).equals("") && onOffList.get(j).toUpperCase().trim().equals("ON") ) {
								taskTime=""+strDoubleValue;
								
							} else if(onOffList.get(j)!=null && !onOffList.get(j).equals("-") && !onOffList.get(j).equals("") && onOffList.get(j).toUpperCase().trim().equals("OFF")) {
								
								if(innerList.get(j)!=null && !innerList.get(j).equals("") && !innerList.get(j).equals("-")) {
									
									if(innerList.get(j-1)!=null && !innerList.get(j-1).equals("") && !innerList.get(j-1).equals("-") && !innerList.get(j-1).equals("WO") && !innerList.get(j-1).equals("H") && !innerList.get(j-1).equals("A")) {
										
										taskTime=""+(uF.parseToDouble(innerList.get(j))+uF.parseToDouble(innerList.get(j-1)));
//										System.out.println("TaskTime==>"+(uF.parseToInt(innerList.get(j))+uF.parseToInt(innerList.get(j-1))));
									} else {
										taskTime=""+strDoubleValue;
									}
							   } else {
								   
								   if(innerList.get(j-1)!=null && !innerList.get(j-1).equals("") && !innerList.get(j-1).equals("-") && !innerList.get(j-1).equals("WO") && !innerList.get(j-1).equals("H") && !innerList.get(j-1).equals("A")) {
									   
									   taskTime=""+uF.parseToDouble(innerList.get(j-1));
								   }
							   }
																
							}*/
							// Call insert attendance function
							
							if(taskTime!=null && !taskTime.equals("-") && !taskTime.equals("")) {
								insertintoAttendance(con, empID, dateRoster, taskId, startTime, endTime, taskTime, service_id, uF);
							}
							// Get difference between start and endtime
							
							long ST_TIME = uF.getTimeFormat(uF.getDateFormat(dateRoster, DATE_FORMAT, DBDATE) +" "+startTime, DBTIMESTAMP).getTime();
							long END_TIME = uF.getTimeFormat(uF.getDateFormat(dateRoster, DATE_FORMAT, DBDATE) +" "+endTime, DBTIMESTAMP).getTime();
							String time = uF.getTimeDiffInHoursMins(ST_TIME, END_TIME);
	
							if(onOffList.get(j)!=null && !onOffList.get(j).equals("-") && !onOffList.get(j).equals("") && onOffList.get(j).toUpperCase().trim().equals("ON") ) {

								if(uF.parseToDouble(taskTime) > 0) {
									pst = con.prepareStatement("insert into task_activity (activity_id, activity, task_date, emp_id, actual_hrs, start_time, end_time, " +
										"total_time, is_billable,issent_report,client_id,is_manual,timesheet_paycycle,task_location,activity_description," +
										"approved_by,approved_date,is_approved,generated_date,billable_hrs,submited_date,is_billable_approved) values (?,?,?,?, ?,?,?,?, " +
										"?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
									pst.setInt(1, uF.parseToInt(taskId));
									if(uF.parseToInt(taskId) == 0) {
										pst.setString(2, nameOfTask);
									} else {
										pst.setString(2, "");
									}
									pst.setDate(3, uF.getDateFormat(uF.getDateFormat(dateRoster,DATE_FORMAT,DBDATE),DBDATE));
									pst.setInt(4, uF.parseToInt(empID));
									pst.setDouble(5, uF.parseToDouble(taskTime));
									pst.setTime(6, uF.getTimeFormat(startTime, DBTIME));
									pst.setTime(7, uF.getTimeFormat(endTime, DBTIME));
									pst.setDouble(8, uF.parseToDouble(uF.getTotalTimeMinutes60To100(time)));
									pst.setBoolean(9, true);
									pst.setBoolean(10, false);
									pst.setInt(11, uF.parseToInt(clientId));
									pst.setBoolean(12, false);
									pst.setInt(13, uF.parseToInt(payCycle[2]));
									pst.setString(14, "ONS");
									pst.setString(15, "");
									pst.setInt(16, uF.parseToInt(approvedById));
									pst.setDate(17, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(18, 2);
									pst.setDate(19, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setDouble(20, uF.parseToDouble(taskTime));
									pst.setDate(21, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(22, 0);
									pst.execute();
									pst.close();
									flag=true;
//									System.out.println("pst==>"+pst);	
								} 
						
							} else if(onOffList.get(j)!=null && !onOffList.get(j).equals("-") && !onOffList.get(j).equals("") && onOffList.get(j).toUpperCase().trim().equals("OFF")) {
									
								if(uF.parseToDouble(taskTime) > 0) {
									pst = con.prepareStatement("insert into task_activity (activity_id, activity, task_date, emp_id, actual_hrs, start_time, end_time, " +
										"total_time, is_billable,issent_report,client_id,is_manual,timesheet_paycycle,task_location,activity_description," +
										"approved_by,approved_date,is_approved,generated_date,billable_hrs,submited_date,is_billable_approved) values (?,?,?,?, ?,?,?,?, " +
										"?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
									pst.setInt(1, uF.parseToInt(taskId));
									if(uF.parseToInt(taskId) == 0) {
										pst.setString(2, nameOfTask);
									} else {
										pst.setString(2, "");
									}
									pst.setDate(3, uF.getDateFormat(uF.getDateFormat(dateRoster,DATE_FORMAT,DBDATE),DBDATE));
									pst.setInt(4, uF.parseToInt(empID));
									pst.setDouble(5, uF.parseToDouble(taskTime));
									pst.setTime(6, uF.getTimeFormat(startTime, DBTIME));
									pst.setTime(7, uF.getTimeFormat(endTime, DBTIME));
									pst.setDouble(8, uF.parseToDouble(uF.getTotalTimeMinutes60To100(time)));
									pst.setBoolean(9, true);
									pst.setBoolean(10, false);
									pst.setInt(11, uF.parseToInt(clientId));
									pst.setBoolean(12, false);
									pst.setInt(13, uF.parseToInt(payCycle[2]));
									pst.setString(14, "OFS");
									pst.setString(15, "");
									pst.setInt(16, uF.parseToInt(approvedById));
									pst.setDate(17, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(18, 2);
									pst.setDate(19, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setDouble(20, uF.parseToDouble(taskTime));
									pst.setDate(21, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(22, 0);
									pst.execute();
									pst.close();
									flag=true;
//									System.out.println("pst==>"+pst);
								}
							}
						}
					}
				}
			}
			
			if (flag) {
				con.commit();
				System.out.println("Time Sheet imported sucessfully ..........");
				session.setAttribute(MESSAGE, SUCCESSM + "TimeSheet Imported Successfully!" + END);
			} else {
				con.rollback();
				if (alErrorList.size() > 0) {
					alReport.add(alErrorList.get(alErrorList.size() - 1));
				}
				System.out.println("alReport ===>> " + alReport);
				System.out.println("Time Sheet not imported ............");
				session.setAttribute("alErrorReport", alReport);
				session.setAttribute(MESSAGE,ERRORM + "TimeSheet not imported. Please check imported file." + END);
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
			System.out.println("alReport ===>> " + alReport);
			session.setAttribute("alErrorReport", alReport);
			session.setAttribute(MESSAGE, ERRORM + "TimeSheet not imported. Please check imported file." + END);
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
	
	public void insertintoAttendance(Connection con,String empId,String taskDate, String activityId, String startTime, String endTime, String tasktime, String serviceID, UtilityFunctions uF) {
		
		PreparedStatement pst = null, pst1=null, pst2=null, pst3=null, pst4=null;
		ResultSet rst = null;
//		UtilityFunctions uF = new UtilityFunctions();
		try {
		    	
					double total_time = 0;
//					System.out.println("activityId ===================>>   " +activityId);
					pst = con.prepareStatement("select * from attendance_details where emp_id =? and in_out_timestamp_actual::text " +
						" LIKE '"+ uF.getDateFormat(taskDate.trim(),DATE_FORMAT, DBDATE) + "%' and in_out in ('IN','OUT')");
					pst.setInt(1, uF.parseToInt(empId));
					rst = pst.executeQuery();
					boolean flag_IN = false;
					boolean flag_OUT = false;
					String atten_id_IN = null;
					String atten_id_OUT = null;
					double hrsWorked = 0.0d;
//					System.out.println("hrsWorked ===>> " + hrsWorked+" -- taskDate ===>> " + taskDate);
					while (rst.next()) {
						if (rst.getString("in_out") != null && rst.getString("in_out").equals("IN")) {
							atten_id_IN = rst.getString("atten_id");
							flag_IN = true;
							
						}
						if (rst.getString("in_out") != null && rst.getString("in_out").equals("OUT")) {
							atten_id_OUT = rst.getString("atten_id");
							flag_OUT = true;
							hrsWorked = rst.getDouble("hours_worked");
						}
					}
					rst.close();
					pst.close();
					
					if (taskDate != null && !taskDate.equals("")) {
						
						Time timeStart = uF.getTimeFormat(startTime, DBTIME);
						long long_startTime = timeStart.getTime();

						Time timeEnd = uF.getTimeFormat(endTime, DBTIME);
						long long_endTime = timeEnd.getTime();

						tasktime = ""+(hrsWorked + uF.parseToDouble(tasktime));
						// total_time=uF.getTimeDifference(long_startTime,
						// long_endTime);
						total_time = uF.parseToDouble(uF.getTimeDiffInHoursMins(long_startTime, long_endTime));
						
//						System.out.println("tasktime ===>> " + tasktime+" -- total_time ===>> " + total_time);
						
						if (flag_IN == true) {
							pst1 = con.prepareStatement("update attendance_details set emp_id=?, in_out_timestamp=?, in_out_timestamp_actual=?,"
									+ " in_out=?, service_id=? where atten_id=?");
							pst1.setInt(1, uF.parseToInt(empId));
							pst1.setTimestamp(2, uF.getTimeStamp(uF.getDateFormat(taskDate, DATE_FORMAT) + "" + uF.getTimeFormat(startTime, "HH:mm:ss"), "yyyy-MM-ddHH:mm:ss"));
							pst1.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(taskDate, DATE_FORMAT) + "" + uF.getTimeFormat(startTime, "HH:mm:ss"), "yyyy-MM-ddHH:mm:ss"));
							pst1.setString(4, "IN");
							pst1.setInt(5, uF.parseToInt(serviceID));
							pst1.setInt(6, uF.parseToInt(atten_id_IN));
							pst1.executeUpdate();
//							System.out.println("pst1 ===>> " + pst1);
//							flag1 = true;
							java.util.Date addedtime = getAddedTime(taskDate, startTime, tasktime);
							
							if (flag_OUT) {
								pst2 = con.prepareStatement("update attendance_details set emp_id=?, in_out_timestamp=?, in_out_timestamp_actual=?,"
										+ " in_out=?, service_id=?,hours_worked=? where atten_id=?");
								pst2.setInt(1, uF.parseToInt(empId));
								pst2.setTimestamp(2, new java.sql.Timestamp(addedtime.getTime()));
								pst2.setTimestamp(3, new java.sql.Timestamp(addedtime.getTime()));
								pst2.setString(4, "OUT");
								pst2.setInt(5, uF.parseToInt(serviceID));
								pst2.setDouble(6, uF.parseToDouble(tasktime));
								pst2.setInt(7, uF.parseToInt(atten_id_OUT));
								pst2.executeUpdate();
//								System.out.println("pst2 ===>> " + pst2);
//								flag2 = true;
							} else {
								pst3 = con.prepareStatement("insert into attendance_details(emp_id, in_out_timestamp, in_out_timestamp_actual,"
										+ " in_out, service_id,hours_worked) VALUES (?,?,?,?,?,?)");
								pst3.setInt(1, uF.parseToInt(empId));
								pst3.setTimestamp(2, new java.sql.Timestamp(addedtime.getTime()));
								pst3.setTimestamp(3, new java.sql.Timestamp(addedtime.getTime()));
								pst3.setString(4, "OUT");
								pst3.setInt(5, uF.parseToInt(serviceID));
								pst3.setDouble(6, uF.parseToDouble(tasktime));
								pst3.executeUpdate();
//								System.out.println("pst3 ===>> " + pst3);
//								flag3 = true;
							}

						} else {
							pst4 = con.prepareStatement("insert into attendance_details(emp_id, in_out_timestamp, in_out_timestamp_actual,"
									+ " in_out, service_id) VALUES (?,?,?,?,?)");
							pst4.setInt(1, uF.parseToInt(empId));
							pst4.setTimestamp(2, uF.getTimeStamp(uF.getDateFormat(taskDate, DATE_FORMAT) + "" + uF.getTimeFormat(startTime, "HH:mm:ss"), "yyyy-MM-ddHH:mm:ss"));
							pst4.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(taskDate, DATE_FORMAT) + "" + uF.getTimeFormat(startTime, "HH:mm:ss"), "yyyy-MM-ddHH:mm:ss"));
							pst4.setString(4, "IN");
							pst4.setInt(5, uF.parseToInt(serviceID));
							pst4.executeUpdate();
//							System.out.println("pst4 ===>> " + pst4);
							
//							flag4 = true;
							java.util.Date addedtime = getAddedTime(taskDate, startTime, tasktime);
							pst3 = con.prepareStatement("insert into attendance_details(emp_id, in_out_timestamp, in_out_timestamp_actual,"
									+ " in_out, service_id,hours_worked) VALUES (?,?,?,?,?,?)");
							pst3.setInt(1, uF.parseToInt(empId));
							pst3.setTimestamp(2, new java.sql.Timestamp(addedtime.getTime()));
							pst3.setTimestamp(3, new java.sql.Timestamp(addedtime.getTime()));
							pst3.setString(4, "OUT");
							pst3.setInt(5, uF.parseToInt(serviceID));
							pst3.setDouble(6, uF.parseToDouble(tasktime));
							pst3.executeUpdate();
//							System.out.println("pst3 ===>> " + pst3);
//							flag3 = true;
						}

					} 

					String convertTime = getConvertedTime(tasktime);

					double timeAvg = (uF.parseToDouble(tasktime) / total_time) * 100;

					double attendance = 0;

					if (timeAvg > 0 && timeAvg < 70) {
						attendance = 0.5;
					} else if (timeAvg > 70) {
						attendance = 1;
					}

					pst = con.prepareStatement("update attendance_payroll set _hour=?, attendance=?, attendance_type=? " +
							"where emp_id=? and _date=? ");
					pst.setTime(1, uF.getTimeFormat(convertTime, DBTIME));
					pst.setDouble(2, attendance);
					pst.setString(3, "");
					pst.setInt(4, uF.parseToInt(empId));
					pst.setDate(5, uF.getDateFormat(taskDate, DATE_FORMAT));
					int x = pst.executeUpdate();
					pst.close();
//					System.out.println("pst update attendance_payroll ===>> " + pst);
					if (x == 0) {
						pst = con.prepareStatement("insert into attendance_payroll(emp_id, _date, _hour, attendance, attendance_type) " +
								"VALUES (?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(empId));
						pst.setDate(2, uF.getDateFormat(taskDate, DATE_FORMAT));
						pst.setTime(3, uF.getTimeFormat(convertTime, DBTIME));
						pst.setDouble(4, attendance);
						pst.setString(5, "");
						pst.execute();
						pst.close();
//						System.out.println("pst insert attendance_payroll ===>> " + pst);
					}
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst1 != null) {
				try {
					pst1.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst2 != null) {
				try {
					pst2.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst3 != null) {
				try {
					pst3.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst4 != null) {
				try {
					pst4.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	private Date getAddedTime(String task_date, String locationstarttime, String tasktime) {
		int hour = 0;
		int minute = 0;
		UtilityFunctions uF = new UtilityFunctions();
		if (tasktime != null && tasktime.contains(".")) {
			hour = (int) uF.parseToDouble(tasktime);
			double minustime = uF.parseToDouble(tasktime) - uF.parseToDouble("" + hour);
			double minutetime = uF.parseToDouble(uF.formatIntoTwoDecimal(minustime)) * 100;
			minute = (int) minutetime;
		} else if (tasktime != null) {
			hour = uF.parseToInt(tasktime);
			minute = 0;
		} else {
			hour = 0;
			minute = 0;
		}

		Date util_date = uF.getDateFormatUtil(task_date + " " + locationstarttime, "dd/MM/yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.setTime(util_date);
		cal.add(Calendar.HOUR_OF_DAY, hour);
		cal.add(Calendar.MINUTE, minute);

		java.util.Date utilDate = cal.getTime();

		return utilDate;
	}

	private String getConvertedTime(String tasktime) {
		int hour = 0;
		int minute = 0;
		UtilityFunctions uF = new UtilityFunctions();

		String convertedTime = null;

		if (tasktime != null && tasktime.contains(".")) {
			hour = (int) uF.parseToDouble(tasktime);
			double minustime = uF.parseToDouble(tasktime) - uF.parseToDouble("" + hour);
			double minutetime = uF.parseToDouble(uF.formatIntoTwoDecimal(minustime)) * 100;
			minute = (int) minutetime;
		} else if (tasktime != null) {
			hour = uF.parseToInt(tasktime);
			minute = 0;
		} else {
			hour = 0;
			minute = 0;
		}
		convertedTime = (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);

		return convertedTime;
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public File getFileUpload1() {
		return fileUpload1;
	}
	
	public void setFileUpload1(File fileUpload1) {
		this.fileUpload1 = fileUpload1;
	}

}
