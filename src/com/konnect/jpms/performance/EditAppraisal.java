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

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class EditAppraisal implements ServletRequestAware, IStatements {
	HttpSession session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;

	
	private  List<FillAttribute> attributeList;
	private List<FillAnswerType> ansTypeList;
	private List<FillFrequency> frequencyList;

	private String id;
	
	private String oreinted;
	private String oldOrientVal;
	private String appsystem;
	private String scoreType;
	private String editID;
	private String type;

	private String attributevalue;
	private String attribute_id;

	private List<FillOrientation> orientationList;

	private List<FillLevel> levelList;
	private List<FillWLocation> workList;
	private List<FillDepartment> departmentList;
	private List<FillDesig> desigList;
	private List<FillGrade> gradeList;
	private List<FillEmployee> empList;
	private List<FillEmployee> finalizationList;
	private List<FillEmployee> reviewerList;
	

	private String strLevel;
	private String strDesignationUpdate;
	private String empGrade;
	private String userlocation;

	private String frequencyValue;

	private List<String> empvalue = new ArrayList<String>();
	private List<String> departmentvalue = new ArrayList<String>();
	private List<String> wlocationvalue = new ArrayList<String>();
	private List<String> gradevalue = new ArrayList<String>();
	private List<String> desigvalue = new ArrayList<String>();
	private List<String> levelvalue = new ArrayList<String>();
	private List<String> reviewervalue = new ArrayList<String>();
	private String orientedValue;

	private String appraiselName;
	private String appraisal_description;
	private String employee;
	private String strDepart;
	private String strWlocation;
	private String emp_status;

	private String appraisalType;
//	private String appraiseeList;

	private String frequency;
	private String from;
	private String to;
	private String eligibilityMinDaysBeforeStartDate;
	private String eligibilityMinDaysBeforeEndDate;

	private String startFrom;
	private String endTo;
	private String weekday;
	
	private String appraisal_typeValue;
	
	private String annualDay;
	private String annualMonth;
	private String day;
	private String monthday;
	private String month;
	
	private String weekdayValue;
	private String annualDayValue;
	private String annualMonthValue;
	private String dayValue;
	private String monthdayValue;
	private List<String> monthValue = new ArrayList<String>(); 
	
	private String appraisal_instruction;
	private String strOrg;
	
	private String reviewerId;
	
	private String hideSelfFillEmpIds;

	private String anonymousReview;
	
	private List<FillOrganisation> organisationList;
	private String appFreqId;
	private String fromPage;
	public String execute() {
		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";

		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/performance/EditAppraisal.jsp");
		request.setAttribute(TITLE, "Edit Appraisal");

		String levelID=getSelfIDs(getId());
//		System.out.println("levelID====>"+levelID);
		if (levelID != null && levelID.length()>0) {
			attributeList = new FillAttribute(request).fillElementAttribute(levelID);
		}else{				
			attributeList = new FillAttribute(request).fillElementAttribute(null);
		}
		ansTypeList = new FillAnswerType(request).fillAnswerType();
		String result = LOAD;
//		getAppraisalDetail();
		//getOrientationValue(uF.parseToInt(getOreinted()));
		getattribute();
		getOtherAnsType();
		getAppraisalQuestionList();
		getMainLevelData1();
		String submit = request.getParameter("submit");
	/*	System.out.println("id=====> " + id);
//		System.out.println("appsystem=====> " + appsystem);
		System.out.println("scoreType=====> " + scoreType);
		System.out.println("type=====> " + type); 
		System.out.println("submit=====> " + submit);*/
		if (appsystem != null && appsystem.equals("appraisal")) {
			if (submit != null && submit.equals("Save")) {
				result = editAppraisal();
//				
//				return result;
				
			} else {
				getAppraisalData();
				initialize(uF);
			}

		}else if (appsystem != null && appsystem.equals("editexistapp")) {
			if (submit != null && submit.equals("Save")) {
				result =  editAppraisal();
//				return "success";
			} else {
				CreateAppraisalFromTemplate template = new CreateAppraisalFromTemplate();
				template.setServletRequest(request);
				template.setExistID(id);
				template.execute();
				setId(template.getId());
//				System.out.println("getId() ===> "+getId());
				getAppraisalData();
				initialize(uF);
			}

		} else if (appsystem != null && appsystem.equals("1")) {
			if (scoreType != null && scoreType.equals("1")) {
				if (type != null && type.equals("score")) {
					if (submit != null && submit.equals("Save")) {
						editScoreCard();
						editGoal();
						editObjective();
						editMeasure();
						editScoreQuestions();
						result = "success";
						//return "success";
					} else {
						getScoreObjectiveMeasureGoal();
					}
				} else if (type != null && type.equals("goal")) {
					if (submit != null && submit.equals("Save")) {
						editGoal();
						editObjective();
						editMeasure();
						editQuestions();
						result = "success";
					} else {
						getObjectiveMeasureGoal();
					}
				} else if (type != null && type.equals("objective")) {
					if (submit != null && submit.equals("Save")) {
						editObjective();
						editMeasure();
						editQuestions();
						result = "success";
					} else {
						getObjectiveMeasure();
					}
				} else if (type != null && type.equals("measure")) {
					if (submit != null && submit.equals("Save")) {
						editMeasure();
						editQuestions();
						result = "success";
					} else {
						getMeasure();
					}
				} else if (type != null && type.equals("quest")) {
					if (submit != null && submit.equals("Save")) {
						editQuestions();
						result = "success";
					} else {
						getQuestions();
					}
				}
			} else if (scoreType != null && scoreType.equals("3")) {
				if (type != null && type.equals("score")) {
					if (submit != null && submit.equals("Save")) {
						editScoreCard();
						editGoal();
						editMeasure();
						editScoreQuestions();
						result = "success";
					} else {
						getScoreMeasureGoal();
					}
				} else if (type != null && type.equals("goal")) {
					if (submit != null && submit.equals("Save")) {
						editGoal();
						editMeasure();
						editQuestions();
						result = "success";
					} else {
						getMeasureGoal();
					}
				} else if (type != null && type.equals("measure")) {
					if (submit != null && submit.equals("Save")) {
						editMeasure();
						editQuestions();
						result = "success";
					} else {
						getMeasure();
					}
				} else if (type != null && type.equals("quest")) {
					if (submit != null && submit.equals("Save")) {
						editQuestions();
						result = "success";
					} else {
						getQuestions();
					}
				}
			} else if (scoreType != null && scoreType.equals("2")) {
				if (type != null && type.equals("score")) {
					if (submit != null && submit.equals("Save")) {
						editScoreCard();
						editMeasure();
						editScoreQuestions();
						result = "success";
					} else {
						getScoreMeasure();
					}
				} else if (type != null && type.equals("measure")) {
					if (submit != null && submit.equals("Save")) {
						editMeasure();
						editQuestions();
						result = "success";
					} else {
						getMeasure();
					}
				} else if (type != null && type.equals("quest")) {
					if (submit != null && submit.equals("Save")) {
						editQuestions();
						result = "success";
					} else {
						getQuestions();
					}
				}
			}
		} else if (appsystem != null && appsystem.equals("2")) {
			if (submit != null && submit.equals("Save")) {
				editOtherQuestions();
				result = "success";
				
			} else {
				getQuestions();
			}
		}

		request.setAttribute("result",result);
		return result;
	}

	
	private void getMainLevelData1() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			//System.out.println("pst ============ > "+pst);
			rs = pst.executeQuery();
			List<List<String>> mainLevelList1 = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("main_level_id"));
				innerList.add(rs.getString("level_title"));
				innerList.add(rs.getString("short_description"));
				innerList.add(rs.getString("long_description"));
				innerList.add(rs.getString("appraisal_id"));
				
				mainLevelList1.add(innerList);
				//System.out.println("mainLevelList1 in java ============ > "+mainLevelList1);
			}
			rs.close();
			pst.close();
			request.setAttribute("mainLevelList1", mainLevelList1);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getAppraisalData() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);

			List<String> appraisalData = new ArrayList<String>();
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			List<String> alDates = new ArrayList<String>();
			rs = pst.executeQuery();
			while (rs.next()) {
				alDates.add(rs.getString("from_date"));
				alDates.add(rs.getString("to_date"));
				alDates.add(rs.getString("eligibility_min_daysbefore_start_date"));
				alDates.add(rs.getString("eligibility_min_daysbefore_end_date"));
				
				appraisalData.add(uF.showData(rs.getString("appraisal_details_id"), ""));//0
				appraisalData.add(uF.showData(rs.getString("appraisal_name"), ""));//1
				appraisalData.add(uF.showData(rs.getString("oriented_type"), ""));//2
				orientedValue = rs.getString("oriented_type");

				appraisalData.add(uF.showData(rs.getString("self_ids"), ""));//3
				List<String> empvalue1 = new ArrayList<String>();
				if (rs.getString("self_ids") == null) {
					empvalue1.add("0");
				} else {
				empvalue1 = Arrays.asList(rs.getString("self_ids").split(","));
				}				
				if (empvalue1 != null) {
					for (int i = 1; i < empvalue1.size(); i++) {
						empvalue.add(empvalue1.get(i).trim());
					}
				} else {
					empvalue.add("0");
				}

				appraisalData.add(uF.showData(rs.getString("level_id"), ""));//4
				List<String> levelvalue1 = new ArrayList<String>();
				if (rs.getString("level_id") == null) {
					levelvalue1.add("0");
				} else {
					levelvalue1 = Arrays.asList(rs.getString("level_id").split(","));
				}
				if (levelvalue1 != null) {
					for (int i = 0; i < levelvalue1.size(); i++) {
						levelvalue.add(levelvalue1.get(i).trim());
					}
				} else {
					levelvalue.add("0");
				}
				
				
				appraisalData.add(uF.showData(rs.getString("desig_id"), ""));//5
				List<String> desigvalue1 = new ArrayList<String>();
				if (rs.getString("desig_id") == null) {
					desigvalue1.add("0");
				} else {
					desigvalue1 = Arrays.asList(rs.getString("desig_id").split(","));
				}
				if (desigvalue1 != null) {
					for (int i = 0; i < desigvalue1.size(); i++) {
						desigvalue.add(desigvalue1.get(i).trim());
					}
				} else {
					desigvalue.add("0");
				}
				
				appraisalData.add(uF.showData(rs.getString("grade_id"), ""));//6
				List<String> gradevalue1 = new ArrayList<String>();
				if (rs.getString("grade_id") == null) {
					gradevalue1.add("0");
				} else {
					gradevalue1 = Arrays.asList(rs.getString("grade_id").split(","));
				}
				if (gradevalue1 != null) {
					for (int i = 0; i < gradevalue1.size(); i++) {
						gradevalue.add(gradevalue1.get(i).trim());
					}
				} else {
					gradevalue.add("0");
				}
				
				if(rs.getString("my_review_status") != null && uF.parseToInt(rs.getString("my_review_status")) == 1) {
					appraisalData.add("1");//7
					frequencyValue = "1";
				} else {
					appraisalData.add(uF.showData(rs.getString("frequency"), ""));//7
					frequencyValue = uF.showData(rs.getString("frequency"), "");
	
					
					if (frequencyValue != null && frequencyValue.equals("2")) {
						weekdayValue = rs.getString("weekday");
						annualDayValue = "";
						annualMonthValue = "";
						dayValue = "";
						monthdayValue = "";
						monthValue.add("");
					} else if (frequencyValue != null && frequencyValue.equals("3")) {
						weekdayValue = "";
						annualDayValue = "";
						annualMonthValue = "";
						dayValue = rs.getString("appraisal_day");
						monthdayValue = "";
						monthValue.add("");
					} else if (frequencyValue != null && (frequencyValue.equals("4") || frequencyValue.equals("5"))) {
						weekdayValue = "";
						annualDayValue = "";
						annualMonthValue = "";
						dayValue = "";
						monthdayValue = rs.getString("appraisal_day");
	
						List<String> monthValue1 = new ArrayList<String>();
							if (rs.getString("appraisal_month") == null) {
								monthValue1.add("");
							} else {
								String appMonths = ","+rs.getString("appraisal_month")+",";
								monthValue1 = Arrays.asList(appMonths.split(","));
							}				
							if (monthValue1 != null) {
								for (int i = 1; i < monthValue1.size(); i++) {
									monthValue.add(monthValue1.get(i).trim());
								}
							} else {
								monthValue.add("0");
							}
							
					} else if (frequencyValue != null && frequencyValue.equals("6")) {
						weekdayValue = "";
						annualDayValue = rs.getString("appraisal_day");
						annualMonthValue = rs.getString("appraisal_month");
						dayValue = "";
						monthdayValue = "";
						monthValue.add("");
					}
				}
				appraisalData.add(uF.showData(rs.getString("supervisor_id"), ""));//8
				appraisalData.add(uF.showData(rs.getString("peer_ids"), ""));//9
				appraisalData.add(uF.showData(rs.getString("self_ids"), ""));//10
				appraisalData.add(uF.showData(rs.getString("emp_status"), ""));//11
				appraisalData.add(uF.showData(rs.getString("appraisal_type"),"")); //12
				appraisal_typeValue = uF.showData(rs.getString("appraisal_type"), "");
				
				appraisalData.add(uF.showData(rs.getString("added_by"), ""));//13
				appraisalData.add(uF.showData(rs.getString("entry_date"), ""));//14
				appraisalData.add(uF.showData(rs.getString("wlocation_id"), "")); //15
				List<String> wlocationvalue1 = new ArrayList<String>();
				if (rs.getString("wlocation_id") == null) {
					wlocationvalue1.add("0");
				} else {
					wlocationvalue1 = Arrays.asList(rs.getString("wlocation_id").split(","));
				}
				if (wlocationvalue1 != null) {
					for (int i = 0; i < wlocationvalue1.size(); i++) {
						wlocationvalue.add(wlocationvalue1.get(i).trim());
					}
				} else {
					wlocationvalue.add("0");
				}
				
				appraisalData.add(uF.showData(rs.getString("department_id"), ""));//16
				List<String> departmentvalue1 = new ArrayList<String>();
				if (rs.getString("department_id") == null) {
					departmentvalue1.add("0");
				} else {
					departmentvalue1 = Arrays.asList(rs.getString("department_id").split(","));
				}
				if (departmentvalue1 != null) {
					for (int i = 0; i < departmentvalue1.size(); i++) {
						departmentvalue.add(departmentvalue1.get(i).trim());
					}
				} else {
					departmentvalue.add("0");
				}
				
				appraisalData.add(uF.showData(uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT), "")); //17
				setFrom(uF.showData(uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT), ""));
				appraisalData.add(uF.showData(uF.getDateFormat(rs.getString("to_date"), DBDATE, DATE_FORMAT), "")); //18
				setTo(uF.showData(uF.getDateFormat(rs.getString("to_date"), DBDATE, DATE_FORMAT), ""));
				appraisalData.add(uF.showData(rs.getString("monthfrom"), "")); //19
				appraisalData.add(uF.showData(rs.getString("monthto"), "")); //20
				appraisalData.add(uF.showData(rs.getString("weekday"), "")); //21
				appraisalData.add(uF.showData(rs.getString("plan_type"), "")); //22
				appraisalData.add(uF.showData(rs.getString("hr_ids"), "")); //23
				appraisalData.add(uF.showData(rs.getString("usertype_member"), "")); //24
				appraisalData.add(uF.showData(rs.getString("appraisal_description"), "")); //25
				appraisalData.add(uF.showData(rs.getString("is_publish"), "")); //26
				appraisalData.add(uF.showData(rs.getString("appraisal_instruction"), "")); //27
				Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
				
				List<String> managerLst = new ArrayList<String>();
				if (rs.getString("supervisor_id") != null && rs.getString("supervisor_id").length() > 0) {
					List<String> manager=Arrays.asList(rs.getString("supervisor_id").split(","));
					for(int i=0;manager!=null && !manager.isEmpty() && i<manager.size();i++) {
						managerLst.add(manager.get(i).trim());
					}
				}
				String managerList = uF.showData(getAppendData11(managerLst, hmEmpName), "");
				
				List<String> hrLst = new ArrayList<String>();
				if (rs.getString("hr_ids") != null && rs.getString("hr_ids").length() > 0) {
					List<String> hr=Arrays.asList(rs.getString("hr_ids").split(","));
					for(int i=0;hr!=null && !hr.isEmpty() && i<hr.size();i++) {
						hrLst.add(hr.get(i).trim());
					}
				}
				String hrList = uF.showData(getAppendData11(hrLst, hmEmpName), "");
				
				List<String> peerLst = new ArrayList<String>();
				if (rs.getString("peer_ids") != null && rs.getString("peer_ids").length() > 0) {
					List<String> peer=Arrays.asList(rs.getString("peer_ids").split(","));
					for(int i=0;peer!=null && !peer.isEmpty() && i<peer.size();i++) {
						peerLst.add(peer.get(i).trim());
					}
				}
				String peerList = uF.showData(getAppendData11(peerLst, hmEmpName), "");
				List<String> otherLst = new ArrayList<String>();
				if (rs.getString("other_ids") != null && rs.getString("other_ids").length() > 0) {
					List<String> other=Arrays.asList(rs.getString("other_ids").split(","));
					for(int i=0;other!=null && !other.isEmpty() && i<other.size();i++) {
						otherLst.add(other.get(i).trim());
					}
				}
				String otherList = uF.showData(getAppendData11(otherLst, hmEmpName), "");
				appraisalData.add(uF.showData(managerList, "")); //28
				appraisalData.add(uF.showData(hrList, "")); //29
				appraisalData.add(uF.showData(peerList, "")); //30
				appraisalData.add(uF.showData(otherList, "")); //31
				if(rs.getInt("oriented_type") == 5) {
					appraisalData.add(uF.showData(getAppendDataEmpName(rs.getString("employee_id"), hmEmpName), "")); //32
				} else {
					appraisalData.add(""); //32
				}
				appraisalData.add(uF.showData(rs.getString("hr_ids"), "")); //33
				appraisalData.add(uF.showData(rs.getString("supervisor_id"), "")); //34
				appraisalData.add(uF.showData(rs.getString("peer_ids"), "")); //35
				appraisalData.add(uF.showData(rs.getString("other_ids"), "")); //36
				
				List<String> ceoLst = new ArrayList<String>();
				if (rs.getString("ceo_ids") != null && rs.getString("ceo_ids").length() > 0) {
					List<String> ceo=Arrays.asList(rs.getString("ceo_ids").split(","));
					for(int i=0;ceo!=null && !ceo.isEmpty() && i<ceo.size();i++) {
						ceoLst.add(ceo.get(i).trim());
					}
				}
				String ceoList = uF.showData(getAppendData11(ceoLst, hmEmpName), "");
				
				List<String> hodLst = new ArrayList<String>();
				if (rs.getString("hod_ids") != null && rs.getString("hod_ids").length() > 0) {
					List<String> hod=Arrays.asList(rs.getString("hod_ids").split(","));
					for(int i=0;hod!=null && !hod.isEmpty() && i<hod.size();i++) {
						hodLst.add(hod.get(i).trim());
					}
				}
				String hodList = uF.showData(getAppendData11(hodLst, hmEmpName), "");
				
				appraisalData.add(uF.showData(ceoList, "")); //37
				appraisalData.add(uF.showData(hodList, "")); //38
				appraisalData.add(uF.showData(rs.getString("ceo_ids"), "")); //39
				appraisalData.add(uF.showData(rs.getString("hod_ids"), "")); //40
				
				appraisalData.add(uF.showData(rs.getString("close_reason"), "")); //41
				appraisalData.add(uF.showData(rs.getString("is_publish"), "")); //42
				appraisalData.add(uF.showData(rs.getString("publish_expire_status"), "")); //43
				appraisalData.add(uF.showData(rs.getString("is_close"), "")); //44
				appraisalData.add(uF.showData(rs.getString("my_review_status"), "")); //45
				
				setHideSelfFillEmpIds(uF.showData(rs.getString("employee_id"), ""));
				
				appraisalData.add(uF.showData(rs.getString("reviewer_id"), ""));//46
				appraisalData.add(uF.showData(rs.getString("eligibility_min_daysbefore_start_date"), "0"));//47
				appraisalData.add(uF.showData(rs.getString("eligibility_min_daysbefore_end_date"), "0"));//48
				appraisalData.add(rs.getString("is_anonymous_review"));//49
				
				List<String> reviewervalue1 = new ArrayList<String>();
				if (rs.getString("reviewer_id") == null) {
					reviewervalue1.add("0");
				} else {
					reviewervalue1 = Arrays.asList(rs.getString("reviewer_id").split(","));
				}				
				if (reviewervalue1 != null) {
					for (int i = 1; i < reviewervalue1.size(); i++) {
						reviewervalue.add(reviewervalue1.get(i).trim());
					}
				} else {
					reviewervalue.add("0");
				}
				
			}
			rs.close();
			pst.close();
			request.setAttribute("alDates", alDates);
			
			
			List<String> alOrientationMembers = CF.getOrientationMembers(con, uF, request, getOrientedValue());
			
			StringBuilder sbAllRevieweePanalist = new StringBuilder(); //table_no_border 
			sbAllRevieweePanalist.append("<table class=\"table table-bordered autoWidth\" width=\"100%\">" +
					"<tr><th>Reviewee Name:</th>");
			
				if(alOrientationMembers.contains("Sub-ordinate")) {
					sbAllRevieweePanalist.append("<th>Sub-ordinates:</th>");
				}
				if(alOrientationMembers.contains("Peer")) {
					sbAllRevieweePanalist.append("<th>Peers:</th>");
				}
				if(alOrientationMembers.contains("Other Peer")) {
					sbAllRevieweePanalist.append("<th>Other Peers:</th>");
				}
				if(alOrientationMembers.contains("Manager")) {
					sbAllRevieweePanalist.append("<th>Managers:</th>");
				}
				if(alOrientationMembers.contains("GroupHead")) {
					sbAllRevieweePanalist.append("<th>Grand Managers:</th>");
				}
				if(alOrientationMembers.contains("HOD")) {
					sbAllRevieweePanalist.append("<th>HODs:</th>");
				}
				if(alOrientationMembers.contains("CEO")) {
					sbAllRevieweePanalist.append("<th>CEOs:</th>");
				}
				if(alOrientationMembers.contains("HR")) {
					sbAllRevieweePanalist.append("<th>HRs:</th>");
				}
				if(alOrientationMembers.contains("Anyone")) {
					sbAllRevieweePanalist.append("<th>Anyone:</th>");
	//			} else if(alOrientationMembers.contains("Managers")) {
					
				}
				sbAllRevieweePanalist.append("</tr>");
			
				pst = con.prepareStatement("select * from appraisal_reviewee_details where appraisal_id=?");
				pst.setInt(1, uF.parseToInt(getId()));
				rs = pst.executeQuery();
				Map<String, List<String>> hmRevieweePanel = new LinkedHashMap<String, List<String>>();
				while(rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("subordinate_ids")); //0
					innerList.add(rs.getString("peer_ids")); //1
					innerList.add(rs.getString("other_peer_ids")); //2
					innerList.add(rs.getString("supervisor_ids")); //3
					innerList.add(rs.getString("grand_supervisor_ids")); //4
					innerList.add(rs.getString("hod_ids")); //5
					innerList.add(rs.getString("ceo_ids")); //6
					innerList.add(rs.getString("hr_ids")); //7
					innerList.add(rs.getString("other_ids")); //8
					innerList.add(rs.getString("ghr_ids")); //9
					innerList.add(rs.getString("recruiter_ids")); //10
					hmRevieweePanel.put(rs.getString("reviewee_id"), innerList);
				}
				rs.close();
				pst.close();

