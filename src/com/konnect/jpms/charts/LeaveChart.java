package com.konnect.jpms.charts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.interceptor.ServletRequestAware;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;


public class LeaveChart extends ActionSupport implements
ServletRequestAware, IStatements {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2070869391718300921L;
	private String duration;
	HttpSession session;
	private HttpServletRequest request;
	CommonFunctions CF;
	
	public String execute()throws Exception{
	    
		System.out.println("getDuration===="+getDuration());
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		return loadLeaveDetails();

	}

//	public static final String selectLeaveRequestsPerEmp = "SELECT count(*) FROM emp_leave_entry WHERE emp_id = ?" ;
//	public static final String selectLeaveStatusPerEmp = "SELECT count(*) FROM emp_leave_entry WHERE emp_id = ? and is_approved = ?" ;
	
	
	public String loadLeaveDetails() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String selectDatesPrev11 = "";
		ArrayList<Date> alDates = new ArrayList<Date>();
		ArrayList<String> alLeaveDetails = new ArrayList<String>();
		String userType = ((String)session.getAttribute(IConstants.USERTYPE));
		String EMPID = ((String)session.getAttribute("EMPID"));
		
		try {
			
			con = db.makeConnection(con);
			
			if(!getDuration().equalsIgnoreCase("day")) {
			
				if(getDuration().equalsIgnoreCase("month")) 
					selectDatesPrev11 = "Select * from (SELECT * FROM alldates where _date<= ? order by _date) a order by _date desc LIMIT 30";
				else if(getDuration().equalsIgnoreCase("week"))
					selectDatesPrev11 = "Select * from (SELECT * FROM alldates where _date<= ? order by _date) a order by _date desc LIMIT 7";
				
				pst = con.prepareStatement(selectDatesPrev11);
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				System.out.println("pst1==>>"+pst);
				rs = pst.executeQuery();
				
				while(rs.next()) {
					alDates.add((rs.getDate("_date")));
				}
		        rs.close();
		        pst.close();
				System.out.println("alDates====>>"+alDates);
			}
			
			if(getDuration().equalsIgnoreCase("month") || getDuration().equalsIgnoreCase("week")) { 
				
				if(userType.equals(IConstants.HRMANAGER) || userType.equals(IConstants.ACCOUNTANT)) {
					pst = con.prepareStatement(selectLeaveRequestsBetweenPerWlocation);
					pst.setInt(1, uF.parseToInt(EMPID));
					pst.setDate(2, alDates.get(alDates.size()-1));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					System.out.println("pst2=>"+pst);
					rs = pst.executeQuery();
				
				}else{
				
					pst = con.prepareStatement(selectLeaveRequestsBetween);
					pst.setDate(1, alDates.get(alDates.size()-1));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					System.out.println("pst2=>"+pst);
					rs = pst.executeQuery();
				}
			
			}else {
				
				if(userType.equals(IConstants.HRMANAGER) || userType.equals(IConstants.ACCOUNTANT)) {
					pst = con.prepareStatement(selectLeaveRequestsOnPerWlocation);
					pst.setInt(1, uF.parseToInt(EMPID));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					System.out.println("pst2=>"+pst);
					rs = pst.executeQuery();
				
				}else {
					
					pst = con.prepareStatement(selectLeaveRequestsOn);
					pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
					System.out.println("pst2=>"+pst);
					rs = pst.executeQuery();
				}
				
			}
			
			while(rs.next()) {
				int cnt = rs.getInt("count");
				alLeaveDetails.add(cnt+"");
			}
	        rs.close();
	        pst.close();
			
			//Approved =>
			
			if(userType.equals(IConstants.HRMANAGER) || userType.equals(IConstants.ACCOUNTANT)) {
				
				if(getDuration().equalsIgnoreCase("day")) {
					
					pst = con.prepareStatement(selectLeaveStatusOnPerWlocation);
					pst.setInt(1, 1);
					pst.setInt(2, uF.parseToInt(EMPID));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					
					System.out.println("pst Approved selectLeaveStatusOn=>"+pst);
					rs = pst.executeQuery();
				
				}else {
					
					pst = con.prepareStatement(selectLeaveStatusBetweenPerWlolcation);
					pst.setInt(1, 1);
					pst.setInt(2, uF.parseToInt(EMPID));
					pst.setDate(3, alDates.get(alDates.size()-1));
					pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
					System.out.println("pst Approved selectLeaveStatusBetween "+pst);
					rs = pst.executeQuery();
				}
				while(rs.next()) {
					int cnt = rs.getInt("count");
					alLeaveDetails.add(cnt+"");
				}
		        rs.close();
		        pst.close();
				
				//Pending =>
				if(getDuration().equalsIgnoreCase("day")) {
					pst = con.prepareStatement(selectLeaveStatusOnPerWlocation);
					pst.setInt(1, 0);
					pst.setInt(2, uF.parseToInt(EMPID));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					
					System.out.println("pst Pending selectLeaveStatusOn=>"+pst);
					rs = pst.executeQuery();
				
				}else {
					
					pst = con.prepareStatement(selectLeaveStatusBetweenPerWlolcation);
					pst.setInt(1, 0);
					pst.setInt(2, uF.parseToInt(EMPID));
					pst.setDate(3, alDates.get(alDates.size()-1));
					pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
					System.out.println("pst pending selectLeaveStatusBetween=>"+pst);
					rs = pst.executeQuery();
				}
				while(rs.next()) {
					int cnt = rs.getInt("count");
					alLeaveDetails.add(cnt+"");
				}
		        rs.close();
		        pst.close();
				
				//Denied =>
				if(getDuration().equalsIgnoreCase("day")) {
					pst = con.prepareStatement(selectLeaveStatusOnPerWlocation);
					pst.setInt(1, -1);
					pst.setInt(2, uF.parseToInt(EMPID));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					System.out.println("pst Denied selectLeaveStatusOn=>"+pst);
					rs = pst.executeQuery();
				
				}else {
					
					pst = con.prepareStatement(selectLeaveStatusBetweenPerWlolcation);
					pst.setInt(1, -1);
					pst.setInt(2, uF.parseToInt(EMPID));
					pst.setDate(3, alDates.get(alDates.size()-1));
					pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
					
					System.out.println("pst Denied selectLeaveStatusBetween=>"+selectLeaveStatusBetween);
					rs = pst.executeQuery();
				}
				while(rs.next()) {
					int cnt = rs.getInt("count");
					alLeaveDetails.add(cnt+"");
				}
		        rs.close();
		        pst.close();
				
			}else if (userType.equals(IConstants.EMPLOYEE)) {
			
				if(getDuration().equalsIgnoreCase("overall")) {
					
//					//Approved (Taken)
//					pst = con.prepareStatement(selectLeaveStatusPerEmp);
//					pst.setInt(1, uF.parseToInt(EMPID));
//					pst.setInt(2, 1);
//					
//					System.out.println("pst Approved selectLeaveStatusPerEmp=>"+pst);
//					rs = pst.executeQuery();
//					
//					while(rs.next()) {
//						int cnt = rs.getInt("count");
//						alLeaveDetails.add(cnt+"");
//					}
//					
//					//Pending 
//					pst = con.prepareStatement(selectLeaveStatusPerEmp);
//					pst.setInt(1, uF.parseToInt(EMPID));
//					pst.setInt(2, 0);
//					
//					System.out.println("pst Pending selectLeaveStatusPerEmp=>"+pst);
//					rs = pst.executeQuery();
//					
//					while(rs.next()) {
//						int cnt = rs.getInt("count");
//						alLeaveDetails.add(cnt+"");
//					}
					
					
					
					
//					pst = con.prepareStatement(selectLeaveStatusPerEmp);
//					pst.setInt(1, uF.parseToInt(EMPID));
//					pst.setInt(2, 1);
//					
//					System.out.println("pst Approved selectLeaveStatusPerEmp=>"+pst);
//					rs = pst.executeQuery();
//					
//					while(rs.next()) {
//						int cnt = rs.getInt("count");
//						alLeaveDetails.add(cnt+"");
//					}
					
				}
			
			}else { //For other users
			
				if(getDuration().equalsIgnoreCase("day")) {
					
					pst = con.prepareStatement(selectLeaveStatusOn);
					pst.setInt(1, 1);
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					
					System.out.println("pst Approved selectLeaveStatusOn=>"+pst);
					rs = pst.executeQuery();
				
				}else {
					
					pst = con.prepareStatement(selectLeaveStatusBetween);
					pst.setInt(1, 1);
					pst.setDate(2, alDates.get(alDates.size()-1));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					System.out.println("pst Approved selectLeaveStatusBetween "+pst);
					rs = pst.executeQuery();
				}
				while(rs.next()) {
					int cnt = rs.getInt("count");
					alLeaveDetails.add(cnt+"");
				}
		        rs.close();
		        pst.close();
				
				//Pending =>
				if(getDuration().equalsIgnoreCase("day")) {
					pst = con.prepareStatement(selectLeaveStatusOn);
					pst.setInt(1, 0);
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					
					System.out.println("pst Pending selectLeaveStatusOn=>"+pst);
					rs = pst.executeQuery();
				
				}else {
					
					pst = con.prepareStatement(selectLeaveStatusBetween);
					pst.setInt(1, 0);
					pst.setDate(2, alDates.get(alDates.size()-1));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					System.out.println("pst pending selectLeaveStatusBetween=>"+pst);
					rs = pst.executeQuery();
				}
				while(rs.next()) {
					int cnt = rs.getInt("count");
					alLeaveDetails.add(cnt+"");
				}
		        rs.close();
		        pst.close();
				
				//Denied =>
				if(getDuration().equalsIgnoreCase("day")) {
					pst = con.prepareStatement(selectLeaveStatusOn);
					pst.setInt(1, -1);
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					System.out.println("pst Denied selectLeaveStatusOn=>"+pst);
					rs = pst.executeQuery();
				
				}else {
					
					pst = con.prepareStatement(selectLeaveStatusBetween);
					pst.setInt(1, -1);
					pst.setDate(2, alDates.get(alDates.size()-1));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					System.out.println("pst Denied selectLeaveStatusBetween=>"+selectLeaveStatusBetween);
					rs = pst.executeQuery();
				}
				while(rs.next()) {
					int cnt = rs.getInt("count");
					alLeaveDetails.add(cnt+"");
				}
		        rs.close();
		        pst.close();
			}
			
			System.out.println("alLeaveDetails========>>>>>>"+alLeaveDetails);
			
			request.setAttribute("alDates", alDates);
			request.setAttribute("alLeaveDetails", alLeaveDetails);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return SUCCESS;
	}
	
	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
}