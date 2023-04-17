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

/**
 * @author workrig
 *
 */
public class CloseGoalTargetKRA extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strSessionEmpId = null;

	private CommonFunctions CF;
	private String goalId;
	private String fromPage;
	private String operation;
	
	private String closeReason;
	private String kratype;
	private String empId;
	private String proPage;
	private String minLimit;
	private String dataType;
	private String currUserType;
	private String type;
	private String f_org;
	
	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
//		System.out.println("getType() ===>> " + getType());
		UtilityFunctions uF = new UtilityFunctions();
		if(getType() != null && getType().equals("open")) {
			if(getOperation() != null && getOperation().equals("update")) {
				if(getKratype() != null && getKratype().equals("EMPKRA")) {
					openEmpKRA(uF);
				} else {
					openGoal(uF);
				}
				if(getFromPage() != null && getFromPage().equals("MyGoals")) {
					return "MSUCCESS";
				} else if(getFromPage() != null && getFromPage().equals("GoalTarget")) {
					return "GTSUCCESS";
				} else if(getFromPage() != null && getFromPage().equals("GoalKRA")) {
					return "GKSUCCESS";
				} else {
					return SUCCESS;
				}
			} else {
				return LOAD;
			}
		} else {
			if(getOperation() != null && getOperation().equals("update")) {
				if(getKratype() != null && getKratype().equals("EMPKRA")) {
					closeEmpKRA(uF);
				} else {
					closeGoal(uF);
				}
				if(getFromPage() != null && getFromPage().equals("MyGoals")) {
					return "MSUCCESS";
				} else if(getFromPage() != null && getFromPage().equals("GoalTarget")) {
					return "GTSUCCESS";
				} else if(getFromPage() != null && getFromPage().equals("GoalKRA")) {
					return "GKSUCCESS";
				} else {
					return SUCCESS;
				}
			} else {
				if(getKratype() != null && getKratype().equals("EMPKRA")) {
					getCloseEmpKRAReason(uF);
				} else {
					getCloseGoalTargetKRAReason(uF);
				}
				return LOAD;
			}
		}
		
	}
	
	
	private void openGoal(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);    
			
			pst = con.prepareStatement("update goal_details set is_close = false, open_reason=?, opened_by=? where goal_id=?");
			pst.setString(1, getCloseReason());
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setInt(3, uF.parseToInt(getGoalId()));
//			System.out.println("pst ===>> " + pst);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("update goal_details set is_close = false, open_reason=?, opened_by=? where goal_parent_id=? and is_close = true");
			pst.setString(1, getCloseReason());
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setInt(3, uF.parseToInt(getGoalId()));
//			System.out.println("pst ===>> " + pst);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("select goal_id from goal_details where goal_parent_id=?");
			pst.setInt(1, uF.parseToInt(getGoalId()));
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
				pst = con.prepareStatement("update goal_details set is_close = false, open_reason=?, opened_by=? where goal_parent_id in ("+sbManagerGoalIds.toString()+") and is_close = true");
				pst.setString(1, getCloseReason());
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
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
					pst = con.prepareStatement("update goal_details set is_close = false, open_reason=?, opened_by=? where goal_parent_id in ("+sbTeamGoalIds.toString()+") and is_close = true");
					pst.setString(1, getCloseReason());
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
//					System.out.println("pst ===>> " + pst);
					pst.executeUpdate();
					pst.close();
				}
			}
			String goalName = CF.getGoalNameById(con, uF, getGoalId());
			session.setAttribute(MESSAGE, SUCCESSM + "" + uF.showData(goalName, "") + " Goal has been opened successfully." + END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void openEmpKRA(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);    
			
			pst = con.prepareStatement("update goal_kras set is_close = false, open_reason=?, opened_by=? where goal_kra_id=?");
			pst.setString(1, getCloseReason());
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setInt(3, uF.parseToInt(getGoalId()));
//			System.out.println("pst ===>> " + pst); 
			pst.executeUpdate();
			pst.close();
//			String goalName = CF.getGoalNameById(con, uF, getGoalId());
			
			pst = con.prepareStatement("select kra_description goal_kras where goal_kra_id = ?");
			pst.setInt(1, uF.parseToInt(getGoalId()));
//			System.out.println("pst ===>> " + pst); 
			rs = pst.executeQuery();
			String kraName = null;
			while (rs.next()) {
				kraName = rs.getString("kra_description");
			}
			rs.close();
			pst.close();
			session.setAttribute(MESSAGE, SUCCESSM + "" + uF.showData(kraName, "") + " KRA has been opened successfully." + END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getCloseEmpKRAReason(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select close_reason from goal_kras where goal_kra_id = ? and is_close = true");
			pst.setInt(1, uF.parseToInt(getGoalId()));
			rst = pst.executeQuery();
			while(rst.next()) {
				setCloseReason(uF.showData(rst.getString("close_reason"), "-"));
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void closeEmpKRA(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);    
			
			pst = con.prepareStatement("update goal_kras set is_close = true, close_reason = ? where goal_kra_id = ?");
			pst.setString(1, getCloseReason());
			pst.setInt(2, uF.parseToInt(getGoalId()));
//			System.out.println("pst ===>> " + pst); 
			pst.executeUpdate();
			pst.close();
//			String goalName = CF.getGoalNameById(con, uF, getGoalId());
			
			pst = con.prepareStatement("select kra_description goal_kras where goal_kra_id = ?");
			pst.setInt(1, uF.parseToInt(getGoalId()));
//			System.out.println("pst ===>> " + pst); 
			rs = pst.executeQuery();
			String kraName = null;
			while (rs.next()) {
				kraName = rs.getString("kra_description");
			}
			rs.close();
			pst.close();
			session.setAttribute(MESSAGE, SUCCESSM + "" + uF.showData(kraName, "") + " KRA has been closed successfully." + END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getCloseGoalTargetKRAReason(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select close_reason from goal_details where goal_id = ? and is_close = true");
			pst.setInt(1, uF.parseToInt(getGoalId()));
			rst = pst.executeQuery();
			while(rst.next()) {
				setCloseReason(uF.showData(rst.getString("close_reason"), "-"));
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void closeGoal(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);    
			
			pst = con.prepareStatement("update goal_details set is_close = true, close_reason = ? where goal_id = ?");
			pst.setString(1, getCloseReason());
			pst.setInt(2, uF.parseToInt(getGoalId()));
//			System.out.println("pst ===>> " + pst);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("update goal_details set is_close = true, close_reason = ? where goal_parent_id = ? and is_close = false");
			pst.setString(1, getCloseReason());
			pst.setInt(2, uF.parseToInt(getGoalId()));
//			System.out.println("pst ===>> " + pst);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("select goal_id from goal_details where goal_parent_id = ?");
			pst.setInt(1, uF.parseToInt(getGoalId()));
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
				pst.setString(1, getCloseReason());
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
					pst.setString(1, getCloseReason());
//					System.out.println("pst ===>> " + pst);
					pst.executeUpdate();
					pst.close();
				}
			}
			String goalName = CF.getGoalNameById(con, uF, getGoalId());
			session.setAttribute(MESSAGE, SUCCESSM + "" + uF.showData(goalName, "") + " Goal has been closed successfully." + END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String getGoalId() {
		return goalId;
	}

	public void setGoalId(String goalId) {
		this.goalId = goalId;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getCloseReason() {
		return closeReason;
	}

	public void setCloseReason(String closeReason) {
		this.closeReason = closeReason;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getKratype() {
		return kratype;
	}

	public void setKratype(String kratype) {
		this.kratype = kratype;
	}

	public String getProPage() {
		return proPage;
	}

	public void setProPage(String proPage) {
		this.proPage = proPage;
	}

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}


	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}
    
}