//				System.out.println("hmRevieweePanel ===>> " + hmRevieweePanel);
				Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
				Iterator<String> it = hmRevieweePanel.keySet().iterator();
				while(it.hasNext()) {
					String revieweeId = it.next();
					List<String> innerList = hmRevieweePanel.get(revieweeId);
//					System.out.println("innerList ===>> " + innerList);
					if(uF.parseToInt(revieweeId)>0 && innerList!=null) {
						sbAllRevieweePanalist.append("<tr>");
						sbAllRevieweePanalist.append("<td>"+hmEmpName.get(revieweeId)+"</td>");
						
						if(alOrientationMembers.contains("Sub-ordinate")) {
							sbAllRevieweePanalist.append("<td>"+getAppendDataRevieweePanel(uF, revieweeId, innerList.get(0), hmEmpName, "Sub-ordinate")+"</td>");
						}
						if(alOrientationMembers.contains("Peer")) {
							sbAllRevieweePanalist.append("<td>"+getAppendDataRevieweePanel(uF, revieweeId, innerList.get(1), hmEmpName, "Peer")+"</td>");
						}
						if(alOrientationMembers.contains("Other Peer")) {
							sbAllRevieweePanalist.append("<td>"+getAppendDataRevieweePanel(uF, revieweeId, innerList.get(2), hmEmpName, "Other Peer")+"</td>");
						}
						if(alOrientationMembers.contains("Manager")) {
							sbAllRevieweePanalist.append("<td>"+getAppendDataRevieweePanel(uF, revieweeId, innerList.get(3), hmEmpName, "Manager")+"</td>");
						}
						if(alOrientationMembers.contains("GroupHead")) {
							sbAllRevieweePanalist.append("<td>"+getAppendDataRevieweePanel(uF, revieweeId, innerList.get(4), hmEmpName, "GroupHead")+"</td>");
						}
						if(alOrientationMembers.contains("HOD")) {
							sbAllRevieweePanalist.append("<td>"+getAppendDataRevieweePanel(uF, revieweeId, innerList.get(5), hmEmpName, "HOD")+"</td>");
						}
						if(alOrientationMembers.contains("CEO")) {
							sbAllRevieweePanalist.append("<td>"+getAppendDataRevieweePanel(uF, revieweeId, innerList.get(6), hmEmpName, "CEO")+"</td>");
						}
						if(alOrientationMembers.contains("HR")) {
							sbAllRevieweePanalist.append("<td>"+getAppendDataRevieweePanel(uF, revieweeId, innerList.get(7), hmEmpName, "HR")+"</td>");
						}
						if(alOrientationMembers.contains("Anyone")) {
							sbAllRevieweePanalist.append("<td>"+getAppendDataRevieweePanel(uF, revieweeId, innerList.get(8), hmEmpName, "Anyone")+"</td>");
							
						}
						sbAllRevieweePanalist.append("</tr>");
					}
				}
				sbAllRevieweePanalist.append("</table>");
				
//				System.out.println("sbAllRevieweePanalist ===>> " + sbAllRevieweePanalist.toString());
				request.setAttribute("sbAllRevieweePanalist", sbAllRevieweePanalist.toString());
				
				List<String> memberList = CF.getOrientationMemberDetails(con,uF.parseToInt(orientedValue));
				request.setAttribute("memberList", memberList);
			
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);	
			request.setAttribute("hmOrientMemberID", hmOrientMemberID);
			request.setAttribute("appraisalData", appraisalData);
		} catch (SQLException e) {
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
	

	private String editAppraisal() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String strReturn= "success";
		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);	
			List<String> selfList = getSelfList();
			List<String> innerList = new ArrayList<String>();
//			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select * from appraisal_details where appraisal_details_id = ?");
			pst = con.prepareStatement(strQuery.toString());
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				innerList.add(uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT));//0
				innerList.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, DATE_FORMAT));//1
				innerList.add(rs.getString("frequency"));//2
				innerList.add(rs.getString("usertype_member"));//3
				innerList.add(rs.getString("appraisal_day")); //4
				innerList.add(rs.getString("weekday"));//5
				innerList.add(rs.getString("my_review_status"));//6
			}
			rs.close();
			pst.close();
			
//			appraiseeList = uF.showData(getAppendData(con, selfList, hmEmpName), "");
			StringBuilder sb2 = new StringBuilder();
			for (int i = 0; i < selfList.size(); i++) {
				if (i == 0) {
					sb2.append("," + selfList.get(i).trim()+",");
				} else {
					sb2.append(selfList.get(i).trim()+",");
				}
			}

			List<String> memberList = getOrientationMemberDetails(uF.parseToInt(getOreinted()));
			StringBuilder members = new StringBuilder();
			for (int i = 0; i < memberList.size(); i++) {
				if (i == 0)
					members.append(memberList.get(i).trim());
				else
					members.append("," + memberList.get(i).trim());
			}

			//weekday annualDay annualMonth day monthday month
			String appraisal_day=null;
			String appraisal_month=null;
			String weeklyDay=null;
			if(frequency == null || frequency.equals("")) {
				setFrequency("1");
			}
			
			if(frequency!=null && frequency.equals("2")) { 
				weeklyDay=weekday;
				appraisal_day=null;
				appraisal_month=null;
			}else if(frequency!=null && frequency.equals("3")) {
				weeklyDay=null;
				appraisal_day=day;
				appraisal_month=null;
			}else if(frequency!=null && frequency.equals("4")) {
				weeklyDay=null;
				appraisal_day=monthday;
				appraisal_month=month;
			}else if(frequency!=null && frequency.equals("5")) {
				weeklyDay=null;
				appraisal_day=monthday;
				appraisal_month=month;
			}else if(frequency!=null && frequency.equals("6")) {
				weeklyDay=null;
				appraisal_day=annualDay;
				appraisal_month=annualMonth;
			}
			 
			String hrIds = request.getParameter("hidehrIdEdit");
			String managerIds = request.getParameter("hidemanagerIdEdit");
			String peerIds = request.getParameter("hidepeerIdEdit");
			String otherIds = request.getParameter("hideotherIdEdit");
			String ceoIds = request.getParameter("hideCeoIdEdit");
			String hodIds = request.getParameter("hideHodIdEdit");
