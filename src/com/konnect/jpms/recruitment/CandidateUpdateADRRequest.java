package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CandidateUpdateADRRequest extends ActionSupport implements
		ServletRequestAware, IStatements{

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strSessionEmpId = null;

	private CommonFunctions CF;
	private static Logger log = Logger.getLogger(CandidateUpdateADRRequest.class);

	String strLeaveTypeId;
	String strEmpId;
	String strNod;
	String req_deny_reason;
	String strId;

	public String getReq_deny_reason() {
		return req_deny_reason;
	}

	public void setReq_deny_reason(String req_deny_reason) {
		this.req_deny_reason = req_deny_reason;
	}

	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		// S RID
		String strStatus = (String) request.getParameter("S");
		strId = (String) request.getParameter("RID");

		updateRequest(strStatus, strId);

		return SUCCESS;

	}

	private void updateRequest(String strStatus, String strId) {
		Connection con = null;
		PreparedStatement pst = null;
		java.sql.ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		boolean flag = false;

		if (strId != null && strStatus != null) {


			int intStatus = uF.parseToInt(strStatus);

			switch (intStatus) {

			case 1:
				try {
//					System.out.println("in 1 strid");
					con = db.makeConnection(con);
					con.setAutoCommit(false);
			/*		int job_code_increment = 0;
					pst1 = con
							.prepareStatement("select max(job_code) from recruitment_details");
					rs = pst1.executeQuery();
					while (rs.next()) {
						job_code_increment = rs.getInt(1);
					}
					job_code_increment++;*/

					String query = "update candidate_personal_details set application_status=? where emp_per_id=?";
					pst = con.prepareStatement(query);
					pst.setInt(1, uF.parseToInt(strStatus));
					pst.setInt(2, uF.parseToInt(strId));
					pst.execute();
					pst.close();
					
					getStatusMessage(uF.parseToInt(strStatus));
					con.commit();

					flag = true;
				} catch (Exception e) {
					try {
						con.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				} finally {
					db.closeResultSet(rs);
					db.closeStatements(pst);
					db.closeConnection(con);
				}

				break;

			}
		}
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
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
			/*request.setAttribute("STATUS_MSG", "<img title=\"Approved\" src=\""
					+ request.getContextPath()
					+ "/images1/icons/approved.png\" border=\"0\">");*/
			
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
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Submited\" style=\"color:#f7ee1d\"></i>");
			
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

	String emp_id;
	String positions;
	String job_code;
	String Level_name;
	String designation_name;
	String grade_name;
	String location_name;
	String skills_name;
	String services;


	public String getStrId() {
		return strId;
	}

	public void setStrId(String strId) {
		this.strId = strId;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getPositions() {
		return positions;
	}

	public void setPositions(String positions) {
		this.positions = positions;
	}

	public String getJob_code() {
		return job_code;
	}

	public void setJob_code(String job_code) {
		this.job_code = job_code;
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



}
