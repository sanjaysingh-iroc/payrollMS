package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.interceptor.ServletRequestAware;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class AppraisalFinalSattlement implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	CommonFunctions CF;
	
	private String id;
	private String empId;
	private String comment;
	private String operation;
	private String status;
	private String appFreqId;
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

		request.setAttribute(PAGE, "/jsp/performance/Appraisal.jsp");
		request.setAttribute(TITLE, "Appraisal");
		getFinalSattleMent();

		return "success";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void getFinalSattleMent() {
		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);

//			Map<String, String> hmDesignation = CF.getDesigMap();
//			Map<String, String> hmGradeMap = CF.getGradeMap();
//			Map<String, String> hmEmpName = CF.getEmpNameMap(null, null);
//			Map<String, String> hmLevelMap = getLevelMap();
//			
//			request.setAttribute("hmEmpName",hmEmpName);

//			String orient=null;
//			System.out.println("uF.parseToBoolean(getStatus()) "+uF.parseToBoolean(getStatus()));
			pst = con
					.prepareStatement("insert into appraisal_final_sattlement(emp_id,appraisal_id,user_id,if_approved,sattlement_comment,appraisal_freq_id) values(?,?,?,?,?,?) ");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setBoolean(4, uF.parseToBoolean(getStatus()));
			pst.setString(5,getComment());
			pst.setInt(6,uF.parseToInt(getAppFreqId()));
			pst.execute();
			pst.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

	
}
