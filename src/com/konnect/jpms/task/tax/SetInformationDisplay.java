package com.konnect.jpms.task.tax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SetInformationDisplay extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strSessionEmpId;
	
	String operation;
	
	String strOnlyTeam;
	String strCost;
	String strRate;
	String strFreq;
	String dataType;
	
	String strAttendFromAttendDetail;
	String strAttendFromTimesheetDetail;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(getOperation() != null) {
			
			if(getDataType() != null && getDataType().equals("InfoDisp")) {
				return setInformationDisplay(uF);
			} else if(getDataType() != null && getDataType().equals("SnapTime")) {
				return setSnapshotTimeSet(uF);
			} else if(getDataType() != null && getDataType().equals("Attendance")) {
				return setAttendanceFrom(uF);
			}
		}
		
		viewInformationDisplay(uF);
		return LOAD;
	}

	
	public String setAttendanceFrom(UtilityFunctions uF) {
		
	    double strTimeFreq=uF.parseToDouble(getStrFreq());
	    strTimeFreq=strTimeFreq*60000;
	    System.out.println("milsec---"+strTimeFreq);
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			boolean flag = false;
			int pro_info_disp_id = 0;
			pst = con.prepareStatement("select project_info_display_id from project_information_display");
			rs = pst.executeQuery();
			while(rs.next()) {
				flag = true;
				pro_info_disp_id = rs.getInt("project_info_display_id");
			}
			rs.close();
			pst.close();
			
			if(!flag) {
				pst = con.prepareStatement("INSERT INTO project_information_display(attend_from_attend_detail,attend_from_timesheet_detail,updated_by,update_date) VALUES (?,?,?,?)");
				pst.setBoolean(1, (getStrAttendFromAttendDetail() != null && getStrAttendFromAttendDetail().equals("AD")) ? true : false );
				pst.setBoolean(2, (getStrAttendFromAttendDetail() != null && getStrAttendFromAttendDetail().equals("TD")) ? true : false );
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
				pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.execute();
				pst.close();
			} else {
				pst = con.prepareStatement("update project_information_display set attend_from_attend_detail=?,attend_from_timesheet_detail=?,updated_by=?,update_date=? where project_info_display_id=?");
				pst.setBoolean(1, (getStrAttendFromAttendDetail() != null && getStrAttendFromAttendDetail().equals("AD")) ? true : false );
				pst.setBoolean(2, (getStrAttendFromAttendDetail() != null && getStrAttendFromAttendDetail().equals("TD")) ? true : false );
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
				pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(5, pro_info_disp_id);
				pst.execute();
				pst.close();
			}
			session.setAttribute(MESSAGE, SUCCESSM+" Attendance setting updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	
	}


	public String setSnapshotTimeSet(UtilityFunctions uF) {
		
        double strTimeFreq=uF.parseToDouble(getStrFreq());
        strTimeFreq=strTimeFreq*60000;
        System.out.println("milsec---"+strTimeFreq);
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			boolean flag = false;
			int pro_info_disp_id = 0;
			pst = con.prepareStatement("select project_info_display_id from project_information_display");
			rs = pst.executeQuery();
			while(rs.next()) {
				flag = true;
				pro_info_disp_id = rs.getInt("project_info_display_id");
			}
			rs.close();
			pst.close();
			
			if(!flag) {
				pst = con.prepareStatement("INSERT INTO project_information_display(snapshot_time,updated_by,update_date) VALUES (?,?,?)");
				pst.setDouble(1, strTimeFreq);
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.execute();
				pst.close();
			} else {
				pst = con.prepareStatement("update project_information_display set snapshot_time=?,updated_by=?,update_date=? where project_info_display_id=?");
				pst.setDouble(1, strTimeFreq);
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(4, pro_info_disp_id);
				pst.execute();
				pst.close();
			}
			session.setAttribute(MESSAGE, SUCCESSM+" Snapshot time setting updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String setInformationDisplay(UtilityFunctions uF) {
		
        double strTimeFreq=uF.parseToDouble(getStrFreq());
        strTimeFreq=strTimeFreq*60000;
        System.out.println("milsec---"+strTimeFreq);
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			boolean flag = false;
			int pro_info_disp_id = 0;
			pst = con.prepareStatement("select project_info_display_id from project_information_display");
			rs = pst.executeQuery();
			while(rs.next()) {
				flag = true;
				pro_info_disp_id = rs.getInt("project_info_display_id");
			}
			rs.close();
			pst.close();
			
			if(!flag) {
				pst = con.prepareStatement("INSERT INTO project_information_display(only_team,is_cost,is_rate,updated_by,update_date) VALUES (?,?,?,?, ?)");
				pst.setBoolean(1, uF.parseToBoolean(getStrOnlyTeam()));
				pst.setBoolean(2, uF.parseToBoolean(getStrCost()));
				pst.setBoolean(3, uF.parseToBoolean(getStrRate()));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.execute();
				pst.close();
			} else {
				pst = con.prepareStatement("update project_information_display set only_team=?,is_cost=?,is_rate=?,updated_by=?,update_date=? where project_info_display_id=?");
				pst.setBoolean(1, uF.parseToBoolean(getStrOnlyTeam()));
				pst.setBoolean(2, uF.parseToBoolean(getStrCost()));
				pst.setBoolean(3, uF.parseToBoolean(getStrRate()));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(6, pro_info_disp_id);
				pst.execute();
				pst.close();
			}
			session.setAttribute(MESSAGE, SUCCESSM+" Information display setting updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	
	
	public String viewInformationDisplay(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from project_information_display");
			rs = pst.executeQuery();
			while(rs.next()) {
				if(rs.getBoolean("only_team")) {
					setStrOnlyTeam("1");
				} else {
					setStrOnlyTeam("0");
				}
				if(rs.getBoolean("is_cost")) {
					setStrCost("1");
				} else {
					setStrCost("0");
				}
				if(rs.getBoolean("is_rate")) {
					setStrRate("1");
				} else {
					setStrRate("0");
				}
				if(rs.getBoolean("attend_from_attend_detail")) {
					setStrAttendFromAttendDetail("1");
				} else {
					setStrAttendFromAttendDetail("0");
				}
				if(rs.getBoolean("attend_from_timesheet_detail")) {
					setStrAttendFromTimesheetDetail("1");
				} else {
					setStrAttendFromTimesheetDetail("0");
				}
				
				double snapshot_time=rs.getDouble("snapshot_time");
				snapshot_time=(snapshot_time/1000)/60;
				int time=(int) snapshot_time;
				setStrFreq(""+time);
				
			}
			rs.close();
			pst.close();
			
			if(getStrOnlyTeam() == null) {
				setStrOnlyTeam("0");
				setStrCost("0");
				setStrRate("0");
				setStrFreq("0");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;

	}
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getStrOnlyTeam() {
		return strOnlyTeam;
	}

	public void setStrOnlyTeam(String strOnlyTeam) {
		this.strOnlyTeam = strOnlyTeam;
	}

	public String getStrCost() {
		return strCost;
	}

	public void setStrCost(String strCost) {
		this.strCost = strCost;
	}

	public String getStrRate() {
		return strRate;
	}

	public void setStrRate(String strRate) {
		this.strRate = strRate;
	}

	public String getStrFreq() {
		return strFreq;
	}

	public void setStrFreq(String strFreq) {
		this.strFreq = strFreq;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getStrAttendFromAttendDetail() {
		return strAttendFromAttendDetail;
	}

	public void setStrAttendFromAttendDetail(String strAttendFromAttendDetail) {
		this.strAttendFromAttendDetail = strAttendFromAttendDetail;
	}

	public String getStrAttendFromTimesheetDetail() {
		return strAttendFromTimesheetDetail;
	}

	public void setStrAttendFromTimesheetDetail(String strAttendFromTimesheetDetail) {
		this.strAttendFromTimesheetDetail = strAttendFromTimesheetDetail;
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
    
}