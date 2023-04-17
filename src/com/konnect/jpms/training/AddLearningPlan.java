package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.performance.FillAttribute;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddLearningPlan extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	
	private String operation;

	private String ID;

	CommonFunctions CF = null;

	private String del;
	private String quest_id;
	private String planId;
	private String stepSubmit;
	private String savePublish;
	private String stepSave;
	private String weekdayValue;
	private String dayValue;
    
	private  String orgids;
	private String wLocids;
	private String deptartids;
	private String levelids;
	private String desigids;
	
	private List<String> attributeID = new ArrayList<String>();
	private List<String> skillsID = new ArrayList<String>();
	private List<String> orgID = new ArrayList<String>();
	private List<String> locID = new ArrayList<String>();
	private List<String> levelID = new ArrayList<String>();
	private List<String> desigID = new ArrayList<String>();
	private List<String> gradeID = new ArrayList<String>();
	private List<String> empID = new ArrayList<String>();
	
	private List<List<String>> alSessionData=new ArrayList<List<String>>();
	
	private String f_org;
	private List<FillCertificate> certificateList;
	private String strGapEmpId;
	
	private String strGapId;
	
	public String execute() {

		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		
		String strOperation = request.getParameter("operation");
		String strID = request.getParameter("ID");

		attributeList = new FillAttribute(request).fillAttribute();
		skillslist = new FillSkills(request).fillSkillsWithId();

		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		certificateList = new FillCertificate(request).fillCertificateList();
		certificateList.add(new FillCertificate("0","Create New Certificate"));
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		if(getPlanId() == null) {
			setPlanId(strID);
		}
		
		/*System.out.println("strOperation==>"+strOperation);
		System.out.println("getSavePublish==>"+getSavePublish()+"==>getStepSubmit==>"+getStepSubmit()+"==>getStepSave==>"+getStepSave());*/
		if (strOperation != null && strOperation.equalsIgnoreCase("A")) {
			insertData(uF);
		} else if (strOperation != null && strOperation.equalsIgnoreCase("E")) {
			updateData(getPlanId(), uF);
		} else if (strOperation != null && strOperation.equalsIgnoreCase("D")) {
			return deletePlan(getPlanId(), uF);
		}

		if (step == null || uF.parseToInt(getStep()) == 0) {
			setStep("1");
			request.setAttribute("selectLearnerIDs", ","+strGapEmpId+",");
		} else if (uF.parseToInt(getStep()) == 1) {
			setStep("2");
		} else if (uF.parseToInt(getStep()) == 2) {
			if (getSavePublish() != null && getSavePublish().equals("Save And Publish")) {
				return SUCCESS;
			}
			setStep("3");
		} else if (uF.parseToInt(getStep()) == 3) {
			if ((getStepSubmit() != null && getStepSubmit().equals("Submit And Proceed")) || (getSavePublish() != null && getSavePublish().equals("Save And Publish"))) {
				return SUCCESS;
			}
		}   
		
		if (getStepSave() != null && getStepSave().equals("Save And Exit")) {
			return SUCCESS;
		}
		
		getAnsType();
		getQuestionList(uF);
		getTrainingList();
		getCoursesList();
		getAssessmentsList();
		//===start parvez date: 08-10-2021===
		getVideoList();
		//===end parvez date: 08-10-2021=== 
		return LOAD;
	}
	
	
	private void getAssessmentsList() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			List<List<String>> assessmentList = new ArrayList<List<String>>();
			
			//===start parvez date: 16-10-2021===
			pst = con.prepareStatement("select * from assessment_details where assessment_details_id not in(select learning_plan_stage_name_id from learning_plan_stage_details where learning_type = 'Assessment')");
