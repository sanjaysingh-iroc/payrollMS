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

public class DeleteCGOMAndQuestion extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request; 
	String strUserType = null;
	String strSessionEmpId = null;

	private CommonFunctions CF;
	
	private String id;
	private String queID;
	private String type;
	private String appFreqId;
	private String fromPage;
	UtilityFunctions uF = new UtilityFunctions();
	
	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
			request.setAttribute(PAGE, "/jsp/performance/AppraisalSummary.jsp");
			Connection con = null;
			if(type != null && type.equals("Q")){
				deleteAppraisalQuestion(con);
			}
			if(type != null && type.equals("C")){
				deleteAppraisalCompentancy(con);
			}
			if(type != null && type.equals("G")){
				deleteAppraisalGoal(con);
			}
			if(type != null && type.equals("O")){
				deleteAppraisalObjective(con);
			}
			if(type != null && type.equals("M")){
				deleteAppraisalMeasure(con);
			}
			
			if(getFromPage() != null && getFromPage().equals("MRS")) {
				return VIEW;
			}
		return SUCCESS;
	}
	
	
	private void deleteAppraisalCompentancy(Connection con) {
		// TODO Auto-generated method stub
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			String goalID = getGoalId(con, queID);
			String objID = getObjectiveId(con, goalID);
			String measureID = null;
			if(objID != null && !objID.equals("")){
				measureID = getMeasureId(con, objID);
			}else{
				measureID = getMeasureId(con, queID);
			}
			if (queID != null && !queID.equals("")) {
				pst = con.prepareStatement("delete from appraisal_scorecard_details where scorecard_id in (" + queID + ")");
				pst.executeUpdate();
				pst.close();
//				System.out.println("PST =====> "+pst);
				if(goalID != null && !goalID.equals("")){
					pst = con.prepareStatement("delete from appraisal_goal_details where goal_id in (" + goalID + ")");
					pst.executeUpdate();
					pst.close();
//					System.out.println("PST =====> "+pst);
				}
				if(objID != null && !objID.equals("")){
					pst = con.prepareStatement("delete from appraisal_objective_details where objective_id in (" + objID + ")");
					pst.executeUpdate();
					pst.close();
//					System.out.println("PST =====> "+pst);
				}
				if(measureID != null && !measureID.equals("")){
					pst = con.prepareStatement("delete from appraisal_measure_details where measure_id in (" + measureID + ")");
					pst.executeUpdate();
					pst.close();
//					System.out.println("PST =====> "+pst);
					
					pst = con.prepareStatement("delete from appraisal_question_details where measure_id in (" + measureID + ")");
					pst.executeUpdate();
					pst.close();
//					System.out.println("PST =====> "+pst);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void deleteAppraisalGoal(Connection con) {
		// TODO Auto-generated method stub
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			String objID = getObjectiveId(con, queID);
			String measureID = null;
			if(objID != null && !objID.equals("")){
				measureID = getMeasureId(con, objID);
			}else{
				measureID = getMeasureId(con, queID);
			}
			if (queID != null && !queID.equals("")) {
				pst = con.prepareStatement("delete from appraisal_goal_details where goal_id in (" + queID + ")");
				pst.executeUpdate();
				pst.close();
//				System.out.println("PST =====> "+pst);
				if(objID != null && !objID.equals("")){
					pst = con.prepareStatement("delete from appraisal_objective_details where objective_id in (" + objID + ")");
					pst.executeUpdate();
					pst.close();
//					System.out.println("PST =====> "+pst);
				}
				if(measureID != null && !measureID.equals("")){
					pst = con.prepareStatement("delete from appraisal_measure_details where measure_id in (" + measureID + ")");
					pst.executeUpdate();
					pst.close();
//					System.out.println("PST =====> "+pst);
					
					pst = con.prepareStatement("delete from appraisal_question_details where measure_id in (" + measureID + ")");
					pst.executeUpdate();
					pst.close();
//					System.out.println("PST =====> "+pst);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void deleteAppraisalObjective(Connection con) {
		// TODO Auto-generated method stub
		PreparedStatement pst = null;
		Database db = new Database(); 
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			String measureID = getMeasureId(con, queID);
			if (queID != null && !queID.equals("")) {
				pst = con.prepareStatement("delete from appraisal_objective_details where objective_id in (" + queID + ")");
				pst.executeUpdate();
				pst.close();
//				System.out.println("PST =====> "+pst);
				
				pst = con.prepareStatement("delete from appraisal_measure_details where measure_id in (" + measureID + ")");
				pst.executeUpdate();
				pst.close();
//				System.out.println("PST =====> "+pst);
				
				pst = con.prepareStatement("delete from appraisal_question_details where measure_id in (" + measureID + ")");
				pst.executeUpdate();
				pst.close();
//				System.out.println("PST =====> "+pst);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void deleteAppraisalMeasure(Connection con) {
		// TODO Auto-generated method stub
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			if (queID != null && !queID.equals("")) {
				pst = con.prepareStatement("delete from appraisal_measure_details where measure_id in (" + queID + ")");
				pst.executeUpdate();
				pst.close();
//				System.out.println("PST =====> "+pst);
				
				pst = con.prepareStatement("delete from appraisal_question_details where measure_id in (" + queID + ")");
				pst.executeUpdate();
				pst.close();
//				System.out.println("PST =====> "+pst);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public void deleteAppraisalQuestion(Connection con) {

//		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			if (queID != null && !queID.equals("")) {
				pst = con.prepareStatement("delete from appraisal_question_details where appraisal_question_details_id in (" + queID + ")");
				pst.executeUpdate();
				pst.close();
//				System.out.println("PST =====> "+pst);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
  	}
	
	
	private String getGoalId(Connection con, String scoreCardId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		String goalId = null;
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
	
	
	private String getObjectiveId(Connection con, String goalId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		String objectiveId = null;
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
	
	
	private String getMeasureId(Connection con, String objId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		String measureId = null;

		try {
			if (objId != null && !objId.equals("")) {
				pst = con.prepareStatement("select measure_id from appraisal_measure_details where objective_id in("+ objId + ")");
				rst = pst.executeQuery();
				int c = 0;
				while (rst.next()) {
					if (c == 0) {
						measureId = rst.getString(1);
					} else {
						measureId += "," + rst.getString(1);
					}
					c++;
				}
				rst.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return measureId;
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQueID() {
		return queID;
	}

	public void setQueID(String queID) {
		this.queID = queID;
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


	public String getFromPage() {
		return fromPage;
	}


	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

}
