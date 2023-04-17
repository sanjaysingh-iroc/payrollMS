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

public class DeleteAppraisalLevelAndSystem extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request; 
	String strUserType = null;
	String strSessionEmpId = null;

	private CommonFunctions CF;
	
	private String id;
	private String levelID;
	private String lvlID;
	private String type;
	private String from;
	private String appFreqId;
	UtilityFunctions uF = new UtilityFunctions();
	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
			request.setAttribute(PAGE, "/jsp/performance/AppraisalSummary.jsp");
			
			if(type != null && type.equals("Level")){
				deleteAppraisalLevel();
			}
			if(type != null && type.equals("System")){
				deleteAppraisalLevelSystem();
			}
			
			
			if(getFrom()!= null && getFrom().equals("SR")) {
				return VIEW;
			}
		return SUCCESS;
	}
	
	public void deleteAppraisalLevelSystem() {

		//String appLevelId = getAppLevelId(levelID);
		
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			String scoreCardId = getScoreCardId(con, lvlID);
			String goalId = getGoalId(con, scoreCardId);
			String objectiveId = getObjectiveId(con, goalId);
			if (objectiveId != null && !objectiveId.equals("")) {
				pst = con.prepareStatement("delete from appraisal_measure_details where objective_id in (" + objectiveId + ")");
				pst.executeUpdate();
				pst.close();
			}
			
			if (goalId != null && !goalId.equals("")) {
				pst = con.prepareStatement("delete from appraisal_measure_details where goal_id in (" + goalId + ")");
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_objective_details where goal_id in (" + goalId + ")");
				pst.executeUpdate();
				pst.close();
			}
			
			if (scoreCardId != null && !scoreCardId.equals("")) {
				pst = con.prepareStatement("delete from appraisal_measure_details where scorecard_id in (" + scoreCardId + ")");
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_goal_details where scorecard_id in (" + scoreCardId + ")");
				pst.executeUpdate();
				pst.close();
			}
			
			if (lvlID != null && !lvlID.equals("")) {
				pst = con.prepareStatement("delete from appraisal_scorecard_details where level_id in (" + lvlID + ")");
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_question_details where appraisal_level_id in (" + lvlID + ")");
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_other_question_type_details where level_id in (" + lvlID + ")");
				pst.executeUpdate();
				pst.close();
			}
			
			pst = con.prepareStatement("delete from appraisal_level_details where appraisal_level_id = ?");
			pst.setInt(1, uF.parseToInt(lvlID));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	public void deleteAppraisalLevel() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			String appLevelId = getAppLevelId(con, levelID);
			String scoreCardId = getScoreCardId(con,appLevelId);
			String goalId = getGoalId(con,scoreCardId);
			String objectiveId = getObjectiveId(con,goalId);
			
			if (objectiveId != null && !objectiveId.equals("")) {
				pst = con.prepareStatement("delete from appraisal_measure_details where objective_id in (" + objectiveId + ")");
				pst.executeUpdate();
				pst.close();
			}
			
			if (goalId != null && !goalId.equals("")) {
				pst = con.prepareStatement("delete from appraisal_measure_details where goal_id in (" + goalId + ")");
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_objective_details where goal_id in (" + goalId + ")");
				pst.executeUpdate();
				pst.close();
			}
			
			if (scoreCardId != null && !scoreCardId.equals("")) {
				pst = con.prepareStatement("delete from appraisal_measure_details where scorecard_id in (" + scoreCardId + ")");
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_goal_details where scorecard_id in (" + scoreCardId + ")");
				pst.executeUpdate();
				pst.close();
			}
			
			if (appLevelId != null && !appLevelId.equals("")) {
				pst = con.prepareStatement("delete from appraisal_scorecard_details where level_id in (" + appLevelId + ")");
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_question_details where appraisal_level_id in (" + appLevelId + ")");
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from appraisal_other_question_type_details where level_id in (" + appLevelId + ")");
				pst.executeUpdate();
				pst.close();
			}
			
			pst = con.prepareStatement("delete from appraisal_level_details where main_level_id = ?");
			pst.setInt(1, uF.parseToInt(levelID));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("delete from appraisal_main_level_details where main_level_id = ?");
			pst.setInt(1, uF.parseToInt(levelID));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	private String getObjectiveId(Connection con, String goalId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		String objectiveId = "";

		try {

			if (goalId != null && !goalId.equals("")) {
				pst = con.prepareStatement("select objective_id from appraisal_objective_details where goal_id in("+ goalId + ")");
				rst = pst.executeQuery();
				int c = 0;
				while (rst.next()) {
					if (c == 0) {
						objectiveId = rst.getString(1);
					} else {
						objectiveId += "," + rst.getString(1);
					}
					c++;
				}
				rst.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objectiveId;
	}
	

	private String getGoalId(Connection con, String scoreCardId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		String goalId = "";

		try {

			if (scoreCardId != null && !scoreCardId.equals("")) {
				pst = con.prepareStatement("select goal_id from appraisal_goal_details where scorecard_id in(" + scoreCardId + ")");
				rst = pst.executeQuery();
				int c = 0;
				while (rst.next()) {
					if (c == 0) {
						goalId = rst.getString(1);
					} else {
						goalId += "," + rst.getString(1);
					}
					c++;
				}
				rst.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return goalId;
	}
	
	
	private String getScoreCardId(Connection con, String appLevelId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		String scoreCardId = "";

		try {

			if (appLevelId != null && !appLevelId.equals("")) {
				pst = con.prepareStatement("select scorecard_id from appraisal_scorecard_details where level_id in(" + appLevelId + ")");
				rst = pst.executeQuery();
				int c = 0;
				while (rst.next()) {
					if (c == 0) {
						scoreCardId = rst.getString(1);
					} else {
						scoreCardId += "," + rst.getString(1);
					}
					c++;
				}
				rst.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return scoreCardId;
	}
	
	
	private String getAppLevelId(Connection con, String mainLevelId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		String appLevelId = "";

		try {

			if (mainLevelId != null && !mainLevelId.equals("")) {
				pst = con.prepareStatement("select appraisal_level_id from appraisal_level_details where main_level_id in(" + mainLevelId + ")");
				rst = pst.executeQuery();
				int c = 0;
				while (rst.next()) {
					if (c == 0) {
						appLevelId = rst.getString(1);
					} else {
						appLevelId += "," + rst.getString(1);
					}
					c++;
				}
				rst.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return appLevelId;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLevelID() {
		return levelID;
	}

	public void setLevelID(String levelID) {
		this.levelID = levelID;
	}

	public String getLvlID() {
		return lvlID;
	}

	public void setLvlID(String lvlID) {
		this.lvlID = lvlID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	

}
