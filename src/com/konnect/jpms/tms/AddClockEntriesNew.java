package com.konnect.jpms.tms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.service.AttendanceService;
import com.konnect.jpms.service.Impl.AttendanceServiceImpl;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddClockEntriesNew extends ActionSupport implements ServletRequestAware, IStatements {
   
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId = null;
	CommonFunctions CF = null;
	String type;
	String paycycle;
	String strDate;
	String strEmpId;
	String strStartTime;
	String strEndTime;
	String org;
	String location;
	String level;
	 
	private static Logger log = Logger.getLogger(AddClockEntries.class);
	
	public String execute() throws Exception {

		log.debug("AddClockEntriesNew: execute()");
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
	
		
			if(getType()!=null && getType().equalsIgnoreCase("D")){
				deleteClockEntries(strDate, strEmpId);
				return "delete";
			}else if(getType()!=null && getType().equalsIgnoreCase("E")){
				updateClockEntries();
				return "clockentries";
			}else if(getType()!=null && getType().equalsIgnoreCase("V")){
				
				loadClockEntries(strEmpId, strDate);
			}
		
		return LOAD;

	}
	
	public void loadClockEntries(String  strEmpID, String  strDate){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);	
			
			boolean flag=true;
		 	pst = con.prepareStatement("SELECT * FROM attendance_details where cast(in_out_timestamp as date) = ?  and emp_id =?");
			pst.setDate(1, uF.getDateFormat(strDate, DATE_FORMAT));
			pst.setInt(2, uF.parseToInt(strEmpID));
			rs = pst.executeQuery();
			while(rs.next()){
				flag=false;
				if(rs.getString("in_out").equals("IN")){
				 setStrStartTime(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
				}else{
				 setStrEndTime(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
				}
			}
			rs.close();
			pst.close();
			
			
			if(flag){
				pst = con.prepareStatement("SELECT * FROM roster_details where _date = ?  and emp_id =?");
				pst.setDate(1, uF.getDateFormat(strDate, DATE_FORMAT));
	//				pst.setInt(2,0);
				pst.setInt(2, uF.parseToInt(strEmpID));
				rs = pst.executeQuery();
				if(rs.next()){
						setStrStartTime(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
						setStrEndTime(uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));
				}
				rs.close();
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally{
			db.closeConnection(con);
		}
	}
	
	
//	UtilityFunctions uF = new UtilityFunctions();
	
	
	public void deleteClockEntries(String strDate, String strEmpId){
		
		Connection con = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from attendance_details where emp_id=? and cast(in_out_timestamp as date)=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strDate, DATE_FORMAT));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from break_application_register where emp_id=? and _date=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strDate, DATE_FORMAT));				
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from overtime_hours where emp_id=? and _date=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strDate, DATE_FORMAT));				
			pst.execute();
			pst.close();
			
			/*request.setAttribute("STATUS_MSG", "<img src=\"images1/tick.png\" width=\"24px\">"); */
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-check checknew\" aria-hidden=\"true\"></i>"); 
			
				
				
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	public void updateClockEntries(){
		
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);
			
			PreparedStatement pst1 = con.prepareStatement("select * from roster_details where emp_id =? and _date=?");
			pst1.setInt(1, uF.parseToInt(getStrEmpId()));
			pst1.setDate(2, uF.getDateFormat(getStrDate(), DATE_FORMAT));
			ResultSet rs = pst1.executeQuery();
			
			List<String> currentDateRoster=new ArrayList<String>();
			
			while(rs.next()){
				
				currentDateRoster.add(rs.getString("_date"));
				currentDateRoster.add(rs.getString("_from"));
				currentDateRoster.add(rs.getString("_to"));
			}
			
			
			if(currentDateRoster.size()==0){
				
				
				ImportAttendance importAttendance=new ImportAttendance();
				currentDateRoster=importAttendance.insertRosterEntry(con, pst1, rs, uF,  uF.parseToInt(getStrEmpId()), strDate, 0, DATE_FORMAT);
				
			}
			
			
			
			
			String levelId = null;
			pst1 = con.prepareStatement("select a.*,dd.level_id from designation_details dd right join (select emp_id, gd.designation_id from employee_official_details eod, grades_details gd where emp_id=? and gd.grade_id=eod.grade_id) a on a.designation_id=dd.designation_id");
			pst1.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst1.executeQuery();
			

			while(rs.next()){
				levelId= rs.getString("level_id");
			}
			
			
			AttendanceService attendanceService=new AttendanceServiceImpl();
			
			int nIn =attendanceService.updateAttendanceService(con, uF, CF, currentDateRoster,uF.getDateFormat( getStrDate(), DATE_FORMAT, DBDATE),getStrStartTime(), null, uF.parseToInt(getStrEmpId()), 0, true,null,0,null);
			
			if(nIn==0){
				attendanceService.insertAttendanceService(con, uF, CF, 0, 0, null, currentDateRoster,uF.getDateFormat( getStrDate(), DATE_FORMAT, DBDATE), getStrStartTime(), null, null, uF.parseToInt(getStrEmpId()), 0,null,0,null);
				
				
				
			}
			PreparedStatement pst=null;
			String[] strPayCycleDates = paycycle.split("-");
				pst = con.prepareStatement("select * from overtime_details where level_id=?  and date_from <=  ? ");
				pst.setInt(1, uF.parseToInt(levelId));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				rs = pst.executeQuery();
				String value=null;
				while (rs.next()) {
					value=rs.getString("buffer_standard_time");
				}
			
			
			
			 nIn =attendanceService.updateAttendanceService(con, uF, CF, currentDateRoster, uF.getDateFormat( getStrDate(), DATE_FORMAT, DBDATE),getStrEndTime(),
					 uF.getDateFormat( getStrDate(), DATE_FORMAT, DBDATE)+" "+getStrStartTime(), uF.parseToInt(getStrEmpId()), 0, false,value,uF.parseToInt(strSessionEmpId),strPayCycleDates);



			
			if(nIn==0){
				attendanceService.insertAttendanceService(con, uF, CF, 0, 1, null, currentDateRoster, uF.getDateFormat( getStrDate(), DATE_FORMAT, DBDATE), getStrEndTime(), null,uF.getDateFormat( getStrDate(), DATE_FORMAT, DBDATE)+" "+getStrStartTime(), uF.parseToInt(getStrEmpId()), 0,value,uF.parseToInt(strSessionEmpId),strPayCycleDates);

			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally{
			db.closeConnection(con);			
		}
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getStrStartTime() {
		return strStartTime;
	}

	public void setStrStartTime(String strStartTime) {
		this.strStartTime = strStartTime;
	}

	public String getStrEndTime() {
		return strEndTime;
	}

	public void setStrEndTime(String strEndTime) {
		this.strEndTime = strEndTime;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

}
