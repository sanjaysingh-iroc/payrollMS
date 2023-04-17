package com.konnect.jpms.requsitions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
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

public class UpdateReimbursements extends ActionSupport  implements ServletRequestAware, IStatements {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	  
	private CommonFunctions CF;
	
	private String strLeaveTypeId;
	private String strEmpId;
	private String strNod;
	private String type;
	private String mReason;
	private String userType;
	private String currUserType;
	private String paycycle;
	public String execute() {
	
		session = request.getSession();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
		
		String strStatus = (String)request.getParameter("S");
		String strId = (String)request.getParameter("RID");
		String strType = (String)request.getParameter("T");
		String strMode = (String)request.getParameter("M");
		
		strLeaveTypeId = (String)request.getParameter("LTID");
		strEmpId = (String)request.getParameter("EMPID");
		strNod = (String)request.getParameter("NOD");
		
//		System.out.println("getCurrentUserType()=====>"+getCurrUserType());
		if(strType!=null && strType.equals("bulk")) {
//			System.out.println("in bulk ");
//			System.out.println("strId=====>"+strId);
//			System.out.println("strStatus=====>"+strStatus);
//			System.out.println("strMode=====>"+strMode);
//			System.out.println("getUserType() ===>> " + getUserType());
			updateBulkReimbursements(strStatus, strId, strMode);
		} else if(strMode!=null && strMode.equalsIgnoreCase("reset")) {
			resetRequest(strStatus, strId, strType);
		}else if(strMode!=null && strMode.equalsIgnoreCase("D")) {
			deleteRequest(strStatus, strId, strType);
		}else{			
			updateRequest(strStatus, strId, strType, strMode);
			if(getType()!=null && getType().equals("type")) {
//				System.out.println("in update getType()=====>"+getType());
				return DASHBOARD;
			}
		}
		
		return "success";
		
	}
	
	
	private void updateBulkReimbursements(String strStatus, String strBulkId, String strMode) {
		
		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
//			System.out.println("strBulkId ===>> " + strBulkId);
			con = db.makeConnection(con);
			List<String> alReimId = new ArrayList<String>();
			if(strBulkId != null && !strBulkId.equals("")) {
				alReimId = Arrays.asList(strBulkId.split(","));
			}
			
			for(int i = 0; alReimId != null && i < alReimId.size(); i++) {
				String strId = alReimId.get(i).trim();
				
				pst = con.prepareStatement("select parent_id from emp_reimbursement where reimbursement_id=?");
				pst.setInt(1, uF.parseToInt(strId));
				rs = pst.executeQuery();
				String reimbParentId = null;
				while(rs.next()) {
					reimbParentId = rs.getString("parent_id");					
				}
				rs.close();
				pst.close();
					
				boolean flag = true;
				boolean flagAdmin = false;
				if(uF.parseToBoolean(CF.getIsWorkFlow())) {
//					pst = con.prepareStatement("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_REIMBURSEMENTS+"'" +
//						" and effective_id=? order by effective_id,member_position");
//					pst.setInt(1, uF.parseToInt(strId));
//					rs = pst.executeQuery();			
//					List<String> checkEmpList=new ArrayList<String>();
//					while(rs.next()) {
//						checkEmpList.add(rs.getString("emp_id"));					
//					}
//					rs.close();
//					pst.close();
//					if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && !checkEmpList.contains(strSessionEmpId) ) {
					if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
						StringBuilder sbQuery = new StringBuilder();
						sbQuery.append("update emp_reimbursement set approval_1=?, approval_1_emp_id=?, approval_1_date=?, " +
								"approval_2=?, approval_2_emp_id=?, approval_2_date=?,approve_reason=? where reimbursement_id>0 ");
						if(uF.parseToInt(reimbParentId) > 0) {
							sbQuery.append(" and parent_id="+uF.parseToDouble(reimbParentId));
						} else {
							sbQuery.append(" and reimbursement_id="+uF.parseToDouble(strId));
						}
						pst = con.prepareStatement(sbQuery.toString());
						pst.setInt(1, uF.parseToInt(strStatus));
						pst.setInt(2, uF.parseToInt(strSessionEmpId));
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(4, uF.parseToInt(strStatus));
						pst.setInt(5, uF.parseToInt(strSessionEmpId));
						pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
	                    pst.setString(7, getmReason());
//						pst.setInt(8, uF.parseToInt(strId));
						pst.execute();
						pst.close();
						flagAdmin = true;
					} else {
						
						StringBuilder sbQuery = new StringBuilder();
						sbQuery.append("select reimbursement_id from emp_reimbursement where reimbursement_id>0 ");
						if(uF.parseToInt(reimbParentId) > 0) {
							sbQuery.append(" and parent_id="+uF.parseToDouble(reimbParentId));
						} else {
							sbQuery.append(" and reimbursement_id="+uF.parseToDouble(strId));
						}
						pst = con.prepareStatement(sbQuery.toString());
						rs = pst.executeQuery();
						StringBuilder reimbIds = null;
						while(rs.next()) {
							if(reimbIds == null) {
								reimbIds = new StringBuilder();
								reimbIds.append(rs.getString("reimbursement_id"));
							} else {
								reimbIds.append(","+rs.getString("reimbursement_id"));
							}
						}
						rs.close();
						pst.close();
						
						if(reimbIds != null) {
//							System.out.println("reimbIds ===>> " + reimbIds.toString());
	//						pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' and is_approved=0 and emp_id=? order by work_flow_id");
							pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id in ("+reimbIds.toString()+") " +
								"and effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' and is_approved=0 and emp_id=? and user_type_id=? order by work_flow_id");
	//						pst.setInt(1, uF.parseToInt(strId));
							pst.setInt(1, uF.parseToInt(strSessionEmpId));
							pst.setInt(2, uF.parseToInt(getUserType()));
//							System.out.println("pst ===>> " + pst);
							rs = pst.executeQuery();
							int work_id=0;
							StringBuilder workflowIds = null;
							while(rs.next()) {
								if(workflowIds == null) {
									workflowIds = new StringBuilder();
									workflowIds.append(rs.getString("work_flow_id"));
								} else {
									workflowIds.append(","+rs.getString("work_flow_id"));
								}
//								work_id=rs.getInt("work_flow_id");
//								break;
							}
							rs.close();
							pst.close();
							
//								System.out.println("work_id===+>"+work_id);
							if(workflowIds != null) {
//								System.out.println("workflowIds===+>"+workflowIds.toString());
	//							pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,approve_date=?,reason=? WHERE work_flow_id=?");
								pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,approve_date=?,reason=? WHERE work_flow_id in ("+workflowIds.toString()+")");
								pst.setInt(1, uF.parseToInt(strStatus));
								pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			                    pst.setString(3, getmReason());
	//							pst.setInt(4, work_id);
								pst.execute();			
								pst.close();  
								
								pst = con.prepareStatement("select * from work_flow_details where effective_id in ("+reimbIds.toString()+") and effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' " +
									" and work_flow_id=(select min(work_flow_id) from work_flow_details where effective_id in ("+reimbIds.toString()+") " +
									" and is_approved=0 and effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' and member_position not in " +
									" (select member_position from work_flow_details where effective_id in ("+reimbIds.toString()+") and is_approved=1 and effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' )) " +
									" order by work_flow_id");
//								pst.setInt(1, uF.parseToInt(strId));
//								pst.setInt(2, uF.parseToInt(strId));
//								pst.setInt(3, uF.parseToInt(strId));
								rs = pst.executeQuery();
								while(rs.next()) {
									flag=false;
								}
								rs.close();
								pst.close();
								
								if(flag) {				
									pst = con.prepareStatement("update emp_reimbursement set approval_1=?, approval_1_emp_id=?, approval_1_date=?, " +
											"approval_2=?, approval_2_emp_id=?, approval_2_date=?, approve_reason=?  where reimbursement_id in ("+reimbIds.toString()+")");
									pst.setInt(1, uF.parseToInt(strStatus));
									pst.setInt(2, uF.parseToInt(strSessionEmpId));
									pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(4, uF.parseToInt(strStatus));
									pst.setInt(5, uF.parseToInt(strSessionEmpId));
									pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
				                    pst.setString(7, getmReason());
//									pst.setInt(8, uF.parseToInt(strId));
									pst.execute();
									pst.close();
								} else if(uF.parseToInt(strStatus) == -1) {
									pst = con.prepareStatement("update emp_reimbursement set approval_1=?, approval_1_emp_id=?, approval_1_date=?, " +
											"approval_2=?, approval_2_emp_id=?, approval_2_date=?, approve_reason=?  where reimbursement_id in ("+reimbIds.toString()+")");
									pst.setInt(1, uF.parseToInt(strStatus));
									pst.setInt(2, uF.parseToInt(strSessionEmpId));
									pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(4, uF.parseToInt(strStatus));
									pst.setInt(5, uF.parseToInt(strSessionEmpId));
									pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
				                    pst.setString(7, getmReason());
//									pst.setInt(8, uF.parseToInt(strId));
									pst.execute();
									pst.close();							
								}
							}
						}
					}
				} else {
					if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
//						pst = con.prepareStatement("update emp_reimbursement set approval_1=?, approval_1_emp_id=?, approval_1_date=?, " +
//								"approval_2=?, approval_2_emp_id=?, approval_2_date=?,approve_reason=?  where reimbursement_id = ?");
						StringBuilder sbQuery = new StringBuilder();
						sbQuery.append("update emp_reimbursement set approval_1=?, approval_1_emp_id=?, approval_1_date=?, " +
								"approval_2=?, approval_2_emp_id=?, approval_2_date=?,approve_reason=? where reimbursement_id>0 ");
						if(uF.parseToInt(reimbParentId) > 0) {
							sbQuery.append(" and parent_id="+uF.parseToDouble(reimbParentId));
						} else {
							sbQuery.append(" and reimbursement_id="+uF.parseToDouble(strId));
						}
						pst = con.prepareStatement(sbQuery.toString());
						pst.setInt(1, uF.parseToInt(strStatus));
						pst.setInt(2, uF.parseToInt(strSessionEmpId));
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(4, uF.parseToInt(strStatus));
						pst.setInt(5, uF.parseToInt(strSessionEmpId));
						pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
	                    pst.setString(7, getmReason());
//						pst.setInt(8, uF.parseToInt(strId));		
						pst.execute();
						pst.close();
						flagAdmin = true;
					} /*else if(!uF.parseToBoolean(CF.getIsWorkFlow()) && strUserType.equalsIgnoreCase(HRMANAGER)) {
						pst = con.prepareStatement("update emp_reimbursement set approval_1=?, approval_1_emp_id=?, approval_1_date=?, " +
								"approval_2=?, approval_2_emp_id=?, approval_2_date=?,approve_reason=?  where reimbursement_id = ?");
						pst.setInt(1, uF.parseToInt(strStatus));
						pst.setInt(2, uF.parseToInt(strSessionEmpId));
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(4, uF.parseToInt(strStatus));
						pst.setInt(5, uF.parseToInt(strSessionEmpId));
						pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
	                    pst.setString(7, getmReason());
						pst.setInt(8, uF.parseToInt(strId));
						pst.execute();
						pst.close();
					}*/
				}
				
				
				if(strMode!=null && (strMode.equalsIgnoreCase("AA") || strMode.equalsIgnoreCase("HRA"))) {
					Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
					if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
//					Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
					Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetailsForPDF(con);
					if(hmCurrency == null) hmCurrency = new HashMap<String, Map<String,String>>();
									
					String strAmount="";
					if(uF.parseToInt(reimbParentId) > 0) {
						pst1 = con.prepareStatement("select sum(reimbursement_amount) as reimbursement_amount from emp_reimbursement where parent_id = ?");
						pst1.setInt(1, uF.parseToInt(reimbParentId));
						rs1 = pst1.executeQuery();
//						String strEmpId=null;
						if(rs1.next()) {
							strAmount = rs1.getString("reimbursement_amount");
						}
						rs1.close();
						pst1.close();
					}
					
					pst1 = con.prepareStatement("select * from emp_reimbursement where reimbursement_id = ?");
					pst1.setInt(1, uF.parseToInt(strId));
					rs1 = pst1.executeQuery();
					if(rs1.next()) {
						String strCurrId = hmEmpCurrency.get(rs1.getString("emp_id"));
						Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
						if (hmCurrencyInner == null)hmCurrencyInner = new HashMap<String, String>();
						String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
						
						String strDomain = request.getServerName().split("\\.")[0];
						Notifications nF = new Notifications(N_EMPLOYEE_REIMBURSEMENT_APPROVAL, CF);
						nF.setDomain(strDomain);
						nF.request = request;
						nF.setStrEmpId(rs1.getString("emp_id"));
//						nF.setStrHostAddress(request.getRemoteHost());
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrEmpReimbursementFrom(uF.getDateFormat(rs1.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));
						nF.setStrEmpReimbursementTo(uF.getDateFormat(rs1.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
						nF.setStrEmpReimbursementPurpose(rs1.getString("reimbursement_purpose"));
						nF.setStrEmpReimbursementType(rs1.getString("reimbursement_type"));
						if(uF.parseToInt(reimbParentId) > 0) {
							nF.setStrEmpReimbursementAmount(strAmount);
						} else {
							nF.setStrEmpReimbursementAmount(rs1.getString("reimbursement_amount"));
						}
						nF.setStrEmpReimbursementDate(uF.getDateFormat(rs1.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
						nF.setStrEmpReimbursementCurrency(strCurrSymbol);
						
						if(uF.parseToInt(strStatus)==1) {
							nF.setStrApprvedDenied("approved");	
						}else if(uF.parseToInt(strStatus)==-1) {
							nF.setStrApprvedDenied("denied");
						}
						nF.setEmailTemplate(true);
						nF.sendNotifications();
					}
					rs1.close();
					pst1.close();
				}
				
//				System.out.println("flagAdmin ===>> " + flagAdmin + " -- flag ===>> " + flag + " -- strStatus ===>> " + strStatus);
				
				if(flagAdmin || flag || uF.parseToInt(strStatus) == -1) {
					String strAmount="";
					if(uF.parseToInt(reimbParentId) > 0) {
						pst1 = con.prepareStatement("select sum(reimbursement_amount) as reimbursement_amount, emp_id from emp_reimbursement where parent_id = ? group by emp_id");
						pst1.setInt(1, uF.parseToInt(reimbParentId));
						rs1 = pst1.executeQuery();
//						String strEmpId=null;
						if(rs1.next()) {
							strEmpId=rs1.getString("emp_id");
							strAmount = rs1.getString("reimbursement_amount");
						}
						rs1.close();
						pst1.close();
					} else {
						pst1 = con.prepareStatement("select * from emp_reimbursement where reimbursement_id = ?");
						pst1.setInt(1, uF.parseToInt(strId));
						rs1 = pst1.executeQuery();
						if(rs1.next()) {
							strEmpId=rs1.getString("emp_id");
							strAmount = rs1.getString("reimbursement_amount");
						}
						rs1.close();
						pst1.close();
					}
					Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
					
					String strDomain = request.getServerName().split("\\.")[0];
					String strApproveDeny = "approved";
					if(uF.parseToInt(strStatus)== -1) {
						strApproveDeny = "denied";
					}
					String alertData = "<div style=\"float: left;\"> Your Claim ("+strAmount+") has been "+strApproveDeny+" by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
					String alertAction = "MyPay.action?pType=WR&callFrom=MyDashReimbursements";
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(strEmpId);
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
//					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//					userAlerts.setStrDomain(strDomain);
//					userAlerts.setStrEmpId(strEmpId);
//					userAlerts.set_type(REIM_APPROVAL_ALERT);
//					userAlerts.setStatus(INSERT_ALERT);
//					Thread t = new Thread(userAlerts);
//					t.run();
				}
				
				getStatusMessage(uF.parseToInt(strStatus));
			}
			
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


	private void resetRequest(String strStatus, String strId, String strType) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try{
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,approve_date=?,status=? WHERE effective_id=?");
			pst.setInt(1, 0);
			pst.setDate(2, null);
			pst.setInt(3, 1);
			pst.setInt(4, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
//		return nRequisitionId;
	} 


	public void updateRequest(String strStatus, String strId, String strType, String strMode) {
		
		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			String strReason = "";
			if(getmReason()!=null && !getmReason().trim().equals("") && !getmReason().trim().equalsIgnoreCase("NULL")) {
				strReason = getmReason(); 
	         }
			if(strType!=null && strType.equalsIgnoreCase("RIM")) {
				boolean flag = true;
				boolean flagAdmin = false;
				pst = con.prepareStatement("select parent_id from emp_reimbursement where reimbursement_id=?");
				pst.setInt(1, uF.parseToInt(strId));
				rs = pst.executeQuery();
				String reimbParentId = null;
				while(rs.next()) {
					reimbParentId = rs.getString("parent_id");					
				}
				rs.close();
				pst.close();
				
				if(uF.parseToBoolean(CF.getIsWorkFlow())) {
					if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
//						pst = con.prepareStatement("update emp_reimbursement set approval_1=?, approval_1_emp_id=?, approval_1_date=?, " +
//								"approval_2=?, approval_2_emp_id=?, approval_2_date=?,approve_reason=?  where reimbursement_id = ?");
						StringBuilder sbQuery = new StringBuilder();
						sbQuery.append("update emp_reimbursement set approval_1=?, approval_1_emp_id=?, approval_1_date=?, " +
								"approval_2=?, approval_2_emp_id=?, approval_2_date=?,approve_reason=? where reimbursement_id>0 ");
						if(uF.parseToInt(reimbParentId) > 0) {
							sbQuery.append(" and parent_id="+uF.parseToDouble(reimbParentId));
						} else {
							sbQuery.append(" and reimbursement_id="+uF.parseToDouble(strId));
						}
						pst = con.prepareStatement(sbQuery.toString());
						pst.setInt(1, uF.parseToInt(strStatus));
						pst.setInt(2, uF.parseToInt(strSessionEmpId));
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(4, uF.parseToInt(strStatus));
						pst.setInt(5, uF.parseToInt(strSessionEmpId));
						pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setString(7, strReason);
//						pst.setInt(8, uF.parseToInt(strId));
						pst.execute();
						pst.close();
						flagAdmin = true;
					} else {						
						
						StringBuilder sbQuery = new StringBuilder();
						sbQuery.append("select reimbursement_id from emp_reimbursement where reimbursement_id>0 ");
						if(uF.parseToInt(reimbParentId) > 0) {
							sbQuery.append(" and parent_id="+uF.parseToDouble(reimbParentId));
						} else {
							sbQuery.append(" and reimbursement_id="+uF.parseToDouble(strId));
						}
						pst = con.prepareStatement(sbQuery.toString());
//						pst.setInt(1, uF.parseToInt(reimbParentId));
						rs = pst.executeQuery();
						StringBuilder reimbIds = null;
						while(rs.next()) {
							if(reimbIds == null) {
								reimbIds = new StringBuilder();
								reimbIds.append(rs.getString("reimbursement_id"));
							} else {
								reimbIds.append(","+rs.getString("reimbursement_id"));
							}
						}
						rs.close();
						pst.close();
						
						if(reimbIds != null) {
							pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id in ("+reimbIds.toString()+") " +
									"and effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' and is_approved=0 and emp_id=? and user_type_id=? order by work_flow_id");
//							pst.setInt(1, uF.parseToInt(strId));
							pst.setInt(1, uF.parseToInt(strSessionEmpId));
		                    pst.setInt(2, uF.parseToInt(getUserType()));
							rs = pst.executeQuery();
							int work_id=0;
							StringBuilder workflowIds = null;
							while(rs.next()) {
								if(workflowIds == null) {
									workflowIds = new StringBuilder();
									workflowIds.append(rs.getString("work_flow_id"));
								} else {
									workflowIds.append(","+rs.getString("work_flow_id"));
								}
//								work_id=rs.getInt("work_flow_id");
//								break;
							}
							rs.close();
							pst.close();
							
//								System.out.println("work_id===+>"+work_id);
							
							if(workflowIds != null) {
								pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,approve_date=?,reason=? WHERE work_flow_id in ("+workflowIds.toString()+")");
								pst.setInt(1, uF.parseToInt(strStatus));
								pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setString(3, strReason);
//								pst.setInt(4, work_id);
								pst.execute();			
								pst.close();  
												
								pst = con.prepareStatement("select * from work_flow_details where effective_id in ("+reimbIds.toString()+") and effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' " +
									" and work_flow_id=(select min(work_flow_id) from work_flow_details where effective_id in ("+reimbIds.toString()+") and is_approved=0 and " +
									"effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' and member_position not in (select member_position from work_flow_details where " +
									"effective_id in ("+reimbIds.toString()+") and is_approved=1 and effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' )) order by work_flow_id");
//								pst.setInt(1, uF.parseToInt(strId));
//								pst.setInt(2, uF.parseToInt(strId));
//								pst.setInt(3, uF.parseToInt(strId));
								rs = pst.executeQuery();
								while(rs.next()) {
									flag=false;
								}
								rs.close();
								pst.close();
								
								if(flag) {				
									pst = con.prepareStatement("update emp_reimbursement set approval_1=?, approval_1_emp_id=?, approval_1_date=?, " +
										"approval_2=?, approval_2_emp_id=?, approval_2_date=?,approve_reason=?  where reimbursement_id in ("+reimbIds.toString()+")");
									pst.setInt(1, uF.parseToInt(strStatus));
									pst.setInt(2, uF.parseToInt(strSessionEmpId));
									pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(4, uF.parseToInt(strStatus));
									pst.setInt(5, uF.parseToInt(strSessionEmpId));
									pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setString(7, strReason);
//									pst.setInt(8, uF.parseToInt(strId));
									pst.execute();
									pst.close();
								} else if(uF.parseToInt(strStatus) == -1) {				
									pst = con.prepareStatement("update emp_reimbursement set approval_1=?, approval_1_emp_id=?, approval_1_date=?, " +
										"approval_2=?, approval_2_emp_id=?, approval_2_date=?,approve_reason=?  where reimbursement_id in ("+reimbIds.toString()+")");
									pst.setInt(1, uF.parseToInt(strStatus));
									pst.setInt(2, uF.parseToInt(strSessionEmpId));
									pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(4, uF.parseToInt(strStatus));
									pst.setInt(5, uF.parseToInt(strSessionEmpId));
									pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setString(7, strReason);
//									pst.setInt(8, uF.parseToInt(strId));
									pst.execute();
									pst.close();
								}
							}
						}
						//request.setAttribute(MESSAGE, getEmpId() + " updated successfully!");
					}
				} else {
					if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))) {
						StringBuilder sbQuery = new StringBuilder();
						sbQuery.append("update emp_reimbursement set approval_1=?, approval_1_emp_id=?, approval_1_date=?, " +
								"approval_2=?, approval_2_emp_id=?, approval_2_date=?,approve_reason=? where reimbursement_id>0 ");
						if(uF.parseToInt(reimbParentId) > 0) {
							sbQuery.append(" and parent_id="+uF.parseToDouble(reimbParentId));
						} else {
							sbQuery.append(" and reimbursement_id="+uF.parseToDouble(strId));
						}
						pst = con.prepareStatement(sbQuery.toString());
						pst.setInt(1, uF.parseToInt(strStatus));
						pst.setInt(2, uF.parseToInt(strSessionEmpId));
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(4, uF.parseToInt(strStatus));
						pst.setInt(5, uF.parseToInt(strSessionEmpId));
						pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setString(7, strReason);
//						pst.setInt(8, uF.parseToInt(strId));
						pst.execute();
						pst.close();
						flagAdmin = true;
					}
				}
				
				
				if(strMode!=null && (strMode.equalsIgnoreCase("AA") || strMode.equalsIgnoreCase("HRA"))) {
					Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
					if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
//					Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
					Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetailsForPDF(con);
					if(hmCurrency == null) hmCurrency = new HashMap<String, Map<String,String>>();
					
					pst1 = con.prepareStatement("select * from emp_reimbursement where reimbursement_id=?");
					pst1.setInt(1, uF.parseToInt(strId));
					rs1 = pst1.executeQuery();
					if(rs1.next()) {
						String strCurrId = hmEmpCurrency.get(rs1.getString("emp_id"));
						strEmpId = rs1.getString("emp_id");
						Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
						if (hmCurrencyInner == null)hmCurrencyInner = new HashMap<String, String>();
						String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
						
						String strDomain = request.getServerName().split("\\.")[0];
						Notifications nF = new Notifications(N_EMPLOYEE_REIMBURSEMENT_APPROVAL, CF);
						nF.setDomain(strDomain);
						nF.request = request;
						nF.setStrEmpId(rs1.getString("emp_id"));
//						nF.setStrHostAddress(request.getRemoteHost());
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrEmpReimbursementFrom(uF.getDateFormat(rs1.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));
						nF.setStrEmpReimbursementTo(uF.getDateFormat(rs1.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
						nF.setStrEmpReimbursementPurpose(rs1.getString("reimbursement_purpose"));
						nF.setStrEmpReimbursementType(rs1.getString("reimbursement_type"));
						nF.setStrEmpReimbursementAmount(rs1.getString("reimbursement_amount"));
						nF.setStrEmpReimbursementDate(uF.getDateFormat(rs1.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
						nF.setStrEmpReimbursementCurrency(strCurrSymbol);
						
						if(uF.parseToInt(strStatus)==1) {
							nF.setStrApprvedDenied("approved");	
						}else if(uF.parseToInt(strStatus)==-1) {
							nF.setStrApprvedDenied("denied");
						}
						nF.setEmailTemplate(true);
						nF.sendNotifications();
					}
					rs1.close();
					pst1.close();
				}
				
//				System.out.println("flagAdmin ===>> " + flagAdmin + " -- flag ===>> " + flag + " -- strStatus ===>> " + strStatus +" -- strEmpId ===>> " + strEmpId);
				
				if(flagAdmin || flag || uF.parseToInt(strStatus) == -1) {
					String strAmount="";
					if(uF.parseToInt(reimbParentId) > 0) {
						pst1 = con.prepareStatement("select sum(reimbursement_amount) as reimbursement_amount from emp_reimbursement where parent_id = ?");
						pst1.setInt(1, uF.parseToInt(reimbParentId));
						rs1 = pst1.executeQuery();
//						String strEmpId=null;
						if(rs1.next()) {
//							strEmpId=rs1.getString("emp_id");
							strAmount = rs1.getString("reimbursement_amount");
						}
						rs1.close();
						pst1.close();
					} else {
						pst1 = con.prepareStatement("select * from emp_reimbursement where reimbursement_id = ?");
						pst1.setInt(1, uF.parseToInt(strId));
						rs1 = pst1.executeQuery();
						if(rs1.next()) {
//							strEmpId=rs1.getString("emp_id");
							strAmount = rs1.getString("reimbursement_amount");
						}
						rs1.close();
						pst1.close();
					}
					
					Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
					if(hmUserTypeId == null) hmUserTypeId = new HashMap<String, String>();
					
					String strDomain = request.getServerName().split("\\.")[0];
					String strApproveDeny = "approved";
					if(uF.parseToInt(strStatus)== -1) {
						strApproveDeny = "denied";
					}
					String alertData = "<div style=\"float: left;\"> Your Claim ("+strAmount+") has been "+strApproveDeny+" by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
					String alertAction = "MyPay.action?pType=WR&callFrom=MyDashReimbursements";
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(strEmpId);
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
//					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//					userAlerts.setStrDomain(strDomain);
//					userAlerts.setStrEmpId(strEmpId);
//					userAlerts.set_type(REIM_APPROVAL_ALERT);
//					userAlerts.setStatus(INSERT_ALERT);
//					Thread t = new Thread(userAlerts);
//					t.run();
					
					if(uF.parseToInt(strStatus) == 1) {
					List<String> alAccountant = CF.getEmpAccountantList(con, uF, strEmpId, hmUserTypeId);
						if(alAccountant == null) alAccountant = new ArrayList<String>();
						if(alAccountant.size() > 0) {
							int nAccountant = alAccountant.size();
							for(int i = 0; i < nAccountant; i++) {
								String strAccountant = alAccountant.get(i);
								alertData = "<div style=\"float: left;\"> Reimbursement, <b>"+CF.getEmpNameMapByEmpId(con, strEmpId)+"</b> has been approved by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. Please Check. </div>";
								alertAction = "Pay.action?pType=WR&callFrom=NotiApproveReimbursement";
								userAlerts=new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(strAccountant);
								userAlerts.setStrData(alertData);
								userAlerts.setStrAction(alertAction);
								userAlerts.setCurrUserTypeID(hmUserTypeId.get(ACCOUNTANT));
								userAlerts.setStatus(INSERT_WR_ALERT);
								t = new Thread(userAlerts);
								t.run();
							}
						} else {
							List<String> alGlobalHR = CF.getGlobalHRList(con,uF,hmUserTypeId);
							if(alGlobalHR == null) alGlobalHR = new ArrayList<String>();
							int nGlobalHR = alGlobalHR.size();
							for(int i = 0; i < nGlobalHR; i++) {
								String strGlobalHR = alGlobalHR.get(i);
								alertData = "<div style=\"float: left;\"> Reimbursement, <b>"+CF.getEmpNameMapByEmpId(con, strEmpId)+"</b> has been approved by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. Please Check. </div>";
								alertAction = "Pay.action?pType=WR&callFrom=NotiApproveReimbursement";
								userAlerts=new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(strGlobalHR);
								userAlerts.setStrData(alertData);
								userAlerts.setStrAction(alertAction);
								userAlerts.setCurrUserTypeID(hmUserTypeId.get(ADMIN));
								userAlerts.setStatus(INSERT_WR_ALERT);
								t = new Thread(userAlerts);
								t.run();
							}
						}
					}
				}
				getStatusMessage(uF.parseToInt(strStatus));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}
		
//		return nRequisitionId;
	}
	
	
	public void deleteRequest(String strStatus, String strId, String strType) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int nRequisitionId=0;
		
		try{
			
			con = db.makeConnection(con);
			
			if(strType!=null && strType.equalsIgnoreCase("RIM")) {
				pst = con.prepareStatement("delete from emp_reimbursement where reimbursement_id =?");
				pst.setInt(1, uF.parseToInt(strId));
			}else if(strType!=null && strType.equalsIgnoreCase("LER")) {
				pst = con.prepareStatement("delete from emp_leave_entry where leave_id = ?");
				pst.setInt(1, uF.parseToInt(strId));
			}else if(strType!=null && strType.equalsIgnoreCase("PERK")) {
				pst = con.prepareStatement("delete from emp_perks where perks_id = ?");
				pst.setInt(1, uF.parseToInt(strId));
			}
			pst.execute();
			pst.close();
			
			getStatusMessage(uF.parseToInt(strStatus));
			
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
	
	public void getStatusMessage(int nStatus) {
		
		switch(nStatus) {
		
		case -1:
			/* request.setAttribute("STATUS_MSG", "<img title=\"Denied\" src=\""+request.getContextPath()+"/images1/icons/denied.png\" border=\"0\">");*/
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
	
	
	public void getNotificationStatusMessage(int nStatus, String str) {
		
		switch(nStatus) {
		
		case 0:
			request.setAttribute("STATUS_MSG", "<img width=\"20px\" title=\"Pending\" src=\""+request.getContextPath()+"/images1/"+(("E".equalsIgnoreCase(str))?"mail_disbl.png":"mob_disbl.png")+"\" border=\"0\">&nbsp;");
			break;
			
		case 1:
			request.setAttribute("STATUS_MSG", "<img width=\"20px\" title=\"Approved\" src=\""+request.getContextPath()+"/images1/"+(("E".equalsIgnoreCase(str))?"mail_enbl.png":"mob_enbl.png")+"\" border=\"0\">&nbsp;");
			break;
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getPaycycle() {
		return paycycle;
	}
	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}
	
}