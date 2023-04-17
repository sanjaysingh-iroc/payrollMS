package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CloseLearningPlan extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strSessionEmpId = null;

	private CommonFunctions CF;
	private String lPlanId;
	private String fromPage;
	private String operation;
	
	private String closeReason;
	
	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();	
//		System.out.println("operation==>"+getOperation());
		if(getOperation() != null && getOperation().equals("update")) {
			
			closeLearningPlan(uF);
//			if(getFromPage() != null && getFromPage().equals("")) {
//				return "ASUCCESS";
//			} else {
				return SUCCESS;
//			}
		} else {
			getCloseLearningPlanReason(uF);
			return LOAD;
		}
	}
	
	
	private void getCloseLearningPlanReason(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select close_reason,is_close from learning_plan_details where learning_plan_id = ? and is_close = true");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			rst = pst.executeQuery();
			while(rst.next()) {
				setCloseReason(rst.getString("close_reason"));
				request.setAttribute("IS_CLOSE", ""+uF.parseToBoolean(rst.getString("is_close")));
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


	public void closeLearningPlan(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update learning_plan_details set is_close = true, close_reason = ? where learning_plan_id = ?");
			pst.setString(1, getCloseReason());
			pst.setInt(2, uF.parseToInt(getlPlanId()));
			pst.executeUpdate();
			pst.close();
			String lPlanName = CF.getLearningPlanNameById(con, uF, getlPlanId());
		//	session.setAttribute(MESSAGE, SUCCESSM + "" + uF.showData(lPlanName, "") + " learning plan has been closed successfully." + END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String getlPlanId() {
		return lPlanId;
	}

	public void setlPlanId(String lPlanId) {
		this.lPlanId = lPlanId;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getCloseReason() {
		return closeReason;
	}

	public void setCloseReason(String closeReason) {
		this.closeReason = closeReason;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}


	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	

}
