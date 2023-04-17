package com.konnect.jpms.performance;

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

public class ExistAppraisalReport extends ActionSupport implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strSessionUserTypeId;
	CommonFunctions CF;

	private String userlocation;

	public String getUserlocation() {
		return userlocation;
	}

	public void setUserlocation(String userlocation) {
		this.userlocation = userlocation;
	}

	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strSessionUserTypeId = (String) session.getAttribute(USERTYPEID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";

		request.setAttribute(PAGE, "/jsp/performance/ExistAppraisalReportPopup.jsp");
		request.setAttribute(TITLE, "Exist Appraisal Systems");

		userlocation = getUserLocation();

		getAppraisalReport();

		return LOAD;
	}

	private String getUserLocation() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String location = "";

		con = db.makeConnection(con);
		try {
			pst = con.prepareStatement("select  wlocation_id from employee_official_details eod,user_details ud where eod.emp_id=ud.emp_id and ud.emp_id=?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
			while (rst.next()) {
				location = rst.getString(1);
			}
			rst.close();
			pst.close();
//			System.out.println("location=" + location);
//			System.out.println("WLOCATIONID=" + (String) session.getAttribute(WLOCATIONID));

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return location;
	}

	public void getAppraisalReport() {
		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		con = db.makeConnection(con);
		
		Map<String,String> orientationMp= CF.getOrientationValue(con);
		Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
		Map<String, String> hmLocation = getLcationMap(con);
		
		try {

			Map<String, String> hmFrequency = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmFrequency.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from appraisal_details a, appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id and"
					+" (is_delete is null or is_delete = false)");
			if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(MANAGER)) {
				sbQuery.append(" and supervisor_id like '%," + strSessionEmpId+ ",%'");
			} else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(HRMANAGER)) {
				sbQuery.append(" and hr_ids like '%," + strSessionEmpId+ ",%'");
			} else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(EMPLOYEE)) {
				sbQuery.append(" and peer_ids like '%," + strSessionEmpId+ ",%'");
			} else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(CEO)) {
				sbQuery.append(" and ceo_ids like '%," + strSessionEmpId+ ",%'");
			} else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(HOD)) {
				sbQuery.append(" and hod_ids like '%," + strSessionEmpId+ ",%'");
			}
			
			sbQuery.append(" and my_review_status = 0 order by appraisal_details_id ,appraisal_freq_id desc");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();

			List<List<String>> outerList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
//				innerList.add(rs.getString("appraisal_details_id")+"_"+rs.getString("appraisal_freq_id"));//0
				innerList.add(rs.getString("appraisal_details_id"));//0
				innerList.add(rs.getString("appraisal_name")+" ("+rs.getString("appraisal_freq_name")+")");//1
				innerList.add(orientationMp.get(rs.getString("oriented_type")));//2

				innerList.add(uF.showData(rs.getString("appraisal_type"), ""));//3
				innerList.add(uF.showData(hmFrequency.get(rs.getString("frequency")), ""));//4
				innerList.add(uF.showData(uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT), ""));//5
				innerList.add(uF.showData(uF.getDateFormat(rs.getString("to_date"), DBDATE, DATE_FORMAT), ""));//6
				innerList.add(uF.showData(getAppendData(rs.getString("wlocation_id"), hmLocation), ""));//7
				innerList.add(uF.showData(getAppendData(rs.getString("added_by"), hmEmpName), ""));//8
				innerList.add(uF.showData(uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT), "")); //9
				innerList.add(rs.getString("is_publish"));//10
				innerList.add(rs.getString("appraisal_freq_id"));//11
				innerList.add(rs.getString("appraisal_freq_name"));//12
				innerList.add(rs.getString("is_appraisal_publish"));//13
				innerList.add(rs.getString("freq_publish_expire_status"));//14
				innerList.add(rs.getString("is_appraisal_close"));//15
				innerList.add(rs.getString("close_reason"));//16
				innerList.add(uF.showData(uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT), ""));//17
				innerList.add(uF.showData(uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT), ""));//18
				outerList.add(innerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("outerList", outerList);
			request.setAttribute("orientationMp", orientationMp);
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private Map<String, String> getLcationMap(Connection con) {
		Map<String, String> mplocation = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {
			pst = con.prepareStatement("select * from work_location_info");
			rst = pst.executeQuery();
			while (rst.next()) {
				mplocation.put(rst.getString("wlocation_id"), rst.getString("wlocation_name"));
			}
			rst.close();
			pst.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mplocation;
	}

	private String getAppendData(String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();

		if (strID != null && !strID.equals("")) {

			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(mp.get(temp[i].trim()));
					} else {
						
						sb.append("," + mp.get(temp[i].trim()));
					}
				}
			} else {
				return mp.get(strID);
			}

		} else {
			return null;
		}

		return sb.toString();
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	public Map<String, String> getLevelMap() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new HashMap<String, String>();
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectLevel);
			rs = pst.executeQuery();

			while (rs.next()) {
				hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name") + "[" + rs.getString("level_code") + "]");
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
		return hmLevelMap;
	}
	
	
}
