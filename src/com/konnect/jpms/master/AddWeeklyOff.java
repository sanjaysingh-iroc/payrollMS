package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillBank;
import com.konnect.jpms.select.FillCity;
import com.konnect.jpms.select.FillCountry;
import com.konnect.jpms.select.FillCurrency;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.select.FillTimezones;
import com.konnect.jpms.select.FillWeekDays;
import com.konnect.jpms.select.FillWlocationType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddWeeklyOff extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	String businessId;
	
	String strStdTimeStart;
	String strStdTimeEnd;
	String strStdTimeStartHd1;
	String strStdTimeEndHd1;
	String strStdTimeStartHd2;
	String strStdTimeEndHd2;
	String strStdTimeStartHd3;
	String strStdTimeEndHd3;
	
	
	String weeklyOff1; 
	String weeklyOff2;
	String weeklyOff3;
	String weeklyOffType1; 
	String weeklyOffType2;
	String weeklyOffType3;
	String []weekno1;
	String []weekno2;
	String []weekno3;
	
	List<FillWeekDays> weeklyOffList;
	List<FillWeekDays> weeklyOffList1;
	List<FillWeekDays> weeklyOffTypeList;
	
	CommonFunctions CF;
	HttpSession session;
	
	String strOrg;
	String strLocation;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		
		request.setAttribute(PAGE, "/jsp/master/AddWeeklyOff.jsp");
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
			
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");

		loadValidateWLocation();
		
		if (operation!=null && operation.equals("D")) {
			return deleteWLocation(strId);
		} 
		if (operation!=null && operation.equals("E")) {
			return viewWLocation(strId);
		}
		
		if (getBusinessId()!=null && getBusinessId().length()>0) {
			return updateWLocation();
		}
		
		return LOAD;
		
	}
	public String updateWLocation() {

		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		
		String updateWLocation = "UPDATE work_location_info SET wlocation_weeklyoff1=?, wlocation_weeklyofftype1=?, wlocation_weeklyoff2=?, " +
				"wlocation_weeklyofftype2=?, wlocation_start_time_halfday=?, wlocation_end_time_halfday=?, wlocation_weeklyoff3=?," +
				" wlocation_weeklyofftype3=?, wlocation_weeknos3=?, wlocation_weeknos1=?, wlocation_weeknos2=?, wlocation_start_time_halfday1=?," +
				" wlocation_end_time_halfday1=?, wlocation_start_time_halfday2=?, wlocation_end_time_halfday2=? WHERE wlocation_id=?";
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateWLocation);
			pst.setString(1, getWeeklyOff1());
			pst.setString(2, getWeeklyOffType1());
			pst.setString(3, getWeeklyOff2());
			pst.setString(4, getWeeklyOffType2());
			pst.setTime(5, getStrStdTimeStartHd1()!=null && !getStrStdTimeStartHd1().equals("") && !getStrStdTimeStartHd1().equals("-") ? uF.getTimeFormat(getStrStdTimeStartHd1(), TIME_FORMAT) : null);
			pst.setTime(6, getStrStdTimeEndHd1()!=null && !getStrStdTimeEndHd1().equals("") && !getStrStdTimeEndHd1().equals("-") ? uF.getTimeFormat(getStrStdTimeEndHd1(), TIME_FORMAT) : null);
			pst.setString(7, getWeeklyOff3());
			pst.setString(8, getWeeklyOffType3());
			StringBuilder sb = new StringBuilder();
			for(int i=0; getWeekno3()!=null && i<getWeekno3().length; i++){
				sb.append(getWeekno3()[i]+",");
			}
			pst.setString(9, sb.toString());
		
			StringBuilder sbWeekNo1 = new StringBuilder();
//			System.out.println("getWeekno1() ====> " +getWeekno1());
			for(int i=0; getWeekno1()!=null && i<getWeekno1().length; i++){
				sbWeekNo1.append(getWeekno1()[i]+",");
			}
			pst.setString(10, sbWeekNo1.toString());
			
			StringBuilder sbWeekNo2 = new StringBuilder();
