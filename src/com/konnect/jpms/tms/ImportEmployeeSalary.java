package com.konnect.jpms.tms;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
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

import org.apache.poi.xssf.usermodel.XSSFCell;
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

public class ImportEmployeeSalary extends ActionSupport implements ServletRequestAware, IConstants,IStatements {

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
			if(CF==null){
				return LOGIN;
			}
			
			
			
			
//			boolean isView  = CF.getAccess(session, request, uF);
//			if(!isView){
//				request.setAttribute(PAGE, PAccessDenied);
//				request.setAttribute(TITLE, TAccessDenied);
//				return ACCESS_DENIED;
//			}
			
			 
			request.setAttribute(PAGE, "/jsp/tms/ImportEmployeeSalary.jsp");
			request.setAttribute(TITLE, "Import Attendance");
			
			if(fileUpload!=null){
				laborAttendance(fileUpload,uF);
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		return SUCCESS;
	}
	
//	public void format5Attendance(File path, UtilityFunctions uF) {
//
//
//		// }
//		Connection con = null;
//		Database db = new Database();
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//
//		try {
//
//			con = db.makeConnection(con);
//			con.setAutoCommit(true);
////			
////			
//			BufferedReader br = new BufferedReader(new FileReader(path));
//			String line = null;
//			int cnt=0;
//			while ((line = br.readLine()) != null) {
//				if(cnt==0){
//					cnt++;
//					continue;
//				}
//				try {
//					List<String> cellList = Arrays.asList(line.split(","));
//					if(cnt<4){
//					System.out.println("cellList=="+cellList.size());
//					System.out.println("cellList="+cellList);
//
//					System.out.println("cellList="+cellList.get(10));
//					System.out.println("cellList="+cellList.get(11));
//
//					cnt++;
//					}
//					if(cellList.size()<11)
//						continue;
//					String empcode = cellList.get(1);
//					String strDate = cellList.get(0);
//					String strInTime=cellList.get(10);
//					String strOutTime=cellList.get(11);
//					String emp_per_id = null;
//					int servic_id = 0;
//					pst = con
//					.prepareStatement("Select emp_per_id,service_id,wlocation_id from employee_personal_details,employee_official_details where emp_per_id=emp_id and empcode=?");
//			pst.setString(1,empcode.trim());
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				emp_per_id = rs.getString("emp_per_id");
//
//				String temp = rs.getString("service_id");
//
//				if (temp.contains(",")) {
//					String str[] = temp.split(",");
//					servic_id = Integer.parseInt(str[0]);
//
//				} else {
//					servic_id = Integer.parseInt(rs.getString(1));
//				}
//			
//			}
//			
//			if(emp_per_id==null)
//				continue;
//			// =================================================================================================================================
//			String rosterInTime = null;
//			String rosterOutTime = null;
//			// System.out.println(uF.getDateFormat(cell2,
//			// "MM/dd/yyyy"));
//			pst = con
//					.prepareStatement("Select * from roster_details where emp_id=? and _date=?");
//			pst.setInt(1,uF.parseToInt( emp_per_id));
//			pst.setDate(2, uF.getDateFormat(strDate, "dd-MM-yyyy"));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				rosterInTime = rs.getString("_from");
//				rosterOutTime = rs.getString("_to");
//			}
//			
//			if(rosterInTime==null || rosterOutTime==null )
//				continue;
//
//			try{
//				pst = con.prepareStatement("Select atten_id from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=? ");
//				pst.setInt(1,uF.parseToInt( emp_per_id));
//				pst.setDate(2, uF.getDateFormat(strDate, "dd-MM-yyyy"));
//				pst.setString(3,"IN");
//					
//				rs = pst.executeQuery();
//				if (rs.next()) {
////					System.out.println("User Already Attand the Office");
//				}else{
//					
//					if(strInTime!=null && strInTime.length()>0){
//					long lStart = uF.getTimeFormat(strDate +" "+strInTime, "dd-MM-yyyy HH:mm:ss").getTime();
//					long in = uF.getTimeFormat(strDate+" "+rosterInTime,"dd-MM-yyyy HH:mm:ss").getTime();
//					pst = con
//					.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
//							+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
//			pst.setInt(1,uF.parseToInt( emp_per_id));
//			pst.setTimestamp(2,uF.getTimeStamp(strDate+" "+getTimeFormat(strInTime, "HH:mm:ss"),"dd-MM-yyyy HH:mm:ss"));
//			pst.setString(3, " ");
//			pst.setString(4, "IN");
//			pst.setInt(5, 1);
//			pst.setString(6, " ");
//			pst.setNull(7, java.sql.Types.DOUBLE);
//			pst.setTimestamp(8,	uF.getTimeStamp(strDate+" "+getTimeFormat(strInTime, "HH:mm:ss"),"dd-MM-yyyy HH:mm:ss"));
//			pst.setInt(9, servic_id);
//			
//			if(in>0 && in>lStart){
//				pst.setDouble(10, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));
//				
//			}else if(lStart>0 && lStart>in){
//				pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));
//				
//			}else{
//				pst.setDouble(10, 0);
//				
//			}
////			pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));
//
//			pst.executeUpdate();
//			pst.close();
//					}
//				}
//				}catch(Exception e){
//				}
//				
//				try{
//				pst = con.prepareStatement("Select atten_id from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=? ");
//				pst.setInt(1,uF.parseToInt( emp_per_id));
//				pst.setDate(2, uF.getDateFormat(strDate, "dd-MM-yyyy"));
//				pst.setString(3,"OUT");
//					
//				rs = pst.executeQuery();
//				if (rs.next()) {
////					System.out.println("User Already Attand the Office");
//				}else{
//					if(strOutTime!=null && strOutTime.length()>0){
//					long lStart = uF.getTimeFormat(strDate +" "+strOutTime, "dd-MM-yyyy HH:mm:ss").getTime();
//					long in = uF.getTimeFormat(strDate+" "+rosterOutTime,"dd-MM-yyyy HH:mm:ss").getTime();
//					long lEnd = uF.getTimeFormat(strDate+" "+strInTime,"dd-MM-yyyy HH:mm:ss").getTime();
//					pst = con
//					.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
//							+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
//					pst.setInt(1,uF.parseToInt( emp_per_id));
//					pst.setTimestamp(2,uF.getTimeStamp(strDate+" "+getTimeFormat(strOutTime, "HH:mm"),"dd-MM-yyyy HH:mm:ss"));
//					pst.setString(3, " ");
//					pst.setString(4, "OUT");
//					pst.setInt(5, 1);
//					pst.setString(6, " ");
//					pst.setDouble(7, uF.parseToDouble(uF.getTimeDiffInHoursMins(lEnd,lStart)));
//					pst.setTimestamp(8,uF.getTimeStamp(strDate+" "+getTimeFormat(strOutTime, "HH:mm"),"dd-MM-yyyy HH:mm:ss"));
//					pst.setInt(9, servic_id);
//					
//					if(in>0 && in>lStart){
//						pst.setDouble(10, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));
//						
//					}else if(lStart>0 && lStart>in){
//						pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));
//						
//					}else{
//						pst.setDouble(10, 0);
//						
//					}
//					
//					pst.executeUpdate();
//			pst.close();
//					}
//				}
//				}catch(Exception e){
//				}
//			
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			} // end main for loop
//			br.close();
//		}// try block end
//
//		catch (Exception e) {
//
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//
//		}
//
//	}
	
	
//	public void format3Attendance(File path,UtilityFunctions uF) {
//
////		if (!fileUpload1ContentType.contains("text/plain")) {
////			alReport.add("Please Check File format.This must be a .txt file");
////			return;
////		}
//		Connection con = null;
//		Database db = new Database();
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//
//		// System.out.println("fileUpload2ContentType==="+fileUpload2ContentType);
//		try {
//
//			con = db.makeConnection(con);
//			con.setAutoCommit(true);
//			
//			
//			FileInputStream fis = new FileInputStream(path);
//
//			XSSFWorkbook workbook = new XSSFWorkbook(fis);
//
//			System.out.println("Start Reading Excelsheet.... ");
//			XSSFSheet attendanceSheet = workbook.getSheetAt(0);
//			List<List<String>> dataList = new ArrayList<List<String>>();
//			Iterator rows = attendanceSheet.rowIterator();
//
//			while (rows.hasNext()) {
//				XSSFRow row = (XSSFRow) rows.next();
//
//				Iterator cells = row.cellIterator();
//				List<String> cellList = new ArrayList<String>();
//
//				while (cells.hasNext()) {
//					String cell =  cells.next().toString();
//					cellList.add(cell);
//				}
//				dataList.add(cellList);
//			}
//			String empcode=null;
//			String dateformat= "dd-MMM-yyyy";
//			for(int i=9;i<dataList.size();i++){
//				List<String> cellList =dataList.get(i);
//
//				if(cellList.size()>=14){
//					
//					
////					if(cellList.get(14).trim().equalsIgnoreCase("Present")){
//						
//						empcode=cellList.get(1).trim();
//						alReport.add(empcode+" Has been Imported.");
//						String strDate=cellList.get(5);
//						String strInTime=cellList.get(8);
//						String strOutTime=cellList.get(9);
//						int cnt=0;
//						try{
//							uF.getTimeFormat(strDate +" "+strInTime, "dd-MMM-yyyy HH:mm").getTime();
//						}catch(Exception e){
//							cnt++;
//						}
//						
//						try{
//							uF.getTimeFormat(strDate +" "+strOutTime, "dd-MMM-yyyy HH:mm").getTime();
//						}catch(Exception e){
//							cnt++;
//						}
//						if(cnt==2)
//							continue;
//					pst = con
//					.prepareStatement("Select emp_per_id,service_id,wlocation_id from employee_personal_details,employee_official_details where emp_per_id=emp_id and empcode=?");
//			pst.setString(1,empcode.trim());
//			rs = pst.executeQuery();
//			String strWLocation=null;
//			String emp_per_id = null;
//			int servic_id = 0;
//			while (rs.next()) {
//				emp_per_id = rs.getString("emp_per_id");
//				strWLocation = rs.getString("wlocation_id");
//
//				String temp = rs.getString("service_id");
//
//				if (temp.contains(",")) {
//					String str[] = temp.split(",");
//					servic_id = Integer.parseInt(str[0]);
//
//				} else {
//					servic_id = Integer.parseInt(rs.getString(1));
//				}
//			
//			}
//			String inTime = null;
//			String outTime = null;
//			pst = con
//					.prepareStatement("Select * from roster_details where emp_id=? and _date=?");
//			pst.setInt(1,uF.parseToInt( emp_per_id));
//			pst.setDate(2, uF.getDateFormat(strDate,dateformat));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				inTime = rs.getString("_from");
//				outTime = rs.getString("_to");
//			}
//			if(inTime==null || outTime==null )
//				continue;
//			
//			try{
//			pst = con.prepareStatement("Select atten_id from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=? ");
//			pst.setInt(1,uF.parseToInt( emp_per_id));
//			pst.setDate(2, uF.getDateFormat(strDate, "dd-MMM-yyyy"));
//			pst.setString(3,"IN");
//				
//			rs = pst.executeQuery();
//			if (rs.next()) {
//				System.out.println("User Already Attand the Office");
//			}else{
//				try{
//					uF.getTimeFormat(strDate +" "+strInTime, "dd-MMM-yyyy HH:mm").getTime();
//				}catch(Exception e){
//					strInTime=inTime;
//				}
//				long lStart = uF.getTimeFormat(strDate +" "+strInTime, "dd-MMM-yyyy HH:mm").getTime();
//				long in = uF.getTimeFormat(strDate+" "+inTime,"dd-MMM-yyyy HH:mm").getTime();
//				pst = con
//				.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
//						+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
//		pst.setInt(1,uF.parseToInt( emp_per_id));
//		pst.setTimestamp(2,uF.getTimeStamp(strDate+" "+getTimeFormat(strInTime, "HH:mm"),"dd-MMM-yyyy HH:mm:ss"));
//		pst.setString(3, " ");
//		pst.setString(4, "IN");
//		pst.setInt(5, 1);
//		pst.setString(6, " ");
//		pst.setNull(7, java.sql.Types.DOUBLE);
//		pst.setTimestamp(8,	uF.getTimeStamp(strDate+" "+getTimeFormat(strInTime, "HH:mm"),"dd-MMM-yyyy HH:mm:ss"));
//		pst.setInt(9, servic_id);
//		
//		if(in>0 && in>lStart){
//			pst.setDouble(10, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));
//			
//		}else if(lStart>0 && lStart>in){
//			pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));
//			
//		}else{
//			pst.setDouble(10, 0);
//			
//		}
////		pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));
//
//		pst.executeUpdate();
//		pst.close();
//			}
//			}catch(Exception e){
//			}
//			
//			try{
//			pst = con.prepareStatement("Select atten_id from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=? ");
//			pst.setInt(1,uF.parseToInt( emp_per_id));
//			pst.setDate(2, uF.getDateFormat(strDate, "dd-MMM-yyyy"));
//			pst.setString(3,"OUT");
//				
//			rs = pst.executeQuery();
//			if (rs.next()) {
//				System.out.println("User Already Attand the Office");
//			}else{
//				try{
//					uF.getTimeFormat(strDate +" "+strOutTime, "dd-MMM-yyyy HH:mm").getTime();
//				}catch(Exception e){
//					strOutTime=outTime;
//				}
//				long lStart = uF.getTimeFormat(strDate +" "+strOutTime, "dd-MMM-yyyy HH:mm").getTime();
//				long in = uF.getTimeFormat(strDate+" "+outTime,"dd-MMM-yyyy HH:mm").getTime();
//				long lEnd = uF.getTimeFormat(strDate+" "+strInTime,"dd-MMM-yyyy HH:mm").getTime();
//				pst = con
//				.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
//						+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
//				pst.setInt(1,uF.parseToInt( emp_per_id));
//				pst.setTimestamp(2,uF.getTimeStamp(strDate+" "+getTimeFormat(strOutTime, "HH:mm"),"dd-MMM-yyyy HH:mm:ss"));
//				pst.setString(3, " ");
//				pst.setString(4, "OUT");
//				pst.setInt(5, 1);
//				pst.setString(6, " ");
//				pst.setDouble(7, uF.parseToDouble(uF.getTimeDiffInHoursMins(lEnd,lStart)));
//				pst.setTimestamp(8,uF.getTimeStamp(strDate+" "+getTimeFormat(strOutTime, "HH:mm"),"dd-MMM-yyyy HH:mm"));
//				pst.setInt(9, servic_id);
//				
//				if(in>0 && in>lStart){
//					pst.setDouble(10, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));
//					
//				}else if(lStart>0 && lStart>in){
//					pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));
//					
//				}else{
//					pst.setDouble(10, 0);
//					
//				}
//				
//				pst.executeUpdate();
//		pst.close();
//			}
//			}catch(Exception e){
//			}
//				}
//
//			} // end main for loop
//		}// try block end
//
//		catch (Exception e) {
//
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//
//		}
//		request.setAttribute("alReport",alReport);
//	}
	
	public String getDayName(java.util.Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
			return "Sat";
		}else if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY){
			return "Fri";
		}else if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.THURSDAY){
			return "Thu";
		}else if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY){
			return "Wed";
		}else if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.TUESDAY){
			return "Tue";
		}else if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY){
			return "Mon";
		}else{
			return "Sun";
		}
		
	}
	