//			System.out.println("hrIds ===> "+hrIds);
//			System.out.println("managerIds ===> "+managerIds);
//			System.out.println("peerIds ===> "+peerIds);
//			System.out.println("otherIds ===> "+otherIds);
			
			String bothHRIds = getEmployeeList(hrIds, sb2.toString(), uF.parseToInt(hmOrientMemberID.get("HR")));
			String bothManagerIds = getEmployeeList(managerIds, sb2.toString(), uF.parseToInt(hmOrientMemberID.get("Manager")));
			String bothPeerIds = getEmployeeList(peerIds, sb2.toString(), uF.parseToInt(hmOrientMemberID.get("Peer")));
			String bothCeoIds = getEmployeeList(ceoIds, sb2.toString(), uF.parseToInt(hmOrientMemberID.get("CEO")));
			String bothHodIds = getEmployeeList(hodIds, sb2.toString(), uF.parseToInt(hmOrientMemberID.get("HOD")));
			
			StringBuilder sbReviewers = null;
			if (getReviewerId() != null && getReviewerId().length() > 0) {
				List<String> alReviewers = Arrays.asList(getReviewerId().split(","));
				for(int i=0; alReviewers!=null && !alReviewers.isEmpty() && i<alReviewers.size();i++) {
					if(sbReviewers == null) {
						sbReviewers = new StringBuilder();
						sbReviewers.append("," + alReviewers.get(i).trim()+",");
					} else {
						sbReviewers.append(alReviewers.get(i).trim()+",");
					}
				}
			}
			if(sbReviewers == null) {
				sbReviewers = new StringBuilder();
			}
			
			pst = con.prepareStatement("update appraisal_details set appraisal_name=?,oriented_type=?,employee_id=?,level_id=?,desig_id=?,grade_id=?," +
				"wlocation_id=?,department_id=?,supervisor_id=?,peer_ids=?,self_ids=?,emp_status=?,appraisal_type=?,added_by=?,entry_date=?," +
				"frequency=?,from_date=?,to_date=?,appraisal_day=?,appraisal_month=?,weekday=?,hr_ids=?,usertype_member=?,appraisal_description=?," +
				"appraisal_instruction=?,other_ids=? ,ceo_ids=?,hod_ids=?, reviewer_id=?,eligibility_min_daysbefore_start_date=?," +
				"eligibility_min_daysbefore_end_date=?,is_anonymous_review=? where appraisal_details_id=? ");
			pst.setString(1, getAppraiselName());
//			System.out.println("getOreinted()===>"+getOreinted());
			pst.setString(2, getOreinted());
//			pst.setString(3, getEmployee());
			if(uF.parseToInt(getOreinted()) == 5 && getHideSelfFillEmpIds() != null) {
				pst.setString(3, getHideSelfFillEmpIds());
			} else {
				if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Self")!=null  && memberList.contains(hmOrientMemberID.get("Self"))) {
					pst.setString(3, sb2.toString());
				} else {
					pst.setString(3, "");
				}
			}
//			System.out.println("sb2.toString()=======>"+sb2.toString());
			pst.setString(4, getStrLevel());
			pst.setString(5, getStrDesignationUpdate());
			pst.setString(6, getEmpGrade());
			pst.setString(7, getStrWlocation());
			pst.setString(8, getStrDepart());
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Manager")!=null && memberList.contains(hmOrientMemberID.get("Manager"))) {
				pst.setString(9, bothManagerIds);
			} else {
				pst.setString(9, "");
			}
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Peer")!=null && memberList.contains(hmOrientMemberID.get("Peer"))) {
				pst.setString(10, bothPeerIds);
			} else {
				pst.setString(10, "");
			}
			pst.setString(11, sb2.toString());
			pst.setString(12, getEmp_status());
			pst.setString(13, getAppraisalType());
			pst.setInt(14, uF.parseToInt(strSessionEmpId));
			pst.setDate(15, uF.getCurrentDate(CF.getStrTimeZone()));

			pst.setString(16, frequency);
			pst.setDate(17, uF.getDateFormat(getFrom(), DATE_FORMAT));
			pst.setDate(18, uF.getDateFormat(getTo(), DATE_FORMAT));
			pst.setString(19, appraisal_day);
			pst.setString(20, appraisal_month);
			pst.setString(21, weekday);
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HR")!=null  && memberList.contains(hmOrientMemberID.get("HR"))) {
				pst.setString(22, bothHRIds);
			} else {
				pst.setString(22, "");
			}
		
			pst.setString(23, members.toString());
			pst.setString(24, getAppraisal_description());
			pst.setString(25, getAppraisal_instruction());
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Anyone")!=null  && memberList.contains(hmOrientMemberID.get("Anyone"))) {
				pst.setString(26, otherIds);
			}else{
				pst.setString(26, "");
			}
			
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("CEO")!=null  && memberList.contains(hmOrientMemberID.get("CEO"))) {
				pst.setString(27, bothCeoIds);
			}else{
				pst.setString(27, "");
			}
			
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HOD")!=null  && memberList.contains(hmOrientMemberID.get("HOD"))) {
				pst.setString(28,bothHodIds);
			}else{
				pst.setString(28, "");
			}
			pst.setString(29, sbReviewers.toString());
			pst.setDouble(30, uF.parseToDouble(getEligibilityMinDaysBeforeStartDate()));
			pst.setDouble(31, uF.parseToDouble(getEligibilityMinDaysBeforeEndDate()));
			pst.setBoolean(32, uF.parseToBoolean(getAnonymousReview()));
			pst.setInt(33, uF.parseToInt(getId()));
			pst.executeUpdate();
			pst.close();
			
//			***************************** appraisal Frequency Start ************************************
			if(getFrom() != null && !getFrom().equals("") && getTo() != null && !getTo().equals("")) {
				String strEmpIds = sb2.toString().length()>1 ? sb2.toString().substring(1, sb2.toString().length()-1) : "0";
				Map<String, Map<String, String>> hmEmpReportingData = new HashMap<String, Map<String, String>>();
				pst = con.prepareStatement("select * from employee_official_details where emp_id in ("+strEmpIds+") ");
				rs = pst.executeQuery();
				while(rs.next()) {
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("MANAGER", rs.getString("supervisor_emp_id"));
					hmInner.put("HR", rs.getString("emp_hr"));
					hmInner.put("HOD", rs.getString("hod_emp_id"));
					hmEmpReportingData.put(rs.getString("emp_id"), hmInner);
				}
				rs.close();
				pst.close();
				
				Map<String, List<String>> hmEmpPeerList = new HashMap<String, List<String>>();
				pst = con.prepareStatement("select eod.emp_id,supervisor_emp_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id=eod.emp_id and " +
					"is_alive=true and supervisor_emp_id in (select supervisor_emp_id from employee_official_details where emp_id in ("+strEmpIds+"))");
				rs = pst.executeQuery();
//				System.out.println("pst ===>> " + pst);
				while(rs.next()) {
					List<String> innList = hmEmpPeerList.get(rs.getString("supervisor_emp_id"));
					if(innList==null) innList = new ArrayList<String>();
					innList.add(rs.getString("emp_id"));
					hmEmpPeerList.put(rs.getString("supervisor_emp_id"), innList);
				}
				rs.close();
				pst.close();
				
				StringBuilder sbCEO = null;
				pst = con.prepareStatement("select eod.emp_id from employee_official_details eod, employee_personal_details epd, user_details ud where epd.emp_per_id=eod.emp_id and " +
					" ud.emp_id=eod.emp_id and ud.usertype_id=5 and is_alive=true and org_id in (select org_id from employee_official_details where emp_id in ("+strEmpIds+"))");
				rs = pst.executeQuery();
				while(rs.next()) {
					if(sbCEO==null) {
						sbCEO = new StringBuilder();
						sbCEO.append(rs.getString("emp_id"));
					} else {
						sbCEO.append(","+rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
				if(sbCEO==null) {
					sbCEO = new StringBuilder();
				}
				
				pst = con.prepareStatement("select * from employee_official_details where supervisor_emp_id in ("+strEmpIds+")");
				rs = pst.executeQuery();
				while(rs.next()) {
					Map<String, String> hmInner = hmEmpReportingData.get(rs.getString("supervisor_emp_id"));
					if(hmInner==null) hmInner = new HashMap<String, String>();
					StringBuilder sbTMem = null;
					if(hmInner.get("SUBORDINATE")==null || hmInner.get("SUBORDINATE").isEmpty()) {
						sbTMem = new StringBuilder();
						sbTMem.append(rs.getString("emp_id"));
					} else {
						sbTMem = new StringBuilder();
						sbTMem.append(hmInner.get("SUBORDINATE"));
						sbTMem.append(","+rs.getString("emp_id"));
					}
					hmInner.put("SUBORDINATE", sbTMem.toString());
					hmEmpReportingData.put(rs.getString("supervisor_emp_id"), hmInner);
				}
				rs.close();
				pst.close();
				
				
				boolean flag = false;
				AppraisalScheduler scheduler = new AppraisalScheduler(request, session, CF, uF, strSessionEmpId);
//				System.out.println("frequency ===>> " + frequency +" -- innerList.get(2) ===>> " + innerList.get(2));
				 if(uF.parseToInt(frequency) == 1 && (uF.parseToInt(innerList.get(2)) == uF.parseToInt(frequency))) {
//					 if(!innerList.get(0).equals(getFrom()) || !innerList.get(1).equals(getTo())) {
						StringBuilder sQuery = new StringBuilder();
						sQuery.append("update appraisal_details_frequency set appraisal_start_date=?,appraisal_due_date=?,freq_start_date=?,freq_end_date=? where appraisal_id = ? and appraisal_freq_id = ?");
						pst = con.prepareStatement(sQuery.toString());
						pst.setDate(1, uF.getDateFormat(getFrom(), DATE_FORMAT));
						pst.setDate(2, uF.getDateFormat(getTo(), DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(getFrom(), DATE_FORMAT));
						pst.setDate(4, uF.getFutureDate(uF.getDateFormatUtil(getTo(), DATE_FORMAT), -1));
						pst.setInt(5, uF.parseToInt(getId()));
						pst.setInt(6, uF.parseToInt(getAppFreqId()));
						pst.executeUpdate();
					    pst.close();
						
					    StringBuilder sbRemoveIds = null;
					    List<String> existRevieweeIds = new ArrayList<String>();
					    pst = con.prepareStatement("select reviewee_id from appraisal_reviewee_details where appraisal_id=?");
					    pst.setInt(1, uF.parseToInt(getId()));
					    rs = pst.executeQuery();
					    while(rs.next()) {
					    	if(!existRevieweeIds.contains(rs.getString("reviewee_id"))) {
						    	if(!selfList.contains(rs.getString("reviewee_id"))) {
						    		if(sbRemoveIds == null) {
						    			sbRemoveIds = new StringBuilder();
						    			sbRemoveIds.append(rs.getString("reviewee_id"));
						    		} else {
						    			sbRemoveIds.append(","+rs.getString("reviewee_id"));
						    		}
						    	}
					    		existRevieweeIds.add(rs.getString("reviewee_id"));
					    	}
					    }
					    rs.close();
					    pst.close();
					    
						if(sbRemoveIds != null) {
					    	pst = con.prepareStatement("delete from appraisal_reviewee_details where reviewee_id in ("+sbRemoveIds.toString()+")");
							pst.executeUpdate();
							pst.close();
						}
//					    System.out.println("selfList ===>> " + selfList + " -- existRevieweeIds ===>> "+existRevieweeIds);
					    
						for(int i=0; selfList != null && i<selfList.size(); i++) {
							
							String hrsId = uF.getAppendData(request.getParameterValues("hrsId_"+selfList.get(i)));
							String ceosId = uF.getAppendData(request.getParameterValues("ceosId_"+selfList.get(i)));
							String hodsId = uF.getAppendData(request.getParameterValues("hodsId_"+selfList.get(i)));
							String subordinatesId = uF.getAppendData(request.getParameterValues("subordinatesId_"+selfList.get(i)));
							String managersId = uF.getAppendData(request.getParameterValues("managersId_"+selfList.get(i)));
							String peersId = uF.getAppendData(request.getParameterValues("peersId_"+selfList.get(i)));
							
//							System.out.println("hrsId.length===>>"+hrsId.length + " -- managersId.length===>>"+managersId.length + " -- peersId.length===>>"+peersId.length);
//							System.out.println("hrsId===>>"+hrsId + " -- ceosId===>>"+ceosId + " -- hodsId===>>"+hodsId + " -- subordinatesId===>>"+subordinatesId 
//									+ " -- managersId===>>"+managersId + " -- peersId===>>"+peersId);
							Map<String, String> hmInner = hmEmpReportingData.get(selfList.get(i));
							if(hmInner==null) hmInner = new HashMap<String, String>();
							List<String> innerPeerList = hmEmpPeerList.get(hmInner.get("MANAGER"));
							StringBuilder sbPeerIds = null;
							for(int a=0; innerPeerList!=null && a<innerPeerList.size(); a++) {
								if(uF.parseToInt(innerPeerList.get(a)) == uF.parseToInt(selfList.get(i))){
									continue;
								}
								if(sbPeerIds==null) {
									sbPeerIds = new StringBuilder();
									sbPeerIds.append(innerPeerList.get(a));
								} else {
									sbPeerIds.append(","+innerPeerList.get(a));
								}
							}
							if(sbPeerIds==null) {
								sbPeerIds = new StringBuilder();
							}
							
							
							if(!existRevieweeIds.contains(selfList.get(i))) {
//								pst = con.prepareStatement("insert into appraisal_reviewee_details (appraisal_id,appraisal_freq_id,reviewee_id) values (?,?,?)");
//								pst.setInt(1, uF.parseToInt(getId()));
//								pst.setInt(2, uF.parseToInt(getAppFreqId()));
//								pst.setInt(3, uF.parseToInt(selfList.get(i)));
//								pst.executeUpdate();
//								pst.close();
//								System.out.println("pst ===>> " + pst);
								pst = con.prepareStatement("insert into appraisal_reviewee_details (appraisal_id, appraisal_freq_id,reviewee_id,subordinate_ids," +
									"supervisor_ids,peer_ids,hr_ids,hod_ids,ceo_ids) values (?,?,?,?, ?,?,?,?, ?)"); 
								pst.setInt(1, uF.parseToInt(getId()));
								pst.setInt(2, uF.parseToInt(getAppFreqId()));
								pst.setInt(3, uF.parseToInt(selfList.get(i)));
								if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Sub-ordinate")!= null  && memberList.contains(hmOrientMemberID.get("Sub-ordinate"))) {
									pst.setString(4, (subordinatesId!=null && subordinatesId.length()>0) ? ","+subordinatesId+"," : "");
								} else {
									pst.setString(4, "");
								}
								if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Manager")!= null  && memberList.contains(hmOrientMemberID.get("Manager"))) {
									pst.setString(5, (managersId!=null && managersId.length()>0) ? ","+managersId+"," : "");
								} else {
									pst.setString(5, "");
								}
								if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Peer")!= null && memberList.contains(hmOrientMemberID.get("Peer"))) {
									pst.setString(6, (peersId!=null && peersId.toString().length()>0) ? ","+peersId.toString()+"," : "");
								} else {
									pst.setString(6, "");
								}
								if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HR")!= null  && memberList.contains(hmOrientMemberID.get("HR"))) {
									pst.setString(7, (hrsId!=null && hrsId.length()>0) ? ","+hrsId+"," : "");
								} else {
									pst.setString(7, "");
								}
								if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HOD")!= null  && memberList.contains(hmOrientMemberID.get("HOD"))) {
									pst.setString(8, (hodsId!=null && hodsId.length()>0) ? ","+hodsId+"," : "");
								} else {
									pst.setString(8, "");
								}
								if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("CEO")!= null  && memberList.contains(hmOrientMemberID.get("CEO"))) {
									pst.setString(9, (ceosId!=null && ceosId.toString().length()>0) ? ","+ceosId.toString()+"," : "");
								} else {
									pst.setString(9, "");
								}
//								System.out.println("pst appraisal_reviewee_details set ====> " + pst);
								pst.executeUpdate();
								pst.close();
							} else {

								pst = con.prepareStatement("update appraisal_reviewee_details set subordinate_ids=?,supervisor_ids=?,peer_ids=?," +
									"hr_ids=?,hod_ids=?,ceo_ids=? where appraisal_id=? and appraisal_freq_id=? and reviewee_id=?"); 
								if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Sub-ordinate")!= null  && memberList.contains(hmOrientMemberID.get("Sub-ordinate"))) {
									pst.setString(1, (subordinatesId!=null && subordinatesId.length()>0) ? ","+subordinatesId+"," : "");
								} else {
									pst.setString(1, "");
								}
								if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Manager")!= null  && memberList.contains(hmOrientMemberID.get("Manager"))) {
									pst.setString(2, (managersId!=null && managersId.length()>0) ? ","+managersId+"," : "");
								} else {
									pst.setString(2, "");
								}
								if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Peer")!= null && memberList.contains(hmOrientMemberID.get("Peer"))) {
									pst.setString(3, (peersId!=null && peersId.toString().length()>0) ? ","+peersId.toString()+"," : "");
								} else {
									pst.setString(3, "");
								}
								if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HR")!= null  && memberList.contains(hmOrientMemberID.get("HR"))) {
									pst.setString(4, (hrsId!=null && hrsId.length()>0) ? ","+hrsId+"," : "");
								} else {
									pst.setString(4, "");
								}
								if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HOD")!= null  && memberList.contains(hmOrientMemberID.get("HOD"))) {
									pst.setString(5, (hodsId!=null && hodsId.length()>0) ? ","+hodsId+"," : "");
								} else {
									pst.setString(5, "");
								}
								if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("CEO")!= null  && memberList.contains(hmOrientMemberID.get("CEO"))) {
									pst.setString(6, (ceosId!=null && ceosId.toString().length()>0) ? ","+ceosId.toString()+"," : "");
								} else {
									pst.setString(6, "");
								}
								pst.setInt(7, uF.parseToInt(getId()));
								pst.setInt(8, uF.parseToInt(getAppFreqId()));
								pst.setInt(9, uF.parseToInt(selfList.get(i)));
