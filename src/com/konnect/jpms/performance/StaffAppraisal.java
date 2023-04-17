package com.konnect.jpms.performance;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;

public class StaffAppraisal implements ServletRequestAware, IStatements, Runnable {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strSessionUserTypeID;
	String strUserType = null;
	CommonFunctions CF;
	private String id;
	private String empID;
	private String currentLevel;
	private String role;
	// String tab;
	// String levelId;
	private String userType;
	private String levelCount;
	private String appFreqId;

	File levelcommentFile;
	String levelcommentFileFileName;
	
	private String areasOfStrength;
	private String areasOfImprovement;
	private String isAreasOfStrengthAndImprovement;
	
	private String btnSave;
	private String dataType;	//created by parvez: 13-07-2022
		
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strSessionUserTypeID = (String) session.getAttribute(USERTYPEID);
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		strUserType = (String) session.getAttribute(USERTYPE);
		
		request.setAttribute(PAGE, "/jsp/performance/StaffAppraisal.jsp");
		request.setAttribute(TITLE, "Review Form");

		request.setAttribute("arrEnabledModules", CF.getArrEnabledModules());
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
//		System.out.println("role=="+getRole());
		UtilityFunctions uF = new UtilityFunctions();
		viewProfile(getEmpID());
		getanswerTypeMap(uF);
		getLevelStep(uF);
		getMainLevelStep(uF);
		// if (tab == null) {
		// tab = "1";
		// } else {
		// tab = uF.parseToInt(tab) + 1 + "";
		// }
		getQuestionSubType(uF);
		getAppraisalDetail(uF);
		getEmployyDetailsList(uF);
		getAppraisalQuestionMap(uF);
		
	//===start parvez date: 07-07-2022===	
		getAppraisalReopenStatus(uF);
	//===end parvez date: 07-07-2022===	
		
	//===start parvez date: 13-07-2022===	
		if(getDataType()!=null && getDataType().equals("Reviewer Feedback")){
			getReviewerFeedbackDetails(uF);
		}
	//===end parvez date: 13-07-2022===

		getKRATargetDetails();
		getKRADetails();
//		System.out.println("SAp/106--levelCount=="+levelCount);
		if (levelCount == null || levelCount.equalsIgnoreCase("null")) {
			levelCount = "1";
		} else {
			int cnt = uF.parseToInt(levelCount);
			cnt++;
			setLevelCount("" + cnt);
		}
		String submit = request.getParameter("submit");
		String btnfinish = request.getParameter("btnfinish");
		String levelAppSystem = request.getParameter("levelAppSystem");
		getEmployeeAssignedKRAAndGoalTarget(uF);
		
