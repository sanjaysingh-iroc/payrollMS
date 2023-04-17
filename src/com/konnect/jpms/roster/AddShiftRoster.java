package com.konnect.jpms.roster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;


public class AddShiftRoster extends ActionSupport implements ServletRequestAware, IStatements {

	
	
	private static final long serialVersionUID = 1L;
	CommonFunctions CF = null;
	HttpSession session;
	
	String shiftId;
	String shiftCode;
	String shiftName;
	String shiftType;
	String shiftStartTime;
	String shiftEndTime;
	String breakStartTime;
	String breakEndTime;
	String totalHours;
	String colourCode;
	
	String strOrg;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		session = request.getSession();
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		request.setAttribute(PAGE, "/jsp/roster/AddShift.jsp");
		UtilityFunctions uF = new UtilityFunctions();
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		
		if (operation!=null && operation.equals("D")) {
			return deleteShift(uF,strId);
		} 
		if (operation!=null && operation.equals("E")) {
			return viewShift(uF,strId);
		}
		
		if (uF.parseToInt(getShiftId()) > 0) {
			return updateShift(uF);
		}
		if (getShiftCode()!=null && getShiftCode().length()>0) {
			return insertShift(uF);
		}
		
		return LOAD;
	}

	
	private String viewShift(UtilityFunctions uF, String strId) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM shift_details where shift_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			while(rs.next()){
				setShiftId(rs.getString("shift_id"));
				setShiftCode(rs.getString("shift_code"));
				setShiftName(rs.getString("shift_name"));
				setShiftType(rs.getString("shift_type"));
				setShiftStartTime(uF.getDateFormat(rs.getString("_from"), DBTIME, TIME_FORMAT));
				setShiftEndTime(uF.getDateFormat(rs.getString("_to"), DBTIME, TIME_FORMAT));
				setBreakStartTime(uF.getDateFormat(rs.getString("break_start"), DBTIME, TIME_FORMAT));
				setBreakEndTime(uF.getDateFormat(rs.getString("break_end"), DBTIME, TIME_FORMAT));
				setColourCode(rs.getString("colour_code"));
				setStrOrg(rs.getString("org_id"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;

	}
	
	public String insertShift(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(insertShift);
			pst.setTime(1, uF.getTimeFormat(getShiftStartTime(), TIME_FORMAT));
			pst.setTime(2, uF.getTimeFormat(getShiftEndTime(), TIME_FORMAT));
			pst.setString(3, getShiftCode());
			pst.setTime(4, uF.getTimeFormat(getBreakStartTime(), TIME_FORMAT));
			pst.setTime(5, uF.getTimeFormat(getBreakEndTime(), TIME_FORMAT));
			pst.setString(6, getColourCode());
//			pst.setString(7, getShiftType());
			pst.setString(7, uF.compareShiftTime(getShiftStartTime(), TIME_FORMAT,getShiftEndTime(), TIME_FORMAT));
			pst.setInt(8, uF.parseToInt(getStrOrg()));
			pst.setString(9, getShiftName());
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	
	public String updateShift(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update shift_details set _from=?,_to=?,shift_code=?,break_start=?,break_end=?,colour_code=?,shift_type=?,shift_name=? where shift_id=?");
			pst.setTime(1, uF.getTimeFormat(getShiftStartTime(), TIME_FORMAT));
			pst.setTime(2, uF.getTimeFormat(getShiftEndTime(), TIME_FORMAT));
			pst.setString(3, getShiftCode());
			pst.setTime(4, uF.getTimeFormat(getBreakStartTime(), TIME_FORMAT));
			pst.setTime(5, uF.getTimeFormat(getBreakEndTime(), TIME_FORMAT));
			pst.setString(6, getColourCode());
//			pst.setString(7, getShiftType());
			pst.setString(7, uF.compareShiftTime(getShiftStartTime(), TIME_FORMAT,getShiftEndTime(), TIME_FORMAT));
			pst.setString(8, getShiftName());
			pst.setInt(9, uF.parseToInt(getShiftId()));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();			
			return ERROR;
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String deleteShift(UtilityFunctions uF, String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteShiftDetail);
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	public HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getShiftCode() {
		return shiftCode;
	}

	public void setShiftCode(String shiftCode) {
		this.shiftCode = shiftCode;
	}

	public String getShiftType() {
		return shiftType;
	}

	public void setShiftType(String shiftType) {
		this.shiftType = shiftType;
	}

	public String getShiftStartTime() {
		return shiftStartTime;
	}

	public void setShiftStartTime(String shiftStartTime) {
		this.shiftStartTime = shiftStartTime;
	}

	public String getShiftEndTime() {
		return shiftEndTime;
	}

	public void setShiftEndTime(String shiftEndTime) {
		this.shiftEndTime = shiftEndTime;
	}

	public String getBreakStartTime() {
		return breakStartTime;
	}

	public void setBreakStartTime(String breakStartTime) {
		this.breakStartTime = breakStartTime;
	}

	public String getBreakEndTime() {
		return breakEndTime;
	}

	public void setBreakEndTime(String breakEndTime) {
		this.breakEndTime = breakEndTime;
	}

	public String getTotalHours() {
		return totalHours;
	}

	public void setTotalHours(String totalHours) {
		this.totalHours = totalHours;
	}

	public String getColourCode() {
		return colourCode;
	}

	public void setColourCode(String colourCode) {
		this.colourCode = colourCode;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getShiftId() {
		return shiftId;
	}

	public void setShiftId(String shiftId) {
		this.shiftId = shiftId;
	}

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}

	public String getShiftName() {
		return shiftName;
	}

	public void setShiftName(String shiftName) {
		this.shiftName = shiftName;
	}

}
