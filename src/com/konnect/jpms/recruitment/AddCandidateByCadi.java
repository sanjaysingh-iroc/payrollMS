package com.konnect.jpms.recruitment;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillApproval;
import com.konnect.jpms.select.FillBank;
import com.konnect.jpms.select.FillBloodGroup;
import com.konnect.jpms.select.FillCity;
import com.konnect.jpms.select.FillCountry;
import com.konnect.jpms.select.FillDegreeDuration;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillGender;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLeaveType;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMaritalStatus;
import com.konnect.jpms.select.FillNoticeDuration;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycleDuration;
import com.konnect.jpms.select.FillPayMode;
import com.konnect.jpms.select.FillProbationDuration;
import com.konnect.jpms.select.FillSalutation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.select.FillSources;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CandidateNotifications;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddCandidateByCadi extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
//	HttpSession session;
//	String strUserType = null;
//	String strSessionEmpId = null;

	private String mode;
	// private int empId;
	String operation;
	

	Boolean autoGenerate = false;
	private String step;
	private String serviceId;
	private String ServiceName;
	String org_id;
	String CandidateId;
	String show;
	String candibymail;
	String otherDocumentCnt;
	 String candiCurrCTC;
	 String candiExpectedCTC;
	 String candiNoticePeriod;
	 String availability;
	 String strAvailability;
	 private String sessionId;
	 
	 String refEmpCode;
	 String otherRefSrc;
	 String candiSource;
	 String isEmpCode;
	 String refEmpId;
	 String frombgverify;
	File idOtherDoc[];

	private List<FillOrganisation> orgList;
	List<FillSources> sourceList;
	StringBuilder sbServicesLink = new StringBuilder();
	CommonFunctions CF = null;

	public static String MOTHER = "MOTHER";
	public static String FATHER = "FATHER";
	public static String SPOUSE = "SPOUSE";
	public static String SIBLING = "SIBLING";

	private static Logger log = Logger.getLogger(AddCandidate.class);

	public String execute() {

		UtilityFunctions uF = new UtilityFunctions();
		getJobCodeFromrecID();
//		session = request.getSession();
		CF = new CommonFunctions();
		CF.setRequest(request);

		orgList = new FillOrganisation(request).fillOrganisation();
		if (uF.parseToInt(getOrg_id())==0) {
			setOrg_id(orgList.get(0).getOrgId());
		}
//		System.out.println("ACBC/114--orgList="+getOrg_id());

		request.setAttribute(PAGE, "/jsp/recruitment/AddCandidateByCadi.jsp");
		request.setAttribute(TITLE, "Add Candidate Details");
		request.setAttribute(MENU, null);
		
//		System.out.println("uF.parseToInt(getCandidateId())=====>"+uF.parseToInt(getCandidateId()));
//		System.out.println("getOperation()======>"+getOperation());
		
		CF.getFormValidationFields(request, ADD_UPDATE_CANDIDATE);
		boolean candiFlag = getCandidateName();
//		System.out.println("candiFlag ===>>" + candiFlag);
		if(!candiFlag) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied); 
			return ACCESS_DENIED;
		}
		
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
		request.setAttribute("hmFeatureStatus", hmFeatureStatus);
		
		if (getOperation() != null && getOperation().equals("U") && uF.parseToInt(getCandidateId()) != 0 && getMode() != null && getType() != null) {
			request.setAttribute(TITLE, "Update Candidate");
//			System.out.println("in dfsjkhdfg");
			viewEmployee();
			loadValidateEmployee();
//			setType(null);
//
			if (uF.parseToInt(getStep()) == 8)
				selectedDateList(uF.parseToInt(getCandidateId()));

			return LOAD;

		} else if (uF.parseToInt(getCandidateId()) != 0 && getMode() != null && (getMode().equals("profile"))) {
//			System.out.println("in update");
//			 if (getStepSubmit() != null){
			updateEmployee();
			

			if (uF.parseToInt(getStep()) == 1) {
				setStep("2");
			} else if (uF.parseToInt(getStep()) == 2) {
				setStep("3");
			} else if (uF.parseToInt(getStep()) == 3) {
				setStep("4");
			} else if (uF.parseToInt(getStep()) == 4) {
				if(getFrombgverify()!=null) {
					setStep("7");
				} else { 
					setStep("5");
				}
			} else if (uF.parseToInt(getStep()) == 5) {
				setStep("6");
			} else if (uF.parseToInt(getStep()) == 6) {
				setStep("7");
			} else if (uF.parseToInt(getStep()) == 7) {
				selectedDateList(uF.parseToInt(getCandidateId()));
			//===start parvez date: 09-08-2022===	
				if(uF.parseToBoolean(hmFeatureStatus.get(F_DISABLE_AVAILABILITY_OF_INTERVIEW))){
					request.setAttribute("STATUS_MSG", "<h4 style='text-align:center;'>Your details has been submited successfully.</h4>");
					return "finish";
				} else {
					if(getFrombgverify()!=null) {
						request.setAttribute("STATUS_MSG", "<h4 style='text-align:center;'>Your details has been submited successfully.</h4>");
						return "finish";
					} else {
						setStep("8");
					}
				}
			//===end parvez date: 09-08-2022===	
			} else if (uF.parseToInt(getStep()) == 8) {
				request.setAttribute("STATUS_MSG", "<h4 style='text-align:center;'>Your details has been submited successfully.</h4>");
				return "finish";
			}
		
			viewEmployee();
			loadValidateEmployee();
			
			return SUCCESS;
//			 }
//			if(getCandibymail() != null && getCandibymail().equals("yes")){
//				return "logout";
//			}else{
//				return PROFILE;
//			}
			//
			// }else if (getOperation()!=null && getOperation().equals("U") &&
			// uF.parseToInt(getCandidateId())!=0 ) {
			//
			// // if (getStepSubmit() != null){
			// updateEmployee();
			// // }
			// viewEmployee();
			// loadValidateEmployee();

		} 
		

		if (uF.parseToInt(getStep()) != 0) {
//			System.out.println("getOperation() ===>> " + getOperation());
			if (getOperation() == null || !getOperation().equals("U")) {
				insertEmployee();
			}
			loadValidateEmployee();
			if (uF.parseToInt(getStep()) == 1) {
				setStep("2");
			} else if (uF.parseToInt(getStep()) == 2) {
				setStep("3");
			} else if (uF.parseToInt(getStep()) == 3) {
				setStep("4");
			} else if (uF.parseToInt(getStep()) == 4) {
				setStep("5");
			} else if (uF.parseToInt(getStep()) == 5) {
				setStep("6");
			} else if (uF.parseToInt(getStep()) == 6) {
				setStep("7");
			} else if (uF.parseToInt(getStep()) == 7) {
				selectedDateList(uF.parseToInt(getCandidateId()));
			//===start parvez date: 09-08-2022===	
//				setStep("8");
				if(uF.parseToBoolean(hmFeatureStatus.get(F_DISABLE_AVAILABILITY_OF_INTERVIEW))){
					request.setAttribute("STATUS_MSG", "<h4 style='text-align:center;'>Your details has been submited successfully.</h4>");
					return "finish";
				}else {
					setStep("8");
				}
			//===end parvez date: 09-08-2022===	
				
			} else if (uF.parseToInt(getStep()) == 8) {
				request.setAttribute("STATUS_MSG", "<h4 style='text-align:center;'>Your details has been submited successfully.</h4>");
				return "finish";
			}

		} else if (uF.parseToInt(getStep()) == 0) {
			setStep("1");
			loadValidateEmployee();
		}

		return LOAD;
	}

	
	private void getJobCodeFromrecID() {

		PreparedStatement pst = null;
		Connection con = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select job_code from recruitment_details where recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				setJobcode(rst.getString("job_code"));
			}
			rst.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void getEmpMiscInfo(UtilityFunctions uF, String strEmpId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select added_by, emp_fname, emp_lname from candidate_personal_details where emp_per_id =?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			int nAddedBy = 0;
			while (rs.next()) {
				request.setAttribute("EMP_FNAME", rs.getString("emp_fname"));
				nAddedBy = uF.parseToInt(rs.getString("added_by"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select emp_email, emp_email_sec from candidate_personal_details where emp_per_id =?");
			pst.setInt(1, nAddedBy);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				if (rs.getString("emp_email_sec") != null && rs.getString("emp_email_sec").length() > 0) {
					request.setAttribute("ADDED_BY_EMAIL", rs.getString("emp_email_sec"));
				} else {
					request.setAttribute("ADDED_BY_EMAIL", rs.getString("emp_email"));
				}
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
	}

	String[] strTime;
	String[] strDate;

	public boolean insertAvailability(UtilityFunctions uF, String strEmpId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		boolean isValidSesseion = false;
		try {

			con = db.makeConnection(con);

			for (int i = 0; getStrDate() != null && i < getStrDate().length; i++) {

				if (getStrDate()[i] != null && getStrDate()[i].length() > 0) {
					pst = con.prepareStatement("insert into candidate_interview_availability (emp_id, ip_address, _timestamp, _date, _time,recruitment_id) values (?,?,?,?,?,?)");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setString(2, "");
					pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
					pst.setDate(4, uF.getDateFormat(getStrDate()[i], DATE_FORMAT));
					pst.setTime(5, uF.getTimeFormat(getStrTime()[i], TIME_FORMAT));
					pst.setInt(6, uF.parseToInt(getRecruitId()));
					pst.execute();
					pst.close();
				}

			}

			pst = con.prepareStatement("update candidate_personal_details set session_id =? where emp_per_id=?");
			pst.setString(1, "");
			pst.setInt(2, uF.parseToInt(getCandidateId()));
			pst.execute();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return isValidSesseion;
	}

	public boolean validateSession(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		boolean isValidSesseion = false;
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from candidate_personal_details where emp_per_id=?");
			pst.setInt(1, uF.parseToInt(request.getParameter("empId")));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				if (rs.getString("session_id") != null && rs.getString("session_id").equals((String) request.getParameter("sessionId"))) {
					isValidSesseion = true;
				}
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

		return isValidSesseion;
	}

	/*
	 * private void updateEmployeeAjax() {
	 * 
	 * Connection con = null; PreparedStatement pst = null; ResultSet rs = null;
	 * Database db = new Database(); UtilityFunctions uF = new
	 * UtilityFunctions();
	 * 
	 * try {
	 * 
	 * con = db.makeConnection(con);
	 * 
	 * int nActivityId = uF.parseToInt((String)request.getParameter("value"));
	 * int nEmpId = uF.parseToInt((String)request.getParameter("id"));
	 * 
	 * String strValue = null; pst =
	 * con.prepareStatement("select * from activity_details where activity_id = ?"
	 * ); pst.setInt(1, nActivityId); rs = pst.executeQuery(); if(rs.next()){
	 * strValue = rs.getString("activity_name"); }
	 * 
	 * 
	 * if(strValue!=null && strValue.equalsIgnoreCase(IConstants.TERMINATED)) {
	 * 
	 * pst = con.prepareStatement(updateUserStatus2); pst.setString(1,
	 * IConstants.TERMINATED); pst.setBoolean(2, true); pst.setDate(3,
	 * uF.getCurrentDate(CF.getStrTimeZone())); pst.setInt(4,
	 * uF.parseToInt(request.getParameter("id"))); log.debug("pst ==>"+pst);
	 * pst.execute();
	 * 
	 * 
	 * pst = con.prepareStatement(
	 * "insert into emp_off_board (emp_id, off_board_type, emp_reason, entry_date, notice_days, last_day_date) values (?,?,?,?,?,?)"
	 * ); pst.setInt(1, uF.parseToInt(request.getParameter("id")));
	 * pst.setString(2, IConstants.TERMINATED); pst.setString(3,
	 * "Direct termination"); pst.setDate(4,
	 * uF.getCurrentDate(CF.getStrTimeZone())); pst.setInt(5, 0); pst.setDate(6,
	 * uF.getCurrentDate(CF.getStrTimeZone())); pst.execute();
	 * 
	 * }if(strValue!=null && strValue.equalsIgnoreCase(IConstants.PERMANENT)) {
	 * // 9 is Permanent in activity details - when an employee is made
	 * permanent it also gets updated in probabtino policy table pst =
	 * con.prepareStatement
	 * ("update probation_policy set is_probation=false where emp_id=?");
	 * pst.setInt(1, uF.parseToInt(request.getParameter("id"))); pst.execute();
	 * 
	 * 
	 * 
	 * 
	 * }else if(strValue!=null){
	 * 
	 * pst = con.prepareStatement(
	 * "UPDATE employee_personal_details SET emp_status = ? WHERE emp_per_id = ?"
	 * ); pst.setString(1, strValue); pst.setInt(2,
	 * uF.parseToInt(request.getParameter("id"))); log.debug("pst===>"+pst);
	 * pst.execute();
	 * 
	 * }
	 * 
	 * } catch (Exception e) { e.printStackTrace();
	 * log.error(e.getClass()+":"+e.getMessage(),e); }finally {
	 * db.closeConnection(con); db.closeStatements(pst); }
	 * 
	 * }
	 */

	public void updateEmpFilledStatus(int nEmpId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE candidate_personal_details SET emp_filled_flag = ?, emp_filled_flag_date = ? " + "WHERE emp_per_id = ?");
			pst.setBoolean(1, true);

			java.util.Date date = new java.util.Date();
//			log.debug(new Timestamp(date.getTime()));
			pst.setTimestamp(2, new Timestamp(date.getTime()));
			pst.setInt(3, nEmpId);
//			log.debug("pst===>" + pst);
			pst.execute();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	

	public String loadValidateEmployee() {

		try {

			UtilityFunctions uF = new UtilityFunctions();

			wLocationList = new FillWLocation(request).fillWLocation();
			bankList = new FillBank(request).fillBankDetails();
			gradeList = new FillGrade(request).fillGrade();
			desigList = new FillDesig(request).fillDesig();
			levelList = new FillLevel(request).fillLevel();
			deptList = new FillDepartment(request).fillDepartment();
			// supervisorList = new FillEmployee().fillEmployeeCode(strUserType,
			// strSessionEmpId);
//			supervisorList = new FillEmployee(request).fillSupervisorNameCode(strUserType, strSessionEmpId);
			serviceList = new FillServices(request).fillServices();
			countryList = new FillCountry(request).fillCountry();
			stateList = new FillState(request).fillState();
			
			salutationList = new FillSalutation(request).fillSalutation();
			
			rosterDependencyList = new FillApproval().fillYesNo();
			empTypeList = new FillEmploymentType().fillEmploymentType(request);
			paymentModeList = new FillPayMode().fillPaymentMode();
			empGenderList = new FillGender().fillGender();
			probationDurationList = new FillProbationDuration().fillProbationDuration();
			noticeDurationList = new FillNoticeDuration().fillNoticeDuration();
			leaveTypeList = new FillLeaveType(request).fillLeave();
			maritalStatusList = new FillMaritalStatus().fillMaritalStatus();
			bloodGroupList = new FillBloodGroup().fillBloodGroup();
			degreeDurationList = new FillDegreeDuration().fillDegreeDuration();
			yearsList = new FillYears().fillYears(uF.getCurrentDate(CF.getStrTimeZone()));
			skillsList = new FillSkills(request).fillSkillsWithId();
			educationalList = new FillEducational(request).fillEducationalQual();
			sourceList = new FillSources().fillSourcesDetails();
			paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();

			request.setAttribute("educationalList", educationalList);
			request.setAttribute("yearsList", yearsList);
			request.setAttribute("degreeDurationList", degreeDurationList);
			request.setAttribute("empGenderList", empGenderList);
			request.setAttribute("currentYear", (uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()).toString(), DBDATE, "yyyy")));
			StringBuilder sbdegreeDuration = new StringBuilder();
			StringBuilder sbPrevEmployment = new StringBuilder();
			StringBuilder sbSibling = new StringBuilder();
			StringBuilder sbSkills = new StringBuilder();
			StringBuilder sbDocument = new StringBuilder();

			sbdegreeDuration.append("<option value=''> Degree</option>");

			for (int i = 0; i < educationalList.size(); i++) {

				sbdegreeDuration.append("<option value=" + ((FillEducational) educationalList.get(i)).getEduName()+ "> " + ((FillEducational) educationalList.get(i)).getEduName()
						+ "</option>");

			}
			sbdegreeDuration.append("<option value=other>Other</option>");
			//====start parvez on 02-07-2021======
			/*sbdegreeDuration.append("</select>" + "</td>" + "<td style='padding-left: 22px!important;'>" + "<select name= degreeDuration style=width:90px!important; >" + "<option value=''>Duration</option>");*/
			sbdegreeDuration.append("</select>" + "</td>" + "<td>" + "<select name= degreeDuration style=width:90px!important; >" + "<option value=''>Duration</option>");
			//====end parvez on 02-07-2021======
			
			for (int i = 0; i < degreeDurationList.size(); i++) {

				sbdegreeDuration.append("<option value=" + ((FillDegreeDuration) degreeDurationList.get(i)).getDegreeDurationID() + "> "
						+ ((FillDegreeDuration) degreeDurationList.get(i)).getDegreeDurationName() + "</option>");

			}

			//====start parvez on 02-07-2021======
			/*sbdegreeDuration.append("</select>" + "</td>" + "<td style='padding-left: 22px!important;'>" + "<select name=completionYear style=width:110px!important; >" + "<option value=''>Completion Year</option>");*/
			sbdegreeDuration.append("</select>" + "</td>" + "<td>" + "<select name=completionYear style=width:110px!important; >" + "<option value=''>Completion Year</option>");
			//====end parvez on 02-07-2021======

			for (int i = 0; i < yearsList.size(); i++) {

				sbdegreeDuration.append("<option value=" + ((FillYears) yearsList.get(i)).getYearsID() + "> " + ((FillYears) yearsList.get(i)).getYearsName() + "</option>");

			}

			//===start parvez date: 06-09-2021===
			
			
			/*sbdegreeDuration.append("</select>" + "</td>" + "<td style='padding-left: 22px!important;'><input type= text  style=height:25px;width:90px!important; name=grade ></input></td>"
					+ "<td><a href=javascript:void(0) onclick=addEducation() class=add-font ></a>");*/
			
			
			sbdegreeDuration.append("</select>" + "</td>" + "<td><input type= text  style=width:90px!important; name=grade ></input></td>"
					+"<td><input type=text style=width:90px!important; name=instituteName  id=instituteName ></input></td>"
					+"<td><input type=text style=width:90px!important; name=universityName id=universityName ></input></td>"
					+"<td><input type=text style=width:90px!important; name=subject  id=subject ></input></td>"
					+"<td><input type=text style=width:90px!important; name=strStartDate id=strStartDate ></input></td>"
					+"<td><input type=text style=width:90px!important; name=strCompletionDate id=strCompletionDate ></input></td>"
					+"<td><input type=text style=width:90px!important; name=marks id=marks onkeypress=return isNumberKey(event) ></input></td>"
					+"<td><input type=text style=width:90px!important; name=city1 id=city1 ></input></td>");
			
			//===end parvez date: 06-09-2021===

			request.setAttribute("sbdegreeDuration", sbdegreeDuration.toString());

			//====start parvez on 02-07-2021======
//			sbPrevEmployment.append("<table style=width:auto class='table table_no_border form-table'>" + "<tr><td class=txtlabel style=text-align:right> Company Name:</td>"
			//====end parvez on 02-07-2021======
					//===start parvez date: 07-09-2021===
//					+ "<td><input type=text name=prevCompanyName style=height:25px;width:180px; name=prevCompanyLocation onchange=prevCompanyExpFile(); ></input></td>" + "</tr>"
					//===end parvez date: 07-09-2021===
			sbPrevEmployment.append("<tr><td class=txtlabel style=text-align:right> Location:</td>" + "<td> <input type=text style=height:25px;width:180px; name=prevCompanyLocation ></input></td>"
					+ "</tr>" + "<tr><td class=txtlabel style=text-align:right> City: </td>" + "<td><input type=text style=height:25px;width:180px; name=prevCompanyCity ></input></td>"
					+ "</tr>" + "<tr><td class=txtlabel style=text-align:right> State:</td><td><input type=text style=height:25px;width:180px; name=prevCompanyState ></input></td></tr>"
					+ "<tr><td class=txtlabel style=text-align:right> Country:</td><td><input type=text style=height:25px;width:180px; name=prevCompanyCountry ></input></td></tr>"
					+ "<tr><td class=txtlabel style=text-align:right> Contact Number:</td><td><input type=text style=height:25px;width:180px; name=prevCompanyContactNo ></input>"
					+ "</td></tr>"
					+ "<tr><td class=txtlabel style=text-align:right> Reporting To:</td><td> <input type=text style=height:25px;width:180px; name=prevCompanyReportingTo ></input>"
					+ "</td></tr>"
					+ "<tr><td class=txtlabel style=text-align:right> Reporting Manager Phone Number:</td><td> <input type=text style=height:25px;width:180px; name=prevCompanyReportManagerPhNo ></input>"
					+ "</td></tr>"
					+ "<tr><td class=txtlabel style=text-align:right> HR Manager:</td><td> <input type=text style=height:25px;width:180px; name=prevCompanyHRManager ></input>"
					+ "</td></tr>"
					+ "<tr><td class=txtlabel style=text-align:right> HR Manager Phone Number:</td><td> <input type=text style=height:25px;width:180px; name=prevCompanyHRManagerPhNo ></input>"
					+ "</td></tr>"
					+ "<tr><td class=txtlabel style=text-align:right> From:</td><td> <input type=text style=height:25px;width:180px; name=prevCompanyFromDate ></input></td></tr>"
					+ "<tr><td class=txtlabel style=text-align:right> To:</td><td> <input type=text style=height:25px;width:180px; name=prevCompanyToDate ></input></td></tr> "
					+ "<tr><td class=txtlabel style=text-align:right> Designation:</td><td> <input type=text style=height:25px;width:180px; name=prevCompanyDesination ></input>"
					+ "</td></tr>"
					+ "<tr><td class=txtlabel style=text-align:right> Responsibility:</td><td> <input type=text style=height:25px;width:180px; name=prevCompanyResponsibilities >"
					+ "</input>" + "</td></tr>"
					+ "<tr><td class=txtlabel style=text-align:right> Skills: </td><td> <input type=text style=height:25px;width:180px; name=prevCompanySkills ></input></td></tr>"
				//===start parvez date: 08-08-2022===
					+ "<tr><td class=txtlabel style=text-align:right> ESIC No.: </td><td> <input type=text style=height:25px;width:180px; name=prevCompanyESICNo ></input></td></tr>"
					+ "<tr><td class=txtlabel style=text-align:right> UAN No.: </td><td> <input type=text style=height:25px;width:180px; name=prevCompanyUANNo ></input></td></tr>"
				//===end parvez date: 08-08-2022===	
					//+ "<tr><td class=txtlabel style=text-align:right> <a href=javascript:void(0) onclick=addPrevEmployment() class=add>Add more information..</a></td>"
			);

			request.setAttribute("sbPrevEmployment", sbPrevEmployment.toString());

			//=====start parvez on 02-07-2021======
			sbSibling.append("<table style=width:auto class='table table_no_border form-table'>" + "<tr><td style=text-align:center class=tdLabelheadingBg style=text-align:right colspan=2>Sibling's Information </td></tr>"
			//=====end parvez on 02-07-2021======
					+ "<tr><td class=txtlabel style=text-align:right>Name:</td><td><input type=text style=height:25px;width:180px; name=memberName ></input></td></tr>"
					+ "<tr><td class=txtlabel style=text-align:right>Date of birth:</td><td> <input type=text style=height:25px;width:180px; name=memberDob ></input></td></tr>"
					+ "<tr><td class=txtlabel style=text-align:right>Education:</td><td> <input type=text style=height:25px;width:180px; name=memberEducation ></input></td></tr>"
					+ "<tr><td class=txtlabel style=text-align:right>Occupation:</td><td> <input type=text style=height:25px;width:180px; name=memberOccupation ></input></td></tr>"
					+ "<tr><td class=txtlabel style=text-align:right>Contact Number:</td><td><input type=text style=height:25px;width:180px; name=memberContactNumber ></input></td></tr>"
					+ "<tr><td class=txtlabel style=text-align:right>Email Id:</td><td><input type=text style=height:25px;width:180px; name=memberEmailId ></input></td></tr>"
					+ "<tr><td class=txtlabel style=text-align:right>Gender:</td><td>" + "<select name= memberGender style=width:180px;>");

			for (int i = 0; i < empGenderList.size(); i++) {

				sbSibling.append("<option value=" + ((FillGender) empGenderList.get(i)).getGenderId() + "> " + ((FillGender) empGenderList.get(i)).getGenderName() + "</option>");

			}

			sbSibling.append("</select>" + "</td></tr>"
					+ "");

			request.setAttribute("sbSibling", sbSibling.toString());

			//====start parvez on 02-07-2021====
			sbSkills.append(" <table><tr><td</td><td>" + "<select name=skillName>" + "<option value=>Select Skill Name</option>");
			//====end parvez on 02-07-2021====

			for (int k = 0; k < skillsList.size(); k++) {

				sbSkills.append("<option value=" + ((FillSkills) skillsList.get(k)).getSkillsId() + "> " + ((FillSkills) skillsList.get(k)).getSkillsName() + "</option>");
			}

			//====start parvez on 02-07-2021====
			sbSkills.append("</select></td><td style='padding-left: 17px;'>" + "<select name=skillValue style=width:105px!importanttable-skills;>" + "<option value=>Skill Rating</option>");
			//====end parvez on 02-07-2021====

			for (int k = 1; k < 11; k++) {
				sbSkills.append("<option value=" + k + ">" + k + "</option>");
			}
			sbSkills.append("</select></td>");
//			log.debug("sbSkills==>" + sbSkills);
			request.setAttribute("sbSkills", sbSkills);
			
			
			
			

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return LOAD;

	}

//	private void generateEmpCode() {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		String empCodeAlpha = "", empCodeNum = "";
//		Map<String, String> hmSettings;
//		UtilityFunctions uF = new UtilityFunctions();
//
//		try {
//			con = db.makeConnection(con);
//			hmSettings = CF.getSettingsMap(con);
//			empCodeAlpha = hmSettings.get(O_EMP_CODE_ALPHA);
//			empCodeNum = hmSettings.get(O_EMP_CODE_NUM);
//			// setAutoGenerate(uF.parseToBoolean(hmSettings.get("EMP_CODE_AUTO_GENERATION")));
//
//			// if(getAutoGenerate()) {
//
//			pst = con.prepareStatement("SELECT empcode FROM candidate_personal_details where empcode like ? order by emp_per_id desc LIMIT 1");
//			pst.setString(1, "%" + empCodeAlpha + "%");
//			rs = pst.executeQuery();
////			System.out.println("new Date ===> " + new Date());
//			while (rs.next()) {
//				String strEmpCode = rs.getString("empcode");
//				String strEmpCodeNum = strEmpCode.substring(empCodeAlpha.length(), strEmpCode.length());
//				log.debug("code Number===>" + strEmpCodeNum);
//				empCodeNum = uF.parseToInt(strEmpCodeNum) + 1 + "";
//			}
//
//			log.debug("empCodeAlpha===>" + empCodeAlpha);
//			log.debug("empCodeNum===>" + empCodeNum);
//
//			setEmpCodeAlphabet(empCodeAlpha);
//			setEmpCodeNumber(empCodeNum);
//
//			// }
//
//			setAutoGenerate(uF.parseToBoolean(hmSettings.get("EMP_CODE_AUTO_GENERATION")));
//
//			/***
//			 * This position of code changed on 26-04-2012 for always displaying
//			 * the auto generated code
//			 */
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally {
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//			db.closeConnection(con);
//		}
//
//	}

	public String viewEmployee() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		// int nEmpOffId = 0;

		List<List<String>> alSkills = new ArrayList<List<String>>();
		List<List<String>> alHobbies;
		List<List<String>> alLanguages;
		List<List<String>> alEducation;
		List<List<Object>> alDocuments;
		List<List<String>> alPrevEmployment;
		
		List<List<Object>> alOtherDocuments;
		//===start parvez date: 18-09-2021===
		Map<String, List<String>> hmEducationDocs;
		//===end parvez date: 18-09-2021===

//		List<String> alSelectedDateList;

		try {

			con = db.makeConnection(con);
			setEmpPersonalDetails(con, uF);
			setEmpReferences(con, uF);
			/*
			 * setEmpOfficialDetails(con, uF); setUser(con, uF);
			 * setProbationPolicy(con, uF);
			 */

			alSkills = CF.selectCandidateSkills(con, uF.parseToInt(getCandidateId()));
			alHobbies = CF.selectCandidateHobbies(con, uF.parseToInt(getCandidateId()));
			alLanguages = CF.selectCandidateLanguages(con,uF.parseToInt(getCandidateId()));
			alEducation = CF.selectCandidateEducation(con, uF.parseToInt(getCandidateId()));
			//===start parvez date: 18-09-2021===
			hmEducationDocs = CF.selectCandidateEducationDocument(con, uF.parseToInt(getCandidateId()));
			//===end parvez date: 18-09-2021===

			String filePath = request.getRealPath("/userDocuments/");

			alDocuments = selectDocuments(con, uF.parseToInt(getCandidateId()), filePath);
			alOtherDocuments = selectOtherDocuments(con, uF.parseToInt(getCandidateId()), filePath);
			setEmpFamilyMembers(con, uF);
			alPrevEmployment = selectPrevEmploment(con, uF.parseToInt(getCandidateId()));
			setEmpMedicalInfo(con, uF);

			// INTERVIEW SCHEDULE

			// request.setAttribute("strEdit", strEdit);
			// setEmpPerId(strEdit);

			wLocationList = new FillWLocation(request).fillWLocation();
			bankList = new FillBank(request).fillBankDetails();
			// desigList = new FillDesig().fillDesig();
			gradeList = new FillGrade(request).fillGrade();
			deptList = new FillDepartment(request).fillDepartment();
//			supervisorList = new FillEmployee(request).fillEmployeeCode(strUserType, strSessionEmpId);
			serviceList = new FillServices(request).fillServices();
			countryList = new FillCountry(request).fillCountry();
			stateList = new FillState(request).fillState();

			salutationList = new FillSalutation(request).fillSalutation();
			
			rosterDependencyList = new FillApproval().fillYesNo();
			empTypeList = new FillEmploymentType().fillEmploymentType(request);
			paymentModeList = new FillPayMode().fillPaymentMode();
			request.setAttribute("alSkills", alSkills);

			request.setAttribute("alHobbies", alHobbies);
			request.setAttribute("alLanguages", alLanguages);
			request.setAttribute("alEducation", alEducation);
			request.setAttribute("alDocuments", alDocuments);
			request.setAttribute("alOtherDocuments", alOtherDocuments);
			request.setAttribute("alPrevEmployment", alPrevEmployment);
			//===start parvez date: 18-09-2021===
			request.setAttribute("hmEducationDocs", hmEducationDocs);
			//===end parvez date: 18-09-2021===

			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;

	}
	
	private boolean getCandidateName(){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		boolean flag = false;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmSettingOption = CF.getSettingsMap(con);
			CF.setStrDocSaveLocation(hmSettingOption.get("DOC_SAVE_LOCATION"));
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			String candName = "";
			pst = con.prepareStatement("select emp_fname,emp_mname,emp_lname,session_id from candidate_personal_details where emp_per_id=?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("pst Candi name = "+pst);
			String candiSessionId = null;
			while(rs.next()){
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				candName = rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname");
				candiSessionId = rs.getString("session_id");
			}
			rs.close();
			pst.close();

//			System.out.println("candiSessionId ===>>" + candiSessionId+"-- getSessionId() ===>> " + getSessionId());
			if((candiSessionId==null && getSessionId()==null) || candiSessionId.equals(getSessionId())) {
				flag = true;
			}
			flag = true;
			request.setAttribute("CandidateName", candName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}

	// Interview Schedule

	private void selectedDateList(int candidateId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		List<String> alDates = new ArrayList<String>();
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);

			Map<String, String> hmEmpNameMap = CF.getEmpNameMap(con, null, null);

			Map<String, String> hmDatesSelected = new LinkedHashMap<String, String>();
			Map<String, String> hmDatesRejected = new LinkedHashMap<String, String>();

			pst = con.prepareStatement("select * from candidate_interview_panel where recruitment_id=? and  candidate_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(2, candidateId);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("pst selectedDateList ===> "+pst);
			while (rs.next()) {

				String name = hmDatesSelected.get(uF.getDateFormat(rs.getString("interview_date"), DBDATE, DATE_FORMAT));
				if (name == null)
					name = hmEmpNameMap.get(rs.getString("panel_emp_id"));
				else
					name += "," + hmEmpNameMap.get(rs.getString("panel_emp_id"));
				hmDatesSelected.put(uF.getDateFormat(rs.getString("interview_date"), DBDATE, DATE_FORMAT), name);

			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from candidate_interview_availability join candidate_interview_panel_availability cipa"
					+ " using(int_avail_id) where cipa.recruitment_id=? and candidate_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(2, candidateId);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {

				String name = hmDatesRejected.get(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT));
				if (name == null)
					name = hmEmpNameMap.get(rs.getString("panel_emp_id"));
				else
					name += "," + hmEmpNameMap.get(rs.getString("panel_emp_id"));
				hmDatesRejected.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), name);

			}
			rs.close();
			pst.close();

			request.setAttribute("hmDatesSelected", hmDatesSelected);
			request.setAttribute("hmDatesRejected", hmDatesRejected);

			Map<String, String> hmDates = new LinkedHashMap<String, String>();

//			pst = con.prepareStatement("select * from candidate_interview_availability where recruitment_id=? and int_avail_id not in(select int_avail_id from candidate_interview_panel_availability where panel_emp_id=?) order by int_avail_id desc limit 3 ");
			pst = con.prepareStatement("select * from candidate_interview_availability where recruitment_id = ? and emp_id = ?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(2, candidateId);
//			log.debug("pst=>" + pst);
//			System.out.println("pst ===> "+pst);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				alDates.add(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT));
				hmDates.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), uF.showData(rs.getString("_time"), "       ").substring(0, 5));
			}
			rs.close();
			pst.close();

			request.setAttribute("hmDates", hmDates);
			request.setAttribute("alDates", alDates);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("alSelectedDateList", alDates);
//		log.debug("selectLanguages: alLanguages==>" + alDates);

	}

	private void setEmpReferences(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement("SELECT * FROM candidate_references WHERE emp_id = ? order by ref_name");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("pst 1 ===> "+pst);
			if (rs.next()) {
				setRef1Name(rs.getString("ref_name"));
				setRef1Company(rs.getString("ref_company"));
				setRef1Designation(rs.getString("ref_designation"));
				setRef1ContactNo(rs.getString("ref_contact_no"));
				setRef1Email(rs.getString("ref_email_id"));
			}

			if (rs.next()) {
				setRef2Name(rs.getString("ref_name"));
				setRef2Company(rs.getString("ref_company"));
				setRef2Designation(rs.getString("ref_designation"));
				setRef2ContactNo(rs.getString("ref_contact_no"));
				setRef2Email(rs.getString("ref_email_id"));
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
	}

	private void setEmpMedicalInfo(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {

			pst = con.prepareStatement("SELECT * FROM candidate_medical_details WHERE emp_id = ? order by question_id");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {

				if (rs.getInt("question_id") == 1) {
					setEmpMedicalId1(rs.getString("medical_id"));
					setCheckQue1(uF.parseToBoolean(rs.getString("yes_no")));
					setQue1Desc(uF.showData(rs.getString("description"), ""));

				} else if (rs.getInt("question_id") == 2) {
					setEmpMedicalId2(rs.getString("medical_id"));
					setCheckQue2(uF.parseToBoolean(rs.getString("yes_no")));
					setQue2Desc(uF.showData(rs.getString("description"), ""));

				} else if (rs.getInt("question_id") == 3) {
					setEmpMedicalId3(rs.getString("medical_id"));
					setCheckQue3(uF.parseToBoolean(rs.getString("yes_no")));
					setQue3Desc(uF.showData(rs.getString("description"), ""));

				} else if (rs.getInt("question_id") == 4) {
					setEmpMedicalId4(rs.getString("medical_id"));
					setCheckQue4(uF.parseToBoolean(rs.getString("yes_no")));
					setQue4Desc(uF.showData(rs.getString("description"), ""));

				} else if (rs.getInt("question_id") == 5) {
					setEmpMedicalId5(rs.getString("medical_id"));
					setCheckQue5(uF.parseToBoolean(rs.getString("yes_no")));
					setQue5Desc(uF.showData(rs.getString("description"), ""));
				}

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
	}

	/*
	 * public void setProbationPolicy(Connection con, UtilityFunctions uF) {
	 * 
	 * PreparedStatement pst =null; ResultSet rs = null;
	 * 
	 * try {
	 * 
	 * pst = con.prepareStatement(selectProbationPolicy);
	 * pst.setInt(1,uF.parseToInt(getCandidateId())); rs = pst.executeQuery();
	 * while(rs.next()){ setProbationDuration(rs.getInt("probation_duration"));
	 * setNoticeDuration(rs.getInt("notice_duration"));
	 * if(rs.getString("leaves_types_allowed")!=null){
	 * setProbationLeaves(rs.getString("leaves_types_allowed").split(",")); } }
	 * 
	 * } catch (Exception e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * }
	 */
	/*
	 * public void setEmpOfficialDetails(Connection con, UtilityFunctions uF) {
	 * 
	 * PreparedStatement pst =null; ResultSet rs = null;
	 * 
	 * try {
	 * 
	 * // pst = con.prepareStatement(
	 * "SELECT * FROM employee_official_details eod, employee_personal_details epd, department_info "
	 * + //
	 * "d WHERE eod.depart_id=d.dept_id and epd.emp_per_id=eod.emp_id AND emp_id=?"
	 * ); // pst =
	 * con.prepareStatement("SELECT * FROM employee_official_details where emp_id=?"
	 * );
	 * 
	 * Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap();
	 * 
	 * pst = con.prepareStatement(
	 * "SELECT * FROM employee_official_details eod left join grades_details gd on gd.grade_id = eod.grade_id where emp_id = ?"
	 * ); pst.setInt(1,uF.parseToInt(getCandidateId())); rs =
	 * pst.executeQuery(); log.debug("selectEmployeeR2V===>"+pst); if
	 * (rs.next()) {
	 * 
	 * setwLocation(rs.getString("wlocation_id")); //
	 * setDesignation(rs.getString("designation_id"));
	 * setDepartment(rs.getString("depart_id"));
	 * setEmpGrade(rs.getString("grade_id"));
	 * setStrDesignation(rs.getString("designation_id"));
	 * setStrLevel(hmEmpLevelMap.get(rs.getString("emp_id")));
	 * setSupervisor(rs.getString("supervisor_emp_id"));
	 * if(rs.getString("service_id")!=null){
	 * setService(rs.getString("service_id").split(",")); }
	 * setStrPaycycleDuration(rs.getString("paycycle_duration"));
	 * setEmpPaymentMode(rs.getString("payment_mode"));
	 * 
	 * 
	 * // setAvailFrom(uF.getDateFormat(rs.getString("available_time_from"),
	 * DBTIME, CF.getStrReportTimeFormat())); //
	 * setAvailTo(uF.getDateFormat(rs.getString("available_time_to"), DBTIME,
	 * CF.getStrReportTimeFormat())); setRosterDependency(new
	 * FillApproval().getBoolValue(rs.getString("is_roster")));
	 * setAttendanceDependency(new
	 * FillApproval().getBoolValue(rs.getString("is_attendance")));
	 * setEmpType(rs.getString("emptype"));
	 * setIsFirstAidAllowance(uF.parseToBoolean
	 * (rs.getString("first_aid_allowance"))); //
	 * setEmpDesignation(rs.getString("designation_name"));
	 * setEmpBankName(rs.getString("emp_bank_name"));
	 * setEmpBankAcctNbr(rs.getString("emp_bank_acct_nbr"));
	 * setEmpEmailSec(rs.getString("emp_email_sec"));
	 * setSkypeId(rs.getString("skype_id"));
	 * 
	 * }
	 * 
	 * } catch (Exception e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * }
	 */

	private int setEmpPersonalDetails(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		int nEmpOffId = 0;

		try {
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			// pst = con.prepareStatement(selectEmployeeR1V);
			Map<String, String> hmEmpIdCode = CF.getEmpIdCodeMap(con);
			pst = con.prepareStatement("SELECT * FROM candidate_personal_details WHERE emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
//			log.debug("pst selectEmployeeR1V==>" + pst);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			if (rs.next()) {
				// nEmpOffId = rs.getInt("emp_off_id");
				// setEmpId(rs.getInt("emp_per_id"));
				// setEmpCode(rs.getString("empcode"));

//				setEmpCode(rs.getString("empcode"), con, uF);
				setSalutation(rs.getString("salutation"));
				setEmpFname(rs.getString("emp_fname"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = rs.getString("emp_mname");
					}
				}
			
				
				setEmpMname(strEmpMName);
				setEmpLname(rs.getString("emp_lname"));
				setEmpEmail(rs.getString("emp_email"));

				setEmpAddress1(rs.getString("emp_address1"));
				setEmpAddress2(rs.getString("emp_address2"));
				setCity(rs.getString("emp_city_id"));
				setState(rs.getString("emp_state_id"));
				setCountry(rs.getString("emp_country_id"));
				setEmpPincode(rs.getString("emp_pincode"));

				setEmpAddress1Tmp(rs.getString("emp_address1_tmp"));
				setEmpAddress2Tmp(rs.getString("emp_address2_tmp"));
				setCityTmp(rs.getString("emp_city_id_tmp"));
				setStateTmp(rs.getString("emp_state_id_tmp"));
				setCountryTmp(rs.getString("emp_country_id_tmp"));
				setEmpPincodeTmp(rs.getString("emp_pincode_tmp"));

				setEmpContactno(rs.getString("emp_contactno"));
				setEmpPanNo(rs.getString("emp_pan_no"));
				setEmpPFNo(rs.getString("emp_pf_no"));
				setEmpGPFNo(rs.getString("emp_gpf_no"));
				setEmpGender(rs.getString("emp_gender"));
				setEmpDateOfBirth(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT));
				setEmpDateOfMarriage(uF.getDateFormat(rs.getString("emp_date_of_marriage"), DBDATE, DATE_FORMAT));
				setEmpBankName(rs.getString("emp_bank_name"));
				setEmpBankAcctNbr(rs.getString("emp_bank_acct_nbr"));
				setEmpEmailSec(rs.getString("emp_email_sec"));
				setSkypeId(rs.getString("skype_id"));
				setEmpMobileNo(rs.getString("emp_contactno_mob"));

				setEmpEmergencyContactName(rs.getString("emergency_contact_name"));
				setEmpEmergencyContactNo(rs.getString("emergency_contact_no"));
				setEmpPassportNo(rs.getString("passport_no"));
				setEmpPassportExpiryDate(uF.getDateFormat(rs.getString("passport_expiry_date"), DBDATE, DATE_FORMAT));
				setEmpBloodGroup(rs.getString("blood_group"));
				setEmpMaritalStatus(rs.getString("marital_status"));
				setCandiCurrCTC(rs.getString("current_ctc"));
				setCandiExpectedCTC(rs.getString("expected_ctc"));
				setCandiNoticePeriod(rs.getString("notice_period"));
				setAvailability(rs.getString("availability_for_interview"));
				setStrAvailability(rs.getBoolean("availability_for_interview") ? "1" : "0");
				setCandiSource(rs.getString("source_type"));
				setRefEmpCode(hmEmpIdCode.get(rs.getString("source_or_ref_code")));
				setOtherRefSrc(rs.getString("other_ref_src"));
				setEmpStartDate(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
//				log.debug("rs.getString(emp_img)==" + rs.getString("emp_image"));
				request.setAttribute("strImage", rs.getString("emp_image"));

				request.setAttribute("strEmpName", uF.showData(rs.getString("emp_fname"), "") + " "+strEmpMName+" " + uF.showData(rs.getString("emp_lname"), ""));

				/*
				 * if(rs.getString("service_id")!=null){
				 * setService(rs.getString("service_id").split(",")); }
				 */

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

		return nEmpOffId;

	}

//	private void setEmpCode(String empCode, Connection con, UtilityFunctions uF) {
//
//		try {
//
//			Map<String, String> hmSettings = CF.getSettingsMap(con);
//			String empCodeAlpha = hmSettings.get(O_EMP_CODE_ALPHA);
//			boolean isAutoGeneration = uF.parseToBoolean(hmSettings.get(O_EMP_CODE_AUTO_GENERATION));
//
//			if (empCode.contains(empCodeAlpha)) {
//
//				setEmpCodeAlphabet(empCodeAlpha);
//				setEmpCodeNumber(empCode.substring(empCodeAlpha.length(), empCode.length()));
//				setAutoGenerate(uF.parseToBoolean(hmSettings.get("EMP_CODE_AUTO_GENERATION")));
//
//			} else if (empCode.length() == 0) {
//				generateEmpCode();
//			} else {
//				setEmpCodeAlphabet(empCode.substring(0, empCodeAlpha.length()));
//				setEmpCodeNumber(empCode.substring(empCodeAlpha.length(), empCode.length()));
//				setAutoGenerate(uF.parseToBoolean(hmSettings.get("EMP_CODE_AUTO_GENERATION")));
//
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

	/*
	 * public void setUser(Connection con, UtilityFunctions uF) {
	 * 
	 * PreparedStatement pst =null; ResultSet rs = null;
	 * 
	 * try {
	 * 
	 * pst = con.prepareStatement(selectUserV1);
	 * pst.setInt(1,uF.parseToInt(getCandidateId())); rs = pst.executeQuery();
	 * while(rs.next()){ setUserName(rs.getString("username"));
	 * setEmpPassword(rs.getString("password"));
	 * setEmpUserTypeId(rs.getString("usertype_id")); }
	 * 
	 * } catch (Exception e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * }
	 */
	public void setEmpFamilyMembers(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<ArrayList<String>> alSiblings = new ArrayList<ArrayList<String>>();

		try {

			pst = con.prepareStatement("SELECT * FROM candidate_family_members WHERE emp_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
//			log.debug("pst==>" + pst);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {

//				log.debug("rs.getString(member_name)==>" + rs.getString("member_name"));

				if (rs.getString("member_type").equals(MOTHER)) {

					setMotherName(rs.getString("member_name"));
					setMotherDob(uF.getDateFormat(rs.getString("member_dob"), DBDATE, DATE_FORMAT));
					setMotherEducation(rs.getString("member_education"));
					setMotherOccupation(rs.getString("member_occupation"));
					setMotherContactNumber(rs.getString("member_contact_no"));
					setMotherEmailId(rs.getString("member_email_id"));
				}

				if (rs.getString("member_type").equals(FATHER)) {

					setFatherName(rs.getString("member_name"));
					setFatherDob(uF.getDateFormat(rs.getString("member_dob"), DBDATE, DATE_FORMAT));
					setFatherEducation(rs.getString("member_education"));
					setFatherOccupation(rs.getString("member_occupation"));
					setFatherContactNumber(rs.getString("member_contact_no"));
					setFatherEmailId(rs.getString("member_email_id"));

				}

				if (rs.getString("member_type").equals(SPOUSE)) {

					setSpouseName(rs.getString("member_name"));
					setSpouseDob(uF.getDateFormat(rs.getString("member_dob"), DBDATE, DATE_FORMAT));
					setSpouseEducation(rs.getString("member_education"));
					setSpouseOccupation(rs.getString("member_occupation"));
					setSpouseContactNumber(rs.getString("member_contact_no"));
					setSpouseEmailId(rs.getString("member_email_id"));
					setSpouseGender(rs.getString("member_gender"));

				}

				if (rs.getString("member_type").equals(SIBLING)) {

					ArrayList<String> alInner = new ArrayList<String>();
					alInner.add(rs.getString("member_id"));
					alInner.add(rs.getString("member_name"));
					alInner.add(uF.getDateFormat(rs.getString("member_dob"), DBDATE, DATE_FORMAT));
					alInner.add(rs.getString("member_education"));
					alInner.add(rs.getString("member_occupation"));
					alInner.add(rs.getString("member_contact_no"));
					alInner.add(rs.getString("member_email_id"));
					alInner.add(rs.getString("member_gender"));
					alSiblings.add(alInner);
				}

			}
			rs.close();
			pst.close();

			request.setAttribute("alSiblings", alSiblings);

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

	}

	public String updateEmployee() {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);

			if (uF.parseToInt(getStep()) == 1) {
				if (getEmpImage() != null)
					uploadImage(uF.parseToInt(getCandidateId()));

				if (getEmpFname() != null) {
					updateEmpPersonalDetails(con, uF);
				}
			} else if (uF.parseToInt(getStep()) == 5) {

				updateEmpReferences(con, uF);

			}

			StringBuilder sbServices = new StringBuilder();

			for (int i = 0; getService() != null && i < getService().length; i++) {

				if (uF.parseToInt(getService()[i]) > 0) {

					sbServices.append(getService()[i] + ",");
				}
			}

				if (uF.parseToInt(getStep()) == 2) {
					updateSkills(con, uF);
					updateHobbies(con, uF);
					updateEmpEducation(con, uF);
					updateEmpLangues(con, uF);

				} else if (uF.parseToInt(getStep()) == 3) {
					updateEmpFamilyMembers(con, uF);

				} else if (uF.parseToInt(getStep()) == 4) {
					updateEmpPrevEmploment(con, uF);

				} else if (uF.parseToInt(getStep()) == 6) {
					updateEmpMedicalInfo(con, uF);

				} else if (uF.parseToInt(getStep()) == 7) {
					updateSupportingDocuments(con, uF);

				} else if (uF.parseToInt(getStep()) == 8) {
//					System.out.println("in update profile");
					updateInterveiwDates(con, uF);
				}

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in updation");

		} finally {
			db.closeConnection(con);
		}

		return SUCCESS;

	}

	private void updateInterveiwDates(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		try {

			if(getStrDate() != null){
				pst = con.prepareStatement("DELETE FROM candidate_interview_availability WHERE emp_id = ? and recruitment_id=?");
				pst.setInt(1, uF.parseToInt(getCandidateId()));
				pst.setInt(2, uF.parseToInt(getRecruitId()));
//				log.debug("pst=>" + pst);
				pst.execute();
				pst.close();
				
				for (int i = 0; getStrDate() != null && i < getStrDate().length; i++) {
	
					if (getStrDate()[i] != null && getStrDate()[i].length() > 0) {
						pst = con.prepareStatement("insert into candidate_interview_availability (emp_id, ip_address, _timestamp, _date, _time,recruitment_id) values (?,?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(getCandidateId()));
						pst.setString(2, "");
						pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
						pst.setDate(4, uF.getDateFormat(getStrDate()[i], DATE_FORMAT));
						pst.setTime(5, uF.getTimeFormat(getStrTime()[i], TIME_FORMAT));
						pst.setInt(6, uF.parseToInt(getRecruitId()));
//						System.out.println("pstt====" + pst);
						pst.execute();
						pst.close();
					}
	
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
						
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	private void updateEmpReferences(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;

		try {
			pst = con.prepareStatement("delete from candidate_references where emp_id = ? ");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.executeUpdate();
			pst.close();
			
				pst = con.prepareStatement("INSERT INTO candidate_references (ref_name, ref_company, ref_designation, ref_contact_no, ref_email_id, emp_id) " + "values(?,?,?,?,?,?)");
				pst.setString(1, getRef1Name());
				pst.setString(2, getRef1Company());
				pst.setString(3, getRef1Designation());
				pst.setString(4, getRef1ContactNo());
				pst.setString(5, getRef1Email());
				pst.setInt(6, uF.parseToInt(getCandidateId()));
//				log.debug("pst==>" + pst);
				pst.execute();
				pst.close();
			
				pst = con.prepareStatement("INSERT INTO candidate_references (ref_name, ref_company, ref_designation, ref_contact_no, ref_email_id, emp_id) " + "values(?,?,?,?,?,?)");
				pst.setString(1, getRef2Name());
				pst.setString(2, getRef2Company());
				pst.setString(3, getRef2Designation());
				pst.setString(4, getRef2ContactNo());
				pst.setString(5, getRef2Email());
				pst.setInt(6, uF.parseToInt(getCandidateId()));
//				log.debug("pst==>" + pst);
				pst.execute();
				pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
						
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}


	}

	private void updateEmpMedicalInfo(Connection con, UtilityFunctions uF) {
		
		
		try {
			File file1=null;
			String filename1=null;
			int fileSize=1;
			if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(0).equals("1")){
				 file1=getQue1DescFile().get(fileSize-1);
				 filename1=getQue1DescFileFileName().get(fileSize-1);
				 fileSize++;
			}
			
			if(uF.parseToInt(getEmpMedicalId1())!=0)
			updateEmpMedicalInfo1(con,uF,getEmpMedicalId1(),isCheckQue1(),getQue1Desc(),file1,filename1);
			else
				insertEmpMedicalInfo1(con,uF,getQue1Id(),isCheckQue1(),getQue1Desc(),file1,filename1);
			
			
			File file2=null;
			String filename2=null;
			if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(1).equals("1")){
				 file2=getQue1DescFile().get(fileSize-1);
				 filename2=getQue1DescFileFileName().get(fileSize-1);
				 fileSize++;
			}
			
			if(uF.parseToInt(getEmpMedicalId2())!=0)
				updateEmpMedicalInfo1(con,uF,getEmpMedicalId2(),isCheckQue2(),getQue2Desc(),file2,filename2);
				else
					insertEmpMedicalInfo1(con,uF,getQue2Id(),isCheckQue2(),getQue2Desc(),file2,filename2);
			
			File file3=null;
			String filename3=null;
			if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(2).equals("1")){
				 file3=getQue1DescFile().get(fileSize-1);
				 filename3=getQue1DescFileFileName().get(fileSize-1);
				 fileSize++;
			}
			
			if(uF.parseToInt(getEmpMedicalId3())!=0)
				updateEmpMedicalInfo1(con,uF,getEmpMedicalId3(),isCheckQue3(),getQue3Desc(),file3,filename3);
				else
					insertEmpMedicalInfo1(con,uF,getQue3Id(),isCheckQue3(),getQue3Desc(),file3,filename3);
			
		
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	
	public void updateEmpPersonalDetails(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			pst = con.prepareStatement("UPDATE candidate_personal_details SET emp_fname=?, emp_lname=?, emp_email=?, emp_address1=?, " +
					"emp_address2=?, emp_city_id=?, emp_state_id=?, emp_country_id=?, emp_pincode=?, emp_address1_tmp=?, emp_address2_tmp=?, " +
					"emp_city_id_tmp=?, emp_state_id_tmp=?, emp_country_id_tmp=?, emp_pincode_tmp=?, emp_contactno=?, emp_pan_no = ?, " +
					"emp_pf_no = ?, emp_gpf_no = ?, emp_gender=?, emp_date_of_birth=?, emp_bank_name=?, emp_bank_acct_nbr=?, emp_email_sec=?, " +
					"skype_id=?, emp_contactno_mob=?, emergency_contact_name=?, emergency_contact_no=?, passport_no=?, passport_expiry_date=?, " +
					"blood_group=?, marital_status=?, emp_date_of_marriage=?,recruitment_id=?,job_code=?, emp_mname=?, salutation=?, emp_entry_date=?,"+
					"source_type=?,source_or_ref_code=?,other_ref_src=?,current_ctc=?,expected_ctc=?,notice_period=?,availability_for_interview=? WHERE emp_per_id=?");
			pst.setString(1, getEmpFname());
			pst.setString(2, getEmpLname());
			pst.setString(3, getEmpEmail());

			pst.setString(4, getEmpAddress1());
			pst.setString(5, getEmpAddress2());
			pst.setString(6, getCity());
			pst.setInt(7, uF.parseToInt(getState()));
			pst.setInt(8, uF.parseToInt(getCountry()));
			pst.setString(9, getEmpPincode());

			pst.setString(10, getEmpAddress1Tmp());
			pst.setString(11, getEmpAddress2Tmp());
			pst.setString(12, getCityTmp());
			pst.setInt(13, uF.parseToInt(getStateTmp()));
			pst.setInt(14, uF.parseToInt(getCountryTmp()));
			pst.setString(15, getEmpPincodeTmp());
			pst.setString(16, getEmpContactno());
			// pst.setString(11, (fileName.length() > 0) ? fileName :
			// "avatar_photo.png");
			pst.setString(17, getEmpPanNo());
			pst.setString(18, getEmpPFNo());
			pst.setString(19, getEmpGPFNo());
			pst.setString(20, getEmpGender());
			pst.setDate(21, uF.getDateFormat(getEmpDateOfBirth(), DATE_FORMAT));
			pst.setString(22, getEmpBankName());
			pst.setString(23, getEmpBankAcctNbr());
			pst.setString(24, uF.showData(getEmpEmailSec(), ""));
			pst.setString(25, uF.showData(getSkypeId(), ""));
			pst.setString(26, uF.showData(getEmpMobileNo(), ""));

			pst.setString(27, getEmpEmergencyContactName());
			pst.setString(28, getEmpEmergencyContactNo());
			pst.setString(29, getEmpPassportNo());
			pst.setDate(30, uF.getDateFormat(getEmpPassportExpiryDate(), DATE_FORMAT));
			pst.setString(31, getEmpBloodGroup());
			pst.setString(32, getEmpMaritalStatus());

			pst.setDate(33, uF.getDateFormat(getEmpDateOfMarriage(), DATE_FORMAT));
			pst.setInt(34, uF.parseToInt(getRecruitId()));
			pst.setString(35, getJobcode());
//				pst.setInt(36, uF.parseToInt(""));
			pst.setString(36, getEmpMname());
			pst.setString(37, getSalutation());
			pst.setTimestamp(38, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			
			pst.setInt(39, uF.parseToInt(getCandiSource()));
			pst.setInt(40, uF.parseToInt(getRefEmpId()));
			if(getCandiSource()!=null && uF.parseToInt(getCandiSource())==SOURCE_OTHER){
				pst.setString(41, getOtherRefSrc());
			}else{
				pst.setString(41, "");
			}
			pst.setDouble(42, uF.parseToDouble(getCandiCurrCTC()));
			pst.setDouble(43, uF.parseToDouble(getCandiExpectedCTC()));
			pst.setInt(44, uF.parseToInt(getCandiNoticePeriod()));
			pst.setBoolean(45, uF.parseToBoolean(getAvailability()));
			
			pst.setInt(46, uF.parseToInt(getCandidateId()));
			int pstint = pst.executeUpdate();
			pst.close();

			pst=con.prepareStatement("select wlocation,org_id from recruitment_details where recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rs=pst.executeQuery();
			int orgId = 0;
			int wLocId = 0;
			while(rs.next()) {
				orgId = rs.getInt("org_id");
				wLocId = rs.getInt("wlocation");
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			List<String> empList = new ArrayList<String>();
			if(orgId > 0 && wLocId > 0) {
				StringBuilder sbQue = new StringBuilder();
				sbQue.append("select emp_per_id from employee_official_details eod,user_details ud, employee_personal_details epd where " +
					" epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=?");
				sbQue.append(" and (org_id = "+orgId+" or org_id_access like '%,"+orgId+",%') and (wlocation_id = "+wLocId+" or wlocation_id_access like '%,"+wLocId+",%') ");
				pst=con.prepareStatement(sbQue.toString());
				pst.setInt(1, uF.parseToInt(hmUserTypeId.get(HRMANAGER)));
				rs=pst.executeQuery();
				while(rs.next()) {
					if(!empList.contains(rs.getString("emp_per_id").trim())) {
						empList.add(rs.getString("emp_per_id").trim());	
					}
				}
				rs.close();
				pst.close();
			}
			pst=con.prepareStatement("select emp_per_id from employee_official_details eod,user_details ud, employee_personal_details epd where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=?");
			pst.setInt(1, uF.parseToInt(hmUserTypeId.get(ADMIN)));
			rs=pst.executeQuery();
			while(rs.next()) {
				if(!empList.contains(rs.getString("emp_per_id").trim())) {
					empList.add(rs.getString("emp_per_id").trim());	
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("empList ===>> " + empList);
			String strDomain = request.getServerName().split("\\.")[0];
			for(int i=0; empList!= null && !empList.isEmpty() && i<empList.size(); i++) {
				if(!empList.get(i).equals("") && uF.parseToInt(empList.get(i)) > 0) {
					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(empList.get(i));
					userAlerts.set_type(NEW_CANDIDATE_FILL_ALERT);
					userAlerts.setStatus(INSERT_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
						
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
}

	/*public void updateEmpPrevEmploment(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;

		try {

			pst = con.prepareStatement("DELETE FROM candidate_prev_employment WHERE emp_id=?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
//			System.out.println("delete step 4 pst====>"+pst);
//			log.debug("pst=>" + pst);
			pst.execute();
			pst.close();
			
			if (getPrevCompanyName() != null && getPrevCompanyName().length > 0) {
				for (int i = 0; i < getPrevCompanyName().length; i++) {
					if (getPrevCompanyName()[i].length() != 0) {
						pst = con.prepareStatement("INSERT INTO candidate_prev_employment(company_name, company_location, company_city, company_state, "
							+ " company_country, company_contact_no, reporting_to, from_date, to_date, designation, responsibilities, skills, emp_id," +
							" report_manager_ph_no, hr_manager, hr_manager_ph_no)" +
							" VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
						pst.setString(1, getPrevCompanyName()[i]);
						pst.setString(2, getPrevCompanyLocation()[i]);
						pst.setString(3, getPrevCompanyCity()[i]);
						pst.setString(4, getPrevCompanyState()[i]);
						pst.setString(5, getPrevCompanyCountry()[i]);
						pst.setString(6, getPrevCompanyContactNo()[i]);
						pst.setString(7, getPrevCompanyReportingTo()[i]);
						pst.setDate(8, uF.getDateFormat(getPrevCompanyFromDate()[i], DATE_FORMAT));
						pst.setDate(9, uF.getDateFormat(getPrevCompanyToDate()[i], DATE_FORMAT));
						pst.setString(10, getPrevCompanyDesination()[i]);
						pst.setString(11, getPrevCompanyResponsibilities()[i]);
						pst.setString(12, getPrevCompanySkills()[i]);
						pst.setInt(13, uF.parseToInt(getCandidateId()));
						pst.setString(14, getPrevCompanyReportManagerPhNo()[i]);
						pst.setString(15, getPrevCompanyHRManager()[i]);
						pst.setString(16, getPrevCompanyHRManagerPhNo()[i]);
//						log.debug("pst=>" + pst);
//						System.out.println("pst====>"+pst);
						pst.execute();
						pst.close();
					}
				}
			}
			
			//===start parvez date: 08-09-2021===
			if (getPrevCompanyName() != null && getPrevCompanyName().length > 0) {
				for (int i = 0; i < getPrevCompanyName().length; i++) {
					if (getPrevCompanyName()[i].length() != 0) {
						if(uF.parseToInt(getExpLetterFileStatus()[i]) == 1) {
							MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
							File[] strExpLetterDoc = mpRequest.getFiles("prevCompanyExpLetter");
							String[] strExpLetterDocFileName = mpRequest.getFileNames("prevCompanyExpLetter");
							for(int j=0; strExpLetterDoc != null && j<strExpLetterDoc.length; j++) {
								String strFileName = null;
								if (CF.getStrDocSaveLocation() == null) {
									strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, strExpLetterDoc[j], strExpLetterDocFileName[j], strExpLetterDocFileName[j], CF);
								} else {
									strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_CANDIDATE + "/" + I_DOCUMENT + "/" + I_PREVIOUS_EMPLOYMENT_DOC + "/"+ getCandidateId(), strExpLetterDoc[j], strExpLetterDocFileName[j], strExpLetterDocFileName[j], CF);
								}
//								System.out.println("AC/3224--strFileName="+strFileName);
								pst = con.prepareStatement("INSERT INTO candidate_prev_employment(company_name, company_location, company_city, company_state, "
										+" company_country, company_contact_no, reporting_to, from_date, to_date, designation, responsibilities, skills, emp_id," +
										" report_manager_ph_no, hr_manager, hr_manager_ph_no)" +
										" VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
								pst.setString(1, getPrevCompanyName()[i]);
								pst.setString(2, getPrevCompanyLocation()[i]);
								pst.setString(3, getPrevCompanyCity()[i]);
								pst.setString(4, getPrevCompanyState()[i]);
								pst.setString(5, getPrevCompanyCountry()[i]);
								pst.setString(6, getPrevCompanyContactNo()[i]);
								pst.setString(7, getPrevCompanyReportingTo()[i]);
								pst.setDate(8, uF.getDateFormat(getPrevCompanyFromDate()[i], DATE_FORMAT));
								pst.setDate(9, uF.getDateFormat(getPrevCompanyToDate()[i], DATE_FORMAT));
								pst.setString(10, getPrevCompanyDesination()[i]);
								pst.setString(11, getPrevCompanyResponsibilities()[i]);
								pst.setString(12, getPrevCompanySkills()[i]);
								pst.setInt(13, uF.parseToInt(getCandidateId()));
								pst.setString(14, getPrevCompanyReportManagerPhNo()[i]);
								pst.setString(15, getPrevCompanyHRManager()[i]);
								pst.setString(16, getPrevCompanyHRManagerPhNo()[i]);
								pst.setString(17, strFileName);
//									log.debug("pst=>" + pst);
//									System.out.println("pst====>"+pst);
								pst.execute();
								pst.close();
							}
						}
						
					}
				}
			}
			//===end parvez date: 08-09-2021===

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
						
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}*/
	
	
	public void updateEmpPrevEmploment(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			/*pst = con.prepareStatement("DELETE FROM candidate_prev_employment WHERE emp_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
//			System.out.println("delete step 4 pst====>"+pst);
			pst.execute();
			pst.close();*/
			
			//===start parvez date: 08-09-2021===
			if (getPrevCompanyName() != null && getPrevCompanyName().length > 0) {
				
				pst = con.prepareStatement("select company_id from candidate_prev_employment where emp_id=?");
				pst.setInt(1, uF.parseToInt(getCandidateId()));
				rs = pst.executeQuery();
				List<String> alPrevCompanyId = new ArrayList<String>();
				while(rs.next()) {
					alPrevCompanyId.add(rs.getString("company_id"));
				}
//				System.out.println("AC/2432--alEmpDegreeId="+alEmpDegreeId);
				rs.close();
				pst.close();
				
				for (int i = 0; i < getPrevCompanyName().length; i++) {
//					System.out.println("AC/1851--getPrevCompanyName="+getPrevCompanyName()[i]);
					if (getPrevCompanyName()[i].length() != 0) {
//						System.out.println("AC/1852--getPrevCompanyId()="+getPrevCompanyId());
						if(getPrevCompanyId() != null && i<getPrevCompanyId().length && uF.parseToInt(getPrevCompanyId()[i])!=0) {
							if(uF.parseToInt(getExpLetterFileStatus()[i]) == 1) {
								MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
								//===start parvez date: 13-09-2021===
								File[] strExpLetterDoc = mpRequest.getFiles("prevCompanyExpLetter"+i);
								String[] strExpLetterDocFileName = mpRequest.getFileNames("prevCompanyExpLetter"+i);
								//===end parvez date: 13-09-2021===
								for(int j=0; strExpLetterDoc != null && j<strExpLetterDoc.length; j++) {
									String strFileName = null;
									if (CF.getStrDocSaveLocation() == null) {
										strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, strExpLetterDoc[j], strExpLetterDocFileName[j], strExpLetterDocFileName[j], CF);
									} else {
										strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_CANDIDATE + "/" + I_DOCUMENT + "/" + I_PREVIOUS_EMPLOYMENT_DOC + "/"+ getCandidateId(), strExpLetterDoc[j], strExpLetterDocFileName[j], strExpLetterDocFileName[j], CF);
									}
//									System.out.println("AC/1923--strFileName="+strFileName);
									
									/*pst = con.prepareStatement("Update candidate_prev_employment set company_name=?, company_location=?, company_city=?, company_state=?, "
											+ "company_country=?, company_contact_no=?, reporting_to=?, from_date=?, to_date=?, designation=?, responsibilities=?, skills=?, " +
												"report_manager_ph_no=?, hr_manager=?, hr_manager_ph_no=?,experience_letter=?"
											+ "where company_id=?");
									pst.setString(1, getPrevCompanyName()[i]);
									pst.setString(2, getPrevCompanyLocation()[i]);
									pst.setString(3, getPrevCompanyCity()[i]);
									pst.setString(4, getPrevCompanyState()[i]);
									pst.setString(5, getPrevCompanyCountry()[i]);
									pst.setString(6, getPrevCompanyContactNo()[i]);
									pst.setString(7, getPrevCompanyReportingTo()[i]);
									pst.setDate(8, uF.getDateFormat(getPrevCompanyFromDate()[i], DATE_FORMAT));
									pst.setDate(9, uF.getDateFormat(getPrevCompanyToDate()[i], DATE_FORMAT));
									pst.setString(10, getPrevCompanyDesination()[i]);
									pst.setString(11, getPrevCompanyResponsibilities()[i]);
									pst.setString(12, getPrevCompanySkills()[i]);
									pst.setString(13, getPrevCompanyReportManagerPhNo()[i]);
									pst.setString(14, getPrevCompanyHRManager()[i]);
									pst.setString(15, getPrevCompanyHRManagerPhNo()[i]);
									pst.setString(16, strFileName);
									pst.setInt(17, uF.parseToInt(getPrevCompanyId()[i]));
//									System.out.println("pst====>"+pst);
									pst.execute();
									pst.close();*/
							//===start parvez date: 08-08-2022 Note: added emp_esic_no & uan_no in query===		
									pst = con.prepareStatement("INSERT INTO candidate_prev_employment(company_name, company_location, company_city, company_state, "
											+ "company_country, company_contact_no, reporting_to, from_date, to_date, designation, responsibilities, skills, emp_id, " +
												"report_manager_ph_no, hr_manager, hr_manager_ph_no,experience_letter, emp_esic_no, uan_no)"
											+ "VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
							//===end parvez date: 08-08-2022===		
									pst.setString(1, getPrevCompanyName()[i]);
									pst.setString(2, getPrevCompanyLocation()[i]);
									pst.setString(3, getPrevCompanyCity()[i]);
									pst.setString(4, getPrevCompanyState()[i]);
									pst.setString(5, getPrevCompanyCountry()[i]);
									pst.setString(6, getPrevCompanyContactNo()[i]);
									pst.setString(7, getPrevCompanyReportingTo()[i]);
									pst.setDate(8, uF.getDateFormat(getPrevCompanyFromDate()[i], DATE_FORMAT));
									pst.setDate(9, uF.getDateFormat(getPrevCompanyToDate()[i], DATE_FORMAT));
									pst.setString(10, getPrevCompanyDesination()[i]);
									pst.setString(11, getPrevCompanyResponsibilities()[i]);
									pst.setString(12, getPrevCompanySkills()[i]);
									pst.setInt(13, uF.parseToInt(getCandidateId()));
									pst.setString(14, getPrevCompanyReportManagerPhNo()[i]);
									pst.setString(15, getPrevCompanyHRManager()[i]);
									pst.setString(16, getPrevCompanyHRManagerPhNo()[i]);
									pst.setString(17, strFileName);
								//===start parvez date: 08-08-2022===
									pst.setString(18, getPrevCompanyESICNo()[i]);
									pst.setString(19, getPrevCompanyUANNo()[i]);
								//===end parvez date: 08-08-2022===
//									System.out.println("AC/1971--pst====>"+pst);
									pst.execute();
									pst.close();
								}
							}else{
//								System.out.println("AC/1976--else");
								pst = con.prepareStatement("Update candidate_prev_employment set company_name=?, company_location=?, company_city=?, company_state=?, "
										+ "company_country=?, company_contact_no=?, reporting_to=?, from_date=?, to_date=?, designation=?, responsibilities=?, skills=?, " +
											"report_manager_ph_no=?, hr_manager=?, hr_manager_ph_no=?, emp_esic_no=?, uan_no=? "
										+ "where company_id=?");
								pst.setString(1, getPrevCompanyName()[i]);
								pst.setString(2, getPrevCompanyLocation()[i]);
								pst.setString(3, getPrevCompanyCity()[i]);
								pst.setString(4, getPrevCompanyState()[i]);
								pst.setString(5, getPrevCompanyCountry()[i]);
								pst.setString(6, getPrevCompanyContactNo()[i]);
								pst.setString(7, getPrevCompanyReportingTo()[i]);
								pst.setDate(8, uF.getDateFormat(getPrevCompanyFromDate()[i], DATE_FORMAT));
								pst.setDate(9, uF.getDateFormat(getPrevCompanyToDate()[i], DATE_FORMAT));
								pst.setString(10, getPrevCompanyDesination()[i]);
								pst.setString(11, getPrevCompanyResponsibilities()[i]);
								pst.setString(12, getPrevCompanySkills()[i]);
								pst.setString(13, getPrevCompanyReportManagerPhNo()[i]);
								pst.setString(14, getPrevCompanyHRManager()[i]);
								pst.setString(15, getPrevCompanyHRManagerPhNo()[i]);
								
						//===start parvez date: 08-08-2022===
//								pst.setInt(16, uF.parseToInt(getPrevCompanyId()[i]));
								pst.setString(16, getPrevCompanyESICNo()[i]);
								pst.setString(17, getPrevCompanyUANNo()[i]);
								pst.setInt(18, uF.parseToInt(getPrevCompanyId()[i]));
						//===end parvez date: 08-08-2022===
								
//								System.out.println("pst====>"+pst);
								pst.execute();
								pst.close();
								
								alPrevCompanyId.remove(getPrevCompanyId()[i]);
							}
						}else{
//							System.out.println("AC/1949--getExpLetterStatus="+getExpLetterStatus()[i]);
							if(uF.parseToInt(getExpLetterFileStatus()[i]) == 1) {
								MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
								//===start parvez date: 13-09-2021===
								File[] strExpLetterDoc = mpRequest.getFiles("prevCompanyExpLetter"+i);
								String[] strExpLetterDocFileName = mpRequest.getFileNames("prevCompanyExpLetter"+i);
								//===end parvez date: 13-09-2021===
//								System.out.println("AC/1963--strExpLetterDocFileName="+strExpLetterDocFileName);
								for(int j=0; strExpLetterDoc != null && j<strExpLetterDoc.length; j++) {
									String strFileName = null;
									if (CF.getStrDocSaveLocation() == null) {
										strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, strExpLetterDoc[j], strExpLetterDocFileName[j], strExpLetterDocFileName[j], CF);
									} else {
										strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_CANDIDATE + "/" + I_DOCUMENT + "/" + I_PREVIOUS_EMPLOYMENT_DOC + "/"+ getCandidateId(), strExpLetterDoc[j], strExpLetterDocFileName[j], strExpLetterDocFileName[j], CF);
									}
//									System.out.println("AC/1963--strFileName="+strFileName);
									
							//===start parvez date: 08-08-2022===		
									pst = con.prepareStatement("INSERT INTO candidate_prev_employment(company_name, company_location, company_city, company_state, "
											+ "company_country, company_contact_no, reporting_to, from_date, to_date, designation, responsibilities, skills, emp_id, " +
												"report_manager_ph_no, hr_manager, hr_manager_ph_no,experience_letter,emp_esic_no,uan_no)"
											+ "VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
							//===end parvez date: 08-08-2022===		
									pst.setString(1, getPrevCompanyName()[i]);
									pst.setString(2, getPrevCompanyLocation()[i]);
									pst.setString(3, getPrevCompanyCity()[i]);
									pst.setString(4, getPrevCompanyState()[i]);
									pst.setString(5, getPrevCompanyCountry()[i]);
									pst.setString(6, getPrevCompanyContactNo()[i]);
									pst.setString(7, getPrevCompanyReportingTo()[i]);
									pst.setDate(8, uF.getDateFormat(getPrevCompanyFromDate()[i], DATE_FORMAT));
									pst.setDate(9, uF.getDateFormat(getPrevCompanyToDate()[i], DATE_FORMAT));
									pst.setString(10, getPrevCompanyDesination()[i]);
									pst.setString(11, getPrevCompanyResponsibilities()[i]);
									pst.setString(12, getPrevCompanySkills()[i]);
									pst.setInt(13, uF.parseToInt(getCandidateId()));
									pst.setString(14, getPrevCompanyReportManagerPhNo()[i]);
									pst.setString(15, getPrevCompanyHRManager()[i]);
									pst.setString(16, getPrevCompanyHRManagerPhNo()[i]);
									pst.setString(17, strFileName);
							//===start parvez date: 08-08-2022===		
									pst.setString(18, getPrevCompanyESICNo()[i]);
									pst.setString(19, getPrevCompanyUANNo()[i]);
							//===end parvez date: 08-08-2022===
//									System.out.println("AC/1986--pst====>"+pst);
									pst.execute();
									pst.close();
								}
							}
						}
						
					}
				}
				
				for(int j=0; alPrevCompanyId!=null && j<alPrevCompanyId.size(); j++) {
					pst = con.prepareStatement("DELETE FROM candidate_prev_employment WHERE emp_id = ? and company_id=?");
					pst.setInt(1, uF.parseToInt(getCandidateId()));
					pst.setInt(2, uF.parseToInt(alPrevCompanyId.get(j)));
					pst.executeUpdate();
					pst.close();
					
				}
				
			}
			//===end parvez date: 08-09-2021===
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	public void updateEmpFamilyMembers(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		int updateCnt = 0;
		try {

			pst = con.prepareStatement("UPDATE candidate_family_members SET member_name = ?, member_dob = ?, member_education = ?, "
					+ "member_occupation = ?, member_contact_no = ?, member_email_id = ?, member_gender = ? " + "WHERE emp_id = ? and member_type = ?");

			pst.setString(1, getMotherName());
			pst.setDate(2, uF.getDateFormat(getMotherDob(), DATE_FORMAT));
			pst.setString(3, getMotherEducation());
			pst.setString(4, getMotherOccupation());
			pst.setString(5, getMotherContactNumber());
			pst.setString(6, getMotherEmailId());
			pst.setString(7, "F");
			pst.setInt(8, uF.parseToInt(getCandidateId()));
			pst.setString(9, MOTHER);
//			log.debug("pst=>" + pst);
			updateCnt = pst.executeUpdate();
			pst.close();
			
			if (updateCnt == 0) {
				pst = con.prepareStatement("INSERT INTO candidate_family_members(member_type, member_name, member_dob, member_education, "
						+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");

				pst.setString(1, MOTHER);
				pst.setString(2, getMotherName());
//				log.debug(getMotherDob() + "getMotherDob()");
				pst.setDate(3, uF.getDateFormat(getMotherDob(), DATE_FORMAT));
				pst.setString(4, getMotherEducation());
				pst.setString(5, getMotherOccupation());
				pst.setString(6, getMotherContactNumber());
				pst.setString(7, getMotherEmailId());
				pst.setString(8, "F");
				pst.setInt(9, uF.parseToInt(getCandidateId()));
//				log.debug("pst=>" + pst);
				pst.execute();
				pst.close();
			}

			pst = con.prepareStatement("UPDATE candidate_family_members SET member_name = ?, member_dob = ?, member_education = ?, "
					+ "member_occupation = ?, member_contact_no = ?, member_email_id = ?, member_gender = ? " + "WHERE emp_id = ? and member_type = ?");

			pst.setString(1, getFatherName());
			pst.setDate(2, uF.getDateFormat(getFatherDob(), DATE_FORMAT));
			pst.setString(3, getFatherEducation());
			pst.setString(4, getFatherOccupation());
			pst.setString(5, getFatherContactNumber());
			pst.setString(6, getFatherEmailId());
			pst.setString(7, "F");
			pst.setInt(8, uF.parseToInt(getCandidateId()));
			pst.setString(9, FATHER);
//			log.debug("pst=>" + pst);
			updateCnt = pst.executeUpdate();
			pst.close();
			
			if (updateCnt == 0) {

				pst = con.prepareStatement("INSERT INTO candidate_family_members(member_type, member_name, member_dob, member_education, "
						+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");

				pst.setString(1, FATHER);
				pst.setString(2, getFatherName());
				pst.setDate(3, uF.getDateFormat(getFatherDob(), DATE_FORMAT));
				pst.setString(4, getFatherEducation());
				pst.setString(5, getFatherOccupation());
				pst.setString(6, getFatherContactNumber());
				pst.setString(7, getFatherEmailId());
				pst.setString(8, "M");
				pst.setInt(9, uF.parseToInt(getCandidateId()));
//				log.debug("pst=>" + pst);
				pst.execute();
				pst.close();

			}

			pst = con.prepareStatement("UPDATE candidate_family_members SET member_name = ?, member_dob = ?, member_education = ?, "
					+ "member_occupation = ?, member_contact_no = ?, member_email_id = ?, member_gender = ? " + "WHERE emp_id = ? and member_type = ?");

			pst.setString(1, getSpouseName());
			pst.setDate(2, uF.getDateFormat(getSpouseDob(), DATE_FORMAT));
			pst.setString(3, getSpouseEducation());
			pst.setString(4, getSpouseOccupation());
			pst.setString(5, getSpouseContactNumber());
			pst.setString(6, getSpouseEmailId());
			pst.setString(7, getSpouseGender());
			pst.setInt(8, uF.parseToInt(getCandidateId()));
			pst.setString(9, SPOUSE);
//			log.debug("pst=>" + pst);
			updateCnt = pst.executeUpdate();
			pst.close();
			
			if (updateCnt == 0) {

				pst = con.prepareStatement("INSERT INTO candidate_family_members(member_type, member_name, member_dob, member_education, "
						+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");
				pst.setString(1, SPOUSE);
				pst.setString(2, getSpouseName());
				pst.setDate(3, uF.getDateFormat(getSpouseDob(), DATE_FORMAT));
				pst.setString(4, getSpouseEducation());
				pst.setString(5, getSpouseOccupation());
				pst.setString(6, getSpouseContactNumber());
				pst.setString(7, getSpouseEmailId());
				pst.setString(8, "F");
				pst.setInt(9, uF.parseToInt(getCandidateId()));
//				log.debug("pst=>" + pst);
				pst.execute();
				pst.close();
			}

			if (getMemberName() != null && getMemberName().length != 0) {

				pst = con.prepareStatement("DELETE FROM candidate_family_members WHERE emp_id = ? and member_type = ?");
				pst.setInt(1, uF.parseToInt(getCandidateId()));
				pst.setString(2, SIBLING);
//				log.debug("pst=>" + pst);
				pst.execute();

				for (int i = 0; i < getMemberName().length; i++) {

					pst = con.prepareStatement("INSERT INTO candidate_family_members(member_type, member_name, member_dob, member_education, "
							+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");
					pst.setString(1, SIBLING);
					pst.setString(2, getMemberName()[i]);
					pst.setDate(3, uF.getDateFormat(getMemberDob()[i], DATE_FORMAT));
					pst.setString(4, getMemberEducation()[i]);
					pst.setString(5, getMemberOccupation()[i]);
					pst.setString(6, getMemberContactNumber()[i]);
					pst.setString(7, getMemberEmailId()[i]);
					pst.setString(8, getMemberGender()[i]);
					pst.setInt(9, uF.parseToInt(getCandidateId()));
//					log.debug("pst=>" + pst);
					pst.execute();
					pst.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
						
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	} 
/*
	public void updateEmpEducation(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			
			//===start parvez date: 08-09-2021===
			if (getDegreeName() != null && getDegreeName().length != 0) {
				pst = con.prepareStatement("DELETE FROM candidate_education_details WHERE emp_id = ?");
				pst.setInt(1, uF.parseToInt(getCandidateId()));
				pst.execute();
				
				String[] degreeNameOther = request.getParameterValues("degreeNameOther");
				for (int i = 0; i < getDegreeName().length; i++) {
//					System.out.println("getDegreeName()[i]");
					if (getDegreeName()[i] != null && getDegreeName()[i].equalsIgnoreCase("other")) {
						if(degreeNameOther[i] != null && !degreeNameOther[i].trim().equals("") && !degreeNameOther[i].equalsIgnoreCase("null")) {
							pst = con.prepareStatement("insert into educational_details(education_name,education_details,org_id) " + "VALUES (?,?,?)");
							pst.setString(1, degreeNameOther[i]);
							pst.setString(2, "");
							pst.setInt(3, uF.parseToInt(getOrg_id()));
							pst.execute();
							pst.close();
							
							int newEduid = 0;
							pst = con.prepareStatement("select max(edu_id) as edu_id from educational_details");
							rs = pst.executeQuery();
							while (rs.next()) {
								newEduid = rs.getInt("edu_id");
							}
							rs.close();
							pst.close();
							
							pst = con.prepareStatement("INSERT INTO candidate_education_details(education_id, degree_duration, completion_year, grade, emp_id," +
									"subject,start_date,completion_date,marks,city,institute_name,university_name)" + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
							pst.setInt(1, newEduid);
							pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
							pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
							pst.setString(4, getGrade()[i]);
							pst.setInt(5, uF.parseToInt(getCandidateId()));
							pst.setString(6, getSubject()[i].trim());
							pst.setDate(7, uF.getDateFormat(getStrStartDate()[i], DATE_FORMAT));
							pst.setDate(8, uF.getDateFormat(getStrCompletionDate()[i], DATE_FORMAT));
							pst.setDouble(9, uF.parseToDouble(getMarks()[i].trim()));
							pst.setString(10, getCity1()[i].trim());
							pst.setString(11, getInstituteName()[i].trim());
							pst.setString(12, getUniversityName()[i].trim());
//							System.out.println("AC/3783--pst="+pst);
//							log.debug("pst=>" + pst);
							pst.execute();
							pst.close();
							
							int intDegreeId = 0;
							//===start parvez date: 08-09-2021===
							pst = con.prepareStatement("select max(degree_id) as degree_id from candidate_education_details");
							//===end parvez date: 08-09-2021===
							rs = pst.executeQuery();
							while(rs.next()) {
								intDegreeId = rs.getInt("degree_id");
							}
							rs.close();
							pst.close();
							
							if(uF.parseToInt(getDegreeCertiStatus()[i]) == 1) {
								MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
								File[] strCertificateDoc = mpRequest.getFiles("degreeCertificate"+i);
								String[] strCertificateDocFileName = mpRequest.getFileNames("degreeCertificate"+i);
//								System.out.println("strCertificateDoc ===>> " + strCertificateDoc.length);
								for(int j=0; strCertificateDoc != null && j<strCertificateDoc.length; j++) {
									String strFileName = null;
									if (CF.getStrDocSaveLocation() == null) {
										strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, strCertificateDoc[j], strCertificateDocFileName[j], strCertificateDocFileName[j], CF);
									} else {
										strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_CANDIDATE + "/" + I_DOCUMENT + "/" + I_EDUCATION_DOC + "/"+ getCandidateId(), strCertificateDoc[j], strCertificateDocFileName[j], strCertificateDocFileName[j], CF);
									}
									pst = con.prepareStatement("INSERT INTO candidate_degree_certificate_details(degree_id, emp_id, degree_certificate_name) VALUES (?,?,?)");
									pst.setInt(1, intDegreeId);
									pst.setInt(2, uF.parseToInt(getCandidateId()));
									pst.setString(3, strFileName);
									pst.executeUpdate();
									pst.close();
								}
							}
						}
					} else {
						if(getDegreeName()[i] != null && !getDegreeName()[i].trim().equals("") && !getDegreeName()[i].equalsIgnoreCase("null")) {
							pst = con.prepareStatement("INSERT INTO candidate_education_details(education_id, degree_duration, completion_year, grade, emp_id," +
									"subject,start_date,completion_date,marks,city,institute_name,university_name)" + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
							pst.setInt(1, uF.parseToInt(getDegreeName()[i].trim()));
							pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
							pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
							pst.setString(4, getGrade()[i]);
							pst.setInt(5, uF.parseToInt(getCandidateId()));
							pst.setString(6, getSubject()[i].trim());
							pst.setDate(7, uF.getDateFormat(getStrStartDate()[i], DATE_FORMAT));
							pst.setDate(8, uF.getDateFormat(getStrCompletionDate()[i], DATE_FORMAT));
							pst.setDouble(9, uF.parseToDouble(getMarks()[i].trim()));
							pst.setString(10, getCity1()[i].trim());
							pst.setString(11, getInstituteName()[i].trim());
							pst.setString(12, getUniversityName()[i].trim());
							
//							System.out.println("AC/3837--pst ===>> " + pst);
							pst.execute();
							pst.close();
							
							int intDegreeId = 0;
							//===start parvez date: 08-09-2021===
							pst = con.prepareStatement("select max(degree_id) as degree_id from candidate_education_details");
							//===end parvez date: 08-09-2021===
							rs = pst.executeQuery();
							while(rs.next()) {
								intDegreeId = rs.getInt("degree_id");
							}
							rs.close();
							pst.close();
							
							if(uF.parseToInt(getDegreeCertiStatus()[i]) == 1) {
								MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
								File[] strCertificateDoc = mpRequest.getFiles("degreeCertificate"+i);
								String[] strCertificateDocFileName = mpRequest.getFileNames("degreeCertificate"+i);
//								System.out.println("AC/3856--strCertificateDoc ===>> " + strCertificateDoc.length);
								for(int j=0; strCertificateDoc != null && j<strCertificateDoc.length; j++) {
									String strFileName = null;
									if (CF.getStrDocSaveLocation() == null) {
										strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, strCertificateDoc[j], strCertificateDocFileName[j], strCertificateDocFileName[j], CF);
									} else {
										strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_CANDIDATE + "/" + I_DOCUMENT + "/" + I_EDUCATION_DOC + "/"+ getCandidateId(), strCertificateDoc[j], strCertificateDocFileName[j], strCertificateDocFileName[j], CF);
									}
									pst = con.prepareStatement("INSERT INTO candidate_degree_certificate_details(degree_id, emp_id, degree_certificate_name) VALUES (?,?,?)");
									pst.setInt(1, intDegreeId);
									pst.setInt(2, uF.parseToInt(getCandidateId()));
									pst.setString(3, strFileName);
//									System.out.println("AC/3868--pst="+pst);
									pst.executeUpdate();
									pst.close();
								}
							}
						}
					}
				}
			}
			//===end parvez date: 08-09-2021=== 
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
	}
	*/
	
	//===start parvez date: 14-09-2021===
	public void updateEmpEducation(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			if (getDegreeName() != null && getDegreeName().length != 0) {

				pst = con.prepareStatement("select degree_id from candidate_education_details where emp_id=?");
				pst.setInt(1, uF.parseToInt(getCandidateId()));
				rs = pst.executeQuery();
				List<String> alEmpDegreeId = new ArrayList<String>();
				while(rs.next()) {
					alEmpDegreeId.add(rs.getString("degree_id"));
				}
				rs.close();
				pst.close();
				
				String[] degreeNameOther = request.getParameterValues("degreeNameOther");
				MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
				for (int i = 0; i < getDegreeName().length; i++) {
					if (getDegreeName()[i].length() != 0) {
						
						if (getDegreeName()[i].equalsIgnoreCase("other")) {
							
							pst = con.prepareStatement("insert into educational_details(education_name,education_details,org_id) VALUES (?,?,?)");
							pst.setString(1, degreeNameOther[i]);
							pst.setString(2, "");
							pst.setInt(3, uF.parseToInt(getOrg_id()));
							pst.execute();
							pst.close();

							int newEduid = 0;
							pst = con.prepareStatement("select max(edu_id) as edu_id from educational_details");
							rs = pst.executeQuery();
							while (rs.next()) {
								newEduid = rs.getInt("edu_id");
							}
							rs.close();
							pst.close();

							pst = con.prepareStatement("INSERT INTO candidate_education_details(education_id, degree_duration, completion_year, grade, emp_id," +
									"subject,start_date,completion_date,marks,city,institute_name,university_name)" + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
							pst.setInt(1, newEduid);
							pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
							pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
							pst.setString(4, getGrade()[i]);
							pst.setInt(5, uF.parseToInt(getCandidateId()));
							pst.setString(6, getSubject()[i].trim());
							pst.setDate(7, uF.getDateFormat(getStrStartDate()[i], DATE_FORMAT));
							pst.setDate(8, uF.getDateFormat(getStrCompletionDate()[i], DATE_FORMAT));
							pst.setDouble(9, uF.parseToDouble(getMarks()[i].trim()));
							pst.setString(10, getCity1()[i].trim());
							pst.setString(11, getInstituteName()[i].trim());
							pst.setString(12, getUniversityName()[i].trim());
//							System.out.println("AC/3783--pst="+pst);
//							log.debug("pst=>" + pst);
							pst.execute();
							pst.close();
							
							int intDegreeId = 0;
							pst = con.prepareStatement("select max(degree_id) as degree_id from candidate_education_details");
							rs = pst.executeQuery();
							while(rs.next()) {
								intDegreeId = rs.getInt("degree_id");
							}
							rs.close();
							pst.close();
						
							
//							System.out.println("getDegreeCertiStatus()[i] ===>> " + getDegreeCertiStatus()[i]);
							if(uF.parseToInt(getDegreeCertiStatus()[i]) == 1) {
								File[] strCertificateDoc = mpRequest.getFiles("degreeCertificate"+i);
								String[] strCertificateDocFileName = mpRequest.getFileNames("degreeCertificate"+i);
//								System.out.println("strCertificateDoc ===>> " + strCertificateDoc.length);
								for(int j=0; strCertificateDoc != null && j<strCertificateDoc.length; j++) {
									String strFileName = null;
									if (CF.getStrDocSaveLocation() == null) {
										strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, strCertificateDoc[j], strCertificateDocFileName[j], strCertificateDocFileName[j], CF);
									} else {
										strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_CANDIDATE + "/" + I_DOCUMENT + "/" + I_EDUCATION_DOC + "/"+ getCandidateId(), strCertificateDoc[j], strCertificateDocFileName[j], strCertificateDocFileName[j], CF);
									}
									pst = con.prepareStatement("INSERT INTO candidate_degree_certificate_details(degree_id, emp_id, degree_certificate_name) VALUES (?,?,?)");
									pst.setInt(1, intDegreeId);
									pst.setInt(2, uF.parseToInt(getCandidateId()));
									pst.setString(3, strFileName);
									pst.executeUpdate();
									pst.close();
								}
							}
						
							
						} else {
							
//							System.out.println("alEmpDegreeId ===>> " + alEmpDegreeId);
							
							if(getDegreeId() != null && i<getDegreeId().length && uF.parseToInt(getDegreeId()[i])!=0) {
								StringBuilder sbQuery = new StringBuilder();
								sbQuery.append("update candidate_education_details set education_id=?,degree_duration=?,completion_year=?,grade=?,subject=?,start_date=?,completion_date=?,marks=?,city=?,institute_name=?,university_name=?");
								sbQuery.append(" where degree_id=?");
								pst = con.prepareStatement(sbQuery.toString());
								pst.setInt(1, uF.parseToInt(getDegreeName()[i]));
								pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
								pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
								pst.setString(4, getGrade()[i]);
								
								pst.setString(5, getSubject()[i].trim());
								pst.setDate(6, uF.getDateFormat(getStrStartDate()[i], DATE_FORMAT));
								pst.setDate(7, uF.getDateFormat(getStrCompletionDate()[i], DATE_FORMAT));
								pst.setDouble(8, uF.parseToDouble(getMarks()[i].trim()));
								pst.setString(9, getCity1()[i].trim());
								pst.setString(10, getInstituteName()[i].trim());
								pst.setString(11, getUniversityName()[i].trim());

								pst.setInt(12, uF.parseToInt(getDegreeId()[i]));
								pst.executeUpdate();
								pst.close();
								
								alEmpDegreeId.remove(getDegreeId()[i]);
//								System.out.println("alEmpDegreeId ===>> " + alEmpDegreeId +" -- getDegreeId()[i] ===>> " + getDegreeId()[i]);
//								System.out.println("getDegreeCertiStatus()[i] ===>> " + getDegreeCertiStatus()[i]);
								if(uF.parseToInt(getDegreeCertiStatus()[i]) == 1) {
									File[] strCertificateDoc = mpRequest.getFiles("degreeCertificate"+i);
									String[] strCertificateDocFileName = mpRequest.getFileNames("degreeCertificate"+i);
//									System.out.println("strCertificateDoc ===>> " + strCertificateDoc.length);
									for(int j=0; strCertificateDoc != null && j<strCertificateDoc.length; j++) {
//										System.out.println("strCertificateDocFileName[j] ===>> " + strCertificateDocFileName[j]);
										String strFileName = null;
										if (CF.getStrDocSaveLocation() == null) {
											strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, strCertificateDoc[j], strCertificateDocFileName[j], strCertificateDocFileName[j], CF);
										} else {
											strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_CANDIDATE + "/" + I_DOCUMENT + "/" + I_EDUCATION_DOC + "/"+ getCandidateId(), strCertificateDoc[j], strCertificateDocFileName[j], strCertificateDocFileName[j], CF);
										}
										pst = con.prepareStatement("INSERT INTO candidate_degree_certificate_details(degree_id, emp_id, degree_certificate_name) VALUES (?,?,?)");
										pst.setInt(1, uF.parseToInt(getDegreeId()[i]));
										pst.setInt(2, uF.parseToInt(getCandidateId()));
										pst.setString(3, strFileName);
										pst.executeUpdate();
										pst.close();
									}
									
									pst = con.prepareStatement("delete from candidate_degree_certificate_details where degree_id=? and emp_id=?");
									pst.setInt(1, uF.parseToInt(getDegreeId()[i]));
									pst.setInt(2, uF.parseToInt(getCandidateId()));
									pst.executeUpdate();
									pst.close();
								}
								
							} else {
								pst = con.prepareStatement("INSERT INTO candidate_education_details(education_id, degree_duration, completion_year, grade, emp_id," +
										"subject,start_date,completion_date,marks,city,institute_name,university_name)" + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
								pst.setInt(1, uF.parseToInt(getDegreeName()[i].trim()));
								pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
								pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
								pst.setString(4, getGrade()[i]);
								pst.setInt(5, uF.parseToInt(getCandidateId()));
								pst.setString(6, getSubject()[i].trim());
								pst.setDate(7, uF.getDateFormat(getStrStartDate()[i], DATE_FORMAT));
								pst.setDate(8, uF.getDateFormat(getStrCompletionDate()[i], DATE_FORMAT));
								pst.setDouble(9, uF.parseToDouble(getMarks()[i].trim()));
								pst.setString(10, getCity1()[i].trim());
								pst.setString(11, getInstituteName()[i].trim());
								pst.setString(12, getUniversityName()[i].trim());
								int x = pst.executeUpdate();
								pst.close();
								
								int intDegreeId = 0;
								pst = con.prepareStatement("select max(degree_id) as degree_id from candidate_education_details");
								rs = pst.executeQuery();
								while(rs.next()) {
									intDegreeId = rs.getInt("degree_id");
								}
								rs.close();
								pst.close();
								
//								System.out.println("getDegreeCertiStatus().length ===>> " + getDegreeCertiStatus().length);
//								System.out.println("getDegreeCertiStatus()[i] ===>> " + getDegreeCertiStatus()[i]);
								if(uF.parseToInt(getDegreeCertiStatus()[i]) == 1) {
									File[] strCertificateDoc = mpRequest.getFiles("degreeCertificate"+i);
									String[] strCertificateDocFileName = mpRequest.getFileNames("degreeCertificate"+i);
//									System.out.println("strCertificateDoc ===>> " + strCertificateDoc.length);
									for(int j=0; strCertificateDoc != null && j<strCertificateDoc.length; j++) {
										String strFileName = null;
										if (CF.getStrDocSaveLocation() == null) {
											strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, strCertificateDoc[j], strCertificateDocFileName[j], strCertificateDocFileName[j], CF);
										} else {
											strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_CANDIDATE + "/" + I_DOCUMENT + "/" + I_EDUCATION_DOC + "/"+ getCandidateId(), strCertificateDoc[j], strCertificateDocFileName[j], strCertificateDocFileName[j], CF);
										}
										pst = con.prepareStatement("INSERT INTO candidate_degree_certificate_details(degree_id, emp_id, degree_certificate_name) VALUES (?,?,?)");
										pst.setInt(1, intDegreeId);
										pst.setInt(2, uF.parseToInt(getCandidateId()));
										pst.setString(3, strFileName);
										pst.executeUpdate();
										pst.close();
									}
								}
							
							}
							
							
							for(int j=0; alEmpDegreeId!=null && j<alEmpDegreeId.size(); j++) {
								pst = con.prepareStatement("delete from candidate_education_details where degree_id =? and emp_id=?");
								pst.setInt(1, uF.parseToInt(alEmpDegreeId.get(j)));
								pst.setInt(2, uF.parseToInt(getCandidateId()));
								pst.executeUpdate();
								pst.close();
								
								pst = con.prepareStatement("delete from candidate_degree_certificate_details where degree_id =? and emp_id=?");
								pst.setInt(1, uF.parseToInt(alEmpDegreeId.get(j)));
								pst.setInt(2, uF.parseToInt(getCandidateId()));
								pst.executeUpdate();
								pst.close();
							}
						}

					}

				}
				
				for(int j=0; alEmpDegreeId!=null && j<alEmpDegreeId.size(); j++) {
					pst = con.prepareStatement("delete from candidate_education_details where degree_id =? and emp_id=?");
					pst.setInt(1, uF.parseToInt(alEmpDegreeId.get(j)));
					pst.setInt(2, uF.parseToInt(getCandidateId()));
					pst.executeUpdate();
					pst.close();
					
					pst = con.prepareStatement("delete from candidate_degree_certificate_details where degree_id =? and emp_id=?");
					pst.setInt(1, uF.parseToInt(alEmpDegreeId.get(j)));
					pst.setInt(2, uF.parseToInt(getCandidateId()));
					pst.executeUpdate();
					pst.close();
				}

			}

		} catch (Exception e) {
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

	}
	//===end parvez date: 14-09-2021===

	public void updateEmpLangues(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		try {
			if(getLanguageName()!=null && getLanguageName().length!=0) {
				pst = con.prepareStatement("DELETE FROM candidate_languages_details WHERE emp_id = ?");
				pst.setInt(1, uF.parseToInt(getCandidateId()));
				pst.execute();
				pst.close();
				
				if(getIsRead()!=null)
				if(getIsWrite()!=null)
				if(getIsSpeak()!=null)
				
				for(int i=0; getLanguageName()!=null && i<getLanguageName().length; i++) {
					if(getLanguageName()[i] != null && getLanguageName()[i].length()!=0 && !getLanguageName()[i].trim().equals("") && !getLanguageName()[i].equalsIgnoreCase("null")) {
						pst = con.prepareStatement("INSERT INTO candidate_languages_details(language_name, language_read, language_write, language_speak, emp_id)" +
						"VALUES (?,?,?,?,?)");
						pst.setString(1, getLanguageName()[i]);
						pst.setInt(2, uF.parseToInt(getIsRead()[i]));
						pst.setInt(3, uF.parseToInt(getIsWrite()[i]));
						pst.setInt(4, uF.parseToInt(getIsSpeak()[i]));
						pst.setInt(5, uF.parseToInt(getCandidateId()));
//						log.debug("pst=>"+pst);
						pst.execute();
						pst.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	
	/*public void updateDocuments(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		try {
			int i=0;
			pst = con.prepareStatement("DELETE FROM candidate_documents_details WHERE emp_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.execute();
			pst.close();
			if(idDoc!=null) {
				int fileSize=1;
				for (i=0; getIdDocName()!=null && i<getIdDocName().length; i++) {
					File file1=null;
					String filename=null;
					if(getIdDoc()!=null && getIdDoc().size()>=fileSize && getIdDocStatus().get(i).equals("1")){
						 file1=getIdDoc().get(fileSize-1);
						 filename=getIdDocFileName().get(fileSize-1);
						  filename = uF.uploadFile(request, DOCUMENT_LOCATION, file1, filename);
						 fileSize++;
					}
						
					if(file1!=null){
			            pst = con.prepareStatement("INSERT INTO candidate_documents_details (documents_name, documents_type, emp_id, " +
			            		"documents_file_name,entry_date) values (?,?,?,?,?)");
			            pst.setString(1, getIdDocName()[i]);
			            pst.setString(2, getIdDocType()[i]);
			            pst.setInt(3, uF.parseToInt(getCandidateId()));
			            pst.setString(4, filename);
//			            pst.setInt(5, uF.parseToInt(strSessionEmpId));
			            pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			            pst.execute();
						pst.close();
					}
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}*/
	
	
	public void updateHobbies(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;

		try {
			pst = con.prepareStatement("DELETE from candidate_hobbies_details where emp_id =?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.execute();
			pst.close();
			for (String h : hobbyName) {
				if (h != null && h.length() != 0 && !h.trim().equals("") && !h.equalsIgnoreCase("null")) {
					pst = con.prepareStatement("INSERT INTO candidate_hobbies_details (hobbies_name, emp_id) VALUES (?,?)");
					pst.setString(1, h);
					pst.setInt(2, uF.parseToInt(getCandidateId()));
					pst.execute();
					pst.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
						
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	public void updateSkills(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
//		System.out.println("in update skills");
//		System.out.println("getSkillName::"+getSkillName());
		try {

			pst = con.prepareStatement("DELETE from candidate_skills_description where emp_id =?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.execute();
			pst.close();

			for (int i = 0; getSkillName() != null && i < getSkillName().length; i++) {
				
				if (getSkillName()[i] != null && getSkillName()[i].length() != 0 && getSkillValue()[i].length() != 0 && !getSkillName()[i].trim().equals("") && !getSkillName()[i].equalsIgnoreCase("null")) {
					//===start parvez on 05-08-2021====
					pst = con.prepareStatement("INSERT INTO candidate_skills_description (skill_id, skills_value, emp_id) VALUES (?,?,?)");
					//===end parvez on 05-08-2021===
					pst.setInt(1, uF.parseToInt(getSkillName()[i]));
					pst.setString(2, getSkillValue()[i]);
					pst.setInt(3, uF.parseToInt(getCandidateId()));
					pst.execute();
					pst.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
						
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	public void insertEmployee() {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
//			System.out.println("getStep() ===>> " + getStep());
//			System.out.println("getCandidateId() ===>> " + getCandidateId());
			if (uF.parseToInt(getStep()) == 1) {

				if (uF.parseToInt(getCandidateId()) == 0) {
					insertCandidatePersonalDetails(con, uF, CF);
					// insertUser(con, uF,uF.parseToInt(getCandidateId()));
					uploadImage(uF.parseToInt(getCandidateId()));
				} else {

					updateEmpPersonalDetails(con, uF);
					uploadImage(uF.parseToInt(getCandidateId()));
				}

//				session.setAttribute("EMPNAME_P", getEmpFname() + " " + getEmpLname());
//				session.setAttribute("EMPID_P", uF.parseToInt(getCandidateId()) + "");

			} else if (uF.parseToInt(getStep()) == 2) {

				insertSkills(con, uF);
				insertHobbies(con, uF);
				insertEmpLangues(con, uF);
				insertEmpEducation(con, uF);

			} else if (uF.parseToInt(getStep()) == 3) {

				insertEmpFamilyMembers(con, uF);

			} else if (uF.parseToInt(getStep()) == 4) {

				insertEmpPrevEmploment(con, uF);

			} else if (uF.parseToInt(getStep()) == 5) {

				insertEmpReferences(con, uF);

			} else if (uF.parseToInt(getStep()) == 6) {

				insertEmpMedicalInfo(con, uF);

			} else if (uF.parseToInt(getStep()) == 7) {

				updateSupportingDocuments(con, uF);

			} else if (uF.parseToInt(getStep()) == 8) {

				System.out.println("uF.parseToInt(getStep()) x==x 8");
				insertAvailability(uF, getCandidateId());
				sendMail(getCandidateId());// Created by Dattatray Date:12-08-21
				/*
				 * StringBuilder sbServices = new StringBuilder();
				 * 
				 * Map<String, String> hmServices = CF.getServicesMap(false);
				 * 
				 * List<List<String>> alServices = new
				 * ArrayList<List<String>>();
				 *//**
				 * This code is comment to fixed one salary input fields. If
				 * required, different salary structure for different services,
				 * then remove this code.
				 */
				/*
				 * 
				 * for (int i = 0; getService() != null && i <
				 * getService().length; i++) {
				 * 
				 * if (uF.parseToInt(getService()[i]) > 0) { List<String>
				 * empServicesList = new ArrayList<String>();
				 * sbServices.append(getService()[i] + ","); //
				 * empServicesList.add(getService()[i]); //
				 * empServicesList.add(hmServices.get(getService()[i])); //
				 * alServices.add(empServicesList); } }
				 * 
				 * log.debug("Step 8 => alServices==>"+alServices);
				 * 
				 * session.setAttribute("alServices", alServices);
				 * updateEmpCodeBankInfo(con,uF); updateEmpJoiningDate(con,uF);
				 * insertEmpOfficialDetails(con, uF, sbServices);
				 * insertProbationPeriod(con, uF);
				 * 
				 * 
				 * setServiceId(getService()[0]);
				 * setServiceName(hmServices.get(getService()[0]));
				 */

			}/*
			 * else if(uF.parseToInt( getStep())==9) {
			 * 
			 * int serviceCount = 0 ;
			 * 
			 * if(session.getAttribute("alServices")!=null) {
			 * 
			 * serviceCount = ((List)session.getAttribute("alServices")).size();
			 * 
			 * log.debug("Step 9 => serviceCount===>"+serviceCount);
			 * 
			 * if(serviceCount!=0) {
			 * 
			 * List<List<String>> alServices = (List<List<String>>)
			 * session.getAttribute("alServices");
			 * 
			 * List<String> empServicesList = alServices.get(serviceCount - 1 );
			 * 
			 * log.debug("Step 9 => empServicesList====>"+empServicesList);
			 * 
			 * setServiceId(empServicesList.get(0));
			 * setServiceName(empServicesList.get(1));
			 * 
			 * alServices.remove(serviceCount-1);
			 * 
			 * }else { session.removeAttribute("alServices");
			 * log.debug("Step 9 => inserted Salary For all services!!");
			 * updateEmpFilledStatus(uF.parseToInt(getCandidateId())); //
			 * approveEmployee(); // setProbationPolicy(con, uF); setStep("10");
			 * }
			 * 
			 * } }
			 */

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			
			db.closeConnection(con);
		}
	}
	/**
	 * Created By Dattatray
	 * @since 12-08-21
	 * @param candidateId
	 */
	public void sendMail(String candidateId) {
		System.out.println("candidateId : "+candidateId);
		Connection con = null;
		ResultSet rst = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
				con = db.makeConnection(con);
				Map<String, Map<String, String>> hmCandiInfo = getCandiInfoMap(con, false);
				Map<String, String> hmCandiInner = hmCandiInfo.get(getCandidateId());
				
				String strDomain = request.getServerName().split("\\.")[0];
				CandidateNotifications nF = new CandidateNotifications(N_FRESHER_JOB_SUBMISSION, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrEmpId(candidateId);
				nF.setStrRecruitmentId(getRecruitId());
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				 
				nF.setStrCandiFname(hmCandiInner !=null? hmCandiInner.get("FNAME"):"");
				nF.setStrCandiLname(hmCandiInner !=null? hmCandiInner.get("LNAME"):"");
				nF.sendNotifications();
			 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	/**
	 * Created By Dattatray
	 * @since 12-08-21
	 * @param con
	 * @param isFamilyInfo
	 * @return
	 */
	public Map<String, Map<String, String>> getCandiInfoMap(Connection con, boolean isFamilyInfo) {
		Map<String, Map<String, String>> hmCandiInfo = new HashMap<String, Map<String, String>>();
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			Map<String, String> hmCandiInner = new HashMap<String, String>();
			if(isFamilyInfo) {
				pst = con.prepareStatement("select * from candidate_family_members order by emp_id");
				rs = pst.executeQuery();
				while(rs.next()) {
					hmCandiInner = hmCandiInfo.get(rs.getString("emp_id"));
					if(hmCandiInner==null)hmCandiInner=new HashMap<String, String>();
					hmCandiInner.put(rs.getString("member_type"), rs.getString("member_name"));
					hmCandiInfo.put(rs.getString("emp_id"), hmCandiInner);
				}
				rs.close();
				pst.close();
			}
			
			pst = con.prepareStatement("SELECT cpd.emp_per_id, cpd.emp_fname, cpd.emp_lname, cpd.empcode, cpd.emp_image, cpd.emp_email,cpd.emp_date_of_birth,  cpd.emp_gender, cpd.marital_status FROM candidate_personal_details cpd order by emp_per_id");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				if (rs.getInt("emp_per_id") < 0) {
					continue;
				}
				hmCandiInner = hmCandiInfo.get(rs.getString("emp_per_id"));
				if(hmCandiInner==null)hmCandiInner=new HashMap<String, String>();

				hmCandiInner.put("FNAME", rs.getString("emp_fname"));
				hmCandiInner.put("LNAME", rs.getString("emp_lname"));
				hmCandiInner.put("FULLNAME", rs.getString("emp_lname")+" "+rs.getString("emp_lname"));
				
				hmCandiInfo.put(rs.getString("emp_per_id"), hmCandiInner);
			}
			rs.close();
			pst.close();
			
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
		return hmCandiInfo;
	}
	private void insertEmpReferences(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;

		try {

			pst = con.prepareStatement("INSERT INTO candidate_references (ref_name, ref_company, ref_designation, ref_contact_no, ref_email_id, emp_id) " + "values(?,?,?,?,?,?)");
			pst.setString(1, getRef1Name());
			pst.setString(2, getRef1Company());
			pst.setString(3, getRef1Designation());
			pst.setString(4, getRef1ContactNo());
			pst.setString(5, getRef1Email());
			pst.setInt(6, uF.parseToInt(getCandidateId()));
//			log.debug("pst==>" + pst);
			pst.execute();
			pst.close();

			pst = con.prepareStatement("INSERT INTO candidate_references (ref_name, ref_company, ref_designation, ref_contact_no, ref_email_id, emp_id) " + "values(?,?,?,?,?,?)");
			pst.setString(1, getRef2Name());
			pst.setString(2, getRef2Company());
			pst.setString(3, getRef2Designation());
			pst.setString(4, getRef2ContactNo());
			pst.setString(5, getRef2Email());
			pst.setInt(6, uF.parseToInt(getCandidateId()));
//			log.debug("pst==>" + pst);
			pst.execute();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
						
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	/*private void insertEmpMedicalInfo(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;

		try {

			pst = con.prepareStatement("INSERT INTO candidate_medical_details (question_id, emp_id, yes_no, description) values (?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getQue1Id()));
			pst.setInt(2, uF.parseToInt(getCandidateId()));
			pst.setBoolean(3, isCheckQue1());
			pst.setString(4, getQue1Desc());

			log.debug("pst ==>" + pst);
			pst.execute();

			pst = con.prepareStatement("INSERT INTO candidate_medical_details (question_id, emp_id, yes_no, description) values (?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getQue2Id()));
			pst.setInt(2, uF.parseToInt(getCandidateId()));
			pst.setBoolean(3, isCheckQue2());
			pst.setString(4, getQue2Desc());

			log.debug("pst ==>" + pst);
			pst.execute();

			pst = con.prepareStatement("INSERT INTO candidate_medical_details (question_id, emp_id, yes_no, description) values (?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getQue3Id()));
			pst.setInt(2, uF.parseToInt(getCandidateId()));
			pst.setBoolean(3, isCheckQue3());
			pst.setString(4, getQue3Desc());

			log.debug("pst ==>" + pst);
			pst.execute();

			pst = con.prepareStatement("INSERT INTO candidate_medical_details (question_id, emp_id, yes_no, description) values (?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getQue4Id()));
			pst.setInt(2, uF.parseToInt(getCandidateId()));
			pst.setBoolean(3, isCheckQue4());
			pst.setString(4, getQue4Desc());

			log.debug("pst ==>" + pst);
			pst.execute();

			pst = con.prepareStatement("INSERT INTO candidate_medical_details (question_id, emp_id, yes_no, description) values (?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getQue5Id()));
			pst.setInt(2, uF.parseToInt(getCandidateId()));
			pst.setBoolean(3, isCheckQue5());
			pst.setString(4, getQue5Desc());

			log.debug("pst ==>" + pst);
			pst.execute();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/


	private void updateEmpMedicalInfo1(Connection con, UtilityFunctions uF,String medicalid, boolean checkQue, String queDesc, File file,String fileName1) {
		PreparedStatement pst = null;

		try {
			String fileName = null;
			if (file != null) {
				fileName = uF.uploadFile(request, DOCUMENT_LOCATION, file,fileName1);
				pst = con.prepareStatement("UPDATE candidate_medical_details SET yes_no=?, description=?,filepath=? WHERE medical_id = ?");
				pst.setBoolean(1, checkQue);
				pst.setString(2, queDesc);
				pst.setString(3, fileName);
				pst.setInt(4, uF.parseToInt(medicalid));
			} else {
				pst = con.prepareStatement("UPDATE candidate_medical_details SET yes_no=?, description=? WHERE medical_id = ?");
				pst.setBoolean(1, checkQue);
				pst.setString(2, queDesc);
				pst.setInt(3, uF.parseToInt(medicalid));
			}
//			log.debug("pst ==>" + pst);
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
						
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	
	private void insertEmpMedicalInfo1(Connection con, UtilityFunctions uF,String queId,boolean checkQue,String queDesc,File file,String fileName1) {
		PreparedStatement pst = null;
		
		try {
			
			String fileName =null;
			if(file!=null){
			 fileName = uF.uploadFile(request, DOCUMENT_LOCATION, file, fileName1);
			 }
//			System.out.println();
			pst = con.prepareStatement("INSERT INTO candidate_medical_details (question_id, emp_id, yes_no, description,filepath) values(?,?,?,?,?)");
			pst.setInt(1, uF.parseToInt(queId));
			pst.setInt(2, uF.parseToInt(getCandidateId()));
			pst.setBoolean(3, checkQue);
			pst.setString(4, queDesc);
			pst.setString(5, fileName);
//					log.debug("pst ==>"+pst);
			pst.execute();
			pst.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
						
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
private void insertEmpMedicalInfo(Connection con, UtilityFunctions uF) {
		
//		PreparedStatement pst = null;
		File file1=null;
		String filename=null;
		int fileSize=1;
		
//		System.out.println("getQue1DescFile() ====> "+getQue1DescFile());
//		System.out.println("getQue1IdFileStatus().get(0) ====> "+getQue1IdFileStatus().get(0));
		if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(0).equals("1")){
			 file1=getQue1DescFile().get(fileSize-1);
			 filename=getQue1DescFileFileName().get(fileSize-1);
//			 System.out.println("file1 ====> "+file1);
//			 System.out.println("filename ====> "+filename);
			 fileSize++;
		insertEmpMedicalInfo1(con,uF,getQue1Id(),isCheckQue1(),getQue1Desc(),file1,filename);
		}
		
		file1=null;
		 filename=null;
		if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(1).equals("1")){
			 file1=getQue1DescFile().get(fileSize-1);
			 filename=getQue1DescFileFileName().get(fileSize-1);
			 fileSize++;
		insertEmpMedicalInfo1(con,uF,getQue2Id(),isCheckQue2(),getQue2Desc(),file1,filename);
		}
		
		file1=null;
		 filename=null;
		if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(2).equals("1")){
			 file1=getQue1DescFile().get(fileSize-1);
			 filename=getQue1DescFileFileName().get(fileSize-1);
			 fileSize++;
		insertEmpMedicalInfo1(con,uF,getQue3Id(),isCheckQue3(),getQue3Desc(),file1,filename);
		}
	}
	
	
	
	private void sendNotifications() {

		// Notifications nF = new Notifications(N_NEW_EMPLOYEE);
		// nF.setStrEmailTo(getEmpEmail());
		// nF.setStrEmpMobileNo(getEmpContactno());
		// nF.setStrEmpCode(getEmpCode());
		// nF.setStrEmpFname(getEmpFname());
		// nF.setStrEmpLname(getEmpLname());
		// nF.setStrUserName(getUserName());
		// nF.setStrPassword(getEmpPassword());
		// nF.sendNotifications();

	}

	private void uploadImage(int empId2) {

		try {

			UploadImage uI = new UploadImage();
			uI.setServletRequest(request);
			uI.setImageType("CANDIDATE_IMAGE");
			uI.setEmpImage(getEmpImage());
			uI.setEmpImageFileName(getEmpImageFileName());
			uI.setEmpId(empId2 + "");
			uI.setCF(CF);
//			System.out.println("empId2 ===> "+empId2+" getEmpImage() ===> "+getEmpImage());
			uI.upoadImage();

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public void insertEmpPrevEmploment(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		try {
			/*if (getPrevCompanyName() != null && getPrevCompanyName().length != 0) {
				for (int i = 0; i < getPrevCompanyName().length; i++) {
					if (getPrevCompanyName()[i].length() != 0) {
						pst = con.prepareStatement("INSERT INTO candidate_prev_employment(company_name, company_location, company_city, company_state, "
							+ "company_country, company_contact_no, reporting_to, from_date, to_date, designation, responsibilities, skills, emp_id," +
							"report_manager_ph_no, hr_manager, hr_manager_ph_no)"
							+ "VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
						pst.setString(1, getPrevCompanyName()[i]);
						pst.setString(2, getPrevCompanyLocation()[i]);
						pst.setString(3, getPrevCompanyCity()[i]);
						pst.setString(4, getPrevCompanyState()[i]);
						pst.setString(5, getPrevCompanyCountry()[i]);
						pst.setString(6, getPrevCompanyContactNo()[i]);
						pst.setString(7, getPrevCompanyReportingTo()[i]);
						pst.setDate(8, uF.getDateFormat(getPrevCompanyFromDate()[i], DATE_FORMAT));
						pst.setDate(9, uF.getDateFormat(getPrevCompanyToDate()[i], DATE_FORMAT));
						pst.setString(10, getPrevCompanyDesination()[i]);
						pst.setString(11, getPrevCompanyResponsibilities()[i]);
						pst.setString(12, getPrevCompanySkills()[i]);
						pst.setInt(13, uF.parseToInt(getCandidateId()));
						pst.setString(14, getPrevCompanyReportManagerPhNo()[i]);
						pst.setString(15, getPrevCompanyHRManager()[i]);
						pst.setString(16, getPrevCompanyHRManagerPhNo()[i]);
//						log.debug("pst=>" + pst);
						pst.execute();
						pst.close();
					}
				}
			}*/
			
			//===start parvez date: 08-09-2021===
			if (getPrevCompanyName() != null && getPrevCompanyName().length != 0) {
//				System.out.println("ACBC/3322--getPrevCompanyName()="+getPrevCompanyName());
				for (int i = 0; i < getPrevCompanyName().length; i++) {
					if (getPrevCompanyName()[i].length() != 0) {
//						System.out.println("ACBC/3325--getExpLetterFileStatus()[i]="+getExpLetterFileStatus()[i]);
						if(uF.parseToInt(getExpLetterFileStatus()[i]) == 1) {
							MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
							File[] strExpLetterDoc = mpRequest.getFiles("prevCompanyExpLetter"+i);
							String[] strExpLetterDocFileName = mpRequest.getFileNames("prevCompanyExpLetter"+i);
//							System.out.println("ACBC/3330--strExpLetterDoc="+strExpLetterDoc[0]);
//							System.out.println("ACBC/3331--strExpLetterDocFileName="+strExpLetterDocFileName[0]);
							for(int j=0; strExpLetterDoc != null && j<strExpLetterDoc.length; j++) {
								String strFileName = null;
								if (CF.getStrDocSaveLocation() == null) {
									strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, strExpLetterDoc[j], strExpLetterDocFileName[j], strExpLetterDocFileName[j], CF);
								} else {
									strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_CANDIDATE + "/" + I_DOCUMENT + "/" + I_PREVIOUS_EMPLOYMENT_DOC + "/"+ getCandidateId(), strExpLetterDoc[j], strExpLetterDocFileName[j], strExpLetterDocFileName[j], CF);
								}
//								System.out.println("ACBC/3339--strFileName="+strFileName);
								
							//===start parvez date: 08-08-2022===	
								pst = con.prepareStatement("INSERT INTO candidate_prev_employment(company_name, company_location, company_city, company_state, "
										+ "company_country, company_contact_no, reporting_to, from_date, to_date, designation, responsibilities, skills, emp_id," +
										"report_manager_ph_no, hr_manager, hr_manager_ph_no,experience_letter, emp_esic_no, uan_no)"
										+ "VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
							//===end parvez date: 08-08-2022===	
								pst.setString(1, getPrevCompanyName()[i]);
								pst.setString(2, getPrevCompanyLocation()[i]);
								pst.setString(3, getPrevCompanyCity()[i]);
								pst.setString(4, getPrevCompanyState()[i]);
								pst.setString(5, getPrevCompanyCountry()[i]);
								pst.setString(6, getPrevCompanyContactNo()[i]);
								pst.setString(7, getPrevCompanyReportingTo()[i]);
								pst.setDate(8, uF.getDateFormat(getPrevCompanyFromDate()[i], DATE_FORMAT));
								pst.setDate(9, uF.getDateFormat(getPrevCompanyToDate()[i], DATE_FORMAT));
								pst.setString(10, getPrevCompanyDesination()[i]);
								pst.setString(11, getPrevCompanyResponsibilities()[i]);
								pst.setString(12, getPrevCompanySkills()[i]);
								pst.setInt(13, uF.parseToInt(getCandidateId()));
								pst.setString(14, getPrevCompanyReportManagerPhNo()[i]);
								pst.setString(15, getPrevCompanyHRManager()[i]);
								pst.setString(16, getPrevCompanyHRManagerPhNo()[i]);
								pst.setString(17, strFileName);
							//===start parvez date: 08-08-2022===
								pst.setString(18, getPrevCompanyESICNo()[i]);
								pst.setString(19, getPrevCompanyUANNo()[i]);
							//===end parvez date: 08-08-2022===
//								log.debug("pst=>" + pst);
								pst.execute();
								pst.close();
							}
							
						}
						
					}
				}
			}
			//===end parvez date: 08-09-2021===

		} catch (Exception e) {
			e.printStackTrace();

		}finally {
						
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	
	public void updateSupportingDocuments(Connection con, UtilityFunctions uF) {
		
		try {
			if(uF.parseToInt(getDocId0())!=0) {
				if(getIdDoc0() != null) {
					updateDocuments(con, uF, getDocId0(), getIdDoc0(), getIdDocName0(), getIdDocType0(), getIdDocStatus0(), getIdDoc0FileName());

				}
			} else {
				insertDocuments(con, uF, getIdDoc0(), getIdDocName0(), getIdDocType0(), getIdDocStatus0(), getIdDoc0FileName());

			}
			if(uF.parseToInt(getDocId1())!=0) {
				if(getIdDoc1() != null) {
					updateDocuments(con, uF, getDocId1(), getIdDoc1(), getIdDocName1(), getIdDocType1(), getIdDocStatus1(), getIdDoc1FileName());
				}
			} else {
				insertDocuments(con, uF, getIdDoc1(), getIdDocName1(), getIdDocType1(), getIdDocStatus1(), getIdDoc1FileName());
			}
			if(uF.parseToInt(getDocId2())!=0) {
				if(getIdDoc2() != null) {
					updateDocuments(con, uF, getDocId2(), getIdDoc2(), getIdDocName2(), getIdDocType2(), getIdDocStatus2(), getIdDoc2FileName());
				}
			} else {
				insertDocuments(con, uF, getIdDoc2(), getIdDocName2(), getIdDocType2(), getIdDocStatus2(), getIdDoc2FileName());
			}
			
		//===start parvez date: 28-10-2022===	
			if(uF.parseToInt(getDocId3())!=0) {
				if(getIdDoc3() != null) {
					updateDocuments(con, uF, getDocId3(), getIdDoc3(), getIdDocName3(), getIdDocType3(), getIdDocStatus3(), getIdDoc3FileName());
				}
			} else {
				insertDocuments(con, uF, getIdDoc3(), getIdDocName3(), getIdDocType3(), getIdDocStatus3(), getIdDoc3FileName());
			}
		//===end parvez date: 28-10-2022===	
			//Inserting Other Documents
			
//			System.out.println("getotherDocumentCnt::::"+getotherDocumentCnt());
			int cnt = uF.parseToInt(getotherDocumentCnt());
			for(int i = 0; i<cnt ; i++) {
//				System.out.println("getIdDocOtherFileName()[i]::"+getIdDocOtherFileName()[i]);
//				System.out.println("otherDocName()[i]::"+getOtherDocName()[i]);
				if(getIdDocOtherFileName()[i] !=null && getIdDocOtherFileName()[i].length() != 0 && !getIdDocOtherFileName()[i].trim().equals("") && !getIdDocOtherFileName()[i].equalsIgnoreCase("null")){
					insertDocuments(con, uF, getIdDocOther()[i], getOtherDocName()[i], "Other", getIdDocStatusOther()[i], getIdDocOtherFileName()[i]);
				}
			} 
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	public void updateDocuments(Connection con, UtilityFunctions uF, String docId, File idDoc, String idDocName, String idDocType, String idDocStatus, String idDocFileName) {
		PreparedStatement pst = null;
		try {
			String strFileName = null;
//			System.out.println("In updateDocuments");
			if(idDoc != null) {
				if (CF.getStrDocSaveLocation() == null) {
					strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, idDoc, idDocFileName, idDocFileName, CF);
				} else {
					strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_CANDIDATE + "/" + I_DOCUMENT + "/" + I_ATTACHMENT + "/"+ getCandidateId(), idDoc, idDocFileName, idDocFileName, CF);
				}
			}
			pst = con.prepareStatement("UPDATE candidate_documents_details SET documents_file_name=?,added_by=?,entry_date=? where documents_id = ?");
			pst.setString(1, strFileName);
			pst.setInt(2, uF.parseToInt(getCandidateId()));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt(docId));
//			System.out.println("update pst ===>> " + pst);
			int x = pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void insertDocuments(Connection con, UtilityFunctions uF, File idDoc, String idDocName, String idDocType, String idDocStatus, String idDocFileName) {
		PreparedStatement pst = null;
		try {
			String strFileName = null;
			if(idDoc != null) {
				if (CF.getStrDocSaveLocation() == null) {
					strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, idDoc, idDocFileName, idDocFileName, CF);
				} else {
					strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_CANDIDATE + "/" + I_DOCUMENT + "/" + I_ATTACHMENT + "/"+ getCandidateId(), idDoc, idDocFileName, idDocFileName, CF);
				}
			}
//			System.out.println("strFileName ===>> " + strFileName);
			pst = con.prepareStatement("INSERT INTO candidate_documents_details(documents_name, documents_type, emp_id, documents_file_name, entry_date) values (?,?,?,?, ?)");
			pst.setString(1, idDocName);
			pst.setString(2, idDocType);
			pst.setInt(3, uF.parseToInt(getCandidateId()));
			pst.setString(4, strFileName);
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			int x = pst.executeUpdate();
//			System.out.println("Insert pst======>"+pst);

			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
//	public void insertDocuments(Connection con, UtilityFunctions uF) {
//		PreparedStatement pst = null;
//		try {
//			if(getIdDoc()!=null && getIdDoc().size()!= 0 ) {
//				for (int i=0; i<getIdDoc().size(); i++) {
//					
//					if(getIdDoc().get(i)!=null & getIdDoc().get(i).length()!= 0) {
//						
//						String fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getIdDoc().get(i), getIdDocFileName().get(i));
//			            pst = con.prepareStatement("INSERT INTO candidate_documents_details (documents_name, documents_type, emp_id, documents_file_name," +
//			            		"entry_date) values (?,?,?,?,?)");
//						pst.setString(1, getIdDocName()[i]);
//			            pst.setString(2, getIdDocType()[i]);
//			            pst.setInt(3, uF.parseToInt(getCandidateId()));
//			            pst.setString(4, fileName);
////			            pst.setInt(5, uF.parseToInt(strSessionEmpId));
//			            pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
////			            log.debug("pst insertDocuments==>"+pst);
//			            pst.execute();
//						pst.close();
//					}
//				}
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally {
//						
//			if(pst != null) {
//				try {
//					pst.close();
//				} catch (SQLException e1) {
//					e1.printStackTrace();
//				}
//			}
//		}
//	}

//	public void insertDocuments(Connection con, UtilityFunctions uF) {
//		PreparedStatement pst = null;
//
//		try {
//
//			String filePath1 = request.getRealPath("/userDocuments/");
//			String fileName1 = "";
//
//			if (getIdDoc() != null && getIdDoc().length != 0) {
//
//				log.debug("getIdDoc().length==>" + getIdDoc().length);
//				log.debug("idDoc.length==>" + idDoc.length + " " + getIdDocName().length);
//
//				log.debug(getIdDoc()[0]);
//
//				for (int i = 0; i < getIdDoc().length; i++) {
//
//					if (getIdDoc()[i] != null & getIdDoc()[i].length() != 0) {
//
//						/*
//						 * int random1 = new Random().nextInt(); fileName1 =
//						 * random1 + getIdDoc()[i].getName(); File fileToCreate
//						 * = new File(filePath1, fileName1);
//						 * FileUtils.copyFile(getIdDoc()[i], fileToCreate);
//						 */
//
//						String fileName = uF.uploadFile(request, CF.getStrDocSaveLocation(), getIdDoc()[i], getIdDocFileName()[i], CF.getIsRemoteLocation(), CF);
//
//						pst = con.prepareStatement("INSERT INTO candidate_documents_details (documents_name, documents_type, emp_id, documents_file_name) values (?,?,?,?)");
//						pst.setString(1, getIdDocName()[i]);
//						pst.setString(2, getIdDocType()[i]);
//						pst.setInt(3, uF.parseToInt(getCandidateId()));
//						pst.setString(4, fileName);
//						log.debug("pst insertDocuments==>" + pst);
//						pst.execute();
//
//					}
//
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//	}

	public void insertHobbies(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;

		try {

			if (getHobbyName() != null && getHobbyName().length != 0) {

				for (String h : hobbyName) {
					if (h != null && h.length() != 0 && !h.trim().equals("") && !h.equalsIgnoreCase("null")) {
						pst = con.prepareStatement("INSERT INTO candidate_hobbies_details (hobbies_name, emp_id) VALUES (?,?)");
						pst.setString(1, h);
						pst.setInt(2, uF.parseToInt(getCandidateId()));
//						log.debug("pst==>>" + pst);
						pst.execute();
						pst.close();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
						
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	public void insertSkills(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
//		System.out.println("getSkillName:"+getSkillName());
		try {

			for (int i = 0; i < getSkillName().length; i++) {

				if (getSkillName()[i] != null && getSkillName()[i].length() != 0 && !getSkillName()[i].trim().equals("") && !getSkillName()[i].equalsIgnoreCase("null")) {

					pst = con.prepareStatement("INSERT INTO candidate_skills_description (skill_id, skills_value, emp_id) VALUES (?,?,?)");
					pst.setInt(1, uF.parseToInt(getSkillName()[i]));
					pst.setString(2, getSkillValue()[i]);
					pst.setInt(3, uF.parseToInt(getCandidateId()));
//					log.debug("pst==>>" + pst);
					pst.execute();
					pst.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
						
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/*
	 * public void insertProbationPeriod(Connection con, UtilityFunctions uF) {
	 * 
	 * PreparedStatement pst = null;
	 * 
	 * try {
	 * 
	 * StringBuilder sbProbationLeaves = new StringBuilder(); int i = 0;
	 * if(getProbationLeaves() != null) { for (; i < getProbationLeaves().length
	 * - 1; i++) {
	 * 
	 * if (uF.parseToInt(getProbationLeaves()[i]) > 0) {
	 * sbProbationLeaves.append(getProbationLeaves()[i] + ","); } } if
	 * (uF.parseToInt(getProbationLeaves()[i]) > 0) {
	 * sbProbationLeaves.append(getProbationLeaves()[i]); } } pst =
	 * con.prepareStatement(
	 * "INSERT INTO candidate_probation_policy(emp_id, leaves_types_allowed, probation_duration, notice_duration) VALUES(?,?,?,?)"
	 * ); pst.setInt(1,uF.parseToInt(getCandidateId())); pst.setString(2,
	 * sbProbationLeaves.toString()); pst.setInt(3, getProbationDuration());
	 * pst.setInt(4, getNoticeDuration());
	 * 
	 * log.debug("pst insertProbationPolicy==>>"+pst); pst.execute();
	 * pst.close();
	 * 
	 * } catch (Exception e) { e.printStackTrace();
	 * 
	 * }
	 * 
	 * }
	 */
	/*
	 * public void insertUser(Connection con, UtilityFunctions uF, int empId) {
	 * 
	 * PreparedStatement pst = null;
	 * 
	 * try {
	 * 
	 * AddCandidateMode aE = new AddCandidateMode();
	 * aE.setServletRequest(request); aE.CF = CF; aE.setFname(getEmpFname());
	 * aE.setLname(getEmpLname()); String username = aE.getUserName();
	 * 
	 * SecureRandom random = new SecureRandom(); String password = new
	 * BigInteger(130, random).toString(32).substring(5, 13);
	 * 
	 * pst = con.prepareStatement(insertUser);
	 * 
	 * pst.setString(1, username); pst.setString(2, password); pst.setInt(3, 3);
	 * pst.setInt(4, empId); pst.setString(5, "ACTIVE"); pst.setTimestamp(6,
	 * uF.getTimeStamp
	 * (uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime
	 * (CF.getStrTimeZone())+"", DBDATE+DBTIME));
	 * 
	 * pst.execute();
	 * 
	 * } catch (Exception e) { e.printStackTrace();
	 * 
	 * } }
	 * 
	 * public void insertEmpOfficialDetails(Connection con, UtilityFunctions uF,
	 * StringBuilder sbServices) {
	 * 
	 * PreparedStatement pst = null;
	 * 
	 * try {
	 * 
	 * pst = con.prepareStatement(insertEmployeeO); pst.setInt(1,
	 * uF.parseToInt(getDepartment())); pst.setInt(2,
	 * uF.parseToInt(getSupervisor())); pst.setString(3, sbServices.toString());
	 * // pst.setTime(5, uF.getTimeFormat((getAvailFrom() != null) ? //
	 * getAvailFrom().replace("T", "") : "", "yyyy-MM-ddhh:mm")); //
	 * pst.setTime(6, uF.getTimeFormat((getAvailTo() != null) ? //
	 * getAvailTo().replace("T", "") : "", "yyyy-MM-ddhh:mm")); //
	 * pst.setTime(5, uF.getTimeFormat((getAvailFrom() != null) ?
	 * getAvailFrom().replace("T", "") : "", CF.getStrReportTimeFormat())); //
	 * pst.setTime(6, uF.getTimeFormat((getAvailTo() != null) ?
	 * getAvailTo().replace("T", "") : "", CF.getStrReportTimeFormat()));
	 * 
	 * pst.setString(4, ""); pst.setInt(5,uF.parseToInt(getCandidateId()));
	 * pst.setInt(6, uF.parseToInt(getwLocation())); pst.setBoolean(7,
	 * uF.parseToBoolean(getRosterDependency())); pst.setBoolean(8,
	 * uF.parseToBoolean(getAttendanceDependency())); pst.setString(9,
	 * getEmpType()); pst.setBoolean(10, getIsFirstAidAllowance());
	 * pst.setInt(11, uF.parseToInt(getEmpGrade())); pst.setString(12,
	 * getStrPaycycleDuration()); pst.setInt(13,
	 * uF.parseToInt(getEmpPaymentMode()));
	 * 
	 * log.debug("pst insertEmployeeO=>"+pst);
	 * 
	 * // pst.setString(13, getEmpDesignation()); pst.execute();
	 * 
	 * } catch (Exception e) { e.printStackTrace();
	 * 
	 * }
	 * 
	 * }
	 */

	public int insertCandidatePersonalDetails(Connection con, UtilityFunctions uF, CommonFunctions CF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		int empPerId = 0;
		try {
			pst = con.prepareStatement("INSERT INTO candidate_personal_details (empcode, emp_fname, emp_lname, emp_email, emp_address1, emp_address2, " +
					"emp_city_id, emp_state_id, emp_country_id, emp_pincode, emp_address1_tmp, emp_address2_tmp, emp_city_id_tmp, emp_state_id_tmp, " +
					"emp_country_id_tmp, emp_pincode_tmp, emp_contactno, joining_date, emp_pan_no,emp_pf_no,emp_gpf_no, emp_gender, " +
					"emp_date_of_birth, emp_bank_name, emp_bank_acct_nbr, emp_email_sec, skype_id, emp_contactno_mob, emergency_contact_name, " +
					"emergency_contact_no, passport_no, passport_expiry_date, blood_group, marital_status,emp_date_of_marriage, emp_entry_date, " +
					"emp_mname, salutation,source_type,source_or_ref_code,other_ref_src,current_ctc,expected_ctc,notice_period,availability_for_interview, org_id) " +
					"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
			pst.setString(1, uF.showData(getEmpCodeAlphabet(), "") + uF.showData(getEmpCodeNumber(), ""));
			pst.setString(2, getEmpFname());
			pst.setString(3, getEmpLname());
			pst.setString(4, getEmpEmail());

			pst.setString(5, getEmpAddress1());
			pst.setString(6, getEmpAddress2());
			pst.setString(7, getCity());
			pst.setInt(8, uF.parseToInt(getState()));
			pst.setInt(9, uF.parseToInt(getCountry()));
			pst.setString(10, getEmpPincode());

			pst.setString(11, getEmpAddress1Tmp());
			pst.setString(12, getEmpAddress2Tmp());
			pst.setString(13, getCityTmp());
			pst.setInt(14, uF.parseToInt(getStateTmp()));
			pst.setInt(15, uF.parseToInt(getCountryTmp()));
			pst.setString(16, getEmpPincodeTmp());

			pst.setString(17, getEmpContactno());
			pst.setDate(18, uF.getDateFormat(getEmpStartDate(), DATE_FORMAT));
			pst.setString(19, getEmpPanNo());
			pst.setString(20, getEmpPFNo());
			pst.setString(21, getEmpGPFNo());
			pst.setString(22, getEmpGender());
			pst.setDate(23, uF.getDateFormat(getEmpDateOfBirth(), DATE_FORMAT));
			pst.setString(24, getEmpBankName());
			pst.setString(25, getEmpBankAcctNbr());
			pst.setString(26, getEmpEmailSec());
			pst.setString(27, getSkypeId());
			pst.setString(28, getEmpMobileNo());
			pst.setString(29, getEmpEmergencyContactName());
			pst.setString(30, getEmpEmergencyContactNo());
			pst.setString(31, getEmpPassportNo());
			pst.setDate(32, uF.getDateFormat(getEmpPassportExpiryDate(), DATE_FORMAT));
			pst.setString(33, getEmpBloodGroup());
			pst.setString(34, getEmpMaritalStatus());
			pst.setDate(35, uF.getDateFormat(getEmpDateOfMarriage(), DATE_FORMAT));
			pst.setTimestamp(36, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.setString(37, getEmpMname());
			pst.setString(38, getSalutation());
			pst.setInt(39, uF.parseToInt(getCandiSource()));
			pst.setInt(40, uF.parseToInt(getRefEmpId()));
			pst.setString(41, getOtherRefSrc());
			
			pst.setDouble(42, uF.parseToDouble(getCandiCurrCTC()));
//			System.out.println("ACBC/3073--getCandiExpectedCTC="+getCandiExpectedCTC());
			pst.setDouble(43, uF.parseToDouble(getCandiExpectedCTC()));
			pst.setInt(44, uF.parseToInt(getCandiNoticePeriod()));
			pst.setBoolean(45, uF.parseToBoolean(getAvailability()));
			pst.setInt(46, uF.parseToInt(getOrg_id()));
//			pst.setInt(39, uF.parseToInt(getRecruitId()));
//			pst.setString(40, getJobcode());
			pst.executeUpdate();
//			System.out.println("pst ===>> " + pst);
			pst.close();
			
			pst = con.prepareStatement("SELECT max(emp_per_id) as emp_per_id from candidate_personal_details");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("ACBC/3086--pst candi max id ===> "+pst);
			while (rs.next()) {
				setCandidateId(rs.getString("emp_per_id"));
				empPerId = uF.parseToInt(rs.getString("emp_per_id"));
			}
			rs.close();
			pst.close();
//			System.out.println("empPerId ===>> " + empPerId);
			
			pst=con.prepareStatement("select wlocation,org_id from recruitment_details where recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rs=pst.executeQuery();
			int orgId = 0;
			int wLocId = 0;
			while(rs.next()) {
				orgId = rs.getInt("org_id");
				wLocId = rs.getInt("wlocation");
			}
			rs.close();
			pst.close();
			

			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			List<String> empList = new ArrayList<String>();
			if(orgId > 0 && wLocId > 0) {
				StringBuilder sbQue = new StringBuilder();
				sbQue.append("select emp_per_id from employee_official_details eod,user_details ud, employee_personal_details epd where " +
					" epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=?");
				sbQue.append(" and (org_id = "+orgId+" or org_id_access like '%,"+orgId+",%') and (wlocation_id = "+wLocId+" or wlocation_id_access like '%,"+wLocId+",%') ");
				pst=con.prepareStatement("");
				pst.setInt(1, uF.parseToInt(hmUserTypeId.get(HRMANAGER)));
				rs=pst.executeQuery();
				while(rs.next()) {
					if(!empList.contains(rs.getString("emp_per_id").trim())) {
						empList.add(rs.getString("emp_per_id").trim());	
					}
				}
				rs.close();
				pst.close();
			}
			pst=con.prepareStatement("select emp_per_id from employee_official_details eod,user_details ud, employee_personal_details epd where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=?");
			pst.setInt(1, uF.parseToInt(hmUserTypeId.get(ADMIN)));
			rs=pst.executeQuery();
			while(rs.next()) {
				if(!empList.contains(rs.getString("emp_per_id").trim())) {
					empList.add(rs.getString("emp_per_id").trim());	
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("empList ===>> " + empList);
			String strDomain = request.getServerName().split("\\.")[0];
			for(int i=0; empList!= null && !empList.isEmpty() && i<empList.size(); i++) {
				if(!empList.get(i).equals("") && uF.parseToInt(empList.get(i)) > 0) {
					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(empList.get(i));
					userAlerts.set_type(NEW_CANDIDATE_FILL_ALERT);
					userAlerts.setStatus(INSERT_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
				}
			}
			
			
//			System.out.println("empPerId ===> "+empPerId);
			getCandidateName();
			
			if(uF.parseToInt(getRecruitId()) > 0) {
				pst = con.prepareStatement("INSERT INTO candidate_application_details (candidate_id,recruitment_id,job_code,application_date," +
						"entry_date) VALUES (?,?,?,?, ?)");
				pst.setInt(1, empPerId);
				pst.setInt(2, uF.parseToInt(getRecruitId()));
				pst.setString(3, getJobcode());
				pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
	//			pst.setInt(5, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
				pst.execute();
				pst.close();
				
				pst=con.prepareStatement("delete from candidate_activity_details where recruitment_id=? and candi_id=? and user_id=? and " +
						"activity_id = ?");
				pst.setInt(1, uF.parseToInt(getRecruitId()));
				pst.setInt(2, empPerId);
				pst.setInt(3, 0);
				pst.setInt(4, CANDI_ACTIVITY_APPLY_ID);
				pst.executeUpdate();
				pst.close();
				
				pst=con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getRecruitId()));
				pst.setInt(2, empPerId);
				pst.setString(3, "Apply for Job");
				pst.setInt(4, 0);
				pst.setDate(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
				pst.setInt(6, CANDI_ACTIVITY_APPLY_ID);
				pst.execute();
				pst.close();
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
		return empPerId;
	}

	
	public void insertEmpFamilyMembers(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;

		try {

			if (getMotherName() != null && getMotherName().length() > 0) {

				pst = con.prepareStatement("INSERT INTO candidate_family_members(member_type, member_name, member_dob, member_education, "
						+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");
				pst.setString(1, MOTHER);
				pst.setString(2, getMotherName());
//				log.debug(getMotherDob() + "getMotherDob()");
				pst.setDate(3, uF.getDateFormat(getMotherDob(), DATE_FORMAT));
				pst.setString(4, getMotherEducation());
				pst.setString(5, getMotherOccupation());
				pst.setString(6, getMotherContactNumber());
				pst.setString(7, getMotherEmailId());
				pst.setString(8, "F");
				pst.setInt(9, uF.parseToInt(getCandidateId()));
//				log.debug("pst=>" + pst);
				pst.execute();
				pst.close();
			}

			if (getFatherName() != null && getFatherName().length() > 0) {

				pst = con.prepareStatement("INSERT INTO candidate_family_members(member_type, member_name, member_dob, member_education, "
						+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");

				pst.setString(1, FATHER);
				pst.setString(2, getFatherName());
				pst.setDate(3, uF.getDateFormat(getFatherDob(), DATE_FORMAT));
				pst.setString(4, getFatherEducation());
				pst.setString(5, getFatherOccupation());
				pst.setString(6, getFatherContactNumber());
				pst.setString(7, getFatherEmailId());
				pst.setString(8, "M");
				pst.setInt(9, uF.parseToInt(getCandidateId()));
//				log.debug("pst=>" + pst);
				pst.execute();
				pst.close();
			}

			if (getSpouseName() != null && getSpouseName().length() > 0) {
				pst = con.prepareStatement("INSERT INTO candidate_family_members(member_type, member_name, member_dob, member_education, "
						+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");

				pst.setString(1, SPOUSE);
				pst.setString(2, getSpouseName());
				pst.setDate(3, uF.getDateFormat(getSpouseDob(), DATE_FORMAT));
				pst.setString(4, getSpouseEducation());
				pst.setString(5, getSpouseOccupation());
				pst.setString(6, getSpouseContactNumber());
				pst.setString(7, getSpouseEmailId());
				pst.setString(8, "F");
				pst.setInt(9, uF.parseToInt(getCandidateId()));
//				log.debug("pst=>" + pst);
				pst.execute();
				pst.close();
			}

			for (int i = 0; i < getMemberName().length; i++) {
				if (getMemberName()[i] != null && getMemberName()[i].length() > 0) {
					pst = con.prepareStatement("INSERT INTO candidate_family_members(member_type, member_name, member_dob, member_education, "
							+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id)" + "VALUES (?,?,?,?,?,?,?,?,?)");

					pst.setString(1, SIBLING);
					pst.setString(2, getMemberName()[i]);
					pst.setDate(3, uF.getDateFormat(getMemberDob()[i], DATE_FORMAT));
					pst.setString(4, getMemberEducation()[i]);
					pst.setString(5, getMemberOccupation()[i]);
					pst.setString(6, getMemberContactNumber()[i]);
					pst.setString(7, getMemberEmailId()[i]);
					pst.setString(8, getMemberGender()[i]);
					pst.setInt(9, uF.parseToInt(getCandidateId()));
//					log.debug("pst=>" + pst);
					pst.execute();
					pst.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	public void insertEmpEducation(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			if (getDegreeName() != null && getDegreeName().length != 0) {
				
				//===added by Parvez date: 04-09-2021===
				//===start===
//				MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
				//===end===
				
				String[] degreeNameOther = request.getParameterValues("degreeNameOther");
				for (int i = 0; i < getDegreeName().length; i++) {

					if (getDegreeName()[i] != null && getDegreeName()[i].equalsIgnoreCase("other")) {
						if(degreeNameOther[i] != null && !degreeNameOther[i].trim().equals("") && !degreeNameOther[i].equalsIgnoreCase("null")) {
							pst = con.prepareStatement("insert into educational_details(education_name,education_details,org_id) " + "VALUES (?,?,?)");
							pst.setString(1, degreeNameOther[i]);
							pst.setString(2, "");
							pst.setInt(3, uF.parseToInt(getOrg_id()));
							pst.execute();
							pst.close();
							
							int newEduid = 0;
							//===start parvez on 05-08-2021 from missing===
							pst = con.prepareStatement("select max(edu_id) as edu_id from educational_details");
							//===end parvez on 05-08-2021===
							rs = pst.executeQuery();
							while (rs.next()) {
								newEduid = rs.getInt("edu_id");
							}
							rs.close();
							pst.close();
							
							/*pst = con.prepareStatement("INSERT INTO candidate_education_details(education_id, degree_duration, completion_year, grade, emp_id)" + "VALUES (?,?,?,?,?)");
							pst.setInt(1, newEduid);
							pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
							pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
							pst.setString(4, getGrade()[i]);
							pst.setInt(5, uF.parseToInt(getCandidateId()));
//							log.debug("pst=>" + pst);
							pst.execute();
							pst.close();*/
							
							//===start parvez date: 07-09-2021===	
//							pst = con.prepareStatement("INSERT INTO candidate_education_details(education_id, degree_duration, completion_year, grade, emp_id)" + "VALUES (?,?,?,?,?)");
							pst = con.prepareStatement("INSERT INTO candidate_education_details(education_id, degree_duration, completion_year, grade, emp_id," +
									"subject,start_date,completion_date,marks,city,institute_name,university_name)" + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
							pst.setInt(1, newEduid);
							pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
							pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
							pst.setString(4, getGrade()[i]);
							pst.setInt(5, uF.parseToInt(getCandidateId()));
							pst.setString(6, getSubject()[i].trim());
							pst.setDate(7, uF.getDateFormat(getStrStartDate()[i], DATE_FORMAT));
							pst.setDate(8, uF.getDateFormat(getStrCompletionDate()[i], DATE_FORMAT));
							pst.setDouble(9, uF.parseToDouble(getMarks()[i].trim()));
							pst.setString(10, getCity1()[i].trim());
							pst.setString(11, getInstituteName()[i].trim());
							pst.setString(12, getUniversityName()[i].trim());
							System.out.println("AC/3783--pst="+pst);
//							log.debug("pst=>" + pst);
							pst.execute();
							pst.close();
							
							
							int intDegreeId = 0;
							//===start parvez date: 08-09-2021===
							pst = con.prepareStatement("select max(degree_id) as degree_id from candidate_education_details");
							//===end parvez date: 08-09-2021===
							rs = pst.executeQuery();
							while(rs.next()) {
								intDegreeId = rs.getInt("degree_id");
							}
							rs.close();
							pst.close();
							
							if(uF.parseToInt(getDegreeCertiStatus()[i]) == 1) {
								MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
								File[] strCertificateDoc = mpRequest.getFiles("degreeCertificate"+i);
								String[] strCertificateDocFileName = mpRequest.getFileNames("degreeCertificate"+i);
//								System.out.println("strCertificateDoc ===>> " + strCertificateDoc.length);
								for(int j=0; strCertificateDoc != null && j<strCertificateDoc.length; j++) {
									String strFileName = null;
									if (CF.getStrDocSaveLocation() == null) {
										strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, strCertificateDoc[j], strCertificateDocFileName[j], strCertificateDocFileName[j], CF);
									} else {
										//===start parvez on 07-09-2021===
										strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_CANDIDATE + "/" + I_DOCUMENT + "/" + I_EDUCATION_DOC + "/"+ getCandidateId(), strCertificateDoc[j], strCertificateDocFileName[j], strCertificateDocFileName[j], CF);
										//===end parvez on 07-09-2021===
									}
									pst = con.prepareStatement("INSERT INTO candidate_degree_certificate_details(degree_id, emp_id, degree_certificate_name) VALUES (?,?,?)");
									pst.setInt(1, intDegreeId);
									pst.setInt(2, uF.parseToInt(getCandidateId()));
									pst.setString(3, strFileName);
									pst.executeUpdate();
									pst.close();
								}
							}
						//===end parvez date: 07-09-2021===
						}
					} else {
						if(getDegreeName()[i] != null && !getDegreeName()[i].trim().equals("") && !getDegreeName()[i].equalsIgnoreCase("null")) {
							//===start parvez on 05-08-2021===
							int newEduid = 0;
							pst = con.prepareStatement("select edu_id from educational_details where education_name=? and org_id=?");
							pst.setString(1, getDegreeName()[i]);
							pst.setInt(2, uF.parseToInt(getOrg_id()));
							
//							System.out.println("ACBC/3354--getOrg_id="+getOrg_id());
							rs = pst.executeQuery();
							while (rs.next()) {
								newEduid = rs.getInt("edu_id");
							}
							rs.close();
							pst.close();
							//===end parvez on 05-08-2021===
							
							//===start parvez on 05-08-2021===
							/*pst = con.prepareStatement("INSERT INTO candidate_education_details(degree_name, degree_duration, completion_year, grade, emp_id, education_id)" + "VALUES (?,?,?,?,?,?)");
							//===end parvez on 05-08-2021===
							pst.setString(1, getDegreeName()[i]);
							pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
							pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
							pst.setString(4, getGrade()[i]);
							pst.setInt(5, uF.parseToInt(getCandidateId()));
							//===start parvez on 05-08-2021===
							pst.setInt(6, newEduid);
							//===start parvez on 05-08-2021===
//							log.debug("pst=>" + pst);
							pst.execute();
							pst.close();*/
							
							//===start parvez on 07-09-2021===
							pst = con.prepareStatement("INSERT INTO candidate_education_details(degree_name, degree_duration, completion_year, grade, emp_id, education_id," +
									"subject,start_date,completion_date,marks,city,institute_name,university_name)" + 
									"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
							pst.setString(1, getDegreeName()[i]);
							pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
							pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
							pst.setString(4, getGrade()[i]);
							pst.setInt(5, uF.parseToInt(getCandidateId()));
							pst.setInt(6, newEduid);
							pst.setString(7, getSubject()[i].trim());
							pst.setDate(8, uF.getDateFormat(getStrStartDate()[i], DATE_FORMAT));
							pst.setDate(9, uF.getDateFormat(getStrCompletionDate()[i], DATE_FORMAT));
							pst.setDouble(10, uF.parseToDouble(getMarks()[i].trim()));
							pst.setString(11, getCity1()[i].trim());
							pst.setString(12, getInstituteName()[i].trim());
							pst.setString(13, getUniversityName()[i].trim());
//							log.debug("pst=>" + pst);
							pst.execute();
							pst.close();
							
							int intDegreeId = 0;
							pst = con.prepareStatement("select max(degree_id) as degree_id from candidate_education_details");
							rs = pst.executeQuery();
							while(rs.next()) {
								intDegreeId = rs.getInt("degree_id");
							}
							rs.close();
							pst.close();
							
//							System.out.println("ACBC/3698--intDegreeId="+intDegreeId);
							if(uF.parseToInt(getDegreeCertiStatus()[i]) == 1) {
								MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
								File[] strCertificateDoc = mpRequest.getFiles("degreeCertificate"+i);
								String[] strCertificateDocFileName = mpRequest.getFileNames("degreeCertificate"+i);
//								System.out.println("AC/3856--strCertificateDoc ===>> " + strCertificateDoc.length);
								for(int j=0; strCertificateDoc != null && j<strCertificateDoc.length; j++) {
									String strFileName = null;
									if (CF.getStrDocSaveLocation() == null) {
										strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, strCertificateDoc[j], strCertificateDocFileName[j], strCertificateDocFileName[j], CF);
									} else {
										//===start parvez on 07-09-2021===
										strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_CANDIDATE + "/" + I_DOCUMENT + "/" + I_EDUCATION_DOC + "/"+ getCandidateId(), strCertificateDoc[j], strCertificateDocFileName[j], strCertificateDocFileName[j], CF);
										//===end parvez on 07-09-2021===
									}
									pst = con.prepareStatement("INSERT INTO candidate_degree_certificate_details(degree_id, emp_id, degree_certificate_name) VALUES (?,?,?)");
									pst.setInt(1, intDegreeId);
									pst.setInt(2, uF.parseToInt(getCandidateId()));
									pst.setString(3, strFileName);
									System.out.println("AC/3868--pst="+pst);
									pst.executeUpdate();
									pst.close();
								}
							}
							//===end parvez on 07-09-2021===
						}
					}

				}

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

	}

	public void insertEmpLangues(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		try {
			
			if(getLanguageName()!=null && getLanguageName().length!=0) {
				for(int i=0; i<getLanguageName().length; i++) {
					if(getLanguageName()[i] != null && getLanguageName()[i].length()!=0 && !getLanguageName()[i].trim().equals("") && !getLanguageName()[i].equalsIgnoreCase("null")) {
						pst = con.prepareStatement("INSERT INTO candidate_languages_details(language_name, language_read, language_write, language_speak, emp_id)" +
						"VALUES (?,?,?,?,?)");
						pst.setString(1, getLanguageName()[i]);
						pst.setInt(2, uF.parseToInt(getIsRead()[i]));
						pst.setInt(3, uF.parseToInt(getIsWrite()[i]));
						pst.setInt(4, uF.parseToInt(getIsSpeak()[i]));
						pst.setInt(5, uF.parseToInt(getCandidateId()));
//						log.debug("pst=>"+pst);
						pst.execute();
						pst.close();
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
						
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		/*PreparedStatement pst = null;

		try {

			if (getLanguageName() != null && getLanguageName().length != 0) {

				for (int i = 0; i < getLanguageName().length; i++) {

					if (getLanguageName()[i].length() != 0 && getLanguageName()[i] != "") {

						pst = con.prepareStatement("INSERT INTO candidate_languages_details(language_name, language_read, language_write, language_speak, emp_id)"
								+ "VALUES (?,?,?,?,?)");
						pst.setString(1, getLanguageName()[i]);
						pst.setInt(2, uF.parseToInt(getIsRead()!=null ? getIsRead()[i] : "0"));
						pst.setInt(3, uF.parseToInt(getIsWrite()!=null ? getIsWrite()[i] : "0"));
						pst.setInt(4, uF.parseToInt(getIsSpeak()!=null ? getIsSpeak()[i] : "0"));
						pst.setInt(5, uF.parseToInt(getCandidateId()));
						log.debug("pst=>" + pst);
						pst.execute();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		}*/

	}

	public String deleteEmployee(int CandidateId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("DELETE FROM candidate_personal_details WHERE emp_per_id=?");
			pst.setInt(1, CandidateId);
			pst.execute();
			pst.close();
			/*
			 * pst = con.prepareStatement(deleteEmployee_O); pst.setInt(1,
			 * CandidateId); pst.execute();
			 * 
			 * pst = con.prepareStatement(deleteAllowance); pst.setInt(1,
			 * CandidateId); pst.execute();
			 * 
			 * pst = con.prepareStatement(deleteUserEmp); pst.setInt(1,
			 * CandidateId); pst.execute();
			 */
			pst = con.prepareStatement("DELETE from candidate_skills_description where emp_id =?");
			pst.setInt(1, CandidateId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE from candidate_hobbies_details where emp_id =?");
			pst.setInt(1, CandidateId);
			pst.execute();
			pst.close();

			pst = con.prepareStatement("DELETE FROM candidate_languages_details WHERE emp_id = ?");
			pst.setInt(1, CandidateId);
			pst.execute();
			pst.close();

			pst = con.prepareStatement("DELETE FROM candidate_education_details WHERE emp_id = ?");
			pst.setInt(1, CandidateId);
			pst.execute();
			pst.close();

			pst = con.prepareStatement("DELETE FROM candidate_documents_details WHERE emp_id = ?");
			pst.setInt(1, CandidateId);
			pst.execute();
			pst.close();

			pst = con.prepareStatement("DELETE FROM candidate_family_members WHERE emp_id = ?");
			pst.setInt(1, CandidateId);
			pst.execute();
			pst.close();

			pst = con.prepareStatement("DELETE FROM candidate_prev_employment WHERE emp_id = ?");
			pst.setInt(1, CandidateId);
			pst.execute();
			pst.close();

			pst = con.prepareStatement("DELETE FROM candidate_references WHERE emp_id = ?");
			pst.setInt(1, CandidateId);
			pst.execute();
			pst.close();

			// request.setAttribute(MESSAGE, "Deleted successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	private String empUserTypeId;
	// private String empPerId;
	// private String empId;
	private String empCodeAlphabet;
	private String empCodeNumber;
	private String salutation;;
	private String empFname;
	private String empMname;
	private String empLname;

	private String userName;
	private String empPassword;

	private String empEmail;

	private String empAddress1;
	private String empAddress2;
	private String country;
	private String state;
	private String city;
	private String empPincode;

	private String empAddress1Tmp;
	private String empAddress2Tmp;
	private String countryTmp;
	private String stateTmp;
	private String cityTmp;
	private String empPincodeTmp;

	private String empContactno;
	private String empEmergencyContactName;
	private String empEmergencyContactNo;
	private String empPassportNo;
	private String empPassportExpiryDate;
	private String empBloodGroup;
	private String empMaritalStatus;
	private boolean approvedFlag;

	private Timestamp empFilledFlagDate;

	private String empImageFileName;
	private File empImage;

	private String empPanNo;
	private String empPFNo;
	private String empGPFNo;
	private String empGender;
	private String empDateOfBirth;
	private String empDateOfMarriage;
	private String empBankName;
	private String empBankAcctNbr;
	private String empEmailSec;
	private String skypeId;
	private String empMobileNo;

	private String ref1Name;
	private String ref1Company;
	private String ref1Designation;
	private String ref1ContactNo;
	private String ref1Email;

	private String ref2Name;
	private String ref2Company;
	private String ref2Designation;
	private String ref2ContactNo;
	private String ref2Email;

	private String empStartDate;
	private String wLocation;
	private String empGrade;
	private String strLevel;
	private String strDesignation;
	private String department;
	private String supervisor;
	private String[] service;
	private String availFrom;
	private String availTo;
	private String rosterDependency;
	private String attendanceDependency;
	private String empType;
	private boolean isFirstAidAllowance;
	private int probationDuration;
	private int noticeDuration;

	private String[] empKra;
	private String[] empKraId;

	private String[] probationLeaves;

	private String[] skillName;
	private String[] skillValue;

	private String[] hobbyName;
	
	private String[] otherDocument[];

	private String[] languageName;
	private String[] isRead;
	private String[] isWrite;
	private String[] isSpeak;

	private String[] degreeName;
	private String[] degreeDuration;
	private String[] completionYear;
	private String[] grade;
	//===start Parvez date: 14-09-2021===
	private String[] degreeId;
	//===end Parvez date: 14-09-2021===
	//===start Parvez date: 07-09-2021===
	private String[] marks;
	private String[] city1;
	private String[] instituteName;
	private String[] universityName;
	private String[] subject;
	private String[] degreeCertiStatus;
	private String[] strStartDate;
	private String[] strCompletionDate;
	//===end parvez date: 07-09-2021===

	/*private List<File> idDoc;
	private List<String> idDocFileName;
	private List<String> idDocStatus;
	private int[] docId;
	private String[] idDocName;
	private String[] idDocType;*/

	private String docId0;
	private File idDoc0;
	private String idDoc0FileName;
	private String idDocStatus0;
	private String idDocName0;
	private String idDocType0;
	
	private String[] docIdother;
	private File[] idDocOther;
	private String[] idDocOtherFileName;
	private String[] idDocStatusOther;
	private String[] idDocNameOther;
	private String[] idDocTypeOther;
	private String[] otherDocName;

	
	private String docId1;
	private File idDoc1;
	private String idDoc1FileName;
	private String idDocStatus1;
	private String idDocName1;
	private String idDocType1;
	
	private String docId2;
	private File idDoc2;
	private String idDoc2FileName;
	private String idDocStatus2;
	private String idDocName2;
	private String idDocType2;
	
//===start parvez date: 28-10-2022===	
	private String docId3;
	private File idDoc3;
	private String idDoc3FileName;
	private String idDocStatus3;
	private String idDocName3;
	private String idDocType3;
//===end parvez date: 28-10-2022===	
	
	private String fatherName;
	private String fatherDob;
	private String fatherEducation;
	private String fatherOccupation;
	private String fatherContactNumber;
	private String fatherEmailId;
	private String motherName;
	private String motherDob;
	private String motherEducation;
	private String motherOccupation;
	private String motherContactNumber;
	private String motherEmailId;

	private String spouseName;
	private String spouseDob;
	private String spouseEducation;
	private String spouseOccupation;
	private String spouseContactNumber;
	private String spouseEmailId;
	private String spouseGender;

	private String[] memberName;
	private String[] memberDob;
	private String[] memberEducation;
	private String[] memberOccupation;
	private String[] memberContactNumber;
	private String[] memberEmailId;
	private String[] memberGender;

	private String[] documentNames;
	private String[] documentValues;

	private String[] prevCompanyName;
	private String[] prevCompanyLocation;
	private String[] prevCompanyCity;
	private String[] prevCompanyState;
	private String[] prevCompanyCountry;
	private String[] prevCompanyContactNo;
	private String[] prevCompanyReportingTo;
	private String[] prevCompanyReportManagerPhNo;
	private String[] prevCompanyHRManager;
	private String[] prevCompanyHRManagerPhNo;
	private String[] prevCompanyFromDate;
	private String[] prevCompanyToDate;
	private String[] prevCompanyDesination;
	private String[] prevCompanyResponsibilities;
	private String[] prevCompanySkills;
	private String[] expLetterFileStatus;
	private String[] prevCompanyId;
	
//===start Parvez date: 08-08-2022===	
	private String[] prevCompanyUANNo;
	private String[] prevCompanyESICNo;
//===end Parvez date: 08-08-2022===

	private String que1Desc;
	private String que2Desc;
	private String que3Desc;
	private String que4Desc;
	private String que5Desc;
	private String que1Id;
	private String que2Id;
	private String que3Id;
	private String que4Id;
	private String que5Id;
	private List<File> que1DescFile;
	private List<String> que1IdFileStatus;
	private List<String> que1DescFileFileName;

	private boolean checkQue1;
	private boolean checkQue2;
	private boolean checkQue3;
	private boolean checkQue4;
	private boolean checkQue5;

	private String empMedicalId1;
	private String empMedicalId2;
	private String empMedicalId3;
	private String empMedicalId4;
	private String empMedicalId5;

	// private String empDesignation;

	List<FillSalutation> salutationList;
	List<FillEmploymentType> empTypeList;
	List<FillApproval> rosterDependencyList;
	List<FillGrade> gradeList;
	List<FillDesig> desigList;
	List<FillLevel> levelList;
	List<FillDepartment> deptList;
	List<FillEmployee> supervisorList;
	List<FillServices> serviceList;
	List<FillCountry> countryList;
	List<FillState> stateList;
	List<FillCity> cityList;
	List<FillWLocation> wLocationList;
	List<FillBank> bankList;
	List<FillGender> empGenderList;
	List<FillProbationDuration> probationDurationList;
	List<FillNoticeDuration> noticeDurationList;
	List<FillLeaveType> leaveTypeList;
	List<FillMaritalStatus> maritalStatusList;
	List<FillBloodGroup> bloodGroupList;
	List<FillDegreeDuration> degreeDurationList;
	List<FillYears> yearsList;
	List<FillSkills> skillsList;
	List<FillEducational> educationalList;

	String empPaymentMode;
	List<FillPayMode> paymentModeList;

	List<FillPayCycleDuration> paycycleDurationList;
	String strPaycycleDuration;

	/*
	 * public void validate() {
	 * 
	 * log.debug("Inside Validate.."); UtilityFunctions uF = new
	 * UtilityFunctions(); session = request.getSession(); CF =
	 * (CommonFunctions)session.getAttribute(CommonFunctions);
	 * loadValidateEmployee(); String referer = request.getHeader("Referer");
	 * strUserType = (String)session.getAttribute(USERTYPE);
	 * request.setAttribute(PAGE, PAddEmployee);
	 * 
	 * if (getMode()!=null && getMode().length()>0 ) {
	 * request.setAttribute(TITLE, TEditEmployee); }else
	 * request.setAttribute(TITLE, TAddEmployee);
	 * 
	 * if (referer != null) { int index1 =
	 * referer.indexOf(request.getContextPath()); int index2 =
	 * request.getContextPath().length(); referer = referer.substring(index1 +
	 * index2 + 1); } setRedirectUrl(referer);
	 * 
	 * if(strUserType.equals(IConstants.ADMIN) ||
	 * strUserType.equals(IConstants.HRMANAGER)) {
	 * 
	 * if (getEmpCodeAlphabet() != null && getEmpCodeAlphabet().length() == 0) {
	 * addFieldError("empCodeAlphabet", "Employee Code Alphabet is required"); }
	 * if (getEmpCodeNumber() != null && getEmpCodeNumber().length() == 0) {
	 * addFieldError("empCodeNumber", "Employee Code Number is required"); }
	 * 
	 * if(getEmpCodeAlphabet() != null && getEmpCodeAlphabet().length() != 0 &&
	 * getEmpCodeNumber() != null && getEmpCodeNumber().length() != 0 &&
	 * !getAutoGenerate() &&uF.parseToInt(getCandidateId())== 0) {
	 * 
	 * Map<String, String> hmEmpCode = CF.getEmpCodeMap1();
	 * 
	 * log.debug("hmEmpCode.get(getEmpCodeAlphabet()+getEmpCodeNumber())====>" +
	 * ""+hmEmpCode.get(getEmpCodeAlphabet()+getEmpCodeNumber()));
	 * 
	 * if(hmEmpCode.get(getEmpCodeAlphabet()+getEmpCodeNumber()) != null) {
	 * 
	 * addFieldError("empCodeAlphabet", "Employee Code already Exist"); }
	 * 
	 * } } if (getEmpFname() != null && getEmpFname().length() == 0) {
	 * addFieldError("empFname", "Employee First Name is required"); } if
	 * (getEmpLname() != null && getEmpLname().length() == 0) {
	 * addFieldError("empLname", "Employee Last Name is required"); } if
	 * (getEmpFname() != null && getEmpLname() != null &&
	 * getEmpFname().length()>0 && getEmpLname().length()>0 &&
	 * getEmpFname().equalsIgnoreCase(getEmpLname())) {
	 * addFieldError("empFLname", "First name and last name can not be same"); }
	 * if (getUserName() != null && getUserName().length() == 0) {
	 * addFieldError("userName", "Username is required"); } if (getEmpPassword()
	 * != null && getEmpPassword().length() == 0) { addFieldError("empPassword",
	 * "Password is required"); } if (getEmpEmail() != null &&
	 * getEmpEmail().length() == 0) { addFieldError("empEmail",
	 * "Employee Email is required"); }else if(getEmpEmail() != null){ Pattern p
	 * = Pattern.compile(".+@.+\\.[a-z]+"); Matcher m=p.matcher(getEmpEmail());
	 * if(!m.matches()){ log.debug("Field Error email");
	 * addFieldError("empEmail", "Please enter valid email address"); } }
	 * 
	 * if (getEmpAddress1() != null && getEmpAddress1().length() == 0) {
	 * addFieldError("empAddress1", "Employee Address1 is required"); } if
	 * (getCountry() != null && uF.parseToInt(getCountry()) == 0) {
	 * addFieldError("country", "Select Country is required"); } if (getState()
	 * != null && uF.parseToInt(getState()) == 0) { addFieldError("state",
	 * "Select State is required"); } if (getCity() != null &&
	 * getCity().length() == 0) { addFieldError("city", "Suburb is required"); }
	 * if(getEmpEmergencyContactNo()!=null &&
	 * getEmpEmergencyContactNo().length() == 0){
	 * addFieldError("empEmergencyContactNo",
	 * "Emergency Contact No is required"); } if (getEmpGender() != null &&
	 * getEmpGender().equals("0")) { addFieldError("empGender",
	 * "Gender is required"); } if (getEmpDateOfBirth() != null &&
	 * getEmpDateOfBirth().length() == 0) { addFieldError("empDateOfBirth",
	 * "Date Of Birth is required"); }
	 * 
	 * if(strUserType.equals(IConstants.ADMIN) ||
	 * strUserType.equals(IConstants.HRMANAGER)) {
	 * 
	 * if (getEmpType() != null && getEmpType().equalsIgnoreCase("0")) {
	 * addFieldError("empType", "Select Employment Type is required"); } if
	 * (getwLocation() != null && uF.parseToInt(getwLocation()) == 0) {
	 * addFieldError("wLocation", "Select Work Location is required"); } if
	 * (getDepartment() != null && uF.parseToInt(getDepartment()) == 0) {
	 * addFieldError("department", "Department is required"); } if
	 * (getEmpGrade() != null && uF.parseToInt(getEmpGrade()) == 0) {
	 * 
	 * addFieldError("empGrade", "Grade is required"); } if (getSupervisor() !=
	 * null && uF.parseToInt(getSupervisor()) == 0) {
	 * addFieldError("supervisor", "Supervisor is required"); } if (
	 * (getEmpFname()!=null && getService()==null) || (getService() != null &&
	 * getService().length==0)) { addFieldError("service",
	 * "Cost-center is required"); } if (getRosterDependency() != null &&
	 * getRosterDependency().equalsIgnoreCase("0")) {
	 * addFieldError("rosterDependency", "Roster Dependency is required"); }
	 * 
	 * if (getAvailFrom() != null && getAvailFrom().equalsIgnoreCase("0")) {
	 * addFieldError("availFrom", "Available from time is required"); }else
	 * if(getAvailFrom() != null && uF.getTimeFormat(getAvailFrom(),
	 * CF.getStrReportTimeFormat())==null){ addFieldError("availFrom",
	 * "Please enter available from time in correct format. For example 12:00PM"
	 * ); }
	 * 
	 * if (getAvailTo() != null && getAvailTo().equalsIgnoreCase("0")) {
	 * addFieldError("availTo", "Available to time is required"); }else
	 * if(getAvailTo() != null && uF.getTimeFormat(getAvailTo(),
	 * CF.getStrReportTimeFormat())==null){ addFieldError("availFrom",
	 * "Please enter available to time in correct format. For example 12:00PM");
	 * }
	 * 
	 * if(getProbationDuration() == -1 ) { addFieldError("probationDuration",
	 * "Probation Duration time is required"); }
	 * log.debug("getProbationLeaves()=-==="+getProbationLeaves());
	 * if(((getEmpFname()!=null && getProbationLeaves()==null) ||
	 * (getProbationLeaves()!=null && getProbationLeaves().length==0))){
	 * addFieldError("probationLeaves", "Probation Leaves are required"); }
	 * 
	 * log.debug("getEmpCodeAlphabet()==>"+getEmpCodeAlphabet());
	 * 
	 * } }
	 */

	private String redirectUrl;

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getEmpMname() {
		return empMname;
	}

	public void setEmpMname(String empMname) {
		this.empMname = empMname;
	}

	public String getEmpFname() {
		return empFname;
	}

	public void setEmpFname(String empFname) {
		this.empFname = empFname;
	}

	public String getEmpLname() {
		return empLname;
	}

	public void setEmpLname(String empLname) {
		this.empLname = empLname;
	}

	public String getEmpAddress1() {
		return empAddress1;
	}

	public void setEmpAddress1(String empAddress1) {
		this.empAddress1 = empAddress1;
	}

	public String getEmpAddress2() {
		return empAddress2;
	}

	public void setEmpAddress2(String empAddress2) {
		this.empAddress2 = empAddress2;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getEmpPincode() {
		return empPincode;
	}

	public void setEmpPincode(String empPincode) {
		this.empPincode = empPincode;
	}

	public String getEmpContactno() {
		return empContactno;
	}

	public void setEmpContactno(String empContactno) {
		this.empContactno = empContactno;
	}

	public String getEmpPanNo() {
		return empPanNo;
	}

	public void setEmpPanNo(String empPanNo) {
		this.empPanNo = empPanNo;
	}

	public String getEmpGender() {
		return empGender;
	}

	public void setEmpGender(String empGender) {
		this.empGender = empGender;
	}

	public void setEmpDateOfBirth(String empDateOfBirth) {
		this.empDateOfBirth = empDateOfBirth;
	}

	public String getEmpDateOfBirth() {
		return empDateOfBirth;
	}

	public void setEmpBankName(String empBankName) {
		this.empBankName = empBankName;
	}

	public String getEmpBankName() {
		return empBankName;
	}

	public String getwLocation() {
		return wLocation;
	}

	public void setwLocation(String wLocation) {
		this.wLocation = wLocation;
	}

	// public String getDesignation() {
	// return designation;
	// }
	//
	// public void setDesignation(String designation) {
	// this.designation = designation;
	// }

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public String[] getService() {
		return service;
	}

	public void setService(String[] service) {
		this.service = service;
	}

	public String getAvailFrom() {
		return availFrom;
	}

	public void setAvailFrom(String availFrom) {
		this.availFrom = availFrom;
	}

	public String getAvailTo() {
		return availTo;
	}

	public void setAvailTo(String availTo) {
		this.availTo = availTo;
	}

	public List<FillWLocation> getWLocationList() {
		return wLocationList;
	}

	// public List<FillDesig> getDesigList() {
	// return desigList;
	// }
	//
	// public void setDesigList(List<FillDesig> desigList) {
	// this.desigList = desigList;
	// }

	public List<FillDepartment> getDeptList() {
		return deptList;
	}

	public void setDeptList(List<FillDepartment> deptList) {
		this.deptList = deptList;
	}

	public List<FillEmployee> getSupervisorList() {
		return supervisorList;
	}

	public void setSupervisorList(List<FillEmployee> supervisorList) {
		this.supervisorList = supervisorList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public List<FillCountry> getCountryList() {
		return countryList;
	}

	public void setCountryList(List<FillCountry> countryList) {
		this.countryList = countryList;
	}

	public List<FillState> getStateList() {
		return stateList;
	}

	public void setStateList(List<FillState> stateList) {
		this.stateList = stateList;
	}

	public List<FillCity> getCityList() {
		return cityList;
	}

	public void setCityList(List<FillCity> cityList) {
		this.cityList = cityList;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getRosterDependency() {
		return rosterDependency;
	}

	public void setRosterDependency(String rosterDependency) {
		this.rosterDependency = rosterDependency;
	}

	public List<FillApproval> getRosterDependencyList() {
		return rosterDependencyList;
	}

	public String getEmpType() {
		return empType;
	}

	public List<FillSalutation> getSalutationList() {
		return salutationList;
	}

	public void setSalutationList(List<FillSalutation> salutationList) {
		this.salutationList = salutationList;
	}

	public void setEmpType(String empType) {
		this.empType = empType;
	}

	public List<FillEmploymentType> getEmpTypeList() {
		return empTypeList;
	}

	public String getEmpEmail() {
		return empEmail;
	}

	public void setEmpEmail(String empEmail) {
		this.empEmail = empEmail;
	}

	public boolean getIsFirstAidAllowance() {
		return isFirstAidAllowance;
	}

	public void setIsFirstAidAllowance(boolean isFirstAidAllowance) {
		this.isFirstAidAllowance = isFirstAidAllowance;
	}

	public void setEmpImage(File empImage) {
		this.empImage = empImage;
	}

	public File getEmpImage() {
		return empImage;
	}

	public String getEmpImageFileName() {
		return empImageFileName;
	}

	public void setEmpImageFileName(String empImageFileName) {
		this.empImageFileName = empImageFileName;
	}

	public String getEmpPassword() {
		return empPassword;
	}

	public void setEmpPassword(String empPassword) {
		this.empPassword = empPassword;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmpStartDate() {
		return empStartDate;
	}

	public void setEmpStartDate(String empStartDate) {
		this.empStartDate = empStartDate;
	}

	public String getEmpUserTypeId() {
		return empUserTypeId;
	}

	public void setEmpUserTypeId(String empUserTypeId) {
		this.empUserTypeId = empUserTypeId;
	}

	public String getEmpBankAcctNbr() {
		return empBankAcctNbr;
	}

	public void setEmpBankAcctNbr(String empBankAcctNbr) {
		this.empBankAcctNbr = empBankAcctNbr;
	}

	public List<FillGender> getEmpGenderList() {
		return empGenderList;
	}

	public void setEmpGenderList(List<FillGender> empGenderList) {
		this.empGenderList = empGenderList;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public List<FillProbationDuration> getProbationDurationList() {
		return probationDurationList;
	}

	public void setProbationDurationList(List<FillProbationDuration> probationDurationList) {
		this.probationDurationList = probationDurationList;
	}

	public List<FillLeaveType> getLeaveTypeList() {
		return leaveTypeList;
	}

	public void setLeaveTypeList(List<FillLeaveType> leaveTypeList) {
		this.leaveTypeList = leaveTypeList;
	}

	public int getProbationDuration() {
		return probationDuration;
	}

	public void setProbationDuration(int probationDuration) {
		this.probationDuration = probationDuration;
	}

	public String getEmpEmailSec() {
		return empEmailSec;
	}

	public void setEmpEmailSec(String empEmailSec) {
		this.empEmailSec = empEmailSec;
	}

	public String getSkypeId() {
		return skypeId;
	}

	public void setSkypeId(String skypeId) {
		this.skypeId = skypeId;
	}

	public String getEmpMobileNo() {
		return empMobileNo;
	}

	public void setEmpMobileNo(String empMobileNo) {
		this.empMobileNo = empMobileNo;
	}

	public String[] getProbationLeaves() {
		return probationLeaves;
	}

	public void setProbationLeaves(String[] probationLeaves) {
		this.probationLeaves = probationLeaves;
	}

	public String[] getHobbyName() {
		return hobbyName;
	}

	public void setHobbyName(String[] hobbyName) {
		this.hobbyName = hobbyName;
	}

	public String[] getDocumentNames() {
		return documentNames;
	}

	public void setDocumentNames(String[] documentNames) {
		this.documentNames = documentNames;
	}

	public String[] getDocumentValues() {
		return documentValues;
	}

	public void setDocumentValues(String[] documentValues) {
		this.documentValues = documentValues;
	}

	public String[] getLanguageName() {
		return languageName;
	}

	public void setLanguageName(String[] languageName) {
		this.languageName = languageName;
	}

	public String[] getIsRead() {
		return isRead;
	}

	public void setIsRead(String[] isRead) {
		this.isRead = isRead;
	}

	public String[] getIsWrite() {
		return isWrite;
	}

	public void setIsWrite(String[] isWrite) {
		this.isWrite = isWrite;
	}

	public String[] getIsSpeak() {
		return isSpeak;
	}

	public void setIsSpeak(String[] isSpeak) {
		this.isSpeak = isSpeak;
	}

	public String[] getDegreeName() {
		return degreeName;
	}

	public void setDegreeName(String[] degreeName) {
		this.degreeName = degreeName;
	}

	public String[] getDegreeDuration() {
		return degreeDuration;
	}

	public void setDegreeDuration(String[] degreeDuration) {
		this.degreeDuration = degreeDuration;
	}

	public String[] getCompletionYear() {
		return completionYear;
	}

	public void setCompletionYear(String[] completionYear) {
		this.completionYear = completionYear;
	}

	public String[] getGrade() {
		return grade;
	}

	public void setGrade(String[] grade) {
		this.grade = grade;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getFatherDob() {
		return fatherDob;
	}

	public void setFatherDob(String fatherDob) {
		this.fatherDob = fatherDob;
	}

	public String getFatherEducation() {
		return fatherEducation;
	}

	public void setFatherEducation(String fatherEducation) {
		this.fatherEducation = fatherEducation;
	}

	public String getFatherOccupation() {
		return fatherOccupation;
	}

	public void setFatherOccupation(String fatherOccupation) {
		this.fatherOccupation = fatherOccupation;
	}

	public String getFatherContactNumber() {
		return fatherContactNumber;
	}

	public void setFatherContactNumber(String fatherContactNumber) {
		this.fatherContactNumber = fatherContactNumber;
	}

	public String getFatherEmailId() {
		return fatherEmailId;
	}

	public void setFatherEmailId(String fatherEmailId) {
		this.fatherEmailId = fatherEmailId;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getMotherDob() {
		return motherDob;
	}

	public void setMotherDob(String motherDob) {
		this.motherDob = motherDob;
	}

	public String getMotherEducation() {
		return motherEducation;
	}

	public void setMotherEducation(String motherEducation) {
		this.motherEducation = motherEducation;
	}

	public String getMotherOccupation() {
		return motherOccupation;
	}

	public void setMotherOccupation(String motherOccupation) {
		this.motherOccupation = motherOccupation;
	}

	public String getMotherContactNumber() {
		return motherContactNumber;
	}

	public void setMotherContactNumber(String motherContactNumber) {
		this.motherContactNumber = motherContactNumber;
	}

	public String getMotherEmailId() {
		return motherEmailId;
	}

	public void setMotherEmailId(String motherEmailId) {
		this.motherEmailId = motherEmailId;
	}

	public String[] getMemberName() {
		return memberName;
	}

	public void setMemberName(String[] memberName) {
		this.memberName = memberName;
	}

	public String[] getMemberDob() {
		return memberDob;
	}

	public void setMemberDob(String[] memberDob) {
		this.memberDob = memberDob;
	}

	public String[] getMemberEducation() {
		return memberEducation;
	}

	public void setMemberEducation(String[] memberEducation) {
		this.memberEducation = memberEducation;
	}

	public String[] getMemberOccupation() {
		return memberOccupation;
	}

	public void setMemberOccupation(String[] memberOccupation) {
		this.memberOccupation = memberOccupation;
	}

	public String[] getMemberContactNumber() {
		return memberContactNumber;
	}

	public void setMemberContactNumber(String[] memberContactNumber) {
		this.memberContactNumber = memberContactNumber;
	}

	public String[] getMemberEmailId() {
		return memberEmailId;
	}

	public void setMemberEmailId(String[] memberEmailId) {
		this.memberEmailId = memberEmailId;
	}

	public String[] getMemberGender() {
		return memberGender;
	}

	public void setMemberGender(String[] memberGender) {
		this.memberGender = memberGender;
	}

	public String[] getPrevCompanyName() {
		return prevCompanyName;
	}

	public void setPrevCompanyName(String[] prevCompanyName) {
		this.prevCompanyName = prevCompanyName;
	}

	public String[] getPrevCompanyLocation() {
		return prevCompanyLocation;
	}

	public void setPrevCompanyLocation(String[] prevCompanyLocation) {
		this.prevCompanyLocation = prevCompanyLocation;
	}

	public String[] getPrevCompanyCity() {
		return prevCompanyCity;
	}

	public void setPrevCompanyCity(String[] prevCompanyCity) {
		this.prevCompanyCity = prevCompanyCity;
	}

	public String[] getPrevCompanyState() {
		return prevCompanyState;
	}

	public void setPrevCompanyState(String[] prevCompanyState) {
		this.prevCompanyState = prevCompanyState;
	}

	public String[] getPrevCompanyCountry() {
		return prevCompanyCountry;
	}

	public void setPrevCompanyCountry(String[] prevCompanyCountry) {
		this.prevCompanyCountry = prevCompanyCountry;
	}

	public String[] getPrevCompanyContactNo() {
		return prevCompanyContactNo;
	}

	public void setPrevCompanyContactNo(String[] prevCompanyContactNo) {
		this.prevCompanyContactNo = prevCompanyContactNo;
	}

	public String[] getPrevCompanyReportingTo() {
		return prevCompanyReportingTo;
	}

	public void setPrevCompanyReportingTo(String[] prevCompanyReportingTo) {
		this.prevCompanyReportingTo = prevCompanyReportingTo;
	}

	public String[] getPrevCompanyReportManagerPhNo() {
		return prevCompanyReportManagerPhNo;
	}

	public void setPrevCompanyReportManagerPhNo(String[] prevCompanyReportManagerPhNo) {
		this.prevCompanyReportManagerPhNo = prevCompanyReportManagerPhNo;
	}

	public String[] getPrevCompanyHRManager() {
		return prevCompanyHRManager;
	}

	public void setPrevCompanyHRManager(String[] prevCompanyHRManager) {
		this.prevCompanyHRManager = prevCompanyHRManager;
	}

	public String[] getPrevCompanyHRManagerPhNo() {
		return prevCompanyHRManagerPhNo;
	}

	public void setPrevCompanyHRManagerPhNo(String[] prevCompanyHRManagerPhNo) {
		this.prevCompanyHRManagerPhNo = prevCompanyHRManagerPhNo;
	}

	public String[] getPrevCompanyFromDate() {
		return prevCompanyFromDate;
	}

	public void setPrevCompanyFromDate(String[] prevCompanyFromDate) {
		this.prevCompanyFromDate = prevCompanyFromDate;
	}

	public String[] getPrevCompanyToDate() {
		return prevCompanyToDate;
	}

	public void setPrevCompanyToDate(String[] prevCompanyToDate) {
		this.prevCompanyToDate = prevCompanyToDate;
	}

	public String[] getPrevCompanyDesination() {
		return prevCompanyDesination;
	}

	public void setPrevCompanyDesination(String[] prevCompanyDesination) {
		this.prevCompanyDesination = prevCompanyDesination;
	}

	public String[] getPrevCompanyResponsibilities() {
		return prevCompanyResponsibilities;
	}

	public void setPrevCompanyResponsibilities(String[] prevCompanyResponsibilities) {
		this.prevCompanyResponsibilities = prevCompanyResponsibilities;
	}

	public String[] getPrevCompanySkills() {
		return prevCompanySkills;
	}

	public void setPrevCompanySkills(String[] prevCompanySkills) {
		this.prevCompanySkills = prevCompanySkills;
	}

	public String[] getSkillName() {
		return skillName;
	}

	public void setSkillName(String[] skillName) {
		this.skillName = skillName;
	}

	public String[] getSkillValue() {
		return skillValue;
	}

	public void setSkillValue(String[] skillValue) {
		this.skillValue = skillValue;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public String getEmpGrade() {
		return empGrade;
	}

	public void setEmpGrade(String empGrade) {
		this.empGrade = empGrade;
	}

	public String getEmpEmergencyContactName() {
		return empEmergencyContactName;
	}

	public void setEmpEmergencyContactName(String empEmergencyContactName) {
		this.empEmergencyContactName = empEmergencyContactName;
	}

	public String getEmpEmergencyContactNo() {
		return empEmergencyContactNo;
	}

	public void setEmpEmergencyContactNo(String empEmergencyContactNo) {
		this.empEmergencyContactNo = empEmergencyContactNo;
	}

	public String getEmpPassportNo() {
		return empPassportNo;
	}

	public void setEmpPassportNo(String empPassportNo) {
		this.empPassportNo = empPassportNo;
	}

	public String getEmpPassportExpiryDate() {
		return empPassportExpiryDate;
	}

	public void setEmpPassportExpiryDate(String empPassportExpiryDate) {
		this.empPassportExpiryDate = empPassportExpiryDate;
	}

	public String getEmpBloodGroup() {
		return empBloodGroup;
	}

	public void setEmpBloodGroup(String empBloodGroup) {
		this.empBloodGroup = empBloodGroup;
	}

	public String getEmpMaritalStatus() {
		return empMaritalStatus;
	}

	public void setEmpMaritalStatus(String empMaritalStatus) {
		this.empMaritalStatus = empMaritalStatus;
	}

	public List<FillMaritalStatus> getMaritalStatusList() {
		return maritalStatusList;
	}

	public void setMaritalStatusList(List<FillMaritalStatus> maritalStatusList) {
		this.maritalStatusList = maritalStatusList;
	}

	public List<FillBloodGroup> getBloodGroupList() {
		return bloodGroupList;
	}

	public void setBloodGroupList(List<FillBloodGroup> bloodGroupList) {
		this.bloodGroupList = bloodGroupList;
	}

	public List<FillDegreeDuration> getDegreeDurationList() {
		return degreeDurationList;
	}

	public void setDegreeDurationList(List<FillDegreeDuration> degreeDurationList) {
		this.degreeDurationList = degreeDurationList;
	}

	public List<FillYears> getYearsList() {
		return yearsList;
	}

	public void setYearsList(List<FillYears> yearsList) {
		this.yearsList = yearsList;
	}

	public String getSpouseName() {
		return spouseName;
	}

	public void setSpouseName(String spouseName) {
		this.spouseName = spouseName;
	}

	public String getSpouseDob() {
		return spouseDob;
	}

	public void setSpouseDob(String spouseDob) {
		this.spouseDob = spouseDob;
	}

	public String getSpouseEducation() {
		return spouseEducation;
	}

	public void setSpouseEducation(String spouseEducation) {
		this.spouseEducation = spouseEducation;
	}

	public String getSpouseContactNumber() {
		return spouseContactNumber;
	}

	public void setSpouseContactNumber(String spouseContactNumber) {
		this.spouseContactNumber = spouseContactNumber;
	}

	public String getSpouseEmailId() {
		return spouseEmailId;
	}

	public void setSpouseEmailId(String spouseEmailId) {
		this.spouseEmailId = spouseEmailId;
	}

	public String getSpouseOccupation() {
		return spouseOccupation;
	}

	public void setSpouseOccupation(String spouseOccupation) {
		this.spouseOccupation = spouseOccupation;
	}

	public String getSpouseGender() {
		return spouseGender;
	}

	public void setSpouseGender(String spouseGender) {
		this.spouseGender = spouseGender;
	}

	public String getQue1Desc() {
		return que1Desc;
	}

	public void setQue1Desc(String que1Desc) {
		this.que1Desc = que1Desc;
	}

	public String getQue2Desc() {
		return que2Desc;
	}

	public void setQue2Desc(String que2Desc) {
		this.que2Desc = que2Desc;
	}

	public String getQue3Desc() {
		return que3Desc;
	}

	public void setQue3Desc(String que3Desc) {
		this.que3Desc = que3Desc;
	}

	public String getQue4Desc() {
		return que4Desc;
	}

	public void setQue4Desc(String que4Desc) {
		this.que4Desc = que4Desc;
	}

	public String getQue5Desc() {
		return que5Desc;
	}

	public void setQue5Desc(String que5Desc) {
		this.que5Desc = que5Desc;
	}

	public String getQue1Id() {
		return que1Id;
	}

	public void setQue1Id(String que1Id) {
		this.que1Id = que1Id;
	}

	public String getQue2Id() {
		return que2Id;
	}

	public void setQue2Id(String que2Id) {
		this.que2Id = que2Id;
	}

	public String getQue3Id() {
		return que3Id;
	}

	public void setQue3Id(String que3Id) {
		this.que3Id = que3Id;
	}

	public String getQue4Id() {
		return que4Id;
	}

	public void setQue4Id(String que4Id) {
		this.que4Id = que4Id;
	}

	public String getQue5Id() {
		return que5Id;
	}

	public void setQue5Id(String que5Id) {
		this.que5Id = que5Id;
	}

	public boolean isCheckQue1() {
		return checkQue1;
	}

	public void setCheckQue1(boolean checkQue1) {
		this.checkQue1 = checkQue1;
	}

	public boolean isCheckQue2() {
		return checkQue2;
	}

	public void setCheckQue2(boolean checkQue2) {
		this.checkQue2 = checkQue2;
	}

	public boolean isCheckQue3() {
		return checkQue3;
	}

	public void setCheckQue3(boolean checkQue3) {
		this.checkQue3 = checkQue3;
	}

	public boolean isCheckQue4() {
		return checkQue4;
	}

	public void setCheckQue4(boolean checkQue4) {
		this.checkQue4 = checkQue4;
	}

	public boolean isCheckQue5() {
		return checkQue5;
	}

	public void setCheckQue5(boolean checkQue5) {
		this.checkQue5 = checkQue5;
	}

	public String getEmpMedicalId1() {
		return empMedicalId1;
	}

	public void setEmpMedicalId1(String empMedicalId1) {
		this.empMedicalId1 = empMedicalId1;
	}

	public String getEmpMedicalId2() {
		return empMedicalId2;
	}

	public void setEmpMedicalId2(String empMedicalId2) {
		this.empMedicalId2 = empMedicalId2;
	}

	public String getEmpMedicalId3() {
		return empMedicalId3;
	}

	public void setEmpMedicalId3(String empMedicalId3) {
		this.empMedicalId3 = empMedicalId3;
	}

	public String getEmpMedicalId4() {
		return empMedicalId4;
	}

	public void setEmpMedicalId4(String empMedicalId4) {
		this.empMedicalId4 = empMedicalId4;
	}

	public String getEmpMedicalId5() {
		return empMedicalId5;
	}

	public void setEmpMedicalId5(String empMedicalId5) {
		this.empMedicalId5 = empMedicalId5;
	}

	public List<FillSkills> getSkillsList() {
		return skillsList;
	}

	public void setSkillsList(List<FillSkills> skillsList) {
		this.skillsList = skillsList;
	}

	/*
	 * public String getStrEdit() { return strEdit; }
	 * 
	 * public void setStrEdit(String strEdit) { this.strEdit = strEdit; }
	 */

	public List<File> getQue1DescFile() {
		return que1DescFile;
	}

	public void setQue1DescFile(List<File> que1DescFile) {
		this.que1DescFile = que1DescFile;
	}

	public List<String> getQue1IdFileStatus() {
		return que1IdFileStatus;
	}

	public void setQue1IdFileStatus(List<String> que1IdFileStatus) {
		this.que1IdFileStatus = que1IdFileStatus;
	}

	public List<String> getQue1DescFileFileName() {
		return que1DescFileFileName;
	}

	public void setQue1DescFileFileName(List<String> que1DescFileFileName) {
		this.que1DescFileFileName = que1DescFileFileName;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getEmpCodeAlphabet() {
		return empCodeAlphabet;
	}

	public void setEmpCodeAlphabet(String empCodeAlphabet) {
		this.empCodeAlphabet = empCodeAlphabet;
	}

	public String getEmpCodeNumber() {
		return empCodeNumber;
	}

	public void setEmpCodeNumber(String empCodeNumber) {
		this.empCodeNumber = empCodeNumber;
	}

	public Boolean getAutoGenerate() {
		return autoGenerate;
	}

	public void setAutoGenerate(Boolean autoGenerate) {
		this.autoGenerate = autoGenerate;
	}

	public String getRef1Name() {
		return ref1Name;
	}

	public void setRef1Name(String ref1Name) {
		this.ref1Name = ref1Name;
	}

	public String getRef1Company() {
		return ref1Company;
	}

	public void setRef1Company(String ref1Company) {
		this.ref1Company = ref1Company;
	}

	public String getRef1Designation() {
		return ref1Designation;
	}

	public void setRef1Designation(String ref1Designation) {
		this.ref1Designation = ref1Designation;
	}

	public String getRef1ContactNo() {
		return ref1ContactNo;
	}

	public void setRef1ContactNo(String ref1ContactNo) {
		this.ref1ContactNo = ref1ContactNo;
	}

	public String getRef1Email() {
		return ref1Email;
	}

	public void setRef1Email(String ref1Email) {
		this.ref1Email = ref1Email;
	}

	public String getRef2Name() {
		return ref2Name;
	}

	public void setRef2Name(String ref2Name) {
		this.ref2Name = ref2Name;
	}

	public String getRef2Company() {
		return ref2Company;
	}

	public void setRef2Company(String ref2Company) {
		this.ref2Company = ref2Company;
	}

	public String getRef2Designation() {
		return ref2Designation;
	}

	public void setRef2Designation(String ref2Designation) {
		this.ref2Designation = ref2Designation;
	}

	public String getRef2ContactNo() {
		return ref2ContactNo;
	}

	public void setRef2ContactNo(String ref2ContactNo) {
		this.ref2ContactNo = ref2ContactNo;
	}

	public String getRef2Email() {
		return ref2Email;
	}

	public void setRef2Email(String ref2Email) {
		this.ref2Email = ref2Email;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return ServiceName;
	}

	public void setServiceName(String serviceName) {
		ServiceName = serviceName;
	}

	public boolean isApprovedFlag() {
		return approvedFlag;
	}

	public void setApprovedFlag(boolean approvedFlag) {
		this.approvedFlag = approvedFlag;
	}

	public Timestamp getEmpFilledFlagDate() {
		return empFilledFlagDate;
	}

	public void setEmpFilledFlagDate(Timestamp empFilledFlagDate) {
		this.empFilledFlagDate = empFilledFlagDate;
	}

	public List<FillNoticeDuration> getNoticeDurationList() {
		return noticeDurationList;
	}

	public void setNoticeDurationList(List<FillNoticeDuration> noticeDurationList) {
		this.noticeDurationList = noticeDurationList;
	}

	public int getNoticeDuration() {
		return noticeDuration;
	}

	public void setNoticeDuration(int noticeDuration) {
		this.noticeDuration = noticeDuration;
	}

	public String getEmpPFNo() {
		return empPFNo;
	}

	public void setEmpPFNo(String empPFNo) {
		this.empPFNo = empPFNo;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public String getStrDesignation() {
		return strDesignation;
	}

	public void setStrDesignation(String strDesignation) {
		this.strDesignation = strDesignation;
	}

	public String getAttendanceDependency() {
		return attendanceDependency;
	}

	public void setAttendanceDependency(String attendanceDependency) {
		this.attendanceDependency = attendanceDependency;
	}

	public String getEmpGPFNo() {
		return empGPFNo;
	}

	public void setEmpGPFNo(String empGPFNo) {
		this.empGPFNo = empGPFNo;
	}

	public String[] getStrTime() {
		return strTime;
	}

	public void setStrTime(String[] strTime) {
		this.strTime = strTime;
	}

	public String[] getStrDate() {
		return strDate;
	}

	public void setStrDate(String[] strDate) {
		this.strDate = strDate;
	}

	public List<FillPayCycleDuration> getPaycycleDurationList() {
		return paycycleDurationList;
	}

	public void setPaycycleDurationList(List<FillPayCycleDuration> paycycleDurationList) {
		this.paycycleDurationList = paycycleDurationList;
	}

	public String getStrPaycycleDuration() {
		return strPaycycleDuration;
	}

	public void setStrPaycycleDuration(String strPaycycleDuration) {
		this.strPaycycleDuration = strPaycycleDuration;
	}

	public String getEmpDateOfMarriage() {
		return empDateOfMarriage;
	}

	public void setEmpDateOfMarriage(String empDateOfMarriage) {
		this.empDateOfMarriage = empDateOfMarriage;
	}

	public String[] getEmpKra() {
		return empKra;
	}

	public void setEmpKra(String[] empKra) {
		this.empKra = empKra;
	}

	public String[] getEmpKraId() {
		return empKraId;
	}

	public void setEmpKraId(String[] empKraId) {
		this.empKraId = empKraId;
	}

	public String getEmpAddress1Tmp() {
		return empAddress1Tmp;
	}

	public void setEmpAddress1Tmp(String empAddress1Tmp) {
		this.empAddress1Tmp = empAddress1Tmp;
	}

	public String getEmpAddress2Tmp() {
		return empAddress2Tmp;
	}

	public void setEmpAddress2Tmp(String empAddress2Tmp) {
		this.empAddress2Tmp = empAddress2Tmp;
	}

	public String getCountryTmp() {
		return countryTmp;
	}

	public void setCountryTmp(String countryTmp) {
		this.countryTmp = countryTmp;
	}

	public String getStateTmp() {
		return stateTmp;
	}

	public void setStateTmp(String stateTmp) {
		this.stateTmp = stateTmp;
	}

	public String getCityTmp() {
		return cityTmp;
	}

	public void setCityTmp(String cityTmp) {
		this.cityTmp = cityTmp;
	}

	public String getEmpPincodeTmp() {
		return empPincodeTmp;
	}

	public void setEmpPincodeTmp(String empPincodeTmp) {
		this.empPincodeTmp = empPincodeTmp;
	}

	public List<FillBank> getBankList() {
		return bankList;
	}

	public void setBankList(List<FillBank> bankList) {
		this.bankList = bankList;
	}

	public String getEmpPaymentMode() {
		return empPaymentMode;
	}

	public void setEmpPaymentMode(String empPaymentMode) {
		this.empPaymentMode = empPaymentMode;
	}

	public List<FillPayMode> getPaymentModeList() {
		return paymentModeList;
	}

	public void setPaymentModeList(List<FillPayMode> paymentModeList) {
		this.paymentModeList = paymentModeList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	// for record particular job id
	String recruitId;

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	String jobcode;

	public String getJobcode() {
		return jobcode;
	}

	public void setJobcode(String jobcode) {
		this.jobcode = jobcode;
	}

//	public List<List<String>> selectEducation(Connection con, int empId) {
//
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		List<List<String>> alEducation = new ArrayList<List<String>>();
//
//		try {
//
//			pst = con.prepareStatement("SELECT * FROM candidate_education_details WHERE emp_id = ?");
//			pst.setInt(1, empId);
//			log.debug("pst=>" + pst);
//			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			while (rs.next()) {
//
//				List<String> alInner = new ArrayList<String>();
//
//				alInner.add(rs.getString("degree_id"));
//				alInner.add(rs.getString("degree_name"));
//				alInner.add(rs.getString("degree_duration"));
//				alInner.add(rs.getString("completion_year"));
//				alInner.add(rs.getString("grade"));
//				alEducation.add(alInner);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " + e.getMessage(), e);
//		}
//		return alEducation;
//
//	}

//	public List<List<String>> selectLanguages(Connection con, int EmpId) {
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		List<List<String>> alLanguages = new ArrayList<List<String>>();
//
//		try {
//			pst = con.prepareStatement("SELECT * FROM candidate_languages_details WHERE emp_id = ?");
//			pst.setInt(1, EmpId);
//			log.debug("pst=>" + pst);
//			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			while (rs.next()) {
//				List<String> alInner = new ArrayList<String>();
//				alInner.add(rs.getString("language_id"));
//				alInner.add(rs.getString("language_name"));
//				alInner.add(rs.getString("language_read"));
//				alInner.add(rs.getString("language_write"));
//				alInner.add(rs.getString("language_speak"));
//				alLanguages.add(alInner);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " + e.getMessage(), e);
//		}
//		log.debug("selectLanguages: alLanguages==>" + alLanguages);
//		return alLanguages;
//
//	}

//	public List<List<String>> selectHobbies(Connection con, int empId) {
//
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		List<List<String>> alHobbies = new ArrayList<List<String>>();
//
//		try {
//			pst = con.prepareStatement("SELECT * FROM candidate_hobbies_details WHERE emp_id=? ORDER BY hobbies_name");
//			pst.setInt(1, empId);
//			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			while (rs.next()) {
//				List<String> alInner1 = new ArrayList<String>();
//				alInner1.add(rs.getInt("hobbies_id") + "");
//				alInner1.add(rs.getString("hobbies_name"));
//				alInner1.add(rs.getInt("emp_id") + "");
//				alHobbies.add(alInner1);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " + e.getMessage(), e);
//		}
//		return alHobbies;
//
//	}

//	public String selectSkills(Connection con, int EmpId, List<List<String>> alSkills) {
//
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		StringBuilder sb = new StringBuilder();
//		String str = "";
//		try {
//			pst = con.prepareStatement("SELECT * FROM candidate_skills_description WHERE emp_id=? ORDER BY skills_name");
//			pst.setInt(1, EmpId);
//			rs = pst.executeQuery();
//			int count = 0;
//			System.out.println("new Date ===> " + new Date());
//			while (rs.next()) {
//
//				List<String> alInner1 = new ArrayList<String>();
//				alInner1.add(rs.getInt("skills_id") + "");
//				if (count == 0) {
//					alInner1.add(rs.getString("skills_name"));
//				} else {
//					alInner1.add(rs.getString("skills_name"));
//				}
//				alInner1.add(rs.getString("skills_value"));
//				alInner1.add(rs.getInt("emp_id") + "");
//
//				if (alSkills != null) {
//					alSkills.add(alInner1);
//				}
//
//				sb.append(rs.getString("skills_name") + ((count == 0) ? " [Pri]" : "") + ", ");
//
//				count++;
//			}
//
//			int index = sb.lastIndexOf(",");
//			if (index > 0) {
//				str = sb.substring(0, index);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " + e.getMessage(), e);
//		}
//		return str;
//
//	}

	public List<List<String>> selectPrevEmploment(Connection con, int empId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<String>> alPrevEmployment = new ArrayList<List<String>>();
		UtilityFunctions uF = new UtilityFunctions();

		try {
			pst = con.prepareStatement("SELECT * FROM candidate_prev_employment WHERE emp_id = ? order by from_date");
			pst.setInt(1, empId);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("company_id"));
				alInner.add(rs.getString("company_name"));
				alInner.add(rs.getString("company_location"));
				alInner.add(rs.getString("company_city"));
				alInner.add(rs.getString("company_state"));
				alInner.add(rs.getString("company_country"));
				alInner.add(rs.getString("company_contact_no"));
				alInner.add(rs.getString("reporting_to"));
				alInner.add(rs.getString("report_manager_ph_no"));
				alInner.add(rs.getString("hr_manager"));
				alInner.add(rs.getString("hr_manager_ph_no"));
				alInner.add(uF.getDateFormat(rs.getString("from_date"), DBDATE, DATE_FORMAT));
				alInner.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, DATE_FORMAT));
				alInner.add(rs.getString("designation"));
				alInner.add(rs.getString("responsibilities"));
				alInner.add(rs.getString("skills"));
				
				alInner.add(uF.showData(rs.getString("experience_letter"), "-"));		//16
			//===start parvez date: 08-08-2022===
				alInner.add(rs.getString("emp_esic_no"));		//17
				alInner.add(rs.getString("uan_no"));		//18
			//===end parvez date: 08-08-2022===
				alPrevEmployment.add(alInner);
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		}

		return alPrevEmployment;
	}

	public List<List<Object>> selectOtherDocuments(Connection con, int empId, String filePath) {
	
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<Object>> alOtherDocuments = new ArrayList<List<Object>>();
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			Map<String, String> hmEmpCodeName = CF.getExistingEmpNameMap(con);
			pst = con.prepareStatement("select * from candidate_documents_details where emp_id = ? and documents_type = ?");
			pst.setInt(1, empId);
			pst.setString(2, "others");
			
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				ArrayList<Object> alInner1 = new ArrayList<Object>();
				alInner1.add(rs.getInt("documents_id") + "");
				alInner1.add(rs.getString("documents_name"));
				alInner1.add(rs.getString("documents_type"));
				alInner1.add(rs.getInt("emp_id") + "");
				alInner1.add(rs.getString("documents_file_name"));
				
				alInner1.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
				alInner1.add(uF.showData(hmEmpCodeName.get(rs.getString("added_by")), "-"));
				
				alOtherDocuments.add(alInner1);
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		}
		return alOtherDocuments;
	}
	
	public List<List<Object>> selectDocuments(Connection con, int empId, String filePath) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<Object>> alDocuments = new ArrayList<List<Object>>();
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			Map<String, String> hmEmpCodeName = CF.getExistingEmpNameMap(con);
			pst = con.prepareStatement("select * from candidate_documents_details where emp_id = ?");
			pst.setInt(1, empId);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				ArrayList<Object> alInner1 = new ArrayList<Object>();
				alInner1.add(rs.getInt("documents_id") + "");
				alInner1.add(rs.getString("documents_name"));
				alInner1.add(rs.getString("documents_type"));
				alInner1.add(rs.getInt("emp_id") + "");
				alInner1.add(rs.getString("documents_file_name"));
				
				alInner1.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
				alInner1.add(uF.showData(hmEmpCodeName.get(rs.getString("added_by")), "-"));
				
				alDocuments.add(alInner1);
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		}
		return alDocuments;

	}

	public List<FillEducational> getEducationalList() {
		return educationalList;
	}

	public void setEducationalList(List<FillEducational> educationalList) {
		this.educationalList = educationalList;
	}

	String degreeNameOther;

	public String getDegreeNameOther() {
		return degreeNameOther;
	}

	public void setDegreeNameOther(String degreeNameOther) {
		this.degreeNameOther = degreeNameOther;
	}

	public String getOrg_id() {
		return org_id;
	}

	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}

	public String getCandidateId() {
		return CandidateId;
	}

	public void setCandidateId(String candidateId) {
		CandidateId = candidateId;
	}

	String type;

	// String stepSubmit;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getShow() {
		return show;
	}

	public void setShow(String show) {
		this.show = show;
	}

	public String getCandibymail() {
		return candibymail;
	}

	public void setCandibymail(String candibymail) {
		this.candibymail = candibymail;
	}

	public String getRefEmpCode() {
		return refEmpCode;
	}

	public void setRefEmpCode(String refEmpCode) {
		this.refEmpCode = refEmpCode;
	}

	public String getOtherRefSrc() {
		return otherRefSrc;
	}

	public void setOtherRefSrc(String otherRefSrc) {
		this.otherRefSrc = otherRefSrc;
	}

	public String getCandiSource() {
		return candiSource;
	}

	public void setCandiSource(String candiSource) {
		this.candiSource = candiSource;
	}

	public String getIsEmpCode() {
		return isEmpCode;
	}

	public void setIsEmpCode(String isEmpCode) {
		this.isEmpCode = isEmpCode;
	}

	public String getRefEmpId() {
		return refEmpId;
	}

	public void setRefEmpId(String refEmpId) {
		this.refEmpId = refEmpId;
	}

	public String getCandiCurrCTC() {
		return candiCurrCTC;
	}

	public void setCandiCurrCTC(String candiCurrCTC) {
		this.candiCurrCTC = candiCurrCTC;
	}

	public String getCandiExpectedCTC() {
		return candiExpectedCTC;
	}

	public void setCandiExpectedCTC(String candiExpectedCTC) {
		this.candiExpectedCTC = candiExpectedCTC;
	}

	public String getCandiNoticePeriod() {
		return candiNoticePeriod;
	}

	public void setCandiNoticePeriod(String candiNoticePeriod) {
		this.candiNoticePeriod = candiNoticePeriod;
	}

	public String getAvailability() {
		return availability;
	}

	public void setAvailability(String availability) {
		this.availability = availability;
	}

	public String getStrAvailability() {
		return strAvailability;
	}

	public void setStrAvailability(String strAvailability) {
		this.strAvailability = strAvailability;
	}

	public List<FillSources> getSourceList() {
		return sourceList;
	}

	public void setSourceList(List<FillSources> sourceList) {
		this.sourceList = sourceList;
	}

	public String getDocId0() {
		return docId0;
	}

	public void setDocId0(String docId0) {
		this.docId0 = docId0;
	}

	public File getIdDoc0() {
		return idDoc0;
	}

	public void setIdDoc0(File idDoc0) {
		this.idDoc0 = idDoc0;
	}

	public String getIdDoc0FileName() {
		return idDoc0FileName;
	}

	public void setIdDoc0FileName(String idDoc0FileName) {
		this.idDoc0FileName = idDoc0FileName;
	}

	public String getIdDocStatus0() {
		return idDocStatus0;
	}

	public void setIdDocStatus0(String idDocStatus0) {
		this.idDocStatus0 = idDocStatus0;
	}

	public String getIdDocName0() {
		return idDocName0;
	}

	public void setIdDocName0(String idDocName0) {
		this.idDocName0 = idDocName0;
	}

	public String getIdDocType0() {
		return idDocType0;
	}

	public void setIdDocType0(String idDocType0) {
		this.idDocType0 = idDocType0;
	}

	public String getDocId1() {
		return docId1;
	}

	public void setDocId1(String docId1) {
		this.docId1 = docId1;
	}

	public File getIdDoc1() {
		return idDoc1;
	}

	public void setIdDoc1(File idDoc1) {
		this.idDoc1 = idDoc1;
	}

	public String getIdDoc1FileName() {
		return idDoc1FileName;
	}

	public void setIdDoc1FileName(String idDoc1FileName) {
		this.idDoc1FileName = idDoc1FileName;
	}

	public String getIdDocStatus1() {
		return idDocStatus1;
	}

	public void setIdDocStatus1(String idDocStatus1) {
		this.idDocStatus1 = idDocStatus1;
	}

	public String getIdDocName1() {
		return idDocName1;
	}

	public void setIdDocName1(String idDocName1) {
		this.idDocName1 = idDocName1;
	}

	public String getIdDocType1() {
		return idDocType1;
	}

	public void setIdDocType1(String idDocType1) {
		this.idDocType1 = idDocType1;
	}

	public String getDocId2() {
		return docId2;
	}

	public void setDocId2(String docId2) {
		this.docId2 = docId2;
	}

	public File getIdDoc2() {
		return idDoc2;
	}

	public void setIdDoc2(File idDoc2) {
		this.idDoc2 = idDoc2;
	}

	public String getIdDoc2FileName() {
		return idDoc2FileName;
	}

	public void setIdDoc2FileName(String idDoc2FileName) {
		this.idDoc2FileName = idDoc2FileName;
	}

	public String getIdDocStatus2() {
		return idDocStatus2;
	}

	public void setIdDocStatus2(String idDocStatus2) {
		this.idDocStatus2 = idDocStatus2;
	}

	public String getIdDocName2() {
		return idDocName2;
	}

	public void setIdDocName2(String idDocName2) {
		this.idDocName2 = idDocName2;
	}

	public String getIdDocType2() {
		return idDocType2;
	}

	public void setIdDocType2(String idDocType2) {
		this.idDocType2 = idDocType2;
	}
	public String getotherDocumentCnt() {
		return otherDocumentCnt;
	}

	public void setotherDocumentCnt(String otherDocumentCnt) {
		this.otherDocumentCnt = otherDocumentCnt;
	}
	public File[] getIdOtherDoc() {
		return idOtherDoc;
	}

	public void setIdOtherDoc(File[] idOtherDoc) {
		this.idOtherDoc = idOtherDoc;
	}

	public String[] getOtherDocName() {
		return otherDocName;
	}

	public void setOtherDocName(String[] otherDocName) {
		this.otherDocName = otherDocName;
	}

	public String[] getDocIdother() {
		return docIdother;
	}

	public void setDocIdother(String[] docIdother) {
		this.docIdother = docIdother;
	}

	public File[] getIdDocOther() {
		return idDocOther;
	}

	public void setIdDocOther(File[] idDocOther) {
		this.idDocOther = idDocOther;
	}

	public String[] getIdDocOtherFileName() {
		return idDocOtherFileName;
	}

	public void setIdDocOtherFileName(String[] idDocOtherFileName) {
		this.idDocOtherFileName = idDocOtherFileName;
	}

	public String[] getIdDocStatusOther() {
		return idDocStatusOther;
	}

	public void setIdDocStatusOther(String[] idDocStatusOther) {
		this.idDocStatusOther = idDocStatusOther;
	}

	public String[] getIdDocNameOther() {
		return idDocNameOther;
	}

	public void setIdDocNameOther(String[] idDocNameOther) {
		this.idDocNameOther = idDocNameOther;
	}

	public String[] getIdDocTypeOther() {
		return idDocTypeOther;
	}

	public void setIdDocTypeOther(String[] idDocTypeOther) {
		this.idDocTypeOther = idDocTypeOther;
	}

	public String getFrombgverify() {
		return frombgverify;
	}

	public void setFrombgverify(String frombgverify) {
		this.frombgverify = frombgverify;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}
	
	public String[] getMarks() {
		return marks;
	}

	public void setMarks(String[] marks) {
		this.marks = marks;
	}

	public String[] getCity1() {
		return city1;
	}

	public void setCity1(String[] city1) {
		this.city1 = city1;
	}

	public String[] getInstituteName() {
		return instituteName;
	}

	public void setInstituteName(String[] instituteName) {
		this.instituteName = instituteName;
	}

	public String[] getUniversityName() {
		return universityName;
	}

	public void setUniversityName(String[] universityName) {
		this.universityName = universityName;
	}
	
	public String[] getSubject() {
		return subject;
	}

	public void setSubject(String[] subject) {
		this.subject = subject;
	}
	
	public String[] getDegreeCertiStatus() {
		return degreeCertiStatus;
	}

	public void setDegreeCertiStatus(String[] degreeCertiStatus) {
		this.degreeCertiStatus = degreeCertiStatus;
	}

	public String[] getStrStartDate() {
		return strStartDate;
	}

	public void setStrStartDate(String[] strStartDate) {
		this.strStartDate = strStartDate;
	}

	public String[] getStrCompletionDate() {
		return strCompletionDate;
	}

	public void setStrCompletionDate(String[] strCompletionDate) {
		this.strCompletionDate = strCompletionDate;
	}


	public String[] getExpLetterFileStatus() {
		return expLetterFileStatus;
	}


	public void setExpLetterFileStatus(String[] expLetterFileStatus) {
		this.expLetterFileStatus = expLetterFileStatus;
	}
	
	public String[] getDegreeId() {
		return degreeId;
	}

	public void setDegreeId(String[] degreeId) {
		this.degreeId = degreeId;
	}
	
	public String[] getPrevCompanyId() {
		return prevCompanyId;
	}

	public void setPrevCompanyId(String[] prevCompanyId) {
		this.prevCompanyId = prevCompanyId;
	}
//===start parvez date: 08-08-2022===
	public String[] getPrevCompanyUANNo() {
		return prevCompanyUANNo;
	}

	public void setPrevCompanyUANNo(String[] prevCompanyUANNo) {
		this.prevCompanyUANNo = prevCompanyUANNo;
	}

	public String[] getPrevCompanyESICNo() {
		return prevCompanyESICNo;
	}

	public void setPrevCompanyESICNo(String[] prevCompanyESICNo) {
		this.prevCompanyESICNo = prevCompanyESICNo;
	}
//===start parvez date: 28-10-2022===	
	public String getDocId3() {
		return docId3;
	}

	public void setDocId3(String docId3) {
		this.docId3 = docId3;
	}

	public File getIdDoc3() {
		return idDoc3;
	}

	public void setIdDoc3(File idDoc3) {
		this.idDoc3 = idDoc3;
	}

	public String getIdDoc3FileName() {
		return idDoc3FileName;
	}

	public void setIdDoc3FileName(String idDoc3FileName) {
		this.idDoc3FileName = idDoc3FileName;
	}

	public String getIdDocStatus3() {
		return idDocStatus3;
	}

	public void setIdDocStatus3(String idDocStatus3) {
		this.idDocStatus3 = idDocStatus3;
	}

	public String getIdDocName3() {
		return idDocName3;
	}

	public void setIdDocName3(String idDocName3) {
		this.idDocName3 = idDocName3;
	}

	public String getIdDocType3() {
		return idDocType3;
	}

	public void setIdDocType3(String idDocType3) {
		this.idDocType3 = idDocType3;
	}
//===end parvez date: 28-10-2022===	

}