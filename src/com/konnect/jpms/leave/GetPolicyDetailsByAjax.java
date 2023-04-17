package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.training.GetTrainerCalenderAjax;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class GetPolicyDetailsByAjax extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	String policyid; 

	CommonFunctions CF=null;

	private static Logger log = Logger.getLogger(GetTrainerCalenderAjax.class);
	
	public String execute() {
		
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			return LOGIN;
		}		
		getPolicyDetails();

		return SUCCESS;
	}


	private void getPolicyDetails() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		
		
		try {
			con=db.makeConnection(con);
			Map<String,String> hmWorkFlowMember = new HashMap<String, String>();
			pst = con.prepareStatement("select * from work_flow_member");
			rs=pst.executeQuery();
			while(rs.next()){
				hmWorkFlowMember.put(rs.getString("work_flow_member_id"),rs.getString("work_flow_mem"));
			}
			rs.close();
			pst.close();	
			
			
//			pst = con.prepareStatement("select *,member_position-cast(member_position as integer) as member_position1 " +
//					" from work_flow_policy where policy_type='1' and trial_status=1 order by member_position1");
			pst = con.prepareStatement("select *,member_position as member_position1 " +
			" from work_flow_policy where policy_type='1' and trial_status=1 order by policy_count,member_position1");
			rs = pst.executeQuery();
			Map<String,String> hmRegularPolicy = new HashMap<String, String>();		
			
			String strPolicyCountNew = null;
			String strPolicyCountOld = null;		
		
			String memberStep="";
			int i=0;
			while (rs.next()) {

				strPolicyCountNew = rs.getString("policy_count");
				if(strPolicyCountNew!=null && !strPolicyCountNew.equalsIgnoreCase(strPolicyCountOld)){
					memberStep="";
					i=0;
				}
				if(i==0){
					memberStep+=hmWorkFlowMember.get(rs.getString("work_flow_member_id").trim());
					i++;
				}else{
					memberStep+=" ======> "+hmWorkFlowMember.get(rs.getString("work_flow_member_id").trim());
				}			
				
				hmRegularPolicy.put(rs.getString("policy_count"), memberStep);
				
				strPolicyCountOld = strPolicyCountNew;
			}
			rs.close();
			pst.close();
			request.setAttribute("hmRegularPolicy", hmRegularPolicy);
			
			pst = con.prepareStatement("select *,member_position as member_position1 " +
				" from work_flow_policy where policy_type='2' and trial_status=1 order by policy_count,member_position1");
			rs = pst.executeQuery();
			Map<String, String> hmContengencyPolicy = new HashMap<String, String>();
			String strPolicyCount1New = null;
			String strPolicyCount1Old = null;
			String memberStep1 = "";
			i = 0;
			while (rs.next()) {

				strPolicyCount1New = rs.getString("policy_count");
				if (strPolicyCount1New != null
						&& !strPolicyCount1New
								.equalsIgnoreCase(strPolicyCount1Old)) {
					memberStep1 = "";
					i = 0;
				}
				if (i == 0) {
					memberStep1 += hmWorkFlowMember.get(rs.getString(
							"work_flow_member_id").trim());
					i++;
				} else {
					memberStep1 += " ======> "
							+ hmWorkFlowMember.get(rs.getString(
									"work_flow_member_id").trim());
				}

				hmContengencyPolicy.put(rs.getString("policy_count"),
						memberStep1);

				strPolicyCount1Old = strPolicyCount1New;
			}
			rs.close();
			pst.close();
			request.setAttribute("hmContengencyPolicy", hmContengencyPolicy);
			
		} catch (Exception e) {
			e.printStackTrace(); 
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
}


	public String getPolicyid() {
		return policyid;
	}


	public void setPolicyid(String policyid) {
		this.policyid = policyid;
	}


	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
		
	}

}
