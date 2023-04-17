package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.recruitment.UpdateADRRequest;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MyReviewSummary extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request; 
	String strUserType = null;
	String strSessionEmpId = null;
	Map<String, List<Map<String, List<List<String>>>>> levelMp = new HashMap<String, List<Map<String, List<List<String>>>>>();

	private CommonFunctions CF;
	private static Logger log = Logger.getLogger(UpdateADRRequest.class);

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
		
		String title="Summary";  
		if(type!=null && type.equals("choose")){
			title="Preview";
		}else{
			title="Summary";
		}
		
		request.setAttribute(TITLE, "My Review "+title);
		if(getEmpId()!=null){
			request.setAttribute(PAGE, "/jsp/performance/MyReviewScoreSummary.jsp");
		}else{
			request.setAttribute(PAGE, "/jsp/performance/MyReviewSummary.jsp");
		}

		if(getEmpId()!=null){
			getScoreMarks();
			getMeasureScoreMarks();
			getScoreMarksType1();
			getObjectiveMarks();
			getGoalMarksType1();
			getGoalMarksType2();
		}
		
		getAppraisalDetail();
//		System.out.println("request.setAttribute(appraisalList)"+request.getAttribute("appraisalList"));
		getReport();
		getAttributeDetails();
		getKRADetails();
		getKRATargetDetails();

		request.setAttribute("levelMp", levelMp);
		
		
		
		String levelID=getSelfIDs(getId());
//		System.out.println("levelID====>"+levelID);
		if (levelID != null && levelID.length()>0){
			attributeList = new FillAttribute(request).fillElementAttribute(levelID);
		}else{
			attributeList = new FillAttribute(request).fillElementAttribute(null);
		}
		ansTypeList = new FillAnswerType(request).fillAnswerType();

