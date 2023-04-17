package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ApproveLearningRequest extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = -2009608472005407630L;
	
	HttpSession session;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null;
	
	private CommonFunctions CF;
	
	private String strEmpId;
	private String strlearningId;
	private String mReason;
	private String userType;
	private String currUserType;
	
	String lNomineeId;
	
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
		
		String strStatus = (String) request.getParameter("apStatus");
		lNomineeId = (String) request.getParameter("lNomineeId");
		
		updateLearningNominee(strStatus, lNomineeId);

        return "LRSUCCESS";

	}
	
	private void updateLearningNominee(String strStatus, String lNomineeId) {
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
		
		if (lNomineeId != null && strStatus != null) {
//			System.out.println("strStatus====>" + strStatus);
//			System.out.println("strId====>" + strId);
//			int intStatus = uF.parseToInt(strStatus);
//			System.out.println("intId====>" + intStatus);
			
				try {
					con = db.makeConnection(con);

					if(getCurrUserType() == null){
						setCurrUserType(request.getParameter("currUserType"));
					}
					if (uF.parseToBoolean(CF.getIsWorkFlow())) {
						
						boolean flag = true;
						if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN)) { //!checkEmpList.contains(strSessionEmpId) &&
							
							String query = "update learning_nominee_details set approve_status = ?, approved_by = ?, approve_reason = ?, approve_date=? where nominated_details_id=?";
							pst = con.prepareStatement(query);
							pst.setInt(1, uF.parseToInt(strStatus));
							
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setString(3, getmReason());
							pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
							pst.setInt(5, uF.parseToInt(lNomineeId));
							
							pst.execute();
							System.out.println("Pst 1 ===> "+pst.toString());
							pst.close();
							
							pst = con.prepareStatement("select * from learning_nominee_details lnd left join learning_plan_details lpd " +
									"using(learning_plan_id) where nominated_details_id=? and approve_status=1");
							pst.setInt(1, uF.parseToInt(lNomineeId));
							rs = pst.executeQuery();
							int learningPlanId = 0;
							String requestedBy = null;
							
							StringBuilder learnerIds = new StringBuilder();
							while (rs.next()) {
								learningPlanId = rs.getInt("learning_plan_id");
								requestedBy = rs.getString("requested_by");
								learnerIds.append(rs.getString("learner_ids"));
							}
							rs.close();
			     			pst.close();
			     			
//			     			StringBuilder newLearnerIds = new StringBuilder();
			     			if(learnerIds != null && learnerIds.length() != 0 && !learnerIds.toString().contains(requestedBy)){
			     				learnerIds.append(requestedBy+",");
			     				pst = con.prepareStatement("update learning_plan_details set learner_ids = ? where learning_plan_id=?");
				     			pst.setString(1, learnerIds.toString());
				     			pst.setInt(2, learningPlanId);
				     			pst.execute();
				     			pst.close();
			     			}
							
						}else{
							pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_LEARNING_REQUEST+"' " +
                    		" and is_approved=0 and emp_id=? and user_type_id=? order by work_flow_id");
			                pst.setInt(1, uF.parseToInt(lNomineeId));
			                pst.setInt(2, uF.parseToInt(strSessionEmpId));
		                	pst.setInt(3, uF.parseToInt(getUserType()));
		                	System.out.println("ADGKRA/124--pst===>"+pst);
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
			                
			     			pst = con.prepareStatement("select * from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_LEARNING_REQUEST+"' " +
	                            " and work_flow_id=(select min(work_flow_id) from work_flow_details where effective_id=? " +
	                            " and is_approved=0 and effective_type='"+WORK_FLOW_LEARNING_REQUEST+"' and member_position not in " +
	                            " (select member_position from work_flow_details where effective_id=? and is_approved=1 and effective_type='"+WORK_FLOW_LEARNING_REQUEST+"' )) " +
	                            " order by work_flow_id");
		                    pst.setInt(1, uF.parseToInt(lNomineeId));
		                    pst.setInt(2, uF.parseToInt(lNomineeId));
		                    pst.setInt(3, uF.parseToInt(lNomineeId));
		                    rs = pst.executeQuery();
		                    while (rs.next()) {
		                        flag = false;
		                    }
		                    rs.close();
		        			pst.close();
			     			
		        			if (flag) {
				     			String query = "update learning_nominee_details set approve_status=?, approved_by = ?, approve_reason = ?, approve_date = ?  where nominated_details_id=?";
								pst = con.prepareStatement(query);
								pst.setInt(1, uF.parseToInt(strStatus));
								pst.setInt(2, uF.parseToInt(strSessionEmpId));
								pst.setString(3, getmReason());
								pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
								pst.setInt(5, uF.parseToInt(lNomineeId));
								
								pst.execute();
								System.out.println("Pst 2 ===> "+pst.toString());
								pst.close();
								
								pst = con.prepareStatement("select * from learning_nominee_details lnd left join learning_plan_details lpd " +
								"using(learning_plan_id) where nominated_details_id=? and approve_status=1");
								pst.setInt(1, uF.parseToInt(lNomineeId));
								rs = pst.executeQuery();
								int learningPlanId = 0;
								String requestedBy = null;
								
								StringBuilder learnerIds = new StringBuilder();
								while (rs.next()) {
									learningPlanId = rs.getInt("learning_plan_id");
									requestedBy = rs.getString("requested_by");
									learnerIds.append(rs.getString("learner_ids"));
								}
								rs.close();
				     			pst.close();
				     			
		//		     			StringBuilder newLearnerIds = new StringBuilder();
				     			if(learnerIds != null && learnerIds.length() != 0 && !learnerIds.toString().contains(requestedBy)){
				     				learnerIds.append(requestedBy+",");
				     				pst = con.prepareStatement("update learning_plan_details set learner_ids = ? where learning_plan_id=?");
					     			pst.setString(1, learnerIds.toString());
					     			pst.setInt(2, learningPlanId);
					     			pst.execute();
					     			pst.close();
				     			}
				     			
		        			}else if(uF.parseToInt(strStatus) == -1){
		        				 
		        				String query = "update learning_nominee_details set approve_status=?, approved_by = ?, approve_reason = ?, approve_date = ? where nominated_details_id=?";
								pst = con.prepareStatement(query);
								pst.setInt(1, uF.parseToInt(strStatus));
								pst.setInt(2, uF.parseToInt(strSessionEmpId));
								pst.setString(3, getmReason());
								pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
								pst.setInt(5, uF.parseToInt(lNomineeId));
								
								pst.execute();
								System.out.println("Pst 3 ===> "+pst.toString());
								pst.close();
								
								pst = con.prepareStatement("select * from learning_nominee_details lnd left join learning_plan_details lpd " +
								"using(learning_plan_id) where nominated_details_id=? and approve_status=1");
								pst.setInt(1, uF.parseToInt(lNomineeId));
								rs = pst.executeQuery();
								int learningPlanId = 0;
								String requestedBy = null;
								
								StringBuilder learnerIds = new StringBuilder();
								while (rs.next()) {
									learningPlanId = rs.getInt("learning_plan_id");
									requestedBy = rs.getString("requested_by");
									learnerIds.append(rs.getString("learner_ids"));
								}
								rs.close();
				     			pst.close();
				     			
		//		     			StringBuilder newLearnerIds = new StringBuilder();
				     			if(learnerIds != null && learnerIds.length() != 0 && !learnerIds.toString().contains(requestedBy)){
				     				learnerIds.append(requestedBy+",");
				     				pst = con.prepareStatement("update learning_plan_details set learner_ids = ? where learning_plan_id=?");
					     			pst.setString(1, learnerIds.toString());
					     			pst.setInt(2, learningPlanId);
					     			pst.execute();
					     			pst.close();
				     			}
								
		        			}
						}
					} else {
						if (strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
							
							String query = "update learning_nominee_details set approve_status=?, approved_by = ?, approve_reason = ?, approve_date = ? where nominated_details_id=?";
							pst = con.prepareStatement(query);
							pst.setInt(1, uF.parseToInt(strStatus));
							
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setString(3, getmReason());
							pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
							pst.setInt(5, uF.parseToInt(lNomineeId));
							
							pst.execute();
							System.out.println("Pst 4 ===> "+pst.toString());
							pst.close();
							
							pst = con.prepareStatement("select * from learning_nominee_details lnd left join learning_plan_details lpd " +
							"using(learning_plan_id) where nominated_details_id=? and approve_status=1");
							pst.setInt(1, uF.parseToInt(lNomineeId));
							rs = pst.executeQuery();
							int learningPlanId = 0;
							String requestedBy = null;
							
							StringBuilder learnerIds = new StringBuilder();
							while (rs.next()) {
								learningPlanId = rs.getInt("learning_plan_id");
								requestedBy = rs.getString("requested_by");
								learnerIds.append(rs.getString("learner_ids"));
							}
							rs.close();
			     			pst.close();
			     			
	//		     			StringBuilder newLearnerIds = new StringBuilder();
			     			if(learnerIds != null && learnerIds.length() != 0 && !learnerIds.toString().contains(requestedBy)){
			     				learnerIds.append(requestedBy+",");
			     				pst = con.prepareStatement("update learning_plan_details set learner_ids = ? where learning_plan_id=?");
				     			pst.setString(1, learnerIds.toString());
				     			pst.setInt(2, learningPlanId);
				     			pst.execute();
				     			pst.close();
			     			}
							
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
	
	private HttpServletRequest request;
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

	public String getStrlearningId() {
		return strlearningId;
	}

	public void setStrlearningId(String strlearningId) {
		this.strlearningId = strlearningId;
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
	

}
