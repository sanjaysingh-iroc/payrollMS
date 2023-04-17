package com.konnect.jpms.performance;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillResourceType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Dattatray
 *
 */
public class AnnualAppraisalReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	public HttpSession session;
	public String strSessionEmpId;
	public String strUserType;
	public String strBaseUserType;
	public CommonFunctions CF;
	String strEmpId;
	String empType;
	private String strOrgId;
	private String fromPage;
	private String empId;

	@Override
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
//		System.out.println("strUserType ===> " + strUserType);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
//		System.out.println("strBaseUserType ===> " + strBaseUserType);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		setStrOrgId((String) session.getAttribute(ORGID));

		UtilityFunctions uF = new UtilityFunctions();

		request.setAttribute(TITLE, "Annual Appraisal Report");
		request.setAttribute(PAGE, "/jsp/reports/AnnualAppraisalReport.jsp");
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		request.setAttribute("DOC_SAVE_LOCATION", CF.getStrDocSaveLocation());
		if (request.getParameter("download") != null && !request.getParameter("download").isEmpty()) {
			request.setAttribute("download", request.getParameter("download"));
		}

		if(getEmpId() !=null && uF.parseToInt(getEmpId())>0) {
			strSessionEmpId = getEmpId();
		}
		viewWLocation(uF);
		employeeIdsCount(uF);
		getEmpProfileDetail(uF, null, strSessionEmpId);
		getAnnualAppraisalReport(uF);
		getSectionName();
		getOrientationMember();
		if (getFromPage() != null && getFromPage().equalsIgnoreCase("MyHR")) {
			return SUCCESS;
		}
		return SUCCESS;
	}

	public void viewWLocation(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			// String strOrgAccess = (String)session.getAttribute(ORG_ACCESS);

			List<String> alInner = new ArrayList<String>();
			Map<String, List<String>> hmOrganistaionMap = new HashMap<String, List<String>>();

			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from org_details where org_id = ?");
			/*
			 * if(strBaseUserType!=null &&
			 * !strBaseUserType.equalsIgnoreCase(ADMIN) && getStrOrgId() != null
			 * && !getStrOrgId().trim().equals("") &&
			 * !getStrOrgId().trim().equalsIgnoreCase("NULL")){
			 * sbQuery.append(" and org_id in( "+getStrOrgId()+") "); }
			 */
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrOrgId()));
//			System.out.println("pst wlocationReport==>" + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("org_id"));// 0
				alInner.add(rs.getString("org_code")); // 1
				alInner.add(rs.getString("org_name"));// 2

				String fileName = "";
				if (rs.getString("org_logo") != null && !rs.getString("org_logo").equals("")) {
					if (CF.getStrDocSaveLocation() == null) {
						fileName = DOCUMENT_LOCATION + rs.getString("org_logo");
					} else {
						fileName = CF.getStrDocSaveLocation() + I_ORGANISATION + "/" + I_IMAGE + "/" + rs.getString("org_logo");
						if(!uF.isFileExist(fileName)) {
							fileName = "userImages/company_avatar_photo.png";	
						} else {
//							fileName = CF.getStrDocRetriveLocation() + I_ORGANISATION + "/" + I_IMAGE + "/" + rs.getString("org_logo");
							fileName = "userImages/haqdarshak.jpg";
						}
					}
				}
				alInner.add(fileName);// 3
				alInner.add(rs.getString("org_name"));// 4

				String fileName1 = "";
				if (rs.getString("org_logo_small") != null && !rs.getString("org_logo_small").equals("")) {
					if (CF.getStrDocSaveLocation() == null) {
						fileName1 = DOCUMENT_LOCATION + rs.getString("org_logo_small");
					} else {
						fileName1 = CF.getStrDocSaveLocation() + I_ORGANISATION + "/" + I_IMAGE_SMALL + "/" + rs.getString("org_logo_small");
						if(!uF.isFileExist(fileName1)) {
							fileName1 = "userImages/company_avatar_photo.png";
						} else {
//							fileName1 = CF.getStrDocRetriveLocation() + I_ORGANISATION + "/" + I_IMAGE_SMALL + "/" + rs.getString("org_logo_small");
							fileName1 = "userImages/haqdarshak.jpg";
						}
					}
				}
				alInner.add(fileName1);// 5
				hmOrganistaionMap.put(rs.getString("org_id"), alInner);
			}
			rs.close();
			pst.close();
//			System.out.println(hmOrganistaionMap);
			request.setAttribute("hmOrganistaionMap", hmOrganistaionMap);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void employeeIdsCount(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);

			pst = con.prepareStatement(
					" select distinct(appraisal_id) as appraisal_id,appraisal_system from appraisal_level_details where appraisal_id in (select appraisal_id from appraisal_reviewee_details where reviewee_id=?) order by appraisal_id");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			String appId360 = null;
			String appId90 = null;
			while (rs.next()) {
				if (rs.getInt("appraisal_system") == 4) {
					appId90 = rs.getString("appraisal_id");
				} else {
					appId360 = rs.getString("appraisal_id");
				}
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
					appId90 = rs.getString("appraisal_id");
				}
			}
			rs.close();
			pst.close();