//			System.out.println("getWeekno2() ====> " +getWeekno2());
			for(int i=0; getWeekno2()!=null && i<getWeekno2().length; i++){
				sbWeekNo2.append(getWeekno2()[i]+",");
			}
			pst.setString(11, sbWeekNo2.toString());
			pst.setTime(12, getStrStdTimeStartHd2()!=null && !getStrStdTimeStartHd2().equals("") && !getStrStdTimeStartHd2().equals("-") ? uF.getTimeFormat(getStrStdTimeStartHd2(), TIME_FORMAT) : null);
			pst.setTime(13, getStrStdTimeEndHd2()!=null && !getStrStdTimeEndHd2().equals("") && !getStrStdTimeEndHd2().equals("-") ? uF.getTimeFormat(getStrStdTimeEndHd2(), TIME_FORMAT) : null);
			pst.setTime(14, getStrStdTimeStartHd3()!=null && !getStrStdTimeStartHd3().equals("") && !getStrStdTimeStartHd3().equals("-") ? uF.getTimeFormat(getStrStdTimeStartHd3(), TIME_FORMAT) : null);
			pst.setTime(15, getStrStdTimeEndHd3()!=null && !getStrStdTimeEndHd3().equals("") && !getStrStdTimeEndHd3().equals("-") ? uF.getTimeFormat(getStrStdTimeEndHd3(), TIME_FORMAT) : null);
			pst.setInt(16, uF.parseToInt(getBusinessId()));
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
	
	public String viewWLocation(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("Select * from ( Select * from ( Select * from work_location_info  WHERE wlocation_id= ? ) ast left join state s on ast.wlocation_state_id = s.state_id ) aco left join country co on aco.wlocation_country_id = co.country_id");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				setBusinessId(rs.getString("wlocation_id"));
				
				setWeeklyOff1(rs.getString("wlocation_weeklyoff1"));
				setWeeklyOffType1(rs.getString("wlocation_weeklyofftype1"));
				setWeeklyOff2(rs.getString("wlocation_weeklyoff2"));
				setWeeklyOffType2(rs.getString("wlocation_weeklyofftype2"));
				
				setStrStdTimeStartHd1(uF.getDateFormat(rs.getString("wlocation_start_time_halfday"), DBTIME, TIME_FORMAT));
				setStrStdTimeEndHd1(uF.getDateFormat(rs.getString("wlocation_end_time_halfday"), DBTIME, TIME_FORMAT));
				setStrStdTimeStartHd2(uF.getDateFormat(rs.getString("wlocation_start_time_halfday1"), DBTIME, TIME_FORMAT));
				setStrStdTimeEndHd2(uF.getDateFormat(rs.getString("wlocation_end_time_halfday1"), DBTIME, TIME_FORMAT));
				setStrStdTimeStartHd3(uF.getDateFormat(rs.getString("wlocation_start_time_halfday2"), DBTIME, TIME_FORMAT));
				setStrStdTimeEndHd3(uF.getDateFormat(rs.getString("wlocation_end_time_halfday2"), DBTIME, TIME_FORMAT));
				
				setWeeklyOff3(rs.getString("wlocation_weeklyoff3"));
				setWeeklyOffType3(rs.getString("wlocation_weeklyofftype3"));
				
				
				String []arr3 = null;
				if(rs.getString("wlocation_weeknos3")!=null && !rs.getString("wlocation_weeknos3").equals("")) {
					arr3 = rs.getString("wlocation_weeknos3").split(",");
					setWeekno3(arr3);
				}
				String []arr1 = null;
				if(rs.getString("wlocation_weeknos1")!=null && !rs.getString("wlocation_weeknos1").equals("")){
					arr1 = rs.getString("wlocation_weeknos1").split(",");
					setWeekno1(arr1);
				}
				String []arr2 = null;
				if(rs.getString("wlocation_weeknos2")!=null && !rs.getString("wlocation_weeknos2").equals("")){
					arr2 = rs.getString("wlocation_weeknos2").split(",");
					setWeekno2(arr2);
				}
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
	
	public String deleteWLocation(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteWLocation);
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Work location deleted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	public String loadValidateWLocation() {
		
		request.setAttribute(PAGE, PAddWLocation);
		request.setAttribute(TITLE, TAddWLocation);
			
		
		weeklyOffList= new FillWeekDays().fillWeekDays();
		weeklyOffList1= new FillWeekDays().fillWeekNos();
		weeklyOffTypeList= new FillWeekDays().fillWeeklyOffType();
		
		
		request.setAttribute("weeklyOffList", weeklyOffList);
		
		
		return LOAD;
	}
	
	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}


	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrStdTimeStart() {
		return strStdTimeStart;
	}

	public void setStrStdTimeStart(String strStdTimeStart) {
		this.strStdTimeStart = strStdTimeStart;
	}

	public String getStrStdTimeEnd() {
		return strStdTimeEnd;
	}

	public void setStrStdTimeEnd(String strStdTimeEnd) {
		this.strStdTimeEnd = strStdTimeEnd;
	}

	public List<FillWeekDays> getWeeklyOffList() {
		return weeklyOffList;
	}

	public String getWeeklyOff1() {
		return weeklyOff1;
	}

	public void setWeeklyOff1(String weeklyOff1) {
		this.weeklyOff1 = weeklyOff1;
	}

	public String getWeeklyOff2() {
		return weeklyOff2;
	}

	public void setWeeklyOff2(String weeklyOff2) {
		this.weeklyOff2 = weeklyOff2;
	}

	public String getWeeklyOffType1() {
		return weeklyOffType1;
	}

	public void setWeeklyOffType1(String weeklyOffType1) {
		this.weeklyOffType1 = weeklyOffType1;
	}

	public String getWeeklyOffType2() {
		return weeklyOffType2;
	}

	public void setWeeklyOffType2(String weeklyOffType2) {
		this.weeklyOffType2 = weeklyOffType2;
	}
	
	public List<FillWeekDays> getWeeklyOffTypeList() {
		return weeklyOffTypeList;
	}

	public void setWeeklyOffTypeList(List<FillWeekDays> weeklyOffTypeList) {
		this.weeklyOffTypeList = weeklyOffTypeList;
	}

	public String getStrStdTimeStartHd1() {
		return strStdTimeStartHd1;
	}

	public void setStrStdTimeStartHd1(String strStdTimeStartHd1) {
		this.strStdTimeStartHd1 = strStdTimeStartHd1;
	}

	public String getStrStdTimeEndHd1() {
		return strStdTimeEndHd1;
	}

	public void setStrStdTimeEndHd1(String strStdTimeEndHd1) {
		this.strStdTimeEndHd1 = strStdTimeEndHd1;
	}

	public String getStrStdTimeStartHd2() {
		return strStdTimeStartHd2;
	}

	public void setStrStdTimeStartHd2(String strStdTimeStartHd2) {
		this.strStdTimeStartHd2 = strStdTimeStartHd2;
	}

	public String getStrStdTimeEndHd2() {
		return strStdTimeEndHd2;
	}

	public void setStrStdTimeEndHd2(String strStdTimeEndHd2) {
		this.strStdTimeEndHd2 = strStdTimeEndHd2;
	}

	public String getStrStdTimeStartHd3() {
		return strStdTimeStartHd3;
	}

	public void setStrStdTimeStartHd3(String strStdTimeStartHd3) {
		this.strStdTimeStartHd3 = strStdTimeStartHd3;
	}

	public String getStrStdTimeEndHd3() {
		return strStdTimeEndHd3;
	}

	public void setStrStdTimeEndHd3(String strStdTimeEndHd3) {
		this.strStdTimeEndHd3 = strStdTimeEndHd3;
	}

	public List<FillWeekDays> getWeeklyOffList1() {
		return weeklyOffList1;
	}

	public void setWeeklyOffList1(List<FillWeekDays> weeklyOffList1) {
		this.weeklyOffList1 = weeklyOffList1;
	}

	public String getWeeklyOff3() {
		return weeklyOff3;
	}

	public void setWeeklyOff3(String weeklyOff3) {
		this.weeklyOff3 = weeklyOff3;
	}

	public String getWeeklyOffType3() {
		return weeklyOffType3;
	}

	public void setWeeklyOffType3(String weeklyOffType3) {
		this.weeklyOffType3 = weeklyOffType3;
	}

	public String[] getWeekno1() {
		return weekno1;
	}

	public void setWeekno1(String[] weekno1) {
		this.weekno1 = weekno1;
	}

	public String[] getWeekno2() {
		return weekno2;
	}

	public void setWeekno2(String[] weekno2) {
		this.weekno2 = weekno2;
	}

	public String[] getWeekno3() {
		return weekno3;
	}

	public void setWeekno3(String []weekno3) {
		this.weekno3 = weekno3;
	}
	public String getStrOrg() {
		return strOrg;
	}
	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}
	public String getStrLocation() {
		return strLocation;
	}
	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
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
