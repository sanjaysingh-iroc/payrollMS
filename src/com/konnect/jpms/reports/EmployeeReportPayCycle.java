package com.konnect.jpms.reports;

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

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeReportPayCycle extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	HttpSession session;
	String strEmpID;
	String strType = null; 
	String strPC = null;
	String strD1 = null;
	String strD2 = null;
	String strAlphaValue = null; 
	String strUserType;
	CommonFunctions CF;
	private static Logger log = Logger.getLogger(EmployeeReportPayCycle.class);
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		request.setAttribute(PAGE, PReportEmployeePayCycle);
		request.setAttribute(TITLE, TViewEmployee);
		
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strEmpID = (String) session.getAttribute("EMPID");
		
		strType = (String)request.getParameter("T");
		strPC = (String)request.getParameter("PC");
		strD1 = (String)request.getParameter("D1");
		strD2 = (String)request.getParameter("D2");
		strAlphaValue = (String)request.getParameter("alphaValue");

		String str = viewEmployee();
		
		if(str.equalsIgnoreCase(ACCESS_DENIED)){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}else{
			return loadEmployee();
		}

	}

	public String loadEmployee() {
		return LOAD;
	}

	public String viewEmployee() {

		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			
			List<String> alExceptionEmployees = new ArrayList<String>();
			pst = con.prepareStatement(selectEarlyLateEntries);
			pst.setDate(1, uF.getDateFormat(strD1, CF.getStrReportDateFormat()));
			pst.setDate(2, uF.getDateFormat(strD2, CF.getStrReportDateFormat()));
			rs = pst.executeQuery();
			while(rs.next()){
				if(rs.getDouble("early_late")!=0){
					alExceptionEmployees.add(rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
			
			
			
			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();

			
			if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || 
					strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO) ||
					strUserType.equalsIgnoreCase(HRMANAGER) )) {
				
				if(strAlphaValue!=null && strAlphaValue.length()>0){
					pst = con.prepareStatement(selectEmployeeR7alpha);
					pst.setDate(1, uF.getDateFormat(strD2, CF.getStrReportDateFormat()));
					pst.setString(2, strAlphaValue+ "%");
				}else{
					pst = con.prepareStatement(selectEmployeeR7);
					pst.setDate(1, uF.getDateFormat(strD2, CF.getStrReportDateFormat()));
				}
			}else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
				if(strAlphaValue!=null && strAlphaValue.length()>0){
					pst = con.prepareStatement(selectEmployeeManagerR7alpha);
					pst.setDate(1, uF.getDateFormat(strD2, CF.getStrReportDateFormat()));
					pst.setInt(2, uF.parseToInt(strEmpID));
					pst.setString(3, strAlphaValue+ "%");
				}else{
					pst = con.prepareStatement(selectEmployeeManagerR7);
					pst.setDate(1, uF.getDateFormat(strD2, CF.getStrReportDateFormat()));
					pst.setInt(2, uF.parseToInt(strEmpID));					
				}
			}else{
				return ACCESS_DENIED;
			}
			
			rs = pst.executeQuery();
			String strEmpId = null;
			int i=0;
			while (rs.next( )) {
				
				strEmpId = rs.getString("emp_per_id");
				
				if(strType!=null && strType.equalsIgnoreCase("C")){
					alInner.add("<a href=" + request.getContextPath() + "/ClockEntries.action?T=C&PAY=Y&EMPID="+strEmpId+"&PC="+strPC+"&D1="+strD1+"&D2="+strD2+">"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"</a>");
					i++;
				}else if(strType!=null && strType.equalsIgnoreCase("T")){
					alInner.add("<a href=" + request.getContextPath() + "/ClockEntries.action?T=T&PAY=Y&EMPID="+strEmpId+"&PC="+strPC+"&D1="+strD1+"&D2="+strD2+">"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"</a>");
					i++;
				}else if(strType!=null && strType.equalsIgnoreCase("O") && alExceptionEmployees!=null && alExceptionEmployees.contains(strEmpId)){
					if(i==0){
						alInner.add("Choose exceptions by employee name");
					}
					alInner.add("<a href=" + request.getContextPath() + "/UpdateClockEntries.action?PAY=Y&EMPID="+strEmpId+"&PC="+strPC+"&D1="+strD1+"&D2="+strD2+">"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"</a>");
					i++;
				}else if(strType!=null && strType.equalsIgnoreCase("RRA")){
					alInner.add("<a href=" + request.getContextPath() + "/ClockEntries.action?T=RRA&PAY=Y&EMPID="+strEmpId+"&PC="+strPC+"&D1="+strD1+"&D2="+strD2+">"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"</a>");
					i++;
				}else if(strType!=null && strType.equalsIgnoreCase("RE")){
					alInner.add("<a href=" + request.getContextPath() + "/ApproveClockEntries.action?T=RE&PAY=Y&EMPID="+strEmpId+"&PC="+strPC+"&D1="+strD1+"&D2="+strD2+">"+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+"</a>");
					i++;
				}
			}
			rs.close();
			pst.close();
			
			if(i==0){
				alInner.add("<a href=\"" + request.getContextPath() + "/PayCycleList.action?T="+strType+" \">No employees available in selected paycycle. Please go back and select different paycycle.</a>");
			}
			
			
			
			
			
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone((CF.getStrTimeZone())));
			
			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(strD1, CF.getStrReportDateFormat(), "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, CF.getStrReportDateFormat(), "MM")) -1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, CF.getStrReportDateFormat(), "yyyy")));
			

			List alDates = new ArrayList();
			String strDate = null;
			for(int j=0; j<14 && i!=0; j++){
				
				if(j==0){
					alDates.add("Choose exceptions by date");
				}
				
				strDate = ((cal.get(Calendar.DAY_OF_MONTH)<10)?"0"+cal.get(Calendar.DAY_OF_MONTH):cal.get(Calendar.DAY_OF_MONTH))+"/"+(((cal.get(Calendar.MONTH)+1)<10)?"0"+(cal.get(Calendar.MONTH)+1):(cal.get(Calendar.MONTH)+1))+"/"+((cal.get(Calendar.YEAR)<10)?"0"+cal.get(Calendar.YEAR):cal.get(Calendar.YEAR));
				alDates.add("<a href=" + request.getContextPath() + "/UpdateClockEntries.action?PAY=Y&T=O&PAY=Y&PC="+strPC+"&D1="+strDate+">"+strDate+"</a>");
				
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
			
			
			
			
			
			request.setAttribute("reportList", alInner);
			request.setAttribute("alDates", alDates);
			

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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

}