//			System.out.println("appId90 1 : " + appId90);
//			System.out.println("appId360 1 : " + appId360);

			sbQuery.append("select * from appraisal_reviewee_details where reviewee_id = ?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			System.out.println("PST Count : " + pst.toString());
			rs = pst.executeQuery();
			List<String> peerIdsList = new ArrayList<String>();
			List<String> supervisorIdsList = new ArrayList<String>();
			List<String> grandSupervisorIdsList = new ArrayList<String>();
			List<String> revieweeIdsList = new ArrayList<String>();
			List<String> subordinateIdsList = new ArrayList<String>();
			List<String> otherPeerIdsList = new ArrayList<String>();
			List<String> hodIdsList = new ArrayList<String>();
			List<String> ceoIdsList = new ArrayList<String>();
			List<String> hrIdsList = new ArrayList<String>();
			List<String> recruiterIdsList = new ArrayList<String>();
			List<String> otherIdsList = new ArrayList<String>();

			Map<String, Map<String, String>> hmHeaderCount = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				if (rs.getString("peer_ids") != null && !rs.getString("peer_ids").contains("''") && rs.getString("peer_ids").length() > 0) {
					String strPeersIds = rs.getString("peer_ids").substring(1);
					peerIdsList = Arrays.asList(strPeersIds.split(","));
				}

				if (rs.getString("reviewee_id") != null && !rs.getString("reviewee_id").contains("''") && rs.getString("reviewee_id").length() > 0) {
					revieweeIdsList = Arrays.asList(rs.getString("reviewee_id"));
				}

				if (rs.getString("supervisor_ids") != null && !rs.getString("supervisor_ids").contains("''") && rs.getString("supervisor_ids").length() > 0) {
					String strSupervisorIds = rs.getString("supervisor_ids").substring(1);
					supervisorIdsList = Arrays.asList(strSupervisorIds.split(","));
				}

				if (rs.getString("grand_supervisor_ids") != null && !rs.getString("grand_supervisor_ids").contains("''")
						&& rs.getString("grand_supervisor_ids").length() > 0) {
					String strGrandSupervisorIds = rs.getString("grand_supervisor_ids").substring(1);
					grandSupervisorIdsList = Arrays.asList(strGrandSupervisorIds.split(","));
				}

				if (rs.getString("subordinate_ids") != null && !rs.getString("subordinate_ids").contains("''")
						&& rs.getString("subordinate_ids").length() > 0) {
					String strSubordinateIds = rs.getString("subordinate_ids").substring(1);
					subordinateIdsList = Arrays.asList(strSubordinateIds.split(","));
				}

				//
				if (rs.getString("other_peer_ids") != null && !rs.getString("other_peer_ids").contains("''") && rs.getString("other_peer_ids").length() > 0) {
					String strOtherIds = rs.getString("other_peer_ids").substring(1);
					otherPeerIdsList = Arrays.asList(strOtherIds.split(","));
				}

				if (rs.getString("hod_ids") != null && !rs.getString("hod_ids").contains("''") && rs.getString("hod_ids").length() > 0) {
					String strHODIds = rs.getString("hod_ids").substring(1);
					hodIdsList = Arrays.asList(strHODIds.split(","));

				}

				if (rs.getString("ceo_ids") != null && !rs.getString("ceo_ids").contains("''") && rs.getString("ceo_ids").length() > 0) {
					String strCEOIds = rs.getString("ceo_ids").substring(1);
					ceoIdsList = Arrays.asList(strCEOIds.split(","));
				}

				if (rs.getString("hr_ids") != null && !rs.getString("hr_ids").contains("''") && rs.getString("hr_ids").length() > 0) {
					String strHRIds = rs.getString("hr_ids").substring(1);
					hrIdsList = Arrays.asList(strHRIds.split(","));
				}

				if (rs.getString("recruiter_ids") != null && !rs.getString("recruiter_ids").contains("''") && rs.getString("recruiter_ids").length() > 0) {
					String strRecruiterIds = rs.getString("recruiter_ids").substring(1);
					recruiterIdsList = Arrays.asList(strRecruiterIds.split(","));
				}

				if (rs.getString("other_ids") != null && !rs.getString("other_ids").contains("''") && rs.getString("other_ids").length() > 0) {
					String strOtherIds = rs.getString("other_ids").substring(1);
					otherIdsList = Arrays.asList(strOtherIds.split(","));
				}

				Map<String, String> value = hmHeaderCount.get(rs.getString("appraisal_id"));
				if (value == null)
					value = new HashMap<String, String>();
				value.put("2", supervisorIdsList.size() + "");
				value.put("3", revieweeIdsList.size() + "");
				value.put("4", peerIdsList.size() + "");
				value.put("5", ceoIdsList.size() + "");
				value.put("6", subordinateIdsList.size() + "");
				value.put("7", hrIdsList.size() + "");
				value.put("8", grandSupervisorIdsList.size() + "");
				value.put("9", recruiterIdsList.size() + "");
				value.put("10", otherIdsList.size() + "");
				value.put("13", hodIdsList.size() + "");
				value.put("14", otherPeerIdsList.size() + "");
				
				/*if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
					value.put("2", supervisorIdsList.size() + "");
					value.put("3", revieweeIdsList.size() + "");
				} else{
					value.put("2", supervisorIdsList.size() + "");
					value.put("3", revieweeIdsList.size() + "");
					value.put("4", peerIdsList.size() + "");
					value.put("5", ceoIdsList.size() + "");
					value.put("6", subordinateIdsList.size() + "");
					value.put("7", hrIdsList.size() + "");
					value.put("8", grandSupervisorIdsList.size() + "");
					value.put("9", recruiterIdsList.size() + "");
					value.put("10", otherIdsList.size() + "");
					value.put("13", hodIdsList.size() + "");
					value.put("14", otherPeerIdsList.size() + "");
				}*/

				hmHeaderCount.put(rs.getString("appraisal_id"), value);

			}
			rs.close();
			pst.close();

