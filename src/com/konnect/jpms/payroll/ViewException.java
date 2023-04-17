package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ViewException extends ActionSupport implements ServletRequestAware, IStatements {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	
	String absent;
	String strEmpId;
	String paycycle;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		  
		UtilityFunctions uF = new UtilityFunctions();
		
		getExceptionCount(uF);
		
		return SUCCESS;
	}


	private void getExceptionCount(UtilityFunctions uF) {


		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			String[] strPayCycleDates = null;
			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
			}
			
			con = db.makeConnection(con);
			int nExceptionCnt = 0;
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("SELECT count(*) as cnt FROM  (Select * from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad, employee_official_details eod " +
					"WHERE eod.emp_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and early_late>0 and approved=-2 and ad.emp_id=? " +
					"ORDER BY in_out_timestamp desc) a JOIN roster_details rd ON a.empl_id=rd.emp_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date ) t  " +
					"WHERE t._date between ? and ? ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrEmpId()));
			pst.setDate(4, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery(); 
			while (rs.next()) {
				nExceptionCnt = rs.getInt("cnt");
			} 
			rs.close();
			pst.close();
			
			request.setAttribute("nExceptionCnt", ""+nExceptionCnt);
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

	public String getAbsent() {
		return absent;
	}

	public void setAbsent(String absent) {
		this.absent = absent;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

}
