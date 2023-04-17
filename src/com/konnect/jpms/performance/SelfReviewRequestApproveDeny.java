package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.recruitment.UpdateADRRequest;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SelfReviewRequestApproveDeny extends ActionSupport implements ServletRequestAware, IStatements, Runnable {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null;

	private CommonFunctions CF;

	private String reviewId;
	private String strStatus;
	private String strReason;
	private String type;
	private String userType;
	private String appFreqId;
	
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

		updateRequest();  
		/*System.out.println("getType ====> " + getType());
		System.out.println("reviewId==>"+getReviewId()+"==>appFreqId==>"+getAppFreqId());*/
		if (getType() != null && getType().equals("type")) {
             return DASHBOARD;
         }

		return SUCCESS;

	}
	
	
	private void updateRequest() {
		Connection con = null;
		PreparedStatement pst = null;
		java.sql.ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		UtilityFunctions uF = new UtilityFunctions();
				
		if (getReviewId() != null && strStatus != null) {

			try {
				con = db.makeConnection(con);
				Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
				String reviewName = CF.getReviewNameById(con, uF, getReviewId());
				boolean isPublishReq = false; 
				pst = con.prepareStatement("select publish_request_to_workflow from appraisal_details where appraisal_details_id=?");
				pst.setInt(1, uF.parseToInt(getReviewId()));
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					if(rs.getString("publish_request_to_workflow")!= null && !rs.getString("publish_request_to_workflow").equals("")) {
						isPublishReq = rs.getBoolean("publish_request_to_workflow");
					}
				}
				rs.close();
				pst.close();
//				System.out.println("isPublishReq==>"+isPublishReq+"==>getStrStatus()==>"+getStrStatus());
//				System.out.println("getUserType==>"+getUserType()+"==>strUserTypeId()==>"+strUserTypeId);
				if (uF.parseToBoolean(CF.getIsWorkFlow())) {
					if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN) && uF.parseToInt(getUserType()) == uF.parseToInt(strUserTypeId)) { //!checkEmpList.contains(strSessionEmpId) &&
						
						String query = "update appraisal_details set is_publish=?, publish_is_approved =?, publish_approve_deny_by=?,publish_approve_deny_reason=? where appraisal_details_id=? and publish_request_to_workflow = true";
						pst = con.prepareStatement(query);
						pst.setBoolean(1, uF.parseToBoolean(strStatus));
						pst.setInt(2, uF.parseToInt(strStatus));
						pst.setInt(3, uF.parseToInt(strSessionEmpId));
						pst.setString(4, getStrReason());
						pst.setInt(5, uF.parseToInt(getReviewId()));
//						System.out.println("pst1==>"+pst);
						pst.execute();
						pst.close();
						
						if(isPublishReq && uF.parseToInt(getStrStatus()) == 1) {
							String query1 = "update appraisal_details_frequency set is_appraisal_publish=? where appraisal_id=? ";
							pst = con.prepareStatement(query1);
							pst.setBoolean(1, true);
							pst.setInt(2, uF.parseToInt(getReviewId()));
//							System.out.println("pst2==>"+pst);
							pst.execute();
							pst.close();
						}else if(uF.parseToInt(getStrStatus()) != 1){
							String query1 = "update appraisal_details_frequency set is_appraisal_publish=? where appraisal_id=? ";
							pst = con.prepareStatement(query1);
							pst.setBoolean(1, uF.parseToBoolean(strStatus));
							pst.setInt(2, uF.parseToInt(getReviewId()));
//							System.out.println("pst3==>"+pst);
							pst.execute();
							pst.close();
						}	
							 pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_SELF_REVIEW+"' " +
							 	"and is_approved=0 and emp_id=? and user_type_id=? order by work_flow_id");
			                 pst.setInt(1, uF.parseToInt(getReviewId()));
			                 pst.setInt(2, uF.parseToInt(strSessionEmpId));
			                 pst.setInt(3, uF.parseToInt(getUserType()));
//			                 System.out.println("pst4==>"+pst);
			                 rs = pst.executeQuery();
			                 int work_id = 0;
			                 while (rs.next()) {
			                     work_id = rs.getInt("work_flow_id");
			                     break;
			                 }
			     			rs.close();
			     			pst.close();
						
		                 pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,approve_date=?,reason=? WHERE work_flow_id=? ");
		                 pst.setInt(1, uF.parseToInt(strStatus));
		                 pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
		                 pst.setString(3, getStrReason());
		                 pst.setInt(4, work_id);
//		                 System.out.println("pst5==>"+pst);
		                 pst.execute();
		     			 pst.close();
		     			 
						getStatusMessage(uF.parseToInt(strStatus));
	
						String strAddedBy = getAddedByName(con, uF, getReviewId());
						if(uF.parseToInt(strAddedBy) > 0) {
							String strDomain = request.getServerName().split("\\.")[0];
							String alertData = "<div style=\"float: left;\"> Your Self Review ("+reviewName+") has been Approved by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
							String alertAction = "MyHR.action?pType=WR";
							
							UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strAddedBy);
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();

						}

					} else if(strUserType != null && strUserType.equalsIgnoreCase(ADMIN) && uF.parseToInt(getUserType()) != uF.parseToInt(strUserTypeId)) {
						pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,approve_date=?,reason=? WHERE effective_id=? and effective_type='"+WORK_FLOW_SELF_REVIEW+"'");
		                 pst.setInt(1, uF.parseToInt(strStatus));
		                 pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
		                 pst.setString(3, getStrReason());
		                 pst.setInt(4, uF.parseToInt(getReviewId()));
//		               /  System.out.println("pst18==>"+pst);
		                 pst.execute();
//		                 System.out.println("pst ===>> " + pst);
		     			 pst.close();
		     			 
		     			String query = "update appraisal_details set is_publish=?, publish_is_approved =?, publish_approve_deny_by=?,publish_approve_deny_reason=? where appraisal_details_id=? and publish_request_to_workflow = true";
						pst = con.prepareStatement(query);
						pst.setBoolean(1, uF.parseToBoolean(strStatus));
						pst.setInt(2, uF.parseToInt(strStatus));
						pst.setInt(3, uF.parseToInt(strSessionEmpId));
						pst.setString(4, getStrReason());
						pst.setInt(5, uF.parseToInt(getReviewId()));
//						System.out.println("pst17==>"+pst);
						pst.execute();
						pst.close();
						
						if(isPublishReq && uF.parseToInt(getStrStatus()) == 1) {
							String query1 = "update appraisal_details_frequency set is_appraisal_publish=? where appraisal_id=? ";
							pst = con.prepareStatement(query1);
							pst.setBoolean(1, true);
							pst.setInt(2, uF.parseToInt(getReviewId()));
//							System.out.println("pst15==>"+pst);
							pst.execute();
							pst.close();
						}else if(uF.parseToInt(getStrStatus()) != 1){
							String query1 = "update appraisal_details_frequency set is_appraisal_publish=? where appraisal_id=? ";
							pst = con.prepareStatement(query1);
							pst.setBoolean(1, uF.parseToBoolean(strStatus));
							pst.setInt(2, uF.parseToInt(getReviewId()));
//							System.out.println("pst16==>"+pst);
							pst.execute();
							pst.close();
						}
	 
						String strAddedBy = getAddedByName(con, uF, getReviewId());
						if(uF.parseToInt(strAddedBy) > 0) {
							String strDomain = request.getServerName().split("\\.")[0];
							String alertData = "<div style=\"float: left;\"> Your Self Review ("+reviewName+") has been Approved by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
							String alertAction = "MyHR.action?pType=WR";
							
							UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strAddedBy);
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
						}
					}else {
						 pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_SELF_REVIEW+"' " +
						 	"and is_approved=0 and emp_id=? and user_type_id=? order by work_flow_id");
		                 pst.setInt(1, uF.parseToInt(getReviewId()));
		                 pst.setInt(2, uF.parseToInt(strSessionEmpId));
	                	 pst.setInt(3, uF.parseToInt(getUserType()));
