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

public class MyAppraisal implements ServletRequestAware, IStatements {

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

		request.setAttribute(PAGE, "/jsp/performance/MyAppraisal.jsp");
		request.setAttribute(TITLE, "Appraisal Report");
		getMyappraisalDetails();

		return "success";
	}

	public void getMyappraisalDetails() {
		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);

		try {
			
			Map<String,String> hmFrequency=new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			while(rs.next()){
				hmFrequency.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));				
			}
			rs.close();
			pst.close();

			Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmLevelMap = getLevelMap(con);
			List<List<String>> outerList = new ArrayList<List<String>>();
			if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(EMPLOYEE)) {
				pst = con.prepareStatement("select * from appraisal_details where self_ids like '%,"+ strSessionEmpId + ",%' order by appraisal_details_id desc");
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("appraisal_details_id"));
					innerList.add(rs.getString("appraisal_name"));
					innerList.add(rs.getString("oriented_type"));
					innerList.add(uF.showData(hmEmpName.get(rs.getString("self_ids")), ""));
					innerList.add(uF.showData(hmLevelMap.get(rs.getString("level_id")), ""));
					innerList.add(uF.showData(hmDesignation.get(rs.getString("desig_id")), ""));
					innerList.add(uF.showData(hmGradeMap.get(rs.getString("grade_id")), ""));
					innerList.add(uF.showData(hmFrequency.get(rs.getString("frequency")),""));  
					innerList.add(rs.getString("from_date"));
					innerList.add(rs.getString("to_date"));
					outerList.add(innerList);
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("outerList", outerList);
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

	public Map<String, String> getLevelMap(Connection con) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new HashMap<String, String>();
		try {
			pst = con.prepareStatement(selectLevel);
			rs = pst.executeQuery();

			while (rs.next()) {
				hmLevelMap.put(rs.getString("level_id"),rs.getString("level_name") + "["+ rs.getString("level_code") + "]");
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmLevelMap;
	}
}
