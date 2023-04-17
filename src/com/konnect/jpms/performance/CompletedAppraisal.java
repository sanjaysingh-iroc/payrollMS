package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class CompletedAppraisal implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	CommonFunctions CF;
	List<FillGrade> gradeList;
	List<FillWLocation> workList;
	List<FillDepartment> departmentList;
	private String strWlocation;
	private String strDepart;
	private String empGrade;

	public List<FillWLocation> getWorkList() {
		return workList;
	}

	public void setWorkList(List<FillWLocation> workList) {
		this.workList = workList;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	private String type;

	// String id;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		gradeList = new FillGrade(request).fillGrade();
		workList = new FillWLocation(request).fillWLocation();
		departmentList = new FillDepartment(request).fillDepartment();
		request.setAttribute(PAGE, "/jsp/performance/CompletedAppraisal.jsp");
		request.setAttribute(TITLE, "Completed Appraisal");
		getAppraisalDetails();
		getOrientationMember();
		return "success";
	}

	
	public List<String> getFilterEmp(Connection con) {
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> list = null;
		try {
			StringBuilder sb = new StringBuilder();

			if (uF.parseToInt(strWlocation) != 0) {
				pst = con.prepareStatement("select emp_id from employee_official_details where wlocation_id=?");
				pst.setInt(1, uF.parseToInt(strWlocation));
			} else {
				pst = con.prepareStatement("select emp_id from employee_official_details ");
			}
			rs = pst.executeQuery();
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					sb.append(rs.getString("emp_id"));
				} else {
					sb.append("," + rs.getString("emp_id"));
				}
				i++;

			}
			rs.close();
			pst.close();
			
			String emp = " where emp_id=0";
			if (sb.length() > 0) {
				emp = " where emp_id in(" + sb.toString() + ")";
			}

			sb = new StringBuilder();
			if (uF.parseToInt(strDepart) != 0) {
				pst = con.prepareStatement("select emp_id from employee_official_details " + emp + " and depart_id=?");
				pst.setInt(1, uF.parseToInt(strDepart));
			} else {
				pst = con.prepareStatement("select emp_id from employee_official_details " + emp);
			}

			rs = pst.executeQuery();
			i = 0;
			while (rs.next()) {
				if (i == 0) {
					sb.append(rs.getString("emp_id"));
				} else {
					sb.append("," + rs.getString("emp_id"));
				}

				i++;
			}
			rs.close();
			pst.close();
			
			emp = " where emp_id=0";
			if (sb.length() > 0) {
				emp = " where emp_id in(" + sb.toString() + ")";
			}

			sb = new StringBuilder();
			if (uF.parseToInt(empGrade) != 0) {
				pst = con.prepareStatement("select emp_id from employee_official_details " + emp + " and grade_id=?");
				pst.setInt(1, uF.parseToInt(empGrade));
			} else {
				pst = con.prepareStatement("select emp_id from employee_official_details " + emp);
			}

			rs = pst.executeQuery();
			i = 0;
			while (rs.next()) {
				if (i == 0) {
					sb.append(rs.getString("emp_id"));
				} else {
					sb.append("," + rs.getString("emp_id"));
				}
				i++;

			}
			rs.close();
			pst.close();
			
			list = Arrays.asList(sb.toString().split(","));

		} catch (Exception e) {

		}
		return list;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public String getStrWlocation() {
		return strWlocation;
	}

	public void setStrWlocation(String strWlocation) {
		this.strWlocation = strWlocation;
	}

	public String getStrDepart() {
		return strDepart;
	}

	public void setStrDepart(String strDepart) {
		this.strDepart = strDepart;
	}

	public String getEmpGrade() {
		return empGrade;
	}

	public void setEmpGrade(String empGrade) {
		this.empGrade = empGrade;
	}

	private void getAppraisalDetails() {
		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		boolean flag=false;

		con = db.makeConnection(con);
		List<String> list = getFilterEmp(con);
		try {
			Map<String, String> hmDesignation = CF.getDesigMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmLevelMap = getLevelMap(con);
			Map<String, String> orientationMp = getOrientationValue(con);
			Map<String, List<String>> hmFinalisedAppraisals = getFinalisedAppraisals(con);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			Map<String, String> hmFreq = new HashMap<String, String>();
			while (rs.next()) {
				hmFreq.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_details a, appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id"
					+" (is_delete = false or is_delete is null)");
			// pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();

			Map<String, Map<String, String>> appraisalDetails = new HashMap<String, Map<String, String>>();
			List<String> appraisalIdList = new ArrayList<String>();

			int k = 0;
			while (rs.next()) {
				Map<String, String> appraisalMp = new HashMap<String, String>();
				appraisalIdList.add(rs.getString("appraisal_details_id")+"_"+rs.getString("appraisal_freq_id"));
				appraisalMp.put("ID", rs.getString("appraisal_details_id"));
				appraisalMp.put("APPRAISAL", rs.getString("appraisal_name"));
				appraisalMp.put("ORIENT", orientationMp.get(rs.getString("oriented_type")));
				appraisalMp.put("EMPLOYEE", uF.showData(getAppendData(rs.getString("self_ids"), hmEmpName), ""));
				appraisalMp.put("LEVEL", uF.showData(hmLevelMap.get(rs.getString("level_id")), ""));
				appraisalMp.put("DESIG", hmDesignation.get(rs.getString("desig_id")));
				appraisalMp.put("GRADE", hmGradeMap.get(rs.getString("grade_id")));
				appraisalMp.put("WLOCATION", rs.getString("wlocation_id"));
				appraisalMp.put("PEER", rs.getString("peer_ids"));
				appraisalMp.put("SELFID", rs.getString("self_ids"));
				appraisalMp.put("SUPERVISORID", rs.getString("supervisor_id"));

				appraisalMp.put("FREQUENCY", hmFreq.get(rs.getString("frequency")));
				appraisalMp.put("FROM", uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalMp.put("TO", uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalMp.put("USERTYPE_MEMBER", rs.getString("usertype_member"));
				appraisalMp.put("APP_FREQ_ID", rs.getString("appraisal_freq_id"));
				appraisalMp.put("APP_FREQ_NAME", rs.getString("appraisal_freq_name"));
				appraisalMp.put("IS_APP_PUBLISH", rs.getString("is_appraisal_publish"));
				appraisalMp.put("IS_APP_PUBLISH_EXPIRE", rs.getString("freq_publish_expire_status"));
				appraisalMp.put("IS_APP_CLOSE", rs.getString("is_appraisal_close"));
				appraisalMp.put("CLOSE_REASON", rs.getString("close_reason"));
				appraisalMp.put("APP_START_DATE", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalMp.put("APP_END_DATE", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalDetails.put(rs.getString("appraisal_details_id")+"_"+rs.getString("appraisal_freq_id"), appraisalMp);

			}
			rs.close();
			pst.close();
			
			request.setAttribute("appraisalIdList", appraisalIdList);
			request.setAttribute("appraisalDetails", appraisalDetails);

			Map<String, String> hmUserTypeID = new HashMap<String, String>();
			pst = con.prepareStatement("select user_type_id,user_type from user_type");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmUserTypeID.put(rs.getString("user_type_id"), rs.getString("user_type"));
			}
			rs.close();
			pst.close();

			Map hmCompletedAppraisals = new HashMap();
			for (int i = 0; i < appraisalIdList.size(); i++) {
				Map<String, String> appraisalMp = appraisalDetails.get(appraisalIdList.get(i));
				List alFinalisedAppraisals = (List)hmFinalisedAppraisals.get(appraisalMp.get("ID"));
				String self = appraisalMp.get("SELFID");
				List<String> memberList = Arrays.asList(appraisalMp.get("USERTYPE_MEMBER").split(","));

				Map<String, String> hmchecksuper = new HashMap<String, String>();

				if (memberList.contains("2")) {
					pst = con.prepareStatement("select supervisor_emp_id,emp_id from employee_official_details where supervisor_emp_id!=0  and emp_id in(" + self + ")");
					rs = pst.executeQuery();
					while (rs.next()) {

						hmchecksuper.put(rs.getString("emp_id"), rs.getString("supervisor_emp_id"));
					}
					rs.close();
					pst.close();
				}

				Map<String, String> hmcheckHr = new HashMap<String, String>();
				if (memberList.contains("7")) {
					Map<String, String> hmcheckHr1 = new HashMap<String, String>();
					pst = con.prepareStatement("select wlocation_id,emp_id from employee_official_details  where emp_id in(" + self + ")");
					rs = pst.executeQuery();
					while (rs.next()) {
						hmcheckHr1.put(rs.getString("emp_id"), rs.getString("wlocation_id"));
					}
					rs.close();
					pst.close();

					Map<String, String> hmHr = new HashMap<String, String>();
					pst = con
							.prepareStatement("select eod.emp_id,eod.wlocation_id from employee_official_details eod,user_details ud where ud.emp_id=eod.emp_id and ud.usertype_id=7 ");
					rs = pst.executeQuery();
					while (rs.next()) {
						hmHr.put(rs.getString("wlocation_id"), rs.getString("emp_id"));
					}
					rs.close();
					pst.close();

					Iterator<String> it = hmcheckHr1.keySet().iterator();

					while (it.hasNext()) {
						String emp_id = it.next();
						String hr_id = hmHr.get(hmcheckHr1.get(emp_id));

						if (hr_id != null) {
							hmcheckHr.put(emp_id, hr_id);
						}
					}

				}

				Map<String, String> hmcheckPeer = new HashMap<String, String>();
				if (memberList.contains("4")) {
					Map<String, String> mp = new HashMap<String, String>();
					pst = con.prepareStatement("select wlocation_id,grade_id,emp_id from employee_official_details where emp_id in(" + self + ")");
					rs = pst.executeQuery();
					while (rs.next()) {

						mp.put(rs.getString("emp_id"), rs.getString("wlocation_id") + "grade" + rs.getString("grade_id"));
					}
					rs.close();
					pst.close();

					Map<String, String> mp1 = new HashMap<String, String>();

					pst = con.prepareStatement("select count(*)as count,wlocation_id,grade_id from employee_official_details group by wlocation_id,grade_id order by wlocation_id");
					rs = pst.executeQuery();
					while (rs.next()) {

						mp1.put(rs.getString("wlocation_id") + "grade" + rs.getString("wlocation_id"), rs.getString("count"));
					}
					rs.close();
					pst.close();
					
					Iterator<String> it = mp.keySet().iterator();

					while (it.hasNext()) {
						String emp_id = it.next();
						String count = mp1.get(mp.get(emp_id));
						hmcheckPeer.put(emp_id, uF.parseToInt(count) + "");
					}

				}

				List<List<String>> outerList = new ArrayList<List<String>>();
				pst = con.prepareStatement("select count(*)as count,emp_id,appraisal_id from (select emp_id,appraisal_id,user_type_id from appraisal_question_answer "
						+ "where appraisal_id=? and appraisal_freq_id= ? group by emp_id,user_type_id,appraisal_id)as a group by emp_id,appraisal_id");
				pst.setInt(1, uF.parseToInt(appraisalMp.get("ID")));
				pst.setInt(2, uF.parseToInt(appraisalMp.get("APP_FREQ_ID")));
//				System.out.println("pst===>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					int cnt = 1;
					if (memberList.contains("7")) {
						if (hmcheckHr.get(rs.getString("emp_id")) != null) {
							cnt++;
						}
					}
					if (memberList.contains("2")) {
						if (hmchecksuper.get(rs.getString("emp_id")) != null) {
							cnt++;

						}
					}
					if (memberList.contains("4")) {
						cnt += uF.parseToInt(hmcheckPeer.get(rs.getString("emp_id")));

					}
//					System.out.println("cnt "+cnt+" count "+rs.getString("count"));
					if (uF.parseToInt(rs.getString("count")) >= cnt) {
						if (!list.contains(rs.getString("emp_id"))){
//							System.out.println("in 1 if");
							continue;
						}
						if(alFinalisedAppraisals!=null && alFinalisedAppraisals.contains(rs.getString("emp_id"))){
//							System.out.println("in 2 if");
							continue;
						}
						
//						System.out.println("after in 2 if");
						
						List<String> innerList = new ArrayList<String>();
						innerList.add(appraisalMp.get("ID"));//0
						innerList.add(appraisalMp.get("APPRAISAL")+" ("+appraisalMp.get("APP_FREQ_NAME")+")");//1
						innerList.add(appraisalMp.get("ORIENT"));//2
						innerList.add(appraisalMp.get("FREQUENCY"));//3
						innerList.add(rs.getString(2));//4
						innerList.add(appraisalMp.get("APP_FREQ_ID"));//5
						outerList.add(innerList);
						flag=true;
					}
					// if (memberCount == rs.getInt(1)) {

					// }
					
					
				}
				rs.close();
				pst.close();
				
				
				hmCompletedAppraisals.put(appraisalIdList.get(i), outerList);
				request.setAttribute("hmCompletedAppraisals", hmCompletedAppraisals);
				
//				System.out.println("hmCompletedAppraisals====>"+hmCompletedAppraisals);

			}
			
			request.setAttribute("flag",flag);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private String getAppendData(String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();

		if (strID != null && !strID.equals("")) {

			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 1; i < temp.length; i++) {
					if (i == 1) {
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

	public Map<String, String> getLevelMap(Connection con) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new HashMap<String, String>();
		try {
			pst = con.prepareStatement(selectLevel);
			rs = pst.executeQuery();

			while (rs.next()) {
				hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name") + "[" + rs.getString("level_code") + "]");
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmLevelMap;
	}

	private void getOrientationMember() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
			Map<String, String> orientationMemberMp = new HashMap<String, String>();
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMemberMp.put(rs.getString("orientation_member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();

			request.setAttribute("orientationMemberMp", orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private Map<String, List<String>> getFinalisedAppraisals(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, List<String>> hmFinalisedAppraisals = new HashMap<String, List<String>>();
		
		try {
			pst = con.prepareStatement("select * from appraisal_final_sattlement where if_approved=true order by appraisal_id");
			rs = pst.executeQuery();
			List<String> alFinalisedAppraisals = new ArrayList<String>();
			String strAppraisalIdNew = null;
			String strAppraisalIdOld = null;
			while (rs.next()) {
				strAppraisalIdNew = rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id");
				if(strAppraisalIdNew!=null && !strAppraisalIdNew.equalsIgnoreCase(strAppraisalIdOld)){
					alFinalisedAppraisals = new ArrayList<String>();
				}
				alFinalisedAppraisals.add(rs.getString("emp_id"));
				hmFinalisedAppraisals.put(strAppraisalIdNew, alFinalisedAppraisals);
				strAppraisalIdOld = strAppraisalIdNew;
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmFinalisedAppraisals;
	}

	private Map<String, String> getOrientationValue(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		Map<String, String> orientationMp = new HashMap<String, String>();
		try {

			pst = con.prepareStatement("select * from apparisal_orientation");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMp.put(rs.getString("apparisal_orientation_id"), rs.getString("orientation_name"));
			}
			rs.close();
			pst.close();

			request.setAttribute("orientationMp", orientationMp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMp;
	}

	// public String getId() {
	// return id;
	// }
	//
	// public void setId(String id) {
	// this.id = id;
	// }

}
