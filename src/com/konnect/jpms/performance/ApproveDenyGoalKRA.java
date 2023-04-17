package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ApproveDenyGoalKRA extends ActionSupport implements ServletRequestAware, IStatements{
	
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null;
	
	private CommonFunctions CF;

	private String strEmpId;
	private String strGoalId;
	
	private String mReason;
	private String userType;
	private String currUserType;
	private String from; 		//===added parvez date:31-12-2021===
	
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

		String strStatus = (String) request.getParameter("apStatus");
//		strGoalId = (String) request.getParameter("strGoalId");
//		System.out.println("strStatus =====>> " + strStatus);
//		System.out.println("ADG/55--getFromPage() =====>> " + getFromPage());
		updateGoal(strStatus, getStrGoalId());
		

        return "TSUCCESS";
         
	}
	
	private void updateGoal(String strStatus, String strGoalId) {
//		System.out.println("ADGKRA/69--updateGoal");
		Connection con = null;
		PreparedStatement pst = null;
		java.sql.ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		UtilityFunctions uF = new UtilityFunctions();

//		System.out.println("ADGKRA/81--getmReason()====>" + getmReason());
//		System.out.println("ADGKRA/82--getUserType()="+getUserType());
//		System.out.println("ADGKRA/83--strStatus====>" + strStatus);
//		System.out.println("ADGKRA/84--strGoalId====>" + strGoalId);
		
		if (strGoalId != null && strStatus != null) {
				try {
					con = db.makeConnection(con);

					if (uF.parseToBoolean(CF.getIsWorkFlow())) {
						
						boolean flag = true;
						if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN)) { //!checkEmpList.contains(strSessionEmpId) &&
							// Created By Dattatray Date:06-09-21 Note: and approved_by = ? and approve_reason = ?
							String query = "update goal_details set approve_status = ?, approved_by = ?, approve_reason = ? where goal_id=?";
							pst = con.prepareStatement(query);
							pst.setInt(1, uF.parseToInt(strStatus));
							// Start Dattatray Date:06-09-21
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setString(3, getmReason());
							pst.setInt(4, uF.parseToInt(strGoalId));
							// End Dattatray Date:06-09-21
							pst.execute();
//							System.out.println("Pst 1 ===> "+pst.toString());
							pst.close();
							
						}else{
							pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_PERSONAL_GOAL+"' " +
                    		" and is_approved=0 and emp_id=? and user_type_id=? order by work_flow_id");
			                pst.setInt(1, uF.parseToInt(strGoalId));
			                pst.setInt(2, uF.parseToInt(strSessionEmpId));
		                	pst.setInt(3, uF.parseToInt(getUserType()));
//		                	System.out.println("ADGKRA/124--pst===>"+pst);
			                rs = pst.executeQuery();
			                int work_id = 0;
			                while (rs.next()) {
			                	work_id = rs.getInt("work_flow_id");
			                    break;
			                }
			     			rs.close();
			     			pst.close();
//			                System.out.println("ADGKRA/132--work_id="+work_id);
			                
			                pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,emp_id=?,approve_date=?,reason=? WHERE work_flow_id=?");
			                pst.setInt(1, uF.parseToInt(strStatus));
			                pst.setInt(2, uF.parseToInt(strSessionEmpId));
			                pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			                pst.setString(4, getmReason());
			                pst.setInt(5, work_id);
			                pst.execute();
			     			pst.close();
			                
			     			pst = con.prepareStatement("select * from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_PERSONAL_GOAL+"' " +
	                            " and work_flow_id=(select min(work_flow_id) from work_flow_details where effective_id=? " +
	                            " and is_approved=0 and effective_type='"+WORK_FLOW_PERSONAL_GOAL+"' and member_position not in " +
	                            " (select member_position from work_flow_details where effective_id=? and is_approved=1 and effective_type='"+WORK_FLOW_PERSONAL_GOAL+"' )) " +
	                            " order by work_flow_id");
		                    pst.setInt(1, uF.parseToInt(strGoalId));
		                    pst.setInt(2, uF.parseToInt(strGoalId));
		                    pst.setInt(3, uF.parseToInt(strGoalId));
		                    rs = pst.executeQuery();
		                    while (rs.next()) {
		                        flag = false;
		                    }
		                    rs.close();
		        			pst.close();
			     			
		        			if (flag) {
		        				// Created By Dattatray Date:06-09-21 Note: and approved_by = ? and approve_reason = ?
				     			String query = "update goal_details set approve_status=?, approved_by = ?, approve_reason = ?  where goal_id=?";
								pst = con.prepareStatement(query);
								pst.setInt(1, uF.parseToInt(strStatus));
								// Start Dattatray Date:Date:06-09-21
								pst.setInt(2, uF.parseToInt(strSessionEmpId));
								pst.setString(3, getmReason());
								pst.setInt(4, uF.parseToInt(strGoalId));
								// End Dattatray :Date:06-09-21
								pst.execute();
//								System.out.println("Pst 2 ===> "+pst.toString());
								pst.close();
		        			}else if(uF.parseToInt(strStatus) == -1){
		        				// Created By Dattatray Date:06-09-21 Note: and approved_by = ? and approve_reason = ?
		        				String query = "update goal_details set approve_status=?, approved_by = ?, approve_reason = ? where goal_id=?";
								pst = con.prepareStatement(query);
								pst.setInt(1, uF.parseToInt(strStatus));
								// Start Dattatray Date : Date:06-09-21
								pst.setInt(2, uF.parseToInt(strSessionEmpId));
								pst.setString(3, getmReason());
								pst.setInt(4, uF.parseToInt(strGoalId));
								// End Dattatray Date : Date:06-09-21
								pst.execute();
//								System.out.println("Pst 3 ===> "+pst.toString());
								pst.close();
		        			}
						}
					} else {
						if (strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
							// Created By Dattatray Date:06-09-21 Note: and approved_by = ? and approve_reason = ?
							String query = "update goal_details set approve_status=?, approved_by = ?, approve_reason = ? where goal_id=?";
							pst = con.prepareStatement(query);
							pst.setInt(1, uF.parseToInt(strStatus));
							// Start Dattatray Date : Date:06-09-21
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setString(3, getmReason());
							pst.setInt(4, uF.parseToInt(strGoalId));
							// End Dattatray Date : Date:06-09-21
							pst.execute();
//							System.out.println("Pst 4 ===> "+pst.toString());
							pst.close();
						}
					}
//					request.setAttribute(MESSAGE, getStrEmpId() + " updated successfully!");
					
				} catch (Exception e) {
					/*try {
						con.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}*/
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
	
	
	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getStrGoalId() {
		return strGoalId;
	}

	public void setStrGoalId(String strGoalId) {
		this.strGoalId = strGoalId;
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

//===start parvez date: 31-12-2021===	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
//===end parvez date: 31-12-2021===	

}