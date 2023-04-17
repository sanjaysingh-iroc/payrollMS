package com.konnect.jpms.task.tax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillWorkFlowPolicy implements IStatements{

	String policyId;
	String policyName;

	HttpServletRequest request;
	public FillWorkFlowPolicy(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillWorkFlowPolicy() {
	}
	
	public FillWorkFlowPolicy(String policyId, String policyName) {
		this.policyId = policyId;
		this.policyName = policyName;
	}
	
	
	public Map<String, List<FillWorkFlowPolicy>> fillWorkFlowPolicyName() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, List<FillWorkFlowPolicy>> hmOrgWFPolicy = new HashMap<String, List<FillWorkFlowPolicy>>();
		try {
			
			con = db.makeConnection(con);
//			pst = con.prepareStatement("select org_id, policy_count, policy_name from work_flow_policy group by org_id, policy_count, policy_name order by policy_name");
			pst = con.prepareStatement("select org_id, b.policy_count, policy_name,location_id from (select max(work_flow_policy_id)as work_flow_policy_id,policy_count " +
					"from work_flow_policy where trial_status=1 group by policy_count) a ,work_flow_policy b where a.work_flow_policy_id=b.work_flow_policy_id " +
					"and group_id in (select group_id from work_flow_member where is_default=false group by group_id)");
			rs = pst.executeQuery();
			List<FillWorkFlowPolicy> al = new ArrayList<FillWorkFlowPolicy>();
			while(rs.next()) {
				al = hmOrgWFPolicy.get(rs.getString("org_id")+"_"+rs.getString("location_id"));
				if(al == null) al = new ArrayList<FillWorkFlowPolicy>();
				
				al.add(new FillWorkFlowPolicy(rs.getString("policy_count"), rs.getString("policy_name")));
				
				hmOrgWFPolicy.put(rs.getString("org_id")+"_"+rs.getString("location_id"), al);
			}	
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmOrgWFPolicy;
	}

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	
}