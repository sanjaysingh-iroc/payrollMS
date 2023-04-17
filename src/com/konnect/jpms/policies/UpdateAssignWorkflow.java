package com.konnect.jpms.policies;

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

public class UpdateAssignWorkflow extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	String strUserType = null;
	String strSessionEmpId = null;
	
	String policyId;
	String levelId;
	String pType;
	String strLocation;
	
	String type;
	String strOrg;
	String leaveTypeId;
	String leavePolicy;
	String strPeriod;
	String strMin1;
	String strMax1;
	String leavePeriodPolicy1;
	String strMin2;
	String strMax2;
	String leavePeriodPolicy2;
	String strMin3;
	String strMax3;
	String leavePeriodPolicy3;
	String strMin4;
	String strMax4;
	String leavePeriodPolicy4;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
//		System.out.println("UpdateTimesheetWorkflowPolicy ===>> ");
		if(getType()!=null && getType().trim().equalsIgnoreCase("L")){
			updateLeavePolicy(uF);
		} else {
			updateLevelTimesheetWFPolicy(uF);
		}
		return SUCCESS;
 
	}  

	private void updateLeavePolicy(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			StringBuilder data = new StringBuilder();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("update emp_leave_type set policy_id=?,is_period=? where level_id=? and leave_type_id=? and wlocation_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getLeavePolicy()));
			pst.setBoolean(2, uF.parseToBoolean(getStrPeriod()));
			pst.setInt(3, uF.parseToInt(getLevelId()));
			pst.setInt(4, uF.parseToInt(getLeaveTypeId()));
			pst.setInt(5, uF.parseToInt(getStrLocation()));
//			System.out.println("pst====>"+pst);
			int x = pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("select policy_name from work_flow_policy where policy_count=? and trial_status=1 limit 1");
			pst.setInt(1, uF.parseToInt(getLeavePolicy()));
			rs =pst.executeQuery();
			String strPolicyName = null;
			while(rs.next()) {
				strPolicyName = rs.getString("policy_name");
			}
			rs.close();
			pst.close();
						
			data.append("<strong>"+uF.showData(strPolicyName,"N/A")+"</strong>&nbsp;" +
					"Is Period: <strong>"+(uF.parseToBoolean(getStrPeriod()) ? "Yes" : "No")+"</strong>" +
					"<input type=\"hidden\" name=\"isPeriod_"+getLevelId()+"_"+getLeaveTypeId()+"\" id=\"isPeriod_"+getLevelId()+"_"+getLeaveTypeId()+"\" value=\""+(uF.parseToBoolean(getStrPeriod()) ? "Yes" : "No")+"\"/>");
			data.append("::::");			
			if(x > 0){
				sbQuery = new StringBuilder();
				sbQuery.append("delete from workflow_policy_period where policy_type=? and level_id=? and leave_type_id=? and wlocation_id=?");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setString(1,WORK_FLOW_LEAVE);
				pst.setInt(2, uF.parseToInt(getLevelId()));
				pst.setInt(3, uF.parseToInt(getLeaveTypeId()));
				pst.setInt(4, uF.parseToInt(getStrLocation()));
//				System.out.println("pst====>"+pst);
				pst.executeUpdate();
				pst.close();
				
				if(uF.parseToBoolean(getStrPeriod())){
					int i = 0;
					
					if(uF.parseToInt(getStrMin1()) > 0 && uF.parseToInt(getStrMax1()) > 0 && uF.parseToInt(getLeavePeriodPolicy1()) > 0){
						sbQuery = new StringBuilder();
						sbQuery.append("insert into workflow_policy_period (min_value,max_value,policy_id,policy_type,level_id,org_id,wlocation_id," +
								"leave_type_id,added_by,entry_date)values(?,?,?,?, ?,?,?,?, ?,?)");
						pst = con.prepareStatement(sbQuery.toString());
						pst.setInt(1, uF.parseToInt(getStrMin1()));
						pst.setInt(2, uF.parseToInt(getStrMax1()));
						pst.setInt(3, uF.parseToInt(getLeavePeriodPolicy1()));
						pst.setString(4, WORK_FLOW_LEAVE);
						pst.setInt(5, uF.parseToInt(getLevelId()));
						pst.setInt(6, uF.parseToInt(getStrOrg()));
						pst.setInt(7, uF.parseToInt(getStrLocation()));
						pst.setInt(8, uF.parseToInt(getLeaveTypeId()));
						pst.setInt(9, uF.parseToInt(strSessionEmpId));
						pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
						int a = pst.executeUpdate();
						pst.close();
						
						if(a > 0){
							i++;
							
							pst = con.prepareStatement("select policy_name from work_flow_policy where policy_count=? and trial_status=1 limit 1");
							pst.setInt(1, uF.parseToInt(getLeavePeriodPolicy1()));
							rs =pst.executeQuery();
							strPolicyName = null;
							while(rs.next()) {
								strPolicyName = rs.getString("policy_name");
							}
							rs.close();
							pst.close();
							
							data.append("<span><strong>"+i+".</strong>&nbsp;" +
									"Min: <strong>"+getStrMin1()+"</strong>&nbsp;" +
									"Max: <strong>"+getStrMax1()+"</strong>&nbsp;" +
									"Workflow Policy: <strong>"+strPolicyName+"</strong></span><br/>");
						}
					}
					if(uF.parseToInt(getStrMin2()) > 0 && uF.parseToInt(getStrMax2()) > 0 && uF.parseToInt(getLeavePeriodPolicy2()) > 0){
						sbQuery = new StringBuilder();
						sbQuery.append("insert into workflow_policy_period (min_value,max_value,policy_id,policy_type,level_id,org_id,wlocation_id," +
								"leave_type_id,added_by,entry_date)values(?,?,?,?, ?,?,?,?, ?,?)");
						pst = con.prepareStatement(sbQuery.toString());
						pst.setInt(1, uF.parseToInt(getStrMin2()));
						pst.setInt(2, uF.parseToInt(getStrMax2()));
						pst.setInt(3, uF.parseToInt(getLeavePeriodPolicy2()));
						pst.setString(4, WORK_FLOW_LEAVE);
						pst.setInt(5, uF.parseToInt(getLevelId()));
						pst.setInt(6, uF.parseToInt(getStrOrg()));
						pst.setInt(7, uF.parseToInt(getStrLocation()));
						pst.setInt(8, uF.parseToInt(getLeaveTypeId()));
						pst.setInt(9, uF.parseToInt(strSessionEmpId));
						pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
						int a = pst.executeUpdate();
						pst.close();
						
						if(a > 0){
							i++;
							pst = con.prepareStatement("select policy_name from work_flow_policy where policy_count=? and trial_status=1 limit 1");
							pst.setInt(1, uF.parseToInt(getLeavePeriodPolicy2()));
							rs =pst.executeQuery();
							strPolicyName = null;
							while(rs.next()) {
								strPolicyName = rs.getString("policy_name");
							}
							rs.close();
							pst.close();
							
							data.append("<span><strong>"+i+".</strong>&nbsp;" +
									"Min: <strong>"+getStrMin2()+"</strong>&nbsp;" +
									"Max: <strong>"+getStrMax2()+"</strong>&nbsp;" +
									"Workflow Policy: <strong>"+strPolicyName+"</strong></span><br/>");
						}
					}
					if(uF.parseToInt(getStrMin3()) > 0 && uF.parseToInt(getStrMax3()) > 0 && uF.parseToInt(getLeavePeriodPolicy3()) > 0){
						sbQuery = new StringBuilder();
						sbQuery.append("insert into workflow_policy_period (min_value,max_value,policy_id,policy_type,level_id,org_id,wlocation_id," +
								"leave_type_id,added_by,entry_date)values(?,?,?,?, ?,?,?,?, ?,?)");
						pst = con.prepareStatement(sbQuery.toString());
						pst.setInt(1, uF.parseToInt(getStrMin3()));
						pst.setInt(2, uF.parseToInt(getStrMax3()));
						pst.setInt(3, uF.parseToInt(getLeavePeriodPolicy3()));
						pst.setString(4, WORK_FLOW_LEAVE);
						pst.setInt(5, uF.parseToInt(getLevelId()));
						pst.setInt(6, uF.parseToInt(getStrOrg()));
						pst.setInt(7, uF.parseToInt(getStrLocation()));
						pst.setInt(8, uF.parseToInt(getLeaveTypeId()));
						pst.setInt(9, uF.parseToInt(strSessionEmpId));
						pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
						int a = pst.executeUpdate();
						pst.close();
						
						if(a > 0){
							i++;
							pst = con.prepareStatement("select policy_name from work_flow_policy where policy_count=? and trial_status=1 limit 1");
							pst.setInt(1, uF.parseToInt(getLeavePeriodPolicy3()));
							rs =pst.executeQuery();
							strPolicyName = null;
							while(rs.next()) {
								strPolicyName = rs.getString("policy_name");
							}
							rs.close();
							pst.close();
							
							data.append("<span><strong>"+i+".</strong>&nbsp;" +
									"Min: <strong>"+getStrMin3()+"</strong>&nbsp;" +
									"Max: <strong>"+getStrMax3()+"</strong>&nbsp;" +
									"Workflow Policy: <strong>"+strPolicyName+"</strong></span><br/>");
						}
					}
					if(uF.parseToInt(getStrMin4()) > 0 && uF.parseToInt(getStrMax4()) > 0 && uF.parseToInt(getLeavePeriodPolicy4()) > 0){
						sbQuery = new StringBuilder();
						sbQuery.append("insert into workflow_policy_period (min_value,max_value,policy_id,policy_type,level_id,org_id,wlocation_id," +
								"leave_type_id,added_by,entry_date)values(?,?,?,?, ?,?,?,?, ?,?)");
						pst = con.prepareStatement(sbQuery.toString());
						pst.setInt(1, uF.parseToInt(getStrMin4()));
						pst.setInt(2, uF.parseToInt(getStrMax4()));
						pst.setInt(3, uF.parseToInt(getLeavePeriodPolicy4()));
						pst.setString(4, WORK_FLOW_LEAVE);
						pst.setInt(5, uF.parseToInt(getLevelId()));
						pst.setInt(6, uF.parseToInt(getStrOrg()));
						pst.setInt(7, uF.parseToInt(getStrLocation()));
						pst.setInt(8, uF.parseToInt(getLeaveTypeId()));
						pst.setInt(9, uF.parseToInt(strSessionEmpId));
						pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
						int a = pst.executeUpdate();
						pst.close();
						
						if(a > 0){
							i++;
							pst = con.prepareStatement("select policy_name from work_flow_policy where policy_count=? and trial_status=1 limit 1");
							pst.setInt(1, uF.parseToInt(getLeavePeriodPolicy4()));
							rs =pst.executeQuery();
							strPolicyName = null;
							while(rs.next()) {
								strPolicyName = rs.getString("policy_name");
							}
							rs.close();
							pst.close();
							
							data.append("<span><strong>"+i+".</strong>&nbsp;" +
									"Min: <strong>"+getStrMin4()+"</strong>&nbsp;" +
									"Max: <strong>"+getStrMax4()+"</strong>&nbsp;" +
									"Workflow Policy: <strong>"+strPolicyName+"</strong></span><br/>");
						}
					}
					
				}
			}
			
			request.setAttribute("data", data.toString());
//			System.out.println("data====>"+data.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String updateLevelTimesheetWFPolicy(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("update work_flow_policy_details set policy_id=? where level_id=? and type=? and wlocation_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getPolicyId()));
			pst.setInt(2, uF.parseToInt(getLevelId()));
			pst.setString(3, getpType());
			pst.setInt(4, uF.parseToInt(getStrLocation()));
//			System.out.println("pst====>"+pst);
			int x = pst.executeUpdate();
			pst.close();
			
			if(x == 0){
				pst = con.prepareStatement("insert into work_flow_policy_details (policy_id,level_id,type,wlocation_id) values(?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getPolicyId()));
				pst.setInt(2, uF.parseToInt(getLevelId()));
				pst.setString(3, getpType());
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

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

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

	public String getpType() {
		return pType;
	}

	public void setpType(String pType) {
		this.pType = pType;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getLeaveTypeId() {
		return leaveTypeId;
	}

	public void setLeaveTypeId(String leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}

	public String getLeavePolicy() {
		return leavePolicy;
	}

	public void setLeavePolicy(String leavePolicy) {
		this.leavePolicy = leavePolicy;
	}

	public String getStrPeriod() {
		return strPeriod;
	}

	public void setStrPeriod(String strPeriod) {
		this.strPeriod = strPeriod;
	}

	public String getStrMin1() {
		return strMin1;
	}

	public void setStrMin1(String strMin1) {
		this.strMin1 = strMin1;
	}

	public String getStrMax1() {
		return strMax1;
	}

	public void setStrMax1(String strMax1) {
		this.strMax1 = strMax1;
	}

	public String getLeavePeriodPolicy1() {
		return leavePeriodPolicy1;
	}

	public void setLeavePeriodPolicy1(String leavePeriodPolicy1) {
		this.leavePeriodPolicy1 = leavePeriodPolicy1;
	}

	public String getStrMin2() {
		return strMin2;
	}

	public void setStrMin2(String strMin2) {
		this.strMin2 = strMin2;
	}

	public String getStrMax2() {
		return strMax2;
	}

	public void setStrMax2(String strMax2) {
		this.strMax2 = strMax2;
	}

	public String getLeavePeriodPolicy2() {
		return leavePeriodPolicy2;
	}

	public void setLeavePeriodPolicy2(String leavePeriodPolicy2) {
		this.leavePeriodPolicy2 = leavePeriodPolicy2;
	}

	public String getStrMin3() {
		return strMin3;
	}

	public void setStrMin3(String strMin3) {
		this.strMin3 = strMin3;
	}

	public String getStrMax3() {
		return strMax3;
	}

	public void setStrMax3(String strMax3) {
		this.strMax3 = strMax3;
	}

	public String getLeavePeriodPolicy3() {
		return leavePeriodPolicy3;
	}

	public void setLeavePeriodPolicy3(String leavePeriodPolicy3) {
		this.leavePeriodPolicy3 = leavePeriodPolicy3;
	}

	public String getStrMin4() {
		return strMin4;
	}

	public void setStrMin4(String strMin4) {
		this.strMin4 = strMin4;
	}

	public String getStrMax4() {
		return strMax4;
	}

	public void setStrMax4(String strMax4) {
		this.strMax4 = strMax4;
	}

	public String getLeavePeriodPolicy4() {
		return leavePeriodPolicy4;
	}

	public void setLeavePeriodPolicy4(String leavePeriodPolicy4) {
		this.leavePeriodPolicy4 = leavePeriodPolicy4;
	}

}
