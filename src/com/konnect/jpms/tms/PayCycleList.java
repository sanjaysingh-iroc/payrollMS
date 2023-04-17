package com.konnect.jpms.tms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PayCycleList extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpID;
	String strSessionEmpID;
	String strPayCycleType;
	String strTimeZone;
	String strUserType;
	CommonFunctions CF = null;
	
	public String execute() throws Exception {

		session = request.getSession();if(session==null)return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpID = (String)session.getAttribute("EMPID");

		request.setAttribute(TITLE, TPayCycles);
		request.setAttribute(PAGE, PPayCycleList);

		strEmpID = (String) request.getParameter("EMPID");
		strPayCycleType = (String) request.getParameter("T");
		strTimeZone = (String) session.getAttribute(O_TIME_ZONE);
		
		viewPayCycle();

		return loadPayCycle();

	}

	public String loadPayCycle() {
		return LOAD;
	}

	public String viewPayCycle() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

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

			String []arrDisplayPAycycle = null;
			int minCycle = 0;
			int maxCycle = 0;
			if(strDisplayPaycycle!=null){
				arrDisplayPAycycle = strDisplayPaycycle.split("-");
				minCycle = uF.parseToInt(arrDisplayPAycycle[0]);
				maxCycle = uF.parseToInt(arrDisplayPAycycle[1]);
			}
			
			
			Calendar calCurrent = GregorianCalendar.getInstance(TimeZone.getTimeZone((strTimeZone)));
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(strTimeZone));
			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(startDate, CF.getStrReportDateFormat(), "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(startDate, CF.getStrReportDateFormat(), "MM")) -1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(startDate, CF.getStrReportDateFormat(), "yyyy")));

			StringBuilder sb = new StringBuilder();
			int nPayCycle = 0;

			String dt1 = null;
			String dt2 = null;

			List<String> alInner = new ArrayList<String>(); 
//			java.util.Date strCurrentDate = calCurrent.getTime();
			java.util.Date strCurrentDate = uF.getDateFormatUtil(((calCurrent.get(Calendar.DAY_OF_MONTH)<10)?"0"+calCurrent.get(Calendar.DAY_OF_MONTH):calCurrent.get(Calendar.DAY_OF_MONTH)) + "/" + (((calCurrent.get(Calendar.MONTH) + 1)<10)?"0"+(calCurrent.get(Calendar.MONTH) + 1):(calCurrent.get(Calendar.MONTH) + 1)) + "/" + calCurrent.get(Calendar.YEAR), CF.getStrReportDateFormat());
			

			
			int nDurationCount = 0;
			
			
			java.util.Date strCurrentPayCycleD1 = null;
			java.util.Date strCurrentPayCycleD2 = null;

			while (true) {
				sb = new StringBuilder();
				nPayCycle++;

				if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("M")){
					nDurationCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH) -1 ;
				}else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("F")){
					nDurationCount = 15 - 1 ;
				}else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("BW")){
					nDurationCount = 14 - 1 ;
				}else if(strPaycycleDuration!=null && strPaycycleDuration.equalsIgnoreCase("W")){
					nDurationCount = 7 - 1 ;
				}else{
					nDurationCount = cal.getMaximum(Calendar.DAY_OF_MONTH) -1 ;
				}
				
				
				dt1 = ((cal.get(Calendar.DAY_OF_MONTH)<10)?"0"+cal.get(Calendar.DAY_OF_MONTH):cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1)<10)?"0"+(cal.get(Calendar.MONTH) + 1):(cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
				cal.add(Calendar.DAY_OF_MONTH, nDurationCount);
				dt2 = ((cal.get(Calendar.DAY_OF_MONTH)<10)?"0"+cal.get(Calendar.DAY_OF_MONTH):cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1)<10)?"0"+(cal.get(Calendar.MONTH) + 1):(cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);

				
				if(nPayCycle<minCycle){
					cal.add(Calendar.DAY_OF_MONTH, 1);
					continue;
				}
				
				
				sb.append("Pay Cycle " + nPayCycle + ", " + dt1 + " - " + dt2);

				
				strCurrentPayCycleD1 = uF.getDateFormatUtil(dt1, CF.getStrReportDateFormat());
				strCurrentPayCycleD2 = uF.getDateFormatUtil(dt2, CF.getStrReportDateFormat());

				
				if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(MANAGER))){
					
					if(strPayCycleType!=null && strPayCycleType.equalsIgnoreCase("A")){
						if (strCurrentDate.equals(strCurrentPayCycleD1) || strCurrentDate.equals(strCurrentPayCycleD2) || (strCurrentDate.after(strCurrentPayCycleD1) && strCurrentDate.before(strCurrentPayCycleD2))) {
							alInner.add("<a style=\"font-weight:bold;color:blue;\" href=" + request.getContextPath() + "/ApprovePayroll.action?T="+strPayCycleType+"&PC="+nPayCycle+"&D1="+dt1+"&D2="+dt2+" >" + sb.toString() + "</a>");
						} else if (strCurrentDate.before(strCurrentPayCycleD2)) {
//							alInner.add( sb.toString());
						}else{
							alInner.add("<a href=" + request.getContextPath() + "/ApprovePayroll.action?T="+strPayCycleType+"&PC="+nPayCycle+"&D1="+dt1+"&D2="+dt2+" >" + sb.toString() + "</a>");
						}
					}else{
						
						if (strCurrentDate.equals(strCurrentPayCycleD1) || strCurrentDate.equals(strCurrentPayCycleD2) || (strCurrentDate.after(strCurrentPayCycleD1) && strCurrentDate.before(strCurrentPayCycleD2))) {
							alInner.add("<a style=\"font-weight:bold;color:blue;\" href=" + request.getContextPath() + "/EmployeeReportPayCycle.action?T="+strPayCycleType+"&PC="+nPayCycle+"&D1="+dt1+"&D2="+dt2+" >" + sb.toString() + "</a>");
						} else if (strCurrentDate.before(strCurrentPayCycleD2)) {
//							alInner.add( sb.toString());
						}else{
							alInner.add("<a href=" + request.getContextPath() + "/EmployeeReportPayCycle.action?T="+strPayCycleType+"&PC="+nPayCycle+"&D1="+dt1+"&D2="+dt2+" >" + sb.toString() + "</a>");
						}
					} 

					
				}else if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)){
					
					if(strPayCycleType!=null && strPayCycleType.equalsIgnoreCase("T")){
						if (strCurrentDate.equals(strCurrentPayCycleD1) || strCurrentDate.equals(strCurrentPayCycleD2) || (strCurrentDate.after(strCurrentPayCycleD1) && strCurrentDate.before(strCurrentPayCycleD2))) {						
							alInner.add("<a style=\"font-weight:bold;color:blue;\" href=" + request.getContextPath() + "/ClockEntries.action?T=T&PAY=Y&EMPID="+strSessionEmpID+"&PC="+nPayCycle+"&D1="+dt1+"&D2="+dt2+">" + sb.toString() + "</a>");
						} else if (strCurrentDate.before(strCurrentPayCycleD2)) {
//							alInner.add( sb.toString());
						}else{
							alInner.add("<a href=" + request.getContextPath() + "/ClockEntries.action?T=T&PAY=Y&EMPID="+strSessionEmpID+"&PC="+nPayCycle+"&D1="+dt1+"&D2="+dt2+">" + sb.toString() + "</a>");
						}
					}else if(strPayCycleType!=null && strPayCycleType.equalsIgnoreCase("C")){
						if (strCurrentDate.equals(strCurrentPayCycleD1) || strCurrentDate.equals(strCurrentPayCycleD2) || (strCurrentDate.after(strCurrentPayCycleD1) && strCurrentDate.before(strCurrentPayCycleD2))) {						
							alInner.add("<a style=\"font-weight:bold;color:blue;\" href=" + request.getContextPath() + "/ClockEntries.action?T=C&PAY=Y&EMPID="+strSessionEmpID+"&PC="+nPayCycle+"&D1="+dt1+"&D2="+dt2+">" + sb.toString() + "</a>");
						} else if (strCurrentDate.before(strCurrentPayCycleD2)) {
//							alInner.add( sb.toString());
						}else{
							alInner.add("<a href=" + request.getContextPath() + "/ClockEntries.action?T=C&PAY=Y&EMPID="+strSessionEmpID+"&PC="+nPayCycle+"&D1="+dt1+"&D2="+dt2+">" + sb.toString() + "</a>");
						}
					}
					
				}
				
				
				
				
				
				cal.add(Calendar.DAY_OF_MONTH, 1);


				if(nPayCycle>=maxCycle){
					break;
				}

			}

			request.setAttribute("reportList", alInner);

		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
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

	public static void main(String args[]) {

		try {
			PayCycleList pcl = new PayCycleList();
			pcl.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
