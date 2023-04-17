package com.konnect.jpms.employee;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.reports.EmployeeLeaveEntryReport;
import com.konnect.jpms.select.FillApproval;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillGender;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLeaveType;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillNoticeDuration;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycleDuration;
import com.konnect.jpms.select.FillProbationDuration;
import com.konnect.jpms.select.FillSalutation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddEmployeeInOneStep extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strBaseUserType = null;
	String strWLocationAccess =  null;
	String strSessionEmpId = null;
	String strSessionOrgId = null;
	String strAction = null;//Created By Dattatray 09-6-2022
	
	private String empDateOfBirth;
	private String empId;
	private String operation;
	private String pageType;
	Boolean autoGenerate = false; 
	public String dobYear="1977";
	
	CommonFunctions CF = null;
	
	private String hod;
	private String HR;
	private String strCXOHOD;
	private String defaultCXO;
	private String defaultHOD;
	private String[] locationCXO;
	private List<String> cxoLocationAccess = new ArrayList<String>();
	
	private String empUserTypeId;
	private String empContractor;
	private String empCodeAlphabet;
	private String empCodeNumber;
	private String empFname;
	private String empCoverImageFileName;
	private File empCoverImage;
	

	private String btnSubmit;
	private String empMname;
	private String empLname;
	private String userName;
	private String empPassword;
	private String empEmail;
	private String empImageFileName;
	private File empImage;
	private String empGender;
	private String empStartDate;
	private String orgId;
	private String strDesignation;
	private String supervisor;
	private String redirectUrl;
	
	private List<FillEmployee> supervisorList;
	private List<FillEmployee> HRList;
	private List<FillEmployee> HodList;
	private List<FillGender> empGenderList;
	private List<FillSalutation> salutationList;
	private List<FillDesig> desigList;
	private List<FillOrganisation> orgList;
	private List<FillLevel> levelList;
	private List<FillWLocation> wLocationList;
	
	private String salutation;
//	private String department;
//	private String wLocation;
//	private String empGrade;
//	private String strLevel;
//	private String services;
	
	private String department;
	private String wLocation;
	private String empGrade;
	private String strLevel;
	private String[] service;
	
	private List<FillServices> serviceList;
	private List<FillDepartment> deptList;
	
	private String empType;
	private List<FillEmploymentType> empTypeList;
	private int probationDuration;
	private List<FillProbationDuration> probationDurationList;
	private int noticeDuration;
	private List<FillNoticeDuration> noticeDurationList;
	private String rosterDependency;
	private List<FillApproval> rosterDependencyList;
	private String[] probationLeaves;
	private List<FillLeaveType> leaveTypeList;
	private String defaultStatus;
	private List<FillGrade> gradeList;
	private String empStatus;
	private String empCorporateMobileNo;
	private String empCorporateDesk;
	private String empEmailSec;
	private String skypeId;
	private String bioId;
	
	private List<FillPayCycleDuration> paycycleDurationList;
	private String strPaycycleDuration;
	
	public String execute() {
			
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			CF = new CommonFunctions();
			CF.setRequest(request);
		}
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);
		strWLocationAccess = (String)session.getAttribute(WLOCATION_ACCESS);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		
		request.setAttribute(PAGE, "/jsp/employee/AddEmployeeInOneStep.jsp");
		request.setAttribute(TITLE, TAddEmployee);
		request.setAttribute("dobYear",dobYear);
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-group\"></i><a href=\"People.action\" style=\"color: #3c8dbc;\"> People</a></li>"
				+"<li>Add Employee in 1 step</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		UtilityFunctions uF = new UtilityFunctions();
		//Created By Dattatray 09-6-2022
		strAction = request.getServletPath();
		if(strAction!=null) {
			strAction = strAction.replace("/","");
		}
		
