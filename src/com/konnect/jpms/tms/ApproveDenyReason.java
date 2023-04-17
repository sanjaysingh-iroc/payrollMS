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

public class ApproveDenyReason extends ActionSupport implements ServletRequestAware, IStatements {
   
	/**
	 *
	 */ 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSelectedEmpId;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF = null; 
	
	String EMPID;
	String DT;
	String SID;
	String S;
	String AST;
	String AET;
	String divid;
	String exceptionType;
	
	String strStartTime;
	String strEndTime;
	String exceptionMode;
	String employeeReason;//Created By Dattatray Date : 12-11-21
	
	private static Logger log = Logger.getLogger(AddReason.class);
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
//		System.out.println("getAST() ===>> " + getAST());
//		System.out.println("getAET() ===>> " + getAET());
		//Created By Dattatray Date:08-12-21 Note : Committed code
		if(getAST()!=null && !getAST().equals("")) {
			setStrStartTime(getAST());
		} else if(getAET()!=null && !getAET().equals("")) {
			setStrEndTime(getAET());
		} else {
			loadRosterData(getSID(), getEMPID(), getDT());
//			System.out.println("employeeReason : "+getEmployeeReason());
		}
		return LOAD;

	}	
	
	
	public void loadRosterData(String strServiceId, String strEmpID, String strDate) {
		
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);	
				
			//Started By Dattatray Date:13-10-21
			pst = con.prepareStatement("select * from exception_reason where emp_id =? and _date=? and service_id=?");
			pst.setInt(1, uF.parseToInt(strEmpID));
			pst.setDate(2, uF.getDateFormat(strDate, DBDATE));
			pst.setInt(3, uF.parseToInt(strServiceId));
			System.out.println("pst1111======>"+pst);
			rs = pst.executeQuery();
			boolean flagIn=false;
			boolean flagOut=false;
			while(rs.next()) {
				//Start Dattatray Date : 10-11-21
				if (rs.getString("in_timestamp") !=null && !rs.getString("in_timestamp").isEmpty() ) {
					setStrStartTime(uF.getDateFormat(rs.getString("in_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
					flagIn = true;
				} if (rs.getString("out_timestamp") !=null && !rs.getString("out_timestamp").isEmpty()) {
					setStrEndTime(uF.getDateFormat(rs.getString("out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
					flagOut = true;
				}//End Dattatray Date : 10-11-21
				
				setEmployeeReason(rs.getString("given_reason"));//Created By Dattatray Date : 12-11-21
				
			}
			rs.close();
			pst.close();
			//Ended By Dattatray Date:13-10-21
			
			//Created By Dattatray Date:13-10-21 Note : condition checked
			//Created By Dattatray Date:08-12-21 Note : condition checked
			if (!flagIn && !flagOut) {
			pst = con.prepareStatement("SELECT * FROM roster_details where _date = ? and service_id =? and emp_id =?");
			pst.setDate(1, uF.getDateFormat(strDate, DBDATE));
			pst.setInt(2, uF.parseToInt(strServiceId));
			pst.setInt(3, uF.parseToInt(strEmpID));
			rs = pst.executeQuery();
			if(rs.next()) {
				if(getStrStartTime()==null || (getStrStartTime()!=null && getStrStartTime().length()==0)) {
					setStrStartTime(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
				}
				if(getStrEndTime()==null || (getStrEndTime()!=null && getStrEndTime().length()==0)) {
					setStrEndTime(uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));
				}
			}
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
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getEMPID() {
		return EMPID;
	}

	public void setEMPID(String eMPID) {
		EMPID = eMPID;
	}

	public String getDT() {
		return DT;
	}

	public void setDT(String dT) {
		DT = dT;
	}

	public String getSID() {
		return SID;
	}

	public void setSID(String sID) {
		SID = sID;
	}

	public String getS() {
		return S;
	}

	public void setS(String s) {
		S = s;
	}

	public String getAST() {
		return AST;
	}

	public void setAST(String aST) {
		AST = aST;
	}

	public String getAET() {
		return AET;
	}

	public void setAET(String aET) {
		AET = aET;
	}

	public String getDivid() {
		return divid;
	}

	public void setDivid(String divid) {
		this.divid = divid;
	}

	public String getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
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

	public String getExceptionMode() {
		return exceptionMode;
	}

	public void setExceptionMode(String exceptionMode) {
		this.exceptionMode = exceptionMode;
	}


	public String getEmployeeReason() {
		return employeeReason;
	}


	public void setEmployeeReason(String employeeReason) {
		this.employeeReason = employeeReason;
	}

}