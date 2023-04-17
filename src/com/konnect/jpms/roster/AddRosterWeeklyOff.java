package com.konnect.jpms.roster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillWeekDays;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddRosterWeeklyOff extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	String rWeeklyOffId;
	String weeklyOff;
	String weeklyOffType;
	String[] weeklyOffDay;
	String[] weekno;
	
	List<FillWeekDays> weeklyOffTypeList;
	List<FillWeekDays> weeklyOffList; 
	List<FillWeekDays> weeklNoList; 
	
	
	CommonFunctions CF;
	HttpSession session;
	
	String strOrg;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/roster/AddRosterWeeklyOff.jsp");
		UtilityFunctions uF = new UtilityFunctions();
			
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");

		
		if (operation!=null && operation.equals("D")) {
			return deleteRosterWeeklyOFF(uF,strId);
		} 
		if (operation!=null && operation.equals("E")) {
			loadRosterWeeklyOFF(uF);
			return viewRosterWeeklyOff(uF,strId);
		}
		
		if (getrWeeklyOffId()!=null && getrWeeklyOffId().length()>0) {
			return updateRosterWeeklyOff(uF);
		}
		if (getWeeklyOff()!=null && getWeeklyOff().length()>0) {
			return insertRosterWeeklyOff(uF);
		}
		
		return loadRosterWeeklyOFF(uF);
		
	}
	private String insertRosterWeeklyOff(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("insert into roster_weeklyoff_policy (weeklyoff_name,weeklyoff_type,weeklyoff_day,weeklyoff_weekno," +
					"entry_date,added_by,org_id) values (?,?,?,?, ?,?,?)");
			pst.setString(1, getWeeklyOff());
			pst.setString(2, getWeeklyOffType());
			StringBuilder sb = new StringBuilder();
			for(int i=0; getWeeklyOffDay()!=null && i<getWeeklyOffDay().length; i++){
				sb.append(getWeeklyOffDay()[i]+",");
			}
			pst.setString(3, sb.toString());
			
			sb = new StringBuilder();
			for(int i=0; getWeekno()!=null && i<getWeekno().length; i++){
				sb.append(getWeekno()[i]+",");
			}
			pst.setString(4, sb.toString());
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(7, uF.parseToInt(getStrOrg()));
			pst.execute();
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+getWeeklyOff()+" saved successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;
	}
	public String updateRosterWeeklyOff(UtilityFunctions uF) {

		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update roster_weeklyoff_policy set weeklyoff_name=?,weeklyoff_type=?,weeklyoff_day=?,weeklyoff_weekno=?,entry_date=?,added_by=? where roster_weeklyoff_id=?");
			pst.setString(1, getWeeklyOff());
			pst.setString(2, getWeeklyOffType());
			StringBuilder sb = new StringBuilder();
			for(int i=0; getWeeklyOffDay()!=null && i<getWeeklyOffDay().length; i++){
				sb.append(getWeeklyOffDay()[i]+",");
			}
			pst.setString(3, sb.toString());
			
			sb = new StringBuilder();
			for(int i=0; getWeekno()!=null && i<getWeekno().length; i++){
				sb.append(getWeekno()[i]+",");
			}
			pst.setString(4, sb.toString());
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6,  uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(7,  uF.parseToInt(getrWeeklyOffId()));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+" updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}
	
	public String viewRosterWeeklyOff(UtilityFunctions uF,String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from roster_weeklyoff_policy where roster_weeklyoff_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			while(rs.next()){
				setrWeeklyOffId(rs.getString("roster_weeklyoff_id"));
				setWeeklyOff(rs.getString("weeklyoff_name"));
				setWeeklyOffType(rs.getString("weeklyoff_type"));

				if(rs.getString("weeklyoff_day")!=null && !rs.getString("weeklyoff_day").equals("")) {
					String[] arr = rs.getString("weeklyoff_day").split(",");
					setWeeklyOffDay(arr);
				}
				
				if(rs.getString("weeklyoff_weekno")!=null && !rs.getString("weeklyoff_weekno").equals("")) {
					String[] arr = rs.getString("weeklyoff_weekno").split(",");
					setWeekno(arr);
				}
				setStrOrg(rs.getString("org_id"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}
	
	public String deleteRosterWeeklyOFF(UtilityFunctions uF, String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from roster_weeklyoff_policy where roster_weeklyoff_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Roster Weekly Off deleted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	public String loadRosterWeeklyOFF(UtilityFunctions uF) {
		weeklyOffTypeList= new FillWeekDays().fillWeeklyOffType();
		weeklyOffList= new FillWeekDays().fillWeekDays();
		weeklNoList= new FillWeekDays().fillWeekNos();
		
		return LOAD;
	}
	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String getrWeeklyOffId() {
		return rWeeklyOffId;
	}
	
	public void setrWeeklyOffId(String rWeeklyOffId) {
		this.rWeeklyOffId = rWeeklyOffId;
	}
	
	public String getWeeklyOff() {
		return weeklyOff;
	}
	
	public void setWeeklyOff(String weeklyOff) {
		this.weeklyOff = weeklyOff;
	}
	
	public String getWeeklyOffType() {
		return weeklyOffType;
	}
	
	public void setWeeklyOffType(String weeklyOffType) {
		this.weeklyOffType = weeklyOffType;
	}
	
	public String[] getWeeklyOffDay() {
		return weeklyOffDay;
	}
	
	public void setWeeklyOffDay(String[] weeklyOffDay) {
		this.weeklyOffDay = weeklyOffDay;
	}
	
	public String[] getWeekno() {
		return weekno;
	}
	
	public void setWeekno(String[] weekno) {
		this.weekno = weekno;
	}
	
	public List<FillWeekDays> getWeeklyOffTypeList() {
		return weeklyOffTypeList;
	}
	
	public void setWeeklyOffTypeList(List<FillWeekDays> weeklyOffTypeList) {
		this.weeklyOffTypeList = weeklyOffTypeList;
	}
	
	public List<FillWeekDays> getWeeklyOffList() {
		return weeklyOffList;
	}
	
	public void setWeeklyOffList(List<FillWeekDays> weeklyOffList) {
		this.weeklyOffList = weeklyOffList;
	}
	
	public List<FillWeekDays> getWeeklNoList() {
		return weeklNoList;
	}
	
	public void setWeeklNoList(List<FillWeekDays> weeklNoList) {
		this.weeklNoList = weeklNoList;
	}
	
	public String getStrOrg() {
		return strOrg;
	}
	
	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
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
