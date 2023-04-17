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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddQuestionSystemOther  extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
//	Map session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;

	private List<FillAttribute> attributeList;
	private List<FillAnswerType> ansTypeList;
	List<FillFrequency> frequencyList;

	private String id; 
	private String oreinted;
	private String appsystem;
	private String scoreType;
	private String type;
	private String type1; 
	
	private String type20;
	private String type21;
	private String type22;
	private String type23;
	private String type24;
	
	private String type30;
	private String type31;
	private String type32;
	 
	private String type40;
	private String type41;
	private String type42;
	private String type43;
	private String UID;
	
	private String UID1;
	
	private String UID20;
	private String UID21;
	private String UID22;
	private String UID23;
	private String UID24;
	
	private String UID30;
	private String UID31;
	private String UID32;

	private String UID40;
	private String UID41;
	private String UID42;
	private String UID43;

	private String appLvlID;
	private String sectionID;
	private String appFreqId;
	private List<FillOrientation> orientationList;


	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/performance/AppraisalSummary.jsp");
		request.setAttribute(TITLE, "Report");
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return SUCCESS;
			
//		System.out.println("appLvlID ====> "+ appLvlID);
		String levelID=getSelfIDs(getId());
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

		if(type1 != null && !type1.equals("")){
			type = type1;
		}
		if(type20 != null && !type20.equals("")){
			type = type20;
		}
		if(type21 != null && !type21.equals("")){
			type = type21;
		}
		if(type22 != null && !type22.equals("")){
			type = type22;
		}
		if(type23 != null && !type23.equals("")){
			type = type23;
		}
		if(type24 != null && !type24.equals("")){
			type = type24;
		}
		if(type30 != null && !type30.equals("")){
			type = type30;
		}
		if(type31 != null && !type31.equals("")){
			type = type31;
		}
		if(type32 != null && !type32.equals("")){
			type = type32;
		}
		if(type40 != null && !type40.equals("")){
			type = type40;
		}
		if(type41 != null && !type41.equals("")){
			type = type41;
		}
		if(type42 != null && !type42.equals("")){
			type = type42;
		}
		if(type43 != null && !type43.equals("")){
			type = type43;
		}
		
		if(UID1 != null && !UID1.equals("")){
			UID = UID1;
		}
		if(UID20 != null && !UID20.equals("")){
			UID = UID20;
		}
		if(UID21 != null && !UID21.equals("")){
			UID = UID21;
		}
		if(UID22 != null && !UID22.equals("")){
			UID = UID22;
		}
		if(UID23 != null && !UID23.equals("")){
			UID = UID23;
		}
		if(UID24 != null && !UID24.equals("")){
			UID = UID24;
		}
		if(UID30 != null && !UID30.equals("")){
			UID = UID30;
		}
		if(UID31 != null && !UID31.equals("")){
			UID = UID31;
		}
		if(UID32 != null && !UID32.equals("")){
			UID = UID32;
		}
		if(UID40 != null && !UID40.equals("")){
			UID = UID40;
		}
		if(UID41 != null && !UID41.equals("")){
			UID = UID41;
		}
		if(UID41 != null && !UID41.equals("")){
			UID = UID41;
		}
		
		if(UID42 != null && !UID42.equals("")){
			UID = UID42;
		}
		if(UID43 != null && !UID43.equals("")){
			UID = UID43;
		}

	//	System.out.println("type==>"+getType()+"==>scoreType==>"+getScoreType()+"==>appsystem==>"+getAppsystem());
		if (submit != null && submit.equals("Save")) {
			if (appsystem != null && appsystem.equals("1")) {
				if (scoreType != null && scoreType.equals("1")) {
					if (type != null && type.equals("score")) {
//						System.out.println("in score ");
						insertScoreGoalObjectiveMeasureScore1();

					} else if (type != null && type.equals("goal")) {
//						System.out.println("in goal ");
						insertGoalObjectiveMeasureScore1();
						updateSectionAndSubsection();

					} else if (type != null && type.equals("objective")) {
//						System.out.println("in objective ");
						insertObjectiveMeasureScore1();
						updateSectionAndSubsection();

					} else if (type != null && type.equals("measure")) {
//						System.out.println("in measure ");
						insertMeasureScore1();
						updateSectionAndSubsection();

					} else if (type != null && type.equals("quest")) {
//						System.out.println("scoreType 1 in quest ");
						insertQuestionsScore1();
						updateSectionAndSubsection();

					}
				} else if (scoreType != null && scoreType.equals("3")) {
					if (type != null && type.equals("score")) {
//						System.out.println("in score ");
						insertScoreGoalMeasureScore3();
						updateSectionAndSubsection();

					} else if (type != null && type.equals("goal")) {
//						System.out.println("in goal ");
						insertGoalMeasureScore3();
						updateSectionAndSubsection();

					} else if (type != null && type.equals("measure")) {
//						System.out.println("in measure ");
						insertScoreMeasureScore3();
						updateSectionAndSubsection();

					} else if (type != null && type.equals("quest")) {
//						System.out.println("scoreType 3 in quest ");
						insertQuestionsScore3();
						updateSectionAndSubsection();

					}
				} else if (scoreType != null && scoreType.equals("2")) {
					if (type != null && type.equals("score")) {
//						System.out.println("in score ");
						insertScoreMeasureScore2();
						updateSectionAndSubsection();

					} else if (type != null && type.equals("measure")) {
//						System.out.println("in measure ");
						insertMeasureScore2();
						updateSectionAndSubsection();

					} else if (type != null && type.equals("quest")) {
//						System.out.println("scoreType 2 in quest ");
						insertQuestionsScore2();
						updateSectionAndSubsection();
					}
				}
			} else if (appsystem != null && appsystem.equals("2")) {
//				System.out.println("in other quest ");
				insertOtherQuestions();
				updateSectionAndSubsection();
			}

			return SUCCESS;
		}

			return LOAD;
		}

	
	private void updateSectionAndSubsection() {
		Connection con = null;
		PreparedStatement ps = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF  = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			ps = con.prepareStatement("update appraisal_level_details set added_by = ?, entry_date=? where appraisal_level_id = ?");
			ps.setInt(1, uF.parseToInt(strSessionEmpId));
			ps.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			ps.setInt(3, uF.parseToInt(getAppLvlID()));
			ps.executeUpdate();
			ps.close();
			
			ps = con.prepareStatement("update appraisal_main_level_details set added_by = ?, entry_date=? where main_level_id = ?");
			ps.setInt(1, uF.parseToInt(strSessionEmpId));
			ps.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			ps.setInt(3, uF.parseToInt(getSectionID()));
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(ps);
			db.closeConnection(con);
		}
		
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

	
	private void insertOtherQuestions() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();		

