package com.konnect.jpms.reports.cubes;

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
import com.opensymphony.xwork2.ActionSupport;

public class CubeReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(CubeReport.class);
	HttpSession session;
	CommonFunctions CF;

	public String execute() throws Exception {

		request.setAttribute(PAGE, PCubeReportDefault);
		request.setAttribute(TITLE, TReportAnalyser);

		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return SUCCESS;
		
		viewCubeReport();
		return loadCubeReport();

	}

	public String loadCubeReport() {

		return LOAD;
	}

	public void viewCubeReport() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);

			pst = con.prepareStatement(selectLeaveTypeF);
			rs = pst.executeQuery();

			List<String> alLeaveType = new ArrayList<String>();

			String strEmpIdNew = null;

			while (rs.next()) {
				alLeaveType.add(rs.getString("leave_type_id"));
				alLeaveType.add(rs.getString("leave_type_name"));
			}
			rs.close();
			pst.close();

			request.setAttribute("alLeaveType", alLeaveType);
			
			
			
			pst = con.prepareStatement(selectWLocation);
			rs = pst.executeQuery();
			List<String> alWLocation = new ArrayList<String>();
			while (rs.next()) {
				alWLocation.add(rs.getString("wlocation_id"));
				alWLocation.add(rs.getString("wlocation_name"));
			}
			rs.close();
			pst.close();

			request.setAttribute("alWLocation", alWLocation);
			
			
			
			pst = con.prepareStatement(selectLevel);
			rs = pst.executeQuery();
			
			List<String> alLevels = new ArrayList<String>();
			while (rs.next()) {
				alLevels.add(rs.getString("level_id"));
				alLevels.add(rs.getString("level_code"));
			}
			rs.close();
			pst.close();
			request.setAttribute("alLevels", alLevels);
			
			pst = con.prepareStatement(selectSalaryDetails1);
			rs = pst.executeQuery();
			List<String> alSalaryHeads = new ArrayList<String>();
			while (rs.next()) {
				alSalaryHeads.add(rs.getString("salary_head_id"));
				alSalaryHeads.add(rs.getString("salary_head_name"));
			}
			rs.close();
			pst.close();

			request.setAttribute("alSalaryHeads", alSalaryHeads);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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

}
