package com.konnect.jpms.requsitions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateLTA extends ActionSupport  implements ServletRequestAware, IStatements {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	 
	private CommonFunctions CF;
	
	String approveStatus;
	String empLtaId;
	String mReason;
	String type;
	String userType;
	String currUserType;
	
	public String execute() {
	
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		if(getApproveStatus()!= null && getApproveStatus().equalsIgnoreCase("2")){
			deleteRequest();
			if(getType()!=null && getType().equals("type")){
				return DASHBOARD;
			}
			return "pullout";
		} else{			
			updateRequest();
			if(getType()!=null && getType().equals("type")){
				return DASHBOARD;
			}
			return SUCCESS;
		}
		
	}
	
	public void updateRequest(){
		
		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			
			con = db.makeConnection(con);
				
			if(uF.parseToBoolean(CF.getIsWorkFlow())){
				
//				pst = con.prepareStatement("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_LTA+"'" +
//					" and effective_id=? order by effective_id,member_position");
//				pst.setInt(1, uF.parseToInt(getEmpLtaId()));
//				rs = pst.executeQuery();			
//				List<String> checkEmpList=new ArrayList<String>();
//				while(rs.next()){
//					checkEmpList.add(rs.getString("emp_id"));					
//				}
//				rs.close();
//				pst.close();
		
				if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN))){
					pst = con.prepareStatement("update emp_lta_details set is_approved = ?,approved_by = ?,approved_date = ?,approve_reason=?  where emp_lta_id = ?");
					pst.setInt(1, uF.parseToInt(getApproveStatus()));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(4, getmReason());
					pst.setInt(5, uF.parseToInt(getEmpLtaId()));
					pst.execute();
					pst.close();
					
				}else{						
					
					pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id=? " +
							"and effective_type='"+WORK_FLOW_LTA+"' and is_approved=0 and emp_id=? and user_type_id=? order by work_flow_id");
					pst.setInt(1, uF.parseToInt(getEmpLtaId()));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
                    pst.setInt(3, uF.parseToInt(getUserType()));
					rs = pst.executeQuery();
					int work_id=0;
					while(rs.next()){
						work_id=rs.getInt("work_flow_id");
						break;
					}
					rs.close();
					pst.close();
					
					
					pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,approve_date=?,reason=? WHERE work_flow_id=?");
					pst.setInt(1, uF.parseToInt(getApproveStatus()));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(3, getmReason());
					pst.setInt(4, work_id);
					pst.execute();			
					pst.close();
	
					
					boolean flag=true;
					pst = con.prepareStatement("select * from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_LTA+"' " +
							" and work_flow_id=(select min(work_flow_id) from work_flow_details where effective_id=? " +
							" and is_approved=0 and effective_type='"+WORK_FLOW_LTA+"' and member_position not in " +
							" (select member_position from work_flow_details where effective_id=? and is_approved=1 and effective_type='"+WORK_FLOW_LTA+"' )) " +
							" order by work_flow_id");
					pst.setInt(1, uF.parseToInt(getEmpLtaId()));
					pst.setInt(2, uF.parseToInt(getEmpLtaId()));
					pst.setInt(3, uF.parseToInt(getEmpLtaId()));
					rs = pst.executeQuery();
					while(rs.next()){
						flag=false;
					}
					rs.close();
					pst.close();
					
					if(flag){				
						pst = con.prepareStatement("update emp_lta_details set is_approved = ?,approved_by = ?,approved_date = ?,approve_reason=? where emp_lta_id = ?");
						pst.setInt(1, uF.parseToInt(getApproveStatus()));
						pst.setInt(2, uF.parseToInt(strSessionEmpId));
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setString(4, getmReason());
						pst.setInt(5, uF.parseToInt(getEmpLtaId()));
						pst.execute();
						pst.close();
					} else if(uF.parseToInt(getApproveStatus()) == -1){				
						pst = con.prepareStatement("update emp_lta_details set is_approved = ?,approved_by = ?,approved_date = ?,approve_reason=? where emp_lta_id = ?");
						pst.setInt(1, uF.parseToInt(getApproveStatus()));
						pst.setInt(2, uF.parseToInt(strSessionEmpId));
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setString(4, getmReason());
						pst.setInt(5, uF.parseToInt(getEmpLtaId()));
						pst.execute();
						pst.close();
					}
				}
			}else{
				if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))){
					pst = con.prepareStatement("update emp_lta_details set is_approved = ?,approved_by = ?,approved_date = ?,approve_reason=?  where emp_lta_id = ?");
					pst.setInt(1, uF.parseToInt(getApproveStatus()));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(4, getmReason());
					pst.setInt(5, uF.parseToInt(getEmpLtaId()));
					pst.execute();
					pst.close();
				}
			}
			
			 Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			
			if(getApproveStatus()!=null && getApproveStatus().equals("1")){
				
					pst1 = con.prepareStatement("select * from emp_lta_details where emp_lta_id = ?");
					pst1.setInt(1, uF.parseToInt(getEmpLtaId()));
					rs1 = pst1.executeQuery();
					String strEmpId=null;
					String appliedAmount =null;
					if(rs1.next()){
						strEmpId=rs1.getString("emp_id");
						appliedAmount =rs1.getString("actual_amount");
					}
					rs1.close();
					pst1.close();
				
					String strDomain = request.getServerName().split("\\.")[0];
					/*UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(strEmpId);
					userAlerts.set_type(LTA_APPROVAL_ALERT);
					userAlerts.setStatus(INSERT_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();*/
					
					String alertData = "<div style=\"float: left;\"> Your CTC Variable request has been Approved by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
					String alertAction = "MyPay.action?pType=WR&callFrom=NotiLTA";
					
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(strEmpId);
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
			}else if(getApproveStatus()!=null && getApproveStatus().equals("-1")){
				pst1 = con.prepareStatement("select * from emp_lta_details where emp_lta_id = ?");
				pst1.setInt(1, uF.parseToInt(getEmpLtaId()));
				rs1 = pst1.executeQuery();
				String strEmpId=null;
				String appliedAmount =null;
				if(rs1.next()){
					strEmpId=rs1.getString("emp_id");
					appliedAmount =rs1.getString("actual_amount");
				}
				rs1.close();
				pst1.close();
			
				String strDomain = request.getServerName().split("\\.")[0];
				/*UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(strEmpId);
				userAlerts.set_type(LTA_APPROVAL_ALERT);
				userAlerts.setStatus(INSERT_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();*/
				
				String alertData = "<div style=\"float: left;\"> Your CTC Variable request has been Denied by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
				String alertAction = "MyPay.action?pType=WR&callFrom=NotiLTA";
				
				UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(strEmpId);
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
				userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
				userAlerts.setStatus(INSERT_WR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
			}
			getStatusMessage(uF.parseToInt(getApproveStatus()));
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}
		
	}
	
	public void deleteRequest(){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from work_flow_details where effective_type='"+WORK_FLOW_LTA+"' and effective_id in (select emp_lta_id from emp_lta_details where emp_lta_id = ?)");
			pst.setInt(1, uF.parseToInt(getEmpLtaId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from emp_lta_details where emp_lta_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpLtaId()));
			pst.execute();
			pst.close();
			
			getStatusMessage(uF.parseToInt(getApproveStatus()));
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
//		return nRequisitionId;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	public void getStatusMessage(int nStatus){
		
		switch(nStatus){
		
		case -1:
			 /*request.setAttribute("STATUS_MSG", "<img title=\"Denied\" src=\""+request.getContextPath()+"/images1/icons/denied.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
			
			break;
			
		case 0:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Pending\" src=\""+request.getContextPath()+"/images1/icons/pending.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Pending\"></i>");
			break;
			
		case 1:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Approved\" src=\""+request.getContextPath()+"/images1/icons/approved.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
			
			break;
			
		case 2:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Pulled\" src=\""+request.getContextPath()+"/images1/icons/pullout.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" title=\"Pulled\"></i>");
			
			break;
			
		case 3:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Submited\" src=\""+request.getContextPath()+"/images1/icons/re_submit.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Submited\"></i>");
			
			break;
		}
	}
	
	
	public void getNotificationStatusMessage(int nStatus, String str){
		
		switch(nStatus){
		
		case 0:
			request.setAttribute("STATUS_MSG", "<img width=\"20px\" title=\"Pending\" src=\""+request.getContextPath()+"/images1/"+(("E".equalsIgnoreCase(str))?"mail_disbl.png":"mob_disbl.png")+"\" border=\"0\">&nbsp;");
			break;
			
		case 1:
			request.setAttribute("STATUS_MSG", "<img width=\"20px\" title=\"Approved\" src=\""+request.getContextPath()+"/images1/"+(("E".equalsIgnoreCase(str))?"mail_enbl.png":"mob_enbl.png")+"\" border=\"0\">&nbsp;");
			break;
		}
	}

	public String getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(String approveStatus) {
		this.approveStatus = approveStatus;
	}

	public String getEmpLtaId() {
		return empLtaId;
	}

	public void setEmpLtaId(String empLtaId) {
		this.empLtaId = empLtaId;
	}

	public String getmReason() {
		return mReason;
	}

	public void setmReason(String mReason) {
		this.mReason = mReason;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}
	
}
