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

public class DeleteAppraisal extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request; 
	String strUserType = null;
	String strSessionEmpId = null;

	private CommonFunctions CF;
	private String id;
	private String fromPage;
	private String appFreqId;
	UtilityFunctions uF = new UtilityFunctions();
	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
			deleteAppraisal(uF);
		
			if(getFromPage() != null && getFromPage().equals("MyReview")){
				return "MSUCCESS";
			}else{
				return SUCCESS;
			}
	}
	
	
	public void deleteAppraisal(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			if(getFromPage() != null && getFromPage().equals("MyReview")){
				pst = con.prepareStatement("delete from work_flow_details where effective_id=? and effective_type=?");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.setString(2, WORK_FLOW_SELF_REVIEW);
				pst.executeUpdate();
				pst.close();
			}
			
				pst = con.prepareStatement("delete from appraisal_details where appraisal_details_id=?");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_final_sattlement where appraisal_id=?");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_goal_details where appraisal_id=?");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_level_details where appraisal_id=?");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_main_level_details where appraisal_id=?");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_measure_details where appraisal_id=?");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_objective_details where appraisal_id=?");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_other_question_type_details where appraisal_id=?");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_plan where appraisal_id=?");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_question_answer where appraisal_id=?");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_question_details where appraisal_id=?");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_scorecard_details where appraisal_id=?");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_details_frequency where appraisal_id=? ");
				pst.setInt(1, uF.parseToInt(getId()));
//				pst.setInt(2, uF.parseToInt(getAppFreqId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_reviewee_details where appraisal_id=? ");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.executeUpdate();
				pst.close();
			 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
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
