package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class Appraisal implements ServletRequestAware, IStatements {

	HttpSession session;
	public String strSessionEmpId;
	public String strSessionUserType;
	public String strSessionUserTypeID;
	public String strBaseUserType = null;
	public String strBaseUserTypeId = null;
	public CommonFunctions CF;
 
	private String type;
		
	private String dataType;
	
	private String strSearchJob;
	
	private String proPage;
	private String minLimit;
	
	private String proPageReviewer;
	private String minLimitReviewer;
	
	private String currUserType;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strSessionUserTypeID = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";

		
		
//		if(strSessionUserType != null && strSessionUserType.equals(HRMANAGER) && getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(HR_REVIEW_ALERT)) {
//			updateUserAlerts();
//		} else if(strSessionUserType != null && strSessionUserType.equals(MANAGER) && getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(MANAGER_REVIEW_ALERT)) {
//			updateUserAlerts();
//		} else if(strSessionUserType != null && strSessionUserType.equals(EMPLOYEE) && getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(MY_REVIEW_ALERT)) {
//			updateUserAlerts();
//		} else if(strSessionUserType != null && strSessionUserType.equals(CEO) && getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(CEO_REVIEW_ALERT)) {
//			updateUserAlerts();
//		} else if(strSessionUserType != null && strSessionUserType.equals(HOD) && getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(HOD_REVIEW_ALERT)) {
//			updateUserAlerts();
//		}
		
		request.setAttribute(PAGE, "/jsp/performance/Appraisal.jsp");
		request.setAttribute(TITLE, TReviewForms);
		
		UtilityFunctions uF = new UtilityFunctions();
		if(uF.parseToInt(getProPage()) == 0) {
			setProPage("1");
		}
		if(uF.parseToInt(getProPageReviewer()) == 0) {
			setProPageReviewer("1");
		}
		if(getDataType() == null || getDataType().equals("") || getDataType().equalsIgnoreCase("null")) {
			setDataType("L");
		}
		
		if(getCurrUserType()==null && strSessionUserType != null && strSessionUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		
		getEmployeeAssignedKRAAndGoalTarget(uF);
		getSearchAutoCompleteData(uF);
		
		getAppraisalDetails();
		getReviewerAppraisalDetails();
		
		getOrientationMember();
		getAppriesalSections();
		getExistUsersInAQA();
		getOrientTypeWiseIds();
//		getExistOrientTypeInAQA();
		
		return "success";
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
			Map<String, List<String>> hmEmpKRAIds = new HashMap<String, List<String>>();
			String kraTypes = INDIVIDUAL_GOAL+","+INDIVIDUAL_KRA+","+EMPLOYEE_KRA;
			pst = con.prepareStatement("select * from goal_kras where goal_type in ("+kraTypes+")"); // and emp_ids like '%,"+empId+",%'
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while (rs.next()) {
				List<String> alEmpIds = new ArrayList<String>();
				if(rs.getString("emp_ids") !=null) {
					alEmpIds = Arrays.asList(rs.getString("emp_ids").split(","));
				}
				for(int i=0; alEmpIds!=null && i<alEmpIds.size(); i++) {
					if(uF.parseToInt(alEmpIds.get(i))>0) {
						List<String> alKRAIds = hmEmpKRAIds.get(alEmpIds.get(i));
						if(alKRAIds==null) alKRAIds = new ArrayList<String>();
						alKRAIds.add(rs.getString("goal_kra_id"));
						hmEmpKRAIds.put(alEmpIds.get(i), alKRAIds);
					}
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmpKRAIds", hmEmpKRAIds);
			
			Map<String, List<String>> hmEmpGoarTargetIds = new HashMap<String, List<String>>();
			pst = con.prepareStatement("select * from goal_details"); //where emp_ids like '%,"+empId+",%'
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> alEmpIds = new ArrayList<String>();
				if(rs.getString("emp_ids") !=null) {
					alEmpIds = Arrays.asList(rs.getString("emp_ids").split(","));
				}
				for(int i=0; alEmpIds!=null && i<alEmpIds.size(); i++) {
					if(uF.parseToInt(alEmpIds.get(i))>0) {
						List<String> alGoarTargetIds = hmEmpGoarTargetIds.get(alEmpIds.get(i));
						if(alGoarTargetIds==null) alGoarTargetIds = new ArrayList<String>();
						alGoarTargetIds.add(rs.getString("goal_id"));
						hmEmpGoarTargetIds.put(alEmpIds.get(i), alGoarTargetIds);
					}
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmpGoarTargetIds", hmEmpGoarTargetIds);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
//	private void checkCurrentLevelExistForCurrentEmp(UtilityFunctions uF) {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		boolean flag = false;
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select count(*) as cnt, section_id from appraisal_question_answer where user_id=? group by appraisal_id,appraisal_freq_id,user_type_id,emp_id,reviewer_or_appraiser,section_id");
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			rst = pst.executeQuery();
////			System.out.println("pst === > "+pst);
//			Map<String, String> hmSectionGivenQueCnt = new HashMap<String, String>();
//			while (rst.next()) {
//				hmSectionGivenQueCnt.put(rst.getString("appraisal_id")+"_"+rst.getString("appraisal_freq_id")+"_"+rst.getString("user_type_id")+"_"+rst.getString("emp_id")+"_"+rst.getString("reviewer_or_appraiser")+"_"+rst.getString("section_id"), rst.getInt("cnt")+"");
//			}
//			rst.close();
//			pst.close();
//			
//			
////			pst = con.prepareStatement("select count(*) as cnt from appraisal_question_details  where appraisal_id=? and appraisal_level_id in (" +
////			"select appraisal_level_id from  appraisal_level_details where main_level_id=?)");
//			pst = con.prepareStatement("select count(*) as cnt,b.main_level_id from appraisal_question_details a, appraisal_level_details b " +
//				"where a.appraisal_level_id = b.appraisal_level_id group by b.main_level_id order by b.main_level_id");
//			rst = pst.executeQuery();
////			System.out.println("pst === > "+pst);
//			Map<String, String> hmSectionGivenAllQueFlag = new HashMap<String, String>();
//			while (rst.next()) {
//				hmSectionGivenAllQueFlag.put(rst.getString("appraisal_id")+"_"+rst.getString("main_level_id"), rst.getInt("cnt")+"");
//			}
//			rst.close();
//			pst.close();
//			
//			request.setAttribute("hmSectionGivenAllQueFlag", hmSectionGivenAllQueFlag);
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rst);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	
	private void getSearchAutoCompleteData(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);

			Map<String,String> orientationMp = CF.getOrientationValue(con);
	    	Map<String, String> hmFrequency = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmFrequency.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();
			
			/*System.out.println("strSessionUserType==>"+strSessionUserType+"==>strBaseUserType==>"+strBaseUserType);
			System.out.println("getCurrUserType==>"+getCurrUserType());*/
			SortedSet<String> setSearchList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			//sbQuery.append("select * from appraisal_details where is_publish=true and is_close = false ");
			sbQuery.append("select * from appraisal_details a, appraisal_details_frequency adf  where a.appraisal_details_id = adf.appraisal_id "
					+" and (is_delete is null or is_delete = false)  and is_appraisal_publish=true and is_appraisal_close = false ");
			if (strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(HRMANAGER) || strSessionUserType.equalsIgnoreCase(ADMIN))) {
				sbQuery.append(" and hr_ids like '%,"+strSessionEmpId+",%' ");
			} else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(MANAGER) && getCurrUserType() != null && !getCurrUserType().equals(strBaseUserType)) {
				sbQuery.append(" and supervisor_id like '%,"+strSessionEmpId+",%' ");
			} else if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(CEO) && getCurrUserType()!=null && getCurrUserType().equals(strBaseUserType)) {
				sbQuery.append(" and ceo_ids like '%,"+strSessionEmpId+",%' ");
			} else if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(HOD) && getCurrUserType()!=null && getCurrUserType().equals(strBaseUserType)) {
				sbQuery.append(" and hod_ids like '%,"+strSessionEmpId+",%' ");
			}
			
			sbQuery.append(" order by to_date DESC");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst===> "+ pst);
			rs = pst.executeQuery();
			StringBuilder sbEmpIds = null;
			while (rs.next()) {
				setSearchList.add(rs.getString("appraisal_name"));
				
				if(rs.getString("self_ids")!=null && !rs.getString("self_ids").trim().equals("")) {
					if(sbEmpIds == null) {
						sbEmpIds = new StringBuilder();
						sbEmpIds.append(rs.getString("self_ids"));
					} else {
						sbEmpIds.append(","+rs.getString("self_ids"));
					}
				}
				
				if(rs.getString("appraisal_type")!=null && !rs.getString("appraisal_type").trim().equals("")) {
					setSearchList.add(rs.getString("appraisal_type"));
				}
				
				if(rs.getString("oriented_type")!=null && uF.parseToInt(rs.getString("oriented_type").trim()) > 0) {
					setSearchList.add(orientationMp.get(rs.getString("oriented_type").trim()));
				}
				if(rs.getString("frequency")!=null && uF.parseToInt(rs.getString("frequency").trim()) > 0) {
					setSearchList.add(hmFrequency.get(rs.getString("frequency").trim()));
				}
				
				if(rs.getString("appraisal_freq_name")!=null && uF.parseToInt(rs.getString("appraisal_freq_name").trim()) > 0) {
					setSearchList.add(hmFrequency.get(rs.getString("appraisal_freq_name").trim()));
				}
				
			}
			rs.close();
			pst.close();
			
			if(sbEmpIds != null) {
				List<String> alEmpIds = Arrays.asList(sbEmpIds.toString().trim().split(","));
				StringBuilder sbEmpIdsId = null;
				for(int i=0; alEmpIds != null && i < alEmpIds.size(); i++) {
					if(alEmpIds.get(i)!=null && !alEmpIds.get(i).trim().equals("") && uF.parseToInt(alEmpIds.get(i).trim()) > 0) {
						if(sbEmpIdsId == null) {
							sbEmpIdsId = new StringBuilder();
							sbEmpIdsId.append(alEmpIds.get(i).trim());
						} else {
							sbEmpIdsId.append(","+alEmpIds.get(i).trim());
						}
					}
				}
				
				/*if(sbEmpIdsId!=null) {
					pst = con.prepareStatement("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id " +
							"and eod.emp_id in("+sbEmpIdsId.toString()+")");
					rs = pst.executeQuery();
					while (rs.next()) {
						setSearchList.add(rs.getString("emp_fname") + " " + rs.getString("emp_lname"));
					}
				}*/
			}
			
			StringBuilder sbData = null;
			Iterator<String> it = setSearchList.iterator();
			while (it.hasNext()) {
				String strData = it.next();
				if(sbData == null) {
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null) {
				sbData = new StringBuilder();
			}
			request.setAttribute("sbData", sbData.toString());
				
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
//	private void updateUserAlerts() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
//		try {
//			con = db.makeConnection(con);
//			
//			String strDomain = request.getServerName().split("\\.")[0];
//			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//			userAlerts.setStrDomain(strDomain);
//			userAlerts.setStrEmpId(""+nEmpId);
//			if(strSessionUserType != null && strSessionUserType.equals(HRMANAGER)) {
//				userAlerts.set_type(HR_REVIEW_ALERT);
//			} else if(strSessionUserType != null && strSessionUserType.equals(MANAGER)) {
//				userAlerts.set_type(MANAGER_REVIEW_ALERT);
//			} else if(strSessionUserType != null && strSessionUserType.equals(EMPLOYEE)) {
//				userAlerts.set_type(MY_REVIEW_ALERT);
//			} else if(strSessionUserType != null && strSessionUserType.equals(CEO)) {
//				userAlerts.set_type(CEO_REVIEW_ALERT);
//			} else if(strSessionUserType != null && strSessionUserType.equals(HOD)) {
//				userAlerts.set_type(HOD_REVIEW_ALERT);
//			}
//			userAlerts.setStatus(UPDATE_ALERT);
//			Thread t = new Thread(userAlerts);
//			t.run();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	
	private void getEmpScoreAvgAppraisalWise(Connection con, UtilityFunctions uF) {
//		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
//		UtilityFunctions uF = new UtilityFunctions();
		try {
			Map<String, String> hmScoreAggregateMap = new HashMap<String, String>();
			pst = con.prepareStatement(" select *,(marks*100/weightage) as average from (select sum(marks) as marks, sum(weightage) as weightage," +
				"appraisal_id,emp_id,appraisal_freq_id from appraisal_question_answer aqw where weightage>0 group by appraisal_id,emp_id,appraisal_freq_id) as a");
//			pst = con.prepareStatement("select sum(marks) as marks, sum(weightage) as weightage, appraisal_id,emp_id,appraisal_freq_id from " +
//				" appraisal_question_answer aqw group by appraisal_id,emp_id,appraisal_freq_id");
			//and aqw.appraisal_attribute = ? where user_id = ? and user_type_id = ?
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			pst.setInt(2, uF.parseToInt(strSessionUserTypeID));
//			System.out.println("pst =====> "+ pst);
			rs = pst.executeQuery();
				while (rs.next()) {
//					double dblAggregate = 0.0d;
//					if(uF.parseToDouble(rs.getString("weightage"))>0) {
//						dblAggregate = (uF.parseToDouble(rs.getString("marks")) * 100) / uF.parseToDouble(rs.getString("weightage"));
//					}
//					hmScoreAggregateMap.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("emp_id"), uF.formatIntoOneDecimalWithOutComma(dblAggregate));
					hmScoreAggregateMap.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("emp_id"), uF.showData(rs.getString("average"), "0"));
				}
				rs.close();
				pst.close();
//			System.out.println("hmScoreAggregateMap =====> "+ hmScoreAggregateMap);
			request.setAttribute("hmScoreAggregateMap", hmScoreAggregateMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}


	
	public void getAppraisalDetails() {
		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
//		System.out.println("in appraisal .......... ");
		try {
			con = db.makeConnection(con);
			
			getEmpScoreAvgAppraisalWise(con, uF);
			
			Map<String, String> hmDesignation = CF.getDesigMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmLevelMap = getLevelMap(con);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			// Map<String, String> mp = new HashMap<String, String>();
			// Map<String, String> hmEmpDepartment = CF.getEmpDepartmentMap();
			// Map<String, String> mpdepart = CF.getDeptMap();
			Map<String, String> orientationMp = getOrientationValue(con);
			Map<String, String> hmOrientMemberID = CF.getOrientMemberID(con);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			// String wlocation = getUserlocation(con);
			// String departId = hmEmpDepartment.get(strSessionEmpId);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);

			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			Map<String, String> hmFreq = new HashMap<String, String>();
			while (rs.next()) {
				hmFreq.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();
			
			List<String> alEmpId = null;
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {	
				pst = con.prepareStatement("select eod.emp_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id " +
						"and upper(epd.emp_fname)|| ' '||upper(epd.emp_lname) like'%"+getStrSearchJob().trim().toUpperCase()+"%'");
				rs = pst.executeQuery();
				while(rs.next()) {
					if(alEmpId == null) {
						alEmpId = new ArrayList<String>();
						alEmpId.add(rs.getString("emp_id"));
					} else {
						alEmpId.add(rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
			}
			
			List<String> alFrequencyId = null;
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {	
				pst = con.prepareStatement("select * from appraisal_frequency where frequency_name is not null " +
						"and frequency_name != '' and upper(frequency_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'");
				rs = pst.executeQuery();
				while(rs.next()) {
					if(alFrequencyId == null) {
						alFrequencyId = new ArrayList<String>();
						alFrequencyId.add(rs.getString("appraisal_frequency_id"));
					} else {
						alFrequencyId.add(rs.getString("appraisal_frequency_id"));
					}
				}
				rs.close();
				pst.close();
			}
			
			List<String> alOrientationId = null;
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {	
				pst = con.prepareStatement("select * from apparisal_orientation  where orientation_name is not null " +
						"and orientation_name != '' and upper(orientation_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'");
				rs = pst.executeQuery();
				while(rs.next()) {
					if(alOrientationId == null) {
						alOrientationId = new ArrayList<String>();
						alOrientationId.add(rs.getString("apparisal_orientation_id"));
					} else {
						alOrientationId.add(rs.getString("apparisal_orientation_id"));
					}
				}
				rs.close();
				pst.close();
			}

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select distinct(appraisal_id) as appraisal_id from appraisal_reviewee_details where appraisal_id>0 ");
			if (strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(HRMANAGER) || strSessionUserType.equalsIgnoreCase(ADMIN))) {
				sbQuery.append(" and hr_ids like '%,"+strSessionEmpId+",%' ");
			} else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(MANAGER) && getCurrUserType() != null && !getCurrUserType().equals(strBaseUserType)) {
				sbQuery.append(" and (supervisor_ids like '%,"+strSessionEmpId+",%' or grand_supervisor_ids like '%,"+strSessionEmpId+",%') ");
			} else if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(CEO) && getCurrUserType()!=null && getCurrUserType().equals(strBaseUserType)) {
				sbQuery.append(" and ceo_ids like '%,"+strSessionEmpId+",%' ");
			} else if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(HOD) && getCurrUserType()!=null && getCurrUserType().equals(strBaseUserType)) {
				sbQuery.append(" and hod_ids like '%,"+strSessionEmpId+",%' ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("appraisal pst======>"+pst);
			StringBuilder sbAppraisalIds = null;
			rs=pst.executeQuery();
			while(rs.next()) {
				if(sbAppraisalIds == null) {
					sbAppraisalIds = new StringBuilder();
					sbAppraisalIds.append(rs.getString("appraisal_id"));
				} else {
					sbAppraisalIds.append(","+rs.getString("appraisal_id"));
				}
			}
			rs.close();
			pst.close();
			
			if(sbAppraisalIds != null) {
				sbQuery = new StringBuilder();
				sbQuery.append("select count(*) as cnt from appraisal_details a, appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id" 
						+" and (is_delete is null or is_delete = false) and is_appraisal_publish=true and is_appraisal_close = false and a.appraisal_details_id in ("+sbAppraisalIds.toString()+") ");
				/*if (strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(HRMANAGER) || strSessionUserType.equalsIgnoreCase(ADMIN))) {
					sbQuery.append(" and hr_ids like '%,"+strSessionEmpId+",%' ");
				} else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(MANAGER) && getCurrUserType() != null && !getCurrUserType().equals(strBaseUserType)) {
					sbQuery.append(" and supervisor_id like '%,"+strSessionEmpId+",%' ");
				} else if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(CEO) && getCurrUserType()!=null && getCurrUserType().equals(strBaseUserType)) {
					sbQuery.append(" and ceo_ids like '%,"+strSessionEmpId+",%' ");
				} else if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(HOD) && getCurrUserType()!=null && getCurrUserType().equals(strBaseUserType)) {
					sbQuery.append(" and hod_ids like '%,"+strSessionEmpId+",%' ");
				}*/
				if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") && !getStrSearchJob().trim().equalsIgnoreCase("null")) {
					sbQuery.append(" and (upper(appraisal_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(appraisal_type) like '%"+getStrSearchJob().trim().toUpperCase()+"%' ");
					if(alEmpId !=null && alEmpId.size() > 0) {
						sbQuery.append(" or (");
			            for(int i=0; i<alEmpId.size(); i++) {
			               sbQuery.append(" self_ids like '%,"+alEmpId.get(i).trim()+",%'");
			               if(i<alEmpId.size()-1) {
			                  sbQuery.append(" OR "); 
			                }
			            }
				        sbQuery.append(" ) ");
					}
					if(alFrequencyId !=null && alFrequencyId.size() > 0) {
						sbQuery.append(" or (");
			            for(int i=0; i<alFrequencyId.size(); i++) {
			               sbQuery.append(" frequency like '%"+alFrequencyId.get(i).trim()+"%'");
			               if(i<alFrequencyId.size()-1) {
			                  sbQuery.append(" OR "); 
			                }
			            }
				        sbQuery.append(" ) ");
					}
					if(alOrientationId !=null && alOrientationId.size() > 0) {
						sbQuery.append(" or (");
			            for(int i=0; i<alOrientationId.size(); i++) {
			               sbQuery.append(" oriented_type like '%"+alOrientationId.get(i).trim()+"%'");
			               if(i<alOrientationId.size()-1) {
			                  sbQuery.append(" OR "); 
			                }
			            }
				        sbQuery.append(" ) ");
					}
					sbQuery.append(")");
				}
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
//				System.out.println("appraisal pst======>"+pst);
				rs=pst.executeQuery();
				int proCount = 0;
				int proCnt = 0;
				while(rs.next()) {
					proCnt = rs.getInt("cnt");
					proCount = rs.getInt("cnt")/10;
					if(rs.getInt("cnt")%10 != 0) {
						proCount++;
					}
				}
				rs.close();
				pst.close();
				request.setAttribute("proCount", proCount+"");
				request.setAttribute("proCnt", proCnt+"");
				
				sbQuery = new StringBuilder();
	//			sbQuery.append("select * from appraisal_details where is_publish=true and is_close = false ");
				sbQuery.append("select * from appraisal_details a, appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id" 
						+" and (is_delete is null or is_delete = false) and is_appraisal_publish=true and is_appraisal_close = false and a.appraisal_details_id in ("+sbAppraisalIds.toString()+") ");
				/*if (strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(HRMANAGER) || strSessionUserType.equalsIgnoreCase(ADMIN))) {
					sbQuery.append(" and hr_ids like '%,"+strSessionEmpId+",%' ");
				} else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(MANAGER) && getCurrUserType() != null && !getCurrUserType().equals(strBaseUserType)) {
					sbQuery.append(" and supervisor_id like '%,"+strSessionEmpId+",%' ");
				} else if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(CEO) && getCurrUserType()!=null && getCurrUserType().equals(strBaseUserType)) {
					sbQuery.append(" and ceo_ids like '%,"+strSessionEmpId+",%' ");
				} else if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(HOD) && getCurrUserType()!=null && getCurrUserType().equals(strBaseUserType)) {
					sbQuery.append(" and hod_ids like '%,"+strSessionEmpId+",%' ");
				}*/
				/*if (strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(HRMANAGER) || strSessionUserType.equalsIgnoreCase(ADMIN))) {
					sbQuery.append(" and hr_ids like '%,"+strSessionEmpId+",%' ");
				} else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(MANAGER)) {
					sbQuery.append(" and supervisor_id like '%,"+strSessionEmpId+",%' ");
				} else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(CEO)) {
					sbQuery.append(" and ceo_ids like '%,"+strSessionEmpId+",%' ");
				} else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(HOD)) {
					sbQuery.append(" and hod_ids like '%,"+strSessionEmpId+",%' ");
				}*/
				
				if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") && !getStrSearchJob().trim().equalsIgnoreCase("null")) {
					sbQuery.append(" and (upper(appraisal_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(appraisal_type) like '%"+getStrSearchJob().trim().toUpperCase()+"%' ");
					if(alEmpId !=null && alEmpId.size() > 0) {
						sbQuery.append(" or (");
			            for(int i=0; i<alEmpId.size(); i++) {
			               sbQuery.append(" self_ids like '%,"+alEmpId.get(i).trim()+",%'");
			               if(i<alEmpId.size()-1) {
			                  sbQuery.append(" OR "); 
			                }
			            }
				        sbQuery.append(" ) ");
					}
					
					if(alFrequencyId !=null && alFrequencyId.size() > 0) {
						sbQuery.append(" or (");
			            for(int i=0; i<alFrequencyId.size(); i++) {
			               sbQuery.append(" frequency like '%"+alFrequencyId.get(i).trim()+"%'");
			               if(i<alFrequencyId.size()-1) {
			                  sbQuery.append(" OR "); 
			                }
			            }
				        sbQuery.append(" ) ");
					}
					
					if(alOrientationId !=null && alOrientationId.size() > 0) {
						sbQuery.append(" or (");
			            for(int i=0; i<alOrientationId.size(); i++) {
			               sbQuery.append(" oriented_type like '%"+alOrientationId.get(i).trim()+"%'");
			               if(i<alOrientationId.size()-1) {
			                  sbQuery.append(" OR "); 
			                }
			            }
				        sbQuery.append(" ) ");
					}
					sbQuery.append(")");
				}
				
				sbQuery.append(" order by to_date DESC,freq_end_date desc");
				int intOffset = uF.parseToInt(getMinLimit());
				sbQuery.append(" limit 10 offset "+intOffset+"");		
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("Appr/662--pst1 ======>> " + pst);
				rs = pst.executeQuery();
				Map<String, Map<String, String>> appraisalDetails = new HashMap<String, Map<String, String>>();
				List<String> appraisalIdList = new ArrayList<String>();
				
		//===start parvez date: 29-03-2022===
				Map<String,List<String>> hmAlMember = new HashMap<String, List<String>>();
		//===end parvez date: 29-03-2022====		
				
				while (rs.next()) {
					Map<String, String> appraisalMp = new HashMap<String, String>();
					appraisalIdList.add(rs.getString("appraisal_details_id")+"_"+rs.getString("appraisal_freq_id"));//0
					appraisalMp.put("ID", rs.getString("appraisal_details_id"));//1
					appraisalMp.put("APPRAISAL", rs.getString("appraisal_name"));//2
					appraisalMp.put("ORIENT", orientationMp.get(rs.getString("oriented_type")));//3
					appraisalMp.put("EMPLOYEE", uF.showData(getAppendData(rs.getString("self_ids"), hmEmpName), ""));//4
					appraisalMp.put("LEVEL", uF.showData(hmLevelMap.get(rs.getString("level_id")), ""));//5
					appraisalMp.put("DESIG", hmDesignation.get(rs.getString("desig_id")));//6
					appraisalMp.put("GRADE", hmGradeMap.get(rs.getString("grade_id")));//7
					appraisalMp.put("WLOCATION", rs.getString("wlocation_id"));//8
					appraisalMp.put("PEER", rs.getString("peer_ids"));//9
					appraisalMp.put("ANYONE", rs.getString("other_ids"));//10
					appraisalMp.put("SELFID", rs.getString("self_ids"));//11
					appraisalMp.put("SUPERVISORID", rs.getString("supervisor_id"));//12
					appraisalMp.put("HRID", rs.getString("hr_ids"));//13
					appraisalMp.put("FREQUENCY", hmFreq.get(rs.getString("frequency")));//14
					appraisalMp.put("FROM", uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));//15
					appraisalMp.put("TO", uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));//16
					//System.out.println("Appr/685--toDate="+uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
					appraisalMp.put("REVIEW_TYPE", rs.getString("appraisal_type"));//17
					
					appraisalMp.put("ORIENTED_TYPE", rs.getString("oriented_type"));//18
					appraisalMp.put("CEOID", rs.getString("ceo_ids"));//19
					appraisalMp.put("HODID", rs.getString("hod_ids"));//20
					appraisalMp.put("APP_FREQ_ID", rs.getString("appraisal_freq_id"));//21
					appraisalMp.put("APP_FREQ_NAME", rs.getString("appraisal_freq_name"));//22
					appraisalMp.put("FREQ_START", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, CF.getStrReportDateFormat()));//23
					appraisalMp.put("FREQ_TO", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, CF.getStrReportDateFormat()));//24
					appraisalMp.put("APP_FREQ_ISPUBLISH", rs.getString("is_appraisal_publish"));//25
					appraisalMp.put("APP_FREQ_ISCLOSE", rs.getString("is_appraisal_close"));//26
					appraisalMp.put("APP_FREQ_EXPIRE", rs.getString("freq_publish_expire_status"));//27
					appraisalMp.put("APP_USERTYPES_FOR_FEEDBACK", rs.getString("user_types_for_feedback"));//28
			//===start parvez date: 29-03-2022===		
					
					List<String> alMemberList1 = new ArrayList<String>();
					if (rs.getString("usertype_member") != null && !rs.getString("usertype_member").equals("")) {
						alMemberList1 = Arrays.asList(rs.getString("usertype_member").split(","));
					}
					
					hmAlMember.put(rs.getString("appraisal_details_id"), alMemberList1);
					
			//===end parvez date: 29-03-2022===		
					appraisalDetails.put(rs.getString("appraisal_details_id")+"_"+rs.getString("appraisal_freq_id"), appraisalMp);
				}
				rs.close();
				pst.close();
				
				request.setAttribute("appraisalIdList", appraisalIdList);
				request.setAttribute("appraisalDetails", appraisalDetails);
				
		//===start parvez date: 29-03-2022===		
				request.setAttribute("hmAlMember", hmAlMember);
				
//				Map<String, List<String>> hmExistOrientationMember = new HashMap<String, List<String>>();
				sbQuery = new StringBuilder();
				sbQuery.append("select * from appraisal_reviewee_details ard,appraisal_main_level_details amld where ard.appraisal_id=amld.appraisal_id" 
						+" and ard.appraisal_id in ("+sbAppraisalIds.toString()+") ");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("App/730---pst="+pst);
				rs = pst.executeQuery();
				Map<String,String> hmPriorUserType = new HashMap<String, String>();
				while (rs.next()) {
					List<String> alPriorUserList = new ArrayList<String>();
					if(rs.getString("supervisor_ids") != null && !rs.getString("supervisor_ids").equals("")){
						alPriorUserList.add(rs.getString("manager"));
					}
					if(rs.getString("hod_ids") != null && !rs.getString("hod_ids").equals("")){
						alPriorUserList.add(rs.getString("hod"));
					}
					if(rs.getString("ceo_ids") != null && !rs.getString("ceo_ids").equals("")){
						alPriorUserList.add(rs.getString("ceo"));
					}
					if(rs.getString("hr_ids") != null && !rs.getString("hr_ids").equals("")){
						alPriorUserList.add(rs.getString("hr"));
					}
					if(rs.getString("peer_ids") != null && !rs.getString("peer_ids").equals("")){
						alPriorUserList.add(rs.getString("peer"));
					}
					if(rs.getString("other_peer_ids") != null && !rs.getString("other_peer_ids").equals("")){
						alPriorUserList.add(rs.getString("other_peer"));
					}
					if(rs.getString("subordinate_ids") != null && !rs.getString("subordinate_ids").equals("")){
						alPriorUserList.add(rs.getString("subordinate"));
					}
					
					Collections.sort(alPriorUserList,Collections.reverseOrder());
					
//					hmExistOrientationMember.put(rs.getString("appraisal_id")+"_"+rs.getString("reviewee_id"), alPriorUserList);
					
					int priorUserTypeId = 0;
					
					if(alPriorUserList != null && !alPriorUserList.isEmpty()){
						if(uF.parseToInt(rs.getString("manager")) == uF.parseToInt(alPriorUserList.get(0))){
							priorUserTypeId = 2;
						} else if(uF.parseToInt(rs.getString("hod")) == uF.parseToInt(alPriorUserList.get(0))){
							priorUserTypeId = 13;
						} else if(uF.parseToInt(rs.getString("ceo")) == uF.parseToInt(alPriorUserList.get(0))){
							priorUserTypeId = 5;
						} else if(uF.parseToInt(rs.getString("hr")) == uF.parseToInt(alPriorUserList.get(0))){
							priorUserTypeId = 7;
						} else if(uF.parseToInt(rs.getString("peer")) == uF.parseToInt(alPriorUserList.get(0))){
							priorUserTypeId = 4;
						} else if(uF.parseToInt(rs.getString("other_peer")) == uF.parseToInt(alPriorUserList.get(0))){
							priorUserTypeId = 14;
						} else if(uF.parseToInt(rs.getString("subordinate")) == uF.parseToInt(alPriorUserList.get(0))){
							priorUserTypeId = 6;
						}
					}
					
					hmPriorUserType.put(rs.getString("appraisal_id")+"_"+rs.getString("reviewee_id"), priorUserTypeId+"");
					
				}
				rs.close();
				pst.close();
				
				request.setAttribute("hmPriorUserType", hmPriorUserType);
//				request.setAttribute("hmExistOrientationMember", hmExistOrientationMember);
				
				sbQuery = new StringBuilder();
				sbQuery.append("select * from appraisal_question_answer where is_submit=true and appraisal_id in ("+sbAppraisalIds.toString()+") ");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("App/790---pst="+pst);
				rs = pst.executeQuery();
				Map<String,String> existFeedback = new HashMap<String, String>();
				while(rs.next()){
					existFeedback.put(rs.getString("appraisal_id")+"_"+rs.getString("user_type_id")+"_"+rs.getString("emp_id")+"_"+rs.getString("reviewer_or_appraiser"), rs.getString("appraisal_question_answer_id"));
				}
				rs.close();
				pst.close();
				
				request.setAttribute("existFeedback", existFeedback);
				
		//===end parvez date: 29-03-2022===		
				
			//===start parvez date: 28-12-2021===
				getNewGoalDetails(appraisalIdList,appraisalDetails,con,uF);
			//===end parvez date: 28-12-2021===
	
//				System.out.println("appraisalIdList==>"+appraisalIdList);
				Map<String, Map<String, Map<String, String>>> appraisalStatusMp = getEmployeeStatus(uF);
				request.setAttribute("appraisalStatusMp", appraisalStatusMp);
	
				Map<String, Map<String, List<String>>> empMpDetails = new HashMap<String, Map<String, List<String>>>();
				Map<String, Map<String, List<String>>> hmRemainOrientDetailsAppWise = new LinkedHashMap<String, Map<String,List<String>>>();
				Map<String, Map<String, List<String>>> hmRemainOrientDetailsForSelfAppWise = new LinkedHashMap<String, Map<String,List<String>>>();
				Map<String, Map<String, List<String>>> hmRemainOrientDetailsForPeerAppWise = new LinkedHashMap<String, Map<String,List<String>>>();
				Map<String, Map<String,List<String>>> hmExistOrientTypeAQAAppWise = new HashMap<String, Map<String,List<String>>>();
				
				Map<String, List<String>> hmExistSectionID = new HashMap<String, List<String>>();
				sbQuery = new StringBuilder();
				sbQuery.append("select distinct (section_id),emp_id,appraisal_id, appraisal_freq_id,user_type_id from appraisal_question_answer " +
					"where is_submit=true and user_id = ? ");
				if(strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(ADMIN) || strSessionUserType.equalsIgnoreCase(HRMANAGER))){
					sbQuery.append(" and user_type_id = "+uF.parseToInt(hmUserTypeId.get(HRMANAGER))+" ");
				} else if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
					sbQuery.append(" and user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(MANAGER)) {
					sbQuery.append(" and (user_type_id = "+uF.parseToInt(strSessionUserTypeID)+" or user_type_id = 8) ");
				} else {
					sbQuery.append(" and user_type_id = "+uF.parseToInt(strSessionUserTypeID)+" ");
				}
				sbQuery.append(" group by section_id,emp_id,appraisal_id,user_id,user_type_id,appraisal_freq_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				rs = pst.executeQuery();
//				System.out.println("pst1 ::: "+pst);
				while (rs.next()) {
					List<String> sectionIDList = hmExistSectionID.get(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("user_type_id")+"_"+rs.getString("emp_id"));
					if(sectionIDList==null) sectionIDList = new ArrayList<String>();				
					sectionIDList.add(rs.getString("section_id"));
					hmExistSectionID.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("user_type_id")+"_"+rs.getString("emp_id"), sectionIDList);
				}
				rs.close();
				pst.close();
				request.setAttribute("hmExistSectionID", hmExistSectionID);
