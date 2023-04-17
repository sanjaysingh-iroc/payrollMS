package com.konnect.jpms.offboarding;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

public class OffboardEmployee implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	CommonFunctions CF;

	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";

		request.setAttribute(PAGE, "/jsp/offboarding/OffboardEmployee.jsp");
		request.setAttribute(TITLE, TResignationEntry);
		getOffboardEmployeeList();

		return "success";
	}

	public void getOffboardEmployeeList() {
		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
			Map<String, Map<String, String>> hmWLocaMap = CF.getWorkLocationMap(con);
			Map<String,String> hmEmpWLocation = CF.getEmpWlocationMap(con);
			Map<String, String> hmDepMap = CF.getEmpDepartmentMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);

			pst = con
					.prepareStatement("select off_board_id,emp_id,off_board_type,entry_date,approved_1_by,notice_days from emp_off_board eob where approved_1=? and approved_2=?");
			pst.setInt(1, 1);
			pst.setInt(2, 1);
			rs = pst.executeQuery();
			List<String> employeeList = new ArrayList<String>();
			Map<String, Map<String, String>> empDetailsMp = new HashMap<String, Map<String, String>>();

			while (rs.next()) {
				Map<String, String> empMap = new HashMap<String, String>();
				employeeList.add(rs.getString("emp_id"));
				String wLocation=hmEmpWLocation.get(rs.getString("emp_id"));
				Map<String,String> wLocationDetails=hmWLocaMap.get(wLocation);
				if(wLocationDetails==null)wLocationDetails=new HashMap<String,String>();
				
				empMap.put("ENTRY_DATE", uF.getDateFormat(
						rs.getString("entry_date"), DBTIMESTAMP,
						CF.getStrReportDateFormat()));
				empMap.put("OFF_BOARD_TYPE", rs.getString("off_board_type"));
				empMap.put("EMP_ID", rs.getString("emp_id"));
				empMap.put("OFF_BOARD_ID", rs.getString("off_board_id"));

				empMap.put("ACCEPTED_BY", uF.showData(
						hmEmpName.get(rs.getString("approved_1_by")), ""));

				empMap.put("EMP_FNAME",
						uF.showData(hmEmpName.get(rs.getString("emp_id")), ""));

				empMap.put("DEPART_NAME",
						uF.showData(hmDepMap.get(rs.getString("emp_id")), ""));
				empMap.put("WLOCATION_NAME", uF.showData(wLocationDetails.get("WL_NAME"), ""));
				empMap.put("NOTICE_DAYS",
						uF.showData(rs.getString("notice_days"), ""));

				empMap.put("DESIGNATION_NAME", uF.showData(
						hmDesignation.get(rs.getString("emp_id")), ""));

				empDetailsMp.put(rs.getString("emp_id"), empMap);
			}
			rs.close();
			pst.close();

			request.setAttribute("empDetailsMp", empDetailsMp);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

}
