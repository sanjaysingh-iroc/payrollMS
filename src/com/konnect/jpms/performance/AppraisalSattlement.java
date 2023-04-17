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

public class AppraisalSattlement implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	CommonFunctions CF;
	
	private String id;
	private String empId;
	private String appFreqId;
	
	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";

		request.setAttribute(PAGE, "/jsp/performance/Appraisal.jsp");
		request.setAttribute(TITLE, "Appraisal");
		getOffboardEmployeeList();

		return "success";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

			Map<String, String> hmDesignation = CF.getDesigMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmLevelMap = getLevelMap(con);
			
			request.setAttribute("hmEmpName",hmEmpName);

			String orient=null;
			pst = con
					.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			while (rs.next()) {
				
				orient=rs.getString("oriented_type");

			}
			rs.close();
			pst.close();
			
			List<String> userList=new ArrayList<String>();
			if(strSessionUserType != null && strSessionUserType.equalsIgnoreCase(ADMIN)){
				userList.add("1");
			}else
			if(uF.parseToInt(orient)==90){
				userList.add("7");
				userList.add("3");
				
			}else if(uF.parseToInt(orient)==180){
				userList.add("2");
				userList.add("7");
				userList.add("3");
			}else if(uF.parseToInt(orient)==270){
				userList.add("2");
				userList.add("7");
				userList.add("3");
				
			}else if(uF.parseToInt(orient)==360){
				userList.add("2");
				userList.add("7");
				userList.add("3");
				
			}
			
//			System.out.println("userList "+userList);
			request.setAttribute("userList",userList);

			pst = con
					.prepareStatement("select qb.* from appraisal_question_details aqd,question_bank qb where appraisal_id=? and aqd.question_id=qb.question_bank_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, List<String>> questionMp = new HashMap<String, List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("question_text"));
				innerList.add(rs.getString("option_a"));
				innerList.add(rs.getString("option_b"));
				innerList.add(rs.getString("option_c"));
				innerList.add(rs.getString("option_d"));
				innerList.add(rs.getString("correct_ans"));
				innerList.add(rs.getString("question_type"));

				questionMp.put(rs.getString("question_bank_id"), innerList);

			}
			rs.close();
			pst.close();
			
//			System.out.println("questionMp "+questionMp);
			request.setAttribute("questionMp", questionMp);

			// Map<String,List<List<String>>> alData=new
			// HashMap<String,List<List<String>>>();

			pst = con
					.prepareStatement("select * from appraisal_question_answer where emp_id=? and appraisal_id=? and appraisal_freq_id=?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();

			Map<String, Map<String, List<List<String>>>> alData = new HashMap<String, Map<String, List<List<String>>>>();

			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("emp_id"));
				innerList.add(rs.getString("answer"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("user_id"));
				innerList.add(rs.getString("user_type_id"));

				Map<String, List<List<String>>> innerMap = alData.get(rs
						.getString("user_type_id"));
								
				
				if (innerMap == null)
					innerMap = new HashMap<String, List<List<String>>>();
				List<List<String>> outerList = innerMap.get(rs
						.getString("user_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				innerMap.put(rs.getString("user_id"), outerList);
				alData.put(rs.getString("user_type_id"), innerMap);

			}
			rs.close();
			pst.close();
			
//			System.out.println("alData "+alData);
			request.setAttribute("alData", alData);

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
				hmLevelMap.put( rs.getString("level_id"), rs.getString("level_name") + "[" + rs.getString("level_code") + "]");
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmLevelMap;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}
	
	
}
