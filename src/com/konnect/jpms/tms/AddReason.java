package com.konnect.jpms.tms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Time;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddReason extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSelectedEmpId;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF = null;

	String inOutStatus;// Created By Dattatray Date:06-10-21

	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		
		
		// System.out.println("getStrReason() ====>> " + getStrReason());
		if (getStrReason() != null) {
			insertReason();
			return SUCCESS;
		}

		return LOAD;

	}

	public void insertReason() {
		Connection con = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		try {
			//System.out.println("Date Pluse : "+uF.getGreaterDateTimeException(uF, getStrDate(), getStrStartTime(), getStrEndTime()));
			con = db.makeConnection(con);
			
	//===Created by parvez date: 02-12-2021===
	//===start===
			pst = con.prepareStatement("delete from exception_reason where emp_id=? and _date=? and status=-1");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrDate(), DBDATE));
			pst.execute();
			pst.close();
	//===end===
			
			StringBuilder sbQry = new StringBuilder();
			// Started By Dattatray Date:07-10-21
			sbQry.append("insert into exception_reason (emp_id, given_reason, _date, in_out_type, service_id");
			System.out.println("start : "+getStrStartTime());
			System.out.println("end : "+getStrEndTime());
			if (getStrStartTime() != null && getStrEndTime() !=null) {
				sbQry.append(",in_timestamp,out_timestamp)");
				
				sbQry.append(" values (?,?,?,?, ?,?,?)");
			}else if(getStrEndTime() !=null &&  getStrStartTime() == null) {
				sbQry.append(",out_timestamp)");
				sbQry.append(" values (?,?,?,?, ?,?)");
			}else if(getStrStartTime() != null && getStrEndTime() ==null) {
				sbQry.append(",in_timestamp)");
				sbQry.append(" values (?,?,?,?, ?,?)");
			}
			// Ended By Dattatray Date:07-10-21
//			"insert into exception_reason (emp_id, given_reason, _date, in_out_type, service_id,in_timestamp,out_timestamp) "
//			+ " values (?,?,?,?, ?,?,?) "
			pst = con.prepareStatement(sbQry.toString());// Created By Dattatray Date:06-10-21

			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setString(2, getStrReason());
			pst.setDate(3, uF.getDateFormat(getStrDate(), DBDATE));
			pst.setString(4, getInOutStatus());
			pst.setInt(5, uF.parseToInt(getStrServiceId()));
			// Started By Dattatray Date:06-10-21
			if (getStrStartTime() != null && getStrEndTime() !=null) {
				pst.setTimestamp(6, uF.getTimeStamp(getStrDate() + " " + getStrStartTime(), DBDATE + " " + DBTIME));
				pst.setTimestamp(7, uF.getTimeStamp(getStrDate() + " " + getStrEndTime(), DBDATE + " " + DBTIME));
//				pst.setTimestamp(7, uF.getGreaterDateTimeException(uF, getStrDate(), getStrStartTime(), getStrEndTime()));//Created By Dattatray Date:08-10-21
			}else if(getStrEndTime() !=null && getStrStartTime() == null) {
				pst.setTimestamp(6, uF.getTimeStamp(getStrDate() + " " + getStrEndTime(), DBDATE + " " + DBTIME));
//				pst.setTimestamp(6, uF.getGreaterDateTimeException(uF, getStrDate(), getStrStartTime(), getStrEndTime()));//Created By Dattatray Date:08-10-21
			}else if(getStrStartTime() != null && getStrEndTime() ==null) {
				pst.setTimestamp(6, uF.getTimeStamp(getStrDate() + " " + getStrStartTime(), DBDATE + " " + DBTIME));
			}
			// Ended By Dattatray Date:06-10-21
			pst.execute();
			System.out.println("AR/101--pst ===>> " + pst);
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	String strDate;
	String strEmpId;
	String strServiceId;
	String strReason;
	// Started By Dattatray Date:07-10-21
	String strStartTime;
	String strEndTime;
	// Ended By Dattatray Date:07-10-21

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getStrServiceId() {
		return strServiceId;
	}

	public void setStrServiceId(String strServiceId) {
		this.strServiceId = strServiceId;
	}

	public String getStrReason() {
		return strReason;
	}

	public void setStrReason(String strReason) {
		this.strReason = strReason;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	public String getInOutStatus() {
		return inOutStatus;
	}

	public void setInOutStatus(String inOutStatus) {
		this.inOutStatus = inOutStatus;
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

}