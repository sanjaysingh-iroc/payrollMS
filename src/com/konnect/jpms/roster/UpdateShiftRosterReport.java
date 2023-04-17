package com.konnect.jpms.roster;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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

public class UpdateShiftRosterReport extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements{
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpID;
	CommonFunctions CF = null;
	
	String _to=null;
	String _from=null;
	private static Logger log = Logger.getLogger(UpdateShiftRosterReport.class);
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF=new UtilityFunctions();
		String roster_id = request.getParameter("roster_id");	    
	   
	    String shift_id = request.getParameter("shift_id");
	    if(shift_id.equals("1")){
	    	 _to =workLocationInfo(uF.parseToInt(shift_id)).get(1) ;
		 	 _from = workLocationInfo(uF.parseToInt(shift_id)).get(0);
	    }
	    else{
		     _to =shiftTimeDetails(uF.parseToInt(shift_id)).get(2) ;
		 	 _from = shiftTimeDetails(uF.parseToInt(shift_id)).get(1);
	    }
	    if(shiftTimeDetails(uF.parseToInt(shift_id)).size()!=0 && (shiftTimeDetails(uF.parseToInt(shift_id)).get(5)).equalsIgnoreCase("Custom")){
	    	_to=request.getParameter("_to");
	    	_from=request.getParameter("_from");
	    }
	   
	    String _del = request.getParameter("DEL");
	    String _upd = request.getParameter("UPD");

	    
     
	     Connection con = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			Database db = new Database();
			db.setRequest(request);
			
			
			double dblTotalRosterTime = 0d;
			
	     try {
	    	 
	    	 con = db.makeConnection(con);
	    	 
	    	 if(_upd!=null){
	    		 pst = con.prepareStatement("UPDATE roster_details SET shift_id=?,_from=?,_to=? WHERE roster_id=?");
		    	 pst.setInt(1, uF.parseToInt(shift_id));
		    	 pst.setTime(2, uF.getTimeFormat(_from, CF.getStrReportTimeFormat()));
		    	 pst.setTime(3, uF.getTimeFormat(_to, CF.getStrReportTimeFormat()));
		    	 pst.setInt(4, uF.parseToInt(roster_id));
		    	 pst.execute();	
		    	 pst.close();
	    	 }else if(_del!=null){
	    		 pst = con.prepareStatement(deleteRosterDetails);
	    		 pst.setInt(1, uF.parseToInt(roster_id));
		    	 pst.execute();	
		    	 pst.close();
	    	 }
	    	 
	    	 
	    	 
	    	 
	    	 
	    	 
	    	 
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
						alInner.add(rs.getString("shift_type"));
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
