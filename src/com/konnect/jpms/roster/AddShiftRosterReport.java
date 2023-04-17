package com.konnect.jpms.roster;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddShiftRosterReport extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpID;
	CommonFunctions CF = null;
	String _to = null;
    String _from = null;
   
    private static Logger log = Logger.getLogger(AddShiftRosterReport.class);
    
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF == null) return LOGIN;
		
		UtilityFunctions uF=new UtilityFunctions();
		
	    String shift_id = request.getParameter("shift_id");
	    String rosterDate = request.getParameter("rosterDate");
	    String empId = request.getParameter("empId");
	    String serviceId = request.getParameter("service");
	    Date rosterdate=uF.getDateFormat(rosterDate,CF.getStrReportDateFormat());
	    String day=null;
	    if(rosterdate!=null){
	    SimpleDateFormat f=new SimpleDateFormat("EEEE");
		day=f.format(rosterdate);
	    }
	    if(shift_id.equals("0")){
	    	if((workLocationInfo(uF.parseToInt(shift_id)).get(6)!=null)&&(day.equalsIgnoreCase(workLocationInfo(uF.parseToInt(shift_id)).get(4)) && (workLocationInfo(uF.parseToInt(shift_id)).get(5).equalsIgnoreCase("HD")) )){
	    		 _to =workLocationInfo(uF.parseToInt(shift_id)).get(7) ;
			 	 _from = workLocationInfo(uF.parseToInt(shift_id)).get(6);
	    	}
	    	else{
	    		
	    	
	    	 _to =workLocationInfo(uF.parseToInt(shift_id)).get(1) ;
		 	 _from = workLocationInfo(uF.parseToInt(shift_id)).get(0);
	    	}
	    }
	    else{
		     _to =shiftTimeDetails(uF.parseToInt(shift_id)).get(2) ;
		 	 _from = shiftTimeDetails(uF.parseToInt(shift_id)).get(1);
	    }
			    
	   
	      
	     
	     	Connection con = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			Database db = new Database();
			db.setRequest(request);
			
			Time startTime = uF.getTimeFormat(_from, CF.getStrReportTimeFormat());
			Time endTime = uF.getTimeFormat(_to, CF.getStrReportTimeFormat());
			
			double dblTotalRosterTime = 0d;
			long lStartTime = startTime.getTime();
			long lEndTime = endTime.getTime();
			
			dblTotalRosterTime = uF.parseToDouble(uF.getTimeDiffInHoursMins(lStartTime, lEndTime));
			
	     try {
	    	 
	    	 	con = db.makeConnection(con);
	    	 
	    	 
	    		 pst = con.prepareStatement("insert into roster_details (emp_id, _date, _from, _to, isapproved, user_id, service_id, actual_hours, attended,is_lunch_ded,shift_id,roster_weeklyoff_id) values(?,?,?,?,?,(select user_id from user_details where emp_id=?),?,?,?,?,?,?)");
	    		 
		    	
		    	 pst.setInt(1, uF.parseToInt(empId));
		    	 pst.setDate(2, uF.getDateFormat(rosterDate, CF.getStrReportDateFormat()));
		    	 pst.setTime(3, uF.getTimeFormat(_from, TIME_FORMAT));
		    	 pst.setTime(4, uF.getTimeFormat(_to, TIME_FORMAT));
		    	 pst.setBoolean(5, false);
		    	 pst.setInt(6, uF.parseToInt(empId));
		    	 pst.setInt(7, uF.parseToInt(serviceId));
		    	 pst.setDouble(8, dblTotalRosterTime);
		    	 pst.setInt(9, 0);
		    	 pst.setBoolean(10, false);
		    	 pst.setInt(11, uF.parseToInt(shift_id));
		    	 pst.setInt(12, 1);
		    	 pst.execute();
				 pst.close();
	    	 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	     
	     return SUCCESS;

		
	}
public List<String>  shiftTimeDetails(int id) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		
		List<String> alInner =new ArrayList<String>();
		try {
				con = db.makeConnection(con);
				pst = con.prepareStatement("SELECT * FROM shift_details where shift_id='"+id+"'");
				rs = pst.executeQuery();
				
				while(rs.next()){
					if(!(rs.getString("shift_code").equalsIgnoreCase("ST"))){
						alInner.add(rs.getString("shift_code"));
						alInner.add(rs.getString("_from"));
						alInner.add(rs.getString("_to"));
						alInner.add(rs.getString("break_start"));
						alInner.add(rs.getString("break_end"));
					}
				}
				rs.close();
				pst.close();
		
		} catch (Exception e) {
			e.printStackTrace();
//					
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return alInner;
		
	}
public List<String>  workLocationInfo(int id) {
	
	Connection con = null;
	PreparedStatement pst=null;
	ResultSet rs= null;
	Database db = new Database();
	db.setRequest(request);
	
	
	List<String> wlocationInner =new ArrayList<String>();
	try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM (SELECT * FROM employee_official_details where emp_id='"+id+"' ) aepd  JOIN work_location_info wpd ON aepd.wlocation_id = wpd.wlocation_id ");
			rs = pst.executeQuery();
			
			while(rs.next()){
				
				wlocationInner.add(rs.getString("wlocation_start_time"));
				wlocationInner.add(rs.getString("wlocation_end_time"));
				wlocationInner.add(rs.getString("wlocation_weeklyoff1"));
				wlocationInner.add(rs.getString("wlocation_weeklyofftype1"));
				wlocationInner.add(rs.getString("wlocation_weeklyoff2"));
				wlocationInner.add(rs.getString("wlocation_weeklyofftype2"));
				wlocationInner.add(rs.getString("wlocation_start_time_halfday"));
				wlocationInner.add(rs.getString("wlocation_end_time_halfday"));
			}
			rs.close();
			pst.close();
		request.setAttribute("shiftDetails", wlocationInner);
	} catch (Exception e) {
		e.printStackTrace();
//				
	}finally{
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	return wlocationInner;
	
}

	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;

	}


}
