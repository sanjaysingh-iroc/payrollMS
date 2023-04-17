package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.common.DBBackup;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class FillPayCycles implements IStatements{

	String paycycleId;
	String paycycleName;
	String paycycleDurationId;
	
	 
	
	
	
	public FillPayCycles(String paycycleId, String paycycleName) {
		this.paycycleId = paycycleId;
		this.paycycleName = paycycleName;
	
	}
	
	public FillPayCycles(String paycycleDurationId) {
		this.paycycleDurationId = paycycleDurationId;
	
	}
	public FillPayCycles(String paycycleDurationId, HttpServletRequest request) {
		this.paycycleDurationId = paycycleDurationId;
		this.request = request;
	}
	
	HttpServletRequest request;
	public FillPayCycles(HttpServletRequest request) {
		this.request = request;
	}
		
	public FillPayCycles() { }
	 
	
	public List<FillPayCycles> fillPayCycles(CommonFunctions CF, String orgId) {
		
		List<FillPayCycles> al = new ArrayList<FillPayCycles>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		List alDesc = new ArrayList();

		try {
			String startDate = null;
			String strDisplayPaycycle = null;
			String strPaycycleDuration = null;
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from org_details where org_id=?");
			pst.setInt(1, uF.parseToInt(orgId));
			rs = pst.executeQuery();
			while (rs.next()) {
				startDate = rs.getString("start_paycycle");
				strDisplayPaycycle = rs.getString("display_paycycle");
				strPaycycleDuration = rs.getString("duration_paycycle");
			} 	
			rs.close();
			pst.close();
					
			if(paycycleDurationId!=null) {
				strPaycycleDuration = paycycleDurationId;
			}

			String []arrDisplayPAycycle = null;
			int minCycle = 0;
			int maxCycle = 0;
			if(strDisplayPaycycle!=null) {
				arrDisplayPAycycle = strDisplayPaycycle.split("-");
				minCycle = uF.parseToInt(arrDisplayPAycycle[0]);
				maxCycle = uF.parseToInt(arrDisplayPAycycle[1]);
			}
			
			
			Calendar calCurrent = GregorianCalendar.getInstance(TimeZone.getTimeZone((CF.getStrTimeZone())));
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "yyyy")));

			
			StringBuilder sb = new StringBuilder();
			int nPayCycle = 0;

			String dt1 = null;
			String dt2 = null;

			List<String> alInner = new ArrayList<String>();
			// java.util.Date strCurrentDate = calCurrent.getTime();
			java.util.Date strCurrentDate = uF.getDateFormatUtil(((calCurrent.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + calCurrent.get(Calendar.DAY_OF_MONTH) : calCurrent.get(Calendar.DAY_OF_MONTH)) + "/"
					+ (((calCurrent.get(Calendar.MONTH) + 1) < 10) ? "0" + (calCurrent.get(Calendar.MONTH) + 1) : (calCurrent.get(Calendar.MONTH) + 1)) + "/" + calCurrent.get(Calendar.YEAR), DATE_FORMAT);

			int nDurationCount = 0;
			java.util.Date strCurrentPayCycleD1 = null;
			java.util.Date strCurrentPayCycleD2 = null;

			while (true) {
				sb = new StringBuilder();
				nPayCycle++;

				if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("M")) {
					nDurationCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH) -1 ;
				} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("F")) {
					nDurationCount = 15 - 1 ;
					if(cal.get(Calendar.DAY_OF_MONTH)==16) {
						int nActual = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
						nDurationCount = nActual - 15 - 1; 
					}
				} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("BW")) {
					nDurationCount = 14 - 1 ;
				} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("W")) {
					nDurationCount = 7 - 1 ;
				} else {
					nDurationCount = cal.getMaximum(Calendar.DAY_OF_MONTH) -1 ;
				}
				
				dt1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);
				cal.add(Calendar.DAY_OF_MONTH, nDurationCount);
				dt2 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);

				if(nPayCycle<minCycle) {
					cal.add(Calendar.DAY_OF_MONTH, 1);
					continue;
				}
				
				sb.append("Pay Cycle " + nPayCycle + ", " + dt1 + " - " + dt2);

				strCurrentPayCycleD1 = uF.getDateFormatUtil(dt1, DATE_FORMAT);
				strCurrentPayCycleD2 = uF.getDateFormatUtil(dt2, DATE_FORMAT);
				
				if (strCurrentDate.equals(strCurrentPayCycleD1) || strCurrentDate.equals(strCurrentPayCycleD2) || (strCurrentDate.after(strCurrentPayCycleD1) && strCurrentDate.before(strCurrentPayCycleD2))) {
					al.add(new FillPayCycles(dt1+"-"+dt2+"-"+nPayCycle, "Pay Cycle " + nPayCycle + ", " + uF.getDateFormat(dt1, DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(dt2, DATE_FORMAT, CF.getStrReportDateFormat())));
				} else if (strCurrentDate.before(strCurrentPayCycleD2)) {
					
				} else {
					al.add(new FillPayCycles(dt1+"-"+dt2+"-"+nPayCycle, "Pay Cycle " + nPayCycle + ", " + uF.getDateFormat(dt1, DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(dt2, DATE_FORMAT, CF.getStrReportDateFormat())));
				}

				cal.add(Calendar.DAY_OF_MONTH, 1);

				if(nPayCycle>=maxCycle) {
					break;
				}
			}
			
			for(int i=al.size()-1; i>=0; i--) {
				alDesc.add((FillPayCycles)al.get(i));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return alDesc;
	}

	
	
public List<FillPayCycles> fillFuturePayCycles(CommonFunctions CF, String orgId, String showDate) {
		
		List<FillPayCycles> al = new ArrayList<FillPayCycles>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		List alDesc = new ArrayList();

		try {
			String startDate = null;
			String strDisplayPaycycle = null;
			String strPaycycleDuration = null;
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from org_details where org_id=?");
			pst.setInt(1, uF.parseToInt(orgId));
			rs = pst.executeQuery();
			while (rs.next()) {
//				if(startDate == null || startDate.equals("")) {
				startDate = rs.getString("start_paycycle");
				strDisplayPaycycle = rs.getString("display_paycycle");
				strPaycycleDuration = rs.getString("duration_paycycle");
			} 	
			rs.close();
			pst.close();
					
			if(paycycleDurationId!=null) {
				strPaycycleDuration = paycycleDurationId;
			}

			String []arrDisplayPAycycle = null;
			int minCycle = 0;
			int maxCycle = 0;
			if(strDisplayPaycycle!=null) {
				arrDisplayPAycycle = strDisplayPaycycle.split("-");
				minCycle = uF.parseToInt(arrDisplayPAycycle[0]);
				maxCycle = uF.parseToInt(arrDisplayPAycycle[1]);
			}
			
			
			Calendar calCurrent = GregorianCalendar.getInstance(TimeZone.getTimeZone((CF.getStrTimeZone())));
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "yyyy")));

			if(showDate == null) {
				showDate = startDate;
			}
			StringBuilder sb = new StringBuilder();
			int nPayCycle = 0;

			String dt1 = null;
			String dt2 = null;

			List<String> alInner = new ArrayList<String>();
			// java.util.Date strCurrentDate = calCurrent.getTime();
			java.util.Date strCurrentDate = uF.getDateFormatUtil(((calCurrent.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + calCurrent.get(Calendar.DAY_OF_MONTH) : calCurrent.get(Calendar.DAY_OF_MONTH)) + "/"
					+ (((calCurrent.get(Calendar.MONTH) + 1) < 10) ? "0" + (calCurrent.get(Calendar.MONTH) + 1) : (calCurrent.get(Calendar.MONTH) + 1)) + "/" + calCurrent.get(Calendar.YEAR), DATE_FORMAT);
			strCurrentDate = uF.getFutureDate(strCurrentDate, 365);
			
			int nDurationCount = 0;
			java.util.Date strCurrentPayCycleD1 = null;
			java.util.Date strCurrentPayCycleD2 = null;

//			System.out.println("showDate ===>> " + uF.getDateFormat(showDate, DBDATE));
//			System.out.println("startDate ===>> " + uF.getDateFormat(startDate, DBDATE));
//			System.out.println("showDate flag ===>> " + uF.getDateFormat(showDate, DBDATE).after(uF.getDateFormat(startDate, DBDATE)));
			while (true) {	
				sb = new StringBuilder();
				nPayCycle++;

				if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("M")) {
					nDurationCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH) -1 ;
				} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("F")) {
					nDurationCount = 15 - 1 ;
					if(cal.get(Calendar.DAY_OF_MONTH)==16) {
						int nActual = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
						nDurationCount = nActual - 15 - 1; 
					}
				} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("BW")) {
					nDurationCount = 14 - 1 ;
				} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("W")) {
					nDurationCount = 7 - 1 ;
				} else {
					nDurationCount = cal.getMaximum(Calendar.DAY_OF_MONTH) -1 ;
				}
				
				dt1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);
				cal.add(Calendar.DAY_OF_MONTH, nDurationCount);
				dt2 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);
				