//		String hidequeid = request.getParameter("hidequeid");
		String weightage = request.getParameter("weightage");	
		String otherQueAnstype = request.getParameter("othrqueanstype");
		String ansType = request.getParameter("ansType");
		
//		System.out.println("otherQueAnstype  in java =========== > "+ otherQueAnstype);
//		System.out.println("ansType  in java =========== > "+ ansType);
		String question = request.getParameter("question");		
		String sectionattribute = request.getParameter("sectionattribute");
		
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
		
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select othe_question_type_id from appraisal_other_question_type_details where level_id =?");
			pst.setInt(1,uF.parseToInt(getUID()));
			rst = pst.executeQuery();
			String othe_question_type_id = null;
			while (rst.next()) {
				othe_question_type_id = rst.getString("othe_question_type_id");	
			}
			rst.close();
			pst.close();
			
			int question_id = 0;
			
			if (question.length() > 0) {
//				if (uF.parseToInt(hidequeid) == 0) {
					String[] correct = request.getParameterValues("correct"+ orientt);
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
					if(otherQueAnstype != null && !otherQueAnstype.equals("") && !otherQueAnstype.equals("0")) {
						pst.setInt(14, uF.parseToInt(otherQueAnstype));
					} else {
						pst.setInt(14, uF.parseToInt(ansType));
					}
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
					if(otherQueAnstype != null && !otherQueAnstype.equals("") && !otherQueAnstype.equals("0")){
						pst.setInt(8, uF.parseToInt(otherQueAnstype));
					}else{
						pst.setInt(8, uF.parseToInt(ansType));
					}
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
//				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,other_id,answer_type) values(?,?,?,?, ?,?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, uF.parseToInt(getUID()));
				pst.setInt(3, uF.parseToInt(sectionattribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, uF.parseToInt(getUID()));
				pst.setInt(7, uF.parseToInt(othe_question_type_id));
				if(otherQueAnstype != null && !otherQueAnstype.equals("") && !otherQueAnstype.equals("0")){
					pst.setInt(8, uF.parseToInt(otherQueAnstype));
				}else{
					pst.setInt(8, uF.parseToInt(ansType));
				}
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

//	private Map<String, String> getOrientMemberID(Connection con) {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Map<String, String> orientationMemberMp = new HashMap<String, String>();
//
//		try {
//
//			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				orientationMemberMp.put(rs.getString("member_name"), rs.getString("member_id"));
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return orientationMemberMp;
//	}

	private void insertQuestionsScore2() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();		

//		String hidequeid = request.getParameter("hidequeid");
		String weightage = request.getParameter("weightage");
		//String weightagenew = request.getParameter("weightagenew");
		String question = request.getParameter("question");
		String sectionattribute = request.getParameter("sectionattribute");

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
		try {
			
			int scorecard_id = 0;
			pst = con.prepareStatement("select scorecard_id from appraisal_measure_details where measure_id=?");
			pst.setInt(1, uF.parseToInt(getUID()));
			rst = pst.executeQuery();
			while (rst.next()) {
				scorecard_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int level_id=0;
			pst = con.prepareStatement("select level_id from appraisal_scorecard_details where scorecard_id=?");
			pst.setInt(1, scorecard_id);
			rst = pst.executeQuery();
			while (rst.next()) {
				 level_id= rst.getInt(1);
			}			
			rst.close();
			pst.close();
			
			int question_id = 0;
			String ansType = request.getParameter("queanstype");
			
			if (question.length() > 0) {
//				if (uF.parseToInt(hidequeid) == 0) {
					String[] correct = request.getParameterValues("correct"+ orientt);
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
//					System.out.println("insertQuestionsScore2 pst==> "+pst);
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
					while (rst.next()) { 
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
//				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id,answer_type) values(?,?,?,?, ?,?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, uF.parseToInt(getUID()));
				pst.setInt(3, uF.parseToInt(sectionattribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, level_id);
				pst.setInt(7, scorecard_id);
				pst.setInt(8, uF.parseToInt(ansType));
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
		
		String measuresSectionName = request.getParameter("measuresSectionName");
		String measuresDescription = request.getParameter("measuresDescription");
		String measureWeightage = request.getParameter("measureWeightage");

		//String hidequeid = request.getParameter("hidequeid");
		String weightage = request.getParameter("weightage");
		//String weightagenew = request.getParameter("weightagenew");
		String question = request.getParameter("question");
		String sectionattribute = request.getParameter("sectionattribute");
		
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
//		System.out.println("getUID() ===> "+getUID());
		try {
			int level_id=0;
						
			pst = con.prepareStatement("select level_id from appraisal_scorecard_details where scorecard_id=?");
			pst.setInt(1, uF.parseToInt(getUID()));
			rst = pst.executeQuery();
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
			while (rst.next()) {
				measure_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int question_id = 0;
			String ansType = request.getParameter("queanstype");
			
			if (question.length() > 0) {
//				if (uF.parseToInt(hidequeid) == 0) {
					String[] correct = request.getParameterValues("correct"+ orientt);
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
//					System.out.println("insertMeasureScore2 pst==> "+pst);
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
//				}
				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id,answer_type) values(?,?,?,?, ?,?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, measure_id);
				pst.setInt(3, uF.parseToInt(sectionattribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, level_id);
				pst.setInt(7, uF.parseToInt(getUID()));
				pst.setInt(8, uF.parseToInt(ansType));
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

		String scoreSectionName = request.getParameter("scoreSectionName");
		String scoreCardDescription = request.getParameter("scoreCardDescription");
		String scoreCardWeightage = request.getParameter("scoreCardWeightage");

		String measuresSectionName = request.getParameter("measuresSectionName");
		String measuresDescription = request.getParameter("measuresDescription");
		String measureWeightage = request.getParameter("measureWeightage");

//		String hidequeid = request.getParameter("hidequeid");
		String weightage = request.getParameter("weightage");
		//String weightagenew = request.getParameter("weightagenew");
		String question = request.getParameter("question");
		String sectionattribute = request.getParameter("sectionattribute");

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
		try {
			pst = con.prepareStatement("insert into appraisal_scorecard_details(scorecard_section_name,scorecard_description,"
							+ "scorecard_weightage,level_id,appraisal_id,appraisal_attribute)values(?,?,?,?,?,?)");
			pst.setString(1, scoreSectionName);
			pst.setString(2, scoreCardDescription);
			pst.setString(3, scoreCardWeightage);
			pst.setInt(4, uF.parseToInt(getUID()));
			pst.setInt(5, uF.parseToInt(id));
			pst.setInt(6, uF.parseToInt(sectionattribute));
			pst.executeUpdate();
			pst.close();
			
			int scorecard_id = 0;
			pst = con.prepareStatement("select max(scorecard_id) from appraisal_scorecard_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				scorecard_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,scorecard_id,appraisal_id,weightage)"
							+ "values(?,?,?,?,?)");
			pst.setString(1, measuresSectionName);
			pst.setString(2, measuresDescription);
			pst.setInt(3, scorecard_id);
			pst.setInt(4, uF.parseToInt(id));
			pst.setString(5, measureWeightage);
			pst.execute();
			pst.close();
			
			int measure_id = 0;
			pst = con.prepareStatement("select max(measure_id) from appraisal_measure_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				measure_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int question_id = 0;
			String ansType = request.getParameter("queanstype");
			
			if (question.length() > 0) {
//				if (uF.parseToInt(hidequeid) == 0) {
					String[] correct = request.getParameterValues("correct"+ orientt);
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
//					System.out.println("insertScoreMeasureScore2 pst==> "+pst);
					pst.execute();
					pst.close();*/

					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
//				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id,answer_type) values(?,?,?,?, ?,?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, measure_id);
				pst.setInt(3, uF.parseToInt(sectionattribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, uF.parseToInt(getUID()));
				pst.setInt(7, scorecard_id);
				pst.setInt(8, uF.parseToInt(ansType));
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

		//String hidequeid = request.getParameter("hidequeid");
		String weightage = request.getParameter("weightage");
		//String weightagenew = request.getParameter("weightagenew");
		String question = request.getParameter("question");
		String sectionattribute = request.getParameter("sectionattribute");

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
		try {
			int goal_id = 0;
			pst = con.prepareStatement("select goal_id from appraisal_measure_details where measure_id=?");
			pst.setInt(1, uF.parseToInt(getUID()));
			rst = pst.executeQuery();
//			System.out.println("pst ===> "+pst);
			while (rst.next()) {
				goal_id = rst.getInt(1);
			}		
			rst.close();
			pst.close();
			
			int scorecard_id=0;
			pst = con.prepareStatement("select scorecard_id from appraisal_goal_details where goal_id=?");
			pst.setInt(1, goal_id);
			rst = pst.executeQuery();
			while (rst.next()) {
				scorecard_id= rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int level_id=0;
			pst = con.prepareStatement("select level_id from appraisal_scorecard_details where scorecard_id=?");
			pst.setInt(1, scorecard_id);
			rst = pst.executeQuery();
			while (rst.next()) {
				 level_id= rst.getInt(1);
			}			
			rst.close();
			pst.close();
			
			int question_id = 0;
			String ansType = request.getParameter("queanstype");
			
			if (question.length() > 0) {
//				if (uF.parseToInt(hidequeid) == 0) {
					String[] correct = request.getParameterValues("correct"+ orientt);
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
//					System.out.println("insertQuestionsScore3 pst==> "+pst);
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
//				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id,answer_type) values(?,?,?,?, ?,?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, uF.parseToInt(getUID()));
				pst.setInt(3, uF.parseToInt(sectionattribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(14, level_id);
				pst.setInt(15,scorecard_id);
				pst.setInt(16, uF.parseToInt(ansType));
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

		String goalSectionName = request.getParameter("goalSectionName");
		String goalDescription = request.getParameter("goalDescription");
		String goalWeightage = request.getParameter("goalWeightage");
	
		String measuresSectionName = request.getParameter("measuresSectionName");
		String measuresDescription = request.getParameter("measuresDescription");
		String measureWeightage = request.getParameter("measureWeightage");

		//String hidequeid = request.getParameter("hidequeid");
		String weightage = request.getParameter("weightage");
		//String weightagenew = request.getParameter("weightagenew");
		String question = request.getParameter("question");
		String sectionattribute = request.getParameter("sectionattribute");

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
		try {
			int level_id=0;
			pst = con.prepareStatement("select level_id from appraisal_scorecard_details where scorecard_id=?");
			pst.setInt(1, uF.parseToInt(getUID()));
			rst = pst.executeQuery();
			while (rst.next()) {
				 level_id= rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("insert into appraisal_goal_details(goal_section_name,goal_description,"
							+ "goal_weightage,scorecard_id,appraisal_id)values(?,?,?,?,?)");
			pst.setString(1, goalSectionName);
			pst.setString(2, goalDescription);
			pst.setString(3, goalWeightage);
			pst.setInt(4, uF.parseToInt(getUID()));
			pst.setInt(5, uF.parseToInt(id));
			pst.execute();
			pst.close();
			
			int goal_id = 0;
			pst = con.prepareStatement("select max(goal_id) from appraisal_goal_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				goal_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,goal_id,appraisal_id,weightage)"
							+ "values(?,?,?,?,?)");
			pst.setString(1, measuresSectionName);
			pst.setString(2, measuresDescription);
			pst.setInt(3, goal_id);
			pst.setInt(4, uF.parseToInt(id));
			pst.setString(5, measureWeightage);
			pst.execute();
			pst.close();
			
			int measure_id = 0;
			pst = con.prepareStatement("select max(measure_id) from appraisal_measure_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				measure_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			int question_id = 0;
			String ansType = request.getParameter("queanstype");
			
			if (question.length() > 0) {
//				if (uF.parseToInt(hidequeid) == 0) {
					String[] correct = request.getParameterValues("correct"+ orientt);
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
//					System.out.println("insertGoalMeasureScore3 pst==> "+pst);
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
//				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id,answer_type) values(?,?,?,?, ?,?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, measure_id);
				pst.setInt(3, uF.parseToInt(sectionattribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, level_id);
				pst.setInt(7,  uF.parseToInt(getUID()));
				pst.setInt(8, uF.parseToInt(ansType));
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
		
		String measuresSectionName = request
				.getParameter("measuresSectionName");
		String measuresDescription = request
				.getParameter("measuresDescription");
		String measureWeightage = request.getParameter("measureWeightage");

		//String hidequeid = request.getParameter("hidequeid");
		String weightage = request.getParameter("weightage");
		//String weightagenew = request.getParameter("weightagenew");
		String question = request.getParameter("question");
		String sectionattribute = request.getParameter("sectionattribute");

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

//		Map<String, String> orientationMemberMp = getOrientationMember();
//		Map<String, String> hmOrientMemberID = getOrientMemberID();

		
		try {
			con = db.makeConnection(con);
			int level_id=0;
			int scorecard_id=0;
			pst = con.prepareStatement("select scorecard_id from appraisal_goal_details where goal_id=?");
			pst.setInt(1, uF.parseToInt(getUID()));
			rst = pst.executeQuery();
			while (rst.next()) {
				scorecard_id= rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select level_id from appraisal_scorecard_details where scorecard_id=?");
			pst.setInt(1, scorecard_id);
			rst = pst.executeQuery();
			while (rst.next()) {
				 level_id= rst.getInt(1);
			}			
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,goal_id,appraisal_id,weightage)"
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
			while (rst.next()) {
				measure_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int question_id = 0;
			String ansType = request.getParameter("queanstype");
			
			if (question.length() > 0) {
//				if (uF.parseToInt(hidequeid) == 0) {
					String[] correct = request.getParameterValues("correct"+ orientt);
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
//					System.out.println("insertScoreMeasureScore3 pst==> "+pst);
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
//				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id,answer_type) values(?,?,?,?, ?,?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, measure_id);
				pst.setInt(3, uF.parseToInt(sectionattribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, level_id);
				pst.setInt(7, scorecard_id);
				pst.setInt(8, uF.parseToInt(ansType));
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

		String scoreSectionName = request.getParameter("scoreSectionName");
		String scoreCardDescription = request.getParameter("scoreCardDescription");
		String scoreCardWeightage = request.getParameter("scoreCardWeightage");

		String goalSectionName = request.getParameter("goalSectionName");
		String goalDescription = request.getParameter("goalDescription");
		String goalWeightage = request.getParameter("goalWeightage");
	

		String measuresSectionName = request.getParameter("measuresSectionName");
		String measuresDescription = request.getParameter("measuresDescription");
		String measureWeightage = request.getParameter("measureWeightage");

//		String hidequeid = request.getParameter("hidequeid");
		String weightage = request.getParameter("weightage");
		//String weightagenew = request.getParameter("weightagenew");
		String question = request.getParameter("question");
		String sectionattribute = request.getParameter("sectionattribute");
		
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

		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("insert into appraisal_scorecard_details(scorecard_section_name,scorecard_description,"
							+ "scorecard_weightage,level_id,appraisal_id,appraisal_attribute)values(?,?,?,?,?,?)");
			pst.setString(1, scoreSectionName);
			pst.setString(2, scoreCardDescription);
			pst.setString(3, scoreCardWeightage);
			pst.setInt(4, uF.parseToInt(getUID()));
			pst.setInt(5, uF.parseToInt(id));
			pst.setInt(6, uF.parseToInt(sectionattribute));
			pst.execute();
			pst.close();
			
			int scorecard_id = 0;
			pst = con.prepareStatement("select max(scorecard_id) from appraisal_scorecard_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				scorecard_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("insert into appraisal_goal_details(goal_section_name,goal_description,"
							+ "goal_weightage,scorecard_id,appraisal_id)values(?,?,?,?,?)");
			pst.setString(1, goalSectionName);
			pst.setString(2, goalDescription);
			pst.setString(3, goalWeightage);
			pst.setInt(4, scorecard_id);
			pst.setInt(5, uF.parseToInt(id));
			pst.execute();
			pst.close();
			
			int goal_id = 0;
			pst = con.prepareStatement("select max(goal_id) from appraisal_goal_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				goal_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,goal_id,appraisal_id,weightage)"
							+ "values(?,?,?,?,?)");
			pst.setString(1, measuresSectionName);
			pst.setString(2, measuresDescription);
			pst.setInt(3, goal_id);
			pst.setInt(4, uF.parseToInt(id));
			pst.setString(5, measureWeightage);
			pst.execute();
			pst.close();
			
			int measure_id = 0;
			pst = con.prepareStatement("select max(measure_id) from appraisal_measure_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				measure_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			int question_id = 0;
			String ansType = request.getParameter("queanstype");
			
			if (question.length() > 0) {
//				if (uF.parseToInt(hidequeid) == 0) {
					String[] correct = request.getParameterValues("correct"+ orientt);
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
//					System.out.println("insertScoreGoalMeasureScore3 pst==> "+pst);
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
//				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id,answer_type) values(?,?,?,?, ?,?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, measure_id);
				pst.setInt(3, uF.parseToInt(sectionattribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, uF.parseToInt(getUID()));
				pst.setInt(7, scorecard_id);
				pst.setInt(8, uF.parseToInt(ansType));
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

		//String hidequeid = request.getParameter("hidequeid");
		String weightage = request.getParameter("weightage");
		//String weightagenew = request.getParameter("weightagenew");
		String question = request.getParameter("question");
		String sectionattribute = request.getParameter("sectionattribute");

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
		String questionCnt = request.getParameter("questionCnt");
		try {
			con = db.makeConnection(con);
				
			int objective_id = 0;
			pst = con.prepareStatement("select objective_id from appraisal_measure_details where measure_id=?");
			pst.setInt(1, uF.parseToInt(getUID()));
			rst = pst.executeQuery();
			while (rst.next()) {
				objective_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int goal_id=0;
			pst = con.prepareStatement("select goal_id from appraisal_objective_details where objective_id=?");
			pst.setInt(1, objective_id);
			rst = pst.executeQuery();
			while (rst.next()) {
				goal_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int scorecard_id=0;
			pst = con.prepareStatement("select scorecard_id from appraisal_goal_details where goal_id=?");
			pst.setInt(1, goal_id);
			rst = pst.executeQuery();
			while (rst.next()) {
				scorecard_id= rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int level_id=0;
			pst = con.prepareStatement("select level_id from appraisal_scorecard_details where scorecard_id=?");
			pst.setInt(1, scorecard_id);
			rst = pst.executeQuery();
			while (rst.next()) {
				 level_id= rst.getInt(1);
			}			
			rst.close();
			pst.close();
			
			int question_id = 0;
			String ansType = request.getParameter("queanstype");
			
			if (question.length() > 0) {
//				if (uF.parseToInt(hidequeid) == 0) {
					String[] correct = request.getParameterValues("correct"+ orientt);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}

					String[] correct1 = request.getParameterValues("correct"+ questionCnt);
					StringBuilder option1= new StringBuilder();

					for (int ab = 0; correct1 != null && ab < correct1.length; ab++) {
						option1.append(correct[ab] + ",");
					}
					/*System.out.println("orientt==>"+orientt);
			        System.out.println("questionCnt==>"+questionCnt+"option==>"+option.toString());*/
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
//					System.out.println("insertQuestionsScore1 pst==> "+pst);
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
//				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id,answer_type) values(?,?,?,?,?,?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, uF.parseToInt(getUID()));
				pst.setInt(3, uF.parseToInt(sectionattribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, level_id);
				pst.setInt(7, scorecard_id);
				pst.setInt(8, uF.parseToInt(ansType));
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
		String measuresSectionName = request.getParameter("measuresSectionName");
		String measuresDescription = request.getParameter("measuresDescription");
		String measureWeightage = request.getParameter("measureWeightage");

		//String hidequeid = request.getParameter("hidequeid");
		String weightage = request.getParameter("weightage");
		//String weightagenew = request.getParameter("weightagenew");
		String question = request.getParameter("question");
		String sectionattribute = request.getParameter("sectionattribute");
		
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

		try {
			con = db.makeConnection(con);
			
			int goal_id=0;
			pst = con.prepareStatement("select goal_id from appraisal_objective_details where objective_id=?");
			pst.setInt(1, uF.parseToInt(getUID()));
			rst = pst.executeQuery();
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
			while (rst.next()) {
				scorecard_id= rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select level_id from appraisal_scorecard_details where scorecard_id=?");
			pst.setInt(1, scorecard_id);
			rst = pst.executeQuery();
			while (rst.next()) {
				 level_id= rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,objective_id,appraisal_id,weightage)"
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
			while (rst.next()) {
				measure_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int question_id = 0;
			String ansType = request.getParameter("queanstype");
			
			if (question.length() > 0) {
//				if (uF.parseToInt(hidequeid) == 0) {
					String[] correct = request.getParameterValues("correct"+ orientt);
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
//					System.out.println("insertMeasureScore1 pst==> "+pst);
					pst.setInt(8, uF.parseToInt(ansType));
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
//				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id,answer_type) values(?,?,?,?, ?,?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, measure_id);
				pst.setInt(3, uF.parseToInt(sectionattribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(14, level_id);
				pst.setInt(15,scorecard_id);
				pst.setInt(16, uF.parseToInt(ansType));
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

		//String hidequeid = request.getParameter("hidequeid");
		String weightage = request.getParameter("weightage");
		//String weightagenew = request.getParameter("weightagenew");
		String question = request.getParameter("question");
		String sectionattribute = request.getParameter("sectionattribute");

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

//		Map<String, String> orientationMemberMp = getOrientationMember(con);
//		Map<String, String> hmOrientMemberID = getOrientMemberID(con);

		try {
			con = db.makeConnection(con);
			
			int level_id=0;
			int scorecard_id=0;
			pst = con.prepareStatement("select scorecard_id from appraisal_goal_details where goal_id=?");
			pst.setInt(1, uF.parseToInt(getUID()));
			rst = pst.executeQuery();
			while (rst.next()) {
				scorecard_id= rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select level_id from appraisal_scorecard_details where scorecard_id=?");
			pst.setInt(1, scorecard_id);
			rst = pst.executeQuery();
			while (rst.next()) {
				 level_id= rst.getInt(1);
			}			
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("insert into appraisal_objective_details(objective_section_name,objective_description,"
							+ "objective_weightage,goal_id,appraisal_id)values(?,?,?,?,?)");
			pst.setString(1, objectiveSectionName);
			pst.setString(2, objectiveDescription);
			pst.setString(3, objectiveWeightage);
			pst.setInt(4, uF.parseToInt(getUID()));
			pst.setInt(5, uF.parseToInt(id));
			pst.execute();
			pst.close();
			
			int objective_id = 0;
			pst = con.prepareStatement("select max(objective_id) from appraisal_objective_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				objective_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,objective_id,appraisal_id,weightage)"
							+ "values(?,?,?,?,?)");
			pst.setString(1, measuresSectionName);
			pst.setString(2, measuresDescription);
			pst.setInt(3, objective_id);
			pst.setInt(4, uF.parseToInt(id));
			pst.setString(5, measureWeightage);
			pst.execute();
			pst.close();
			
			int measure_id = 0;
			pst = con.prepareStatement("select max(measure_id) from appraisal_measure_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				measure_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			int question_id = 0;
			String ansType = request.getParameter("queanstype");
			
			if (question.length() > 0) {
//				if (uF.parseToInt(hidequeid) == 0) {
					String[] correct = request.getParameterValues("correct"+ orientt);
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
//					System.out.println("insertObjectiveMeasureScore1 pst==> "+pst);
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
//				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id,answer_type) values(?,?,?,?, ?,?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, measure_id);
				pst.setInt(3, uF.parseToInt(sectionattribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, level_id);
				pst.setInt(7, scorecard_id);
				pst.setInt(8, uF.parseToInt(ansType));
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

		//String hidequeid = request.getParameter("hidequeid");
		String weightage = request.getParameter("weightage");
		//String weightagenew = request.getParameter("weightagenew");
		String question = request.getParameter("question");
		String sectionattribute = request.getParameter("sectionattribute");
		
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

		try {
			con = db.makeConnection(con);
			
			int level_id=0;
			pst = con.prepareStatement("select level_id from appraisal_scorecard_details where scorecard_id=?");
			pst.setInt(1, uF.parseToInt(getUID()));
			rst = pst.executeQuery();
			while (rst.next()) {
				 level_id= rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("insert into appraisal_goal_details(goal_section_name,goal_description,"
							+ "goal_weightage,scorecard_id,appraisal_id)values(?,?,?,?,?)");
			pst.setString(1, goalSectionName);
			pst.setString(2, goalDescription);
			pst.setString(3, goalWeightage);
			pst.setInt(4, uF.parseToInt(getUID()));
			pst.setInt(5, uF.parseToInt(id));
			pst.execute();
			pst.close();
			
			int goal_id = 0;
			pst = con.prepareStatement("select max(goal_id) from appraisal_goal_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				goal_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			pst = con.prepareStatement("insert into appraisal_objective_details(objective_section_name,objective_description,"
							+ "objective_weightage,goal_id,appraisal_id)values(?,?,?,?,?)");
			pst.setString(1, objectiveSectionName);
			pst.setString(2, objectiveDescription);
			pst.setString(3, objectiveWeightage);
			pst.setInt(4, goal_id);
			pst.setInt(5, uF.parseToInt(id));
			pst.execute();
			pst.close();
			
			int objective_id = 0;
			pst = con.prepareStatement("select max(objective_id) from appraisal_objective_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				objective_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,objective_id,appraisal_id,weightage)"
							+ "values(?,?,?,?,?)");
			pst.setString(1, measuresSectionName);
			pst.setString(2, measuresDescription);
			pst.setInt(3, objective_id);
			pst.setInt(4, uF.parseToInt(id));
			pst.setString(5, measureWeightage);
			pst.execute();
			pst.close();
			
			int measure_id = 0;
			pst = con.prepareStatement("select max(measure_id) from appraisal_measure_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				measure_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			int question_id = 0;
			String ansType = request.getParameter("queanstype");
			
			if (question.length() > 0) {
//				if (uF.parseToInt(hidequeid) == 0) {
					String[] correct = request.getParameterValues("correct"+ orientt);
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
//					System.out.println("insertGoalObjectiveMeasureScore1 pst1==>"+pst);
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
//				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id,answer_type) values(?,?,?,?, ?,?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, measure_id);
				pst.setInt(3, uF.parseToInt(sectionattribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, level_id);
				pst.setInt(7, uF.parseToInt(getUID()));
				pst.setInt(8, uF.parseToInt(ansType));
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

		String scoreSectionName = request.getParameter("scoreSectionName");
		String scoreCardDescription = request.getParameter("scoreCardDescription");
		String scoreCardWeightage = request.getParameter("scoreCardWeightage");

		String goalSectionName = request.getParameter("goalSectionName");
		String goalDescription = request.getParameter("goalDescription");
		String goalWeightage = request.getParameter("goalWeightage");

		String objectiveSectionName = request.getParameter("objectiveSectionName");
		String objectiveDescription = request.getParameter("objectiveDescription");
		String objectiveWeightage = request.getParameter("objectiveWeightage");

		String measuresSectionName = request.getParameter("measuresSectionName");
		String measuresDescription = request.getParameter("measuresDescription");
		String measureWeightage = request.getParameter("measureWeightage");

		//String hidequeid = request.getParameter("hidequeid");
		String weightage = request.getParameter("weightage");
		//String weightagenew = request.getParameter("weightagenew");
		String question = request.getParameter("question");
		String sectionattribute = request.getParameter("sectionattribute");

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
		String questionCnt = request.getParameter("questionCnt");
		String[] correct = request.getParameterValues("correct" + orientt);
		StringBuilder option = new StringBuilder();

		for (int ab = 0; correct != null && ab < correct.length; ab++) {
			option.append(correct[ab] + ",");
		}
		
//		System.out.println("orientt==>"+orientt);
//        System.out.println("questionCnt==>"+questionCnt+"option==>"+option.toString());
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("insert into appraisal_scorecard_details(scorecard_section_name,scorecard_description,"
							+ "scorecard_weightage,level_id,appraisal_id,appraisal_attribute)values(?,?,?,?,?,?)");
			pst.setString(1, scoreSectionName);
			pst.setString(2, scoreCardDescription);
			pst.setString(3, scoreCardWeightage);
			pst.setInt(4, uF.parseToInt(getUID()));
			pst.setInt(5, uF.parseToInt(id));
			pst.setInt(6, uF.parseToInt(sectionattribute));
			pst.execute();
			pst.close();
			
			int scorecard_id = 0;
			pst = con.prepareStatement("select max(scorecard_id) from appraisal_scorecard_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				scorecard_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			pst = con.prepareStatement("insert into appraisal_goal_details(goal_section_name,goal_description,"
							+ "goal_weightage,scorecard_id,appraisal_id)values(?,?,?,?,?)");
			pst.setString(1, goalSectionName);
			pst.setString(2, goalDescription);
			pst.setString(3, goalWeightage);
			pst.setInt(4, scorecard_id);
			pst.setInt(5, uF.parseToInt(id));
			pst.execute();
			pst.close();

			int goal_id = 0;
			pst = con.prepareStatement("select max(goal_id) from appraisal_goal_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				goal_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			pst = con.prepareStatement("insert into appraisal_objective_details(objective_section_name,objective_description,"
							+ "objective_weightage,goal_id,appraisal_id)values(?,?,?,?,?)");
			pst.setString(1, objectiveSectionName);
			pst.setString(2, objectiveDescription);
			pst.setString(3, objectiveWeightage);
			pst.setInt(4, goal_id);
			pst.setInt(5, uF.parseToInt(id));
			pst.execute();
			pst.close();
			
			int objective_id = 0;
			pst = con.prepareStatement("select max(objective_id) from appraisal_objective_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				objective_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,objective_id,appraisal_id,weightage)"
							+ "values(?,?,?,?,?)");
			pst.setString(1, measuresSectionName);
			pst.setString(2, measuresDescription);
			pst.setInt(3, objective_id);
			pst.setInt(4, uF.parseToInt(id));
			pst.setString(5, measureWeightage);
			pst.execute();
			pst.close();
			
			int measure_id = 0;
			pst = con.prepareStatement("select max(measure_id) from appraisal_measure_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				measure_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			int question_id = 0;
			String ansType = request.getParameter("queanstype");
			
			if (question.length() > 0) {
//				if (uF.parseToInt(hidequeid) == 0) {
					
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
//					System.out.println("insert ques pst==>"+pst);
					pst.execute();
					pst.close();*/

					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
//				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage,appraisal_id," +
					"appraisal_level_id,scorecard_id,answer_type) values(?,?,?,?, ?,?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, measure_id);
				pst.setInt(3, uF.parseToInt(sectionattribute));
				pst.setDouble(4, uF.parseToDouble(weightage));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, uF.parseToInt(getUID()));
				pst.setInt(7, scorecard_id);
				pst.setInt(8, uF.parseToInt(ansType));
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
			while (rs.next()) {
				hmFrequency.put(rs.getString("appraisal_frequency_id"),
						rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();

			List<String> appraisalList = new ArrayList<String>();
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
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

			pst = con.prepareStatement("select member_name from orientation_details od,orientation_member om  where orientation_id=? and od.member_id=orientation_member_id");
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

	private Map<String, String> getOrientationMember(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> orientationMemberMp = new HashMap<String, String>();

		try {

			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				//orientationMemberMp.put(rs.getString("orientation_member_id"),rs.getString("member_name"));
				orientationMemberMp.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
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

		if (strID != null && !strID.equals("") && !strID.isEmpty() && strID.length() >1) {
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

	public Map<String, String> getAppraisalQuestionMap(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, String> AppraisalQuestion = new HashMap<String, String>();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from question_bank qb, appraisal_question_details aqd where qb.question_bank_id=aqd.question_id and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				AppraisalQuestion.put(rs.getString("question_bank_id"), rs.getString("question_text"));
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
			while (rs.next()) {
				orientationMp.put(rs.getString("apparisal_orientation_id"),
						rs.getString("orientation_name"));
			}
			rs.close();
			pst.close();
			request.setAttribute("orientationMp", orientationMp);

			pst = con.prepareStatement("select * from orientation_member");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmorientationMembers.put(rs.getString("orientation_member_id"),
						rs.getString("member_name"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmorientationMembers", hmorientationMembers);

		} catch (Exception e) {
			e.printStackTrace();
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

	public String getType1() {
		return type1;
	}

	public void setType1(String type1) {
		this.type1 = type1;
	}

	public String getType30() {
		return type30;
	}

	public void setType30(String type30) {
		this.type30 = type30;
	}

	public String getType31() {
		return type31;
	}

	public void setType31(String type31) {
		this.type31 = type31;
	}

	public String getType20() {
		return type20;
	}

	public void setType20(String type20) {
		this.type20 = type20;
	}

	public String getType21() {
		return type21;
	}

	public void setType21(String type21) {
		this.type21 = type21;
	}

	public String getType22() {
		return type22;
	}

	public void setType22(String type22) {
		this.type22 = type22;
	}

	public String getType23() {
		return type23;
	}

	public void setType23(String type23) {
		this.type23 = type23;
	}

	public String getType24() {
		return type24;
	}

	public void setType24(String type24) {
		this.type24 = type24;
	}

	public String getType32() {
		return type32;
	}

	public void setType32(String type32) {
		this.type32 = type32;
	}

	public String getType40() {
		return type40;
	}

	public void setType40(String type40) {
		this.type40 = type40;
	}

	public String getType41() {
		return type41;
	}

	public void setType41(String type41) {
		this.type41 = type41;
	}

	public String getType42() {
		return type42;
	}

	public void setType42(String type42) {
		this.type42 = type42;
	}

	public String getType43() {
		return type43;
	}

	public void setType43(String type43) {
		this.type43 = type43;
	}

	public String getUID1() {
		return UID1;
	}

	public void setUID1(String uID1) {
		UID1 = uID1;
	}

	public String getUID20() {
		return UID20;
	}

	public void setUID20(String uID20) {
		UID20 = uID20;
	}

	public String getUID21() {
		return UID21;
	}

	public void setUID21(String uID21) {
		UID21 = uID21;
	}

	public String getUID22() {
		return UID22;
	}

	public void setUID22(String uID22) {
		UID22 = uID22;
	}

	public String getUID23() {
		return UID23;
	}

	public void setUID23(String uID23) {
		UID23 = uID23;
	}

	public String getUID24() {
		return UID24;
	}

	public void setUID24(String uID24) {
		UID24 = uID24;
	}

	public String getUID30() {
		return UID30;
	}

	public void setUID30(String uID30) {
		UID30 = uID30;
	}

	public String getUID31() {
		return UID31;
	}

	public void setUID31(String uID31) {
		UID31 = uID31;
	}

	public String getUID32() {
		return UID32;
	}

	public void setUID32(String uID32) {
		UID32 = uID32;
	}

	public String getUID40() {
		return UID40;
	}

	public void setUID40(String uID40) {
		UID40 = uID40;
	}

	public String getUID41() {
		return UID41;
	}

	public void setUID41(String uID41) {
		UID41 = uID41;
	}

	public String getUID42() {
		return UID42;
	}

	public void setUID42(String uID42) {
		UID42 = uID42;
	}

	public String getUID43() {
		return UID43;
	}

	public void setUID43(String uID43) {
		UID43 = uID43;
	}

	public String getAppLvlID() {
		return appLvlID;
	}

	public void setAppLvlID(String appLvlID) {
		this.appLvlID = appLvlID;
	}

	public String getSectionID() {
		return sectionID;
	}

	public void setSectionID(String sectionID) {
		this.sectionID = sectionID;
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

}