//								System.out.println("pst appraisal_reviewee_details set ====> " + pst);
								pst.executeUpdate();
								pst.close();
							
							}
						}
//					}
				} else {
					if(uF.parseToInt(innerList.get(2)) != uF.parseToInt(frequency)) {
						scheduler.updateAppraisalDetails(getId());
						strReturn= "freqsuccess";
						flag = true;
					} else if(uF.parseToInt(innerList.get(2)) == 2 && uF.parseToInt(frequency) == 2) {  // Weekly
						if(!innerList.get(5).equals(weeklyDay)) {
							scheduler.updateAppraisalDetails(getId());
							strReturn= "freqsuccess";
							flag = true;
						} else if(!innerList.get(0).equals(getFrom())) { 
							scheduler.updateAppraisalDetails(getId());
							strReturn= "freqsuccess";
							flag = true;
						} else if(!innerList.get(1).equals(getTo())) {
							scheduler.updateAppraisalDetails(getId());
							strReturn= "freqsuccess";
							flag = true;
						}
					} else if(uF.parseToInt(innerList.get(2)) == 3 && uF.parseToInt(frequency) == 3) { // Monthly
							if(!innerList.get(4).equals(appraisal_day)) {
							scheduler.updateAppraisalDetails(getId());
							strReturn= "freqsuccess";
							flag = true;
						} else if(!innerList.get(0).equals(getFrom())) { 
							scheduler.updateAppraisalDetails(getId());
							strReturn= "freqsuccess";
							flag = true;
						} else if(!innerList.get(1).equals(getTo())) {
							scheduler.updateAppraisalDetails(getId());
							strReturn= "freqsuccess";
							flag = true;
						}
					} else if(!innerList.get(0).equals(getFrom())) { 
						scheduler.updateAppraisalDetails(getId());
						strReturn= "freqsuccess";
						flag = true;
					} else if(!innerList.get(1).equals(getTo())) {
						scheduler.updateAppraisalDetails(getId());
						strReturn= "freqsuccess";
						flag = true;
					 }
				 }
				 
				 if(flag) {
					 List<String> appFreqIDs = new ArrayList<String>();
					pst = con.prepareStatement("select appraisal_freq_id from appraisal_details_frequency where appraisal_id=? and is_delete=false");
					pst.setInt(1, uF.parseToInt(getId()));
					rs = pst.executeQuery();
					if(rs.next()) {
						appFreqIDs.add(rs.getString("appraisal_freq_id"));
					}
					
					StringBuilder sbRemoveAFIds = null;
				    List<String> existAFreqIds = new ArrayList<String>();
				    pst = con.prepareStatement("select appraisal_freq_id from appraisal_reviewee_details where appraisal_id=?");
				    pst.setInt(1, uF.parseToInt(getId()));
				    rs = pst.executeQuery();
				    while(rs.next()) {
				    	if(!appFreqIDs.contains(rs.getString("appraisal_freq_id"))) {
				    		if(sbRemoveAFIds == null) {
				    			sbRemoveAFIds = new StringBuilder();
				    			sbRemoveAFIds.append(rs.getString("appraisal_freq_id"));
				    		} else {
				    			sbRemoveAFIds.append(","+rs.getString("appraisal_freq_id"));
				    		}
				    	} else {
				    		if(!existAFreqIds.contains(rs.getString("appraisal_freq_id"))) {
				    			existAFreqIds.add(rs.getString("appraisal_freq_id"));
				    		}
				    	}
				    }
				    rs.close();
				    pst.close();
					
				    if(sbRemoveAFIds != null) {
				    	pst = con.prepareStatement("delete from appraisal_reviewee_details where appraisal_freq_id in ("+sbRemoveAFIds.toString()+")");
						pst.executeUpdate();
						pst.close();
					}
				    
				    
					StringBuilder sbRemoveIds = null;
				    List<String> existRevieweeIds = new ArrayList<String>();
				    pst = con.prepareStatement("select reviewee_id from appraisal_reviewee_details where appraisal_id=?");
				    pst.setInt(1, uF.parseToInt(getId()));
				    rs = pst.executeQuery();
				    while(rs.next()) {
				    	if(!existRevieweeIds.contains(rs.getString("reviewee_id"))) {
					    	if(!selfList.contains(rs.getString("reviewee_id"))) {
					    		if(sbRemoveIds == null) {
					    			sbRemoveIds = new StringBuilder();
					    			sbRemoveIds.append(rs.getString("reviewee_id"));
					    		} else {
					    			sbRemoveIds.append(","+rs.getString("reviewee_id"));
					    		}
					    	}
			    			existRevieweeIds.add(rs.getString("reviewee_id"));
			    		}
				    }
				    rs.close();
				    pst.close();
				    
					if(sbRemoveIds != null) {
				    	pst = con.prepareStatement("delete from appraisal_reviewee_details where reviewee_id in ("+sbRemoveIds.toString()+")");
						pst.executeUpdate();
						pst.close();
					}
				    
					for(int i=0; selfList != null && i<selfList.size(); i++) {
						
						String hrsId = uF.getAppendData(request.getParameterValues("hrsId_"+selfList.get(i)));
						String ceosId = uF.getAppendData(request.getParameterValues("ceosId_"+selfList.get(i)));
						String hodsId = uF.getAppendData(request.getParameterValues("hodsId_"+selfList.get(i)));
						String subordinatesId = uF.getAppendData(request.getParameterValues("subordinatesId_"+selfList.get(i)));
						String managersId = uF.getAppendData(request.getParameterValues("managersId_"+selfList.get(i)));
						String peersId = uF.getAppendData(request.getParameterValues("peersId_"+selfList.get(i)));
						
						Map<String, String> hmInner = hmEmpReportingData.get(selfList.get(i));
						if(hmInner==null) hmInner = new HashMap<String, String>();
						List<String> innerPeerList = hmEmpPeerList.get(hmInner.get("MANAGER"));
						StringBuilder sbPeerIds = null;
						for(int a=0; innerPeerList!=null && a<innerPeerList.size(); a++) {
							if(uF.parseToInt(innerPeerList.get(a)) == uF.parseToInt(selfList.get(i))){
								continue;
							}
							if(sbPeerIds==null) {
								sbPeerIds = new StringBuilder();
								sbPeerIds.append(innerPeerList.get(a));
							} else {
								sbPeerIds.append(","+innerPeerList.get(a));
							}
						}
						if(sbPeerIds==null) {
							sbPeerIds = new StringBuilder();
						}
						
						
						if(!existRevieweeIds.contains(selfList.get(i))) {
							pst = con.prepareStatement("insert into appraisal_reviewee_details (appraisal_id, appraisal_freq_id,reviewee_id,subordinate_ids," +
								"supervisor_ids,peer_ids,hr_ids,hod_ids,ceo_ids) values (?,?,?,?, ?,?,?,?, ?)"); 
							pst.setInt(1, uF.parseToInt(getId()));
							pst.setInt(2, uF.parseToInt(getAppFreqId()));
							pst.setInt(3, uF.parseToInt(selfList.get(i)));
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Sub-ordinate")!= null  && memberList.contains(hmOrientMemberID.get("Sub-ordinate"))) {
								pst.setString(4, (subordinatesId!=null && subordinatesId.length()>0) ? ","+subordinatesId+"," : "");
							} else {
								pst.setString(4, "");
							}
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Manager")!= null  && memberList.contains(hmOrientMemberID.get("Manager"))) {
								pst.setString(5, (managersId!=null && managersId.length()>0) ? ","+managersId+"," : "");
							} else {
								pst.setString(5, "");
							}
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Peer")!= null && memberList.contains(hmOrientMemberID.get("Peer"))) {
								pst.setString(6, (peersId!=null && peersId.toString().length()>0) ? ","+peersId.toString()+"," : "");
							} else {
								pst.setString(6, "");
							}
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HR")!= null  && memberList.contains(hmOrientMemberID.get("HR"))) {
								pst.setString(7, (hrsId!=null && hrsId.length()>0) ? ","+hrsId+"," : "");
							} else {
								pst.setString(7, "");
							}
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HOD")!= null  && memberList.contains(hmOrientMemberID.get("HOD"))) {
								pst.setString(8, (hodsId!=null && hodsId.length()>0) ? ","+hodsId+"," : "");
							} else {
								pst.setString(8, "");
							}
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("CEO")!= null  && memberList.contains(hmOrientMemberID.get("CEO"))) {
								pst.setString(9, (ceosId!=null && ceosId.toString().length()>0) ? ","+ceosId.toString()+"," : "");
							} else {
								pst.setString(9, "");
							}
//							System.out.println("pst appraisal_reviewee_details set ====> " + pst);
							pst.executeUpdate();
							pst.close();
						} else {

							pst = con.prepareStatement("update appraisal_reviewee_details set subordinate_ids=?,supervisor_ids=?,peer_ids=?," +
								"hr_ids=?,hod_ids=?,ceo_ids=? where appraisal_id=? and appraisal_freq_id=? and reviewee_id=?"); 
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Sub-ordinate")!= null  && memberList.contains(hmOrientMemberID.get("Sub-ordinate"))) {
								pst.setString(1, (subordinatesId!=null && subordinatesId.length()>0) ? ","+subordinatesId+"," : "");
							} else {
								pst.setString(1, "");
							}
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Manager")!= null  && memberList.contains(hmOrientMemberID.get("Manager"))) {
								pst.setString(2, (managersId!=null && managersId.length()>0) ? ","+managersId+"," : "");
							} else {
								pst.setString(2, "");
							}
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Peer")!= null && memberList.contains(hmOrientMemberID.get("Peer"))) {
								pst.setString(3, (peersId!=null && peersId.toString().length()>0) ? ","+peersId.toString()+"," : "");
							} else {
								pst.setString(3, "");
							}
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HR")!= null  && memberList.contains(hmOrientMemberID.get("HR"))) {
								pst.setString(4, (hrsId!=null && hrsId.length()>0) ? ","+hrsId+"," : "");
							} else {
								pst.setString(4, "");
							}
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HOD")!= null  && memberList.contains(hmOrientMemberID.get("HOD"))) {
								pst.setString(5, (hodsId!=null && hodsId.length()>0) ? ","+hodsId+"," : "");
							} else {
								pst.setString(5, "");
							}
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("CEO")!= null  && memberList.contains(hmOrientMemberID.get("CEO"))) {
								pst.setString(6, (ceosId!=null && ceosId.toString().length()>0) ? ","+ceosId.toString()+"," : "");
							} else {
								pst.setString(6, "");
							}
							pst.setInt(7, uF.parseToInt(getId()));
							pst.setInt(8, uF.parseToInt(getAppFreqId()));
							pst.setInt(9, uF.parseToInt(selfList.get(i)));
