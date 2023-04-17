package com.konnect.jpms.task.tax;

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

public class UpdateTimesheetWorkflowPolicy extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	
	String policyId;
	String levelId;
	String strLocation;
	
	public String execute() throws Exception {
		
		request.setAttribute(PAGE, "/jsp/task/tax/UpdateTimesheetWorkflowPolicy.jsp");
		request.setAttribute(TITLE, "Update Timesheet Setting");
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();
		
//		System.out.println("UpdateTimesheetWorkflowPolicy ===>> ");
		updateLevelTimesheetWFPolicy(uF);
		return SUCCESS;
 
	}
	
	   

	public String updateLevelTimesheetWFPolicy(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("update work_flow_policy_details set policy_id=? where level_id=? and type='"+WORK_FLOW_TIMESHEET+"'");
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getPolicyId()));
//			pst.setInt(2, uF.parseToInt(getLevelId()));
//			pst.executeUpdate();
//			pst.close();
//			
//			pst = con.prepareStatement("select policy_name from work_flow_policy where policy_count=? limit 1");
//			pst.setInt(1, uF.parseToInt(getPolicyId()));
//			rs =pst.executeQuery();
//			while(rs.next()) {
//				request.setAttribute("POLICY_NAME", rs.getString("policy_name"));
//			}
//			rs.close();
//			pst.close();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("update work_flow_policy_details set policy_id=? where level_id=? and type=? and wlocation_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getPolicyId()));
			pst.setInt(2, uF.parseToInt(getLevelId()));
			pst.setString(3, WORK_FLOW_TIMESHEET);
			pst.setInt(4, uF.parseToInt(getStrLocation()));
//			System.out.println("pst====>"+pst);
			int x = pst.executeUpdate();
			pst.close();
			
			if(x == 0){
				pst = con.prepareStatement("insert into work_flow_policy_details (policy_id,level_id,type,wlocation_id) values(?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getPolicyId()));
				pst.setInt(2, uF.parseToInt(getLevelId()));
				pst.setString(3, WORK_FLOW_TIMESHEET);
				pst.setInt(4, uF.parseToInt(getStrLocation()));
//				System.out.println("pst====>"+pst);
				pst.execute();
				pst.close();
			}
			
			pst = con.prepareStatement("select policy_name from work_flow_policy where policy_count=? and trial_status=1 limit 1");
			pst.setInt(1, uF.parseToInt(getPolicyId()));
			rs =pst.executeQuery();
			while(rs.next()) {
				request.setAttribute("POLICY_NAME", rs.getString("policy_name"));
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
		return SUCCESS;
	}
	

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public String getLevelId() {
		return levelId;
	}

	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}



	public String getStrLocation() {
		return strLocation;
	}



	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

}
