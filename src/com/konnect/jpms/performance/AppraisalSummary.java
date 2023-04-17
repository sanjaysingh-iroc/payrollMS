package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AppraisalSummary extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 6485071086781961535L;
	
	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strSessionEmpId = null;
	Map<String, List<Map<String, List<List<String>>>>> levelMp = new HashMap<String, List<Map<String, List<List<String>>>>>();

	private CommonFunctions CF;
	private static Logger log = Logger.getLogger(AppraisalSummary.class);

	private String id;
	
	private String empId;

	private List<FillAttribute> attributeList;
	private List<FillAnswerType> ansTypeList;
	private List<FillFrequency> frequencyList;
	private List<FillOrientation> orientationList;
	private String oreinted;
	private String appsystem;
	private String scoreType;
	private String type;
	private String UID;
	private String newID;
	
	private String importMsg;
	
	private String appFreqId;
	private String fromPage;
	public  String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		UtilityFunctions uF = new UtilityFunctions();

		if (CF == null) {
			return LOGIN;
		}

		String title = "Summary";
		if (type != null && type.equals("choose")) {
			title = "Preview";
		} else {
			title = "Summary";
		}
	
		request.setAttribute(TITLE, "Review " + title);
		if (getEmpId() != null) {
			request.setAttribute(PAGE, "/jsp/performance/AppraisalScoreSummary.jsp");
		} else {
			request.setAttribute(PAGE, "/jsp/performance/AppraisalSummary.jsp");
			}
		
		if(getEmpId() != null) {
			getScoreMarks();
			getMeasureScoreMarks();
			getScoreMarksType1();
			getObjectiveMarks();
			getGoalMarksType1();
			getGoalMarksType2();
		}
		getAppraisalDetail();
	
		getReport();
		getAttributeDetails();
		getKRADetails();
		//getGoalTargetDetails();

//		System.out.println("ApSum/104--levelMp="+levelMp);
		request.setAttribute("levelMp", levelMp);

		String levelID=getSelfIDs(getId());
//		System.out.println("levelID====>"+levelID);
		if (levelID != null && levelID.length() > 0) {
			attributeList = new FillAttribute(request).fillElementAttribute(levelID);
		} else {
			attributeList = new FillAttribute(request).fillElementAttribute(null);
		}
		ansTypeList = new FillAnswerType(request).fillAnswerType();

//		getAppraisalDetail();
		getOrientationValue(uF.parseToInt(getOreinted()));
		getattribute();
		getOtherAnsType();
		getAppraisalQuestionList();