//			System.out.println("peerIdsCount : " + peerIdsList.size());
//			System.out.println("supervisorIdsCount : " + supervisorIdsList.size());
//			System.out.println("grandSupervisorIdsList : " + grandSupervisorIdsList.size());
//			System.out.println("revieweeIdsCount : " + revieweeIdsList.size());
//			System.out.println("subordinateIdsCount : " + subordinateIdsList.size());

			request.setAttribute("hmHeaderCount", hmHeaderCount);

			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uF.parseToInt(appId360));
			rs = pst.executeQuery();
			// System.out.println("==>pstAppraisaldetails"+pst);
			while (rs.next()) {

				List<String> memberList = new ArrayList<String>();
				if (rs.getString("usertype_member") != null && !rs.getString("usertype_member").equals("")) {
					memberList = Arrays.asList(rs.getString("usertype_member").split(","));
				}
				// Start dattatray
				if (memberList.contains("3")) {
					Collections.swap(memberList, 0, memberList.indexOf("3"));
				}

				if (memberList.contains("2")) {
					Collections.swap(memberList, 1, memberList.indexOf("2"));
				} // end dattatray
//				System.out.println("memberList : " + memberList);
				request.setAttribute("memberList", memberList);

			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uF.parseToInt(appId90));
			rs = pst.executeQuery();
			// System.out.println("==>pstAppraisaldetails"+pst);
			while (rs.next()) {

				List<String> memberList90 = new ArrayList<String>();
				if (rs.getString("usertype_member") != null && !rs.getString("usertype_member").equals("")) {
					memberList90 = Arrays.asList(rs.getString("usertype_member").split(","));
				}
				// Start dattatray
				if (memberList90.contains("3")) {
					Collections.swap(memberList90, 0, memberList90.indexOf("3"));
				}

				if (memberList90.contains("2")) {
					Collections.swap(memberList90, 1, memberList90.indexOf("2"));
				} // end dattatray
//				System.out.println("memberList90 : " + memberList90);
				request.setAttribute("memberList90", memberList90);
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
	}

	public Map<String, String> getEmpProfileDetail(UtilityFunctions uF, String strUserType, String strEmpIdReq) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> hmEmpProfile = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			request.setAttribute("hmEmpName", hmEmpName);

			Map<String, String> hmEmpResource = FillResourceType.getResourceData();
			if (hmEmpResource == null)
				hmEmpResource = new HashMap<String, String>();

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));

			// pst = con.prepareStatement("Select * from ( Select * from (
			// Select * from employee_personal_details epd left join
			// employee_official_details eod on epd.emp_per_id=eod.emp_id where
			// epd.emp_per_id=?) ast left join state s on ast.emp_state_id =
			// s.state_id ) aco left join country co on aco.emp_country_id =
			// co.country_id");
			pst = con.prepareStatement(" select a.*, org_name from org_details od right join(select a.*,wlocation_name from work_location_info wl right join("
					+ "select a.*,dept_name,dept_code from department_info d right join("
					+ "select a.*,level_code,level_name from level_details ld right join(select a.*,dd.designation_id,designation_code,designation_name,"
					+ "level_id from designation_details dd right join(select * from grades_details gd right join(select a.*,country_name from "
					+ "country co right join(select a.*,state_name from state s right join(select * from employee_personal_details epd, "
					+ "employee_official_details eod where epd.emp_per_id= ? and epd.emp_per_id=eod.emp_id) a on a.emp_state_id = s.state_id"
					+ ") a on a.emp_country_id = co.country_id) a on a.grade_id=gd.grade_id) a on a.designation_id=dd.designation_id"
					+ ") a on a.level_id=ld.level_id) a on a.depart_id=d.dept_id) a on a.wlocation_id=wl.wlocation_id" + ") a on a.org_id = od.org_id");
			pst.setInt(1, uF.parseToInt(strEmpIdReq));
			rs = pst.executeQuery();

			while (rs.next()) {

				if (strUserType != null
						&& (!strUserType.equalsIgnoreCase(EMPLOYEE) || !strUserType.equalsIgnoreCase(ARTICLE) || !strUserType.equalsIgnoreCase(CONSULTANT))) {

					String strEmpMName = "";
					if (flagMiddleName) {
						if (rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length() > 0) {
							strEmpMName = " " + rs.getString("emp_mname");
						}
					}

					request.setAttribute(TITLE, rs.getString("emp_fname") + strEmpMName + " " + rs.getString("emp_lname") + "'s Profile");
				}
				hmEmpProfile.put("EMP_ID", rs.getString("emp_per_id"));
				hmEmpProfile.put("EMPCODE", rs.getString("empcode"));

				String strEmpMName = "";
				if (flagMiddleName) {
					if (rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length() > 0) {
						strEmpMName = " " + rs.getString("emp_mname");
					}
				}
				// uF.showData(rs.getString("salutation"), "")+ " " +
				hmEmpProfile.put("NAME", rs.getString("emp_fname") + strEmpMName + " " + rs.getString("emp_lname"));

				hmEmpProfile.put("IMAGE", rs.getString("emp_image"));
				hmEmpProfile.put("EMAIL", rs.getString("emp_email"));
				hmEmpProfile.put("EMAIL_SEC", rs.getString("emp_email_sec"));
				hmEmpProfile.put("ORG_NAME", rs.getString("org_name"));

				if (rs.getString("emp_email_sec") != null && rs.getString("emp_email_sec").indexOf("@") > 0) {
					hmEmpProfile.put("EMP_EMAIL", rs.getString("emp_email_sec"));
				} else {
					hmEmpProfile.put("EMP_EMAIL", rs.getString("emp_email"));
				}

				hmEmpProfile.put("DESIGNATION_NAME", rs.getString("designation_name"));
				hmEmpProfile.put("LEVEL_NAME", rs.getString("level_name"));
				hmEmpProfile.put("GRADE_NAME", rs.getString("grade_name"));
				hmEmpProfile.put("GENDER", uF.getGender(rs.getString("emp_gender")));

				hmEmpProfile.put("DOB", uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, CF.getStrReportDateFormat()));

				hmEmpProfile.put("WLOCATION_NAME", rs.getString("wlocation_name"));
				hmEmpProfile.put("DEPARTMENT_NAME", rs.getString("dept_name"));

				// hmEmpProfile.put("SBU_NAME", rs.getString("service_name"));
				String joinDate = "";
				if (rs.getString("joining_date") != null) {
					joinDate = uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat());
				}
				hmEmpProfile.put("JOINING_DATE", joinDate);

				hmEmpProfile.put("EMP_TYPE", uF.stringMapping(rs.getString("emptype")));
				hmEmpProfile.put("EMPLOYMENT_TYPE", rs.getString("emp_status"));

				hmEmpProfile.put("SUPERVISOR_NAME", hmEmpName.get(rs.getString("supervisor_emp_id")));
				hmEmpProfile.put("HOD_NAME", hmEmpName.get(rs.getString("hod_emp_id")));
				hmEmpProfile.put("HR_NAME", hmEmpName.get(rs.getString("emp_hr")));

				String strEmpProfile = "";
				if (uF.parseToInt(rs.getString("emprofile")) > 0) {
					strEmpProfile = uF.showData(hmEmpResource.get(rs.getString("emprofile")), "");
				}
				hmEmpProfile.put("PROFILE", strEmpProfile);
				hmEmpProfile.put("PROFILE_ID", rs.getString("emprofile"));

				String strEmpContractor = "";
				if (uF.parseToInt(rs.getString("emp_contractor")) == 1) {
					strEmpContractor = "Employee";
				} else if (uF.parseToInt(rs.getString("emp_contractor")) == 2) {
					strEmpContractor = "Contractor";
				}
				hmEmpProfile.put("EMPLOYEE_CONTRACTOR", strEmpContractor);

			}
			rs.close();
			pst.close();

			request.setAttribute("hmEmpProfile", hmEmpProfile);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

		return hmEmpProfile;
	}
	public void getSectionName() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			// pst = con.prepareStatement(" select distinct(appraisal_id) as
			// appraisal_id,appraisal_system from appraisal_level_details where
			// appraisal_id in (select appraisal_id from
			// appraisal_reviewee_details where reviewee_id=?)");
			pst = con.prepareStatement(
					" select distinct(ald.appraisal_id) as appraisal_id,ald.appraisal_system,ad.appraisal_name,ad.from_date from appraisal_level_details ald,appraisal_details ad where ad.appraisal_details_id = ald.appraisal_id and ald.appraisal_id in (select appraisal_id from appraisal_reviewee_details where reviewee_id=?) order by appraisal_id");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			System.out.println("PST Test : " + pst.toString());
			rs = pst.executeQuery();
			String appId360 = null;
			String appId90 = null;
			Map<String, List<String>> hmDetails = new HashMap<String, List<String>>();
			while (rs.next()) {
				if (rs.getInt("appraisal_system") == 4) {
					appId90 = rs.getString("appraisal_id");
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("appraisal_name"));
					innerList.add(uF.getPreviousYear(rs.getDate("from_date")));
					hmDetails.put(appId90, innerList);
				} else {
					appId360 = rs.getString("appraisal_id");
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("appraisal_name"));
					innerList.add(uF.getPreviousYear(rs.getDate("from_date")));
					hmDetails.put(appId360, innerList);
				}
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
					appId90 = rs.getString("appraisal_id");
				}

			}
			rs.close();
			pst.close();
