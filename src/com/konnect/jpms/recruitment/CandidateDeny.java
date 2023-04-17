package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.requsitions.UpdateRequest;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CandidateDeny extends ActionSupport implements ServletRequestAware,
		IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strSessionEmpId = null;

	private CommonFunctions CF;
	private static Logger log = Logger.getLogger(UpdateRequest.class);

	String strLeaveTypeId;
	String strEmpId;
	String strNod;
	String candidate_deny_reason;



	public String getCandidate_deny_reason() {
		return candidate_deny_reason;
	}

	public void setCandidate_deny_reason(String candidate_deny_reason) {
		this.candidate_deny_reason = candidate_deny_reason;
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
		String strId = (String) request.getParameter("RID");
		String requestDeny = (String) request.getParameter("requestDeny");
	
		if (requestDeny != null && requestDeny.equals("popup")) {
			request.setAttribute("strStatus", strStatus);
			request.setAttribute("strId", strId);
			return SUCCESS;
		} else if (requestDeny != null && requestDeny.equals("view")) {
		
			request.setAttribute("strStatus", strStatus);
			request.setAttribute("strId", strId);
			request.setAttribute("view", requestDeny);
			viewReason(strStatus, strId);
			return SUCCESS;
		}else if (requestDeny != null && requestDeny.equals("RequestDeny")) {
		
			updateRequest(strStatus, strId);
			return UPDATE;
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
					String query = "update candidate_personal_details set application_status=?,application_status_reason=? where emp_per_id=?";
					pst = con.prepareStatement(query);
					pst.setInt(1, uF.parseToInt(strStatus));
					pst.setString(2, candidate_deny_reason);
					pst.setInt(3, uF.parseToInt(strId));
					pst.execute();
					pst.close();
					
					getStatusMessage(uF.parseToInt(strStatus));

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					
					db.closeStatements(pst);
					db.closeConnection(con);
				}
			}
		}
	}
	
	private void viewReason(String strStatus, String strId) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String job_deny_reason = "";

		if (strId != null && strStatus != null) {


			int intStatus = uF.parseToInt(strStatus);


			if (intStatus == -1) {

				try {
					con = db.makeConnection(con);
					String query = "select application_status_reason from candidate_personal_details where emp_per_id=?";
					pst = con.prepareStatement(query);
					pst.setInt(1, uF.parseToInt(strId));
					pst.setInt(2, uF.parseToInt(strStatus));
					rs = pst.executeQuery();
					while (rs.next()) {
						job_deny_reason = rs.getString(1);
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
		}
		request.setAttribute("denyReason", candidate_deny_reason);

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
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
			
			
			break;

		case 0:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Pending\" src=\""
					+ request.getContextPath()
					+ "/images1/icons/pending.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\"></i>");
			break;

		case 1:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Approved\" src=\""
					+ request.getContextPath()
					+ "/images1/icons/approved.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
			
			break;

		case 2:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Pulled\" src=\""
					+ request.getContextPath()
					+ "/images1/icons/pullout.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\"></i>");
			
			break;

		case 3:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Submited\" src=\""
					+ request.getContextPath()
					+ "/images1/icons/re_submit.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>");
			
			
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
}
