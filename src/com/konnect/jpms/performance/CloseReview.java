package com.konnect.jpms.performance;

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

public class CloseReview extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request; 
	String strUserType = null;
	String strSessionEmpId = null;

	private CommonFunctions CF;
	private String reviewId;
	private String fromPage;
	private String operation;
	
	private String closeReason;
	private String appFreqId;
	
	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
	
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("getFromPage() ===>> " + getFromPage());
//		System.out.println("getOperation() ===>> " + getOperation());
		if(getOperation() != null && getOperation().equals("update")) {
			
			closeReview(uF);
			
			if(getFromPage() != null && getFromPage().equals("MyReview")) {
				return "MSUCCESS";
			} else {
				return SUCCESS;
			}
		} else {
			getCloseReviewReason(uF);
			return LOAD;
		}
	}
	
	
	private void getCloseReviewReason(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select close_reason from appraisal_details_frequency where appraisal_id = ? and appraisal_freq_id = ? and is_appraisal_close = true");
			pst.setInt(1, uF.parseToInt(getReviewId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rst = pst.executeQuery();
			while(rst.next()) {
				setCloseReason(uF.showData(rst.getString("close_reason"), "-"));
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


	public void closeReview(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update appraisal_details_frequency set is_appraisal_close = true, close_reason = ? where appraisal_id = ? and appraisal_freq_id = ?");
			pst.setString(1, getCloseReason());
			pst.setInt(2, uF.parseToInt(getReviewId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
//			System.out.println("pst ===>> " + pst);
			pst.executeUpdate();
			pst.close();
			String reviewName = CF.getReviewNameById(con, uF, getReviewId());
			session.setAttribute(MESSAGE, SUCCESSM + "" + uF.showData(reviewName, "") + " review has been closed successfully." + END);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public String getReviewId() {
		return reviewId;
	}

	public void setReviewId(String reviewId) {
		this.reviewId = reviewId;
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


	public String getAppFreqId() {
		return appFreqId;
	}


	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}
}
