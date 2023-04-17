package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateADRRequest extends ActionSupport implements ServletRequestAware, IStatements, Runnable {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null;

	private CommonFunctions CF;

	String strLeaveTypeId;
	String strEmpId;
	String strNod;
	String req_deny_reason;
	String strId;
	
	String candiAppId;
	String mReason;

	String userType;
	String type;
	
	public String getReq_deny_reason() {
		return req_deny_reason;
	}

	public void setReq_deny_reason(String req_deny_reason) {
		this.req_deny_reason = req_deny_reason;
	}

	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		// S RID
		String strStatus = (String) request.getParameter("S");
		strId = (String) request.getParameter("RID");
		
		if(uF.parseToInt(strId)>0) {
			updateRequest(strStatus, strId);
		}
		if(uF.parseToInt(getCandiAppId())>0) {
			updateResumeShortlistStatus(strStatus, getCandiAppId());
		}
//		System.out.println("getType() ====> " + getType());
		if (getType() != null && getType().equals("type")) {
             return DASHBOARD;
         } else {
        	 return SUCCESS;
         }

	}
	
	private void updateResumeShortlistStatus(String strStatus, String candiAppId) {
		Connection con = null;
		PreparedStatement pst = null;
		java.sql.ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		UtilityFunctions uF = new UtilityFunctions();

//		System.out.println("getmReason()====>" + getmReason());
		
		if (candiAppId != null && strStatus != null) {
//			System.out.println("strStatus====>" + strStatus);
//			System.out.println("strId====>" + strId);
			int intStatus = uF.parseToInt(strStatus);
//			System.out.println("intId====>" + intStatus);
			try {
//					System.out.println("in 1 strid"+strId);
				con = db.makeConnection(con);
				con.setAutoCommit(false);
				strUserTypeId = (String) session.getAttribute(USERTYPEID);
//					System.out.println("uF.parseToBoolean(CF.getIsWorkFlow()) ===>> " + uF.parseToBoolean(CF.getIsWorkFlow()));
				if (uF.parseToBoolean(CF.getIsWorkFlow())) {
					
					if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN)) { //!checkEmpList.contains(strSessionEmpId) &&
						String query = "update candidate_application_details set application_status=?,application_shortlist_by=?,application_status_date=? " +
							",application_shortlist_reason=? where candi_application_deatils_id=?";
						pst = con.prepareStatement(query);
						pst.setInt(1, uF.parseToInt(strStatus));
						pst.setInt(2, uF.parseToInt(strSessionEmpId));
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setString(4, getmReason());
						pst.setInt(5, uF.parseToInt(candiAppId));
						pst.execute();
						pst.close();
						
						getStatusMessage(uF.parseToInt(strStatus));
						con.commit();
	
						Map<String, String> hmUserType = CF.getUserTypeMap(con);
						String strAddedBy = getAddedByName(con, uF, candiAppId);
//							System.out.println("in workflow strAddedBy ===>> " + strAddedBy);
						if(uF.parseToInt(strAddedBy) > 0) {
							String alertData ="";
							if(intStatus==2) {
								alertData = "<div style=\"float: left;\"> Resume has been shortlisted from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. ["+hmUserType.get(strUserTypeId)+"] </div>";
							} else {
								alertData = "<div style=\"float: left;\"> Resume has been rejected from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. ["+hmUserType.get(strUserTypeId)+"] </div>";
							}
							String alertAction = "RecruitmentDashboard.action?pType=WR";
							String strDomain = request.getServerName().split("\\.")[0];
							UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strAddedBy); 
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID("7");
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
						}
						
						/*pst = con.prepareStatement("select emp_id,usertype_id from user_details where usertype_id=7 OR usertype_id=1");
							rs = pst.executeQuery();
							while (rs.next()) {
//								System.out.println("emp_id ===>> " + rs.getString("emp_id"));
								String alertData = "<div style=\"float: left;\"> Received a new Job Profile Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. ["+hmUserType.get(rs.getString("usertype_id"))+"] </div>";
								String alertAction = "RequirementDashboard.action?pType=WR&callFrom=HRDashJobApproval";
								String strDomain = request.getServerName().split("\\.")[0];
								UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(rs.getString("emp_id")); 
								userAlerts.setStrData(alertData);
								userAlerts.setStrAction(alertAction);
								userAlerts.setCurrUserTypeID(rs.getString("usertype_id"));
								userAlerts.setStatus(INSERT_WR_ALERT);
								Thread t = new Thread(userAlerts);
								t.run();
							}
							rs.close();
							pst.close();*/
						
					} else {
						 pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_RESUME_SHORTLIST+"' " +
						 	"and is_approved=0 and emp_id=? and user_type_id=? order by work_flow_id");
		                 pst.setInt(1, uF.parseToInt(candiAppId));
		                 pst.setInt(2, uF.parseToInt(strSessionEmpId));
	                	 pst.setInt(3, uF.parseToInt(getUserType()));
		                 rs = pst.executeQuery();
		                 int work_id = 0;
		                 while (rs.next()) {
		                     work_id = rs.getInt("work_flow_id");
		                     break;
		                 }
		     			rs.close();
		     			pst.close();
		                 
		                 pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,emp_id=?,approve_date=?,reason=? WHERE work_flow_id=?");
		                 pst.setInt(1, uF.parseToInt(strStatus));
		                 pst.setInt(2, uF.parseToInt(strSessionEmpId));
		                 pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
		                 pst.setString(4, getmReason());
		                 pst.setInt(5, work_id);
		                 pst.execute();
		     			 pst.close();
		                 
		     			getStatusMessage(uF.parseToInt("2"));
		     			
		                 boolean flag = true;
		                 
		                pst = con.prepareStatement("select * from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_RESUME_SHORTLIST+"' " +
							" and work_flow_id=(select min(work_flow_id) from work_flow_details where effective_id=? " +
							" and is_approved=0 and effective_type='"+WORK_FLOW_RESUME_SHORTLIST+"' and member_position not in " +
							" (select member_position from work_flow_details where effective_id=? and is_approved=1 and effective_type='"+WORK_FLOW_RESUME_SHORTLIST+"' )) " +
							" order by work_flow_id");
						pst.setInt(1, uF.parseToInt(candiAppId));
						pst.setInt(2, uF.parseToInt(candiAppId));
						pst.setInt(3, uF.parseToInt(candiAppId));
						rs = pst.executeQuery();
		                while (rs.next()) {
		                	flag = false;
		                }
		                rs.close();
		     			pst.close();
		     			 
		     			if (flag) {
		     				
		     				String query = "update candidate_application_details set application_status=?,application_shortlist_by=?,application_status_date=? " +
								",application_shortlist_reason=? where candi_application_deatils_id=?";
							pst = con.prepareStatement(query);
							pst.setInt(1, uF.parseToInt(strStatus));
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setString(4, getmReason());
							pst.setInt(5, uF.parseToInt(candiAppId));
							pst.execute();
							pst.close();
							
							getStatusMessage(uF.parseToInt(strStatus));
							con.commit();
		
							Map<String, String> hmUserType = CF.getUserTypeMap(con);
							String strAddedBy = getAddedByName(con, uF, candiAppId);
//							System.out.println("in workflow strAddedBy ===>> " + strAddedBy);
							if(uF.parseToInt(strAddedBy) > 0) {
								String alertData ="";
								if(intStatus==2) {
									alertData = "<div style=\"float: left;\"> Resume has been shortlisted from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. ["+hmUserType.get(strUserTypeId)+"] </div>";
								} else {
									alertData = "<div style=\"float: left;\"> Resume has been rejected from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. ["+hmUserType.get(strUserTypeId)+"] </div>";
								}
								String alertAction = "RecruitmentDashboard.action?pType=WR";
								String strDomain = request.getServerName().split("\\.")[0];
								UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(strAddedBy); 
								userAlerts.setStrData(alertData);
								userAlerts.setStrAction(alertAction);
								userAlerts.setCurrUserTypeID("7");
								userAlerts.setStatus(INSERT_WR_ALERT);
								Thread t = new Thread(userAlerts);
								t.run();
							}
						
							
							/*pst = con.prepareStatement("select emp_id,usertype_id from user_details where usertype_id=7 OR usertype_id=1");
								rs = pst.executeQuery();
								while (rs.next()) {
//									System.out.println("emp_id ===>> " + rs.getString("emp_id"));
									String alertData = "<div style=\"float: left;\"> Received a new Job Profile Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. ["+hmUserType.get(rs.getString("usertype_id"))+"] </div>";
									String alertAction = "RequirementDashboard.action?pType=WR&callFrom=HRDashJobApproval";
									String strDomain = request.getServerName().split("\\.")[0];
									UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
									userAlerts.setStrDomain(strDomain);
									userAlerts.setStrEmpId(rs.getString("emp_id")); 
									userAlerts.setStrData(alertData);
									userAlerts.setStrAction(alertAction);
									userAlerts.setCurrUserTypeID(rs.getString("usertype_id"));
									userAlerts.setStatus(INSERT_WR_ALERT);
									Thread t = new Thread(userAlerts);
									t.run();
								}
								rs.close();
								pst.close();*/
							
		     			}
					}
				} else {
					String query = "update candidate_application_details set application_status=?,application_shortlist_by=?,application_status_date=? " +
						",application_shortlist_reason=? where candi_application_deatils_id=?";
					pst = con.prepareStatement(query);
					pst.setInt(1, uF.parseToInt(strStatus));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(4, getmReason());
					pst.setInt(5, uF.parseToInt(candiAppId));
					pst.execute();
					pst.close();
					
					getStatusMessage(uF.parseToInt(strStatus));
					con.commit();

					Map<String, String> hmUserType = CF.getUserTypeMap(con);
					String strAddedBy = getAddedByName(con, uF, candiAppId);
//							System.out.println("in workflow strAddedBy ===>> " + strAddedBy);
					if(uF.parseToInt(strAddedBy) > 0) {
						String alertData ="";
						if(intStatus==2) {
							alertData = "<div style=\"float: left;\"> Resume has been shortlisted from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. ["+hmUserType.get(strUserTypeId)+"] </div>";
						} else {
							alertData = "<div style=\"float: left;\"> Resume has been rejected from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. ["+hmUserType.get(strUserTypeId)+"] </div>";
						}
						String alertAction = "RecruitmentDashboard.action?pType=WR";
						String strDomain = request.getServerName().split("\\.")[0];
						UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strAddedBy); 
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
						userAlerts.setCurrUserTypeID("7");
						userAlerts.setStatus(INSERT_WR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
					}
					
				}
			} catch (Exception e) {
				try {
					con.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			} finally {
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
		}
	}

	
	
	private void updateRequest(String strStatus, String strId) {
		Connection con = null;
		PreparedStatement pst = null;
		java.sql.ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		UtilityFunctions uF = new UtilityFunctions();

//		System.out.println("getmReason()====>" + getmReason());
		
		if (strId != null && strStatus != null) {
//			System.out.println("strStatus====>" + strStatus);
//			System.out.println("strId====>" + strId);
			int intStatus = uF.parseToInt(strStatus);
//			System.out.println("intId====>" + intStatus);
			switch (intStatus) {

			case 1:
				try {
//					System.out.println("in 1 strid"+strId);
					con = db.makeConnection(con);
					con.setAutoCommit(false);

					// for making job code *********
					String jobCode="";
					
					pst=con.prepareStatement("select wloacation_code,designation_code,custum_designation from recruitment_details " +
							" left  join designation_details using (designation_id) join work_location_info on (wlocation_id=wlocation)" +
							" where recruitment_id=? ");
					pst.setInt(1, uF.parseToInt(strId));
					rs=pst.executeQuery();
//					System.out.println("new pst ===> " + pst);
					if(rs.next()){
						if(rs.getString("designation_code")==null)
							jobCode+=rs.getString("wloacation_code")+"-NEW";
						else
							jobCode+=rs.getString("wloacation_code")+"-"+rs.getString("designation_code");
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select count(*) as count from recruitment_details where job_code like '"+jobCode+"%'");
					rs = pst.executeQuery();
					
					while (rs.next()) {
						int count=uF.parseToInt(rs.getString("count"));
							count++;
							// Conversion to 3 decimal places
							DecimalFormat decimalFormat = new DecimalFormat();
							decimalFormat.setMinimumIntegerDigits(3);
							
						jobCode+="-"+decimalFormat.format(count);
					}
					rs.close();
					pst.close();
					
//					System.out.println("job_code_increment "
//							+ job_code_increment);
//					System.out.println("uF.parseToBoolean(CF.getIsWorkFlow()) ===>> " + uF.parseToBoolean(CF.getIsWorkFlow()));
					if (uF.parseToBoolean(CF.getIsWorkFlow())) {
						/*StringBuilder sbQuery=new StringBuilder();
						sbQuery.append("select emp_id,effective_id from work_flowdetails where effective_type='"+WORK_FLOW_RECRUITMENT+"' and user_type_id=? and effective_id = ? order by effective_id,member_position");
						pst = con.prepareStatement(sbQuery.toString());
						pst.setInt(1, uF.parseToInt(strUserTypeId));
						pst.setInt(5, uF.parseToInt(strId));
//						System.out.println("pst ===>> " + pst);
						rs = pst.executeQuery();
						Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	
						while(rs.next()) {
							List<String> checkEmpList = hmCheckEmp.get(rs.getString("effective_id"));
							if(checkEmpList == null)checkEmpList = new ArrayList<String>();				
							checkEmpList.add(rs.getString("emp_id"));
							
							hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
						}
						rs.close();
						pst.close();
						List<String> checkEmpList = hmCheckEmp.get(strId);
						if(checkEmpList==null) checkEmpList = new ArrayList<String>();*/
						
						if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN)) { //!checkEmpList.contains(strSessionEmpId) &&
							String query = "update recruitment_details set status=?,job_code=?,approved_by=?,approved_date=?,req_deny_reason=? where recruitment_id=?";
							pst = con.prepareStatement(query);
							pst.setInt(1, uF.parseToInt(strStatus));
							pst.setString(2, jobCode);
							pst.setInt(3, uF.parseToInt(strSessionEmpId));
							pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setString(5, getmReason());
							pst.setInt(6, uF.parseToInt(strId));
							pst.execute();
							pst.close();
							
							getStatusMessage(uF.parseToInt(strStatus));
							con.commit();
		
							Map<String, String> hmUserType = CF.getUserTypeMap(con);
							String strAddedBy = getAddedByName(con, uF, strId);
//							System.out.println("in workflow strAddedBy ===>> " + strAddedBy);
							if(uF.parseToInt(strAddedBy) > 0) {
								String alertData = "<div style=\"float: left;\"> Your Requisition Request has been approved from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. ["+hmUserType.get(strUserTypeId)+"] </div>";
								String alertAction = "TeamRequests.action?pType=WR";
								String strDomain = request.getServerName().split("\\.")[0];
								UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(strAddedBy); 
								userAlerts.setStrData(alertData);
								userAlerts.setStrAction(alertAction);
								userAlerts.setCurrUserTypeID("2");
								userAlerts.setStatus(INSERT_WR_ALERT);
								Thread t = new Thread(userAlerts);
								t.run();
//								String strDomain = request.getServerName().split("\\.")[0];
//								UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//								userAlerts.setStrDomain(strDomain);
//								userAlerts.setStrEmpId(strAddedBy);
//								userAlerts.set_type(REQUIREMENT_APPROVAL_ALERT);
//								userAlerts.setStatus(INSERT_ALERT);
//								Thread t = new Thread(userAlerts);
//								t.run();
							}
							
							pst = con.prepareStatement("select emp_id,usertype_id from user_details where usertype_id=7 OR usertype_id=1");
							rs = pst.executeQuery();
							while (rs.next()) {
//								System.out.println("emp_id ===>> " + rs.getString("emp_id"));
								String alertData = "<div style=\"float: left;\"> Received a new Job Profile Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. ["+hmUserType.get(rs.getString("usertype_id"))+"] </div>";
								String alertAction = "RequirementDashboard.action?pType=WR&callFrom=HRDashJobApproval";
								String strDomain = request.getServerName().split("\\.")[0];
								UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(rs.getString("emp_id")); 
								userAlerts.setStrData(alertData);
								userAlerts.setStrAction(alertAction);
								userAlerts.setCurrUserTypeID(rs.getString("usertype_id"));
								userAlerts.setStatus(INSERT_WR_ALERT);
								Thread t = new Thread(userAlerts);
								t.run();
//								UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//								userAlerts.setStrDomain(strDomain);
//								userAlerts.setStrEmpId(rs.getString(1));
//								userAlerts.set_type(JOBCODE_REQUEST_ALERT);
//								userAlerts.setStatus(INSERT_ALERT);
//								Thread t = new Thread(userAlerts);
//								t.run();
							}
							rs.close();
							pst.close();
							
							String designationName = getDesignationNameById(con, uF, strId);
							session.setAttribute(MESSAGE, SUCCESSM+""+designationName+" designation requirement has been approved successfully."+END);
		
							String strDomain = request.getServerName().split("\\.")[0];
							setDomain(strDomain);
							Thread th = new Thread(this);
							th.start();
							sendMail(strId);
						} else {
							 pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_RECRUITMENT+"' " +
							 	"and is_approved=0 and emp_id=? and user_type_id=? order by work_flow_id");
			                 pst.setInt(1, uF.parseToInt(strId));
			                 pst.setInt(2, uF.parseToInt(strSessionEmpId));
		                	 pst.setInt(3, uF.parseToInt(getUserType()));
			                 rs = pst.executeQuery();
			                 int work_id = 0;
			                 while (rs.next()) {
			                     work_id = rs.getInt("work_flow_id");
			                     break;
			                 }
			     			rs.close();
			     			pst.close();
			                 
			                 pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,emp_id=?,approve_date=?,reason=? WHERE work_flow_id=?");
			                 pst.setInt(1, uF.parseToInt(strStatus));
			                 pst.setInt(2, uF.parseToInt(strSessionEmpId));
			                 pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			                 pst.setString(4, getmReason());
			                 pst.setInt(5, work_id);
			                 pst.execute();
			     			 pst.close();
			                 
			     			getStatusMessage(uF.parseToInt("2"));
			     			
			                 boolean flag = true;
//			                 pst = con.prepareStatement("select * from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_RECRUITMENT+"' " +
//			                         " and (is_approved=0 or is_approved=-1) ");
//			                 pst.setInt(1, uF.parseToInt(strId));
//			                 rs = pst.executeQuery();
			                 
			                pst = con.prepareStatement("select * from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_RECRUITMENT+"' " +
								" and work_flow_id=(select min(work_flow_id) from work_flow_details where effective_id=? " +
								" and is_approved=0 and effective_type='"+WORK_FLOW_RECRUITMENT+"' and member_position not in " +
								" (select member_position from work_flow_details where effective_id=? and is_approved=1 and effective_type='"+WORK_FLOW_RECRUITMENT+"' )) " +
								" order by work_flow_id");
							pst.setInt(1, uF.parseToInt(strId));
							pst.setInt(2, uF.parseToInt(strId));
							pst.setInt(3, uF.parseToInt(strId));
							rs = pst.executeQuery();
			                while (rs.next()) {
			                	flag = false;
			                }
			                rs.close();
			     			pst.close();
			     			 
			     			if (flag) {
			     				String query = "update recruitment_details set status=?,job_code=?,approved_by=?,approved_date=?,req_deny_reason=? where recruitment_id=?";
								pst = con.prepareStatement(query);
								pst.setInt(1, uF.parseToInt(strStatus));
								pst.setString(2, jobCode);
								pst.setInt(3, uF.parseToInt(strSessionEmpId));
								pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setString(5, getmReason());
								pst.setInt(6, uF.parseToInt(strId));
								pst.execute();
								pst.close();
								
								getStatusMessage(uF.parseToInt(strStatus));
								con.commit();
			
								Map<String, String> hmUserType = CF.getUserTypeMap(con);
								String strAddedBy = getAddedByName(con, uF, strId);
//								System.out.println("strAddedBy ===>> " + strAddedBy);
								if(uF.parseToInt(strAddedBy) > 0) {
									String alertData = "<div style=\"float: left;\"> Your Requisition Request has been approved from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. ["+hmUserType.get(strUserTypeId)+"] </div>";
									String alertAction = "TeamRequests.action?pType=WR";
									String strDomain = request.getServerName().split("\\.")[0];
									UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
									userAlerts.setStrDomain(strDomain);
									userAlerts.setStrEmpId(strAddedBy); 
									userAlerts.setStrData(alertData);
									userAlerts.setStrAction(alertAction);
									userAlerts.setCurrUserTypeID("2");
									userAlerts.setStatus(INSERT_WR_ALERT);
									Thread t = new Thread(userAlerts);
									t.run();
									
//									String strDomain = request.getServerName().split("\\.")[0];
//									UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//									userAlerts.setStrDomain(strDomain);
//									userAlerts.setStrEmpId(strAddedBy);
//									userAlerts.set_type(REQUIREMENT_APPROVAL_ALERT);
//									userAlerts.setStatus(INSERT_ALERT);
//									Thread t = new Thread(userAlerts);
//									t.run();
								}
								
								pst = con.prepareStatement("select emp_id,usertype_id from user_details where usertype_id=7 OR usertype_id=1");
								rs = pst.executeQuery();
								while (rs.next()) {
//									System.out.println("emp_id ===>> " + rs.getString("emp_id"));
									String alertData = "<div style=\"float: left;\"> Received a new Job Profile Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. ["+hmUserType.get(rs.getString("usertype_id"))+"] </div>";
									String alertAction = "RequirementDashboard.action?pType=WR&callFrom=HRDashJobApproval";
									String strDomain = request.getServerName().split("\\.")[0];
									UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
									userAlerts.setStrDomain(strDomain);
									userAlerts.setStrEmpId(rs.getString("emp_id")); 
									userAlerts.setStrData(alertData);
									userAlerts.setStrAction(alertAction);
									userAlerts.setCurrUserTypeID(rs.getString("usertype_id"));
									userAlerts.setStatus(INSERT_WR_ALERT);
									Thread t = new Thread(userAlerts);
									t.run();
								}
								rs.close();
								pst.close();
								
								String designationName = getDesignationNameById(con, uF, strId);
								session.setAttribute(MESSAGE, SUCCESSM+""+designationName+" designation requirement has been approved successfully."+END);
			
								String strDomain = request.getServerName().split("\\.")[0];
								setDomain(strDomain);
								Thread th = new Thread(this);
								th.start();
								sendMail(strId);
			     			}
			     			
//			     			else if (strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
//			     				String query = "update recruitment_details set status=?,job_code=?,approved_by=?,approved_date=? where recruitment_id=?";
//								pst = con.prepareStatement(query);
//								pst.setInt(1, uF.parseToInt(strStatus));
//								pst.setString(2, jobCode);
//								pst.setInt(3, uF.parseToInt(strSessionEmpId));
//								pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
//								pst.setInt(5, uF.parseToInt(strId));
//								pst.execute();
//								pst.close();
//								
//								getStatusMessage(uF.parseToInt(strStatus));
//								con.commit();
//			
//								String strAddedBy = getAddedByName(con, uF, strId);
//								if(uF.parseToInt(strAddedBy) > 0) {
//									String strDomain = request.getServerName().split("\\.")[0];
//									UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//									userAlerts.setStrDomain(strDomain);
//									userAlerts.setStrEmpId(strAddedBy);
//									userAlerts.set_type(REQUIREMENT_APPROVAL_ALERT);
//									userAlerts.setStatus(INSERT_ALERT);
//									Thread t = new Thread(userAlerts);
//									t.run();
//								}
//								
//								pst = con.prepareStatement("select emp_id from user_details where usertype_id=7 OR usertype_id=1");
//								rs = pst.executeQuery();
//								while (rs.next()) {
//									UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//									userAlerts.setStrDomain(strDomain);
//									userAlerts.setStrEmpId(rs.getString(1));
//									userAlerts.set_type(JOBCODE_REQUEST_ALERT);
//									userAlerts.setStatus(INSERT_ALERT);
//									Thread t = new Thread(userAlerts);
//									t.run();
//								}
//								rs.close();
//								pst.close();
//								
//								String designationName = getDesignationNameById(con, uF, strId);
//								session.setAttribute(MESSAGE, SUCCESSM+""+designationName+" designation requirement has been approved successfully."+END);
//			
//								String strDomain = request.getServerName().split("\\.")[0];
//								setDomain(strDomain);
//								Thread th = new Thread(this);
//								th.start();
//								sendMail(strId);
//			     			}
						}
					} else {
						String query = "update recruitment_details set status=?,job_code=?,approved_by=?,approved_date=?,req_deny_reason=? where recruitment_id=?";
						pst = con.prepareStatement(query);
						pst.setInt(1, uF.parseToInt(strStatus));
						pst.setString(2, jobCode);
						pst.setInt(3, uF.parseToInt(strSessionEmpId));
						pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setString(5, getmReason());
						pst.setInt(6, uF.parseToInt(strId));
						pst.execute();
						pst.close();
						
						getStatusMessage(uF.parseToInt(strStatus));
						con.commit();
	
						String strAddedBy = getAddedByName(con, uF, strId);
						if(uF.parseToInt(strAddedBy) > 0) {
							String strDomain = request.getServerName().split("\\.")[0];
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strAddedBy);
							userAlerts.set_type(REQUIREMENT_APPROVAL_ALERT);
							userAlerts.setStatus(INSERT_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
						}
						
						pst = con.prepareStatement("select emp_id from user_details where usertype_id=7 OR usertype_id=1");
						rs = pst.executeQuery();
						while (rs.next()) {
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(rs.getString(1));
							userAlerts.set_type(JOBCODE_REQUEST_ALERT);
							userAlerts.setStatus(INSERT_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
						}
						rs.close();
						pst.close();
						
						String designationName = getDesignationNameById(con, uF, strId);
						session.setAttribute(MESSAGE, SUCCESSM+""+designationName+" designation requirement has been approved successfully."+END);
	
						String strDomain = request.getServerName().split("\\.")[0];
						setDomain(strDomain);
						Thread th = new Thread(this);
						th.start();
						sendMail(strId);
					}
				} catch (Exception e) {
					try {
						con.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				} finally {
					db.closeResultSet(rs);
					db.closeStatements(pst);
					db.closeConnection(con);
				}
				break;
			}
		}
	}