//				System.out.println("status ===>> " + uF.getDateFormat(showDate, DBDATE).after(uF.getDateFormat(dt1, DATE_FORMAT)));
				
				if(nPayCycle<minCycle || (showDate != null && uF.getDateFormat(showDate, DBDATE).after(uF.getDateFormat(dt2, DATE_FORMAT)))) {
					cal.add(Calendar.DAY_OF_MONTH, 1);
					continue;
				}
				
				sb.append("Pay Cycle " + nPayCycle + ", " + dt1 + " - " + dt2);

				strCurrentPayCycleD1 = uF.getDateFormatUtil(dt1, DATE_FORMAT);
				strCurrentPayCycleD2 = uF.getDateFormatUtil(dt2, DATE_FORMAT);

				if (strCurrentDate.equals(strCurrentPayCycleD1) || strCurrentDate.equals(strCurrentPayCycleD2) || (strCurrentDate.after(strCurrentPayCycleD1) && strCurrentDate.before(strCurrentPayCycleD2))) {
					al.add(new FillPayCycles(dt1+"-"+dt2+"-"+nPayCycle, "Pay Cycle " + nPayCycle + ", " + uF.getDateFormat(dt1, DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(dt2, DATE_FORMAT, CF.getStrReportDateFormat())));
				} else if (strCurrentDate.before(strCurrentPayCycleD2)) {
					
				} else {
					al.add(new FillPayCycles(dt1+"-"+dt2+"-"+nPayCycle, "Pay Cycle " + nPayCycle + ", " + uF.getDateFormat(dt1, DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(dt2, DATE_FORMAT, CF.getStrReportDateFormat())));
				}

				cal.add(Calendar.DAY_OF_MONTH, 1);
				if(nPayCycle>=maxCycle) {
					break;
				}
			}
			
			for(int i=al.size()-1; i>=0; i--) {
				alDesc.add((FillPayCycles)al.get(i));
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return alDesc;
	}



	public List<FillPayCycles> fillPayCycles(CommonFunctions CF) {
		
		List<FillPayCycles> al = new ArrayList<FillPayCycles>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		List alDesc = new ArrayList();

		try {
 

			String startDate = null;
			String strDisplayPaycycle = null;
			String strPaycycleDuration = null;
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectSettings);
			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getString("options").equalsIgnoreCase(O_START_PAY_CLYCLE)) {
					startDate = rs.getString("value");
				}
				if (rs.getString("options").equalsIgnoreCase(O_DISPLAY_PAY_CLYCLE)) {
					strDisplayPaycycle = rs.getString("value");
				}
				if (rs.getString("options").equalsIgnoreCase(O_PAYCYCLE_DURATION)) {
					strPaycycleDuration = rs.getString("value");
				}
			}	
			rs.close();
			pst.close(); 
		
			
			if(paycycleDurationId!=null) {
				strPaycycleDuration = paycycleDurationId;
			}
			
			String []arrDisplayPAycycle = null;
			int minCycle = 0;
			int maxCycle = 0;
			if(strDisplayPaycycle!=null) {
				arrDisplayPAycycle = strDisplayPaycycle.split("-");
				minCycle = uF.parseToInt(arrDisplayPAycycle[0]);
				maxCycle = uF.parseToInt(arrDisplayPAycycle[1]);
			}
			
			Calendar calCurrent = GregorianCalendar.getInstance(TimeZone.getTimeZone((CF.getStrTimeZone())));
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(startDate, DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(startDate, DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(startDate, DATE_FORMAT, "yyyy")));

			
			StringBuilder sb = new StringBuilder();
			int nPayCycle = 0;

			String dt1 = null;
			String dt2 = null;

			List<String> alInner = new ArrayList<String>();
			// java.util.Date strCurrentDate = calCurrent.getTime();
			java.util.Date strCurrentDate = uF.getDateFormatUtil(((calCurrent.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + calCurrent.get(Calendar.DAY_OF_MONTH) : calCurrent.get(Calendar.DAY_OF_MONTH)) + "/"
					+ (((calCurrent.get(Calendar.MONTH) + 1) < 10) ? "0" + (calCurrent.get(Calendar.MONTH) + 1) : (calCurrent.get(Calendar.MONTH) + 1)) + "/" + calCurrent.get(Calendar.YEAR), DATE_FORMAT);

			int nDurationCount = 0;
			java.util.Date strCurrentPayCycleD1 = null;
			java.util.Date strCurrentPayCycleD2 = null;

			while (true) {
				sb = new StringBuilder();
				nPayCycle++;

				if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("M")) {
					nDurationCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH) -1 ;
				} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("F")) {
					nDurationCount = 15 - 1 ;
					if(cal.get(Calendar.DAY_OF_MONTH)==16) {
						int nActual = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
						nDurationCount = nActual - 15 - 1; 
					}
				} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("BW")) {
					nDurationCount = 14 - 1 ;
				} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("W")) {
					nDurationCount = 7 - 1 ;
				} else {
					nDurationCount = cal.getMaximum(Calendar.DAY_OF_MONTH) -1 ;
				}
				
				dt1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);
				cal.add(Calendar.DAY_OF_MONTH, nDurationCount);
				dt2 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);

				if(nPayCycle<minCycle) {
					cal.add(Calendar.DAY_OF_MONTH, 1);
					continue;
				}
				
				sb.append("Pay Cycle " + nPayCycle + ", " + dt1 + " - " + dt2);

				strCurrentPayCycleD1 = uF.getDateFormatUtil(dt1, DATE_FORMAT);
				strCurrentPayCycleD2 = uF.getDateFormatUtil(dt2, DATE_FORMAT);
				
				if (strCurrentDate.equals(strCurrentPayCycleD1) || strCurrentDate.equals(strCurrentPayCycleD2) || (strCurrentDate.after(strCurrentPayCycleD1) && strCurrentDate.before(strCurrentPayCycleD2))) {
					al.add(new FillPayCycles(dt1+"-"+dt2+"-"+nPayCycle, "Pay Cycle " + nPayCycle + ", " + uF.getDateFormat(dt1, DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(dt2, DATE_FORMAT, CF.getStrReportDateFormat())));
				} else if (strCurrentDate.before(strCurrentPayCycleD2)) {
					
				} else {
					al.add(new FillPayCycles(dt1+"-"+dt2+"-"+nPayCycle, "Pay Cycle " + nPayCycle + ", " + uF.getDateFormat(dt1, DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(dt2, DATE_FORMAT, CF.getStrReportDateFormat())));
				}

				cal.add(Calendar.DAY_OF_MONTH, 1);

				if(nPayCycle>=maxCycle) {
					break;
				}
			}
			
			for(int i=al.size()-1; i>=0; i--) {
				alDesc.add((FillPayCycles)al.get(i));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return alDesc;
	}
	
	
	public List<FillPayCycles> fillPayCycles(CommonFunctions CF, int noOfPaycycles) {
		
		List<FillPayCycles> al = new ArrayList<FillPayCycles>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		List alDesc = new ArrayList();

		try {
 

			String startDate = null;
			String strDisplayPaycycle = null;
			String strPaycycleDuration = null;
			
			con = db.makeConnection(con);
			
			
			
			pst = con.prepareStatement(selectSettings);
			rs = pst.executeQuery();

			while (rs.next()) {
				if (rs.getString("options").equalsIgnoreCase(O_START_PAY_CLYCLE)) {
					startDate = rs.getString("value");
				}
				if (rs.getString("options").equalsIgnoreCase(O_DISPLAY_PAY_CLYCLE)) {
					strDisplayPaycycle = rs.getString("value");
				}
				if (rs.getString("options").equalsIgnoreCase(O_PAYCYCLE_DURATION)) {
					strPaycycleDuration = rs.getString("value");
				}
			} 	
			rs.close();
			pst.close();
		
			
			if(paycycleDurationId!=null) {
				strPaycycleDuration = paycycleDurationId;
			}

			String []arrDisplayPAycycle = null;
			int minCycle = 0;
			int maxCycle = 0;
			if(strDisplayPaycycle!=null) {
				arrDisplayPAycycle = strDisplayPaycycle.split("-");
				minCycle = uF.parseToInt(arrDisplayPAycycle[0]);
				maxCycle = uF.parseToInt(arrDisplayPAycycle[1]);
			}
			
			
			Calendar calCurrent = GregorianCalendar.getInstance(TimeZone.getTimeZone((CF.getStrTimeZone())));
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(startDate, DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(startDate, DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(startDate, DATE_FORMAT, "yyyy")));

			
			StringBuilder sb = new StringBuilder();
			int nPayCycle = 0;

			String dt1 = null;
			String dt2 = null;

			List<String> alInner = new ArrayList<String>();
			// java.util.Date strCurrentDate = calCurrent.getTime();
			java.util.Date strCurrentDate = uF.getDateFormatUtil(((calCurrent.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + calCurrent.get(Calendar.DAY_OF_MONTH) : calCurrent.get(Calendar.DAY_OF_MONTH)) + "/"
					+ (((calCurrent.get(Calendar.MONTH) + 1) < 10) ? "0" + (calCurrent.get(Calendar.MONTH) + 1) : (calCurrent.get(Calendar.MONTH) + 1)) + "/" + calCurrent.get(Calendar.YEAR), DATE_FORMAT);

			int nDurationCount = 0;
			int nCount = 0;
			java.util.Date strCurrentPayCycleD1 = null;
			java.util.Date strCurrentPayCycleD2 = null;

			while (true) {
				sb = new StringBuilder();
				nPayCycle++;

				if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("M")) {
					nDurationCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH) -1 ;
				} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("F")) {
					nDurationCount = 15 - 1 ;
					if(cal.get(Calendar.DAY_OF_MONTH)==16) {
						int nActual = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
						nDurationCount = nActual - 15 - 1; 
					}
				} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("BW")) {
					nDurationCount = 14 - 1 ;
				} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("W")) {
					nDurationCount = 7 - 1 ;
				} else {
					nDurationCount = cal.getMaximum(Calendar.DAY_OF_MONTH) -1 ;
				}
				
				
				
				
				
				dt1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);
				cal.add(Calendar.DAY_OF_MONTH, nDurationCount);
				dt2 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);

				
				
				if(nPayCycle<minCycle) {
					cal.add(Calendar.DAY_OF_MONTH, 1);
					continue;
				}
				
				
				sb.append("Pay Cycle " + nPayCycle + ", " + dt1 + " - " + dt2);

				strCurrentPayCycleD1 = uF.getDateFormatUtil(dt1, DATE_FORMAT);
				strCurrentPayCycleD2 = uF.getDateFormatUtil(dt2, DATE_FORMAT);

				
				
				if (strCurrentDate.equals(strCurrentPayCycleD1) || strCurrentDate.equals(strCurrentPayCycleD2) || (strCurrentDate.after(strCurrentPayCycleD1) && strCurrentDate.before(strCurrentPayCycleD2))) {
					al.add(new FillPayCycles(dt1+"-"+dt2+"-"+nPayCycle, "Pay Cycle " + nPayCycle + ", " + uF.getDateFormat(dt1, DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(dt2, DATE_FORMAT, CF.getStrReportDateFormat())));
				} else if (strCurrentDate.before(strCurrentPayCycleD2)) {
					if(nCount <= noOfPaycycles) {
						al.add(new FillPayCycles(dt1+"-"+dt2+"-"+nPayCycle, "Pay Cycle " + nPayCycle + ", " + uF.getDateFormat(dt1, DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(dt2, DATE_FORMAT, CF.getStrReportDateFormat())));
					}
					nCount++;
				} else {
					al.add(new FillPayCycles(dt1+"-"+dt2+"-"+nPayCycle, "Pay Cycle " + nPayCycle + ", " + uF.getDateFormat(dt1, DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(dt2, DATE_FORMAT, CF.getStrReportDateFormat())));
				}

				cal.add(Calendar.DAY_OF_MONTH, 1);

				if(nPayCycle>=maxCycle) {
					break;
				}

			}
			
			
			for(int i=al.size()-1; i>=0; i--) {
				alDesc.add((FillPayCycles)al.get(i));
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return alDesc;
	}
	
	
public List<FillPayCycles> fillNoofPayCycles(CommonFunctions CF,String strOrgId, int noOfPaycycles) {
		
		List<FillPayCycles> al = new ArrayList<FillPayCycles>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		List alDesc = new ArrayList();

		try {
 

			String startDate = null;
			String strDisplayPaycycle = null;
			String strPaycycleDuration = null;
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from org_details where org_id=?");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();

			while (rs.next()) {
				startDate = rs.getString("start_paycycle");
				strDisplayPaycycle = rs.getString("display_paycycle");
				strPaycycleDuration = rs.getString("duration_paycycle");
			} 	
			rs.close();
			pst.close();
		
			
			if(paycycleDurationId!=null) {
				strPaycycleDuration = paycycleDurationId;
			}
			
			String []arrDisplayPAycycle = null;
			int minCycle = 0;
			int maxCycle = 0;
			if(strDisplayPaycycle!=null) {
				arrDisplayPAycycle = strDisplayPaycycle.split("-");
				minCycle = uF.parseToInt(arrDisplayPAycycle[0]);
				maxCycle = uF.parseToInt(arrDisplayPAycycle[1]);
			}
			
			
			Calendar calCurrent = GregorianCalendar.getInstance(TimeZone.getTimeZone((CF.getStrTimeZone())));
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "yyyy")));

			
			StringBuilder sb = new StringBuilder();
			int nPayCycle = 0;
			int nPayCycleTemp = 0;
			String dt1 = null;
			String dt2 = null;

			List<String> alInner = new ArrayList<String>();
			// java.util.Date strCurrentDate = calCurrent.getTime();
			java.util.Date strCurrentDate = uF.getDateFormatUtil(((calCurrent.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + calCurrent.get(Calendar.DAY_OF_MONTH) : calCurrent.get(Calendar.DAY_OF_MONTH)) + "/"
					+ (((calCurrent.get(Calendar.MONTH) + 1) < 10) ? "0" + (calCurrent.get(Calendar.MONTH) + 1) : (calCurrent.get(Calendar.MONTH) + 1)) + "/" + calCurrent.get(Calendar.YEAR), DATE_FORMAT);

			int nDurationCount = 0;
			int nCount = 0;
			java.util.Date strCurrentPayCycleD1 = null;
			java.util.Date strCurrentPayCycleD2 = null;

			while (true) {
				sb = new StringBuilder();
				nPayCycle++;

				if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("M")) {
					nDurationCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH) -1 ;
				} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("F")) {
					nDurationCount = 15 - 1 ;
					if(cal.get(Calendar.DAY_OF_MONTH)==16) {
						int nActual = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
						nDurationCount = nActual - 15 - 1; 
					}
				} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("BW")) {
					nDurationCount = 14 - 1 ;
				} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("W")) {
					nDurationCount = 7 - 1 ;
				} else {
					nDurationCount = cal.getMaximum(Calendar.DAY_OF_MONTH) -1 ;
				}
				
				dt1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);
				cal.add(Calendar.DAY_OF_MONTH, nDurationCount);
				dt2 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);

				
				
				if(nPayCycle<minCycle) {
					cal.add(Calendar.DAY_OF_MONTH, 1);
					continue;
				}
				
				
				sb.append("Pay Cycle " + nPayCycle + ", " + dt1 + " - " + dt2);

				strCurrentPayCycleD1 = uF.getDateFormatUtil(dt1, DATE_FORMAT);
				strCurrentPayCycleD2 = uF.getDateFormatUtil(dt2, DATE_FORMAT);

				
				
				if (strCurrentDate.equals(strCurrentPayCycleD1) || strCurrentDate.equals(strCurrentPayCycleD2) || (strCurrentDate.after(strCurrentPayCycleD1) && strCurrentDate.before(strCurrentPayCycleD2))) {
					al.add(new FillPayCycles(dt1+"-"+dt2+"-"+nPayCycle, "Pay Cycle " + nPayCycle + ", " + uF.getDateFormat(dt1, DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(dt2, DATE_FORMAT, CF.getStrReportDateFormat())));
					
				} else if (strCurrentDate.after(strCurrentPayCycleD1)) {
//					if(nPayCycle ==noOfPaycycles) { 
					if(nPayCycle >=noOfPaycycles) {
//						System.out.println("nPayCycle==>"+nPayCycle+"--noOfPaycycles==>"+noOfPaycycles);
						al.add(new FillPayCycles(dt1+"-"+dt2+"-"+nPayCycle, "Pay Cycle " + nPayCycle + ", " + uF.getDateFormat(dt1, DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(dt2, DATE_FORMAT, CF.getStrReportDateFormat())));
					}
					nCount++; 
				}

				cal.add(Calendar.DAY_OF_MONTH, 1);

				if(nPayCycle>=maxCycle) {
					break; 
				}

			}
			
			
			for(int i=al.size()-1; i>=0; i--) {
				alDesc.add((FillPayCycles)al.get(i));
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return alDesc;
	}

	public List<FillPayCycles> fillCurrentPayCycles(CommonFunctions CF, String orgId,String strTimeZone) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String[] strPayCycleDate = new String[3]; 
		List alDesc = new ArrayList();
		try {

			String startDate = null;
			String strDisplayPaycycle = null;
			String strPaycycleDuration = null;

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from org_details where org_id=?");
			pst.setInt(1, uF.parseToInt(orgId));
			rs = pst.executeQuery();

			while (rs.next()) {
				startDate = rs.getString("start_paycycle");
				strDisplayPaycycle = rs.getString("display_paycycle");
				strPaycycleDuration = rs.getString("duration_paycycle");
			} 	
			rs.close();
			pst.close();
					

			String[] arrDisplayPAycycle = null;
			int minCycle = 0;
			int maxCycle = 0;
			if (strDisplayPaycycle != null) {
				arrDisplayPAycycle = strDisplayPaycycle.split("-");
				minCycle = uF.parseToInt(arrDisplayPAycycle[0]);
				maxCycle = uF.parseToInt(arrDisplayPAycycle[1]);
			}

			Calendar calCurrent = GregorianCalendar.getInstance(TimeZone.getTimeZone((strTimeZone)));
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(strTimeZone));
			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "yyyy")));

			StringBuilder sb = new StringBuilder();
			int nPayCycle = 0;

			int nDurationCount = 0;
			String dt1 = null;
			String dt2 = null;

			// java.util.Date strCurrentDate = calCurrent.getTime();
			java.util.Date strCurrentDate = uF.getDateFormatUtil(
					((calCurrent.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + calCurrent.get(Calendar.DAY_OF_MONTH) : calCurrent.get(Calendar.DAY_OF_MONTH)) + "/"
							+ (((calCurrent.get(Calendar.MONTH) + 1) < 10) ? "0" + (calCurrent.get(Calendar.MONTH) + 1) : (calCurrent.get(Calendar.MONTH) + 1)) + "/"
							+ calCurrent.get(Calendar.YEAR), DATE_FORMAT);

			java.util.Date strCurrentPayCycleD1 = null;
			java.util.Date strCurrentPayCycleD2 = null;

			while (true) {
				sb = new StringBuilder();
				nPayCycle++;

				if (strPaycycleDuration != null && strPaycycleDuration.equalsIgnoreCase("M")) {
					nDurationCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH) - 1;
				} else if (strPaycycleDuration != null && strPaycycleDuration.equalsIgnoreCase("F")) {
					nDurationCount = 15 - 1;
					if (cal.get(Calendar.DAY_OF_MONTH) == 16) {
						int nActual = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
						nDurationCount = nActual - 15 - 1;
					}
				} else if (strPaycycleDuration != null && strPaycycleDuration.equalsIgnoreCase("BW")) {
					nDurationCount = 14 - 1;
				} else if (strPaycycleDuration != null && strPaycycleDuration.equalsIgnoreCase("W")) {
					nDurationCount = 7 - 1;
				} else {
					nDurationCount = cal.getMaximum(Calendar.DAY_OF_MONTH) - 1;
				}

				dt1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/"
						+ (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
				cal.add(Calendar.DAY_OF_MONTH, nDurationCount);
				dt2 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/"
						+ (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);

				if (nPayCycle < minCycle) {
					cal.add(Calendar.DAY_OF_MONTH, 1);
					continue;
				}

				sb.append("Pay Cycle " + nPayCycle + ", " + dt1 + " - " + dt2);

				strCurrentPayCycleD1 = uF.getDateFormatUtil(dt1, DATE_FORMAT);
				strCurrentPayCycleD2 = uF.getDateFormatUtil(dt2, DATE_FORMAT);

				if (strCurrentDate.equals(strCurrentPayCycleD1) || strCurrentDate.equals(strCurrentPayCycleD2)
						|| (strCurrentDate.after(strCurrentPayCycleD1) && strCurrentDate.before(strCurrentPayCycleD2))) {
					strPayCycleDate[0] = dt1;
					strPayCycleDate[1] = dt2;
					strPayCycleDate[2] = nPayCycle + "";
					alDesc.add(new FillPayCycles(dt1+"-"+dt2+"-"+nPayCycle, "Pay Cycle " + nPayCycle + ", " + uF.getDateFormat(dt1, DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(dt2, DATE_FORMAT, CF.getStrReportDateFormat())));
				}

				cal.add(Calendar.DAY_OF_MONTH, 1);

				if (nPayCycle >= maxCycle) {
					break;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return alDesc;
	}
	
	public List<FillPayCycles> fillCurrentNextPayCycleByOrg(String strTimeZone,com.konnect.jpms.util.CommonFunctions CF, String strOrg) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		String[] strPayCycleDate = new String[3];
		
		List<FillPayCycles> al = new ArrayList<FillPayCycles>();
		List alDesc = new ArrayList();
		try {
			con = db.makeConnection(con);
			
			String []strDate = CF.getCurrentPayCycleByOrg(con,CF.getStrTimeZone(), CF,strOrg);
			al.add(new FillPayCycles(strDate[0]+"-"+strDate[1]+"-"+strDate[2], "Pay Cycle " + strDate[2] + ", " + uF.getDateFormat(strDate[0], DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(strDate[1], DATE_FORMAT, CF.getStrReportDateFormat())));
			
			String startDate = null;
			String strDisplayPaycycle = null;
			String strPaycycleDuration = null;
			
			pst = con.prepareStatement("select * from org_details where org_id=?");
			pst.setInt(1, uF.parseToInt(strOrg));
			rs = pst.executeQuery();

			while (rs.next()) {
				startDate = rs.getString("start_paycycle");
				strDisplayPaycycle = rs.getString("display_paycycle");
				strPaycycleDuration = rs.getString("duration_paycycle");
			} 	
			rs.close();
			pst.close();

			String[] arrDisplayPAycycle = null;
			int minCycle = 0;
			int maxCycle = 0;
			if (strDisplayPaycycle != null) {
				arrDisplayPAycycle = strDisplayPaycycle.split("-");
				minCycle = uF.parseToInt(arrDisplayPAycycle[0]);
				maxCycle = uF.parseToInt(arrDisplayPAycycle[1]);
			}

			Calendar calCurrent = GregorianCalendar.getInstance(TimeZone.getTimeZone((strTimeZone)));
			calCurrent.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(strDate[0], DATE_FORMAT, "dd")));
			calCurrent.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strDate[0], DATE_FORMAT, "MM")) + 1);
			calCurrent.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strDate[0], DATE_FORMAT, "yyyy")));

			calCurrent.add(Calendar.DAY_OF_MONTH, -1);

			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(strTimeZone));
			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "yyyy")));

			StringBuilder sb = new StringBuilder();
			int nPayCycle = 0;

			int nDurationCount = 0;
			String dt1 = null;
			String dt2 = null;

