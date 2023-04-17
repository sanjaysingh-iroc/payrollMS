package com.konnect.jpms.task.tax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class InformationDisplay extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	public String execute() throws Exception {
		
		request.setAttribute(PAGE, "/jsp/task/tax/InformationDisplay.jsp");
		request.setAttribute(TITLE, "Information Display");
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();
		boolean isView  = CF.getAccess(session, request, uF);
		/*if(!isView) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		viewInformationDisplay(uF);
		return SUCCESS;
 
	}
	


	private void viewInformationDisplay(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		double snapshot_time=0.0;
		try {
			con = db.makeConnection(con);

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from project_information_display");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			Map<String, String> hmInfoDisplay = new HashMap<String, String>();
			while(rs.next()) {
				hmInfoDisplay.put("ONLY_TEAM", uF.showYesNo(rs.getString("only_team")));
				hmInfoDisplay.put("IS_COST", uF.showYesNo(rs.getString("is_cost")));
				hmInfoDisplay.put("IS_RATE", uF.showYesNo(rs.getString("is_rate")));
				hmInfoDisplay.put("IS_ATTEND_FROM_ATTEND_DETAILS", uF.showYesNo(rs.getString("attend_from_attend_detail")));
				hmInfoDisplay.put("IS_ATTEND_FROM_TIMESHEET_DETAILS", uF.showYesNo(rs.getString("attend_from_timesheet_detail")));
				 snapshot_time=rs.getDouble("snapshot_time");
				 snapshot_time=(snapshot_time/1000)/60;
				 int time=(int) snapshot_time;
				 hmInfoDisplay.put("SNAPSHOT_TIME",""+time+" minutes");
			}
			rs.close();
			pst.close();
			request.setAttribute("hmInfoDisplay", hmInfoDisplay);
			
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

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}

}
