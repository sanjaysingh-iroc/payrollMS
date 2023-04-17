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

public class PendingAppraisal implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	CommonFunctions CF;
	
	private String id;
	private String empId;
	private String checkStatus;

	public String getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}

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

		request.setAttribute(PAGE, "/jsp/performance/PendingAppraisal.jsp");
		request.setAttribute(TITLE, "Appraisal");
//		System.out.println("checkStatus " + getCheckStatus());
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
		con = db.makeConnection(con);

		try {

			Map<String, String> hmDesignation = CF.getDesigMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmLevelMap = getLevelMap(con);

			// Map<String, String> approvebySuperID = new HashMap<String,
			// String>();
			// Map<String, String> approvebyHRID = new HashMap<String,
			// String>();
			// Map<String, String> approvebyPeerID = new HashMap<String,
			// String>();

			request.setAttribute("hmEmpName", hmEmpName);

			// String orient=null;

			StringBuilder sb = new StringBuilder();
			sb.append("select * from appraisal_details where appraisal_details_id =? ");

			pst = con.prepareStatement(sb.toString());
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();

			Map<String, String> appraisalMp = new HashMap<String, String>();
			String appraisalee = null;
			int wlocation = 0;
			int departId = 0;

			String superwiser_name = null;
			String hrManagername = null;
			String peer_name = null;

			while (rs.next()) {

				appraisalMp.put("ID", rs.getString("appraisal_details_id"));
				appraisalMp.put("APPRAISAL", rs.getString("appraisal_name"));
				appraisalMp.put("ORIENT", rs.getString("oriented_type"));
				appraisalMp.put("EMPLOYEE", hmEmpName.get(rs.getString("self_ids")));
				appraisalMp.put("LEVEL", uF.showData( hmLevelMap.get(rs.getString("level_id")), ""));
				appraisalMp.put("DESIG", hmDesignation.get(rs.getString("desig_id")));
				appraisalMp.put("GRADE", hmGradeMap.get(rs.getString("grade_id")));
				wlocation = rs.getInt("wlocation_id");
				departId = rs.getInt("department_id");
				appraisalMp.put("WLOCATION", rs.getString("wlocation_id"));
				appraisalMp.put("PEER", rs.getString("peer_ids"));

				appraisalMp.put("FREQUENCY", rs.getString("frequency"));
				appraisalMp.put("FROM", rs.getString("from_date"));
				appraisalMp.put("TO", rs.getString("to_date"));
				appraisalMp.put("PLAN_TYPE", uF.parseToInt(rs.getString("plan_type")) == 2 ? "Appraisal Based" : "Performance Based");
				
				if (uF.parseToInt(rs.getString("employee_id")) != 0) {
					appraisalee = rs.getString("employee_id");
				} else if (uF.parseToInt(rs.getString("grade_id")) != 0) {
					appraisalee = rs.getString("grade_id");
				} else if (uF.parseToInt(rs.getString("desig_id")) != 0) {
					appraisalee = rs.getString("desig_id");
				} else {
					appraisalee = rs.getString("level_id");

				}

				Map<String, Map<String, String>> mp = new HashMap<String, Map<String, String>>();

				pst = con.prepareStatement("select * from appraisal_approval where appraisal_id =? ");
				pst.setInt(1, uF.parseToInt(id));
				rs = pst.executeQuery();
				while (rs.next()) {
					Map<String, String> innerMp = mp.get(rs.getString("emp_id"));
					if (innerMp == null)
						innerMp = new HashMap<String, String>();
					innerMp.put(rs.getString("user_type_id"),rs.getString("status"));

					mp.put(rs.getString("emp_id"), innerMp);
				}
				rs.close();
				pst.close();
				request.setAttribute("mp", mp);
			}
			rs.close();
			pst.close();
			
			

//			pst = con
//					.prepareStatement("select * from appraisal_plan where appraisal_id =?");
//			pst.setInt(1, uF.parseToInt(id));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//
//				appraisalMp.put("FREQUENCY", rs.getString("frequency_type"));
//				appraisalMp.put("FROM", rs.getString("from_date"));
//				appraisalMp.put("TO", rs.getString("to_date"));
//				appraisalMp.put("PLAN_TYPE", uF.parseToInt(rs
//						.getString("plan_type")) == 2 ? "Appraisal Based"
//						: "Performance Based");
//
//			}

			request.setAttribute("appraisalMp", appraisalMp);

			List<String> employeeList = new ArrayList<String>();

			if (appraisalMp.get("EMPLOYEE") != null) {
//				System.out.println("in EMPLOYEE");
				employeeList.add(appraisalee);

			} else if (appraisalMp.get("GRADE") != null) {
//				System.out.println("in grade");
				if (uF.parseToInt(getCheckStatus()) == 1) {
//					System.out.println("in 1");
					pst = con.prepareStatement("select emp_per_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and wlocation_id=?  and grade_id =? and depart_id=? and emp_id  in(select emp_id from appraisal_final_sattlement where appraisal_id=?)  group by emp_per_id order by emp_per_id");
				} else if (uF.parseToInt(getCheckStatus()) == 2) {
//					System.out.println("in 2");
					pst = con.prepareStatement("select emp_per_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and wlocation_id=?  and grade_id =? and depart_id=? and emp_id not in(select emp_id from appraisal_final_sattlement where appraisal_id=?)  group by emp_per_id order by emp_per_id");
				} else {
//					System.out.println("in 0");
					pst = con.prepareStatement("select emp_per_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and wlocation_id=?  and grade_id =? and depart_id=?  group by emp_per_id order by emp_per_id");
				}
				pst.setInt(1, wlocation);
				pst.setInt(2, uF.parseToInt(appraisalee));
				pst.setInt(3, departId);

				if (uF.parseToInt(getCheckStatus()) == 1) {
					pst.setInt(4, uF.parseToInt(getId()));
				} else if (uF.parseToInt(getCheckStatus()) == 2) {
					pst.setInt(4, uF.parseToInt(getId()));
				}
				rs = pst.executeQuery();
				while (rs.next()) {
					employeeList.add(rs.getString("emp_per_id"));
				}
				rs.close();
				pst.close();

			} else if (appraisalMp.get("DESIG") != null) {
//				System.out.println("in desig");
				if (uF.parseToInt(getCheckStatus()) == 1) {
//					System.out.println("in 1");
					pst = con.prepareStatement("select emp_per_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and wlocation_id=? and  grade_id in(SELECT grade_id FROM grades_details where designation_id =? ) and depart_id=? and emp_id  in(select emp_id from appraisal_final_sattlement where appraisal_id=?)  group by emp_per_id order by emp_per_id ");

				} else if (uF.parseToInt(getCheckStatus()) == 2) {
//					System.out.println("in 2");
					pst = con.prepareStatement("select emp_per_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and wlocation_id=? and  grade_id in(SELECT grade_id FROM grades_details where designation_id =? ) and depart_id=? and emp_id not in(select emp_id from appraisal_final_sattlement where appraisal_id=?)  group by emp_per_id order by emp_per_id ");

				} else {
					pst = con.prepareStatement("select emp_per_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and wlocation_id=? and  grade_id in(SELECT grade_id FROM grades_details where designation_id =? ) and depart_id=?  group by emp_per_id order by emp_per_id ");
				}

				// pst = con
				// .prepareStatement("select emp_per_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and wlocation_id=? and  grade_id in(SELECT grade_id FROM grades_details where designation_id =? ) and depart_id=? and emp_id not in(select emp_id from appraisal_final_sattlement where appraisal_id=?)  group by emp_per_id order by emp_per_id ");
				pst.setInt(1, wlocation);
				pst.setInt(2, uF.parseToInt(appraisalee));
				pst.setInt(3, departId);

				if (uF.parseToInt(getCheckStatus()) == 1) {
					pst.setInt(4, uF.parseToInt(getId()));
				} else if (uF.parseToInt(getCheckStatus()) == 2) {
					pst.setInt(4, uF.parseToInt(getId()));
				}
				rs = pst.executeQuery();

				while (rs.next()) {
					employeeList.add(rs.getString(1));
				}
				rs.close();
				pst.close();
				
			} else {
//				System.out.println("in else");
				if (uF.parseToInt(getCheckStatus()) == 1) {
//					System.out.println("in 1");
					pst = con
							.prepareStatement("select emp_per_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id  and wlocation_id=? and   grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details  WHERE level_id = ?) ) and depart_id=? and emp_id  in(select emp_id from appraisal_final_sattlement where appraisal_id=?) group by emp_per_id order by emp_per_id");

				} else if (uF.parseToInt(getCheckStatus()) == 2) {
//					System.out.println("in 2");
					pst = con
							.prepareStatement("select emp_per_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id  and wlocation_id=? and   grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details  WHERE level_id = ?) ) and depart_id=? and emp_id not in(select emp_id from appraisal_final_sattlement where appraisal_id=?) group by emp_per_id order by emp_per_id");

				} else {
					pst = con
							.prepareStatement("select emp_per_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id  and wlocation_id=? and   grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details  WHERE level_id = ?) ) and depart_id=? group by emp_per_id order by emp_per_id");
				}
				// pst =
				// con.prepareStatement("select emp_per_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id  and wlocation_id=? and   grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details  WHERE level_id = ?) ) and depart_id=? and emp_id not in(select emp_id from appraisal_final_sattlement where appraisal_id=?) group by emp_per_id order by emp_per_id");
				pst.setInt(1, wlocation);
				pst.setInt(2, uF.parseToInt(appraisalee));
				pst.setInt(3, departId);
				if (uF.parseToInt(getCheckStatus()) == 1) {
					pst.setInt(4, uF.parseToInt(getId()));
				} else if (uF.parseToInt(getCheckStatus()) == 2) {
					pst.setInt(4, uF.parseToInt(getId()));
				}
				rs = pst.executeQuery();

				while (rs.next()) {
					employeeList.add(rs.getString(1));
				}
				rs.close();
				pst.close();

			}

			request.setAttribute("employeeList", employeeList);

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
				hmLevelMap.put(
						rs.getString("level_id"),
						rs.getString("level_name") + "["
								+ rs.getString("level_code") + "]");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmLevelMap;
	}
}