//							System.out.println("pst appraisal_reviewee_details set ====> " + pst);
							pst.executeUpdate();
							pst.close();
						
						}
					}
				} else {
					 
					StringBuilder sbRemoveIds = null;
				    List<String> existRevieweeIds = new ArrayList<String>();
				    pst = con.prepareStatement("select reviewee_id from appraisal_reviewee_details where appraisal_id=?");
				    pst.setInt(1, uF.parseToInt(getId()));
				    rs = pst.executeQuery();
				    while(rs.next()) {
				    	if(!existRevieweeIds.contains(rs.getString("reviewee_id"))) {
					    	if(!selfList.contains(rs.getString("reviewee_id"))) {
					    		if(sbRemoveIds == null) {
					    			sbRemoveIds = new StringBuilder();
					    			sbRemoveIds.append(rs.getString("reviewee_id"));
					    		} else {
					    			sbRemoveIds.append(","+rs.getString("reviewee_id"));
					    		}
					    	}
			    			existRevieweeIds.add(rs.getString("reviewee_id"));
			    		}
				    }
				    rs.close();
				    pst.close();
				    
					if(sbRemoveIds != null) {
				    	pst = con.prepareStatement("delete from appraisal_reviewee_details where reviewee_id in ("+sbRemoveIds.toString()+")");
						pst.executeUpdate();
						pst.close();
					}
				    
					for(int i=0; selfList != null && i<selfList.size(); i++) {
						
						String hrsId = uF.getAppendData(request.getParameterValues("hrsId_"+selfList.get(i)));
						String ceosId = uF.getAppendData(request.getParameterValues("ceosId_"+selfList.get(i)));
						String hodsId = uF.getAppendData(request.getParameterValues("hodsId_"+selfList.get(i)));
						String subordinatesId = uF.getAppendData(request.getParameterValues("subordinatesId_"+selfList.get(i)));
						String managersId = uF.getAppendData(request.getParameterValues("managersId_"+selfList.get(i)));
						String peersId = uF.getAppendData(request.getParameterValues("peersId_"+selfList.get(i)));
						
						Map<String, String> hmInner = hmEmpReportingData.get(selfList.get(i));
						if(hmInner==null) hmInner = new HashMap<String, String>();
						List<String> innerPeerList = hmEmpPeerList.get(hmInner.get("MANAGER"));
						StringBuilder sbPeerIds = null;
						for(int a=0; innerPeerList!=null && a<innerPeerList.size(); a++) {
							if(uF.parseToInt(innerPeerList.get(a)) == uF.parseToInt(selfList.get(i))){
								continue;
							}
							if(sbPeerIds==null) {
								sbPeerIds = new StringBuilder();
								sbPeerIds.append(innerPeerList.get(a));
							} else {
								sbPeerIds.append(","+innerPeerList.get(a));
							}
						}
						if(sbPeerIds==null) {
							sbPeerIds = new StringBuilder();
						}
						
						
						if(!existRevieweeIds.contains(selfList.get(i))) {
							pst = con.prepareStatement("insert into appraisal_reviewee_details (appraisal_id, appraisal_freq_id,reviewee_id,subordinate_ids," +
								"supervisor_ids,peer_ids,hr_ids,hod_ids,ceo_ids) values (?,?,?,?, ?,?,?,?, ?)"); 
							pst.setInt(1, uF.parseToInt(getId()));
							pst.setInt(2, uF.parseToInt(getAppFreqId()));
							pst.setInt(3, uF.parseToInt(selfList.get(i)));
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Sub-ordinate")!= null  && memberList.contains(hmOrientMemberID.get("Sub-ordinate"))) {
								pst.setString(4, (subordinatesId!=null && subordinatesId.length()>0) ? ","+subordinatesId+"," : "");
							} else {
								pst.setString(4, "");
							}
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Manager")!= null  && memberList.contains(hmOrientMemberID.get("Manager"))) {
								pst.setString(5, (managersId!=null && managersId.length()>0) ? ","+managersId+"," : "");
							} else {
								pst.setString(5, "");
							}
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Peer")!= null && memberList.contains(hmOrientMemberID.get("Peer"))) {
								pst.setString(6, (peersId!=null && peersId.toString().length()>0) ? ","+peersId.toString()+"," : "");
							} else {
								pst.setString(6, "");
							}
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HR")!= null  && memberList.contains(hmOrientMemberID.get("HR"))) {
								pst.setString(7, (hrsId!=null && hrsId.length()>0) ? ","+hrsId+"," : "");
							} else {
								pst.setString(7, "");
							}
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HOD")!= null  && memberList.contains(hmOrientMemberID.get("HOD"))) {
								pst.setString(8, (hodsId!=null && hodsId.length()>0) ? ","+hodsId+"," : "");
							} else {
								pst.setString(8, "");
							}
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("CEO")!= null  && memberList.contains(hmOrientMemberID.get("CEO"))) {
								pst.setString(9, (ceosId!=null && ceosId.toString().length()>0) ? ","+ceosId.toString()+"," : "");
							} else {
								pst.setString(9, "");
							}
//							System.out.println("pst appraisal_reviewee_details set ====> " + pst);
							pst.executeUpdate();
							pst.close();
						} else {

							pst = con.prepareStatement("update appraisal_reviewee_details set subordinate_ids=?,supervisor_ids=?,peer_ids=?," +
								"hr_ids=?,hod_ids=?,ceo_ids=? where appraisal_id=? and appraisal_freq_id=? and reviewee_id=?"); 
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Sub-ordinate")!= null  && memberList.contains(hmOrientMemberID.get("Sub-ordinate"))) {
								pst.setString(1, (subordinatesId!=null && subordinatesId.length()>0) ? ","+subordinatesId+"," : "");
							} else {
								pst.setString(1, "");
							}
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Manager")!= null  && memberList.contains(hmOrientMemberID.get("Manager"))) {
								pst.setString(2, (managersId!=null && managersId.length()>0) ? ","+managersId+"," : "");
							} else {
								pst.setString(2, "");
							}
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Peer")!= null && memberList.contains(hmOrientMemberID.get("Peer"))) {
								pst.setString(3, (peersId!=null && peersId.toString().length()>0) ? ","+peersId.toString()+"," : "");
							} else {
								pst.setString(3, "");
							}
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HR")!= null  && memberList.contains(hmOrientMemberID.get("HR"))) {
								pst.setString(4, (hrsId!=null && hrsId.length()>0) ? ","+hrsId+"," : "");
							} else {
								pst.setString(4, "");
							}
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HOD")!= null  && memberList.contains(hmOrientMemberID.get("HOD"))) {
								pst.setString(5, (hodsId!=null && hodsId.length()>0) ? ","+hodsId+"," : "");
							} else {
								pst.setString(5, "");
							}
							if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("CEO")!= null  && memberList.contains(hmOrientMemberID.get("CEO"))) {
								pst.setString(6, (ceosId!=null && ceosId.toString().length()>0) ? ","+ceosId.toString()+"," : "");
							} else {
								pst.setString(6, "");
							}
							pst.setInt(7, uF.parseToInt(getId()));
							pst.setInt(8, uF.parseToInt(getAppFreqId()));
							pst.setInt(9, uF.parseToInt(selfList.get(i)));
//							System.out.println("pst appraisal_reviewee_details set ====> " + pst);
							pst.executeUpdate();
							pst.close();
						
						}
					}
				}
			}
//			***************************** appraisal Frequency End ************************************			
			
			int hr=0,manager=0,self=0,peer=0,other=0,ceo=0,hod=0,subordinate=0,grouphead=0,other_peer=0;
			
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Self")!=null && memberList.contains(hmOrientMemberID.get("Self"))) {
				if(sb2 != null && !sb2.equals("")) {
					self = 1;
				}
			}
			
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Manager")!=null && memberList.contains(hmOrientMemberID.get("Manager"))) {
				if(managerIds != null && !managerIds.equals("")) {
					manager = 1;
				}
			}
			
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Peer")!=null && memberList.contains(hmOrientMemberID.get("HR"))) {
				if(hrIds != null && !hrIds.equals("")) {
					hr = 1;
				}
			}
			
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Self")!=null  && memberList.contains(hmOrientMemberID.get("Peer"))) {
				if(peerIds != null && !peerIds.equals("")) {
					peer = 1;
				}
			}
			
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Anyone")!=null  && memberList.contains(hmOrientMemberID.get("Anyone"))) {
				if(otherIds != null && !otherIds.equals("")) {
					other = 1;
				}
			}
			
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("CEO")!=null  && memberList.contains(hmOrientMemberID.get("CEO"))) {
				if(ceoIds != null && !ceoIds.equals("")) {
					ceo = 1;
				}
			}
			
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HOD")!=null  && memberList.contains(hmOrientMemberID.get("HOD"))) {
				if(hodIds != null && !hodIds.equals("")) {
					hod = 1;
				}
			}
			
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Sub-ordinate")!=null  && memberList.contains(hmOrientMemberID.get("Sub-ordinate"))) {
//				if(hodIds != null && !hodIds.equals("")) {
					subordinate = 1;
//				}
			}
			
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("GroupHead")!=null  && memberList.contains(hmOrientMemberID.get("GroupHead"))) {
//				if(hodIds != null && !hodIds.equals("")) {
					grouphead = 1;
//				}
			}
			
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Other Peer")!=null  && memberList.contains(hmOrientMemberID.get("Other Peer"))) {
//				if(hodIds != null && !hodIds.equals("")) {
					other_peer = 1;