//	                	 System.out.println("pst6==>"+pst);
		                 rs = pst.executeQuery();
		                 int work_id = 0;
		                 while (rs.next()) {
		                     work_id = rs.getInt("work_flow_id");
		                     break;
		                 }
		     			rs.close();
		     			pst.close();
		     
		     			 pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,approve_date=?,reason=? WHERE work_flow_id=?");
		                 pst.setInt(1, uF.parseToInt(strStatus));
		                 pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
		                 pst.setString(3, getStrReason());
		                 pst.setInt(4, work_id);
//		                 System.out.println("pst7==>"+pst);
		                 pst.execute();
//		                 System.out.println("pst ===>> " + pst);
		     			 pst.close();
		                 
		                 boolean flag = true;
 
		                 pst = con.prepareStatement("select * from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_SELF_REVIEW+"' " +
							" and work_flow_id=(select min(work_flow_id) from work_flow_details where effective_id=? " +
							" and is_approved=0 and effective_type='"+WORK_FLOW_SELF_REVIEW+"' and member_position not in " +
							" (select member_position from work_flow_details where effective_id=? and is_approved=1 and effective_type='"+WORK_FLOW_SELF_REVIEW+"' )) " +
							" order by work_flow_id");
		                 pst.setInt(1, uF.parseToInt(getReviewId()));
		                 pst.setInt(2, uF.parseToInt(getReviewId()));
		                 pst.setInt(3, uF.parseToInt(getReviewId()));