//			System.out.println("appId90 : " + appId90);
//			System.out.println("appId360 : " + appId360);
//			System.out.println("hmDetails : " + hmDetails);

			Map<String, List<List<String>>> hmAppraisalSections = new HashMap<String, List<List<String>>>();
			pst = con.prepareStatement("select * from appraisal_main_level_details order by main_level_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				List<List<String>> sectionList = hmAppraisalSections.get(rs.getString("appraisal_id"));
				if (sectionList == null)
					sectionList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("main_level_id"));
				innerList.add(rs.getString("level_title"));
				sectionList.add(innerList);
				hmAppraisalSections.put(rs.getString("appraisal_id"), sectionList);
			}
			rs.close();
			pst.close();

			request.setAttribute("hmAppraisalSections", hmAppraisalSections);
			request.setAttribute("appId360", appId360);
			request.setAttribute("appId90", appId90);
			request.setAttribute("hmDetails", hmDetails);

			pst = con.prepareStatement("select * from appraisal_final_sattlement where emp_id = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			Map<String, Map<String, List<String>>> hmComment = new HashMap<String, Map<String, List<String>>>();
			List<String> aloverAllScore = new ArrayList<String>();
			List<String> alareasOfStrength = new ArrayList<String>();
			List<String> alareasOfImprovement = new ArrayList<String>();
			while (rs.next()) {
				if (rs.getString("areas_of_strength") != null) {
//					System.out.println("BLOCK areas_of_strength");
					String strareasOfStrength = rs.getString("areas_of_strength").replace("N/A", "").replace("\n", "").replace("''", "");
					alareasOfStrength = Arrays.asList(strareasOfStrength.split("\n"));
				}
				if (rs.getString("areas_of_development") != null) {
//					System.out.println("BLOCK areas_of_development");
					String strareasOfImprovement = rs.getString("areas_of_development").replace("N/A", "").replace("\n", "").replace("''", "");
					alareasOfImprovement = Arrays.asList(strareasOfImprovement.split("\n"));
				}
				// Created by Dattatray Date : 19-07-21 Noted Committed Code
//				if (rs.getString("sattlement_comment") != null) {
//					System.out.println("BLOCK sattlement_comment");
//					String strOverAllScore = rs.getString("sattlement_comment").trim().replace("N/A", "").replace("\n", "").replace("''", "");
//					aloverAllScore = Arrays.asList(strOverAllScore.split("\n"));
//				}
				Map<String, List<String>> hmInner = hmComment.get(rs.getString("appraisal_id"));
				if (hmInner == null)
					hmInner = new HashMap<String, List<String>>();
				hmInner.put("areasOfStrength", alareasOfStrength);
				hmInner.put("areasOfImprovement", alareasOfImprovement);
				//hmInner.put("overallComments", aloverAllScore);// Created by Dattatray Date : 19-07-21 Noted Committed Code
				hmComment.put(rs.getString("appraisal_id"), hmInner);
			}
			rs.close();
			pst.close();
			
			// Start Dattatray Date : 19-07-21
			pst = con.prepareStatement("select * from (select section_id,section_comment,appraisal_id,user_id,emp_id,user_type_id from appraisal_question_answer  "
					+ " where appraisal_id = ? and emp_id=? and user_type_id = 2 group by section_id,section_comment,appraisal_id,user_id,user_type_id,emp_id) as a");
			pst.setInt(1, uF.parseToInt(appId90));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			System.out.println("pst : "+pst.toString());
			rs = pst.executeQuery();
			Map<String, Map<String, List<String>>> hmCommentKRA = new HashMap<String, Map<String, List<String>>>();
			List<String> aloverAllScore1 = new ArrayList<String>();
			while (rs.next()) {
				
				if (rs.getString("section_comment") != null) {
					String strOverAllScore = rs.getString("section_comment").trim().replace("N/A", "").replace("\n", "").replace("''", "");
					aloverAllScore1 = Arrays.asList(strOverAllScore.split("\n"));
				}
				Map<String, List<String>> hmInner = hmCommentKRA.get(rs.getString("appraisal_id"));
				if (hmInner == null)hmInner = new HashMap<String, List<String>>();
				hmInner.put("overallComments", aloverAllScore1);
				hmCommentKRA.put(rs.getString("appraisal_id"), hmInner);
			}
			rs.close();
			pst.close();
			// Start Dattatray Date : 19-07-21

//			System.out.println("hmComment : " + hmComment);
			request.setAttribute("hmComment", hmComment);
			request.setAttribute("hmCommentKRA", hmCommentKRA);// Created by Dattatray Date : 19-07-21
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	public void getAnnualAppraisalReport(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 03-02-2023===	
			/*sbQuery.append("select *,(marks*100/weightage) as average, (reviewer_marks*100/weightage) as reviewer_average from (select sum(aqa.marks) as marks, sum(aqa.weightage) as weightage, " +
					" sum(aqa.reviewer_marks) as reviewer_marks,aqa.user_type_id,aqa.emp_id,aqa.user_id,amld.main_level_id,section_weightage from appraisal_question_answer aqa , " +
					" appraisal_main_level_details amld where aqa.emp_id=? and aqa.weightage>0 and aqa.reviewer_or_appraiser=0 and aqa.section_id=amld.main_level_id"
					+ "	group by aqa.user_type_id,aqa.emp_id,aqa.section_id,aqa.user_id,amld.main_level_id) as a order by emp_id");*/
			
			sbQuery.append("select *,(marks*100/weightage) as average, (reviewer_marks*100/weightage) as reviewer_average from (select sum(aqa.marks) as marks, sum(aqa.weightage) as weightage, " +
					" sum(aqa.reviewer_marks) as reviewer_marks,aqa.user_type_id,aqa.emp_id,aqa.user_id,amld.main_level_id,section_weightage,score_calculation_basis from appraisal_question_answer aqa , " +
					" appraisal_main_level_details amld,appraisal_question_details aqd where aqa.emp_id=? and aqa.weightage>0 and aqa.reviewer_or_appraiser=0 and aqa.section_id=amld.main_level_id"
					+ " and aqa.appraisal_question_details_id=aqd.appraisal_question_details_id	" +
					" group by aqa.user_type_id,aqa.emp_id,aqa.section_id,aqa.user_id,amld.main_level_id,score_calculation_basis) as a order by emp_id");
		//===end parvez date: 03-02-2023===	
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			System.out.println("PST getAnnualAppraisalReport :::: " + pst.toString());
			rs = pst.executeQuery();
			Map<String, Map<String, String>> outerMp = new HashMap<String, Map<String, String>>();
			Map<String, String> hmSectionWeightage = new HashMap<String, String>();
			while (rs.next()) {

				Map<String, String> value = outerMp.get(rs.getString("main_level_id"));
				if (value == null) value = new HashMap<String, String>();
				value.put(rs.getString("user_type_id"), uF.formatIntoTwoDecimal(rs.getDouble("average")));
//				value.put(rs.getString("user_type_id") + "_REVIEWER", uF.formatIntoTwoDecimal(rs.getDouble("reviewer_average")));

			//===start parvez date: 03-02-2023===	
				value.put("ACTUAL_CAL_BASIS", rs.getString("score_calculation_basis"));
			//===end parvez date: 03-02-2023===	
				outerMp.put(rs.getString("main_level_id"), value);
				hmSectionWeightage.put(rs.getString("main_level_id"), rs.getString("section_weightage"));

			}
			
			rs.close();
			pst.close();
//			System.out.println("outerMp : " + outerMp);
//			System.out.println("hmSectionWeightage : " + hmSectionWeightage);
			
			request.setAttribute("hmSectionWeightage", hmSectionWeightage);
			request.setAttribute("outerMp", outerMp);
			
		//===start parvez date: 29-03-2023===	
			Map<String, String> hmFinalScore = new HashMap<String, String>();
			if(uF.parseToBoolean(hmFeatureStatus.get(F_HR_GHR_APPROVAL_FOR_FINAL_RATING_AND_COMMENT))){
				sbQuery = new StringBuilder();
				sbQuery.append("select distinct(user_type_id) as user_type_id,user_id,rfd.emp_id,reviewer_comment,reviewer_marks,section_weightage,main_level_id,rfd.appraisal_id from " +
						" reviewer_feedback_details rfd,appraisal_main_level_details amld where rfd.appraisal_id=amld.appraisal_id and emp_id=?");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				rs = pst.executeQuery();
				while (rs.next()) {
					hmFinalScore.put(rs.getString("appraisal_id"), rs.getString("reviewer_marks"));
				}
				rs.close();
				pst.close();
			}
		//===end parvez date: 29-03-2023===	
			
			pst = con.prepareStatement(" select distinct(appraisal_id) as appraisal_id,appraisal_system from appraisal_level_details where appraisal_id in (select appraisal_id from appraisal_reviewee_details where reviewee_id=?) order by appraisal_id");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			String appId360 = null;
			String appId90 = null;
			while (rs.next()) {
				if (rs.getInt("appraisal_system") == 4) {
					appId90 = rs.getString("appraisal_id");
				} else {
					appId360 = rs.getString("appraisal_id");
				}
				
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
					appId90 = rs.getString("appraisal_id");
				}
			}
			rs.close();
			pst.close();
			
			Map<String, List<List<String>>> hmAppraisalSections = new HashMap<String, List<List<String>>>();
			pst = con.prepareStatement("select * from appraisal_main_level_details order by main_level_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				List<List<String>> sectionList = hmAppraisalSections.get(rs.getString("appraisal_id"));
				if (sectionList == null)
					sectionList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("main_level_id"));
				innerList.add(rs.getString("level_title"));
				sectionList.add(innerList);
				hmAppraisalSections.put(rs.getString("appraisal_id"), sectionList);
			}
			rs.close();
			pst.close();

			List<String> memberList360 = (List<String>) request.getAttribute("memberList");
			List<String> memberList90 = (List<String>) request.getAttribute("memberList90");
