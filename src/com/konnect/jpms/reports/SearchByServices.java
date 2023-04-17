package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SearchByServices extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session = null;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(SearchByServices.class);
	
	public String execute() throws Exception {

		session = request.getSession();if(session==null)return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		request.setAttribute(PAGE, PSearchByServices);
		request.setAttribute(TITLE, TViewService);

		
		if (getService() != null) {
			viewServices();
		}

		return loadServices();

	}

	public String loadServices() {

		serviceList = new FillServices(request).fillServices();
		return LOAD;
	}

	public String viewServices() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();

			con = db.makeConnection(con);
			
			
			pst = con.prepareStatement(selectSearchServices);
			pst.setInt(1, uF.parseToInt(getService()));
			if(getStrDate()==null){
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));	
			}else{
				pst.setDate(2, uF.getDateFormat(getStrDate(), CF.getStrReportDateFormat()));
			}
			rs = pst.executeQuery();
			while (rs.next()) {
				alInner = new ArrayList<String>();
				alInner.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(rs.getString("emp_fname") + " " + rs.getString("emp_lname"));
				alInner.add(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
				alInner.add(uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));

				al.add(alInner);
			}
			rs.close();
			pst.close();

			request.setAttribute("reportList", al);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;

	}

	String strDate;
	String service;
	List<FillServices> serviceList;

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

}