//				System.out.println("hmExistSectionID==>"+hmExistSectionID);
				
				Map<String, Integer> hmSectionCount = new HashMap<String, Integer>();
			//===start parvez date: 17-03-2023===	
//				pst = con.prepareStatement("select count(main_level_id) as sectionCnt, appraisal_id from appraisal_main_level_details group by appraisal_id");
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) && 
						strBaseUserType != null && strBaseUserType.equalsIgnoreCase(HOD) && getCurrUserType()!=null && getCurrUserType().equals(strBaseUserType)){
					pst = con.prepareStatement("select count(main_level_id) as sectionCnt, appraisal_id from appraisal_main_level_details " +
							" where main_level_id not in (select main_level_id from appraisal_level_details where appraisal_system!=2) group by appraisal_id");
				}else{
					pst = con.prepareStatement("select count(main_level_id) as sectionCnt, appraisal_id from appraisal_main_level_details group by appraisal_id");
				}
			//===end parvez date: 17-03-2023===	
				rs = pst.executeQuery();
				while (rs.next()) {
					hmSectionCount.put(rs.getString("appraisal_id"), rs.getInt("sectionCnt"));
				}
				rs.close();
				pst.close();
				
				
				Map<String, List<String>> hmEmpKRAIds = (Map<String, List<String>>) request.getAttribute("hmEmpKRAIds");
				Map<String, List<String>> hmEmpGoarTargetIds = (Map<String, List<String>>) request.getAttribute("hmEmpGoarTargetIds");
				
				Map<String, String> hmQueCount = new HashMap<String, String>();