//			System.out.println("memberList360 11 : " + memberList360);
//			System.out.println("memberList90 22: " + memberList90);
			List<List<String>> section360List = hmAppraisalSections.get(appId360);
			double result = 0.0;
			double result360 = 0.0;
			double total360 = 0.0;
			String actualCalDaysTotal360 = null;	//added by parvez date: 02-03-2023===
			Map<String, String> hmfinalData = new HashMap<String, String>();
			if (section360List != null) {
				for (int i = 0; i < section360List.size(); i++) {
					List<String> innerList = section360List.get(i);
					Map<String, String> value = outerMp.get(innerList.get(0).trim());
					if (value == null) value = new HashMap<String, String>();
					double total = 0.0f;
					int memCnt = 0;
					for (int j = 0; memberList360 != null && !memberList360.isEmpty() && j < memberList360.size(); j++) {
						if (uF.parseToInt(memberList360.get(j)) != 3 && uF.parseToDouble(value.get(memberList360.get(j).trim()))>0) {
							total += uF.parseToDouble(value.get(memberList360.get(j).trim()));
							memCnt++;
						}
					}
					if (memCnt > 0) {
						result = (total / memCnt);
						result360 += (result*uF.parseToInt(hmSectionWeightage.get(innerList.get(0)+"")))/100;
//						System.out.println("result360 : " + result360);
					}
				//===start parvez date 02-03-2023===	
					actualCalDaysTotal360 = value.get("ACTUAL_CAL_BASIS");
				//===end parvez date: 02-03-2023===	
				}
//				System.out.println("Final result : " + result360);
				total360 = uF.parseToDouble(uF.formatIntoOneDecimal(result360));
//				System.out.println("total360 : " + total360);
			}

			
			
			/*if (appId90 != null) {
				for (int j = 0; memberList90 != null && !memberList90.isEmpty() && j < memberList90.size(); j++) {
					if (uF.parseToInt(memberList90.get(j).trim()) == 3) {
						continue;
					}
					StringBuilder builder = new StringBuilder();
					builder.append("UPDATE appraisal_reviewee_details SET ");
					
					if (uF.parseToInt(memberList90.get(j)) == 2) {
						builder.append(" supervisor_weightage=70");
					}
					if (uF.parseToInt(memberList90.get(j)) == 4) {
						builder.append(" peer_weightage=0");
					}
					if (uF.parseToInt(memberList90.get(j)) == 5) {
						builder.append(" ceo_weightage=0");
					}
					if (uF.parseToInt(memberList90.get(j)) == 6) {
						builder.append(" subordinate_weightage=0");
					}
					if (uF.parseToInt(memberList90.get(j)) == 7) {
						builder.append(" hr_weightage=0");
					}
					if (uF.parseToInt(memberList90.get(j)) == 8) {
						builder.append(" grand_supervisor_weightage=30");
					}
					if (uF.parseToInt(memberList90.get(j)) == 10) {
						builder.append(" other_weightage=0");
					}
					if (uF.parseToInt(memberList90.get(j)) == 14) {
						builder.append(" other_peer_weightage=0");
					}
					if (uF.parseToInt(memberList90.get(j)) == 13) {
						builder.append(" hod_weightage=0");
					}
					builder.append(" where appraisal_id=?");
					pst = con.prepareStatement(builder.toString());
					pst.setInt(1, uF.parseToInt(appId90));
					pst.executeUpdate();
					pst.close();
					
				}
			}*/
			
			pst = con.prepareStatement("select reviewee_id,subordinate_weightage,peer_weightage,other_peer_weightage,supervisor_weightage,grand_supervisor_weightage,hod_weightage,ceo_weightage,hr_weightage,other_weightage " + 
					" from appraisal_reviewee_details where appraisal_id = ? and reviewee_id=?");
			pst.setInt(1, uF.parseToInt(appId90));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
