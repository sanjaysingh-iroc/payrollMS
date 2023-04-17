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

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class GetEditQuestionDetails implements ServletRequestAware, SessionAware, IStatements {
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
	private String editID;
	private String type;

	private String attributevalue;
	private String attribute_id;

	private List<FillOrientation> orientationList;
	
	private String strLevel;
	private String strDesignationUpdate;
	private String empGrade;
	private String userlocation;

	private String frequencyValue;
	private String quediv;
	private String othrquetype;
	private String queno;
	private String ansid;
	private String selectanstype;
	private String totWeightage;
	
	private List<String> empvalue = new ArrayList<String>();
	private List<String> departmentvalue = new ArrayList<String>();
	private List<String> wlocationvalue = new ArrayList<String>();
	private List<String> gradevalue = new ArrayList<String>();
	private List<String> desigvalue = new ArrayList<String>();
	private List<String> levelvalue = new ArrayList<String>();
	private String orientedValue;

	private String appraiselName;
	private String appraisal_description;
	private String employee;
	private String strDepart;
	private String strWlocation;
	private String emp_status;

	private String appraisalType;
	private String appraiseeList;

	private String frequency;
	private String from;
	private String to;

	private String startFrom;
	private String endTo;
	private String weekday;
	
	private String appraisal_typeValue;
	
	private String annualDay;
	private String annualMonth;
	private String day;
	private String monthday;
	private String month;
	
	private String appraisal_instruction;
	private String strOrg;
	private String sectionID;
	private String subsectionID;
	private String appFreqId;
	private List<FillOrganisation> organisationList;
	private String fromPage;
	public String execute() {
		strUserType = (String) session.get(USERTYPE);
		strSessionEmpId = (String) session.get(EMPID);

		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return "login";

		request.setAttribute(PAGE, "/jsp/performance/GetEditQuestionDetails.jsp");
		request.setAttribute(TITLE, "Edit Question");

//		System.out.println("ansType==>"+getSelectanstype());
		String levelID=getSelfIDs(getId());
//		System.out.println("levelID====>"+levelID);
		if (levelID != null && levelID.length()>0){
			attributeList = new FillAttribute(request).fillElementAttribute(levelID);
		}else{				
			attributeList = new FillAttribute(request).fillElementAttribute(null);
		}
		ansTypeList = new FillAnswerType(request).fillAnswerType();

		getAppraisalDetail();
		//getOrientationValue(uF.parseToInt(getOreinted()));
		getattribute();
		getOtherAnsType();
		getAppraisalQuestionList();
//		getMainLevelData1();
//		System.out.println("appsystem==>"+getAppsystem()+"==>scoreType==>"+getScoreType()+"==>type==>"+getType());
		String submit = request.getParameter("submit");
		if (appsystem != null && appsystem.equals("1")) {
			if (scoreType != null && scoreType.equals("1")) {
				if (type != null && type.equals("score")) {
					if (submit != null && submit.equals("Save")) {
						editScoreCard();
						editGoal();
						editObjective();
						editMeasure();
						editScoreQuestions();
						updateSectionAndSubsection();
						return "success";
					} else {
						getScoreObjectiveMeasureGoal();
						return "success";
					}
				} else if (type != null && type.equals("goal")) {
					if (submit != null && submit.equals("Save")) {
						editGoal();
						editObjective();
						editMeasure();
						editQuestions();
						updateSectionAndSubsection();
						return "success";
					} else {
						getObjectiveMeasureGoal();
						return "success";
					}
				} else if (type != null && type.equals("objective")) {
					if (submit != null && submit.equals("Save")) {
						editObjective();
						editMeasure();
						editQuestions();
						updateSectionAndSubsection();
						return "success";
					} else {
						getObjectiveMeasure();
						return "success";
					}
				} else if (type != null && type.equals("measure")) {
					if (submit != null && submit.equals("Save")) {
						editMeasure();
						editQuestions();
						updateSectionAndSubsection();
						return "success"; 
					} else {
						getMeasure();
						return "success";
					}
				} else if (type != null && type.equals("quest")) {
					if (submit != null && submit.equals("Save")) {
						editQuestions();
						updateSectionAndSubsection();
						return "success";
					} else {
						getQuestions();
						return "success";
					}
				}
			} else if (scoreType != null && scoreType.equals("3")) {
				if (type != null && type.equals("score")) {
					if (submit != null && submit.equals("Save")) {
						editScoreCard();
						editGoal();
						editMeasure();
						editScoreQuestions();
						updateSectionAndSubsection();
						return "success";
					} else {
						getScoreMeasureGoal();
						return "success";
					}
				} else if (type != null && type.equals("goal")) {
					if (submit != null && submit.equals("Save")) {
						editGoal();
						editMeasure();
						editQuestions();
						updateSectionAndSubsection();
						return "success";
					} else {
						getMeasureGoal();
						return "success";
					}
				} else if (type != null && type.equals("measure")) {
					if (submit != null && submit.equals("Save")) {
						editMeasure();
						editQuestions();
						updateSectionAndSubsection();
						return "success";
					} else {
						getMeasure();
						return "success";
					}
				} else if (type != null && type.equals("quest")) {
					if (submit != null && submit.equals("Save")) {
						editQuestions();
						updateSectionAndSubsection();
						return "success";
					} else {
						getQuestions();
						return "success";
					}
				}
			} else if (scoreType != null && scoreType.equals("2")) {
				if (type != null && type.equals("score")) {
					if (submit != null && submit.equals("Save")) {
						editScoreCard();
						editMeasure();
						editScoreQuestions();
						updateSectionAndSubsection();
						return "success";
					} else {
						getScoreMeasure();
						return "success";
					}
				} else if (type != null && type.equals("measure")) {
					if (submit != null && submit.equals("Save")) {
						editMeasure();
						editQuestions();
						updateSectionAndSubsection();
						return "success";
					} else {
						getMeasure();
						return "success";
					}
				} else if (type != null && type.equals("quest")) {
					if (submit != null && submit.equals("Save")) {
						editQuestions();
						updateSectionAndSubsection();
						return "success";
					} else {
						getQuestions();
						return "success";
					}
				}
			}
		} else if (appsystem != null && appsystem.equals("2")) {
			if (submit != null && submit.equals("Save")) {
				editOtherQuestions();
				updateSectionAndSubsection();
				return "success";
			} else {
//				System.out.println("IN AppSystem 2 getQuestions ......... ");
				getQuestions();
				return "success";
			}
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
			ps.setInt(3, uF.parseToInt(getSubsectionID()));
			ps.executeUpdate();
			ps.close();
			
//			System.out.println("PS ===> "+ps);
			ps = con.prepareStatement("update appraisal_main_level_details set added_by = ?, entry_date=? where main_level_id = ?");
			ps.setInt(1, uF.parseToInt(strSessionEmpId));
			ps.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			ps.setInt(3, uF.parseToInt(getSectionID()));
			ps.executeUpdate();
			ps.close();
//			System.out.println("PS 1 ===> "+ps);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			db.closeStatements(ps);
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

	
	private void editOtherQuestions() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		String[] questionID = request.getParameterValues("questionID");
		//String[] hidequeid = request.getParameterValues("hidequeid");
		String[] weightage = request.getParameterValues("weightage");
		String[] question = request.getParameterValues("question");
		String othrqueanstype = request.getParameter("othrqueanstype");
		
		String[] addFlag = request.getParameterValues("status");
		String[] optiona = request.getParameterValues("optiona");
		String[] optionb = request.getParameterValues("optionb");
		String[] optionc = request.getParameterValues("optionc");
		String[] optiond = request.getParameterValues("optiond");
		String[] optione = request.getParameterValues("optione");
		String[] rateoptiona = request.getParameterValues("rateoptiona");
		String[] rateoptionb = request.getParameterValues("rateoptionb");
		String[] rateoptionc = request.getParameterValues("rateoptionc");
		String[] rateoptiond = request.getParameterValues("rateoptiond");
		String[] rateoptione = request.getParameterValues("rateoptione");
		String[] orientt = request.getParameterValues("orientt");
		String[] otherSDescription = request.getParameterValues("otherSDescription");

		
		try {
			con = db.makeConnection(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
			
//			System.out.println("othrqueanstype ;;;;;;;;;;;; " + othrqueanstype);			
//			System.out.println("hidequeid.length ;;;;;;;;;;;; " + hidequeid.length);
			for (int l = 0; l < question.length; l++) {
//				System.out.println("hidequeid[l].length() :: "+hidequeid[l].length());
//				System.out.println("hidequeid[l] :: "+hidequeid[l]);
//				int question_id = uF.parseToInt(hidequeid[l]);
				int question_id = 0;
				if (question[l].length() > 0) {
//					if (uF.parseToInt(hidequeid[l]) == 0) {

						String[] correct = request.getParameterValues("correct"+ orientt[l]);
						String ansType = request.getParameter("othrqueanstype");
						StringBuilder option = new StringBuilder();

						for (int ab = 0; correct != null && ab < correct.length; ab++) {
							option.append(correct[ab] + ",");
						}

						pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
							"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
							"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
						pst.setString(1, question[l]);
						pst.setString(2, (optiona != null && optiona.length > l ? optiona[l]: ""));
						pst.setString(3, (optionb != null && optionb.length > l ? optionb[l]: ""));
						pst.setString(4, (optionc != null && optionc.length > l ? optionc[l]: ""));
						pst.setString(5, (optiond != null && optiond.length > l ? optiond[l]: ""));
						pst.setString(6, (optione != null && optione.length > l ? optione[l]: ""));
						pst.setInt(7, (rateoptiona != null && rateoptiona.length > l ? uF.parseToInt(rateoptiona[l]): 0));
						pst.setInt(8, (rateoptionb != null && rateoptionb.length > l ? uF.parseToInt(rateoptionb[l]): 0));
						pst.setInt(9, (rateoptionc != null && rateoptionc.length > l ? uF.parseToInt(rateoptionc[l]): 0));
						pst.setInt(10, (rateoptiond != null && rateoptiond.length > l ? uF.parseToInt(rateoptiond[l]): 0));
						pst.setInt(11, (rateoptione != null && rateoptione.length > l ? uF.parseToInt(rateoptione[l]): 0));
						pst.setString(12, option.toString());
						pst.setBoolean(13, uF.parseToBoolean(addFlag[l]));
						pst.setInt(14, uF.parseToInt(ansType));
						pst.executeUpdate();
						pst.close();
						
						/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
						pst.setString(1, question[l]);
						pst.setString(2, (optiona != null && optiona.length > l ? optiona[l]: ""));
						pst.setString(3, (optionb != null && optionb.length > l ? optionb[l]: ""));
						pst.setString(4, (optionc != null && optionc.length > l ? optionc[l]: ""));
						pst.setString(5, (optiond != null && optiond.length > l ? optiond[l]: ""));
						pst.setString(6, option.toString());
						pst.setBoolean(7, uF.parseToBoolean(addFlag[l]));
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
//					}
					String other_id = null;
					pst = con.prepareStatement("select other_id from appraisal_question_details where appraisal_question_details_id=?");
					pst.setInt(1, uF.parseToInt(questionID[l]));
//					System.out.println("pst : ====== > " +pst);
					rst = pst.executeQuery();
					while (rst.next()) {
						other_id = rst.getString(1);
					} 
					rst.close();
					pst.close();
					
					String attributeid = "";
					pst = con.prepareStatement("select attribute_id from appraisal_other_question_type_details aoq,appraisal_level_details al "
									+ "where aoq.othe_question_type_id=? and al.appraisal_level_id=aoq.level_id");
					pst.setInt(1, uF.parseToInt(other_id));
					rst = pst.executeQuery();
					while (rst.next()) {
						if(rst.getString(1) != null && !rst.getString(1).equals("")){
							attributeid = rst.getString(1).trim();
						}
					}
					rst.close();
					pst.close();
					
//					System.out.println("getId() ;;;;;;;;;;;; " + getId());
//					System.out.println("hidequeid[l] ;;;;;;;;;;;; " + hidequeid[l]);
				//===start parvez date: 22-02-2023===	
//					pst = con.prepareStatement("update appraisal_question_details set question_id=?,weightage=?,attribute_id=?,answer_type=? " +
//							"where appraisal_id=? and appraisal_question_details_id=?");
					pst = con.prepareStatement("update appraisal_question_details set question_id=?,weightage=?,attribute_id=?,answer_type=?, other_short_description=? " +
								"where appraisal_id=? and appraisal_question_details_id=?");
				//===end parvez date: 22-02-2023===	
					pst.setInt(1, question_id);
					pst.setDouble(2, uF.parseToDouble(weightage[l]));
					pst.setInt(3, uF.parseToInt(attributeid));
					pst.setInt(4, uF.parseToInt(othrqueanstype));
				//===start parvez date: 22-02-2023===	
//					pst.setInt(5, uF.parseToInt(getId()));
//					pst.setInt(6, uF.parseToInt(questionID[l]));
					pst.setString(5, otherSDescription[l]);
					pst.setInt(6, uF.parseToInt(getId()));
					pst.setInt(7, uF.parseToInt(questionID[l]));
				//===end parvez date: 22-02-2023===	
					pst.execute();
					pst.close();
//					System.out.println("pst ;;;;;;;;;;;; " + pst);
					/*pst = con.prepareStatement("update appraisal_question_answer set "
									+ " appraisal_attribute=? where appraisal_id=? and question_id=?");
					pst.setInt(1, uF.parseToInt(attributeid));
					pst.setInt(2, uF.parseToInt(getId()));
					pst.setInt(3, uF.parseToInt(questionID[l]));
					pst.execute();*/

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void editScoreQuestions() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		String[] questionID = request.getParameterValues("questionID");
		//String[] hidequeid = request.getParameterValues("hidequeid");
		String[] weightage = request.getParameterValues("weightage");
		//String[] weightagenew = request.getParameterValues("weightagenew");
		String[] question = request.getParameterValues("question");

		String[] addFlag = request.getParameterValues("status");
		String[] optiona = request.getParameterValues("optiona");
		String[] optionb = request.getParameterValues("optionb");
		String[] optionc = request.getParameterValues("optionc");
		String[] optiond = request.getParameterValues("optiond");
		String[] optione = request.getParameterValues("optione");
		String[] rateoptiona = request.getParameterValues("rateoptiona");
		String[] rateoptionb = request.getParameterValues("rateoptionb");
		String[] rateoptionc = request.getParameterValues("rateoptionc");
		String[] rateoptiond = request.getParameterValues("rateoptiond");
		String[] rateoptione = request.getParameterValues("rateoptione");
		String[] orientt = request.getParameterValues("orientt");
		
		try {
			con = db.makeConnection(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);

			for (int l = 0; l < question.length; l++) {
//				System.out.println("hidequeid[l].length() :: "+hidequeid[l].length());
//				System.out.println("hidequeid[l] :: "+hidequeid[l]);
//				int question_id = uF.parseToInt(hidequeid[l]);
				int question_id = 0;
				if (question[l].length() > 0) {
//					if (uF.parseToInt(hidequeid[l]) == 0) {

						String[] correct = request.getParameterValues("correct"+ orientt[l]);
						String ansType = request.getParameter("queanstype");
						StringBuilder option = new StringBuilder();

						for (int ab = 0; correct != null && ab < correct.length; ab++) {
							option.append(correct[ab] + ",");
						}

						pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
							"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
							"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
						pst.setString(1, question[l]);
						pst.setString(2, (optiona != null && optiona.length > l ? optiona[l]: ""));
						pst.setString(3, (optionb != null && optionb.length > l ? optionb[l]: ""));
						pst.setString(4, (optionc != null && optionc.length > l ? optionc[l]: ""));
						pst.setString(5, (optiond != null && optiond.length > l ? optiond[l]: ""));
						pst.setString(6, (optione != null && optione.length > l ? optione[l]: ""));
						pst.setInt(7, (rateoptiona != null && rateoptiona.length > l ? uF.parseToInt(rateoptiona[l]): 0));
						pst.setInt(8, (rateoptionb != null && rateoptionb.length > l ? uF.parseToInt(rateoptionb[l]): 0));
						pst.setInt(9, (rateoptionc != null && rateoptionc.length > l ? uF.parseToInt(rateoptionc[l]): 0));
						pst.setInt(10, (rateoptiond != null && rateoptiond.length > l ? uF.parseToInt(rateoptiond[l]): 0));
						pst.setInt(11, (rateoptione != null && rateoptione.length > l ? uF.parseToInt(rateoptione[l]): 0));
						pst.setString(12, option.toString());
						pst.setBoolean(13, uF.parseToBoolean(addFlag[l]));
						pst.setInt(14, uF.parseToInt(ansType));
						pst.executeUpdate();
						pst.close();
						
						/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
						pst.setString(1, question[l]);
						pst.setString(2, (optiona != null && optiona.length > l ? optiona[l]: ""));
						pst.setString(3, (optionb != null && optionb.length > l ? optionb[l]: ""));
						pst.setString(4, (optionc != null && optionc.length > l ? optionc[l]: ""));
						pst.setString(5, (optiond != null && optiond.length > l ? optiond[l]: ""));
						pst.setString(6, option.toString());
						pst.setBoolean(7, uF.parseToBoolean(addFlag[l]));
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
//					}

					pst = con.prepareStatement("update appraisal_question_details set question_id=?,weightage=?,attribute_id=? where appraisal_id=? "
						+ "and appraisal_question_details_id=?");
					pst.setInt(1, question_id);
					pst.setDouble(2, uF.parseToDouble(weightage[l]));
					pst.setInt(3, uF.parseToInt(getAttribute_id()));
					pst.setInt(4, uF.parseToInt(getId()));
					pst.setInt(5, uF.parseToInt(questionID[l]));
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("update appraisal_question_answer set appraisal_attribute=? where appraisal_id=? and question_id=? and appraisal_freq_id = ?");
					pst.setInt(1, uF.parseToInt(getAttribute_id()));
					pst.setInt(2, uF.parseToInt(getId()));
					pst.setInt(3, uF.parseToInt(questionID[l]));
					pst.setInt(4, uF.parseToInt(getAppFreqId()));
					pst.execute();
					pst.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getQuestions() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			Map<String, String> AppraisalQuestion = getAppraisalQuestionMap(con, uF);
			pst = con.prepareStatement("select * from appraisal_question_details where appraisal_question_details_id=? and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getEditID()));
			pst.setInt(2, uF.parseToInt(getId()));

			rs = pst.executeQuery();
//			System.out.println("pst ==== > "+pst);
			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
//			Map<String, Map<String, String>> memberMp = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_Q"), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_A"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_B"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_C"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_D"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ANS"), "")); //8
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ANSTYPE"), "")); //9
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ISADD"), "")); //10
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_E"), "")); //11
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_A"), "")); //12
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_B"), "")); //13
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_C"), "")); //14
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_D"), "")); //15
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_E"), "")); //16
				innerList.add(rs.getString("other_short_description")); //17
				
				List<List<String>> outerList = questionMp.get(rs.getString("appraisal_question_details_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				questionMp.put(rs.getString("appraisal_question_details_id"),outerList);

				/*Map<String, String> memberInnerMp = new HashMap<String, String>();
				memberInnerMp.put("HR", rs.getString("hr"));
				memberInnerMp.put("Self", rs.getString("self"));
				memberInnerMp.put("Manager", rs.getString("manager"));
				memberInnerMp.put("Peer", rs.getString("peer"));
				memberInnerMp.put("Client", rs.getString("client"));
				memberInnerMp.put("Sub ordinate", rs.getString("subordinate"));
				memberInnerMp.put("GroupHead", rs.getString("grouphead"));
				memberInnerMp.put("Vendor", rs.getString("vendor"));
				memberInnerMp.put("CEO", rs.getString("ceo"));
				memberInnerMp.put("HOD", rs.getString("hod"));
				memberMp.put(rs.getString("appraisal_question_details_id"), memberInnerMp);*/
			}
			rs.close();
			pst.close();
			
//			request.setAttribute("memberMp", memberMp);

			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
			list.add(questionMp);

			request.setAttribute("score1", list);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getScoreMeasure() {

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
			pst = con.prepareStatement("select * from appraisal_scorecard_details where scorecard_id =? and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getEditID()));
			pst.setInt(2, uF.parseToInt(id));
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
				innerList.add(uF.showData(rs.getString("scorecard_section_name"), ""));
				innerList.add(uF.showData(rs.getString("scorecard_description"), ""));
				innerList.add(uF.showData(rs.getString("scorecard_weightage"), ""));
				innerList.add(uF.showData(rs.getString("appraisal_attribute"), ""));
				attributevalue = uF.showData(rs.getString("appraisal_attribute"), "");

				List<List<String>> outerList = scoreMp.get(rs.getString("scorecard_id"));

				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				scoreMp.put(rs.getString("scorecard_id"), outerList);
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
					measure_id = rs.getString("measure_id").trim();
				} else {
					measure_id += "," + rs.getString("measure_id").trim();
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("measure_section_name"));
				innerList.add(rs.getString("measure_description"));
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
//			Map<String, Map<String, String>> memberMp = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_Q"), ""));//0
				innerList.add(rs.getString("weightage"));//1
				innerList.add(rs.getString("appraisal_question_details_id"));//2
				innerList.add(rs.getString("question_id"));//3
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_A"), ""));//4
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_B"), ""));//5
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_C"), ""));//6
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_D"), ""));//7
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ANS"), ""));//8
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ANSTYPE"), ""));//9
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ISADD"), ""));//10
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_E"), ""));//11
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_A"), "")); //12
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_B"), "")); //13
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_C"), "")); //14
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_D"), "")); //15
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_E"), "")); //16
//				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
//				innerList.add(rs.getString("weightage"));
//				innerList.add(rs.getString("appraisal_question_details_id"));
//				innerList.add(rs.getString("question_id"));

				List<List<String>> outerList = questionMp.get(rs.getString("measure_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				questionMp.put(rs.getString("measure_id"), outerList);

				/*Map<String, String> memberInnerMp = new HashMap<String, String>();
				memberInnerMp.put("HR", rs.getString("hr"));
				memberInnerMp.put("Self", rs.getString("self"));
				memberInnerMp.put("Manager", rs.getString("manager"));
				memberInnerMp.put("Peer", rs.getString("peer"));
				memberInnerMp.put("Client", rs.getString("client"));
				memberInnerMp.put("Sub ordinate", rs.getString("subordinate"));
				memberInnerMp.put("GroupHead", rs.getString("grouphead"));
				memberInnerMp.put("Vendor", rs.getString("vendor"));
				memberInnerMp.put("CEO", rs.getString("ceo"));
				memberInnerMp.put("HOD", rs.getString("hod"));
				
				memberMp.put(rs.getString("appraisal_question_details_id"), memberInnerMp);*/

			}
			rs.close();
			pst.close();
			
//			request.setAttribute("memberMp", memberMp);

			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
			list.add(scoreMp);
			list.add(measureMp);
			list.add(questionMp);

			request.setAttribute("score1", list);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getMeasureGoal() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			Map<String, String> AppraisalQuestion = getAppraisalQuestionMap(con, uF);
			
			pst = con.prepareStatement("select * from appraisal_goal_details where goal_id=? and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getEditID()));
			pst.setInt(2, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> GoalMp = new HashMap<String, List<List<String>>>();
			String goal_id = null;
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					goal_id = rs.getString("goal_id").trim();
				} else {
					goal_id += "," + rs.getString("goal_id").trim();
				}
				i++;
				List<String> innerGoalList = new ArrayList<String>();
				innerGoalList.add(rs.getString("goal_id"));
				innerGoalList.add(rs.getString("goal_section_name"));
				innerGoalList.add(rs.getString("goal_description"));
				innerGoalList.add(rs.getString("goal_weightage"));

				List<List<String>> outerGoalList = GoalMp.get(rs.getString("goal_id"));
				if (outerGoalList == null)
					outerGoalList = new ArrayList<List<String>>();
				outerGoalList.add(innerGoalList);

				GoalMp.put(rs.getString("goal_id"), outerGoalList);

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
				innerList.add(rs.getString("measure_description"));
				innerList.add(rs.getString("weightage"));
				List<List<String>> outerList = measureMp.get(rs.getString("goal_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				measureMp.put(rs.getString("goal_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_question_details where measure_id in(" + measure_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
//			Map<String, Map<String, String>> memberMp = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_Q"), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_A"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_B"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_C"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_D"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ANS"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ANSTYPE"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ISADD"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_E"), ""));//11
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_A"), "")); //12
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_B"), "")); //13
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_C"), "")); //14
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_D"), "")); //15
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_E"), "")); //16
//				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
//				innerList.add(rs.getString("weightage"));
//				innerList.add(rs.getString("appraisal_question_details_id"));
//				innerList.add(rs.getString("question_id"));

				List<List<String>> outerList = questionMp.get(rs.getString("measure_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				questionMp.put(rs.getString("measure_id"), outerList);

				/*Map<String, String> memberInnerMp = new HashMap<String, String>();
				memberInnerMp.put("HR", rs.getString("hr"));
				memberInnerMp.put("Self", rs.getString("self"));
				memberInnerMp.put("Manager", rs.getString("manager"));
				memberInnerMp.put("Peer", rs.getString("peer"));
				memberInnerMp.put("Client", rs.getString("client"));
				memberInnerMp.put("Sub ordinate", rs.getString("subordinate"));
				memberInnerMp.put("GroupHead", rs.getString("grouphead"));
				memberInnerMp.put("Vendor", rs.getString("vendor"));
				memberInnerMp.put("CEO", rs.getString("ceo"));
				memberInnerMp.put("HOD", rs.getString("hod"));
				
				memberMp.put(rs.getString("appraisal_question_details_id"), memberInnerMp);*/
			}
			rs.close();
			pst.close();
			
//			request.setAttribute("memberMp", memberMp);

			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
			list.add(measureMp);
			list.add(questionMp);
			list.add(GoalMp);

			request.setAttribute("score1", list);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getScoreMeasureGoal() {

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
			pst = con.prepareStatement("select * from appraisal_scorecard_details where scorecard_id =? and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getEditID()));
			pst.setInt(2, uF.parseToInt(id));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> scoreMp = new HashMap<String, List<List<String>>>();
			String scorecard_id = null;
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					scorecard_id = rs.getString("scorecard_id").trim();
				} else {
					scorecard_id += "," + rs.getString("scorecard_id").trim();
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(uF.showData(rs.getString("scorecard_section_name"), ""));
				innerList.add(uF.showData(rs.getString("scorecard_description"), ""));
				innerList.add(uF.showData(rs.getString("scorecard_weightage"), ""));
				innerList.add(uF.showData(rs.getString("appraisal_attribute"), ""));
				attributevalue = uF.showData(rs.getString("appraisal_attribute"), "");

				List<List<String>> outerList = scoreMp.get(rs.getString("scorecard_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				scoreMp.put(rs.getString("scorecard_id"), outerList);
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
					goal_id = rs.getString("goal_id").trim();
				} else {
					goal_id += "," + rs.getString("goal_id").trim();
				}
				i++;
				List<String> innerGoalList = new ArrayList<String>();
				innerGoalList.add(rs.getString("goal_id"));
				innerGoalList.add(rs.getString("goal_section_name"));
				innerGoalList.add(rs.getString("goal_description"));
				innerGoalList.add(rs.getString("goal_weightage"));

				List<List<String>> outerGoalList = GoalMp.get(rs.getString("scorecard_id"));
				if (outerGoalList == null)
					outerGoalList = new ArrayList<List<String>>();
				outerGoalList.add(innerGoalList);

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
					measure_id = rs.getString("measure_id").trim();
				} else {
					measure_id += "," + rs.getString("measure_id").trim();
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("measure_section_name"));
				innerList.add(rs.getString("measure_description"));
				innerList.add(rs.getString("weightage"));
				List<List<String>> outerList = measureMp.get(rs.getString("goal_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				measureMp.put(rs.getString("goal_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_question_details where measure_id in(" + measure_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
//			Map<String, Map<String, String>> memberMp = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_Q"), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_A"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_B"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_C"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_D"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ANS"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ANSTYPE"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ISADD"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_E"), ""));//11
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_A"), "")); //12
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_B"), "")); //13
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_C"), "")); //14
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_D"), "")); //15
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_E"), "")); //16
//				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
//				innerList.add(rs.getString("weightage"));
//				innerList.add(rs.getString("appraisal_question_details_id"));
//				innerList.add(rs.getString("question_id"));

				List<List<String>> outerList = questionMp.get(rs.getString("measure_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				questionMp.put(rs.getString("measure_id"), outerList);

				/*Map<String, String> memberInnerMp = new HashMap<String, String>();
				memberInnerMp.put("HR", rs.getString("hr"));
				memberInnerMp.put("Self", rs.getString("self"));
				memberInnerMp.put("Manager", rs.getString("manager"));
				memberInnerMp.put("Peer", rs.getString("peer"));
				memberInnerMp.put("Client", rs.getString("client"));
				memberInnerMp.put("Sub ordinate", rs.getString("subordinate"));
				memberInnerMp.put("GroupHead", rs.getString("grouphead"));
				memberInnerMp.put("Vendor", rs.getString("vendor"));
				memberInnerMp.put("CEO", rs.getString("ceo"));
				memberInnerMp.put("HOD", rs.getString("hod"));
				
				memberMp.put(rs.getString("appraisal_question_details_id"), memberInnerMp);*/

			}
			rs.close();
			pst.close();
			
//			request.setAttribute("memberMp", memberMp);

			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
			list.add(scoreMp);
			list.add(measureMp);
			list.add(questionMp);
			list.add(GoalMp);

			request.setAttribute("score1", list);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getMeasure() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			Map<String, String> AppraisalQuestion = getAppraisalQuestionMap(con, uF);
			pst = con.prepareStatement("select * from appraisal_measure_details where measure_id=? and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getEditID()));
			pst.setInt(2, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> measureMp = new HashMap<String, List<List<String>>>();
			String measure_id = null;
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					measure_id = rs.getString("measure_id").trim();
				} else {
					measure_id += "," + rs.getString("measure_id").trim();
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("measure_section_name"));
				innerList.add(rs.getString("measure_description"));
				innerList.add(rs.getString("weightage"));
				List<List<String>> outerList = measureMp.get(rs.getString("measure_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				measureMp.put(rs.getString("measure_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_question_details where measure_id in(" + measure_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
//			Map<String, Map<String, String>> memberMp = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_Q"), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_A"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_B"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_C"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_D"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ANS"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ANSTYPE"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ISADD"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_E"), ""));//11
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_A"), "")); //12
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_B"), "")); //13
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_C"), "")); //14
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_D"), "")); //15
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_E"), "")); //16
//				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
//				innerList.add(rs.getString("weightage"));
//				innerList.add(rs.getString("appraisal_question_details_id"));
//				innerList.add(rs.getString("question_id"));

				List<List<String>> outerList = questionMp.get(rs.getString("measure_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				questionMp.put(rs.getString("measure_id"), outerList);

				/*Map<String, String> memberInnerMp = new HashMap<String, String>();
				memberInnerMp.put("HR", rs.getString("hr"));
				memberInnerMp.put("Self", rs.getString("self"));
				memberInnerMp.put("Manager", rs.getString("manager"));
				memberInnerMp.put("Peer", rs.getString("peer"));
				memberInnerMp.put("Client", rs.getString("client"));
				memberInnerMp.put("Sub ordinate", rs.getString("subordinate"));
				memberInnerMp.put("GroupHead", rs.getString("grouphead"));
				memberInnerMp.put("Vendor", rs.getString("vendor"));
				memberInnerMp.put("CEO", rs.getString("ceo"));
				memberInnerMp.put("HOD", rs.getString("hod"));
				
				memberMp.put(rs.getString("appraisal_question_details_id"), memberInnerMp);*/

			}
			rs.close();
			pst.close();
			
//			request.setAttribute("memberMp", memberMp);

			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
			list.add(measureMp);
			list.add(questionMp);

			request.setAttribute("score1", list);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getObjectiveMeasure() {

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

			pst = con
					.prepareStatement("select * from appraisal_objective_details where objective_id=? and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getEditID()));
			pst.setInt(2, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> objectiveMp = new HashMap<String, List<List<String>>>();
			String objective_id = null;
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					objective_id = rs.getString("objective_id").trim();
				} else {
					objective_id += "," + rs.getString("objective_id").trim();
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("objective_id"));
				innerList.add(rs.getString("objective_section_name"));
				innerList.add(rs.getString("objective_description"));
				innerList.add(rs.getString("objective_weightage"));

				List<List<String>> outerList = objectiveMp.get(rs.getString("objective_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				objectiveMp.put(rs.getString("objective_id"), outerList);
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
					measure_id = rs.getString("measure_id").trim();
				} else {
					measure_id += "," + rs.getString("measure_id").trim();
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("measure_section_name"));
				innerList.add(rs.getString("measure_description"));
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
//			Map<String, Map<String, String>> memberMp = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_Q"), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_A"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_B"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_C"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_D"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ANS"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ANSTYPE"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ISADD"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_E"), ""));//11
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_A"), "")); //12
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_B"), "")); //13
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_C"), "")); //14
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_D"), "")); //15
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_E"), "")); //16
//				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
//				innerList.add(rs.getString("weightage"));
//				innerList.add(rs.getString("appraisal_question_details_id"));
//				innerList.add(rs.getString("question_id"));

				List<List<String>> outerList = questionMp.get(rs.getString("measure_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				questionMp.put(rs.getString("measure_id"), outerList);

				/*Map<String, String> memberInnerMp = new HashMap<String, String>();
				memberInnerMp.put("HR", rs.getString("hr"));
				memberInnerMp.put("Self", rs.getString("self"));
				memberInnerMp.put("Manager", rs.getString("manager"));
				memberInnerMp.put("Peer", rs.getString("peer"));
				memberInnerMp.put("Client", rs.getString("client"));
				memberInnerMp.put("Sub ordinate", rs.getString("subordinate"));
				memberInnerMp.put("GroupHead", rs.getString("grouphead"));
				memberInnerMp.put("Vendor", rs.getString("vendor"));
				memberInnerMp.put("CEO", rs.getString("ceo"));
				memberInnerMp.put("HOD", rs.getString("hod"));
				
				memberMp.put(rs.getString("appraisal_question_details_id"), memberInnerMp);*/

			}
			rs.close();
			pst.close();
			
//			request.setAttribute("memberMp", memberMp);

			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
			list.add(measureMp);
			list.add(questionMp);
			list.add(objectiveMp);

			request.setAttribute("score1", list);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getObjectiveMeasureGoal() {

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

			pst = con.prepareStatement("select * from appraisal_goal_details where goal_id=? and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getEditID()));
			pst.setInt(2, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> GoalMp = new HashMap<String, List<List<String>>>();
			String goal_id = null;
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					goal_id = rs.getString("goal_id").trim();
				} else {
					goal_id += "," + rs.getString("goal_id").trim();
				}
				i++;
				List<String> innerGoalList = new ArrayList<String>();
				innerGoalList.add(rs.getString("goal_id"));
				innerGoalList.add(rs.getString("goal_section_name"));
				innerGoalList.add(rs.getString("goal_description"));
				innerGoalList.add(rs.getString("goal_weightage"));

				List<List<String>> outerGoalList = GoalMp.get(rs.getString("goal_id"));
				if (outerGoalList == null)
					outerGoalList = new ArrayList<List<String>>();
				outerGoalList.add(innerGoalList);

				GoalMp.put(rs.getString("goal_id"), outerGoalList);

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
					objective_id = rs.getString("objective_id").trim();
				} else {
					objective_id += "," + rs.getString("objective_id").trim();
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("objective_id"));
				innerList.add(rs.getString("objective_section_name"));
				innerList.add(rs.getString("objective_description"));
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
					measure_id = rs.getString("measure_id").trim();
				} else {
					measure_id += "," + rs.getString("measure_id").trim();
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("measure_section_name"));
				innerList.add(rs.getString("measure_description"));
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
//			Map<String, Map<String, String>> memberMp = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_Q"), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_A"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_B"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_C"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_D"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ANS"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ANSTYPE"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ISADD"), ""));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_E"), ""));//11
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_A"), "")); //12
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_B"), "")); //13
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_C"), "")); //14
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_D"), "")); //15
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_E"), "")); //16
//				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
//				innerList.add(rs.getString("weightage"));
//				innerList.add(rs.getString("appraisal_question_details_id"));
//				innerList.add(rs.getString("question_id"));

				List<List<String>> outerList = questionMp.get(rs.getString("measure_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				questionMp.put(rs.getString("measure_id"), outerList);

				/*Map<String, String> memberInnerMp = new HashMap<String, String>();
				memberInnerMp.put("HR", rs.getString("hr"));
				memberInnerMp.put("Self", rs.getString("self"));
				memberInnerMp.put("Manager", rs.getString("manager"));
				memberInnerMp.put("Peer", rs.getString("peer"));
				memberInnerMp.put("Client", rs.getString("client"));
				memberInnerMp.put("Sub ordinate", rs.getString("subordinate"));
				memberInnerMp.put("GroupHead", rs.getString("grouphead"));
				memberInnerMp.put("Vendor", rs.getString("vendor"));
				memberInnerMp.put("CEO", rs.getString("ceo"));
				memberInnerMp.put("HOD", rs.getString("hod"));
				
				memberMp.put(rs.getString("appraisal_question_details_id"), memberInnerMp);*/

			}
			rs.close();
			pst.close();
			
//			request.setAttribute("memberMp", memberMp);

			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
			list.add(measureMp);
			list.add(questionMp);
			list.add(GoalMp);
			list.add(objectiveMp);

			request.setAttribute("score1", list);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void editQuestions() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		String[] questionID = request.getParameterValues("questionID");
		//String[] hidequeid = request.getParameterValues("hidequeid");
		String[] weightage = request.getParameterValues("weightage");
		//String[] weightagenew = request.getParameterValues("weightagenew");
		String[] question = request.getParameterValues("question");

		String[] addFlag = request.getParameterValues("status");
		String[] optiona = request.getParameterValues("optiona");
		String[] optionb = request.getParameterValues("optionb");
		String[] optionc = request.getParameterValues("optionc");
		String[] optiond = request.getParameterValues("optiond");
		String[] optione = request.getParameterValues("optione");
		String[] rateoptiona = request.getParameterValues("rateoptiona");
		String[] rateoptionb = request.getParameterValues("rateoptionb");
		String[] rateoptionc = request.getParameterValues("rateoptionc");
		String[] rateoptiond = request.getParameterValues("rateoptiond");
		String[] rateoptione = request.getParameterValues("rateoptione");
		String[] orientt = request.getParameterValues("orientt");

		
		try {
			con = db.makeConnection(con);
//			Map<String, String> orientationMemberMp = getOrientationMember(con);
//			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
			
			for (int l = 0; l < question.length; l++) {
//				System.out.println("hidequeid[l].length() :: "+hidequeid[l].length());
//				System.out.println("hidequeid[l] :: "+hidequeid[l]);
//				int question_id = uF.parseToInt(hidequeid[l]);
				int question_id = 0;
				String ansType = request.getParameter("queanstype");
				
				if (question[l].length() > 0) {
//					if (uF.parseToInt(hidequeid[l]) == 0) {
						
						String[] correct = request.getParameterValues("correct"+ orientt[l]);
						StringBuilder option = new StringBuilder();

						for (int ab = 0; correct != null && ab < correct.length; ab++) {
							option.append(correct[ab] + ",");
						}

						pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
							"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
							"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
						pst.setString(1, question[l]);
						pst.setString(2, (optiona != null && optiona.length > l ? optiona[l]: ""));
						pst.setString(3, (optionb != null && optionb.length > l ? optionb[l]: ""));
						pst.setString(4, (optionc != null && optionc.length > l ? optionc[l]: ""));
						pst.setString(5, (optiond != null && optiond.length > l ? optiond[l]: ""));
						pst.setString(6, (optione != null && optione.length > l ? optione[l]: ""));
						pst.setInt(7, (rateoptiona != null && rateoptiona.length > l ? uF.parseToInt(rateoptiona[l]): 0));
						pst.setInt(8, (rateoptionb != null && rateoptionb.length > l ? uF.parseToInt(rateoptionb[l]): 0));
						pst.setInt(9, (rateoptionc != null && rateoptionc.length > l ? uF.parseToInt(rateoptionc[l]): 0));
						pst.setInt(10, (rateoptiond != null && rateoptiond.length > l ? uF.parseToInt(rateoptiond[l]): 0));
						pst.setInt(11, (rateoptione != null && rateoptione.length > l ? uF.parseToInt(rateoptione[l]): 0));
						pst.setString(12, option.toString());
						pst.setBoolean(13, uF.parseToBoolean(addFlag[l]));
						pst.setInt(14, uF.parseToInt(ansType));
						pst.executeUpdate();
						pst.close();
						
						/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
						pst.setString(1, question[l]);
						pst.setString(2, (optiona != null && optiona.length > l ? optiona[l]: ""));
						pst.setString(3, (optionb != null && optionb.length > l ? optionb[l]: ""));
						pst.setString(4, (optionc != null && optionc.length > l ? optionc[l]: ""));
						pst.setString(5, (optiond != null && optiond.length > l ? optiond[l]: ""));
						pst.setString(6, option.toString());
						pst.setBoolean(7, uF.parseToBoolean(addFlag[l]));
						pst.setInt(8, uF.parseToInt(ansType));
						System.out.println("pst1==>"+pst);
						pst.execute();
						pst.close();*/
						
						pst = con.prepareStatement("select max(question_bank_id) from question_bank");
						rst = pst.executeQuery();
						while (rst.next()) {
							question_id = rst.getInt(1);
						}
						rst.close();
						pst.close();
//					}

					pst = con.prepareStatement("update appraisal_question_details set question_id=?,weightage=? where appraisal_id=? "
							+ "and appraisal_question_details_id=?");
					pst.setInt(1, question_id);
					pst.setDouble(2, uF.parseToDouble(weightage[l]));
					pst.setInt(3, uF.parseToInt(getId()));
					pst.setInt(4, uF.parseToInt(questionID[l]));
//					System.out.println("pst2==>"+pst);
					pst.execute();
					pst.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void editMeasure() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		String[] measuresSectionName = request.getParameterValues("measuresSectionName");
		String[] measuresDescription = request.getParameterValues("measuresDescription");
		String[] measureWeightage = request.getParameterValues("measureWeightage");
		String[] measureID = request.getParameterValues("measureID");

		try {
			con = db.makeConnection(con);

			for (int i = 0; i < measuresSectionName.length; i++) {

				pst = con.prepareStatement("update appraisal_measure_details set measure_section_name=?,measure_description=?,"
								+ "weightage=? where appraisal_id=? and measure_id=?");
				pst.setString(1, measuresSectionName[i]);
				pst.setString(2, measuresDescription[i]);
				pst.setString(3, measureWeightage[i]);
				pst.setInt(4, uF.parseToInt(id));
				pst.setInt(5, uF.parseToInt(measureID[i]));
				pst.execute();
				pst.close();
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void editObjective() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		String[] objectiveSectionName = request.getParameterValues("objectiveSectionName");
		String[] objectiveDescription = request.getParameterValues("objectiveDescription");
		String[] objectiveWeightage = request.getParameterValues("objectiveWeightage");
		String[] objectiveID = request.getParameterValues("objectiveID");

		try {
			con = db.makeConnection(con);
			
			for (int i = 0; i < objectiveSectionName.length; i++) {
				pst = con.prepareStatement("update appraisal_objective_details set objective_section_name=?,objective_description=?,"
						+ "objective_weightage=? where appraisal_id=? and objective_id=?");
				pst.setString(1, objectiveSectionName[i]);
				pst.setString(2, objectiveDescription[i]);
				pst.setString(3, objectiveWeightage[i]);
				pst.setInt(4, uF.parseToInt(id));
				pst.setInt(5, uF.parseToInt(objectiveID[i]));
				pst.execute();
				pst.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void editGoal() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		String[] goalSectionName = request.getParameterValues("goalSectionName");
		String[] goalDescription = request.getParameterValues("goalDescription");
		String[] goalWeightage = request.getParameterValues("goalWeightage");
		String[] goalID = request.getParameterValues("goalID");

		try {
			con = db.makeConnection(con);
			
			for (int i = 0; i < goalSectionName.length; i++) {

				pst = con.prepareStatement("update appraisal_goal_details set goal_section_name=?,goal_description=?,"
						+ "goal_weightage=? where appraisal_id=? and goal_id=?");
				pst.setString(1, goalSectionName[i]);
				pst.setString(2, goalDescription[i]);
				pst.setString(3, goalWeightage[i]);
				pst.setInt(4, uF.parseToInt(id));
				pst.setInt(5, uF.parseToInt(goalID[i]));
				pst.execute();
				pst.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void editScoreCard() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		String[] scoreSectionName = request.getParameterValues("scoreSectionName");
		String[] scoreCardDescription = request.getParameterValues("scoreCardDescription");
		String[] scoreCardWeightage = request.getParameterValues("scoreCardWeightage");
		String[] scoreID = request.getParameterValues("scoreID");
//		String[] attribute = request.getParameterValues("attribute");

		try {
			con = db.makeConnection(con);
			
			for (int i = 0; i < scoreSectionName.length; i++) {
//				attribute_id = attribute[i];
				pst = con.prepareStatement("update appraisal_scorecard_details set scorecard_section_name=?,scorecard_description=?,"
						+ "scorecard_weightage=? where appraisal_id=? and scorecard_id=? ");
				pst.setString(1, scoreSectionName[i]);
				pst.setString(2, scoreCardDescription[i]);
				pst.setString(3, scoreCardWeightage[i]);
//				pst.setInt(4, uF.parseToInt(attribute[i]));
				pst.setInt(4, uF.parseToInt(id));
				pst.setInt(5, uF.parseToInt(scoreID[i]));
				pst.execute();
				pst.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void getScoreObjectiveMeasureGoal() {

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
			pst = con.prepareStatement("select * from appraisal_scorecard_details where scorecard_id =? and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getEditID()));
			pst.setInt(2, uF.parseToInt(id));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> scoreMp = new HashMap<String, List<List<String>>>();
			String scorecard_id = null;
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					scorecard_id = rs.getString("scorecard_id").trim();
				} else {
					scorecard_id += "," + rs.getString("scorecard_id").trim();
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(uF.showData(rs.getString("scorecard_section_name"), ""));
				innerList.add(uF.showData(rs.getString("scorecard_description"), ""));
				innerList.add(uF.showData(rs.getString("scorecard_weightage"), ""));
				innerList.add(uF.showData(rs.getString("appraisal_attribute"), ""));
				attributevalue = uF.showData(rs.getString("appraisal_attribute"), "");

				List<List<String>> outerList = scoreMp.get(rs.getString("scorecard_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				scoreMp.put(rs.getString("scorecard_id"), outerList);
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
					goal_id = rs.getString("goal_id").trim();
				} else {
					goal_id += "," + rs.getString("goal_id").trim();
				}
				i++;
				List<String> innerGoalList = new ArrayList<String>();
				innerGoalList.add(rs.getString("goal_id"));
				innerGoalList.add(rs.getString("goal_section_name"));
				innerGoalList.add(rs.getString("goal_description"));
				innerGoalList.add(rs.getString("goal_weightage"));

				List<List<String>> outerGoalList = GoalMp.get(rs.getString("scorecard_id"));
				if (outerGoalList == null)
					outerGoalList = new ArrayList<List<String>>();
				outerGoalList.add(innerGoalList);

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
					objective_id = rs.getString("objective_id").trim();
				} else {
					objective_id += "," + rs.getString("objective_id").trim();
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("objective_id"));
				innerList.add(rs.getString("objective_section_name"));
				innerList.add(rs.getString("objective_description"));
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
					measure_id = rs.getString("measure_id").trim();
				} else {
					measure_id += "," + rs.getString("measure_id").trim();
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("measure_section_name"));
				innerList.add(rs.getString("measure_description"));
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
//			Map<String, Map<String, String>> memberMp = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_Q"), ""));//0
				innerList.add(rs.getString("weightage"));//1
				innerList.add(rs.getString("appraisal_question_details_id"));//2
				innerList.add(rs.getString("question_id"));//3
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_A"), ""));//4
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_B"), ""));//5
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_C"), ""));//6
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_D"), ""));//7
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ANS"), ""));//8
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ANSTYPE"), "")); //9
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_ISADD"), "")); //10
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_E"), ""));//11
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_A"), "")); //12
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_B"), "")); //13
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_C"), "")); //14
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_D"), "")); //15
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")+"_RATE_E"), "")); //16
//				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
//				innerList.add(rs.getString("weightage"));
//				innerList.add(rs.getString("appraisal_question_details_id"));
//				innerList.add(rs.getString("question_id"));

				List<List<String>> outerList = questionMp.get(rs.getString("measure_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				questionMp.put(rs.getString("measure_id"), outerList);

				/*Map<String, String> memberInnerMp = new HashMap<String, String>();
				memberInnerMp.put("HR", rs.getString("hr"));
				memberInnerMp.put("Self", rs.getString("self"));
				memberInnerMp.put("Manager", rs.getString("manager"));
				memberInnerMp.put("Peer", rs.getString("peer"));
				memberInnerMp.put("Client", rs.getString("client"));
				memberInnerMp.put("Sub ordinate", rs.getString("subordinate"));
				memberInnerMp.put("GroupHead", rs.getString("grouphead"));
				memberInnerMp.put("Vendor", rs.getString("vendor"));
				memberInnerMp.put("CEO", rs.getString("ceo"));
				memberInnerMp.put("HOD", rs.getString("hod"));
				
				memberMp.put(rs.getString("appraisal_question_details_id"), memberInnerMp);*/
			}
			rs.close();
			pst.close();
			
//			request.setAttribute("memberMp", memberMp);

			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
			list.add(scoreMp);
			list.add(measureMp);
			list.add(questionMp);
			list.add(GoalMp);
			list.add(objectiveMp);

			request.setAttribute("score1", list);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
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

		con = db.makeConnection(con);
		Map<String, String> hmDesignation = CF.getDesigMap(con);
		Map<String, String> hmGradeMap = CF.getGradeMap(con);
		Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
		Map<String, String> hmLevelMap = getLevelMap(con);
		Map<String, String> hmLocation = getLocationMap(con);
		Map<String, String> mpdepart = CF.getDeptMap(con);
		Map<String, String> orientationMp = getOrientationValue(con);
		Map<String, String> orientationMemberMp = getOrientationMember(con);
		
		String oreinted1=null;
		
		try {
			Map<String, String> hmFrequency = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmFrequency.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
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
						memberName += "," + orientationMemberMp.get(memberList.get(i));
				}

				appraisalList.add(uF.showData(rs.getString("appraisal_details_id"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_name"), ""));
				oreinted1 = rs.getString("oriented_type");
				appraisalList.add(uF.showData(orientationMp.get(rs.getString("oriented_type")) + "&deg( " + memberName + " )", ""));
				appraisalList.add(uF.showData(rs.getString("self_ids"), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("level_id"), hmLevelMap), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("desig_id"), hmDesignation), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("grade_id"), hmGradeMap), ""));
				appraisalList.add(uF.showData(hmFrequency.get(rs.getString("frequency")), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("wlocation_id"), hmLocation), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("department_id"), mpdepart), ""));
				appraisalList.add(uF.showData(rs.getString("supervisor_id"), ""));
				appraisalList.add(uF.showData(rs.getString("peer_ids"), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("self_ids"), hmEmpName), ""));
				appraisalList.add(uF.showData(rs.getString("emp_status"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_type"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_description"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_instruction"), ""));
				appraisalList.add(uF.showData(rs.getString("ceo_ids"), ""));
				appraisalList.add(uF.showData(rs.getString("hod_ids"), ""));
//				System.out.println("oreinted1=====> "+oreinted1);
				getOrientationValue(uF.parseToInt(oreinted1));
			}
			rs.close();
			pst.close();
//			System.out.println("appraisalList =====> "+appraisalList);
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
		organisationList = new FillOrganisation(request).fillOrganisation();

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

	private String getSelfIDs(String id2) {
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
				empID=empID.substring(1,empID.length()-1);
				List<String> levellistID=new ArrayList<String>();
				pst = con.prepareStatement("select ld.level_id from level_details ld right join (select * from designation_details dd " +
					"right join (select *, gd.designation_id as designationid from employee_official_details eod, grades_details gd " +
					"where gd.grade_id=eod.grade_id and eod.emp_id in ("+empID+")) a on a.designationid=dd.designation_id)" +
					" a on a.level_id=ld.level_id");
				rs=pst.executeQuery();
				while(rs.next()) {
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
//			System.out.println("member=====>"+sb.toString());
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

	private Map<String, String> getOrientationMember(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> orientationMemberMp = new HashMap<String, String>();

		try {
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMemberMp.put(rs.getString("orientation_member_id"), rs.getString("member_name"));
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

			sb.append("<option value=\"" + fillAttribute.getId() + "\">" + fillAttribute.getName() + "</option>");

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
			StringBuilder sb = new StringBuilder();
			Map<String, String> questMp = new HashMap<String, String>();
			con = db.makeConnection(con);
//			pst = con.prepareStatement("select * from question_bank where is_add=true");
			pst = con.prepareStatement("select * from question_bank");
			rs = pst.executeQuery();
			while (rs.next()) {
				sb.append("<option value=\"" + rs.getString("question_bank_id") + "\">" + rs.getString("question_text") != null ? rs.getString("question_text").replace("'", "") : "" + "</option>");
				questMp.put(rs.getString("question_bank_id"), rs.getString("question_text"));
			}
			rs.close();
			pst.close();

			sb.append("<option value=\"0\">Add new Question</option>");

			request.setAttribute("option", sb.toString());
			request.setAttribute("questMp", questMp);

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
			strID=strID.substring(1,strID.length()-1);
			if (strID.contains(",")) {				
				String[] temp = strID.split(",");
				
				for(int i = 0; i < temp.length; i++) {
					if (i == 0) {
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

	public Map<String, String> getAppraisalQuestionMap(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> AppraisalQuestion = new HashMap<String, String>();

		try {

			pst = con.prepareStatement("select * from question_bank qb, appraisal_question_details aqd where qb.question_bank_id=aqd.question_id and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				AppraisalQuestion.put(rs.getString("question_bank_id")+"_Q",rs.getString("question_text"));//0
				AppraisalQuestion.put(rs.getString("question_bank_id")+"_A",rs.getString("option_a"));//1
				AppraisalQuestion.put(rs.getString("question_bank_id")+"_B",rs.getString("option_b"));//2
				AppraisalQuestion.put(rs.getString("question_bank_id")+"_C",rs.getString("option_c"));//3
				AppraisalQuestion.put(rs.getString("question_bank_id")+"_D",rs.getString("option_d"));//4
				AppraisalQuestion.put(rs.getString("question_bank_id")+"_E",rs.getString("option_e"));//5
//				System.out.println("quest id==>"+rs.getString("question_bank_id")+"correct ans==>"+rs.getString("correct_ans"));
				AppraisalQuestion.put(rs.getString("question_bank_id")+"_ANS",rs.getString("correct_ans"));//6
				AppraisalQuestion.put(rs.getString("question_bank_id")+"_ANSTYPE",rs.getString("question_type"));//7
				AppraisalQuestion.put(rs.getString("question_bank_id")+"_ISADD",rs.getString("is_add"));//8
				AppraisalQuestion.put(rs.getString("question_bank_id")+"_RATE_A",rs.getString("rate_option_a"));
				AppraisalQuestion.put(rs.getString("question_bank_id")+"_RATE_B",rs.getString("rate_option_b"));
				AppraisalQuestion.put(rs.getString("question_bank_id")+"_RATE_C",rs.getString("rate_option_c"));
				AppraisalQuestion.put(rs.getString("question_bank_id")+"_RATE_D",rs.getString("rate_option_d"));
				AppraisalQuestion.put(rs.getString("question_bank_id")+"_RATE_E",rs.getString("rate_option_e"));
			}
			rs.close();
			pst.close();
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
				hmorientationMembers.put(rs.getString("orientation_member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();

			request.setAttribute("hmorientationMembers", hmorientationMembers);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMp;
	}

//	private String getManagerLocation() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		String location = "";
//
//		con = db.makeConnection(con);
//		try {
//			pst = con
//					.prepareStatement("select e.wlocation_id from employee_official_details e where e.emp_id=?");
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			rst = pst.executeQuery();
//			while (rst.next()) {
//				location = rst.getString(1);
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//			db.closeStatements(pst);
//			db.closeResultSet(rst);
//		}
//
//		return location;
//	}

//	public String getEmployeeList(String self, int type) {
//		StringBuilder sb = new StringBuilder();
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		con = db.makeConnection(con);
//		try {
//			self=self.substring(1,self.length()-1);
////			System.out.println("self=====>"+self);
//			if (type == 2) {
//
//				pst = con
//						.prepareStatement("select supervisor_emp_id from employee_official_details where emp_id in ("
//								+ self + ") and supervisor_emp_id!=0");
//				rs = pst.executeQuery();
//
//				int cnt = 0;
//				while (rs.next()) {
//					if (cnt == 0) {
//						sb.append(","+rs.getString("supervisor_emp_id").trim()+",");
//					} else {
//						sb.append(rs.getString("supervisor_emp_id").trim()+",");
//
//					}
//					cnt++;
//				}
//				return sb.toString();
//
//			} else if (type == 3) {
//
//			} else if (type == 4) {
//
//				pst = con
//						.prepareStatement("select grade_id from employee_official_details  where emp_id in("
//								+ self + ") group by grade_id");
//				rs = pst.executeQuery();
//				StringBuilder sb4 = new StringBuilder();
//				int cnt = 0;
//				while (rs.next()) {
//					if (cnt == 0) {
//						sb4.append(rs.getString("grade_id").trim());
//					} else {
//						sb4.append("," + rs.getString("grade_id").trim());
//
//					}
//					cnt++;
//				}
//				pst = con
//						.prepareStatement("select wlocation_id from employee_official_details  where emp_id in("
//								+ self + ") group by wlocation_id");
//				rs = pst.executeQuery();
//				StringBuilder sb5 = new StringBuilder();
//				cnt = 0;
//				while (rs.next()) {
//					if (cnt == 0) {
//						sb5.append(rs.getString("wlocation_id").trim());
//					} else {
//						sb5.append("," + rs.getString("wlocation_id").trim());
//
//					}
//					cnt++;
//				}
//				pst = con
//						.prepareStatement("select emp_id from employee_official_details  where wlocation_id in("
//								+ sb5.toString()
//								+ ") and grade_id in("
//								+ sb4.toString() + ") group by emp_id");
//
//				rs = pst.executeQuery();
//
//				cnt = 0;
//				while (rs.next()) {
//					if (cnt == 0) {
//						sb.append("," + rs.getString("emp_id").trim() +",");
//					} else {
//						sb.append(rs.getString("emp_id").trim() +",");
//
//					}
//					cnt++;
//				}
//				return sb.toString();
//
//			} else if (type == 5) {
//
//			} else if (type == 6) {
//
//			} else if (type == 7) {
//
//				pst = con
//						.prepareStatement("select wlocation_id from employee_official_details  where emp_id in("
//								+ self + ") group by wlocation_id");
//				rs = pst.executeQuery();
//				StringBuilder sb5 = new StringBuilder();
//				int cnt = 0;
//				while (rs.next()) {
//					if (cnt == 0) {
//						sb5.append(rs.getString("wlocation_id").trim());
//					} else {
//						sb5.append("," + rs.getString("wlocation_id").trim());
//
//					}
//					cnt++;
//				}
//
//				pst = con
//						.prepareStatement("select * from employee_official_details eod,user_details ud where ud.emp_id=eod.emp_id and ud.usertype_id=? and wlocation_id in("
//								+ sb5.toString() + ")");
//				pst.setInt(1, 7);
//				rs = pst.executeQuery();
//				cnt = 0;
//				while (rs.next()) {
//					if (cnt == 0) {
//						sb.append("," + rs.getString("emp_id").trim() +",");
//					} else {
//						sb.append(rs.getString("emp_id").trim() +",");
//
//					}
//					cnt++;
//				}
//				return sb.toString();
//			} else if (type == 8) {
//
//			} else if (type == 9) {
//
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//			db.closeConnection(con);
//		}
//
//		return null;
//	}

//	private List<String> getOrientationMemberDetails(int id) {
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		ResultSet rs = null;
//		List<String> memberList = new ArrayList<String>();
//		try {
//
//			con = db.makeConnection(con);
//
//			pst = con
//					.prepareStatement("select * from orientation_details where orientation_id=?");
//			pst.setInt(1, id);
//			rs = pst.executeQuery();
//
//			while (rs.next()) {
//				memberList.add(rs.getString("member_id").trim());
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//			db.closeStatements(pst);
//		}
//		return memberList;
//	}

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

	public String getEditID() {
		return editID;
	}

	public void setEditID(String editID) {
		this.editID = editID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAttributevalue() {
		return attributevalue;
	}

	public void setAttributevalue(String attributevalue) {
		this.attributevalue = attributevalue;
	}

	public String getAttribute_id() {
		return attribute_id;
	}

	public void setAttribute_id(String attribute_id) {
		this.attribute_id = attribute_id;
	}

	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public String getStrSessionEmpId() {
		return strSessionEmpId;
	}

	public void setStrSessionEmpId(String strSessionEmpId) {
		this.strSessionEmpId = strSessionEmpId;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getStrDesignationUpdate() {
		return strDesignationUpdate;
	}

	public void setStrDesignationUpdate(String strDesignationUpdate) {
		this.strDesignationUpdate = strDesignationUpdate;
	}

	public String getEmpGrade() {
		return empGrade;
	}

	public void setEmpGrade(String empGrade) {
		this.empGrade = empGrade;
	}

	public String getUserlocation() {
		return userlocation;
	}

	public void setUserlocation(String userlocation) {
		this.userlocation = userlocation;
	}

	public String getFrequencyValue() {
		return frequencyValue;
	}

	public void setFrequencyValue(String frequencyValue) {
		this.frequencyValue = frequencyValue;
	}

	public List<String> getEmpvalue() {
		return empvalue;
	}

	public void setEmpvalue(List<String> empvalue) {
		this.empvalue = empvalue;
	}

	public List<String> getDepartmentvalue() {
		return departmentvalue;
	}

	public void setDepartmentvalue(List<String> departmentvalue) {
		this.departmentvalue = departmentvalue;
	}

	public List<String> getWlocationvalue() {
		return wlocationvalue;
	}

	public void setWlocationvalue(List<String> wlocationvalue) {
		this.wlocationvalue = wlocationvalue;
	}

	public List<String> getGradevalue() {
		return gradevalue;
	}

	public void setGradevalue(List<String> gradevalue) {
		this.gradevalue = gradevalue;
	}

	public List<String> getDesigvalue() {
		return desigvalue;
	}

	public void setDesigvalue(List<String> desigvalue) {
		this.desigvalue = desigvalue;
	}

	public List<String> getLevelvalue() {
		return levelvalue;
	}

	public void setLevelvalue(List<String> levelvalue) {
		this.levelvalue = levelvalue;
	}

	public String getOrientedValue() {
		return orientedValue;
	}

	public void setOrientedValue(String orientedValue) {
		this.orientedValue = orientedValue;
	}

	public String getAppraiselName() {
		return appraiselName;
	}

	public void setAppraiselName(String appraiselName) {
		this.appraiselName = appraiselName;
	}

	public String getAppraisal_description() {
		return appraisal_description;
	}

	public void setAppraisal_description(String appraisal_description) {
		this.appraisal_description = appraisal_description;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public String getStrDepart() {
		return strDepart;
	}

	public void setStrDepart(String strDepart) {
		this.strDepart = strDepart;
	}

	public String getStrWlocation() {
		return strWlocation;
	}

	public void setStrWlocation(String strWlocation) {
		this.strWlocation = strWlocation;
	}

	public String getEmp_status() {
		return emp_status;
	}

	public void setEmp_status(String emp_status) {
		this.emp_status = emp_status;
	}

	public String getAppraisalType() {
		return appraisalType;
	}

	public void setAppraisalType(String appraisalType) {
		this.appraisalType = appraisalType;
	}

	public String getAppraiseeList() {
		return appraiseeList;
	}

	public void setAppraiseeList(String appraiseeList) {
		this.appraiseeList = appraiseeList;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getStartFrom() {
		return startFrom;
	}

	public void setStartFrom(String startFrom) {
		this.startFrom = startFrom;
	}

	public String getEndTo() {
		return endTo;
	}

	public void setEndTo(String endTo) {
		this.endTo = endTo;
	}

	public String getWeekday() {
		return weekday;
	}

	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}

	public String getAppraisal_typeValue() {
		return appraisal_typeValue;
	}

	public void setAppraisal_typeValue(String appraisal_typeValue) {
		this.appraisal_typeValue = appraisal_typeValue;
	}

	public String getAnnualDay() {
		return annualDay;
	}

	public void setAnnualDay(String annualDay) {
		this.annualDay = annualDay;
	}

	public String getAnnualMonth() {
		return annualMonth;
	}

	public void setAnnualMonth(String annualMonth) {
		this.annualMonth = annualMonth;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getMonthday() {
		return monthday;
	}

	public void setMonthday(String monthday) {
		this.monthday = monthday;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getAppraisal_instruction() {
		return appraisal_instruction;
	}

	public void setAppraisal_instruction(String appraisal_instruction) {
		this.appraisal_instruction = appraisal_instruction;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getQuediv() {
		return quediv;
	}

	public void setQuediv(String quediv) {
		this.quediv = quediv;
	}

	public String getOthrquetype() {
		return othrquetype;
	}

	public void setOthrquetype(String othrquetype) {
		this.othrquetype = othrquetype;
	}

	public String getQueno() {
		return queno;
	}

	public void setQueno(String queno) {
		this.queno = queno;
	}

	public String getAnsid() {
		return ansid;
	}

	public void setAnsid(String ansid) {
		this.ansid = ansid;
	}

	public String getSelectanstype() {
		return selectanstype;
	}

	public void setSelectanstype(String selectanstype) {
		this.selectanstype = selectanstype;
	}

	public String getTotWeightage() {
		return totWeightage;
	}

	public void setTotWeightage(String totWeightage) {
		this.totWeightage = totWeightage;
	}

	public String getSectionID() {
		return sectionID;
	}

	public void setSectionID(String sectionID) {
		this.sectionID = sectionID;
	}

	public String getSubsectionID() {
		return subsectionID;
	}

	public void setSubsectionID(String subsectionID) {
		this.subsectionID = subsectionID;
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