//				pst = con.prepareStatement("select count(appraisal_question_details_id) as queCnt, appraisal_id from appraisal_question_details group by appraisal_id");
				pst = con.prepareStatement("select appraisal_question_details_id, aqd.appraisal_id,reviewee_id,app_system_type,kra_id,goal_kra_target_id" +
						" from appraisal_question_details aqd, appraisal_reviewee_details ard where aqd.appraisal_id=ard.appraisal_id");
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> alKRAIds = hmEmpKRAIds.get(rs.getString("reviewee_id"));
					if(rs.getInt("app_system_type")==4 && alKRAIds!=null && rs.getString("kra_id")!=null && !alKRAIds.contains(rs.getString("kra_id"))) {
						continue;
					}
					List<String> alGoarTargetIds = hmEmpGoarTargetIds.get(rs.getString("reviewee_id"));
					if((rs.getInt("app_system_type")==3 || rs.getInt("app_system_type")==5) && alGoarTargetIds!=null && rs.getString("goal_kra_target_id")!=null && !alGoarTargetIds.contains(rs.getString("goal_kra_target_id"))) {
						continue;
					}
				//===start parvez date: 17-03-2023===	
					if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) && rs.getInt("app_system_type")!=0 && rs.getInt("app_system_type")!=2
							 && strBaseUserType != null && strBaseUserType.equalsIgnoreCase(HOD) && getCurrUserType()!=null && getCurrUserType().equals(strBaseUserType)){
						continue;
					}
				//===end parvez date: 17-03-2023===	
					int intCnt = uF.parseToInt(hmQueCount.get(rs.getString("appraisal_id")+"_"+rs.getString("reviewee_id")));
					intCnt++;
					hmQueCount.put(rs.getString("appraisal_id")+"_"+rs.getString("reviewee_id"), intCnt+"");
				}
				rs.close();
				pst.close();
				
				Map<String, Integer> hmExistSectionCount = new HashMap<String, Integer>();
				sbQuery = new StringBuilder();
				sbQuery.append("select count(distinct section_id) as existSectionCnt,emp_id, appraisal_id, appraisal_freq_id,user_type_id from appraisal_question_answer " +
					" where is_submit=true and user_id=? ");
				if(strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(ADMIN) || strSessionUserType.equalsIgnoreCase(HRMANAGER))){
					sbQuery.append(" and user_type_id = "+uF.parseToInt(hmUserTypeId.get(HRMANAGER))+" ");
				} else if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
					sbQuery.append(" and user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(MANAGER)) {
					sbQuery.append(" and (user_type_id = "+uF.parseToInt(strSessionUserTypeID)+" or user_type_id = 8) ");
				} else {
					sbQuery.append(" and user_type_id = "+uF.parseToInt(strSessionUserTypeID)+" ");
				}
				sbQuery.append(" group by appraisal_id,emp_id,user_type_id,appraisal_freq_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				rs = pst.executeQuery();
//				System.out.println("hmExistSectionCount pst2 ::: "+pst);
				while (rs.next()) {
					hmExistSectionCount.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("user_type_id")+"_"+rs.getString("emp_id"), rs.getInt("existSectionCnt"));
				}
				rs.close();
				pst.close();
				
				
				Map<String, Integer> hmExistQueCount = new HashMap<String, Integer>();
				sbQuery = new StringBuilder();
				sbQuery.append("select count(distinct appraisal_question_details_id) as existQueCnt,emp_id, appraisal_id, appraisal_freq_id,user_type_id from appraisal_question_answer " +
					" where is_submit=true and user_id=? ");
				if(strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(ADMIN) || strSessionUserType.equalsIgnoreCase(HRMANAGER))){
					sbQuery.append(" and user_type_id = "+uF.parseToInt(hmUserTypeId.get(HRMANAGER))+" ");
				} else if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
					sbQuery.append(" and user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(MANAGER)) {
					sbQuery.append(" and (user_type_id = "+uF.parseToInt(strSessionUserTypeID)+" or user_type_id = 8) ");
				} else {
					sbQuery.append(" and user_type_id = "+uF.parseToInt(strSessionUserTypeID)+" ");
				}
				sbQuery.append(" and section_comment is not null and section_comment != '' group by appraisal_id,emp_id,user_type_id,appraisal_freq_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				rs = pst.executeQuery();
//				System.out.println("hmExistSectionCount pst2 ::: "+pst);
				while (rs.next()) {
					hmExistQueCount.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("user_type_id")+"_"+rs.getString("emp_id"), rs.getInt("existQueCnt"));
				}
				rs.close();
				pst.close();
//				System.out.println(" hmExistQueCount::: "+hmExistQueCount);
				
				Map<String, String> hmUsersFeedbackReopenComment = new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select reopen_comment, emp_id, review_id, review_freq_id,user_type_id from review_feedback_reopen_details where user_id =? ");
				if(strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(ADMIN) || strSessionUserType.equalsIgnoreCase(HRMANAGER))) {
					sbQuery.append(" and user_type_id = "+uF.parseToInt(hmUserTypeId.get(HRMANAGER))+" ");
				} else if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
					sbQuery.append(" and user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(MANAGER)) {
					sbQuery.append(" and (user_type_id = "+uF.parseToInt(strSessionUserTypeID)+" or user_type_id = 8) ");
				} else {
					sbQuery.append(" and user_type_id = "+uF.parseToInt(strSessionUserTypeID)+" ");
				}
//				sbQuery.append(" group by review_id,emp_id,user_type_id,review_freq_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				rs = pst.executeQuery();
//				System.out.println("hmExistSectionCount pst2 ::: "+pst);
				while (rs.next()) {
					hmUsersFeedbackReopenComment.put(rs.getString("review_id")+"_"+rs.getString("review_freq_id")+"_"+rs.getString("user_type_id")+"_"+rs.getString("emp_id"), rs.getString("reopen_comment"));
				}
				rs.close();
				pst.close();
				request.setAttribute("hmUsersFeedbackReopenComment", hmUsersFeedbackReopenComment);
				
				
				int reviewEmpCount = 0;
				Map<String, String> hmApprovedFeedback = new HashMap<String, String>();
				for (int i = 0; appraisalIdList != null && i < appraisalIdList.size(); i++) {
					Map<String, String> appraisalMp = appraisalDetails.get(appraisalIdList.get(i));
					Map<String, List<String>> empMp = new HashMap<String, List<String>>();
					
					Map<String, Map<String, String>> userTypeMp = appraisalStatusMp.get(appraisalIdList.get(i));
					if(userTypeMp == null) userTypeMp = new HashMap<String, Map<String, String>>();
					String appId = appraisalIdList.get(i);
					if(appId != null && !appId.equals("")) {
						appId = appId.substring(0,appId.lastIndexOf("_"));
					}
					
					String self = appraisalMp.get("SELFID");
					self=self!=null && !self.equals("") ? self.substring(1,self.length()-1) : "";
					int oriented_type = uF.parseToInt(appraisalMp.get("ORIENTED_TYPE"));
					List<String> memberList = CF.getOrientationMemberDetails(con,oriented_type);
					request.setAttribute("memberList", memberList);
//					System.out.println("Ap/874---memberList="+memberList);

					
					Map<String, List<String>> hmRevieweeAndUsersIds = new HashMap<String, List<String>>();
					sbQuery = new StringBuilder();
					sbQuery.append("select * from appraisal_reviewee_details where appraisal_id=? ");
					if(strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(ADMIN) || strSessionUserType.equalsIgnoreCase(HRMANAGER))) {
						sbQuery.append(" and hr_ids like '%,"+strSessionEmpId+",%' ");
					} else if(strSessionUserType != null && strSessionUserType.equalsIgnoreCase(MANAGER) && getCurrUserType()!=null && !getCurrUserType().equals(strBaseUserType)) {
						sbQuery.append(" and (supervisor_ids like '%,"+strSessionEmpId+",%' or grand_supervisor_ids like '%,"+strSessionEmpId+",%') ");
					} else if(strBaseUserType != null && strBaseUserType.equalsIgnoreCase(CEO) && getCurrUserType()!=null && getCurrUserType().equals(strBaseUserType)) {
						sbQuery.append(" and ceo_ids like '%,"+strSessionEmpId+",%' ");
					} else if(strBaseUserType != null && strBaseUserType.equalsIgnoreCase(HOD) && getCurrUserType()!=null && getCurrUserType().equals(strBaseUserType)) {
						sbQuery.append(" and hod_ids like '%,"+strSessionEmpId+",%' ");
					}
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt(appraisalMp.get("ID")));
//					System.out.println("pst ===>> " + pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("hr_ids"));
						innerList.add(rs.getString("supervisor_ids"));
						innerList.add(rs.getString("grand_supervisor_ids"));
						innerList.add(rs.getString("hod_ids"));
						innerList.add(rs.getString("ceo_ids"));
						hmRevieweeAndUsersIds.put(rs.getString("reviewee_id"), innerList);
							
						
					}
					rs.close();
					pst.close();
						
//					Map<String, String> hmApprovedFeedback = new HashMap<String, String>();
					pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id=? and appraisal_freq_id=?");
					pst.setInt(1, uF.parseToInt(appraisalMp.get("ID")));
					pst.setInt(2, uF.parseToInt(appraisalMp.get("APP_FREQ_ID")));
//					System.out.println("pst===>"+pst);
					rs = pst.executeQuery();
					while(rs.next()){
						
						if(hmApprovedFeedback.containsKey(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("emp_id")) && !uF.parseToBoolean(hmApprovedFeedback.get(rs.getString("appraisal_id")+"_"+rs.getString("emp_id")))){
							
						} else{
							hmApprovedFeedback.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("emp_id"),rs.getString("hr_approval"));
						}
//						System.out.println("Ap/1030---hmApprovedFeedback="+hmApprovedFeedback);
					}
					rs.close();
					pst.close();
					
//					System.out.println("hmApprovedFeedback="+hmApprovedFeedback);
					
					Map<String, List<String>> hmReviewerFeedback = new HashMap<String, List<String>>();
					pst = con.prepareStatement("select * from reviewer_feedback_details where appraisal_id=? and appraisal_freq_id=? and user_id=? and is_submit=true");
					pst.setInt(1, uF.parseToInt(appraisalMp.get("ID")));
					pst.setInt(2, uF.parseToInt(appraisalMp.get("APP_FREQ_ID")));
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
					rs = pst.executeQuery();
					while(rs.next()){
						List<String> alInnerList = hmReviewerFeedback.get(rs.getString("appraisal_id")+"_"+rs.getString("user_type_id"));
						if(alInnerList == null){
							alInnerList = new ArrayList<String>();
						}
						alInnerList.add(rs.getString("emp_id"));
						hmReviewerFeedback.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("user_type_id"), alInnerList);
					}
					rs.close();
					pst.close();