//			System.out.println("PST === > "+pst.toString());
			rs = pst.executeQuery();
			Map<String, String> hmKRA90W = new HashMap<String, String>();
			while (rs.next()) {
				if (uF.parseToInt(rs.getString("supervisor_weightage")) >0) {
					hmKRA90W.put("2", rs.getString("supervisor_weightage"));
				}
				if (uF.parseToInt(rs.getString("peer_weightage")) >0) {
					hmKRA90W.put("4", rs.getString("peer_weightage"));
				}
				if (uF.parseToInt(rs.getString("ceo_weightage")) >0) {
					hmKRA90W.put("5", rs.getString("ceo_weightage"));
				}
				if (uF.parseToInt(rs.getString("subordinate_weightage")) >0) {
					hmKRA90W.put("6", rs.getString("subordinate_weightage"));
				}
				if (uF.parseToInt(rs.getString("hr_weightage")) >0) {
					hmKRA90W.put("7", rs.getString("hr_weightage"));
				}
				if (uF.parseToInt(rs.getString("grand_supervisor_weightage")) >0) {
					hmKRA90W.put("8", rs.getString("grand_supervisor_weightage"));
				}
				if (uF.parseToInt(rs.getString("other_weightage")) >0) {
					hmKRA90W.put("10", rs.getString("other_weightage"));
				}
				if (uF.parseToInt(rs.getString("hod_weightage")) >0) {
					hmKRA90W.put("13", rs.getString("hod_weightage"));
				}
				if (uF.parseToInt(rs.getString("other_peer_weightage")) >0) {
					hmKRA90W.put("14", rs.getString("other_peer_weightage"));
				}
			}
			rs.close();
			pst.close();