//				}
			}
			
			if(getOreinted() != null && getOldOrientVal() != null && !getOreinted().equals(getOldOrientVal())) {
//				System.out.println("Not equal .......");
				pst = con.prepareStatement("update appraisal_main_level_details set self=?,hr=?,manager=?,peer=?,ceo=?,hod=?,subordinate=?,grouphead=?," +
					"other_peer=? where appraisal_id=?");
				pst.setInt(1, self);
				pst.setInt(2, hr);
				pst.setInt(3, manager);
				pst.setInt(4, peer);
				pst.setInt(5, ceo);
				pst.setInt(6, hod);
				pst.setInt(7, subordinate);
				pst.setInt(8, grouphead);
				pst.setInt(9, other_peer);
				pst.setInt(10, uF.parseToInt(getId()));
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
		return strReturn;
	}
	
	
	private List<String> getSelfList() {
		List<String> al = new ArrayList<String>();
		try {
			if (getEmployee() != null && getEmployee().length() > 0) {
				List<String> emp = Arrays.asList(getEmployee().split(","));
				for(int i=0;emp!=null && !emp.isEmpty() && i<emp.size();i++) {
					al.add(emp.get(i).trim());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	

	/*private List<String> getSelfList(Connection con) {
		List<String> al = new ArrayList<String>();
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;

		try {

//			System.out.println("getEmployee() ===>> " + getEmployee());
			if (getEmployee() != null && getEmployee().length() > 0) {
//				System.out.println("getEmployee().length() ===>> " + getEmployee().length());
				List<String> emp = Arrays.asList(getEmployee().split(","));
//				System.out.println("emp ===>> " + emp);
				for(int i=0;emp!=null && !emp.isEmpty() && i<emp.size();i++) {
					al.add(emp.get(i).trim());
				}
//				System.out.println("al.add(emp.get(i).trim()) ==========>" + al.toString());
//				al.add(getEmployee());
			} else {
			
				StringBuilder sbQuery=new StringBuilder();
				sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id ");
				
				if(getStrOrg()!=null && !getStrOrg().equals("")) {
					sbQuery.append(" and eod.org_id in ("+getStrOrg()+") ");
				}
				if(getStrWlocation()!=null && !getStrWlocation().equals("")) {
					sbQuery.append(" and eod.wlocation_id in ("+getStrWlocation()+") ");
				}
				if(getStrDepart()!=null && !getStrDepart().equals("")) {
					sbQuery.append(" and eod.depart_id in ("+getStrDepart()+") ");
				}
				if(getStrLevel()!=null && !getStrLevel().equals("")) {
					sbQuery.append(" and eod.grade_id in (SELECT grade_id FROM grades_details where designation_id in " +
							" (SELECT designation_id FROM designation_details  WHERE level_id in (" + getStrLevel()+ "))) ");
				}
				if(getStrDesignationUpdate()!=null && !getStrDesignationUpdate().equals("")) {
					sbQuery.append(" and eod.grade_id in (SELECT grade_id FROM grades_details where designation_id in " +
							" (SELECT designation_id FROM designation_details  WHERE designation_id in (" + getStrDesignationUpdate() + ")))  ");
				}
				if(getEmpGrade()!=null && !getEmpGrade().equals("")) {
					sbQuery.append("  and eod.grade_id in(SELECT grade_id FROM grades_details where grade_id in (" + getEmpGrade()+ ") ) ");
				}
				
				sbQuery.append(" order by epd.emp_per_id");
				
	
				pst = con.prepareStatement(sbQuery.toString());			
				rsEmpCode = pst.executeQuery();
				while (rsEmpCode.next()) {
//					System.out.println("rsEmpCode.getString('emp_per_id') ============ >"+ rsEmpCode.getString("emp_per_id"));
					al.add(rsEmpCode.getString("emp_per_id").trim());
				}
				rsEmpCode.close();
				pst.close();
		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}*/

	
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
		String[] questionSelect = request.getParameterValues("questionSelect");
		String[] weightage = request.getParameterValues("weightage");
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

			for (int l = 0; l < questionSelect.length; l++) {

				int question_id = uF.parseToInt(questionSelect[l]);
				
				if (questionSelect[l].length() > 0) {
					if (uF.parseToInt(questionSelect[l]) == 0) {

						String[] correct = request.getParameterValues("correct" + orientt[l]);
						String ansType = request.getParameter("ansType" + orientt[l]);
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
						pst.setString(2, optiona[l]);
						pst.setString(3, optionb[l]);
						pst.setString(4, optionc[l]);
						pst.setString(5, optiond[l]);
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
						
					}
					String other_id = null;
					pst = con.prepareStatement("select other_id from appraisal_question_details where appraisal_question_details_id=?");
					pst.setInt(1, uF.parseToInt(questionID[l]));
					rst = pst.executeQuery();
					while (rst.next()) {
						other_id = rst.getString(1);
					}
					rst.close();
					pst.close();

					// select * from appraisal_other_question_type_details
					// aoq,appraisal_level_details al where
					// aoq.othe_question_type_id=95 and
					// al.appraisal_level_id=aoq.level_id
					String attributeid = null;
					pst = con
							.prepareStatement("select attribute_id from appraisal_other_question_type_details aoq,appraisal_level_details al "
									+ "where aoq.othe_question_type_id=? and al.appraisal_level_id=aoq.level_id");
					pst.setInt(1, uF.parseToInt(other_id));
					rst = pst.executeQuery();
					while (rst.next()) {
						attributeid = rst.getString(1).trim();
					}
					rst.close();
					pst.close();

					pst = con.prepareStatement("update appraisal_question_details set question_id=?,weightage=?attribute_id=? where appraisal_id=? and appraisal_question_details_id=?");
					pst.setInt(1, question_id);
					pst.setDouble(2, uF.parseToDouble(weightage[l]));
					pst.setInt(3, uF.parseToInt(attributeid));
					pst.setInt(4, uF.parseToInt(getId()));
					pst.setInt(5, uF.parseToInt(questionID[l]));
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("update appraisal_question_answer set appraisal_attribute=? where appraisal_id=? and question_id=?");
					pst.setInt(1, uF.parseToInt(attributeid));
					pst.setInt(2, uF.parseToInt(getId()));
					pst.setInt(3, uF.parseToInt(questionID[l]));
					
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

	private void editScoreQuestions() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		String[] questionID = request.getParameterValues("questionID");
		String[] questionSelect = request.getParameterValues("questionSelect");
		String[] weightage = request.getParameterValues("weightage");
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

			for (int l = 0; l < questionSelect.length; l++) {

				int question_id = uF.parseToInt(questionSelect[l]);
				if (questionSelect[l].length() > 0) {
					if (uF.parseToInt(questionSelect[l]) == 0) {

						String[] correct = request.getParameterValues("correct"
								+ orientt[l]);
						String ansType = request.getParameter("ansType"
								+ orientt[l]);
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
						pst.setString(2, optiona[l]);
						pst.setString(3, optionb[l]);
						pst.setString(4, optionc[l]);
						pst.setString(5, optiond[l]);
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
					}

					pst = con.prepareStatement("update appraisal_question_details set question_id=?,weightage=?,attribute_id=? where appraisal_id=? and appraisal_question_details_id=?");
					pst.setInt(1, question_id);
					pst.setDouble(2, uF.parseToDouble(weightage[l])); 
					pst.setInt(3, uF.parseToInt(getAttribute_id()));
					pst.setInt(4, uF.parseToInt(getId()));
					pst.setInt(5, uF.parseToInt(questionID[l]));
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("update appraisal_question_answer set appraisal_attribute=? where appraisal_id=? and question_id=?");
					pst.setInt(1, uF.parseToInt(getAttribute_id()));
					pst.setInt(2, uF.parseToInt(getId()));
					pst.setInt(3, uF.parseToInt(questionID[l]));
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
			Map<String, String> AppraisalQuestion = getAppraisalQuestionMap(con,uF);

			pst = con.prepareStatement("select * from appraisal_question_details where appraisal_question_details_id=? and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getEditID()));
			pst.setInt(2, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
//			Map<String, Map<String, String>> memberMp = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));

				List<List<String>> outerList = questionMp.get(rs.getString("appraisal_question_details_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				questionMp.put(rs.getString("appraisal_question_details_id"), outerList);

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
			pst = con
					.prepareStatement("select * from appraisal_scorecard_details where scorecard_id =? and appraisal_id=?");
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
				innerList.add(uF.showData(
						rs.getString("scorecard_section_name"), ""));
				innerList.add(uF.showData(
						rs.getString("scorecard_description"), ""));
				innerList.add(uF.showData(rs.getString("scorecard_weightage"),
						""));
				innerList.add(uF.showData(rs.getString("appraisal_attribute"),
						""));
				attributevalue = uF.showData(
						rs.getString("appraisal_attribute"), "");

				List<List<String>> outerList = scoreMp.get(rs
						.getString("scorecard_id"));

				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				scoreMp.put(rs.getString("scorecard_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con
					.prepareStatement("select * from appraisal_measure_details where scorecard_id in("
							+ scorecard_id + ") and appraisal_id=?");
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
				List<List<String>> outerList = measureMp.get(rs
						.getString("scorecard_id"));
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
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));

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
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));

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
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));

				List<List<String>> outerList = questionMp.get(rs.getString("measure_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				questionMp.put(rs.getString("measure_id"), outerList);

//				Map<String, String> memberInnerMp = new HashMap<String, String>();
//				memberInnerMp.put("HR", rs.getString("hr"));
//				memberInnerMp.put("Self", rs.getString("self"));
//				memberInnerMp.put("Manager", rs.getString("manager"));
//				memberInnerMp.put("Peer", rs.getString("peer"));
//				memberInnerMp.put("Client", rs.getString("client"));
//				memberInnerMp.put("Sub ordinate", rs.getString("subordinate"));
//				memberInnerMp.put("GroupHead", rs.getString("grouphead"));
//				memberInnerMp.put("Vendor", rs.getString("vendor"));
//				memberInnerMp.put("CEO", rs.getString("ceo"));
//				memberInnerMp.put("HOD", rs.getString("hod"));
//				memberMp.put(rs.getString("appraisal_question_details_id"), memberInnerMp);

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

			pst = con
					.prepareStatement("select * from appraisal_measure_details where measure_id=? and appraisal_id=?");
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
				List<List<String>> outerList = measureMp.get(rs
						.getString("measure_id"));
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
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));

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
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));

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

				List<List<String>> outerGoalList = GoalMp.get(rs
						.getString("goal_id"));
				if (outerGoalList == null)
					outerGoalList = new ArrayList<List<String>>();
				outerGoalList.add(innerGoalList);

				GoalMp.put(rs.getString("goal_id"), outerGoalList);

			}
			rs.close();
			pst.close();
			
			pst = con
					.prepareStatement("select * from appraisal_objective_details where goal_id in("
							+ goal_id + ") and appraisal_id=?");
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

				List<List<String>> outerList = objectiveMp.get(rs
						.getString("goal_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				objectiveMp.put(rs.getString("goal_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con
					.prepareStatement("select * from appraisal_measure_details where objective_id in("
							+ objective_id + ") and appraisal_id=?");
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
				List<List<String>> outerList = measureMp.get(rs
						.getString("objective_id"));
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
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));

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
		String[] questionSelect = request.getParameterValues("questionSelect");
		String[] weightage = request.getParameterValues("weightage");
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
	
			for (int l = 0; l < questionSelect.length; l++) {

				int question_id = uF.parseToInt(questionSelect[l]);
				
				if (questionSelect[l].length() > 0) {
					if (uF.parseToInt(questionSelect[l]) == 0) {

						String[] correct = request.getParameterValues("correct"
								+ orientt[l]);
						String ansType = request.getParameter("ansType"
								+ orientt[l]);
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
						pst.setString(2, optiona[l]);
						pst.setString(3, optionb[l]);
						pst.setString(4, optionc[l]);
						pst.setString(5, optiond[l]);
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
					}

					pst = con.prepareStatement("update appraisal_question_details set question_id=?,weightage=? where appraisal_id=? and appraisal_question_details_id=?");
					pst.setInt(1, question_id);
					pst.setDouble(2, uF.parseToDouble(weightage[l]));
					pst.setInt(3, uF.parseToInt(getId()));
					pst.setInt(4, uF.parseToInt(questionID[l]));
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

		String[] objectiveSectionName = request
				.getParameterValues("objectiveSectionName");
		String[] objectiveDescription = request
				.getParameterValues("objectiveDescription");
		String[] objectiveWeightage = request
				.getParameterValues("objectiveWeightage");
		String[] objectiveID = request.getParameterValues("objectiveID");

		try {
			con = db.makeConnection(con);
			
			for (int i = 0; i < objectiveSectionName.length; i++) {

				pst = con
						.prepareStatement("update appraisal_objective_details set objective_section_name=?,objective_description=?,"
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

		String[] goalSectionName = request
				.getParameterValues("goalSectionName");
		String[] goalDescription = request
				.getParameterValues("goalDescription");
		String[] goalWeightage = request.getParameterValues("goalWeightage");
		String[] goalID = request.getParameterValues("goalID");

		try {
			con = db.makeConnection(con);

			
			for (int i = 0; i < goalSectionName.length; i++) {

				pst = con
						.prepareStatement("update appraisal_goal_details set goal_section_name=?,goal_description=?,"
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

		String[] scoreSectionName = request
				.getParameterValues("scoreSectionName");
		String[] scoreCardDescription = request
				.getParameterValues("scoreCardDescription");
		String[] scoreCardWeightage = request
				.getParameterValues("scoreCardWeightage");
		String[] scoreID = request.getParameterValues("scoreID");
		String[] attribute = request.getParameterValues("attribute");

		try {
			con = db.makeConnection(con);
			
			for (int i = 0; i < scoreSectionName.length; i++) {

				attribute_id = attribute[i];

				pst = con
						.prepareStatement("update appraisal_scorecard_details set scorecard_section_name=?,scorecard_description=?,"
								+ "scorecard_weightage=?,appraisal_attribute=? where appraisal_id=? and scorecard_id=? ");
				pst.setString(1, scoreSectionName[i]);
				pst.setString(2, scoreCardDescription[i]);
				pst.setString(3, scoreCardWeightage[i]);
				pst.setInt(4, uF.parseToInt(attribute[i]));
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, uF.parseToInt(scoreID[i]));
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
			pst = con
					.prepareStatement("select * from appraisal_scorecard_details where scorecard_id =? and appraisal_id=?");
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
				innerList.add(uF.showData(
						rs.getString("scorecard_section_name"), ""));
				innerList.add(uF.showData(
						rs.getString("scorecard_description"), ""));
				innerList.add(uF.showData(rs.getString("scorecard_weightage"),
						""));
				innerList.add(uF.showData(rs.getString("appraisal_attribute"),
						""));
				attributevalue = uF.showData(
						rs.getString("appraisal_attribute"), "");

				List<List<String>> outerList = scoreMp.get(rs
						.getString("scorecard_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				scoreMp.put(rs.getString("scorecard_id"), outerList);
			}
			rs.close();
			pst.close();

			pst = con
					.prepareStatement("select * from appraisal_goal_details where scorecard_id in("
							+ scorecard_id + ") and appraisal_id=?");
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

				List<List<String>> outerGoalList = GoalMp.get(rs
						.getString("scorecard_id"));
				if (outerGoalList == null)
					outerGoalList = new ArrayList<List<String>>();
				outerGoalList.add(innerGoalList);

				GoalMp.put(rs.getString("scorecard_id"), outerGoalList);

			}
			rs.close();
			pst.close();
			
			pst = con
					.prepareStatement("select * from appraisal_objective_details where goal_id in("
							+ goal_id + ") and appraisal_id=?");
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

				List<List<String>> outerList = objectiveMp.get(rs
						.getString("goal_id"));
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
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));

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
				memberMp.put(rs.getString("appraisal_question_details_id"),	memberInnerMp);*/

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

//	private void getAppraisalDetail() {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//
//		con = db.makeConnection(con);
//		Map<String, String> hmDesignation = CF.getDesigMap(con);
//		Map<String, String> hmGradeMap = CF.getGradeMap(con);
//		Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
//		Map<String, String> hmLevelMap = getLevelMap(con);
//		Map<String, String> hmLocation = getLocationMap(con);
//		Map<String, String> mpdepart = CF.getDeptMap(con);
//		Map<String, String> orientationMp = getOrientationValue(con);
//		Map<String, String> orientationMemberMp = getOrientationMember(con);
//		
//		String oreinted1=null;
//		
//		try {
//			Map<String, String> hmFrequency = new HashMap<String, String>();
//			pst = con.prepareStatement("select * from appraisal_frequency");
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				hmFrequency.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
//			}
//			rs.close();
//			pst.close();
//
//			List<String> appraisalList = new ArrayList<String>();
//			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
//			pst.setInt(1, uF.parseToInt(id));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				String memberName = "";
//				if(rs.getString("usertype_member") != null && !rs.getString("usertype_member").equals("")) {
//					List<String> memberList = Arrays.asList(rs.getString("usertype_member").split(","));
//	
//					for (int i = 0; i < memberList.size(); i++) {
//						if (i == 0)
//							memberName += orientationMemberMp.get(memberList.get(i));
//						else
//							memberName += ","+ orientationMemberMp.get(memberList.get(i));
//					}
//				}
//				appraisalList.add(uF.showData(rs.getString("appraisal_details_id"), ""));
//				appraisalList.add(uF.showData(rs.getString("appraisal_name"),""));
//				oreinted1 = rs.getString("oriented_type");
//				appraisalList.add(uF.showData(orientationMp.get(rs.getString("oriented_type"))+ "&deg( " + memberName + " )", ""));
//				appraisalList.add(uF.showData(rs.getString("self_ids"), ""));
//				appraisalList.add(uF.showData(getAppendData(rs.getString("level_id"),hmLevelMap), ""));
//				appraisalList.add(uF.showData(getAppendData(rs.getString("desig_id"), hmDesignation),""));
//				appraisalList.add(uF.showData(getAppendData(rs.getString("grade_id"),hmGradeMap), ""));
//				appraisalList.add(uF.showData(hmFrequency.get(rs.getString("frequency")), ""));
//				appraisalList.add(uF.showData(getAppendData(rs.getString("wlocation_id"),hmLocation), ""));
//				appraisalList.add(uF.showData(getAppendData(rs.getString("department_id"), mpdepart),""));
//				appraisalList.add(uF.showData(rs.getString("supervisor_id"), ""));
//				appraisalList.add(uF.showData(rs.getString("peer_ids"), ""));
//				appraisalList.add(uF.showData(getAppendData(rs.getString("self_ids"),hmEmpName), ""));
//				appraisalList.add(uF.showData(rs.getString("emp_status"), ""));
//				appraisalList.add(uF.showData(rs.getString("appraisal_type"),""));
//				appraisalList.add(uF.showData(rs.getString("appraisal_description"), ""));
//				appraisalList.add(uF.showData(rs.getString("appraisal_instruction"), ""));
//				
////				System.out.println("oreinted1=====> "+oreinted1);
//				getOrientationValue(uF.parseToInt(oreinted1));
//
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("appraisalList", appraisalList);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}

	public void initialize(UtilityFunctions uF) {

		orientationList = new FillOrientation(request).fillOrientation();
		frequencyList = new FillFrequency(request).fillFrequency();
		levelList = new FillLevel(request).fillLevel();
		workList = new FillWLocation(request).fillWLocation();
		departmentList = new FillDepartment(request).fillDepartment();
		organisationList = new FillOrganisation(request).fillOrganisation();

		if (strLevel != null) {
			desigList = new FillDesig(request).fillDesigFromLevel(strLevel);
		} else {
			desigList = new FillDesig(request).fillDesig();
		}
		if (strDesignationUpdate != null) {
			gradeList = new FillGrade(request).fillGradeFromDesignation(strDesignationUpdate);
		} else {
			gradeList = new FillGrade(request).fillGrade();
		}
//		userlocation = getManagerLocation();
		if (empGrade != null) {
//			empList = new FillEmployee(request).fillEmployeeName(uF.parseToInt(empGrade));
			empList = new FillEmployee(request).fillEmployeeName();
		} else if (strUserType != null && strUserType.equals(MANAGER)) {
			empList = new FillEmployee(request).fillEmployeeNameBySupervisor(strSessionEmpId);
		} else {
//			empList = new FillEmployee(request).fillEmployeeNameByLocation(userlocation);
			List<String> alDates = (List<String>) request.getAttribute("alDates");
			String strStartDt = null;
			String strEndDt = null;
			String strMinDayBeforeStartDt = null;
			String strMinDayBeforeEndDt = null;
			if(alDates!=null && alDates.size()>0) {
				strStartDt = alDates.get(0);
				strEndDt = alDates.get(1);
				strMinDayBeforeStartDt = alDates.get(2);
				strMinDayBeforeEndDt = alDates.get(3);
				empList = new FillEmployee(request).fillEmployeeNameWithJoiningDate(strStartDt, strMinDayBeforeStartDt, strEndDt, strMinDayBeforeEndDt);
			} else {
				empList = new FillEmployee(request).fillEmployeeName();
			}
//			wlocationvalue.add(userlocation);
		}
		
		/*if (empGrade != null) {
			empList = new FillEmployee(request).fillEmployeeName(uF.parseToInt(empGrade));
		} else if (strUserType != null && strUserType.equals(MANAGER)) {
			empList = new FillEmployee(request).fillEmployeeNameBySupervisor(strSessionEmpId);
		} else if(strUserType != null && strUserType.equals(ADMIN)) {
			empList = new FillEmployee(request).fillCafeEmployees(strUserType, "");
		} else if(strUserType != null && strUserType.equals(HRMANAGER)) {
			if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")) {
				empList = new FillEmployee(request).fillCafeEmployees(strUserType, (String)session.getAttribute(WLOCATION_ACCESS));
			} else {
				empList = new FillEmployee(request).fillCafeEmployees(strUserType,userlocation);
			}
			
		} else {
			empList = new FillEmployee(request).fillEmployeeNameByLocation(userlocation);
		}*/
		
		finalizationList = new FillEmployee(request).fillFinalizationNameByLocation(userlocation);
		reviewerList  = new FillEmployee(request).fillReviewerNameByLocation(null);
		String levelID=getSelfIDs(getId());
//		System.out.println("levelID====>"+levelID);
		if (levelID != null && levelID.length()>0) {
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
			while(rs.next()) {
				empID=rs.getString("self_ids");
			}
			rs.close();
			pst.close();
			
			if(empID!=null && !empID.equals("")) {
				empID=empID.substring(1,empID.length()-1);
				List<String> levellistID=new ArrayList<String>();
				pst = con.prepareStatement("select ld.level_id from level_details ld right join (select * from designation_details dd " +
						"right join (select *, gd.designation_id as designationid from employee_official_details eod, grades_details gd " +
						"where gd.grade_id=eod.grade_id and eod.emp_id in ("+empID+")) a on a.designationid=dd.designation_id)" +
						" a on a.level_id=ld.level_id");
//				System.out.println("self id pst==>"+pst);
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
					if(i==0) {
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
			while (rs.next()) {
				orientationMemberMp.put(rs.getString("orientation_member_id"),
						rs.getString("member_name"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMemberMp;
	}

	
	private String getAppendDataEmpName(String strEmpIds, Map<String, String> hmEmpName) {
		StringBuilder sbEmpName = null;
//		Map<String, String> hmDesignation = CF.getEmpDesigMap();
//		Map<String, String> hmEmpCode = CF.getEmpCodeMap();
		UtilityFunctions uF = new UtilityFunctions();
		if (strEmpIds != null) {
			List<String> strID = Arrays.asList(strEmpIds.split(","));
			for (int i = 0; i < strID.size(); i++) {
				if(uF.parseToInt(strID.get(i)) > 0) {
					if (sbEmpName == null) {
						sbEmpName = new StringBuilder();
						sbEmpName.append(hmEmpName.get(strID.get(i))+" <a onclick=\"removeRevieweeForSelfReview('"+strID.get(i)+"');\" href=\"javascript: void(0);\">X</a>");
					} else {
						sbEmpName.append(", " + hmEmpName.get(strID.get(i))+" <a onclick=\"removeRevieweeForSelfReview('"+strID.get(i)+"');\" href=\"javascript: void(0);\">X</a>");
					}
				}
			}
		} else {
			return null;
		}
		if (sbEmpName == null) {
			sbEmpName = new StringBuilder();
		}
		return sbEmpName.toString();
	}
	
	
	
	private String getAppendData11(List<String> strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();
//		Map<String, String> hmDesignation = CF.getEmpDesigMap();
//		Map<String, String> hmEmpCode = CF.getEmpCodeMap();
		if (strID != null) {
			int cnt=0;
			for (int i = 0; i < strID.size(); i++) {
				if(strID.get(i) != null && !strID.get(i).equals("")) {
					if (cnt == 0) {
						sb.append(mp.get(strID.get(i)));
						cnt++;
					} else {
						sb.append(", " + mp.get(strID.get(i)));
					}
				}
			}
		} else {
			return null;
		}
		return sb.toString();
	}
	
	
	public String getAppendData(Connection con, List<String> strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();
		Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
		Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
		if (strID != null) {
			for (int i = 0; i < strID.size(); i++) {
				if (i == 0) {
					sb.append("[" + hmEmpCode.get(strID.get(i)) + "] "
							+ mp.get(strID.get(i)) + "("
							+ hmDesignation.get(strID.get(i)) + ")");
//					System.out.println("sb.toString() =====>"+sb.toString());
				} else {
					sb.append(", [" + hmEmpCode.get(strID.get(i)) + "] "
							+ mp.get(strID.get(i)) + "("
							+ hmDesignation.get(strID.get(i)) + ")");
//					System.out.println("sb.toString() =====>"+sb.toString());
				}
			}
		} else {
			return null;
		}

		return sb.toString();
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
			StringBuilder sb = new StringBuilder("");
			Map<String, String> questMp = new HashMap<String, String>();
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from question_bank where is_add=true");
			rs = pst.executeQuery();
			while (rs.next()) {
				sb.append("<option value=\"" + rs.getString("question_bank_id") + "\">" + rs.getString("question_text").replace("'", "") + "</option>");
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
	

	private String getAppendDataRevieweePanel(UtilityFunctions uF, String revieweeId, String strPanelId, Map<String, String> hmEmpName, String panelMemberType) {
		StringBuilder sbPanel = new StringBuilder();
		String[] temp = strPanelId!=null ? strPanelId.split(","): ("").split(",");
		for(int i=0; i<temp.length; i++) {
			if(uF.parseToInt(temp[i].trim())>0) {
				if(panelMemberType.equals("Sub-ordinate")) {
					if (i == (temp.length-1)) {
						sbPanel.append("<span style=\"background-color: #f6f6f6; border-radius: 5px; padding: 2px 0px; margin-right: 3px;\" id=\"subordinates_"+revieweeId+"_"+temp[i].trim()+"\"><input type=\"hidden\" name=\"subordinatesId_"+revieweeId+"\" value=\""+temp[i].trim()+"\" />"+hmEmpName.get(temp[i].trim())+" <a onclick=\"removeRevieweePanelMember('subordinates_"+revieweeId+"_"+temp[i].trim()+"');\" href=\"javascript: void(0);\" style=\"color: red;\"><i class=\"fa fa-times\" aria-hidden=\"true\"></i></a>"+"</span>");
					} else {
						sbPanel.append("<span style=\"background-color: #f6f6f6; border-radius: 5px; padding: 2px 0px; margin-right: 3px;\" id=\"subordinates_"+revieweeId+"_"+temp[i].trim()+"\"><input type=\"hidden\" name=\"subordinatesId_"+revieweeId+"\" value=\""+temp[i].trim()+"\" />"+hmEmpName.get(temp[i].trim())+" <a onclick=\"removeRevieweePanelMember('subordinates_"+revieweeId+"_"+temp[i].trim()+"');\" href=\"javascript: void(0);\" style=\"color: red;\"><i class=\"fa fa-times\" aria-hidden=\"true\"></i></a>"+"</span>, ");
					}
				} else if(panelMemberType.equals("Peer")) {
					if (i == (temp.length-1)) {
						sbPanel.append("<span style=\"background-color: #f6f6f6; border-radius: 5px; padding: 2px 0px; margin-right: 3px;\" id=\"peers_"+revieweeId+"_"+temp[i].trim()+"\"><input type=\"hidden\" name=\"peersId_"+revieweeId+"\" value=\""+temp[i].trim()+"\" />"+hmEmpName.get(temp[i].trim())+" <a onclick=\"removeRevieweePanelMember('peers_"+revieweeId+"_"+temp[i].trim()+"');\" href=\"javascript: void(0);\" style=\"color: red;\"><i class=\"fa fa-times\" aria-hidden=\"true\"></i></a>"+"</span>");
					} else {
						sbPanel.append("<span style=\"background-color: #f6f6f6; border-radius: 5px; padding: 2px 0px; margin-right: 3px;\" id=\"peers_"+revieweeId+"_"+temp[i].trim()+"\"><input type=\"hidden\" name=\"peersId_"+revieweeId+"\" value=\""+temp[i].trim()+"\" />"+hmEmpName.get(temp[i].trim())+" <a onclick=\"removeRevieweePanelMember('peers_"+revieweeId+"_"+temp[i].trim()+"');\" href=\"javascript: void(0);\" style=\"color: red;\"><i class=\"fa fa-times\" aria-hidden=\"true\"></i></a>"+"</span>, ");
					}
				} else if(panelMemberType.equals("Other Peer")) {
					if (i == (temp.length-1)) {
						sbPanel.append("<span style=\"background-color: #f6f6f6; border-radius: 5px; padding: 2px 0px; margin-right: 3px;\" id=\"otherPeers_"+revieweeId+"_"+temp[i].trim()+"\"><input type=\"hidden\" name=\"otherPeersId_"+revieweeId+"\" value=\""+temp[i].trim()+"\" />"+hmEmpName.get(temp[i].trim())+" <a onclick=\"removeRevieweePanelMember('otherPeers_"+revieweeId+"_"+temp[i].trim()+"');\" href=\"javascript: void(0);\" style=\"color: red;\"><i class=\"fa fa-times\" aria-hidden=\"true\"></i></a>"+"</span>");
					} else {
						sbPanel.append("<span style=\"background-color: #f6f6f6; border-radius: 5px; padding: 2px 0px; margin-right: 3px;\" id=\"otherPeers_"+revieweeId+"_"+temp[i].trim()+"\"><input type=\"hidden\" name=\"otherPeersId_"+revieweeId+"\" value=\""+temp[i].trim()+"\" />"+hmEmpName.get(temp[i].trim())+" <a onclick=\"removeRevieweePanelMember('otherPeers_"+revieweeId+"_"+temp[i].trim()+"');\" href=\"javascript: void(0);\" style=\"color: red;\"><i class=\"fa fa-times\" aria-hidden=\"true\"></i></a>"+"</span>, ");
					}
				} else if(panelMemberType.equals("Manager")) {
					if (i == (temp.length-1)) {
						sbPanel.append("<span style=\"background-color: #f6f6f6; border-radius: 5px; padding: 2px 0px; margin-right: 3px;\" id=\"managers_"+revieweeId+"_"+temp[i].trim()+"\"><input type=\"hidden\" name=\"managersId_"+revieweeId+"\" value=\""+temp[i].trim()+"\" />"+hmEmpName.get(temp[i].trim())+" <a onclick=\"removeRevieweePanelMember('managers_"+revieweeId+"_"+temp[i].trim()+"');\" href=\"javascript: void(0);\" style=\"color: red;\"><i class=\"fa fa-times\" aria-hidden=\"true\"></i></a>"+"</span>");
					} else {
						sbPanel.append("<span style=\"background-color: #f6f6f6; border-radius: 5px; padding: 2px 0px; margin-right: 3px;\" id=\"managers_"+revieweeId+"_"+temp[i].trim()+"\"><input type=\"hidden\" name=\"managersId_"+revieweeId+"\" value=\""+temp[i].trim()+"\" />"+hmEmpName.get(temp[i].trim())+" <a onclick=\"removeRevieweePanelMember('managers_"+revieweeId+"_"+temp[i].trim()+"');\" href=\"javascript: void(0);\" style=\"color: red;\"><i class=\"fa fa-times\" aria-hidden=\"true\"></i></a>"+"</span>, ");
					}
				} else if(panelMemberType.equals("GroupHead")) {
					if (i == (temp.length-1)) {
						sbPanel.append("<span style=\"background-color: #f6f6f6; border-radius: 5px; padding: 2px 0px; margin-right: 3px;\" id=\"groupHeads_"+revieweeId+"_"+temp[i].trim()+"\"><input type=\"hidden\" name=\"groupHeadsId_"+revieweeId+"\" value=\""+temp[i].trim()+"\" />"+hmEmpName.get(temp[i].trim())+" <a onclick=\"removeRevieweePanelMember('groupHeads_"+revieweeId+"_"+temp[i].trim()+"');\" href=\"javascript: void(0);\" style=\"color: red;\"><i class=\"fa fa-times\" aria-hidden=\"true\"></i></a>"+"</span>");
					} else {
						sbPanel.append("<span style=\"background-color: #f6f6f6; border-radius: 5px; padding: 2px 0px; margin-right: 3px;\" id=\"groupHeads_"+revieweeId+"_"+temp[i].trim()+"\"><input type=\"hidden\" name=\"groupHeadsId_"+revieweeId+"\" value=\""+temp[i].trim()+"\" />"+hmEmpName.get(temp[i].trim())+" <a onclick=\"removeRevieweePanelMember('groupHeads_"+revieweeId+"_"+temp[i].trim()+"');\" href=\"javascript: void(0);\" style=\"color: red;\"><i class=\"fa fa-times\" aria-hidden=\"true\"></i></a>"+"</span>, ");
					}
				} else if(panelMemberType.equals("HOD")) {
					if (i == (temp.length-1)) {
						sbPanel.append("<span style=\"background-color: #f6f6f6; border-radius: 5px; padding: 2px 0px; margin-right: 3px;\" id=\"hods_"+revieweeId+"_"+temp[i].trim()+"\"><input type=\"hidden\" name=\"hodsId_"+revieweeId+"\" value=\""+temp[i].trim()+"\" />"+hmEmpName.get(temp[i].trim())+" <a onclick=\"removeRevieweePanelMember('hods_"+revieweeId+"_"+temp[i].trim()+"');\" href=\"javascript: void(0);\" style=\"color: red;\"><i class=\"fa fa-times\" aria-hidden=\"true\"></i></a>"+"</span>");
					} else {
						sbPanel.append("<span style=\"background-color: #f6f6f6; border-radius: 5px; padding: 2px 0px; margin-right: 3px;\" id=\"hods_"+revieweeId+"_"+temp[i].trim()+"\"><input type=\"hidden\" name=\"hodsId_"+revieweeId+"\" value=\""+temp[i].trim()+"\" />"+hmEmpName.get(temp[i].trim())+" <a onclick=\"removeRevieweePanelMember('hods_"+revieweeId+"_"+temp[i].trim()+"');\" href=\"javascript: void(0);\" style=\"color: red;\"><i class=\"fa fa-times\" aria-hidden=\"true\"></i></a>"+"</span>, ");
					}
				} else if(panelMemberType.equals("CEO")) {
					if (i == (temp.length-1)) {
						sbPanel.append("<span style=\"background-color: #f6f6f6; border-radius: 5px; padding: 2px 0px; margin-right: 3px;\" id=\"ceos_"+revieweeId+"_"+temp[i].trim()+"\"><input type=\"hidden\" name=\"ceosId_"+revieweeId+"\" value=\""+temp[i].trim()+"\" />"+hmEmpName.get(temp[i].trim())+" <a onclick=\"removeRevieweePanelMember('ceos_"+revieweeId+"_"+temp[i].trim()+"');\" href=\"javascript: void(0);\" style=\"color: red;\"><i class=\"fa fa-times\" aria-hidden=\"true\"></i></a>"+"</span>");
					} else {
						sbPanel.append("<span style=\"background-color: #f6f6f6; border-radius: 5px; padding: 2px 0px; margin-right: 3px;\" id=\"ceos_"+revieweeId+"_"+temp[i].trim()+"\"><input type=\"hidden\" name=\"ceosId_"+revieweeId+"\" value=\""+temp[i].trim()+"\" />"+hmEmpName.get(temp[i].trim())+" <a onclick=\"removeRevieweePanelMember('ceos_"+revieweeId+"_"+temp[i].trim()+"');\" href=\"javascript: void(0);\" style=\"color: red;\"><i class=\"fa fa-times\" aria-hidden=\"true\"></i></a>"+"</span>, ");
					}
				} else if(panelMemberType.equals("HR")) {
					if (i == (temp.length-1)) {
						sbPanel.append("<span style=\"background-color: #f6f6f6; border-radius: 5px; padding: 2px 0px; margin-right: 3px;\" id=\"hrs_"+revieweeId+"_"+temp[i].trim()+"\"><input type=\"hidden\" name=\"hrsId_"+revieweeId+"\" value=\""+temp[i].trim()+"\" />"+hmEmpName.get(temp[i].trim())+" <a onclick=\"removeRevieweePanelMember('hrs_"+revieweeId+"_"+temp[i].trim()+"');\" href=\"javascript: void(0);\" style=\"color: red;\"><i class=\"fa fa-times\" aria-hidden=\"true\"></i></a>"+"</span>");
					} else {
						sbPanel.append("<span style=\"background-color: #f6f6f6; border-radius: 5px; padding: 2px 0px; margin-right: 3px;\" id=\"hrs_"+revieweeId+"_"+temp[i].trim()+"\"><input type=\"hidden\" name=\"hrsId_"+revieweeId+"\" value=\""+temp[i].trim()+"\" />"+hmEmpName.get(temp[i].trim())+" <a onclick=\"removeRevieweePanelMember('hrs_"+revieweeId+"_"+temp[i].trim()+"');\" href=\"javascript: void(0);\" style=\"color: red;\"><i class=\"fa fa-times\" aria-hidden=\"true\"></i></a>"+"</span>, ");
					}
				}
			}
		}
		return sbPanel.toString();
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

	
	public Map<String, String> getAppraisalQuestionMap(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> AppraisalQuestion = new HashMap<String, String>();
		try {
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

	
	private String getManagerLocation() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String location = "";

		con = db.makeConnection(con);
		try {
			pst = con.prepareStatement("select e.wlocation_id from employee_official_details e where e.emp_id=?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
			while (rst.next()) {
				location = rst.getString(1);
			}
			rst.close();
			pst.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return location;
	}

	
	public String getEmployeeList(String existIds, String self, int type) {
		StringBuilder sb = null; 
		List<String> empList = new ArrayList<String>();
		if(existIds != null) {
			List<String> empList1 = Arrays.asList(existIds.split(","));
			for(int i=0; empList1 != null && !empList1.isEmpty() && i<empList1.size(); i++) {
				if(!empList1.get(i).trim().equals("") && !empList.contains(empList1.get(i).trim())) {
					empList.add(empList1.get(i).trim());
					if(sb == null) {
						sb = new StringBuilder();
						sb.append(","+empList1.get(i).trim()+",");
					} else {
						sb.append(empList1.get(i).trim()+",");
					}
				}
			}
		}
		if(sb == null) {
			sb = new StringBuilder();
		}
		return sb.toString();
	}
	
	
	
	/*public String getEmployeeList(String existIds, String self, int type) {
		StringBuilder sb = null; 
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		try {
			List<String> empList = new ArrayList<String>();
			if(existIds != null) {
				List<String> empList1 = Arrays.asList(existIds.split(","));
				for(int i=0; empList1 != null && !empList1.isEmpty() && i<empList1.size(); i++) {
					if(!empList1.get(i).trim().equals("") && !empList.contains(empList1.get(i).trim())) {
						empList.add(empList1.get(i).trim());
						if(sb == null) {
							sb = new StringBuilder();
							sb.append(","+empList1.get(i).trim()+",");
						} else {
							sb.append(empList1.get(i).trim()+",");
						}
					}
				}
			}
			if(self.length()>1) {
			 self=self.substring(1,self.length()-1);
			}
			
			if (type == 2) {

				pst = con.prepareStatement("select supervisor_emp_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and emp_id in ("+ self+ ") and supervisor_emp_id!=0");
				rs = pst.executeQuery();
				
				while(rs.next()) {
					if(rs.getString("supervisor_emp_id") != null && !empList.contains(rs.getString("supervisor_emp_id").trim())) {
						if(sb == null) {
							sb = new StringBuilder();
							sb.append(","+rs.getString("supervisor_emp_id").trim()+",");
						} else {
							sb.append(rs.getString("supervisor_emp_id").trim()+",");
						}
						empList.add(rs.getString("supervisor_emp_id").trim());
					}
				}
				rs.close();
				pst.close();
				
				if(sb == null) {
					sb = new StringBuilder();
				} 
				return sb.toString();
		
			} else if (type == 3) {

			} else if (type == 4) {
				
				pst=con.prepareStatement("select grade_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and emp_id in("+self+") group by grade_id");
				rs=pst.executeQuery();
				StringBuilder sb4=new StringBuilder();
				int cnt=0;
				while(rs.next()) {
					if(rs.getString("grade_id") != null && rs.getInt("grade_id")>0) {
						if(cnt==0) {
							sb4.append(rs.getString("grade_id").trim());
						} else {
							sb4.append(","+rs.getString("grade_id").trim());
						}
	//					System.out.println("sb4=====>"+sb4.toString());
						cnt++;
					}
				}
				rs.close();
				pst.close();
				
				pst=con.prepareStatement("select wlocation_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and emp_id in("+self+") group by wlocation_id");
				rs=pst.executeQuery();
				StringBuilder sb5=new StringBuilder();
				 cnt=0;
				while(rs.next()) {
					if(rs.getString("wlocation_id") != null  && rs.getInt("wlocation_id")>0) {
						if(cnt==0) {
							sb5.append(","+rs.getString("wlocation_id").trim()+",");
						}else{
							sb5.append(rs.getString("wlocation_id").trim()+",");
						}
	//					System.out.println("sb5=====>"+sb5.toString());
						cnt++;
					}
				}
				rs.close();
				pst.close();
				
				String strsb5 = (sb5 != null ? sb5.toString().substring(1, sb5.toString().length()-1) : "");
				//String strsb4 = (sb4 != null ? sb4.toString().substring(1, sb4.toString().length()-1) : "");
				pst=con.prepareStatement("select emp_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and wlocation_id in("+strsb5+") and grade_id in("+sb4.toString()+") group by emp_id");
				rs=pst.executeQuery();
				
				while(rs.next()) {
					if(rs.getString("emp_id") != null && !empList.contains(rs.getString("emp_id").trim())) {
						if(sb == null) {
							sb = new StringBuilder();
							sb.append(","+rs.getString("emp_id").trim()+",");
						}else{
							sb.append(rs.getString("emp_id").trim()+",");
						}
					empList.add(rs.getString("emp_id").trim());	
					}
				}
				rs.close();
				pst.close();
				if(sb == null) {
					sb = new StringBuilder();
				}
				return sb.toString();

			} else if (type == 5) {

				pst=con.prepareStatement("select wlocation_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and emp_id in("+self+") group by wlocation_id");
				rs=pst.executeQuery();
				StringBuilder sb5=new StringBuilder();
				int cnt=0;
				while(rs.next()){
					if(rs.getString("wlocation_id") != null && rs.getInt("wlocation_id") > 0) {
						if(cnt==0){
							sb5.append(rs.getString("wlocation_id").trim());
						}else{
							sb5.append(","+rs.getString("wlocation_id").trim());
	
						}
						cnt++;
					}
				}
				rs.close();
				pst.close();
				
				//String strsb5 = (sb5 != null ? sb5.toString().substring(1, sb5.toString().length()-1) : "");
				pst=con.prepareStatement("select emp_per_id from employee_official_details eod,user_details ud, employee_personal_details epd where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=? and wlocation_id in("+sb5.toString()+")");
				pst.setInt(1, 5);
				rs=pst.executeQuery();
				while(rs.next()) {
					if(rs.getString("emp_per_id") !=null && !empList.contains(rs.getString("emp_per_id").trim())) {
						if(sb == null) {
							sb = new StringBuilder();
							sb.append(","+rs.getString("emp_per_id").trim()+",");
						} else {
							sb.append(rs.getString("emp_per_id").trim()+",");
						}
					empList.add(rs.getString("emp_per_id").trim());	
					}
				}
				rs.close();
				pst.close();
				
				if(sb == null) {
					sb = new StringBuilder();
				}
				return sb.toString();
			
			} else if (type == 6) {

			} else if (type == 7) {
				
				pst=con.prepareStatement("select wlocation_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and emp_id in("+self+") group by wlocation_id");
				rs=pst.executeQuery();
				StringBuilder sb5=new StringBuilder();
				int cnt=0;
				while(rs.next()) {
					if(rs.getString("wlocation_id") != null  && rs.getInt("wlocation_id")>0) {
						if(cnt==0) {
							sb5.append(rs.getString("wlocation_id").trim());
						} else {
							sb5.append(","+rs.getString("wlocation_id").trim());
	
						}
						cnt++;
					}
				}
				rs.close();
				pst.close();
				
				//String strsb5 = (sb5 != null ? sb5.toString().substring(1, sb5.toString().length()-1) : "");
				pst=con.prepareStatement("select emp_per_id from employee_official_details eod,user_details ud, employee_personal_details epd where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=? and wlocation_id in("+sb5.toString()+")");
				pst.setInt(1, 7);
				rs=pst.executeQuery();
				while(rs.next()) {
					if(rs.getString("emp_per_id") != null && !empList.contains(rs.getString("emp_per_id").trim())) {
						if(sb == null) {
							sb = new StringBuilder();
							sb.append(","+rs.getString("emp_per_id").trim()+",");
						} else {
							sb.append(rs.getString("emp_per_id").trim()+",");
						}
					empList.add(rs.getString("emp_per_id").trim());	
					}
				}
				rs.close();
				pst.close();
				
				if(sb == null) {
					sb = new StringBuilder();
				}
				return sb.toString();
			} else if (type == 8) {

			} else if (type == 9) {

			} else if (type == 13) {
				
				pst=con.prepareStatement("select hod_emp_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id=eod.emp_id "
						+"  and is_alive = true and hod_emp_id > 0  and eod.emp_id in("+self+")");
				rs=pst.executeQuery();
				while(rs.next()) {
					if(rs.getString("hod_emp_id") != null && !empList.contains(rs.getString("hod_emp_id").trim())) {
						if(sb == null) {
							sb = new StringBuilder();
							sb.append(","+rs.getString("hod_emp_id").trim()+",");
						} else {
							sb.append(rs.getString("hod_emp_id").trim()+",");
						}
					empList.add(rs.getString("hod_emp_id").trim());	
					}
				}
				rs.close();
				pst.close();
				
				if(sb == null) {
					sb = new StringBuilder();
				}
				return sb.toString();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return null;
	}*/

	
	private List<String> getOrientationMemberDetails(int id) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		List<String> memberList = new ArrayList<String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from orientation_details where orientation_id=?");
			pst.setInt(1, id);
			rs = pst.executeQuery();
			while (rs.next()) {
				memberList.add(rs.getString("member_id").trim());
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
		return memberList;
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

	public String getOldOrientVal() {
		return oldOrientVal;
	}

	public void setOldOrientVal(String oldOrientVal) {
		this.oldOrientVal = oldOrientVal;
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

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillWLocation> getWorkList() {
		return workList;
	}

	public void setWorkList(List<FillWLocation> workList) {
		this.workList = workList;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public List<FillEmployee> getFinalizationList() {
		return finalizationList;
	}

	public void setFinalizationList(List<FillEmployee> finalizationList) {
		this.finalizationList = finalizationList;
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

	public String getWeekdayValue() {
		return weekdayValue;
	}

	public void setWeekdayValue(String weekdayValue) {
		this.weekdayValue = weekdayValue;
	}

	public String getAnnualDayValue() {
		return annualDayValue;
	}

	public void setAnnualDayValue(String annualDayValue) {
		this.annualDayValue = annualDayValue;
	}

	public String getAnnualMonthValue() {
		return annualMonthValue;
	}

	public void setAnnualMonthValue(String annualMonthValue) {
		this.annualMonthValue = annualMonthValue;
	}

	public String getDayValue() {
		return dayValue;
	}

	public void setDayValue(String dayValue) {
		this.dayValue = dayValue;
	}

	public String getMonthdayValue() {
		return monthdayValue;
	}

	public void setMonthdayValue(String monthdayValue) {
		this.monthdayValue = monthdayValue;
	}

	public List<String> getMonthValue() {
		return monthValue;
	}

	public void setMonthValue(List<String> monthValue) {
		this.monthValue = monthValue;
	}

	public String getHideSelfFillEmpIds() {
		return hideSelfFillEmpIds;
	}

	public void setHideSelfFillEmpIds(String hideSelfFillEmpIds) {
		this.hideSelfFillEmpIds = hideSelfFillEmpIds;
	}

	public List<FillEmployee> getReviewerList() {
		return reviewerList;
	}

	public void setReviewerList(List<FillEmployee> reviewerList) {
		this.reviewerList = reviewerList;
	}

	public String getReviewerId() {
		return reviewerId;
	}

	public void setReviewerId(String reviewerId) {
		this.reviewerId = reviewerId;
	}

	public List<String> getReviewervalue() {
		return reviewervalue;
	}

	public void setReviewervalue(List<String> reviewervalue) {
		this.reviewervalue = reviewervalue;
	}

	public String getAnonymousReview() {
		return anonymousReview;
	}

	public void setAnonymousReview(String anonymousReview) {
		this.anonymousReview = anonymousReview;
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

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getEligibilityMinDaysBeforeStartDate() {
		return eligibilityMinDaysBeforeStartDate;
	}

	public void setEligibilityMinDaysBeforeStartDate(String eligibilityMinDaysBeforeStartDate) {
		this.eligibilityMinDaysBeforeStartDate = eligibilityMinDaysBeforeStartDate;
	}

	public String getEligibilityMinDaysBeforeEndDate() {
		return eligibilityMinDaysBeforeEndDate;
	}

	public void setEligibilityMinDaysBeforeEndDate(String eligibilityMinDaysBeforeEndDate) {
		this.eligibilityMinDaysBeforeEndDate = eligibilityMinDaysBeforeEndDate;
	}

	
}
