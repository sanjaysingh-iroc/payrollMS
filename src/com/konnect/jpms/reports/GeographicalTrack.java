package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import java.util.*;

public class GeographicalTrack extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;

	String strSessionEmpId = null;
	UtilityFunctions uF = new UtilityFunctions();

	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
		return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		request.setAttribute(TITLE, "People's Dashboard");
		request.setAttribute(PAGE, "/jsp/employee/geographicalTrack.jsp");
		getWlocationEmployeeCount();
		getAttendancePunchdata();
		return LOAD;

	}

	public void getAttendancePunchdata()
	{
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst  = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, List<String>> hmLongLatDetails = new HashMap<String, List<String>>();

		
		try 
		{
			con = db.makeConnection(con);
			Map<String, String> Empname = CF.getEmpNameMap(con, null, null);
			pst = con.prepareStatement("select * from attendance_punch_in_out_details where  punch_date = '2019-08-09' ");
			rs = pst.executeQuery();
			while (rs.next()) {
				List listLongLatDetails = new ArrayList<String>();
				listLongLatDetails.add(rs.getString("longitude"));
				listLongLatDetails.add(rs.getString("latitude"));
				listLongLatDetails.add(Empname.get(rs.getString("emp_id")));
				listLongLatDetails.add(rs.getString("punch_mode"));
				hmLongLatDetails.put(rs.getString("attendance_punch_in_out_id"), listLongLatDetails);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmLongLatDetails", hmLongLatDetails);
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
			
	}
	public void getWlocationEmployeeCount() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst  = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmWLocOrgName = CF.getOrgNameWLocationIdwise(con);
			Map<String, Map<String, String>> hmWorkLocationMap = CF.getWorkLocationMap(con);

//			pst = con.prepareStatement("select count(eod.emp_id) as count, wlocation_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and epd.is_alive=true and wlocation_id > 0 group by wlocation_id");
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(eod.emp_id) as count, wlocation_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id and epd.is_alive=true and wlocation_id > 0 ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" group by wlocation_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			int wLocationCount = 0;
			Map<String, String> hmWLocationEmployeeCount = new HashMap<String, String>();
			while (rs.next()) {
				wLocationCount++;
				hmWLocationEmployeeCount.put(rs.getString("wlocation_id"), uF.formatIntoComma(rs.getDouble("count")) );
			}
			rs.close();
			pst.close();
			request.setAttribute("wLocationCount", ""+wLocationCount);
			request.setAttribute("hmWLocationEmployeeCount", hmWLocationEmployeeCount);
			request.setAttribute("hmWLocOrgName", hmWLocOrgName);
			request.setAttribute("hmWorkLocationMap", hmWorkLocationMap);

			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