//			System.out.println("hmKRA90W : "+hmKRA90W);

			request.setAttribute("hmKRA90W", hmKRA90W);
			List<List<String>> section90List = hmAppraisalSections.get(appId90);
			
//			System.out.println("section90List ===>> " + section90List);
			double result90 = 0.0;
			double resultKRA90 = 0.0;
			double totalKRA90 = 0.0;
			String actualCalDaysTotalKRA90 = null; 	//added by parvez date: 02-03-2023
			if (section90List != null) {
				for (int i = 0; i < section90List.size(); i++) {
					List<String> innerList = section90List.get(i);
					Map<String, String> value = outerMp.get(innerList.get(0).trim());
					if (value == null) value = new HashMap<String, String>();
//					System.out.println("value ===>> " + value);
					double total = 0.0f;
					int memCnt = 0;
//					System.out.println("value: " + value);
					for (int j = 0; memberList90 != null && !memberList90.isEmpty() && j < memberList90.size(); j++) {
						
						if (uF.parseToInt(memberList90.get(j)) != 3 && uF.parseToDouble(value.get(memberList90.get(j).trim()))>0) {
							total += (uF.parseToDouble(value.get(memberList90.get(j).trim()))*uF.parseToInt(hmKRA90W.get(memberList90.get(j))))/100;
							memCnt++;
//							System.out.println("total: " + total + " -- memCnt ===>> " + memCnt);
						}
					}
					if (memCnt > 0) {
						result90 = total;
						resultKRA90 += result90;
					}
				//===start parvez date: 02-03-2023===	
					actualCalDaysTotalKRA90 = value.get("ACTUAL_CAL_BASIS");
				//===end parvez date: 02-03-2023===	
				}
				totalKRA90 = uF.parseToDouble(uF.formatIntoOneDecimal(resultKRA90));
				
//				System.out.println("total90 : " + totalKRA90);
			}
			
		//===start parvez date: 02-03-2023===	
			hmfinalData.put(appId360+"_ACTUAL_CAL_BASIS", actualCalDaysTotal360);
			hmfinalData.put(appId90+"_ACTUAL_CAL_BASIS", actualCalDaysTotalKRA90);
		//===end parvez date: 02-03-2023===	
			
			/*hmfinalData.put(appId360, total360 > 0 ? total360 + "" : "0");
			hmfinalData.put(appId90, totalKRA90 > 0 ? totalKRA90 + "" : "0");*/
			if(hmFinalScore!=null && (uF.parseToInt(hmFinalScore.get(appId90))>0 || uF.parseToInt(hmFinalScore.get(appId360))>0)){
				hmfinalData.put(appId360, hmFinalScore.get(appId360));
				hmfinalData.put(appId90, hmFinalScore.get(appId90));
			} else{
				hmfinalData.put(appId360, total360 > 0 ? total360 + "" : "0");
				hmfinalData.put(appId90, totalKRA90 > 0 ? totalKRA90 + "" : "0");
			}
			request.setAttribute("hmfinalData", hmfinalData);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private Map<String, String> getOrientationMember() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> orientationMemberMp = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMemberMp.put(rs.getString("member_id"), rs.getString("member_name"));
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
		return orientationMemberMp;
	}

	public HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	public String getStrOrgId() {
		return strOrgId;
	}
	public void setStrOrgId(String strOrgId) {
		this.strOrgId = strOrgId;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}
	
}