//			List<String> alInner = new ArrayList<String>();
			// java.util.Date strCurrentDate = calCurrent.getTime();
			java.util.Date strCurrentDate = uF.getDateFormatUtil(
					((calCurrent.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + calCurrent.get(Calendar.DAY_OF_MONTH) : calCurrent.get(Calendar.DAY_OF_MONTH)) + "/"
							+ (((calCurrent.get(Calendar.MONTH) + 1) < 10) ? "0" + (calCurrent.get(Calendar.MONTH) + 1) : (calCurrent.get(Calendar.MONTH) + 1)) + "/"
							+ calCurrent.get(Calendar.YEAR), DATE_FORMAT);

			java.util.Date strCurrentPayCycleD1 = null;
			java.util.Date strCurrentPayCycleD2 = null;

			while (true) {
				sb = new StringBuilder();
				nPayCycle++;

				if (strPaycycleDuration != null && strPaycycleDuration.equalsIgnoreCase("M")) {
					nDurationCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH) - 1;
				} else if (strPaycycleDuration != null && strPaycycleDuration.equalsIgnoreCase("F")) {
					nDurationCount = 15 - 1;
					if (cal.get(Calendar.DAY_OF_MONTH) == 16) {
						int nActual = cal.getActualMaximum(Calendar.DAY_OF_MONTH);    
						nDurationCount = nActual - 15 - 1;
					}
				} else if (strPaycycleDuration != null && strPaycycleDuration.equalsIgnoreCase("BW")) {
					nDurationCount = 14 - 1;
				} else if (strPaycycleDuration != null && strPaycycleDuration.equalsIgnoreCase("W")) {
					nDurationCount = 7 - 1;
				} else {
					nDurationCount = cal.getMaximum(Calendar.DAY_OF_MONTH) - 1;
				}

				dt1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/"
						+ (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
				cal.add(Calendar.DAY_OF_MONTH, nDurationCount);
				dt2 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/"
						+ (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);

				if (nPayCycle < minCycle) {
					cal.add(Calendar.DAY_OF_MONTH, 1);
					continue;
				}

				sb.append("Pay Cycle " + nPayCycle + ", " + dt1 + " - " + dt2);

				strCurrentPayCycleD1 = uF.getDateFormatUtil(dt1, DATE_FORMAT);
				strCurrentPayCycleD2 = uF.getDateFormatUtil(dt2, DATE_FORMAT);


				if (strCurrentDate.equals(strCurrentPayCycleD1) || strCurrentDate.equals(strCurrentPayCycleD2)
						|| (strCurrentDate.after(strCurrentPayCycleD1) && strCurrentDate.before(strCurrentPayCycleD2))) {
					strPayCycleDate[0] = dt1;
					strPayCycleDate[1] = dt2;
					strPayCycleDate[2] = nPayCycle + "";

				}

				cal.add(Calendar.DAY_OF_MONTH, 1);

				if (nPayCycle >= maxCycle) {
					break;
				}

			}
			if(strPayCycleDate!=null && strPayCycleDate.length>0) {
				al.add(new FillPayCycles(strPayCycleDate[0]+"-"+strPayCycleDate[1]+"-"+strPayCycleDate[2], "Pay Cycle " + strPayCycleDate[2] + ", " + uF.getDateFormat(strPayCycleDate[0], DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(strPayCycleDate[1], DATE_FORMAT, CF.getStrReportDateFormat())));
			}
			
			for(int i=al.size()-1; i>=0; i--) {
				alDesc.add((FillPayCycles)al.get(i));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return alDesc;
	}

	public String getPaycycleId() {
		return paycycleId;
	}

	public void setPaycycleId(String paycycleId) {
		this.paycycleId = paycycleId;
	}

	public String getPaycycleName() {
		return paycycleName;
	}

	public void setPaycycleName(String paycycleName) {
		this.paycycleName = paycycleName;
	}

	public List<FillPayCycles> fillBetweenNoofPayCycles(CommonFunctions CF, String strOrgId, int nFromPaycycle, int nToPaycycle) {
		
		List<FillPayCycles> al = new ArrayList<FillPayCycles>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		List alDesc = new ArrayList();

		try {
			String startDate = null;
			String strDisplayPaycycle = null;
			String strPaycycleDuration = null;
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from org_details where org_id=?");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			while (rs.next()) {
				startDate = rs.getString("start_paycycle");
				strDisplayPaycycle = rs.getString("display_paycycle");
				strPaycycleDuration = rs.getString("duration_paycycle");
			} 	
			rs.close();
			pst.close();
			
			if(paycycleDurationId!=null) {
				strPaycycleDuration = paycycleDurationId;
			}
			
			String []arrDisplayPAycycle = null;
			int minCycle = 0;
			int maxCycle = 0;
			if(strDisplayPaycycle!=null) {
				arrDisplayPAycycle = strDisplayPaycycle.split("-");
				minCycle = uF.parseToInt(arrDisplayPAycycle[0]);
				maxCycle = uF.parseToInt(arrDisplayPAycycle[1]);
			}
			
			Calendar calCurrent = GregorianCalendar.getInstance(TimeZone.getTimeZone((CF.getStrTimeZone())));
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "yyyy")));
			
			StringBuilder sb = new StringBuilder();
			int nPayCycle = 0;
			int nPayCycleTemp = 0;
			String dt1 = null;
			String dt2 = null;

			List<String> alInner = new ArrayList<String>();
			// java.util.Date strCurrentDate = calCurrent.getTime();
			java.util.Date strCurrentDate = uF.getDateFormatUtil(((calCurrent.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + calCurrent.get(Calendar.DAY_OF_MONTH) : calCurrent.get(Calendar.DAY_OF_MONTH)) + "/"
					+ (((calCurrent.get(Calendar.MONTH) + 1) < 10) ? "0" + (calCurrent.get(Calendar.MONTH) + 1) : (calCurrent.get(Calendar.MONTH) + 1)) + "/" + calCurrent.get(Calendar.YEAR), DATE_FORMAT);

			int nDurationCount = 0;
			int nCount = 0;
			java.util.Date strCurrentPayCycleD1 = null;
			java.util.Date strCurrentPayCycleD2 = null;

			while (true) {
				sb = new StringBuilder();
				nPayCycle++;

				if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("M")) {
					nDurationCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH) -1 ;
				} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("F")) {
					nDurationCount = 15 - 1 ;
					if(cal.get(Calendar.DAY_OF_MONTH)==16) {
						int nActual = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
						nDurationCount = nActual - 15 - 1; 
					}
				} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("BW")) {
					nDurationCount = 14 - 1 ;
				} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("W")) {
					nDurationCount = 7 - 1 ;
				} else {
					nDurationCount = cal.getMaximum(Calendar.DAY_OF_MONTH) -1 ;
				}
				
				dt1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);
				cal.add(Calendar.DAY_OF_MONTH, nDurationCount);
				dt2 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);
				
				if(nPayCycle<minCycle) {
					cal.add(Calendar.DAY_OF_MONTH, 1);
					continue;
				}
				
				sb.append("Pay Cycle " + nPayCycle + ", " + dt1 + " - " + dt2);

				strCurrentPayCycleD1 = uF.getDateFormatUtil(dt1, DATE_FORMAT);
				strCurrentPayCycleD2 = uF.getDateFormatUtil(dt2, DATE_FORMAT);
				
				if(nPayCycle <= nFromPaycycle && nPayCycle >= nToPaycycle) {
					al.add(new FillPayCycles(dt1+"-"+dt2+"-"+nPayCycle, "Pay Cycle " + nPayCycle + ", " + uF.getDateFormat(dt1, DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(dt2, DATE_FORMAT, CF.getStrReportDateFormat())));
				}

				cal.add(Calendar.DAY_OF_MONTH, 1);

				if(nPayCycle>=maxCycle) {
					break; 
				}

			}
			
			for(int i=al.size()-1; i>=0; i--) {
				alDesc.add((FillPayCycles)al.get(i));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return alDesc;
	}	
	
	
	
	public List<FillPayCycles> fillPayCyclesWithEffectiveDate(CommonFunctions CF, String orgId, String effectiveDate) {
		
		List<FillPayCycles> al = new ArrayList<FillPayCycles>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		List alDesc = new ArrayList();

		try {
			if(effectiveDate != null && !effectiveDate.equals("")) {
				String startDate = null;
				String strDisplayPaycycle = null;
				String strPaycycleDuration = null;
				
				con = db.makeConnection(con);
				
				pst = con.prepareStatement("select * from org_details where org_id=?");
				pst.setInt(1, uF.parseToInt(orgId));
				rs = pst.executeQuery();
				while (rs.next()) {
					startDate = rs.getString("start_paycycle");
					strDisplayPaycycle = rs.getString("display_paycycle");
					strPaycycleDuration = rs.getString("duration_paycycle");
				} 	
				rs.close();
				pst.close();
						
				if(paycycleDurationId!=null) {
					strPaycycleDuration = paycycleDurationId;
				}
	
				String []arrDisplayPAycycle = null;
				int minCycle = 0;
				int maxCycle = 0;
				if(strDisplayPaycycle!=null) {
					arrDisplayPAycycle = strDisplayPaycycle.split("-");
					minCycle = uF.parseToInt(arrDisplayPAycycle[0]);
					maxCycle = uF.parseToInt(arrDisplayPAycycle[1]);
				}
				
				Calendar calCurrent = GregorianCalendar.getInstance(TimeZone.getTimeZone((CF.getStrTimeZone())));
				Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
				cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "dd")));
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "MM")) - 1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "yyyy")));
	
				StringBuilder sb = new StringBuilder();
				StringBuilder sbPCCnt = new StringBuilder();
				int nPayCycle = 0;
	
				String dt1 = null;
				String dt2 = null;
	
				List<String> alInner = new ArrayList<String>();
				// java.util.Date strCurrentDate = calCurrent.getTime();
				java.util.Date strCurrentDate = uF.getDateFormatUtil(((calCurrent.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + calCurrent.get(Calendar.DAY_OF_MONTH) : calCurrent.get(Calendar.DAY_OF_MONTH)) + "/"
						+ (((calCurrent.get(Calendar.MONTH) + 1) < 10) ? "0" + (calCurrent.get(Calendar.MONTH) + 1) : (calCurrent.get(Calendar.MONTH) + 1)) + "/" + calCurrent.get(Calendar.YEAR), DATE_FORMAT);
				java.util.Date strEffectiveDate = uF.getDateFormatUtil(effectiveDate, DATE_FORMAT);
				
				int nDurationCount = 0;
				java.util.Date strCurrentPayCycleD1 = null;
				java.util.Date strCurrentPayCycleD2 = null;
	
				while (true) {
//					sb = new StringBuilder();
					nPayCycle++;
	
					if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("M")) {
						nDurationCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH) -1 ;
					} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("F")) {
						nDurationCount = 15 - 1 ;
						if(cal.get(Calendar.DAY_OF_MONTH)==16) {
							int nActual = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
							nDurationCount = nActual - 15 - 1; 
						}
					} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("BW")) {
						nDurationCount = 14 - 1 ;
					} else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("W")) {
						nDurationCount = 7 - 1 ;
					} else {
						nDurationCount = cal.getMaximum(Calendar.DAY_OF_MONTH) -1 ;
					}
					
					dt1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
							+ cal.get(Calendar.YEAR);
					cal.add(Calendar.DAY_OF_MONTH, nDurationCount);
					dt2 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
							+ cal.get(Calendar.YEAR);
	
					if(nPayCycle<minCycle) {
						cal.add(Calendar.DAY_OF_MONTH, 1);
						continue;
					}
					
					strCurrentPayCycleD1 = uF.getDateFormatUtil(dt1, DATE_FORMAT);
					strCurrentPayCycleD2 = uF.getDateFormatUtil(dt2, DATE_FORMAT);
					
					if(strEffectiveDate.before(strCurrentPayCycleD2)) {
						sb.append("Pay Cycle " + nPayCycle + ", " + dt1 + " - " + dt2);
						if (strCurrentDate.equals(strCurrentPayCycleD1) || strCurrentDate.equals(strCurrentPayCycleD2) || (strCurrentDate.after(strCurrentPayCycleD1) && strCurrentDate.before(strCurrentPayCycleD2))) {
							al.add(new FillPayCycles(dt1+"-"+dt2+"-"+nPayCycle, "Pay Cycle " + nPayCycle + ", " + uF.getDateFormat(dt1, DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(dt2, DATE_FORMAT, CF.getStrReportDateFormat())));
							sbPCCnt.append(nPayCycle+",");
						} else if (strCurrentDate.before(strCurrentPayCycleD2)) {
							
						} else {
							al.add(new FillPayCycles(dt1+"-"+dt2+"-"+nPayCycle, "Pay Cycle " + nPayCycle + ", " + uF.getDateFormat(dt1, DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(dt2, DATE_FORMAT, CF.getStrReportDateFormat())));
							sbPCCnt.append(nPayCycle+",");
						}
					}
					
					cal.add(Calendar.DAY_OF_MONTH, 1);
					if(nPayCycle>=maxCycle) {
						break;
					}
				}
//				System.out.println("sb ===>> " + sb.toString());
				
				for(int i=al.size()-1; i>=0; i--) {
					alDesc.add((FillPayCycles)al.get(i));
				}
				request.setAttribute("sbPCCnt", sbPCCnt.toString());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return alDesc;
	}


}
