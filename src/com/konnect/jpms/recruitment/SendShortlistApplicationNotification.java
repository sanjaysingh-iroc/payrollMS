package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CandidateNotifications;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SendShortlistApplicationNotification extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	HttpSession session;
	public CommonFunctions CF;
	String strUserType = null;
	String strSessionEmpId = null;
	String recruitId;

	public String execute() throws Exception {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		// System.out.println("strId ===> "+strId);
		sendMail();
		return SUCCESS;
	}

	String emp_id;
	String positions;
	String job_code;
	String Level_name;
	String designation_name;
	String grade_name;
	String location_name;
	String skills_name;
	String services;
	String min_exp;
	String max_exp;
	String min_education;
	String job_title;
	String recruiter_name;
	String org_id;

	public void sendMail() {

		Connection con = null;
		ResultSet rst = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmCandiInfo = CF.getCandiInfoMap(con, false);
			Map<String, String> hmOrgMap = CF.getOrgName(con);
			// System.out.println("Req id is ========= "+getRecruitId());
			String usrType = null;
			pst = con.prepareStatement("select usertype_id from user_details where emp_id = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				usrType = rst.getString("usertype_id");
			}
			rst.close();
			pst.close();

			// Map<String, String> hmUserType = CF.getUserTypeMap(con);
			// String usrType = hmUserType.get(strSessionEmpId);
			// System.out.println("hmUserType ===> " + hmUserType);
			// System.out.println("usrType ===> " + usrType + "strSessionEmpId
			// ===> " + strSessionEmpId);

			String new_application_id = "";
			// Created Dattatray Date : 10-08-21 Note : added candidate_id
			pst = con.prepareStatement(
					"select candi_application_deatils_id,candidate_id from candidate_application_details where recruitment_id = ? and application_status=2 and candidate_final_status=0 and send_notification_status=0");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rst = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
//				new_application_id += "," + rst.getString("candi_application_deatils_id");
				new_application_id += "," + rst.getString("candidate_id");// Created Dattatray Date : 10-08-21 Note : added candidate_id
			}
			rst.close();
			pst.close();

			pst = con.prepareStatement(
					"update candidate_application_details set send_notification_status=1 where recruitment_id = ? and application_status=2 and candidate_final_status=0");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.executeUpdate();
			pst.close();

			// System.out.println("new_application_id ===>
			// "+new_application_id);
			String tmpsltempids = new_application_id;
			if (new_application_id.length() > 1) {
				tmpsltempids = new_application_id.substring(1, new_application_id.length());
			}
//			System.out.println("hmCandiInfo : "+hmCandiInfo);
			List<String> applicationIdsLst = Arrays.asList(tmpsltempids.split(","));
