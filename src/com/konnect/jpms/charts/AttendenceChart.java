package com.konnect.jpms.charts;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AttendenceChart extends ActionSupport implements
		ServletRequestAware, IStatements {

	/** 
	 *  
	 */
	private static final long serialVersionUID = 6684038378838021787L;
	HttpSession session;
	private HttpServletRequest request;
	CommonFunctions CF;
	
	public String execute() {

		System.out.println("execute of AttendenceChart..");
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		return loadAttendanceDetails();

	}

	public String loadAttendanceDetails() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ArrayList<Date> alDates = new ArrayList<Date>();
		ArrayList<String> alDatesChart = new ArrayList<String>();
		ArrayList<String> alLeaveEntriesChart = new ArrayList<String>();
		ArrayList<String> alLateInChart = new ArrayList<String>();
		ArrayList<String> alEarlyOutChart = new ArrayList<String>();
		ArrayList<String> alAbsentChart = new ArrayList<String>();
		String userType = ((String)session.getAttribute(IConstants.USERTYPE));
		String EMPID = ((String)session.getAttribute("EMPID"));
		ArrayList<String> alEmpInWlocation = new ArrayList<String>();
		try {
			
			con = db.makeConnection(con);

			//Select Dates =>
			pst = con.prepareStatement(selectDatesPrev1);
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst1==>" + pst);
			rs = pst.executeQuery();

			while (rs.next()) {
				alDates.add((rs.getDate("_date")));
				alDatesChart.add("'"+(uF.getDateFormat(rs.getString("_date"), DBDATE, "dd MMM"))+"'");
			}
			rs.close();
			pst.close();

			//Leave Request =>
			
			for(int i=0;i<alDates.size(); i++) {
				
				ArrayList<ArrayList> alLeaveEntries = new ArrayList<ArrayList>();
				
				if(userType.equals(IConstants.HRMANAGER) || userType.equals(IConstants.ACCOUNTANT)) {
					
					pst = con.prepareStatement(selectLeaveRequestsPerWlocation);
					pst.setDate(1, alDates.get(i));
					pst.setInt(2, uF.parseToInt(EMPID));
//					System.out.println("pst selectLeaveRequestsPerWlocation==>" + pst);
					rs = pst.executeQuery();
					
				}else {
					pst = con.prepareStatement(selectLeaveRequests);
					pst.setDate(1, alDates.get(i));
//					System.out.println("pst selectLeaveRequests==>" + pst);
					rs = pst.executeQuery();
					
				}
				
				while (rs.next()) {
					ArrayList<String> inner = new ArrayList<String>();
					inner.add(rs.getString("emp_id"));
					inner.add(rs.getString("entrydate"));
					alLeaveEntries.add(inner);
				}
				
				alLeaveEntriesChart.add(alLeaveEntries.size()+"");
				rs.close();
				pst.close();
			
//				System.out.println("alLeaveEntries for "+alDates.get(i)+" "+alLeaveEntries);
				
			// Late/Early Attendance ==>>
			
//			for(int i=0;i<alDates.size(); i++) {
				
				ArrayList<ArrayList> alLateIn = new ArrayList<ArrayList>();
				
				if(userType.equals(IConstants.HRMANAGER) || userType.equals(IConstants.ACCOUNTANT)) {
					
					pst = con.prepareStatement(selectLateInPerWlocation);
					pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(2, uF.parseToInt(EMPID));
					rs = pst.executeQuery();
				
				}else {
				
					pst = con.prepareStatement(selectLateIn);
					pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
					rs = pst.executeQuery();
				
				}
				
				while (rs.next()) {
					ArrayList<String> inner = new ArrayList<String>();
					inner.add(rs.getDate(3).toString());
					inner.add(rs.getString("emp_id"));
					alLateIn.add(inner);
				}
				alLateInChart.add(alLateIn.size()+"");
				rs.close();
				pst.close();

			
				ArrayList<ArrayList> alEarlyOut = new ArrayList<ArrayList>();
				if(userType.equals(IConstants.HRMANAGER) || userType.equals(IConstants.ACCOUNTANT)) {
					
					pst = con.prepareStatement(selectEarlyOutPerWlocation);
					pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(2, uF.parseToInt(EMPID));
					rs = pst.executeQuery();
					
				}else {
					
					pst = con.prepareStatement(selectEarlyOut);
					pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
					rs = pst.executeQuery();
					
				}
				
				while (rs.next()) {
					ArrayList<String> inner = new ArrayList<String>();
					inner.add(rs.getString("emp_id"));
					alEarlyOut.add(inner);
				}
				alEarlyOutChart.add(alEarlyOut.size()+"");
				rs.close();
				pst.close();
			
			// Absent ==>
				
				ArrayList<ArrayList> alAbsent = new ArrayList<ArrayList>();
				if(userType.equals(IConstants.HRMANAGER) || userType.equals(IConstants.ACCOUNTANT)) {
					pst = con.prepareStatement(selectAbsentPerWlocation);
					pst.setDate(1, alDates.get(i));
					pst.setInt(2, uF.parseToInt(EMPID));
					System.out.println("pst5=>" + pst);
					rs = pst.executeQuery();
				
				}else {
					
					pst = con.prepareStatement(selectAbsent);
					pst.setDate(1, alDates.get(i));
					System.out.println("pst5=>" + pst);
					rs = pst.executeQuery();
				}
	
				while (rs.next()) {
						
					ArrayList<String> inner = new ArrayList<String>();
					inner.add(rs.getDate(3).toString());
					inner.add(rs.getInt(2) + "");
					alAbsent.add(inner);
				}
				
				alAbsentChart.add(alAbsent.size()+"");
				rs.close();
				pst.close();
				
			}
			
//			System.out.println("alDates==>" + alDates);
//			System.out.println("alLeaveEntriesChart====>>"+alLeaveEntriesChart);
//			System.out.println("alLateInChart===>>" + alLateInChart);
//			System.out.println("alEarlyOutChart===>>" + alEarlyOutChart);
//			System.out.println("alAbsentChart====>"+alAbsentChart);
			
			request.setAttribute("alDatesChart", alDatesChart);
			request.setAttribute("alLeaveEntriesChart", alLeaveEntriesChart);
			request.setAttribute("alLateInChart", alLateInChart);
			request.setAttribute("alEarlyOutChart", alEarlyOutChart);
			request.setAttribute("alAbsentChart", alAbsentChart);
			
		} catch (SQLException se) {
			se.printStackTrace();

		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