//	public void format2Attendance(File path, UtilityFunctions uF) {
//
//
//		// }
//		Connection con = null;
//		Database db = new Database();
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//
//		try {
//
//			con = db.makeConnection(con);
//			con.setAutoCommit(true);
//			String emp_per_id = null;
//			int servic_id = 0;
//			BufferedReader br = new BufferedReader(new FileReader(path));
//			String line = null;
//
//			while ((line = br.readLine()) != null) {
//				try {
//					List<String> cellList = Arrays.asList(line.split(","));
//
//					String empcode = null;
//					String strStartDate = cellList.get(4).replace("\"", "");
//					String strEndDate = cellList.get(6).replace("\"", "");
//					List<String> attendanceStatus = new ArrayList<String>();
//					List<String> shiftList = new ArrayList<String>();
//					List<String> inTimeList = new ArrayList<String>();
//					List<String> outTimeList = new ArrayList<String>();
//					List<String> lateList = new ArrayList<String>();
//					List<String> earlyList = new ArrayList<String>();
//					List<String> overtimeList = new ArrayList<String>();
//					List<String> dateList = new ArrayList<String>();
//
//					boolean flag = false, flag1 = false, flag2 = false, flag4 = false, flag5 = false, flag6 = false, flag7 = false, flag8 = false, flag9 = false;
//					int cnt = 0;
//					int cnt1 = 0;
//
//					for (int i = 0; i < cellList.size(); i++) {
//
//						if (flag
//								|| cellList.get(i).replace("\"", "")
//										.equals("Page No.:")) {
//							flag = true;
//
//							if (cellList
//									.get(i + 1)
//									.replace("\"", "")
//									.equalsIgnoreCase(
//											getDayName(uF.getDateFormat(
//													strStartDate, DATE_FORMAT)))) {
//								flag = false;
//							}
//							if (flag)
//								dateList.add(cellList.get(i + 1).replace("\"",
//										""));
//						} else if (cellList.get(i).replace("\"", "")
//								.equals("Employee Code")) {
//							empcode = cellList.get(i + 1).replace("\"", "");
//						} else if (flag1
//								|| cellList.get(i).replace("\"", "")
//										.equals("Status")) {
//							flag1 = true;
//
//							if (cellList.get(i + 1).replace("\"", "")
//									.equalsIgnoreCase("Shift")) {
//								flag1 = false;
//							}
//							if (flag1)
//								attendanceStatus.add(cellList.get(i + 1)
//										.replace("\"", ""));
//
//						} else if (flag2
//								|| cellList.get(i).replace("\"", "")
//										.equals("Shift")) {
//							flag2 = true;
//
//							if (cellList.get(i + 1).replace("\"", "")
//									.equalsIgnoreCase("Time In")) {
//								flag2 = false;
//							}
//							if (flag2)
//								shiftList.add(cellList.get(i + 1).replace("\"",
//										""));
//
//						} else if (flag4
//								|| cellList.get(i).replace("\"", "")
//										.equals("Time In")) {
//							flag4 = true;
//
//							if (cellList.get(i + 1).replace("\"", "")
//									.equalsIgnoreCase("Time Out")) {
//								flag4 = false;
//							}
//							if (flag4)
//								inTimeList.add(cellList.get(i + 1).replace(
//										"\"", ""));
//						} else if (flag5
//								|| cellList.get(i).replace("\"", "")
//										.equals("Time Out")) {
//							flag5 = true;
//
//							if (cellList.get(i + 1).replace("\"", "")
//									.equalsIgnoreCase("Late")) {
//								flag5 = false;
//							}
//							if (flag5)
//								outTimeList.add(cellList.get(i + 1).replace(
//										"\"", ""));
//						} else if (flag6
//								|| cellList.get(i).replace("\"", "")
//										.equals("Late")) {
//							if (cellList.get(i).replace("\"", "")
//									.equals("Late")) {
//								if (cnt == 0)
//									cnt++;
//								else
//									continue;
//							}
//							flag6 = true;
//
//							if (cellList.get(i + 1).replace("\"", "")
//									.equalsIgnoreCase("Early")) {
//								flag6 = false;
//							}
//							if (flag6)
//								lateList.add(cellList.get(i + 1).replace("\"",
//										""));
//						} else if (flag7
//								|| cellList.get(i).replace("\"", "")
//										.equals("Early")) {
//							if (cellList.get(i).replace("\"", "")
//									.equals("Early")) {
//								if (cnt1 == 0)
//									cnt1++;
//								else
//									continue;
//							}
//							flag7 = true;
//
//							if (cellList.get(i + 1).replace("\"", "")
//									.equalsIgnoreCase("OverTime")) {
//								flag7 = false;
//							}
//							if (flag7)
//								earlyList.add(cellList.get(i + 1).replace("\"",
//										""));
//						} else if (flag8
//								|| cellList.get(i).replace("\"", "")
//										.equals("OverTime")) {
//							flag8 = true;
//
//							if (cellList.get(i + 1).replace("\"", "")
//									.equalsIgnoreCase("WorkHours")) {
//								flag8 = false;
//							}
//							if (flag8)
//								overtimeList.add(cellList.get(i + 1).replace(
//										"\"", ""));
//
//						}
//
//					}
//
//					System.out.println("empcode===" + empcode);
//					String strWLocation = null;
//					if (empcode.contains(".")) {
//						empcode = empcode.substring(0, empcode.indexOf("."));
//
//					}
//
//					// // Select Employ ID
//					pst = con
//							.prepareStatement("Select emp_id,service_id,wlocation_id from employee_official_details eod,employee_personal_details epd where eod.emp_id=epd.emp_per_id and empcode=?");
//					pst.setString(1, empcode.trim());
//					rs = pst.executeQuery();
//					while (rs.next()) {
//						emp_per_id = rs.getString("emp_id");
//						strWLocation = rs.getString("wlocation_id");
//
//						String temp = rs.getString("service_id");
//
//						if (temp.contains(",")) {
//							String str[] = temp.split(",");
//							servic_id = Integer.parseInt(str[0]);
//
//						} else {
//							servic_id = Integer.parseInt(rs.getString(1));
//						}
//
//					}
//
//					String datediff = uF.dateDifference(strStartDate,
//							DATE_FORMAT, strEndDate, DATE_FORMAT);
//					Date startDate = uF
//							.getDateFormat(strStartDate, DATE_FORMAT);
//					Calendar calendar = Calendar.getInstance();
//					calendar.setTime(startDate);
//					if (uF.parseToInt(datediff) == dateList.size()) {
//
//						for (int i = 0; i < dateList.size(); i++) {
//
//							if (attendanceStatus.get(i) != null
//									&& (attendanceStatus.get(i)
//											.equalsIgnoreCase("AB") || attendanceStatus
//											.get(i).equalsIgnoreCase("WO"))) {
//								calendar.add(Calendar.DATE, 1);
//								continue;
//							}
//							String strCurrentDBDate = calendar
//									.get(Calendar.YEAR)
//									+ "-"
//									+ calendar.get(Calendar.MONTH)
//									+ "-"
//									+ calendar.get(Calendar.DATE);
//							System.out.println("===" + strCurrentDBDate);
//							System.out.println("dateList==" + dateList.get(i));
//
//							String inTime = null;
//							String outTime = null;
//							pst = con
//									.prepareStatement("select * from shift_details where shift_code=?");
//							pst.setString(1, shiftList.get(i));
//							rs = pst.executeQuery();
//							while (rs.next()) {
//								inTime = rs.getString("_from");
//								outTime = rs.getString("_to");
//							}
//							if (inTime == null && outTime == null)
//								continue;
//
//							pst = con
//									.prepareStatement("Select atten_id from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=? ");
//							pst.setInt(1, uF.parseToInt(emp_per_id));
//							pst.setDate(2,
//									uF.getDateFormat(strCurrentDBDate, DBDATE));
//							pst.setString(3, "IN");
//							rs = pst.executeQuery();
//							if (rs.next()) {
//								System.out
//										.println("User Already Attand the Office");
//							} else {
//
//								long in = uF.getTimeFormat(
//										strCurrentDBDate + " " + inTime,
//										DBTIMESTAMP).getTime();
//								long lStart = uF.getTimeFormat(
//										strCurrentDBDate
//												+ " "
//												+ getTimeFormat(
//														inTimeList.get(i),
//														"HH:mm"), DBTIMESTAMP)
//										.getTime();
//								pst = con
//										.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
//												+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
//								pst.setInt(1, uF.parseToInt(emp_per_id));
//								pst.setTimestamp(2, uF.getTimeStamp(
//										strCurrentDBDate
//												+ " "
//												+ getTimeFormat(
//														inTimeList.get(i),
//														"HH:mm"), DBTIMESTAMP));
//								pst.setString(3, " ");
//								pst.setString(4, "IN");
//								pst.setInt(5, 1);
//								pst.setString(6, " ");
//								pst.setNull(7, java.sql.Types.DOUBLE);
//								pst.setTimestamp(8, uF.getTimeStamp(
//										strCurrentDBDate
//												+ " "
//												+ getTimeFormat(
//														inTimeList.get(i),
//														"HH:mm"), DBTIMESTAMP));
//								pst.setInt(9, servic_id);
//
//								if (in > 0 && in > lStart) {
//									pst.setDouble(10,
//											-uF.parseToDouble(uF
//													.getTimeDiffInHoursMins(
//															lStart, in)));
//
//								} else if (lStart > 0 && lStart > in) {
//									pst.setDouble(10,
//											uF.parseToDouble(uF
//													.getTimeDiffInHoursMins(in,
//															lStart)));
//
//								} else {
//									pst.setDouble(10, 0);
//
//								}
//
//								pst.executeUpdate();
//								pst.close();
//
//							}
//
//							pst = con
//									.prepareStatement("Select atten_id from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=? ");
//							pst.setInt(1, uF.parseToInt(emp_per_id));
//							pst.setDate(2,
//									uF.getDateFormat(strCurrentDBDate, DBDATE));
//							pst.setString(3, "OUT");
//							rs = pst.executeQuery();
//							if (rs.next()) {
//								System.out
//										.println("User Already Attand the Office");
//							} else {
//
//								long in = uF.getTimeFormat(
//										strCurrentDBDate + " " + outTime,
//										DBTIMESTAMP).getTime();
//
//								long lEnd = 0;
//								try {
//									lEnd = uF.getTimeFormat(
//											strCurrentDBDate
//													+ " "
//													+ getTimeFormat(
//															inTimeList.get(i),
//															"HH:mm"),
//											DBTIMESTAMP).getTime();
//								} catch (Exception e) {
//								}
//								long lStart = uF.getTimeFormat(
//										strCurrentDBDate
//												+ " "
//												+ getTimeFormat(
//														outTimeList.get(i),
//														"HH:mm"), DBTIMESTAMP)
//										.getTime();
//								pst = con
//										.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
//												+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
//								pst.setInt(1, uF.parseToInt(emp_per_id));
//								pst.setTimestamp(2, uF.getTimeStamp(
//										strCurrentDBDate
//												+ " "
//												+ getTimeFormat(
//														outTimeList.get(i),
//														"HH:mm"), DBTIMESTAMP));
//								pst.setString(3, " ");
//								pst.setString(4, "OUT");
//								pst.setInt(5, 1);
//								pst.setString(6, " ");
//								pst.setDouble(7, uF.parseToDouble(uF
//										.getTimeDiffInHoursMins(lEnd, lStart)));
//								pst.setTimestamp(8, uF.getTimeStamp(
//										strCurrentDBDate
//												+ " "
//												+ getTimeFormat(
//														outTimeList.get(i),
//														"HH:mm"), DBTIMESTAMP));
//								pst.setInt(9, servic_id);
//
//								if (in > 0 && in > lStart) {
//									pst.setDouble(10,
//											-uF.parseToDouble(uF
//													.getTimeDiffInHoursMins(
//															lStart, in)));
//
//								} else if (lStart > 0 && lStart > in) {
//									pst.setDouble(10,
//											uF.parseToDouble(uF
//													.getTimeDiffInHoursMins(in,
//															lStart)));
//
//								} else {
//									pst.setDouble(10, 0);
//
//								}
//
//								pst.executeUpdate();
//
//							}
//
//							calendar.add(Calendar.DATE, 1);
//							alReport.add(empcode.toString());
//
//							request.setAttribute("alReport", alReport);
//						}
//
//					}
//
//					// }// end if-else
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			} // end main for loop
//			br.close();
//		}// try block end
//
//		catch (Exception e) {
//
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//
//		}
//
//	}
//	public void format4Attendance(File path,UtilityFunctions uF) {
//
//		if (!fileUpload1ContentType.contains("text/plain")) {
//			alReport.add("Please Check File format.This must be a .txt file");
//			return;
//		}
//		Connection con = null;
//		Database db = new Database();
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//
//		// System.out.println("fileUpload2ContentType==="+fileUpload2ContentType);
//		try {
//
//			con = db.makeConnection(con);
//			con.setAutoCommit(true);
//			String emp_per_id = null;
//			int servic_id = 0;
//			BufferedReader br = new BufferedReader(new FileReader(path));
//			String line = null;
//
//			while ((line = br.readLine()) != null) {
//				try {
//					List<String> cellList = Arrays.asList(line.split(" "));
//
//					if (cellList.size() < 6)
//						continue;
//					String empcode = cellList.get(1);
//
//					String in_out = cellList.get(2);
//					String strDate = cellList.get(4);
//					String strTime = cellList.get(5);
//					System.out.println("empcode==="+empcode);
//					System.out.println("in_out==="+in_out);
//					System.out.println("strDate==="+strDate);
//					System.out.println("strTime==="+strTime);
//
////					String cell5 = cellList.get(13);
//					String strWLocation = null;
//					if (empcode.contains(".")) {
//						empcode = empcode.substring(0, empcode.indexOf("."));
//
//					}
//
//					// Select Employ ID
//					pst = con
//							.prepareStatement("Select emp_id,service_id,wlocation_id from employee_official_details where biometrix_id=?");
//					pst.setInt(1, uF.parseToInt(empcode.trim()));
//					rs = pst.executeQuery();
//					while (rs.next()) {
//						emp_per_id = rs.getString("emp_id");
//						strWLocation = rs.getString("wlocation_id");
//
//						String temp = rs.getString("service_id");
//
//						if (temp.contains(",")) {
//							String str[] = temp.split(",");
//							servic_id = Integer.parseInt(str[0]);
//
//						} else {
//							servic_id = Integer.parseInt(rs.getString(1));
//						}
//					
//					}
//
//					// =================================================================================================================================
//					String inTime = null;
//					String outTime = null;
//					// System.out.println(uF.getDateFormat(cell2,
//					// "MM/dd/yyyy"));
//					pst = con
//							.prepareStatement("Select * from roster_details where emp_id=? and _date=?");
//					pst.setInt(1,uF.parseToInt( emp_per_id));
//					pst.setDate(2, uF.getDateFormat(strDate, "dd-MM-yyyy"));
//					rs = pst.executeQuery();
//					while (rs.next()) {
//						inTime = rs.getString("_from");
//						outTime = rs.getString("_to");
//					}
//					
//					if(inTime==null || outTime==null )
//						continue;
//
//
//					pst = con.prepareStatement("Select atten_id from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=? ");
//					pst.setInt(1,uF.parseToInt( emp_per_id));
//					pst.setDate(2, uF.getDateFormat(strDate, "dd-MM-yyyy"));
//					if(in_out.equalsIgnoreCase("I")){
//						pst.setString(3,"IN");
//						
//					}else if(in_out.equalsIgnoreCase("O")){
//						pst.setString(3,"OUT");
//						
//					}
//					rs = pst.executeQuery();
//					if (rs.next()) {
//						System.out.println("User Already Attand the Office");
//					} else {
//
////						String _fromTime = strTime.toString();
////						String _toTime = cell5.toString();
//
//						long lStart = uF.getTimeFormat(strDate +" "+strTime, "dd-MM-yyyy HH:mm").getTime();
////						long lEnd = uF.getTimeFormat(_toTime,
////								CF.getStrReportTimeFormat()).getTime();
//
//						long in = 0;
////						long out = 0;
//
//						try {
//							
////							out = uF.getTimeFormat(outTime,
////									CF.getStrReportTimeFormat()).getTime();
//						} catch (Exception e) {
//
//						}
//
//						if(in_out.equalsIgnoreCase("I")){
//							in = uF.getTimeFormat(strDate+" "+inTime,"dd-MM-yyyy HH:mm:ss").getTime();
//							long lIn = uF.getTimeFormat(strDate +" "+ strTime, "dd-MM-yyyy HH:mm").getTime();
//							long lIn1 = uF.getTimeFormat(strDate +" "+ inTime, "dd-MM-yyyy HH:mm").getTime();
//							System.out.println("lIn=="+lIn);
//							System.out.println("lIn1=="+lIn1);
//							if(lIn1<lIn){
//							updateBreakRegisters(emp_per_id,uF, con, "IN", strDate, inTime, lIn, strTime, strWLocation);
//							}
//						pst = con
//								.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
//										+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
//						pst.setInt(1,uF.parseToInt( emp_per_id));
//						pst.setTimestamp(2,uF.getTimeStamp(strDate+" "+getTimeFormat(strTime, "HH:mm"),"dd-MM-yyyy HH:mm:ss"));
//						pst.setString(3, " ");
//						pst.setString(4, "IN");
//						pst.setInt(5, 1);
//						pst.setString(6, " ");
//						pst.setNull(7, java.sql.Types.DOUBLE);
//						pst.setTimestamp(8,	uF.getTimeStamp(strDate+" "+getTimeFormat(strTime, "HH:mm"),"dd-MM-yyyy HH:mm:ss"));
//						pst.setInt(9, servic_id);
//						
//						if(in>0 && in>lStart){
//							pst.setDouble(10, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));
//							
//						}else if(lStart>0 && lStart>in){
//							pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));
//							
//						}else{
//							pst.setDouble(10, 0);
//							
//						}
////						pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));
//
//						pst.executeUpdate();
//						pst.close();
//						}else if(in_out.equalsIgnoreCase("O")){
//							
//							in = uF.getTimeFormat(strDate+" "+outTime,"dd-MM-yyyy HH:mm:ss").getTime();
//							
//							long lOut = uF.getTimeFormat(strDate +" "+ strTime, "dd-MM-yyyy HH:mm").getTime();
////							long lOut1 = uF.getTimeFormat(strDate +" "+ outTime, "dd-MM-yyyy HH:mm").getTime();
//							System.out.println("lOut=="+lOut);
//							System.out.println("lOut1=="+in);
//							if(lOut>in){
//							updateBreakRegisters(emp_per_id,uF, con, "OUT", strDate, outTime, lOut, strTime, strWLocation);
//							}
//							String _toTime = null;
//							pst = con.prepareStatement("Select * from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and in_out=? ");
//							pst.setInt(1,uF.parseToInt( emp_per_id));
//							pst.setDate(2, uF.getDateFormat(strDate, "dd-MM-yyyy"));
//							pst.setString(3,"IN");
//								
//							rs = pst.executeQuery();
//							while (rs.next()) {
//								System.out.println("sjkdsfkjdsf===="+rs.getString("in_out_timestamp"));
//								_toTime=rs.getString("in_out_timestamp");
//							} 
//							long lEnd =0;
//							try{
//							lEnd = uF.getTimeFormat(_toTime,DBTIMESTAMP).getTime();
//							}catch(Exception e){}
//							pst.close();
//						pst = con
//								.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
//										+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
//						pst.setInt(1,uF.parseToInt( emp_per_id));
//						pst.setTimestamp(2,uF.getTimeStamp(strDate+" "+getTimeFormat(strTime, "HH:mm"),"dd-MM-yyyy HH:mm:ss"));
//						pst.setString(3, " ");
//						pst.setString(4, "OUT");
//						pst.setInt(5, 1);
//						pst.setString(6, " ");
//						// pst.setDouble(7,
//						// minuteToTime(timediffrence(_fromTime, _toTime)));
//						pst.setDouble(7, uF.parseToDouble(uF.getTimeDiffInHoursMins(lEnd,lStart)));
//						pst.setTimestamp(8,uF.getTimeStamp(strDate+" "+getTimeFormat(strTime, "HH:mm"),"dd-MM-yyyy HH:mm"));
//						pst.setInt(9, servic_id);
//						
//						if(in>0 && in>lStart){
//							pst.setDouble(10, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));
//							
//						}else if(lStart>0 && lStart>in){
//							pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));
//							
//						}else{
//							pst.setDouble(10, 0);
//							
//						}
//						
////						pst.setDouble(10, uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart,in)));
//						pst.executeUpdate();
//						
//						System.out.println("lStart="+new Date(lStart));
//						System.out.println("in="+new Date(in));
//						
//						}
//						alReport.add(empcode.toString());
//
//						request.setAttribute("alReport", alReport);
//
//					}// end if-else
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			} // end main for loop
//			br.close();
//		}// try block end
//
//		catch (Exception e) {
//
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//
//		}
//
//	}

	public void laborAttendance(File path,UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
//		String _fromTime = null;
//		String _toTime = null;
		PreparedStatement pst = null;
		ResultSet rs=null;
		
		

		try {

			
			con = db.makeConnection(con);
			
			
			Map<String, String> hmEmpLevel = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpService =getEmpServiceMap(con);
			//Map<String, String> hmEmpPaymentMode = CF.getEmpPaymentMode(con, uF);
			//Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);

			FileInputStream fis = new FileInputStream(path);

			XSSFWorkbook workbook = new XSSFWorkbook(fis);

			System.out.println("Start Reading Excelsheet.... ");
			XSSFSheet attendanceSheet = workbook.getSheetAt(1);
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
			
			System.out.println("dataList=="+dataList);
			System.out.println("dataList=="+dataList.size());

			List<String> salaryHeadList=new ArrayList<String>();
//			double actual_hours = 0.0;
			for (int i = 4; dataList!=null && i < dataList.size(); i++) {
				List<String> cellList = dataList.get(i);
				for(int j=5;j<cellList.size();j++){
					if(!salaryHeadList.contains(cellList.get(j))){
						salaryHeadList.add(cellList.get(j));
					}
				}
				
//				salaryHeadList.add("");
			}
			
			System.out.println("salaryHeadList====>"+salaryHeadList);
			
			Map<String,Map<String,Map<String,String>>> levelSalaryDetails=new HashMap<String,Map<String,Map<String,String>>>();
			pst = con.prepareStatement(" SELECT * FROM salary_details");
			 rs = pst.executeQuery();
//			 Map<String,String> salarymp=new HashMap<String,String>();
				while (rs.next()) {
					Map<String,Map<String,String>> salarymp=levelSalaryDetails.get(rs.getString("level_id"));
					if(salarymp==null)salarymp=new HashMap<String,Map<String,String>>();
					Map<String,String> innerList=new HashMap<String,String>();
					innerList.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
					innerList.put("EARNING_DEDUCTION", rs.getString("earning_deduction"));
					innerList.put("SALARY_TYPE", rs.getString("salary_type"));
//					innerList.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
//					innerList.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
//					innerList.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));

					salarymp.put(rs.getString("salary_head_name"),innerList);
					levelSalaryDetails.put(rs.getString("level_id"), salarymp);
				}
				rs.close();
				pst.close();
			
			
			for (int i = 5;dataList!=null && i < dataList.size(); i++) {
				int emp_per_id = 0;
//				int servic_id = 0;
				List<String> cellList = dataList.get(i);
				
				String cell1 =  cellList.get(0);
				System.out.println("cellList===>"+cellList);
				System.out.println("cellList===>"+cellList.size());

//				String cell2 =  cellList.get(2);
//				String cell4 =  cellList.get(3);
//				String cell5 =  cellList.get(4);

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
				
				if(emp_per_id==0){
					alReport.add( "This '<strong>"+empcode+"</strong>' is not available.");
					continue;
				}
				System.out.println("emp_per_id=="+emp_per_id);
				String levelId = hmEmpLevel.get(emp_per_id+"");
				System.out.println("levelId=====>"+levelId);
				Map<String,Map<String,String>> salarymp=levelSalaryDetails.get(levelId);
				if(salarymp==null){
					alReport.add( "This <strong>"+empcode+"</strong> 's salary structure  is not set.");
					continue;
				}
				
//				pst = con.prepareStatement(selectUpdateEmpSalaryDetails);
//				pst.setInt(1,emp_per_id );
////				pst.setInt(2, uF.parseToInt(getCCID()) );
//				pst.setInt(2, 0);  // Default Service Id
//				pst.setInt(3, emp_per_id );
//				pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
//				pst.setInt(5, uF.parseToInt(levelId) );
//				
//				Map<String,Map<String,String>> EmpSalaryDetails=new HashMap<String,Map<String,String>>();
//
//				while(rs.next()){
//					Map<String,String> mp=new HashMap<String,String>();
//					mp.put("EARNING_DEDUCTION", rs.getString("earning_deduction"));
//					mp.put("EARNING_DEDUCTION", rs.getString("isdisplay"));
//					mp.put("EARNING_DEDUCTION", rs.getString("pay_type"));
//					mp.put("EARNING_DEDUCTION", rs.getString("earning_deduction"));
//					mp.put("EARNING_DEDUCTION", rs.getString("earning_deduction"));
//					EmpSalaryDetails.put(rs.getString("salary_head_id"), mp);
//				}
//				if(EmpSalaryDetails.size()==0){
//					continue;
//				}
				
				for(int j=0;j<salaryHeadList.size();j++){
					
					Map<String,String> mp=salarymp.get(salaryHeadList.get(j));
					System.out.println("mp===>"+mp);
//					Map<String,String> mp=EmpSalaryDetails.get(salaryHeadId);
					
					if(mp==null){
						continue;
					}
					
					double amt=uF.parseToDouble(cellList.get(j+5));
//					"INSERT INTO emp_salary_details (emp_id , salary_head_id,
//					amount, entry_date, user_id, pay_type, isdisplay, service_id, 
//					effective_date, earning_deduction, salary_type) VALUES (?,?,?,?,?,?,?,?,?,?,?)"
					pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id, salary_head_id, " +
							"amount, entry_date, user_id, pay_type, isdisplay, service_id, effective_date, " +
							"earning_deduction, salary_type,level_id) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?)");
					pst.setInt(1,emp_per_id);
					pst.setInt(2, uF.parseToInt(mp.get("SALARY_HEAD_ID")));
					pst.setDouble(3, amt);
					pst.setDate	(4, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
					pst.setInt(5, 1);
					pst.setString(6, "M");
//					pst.setBoolean(7, isDisplay[i]);
					pst.setBoolean(7, true);
//					pst.setBoolean(7, ArrayUtils.contains(isDisplay, emp_salary_id[i])>=0);
					pst.setInt(8, uF.parseToInt(hmEmpService.get(emp_per_id+"")));
					pst.setDate	(9, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
					pst.setString(10, mp.get("EARNING_DEDUCTION"));
					pst.setString(11, mp.get("SALARY_TYPE"));
					pst.setInt(12, uF.parseToInt(levelId));
					pst.execute();
					pst.close();
					
					CF.updateNextEmpSalaryEffectiveDate(con, uF, emp_per_id, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), DATE_FORMAT);
					
//					pst = con.prepareStatement("insert into payroll_generation (" +
//							"emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle, financial_year_from_date," +
//							" financial_year_to_date, currency_id, service_id, earning_deduction," +
//							" pay_mode, paid_from, paid_to, payment_mode, present_days, paid_days, paid_leaves, total_days) " +
//							"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
//					
//					pst = con.prepareStatement(insertPayrollGeneration);
//					pst.setInt(1, emp_per_id);
//					pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
//					pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
//					pst.setDate(4, uF.getCurrentDate(CF.getStrReportDateFormat()));
//					pst.setDate(5, uF.getCurrentDate(CF.getStrReportDateFormat()));
//					pst.setInt(6, uF.parseToInt(salaryHeadId));
//					pst.setDouble(7, amt);
//					pst.setInt(8, uF.parseToInt(strApprovePayCycle[2]));
//					pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//					pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//					pst.setInt(11, uF.parseToInt(hmEmpCurrency.get(emp_per_id+"")));
//					pst.setInt(12, uF.parseToInt(hmEmpService.get(emp_per_id+"")));
//					pst.setString(13,mp.get("EARNING_DEDUCTION"));
//					pst.setString(14,getStrPaycycleDuration()); 
//					pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
//					pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
//					pst.setInt(17, uF.parseToInt(hmEmpPaymentMode.get(emp_per_id+"")));
//					
//					
//					pst.setDouble(18, dblPresentDays);
//					pst.setDouble(19, dblPaidDays);
//					pst.setDouble(20, dblPaidLeaveDays);
//					pst.setDouble(21, dbTotalDays);
//					
//					pst.execute();
					
					
				}
				
				
				
				// =================================================================================================================================
//				pst = con.prepareStatement("Select service_id from employee_official_details where emp_id=?");
//				pst.setInt(1, emp_per_id);
//				 rs = pst.executeQuery();
//				if (rs.next()) {
//					String temp = rs.getString(1);
//
//					if (temp.contains(",")) {
//						String str[] = temp.split(",");
//						servic_id = Integer.parseInt(str[0]);
//
//					} else {
//						servic_id = Integer.parseInt(rs.getString(1));
//					}
//				}

//				pst = con.prepareStatement("Select atten_id from attendance_details where emp_id=? and in_out_timestamp=?");
//				pst.setInt(1, emp_per_id);
//				pst.setDate(2, getDate(cell2));
//				 rs= pst.executeQuery();
//				if (rs.next()) {
//					System.out.println("User Already Attand the Office");
//				} else {
//
//					_fromTime = cell4.toString();
//					_toTime = cell5.toString();
//
//					pst = con.prepareStatement("insert into attendance_details(emp_id," + "in_out_timestamp," + "reason," + "in_out," + "approved," + "comments," + "hours_worked,"
//							+ "in_out_timestamp_actual," + "service_id)values(?,?,?,?,?,?,?,?,?)");
//					pst.setInt(1, emp_per_id);
//					pst.setTimestamp(2, getTimeStamp(cell2, getTimeFormat(_fromTime, "HH:mm:ss")));
//					pst.setString(3, " ");
//					pst.setString(4, "IN");
//					pst.setInt(5, 1);
//					pst.setString(6, " ");
//					pst.setNull(7, java.sql.Types.DOUBLE);
//					pst.setTimestamp(8, getTimeStamp(cell2, getTimeFormat(_fromTime, "HH:mm:ss")));
//					pst.setInt(9, servic_id);
//					pst.executeUpdate();
//					
//					
//					long lStart = uF.getTimeFormat(_fromTime, CF.getStrReportTimeFormat()).getTime();
//					long lEnd = uF.getTimeFormat(_toTime, CF.getStrReportTimeFormat()).getTime();
//					
//					
//					System.out.println("pst====>"+pst);
//
//					pst = con.prepareStatement("insert into attendance_details(emp_id," + "in_out_timestamp," + "reason," + "in_out," + "approved," + "comments," + "hours_worked,"
//							+ "in_out_timestamp_actual," + "service_id)values(?,?,?,?,?,?,?,?,?)");
//					pst.setInt(1, emp_per_id);
//					pst.setTimestamp(2, getTimeStamp(cell2, getTimeFormat(_toTime, "HH:mm:ss")));
//					pst.setString(3, " ");
//					pst.setString(4, "OUT");
//					pst.setInt(5, 1);
//					pst.setString(6, " ");
////					pst.setDouble(7, minuteToTime(timediffrence(_fromTime, _toTime)));
//					pst.setDouble(7, uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, lEnd)));
//					pst.setTimestamp(8, getTimeStamp(cell2, getTimeFormat(_toTime, "HH:mm:ss")));
//					pst.setInt(9, servic_id);
//					pst.executeUpdate();
//					alReport.add(cell1.toString());
//
					request.setAttribute("alReport", alReport);
//
//				}// end if-else
			} // end main for loop

		}// try block end

		catch (Exception e) {

			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	
	public Map<String, String> getEmpServiceMap(Connection con){
		
		
		
		ResultSet rs=null;
		PreparedStatement pst=null;
		Map<String, String> hmEmpservice = new HashMap<String,String>();
		try {
			
			pst=con.prepareStatement("select * from employee_official_details");
			rs=pst.executeQuery();
			while(rs.next()){
				List<String> service=Arrays.asList((rs.getString("service_id")==null?"0":rs.getString("service_id")).split(","));
				hmEmpservice.put(rs.getString("emp_id"), service.get(0));
			}
			rs.close();
			pst.close();
			
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmEmpservice;
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

//	public String getEmp_name() {
//		return emp_name;
//	}
//
//	public void setEmp_name(String emp_name) {
//		this.emp_name = emp_name;
//	}

	public static java.sql.Date getDate(String s) throws Exception {
		java.sql.Date sqlToday = null;
//		String s = cell.toString();
		try {
			if (s.contains("/")) {
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				sqlToday = new java.sql.Date(df.parse(s).getTime());
			}
			if (s.contains("-")) {
				DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
				sqlToday = new java.sql.Date(df.parse(s).getTime());
			}
			if (s.contains(".")) {
				DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
				sqlToday = new java.sql.Date(df.parse(s).getTime());
			}
			return sqlToday;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sqlToday;
	}

	public static java.sql.Timestamp getTimeStamp(String cell, java.sql.Time time) {
		Timestamp timeStamp = null;
		try {

			String ts = cell + " " + time.toString();

			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			timeStamp = new java.sql.Timestamp(df.parse(ts).getTime());
			return timeStamp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeStamp;
	}

	public java.sql.Time getTimeFormat(String strDate, String strFormat) {
		java.util.Date utdt = null;

		try {
			if (strDate != null) {
				SimpleDateFormat smft = new SimpleDateFormat(strFormat);
				utdt = smft.parse(strDate);
			}
		} catch (Exception e) {
			LOG.error(e.getClass() + ": " + e.getMessage(), e);
		}
		if (utdt != null) {
			return new java.sql.Time((utdt.getTime()));
		} else {
			return null;
		}
	}

//	public static double timediffrence(String startTime, String endTime) {
//
//		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
//		java.util.Date time1;
//		java.util.Date time2;
//		double Hours = 0.0;
//		double minutes = 0.0;
//
//		try {
//
//			time1 = format.parse(endTime);
//			time2 = format.parse(startTime);
//
//			long elapsed = (time1.getTime() - time2.getTime()) / 1000;
//			minutes = elapsed / (double) 60;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return minutes;
//	}

//	public static double minuteToTime(double minute) {
//		double Hours = 0.0;
//		try {
//			Hours = (int) minute / 60;
//			double minutes1 = minute % 60;
//			Hours += minutes1 / 100;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return (Hours);
//
//	}
//	public String getTimeDiffInHoursMins(long IN, long OUT,UtilityFunctions uF ) {
//
//		long tDiff = (OUT - IN);
//		
////		if(tDiff < 0){
////			OUT += 24 * 3600 * 1000; 
////		}
//		tDiff = (OUT - IN);
//
//		long diffHours = tDiff / (1000 * 60 * 60);
//		long diffMinutes = (tDiff % (1000 * 60 * 60)) / (1000 * 60);
//
////		String minutesFormat = ((diffHours > 0) ? diffHours : "0") + ((diffMinutes > 0) ? ((diffMinutes < 10) ? ".0" + diffMinutes * (100d / 60d) : "." + diffMinutes * (100d / 60d)) : "");
////		minutesFormat = ((diffHours > 0) ? diffHours : "0") + ((diffMinutes > 0) ? ((diffMinutes < 10) ? ".0" + (int) (diffMinutes * (100d / 60)) : "." + (int) (diffMinutes * (100d / 60))) : "");
//
//		
//		String minutesFormat = ((diffHours > 0) ? diffHours : "0") + ((diffMinutes > 0) ? ((diffMinutes < 10) ? ".0" + diffMinutes : "." + diffMinutes) : "");
//		minutesFormat        = ((diffHours > 0) ? diffHours : "0") + ((diffMinutes > 0) ? ((diffMinutes < 10) ? ".0" + (int) (diffMinutes ) : "." + (int) (diffMinutes )) : "");
//		
////		log.debug("minutesFormat="+minutesFormat);
////		log.debug("formatIntoTwoDecimal="+formatIntoTwoDecimal(parseToDouble(minutesFormat)));
//		
//		
//		return formatIntoTwoDecimal(uF.parseToDouble(minutesFormat));
//
//	}
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	private void updateBreakRegisters(String strEmpId,UtilityFunctions uF, Connection con, String strMode, String strDate2, String strStart, long lIn, String strActualTime, String strWLocation){
		PreparedStatement pst = null;
		ResultSet rs =null;
		try {
			
			
			String []arr = CF.getCurrentPayCycle(con, CF.getStrTimeZone(), uF.getDateFormatUtil(strDate2, "dd-MM-yyyy"), CF);
			
			Map hmBreakBalance = new HashMap();
			Map hmBreakTaken = new HashMap();
			Map hmBreakUnPaid = new HashMap();
			
			Map<String, String> hmEmpLevelMap =CF.getEmpLevelMap(con);
			String levelid=hmEmpLevelMap.get(strEmpId);
			
//			PreparedStatement pst = con.prepareStatement("select a.emp_id, br.balance, a.break_type_id, br.taken_paid, br.taken_unpaid from break_register br, ( select max(_date) as _date,emp_id, break_type_id from break_register where _date <= ? group by emp_id,break_type_id ) a where br._date = a._date and br.emp_id = a.emp_id and br.break_type_id = a.break_type_id and a.emp_id = ?");
			pst = con.prepareStatement("select a.emp_id, br.balance, a.break_type_id, br.taken_paid, br.taken_unpaid from break_register br, ( select max(register_id) as register_id,emp_id, break_type_id from break_register where _date <= ? group by emp_id,break_type_id ) a where br.register_id = a.register_id and br.emp_id = a.emp_id and br.break_type_id = a.break_type_id and a.emp_id = ?");
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.setDate(1, uF.getCurrentDate("2013-12-31"));
			pst.setDate(1, uF.getDateFormat(arr[1], DATE_FORMAT));
			pst.setInt(2, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while(rs.next()){
				hmBreakBalance.put(rs.getString("break_type_id"), rs.getString("balance"));
				hmBreakTaken.put(rs.getString("break_type_id"), rs.getString("taken_paid"));
				hmBreakUnPaid.put(rs.getString("break_type_id"), rs.getString("taken_unpaid"));
			}
			rs.close();
			pst.close();
			System.out.println("=======>"+pst);
			
			Map<String,String> hmEmpBreakTaken=new HashMap<String, String>();
			pst = con.prepareStatement("select break_type_id,sum(leave_no)as no_of_leaves from break_application_register where emp_id=? and is_paid = true group by break_type_id");
			pst.setInt(1, uF.parseToInt(strEmpId)); 
			rs = pst.executeQuery();
			while(rs.next()){
				hmEmpBreakTaken.put(rs.getString("break_type_id"), rs.getString("no_of_leaves"));
				hmBreakTaken.put(rs.getString("break_type_id"), rs.getString("no_of_leaves"));
			}
			rs.close();
			pst.close();
			
			
			
			pst = con.prepareStatement("select a.break_type_id,days from break_policy a,emp_leave_break_type elt where a.break_type_id=elt.break_type_id and a.wlocation_id=elt.wlocation_id and a.wlocation_id=? and level_id=?");
			pst.setInt(1, uF.parseToInt(strWLocation));
			pst.setInt(2, uF.parseToInt(levelid));
			rs = pst.executeQuery();
			while(rs.next()){
				double totalBalance=uF.parseToDouble(rs.getString("days"))-uF.parseToDouble(hmEmpBreakTaken.get(rs.getString("break_type_id")));
				hmBreakBalance.put(rs.getString("break_type_id"), ""+totalBalance);
				/*hmBreakTaken.put(rs.getString("break_type_id"), rs.getString("taken_paid"));
				hmBreakUnPaid.put(rs.getString("break_type_id"), rs.getString("taken_unpaid"));*/
			}
			rs.close();
			pst.close();
			
			
			System.out.println("=======>"+pst);
			
			
			
			long lIn1 = uF.getTimeFormat(strDate2 +" "+ strStart, "dd-MM-yyyy HH:mm:ss").getTime();
			long tDiff = (lIn - lIn1);
			
			if(strMode!=null && strMode.equalsIgnoreCase("IN")){
				tDiff = (lIn - lIn1);
			}else{
//				tDiff = (lIn1 - lIn);
				tDiff = (lIn - lIn1);
			}
			
			long diffMinutes = 0;
			if(tDiff>0 || tDiff<0){
				long diffHours = tDiff / (1000 * 60 * 60);
//				diffMinutes = (tDiff % (1000 * 60 * 60)) / (1000 * 60);
				diffMinutes = Math.abs((tDiff)/60000);   
			}
			
			
			if( (strMode.equalsIgnoreCase("IN") && tDiff<0) || strMode.equalsIgnoreCase("OUT") && tDiff>0){
				return;
			}

			
			pst = con.prepareStatement("select * from  break_policy where wlocation_id = ? and time_value>=? and _mode like ?  order by time_value limit 1");
			pst.setInt(1, uF.parseToInt(strWLocation));
			pst.setDouble(2, diffMinutes);
			pst.setString(3, "%"+strMode+"%");
			rs = pst.executeQuery();
			String strBreakPolicyId = null;
			String strTimeValue = null;
			boolean isAvailable = false;
			
			System.out.println("strBreakPolicyId=1==>"+strBreakPolicyId);
			
			while(rs.next()){
				strBreakPolicyId = rs.getString("break_type_id");
				strTimeValue = rs.getString("time_value");
				
				isAvailable = true;
				System.out.println("strBreakPolicyId=2==>"+strBreakPolicyId);
			}
			rs.close();
			pst.close();
			
			
			
			double dblBalance = uF.parseToDouble((String)hmBreakBalance.get(strBreakPolicyId));
			double dblTakenPaid = uF.parseToDouble((String)hmBreakTaken.get(strBreakPolicyId));
			double dblTakenUnPaid = uF.parseToDouble((String)hmBreakUnPaid.get(strBreakPolicyId));
			
			
			
			
			System.out.println("dblBalance==>"+dblBalance);
			System.out.println(strBreakPolicyId+" hmBreakBalance==>"+hmBreakBalance);
			
			int k=0;
			for(k=0; k<5 && dblBalance==0 && isAvailable; k++){
				
				if(dblBalance==0){
					pst = con.prepareStatement("select * from  break_policy where wlocation_id = ? and time_value > ? and _mode like ? order by time_value limit 1");
					pst.setInt(1, uF.parseToInt(strWLocation));
					pst.setDouble(2, uF.parseToDouble(strTimeValue));
					pst.setString(3, "%"+strMode+"%");
					rs = pst.executeQuery();
					while(rs.next()){
						strBreakPolicyId = rs.getString("break_type_id");
						strTimeValue = rs.getString("time_value");
					}
					rs.close();
					pst.close();
					
					dblBalance = uF.parseToDouble((String)hmBreakBalance.get(strBreakPolicyId));
					dblTakenPaid = uF.parseToDouble((String)hmBreakTaken.get(strBreakPolicyId));
					dblTakenUnPaid = uF.parseToDouble((String)hmBreakUnPaid.get(strBreakPolicyId));
			
					
					System.out.println("dblBalance= k="+k+" =>"+dblBalance+" pst="+pst);
				}
					
			}
			
			
			if(diffMinutes<120 && dblBalance==0){
				
				strBreakPolicyId = "-2";
				dblBalance = uF.parseToDouble((String)hmBreakBalance.get(strBreakPolicyId));
				dblTakenPaid = uF.parseToDouble((String)hmBreakTaken.get(strBreakPolicyId));
				dblTakenUnPaid = uF.parseToDouble((String)hmBreakUnPaid.get(strBreakPolicyId));
				
				dblTakenUnPaid += 1;
				dblTakenPaid = 0;
				
			} else if(dblBalance==0){							
				strBreakPolicyId = "-1";
				dblBalance = uF.parseToDouble((String)hmBreakBalance.get(strBreakPolicyId));
				dblTakenPaid = uF.parseToDouble((String)hmBreakTaken.get(strBreakPolicyId));
				dblTakenUnPaid = uF.parseToDouble((String)hmBreakUnPaid.get(strBreakPolicyId));
				
				dblTakenUnPaid += 1;
				dblTakenPaid = 0;
			}else{
				dblTakenPaid += 1;
				dblTakenUnPaid = 0;
			}
			
			
				
				pst = con.prepareStatement("insert into break_application_register (_date, emp_id, break_type_id, leave_no, is_paid, balance, _type) values (?,?,?,?,?,?,?)");
				pst.setDate(1, uF.getDateFormat(strDate2, "dd-MM-yyyy"));
				pst.setInt(2, uF.parseToInt(strEmpId));
				pst.setInt(3, uF.parseToInt(strBreakPolicyId));
				pst.setInt(4, 1);
				if(dblBalance==0){
					pst.setBoolean(5, false);
				}else{
					pst.setBoolean(5, true);
				}
				
				
				if(dblBalance>0){
					pst.setDouble(6, (dblBalance - 1));
				}else{
					pst.setDouble(6, dblBalance);
				}
				pst.setString(7, strMode);
				pst.execute();
				pst.close();
				
				
				
				pst = con.prepareStatement("update break_register set taken_paid =?,taken_unpaid =?, balance=? where break_type_id =? and _date=? and emp_id =?");
				pst.setDouble(1, (dblTakenPaid));
				pst.setDouble(2, (dblTakenUnPaid));
				
				if(dblBalance>0){
					pst.setDouble(3, (dblBalance - 1));
				}else{
					pst.setDouble(3, dblBalance);
				}
				
				pst.setInt(4, uF.parseToInt(strBreakPolicyId));
				pst.setDate(5, uF.getDateFormat(strDate2, "dd-MM-yyyy"));
				pst.setInt(6, uF.parseToInt(strEmpId));
				int x = pst.executeUpdate();
				pst.close();
				
				System.out.println("== update =>"+pst);
				
				if(x==0){
					pst = con.prepareStatement("insert into break_register (_date, emp_id, taken_paid, balance, taken_unpaid, break_type_id) values (?,?,?,?,?,?)") ;
					
					pst.setDate(1, uF.getDateFormat(strDate2, "dd-MM-yyyy"));
					pst.setInt(2, uF.parseToInt(strEmpId));
					pst.setDouble(3, dblTakenPaid);
					if(dblBalance>0){
						pst.setDouble(4, (dblBalance - 1));
					}else{
						pst.setDouble(4, dblBalance);
					}
					
					
					pst.setDouble(5, dblTakenUnPaid);
					pst.setInt(6, uF.parseToInt(strBreakPolicyId));
					pst.execute();
					pst.close();
					
					
					System.out.println("== insert =>"+pst);
				}
				
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
}