//					System.out.println("hmReviewerFeedback="+hmReviewerFeedback);
					
					Map<String, List<String>> hmRemainOrientDetails = new HashMap<String, List<String>>() ;
					List<String> employeeList = new ArrayList<String>();
					Iterator<String> it = hmRevieweeAndUsersIds.keySet().iterator();
					while (it.hasNext()) {
						String StrRevieweeId = it.next();
//						System.out.println("StrRevieweeId ===>> " + StrRevieweeId);
						List<String> innerList = hmRevieweeAndUsersIds.get(StrRevieweeId);
//						System.out.println("innerList.get(3) ===>> " + innerList.get(3));
						if (strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(HRMANAGER) || strSessionUserType.equalsIgnoreCase(ADMIN))) {
							List<String> hrIdsList = new ArrayList<String>();
							if(innerList.get(0) != null) {
								hrIdsList = Arrays.asList(innerList.get(0).split(","));
							}
							if(hrIdsList != null && hrIdsList.size() > 0) {
								if(hrIdsList != null && hrIdsList.contains(strSessionEmpId)) {
									Map<String, String> empstatusMp = userTypeMp.get(hmOrientMemberID.get("HR"));
									if(empstatusMp==null)empstatusMp = new HashMap<String, String>();
									employeeList = empMp.get(hmOrientMemberID.get("HR"));
									if(employeeList == null) employeeList = new ArrayList<String>();
									
									if (getDataType() != null && getDataType().equals("L") && empstatusMp != null && empstatusMp.get(StrRevieweeId+"_"+strSessionEmpId) == null) {
										employeeList.add(StrRevieweeId);
										reviewEmpCount++;
									} else if(hmExistSectionCount != null && !hmExistSectionCount.isEmpty()) {
										if(getDataType() != null && getDataType().equals("L") && hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("HR")+"_"+StrRevieweeId) != null && hmSectionCount.get(appId) != null && hmSectionCount.get(appId) != hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("HR")+"_"+StrRevieweeId)) {
											employeeList.add(StrRevieweeId);
											reviewEmpCount++;
										} else if(getDataType() != null && getDataType().equals("L") && (hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("HR")+"_"+StrRevieweeId) == null || (hmQueCount.get(appId+"_"+StrRevieweeId) != null && uF.parseToInt(hmQueCount.get(appId+"_"+StrRevieweeId)) != hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("HR")+"_"+StrRevieweeId)))) {
											employeeList.add(StrRevieweeId);
											reviewEmpCount++;
										} else if(getDataType() != null && getDataType().equals("C") && empstatusMp != null && empstatusMp.get(StrRevieweeId+"_"+strSessionEmpId) != null) {
											if(hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("HR")+"_"+StrRevieweeId) != null && hmSectionCount.get(appId) != null && hmSectionCount.get(appId) == hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("HR")+"_"+StrRevieweeId) && hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("HR")+"_"+StrRevieweeId) != null && hmQueCount.get(appId+"_"+StrRevieweeId) != null && uF.parseToInt(hmQueCount.get(appId+"_"+StrRevieweeId)) == hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("HR")+"_"+StrRevieweeId)) {
												employeeList.add(StrRevieweeId);
												reviewEmpCount++;
											}	
										}
									}
				//-----------------------------------------------------------------------------------
									getSecondMaxOrientNameAndIDs(con, 0, appraisalMp.get("ID"), StrRevieweeId, uF, hmRemainOrientDetails);
									/*if(uF.parseToInt(StrRevieweeId)==66){
										System.out.println("Ap/1096--hmRemainOrientDetails=="+hmRemainOrientDetails);
									}*/
									getExistOrientTypeInAQA(appraisalIdList.get(i), uF);
									empMp.put(hmOrientMemberID.get("HR"), employeeList);
								}
							}
							
						} else if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(HOD) && getCurrUserType()!=null && getCurrUserType().equals(strBaseUserType)) {
//							System.out.println("currUserType ===>> " + getCurrUserType());
							List<String> hodIdsList = new ArrayList<String>();
							if(innerList.get(3) != null) {
								hodIdsList = Arrays.asList(innerList.get(3).split(","));
							}
//							System.out.println("hodIdsList ===>> " + hodIdsList);
//							System.out.println("Id ===>> " + hodIdsList.contains(strSessionEmpId));
							if(hodIdsList != null && hodIdsList.size() > 0) {
								if(hodIdsList != null && hodIdsList.contains(strSessionEmpId)) {
									Map<String, String> empstatusMp = userTypeMp.get(hmOrientMemberID.get("HOD"));
									if(empstatusMp==null)empstatusMp = new HashMap<String, String>();
									employeeList = empMp.get(hmOrientMemberID.get("HOD"));
									if(employeeList == null) employeeList = new ArrayList<String>();
//									System.out.println("employeeList ===>> " + employeeList);
//									System.out.println("hmExistSectionCount="+hmExistSectionCount);
									
									List<String> existFeedbackEmp = hmReviewerFeedback.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("HOD"));
//									if(i==0){
//										System.out.println("App/1121--existFeedbackEmp="+existFeedbackEmp);
//										System.out.println("App/1122---empstatusMp="+empstatusMp);
//										System.out.println("App/1123---StrRevieweeId="+StrRevieweeId+"--getDataType()="+getDataType());
//										System.out.println("App/1123---hmExistSectionCount="+hmExistSectionCount);
//									}
									if (getDataType() != null && getDataType().equals("L") && empstatusMp != null && empstatusMp.get(StrRevieweeId+"_"+strSessionEmpId) == null) {
										
										if(existFeedbackEmp!=null && !existFeedbackEmp.contains(StrRevieweeId) && uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) || !uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) || existFeedbackEmp == null){
											employeeList.add(StrRevieweeId);
											reviewEmpCount++;
										}
										
									
//									} else if(hmExistSectionCount != null && !hmExistSectionCount.isEmpty()) {
									} else if(hmExistSectionCount != null && !hmExistSectionCount.isEmpty() || existFeedbackEmp!=null && existFeedbackEmp.contains(StrRevieweeId) && uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))) {
//										System.out.println("Ap/1132--reviewEmpCount employeeList ===>> " + employeeList);
										if(getDataType() != null && getDataType().equals("L") && hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("HOD")+"_"+StrRevieweeId) != null && hmSectionCount.get(appId) != null && hmSectionCount.get(appId) != hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("HOD")+"_"+StrRevieweeId)) {
											
											if(existFeedbackEmp!=null && !existFeedbackEmp.contains(StrRevieweeId) && uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) || !uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) || existFeedbackEmp == null){
												employeeList.add(StrRevieweeId);
												reviewEmpCount++;
											}
//											employeeList.add(StrRevieweeId);
//											reviewEmpCount++;
										} else if(getDataType() != null && getDataType().equals("L") && (hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("HOD")+"_"+StrRevieweeId) == null || (hmQueCount.get(appId+"_"+StrRevieweeId) != null && uF.parseToInt(hmQueCount.get(appId+"_"+StrRevieweeId)) != hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("HOD")+"_"+StrRevieweeId)))) {
											if(existFeedbackEmp!=null && !existFeedbackEmp.contains(StrRevieweeId) && uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) || !uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) || existFeedbackEmp == null){
												employeeList.add(StrRevieweeId);
												reviewEmpCount++;
											}
//											employeeList.add(StrRevieweeId);
//											reviewEmpCount++;
										} else if(getDataType() != null && getDataType().equals("C") && empstatusMp != null && empstatusMp.get(StrRevieweeId+"_"+strSessionEmpId) != null) {
//											System.out.println("App/1134---StrRevieweeId="+StrRevieweeId);
											if(uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) || hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("HOD")+"_"+StrRevieweeId) != null && hmSectionCount.get(appId) != null && hmSectionCount.get(appId) == hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("HOD")+"_"+StrRevieweeId) && hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("HOD")+"_"+StrRevieweeId) != null && hmQueCount.get(appId+"_"+StrRevieweeId) != null && uF.parseToInt(hmQueCount.get(appId+"_"+StrRevieweeId)) == hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("HOD")+"_"+StrRevieweeId)) {
												
												if(existFeedbackEmp!=null && existFeedbackEmp.contains(StrRevieweeId) && uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) || !uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){
													employeeList.add(StrRevieweeId);
													reviewEmpCount++;
												}
//												employeeList.add(StrRevieweeId);
//												reviewEmpCount++;
											}	
										}
									}
				//-----------------------------------------------------------------------------------
									getSecondMaxOrientNameAndIDs(con, 9, appraisalMp.get("ID"), StrRevieweeId, uF, hmRemainOrientDetails);
									/*if(uF.parseToInt(StrRevieweeId)==66){
										System.out.println("hmRemainOrientDetails=="+hmRemainOrientDetails);
									}*/
									getExistOrientTypeInAQA(appraisalIdList.get(i), uF);
									empMp.put(hmOrientMemberID.get("HOD"), employeeList);
								}
							}
						} else if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(CEO) && getCurrUserType()!=null && getCurrUserType().equals(strBaseUserType)) {
							List<String> ceoIdsList = new ArrayList<String>();
							if(innerList.get(4) != null) {
								ceoIdsList = Arrays.asList(innerList.get(4).split(","));
							}
							if(ceoIdsList != null && ceoIdsList.size() > 0) {
								if(ceoIdsList != null && ceoIdsList.contains(strSessionEmpId)) {
									Map<String, String> empstatusMp = userTypeMp.get(hmOrientMemberID.get("CEO"));
									if(empstatusMp==null)empstatusMp = new HashMap<String, String>();
									employeeList = empMp.get(hmOrientMemberID.get("CEO"));
									if(employeeList == null) employeeList = new ArrayList<String>();
									
									if (getDataType() != null && getDataType().equals("L") && empstatusMp != null && empstatusMp.get(StrRevieweeId+"_"+strSessionEmpId) == null) {
										employeeList.add(StrRevieweeId);
										reviewEmpCount++;
									} else if(hmExistSectionCount != null && !hmExistSectionCount.isEmpty()) {
										if(getDataType() != null && getDataType().equals("L") && hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("CEO")+"_"+StrRevieweeId) != null && hmSectionCount.get(appId) != null && hmSectionCount.get(appId) != hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("CEO")+"_"+StrRevieweeId)) {
											employeeList.add(StrRevieweeId);
											reviewEmpCount++;
										} else if(getDataType() != null && getDataType().equals("L") && (hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("CEO")+"_"+StrRevieweeId) == null || (hmQueCount.get(appId+"_"+StrRevieweeId) != null && uF.parseToInt(hmQueCount.get(appId+"_"+StrRevieweeId)) != hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("CEO")+"_"+StrRevieweeId)))) {
											employeeList.add(StrRevieweeId);
											reviewEmpCount++;
										} else if(getDataType() != null && getDataType().equals("C") && empstatusMp != null && empstatusMp.get(StrRevieweeId+"_"+strSessionEmpId) != null) {
											if(hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("CEO")+"_"+StrRevieweeId) != null && hmSectionCount.get(appId) != null && hmSectionCount.get(appId) == hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("CEO")+"_"+StrRevieweeId) && hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("CEO")+"_"+StrRevieweeId) != null && hmQueCount.get(appId+"_"+StrRevieweeId) != null && uF.parseToInt(hmQueCount.get(appId+"_"+StrRevieweeId)) == hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("CEO")+"_"+StrRevieweeId)) {
												employeeList.add(StrRevieweeId);
												reviewEmpCount++;
											}	
										}
									}
				//-----------------------------------------------------------------------------------
									
									getSecondMaxOrientNameAndIDs(con, 8, appraisalMp.get("ID"), StrRevieweeId, uF, hmRemainOrientDetails);
									/*if(uF.parseToInt(StrRevieweeId)==66){
										System.out.println("hmRemainOrientDetails=="+hmRemainOrientDetails);
									}*/
									getExistOrientTypeInAQA(appraisalIdList.get(i), uF);
									empMp.put(hmOrientMemberID.get("CEO"), employeeList);
								}
							}
						} else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(MANAGER) && getCurrUserType() != null && !getCurrUserType().equals(strBaseUserType)) {
							List<String> managerIdsList = new ArrayList<String>();
							if(innerList.get(1) != null) {
								managerIdsList = Arrays.asList(innerList.get(1).split(","));
							}
							if(managerIdsList != null && managerIdsList.size() > 0) {
								if(managerIdsList != null && managerIdsList.contains(strSessionEmpId)) {
									Map<String, String> empstatusMp = userTypeMp.get(hmOrientMemberID.get("Manager"));
									if(empstatusMp==null)empstatusMp = new HashMap<String, String>();
									employeeList = empMp.get(hmOrientMemberID.get("Manager"));
									if(employeeList == null) employeeList = new ArrayList<String>();
//									if(uF.parseToInt(StrRevieweeId)==661) {
//										System.out.println(strSessionEmpId + " --- StrRevieweeId ===>> " + StrRevieweeId);
//										System.out.println(strSessionEmpId + " --- empstatusMp ===>> " + empstatusMp);
//										System.out.println(strSessionEmpId + " --- getDataType ===>> " + getDataType());
//										System.out.println(strSessionEmpId + " --- hmExistSectionCount ===>> " + hmExistSectionCount);
//										System.out.println(strSessionEmpId + " --- hmSectionCount.get(appId) ===>> " + hmSectionCount.get(appId)+" --- " + hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Manager")+"_"+StrRevieweeId));
//									}
									if (getDataType() != null && getDataType().equals("L") && empstatusMp != null && empstatusMp.get(StrRevieweeId+"_"+strSessionEmpId) == null) {
										employeeList.add(StrRevieweeId);
										reviewEmpCount++;
									} else if(hmExistSectionCount != null && !hmExistSectionCount.isEmpty()) {
										if(getDataType() != null && getDataType().equals("L") && hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Manager")+"_"+StrRevieweeId) != null && hmSectionCount.get(appId) != null && hmSectionCount.get(appId) != hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Manager")+"_"+StrRevieweeId)) {
											employeeList.add(StrRevieweeId);
											reviewEmpCount++;
										} else if(getDataType() != null && getDataType().equals("L") && (hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Manager")+"_"+StrRevieweeId) == null || (hmQueCount.get(appId+"_"+StrRevieweeId) != null && uF.parseToInt(hmQueCount.get(appId+"_"+StrRevieweeId)) != hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Manager")+"_"+StrRevieweeId)))) {
											employeeList.add(StrRevieweeId);
											reviewEmpCount++;
										} else if(getDataType() != null && getDataType().equals("C") && empstatusMp != null && empstatusMp.get(StrRevieweeId+"_"+strSessionEmpId) != null) {
											if(hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Manager")+"_"+StrRevieweeId) != null && hmSectionCount.get(appId) != null && hmSectionCount.get(appId) == hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Manager")+"_"+StrRevieweeId) && hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Manager")+"_"+StrRevieweeId) != null && hmQueCount.get(appId+"_"+StrRevieweeId) != null && uF.parseToInt(hmQueCount.get(appId+"_"+StrRevieweeId)) == hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("Manager")+"_"+StrRevieweeId)) {
												employeeList.add(StrRevieweeId);
												reviewEmpCount++;
											}	
										}
									}
				//-----------------------------------------------------------------------------------
									getSecondMaxOrientNameAndIDs(con, 1, appraisalMp.get("ID"), StrRevieweeId, uF, hmRemainOrientDetails);
									getExistOrientTypeInAQA(appraisalIdList.get(i), uF);
									empMp.put(hmOrientMemberID.get("Manager"), employeeList);
								}
							}
							