//			pst = con.prepareStatement("select * from assessment_details");
			//===end parvez date: 16-10-2021===
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("assessment_details_id"));
				innerList.add(rs.getString("assessment_name"));
				assessmentList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("assessmentList", assessmentList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void getTrainingList() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			List<List<String>> trainingList = new ArrayList<List<String>>();
			pst = con.prepareStatement("select tp.* from training_plan tp,training_schedule ts where tp.plan_id not in(select learning_plan_stage_name_id from learning_plan_stage_details where learning_type = 'Training') and ts.plan_id = tp.plan_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("plan_id"));
				innerList.add(rs.getString("training_title"));
				trainingList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("trainingList", trainingList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
		
	public void getCoursesList() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			List<List<String>> coursesList = new ArrayList<List<String>>();
			
			//===start parvez date: 16-10-2021===
			pst = con.prepareStatement("select * from course_details tp where course_id not in(select learning_plan_stage_name_id from learning_plan_stage_details where learning_type = 'Course')");
//			pst = con.prepareStatement("select * from course_details ");
			//===end parvez date:16-10-2021===
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("course_id"));
				innerList.add(rs.getString("course_name"));
				coursesList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("coursesList", coursesList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
		
	public void getQuestionList(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			List<List<String>> feedbackQueList = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from learning_plan_question_bank where learning_plan_id = ? order by learning_plan_question_bank_id ");
			pst.setInt(1, uF.parseToInt(getPlanId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("learning_plan_question_bank_id"));
				innerList.add(rs.getString("learning_plan_question_text"));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("learning_plan_id"));
				innerList.add(rs.getString("answer_type"));
				feedbackQueList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("feedbackQueList", feedbackQueList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
		
	private void getAnsType() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpLocation = CF.getEmpWlocationMap(con);
			Map<String, String> hmWLocation = CF.getWLocationMap(con, null, null);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			request.setAttribute("hmEmpLocation", hmEmpLocation);
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			StringBuilder sb = new StringBuilder("");
			pst = con.prepareStatement("select * from training_answer_type");
			rs = pst.executeQuery();
			while (rs.next()) {
				if (uF.parseToInt(rs.getString("answer_type")) == 9) {
					sb.append("<option value=\"" + rs.getString("answer_type") + "\" selected>" + rs.getString("answer_type_name") + "</option>");
				} else {
					sb.append("<option value=\"" + rs.getString("answer_type") + "\">" + rs.getString("answer_type_name") + "</option>");
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("anstype", sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private String deletePlan(String planId, UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		try {

			con = db.makeConnection(con);

			String lPlanName = CF.getLearningPlanNameById(con, uF, planId);
			pst = con.prepareStatement("delete from learning_plan_details where learning_plan_id = ?");
			pst.setInt(1, uF.parseToInt(planId));
			int x = pst.executeUpdate();
			pst.close();

			if( x > 0){
				pst = con.prepareStatement("delete from learning_plan_stage_details where learning_plan_id=? ");
				pst.setInt(1, uF.parseToInt(planId));
				pst.execute();
				pst.close();
	
				pst = con.prepareStatement("delete  from learning_plan_question_bank where learning_plan_id=?");
				pst.setInt(1, uF.parseToInt(planId));
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("delete  from learning_plan_finalize_details where learning_plan_id=?");
				pst.setInt(1, uF.parseToInt(planId));
				pst.execute();
				pst.close();
			}

			session.setAttribute(MESSAGE, SUCCESSM+""+lPlanName+" learning plan has been deleted successfully."+END);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	private void updateData(String strID, UtilityFunctions uF) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			if (getStepSubmit() != null || getStepSave() != null || getSavePublish() != null){
				insertUpdatedStepData(con, strID, uF);
			}
			updateStepData(con, strID, uF);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
		
		
	}

	private void insertUpdatedStepData(Connection con, String strID, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst = null;
	
		try {
			if (uF.parseToInt(step) == 1) {

				String orgids = request.getParameter("orgids");
				String wLocids = request.getParameter("wLocids");
				String deptartids = request.getParameter("deptartids");
				String levelids = request.getParameter("levelids");
				String desigids = request.getParameter("desigids");
				
								
				pst = con.prepareStatement("update learning_plan_details set learning_plan_name=?, learning_plan_objective=?, group_or_condition=?, " +
						" attribute_id=?, learner_ids=?, org_id=?, location_id=?, depart_id=?, level_id=?, desig_id=?, grade_id=?, updated_by=?, update_date=?, " +
						"certificate_id=?, skills=? where learning_plan_id = ?");
				pst.setString(1, getLearningTitle());
				pst.setString(2, getLearningObjective());
				pst.setInt(3, uF.parseToInt(getAlignedwith()));  
				pst.setString(4, getAppendData(getStrAttribute()));
				if(getAlignedwith() !=null && (getAlignedwith().equals("1") || getAlignedwith().equals("4") || getAlignedwith().equals("5"))) {
					pst.setString(5, getEmpselected());
					pst.setString(6, orgids != null ? orgids : ""); //getAppendData(getF_organization()));
					pst.setString(7, wLocids != null ? wLocids : "");
					pst.setString(8, deptartids != null ? deptartids : "");
					pst.setString(9, levelids != null ? levelids : "");//getAppendData(getStrlearnerLevel()));
					pst.setString(10, desigids != null ? desigids : "");//getAppendData(getStrlearnerDesignation()));
					pst.setString(11, getAppendData(getStrlearnerGrade()));
				} else {
					pst.setString(5, getEmpselected());
					pst.setString(6, ""); //getAppendData(getF_organization()));
					pst.setString(7, "");
					pst.setString(8, "");
					pst.setString(9, "");//getAppendData(getStrlearnerLevel()));
					pst.setString(10, "");//getAppendData(getStrlearnerDesignation()));
					pst.setString(11, getAppendData(getStrlearnerGrade()));
				}
				pst.setInt(12, uF.parseToInt(strSessionEmpId));
				pst.setDate(13, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(14, uF.parseToInt(getStrCertificateId()));
				pst.setString(15, getAppendData(getSkills()));
				pst.setInt(16, uF.parseToInt(strID));
				pst.executeUpdate();
				pst.close();
			
				setPlanId(strID);

			} else if (uF.parseToInt(step) == 2) {
				String existstageIDs = request.getParameter("existstageIDs");
					
				String[] lexiststageid = request.getParameterValues("lexiststageid");	
				String[] lstagetypeid = request.getParameterValues("lstagetypeid");
				String[] lstagetype = request.getParameterValues("lstagetype");
				String[] lstagename = request.getParameterValues("lstagename");
				String[] startdate = request.getParameterValues("startdate");
				String[] enddate = request.getParameterValues("enddate");
				String[] starttime = request.getParameterValues("starttime");
				String[] endtime = request.getParameterValues("endtime");
				
				List<String> existstageIdList = new ArrayList<String>();
				if(existstageIDs != null && existstageIDs.length() > 1){
					existstageIdList = Arrays.asList(existstageIDs.substring(1, existstageIDs.length()-1).split(","));
				}
				
				List<String> stageTypeIdList = new ArrayList<String>();
				List<String> stageTypeList = new ArrayList<String>();
				List<String> remainExistStageIDList = new ArrayList<String>();
				for (int i = 0; existstageIdList != null && !existstageIdList.isEmpty() && i < existstageIdList.size(); i++) {
					int cnt=0;
					for (int j = 0; lexiststageid != null && j < lexiststageid.length; j++) {
						if(existstageIdList.get(i).contains(lexiststageid[j])){
							if(lstagetypeid != null && lstagetypeid[i] != null  && lstagetypeid.length > 0) {
								stageTypeIdList.add(lstagetypeid[i]); // update data with this id
								stageTypeList.add(lstagetype[i]); // update data with this type
								cnt++;
							}
						}
					}
					if(cnt == 0){
						remainExistStageIDList.add(existstageIdList.get(i)); // delete this id from table
					}
				}
				
				for (int i = 0; stageTypeIdList != null && i < stageTypeIdList.size(); i++) {
					String[] weekdays = request.getParameterValues("weekdays"+lstagetype[i]+"_"+lstagetypeid[i]);
					String weekday = "";
					for (int j = 0; weekdays != null && j < weekdays.length; j++) {
						if(j == 0)
						weekday += ","+weekdays[j]+",";
						else
							weekday += weekdays[j]+",";
					}
					pst = con.prepareStatement("update learning_plan_stage_details set learning_plan_stage_name=?,from_date=?,to_date=?,"
							+ "from_time=?,to_time=?,updated_by=?,updated_date=?,weekdays=? where learning_plan_stage_name_id=? " +
									"and learning_type = ? and learning_plan_id = ?");
					pst.setString(1, lstagename[i]);
					pst.setDate(2, uF.getDateFormat(startdate[i], DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(enddate[i], DATE_FORMAT));
					pst.setTime(4, uF.getTimeFormat(starttime[i], DBTIME));
					pst.setTime(5, uF.getTimeFormat(endtime[i], DBTIME));
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(8, weekday);
					pst.setInt(9, uF.parseToInt(stageTypeIdList.get(i)));
					pst.setString(10, stageTypeList.get(i));
					pst.setInt(11, uF.parseToInt(getPlanId()));
					pst.executeUpdate();
					pst.close();
				}
				
				for (int i = 0; lstagetypeid != null && i < lstagetypeid.length; i++) {
					
					if((!stageTypeIdList.contains(lstagetypeid[i]) && !stageTypeList.contains(lstagetype[i])) || (stageTypeIdList.contains(lstagetypeid[i]) && !stageTypeList.contains(lstagetype[i])) || (!stageTypeIdList.contains(lstagetypeid[i]) && stageTypeList.contains(lstagetype[i]))){
						String[] weekdays = request.getParameterValues("weekdays"+lstagetype[i]+"_"+lstagetypeid[i]);
						String weekday = "";
						for (int j = 0; weekdays != null && j < weekdays.length; j++) {
							if(j == 0)
							weekday += ","+weekdays[j]+",";
							else
								weekday += weekdays[j]+",";
						}
						pst = con.prepareStatement("insert into learning_plan_stage_details(learning_plan_stage_name,from_date,to_date,"
										+ "from_time,to_time,learning_type,learning_plan_id,added_by,entry_date,weekdays,learning_plan_stage_name_id)" 
										+ "values(?,?,?,?, ?,?,?,?, ?,?,?)");
						pst.setString(1, lstagename[i]);
						pst.setDate(2, uF.getDateFormat(startdate[i], DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(enddate[i], DATE_FORMAT));
						pst.setTime(4, uF.getTimeFormat(starttime[i], DBTIME));
						pst.setTime(5, uF.getTimeFormat(endtime[i], DBTIME));
						pst.setString(6, lstagetype[i]);
						pst.setInt(7, uF.parseToInt(getPlanId()));
						pst.setInt(8, uF.parseToInt(strSessionEmpId));
						pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setString(10, weekday);
						pst.setInt(11, uF.parseToInt(lstagetypeid[i]));
						pst.execute();
						pst.close();
					}
				}
				
				for (int i = 0; remainExistStageIDList != null && !remainExistStageIDList.isEmpty() && i < remainExistStageIDList.size(); i++) {
					pst = con.prepareStatement("delete from learning_plan_stage_details where learning_plan_stage_id = ?");
					pst.setInt(1, uF.parseToInt(remainExistStageIDList.get(i)));
					pst.executeUpdate();
					pst.close();
				}
				
	
				if(getSavePublish() != null && getSavePublish().equals("Save And Publish")) {
					String query ="update learning_plan_details set is_publish = true where learning_plan_id = ?";
					pst = con.prepareStatement(query);
					pst.setInt(1, uF.parseToInt(getPlanId()));
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("select learning_plan_name,learner_ids from learning_plan_details where learning_plan_id = ?");
					pst.setInt(1, uF.parseToInt(getPlanId()));
					rst = pst.executeQuery();
					String learnersId = "";
					String learningPlanName = "";
					while (rst.next()) {
						learnersId = rst.getString("learner_ids");
						learningPlanName = rst.getString("learning_plan_name");
					}
					rst.close();
					pst.close();
					
					List<String> selectEmpList = Arrays.asList(learnersId.split(","));
					Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
					
					for(int i=0; selectEmpList!= null && !selectEmpList.isEmpty() && i<selectEmpList.size(); i++) {
						if(!selectEmpList.get(i).equals("") && uF.parseToInt(selectEmpList.get(i)) > 0) {
							String strDomain = request.getServerName().split("\\.")[0];
							String alertData = "<div style=\"float: left;\"> A new Learning ("+learningPlanName+") has been aligned with you by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
							String alertAction = "MyHR.action?callFrom=LPDash&pType=WR";
							
							UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(selectEmpList.get(i));
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
						}
					}
					sendMail(con, getPlanId());
				}

			} else if (uF.parseToInt(step) == 3) {

				insertStep3Data(con, uF);
			}

			String lPlanName = CF.getLearningPlanNameById(con, uF, getPlanId());
			session.setAttribute(MESSAGE, SUCCESSM+""+lPlanName+" learning plan has been updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null){
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}
	}

	private void updateStepData(Connection con, String strID, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rst = null;
//		System.out.println("step ===> "+step);
		String step1 = step;
		
		try {
			if (uF.parseToInt(step1) == 0) {
				Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
				String selectLearnerIDs=null;
				pst = con.prepareStatement("select * from learning_plan_details where learning_plan_id = ?");
				pst.setInt(1, uF.parseToInt(strID));
				rst = pst.executeQuery();
				while (rst.next()) {
					
					setLearningTitle(rst.getString("learning_plan_name"));
					setLearningObjective(rst.getString("learning_plan_objective"));
					setAlignedwith(rst.getString("group_or_condition"));
					setCertificateId(rst.getString("certificate_id"));
//					System.out.println("getAlignedwith() ===> " + getAlignedwith());
					setStrCertificateId(rst.getString("certificate_id"));
					setBoolPublished(uF.parseToBoolean(rst.getString("is_publish")));
					
					if (rst.getString("attribute_id") != null && !rst.getString("attribute_id").equals("")){
						List<String> attributeValue1 = new ArrayList<String>();
						attributeValue1 = Arrays.asList(rst.getString("attribute_id").split(","));
						for (int k = 0; k < attributeValue1.size(); k++) {
							if (attributeValue1.get(k) != null && !attributeValue1.get(k).equals("")) {
								attributeID.add(attributeValue1.get(k).trim());
							}
						}
					}
					
					if(rst.getString("org_id") != null && !rst.getString("org_id").equals("")){
						List<String> orgValue = new ArrayList<String>();
						orgValue = Arrays.asList(rst.getString("org_id").split(","));
						for (int k = 0; k < orgValue.size(); k++) {
							if (orgValue.get(k) != null && !orgValue.get(k).equals("")) {
								orgID.add(orgValue.get(k).trim());
							}
						}
					}
					if (rst.getString("location_id") != null && !rst.getString("location_id").equals("")) {
						List<String> locationValue = new ArrayList<String>();
						locationValue = Arrays.asList(rst.getString("location_id").split(","));
						for (int k = 0; k < locationValue.size(); k++) {
							if (locationValue.get(k) != null && !locationValue.get(k).equals("")) {
								locID.add(locationValue.get(k).trim());
							}
						}
					}
					if (rst.getString("level_id") != null && !rst.getString("level_id").equals("")) {
						List<String> levelValue = new ArrayList<String>();
						levelValue = Arrays.asList(rst.getString("level_id").split(","));
						for (int k = 0; k < levelValue.size(); k++) {
							if (levelValue.get(k) != null && !levelValue.get(k).equals("")) {
								levelID.add(levelValue.get(k).trim());
							}
						}
					}
					if (rst.getString("desig_id") != null && !rst.getString("desig_id").equals("")) {
						List<String> desigValue = new ArrayList<String>();
						desigValue = Arrays.asList(rst.getString("desig_id").split(","));
						for (int k = 0; k < desigValue.size(); k++) {
							if (desigValue.get(k) != null && !desigValue.get(k).equals("")) {
								desigID.add(desigValue.get(k).trim());
							}
						}
					}
					if (rst.getString("grade_id") != null && !rst.getString("grade_id").equals("")) {
						List<String> gradeValue = new ArrayList<String>();
						gradeValue = Arrays.asList(rst.getString("grade_id").split(","));
						for (int k = 0; k < gradeValue.size(); k++) {
							if (gradeValue.get(k) != null && !gradeValue.get(k).equals("")) {
								gradeID.add(gradeValue.get(k).trim());
							}
						}
					}
					if (rst.getString("learner_ids") != null && !rst.getString("learner_ids").equals("")) {
//						locationList=null;
						List<String> empValue = new ArrayList<String>();
						empValue = Arrays.asList(rst.getString("learner_ids").split(","));
						for (int k = 0; k < empValue.size(); k++) {
							if(empValue.get(k) != null && !empValue.get(k).equals("")){
								empID.add(empValue.get(k).trim());
							}
						}
					}
					if (rst.getString("skills") != null && !rst.getString("skills").equals("")) {
						List<String> skillValue = new ArrayList<String>();
						skillValue = Arrays.asList(rst.getString("skills").split(","));
						for (int k = 0; k < skillValue.size(); k++) {
							if(skillValue.get(k) != null && !skillValue.get(k).equals("")){
								skillsID.add(skillValue.get(k).trim());
							}
						}
					}

					selectLearnerIDs=rst.getString("learner_ids");
					setOrgids(rst.getString("org_id"));
					setwLocids(rst.getString("location_id"));
					setDeptartids(rst.getString("depart_id"));
					setLevelids(rst.getString("level_id"));
					setDesigids(rst.getString("desig_id"));
				}
				rst.close();
				pst.close();

				List<List<String>> selectEmpList=new ArrayList<List<String>>();
				Map<String,String> hmCheckEmpList=new HashMap<String, String>();

				if(selectLearnerIDs!=null && !selectLearnerIDs.equals("")){
					
					List<String> tmpselectEmpList=Arrays.asList(selectLearnerIDs.split(","));
					
					if(tmpselectEmpList != null && !tmpselectEmpList.isEmpty()){
						for(String empId:tmpselectEmpList){
							List<String> innerList = new ArrayList<String>();
							if(empId.equals("0") || empId.equals("")){
								continue;
							}
							innerList.add(empId);
							innerList.add(hmEmpName.get(empId));
							selectEmpList.add(innerList);
							hmCheckEmpList.put(empId.trim(), empId.trim());
						}
					}
					
				}else{
					selectEmpList=null;
					
				}
			
				request.setAttribute("selectEmpList", selectEmpList);
				request.setAttribute("hmCheckEmpList", hmCheckEmpList);
				request.setAttribute("selectLearnerIDs1", selectLearnerIDs);
				
			} else if (uF.parseToInt(step1) == 1) {
				Map<String, String> hmWeekdays = new HashMap<String, String>();
				List<List<String>> stageList = new ArrayList<List<String>>();
				pst = con.prepareStatement("select * from learning_plan_stage_details where learning_plan_id = ?");
				pst.setInt(1, uF.parseToInt(strID));
				rst = pst.executeQuery();
				while (rst.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rst.getString("learning_plan_stage_id"));
					innerList.add(rst.getString("learning_plan_stage_name_id"));
					innerList.add(rst.getString("learning_plan_stage_name"));
					innerList.add(rst.getString("learning_type"));
					innerList.add(uF.getDateFormat(rst.getString("from_date"), DBDATE, DATE_FORMAT));
					innerList.add(uF.getDateFormat(rst.getString("to_date"), DBDATE, DATE_FORMAT));
					innerList.add(uF.getTimeFormatStr(rst.getString("from_time"), DBTIME, TIME_FORMAT));
					innerList.add(uF.getTimeFormatStr(rst.getString("to_time"), DBTIME, TIME_FORMAT));
					innerList.add(rst.getString("weekdays"));
					stageList.add(innerList);
					if(getExiststageIDs() == null || getExiststageIDs().equals("")){
						setExiststageIDs(","+rst.getString("learning_plan_stage_id")+",");
					}else{
						setExiststageIDs(getExiststageIDs()+rst.getString("learning_plan_stage_id")+",");
					}
					
					List<String> weekdayList = Arrays.asList(rst.getString("weekdays").split(","));
					int weekdaysCnt = 0;
					for (int i = 0; weekdayList != null && !weekdayList.isEmpty() && i < weekdayList.size(); i++) {
						if(weekdayList.get(i).trim().equals("Mon")){
							hmWeekdays.put(rst.getString("learning_plan_stage_id")+"_MON", "checked");
							weekdaysCnt++;
						} else if(weekdayList.get(i).trim().equals("Tue")){
							hmWeekdays.put(rst.getString("learning_plan_stage_id")+"_TUE", "checked");
							weekdaysCnt++;
						} else if(weekdayList.get(i).trim().equals("Wed")){
							hmWeekdays.put(rst.getString("learning_plan_stage_id")+"_WED", "checked");
							weekdaysCnt++;
						} else if(weekdayList.get(i).trim().equals("Thu")){
							hmWeekdays.put(rst.getString("learning_plan_stage_id")+"_THU", "checked");
							weekdaysCnt++;
						} else if(weekdayList.get(i).trim().equals("Fri")){
							hmWeekdays.put(rst.getString("learning_plan_stage_id")+"_FRI", "checked");
							weekdaysCnt++;
						} else if(weekdayList.get(i).trim().equals("Sat")){
							hmWeekdays.put(rst.getString("learning_plan_stage_id")+"_SAT", "checked");
							weekdaysCnt++;
						} else if(weekdayList.get(i).trim().equals("Sun")){
							hmWeekdays.put(rst.getString("learning_plan_stage_id")+"_SUN", "checked");
							weekdaysCnt++;
						} 
					}
					if(weekdaysCnt == 7) {
						hmWeekdays.put(rst.getString("learning_plan_stage_id")+"_EVERYDAY", "checked");
					} else {
						hmWeekdays.put(rst.getString("learning_plan_stage_id")+"_EVERYDAY", "");
					}
					
				}
				rst.close();
				pst.close();

				request.setAttribute("stageList", stageList);
				request.setAttribute("hmWeekdays", hmWeekdays);
				
				getTrainingData(con, uF);
			} else if (uF.parseToInt(step1) == 2) {
				
			} 
			
			if (strID != null) {
				setPlanId(strID);
			}
			request.setAttribute("plan_Id", getPlanId());

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if(rst !=null){
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void getTrainingData(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		try {
	
			Map<String, List<String>> hmTrainingList = new HashMap<String, List<String>>();
			pst = con.prepareStatement("select tp.plan_id,training_title,schedule_id,start_date,end_date,day_schedule_type,training_frequency from training_schedule ts, training_plan tp where ts.plan_id = tp.plan_id");
			rst = pst.executeQuery();
			while (rst.next()) {
				List<String> trainingList = new ArrayList<String>();
				trainingList.add(rst.getString("plan_id"));
				trainingList.add(rst.getString("training_title"));
				trainingList.add("Training");
				hmTrainingList.put(rst.getString("plan_id"), trainingList);
			}
			rst.close();
			pst.close();
			request.setAttribute("hmTrainingList", hmTrainingList);
			
			String schedule_id = null;
			Map<String, List<String>> hmTrainingDataList = new HashMap<String, List<String>>();
			pst = con.prepareStatement("select * from training_schedule join training_session using (schedule_id)");
			rst = pst.executeQuery();
			String trainingSchedulePeriod = null;
			String scheduleTypeValue = null;
			Map<String, String> hmWeekdays1 = new HashMap<String, String>();
			while (rst.next()) {
				List<String> trainingDataList = new ArrayList<String>();
				trainingDataList.add(rst.getString("plan_id"));
				trainingDataList.add(uF.getDateFormat(rst.getString("start_date"), DBDATE, CF.getStrReportDateFormat()));
				trainingDataList.add(uF.getDateFormat(rst.getString("end_date"), DBDATE, CF.getStrReportDateFormat()));
				trainingDataList.add(uF.getDateFormat(rst.getString("start_date"), DBDATE, DATE_FORMAT));
				trainingDataList.add(uF.getDateFormat(rst.getString("end_date"), DBDATE, DATE_FORMAT));
				trainingSchedulePeriod = rst.getString("training_frequency") != null ? rst.getString("training_frequency") : "1";
				trainingDataList.add(rst.getString("training_frequency") != null ? rst.getString("training_frequency") : "1");
				schedule_id = rst.getString("schedule_id");
				if (trainingSchedulePeriod != null && trainingSchedulePeriod.equals("2")) {
					weekdayValue = rst.getString("training_weekday");
					dayValue = "";
				} else if (trainingSchedulePeriod != null && trainingSchedulePeriod.equals("3")) {
					dayValue = rst.getString("training_weekday");
				}
				trainingDataList.add(rst.getString("training_duration_type"));
				trainingDataList.add(rst.getString("day_schedule_type") != null ? rst.getString("day_schedule_type") : "1");
				scheduleTypeValue = rst.getString("day_schedule_type") != null ? rst.getString("day_schedule_type") : "1";
				
				List<String> weekdayList = new ArrayList<String>();
				if(rst.getString("week_days") != null) {
					weekdayList = Arrays.asList(rst.getString("week_days").split(","));
				}
				for (int i = 0; weekdayList != null && !weekdayList.isEmpty() && i < weekdayList.size(); i++) {
					if(weekdayList.get(i).trim().equals("Mon")){
						hmWeekdays1.put(rst.getString("plan_id")+"_MON", "checked");
					} else if(weekdayList.get(i).trim().equals("Tue")){
						hmWeekdays1.put(rst.getString("plan_id")+"_TUE", "checked");
					} else if(weekdayList.get(i).trim().equals("Wed")){
						hmWeekdays1.put(rst.getString("plan_id")+"_WED", "checked");
					} else if(weekdayList.get(i).trim().equals("Thu")){
						hmWeekdays1.put(rst.getString("plan_id")+"_THU", "checked");
					} else if(weekdayList.get(i).trim().equals("Fri")){
						hmWeekdays1.put(rst.getString("plan_id")+"_FRI", "checked");
					} else if(weekdayList.get(i).trim().equals("Sat")){
						hmWeekdays1.put(rst.getString("plan_id")+"_SAT", "checked");
					} else if(weekdayList.get(i).trim().equals("Sun")){
						hmWeekdays1.put(rst.getString("plan_id")+"_SUN", "checked");
					} 
				}
				
				hmTrainingDataList.put(rst.getString("plan_id"), trainingDataList);
			}
			rst.close();
			pst.close();

			Map<String, List<List<String>>> hmSessionData = new HashMap<String, List<List<String>>>();
			List<List<String>> alSessionData = new ArrayList<List<String>>();
			pst=con.prepareStatement("select plan_id,frequency,frequency_date,training_frequency,start_time,end_time,schedule_type,week_days" +
					" from training_schedule join training_session using (schedule_id)");
			rst=pst.executeQuery();
			
			while(rst.next()){
				alSessionData = hmSessionData.get(rst.getString("plan_id"));
				if(alSessionData == null) alSessionData = new ArrayList<List<String>>();
				List<String> alInner= new ArrayList<String>();
				
				alInner.add(rst.getString("training_frequency"));
				alInner.add(uF.getDateFormat(rst.getString("frequency_date"), DBDATE, CF.getStrReportDateFormat()));
				
				String startTime="";
				if(rst.getString("start_time")!=null && !rst.getString("start_time").equals("")){
					startTime=rst.getString("start_time").substring(0,5);
				}					
				alInner.add(startTime);
				
				String endTime="";
				if(rst.getString("end_time")!=null && !rst.getString("end_time").equals("")){
					endTime=rst.getString("end_time").substring(0, 5);
				}
				alInner.add(endTime); 
				alInner.add(rst.getString("schedule_type"));
				alInner.add(rst.getString("week_days"));
				
			 alSessionData.add(alInner);
			 hmSessionData.put(rst.getString("plan_id"), alSessionData);
			 
			}
			rst.close();
			pst.close();
		
			request.setAttribute("scheduleTypeValue", scheduleTypeValue);
			request.setAttribute("trainingSchedulePeriod", trainingSchedulePeriod);
			request.setAttribute("hmTrainingDataList", hmTrainingDataList);
			request.setAttribute("hmSessionData", hmSessionData);
			request.setAttribute("hmWeekdays1", hmWeekdays1);
			
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			if(rst !=null){
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void insertData(UtilityFunctions uF) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
		if (uF.parseToInt(step) == 1) {

			setPlanId(insertStep1Data(con, uF));
			request.setAttribute("plan_Id", getPlanId());

		} else if (uF.parseToInt(step) == 2) {
			request.setAttribute("plan_Id", getPlanId());
			insertStep2Data(con, uF);

		} else if (uF.parseToInt(step) == 3) {
			request.setAttribute("plan_Id", getPlanId());
			insertStep3Data(con, uF);
		} else if(uF.parseToInt(step) == 0){
			attributeID.add(getStrAttribute());
		//	setAlignedwith("2");
		} 
		
		String lPlanName = CF.getLearningPlanNameById(con, uF, getPlanId());
		session.setAttribute(MESSAGE, SUCCESSM+""+lPlanName+" learning plan has been created successfully."+END);
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}

	}

	private void insertStep3Data(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		try {
	
			String question[] = request.getParameterValues("question");
			String addFlag[] = request.getParameterValues("status");
			String optiona[] = request.getParameterValues("optiona");
			String optionb[] = request.getParameterValues("optionb");
			String optionc[] = request.getParameterValues("optionc");
			String optiond[] = request.getParameterValues("optiond");
			String ansType[] = request.getParameterValues("ansType");
			String orientt[] = request.getParameterValues("orientt");
			
			for (int i = 0; question != null && i < question.length; i++) {
					String[] correct = request.getParameterValues("correct"+ orientt[i]);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}

					pst = con.prepareStatement("insert into learning_plan_question_bank(learning_plan_question_text,option_a,option_b,option_c," +
							"option_d,correct_ans,answer_type,learning_plan_id,weightage,added_by,entry_date)values(?,?,?,?, ?,?,?,?, ?,?,?)");
					pst.setString(1, question[i]);
					pst.setString(2, (optiona != null && optiona.length > i ? optiona[i]: ""));
					pst.setString(3, (optionb != null && optionb.length > i ? optionb[i]: ""));
					pst.setString(4, (optionc != null && optionc.length > i ? optionc[i]: ""));
					pst.setString(5, (optiond != null && optiond.length > i ? optiond[i]: ""));
					pst.setString(6, option.toString());
					pst.setInt(7, uF.parseToInt(ansType[i]));
					pst.setInt(8, uF.parseToInt(getPlanId()));
					pst.setDouble(9, uF.parseToDouble("0"));
					pst.setInt(10, uF.parseToInt(strSessionEmpId));
					pst.setDate(11, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.execute();
					pst.close();
			}
			
			if(getSavePublish() != null && getSavePublish().equals("Save And Publish")) {
				String query ="update learning_plan_details set is_publish = true where learning_plan_id = ?";
				pst = con.prepareStatement(query);
				pst.setInt(1, uF.parseToInt(getPlanId()));
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("select learning_plan_name,learner_ids from learning_plan_details where learning_plan_id = ?");
				pst.setInt(1, uF.parseToInt(getPlanId()));
				rst = pst.executeQuery();
				String learnersId = "";
				String learningPlanName = "";
				while (rst.next()) {
					learnersId = rst.getString("learner_ids");
					learningPlanName = rst.getString("learning_plan_name");
				}
				rst.close();
				pst.close();
				
				List<String> selectEmpList = Arrays.asList(learnersId.split(","));
				Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
				
				for(int i=0; selectEmpList!= null && !selectEmpList.isEmpty() && i<selectEmpList.size(); i++) {
					if(!selectEmpList.get(i).equals("") && uF.parseToInt(selectEmpList.get(i)) > 0) {
						String strDomain = request.getServerName().split("\\.")[0];
						String alertData = "<div style=\"float: left;\"> A new Learning ("+learningPlanName+") has been aligned with you by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
						String alertAction = "MyHR.action?callFrom=LPDash&pType=WR";
						
						UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(selectEmpList.get(i));
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
						userAlerts.setStatus(INSERT_WR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();

					}
				}
				
				sendMail(con, getPlanId());
				
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null){
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}


	private void insertStep2Data(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		
		try {
			String[] lstagetypeid = request.getParameterValues("lstagetypeid");
			String[] lstagetype = request.getParameterValues("lstagetype");
			String[] lstagename = request.getParameterValues("lstagename");
			String[] startdate = request.getParameterValues("startdate");
			String[] enddate = request.getParameterValues("enddate");
			String[] starttime = request.getParameterValues("starttime");
			String[] endtime = request.getParameterValues("endtime");
			
			for (int i = 0; lstagetypeid != null && i < lstagetypeid.length; i++) {
				String[] weekdays = request.getParameterValues("weekdays"+lstagetype[i]+"_"+lstagetypeid[i]);
				String weekday = "";
				for (int j = 0; weekdays != null && j < weekdays.length; j++) {
					if(j == 0)
					weekday += ","+weekdays[j]+",";
					else
						weekday += weekdays[j]+",";
				}
				pst = con.prepareStatement("insert into learning_plan_stage_details(learning_plan_stage_name,from_date,to_date,"
								+ "from_time,to_time,learning_type,learning_plan_id,added_by,entry_date,weekdays,learning_plan_stage_name_id)" 
								+ "values(?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setString(1, lstagename[i]);
				if(startdate != null && startdate.length>0) {
					pst.setDate(2, uF.getDateFormat(startdate[i], DATE_FORMAT));
				}else {
					pst.setDate(2, null);
				}
				pst.setDate(3, uF.getDateFormat(enddate[i], DATE_FORMAT));
				pst.setTime(4, uF.getTimeFormat(starttime[i], DBTIME));
				pst.setTime(5, uF.getTimeFormat(endtime[i], DBTIME));
				pst.setString(6, lstagetype[i]);
				pst.setInt(7, uF.parseToInt(getPlanId()));
				pst.setInt(8, uF.parseToInt(strSessionEmpId));
				pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(10, weekday);
				pst.setInt(11, uF.parseToInt(lstagetypeid[i]));
				pst.execute();
				pst.close();
			}
			
			//System.out.println("getStepSave()==>"+getStepSave()+"==>getSavePublish()==>"+getSavePublish());
			if(getSavePublish() != null && getSavePublish().equals("Save And Publish")) {
				String query ="update learning_plan_details set is_publish = true where learning_plan_id = ?";
				pst = con.prepareStatement(query);
				pst.setInt(1, uF.parseToInt(getPlanId()));
			//	System.out.println("pst==>"+pst);
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("select learning_plan_name,learner_ids from learning_plan_details where learning_plan_id = ?");
				pst.setInt(1, uF.parseToInt(getPlanId()));
				rst = pst.executeQuery();
				String learnersId = "";
				String learningPlanName = "";
				while (rst.next()) {
					learnersId = rst.getString("learner_ids");
					learningPlanName = rst.getString("learning_plan_name");
				}
				rst.close();
				pst.close();
				
				List<String> selectEmpList = Arrays.asList(learnersId.split(","));
				Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
				
				for(int i=0; selectEmpList!= null && !selectEmpList.isEmpty() && i<selectEmpList.size(); i++) {
					if(!selectEmpList.get(i).equals("") && uF.parseToInt(selectEmpList.get(i)) > 0) {
						String strDomain = request.getServerName().split("\\.")[0];
						String alertData = "<div style=\"float: left;\"> A new Learning ("+learningPlanName+") has been aligned with you by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
						String alertAction = "MyHR.action?callFrom=LPDash&pType=WR";
						
						UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(selectEmpList.get(i));
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
						userAlerts.setStatus(INSERT_WR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();

					}
				}
				sendMail(con, getPlanId());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null){
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	private String insertStep1Data(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rst = null;

		String plan_idNew = null;

		String orgids = request.getParameter("orgids");
		String wLocids = request.getParameter("wLocids");
		String deptartids = request.getParameter("deptartids");
		String levelids = request.getParameter("levelids");
		String desigids = request.getParameter("desigids");
//		System.out.println("getEmpselected==>"+getEmpselected());
		try {
//			System.out.println("insert getAlignedwith()=======>"+getAlignedwith());
			pst = con.prepareStatement("insert into learning_plan_details(learning_plan_name,learning_plan_objective,group_or_condition," +
					"attribute_id,learner_ids,org_id,location_id,depart_id,level_id,desig_id,grade_id,added_by,entry_date,certificate_id,skills)" +
					"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setString(1, getLearningTitle());
			pst.setString(2, getLearningObjective());
			pst.setInt(3, uF.parseToInt(getAlignedwith()));
			pst.setString(4, getAppendData(getStrAttribute()));
			if(getAlignedwith() != null && (getAlignedwith().equals("1") || getAlignedwith().equals("4") || getAlignedwith().equals("5"))) {
//				pst.setString(5, "");
				pst.setString(5, getEmpselected());
				pst.setString(6, orgids != null ? orgids : ""); //getAppendData(getF_organization()));
				pst.setString(7, wLocids != null ? wLocids : "");
				pst.setString(8, deptartids != null ? deptartids : "");
				pst.setString(9, levelids != null ? levelids : "");//getAppendData(getStrlearnerLevel()));
				pst.setString(10, desigids != null ? desigids : "");//getAppendData(getStrlearnerDesignation()));
				pst.setString(11, getAppendData(getStrlearnerGrade()));
			} else {
				pst.setString(5, getEmpselected());
				pst.setString(6, ""); //getAppendData(getF_organization()));
				pst.setString(7, "");
				pst.setString(8, "");
				pst.setString(9, "");//getAppendData(getStrlearnerLevel()));
				pst.setString(10, "");//getAppendData(getStrlearnerDesignation()));
				pst.setString(11, getAppendData(getStrlearnerGrade()));
			}
			pst.setInt(12, uF.parseToInt(strSessionEmpId));
			pst.setDate(13, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(14, uF.parseToInt(getStrCertificateId()));
		
			pst.setString(15, getAppendData(getSkills()));
			int x= pst.executeUpdate();
			pst.close();
			
			if(x > 0){
				pst = con.prepareStatement(" select max(learning_plan_id) as learning_plan_id from learning_plan_details ");
				rst = pst.executeQuery();
				while (rst.next()) {
					plan_idNew = rst.getString("learning_plan_id");
				}
				rst.close();
				pst.close();
				
				if(uF.parseToInt(getStrGapId()) > 0 && uF.parseToInt(getAlignedwith()) == 2){
					pst = con.prepareStatement("select emp_id from training_gap_details where training_gap_id= ?");
					pst.setInt(1,uF.parseToInt(getStrGapId()));
					rst=pst.executeQuery();
					String tGapEmpid=null;
					while(rst.next()){
						tGapEmpid=rst.getString("emp_id");		
					}
					rst.close();
					pst.close();
										
					List<String> empList = getEmpselected()!=null && getEmpselected().length()>0 ? Arrays.asList(getEmpselected().trim().split(",")) : null;
					if(empList!=null && empList.contains(tGapEmpid)){
						pst = con.prepareStatement("update training_gap_details set is_training_schedule= ?,assign_learning_plan_id=? where training_gap_id= ? ");
						pst.setBoolean(1, true);
						pst.setInt(2,uF.parseToInt(plan_idNew));
						pst.setInt(3,uF.parseToInt(getStrGapId()));
						pst.execute();
						pst.close();
					}
				}
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null){
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return plan_idNew;
	}
	
	
	public void sendMail(Connection con, String plan_idNew) {

		ResultSet rst1 = null;
		PreparedStatement pst1 = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
//			System.out.println("Req id is ========= "+getStrId());
			String lPlanName = "";
			String learnersId = "";
			pst1 = con.prepareStatement("select learning_plan_name,learner_ids from learning_plan_details where learning_plan_id = ?");
			pst1.setInt(1, uF.parseToInt(plan_idNew));
			rst1 = pst1.executeQuery();
			while (rst1.next()) {
				lPlanName = rst1.getString("learning_plan_name");
				learnersId = rst1.getString("learner_ids");
			}
			rst1.close();
			pst1.close();
			
			List<String> selectEmpList = Arrays.asList(learnersId.split(","));
			
			List<String> startAndEndDate = getStartAndEndDate(con, uF, uF.parseToInt(plan_idNew));
//			System.out.println("panel_employee_id ===> "+panel_employee_id);
			if(lPlanName != null && !lPlanName.equals("")) {
				StringBuilder sbLearnersName = null;
				
				for(int i=0; selectEmpList!= null && i<selectEmpList.size(); i++){
					if(uF.parseToInt(selectEmpList.get(i).trim()) == 0){
						continue;
					}
					
					Map<String, String> hmEmpInner = hmEmpInfo.get(selectEmpList.get(i));
					if(sbLearnersName == null) {
						sbLearnersName = new StringBuilder();
						sbLearnersName.append(hmEmpInner.get("FNAME")+" " +hmEmpInner.get("LNAME"));
					} else {
						sbLearnersName.append(", "+ hmEmpInner.get("FNAME")+" " +hmEmpInner.get("LNAME"));
					}
					if(selectEmpList.get(i) != null && !selectEmpList.get(i).equals("")){
						String strDomain = request.getServerName().split("\\.")[0];	
						Notifications nF = new Notifications(N_NEW_LEARNING_PLAN_FOR_LEARNERS, CF);
			//			System.out.println("Emp ID is ========= "+rst1.getString("panel_emp_id"));
						 nF.setDomain(strDomain);
						 nF.request = request;
						 nF.setStrEmpId(selectEmpList.get(i));
						 nF.setStrHostAddress(CF.getStrEmailLocalHost());
						 nF.setStrHostPort(CF.getStrHostPort());
						 nF.setStrContextPath(request.getContextPath());
						 nF.setStrLearningPlanName(lPlanName);
						 nF.setStrLearningPlanStartdate(startAndEndDate.get(0));
						 nF.setStrLearningPlanEnddate(startAndEndDate.get(1));
						 nF.setStrEmpFname(hmEmpInner.get("FNAME"));
						 nF.setStrEmpLname(hmEmpInner.get("LNAME"));
						 nF.setEmailTemplate(true);
						 nF.sendNotifications();
					}
				}
				
				Map<String, String> hmEmpInner = hmEmpInfo.get(strSessionEmpId);
				String strDomain = request.getServerName().split("\\.")[0];	
				Notifications nF = new Notifications(N_NEW_LEARNING_PLAN_FOR_HR, CF);
	//			System.out.println("Emp ID is ========= "+rst1.getString("panel_emp_id"));
				 nF.setDomain(strDomain);
				 nF.request = request;
				 nF.setStrEmpId(strSessionEmpId);
				 nF.setStrHostAddress(CF.getStrEmailLocalHost());
				 nF.setStrHostPort(CF.getStrHostPort());
				 nF.setStrContextPath(request.getContextPath());
				 nF.setStrLearningPlanName(lPlanName);
				 nF.setStrLearningPlanStartdate(startAndEndDate.get(0));
				 nF.setStrLearningPlanEnddate(startAndEndDate.get(1));
				 nF.setStrLearnersName(sbLearnersName.toString());
				 
				 nF.setStrEmpFname(hmEmpInner.get("FNAME"));
				 nF.setStrEmpLname(hmEmpInner.get("LNAME"));
				 nF.setEmailTemplate(true);
				 nF.sendNotifications();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst1 !=null){
				try {
					rst1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst1 !=null){
				try {
					pst1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private List<String> getStartAndEndDate(Connection con, UtilityFunctions uF, int learningPlanId) {
		
		PreparedStatement pst = null;
		ResultSet rst=null;
		List<String> startAndEndDate = new ArrayList<String>();
//		StringBuilder startEndDate = new StringBuilder();
		try {
			pst = con.prepareStatement("select min(from_date) as minDate, max(to_date) as maxDate from learning_plan_stage_details where learning_plan_id = ?");
			pst.setInt(1, learningPlanId);
			rst = pst.executeQuery();
		//	System.out.println("pst1 =====> " + pst1);
			String minFromDate = null, maxToDate = null; 
			while (rst.next()) {
				minFromDate = rst.getString("minDate");
				maxToDate = rst.getString("maxDate");
			}
			rst.close();
			pst.close();
			startAndEndDate.add(uF.getDateFormat(minFromDate, DBDATE, CF.getStrReportDateFormat()));
			startAndEndDate.add(uF.getDateFormat(maxToDate, DBDATE, CF.getStrReportDateFormat()));
			
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			if(rst !=null){
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return startAndEndDate;
	}
	
	public String getAppendData(String strIds) {
		
		StringBuilder sb = new StringBuilder();
		if(strIds != null && !strIds.equals("")) {
			
			List<String> idsList = Arrays.asList(strIds.split(","));
			if (idsList != null && !idsList.isEmpty()) {
				
				for (int i = 0; i < idsList.size(); i++) {
					if (i == 0) {
						sb.append("," + idsList.get(i).trim() + ",");
					} else {
						sb.append(idsList.get(i).trim() + ",");
					}
				}
			} else {
				return null;
			}
		}
		return sb.toString();
	}
	
	//===start parvez date: 08-10-2021===
	private void getVideoList() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			List<List<String>> videoList = new ArrayList<List<String>>();
			//===start parvez date: 16-10-2021===
//			pst = con.prepareStatement("select * from learning_video_details where learning_video_id not in(select learning_plan_stage_name_id from learning_plan_stage_details where learning_type = 'Video')");
			pst = con.prepareStatement("select * from learning_video_details");
			//===end parvez date: 16-10-2021===
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("learning_video_id"));
				innerList.add(rs.getString("learning_video_title"));
				videoList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("videoList", videoList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	//===end parvez date: 08-10-2021===
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;

	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	private String step;

	// form parameters **********

	public String getStrEmployee() {
		return strEmployee;
	}

	public void setStrEmployee(String strEmployee) {
		this.strEmployee = strEmployee;
	}

	String strEmployee;

	List<FillAttribute> attributeList;
	List<FillSkills> skillslist;

	public List<FillSkills> getSkillslist() {
		return skillslist;
	}

	public void setSkillslist(List<FillSkills> skillslist) {
		this.skillslist = skillslist;
	}

	public List<FillAttribute> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<FillAttribute> attributeList) {
		this.attributeList = attributeList;
	}

	// first step variables **********

	private String learningTitle;
	private String learningObjective;
	private String alignedwith;
	private String strCertificateId;
	private String f_organization;
	private String empLocation;
	private String strlearnerLevel;
	private String strlearnerDesignation;
	private String strlearnerGrade;
	private String empselected;
	private String strAttribute;
	private String skills;
	private String certificateId;
	private boolean boolPublished;
	
	
	public boolean isBoolPublished() {
		return boolPublished;
	}


	public void setBoolPublished(boolean boolPublished) {
		this.boolPublished = boolPublished;
	}


	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public String getStrCertificateId() {
		return strCertificateId;
	}

	public void setStrCertificateId(String strCertificateId) {
		this.strCertificateId = strCertificateId;
	}

	public String getCertificateId() {
		return certificateId;
	}

	public void setCertificateId(String certificateId) {
		this.certificateId = certificateId;
	}

	public List<FillCertificate> getCertificateList() {
		return certificateList;
	}

	public void setCertificateList(List<FillCertificate> certificateList) {
		this.certificateList = certificateList;
	}

	public String getLearningTitle() {
		return learningTitle;
	}

	public void setLearningTitle(String learningTitle) {
		this.learningTitle = learningTitle;
	}

	public String getLearningObjective() {
		return learningObjective;
	}

	public void setLearningObjective(String learningObjective) {
		this.learningObjective = learningObjective;
	}

	public String getAlignedwith() {
		return alignedwith;
	}

	public void setAlignedwith(String alignedwith) {
		this.alignedwith = alignedwith;
	}

	public String getF_organization() {
		return f_organization;
	}

	public void setF_organization(String f_organization) {
		this.f_organization = f_organization;
	}

	public String getEmpLocation() {
		return empLocation;
	}

	public void setEmpLocation(String empLocation) {
		this.empLocation = empLocation;
	}

	public String getStrlearnerLevel() {
		return strlearnerLevel;
	}

	public void setStrlearnerLevel(String strlearnerLevel) {
		this.strlearnerLevel = strlearnerLevel;
	}

	public String getStrlearnerDesignation() {
		return strlearnerDesignation;
	}

	public void setStrlearnerDesignation(String strlearnerDesignation) {
		this.strlearnerDesignation = strlearnerDesignation;
	}

	public String getStrlearnerGrade() {
		return strlearnerGrade;
	}

	public void setStrlearnerGrade(String strlearnerGrade) {
		this.strlearnerGrade = strlearnerGrade;
	}

	public String getEmpselected() {
		return empselected;
	}

	public void setEmpselected(String empselected) {
		this.empselected = empselected;
	}

	public String getStrAttribute() {
		return strAttribute;
	}

	public void setStrAttribute(String strAttribute) {
		this.strAttribute = strAttribute;
	}

	// fields for 2nd screen Stage info **************
		String existstageIDs;
		
		public String getExiststageIDs() {
			return existstageIDs;
		}


		public void setExiststageIDs(String existstageIDs) {
			this.existstageIDs = existstageIDs;
		}

	
	// Fields for 3rd Screen ****************

	
	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getStepSubmit() {
		return stepSubmit;
	}

	public void setStepSubmit(String stepSubmit) {
		this.stepSubmit = stepSubmit;
	}

	public String getStepSave() {
		return stepSave;
	}

	public void setStepSave(String stepSave) {
		this.stepSave = stepSave;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public List<List<String>> getAlSessionData() {
		return alSessionData;
	}

	public void setAlSessionData(List<List<String>> alSessionData) {
		this.alSessionData = alSessionData;
	}

	public List<String> getAttributeID() {
		return attributeID;
	}

	public void setAttributeID(List<String> attributeID) {
		this.attributeID = attributeID;
	}

	public List<String> getSkillsID() {
		return skillsID;
	}

	public void setSkillsID(List<String> skillsID) {
		this.skillsID = skillsID;
	}

	public List<String> getOrgID() {
		return orgID;
	}

	public void setOrgID(List<String> orgID) {
		this.orgID = orgID;
	}

	public List<String> getLocID() {
		return locID;
	}

	public void setLocID(List<String> locID) {
		this.locID = locID;
	}

	public List<String> getLevelID() {
		return levelID;
	}

	public void setLevelID(List<String> levelID) {
		this.levelID = levelID;
	}

	public List<String> getDesigID() {
		return desigID;
	}

	public void setDesigID(List<String> desigID) {
		this.desigID = desigID;
	}

	public List<String> getGradeID() {
		return gradeID;
	}

	public void setGradeID(List<String> gradeID) {
		this.gradeID = gradeID;
	}

	public List<String> getEmpID() {
		return empID;
	}

	public void setEmpID(List<String> empID) {
		this.empID = empID;
	}

	public String getWeekdayValue() {
		return weekdayValue;
	}

	public void setWeekdayValue(String weekdayValue) {
		this.weekdayValue = weekdayValue;
	}

	public String getDayValue() {
		return dayValue;
	}

	public void setDayValue(String dayValue) {
		this.dayValue = dayValue;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getOrgids() {
		return orgids;
	}

	public void setOrgids(String orgids) {
		this.orgids = orgids;
	}

	public String getwLocids() {
		return wLocids;
	}

	public void setwLocids(String wLocids) {
		this.wLocids = wLocids;
	}

	public String getDeptartids() {
		return deptartids;
	}

	public void setDeptartids(String deptartids) {
		this.deptartids = deptartids;
	}

	public String getLevelids() {
		return levelids;
	}

	public void setLevelids(String levelids) {
		this.levelids = levelids;
	}

	public String getDesigids() {
		return desigids;
	}

	public void setDesigids(String desigids) {
		this.desigids = desigids;
	}

	public String getSavePublish() {
		return savePublish;
	}

	public void setSavePublish(String savePublish) {
		this.savePublish = savePublish;
	}


	public String getStrGapEmpId() {
		return strGapEmpId;
	}


	public void setStrGapEmpId(String strGapEmpId) {
		this.strGapEmpId = strGapEmpId;
	}


	public String getStrGapId() {
		return strGapId;
	}


	public void setStrGapId(String strGapId) {
		this.strGapId = strGapId;
	}

}