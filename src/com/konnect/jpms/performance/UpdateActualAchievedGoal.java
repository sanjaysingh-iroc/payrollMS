package com.konnect.jpms.performance;

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

public class UpdateActualAchievedGoal extends ActionSupport implements ServletRequestAware,IStatements{

	HttpSession session;
	
	String strSessionEmpId;
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF;
	
	
	private String empId;
	private String goalid;
	private String goalFreqId;
	private String weightage;
	private String achievedShare;
	private String actualAchieved;
	private String type;
	
	private static final long serialVersionUID = 1L;

	public String execute() {
		
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		UtilityFunctions uF=new UtilityFunctions();
		if(getGoalid() != null && !getGoalid().equals("")) {
			updateGoalAchievedStatus(uF);
		}
		
		return LOAD;
		
	}

	private void updateGoalAchievedStatus(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			String updateMsg = null;
			
			pst=con.prepareStatement("select * from goal_kra_target_finalization where emp_id =? and goal_id=? and goal_freq_id=?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt(getGoalid()));
			pst.setInt(3, uF.parseToInt(getGoalFreqId()));
			rs=pst.executeQuery();
//			System.out.println("pst ===>>>> " + pst);
			boolean flag = false;
			while(rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			
			if(flag) {
				pst = con.prepareStatement("update goal_kra_target_finalization set goal_weightage=?, goal_achieve_share=?, goal_actual_achieved=?, " +
						"updated_by=?, update_date=? where emp_id=? and goal_id=? and goal_freq_id=?");
				pst.setDouble(1, uF.parseToDouble(getWeightage()));
				pst.setDouble(2, uF.parseToDouble(getAchievedShare()));
				pst.setDouble(3, uF.parseToDouble(getActualAchieved()));
				pst.setInt(4,uF.parseToInt(strSessionEmpId));
				pst.setDate(5,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(6, uF.parseToInt(getEmpId()));
				pst.setInt(7, uF.parseToInt(getGoalid()));
				pst.setInt(8, uF.parseToInt(getGoalFreqId()));
				pst.executeUpdate();
				pst.close();
				updateMsg = "Status Updated";
			} else {
				pst = con.prepareStatement("insert into goal_kra_target_finalization(emp_id, goal_id, goal_weightage,goal_achieve_share," +
						"goal_actual_achieved, added_by, entry_date,goal_freq_id) values(?,?,?,?, ?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.setInt(2, uF.parseToInt(getGoalid()));
				pst.setDouble(3, uF.parseToDouble(getWeightage()));
				pst.setDouble(4, uF.parseToDouble(getAchievedShare()));
				pst.setDouble(5, uF.parseToDouble(getActualAchieved()));
				pst.setInt(6,uF.parseToInt(strSessionEmpId));
				pst.setDate(7,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(8, uF.parseToInt(getGoalFreqId()));
				pst.execute();
				pst.close();
				updateMsg = "Status Inserted";
			} 
			
			if(getType() != null && getType().equals("SAVECLOSE")) {
				closeGoal(con, uF);
			}
			
			request.setAttribute("updateMsg", updateMsg);			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
	}
	
	
	public void closeGoal(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("update goal_details set is_close = true, close_reason = ? where goal_id = ?");
			pst.setString(1, "Close via Finalization");
			pst.setInt(2, uF.parseToInt(getGoalid()));
//			System.out.println("pst ===>> " + pst);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("update goal_details set is_close = true, close_reason = ? where goal_parent_id = ? and is_close = false");
			pst.setString(1, "Close via Finalization");
			pst.setInt(2, uF.parseToInt(getGoalid()));
//			System.out.println("pst ===>> " + pst);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("select goal_id from goal_details where goal_parent_id = ?");
			pst.setInt(1, uF.parseToInt(getGoalid()));
			rs = pst.executeQuery();
			StringBuilder sbManagerGoalIds = null;
			while(rs.next()) {
				if(sbManagerGoalIds == null) {
					sbManagerGoalIds = new StringBuilder();
					sbManagerGoalIds.append(rs.getString("goal_id"));
				} else {
					sbManagerGoalIds.append(","+rs.getString("goal_id"));
				}
			}
			rs.close();
			pst.close();
			if(sbManagerGoalIds == null) {
				sbManagerGoalIds = new StringBuilder();
			}
			
			if(sbManagerGoalIds != null && sbManagerGoalIds.length()>0) {
				pst = con.prepareStatement("update goal_details set is_close = true, close_reason = ? where goal_parent_id in ("+sbManagerGoalIds.toString()+") and is_close = false");
				pst.setString(1, "Close via Finalization");
//				System.out.println("pst ===>> " + pst);
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("select goal_id from goal_details where goal_parent_id in ("+sbManagerGoalIds.toString()+")");
				rs = pst.executeQuery();
				StringBuilder sbTeamGoalIds = null;
				while(rs.next()) {
					if(sbTeamGoalIds == null) {
						sbTeamGoalIds = new StringBuilder();
						sbTeamGoalIds.append(rs.getString("goal_id"));
					} else {
						sbTeamGoalIds.append(","+rs.getString("goal_id"));
					}
				}
				rs.close();
				pst.close();
				
				if(sbTeamGoalIds == null) {
					sbTeamGoalIds = new StringBuilder();
				}
				
				if(sbTeamGoalIds != null && sbTeamGoalIds.length()>0) {
					pst = con.prepareStatement("update goal_details set is_close = true, close_reason = ? where goal_parent_id in ("+sbTeamGoalIds.toString()+") and is_close = false");
					pst.setString(1, "Close via Finalization");
//					System.out.println("pst ===>> " + pst);
					pst.executeUpdate();
					pst.close();
				}
			}
			
			String goalName = CF.getGoalNameById(con, uF, getGoalid());
			session.setAttribute(MESSAGE, SUCCESSM + "" + uF.showData(goalName, "") + " Goal has been closed successfully." + END);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getGoalid() {
		return goalid;
	}

	public void setGoalid(String goalid) {
		this.goalid = goalid;
	}

	public String getWeightage() {
		return weightage;
	}

	public void setWeightage(String weightage) {
		this.weightage = weightage;
	}

	public String getAchievedShare() {
		return achievedShare;
	}

	public void setAchievedShare(String achievedShare) {
		this.achievedShare = achievedShare;
	}

	public String getActualAchieved() {
		return actualAchieved;
	}

	public void setActualAchieved(String actualAchieved) {
		this.actualAchieved = actualAchieved;
	}

	public String getGoalFreqId() {
		return goalFreqId;
	}

	public void setGoalFreqId(String goalFreqId) {
		this.goalFreqId = goalFreqId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