//		getAppraisalDetail();
		getOrientationValue(uF.parseToInt(getOreinted()));
		getattribute();
		getOtherAnsType();
		getAppraisalQuestionList();
		
		if(getFromPage() != null && getFromPage().equalsIgnoreCase("SRR")) {
			return LOAD;
		} 
		return SUCCESS;
	}
	
	
	private String getSelfIDs(String id) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;		
		UtilityFunctions uF=new UtilityFunctions();
		
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
					if(i==0){
						levelID=levelid;
					}else{
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
//			db.closeConnection(con);
//			db.closeStatements(pst);
//		}
//		return orientationMemberMp;
//	}

	

	public void initialize(UtilityFunctions uF) {

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

	private void getOrientationValue(int id) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
			StringBuilder sb = new StringBuilder();
			con = db.makeConnection(con);

			pst = con
					.prepareStatement("select member_name from orientation_details od,orientation_member om  where orientation_id=? and od.member_id=orientation_member_id");
			pst.setInt(1, id);
			rs = pst.executeQuery();
			int i = 0;
			while (rs.next()) {
				if (i == 0)
					sb.append(rs.getString("member_name"));
				else
					sb.append("," + rs.getString("member_name"));
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

	public List<FillFrequency> getFrequencyList() {
		return frequencyList;
	}

	public void setFrequencyList(List<FillFrequency> frequencyList) {
		this.frequencyList = frequencyList;
	}

	public List<FillOrientation> getOrientationList() {
		return orientationList;
	}

	public void setOrientationList(List<FillOrientation> orientationList) {
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
					sb.append("<option value=\""
							+ rs.getString("appraisal_answer_type_id")
							+ "\" selected>"
							+ rs.getString("appraisal_answer_type_name")
							+ "</option>");
				} else {
					sb.append("<option value=\""
							+ rs.getString("appraisal_answer_type_id") + "\">"
							+ rs.getString("appraisal_answer_type_name")
							+ "</option>");
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

//	public String getAppendData(Connection con, List<String> strID, Map<String, String> mp) {
//		StringBuilder sb = new StringBuilder();
//		Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
//		Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
//
//		if (strID != null) {
//			for (int i = 0; i < strID.size(); i++) {
//				if (i == 0) {
//					sb.append("[" + hmEmpCode.get(strID.get(i)) + "] "
//							+ mp.get(strID.get(i)) + "("
//							+ hmDesignation.get(strID.get(i)) + ")");
//				} else {
//					sb.append(", [" + hmEmpCode.get(strID.get(i)) + "] "
//							+ mp.get(strID.get(i)) + "("
//							+ hmDesignation.get(strID.get(i)) + ")");
//				}
//			}
//		} else {
//			return null;
//		}
//
//		return sb.toString();
//	}

	public List<FillAttribute> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<FillAttribute> attributeList) {
		this.attributeList = attributeList;
	}

	public void getattribute() {

		StringBuilder sb = new StringBuilder("");

		for (int i = 0; i < attributeList.size(); i++) {
			FillAttribute fillAttribute = attributeList.get(i);

			sb.append("<option value=\"" + fillAttribute.getId() + "\">"
					+ fillAttribute.getName() + "</option>");

		}
		request.setAttribute("attribute", sb.toString());

	}

	public void getAppraisalQuestionList() {

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
				sb.append("<option value=\"" + rs.getString("question_bank_id")
						+ "\">" + rs.getString("question_text").replace("'", "") + "</option>");
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

	private String getAppendData(String strID, Map<String, String> mp) {
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<FillAnswerType> getAnsTypeList() {
		return ansTypeList;
	}

	public void setAnsTypeList(List<FillAnswerType> ansTypeList) {
		this.ansTypeList = ansTypeList;
	}

	public String getOreinted() {
		return oreinted;
	}

	public void setOreinted(String oreinted) {
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
			
			pst = con.prepareStatement("select marks,weightage,((marks*100)/weightage)as percentage,user_type_id,goal_id from" +
					" (select sum(aqs.marks)as marks,sum(aqs.weightage)as weightage,aqs.user_type_id,amd.goal_id from appraisal_question_answer aqs " +
					"join appraisal_question_details aq  using (appraisal_question_details_id) join appraisal_measure_details amd using (measure_id)" +
					" where aqs.appraisal_id=? and aqs.emp_id=? and appraisal_freq_id = ? and amd.goal_id is not null and aqs.weightage>0 group by aqs.user_type_id,amd.goal_id order by aqs.user_type_id,amd.goal_id) as a");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
//			System.out.println("getGoalMarksType2 pst ===> "+pst);
			String strUserTypeNew = null;
			String strUserTypeOld = null;
			
			Map<String,String> hmMeasureAnsScore = new HashMap<String, String>();
			Map<String, Map<String,String>> hmGoalType2Marks = new HashMap<String, Map<String,String>>();
			
			while (rs.next()) { 
				
				strUserTypeNew = rs.getString("user_type_id");
				if(strUserTypeNew!=null && !strUserTypeNew.equalsIgnoreCase(strUserTypeOld)){
					hmMeasureAnsScore = new HashMap<String, String>();
				}
								
				double dblPercent = uF.parseToDouble(rs.getString("percentage"));
				hmMeasureAnsScore.put(rs.getString("goal_id"), uF.formatIntoComma(dblPercent));
				hmGoalType2Marks.put(rs.getString("user_type_id"), hmMeasureAnsScore);
				
				strUserTypeOld = strUserTypeNew;
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
		
			pst = con.prepareStatement("select marks,weightage,((marks*100)/weightage)as percentage,user_type_id,goal_id from" +
					" (select sum(aqs.marks)as marks,sum(aqs.weightage)as weightage,aqs.user_type_id,aod.goal_id" +
					" from appraisal_question_answer aqs join appraisal_question_details aq  using (appraisal_question_details_id)" +
					" join appraisal_measure_details amd using (measure_id) join appraisal_objective_details aod using (objective_id)" +
					" where aqs.appraisal_id=? and aqs.emp_id=? and appraisal_freq_id = ? and amd.objective_id is not null" +
					" and aqs.weightage>0 group by aqs.user_type_id,aod.goal_id order by aqs.user_type_id,aod.goal_id) as a");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery(); 
//			System.out.println("getGoalMarksType1 pst ===> "+pst);
			String strUserTypeNew = null;
			String strUserTypeOld = null;
			
			Map<String,String> hmMeasureAnsScore = new HashMap<String, String>();
			Map<String, Map<String,String>> hmGoalType1Marks = new HashMap<String, Map<String,String>>();
			
			while (rs.next()) {
				
				strUserTypeNew = rs.getString("user_type_id");
				if(strUserTypeNew!=null && !strUserTypeNew.equalsIgnoreCase(strUserTypeOld)){
					hmMeasureAnsScore = new HashMap<String, String>();
				}
								
				double dblPercent = uF.parseToDouble(rs.getString("percentage"));
				hmMeasureAnsScore.put(rs.getString("goal_id"), uF.formatIntoComma(dblPercent));
				hmGoalType1Marks.put(rs.getString("user_type_id"), hmMeasureAnsScore);
				
				strUserTypeOld = strUserTypeNew;
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
			pst = con.prepareStatement("select marks,weightage,((marks*100)/weightage)as percentage,user_type_id,objective_id " +
					" from (select sum(aqs.marks)as marks,sum(aqs.weightage)as weightage,aqs.user_type_id,amd.objective_id " +
					" from appraisal_question_answer aqs join appraisal_question_details aq  using (appraisal_question_details_id)" +
					" join appraisal_measure_details amd using (measure_id) where aqs.appraisal_id=? and aqs.emp_id=? and appraisal_freq_id = ? " +
					" and amd.objective_id is not null and aqs.weightage>0 group by aqs.user_type_id,amd.objective_id order by aqs.user_type_id,amd.objective_id)as a");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
//			System.out.println("getObjectiveMarks pst ===> "+pst);
			String strUserTypeNew = null;
			String strUserTypeOld = null;
			
			Map<String,String> hmMeasureAnsScore = new HashMap<String, String>();
			Map<String, Map<String,String>> hmObjectiveMarks = new HashMap<String, Map<String,String>>();
			
			while (rs.next()) {
				strUserTypeNew = rs.getString("user_type_id");
				if(strUserTypeNew!=null && !strUserTypeNew.equalsIgnoreCase(strUserTypeOld)){
					hmMeasureAnsScore = new HashMap<String, String>();
				}
								
				double dblPercent = uF.parseToDouble(rs.getString("percentage"));
				hmMeasureAnsScore.put(rs.getString("objective_id"), uF.formatIntoComma(dblPercent));
				hmObjectiveMarks.put(rs.getString("user_type_id"), hmMeasureAnsScore);
				
				strUserTypeOld = strUserTypeNew;
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
			pst = con.prepareStatement("select marks,weightage,((marks*100)/weightage)as percentage,user_type_id,scorecard_id " +
					"from (select sum(aqs.marks)as marks,sum(aqs.weightage)as weightage,aqs.user_type_id,aq.scorecard_id " +
					"from appraisal_question_answer aqs,appraisal_question_details aq where  aqs.appraisal_question_details_id=aq.appraisal_question_details_id " +
					"and aqs.appraisal_id=? and aqs.emp_id=? and appraisal_freq_id = ? and aq.scorecard_id is not null and aqs.weightage>0 group by aqs.user_type_id,aq.scorecard_id " +
					"order by aqs.user_type_id,aq.scorecard_id) as a");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
//			System.out.println("getScoreMarksType1 pst ===> "+pst);
			String strUserTypeNew = null;
			String strUserTypeOld = null;
			
			Map<String,String> hmMeasureAnsScore = new HashMap<String, String>();
			Map<String, Map<String,String>> hmScoreMarksType1 = new HashMap<String, Map<String,String>>();
			
			while (rs.next()) {
				
				strUserTypeNew = rs.getString("user_type_id");
				if(strUserTypeNew!=null && !strUserTypeNew.equalsIgnoreCase(strUserTypeOld)){
					hmMeasureAnsScore = new HashMap<String, String>();
				}
								
				double dblPercent = uF.parseToDouble(rs.getString("percentage"));
				hmMeasureAnsScore.put(rs.getString("scorecard_id"), uF.formatIntoComma(dblPercent));
				hmScoreMarksType1.put(rs.getString("user_type_id"), hmMeasureAnsScore);
				
				strUserTypeOld = strUserTypeNew;
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

			pst = con.prepareStatement("select marks,weightage,(marks*100/weightage)as percentage,user_type_id,measure_id from " +
					"(select sum(aqs.marks)as marks,sum(aqs.weightage)as weightage,aqs.user_type_id,aq.measure_id from appraisal_question_answer aqs," +
					"appraisal_question_details aq where  aqs.appraisal_question_details_id=aq.appraisal_question_details_id and aqs.appraisal_id=? " +
					"and aqs.emp_id=? and appraisal_freq_id = ? and aq.measure_id is not null and aqs.weightage>0 group by aqs.user_type_id, aq.measure_id order by aqs.user_type_id,aq.measure_id) as a");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
//			System.out.println("getMeasureScoreMarks pst ===> "+pst);
			String strUserTypeNew = null;
			String strUserTypeOld = null;
			
			Map<String,String> hmMeasureAnsScore = new HashMap<String, String>();
			Map<String, Map<String,String>> hmMeasureUserScore = new HashMap<String, Map<String,String>>();
			
			while (rs.next()) {
				
				strUserTypeNew = rs.getString("user_type_id");
				if(strUserTypeNew!=null && !strUserTypeNew.equalsIgnoreCase(strUserTypeOld)){
					hmMeasureAnsScore = new HashMap<String, String>();
				}
				
				double dblMarks = uF.parseToDouble(rs.getString("marks"));
				double dblWeightage = uF.parseToDouble(rs.getString("weightage"));
				double dblPercent = uF.parseToDouble(rs.getString("percentage"));
//				if(dblWeightage>0){
//					dblPercent = dblMarks * dblWeightage / 100;  
//				}
//				
				//System.out.println(rs.getString("measure_id")+" uF.formatIntoComma(dblPercent) "+uF.formatIntoComma(dblPercent));
				
				hmMeasureAnsScore.put(rs.getString("measure_id"), uF.formatIntoComma(dblPercent));
				hmMeasureUserScore.put(rs.getString("user_type_id"), hmMeasureAnsScore);
				
				strUserTypeOld = strUserTypeNew;
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

			pst = con
					.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
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

			//if (getEmpID() != null && !getEmpID().equals("")) {

				StringBuilder sb = new StringBuilder();
				/*sb.append("select * from goal_kras k join goal_details g on k.goal_id=g.goal_id and g.emp_ids like '%"
						+ getEmpID()
						+ "%' "
						+ "and g.goal_type=4 and measure_type !=''  order by k.goal_id");*/
				
				sb.append("select * from goal_kras k join goal_details g on k.goal_id=g.goal_id ");
				//sb.append(" and g.emp_ids like '%"+ getEmpID()+ "%' ");
				sb.append(" and g.goal_type=4 and measure_type !=''  order by k.goal_id");

				pst = con.prepareStatement(sb.toString());
//				System.out.println("pst=====>"+pst);
				rs = pst.executeQuery();

				while (rs.next()) {
					List<List<String>> outerList = hmKRA.get(rs
							.getString("goal_id"));
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

					hmGoalOrientation.put(rs.getString("goal_id"),
							rs.getString("orientation_id"));

					hmGoalTitle.put(rs.getString("goal_id"),
							rs.getString("goal_title"));

					String measures = "";
					if (rs.getString("measure_type").equals("Amount")) {
						measures = rs.getString("measure_currency_value");
					} else if (rs.getString("measure_type").equals("Effort")) {
						measures = rs.getString("measure_effort_days")
								+ " Days and "
								+ rs.getString("measure_effort_hrs") + " Hrs.";
					}
					hmMesures.put(rs.getString("goal_id"), measures);
					hmMesuresType.put(rs.getString("goal_id"),
							rs.getString("measure_type"));

					outerList.add(innerList);
					hmKRA.put(rs.getString("goal_id"), outerList);
				}
				rs.close();
				pst.close();
			//}
			request.setAttribute("hmKRA", hmKRA);
//			System.out.println("hmKRA"+hmKRA);
			//System.out.println("hmMesuresType"+hmMesuresType);
			
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
	private void getKRADetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uf = new UtilityFunctions();

				Map<String, List<List<String>>> hmKRA = new LinkedHashMap<String, List<List<String>>>();
		boolean levelFlag=false;

		try {
			con = db.makeConnection(con);
			Map<String, String> hmGoalOrientation = new HashMap<String, String>();
			Map<String, String> hmMesures = new HashMap<String, String>();
			Map<String, String> hmMesuresType = new HashMap<String, String>();
			Map<String, String> hmGoalTitle = new HashMap<String, String>();
			Map<String, String> orientationMp = getOrientationValue(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);

			pst = con
					.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uf.parseToInt(getId()));
			rs = pst.executeQuery();

			Map<String, String> appraisalMp = new HashMap<String, String>();
			int memberCount = 0;
			List<String> memberList = new ArrayList<String>();
			while (rs.next()) {
				memberList = Arrays.asList(rs.getString("usertype_member")
						.split(","));
			}
			rs.close();
			pst.close();
			request.setAttribute("memberList1", memberList);

			//if (getEmpID() != null && !getEmpID().equals("")) {

				StringBuilder sb = new StringBuilder();
				/*sb.append("select * from goal_kras k join goal_details g on k.goal_id=g.goal_id and g.emp_ids like '%"
						+ getEmpID()
						+ "%' "
						+ "and g.goal_type=4 and (measure_type='' or measure_type is null) order by k.goal_id");*/
				sb.append("select * from goal_kras k join goal_details g on k.goal_id=g.goal_id ");
				//sb.append(" and g.emp_ids like '%"+ getEmpID()+ "%' ");
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
					if (rs.getString("measure_type") != null && rs.getString("measure_type").equals("$")) {
						measures = rs.getString("measure_currency_value");
					} else if (rs.getString("measure_type") != null && rs.getString("measure_type").equals("Effort")) {
						measures = rs.getString("measure_effort_days")+ " Days and "+ rs.getString("measure_effort_hrs") + " Hrs.";
					}
					hmMesures.put(rs.getString("goal_id"), measures);
					hmMesuresType.put(rs.getString("goal_id"), rs.getString("measure_type"));

					outerList.add(innerList);
					hmKRA.put(rs.getString("goal_id"), outerList);
					
					levelFlag=true;
				}
				rs.close();
				pst.close();
				
			//}
				//System.out.println("hmKRA"+hmKRA);
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

	private Map<String, String> getOrientationMember(Connection con) {
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
		Map hmScoreAggregateMap = new HashMap();
		try {

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id=? and emp_id= ? and appraisal_freq_id= ?  order by user_type_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
//			System.out.println("getScoreMarks pst ===> "+pst);
			String strUserTypeNew = null;
			String strUserTypeOld = null;
			
			Map hmAnswerScore = new HashMap();
			
			while (rs.next()) {
				
				strUserTypeNew = rs.getString("user_type_id");
				if(strUserTypeNew!=null && !strUserTypeNew.equalsIgnoreCase(strUserTypeOld)){
					hmAnswerScore = new HashMap();
				}
				
				double dblMarks = uF.parseToDouble(rs.getString("marks"));
				double dblWeightage = uF.parseToDouble(rs.getString("weightage"));
				double dblPercent = 0;
				if(dblWeightage>0){
					dblPercent = dblMarks * 100 / dblWeightage;  
				}
				//System.out.println("dblPercent :::::: "+dblPercent);
				hmAnswerScore.put(rs.getString("question_id"), uF.formatIntoComma(dblPercent)+"%");
//				System.out.println("hmAnswerScore :::::: "+hmAnswerScore);
				hmScoreDetailsMap.put(rs.getString("user_type_id"), hmAnswerScore);
//				System.out.println("hmScoreDetailsMap :::::: "+hmScoreDetailsMap);
				strUserTypeOld = strUserTypeNew;
			}
			rs.close();
			pst.close();

			request.setAttribute("hmScoreDetailsMap", hmScoreDetailsMap);
			
			
			pst = con.prepareStatement("select user_type_id, sum(marks) as marks, sum(weightage) as weightage, aqw.appraisal_level_id from appraisal_question_answer aqw, appraisal_level_details ald " 
					+ " where ald.appraisal_id = aqw.appraisal_id and aqw.appraisal_level_id = ald.appraisal_level_id and aqw.appraisal_id=? and emp_id= ? and appraisal_freq_id= ? " 
					+ "group by aqw.appraisal_level_id, user_type_id order by user_type_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			
			double dblTotalMarks=0;
			double dblTotalWeightage=0;
			Map hmTemp = new HashMap();
			
			while (rs.next()) {
				
				strUserTypeNew = rs.getString("user_type_id");
				if(strUserTypeNew!=null && !strUserTypeNew.equalsIgnoreCase(strUserTypeOld)){
					hmTemp = new HashMap();
				}
				
				dblTotalMarks = uF.parseToDouble(rs.getString("marks"));
				dblTotalWeightage = uF.parseToDouble(rs.getString("weightage"));
				
				
				if(dblTotalWeightage>0 && rs.getString("marks")!=null){
					hmTemp.put(rs.getString("appraisal_level_id"), uF.formatIntoTwoDecimal(((dblTotalMarks / dblTotalWeightage) * 100))+"%");
				}
				
				hmScoreAggregateMap.put(rs.getString("user_type_id"), hmTemp);
				
				
				strUserTypeOld = strUserTypeNew;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmScoreAggregateMap", hmScoreAggregateMap);
			
			pst = con.prepareStatement("Select sattlement_comment,if_approved,user_id, emp_fname,emp_mname,  emp_lname, _date from appraisal_final_sattlement afs,employee_personal_details epd " 
					+ " where afs.user_id = epd.emp_per_id and appraisal_id=? and emp_id=? and appraisal_freq_id= ?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			
			String strFinalComments = null;
			String strAppraisedBy = null;
			String strAppraisedOn = null;
			while(rs.next()){
				strFinalComments = rs.getString("sattlement_comment");
				if(strFinalComments!=null){
					strFinalComments = strFinalComments.replace("\n", "<br/>");
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
			
			request.setAttribute("strFinalComments", strFinalComments);
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
	private Map<String, String> getOrientationValue(Connection con) {
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

	public void getDataMeasure(int id) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			Map<String, String> AppraisalQuestion = getAppraisalQuestionMap(con, uF);
			Map<String, String> appQueAnsType = (Map<String, String>)request.getAttribute("appQueAnsType");
			Map<String, String> attributeMp = getAttributeMap(con);
			Map<String, String> answertypeMp = getAnswerTypeMap(con);

			pst = con.prepareStatement("select * from appraisal_scorecard_details where level_id =? and appraisal_id=?");
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
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				scoreMp.put(rs.getString("level_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_measure_details where scorecard_id in(" + scorecard_id + ") and appraisal_id=?");
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
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				measureMp.put(rs.getString("scorecard_id"), outerList);
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from appraisal_question_details where measure_id in(" + measure_id + ") and appraisal_id=?");
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
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
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

	public void getDataMeasureGoal(int id) {

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
			pst = con.prepareStatement("select * from appraisal_scorecard_details where level_id =? and appraisal_id=?");
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
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				scoreMp.put(rs.getString("level_id"), outerList);
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from appraisal_goal_details where scorecard_id in(" + scorecard_id + ") and appraisal_id=?");
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
				if (outerGoalList == null)
					outerGoalList = new ArrayList<List<String>>();
				outerGoalList.add(innerGoalList);
				// outerGoalList.add(innerGoalList);

				GoalMp.put(rs.getString("scorecard_id"), outerGoalList);

			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_measure_details where goal_id in(" + goal_id + ") and appraisal_id=?");
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
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				measureMp.put(rs.getString("goal_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_question_details where measure_id in (" + measure_id + ") and appraisal_id=?");
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
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
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

	public void getDataObjectiveMeasureGoal(int id) {

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
			pst = con.prepareStatement("select * from appraisal_scorecard_details where level_id =? and appraisal_id=?");
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
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				scoreMp.put(rs.getString("level_id"), outerList);
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from appraisal_goal_details where scorecard_id in(" + scorecard_id + ") and appraisal_id=?");
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
				if (outerGoalList == null)
					outerGoalList = new ArrayList<List<String>>();
				outerGoalList.add(innerGoalList);
				// outerGoalList.add(innerGoalList);

				GoalMp.put(rs.getString("scorecard_id"), outerGoalList);

			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from appraisal_objective_details where goal_id in(" + goal_id + ") and appraisal_id=?");
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
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				objectiveMp.put(rs.getString("goal_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_measure_details where objective_id in(" + objective_id + ") and appraisal_id=?");
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
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				measureMp.put(rs.getString("objective_id"), outerList);
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from appraisal_question_details where measure_id in(" + measure_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));

			rs = pst.executeQuery();

			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("answer_type"));
				innerList.add(rs.getString("attribute_id"));
				
				List<List<String>> outerList = questionMp.get(rs.getString("measure_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
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

	public void getReport() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmUserName = getSessionUserName();
			pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			List<List<String>> mainLevelList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("main_level_id"));
				innerList.add(rs.getString("level_title"));
				innerList.add(rs.getString("short_description"));
				innerList.add(rs.getString("long_description"));
				innerList.add(rs.getString("appraisal_id"));
				innerList.add(rs.getString("section_weightage"));
				innerList.add(hmUserName.get(rs.getString("added_by")));
				innerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				mainLevelList.add(innerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("mainLevelList", mainLevelList);
			
			pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
//			List<List<String>> outerList1 = new ArrayList<List<String>>();
			Map<String, List<List<String>>> hmSystemLevelMp = new HashMap<String, List<List<String>>>();
			Map<String, String> attributeMp = getAttributeMap(con);
			while (rs.next()) {

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_level_id"));
				innerList.add(rs.getString("level_title"));
				innerList.add(rs.getString("scorecard_type"));
				innerList.add(rs.getString("appraisal_system"));
				innerList.add(rs.getString("short_description"));
				innerList.add(rs.getString("long_description"));
				innerList.add(attributeMp.get(rs.getString("attribute_id")));
				innerList.add(rs.getString("subsection_weightage"));
				innerList.add(hmUserName.get(rs.getString("added_by")));
				innerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				if (uF.parseToInt(rs.getString("appraisal_system")) == 1) {

					if (uF.parseToInt(rs.getString("scorecard_type")) == 1)
						getDataObjectiveMeasureGoal(uF.parseToInt(rs.getString("appraisal_level_id")));
					if (uF.parseToInt(rs.getString("scorecard_type")) == 2)
						getDataMeasure(uF.parseToInt(rs.getString("appraisal_level_id")));
					else if (uF.parseToInt(rs.getString("scorecard_type")) == 3) {
						getDataMeasureGoal(uF.parseToInt(rs.getString("appraisal_level_id")));
					}
				} else if (uF.parseToInt(rs.getString("appraisal_system")) == 2) {
					// code for other option
					getOtherData(uF.parseToInt(rs.getString("appraisal_level_id")));
				}
				innerList.add(rs.getString("main_level_id"));
				
				List<List<String>> outerList1 = hmSystemLevelMp.get(rs.getString("main_level_id"));
				if (outerList1 == null)
					outerList1 = new ArrayList<List<String>>();
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

	public void getOtherData(int id) {
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
			
			pst = con.prepareStatement("select * from appraisal_question_details where other_id in(" + othe_question_type_id + ")");
			rs = pst.executeQuery();
			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("answer_type"));
				innerList.add(uF.showData(appOtherQueType.get(rs.getString("other_id")), ""));
				innerList.add(rs.getString("other_short_description"));
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("attribute_id"));

				List<List<String>> outerList = questionMp.get(id + "");
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				questionMp.put(id + "", outerList);

			}
			rs.close();
			pst.close();
			
//			System.out.println("questionMp =====> "+questionMp);
			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
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

		con = db.makeConnection(con);
		Map<String, String> hmDesignation = CF.getDesigMap(con);
		Map<String, String> hmGradeMap = CF.getGradeMap(con);
		Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
		Map<String, String> hmLevelMap = getLevelMap(con);
		Map<String, String> hmLocation = getLocationMap(con);
		Map<String, String> mpdepart = CF.getDeptMap(con);
		Map<String, String> orientationMp = getOrientationValue(con);
		Map<String, String> orientationMemberMp = getOrientationMember(con);
		try {
			Map<String, String> hmFrequency = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmFrequency.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("App ID ===> "+id +"==>appFreqid==>"+getAppFreqId()+"==>fromPage==>"+getFromPage());
			List<String> appraisalList = new ArrayList<String>();
			pst = con.prepareStatement("select * from appraisal_details a, appraisal_details_frequency adf where a.appraisal_details_id= adf.appraisal_id "
					+" and (adf.is_delete is null or is_delete = false) and appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			while (rs.next()) {
				List<String> memberList = Arrays.asList(rs.getString("usertype_member").split(","));
				String memberName = "";

				for (int i = 0; i < memberList.size(); i++) {
					if (i == 0) {
						memberName += orientationMemberMp.get(memberList.get(i));
					} else {
						memberName += ", " + orientationMemberMp.get(memberList.get(i));
					}
				}

				appraisalList.add(uF.showData(rs.getString("appraisal_details_id"), ""));//0
				appraisalList.add(uF.showData(rs.getString("appraisal_name"), ""));//1
				oreinted = rs.getString("oriented_type");
				appraisalList.add(uF.showData(orientationMp.get(rs.getString("oriented_type")) + "&deg( " + memberName + " )", ""));//2
				appraisalList.add(uF.showData(rs.getString("self_ids"), ""));//3
				appraisalList.add(uF.showData(getAppendData(rs.getString("level_id"), hmLevelMap), ""));//4
				appraisalList.add(uF.showData(getAppendData(rs.getString("desig_id"), hmDesignation), ""));//5
				appraisalList.add(uF.showData(getAppendData(rs.getString("grade_id"), hmGradeMap), ""));//6
				appraisalList.add(uF.showData(hmFrequency.get(rs.getString("frequency")), ""));//7
				appraisalList.add(uF.showData(getAppendData(rs.getString("wlocation_id"), hmLocation), ""));//8
				appraisalList.add(uF.showData(getAppendData(rs.getString("department_id"), mpdepart), ""));//9
				appraisalList.add(uF.showData(rs.getString("supervisor_id"), ""));//10
				appraisalList.add(uF.showData(rs.getString("peer_ids"), ""));//11
//				appraisalList.add(uF.showData(getAppendData(rs.getString("self_ids"), hmEmpName), ""));//12
				appraisalList.add(getReviewees(con, uF, rs.getString("appraisal_details_id"), hmEmpName));//Create by dattatray
				appraisalList.add(uF.showData(rs.getString("emp_status"), ""));//13
				appraisalList.add(uF.showData(rs.getString("appraisal_type"), ""));//14
				appraisalList.add(uF.showData(rs.getString("appraisal_description"), ""));//15
				appraisalList.add(uF.showData(rs.getString("appraisal_instruction"), ""));//16
				appraisalList.add(uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));//17
				appraisalList.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));//18
				boolean flag=uF.parseToBoolean(rs.getString("is_appraisal_publish"));
				appraisalList.add(""+flag);//19 
				appraisalList.add(uF.showData(rs.getString("ceo_ids"), ""));//20
				appraisalList.add(uF.showData(rs.getString("hod_ids"), ""));//21
				appraisalList.add(uF.showData(rs.getString("appraisal_freq_id"), ""));//22
				appraisalList.add(uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, CF.getStrReportDateFormat()));//23
				appraisalList.add(uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, CF.getStrReportDateFormat()));//24
				appraisalList.add(uF.showData(getAppendData(rs.getString("reviewer_id"), hmEmpName), ""));//25
				
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
				appraisalList.add(uF.showData(sbAppraisers.toString(), ""));//26
				
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmUserType = CF.getUserTypeMap(con);
			StringBuilder sbWorkflow = new StringBuilder();
			StringBuilder sbWorkflow1 = new StringBuilder();
			pst = con.prepareStatement("select * from work_flow_details where effective_type=? and effective_id=? order by work_flow_id");
			pst.setString(1, WORK_FLOW_SELF_REVIEW);
			pst.setInt(2, uF.parseToInt(id));
			rs = pst.executeQuery();
			while (rs.next()) {
				sbWorkflow.append("<tr><th>"+hmUserType.get(rs.getString("user_type_id"))+":</th>" +
					"<td colspan=\"1\">"+hmEmpName.get(rs.getString("emp_id"))+"</td></tr>");
				
				sbWorkflow1.append("<div style=\"float: left; width: 100%;\"><span style=\"float: left; font-size: 12px; line-height: 35px;\"><b>"+hmUserType.get(rs.getString("user_type_id"))+":&nbsp;&nbsp;&nbsp;&nbsp;</b>" +
						hmEmpName.get(rs.getString("emp_id"))+"</span></div>");//Created by dattatray
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("appraisalList", appraisalList);
			request.setAttribute("sbWorkflow", sbWorkflow.toString());
			request.setAttribute("sbWorkflow1", sbWorkflow1.toString());//Created by dattatray
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
//	Create by dattatray
	private String getReviewees(Connection con, UtilityFunctions uF, String appraisalId, Map<String, String> hmEmpName) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder sbReviewees = new StringBuilder();
		try {
			
			StringBuilder sbEmpId = null;
			if(strUserType != null && strUserType.equals(MANAGER)) {
				pst = con.prepareStatement("select reviewee_id from appraisal_reviewee_details where supervisor_ids like '%,"+strSessionEmpId+",%' ");
//				System.out.println("MANAGER PST : "+pst.toString());
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
			
			StringBuilder sbQuery = new StringBuilder();
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
					sbReviewees.append("<a href=\"javascript:void(0)\" title='"+hmEmpName.get(rs.getString("reviewee_id"))+"' onclick=\"getRevieweeAppraisers('"+appraisalId+"','"+rs.getString("reviewee_id")+"', '"+hmEmpName.get(rs.getString("reviewee_id"))+"')\"> <span style=\"float:left;width:35px;height:35px;margin:2px;\"><img height=\"25\" width=\"25\" class=\"lazy img-circle zoom\" src=\"userImages/avatar_photo.png\" data-original="+CF.getStrDocRetriveLocation()+IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+rs.getString("reviewee_id")+"/"+IConstants.I_22x22+"/"+hmEmpName.get("empImage"+rs.getString("reviewee_id"))+" /></span></a>");
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

	/*private String getAppendData(String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();

		if (strID != null && !strID.equals("")) {
			strID=strID.substring(1, strID.length()-1);
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
*/
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

	private Map<String, String> getLocationMap(Connection con) {
		Map<String, String> mplocation = new HashMap<String, String>();
		UtilityFunctions uF = new UtilityFunctions();
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
		}
		return mplocation;
	}

	
	public Map<String, String> getAppraisalOtherQueType(Connection con, UtilityFunctions uF) {
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
		}
		return appOtherQueType;
	}
	
	public Map<String, String> getAppraisalQuestionMap(Connection con, UtilityFunctions uF) {

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
		}
		return AppraisalQuestion;
	}

	public Map<String, String> getAttributeMap(Connection con) {

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
		}
		return AppraisalQuestion;
	}
	
	public Map<String, String> getAnswerTypeMap(Connection con) {

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
		}
		return answerTypeMp;
	}
	
	
	public Map<String, String> getSessionUserName() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, String> hmUserName = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			
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
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmUserName;
	}


	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
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