//		                 System.out.println("pst8==>"+pst);
		                 rs = pst.executeQuery();		                 
		                 while (rs.next()) {
		                     flag = false;
		                 }
		                 rs.close();
		     			 pst.close();
		     			 
		     			getStatusMessage(uF.parseToInt(strStatus));
		     			
		     			if (flag) {
		     			
							String query = "update appraisal_details set is_publish=?, publish_is_approved =?, publish_approve_deny_by=?,publish_approve_deny_reason=? where appraisal_details_id=? and publish_request_to_workflow = true";
							pst = con.prepareStatement(query);
							pst.setBoolean(1, uF.parseToBoolean(strStatus));
							pst.setInt(2, uF.parseToInt(strStatus));
							pst.setInt(3, uF.parseToInt(strSessionEmpId));
							pst.setString(4, getStrReason());
							pst.setInt(5, uF.parseToInt(getReviewId()));
//							System.out.println("pst14==>"+pst);
							pst.execute();
							pst.close();
							
							if(isPublishReq && uF.parseToInt(getStrStatus()) == 1) {
								String query1 = "update appraisal_details_frequency set is_appraisal_publish=? where appraisal_id=? ";
								pst = con.prepareStatement(query1);
								pst.setBoolean(1, true);
								pst.setInt(2, uF.parseToInt(getReviewId()));
//								System.out.println("pst9==>"+pst);
								pst.execute();
								pst.close();
							}else if(uF.parseToInt(getStrStatus()) != 1){
								String query1 = "update appraisal_details_frequency set is_appraisal_publish=? where appraisal_id=? ";
								pst = con.prepareStatement(query1);
								pst.setBoolean(1, uF.parseToBoolean(strStatus));
								pst.setInt(2, uF.parseToInt(getReviewId()));
//							/	System.out.println("pst10==>"+pst);
								pst.execute();
								pst.close();
							}
		 
							String strAddedBy = getAddedByName(con, uF, getReviewId());
							if(uF.parseToInt(strAddedBy) > 0) {
								String strDomain = request.getServerName().split("\\.")[0];
								String alertData = "<div style=\"float: left;\"> Your Self Review ("+reviewName+") has been Approved by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
								String alertAction = "MyHR.action?pType=WR";
								
								UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(strAddedBy);
								userAlerts.setStrData(alertData);
								userAlerts.setStrAction(alertAction);
								userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
								userAlerts.setStatus(INSERT_WR_ALERT);
								Thread t = new Thread(userAlerts);
								t.run();
								
							}
							
		     			}
		     			
					}
				} else {
				
					String query = "update appraisal_details set is_publish=?, publish_is_approved =?, publish_approve_deny_by=?,publish_approve_deny_reason=? where appraisal_details_id=? and publish_request_to_workflow = true";
					pst = con.prepareStatement(query);
					pst.setBoolean(1, uF.parseToBoolean(strStatus));
					pst.setInt(2, uF.parseToInt(strStatus));
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
					pst.setString(4, getStrReason());
					pst.setInt(5, uF.parseToInt(getReviewId()));
//					System.out.println("pst11==>"+pst);
					pst.execute();
					pst.close();
					
					if(isPublishReq && uF.parseToInt(getStrStatus()) == 1) {
						String query1 = "update appraisal_details_frequency set is_appraisal_publish=? where appraisal_id=? ";
						pst = con.prepareStatement(query1);
						pst.setBoolean(1, true);
						pst.setInt(2, uF.parseToInt(getReviewId()));
//						System.out.println("pst12==>"+pst);
						pst.execute();
						pst.close();
					}else if(uF.parseToInt(getStrStatus()) != 1){
						String query1 = "update appraisal_details_frequency set is_appraisal_publish=? where appraisal_id=? ";
						pst = con.prepareStatement(query1);
						pst.setBoolean(1, uF.parseToBoolean(strStatus));
						pst.setInt(2, uF.parseToInt(getReviewId()));
//						System.out.println("pst13==>"+pst);
						pst.execute();
						pst.close();
					}
					getStatusMessage(uF.parseToInt(strStatus));

					String strAddedBy = getAddedByName(con, uF, getReviewId());
					if(uF.parseToInt(strAddedBy) > 0) {
						String strDomain = request.getServerName().split("\\.")[0];
						String alertData = "<div style=\"float: left;\"> Your Self Review ("+reviewName+") has been Approved by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
						String alertAction = "MyHR.action?pType=WR";
						
						UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strAddedBy);
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
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

	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void getStatusMessage(int nStatus) {

		switch (nStatus) {

		case -1:
			 /*request.setAttribute("STATUS_MSG", "<img title=\"Denied\" src=\"" + request.getContextPath() + "/images1/icons/denied.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
			break;

		case 0:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Pending\" src=\"" + request.getContextPath() + "/images1/icons/pending.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Pending\"></i>");
			
			break;

		case 1:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Approved\" src=\"" + request.getContextPath() + "/images1/icons/approved.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\" ></i>");
			
			break;

		case 2:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Pulled\" src=\"" + request.getContextPath() + "/images1/icons/pullout.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" title=\"Pulled\"></i>");
			break;

		case 3:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Submited\" src=\"" + request.getContextPath() + "/images1/icons/re_submit.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Submited\"></i>");
			
			
			break;
		}
	}


	private String getAddedByName(Connection con, UtilityFunctions uF, String strId) {
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		String strAddedBy = null;
		try{
			
			String queryy = "select added_by from appraisal_details where appraisal_details_id = ?"; // and ud.usertype_id = 2
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

	public String getReviewId() {
		return reviewId;
	}

	public void setReviewId(String reviewId) {
		this.reviewId = reviewId;
	}

	public String getStrStatus() {
		return strStatus;
	}

	public void setStrStatus(String strStatus) {
		this.strStatus = strStatus;
	}

	public String getStrReason() {
		return strReason;
	}

	public void setStrReason(String strReason) {
		this.strReason = strReason;
	}

	private String strDomain;
	public void setDomain(String strDomain) {
		this.strDomain=strDomain;
	}

	public String getStrDomain() {
		return strDomain;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
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


	public String getAppFreqId() {
		return appFreqId;
	}


	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}
}
