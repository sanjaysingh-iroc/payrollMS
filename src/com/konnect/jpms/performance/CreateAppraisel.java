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
import org.apache.struts2.interceptor.SessionAware;

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
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;

public class CreateAppraisel implements ServletRequestAware, IStatements, Runnable {
	HttpSession session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;

	private List<FillLevel> levelList;
	private List<FillGrade> gradeList;
	private List<FillDesig> desigList;
	private List<FillAttribute> attributeList;
	private List<FillAnswerType> ansTypeList;
	private List<FillFrequency> frequencyList;
	private String ansTypeOption; 

	private String id;
	private String step;
	private String appraiselName;
	private String appraisal_description;
	private String oreinted;
	private String employee;
	private String finalizationName;
	private String strLevel;
	private String strDesignationUpdate;
	private String empGrade;
	private String strDepart;
	private String strWlocation;
	private String emp_status;

	private String userlocation;

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

	private List<FillEmployee> empList;
	private List<FillEmployee> finalizationList;
	private List<FillEmployee> reviewerList;
	private List<FillWLocation> workList;
	private List<FillDepartment> departmentList;
	private List<FillOrientation> orientationList;
	private List<FillOrganisation> organisationList;
	
	private String annualDay;
	private String annualMonth;
	private String day;
	private String monthday;
	private String month;
	
	private String main_level_id;
	private String strOrg;
	
	private String reviewerId;
	
	private String appraisal_instruction;
	private String hideSelfFillEmpIds;
	
	private String anonymousReview;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		
		UtilityFunctions uF = new UtilityFunctions();
		String submit = request.getParameter("submit");
		String saveandnew = request.getParameter("saveandnew");
		String submitandpublish = request.getParameter("submitandpublish");
		String saveandnewsystem=request.getParameter("saveandnewsystem");
		String cancel = request.getParameter("cancel");
		
//		System.out.println("initialize id ===>> " + id);
		
		if(id == null) {
			initialize(uF);
		}

		if(cancel!= null && cancel.equals("Cancel")) {
			return "cancel";
		}
	//	System.out.println("submitandpublish1==>"+submitandpublish);
		if (id != null && step != null && !step.equals("2")) {
			String levelID=getSelfIDs(getId());			
			if (levelID != null && levelID.length()>0) {
//				System.out.println("levelID in id != null :: "+levelID);
				attributeList = new FillAttribute(request).fillElementAttribute(levelID);
			} else {				
				attributeList = new FillAttribute(request).fillElementAttribute(null);
			}
			getattribute();
			ansTypeList = new FillAnswerType(request).fillAnswerType();
			String appraisalSystem = request.getParameter("appraisalSystem");
			
//			System.out.println("appraisalSystem==>"+appraisalSystem +"==>saveandnewsystem==>"+saveandnewsystem);
			getOrientationValue(uF.parseToInt(getOreinted()));
			if (saveandnewsystem != null && saveandnewsystem.equals("Save And Add New Subsection")) {
				
				insertInMainLevelDetails();
				if(appraisalSystem != null && !appraisalSystem.equals("")) {
				if (appraisalSystem.equals("1")) {
					selectFunction();
					getSelectedOrientationPosition(uF);
				} else if (appraisalSystem.equals("2")) {
					addOtherQuestions();
					getSelectedOrientationPosition(uF);
				} else if (appraisalSystem.equals("3") || appraisalSystem.equals("5")) {
					insertGoalTarget();
					getSelectedOrientationPosition(uF);
				} else if (appraisalSystem.equals("4")) {
					insertKRA();
					getSelectedOrientationPosition(uF);
		//===start parvez date: 22-12-2021===		
				} else if(appraisalSystem.equals("6")){
					insertNxtAppraisalCycleGoal();
					getSelectedOrientationPosition(uF);
				}
		//===end parvez date: 22-12-2021===
			 }
			} else if (appraisalSystem != null) {
				//System.out.println("in else ");
				insertInMainLevelDetails();

				if (appraisalSystem.equals("1")) {
					selectFunction();
					getSelectedOrientationPosition(uF);
				} else if (appraisalSystem.equals("2")) { 
					addOtherQuestions();
					getSelectedOrientationPosition(uF);
				} else if (appraisalSystem.equals("3") || appraisalSystem.equals("5")) {
					insertGoalTarget();
					getSelectedOrientationPosition(uF);
				} else if (appraisalSystem.equals("4")) {
					insertKRA();
					getSelectedOrientationPosition(uF);
		//===start parvez date: 22-12-2021===		
				} else if(appraisalSystem.equals("6")){
					insertNxtAppraisalCycleGoal();
					getSelectedOrientationPosition(uF);
				}
		//===end parvez date: 22-12-2021===		
				request.setAttribute("mainlevelTitle", null);
				request.setAttribute("mainshortDesrciption", null);
				request.setAttribute("mainlongDesrciption", null);
				request.setAttribute("attribname", null);
				request.setAttribute("attribid", null);
				request.setAttribute("sectionWeightage",null);
				setMain_level_id(null);
			}
			
		
			getOtherAnsType();
			getAppraisalQuestionList();
			getLevelDetails();
			request.setAttribute(PAGE, "/jsp/performance/CreateAppraisel.jsp");
			request.setAttribute(TITLE, "Create Appraisal");
//			System.out.println("submitandpublish3==>"+submitandpublish);
			if(submitandpublish!= null && submitandpublish.equals("Save And Publish")) {
				updateStatus();
				return "update";
			} else if (saveandnew != null && saveandnew.equals("Save And Add New Section")) {	
				
				getOrientationValue(uF.parseToInt(getOreinted()));
				return "success";
			} else if (saveandnewsystem != null && saveandnewsystem.equals("Save And Add New Subsection")) {
				getOrientationValue(uF.parseToInt(getOreinted()));
				return "success";
			} else {
				return "update";
			}
		} else if (appraiselName != null) {
			if (submit != null && submit.equals("Save") && step != null && step.equals("1")) {
				addAppraisal(uF);
			}
			userlocation = getManagerLocation();
			List<String> alDates = getReviewStartAndEndDates();
			if (empGrade != null) {
				empList = new FillEmployee(request).fillEmployeeName(uF.parseToInt(empGrade));
			} else if (strUserType != null && strUserType.equals(MANAGER)) {
				empList = new FillEmployee(request).fillEmployeeNameBySupervisor(strSessionEmpId);
			} else {
				String strStartDt = null;
				String strEndDt = null;
				String strMinDayBeforeStartDt = null;
				String strMinDayBeforeEndDt = null;
				if(alDates!=null && alDates.size()>0) {
					strStartDt = alDates.get(0);
					strEndDt = alDates.get(1);
					strMinDayBeforeStartDt = alDates.get(2);
					strMinDayBeforeEndDt = alDates.get(3);
					empList = new FillEmployee(request).fillEmployeeNameByLocationWithJoiningDate(userlocation, strStartDt, strMinDayBeforeStartDt, strEndDt, strMinDayBeforeEndDt);
				} else {
					empList = new FillEmployee(request).fillEmployeeNameByLocation(userlocation);
				}
			}
			
			request.setAttribute(PAGE, "/jsp/performance/CreateAppraisel.jsp");
			request.setAttribute(TITLE, TCreateReview);
		} else if(submit != null && submit.equals("Save") && step != null && step.equals("2")) {
		//			System.out.println("STEP ===> "+step);
		//			System.out.println("ID ===> "+id);
				addTeamMember(uF);
				
				getOtherAnsType();
				getLevelDetails();
				getOrientationValue(uF.parseToInt(getOreinted()));
				String levelID=getSelfIDs(getId());
		//		System.out.println("levelID in appraiselName != null :: "+levelID);
				if (levelID != null && levelID.length()>0) {
					attributeList = new FillAttribute(request).fillElementAttribute(levelID);
				} else {				
					attributeList = new FillAttribute(request).fillElementAttribute(null);
				}
				
				getattribute();
				request.setAttribute(PAGE, "/jsp/performance/CreateAppraisel.jsp");
				request.setAttribute(TITLE, "Create Review");
				}
		if(getStep() == null) {
			setStep("1");
		} else {
			int cnt = uF.parseToInt(getStep());
			cnt++;
			setStep(""+cnt);
		}
		request.setAttribute("id", id);
		getAppraisalQuestionList();
		request.setAttribute(PAGE, "/jsp/performance/CreateAppraisel.jsp");
		request.setAttribute(TITLE, "Create Review");
		return "success";
	}
	
	
	private void updateStatus() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update appraisal_details set is_publish= true where appraisal_details_id = ?");
			pst.setInt(1, uF.parseToInt(id));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("update appraisal_details_frequency set is_appraisal_publish= true where appraisal_id = ?");
			pst.setInt(1, uF.parseToInt(id));
			pst.executeUpdate();
			pst.close();
			
			String strDomain = request.getServerName().split("\\.")[0];
			setDomain(strDomain);