//		System.out.println("AS fromPage==>"+getFromPage());
		if(getFromPage() != null && getFromPage().equalsIgnoreCase("AD")) {
			if(uF.parseToInt(getEmpId()) > 0) {
				return VIEW;
			} else {
				return LOAD;
			}
		}
		return SUCCESS;
	}
	
	
	private String getSelfIDs( String id) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		String levelID=null;
		try {
			con = db.makeConnection(con);
			String empID=null;
			pst = con.prepareStatement("select self_ids from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs=pst.executeQuery();
			
			while(rs.next()){
				empID=rs.getString("self_ids");
			}
			rs.close();
			pst.close();
			
			if(empID!=null && !empID.equals("")){
				empID = empID.substring(1, empID.length()-1);
				List<String> levellistID=new ArrayList<String>();
				pst = con.prepareStatement("select ld.level_id from level_details ld right join (select * from designation_details dd " +
						"right join (select *, gd.designation_id as designationid from employee_official_details eod, grades_details gd " +
						"where gd.grade_id=eod.grade_id and eod.emp_id in ("+empID+")) a on a.designationid=dd.designation_id)" +
						" a on a.level_id=ld.level_id");
				rs=pst.executeQuery();
				
				while(rs.next()){
					levellistID.add(rs.getString(1));
				}
				rs.close();
				pst.close();
				
				Set<String> levelIdSet = new HashSet<String>(levellistID);
				Iterator<String> itr = levelIdSet.iterator();
				int i=0;
				while (itr.hasNext()) {
					String levelid = (String) itr.next();
					if(i==0) {
						levelID = levelid;
					} else {
						levelID+=","+levelid;
					}
					i++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return levelID;
	}

	

//	private Map<String, String> getOrientMemberID() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rs = null;
//		Map<String, String> orientationMemberMp = new HashMap<String, String>();
//
//		try {
//			
//			con = db.makeConnection(con);
//
//			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				orientationMemberMp.put(rs.getString("member_name"), rs.getString("member_id"));
//			}
//
////			System.out.println("memberid=====>"+orientationMemberMp);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return orientationMemberMp;
//	}

	

	public  void initialize( UtilityFunctions uF) {

		orientationList = new FillOrientation(request).fillOrientation();
		frequencyList = new FillFrequency(request).fillFrequency();

		String levelID=getSelfIDs(getId());
//		System.out.println("levelID====>"+levelID);
		if (levelID != null && levelID.length()>0){
			attributeList = new FillAttribute(request).fillElementAttribute(levelID);
		}else{				
			attributeList = new FillAttribute(request).fillElementAttribute(null);
		}

		getattribute();
		ansTypeList = new FillAnswerType(request).fillAnswerType();

	}

	private void getOrientationValue( int id) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
			StringBuilder sb = new StringBuilder();
			con = db.makeConnection(con);

			pst = con.prepareStatement("select member_name from orientation_details od,orientation_member om  where orientation_id=? and od.member_id=orientation_member_id");
			pst.setInt(1, id);
			rs = pst.executeQuery();
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					sb.append(rs.getString("member_name"));
				} else {
					sb.append("," + rs.getString("member_name"));
				}
				i++;
			}
			rs.close();
			pst.close();
			request.setAttribute("member", sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public  List<FillFrequency> getFrequencyList() {
		return frequencyList;
	}

	public  void setFrequencyList( List<FillFrequency> frequencyList) {
		this.frequencyList = frequencyList;
	}

	public  List<FillOrientation> getOrientationList() {
		return orientationList;
	}

	public  void setOrientationList( List<FillOrientation> orientationList) {
		this.orientationList = orientationList;
	}

	private void getOtherAnsType() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {
			StringBuilder sb = new StringBuilder("");

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_answer_type");
			rs = pst.executeQuery();
			while (rs.next()) {
				if (uF.parseToInt(rs.getString("appraisal_answer_type_id")) == 9) {
					sb.append("<option value=\"" + rs.getString("appraisal_answer_type_id") + "\" selected>"
							+ rs.getString("appraisal_answer_type_name") + "</option>");
				} else {
					sb.append("<option value=\"" + rs.getString("appraisal_answer_type_id") + "\">"
							+ rs.getString("appraisal_answer_type_name") + "</option>");
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

	/*private Map<String, String> getOrientationMember() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		ResultSet rs = null;
		Map<String, String> orientationMemberMp = new HashMap<String, String>();

		try {

			con = db.makeConnection(con);

			pst = con
					.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				//orientationMemberMp.put(rs.getString("orientation_member_id"),rs.getString("member_name"));
				orientationMemberMp.put(rs.getString("member_id"), rs.getString("member_name"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
			db.closeStatements(pst);
		}
		return orientationMemberMp;
	}*/

	

	public  List<FillAttribute> getAttributeList() {
		return attributeList;
	}

	public  void setAttributeList( List<FillAttribute> attributeList) {
		this.attributeList = attributeList;
	}

	public  void getattribute() {

		StringBuilder sb = new StringBuilder("");

		for (int i = 0; i < attributeList.size(); i++) {
			FillAttribute fillAttribute = attributeList.get(i);
			sb.append("<option value=\"" + fillAttribute.getId() + "\">" + fillAttribute.getName() + "</option>");

		}
		request.setAttribute("attribute", sb.toString());

	}

	public  void getAppraisalQuestionList() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			StringBuilder sb = new StringBuilder("");
			int queCnt=1;
			con = db.makeConnection(con);			
			pst = con.prepareStatement("select * from question_bank where is_add=true");
			rs = pst.executeQuery();
			while (rs.next()) {
				sb.append("<option value=\"" + rs.getString("question_bank_id") + "\">" + rs.getString("question_text").replace("'", "") + "</option>");
				queCnt++;
			}
			rs.close();
			pst.close();

			sb.append("<option value=\"0\">Add New Question</option>");

			request.setAttribute("option", sb.toString());
			request.setAttribute("queCnt", queCnt);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private String getAppendData( String strID,  Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();
//		System.out.println("strID :: "+strID);
		if (strID != null && !strID.equals("") && !strID.isEmpty()) {
			if(strID.length()>0 && strID.substring(0, 1).equals(",") && strID.substring(strID.length()-1, strID.length()).equals(",")){
			strID = strID.substring(1, strID.length()-1);
			}
			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(mp.get(temp[i].trim()));
					} else {
						sb.append(", " + mp.get(temp[i].trim()));
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

	/*public Map<String, String> getLevelMap() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new HashMap<String, String>();
		Database db = new Database();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectLevel);
			rs = pst.executeQuery();

			while (rs.next()) {
				hmLevelMap.put(
						rs.getString("level_id"),
						rs.getString("level_name") + "["
								+ rs.getString("level_code") + "]");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
			db.closeStatements(pst);
			db.closeResultSet(rs);
		}
		return hmLevelMap;
	}*/

	/*private Map<String, String> getLocationMap() {
		Map<String, String> mplocation = new HashMap<String, String>();
		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();

		con = db.makeConnection(con);

		try {

			pst = con.prepareStatement("select * from work_location_info");
			rst = pst.executeQuery();
			while (rst.next()) {
				mplocation.put(rst.getString("wlocation_id"),
						rst.getString("wlocation_name"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			db.closeConnection(con);
			db.closeStatements(pst);
			db.closeResultSet(rst);
		}

		return mplocation;
	}*/

	/*public Map<String, String> getAppraisalQuestionMap(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		ResultSet rs = null;
		Map<String, String> AppraisalQuestion = new HashMap<String, String>();

		try {

			con = db.makeConnection(con);
			pst = con
					.prepareStatement("select * from question_bank qb, appraisal_question_details aqd where qb.question_bank_id=aqd.question_id and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				AppraisalQuestion.put(rs.getString("question_bank_id"),
						rs.getString("question_text"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
			db.closeStatements(pst);
		}
		return AppraisalQuestion;
	}*/

	/*public Map<String, String> getAttributeMap() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		ResultSet rs = null;
		Map<String, String> AppraisalQuestion = new HashMap<String, String>();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_attribute ");
			rs = pst.executeQuery();
			while (rs.next()) {
				AppraisalQuestion.put(rs.getString("arribute_id"),
						rs.getString("attribute_name"));

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
			db.closeStatements(pst);
		}
		return AppraisalQuestion;
	}*/

	/*private Map<String, String> getOrientationValue() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		ResultSet rs = null;

		Map<String, String> orientationMp = new HashMap<String, String>();
		Map<String, String> hmorientationMembers = new HashMap<String, String>();
		try {

			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from apparisal_orientation");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMp.put(rs.getString("apparisal_orientation_id"),
						rs.getString("orientation_name"));
			}

			request.setAttribute("orientationMp", orientationMp);

			pst = con.prepareStatement("select * from orientation_member");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmorientationMembers.put(rs.getString("orientation_member_id"),
						rs.getString("member_name"));
			}

			request.setAttribute("hmorientationMembers", hmorientationMembers);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
			db.closeStatements(pst);
		}
		return orientationMp;
	}*/

	public  String getId() {
		return id;
	}

	public  void setId( String id) {
		this.id = id;
	}

	public  List<FillAnswerType> getAnsTypeList() {
		return ansTypeList;
	}

	public  void setAnsTypeList( List<FillAnswerType> ansTypeList) {
		this.ansTypeList = ansTypeList;
	}

	public  String getOreinted() {
		return oreinted;
	}

	public  void setOreinted( String oreinted) {
		this.oreinted = oreinted;
	}




	
	private void getGoalMarksType2() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select marks,weightage,((marks*100)/weightage)as percentage,user_type_id,reviewer_or_appraiser,goal_id from" +
				" (select sum(aqs.marks)as marks,sum(aqs.weightage)as weightage,aqs.user_type_id,aqs.reviewer_or_appraiser,amd.goal_id from appraisal_question_answer aqs " +
				"join appraisal_question_details aq  using (appraisal_question_details_id) join appraisal_measure_details amd using (measure_id)" +
				" where aqs.appraisal_id=? and aqs.emp_id=? and amd.goal_id is not null and aqs.weightage>0 group by aqs.reviewer_or_appraiser,aqs.user_type_id,amd.goal_id order by aqs.user_type_id,amd.goal_id) as a");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
//			System.out.println("getGoalMarksType2 pst ===> "+pst);
//			String strUserTypeNew = null;
//			String strUserTypeOld = null;
			
			Map<String,String> hmMeasureAnsScore = new HashMap<String, String>();
			Map<String, Map<String,String>> hmGoalType2Marks = new HashMap<String, Map<String,String>>();
			
			while (rs.next()) { 
				
//				strUserTypeNew = rs.getString("user_type_id");
//				if(strUserTypeNew!=null && !strUserTypeNew.equalsIgnoreCase(strUserTypeOld)){
//					hmMeasureAnsScore = new HashMap<String, String>();
//				}
				if(rs.getInt("reviewer_or_appraiser") == 1) {
					hmMeasureAnsScore = hmGoalType2Marks.get("REVIEWER_"+rs.getString("reviewer_or_appraiser"));
					if(hmMeasureAnsScore == null) hmMeasureAnsScore = new HashMap<String, String>();
				} else {
					hmMeasureAnsScore = hmGoalType2Marks.get(rs.getString("user_type_id")+"_"+rs.getString("reviewer_or_appraiser"));
					if(hmMeasureAnsScore == null) hmMeasureAnsScore = new HashMap<String, String>();
				}
								
				double dblPercent = uF.parseToDouble(rs.getString("percentage"));
				hmMeasureAnsScore.put(rs.getString("goal_id"), uF.getRoundOffValue(2,dblPercent));
				if(rs.getInt("reviewer_or_appraiser") == 1) {
					hmGoalType2Marks.put("REVIEWER_"+rs.getString("reviewer_or_appraiser"), hmMeasureAnsScore);
				} else {
					hmGoalType2Marks.put(rs.getString("user_type_id")+"_"+rs.getString("reviewer_or_appraiser"), hmMeasureAnsScore);
				}
//				strUserTypeOld = strUserTypeNew;
			}
			rs.close();
			pst.close();
			request.setAttribute("hmGoalType2Marks", hmGoalType2Marks);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getGoalMarksType1() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
		
			pst = con.prepareStatement("select marks,weightage,((marks*100)/weightage)as percentage,user_type_id,reviewer_or_appraiser,goal_id from" +
				" (select sum(aqs.marks)as marks,sum(aqs.weightage)as weightage,aqs.user_type_id,aqs.reviewer_or_appraiser,aod.goal_id from appraisal_question_answer aqs " +
				"join appraisal_question_details aq using (appraisal_question_details_id) join appraisal_measure_details amd using (measure_id) " +
				"join appraisal_objective_details aod using (objective_id) where aqs.appraisal_id=? and aqs.emp_id=? and amd.objective_id is not null" +
				" and aqs.weightage>0 group by aqs.reviewer_or_appraiser,aqs.user_type_id,aod.goal_id order by aqs.user_type_id,aod.goal_id)as a");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery(); 
//			System.out.println("getGoalMarksType1 pst ===> "+pst);
//			String strUserTypeNew = null;
//			String strUserTypeOld = null;
			
			Map<String,String> hmMeasureAnsScore = new HashMap<String, String>();
			Map<String, Map<String,String>> hmGoalType1Marks = new HashMap<String, Map<String,String>>();
			
			while (rs.next()) {
				
//				strUserTypeNew = rs.getString("user_type_id");
//				if(strUserTypeNew!=null && !strUserTypeNew.equalsIgnoreCase(strUserTypeOld)){
//					hmMeasureAnsScore = new HashMap<String, String>();
//				}
				if(rs.getInt("reviewer_or_appraiser") == 1) {
					hmMeasureAnsScore = hmGoalType1Marks.get("REVIEWER_"+rs.getString("reviewer_or_appraiser"));
					if(hmMeasureAnsScore == null) hmMeasureAnsScore = new HashMap<String, String>();
				} else {
					hmMeasureAnsScore = hmGoalType1Marks.get(rs.getString("user_type_id")+"_"+rs.getString("reviewer_or_appraiser"));
					if(hmMeasureAnsScore == null) hmMeasureAnsScore = new HashMap<String, String>();
				}
				double dblPercent = uF.parseToDouble(rs.getString("percentage"));
				hmMeasureAnsScore.put(rs.getString("goal_id"), uF.getRoundOffValue(2,dblPercent));
				if(rs.getInt("reviewer_or_appraiser") == 1) {
					hmGoalType1Marks.put("REVIEWER_"+rs.getString("reviewer_or_appraiser"), hmMeasureAnsScore);
				} else {
					hmGoalType1Marks.put(rs.getString("user_type_id")+"_"+rs.getString("reviewer_or_appraiser"), hmMeasureAnsScore);
				}
//				strUserTypeOld = strUserTypeNew;
			}
			rs.close();
			pst.close();
			request.setAttribute("hmGoalType1Marks", hmGoalType1Marks);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getObjectiveMarks() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select marks,weightage,((marks*100)/weightage)as percentage,user_type_id,reviewer_or_appraiser,objective_id " +
				" from (select sum(aqs.marks)as marks,sum(aqs.weightage)as weightage,aqs.user_type_id,aqs.reviewer_or_appraiser,amd.objective_id from appraisal_question_answer aqs " +
				"join appraisal_question_details aq  using (appraisal_question_details_id) join appraisal_measure_details amd using (measure_id) where " +
				"aqs.appraisal_id=? and aqs.emp_id=? and amd.objective_id is not null and aqs.weightage>0 group by aqs.reviewer_or_appraiser,aqs.user_type_id,amd.objective_id order by aqs.user_type_id,amd.objective_id)as a");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
//			System.out.println("getObjectiveMarks pst ===> "+pst);
//			String strUserTypeNew = null;
//			String strUserTypeOld = null;
			
			Map<String,String> hmMeasureAnsScore = new HashMap<String, String>();
			Map<String, Map<String,String>> hmObjectiveMarks = new HashMap<String, Map<String,String>>();
			
			while (rs.next()) {
				
//				strUserTypeNew = rs.getString("user_type_id");
//				if(strUserTypeNew!=null && !strUserTypeNew.equalsIgnoreCase(strUserTypeOld)){
//					hmMeasureAnsScore = new HashMap<String, String>();
//				}
				if(rs.getInt("reviewer_or_appraiser") == 1) {
					hmMeasureAnsScore = hmObjectiveMarks.get("REVIEWER_"+rs.getString("reviewer_or_appraiser"));
					if(hmMeasureAnsScore == null) hmMeasureAnsScore = new HashMap<String, String>();
				} else {
					hmMeasureAnsScore = hmObjectiveMarks.get(rs.getString("user_type_id")+"_"+rs.getString("reviewer_or_appraiser"));
					if(hmMeasureAnsScore == null) hmMeasureAnsScore = new HashMap<String, String>();
				}
				double dblPercent = uF.parseToDouble(rs.getString("percentage"));
				hmMeasureAnsScore.put(rs.getString("objective_id"), uF.getRoundOffValue(2,(dblPercent)));
				if(rs.getInt("reviewer_or_appraiser") == 1) {
					hmObjectiveMarks.put("REVIEWER_"+rs.getString("reviewer_or_appraiser"), hmMeasureAnsScore);
				} else {
					hmObjectiveMarks.put(rs.getString("user_type_id")+"_"+rs.getString("reviewer_or_appraiser"), hmMeasureAnsScore);
				}
//				strUserTypeOld = strUserTypeNew;
			}
			rs.close();
			pst.close();
			request.setAttribute("hmObjectiveMarks", hmObjectiveMarks);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getScoreMarksType1() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select marks,weightage,((marks*100)/weightage)as percentage,user_type_id,reviewer_or_appraiser,scorecard_id " +
				"from (select sum(aqs.marks)as marks,sum(aqs.weightage)as weightage,aqs.user_type_id,aqs.reviewer_or_appraiser,aq.scorecard_id from appraisal_question_answer aqs," +
				"appraisal_question_details aq where  aqs.appraisal_question_details_id=aq.appraisal_question_details_id and aqs.appraisal_id=? and aqs.emp_id=? " +
				"and aq.scorecard_id is not null and aqs.weightage>0 group by aqs.reviewer_or_appraiser,aqs.user_type_id,aq.scorecard_id order by aqs.user_type_id,aq.scorecard_id) as a");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
//			System.out.println("getScoreMarksType1 pst ===> "+pst);
//			String strUserTypeNew = null;
//			String strUserTypeOld = null;
			
			Map<String,String> hmMeasureAnsScore = new HashMap<String, String>();
			Map<String, Map<String,String>> hmScoreMarksType1 = new HashMap<String, Map<String,String>>();
			
			while (rs.next()) {
				
//				strUserTypeNew = rs.getString("user_type_id");
//				if(strUserTypeNew!=null && !strUserTypeNew.equalsIgnoreCase(strUserTypeOld)){
//					hmMeasureAnsScore = new HashMap<String, String>();
//				}
				if(rs.getInt("reviewer_or_appraiser") == 1) {
					hmMeasureAnsScore = hmScoreMarksType1.get("REVIEWER_"+rs.getString("reviewer_or_appraiser"));
					if(hmMeasureAnsScore == null) hmMeasureAnsScore = new HashMap<String, String>();
				} else {
					hmMeasureAnsScore = hmScoreMarksType1.get(rs.getString("user_type_id")+"_"+rs.getString("reviewer_or_appraiser"));
					if(hmMeasureAnsScore == null) hmMeasureAnsScore = new HashMap<String, String>();
				}
								
				double dblPercent = uF.parseToDouble(rs.getString("percentage"));
				hmMeasureAnsScore.put(rs.getString("scorecard_id"), uF.getRoundOffValue(2,dblPercent));
				if(rs.getInt("reviewer_or_appraiser") == 1) {
					hmScoreMarksType1.put("REVIEWER_"+rs.getString("reviewer_or_appraiser"), hmMeasureAnsScore);
				} else {
					hmScoreMarksType1.put(rs.getString("user_type_id")+"_"+rs.getString("reviewer_or_appraiser"), hmMeasureAnsScore);
				}
//				strUserTypeOld = strUserTypeNew;
			}
			rs.close();
			pst.close();
			request.setAttribute("hmScoreMarksType1", hmScoreMarksType1);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getMeasureScoreMarks() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select marks,weightage,(marks*100/weightage)as percentage,user_type_id,reviewer_or_appraiser,measure_id from " +
				"(select sum(aqs.marks)as marks,sum(aqs.weightage)as weightage,aqs.user_type_id,aqs.reviewer_or_appraiser,aq.measure_id from appraisal_question_answer aqs," +
				"appraisal_question_details aq where aqs.appraisal_question_details_id = aq.appraisal_question_details_id and aqs.appraisal_id=? and " +
				"aqs.emp_id=? and aq.measure_id is not null and aqs.weightage>0 group by aqs.reviewer_or_appraiser,aqs.user_type_id,aq.measure_id order by aqs.user_type_id,aq.measure_id) as a");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
//			System.out.println("getMeasureScoreMarks pst ===> "+pst);
//			String strUserTypeNew = null;
//			String strUserTypeOld = null;
			
			Map<String,String> hmMeasureAnsScore = new HashMap<String, String>();
			Map<String, Map<String,String>> hmMeasureUserScore = new HashMap<String, Map<String,String>>();
			while (rs.next()) {
//				strUserTypeNew = rs.getString("user_type_id");
//				if(strUserTypeNew!=null && !strUserTypeNew.equalsIgnoreCase(strUserTypeOld)){
//					hmMeasureAnsScore = new HashMap<String, String>();
//				}
				if(rs.getInt("reviewer_or_appraiser") == 1) {
					hmMeasureAnsScore = hmMeasureUserScore.get("REVIEWER_"+rs.getString("reviewer_or_appraiser"));
					if(hmMeasureAnsScore == null) hmMeasureAnsScore = new HashMap<String, String>();
				} else {
					hmMeasureAnsScore = hmMeasureUserScore.get(rs.getString("user_type_id")+"_"+rs.getString("reviewer_or_appraiser"));
					if(hmMeasureAnsScore == null) hmMeasureAnsScore = new HashMap<String, String>();
				}
				double dblMarks = uF.parseToDouble(rs.getString("marks"));
				double dblWeightage = uF.parseToDouble(rs.getString("weightage"));
				double dblPercent = uF.parseToDouble(rs.getString("percentage"));
//				if(dblWeightage>0){
//					dblPercent = dblMarks * dblWeightage / 100;  
//				}
//				System.out.println(rs.getString("measure_id")+" -- uF.formatIntoComma(dblPercent) ===>> "+uF.formatIntoComma(dblPercent)+" -- uF.getRoundOffValue(2, dblPercent) ===>> " + uF.getRoundOffValue(2, dblPercent));
				
				hmMeasureAnsScore.put(rs.getString("measure_id"), uF.getRoundOffValue(2, dblPercent));
				if(rs.getInt("reviewer_or_appraiser") == 1) {
					hmMeasureUserScore.put("REVIEWER_"+rs.getString("reviewer_or_appraiser"), hmMeasureAnsScore);
				} else {
					hmMeasureUserScore.put(rs.getString("user_type_id")+"_"+rs.getString("reviewer_or_appraiser"), hmMeasureAnsScore);
				}
//				strUserTypeOld = strUserTypeNew;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmMeasureUserScore", hmMeasureUserScore);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void getGoalTargetDetails( Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, List<String>> hmGoalTargetDetails = new LinkedHashMap<String, List<String>>();

		try { 
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			
			Map<String, String> hmGoalTargetMesures = new HashMap<String, String>();
		
			StringBuilder sb = new StringBuilder();
			String goaltype = INDIVIDUAL_GOAL+","+INDIVIDUAL_TARGET;
			sb.append("select * from goal_details g where g.goal_type in ("+goaltype+") and (measure_kra !='' or measure_kra is not null) order by g.goal_id");
			pst = con.prepareStatement(sb.toString());
//				System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				
				String strCurrId = CF.getOrgCurrencyIdByOrg(con, rs.getString("org_id"));
				String strCurrency = "";
				if(uF.parseToInt(strCurrId) > 0){
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(strCurrId);
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				} 
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_title"));
				innerList.add(rs.getString("goal_type"));
				innerList.add(rs.getString("weightage"));

				String measures = "";
				if (rs.getString("measure_type") != null && rs.getString("measure_type").equals("Amount")) {
					measures = strCurrency + " " + rs.getString("measure_currency_value");
				}else if (rs.getString("measure_type") != null && rs.getString("measure_type").equals("Percentage")) {
					measures = rs.getString("measure_currency_value") + " %";
				} else if (rs.getString("measure_type") != null && rs.getString("measure_type").equals("Effort")) {
					measures = rs.getString("measure_effort_days") + " Days and " + rs.getString("measure_effort_hrs") + " Hrs.";
				}
				hmGoalTargetMesures.put(rs.getString("goal_id"), uF.showData(measures, ""));

				hmGoalTargetDetails.put(rs.getString("goal_id"), innerList);
			}
			rs.close();
			pst.close();
				
			request.setAttribute("hmGoalTargetDetails", hmGoalTargetDetails);
			request.setAttribute("hmGoalTargetMesures", hmGoalTargetMesures);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void getGoalsIds( Connection con,  int subSectionID,  int systemType) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uf = new UtilityFunctions();

		try { 
			getGoalTargetDetails(con);
//			pst = con.prepareStatement("select * from appraisal_question_details where appraisal_id = ? and app_system_type = 3 order by appraisal_question_details_id"); // and appraisal_level_id = ?
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from appraisal_question_details a, goal_details g where g.goal_id=a.goal_kra_target_id and app_system_type=3 and appraisal_id=?");
			if(getEmpId()!=null && uf.parseToInt(getEmpId())>0) {
				sbQuery.append(" and g.emp_ids like '%,"+getEmpId()+",%' ");
			}
			sbQuery.append(" order by appraisal_question_details_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst = con.prepareStatement("select * from appraisal_question_details a, goal_details g where g.goal_id=a.goal_kra_target_id and app_system_type=3 " +
//				" and appraisal_id=? and g.emp_ids like '%,"+getEmpId()+",%' order by appraisal_question_details_id");
			pst.setInt(1, uf.parseToInt(getId()));
//			pst.setInt(2, subSectionID);
			rs = pst.executeQuery(); 
			Map<String, List<List<String>>> goalMp = new HashMap<String, List<List<String>>>();
			Map<String, List<List<String>>> hmGoalId = new HashMap<String, List<List<String>>>();
//			System.out.println("pst goalTargetIds=====>"+pst);
//			StringBuilder goalTargetIds = new StringBuilder();
			while (rs.next()) {
				
				List<List<String>> goalIDList =hmGoalId.get(rs.getString("appraisal_level_id")+"_"+rs.getString("app_system_type"));
				if(goalIDList==null) {
					goalIDList=new ArrayList<List<String>>();
				}
				List<String> innerlist = new ArrayList<String>();
				innerlist.add(rs.getString("question_id"));//0
				innerlist.add(rs.getString("answer_type"));//1
//				System.out.println("1 st ans type==>"+ rs.getString("answer_type"));
				innerlist.add(rs.getString("goal_kra_target_id"));//2
				innerlist.add(rs.getString("attribute_id"));//3
				innerlist.add(getQuestionName(con, uf.parseToInt(rs.getString("question_id"))));//4
				innerlist.add(rs.getString("weightage"));//5
				goalIDList.add(innerlist);
				hmGoalId.put(rs.getString("appraisal_level_id")+"_"+rs.getString("app_system_type"), goalIDList);
				goalMp.put(rs.getString("appraisal_level_id"), goalIDList);
			}
			rs.close();
			pst.close();
			
			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
			// list.add(otherMp);
			list.add(goalMp);
			levelMp.put(subSectionID + "", list);
			
//			System.out.println("hmGoalId =====> " + hmGoalId);
			request.setAttribute("hmGoalId", hmGoalId);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	private void getTargetIds( Connection con,  int subSectionID,  int systemType) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uf = new UtilityFunctions();

		try { 
			getGoalTargetDetails(con);
//			pst = con.prepareStatement("select * from appraisal_question_details where appraisal_id = ? and app_system_type = 5 order by appraisal_question_details_id"); // and appraisal_level_id = ?
//			pst = con.prepareStatement("select * from appraisal_question_details a, goal_details g where g.goal_id=a.goal_kra_target_id and app_system_type=5 " +
//				" and appraisal_id=? and g.emp_ids like '%,"+getEmpId()+",%' order by appraisal_question_details_id");
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from appraisal_question_details a, goal_details g where g.goal_id=a.goal_kra_target_id and app_system_type=5 and appraisal_id=? ");
			if(getEmpId()!=null && uf.parseToInt(getEmpId())>0) {
				sbQuery.append(" and g.emp_ids like '%,"+getEmpId()+",%' ");
			}
			sbQuery.append(" order by appraisal_question_details_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uf.parseToInt(getId()));
//			pst.setInt(2, subSectionID);
			rs = pst.executeQuery();
			Map<String, List<List<String>>> targetMp = new HashMap<String, List<List<String>>>();
			Map<String, List<List<String>>> hmTargetId = new HashMap<String, List<List<String>>>();
//			System.out.println("pst goalTargetIds =====>> " + pst);
//			StringBuilder goalTargetIds = new StringBuilder();
			while (rs.next()) {
				List<List<String>> targetIDList =hmTargetId.get(rs.getString("appraisal_level_id")+"_"+rs.getString("app_system_type"));
				if(targetIDList==null) {
					targetIDList=new ArrayList<List<String>>();
				}
				List<String> innerlist = new ArrayList<String>();
				innerlist.add(rs.getString("question_id"));//0
				innerlist.add(rs.getString("answer_type"));//1
//				System.out.println("2nd answer type==>"+rs.getString("answer_type"));
				innerlist.add(rs.getString("goal_kra_target_id"));//2
				innerlist.add(rs.getString("attribute_id"));//3
				innerlist.add(getQuestionName(con, uf.parseToInt(rs.getString("question_id"))));//4
				innerlist.add(rs.getString("weightage"));//5
				targetIDList.add(innerlist);
				hmTargetId.put(rs.getString("appraisal_level_id")+"_"+rs.getString("app_system_type"), targetIDList);
				targetMp.put(rs.getString("appraisal_level_id"), targetIDList);
			}
			rs.close();
			pst.close();
			
			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
			// list.add(otherMp);
			list.add(targetMp);
			levelMp.put(subSectionID + "", list);
			
//			System.out.println("pst hmTargetId =====> " + hmTargetId);
			request.setAttribute("hmTargetId", hmTargetId);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getQuestionName( Connection con,  int queID) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String queName = "";
		try { 
			getGoalTargetDetails(con);
			pst = con.prepareStatement("select question_bank_id,question_text from question_bank where question_bank_id = ?");
			pst.setInt(1, queID);
			rs = pst.executeQuery();
			while (rs.next()) {
				queName = rs.getString("question_text");
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return queName;
	}
	
	
	private void getKRAIds( Connection con,  int subSectionID,  int systemType) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uf = new UtilityFunctions();

		try { 
			getGoalTargetDetails(con);
			String kraTypes = INDIVIDUAL_GOAL+","+INDIVIDUAL_KRA+","+EMPLOYEE_KRA;
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from appraisal_question_details a, goal_kras g where goal_kra_id = kra_id and a.app_system_type = 4 and a.appraisal_id = ? and g.goal_type in ("+kraTypes+") ");
			if(getEmpId()!=null && uf.parseToInt(getEmpId())>0) {
				sbQuery.append(" and g.emp_ids like '%,"+getEmpId()+",%' ");
			}
			sbQuery.append(" order by appraisal_question_details_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst = con.prepareStatement("select * from appraisal_question_details where appraisal_id = ? and app_system_type = 4 order by appraisal_question_details_id"); // and appraisal_level_id = ?
			pst.setInt(1, uf.parseToInt(getId()));
//			pst.setInt(2, subSectionID);
			rs = pst.executeQuery();
			Map<String, List<List<String>>> kraMp = new HashMap<String, List<List<String>>>();
			Map<String, List<List<String>>> hmKRAId = new HashMap<String, List<List<String>>>();
//			System.out.println("pst goalTargetIds=====>"+pst);
//			StringBuilder goalTargetIds = new StringBuilder();
			while (rs.next()) {
				List<List<String>> kraIDList = hmKRAId.get(rs.getString("appraisal_level_id")+"_"+rs.getString("app_system_type"));
				if(kraIDList==null) {
					kraIDList=new ArrayList<List<String>>();
				}
				List<String> innerlist = new ArrayList<String>();
				innerlist.add(rs.getString("question_id"));//0
				innerlist.add(rs.getString("answer_type"));//1
//				System.out.println("3 rd answer type==>"+rs.getString("answer_type"));
				innerlist.add(rs.getString("kra_id"));//2
				innerlist.add(rs.getString("attribute_id"));//3
				innerlist.add(getQuestionName(con, uf.parseToInt(rs.getString("question_id"))));//4
				innerlist.add(rs.getString("weightage"));//5
				kraIDList.add(innerlist);
				hmKRAId.put(rs.getString("appraisal_level_id")+"_"+rs.getString("app_system_type"), kraIDList);
				kraMp.put(rs.getString("appraisal_level_id"), kraIDList); 
			}
			rs.close();
			pst.close();
			
			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
			// list.add(otherMp);
			list.add(kraMp);
			levelMp.put(subSectionID + "", list);
			
//			System.out.println("hmKRAId =====> " + hmKRAId);
//			System.out.println("hmGoalsKRAIds =====> " + hmGoalsKRAIds);
			request.setAttribute("hmKRAId", hmKRAId);
//			request.setAttribute("hmGoalsKRAIds", hmGoalsKRAIds);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void getKRADetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
//		UtilityFunctions uf = new UtilityFunctions();
		
		Map<String, List<String>> hmKRADetails = new LinkedHashMap<String, List<String>>();
		boolean levelFlag=false;

		try {
			con = db.makeConnection(con);

//			if(goalKraTargetIds != null && !goalKraTargetIds.toString().equals("")){
				StringBuilder sb = new StringBuilder();
				sb.append("select * from goal_kras k join goal_details g on k.goal_id=g.goal_id ");
//				sb.append(" and g.goal_id in ("+ goalKraTargetIds.toString() +") ");
				sb.append(" and g.goal_type=4 and (measure_type is null or measure_type='') and g.measure_kra is not null and g.measure_kra !='' order by k.goal_id");

				pst = con.prepareStatement(sb.toString());
				rs = pst.executeQuery();
//				System.out.println("pst getKRADetails ===> "+pst);
				while (rs.next()) {

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

					hmKRADetails.put(rs.getString("goal_kra_id"), innerList);
					
				}
				rs.close();
				pst.close();
				
				request.setAttribute("hmKRADetails", hmKRADetails);

				request.setAttribute("levelFlag", levelFlag);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
//	private Map<String, String> getGoalTargetKRAData( Connection con) {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//
//		Map<String, String> hmGoalTargetKRAData = new HashMap<String, String>();
//		try {
//
//			pst = con.prepareStatement("select * from goal_details g where g.goal_type=4");
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				hmGoalTargetKRAData.put(rs.getString("goal_id")+"_TITLE", rs.getString("goal_title"));
//			}
//
////			request.setAttribute("orientationMemberMp", orientationMemberMp);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return hmGoalTargetKRAData;
//	}
	
	
	private Map<String, String> getOrientationMember( Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		Map<String, String> orientationMemberMp = new HashMap<String, String>();
		try {

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
		}
		return orientationMemberMp;
	}

	
	private Map getScoreMarks() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		Map hmScoreDetailsMap = new HashMap();
		Map hmAggregateScoreDetailsMap = new HashMap();  //added by parvez date: 13-03-2023
		Map hmScoreAggregateMap = new HashMap();
		try {

			con = db.makeConnection(con);
			
	//===start parvez date: 21-03-2022===		
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con,request);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
			List<String> alFeatureUserTypeId = hmFeatureUserTypeId.get(F_ENABLE_BALANCE_SCORECARD_CALCULATION_USERTYPE);
			
			boolean isUserTypeRating = uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_BALANCE_SCORECARD_CALCULATION_USERTYPE));
			Map<String, String> hmRevieweeId = CF.getAppraisalRevieweesId(con, uF);
			if(hmRevieweeId == null) hmRevieweeId = new HashMap<String, String>();
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
	
	//===end parvez date: 21-03-2022===
			
			/*pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id=? and emp_id=? order by reviewer_or_appraiser,user_type_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			System.out.println("getScoreMarks pst ===> " + pst);
//			String strUserTypeNew = null;
//			String strUserTypeOld = null;
			Map hmAnswerScore = new HashMap();
			while (rs.next()) {
//				strUserTypeNew = rs.getString("user_type_id");
//				if(strUserTypeNew != null && !strUserTypeNew.equalsIgnoreCase(strUserTypeOld)) {
//					hmAnswerScore = new HashMap();
//				}
				if(rs.getInt("reviewer_or_appraiser") == 1) {
					hmAnswerScore = (Map)hmScoreDetailsMap.get("REVIEWER_"+rs.getString("reviewer_or_appraiser"));
					if(hmAnswerScore == null) hmAnswerScore = new HashMap();
				} else {
					hmAnswerScore = (Map)hmScoreDetailsMap.get(rs.getString("user_type_id")+"_"+rs.getString("reviewer_or_appraiser"));
					if(hmAnswerScore == null) hmAnswerScore = new HashMap();
				}
				double dblMarks = uF.parseToDouble(rs.getString("marks"));
				double dblWeightage = uF.parseToDouble(rs.getString("weightage"));
				double dblPercent = 0;
				if(dblWeightage>0) {
					dblPercent = dblMarks * 100 / dblWeightage;  
				}
				hmAnswerScore.put(rs.getString("question_id"), uF.getRoundOffValue(2, (dblPercent))+"%");
				if(rs.getInt("reviewer_or_appraiser") == 1) {
					hmScoreDetailsMap.put("REVIEWER_"+rs.getString("reviewer_or_appraiser"), hmAnswerScore);
				} else {
					hmScoreDetailsMap.put(rs.getString("user_type_id")+"_"+rs.getString("reviewer_or_appraiser"), hmAnswerScore);
				}
			}
			rs.close();
			pst.close();
			
			System.out.println("hmScoreDetailsMap :::::: "+hmScoreDetailsMap);
			request.setAttribute("hmScoreDetailsMap", hmScoreDetailsMap);*/
			
	//===start parvez date: 21-03-2022===
			Map<String,String> hmPriorUser = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> alPriorUserList = new ArrayList<String>();
				if(hmRevieweeId.get(rs.getString("appraisal_id")+"_"+getEmpId()+"_SUPERVISOR") != null && !hmRevieweeId.get(rs.getString("appraisal_id")+"_"+getEmpId()+"_SUPERVISOR").equals("")){
					alPriorUserList.add(rs.getString("manager"));
				}
				if(hmRevieweeId.get(rs.getString("appraisal_id")+"_"+getEmpId()+"_HOD") != null && !hmRevieweeId.get(rs.getString("appraisal_id")+"_"+getEmpId()+"_HOD").equals("")){
					alPriorUserList.add(rs.getString("hod"));
				}
				if(hmRevieweeId.get(rs.getString("appraisal_id")+"_"+getEmpId()+"_CEO") != null && !hmRevieweeId.get(rs.getString("appraisal_id")+"_"+getEmpId()+"_CEO").equals("")){
					alPriorUserList.add(rs.getString("ceo"));
				}
				if(hmRevieweeId.get(rs.getString("appraisal_id")+"_"+getEmpId()+"_HR") != null && !hmRevieweeId.get(rs.getString("appraisal_id")+"_"+getEmpId()+"_HR").equals("")){
					alPriorUserList.add(rs.getString("hr"));
				}
				if(hmRevieweeId.get(rs.getString("appraisal_id")+"_"+getEmpId()+"_PEER") != null && !hmRevieweeId.get(rs.getString("appraisal_id")+"_"+getEmpId()+"_PEER").equals("")){
					alPriorUserList.add(rs.getString("peer"));
				}
				if(hmRevieweeId.get(rs.getString("appraisal_id")+"_"+getEmpId()+"_OTHER_PEER") != null && !hmRevieweeId.get(rs.getString("appraisal_id")+"_"+getEmpId()+"_OTHER_PEER").equals("")){
					alPriorUserList.add(rs.getString("other_peer"));
				}
				if(hmRevieweeId.get(rs.getString("appraisal_id")+"_"+getEmpId()+"_SUBORDINATE") != null && !hmRevieweeId.get(rs.getString("appraisal_id")+"_"+getEmpId()+"_SUBORDINATE").equals("")){
					alPriorUserList.add(rs.getString("subordinate"));
				}
				
				Collections.sort(alPriorUserList,Collections.reverseOrder());
				
				int priorUserTypeId = 0;
				
		//===start parvez date: 21-03-2022===	
				if(alPriorUserList !=null && !alPriorUserList.isEmpty()){
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
		//===end parvez date: 21-03-2022===
				
				hmPriorUser.put(rs.getString("main_level_id"), priorUserTypeId+"");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmPriorUser", hmPriorUser);
			
	//===end parvez date: 21-03-2022===		
			
	//===start parvez date: 13-03-2023===	
//			pst = con.prepareStatement("select user_type_id, sum(marks) as marks, sum(weightage) as weightage, aqw.appraisal_level_id,reviewer_or_appraiser" +
//				",aqw.question_id from appraisal_question_answer aqw, appraisal_level_details ald " +
//				" where ald.appraisal_id = aqw.appraisal_id and aqw.appraisal_level_id = ald.appraisal_level_id and aqw.appraisal_id=? and emp_id= ? " +
//				" group by aqw.appraisal_level_id,user_type_id,reviewer_or_appraiser,aqw.question_id order by reviewer_or_appraiser,user_type_id,question_id");
			
			/*pst = con.prepareStatement("select user_type_id, sum(marks) as marks, sum(weightage) as weightage, aqw.appraisal_level_id,reviewer_or_appraiser" +
					",aqw.question_id,ald.main_level_id from appraisal_question_answer aqw, appraisal_level_details ald " +
					" where ald.appraisal_id = aqw.appraisal_id and aqw.appraisal_level_id = ald.appraisal_level_id and aqw.appraisal_id=? and emp_id= ? and aqw.is_submit=true " +
					" group by aqw.appraisal_level_id,user_type_id,reviewer_or_appraiser,aqw.question_id,ald.main_level_id order by reviewer_or_appraiser,user_type_id,question_id");*/
			
			/*pst = con.prepareStatement("select user_type_id, sum(marks) as marks, sum(aqw.weightage) as weightage, aqw.appraisal_level_id,reviewer_or_appraiser" +
						",aqw.question_id,ald.main_level_id, score_calculation_basis from appraisal_question_answer aqw, appraisal_level_details ald, appraisal_question_details aqd " +
						" where ald.appraisal_id = aqw.appraisal_id and aqw.appraisal_level_id = ald.appraisal_level_id and aqw.appraisal_id=? and emp_id= ? and aqw.is_submit=true " +
						" and aqw.appraisal_question_details_id=aqd.appraisal_question_details_id" +
						" group by aqw.appraisal_level_id,user_type_id,reviewer_or_appraiser,aqw.question_id,ald.main_level_id,score_calculation_basis order by reviewer_or_appraiser,user_type_id,question_id");*/
			pst = con.prepareStatement("select user_type_id,user_id, sum(marks) as marks, sum(aqw.weightage) as weightage, aqw.appraisal_level_id,reviewer_or_appraiser" +
					",aqw.question_id,ald.main_level_id, score_calculation_basis,appraisal_system from appraisal_question_answer aqw, appraisal_level_details ald, appraisal_question_details aqd " +
					" where ald.appraisal_id = aqw.appraisal_id and aqw.appraisal_level_id = ald.appraisal_level_id and aqw.appraisal_id=? and emp_id= ? and aqw.is_submit=true " +
					" and aqw.appraisal_question_details_id=aqd.appraisal_question_details_id" +
					" group by aqw.appraisal_level_id,user_type_id,user_id,reviewer_or_appraiser,aqw.question_id,ald.main_level_id,score_calculation_basis,appraisal_system order by reviewer_or_appraiser,user_type_id,question_id");
		//===end parvez date: 13-03-2023===		
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
//			System.out.println("ApSum/1427--pst ===>> " + pst);
			rs = pst.executeQuery();
//			System.out.println("ApSum/1376--pst ===>> " + pst);
			double dblTotalQueMarks = 0;
			double dblTotalQueWeightage = 0;
			Map hmQueTemp = new HashMap();
			Map<String, String> hmQueTemp1 = new HashMap<String, String>();
			Map<String, Map<String, String>> hmTempMarkScore = new HashMap<String, Map<String, String>>();
			
			boolean isSelfRating = CF.getFeatureManagementStatus(request, uF, F_DISABLE_SELF_APPRAISAL_RATING_DURING_FINAL_RATING_CALCULATION);
			boolean actualCalBais = false; //added by parvez date:01-03-2023
			while (rs.next()) {
//					strUserTypeNew = rs.getString("user_type_id"); 
				
				if(rs.getInt("reviewer_or_appraiser") == 1) {
					hmQueTemp = (Map)hmScoreDetailsMap.get("REVIEWER_"+rs.getString("reviewer_or_appraiser"));
					if(hmQueTemp == null)hmQueTemp = new HashMap();
					
					hmQueTemp1 = hmTempMarkScore.get("REVIEWER_"+rs.getString("reviewer_or_appraiser"));
					if(hmQueTemp1 == null)hmQueTemp1 = new HashMap<String, String>();
				} else {
					hmQueTemp = (Map)hmScoreDetailsMap.get(rs.getString("user_type_id")+"_"+rs.getString("reviewer_or_appraiser"));
					if(hmQueTemp == null)hmQueTemp = new HashMap();
					
					hmQueTemp1 = hmTempMarkScore.get(rs.getString("user_type_id")+"_"+rs.getString("reviewer_or_appraiser"));
					if(hmQueTemp1 == null)hmQueTemp1 = new HashMap<String, String>();
				}
//				dblTotalQueMarks = uF.parseToDouble(rs.getString("marks"));
//				dblTotalQueWeightage = uF.parseToDouble(rs.getString("weightage"));
				dblTotalQueMarks = uF.parseToDouble(rs.getString("marks"))+uF.parseToDouble(hmQueTemp1.get(rs.getString("question_id")+"_MARKS"));
				dblTotalQueWeightage = uF.parseToDouble(rs.getString("weightage"))+uF.parseToDouble(hmQueTemp1.get(rs.getString("question_id")+"_WEIGHTAGE"));

				hmQueTemp1.put(rs.getString("question_id")+"_MARKS", dblTotalQueMarks+"");
				hmQueTemp1.put(rs.getString("question_id")+"_WEIGHTAGE", dblTotalQueWeightage+"");
				if(dblTotalQueWeightage>0 && rs.getString("marks")!=null){
				//===start parvez date: 01-03-2023===	
//					hmQueTemp.put(rs.getString("question_id"), uF.getRoundOffValue(2,(((dblTotalQueMarks / dblTotalQueWeightage) * 100)))+"%");
					if(rs.getBoolean("score_calculation_basis")){
						hmQueTemp.put(rs.getString("question_id"), uF.getRoundOffValue(1,((((dblTotalQueMarks / dblTotalQueWeightage) * 100)/20))));
						actualCalBais = true;
					} else{
						hmQueTemp.put(rs.getString("question_id"), uF.getRoundOffValue(2,(((dblTotalQueMarks / dblTotalQueWeightage) * 100)))+"%");
					}
				//===end parvez date: 01-03-2023===	
				}
				
			//===start parvez date: 01-03-2023===	
				hmQueTemp.put("ACTUAL_CAL_BASIS", actualCalBais+"");
			//===end parvez date: 01-03-2023===	
				
				//===start parvez date: 13-03-2023===	
				if(rs.getInt("reviewer_or_appraiser") == 1) {
					hmScoreDetailsMap.put("REVIEWER_"+rs.getString("reviewer_or_appraiser"), hmQueTemp);
					hmTempMarkScore.put("REVIEWER_"+rs.getString("reviewer_or_appraiser"), hmQueTemp1);
					hmAggregateScoreDetailsMap.put("REVIEWER_"+rs.getString("user_id")+"_"+rs.getString("reviewer_or_appraiser"), hmQueTemp);
				} else {
				
				
//					hmScoreDetailsMap.put(rs.getString("user_type_id")+"_"+rs.getString("reviewer_or_appraiser"), hmQueTemp);
					if(uF.parseToInt(rs.getString("user_type_id")) != 3 || !isSelfRating){
						
						if(!isUserTypeRating || (isUserTypeRating && alFeatureUserTypeId.contains(rs.getString("user_type_id")) && uF.parseToInt(rs.getString("user_type_id")) == uF.parseToInt(hmPriorUser.get(rs.getString("main_level_id"))))){
							hmScoreDetailsMap.put(rs.getString("user_type_id")+"_"+rs.getString("reviewer_or_appraiser"), hmQueTemp);
							hmTempMarkScore.put(rs.getString("user_type_id")+"_"+rs.getString("reviewer_or_appraiser"), hmQueTemp1);
							hmAggregateScoreDetailsMap.put(rs.getString("user_type_id")+"_"+rs.getString("user_id")+"_"+rs.getString("reviewer_or_appraiser"), hmQueTemp);
						}
						
//						System.out.println("ApSum/1408--user_type_id="+rs.getString("user_type_id")+"");
					}
				//===end parvez date: 13-03-2023===
				}
			}
			rs.close();
			pst.close();
//			System.out.println("ApSum/1539--hmAggregateScoreDetailsMap ===>> " + hmAggregateScoreDetailsMap);
//			System.out.println("ApSum/1540--hmScoreDetailsMap ===>> " + hmScoreDetailsMap);
			request.setAttribute("hmScoreDetailsMap", hmScoreDetailsMap);
			request.setAttribute("hmAggregateScoreDetailsMap", hmAggregateScoreDetailsMap);
				
	//===start parvez date: 28-02-2023===
//			pst = con.prepareStatement("select user_type_id, sum(marks) as marks, sum(weightage) as weightage, aqw.appraisal_level_id,reviewer_or_appraiser " +
//				" from appraisal_question_answer aqw, appraisal_level_details ald " +
//				" where ald.appraisal_id = aqw.appraisal_id and aqw.appraisal_level_id = ald.appraisal_level_id and aqw.appraisal_id=? and emp_id= ? " +
//				" group by aqw.appraisal_level_id,user_type_id,reviewer_or_appraiser order by reviewer_or_appraiser,user_type_id");
			
			/*pst = con.prepareStatement("select user_type_id, sum(marks) as marks, sum(weightage) as weightage, aqw.appraisal_level_id,reviewer_or_appraiser,aqw.is_submit " +
					" from appraisal_question_answer aqw, appraisal_level_details ald " +
					" where ald.appraisal_id = aqw.appraisal_id and aqw.appraisal_level_id = ald.appraisal_level_id and aqw.appraisal_id=? and emp_id= ? " +
					" group by aqw.appraisal_level_id,user_type_id,reviewer_or_appraiser,aqw.is_submit order by reviewer_or_appraiser,user_type_id");*/
			
			pst = con.prepareStatement("select user_type_id, sum(marks) as marks, sum(aqw.weightage) as weightage, aqw.appraisal_level_id,reviewer_or_appraiser,aqw.is_submit,score_calculation_basis " +
					" from appraisal_question_answer aqw, appraisal_level_details ald, appraisal_question_details aqd " +
					" where ald.appraisal_id = aqw.appraisal_id and aqw.appraisal_level_id = ald.appraisal_level_id and aqw.appraisal_id=? and emp_id= ? and aqw.appraisal_question_details_id=aqd.appraisal_question_details_id " +
					" group by aqw.appraisal_level_id,user_type_id,reviewer_or_appraiser,aqw.is_submit,aqd.score_calculation_basis order by reviewer_or_appraiser,user_type_id");
			
	//===end parvez date: 28-02-2023===
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
//			System.out.println("ApSum/1433---pst ===>> " + pst);
			double dblTotalMarks = 0;
			double dblTotalWeightage = 0;
			Map hmTemp = new HashMap();
			while (rs.next()) {
//				strUserTypeNew = rs.getString("user_type_id");
				if(rs.getInt("reviewer_or_appraiser") == 1) {
					hmTemp = (Map)hmScoreAggregateMap.get("REVIEWER_"+rs.getString("reviewer_or_appraiser"));
					if(hmTemp == null)	hmTemp = new HashMap();
				} else {
					hmTemp = (Map)hmScoreAggregateMap.get(rs.getString("user_type_id")+"_"+rs.getString("reviewer_or_appraiser"));
					if(hmTemp == null)	hmTemp = new HashMap();
				}
				
				
				dblTotalMarks = uF.parseToDouble(rs.getString("marks"));
				dblTotalWeightage = uF.parseToDouble(rs.getString("weightage"));
				
				/*if(dblTotalWeightage>0 && rs.getString("marks")!=null){
					hmTemp.put(rs.getString("appraisal_level_id"), uF.getRoundOffValue(2,(((dblTotalMarks / dblTotalWeightage) * 100)))+"%");
				}*/
				if(dblTotalWeightage>0 && rs.getString("marks")!=null && rs.getBoolean("is_submit")){
//					hmTemp.put(rs.getString("appraisal_level_id"), uF.getRoundOffValue(2,(((dblTotalMarks / dblTotalWeightage) * 100)))+"%");
				//===start parvez date: 01-03-2023===	
					if(rs.getBoolean("score_calculation_basis")){
						hmTemp.put(rs.getString("appraisal_level_id"), uF.getRoundOffValue(1,((((dblTotalMarks / dblTotalWeightage) * 100)/20))));
					} else{
						hmTemp.put(rs.getString("appraisal_level_id"), uF.getRoundOffValue(2,(((dblTotalMarks / dblTotalWeightage) * 100)))+"%");
					}
				//===end parvez date: 01-03-2023===	
				}
				
				if(rs.getInt("reviewer_or_appraiser") == 1) {
					hmScoreAggregateMap.put("REVIEWER_"+rs.getString("reviewer_or_appraiser"), hmTemp);
				} else {
					hmScoreAggregateMap.put(rs.getString("user_type_id")+"_"+rs.getString("reviewer_or_appraiser"), hmTemp);
				}
//				strUserTypeOld = strUserTypeNew;
			}
			rs.close();
			pst.close();
//			System.out.println("hmScoreAggregateMap ===>> " + hmScoreAggregateMap);
			request.setAttribute("hmScoreAggregateMap", hmScoreAggregateMap);
			
			
			pst = con.prepareStatement("Select sattlement_comment,if_approved,user_id, emp_fname,emp_mname, emp_lname, _date from appraisal_final_sattlement afs, employee_personal_details epd where afs.user_id = epd.emp_per_id and appraisal_id=? and emp_id=? ");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
//			System.out.println("pst===>"+pst);
			String strComments = null;
			String strAppraisedBy = null;
			String strAppraisedOn = null;
			while(rs.next()){
				strComments = rs.getString("sattlement_comment");
				if(strComments!=null){
					strComments = strComments.replace("\n", "<br/>");
				}
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				strAppraisedBy = rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname");
				strAppraisedOn = uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat());
			}
			rs.close();
			pst.close();
			
			request.setAttribute("strComments", strComments);
			request.setAttribute("strAppraisedBy", strAppraisedBy);
			request.setAttribute("strAppraisedOn", strAppraisedOn);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmScoreDetailsMap;
	}
	
	
	private Map<String, String> getOrientationValue( Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		Map<String, String> orientationMp = new HashMap<String, String>();
		Map<String, String> hmorientationMembers = new HashMap<String, String>();
		try {

			pst = con.prepareStatement("select * from apparisal_orientation");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMp.put(rs.getString("apparisal_orientation_id"), rs.getString("orientation_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("orientationMp", orientationMp);
			
			pst = con.prepareStatement("select * from orientation_member");
			rs = pst.executeQuery();
			while (rs.next()) {
				//hmorientationMembers.put(rs.getString("orientation_member_id"), rs.getString("member_name"));
				hmorientationMembers.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmorientationMembers", hmorientationMembers);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMp;
	}

	
	
	private void getAttributeDetails() {
		Map<String, String> mpAttribute = new HashMap<String, String>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_attribute where status=true");
			rs = pst.executeQuery();
			while (rs.next()) {
				mpAttribute.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
			}
			rs.close();
			pst.close();
			request.setAttribute("mpAttribute", mpAttribute);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	
	
	public  void getDataMeasure( int id) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, String> appQueAnsType = (Map<String, String>)request.getAttribute("appQueAnsType");

		try {
			con = db.makeConnection(con);
			Map<String, String> AppraisalQuestion = getAppraisalQuestionMap(con, uF);
			
			Map<String, String> attributeMp = getAttributeMap(con);
			Map<String, String> answertypeMp = getAnswerTypeMap(con);
			pst = con.prepareStatement("select * from appraisal_scorecard_details where level_id =? and appraisal_id=? order by scorecard_id");
			pst.setInt(1, id);
			pst.setInt(2, uF.parseToInt(getId()));

			rs = pst.executeQuery();
			Map<String, List<List<String>>> scoreMp = new HashMap<String, List<List<String>>>();
			String scorecard_id = null;
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					scorecard_id = rs.getString("scorecard_id");
				} else {
					scorecard_id += "," + rs.getString("scorecard_id");
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(rs.getString("scorecard_section_name"));
				innerList.add(rs.getString("scorecard_weightage"));
				innerList.add(uF.showData(attributeMp.get(rs.getString("appraisal_attribute")), ""));
				innerList.add(uF.showData(answertypeMp.get(id+"_"+getId()), ""));
				innerList.add(rs.getString("appraisal_attribute"));
				
				List<List<String>> outerList = scoreMp.get(rs.getString("level_id"));
				if (outerList == null) {
					outerList = new ArrayList<List<String>>();
				}
				outerList.add(innerList);
				scoreMp.put(rs.getString("level_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_measure_details where scorecard_id in(" + scorecard_id + ") and appraisal_id=? order by measure_id");
			pst.setInt(1, uF.parseToInt(getId()));

			rs = pst.executeQuery();
			Map<String, List<List<String>>> measureMp = new HashMap<String, List<List<String>>>();
			String measure_id = null;
			i = 0;
			while (rs.next()) {
				if (i == 0) {
					measure_id = rs.getString("measure_id");
				} else {
					measure_id += "," + rs.getString("measure_id");
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("measure_section_name"));
				innerList.add(rs.getString("weightage"));
				List<List<String>> outerList = measureMp.get(rs.getString("scorecard_id"));
				if (outerList == null) {
					outerList = new ArrayList<List<String>>();
				}
				outerList.add(innerList);
				measureMp.put(rs.getString("scorecard_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_question_details where measure_id in(" + measure_id + ") and appraisal_id=? order by appraisal_question_details_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("appraisal_question_details_id"));

				List<List<String>> outerList = questionMp.get(rs.getString("measure_id"));
				if (outerList == null) {
					outerList = new ArrayList<List<String>>();
				}
				outerList.add(innerList);
				questionMp.put(rs.getString("measure_id"), outerList);
			}
			rs.close();
			pst.close();
			
			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
			list.add(scoreMp);
			list.add(measureMp);
			list.add(questionMp);
			//list.add(questionMp);
			levelMp.put(id + "", list);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public  void getDataMeasureGoal( int id) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			Map<String, String> AppraisalQuestion = getAppraisalQuestionMap(con, uF);

			Map<String, String> attributeMp = getAttributeMap(con);
			Map<String, String> answertypeMp = getAnswerTypeMap(con);
			pst = con.prepareStatement("select * from appraisal_scorecard_details where level_id=? and appraisal_id=? order by scorecard_id");
			pst.setInt(1, id);
			pst.setInt(2, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> scoreMp = new HashMap<String, List<List<String>>>();
			String scorecard_id = null;
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					scorecard_id = rs.getString("scorecard_id");
				} else {
					scorecard_id += "," + rs.getString("scorecard_id");
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(rs.getString("scorecard_section_name"));
				innerList.add(rs.getString("scorecard_weightage"));
				innerList.add(uF.showData(attributeMp.get(rs.getString("appraisal_attribute")), ""));
				innerList.add(uF.showData(answertypeMp.get(id+"_"+getId()), ""));
				innerList.add(rs.getString("appraisal_attribute"));

				List<List<String>> outerList = scoreMp.get(rs.getString("level_id"));
				if (outerList == null) {
					outerList = new ArrayList<List<String>>();
				}
				outerList.add(innerList);
				scoreMp.put(rs.getString("level_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_goal_details where scorecard_id in(" + scorecard_id + ") and appraisal_id=? order by goal_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> GoalMp = new HashMap<String, List<List<String>>>();
			String goal_id = null;
			i = 0;
			while (rs.next()) {
				if (i == 0) {
					goal_id = rs.getString("goal_id");
				} else {
					goal_id += "," + rs.getString("goal_id");
				}
				i++;
				List<String> innerGoalList = new ArrayList<String>();
				innerGoalList.add(rs.getString("goal_id"));
				// innerGoalList.add(rs.getString("scorecard_id"));
				innerGoalList.add(rs.getString("goal_section_name"));
				// innerGoalList.add(rs.getString("goal_description"));
				innerGoalList.add(rs.getString("goal_weightage"));

				List<List<String>> outerGoalList = GoalMp.get(rs.getString("scorecard_id"));
				if (outerGoalList == null) {
					outerGoalList = new ArrayList<List<String>>();
				}
				outerGoalList.add(innerGoalList);
				// outerGoalList.add(innerGoalList);
				GoalMp.put(rs.getString("scorecard_id"), outerGoalList);
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from appraisal_measure_details where goal_id in(" + goal_id + ") and appraisal_id=? order by measure_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> measureMp = new HashMap<String, List<List<String>>>();
			String measure_id = null;
			i = 0;
			while (rs.next()) {
				if (i == 0) {
					measure_id = rs.getString("measure_id");
				} else {
					measure_id += "," + rs.getString("measure_id");
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("measure_section_name"));
				innerList.add(rs.getString("weightage"));
				List<List<String>> outerList = measureMp.get(rs.getString("goal_id"));
				if (outerList == null) {
					outerList = new ArrayList<List<String>>();
				}
				outerList.add(innerList);
				measureMp.put(rs.getString("goal_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_question_details where measure_id in (" + measure_id + ") and appraisal_id=? order by appraisal_question_details_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("appraisal_question_details_id"));

				List<List<String>> outerList = questionMp.get(rs.getString("measure_id"));
				if (outerList == null) {
					outerList = new ArrayList<List<String>>();
				}
				outerList.add(innerList);
				questionMp.put(rs.getString("measure_id"), outerList);
			}
			rs.close();
			pst.close();
			
			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
			list.add(scoreMp);
			list.add(measureMp);
			list.add(questionMp);
			list.add(GoalMp);
			levelMp.put(id + "", list);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	
	public  void getDataObjectiveMeasureGoal( int id) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			Map<String, String> AppraisalQuestion = getAppraisalQuestionMap(con, uF);

			Map<String, String> attributeMp = getAttributeMap(con);
			Map<String, String> answertypeMp = getAnswerTypeMap(con);
			pst = con.prepareStatement("select * from appraisal_scorecard_details where level_id =? and appraisal_id=? order by scorecard_id");
			pst.setInt(1, id);
			pst.setInt(2, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> scoreMp = new HashMap<String, List<List<String>>>();
			String scorecard_id = null;
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					scorecard_id = rs.getString("scorecard_id");
				} else {
					scorecard_id += "," + rs.getString("scorecard_id");
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(rs.getString("scorecard_section_name"));
				innerList.add(rs.getString("scorecard_weightage"));
				innerList.add(uF.showData(attributeMp.get(rs.getString("appraisal_attribute")), ""));
				innerList.add(uF.showData(answertypeMp.get(id+"_"+getId()), ""));
				innerList.add(rs.getString("appraisal_attribute"));
				
				List<List<String>> outerList = scoreMp.get(rs.getString("level_id"));
				if (outerList == null) {
					outerList = new ArrayList<List<String>>();
				}
				outerList.add(innerList);
				scoreMp.put(rs.getString("level_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_goal_details where scorecard_id in(" + scorecard_id + ") and appraisal_id=? order by goal_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> GoalMp = new HashMap<String, List<List<String>>>();
			String goal_id = null;
			i = 0;
			while (rs.next()) {
				if (i == 0) {
					goal_id = rs.getString("goal_id");
				} else {
					goal_id += "," + rs.getString("goal_id");
				}
				i++;
				List<String> innerGoalList = new ArrayList<String>();
				innerGoalList.add(rs.getString("goal_id"));
				// innerGoalList.add(rs.getString("scorecard_id"));
				innerGoalList.add(rs.getString("goal_section_name"));
				// innerGoalList.add(rs.getString("goal_description"));
				innerGoalList.add(rs.getString("goal_weightage"));
				List<List<String>> outerGoalList = GoalMp.get(rs.getString("scorecard_id"));
				if (outerGoalList == null) {
					outerGoalList = new ArrayList<List<String>>();
				}
				outerGoalList.add(innerGoalList);
				// outerGoalList.add(innerGoalList);
				GoalMp.put(rs.getString("scorecard_id"), outerGoalList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_objective_details where goal_id in(" + goal_id + ") and appraisal_id=? order by objective_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> objectiveMp = new HashMap<String, List<List<String>>>();
			String objective_id = null;
			i = 0;
			while (rs.next()) {
				if (i == 0) {
					objective_id = rs.getString("objective_id");
				} else {
					objective_id += "," + rs.getString("objective_id");
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("objective_id"));
				innerList.add(rs.getString("objective_section_name"));
				innerList.add(rs.getString("objective_weightage"));

				List<List<String>> outerList = objectiveMp.get(rs.getString("goal_id"));
				if (outerList == null) {
					outerList = new ArrayList<List<String>>();
				}
				outerList.add(innerList);
				objectiveMp.put(rs.getString("goal_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_measure_details where objective_id in(" + objective_id + ") and appraisal_id=? order by measure_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> measureMp = new HashMap<String, List<List<String>>>();
			String measure_id = null;
			i = 0;
			while (rs.next()) {
				if (i == 0) {
					measure_id = rs.getString("measure_id");
				} else {
					measure_id += "," + rs.getString("measure_id");
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("measure_section_name"));
				innerList.add(rs.getString("weightage"));
				List<List<String>> outerList = measureMp.get(rs.getString("objective_id"));
				if (outerList == null) {
					outerList = new ArrayList<List<String>>();
				}
				outerList.add(innerList);
				measureMp.put(rs.getString("objective_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_question_details where measure_id in(" + measure_id + ") and appraisal_id=? order by appraisal_question_details_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("appraisal_question_details_id"));
//				System.out.println("4nd answer type==>"+rs.getString("answer_type"));
				innerList.add(rs.getString("answer_type"));
				innerList.add(rs.getString("attribute_id"));
				
				List<List<String>> outerList = questionMp.get(rs.getString("measure_id"));
				if (outerList == null) {
					outerList = new ArrayList<List<String>>();
				}
				outerList.add(innerList);
				questionMp.put(rs.getString("measure_id"), outerList);
			}
			rs.close();
			pst.close();
			
			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
			list.add(scoreMp);
			list.add(measureMp);
			list.add(questionMp);
			list.add(GoalMp);
			list.add(objectiveMp);

			levelMp.put(id + "", list);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	

	public  void getReport() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmUserName = getSessionUserName(con);
			pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id=? order by main_level_id ");
			pst.setInt(1, uF.parseToInt(getId()));
//			System.out.println("pst1==>"+pst);
			rs = pst.executeQuery();
			List<List<String>> mainLevelList = new LinkedList<List<String>>();
			while (rs.next()) {

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("main_level_id"));//0
				innerList.add(rs.getString("level_title"));//1
				innerList.add(rs.getString("short_description"));//2
				innerList.add(rs.getString("long_description"));//3
				innerList.add(rs.getString("appraisal_id"));//4
				innerList.add(rs.getString("section_weightage"));//5
				
				innerList.add(hmUserName.get(rs.getString("added_by")));//6
				innerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));//7
				mainLevelList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("mainLevelList", mainLevelList);
//			System.out.println("mainLevelList==>"+mainLevelList.size());
			
			pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id=? order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
//			System.out.println("pst2=>"+pst);
			Map<String, List<List<String>>> hmSystemLevelMp = new LinkedHashMap<String, List<List<String>>>();
			Map<String, String> attributeMp = getAttributeMap(con);
			while (rs.next()) {

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_level_id"));//0
				innerList.add(rs.getString("level_title"));//1
				innerList.add(rs.getString("scorecard_type"));//2
				innerList.add(rs.getString("appraisal_system"));//3
				innerList.add(rs.getString("short_description"));//4
				innerList.add(rs.getString("long_description"));//5
				innerList.add(attributeMp.get(rs.getString("attribute_id")));//6
				innerList.add(rs.getString("subsection_weightage"));//7
				innerList.add(hmUserName.get(rs.getString("added_by")));//8
				innerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));//9
				innerList.add(rs.getString("appraisal_level_id"));//10
				
				if (uF.parseToInt(rs.getString("appraisal_system")) == 1) {

					if (uF.parseToInt(rs.getString("scorecard_type")) == 1) {
						getDataObjectiveMeasureGoal(uF.parseToInt(rs.getString("appraisal_level_id")));
					}
					if (uF.parseToInt(rs.getString("scorecard_type")) == 2) {
						getDataMeasure(uF.parseToInt(rs.getString("appraisal_level_id")));
					} else if (uF.parseToInt(rs.getString("scorecard_type")) == 3) {
						getDataMeasureGoal(uF.parseToInt(rs.getString("appraisal_level_id")));
					}
				} else if (uF.parseToInt(rs.getString("appraisal_system")) == 2) {
					// code for other option
					getOtherData(uF.parseToInt(rs.getString("appraisal_level_id")));
				} else if (uF.parseToInt(rs.getString("appraisal_system")) == 3) {
					// code for Goal option
					getGoalsIds(con, uF.parseToInt(rs.getString("appraisal_level_id")), uF.parseToInt(rs.getString("appraisal_system")));
				} else if (uF.parseToInt(rs.getString("appraisal_system")) == 5) {
					// code for Target option
					getTargetIds(con, uF.parseToInt(rs.getString("appraisal_level_id")), uF.parseToInt(rs.getString("appraisal_system")));
				} else if (uF.parseToInt(rs.getString("appraisal_system")) == 4) {
					// code for KRA option
					getKRAIds(con, uF.parseToInt(rs.getString("appraisal_level_id")), uF.parseToInt(rs.getString("appraisal_system")));
				}
				
				innerList.add(rs.getString("main_level_id"));
				
				List<List<String>> outerList1 = hmSystemLevelMp.get(rs.getString("main_level_id"));
				if (outerList1 == null) {
					outerList1 = new ArrayList<List<String>>();
				}
				outerList1.add(innerList);
				hmSystemLevelMp.put(rs.getString("main_level_id"), outerList1);
				
			}
			rs.close();
			pst.close();
			request.setAttribute("hmSystemLevelMp", hmSystemLevelMp);
//			System.out.println("hmSystemLevelMp =====> "+hmSystemLevelMp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	
	public  void getOtherData( int id) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> AppraisalQuestion = getAppraisalQuestionMap(con, uF);
			Map<String, String> appOtherQueType = getAppraisalOtherQueType(con, uF);

			pst = con.prepareStatement("select * from appraisal_other_question_type_details where level_id =?");
			pst.setInt(1, id);
			rs = pst.executeQuery();
			String othe_question_type_id = null;
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					othe_question_type_id = rs.getString("othe_question_type_id");
				} else {
					othe_question_type_id += "," + rs.getString("othe_question_type_id");
				}
				i++;
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_question_details where other_id in(" + othe_question_type_id + ") order by appraisal_question_details_id");
			rs = pst.executeQuery();
			Map<String, List<List<String>>> questionMp = new LinkedHashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));//0
				innerList.add(rs.getString("weightage"));//1
//				System.out.println("5nd answer type==>"+rs.getString("answer_type"));
				innerList.add(rs.getString("answer_type"));//2
				//System.out.println("ans_type==>"+rs.getString("answer_type"));
				innerList.add(uF.showData(appOtherQueType.get(rs.getString("other_id")), ""));//3
				innerList.add(rs.getString("other_short_description"));//4
				innerList.add(rs.getString("appraisal_question_details_id"));//5
				innerList.add(rs.getString("question_id"));//6
				innerList.add(rs.getString("attribute_id"));//7

				List<List<String>> outerList = questionMp.get(id + "");
				if (outerList == null) {
					outerList = new ArrayList<List<String>>();
				}
				outerList.add(innerList);
				questionMp.put(id + "", outerList);

			}
			rs.close();
			pst.close();
//			System.out.println("questionMp =====> "+questionMp);
			List<Map<String, List<List<String>>>> list = new LinkedList<Map<String, List<List<String>>>>();
			// list.add(otherMp);
			list.add(questionMp);
			levelMp.put(id + "", list);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	

	public void getAppraisalDetail() {

		Connection con = null;
		PreparedStatement pst = null; 
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmDesignation = CF.getDesigMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmLevelMap = getLevelMap(con);
			Map<String, String> hmLocation = getLocationMap(con);
			Map<String, String> mpdepart = CF.getDeptMap(con);
			Map<String,String> hmAnswerType = CF.getAnswerTypeMap(con);
			
			Map<String, String> orientationMp = getOrientationValue(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			
			List<String> appQueAnsStatusList = CF.getAppraisalQueAnsStatus(con);
			
			Map<String, String> hmFrequency = new HashMap<String, String>();
	    	Map<String, String> hmAppraisalCount = new HashMap<String, String>();
	    	
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmFrequency.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();
			
			boolean flagProcess = false;
			Map<String, String> hmSections = new HashMap<String, String>();
			Map<String, String> hmSubSections = new HashMap<String, String>();
			Map<String, String> hmQuestions = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id=? ");
			pst.setInt(1, uF.parseToInt(getId()));
		   
			rs = pst.executeQuery();
			while (rs.next()) {
				flagProcess = true;
				hmSections.put(rs.getString("section_id"), rs.getString("section_id"));
				hmSubSections.put(rs.getString("appraisal_level_id"), rs.getString("appraisal_level_id"));
				hmQuestions.put(rs.getString("question_id"), rs.getString("question_id"));
			}
			rs.close();
			pst.close();
		
			request.setAttribute("flagProcess", flagProcess);
			request.setAttribute("hmSections", hmSections);
			request.setAttribute("hmSubSections", hmSubSections);
			request.setAttribute("hmQuestions", hmQuestions);
			request.setAttribute("hmAnswerType", hmAnswerType);
			
			
//			System.out.println("AppFreq ID ===> "+getAppFreqId());
			List<String> appraisalList = new ArrayList<String>();
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?" );
			pst.setInt(1, uF.parseToInt(getId()));
//			System.out.println("ApS/2345--pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			while (rs.next()) {
				String memberName = "";
				if(rs.getString("usertype_member") != null) {
					List<String> memberList = Arrays.asList(rs.getString("usertype_member").split(","));
					for (int i = 0; i < memberList.size(); i++) {
						if (i == 0) {
							memberName += orientationMemberMp.get(memberList.get(i));
						} else {
							memberName += ", " + orientationMemberMp.get(memberList.get(i));
						}
					}
				}
				if(memberName == null || memberName.equals("null")) {
					memberName = "Anyone"; 
				}
				appraisalList.add(uF.showData(rs.getString("appraisal_details_id"), ""));//0
				appraisalList.add(uF.showData(rs.getString("appraisal_name"), ""));//1
				oreinted = rs.getString("oriented_type");
				appraisalList.add(uF.showData(orientationMp.get(rs.getString("oriented_type")) + "&deg( " + memberName + " )", ""));//2
				appraisalList.add(uF.showData(rs.getString("self_ids"), ""));//3
				appraisalList.add(uF.showData(getAppendData(rs.getString("level_id"), hmLevelMap), ""));//4
				appraisalList.add(uF.showData(getAppendData(rs.getString("desig_id"), hmDesignation), ""));//5
				appraisalList.add(uF.showData(getAppendData(rs.getString("grade_id"), hmGradeMap), ""));//6
				appraisalList.add(uF.showData(hmFrequency.get(rs.getString("frequency").trim()), ""));//7
				appraisalList.add(uF.showData(getAppendData(rs.getString("wlocation_id"), hmLocation), ""));//8
				appraisalList.add(uF.showData(getAppendData(rs.getString("department_id"), mpdepart), ""));//9
				appraisalList.add(uF.showData(rs.getString("supervisor_id"), ""));//10
				appraisalList.add(uF.showData(rs.getString("peer_ids"), ""));//11
//				appraisalList.add(uF.showData(getAppendData(rs.getString("self_ids"), hmEmpName), ""));//12
				appraisalList.add(getReviewees(con, uF, rs.getString("appraisal_details_id"), hmEmpName));//12
				appraisalList.add(uF.showData(rs.getString("emp_status"), ""));//13
				appraisalList.add(uF.showData(rs.getString("appraisal_type"), ""));//14
				appraisalList.add(uF.showData(rs.getString("appraisal_description"), ""));//15
				appraisalList.add(uF.showData(rs.getString("appraisal_instruction"), ""));//16
				appraisalList.add(uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));//17
				appraisalList.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));//18
				appraisalList.add(uF.showData(rs.getString("ceo_ids"), ""));//19
				appraisalList.add(uF.showData(rs.getString("hod_ids"), ""));//20
				appraisalList.add(uF.showData(rs.getString("my_review_status"), ""));//21
				appraisalList.add(uF.showData(rs.getString("added_by"), ""));//22
				
				appraisalList.add(uF.showData(getAppendData(rs.getString("reviewer_id"), hmEmpName), "No Reviewer added."));//23
				StringBuilder sbAppraisers = new StringBuilder();
				if(rs.getString("usertype_member") != null && rs.getString("usertype_member").length()>0) {
					List<String> alAppraiserMember = Arrays.asList(rs.getString("usertype_member").split(","));
					for(int i=0; alAppraiserMember != null && i<alAppraiserMember.size(); i++) {
						if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
							sbAppraisers.append("Managers: " + uF.showData(getAppendData(rs.getString("supervisor_id"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(HOD))) {
							sbAppraisers.append("HODs: " + uF.showData(getAppendData(rs.getString("hod_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(CEO))) {
							sbAppraisers.append("CEOs: " + uF.showData(getAppendData(rs.getString("ceo_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(HRMANAGER))) {
							sbAppraisers.append("HRs: " + uF.showData(getAppendData(rs.getString("hr_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == uF.parseToInt(hmUserTypeId.get(ADMIN))) {
							sbAppraisers.append("Global HRs: " + uF.showData(getAppendData(rs.getString("hr_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == 4) {
							sbAppraisers.append("Peers: " + uF.showData(getAppendData(rs.getString("peer_ids"), hmEmpName), "N/A")+"</br>");
						} else if(uF.parseToInt(alAppraiserMember.get(i)) == 10) {
							sbAppraisers.append("Anyone: " + uF.showData(getAppendData(rs.getString("other_ids"), hmEmpName), "N/A")+"</br>");
						}
					}
				}
				appraisalList.add(uF.showData(sbAppraisers.toString(), ""));//24
				
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(*) as cnt, appraisal_id,appraisal_freq_id from appraisal_final_sattlement group by appraisal_id,appraisal_freq_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmAppraisalCount.put(rs.getString("appraisal_id")+"_"+rs.getString("appraisal_freq_id"), rs.getString("cnt"));
			}
			rs.close();
			pst.close();
			
			if(getAppFreqId() != null && uF.parseToInt(getAppFreqId()) > 0) {
				pst = con.prepareStatement("select * from appraisal_details_frequency where appraisal_freq_id=?");
				pst.setInt(1, uF.parseToInt(getAppFreqId()));
				rs= pst.executeQuery();
				while(rs.next()) {
					boolean flag = uF.parseToBoolean(rs.getString("is_appraisal_publish"));
					appraisalList.add(""+flag);// 25
					appraisalList.add(""+uF.parseToBoolean(rs.getString("is_appraisal_close")));// 26
					appraisalList.add(uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, CF.getStrReportDateFormat()));// 27
					appraisalList.add(uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, CF.getStrReportDateFormat()));// 28
				}
				rs.close();
				pst.close();
			} else {
				if(uF.parseToInt(getId()) > 0 ) {
					appraisalList.add(""+false);// 25
					appraisalList.add(""+false);// 26
					appraisalList.add("");// 27
					appraisalList.add("");// 28
				}
			}
			
			boolean finalFlag = false;
			pst = con.prepareStatement("select * from appraisal_final_sattlement where appraisal_id=?");
			pst.setInt(1,uF.parseToInt(getId()));
			rs= pst.executeQuery();
			while(rs.next()) {
				finalFlag = true;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("finalFlag", finalFlag);
			
		StringBuilder sbIcons = new StringBuilder();
		if(appraisalList != null  && !appraisalList.isEmpty() && appraisalList.size() > 0) {
			List<String> empList =new ArrayList<String>();
			empList = getAppendData(con, appraisalList.get(3));
			
			String employeeId = "";
			if(empList != null && !empList.isEmpty() && empList.size() > 0) {
				if(empList.get(1)!=null) {
					employeeId = empList.get(1);
				}
			}
			
			
			
				if(strUserType != null && ((strUserType.equals(MANAGER) && uF.parseToInt(appraisalList.get(22)) == uF.parseToInt(strSessionEmpId)) || strUserType.equals(HRMANAGER) || strUserType.equals(ADMIN) || strUserType.equals(RECRUITER))) {
					if(uF.parseToInt(appraisalList.get(21)) == 0) {
						if(!uF.parseToBoolean(appraisalList.get(26)) && (!finalFlag || !flagProcess )) {
							if(uF.parseToBoolean(appraisalList.get(25))) {
								
									sbIcons.append("<div id=\"myDivM\"  style=\"float:left\">"+
										"<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to unpublish this review?'))"+
										"getPublishAppraisal('"+getId()+"','"+employeeId+"','"+getAppFreqId()+"','"+getFromPage()+"');\" >"+
										"<i class=\"fa fa-toggle-on\" aria-hidden=\"true\" title=\"Published\"></i></a>"+
										"</div>");
//									"<img src=\"images1/icons/icons/publish_icon_b.png\" title=\"Published\" /></a>"+
								
							} else { 
								sbIcons.append("<div id=\"myDivM\" style=\"float:left\">"+
									"<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to publish this review?'))"+
									"getPublishAppraisal('"+getId()+"','"+employeeId+"','"+getAppFreqId()+"','"+getFromPage()+"');\" >"+
									"<i class=\"fa fa-toggle-off\" aria-hidden=\"true\" title=\"Waiting to be publish\"></i></a>"+
									"</div>");
//								"<img src=\"images1/icons/icons/unpublish_icon_b.png\" title=\"Waiting to be publish\" /></a>"+ 
							}
						} else if(uF.parseToBoolean(appraisalList.get(26))) {
							if(uF.parseToBoolean(appraisalList.get(25))) {
								sbIcons.append("<div id=\"myDivM\"  style=\"float:left\">"+
									"<a href=\"javascript:void(0)\" onclick=\"alert('Review is already closed')\">"+
									"<i class=\"fa fa-toggle-on\" aria-hidden=\"true\" title=\"Published\"></i></a>"+ 
									"</div>");	
							} else {
								sbIcons.append("<div id=\"myDivM\" style=\"float:left\">"+
									"<a href=\"javascript:void(0)\" onclick=\"alert('Review is already closed')\" >"+
									"<i class=\"fa fa-toggle-off\" aria-hidden=\"true\" title=\"Waiting to be publish\"></i></a>"+ 
									"</div>");
							}
						} else {
							if(uF.parseToBoolean(appraisalList.get(25))) {
								sbIcons.append("<div id=\"myDivM\"  style=\"float:left\">"+
									"<a href=\"javascript:void(0)\" onclick=\"alert('Review is already in proccess/completed')\">"+
									"<i class=\"fa fa-toggle-on\" aria-hidden=\"true\" title=\"Published\"></i></a>"+ 
									"</div>");	
							} else {
								sbIcons.append("<div id=\"myDivM\" style=\"float:left\">"+
									"<a href=\"javascript:void(0)\" onclick=\"alert('Review is already in proccess/completed')\" >"+
									"<i class=\"fa fa-toggle-off\" aria-hidden=\"true\" title=\"Waiting to be publish\"></i></a>"+ 
									"</div>");
							}
						}
					} else {
						sbIcons.append("<div id=\"myDivM\"  style=\"float:left; width: 18px;\">&nbsp;</div>");	
					}
				}
				sbIcons.append("<a style=\"float:left;\" href=\"javascript: void(0)\" onclick=\"openAppraisalPreview('"+getId()+"','"+getAppFreqId()+"','"+getFromPage()+"')\" title=\"Preview\"><i class=\"fa fa-eye\" aria-hidden=\"true\"></i></a>");
				if(strUserType != null && ((strUserType.equals(MANAGER) && uF.parseToInt(appraisalList.get(22)) == uF.parseToInt(strSessionEmpId)) || strUserType.equals(HRMANAGER) || strUserType.equals(ADMIN) || strUserType.equals(RECRUITER))) {
					if(uF.parseToBoolean(appraisalList.get(26))) {
						sbIcons.append("<div id=\"myDivC\"  style=\"float:left;\">"+"<a href=\"javascript:void(0);\" onclick=\"closeReview('"+getId()+"','view','"+getAppFreqId()+"','"+getFromPage()+"')\" title=\"Close Review Reason\"><i class=\"fa fa-comment-o\" aria-hidden=\"true\"></i></a>"+"</div>");
					} else if(!uF.parseToBoolean(appraisalList.get(26))) {
						sbIcons.append("<div id=\"myDivC\"  style=\"float:left;\">"+"<a href=\"javascript:void(0);\" onclick=\"closeReview('"+getId()+"','close','"+getAppFreqId()+"','"+getFromPage()+"')\" title=\"Close Review\"><i class=\"fa fa-times-circle-o\" aria-hidden=\"true\"></i></a>"+"</div>");
					} 
				}
				appraisalList.add(sbIcons.toString());// 29
			}
		
			request.setAttribute("appraisalList", appraisalList);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private String getReviewees(Connection con, UtilityFunctions uF, String appraisalId, Map<String, String> hmEmpName) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder sbReviewees = new StringBuilder();
		try {
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,supervisor_emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
				" and is_alive= true and emp_per_id >0 order by supervisor_emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("PST ===>> " + pst);
			Map<String, List<String>> hmHireracyLevelEmpIds = new LinkedHashMap<String, List<String>>();
//			List<String> alHireracyLevels = new ArrayList<String>();
			while(rs.next()) {
				List<String> alInner = hmHireracyLevelEmpIds.get(rs.getString("supervisor_emp_id"));
				if(alInner==null) alInner = new ArrayList<String>();
				if(uF.parseToInt(rs.getString("supervisor_emp_id")) == uF.parseToInt(rs.getString("emp_id"))) {
					continue;
				}
				alInner.add(rs.getString("emp_id"));
				hmHireracyLevelEmpIds.put(rs.getString("supervisor_emp_id"), alInner);
			}
			rs.close();
			pst.close();

			StringBuilder sbClildEmpIds = null;
			List<String> empIDList = new ArrayList<String>();
			sbClildEmpIds = getChildEmpIds(hmHireracyLevelEmpIds, strSessionEmpId, empIDList, sbClildEmpIds);
			
			StringBuilder sbEmpId = null;
			if(strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery = new StringBuilder();
				sbQuery.append("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id  and appraisal_id=? " +
					"and (supervisor_ids like '%,"+strSessionEmpId+",%' or grand_supervisor_ids like '%,"+strSessionEmpId+",%' or hod_ids like '%,"+strSessionEmpId+",%' ");
				if(sbClildEmpIds!=null && !sbClildEmpIds.toString().equals("")) {
					sbQuery.append(" or epd.emp_per_id in ("+sbClildEmpIds.toString()+") ");
				}
				sbQuery.append(") order by emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
//				pst = con.prepareStatement("select reviewee_id from appraisal_reviewee_details ard, employee_personal_details epd where epd.emp_per_id=ard.reviewee_id and supervisor_ids like '%,"+strSessionEmpId+",%' and appraisal_id=? order by emp_fname");
				pst.setInt(1, uF.parseToInt(appraisalId));
				rs = pst.executeQuery();
				while (rs.next()) {
					if(sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rs.getString("reviewee_id"));
					} else {
						sbEmpId.append(","+rs.getString("reviewee_id"));
					}
				}
				rs.close();
				pst.close();
			}
			
			/*StringBuilder sbEmpId = null;
			if(strUserType != null && strUserType.equals(MANAGER)) {
				pst = con.prepareStatement("select reviewee_id from appraisal_reviewee_details where supervisor_ids like '%,"+strSessionEmpId+",%' and appraisal_id=?");
				pst.setInt(1, uF.parseToInt(appraisalId));
				rs = pst.executeQuery();
				while (rs.next()) {
					if(sbEmpId == null) {
						sbEmpId = new StringBuilder();
						sbEmpId.append(rs.getString("reviewee_id"));
					} else {
						sbEmpId.append(","+rs.getString("reviewee_id"));
					}
				}
				rs.close();
				pst.close();
			}*/
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from appraisal_reviewee_details where appraisal_id=? ");
			if(sbEmpId!=null) {
				sbQuery.append(" and reviewee_id in ("+sbEmpId.toString()+")");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(appraisalId));
			rs = pst.executeQuery();
			List<String> alRevieweeIds = new ArrayList<String>();
			while (rs.next()) {
				if(!alRevieweeIds.contains(rs.getString("reviewee_id"))) {
//					if(sbReviewees == null) {
						/* created by seema */
					sbReviewees.append("<a href=\"javascript:void(0)\" title='"+hmEmpName.get(rs.getString("reviewee_id"))+"' onclick=\"getRevieweeAppraisers('"+appraisalId+"','"+rs.getString("reviewee_id")+"', '"+hmEmpName.get(rs.getString("reviewee_id"))+"')\"> <span style=\"float:left;width:35px;height:35px;margin:2px;\"><img height=\"25\" width=\"25\" class=\"lazy img-circle zoom\" src=\"userImages/avatar_photo.png\" data-original="+CF.getStrDocRetriveLocation()+IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+rs.getString("reviewee_id")+"/"+IConstants.I_22x22+"/"+hmEmpName.get("empImage"+rs.getString("reviewee_id"))+" /></span></a>");
//					} else {
//						sbReviewees.append("<a href=\"javascript:void(0)\" title='"+hmEmpName.get(rs.getString("reviewee_id"))+"' onclick=\"getRevieweeAppraisers('"+appraisalId+"','"+rs.getString("reviewee_id")+"', '"+hmEmpName.get(rs.getString("reviewee_id"))+"')\"> <span style=\"float:left;width:35px;height:35px;margin:2px;\"><img height=\"25\" width=\"25\" class=\"lazy img-circle zoom\" src=\"userImages/avatar_photo.png\" data-original="+CF.getStrDocRetriveLocation()+IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+rs.getString("reviewee_id")+"/"+IConstants.I_22x22+"/"+hmEmpName.get("empImage"+rs.getString("reviewee_id"))+" /></span></a>");
//						/* created by seema */
//					}
				alRevieweeIds.add(rs.getString("reviewee_id"));
				}
			}
			rs.close();
			pst.close();
			if(sbReviewees == null) {
				sbReviewees = new StringBuilder("No Reviewee added.");
			}
			
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
		return sbReviewees.toString();
	}

	
	public StringBuilder getChildEmpIds(Map<String, List<String>> hmHireracyLevelEmpIds, String empId, List<String> empIDList, StringBuilder sbClildEmpIds) {

		if(empId != null && !empId.trim().equals("")) {
			if(hmHireracyLevelEmpIds == null) hmHireracyLevelEmpIds = new HashMap<String, List<String>>();
			List<String> innerList = (List<String>)hmHireracyLevelEmpIds.get(empId.trim());
	
			for(int i= 0; innerList != null && !innerList.isEmpty() && i<innerList.size(); i++) {
				String empId1 = innerList.get(i);
				if(empId1 != null && !empId1.trim().equals("") && !empIDList.contains(empId1.trim())) {
					empIDList.add(empId1.trim());
					if(sbClildEmpIds==null) {
						sbClildEmpIds = new StringBuilder();
						sbClildEmpIds.append(empId1.trim());
					} else {
						sbClildEmpIds.append(","+empId1.trim());
					}
				}
				if(empId1 != null && !empId1.trim().equals("")) {
					getChildEmpIds(hmHireracyLevelEmpIds, empId1, empIDList, sbClildEmpIds);
				}
			}
		}
		return sbClildEmpIds;
	}
	

	private List<String> getAppendData(Connection con, String strID) {
		StringBuilder sb = new StringBuilder();
		List<String> empList = new ArrayList<String>();
//		EncryptionUtils encryption = new EncryptionUtils();// Created By Dattatray Date : 20-July-2021 Note : Encryption
		if (strID != null && !strID.equals("")) {
			int flag = 0, empcnt = 0;
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);

			String[] temp = strID.split(",");
			empcnt = temp.length - 1;
			for (int i = 0; i < temp.length; i++) {

				if (temp[i] != null && !temp[i].equals("")) {
					if (flag == 0) { //encryption.encrypt(temp[i])
						sb.append("<a href=\"MyProfile.action?empId=" + temp[i] + "\">" + hmEmpName.get(temp[i].trim()) + "</a>");// Created By Dattatray Date : 20-July-2021 Note : empId Encryption
					} else { 
						sb.append(", " + "<a href=\"MyProfile.action?empId=" + temp[i] + "\">" + hmEmpName.get(temp[i].trim()) + "</a>");// Created By Dattatray Date : 20-July-2021 Note : empId Encryption
					}
					flag = 1;
				}
			}
			empList.add(sb.toString());
			empList.add(empcnt + "");
		}
		return empList;
	}

	public  Map<String, String> getLevelMap( Connection con) {

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
		}finally {
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
		
		return hmLevelMap;
	}

	
	private Map<String, String> getLocationMap( Connection con) {
		Map<String, String> mplocation = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {

			pst = con.prepareStatement("select * from work_location_info");
			rst = pst.executeQuery();
			while (rst.next()) {
				mplocation.put(rst.getString("wlocation_id"), rst.getString("wlocation_name"));
			}
			rst.close();
			pst.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(rst != null) {
				try {
					rst.close();
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
		return mplocation;
	}

	
	public  Map<String, String> getAppraisalOtherQueType( Connection con,  UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> appOtherQueType = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from appraisal_other_question_type_details aoqtd, appraisal_question_details aqd where " +
					"aoqtd.othe_question_type_id=aqd.other_id and aqd.appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				appOtherQueType.put(rs.getString("othe_question_type_id"), rs.getString("other_question_type"));
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
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
		return appOtherQueType;
	}
	
	
	public  Map<String, String> getAppraisalQuestionMap( Connection con,  UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> AppraisalQuestion = new HashMap<String, String>();
		Map<String, String> appQueAnsType = new HashMap<String, String>();

		try {

			pst = con.prepareStatement("select * from question_bank qb, appraisal_question_details aqd where qb.question_bank_id=aqd.question_id and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				AppraisalQuestion.put(rs.getString("question_bank_id"), rs.getString("question_text"));
				appQueAnsType.put(rs.getString("question_bank_id"), rs.getString("question_type"));
				
			}
			rs.close();
			pst.close();
			request.setAttribute("appQueAnsType", appQueAnsType);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
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
		return AppraisalQuestion;
	}

	public  Map<String, String> getAttributeMap( Connection con) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> AppraisalQuestion = new HashMap<String, String>();

		try {

			pst = con.prepareStatement("select * from appraisal_attribute ");
			rs = pst.executeQuery();
			while (rs.next()) {
				AppraisalQuestion.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
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
		return AppraisalQuestion;
	}
	
	
	public  Map<String, String> getAnswerTypeMap( Connection con) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> answerTypeMp = new HashMap<String, String>();

		try {
			pst = con.prepareStatement("select * from appraisal_question_details ");
			rs = pst.executeQuery();
			while (rs.next()) {
				answerTypeMp.put(rs.getString("appraisal_level_id")+"_"+rs.getString("appraisal_id"), rs.getString("answer_type"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
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
		return answerTypeMp;
	}
	
	
	public  Map<String, String> getSessionUserName( Connection con) {
		UtilityFunctions uF=new UtilityFunctions();
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmUserName = new HashMap<String, String>();
		try {
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
			pst = con.prepareStatement("select * from employee_personal_details ");
			rs = pst.executeQuery();
			while (rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmUserName.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
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
		return hmUserName;
	}


	@Override
	public  void setServletRequest( HttpServletRequest request) {
		this.request = request;
	}
	
	public  String getType() {
		return type;
	}
	
	public  void setType( String type) {
		this.type = type;
	}
	
	public  String getEmpId() {
		return empId;
	}

	public  void setEmpId( String empId) {
		this.empId = empId;
	}

	public String getImportMsg() {
		return importMsg;
	}

	public void setImportMsg(String importMsg) {
		this.importMsg = importMsg;
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