//			System.out.println("applicationIdsLst : "+applicationIdsLst);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);// Created Dattatray Date : 09-08-21
			for (int i = 0; applicationIdsLst != null && i < applicationIdsLst.size(); i++) {
				Map<String, String> hmCandiInner = hmCandiInfo.get(applicationIdsLst.get(i));
				
//				System.out.println("hmCandiInner : "+hmCandiInner);
				StringBuilder strQuery = new StringBuilder();

				
				strQuery.append("select d.designation_code,d.designation_name,g.grade_code,g.grade_name,w.wlocation_name,r.no_position,"
						+ "r.job_code,r.added_by,l.level_code,l.level_name,r.skills,r.min_exp,r.max_exp,r.min_education,r.job_title,r.hiring_manager,r.org_id"
						+ " from recruitment_details r,grades_details g,work_location_info w,designation_details d,employee_personal_details e," +
						"level_details l where r.grade_id=g.grade_id and r.wlocation=w.wlocation_id and r.designation_id=d.designation_id and r.added_by=e.emp_per_id "
						+ "and r.level_id=l.level_id and r.recruitment_id=?");//Created by Dattatray Date : 09-08-2021 Note:  removed and r.services=s.service_id from query and added r.job_title,r.hiring_manager
				pst = con.prepareStatement(strQuery.toString());
				pst.setInt(1, uF.parseToInt(getRecruitId()));
				System.out.println("pst : "+pst.toString());
				rst = pst.executeQuery();
				// System.out.println("new Date ===> " + new Date());
				while (rst.next()) {
					designation_name = "[" + rst.getString("designation_code") + "] " + rst.getString("designation_name");
					grade_name = "[" + rst.getString("grade_code") + "] " + rst.getString("grade_name");
					location_name = rst.getString("wlocation_name");
					positions = rst.getString("no_position");
					job_code = rst.getString("job_code");
					emp_id = rst.getString("added_by");
					Level_name = "[" + rst.getString("level_code") + "] " + rst.getString("level_name");
					skills_name = rst.getString("skills");
					min_exp = rst.getString("min_exp");
					max_exp = rst.getString("max_exp");
					min_education = rst.getString("min_education");
					// Start Dattatray Date : 09-08-21
					job_title = rst.getString("job_title");
					recruiter_name = uF.showData(getAppendData(rst.getString("hiring_manager"), hmEmpName), "");
					org_id = rst.getString("org_id");
					// ENd Dattatray 
				}
				rst.close();
				pst.close();
				System.out.println("job_title : "+job_title);
				System.out.println("recruiter_name : "+recruiter_name);
				String strDomain = request.getServerName().split("\\.")[0];
				CandidateNotifications nF = new CandidateNotifications(N_APPLICATION_SHORTLIST, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				// System.out.println("applicationIdsLst.get(i) is =========
				// "+applicationIdsLst.get(i));
				
				nF.setStrRecruitmentId(getRecruitId());
				// System.out.println("CF.getStrEmailLocalHost() is =========
				// "+CF.getStrEmailLocalHost());
				// System.out.println("request.getContextPath() is =========
				// "+request.getContextPath());
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrEmpId(applicationIdsLst.get(i));//Created By Dattatray Date:30-10-21
				nF.setStrRecruitmentDesignation(designation_name);
				nF.setStrRecruitmentGrade(grade_name);
				nF.setStrRecruitmentLevel(Level_name);
				nF.setStrRecruitmentPosition(positions);
				nF.setStrRecruitmentWLocation(location_name);
				nF.setStrRecruitmentSkill(skills_name);
				nF.setStrCandiFname(hmCandiInner != null ? hmCandiInner.get("FNAME") : "");
				nF.setStrCandiLname(hmCandiInner != null ? hmCandiInner.get("LNAME") : "");
				// Start Dattatray Date : 09-08-21
				nF.setStrJobTitle(job_title);
				nF.setStrRecruiterName(recruiter_name);
				nF.setStrLegalEntityName(hmOrgMap.get(org_id));	// Created By Dattatray Date : 01-11-21
//				System.out.println("legal entity "+hmOrgMap.get(org_id));
				// End Dattatray Date : 09-08-21
				nF.sendNotifications();
			}

			Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);
			String empWlocation = hmEmpWLocation.get(strSessionEmpId);

			if (usrType != null && usrType.equals("9")) {
				// getRecruitmentDetails();
				Map<String, String> hmRecruitmentData = CF.getRecruitmentDetails(con, uF, CF, request, getRecruitId());
				// System.out.println("in usrType ===> " + usrType);
				pst = con.prepareStatement("select emp_per_id from employee_personal_details epd, employee_official_details eod, user_details ud "
						+ "where epd.emp_per_id = eod.emp_id and epd.is_alive=true and epd.emp_per_id = ud.emp_id and ud.usertype_id = 7 and eod.wlocation_id = ?");
				pst.setInt(1, uF.parseToInt(empWlocation));
				rst = pst.executeQuery();
				// System.out.println("new Date ===> " + new Date());
				// System.out.println("pst ===> "+pst);
				while (rst.next()) {
					// System.out.println("usrId ===> " +
					// rst.getString("emp_per_id"));
					if (rst.getString("emp_per_id") != null && !rst.getString("emp_per_id").equals("")) {
						// System.out.println("usrId in ===> " +
						// rst.getString("emp_per_id"));
						for (int i = 0; applicationIdsLst != null && i < applicationIdsLst.size(); i++) {
							Map<String, String> hmCandiInner = hmCandiInfo.get(applicationIdsLst.get(i));
							String strDomain = request.getServerName().split("\\.")[0];
							Notifications nF = new Notifications(N_CANDI_SHORTLIST_FROM_CONSULTANT, CF);
							nF.setDomain(strDomain);
							nF.request = request;
							nF.setStrEmpId(rst.getString("emp_per_id"));
							nF.setStrHostAddress(CF.getStrEmailLocalHost());
							nF.setStrHostPort(CF.getStrHostPort());
							nF.setStrContextPath(request.getContextPath());

							nF.setStrRecruitmentDesignation(hmRecruitmentData.get("DESIG_NAME"));
							nF.setStrRecruitmentGrade(hmRecruitmentData.get("GRADE_NAME"));
							nF.setStrRecruitmentLevel(hmRecruitmentData.get("LEVEL_NAME"));
							nF.setStrRecruitmentPosition(hmRecruitmentData.get("POSITIONS"));
							nF.setStrRecruitmentWLocation(hmRecruitmentData.get("WLOC_NAME"));
							// nF.setStrRecruitmentProfile(getServices());
							nF.setStrRecruitmentSkill(hmRecruitmentData.get("SKILLS_NAME"));
							nF.setStrCandiFname(hmCandiInner.get("FNAME"));
							nF.setStrCandiLname(hmCandiInner.get("LNAME"));
							nF.setEmailTemplate(true);
							nF.sendNotifications();
						}
					}
				}
				rst.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	// Created Dattatray Date : 10-08-21
	private String getAppendData( String strID,  Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();
//		System.out.println("strID :: "+strID);
		if (strID != null && !strID.equals("") && !strID.isEmpty()) {
			if(strID.length()>0 && strID.substring(0, 1).equals(",") && strID.substring(strID.length()-1, strID.length()).equals(",")){
			strID = strID.substring(1, strID.length()-1);
			}
			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(mp.get(temp[i].trim()));
					} else {
						sb.append(", " + mp.get(temp[i].trim()));
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
	// private void getRecruitmentDetails() {
	// Connection con = null;
	// PreparedStatement pst = null;
	// ResultSet rst = null;
	// Database db = new Database();
	// db.setRequest(request);
	//
	// UtilityFunctions uF = new UtilityFunctions();
	//
	// con = db.makeConnection(con);
	//
	// try {
	//// System.out.println("in getRecruitmentDetails strid " + getRecruitId());
	//
	// StringBuilder strQuery = new StringBuilder();
	//
	// strQuery.append("select
	// d.designation_code,d.designation_name,g.grade_code,g.grade_name,w.wlocation_name,r.no_position,"
	// +
	// "r.job_code,s.service_name,r.added_by,l.level_code,l.level_name,r.skills
	// from recruitment_details r,grades_details g,"
	// + "work_location_info w,designation_details d,services s,department_info
	// di,employee_personal_details e,level_details l "
	// + "where r.grade_id=g.grade_id and r.wlocation=w.wlocation_id and
	// r.designation_id=d.designation_id and r.added_by=e.emp_per_id "
	// + "and r.services=s.service_id and r.dept_id=di.dept_id and
	// r.level_id=l.level_id and r.recruitment_id=?");
	//
	// pst = con.prepareStatement(strQuery.toString());
	// pst.setInt(1, uF.parseToInt(getRecruitId()));
	// rst = pst.executeQuery();
	//// System.out.println("new Date ===> " + new Date());
	// while (rst.next()) {
	//// System.out.println("in rst.next");
	// designation_name = "[" + rst.getString(1) + "] "
	// + rst.getString(2);
	// grade_name = "[" + rst.getString(3) + "] " + rst.getString(4);
	// location_name = rst.getString(5);
	// positions = rst.getString(6);
	// job_code = rst.getString(7);
	// services = rst.getString(8);
	// emp_id = rst.getString(9);
	// Level_name = "[" + rst.getString(10) + "] " + rst.getString(11);
	// skills_name = rst.getString(12);
	// }
	//
	// } catch (SQLException e) {
	// e.printStackTrace();
	// } finally {
	//
	// db.closeStatements(pst);
	// db.closeResultSet(rst);
	// db.closeConnection(con);
	// }
	//
	// }

	// public Map<String, Map<String, String>> getCandiInfoMap(Connection con,
	// boolean isFamilyInfo) {
	// Map<String, Map<String, String>> hmCandiInfo = new HashMap<String,
	// Map<String, String>>();
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	// try {
	// Map<String, String> hmCandiInner = new HashMap<String, String>();
	// if(isFamilyInfo){
	// pst = con.prepareStatement("select * from candidate_family_members order
	// by emp_id");
	// rs = pst.executeQuery();
	//// System.out.println("new Date ===> " + new Date());
	// while(rs.next()){
	//
	// hmCandiInner = hmCandiInfo.get(rs.getString("emp_id"));
	// if(hmCandiInner==null)hmCandiInner=new HashMap<String, String>();
	//
	// hmCandiInner.put(rs.getString("member_type"),
	// rs.getString("member_name"));
	// hmCandiInfo.put(rs.getString("emp_id"), hmCandiInner);
	// }
	// }
	//
	// pst = con.prepareStatement("SELECT emp_per_id, emp_fname, emp_lname,
	// empcode, emp_image, emp_email, emp_date_of_birth, candidate_joining_date,
	// " +
	// "emp_gender, marital_status FROM candidate_personal_details order by
	// emp_per_id");
	// rs = pst.executeQuery();
	//// System.out.println("new Date ===> " + new Date());
	// while (rs.next()) {
	// if (rs.getInt("emp_per_id") < 0) {
	// continue;
	// }
	// hmCandiInner = hmCandiInfo.get(rs.getString("emp_per_id"));
	// if(hmCandiInner==null)hmCandiInner=new HashMap<String, String>();
	//
	// hmCandiInner.put("FNAME", rs.getString("emp_fname"));
	// hmCandiInner.put("LNAME", rs.getString("emp_lname"));
	// hmCandiInner.put("FULLNAME", rs.getString("emp_lname")+"
	// "+rs.getString("emp_lname"));
	// hmCandiInner.put("EMPCODE", rs.getString("empcode"));
	// hmCandiInner.put("IMAGE", rs.getString("emp_image"));
	// hmCandiInner.put("EMAIL", rs.getString("emp_email"));
	// hmCandiInner.put("DOB", rs.getString("emp_date_of_birth"));
	// hmCandiInner.put("JOINING_DATE", rs.getString("candidate_joining_date"));
	// hmCandiInner.put("GENDER", rs.getString("emp_gender"));
	// hmCandiInner.put("MARITAL_STATUS", rs.getString("marital_status"));
	//
	// hmCandiInfo.put(rs.getString("emp_per_id"), hmCandiInner);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// //log.error(e.getClass() + ": " + e.getMessage(), e);
	// }
	// return hmCandiInfo;
	// }

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getPositions() {
		return positions;
	}

	public void setPositions(String positions) {
		this.positions = positions;
	}

	public String getServices() {
		return services;
	}

	public void setServices(String services) {
		this.services = services;
	}

	public String getLevel_name() {
		return Level_name;
	}

	public void setLevel_name(String level_name) {
		Level_name = level_name;
	}

	public String getDesignation_name() {
		return designation_name;
	}

	public void setDesignation_name(String designation_name) {
		this.designation_name = designation_name;
	}

	public String getGrade_name() {
		return grade_name;
	}

	public void setGrade_name(String grade_name) {
		this.grade_name = grade_name;
	}

	public String getLocation_name() {
		return location_name;
	}

	public void setLocation_name(String location_name) {
		this.location_name = location_name;
	}

	public String getSkills_name() {
		return skills_name;
	}

	public void setSkills_name(String skills_name) {
		this.skills_name = skills_name;
	}

	public String getMin_exp() {
		return min_exp;
	}

	public void setMin_exp(String min_exp) {
		this.min_exp = min_exp;
	}

	public String getMax_exp() {
		return max_exp;
	}

	public void setMax_exp(String max_exp) {
		this.max_exp = max_exp;
	}

	public String getMin_education() {
		return min_education;
	}

	public void setMin_education(String min_education) {
		this.min_education = min_education;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getRecruiter_name() {
		return recruiter_name;
	}

	public void setRecruiter_name(String recruiter_name) {
		this.recruiter_name = recruiter_name;
	}

}