//		EncryptionUtility eU = new EncryptionUtility();
//		CF.getFormValidationFields(request, ADD_UPDATE_EMPLOYEE);
//		
//		if(getEmpId() != null && uF.parseToInt(getEmpId()) == 0) {
//			String decodeEmpId = eU.decode(getEmpId());
//			setEmpId(decodeEmpId);
//		}
		
		List<String> accessEmpList = CF.viewEmployeeIdsList(request, uF, strBaseUserType, strSessionEmpId, strWLocationAccess);
		if((strBaseUserType != null && strUserType != null && (strBaseUserType.equals(EMPLOYEE) || strUserType.equals(EMPLOYEE))) || (!accessEmpList.contains(getEmpId()) && uF.parseToInt(getEmpId()) > 0)) {
			setEmpId(strSessionEmpId);
		}
		
		CF.getFormValidationFields(request, ADD_UPDATE_EMPLOYEE);
		
		getFormValidationFieldWise(uF);
		
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
			
		try {
			if (getOperation()!=null && getOperation().equals("U") && uF.parseToInt(getEmpId()) != 0) {
				request.setAttribute(TITLE, TEditEmployee);
				if(getBtnSubmit() == null) {
					CF.pageVisitAuditTrail(con,CF,uF, strSessionEmpId, strAction, strBaseUserType, "Edit Employee Detail Opened");//Created By Dattatray 09-6-2022
					loadPageVisitAuditTrail(uF,"Employee profile checked");//Created By Dattatray 09-6-2022
					viewEmployee(con);
					loadValidateEmployee(con);
					return SUCCESS;
				} else if(getBtnSubmit() != null) {
					loadPageVisitAuditTrail(uF,"Employee profile updated");//Created By Dattatray 09-6-2022
					updateEmployee(con);
					return VIEW;
				}
				
			} else if (getOperation()!=null && operation.equals("D")) {
				if(getPageType() != null && getPageType().equals("WE")) {
					deleteEmployee(con, uF);
					loadPageVisitAuditTrail(uF,"Employee profile deleted");//Created By Dattatray 09-06-2022
					return VIEW;
				} else if(getPageType() != null && getPageType().equals("PE")) {
					deletePendingEmployee(con, uF);
					loadPageVisitAuditTrail(uF,"Employee Onboarding Form deleted");//Created By Dattatray 10-06-2022
					return REPORT;
				}
			}
	
			if(getBtnSubmit() != null && getBtnSubmit().equals("Submit")) {
				loadPageVisitAuditTrail(uF,"New employee profile created");
				insertEmployee(con);
				return VIEW;
			}
		
			setDefaultStatus("1");
			loadValidateEmployee(con);
				
			return SUCCESS;
		} finally {  
			db.closeConnection(con);
		}
	 }
	
	//Created By Dattatray 09-06-2022 t0 13-06-2022
	private void loadPageVisitAuditTrail(UtilityFunctions uF,String strMsg) {
		Connection con=null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpProfile = CF.getEmpNameMap(con,null,getEmpId());
			StringBuilder builder = new StringBuilder();
			if(uF.parseToInt(getEmpId())>0) {
				builder.append("Emp name : "+hmEmpProfile.get(getEmpId()));
				builder.append("\nRemark : "+strMsg);
				if(getPageType() != null && getPageType().equals("WE")&& getOperation()!=null && getOperation().equals("D")) {
					builder.append("\nPage Type : "+getPageType());
					
				}else if(getPageType() != null && getPageType().equals("PE") && getOperation()!=null  && getOperation().equals("D")) {
					builder.append("\nPage Type : "+getPageType());
				}
			}else if(getBtnSubmit() != null && getBtnSubmit().equals("Submit")){
				builder.append("Employee name : "+getEmpFname()+" "+getEmpLname());
				builder.append("\n");
				builder.append("Remark : "+strMsg);
			}
			
			CF.pageVisitAuditTrail(con,CF,uF, strSessionEmpId, strAction, strBaseUserType, builder.toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			db.closeConnection(con);
		}
	}
	
	public void setProbationPolicy(Connection con, UtilityFunctions uF) {
//		System.out.println("setProbationPolicy===>");
		PreparedStatement pst =null;
		ResultSet rs = null;
		
		try {
			
			pst = con.prepareStatement(selectProbationPolicy);
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				setProbationDuration(rs.getInt("probation_duration"));
				setNoticeDuration(rs.getInt("notice_duration"));
				if(rs.getString("leaves_types_allowed")!=null) {
					setProbationLeaves(rs.getString("leaves_types_allowed").split(","));
				}
			}
			rs.close();
			pst.close();
					
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private void deletePendingEmployee(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		try {
			
			pst = con.prepareStatement(deleteEmployee_P);
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement(deleteEmployee_O);
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM employee_activity_details WHERE emp_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement(deleteUserEmp);
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement(deleteSkills);
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement(deleteHobbies);
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM languages_details WHERE emp_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM education_details WHERE emp_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement(deleteDocuments);
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM emp_family_members WHERE emp_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM emp_prev_employment WHERE emp_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM emp_references WHERE emp_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}



	private void getFormValidationFieldWise(UtilityFunctions uF) {
		Map<String, List<String>> hmValidationFields = (Map<String, List<String>> ) request.getAttribute("hmValidationFields");
//		String validReqOpt = "";
//		String validAsterix = "";
		
		
//		************************** Step 1 ***********************************
		List<String> salutationValidList = hmValidationFields.get("EMP_SALUTATION"); 
		String salutationValidReqOpt = "";
		String salutationValidAsterix = "";
		if(uF.parseToBoolean(salutationValidList.get(0))) {
			salutationValidReqOpt = "validateRequired";
			salutationValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("salutationValidReqOpt", salutationValidReqOpt);
		request.setAttribute("salutationValidAsterix", salutationValidAsterix);
		
		List<String> empFNameValidList = hmValidationFields.get("EMP_FIRST_NAME"); 
		String empFNameValidReqOpt = "";
		String empFNameValidAsterix = "";
		if(uF.parseToBoolean(empFNameValidList.get(0))) {
			empFNameValidReqOpt = "validateRequired";
			empFNameValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empFNameValidReqOpt", empFNameValidReqOpt);
		request.setAttribute("empFNameValidAsterix", empFNameValidAsterix);
		
		List<String> empMNameValidList = hmValidationFields.get("EMP_MIDDLE_NAME"); 
		String empMNameValidReqOpt = "";
		String empMNameValidAsterix = "";
		if(uF.parseToBoolean(empMNameValidList.get(0))) {
			empMNameValidReqOpt = "validateRequired";
			empMNameValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empMNameValidReqOpt", empMNameValidReqOpt);
		request.setAttribute("empMNameValidAsterix", empMNameValidAsterix);
		
		List<String> empLNameValidList = hmValidationFields.get("EMP_LAST_NAME"); 
		String empLNameValidReqOpt = "";
		String empLNameValidAsterix = "";
		if(uF.parseToBoolean(empLNameValidList.get(0))) {
			empLNameValidReqOpt = "validateRequired";
			empLNameValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empLNameValidReqOpt", empLNameValidReqOpt);
		request.setAttribute("empLNameValidAsterix", empLNameValidAsterix);
		
		List<String> empPersonalEmailIdValidList = hmValidationFields.get("EMP_PERSONAL_EMAIL_ID"); 
		String empPersonalEmailIdValidReqOpt = "validateEmail";
		String empPersonalEmailIdValidAsterix = "";
		if(uF.parseToBoolean(empPersonalEmailIdValidList.get(0))) {
			empPersonalEmailIdValidReqOpt = "validateEmailRequired";
			empPersonalEmailIdValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPersonalEmailIdValidReqOpt", empPersonalEmailIdValidReqOpt);
		request.setAttribute("empPersonalEmailIdValidAsterix", empPersonalEmailIdValidAsterix);
		
		
		List<String> empMobileNoValidList = hmValidationFields.get("EMP_MOBILE_NO"); 
		String empMobileNoValidReqOpt = "";
		String empMobileNoValidAsterix = "";
		if(uF.parseToBoolean(empMobileNoValidList.get(0))) {
			empMobileNoValidReqOpt = "validateRequired";
			empMobileNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empMobileNoValidReqOpt", empMobileNoValidReqOpt);
		request.setAttribute("empMobileNoValidAsterix", empMobileNoValidAsterix);
		
		
		List<String> empDateOfBirthValidList = hmValidationFields.get("EMP_DATE_OF_BIRTH"); 
		String empDateOfBirthValidReqOpt = "";
		String empDateOfBirthValidAsterix = "";
		if(uF.parseToBoolean(empDateOfBirthValidList.get(0))) {
			empDateOfBirthValidReqOpt = "validateRequired";
			empDateOfBirthValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empDateOfBirthValidReqOpt", empDateOfBirthValidReqOpt);
		request.setAttribute("empDateOfBirthValidAsterix", empDateOfBirthValidAsterix);
		
		List<String> empGenderValidList = hmValidationFields.get("EMP_GENDER"); 
		String empGenderValidReqOpt = "";
		String empGenderValidAsterix = "";
		if(uF.parseToBoolean(empGenderValidList.get(0))) {
			empGenderValidReqOpt = "validateRequired";
			empGenderValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empGenderValidReqOpt", empGenderValidReqOpt);
		request.setAttribute("empGenderValidAsterix", empGenderValidAsterix);
		
		
		List<String> empPanNoValidList = hmValidationFields.get("EMP_PAN_NO"); 
		String empPanNoValidReqOpt = "";
		String empPanNoValidAsterix = "";
		if(uF.parseToBoolean(empPanNoValidList.get(0))) {
			empPanNoValidReqOpt = "validateRequired";
			empPanNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPanNoValidReqOpt", empPanNoValidReqOpt);
		request.setAttribute("empPanNoValidAsterix", empPanNoValidAsterix);
		
		
		List<String> empProfilePhotoValidList = hmValidationFields.get("EMP_PROFILE_PHOTO"); 
		String empProfilePhotoValidReqOpt = "";
		String empProfilePhotoValidAsterix = "";
		if(uF.parseToBoolean(empProfilePhotoValidList.get(0))) {
			empProfilePhotoValidReqOpt = "validateRequired";
			empProfilePhotoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empProfilePhotoValidReqOpt", empProfilePhotoValidReqOpt);
		request.setAttribute("empProfilePhotoValidAsterix", empProfilePhotoValidAsterix);
		
	}

	public String loadValidateEmployee(Connection con) {
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		try {
			FillEmployee fillEmployee=new FillEmployee(request);
			UtilityFunctions uF = new UtilityFunctions();
			if (uF.parseToInt(getEmpId()) == 0) {
				setEmpCode("", con, uF);
			}
			salutationList = new FillSalutation(request).fillSalutation();
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getOrgId());
			levelList = new FillLevel(request).fillLevel(uF.parseToInt(getOrgId()));
			gradeList = new FillGrade(request).fillGradeFromDesignation(getStrDesignation());
			serviceList = new FillServices(request).fillServices(getOrgId(), uF);
			deptList = new FillDepartment(request).fillDepartment(uF.parseToInt(getOrgId()));
			empTypeList = new FillEmploymentType().fillEmploymentType(request);
			
			probationDurationList = new FillProbationDuration().fillProbationDuration();
			noticeDurationList = new FillNoticeDuration().fillNoticeDuration();
			rosterDependencyList = new FillApproval().fillYesNo();
			
			leaveTypeList = new ArrayList<FillLeaveType>();
			if(uF.parseToInt(getOrgId()) > 0 && uF.parseToInt(getwLocation()) > 0 && uF.parseToInt(getStrLevel()) > 0 && uF.parseToInt(getDefaultStatus()) > 0) {
				leaveTypeList = new FillLeaveType(request).fillLeave(uF.parseToInt(getOrgId()), uF.parseToInt(getwLocation()), uF.parseToInt(getStrLevel()), getDefaultStatus(),uF.parseToInt(getEmpId()), true);
			} else {
				leaveTypeList = new ArrayList<FillLeaveType>();
			}
			
//			if(strUserType != null && strUserType.equals(ADMIN)) {
//				desigList = new FillDesig(request).fillDesig();
//			} else {
			desigList = new FillDesig(request).fillDesigFromLevel(getStrLevel());
//			}
			supervisorList = fillEmployee.fillSupervisorNameCode(uF.parseToInt(getEmpId()), getOrgId(), getDepartment());
			HodList = fillEmployee.fillHODNameCode(""+getEmpId(), getOrgId(), uF.parseToInt(getwLocation()), CF);
			
			if(getwLocation()!= null && !getwLocation().equals("0")) {
				HRList=fillEmployee.fillEmployeeNameHR(""+getEmpId(),0, uF.parseToInt(getwLocation()), CF, uF); 
			} else if (getOrgId() != null && !getOrgId().equals("0")) {
				HRList=fillEmployee.fillEmployeeNameHR(""+getEmpId(),uF.parseToInt(getOrgId()), 0, CF, uF); 
			} else {
				HRList=fillEmployee.fillEmployeeNameHR(""+getEmpId(),0, 0, CF, uF); 
			}
			if(getSupervisor()==null || getSupervisor().equals("")) {
				setSupervisor("0");
			}
			empGenderList = new FillGender().fillGender(); 
			request.setAttribute("empGenderList", empGenderList);
			request.setAttribute("currentYear", (uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()).toString(), DBDATE, "yyyy")));
			
			paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();
			
			
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			if(rst !=null) {
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return LOAD;
		
	}

	
	
	private void generateEmpCode(Connection con) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		String empCodeAlpha = "", empCodeNum = ""; 
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			pst = con.prepareStatement("SELECT emp_code_auto_generate,emp_code_alpha,contractor_code_alpha,emp_code_numeric FROM org_details where org_id = ?");
			pst.setInt(1, uF.parseToInt(getOrgId()));
			rs = pst.executeQuery();
			
			while(rs.next()) {
				setAutoGenerate(uF.parseToBoolean(rs.getString("emp_code_auto_generate")));
				empCodeAlpha = rs.getString("emp_code_alpha");
				if(uF.parseToInt(getEmpContractor()) == 2) {
					empCodeAlpha = rs.getString("contractor_code_alpha");
				}
				empCodeNum = rs.getString("emp_code_numeric");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT empcode FROM employee_personal_details where empcode like ? order by emp_per_id desc limit 1");
			pst.setString(1, empCodeAlpha+"%");
			rs = pst.executeQuery();
			boolean empcodeFlag = false;
			while(rs.next()) {
				empcodeFlag = true;
				String strEmpCode = rs.getString("empcode");
				String strEmpCodeNum = strEmpCode.substring(empCodeAlpha.length(), strEmpCode.length());
				empCodeNum = (uF.parseToInt(strEmpCodeNum)+1) + "";
				getLatestEmpCode(con, uF, empCodeAlpha, empCodeNum);
			}
			rs.close();
			pst.close();
			
			if(!empcodeFlag) {
				setEmpCodeAlphabet(empCodeAlpha);
				setEmpCodeNumber(empCodeNum);
				request.setAttribute("EMP_CODE_ALPHA", uF.showData(getEmpCodeAlphabet(), ""));
				request.setAttribute("EMP_CODE_NUM", uF.showData(getEmpCodeNumber(), ""));
			}
			
	// This position of code changed on 26-04-2012 for always displaying the auto generated code
			 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	private void getLatestEmpCode(Connection con, UtilityFunctions uF, String empCodeAlpha, String empCodeNum) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			boolean flag = false;
			pst = con.prepareStatement("SELECT empcode FROM employee_personal_details where empcode = ? ");
			pst.setString(1, empCodeAlpha+empCodeNum);
			rs = pst.executeQuery();
			
			while(rs.next()) {
				flag = true;
				String strEmpCode = rs.getString("empcode");
				String strEmpCodeNum = strEmpCode.substring(empCodeAlpha.length(), strEmpCode.length());
				
				empCodeNum = (uF.parseToInt(strEmpCodeNum)+1) + "";
				getLatestEmpCode(con, uF, empCodeAlpha, empCodeNum);
			}
			rs.close();
			pst.close();
			
			if(!flag) {
				setEmpCodeAlphabet(empCodeAlpha);
				setEmpCodeNumber(empCodeNum);
				request.setAttribute("EMP_CODE_ALPHA", uF.showData(getEmpCodeAlphabet(), ""));
				request.setAttribute("EMP_CODE_NUM", uF.showData(getEmpCodeNumber(), ""));
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String viewEmployee(Connection con) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			setEmpPersonalDetails(con, uF);
			setEmpOfficialDetails(con, uF);
			setUser(con, uF);
			setProbationPolicy(con, uF);
			
			String levelId = CF.getEmpLevelId(con, ""+getEmpId());
			
			Map<String, String> hmLeaveTypeMap = CF.getLeaveTypeMap(con);
			if(hmLeaveTypeMap == null) hmLeaveTypeMap = new HashMap<String, String>();
			
			StringBuffer sbLeaves = null;
			for(int i = 0; getProbationLeaves() != null && i <getProbationLeaves().length; i++){
				if(sbLeaves == null){
					if(uF.parseToInt(getProbationLeaves()[i].trim()) > 0){
						sbLeaves = new StringBuffer();
						sbLeaves.append(getProbationLeaves()[i].trim());
					}
				} else {
					if(uF.parseToInt(getProbationLeaves()[i].trim()) > 0){
						sbLeaves.append(","+getProbationLeaves()[i].trim());
					}
				}
			}
			if(sbLeaves == null) sbLeaves = new StringBuffer();
			
			List<String> alExistLeave = new ArrayList<String>();
//			List<List<String>> leaveTypeListWithBalance = CF.getLevelLeaveTypeBalanceForEmp(con, levelId, getStrEmpId(), CF,alExistLeave);
			Map<String, String> hmProRataLeaveBalance = CF.getLevelLeaveTypeBalanceForEmp(con, CF, uF, uF.parseToInt(getEmpId()), uF.parseToInt(getOrgId()), uF.parseToInt(getwLocation()),uF.parseToInt(getStrLevel()),getEmpStartDate(),getEmpStatus(),sbLeaves.toString(),alExistLeave);
			List<String> alAccrueLeave = CF.getAccrueLeave(con, CF, uF, uF.parseToInt(getOrgId()), uF.parseToInt(getwLocation()),uF.parseToInt(getStrLevel()),sbLeaves.toString());
			if(alAccrueLeave == null) alAccrueLeave = new ArrayList<String>();
			
			Map<String, String> hmEmpLeaveBalance = new HashMap<String, String>();
			EmployeeLeaveEntryReport leaveEntryReport = new EmployeeLeaveEntryReport();
			leaveEntryReport.request = request;
			leaveEntryReport.session = session;
			leaveEntryReport.CF = CF;
			leaveEntryReport.setStrEmpId(""+getEmpId());
			leaveEntryReport.setDataType("L");
			leaveEntryReport.viewEmployeeLeaveEntry1();
			
//			System.out.println("alAccrueLeave ========>> " + alAccrueLeave);  
			java.util.List leaveList = (java.util.List)request.getAttribute("leaveList");
			List<List<String>> leaveTypeListWithBalance1 = new ArrayList<List<String>>();
			
			for (int j=0; leaveList!=null && j<leaveList.size(); j++) {
				List<String> cinnerlist = (List<String>)leaveList.get(j);
//				System.out.println("cinnerlist.get(6) ========>> " + cinnerlist.get(6));  
//				System.out.println("if ========>> "); 
				if(alAccrueLeave.contains((String)cinnerlist.get(6))){
					continue;
				}
				boolean existLeaveFlag = CF.checkExistLeaveType(con, (String)cinnerlist.get(6), getEmpId(), CF);
				if(uF.parseToDouble((String)cinnerlist.get(5)) > 0.0d || existLeaveFlag) {
//					System.out.println("if if========>> ");  
					hmEmpLeaveBalance.put(""+cinnerlist.get(6), ""+cinnerlist.get(5));
					List<String> innerList = new ArrayList<String>();
					innerList.add(""+cinnerlist.get(6));
					innerList.add(uF.showData(hmLeaveTypeMap.get(""+cinnerlist.get(6)), ""));
					innerList.add("0");
					leaveTypeListWithBalance1.add(innerList);
				} else {
					List<String> innerList = new ArrayList<String>();
					innerList.add(""+cinnerlist.get(6));
					innerList.add(uF.showData(hmLeaveTypeMap.get(""+cinnerlist.get(6)), ""));
					innerList.add(""+uF.parseToDouble(hmProRataLeaveBalance.get(""+cinnerlist.get(6))));
					leaveTypeListWithBalance1.add(innerList);
				}
			}
			
			request.setAttribute("leaveTypeListWithBalance", leaveTypeListWithBalance1);
			request.setAttribute("hmEmpLeaveBalance", hmEmpLeaveBalance);
			request.setAttribute("alAccrueLeave", alAccrueLeave);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return SUCCESS;

	}

	public void setEmpOfficialDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst =null;
		ResultSet rs = null;
		
		try {
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			
			pst = con.prepareStatement("SELECT * FROM employee_official_details eod left join grades_details gd on gd.grade_id = eod.grade_id where emp_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			if (rs.next()) {
				setOrgId(rs.getString("org_id"));
				setwLocation(rs.getString("wlocation_id"));
				setDepartment(rs.getString("depart_id"));
				setStrDesignation(rs.getString("designation_id"));
				setStrLevel(hmEmpLevelMap.get(rs.getString("emp_id")));
				setEmpGrade(rs.getString("grade_id"));
				setSupervisor(""+rs.getInt("supervisor_emp_id"));
				setHod(rs.getString("hod_emp_id"));
				setHR(rs.getString("emp_hr"));
				setEmpContractor(uF.showData(rs.getString("emp_contractor"), "1"));
				request.setAttribute("EMP_OR_CONTRACTOR", uF.showData(rs.getString("emp_contractor"), "1"));
				
				if(rs.getString("service_id")!=null) {
					setService(rs.getString("service_id").split(","));	
				}
				
				setDefaultCXO(rs.getBoolean("is_cxo") ? "1" : "0");
				setDefaultHOD(rs.getBoolean("is_hod") ? "1" : "0");
				String departName = CF.getDepartMentNameById(con, rs.getString("depart_id"));
				request.setAttribute("HOD_DEPART_NAME", "Department: "+uF.showData(departName, ""));
				if(rs.getBoolean("is_hod")) {
					setStrCXOHOD("2");
				}
				if(rs.getBoolean("is_cxo")) {
					setStrCXOHOD("1");
					List<String> accessLocIds = CF.getCXOLocationAccessIds(con, uF, uF.parseToInt(getEmpId()));
					if (accessLocIds != null && !accessLocIds.isEmpty()) {
						for (int i = 0; i<accessLocIds.size(); i++) {
							cxoLocationAccess.add(accessLocIds.get(i).trim());
						}
					} else {
						cxoLocationAccess.add("0");
					}
				}
				
				setEmpType(rs.getString("emptype"));
				setEmpCorporateMobileNo(rs.getString("corporate_mobile_no"));
				setEmpCorporateDesk(rs.getString("corporate_desk"));
				
				setBioId(rs.getString("biometrix_id"));
				setRosterDependency(new FillApproval().getBoolValue(rs.getString("is_roster")));
				setStrPaycycleDuration(rs.getString("paycycle_duration"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM employee_personal_details where emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			if (rs.next()) {
				if(rs.getString("emp_status") !=null && rs.getString("emp_status").trim().equals(PROBATION)) {
					setDefaultStatus("1");
				} else if(rs.getString("emp_status") !=null && rs.getString("emp_status").trim().equals(PERMANENT)) {
					setDefaultStatus("2");
				} else if(rs.getString("emp_status") !=null && rs.getString("emp_status").trim().equals(TEMPORARY)) {
					setDefaultStatus("4");
				}
			}
			rs.close();
			pst.close();
			
			String leaveTypes = "";
			pst = con.prepareStatement("select emp_id,leaves_types_allowed from probation_policy where emp_id =?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
//			System.out.println("leaves pst==>"+pst);
			rs = pst.executeQuery();
			if (rs.next()) {
				leaveTypes = rs.getString("leaves_types_allowed");
				if(leaveTypes!=null && !leaveTypes.equals("") && !leaveTypes.equalsIgnoreCase("null")) {
					setProbationLeaves(leaveTypes.split(","));
				}
			}
			rs.close();
			pst.close();
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private int setEmpPersonalDetails(Connection con, UtilityFunctions uF) {

		PreparedStatement pst =null;
		ResultSet rs = null;
		int nEmpOffId = 0;
		try {

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			pst = con.prepareStatement("select emp_contractor,org_id from employee_official_details where emp_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			if (rs.next()) {
				setEmpContractor(uF.showData(rs.getString("emp_contractor"), "1"));
				setOrgId(rs.getString("org_id"));
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement(selectEmployee1Details);
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			if (rs.next()) {
				setEmpId(rs.getString("emp_per_id"));
				
				setEmpCode(rs.getString("empcode"), con, uF);
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
				setEmpGender(rs.getString("emp_gender"));
				setEmpDateOfBirth(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT));
//				System.out.println("empGender ===>> " + getEmpGender());
				
				setEmpStartDate(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				request.setAttribute("strImage", rs.getString("emp_image"));
				request.setAttribute("EMPLOYEE_EMAIL", rs.getString("emp_email"));
				request.setAttribute("EMPLOYEE_EMAIL2", rs.getString("emp_email_sec"));
				request.setAttribute("EMP_CODE", rs.getString("empcode"));
				
				if(rs.getString("emp_date_of_birth") != null && !rs.getString("emp_date_of_birth").equals("")) {
					dobYear = uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "yyyy");
					request.setAttribute("dobYear", dobYear);
				}
//				request.setAttribute("strEmpName", uF.showData(rs.getString("emp_fname"),"") +" "+uF.showData(rs.getString("emp_mname"),"") +" "+uF.showData(rs.getString("emp_lname"),"") );
				
				setEmpEmailSec(rs.getString("emp_email_sec"));
				setSkypeId(rs.getString("skype_id"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nEmpOffId;
	}

	private void setEmpCode(String empCode, Connection con, UtilityFunctions uF) {
		PreparedStatement pst =null;
		ResultSet rs = null;
		
		try {
			String empCodeAlpha = null;
			pst = con.prepareStatement("SELECT emp_code_auto_generate,emp_code_alpha,contractor_code_alpha,emp_code_numeric FROM org_details where org_id = ?");
			pst.setInt(1, uF.parseToInt(getOrgId()));
			rs = pst.executeQuery();
			
			while(rs.next()) {
				setAutoGenerate(uF.parseToBoolean(rs.getString("emp_code_auto_generate")));
				empCodeAlpha = rs.getString("emp_code_alpha");
				if(uF.parseToInt(getEmpContractor()) == 2) {
					empCodeAlpha = rs.getString("contractor_code_alpha");
				}
			}
			rs.close();
			pst.close();
			
			if(empCodeAlpha == null) {
				empCodeAlpha = "";
			}
			
			if(empCode!=null && empCode.contains(empCodeAlpha)) {
				
				setEmpCodeAlphabet(empCodeAlpha);
				setEmpCodeNumber(empCode.substring(empCodeAlpha.length(), empCode.length()));
				request.setAttribute("EMP_CODE_ALPHA", uF.showData(getEmpCodeAlphabet(), ""));
				request.setAttribute("EMP_CODE_NUM", uF.showData(getEmpCodeNumber(), ""));
				
			} else if(empCode!=null && empCode.length()==0 && getAutoGenerate()) {
				generateEmpCode(con);
				
			} else if(empCode!=null && empCodeAlpha!=null && empCode.length()>empCodeAlpha.length()) {
				setEmpCodeAlphabet(empCode.substring(0, empCodeAlpha.length()));
				setEmpCodeNumber(empCode.substring(empCodeAlpha.length(), empCode.length()));
				request.setAttribute("EMP_CODE_ALPHA", uF.showData(getEmpCodeAlphabet(), ""));
				request.setAttribute("EMP_CODE_NUM", uF.showData(getEmpCodeNumber(), ""));
			} else {
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public void setUser(Connection con, UtilityFunctions uF) {
		PreparedStatement pst =null;
		ResultSet rs = null;
		
		try {
			pst = con.prepareStatement(selectUserV1);
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			while(rs.next()){
				setUserName(rs.getString("username"));
				setEmpPassword(rs.getString("password"));
				setEmpUserTypeId(rs.getString("usertype_id"));
			}
			rs.close();
			pst.close();
					
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void insertEmployee(Connection con) {
		
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			if(uF.parseToInt(getEmpId())==0) {
				setEmpId(insertEmpPersonalDetails(con, uF, CF));
				insertUser(con, uF, uF.parseToInt(getEmpId()));
				if(getEmpImage() != null) {
					uploadImage(uF.parseToInt(getEmpId()),1);
				}
				
				if(getEmpCoverImage() != null) {
					uploadImage(uF.parseToInt(getEmpId()),2);
				}
				
				updateEmpOfficialDetailsAdmin(con, uF);
				insertProbationPeriod(con, uF); 
				
				insertLeaveRegisterNewEmployee(con, uF.parseToInt(getEmpId()));
				
				if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
					String strDomain = request.getServerName().split("\\.")[0];
					Notifications nF = new Notifications(N_NEW_EMPLOYEE, CF);
					nF.setDomain(strDomain);
					nF.request = request;
					nF.setStrEmpId(getEmpId()+"");
//					nF.setStrHostAddress(request.getRemoteHost());
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.setEmailTemplate(true);
					nF.sendNotifications();
				}
				
			} else {
				updateEmpPersonalDetails(con, uF);
				if(getEmpImage() != null) {
					uploadImage(uF.parseToInt(getEmpId()),1);
				}
				
				if(getEmpCoverImage() != null) {
					uploadImage(uF.parseToInt(getEmpId()),2);
				}
				updateEmpOfficialDetailsAdmin(con, uF);
				updateProbationPeriod(con, uF);
				
				insertLeaveRegisterNewEmployee(con, uF.parseToInt(getEmpId()));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void insertLeaveRegisterNewEmployee(Connection con, int nEmpId) {
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
//			String levelId = CF.getEmpLevelId(con, nEmpId+"");
//			List<List<String>> leaveTypeListWithBalance = CF.getLevelLeaveTypeBalanceForEmp(con, levelId, ""+getEmpId(), CF);
//			
//			for (int i = 0; leaveTypeListWithBalance != null && !leaveTypeListWithBalance.isEmpty() && i < leaveTypeListWithBalance.size(); i++) {
//				List<String> innerList = leaveTypeListWithBalance.get(i);
//				String strLeaveType = innerList.get(0);
//				String strLeaveBalance = request.getParameter("leaveBal"+innerList.get(0));
//				String strLeaveTypeStatus = request.getParameter(innerList.get(0));
//				if(uF.parseToInt(strLeaveTypeStatus) == 1 && uF.parseToDouble(strLeaveBalance) > 0.0d) {
//					CF.insertLeaveBalanceForNewEmployee(con, strLeaveType, strLeaveBalance, ""+nEmpId, CF);
//				}
//			}
			
			Map<String, String> hmLeaveTypeMap = CF.getLeaveTypeMap(con);
			if(hmLeaveTypeMap == null) hmLeaveTypeMap = new HashMap<String, String>();
			Iterator<String> it = hmLeaveTypeMap.keySet().iterator();
			while(it.hasNext()){
				String strLeaveTypeId = it.next();
				String strLeaveBalance = request.getParameter("leaveBal"+strLeaveTypeId);
				String strLeaveTypeStatus = request.getParameter(strLeaveTypeId);
//				System.out.println("strLeaveTypeId==>"+strLeaveTypeId+"--strLeaveBalance==>"+strLeaveBalance+"--strLeaveTypeStatus==>"+strLeaveTypeStatus);
//				if(uF.parseToInt(strLeaveTypeStatus) == 1 && uF.parseToDouble(strLeaveBalance) > 0.0d) {
				if(uF.parseToInt(strLeaveTypeStatus) == 1) {
					CF.insertLeaveBalanceForNewEmployee(con, strLeaveTypeId, strLeaveBalance, ""+nEmpId, CF);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void insertProbationPeriod(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			StringBuilder sbProbationLeaves = new StringBuilder();
			int i = 0;
			if(getProbationLeaves() != null) {
				for (; i < getProbationLeaves().length - 1; i++) {
	
					if (uF.parseToInt(getProbationLeaves()[i]) > 0) {
						sbProbationLeaves.append(getProbationLeaves()[i] + ",");
					}
				}
				if (uF.parseToInt(getProbationLeaves()[i]) > 0) {
					sbProbationLeaves.append(getProbationLeaves()[i]);
				}
			}
			
			String joiningDate = null;
			int probation = getProbationDuration();
			pst = con.prepareStatement("select emp_per_id,joining_date from employee_personal_details where emp_per_id=? ");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				joiningDate = rs.getString("joining_date");
			}
			rs.close();
			pst.close();
			
			Date probEndDate = null;
			if(joiningDate != null && !joiningDate.equals("")) {
				if(getProbationDuration()>0) {
//					lastProbationEndDate = uF.getFutureDate(uF.getDateFormat(joiningDate, DBDATE), getProbationDuration());
					probEndDate = uF.getFutureDate(uF.getDateFormat(getEmpStartDate(), DATE_FORMAT), (getProbationDuration()-1));
				}
			}
			
//			System.out.println("joiningDate==>"+joiningDate+"==>getProbationDuration()==>"+getProbationDuration());
			
			pst = con.prepareStatement("INSERT INTO probation_policy(emp_id, leaves_types_allowed, probation_duration, notice_duration,probation_end_date) VALUES(?,?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setString(2, sbProbationLeaves.toString());
			pst.setInt(3, getProbationDuration());
			pst.setInt(4, getNoticeDuration());
			pst.setDate(5, probEndDate);
			pst.execute();
			pst.close();
			
			if(getProbationDuration() > 0 && uF.parseToInt(getEmpStatus()) == 1) {
				Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
				Date probEndDate1 = uF.getFutureDate(uF.getDateFormat(getEmpStartDate(), DATE_FORMAT), (getProbationDuration()-1));
				
				int dateResult = probEndDate1.compareTo(currDate);
				if(dateResult >= 0) {
					pst = con.prepareStatement("update employee_personal_details set emp_status = ? where emp_per_id = ?");
					pst.setString(1, PROBATION);
					pst.setInt(2, uF.parseToInt(getEmpId()));
					int x = pst.executeUpdate();
					pst.close();
				}
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	public String updateEmployee(Connection con) {
		UtilityFunctions uF = new UtilityFunctions();
		try {
			if(getEmpImage()!=null) {
				uploadImage(uF.parseToInt(getEmpId()),1);
			}
			if(getEmpImage()!=null) {
				uploadImage(uF.parseToInt(getEmpId()),2);
			}
			if(getEmpFname()!=null) {
				updateEmpPersonalDetails(con, uF);
				updateEmpOfficialDetailsAdmin(con, uF);
				updateProbationPeriod(con, uF);
				
				insertLeaveRegisterNewEmployee(con, uF.parseToInt(getEmpId()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in updation");
		}
		return VIEW;
	}
	
	public void updateProbationPeriod(Connection con, UtilityFunctions uF) {
//		System.out.println("updateProbationPeriod====>>>");
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
				
			StringBuilder sbProbationLeaves = new StringBuilder();
			
			for (int i = 0; getProbationLeaves() != null && i < getProbationLeaves().length; i++) {
				if (uF.parseToInt(getProbationLeaves()[i]) > 0 ) {
					sbProbationLeaves.append(getProbationLeaves()[i]);
					if(i < getProbationLeaves().length -1) {
						sbProbationLeaves.append(",");
					}
				}
			}
			
			String joiningDate = null;
			int probation = getProbationDuration();
			pst = con.prepareStatement("select emp_per_id,joining_date from employee_personal_details where emp_per_id=? ");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				joiningDate = rs.getString("joining_date");
				
			}
			rs.close();
			pst.close();
			
			Date probEndDate = null;
			if(joiningDate != null && !joiningDate.equals("")) {
				if(getProbationDuration()>0) {
					//lastProbationEndDate = uF.getFutureDate(uF.getDateFormat(joiningDate, DBDATE), getProbationDuration());
					probEndDate = uF.getFutureDate(uF.getDateFormat(getEmpStartDate(), DATE_FORMAT), (getProbationDuration()-1));
					
				}
			}
			pst = con.prepareStatement("UPDATE probation_policy SET leaves_types_allowed=?, probation_duration=?, notice_duration = ?,probation_end_date=?,extend_probation_duration=? " +
					" WHERE emp_id = ?");
			pst.setString(1, sbProbationLeaves.toString() );
			pst.setInt(2, getProbationDuration());
			pst.setInt(3, getNoticeDuration());
			pst.setDate(4, probEndDate);
			pst.setInt(5, 0);
			pst.setInt(6, uF.parseToInt(getEmpId()));
			int cnt=pst.executeUpdate();
			pst.close();
			if(cnt==0) {
				insertProbationPeriod(con,uF);
			} else if(uF.parseToInt(getEmpStatus()) == 1) {
				pst = con.prepareStatement("update employee_personal_details set emp_status = ? where emp_per_id = ?");
				pst.setString(1, PROBATION);
				pst.setInt(2, uF.parseToInt(getEmpId()));
				int x = pst.executeUpdate();
				pst.close();
			}
				
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void updateEmpOfficialDetailsAdmin(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			int nSuperWiserId = 0;
			pst = con.prepareStatement("select supervisor_emp_id from employee_official_details WHERE emp_id=?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			while(rs.next()){
				nSuperWiserId = rs.getInt("supervisor_emp_id");
			}
			rs.close();
			pst.close();
			
			getCurrOrgWLocDeptSBUGrade(con,uF);
			
			StringBuilder sbServices = null;
			
			for (int i = 0; getService() != null && i < getService().length; i++) {
				
				if (uF.parseToInt(getService()[i]) > 0) {
					if(sbServices == null) {
						sbServices = new StringBuilder();
						sbServices.append("," + getService()[i] + ",");
					} else {
						sbServices.append(getService()[i] + ",");
					}
				}
			}
			if(sbServices == null) {
				sbServices = new StringBuilder();
			}
			
			pst = con.prepareStatement("UPDATE employee_official_details SET depart_id=?,supervisor_emp_id=?,hod_emp_id=?,service_id=?," +
					"wlocation_id=?,emptype=?,grade_id=?,org_id=?,emp_hr=?,emp_contractor=?,is_hod=?,is_cxo=?,corporate_mobile_no=?," +
					"corporate_desk=?,biometrix_id=?,is_roster=?,paycycle_duration=? WHERE emp_id=?");
			pst.setInt(1, uF.parseToInt(getDepartment()));
			pst.setInt(2, uF.parseToInt(getSupervisor()));
			pst.setInt(3, uF.parseToInt(getHod()));
//			setHod(rs.getString("supervisor_emp_id"));
			pst.setString(4, sbServices.toString());
			pst.setInt(5, uF.parseToInt(getwLocation()));
			pst.setString(6, getEmpType());
			pst.setInt(7, uF.parseToInt(getEmpGrade()));
			pst.setInt(8, uF.parseToInt(getOrgId()));
			pst.setInt(9, uF.parseToInt(getHR()));
			pst.setInt(10, uF.parseToInt(getEmpContractor()));
			pst.setBoolean(11, (uF.parseToInt(getStrCXOHOD()) == 2) ? true : false);
			pst.setBoolean(12, (uF.parseToInt(getStrCXOHOD()) == 1) ? true : false);
			pst.setString(13, getEmpCorporateMobileNo());
			pst.setString(14, getEmpCorporateDesk());
			pst.setInt(15, uF.parseToInt(getBioId()));
			pst.setBoolean(16, uF.parseToBoolean(getRosterDependency()));
			pst.setString(17, getStrPaycycleDuration());
			pst.setInt(18, uF.parseToInt(getEmpId()));
//			System.out.println("Update Pst =====> " + pst);
			int cnt = pst.executeUpdate();
			pst.close();
//			System.out.println("cnt =====> " + cnt);
			
// -------------------------------- Clock On/Off Access Control Start ----------------------------------------			
			pst = con.prepareStatement("select * from emp_clock_on_off_access where emp_id=?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			boolean accessFlag = false;
			while (rs.next()) {
				accessFlag = true;
			}
			rs.close();
			pst.close();
			
			if(!accessFlag) {
				pst = con.prepareStatement("insert into emp_clock_on_off_access (emp_id,is_web_access,is_mobile_access,is_biomatric_access) values(?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.setBoolean(2, true);
				pst.setBoolean(3, true);
				pst.setBoolean(4, true);
				pst.executeUpdate();
				pst.close();
			}
// -------------------------------- Clock On/Off Access Control End ----------------------------------------
			
			
			if(uF.parseToInt(getStrCXOHOD()) == 2) {
				Map<String, String> hmUserTypeID = CF.getUserTypeIdMap(con);
				pst = con.prepareStatement("UPDATE user_details SET usertype_id=? WHERE emp_id=?");
				pst.setInt(1, uF.parseToInt(hmUserTypeID.get(HOD)));
				pst.setInt(2, uF.parseToInt(getEmpId()));
				pst.executeUpdate();
				pst.close();
			}
			
			if(uF.parseToInt(getStrCXOHOD()) == 1) {
				StringBuilder sbwLocId = null;
				for(int i=0; locationCXO != null && i<locationCXO.length; i++) {
					if(uF.parseToInt(locationCXO[i]) > 0) {
						if(sbwLocId == null) {
							sbwLocId = new StringBuilder();
							sbwLocId.append("," + locationCXO[i]+",");
						} else {
							sbwLocId.append(locationCXO[i]+",");
						}
					}
				}
				if(uF.parseToInt(getOrgId())>0 && (locationCXO == null || locationCXO.length==0)) {
					pst = con.prepareStatement("select wlocation_id from work_location_info where org_id in("+ getOrgId() +")");
					rs = pst.executeQuery();
					while (rs.next()) {
						if(rs.getInt("wlocation_id") > 0) {
							if(sbwLocId == null) {
								sbwLocId = new StringBuilder();
								sbwLocId.append("," + rs.getInt("wlocation_id") +",");
							} else {
								sbwLocId.append(rs.getInt("wlocation_id") +",");
							}
						}
					}
					rs.close();
					pst.close();
				}
				if(sbwLocId == null) sbwLocId = new StringBuilder();
				
				Map<String, String> hmUserTypeID = CF.getUserTypeIdMap(con);
				pst = con.prepareStatement("UPDATE user_details SET org_id_access=?,wlocation_id_access=?,usertype_id=? WHERE emp_id=?");
				pst.setString(1, ","+getOrgId()+",");
				pst.setString(2, sbwLocId.toString());
				pst.setInt(3, uF.parseToInt(hmUserTypeID.get(CEO)));
				pst.setInt(4, uF.parseToInt(getEmpId()));
				pst.executeUpdate();
				pst.close();
			}
			
			if(uF.parseToInt(getStrCXOHOD()) == 0) {	
				Map<String, String> hmUserTypeID = CF.getUserTypeIdMap(con);
				pst = con.prepareStatement("update user_details set usertype_id=?, org_id_access=?, wlocation_id_access=? where emp_id=? and (usertype_id=? or usertype_id=?)");
				pst.setInt(1, uF.parseToInt(hmUserTypeID.get(EMPLOYEE)));
				pst.setString(2, null);
				pst.setString(3, null);
				pst.setInt(4, uF.parseToInt(getEmpId()));
				pst.setInt(5, uF.parseToInt(hmUserTypeID.get(HOD)));
				pst.setInt(6, uF.parseToInt(hmUserTypeID.get(CEO)));
				pst.execute();
				pst.close();
			}
			
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			pst = con.prepareStatement("select usertype_id from user_details WHERE emp_id=?");
			pst.setInt(1, uF.parseToInt(getSupervisor()));
			rs = pst.executeQuery();
			int userTypeId = 0;
			while(rs.next()) {
				userTypeId = rs.getInt("usertype_id");
			}
			rs.close();
			pst.close();
			
			if(userTypeId == uF.parseToInt(hmUserTypeId.get(EMPLOYEE))) {
				pst = con.prepareStatement("UPDATE user_details SET usertype_id=? WHERE emp_id=?");
				pst.setInt(1, uF.parseToInt(hmUserTypeId.get(MANAGER)));
				pst.setInt(2, uF.parseToInt(getSupervisor()));
				pst.executeUpdate();
				pst.close();
			}
			
			if(nSuperWiserId == 0 || nSuperWiserId != uF.parseToInt(getSupervisor())) {
				String strDomain = request.getServerName().split("\\.")[0];
				String alertData = "<div style=\"float: left;\"> <b>"+CF.getEmpNameMapByEmpId(con, getEmpId()+"")+"</b> has been added in your team by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
				String alertAction = "TeamStructure.action?pType=WR";
				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(getSupervisor());
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
				userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
				userAlerts.setStatus(INSERT_WR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
				
//				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//				userAlerts.setStrDomain(strDomain);
//				userAlerts.setStrEmpId(getSupervisor());
//				userAlerts.set_type(ADD_MYTEAM_MEMBER_ALERT);
//				userAlerts.setStatus(INSERT_ALERT);
//				Thread t = new Thread(userAlerts);
//				t.run();
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void getCurrOrgWLocDeptSBUGrade(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
//			pst = con.prepareStatement("SELECT * FROM work_location_info where org_id = ? order by wlocation_id limit 1");
//			pst.setInt(1, uF.parseToInt(getOrgId()));
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				setwLocation(rs.getString("wlocation_id"));
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select * from department_info where org_id = ? order by dept_id limit 1");
//			pst.setInt(1, uF.parseToInt(getOrgId()));
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				setDepartment(rs.getString("dept_id"));
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("SELECT * FROM services where org_id = ? order by service_id limit 1");
//			pst.setInt(1, uF.parseToInt(getOrgId()));
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				setServices(rs.getString("service_id"));
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select * from grades_details where designation_id = ? order by grade_id limit 1");
//			pst.setInt(1, uF.parseToInt(getStrDesignation()));
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				setEmpGrade(rs.getString("grade_id"));
//			}
//			rs.close();
//			pst.close();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void updateEmpPersonalDetails(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		try {
			
			String strEmpStatus = PERMANENT;
			if(uF.parseToInt(getEmpStatus()) == 1) {
				strEmpStatus = PROBATION;
			} else if(uF.parseToInt(getEmpStatus()) == 2) {
				strEmpStatus = PERMANENT;
			} else if(uF.parseToInt(getEmpStatus()) == 4) {
				strEmpStatus = TEMPORARY;
			}
//				empcode, emp_fname,emp_mname, emp_lname, emp_email, emp_status, " +
//				" salutation, emp_gender, joining_date, is_alive,emp_filled_flag,emp_filled_flag_date, emp_entry_date,emp_address1,is_one_step" +
			pst = con.prepareStatement("UPDATE employee_personal_details SET empcode=?, emp_fname=?, emp_mname=?, emp_lname=?, emp_email=?," +
				" salutation=?, emp_gender=?,emp_date_of_birth=?, joining_date=?,skype_id=?, emp_email_sec=?, emp_status=? WHERE emp_per_id=?");
			pst.setString(1, uF.showData(getEmpCodeAlphabet(), "")+uF.showData(getEmpCodeNumber(),""));
			pst.setString(2, getEmpFname());
			pst.setString(3, getEmpMname());
			pst.setString(4, getEmpLname());
			pst.setString(5, getEmpEmail());
			pst.setString(6, getSalutation());
			pst.setString(7, getEmpGender());
			pst.setDate(8, uF.getDateFormat(getEmpDateOfBirth(), DATE_FORMAT));
			pst.setDate(9, uF.getDateFormat(getEmpStartDate(), DATE_FORMAT));
			pst.setString(10, getSkypeId());
			pst.setString(11, getEmpEmailSec());
			pst.setString(12, strEmpStatus);
			pst.setInt(13, uF.parseToInt(getEmpId()));
//				log.debug("pst updateEmployeeP==>"+pst);
			pst.execute();
			pst.close();
				
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void uploadImage(int empId2,int type) {
		try {
			if(type == 1) {
				UploadImage uI = new UploadImage();
				uI.setServletRequest(request);
				uI.setImageType("EMPLOYEE_IMAGE");
				uI.setEmpImage(getEmpImage());
				uI.setEmpImageFileName(getEmpImageFileName());
				uI.setEmpId(empId2+"");
				uI.setCF(CF);
				uI.upoadImage();
			}else if(type == 2) {
				UploadImage uI = new UploadImage();
				uI.setServletRequest(request);
				uI.setImageType("EMPLOYEE_COVER_IMAGE");
				uI.setEmpImage(getEmpCoverImage());
				uI.setEmpImageFileName(getEmpCoverImageFileName());
				uI.setEmpId(empId2+"");
				uI.setCF(CF);
				uI.upoadImage();
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			
		}
	}

	public void insertUser(Connection con, UtilityFunctions uF, int empId) {
		PreparedStatement pst = null;
		
		try {
			Map<String,String> userPresent= CF.getUsersMap(con);
			AddEmployeeMode aE = new AddEmployeeMode();
			aE.setServletRequest(request);
			aE.CF = CF;
			aE.session = session;
			aE.setFname(getEmpFname());
			aE.setMname(getEmpMname());
			aE.setLname(getEmpLname());
			String username = aE.getUserName(userPresent);
			
			SecureRandom random = new SecureRandom();
			String password = new BigInteger(130, random).toString(32).substring(5, 13);
			
			pst = con.prepareStatement(insertUser);
			
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			pst.setString(1, username);
			pst.setString(2, password);
			pst.setInt(3, uF.parseToInt(hmUserTypeId.get(EMPLOYEE)));
			pst.setInt(4, empId);
			pst.setString(5, "ACTIVE");
			pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String insertEmpPersonalDetails(Connection con, UtilityFunctions uF, CommonFunctions CF) {
		PreparedStatement pst = null;
		ResultSet rs =null;
		String empPerId = "";
		try {
			
			String strEmpStatus = PERMANENT;
			if(uF.parseToInt(getEmpStatus()) == 1){
				strEmpStatus = PROBATION;
			} else if(uF.parseToInt(getEmpStatus()) == 2){
				strEmpStatus = PERMANENT;
			} else if(uF.parseToInt(getEmpStatus()) == 4){
				strEmpStatus = TEMPORARY;
			}
			
			pst = con.prepareStatement("INSERT INTO employee_personal_details (empcode, emp_fname,emp_mname, emp_lname, emp_email, emp_status, " +
				" salutation, emp_gender,emp_date_of_birth , joining_date, is_alive,emp_filled_flag,emp_filled_flag_date, emp_entry_date," +
				"emp_address1,is_one_step,skype_id, emp_email_sec) " +
				"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?) ");
			pst.setString(1, uF.showData(getEmpCodeAlphabet(), "")+uF.showData(getEmpCodeNumber(),""));
			pst.setString(2, getEmpFname());
			pst.setString(3, getEmpMname());
			pst.setString(4, getEmpLname());
			pst.setString(5, getEmpEmail());
			pst.setString(6, strEmpStatus);
			pst.setString(7, getSalutation());
			pst.setString(8, getEmpGender());
			pst.setDate(9, uF.getDateFormat(getEmpDateOfBirth(), DATE_FORMAT));
			pst.setDate(10, uF.getDateFormat(getEmpStartDate(), DATE_FORMAT));
			pst.setBoolean(11, true);
			pst.setBoolean(12, true);
			pst.setTimestamp(13, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setTimestamp(14, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setString(15, "");
			pst.setBoolean(16, true);
			pst.setString(17, getSkypeId());
			pst.setString(18, getEmpEmailSec());
			pst.execute();
//			System.out.println("pst ===>> " + pst);
			pst.close();
			
			
			pst = con.prepareStatement(selectMaxEmpId);
			rs = pst.executeQuery();
			while(rs.next()) {
				empPerId = rs.getString(1);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("INSERT INTO employee_official_details(emp_id) VALUES (?)");
			pst.setInt(1, uF.parseToInt(empPerId));
			pst.execute();
			pst.close();
			
			session.setAttribute("EMPNAME_P", getEmpFname()+" "+ getEmpMname()+" "+getEmpLname());
			session.setAttribute("EMPID_P", getEmpId()+"");
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return empPerId;
	}

	public String deleteEmployee(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		
		try {
			pst = con.prepareStatement("update employee_personal_details set approved_flag=?, is_alive=?,emp_filled_flag=?,is_delete=? WHERE emp_per_id=? ");
			pst.setBoolean(1, false);
			pst.setBoolean(2, false);
			pst.setBoolean(3, false);
			pst.setBoolean(4, true);
			pst.setInt(5, uF.parseToInt(getEmpId()));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return VIEW;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getEmpCoverImageFileName() {
		return empCoverImageFileName;
	}

	public void setEmpCoverImageFileName(String empCoverImageFileName) {
		this.empCoverImageFileName = empCoverImageFileName;
	}

	public File getEmpCoverImage() {
		return empCoverImage;
	}

	public void setEmpCoverImage(File empCoverImage) {
		this.empCoverImage = empCoverImage;
	}
	
	public String getEmpMname() {
		return empMname;
	}

	public void setEmpMname(String empMname) {
		this.empMname = empMname;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
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

	public String getEmpGender() {
		return empGender;
	}
	
	public void setEmpGender(String empGender) {
		this.empGender = empGender;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public List<FillEmployee> getSupervisorList() {
		return supervisorList;
	}

	public void setSupervisorList(List<FillEmployee> supervisorList) {
		this.supervisorList = supervisorList;
	}

	public String getEmpEmail() {
		return empEmail;
	}

	public void setEmpEmail(String empEmail) {
		this.empEmail = empEmail;
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

	public String getEmpUserTypeId() {
		return empUserTypeId;
	}

	public void setEmpUserTypeId(String empUserTypeId) {
		this.empUserTypeId = empUserTypeId;
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

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getEmpContractor() {
		return empContractor;
	}

	public void setEmpContractor(String empContractor) {
		this.empContractor = empContractor;
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

	public String getStrDesignation() {
		return strDesignation;
	}
	
	public void setStrDesignation(String strDesignation) {
		this.strDesignation = strDesignation;
	}

	public List<FillSalutation> getSalutationList() {
		return salutationList;
	}

	public void setSalutationList(List<FillSalutation> salutationList) {
		this.salutationList = salutationList;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getwLocation() {
		return wLocation;
	}

	public void setwLocation(String wLocation) {
		this.wLocation = wLocation;
	}

	public String getEmpGrade() {
		return empGrade;
	}

	public void setEmpGrade(String empGrade) {
		this.empGrade = empGrade;
	}

	public String[] getService() {
		return service;
	}

	public void setService(String[] service) {
		this.service = service;
	}

	public String getEmpStartDate() {
		return empStartDate;
	}

	public void setEmpStartDate(String empStartDate) {
		this.empStartDate = empStartDate;
	}

	public String getBtnSubmit() {
		return btnSubmit;
	}

	public void setBtnSubmit(String btnSubmit) {
		this.btnSubmit = btnSubmit;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	public List<FillEmployee> getHRList() {
		return HRList;
	}

	public void setHRList(List<FillEmployee> hRList) {
		HRList = hRList;
	}

	public List<FillEmployee> getHodList() {
		return HodList;
	}

	public void setHodList(List<FillEmployee> hodList) {
		HodList = hodList;
	}

	public String getStrCXOHOD() {
		return strCXOHOD;
	}

	public void setStrCXOHOD(String strCXOHOD) {
		this.strCXOHOD = strCXOHOD;
	}

	public String getDefaultCXO() {
		return defaultCXO;
	}

	public void setDefaultCXO(String defaultCXO) {
		this.defaultCXO = defaultCXO;
	}

	public String getDefaultHOD() {
		return defaultHOD;
	}

	public void setDefaultHOD(String defaultHOD) {
		this.defaultHOD = defaultHOD;
	}

	public String[] getLocationCXO() {
		return locationCXO;
	}

	public void setLocationCXO(String[] locationCXO) {
		this.locationCXO = locationCXO;
	}

	public List<String> getCxoLocationAccess() {
		return cxoLocationAccess;
	}

	public void setCxoLocationAccess(List<String> cxoLocationAccess) {
		this.cxoLocationAccess = cxoLocationAccess;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public String getHod() {
		return hod;
	}

	public void setHod(String hod) {
		this.hod = hod;
	}

	public String getHR() {
		return HR;
	}

	public void setHR(String hR) {
		HR = hR;
	}

	public String getEmpDateOfBirth() {
		return empDateOfBirth;
	}

	public void setEmpDateOfBirth(String empDateOfBirth) {
		this.empDateOfBirth = empDateOfBirth;
	}
	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public List<FillDepartment> getDeptList() {
		return deptList;
	}

	public void setDeptList(List<FillDepartment> deptList) {
		this.deptList = deptList;
	}

	public String getEmpType() {
		return empType;
	}

	public void setEmpType(String empType) {
		this.empType = empType;
	}

	public List<FillEmploymentType> getEmpTypeList() {
		return empTypeList;
	}

	public void setEmpTypeList(List<FillEmploymentType> empTypeList) {
		this.empTypeList = empTypeList;
	}

	public int getProbationDuration() {
		return probationDuration;
	}

	public void setProbationDuration(int probationDuration) {
		this.probationDuration = probationDuration;
	}

	public List<FillProbationDuration> getProbationDurationList() {
		return probationDurationList;
	}

	public void setProbationDurationList(List<FillProbationDuration> probationDurationList) {
		this.probationDurationList = probationDurationList;
	}

	public int getNoticeDuration() {
		return noticeDuration;
	}

	public void setNoticeDuration(int noticeDuration) {
		this.noticeDuration = noticeDuration;
	}

	public List<FillNoticeDuration> getNoticeDurationList() {
		return noticeDurationList;
	}

	public void setNoticeDurationList(List<FillNoticeDuration> noticeDurationList) {
		this.noticeDurationList = noticeDurationList;
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

	public void setRosterDependencyList(List<FillApproval> rosterDependencyList) {
		this.rosterDependencyList = rosterDependencyList;
	}

	public String[] getProbationLeaves() {
		return probationLeaves;
	}

	public void setProbationLeaves(String[] probationLeaves) {
		this.probationLeaves = probationLeaves;
	}

	public List<FillLeaveType> getLeaveTypeList() {
		return leaveTypeList;
	}

	public void setLeaveTypeList(List<FillLeaveType> leaveTypeList) {
		this.leaveTypeList = leaveTypeList;
	}

	public String getDefaultStatus() {
		return defaultStatus;
	}

	public void setDefaultStatus(String defaultStatus) {
		this.defaultStatus = defaultStatus;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public String getEmpStatus() {
		return empStatus;
	}

	public void setEmpStatus(String empStatus) {
		this.empStatus = empStatus;
	}

	public String getEmpCorporateMobileNo() {
		return empCorporateMobileNo;
	}

	public void setEmpCorporateMobileNo(String empCorporateMobileNo) {
		this.empCorporateMobileNo = empCorporateMobileNo;
	}

	public String getEmpCorporateDesk() {
		return empCorporateDesk;
	}

	public void setEmpCorporateDesk(String empCorporateDesk) {
		this.empCorporateDesk = empCorporateDesk;
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

	public String getBioId() {
		return bioId;
	}

	public void setBioId(String bioId) {
		this.bioId = bioId;
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

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}
	
}