//				------------------------------ Grand Supervisor ---------------------------
							
							List<String> gManagerIdsList = new ArrayList<String>();
							if(innerList.get(2) != null) {
								gManagerIdsList = Arrays.asList(innerList.get(2).split(","));
							}
							if(gManagerIdsList != null && gManagerIdsList.size() > 0) {
								if(gManagerIdsList != null && gManagerIdsList.contains(strSessionEmpId)) {
									Map<String, String> empstatusMp = userTypeMp.get(hmOrientMemberID.get("GroupHead"));
									if(empstatusMp==null)empstatusMp = new HashMap<String, String>();
									employeeList = empMp.get(hmOrientMemberID.get("GroupHead"));
									if(employeeList == null) employeeList = new ArrayList<String>();
									
									if (getDataType() != null && getDataType().equals("L") && empstatusMp != null && empstatusMp.get(StrRevieweeId+"_"+strSessionEmpId) == null) {
										employeeList.add(StrRevieweeId);
										reviewEmpCount++;
									} else if(hmExistSectionCount != null && !hmExistSectionCount.isEmpty()) {
										if(getDataType() != null && getDataType().equals("L") && hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("GroupHead")+"_"+StrRevieweeId) != null && hmSectionCount.get(appId) != null && hmSectionCount.get(appId) != hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("GroupHead")+"_"+StrRevieweeId)) {
											employeeList.add(StrRevieweeId);
											reviewEmpCount++;
										} else if(getDataType() != null && getDataType().equals("L") && (hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("GroupHead")+"_"+StrRevieweeId) == null || (hmQueCount.get(appId+"_"+StrRevieweeId) != null && uF.parseToInt(hmQueCount.get(appId+"_"+StrRevieweeId)) != hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("GroupHead")+"_"+StrRevieweeId)))) {
											employeeList.add(StrRevieweeId);
											reviewEmpCount++;
										} else if(getDataType() != null && getDataType().equals("C") && empstatusMp != null && empstatusMp.get(StrRevieweeId+"_"+strSessionEmpId) != null) {
											if(hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("GroupHead")+"_"+StrRevieweeId) != null && hmSectionCount.get(appId) != null && hmSectionCount.get(appId) == hmExistSectionCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("GroupHead")+"_"+StrRevieweeId) && hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("GroupHead")+"_"+StrRevieweeId) != null && hmQueCount.get(appId+"_"+StrRevieweeId) != null && uF.parseToInt(hmQueCount.get(appId+"_"+StrRevieweeId)) == hmExistQueCount.get(appraisalIdList.get(i)+"_"+hmOrientMemberID.get("GroupHead")+"_"+StrRevieweeId)) {
												employeeList.add(StrRevieweeId);
												reviewEmpCount++;
											}	
										}
									}
				//-----------------------------------------------------------------------------------
									getSecondMaxOrientNameAndIDs(con, 6, appraisalMp.get("ID"), StrRevieweeId, uF, hmRemainOrientDetails);
									getExistOrientTypeInAQA(appraisalIdList.get(i), uF);
									empMp.put(hmOrientMemberID.get("GroupHead"), employeeList);
								}
							}
							
						}
						
					}
//					System.out.println("Ap/1103---employeeList="+employeeList);
					
					
					
					if (strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(HRMANAGER) || strSessionUserType.equalsIgnoreCase(ADMIN))) {
	//					System.out.println("strSessionUserType==>"+strSessionUserType); 
//						if(hmOrientMemberID.get("HR") != null && memberList.contains(hmOrientMemberID.get("HR")) ) {
//								Map<String, String> empstatusMp = userTypeMp.get(hmOrientMemberID.get("HR"));
//								if(empstatusMp == null) empstatusMp = new HashMap<String, String>();
//								employeeList = new ArrayList<String>();
//								pst = con.prepareStatement("select distinct(emp_per_id) from employee_official_details eod,employee_personal_details epd,appraisal_details ad" +
//										" where epd.emp_per_id=eod.emp_id  and emp_per_id in ("+ self + ") and ad.hr_ids like '%,"+strSessionEmpId+",%' order by emp_per_id"); // and eod.wlocation_id=?
//			//					pst.setInt(1, uF.parseToInt(hmUserDetails.get("WLOCATION")));
//								rs = pst.executeQuery();
//	//							System.out.println("pst HRManager =====>"+pst);
//								while (rs.next()) {
//									if (getDataType() != null && getDataType().equals("L") && empstatusMp != null && empstatusMp.get(rs.getString("emp_per_id")+"_"+strSessionEmpId) == null) {
//	//									System.out.println("Emp ID in if "+rs.getString("emp_per_id"));
//										employeeList.add(rs.getString("emp_per_id"));
//										reviewEmpCount++;
//									} else if(hmExistSectionCount != null && !hmExistSectionCount.isEmpty()) {
//	//										System.out.println("Emp ID in else if "+rs.getString("emp_per_id"));
//											if(getDataType() != null && getDataType().equals("L") && hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id")) != null && hmSectionCount.get(appId) != null && hmSectionCount.get(appId) != hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id"))) {
//	//											System.out.println("hmExistSectionCount==="+hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id"))+"==>hmSectionCount==>"+hmSectionCount);
//												employeeList.add(rs.getString("emp_per_id"));
//												reviewEmpCount++;
//											} else if(getDataType() != null && getDataType().equals("C") && empstatusMp != null && empstatusMp.get(rs.getString("emp_per_id")+"_"+strSessionEmpId) != null) {
//												if(hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id")) != null && hmSectionCount.get(appId) != null && hmSectionCount.get(appId) == hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id"))) {
//													employeeList.add(rs.getString("emp_per_id"));
//													reviewEmpCount++;
//												}
//											}
//										
//									} else if(getDataType() != null && getDataType().equals("C") && empstatusMp != null && empstatusMp.get(rs.getString("emp_per_id")+"_"+strSessionEmpId) != null) {
//										employeeList.add(rs.getString("emp_per_id"));
//										reviewEmpCount++;
//									}
//								}
//								rs.close();
//								pst.close();
//			//---------------------------------------------------------------------------------------
//								getSecondMaxOrientNameAndIDs(con, 0,appId, uF);
//								getExistOrientTypeInAQA(appraisalIdList.get(i), uF);
//			//					System.out.println("orientPositionList.size() :::::::: "+orientPositionList.size());
//							
//								empMp.put(hmOrientMemberID.get("HR"), employeeList);
//						  }
						  
					} else if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(HOD) && getCurrUserType()!=null && getCurrUserType().equals(strBaseUserType)) {
	//					
//						if(hmOrientMemberID.get("HOD") != null && memberList!= null && memberList.contains(hmOrientMemberID.get("HOD"))) {
//								Map<String, String> empstatusMp = userTypeMp.get(hmOrientMemberID.get("HOD"));
//								if(empstatusMp==null)empstatusMp=new HashMap<String, String>();
//								employeeList = new ArrayList<String>();
//		
//								pst = con.prepareStatement("select distinct(emp_per_id) from employee_official_details eod,employee_personal_details epd,appraisal_details ad" +
//										" where epd.emp_per_id=eod.emp_id  and emp_per_id in("+ self + ") and ad.hod_ids like '%,"+strSessionEmpId+",%' order by emp_per_id"); //and eod.wlocation_id=? 
//								rs = pst.executeQuery();
//								while (rs.next()) {
//									if (getDataType() != null && getDataType().equals("L") && empstatusMp != null && empstatusMp.get(rs.getString("emp_per_id")+"_"+strSessionEmpId) == null) {
//										employeeList.add(rs.getString("emp_per_id"));
//										reviewEmpCount++;
//									} else if(hmExistSectionCount != null && !hmExistSectionCount.isEmpty()) {
//										if(getDataType() != null && getDataType().equals("L") && hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id")) != null && hmSectionCount.get(appId) != null && hmSectionCount.get(appId) != hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id"))) {
//											employeeList.add(rs.getString("emp_per_id"));
//											reviewEmpCount++;
//										} else if(getDataType() != null && getDataType().equals("C") && empstatusMp != null && empstatusMp.get(rs.getString("emp_per_id")+"_"+strSessionEmpId) != null) {
//											if(hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id")) != null && hmSectionCount.get(appId) != null && hmSectionCount.get(appId) == hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id"))) {
//												employeeList.add(rs.getString("emp_per_id"));
//												reviewEmpCount++;
//											}
//										}
//									} else if(getDataType() != null && getDataType().equals("C") && empstatusMp != null && empstatusMp.get(rs.getString("emp_per_id")+"_"+strSessionEmpId) != null) {
//										employeeList.add(rs.getString("emp_per_id"));
//										reviewEmpCount++;
//									}
//								}
//								rs.close();
//								pst.close();
//	//-----------------------------------------------------------------------------------
//								getSecondMaxOrientNameAndIDs(con, 9, appId, uF);
//								getExistOrientTypeInAQA(appraisalIdList.get(i), uF);
//								
//								empMp.put(hmOrientMemberID.get("HOD"), employeeList);
//							//}
//						}
							
						
					} else if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(CEO) && getCurrUserType()!=null && getCurrUserType().equals(strBaseUserType)) {
//						if(hmOrientMemberID.get("CEO") != null && memberList!= null && memberList.contains(hmOrientMemberID.get("CEO"))) {
//								Map<String, String> empstatusMp = userTypeMp.get(hmOrientMemberID.get("CEO"));
//								if(empstatusMp==null)empstatusMp=new HashMap<String, String>();
//								employeeList = new ArrayList<String>();
//								
//								pst = con.prepareStatement("select distinct(emp_per_id) from employee_official_details eod,employee_personal_details epd,appraisal_details ad" +
//										" where epd.emp_per_id=eod.emp_id  and emp_per_id in("+ self + ") and ad.ceo_ids like '%,"+strSessionEmpId+",%' order by emp_per_id"); //and eod.wlocation_id=? 
//								
//								rs = pst.executeQuery();
//								while (rs.next()) {
//									if (getDataType() != null && getDataType().equals("L") && empstatusMp != null && empstatusMp.get(rs.getString("emp_per_id")+"_"+strSessionEmpId) == null  ) {
//										employeeList.add(rs.getString("emp_per_id"));
//										reviewEmpCount++;
//									} else if(hmExistSectionCount != null && !hmExistSectionCount.isEmpty()) {
//										 if(getDataType() != null && getDataType().equals("L") && hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id")) != null && hmSectionCount.get(appId) != null && hmSectionCount.get(appId) != hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id"))) {
//											employeeList.add(rs.getString("emp_per_id"));
//											reviewEmpCount++;
//										} else if(getDataType() != null && getDataType().equals("C") && empstatusMp != null && empstatusMp.get(rs.getString("emp_per_id")+"_"+strSessionEmpId) != null) {
//											if(hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id")) != null && hmSectionCount.get(appId) != null && hmSectionCount.get(appId) == hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id"))) {
//												employeeList.add(rs.getString("emp_per_id"));
//												reviewEmpCount++;
//											}
//										}
//									} else if(getDataType() != null && getDataType().equals("C") && empstatusMp != null && empstatusMp.get(rs.getString("emp_per_id")+"_"+strSessionEmpId) != null) {
//										employeeList.add(rs.getString("emp_per_id"));
//										reviewEmpCount++;
//									}
//								}
//								rs.close();
//								pst.close();
//	//-----------------------------------------------------------------------------------
//								getSecondMaxOrientNameAndIDs(con, 8, appId, uF);
//								getExistOrientTypeInAQA(appraisalIdList.get(i), uF);
//								
//								empMp.put(hmOrientMemberID.get("CEO"), employeeList);
//							//}
//						}
					} else if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(MANAGER) && getCurrUserType() != null && !getCurrUserType().equals(strBaseUserType)) {
	//					System.out.println("in manager ..... ");
//						if(hmOrientMemberID.get("Manager") != null && memberList!= null && memberList.contains(hmOrientMemberID.get("Manager"))) {
//	//						System.out.println("in manager 1 ..... ");
//								Map<String, String> empstatusMp = userTypeMp.get(hmOrientMemberID.get("Manager"));
//								if(empstatusMp==null)empstatusMp=new HashMap<String, String>();
//	//							System.out.println(getDataType() + " --- in manager empstatusMp ===>> " + empstatusMp);
//								employeeList = new ArrayList<String>();
//								pst = con.prepareStatement("select distinct(emp_per_id) from employee_official_details eod,employee_personal_details epd,appraisal_details ad" +
//									" where epd.emp_per_id=eod.emp_id  and emp_per_id in("+ self + ") and ad.supervisor_id like '%,"+strSessionEmpId+",%' order by emp_per_id"); //and eod.wlocation_id=? 
//								rs = pst.executeQuery();
//	//							System.out.println("pst in manager ===>> " + pst);
//								while (rs.next()) {
//	//								System.out.println(getDataType() + " --- in manager empstatusMp.size() ===>> " + empstatusMp.size());
//									if (getDataType() != null && getDataType().equals("L") && (empstatusMp == null || empstatusMp.isEmpty() || empstatusMp.size() == 0 || empstatusMp.equals("") || (empstatusMp != null && empstatusMp.get(rs.getString("emp_per_id")+"_"+strSessionEmpId) == null))) {
//										employeeList.add(rs.getString("emp_per_id"));
//										reviewEmpCount++;
//									} else if(hmExistSectionCount != null && !hmExistSectionCount.isEmpty()) {
//										if(getDataType() != null && getDataType().equals("L") && hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id")) != null && hmSectionCount.get(appId) != null && hmSectionCount.get(appId) != hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id"))) {
//											employeeList.add(rs.getString("emp_per_id"));
//											reviewEmpCount++;
//										} else if(getDataType() != null && getDataType().equals("C") && empstatusMp != null && empstatusMp.get(rs.getString("emp_per_id")+"_"+strSessionEmpId) != null) {
//											if(hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id")) != null && hmSectionCount.get(appId) != null && hmSectionCount.get(appId) == hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id"))) {
//												employeeList.add(rs.getString("emp_per_id"));
//												reviewEmpCount++;
//											}
//										}
//									} else if(getDataType() != null && getDataType().equals("C") && empstatusMp != null && empstatusMp.get(rs.getString("emp_per_id")+"_"+strSessionEmpId) != null) {
//										employeeList.add(rs.getString("emp_per_id"));
//										reviewEmpCount++;
//									}
//								}
//								rs.close();
//								pst.close();
//	//-----------------------------------------------------------------------------------
//								getSecondMaxOrientNameAndIDs(con, 1,appId, uF);
//								getExistOrientTypeInAQA(appraisalIdList.get(i), uF);
//								
//								empMp.put(hmOrientMemberID.get("Manager"), employeeList);
//							//}
//						}
					}
					
					empMpDetails.put(appraisalIdList.get(i), empMp);
					
//					Map<String, List<String>> hmRemainOrientDetails = (Map<String, List<String>>)request.getAttribute("hmRemainOrientDetails");
					hmRemainOrientDetailsAppWise.put(appraisalIdList.get(i), hmRemainOrientDetails);
//					if(uF.parseToInt(appraisalIdList.get(i))==32) {
//						System.out.println(appraisalIdList.get(i)+" -- hmRemainOrientDetails ===>> " + hmRemainOrientDetails);
//					}
					Map<String, List<String>> hmRemainOrientDetailsForSelf = (Map<String, List<String>>)request.getAttribute("hmRemainOrientDetailsForSelf");
					hmRemainOrientDetailsForSelfAppWise.put(appraisalIdList.get(i), hmRemainOrientDetailsForSelf);
					
					Map<String, List<String>> hmRemainOrientDetailsForPeer = (Map<String, List<String>>)request.getAttribute("hmRemainOrientDetailsForPeer");
					hmRemainOrientDetailsForPeerAppWise.put(appraisalIdList.get(i), hmRemainOrientDetailsForPeer);
					
					Map<String,List<String>> hmExistOrientTypeAQA =  (Map<String, List<String>>)request.getAttribute("hmExistOrientTypeAQA");
					hmExistOrientTypeAQAAppWise.put(appraisalIdList.get(i), hmExistOrientTypeAQA);
					
//					System.out.println("empMpDetails ::::: "+ appraisalIdList.get(i)+" - " + empMpDetails);
					
					
				}
