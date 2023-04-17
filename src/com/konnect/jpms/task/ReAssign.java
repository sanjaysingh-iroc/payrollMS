package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ReAssign extends ActionSupport implements ServletRequestAware, IConstants {

	private static final long serialVersionUID = 1L;

	CommonFunctions CF;
	private HttpServletRequest request;
	UtilityFunctions uF = new UtilityFunctions();

	HttpSession session;
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		
		String strSessionEmpId = (String) session.getAttribute(EMPID);

		String strTaskId = request.getParameter("taskId");

		if (strTaskId != null) {
			setStrTaskId(strTaskId);
		}

		if (getStrReAssign() != null && getStrReAssign().length() > 0) {
			reAssignTask(strSessionEmpId);
			return SUCCESS;
		}
		return "load";
	}

	String strReAssign;
	String strTaskId;
	public void reAssignTask(String strSessionEmpId) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		try {

			// System.out.println("strTaskId ===>> " + strTaskId);

			con = db.makeConnection(con);
			int parentTaskId = 0;
			pst = con.prepareStatement("select parent_task_id from activity_info where task_id = ? and parent_task_id != 0");
			pst.setInt(1, uF.parseToInt(getStrTaskId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				parentTaskId = rs.getInt("parent_task_id");
			}
			rs.close();
			pst.close();

			double dblAllCompleted = 0.0d;
			int subTaskCnt = 0;
			pst = con
					.prepareStatement("select sum(completed) as completed, count(task_id) as count from activity_info where parent_task_id = ? and task_id != ? and task_accept_status = 1");
			pst.setInt(1, parentTaskId);
			pst.setInt(2, uF.parseToInt(getStrTaskId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				dblAllCompleted = rs.getDouble("completed");
				subTaskCnt = rs.getInt("count");
			}
			rs.close();
			pst.close();

			// System.out.println("dblAllCompleted ===>> " + dblAllCompleted);
			// System.out.println("subTaskCnt ===>> " + subTaskCnt);

			subTaskCnt = subTaskCnt + 1;
			dblAllCompleted += uF.parseToDouble(getStrReAssign());

			// System.out.println("dblAllCompleted 1 ===>> " + dblAllCompleted);
			// System.out.println("subTaskCnt 1 ===>> " + subTaskCnt);

			double avgComplted = 0.0d;
			if (dblAllCompleted > 0 && subTaskCnt > 0) {
				avgComplted = dblAllCompleted / subTaskCnt;
			}
			// System.out.println("avgComplted ===>> " + avgComplted);

			if (avgComplted > 0) {
				pst = con.prepareStatement("update activity_info set completed=? where task_id =?");
				pst.setDouble(1, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(avgComplted)));
				pst.setInt(2, parentTaskId);
				pst.execute();
				pst.close();
			}

			pst = con.prepareStatement("UPDATE activity_info SET completed=?, reassign_by=? WHERE task_id =?");
			pst.setDouble(1, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(getStrReAssign()))));
			pst.setDouble(2, uF.parseToInt(strSessionEmpId));
			pst.setInt(3, uF.parseToInt(getStrTaskId()));
			pst.execute();
			pst.close();

			String pro_id = CF.getProjectIdByTaskId(con, getStrTaskId());
			pst = con
					.prepareStatement("select sum(completed)/count(task_id) as avrg from activity_info where pro_id=? and parent_task_id = 0 and task_accept_status = 1");
			pst.setInt(1, uF.parseToInt(pro_id));
			rs = pst.executeQuery();
			String projectCompletePercent = null;
			while (rs.next()) {
				projectCompletePercent = uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(rs.getString("avrg")));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("update projectmntnc set completed=? where pro_id=? ");
			pst.setDouble(1, uF.parseToDouble(projectCompletePercent));
			pst.setInt(2, uF.parseToInt(pro_id));
			pst.execute();
			pst.close();
			// System.out.println("pst===>"+pst);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrReAssign() {
		return strReAssign;
	}

	public void setStrReAssign(String strReAssign) {
		this.strReAssign = strReAssign;
	}

	public String getStrTaskId() {
		return strTaskId;
	}

	public void setStrTaskId(String strTaskId) {
		this.strTaskId = strTaskId;
	}
}