//	private void updateRequest(String strStatus, String strId) {
//		Connection con = null;
//		PreparedStatement pst = null;
//		java.sql.ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//
//		UtilityFunctions uF = new UtilityFunctions();
//
//		boolean flag = false;
//
//		if (strId != null && strStatus != null) {
////			System.out.println("strStatus====>" + strStatus);
////			System.out.println("strId====>" + strId);
//			int intStatus = uF.parseToInt(strStatus);
////			System.out.println("intId====>" + intStatus);
//			switch (intStatus) {
//
//			case 1:
//				try {
////					System.out.println("in 1 strid"+strId);
//					con = db.makeConnection(con);
//					con.setAutoCommit(false);
//
//					
//					// for making job code *********
//					String jobCode="";
//					
//					pst=con.prepareStatement("select wloacation_code,designation_code,custum_designation from recruitment_details " +
//							" left  join designation_details using (designation_id) join work_location_info on (wlocation_id=wlocation)" +
//							" where recruitment_id=? ");
//					pst.setInt(1, uF.parseToInt(strId));
//					rs=pst.executeQuery();
////					System.out.println("new Date ===> " + new Date());
//					if(rs.next()){
//						if(rs.getString("designation_code")==null)
//							jobCode+=rs.getString("wloacation_code")+"-NEW";
//						else
//							jobCode+=rs.getString("wloacation_code")+"-"+rs.getString("designation_code");
//					}
//					rs.close();
//					pst.close();
//					
//					pst = con.prepareStatement("select count(*) as count from recruitment_details where job_code like '"+jobCode+"%'");
//					rs = pst.executeQuery();
//					
//					while (rs.next()) {
//						int count=uF.parseToInt(rs.getString("count"));
//							count++;
//							// Conversion to 3 decimal places
//							DecimalFormat decimalFormat = new DecimalFormat();
//							decimalFormat.setMinimumIntegerDigits(3);
//							
//						jobCode+="-"+decimalFormat.format(count);
//					}
//					rs.close();
//					pst.close();
//					
////					System.out.println("job_code_increment "
////							+ job_code_increment);
//
//					String query = "update recruitment_details set status=?,job_code=?,approved_by=?,approved_date=? where recruitment_id=?";
//					pst = con.prepareStatement(query);
////					System.out.println("strStatus parse====>"
////							+ uF.parseToInt(strStatus));
////					System.out.println("strId parse====>"
////							+ uF.parseToInt(strId));
//					pst.setInt(1, uF.parseToInt(strStatus));
//					pst.setString(2, jobCode);
//					pst.setInt(3, uF.parseToInt(strSessionEmpId));
//					pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setInt(5, uF.parseToInt(strId));
//					pst.execute();
//					pst.close();
//					
//					getStatusMessage(uF.parseToInt(strStatus));
//					con.commit();
//
//					String strAddedBy = getAddedByName(con, uF, strId);
//					if(uF.parseToInt(strAddedBy) > 0) {
//						String strDomain = request.getServerName().split("\\.")[0];
//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//						userAlerts.setStrDomain(strDomain);
//						userAlerts.setStrEmpId(strAddedBy);
//						userAlerts.set_type(REQUIREMENT_APPROVAL_ALERT);
//						userAlerts.setStatus(INSERT_ALERT);
//						Thread t = new Thread(userAlerts);
//						t.run();
//					}
//					
//					pst = con.prepareStatement("select emp_id from user_details where usertype_id=7 OR usertype_id=1");
//					rs = pst.executeQuery();
//					while (rs.next()) {
//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//						userAlerts.setStrDomain(strDomain);
//						userAlerts.setStrEmpId(rs.getString(1));
//						userAlerts.set_type(JOBCODE_REQUEST_ALERT);
//						userAlerts.setStatus(INSERT_ALERT);
//						Thread t = new Thread(userAlerts);
//						t.run();
//					}
//					rs.close();
//					pst.close();
//					
//					String designationName = getDesignationNameById(con, uF, strId);
//					session.setAttribute(MESSAGE, SUCCESSM+""+designationName+" designation requirement has been approved successfully."+END);
//
//					String strDomain = request.getServerName().split("\\.")[0];
//					setDomain(strDomain);
//					Thread th = new Thread(this);
//					th.start();
//					sendMail(strId);
//					
//					flag = true;
//				} catch (Exception e) {
//					try {
//						con.rollback();
//					} catch (SQLException e1) {
//						e1.printStackTrace();
//					}
//					e.printStackTrace();
//				} finally {
//					db.closeResultSet(rs);
//					db.closeStatements(pst);
//					db.closeConnection(con);
//				}
//				break;
//			}
//		}
//	}

	
	private String getDesignationNameById(Connection con, UtilityFunctions uF, String recruitId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
//		String requirementName = null;
		String designationName = null;
		try {
			
				pst = con.prepareStatement("select job_code,designation_id from recruitment_details where recruitment_id = ?");
				pst.setInt(1, uF.parseToInt(recruitId));
				rst = pst.executeQuery();
				Map<String, String> hmDesignation = CF.getDesigMap(con);
				String desigId = null;
				while (rst.next()) {
					desigId = rst.getString("designation_id");
//					requirementName = rst.getString("job_code");
				}
				rst.close();
				pst.close();
				designationName = hmDesignation.get(desigId);
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
		return designationName;
	}
	
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void getStatusMessage(int nStatus) {

		switch (nStatus) {

		case -1:
			 /*request.setAttribute("STATUS_MSG", "<img title=\"Denied\" src=\"" + request.getContextPath() + "/images1/icons/denied.png\" border=\"0\">::::-1");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>::::-1");
			break;

		case 0:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Pending\" src=\"" + request.getContextPath() + "/images1/icons/pending.png\" border=\"0\">::::0");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Pending\"></i>::::0");
			break;

		case 1:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Approved\" src=\"" + request.getContextPath() + "/images1/icons/approved.png\" border=\"0\">::::1");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>::::1");
			break;

		case 2:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Pulled\" src=\"" + request.getContextPath() + "/images1/icons/pullout.png\" border=\"0\">::::2");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" title=\"Pulled\"></i>::::2");
			break;

		case 3:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Submited\" src=\"" + request.getContextPath() + "/images1/icons/re_submit.png\" border=\"0\">::::3");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Submited\" ></i>::::3");
			break;
		}
	}

	public void getNotificationStatusMessage(int nStatus, String str) {

		switch (nStatus) {

		case 0:
			request.setAttribute(
					"STATUS_MSG", "<img width=\"20px\" title=\"Pending\" src=\"" + request.getContextPath() + "/images1/" 
					+ (("E".equalsIgnoreCase(str)) ? "mail_disbl.png" : "mob_disbl.png") + "\" border=\"0\">&nbsp;");
			break;

		case 1:
			request.setAttribute( "STATUS_MSG", "<img width=\"20px\" title=\"Approved\" src=\"" + request.getContextPath()
							+ "/images1/" + (("E".equalsIgnoreCase(str)) ? "mail_enbl.png" : "mob_enbl.png") + "\" border=\"0\">&nbsp;");
			break;
		}
	}

	String emp_id;
	String positions;
	String job_code;
	String Level_name;
	String designation_name;
	String grade_name;
	String location_name;
	String skills_name;
	String services;

//	@Override
//	public void run() {
	public void sendMail(String strId) {
//		System.out.println("++++++Thread example UpdateADRRequest++++++");
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);

		try{
			con = db.makeConnection(con);
			Map<String, String> hmRecruitmentData = CF.getRecruitmentDetails(con, uF, CF, request, strId);	
			String strAddedBy = getAddedByName(con, uF, strId);
			
			if(strAddedBy != null && !strAddedBy.equals("")){
				String strDomain = request.getServerName().split("\\.")[0]; 
				Notifications nF = new Notifications(N_RECRUITMENT_APPROVAL, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrEmpId(strAddedBy);
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
		
				nF.setStrRecruitmentDesignation(hmRecruitmentData.get("DESIG_NAME"));
				nF.setStrRecruitmentGrade(hmRecruitmentData.get("GRADE_NAME"));
				nF.setStrRecruitmentLevel(hmRecruitmentData.get("LEVEL_NAME"));
				nF.setStrRecruitmentPosition(hmRecruitmentData.get("POSITIONS"));
				nF.setStrRecruitmentWLocation(hmRecruitmentData.get("WLOC_NAME"));
				nF.setStrRecruitmentSkill(hmRecruitmentData.get("SKILLS_NAME"));
				nF.setEmailTemplate(true);		
				nF.setStrJobTitle(hmRecruitmentData.get("JOB_TITLE"));//Created By Dattatray Date : 05-10-21
				nF.sendNotifications();
			}
			
		Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);
		String empWlocation = hmEmpWLocation.get(strSessionEmpId);
//		String panel_employee_id = "";
		pst = con.prepareStatement("select emp_per_id from employee_personal_details epd, employee_official_details eod, user_details ud " +
				"where epd.emp_per_id = eod.emp_id and epd.is_alive=true and epd.emp_per_id = ud.emp_id and ud.usertype_id = 7 " +
				"and eod.wlocation_id = ? and emp_per_id != ?");
		pst.setInt(1, uF.parseToInt(empWlocation));
		pst.setInt(2, uF.parseToInt(strAddedBy));
		rst = pst.executeQuery();
//		System.out.println("new Date ===> " + new Date());
//		System.out.println("pst ===> "+pst);
		while (rst.next()) {
			//Map<String, String> hmEmpInner = hmEmpInfo.get(rst.getString("emp_per_id"));

			if(rst.getString("emp_per_id") != null && !rst.getString("emp_per_id").equals("")){
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_RECRUITMENT_APPROVAL, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrEmpId(rst.getString("emp_per_id"));
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
		
				nF.setStrRecruitmentDesignation(hmRecruitmentData.get("DESIG_NAME"));
				nF.setStrRecruitmentGrade(hmRecruitmentData.get("GRADE_NAME"));
				nF.setStrRecruitmentLevel(hmRecruitmentData.get("LEVEL_NAME"));
				nF.setStrRecruitmentPosition(hmRecruitmentData.get("POSITIONS"));
				nF.setStrRecruitmentWLocation(hmRecruitmentData.get("WLOC_NAME"));
				nF.setStrRecruitmentSkill(hmRecruitmentData.get("SKILLS_NAME"));
				nF.setStrJobTitle(hmRecruitmentData.get("JOB_TITLE"));//Created By Dattatray Date : 05-10-21
//				nF.setStrRecruitmentDesignation(getDesignation_name());
//				nF.setStrRecruitmentGrade(getGrade_name());
//				nF.setStrRecruitmentLevel(getLevel_name());
//				nF.setStrRecruitmentPosition(getPositions());
//				nF.setStrRecruitmentWLocation(getLocation_name());
//				nF.setStrRecruitmentProfile(getServices());
//				nF.setStrRecruitmentSkill(getSkills_name());
				nF.setEmailTemplate(true);		
				nF.sendNotifications();
			} 
		}
		rst.close();
		pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private String getAddedByName(Connection con, UtilityFunctions uF, String strId) {
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		String strAddedBy = null;
		try{
			
			String queryy = "select rd.added_by,ud.usertype_id from recruitment_details rd, user_details ud " +
					"where recruitment_id = ? and rd.added_by = ud.emp_id"; // and ud.usertype_id = 2
			pst = con.prepareStatement(queryy);
			pst.setInt(1, uF.parseToInt(strId));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				strAddedBy = rst.getString("added_by");
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
		return strAddedBy;
	}

	
//	private void getRecruitmentDetails() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rst = null;
//		Database db = new Database();
//		db.setRequest(request);
//
//		UtilityFunctions uF = new UtilityFunctions();
//
//		con = db.makeConnection(con);
//
//		try {
////			System.out.println("in getRecruitmentDetails strid " + getStrId());
//
//			StringBuilder strQuery = new StringBuilder();
//
//			strQuery.append("select d.designation_code,d.designation_name,g.grade_code,g.grade_name,w.wlocation_name,r.no_position,r.job_code," +
//					"r.added_by,l.level_code,l.level_name,r.skills from recruitment_details r,grades_details g,work_location_info w," +
//					"designation_details d,department_info di,employee_personal_details e,level_details l where r.grade_id = g.grade_id and" +
//					"r.wlocation = w.wlocation_id and r.designation_id = d.designation_id and r.added_by = e.emp_per_id and" +
//					"r.dept_id=di.dept_id and r.level_id=l.level_id and r.recruitment_id=?");
//			
//			pst = con.prepareStatement(strQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrId()));
//			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			while (rst.next()) {
////				System.out.println("in rst.next");
//				designation_name = "[" + rst.getString(1) + "] "
//						+ rst.getString(2);
//				grade_name = "[" + rst.getString(3) + "] " + rst.getString(4);
//				location_name = rst.getString(5);
//				positions = rst.getString(6);
//				job_code = rst.getString(7);
//				services = rst.getString(8);
//				emp_id = rst.getString(9);
//				Level_name = "[" + rst.getString(10) + "] " + rst.getString(11);
//				skills_name = rst.getString(12);
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rst);
//			db.closeConnection(con);
//		}
//
//	}

	public String getStrId() {
		return strId;
	}

	public void setStrId(String strId) {
		this.strId = strId;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getPositions() {
		return positions;
	}

	public void setPositions(String positions) {
		this.positions = positions;
	}

	public String getJob_code() {
		return job_code;
	}

	public void setJob_code(String job_code) {
		this.job_code = job_code;
	}

	public String getLevel_name() {
		return Level_name;
	}

	public void setLevel_name(String level_name) {
		Level_name = level_name;
	}

	public String getDesignation_name() {
		return designation_name;
	}

	public void setDesignation_name(String designation_name) {
		this.designation_name = designation_name;
	}

	public String getGrade_name() {
		return grade_name;
	}

	public void setGrade_name(String grade_name) {
		this.grade_name = grade_name;
	}

	public String getLocation_name() {
		return location_name;
	}

	public void setLocation_name(String location_name) {
		this.location_name = location_name;
	}

	public String getSkills_name() {
		return skills_name;
	}

	public void setSkills_name(String skills_name) {
		this.skills_name = skills_name;
	}

	public String getServices() {
		return services;
	}

	public void setServices(String services) {
		this.services = services;
	}

	String strDomain;
	public void setDomain(String strDomain) {
		this.strDomain=strDomain;
	}

	public String getStrDomain() {
		return strDomain;
	}

	@Override
	public void run() {
		
	}

	public String getmReason() {
		return mReason;
	}

	public void setmReason(String mReason) {
		this.mReason = mReason;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCandiAppId() {
		return candiAppId;
	}

	public void setCandiAppId(String candiAppId) {
		this.candiAppId = candiAppId;
	}

}