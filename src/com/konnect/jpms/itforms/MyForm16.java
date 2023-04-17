package com.konnect.jpms.itforms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MyForm16 extends ActionSupport implements ServletRequestAware, IStatements { 

	/**
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSesionEmpId = null;
	String strUserType = null;
	       
	CommonFunctions CF = null; 
	
	String alertStatus;
	String alert_type;
	
	String alertID;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/itforms/MyForm16.jsp");
		request.setAttribute(TITLE, "Form 16");
		
		strSesionEmpId =(String) session.getAttribute(EMPID);
		strUserType =(String) session.getAttribute(USERTYPE);
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		viewMyForm16(uF);
		
		return loadMyForm16(uF);
	}
	
	private void viewMyForm16(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
			
			pst = con.prepareStatement("select * from form16_documents where emp_id=? order by financial_year_start desc");
			pst.setInt(1, uF.parseToInt(strSesionEmpId));
			rs = pst.executeQuery();
			List<Map<String, String>> alForm16 = new ArrayList<Map<String,String>>();
			while(rs.next()){
				
				Map<String, String> hmEmpForm16 = new HashMap<String, String>();
				hmEmpForm16.put("FORM16_DOCUMENT_ID", rs.getString("form16_document_id"));
				hmEmpForm16.put("EMP_ID", rs.getString("emp_id"));
				hmEmpForm16.put("FINANCIAL_YEAR_START", uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, CF.getStrReportDateFormat()));
				hmEmpForm16.put("FINANCIAL_YEAR_END", uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, CF.getStrReportDateFormat()));
				hmEmpForm16.put("FORM16_NAME", rs.getString("form16_name"));
				hmEmpForm16.put("APPROVED_BY", uF.showData(hmEmpCodeName.get(rs.getString("approved_by")), ""));
				hmEmpForm16.put("APPROVED_DATE", uF.getDateFormat(rs.getString("approved_date"), DBDATE, CF.getStrReportDateFormat()));
				
				alForm16.add(hmEmpForm16);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alForm16", alForm16);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadMyForm16(UtilityFunctions uF){
		return LOAD;
	}
	

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}
	
}
