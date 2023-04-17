package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetLRApprovalStatus extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	
	CommonFunctions CF = null;
	
	private String effectiveid;
	
	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		String effectivetype = WORK_FLOW_LEARNING_REQUEST;
		getApprovalStatus(effectivetype);
		
		return SUCCESS;
	}
	
	private void getApprovalStatus(String effectivetype) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if (hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmEmpUserId = CF.getEmpUserIdMap(con);
			if(hmEmpUserId == null) hmEmpUserId = new HashMap<String, String>();
			

			pst = con.prepareStatement("select * from work_flow_details where effective_id=? and effective_type=? order by member_position");
			pst.setInt(1, uF.parseToInt(getEffectiveid()));
			pst.setString(2, effectivetype);
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmApprovalStatus = new HashMap<String, String>();
			String strMemberPosition = null;
			int step = 1;
			boolean flag = false;
			while (rs.next()) {

				String memberStep = hmApprovalStatus.get(rs.getString("effective_id"));
				String strUserTypeName = uF.parseToInt(rs.getString("user_type_id")) > 0 ? " ("+ uF.showData(hmUserTypeMap.get(rs.getString("user_type_id")), "") + ")" : "";
				String strApproveMemTick = "";
				String strApproveMemReason = "";
				if (uF.parseToInt(rs.getString("is_approved")) == 1 || uF.parseToInt(rs.getString("is_approved")) == 2 || uF.parseToInt(rs.getString("is_approved")) == -1) {
					if (uF.parseToInt(rs.getString("is_approved")) == 2) {
						/*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
						strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
					} else if (uF.parseToInt(rs.getString("is_approved")) == 1) {
						/*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
						strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
					} else if (uF.parseToInt(rs.getString("is_approved")) == -1) {
						/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
						strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Denied\"></i>";
					} else {
						strApproveMemTick = "<i class=\"fa fa-minus\" aria-hidden=\"true\" title=\"Waiting\"></i>";
					}
					if(rs.getString("reason")!=null && !rs.getString("reason").trim().equals("") && !rs.getString("reason").trim().equalsIgnoreCase("NULL")){
						strApproveMemReason = "[Reason: "+rs.getString("reason").trim()+"]";
					}
				} else if (uF.parseToInt(rs.getString("is_approved")) == 0) {
					flag = true;
				}

				if (memberStep == null) {
					memberStep = "<strong>Step " + step + ".</strong> " + strApproveMemTick + hmEmployeeNameMap.get(rs.getString("emp_id").trim()) + strUserTypeName + strApproveMemReason;
					strMemberPosition = rs.getString("member_position");
				} else {
					if (strMemberPosition != null && strMemberPosition.equals(rs.getString("member_position"))) {
						memberStep += ", "+ strApproveMemTick  + hmEmployeeNameMap.get(rs.getString("emp_id").trim()) + strUserTypeName + strApproveMemReason;
					} else {
						step++;
						strMemberPosition = rs.getString("member_position");
						// memberStep+=" ======> "+hmEmployeeNameMap.get(rs.getString("emp_id").trim())+strUserTypeName;
						memberStep += "<br/><strong>Step " + step + ".</strong> "+ strApproveMemTick + hmEmployeeNameMap.get(rs.getString("emp_id").trim()) + strUserTypeName + strApproveMemReason;
					}
				}
				hmApprovalStatus.put(rs.getString("effective_id"), memberStep);
			}
			rs.close();
			pst.close();
//			System.out.println("hmApprovalStatus ===>> " + hmApprovalStatus);
			request.setAttribute("hmApprovalStatus", hmApprovalStatus);
			
			StringBuilder sbReason = new StringBuilder();;
			if(flag){
				if(effectivetype.equals(WORK_FLOW_LEARNING_REQUEST)){
					pst = con.prepareStatement("select * from learning_nominee_details where nominated_details_id=? and requested_by > 0");
					pst.setInt(1, uF.parseToInt(getEffectiveid()));
					rs = pst.executeQuery();
					while (rs.next()) {
						String strApproveMemTick = "";
						String strApproveMemReason = "";
						if (uF.parseToInt(rs.getString("approve_status")) == 1){
							/*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
							strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
							
						} else if (uF.parseToInt(rs.getString("approve_status")) == -1){
							/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
							strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Denied\"></i>";
							
						}
						if(rs.getString("approve_reason")!=null && !rs.getString("approve_reason").trim().equals("") && !rs.getString("approve_reason").trim().equalsIgnoreCase("NULL")){
							strApproveMemReason = "[Reason: "+rs.getString("approve_reason").trim()+"]";
						}

						String strUserTypeName = uF.parseToInt(hmEmpUserId.get(rs.getString("approved_by"))) > 0 ? " ("+ uF.showData(hmUserTypeMap.get(hmEmpUserId.get(rs.getString("approved_by"))), "") + ")" : "";
						System.out.println("GLRAS/144--strUserTypeName="+strUserTypeName);
						System.out.println("GLRAS/145--hmEmployeeNameMap="+hmEmployeeNameMap.get(rs.getString("approved_by")));
						sbReason.append(strApproveMemTick+uF.showData(hmEmployeeNameMap.get(rs.getString("approved_by")), "")+ strUserTypeName + strApproveMemReason);
					}
					rs.close();
					pst.close();
				}
			}
			
			request.setAttribute("strReason", sbReason.toString());
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getEffectiveid() {
		return effectiveid;
	}

	public void setEffectiveid(String effectiveid) {
		this.effectiveid = effectiveid;
	}
	

}
