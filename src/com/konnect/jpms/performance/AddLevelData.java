package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class AddLevelData implements ServletRequestAware, SessionAware,
		IStatements {
	Map session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;

	private List<FillAttribute> attributeList;
	private List<FillAnswerType> ansTypeList;
	private List<FillFrequency> frequencyList;

	private String id; 

	private String oreinted;
	private String appsystem;
	private String scoreType;
	private String type;
	private String UID;

	private List<FillOrientation> orientationList;

	public String execute() {
		strUserType = (String) session.get(USERTYPE);
		strSessionEmpId = (String) session.get(EMPID);

		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return "login";

		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/performance/AddLevelData.jsp");
		request.setAttribute(TITLE, "Add Level Data");

		String levelID=getSelfIDs(getId());
//		System.out.println("levelID====>"+levelID);
		if (levelID != null && levelID.length()>0){
			attributeList = new FillAttribute(request).fillElementAttribute(levelID);
		}else{
			attributeList = new FillAttribute(request).fillElementAttribute(null);
		}
		ansTypeList = new FillAnswerType(request).fillAnswerType();

		getAppraisalDetail();
		getOrientationValue(uF.parseToInt(getOreinted()));
		getattribute();
		getOtherAnsType();
		getAppraisalQuestionList();

		String submit = request.getParameter("submit");

//		System.out.println("id=====> " + id);
//		System.out.println("appsystem=====> " + appsystem);
//		System.out.println("scoreType=====> " + scoreType);
//		System.out.println("type=====> " + type);

		if (submit != null && submit.equals("Save")) {
			if (appsystem != null && appsystem.equals("1")) {
				if (scoreType != null && scoreType.equals("1")) {
					if (type != null && type.equals("score")) {
//						System.out.println("in score ");
						insertScoreGoalObjectiveMeasureScore1();

					} else if (type != null && type.equals("goal")) {
//						System.out.println("in goal ");
						insertGoalObjectiveMeasureScore1();

					} else if (type != null && type.equals("objective")) {
//						System.out.println("in objective ");
						insertObjectiveMeasureScore1();

					} else if (type != null && type.equals("measure")) {
//						System.out.println("in measure ");
						insertMeasureScore1();

					} else if (type != null && type.equals("quest")) {
//						System.out.println("in quest ");
						insertQuestionsScore1();

					}
				} else if (scoreType != null && scoreType.equals("3")) {
					if (type != null && type.equals("score")) {
//						System.out.println("in score ");
						insertScoreGoalMeasureScore3();

					} else if (type != null && type.equals("goal")) {
//						System.out.println("in goal ");
						insertGoalMeasureScore3();

					} else if (type != null && type.equals("measure")) {
//						System.out.println("in measure ");
						insertScoreMeasureScore3();

					} else if (type != null && type.equals("quest")) {
//						System.out.println("in quest ");
						insertQuestionsScore3();

					}
				} else if (scoreType != null && scoreType.equals("2")) {
					if (type != null && type.equals("score")) {
//						System.out.println("in score ");
						insertScoreMeasureScore2();

					} else if (type != null && type.equals("measure")) {
//						System.out.println("in measure ");
						insertMeasureScore2();

					} else if (type != null && type.equals("quest")) {
//						System.out.println("in quest ");
						insertQuestionsScore2();
					}
				}
			} else if (appsystem != null && appsystem.equals("2")) {
//				System.out.println("in other quest ");
				insertOtherQuestions();
			}

			return "success";
		}

		return LOAD;
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
//			System.out.println("new Date ===> " + new Date());
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
//				System.out.println("new Date ===> " + new Date());
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

	
	private void insertOtherQuestions() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			String questionSelect = request.getParameter("questionSelect");
			String weightage = request.getParameter("weightage");		
			String question = request.getParameter("question");		
			String attribute = request.getParameter("attribute");
	
			String addFlag = request.getParameter("status");
			String optiona = request.getParameter("optiona");
			String optionb = request.getParameter("optionb");
			String optionc = request.getParameter("optionc");
			String optiond = request.getParameter("optiond");
			String optione = request.getParameter("optione");
			String rateoptiona = request.getParameter("rateoptiona");
			String rateoptionb = request.getParameter("rateoptionb");
			String rateoptionc = request.getParameter("rateoptionc");
			String rateoptiond = request.getParameter("rateoptiond");
			String rateoptione = request.getParameter("rateoptione");
			String orientt = request.getParameter("orientt");
			
			con = db.makeConnection(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);

			pst = con.prepareStatement("select othe_question_type_id from appraisal_other_question_type_details where level_id =?");
			pst.setInt(1,uF.parseToInt(getUID()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String othe_question_type_id = null;
			while (rst.next()) {
					othe_question_type_id = rst.getString("othe_question_type_id");	
			}			
			rst.close();
			pst.close();

			int question_id = uF.parseToInt(questionSelect);
			if (questionSelect.length() > 0) {
				if (uF.parseToInt(questionSelect) == 0) {

					String[] correct = request.getParameterValues("correct"+ orientt);
					String ansType = request.getParameter("ansType" + orientt);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}

//					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
						"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, optione);
					pst.setInt(7, uF.parseToInt(rateoptiona));
					pst.setInt(8, uF.parseToInt(rateoptionb));
					pst.setInt(9, uF.parseToInt(rateoptionc));
					pst.setInt(10, uF.parseToInt(rateoptiond));
					pst.setInt(11, uF.parseToInt(rateoptione));
					pst.setString(12, option.toString());
					pst.setBoolean(13, uF.parseToBoolean(addFlag));
					pst.setInt(14, uF.parseToInt(ansType));
					pst.executeUpdate();
					pst.close();
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,other_id) values(?,?,?,?, ?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, uF.parseToInt(getUID()));
				pst.setInt(3, uF.parseToInt(attribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, uF.parseToInt(getUID()));
				pst.setInt(7, uF.parseToInt(othe_question_type_id));
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

	
	private Map<String, String> getOrientMemberID(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> orientationMemberMp = new HashMap<String, String>();

		try {
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				orientationMemberMp.put(rs.getString("member_name"), rs.getString("member_id"));
			}
			rs.close();
			pst.close();

//			System.out.println("memberid=====>"+orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMemberMp;
	}
	

	private void insertQuestionsScore2() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();		

		try {
			
			String questionSelect = request.getParameter("questionSelect");
			String weightage = request.getParameter("weightage");		
			String question = request.getParameter("question");		
			String attribute = request.getParameter("attribute");
	
			String addFlag = request.getParameter("status");
			String optiona = request.getParameter("optiona");
			String optionb = request.getParameter("optionb");
			String optionc = request.getParameter("optionc");
			String optiond = request.getParameter("optiond");
			String optione = request.getParameter("optione");
			String rateoptiona = request.getParameter("rateoptiona");
			String rateoptionb = request.getParameter("rateoptionb");
			String rateoptionc = request.getParameter("rateoptionc");
			String rateoptiond = request.getParameter("rateoptiond");
			String rateoptione = request.getParameter("rateoptione");
			String orientt = request.getParameter("orientt");
	
			
			con = db.makeConnection(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);

			int scorecard_id = 0;
			pst = con.prepareStatement("select scorecard_id from appraisal_measure_details where measure_id=?");
			pst.setInt(1, uF.parseToInt(getUID()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				scorecard_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int level_id=0;
			pst = con.prepareStatement("select level_id from appraisal_scorecard_details where scorecard_id=?");
			pst.setInt(1, scorecard_id);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				 level_id= rst.getInt(1);
			}
			rst.close();
			pst.close();

			int question_id = uF.parseToInt(questionSelect);
			if (questionSelect.length() > 0) {
				if (uF.parseToInt(questionSelect) == 0) {

					String[] correct = request.getParameterValues("correct"+ orientt);
					String ansType = request.getParameter("ansType" + orientt);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}

					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
						"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, optione);
					pst.setInt(7, uF.parseToInt(rateoptiona));
					pst.setInt(8, uF.parseToInt(rateoptionb));
					pst.setInt(9, uF.parseToInt(rateoptionc));
					pst.setInt(10, uF.parseToInt(rateoptiond));
					pst.setInt(11, uF.parseToInt(rateoptione));
					pst.setString(12, option.toString());
					pst.setBoolean(13, uF.parseToBoolean(addFlag));
					pst.setInt(14, uF.parseToInt(ansType));
					pst.executeUpdate();
					pst.close();
					
					/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, option.toString());
					pst.setBoolean(7, uF.parseToBoolean(addFlag));
					pst.setInt(8, uF.parseToInt(ansType));
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id) values(?,?,?,?, ?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, uF.parseToInt(getUID()));
				pst.setInt(3, uF.parseToInt(attribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, level_id);
				pst.setInt(7, scorecard_id);
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

	private void insertMeasureScore2() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			String measuresSectionName = request.getParameter("measuresSectionName");
			String measuresDescription = request.getParameter("measuresDescription");
			String measureWeightage = request.getParameter("measureWeightage");
	
			String questionSelect = request.getParameter("questionSelect");
			String weightage = request.getParameter("weightage");
			
			String question = request.getParameter("question");
			
			String attribute = request.getParameter("attribute");
	
			String addFlag = request.getParameter("status");
			String optiona = request.getParameter("optiona");
			String optionb = request.getParameter("optionb");
			String optionc = request.getParameter("optionc");
			String optiond = request.getParameter("optiond");
			String optione = request.getParameter("optione");
			String rateoptiona = request.getParameter("rateoptiona");
			String rateoptionb = request.getParameter("rateoptionb");
			String rateoptionc = request.getParameter("rateoptionc");
			String rateoptiond = request.getParameter("rateoptiond");
			String rateoptione = request.getParameter("rateoptione");
			String orientt = request.getParameter("orientt");
	
			
			con = db.makeConnection(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
			
			int level_id=0;
						
			pst = con.prepareStatement("select level_id from appraisal_scorecard_details where scorecard_id=?");
			pst.setInt(1, uF.parseToInt(getUID()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				 level_id= rst.getInt(1);
			}	
			rst.close();
			pst.close();

			pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,scorecard_id,appraisal_id,weightage)"
							+ "values(?,?,?,?,?)");
			pst.setString(1, measuresSectionName);
			pst.setString(2, measuresDescription);
			pst.setInt(3, uF.parseToInt(getUID()));
			pst.setInt(4, uF.parseToInt(id));
			pst.setString(5, measureWeightage);
			pst.execute();
			pst.close();
			
			int measure_id = 0;
			pst = con.prepareStatement("select max(measure_id) from appraisal_measure_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				measure_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			int question_id = uF.parseToInt(questionSelect);
			if (questionSelect.length() > 0) {
				if (uF.parseToInt(questionSelect) == 0) {

					String[] correct = request.getParameterValues("correct"
							+ orientt);
					String ansType = request.getParameter("ansType" + orientt);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}

					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
						"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, optione);
					pst.setInt(7, uF.parseToInt(rateoptiona));
					pst.setInt(8, uF.parseToInt(rateoptionb));
					pst.setInt(9, uF.parseToInt(rateoptionc));
					pst.setInt(10, uF.parseToInt(rateoptiond));
					pst.setInt(11, uF.parseToInt(rateoptione));
					pst.setString(12, option.toString());
					pst.setBoolean(13, uF.parseToBoolean(addFlag));
					pst.setInt(14, uF.parseToInt(ansType));
					pst.executeUpdate();
					pst.close();
						
					
					/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, option.toString());
					pst.setBoolean(7, uF.parseToBoolean(addFlag));
					pst.setInt(8, uF.parseToInt(ansType));
					pst.execute();
					pst.close();*/
					
					pst = con
							.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id) values(?,?,?,?, ?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, measure_id);
				pst.setInt(3, uF.parseToInt(attribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(14, level_id);
				pst.setInt(15,uF.parseToInt(getUID()));
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

	private void insertScoreMeasureScore2() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			String scoreSectionName = request.getParameter("scoreSectionName");
			String scoreCardDescription = request.getParameter("scoreCardDescription");
			String scoreCardWeightage = request.getParameter("scoreCardWeightage");
	
			String measuresSectionName = request.getParameter("measuresSectionName");
			String measuresDescription = request.getParameter("measuresDescription");
			String measureWeightage = request.getParameter("measureWeightage");
	
			String questionSelect = request.getParameter("questionSelect");
			String weightage = request.getParameter("weightage");
			String question = request.getParameter("question");
	
			String attribute = request.getParameter("attribute");
	
			String addFlag = request.getParameter("status");
			String optiona = request.getParameter("optiona");
			String optionb = request.getParameter("optionb");
			String optionc = request.getParameter("optionc");
			String optiond = request.getParameter("optiond");
			String optione = request.getParameter("optione");
			String rateoptiona = request.getParameter("rateoptiona");
			String rateoptionb = request.getParameter("rateoptionb");
			String rateoptionc = request.getParameter("rateoptionc");
			String rateoptiond = request.getParameter("rateoptiond");
			String rateoptione = request.getParameter("rateoptione");
			String orientt = request.getParameter("orientt");
	
			con = db.makeConnection(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
		
			pst = con.prepareStatement("insert into appraisal_scorecard_details(scorecard_section_name,scorecard_description,"
							+ "scorecard_weightage,level_id,appraisal_id,appraisal_attribute)values(?,?,?,?,?,?)");
			pst.setString(1, scoreSectionName);
			pst.setString(2, scoreCardDescription);
			pst.setString(3, scoreCardWeightage);
			pst.setInt(4, uF.parseToInt(getUID()));
			pst.setInt(5, uF.parseToInt(id));
			pst.setInt(6, uF.parseToInt(attribute));
			pst.execute();
			pst.close();

			int scorecard_id = 0;
			pst = con
					.prepareStatement("select max(scorecard_id) from appraisal_scorecard_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				scorecard_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			pst = con
					.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,scorecard_id,appraisal_id,weightage)"
							+ "values(?,?,?,?,?)");
			pst.setString(1, measuresSectionName);
			pst.setString(2, measuresDescription);
			pst.setInt(3, scorecard_id);
			pst.setInt(4, uF.parseToInt(id));
			pst.setString(5, measureWeightage);
			pst.execute();
			pst.close();
			
			int measure_id = 0;
			pst = con
					.prepareStatement("select max(measure_id) from appraisal_measure_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				measure_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int question_id = uF.parseToInt(questionSelect);
			if (questionSelect.length() > 0) {
				if (uF.parseToInt(questionSelect) == 0) {

					String[] correct = request.getParameterValues("correct"
							+ orientt);
					String ansType = request.getParameter("ansType" + orientt);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}

					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
						"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, optione);
					pst.setInt(7, uF.parseToInt(rateoptiona));
					pst.setInt(8, uF.parseToInt(rateoptionb));
					pst.setInt(9, uF.parseToInt(rateoptionc));
					pst.setInt(10, uF.parseToInt(rateoptiond));
					pst.setInt(11, uF.parseToInt(rateoptione));
					pst.setString(12, option.toString());
					pst.setBoolean(13, uF.parseToBoolean(addFlag));
					pst.setInt(14, uF.parseToInt(ansType));
					pst.executeUpdate();
					pst.close();
					
					/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, option.toString());
					pst.setBoolean(7, uF.parseToBoolean(addFlag));
					pst.setInt(8, uF.parseToInt(ansType));
					pst.execute();
					pst.close();*/
					
					pst = con
							.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id) values(?,?,?,?, ?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, measure_id);
				pst.setInt(3, uF.parseToInt(attribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, uF.parseToInt(getUID()));
				pst.setInt(7, scorecard_id);
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

	private void insertQuestionsScore3() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();		

		try {

		String questionSelect = request.getParameter("questionSelect");
		String weightage = request.getParameter("weightage");		
		String question = request.getParameter("question");		
		String attribute = request.getParameter("attribute");

		String addFlag = request.getParameter("status");
		String optiona = request.getParameter("optiona");
		String optionb = request.getParameter("optionb");
		String optionc = request.getParameter("optionc");
		String optiond = request.getParameter("optiond");
		String optione = request.getParameter("optione");
		String rateoptiona = request.getParameter("rateoptiona");
		String rateoptionb = request.getParameter("rateoptionb");
		String rateoptionc = request.getParameter("rateoptionc");
		String rateoptiond = request.getParameter("rateoptiond");
		String rateoptione = request.getParameter("rateoptione");
		String orientt = request.getParameter("orientt");

		
		con = db.makeConnection(con);
		Map<String, String> orientationMemberMp = getOrientationMember(con);
		Map<String, String> hmOrientMemberID = getOrientMemberID(con);
			
			int goal_id = 0;
			pst = con
					.prepareStatement("select goal_id from appraisal_measure_details where measure_id=?");
			pst.setInt(1, uF.parseToInt(getUID()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				goal_id = rst.getInt(1);
			}	
			rst.close();
			pst.close();
			
			int scorecard_id=0;
			pst = con.prepareStatement("select scorecard_id from appraisal_goal_details where goal_id=?");
			pst.setInt(1, goal_id);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				scorecard_id= rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int level_id=0;
			pst = con.prepareStatement("select level_id from appraisal_scorecard_details where scorecard_id=?");
			pst.setInt(1, scorecard_id);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				 level_id= rst.getInt(1);
			}		
			rst.close();
			pst.close();

			int question_id = uF.parseToInt(questionSelect);
			if (questionSelect.length() > 0) {
				if (uF.parseToInt(questionSelect) == 0) {

					String[] correct = request.getParameterValues("correct"
							+ orientt);
					String ansType = request.getParameter("ansType" + orientt);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}

					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
						"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, optione);
					pst.setInt(7, uF.parseToInt(rateoptiona));
					pst.setInt(8, uF.parseToInt(rateoptionb));
					pst.setInt(9, uF.parseToInt(rateoptionc));
					pst.setInt(10, uF.parseToInt(rateoptiond));
					pst.setInt(11, uF.parseToInt(rateoptione));
					pst.setString(12, option.toString());
					pst.setBoolean(13, uF.parseToBoolean(addFlag));
					pst.setInt(14, uF.parseToInt(ansType));
					pst.executeUpdate();
					pst.close();
					
					/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, option.toString());
					pst.setBoolean(7, uF.parseToBoolean(addFlag));
					pst.setInt(8, uF.parseToInt(ansType));
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id) values(?,?,?,?, ?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, uF.parseToInt(getUID()));
				pst.setInt(3, uF.parseToInt(attribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, level_id);
				pst.setInt(7, scorecard_id);
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

	private void insertGoalMeasureScore3() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();		

		
		try {
			String goalSectionName = request.getParameter("goalSectionName");
			String goalDescription = request.getParameter("goalDescription");
			String goalWeightage = request.getParameter("goalWeightage");
		
			String measuresSectionName = request
					.getParameter("measuresSectionName");
			String measuresDescription = request
					.getParameter("measuresDescription");
			String measureWeightage = request.getParameter("measureWeightage");
	
			String questionSelect = request.getParameter("questionSelect");
			String weightage = request.getParameter("weightage");
			String question = request.getParameter("question");
	
			String attribute = request.getParameter("attribute");
	
			String addFlag = request.getParameter("status");
			String optiona = request.getParameter("optiona");
			String optionb = request.getParameter("optionb");
			String optionc = request.getParameter("optionc");
			String optiond = request.getParameter("optiond");
			String optione = request.getParameter("optione");
			String rateoptiona = request.getParameter("rateoptiona");
			String rateoptionb = request.getParameter("rateoptionb");
			String rateoptionc = request.getParameter("rateoptionc");
			String rateoptiond = request.getParameter("rateoptiond");
			String rateoptione = request.getParameter("rateoptione");
			String orientt = request.getParameter("orientt");
	
			
			con = db.makeConnection(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);

			int level_id=0;
			pst = con.prepareStatement("select level_id from appraisal_scorecard_details where scorecard_id=?");
			pst.setInt(1, uF.parseToInt(getUID()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				 level_id= rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			pst = con
					.prepareStatement("insert into appraisal_goal_details(goal_section_name,goal_description,"
							+ "goal_weightage,scorecard_id,appraisal_id)values(?,?,?,?,?)");
			pst.setString(1, goalSectionName);
			pst.setString(2, goalDescription);
			pst.setString(3, goalWeightage);
			pst.setInt(4, uF.parseToInt(getUID()));
			pst.setInt(5, uF.parseToInt(id));
			pst.execute();
			pst.close();
			
			int goal_id = 0;
			pst = con
					.prepareStatement("select max(goal_id) from appraisal_goal_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				goal_id = rst.getInt(1);
			}
			rst.close();
			pst.close();


			pst = con
					.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,goal_id,appraisal_id,weightage)"
							+ "values(?,?,?,?,?)");
			pst.setString(1, measuresSectionName);
			pst.setString(2, measuresDescription);
			pst.setInt(3, goal_id);
			pst.setInt(4, uF.parseToInt(id));
			pst.setString(5, measureWeightage);
			pst.execute();
			pst.close();
			
			int measure_id = 0;
			pst = con
					.prepareStatement("select max(measure_id) from appraisal_measure_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				measure_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			int question_id = uF.parseToInt(questionSelect);
			if (questionSelect.length() > 0) {
				if (uF.parseToInt(questionSelect) == 0) {

					String[] correct = request.getParameterValues("correct"
							+ orientt);
					String ansType = request.getParameter("ansType" + orientt);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}

					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
						"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, optione);
					pst.setInt(7, uF.parseToInt(rateoptiona));
					pst.setInt(8, uF.parseToInt(rateoptionb));
					pst.setInt(9, uF.parseToInt(rateoptionc));
					pst.setInt(10, uF.parseToInt(rateoptiond));
					pst.setInt(11, uF.parseToInt(rateoptione));
					pst.setString(12, option.toString());
					pst.setBoolean(13, uF.parseToBoolean(addFlag));
					pst.setInt(14, uF.parseToInt(ansType));
					pst.executeUpdate();
					pst.close();
					
					/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, option.toString());
					pst.setBoolean(7, uF.parseToBoolean(addFlag));
					pst.setInt(8, uF.parseToInt(ansType));
					pst.execute();
					pst.close();*/
					
					pst = con
							.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id) values(?,?,?,?, ?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, measure_id);
				pst.setInt(3, uF.parseToInt(attribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(14, level_id);
				pst.setInt(15,  uF.parseToInt(getUID()));
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

	private void insertScoreMeasureScore3() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			String measuresSectionName = request
					.getParameter("measuresSectionName");
			String measuresDescription = request
					.getParameter("measuresDescription");
			String measureWeightage = request.getParameter("measureWeightage");
	
			String questionSelect = request.getParameter("questionSelect");
			String weightage = request.getParameter("weightage");
			
			String question = request.getParameter("question");
			
			String attribute = request.getParameter("attribute");
	
			String addFlag = request.getParameter("status");
			String optiona = request.getParameter("optiona");
			String optionb = request.getParameter("optionb");
			String optionc = request.getParameter("optionc");
			String optiond = request.getParameter("optiond");
			String optione = request.getParameter("optione");
			String rateoptiona = request.getParameter("rateoptiona");
			String rateoptionb = request.getParameter("rateoptionb");
			String rateoptionc = request.getParameter("rateoptionc");
			String rateoptiond = request.getParameter("rateoptiond");
			String rateoptione = request.getParameter("rateoptione");
			String orientt = request.getParameter("orientt");
	
			
			con = db.makeConnection(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);

			int level_id=0;
			int scorecard_id=0;
			pst = con.prepareStatement("select scorecard_id from appraisal_goal_details where goal_id=?");
			pst.setInt(1, uF.parseToInt(getUID()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				scorecard_id= rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select level_id from appraisal_scorecard_details where scorecard_id=?");
			pst.setInt(1, scorecard_id);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				 level_id= rst.getInt(1);
			}		
			rst.close();
			pst.close();

			pst = con
					.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,objective_id,appraisal_id,weightage)"
							+ "values(?,?,?,?,?)");
			pst.setString(1, measuresSectionName);
			pst.setString(2, measuresDescription);
			pst.setInt(3, uF.parseToInt(getUID()));
			pst.setInt(4, uF.parseToInt(id));
			pst.setString(5, measureWeightage);
			pst.execute();
			pst.close();
			
			int measure_id = 0;
			pst = con
					.prepareStatement("select max(measure_id) from appraisal_measure_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				measure_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			int question_id = uF.parseToInt(questionSelect);
			if (questionSelect.length() > 0) {
				if (uF.parseToInt(questionSelect) == 0) {

					String[] correct = request.getParameterValues("correct"
							+ orientt);
					String ansType = request.getParameter("ansType" + orientt);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}

					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
						"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, optione);
					pst.setInt(7, uF.parseToInt(rateoptiona));
					pst.setInt(8, uF.parseToInt(rateoptionb));
					pst.setInt(9, uF.parseToInt(rateoptionc));
					pst.setInt(10, uF.parseToInt(rateoptiond));
					pst.setInt(11, uF.parseToInt(rateoptione));
					pst.setString(12, option.toString());
					pst.setBoolean(13, uF.parseToBoolean(addFlag));
					pst.setInt(14, uF.parseToInt(ansType));
					pst.executeUpdate();
					pst.close();
						
					/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, option.toString());
					pst.setBoolean(7, uF.parseToBoolean(addFlag));
					pst.setInt(8, uF.parseToInt(ansType));
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id) values(?,?,?,?, ?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, measure_id);
				pst.setInt(3, uF.parseToInt(attribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, level_id);
				pst.setInt(7, scorecard_id);
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

	private void insertScoreGoalMeasureScore3() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			String scoreSectionName = request.getParameter("scoreSectionName");
			String scoreCardDescription = request
					.getParameter("scoreCardDescription");
			String scoreCardWeightage = request.getParameter("scoreCardWeightage");
	
			String goalSectionName = request.getParameter("goalSectionName");
			String goalDescription = request.getParameter("goalDescription");
			String goalWeightage = request.getParameter("goalWeightage");
		
	
			String measuresSectionName = request
					.getParameter("measuresSectionName");
			String measuresDescription = request
					.getParameter("measuresDescription");
			String measureWeightage = request.getParameter("measureWeightage");
	
			String questionSelect = request.getParameter("questionSelect");
			String weightage = request.getParameter("weightage");
			String question = request.getParameter("question");
	
			String attribute = request.getParameter("attribute");
	
			String addFlag = request.getParameter("status");
			String optiona = request.getParameter("optiona");
			String optionb = request.getParameter("optionb");
			String optionc = request.getParameter("optionc");
			String optiond = request.getParameter("optiond");
			String optione = request.getParameter("optione");
			String rateoptiona = request.getParameter("rateoptiona");
			String rateoptionb = request.getParameter("rateoptionb");
			String rateoptionc = request.getParameter("rateoptionc");
			String rateoptiond = request.getParameter("rateoptiond");
			String rateoptione = request.getParameter("rateoptione");
			String orientt = request.getParameter("orientt");
	
			
			con = db.makeConnection(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
		
			pst = con
					.prepareStatement("insert into appraisal_scorecard_details(scorecard_section_name,scorecard_description,"
							+ "scorecard_weightage,level_id,appraisal_id,appraisal_attribute)values(?,?,?,?,?,?)");
			pst.setString(1, scoreSectionName);
			pst.setString(2, scoreCardDescription);
			pst.setString(3, scoreCardWeightage);
			pst.setInt(4, uF.parseToInt(getUID()));
			pst.setInt(5, uF.parseToInt(id));
			pst.setInt(6, uF.parseToInt(attribute));
			pst.execute();
			pst.close();
			
			int scorecard_id = 0;
			pst = con
					.prepareStatement("select max(scorecard_id) from appraisal_scorecard_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				scorecard_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			pst = con
					.prepareStatement("insert into appraisal_goal_details(goal_section_name,goal_description,"
							+ "goal_weightage,scorecard_id,appraisal_id)values(?,?,?,?,?)");
			pst.setString(1, goalSectionName);
			pst.setString(2, goalDescription);
			pst.setString(3, goalWeightage);
			pst.setInt(4, scorecard_id);
			pst.setInt(5, uF.parseToInt(id));
			pst.execute();
			pst.close();
			
			int goal_id = 0;
			pst = con
					.prepareStatement("select max(goal_id) from appraisal_goal_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				goal_id = rst.getInt(1);
			}
			rst.close();
			pst.close();


			pst = con
					.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,goal_id,appraisal_id,weightage)"
							+ "values(?,?,?,?,?)");
			pst.setString(1, measuresSectionName);
			pst.setString(2, measuresDescription);
			pst.setInt(3, goal_id);
			pst.setInt(4, uF.parseToInt(id));
			pst.setString(5, measureWeightage);
			pst.execute();
			pst.close();
			
			int measure_id = 0;
			pst = con
					.prepareStatement("select max(measure_id) from appraisal_measure_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				measure_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			int question_id = uF.parseToInt(questionSelect);
			if (questionSelect.length() > 0) {
				if (uF.parseToInt(questionSelect) == 0) {

					String[] correct = request.getParameterValues("correct"
							+ orientt);
					String ansType = request.getParameter("ansType" + orientt);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}

					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
						"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, optione);
					pst.setInt(7, uF.parseToInt(rateoptiona));
					pst.setInt(8, uF.parseToInt(rateoptionb));
					pst.setInt(9, uF.parseToInt(rateoptionc));
					pst.setInt(10, uF.parseToInt(rateoptiond));
					pst.setInt(11, uF.parseToInt(rateoptione));
					pst.setString(12, option.toString());
					pst.setBoolean(13, uF.parseToBoolean(addFlag));
					pst.setInt(14, uF.parseToInt(ansType));
					pst.executeUpdate();
					pst.close();
					
					/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, option.toString());
					pst.setBoolean(7, uF.parseToBoolean(addFlag));
					pst.setInt(8, uF.parseToInt(ansType));
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id) values(?,?,?,?, ?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, measure_id);
				pst.setInt(3, uF.parseToInt(attribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, uF.parseToInt(getUID()));
				pst.setInt(7, scorecard_id);
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

	private void insertQuestionsScore1() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();		

		try {
			
			String questionSelect = request.getParameter("questionSelect");
			String weightage = request.getParameter("weightage");		
			String question = request.getParameter("question");		
			String attribute = request.getParameter("attribute");
	
			String addFlag = request.getParameter("status");
			String optiona = request.getParameter("optiona");
			String optionb = request.getParameter("optionb");
			String optionc = request.getParameter("optionc");
			String optiond = request.getParameter("optiond");
			String optione = request.getParameter("optione");
			String rateoptiona = request.getParameter("rateoptiona");
			String rateoptionb = request.getParameter("rateoptionb");
			String rateoptionc = request.getParameter("rateoptionc");
			String rateoptiond = request.getParameter("rateoptiond");
			String rateoptione = request.getParameter("rateoptione");
			String orientt = request.getParameter("orientt");
	
			con = db.makeConnection(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);

			int objective_id = 0;
			pst = con
					.prepareStatement("select objective_id from appraisal_measure_details where measure_id=?");
			pst.setInt(1, uF.parseToInt(getUID()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				objective_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int goal_id=0;
			pst = con
					.prepareStatement("select goal_id from appraisal_objective_details where objective_id=?");
			pst.setInt(1, objective_id);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				goal_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int scorecard_id=0;
			pst = con.prepareStatement("select scorecard_id from appraisal_goal_details where goal_id=?");
			pst.setInt(1, goal_id);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				scorecard_id= rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int level_id=0;
			pst = con.prepareStatement("select level_id from appraisal_scorecard_details where scorecard_id=?");
			pst.setInt(1, scorecard_id);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				 level_id= rst.getInt(1);
			}		
			rst.close();
			pst.close();

			int question_id = uF.parseToInt(questionSelect);
			if (questionSelect.length() > 0) {
				if (uF.parseToInt(questionSelect) == 0) {

					String[] correct = request.getParameterValues("correct"
							+ orientt);
					String ansType = request.getParameter("ansType" + orientt);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}

					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
						"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, optione);
					pst.setInt(7, uF.parseToInt(rateoptiona));
					pst.setInt(8, uF.parseToInt(rateoptionb));
					pst.setInt(9, uF.parseToInt(rateoptionc));
					pst.setInt(10, uF.parseToInt(rateoptiond));
					pst.setInt(11, uF.parseToInt(rateoptione));
					pst.setString(12, option.toString());
					pst.setBoolean(13, uF.parseToBoolean(addFlag));
					pst.setInt(14, uF.parseToInt(ansType));
					pst.executeUpdate();
					pst.close();
					
					/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, option.toString());
					pst.setBoolean(7, uF.parseToBoolean(addFlag));
					pst.setInt(8, uF.parseToInt(ansType));
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id) values(?,?,?,?, ?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, uF.parseToInt(getUID()));
				pst.setInt(3, uF.parseToInt(attribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, level_id);
				pst.setInt(7, scorecard_id);
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

	private void insertMeasureScore1() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			String measuresSectionName = request
					.getParameter("measuresSectionName");
			String measuresDescription = request
					.getParameter("measuresDescription");
			String measureWeightage = request.getParameter("measureWeightage");
	
			String questionSelect = request.getParameter("questionSelect");
			String weightage = request.getParameter("weightage");
			
			String question = request.getParameter("question");
			
			String attribute = request.getParameter("attribute");
	
			String addFlag = request.getParameter("status");
			String optiona = request.getParameter("optiona");
			String optionb = request.getParameter("optionb");
			String optionc = request.getParameter("optionc");
			String optiond = request.getParameter("optiond");
			String optione = request.getParameter("optione");
			String rateoptiona = request.getParameter("rateoptiona");
			String rateoptionb = request.getParameter("rateoptionb");
			String rateoptionc = request.getParameter("rateoptionc");
			String rateoptiond = request.getParameter("rateoptiond");
			String rateoptione = request.getParameter("rateoptione");
			String orientt = request.getParameter("orientt");
	
			
			con = db.makeConnection(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
		
			int goal_id=0;
			pst = con
					.prepareStatement("select goal_id from appraisal_objective_details where objective_id=?");
			pst.setInt(1, uF.parseToInt(getUID()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				goal_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int level_id=0;
			int scorecard_id=0;
			pst = con.prepareStatement("select scorecard_id from appraisal_goal_details where goal_id=?");
			pst.setInt(1, goal_id);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				scorecard_id= rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select level_id from appraisal_scorecard_details where scorecard_id=?");
			pst.setInt(1, scorecard_id);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				 level_id= rst.getInt(1);
			}	
			rst.close();
			pst.close();

			pst = con
					.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,objective_id,appraisal_id,weightage)"
							+ "values(?,?,?,?,?)");
			pst.setString(1, measuresSectionName);
			pst.setString(2, measuresDescription);
			pst.setInt(3, uF.parseToInt(getUID()));
			pst.setInt(4, uF.parseToInt(id));
			pst.setString(5, measureWeightage);
			pst.execute();
			pst.close();
			
			int measure_id = 0;
			pst = con
					.prepareStatement("select max(measure_id) from appraisal_measure_details");
			rst = pst.executeQuery();
		//	System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				measure_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			int question_id = uF.parseToInt(questionSelect);
			if (questionSelect.length() > 0) {
				if (uF.parseToInt(questionSelect) == 0) {

					String[] correct = request.getParameterValues("correct"
							+ orientt);
					String ansType = request.getParameter("ansType" + orientt);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}

					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
						"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, optione);
					pst.setInt(7, uF.parseToInt(rateoptiona));
					pst.setInt(8, uF.parseToInt(rateoptionb));
					pst.setInt(9, uF.parseToInt(rateoptionc));
					pst.setInt(10, uF.parseToInt(rateoptiond));
					pst.setInt(11, uF.parseToInt(rateoptione));
					pst.setString(12, option.toString());
					pst.setBoolean(13, uF.parseToBoolean(addFlag));
					pst.setInt(14, uF.parseToInt(ansType));
					pst.executeUpdate();
					pst.close();
					
					/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, option.toString());
					pst.setBoolean(7, uF.parseToBoolean(addFlag));
					pst.setInt(8, uF.parseToInt(ansType));
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id) values(?,?,?,?, ?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, measure_id);
				pst.setInt(3, uF.parseToInt(attribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, level_id);
				pst.setInt(7, scorecard_id);
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

	private void insertObjectiveMeasureScore1() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			String objectiveSectionName = request
					.getParameter("objectiveSectionName");
			String objectiveDescription = request
					.getParameter("objectiveDescription");
			String objectiveWeightage = request.getParameter("objectiveWeightage");
	
			String measuresSectionName = request
					.getParameter("measuresSectionName");
			String measuresDescription = request
					.getParameter("measuresDescription");
			String measureWeightage = request.getParameter("measureWeightage");
	
			String questionSelect = request.getParameter("questionSelect");
			String weightage = request.getParameter("weightage");
			
			String question = request.getParameter("question");
			
			String attribute = request.getParameter("attribute");
	
			String addFlag = request.getParameter("status");
			String optiona = request.getParameter("optiona");
			String optionb = request.getParameter("optionb");
			String optionc = request.getParameter("optionc");
			String optiond = request.getParameter("optiond");
			String optione = request.getParameter("optione");
			String rateoptiona = request.getParameter("rateoptiona");
			String rateoptionb = request.getParameter("rateoptionb");
			String rateoptionc = request.getParameter("rateoptionc");
			String rateoptiond = request.getParameter("rateoptiond");
			String rateoptione = request.getParameter("rateoptione");
			String orientt = request.getParameter("orientt");
	
			
			con = db.makeConnection(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
		
			int level_id=0;
			int scorecard_id=0;
			pst = con.prepareStatement("select scorecard_id from appraisal_goal_details where goal_id=?");
			pst.setInt(1, uF.parseToInt(getUID()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				scorecard_id= rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select level_id from appraisal_scorecard_details where scorecard_id=?");
			pst.setInt(1, scorecard_id);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				 level_id= rst.getInt(1);
			}			
			rst.close();
			pst.close();

			pst = con
					.prepareStatement("insert into appraisal_objective_details(objective_section_name,objective_description,"
							+ "objective_weightage,goal_id,appraisal_id)values(?,?,?,?,?)");
			pst.setString(1, objectiveSectionName);
			pst.setString(2, objectiveDescription);
			pst.setString(3, objectiveWeightage);
			pst.setInt(4, uF.parseToInt(getUID()));
			pst.setInt(5, uF.parseToInt(id));
			pst.execute();
			pst.close();
			
			int objective_id = 0;
			pst = con
					.prepareStatement("select max(objective_id) from appraisal_objective_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				objective_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			pst = con
					.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,objective_id,appraisal_id,weightage)"
							+ "values(?,?,?,?,?)");
			pst.setString(1, measuresSectionName);
			pst.setString(2, measuresDescription);
			pst.setInt(3, objective_id);
			pst.setInt(4, uF.parseToInt(id));
			pst.setString(5, measureWeightage);
			pst.execute();
			pst.close();
			
			int measure_id = 0;
			pst = con
					.prepareStatement("select max(measure_id) from appraisal_measure_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				measure_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			int question_id = uF.parseToInt(questionSelect);
			if (questionSelect.length() > 0) {
				if (uF.parseToInt(questionSelect) == 0) {

					String[] correct = request.getParameterValues("correct"
							+ orientt);
					String ansType = request.getParameter("ansType" + orientt);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}

					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
						"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, optione);
					pst.setInt(7, uF.parseToInt(rateoptiona));
					pst.setInt(8, uF.parseToInt(rateoptionb));
					pst.setInt(9, uF.parseToInt(rateoptionc));
					pst.setInt(10, uF.parseToInt(rateoptiond));
					pst.setInt(11, uF.parseToInt(rateoptione));
					pst.setString(12, option.toString());
					pst.setBoolean(13, uF.parseToBoolean(addFlag));
					pst.setInt(14, uF.parseToInt(ansType));
					pst.executeUpdate();
					pst.close();
					
					/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, option.toString());
					pst.setBoolean(7, uF.parseToBoolean(addFlag));
					pst.setInt(8, uF.parseToInt(ansType));
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id) values(?,?,?,?, ?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, measure_id);
				pst.setInt(3, uF.parseToInt(attribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, level_id);
				pst.setInt(7,scorecard_id);
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

	private void insertGoalObjectiveMeasureScore1() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			String goalSectionName = request.getParameter("goalSectionName");
			String goalDescription = request.getParameter("goalDescription");
			String goalWeightage = request.getParameter("goalWeightage");
	
			String objectiveSectionName = request
					.getParameter("objectiveSectionName");
			String objectiveDescription = request
					.getParameter("objectiveDescription");
			String objectiveWeightage = request.getParameter("objectiveWeightage");
	
			String measuresSectionName = request
					.getParameter("measuresSectionName");
			String measuresDescription = request
					.getParameter("measuresDescription");
			String measureWeightage = request.getParameter("measureWeightage");
	
			String questionSelect = request.getParameter("questionSelect");
			String weightage = request.getParameter("weightage");
			
			String question = request.getParameter("question");
			
			String attribute = request.getParameter("attribute");
	
			String addFlag = request.getParameter("status");
			String optiona = request.getParameter("optiona");
			String optionb = request.getParameter("optionb");
			String optionc = request.getParameter("optionc");
			String optiond = request.getParameter("optiond");
			String optione = request.getParameter("optione");
			String rateoptiona = request.getParameter("rateoptiona");
			String rateoptionb = request.getParameter("rateoptionb");
			String rateoptionc = request.getParameter("rateoptionc");
			String rateoptiond = request.getParameter("rateoptiond");
			String rateoptione = request.getParameter("rateoptione");
			String orientt = request.getParameter("orientt");
	
			
			con = db.makeConnection(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);

			int level_id=0;
			pst = con.prepareStatement("select level_id from appraisal_scorecard_details where scorecard_id=?");
			pst.setInt(1, uF.parseToInt(getUID()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				 level_id= rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			pst = con
					.prepareStatement("insert into appraisal_goal_details(goal_section_name,goal_description,"
							+ "goal_weightage,scorecard_id,appraisal_id)values(?,?,?,?,?)");
			pst.setString(1, goalSectionName);
			pst.setString(2, goalDescription);
			pst.setString(3, goalWeightage);
			pst.setInt(4, uF.parseToInt(getUID()));
			pst.setInt(5, uF.parseToInt(id));
			pst.execute();
			pst.close();
			
			int goal_id = 0;
			pst = con
					.prepareStatement("select max(goal_id) from appraisal_goal_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				goal_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			pst = con
					.prepareStatement("insert into appraisal_objective_details(objective_section_name,objective_description,"
							+ "objective_weightage,goal_id,appraisal_id)values(?,?,?,?,?)");
			pst.setString(1, objectiveSectionName);
			pst.setString(2, objectiveDescription);
			pst.setString(3, objectiveWeightage);
			pst.setInt(4, goal_id);
			pst.setInt(5, uF.parseToInt(id));
			pst.execute();
			pst.close();
			
			int objective_id = 0;
			pst = con
					.prepareStatement("select max(objective_id) from appraisal_objective_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				objective_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			pst = con
					.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,objective_id,appraisal_id,weightage)"
							+ "values(?,?,?,?,?)");
			pst.setString(1, measuresSectionName);
			pst.setString(2, measuresDescription);
			pst.setInt(3, objective_id);
			pst.setInt(4, uF.parseToInt(id));
			pst.setString(5, measureWeightage);
			pst.execute();
			pst.close();
			
			int measure_id = 0;
			pst = con
					.prepareStatement("select max(measure_id) from appraisal_measure_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				measure_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			int question_id = uF.parseToInt(questionSelect);
			if (questionSelect.length() > 0) {
				if (uF.parseToInt(questionSelect) == 0) {

					String[] correct = request.getParameterValues("correct"
							+ orientt);
					String ansType = request.getParameter("ansType" + orientt);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}

					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
						"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, optione);
					pst.setInt(7, uF.parseToInt(rateoptiona));
					pst.setInt(8, uF.parseToInt(rateoptionb));
					pst.setInt(9, uF.parseToInt(rateoptionc));
					pst.setInt(10, uF.parseToInt(rateoptiond));
					pst.setInt(11, uF.parseToInt(rateoptione));
					pst.setString(12, option.toString());
					pst.setBoolean(13, uF.parseToBoolean(addFlag));
					pst.setInt(14, uF.parseToInt(ansType));
					pst.executeUpdate();
					pst.close();
						
						
					/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, option.toString());
					pst.setBoolean(7, uF.parseToBoolean(addFlag));
					pst.setInt(8, uF.parseToInt(ansType));
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
					System.out.println("new Date ===> " + new Date());
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id) values(?,?,?,?, ?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, measure_id);
				pst.setInt(3, uF.parseToInt(attribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, level_id);
				pst.setInt(7, uF.parseToInt(getUID()));
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

	private void insertScoreGoalObjectiveMeasureScore1() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			String scoreSectionName = request.getParameter("scoreSectionName");
			String scoreCardDescription = request
					.getParameter("scoreCardDescription");
			String scoreCardWeightage = request.getParameter("scoreCardWeightage");
	
			String goalSectionName = request.getParameter("goalSectionName");
			String goalDescription = request.getParameter("goalDescription");
			String goalWeightage = request.getParameter("goalWeightage");
	
			String objectiveSectionName = request
					.getParameter("objectiveSectionName");
			String objectiveDescription = request
					.getParameter("objectiveDescription");
			String objectiveWeightage = request.getParameter("objectiveWeightage");
	
			String measuresSectionName = request
					.getParameter("measuresSectionName");
			String measuresDescription = request
					.getParameter("measuresDescription");
			String measureWeightage = request.getParameter("measureWeightage");
	
			String questionSelect = request.getParameter("questionSelect");
			String weightage = request.getParameter("weightage");
			String question = request.getParameter("question");
	
			String attribute = request.getParameter("attribute");
	
			String addFlag = request.getParameter("status");
			String optiona = request.getParameter("optiona");
			String optionb = request.getParameter("optionb");
			String optionc = request.getParameter("optionc");
			String optiond = request.getParameter("optiond");
			String optione = request.getParameter("optione");
			String rateoptiona = request.getParameter("rateoptiona");
			String rateoptionb = request.getParameter("rateoptionb");
			String rateoptionc = request.getParameter("rateoptionc");
			String rateoptiond = request.getParameter("rateoptiond");
			String rateoptione = request.getParameter("rateoptione");
			String orientt = request.getParameter("orientt");
	
			
			con = db.makeConnection(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);

			pst = con
					.prepareStatement("insert into appraisal_scorecard_details(scorecard_section_name,scorecard_description,"
							+ "scorecard_weightage,level_id,appraisal_id,appraisal_attribute)values(?,?,?,?,?,?)");
			pst.setString(1, scoreSectionName);
			pst.setString(2, scoreCardDescription);
			pst.setString(3, scoreCardWeightage);
			pst.setInt(4, uF.parseToInt(getUID()));
			pst.setInt(5, uF.parseToInt(id));
			pst.setInt(6, uF.parseToInt(attribute));
			pst.execute();
			pst.close();
			
			int scorecard_id = 0;
			pst = con
					.prepareStatement("select max(scorecard_id) from appraisal_scorecard_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				scorecard_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			pst = con
					.prepareStatement("insert into appraisal_goal_details(goal_section_name,goal_description,"
							+ "goal_weightage,scorecard_id,appraisal_id)values(?,?,?,?,?)");
			pst.setString(1, goalSectionName);
			pst.setString(2, goalDescription);
			pst.setString(3, goalWeightage);
			pst.setInt(4, scorecard_id);
			pst.setInt(5, uF.parseToInt(id));
			pst.execute();
			pst.close();
			
			int goal_id = 0;
			pst = con
					.prepareStatement("select max(goal_id) from appraisal_goal_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				goal_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			pst = con
					.prepareStatement("insert into appraisal_objective_details(objective_section_name,objective_description,"
							+ "objective_weightage,goal_id,appraisal_id)values(?,?,?,?,?)");
			pst.setString(1, objectiveSectionName);
			pst.setString(2, objectiveDescription);
			pst.setString(3, objectiveWeightage);
			pst.setInt(4, goal_id);
			pst.setInt(5, uF.parseToInt(id));
			pst.execute();
			pst.close();
			
			int objective_id = 0;
			pst = con
					.prepareStatement("select max(objective_id) from appraisal_objective_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				objective_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			pst = con
					.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,objective_id,appraisal_id,weightage)"
							+ "values(?,?,?,?,?)");
			pst.setString(1, measuresSectionName);
			pst.setString(2, measuresDescription);
			pst.setInt(3, objective_id);
			pst.setInt(4, uF.parseToInt(id));
			pst.setString(5, measureWeightage);
			pst.execute();
			pst.close();
			
			int measure_id = 0;
			pst = con
					.prepareStatement("select max(measure_id) from appraisal_measure_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				measure_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			int question_id = uF.parseToInt(questionSelect);
			if (questionSelect.length() > 0) {
				if (uF.parseToInt(questionSelect) == 0) {

					String[] correct = request.getParameterValues("correct"
							+ orientt);
					String ansType = request.getParameter("ansType" + orientt);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}

					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
						"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, optione);
					pst.setInt(7, uF.parseToInt(rateoptiona));
					pst.setInt(8, uF.parseToInt(rateoptionb));
					pst.setInt(9, uF.parseToInt(rateoptionc));
					pst.setInt(10, uF.parseToInt(rateoptiond));
					pst.setInt(11, uF.parseToInt(rateoptione));
					pst.setString(12, option.toString());
					pst.setBoolean(13, uF.parseToBoolean(addFlag));
					pst.setInt(14, uF.parseToInt(ansType));
					pst.executeUpdate();
					pst.close();
					
					/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
					pst.setString(1, question);
					pst.setString(2, optiona);
					pst.setString(3, optionb);
					pst.setString(4, optionc);
					pst.setString(5, optiond);
					pst.setString(6, option.toString());
					pst.setBoolean(7, uF.parseToBoolean(addFlag));
					pst.setInt(8, uF.parseToInt(ansType));
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id) values(?,?,?,?, ?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, measure_id);
				pst.setInt(3, uF.parseToInt(attribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, uF.parseToInt(getUID()));
				pst.setInt(7, scorecard_id);
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

	private void getAppraisalDetail() {

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
			Map<String, String> orientationMp = getOrientationValue(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
		
			Map<String, String> hmFrequency = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				hmFrequency.put(rs.getString("appraisal_frequency_id"),
						rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();

			List<String> appraisalList = new ArrayList<String>();
			pst = con
					.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				List<String> memberList = Arrays.asList(rs.getString("usertype_member").split(","));
				String memberName = "";

				for (int i = 0; i < memberList.size(); i++) {
					if (i == 0)
						memberName += orientationMemberMp.get(memberList.get(i));
					else
						memberName += ","+ orientationMemberMp.get(memberList.get(i));
				}
				appraisalList.add(uF.showData(rs.getString("appraisal_details_id"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_name"),""));
				oreinted = rs.getString("oriented_type");
				appraisalList.add(uF.showData(orientationMp.get(rs.getString("oriented_type"))+ "&deg( " + memberName + " )", ""));
				appraisalList.add(uF.showData(rs.getString("self_ids"), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("level_id"),hmLevelMap), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("desig_id"), hmDesignation),""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("grade_id"),hmGradeMap), ""));
				appraisalList.add(uF.showData(hmFrequency.get(rs.getString("frequency")), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("wlocation_id"),hmLocation), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("department_id"), mpdepart),""));
				appraisalList.add(uF.showData(rs.getString("supervisor_id"), ""));
				appraisalList.add(uF.showData(rs.getString("peer_ids"), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("self_ids"),hmEmpName), ""));
				appraisalList.add(uF.showData(rs.getString("emp_status"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_type"),""));
				appraisalList.add(uF.showData(rs.getString("appraisal_description"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_instruction"), ""));
				appraisalList.add(uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalList.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalList.add(uF.showData(rs.getString("ceo_ids"), ""));
				appraisalList.add(uF.showData(rs.getString("hod_ids"), ""));
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
//			System.out.println("new Date ===> " + new Date());
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
//			System.out.println("new Date ===> " + new Date());
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

	private Map<String, String> getOrientationMember(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> orientationMemberMp = new HashMap<String, String>();

		try {

			pst = con
					.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				//orientationMemberMp.put(rs.getString("orientation_member_id"),rs.getString("member_name"));
				orientationMemberMp.put(rs.getString("member_id"), rs.getString("member_name"));
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
		return orientationMemberMp;
	}

	
	

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

			con = db.makeConnection(con);			
			pst = con
					.prepareStatement("select * from question_bank where is_add=true");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				sb.append("<option value=\"" + rs.getString("question_bank_id")
						+ "\">" + rs.getString("question_text").replace("'", "") + "</option>");
			}
			rs.close();
			pst.close();

			sb.append("<option value=\"0\">Add new Question</option>");

			request.setAttribute("option", sb.toString());

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

		if (strID != null && !strID.equals("")) {
			strID = strID.substring(1, strID.length()-1);
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

	public Map<String, String> getLevelMap(Connection con) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new HashMap<String, String>();
		try {
			pst = con.prepareStatement(selectLevel);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
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

	private Map<String, String> getLocationMap(Connection con) {
		Map<String, String> mplocation = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {

			pst = con.prepareStatement("select * from work_location_info");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
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

	
	public Map<String, String> getAppraisalQuestionMap(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, String> AppraisalQuestion = new HashMap<String, String>();

		try {

			con = db.makeConnection(con);
			pst = con
					.prepareStatement("select * from question_bank qb, appraisal_question_details aqd where qb.question_bank_id=aqd.question_id and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				AppraisalQuestion.put(rs.getString("question_bank_id"),
						rs.getString("question_text"));
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
		return AppraisalQuestion;
	}

	public Map<String, String> getAttributeMap() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, String> AppraisalQuestion = new HashMap<String, String>();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_attribute ");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				AppraisalQuestion.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
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
		return AppraisalQuestion;
	}

	
	private Map<String, String> getOrientationValue(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		Map<String, String> orientationMp = new HashMap<String, String>();
		Map<String, String> hmorientationMembers = new HashMap<String, String>();
		try {

			pst = con.prepareStatement("select * from apparisal_orientation");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				orientationMp.put(rs.getString("apparisal_orientation_id"), rs.getString("orientation_name"));
			}
			rs.close();
			pst.close();
			request.setAttribute("orientationMp", orientationMp);

			pst = con.prepareStatement("select * from orientation_member");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				hmorientationMembers.put(rs.getString("orientation_member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmorientationMembers", hmorientationMembers);

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
		return orientationMp;
	}

	
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

	public String getAppsystem() {
		return appsystem;
	}

	public void setAppsystem(String appsystem) {
		this.appsystem = appsystem;
	}

	public String getScoreType() {
		return scoreType;
	}

	public void setScoreType(String scoreType) {
		this.scoreType = scoreType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUID() {
		return UID;
	}

	public void setUID(String uID) {
		UID = uID;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	@Override
	public void setSession(Map session) {
		this.session = session;
	}
}
