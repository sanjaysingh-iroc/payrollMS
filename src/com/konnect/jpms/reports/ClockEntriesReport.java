package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class ClockEntriesReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(ClockEntriesReport.class);
	 
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		
		request.setAttribute(PAGE, PReportClockEntriesApproval);
		request.setAttribute(TITLE, TViewClockEntries);

		String q = (String) request.getParameter("q");

		viewClockEntriesReport(q);
		return loadClockEntriesReport();

	}

	public String loadClockEntriesReport() {

		return LOAD;
	}

	public String viewClockEntriesReport(String q) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectClockEntriesRR);
			pst.setInt(1, ((uF.parseToInt(q))==0)?1:uF.parseToInt(q));
			
			rs = pst.executeQuery();
			while (rs.next()) {
				alInner = new ArrayList<String>();

				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				alInner.add(rs.getString("emp_fname") + strEmpMName+" " + rs.getString("emp_lname"));
				alInner.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()));
				alInner.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDayFormat()));
				alInner.add(rs.getString("in_out"));
				alInner.add(uF.removeNull(rs.getString("reason")));
				alInner.add(uF.removeNull(rs.getString("comments")));
				alInner.add((uF.parseToInt(rs.getString("early_late")) > 0) ? Math.abs(rs.getInt("early_late"))+"Late" : Math.abs(rs.getInt("early_late"))+"Early");
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

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}