//			Thread th = new Thread(this);
//			th.start();
			
			Map<String, String> hmReviewData = getReviewDetails(con);
			Map<String, Map<String, String>> hmRevieweewiseAppraiser = getRevieweewiseAppraiser(con);
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);//Created By Dattatray Date:11-11-21
			Iterator<String> it = hmRevieweewiseAppraiser.keySet().iterator();
			while(it.hasNext()) {
				String strRevieweeId = it.next();
				Map<String, String> hmRevieweeNameData = hmEmpInfo.get(strRevieweeId);
				
				Map<String, String> hmRevieweeData = hmRevieweewiseAppraiser.get(strRevieweeId);
				List<List<String>> allIdList = new ArrayList<List<String>>();
				//Started By Dattatray Date:11-11-21
				if(uF.parseToBoolean(hmFeatureStatus.get(F_REVIEW_PUBLISH_MAIL_SENT_TO_REVIEWEE))) {
					if(hmRevieweeData.get("REVIEW_SELFID") != null && !hmRevieweeData.get("REVIEW_SELFID").equals("")) {
						List<String> selfID = Arrays.asList(hmRevieweeData.get("REVIEW_SELFID").split(",")); 
						for (int i = 0; selfID != null && i < selfID.size(); i++) {
							if(selfID.get(i) != null && !selfID.get(i).equals("")) {
								List<String> innerList = new ArrayList<String>();
								innerList.add(selfID.get(i));
								innerList.add("Self");
								allIdList.add(innerList);
							}
						}
					}
				}//Ended By Dattatray Date:11-11-21
				
				if(hmRevieweeData.get("REVIEW_PEERID") != null && !hmRevieweeData.get("REVIEW_PEERID").equals("")) {
					List<String> peerID = Arrays.asList(hmRevieweeData.get("REVIEW_PEERID").split(",")); 
					for (int i = 0; peerID != null && i < peerID.size(); i++) {
						if(peerID.get(i) != null && !peerID.get(i).equals("")) {
							List<String> innerList = new ArrayList<String>();
							innerList.add(peerID.get(i));
							innerList.add("Peer");
							allIdList.add(innerList);
						}
					}
				}
				if(hmRevieweeData.get("REVIEW_MANAGERID") != null && !hmRevieweeData.get("REVIEW_MANAGERID").equals("")) {
					List<String> managerID = Arrays.asList(hmRevieweeData.get("REVIEW_MANAGERID").split(",")); 
					for (int i = 0; managerID != null && i < managerID.size(); i++) {
						if(managerID.get(i) != null && !managerID.get(i).equals("")) {
							List<String> innerList = new ArrayList<String>();
							innerList.add(managerID.get(i));
							innerList.add("Manager");
							allIdList.add(innerList);
						}
					}
				}
				if(hmRevieweeData.get("REVIEW_HRID") != null && !hmRevieweeData.get("REVIEW_HRID").equals("")) {
					List<String> hrID = Arrays.asList(hmRevieweeData.get("REVIEW_HRID").split(",")); 
					for (int i = 0; hrID != null && i < hrID.size(); i++) {
						if(hrID.get(i) != null && !hrID.get(i).equals("")) {
							List<String> innerList = new ArrayList<String>();
							innerList.add(hrID.get(i));
							innerList.add("HR");
							allIdList.add(innerList);
						}
					}
				}
				
				if(hmRevieweeData.get("REVIEW_CEOID") != null && !hmRevieweeData.get("REVIEW_CEOID").equals("")) {
					List<String> ceoID = Arrays.asList(hmRevieweeData.get("REVIEW_CEOID").split(",")); 
					for (int i = 0; ceoID != null && i < ceoID.size(); i++) {
						if(ceoID.get(i) != null && !ceoID.get(i).equals("")) {
							List<String> innerList = new ArrayList<String>();
							innerList.add(ceoID.get(i));
							innerList.add("CEO");
							allIdList.add(innerList);
						}
					}
				}
				
				if(hmRevieweeData.get("REVIEW_HODID") != null && !hmRevieweeData.get("REVIEW_HODID").equals("")) {
					List<String> hodID = Arrays.asList(hmRevieweeData.get("REVIEW_HODID").split(",")); 
					for (int i = 0; hodID != null && i < hodID.size(); i++) {
						if(hodID.get(i) != null && !hodID.get(i).equals("")) {
							List<String> innerList = new ArrayList<String>();
							innerList.add(hodID.get(i));
							innerList.add("HOD");
							allIdList.add(innerList);
						}
					}
				}
				
				if(hmRevieweeData.get("REVIEW_OTHERID") != null && !hmRevieweeData.get("REVIEW_OTHERID").equals("")) {
					List<String> otherID = Arrays.asList(hmRevieweeData.get("REVIEW_OTHERID").split(",")); 
					for (int i = 0; otherID != null && i < otherID.size(); i++) {
						if(otherID.get(i) != null && !otherID.get(i).equals("")) {
							List<String> innerList = new ArrayList<String>();
							innerList.add(otherID.get(i));
							innerList.add("Anyone");
							allIdList.add(innerList);
						}
					}
				}
				
				if(hmRevieweeData.get("REVIEW_SUBORDINATEID") != null && !hmRevieweeData.get("REVIEW_SUBORDINATEID").equals("")) {
					List<String> subordinateID = Arrays.asList(hmRevieweeData.get("REVIEW_SUBORDINATEID").split(",")); 
					for (int i = 0; subordinateID != null && i < subordinateID.size(); i++) {
						if(subordinateID.get(i) != null && !subordinateID.get(i).equals("")) {
							List<String> innerList = new ArrayList<String>();
							innerList.add(subordinateID.get(i));
							innerList.add("Sub-ordinate");
							allIdList.add(innerList);
						}
					}
				}
				
				if(hmRevieweeData.get("REVIEW_GRANDSUPERVISORID") != null && !hmRevieweeData.get("REVIEW_GRANDSUPERVISORID").equals("")) {
					List<String> gSupervisorID = Arrays.asList(hmRevieweeData.get("REVIEW_GRANDSUPERVISORID").split(",")); 
					for (int i = 0; gSupervisorID != null && i < gSupervisorID.size(); i++) {
						if(gSupervisorID.get(i) != null && !gSupervisorID.get(i).equals("")) {
							List<String> innerList = new ArrayList<String>();
							innerList.add(gSupervisorID.get(i));
							innerList.add("Group Head");
							allIdList.add(innerList);
						}
					}
				}
				
				if(hmRevieweeData.get("REVIEW_OTHERPEERID") != null && !hmRevieweeData.get("REVIEW_OTHERPEERID").equals("")) {
					List<String> otherPeerID = Arrays.asList(hmRevieweeData.get("REVIEW_OTHERPEERID").split(",")); 
					for (int i = 0; otherPeerID != null && i < otherPeerID.size(); i++) {
						if(otherPeerID.get(i) != null && !otherPeerID.get(i).equals("")) {
							List<String> innerList = new ArrayList<String>();
							innerList.add(otherPeerID.get(i));
							innerList.add("Other Peer");
							allIdList.add(innerList);
						}
					}
				}
				
				
				for (int i = 0; allIdList != null && !allIdList.isEmpty() && i < allIdList.size(); i++) {
					List<String> innerList = allIdList.get(i);
					if(innerList.get(0) != null && !innerList.get(0).equals("")) {
						Map<String, String> hmEmpInner = hmEmpInfo.get(innerList.get(0));
						Notifications nF = new Notifications(N_NEW_REVIW_PUBLISH, CF);
						nF.setDomain(getStrDomain());
						nF.request = request;
						nF.setStrEmpId(innerList.get(0));
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrRevieweeName(hmRevieweeNameData.get("FNAME")+" " +hmRevieweeNameData.get("LNAME"));
						nF.setStrRoleType(innerList.get(1));
						nF.setStrReviewName(hmReviewData.get("REVIEW_NAME"));
						nF.setStrReviewStartdate(uF.getDateFormat(hmReviewData.get("REVIEW_STARTDATE"), DBDATE, CF.getStrReportDateFormat()));
						nF.setStrReviewEnddate(uF.getDateFormat(hmReviewData.get("REVIEW_ENDDATE"), DBDATE, CF.getStrReportDateFormat()));
			
						nF.setStrEmpFname(hmEmpInner.get("FNAME"));
						nF.setStrEmpLname(hmEmpInner.get("LNAME"));
						nF.setEmailTemplate(true);				
						nF.sendNotifications();
					}
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private Map<String, Map<String, String>> getRevieweewiseAppraiser(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, Map<String, String>> hmRevieweewiseAppraiser = new HashMap<String, Map<String, String>>();
		try {
			pst = con.prepareStatement("select * from appraisal_reviewee_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String> hmRevieweeData = new HashMap<String, String>();	
				
				hmRevieweeData.put("REVIEW_SELFID", rs.getString("reviewee_id"));
				hmRevieweeData.put("REVIEW_PEERID", rs.getString("peer_ids"));
				hmRevieweeData.put("REVIEW_MANAGERID", rs.getString("supervisor_ids"));
				hmRevieweeData.put("REVIEW_HRID", rs.getString("hr_ids"));
				hmRevieweeData.put("REVIEW_OTHERID", rs.getString("other_ids"));
				hmRevieweeData.put("REVIEW_CEOID", rs.getString("ceo_ids"));
				hmRevieweeData.put("REVIEW_HODID", rs.getString("hod_ids"));
				hmRevieweeData.put("REVIEW_SUBORDINATEID", rs.getString("subordinate_ids"));
				hmRevieweeData.put("REVIEW_GRANDSUPERVISORID", rs.getString("grand_supervisor_ids"));
				hmRevieweeData.put("REVIEW_OTHERPEERID", rs.getString("other_peer_ids"));
				
				hmRevieweewiseAppraiser.put(rs.getString("reviewee_id"), hmRevieweeData);
			}
			rs.close();
			pst.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hmRevieweewiseAppraiser;
	}
	
	
	private Map<String, String> getReviewDetails(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, String> hmReviewData = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmReviewData.put("REVIEW_NAME", rs.getString("appraisal_name"));
				hmReviewData.put("REVIEW_STARTDATE", rs.getString("from_date"));
				hmReviewData.put("REVIEW_ENDDATE", rs.getString("to_date"));
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hmReviewData;
	}

	
	private void insertInMainLevelDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;		
		UtilityFunctions uF=new UtilityFunctions();
		
		String levelTitle = request.getParameter("levelTitle");
		String shortDesrciption = request.getParameter("shortDesrciption");
		String longDesrciption = request.getParameter("longDesrciption");
		String attribute = request.getParameter("attribute");
		String sectionWeightage = request.getParameter("sectionWeightage");
		
		try {
			con = db.makeConnection(con);
			Map<String,String> orientationMemberMp=getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
			
//			System.out.println("getMain_level_id()======>"+getMain_level_id());
			if(getMain_level_id()==null || getMain_level_id().equals("") || getMain_level_id().equals("null")) {
				pst = con.prepareStatement("insert into appraisal_main_level_details(level_title,short_description,long_description,appraisal_id," +
					"attribute_id,section_weightage,added_by,hr,manager,peer,self,subordinate,grouphead,vendor,client,entry_date,ceo,hod,other_peer) " +
					"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setString(1, levelTitle);
				pst.setString(2, shortDesrciption);
				pst.setString(3, longDesrciption);
				pst.setInt(4, uF.parseToInt(id));
				pst.setInt(5, uF.parseToInt(attribute));
				pst.setString(6, sectionWeightage);
				pst.setInt(7, uF.parseToInt(strSessionEmpId));
				if (hmOrientMemberID.get("HR") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("HR"))) != null) {
					pst.setInt(8,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("HR")))));
				} else {
					pst.setInt(8, 0);
				}
				if (hmOrientMemberID.get("Manager") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Manager"))) != null) {
					pst.setInt(9, uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Manager")))));
				} else {
					pst.setInt(9, 0);
				}
				if (hmOrientMemberID.get("Peer") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Peer"))) != null) {
					pst.setInt(10,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Peer")))));
				} else {
					pst.setInt(10, 0);
				}
				if (hmOrientMemberID.get("Self") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Self"))) != null) {
					pst.setInt(11,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Self")))));
				} else {
					if(getOreinted() != null && getOreinted().equals("5")) {
						pst.setInt(11, 1);
					} else {
						pst.setInt(11, 0);
					}
				}
				if (hmOrientMemberID.get("Sub-ordinate") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Sub-ordinate"))) != null) {
					pst.setInt(12,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Sub-ordinate")))));
				} else {
					pst.setInt(12, 0);
				}
				if (hmOrientMemberID.get("GroupHead") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("GroupHead"))) != null) {
					pst.setInt(13,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("GroupHead")))));
				} else {
					pst.setInt(13, 0);
				}
				if (hmOrientMemberID.get("Vendor") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Vendor"))) != null) {
					pst.setInt(14,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Vendor")))));
				} else {
					pst.setInt(14, 0);
				}
				if (hmOrientMemberID.get("Client") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Client"))) != null) {
					pst.setInt(15,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Client")))));
				} else {
					pst.setInt(15, 0);
				}
				
				pst.setTimestamp(16, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
				
				if (hmOrientMemberID.get("CEO") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("CEO"))) != null) {
					pst.setInt(17,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("CEO")))));
				} else {
					pst.setInt(17, 0);
				}
				if (hmOrientMemberID.get("HOD") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("HOD"))) != null) {
					pst.setInt(18,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("HOD")))));
				} else {
					pst.setInt(18, 0);
				}
				
				if (hmOrientMemberID.get("Other Peer") != null && request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Other Peer"))) != null) {
					pst.setInt(19,uF.parseToInt((String) request.getParameter(orientationMemberMp.get(hmOrientMemberID.get("Other Peer")))));
				} else {
					pst.setInt(19, 0);
				}
				pst.execute();
				pst.close();
				
				int main_level_id = 0;
				pst = con.prepareStatement("select max(main_level_id) from appraisal_main_level_details");
				rs = pst.executeQuery();
				while (rs.next()) {
					main_level_id = rs.getInt(1);
				}
				rs.close();
				pst.close();
				
				setMain_level_id(""+main_level_id);
				request.setAttribute("mainlevelTitle", levelTitle);
				request.setAttribute("mainshortDesrciption", shortDesrciption);
				request.setAttribute("mainlongDesrciption", longDesrciption);
				Map<String, String> attributeMp = getAttributeMap();
				request.setAttribute("attribname",uF.showData(attributeMp.get(attribute), ""));
				request.setAttribute("attribid",attribute);
				request.setAttribute("sectionWeightage",sectionWeightage);
			} else {
				request.setAttribute("mainlevelTitle", levelTitle);
				request.setAttribute("mainshortDesrciption", shortDesrciption);
				request.setAttribute("mainlongDesrciption", longDesrciption);
				Map<String, String> attributeMp = getAttributeMap();
				request.setAttribute("attribname",uF.showData(attributeMp.get(attribute), ""));
				request.setAttribute("attribid",attribute);
				request.setAttribute("sectionWeightage",sectionWeightage);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void getSelectedOrientationPosition(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, String> orientPosition = new HashMap<String, String>();
		try {
//			System.out.println("getMain_level_id() in method ;;;;;;;;;;;; "+getMain_level_id());
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_main_level_details where main_level_id=?");
			pst.setInt(1, uF.parseToInt(getMain_level_id()));
			rs = pst.executeQuery();
//			System.out.println("pst ;;;;;;;;;;;; "+pst);
			while (rs.next()) {
				orientPosition.put("HR", rs.getString("hr"));
				orientPosition.put("Manager", rs.getString("manager"));
				orientPosition.put("Self", rs.getString("self"));
				orientPosition.put("Peer", rs.getString("peer"));
				orientPosition.put("Client", rs.getString("client"));
				orientPosition.put("Sub-ordinate", rs.getString("subordinate"));
				orientPosition.put("GroupHead", rs.getString("grouphead"));
				orientPosition.put("Vendor", rs.getString("vendor"));
				orientPosition.put("CEO", rs.getString("ceo"));
				orientPosition.put("HOD", rs.getString("hod"));
				orientPosition.put("Other Peer", rs.getString("other_peer"));
			}
			rs.close();
			pst.close();
//			System.out.println("orientPosition ::: "+orientPosition);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("orientPosition",orientPosition);
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
			
			while(rs.next()) {
				empID=rs.getString("self_ids");
			}
			rs.close();
			pst.close();
			
			if(empID!=null && !empID.equals("")) {
				
				empID=empID.substring(1,empID.length()-1);
//				System.out.println("empID=====>"+empID);
				List<String> levellistID=new ArrayList<String>();
				pst = con.prepareStatement("select ld.level_id from level_details ld right join (select * from designation_details dd " +
						"right join (select *, gd.designation_id as designationid from employee_official_details eod, grades_details gd " +
						"where gd.grade_id=eod.grade_id and eod.emp_id in ("+empID+")) a on a.designationid=dd.designation_id)" +
						" a on a.level_id=ld.level_id");
//				System.out.println("pst==>"+pst);
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
					} else {
						levelID+=","+levelid;
					}
					i++;
				}
//				System.out.println("levelID=====>"+levelID);
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

	
	private void insertKRA() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		con = db.makeConnection(con);
		
		Map<String, String> hmOrientMemberID = getOrientMemberID(con);

		String subsectionName = request.getParameter("subsectionname");
		String subsectionDescription = request.getParameter("subsectionDescription");
		String subsectionLongDescription = request.getParameter("subsectionLongDescription");
		String subSectionWeightage = request.getParameter("subSectionWeightage");
		
//		System.out.println("addOtherQuestions levelTitle : "+levelTitle);
//		System.out.println("addOtherQuestions shortDesrciption : "+shortDesrciption);
//		System.out.println("addOtherQuestions longDesrciption : "+longDesrciption);
		
		String attribute = request.getParameter("attribute");
		String appraisalSystem = request.getParameter("appraisalSystem");
		String scoreCard = request.getParameter("scoreCard");

		String[] goalId = request.getParameterValues("goalId");
		
		Map<String, String> orientationMemberMp = getOrientationMember(con);
		try {
			pst = con.prepareStatement("insert into appraisal_level_details(level_title,short_description,long_description,appraisal_system," +
				"scorecard_type,appraisal_id,main_level_id,attribute_id,subsection_weightage,added_by,entry_date) values(?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setString(1, subsectionName);
			pst.setString(2, subsectionDescription);
			pst.setString(3, subsectionLongDescription);
			pst.setInt(4, uF.parseToInt(appraisalSystem));
			pst.setInt(5, uF.parseToInt(scoreCard));
			pst.setInt(6, uF.parseToInt(id));
			pst.setInt(7, uF.parseToInt(getMain_level_id()));
			pst.setInt(8, uF.parseToInt(attribute));
			pst.setString(9, subSectionWeightage);
			pst.setInt(10, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(11, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.execute();
			pst.close();
			
			int appraisal_level_id = 0;
			pst = con.prepareStatement("select max(appraisal_level_id) from appraisal_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rst = pst.executeQuery();
			while (rst.next()) {
				appraisal_level_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			Map<String, String> hmKRAName = new HashMap<String, String>();
			String kraTypes = INDIVIDUAL_GOAL+","+INDIVIDUAL_KRA+","+EMPLOYEE_KRA;
			pst = con.prepareStatement("select * from goal_kras where goal_type in ("+kraTypes+")");
			rst = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while (rst.next()) {
				hmKRAName.put(rst.getString("goal_kra_id"), rst.getString("kra_description"));
			}
			rst.close();
			pst.close();
			
			Map<String, String> hmKRADescription = new HashMap<String, String>();
			pst = con.prepareStatement("select * from goal_details where goal_type in ("+kraTypes+")");
			rst = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while (rst.next()) {
				hmKRADescription.put(rst.getString("goal_id"), rst.getString("goal_description"));
			}
			rst.close();
			pst.close();
			
//			System.out.println("hmKRAName ====>> " + hmKRAName);
			for (int i = 0; goalId != null && i < goalId.length; i++) {
				String ansType = request.getParameter("ansType");
				String chkCaculationBasis = request.getParameter("chkCaculationBasis");		//added by parvez date: 28-02-2023
				/*int question_id = uF.parseToInt(hidequeid[i]);*/
				int question_id = 0;
				
				String optiona = request.getParameter("optiona"+ goalId[i]);
				String optionb = request.getParameter("optionb"+ goalId[i]);
				String optionc = request.getParameter("optionc"+ goalId[i]);
				String optiond = request.getParameter("optiond"+ goalId[i]);
				String optione = request.getParameter("optione"+ goalId[i]);
				String rateoptiona = request.getParameter("rateoptiona"+ goalId[i]);
				String rateoptionb = request.getParameter("rateoptionb"+ goalId[i]);
				String rateoptionc = request.getParameter("rateoptionc"+ goalId[i]);
				String rateoptiond = request.getParameter("rateoptiond"+ goalId[i]);
				String rateoptione = request.getParameter("rateoptione"+ goalId[i]);
				
				String goalWeightage = request.getParameter("goalWeightage"+ goalId[i]);
				String goalID = request.getParameter("goalID"+ goalId[i]);
//				System.out.println("GOal ID ===> "+goalId[i]+ "  optiona === "+optiona + "  optiona=b === "+optionb + "  optionc === "+optionc + "  optiond === "+optiond);
				
				String[] correct = request.getParameterValues("correct"+ goalId[i]);
				StringBuilder option = new StringBuilder();

				for (int ab = 0; correct != null && ab < correct.length; ab++) {
					option.append(correct[ab] + ",");
				}
				
				pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
					"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans," +
					"is_add,question_type,goal_kra_target_id,app_system_type,kra_id)values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
				pst.setString(1, hmKRAName.get(goalId[i]));
				pst.setString(2, uF.showData(optiona, ""));
				pst.setString(3, uF.showData(optionb, ""));
				pst.setString(4, uF.showData(optionc, ""));
				pst.setString(5, uF.showData(optiond, ""));
				pst.setString(6, uF.showData(optione, ""));
				pst.setInt(7, uF.parseToInt(rateoptiona));
				pst.setInt(8, uF.parseToInt(rateoptionb));
				pst.setInt(9, uF.parseToInt(rateoptionc));
				pst.setInt(10, uF.parseToInt(rateoptiond));
				pst.setInt(11, uF.parseToInt(rateoptione));
				pst.setString(12, option.toString());
				pst.setBoolean(13, false);
				pst.setInt(14, uF.parseToInt(ansType));
				pst.setInt(15, uF.parseToInt(goalID));
				pst.setInt(16, uF.parseToInt(appraisalSystem));
				pst.setInt(17, uF.parseToInt(goalId[i]));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("select max(question_bank_id) from question_bank");
				rst = pst.executeQuery();
				while (rst.next()) {
					question_id = rst.getInt(1);
				}
				rst.close();
				pst.close();

		//===start parvez date: 28-02-2023 note added: score_calculation_basis===
				
				/*pst = con.prepareStatement("insert into appraisal_question_details(question_id,attribute_id,appraisal_id,appraisal_level_id," +
					"answer_type,goal_kra_target_id,app_system_type,weightage,kra_id) values(?,?,?,?, ?,?,?,?, ?)");*/
				
				pst = con.prepareStatement("insert into appraisal_question_details(question_id,attribute_id,appraisal_id,appraisal_level_id," +
						"answer_type,goal_kra_target_id,app_system_type,weightage,kra_id,other_short_description, score_calculation_basis) values(?,?,?,?, ?,?,?,?, ?,?,?)");
		//===end parvez date: 28-02-2023===		
				pst.setInt(1, question_id);
				pst.setInt(2, uF.parseToInt(attribute));
				pst.setInt(3, uF.parseToInt(id));
				pst.setInt(4, appraisal_level_id);
				pst.setInt(5, uF.parseToInt(ansType));
				pst.setInt(6, uF.parseToInt(goalID));
				pst.setInt(7, uF.parseToInt(appraisalSystem));
				pst.setDouble(8, uF.parseToDouble(goalWeightage));
				pst.setInt(9, uF.parseToInt(goalId[i]));
				pst.setString(10, hmKRADescription.get(goalID));
		//===start parvez date: 28-02-2023===		
				pst.setBoolean(11, uF.parseToBoolean(chkCaculationBasis));
		//===end parvez date: 28-02-2023===		
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
	
	
	private void insertGoalTarget() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		con = db.makeConnection(con);
		
		Map<String, String> hmOrientMemberID = getOrientMemberID(con);

		String subsectionName = request.getParameter("subsectionname");
		String subsectionDescription = request.getParameter("subsectionDescription");
		String subsectionLongDescription = request.getParameter("subsectionLongDescription");
		String subSectionWeightage = request.getParameter("subSectionWeightage");
		
//		System.out.println("addOtherQuestions levelTitle : "+levelTitle);
//		System.out.println("addOtherQuestions shortDesrciption : "+shortDesrciption);
//		System.out.println("addOtherQuestions longDesrciption : "+longDesrciption);
		
		String attribute = request.getParameter("attribute");
		String appraisalSystem = request.getParameter("appraisalSystem");
		String scoreCard = request.getParameter("scoreCard");

		String[] goalId = request.getParameterValues("goalId");
		
		Map<String, String> orientationMemberMp = getOrientationMember(con);
		try {
			
			pst = con.prepareStatement("insert into appraisal_level_details(level_title,short_description,long_description,appraisal_system," +
				"scorecard_type,appraisal_id,main_level_id,attribute_id,subsection_weightage,added_by,entry_date) values(?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setString(1, subsectionName);
			pst.setString(2, subsectionDescription);
			pst.setString(3, subsectionLongDescription);
			pst.setInt(4, uF.parseToInt(appraisalSystem));
			pst.setInt(5, uF.parseToInt(scoreCard));
			pst.setInt(6, uF.parseToInt(id));
			pst.setInt(7, uF.parseToInt(getMain_level_id()));
			pst.setInt(8, uF.parseToInt(attribute));
			pst.setString(9, subSectionWeightage);
			pst.setInt(10, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(11, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.execute();
			pst.close();
			
			int appraisal_level_id = 0;
			pst = con.prepareStatement("select max(appraisal_level_id) from appraisal_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rst = pst.executeQuery();
			while (rst.next()) {
				appraisal_level_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			Map<String, String> hmGoalName = new HashMap<String, String>();
			String goalTypes =  INDIVIDUAL_GOAL+","+INDIVIDUAL_TARGET+","+PERSONAL_GOAL;
			pst = con.prepareStatement("select * from goal_details where goal_type in ("+goalTypes+")");
			rst = pst.executeQuery();
			while (rst.next()) {
				hmGoalName.put(rst.getString("goal_id"), rst.getString("goal_title"));
			
			//===start parvez date: 17-03-2022===	
				hmGoalName.put(rst.getString("goal_id")+"_DESCRIPTION", rst.getString("goal_description"));
			//===end parvez date: 17-03-2022===
				
			}
			rst.close();
			pst.close();
			
			for (int i = 0; goalId != null && i < goalId.length; i++) {
				String ansType = request.getParameter("ansType");
				String chkCaculationBasis = request.getParameter("chkCaculationBasis"); //added by parvez date: 28-02-2023
				/*int question_id = uF.parseToInt(hidequeid[i]);*/
				int question_id = 0;
				
				String optiona = request.getParameter("optiona"+ goalId[i]);
				String optionb = request.getParameter("optionb"+ goalId[i]);
				String optionc = request.getParameter("optionc"+ goalId[i]);
				String optiond = request.getParameter("optiond"+ goalId[i]);
				String optione = request.getParameter("optione"+ goalId[i]);
				String rateoptiona = request.getParameter("rateoptiona"+ goalId[i]);
				String rateoptionb = request.getParameter("rateoptionb"+ goalId[i]);
				String rateoptionc = request.getParameter("rateoptionc"+ goalId[i]);
				String rateoptiond = request.getParameter("rateoptiond"+ goalId[i]);
				String rateoptione = request.getParameter("rateoptione"+ goalId[i]);
				String goalWeightage = request.getParameter("goalWeightage"+ goalId[i]);
//				System.out.println("GOal ID ===> "+goalId[i]+ "  optiona === "+optiona + "  optiona=b === "+optionb + "  optionc === "+optionc + "  optiond === "+optiond);
				
					String[] correct = request.getParameterValues("correct"+ goalId[i]);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}

					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
						"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans," +
						"is_add,question_type,goal_kra_target_id,app_system_type)values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
					pst.setString(1, hmGoalName.get(goalId[i]));
					pst.setString(2, uF.showData(optiona, ""));
					pst.setString(3, uF.showData(optionb, ""));
					pst.setString(4, uF.showData(optionc, ""));
					pst.setString(5, uF.showData(optiond, ""));
					pst.setString(6, uF.showData(optione, ""));
					pst.setInt(7, uF.parseToInt(rateoptiona));
					pst.setInt(8, uF.parseToInt(rateoptionb));
					pst.setInt(9, uF.parseToInt(rateoptionc));
					pst.setInt(10, uF.parseToInt(rateoptiond));
					pst.setInt(11, uF.parseToInt(rateoptione));
					pst.setString(12, option.toString());
					pst.setBoolean(13, false);
					pst.setInt(14, uF.parseToInt(ansType));
					pst.setInt(15, uF.parseToInt(goalId[i]));
					pst.setInt(16, uF.parseToInt(appraisalSystem));
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();

			//===start parvez date: 28-02-2023===
					
				/*pst = con.prepareStatement("insert into appraisal_question_details(question_id,attribute_id,appraisal_id,appraisal_level_id," +
					"answer_type,goal_kra_target_id,app_system_type,weightage) values(?,?,?,?, ?,?,?,?)");*/
					
				pst = con.prepareStatement("insert into appraisal_question_details(question_id,attribute_id,appraisal_id,appraisal_level_id," +
					"answer_type,goal_kra_target_id,app_system_type,weightage,other_short_description, score_calculation_basis) values(?,?,?,?, ?,?,?,?, ?,?)");	
			//===end parvez date: 28-02-2023===	
				
				pst.setInt(1, question_id);
				pst.setInt(2, uF.parseToInt(attribute));
				pst.setInt(3, uF.parseToInt(id));
				pst.setInt(4, appraisal_level_id);
				pst.setInt(5, uF.parseToInt(ansType));
				pst.setInt(6, uF.parseToInt(goalId[i]));
				pst.setInt(7, uF.parseToInt(appraisalSystem));
				pst.setDouble(8, uF.parseToDouble(goalWeightage));
				pst.setString(9, hmGoalName.get(goalId[i]+"_DESCRIPTION"));
		//===start parvez date: 28-02-2023===
				pst.setBoolean(10, uF.parseToBoolean(chkCaculationBasis));
		//===start parvez date: 28-02-2023===		
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

	public void initialize(UtilityFunctions uF ) {
		
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
		userlocation = getManagerLocation();
		if (empGrade != null) {
			empList = new FillEmployee(request).fillEmployeeName(uF.parseToInt(empGrade));
		} else if (strUserType != null && strUserType.equals(MANAGER)) {
			empList = new FillEmployee(request).fillEmployeeNameBySupervisor(strSessionEmpId);
		} else {
			empList = new FillEmployee(request).fillEmployeeNameByLocation(userlocation);
		}
		
		finalizationList = new FillEmployee(request).fillFinalizationNameByLocation(userlocation);
		reviewerList  = new FillEmployee(request).fillReviewerNameByLocation(null);
		String levelID=getSelfIDs(getId());
//		System.out.println("create levelID :: "+levelID);
		if (levelID != null && levelID.length()>0) {
//			System.out.println("levelID in :: "+levelID);
			attributeList = new FillAttribute(request).fillElementAttribute(levelID);
		} else {				
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
			StringBuilder sb=new StringBuilder();
			con = db.makeConnection(con);
			pst = con.prepareStatement("select member_name from orientation_details od,orientation_member om  where orientation_id=? and od.member_id=orientation_member_id");
			pst.setInt(1,id);
			rs=pst.executeQuery();
			int i=0;
			while(rs.next()) {
				if(i==0) {
					sb.append(rs.getString("member_name"));
				} else {
					sb.append(","+rs.getString("member_name"));
				}
				i++;
			}
			rs.close();
			pst.close();
	//		System.out.println("orientation members==>"+ sb.toString());
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

	public void getLevelDetails() {
//		System.out.println("APP ID ===="+ id);
		AppraisalSummary report = new AppraisalSummary();
		report.setServletRequest(request);
		report.setId(id);
		report.execute();
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
					sb.append("<option value=\"" + rs.getString("appraisal_answer_type_id") + "\" selected>" + rs.getString("appraisal_answer_type_name") + "</option>");
				} else {
					sb.append("<option value=\"" + rs.getString("appraisal_answer_type_id") + "\">" + rs.getString("appraisal_answer_type_name") + "</option>");
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

	private void addOtherQuestions() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		con = db.makeConnection(con);

		String subsectionName = request.getParameter("subsectionname");
		String subsectionDescription = request.getParameter("subsectionDescription");
		String subsectionLongDescription = request.getParameter("subsectionLongDescription");
		String subSectionWeightage = request.getParameter("subSectionWeightage");
		
		String attribute = request.getParameter("attribute");
		String appraisalSystem = request.getParameter("appraisalSystem");
		String scoreCard = request.getParameter("scoreCard");

		String otherQuestionType = request.getParameter("otherQuestionType");
		String checkWeightage = request.getParameter("checkWeightage");

		String[] otherSDescription = request.getParameterValues("otherSDescription");
		String[] orientt = request.getParameterValues("orientt");

		//String[] questionSelect = request.getParameterValues("questionSelect");
		//String[] hidequeid = request.getParameterValues("hidequeid");
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
		
		Map<String,String> orientationMemberMp=getOrientationMember(con);
		Map<String, String> hmOrientMemberID = getOrientMemberID(con);
		try {
						
			pst = con.prepareStatement("insert into appraisal_level_details(level_title,short_description,long_description,appraisal_system," +
				"scorecard_type,appraisal_id,main_level_id,attribute_id,subsection_weightage,added_by,entry_date) values(?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setString(1, uF.showData(subsectionName, ""));
			pst.setString(2, uF.showData(subsectionDescription,""));
			pst.setString(3, uF.showData(subsectionLongDescription, ""));
			pst.setInt(4, uF.parseToInt(appraisalSystem));
			pst.setInt(5, uF.parseToInt(scoreCard));
			pst.setInt(6, uF.parseToInt(id));
			pst.setInt(7, uF.parseToInt(getMain_level_id()));
			pst.setInt(8, uF.parseToInt(attribute));
			pst.setString(9, uF.showData(subSectionWeightage,"100"));
			pst.setInt(10, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(11, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.execute();
			pst.close();
			
			int appraisal_level_id = 0;
			pst = con.prepareStatement("select max(appraisal_level_id) from appraisal_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rst = pst.executeQuery();
			while (rst.next()) {
				appraisal_level_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("insert into appraisal_other_question_type_details(other_question_type,is_weightage,appraisal_id,level_id)values(?,?,?,?)");
			pst.setString(1, otherQuestionType);
			pst.setBoolean(2, uF.parseToBoolean(checkWeightage));
			pst.setInt(3, uF.parseToInt(id));
			pst.setInt(4, appraisal_level_id);
			pst.execute();
			pst.close();
			
			int other_question_type_id = 0;
			pst = con.prepareStatement("select max(othe_question_type_id) from appraisal_other_question_type_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				other_question_type_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int questionLength = 0;
			if(question != null ) {
				questionLength = question.length;
			}
			
			for (int i = 0; i < questionLength; i++) {
				String ansType = request.getParameter("ansType");
				String chkCaculationBasis = request.getParameter("chkCaculationBasis"); //added by parvez date: 28-02-2023
//				int question_id = uF.parseToInt(hidequeid[i]);
				int question_id = 0;
//				if (uF.parseToInt(hidequeid[i]) == 0) {

					String[] correct = request.getParameterValues("correct" + orientt[i]);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}

					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
						"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setString(1, question[i]);
					pst.setString(2, (optiona != null && optiona.length > i ? optiona[i]: ""));
					pst.setString(3, (optionb != null && optionb.length > i ? optionb[i]: ""));
					pst.setString(4, (optionc != null && optionc.length > i ? optionc[i]: ""));
					pst.setString(5, (optiond != null && optiond.length > i ? optiond[i]: ""));
					pst.setString(6, (optione != null && optione.length > i ? optione[i]: ""));
					pst.setInt(7, (rateoptiona != null && rateoptiona.length > i ? uF.parseToInt(rateoptiona[i]): 0));
					pst.setInt(8, (rateoptionb != null && rateoptionb.length > i ? uF.parseToInt(rateoptionb[i]): 0));
					pst.setInt(9, (rateoptionc != null && rateoptionc.length > i ? uF.parseToInt(rateoptionc[i]): 0));
					pst.setInt(10, (rateoptiond != null && rateoptiond.length > i ? uF.parseToInt(rateoptiond[i]): 0));
					pst.setInt(11, (rateoptione != null && rateoptione.length > i ? uF.parseToInt(rateoptione[i]): 0));
					pst.setString(12, option.toString());
					pst.setBoolean(13, uF.parseToBoolean(addFlag[i]));
					pst.setInt(14, uF.parseToInt(ansType));
					pst.executeUpdate();
					pst.close();
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
//				}
				
			//===start parvez date: 28-02-2023===		
				/*pst = con.prepareStatement("insert into appraisal_question_details(question_id,other_id,attribute_id,weightage,appraisal_id," +
					"other_short_description,appraisal_level_id,answer_type) values(?,?,?,?, ?,?,?,?)");*/
				pst = con.prepareStatement("insert into appraisal_question_details(question_id,other_id,attribute_id,weightage,appraisal_id," +
					"other_short_description,appraisal_level_id,answer_type,score_calculation_basis) values(?,?,?,?, ?,?,?,?, ?)");
			//===end parvez date: 28-02-2023===	
				pst.setInt(1, question_id);
				pst.setInt(2, other_question_type_id);
				pst.setInt(3, uF.parseToInt(attribute));
				pst.setDouble(4, uF.parseToDouble(weightage[i]));
				pst.setInt(5, uF.parseToInt(id));
				pst.setString(6, (otherSDescription!= null && otherSDescription.length > i) ? otherSDescription[i] : "");
				pst.setInt(7, appraisal_level_id);
				pst.setInt(8, uF.parseToInt(ansType));
			//===start parvez date: 28-02-2023===
				pst.setBoolean(9, uF.parseToBoolean(chkCaculationBasis));
			//===end parvez date: 28-02-2023===	
				System.out.println("CA/1422--addOtherQuestions--pst==>"+pst);
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

	
	public void selectFunction() {
		UtilityFunctions uF = new UtilityFunctions();

		int scoreCard = uF.parseToInt((String) request.getParameter("scoreCard"));
		System.out.println("CAp/1398--scoreCard="+scoreCard);
		if (scoreCard == 1) {
			insertDatawithGoalObjective();
		} else if (scoreCard == 2) {
			insertData();
		} else if (scoreCard == 3) {
			insertDatawithGoal();
		}

	}

	public void insertDatawithGoal() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		String[] scoreSectionName = request.getParameterValues("scoreSectionName");
		String[] scoreCardDescription = request.getParameterValues("scoreCardDescription");
		String[] scoreCardWeightage = request.getParameterValues("scoreCardWeightage");

		String[] goalSectionName = request.getParameterValues("goalSectionName");
		String[] goalDescription = request.getParameterValues("goalDescription");
		String[] goalWeightage = request.getParameterValues("goalWeightage");

		String[] measuresSectionName = request.getParameterValues("measuresSectionName");
		String[] measuresDescription = request.getParameterValues("measuresDescription");
		String[] measureWeightage=request.getParameterValues("measureWeightage");

		//String[] hidequeid = request.getParameterValues("hidequeid");
		String[] weightage = request.getParameterValues("weightage");
		String[] measurecount = request.getParameterValues("measurecount");
		String[] questioncount = request.getParameterValues("questioncount");
		String[] goalcount = request.getParameterValues("goalcount");
		String[] question = request.getParameterValues("question");

		String subsectionname = request.getParameter("subsectionname");
		String subsectionDescription = request.getParameter("subsectionDescription");
		String subsectionLongDescription = request.getParameter("subsectionLongDescription");
		String subSectionWeightage = request.getParameter("subSectionWeightage");
		
		//String longDesrciption = request.getParameter("longDesrciption");
		String attribute = request.getParameter("attribute");
		String appraisalSystem = request.getParameter("appraisalSystem");
		String scoreCard = request.getParameter("scoreCard");

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
		String[] orientt=request.getParameterValues("orientt");
		con = db.makeConnection(con);
		Map<String,String> orientationMemberMp=getOrientationMember(con);
		Map<String, String> hmOrientMemberID = getOrientMemberID(con);
		
		try {
			pst = con.prepareStatement("insert into appraisal_level_details(level_title,short_description,long_description,appraisal_system," +
				"scorecard_type,appraisal_id,main_level_id,attribute_id,subsection_weightage,added_by,entry_date) values(?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setString(1, uF.showData(subsectionname,""));
			pst.setString(2, uF.showData(subsectionDescription,""));
			pst.setString(3, uF.showData(subsectionLongDescription, ""));
			pst.setString(3, "");
			pst.setInt(4, uF.parseToInt(appraisalSystem));
			pst.setInt(5, uF.parseToInt(scoreCard));
			pst.setInt(6, uF.parseToInt(id));
			pst.setInt(7, uF.parseToInt(getMain_level_id()));
			pst.setInt(8, uF.parseToInt(attribute));
			pst.setString(9, uF.showData(subSectionWeightage,"100"));
			pst.setInt(10, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(11, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.execute();
			pst.close();
			
			int appraisal_level_id = 0;
			pst = con.prepareStatement("select max(appraisal_level_id) from appraisal_level_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				appraisal_level_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			int questionserial = 0;
			int measureserial = 0;
			int goalserial = 0;
			for (int i = 0; i < scoreSectionName.length; i++) {
				pst = con.prepareStatement("insert into appraisal_scorecard_details(scorecard_section_name,scorecard_description,"
					+ "scorecard_weightage,level_id,appraisal_id,appraisal_attribute)values(?,?,?,?,?,?)");
				pst.setString(1, scoreSectionName[i]);
				pst.setString(2, scoreCardDescription[i]);
				pst.setString(3, scoreCardWeightage[i]);
				pst.setInt(4, appraisal_level_id);
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, uF.parseToInt(attribute));
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

				int goal = uF.parseToInt(goalcount[i]);
				for (int j = 0; j < goal; j++) {

					pst = con.prepareStatement("insert into appraisal_goal_details(goal_section_name,goal_description,"
									+ "goal_weightage,scorecard_id,appraisal_id)values(?,?,?,?,?)");
					pst.setString(1, goalSectionName[goalserial]);
					pst.setString(2, goalDescription[goalserial]);
					pst.setString(3, goalWeightage[goalserial]);
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

					int measure = uF.parseToInt(measurecount[goalserial]);
					goalserial++;
					for (int k = 0; k < measure; k++) {
						pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,goal_id,appraisal_id,weightage)"
										+ "values(?,?,?,?,?)");
						pst.setString(1, measuresSectionName[measureserial]);
						pst.setString(2, measuresDescription[measureserial]);
						pst.setInt(3, goal_id);
						pst.setInt(4, uF.parseToInt(id));
						pst.setString(5,measureWeightage[measureserial]);
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

						int questioncnt = uF.parseToInt(questioncount[measureserial]);
						measureserial++;
						for (int l = 0; l < questioncnt; l++) {
							String ansType = request.getParameter("ansType");
							String chkCaculationBasis = request.getParameter("chkCaculationBasis");		//added by parvez date: 28-02-2023
//							int question_id = uF.parseToInt(hidequeid[questionserial]);
							int question_id = 0;
							
							if (question[questionserial].length() > 0) {
//								if (uF.parseToInt(hidequeid[questionserial]) == 0) {
									String[] correct = request.getParameterValues("correct"+ orientt[questionserial]);
									StringBuilder option = new StringBuilder();
									for (int ab = 0; correct != null && ab < correct.length; ab++) {
										option.append(correct[ab] + ",");
									}

									pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
										"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
										"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
									pst.setString(1, question[questionserial]);
									pst.setString(2, (optiona != null && optiona.length > questionserial ? optiona[questionserial]: ""));
									pst.setString(3, (optionb != null && optionb.length > questionserial ? optionb[questionserial]: ""));
									pst.setString(4, (optionc != null && optionc.length > questionserial ? optionc[questionserial]: ""));
									pst.setString(5, (optiond != null && optiond.length > questionserial ? optiond[questionserial]: ""));
									pst.setString(6, (optione != null && optione.length > questionserial ? optione[questionserial]: ""));
									pst.setInt(7, (rateoptiona != null && rateoptiona.length > questionserial ? uF.parseToInt(rateoptiona[questionserial]): 0));
									pst.setInt(8, (rateoptionb != null && rateoptionb.length > questionserial ? uF.parseToInt(rateoptionb[questionserial]): 0));
									pst.setInt(9, (rateoptionc != null && rateoptionc.length > questionserial ? uF.parseToInt(rateoptionc[questionserial]): 0));
									pst.setInt(10, (rateoptiond != null && rateoptiond.length > questionserial ? uF.parseToInt(rateoptiond[questionserial]): 0));
									pst.setInt(11, (rateoptione != null && rateoptione.length > questionserial ? uF.parseToInt(rateoptione[questionserial]): 0));
									pst.setString(12, option.toString());
									pst.setBoolean(13, uF.parseToBoolean(addFlag[i]));
									pst.setInt(14, uF.parseToInt(ansType));
									pst.executeUpdate();
									pst.close();
										
										
									/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
									pst.setString(1, question[questionserial]);
									pst.setString(2, (optiona != null && optiona.length > questionserial ? optiona[questionserial]: ""));
									pst.setString(3, (optionb != null && optionb.length > questionserial ? optionb[questionserial]: ""));
									pst.setString(4, (optionc != null && optionc.length > questionserial ? optionc[questionserial]: ""));
									pst.setString(5, (optiond != null && optiond.length > questionserial ? optiond[questionserial]: ""));
									pst.setString(6, option.toString());
									pst.setBoolean(7,uF.parseToBoolean(addFlag[i]));
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
//								}

							//===start parvez date: 28-02-2023===		
//								pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage," +
//									"appraisal_id,appraisal_level_id,scorecard_id,answer_type) values(?,?,?,?, ?,?,?,?)");
								pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage," +
									"appraisal_id,appraisal_level_id,scorecard_id,answer_type,score_calculation_basis) values(?,?,?,?, ?,?,?,?, ?)");
							//===end parvez date: 28-02-2023===	
								pst.setInt(1, question_id);
								pst.setInt(2, measure_id);
								pst.setInt(3, uF.parseToInt(attribute));
								pst.setDouble(4, uF.parseToDouble(weightage[questionserial]));
								pst.setInt(5, uF.parseToInt(id));
								pst.setInt(6, appraisal_level_id);
								pst.setInt(7, scorecard_id);
								pst.setInt(8, uF.parseToInt(ansType));
							//===start parvez date: 28-02-2023===
								pst.setBoolean(9, uF.parseToBoolean(chkCaculationBasis));
							//===end parvez date: 28-02-2023===	
								pst.execute();
								pst.close();
							}
							questionserial++;
						}
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	private Map<String,String> getOrientationMember(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> orientationMemberMp=new HashMap<String,String>();
	
		try {
			
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs=pst.executeQuery();
			while(rs.next()) {
				orientationMemberMp.put(rs.getString("orientation_member_id"),rs.getString("member_name"));
			}
			rs.close();
			pst.close();
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMemberMp;
	}
	

	public void insertDatawithGoalObjective() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		String[] scoreSectionName = request.getParameterValues("scoreSectionName");
		String[] scoreCardDescription = request.getParameterValues("scoreCardDescription");
		String[] scoreCardWeightage = request.getParameterValues("scoreCardWeightage");

		String[] goalSectionName = request.getParameterValues("goalSectionName");
		String[] goalDescription = request.getParameterValues("goalDescription");
		String[] goalWeightage = request.getParameterValues("goalWeightage");

		String[] objectiveSectionName = request.getParameterValues("objectiveSectionName");
		String[] objectiveDescription = request.getParameterValues("objectiveDescription");
		String[] objectiveWeightage = request.getParameterValues("objectiveWeightage");

		String[] measuresSectionName = request.getParameterValues("measuresSectionName");
		String[] measuresDescription = request.getParameterValues("measuresDescription");
		String[] measureWeightage=request.getParameterValues("measureWeightage");

		//String[] hidequeid = request.getParameterValues("hidequeid");
		String[] weightage = request.getParameterValues("weightage");
		String[] measurecount = request.getParameterValues("measurecount");
		String[] questioncount = request.getParameterValues("questioncount");
		String[] goalcount = request.getParameterValues("goalcount");
		String[] objectivecount = request.getParameterValues("objectivecount");
		String[] question = request.getParameterValues("question");

		String subsectionname = request.getParameter("subsectionname");
		String subsectionDescription = request.getParameter("subsectionDescription");
		String subsectionLongDescription = request.getParameter("subsectionLongDescription");
		String subSectionWeightage = request.getParameter("subSectionWeightage");
		
		String attribute = request.getParameter("attribute");
		String appraisalSystem = request.getParameter("appraisalSystem");
		String scoreCard = request.getParameter("scoreCard");

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
		
		
		con = db.makeConnection(con);
		Map<String,String> orientationMemberMp=getOrientationMember(con);
		Map<String, String> hmOrientMemberID = getOrientMemberID(con);
		
		try {
			
			pst = con.prepareStatement("insert into appraisal_level_details(level_title,short_description,long_description,appraisal_system," +
				"scorecard_type,appraisal_id,main_level_id,attribute_id,subsection_weightage,added_by,entry_date) values(?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setString(1, uF.showData(subsectionname,""));
			pst.setString(2, uF.showData(subsectionDescription,""));
			pst.setString(3, uF.showData(subsectionLongDescription, ""));
			pst.setString(3, "");
			pst.setInt(4, uF.parseToInt(appraisalSystem));
			pst.setInt(5, uF.parseToInt(scoreCard));
			pst.setInt(6, uF.parseToInt(id));
			pst.setInt(7, uF.parseToInt(getMain_level_id()));
			pst.setInt(8, uF.parseToInt(attribute));
			pst.setString(9, uF.showData(subSectionWeightage, "100"));
			pst.setInt(10, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(11, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.execute();
			pst.close();
			
			
			int appraisal_level_id = 0;
			pst = con.prepareStatement("select max(appraisal_level_id) from appraisal_level_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				appraisal_level_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int questionserial = 0;
			int measureserial = 0;
			int goalserial = 0;
			int objectiveserial = 0;
			for (int i = 0; i < scoreSectionName.length; i++) {
				pst = con.prepareStatement("insert into appraisal_scorecard_details(scorecard_section_name,scorecard_description,"
					+ "scorecard_weightage,level_id,appraisal_id,appraisal_attribute)values(?,?,?,?, ?,?)");
				pst.setString(1, scoreSectionName[i]);
				pst.setString(2, scoreCardDescription[i]);
				pst.setString(3, scoreCardWeightage[i]);
				pst.setInt(4, appraisal_level_id);
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, uF.parseToInt(attribute));
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

				int goal = uF.parseToInt(goalcount[i]);
				for (int j = 0; j < goal; j++) {
					pst = con.prepareStatement("insert into appraisal_goal_details(goal_section_name,goal_description,"
						+ "goal_weightage,scorecard_id,appraisal_id)values(?,?,?,?, ?)");
					pst.setString(1, goalSectionName[goalserial]);
					pst.setString(2, goalDescription[goalserial]);
					pst.setString(3, goalWeightage[goalserial]);
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

					int objective = uF.parseToInt(objectivecount[goalserial]);
					goalserial++;
					for (int m = 0; m < objective; m++) {
						pst = con.prepareStatement("insert into appraisal_objective_details(objective_section_name,objective_description,"
							+ "objective_weightage,goal_id,appraisal_id)values(?,?,?,?,?)");
						pst.setString(1, objectiveSectionName[objectiveserial]);
						pst.setString(2, objectiveDescription[objectiveserial]);
						pst.setString(3, objectiveWeightage[objectiveserial]);
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

						int measure = uF.parseToInt(measurecount[objectiveserial]);
						objectiveserial++;
						for (int k = 0; k < measure; k++) {
							pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,objective_id,appraisal_id,weightage)"
											+ "values(?,?,?,?,?)");
							pst.setString(1, measuresSectionName[measureserial]);
							pst.setString(2, measuresDescription[measureserial]);
							pst.setInt(3, objective_id);
							pst.setInt(4, uF.parseToInt(id));
							pst.setString(5,measureWeightage[measureserial]);
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
							
							int questioncnt = uF.parseToInt(questioncount[measureserial]);
							measureserial++;
							for (int l = 0; l < questioncnt; l++) {
								String ansType = request.getParameter("ansType");
								String chkCaculationBasis = request.getParameter("chkCaculationBasis");		//added by parvez date: 28-02-2023
//								int question_id = uF.parseToInt(hidequeid[questionserial]);
								int question_id = 0;
								if (question[questionserial] != null && question[questionserial].length() > 0) {
//									if (uF.parseToInt(hidequeid[questionserial]) == 0) {
										String[] correct = request.getParameterValues("correct" + orientt[questionserial]);
										StringBuilder option = new StringBuilder();

										for (int ab = 0; correct != null && ab < correct.length; ab++) {
											option.append(correct[ab] + ",");
										}

										pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
											"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
											"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
										pst.setString(1, question[questionserial]);
										pst.setString(2, (optiona != null && optiona.length > questionserial ? optiona[questionserial]: ""));
										pst.setString(3, (optionb != null && optionb.length > questionserial ? optionb[questionserial]: ""));
										pst.setString(4, (optionc != null && optionc.length > questionserial ? optionc[questionserial]: ""));
										pst.setString(5, (optiond != null && optiond.length > questionserial ? optiond[questionserial]: ""));
										pst.setString(6, (optione != null && optione.length > questionserial ? optione[questionserial]: ""));
										pst.setInt(7, (rateoptiona != null && rateoptiona.length > questionserial ? uF.parseToInt(rateoptiona[questionserial]): 0));
										pst.setInt(8, (rateoptionb != null && rateoptionb.length > questionserial ? uF.parseToInt(rateoptionb[questionserial]): 0));
										pst.setInt(9, (rateoptionc != null && rateoptionc.length > questionserial ? uF.parseToInt(rateoptionc[questionserial]): 0));
										pst.setInt(10, (rateoptiond != null && rateoptiond.length > questionserial ? uF.parseToInt(rateoptiond[questionserial]): 0));
										pst.setInt(11, (rateoptione != null && rateoptione.length > questionserial ? uF.parseToInt(rateoptione[questionserial]): 0));
										pst.setString(12, option.toString());
										pst.setBoolean(13, uF.parseToBoolean(addFlag[i]));
										pst.setInt(14, uF.parseToInt(ansType));
										pst.executeUpdate();
										pst.close();
										
										/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
										pst.setString(1,question[questionserial]);
										pst.setString(2, (optiona != null && optiona.length > questionserial ? optiona[questionserial]: ""));
										pst.setString(3, (optionb != null && optionb.length > questionserial ? optionb[questionserial]: ""));
										pst.setString(4, (optionc != null && optionc.length > questionserial ? optionc[questionserial]: ""));
										pst.setString(5, (optiond != null && optiond.length > questionserial ? optiond[questionserial]: ""));
										pst.setString(6, option.toString());
										pst.setBoolean(7,uF.parseToBoolean(addFlag[i]));
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
//									}

								//===start parvez date: 28-02-2023===		
//									pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage," +
//										"appraisal_id,appraisal_level_id,scorecard_id,answer_type) values(?,?,?,?, ?,?,?,?)");
									
									pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage," +
										"appraisal_id,appraisal_level_id,scorecard_id,answer_type,score_calculation_basis) values(?,?,?,?, ?,?,?,?, ?)");
								//===end parvez date: 28-02-2023===	
									pst.setInt(1, question_id);
									pst.setInt(2, measure_id);
									pst.setInt(3, uF.parseToInt(attribute));
									pst.setDouble(4, uF.parseToDouble(weightage[questionserial]));
									pst.setInt(5, uF.parseToInt(id));
									pst.setInt(6, appraisal_level_id);
									pst.setInt(7, scorecard_id);
									pst.setInt(8, uF.parseToInt(ansType));
								//===start parvez date: 28-02-2023===	
									pst.setBoolean(9, uF.parseToBoolean(chkCaculationBasis));
								//===end parvez date: 28-02-2023===	
									pst.execute();
									pst.close();
								}
								questionserial++;
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public void insertData() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		String subsectionname = request.getParameter("subsectionname");
		String subsectionDescription = request.getParameter("subsectionDescription");
		String subsectionLongDescription = request.getParameter("subsectionLongDescription");
		String subSectionWeightage = request.getParameter("subSectionWeightage");
		
		String attribute = request.getParameter("attribute");
		String appraisalSystem = request.getParameter("appraisalSystem");
		String scoreCard = request.getParameter("scoreCard");

		String[] scoreSectionName = request.getParameterValues("scoreSectionName");
		String[] scoreCardDescription = request.getParameterValues("scoreCardDescription");
		String[] scoreCardWeightage = request.getParameterValues("scoreCardWeightage");

		String[] measuresSectionName = request.getParameterValues("measuresSectionName");
		String[] measuresDescription = request.getParameterValues("measuresDescription");
		String[] measureWeightage=request.getParameterValues("measureWeightage");

		//String[] hidequeid = request.getParameterValues("hidequeid");
		String[] weightage = request.getParameterValues("weightage");
		String[] measurecount = request.getParameterValues("measurecount");
		String[] questioncount = request.getParameterValues("questioncount");
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
		
		con = db.makeConnection(con);
		Map<String,String> orientationMemberMp=getOrientationMember(con);
		Map<String, String> hmOrientMemberID = getOrientMemberID(con);
		
		try {
			
			pst = con.prepareStatement("insert into appraisal_level_details(level_title,short_description,long_description,appraisal_system," +
				"scorecard_type,appraisal_id,main_level_id,attribute_id,subsection_weightage,added_by,entry_date) values(?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setString(1, uF.showData(subsectionname,""));
			pst.setString(2, uF.showData(subsectionDescription,""));
			pst.setString(3, uF.showData(subsectionLongDescription, ""));
//			pst.setString(3, "");
			pst.setInt(4, uF.parseToInt(appraisalSystem));
			pst.setInt(5, uF.parseToInt(scoreCard));
			pst.setInt(6, uF.parseToInt(id));
			pst.setInt(7, uF.parseToInt(getMain_level_id()));
			pst.setInt(8, uF.parseToInt(attribute));
			pst.setString(9, uF.showData(subSectionWeightage,"100"));
			pst.setInt(10, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(11, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
//			System.out.println("insert appraisal pst==>"+pst);
			pst.execute();
			pst.close();
			
			int appraisal_level_id = 0;
			pst = con.prepareStatement("select max(appraisal_level_id) from appraisal_level_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				appraisal_level_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			int questionserial = 0;
			int measureserial = 0;

			for (int i = 0; i < scoreSectionName.length; i++) {

				pst = con.prepareStatement("insert into appraisal_scorecard_details(scorecard_section_name,scorecard_description,"
								+ "scorecard_weightage,level_id,appraisal_id,appraisal_attribute)values(?,?,?,?,?,?)");
				pst.setString(1, scoreSectionName[i]);
				pst.setString(2, scoreCardDescription[i]);
				pst.setString(3, scoreCardWeightage[i]);
				pst.setInt(4, appraisal_level_id);
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, uF.parseToInt(attribute));
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

				int measure = uF.parseToInt(measurecount[i]);
				for (int k = 0; k < measure; k++) {
					pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,scorecard_id,appraisal_id,weightage)"
									+ "values(?,?,?,?,?)");
					pst.setString(1, measuresSectionName[measureserial]);
					pst.setString(2, measuresDescription[measureserial]);
					pst.setInt(3, scorecard_id);
					pst.setInt(4, uF.parseToInt(id));
					pst.setString(5,measureWeightage[measureserial]);
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

					int questioncnt = uF.parseToInt(questioncount[measureserial]);
					measureserial++;
					for (int l = 0; l < questioncnt; l++) {
//						int question_id = uF.parseToInt(hidequeid[questionserial]);
						int question_id = 0;
						String ansType = request.getParameter("ansType");
						String chkCaculationBasis = request.getParameter("chkCaculationBasis");		//added by parvez date: 28-02-2023===
						
						if (question[questionserial].length() > 0) {
//							if (uF.parseToInt(hidequeid[questionserial]) == 0) {
								String[] correct = request.getParameterValues("correct"+ orientt[questionserial]);
								StringBuilder option = new StringBuilder();

								for (int ab = 0; correct != null && ab < correct.length; ab++) {
									option.append(correct[ab] + ",");
								}

								pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
									"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
									"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
								pst.setString(1, question[questionserial]);
								pst.setString(2, (optiona != null && optiona.length > questionserial ? optiona[questionserial]: ""));
								pst.setString(3, (optionb != null && optionb.length > questionserial ? optionb[questionserial]: ""));
								pst.setString(4, (optionc != null && optionc.length > questionserial ? optionc[questionserial]: ""));
								pst.setString(5, (optiond != null && optiond.length > questionserial ? optiond[questionserial]: ""));
								pst.setString(6, (optione != null && optione.length > questionserial ? optione[questionserial]: ""));
								pst.setInt(7, (rateoptiona != null && rateoptiona.length > questionserial ? uF.parseToInt(rateoptiona[questionserial]): 0));
								pst.setInt(8, (rateoptionb != null && rateoptionb.length > questionserial ? uF.parseToInt(rateoptionb[questionserial]): 0));
								pst.setInt(9, (rateoptionc != null && rateoptionc.length > questionserial ? uF.parseToInt(rateoptionc[questionserial]): 0));
								pst.setInt(10, (rateoptiond != null && rateoptiond.length > questionserial ? uF.parseToInt(rateoptiond[questionserial]): 0));
								pst.setInt(11, (rateoptione != null && rateoptione.length > questionserial ? uF.parseToInt(rateoptione[questionserial]): 0));
								pst.setString(12, option.toString());
								pst.setBoolean(13, uF.parseToBoolean(addFlag[i]));
								pst.setInt(14, uF.parseToInt(ansType));
//								System.out.println("CAp/2074--pst="+pst);
								pst.executeUpdate();
								pst.close();
								
								/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
								pst.setString(1, question[questionserial]);
								pst.setString(2, (optiona != null && optiona.length > questionserial ? optiona[questionserial]: ""));
								pst.setString(3, (optionb != null && optionb.length > questionserial ? optionb[questionserial]: ""));
								pst.setString(4, (optionc != null && optionc.length > questionserial ? optionc[questionserial]: ""));
								pst.setString(5, (optiond != null && optiond.length > questionserial ? optiond[questionserial]: ""));
								pst.setString(6, option.toString());
								pst.setBoolean(7, uF.parseToBoolean(addFlag[i]));
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
//							}

					//===start parvez date: 28-02-2023===			
//						pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage," +
//							"appraisal_id,appraisal_level_id,scorecard_id,answer_type) values(?,?,?,?, ?,?,?,?)");
						pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,weightage," +
								"appraisal_id,appraisal_level_id,scorecard_id,answer_type, score_calculation_basis) values(?,?,?,?, ?,?,?,?, ?)");		
					//===end parvez date: 28-02-2023===	
						pst.setInt(1, question_id);
						pst.setInt(2, measure_id);
						pst.setInt(3, uF.parseToInt(attribute));
						pst.setDouble(4, uF.parseToDouble(weightage[questionserial]));
						pst.setInt(5, uF.parseToInt(id));
						pst.setInt(6, appraisal_level_id);
						pst.setInt(7, scorecard_id);
						pst.setInt(8, uF.parseToInt(ansType));
					//===start parvez date: 28-02-2023===	
						pst.setBoolean(9, uF.parseToBoolean(chkCaculationBasis));
					//===end parvez date: 28-02-2023===	
						pst.executeUpdate();
						pst.close();
					}
						questionserial++;
					}
				}
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
//			System.out.println("self=====>"+self);
//			System.out.println("type=====>"+type);
			
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
					if(rs.getString("grade_id") != null && rs.getInt("grade_id") > 0) {
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
					if(rs.getString("wlocation_id") != null && rs.getInt("wlocation_id") > 0) {
						if(cnt==0) {
							sb5.append(","+rs.getString("wlocation_id").trim()+",");
						} else {
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
						} else {
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
				while(rs.next()) {
					if(rs.getString("wlocation_id") != null && rs.getInt("wlocation_id") > 0) {
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
				pst.setInt(1, 5);
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
			} else if (type == 6) {

			} else if (type == 7) {
				
				pst=con.prepareStatement("select wlocation_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and emp_id in("+self+") group by wlocation_id");
				rs=pst.executeQuery();
				StringBuilder sb5=new StringBuilder();
				int cnt=0;
				while(rs.next()) {
					if(rs.getString("wlocation_id") != null && rs.getInt("wlocation_id") > 0) {
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
				
				pst = con.prepareStatement("select hod_emp_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id=eod.emp_id "
						+"  and is_alive = true and hod_emp_id > 0  and eod.emp_id in("+self+")");
//				System.out.println("pst ===>> " + pst);
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
	

	private List<String> getReviewStartAndEndDates() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		List<String> alDates = new ArrayList<String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select from_date,to_date,eligibility_min_daysbefore_start_date,eligibility_min_daysbefore_end_date from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
//			System.out.println("pst ===>> " + pst);
			rst = pst.executeQuery();
			while (rst.next()) {
				alDates.add(rst.getString("from_date"));
				alDates.add(rst.getString("to_date"));
				alDates.add(rst.getString("eligibility_min_daysbefore_start_date"));
				alDates.add(rst.getString("eligibility_min_daysbefore_end_date"));
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

		return alDates;
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
			pst = con
					.prepareStatement("select e.wlocation_id from employee_official_details e where e.emp_id=?");
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

	/*private String getAppendData(Connection con, List<String> strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();
		Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
		Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);

		if (strID != null) {
			for (int i = 0; i < strID.size(); i++) {
				if (i == 0) {
					sb.append("[" + hmEmpCode.get(strID.get(i)) + "] " + mp.get(strID.get(i)) + "(" + hmDesignation.get(strID.get(i)) + ")");
				} else {
					sb.append(", [" + hmEmpCode.get(strID.get(i)) + "] " + mp.get(strID.get(i)) + "(" + hmDesignation.get(strID.get(i)) + ")");
				}
			}
		} else {
			return null;
		}

		return sb.toString();
	}*/

	public void addAppraisal(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		con = db.makeConnection(con);
//		Map<String, String> hmOrientMemberID = getOrientMemberID(con);
		try {
	
//			List<String> selfList = getSelfList();
//			List<String> finalizeList = getFinalizeIdsList(con);
			
//			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
//			appraiseeList = uF.showData(getAppendData(con, selfList, hmEmpName), "");
					
			//weekday annualDay annualMonth day monthday month
			String appraisal_day=null;
			String appraisal_month=null;
			String weeklyDay=null;
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
	
			pst = con.prepareStatement("insert into appraisal_details(appraisal_name,appraisal_type,added_by,entry_date,frequency,from_date," +
				"to_date,appraisal_day,appraisal_month,weekday,appraisal_description,is_publish,appraisal_instruction, eligibility_min_daysbefore_start_date" +
				", eligibility_min_daysbefore_end_date,is_anonymous_review) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
			pst.setString(1, getAppraiselName());
			pst.setString(2, getAppraisalType());
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(5, frequency);
			pst.setDate(6, uF.getDateFormat(getFrom(), DATE_FORMAT));
			pst.setDate(7, uF.getDateFormat(getTo(), DATE_FORMAT));
			pst.setString(8, appraisal_day);
			pst.setString(9, appraisal_month);
			pst.setString(10, weeklyDay);
			pst.setString(11, getAppraisal_description());
			pst.setBoolean(12,false);
			pst.setString(13, getAppraisal_instruction());
			pst.setDouble(14, uF.parseToDouble(getEligibilityMinDaysBeforeStartDate()));
			pst.setDouble(15, uF.parseToDouble(getEligibilityMinDaysBeforeEndDate()));
			pst.setBoolean(16, uF.parseToBoolean(getAnonymousReview()));
			pst.executeUpdate();
			pst.close();
			
			
			pst = con.prepareStatement("select max(appraisal_details_id) from appraisal_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				id = rs.getString(1);
			}
			rs.close();
			pst.close();
			
//			System.out.println("id ===>> " + id);
			if(getFrom() != null && !getFrom().equals("") && getTo()!= null && !getTo().equals("")) {
//				***************************** appraisal Frequency Start ************************************
				AppraisalScheduler scheduler = new AppraisalScheduler(request, session, CF, uF, strSessionEmpId);
				scheduler.updateAppraisalDetails(id);
//				***************************** appraisal Frequency End ************************************
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void addTeamMember(UtilityFunctions uF) {
//		System.out.println("ID ====> "+id);
//		System.out.println("getId() ====> "+getId());
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
			List<String> selfList = getSelfList();
			List<String> finalizeList = getFinalizeIdsList(con);
//			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
//			appraiseeList = uF.showData(getAppendData(con, selfList, hmEmpName), "");
			StringBuilder sb1 = new StringBuilder();
			for (int i = 0; i < selfList.size(); i++) {
				if (i == 0) {
					sb1.append("," + selfList.get(i).trim()+",");
				} else {
					sb1.append(selfList.get(i).trim()+",");
				}
			}
			
			StringBuilder sb2 = new StringBuilder();
			for (int i = 0; i < finalizeList.size(); i++) {
				if (i == 0) {
					sb2.append("," + finalizeList.get(i).trim()+",");
				} else {
					sb2.append(finalizeList.get(i).trim()+",");
				}
			}
					
			List<String> memberList = CF.getOrientationMemberDetails(con, uF.parseToInt(getOreinted()));
			StringBuilder members = new StringBuilder();
			for(int i=0; i<memberList.size(); i++) {
				if(i==0)
					members.append(memberList.get(i));
				else
					members.append(","+memberList.get(i));
			}
			
//			System.out.println("orientationId==>"+getOreinted()+"==>memberList==>"+memberList);
			String hrIds = request.getParameter("hidehrId");
			String managerIds = request.getParameter("hidemanagerId");
			String peerIds = request.getParameter("hidepeerId");
			String otherIds = request.getParameter("hideotherId");
			String ceoIds = request.getParameter("hideCeoId");
			String hodIds = request.getParameter("hideHodId");
//			System.out.println("hrIds ===> "+hrIds);
//			System.out.println("managerIds ===> "+managerIds);
			
			String bothHRIds = getEmployeeList(hrIds, sb1.toString(), uF.parseToInt(hmOrientMemberID.get("HR")));
			String bothManagerIds = getEmployeeList(managerIds, sb1.toString(), uF.parseToInt(hmOrientMemberID.get("Manager")));
			String bothPeerIds = getEmployeeList(peerIds, sb1.toString(), uF.parseToInt(hmOrientMemberID.get("Peer")));
			String bothCeoIds = getEmployeeList(ceoIds, sb1.toString(), uF.parseToInt(hmOrientMemberID.get("CEO")));
			String bothHodIds = getEmployeeList(hodIds, sb1.toString(), uF.parseToInt(hmOrientMemberID.get("HOD")));
			
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
//				System.out.println("al.add(emp.get(i).trim()) ==========>" + al.toString());
			}
			if(sbReviewers == null) {
				sbReviewers = new StringBuilder();
			}
			
			pst = con.prepareStatement("update appraisal_details set oriented_type=?,employee_id=?,level_id=?,desig_id=?,grade_id=?,wlocation_id=?," +
					"department_id=?,supervisor_id=?,peer_ids=?,self_ids=?,emp_status=?,added_by=?,entry_date=?,hr_ids=?,usertype_member=?," +
					"finalization_ids=?,other_ids=?,ceo_ids=?, hod_ids=?, reviewer_id=? where appraisal_details_id=?");
			pst.setString(1, getOreinted());
			
			if(uF.parseToInt(getOreinted()) == 5 && getHideSelfFillEmpIds() != null) {
				pst.setString(2, getHideSelfFillEmpIds());
			} else {
				if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Self")!= null && memberList.contains(hmOrientMemberID.get("Self"))) {
					pst.setString(2, sb1.toString());
				} else {
					pst.setString(2, "");
				}
			}
			pst.setString(3, getStrLevel());
			pst.setString(4, getStrDesignationUpdate());
			pst.setString(5, getEmpGrade()); 
			pst.setString(6, getStrWlocation());
			pst.setString(7, getStrDepart());
//			if(uF.parseToInt(getOreinted())!= 1 && uF.parseToInt(getOreinted())!= 5) {
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Manager")!= null  && memberList.contains(hmOrientMemberID.get("Manager"))) {
				pst.setString(8, bothManagerIds);
			} else {
				pst.setString(8, "");
			}
			
			//if(uF.parseToInt(getOreinted())== 3 || uF.parseToInt(getOreinted())== 4) {
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Peer")!= null && memberList.contains(hmOrientMemberID.get("Peer"))) {
				pst.setString(9, bothPeerIds);
			} else {
				pst.setString(9, "");
			}
			pst.setString(10, sb1.toString());
			/*if(memberList != null && memberList.size()>0 && memberList.contains(hmOrientMemberID.get("Self"))) {
				pst.setString(10, sb1.toString());
			} else {
				pst.setString(10, "");
			}*/
			
			pst.setString(11, getEmp_status());
			pst.setInt(12, uF.parseToInt(strSessionEmpId));
			pst.setDate(13, uF.getCurrentDate(CF.getStrTimeZone()));
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HR")!= null  && memberList.contains(hmOrientMemberID.get("HR"))) {
				pst.setString(14, bothHRIds);
			} else {
				pst.setString(14, "");
			}
			
			pst.setString(15,members.toString());
			pst.setString(16, sb2.toString());
			
		//	if(uF.parseToInt(getOreinted()) == 5  && otherIds != null && !otherIds.equals("")) {
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("Anyone")!= null && memberList.contains(hmOrientMemberID.get("Anyone"))) {
				pst.setString(17, otherIds);
			} else {
				pst.setString(17, "");
			}
			
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("CEO")!= null  && memberList.contains(hmOrientMemberID.get("CEO"))) {
				pst.setString(18, bothCeoIds);
			} else {
				pst.setString(18, "");
			}
			
			if(memberList != null && memberList.size()>0 && hmOrientMemberID.get("HOD")!= null  && memberList.contains(hmOrientMemberID.get("HOD"))) {
				pst.setString(19,bothHodIds);
			} else {
				pst.setString(19, "");
			}
			pst.setString(20, sbReviewers.toString());
			pst.setInt(21, uF.parseToInt(getId()));
			//System.out.println("pst==>"+pst);
			pst.execute();
			pst.close();
			
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			String reviewName = CF.getReviewNameById(con, uF, getId());
			
			session.setAttribute(MESSAGE, SUCCESSM+""+reviewName+" added successfully."+END);
			
			List<String> appFreqIDs = new ArrayList<String>();
			pst = con.prepareStatement("select appraisal_freq_id from appraisal_details_frequency where appraisal_id=? and is_delete=false");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			if(rs.next()) {
				appFreqIDs.add(rs.getString("appraisal_freq_id"));
			}
			rs.close();
			pst.close();
			
//			String strEmpIds = sb1.toString().length()>1 ? sb1.toString().substring(1, sb1.toString().length()-1) : "0";
//			Map<String, Map<String, String>> hmEmpReportingData = new HashMap<String, Map<String,String>>();
//			pst = con.prepareStatement("select * from employee_official_details where emp_id in ("+strEmpIds+")");
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				Map<String, String> hmInner = new HashMap<String, String>();
//				hmInner.put("MANAGER", rs.getString("supervisor_emp_id"));
//				hmInner.put("HR", rs.getString("emp_hr"));
//				hmInner.put("HOD", rs.getString("hod_emp_id"));
//				hmEmpReportingData.put(rs.getString("emp_id"), hmInner);
//			}
//			rs.close();
//			pst.close();
//			
//			Map<String, List<String>> hmEmpPeerList = new HashMap<String, List<String>>();
//			pst = con.prepareStatement("select eod.emp_id,supervisor_emp_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id=eod.emp_id and " +
//				"is_alive=true and supervisor_emp_id in (select supervisor_emp_id from employee_official_details where emp_id in ("+strEmpIds+"))");
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				List<String> innerList = hmEmpPeerList.get(rs.getString("supervisor_emp_id"));
//				if(innerList==null) innerList = new ArrayList<String>();
//				innerList.add(rs.getString("emp_id"));
//				hmEmpPeerList.put(rs.getString("supervisor_emp_id"), innerList);
//			}
//			rs.close();
//			pst.close();
//			
//			StringBuilder sbCEO = null;
//			pst = con.prepareStatement("select eod.emp_id from employee_official_details eod, employee_personal_details epd, user_details ud where epd.emp_per_id=eod.emp_id and " +
//				" ud.emp_id=eod.emp_id and ud.usertype_id=5 and is_alive=true and org_id in (select org_id from employee_official_details where emp_id in ("+strEmpIds+"))");
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				if(sbCEO==null) {
//					sbCEO = new StringBuilder();
//					sbCEO.append(rs.getString("emp_id"));
//				} else {
//					sbCEO.append(","+rs.getString("emp_id"));
//				}
//			}
//			rs.close();
//			pst.close();
//			if(sbCEO==null) {
//				sbCEO = new StringBuilder();
//			}
			
//			pst = con.prepareStatement("select * from employee_official_details where supervisor_emp_id in ("+strEmpIds+")");
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				Map<String, String> hmInner = hmEmpReportingData.get(rs.getString("supervisor_emp_id"));
//				if(hmInner==null) hmInner = new HashMap<String, String>();
//				StringBuilder sbTMem = null;
//				if(hmInner.get("SUBORDINATE")==null || hmInner.get("SUBORDINATE").isEmpty()) {
//					sbTMem = new StringBuilder();
//					sbTMem.append(rs.getString("emp_id"));
//				} else {
//					sbTMem = new StringBuilder();
//					sbTMem.append(hmInner.get("SUBORDINATE"));
//					sbTMem.append(","+rs.getString("emp_id"));
//				}
//				hmInner.put("SUBORDINATE", sbTMem.toString());
//				hmEmpReportingData.put(rs.getString("supervisor_emp_id"), hmInner);
//			}
//			rs.close();
//			pst.close();
			
			
			List<String> managerIDList = new ArrayList<String>();
			List<String> peerIDList = new ArrayList<String>();
			List<String> hrIDList = new ArrayList<String>();
			List<String> ceoIDList = new ArrayList<String>();
			List<String> hodIDList = new ArrayList<String>();
			List<String> subordinateIDList = new ArrayList<String>();
			
			for(int i=0; appFreqIDs!= null && !appFreqIDs.isEmpty() && i<appFreqIDs.size(); i++) {
				for(int j=0; selfList!= null && !selfList.isEmpty() && j<selfList.size(); j++) {
					
					String hrsId = uF.getAppendData(request.getParameterValues("hrsId_"+selfList.get(j)));
					String ceosId = uF.getAppendData(request.getParameterValues("ceosId_"+selfList.get(j)));
					String hodsId = uF.getAppendData(request.getParameterValues("hodsId_"+selfList.get(j)));
					String subordinatesId = uF.getAppendData(request.getParameterValues("subordinatesId_"+selfList.get(j)));
					String managersId = uF.getAppendData(request.getParameterValues("managersId_"+selfList.get(j)));
					String peersId = uF.getAppendData(request.getParameterValues("peersId_"+selfList.get(j)));
					
					List<String> managerList = Arrays.asList(uF.showData(managersId, "").split(","));
					managerIDList.addAll(managerList);
					List<String> peerList = Arrays.asList(uF.showData(peersId, "").split(","));
					peerIDList.addAll(peerList);
					List<String> hrList = Arrays.asList(uF.showData(hrsId, "").split(","));
					hrIDList.addAll(hrList);
					List<String> ceoList = Arrays.asList(uF.showData(ceosId, "").split(","));
					ceoIDList.addAll(ceoList);
					List<String> hodList = Arrays.asList(uF.showData(hodsId, "").split(","));
					hodIDList.addAll(hodList);
					List<String> subordinateList = Arrays.asList(uF.showData(subordinatesId, "").split(","));
					subordinateIDList.addAll(subordinateList);
//					Map<String, String> hmInner = hmEmpReportingData.get(selfList.get(j));
//					if(hmInner==null) hmInner = new HashMap<String, String>();
//					List<String> innerPeerList = hmEmpPeerList.get(hmInner.get("MANAGER"));
//					StringBuilder sbPeerIds = null;
//					for(int a=0; innerPeerList!=null && a<innerPeerList.size(); a++) {
//						if(uF.parseToInt(innerPeerList.get(a)) == uF.parseToInt(selfList.get(j))){
//							continue;
//						}
//						if(sbPeerIds==null) {
//							sbPeerIds = new StringBuilder();
//							sbPeerIds.append(innerPeerList.get(a));
//						} else {
//							sbPeerIds.append(","+innerPeerList.get(a));
//						}
//					}
//					if(sbPeerIds==null) {
//						sbPeerIds = new StringBuilder();
//					}
					
					pst = con.prepareStatement("insert into appraisal_reviewee_details (appraisal_id, appraisal_freq_id,reviewee_id,subordinate_ids," +
						"supervisor_ids,peer_ids,hr_ids,hod_ids,ceo_ids) values (?,?,?,?, ?,?,?,?, ?)"); 
					pst.setInt(1, uF.parseToInt(getId()));
					pst.setInt(2, uF.parseToInt(appFreqIDs.get(i)));
					pst.setInt(3, uF.parseToInt(selfList.get(j)));
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
//					System.out.println("pst appraisal_reviewee_details set ====> " + pst);
					pst.executeUpdate();
					pst.close();
	
				}
			}
			
			for(int i=0; selfList!= null && !selfList.isEmpty() && i<selfList.size(); i++) {
				String strDomain = request.getServerName().split("\\.")[0];
				String alertData = "<div style=\"float: left;\"> A new Review ("+reviewName+") received for role <b>Self</b> from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
				String alertAction = "MyHR.action?pType=WR";
				
				UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(selfList.get(i));
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
				userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
				userAlerts.setStatus(INSERT_WR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
				
//				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//				userAlerts.setStrDomain(strDomain);
//				userAlerts.setStrEmpId(selfList.get(i));
//				userAlerts.set_type(MY_REVIEW_ALERT);
//				userAlerts.setStatus(INSERT_ALERT);
//				Thread t = new Thread(userAlerts);
//				t.run();
			}
			
			
			if (hmOrientMemberID.get("Manager") != null && memberList.contains(hmOrientMemberID.get("Manager"))) {
				String strDomain = request.getServerName().split("\\.")[0];
				for(int i=0; managerIDList!= null && !managerIDList.isEmpty() && i<managerIDList.size(); i++) {
					if(!managerIDList.get(i).equals("") && uF.parseToInt(managerIDList.get(i)) > 0) {
						String alertData = "<div style=\"float: left;\"> A new Review ("+reviewName+") received for role <b>Manager</b> from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
						String alertAction = "Reviews.action?callFrom=Dash";
						
						UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(managerIDList.get(i));
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(MANAGER));
						userAlerts.setStatus(INSERT_WR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//						userAlerts.setStrDomain(strDomain);
//						userAlerts.setStrEmpId(managerIDList.get(i));
//						userAlerts.set_type(MANAGER_REVIEW_ALERT);
//						userAlerts.setStatus(INSERT_ALERT);
//						Thread t = new Thread(userAlerts);
//						t.run();
					}
				}
			}
			if (hmOrientMemberID.get("Peer") != null && memberList.contains(hmOrientMemberID.get("Peer"))) {
				String strDomain = request.getServerName().split("\\.")[0];
				for(int i=0; peerIDList!= null && !peerIDList.isEmpty() && i<peerIDList.size(); i++) {
					if(!peerIDList.get(i).equals("") && uF.parseToInt(peerIDList.get(i)) > 0) {
						String alertData = "<div style=\"float: left;\"> A new Review ("+reviewName+") received for role <b>Peer</b> from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
						String alertAction = "MyHR.action?pType=WR";
						
						UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(peerIDList.get(i));
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
						userAlerts.setStatus(INSERT_WR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//						userAlerts.setStrDomain(strDomain);
//						userAlerts.setStrEmpId(peerIDList.get(i));
//						userAlerts.set_type(PEER_REVIEW_ALERT);
//						userAlerts.setStatus(INSERT_ALERT);
//						Thread t = new Thread(userAlerts);
//						t.run();
					}
				}
			}
			if (hmOrientMemberID.get("HR") != null && memberList.contains(hmOrientMemberID.get("HR"))) {
				String strDomain = request.getServerName().split("\\.")[0];
				for(int i=0; hrIDList!= null && !hrIDList.isEmpty() && i<hrIDList.size(); i++) {
					if(!hrIDList.get(i).equals("") && uF.parseToInt(hrIDList.get(i)) > 0) {
						String alertData = "<div style=\"float: left;\"> A new Review ("+reviewName+") received for role <b>HR</b> from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
						String alertAction = "Reviews.action?callFrom=Dash";
						
						UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(hrIDList.get(i));
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(HRMANAGER));
						userAlerts.setStatus(INSERT_WR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//						userAlerts.setStrDomain(strDomain);
//						userAlerts.setStrEmpId(hrIDList.get(i));
//						userAlerts.set_type(HR_REVIEW_ALERT);
//						userAlerts.setStatus(INSERT_ALERT);
//						Thread t = new Thread(userAlerts);
//						t.run();
					}
				}
			}
			
			if (hmOrientMemberID.get("CEO") != null && memberList.contains(hmOrientMemberID.get("CEO"))) {
				String strDomain = request.getServerName().split("\\.")[0];
				for(int i=0; ceoIDList!= null && !ceoIDList.isEmpty() && i<ceoIDList.size(); i++) {
					if(!ceoIDList.get(i).equals("") && uF.parseToInt(ceoIDList.get(i)) > 0) {
						String alertData = "<div style=\"float: left;\"> A new Review ("+reviewName+") received for role <b>CEO</b> from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
						String alertAction = "Reviews.action?callFrom=Dash";
						
						UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(ceoIDList.get(i));
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(CEO));
						userAlerts.setStatus(INSERT_WR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//						userAlerts.setStrDomain(strDomain);
//						userAlerts.setStrEmpId(ceoIDList.get(i));
//						userAlerts.set_type(CEO_REVIEW_ALERT);
//						userAlerts.setStatus(INSERT_ALERT);
//						Thread t = new Thread(userAlerts);
//						t.run();
					}
				}
			}
			
			if (hmOrientMemberID.get("HOD") != null && memberList.contains(hmOrientMemberID.get("HOD"))) {
				String strDomain = request.getServerName().split("\\.")[0];
				for(int i=0; hodIDList!= null && !hodIDList.isEmpty() && i<hodIDList.size(); i++) {
					if(!hodIDList.get(i).equals("") && uF.parseToInt(hodIDList.get(i)) > 0) {
						String alertData = "<div style=\"float: left;\"> A new Review ("+reviewName+") received for role <b>HOD</b> from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
						String alertAction = "Reviews.action?callFrom=Dash";
						
						UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(hodIDList.get(i));
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(HOD));
						userAlerts.setStatus(INSERT_WR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//						userAlerts.setStrDomain(strDomain);
//						userAlerts.setStrEmpId(hodIDList.get(i));
//						userAlerts.set_type(HOD_REVIEW_ALERT);
//						userAlerts.setStatus(INSERT_ALERT);
//						Thread t = new Thread(userAlerts);
//						t.run();
					}
				}
			}
			
			
			pst = con.prepareStatement("select emp_per_id,usertype_id from employee_official_details eod, user_details ud, employee_personal_details epd where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and (ud.usertype_id=? or ud.usertype_id=?)");
			pst.setInt(1, uF.parseToInt(hmUserTypeId.get(ADMIN)));
			pst.setInt(2, uF.parseToInt(hmUserTypeId.get(HRMANAGER)));
			rs = pst.executeQuery();
			while(rs.next()) {
				String strDomain = request.getServerName().split("\\.")[0];
				String alertData = "<div style=\"float: left;\"> A new Review ("+reviewName+") created by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
				String alertAction = "Reviews.action?callFrom=Dash";
				
				UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(rs.getString("emp_per_id"));
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
				userAlerts.setCurrUserTypeID(rs.getString("usertype_id"));
				userAlerts.setStatus(INSERT_WR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
			}
			rs.close();
			pst.close();
			
//				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//				userAlerts.setStrDomain(strDomain);
//				userAlerts.setStrEmpId(rs.getString(1));
//				userAlerts.set_type(NEW_REVIEW_ALERT);
//				userAlerts.setStatus(INSERT_ALERT);
//				Thread t = new Thread(userAlerts);
//				t.run();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private List<String> getSelfList() {
		List<String> al = new ArrayList<String>();
		try {
			if (getEmployee() != null && getEmployee().length() > 0) {
				List<String> emp=Arrays.asList(getEmployee().split(","));
				for(int i=0;emp!=null && !emp.isEmpty() && i<emp.size();i++) {
					al.add(emp.get(i).trim());
				}
//				System.out.println("al.add(emp.get(i).trim()) ==========>" + al.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
	
/*	private List<String> getSelfList(Connection con) {
		List<String> al = new ArrayList<String>();
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		try {

			if (getEmployee() != null && getEmployee().length() > 0) {
				List<String> emp=Arrays.asList(getEmployee().split(","));
				for(int i=0;emp!=null && !emp.isEmpty() && i<emp.size();i++) {
					al.add(emp.get(i).trim());
				}
//				System.out.println("al.add(emp.get(i).trim()) ==========>" + al.toString());
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
					al.add(rsEmpCode.getString("emp_per_id"));
				}
				rsEmpCode.close();
				pst.close();
		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}*/
	
	
	private List<String> getFinalizeIdsList(Connection con) {
		List<String> al = new ArrayList<String>();
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		try {

			if (getFinalizationName() != null && getFinalizationName().length() > 0) {
				List<String> empFinal=Arrays.asList(getFinalizationName().split(","));
				for(int i=0;empFinal!=null && !empFinal.isEmpty() && i<empFinal.size();i++) {
					al.add(empFinal.get(i).trim());
				}
//				System.out.println("al.add(emp.get(i).trim()) ==========>" + al.toString());
			} else {
				StringBuilder sbQuery=new StringBuilder();
				sbQuery.append("select * from employee_official_details eod,employee_personal_details epd,user_details ud where " +
						"epd.emp_per_id=eod.emp_id and epd.emp_per_id=ud.emp_id and ud.usertype_id in(1,7) ");
				if(getStrWlocation()!=null && !getStrWlocation().equals("")) {
					sbQuery.append(" and eod.wlocation_id in ("+getStrWlocation()+") ");
				}
				sbQuery.append(" order by epd.emp_per_id");
	
				pst = con.prepareStatement(sbQuery.toString());			
				rsEmpCode = pst.executeQuery();
				while (rsEmpCode.next()) {
					al.add(rsEmpCode.getString("emp_per_id"));
				}
				rsEmpCode.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
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

//			System.out.println("option===="+sb.toString());
			request.setAttribute("option", sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private List<String> getOrientationMemberDetails(int id) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		List<String> memberList=new ArrayList<String>();
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from orientation_details where orientation_id=?");
			pst.setInt(1,id);
			rs=pst.executeQuery();
			while(rs.next()) {
				memberList.add(rs.getString("member_id"));
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
	
//===Created by Parvez date: 22-12-2021===
//===start Parvez===
	private void insertNxtAppraisalCycleGoal() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		con = db.makeConnection(con);

		String subsectionName = request.getParameter("subsectionname");
		String subsectionDescription = request.getParameter("subsectionDescription");
		String subsectionLongDescription = request.getParameter("subsectionLongDescription");
		String subSectionWeightage = request.getParameter("subSectionWeightage");
		
		String attribute = request.getParameter("attribute");
		String appraisalSystem = request.getParameter("appraisalSystem");
		String scoreCard = request.getParameter("scoreCard");

//		Map<String,String> orientationMemberMp=getOrientationMember(con);
//		Map<String, String> hmOrientMemberID = getOrientMemberID(con);
		try {
						
			pst = con.prepareStatement("insert into appraisal_level_details(level_title,short_description,long_description,appraisal_system," +
				"scorecard_type,appraisal_id,main_level_id,attribute_id,subsection_weightage,added_by,entry_date) values(?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setString(1, uF.showData(subsectionName, ""));
			pst.setString(2, uF.showData(subsectionDescription,""));
			pst.setString(3, uF.showData(subsectionLongDescription, ""));
			pst.setInt(4, uF.parseToInt(appraisalSystem));
			pst.setInt(5, uF.parseToInt(scoreCard));
			pst.setInt(6, uF.parseToInt(id));
			pst.setInt(7, uF.parseToInt(getMain_level_id()));
			pst.setInt(8, uF.parseToInt(attribute));
			pst.setString(9, uF.showData(subSectionWeightage,"100"));
			pst.setInt(10, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(11, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.execute();
			pst.close();
			
			int appraisal_level_id = 0;
			pst = con.prepareStatement("select max(appraisal_level_id) from appraisal_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rst = pst.executeQuery();
			while (rst.next()) {
				appraisal_level_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			String ansType = request.getParameter("ansType");
			/*int question_id = uF.parseToInt(hidequeid[i]);*/
			int question_id = 0;
			
//			System.out.println("GOal ID ===> "+goalId[i]+ "  optiona === "+optiona + "  optiona=b === "+optionb + "  optionc === "+optionc + "  optiond === "+optiond);
//			pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
//					"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans," +
//					"is_add,question_type,goal_kra_target_id,app_system_type)values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
			
			pst = con.prepareStatement("insert into question_bank(app_system_type)values(?)");
			
			pst.setInt(1, uF.parseToInt(appraisalSystem));
//			pst.setString(1, "");
//			pst.setString(2, "");
//			pst.setString(3, "");
//			pst.setString(4, "");
//			pst.setString(5, "");
//			pst.setString(6, "");
//			pst.setInt(7, 0);
//			pst.setInt(8, 0);
//			pst.setInt(9, 0);
//			pst.setInt(10, 0);
//			pst.setInt(11, 0);
//			pst.setString(12, "");
//			pst.setBoolean(13, false);
//			pst.setInt(14, uF.parseToInt(ansType));
//			pst.setInt(15, 0);
//			pst.setInt(16, uF.parseToInt(appraisalSystem));
			pst.execute();
			pst.close();
				
			pst = con.prepareStatement("select max(question_bank_id) from question_bank");
			rst = pst.executeQuery();
			while (rst.next()) {
				question_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

/*			pst = con.prepareStatement("insert into appraisal_question_details(question_id,attribute_id,appraisal_id,appraisal_level_id," +
				"answer_type,goal_kra_target_id,app_system_type,weightage) values(?,?,?,?, ?,?,?,?)");
			pst.setInt(1, question_id);
			pst.setInt(2, uF.parseToInt(attribute));
			pst.setInt(3, uF.parseToInt(id));
			pst.setInt(4, appraisal_level_id);
			pst.setInt(5, uF.parseToInt(ansType));
			pst.setInt(6, uF.parseToInt(goalId[i]));
			pst.setInt(7, uF.parseToInt(appraisalSystem));
			pst.setDouble(8, uF.parseToDouble(goalWeightage));
			pst.execute();
			pst.close();*/
			
			pst = con.prepareStatement("insert into appraisal_question_details(question_id,attribute_id,appraisal_id,appraisal_level_id," +
			"app_system_type,weightage) values(?,?,?,?,?,?)");
			pst.setInt(1, question_id);
			pst.setInt(2, uF.parseToInt(attribute));
			pst.setInt(3, uF.parseToInt(id));
			pst.setInt(4, appraisal_level_id);
			pst.setInt(5, uF.parseToInt(appraisalSystem));
			pst.setDouble(6, uF.parseToDouble(subSectionWeightage));
			pst.execute();
			pst.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
//===end Parvez
	
	
	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillEmployee> getFinalizationList() {
		return finalizationList;
	}

	public void setFinalizationList(List<FillEmployee> finalizationList) {
		this.finalizationList = finalizationList;
	}

	public String getAppraiselName() {
		return appraiselName;
	}

	public void setAppraiselName(String appraiselName) {
		this.appraiselName = appraiselName;
	}

	public String getOreinted() {
		return oreinted;
	}

	public void setOreinted(String oreinted) {
		this.oreinted = oreinted;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public String getFinalizationName() {
		return finalizationName;
	}

	public void setFinalizationName(String finalizationName) {
		this.finalizationName = finalizationName;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public String getAnsTypeOption() {
		return ansTypeOption;
	}

	public void setAnsTypeOption(String ansTypeOption) {
		this.ansTypeOption = ansTypeOption;
	}

	public String getEndTo() {
		return endTo;
	}

	public void setEndTo(String endTo) {
		this.endTo = endTo;
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

	public String getStartFrom() {
		return startFrom;
	}

	public void setStartFrom(String startFrom) {
		this.startFrom = startFrom;
	}

	public String getWeekday() {
		return weekday;
	}

	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}

	public List<FillAnswerType> getAnsTypeList() {
		return ansTypeList;
	}

	public void setAnsTypeList(List<FillAnswerType> ansTypeList) {
		this.ansTypeList = ansTypeList;
	}

	public String getAppraisalType() {
		return appraisalType;
	}

	public void setAppraisalType(String appraisalType) {
		this.appraisalType = appraisalType;
	}

	public String getEmp_status() {
		return emp_status;
	}

	public void setEmp_status(String emp_status) {
		this.emp_status = emp_status;
	}

	public String getUserlocation() {
		return userlocation;
	}

	public void setUserlocation(String userlocation) {
		this.userlocation = userlocation;
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

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillWLocation> getWorkList() {
		return workList;
	}

	public void setWorkList(List<FillWLocation> workList) {
		this.workList = workList;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public String getAppraisal_description() {
		return appraisal_description;
	}

	public void setAppraisal_description(String appraisal_description) {
		this.appraisal_description = appraisal_description;
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

	public String getMain_level_id() {
		return main_level_id;
	}

	public void setMain_level_id(String main_level_id) {
		this.main_level_id = main_level_id;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getAppraisal_instruction() {
		return appraisal_instruction;
	}

	public void setAppraisal_instruction(String appraisal_instruction) {
		this.appraisal_instruction = appraisal_instruction;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
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

	public String getAnonymousReview() {
		return anonymousReview;
	}

	public void setAnonymousReview(String anonymousReview) {
		this.anonymousReview = anonymousReview;
	}

	String strDomain;
	public void setDomain(String strDomain) {
		this.strDomain=strDomain;
	}

	public String getStrDomain() {
		return strDomain;
	}
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	@Override
	public void run() {
	
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		con = db.makeConnection(con);
		Map<String, String> hmReviewData = getReviewDetails(con);
		List<String> allIdList = new ArrayList<String>();
		if(hmReviewData.get("REVIEW_SELFID") != null && !hmReviewData.get("REVIEW_SELFID").equals("")) {
			List<String> selfID = Arrays.asList(hmReviewData.get("REVIEW_SELFID").split(",")); 
			for (int i = 0; selfID != null && i < selfID.size(); i++) {
				if(selfID.get(i) != null && !selfID.get(i).equals("")) {
					allIdList.add(selfID.get(i));
				}
			}
		}
		if(hmReviewData.get("REVIEW_PEERID") != null && !hmReviewData.get("REVIEW_PEERID").equals("")) {
			List<String> peerID = Arrays.asList(hmReviewData.get("REVIEW_PEERID").split(",")); 
			for (int i = 0; peerID != null && i < peerID.size(); i++) {
				if(peerID.get(i) != null && !peerID.get(i).equals("")) {
					allIdList.add(peerID.get(i));
				}
			}
		}
		if(hmReviewData.get("REVIEW_MANAGERID") != null && !hmReviewData.get("REVIEW_MANAGERID").equals("")) {
			List<String> managerID = Arrays.asList(hmReviewData.get("REVIEW_MANAGERID").split(",")); 
			for (int i = 0; managerID != null && i < managerID.size(); i++) {
				if(managerID.get(i) != null && !managerID.get(i).equals("")) {
					allIdList.add(managerID.get(i));
				}
			}
		}
		if(hmReviewData.get("REVIEW_HRID") != null && !hmReviewData.get("REVIEW_HRID").equals("")) {
			List<String> hrID = Arrays.asList(hmReviewData.get("REVIEW_HRID").split(",")); 
			for (int i = 0; hrID != null && i < hrID.size(); i++) {
				if(hrID.get(i) != null && !hrID.get(i).equals("")) {
					allIdList.add(hrID.get(i));
				}
			}
		}
		
		if(hmReviewData.get("REVIEW_CEOID") != null && !hmReviewData.get("REVIEW_CEOID").equals("")) {
			List<String> ceoID = Arrays.asList(hmReviewData.get("REVIEW_CEOID").split(",")); 
			for (int i = 0; ceoID != null && i < ceoID.size(); i++) {
				if(ceoID.get(i) != null && !ceoID.get(i).equals("")) {
					allIdList.add(ceoID.get(i));
				}
			}
		}
		
		if(hmReviewData.get("REVIEW_HODID") != null && !hmReviewData.get("REVIEW_HODID").equals("")) {
			List<String> hodID = Arrays.asList(hmReviewData.get("REVIEW_HODID").split(",")); 
			for (int i = 0; hodID != null && i < hodID.size(); i++) {
				if(hodID.get(i) != null && !hodID.get(i).equals("")) {
					allIdList.add(hodID.get(i));
				}
			}
		}
		
		Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);

//		 System.out.println("allIdList ==> "+ allIdList);
		// String strDomain = request.getServerName().split("\\.")[0];
		for (int i = 0; allIdList != null && !allIdList.isEmpty() && i < allIdList.size(); i++) {
			if(allIdList.get(i) != null && !allIdList.get(i).equals("")) {
				Map<String, String> hmEmpInner = hmEmpInfo.get(allIdList.get(i));
//				 System.out.println(i+" allIdList "+allIdList.get(i));
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_NEW_REVIW_PUBLISH, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrEmpId(allIdList.get(i));
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
	
				nF.setStrReviewName(hmReviewData.get("REVIEW_NAME"));
				nF.setStrReviewStartdate(uF.getDateFormat(hmReviewData.get("REVIEW_STARTDATE"), DBDATE, CF.getStrReportDateFormat()));
				nF.setStrReviewEnddate(uF.getDateFormat(hmReviewData.get("REVIEW_ENDDATE"), DBDATE, CF.getStrReportDateFormat()));
	
				nF.setStrEmpFname(hmEmpInner.get("FNAME"));
				nF.setStrEmpLname(hmEmpInner.get("LNAME"));
				nF.setEmailTemplate(true);				
				nF.sendNotifications();
			}
		}
		db.closeConnection(con);
	}
	
}