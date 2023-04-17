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

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ServicesEmployeeReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String strTimeZone = null;
	HttpSession session = null;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(ServicesEmployeeReport.class);
	
	public String execute() throws Exception {
 
		session = request.getSession();if(session==null)return LOGIN;
		request.setAttribute(PAGE, PReportServicesEmployee);
		request.setAttribute(TITLE, TViewService);
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		
		String strDate = (String) request.getParameter("strDate");

		viewServicesEmployee(strDate);
		return loadServicesEmployee();

	}

	public String loadServicesEmployee() {

		return LOAD;
	}

	public String viewServicesEmployee(String strDate) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
			
		try {

			if(strDate==null){
				strDate = uF.getDateFormat(uF.getCurrentDate(strTimeZone)+"", DBDATE, CF.getStrReportDateFormat());
			}
			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			log.debug(strDate);
			
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectServicesEmployeeRoster);
			pst.setDate(1, uF.getDateFormat(strDate, DATE_FORMAT));
			rs = pst.executeQuery();
			String strOldService = null;
			String strNewService = null;

			
			log.debug("pst===>"+pst);
			
			while (rs.next()) {
				alInner = new ArrayList<String>();

				strNewService = rs.getString("service_name");

				if (strNewService != null && !strNewService.equalsIgnoreCase(strOldService)) {
					alInner.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
					alInner.add(rs.getString("service_name"));
				} else {
					alInner.add("");
					alInner.add("");
				}
				alInner.add(rs.getString("emp_fname") + " " + rs.getString("emp_lname"));
				alInner.add(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
				alInner.add(uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));

				al.add(alInner);
				strOldService = strNewService;
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

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}