	//===start parvez date: 24-12-2021===
		String appSystemType = request.getParameter("appSystemType");
	//===end parvez date: 24-12-2021===	
//		System.out.println("SA/121--0--levelAppSystem="+levelAppSystem);
		if (submit == null && btnfinish == null && getBtnSave() == null) {
			getLevelStatus(uF);
			getTargetDetails();
			getKRAAmtDetails();
			checkCurrentLevelExistForCurrentEmp(uF);
			getFinalResult();
		//===start parvez date: 24-12-2021===
			if(appSystemType != null && uF.parseToInt(appSystemType) == 6){
				insertNewGoalForNxtCycle();
			}
		//===end parvez date: 24-12-2021===
			return getLevelQuestion(uF);

		} else {
			getPreviousLevelData(uF);
			if (levelAppSystem != null) {
//				System.out.println("levelAppSystem ===>> " + levelAppSystem);
				// if(levelAppSystem.equals("3") || levelAppSystem.equals("5")){
				// insertTarget();
				//
				// }else if(levelAppSystem.equals("4")){
				// insertKRA();
				//
				// }else{
				// System.out.println("levelAppSystem else ========= > "+levelAppSystem);
//				insertMarks(uF);
				// }
//				System.out.println("getDataType---"+getDataType());
//				System.out.println("SA/150--1--levelCount="+levelCount);
				if(getDataType()!=null && getDataType().equals("Reviewer Feedback")){
					insertReviewerFeedback(uF);
				} else{
					insertMarks(uF);
				}
//			} else if(getDataType()!=null && getDataType().equals("Reviewer Feedback") && (btnfinish != null && btnfinish.equals("Preview")) || getBtnSave() != null && getBtnSave().equals("Save")){
//				insertReviewerFeedback(uF);
			}
			
			getTargetDetails();
			getKRAAmtDetails();
		//===start parvez date: 24-12-2021===
			if(appSystemType != null && uF.parseToInt(appSystemType) == 6 && strUserType.equals(EMPLOYEE)){
				insertNewGoalForNxtCycle();
			}
		//===end parvez date: 24-12-2021===	
			if (uF.parseToInt(levelCount) > 2) {
				if (swapLevelId(uF)) {
					// String btnSubmit = request.getParameter("btnfinish");
//					System.out.println("btnfinish 1 =========>> " + btnfinish);
					if (btnfinish != null && btnfinish.equals("Submit")) {
						String strDomain = request.getServerName().split("\\.")[0];
						setDomain(strDomain);
						Thread th = new Thread(this);
						th.start();
//						System.out.println("btnfinish 2 =========>> " + btnfinish);
						return "finish";
					} else {
						return "update";
					}
				}
			}
			getLevelStatus(uF);
			checkCurrentLevelExistForCurrentEmp(uF);
			getFinalResult();
			
			return getLevelQuestion(uF);
		}

	}

	
	private void getEmployeeAssignedKRAAndGoalTarget(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		// boolean flag = false;
		try {
			con = db.makeConnection(con);
			List<String> alKRAIds = new ArrayList<String>();
			String kraTypes = INDIVIDUAL_GOAL+","+INDIVIDUAL_KRA+","+EMPLOYEE_KRA;
			pst = con.prepareStatement("select * from goal_kras where goal_type in ("+kraTypes+") and emp_ids like '%,"+getEmpID()+",%'");
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while (rs.next()) {
				alKRAIds.add(rs.getString("goal_kra_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("alKRAIds", alKRAIds);
			
			List<String> alGoarTargetIds = new ArrayList<String>();
			pst = con.prepareStatement("select * from goal_details where emp_ids like '%,"+getEmpID()+",%'");
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while (rs.next()) {
				alGoarTargetIds.add(rs.getString("goal_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("alGoarTargetIds", alGoarTargetIds);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void viewProfile(String strEmpIdReq) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			
			CF.getEmpProfileDetail(con, request, session, CF, uF, strUserType, strEmpIdReq);
			request.setAttribute(TITLE, "Review Form");

			CF.getElementList(con, request);
			CF.getAttributes(con, request, strEmpIdReq);

			List<List<String>> alSkills = new ArrayList<List<String>>();
			alSkills = CF.selectSkills(con, uF.parseToInt(strEmpIdReq));
			request.setAttribute("alSkills", alSkills);

			// request.setAttribute("alActivityDetails", alActivityDetails);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		// return SUCCESS;

	}

	private void getKRAAmtDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from kra_rating_details order by kra_rating_id");
			rs = pst.executeQuery();
			Map<String, String> hmKRARating = new LinkedHashMap<String, String>();
			while (rs.next()) {
				String kraValue = rs.getString("kra_rating_id") + ":_:" + rs.getString("rating");
				hmKRARating.put(rs.getString("goal_id") + rs.getString("goal_kra_id") + rs.getString("emp_id") + rs.getString("appraisal_id")
					+ rs.getString("added_by") + rs.getString("user_type_id")+rs.getString("appraisal_freq_id"), kraValue);
			}
			rs.close();
			pst.close();

			request.setAttribute("hmKRARating", hmKRARating);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	// private void insertKRA() {
	// Connection con = null;
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	// Database db = new Database();
	// db.setRequest(request);
	// UtilityFunctions uf = new UtilityFunctions();
	//
	// String[] goalid = request.getParameterValues("goalid");
	// String[] goalkraid = request.getParameterValues("goalkraid");
	// String[] weightage = request.getParameterValues("weightage");
	// String[] usertype = request.getParameterValues("usertype");
	// String[] kra_rating_id = request.getParameterValues("kra_rating_id");
	// String[] app_level_id=request.getParameterValues("app_level_id");
	//
	// try {
	// con = db.makeConnection(con);
	//
	// // System.out.println("getId===>"+getId());
	// // System.out.println("getEmpid===>"+getEmpID());
	// con.setAutoCommit(false);
	// if (usertype != null && !usertype.equals("")) {
	//
	//
	// for (int i = 0; i < usertype.length; i++) {
	//
	// if(kra_rating_id[i]!=null && !kra_rating_id[i].equals("")){
	// pst =
	// con.prepareStatement("delete from kra_rating_details where kra_rating_id=?");
	// pst.setInt(1, uf.parseToInt(kra_rating_id[i]));
	// pst.execute();
	// }
	//
	// //goal_kra_id,goal_id,appraisal_id,emp_id,user_type_id,added_by,rating,weightage,entry_date,
	//
	// String[]
	// rating=request.getParameterValues("gradewithrating"+usertype[i]);
	// pst =
	// con.prepareStatement("insert into kra_rating_details(goal_kra_id,goal_id,appraisal_id,emp_id,user_type_id,added_by,"
	// +
	// "rating,weightage,entry_date,appraisal_level_id)values(?,?,?,?,?,?,?,?,?,?)");
	// pst.setInt(1, uf.parseToInt(goalkraid[i]));
	// pst.setInt(2, uf.parseToInt(goalid[i]));
	// pst.setInt(3, uf.parseToInt(getId()));
	// pst.setInt(4, uf.parseToInt(getEmpID()));
	// pst.setInt(5, uf.parseToInt(usertype[i]));
	// pst.setInt(6, uf.parseToInt(strSessionEmpId));
	// double marks=uf.parseToDouble(rating[i]) * uf.parseToDouble(weightage[i])
	// / 5;
	// pst.setDouble(7,marks);
	// pst.setDouble(8,uf.parseToDouble(weightage[i]));
	// pst.setDate(9,uf.getCurrentDate(CF.getStrTimeZone()) );
	// pst.setInt(10, uf.parseToInt(app_level_id[i]));
	//
	// pst.execute();
	// }
	// con.commit();
	// }
	//
	// } catch (Exception e) {
	// try {
	// con.rollback();
	// } catch (SQLException e1) {
	// e1.printStackTrace();
	// }
	// e.printStackTrace();
	// } finally {
	//
	// db.closeStatements(pst);
	// db.closeResultSet(rs);
	// db.closeConnection(con);
	// }
	// }

	private void getKRADetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uf = new UtilityFunctions();

		Map<String, List<List<String>>> hmKRA = new LinkedHashMap<String, List<List<String>>>();
		boolean levelFlag = false;

		try {
			con = db.makeConnection(con);
			Map<String, String> hmGoalOrientation = new HashMap<String, String>();
			Map<String, String> hmMesures = new HashMap<String, String>();
			Map<String, String> hmMesuresType = new HashMap<String, String>();
			Map<String, String> hmGoalTitle = new HashMap<String, String>();
			Map<String, String> orientationMp = getOrientationValue(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);

			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uf.parseToInt(getId()));
			rs = pst.executeQuery();

			Map<String, String> appraisalMp = new HashMap<String, String>();
			int memberCount = 0;
			List<String> memberList = new ArrayList<String>();
			while (rs.next()) {
				memberList = Arrays.asList(rs.getString("usertype_member").split(","));
			}
			rs.close();
			pst.close();

			request.setAttribute("memberList1", memberList);

			if (getEmpID() != null && !getEmpID().equals("")) {

				StringBuilder sb = new StringBuilder();
				/*
				 * sb.append(
				 * "select * from goal_kras k join goal_details g on k.goal_id=g.goal_id and g.emp_ids like '%"
				 * + getEmpID() + "%' " +
				 * "and g.goal_type=4 and (measure_type='' or measure_type is null) order by k.goal_id"
				 * );
				 */
				sb.append("select * from goal_kras k join goal_details g on k.goal_id=g.goal_id ");
				// sb.append(" and g.emp_ids like '%"+ getEmpID()+ "%' ");
				sb.append(" and g.goal_type=4 and (measure_type='' or measure_type is null) order by k.goal_id");

				pst = con.prepareStatement(sb.toString());
				rs = pst.executeQuery();

				while (rs.next()) {
					List<List<String>> outerList = hmKRA.get(rs.getString("goal_id"));
					if (outerList == null)
						outerList = new ArrayList<List<String>>();
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("goal_kra_id"));
					innerList.add(rs.getString("goal_id"));
					innerList.add(rs.getString("entry_date"));
					innerList.add(rs.getString("effective_date"));
					innerList.add(rs.getString("is_approved"));
					innerList.add(rs.getString("approved_by"));
					innerList.add(rs.getString("kra_order"));
					innerList.add(rs.getString("kra_description"));
					innerList.add(rs.getString("goal_type"));
					innerList.add(rs.getString("weightage"));

					hmGoalOrientation.put(rs.getString("goal_id"), rs.getString("orientation_id"));

					hmGoalTitle.put(rs.getString("goal_id"), rs.getString("goal_title"));

					String measures = "";
					if (rs.getString("measure_type") != null) {
						if (rs.getString("measure_type").equals("$")) {
							measures = rs.getString("measure_currency_value");
						} else if (rs.getString("measure_type").equals("Effort")) {
							measures = rs.getString("measure_effort_days") + " Days and " + rs.getString("measure_effort_hrs") + " Hrs.";
						}
					}
					hmMesures.put(rs.getString("goal_id"), measures);
					hmMesuresType.put(rs.getString("goal_id"), rs.getString("measure_type"));

					outerList.add(innerList);
					hmKRA.put(rs.getString("goal_id"), outerList);

					levelFlag = true;
				}
				rs.close();
				pst.close();
			}
			// System.out.println("hmKRA1=====>"+hmKRA);
			request.setAttribute("hmKRA1", hmKRA);
			request.setAttribute("hmMesures1", hmMesures);
			request.setAttribute("hmMesuresType1", hmMesuresType);
			request.setAttribute("hmGoalOrientation1", hmGoalOrientation);
			request.setAttribute("hmGoalTitle1", hmGoalTitle);

			request.setAttribute("orientationMemberMp1", orientationMemberMp);
			request.setAttribute("levelFlag", levelFlag);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	// private void insertTarget() {
	// Connection con = null;
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	// Database db = new Database();
	// db.setRequest(request);
	// UtilityFunctions uf = new UtilityFunctions();
	//
	// String[] goalid = request.getParameterValues("goalid");
	// String[] goalkraid = request.getParameterValues("goalkraid");
	// //String[] weightage = request.getParameterValues("weightage");
	// String[] usertype = request.getParameterValues("usertype");
	// String[] target_id=request.getParameterValues("target_id");
	// String[] app_level_id=request.getParameterValues("app_level_id");
	//
	// try {
	// con = db.makeConnection(con);
	//
	// // System.out.println("getId===>"+getId());
	// // System.out.println("getEmpid===>"+getEmpID());
	// con.setAutoCommit(false);
	// if (usertype != null && !usertype.equals("")) {
	//
	//
	// for (int i = 0; i < usertype.length; i++) {
	// // System.out.println("target_id[i]===>"+target_id[i]);
	// if(target_id[i]!=null && !target_id[i].equals("")){
	// pst =
	// con.prepareStatement("delete from target_details where target_id=?");
	// pst.setInt(1, uf.parseToInt(target_id[i]));
	// pst.execute();
	// }
	//
	// //goal_kra_id,goal_id,appraisal_id,emp_id,user_type_id,added_by,rating,weightage,entry_date,
	//
	// String[] amt_percentage=request.getParameterValues("amount"+usertype[i]);
	// String[]
	// amt_percentage_type=request.getParameterValues("amtPercentage"+usertype[i]);
	// pst = con
	// .prepareStatement("insert into target_details(goal_kra_id,goal_id,appraisal_id,emp_id,user_type_id,added_by,"
	// +
	// "amt_percentage,amt_percentage_type,entry_date,appraisal_level_id)values(?,?,?,?,?,?,?,?,?,?)");
	// pst.setInt(1, uf.parseToInt(goalkraid[i]));
	// pst.setInt(2, uf.parseToInt(goalid[i]));
	// pst.setInt(3, uf.parseToInt(getId()));
	// pst.setInt(4, uf.parseToInt(getEmpID()));
	// pst.setInt(5, uf.parseToInt(usertype[i]));
	// pst.setInt(6, uf.parseToInt(strSessionEmpId));
	// pst.setDouble(7,uf.parseToDouble(amt_percentage[i]));
	// pst.setString(8,amt_percentage_type[i]);
	// pst.setDate(9,uf.getCurrentDate(CF.getStrTimeZone()) );
	// pst.setInt(10, uf.parseToInt(app_level_id[i]));
	//
	// pst.execute();
	// }
	// con.commit();
	// }
	//
	// } catch (Exception e) {
	// try {
	// con.rollback();
	// } catch (SQLException e1) {
	// e1.printStackTrace();
	// }
	// e.printStackTrace();
	// } finally {
	//
	// db.closeStatements(pst);
	// db.closeResultSet(rs);
	// db.closeConnection(con);
	// }
	// }

	private void getTargetDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from target_details order by target_id");
			rs = pst.executeQuery();
			Map<String, String> hmTarget = new LinkedHashMap<String, String>();
			while (rs.next()) {
				String kraValue = rs.getString("target_id") + ":_:" + rs.getString("amt_percentage") + ":_:" + rs.getString("amt_percentage_type");

				hmTarget.put(rs.getString("goal_id") + rs.getString("goal_kra_id") + rs.getString("emp_id") + rs.getString("appraisal_id")
					+ rs.getString("added_by") + rs.getString("user_type_id")+rs.getString("appraisal_freq_id"), kraValue);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmTarget", hmTarget);
			// System.out.println("hmTarget"+hmTarget);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	private void getKRATargetDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uf = new UtilityFunctions();

		Map<String, List<List<String>>> hmKRA = new LinkedHashMap<String, List<List<String>>>();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmGoalOrientation = new HashMap<String, String>();
			Map<String, String> hmMesures = new HashMap<String, String>();
			Map<String, String> hmMesuresType = new HashMap<String, String>();
			Map<String, String> hmGoalTitle = new HashMap<String, String>();
			Map<String, String> orientationMp = getOrientationValue(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uf.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, String> appraisalMp = new HashMap<String, String>();
			int memberCount = 0;
			List<String> memberList = new ArrayList<String>();
			while (rs.next()) {
				memberList = Arrays.asList(rs.getString("usertype_member").split(","));
			}
			rs.close();
			pst.close();

			request.setAttribute("memberList", memberList);

			if (getEmpID() != null && !getEmpID().equals("")) {
				StringBuilder sb = new StringBuilder();
				/*
				 * sb.append(
				 * "select * from goal_kras k join goal_details g on k.goal_id=g.goal_id and g.emp_ids like '%"
				 * + getEmpID() + "%' " +
				 * "and g.goal_type=4 and measure_type !=''  order by k.goal_id"
				 * );
				 */
				sb.append("select * from goal_kras k join goal_details g on k.goal_id=g.goal_id ");
				// sb.append(" and g.emp_ids like '%"+ getEmpID()+ "%' ");
				sb.append(" and g.goal_type=4 and measure_type !='' order by k.goal_id");
				pst = con.prepareStatement(sb.toString());
				rs = pst.executeQuery();
				while (rs.next()) {
					List<List<String>> outerList = hmKRA.get(rs.getString("goal_id"));
					if (outerList == null)
						outerList = new ArrayList<List<String>>();
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("goal_kra_id"));
					innerList.add(rs.getString("goal_id"));
					innerList.add(rs.getString("entry_date"));
					innerList.add(rs.getString("effective_date"));
					innerList.add(rs.getString("is_approved"));
					innerList.add(rs.getString("approved_by"));
					innerList.add(rs.getString("kra_order"));
					innerList.add(rs.getString("kra_description"));
					innerList.add(rs.getString("goal_type"));
					innerList.add(rs.getString("weightage"));

					hmGoalOrientation.put(rs.getString("goal_id"), rs.getString("orientation_id"));

					hmGoalTitle.put(rs.getString("goal_id"), rs.getString("goal_title"));
					String measures = "";
					if (rs.getString("measure_type") != null) {
						if (rs.getString("measure_type").equals("$")) {
							measures = rs.getString("measure_currency_value");
						} else if (rs.getString("measure_type").equals("Effort")) {
							measures = rs.getString("measure_effort_days") + " Days and " + rs.getString("measure_effort_hrs") + " Hrs.";
						}
					}
					hmMesures.put(rs.getString("goal_id"), measures);
					hmMesuresType.put(rs.getString("goal_id"), rs.getString("measure_type"));

					outerList.add(innerList);
					hmKRA.put(rs.getString("goal_id"), outerList);
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("hmKRA", hmKRA);
			// System.out.println("hmKRA"+hmKRA);
			// System.out.println("hmMesuresType"+hmMesuresType);

			request.setAttribute("hmMesures", hmMesures);
			request.setAttribute("hmMesuresType", hmMesuresType);
			request.setAttribute("hmGoalOrientation", hmGoalOrientation);
			request.setAttribute("hmGoalTitle", hmGoalTitle);

			request.setAttribute("orientationMemberMp", orientationMemberMp);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public boolean swapLevelId(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		boolean flag = true;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select main_level_id from appraisal_main_level_details where appraisal_id=? and main_level_id>? order by main_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getCurrentLevel()));
			rs = pst.executeQuery();
//			 System.out.println("pst ====> "+pst);
			while (rs.next()) {
				flag = false;
				if(getBtnSave() != null) {
					setCurrentLevel(getCurrentLevel());
				} else {
					setCurrentLevel(rs.getString("main_level_id"));
				}
				break;
				// request.setAttribute( rs.getString("appraisal_level_id"),
				// rs.getString("appraisal_level_id"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			
	//===start parvez date: 18-07-2022===		
			if(getDataType()!=null && getDataType().equals("Reviewer Feedback") && uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) && uF.parseToInt(getUserType())==13){
				flag = true;
			}
	//===end parvez date: 18-07-2022===		
			
			if(getBtnSave() != null) {
				flag = false;
				setCurrentLevel(getCurrentLevel());
			}
			
			// System.out.println("flag ====> "+flag);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}

	public void getCurrentLevelAnswer(UtilityFunctions uF, int level) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_question_answer where section_id=? and appraisal_id=? and emp_id=? and " +
				"user_id=? and user_type_id=? and appraisal_freq_id=? and reviewer_or_appraiser=?");
			pst.setInt(1, level);
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getEmpID()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setInt(5, uF.parseToInt(getUserType()));
			pst.setInt(6, uF.parseToInt(getAppFreqId()));
			if(getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
				pst.setInt(7, 1);
			} else {
				pst.setInt(7, 0);
			}
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> questionanswerMp = new HashMap<String, Map<String, String>>();

			while (rs.next()) {
				
				Map<String, String> innerMp = new HashMap<String, String>();
				innerMp.put("ANSWER", rs.getString("answer"));
				innerMp.put("REMARK", rs.getString("remark"));
				innerMp.put("MARKS", rs.getString("marks"));
				innerMp.put("APP_QUE_ANS_ID", rs.getString("appraisal_question_answer_id"));
				innerMp.put("WEIGHTAGE", rs.getString("weightage"));
				innerMp.put("ANSWERCOMMENT", rs.getString("answers_comment"));
				innerMp.put("LEVEL_COMMENT", rs.getString("section_comment"));
				if (uF.parseToInt(rs.getString("scorecard_id")) != 0) {
					questionanswerMp.put(rs.getString("scorecard_id") + "question" + rs.getString("question_id"), innerMp);
				} else if (uF.parseToInt(rs.getString("other_id")) != 0) {
					questionanswerMp.put(rs.getString("other_id") + "question" + rs.getString("question_id"), innerMp);
				} else {
					questionanswerMp.put("question" + rs.getString("question_id"), innerMp);
				}
				/*if (uF.parseToInt(rs.getString("scorecard_id")) != 0) {
					questionanswerMp.put(rs.getString("scorecard_id") + "question" + rs.getString("question_id"), innerMp);
				} else {
					questionanswerMp.put(rs.getString("other_id") + "question" + rs.getString("question_id"), innerMp);
				}*/
			}
			rs.close();
			pst.close();

//			System.out.println("questionanswerMp ===>> " + questionanswerMp);
			request.setAttribute("questionanswerMp", questionanswerMp);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void getLevelStatus(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select appraisal_level_id from appraisal_question_answer where appraisal_id=? and emp_id=? and user_id=? " +
				"and user_type_id=? and appraisal_freq_id=? and reviewer_or_appraiser=? group by appraisal_level_id order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			if(getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
				pst.setInt(6, 1);
			} else {
				pst.setInt(6, 0);
			}
			rs = pst.executeQuery();
			Map<String, String> innerMp = new HashMap<String, String>();
			while (rs.next()) {
				innerMp.put(rs.getString("appraisal_level_id"), rs.getString("appraisal_level_id"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select appraisal_level_id from kra_rating_details where appraisal_id=? and emp_id=? and added_by=? and user_type_id=? and appraisal_freq_id = ?  group by appraisal_level_id order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				innerMp.put(rs.getString("appraisal_level_id"), rs.getString("appraisal_level_id"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select appraisal_level_id from target_details where appraisal_id=? and emp_id=? and added_by=? and user_type_id=? and appraisal_freq_id = ? group by appraisal_level_id order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				innerMp.put(rs.getString("appraisal_level_id"), rs.getString("appraisal_level_id"));
			}
			rs.close();
			pst.close();

			request.setAttribute("LEVEL_STATUS", innerMp);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void getMainLevelStep(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			List<String> mainLevelList = new ArrayList<String>();
			// mainLevelList.add("0");
			/*pst = con.prepareStatement("select main_level_id from appraisal_main_level_details where appraisal_id=? order by main_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				mainLevelList.add(rs.getString("main_level_id"));
			}
			rs.close();
			pst.close();*/
			
			if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) && getRole()!=null 
					&& (getRole().equalsIgnoreCase("Peer") || getRole().equalsIgnoreCase("Other Peer") || getRole().equalsIgnoreCase("HOD"))){
				pst = con.prepareStatement("select main_level_id from appraisal_main_level_details where appraisal_id=? and main_level_id in (select main_level_id from appraisal_level_details where appraisal_id=? and appraisal_system=2)" +
						" order by main_level_id");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.setInt(2, uF.parseToInt(getId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					mainLevelList.add(rs.getString("main_level_id"));
				}
				rs.close();
				pst.close();
			} else{
				pst = con.prepareStatement("select main_level_id from appraisal_main_level_details where appraisal_id=? order by main_level_id");
				pst.setInt(1, uF.parseToInt(getId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					mainLevelList.add(rs.getString("main_level_id"));
				}
				rs.close();
				pst.close();
			}

//			System.out.println("SAp/923---mainLevelList="+mainLevelList);
			request.setAttribute("mainLevelList", mainLevelList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public void getLevelStep(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
		//===start parvez date: 14-03-2023===	
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
			// List<String> levelList = new ArrayList<String>();
			Map<String, Map<String, String>> hmSubsection = new HashMap<String, Map<String, String>>();
			/*pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id=? order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String> hmSubsectionDetails = new HashMap<String, String>();
				hmSubsectionDetails.put("LEVEL_NAME", rs.getString("level_title"));
				hmSubsectionDetails.put("LEVEL_SDESC", rs.getString("short_description"));
				hmSubsectionDetails.put("LEVEL_LDESC", rs.getString("long_description"));
				hmSubsectionDetails.put("LEVEL_APPSYSTEM", rs.getString("appraisal_system"));
				hmSubsectionDetails.put("APP_LEVEL_ID", rs.getString("appraisal_level_id"));
				// request.setAttribute( rs.getString("appraisal_level_id"),
				// rs.getString("appraisal_level_id"));
				hmSubsection.put(rs.getString("appraisal_level_id"), hmSubsectionDetails);
			}
			rs.close();
			pst.close();*/
			
			if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) && getRole()!=null 
					&& (getRole().equalsIgnoreCase("Peer") || getRole().equalsIgnoreCase("Other Peer") || getRole().equalsIgnoreCase("HOD"))){
				pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id=? and appraisal_system=2 order by appraisal_level_id");
				pst.setInt(1, uF.parseToInt(getId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					Map<String, String> hmSubsectionDetails = new HashMap<String, String>();
					hmSubsectionDetails.put("LEVEL_NAME", rs.getString("level_title"));
					hmSubsectionDetails.put("LEVEL_SDESC", rs.getString("short_description"));
					hmSubsectionDetails.put("LEVEL_LDESC", rs.getString("long_description"));
					hmSubsectionDetails.put("LEVEL_APPSYSTEM", rs.getString("appraisal_system"));
					hmSubsectionDetails.put("APP_LEVEL_ID", rs.getString("appraisal_level_id"));
					// request.setAttribute( rs.getString("appraisal_level_id"),
					// rs.getString("appraisal_level_id"));
					hmSubsection.put(rs.getString("appraisal_level_id"), hmSubsectionDetails);
				}
				rs.close();
				pst.close();
			} else{
				pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id=? order by appraisal_level_id");
				pst.setInt(1, uF.parseToInt(getId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					Map<String, String> hmSubsectionDetails = new HashMap<String, String>();
					hmSubsectionDetails.put("LEVEL_NAME", rs.getString("level_title"));
					hmSubsectionDetails.put("LEVEL_SDESC", rs.getString("short_description"));
					hmSubsectionDetails.put("LEVEL_LDESC", rs.getString("long_description"));
					hmSubsectionDetails.put("LEVEL_APPSYSTEM", rs.getString("appraisal_system"));
					hmSubsectionDetails.put("APP_LEVEL_ID", rs.getString("appraisal_level_id"));
					// request.setAttribute( rs.getString("appraisal_level_id"),
					// rs.getString("appraisal_level_id"));
					hmSubsection.put(rs.getString("appraisal_level_id"), hmSubsectionDetails);
				}
				rs.close();
				pst.close();
			}
		//===end parvez date: 14-03-2023===	

			request.setAttribute("hmSubsection", hmSubsection);
		
		//===start parvez date: 27-02-2023===	
			pst = con.prepareStatement("select * from appraisal_other_question_type_details where appraisal_id=? order by level_id");
			pst.setInt(1, uF.parseToInt(getId()));
//			System.out.println("SAp/941---pst===>"+pst);
			rs = pst.executeQuery();
			Map<String, String> othrQueType = new HashMap<String, String>();
			while (rs.next()) {
				othrQueType.put(rs.getString("level_id"), rs.getString("other_question_type"));
			}
			rs.close();
			pst.close();
			request.setAttribute("othrQueType", othrQueType);
		//===end parvez date: 27-02-2023===	
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public boolean getPreviousLevelData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		boolean flag = false;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			
			List<String> alKRAIds = (List<String>)request.getAttribute("alKRAIds");
			List<String> alGoarTargetIds = (List<String>)request.getAttribute("alGoarTargetIds");
			
			// StringBuilder sb = new StringBuilder("select * from appraisal_question_details where appraisal_id=? and appraisal_level_id=?");
			StringBuilder sb = new StringBuilder("select * from appraisal_question_details where appraisal_id=? and appraisal_level_id in " +
				"(select appraisal_level_id from appraisal_level_details where main_level_id=?) order by appraisal_question_details_id");
			/*if(getRole() == null || !getRole().equalsIgnoreCase("Reviewer")) {
				if (uF.parseToInt(getUserType()) == 2) {
					sb.append(" and manager !=0");
				} else if (uF.parseToInt(getUserType()) == 7) {
					sb.append(" and hr !=0");
				} else if (uF.parseToInt(getUserType()) == 3) {
					sb.append(" and self!=0 ");
				} else if (uF.parseToInt(getUserType()) == 4) {
					sb.append(" and peer !=0 ");
				} else if (uF.parseToInt(getUserType()) == 5) {
					sb.append(" and ceo !=0 ");
				} else if (uF.parseToInt(getUserType()) == 13) {
					sb.append(" and hod !=0 ");
				}
			}*/
			pst = con.prepareStatement(sb.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getCurrentLevel()));
//			System.out.println("StAp/1069---pst===>"+pst);
			rs = pst.executeQuery();
			Map<String, List<List<String>>> hmLevelQuestion = new HashMap<String, List<List<String>>>();
			// List<List<String>> outerList = new ArrayList<List<String>>();
			while (rs.next()) {
				if(rs.getInt("app_system_type")==4 && alKRAIds!=null && rs.getString("kra_id")!=null && !alKRAIds.contains(rs.getString("kra_id"))) {
					continue;
				}
				if((rs.getInt("app_system_type")==3 || rs.getInt("app_system_type")==5) && alGoarTargetIds!=null && rs.getString("goal_kra_target_id")!=null && !alGoarTargetIds.contains(rs.getString("goal_kra_target_id"))) {
					continue;
				}
			//===start parvez date: 14-03-2023===	
				/*if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) && getRole()!=null 
						&& (getRole().equalsIgnoreCase("Peer") || getRole().equalsIgnoreCase("Other Peer") || getRole().equalsIgnoreCase("HOD")) && rs.getInt("app_system_type")!=2){
					continue;
				}*/
			//===end parvez date: 14-03-2023===
				List<List<String>> outerList = hmLevelQuestion.get(rs.getString("appraisal_level_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add("");
				innerList.add(rs.getString("appraisal_id"));
				innerList.add(rs.getString("plan_id"));
				innerList.add("");
				innerList.add("");
				innerList.add("");
				innerList.add("");
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("attribute_id"));
				innerList.add(rs.getString("other_short_description"));
				innerList.add(rs.getString("appraisal_level_id"));
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(rs.getString("other_id"));
				innerList.add("");
				innerList.add("");
				outerList.add(innerList);
				hmLevelQuestion.put(rs.getString("appraisal_level_id"), outerList);
			}
			rs.close();
			pst.close();

			request.setAttribute("hmLevelQuestion", hmLevelQuestion);
//			 System.out.println("StAp/1115--getPreviousLevelData hmLevelQuestion===>"+hmLevelQuestion);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}

	private void insertMarks(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		List<String> appraisalList = (List<String>) request.getAttribute("appraisalList");
		List<String> alUserTypeForFeedback = new ArrayList<String>();
		if(appraisalList !=null && appraisalList.get(6) != null) {
			alUserTypeForFeedback = Arrays.asList(appraisalList.get(6).split(","));
		}
		Map<String, List<List<String>>> hmLevelQuestion = (Map<String, List<List<String>>>) request.getAttribute("hmLevelQuestion");
		// List<List<String>> outerList = (List<List<String>>)
		// request.getAttribute("questionList");
		
		Map<String, List<String>> hmQuestion = (Map<String, List<String>>) request.getAttribute("hmQuestion");
		
	//===start parvez date: 09-03-2023===
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
	//===end parvez date: 09-03-2023===	
		
		try {
			pst = con.prepareStatement("delete from appraisal_question_answer where emp_id=? and appraisal_id=? and user_id=? and user_type_id=? " +
				"and section_id=? and appraisal_freq_id=? and reviewer_or_appraiser=?");
			pst.setInt(1, uF.parseToInt(empID));
			pst.setInt(2, uF.parseToInt(id));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(currentLevel));
			pst.setInt(6, uF.parseToInt(getAppFreqId()));
			if(getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
				pst.setInt(7, 1);
			} else {
				pst.setInt(7, 0);
			}
//			System.out.println("pst1==>"+pst);
			pst.execute();
			pst.close();

			if(!alUserTypeForFeedback.contains(getUserType())) {
			Set keys = hmLevelQuestion.keySet();
			Iterator it = keys.iterator();
				while (it.hasNext()) {
					
//					File levelCommentFile = request.getParameter("levelcommentFile"+currentLevel);
//					System.out.println("getLevelcommentFile() ===>> " + getLevelcommentFile());
					String fileName = null;
					if(getLevelcommentFile() != null) {
						MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request; 
						String strOrgId = CF.getEmpOrgId(con, uF, strSessionEmpId);
						String mainPathWithOrg = CF.getProjectDocumentFolder()+strOrgId;
						File fileOrg = new File(mainPathWithOrg);
						if (!fileOrg.exists()) {
							if (fileOrg.mkdir()) {
//								System.out.println("Org Directory is created!");
							}
						}
						
						String mainReivewPath = mainPathWithOrg+"/Reviews";
						File file = new File(mainReivewPath);
						if (!file.exists()) {
							if (file.mkdir()) {
//								System.out.println("Reviews Directory is created!");
							}
						}
						
						String mainPathWithReviewId = mainReivewPath+"/"+id;
						File fileReviewId = new File(mainPathWithReviewId);
						if (!fileReviewId.exists()) {
							if (fileReviewId.mkdir()) {
//								System.out.println("Review Id is created!");
							}
						}
						
						String mainPathWithReviewFreqId = mainPathWithReviewId+"/"+getAppFreqId();
						File fileReviewFreqId = new File(mainPathWithReviewFreqId);
						if (!fileReviewFreqId.exists()) {
							if (fileReviewFreqId.mkdir()) {
//								System.out.println("Review Freq Id is created!");
							}
						}
						
						String mainPathWithSessionEmpId = mainPathWithReviewFreqId+"/"+strSessionEmpId;
						File fileSessionEmpId = new File(mainPathWithSessionEmpId);
						if (!fileSessionEmpId.exists()) {
							if (fileSessionEmpId.mkdir()) {
//								System.out.println("Session Emp Id is created!");
							}
						}
						
						String mainPathWithUserTypeId = mainPathWithSessionEmpId+"/"+getUserType();
						File fileUserTypeId = new File(mainPathWithUserTypeId);
						if (!fileUserTypeId.exists()) {
							if (fileUserTypeId.mkdir()) {
//								System.out.println("User Type Id is created!");
							}
						}
						
						String mainPathWithCurrentLevel = mainPathWithUserTypeId+"/"+currentLevel;
						File fileCurrentLevel = new File(mainPathWithCurrentLevel);
						if (!fileCurrentLevel.exists()) {
							if (fileCurrentLevel.mkdir()) {
//								System.out.println("Current Level Id is created!");
							}
						}
					
						double lengthBytes =  getLevelcommentFile().length();
						boolean isFileExist = false;
						
						String extenstion = FilenameUtils.getExtension(getLevelcommentFileFileName());	
						String strFileName = FilenameUtils.getBaseName(getLevelcommentFileFileName());
						strFileName = strFileName+"v1."+extenstion;
						
						File f = new File(mainPathWithCurrentLevel+"/"+strFileName);
						if(f.isFile()) {
//						    System.out.println("isFile");
						    if(f.exists()){
								isFileExist = true;
//							    System.out.println("exists");
							} else {
//							    System.out.println("exists fail");
							}   
						} else {
//						    System.out.println("isFile fail");
						}
						
						if(lengthBytes > 0 && !isFileExist) {
							if(CF.getProjectDocumentFolder()==null) { 
								fileName = uF.uploadProjectDocuments(request, DOCUMENT_LOCATION, getLevelcommentFile(), strFileName, strFileName, CF);
							} else {
								fileName = uF.uploadProjectDocuments(request, mainPathWithCurrentLevel, getLevelcommentFile(), strFileName, strFileName, CF);
							}
//							uploadReviewDocuments(con, getLevelcommentFile(), strFileName, mainPathWithCurrentLevel, id, getAppFreqId());
						}
						
					}
					
					String key = (String) it.next();
//					 System.out.println("key =====>"+ key);
					String levelComment = request.getParameter("levelcomment"+currentLevel);
					
					List<List<String>> outerList = hmLevelQuestion.get(key);
//					 System.out.println("outerList =====>"+outerList);
					for (int i = 0; outerList != null && i < outerList.size(); i++) {
						List<String> innerlist = (List<String>) outerList.get(i);
						// System.out.println("innerlist.get(0)=====>"+innerlist.get(0));
						// System.out.println("innerlist.get(1)=====>"+innerlist.get(1));
						List<String> questioninnerList = hmQuestion.get(innerlist.get(1));
						// System.out.println("questioninnerList =====>"+ questioninnerList);
						String weightage = innerlist.get(2);
						String appraisal_level_id = innerlist.get(13);
						String scorecard_id = innerlist.get(14);
						String attribute = innerlist.get(11);
						String givenAnswer = null;
						String other_id = innerlist.get(15);
						double marks = 0;
						String remark = null;
						String ansComment = null;
						// String sectionId = request.getParameter("hideSectionId");
//						 System.out.println("questioninnerList.get(8) =====>"+ questioninnerList.get(8));
						// System.out.println("questioninnerList.get(9) =====>"+ questioninnerList.get(9));
						// System.out.println("questioninnerList.get(6) =====>"+ questioninnerList.get(6));
	//					System.out.println("questioninnerList.get(9) =====>"+questioninnerList.get(9)+"==>innerlist.get(1)==>"+innerlist.get(1));
						if (uF.parseToInt(questioninnerList.get(8)) == 1) {
							ansComment = request.getParameter("anscomment" + innerlist.get(1) + "_" + questioninnerList.get(9));
							String[] correct = request.getParameterValues("correct" + innerlist.get(1) + "_" + questioninnerList.get(9));
//							remark = request.getParameter("" + innerlist.get(1) + "_" + questioninnerList.get(9));
							remark = request.getParameter("multiplewithremark" + innerlist.get(1) + "_" + questioninnerList.get(9));//Created by Dattatray Date : 29-June-2021 Note :multiplewithremark parameter added
							String correctanswer = questioninnerList.get(6);
							for (int k = 0; correct != null && k < correct.length; k++) {
								if (k == 0) {
									givenAnswer = correct[k] + ",";
								} else {
									givenAnswer += correct[k] + ",";
								}
							}
							if (correctanswer != null && givenAnswer != null && givenAnswer.equals(correctanswer)) {
								marks = uF.parseToDouble(weightage);
							}
	
						} else if (uF.parseToInt(questioninnerList.get(8)) == 2) {
							ansComment = request.getParameter("anscomment" + innerlist.get(1) + "_" + questioninnerList.get(9));
							String[] correct = request.getParameterValues("correct" + innerlist.get(1) + "_" + questioninnerList.get(9));
							for (int k = 0; correct != null && k < correct.length; k++) {
								if (k == 0) {
									givenAnswer = correct[k] + ",";
								} else {
									givenAnswer += correct[k] + ",";
								}
							}
							String correctanswer = questioninnerList.get(6);
	
							if (correctanswer != null && givenAnswer != null && givenAnswer.equals(correctanswer)) {
								marks = uF.parseToDouble(weightage);
							} else {
								marks = 0;
							}
	
						} else if (uF.parseToInt(questioninnerList.get(8)) == 3) {
							ansComment = request.getParameter("anscomment" + innerlist.get(1) + "_" + questioninnerList.get(9));
							// System.out.println("MArks OF ANSTYPE 3 ===> " +request.getParameter("marks" + innerlist.get(1)+"_"+questioninnerList.get(9)));
							// System.out.println("ANSTYPE 3 ID ===> " + "marks" +innerlist.get(1)+"_"+questioninnerList.get(9));
							marks = uF.parseToDouble(request.getParameter("marks" + innerlist.get(1) + "_" + questioninnerList.get(9)));
	
						} else if (uF.parseToInt(questioninnerList.get(8)) == 4) {
							ansComment = request.getParameter("anscomment" + innerlist.get(1) + "_" + questioninnerList.get(9));
							givenAnswer = request.getParameter("" + innerlist.get(1) + "_" + questioninnerList.get(9));
							marks = (uF.parseToDouble(givenAnswer) * uF.parseToDouble(weightage)) / 100;
	
						} else if (uF.parseToInt(questioninnerList.get(8)) == 5) {
							ansComment = request.getParameter("anscomment" + innerlist.get(1) + "_" + questioninnerList.get(9));
							givenAnswer = request.getParameter("" + innerlist.get(1) + "_" + questioninnerList.get(9)) + ",";
							String answer = questioninnerList.get(6);
							if (givenAnswer != null && answer != null && givenAnswer.equals(answer)) {
								marks = uF.parseToDouble(weightage);
							}
	
						} else if (uF.parseToInt(questioninnerList.get(8)) == 6) {
							ansComment = request.getParameter("anscomment" + innerlist.get(1) + "_" + questioninnerList.get(9));
							givenAnswer = request.getParameter("" + innerlist.get(1) + "_" + questioninnerList.get(9)) + ",";
							String answer = questioninnerList.get(6);
							if (givenAnswer != null && answer != null && givenAnswer.equals(answer)) {
								marks = uF.parseToDouble(weightage);
							}
	
						} else if (uF.parseToInt(questioninnerList.get(8)) == 7) {
							ansComment = request.getParameter("anscomment" + innerlist.get(1) + "_" + questioninnerList.get(9));
							givenAnswer = request.getParameter("" + innerlist.get(1) + "_" + questioninnerList.get(9));
							// System.out.println("MArks OF ANSTYPE 7 ===> " +
							// request.getParameter("marks" +
							// innerlist.get(1)+"_"+questioninnerList.get(9)));
							// System.out.println("ANSTYPE 7 ID ===> " + "marks" +
							// innerlist.get(1)+"_"+questioninnerList.get(9));
							marks = uF.parseToDouble(request.getParameter("marks" + innerlist.get(1) + "_" + questioninnerList.get(9)));
							weightage = request.getParameter("outofmarks" + innerlist.get(1) + "_" + questioninnerList.get(9));
	
						} else if (uF.parseToInt(questioninnerList.get(8)) == 8) {
							ansComment = request.getParameter("anscomment" + innerlist.get(1) + "_" + questioninnerList.get(9));
							givenAnswer = request.getParameter("correct" + innerlist.get(1) + "_" + questioninnerList.get(9)) + ",";
							String correctanswer = questioninnerList.get(6);
							if (givenAnswer != null && correctanswer != null && givenAnswer.equals(correctanswer)) {
								marks = uF.parseToDouble(weightage);
							}
	
						} else if (uF.parseToInt(questioninnerList.get(8)) == 9) {
							ansComment = request.getParameter("anscomment" + innerlist.get(1) + "_" + questioninnerList.get(9));
							String[] correct = request.getParameterValues("correct" + innerlist.get(1) + "_" + questioninnerList.get(9));
							for (int k = 0; correct != null && k < correct.length; k++) {
								if (k == 0) {
									givenAnswer = correct[k] + ",";
								} else {
									givenAnswer += correct[k] + ",";
								}
							}
							String correctanswer = questioninnerList.get(6);
							// System.out.println("correctanswer ===> " +
							// correctanswer +" givenAnswer ===> "+givenAnswer);
							if (correctanswer != null && givenAnswer != null && givenAnswer.equals(correctanswer)) {
								// System.out.println("in if correctanswer ===> " +
								// correctanswer +" givenAnswer ===> "+givenAnswer);
								marks = uF.parseToDouble(weightage);
							}
						} else if (uF.parseToInt(questioninnerList.get(8)) == 10) {
							ansComment = request.getParameter("anscomment" + innerlist.get(1) + "_" + questioninnerList.get(9));
							// System.out.println("MArks OF ANSTYPE 10 ===> " +
							// request.getParameter("marks" +
							// innerlist.get(1)+"_"+questioninnerList.get(9)));
							// System.out.println("ANSTYPE 10 ID ===> " + "marks" +
							// innerlist.get(1)+"_"+questioninnerList.get(9));
							marks = uF.parseToDouble(request.getParameter("marks" + innerlist.get(1) + "_" + questioninnerList.get(9)));
							String a = request.getParameter("a" + innerlist.get(1) + "_" + questioninnerList.get(9));
							String b = request.getParameter("b" + innerlist.get(1) + "_" + questioninnerList.get(9));
							String c = request.getParameter("c" + innerlist.get(1) + "_" + questioninnerList.get(9));
							String d = request.getParameter("d" + innerlist.get(1) + "_" + questioninnerList.get(9));
							givenAnswer = uF.showData(a, " ") + ":_:" + uF.showData(b, "") + ":_:" + uF.showData(c, "") + ":_:" + uF.showData(d, " ");
						} else if (uF.parseToInt(questioninnerList.get(8)) == 11) {
							ansComment = request.getParameter("anscomment" + innerlist.get(1) + "_" + questioninnerList.get(9));
							String rating = request.getParameter("gradewithrating" + innerlist.get(1) + "_" + questioninnerList.get(9));
						//===start parvez date: 09-03-2023===
//							marks = uF.parseToDouble(rating) * uF.parseToDouble(weightage) / 5;
							if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
								marks = uF.parseToDouble(rating) * uF.parseToDouble(weightage) / 10;
							} else{
								marks = uF.parseToDouble(rating) * uF.parseToDouble(weightage) / 5;
							}
						//===end parvez date: 09-03-2023===	
//							System.out.println("SA/1323--rating=="+rating+"---marks=="+marks+"--weightage=="+weightage);
						} else if (uF.parseToInt(questioninnerList.get(8)) == 12) {
							ansComment = request.getParameter("anscomment" + innerlist.get(1) + "_" + questioninnerList.get(9));
							givenAnswer = request.getParameter("" + innerlist.get(1) + "_" + questioninnerList.get(9));
						} else if (uF.parseToInt(questioninnerList.get(8)) == 13) {
							ansComment = request.getParameter("anscomment" + innerlist.get(1) + "_" + questioninnerList.get(9));
							String strAns = request.getParameter("correct" + innerlist.get(1) + "_" + questioninnerList.get(9));
							if(strAns != null && !strAns.equals("null")) {
								givenAnswer = request.getParameter("correct" + innerlist.get(1) + "_" + questioninnerList.get(9)) + ",";
							}
							String gvnAnswer = request.getParameter("correct" + innerlist.get(1) + "_" + questioninnerList.get(9));
	//						String correctanswer = questioninnerList.get(6);
							String correctAnsVal = null;
							if(gvnAnswer != null && gvnAnswer.trim().equalsIgnoreCase("a")) {
								correctAnsVal = questioninnerList.get(11);
							} else if(gvnAnswer != null && gvnAnswer.trim().equalsIgnoreCase("b")) {
								correctAnsVal = questioninnerList.get(12);
							} else if(gvnAnswer != null && gvnAnswer.trim().equalsIgnoreCase("c")) {
								correctAnsVal = questioninnerList.get(13);
							} else if(gvnAnswer != null && gvnAnswer.trim().equalsIgnoreCase("d")) {
								correctAnsVal = questioninnerList.get(14);
							} else if(gvnAnswer != null && gvnAnswer.trim().equalsIgnoreCase("e")) {
								correctAnsVal = questioninnerList.get(15);
							}
	//						if (givenAnswer != null && correctanswer != null && givenAnswer.equals(correctanswer)) {
								marks = uF.parseToDouble(correctAnsVal) * uF.parseToDouble(weightage) / 5;
	//						}
						}
						
//						 System.out.println("givenAnswer=====>"+givenAnswer+"==ansComment==>"+ansComment+"==marks==>"+marks);
//						System.out.println("getBtnSave() ===>> " + getBtnSave()); 
						//if(getBtnSave() == null || (getBtnSave() != null && (givenAnswer != null && !givenAnswer.equals("") && !givenAnswer.equals("null")) || (givenAnswer==null && marks>0))) {
						// Created by Dattatray : Note - !givenAnswer.contains("null") condition checked.
						if(getBtnSave() == null || (getBtnSave() != null && (givenAnswer != null && !givenAnswer.equals("") && !givenAnswer.equals("null") && !givenAnswer.contains("null") && !givenAnswer.contains(" :_::_::_: ")) || (givenAnswer==null && marks>0))) {
							pst = con.prepareStatement("insert into appraisal_question_answer(emp_id,answer,appraisal_id,question_id,user_id,user_type_id," +
								"attempted_on,weightage,marks,appraisal_level_id,scorecard_id,appraisal_attribute,remark,other_id,appraisal_question_details_id," +
								"section_id,answers_comment,appraisal_freq_id,reviewer_or_appraiser,section_comment,section_comment_file" +
								") values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
							pst.setInt(1, uF.parseToInt(empID));
							pst.setString(2, givenAnswer);
							pst.setInt(3, uF.parseToInt(id));
							pst.setInt(4, uF.parseToInt(innerlist.get(1)));
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setInt(6, uF.parseToInt(getUserType()));
							pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setDouble(8, uF.parseToDouble(weightage));
							pst.setDouble(9, marks);
							pst.setInt(10, uF.parseToInt(appraisal_level_id));
							pst.setInt(11, uF.parseToInt(scorecard_id));
							pst.setInt(12, uF.parseToInt(attribute));
							pst.setString(13, remark);
							pst.setInt(14, uF.parseToInt(other_id));
							pst.setInt(15, uF.parseToInt(innerlist.get(0)));
							pst.setInt(16, uF.parseToInt(currentLevel));
							pst.setString(17, ansComment);
							pst.setInt(18, uF.parseToInt(getAppFreqId()));
							if(getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
								pst.setInt(19, 1);
							} else {
								pst.setInt(19, 0);
							}
							pst.setString(20, uF.showData(levelComment, "-"));
							pst.setString(21, fileName);
//							System.out.println("SA/1351--pst2 =====>"+ pst);
							pst.execute();
							pst.close();
						}
					}
				}
				
				
			} else {
				
				String levelComment = request.getParameter("levelcomment"+currentLevel);
				String levelCommentFile = request.getParameter("levelcommentFile"+currentLevel);
				pst = con.prepareStatement("insert into appraisal_question_answer(emp_id,appraisal_id,user_id,user_type_id,attempted_on,section_id," +
					"appraisal_freq_id,reviewer_or_appraiser,section_comment) values(?,?,?,?, ?,?,?,?, ?)");
//				System.out.println("SA/1365---pst="+pst);
				pst.setInt(1, uF.parseToInt(empID));
				pst.setInt(2, uF.parseToInt(id));
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
				pst.setInt(4, uF.parseToInt(getUserType()));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(6, uF.parseToInt(currentLevel));
				pst.setInt(7, uF.parseToInt(getAppFreqId()));
				if(getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
					pst.setInt(8, 1);
				} else {
					pst.setInt(8, 0);
				}
				pst.setString(9, uF.showData(levelComment, "-"));
//				System.out.println("pst2 =====> "+ pst);
				pst.execute();
				pst.close();
			}
			
			
			if(uF.parseToBoolean(getIsAreasOfStrengthAndImprovement())) {
				pst = con.prepareStatement("delete from reviewee_strength_improvements where emp_id=? and review_id=? and review_freq_id=? and user_id=? and user_type_id=?");
				pst.setInt(1, uF.parseToInt(empID));
				pst.setInt(2, uF.parseToInt(id));
				pst.setInt(3, uF.parseToInt(getAppFreqId()));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setInt(5, uF.parseToInt(getUserType()));
				pst.executeUpdate();
				pst.close();
			
				pst = con.prepareStatement("insert into reviewee_strength_improvements(emp_id,review_id,review_freq_id,user_id,user_type_id,entry_date" +
					",areas_of_strength,areas_of_improvement) values(?,?,?,?, ?,?,?,?)");
				pst.setInt(1, uF.parseToInt(empID));
				pst.setInt(2, uF.parseToInt(id));
				pst.setInt(3, uF.parseToInt(getAppFreqId()));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setInt(5, uF.parseToInt(getUserType()));
				pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.setString(7, getAreasOfStrength());
				pst.setString(8, getAreasOfImprovement());
				pst.executeUpdate();
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void getAppraisalDetail(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		try {
			List<String> appraisalList = new ArrayList<String>();
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();

			while (rs.next()) {
				appraisalList.add(uF.showData(rs.getString("appraisal_details_id"), ""));	//0
				appraisalList.add(uF.showData(rs.getString("appraisal_name"), ""));		//1
				appraisalList.add(uF.showData(rs.getString("appraisal_description"), ""));		//2
				appraisalList.add(uF.showData(rs.getString("oriented_type"), ""));		//3
				appraisalList.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));		//4
				appraisalList.add(uF.showData(rs.getString("appraisal_instruction"), ""));		//5
				appraisalList.add(uF.showData(rs.getString("user_types_for_feedback"), "")); //6
				
			}
			rs.close();
			pst.close();

			request.setAttribute("appraisalList", appraisalList);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getEmployyDetailsList(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		con = db.makeConnection(con);
		Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
		// Map<String, String> hmEmpProbationEnd =
		// CF.getEmpProbationEndDateMap(con, uF);
		Map<String, String> hmDepartmentMap = CF.getDeptMap(con);
		Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
		Map<String, String> hmLevelMap = CF.getLevelMap(con);
		Map<String, String> hmOrientationMember = getOrientationMember(con);

		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
		request.setAttribute("hmFeatureStatus", hmFeatureStatus);			//added by parvez date: 07-07-2022
		boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
		try {

			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and emp_per_id=?");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			rs = pst.executeQuery();
			List<String> empList = new ArrayList<String>();
			Map<String, String> hmEmpDetails = new HashMap<String, String>();

			while (rs.next()) {
				// empList.add(rs.getString("emp_per_id"));
				// empList.add(rs.getString("emp_fname") + " " +
				// rs.getString("emp_lname") + " [" + rs.getString("empcode") +
				// "]");
				// empList.add(uF.showData(mpdepart.get(hmEmpDepartment.get(rs.getString("emp_per_id"))),
				// ""));
				// empList.add(uF.showData(hmEmpCodeDesig.get(rs.getString("emp_per_id")),
				// ""));
				// empList.add(uF.showData(hmEmpJoiningDate.get(rs.getString("emp_per_id")),
				// ""));
				// empList.add(uF.showData(hmEmpProbationEnd.get(rs.getString("emp_per_id")),
				// ""));

				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				hmEmpDetails.put("EMP_NAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				hmEmpDetails.put("EMP_CODE", rs.getString("empcode"));
				hmEmpDetails.put("EMP_ID", rs.getString("emp_per_id"));
				hmEmpDetails.put("DESIGNATION", uF.showData(hmEmpCodeDesig.get(rs.getString("emp_per_id")), ""));
				hmEmpDetails.put("LEVEL", uF.showData(hmLevelMap.get(hmEmpLevelMap.get(rs.getString("emp_per_id"))), ""));

				hmEmpDetails.put("DEAPRTMENT", uF.showData(hmDepartmentMap.get(rs.getString("depart_id")), ""));
				hmEmpDetails.put("JOINING_DATE", uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));

			}
			rs.close();
			pst.close();

			if(getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
				hmEmpDetails.put("ORIENTATION", "Reviewer");
			} else {
				hmEmpDetails.put("ORIENTATION", hmOrientationMember.get(getUserType()));
			}
			request.setAttribute("hmEmpDetails", hmEmpDetails);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public String getLevelQuestion(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		// boolean flag = false;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			Map<String, String> hmLevelName = new HashMap<String, String>();
			Map<String, String> hmOrientMemberID = getOrientMemberID();
			int sectionCnt = 0;
			if (uF.parseToInt(currentLevel) == 0) {
				// pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id=? order by appraisal_level_id");
			//===start parvez date: 14-03-2023===	
//				pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id=? order by main_level_id");
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) && getRole()!=null 
						&& (getRole().equalsIgnoreCase("Peer") || getRole().equalsIgnoreCase("Other Peer") || getRole().equalsIgnoreCase("HOD"))){
					pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id=? and main_level_id in (select main_level_id from appraisal_level_details where appraisal_id=? and appraisal_system=2) order by main_level_id");
					pst.setInt(2, uF.parseToInt(id));
				}else{
					pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id=? order by main_level_id");
				}
			//===end parvez date: 14-03-2023===
			} else {
				// pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id=?  and appraisal_level_id=? order by appraisal_level_id");
			//===start parvez date: 14-03-2023===	
				/*pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id=?  and main_level_id=? order by main_level_id");
				pst.setInt(2, uF.parseToInt(currentLevel));*/
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) && getRole()!=null 
						&& (getRole().equalsIgnoreCase("Peer") || getRole().equalsIgnoreCase("Other Peer") || getRole().equalsIgnoreCase("HOD"))){
					pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id=?  and main_level_id in (select main_level_id from appraisal_level_details where appraisal_id=? and appraisal_system=2) order by main_level_id");
					pst.setInt(2, uF.parseToInt(id));
				} else{
					pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id=?  and main_level_id=? order by main_level_id");
					pst.setInt(2, uF.parseToInt(currentLevel));
				}
			//===end parvez date: 14-03-2023===	
			}
			pst.setInt(1, uF.parseToInt(id));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			String appraisal_level_id = null;
			while (rs.next()) {
				sectionCnt++;
				// flag = true;
				// if (levelId != null) {
				// levelId += "," + rs.getString("appraisal_level_id");
				// } else {
				// levelId = rs.getString("appraisal_level_id");
				// }
				appraisal_level_id = rs.getString("main_level_id");
				hmLevelName.put("LEVEL_NAME", rs.getString("level_title"));
				hmLevelName.put("LEVEL_SDESC", rs.getString("short_description"));
				hmLevelName.put("LEVEL_LDESC", rs.getString("long_description"));
				hmLevelName.put("LEVEL_COUNT", sectionCnt + "");
				hmLevelName.put("APP_LEVEL_ID", rs.getString("main_level_id"));
				hmLevelName.put("LEVEL_WEIGHTAGE", rs.getString("section_weightage"));  //added by parvez date: 27-02-2023
			//===start parvez date: 15-03-2023===
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) && getRole()!=null 
						&& (getRole().equalsIgnoreCase("Peer") || getRole().equalsIgnoreCase("Other Peer") || getRole().equalsIgnoreCase("HOD"))){
					hmLevelName.put("LEVEL_WEIGHTAGE", uF.parseToInt(rs.getString("section_weightage"))<100 ? 100+"" : rs.getString("section_weightage"));
				}
			//===end parvez date: 15-03-2023===	
				break;
			}
			rs.close();
			pst.close();
			
//			System.out.println("appraisal_level_id ===>> " + appraisal_level_id);
			
			pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id=? and emp_id=? and user_id=? and " +
				"user_type_id=? and appraisal_freq_id=? and reviewer_or_appraiser=? and section_id=? order by section_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			if(getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
				pst.setInt(6, 1);
			} else {
				pst.setInt(6, 0);
			}
			pst.setInt(7, uF.parseToInt(currentLevel));
			rs = pst.executeQuery();
//			System.out.println(" pst =============>> "+ pst);
			while(rs.next()) {
				hmLevelName.put("SECTION_COMMENT", rs.getString("section_comment"));
			} 
			rs.close();
			pst.close();
			
//			System.out.println("appraisal_level_id ===>> " + appraisal_level_id);
			if (appraisal_level_id == null) {
				return "update";
			}
			request.setAttribute("hmLevelName", hmLevelName);

//			System.out.println(" currentLevel before =============>> "+ currentLevel);
			setCurrentLevel(appraisal_level_id);
//			System.out.println(" currentLevel after =============>> "+ currentLevel);
			getCurrentLevelAnswer(uF, uF.parseToInt(currentLevel));

			
			List<String> alKRAIds = (List<String>)request.getAttribute("alKRAIds");
			List<String> alGoarTargetIds = (List<String>)request.getAttribute("alGoarTargetIds");
			
			// StringBuilder sb = new StringBuilder("select * from appraisal_question_details where appraisal_id=? and appraisal_level_id=?");
			StringBuilder sb = new StringBuilder("select * from appraisal_question_details where appraisal_id=? and appraisal_level_id in (" +
				"select appraisal_level_id from appraisal_level_details where main_level_id=?) ");
			sb.append(" order by appraisal_question_details_id");
			/*if(getRole() == null || !getRole().equalsIgnoreCase("Reviewer")) {
				if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get(MANAGER))) {
					sb.append(" and manager !=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("HR"))) {
					sb.append(" and hr !=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("Self"))) {
					sb.append(" and self !=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("Peer"))) {
					sb.append(" and peer !=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("CEO"))) {
					sb.append(" and ceo !=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("HOD"))) {
					sb.append(" and hod !=0 ");
				}
			}*/
			pst = con.prepareStatement(sb.toString());
			pst.setInt(1, uF.parseToInt(id));
			pst.setInt(2, uF.parseToInt(currentLevel));
			rs = pst.executeQuery();
//			System.out.println("StAp/1790---Get Questions LevelWise pst ===> "+pst);
			Map<String, List<List<String>>> hmLevelQuestion = new LinkedHashMap<String, List<List<String>>>();
			// List<List<String>> outerList = new ArrayList<List<String>>();
			while (rs.next()) {
				if(rs.getInt("app_system_type")==4 && alKRAIds!=null && rs.getString("kra_id")!=null && !alKRAIds.contains(rs.getString("kra_id"))) {
					continue;
				}
				if((rs.getInt("app_system_type")==3 || rs.getInt("app_system_type")==5) && alGoarTargetIds!=null && rs.getString("goal_kra_target_id")!=null && !alGoarTargetIds.contains(rs.getString("goal_kra_target_id"))) {
					continue;
				}
			//===start parvez date: 14-03-2023===	
				/*if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) && getRole()!=null 
						&& (getRole().equalsIgnoreCase("Peer") || getRole().equalsIgnoreCase("Other Peer") || getRole().equalsIgnoreCase("HOD")) && rs.getInt("app_system_type")==4){
					continue;
				}*/
			//===end parvez date: 14-03-2023===
				List<List<String>> outerList = hmLevelQuestion.get(rs.getString("appraisal_level_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add("");
				innerList.add(rs.getString("appraisal_id"));
				innerList.add(rs.getString("plan_id"));
				innerList.add("");
				innerList.add("");
				innerList.add("");
				innerList.add("");
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("attribute_id"));
				innerList.add(rs.getString("other_short_description"));
				innerList.add(rs.getString("appraisal_level_id"));
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(rs.getString("other_id"));
				innerList.add("");
				innerList.add("");
				outerList.add(innerList);
				hmLevelQuestion.put(rs.getString("appraisal_level_id"), outerList);
			}
			rs.close();
			pst.close();

			// request.setAttribute("questionList", outerList);
//			 System.out.println("StAp/1836--hmLevelQuestion ===>"+hmLevelQuestion);
			request.setAttribute("hmLevelQuestion", hmLevelQuestion);
			// System.out.println("currentLevel===>"+currentLevel);
			// System.out.println("getUserType()===>"+getUserType());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "success";
	}

	
	private Map<String, String> getOrientMemberID() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, String> orientationMemberMp = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from orientation_member where status = true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMemberMp.put(rs.getString("member_name"), rs.getString("member_id"));
			}
			rs.close();
			pst.close();
			// System.out.println("memberid=====>"+orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return orientationMemberMp;
	}

	
	// public String getLevelId() {
	// return levelId;
	// }
	//
	// public void setLevelId(String levelId) {
	// this.levelId = levelId;
	// }

	
	public Map<String, String> getAppraisalQuestionMap(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, String> AppraisalQuestion = new HashMap<String, String>();
		Map<String, List<String>> hmQuestion = new HashMap<String, List<String>>();
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);  //added by parvez date: 14-03-2023
			
			List<String> alKRAIds = (List<String>)request.getAttribute("alKRAIds");
			List<String> alGoarTargetIds = (List<String>)request.getAttribute("alGoarTargetIds");
//			System.out.println("StAp/1864---alKRAIds=="+alKRAIds);
			
	//===start parvez date: 16-03-2022===		
			pst = con.prepareStatement("select qb.*,aqd.app_system_type,aqd.appraisal_question_details_id,aqd.other_short_description,aqd.weightage from question_bank qb, " +
				" appraisal_question_details aqd where qb.question_bank_id = aqd.question_id and appraisal_id=? order by appraisal_question_details_id");
	//===end parvez date: 16-03-2022===		
			pst.setInt(1, uF.parseToInt(getId()));
//			System.out.println("SAp/1724--pst="+pst);
			rs = pst.executeQuery();
			List<List<String>> outerList = new ArrayList<List<String>>();
			while (rs.next()) {
				if(rs.getInt("app_system_type")==4 && alKRAIds!=null && rs.getString("kra_id")!=null && !alKRAIds.contains(rs.getString("kra_id"))) {
					continue;
				}
				if((rs.getInt("app_system_type")==3 || rs.getInt("app_system_type")==5) && alGoarTargetIds!=null && rs.getString("goal_kra_target_id")!=null && !alGoarTargetIds.contains(rs.getString("goal_kra_target_id"))) {
					continue;
				}
			//===start parvez date: 14-03-2023===	
				/*if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) && getRole()!=null 
						&& (getRole().equalsIgnoreCase("Peer") || getRole().equalsIgnoreCase("Other Peer") || getRole().equalsIgnoreCase("HOD")) && rs.getInt("app_system_type")!=2){
					continue;
				}*/
			//===end parvez date: 14-03-2023===	
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("question_bank_id")); //0
				innerList.add(rs.getString("question_text")); //1
				innerList.add(rs.getString("option_a")); //2
				innerList.add(rs.getString("option_b")); //3
				innerList.add(rs.getString("option_c")); //4
				innerList.add(rs.getString("option_d")); //5
				innerList.add(rs.getString("correct_ans")); //6
				innerList.add(rs.getString("is_add")); //7
				innerList.add(rs.getString("question_type")); //8
				innerList.add(rs.getString("appraisal_question_details_id")); //9
				innerList.add(rs.getString("option_e")); //10
				innerList.add(rs.getString("rate_option_a")); //11
				innerList.add(rs.getString("rate_option_b")); //12
				innerList.add(rs.getString("rate_option_c")); //13
				innerList.add(rs.getString("rate_option_d")); //14
				innerList.add(rs.getString("rate_option_e")); //15
		//===start parvez date: 24-12-2021===
				innerList.add(rs.getString("app_system_type")); //16
		//===end parvez date: 24-12-2021===
		
		//===start parvez date: 10-03-2022===
				innerList.add(rs.getString("other_short_description")); //17
		//===end parvez date: 10-03-2022===
				
		//===start parvez date: 16-03-2022===
				innerList.add(rs.getString("weightage"));		//18
		//===end parvez date: 16-03-2022===
				
				outerList.add(innerList);
				hmQuestion.put(rs.getString("question_bank_id"), innerList);
				AppraisalQuestion.put(rs.getString("question_bank_id"), rs.getString("question_text"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmQuestion", hmQuestion);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return AppraisalQuestion;
	}

	
	public void getanswerTypeMap(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, List<List<String>>> hmQuestionanswerType = new HashMap<String, List<List<String>>>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_answer_type_sub order by appraisal_answer_type_sub_id ");
			rs = pst.executeQuery();
			while (rs.next()) {
				List<List<String>> outerList = hmQuestionanswerType.get(rs.getString("answer_type_id"));
				if (outerList == null)
					outerList = new LinkedList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("score_label"));
				innerList.add(rs.getString("score"));
				outerList.add(innerList);
				hmQuestionanswerType.put(rs.getString("answer_type_id"), outerList);
			}
			rs.close();
			pst.close();
//			System.out.println("SAp/1883---hmQuestionanswerType=="+hmQuestionanswerType);
			request.setAttribute("hmQuestionanswerType", hmQuestionanswerType);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public Map<String, String> getLevelMap(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new HashMap<String, String>();
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectLevel);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name") + "[" + rs.getString("level_code") + "]");
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmLevelMap;
	}

	
	private void getQuestionSubType(UtilityFunctions uF) {
		Map<String, List<List<String>>> answertypeSub = new HashMap<String, List<List<String>>>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_answer_type_sub order by appraisal_answer_type_sub_id");
			rst = pst.executeQuery();
			while (rst.next()) {
				List<List<String>> outerList = answertypeSub.get(rst.getString("answer_type_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("score"));
				innerList.add(rst.getString("score_label"));
				outerList.add(innerList);
				answertypeSub.put(rst.getString("answer_type_id"), outerList);
			}
			rst.close();
			pst.close();
			request.setAttribute("answertypeSub", answertypeSub);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	private void checkCurrentLevelExistForCurrentEmp(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		boolean flag = false;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			
			pst = con.prepareStatement("select count(*) as cnt, section_id from appraisal_question_answer where emp_id=? and appraisal_id=? and user_type_id=?"
				+ " and user_id=? and appraisal_freq_id=? and reviewer_or_appraiser=? group by section_id");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getUserType()));
//			pst.setInt(4, uF.parseToInt(getCurrentLevel()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			if(getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
				pst.setInt(6, 1);
			} else {
				pst.setInt(6, 0);
			}
			rst = pst.executeQuery();
//			System.out.println("pst === > "+pst);
			Map<String, String> hmSectionGivenQueCnt = new HashMap<String, String>();
			while (rst.next()) {
				hmSectionGivenQueCnt.put(rst.getString("section_id"), rst.getInt("cnt")+"");
			}
			rst.close();
			pst.close();
			
			List<String> alKRAIds = (List<String>)request.getAttribute("alKRAIds");
			List<String> alGoarTargetIds = (List<String>)request.getAttribute("alGoarTargetIds");
			
//			pst = con.prepareStatement("select count(*) as cnt from appraisal_question_details  where appraisal_id=? and appraisal_level_id in (" +
//			"select appraisal_level_id from  appraisal_level_details where main_level_id=?)");
			Map<String, String> hmLevelQueCnt = new HashMap<String, String>();
			List<String> questionTotalList = new ArrayList<String>();/* Create by dattatray */
			/*pst = con.prepareStatement("select count(*) as cnt,b.main_level_id from appraisal_question_details a, appraisal_level_details b " +
				"where a.appraisal_id=? and a.appraisal_level_id = b.appraisal_level_id group by b.main_level_id order by b.main_level_id");*/
		//===start parvez date: 15-03-2023===	
			/*pst = con.prepareStatement("select a.*, b.main_level_id from appraisal_question_details a, appraisal_level_details b " +
				"where a.appraisal_id=? and a.appraisal_level_id = b.appraisal_level_id order by b.main_level_id");*/
			if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) && getRole()!=null 
					&& (getRole().equalsIgnoreCase("Peer") || getRole().equalsIgnoreCase("Other Peer") || getRole().equalsIgnoreCase("HOD"))){
				pst = con.prepareStatement("select a.*, b.main_level_id from appraisal_question_details a, appraisal_level_details b " +
					"where a.appraisal_id=? and a.appraisal_level_id = b.appraisal_level_id and b.appraisal_system=2 order by b.main_level_id");
			} else{
				pst = con.prepareStatement("select a.*, b.main_level_id from appraisal_question_details a, appraisal_level_details b " +
					"where a.appraisal_id=? and a.appraisal_level_id = b.appraisal_level_id order by b.main_level_id");
			}
		//===end parvez date: 23-03-2023===	
			pst.setInt(1, uF.parseToInt(getId()));
			rst = pst.executeQuery();
//			System.out.println("pst === > "+pst);
			Map<String, String> hmSectionGivenAllQueFlag = new HashMap<String, String>();
			while (rst.next()) {
				if(rst.getInt("app_system_type")==4 && !alKRAIds.contains(rst.getString("kra_id"))) {
					continue;
				}
				if((rst.getInt("app_system_type")==3 || rst.getInt("app_system_type")==5) && !alGoarTargetIds.contains(rst.getString("goal_kra_target_id"))) {
					continue;
				}
				questionTotalList.add(rst.getString("appraisal_question_details_id"));/* Create by dattatray */
				int intCnt = uF.parseToInt(hmLevelQueCnt.get(rst.getString("main_level_id")));
				intCnt++;
				hmLevelQueCnt.put(rst.getString("main_level_id"), intCnt+"");
			}
			rst.close();
			pst.close();
			request.setAttribute("questionTotalList", questionTotalList);/* Create by dattatray */
//			System.out.println("StApp/1966--questionTotalList="+questionTotalList);
			
			Iterator<String> it = hmLevelQueCnt.keySet().iterator();
			while (it.hasNext()) {
				String appLevelId = (String) it.next();
				String queCnt = hmLevelQueCnt.get(appLevelId);
				boolean flagAllQue = false;
				if(uF.parseToInt(hmSectionGivenQueCnt.get(appLevelId)) == uF.parseToInt(queCnt)) {
					flagAllQue = true;
				}
				if(uF.parseToInt(getCurrentLevel()) == uF.parseToInt(appLevelId) && uF.parseToInt(hmSectionGivenQueCnt.get(getCurrentLevel())) == uF.parseToInt(queCnt)) {
					flag = true;
				}
				hmSectionGivenAllQueFlag.put(appLevelId, flagAllQue+"");

			}
			
	//===start parvez date: 18-07-2022===
			if(uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) && uF.parseToInt(getUserType())==13){
				pst = con.prepareStatement("select * from reviewer_feedback_details where appraisal_id=? and appraisal_freq_id=? and emp_id=? and user_id=? and user_type_id=?");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.setInt(2, uF.parseToInt(getAppFreqId()));
				pst.setInt(3, uF.parseToInt(getEmpID()));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setInt(5, uF.parseToInt(getUserType()));
				rst = pst.executeQuery();
				while (rst.next()) {
					flag = true;
				}
				rst.close();
				pst.close();
			}
	//===end parvez date: 18-07-2022===		
			
			request.setAttribute("hmSectionGivenAllQueFlag", hmSectionGivenAllQueFlag);
			
			
			String sectionCount = "0";
			pst = con.prepareStatement("select count(distinct(section_id)) as section_id from appraisal_question_answer where emp_id=? and " +
				"appraisal_id=? and user_type_id=? and user_id=? and appraisal_freq_id=? and reviewer_or_appraiser=?");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getUserType()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			if(getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
				pst.setInt(6, 1);
			} else {
				pst.setInt(6, 0);
			}
			rst = pst.executeQuery();
			// System.out.println("pst === > "+pst);
			while (rst.next()) {
				sectionCount = rst.getString("section_id");
			}
			rst.close();
			pst.close();
			// System.out.println("sectionCount ===> " + sectionCount);
			
			/* Create by dattatray */
			String questionCount = "0";
			pst = con.prepareStatement("select count(question_id) as question_id from appraisal_question_answer where emp_id=? and " +
				"appraisal_id=? and user_type_id=? and user_id=? and appraisal_freq_id=? and reviewer_or_appraiser=?");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getUserType()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			if(getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
				pst.setInt(6, 1);
			} else {
				pst.setInt(6, 0);
			}
			rst = pst.executeQuery();
//			System.out.println("QuestionCount pst === > "+pst);
			while (rst.next()) {
				questionCount = rst.getString("question_id");
			}
			rst.close();
			pst.close();
			request.setAttribute("questionCount", questionCount);/* Create by dattatray */
			
			request.setAttribute("sectionCount", sectionCount);
			request.setAttribute("existLevelFlag", flag);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	private void getFinalResult() {
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, List<String>> hmRemainOrientDetails = getSecondMaxOrientNameAndIDs(uF);
		List<String> listRemainOrientName = hmRemainOrientDetails.get("NAME");
		List<String> listRemainOrientID = hmRemainOrientDetails.get("ID");
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);

		Map<String, List<String>> hmExistOrientTypeAQA = getExistOrientTypeInAQA(uF);
	
		List<String> listExistOrientTypeInAQA = hmExistOrientTypeAQA.get(getCurrentLevel() + "_" + getEmpID()+"_"+getAppFreqId());

		List<String> listRemainOrientType = new ArrayList<String>();

		Map<String, List<String>> hmExistUsersAQA = getExistUsersInAQA();
		Map<String, List<String>> hmOrientTypewiseID = getOrientTypeWiseIds();
//		System.out.println("listRemainOrientID =========>> " + listRemainOrientID);
//		System.out.println("listRemainOrientName =========>> " + listRemainOrientName);
		for (int b = 0; listRemainOrientID != null && b < listRemainOrientID.size(); b++) {
			// System.out.println("listExistOrientTypeInAQA =========>> " + listExistOrientTypeInAQA);
			if (listExistOrientTypeInAQA != null) {
//				if (!listRemainOrientID.get(b).trim().equals("") && !listExistOrientTypeInAQA.contains(listRemainOrientID.get(b).trim())) {
				if (!listRemainOrientID.get(b).trim().equals("") && !listExistOrientTypeInAQA.contains(listRemainOrientID.get(b).trim())&& hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) 
						&& (uF.parseToInt(listRemainOrientID.get(b))==4 || uF.parseToInt(listRemainOrientID.get(b))==14 || uF.parseToInt(listRemainOrientID.get(b))==13)&& uF.parseToInt(getCurrentLevel())==2) {
					
					listRemainOrientType.add(listRemainOrientName.get(b));
					// sbRemainOrientTypeID.append(listRemainOrientID.get(b)+",");
				} else {
					List<String> listExistUserInAQA = hmExistUsersAQA.get(getCurrentLevel() + "_" + listRemainOrientID.get(b));
					List<String> listIds = hmOrientTypewiseID.get(getId() + "_" + listRemainOrientID.get(b));

					// System.out.println("listExistUserInAQA =========>> " + listExistUserInAQA);
					// System.out.println("listIds =========>> " + listIds);

					boolean flag = false;
					for (int a = 0; listIds != null && a < listIds.size(); a++) {
						if (listExistUserInAQA != null) {
							if (!listIds.get(a).trim().equals("") && uF.parseToInt(listIds.get(a).trim()) > 0
									&& !listExistUserInAQA.contains(listIds.get(a).trim()) && !listRemainOrientName.get(b).trim().equals("Self")) {
								flag = true;
							} else {
								flag = false;
								break;
							}
						}
					}
					if (flag == true) {
						listRemainOrientType.add(listRemainOrientName.get(b));
						// sbRemainOrientTypeIDForSelf.append(listRemainOrientIDForSelf.get(b)+",");
					}
				}
			} else {
				listRemainOrientType.add(listRemainOrientName.get(b));
				// sbRemainOrientTypeIDForSelf.append(listRemainOrientIDForSelf.get(b)+",");
			}
		}
//		 System.out.println("listRemainOrientType ===>> " + listRemainOrientType);
		request.setAttribute("listRemainOrientType", listRemainOrientType);
	}

	
	private Map<String, List<String>> getOrientTypeWiseIds() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, List<String>> hmOrientTypewiseID = new HashMap<String, List<String>>();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmManagerId = new HashMap<String, String>();
			pst = con.prepareStatement("select emp_id,supervisor_emp_id from employee_official_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmManagerId.put(rs.getString("emp_id"), rs.getString("supervisor_emp_id"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select appraisal_details_id,supervisor_id,peer_ids,self_ids,hr_ids,finalization_ids,ceo_ids,hod_ids "
					+ " from appraisal_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				// System.out.println("getEmpId() ========== > "+getEmpID());
				String finalizeIds = null;
				if (rs.getString("hr_ids") != null && !rs.getString("hr_ids").equals("")) {
					finalizeIds = rs.getString("hr_ids");
				} else {
					finalizeIds = rs.getString("finalization_ids");;
				}
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id") + "_7", getListData1(rs.getString("hr_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id") + "_4", getListData1(rs.getString("peer_ids")));
				// hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_3",
				// getListData1(","+getEmpID()+","));
				// hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_2",
				// getListData1(","+hmManagerId.get(getEmpID())+","));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id") + "_3", getListData1(rs.getString("self_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id") + "_2", getListData1(rs.getString("supervisor_id")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id") + "_5", getListData1(rs.getString("ceo_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id") + "_13", getListData1(rs.getString("hod_ids")));
			}
			rs.close();
			pst.close();
			// System.out.println("hmOrientTypewiseID ========== > "+hmOrientTypewiseID);
			request.setAttribute("hmOrientTypewiseID", hmOrientTypewiseID);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmOrientTypewiseID;
	}
	

	private List<String> getListData1(String Ids) {
		List<String> listIds = new ArrayList<String>();
		if (Ids != null && !Ids.equals("") && Ids.length() > 1) {
			Ids = Ids.substring(1, Ids.length());
			listIds = Arrays.asList(Ids.split(","));
		}
		// System.out.println("listIds ::: "+listIds);
		return listIds;
	}

	
	private Map<String, List<String>> getExistUsersInAQA() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, List<String>> hmExistUsersAQA = new HashMap<String, List<String>>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select distinct(user_type_id),appraisal_id,section_id,user_id,appraisal_freq_id from appraisal_question_answer");
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> existUsersAQAList = hmExistUsersAQA.get(rs.getString("section_id") + "_" + rs.getString("user_type_id")+"_"+rs.getString("appraisal_freq_id"));
				if (existUsersAQAList == null)
					existUsersAQAList = new ArrayList<String>();
				existUsersAQAList.add(rs.getString("user_id"));
				hmExistUsersAQA.put(rs.getString("section_id") + "_" + rs.getString("user_type_id")+"_"+rs.getString("appraisal_freq_id"), existUsersAQAList);
			}
			rs.close();
			pst.close();
			// System.out.println("hmExistUsersAQA ========== > "+hmExistUsersAQA);
			request.setAttribute("hmExistUsersAQA", hmExistUsersAQA);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmExistUsersAQA;
	}

	
	private Map<String, List<String>> getExistOrientTypeInAQA(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, List<String>> hmExistOrientTypeAQA = new HashMap<String, List<String>>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select distinct(user_type_id),appraisal_id,section_id,emp_id,appraisal_freq_id from appraisal_question_answer where appraisal_id = ? and appraisal_freq_id =?");
			pst.setInt(1, uF.parseToInt(id));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
//			System.out.println("staff ap getExistOrientTypeInAQA  pst===>"+pst );
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> existOrientTypeAQAList = hmExistOrientTypeAQA.get(rs.getString("section_id") + "_" + rs.getString("emp_id")+"_"+rs.getString("appraisal_freq_id"));
				if (existOrientTypeAQAList == null)
					existOrientTypeAQAList = new ArrayList<String>();
				existOrientTypeAQAList.add(rs.getString("user_type_id"));
				hmExistOrientTypeAQA.put(rs.getString("section_id") + "_" + rs.getString("emp_id")+"_"+rs.getString("appraisal_freq_id"), existOrientTypeAQAList);
			}
			rs.close();
			pst.close();
			
			//===start parvez date: 16-07-2022===
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			if(uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){
				pst = con.prepareStatement("select distinct(rfd.user_type_id),rfd.appraisal_id,rfd.emp_id,rfd.appraisal_freq_id,main_level_id from reviewer_feedback_details rfd,appraisal_main_level_details amld where rfd.appraisal_id=amld.appraisal_id and rfd.appraisal_id=? and rfd.appraisal_freq_id=?");
				pst.setInt(1, uF.parseToInt(id));
				pst.setInt(2, uF.parseToInt(getAppFreqId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> existOrientTypeAQAList = hmExistOrientTypeAQA.get(rs.getString("main_level_id")+"_"+rs.getString("emp_id")+"_"+rs.getString("appraisal_freq_id"));
					if(existOrientTypeAQAList==null) existOrientTypeAQAList = new ArrayList<String>();				
					if(!existOrientTypeAQAList.contains(rs.getString("user_type_id"))){
						existOrientTypeAQAList.add(rs.getString("user_type_id"));
					}
					hmExistOrientTypeAQA.put(rs.getString("main_level_id")+"_"+rs.getString("emp_id")+"_"+rs.getString("appraisal_freq_id"), existOrientTypeAQAList);
				}
				rs.close();
				pst.close();
			}
	//===end parvez date: 16-07-2022===
			// System.out.println("hmExistOrientTypeAQA ========== > "+hmExistOrientTypeAQA);
			request.setAttribute("hmExistOrientTypeAQA", hmExistOrientTypeAQA);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmExistOrientTypeAQA;
	}

	
	private Map<String, List<String>> getSecondMaxOrientNameAndIDs(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		Map<String, List<String>> hmRemainOrientDetails = new HashMap<String, List<String>>();
		try {

			con = db.makeConnection(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID();
			Map<String, String> hmRevieweeAndUsersIds = new HashMap<String, String>(); 
			pst = con.prepareStatement("select * from appraisal_reviewee_details where appraisal_id=? and appraisal_freq_id=? ");//Created By dattatray date:12-11-21
			pst.setInt(1, uF.parseToInt(id));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
//				System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmRevieweeAndUsersIds.put("6", rs.getString("subordinate_ids"));
				hmRevieweeAndUsersIds.put("4", rs.getString("peer_ids"));
				hmRevieweeAndUsersIds.put("14", rs.getString("other_peer_ids"));
				hmRevieweeAndUsersIds.put("10", rs.getString("other_ids"));
				hmRevieweeAndUsersIds.put("7", rs.getString("hr_ids"));
				hmRevieweeAndUsersIds.put("8", rs.getString("grand_supervisor_ids"));
				hmRevieweeAndUsersIds.put("13", rs.getString("hod_ids"));
				hmRevieweeAndUsersIds.put("2", rs.getString("supervisor_ids"));
				hmRevieweeAndUsersIds.put("5", rs.getString("ceo_ids"));
				hmRevieweeAndUsersIds.put("3", ","+rs.getString("reviewee_id")+",");
				
			}
			rs.close();
			pst.close();
			
			List<String> allOrientName = new ArrayList<String>();
			List<String> allOrientID = new ArrayList<String>();
			getOrientPositions();
			int posiID = 0;
			List<String> orientPositionList = (List<String>) request.getAttribute("orientPositionList");
			String orientName[] = {"HR", "Manager", "Self", "Peer", "Client", "Sub-ordinate", "GroupHead", "Vendor", "CEO", "HOD","Other Peer"};
			for (int a = 0; a < orientName.length; a++) {
				if (role.equals(orientName[a])) {
					posiID = a;
				}
			}
			if (orientPositionList != null && !orientPositionList.isEmpty()) {
				int position = uF.parseToInt(orientPositionList.get(posiID));
				int cnt = 1;
				for (int i = position; i >= 1; i--) {
					if (allOrientID == null || allOrientID.isEmpty()) {
						for (int j = 0; orientPositionList != null && j < orientPositionList.size(); j++) {
							if (uF.parseToInt(orientPositionList.get(posiID)) > 1 && (uF.parseToInt(orientPositionList.get(posiID)) - cnt) == uF.parseToInt(orientPositionList.get(j))) {
								if(hmRevieweeAndUsersIds.get(hmOrientMemberID.get(orientName[j]))!=null && hmRevieweeAndUsersIds.get(hmOrientMemberID.get(orientName[j])).length()>1) {
									allOrientName.add(orientName[j]);
									allOrientID.add(hmOrientMemberID.get(orientName[j]));
								}
							}
						}
					}
					cnt++;
				}
			}
			hmRemainOrientDetails.put("NAME", allOrientName);
			hmRemainOrientDetails.put("ID", allOrientID);
//			 System.out.println("hmRemainOrientDetails ========== > "+hmRemainOrientDetails);
			request.setAttribute("hmRemainOrientDetails", hmRemainOrientDetails);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmRemainOrientDetails;
	}

	private void getOrientPositions() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		List<String> orientPositionList = new ArrayList<String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_main_level_details where main_level_id=? limit 1");
			pst.setInt(1, uF.parseToInt(currentLevel));
			rs = pst.executeQuery();
			while (rs.next()) {
				orientPositionList.add(rs.getString("hr"));
				orientPositionList.add(rs.getString("manager"));
				orientPositionList.add(rs.getString("self"));
				orientPositionList.add(rs.getString("peer"));
				orientPositionList.add(rs.getString("client"));
				orientPositionList.add(rs.getString("subordinate"));
				orientPositionList.add(rs.getString("grouphead"));
				orientPositionList.add(rs.getString("vendor"));
				orientPositionList.add(rs.getString("ceo"));
				orientPositionList.add(rs.getString("hod"));
				orientPositionList.add(rs.getString("other_peer"));
			}
			rs.close();
			pst.close();

			request.setAttribute("orientPositionList", orientPositionList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	private Map<String, String> getOrientationMember(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		Map<String, String> orientationMemberMp = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				// orientationMemberMp.put(rs.getString("orientation_member_id"),
				// rs.getString("member_name"));
				orientationMemberMp.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return orientationMemberMp;
	}

	
	private Map<String, String> getOrientationValue(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		Map<String, String> orientationMp = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from apparisal_orientation");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMp.put(rs.getString("apparisal_orientation_id"), rs.getString("orientation_name"));
			}
			rs.close();
			pst.close();

			request.setAttribute("orientationMp", orientationMp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return orientationMp;
	}

	
	private Map<String, String> getReviewDetails(Connection con) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, String> hmReviewData = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rst = pst.executeQuery();
			while (rst.next()) {
				hmReviewData.put("REVIEW_NAME", rst.getString("appraisal_name"));
				hmReviewData.put("REVIEW_SELFID", rst.getString("self_ids"));
				hmReviewData.put("REVIEW_PEERID", rst.getString("peer_ids"));
				hmReviewData.put("REVIEW_MANAGERID", rst.getString("supervisor_id"));
				hmReviewData.put("REVIEW_HRID", rst.getString("hr_ids"));
				hmReviewData.put("REVIEW_OTHERID", rst.getString("other_ids"));
				hmReviewData.put("REVIEW_STARTDATE", rst.getString("from_date"));
				hmReviewData.put("REVIEW_ENDDATE", rst.getString("to_date"));
				hmReviewData.put("REVIEW_CEOID", rst.getString("ceo_ids"));
				hmReviewData.put("REVIEW_HODID", rst.getString("hod_ids"));
				// hmReviewData.put("REVIEW_ENDDATE", rst.getString(""));
			}
			rst.close();
			pst.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hmReviewData;
	}
	

	@Override
	public void run() {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		con = db.makeConnection(con);
		Map<String, String> hmReviewData = getReviewDetails(con);
		Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);

		// System.out.println("allIdList ==> "+ allIdList);
		// String strDomain = request.getServerName().split("\\.")[0];
		// for (int i = 0; allIdList != null && !allIdList.isEmpty() && i <
		// allIdList.size(); i++) {
		// if(allIdList.get(i) != null && !allIdList.get(i).equals("")){
		/*
		 * Map<String, String> hmEmpInner = hmEmpInfo.get(strSessionEmpId);
		 * Map<String, String> hmRevieweeInner = hmEmpInfo.get(empID); //
		 * System.out.println(i+" allIdList "+allIdList.get(i)); // String
		 * strDomain = request.getServerName().split("\\.")[0]; Notifications nF
		 * = new Notifications(N_EMP_REVIW_SUBMITED, CF);
		 * nF.setDomain(getStrDomain()); nF.request = request;
		 * nF.setStrEmpId(strSessionEmpId);
		 * nF.setStrHostAddress(CF.getStrEmailLocalHost());
		 * nF.setStrHostPort(CF.getStrHostPort());
		 * nF.setStrContextPath(request.getContextPath());
		 * 
		 * if(empID != null && strSessionEmpId != null &&
		 * empID.equals(strSessionEmpId)){ nF.setStrRevieweeName("SELF"); }else{
		 * nF
		 * .setStrRevieweeName(hmRevieweeInner.get("FNAME")+" "+hmRevieweeInner
		 * .get("LNAME")); }
		 * nF.setStrReviewName(hmReviewData.get("REVIEW_NAME"));
		 * nF.setStrReviewStartdate
		 * (uF.getDateFormat(hmReviewData.get("REVIEW_STARTDATE"), DBDATE,
		 * CF.getStrReportDateFormat()));
		 * nF.setStrReviewEnddate(uF.getDateFormat
		 * (hmReviewData.get("REVIEW_ENDDATE"), DBDATE,
		 * CF.getStrReportDateFormat()));
		 * 
		 * nF.setStrEmpFname(hmEmpInner.get("FNAME"));
		 * nF.setStrEmpLname(hmEmpInner.get("LNAME"));
		 * nF.setEmailTemplate(true); nF.sendNotifications();
		 */
		// }
		// }
		db.closeConnection(con);
	}
	
	
//===Created by Parvez date: 22-12-2021===
//===start Parvez===
	private void insertNewGoalForNxtCycle() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		con = db.makeConnection(con);
		
		try {
			
			String strOrgId = CF.getEmpOrgId(con, uF, strSessionEmpId);
			
			String goalTitle = request.getParameter("goalTitle");
			String strAttributeId = "";
			pst = con.prepareStatement("select * from appraisal_attribute limit 1");
			rst = pst.executeQuery();
			while (rst.next()) {
				strAttributeId = rst.getString("arribute_id");
				
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select * from apparisal_orientation where apparisal_orientation_id = 6");
			rst = pst.executeQuery();
			String strOrientationId = "";
			
			while(rst.next()){
				strOrientationId = rst.getString("apparisal_orientation_id");
			}	
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_frequency where appraisal_frequency_id !=2 limit 1");
			rst = pst.executeQuery();
			String strFeqId = "";
			
			while(rst.next()){
				strFeqId = rst.getString("appraisal_frequency_id");
				
			}	
			rst.close();
			pst.close();
			
			List<String> appraisalList = (List<String>) request.getAttribute("appraisalList");
			
			String appId = appraisalList.get(0);
			String goalEffectiveDate = uF.getFutureDate(uF.getDateFormatUtil(appraisalList.get(4), "dd-MMM-yyyy"), 1)+"";
			
			String strMinMaxDates = uF.getCurrentMonthMinMaxDate(uF.getFutureDate(uF.getDateFormatUtil(goalEffectiveDate, DBDATE), 180)+"", DBDATE);
			String[] tmpMinMaxDt = strMinMaxDates.split("::::");
			
			String goalDueDate = tmpMinMaxDt[1];
						
			pst = con.prepareStatement("insert into goal_details(goal_type,goal_parent_id,goal_title,goal_objective,goal_description,goal_attribute," +
					"measure_type,measure_currency_value,measure_currency_id,measure_effort_days,measure_effort_hrs,due_date,is_feedback,orientation_id," +
					"weightage,emp_ids,level_id,grade_id,is_measure_kra,measure_kra,measure_type1,measure_currency_value1,measure_kra_days,measure_kra_hrs," +
					"entry_date,user_id,frequency,freq_year_type,recurring_years,frequency_month,priority,effective_date,goalalign_with_teamgoal," +
					"goal_element,org_id,super_id,peer_ids,manager_ids,hr_ids,anyone_ids,align_with_perspective,perspective_id,review_this_goal)" +
					"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
			
			pst.setInt(1, PERSONAL_GOAL);
			pst.setInt(2, 0);
			pst.setString(3, goalTitle);
			pst.setString(4, "");
			pst.setString(5, "");
			pst.setInt(6, uF.parseToInt(strAttributeId));
				
			pst.setString(7, "");
			pst.setDouble(8, 0);
			pst.setInt(9, 3);
			pst.setDouble(10, 0);
			pst.setDouble(11, 0);
			pst.setDate(12, uF.getDateFormat(goalDueDate, DBDATE));
			pst.setBoolean(13, false);
			pst.setInt(14, uF.parseToInt(strOrientationId));
			pst.setDouble(15, 100);
			pst.setString(16, ","+strSessionEmpId+",");
			pst.setString(17, "");
			pst.setString(18, "");
			pst.setBoolean(19, false);
			pst.setString(20, "");
			
			pst.setString(21, "");
			pst.setDouble(22,uF.parseToDouble("0"));
			pst.setDouble(23,uF.parseToDouble("0"));
			pst.setDouble(24,uF.parseToDouble("0"));
			pst.setDate(25, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(26, uF.parseToInt(strSessionEmpId));
			pst.setInt(27, uF.parseToInt(strFeqId));
			pst.setInt(28, 1);
			pst.setString(29, "");
			pst.setString(30, "");
			pst.setInt(31, 1);
			pst.setDate(32, uF.getDateFormat(goalEffectiveDate, DBDATE));
			pst.setBoolean(33, false);
			pst.setInt(34, 0);
			pst.setInt(35, uF.parseToInt(strOrgId));
			
			pst.setInt(36, 0);
			
			pst.setString(37, "");
			pst.setString(38, "");
			
			pst.setString(39, "");
			
			pst.setString(40, null);
				
			pst.setBoolean(41, false);
			pst.setInt(42, 0);
			pst.setString(43, "");
//			System.out.println("SAp/2594---pst==>"+pst);
			pst.execute();
			pst.close();
			
			int individual_goal_id = 0;
			pst = con.prepareStatement("select max(goal_id) from goal_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				individual_goal_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			List<String> alManagers = null;
			if (uF.parseToBoolean(CF.getIsWorkFlow())) {
				alManagers = insertApprovalMember(con, pst, rst, uF.parseToInt(individual_goal_id+""), uF);
			}
			
//			GoalScheduler scheduler = new GoalScheduler(request, session, CF, uF, strSessionEmpId);
//			scheduler.updateGoalDetails(individual_goal_id+"");
			
			pst = con.prepareStatement("insert into goal_details_frequency (goal_id, goal_start_date, goal_due_date, freq_start_date, freq_end_date, added_by, entry_date, goal_freq_name) " 
					+ "values (?,?,?,?, ?,?,?,?)"); 
			pst.setInt(1, individual_goal_id);
			pst.setDate(2, uF.getDateFormat(goalEffectiveDate, DBDATE));
			pst.setDate(3, uF.getDateFormat(goalDueDate, DBDATE));
			pst.setDate(4, uF.getDateFormat(goalEffectiveDate, DBDATE));
			pst.setDate(5, uF.getDateFormat(goalDueDate, DBDATE));
			pst.setInt(6, uF.parseToInt(strSessionEmpId));
			pst.setDate(7, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setString(8, null);
			pst.executeUpdate();
			pst.close();
			
			List<String> appQuestionDetailsId = new ArrayList<String>();
			pst = con.prepareStatement("select * from appraisal_question_details where appraisal_id=? and app_system_type=6");
			pst.setInt(1, uF.parseToInt(getId()));
			rst = pst.executeQuery();
			while (rst.next()) {
				appQuestionDetailsId.add(rst.getString("appraisal_question_details_id"));
			}
			rst.close();
			pst.close();
			
			System.out.println("StAppr/2660--appQuestionDetailsId="+appQuestionDetailsId);
			for(int j=0; appQuestionDetailsId != null && j<appQuestionDetailsId.size(); j++){
				pst = con.prepareStatement("update appraisal_question_details set goal_kra_target_id=? where appraisal_question_details_id=? and app_system_type=6");
				pst.setInt(1, individual_goal_id);
				pst.setInt(2, uF.parseToInt(appQuestionDetailsId.get(j)));
				pst.execute();
    			pst.close();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	}
//===end Parvez===
		
//===Created by Parvez date: 27-12-2021===
//===start Parvez===	
	private List<String> insertApprovalMember(Connection con, PreparedStatement pst, ResultSet rs, int nRecritmentId, UtilityFunctions uF) {

		List<String> alManagers = new ArrayList<String>();
		try {
			
			String policy_id = null;
			String empLevelId = CF.getEmpLevelId(con, strSessionEmpId);
			String location = CF.getEmpWlocationId(con, uF, strSessionEmpId);
			
			pst = con.prepareStatement("select policy_id from work_flow_policy_details where type='"+WORK_FLOW_PERSONAL_GOAL+"' and level_id=? and wlocation_id=?");
			pst.setInt(1, uF.parseToInt(empLevelId));
			pst.setInt(2, uF.parseToInt(location));
//			System.out.println("SAp/2669--pst====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				policy_id = rs.getString("policy_id");
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(policy_id) == 0){
				pst = con.prepareStatement("select policy_count from work_flow_member wfm,work_flow_policy wfp where wfp.group_id=wfm.group_id " +
						"and wfp.work_flow_member_id=wfm.work_flow_member_id and wfm.wlocation_id=? and wfm.is_default = true");
				pst.setInt(1, uF.parseToInt(location));
//				System.out.println("SAp/2681--pst="+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					policy_id = rs.getString("policy_count");
				}
				rs.close();
				pst.close();
			}
			
			Map<String, String> hmEmpSupervisorId = CF.getEmpSupervisorIdMap(con);

			Map<String, String> hmEmpUserTypeId = CF.getEmployeeIdUserTypeIdMap(con);
			Map<String, String> hmUserType = CF.getUserTypeMap(con);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);

			pst = con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where "
					+ " policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
			pst.setInt(1, uF.parseToInt(policy_id));
//			System.out.println("SAp/2697--pst ===>> " + pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmMemberMap = new LinkedHashMap<String, List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("member_type"));
				innerList.add(rs.getString("member_id"));
				innerList.add(rs.getString("member_position"));
				innerList.add(rs.getString("work_flow_mem"));
				innerList.add(rs.getString("work_flow_member_id"));

				hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("delete from work_flow_details where effective_id=? and effective_type=?");
			pst.setInt(1, nRecritmentId);
			pst.setString(2, WORK_FLOW_PERSONAL_GOAL);
			pst.executeUpdate();
			pst.close();

			String strDomain = request.getServerName().split("\\.")[0];
			Iterator<String> it = hmMemberMap.keySet().iterator();
			while (it.hasNext()) {
				String work_flow_member_id = it.next();
				List<String> innerList = hmMemberMap.get(work_flow_member_id);

				int memid = uF.parseToInt(innerList.get(1));
				// System.out.println("innerList.get(3)+memid====>"+innerList.get(3)+memid+"=====>"+request.getParameter(innerList.get(3)+memid));
//				String empid = request.getParameter(innerList.get(3) + memid);
				String empid = hmEmpSupervisorId.get(strSessionEmpId);

				if (empid != null && !empid.equals("")) {
					int userTypeId = memid;
					if (uF.parseToInt(innerList.get(0)) == 3) {
						userTypeId = uF.parseToInt(hmEmpUserTypeId.get(empid));
					}
					// System.out.println("approval empid====>"+empid);
					pst = con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position,"
							+ "work_flow_mem_id,is_approved,status,user_type_id) values(?,?,?,?, ?,?,?,?, ?)");
					pst.setInt(1, uF.parseToInt(empid));
					pst.setInt(2, nRecritmentId);
					pst.setString(3, WORK_FLOW_PERSONAL_GOAL);
					pst.setInt(4, uF.parseToInt(innerList.get(0)));
					pst.setInt(5, (int) uF.parseToDouble(innerList.get(2)));
					pst.setInt(6, uF.parseToInt(innerList.get(4)));
					pst.setInt(7, 0);
					pst.setInt(8, 0);
					pst.setInt(9, userTypeId);
//					System.out.println("SAp/2746--pst ===>> " + pst);
					pst.execute();
					pst.close();

					String alertData = "<div style=\"float: left;\"> Received a new Request for Personal Goal from <b>"
							+ CF.getEmpNameMapByEmpId(con, strSessionEmpId) + "</b>. [" + hmUserType.get(userTypeId + "") + "] </div>";
					String strSubAction = "";
					String alertAction = "";
					if (userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD))
							|| userTypeId == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
						if (userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD))) {
							strSubAction = "&currUserType=" + hmUserType.get(userTypeId + "");
						}
						alertAction = "GoalKRATargets.action?pType=WR&callFrom=KTDash&strEmpId="+strSessionEmpId+strSubAction;
					} else {
						alertAction = "GoalKRATargets.action?pType=WR&callFrom=KTDash&strEmpId="+strSessionEmpId+strSubAction;
					}

					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(empid);
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(userTypeId + "");
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();


					if (!alManagers.contains(empid)) {
						alManagers.add(empid);
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return alManagers;
	
	}
	
//===end Parvez===
	
//===created by parvez date: 06-07-2022===
	//===start===
	private void getAppraisalReopenStatus(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmUsersFeedbackReopenComment = new HashMap<String, String>();
			
			pst = con.prepareStatement("select reopen_comment from review_feedback_reopen_details where review_id=? and review_freq_id=? and emp_id=? and user_id =? and user_type_id=? " +
					" order by review_feedback_reopen_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			pst.setInt(3, uF.parseToInt(getEmpID()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setInt(5, uF.parseToInt(getUserType()));
			rs = pst.executeQuery();
//			System.out.println("hmExistSectionCount pst2 ::: "+pst);
			while (rs.next()) {
				hmUsersFeedbackReopenComment.put("REOPEN_REASON", rs.getString("reopen_comment"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmUsersFeedbackReopenComment", hmUsersFeedbackReopenComment);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}
	
	private void getReviewerFeedbackDetails(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmUsersFeedbackDetails = new HashMap<String, String>();
			
			double weightage = 0;
			pst = con.prepareStatement("select section_weightage from appraisal_main_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while(rs.next()){
				weightage += uF.parseToDouble(rs.getString("section_weightage")); 
			}
			rs.close();
			pst.close();
			hmUsersFeedbackDetails.put("WEIGHTAGE", weightage+"");
			
			pst = con.prepareStatement("select * from reviewer_feedback_details where appraisal_id=? and appraisal_freq_id=? and emp_id=? and user_id=? and user_type_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			pst.setInt(3, uF.parseToInt(getEmpID()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setInt(5, uF.parseToInt(getUserType()));
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String> innerMap = new HashMap<String, String>();
				hmUsersFeedbackDetails.put("MARKS", rs.getString("reviewer_marks"));
				hmUsersFeedbackDetails.put("COMMENT", rs.getString("reviewer_comment"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmUsersFeedbackDetails", hmUsersFeedbackDetails);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void insertReviewerFeedback(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
		//===start parvez date: 09-03-2023===
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
		//===end parvez date: 09-03-2023===	
			
			String levelComment = request.getParameter("levelcomment");
			String rating = request.getParameter("gradewithrating");
			
			double weightage = 0;
			pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while(rs.next()){
				weightage += uF.parseToDouble(rs.getString("section_weightage")); 
			}
			rs.close();
			pst.close();
			
		//===start parvez date: 09-03-2023===	
//			double marks = uF.parseToDouble(rating) * weightage / 5;
			double marks = 0;
			if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
				marks = uF.parseToDouble(rating) * weightage;
			}else{
				marks = uF.parseToDouble(rating) * weightage / 5;
			}
		//===end parvez date: 09-03-2023===	
			
			pst = con.prepareStatement("delete from reviewer_feedback_details where emp_id=? and appraisal_id=? and user_id=? and user_type_id=? " +
				" and appraisal_freq_id=?");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
	//		System.out.println("pst1==>"+pst);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("insert into reviewer_feedback_details(emp_id,appraisal_id,user_id,user_type_id,appraisal_freq_id," +
					"reviewer_comment,reviewer_marks,entry_date) values(?,?,?,?, ?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			pst.setString(6, levelComment);
			pst.setDouble(7, marks);
			pst.setTimestamp(8, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
//			System.out.println("SAP/3034--pst="+pst);
			pst.executeUpdate();
			pst.close();
			
			session.setAttribute("sbMessage", SUCCESSM+"Reviewee submitted successfully!"+END);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	//===end===
	
	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(String currentLevel) {
		this.currentLevel = currentLevel;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getLevelCount() {
		return levelCount;
	}

	public void setLevelCount(String levelCount) {
		this.levelCount = levelCount;
	}

	String strDomain;
	public void setDomain(String strDomain) {
		this.strDomain = strDomain;
	}

	public String getStrDomain() {
		return strDomain;
	}

	HttpServletRequest request;

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

	public String getBtnSave() {
		return btnSave;
	}

	public void setBtnSave(String btnSave) {
		this.btnSave = btnSave;
	}

	public File getLevelcommentFile() {
		return levelcommentFile;
	}

	public void setLevelcommentFile(File levelcommentFile) {
		this.levelcommentFile = levelcommentFile;
	}

	public String getLevelcommentFileFileName() {
		return levelcommentFileFileName;
	}

	public void setLevelcommentFileFileName(String levelcommentFileFileName) {
		this.levelcommentFileFileName = levelcommentFileFileName;
	}

	public String getAreasOfStrength() {
		return areasOfStrength;
	}

	public void setAreasOfStrength(String areasOfStrength) {
		this.areasOfStrength = areasOfStrength;
	}

	public String getAreasOfImprovement() {
		return areasOfImprovement;
	}

	public void setAreasOfImprovement(String areasOfImprovement) {
		this.areasOfImprovement = areasOfImprovement;
	}

	public String getIsAreasOfStrengthAndImprovement() {
		return isAreasOfStrengthAndImprovement;
	}

	public void setIsAreasOfStrengthAndImprovement(String isAreasOfStrengthAndImprovement) {
		this.isAreasOfStrengthAndImprovement = isAreasOfStrengthAndImprovement;
	}

//===start parvez date: 18-07-2022===
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
//===end parvez date: 18-07-2022===
}