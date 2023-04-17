package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class JobDeny extends ActionSupport implements ServletRequestAware,
		IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strSessionEmpId = null;

	private CommonFunctions CF;

	private String strLeaveTypeId;
	private String strEmpId;
	private String strNod;
	private String job_deny_reason;

	private String orgID;
	private String wlocID;
	private String desigID;
	private String checkStatus;
	private String fdate;
	private String tdate;
	private  String strId;
	private String frmPage;
	private String currUserType;
	public String getJob_deny_reason() {
		return job_deny_reason;
	}

	public void setJob_deny_reason(String job_deny_reason) {
		this.job_deny_reason = job_deny_reason;
	}

	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		// S RID strStatus strId
		String strStatus = (String) request.getParameter("ST");
		strId = (String) request.getParameter("RID");
		String requestDeny = (String) request.getParameter("requestDeny");


		if (requestDeny != null && requestDeny.equals("popup")) {
//			System.out.println("strStatus popup ===> " + strStatus);
			request.setAttribute("strStatus", strStatus);
			request.setAttribute("strId", strId);
			return SUCCESS;
		} else if (requestDeny != null && requestDeny.equals("view")) {
//			System.out.println("strStatus view ===> " + strStatus);
			request.setAttribute("strStatus", strStatus);
			request.setAttribute("strId", strId);
			request.setAttribute("view", requestDeny);
			viewReason(strStatus, strId);
			return SUCCESS;
		}else if (requestDeny != null && requestDeny.equals("JobDeny")) {
			System.out.println("requestDeny ===>> " + requestDeny);
			updateRequest(strStatus, strId);
			if (getFrmPage() != null && getFrmPage().equals("HRDash")) {
				System.out.println("hrupdate ===>> ");
				return "hrupdate";
			} else {
				System.out.println("UPDATE ===>> ");
				return UPDATE;
			}
		}

		return SUCCESS;

	}

	private void updateRequest(String strStatus, String strId) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		if (strId != null && strStatus != null) {

			int intStatus = uF.parseToInt(strStatus);

			if (intStatus == -1) {

				try {
					con = db.makeConnection(con);
					String query = "update recruitment_details set job_approval_status=?,job_deny_reason=?,job_approval_date=? where recruitment_id=?";
					pst = con.prepareStatement(query);
					pst.setInt(1, uF.parseToInt(strStatus));
					pst.setString(2, job_deny_reason);
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(4, uF.parseToInt(strId));
					pst.execute();
					pst.close();

					getStatusMessage(uF.parseToInt(strStatus));

					String jobCodeName = getJobCodeNameById(con, uF, strId);
					session.setAttribute(MESSAGE, SUCCESSM+""+jobCodeName+" job profile has been denied successfully."+END);
					
					sendMail(strId);
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					
					db.closeStatements(pst);
					db.closeConnection(con);
				}
			}
		}
	}
	
	
	private String getJobCodeNameById(Connection con, UtilityFunctions uF, String recruitId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		String requirementName = null;
		try {
				pst = con.prepareStatement("select job_code,designation_id from recruitment_details where recruitment_id = ?");
				pst.setInt(1, uF.parseToInt(recruitId));
				rst = pst.executeQuery();
//				Map<String, String> hmDesignation = CF.getDesigMap(con);
				String desigId = null;
				while (rst.next()) {
					desigId = rst.getString("designation_id");
					requirementName = rst.getString("job_code");
				}
				rst.close();
				pst.close();
//				designationName = hmDesignation.get(desigId);
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
		return requirementName;
	}
	
	public void sendMail(String strId) {
//		System.out.println("++++++Thread example UpdateADRRequest++++++");
//		getRecruitmentDetails();

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);

		try{
			con = db.makeConnection(con);
			Map<String, String> hmRecruitmentData = CF.getRecruitmentDetails(con, uF, CF, request, strId);
			
			String strAddedBy = null;
			String queryy = "select added_by from recruitment_details where recruitment_id = ?";
			pst = con.prepareStatement(queryy);
			pst.setInt(1, uF.parseToInt(strId));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				strAddedBy = rst.getString("added_by");
			}
			rst.close();
			pst.close();
			
			if(strAddedBy != null && !strAddedBy.equals("")){
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_JOB_PROFILE_DENY, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrEmpId(strAddedBy);
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
		
				nF.setStrRecruitmentDesignation(hmRecruitmentData.get("DESIG_NAME"));
				nF.setStrRecruitmentGrade(hmRecruitmentData.get("GRADE_NAME"));
				nF.setStrRecruitmentLevel(hmRecruitmentData.get("LEVEL_NAME"));
				nF.setStrRecruitmentPosition(hmRecruitmentData.get("POSITIONS"));
				nF.setStrRecruitmentWLocation(hmRecruitmentData.get("WLOC_NAME"));
//				nF.setStrRecruitmentProfile(getServices());
				nF.setStrRecruitmentSkill(hmRecruitmentData.get("SKILLS_NAME"));
				nF.setEmailTemplate(true);		
				nF.sendNotifications();
			}
			
//		Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);
//		String empWlocation = hmEmpWLocation.get(strSessionEmpId);
////		String panel_employee_id = "";
//		pst = con.prepareStatement("select emp_per_id from employee_personal_details epd, employee_official_details eod, user_details ud " +
//				"where epd.emp_per_id = eod.emp_id and epd.emp_per_id = ud.emp_id and ud.usertype_id = 7 and eod.wlocation_id = ?");
//		pst.setInt(1, uF.parseToInt(empWlocation));
//		rst = pst.executeQuery();
////		System.out.println("pst ===> "+pst);
//		while (rst.next()) {
//			//Map<String, String> hmEmpInner = hmEmpInfo.get(rst.getString("emp_per_id"));
//
//			if(rst.getString("emp_per_id") != null && !rst.getString("emp_per_id").equals("")){
//				String strDomain = request.getServerName().split("\\.")[0];
//				Notifications nF = new Notifications(N_RECRUITMENT_DENY, CF);
//				nF.setDomain(strDomain);
//				nF.setStrEmpId(rst.getString("emp_per_id"));
//				nF.setStrHostAddress(CF.getStrEmailLocalHost());
//				nF.setStrContextPath(request.getContextPath());
//		
//				nF.setStrRecruitmentDesignation(getDesignation_name());
//				nF.setStrRecruitmentGrade(getGrade_name());
//				nF.setStrRecruitmentLevel(getLevel_name());
//				nF.setStrRecruitmentPosition(getPositions());
//				nF.setStrRecruitmentWLocation(getLocation_name());
//				nF.setStrRecruitmentProfile(getServices());
//				nF.setStrRecruitmentSkill(getSkills_name());
//		
//				nF.sendNotifications();
//			} 
//		}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
//	private void getRecruitmentDetails() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rst = null;
//		Database db = new Database();
//		db.setRequest(request);
//
//		UtilityFunctions uF = new UtilityFunctions();
//
//		con = db.makeConnection(con);
//
//		try {
////			System.out.println("in getRecruitmentDetails strid " + getStrId());
//
//			StringBuilder strQuery = new StringBuilder();
//
//			strQuery.append("select d.designation_code,d.designation_name,g.grade_code,g.grade_name,w.wlocation_name,r.no_position,"
//					+ "r.job_code,s.service_name,r.added_by,l.level_code,l.level_name,r.skills from recruitment_details r,grades_details g,"
//					+ "work_location_info w,designation_details d,services s,department_info di,employee_personal_details e,level_details l "
//					+ "where r.grade_id=g.grade_id and r.wlocation=w.wlocation_id and r.designation_id=d.designation_id and r.added_by=e.emp_per_id "
//					+ "and r.services=s.service_id and r.dept_id=di.dept_id and r.level_id=l.level_id and r.recruitment_id=?");
//
//			pst = con.prepareStatement(strQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrId()));
//			rst = pst.executeQuery();
////			System.out.println("new Date ===> " + new Date());
//			while (rst.next()) {
////				System.out.println("in rst.next");
//				designation_name = "[" + rst.getString(1) + "] "
//						+ rst.getString(2);
//				grade_name = "[" + rst.getString(3) + "] " + rst.getString(4);
//				location_name = rst.getString(5);
//				positions = rst.getString(6);
//				services = rst.getString(8);
//				Level_name = "[" + rst.getString(10) + "] " + rst.getString(11);
//				skills_name = rst.getString(12);
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rst);
//			db.closeConnection(con);
//		}
//
//	}
	
	
	private void viewReason(String strStatus, String strId) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String job_deny_reason = "";

		if (strId != null && strStatus != null) {
			int intStatus = uF.parseToInt(strStatus);
			if (intStatus == -1) {

				try {
					con = db.makeConnection(con);
					String query = "select job_deny_reason from recruitment_details where recruitment_id=? and job_approval_status=?";
					pst = con.prepareStatement(query);
					pst.setInt(1, uF.parseToInt(strId));
					pst.setInt(2, uF.parseToInt(strStatus));
					rst = pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
					while (rst.next()) {
						job_deny_reason = rst.getString(1);
					}
					rst.close();
					pst.close();

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					db.closeResultSet(rst);
					db.closeStatements(pst);
					db.closeConnection(con);
				}
			}
		}
		request.setAttribute("denyReason", job_deny_reason);

	}
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	public void getStatusMessage(int nStatus) {

		switch (nStatus) {

		case -1:
			 /*request.setAttribute("STATUS_MSG", "<img title=\"Denied\" src=\""
					+ request.getContextPath()
					+ "/images1/icons/denied.png\" border=\"0\">");*/
			 
			 request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
			break;

		case 0:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Pending\" src=\""
					+ request.getContextPath()
					+ "/images1/icons/pending.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Pending\"></i>");
			
			break;

		case 1:
//			request.setAttribute("STATUS_MSG", "<img title=\"Approved\" src=\""
//					+ request.getContextPath()
//					+ "/images1/icons/approved.png\" border=\"0\">");
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
			
			break;

		case 2:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Pulled\" src=\""
					+ request.getContextPath()
					+ "/images1/icons/pullout.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" title=\"Pulled\"></i>");
			
			
			break;

		case 3:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Submited\" src=\""
					+ request.getContextPath()
					+ "/images1/icons/re_submit.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Submited\"></i>");
			
			break;
		}
	}

	public void getNotificationStatusMessage(int nStatus, String str) {

		switch (nStatus) {

		case 0:
			request.setAttribute(
					"STATUS_MSG",
					"<img width=\"20px\" title=\"Pending\" src=\""
							+ request.getContextPath()
							+ "/images1/"
							+ (("E".equalsIgnoreCase(str)) ? "mail_disbl.png"
									: "mob_disbl.png")
							+ "\" border=\"0\">&nbsp;");
			break;

		case 1:
			request.setAttribute(
					"STATUS_MSG",
					"<img width=\"20px\" title=\"Approved\" src=\""
							+ request.getContextPath()
							+ "/images1/"
							+ (("E".equalsIgnoreCase(str)) ? "mail_enbl.png"
									: "mob_enbl.png")
							+ "\" border=\"0\">&nbsp;");
			break;
		}
	}

	public String getOrgID() {
		return orgID;
	}

	public void setOrgID(String orgID) {
		this.orgID = orgID;
	}

	public String getWlocID() {
		return wlocID;
	}

	public void setWlocID(String wlocID) {
		this.wlocID = wlocID;
	}

	public String getDesigID() {
		return desigID;
	}

	public void setDesigID(String desigID) {
		this.desigID = desigID;
	}

	public String getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}

	public String getFdate() {
		return fdate;
	}

	public void setFdate(String fdate) {
		this.fdate = fdate;
	}

	public String getTdate() {
		return tdate;
	}

	public void setTdate(String tdate) {
		this.tdate = tdate;
	}
	
	String positions;
	String Level_name;
	String designation_name;
	String grade_name;
	String location_name;
	String skills_name;
	String services;

	public String getPositions() {
		return positions;
	}

	public void setPositions(String positions) {
		this.positions = positions;
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

	public String getServices() {
		return services;
	}

	public void setServices(String services) {
		this.services = services;
	}

	public String getStrId() {
		return strId;
	}

	public void setStrId(String strId) {
		this.strId = strId;
	}

	public String getFrmPage() {
		return frmPage;
	}

	public void setFrmPage(String frmPage) {
		this.frmPage = frmPage;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}
	
}
