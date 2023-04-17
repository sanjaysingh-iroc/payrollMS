package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class EmpAppraisalSummaryReviewer implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strBaseUserTypeID;
	CommonFunctions CF;
	
	private String id;
	private String empID;
	private String userType;
	private String appFreqId;
	private String role;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserTypeID = (String) session.getAttribute(BASEUSERTYPEID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		request.setAttribute(PAGE, "/jsp/performance/EmpAppraisalSummaryReviewer.jsp");
		request.setAttribute(TITLE, "Employee Review Summary");
		
		request.setAttribute("arrEnabledModules", CF.getArrEnabledModules());
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		UtilityFunctions uF = new UtilityFunctions();

//		System.out.println("empID =====>> " + getEmpID());
//		System.out.println("userType =====>> " + getUserType());
//		System.out.println("appFreqId =====>> " + getAppFreqId());
//		System.out.println("id =====>> " + getId());
//		System.out.println("role =====>> " + getRole());
		
		getanswerTypeMap(uF);
		// getLevelStep(uF);
		getQuestionSubType(uF);
		getAppraisalDetail(uF);
		getEmployyDetailsList(uF);
		getAppraisalQuestionMap(uF);
		getLevelStatus(uF);
		getCurrentLevelAnswer(uF);
		checkCurrentLevelExistForCurrentEmp(uF);
		getSummary(uF);
		
		return getLevelQuestion(uF);

	}
	
	
	private void checkCurrentLevelExistForCurrentEmp(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		List<String> sectionIDList = new ArrayList<String>(); 
		try {
			pst = con.prepareStatement("select * from appraisal_question_answer where emp_id=? and appraisal_id=? and reviewer_user_type_id=?" +
				" and reviewer_id=? and appraisal_freq_id=? and reviewer_or_appraiser=? and user_type_id=?"); //section_id = ? and 
			pst.setInt(1, uF.parseToInt(getEmpID()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(strBaseUserTypeID));
//			pst.setInt(4, uF.parseToInt(getCurrentLevel()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			pst.setInt(6, 0);
			pst.setInt(7, uF.parseToInt(getUserType()));
			rst = pst.executeQuery();
//			System.out.println("pst === > "+pst);
			while (rst.next()) {
				sectionIDList.add(rst.getString("section_id"));
			}
			rst.close();
			pst.close();
//			System.out.println("sectionIDList ===>> " + sectionIDList);
			
			List<String> IsQueSectionIDList = new ArrayList<String>();
			pst = con.prepareStatement("select main_level_id from appraisal_level_details where appraisal_level_id in (select appraisal_level_id from appraisal_question_details where appraisal_id=?) "); //section_id = ? and 
			pst.setInt(1, uF.parseToInt(getId()));
			rst = pst.executeQuery();
//			System.out.println("pst === > "+pst);
			while (rst.next()) {
				IsQueSectionIDList.add(rst.getString("main_level_id"));
			}
			rst.close();
			pst.close();
//			System.out.println("IsQueSectionIDList ===>> " + IsQueSectionIDList);
			
			String sectionCount="0";
			pst = con.prepareStatement("select count(distinct(section_id)) as section_id from appraisal_question_answer where emp_id=? and appraisal_id=? " +
				"and reviewer_user_type_id=? and reviewer_id=? and appraisal_freq_id=? and reviewer_or_appraiser=? and user_type_id=?");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(strBaseUserTypeID));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			pst.setInt(6, 0);
			pst.setInt(7, uF.parseToInt(getUserType()));
			rst = pst.executeQuery();
			while (rst.next()) {
				sectionCount = rst.getString("section_id");
			}
			rst.close();
			pst.close();
//			System.out.println("sectionCount ===> " + sectionCount);
			request.setAttribute("sectionCount", sectionCount);
			request.setAttribute("IsQueSectionIDList", IsQueSectionIDList);
			request.setAttribute("sectionIDList", sectionIDList);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getSummary(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			// Map<String, String> hmEmpProbationEnd =
			// CF.getEmpProbationEndDateMap(con, uF);
			Map<String, String> hmDepartmentMap = CF.getDeptMap(con);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			Map<String, String> hmOrientationMember = getOrientationMember(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and emp_per_id=?");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			rs = pst.executeQuery();
			Map<String, String> hmEmpDetails = new HashMap<String, String>();
			while (rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				hmEmpDetails.put("EMP_NAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				hmEmpDetails.put("EMP_CODE", rs.getString("empcode"));
				hmEmpDetails.put("EMP_ID", rs.getString("emp_per_id"));
				hmEmpDetails.put("DESIGNATION", uF.showData(hmEmpCodeDesig.get(rs.getString("emp_per_id")), ""));
				hmEmpDetails.put("LEVEL", uF.showData(hmLevelMap.get(hmEmpLevelMap.get(rs.getString("emp_per_id"))), ""));

				hmEmpDetails.put("DEAPRTMENT", uF.showData(hmDepartmentMap.get(rs.getString("depart_id")), ""));
				hmEmpDetails.put("JOINING_DATE", uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();
			
//			if(getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
				hmEmpDetails.put("ORIENTATION", "Reviewer");
//			} else {
//				hmEmpDetails.put("ORIENTATION", hmOrientationMember.get(getUserType()));
//			}
			request.setAttribute("hmEmpDetails", hmEmpDetails);

			
			pst = con.prepareStatement("select appraisal_level_id from appraisal_question_answer where appraisal_id=? and emp_id=? and reviewer_id=? " +
				"and reviewer_user_type_id=? and appraisal_freq_id=? and reviewer_or_appraiser=? and user_type_id=? group by appraisal_level_id order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(strBaseUserTypeID));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			pst.setInt(6, 0);
			pst.setInt(7, uF.parseToInt(getUserType()));
			rs = pst.executeQuery();
			Map<String, String> innerMp = new HashMap<String, String>();
			while (rs.next()) {
				innerMp.put(rs.getString("appraisal_level_id"), rs.getString("appraisal_level_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("LEVEL_STATUS", innerMp);

			List<String> levelList = new ArrayList<String>();
			pst = con.prepareStatement("select appraisal_level_id from appraisal_level_details where appraisal_id=? order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				levelList.add(rs.getString("appraisal_level_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("levelList", levelList);
//			System.out.println("levelList=======+>" + levelList);
			
			List<String> mainLevelList = new ArrayList<String>();
			pst = con.prepareStatement("select main_level_id from appraisal_main_level_details where appraisal_id=? order by main_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				mainLevelList.add(rs.getString("main_level_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("mainLevelList", mainLevelList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public void getCurrentLevelAnswer(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id=? and emp_id=? and reviewer_id=? and " +
				"reviewer_user_type_id=? and appraisal_freq_id=? and reviewer_or_appraiser=? and user_type_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(strBaseUserTypeID));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			pst.setInt(6, 0);
			pst.setInt(7, uF.parseToInt(getUserType()));
			rs = pst.executeQuery();
			Map<String, Map<String, String>> questionanswerMp = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				Map<String, String> innerMp = new HashMap<String, String>();
				innerMp.put("ANSWER", rs.getString("reviewer_answer"));
				innerMp.put("REMARK", rs.getString("reviewer_remark"));
				innerMp.put("MARKS", rs.getString("reviewer_marks"));
				innerMp.put("APP_QUE_ANS_ID", rs.getString("appraisal_question_answer_id"));
				innerMp.put("WEIGHTAGE", rs.getString("weightage"));
				innerMp.put("ANSWERCOMMENT", rs.getString("reviewer_answers_comment"));
				innerMp.put("LEVEL_COMMENT", rs.getString("section_comment"));
				if (uF.parseToInt(rs.getString("scorecard_id")) != 0) {
					questionanswerMp.put(rs.getString("scorecard_id") + "question" + rs.getString("question_id"), innerMp);
				} else if (uF.parseToInt(rs.getString("other_id")) != 0) { 
					questionanswerMp.put(rs.getString("other_id") + "question" + rs.getString("question_id"), innerMp);
				} else {
					questionanswerMp.put("question" + rs.getString("question_id"), innerMp);
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("questionanswerMp", questionanswerMp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public void getLevelStatus(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select appraisal_level_id from appraisal_question_answer where appraisal_id=? and emp_id=? and " +
				"reviewer_id=? and reviewer_user_type_id=? and appraisal_freq_id=? and reviewer_or_appraiser=? and user_type_id=? " +
				"group by appraisal_level_id order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(strBaseUserTypeID));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			pst.setInt(6, 0);
			pst.setInt(7, uF.parseToInt(getUserType()));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			Map<String, String> innerMp = new HashMap<String, String>();
			while (rs.next()) {
				innerMp.put(rs.getString("appraisal_level_id"), rs.getString("appraisal_level_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("LEVEL_STATUS", innerMp); 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public boolean getPreviousLevelData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		boolean flag = false;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
			StringBuilder sb = new StringBuilder("select * from appraisal_question_details where appraisal_id=? ");
			/*if(getRole() == null || !getRole().equalsIgnoreCase("Reviewer")) {
				if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("Manager"))) {
					sb.append(" and manager !=0");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("HR"))) {
					sb.append(" and hr !=0");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("Self"))) {
					sb.append(" and  self!=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("Peer"))) {
					sb.append(" and peer !=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("CEO"))) {
					sb.append(" and ceo !=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("HOD"))) {
					sb.append(" and hod !=0 ");
				} 
			}*/
			pst = con.prepareStatement(sb.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			// pst.setInt(2, uF.parseToInt(getCurrentLevel()));
			rs = pst.executeQuery();
			List<List<String>> outerList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add("");
				innerList.add(rs.getString("appraisal_id"));
				innerList.add(rs.getString("plan_id"));
				innerList.add("");
				innerList.add("");
				innerList.add("");
				innerList.add("");
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("attribute_id"));
				innerList.add(rs.getString("other_short_description"));
				innerList.add(rs.getString("appraisal_level_id"));
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(rs.getString("other_id"));
				innerList.add("");
				innerList.add("");
				outerList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("questionList", outerList);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}

	
	private Map<String, String> getOrientMemberID(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> orientationMemberMp = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMemberMp.put(rs.getString("member_name"), rs.getString("member_id"));
			}
			rs.close();
			pst.close();
//			System.out.println("memberid=====>"+orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMemberMp;
	}
	

	
	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	public void getAppraisalDetail(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			List<String> appraisalList = new ArrayList<String>();
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
//			System.out.println("appraisalList pst ---> "+ pst);
			while (rs.next()) {
				appraisalList.add(uF.showData(rs.getString("appraisal_details_id"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_name"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_description"), ""));
				appraisalList.add(uF.showData(rs.getString("oriented_type"), ""));
				appraisalList.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalList.add(uF.showData(rs.getString("appraisal_instruction"), ""));
			}
			rs.close();
			pst.close();
			request.setAttribute("appraisalList", appraisalList);
//			System.out.println("appraisalList ---> "+appraisalList);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	private void getEmployyDetailsList(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpDepartment = CF.getEmpDepartmentMap(con);
			Map<String, String> hmEmpJoiningDate = CF.getEmpJoiningDateMap(con, uF);
			Map<String, String> hmEmpProbationEnd = CF.getEmpProbationEndDateMap(con, uF);
			Map<String, String> mpdepart = CF.getDeptMap(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("select * from employee_personal_details where emp_per_id=?");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			rs = pst.executeQuery();
			List<String> empList = new ArrayList<String>();
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			while (rs.next()) {
				empList.add(rs.getString("emp_per_id"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				empList.add(rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname") + " [" + rs.getString("empcode") + "]");
				empList.add(uF.showData(mpdepart.get(hmEmpDepartment.get(rs.getString("emp_per_id"))), ""));
				empList.add(uF.showData(hmEmpCodeDesig.get(rs.getString("emp_per_id")), ""));
				empList.add(uF.showData(hmEmpJoiningDate.get(rs.getString("emp_per_id")), ""));
				empList.add(uF.showData(hmEmpProbationEnd.get(rs.getString("emp_per_id")), ""));
			}
			rs.close();
			pst.close();
			empList.add(orientationMemberMp.get(getUserType()));
			request.setAttribute("empList", empList);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public String getLevelQuestion(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		// boolean flag = false;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
			StringBuilder sb = new StringBuilder("select * from appraisal_question_details where appraisal_id=? ");
			/*if(getRole() == null || !getRole().equalsIgnoreCase("Reviewer")) {
				if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get(MANAGER))) {
					sb.append(" and manager !=0");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("HR"))) {
					sb.append(" and hr !=0");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("Self"))) {
					sb.append(" and  self!=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("Peer"))) {
					sb.append(" and peer !=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("CEO"))) {
					sb.append(" and ceo !=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("HOD"))) {
					sb.append(" and hod !=0 ");
				}
			}*/
			sb.append(" order by appraisal_level_id");
			pst = con.prepareStatement(sb.toString());
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
//			System.out.println("pst 1 :::: "+pst);
			Map<String,List<List<String>>> hmLevelQuestion = new LinkedHashMap<String, List<List<String>>>();
			StringBuilder sbLevels = new StringBuilder();
			while (rs.next()) {
				List<List<String>> outerList=hmLevelQuestion.get(rs.getString("appraisal_level_id"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add("");
				innerList.add(rs.getString("appraisal_id"));
				innerList.add(rs.getString("plan_id"));
				innerList.add("");
				innerList.add("");
				innerList.add("");
				innerList.add("");
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("attribute_id"));
				innerList.add(rs.getString("other_short_description"));
				innerList.add(rs.getString("appraisal_level_id"));
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(rs.getString("other_id"));
				innerList.add("");
				innerList.add("");
				outerList.add(innerList);
				hmLevelQuestion.put(rs.getString("appraisal_level_id"), outerList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmLevelQuestion :::: "+hmLevelQuestion);
			request.setAttribute("hmLevelQuestion", hmLevelQuestion);
			
			Map<String, List<List<String>>> hmSection = new LinkedHashMap<String, List<List<String>>>();
			sb = new StringBuilder("select * from appraisal_level_details where main_level_id in(select main_level_id from " +
					"appraisal_main_level_details where appraisal_id=(select appraisal_details_id from appraisal_details where " +
					"appraisal_details_id = ?) ");
			if(getRole() == null || !getRole().equalsIgnoreCase("Reviewer")) {
			 	if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get(MANAGER))) {
					sb.append(" and manager !=0");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("HR"))) {
					sb.append(" and hr !=0");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("Self"))) {
					sb.append(" and  self!=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("Peer"))) {
					sb.append(" and peer !=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("CEO"))) {
					sb.append(" and ceo !=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("HOD"))) {
					sb.append(" and hod !=0 ");
				}
			}
			sb.append(") order by main_level_id,appraisal_level_id");
			pst = con.prepareStatement(sb.toString());
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
//				System.out.println("pst 2 :::: "+pst);
			while (rs.next()) {
				List<List<String>> outerList=hmSection.get(rs.getString("main_level_id"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_level_id"));
				innerList.add(rs.getString("level_title"));
				innerList.add(rs.getString("short_description"));
				innerList.add(rs.getString("long_description"));
				outerList.add(innerList);
				hmSection.put(rs.getString("main_level_id"), outerList);
				
				sbLevels.append(rs.getString("main_level_id")+",");
			}
			rs.close();
			pst.close();
				
			request.setAttribute("hmSection", hmSection);
//			System.out.println("hmSection :::: "+hmSection);
			
			Map hmLevelDetails = new HashMap();
			if(sbLevels.length()>1){
				sbLevels.replace(0, sbLevels.length(), sbLevels.substring(0, sbLevels.length()-1));
				pst = con.prepareStatement("select * from appraisal_main_level_details where main_level_id in ("+sbLevels.toString()+") order by main_level_id");
				rs = pst.executeQuery();
//				System.out.println(" pst : "+ pst);
				while(rs.next()){
					hmLevelDetails.put(rs.getString("main_level_id")+"_TITLE", rs.getString("level_title"));
					hmLevelDetails.put(rs.getString("main_level_id")+"_SDESC", rs.getString("short_description"));
					hmLevelDetails.put(rs.getString("main_level_id")+"_LDESC", rs.getString("long_description"));
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("hmLevelDetails", hmLevelDetails);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "success";
	}


	public void getAppraisalQuestionMap(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, List<String>> hmQuestion = new HashMap<String, List<String>>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from question_bank qb, appraisal_question_details aqd where qb.question_bank_id = aqd.question_id and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			List<List<String>> outerList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("question_bank_id"));
				innerList.add(rs.getString("question_text"));
				innerList.add(rs.getString("option_a"));
				innerList.add(rs.getString("option_b"));
				innerList.add(rs.getString("option_c"));
				innerList.add(rs.getString("option_d"));
				innerList.add(rs.getString("correct_ans"));
				innerList.add(rs.getString("is_add"));
				innerList.add(rs.getString("question_type"));
				innerList.add(rs.getString("option_e")); //9

				outerList.add(innerList);
				hmQuestion.put(rs.getString("question_bank_id"), innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmQuestion", hmQuestion);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		// return AppraisalQuestion;
	}

	
	public void getanswerTypeMap(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, List<List<String>>> hmQuestionanswerType = new HashMap<String, List<List<String>>>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_answer_type_sub ");
			rs = pst.executeQuery();
			while (rs.next()) {
				List<List<String>> outerList = hmQuestionanswerType.get(rs.getString("answer_type_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("score_label"));
				innerList.add(rs.getString("score"));
				outerList.add(innerList);
				hmQuestionanswerType.put(rs.getString("answer_type_id"), outerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmQuestionanswerType", hmQuestionanswerType);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public Map<String, String> getLevelMap(UtilityFunctions uF) {
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

	
	private void getQuestionSubType(UtilityFunctions uF) {
		Map<String, List<List<String>>> answertypeSub = new HashMap<String, List<List<String>>>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_answer_type_sub order by appraisal_answer_type_sub_id");
			rst = pst.executeQuery();
			while (rst.next()) {
				List<List<String>> outerList = answertypeSub.get(rst.getString("answer_type_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("score"));
				innerList.add(rst.getString("score_label"));
				outerList.add(innerList);
				answertypeSub.put(rst.getString("answer_type_id"), outerList);
			}
			rst.close();
			pst.close();
			
			request.setAttribute("answertypeSub", answertypeSub);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	
	private Map<String, String> getOrientationMember(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> orientationMemberMp = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from orientation_member where status = true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMemberMp.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMemberMp;
	}


	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

	
}
