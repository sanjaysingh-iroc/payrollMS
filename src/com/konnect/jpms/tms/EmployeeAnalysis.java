package com.konnect.jpms.tms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeAnalysis extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpID;
	CommonFunctions CF = null;
	
	
	public String execute() throws Exception {

		session = request.getSession();if(session==null)return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		
		request.setAttribute(PAGE, PEmployeeAnalysis);
		request.setAttribute(TITLE, TEmployeeAnalysis);
		

		String date = request.getParameter("DATE");
		String strDate = request.getParameter("strDate");

		if (getStrEmpId() != null && getStrEmpId().length > 0) {
			updateClockEntries(strDate);
		}
		viewEmployeeAnalysis(date, strDate);
		return loadEmployeeAnalysis();
	}

	public String loadEmployeeAnalysis() {
		return LOAD;
	}

	public String viewEmployeeAnalysis(String date, String strDate) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		StringBuilder sb = new StringBuilder();
		List _alDate = new ArrayList();
		List _alDateTemp = new ArrayList();
		Map hmOuter = new HashMap();
		
		try {

			con = db.makeConnection(con);
//			
//			pst = con.prepareStatement(selectMaxPayrollDate);
//			rs = pst.executeQuery();
//			java.sql.Date dt = null;
//			while(rs.next()){
//				dt = rs.getDate("_date");
//			}
//			 
//			long lnDate = dt.getTime() + 5 * 24 * 3600 * 1000;
//			
//			
//			
			
			
			pst = con.prepareStatement(selectDataForEmployeeAnalysis_Emp);
			rs = pst.executeQuery();

			int i = 0;

			String strNewEmpId = null;

			
			Map hmInner = new HashMap();
			double dblTotal = 0.0;
			while (rs.next()) {

				strNewEmpId = rs.getString("empl_id");

				hmInner = (Map)hmOuter.get(strNewEmpId);
				if(hmInner==null){
					hmInner = new HashMap();
				}
				
				if (!_alDateTemp.contains(rs.getString("_date"))) {
					_alDateTemp.add(rs.getString("_date"));
					
					i++;
					if(i==1){
						sb.append(rs.getString("_date"));
					}
					if(i==3){
						sb.append("-"+rs.getString("_date"));
						sb = new StringBuilder();
					}
					
				}
				
				hmInner.put(rs.getString("_date"), dblTotal+"");
				hmOuter.put(strNewEmpId, hmInner);
				


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
		request.setAttribute("_alDate", _alDate);
		request.setAttribute("hmOuter", hmOuter);

		return SUCCESS;

	}

	public String updateClockEntries(String strDate) {

		Connection con = null;
		PreparedStatement pst = null, pst1 = null, pst2 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			if (strDate == null) {
				strDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, CF.getStrReportDateFormat());
			}

			con = db.makeConnection(con);
			pst = con.prepareStatement(updateClockEntries1);
			pst1 = con.prepareStatement(updateClockEntries1);
			pst2 = con.prepareStatement(insertClockEntries1);

			for (int i = 0; getStrEmpId() != null && i < getStrEmpId().length; i++) {
				pst.setTimestamp(1, uF.getTimeStamp(strDate + getStrEmpIN()[i], CF.getStrReportDateFormat() + CF.getStrReportTimeFormat()));
				pst.setInt(2, uF.parseToInt(getStrEmpId()[i]));
				pst.setString(3, "IN");
				pst.setDate(4, uF.getDateFormat(strDate, CF.getStrReportDateFormat()));

				if (pst.executeUpdate() == 0) {
					pst2.setInt(1, uF.parseToInt(getStrEmpId()[i]));
					pst2.setTimestamp(2, uF.getTimeStamp(strDate + getStrEmpIN()[i], CF.getStrReportDateFormat() + CF.getStrReportTimeFormat()));
					pst2.setString(3, "IN");
					pst2.execute();
					pst2.close();
				}
				pst.close();

				pst1.setTimestamp(1, uF.getTimeStamp(strDate + getStrEmpOUT()[i], CF.getStrReportDateFormat() + CF.getStrReportTimeFormat()));
				pst1.setInt(2, uF.parseToInt(getStrEmpId()[i]));
				pst1.setString(3, "OUT");
				pst1.setDate(4, uF.getDateFormat(strDate, CF.getStrReportDateFormat()));
				if (pst1.executeUpdate() == 0) {
					pst2.setInt(1, uF.parseToInt(getStrEmpId()[i]));
					pst2.setTimestamp(2, uF.getTimeStamp(strDate + getStrEmpOUT()[i], CF.getStrReportDateFormat() + CF.getStrReportTimeFormat()));
					pst2.setString(3, "OUT");
					pst2.execute();
					pst2.close();
				}
				pst1.close();

			}

		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst2);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}

		return SUCCESS;

	}

	String[] strEmpId;
	String[] strEmpIN;
	String[] strEmpOUT;
	String strDate;

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String[] getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String[] strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String[] getStrEmpIN() {
		return strEmpIN;
	}

	public void setStrEmpIN(String[] strEmpIN) {
		this.strEmpIN = strEmpIN;
	}

	public String[] getStrEmpOUT() {
		return strEmpOUT;
	}

	public void setStrEmpOUT(String[] strEmpOUT) {
		this.strEmpOUT = strEmpOUT;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

}