//				System.out.println("hmRemainOrientDetailsForPeerAppWise ::::: "+hmRemainOrientDetailsForPeerAppWise);
//				System.out.println("hmRemainOrientDetailsAppWise ::::: "+hmRemainOrientDetailsAppWise);
//				System.out.println("hmPriorUserType ::::: "+hmPriorUserType);
				
			//===start parvez date: 06-08-2022===	
				request.setAttribute("hmApprovedFeedback", hmApprovedFeedback);
			//===end parvez date: 06-08-2022===	
				
				request.setAttribute("reviewEmpCount", ""+reviewEmpCount);
				request.setAttribute("empMpDetails", empMpDetails);
				request.setAttribute("hmRemainOrientDetailsAppWise", hmRemainOrientDetailsAppWise);
				request.setAttribute("hmRemainOrientDetailsForSelfAppWise", hmRemainOrientDetailsForSelfAppWise);
				request.setAttribute("hmRemainOrientDetailsForPeerAppWise", hmRemainOrientDetailsForPeerAppWise);
				request.setAttribute("hmExistOrientTypeAQAAppWise", hmExistOrientTypeAQAAppWise);
			
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	public void getReviewerAppraisalDetails() {
		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
//		System.out.println("in appraisal .......... ");
		try {
			con = db.makeConnection(con);
			
//			getEmpScoreAvgAppraisalWise(con, uF);
			
			Map<String, String> hmDesignation = CF.getDesigMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmLevelMap = getLevelMap(con);
			Map<String, String> orientationMp = getOrientationValue(con);

			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			Map<String, String> hmFreq = new HashMap<String, String>();
			while (rs.next()) {
				hmFreq.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();
			
			List<String> alEmpId = null;
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {	
				pst = con.prepareStatement("select eod.emp_id from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id " +
						"and upper(epd.emp_fname)|| ' '||upper(epd.emp_lname) like'%"+getStrSearchJob().trim().toUpperCase()+"%'");
				rs = pst.executeQuery();
				while(rs.next()) {
					if(alEmpId == null) {
						alEmpId = new ArrayList<String>();
						alEmpId.add(rs.getString("emp_id"));
					} else {
						alEmpId.add(rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
			}
			
			List<String> alFrequencyId = null;
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {	
				pst = con.prepareStatement("select * from appraisal_frequency where frequency_name is not null and frequency_name != '' " +
					" and upper(frequency_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'");
				rs = pst.executeQuery();
				while(rs.next()) {
					if(alFrequencyId == null) {
						alFrequencyId = new ArrayList<String>();
						alFrequencyId.add(rs.getString("appraisal_frequency_id"));
					} else {
						alFrequencyId.add(rs.getString("appraisal_frequency_id"));
					}
				}
				rs.close();
				pst.close();
			}
			
			List<String> alOrientationId = null;
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")) {	
				pst = con.prepareStatement("select * from apparisal_orientation  where orientation_name is not null " +
					"and orientation_name != '' and upper(orientation_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'");
				rs = pst.executeQuery();
				while(rs.next()) {
					if(alOrientationId == null) {
						alOrientationId = new ArrayList<String>();
						alOrientationId.add(rs.getString("apparisal_orientation_id"));
					} else {
						alOrientationId.add(rs.getString("apparisal_orientation_id"));
					}
				}
				rs.close();
				pst.close();
			}

			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select count(*) as cnt from appraisal_details where is_publish=true and is_close = false ");
			sbQuery.append("select count(*) as cnt from appraisal_details a, appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id" 
				+" and (is_delete is null or is_delete = false) and is_appraisal_publish=true and is_appraisal_close = false ");
			sbQuery.append(" and reviewer_id like '%,"+strSessionEmpId+",%' ");
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") && !getStrSearchJob().trim().equalsIgnoreCase("null")) {
				sbQuery.append(" and (upper(appraisal_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(appraisal_type) like '%"+getStrSearchJob().trim().toUpperCase()+"%' ");
				if(alEmpId !=null && alEmpId.size() > 0) {
					sbQuery.append(" or (");
		            for(int i=0; i<alEmpId.size(); i++) {
		               sbQuery.append(" self_ids like '%,"+alEmpId.get(i).trim()+",%'");
		               if(i<alEmpId.size()-1) {
		                  sbQuery.append(" OR "); 
		                }
		            }
			        sbQuery.append(" ) ");
				}
				if(alFrequencyId !=null && alFrequencyId.size() > 0) {
					sbQuery.append(" or (");
		            for(int i=0; i<alFrequencyId.size(); i++) {
		               sbQuery.append(" frequency like '%"+alFrequencyId.get(i).trim()+"%'");
		               if(i<alFrequencyId.size()-1) {
		                  sbQuery.append(" OR "); 
		                }
		            }
			        sbQuery.append(" ) ");
				}
				if(alOrientationId !=null && alOrientationId.size() > 0) {
					sbQuery.append(" or (");
		            for(int i=0; i<alOrientationId.size(); i++) {
		               sbQuery.append(" oriented_type like '%"+alOrientationId.get(i).trim()+"%'");
		               if(i<alOrientationId.size()-1) {
		                  sbQuery.append(" OR "); 
		                }
		            }
			        sbQuery.append(" ) ");
				}
				sbQuery.append(")");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("appraisal pst======>"+pst);
			rs=pst.executeQuery();
			int proCount = 0;
			int proCnt = 0;
			while(rs.next()) {
				proCnt = rs.getInt("cnt");
				proCount = rs.getInt("cnt")/10;
				if(rs.getInt("cnt")%10 != 0) {
					proCount++;
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("proCountReviewer", proCount+"");
			request.setAttribute("proCntReviewer", proCnt+"");
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from appraisal_details a, appraisal_details_frequency adf where a.appraisal_details_id = adf.appraisal_id" 
					+" and (is_delete is null or is_delete = false) and is_appraisal_publish=true and is_appraisal_close = false ");
			sbQuery.append(" and reviewer_id like '%,"+strSessionEmpId+",%' ");
			
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") && !getStrSearchJob().trim().equalsIgnoreCase("null")) {
				sbQuery.append(" and (upper(appraisal_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(appraisal_type) like '%"+getStrSearchJob().trim().toUpperCase()+"%' ");
				if(alEmpId !=null && alEmpId.size() > 0) {
					sbQuery.append(" or (");
		            for(int i=0; i<alEmpId.size(); i++) {
		               sbQuery.append(" self_ids like '%,"+alEmpId.get(i).trim()+",%'");
		               if(i<alEmpId.size()-1) {
		                  sbQuery.append(" OR "); 
		                }
		            }
			        sbQuery.append(" ) ");
				}
				
				if(alFrequencyId !=null && alFrequencyId.size() > 0) {
					sbQuery.append(" or (");
		            for(int i=0; i<alFrequencyId.size(); i++) {
		               sbQuery.append(" frequency like '%"+alFrequencyId.get(i).trim()+"%'");
		               if(i<alFrequencyId.size()-1) {
		                  sbQuery.append(" OR "); 
		                }
		            }
			        sbQuery.append(" ) ");
				}
				
				if(alOrientationId !=null && alOrientationId.size() > 0) {
					sbQuery.append(" or (");
		            for(int i=0; i<alOrientationId.size(); i++) {
		               sbQuery.append(" oriented_type like '%"+alOrientationId.get(i).trim()+"%'");
		               if(i<alOrientationId.size()-1) {
		                  sbQuery.append(" OR "); 
		                }
		            }
			        sbQuery.append(" ) ");
				}
				sbQuery.append(")");
			}
			
			sbQuery.append(" order by to_date DESC, freq_end_date desc");
			int intOffset = uF.parseToInt(getMinLimitReviewer());
			sbQuery.append(" limit 10 offset "+intOffset+"");		
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1======>"+pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> appraisalDetails = new HashMap<String, Map<String, String>>();
			List<String> appraisalIdList = new ArrayList<String>();
			while (rs.next()) {
				Map<String, String> appraisalMp = new HashMap<String, String>();
				appraisalIdList.add(rs.getString("appraisal_details_id")+"_"+rs.getString("appraisal_freq_id"));//0
				appraisalMp.put("ID", rs.getString("appraisal_details_id"));//1
				appraisalMp.put("APPRAISAL", rs.getString("appraisal_name"));//2
				appraisalMp.put("ORIENT", orientationMp.get(rs.getString("oriented_type")));//3
				appraisalMp.put("EMPLOYEE", uF.showData(getAppendData(rs.getString("self_ids"), hmEmpName), ""));//4
				appraisalMp.put("LEVEL", uF.showData(hmLevelMap.get(rs.getString("level_id")), ""));//5
				appraisalMp.put("DESIG", hmDesignation.get(rs.getString("desig_id")));//6
				appraisalMp.put("GRADE", hmGradeMap.get(rs.getString("grade_id")));//7
				appraisalMp.put("WLOCATION", rs.getString("wlocation_id"));//8
				appraisalMp.put("PEER", rs.getString("peer_ids"));//9
				appraisalMp.put("ANYONE", rs.getString("other_ids"));//10
				appraisalMp.put("SELFID", rs.getString("self_ids"));//11
				appraisalMp.put("SUPERVISORID", rs.getString("supervisor_id"));//12
				appraisalMp.put("HRID", rs.getString("hr_ids"));//13
				appraisalMp.put("FREQUENCY", hmFreq.get(rs.getString("frequency")));//14
				appraisalMp.put("FROM", uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));//15
				appraisalMp.put("TO", uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));//16
				appraisalMp.put("REVIEW_TYPE", rs.getString("appraisal_type"));//17
				
				appraisalMp.put("ORIENTED_TYPE", rs.getString("oriented_type"));//18
				appraisalMp.put("CEOID", rs.getString("ceo_ids"));//19
				appraisalMp.put("HODID", rs.getString("hod_ids"));//20
				appraisalMp.put("APP_FREQ_ID", rs.getString("appraisal_freq_id"));//21
				appraisalMp.put("APP_FREQ_NAME", rs.getString("appraisal_freq_name"));//22
				appraisalMp.put("FREQ_START", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, CF.getStrReportDateFormat()));//23
				appraisalMp.put("FREQ_TO", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, CF.getStrReportDateFormat()));//24
				appraisalMp.put("APP_FREQ_ISPUBLISH", rs.getString("is_appraisal_publish"));//25
				appraisalMp.put("APP_FREQ_ISCLOSE", rs.getString("is_appraisal_close"));//26
				appraisalMp.put("APP_FREQ_EXPIRE", rs.getString("freq_publish_expire_status"));//27
				appraisalMp.put("USERTYPE_ID", rs.getString("usertype_member"));//28
				appraisalMp.put("APP_USERTYPES_FOR_FEEDBACK", rs.getString("user_types_for_feedback"));//29
				appraisalDetails.put(rs.getString("appraisal_details_id")+"_"+rs.getString("appraisal_freq_id"), appraisalMp);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("appraisalIdListReviewer", appraisalIdList);
			request.setAttribute("appraisalDetailsReviewer", appraisalDetails);

//			System.out.println("appraisalIdList==>"+appraisalIdList);
			Map<String, Map<String, Map<String, String>>> appraisalStatusMp = getEmployeeStatus(uF);
			request.setAttribute("appraisalStatusMpReviewer", appraisalStatusMp);

			Map<String, List<String>> hmReviewerEmp = new HashMap<String, List<String>>();
			
			Map<String, String> hmExistSectionCnt = new HashMap<String, String>();
			Map<String, List<String>> hmExistUserTypeCnt = new HashMap<String, List<String>>();
			pst = con.prepareStatement("select count (distinct(section_id)) as secCnt, emp_id,user_type_id, appraisal_id, appraisal_freq_id from " +
				" appraisal_question_answer where is_submit=true and reviewer_or_appraiser=0 group by emp_id,appraisal_id,user_type_id,appraisal_freq_id");
			rs = pst.executeQuery();
//			System.out.println("pst1 ::: "+pst);
			while (rs.next()) {
				List<String> userTypeList = hmExistUserTypeCnt.get(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("emp_id"));
				if(userTypeList==null) userTypeList = new ArrayList<String>();				
				userTypeList.add(rs.getString("user_type_id"));
				
				hmExistUserTypeCnt.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("emp_id"), userTypeList);
				
				hmExistSectionCnt.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("emp_id")+"_"+rs.getString("user_type_id"), rs.getString("secCnt"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmExistSectionCntReviewer", hmExistSectionCnt);
			request.setAttribute("hmExistUserTypeCntReviewer", hmExistUserTypeCnt);
			
			
			Map<String, String> hmExistReviewerCnt = new HashMap<String, String>();
			pst = con.prepareStatement("select count (distinct(section_id)) as secCnt, emp_id, appraisal_id, appraisal_freq_id from " +
				" appraisal_question_answer where is_submit=true and reviewer_or_appraiser=1 group by emp_id,appraisal_id,appraisal_freq_id"); // and user_id=? 
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
	//		System.out.println("pst1 ::: "+pst);
			while (rs.next()) {
				hmExistReviewerCnt.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")+"_"+rs.getString("emp_id"), rs.getString("secCnt"));
			}
			rs.close();
			pst.close();
			
			Map<String, Integer> hmSectionCount = new HashMap<String, Integer>();
//			int sectionCount = 0;
			pst = con.prepareStatement("select count(main_level_id) as sectionCnt, appraisal_id from appraisal_main_level_details group by appraisal_id");
//			pst.setInt(1, uF.parseToInt(appraisalIdList.get(i)));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmSectionCount.put(rs.getString("appraisal_id"), rs.getInt("sectionCnt"));
//				sectionCount = rs.getInt("sectionCnt");
			}
			rs.close();
			pst.close();
//			System.out.println("hmSectionCount ===>> " + hmSectionCount);
			
			int reviewEmpCount = 0;
			for (int i = 0; i < appraisalIdList.size(); i++) {
				Map<String, String> appraisalMp = appraisalDetails.get(appraisalIdList.get(i));
				
				Map<String, Map<String, String>> userTypeMp = appraisalStatusMp.get(appraisalIdList.get(i));
				if(userTypeMp == null) userTypeMp = new HashMap<String, Map<String, String>>();
				String appId = appraisalIdList.get(i);
				if(appId != null && !appId.equals("")) {
					appId = appId.substring(0,appId.lastIndexOf("_"));
				}
//				String appraisalId = appraisalMp.get("ID");
				String self = appraisalMp.get("SELFID");
				self = self!=null && !self.equals("") ? self.substring(1,self.length()-1) : "";
				
				String userTypeId = appraisalMp.get("USERTYPE_ID");
				List<String> userTypeList = new ArrayList<String>();
				if(userTypeId != null && userTypeId.trim().length()>0) {
					userTypeList = Arrays.asList(userTypeId.split(","));
				}
				int appSectionCnt = hmSectionCount.get(appId);
//				System.out.println("userTypeList ===>> " + userTypeList);
//				System.out.println("self ===>> " + self);
				List<String> employeeList = new ArrayList<String>();
				pst = con.prepareStatement("select distinct(emp_per_id) from employee_official_details eod,employee_personal_details epd,appraisal_details ad" +
					" where epd.emp_per_id=eod.emp_id and emp_per_id in ("+ self + ") and ad.reviewer_id like '%,"+strSessionEmpId+",%' order by emp_per_id"); // and eod.wlocation_id=?
				rs = pst.executeQuery();
//				System.out.println("pst Reviewer =====>"+pst);
				while (rs.next()) {
					if(hmExistUserTypeCnt != null && userTypeList != null && hmExistUserTypeCnt.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id")) != null && userTypeList.size() == hmExistUserTypeCnt.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id")).size()) {
						boolean flag = true;
						for(int a=0; a<userTypeList.size(); a++) {
							String existSectionCnt = hmExistSectionCnt.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id")+"_"+userTypeList.get(a));
							if(appSectionCnt > uF.parseToInt(existSectionCnt)) {
								flag = false;
							}
						}
//						System.out.println("flag =====>> " + flag+" -- getDataType() ===>> " +getDataType()+" --- hmExistReviewerCnt =====>> " + hmExistReviewerCnt);
						if(getDataType() != null && getDataType().equals("L") && flag && (hmExistReviewerCnt == null || (hmExistReviewerCnt != null && hmExistReviewerCnt.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id")) == null))) {
//							System.out.println("hmExistReviewerCnt ===>> "+hmExistReviewerCnt.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id"))+" ==> hmSectionCount==>"+hmSectionCount);
							employeeList.add(rs.getString("emp_per_id"));
							reviewEmpCount++;
						} else if(getDataType() != null && getDataType().equals("C") && flag && hmExistReviewerCnt != null && hmExistReviewerCnt.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id")) != null) {
//							System.out.println("hmExistSectionCount==="+hmExistSectionCount.get(appraisalIdList.get(i)+"_"+rs.getString("emp_per_id"))+"==>hmSectionCount==>"+hmSectionCount);
							employeeList.add(rs.getString("emp_per_id"));
							reviewEmpCount++;
						}
					}
				}
				rs.close();
				pst.close();
//				empMp.put("REVIEWER", employeeList);
				
//---------------------------------------------------------------------------------------
				hmReviewerEmp.put(appraisalIdList.get(i), employeeList);
				
//				System.out.println("hmReviewerEmp ::::: "+ appraisalIdList.get(i)+" - " + hmReviewerEmp);
			}
//			System.out.println("hmRemainOrientDetailsForPeerAppWise ::::: "+hmRemainOrientDetailsForPeerAppWise);
//			System.out.println("empMpDetails ::::: "+empMpDetails);
			
//			request.setAttribute("reviewEmpCount", ""+reviewEmpCount);
			request.setAttribute("hmReviewerEmp", hmReviewerEmp);
//			request.setAttribute("hmRemainOrientDetailsAppWise", hmRemainOrientDetailsAppWise);
//			request.setAttribute("hmRemainOrientDetailsForSelfAppWise", hmRemainOrientDetailsForSelfAppWise);
//			request.setAttribute("hmRemainOrientDetailsForPeerAppWise", hmRemainOrientDetailsForPeerAppWise);
//			request.setAttribute("hmExistOrientTypeAQAAppWise", hmExistOrientTypeAQAAppWise);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public void getOrientTypeWiseIds() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		Map<String,List<String>> hmOrientTypewiseID = new HashMap<String, List<String>>();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select appraisal_details_id,reviewee_id,ard.subordinate_ids,ard.peer_ids,ard.other_peer_ids,ard.supervisor_ids," +
				"ard.grand_supervisor_ids,ard.hod_ids,ard.ceo_ids,ard.hr_ids,ard.other_ids from appraisal_details ad, " +
				"appraisal_reviewee_details ard where ad.appraisal_details_id=ard.appraisal_id and ad.is_close = false");
//			pst.setInt(1, uF.parseToInt(appid));
			rs = pst.executeQuery();
			while (rs.next()) {
//				List<String> existUsersAQAList = hmExistUsersAQA.get(rs.getString("section_id")+"_"+rs.getString("user_type_id"));
				
				List<String> al = hmOrientTypewiseID.get(rs.getString("appraisal_details_id")+"_7");
				if(al==null) al = new ArrayList<String>();
				al.addAll(getListData(rs.getString("hr_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_7", al);
				
				al = hmOrientTypewiseID.get(rs.getString("appraisal_details_id")+"_4");
				if(al==null) al = new ArrayList<String>();
				al.addAll(getListData(rs.getString("peer_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_4", al);
				
				al = hmOrientTypewiseID.get(rs.getString("appraisal_details_id")+"_3");
				if(al==null) al = new ArrayList<String>();
				al.addAll(getListData(rs.getString("reviewee_id")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_3", al);
				
				al = hmOrientTypewiseID.get(rs.getString("appraisal_details_id")+"_2");
				if(al==null) al = new ArrayList<String>();
				al.addAll(getListData(rs.getString("supervisor_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_2", al);
				
				al = hmOrientTypewiseID.get(rs.getString("appraisal_details_id")+"_5");
				if(al==null) al = new ArrayList<String>();
				al.addAll(getListData(rs.getString("ceo_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_5", al);
				
				al = hmOrientTypewiseID.get(rs.getString("appraisal_details_id")+"_13");
				if(al==null) al = new ArrayList<String>();
				al.addAll(getListData(rs.getString("hod_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_13", al);
				
				al = hmOrientTypewiseID.get(rs.getString("appraisal_details_id")+"_6");
				if(al==null) al = new ArrayList<String>();
				al.addAll(getListData(rs.getString("subordinate_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_6", al);
				
				al = hmOrientTypewiseID.get(rs.getString("appraisal_details_id")+"_8");
				if(al==null) al = new ArrayList<String>();
				al.addAll(getListData(rs.getString("grand_supervisor_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_8", al);
				
				al = hmOrientTypewiseID.get(rs.getString("appraisal_details_id")+"_14");
				if(al==null) al = new ArrayList<String>();
				al.addAll(getListData(rs.getString("other_peer_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_14", al);
				
				al = hmOrientTypewiseID.get(rs.getString("appraisal_details_id")+"_10");
				if(al==null) al = new ArrayList<String>();
				al.addAll(getListData(rs.getString("other_ids")));
				hmOrientTypewiseID.put(rs.getString("appraisal_details_id")+"_10", al);
				
			}
			rs.close();
			pst.close();
			
			
//			System.out.println("hmOrientTypewiseID ========== > "+hmOrientTypewiseID);
			request.setAttribute("hmOrientTypewiseID", hmOrientTypewiseID);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private List<String> getListData(String Ids) {
		List<String> listIds = new ArrayList<String>();
		if(Ids != null && !Ids.equals("") && Ids.length()>1) {
			Ids = Ids.substring(1, Ids.length());
			
			if (Ids.contains(",")) {
				String[] temp = Ids.split(",");
				for (int i = 0; i < temp.length; i++) {
					listIds.add(temp[i]);
				}
			} else {
				listIds.add(Ids);
			}
		}
		return listIds;
	}
	
	
	public void getExistUsersInAQA() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		Map<String,List<String>> hmExistUsersAQA = new HashMap<String, List<String>>();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select distinct(user_type_id),appraisal_id,section_id,user_id,emp_id from appraisal_question_answer where is_submit=true");
//			pst.setInt(1, uF.parseToInt(appid));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> existUsersAQAList = hmExistUsersAQA.get(rs.getString("emp_id")+"_"+rs.getString("section_id")+"_"+rs.getString("user_type_id"));
				if(existUsersAQAList==null) existUsersAQAList = new ArrayList<String>();				
				existUsersAQAList.add(rs.getString("user_id"));
				hmExistUsersAQA.put(rs.getString("emp_id")+"_"+rs.getString("section_id")+"_"+rs.getString("user_type_id"), existUsersAQAList);
			}
			rs.close();
			pst.close();
//			System.out.println("hmExistUsersAQA ========== > "+hmExistUsersAQA);
			request.setAttribute("hmExistUsersAQA", hmExistUsersAQA);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getExistOrientTypeInAQA(String appraisalFreqId, UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		Map<String,List<String>> hmExistOrientTypeAQA = new HashMap<String, List<String>>();
		
		try {
			con = db.makeConnection(con);
			String[] appFreqArr = null;
			String appId = "";
			String appFreqId = "";
			if(appraisalFreqId != null && !appraisalFreqId.equals("")) {
				appFreqArr = appraisalFreqId.split("_");
				appId = appFreqArr[0];
				appFreqId = appFreqArr[1];
			}
			
		//===start parvez date:17-03-2023===
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			pst = con.prepareStatement("select appraisal_level_id,appraisal_id,appraisal_system,main_level_id from appraisal_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(appId));
			rs = pst.executeQuery();
			Map<String, List<String>> hmAppraisalLevelIds = new HashMap<String, List<String>>();
			Map<String, String> hmAppraisalLevelSystemType = new HashMap<String, String>();
			while (rs.next()) {
				List<String> innList = hmAppraisalLevelIds.get(rs.getString("appraisal_id"));
				if(innList==null) innList = new ArrayList<String>();
				innList.add(rs.getString("main_level_id"));
				hmAppraisalLevelIds.put(rs.getString("appraisal_id"), innList);
				hmAppraisalLevelSystemType.put(rs.getString("appraisal_id")+"_"+rs.getString("main_level_id"), rs.getString("appraisal_system"));
			}
			rs.close();
			pst.close();
		//===end parvez date: 17-03-2023===
			
			pst = con.prepareStatement("select distinct(user_type_id),appraisal_id,section_id,emp_id from appraisal_question_answer where is_submit=true and appraisal_id=? and appraisal_freq_id=?");
			pst.setInt(1, uF.parseToInt(appId));
			pst.setInt(2, uF.parseToInt(appFreqId));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> existOrientTypeAQAList = hmExistOrientTypeAQA.get(rs.getString("section_id")+"_"+rs.getString("emp_id"));
				if(existOrientTypeAQAList==null) existOrientTypeAQAList = new ArrayList<String>();				
				existOrientTypeAQAList.add(rs.getString("user_type_id"));
				hmExistOrientTypeAQA.put(rs.getString("section_id")+"_"+rs.getString("emp_id"), existOrientTypeAQAList);
			//===start parvez date:17-03-2023===	
				List<String> tempInnList = hmAppraisalLevelIds.get(rs.getString("appraisal_id"));
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) 
						&& (uF.parseToInt(rs.getString("user_type_id"))==4 || uF.parseToInt(rs.getString("user_type_id"))==14 || uF.parseToInt(rs.getString("user_type_id"))==13)){
					
					for(int i=0; tempInnList!=null && i<tempInnList.size(); i++){
						if(!hmExistOrientTypeAQA.containsKey(tempInnList.get(i)+"_"+rs.getString("emp_id")) && uF.parseToInt(hmAppraisalLevelSystemType.get(rs.getString("appraisal_id")+"_"+tempInnList.get(i)))!=2){
//							existOrientTypeAQAList.add(rs.getString("user_type_id"));
							hmExistOrientTypeAQA.put(tempInnList.get(i)+"_"+rs.getString("emp_id"), existOrientTypeAQAList);
						}
					}
				}
		//===start parvez date:17-03-2023===	
			}
			rs.close();
			pst.close();
			
	//===start parvez date: 16-07-2022===
			
			if(uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){
				pst = con.prepareStatement("select distinct(rfd.user_type_id),rfd.appraisal_id,rfd.emp_id,main_level_id from reviewer_feedback_details rfd,appraisal_main_level_details amld where rfd.appraisal_id=amld.appraisal_id and rfd.appraisal_id=? and rfd.appraisal_freq_id=?");
				pst.setInt(1, uF.parseToInt(appId));
				pst.setInt(2, uF.parseToInt(appFreqId));
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> existOrientTypeAQAList = hmExistOrientTypeAQA.get(rs.getString("main_level_id")+"_"+rs.getString("emp_id"));
					if(existOrientTypeAQAList==null) existOrientTypeAQAList = new ArrayList<String>();				
					if(!existOrientTypeAQAList.contains(rs.getString("user_type_id"))){
						existOrientTypeAQAList.add(rs.getString("user_type_id"));
					}
					hmExistOrientTypeAQA.put(rs.getString("main_level_id")+"_"+rs.getString("emp_id"), existOrientTypeAQAList);
				}
				rs.close();
				pst.close();
			}
	//===end parvez date: 16-07-2022===
			
//			System.out.println("hmExistOrientTypeAQA ========== > "+appId+" -- "+hmExistOrientTypeAQA);
			request.setAttribute("hmExistOrientTypeAQA", hmExistOrientTypeAQA);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	 
	
	private void getSecondMaxOrientNameAndIDs(Connection con, int id, String appid, String empId, UtilityFunctions uF, Map<String, List<String>> hmRemainOrientDetails) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<String> appSections = new ArrayList<String>();
		
		try {
			Map<String, String> hmOrientMemberID = CF.getOrientMemberID(con);
//			System.out.println("appid=="+appid+"---hmOrientMemberID : "+hmOrientMemberID);
			Map<String, String> hmRevieweeAndUsersIds = new HashMap<String, String>(); 
			pst = con.prepareStatement("select * from appraisal_reviewee_details where appraisal_id=? and reviewee_id=? ");
			pst.setInt(1, uF.parseToInt(appid));
			pst.setInt(2, uF.parseToInt(empId));
//				System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
//			List<String> supervisorIdsList = new ArrayList<String>();
			while(rs.next()) {
//				hmRevieweeAndUsersIds.put("6", rs.getString("subordinate_ids"));
//				hmRevieweeAndUsersIds.put("4", rs.getString("peer_ids"));
//				hmRevieweeAndUsersIds.put("14", rs.getString("other_peer_ids"));
//				hmRevieweeAndUsersIds.put("10", rs.getString("other_ids"));
//				System.out.println("Ap/2132---hr_ids=="+rs.getString("hr_ids"));
				String hr_ids = rs.getString("hr_ids")!=null && !rs.getString("hr_ids").equals("") ? rs.getString("hr_ids").substring(1, rs.getString("hr_ids").length()-1) : "";//Created By Dattatray Date:13-11-21
				hmRevieweeAndUsersIds.put("7", hr_ids);//Created By Dattatray Date:13-11-21
				hmRevieweeAndUsersIds.put("8", rs.getString("grand_supervisor_ids"));
				hmRevieweeAndUsersIds.put("13", rs.getString("hod_ids"));
//				System.out.println("Appr/1871--supervisor_ids="+rs.getString("supervisor_ids"));
				
				String supervisor_ids = rs.getString("supervisor_ids")!=null ? rs.getString("supervisor_ids").substring(1, rs.getString("supervisor_ids").length()-1) : "";//Created By Dattatray Date:13-11-21
					
				hmRevieweeAndUsersIds.put("2", supervisor_ids);//Created By Dattatray Date:13-11-21
//				supervisorIdsList = Arrays.asList(supervisor_ids.split(","));
				hmRevieweeAndUsersIds.put("5", rs.getString("ceo_ids"));
//				hmRevieweeAndUsersIds.put("3", ","+rs.getString("reviewee_id")+",");
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select main_level_id from appraisal_main_level_details where appraisal_id = ? order by main_level_id");
			pst.setInt(1, uF.parseToInt(appid));
			rs = pst.executeQuery();
			while (rs.next()) {
				appSections.add(rs.getString("main_level_id"));
			}
			rs.close();
			pst.close();
			
//			Map<String, List<String>> hmRemainOrientDetails = new HashMap<String, List<String>>() ;
			for(int a=0; appSections != null && a<appSections.size();a++) {
				List<String> allOrientName = new ArrayList<String>();
				List<String> allOrientID = new ArrayList<String>();
				getOrientPositions(appSections.get(a));
			
				List<String> orientPositionList = (List<String>)request.getAttribute("orientPositionList");
				String orientName[] = {"HR","Manager","Self","Peer","Client","Sub-ordinate","GroupHead","Vendor","CEO","HOD","Other Peer"};
				/*if(uF.parseToInt(empId)==66 && a==0) {
					System.out.println("orientPositionList ===>> "+ orientPositionList);
				}*/
//				System.out.println("appid=="+appid+"orientPositionList ===>> "+ orientPositionList);
//				System.out.println("id ===>> "+ id);
//				System.out.println("hmRevieweeAndUsersIds ===>> "+ hmRevieweeAndUsersIds);
			
				if(orientPositionList != null && !orientPositionList.isEmpty()) {
					int position = uF.parseToInt(orientPositionList.get(id));
//					System.out.println("position : "+position);
					int cnt=1;
//					if(id==1 && uF.parseToInt(appid)==32) {System.out.println("position ===>> " + position);}
//					pst = con.prepareStatement("select COUNT(user_id) as userCnt from appraisal_question_answer where appraisal_id=? and user_type_id=? ");
//					pst.setInt(1, uF.parseToInt(appid));
//					pst.setInt(2, uF.parseToInt(orientPositionList.get(id))-cnt);
//					System.out.println("pst ===>> " + pst);
//					rs = pst.executeQuery();
//					int userCnt = 0;
//					while (rs.next()) {
//						userCnt = rs.getInt("userCnt");
//					}
//					rs.close();
//					pst.close();
//					System.out.println("supervisorIdsList.size() ===>> " + supervisorIdsList.size());
//					System.out.println("userCnt ===>> " + userCnt);
					for(int i = position; i>=1; i--) {
						if(allOrientID == null || allOrientID.isEmpty()) {
//							System.out.println("cnt : "+cnt);
							for(int j = 0; orientPositionList!=null && j<orientPositionList.size(); j++) {
//							if(supervisorIdsList.size() == userCnt) {
								if(uF.parseToInt(orientPositionList.get(id)) > 1 && (uF.parseToInt(orientPositionList.get(id))-cnt) == uF.parseToInt(orientPositionList.get(j))) {
//									System.out.println("orientPositionList - cnt : "+(uF.parseToInt(orientPositionList.get(id))-cnt));
//									System.out.println("orientPositionList : "+uF.parseToInt(orientPositionList.get(id)));
									
//										if(hmRevieweeAndUsersIds.get(hmOrientMemberID.get(orientName[j]))!=null && hmRevieweeAndUsersIds.get(hmOrientMemberID.get(orientName[j])).length()>1) {
											allOrientName.add(orientName[j]);
											allOrientID.add(hmOrientMemberID.get(orientName[j]));
//										}
								}
									
									
//								}
							}
						}
						cnt++;
					}
				}
			
//				System.out.println("getSecondMaxOrientNameAndIDs allOrientName1==>"+allOrientName);
//				System.out.println("getSecondMaxOrientNameAndIDs allOrientID1==>"+allOrientID);
				hmRemainOrientDetails.put(empId+"_"+appSections.get(a)+"NAME", allOrientName);
				hmRemainOrientDetails.put(empId+"_"+appSections.get(a)+"ID", allOrientID);
			}
//			System.out.println("hmRemainOrientDetails="+hmRemainOrientDetails);
			request.setAttribute("hmRemainOrientDetails", hmRemainOrientDetails);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void getOrientPositions(String mainLevelID) {
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
			pst.setInt(1, uF.parseToInt(mainLevelID));
//			System.out.println("mainLevelId==>"+mainLevelID+"\npst==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				orientPositionList.add(rs.getString("hr")); //0
				orientPositionList.add(rs.getString("manager")); //1
				orientPositionList.add(rs.getString("self")); //2
				orientPositionList.add(rs.getString("peer")); //3
				orientPositionList.add(rs.getString("client")); //4
				orientPositionList.add(rs.getString("subordinate")); //5
				orientPositionList.add(rs.getString("grouphead")); //6
				orientPositionList.add(rs.getString("vendor")); //7
				orientPositionList.add(rs.getString("ceo")); //8
				orientPositionList.add(rs.getString("hod")); //9
				orientPositionList.add(rs.getString("other_peer")); //10
			}
			rs.close();
			pst.close();
//			System.out.println("App/2015--orientPositionList="+orientPositionList);
			request.setAttribute("orientPositionList", orientPositionList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public Map<String, List<List<String>>> getAppriesalSections() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, List<List<String>>> hmAppraisalSectins = new LinkedHashMap<String, List<List<String>>>();
		try {
//			List<Map<String, String>> listAppSectionIDs = new ArrayList<Map<String, String>>();
			con = db.makeConnection(con);
			
		//===start parvez date: 16-03-2023===	
			pst = con.prepareStatement("select * from appraisal_level_details order by appraisal_id,main_level_id");
			rs = pst.executeQuery();
			Map<String, String> hmAppSystemType = new HashMap<String, String>();
			while (rs.next()) {
				hmAppSystemType.put(rs.getString("appraisal_id")+"_"+rs.getString("main_level_id"), rs.getString("appraisal_system"));
			}
			rs.close();
			pst.close();
		//===end parvez date: 16-03-2023===	
			
			pst = con.prepareStatement("select * from appraisal_main_level_details order by appraisal_id,main_level_id");
			rs = pst.executeQuery();
//			System.out.println("hmAppraisalSectins pst==>"+pst);
			String appID="";
			while (rs.next()) {
				appID = rs.getString("appraisal_id");
				List<List<String>> outerList=hmAppraisalSectins.get(appID);
				if(outerList==null)outerList=new ArrayList<List<String>>();
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("main_level_id"));
				innerList.add(rs.getString("level_title"));
				innerList.add(hmAppSystemType.get(rs.getString("appraisal_id")+"_"+rs.getString("main_level_id")));		//added by parvez date: 16-03-2023
				outerList.add(innerList);
				hmAppraisalSectins.put(appID, outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(*) as cnt, user_type_id,appraisal_id,appraisal_freq_id,reviewer_or_appraiser,emp_id,section_id " +
				"from appraisal_question_answer where is_submit=true and user_id=? group by user_type_id,appraisal_id,appraisal_freq_id,reviewer_or_appraiser,emp_id,section_id");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
//				System.out.println("pst === > "+pst);
			Map<String, String> hmSectionGivenQueCnt = new HashMap<String, String>();
			while (rs.next()) {
				hmSectionGivenQueCnt.put(rs.getString("user_type_id")+"_"+rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id")
					+"_"+rs.getString("reviewer_or_appraiser")+"_"+rs.getString("emp_id")+"_"+rs.getString("section_id"), rs.getInt("cnt")+"");
			}
			rs.close();
			pst.close();
			
			Map<String, List<String>> hmEmpKRAIds = (Map<String, List<String>>) request.getAttribute("hmEmpKRAIds");
			Map<String, List<String>> hmEmpGoarTargetIds = (Map<String, List<String>>) request.getAttribute("hmEmpGoarTargetIds");
			/*pst = con.prepareStatement("select count(*) as cnt,b.main_level_id,a.appraisal_id from appraisal_question_details a, appraisal_level_details b " +
				"where a.appraisal_level_id = b.appraisal_level_id group by a.appraisal_id,b.main_level_id order by a.appraisal_id,b.main_level_id");*/
			pst = con.prepareStatement("select appraisal_question_details_id,reviewee_id,b.main_level_id,a.appraisal_id,a.app_system_type,a.kra_id,a.goal_kra_target_id" +
				" from appraisal_question_details a, appraisal_level_details b, " +
				"appraisal_reviewee_details ard where a.appraisal_id=ard.appraisal_id and a.appraisal_level_id = b.appraisal_level_id order by a.appraisal_id,b.main_level_id");
			rs = pst.executeQuery();
//			System.out.println("pst === > "+pst);
			Map<String, String> hmSectionQueCnt = new HashMap<String, String>();
			while (rs.next()) {
				List<String> alKRAIds = hmEmpKRAIds.get(rs.getString("reviewee_id"));
				if(rs.getInt("app_system_type")==4 && alKRAIds!=null && rs.getString("kra_id")!=null && !alKRAIds.contains(rs.getString("kra_id"))) {
					continue;
				}
				List<String> alGoarTargetIds = hmEmpGoarTargetIds.get(rs.getString("reviewee_id"));
				if((rs.getInt("app_system_type")==3 || rs.getInt("app_system_type")==5) && alGoarTargetIds!=null && rs.getString("goal_kra_target_id")!=null && !alGoarTargetIds.contains(rs.getString("goal_kra_target_id"))) {
					continue;
				}
				int intCnt = uF.parseToInt(hmSectionQueCnt.get(rs.getString("appraisal_id")+"_"+rs.getString("reviewee_id")+"_"+rs.getString("main_level_id")));
				intCnt++;
				hmSectionQueCnt.put(rs.getString("appraisal_id")+"_"+rs.getString("reviewee_id")+"_"+rs.getString("main_level_id"), intCnt+"");
			}
			rs.close();
			pst.close();
				
			request.setAttribute("hmSectionGivenQueCnt", hmSectionGivenQueCnt);
			request.setAttribute("hmSectionQueCnt", hmSectionQueCnt);
//			System.out.println("hmSectionGivenQueCnt =====>"+hmSectionGivenQueCnt);
//			System.out.println("hmSectionQueCnt =====>"+hmSectionQueCnt);
			
			request.setAttribute("hmAppraisalSectins", hmAppraisalSectins);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmAppraisalSectins;
	}
	
	
	
	public Map<String, Map<String, Map<String, String>>> getEmployeeStatus(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, Map<String, Map<String, String>>> appraisalMp = new HashMap<String, Map<String, Map<String, String>>>();
		con = db.makeConnection(con);
		try {
			pst = con.prepareStatement("select emp_id,appraisal_id,user_type_id,user_id,appraisal_freq_id from appraisal_question_answer where is_submit=true group by"
				+" emp_id,appraisal_id,user_type_id,user_id,appraisal_freq_id order by emp_id");
//			System.out.println("pst getEmployeeStatus==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				
				Map<String, Map<String, String>> userTypeMp = appraisalMp.get(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id"));
				if (userTypeMp == null)userTypeMp = new HashMap<String, Map<String, String>>();
				Map<String, String> empMp = userTypeMp.get(rs.getString("user_type_id"));
				if (empMp == null)empMp = new HashMap<String, String>();
//				if(uF.parseToInt(rs.getString("user_type_id"))==4 || uF.parseToInt(rs.getString("user_type_id"))==10) {
					empMp.put(rs.getString("emp_id")+"_"+rs.getString("user_id"), rs.getString("emp_id"));
//				} else {
//					empMp.put(rs.getString("emp_id"), rs.getString("emp_id"));
//				}
				
				userTypeMp.put(rs.getString("user_type_id"), empMp);
				appraisalMp.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id"), userTypeMp);
//				System.out.println("appraisalMp :: "+appraisalMp);
			}
			rs.close();
			pst.close();
			
	//===start parvez date: 14-07-2022===
			Map<String,String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			if(uF.parseToBoolean(hmFeatureStatus.get(F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT))){
				pst = con.prepareStatement("select emp_id,appraisal_id,user_type_id,user_id,appraisal_freq_id from reviewer_feedback_details where is_submit=true group by"
						+" emp_id,appraisal_id,user_type_id,user_id,appraisal_freq_id order by emp_id");
				rs = pst.executeQuery();
				while (rs.next()) {
					Map<String, Map<String, String>> userTypeMp = appraisalMp.get(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id"));
					if (userTypeMp == null)userTypeMp = new HashMap<String, Map<String, String>>();
					Map<String, String> empMp = userTypeMp.get(rs.getString("user_type_id"));
					if (empMp == null)empMp = new HashMap<String, String>();
						empMp.put(rs.getString("emp_id")+"_"+rs.getString("user_id"), rs.getString("emp_id"));
					
					userTypeMp.put(rs.getString("user_type_id"), empMp);
					appraisalMp.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id"), userTypeMp);
				}
				rs.close();
				pst.close();
			}
	//===end parvez date: 14-07-2022===		
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return appraisalMp;
	}
	

//	private Map<String, String> getUserDetails(Connection con) {
//		PreparedStatement pst = null;
//		ResultSet rst = null;
//		UtilityFunctions uF = new UtilityFunctions();
//
//		Map<String, String> hmUserDetails = new HashMap<String, String>();
//
//		try {
//			pst = con.prepareStatement("select * from employee_official_details e where e.emp_id=?");
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			rst = pst.executeQuery();
//			while (rst.next()) {
//				hmUserDetails.put("WLOCATION", rst.getString("wlocation_id"));
//				hmUserDetails.put("DEPARTID", rst.getString("depart_id"));
//				hmUserDetails.put("GRADEID", rst.getString("grade_id"));
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return hmUserDetails;
//	}

	
	private String getAppendData(String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();

		if (strID != null && !strID.equals("")) {
			strID = strID.substring(1, strID.length()-1);
			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 1; i < temp.length; i++) {
					if (i == 1) {
						sb.append(mp.get(temp[i].trim()));
					} else {
						sb.append("," + mp.get(temp[i].trim()));
					}
				}
			} else {
				return mp.get(strID);
			}
		} else {
			return null;
		}
		return sb.toString();
	}

	
	// private String getUserlocation(Connection con) {
	// // Connection con = null;
	// PreparedStatement pst = null;
	// ResultSet rst = null;
	// Database db = new Database();
	// UtilityFunctions uF = new UtilityFunctions();
	// String location = "";
	//
	// // con = db.makeConnection(con);
	// try {
	// pst =
	// con.prepareStatement("select e.wlocation_id from employee_official_details e where e.emp_id=?");
	// pst.setInt(1, uF.parseToInt(strSessionEmpId));
	// rst = pst.executeQuery();
	// while (rst.next()) {
	// location = rst.getString(1);
	// }
	//
	// } catch (SQLException e) {
	// e.printStackTrace();
	// } finally {
	// // db.closeConnection(con);
	// db.closeStatements(pst);
	// db.closeResultSet(rst);
	// }
	//
	// return location;
	// }

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	
	public Map<String, String> getLevelMap(Connection con) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new HashMap<String, String>();
		try {
			pst = con.prepareStatement(selectLevel);
			rs = pst.executeQuery();

			while (rs.next()) {
				hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name") + "[" + rs.getString("level_code") + "]");
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmLevelMap;
	}

	
	public void getOrientationMember() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
			Map<String, String> orientationMemberMp = new HashMap<String, String>();
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				//orientationMemberMp.put(rs.getString("orientation_member_id"), rs.getString("member_name"));
				orientationMemberMp.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("orientationMemberMp", orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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
			if(rs != null) {
				try {
					rs.close();
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
		return orientationMp;
	}
	
	//===start parvez date: 28-12-2021===
	public void getNewGoalDetails(List<String> appraisalIdList,Map<String, Map<String, String>> appraisalDetails,Connection con, UtilityFunctions uF){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			Map<String, List<String>> hmNewGoalMap = new HashMap<String, List<String>>();
			
			for(int i=0; appraisalIdList!=null && i<appraisalIdList.size();i++){
//				String appToDate = appraisalDetails.get(appraisalIdList).get("TO");
//				String goalEffectiveDate = uF.getFutureDate(uF.getDateFormatUtil(appToDate, "dd-MMM-yyyy"), 1)+"";
				/*pst = con.prepareStatement("select * from goal_details where goal_type="+PERSONAL_GOAL+"and effective_date=? " +
						"and emp_ids like '%,"+appraisalDetails.get(appraisalIdList).get("SELFID").trim()+",%'");*/
				String tempAppId = appraisalIdList.get(i);
				pst = con.prepareStatement("select * from goal_details where goal_type="+PERSONAL_GOAL+" and emp_ids like '%"+appraisalDetails.get(appraisalIdList.get(i)).get("SELFID").trim()+"%'");
				rs = pst.executeQuery();
//				System.out.println("Appr/2319--pst="+pst);
				List<String> innerList = new ArrayList<String>();
				while(rs.next()){
					innerList.add(rs.getString("goal_id"));
					innerList.add(rs.getString("goal_title"));
					hmNewGoalMap.put(tempAppId, innerList);
				}
				rs.close();
				pst.close();
				
				request.setAttribute("hmNewGoalMap", hmNewGoalMap);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	//===end parvez date: 28-12-2021===


	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getType() {
		return type;
	} 

	public void setType(String type) {
		this.type = type;
	}

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
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

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getProPageReviewer() {
		return proPageReviewer;
	}

	public void setProPageReviewer(String proPageReviewer) {
		this.proPageReviewer = proPageReviewer;
	}

	public String getMinLimitReviewer() {
		return minLimitReviewer;
	}

	public void setMinLimitReviewer(String minLimitReviewer) {
		this.minLimitReviewer = minLimitReviewer;
	}
	
}