package com.konnect.jpms.tms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class GetExceptionReason extends ActionSupport implements ServletRequestAware, IStatements {
   
	/**
	 *
	 */ 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSelectedEmpId;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF = null;
	
	
	
	private static Logger log = Logger.getLogger(GetExceptionReason.class);
	
	public String execute() throws Exception {

		log.debug("ClockEntries: execute()");
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		getReason();
		
		return LOAD;

	}	
	
	private void getApprovalReason() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			UtilityFunctions uF = new UtilityFunctions();
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from attendance_details where in_out =? and emp_id = ? and service_id=? and to_date(in_out_timestamp::text, 'YYYY-MM-DD') = ?");
			pst.setString(1, getMode());
			pst.setInt(2, uF.parseToInt(getEmp_id()));
			pst.setInt(3, uF.parseToInt(getService_id()));
			pst.setDate(4, uF.getDateFormat(get_date(), CF.getStrReportDateFormat()));
			rs = pst.executeQuery();
			String strReason=null;
			
			
			while(rs.next()){
				strReason = rs.getString("reason");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("strReason", strReason);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	String strAtten_id;
	
	public void getReason(){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			UtilityFunctions uF = new UtilityFunctions();
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from attendance_details where in_out =? and emp_id = ? and service_id=? and to_date(in_out_timestamp::text, 'YYYY-MM-DD') = ?");
			pst.setString(1, getMode());
			pst.setInt(2, uF.parseToInt(getEmp_id()));
			pst.setInt(3, uF.parseToInt(getService_id()));
			pst.setDate(4, uF.getDateFormat(get_date(), CF.getStrReportDateFormat()));
			rs = pst.executeQuery();
			String strReason=null;
			String strManagerReason=null;
			while(rs.next()){
				strReason = rs.getString("reason");
				strManagerReason= rs.getString("approval_reason");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("strReason", strReason);
			request.setAttribute("strManagerReason", strManagerReason);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	String mode;
	String emp_id;
	String service_id;
	String _date;
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getService_id() {
		return service_id;
	}

	public void setService_id(String service_id) {
		this.service_id = service_id;
	}

	public String get_date() {
		return _date;
	}

	public void set_date(String _date) {
		this._date = _date;
	}

	

}
