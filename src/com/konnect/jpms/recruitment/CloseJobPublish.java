package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

public class CloseJobPublish extends ActionSupport implements
		ServletRequestAware, IStatements {

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
	String job_deny_reason;

	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		// S RID
		String strStatus = (String) request.getParameter("S");
		String strId = (String) request.getParameter("RID");

		updateRequest(strStatus, strId);

		return updateRequest(strStatus, strId);

	}

	private String updateRequest(String strStatus, String strId) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("strStatus :: "+strStatus+"  strId :: "+strId);
		if (strId != null && strStatus != null) {
			if (strStatus.equals("close")) {
				try {
	
					con = db.makeConnection(con);
					String query = "update recruitment_details set close_job_status=? where recruitment_id=?";
					pst = con.prepareStatement(query);
					pst.setBoolean(1, true);
					pst.setInt(2, uF.parseToInt(strId));
					pst.execute();
					pst.close();
					
//					System.out.println("pst ::: "+pst);
					getStatusMessage(true, strId);

				} catch (Exception e) {
					e.printStackTrace();
					return ERROR;
				} finally {
					
					db.closeStatements(pst);
					db.closeConnection(con);
				}
			}
		}
		return SUCCESS;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	public void getStatusMessage(boolean nStatus, String strId) {

		if (nStatus == true) {
			StringBuilder sb = new StringBuilder();
			 /*sb.append("<a href=\"javascript:void(0)\" onclick=\"checkProfile('"
					+ strId
					+ "')\"><img src=\"images1/icons/approved.png\" title=\"Approved\" /></a>  ");*/
			
			sb.append("<a href=\"javascript:void(0);\" onclick=\"checkProfile('"
					+ strId
					+ "')\" > <i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i> </a> ");
			
			
			sb.append("Job Closed");
			request.setAttribute("STATUS_MSG", sb.toString());
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

	public String getJob_deny_reason() {
		return job_deny_reason;
	}

	public void setJob_deny_reason(String job_deny_reason) {
		this.job_deny_reason = job_deny_reason;
	}
}
