package com.konnect.jpms.employee;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.document.HeaderFooterPageEvent;
import com.konnect.jpms.performance.FillAttribute;
import com.konnect.jpms.recruitment.FillEducational;
import com.konnect.jpms.reports.EmployeeLeaveEntryReport;
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
import com.konnect.jpms.select.FillFinancialYears;
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
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.LogDetails;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddEmployee extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strUserType = null;
	String strBaseUserType = null;
	String strWLocationAccess =  null;
	String strSessionEmpId = null; 
	private String mode;
	private String empId;
	private String org_id;
	private String operation;
	Boolean autoGenerate = false; 
	private String step;
	private String strStep;
	private String serviceId;  
	private String ServiceName;
	StringBuilder sbServicesLink = new StringBuilder();
	public CommonFunctions CF = null;
	public static String MOTHER = "MOTHER";
	public static String FATHER = "FATHER";
	public static String SPOUSE = "SPOUSE";
	public static String SIBLING = "SIBLING";
	public static String CHILD = "CHILD";
	public String dobYear="1977";

	private static Logger log = Logger.getLogger(AddEmployee.class);
	
	private String prevEarnDeductId;
	private String prevEmpFYear;
	private String prevTotalEarning;
	private String prevTotalDeduction;
	private String prevEarnDeductFile;
	private File prevFileEarnDeduct;
	private String prevFileEarnDeductFileName;
	private String isForm16;
	private String isForm16A;
	private String slabType;
	private String empESICNo;
	private String prevPANNumber;
	private String prevTANNumber;
	
	
	private List<FillAttribute> attributeList;
	
	private String defaultStatus;
	private String empStatus;
	
//	private String isCXO;
//	private String isHOD;
	private String strCXOHOD;
	private String defaultCXO;
	private String defaultHOD;
	private String[] locationCXO;
	
	private String empBankName2;
	private String empBankAcctNbr2;
	private String empBankIFSCNbr2;


	private String spouseMaritalStatus;
	

	private String hod;
	private String HR;
//	private String service;
	private String[] service;
	private String availFrom;
	private String availTo;
	private String rosterDependency;
	private String attendanceDependency;
	private String empType;
	private boolean isFirstAidAllowance;
	private int probationDuration;
	private int noticeDuration;
	private String[] goalElements;	
	
	
	private String[] elementAttribute;
	private String[] empKRA;
	private String[] empKRATask;
	private String[] empKraId;
	private String[] empKraTaskId;
	
	private String[] desigElements;
	private String[] desigElementAttribute;
	private String[] desigKraId;
	private String[] desigKraTaskId;
	private String[] desigEmpKraId;
	private String[] desigEmpKraTaskId;
	private String[] designKRA;
	private String[] designKRATask;
	private String[] status;
	
	private String[] probationLeaves;

	
	private String[] skillName;
	private String[] skillValue;
	
	private String[] hobbyName;
	
	private String[] languageName;
	private String[] isRead;
	private String[] isWrite;
	private String[] isSpeak;
	private String[] isMotherTounge;

	private String[] degreeId;
	private String[] degreeName;
	private String[] degreeDuration;
	private String[] completionYear;
	private String[] grade;
	private String[] degreeCertiStatus;
//	private File[] degreeCertificate;
//	private String[] degreeCertificateFileName;
	private String[] instName;
//	private String[] degreeNameOther;
	
	private String[] docId;
	private File[] idDoc;
	private String[] idDocFileName;
	private String[] idDocStatus;
	private String[] idDocName;
	private String[] idDocType;
	
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
	
	private String fatherName;
	private String fatherDob;
	private String fatherEducation;
	private String fatherOccupation;
	private String fatherContactNumber;
	private String fatherEmailId;
	private String fatherMRDNo;
	
	private String motherName;
	private String motherDob;
	private String motherEducation;
	private String motherOccupation;
	private String motherContactNumber;
	private String motherEmailId;
	private String motherMRDNo;
	
	private String spouseName;
	private String spouseDob;
	private String spouseEducation;
	private String spouseOccupation;
	private String spouseContactNumber;
	private String spouseEmailId;
	private String spouseGender;
	private String spouseMRDNo;
	
	private String[] memberName;
	private String[] memberDob;
	private String[] memberEducation;
	private String[] memberOccupation;
	private String[] memberContactNumber;
	private String[] memberEmailId;
	private String[] siblingsMRDNo;
	private String[] memberGender;
	private String[] siblingMaritalStatus;
	
	private String[] childName;
	private String[] childDob;
	private String[] childEducation;
	private String[] childOccupation;
	private String[] childContactNumber;
	private String[] childEmailId;
	private String[] childMRDNo;
	private String[] childGender;
	private String[] childMaritalStatus;
	               
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
	private String[] prevCompanyUANNo;		//created by parvez date: 08-08-2022
	private String[] prevCompanyESICNo;		//created by parvez date: 08-08-2022
	
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
	private String que1DocName;
	private String que2DocName;
	private String que3DocName;
	private String que4DocName;
	private String que5DocName;
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
	
	
//	private String empDesignation;
	
	private List<FillEmploymentType> empTypeList;
	private List<FillApproval> rosterDependencyList;
	private List<FillGrade> gradeList;
	private List<FillDesig> desigList;
	private List<FillLevel> levelList;
	private List<FillDepartment> deptList;
	private List<FillEmployee> supervisorList;
	private List<FillEmployee> HRList;
	private List<FillEmployee> HodList;
	private List<FillServices> serviceList;
	private List<FillCountry> countryList;
	private List<FillState> stateList;
	private List<FillCity> cityList;
	private List<FillWLocation> wLocationList;
	private List<FillOrganisation> orgList;
	private List<FillBank> bankList;
	private List<FillGender> empGenderList;
	private List<FillProbationDuration> probationDurationList;
	private List<FillNoticeDuration> noticeDurationList;
	private List<FillLeaveType> leaveTypeList;
	private List<FillMaritalStatus> maritalStatusList;
	private List<FillBloodGroup> bloodGroupList;
	private List<FillEducational> educationalList;
	private List<FillDegreeDuration> degreeDurationList;
	private List<FillYears> yearsList;
	
	private List<FillSkills> skillsList;
	private List<FillSalutation> salutationList;
	private List<FillFinancialYears> financialYearList;
	private List<String> prevCompList;
	
	private String salutation;
	
	private String empPaymentMode;
	private String empCorporateMobileNo;
	private String empCorporateDesk;
	
	private List<FillPayMode> paymentModeList;
	
	private List<FillPayCycleDuration> paycycleDurationList;
	private String strPaycycleDuration;
	
	private List<String> cxoLocationAccess = new ArrayList<String>();
	private List<String> leavesValue = new ArrayList<String>();
	
	private String empUserTypeId;
	private String empContractor;
	private String empCodeAlphabet;
	private String empCodeNumber;
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
	private String empDoctorName;
	private String empDoctorNo;
	private String empPassportNo;
	private String empPassportExpiryDate;
	private String empPFStartDate;
	private String empBloodGroup;
	private String empMaritalStatus;
	private boolean approvedFlag;
	
	private Timestamp empFilledFlagDate;
	
	private String empImageFileName;
	private File empImage;
	
	private String empCoverImageFileName;
	private File empCoverImage;
	
	private String empUANNo;
	private String empUIDNo;
	private String empPanNo;
	private String empMRDNo;
	private String empPFNo;
	private String empGPFNo;
	private String empGender;
	private String empDateOfBirth;
	private String empDateOfMarriage;
	
	private String empBankName;
	private String empBankAcctNbr;
	private String empBankIFSCNbr;
	
	private String empEmailSec;
	private String skypeId;
	private String empMobileNo;
	
	private String[] refName;
	private String[] refCompany;
	private String[] refCompanyOther;
	private String[] refDesignation;
	private String[] refContactNo;
	private String[] refEmail;
	

	private String empStartDate;
	private String wLocation;
	private String orgId;
	private String empGrade;
	private String strLevel;
	private String strDesignation;
	private String department;
	private String supervisor;
	private String bioId;
	
	private String empSeparationDate;
	private String empConfirmationDate;
	private String empActConfirmDate;
	private String empPromotionDate;
	private String empIncrementDate;

	private boolean isMedicalProfessional;
	private String strEmpKncKmc;
	private String strKmcNo;
	private String strKncNo;
	private String strRenewalDate;

	private String isMedicalCheck;
	
	private String strDOBMaxDate;
	private String strCurrDate;
	
	private String isTdsDetails;
	private String strAction=null;
	
	private String empEmergencyContactRelation;		//added by parvez date: 30-07-2022
	private String empOtherBankName;
	private String empOtherBankName2;
	
	private String empOtherBankBranch;
	private String empOtherBankBranch2;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) {
			CF = new CommonFunctions();
			CF.setRequest(request); 
		} 
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);
		strWLocationAccess = (String)session.getAttribute(WLOCATION_ACCESS);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		UtilityFunctions uF = new UtilityFunctions();
		
//		java.util.Date dtCurDt = uF.getDateFormatUtil(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
		String strDayDt = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "dd");
		String strMonthDt = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM");
		int intYearDt = uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"));
		intYearDt = intYearDt - 18;
//		java.util.Date dtDOBDt = uF.getFutureDate(dtCurDt, -intPrevYearDays);
//		System.out.println("dtDOBDt ===>> " + dtDOBDt);
//		String strDOBDt = uF.getDateFormat(dtDOBDt+"", DBDATE, DATE_MM_DD_YYYY);
		setStrDOBMaxDate(strMonthDt+"/"+strDayDt+"/"+intYearDt);
		setStrCurrDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_MM_DD_YYYY));
//		System.out.println("getStrDOBMaxDate() ===>> " + getStrDOBMaxDate());
		
		request.setAttribute("dobYear", dobYear);
		
		request.setAttribute(PAGE, PAddEmployee);
		request.setAttribute(TITLE, TAddEmployee);
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		//Created By Dattatray 10-6-2022
		strAction = request.getServletPath();
		if(strAction!=null) {
			strAction = strAction.replace("/","");
		}
		loadPageVisitAuditTrail(uF,"");//Created By Dattatray 15-06-2022

//		EncryptionUtility eU = new EncryptionUtility();
		/*System.out.println("getEmpId ===>> " + getEmpId());
		setEmpId(URLEncoder.encode(getEmpId()));
		System.out.println("getEmpId URLEncoder.encode(getEmpId()) ===>> " + getEmpId());
		setEmpId(URLDecoder.decode(getEmpId()));
		System.out.println("getEmpId URLDecoder.decode(getEmpId()) ===>> " + getEmpId());
		if(getEmpId() != null && uF.parseToInt(getEmpId()) == 0) {
			String decodeEmpId = eU.decode(getEmpId());
			setEmpId(decodeEmpId);
		}
		System.out.println("getEmpId after decode ===>> " + getEmpId());
//		System.out.println("getStep ===>> " + getStep()); 
		if(getStep() != null && !getStep().equalsIgnoreCase("null") && !getStep().trim().equalsIgnoreCase("") && uF.parseToInt(getStep()) == 0) {
			String decodeStep = eU.decode(getStep());
			setStep(decodeStep);
		}*/
		
//		System.out.println("getEmpId() 0 ===>> " + getEmpId());
//		System.out.println("getMode() 0 ===>> " + getMode());
//		System.out.println("getOperation() 0 ===>> " + getOperation());
		
		List<String> accessEmpList = CF.viewEmployeeIdsList(request, uF, strBaseUserType, strSessionEmpId, strWLocationAccess);
		if((strBaseUserType != null && strUserType != null && (strBaseUserType.equals(EMPLOYEE) || strUserType.equals(EMPLOYEE))) || (!accessEmpList.contains(getEmpId()) && uF.parseToInt(getEmpId()) > 0)) {
			setEmpId(strSessionEmpId);
		}
		if(strBaseUserType != null && strUserType != null && (strBaseUserType.equals(EMPLOYEE) || strBaseUserType.equals(MANAGER) || strUserType.equals(EMPLOYEE) || strUserType.equals(MANAGER)) ) {
			if(uF.parseToInt(getStrStep())>5) {
				setStrStep("1");
			}
		}
		
		if(uF.parseToInt(getStrStep())==9) {
			if(strBaseUserType!=null && !strBaseUserType.equalsIgnoreCase(ADMIN) && !strBaseUserType.equalsIgnoreCase(ACCOUNTANT) 
				&& !strBaseUserType.equalsIgnoreCase(HRMANAGER) && !strBaseUserType.equalsIgnoreCase(CEO) && !strBaseUserType.equalsIgnoreCase(RECRUITER)) { // && !strBaseUserType.equalsIgnoreCase(MANAGER) && !strBaseUserType.equalsIgnoreCase(HOD)
	
				request.setAttribute(PAGE, PAccessDenied);
				request.setAttribute(TITLE, TAccessDenied); 
				return ACCESS_DENIED;
			}
		}
		
//		System.out.println("getEmpId after strSessionEmpId ===>> " + getEmpId()+" -- getStrStep() ===>> " + getStrStep());
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-group\"></i><a href=\"People.action\" style=\"color: #3c8dbc;\"> People</a></li>"
				+"<li>Add Employee in 8 step</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		CF.getFormValidationFields(request, ADD_UPDATE_EMPLOYEE);
		
		
		if (getIsMedicalCheck() != null) {
			setMedicalProfessional(uF.parseToBoolean(getIsMedicalCheck()));
		}
//		System.out.println("emp java empdateOfbirth==>"+getEmpDateOfBirth());
		if(getDefaultCXO() == null || getDefaultCXO().equals("")) {
			setDefaultCXO("0");
		}
		if(getDefaultHOD() == null || getDefaultHOD().equals("")) {
			setDefaultHOD("0");
		}
		
		setDefaultStatus("1");
		
		if (getStrEmpKncKmc() == null || getStrEmpKncKmc().equals("") || getStrEmpKncKmc().equalsIgnoreCase("null")) {
			setStrEmpKncKmc("0");
		}
//		System.out.println("in execute mode ==>"+getMode()+"==>step ==>"+getStep());
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		
		try {
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			getFormValidationFieldWise(uF);
				
			if(CF.getStrTimeZone() == null || CF.getStrTimeZone().equals("")) {
				Map<String, String> hmSettings = CF.getSettingsMap(con);
				CF.setStrTimeZone(hmSettings.get(O_TIME_ZONE));
			}
			
			if(uF.parseToInt(getEmpId()) > 0) {
				getPrevCompNameList(con, uF, uF.parseToInt(getEmpId()), null);
			}
		
		if (getOperation()!=null && getOperation().equals("U") && uF.parseToInt(getEmpId()) != 0 && getMode() != null) {
			request.setAttribute(TITLE, TEditEmployee);
			viewEmployee(con);
			loadValidateEmployee(con);
			return SUCCESS;
			
		} else if(getOperation()!=null && getOperation().equals("ajax")) {
			updateEmployeeAjax(con);
			return UPDATE;
			
		} else if (getOperation()!=null && getOperation().equals("D")) {
			deleteEmployee(con);
			return "deletePending";
		
		} else if(getOperation()!=null && getOperation().equals("SA")) {
			loadPageVisitAuditTrail(uF, "Resended appointment letter to employee");//Created by Dattatray 13-06-2022
			sendAttachDocument(con,uF.parseToInt(ACTIVITY_APPOINTMENT_ID), uF.parseToInt(getEmpId()),"",CF,uF,"");
			return "ajax";
		} else if (uF.parseToInt(getEmpId())!=0 && getMode()!=null && (getMode().equals("profile") || getMode().equals("onboard") || getMode().equals("report"))) {
			
//			System.out.println("getEmpId() ===>> " + getEmpId());
//			System.out.println("getMode() ===>> " + getMode());
//			System.out.println("getOperation() ===>> " + getOperation());
			
			if(getOperation()==null || !getOperation().equals("EO")) {
				updateEmployee(con);
			}
			if (!getMode().equals("onboard")) {
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_UPD_EMPLOYEE_PROFILE, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrEmpId(getEmpId()+"");
					
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setEmailTemplate(true);
				nF.sendNotifications();
			} else if (getMode().equals("onboard")) {
				if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
					String strDomain = request.getServerName().split("\\.")[0];
					Notifications nF1 = new Notifications(N_NEW_EMPLOYEE, CF);
					nF1.setDomain(strDomain);
					nF1.request = request;
					nF1.setStrEmpId(getEmpId()+"");
//					nF1.setStrHostAddress(request.getRemoteHost());
					nF1.setStrHostAddress(CF.getStrEmailLocalHost());
					nF1.setStrHostPort(CF.getStrHostPort());
					nF1.setStrContextPath(request.getContextPath());
//					nF1.setServletRequest(request); 
					nF1.setEmailTemplate(true);
					nF1.sendNotifications();
				}
			}
			
			
			if (getMode().equals("report")) {
				return REPORT;
			} else if (getMode().equals("profile")) {
				/*if(uF.parseToInt(getEmpId()) > 0) {
					String encodeEmpId = eU.encode(getEmpId());
					setEmpId(encodeEmpId);
				}*/
				return PROFILE;
			} else {
				viewEmployee(con);
				loadValidateEmployee(con);
				
				if(uF.parseToInt(getStep()) == 0) {
					setStep("1");
				} else if(uF.parseToInt(getStep()) == 1) {
					setStep("2");
				} else if(uF.parseToInt(getStep()) == 2) {
					setStep("3");
				} else if(uF.parseToInt(getStep()) == 3) {
					setStep("4");
				} else if(uF.parseToInt(getStep()) == 4) {
					setStep("5");
				} else if(uF.parseToInt(getStep()) == 5) {
					setStep("6");
				} else if(uF.parseToInt(getStep()) == 6) {
					setStep("7");
				} else if(uF.parseToInt(getStep()) == 7) {
					
					if(strUserType!=null && strUserType.equals(EMPLOYEE)) {
						generateEmpCode(con);
						updateEmpFilledStatus(con,uF.parseToInt(getEmpId()));
						setStep("8");
					} else {
//						updateEmpFilledStatus(con,getEmpId());
						if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
							insertEmpActivity(con, getEmpId()+"", CF, strSessionEmpId, null);
						}
//						setStep(10);
						setStep("8");
					}
					
				} else if(uF.parseToInt(getStep()) == 8) {
					if(strBaseUserType!=null && !strBaseUserType.equalsIgnoreCase(ADMIN) && !strBaseUserType.equalsIgnoreCase(ACCOUNTANT) 
						&& !strBaseUserType.equalsIgnoreCase(HRMANAGER) && !strBaseUserType.equalsIgnoreCase(CEO) && !strBaseUserType.equalsIgnoreCase(RECRUITER)) { // && !strBaseUserType.equalsIgnoreCase(MANAGER) && !strBaseUserType.equalsIgnoreCase(HOD)

						request.setAttribute(PAGE, PAccessDenied);
						request.setAttribute(TITLE, TAccessDenied); 
						return ACCESS_DENIED;
					}
					setStep("9");
				} else if(uF.parseToInt(getStep()) == 11) {
					insertAvailability(con,uF, getEmpId()+"");
					setStep("10");
					
				}
				
				if(uF.parseToInt(getStep()) == 10) {
					
					if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
						String strDomain = request.getServerName().split("\\.")[0];
						Notifications nF1 = new Notifications(N_NEW_EMPLOYEE, CF);
						nF1.setDomain(strDomain);
						nF1.request = request;
						nF1.setStrEmpId(getEmpId()+"");
//						nF1.setStrHostAddress(request.getRemoteHost());
						nF1.setStrHostAddress(CF.getStrEmailLocalHost());
						nF1.setStrHostPort(CF.getStrHostPort());
						nF1.setStrContextPath(request.getContextPath());
//						nF1.setServletRequest(request); 
						nF1.setEmailTemplate(true);
						nF1.sendNotifications();
					}
					
					getEmpMiscInfo(con,uF, getEmpId()+"");

					/*request.setAttribute(TITLE, TEditEmployee);
					request.setAttribute(PAGE, PPolicyPayroll2);*/
//					System.out.println("mode ==>"+getMode()+"==>step ==>"+getStep());
					/*if(uF.parseToInt(getEmpId()) > 0) {
						String encodeEmpId = eU.encode(getEmpId());
						setEmpId(encodeEmpId);
					}*/
					return RATE;
				} else {
					request.setAttribute(TITLE, TEditEmployee);
					loadValidateEmployee(con);
					
					return SUCCESS;
				}
				
			}
		
		} else if (uF.parseToInt(getStep())!= 0) {
		
			insertEmployee(con);
			loadValidateEmployee(con);
			if(uF.parseToInt(getStep()) == 1) {
				setStep("2");
			} else if(uF.parseToInt(getStep()) == 2) {
				setStep("3");
			} else if(uF.parseToInt(getStep()) == 3) {
				setStep("4");
			} else if(uF.parseToInt(getStep()) == 4) {
				setStep("5");
			} else if(uF.parseToInt(getStep()) == 5) {
				setStep("6");
			} else if(uF.parseToInt(getStep()) == 6) {
				setStep("7");
			} else if(uF.parseToInt(getStep()) == 7) {
				
				if(strUserType!=null && !strUserType.equals(EMPLOYEE)) {
					generateEmpCode(con);
					updateEmpFilledStatus(con,uF.parseToInt(getEmpId()));
					setStep("8");
				} else {
					
					updateEmpFilledStatus(con,uF.parseToInt(getEmpId()));
					if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
						insertEmpActivity(con,getEmpId()+"", CF, strSessionEmpId, null);
					}
					setStep("10");

				}
				
			} else if(uF.parseToInt(getStep()) == 8) {
				if(strBaseUserType!=null && !strBaseUserType.equalsIgnoreCase(ADMIN) && !strBaseUserType.equalsIgnoreCase(ACCOUNTANT) 
					&& !strBaseUserType.equalsIgnoreCase(HRMANAGER) && !strBaseUserType.equalsIgnoreCase(CEO) && !strBaseUserType.equalsIgnoreCase(RECRUITER)) { // && !strBaseUserType.equalsIgnoreCase(MANAGER) && !strBaseUserType.equalsIgnoreCase(HOD)

					request.setAttribute(PAGE, PAccessDenied);
					request.setAttribute(TITLE, TAccessDenied); 
					return ACCESS_DENIED;
				}
				setStep("9");
			} else if(uF.parseToInt(getStep()) == 11) {
				
				insertAvailability(con,uF, getEmpId()+"");
				setStep("10");
				
			}
			
			if(uF.parseToInt(getStep()) == 10) {
				
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
//					nF.setServletRequest(request);
					nF.setEmailTemplate(true);
					nF.sendNotifications();
				}
				
				getEmpMiscInfo(con,uF, getEmpId()+"");
				
				/*request.setAttribute(TITLE, TAddEmployee);
				request.setAttribute(PAGE, PPolicyPayroll2);*/
				return RATE;
			} else {
				/*loadValidateEmployee(con);
				System.out.println("in 5");*/
				return SUCCESS;
			}
			
		} else if(uF.parseToInt(getStep()) == 0) {
//			System.out.println("step6");
//			log.debug("Default Step..");
			setStep("1");
			request.setAttribute(TITLE, TAddEmployee);
			request.setAttribute(PAGE, PAddEmployee);
			
			loadValidateEmployee(con);
			
			if(strUserType==null) {
				session.setAttribute(MENU, "/jsp/common/PreMenu.jsp");
				
				
				if(validateSession(con,uF)) {

					
//					if(strUserType.equals(EMPLOYEE)) {
						setEmpFnameLnameEmail(con, uF);
//					}
					return SUCCESS;
				}  else {

					loadValidateEmployee(con); 
//					if(strUserType.equals(EMPLOYEE)) {
						setEmpFnameLnameEmail(con, uF);
//					}
						
						request.setAttribute(PAGE, "/jsp/common/showMessagePage.jsp");
						request.setAttribute("MSG", "<b>Oops!</b><br/><br/>" +
								"We have found this as an invalid request. <br> " +
								"1. If you have received this link on email and are accessing it first time please try again or else contact concern person.<br> " +
								"2. If you have received this link on email and have filled your information then please wait till you receive the next mail from us.");
						
					return "invalidsession";
				}
				
			}
			
		} else if(uF.parseToInt(getStep()) == 11) {
			
			setStep("10");
		}
		
		return SUCCESS;
	} finally {  
		db.closeConnection(con);
	}
	
	}

	//Created By Dattatray 10-06-2022 t0 13-06-2022
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
				
			}
			if(uF.parseToInt(getStrStep())==9) {
				if(strBaseUserType!=null && !strBaseUserType.equalsIgnoreCase(ADMIN) && !strBaseUserType.equalsIgnoreCase(ACCOUNTANT) 
					&& !strBaseUserType.equalsIgnoreCase(HRMANAGER) && !strBaseUserType.equalsIgnoreCase(CEO) && !strBaseUserType.equalsIgnoreCase(RECRUITER)) { // && !strBaseUserType.equalsIgnoreCase(MANAGER) && !strBaseUserType.equalsIgnoreCase(HOD)
		
					builder.append("Alert : "+hmEmpProfile.get(getEmpId()) +" trying to access "+strAction);
				}
			}else if(uF.parseToInt(getStep()) == 8) {
				if(strBaseUserType!=null && !strBaseUserType.equalsIgnoreCase(ADMIN) && !strBaseUserType.equalsIgnoreCase(ACCOUNTANT) 
						&& !strBaseUserType.equalsIgnoreCase(HRMANAGER) && !strBaseUserType.equalsIgnoreCase(CEO) && !strBaseUserType.equalsIgnoreCase(RECRUITER)) { // && !strBaseUserType.equalsIgnoreCase(MANAGER) && !strBaseUserType.equalsIgnoreCase(HOD)
					builder.append("Alert : "+hmEmpProfile.get(getEmpId()) +" trying to access "+strAction);

				}
			}
			CF.pageVisitAuditTrail(con,CF,uF, strSessionEmpId, strAction, strBaseUserType, builder.toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			db.closeConnection(con);
		}
		
	}

	private void sendAttachDocument(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityNames) {

		ResultSet rst = null;
		PreparedStatement pst = null;

		try {
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
			Map<String, String> hmEmpInner = hmEmpInfo.get("" + nEmpId);

			Map<String, String> hmActivity = CF.getActivityName(con);
			if (hmActivity == null)
				hmActivity = new HashMap<String, String>();
			StringBuilder sbEmpSalTable = CF.getEmployeeSalaryDetails(con, CF, uF, "" + nEmpId, request, session);
			if (sbEmpSalTable == null)
				sbEmpSalTable = new StringBuilder();

			String empOrgId = CF.getEmpOrgId(con, uF, "" + nEmpId);

			Map<String, Map<String, String>> hmHeader = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmFooter = new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select * from document_collateral");
			rst = pst.executeQuery();
			// System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				if (rst.getString("_type").equals("H")) {
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("COLLATERAL_ID", rst.getString("collateral_id"));
					hmInner.put("COLLATERAL_PATH", rst.getString("collateral_image"));
					hmInner.put("COLLATERAL_IMG_ALIGN", rst.getString("image_align"));
					hmInner.put("COLLATERAL_TEXT", uF.showData(rst.getString("collateral_text"), ""));

					hmHeader.put(rst.getString("collateral_id"), hmInner);
				} else {
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("COLLATERAL_ID", rst.getString("collateral_id"));
					hmInner.put("COLLATERAL_PATH", rst.getString("collateral_image"));
					hmInner.put("COLLATERAL_IMG_ALIGN", rst.getString("image_align"));
					hmInner.put("COLLATERAL_TEXT", uF.showData(rst.getString("collateral_text"), ""));

					hmFooter.put(rst.getString("collateral_id"), hmInner);
				}
			}
			rst.close();
			pst.close();

			pst = con.prepareStatement("select * from nodes");
			rst = pst.executeQuery();
			Map<String, String> hmMapActivityNode = new HashMap<String, String>();
			while (rst.next()) {
				hmMapActivityNode.put(rst.getString("mapped_activity_id"), rst.getString("node_id"));
			}
			rst.close();
			pst.close();
			int nTriggerNode = uF.parseToInt(hmMapActivityNode.get("" + activityType));

			String strDocumentName = null;
			String strDocumentContent = null;
			String strDocumentHeader = null;
			String strDocumentFooter = null;
			String strHeader = null;
			String strFooter = null;
			String strHeaderImageAlign = "";
			String strHeaderCollateralText = "";
			String strHeaderTextAlign = "";
			String strFooterImageAlign = "";
			String strFooterCollateralText = "";
			String strFooterTextAlign = "";

			if (nTriggerNode > 0) {
				pst = con.prepareStatement("select * from document_comm_details where trigger_nodes like '%," + nTriggerNode
						+ ",%' and status=1 and org_id=? order by document_id desc limit 1");
				pst.setInt(1, uF.parseToInt(empOrgId));
				rst = pst.executeQuery();
				// System.out.println("new Date ===> " + new Date());
				while (rst.next()) {
					strDocumentName = rst.getString("document_name");
					strDocumentContent = rst.getString("document_text");

					if (rst.getString("collateral_header") != null && !rst.getString("collateral_header").equals("")
							&& hmHeader.get(rst.getString("collateral_header")) != null) {
						Map<String, String> hmInner = hmHeader.get(rst.getString("collateral_header"));
						strHeader = uF.showData(hmInner.get("COLLATERAL_PATH"), "");
						strHeaderImageAlign = uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"), "");
						strHeaderCollateralText = uF.showData(hmInner.get("COLLATERAL_TEXT"), "");
						strHeaderTextAlign = uF.showData(hmInner.get("COLLATERAL_TEXT_ALIGN"), "");
					}
					if (rst.getString("collateral_footer") != null && !rst.getString("collateral_footer").equals("")
							&& hmFooter.get(rst.getString("collateral_footer")) != null) {
						Map<String, String> hmInner = hmFooter.get(rst.getString("collateral_footer"));
						strFooter = uF.showData(hmInner.get("COLLATERAL_PATH"), "");
						strFooterImageAlign = uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"), "");
						strFooterCollateralText = uF.showData(hmInner.get("COLLATERAL_TEXT"), "");
						strFooterTextAlign = uF.showData(hmInner.get("COLLATERAL_TEXT_ALIGN"), "");
					}
				}
				rst.close();
				pst.close();
			}
			if (strDocumentName != null) {
				// strDocumentName = strDocumentName.replace(" ", "");
				strDocumentName = strDocumentName != null ? strDocumentName.trim() : "";
			}

			String strDomain = request.getServerName().split("\\.")[0];

			Notifications nF = new Notifications(N_NEW_ACTIVITY, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrEmpId("" + nEmpId);
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			// nF.setStrContextPath(request.getServletPath());
			nF.setStrContextPath(request.getContextPath());

			nF.setStrEmpFname(hmEmpInner.get("FNAME"));
			nF.setStrEmpLname(hmEmpInner.get("LNAME"));
			nF.setStrSalaryStructure(sbEmpSalTable.toString());
			nF.setStrActivityName(hmActivity.get(ACTIVITY_APPOINTMENT_ID));

			nF.setStrEffectiveDate(uF.getDateFormat(hmEmpInner.get("JOINING_DATE"), DBDATE, CF.getStrReportDateFormat()));
			nF.setStrPromotionDate(uF.getDateFormat(hmEmpInner.get("JOINING_DATE"), DBDATE, CF.getStrReportDateFormat()));
			// nF.setStrIncrementPercent(""+uF.parseToDouble(getStrIncrementPercentage()));

			Map<String, String> hmParsedContent = null;

			// Document document = new Document(PageSize.A4);
			Document document = new Document(PageSize.A4, 40, 40, 10, 60);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			StringBuilder sbHeader = new StringBuilder();
			StringBuilder sbFooter = new StringBuilder();
			String strDocName = null;
			String strDocContent = null;
			if (strDocumentContent != null) {

				hmParsedContent = nF.parseContent(strDocumentContent, "", "");
				strDocName = strDocumentName;
				strDocContent = hmParsedContent.get("MAIL_BODY");
				String strDocument = hmParsedContent.get("MAIL_BODY");
				if (strDocument != null) {
//					strDocument = strDocument.replaceAll("<br/>", "");
					
					//Satrt Dattatray Date : 31-07-21  
					if (strDocument.contains("<pre style=\"text-align:justify\">") || strDocument.contains("<pre style=\"text-align:justify;\">") || strDocument.contains("<pre style=\"text-align: justify;\">") || strDocument.contains("<pre style=\"text-align: justify\">")) {
//						System.out.println("if");
						if (strDocument.contains("<pre ")) {
							strDocument = strDocument.replaceAll("<pre ", "<p ");
						}
						 if(strDocument.contains("<pre>") ){
							 strDocument = strDocument.replaceAll("<pre>", "<p>");
						 }
						//
						/*if (strDocument.contains("><span")) {
							strDocument = strDocument.replaceAll("><span ", "><p style=\"text-align: justify\"><span ");
						}
						if (strDocument.contains("</span>")) {
							strDocument = strDocument.replaceAll("</span>", "</span></p>");
						}*/

						if (strDocument.contains("</pre>")) {
							strDocument = strDocument.replaceAll("</pre>", "</p>");
						}
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p>", "<br/><p style=\"text-align: justify\">", true, true, "<p>");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<li>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
//						strDocument = replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>", "<p style=\"text-align: justify\">", true, true, "<p style=\"text-align: justify\">");
//						strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
//						strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</p>", true, true, "<p>");
//						strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
					}else {
						System.out.println("Else");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<li>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<p>", true, true, "<p>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", " </li>	", true, true, "</li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<pre>", true, true, "<pre>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<pre>", "<br/>", true, true, "<pre>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
					}
					//End Dattatray Date : 31-07-21 
				}

				String headerPath = "";
				if (strHeader != null && !strHeader.equals("")) {

					if (CF.getStrDocRetriveLocation() == null) {
						headerPath = DOCUMENT_LOCATION + strHeader;
					} else {
						headerPath = CF.getStrDocSaveLocation() + I_COLLATERAL + "/" + I_IMAGE + "/" + strHeader;
					}
				}

				if (headerPath != null && !headerPath.equals("")) {
					sbHeader.append("<table style=\"width: 100%;\"><tr>");
					sbHeader.append("<td>");
					if (strHeader != null && !strHeader.equals("")) {
						sbHeader.append("<img src=\"" + headerPath + "\">");
					}
					sbHeader.append("</td>");
					sbHeader.append("</tr></table>");
				}

				PdfWriter writer = PdfWriter.getInstance(document, buffer);
				HeaderFooterPageEvent event = new HeaderFooterPageEvent(sbHeader.toString(), strFooterCollateralText);
				writer.setPageEvent(event);

				document.open();

				HTMLWorker hw = new HTMLWorker(document);
				hw.parse(new StringReader(strDocument));
				document.close();

			}

			byte[] bytes = buffer.toByteArray();
			if (strDocumentContent != null) {
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName(strDocumentName + ".pdf");
			}
			String strMailSubject = nF.getStrEmailSubject();
			String strMailBody = nF.getStrNewEmailBody();

			nF.setEmailTemplate(true);
			nF.sendNotifications();

			pst = con.prepareStatement("select max(emp_activity_id) as emp_activity_id  from employee_activity_details");
			rst = pst.executeQuery();
			int nEmpActivityId = 0;
			while (rst.next()) {
				nEmpActivityId = uF.parseToInt(rst.getString("emp_activity_id"));
			}
			rst.close();
			pst.close();

			saveDocumentActivity(con, uF, CF, nEmpId, strDocName, sbHeader.toString(), strDocContent, strFooterCollateralText, strMailSubject, strMailBody,
					nEmpActivityId, hmEmpInner);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
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
	
	private void saveDocumentActivity(Connection con, UtilityFunctions uF,CommonFunctions CF, int nEmpId, String strDocumentName, String strDocumentHeader, String strDocumentContent, String strDocumentFooter, String strMailSubject, String strMailBody,int nEmpActivityId,Map<String, String> hmEmpInner) {
		
		PreparedStatement pst = null;
		
		try {

			pst = con.prepareStatement("insert into document_activities (document_name, document_content, effective_date, entry_date, user_id, emp_id, "
					+ "mail_subject, mail_body, document_header, document_footer,emp_activity_id) values (?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setString(1, strDocumentName);
			pst.setString(2, strDocumentContent);
			// System.out.println("joining date==>"+uF.getDateFormat(hmEmpInner.get("JOINING_DATE"), DBDATE, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(uF.getDateFormat(hmEmpInner.get("JOINING_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(5, uF.parseToInt((String) session.getAttribute(EMPID)));
			pst.setInt(6, nEmpId);
			pst.setString(7, strMailSubject);
			pst.setString(8, strMailBody);
			pst.setString(9, strDocumentHeader);
			pst.setString(10, strDocumentFooter);
			pst.setInt(11, nEmpActivityId);
			pst.execute();
			pst.close();

		} catch (Exception e) {
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
		

	private void getFormValidationFieldWise(UtilityFunctions uF) {
		Map<String, List<String>> hmValidationFields = (Map<String, List<String>> ) request.getAttribute("hmValidationFields");
//		String validReqOpt = "";
//		String validAsterix = "";
		
		
//		************************** Step 1 ***********************************
		List<String> salutationValidList = hmValidationFields.get("EMP_SALUTATION"); 
		String salutationValidReqOpt = "";
		String salutationValidAsterix = "";
		if(salutationValidList != null && uF.parseToBoolean(salutationValidList.get(0))) {
			salutationValidReqOpt = "validateRequired";
			salutationValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("salutationValidReqOpt", salutationValidReqOpt);
		request.setAttribute("salutationValidAsterix", salutationValidAsterix);
		
		List<String> empFNameValidList = hmValidationFields.get("EMP_FIRST_NAME"); 
		String empFNameValidReqOpt = "";
		String empFNameValidAsterix = "";
		if(empFNameValidList != null && uF.parseToBoolean(empFNameValidList.get(0))) {
			empFNameValidReqOpt = "validateRequired";
			empFNameValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empFNameValidReqOpt", empFNameValidReqOpt);
		request.setAttribute("empFNameValidAsterix", empFNameValidAsterix);
		
		List<String> empMNameValidList = hmValidationFields.get("EMP_MIDDLE_NAME"); 
		String empMNameValidReqOpt = "";
		String empMNameValidAsterix = "";
		if(empMNameValidList != null && uF.parseToBoolean(empMNameValidList.get(0))) {
			empMNameValidReqOpt = "validateRequired";
			empMNameValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empMNameValidReqOpt", empMNameValidReqOpt);
		request.setAttribute("empMNameValidAsterix", empMNameValidAsterix);
		
		List<String> empLNameValidList = hmValidationFields.get("EMP_LAST_NAME"); 
		String empLNameValidReqOpt = "";
		String empLNameValidAsterix = "";
		if(empLNameValidList != null && uF.parseToBoolean(empLNameValidList.get(0))) {
			empLNameValidReqOpt = "validateRequired";
			empLNameValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empLNameValidReqOpt", empLNameValidReqOpt);
		request.setAttribute("empLNameValidAsterix", empLNameValidAsterix);
		
		List<String> empPersonalEmailIdValidList = hmValidationFields.get("EMP_PERSONAL_EMAIL_ID"); 
		String empPersonalEmailIdValidReqOpt = "validateEmail";
		String empPersonalEmailIdValidAsterix = "";
		if(empPersonalEmailIdValidList != null && uF.parseToBoolean(empPersonalEmailIdValidList.get(0))) {
			empPersonalEmailIdValidReqOpt = "validateEmailRequired";
			empPersonalEmailIdValidAsterix = "<sup>*</sup>";
		}
		
		request.setAttribute("empPersonalEmailIdValidReqOpt", empPersonalEmailIdValidReqOpt);
		request.setAttribute("empPersonalEmailIdValidAsterix", empPersonalEmailIdValidAsterix);
		
		List<String> empTemporaryAddressValidList = hmValidationFields.get("EMP_TEMPORARY_ADDRESS"); 
		String empTemporaryAddressValidReqOpt = "";
		String empTemporaryAddressValidAsterix = "";
		if(empTemporaryAddressValidList != null && uF.parseToBoolean(empTemporaryAddressValidList.get(0))) {
			empTemporaryAddressValidReqOpt = "validateRequired";
			empTemporaryAddressValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empTemporaryAddressValidReqOpt", empTemporaryAddressValidReqOpt);
		request.setAttribute("empTemporaryAddressValidAsterix", empTemporaryAddressValidAsterix);
		
		List<String> empTemporaryCityValidList = hmValidationFields.get("EMP_TEMPORARY_CITY"); 
		String empTemporaryCityValidReqOpt = "";
		String empTemporaryCityValidAsterix = "";
		if(empTemporaryCityValidList != null && uF.parseToBoolean(empTemporaryCityValidList.get(0))) {
			empTemporaryCityValidReqOpt = "validateRequired";
			empTemporaryCityValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empTemporaryCityValidReqOpt", empTemporaryCityValidReqOpt);
		request.setAttribute("empTemporaryCityValidAsterix", empTemporaryCityValidAsterix);
		
		List<String> empTemporaryCountryValidList = hmValidationFields.get("EMP_TEMPORARY_COUNTRY"); 
		String empTemporaryCountryValidReqOpt = "";
		String empTemporaryCountryValidAsterix = "";
		if(empTemporaryCountryValidList != null && uF.parseToBoolean(empTemporaryCountryValidList.get(0))) {
			empTemporaryCountryValidReqOpt = "validateRequired";
			empTemporaryCountryValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empTemporaryCountryValidReqOpt", empTemporaryCountryValidReqOpt);
		request.setAttribute("empTemporaryCountryValidAsterix", empTemporaryCountryValidAsterix);
		
		List<String> empTemporaryStateValidList = hmValidationFields.get("EMP_TEMPORARY_STATE"); 
		String empTemporaryStateValidReqOpt = "";
		String empTemporaryStateValidAsterix = "";
		if(empTemporaryStateValidList != null && uF.parseToBoolean(empTemporaryStateValidList.get(0))) {
			empTemporaryStateValidReqOpt = "validateRequired";
			empTemporaryStateValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empTemporaryStateValidReqOpt", empTemporaryStateValidReqOpt);
		request.setAttribute("empTemporaryStateValidAsterix", empTemporaryStateValidAsterix);
		
		List<String> empTemporaryPostcodeValidList = hmValidationFields.get("EMP_TEMPORARY_POSTCODE"); 
		String empTemporaryPostcodeValidReqOpt = "";
		String empTemporaryPostcodeValidAsterix = "";
		if(empTemporaryPostcodeValidList != null && uF.parseToBoolean(empTemporaryPostcodeValidList.get(0))) {
			empTemporaryPostcodeValidReqOpt = "validateRequired";
			empTemporaryPostcodeValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empTemporaryPostcodeValidReqOpt", empTemporaryPostcodeValidReqOpt);
		request.setAttribute("empTemporaryPostcodeValidAsterix", empTemporaryPostcodeValidAsterix);
		
		List<String> empPermanentAddressValidList = hmValidationFields.get("EMP_PERMANENT_ADDRESS"); 
		String empPermanentAddressValidReqOpt = "";
		String empPermanentAddressValidAsterix = "";
		if(empPermanentAddressValidList != null && uF.parseToBoolean(empPermanentAddressValidList.get(0))) {
			empPermanentAddressValidReqOpt = "validateRequired";
			empPermanentAddressValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPermanentAddressValidReqOpt", empPermanentAddressValidReqOpt);
		request.setAttribute("empPermanentAddressValidAsterix", empPermanentAddressValidAsterix);
		
		List<String> empPermanentCityValidList = hmValidationFields.get("EMP_PERMANENT_CITY"); 
		String empPermanentCityValidReqOpt = "";
		String empPermanentCityValidAsterix = "";
		if(empPermanentCityValidList != null && uF.parseToBoolean(empPermanentCityValidList.get(0))) {
			empPermanentCityValidReqOpt = "validateRequired";
			empPermanentCityValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPermanentCityValidReqOpt", empPermanentCityValidReqOpt);
		request.setAttribute("empPermanentCityValidAsterix", empPermanentCityValidAsterix);
		
		List<String> empPermanentCountryValidList = hmValidationFields.get("EMP_PERMANENT_COUNTRY"); 
		String empPermanentCountryValidReqOpt = "";
		String empPermanentCountryValidAsterix = "";
		if(empPermanentCountryValidList != null && uF.parseToBoolean(empPermanentCountryValidList.get(0))) {
			empPermanentCountryValidReqOpt = "validateRequired";
			empPermanentCountryValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPermanentCountryValidReqOpt", empPermanentCountryValidReqOpt);
		request.setAttribute("empPermanentCountryValidAsterix", empPermanentCountryValidAsterix);
		
		List<String> empPermanentStateValidList = hmValidationFields.get("EMP_PERMANENT_STATE"); 
		String empPermanentStateValidReqOpt = "";
		String empPermanentStateValidAsterix = "";
		if(empPermanentStateValidList != null && uF.parseToBoolean(empPermanentStateValidList.get(0))) {
			empPermanentStateValidReqOpt = "validateRequired";
			empPermanentStateValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPermanentStateValidReqOpt", empPermanentStateValidReqOpt);
		request.setAttribute("empPermanentStateValidAsterix", empPermanentStateValidAsterix);
		
		List<String> empPermanentPostcodeValidList = hmValidationFields.get("EMP_PERMANENT_POSTCODE"); 
		String empPermanentPostcodeValidReqOpt = "";
		String empPermanentPostcodeValidAsterix = "";
		if(empPermanentPostcodeValidList != null && uF.parseToBoolean(empPermanentPostcodeValidList.get(0))) {
			empPermanentPostcodeValidReqOpt = "validateRequired";
			empPermanentPostcodeValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPermanentPostcodeValidReqOpt", empPermanentPostcodeValidReqOpt);
		request.setAttribute("empPermanentPostcodeValidAsterix", empPermanentPostcodeValidAsterix);
		
		List<String> empLandlineNoValidList = hmValidationFields.get("EMP_LANDLINE_NO"); 
		String empLandlineNoValidReqOpt = "";
		String empLandlineNoValidAsterix = "";
		if(empLandlineNoValidList != null && uF.parseToBoolean(empLandlineNoValidList.get(0))) {
			empLandlineNoValidReqOpt = "validateRequired";
			empLandlineNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empLandlineNoValidReqOpt", empLandlineNoValidReqOpt);
		request.setAttribute("empLandlineNoValidAsterix", empLandlineNoValidAsterix);
		
		List<String> empMobileNoValidList = hmValidationFields.get("EMP_MOBILE_NO"); 
		String empMobileNoValidReqOpt = "";
		String empMobileNoValidAsterix = "";
		if(empMobileNoValidList != null && uF.parseToBoolean(empMobileNoValidList.get(0))) {
			empMobileNoValidReqOpt = "validateRequired";
			empMobileNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empMobileNoValidReqOpt", empMobileNoValidReqOpt);
		request.setAttribute("empMobileNoValidAsterix", empMobileNoValidAsterix);
		
		List<String> empEmergencyContactNameValidList = hmValidationFields.get("EMP_EMERGENCY_CONTACT_NAME"); 
		String empEmergencyContactNameValidReqOpt = "";
		String empEmergencyContactNameValidAsterix = "";
		if(empEmergencyContactNameValidList != null && uF.parseToBoolean(empEmergencyContactNameValidList.get(0))) {
			empEmergencyContactNameValidReqOpt = "validateRequired";
			empEmergencyContactNameValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empEmergencyContactNameValidReqOpt", empEmergencyContactNameValidReqOpt);
		request.setAttribute("empEmergencyContactNameValidAsterix", empEmergencyContactNameValidAsterix);
		
		List<String> empEmergencyContactNoValidList = hmValidationFields.get("EMP_EMERGENCY_CONTACT_NO"); 
		String empEmergencyContactNoValidReqOpt = "";
		String empEmergencyContactNoValidAsterix = "";
		if(empEmergencyContactNoValidList != null && uF.parseToBoolean(empEmergencyContactNoValidList.get(0))) {
			empEmergencyContactNoValidReqOpt = "validateRequired";
			empEmergencyContactNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empEmergencyContactNoValidReqOpt", empEmergencyContactNoValidReqOpt);
		request.setAttribute("empEmergencyContactNoValidAsterix", empEmergencyContactNoValidAsterix);
		
		List<String> empDoctorsNameValidList = hmValidationFields.get("EMP_DOCTORS_NAME"); 
		String empDoctorsNameValidReqOpt = "";
		String empDoctorsNameValidAsterix = "";
		if(empDoctorsNameValidList != null && uF.parseToBoolean(empDoctorsNameValidList.get(0))) {
			empDoctorsNameValidReqOpt = "validateRequired";
			empDoctorsNameValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empDoctorsNameValidReqOpt", empDoctorsNameValidReqOpt);
		request.setAttribute("empDoctorsNameValidAsterix", empDoctorsNameValidAsterix);
		
		List<String> empDoctorsContactNoValidList = hmValidationFields.get("EMP_DOCTORS_CONTACT_NO"); 
		String empDoctorsContactNoValidReqOpt = "";
		String empDoctorsContactNoValidAsterix = "";
		if(empDoctorsContactNoValidList != null && uF.parseToBoolean(empDoctorsContactNoValidList.get(0))) {
			empDoctorsContactNoValidReqOpt = "validateRequired";
			empDoctorsContactNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empDoctorsContactNoValidReqOpt", empDoctorsContactNoValidReqOpt);
		request.setAttribute("empDoctorsContactNoValidAsterix", empDoctorsContactNoValidAsterix);
		
		List<String> empBloodGroupValidList = hmValidationFields.get("EMP_BLOOD_GROUP"); 
		String empBloodGroupValidReqOpt = "";
		String empBloodGroupValidAsterix = "";
		if(empBloodGroupValidList != null && uF.parseToBoolean(empBloodGroupValidList.get(0))) {
			empBloodGroupValidReqOpt = "validateRequired";
			empBloodGroupValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empBloodGroupValidReqOpt", empBloodGroupValidReqOpt);
		request.setAttribute("empBloodGroupValidAsterix", empBloodGroupValidAsterix);
		
		List<String> empDateOfBirthValidList = hmValidationFields.get("EMP_DATE_OF_BIRTH"); 
		String empDateOfBirthValidReqOpt = "";
		String empDateOfBirthValidAsterix = "";
		if(empDateOfBirthValidList != null && uF.parseToBoolean(empDateOfBirthValidList.get(0))) {
			empDateOfBirthValidReqOpt = "validateRequired";
			empDateOfBirthValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empDateOfBirthValidReqOpt", empDateOfBirthValidReqOpt);
		request.setAttribute("empDateOfBirthValidAsterix", empDateOfBirthValidAsterix);
		
		List<String> empMaritalStatusValidList = hmValidationFields.get("EMP_MARITAL_STATUS"); 
		String empMaritalStatusValidReqOpt = "";
		String empMaritalStatusValidAsterix = "";
		if(empMaritalStatusValidList != null && uF.parseToBoolean(empMaritalStatusValidList.get(0))) {
			empMaritalStatusValidReqOpt = "validateRequired";
			empMaritalStatusValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empMaritalStatusValidReqOpt", empMaritalStatusValidReqOpt);
		request.setAttribute("empMaritalStatusValidAsterix", empMaritalStatusValidAsterix);
		
		List<String> empDateOfMarriageValidList = hmValidationFields.get("EMP_DATE_OF_MARRIAGE"); 
		String empDateOfMarriageValidReqOpt = "";
		String empDateOfMarriageValidAsterix = "";
		if(empDateOfMarriageValidList != null && uF.parseToBoolean(empDateOfMarriageValidList.get(0))) {
			empDateOfMarriageValidReqOpt = "validateRequired";
			empDateOfMarriageValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empDateOfMarriageValidReqOpt", empDateOfMarriageValidReqOpt);
		request.setAttribute("empDateOfMarriageValidAsterix", empDateOfMarriageValidAsterix);
		
		List<String> empGenderValidList = hmValidationFields.get("EMP_GENDER"); 
		String empGenderValidReqOpt = "";
		String empGenderValidAsterix = "";
		if(empGenderValidList != null && uF.parseToBoolean(empGenderValidList.get(0))) {
			empGenderValidReqOpt = "validateRequired";
			empGenderValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empGenderValidReqOpt", empGenderValidReqOpt);
		request.setAttribute("empGenderValidAsterix", empGenderValidAsterix);
		
		List<String> empPassportNoValidList = hmValidationFields.get("EMP_PASSPORT_NO"); 
		String empPassportNoValidReqOpt = "";
		String empPassportNoValidAsterix = "";
		if(empPassportNoValidList != null && uF.parseToBoolean(empPassportNoValidList.get(0))) {
			empPassportNoValidReqOpt = "validateRequired";
			empPassportNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPassportNoValidReqOpt", empPassportNoValidReqOpt);
		request.setAttribute("empPassportNoValidAsterix", empPassportNoValidAsterix);
		
		List<String> empPassportExpiryDateValidList = hmValidationFields.get("EMP_PASSPORT_EXPIRY_DATE"); 
		String empPassportExpiryDateValidReqOpt = "";
		String empPassportExpiryDateValidAsterix = "";
		if(empPassportExpiryDateValidList != null && uF.parseToBoolean(empPassportExpiryDateValidList.get(0))) {
			empPassportExpiryDateValidReqOpt = "validateRequired";
			empPassportExpiryDateValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPassportExpiryDateValidReqOpt", empPassportExpiryDateValidReqOpt);
		request.setAttribute("empPassportExpiryDateValidAsterix", empPassportExpiryDateValidAsterix);
		
		List<String> empPanNoValidList = hmValidationFields.get("EMP_PAN_NO"); 
		String empPanNoValidReqOpt = "";
		String empPanNoValidAsterix = "";
		if(empPanNoValidList != null && uF.parseToBoolean(empPanNoValidList.get(0))) {
			empPanNoValidReqOpt = "validateRequired";
			empPanNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPanNoValidReqOpt", empPanNoValidReqOpt);
		request.setAttribute("empPanNoValidAsterix", empPanNoValidAsterix);
		
		List<String> empMRDNoValidList = hmValidationFields.get("EMP_MRD_NO"); 
		String empMRDNoValidReqOpt = "";
		String empMRDNoValidAsterix = "";
		if(empMRDNoValidList != null && uF.parseToBoolean(empMRDNoValidList.get(0))) {
			empMRDNoValidReqOpt = "validateRequired";
			empMRDNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empMRDNoValidReqOpt", empMRDNoValidReqOpt);
		request.setAttribute("empMRDNoValidAsterix", empMRDNoValidAsterix);
				
		List<String> empProvidentFundNoValidList = hmValidationFields.get("EMP_PROVIDENT_FUND_NO"); 
		String empProvidentFundNoValidReqOpt = "";
		String empProvidentFundNoValidAsterix = "";
		if(empProvidentFundNoValidList != null && uF.parseToBoolean(empProvidentFundNoValidList.get(0))) {
			empProvidentFundNoValidReqOpt = "validateRequired";
			empProvidentFundNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empProvidentFundNoValidReqOpt", empProvidentFundNoValidReqOpt);
		request.setAttribute("empProvidentFundNoValidAsterix", empProvidentFundNoValidAsterix);
		
		List<String> empProvidentFundStartDateValidList = hmValidationFields.get("EMP_PROVIDENT_FUND_START_DATE"); 
		String empProvidentFundStartDateValidReqOpt = "";
		String empProvidentFundStartDateValidAsterix = "";
		if(empProvidentFundStartDateValidList != null && uF.parseToBoolean(empProvidentFundStartDateValidList.get(0))) {
			empProvidentFundStartDateValidReqOpt = "validateRequired";
			empProvidentFundStartDateValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empProvidentFundStartDateValidReqOpt", empProvidentFundStartDateValidReqOpt);
		request.setAttribute("empProvidentFundStartDateValidAsterix", empProvidentFundStartDateValidAsterix);
		
		
		List<String> empGPFAccountNoValidList = hmValidationFields.get("EMP_GPF_ACC_NO"); 
		String empGPFAccountNoValidReqOpt = "";
		String empGPFAccountNoValidAsterix = "";
		if(empGPFAccountNoValidList != null && uF.parseToBoolean(empGPFAccountNoValidList.get(0))) {
			empGPFAccountNoValidReqOpt = "validateRequired";
			empGPFAccountNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empGPFAccountNoValidReqOpt", empGPFAccountNoValidReqOpt);
		request.setAttribute("empGPFAccountNoValidAsterix", empGPFAccountNoValidAsterix);
		
		List<String> empESICNoValidList = hmValidationFields.get("EMP_ESIC_NO"); 
		String empESICNoValidReqOpt = "";
		String empESICNoValidAsterix = "";
		if(empESICNoValidList != null && uF.parseToBoolean(empESICNoValidList.get(0))) {
			empESICNoValidReqOpt = "validateRequired";
			empESICNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empESICNoValidReqOpt", empESICNoValidReqOpt);
		request.setAttribute("empESICNoValidAsterix", empESICNoValidAsterix);
		
		List<String> empUANNoValidList = hmValidationFields.get("EMP_UAN_NO"); 
		String empUANNoValidReqOpt = "";
		String empUANNoValidAsterix = "";
		if(empUANNoValidList != null && uF.parseToBoolean(empUANNoValidList.get(0))) {
			empUANNoValidReqOpt = "validateRequired";
			empUANNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empUANNoValidReqOpt", empUANNoValidReqOpt);
		request.setAttribute("empUANNoValidAsterix", empUANNoValidAsterix);
		
		List<String> empUIDNoValidList = hmValidationFields.get("EMP_UID_NO"); 
		String empUIDNoValidReqOpt = "";
		String empUIDNoValidAsterix = "";
		if(empUIDNoValidList != null && uF.parseToBoolean(empUIDNoValidList.get(0))) {
			empUIDNoValidReqOpt = "validateRequired";
			empUIDNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empUIDNoValidReqOpt", empUIDNoValidReqOpt);
		request.setAttribute("empUIDNoValidAsterix", empUIDNoValidAsterix);
		
		List<String> empProfilePhotoValidList = hmValidationFields.get("EMP_PROFILE_PHOTO"); 
		String empProfilePhotoValidReqOpt = "";
		String empProfilePhotoValidAsterix = "";
		if(empProfilePhotoValidList != null && uF.parseToBoolean(empProfilePhotoValidList.get(0))) {
			empProfilePhotoValidReqOpt = "validateRequired";
			empProfilePhotoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empProfilePhotoValidReqOpt", empProfilePhotoValidReqOpt);
		request.setAttribute("empProfilePhotoValidAsterix", empProfilePhotoValidAsterix);
		
	//===start parvez date: 30-07-2022===	
		List<String> empEmergencyRelationValidList = hmValidationFields.get("EMP_EMERGENCY_CONTACT_RELATION"); 
		String empEmergencyContactRelationValidReqOpt = "";
		String empEmergencyContactRelationValidAsterix = "";
		if(empEmergencyRelationValidList != null && uF.parseToBoolean(empEmergencyRelationValidList.get(0))) {
			empEmergencyContactRelationValidReqOpt = "validateRequired";
			empEmergencyContactRelationValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empEmergencyContactRelationValidReqOpt", empEmergencyContactRelationValidReqOpt);
		request.setAttribute("empEmergencyContactRelationValidAsterix", empEmergencyContactRelationValidAsterix);
	//===end parvez date: 30-07-2022===	
		
		
//		************************** Step 2 ***********************************
		List<String> empSkillNameValidList = hmValidationFields.get("EMP_SKILL_NAME"); 
		String empSkillNameValidReqOpt = "";
		String empSkillNameValidAsterix = "";
		if(empSkillNameValidList != null && uF.parseToBoolean(empSkillNameValidList.get(0))) {
			empSkillNameValidReqOpt = "validateRequired";
			empSkillNameValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empSkillNameValidReqOpt", empSkillNameValidReqOpt);
		request.setAttribute("empSkillNameValidAsterix", empSkillNameValidAsterix);
		
		List<String> empSkillRatingValidList = hmValidationFields.get("EMP_SKILL_RATING"); 
		String empSkillRatingValidReqOpt = "";
		String empSkillRatingValidAsterix = "";
		if(empSkillRatingValidList != null && uF.parseToBoolean(empSkillRatingValidList.get(0))) {
			empSkillRatingValidReqOpt = "validateRequired";
			empSkillRatingValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empSkillRatingValidReqOpt", empSkillRatingValidReqOpt);
		request.setAttribute("empSkillRatingValidAsterix", empSkillRatingValidAsterix);
		
		List<String> empDegreeNameValidList = hmValidationFields.get("EMP_DEGREE_NAME"); 
		String empDegreeNameValidReqOpt = "";
		String empDegreeNameValidAsterix = "";
		if(empDegreeNameValidList != null && uF.parseToBoolean(empDegreeNameValidList.get(0))) {
			empDegreeNameValidReqOpt = "validateRequired";
			empDegreeNameValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empDegreeNameValidReqOpt", empDegreeNameValidReqOpt);
		request.setAttribute("empDegreeNameValidAsterix", empDegreeNameValidAsterix);
		
		List<String> empDegreeDurationValidList = hmValidationFields.get("EMP_DEGREE_DURATION"); 
		String empDegreeDurationValidReqOpt = "";
		String empDegreeDurationValidAsterix = "";
		if(empDegreeDurationValidList != null && uF.parseToBoolean(empDegreeDurationValidList.get(0))) {
			empDegreeDurationValidReqOpt = "validateRequired";
			empDegreeDurationValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empDegreeDurationValidReqOpt", empDegreeDurationValidReqOpt);
		request.setAttribute("empDegreeDurationValidAsterix", empDegreeDurationValidAsterix);
		
		List<String> empDegreeCompletionYearValidList = hmValidationFields.get("EMP_DEGREE_COMPLETION_YEAR"); 
		String empDegreeCompletionYearValidReqOpt = "";
		String empDegreeCompletionYearValidAsterix = "";
		if(empDegreeCompletionYearValidList != null && uF.parseToBoolean(empDegreeCompletionYearValidList.get(0))) {
			empDegreeCompletionYearValidReqOpt = "validateRequired";
			empDegreeCompletionYearValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empDegreeCompletionYearValidReqOpt", empDegreeCompletionYearValidReqOpt);
		request.setAttribute("empDegreeCompletionYearValidAsterix", empDegreeCompletionYearValidAsterix);
		
		List<String> empDegreeGradeValidList = hmValidationFields.get("EMP_DEGREE_GRADE"); 
		String empDegreeGradeValidReqOpt = "";
		String empDegreeGradeValidAsterix = "";
		if(empDegreeGradeValidList != null && uF.parseToBoolean(empDegreeGradeValidList.get(0))) {
			empDegreeGradeValidReqOpt = "validateRequired";
			empDegreeGradeValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empDegreeGradeValidReqOpt", empDegreeGradeValidReqOpt);
		request.setAttribute("empDegreeGradeValidAsterix", empDegreeGradeValidAsterix);
		
		List<String> empDegreeInstituteNameValidList = hmValidationFields.get("EMP_DEGREE_INSTITUTE_NAME"); 
		String empDegreeInstituteNameValidReqOpt = "";
		String empDegreeInstituteNameValidAsterix = "";
		if(empDegreeInstituteNameValidList != null && uF.parseToBoolean(empDegreeInstituteNameValidList.get(0))) {
			empDegreeInstituteNameValidReqOpt = "validateRequired";
			empDegreeInstituteNameValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empDegreeInstituteNameValidReqOpt", empDegreeInstituteNameValidReqOpt);
		request.setAttribute("empDegreeInstituteNameValidAsterix", empDegreeInstituteNameValidAsterix);
		
		List<String> empDegreeCertificateValidList = hmValidationFields.get("EMP_EDUCATION_CERTIFICATE"); 
		String empDegreeCertificateValidReqOpt = "";
		String empDegreeCertificateValidAsterix = "";
		if(empDegreeCertificateValidList != null && uF.parseToBoolean(empDegreeCertificateValidList.get(0))) {
			empDegreeCertificateValidReqOpt = "validateRequired";
			empDegreeCertificateValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empDegreeCertificateValidReqOpt", empDegreeCertificateValidReqOpt);
		request.setAttribute("empDegreeCertificateValidAsterix", empDegreeCertificateValidAsterix);
		
		List<String> empLanguageNameValidList = hmValidationFields.get("EMP_LANGUAGE_NAME"); 
		String empLanguageNameValidReqOpt = "";
		String empLanguageNameValidAsterix = "";
		if(uF.parseToBoolean(empLanguageNameValidList.get(0))) {
			empLanguageNameValidReqOpt = "validateRequired";
			empLanguageNameValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empLanguageNameValidReqOpt", empLanguageNameValidReqOpt);
		request.setAttribute("empLanguageNameValidAsterix", empLanguageNameValidAsterix);
		
		List<String> empHobbyNameValidList = hmValidationFields.get("EMP_HOBBY_NAME"); 
		String empHobbyNameValidReqOpt = "";
		String empHobbyNameValidAsterix = "";
		if(uF.parseToBoolean(empHobbyNameValidList.get(0))) {
			empHobbyNameValidReqOpt = "validateRequired";
			empHobbyNameValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empHobbyNameValidReqOpt", empHobbyNameValidReqOpt);
		request.setAttribute("empHobbyNameValidAsterix", empHobbyNameValidAsterix);
		
		
		
		
		
//		************************** Step 3 ***********************************
		List<String> empPrevCompNameValidList = hmValidationFields.get("EMP_PREV_COMPANY_NAME"); 
		String empPrevCompNameValidReqOpt = "";
		String empPrevCompNameValidAsterix = "";
		if(uF.parseToBoolean(empPrevCompNameValidList.get(0))) {
			empPrevCompNameValidReqOpt = "validateRequired";
			empPrevCompNameValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompNameValidReqOpt", empPrevCompNameValidReqOpt);
		request.setAttribute("empPrevCompNameValidAsterix", empPrevCompNameValidAsterix);
		
		List<String> empPrevCompLocationValidList = hmValidationFields.get("EMP_PREV_COMPANY_LOCATION"); 
		String empPrevCompLocationValidReqOpt = "";
		String empPrevCompLocationValidAsterix = "";
		if(uF.parseToBoolean(empPrevCompLocationValidList.get(0))) {
			empPrevCompLocationValidReqOpt = "validateRequired";
			empPrevCompLocationValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompLocationValidReqOpt", empPrevCompLocationValidReqOpt);
		request.setAttribute("empPrevCompLocationValidAsterix", empPrevCompLocationValidAsterix);
		
		List<String> empPrevCompCityValidList = hmValidationFields.get("EMP_PREV_COMPANY_CITY"); 
		String empPrevCompCityValidReqOpt = "";
		String empPrevCompCityValidAsterix = "";
		if(uF.parseToBoolean(empPrevCompCityValidList.get(0))) {
			empPrevCompCityValidReqOpt = "validateRequired";
			empPrevCompCityValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompCityValidReqOpt", empPrevCompCityValidReqOpt);
		request.setAttribute("empPrevCompCityValidAsterix", empPrevCompCityValidAsterix);
		
		List<String> empPrevCompCountryValidList = hmValidationFields.get("EMP_PREV_COMPANY_COUNRTY"); 
		String empPrevCompCountryValidReqOpt = "";
		String empPrevCompCountryValidAsterix = "";
		if(uF.parseToBoolean(empPrevCompCountryValidList.get(0))) {
			empPrevCompCountryValidReqOpt = "validateRequired";
			empPrevCompCountryValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompCountryValidReqOpt", empPrevCompCountryValidReqOpt);
		request.setAttribute("empPrevCompCountryValidAsterix", empPrevCompCountryValidAsterix);
		
		List<String> empPrevCompStateValidList = hmValidationFields.get("EMP_PREV_COMPANY_STATE"); 
		String empPrevCompStateValidReqOpt = "";
		String empPrevCompStateValidAsterix = "";
		if(uF.parseToBoolean(empPrevCompStateValidList.get(0))) {
			empPrevCompStateValidReqOpt = "validateRequired";
			empPrevCompStateValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompStateValidReqOpt", empPrevCompStateValidReqOpt);
		request.setAttribute("empPrevCompStateValidAsterix", empPrevCompStateValidAsterix);
		
		List<String> empPrevCompContactNoValidList = hmValidationFields.get("EMP_PREV_COMPANY_CONTACT_NO"); 
		String empPrevCompContactNoValidReqOpt = "";
		String empPrevCompContactNoValidAsterix = "";
		if(uF.parseToBoolean(empPrevCompContactNoValidList.get(0))) {
			empPrevCompContactNoValidReqOpt = "validateRequired";
			empPrevCompContactNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompContactNoValidReqOpt", empPrevCompContactNoValidReqOpt);
		request.setAttribute("empPrevCompContactNoValidAsterix", empPrevCompContactNoValidAsterix);
		
		List<String> empPrevCompReportingToValidList = hmValidationFields.get("EMP_PREV_COMPANY_REPORTING_TO"); 
		String empPrevCompReportingToValidReqOpt = "";
		String empPrevCompReportingToValidAsterix = "";
		if(uF.parseToBoolean(empPrevCompReportingToValidList.get(0))) {
			empPrevCompReportingToValidReqOpt = "validateRequired";
			empPrevCompReportingToValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompReportingToValidReqOpt", empPrevCompReportingToValidReqOpt);
		request.setAttribute("empPrevCompReportingToValidAsterix", empPrevCompReportingToValidAsterix);
		
		List<String> empPrevCompReportingToPhNoValidList = hmValidationFields.get("EMP_PREV_COMPANY_REPORTING_TO_PHONE_NO"); 
		String empPrevCompReportingToPhNoValidReqOpt = "";
		String empPrevCompReportingToPhNoValidAsterix = "";
		if(uF.parseToBoolean(empPrevCompReportingToPhNoValidList.get(0))) {
			empPrevCompReportingToPhNoValidReqOpt = "validateRequired";
			empPrevCompReportingToPhNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompReportingToPhNoValidReqOpt", empPrevCompReportingToPhNoValidReqOpt);
		request.setAttribute("empPrevCompReportingToPhNoValidAsterix", empPrevCompReportingToPhNoValidAsterix);
		
		List<String> empPrevCompHRManagerValidList = hmValidationFields.get("EMP_PREV_COMPANY_HR_MANAGER"); 
		String empPrevCompHRManagerValidReqOpt = "";
		String empPrevCompHRManagerValidAsterix = "";
		if(uF.parseToBoolean(empPrevCompHRManagerValidList.get(0))) {
			empPrevCompHRManagerValidReqOpt = "validateRequired";
			empPrevCompHRManagerValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompHRManagerValidReqOpt", empPrevCompHRManagerValidReqOpt);
		request.setAttribute("empPrevCompHRManagerValidAsterix", empPrevCompHRManagerValidAsterix);
		
		List<String> empPrevCompHRManagerPhNoValidList = hmValidationFields.get("EMP_PREV_COMPANY_HR_MANAGER_PHONE_NO"); 
		String empPrevCompHRManagerPhNoValidReqOpt = "";
		String empPrevCompHRManagerPhNoValidAsterix = "";
		if(uF.parseToBoolean(empPrevCompHRManagerPhNoValidList.get(0))) {
			empPrevCompHRManagerPhNoValidReqOpt = "validateRequired";
			empPrevCompHRManagerPhNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompHRManagerPhNoValidReqOpt", empPrevCompHRManagerPhNoValidReqOpt);
		request.setAttribute("empPrevCompHRManagerPhNoValidAsterix", empPrevCompHRManagerPhNoValidAsterix);
		
		List<String> empPrevCompFromDateValidList = hmValidationFields.get("EMP_PREV_COMPANY_FROM_DATE"); 
		String empPrevCompFromDateValidReqOpt = "";
		String empPrevCompFromDateValidAsterix = "";
		if(uF.parseToBoolean(empPrevCompFromDateValidList.get(0))) {
			empPrevCompFromDateValidReqOpt = "validateRequired";
			empPrevCompFromDateValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompFromDateValidReqOpt", empPrevCompFromDateValidReqOpt);
		request.setAttribute("empPrevCompFromDateValidAsterix", empPrevCompFromDateValidAsterix);
		
		List<String> empPrevCompToDateValidList = hmValidationFields.get("EMP_PREV_COMPANY_TO_DATE"); 
		String empPrevCompToDateValidReqOpt = "";
		String empPrevCompToDateValidAsterix = "";
		if(uF.parseToBoolean(empPrevCompToDateValidList.get(0))) {
			empPrevCompToDateValidReqOpt = "validateRequired";
			empPrevCompToDateValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompToDateValidReqOpt", empPrevCompToDateValidReqOpt);
		request.setAttribute("empPrevCompToDateValidAsterix", empPrevCompToDateValidAsterix);
		
		List<String> empPrevCompDesignationValidList = hmValidationFields.get("EMP_PREV_COMPANY_DESIGNATION"); 
		String empPrevCompDesignationValidReqOpt = "";
		String empPrevCompDesignationValidAsterix = "";
		if(uF.parseToBoolean(empPrevCompDesignationValidList.get(0))) {
			empPrevCompDesignationValidReqOpt = "validateRequired";
			empPrevCompDesignationValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompDesignationValidReqOpt", empPrevCompDesignationValidReqOpt);
		request.setAttribute("empPrevCompDesignationValidAsterix", empPrevCompDesignationValidAsterix);
		
		List<String> empPrevCompResponsibilityValidList = hmValidationFields.get("EMP_PREV_COMPANY_RESPONSIBILITY"); 
		String empPrevCompResponsibilityValidReqOpt = "";
		String empPrevCompResponsibilityValidAsterix = "";
		if(uF.parseToBoolean(empPrevCompResponsibilityValidList.get(0))) {
			empPrevCompResponsibilityValidReqOpt = "validateRequired";
			empPrevCompResponsibilityValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompResponsibilityValidReqOpt", empPrevCompResponsibilityValidReqOpt);
		request.setAttribute("empPrevCompResponsibilityValidAsterix", empPrevCompResponsibilityValidAsterix);
		
		List<String> empPrevCompSkillsValidList = hmValidationFields.get("EMP_PREV_COMPANY_SKILLS"); 
		String empPrevCompSkillsValidReqOpt = "";
		String empPrevCompSkillsValidAsterix = "";
		if(uF.parseToBoolean(empPrevCompSkillsValidList.get(0))) {
			empPrevCompSkillsValidReqOpt = "validateRequired";
			empPrevCompSkillsValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompSkillsValidReqOpt", empPrevCompSkillsValidReqOpt);
		request.setAttribute("empPrevCompSkillsValidAsterix", empPrevCompSkillsValidAsterix);
		
	//===start parvez date: 08-08-2022===
		List<String> empPrevCompUANNoValidList = hmValidationFields.get("EMP_PREV_COMPANY_UAN_NO"); 
		String empPrevCompUANNoValidReqOpt = "";
		String empPrevCompUANNoValidAsterix = "";
		if(empPrevCompUANNoValidList != null && uF.parseToBoolean(empPrevCompUANNoValidList.get(0))) {
			empPrevCompUANNoValidReqOpt = "validateRequired";
			empPrevCompUANNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompUANNoValidReqOpt", empPrevCompUANNoValidReqOpt);
		request.setAttribute("empPrevCompUANNoValidAsterix", empPrevCompUANNoValidAsterix);
		
		List<String> empPrevCompESICNoValidList = hmValidationFields.get("EMP_PREV_COMPANY_ESIC_NO"); 
		String empPrevCompESICNoValidReqOpt = "";
		String empPrevCompESICNoValidAsterix = "";
		if(empPrevCompESICNoValidList != null && uF.parseToBoolean(empPrevCompESICNoValidList.get(0))) {
			empPrevCompESICNoValidReqOpt = "validateRequired";
			empPrevCompESICNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompESICNoValidReqOpt", empPrevCompESICNoValidReqOpt);
		request.setAttribute("empPrevCompESICNoValidAsterix", empPrevCompESICNoValidAsterix);
	//===end parvez date: 08-08-2022===	

		List<String> empPrevCompTdsFinancialYearValidList = hmValidationFields.get("EMP_PREV_COMPANY_TDS_FINANCIAL_YEAR"); 
		String empPrevCompTdsFinancialYearValidReqOpt = "";
		String empPrevCompTdsFinancialYearValidAsterix = "";
		if(uF.parseToBoolean(empPrevCompTdsFinancialYearValidList.get(0))) {
			empPrevCompTdsFinancialYearValidReqOpt = "validateRequired";
			empPrevCompTdsFinancialYearValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompTdsFinancialYearValidReqOpt", empPrevCompTdsFinancialYearValidReqOpt);
		request.setAttribute("empPrevCompTdsFinancialYearValidAsterix", empPrevCompTdsFinancialYearValidAsterix);
		
		List<String> empPrevCompGrossAmtValidList = hmValidationFields.get("EMP_PREV_COMPANY_GROSS_AMOUNT"); 
		String empPrevCompGrossAmtValidReqOpt = "";
		String empPrevCompGrossAmtValidAsterix = "";
		if(uF.parseToBoolean(empPrevCompGrossAmtValidList.get(0))) {
			empPrevCompGrossAmtValidReqOpt = "validateRequired";
			empPrevCompGrossAmtValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompGrossAmtValidReqOpt", empPrevCompGrossAmtValidReqOpt);
		request.setAttribute("empPrevCompGrossAmtValidAsterix", empPrevCompGrossAmtValidAsterix);
		
		List<String> empPrevCompTdsAmountValidList = hmValidationFields.get("EMP_PREV_COMPANY_TDS_AMOUNT"); 
		String empPrevCompTdsAmountValidReqOpt = "";
		String empPrevCompTdsAmountValidAsterix = "";
		if(uF.parseToBoolean(empPrevCompTdsAmountValidList.get(0))) {
			empPrevCompTdsAmountValidReqOpt = "validateRequired";
			empPrevCompTdsAmountValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompTdsAmountValidReqOpt", empPrevCompTdsAmountValidReqOpt);
		request.setAttribute("empPrevCompTdsAmountValidAsterix", empPrevCompTdsAmountValidAsterix);
		
		List<String> empPrevCompTdsForm16ValidList = hmValidationFields.get("EMP_PREV_COMPANY_TDS_FORM_16"); 
		String empPrevCompTdsForm16ValidReqOpt = "";
		String empPrevCompTdsForm16ValidAsterix = "";
		if(uF.parseToBoolean(empPrevCompTdsForm16ValidList.get(0))) {
			empPrevCompTdsForm16ValidReqOpt = "validateRequired";
			empPrevCompTdsForm16ValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompTdsForm16ValidReqOpt", empPrevCompTdsForm16ValidReqOpt);
		request.setAttribute("empPrevCompTdsForm16ValidAsterix", empPrevCompTdsForm16ValidAsterix);
		
//===start parvez date: 31-03-2022===
		List<String> empPrevCompPanNumberValidList = hmValidationFields.get("EMP_PREV_COMPANY_PAN_NUMBER"); 
		String empPrevCompPanNumberValidReqOpt = "";
		String empPrevCompPanNumberValidAsterix = "";
		if(empPrevCompPanNumberValidList!=null && uF.parseToBoolean(empPrevCompPanNumberValidList.get(0))) {
			empPrevCompPanNumberValidReqOpt = "validateRequired";
			empPrevCompPanNumberValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompPanNumberValidReqOpt", empPrevCompPanNumberValidReqOpt);
		request.setAttribute("empPrevCompPanNumberValidAsterix", empPrevCompPanNumberValidAsterix);
		
		List<String> empPrevCompTanNumberValidList = hmValidationFields.get("EMP_PREV_COMPANY_TAN_NUMBER"); 
		String empPrevCompTanNumberValidReqOpt = "";
		String empPrevCompTanNumberValidAsterix = "";
		if(empPrevCompTanNumberValidList!=null && uF.parseToBoolean(empPrevCompTanNumberValidList.get(0))) {
			empPrevCompTanNumberValidReqOpt = "validateRequired";
			empPrevCompTanNumberValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empPrevCompTanNumberValidReqOpt", empPrevCompTanNumberValidReqOpt);
		request.setAttribute("empPrevCompTanNumberValidAsterix", empPrevCompTanNumberValidAsterix);
//===end parvez date: 31-03-2022===		
		
		
		
//		************************** Step 4 ***********************************
		List<String> empRefNameValidList = hmValidationFields.get("EMP_REFERENCES_NAME"); 
		String empRefNameValidReqOpt = "";
		String empRefNameValidAsterix = "";
		if(uF.parseToBoolean(empRefNameValidList.get(0))) {
			empRefNameValidReqOpt = "validateRequired";
			empRefNameValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empRefNameValidReqOpt", empRefNameValidReqOpt);
		request.setAttribute("empRefNameValidAsterix", empRefNameValidAsterix);
		
		List<String> empRefCompanyValidList = hmValidationFields.get("EMP_REFERENCES_COMPANY"); 
		String empRefCompanyValidReqOpt = "";
		String empRefCompanyValidAsterix = "";
		if(uF.parseToBoolean(empRefCompanyValidList.get(0))) {
			empRefCompanyValidReqOpt = "validateRequired";
			empRefCompanyValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empRefCompanyValidReqOpt", empRefCompanyValidReqOpt);
		request.setAttribute("empRefCompanyValidAsterix", empRefCompanyValidAsterix);
		
		List<String> empRefDesigValidList = hmValidationFields.get("EMP_REFERENCES_DESIGNATION"); 
		String empRefDesigValidReqOpt = "";
		String empRefDesigValidAsterix = "";
		if(uF.parseToBoolean(empRefDesigValidList.get(0))) {
			empRefDesigValidReqOpt = "validateRequired";
			empRefDesigValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empRefDesigValidReqOpt", empRefDesigValidReqOpt);
		request.setAttribute("empRefDesigValidAsterix", empRefDesigValidAsterix);
		
		List<String> empRefContactNoValidList = hmValidationFields.get("EMP_REFERENCES_CONTACT_NO"); 
		String empRefContactNoValidReqOpt = "";
		String empRefContactNoValidAsterix = "";
		if(uF.parseToBoolean(empRefContactNoValidList.get(0))) {
			empRefContactNoValidReqOpt = "validateRequired";
			empRefContactNoValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empRefContactNoValidReqOpt", empRefContactNoValidReqOpt);
		request.setAttribute("empRefContactNoValidAsterix", empRefContactNoValidAsterix);
		
		List<String> empRefEmailIdValidList = hmValidationFields.get("EMP_REFERENCES_EMAIL_ID"); 
		String empRefEmailIdValidReqOpt = "validateEmail";
		String empRefEmailIdValidAsterix = "";
		if(uF.parseToBoolean(empRefEmailIdValidList.get(0))) {
			empRefEmailIdValidReqOpt = "validateEmailRequired";
			empRefEmailIdValidAsterix = "<sup>*</sup>";
		}
		request.setAttribute("empRefEmailIdValidReqOpt", empRefEmailIdValidReqOpt);
		request.setAttribute("empRefEmailIdValidAsterix", empRefEmailIdValidAsterix);
		
		
		
//		************************** Step 5 ***********************************	
			List<String> empFatherNameValidList = hmValidationFields.get("EMP_FATHER_NAME"); 
			String empFatherNameValidReqOpt = "";
			String empFatherNameValidAsterix = "";
			if(uF.parseToBoolean(empFatherNameValidList.get(0))) {
				empFatherNameValidReqOpt = "validateRequired";
				empFatherNameValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empFatherNameValidReqOpt", empFatherNameValidReqOpt);
			request.setAttribute("empFatherNameValidAsterix", empFatherNameValidAsterix);
			
			List<String> empFatherDateOfBirthValidList = hmValidationFields.get("EMP_FATHER_DATE_OF_BIRTH"); 
			String empFatherDateOfBirthValidReqOpt = "";
			String empFatherDateOfBirthValidAsterix = "";
			if(uF.parseToBoolean(empFatherDateOfBirthValidList.get(0))) {
				empFatherDateOfBirthValidReqOpt = "validateRequired";
				empFatherDateOfBirthValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empFatherDateOfBirthValidReqOpt", empFatherDateOfBirthValidReqOpt);
			request.setAttribute("empFatherDateOfBirthValidAsterix", empFatherDateOfBirthValidAsterix);
			
			List<String> empFatherEductionValidList = hmValidationFields.get("EMP_FATHER_EDUCATION"); 
			String empFatherEductionValidReqOpt = "";
			String empFatherEductionValidAsterix = "";
			if(uF.parseToBoolean(empFatherEductionValidList.get(0))) {
				empFatherEductionValidReqOpt = "validateRequired";
				empFatherEductionValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empFatherEductionValidReqOpt", empFatherEductionValidReqOpt);
			request.setAttribute("empFatherEductionValidAsterix", empFatherEductionValidAsterix);
			
			List<String> empFatherOccupationValidList = hmValidationFields.get("EMP_FATHER_OCCUPATION"); 
			String empFatherOccupationValidReqOpt = "";
			String empFatherOccupationValidAsterix = "";
			if(uF.parseToBoolean(empFatherOccupationValidList.get(0))) {
				empFatherOccupationValidReqOpt = "validateRequired";
				empFatherOccupationValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empFatherOccupationValidReqOpt", empFatherOccupationValidReqOpt);
			request.setAttribute("empFatherOccupationValidAsterix", empFatherOccupationValidAsterix);
			
			List<String> empFatherContactNoValidList = hmValidationFields.get("EMP_FATHER_CONTACT_NO"); 
			String empFatherContactNoValidReqOpt = "";
			String empFatherContactNoValidAsterix = "";
			if(uF.parseToBoolean(empFatherContactNoValidList.get(0))) {
				empFatherContactNoValidReqOpt = "validateRequired";
				empFatherContactNoValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empFatherContactNoValidReqOpt", empFatherContactNoValidReqOpt);
			request.setAttribute("empFatherContactNoValidAsterix", empFatherContactNoValidAsterix);
			
			List<String> empFatherEmailIdValidList = hmValidationFields.get("EMP_FATHER_EMAIL_ID"); 
			String empFatherEmailIdValidReqOpt = "validateEmail";
			String empFatherEmailIdValidAsterix = "";
			if(uF.parseToBoolean(empFatherEmailIdValidList.get(0))) {
				empFatherEmailIdValidReqOpt = "validateEmailRequired";
				empFatherEmailIdValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empFatherEmailIdValidReqOpt", empFatherEmailIdValidReqOpt);
			request.setAttribute("empFatherEmailIdValidAsterix", empFatherEmailIdValidAsterix);
			
			List<String> empMotherNameValidList = hmValidationFields.get("EMP_MOTHER_NAME"); 
			String empMotherNameValidReqOpt = "";
			String empMotherNameValidAsterix = "";
			if(uF.parseToBoolean(empMotherNameValidList.get(0))) {
				empMotherNameValidReqOpt = "validateRequired";
				empMotherNameValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empMotherNameValidReqOpt", empMotherNameValidReqOpt);
			request.setAttribute("empMotherNameValidAsterix", empMotherNameValidAsterix);
			
			List<String> empMotherDateOfBirthValidList = hmValidationFields.get("EMP_MOTHER_DATE_OF_BIRTH"); 
			String empMotherDateOfBirthValidReqOpt = "";
			String empMotherDateOfBirthValidAsterix = "";
			if(uF.parseToBoolean(empMotherDateOfBirthValidList.get(0))) {
				empMotherDateOfBirthValidReqOpt = "validateRequired";
				empMotherDateOfBirthValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empMotherDateOfBirthValidReqOpt", empMotherDateOfBirthValidReqOpt);
			request.setAttribute("empMotherDateOfBirthValidAsterix", empMotherDateOfBirthValidAsterix);
			
			List<String> empMotherEducationValidList = hmValidationFields.get("EMP_MOTHER_EDUCATION"); 
			String empMotherEducationValidReqOpt = "";
			String empMotherEducationValidAsterix = "";
			if(uF.parseToBoolean(empMotherEducationValidList.get(0))) {
				empMotherEducationValidReqOpt = "validateRequired";
				empMotherEducationValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empMotherEducationValidReqOpt", empMotherEducationValidReqOpt);
			request.setAttribute("empMotherEducationValidAsterix", empMotherEducationValidAsterix);
			
			List<String> empMotherOccupationValidList = hmValidationFields.get("EMP_MOTHER_OCCUPATION"); 
			String empMotherOccupationValidReqOpt = "";
			String empMotherOccupationValidAsterix = "";
			if(uF.parseToBoolean(empMotherOccupationValidList.get(0))) {
				empMotherOccupationValidReqOpt = "validateRequired";
				empMotherOccupationValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empMotherOccupationValidReqOpt", empMotherOccupationValidReqOpt);
			request.setAttribute("empMotherOccupationValidAsterix", empMotherOccupationValidAsterix);
			
			List<String> empMotherContactNoValidList = hmValidationFields.get("EMP_MOTHER_CONTACT_NO"); 
			String empMotherContactNoValidReqOpt = "";
			String empMotherContactNoValidAsterix = "";
			if(uF.parseToBoolean(empMotherContactNoValidList.get(0))) {
				empMotherContactNoValidReqOpt = "validateRequired";
				empMotherContactNoValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empMotherContactNoValidReqOpt", empMotherContactNoValidReqOpt);
			request.setAttribute("empMotherContactNoValidAsterix", empMotherContactNoValidAsterix);
			
			List<String> empMotherEmailIdValidList = hmValidationFields.get("EMP_MOTHER_EMAIL_ID"); 
			String empMotherEmailIdValidReqOpt = "validateEmail";
			String empMotherEmailIdValidAsterix = "";
			if(uF.parseToBoolean(empMotherEmailIdValidList.get(0))) {
				empMotherEmailIdValidReqOpt = "validateEmailRequired";
				empMotherEmailIdValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empMotherEmailIdValidReqOpt", empMotherEmailIdValidReqOpt);
			request.setAttribute("empMotherEmailIdValidAsterix", empMotherEmailIdValidAsterix);
			
			List<String> empSpouseNameValidList = hmValidationFields.get("EMP_SPOUSE_NAME");
			String empSpouseNameValidReqOpt = "";
			String empSpouseNameValidAsterix = "";
			if(uF.parseToBoolean(empSpouseNameValidList.get(0))) {
				empSpouseNameValidReqOpt = "validateRequired";
				empSpouseNameValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empSpouseNameValidReqOpt", empSpouseNameValidReqOpt);
			request.setAttribute("empSpouseNameValidAsterix", empSpouseNameValidAsterix);
			
			List<String> empSpouseDateOfBirthValidList = hmValidationFields.get("EMP_SPOUSE_DATE_OF_BIRTH");
			String empSpouseDateOfBirthValidReqOpt = "";
			String empSpouseDateOfBirthValidAsterix = "";
			if(uF.parseToBoolean(empSpouseDateOfBirthValidList.get(0))) {
				empSpouseDateOfBirthValidReqOpt = "validateRequired";
				empSpouseDateOfBirthValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empSpouseDateOfBirthValidReqOpt", empSpouseDateOfBirthValidReqOpt);
			request.setAttribute("empSpouseDateOfBirthValidAsterix", empSpouseDateOfBirthValidAsterix);
			
			List<String> empSpouseEducationValidList = hmValidationFields.get("EMP_SPOUSE_EDUCATION");
			String empSpouseEducationValidReqOpt = "";
			String empSpouseEducationValidAsterix = "";
			if(uF.parseToBoolean(empSpouseEducationValidList.get(0))) {
				empSpouseEducationValidReqOpt = "validateRequired";
				empSpouseEducationValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empSpouseEducationValidReqOpt", empSpouseEducationValidReqOpt);
			request.setAttribute("empSpouseEducationValidAsterix", empSpouseEducationValidAsterix);
			
			List<String> empSpouseOccupationValidList = hmValidationFields.get("EMP_SPOUSE_OCCUPATION");
			String empSpouseOccupationValidReqOpt = "";
			String empSpouseOccupationValidAsterix = "";
			if(uF.parseToBoolean(empSpouseOccupationValidList.get(0))) {
				empSpouseOccupationValidReqOpt = "validateRequired";
				empSpouseOccupationValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empSpouseOccupationValidReqOpt", empSpouseOccupationValidReqOpt);
			request.setAttribute("empSpouseOccupationValidAsterix", empSpouseOccupationValidAsterix);
			
			List<String> empSpouseContactNoValidList = hmValidationFields.get("EMP_SPOUSE_CONTACT_NO");
			String empSpouseContactNoValidReqOpt = "";
			String empSpouseContactNoValidAsterix = "";
			if(uF.parseToBoolean(empSpouseContactNoValidList.get(0))) {
				empSpouseContactNoValidReqOpt = "validateRequired";
				empSpouseContactNoValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empSpouseContactNoValidReqOpt", empSpouseContactNoValidReqOpt);
			request.setAttribute("empSpouseContactNoValidAsterix", empSpouseContactNoValidAsterix);
			
			List<String> empSpouseEmailIdValidList = hmValidationFields.get("EMP_SPOUSE_EMAIL_ID");
			String empSpouseEmailIdValidReqOpt = "validateEmail";
			String empSpouseEmailIdValidAsterix = "";
			if(uF.parseToBoolean(empSpouseEmailIdValidList.get(0))) {
				empSpouseEmailIdValidReqOpt = "validateEmailRequired";
				empSpouseEmailIdValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empSpouseEmailIdValidReqOpt", empSpouseEmailIdValidReqOpt);
			request.setAttribute("empSpouseEmailIdValidAsterix", empSpouseEmailIdValidAsterix);
			
			List<String> empSpouseGenderValidList = hmValidationFields.get("EMP_SPOUSE_GENDER");
			String empSpouseGenderValidReqOpt = "";
			String empSpouseGenderValidAsterix = "";
			if(uF.parseToBoolean(empSpouseGenderValidList.get(0))) {
				empSpouseGenderValidReqOpt = "validateRequired";
				empSpouseGenderValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empSpouseGenderValidReqOpt", empSpouseGenderValidReqOpt);
			request.setAttribute("empSpouseGenderValidAsterix", empSpouseGenderValidAsterix);
			
			List<String> empSiblingNameValidList = hmValidationFields.get("EMP_SIBLING_NAME");
			String empSiblingNameValidReqOpt = "";
			String empSiblingNameValidAsterix = "";
			if(uF.parseToBoolean(empSiblingNameValidList.get(0))) {
				empSiblingNameValidReqOpt = "validateRequired";
				empSiblingNameValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empSiblingNameValidReqOpt", empSiblingNameValidReqOpt);
			request.setAttribute("empSiblingNameValidAsterix", empSiblingNameValidAsterix);
			
			List<String> empSiblingDateOfBirthValidList = hmValidationFields.get("EMP_SIBLING_DATE_OF_BIRTH");
			String empSiblingDateOfBirthValidReqOpt = "";
			String empSiblingDateOfBirthValidAsterix = "";
			if(uF.parseToBoolean(empSiblingDateOfBirthValidList.get(0))) {
				empSiblingDateOfBirthValidReqOpt = "validateRequired";
				empSiblingDateOfBirthValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empSiblingDateOfBirthValidReqOpt", empSiblingDateOfBirthValidReqOpt);
			request.setAttribute("empSiblingDateOfBirthValidAsterix", empSiblingDateOfBirthValidAsterix);
			
			List<String> empSiblingEducationValidList = hmValidationFields.get("EMP_SIBLING_EDUCATION");
			String empSiblingEducationValidReqOpt = "";
			String empSiblingEducationValidAsterix = "";
			if(uF.parseToBoolean(empSiblingEducationValidList.get(0))) {
				empSiblingEducationValidReqOpt = "validateRequired";
				empSiblingEducationValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empSiblingEducationValidReqOpt", empSiblingEducationValidReqOpt);
			request.setAttribute("empSiblingEducationValidAsterix", empSiblingEducationValidAsterix);
			
			List<String> empSiblingOccupationValidList = hmValidationFields.get("EMP_SIBLING_OCCUPATION");
			String empSiblingOccupationValidReqOpt = "";
			String empSiblingOccupationValidAsterix = "";
			if(uF.parseToBoolean(empSiblingOccupationValidList.get(0))) {
				empSiblingOccupationValidReqOpt = "validateRequired";
				empSiblingOccupationValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empSiblingOccupationValidReqOpt", empSiblingOccupationValidReqOpt);
			request.setAttribute("empSiblingOccupationValidAsterix", empSiblingOccupationValidAsterix);
			
			List<String> empSiblingContactNoValidList = hmValidationFields.get("EMP_SIBLING_CONTACT_NO");
			String empSiblingContactNoValidReqOpt = "";
			String empSiblingContactNoValidAsterix = "";
			if(uF.parseToBoolean(empSiblingContactNoValidList.get(0))) {
				empSiblingContactNoValidReqOpt = "validateRequired";
				empSiblingContactNoValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empSiblingContactNoValidReqOpt", empSiblingContactNoValidReqOpt);
			request.setAttribute("empSiblingContactNoValidAsterix", empSiblingContactNoValidAsterix);
			
			List<String> empSiblingEmailIdValidList = hmValidationFields.get("EMP_SIBLING_EMAIL_ID");
			String empSiblingEmailIdValidReqOpt = "validateEmail";
			String empSiblingEmailIdValidAsterix = "";
			if(uF.parseToBoolean(empSiblingEmailIdValidList.get(0))) {
				empSiblingEmailIdValidReqOpt = "validateEmailRequired";
				empSiblingEmailIdValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empSiblingEmailIdValidReqOpt", empSiblingEmailIdValidReqOpt);
			request.setAttribute("empSiblingEmailIdValidAsterix", empSiblingEmailIdValidAsterix);
			
			List<String> empSiblingGenderValidList = hmValidationFields.get("EMP_SIBLING_GENDER");
			String empSiblingGenderValidReqOpt = "";
			String empSiblingGenderValidAsterix = "";
			if(uF.parseToBoolean(empSiblingGenderValidList.get(0))) {
				empSiblingGenderValidReqOpt = "validateRequired";
				empSiblingGenderValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empSiblingGenderValidReqOpt", empSiblingGenderValidReqOpt);
			request.setAttribute("empSiblingGenderValidAsterix", empSiblingGenderValidAsterix);
		
			List<String> empSiblingMaritalStatusValidList = hmValidationFields.get("EMP_SIBLING_MARITAL_STATUS");
			String empSiblingMaritalStatusValidReqOpt = "";
			String empSiblingMaritalStatusValidAsterix = "";
			if(uF.parseToBoolean(empSiblingMaritalStatusValidList.get(0))) {
				empSiblingMaritalStatusValidReqOpt = "validateRequired";
				empSiblingMaritalStatusValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empSiblingMaritalStatusValidReqOpt", empSiblingMaritalStatusValidReqOpt);
			request.setAttribute("empSiblingMaritalStatusValidAsterix", empSiblingMaritalStatusValidAsterix);
			
			List<String> empChildNameValidList = hmValidationFields.get("EMP_CHILD_NAME");
			String empChildNameValidReqOpt = "";
			String empChildNameValidAsterix = "";
			if(uF.parseToBoolean(empChildNameValidList.get(0))) {
				empChildNameValidReqOpt = "validateRequired";
				empChildNameValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empChildNameValidReqOpt", empChildNameValidReqOpt);
			request.setAttribute("empChildNameValidAsterix", empChildNameValidAsterix);
			
			List<String> empChildDateOfBirthValidList = hmValidationFields.get("EMP_CHILD_DATE_OF_BIRTH");
			String empChildDateOfBirthValidReqOpt = "";
			String empChildDateOfBirthValidAsterix = "";
			if(uF.parseToBoolean(empChildDateOfBirthValidList.get(0))) {
				empChildDateOfBirthValidReqOpt = "validateRequired";
				empChildDateOfBirthValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empChildDateOfBirthValidReqOpt", empChildDateOfBirthValidReqOpt);
			request.setAttribute("empChildDateOfBirthValidAsterix", empChildDateOfBirthValidAsterix);
			
			List<String> empChildEducationValidList = hmValidationFields.get("EMP_CHILD_EDUCATION");
			String empChildEducationValidReqOpt = "";
			String empChildEducationValidAsterix = "";
			if(uF.parseToBoolean(empChildEducationValidList.get(0))) {
				empChildEducationValidReqOpt = "validateRequired";
				empChildEducationValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empChildEducationValidReqOpt", empChildEducationValidReqOpt);
			request.setAttribute("empChildEducationValidAsterix", empChildEducationValidAsterix);
			
			List<String> empChildOccupationValidList = hmValidationFields.get("EMP_CHILD_OCCUPATION");
			String empChildOccupationValidReqOpt = "";
			String empChildOccupationValidAsterix = "";
			if(uF.parseToBoolean(empChildOccupationValidList.get(0))) {
				empChildOccupationValidReqOpt = "validateRequired";
				empChildOccupationValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empChildOccupationValidReqOpt", empChildOccupationValidReqOpt);
			request.setAttribute("empChildOccupationValidAsterix", empChildOccupationValidAsterix);
			
			List<String> empChildContactNoValidList = hmValidationFields.get("EMP_CHILD_CONTACT_NO");
			String empChildContactNoValidReqOpt = "";
			String empChildContactNoValidAsterix = "";
			if(uF.parseToBoolean(empChildContactNoValidList.get(0))) {
				empChildContactNoValidReqOpt = "validateRequired";
				empChildContactNoValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empChildContactNoValidReqOpt", empChildContactNoValidReqOpt);
			request.setAttribute("empChildContactNoValidAsterix", empChildContactNoValidAsterix);
			
			List<String> empChildEmailIdValidList = hmValidationFields.get("EMP_CHILD_EMAIL_ID");
			String empChildEmailIdValidReqOpt = "validateEmail";
			String empChildEmailIdValidAsterix = "";
			if(uF.parseToBoolean(empChildEmailIdValidList.get(0))) {
				empChildEmailIdValidReqOpt = "validateEmailRequired";
				empChildEmailIdValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empChildEmailIdValidReqOpt", empChildEmailIdValidReqOpt);
			request.setAttribute("empChildEmailIdValidAsterix", empChildEmailIdValidAsterix);
			
			List<String> empChildGenderValidList = hmValidationFields.get("EMP_CHILD_GENDER");
			String empChildGenderValidReqOpt = "";
			String empChildGenderValidAsterix = "";
			if(uF.parseToBoolean(empChildGenderValidList.get(0))) {
				empChildGenderValidReqOpt = "validateRequired";
				empChildGenderValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empChildGenderValidReqOpt", empChildGenderValidReqOpt);
			request.setAttribute("empChildGenderValidAsterix", empChildGenderValidAsterix);
			
			List<String> empChildMaritalStatusValidList = hmValidationFields.get("EMP_CHILD_MARITAL_STATUS");
			String empChildMaritalStatusValidReqOpt = "";
			String empChildMaritalStatusValidAsterix = "";
			if(uF.parseToBoolean(empChildMaritalStatusValidList.get(0))) {
				empChildMaritalStatusValidReqOpt = "validateRequired";
				empChildMaritalStatusValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empChildMaritalStatusValidReqOpt", empChildMaritalStatusValidReqOpt);
			request.setAttribute("empChildMaritalStatusValidAsterix", empChildMaritalStatusValidAsterix);
			
			List<String> empSpouseMaritalStatusValidList = hmValidationFields.get("SPOUSE_MARITAL_STATUS");
			String empSpouseMaritalStatusValidReqOpt = "";
			String empSpouseMaritalStatusValidAsterix = "";
			if(uF.parseToBoolean(empChildMaritalStatusValidList.get(0))) {
				empSpouseMaritalStatusValidReqOpt = "validateRequired";
				empSpouseMaritalStatusValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empSpouseMaritalStatusValidReqOpt", empSpouseMaritalStatusValidReqOpt);
			request.setAttribute("empSpouseMaritalStatusValidAsterix", empSpouseMaritalStatusValidAsterix);
			
			List<String> empFatherMRDNoValidList = hmValidationFields.get("EMP_FATHER_MRD_NO");
			String empFatherMRDNoValidReqOpt = "";
			String empFatherMRDNoValidAsterix = "";
			if (empFatherMRDNoValidList != null && empFatherMRDNoValidList.get(0) != null && uF.parseToBoolean(empFatherMRDNoValidList.get(0))) {
				empFatherMRDNoValidReqOpt = "validateRequired";
				empFatherMRDNoValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empFatherMRDNoValidReqOpt", empFatherMRDNoValidReqOpt);
			request.setAttribute("empFatherMRDNoValidAsterix", empFatherMRDNoValidAsterix);

			List<String> empMotherMRDNoValidList = hmValidationFields.get("EMP_MOTHER_MRD_NO");
			String empMotherMRDNoValidReqOpt = "";
			String empMotherMRDNoValidAsterix = "";
			if (empMotherMRDNoValidList != null && empMotherMRDNoValidList.get(0) != null && uF.parseToBoolean(empMotherMRDNoValidList.get(0))) {
				empMotherMRDNoValidReqOpt = "validateRequired";
				empMotherMRDNoValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empMotherMRDNoValidReqOpt", empMotherMRDNoValidReqOpt);
			request.setAttribute("empMotherMRDNoValidAsterix", empMotherMRDNoValidAsterix);

			List<String> empSpouseMRDNoValidList = hmValidationFields.get("EMP_SPOUSE_MRD_NO");
			String empSpouseMRDNoValidReqOpt = "";
			String empSpouseMRDNoValidAsterix = "";
			if (empSpouseMRDNoValidList != null && empSpouseMRDNoValidList.get(0) != null && uF.parseToBoolean(empSpouseMRDNoValidList.get(0))) {
				empSpouseMRDNoValidReqOpt = "validateRequired";
				empSpouseMRDNoValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empSpouseMRDNoValidReqOpt", empSpouseMRDNoValidReqOpt);
			request.setAttribute("empSpouseMRDNoValidAsterix", empSpouseMRDNoValidAsterix);

			List<String> empSiblingsMRDNoValidList = hmValidationFields.get("EMP_SIBLINGS_MRD_NO");
			String empSiblingsMRDNoValidReqOpt = "";
			String empSiblingsMRDNoValidAsterix = "";
			if (empSiblingsMRDNoValidList != null && empSiblingsMRDNoValidList.get(0) != null && uF.parseToBoolean(empSiblingsMRDNoValidList.get(0))) {
				empSiblingsMRDNoValidReqOpt = "validateRequired";
				empSiblingsMRDNoValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empSiblingsMRDNoValidReqOpt", empSiblingsMRDNoValidReqOpt);
			request.setAttribute("empSiblingsMRDNoValidAsterix", empSiblingsMRDNoValidAsterix);

			List<String> empChildMRDNoValidList = hmValidationFields.get("EMP_CHILD_MRD_NO");
			String empChildMRDNoValidReqOpt = "";
			String empChildMRDNoValidAsterix = "";
			if (empChildMRDNoValidList != null && empChildMRDNoValidList.get(0) != null && uF.parseToBoolean(empChildMRDNoValidList.get(0))) {
				empChildMRDNoValidReqOpt = "validateRequired";
				empChildMRDNoValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empChildMRDNoValidReqOpt", empChildMRDNoValidReqOpt);
			request.setAttribute("empChildMRDNoValidAsterix", empChildMRDNoValidAsterix);

			List<String> empRenewalDateValidList = hmValidationFields.get("EMP_KMCKNC_RENEWAL_DATE");
			String empRenewalDateValidReqOpt = "";
			String empRenewalDateValidAsterix = "";
			if (empRenewalDateValidList != null && empRenewalDateValidList.get(0) != null && uF.parseToBoolean(empRenewalDateValidList.get(0))) {
				empRenewalDateValidReqOpt = "validateRequired";
				empRenewalDateValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empRenewalDateValidReqOpt", empRenewalDateValidReqOpt);
			request.setAttribute("empRenewalDateValidAsterix", empRenewalDateValidAsterix);

			List<String> empKmcNoValidList = hmValidationFields.get("EMP_KMC_NO");
			String empKmcNoValidReqOpt = "";
			String empKmcNoValidAsterix = "";
			if (empKmcNoValidList != null && empKmcNoValidList.get(0) != null && uF.parseToBoolean(empKmcNoValidList.get(0))) {
				empKmcNoValidReqOpt = "validateRequired";
				empKmcNoValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empKmcNoValidReqOpt", empKmcNoValidReqOpt);
			request.setAttribute("empKmcNoValidAsterix", empKmcNoValidAsterix);

			List<String> empKncNoValidList = hmValidationFields.get("EMP_KNC_NO");
			String empKncNoValidReqOpt = "";
			String empKncNoValidAsterix = "";
			if (empKncNoValidList != null && empKncNoValidList.get(0) != null && uF.parseToBoolean(empKncNoValidList.get(0))) {
				empKncNoValidReqOpt = "validateRequired";
				empKncNoValidAsterix = "<sup>*</sup>";
			}
			request.setAttribute("empKncNoValidReqOpt", empKncNoValidReqOpt);
			request.setAttribute("empKncNoValidAsterix", empKncNoValidAsterix);
			
			
			
			Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
			if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
			
			List<List<String>> alDefaultSupportingDocs = new ArrayList<List<String>>();
			
			String validReqOpt = "";
			String validAsterix = "";
			
			List<String> empDocResumeValidList = hmValidationFields.get("EMP_DOC_RESUME"); 
			if(empDocResumeValidList != null && uF.parseToBoolean(empDocResumeValidList.get(0))) {
				validReqOpt = "validateRequired";
				validAsterix = "<sup>*</sup>";
			}
			List<String> innerList = new ArrayList<String>();
			innerList.add(DOCUMENT_RESUME);
			innerList.add(validReqOpt);
			innerList.add(validAsterix);
			alDefaultSupportingDocs.add(innerList);
			
			List<String> empDocIdProofValidList = hmValidationFields.get("EMP_DOC_IDENTITY_PROOF"); 
			validReqOpt = "";
			validAsterix = "";
			if(empDocIdProofValidList != null && uF.parseToBoolean(empDocIdProofValidList.get(0))) {
				validReqOpt = "validateRequired";
				validAsterix = "<sup>*</sup>";
			}
			innerList = new ArrayList<String>();
			innerList.add(DOCUMENT_ID_PROOF);
			innerList.add(validReqOpt);
			innerList.add(validAsterix);
			alDefaultSupportingDocs.add(innerList);
			
			List<String> empDocAddressProofValidList = hmValidationFields.get("EMP_DOC_ADDRESS_PROOF"); 
			validReqOpt = "";
			validAsterix = "";
			if(empDocAddressProofValidList != null && uF.parseToBoolean(empDocAddressProofValidList.get(0))) {
				validReqOpt = "validateRequired";
				validAsterix = "<sup>*</sup>";
			}
			innerList = new ArrayList<String>();
			innerList.add(DOCUMENT_ADDRESS_PROOF);
			innerList.add(validReqOpt);
			innerList.add(validAsterix);
			alDefaultSupportingDocs.add(innerList);
			
			if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_CURR_ADDRESS_PROOF_IN_DOCUMENT))) {
				List<String> empDocCurrAddressProofValidList = hmValidationFields.get("EMP_DOC_CURR_ADDRESS_PROOF"); 
				validReqOpt = "";
				validAsterix = "";
				if(empDocCurrAddressProofValidList != null && uF.parseToBoolean(empDocCurrAddressProofValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
				innerList = new ArrayList<String>();
				innerList.add(DOCUMENT_CURR_ADDRESS_PROOF);
				innerList.add(validReqOpt);
				innerList.add(validAsterix);
				alDefaultSupportingDocs.add(innerList);
			}
			
			if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_UAN_IN_DOCUMENT))) {
				List<String> empDocUANValidList = hmValidationFields.get("EMP_DOC_UAN"); 
				validReqOpt = "";
				validAsterix = "";
				if(empDocUANValidList != null && uF.parseToBoolean(empDocUANValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
				innerList = new ArrayList<String>();
				innerList.add(DOCUMENT_UAN);
				innerList.add(validReqOpt);
				innerList.add(validAsterix);
				alDefaultSupportingDocs.add(innerList);
			}
			
			if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_PASSPORT_IN_DOCUMENT))) {
				List<String> empDocPassportValidList = hmValidationFields.get("EMP_DOC_PASSPORT"); 
				validReqOpt = "";
				validAsterix = "";
				if(empDocPassportValidList != null && uF.parseToBoolean(empDocPassportValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
				innerList = new ArrayList<String>();
				innerList.add(DOCUMENT_PASSPORT);
				innerList.add(validReqOpt);
				innerList.add(validAsterix);
				alDefaultSupportingDocs.add(innerList);
			}
			
			if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_BANK_ACCOUNT_IN_DOCUMENT))) {
				List<String> empDocBankAccountValidList = hmValidationFields.get("EMP_DOC_BANK_ACCOUNT"); 
				validReqOpt = "";
				validAsterix = "";
				if(empDocBankAccountValidList != null && uF.parseToBoolean(empDocBankAccountValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
				innerList = new ArrayList<String>();
				innerList.add(DOCUMENT_BANK_ACCOUNT);
				innerList.add(validReqOpt);
				innerList.add(validAsterix);
				alDefaultSupportingDocs.add(innerList);
			}
			
		//===start parvez date: 28-10-2022===	
			if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_COMPANY_PROFILE_DOCUMENT))) {
				List<String> empDocCompanyProfileValidList = hmValidationFields.get("EMP_DOC_COMPANY_PROFILE"); 
				validReqOpt = "";
				validAsterix = "";
				if(empDocCompanyProfileValidList != null && uF.parseToBoolean(empDocCompanyProfileValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
				innerList = new ArrayList<String>();
				innerList.add(DOCUMENT_COMPANY_PROFILE);
				innerList.add(validReqOpt);
				innerList.add(validAsterix);
				alDefaultSupportingDocs.add(innerList);
			}
		//===end parvez date: 28-10-2022===	
			
			request.setAttribute("alDefaultSupportingDocs", alDefaultSupportingDocs);
	}

	private String getPrevCompNameList(Connection con, UtilityFunctions uF, int empId, String compName) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder("");
		try {
			pst = con.prepareStatement("select company_name from emp_prev_employment where emp_id = ?");
			pst.setInt(1, empId);
			rs = pst.executeQuery();
			int cnt = 0;
			while(rs.next()) {
				if (compName != null && rs.getString("company_name").equals(compName)) {
					sb.append("<option value=" + rs.getString("company_name") + " selected>" + rs.getString("company_name") + "</option>");
					cnt++;
				} else {
					sb.append("<option value=" + rs.getString("company_name") + ">" + rs.getString("company_name") + "</option>");
				}
//				if(prevCompList == null) prevCompList = new ArrayList<String>();
//				prevCompList.add(rs.getString("company_name"));
			}
			rs.close();
			pst.close();
			sb.append("<option value=Other " );
				if(cnt == 0) { sb.append(" selected " ); } 
			sb.append(">Other</option>");
			
			request.setAttribute("empPrevEmployment", sb.toString());
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
		return sb.toString();
	}
	
	
private String getOtherCompName(Connection con, UtilityFunctions uF, int empId, String compName) {
		
		PreparedStatement pst = null;
		ResultSet rs =null;
		StringBuilder sb = new StringBuilder("");
		try {
			pst = con.prepareStatement("select company_name from emp_prev_employment where emp_id = ?");
			pst.setInt(1, empId);
			rs = pst.executeQuery();
			int cnt = 0;
			while(rs.next()) {
				if (compName != null && rs.getString("company_name").equals(compName)) {
					cnt++;
//					System.out.println("compName ==>>> " + compName);
				}
			}
			rs.close();
			pst.close();
			if(cnt == 0) {
				sb.append(compName);
			}
//				System.out.println("sb ==>>> " + sb.toString());
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
		return sb.toString();
	}


	public void getEmpMiscInfo(Connection con,UtilityFunctions uF, String strEmpId) {
//		Database db = new Database();
		PreparedStatement pst = null;
		ResultSet rs =null;
//		Connection con = null;
		try {
//			con = db.makeConnection(con);
			pst = con.prepareStatement("select added_by, emp_fname, emp_lname from employee_personal_details where emp_per_id =?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			int nAddedBy = 0;
			while(rs.next()) {
				request.setAttribute("EMP_FNAME", rs.getString("emp_fname"));
				nAddedBy = uF.parseToInt(rs.getString("added_by"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select emp_email, emp_email_sec from employee_personal_details where emp_per_id =?");
			pst.setInt(1, nAddedBy);
			rs = pst.executeQuery();
			
			while(rs.next()) {
				if(rs.getString("emp_email_sec")!=null && rs.getString("emp_email_sec").length()>0) {
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
	
	
	String []strTime;
	String []strDate;

	public boolean insertAvailability(Connection con,UtilityFunctions uF, String strEmpId) {
		
//		Database db = new Database();
//		Connection con = null;
		boolean isValidSesseion = false;
		PreparedStatement pst = null;
		ResultSet rs =null;
		try {
			
//			con = db.makeConnection(con);
			
			for(int i=0; getStrDate()!=null && i<getStrDate().length; i++) {
				
				if(getStrDate()[i]!=null && getStrDate()[i].length()>0) {
					pst = con.prepareStatement("insert into emp_interview_availability (emp_id, ip_address, _timestamp, _date, _time) values (?,?,?,?,?)");		
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setString(2, "");
					pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
					pst.setDate(4, uF.getDateFormat(getStrDate()[i], DATE_FORMAT));
					pst.setTime(5, uF.getTimeFormat(getStrTime()[i], TIME_FORMAT));
					pst.execute();
					pst.close();
				}
			}
			
			
			pst = con.prepareStatement("update employee_personal_details set session_id =? where emp_per_id=?");
			pst.setString(1, "");
			pst.setInt(2, uF.parseToInt(getEmpId()));
			pst.execute();
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
		
		return isValidSesseion;
	}
	
	public boolean validateSession(Connection con,UtilityFunctions uF) {
		
		boolean isValidSesseion = false;
		PreparedStatement pst = null;
		ResultSet rs =null;
		try {
//			System.out.println("sessionId=====>"+(String)request.getParameter("sessionId"));
			pst = con.prepareStatement("select * from employee_personal_details where emp_per_id=?");
			pst.setInt(1, uF.parseToInt((String)request.getParameter("empId")));
			rs = pst.executeQuery();			
			while(rs.next()) {
				if(rs.getString("session_id")!=null) {
					String strSessionId = rs.getString("session_id");
//					System.out.println("rs.getString(session_i).trim()=====>"+rs.getString("session_id").trim()+"----strSessionId=====>"+strSessionId);
					if(strSessionId.equals((String)request.getParameter("sessionId"))) {
						isValidSesseion = true;
					}
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
		
		return isValidSesseion;
	}
	
	
	/*void insertEmpActivity(Connection con,int nActivityId, String strEmpId, CommonFunctions CF, HttpSession session, UtilityFunctions uF) {
		
		
//		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
//		Database db = new Database();
		
		try {
			
			
//			String strWLocation = CF.getEmpWlocationMap().get(getEmpId()+"");
//			String strDepartment = CF.getEmpDepartmentMap().get(getEmpId()+"");
//			String strLevel = CF.getEmpLevelMap().get(getEmpId()+"");
//			String strDesignation = CF.getEmpDesigMapId().get(getEmpId()+"");
//			String strGrade = CF.getGradeMap().get(getEmpId()+"");
//			String strNewStatus = CF.getEmpEmploymentStatusMap().get(getEmpId()+"");
//			String strActivity = "";
			
			
			int nWLocation = 0;
			int nDepartment = 0;
			int nLevel = 0;
			int nDesignation = 0;
			int nGrade = 0;
			int nActivity = 0;
			int nNoticePeriod = 0;
			int nProbationPeriod = 0;
			
//			con = db.makeConnection(con);
			
			pst = con.prepareStatement(selectEmpActivityDetails1);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			
			
//			log.debug(strEmpId+" getEmpId()===>"+getEmpId()+" pst===>"+pst);
			
			
			while(rs.next()) {
				nWLocation = rs.getInt("wlocation_id");
				nDepartment = rs.getInt("department_id");
				nLevel = rs.getInt("level_id");
				nDesignation = rs.getInt("desig_id");
				nGrade = rs.getInt("grade_id");
				nActivity = rs.getInt("activity_id");
				nNoticePeriod = rs.getInt("notice_period");
				nProbationPeriod = rs.getInt("probation_period");
			}
			
			
			pst = con.prepareStatement("select joining_date from employee_personal_details where emp_per_id=?");
			pst.setInt(1,  uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			String strJoiningDt = null;
			while(rs.next()) {
				strJoiningDt = rs.getString("joining_date");
			}
			
//			System.out.println("========== 2 ============");  
			String strReason = "";

			pst = con.prepareStatement("insert into employee_activity_details (wlocation_id, department_id, level_id, desig_id, grade_id, emp_status_code, " +
					"activity_id, reason, effective_date, entry_date, user_id, emp_id, notice_period, probation_period) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pst.setInt(1, nWLocation);
			pst.setInt(2, nDepartment);
			pst.setInt(3, nLevel);
			pst.setInt(4, nDesignation);
			pst.setInt(5, nGrade);
			pst.setString(6, "FT");
			pst.setInt(7, nActivityId);
			pst.setString(8, strReason);
//			pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(9, uF.getFutureDate(uF.getDateFormatUtil(strJoiningDt, DBDATE), nProbationPeriod));
			pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(11, uF.parseToInt((String)session.getAttribute(USERID)));
			pst.setInt(12, uF.parseToInt(strEmpId));
			pst.setInt(13, nNoticePeriod);
			pst.setInt(14, nProbationPeriod);
			pst.execute();
			
//			log.debug("pst==>"+pst);  
			
			
		}catch(Exception e) {   
			e.printStackTrace();
		}finally {
//			
//			db.closeStatements(pst);
//			db.closeConnection(con);
		}
		
	}*/

	public void insertEmpActivity(Connection con,String strEmpId, CommonFunctions CF, String strSessionEmpId, String activity_id) {

		UtilityFunctions uF = new UtilityFunctions();

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			String strOrg = CF.getEmpOrgId(con, uF, strEmpId);
			String strWLocation = CF.getEmpWlocationMap(con).get(strEmpId);
			String strDepartment = CF.getEmpDepartmentMap(con).get(strEmpId);
			String strLevel = CF.getEmpLevelMap(con).get(strEmpId);
			String strService = CF.getEmpServiceMap(con).get(strEmpId);
			String strDesignation = CF.getEmpDesigMapId(con).get(strEmpId);
			String strGrade = CF.getEmpGenderMap(con).get(strEmpId);
			String strNewStatus = CF.getEmpEmploymentStatusMap(con).get(strEmpId);
			String strActivity = "";

			if (activity_id == null) {
				if (getProbationDuration() > 0) {
					strActivity = ACTIVITY_PROBATION_ID;
				} else {
					strActivity = ACTIVITY_NEW_JOINEE_PENDING_ID;
				}
			} else {
				strActivity = activity_id;
			}
			String strReason = "";

			pst = con.prepareStatement("select joining_date from employee_personal_details where emp_per_id=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			String strJoiningDt = null;
			while (rs.next()) {
				strJoiningDt = rs.getString("joining_date");
			}
			rs.close();
			pst.close();

			// pst = con.prepareStatement(insertEmpActivity);
			pst = con.prepareStatement("insert into employee_activity_details (wlocation_id, department_id, level_id, desig_id, grade_id, "
					+ "emp_status_code, activity_id, reason, effective_date, entry_date, user_id, emp_id, notice_period, probation_period, appraisal_id, "
					+ "extend_probation_period, org_id,service_id,increment_type,increment_percent) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
			pst.setInt(1, uF.parseToInt(strWLocation));
			pst.setInt(2, uF.parseToInt(strDepartment));
			pst.setInt(3, uF.parseToInt(strLevel));
			pst.setInt(4, uF.parseToInt(strDesignation));
			pst.setInt(5, uF.parseToInt(strGrade));
			pst.setString(6, strNewStatus);
			pst.setInt(7, uF.parseToInt(strActivity));
			pst.setString(8, strReason);
			// pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
			if (activity_id != null) {
				pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
			} else {
				if (strJoiningDt != null && !strJoiningDt.equals("")) {
					pst.setDate(9, uF.getFutureDate(uF.getDateFormatUtil(strJoiningDt, DBDATE), getProbationDuration()));
				} else {
					pst.setDate(9, null);
				}
			}
			pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(11, uF.parseToInt(strSessionEmpId));
			pst.setInt(12, uF.parseToInt(strEmpId));
			pst.setInt(13, getNoticeDuration());
			pst.setInt(14, getProbationDuration());
			pst.setInt(15, 0);
			pst.setInt(16, 0);
			pst.setInt(17, uF.parseToInt(strOrg));
			pst.setString(18, strService);
			pst.setInt(19, 0);
			pst.setDouble(20, 0);
			int x = pst.executeUpdate();
			pst.close();

			if (x > 0) {
				/**
				 * Log Details
				 * */
				String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
				String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
				String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted activity of " + uF.showData(strEmpName, "") + " on " + ""
						+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
						+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
				LogDetails logDetails = new LogDetails();
				logDetails.session = session;
				logDetails.CF = CF;
				logDetails.request = request;
				logDetails.setProcessId(uF.parseToInt(getEmpId()));
				logDetails.setProcessType(L_EMPLOYEE);
				logDetails.setProcessActivity(L_ADD);
				logDetails.setProcessMsg(strProcessMsg);
				logDetails.setProcessStep(uF.parseToInt(getStep()));
				logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
				logDetails.insertLog(con, uF);
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


	private void updateEmployeeAjax(Connection con) {

		// Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		// Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();

		try {

			String statusType = (String) request.getParameter("value");
			int nEmpId = uF.parseToInt((String) request.getParameter("id"));

			if (statusType != null && statusType.equalsIgnoreCase(IConstants.TERMINATED)) {

				pst = con.prepareStatement(updateUserStatus2);
				pst.setString(1, IConstants.TERMINATED);
				pst.setBoolean(2, false);
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(4, uF.parseToInt(request.getParameter("id")));
				// log.debug("pst ==>"+pst);
				pst.execute();
				pst.close();

				pst = con
						.prepareStatement("insert into emp_off_board (emp_id, off_board_type, emp_reason, entry_date, notice_days, last_day_date) values (?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(request.getParameter("id")));
				pst.setString(2, IConstants.TERMINATED);
				pst.setString(3, "Direct termination");
				pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(5, 0);
				pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.execute();
				pst.close();

			} else if (statusType != null && statusType.equalsIgnoreCase(IConstants.RESIGNED)) {

				pst = con.prepareStatement(updateUserStatus2);
				pst.setString(1, IConstants.RESIGNED);
				pst.setBoolean(2, true);
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(4, uF.parseToInt(request.getParameter("id")));
				// log.debug("pst ==>"+pst);
				pst.execute();
				pst.close();

				pst = con
						.prepareStatement("insert into emp_off_board (emp_id, off_board_type, emp_reason, entry_date, notice_days, last_day_date) values (?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(request.getParameter("id")));
				pst.setString(2, IConstants.RESIGNED);
				pst.setString(3, "Direct resignation");
				pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(5, 0);
				pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.execute();
				pst.close();

			} else if (statusType != null && statusType.equalsIgnoreCase(IConstants.PERMANENT)) { 
				pst = con.prepareStatement("UPDATE employee_personal_details SET emp_status = ? WHERE emp_per_id = ?");
				pst.setString(1, PERMANENT);
				pst.setInt(2, uF.parseToInt(request.getParameter("id")));
				// log.debug("pst===>"+pst);
				pst.execute();
				pst.close();

				pst = con.prepareStatement("update probation_policy set is_probation=false where emp_id=?");
				pst.setInt(1, uF.parseToInt(request.getParameter("id")));
				pst.execute();
				pst.close();

			} else if (statusType != null && !statusType.equals("")) {

				pst = con.prepareStatement("UPDATE employee_personal_details SET emp_status = ? WHERE emp_per_id = ?");
				pst.setString(1, statusType);
				pst.setInt(2, uF.parseToInt(request.getParameter("id")));
				// log.debug("pst===>"+pst);
				pst.execute();
				pst.close();

			}

			if (statusType != null && !statusType.equals("")) {
				insertEmpActivity(con, request.getParameter("id"), CF, strSessionEmpId, ACTIVITY_EMP_STATUS_CHANGE_ID);
				// new EmployeeActivity().processActivity(nActivityId, nEmpId,
				// uF.getCurrentDate(CF.getStrTimeZone())+"", CF);
				// new EmployeeActivity().processActivity(con, nActivityId,
				// nEmpId, uF.getCurrentDate(CF.getStrTimeZone())+"", CF, uF);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ":" + e.getMessage(), e);
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
	   

	public void updateEmpFilledStatus(Connection con,int nEmpId) {
		
//		Connection con = null;
		PreparedStatement pst = null;
//		Database db = new Database();
		
		try {
			
//			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE employee_personal_details SET emp_filled_flag = ?, emp_filled_flag_date = ? " +
					"WHERE emp_per_id = ?");
			pst.setBoolean(1, true);
			
			java.util.Date date= new java.util.Date();
//			log.debug(new Timestamp(date.getTime()));
			pst.setTimestamp(2, new Timestamp(date.getTime()));
			
			pst.setInt(3, nEmpId);
			
//			log.debug("pst===>"+pst);
			pst.execute();
			pst.close();
			
			
		} catch (Exception e) {
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
	
	
	public void insertLeaveRegisterNewEmployee(Connection con, int nEmpId) {
		UtilityFunctions uF = new UtilityFunctions();
		try {

			// String levelId = CF.getEmpLevelId(con, nEmpId+"");
			// List<List<String>> leaveTypeListWithBalance =
			// CF.getLevelLeaveTypeBalanceForEmp(con, levelId, ""+getEmpId(),
			// CF);
			//
			// for (int i = 0; leaveTypeListWithBalance != null &&
			// !leaveTypeListWithBalance.isEmpty() && i <
			// leaveTypeListWithBalance.size(); i++) {
			// List<String> innerList = leaveTypeListWithBalance.get(i);
			// String strLeaveType = innerList.get(0);
			// String strLeaveBalance =
			// request.getParameter("leaveBal"+innerList.get(0));
			// String strLeaveTypeStatus =
			// request.getParameter(innerList.get(0));
			// if(uF.parseToInt(strLeaveTypeStatus) == 1 &&
			// uF.parseToDouble(strLeaveBalance) > 0.0d) {
			// CF.insertLeaveBalanceForNewEmployee(con, strLeaveType,
			// strLeaveBalance, ""+nEmpId, CF);
			// }
			// }

			Map<String, String> hmLeaveTypeMap = CF.getLeaveTypeMap(con);
			if (hmLeaveTypeMap == null)
				hmLeaveTypeMap = new HashMap<String, String>();
			Iterator<String> it = hmLeaveTypeMap.keySet().iterator();
			while (it.hasNext()) {
				String strLeaveTypeId = it.next();
				String strLeaveBalance = request.getParameter("leaveBal" + strLeaveTypeId);
				String strLeaveTypeStatus = request.getParameter(strLeaveTypeId);
//				System.out.println("strLeaveTypeId==>"+strLeaveTypeId+"--strLeaveBalance==>"+strLeaveBalance+"--strLeaveTypeStatus==>"+strLeaveTypeStatus);
				// if(uF.parseToInt(strLeaveTypeStatus) == 1 &&
				// uF.parseToDouble(strLeaveBalance) > 0.0d) {
				if (uF.parseToInt(strLeaveTypeStatus) == 1) {
					CF.insertLeaveBalanceForNewEmployee(con, strLeaveTypeId, strLeaveBalance, "" + nEmpId, CF);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	public void updateEmpLiveStatus(Connection con,int nEmpId) {

		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			pst = con.prepareStatement("UPDATE employee_personal_details SET is_alive = ?, approved_flag = ? " + "WHERE emp_per_id = ?");
			pst.setBoolean(1, true);
			pst.setBoolean(2, true);
			pst.setInt(3, nEmpId);
			int x = pst.executeUpdate();
			pst.close();

			if (x > 0) {
				/**
				 * Log Details
				 * */
				String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
				String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
				String strProcessMsg = uF.showData(strProcessByName, "") + " has active of " + uF.showData(strEmpName, "") + " on " + ""
						+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
						+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());

				LogDetails logDetails = new LogDetails();
				logDetails.session = session;
				logDetails.CF = CF;
				logDetails.request = request;
				logDetails.setProcessId(uF.parseToInt(getEmpId()));
				logDetails.setProcessType(L_EMPLOYEE);
				logDetails.setProcessActivity(L_UPDATE);
				logDetails.setProcessMsg(strProcessMsg);
				logDetails.setProcessStep(uF.parseToInt(getStep()));
				logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
				logDetails.insertLog(con, uF);
			}

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
	
	


	private void setEmpFnameLnameEmail(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		ResultSet rs =	null;
		
		try {
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement(selectEmployeeOfficialDetails);
			pst.setInt(1, uF.parseToInt(getEmpId()));
			
//			log.debug("pst===>"+pst);
			rs = pst.executeQuery();
			
			while(rs.next()) {
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


	public String loadValidateEmployee(Connection con) {
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		try {
			FillEmployee fillEmployee=new FillEmployee(request);
			UtilityFunctions uF = new UtilityFunctions();
			Map<String, List<String>> hmValidationFields = (Map<String, List<String>> ) request.getAttribute("hmValidationFields");
//			System.out.println("hmValidationFields ===>>>> " + hmValidationFields);
			String validReqOpt = "";
			String validAsterix = "";
			
//			System.out.println("getStrLevel() in list ===>> " + getStrLevel());
//			System.out.println("getStrDesignation() in list ===>> " + getStrDesignation());
			salutationList = new FillSalutation(request).fillSalutation();
			wLocationList = new FillWLocation(request).fillWLocation(getOrgId());
			orgList = new FillOrganisation(request).fillOrganisation();
			bankList = new FillBank(request).fillBankDetails();
			gradeList = new FillGrade(request).fillGradeFromDesignation(getStrDesignation());
			desigList = new FillDesig(request).fillDesigFromLevel(getStrLevel());
			levelList = new FillLevel(request).fillLevel(uF.parseToInt(getOrgId()));
			deptList = new FillDepartment(request).fillDepartment(uF.parseToInt(getOrgId()));
			// supervisorList = new FillEmployee().fillEmployeeCode(strUserType,strSessionEmpId);
			supervisorList = fillEmployee.fillSupervisorNameCode(uF.parseToInt(getEmpId()), getOrgId(), getDepartment());
			HodList = fillEmployee.fillHODNameCode("" + getEmpId(), getOrgId(), uF.parseToInt(getwLocation()), CF);
			
			if(getHod()==null || getHod().equals("")) {
				setHod("0");
			}
			if(getSupervisor()==null || getSupervisor().equals("")) {
				setSupervisor("0");
			}
			serviceList = new FillServices(request).fillServices(getOrgId(), uF);
			countryList = new FillCountry(request).fillCountry();
			// stateList = new FillState(request).fillState();
			stateList = new FillState(request).fillState();
			rosterDependencyList = new FillApproval().fillYesNo();
			empTypeList = new FillEmploymentType().fillEmploymentType(request);
			paymentModeList = new FillPayMode().fillPaymentMode();
			empGenderList = new FillGender().fillGender();
			probationDurationList = new FillProbationDuration().fillProbationDuration();
			noticeDurationList = new FillNoticeDuration().fillNoticeDuration();
			leaveTypeList = new ArrayList<FillLeaveType>();
//			if (getOrgId() != null && !getOrgId().equals("0") && getwLocation()!=null && !getwLocation().equals("") && getStrLevel()!=null && !getStrLevel().equals("")) {
			if (uF.parseToInt(getOrgId()) > 0 && uF.parseToInt(getwLocation()) > 0 && uF.parseToInt(getStrLevel()) > 0 && uF.parseToInt(getDefaultStatus()) > 0) {
				leaveTypeList = new FillLeaveType(request).fillLeave(uF.parseToInt(getOrgId()), uF.parseToInt(getwLocation()), uF.parseToInt(getStrLevel()), getDefaultStatus(), uF.parseToInt(getEmpId()), true);
			} else {
				leaveTypeList = new ArrayList<FillLeaveType>();
			}
		
			/*if (getOrgId() != null && !getOrgId().equals("0") && getwLocation()!=null && !getwLocation().equals("") && getStrLevel()!=null && !getStrLevel().equals("")) {
				leaveTypeList = new FillLeaveType(request).fillLeave(uF.parseToInt(getOrgId()));
			} else {
				leaveTypeList = new FillLeaveType(request).fillLeave();
			}*/
			
			if (getwLocation() != null && !getwLocation().equals("0")) {
				HRList = fillEmployee.fillEmployeeNameHR("" + getEmpId(), 0, uF.parseToInt(getwLocation()), CF, uF);

			} else if (getOrgId() != null && !getOrgId().equals("0")) {
				HRList = fillEmployee.fillEmployeeNameHR("" + getEmpId(), uF.parseToInt(getOrgId()), 0, CF, uF);
			} else {
				HRList = fillEmployee.fillEmployeeNameHR("" + getEmpId(), 0, 0, CF, uF);
			}
			if (getHR() == null || getHR().equals("")) {
				setHR("0");
			}
			maritalStatusList = new FillMaritalStatus().fillMaritalStatus();
			bloodGroupList = new FillBloodGroup().fillBloodGroup();
			degreeDurationList = new FillDegreeDuration().fillDegreeDuration();
			
			if (getEmpDateOfBirth() != null && !getEmpDateOfBirth().equals("") && !getEmpDateOfBirth().equals("-")) {
				yearsList = new FillYears().fillYears(uF.getCurrentDate(CF.getStrTimeZone()), uF.getDateFormat(getEmpDateOfBirth(), DATE_FORMAT, "yyyy"));
			}else {
				yearsList = new FillYears().fillYears(uF.getCurrentDate(CF.getStrTimeZone()));
			}
			
			skillsList = new FillSkills(request).fillSkillsWithId();
			educationalList = new FillEducational(request).fillEducationalQual();
			
			paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();
			
			financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
	
			request.setAttribute("degreeDurationList", degreeDurationList);
			request.setAttribute("empGenderList", empGenderList);
			request.setAttribute("currentYear", (uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()).toString(), DBDATE, "yyyy")));
			StringBuilder sbdegreeDuration = new StringBuilder();
			StringBuilder sbPrevEmployment = new StringBuilder();
			StringBuilder sbEmpReferences = new StringBuilder();
			StringBuilder sbSibling = new StringBuilder();
			StringBuilder sbChildren = new StringBuilder();

			StringBuilder sbSkills = new StringBuilder();
		
			sbdegreeDuration.append("<option value=''> Academics Degree</option>"); 
			for (int i = 0; i < educationalList.size(); i++) {
				sbdegreeDuration.append("<option value=" + ((FillEducational) educationalList.get(i)).getEduId()+ "> " + ((FillEducational) educationalList.get(i)).getEduName()
					+ "</option>");
			}
			sbdegreeDuration.append("<option value=other>Other</option>");
			sbdegreeDuration.append("</select>" + "</td>" + "<td><input type=hidden name=degreeNameOther id=hidedegreeNameOther />" +
				"<input type= text  style=height:25px; name=instName class="+(String)request.getAttribute("empDegreeInstituteNameValidReqOpt")+" ></input></td>");
			sbdegreeDuration.append("<td>" + "<select name=degreeDuration style=width:90px!important; class="+(String)request.getAttribute("empDegreeDurationValidReqOpt")+" >" + "<option value=''>Duration</option>");
			
			for (int i = 0; i < degreeDurationList.size(); i++) {
				sbdegreeDuration.append("<option value=" + ((FillDegreeDuration) degreeDurationList.get(i)).getDegreeDurationID() + "> "
					+ ((FillDegreeDuration) degreeDurationList.get(i)).getDegreeDurationName() + "</option>");
			}
			sbdegreeDuration.append("</select>" + "</td>" + "<td>" + "<select name=completionYear style=width:100px!important; class="+(String)request.getAttribute("empDegreeCompletionYearValidReqOpt")+" >" + "<option value=''>Completion Year</option>");

			for (int i = 0; i < yearsList.size(); i++) {
				sbdegreeDuration.append("<option value=" + ((FillYears) yearsList.get(i)).getYearsID() + "> " + ((FillYears) yearsList.get(i)).getYearsName() + "</option>");
			}

			sbdegreeDuration.append("</select>" + "</td>" + "<td><input type= text name=grade style=height:25px;width:80px!important; class="+(String)request.getAttribute("empDegreeGradeValidReqOpt")+" ></input></td>");

			request.setAttribute("sbdegreeDuration", sbdegreeDuration.toString());
			
			String countryOptions = getCountryOptions(con, null);
			String statesOptions = getStatesOptions(con, null, null);
			
			request.setAttribute("countryOptions", countryOptions);
			request.setAttribute("statesOptions", statesOptions);
			
			sbPrevEmployment.append(
			"<table class='table form-table'>"+
		 	"<tr><td class=txtlabel style=text-align:right> Company Name:</td>" +
			"<td><input type=text name=prevCompanyName style=; name=prevCompanyLocation ></input></td>" +
			"</tr>" +
			"<tr><td class=txtlabel style=text-align:right> Location:</td>" +
			"<td> <input type=text style=; name=prevCompanyLocation ></input></td>" +
			"</tr>" +
			"<tr><td class=txtlabel style=text-align:right> City: </td>" +
			"<td><input type=text style=; name=prevCompanyCity ></input></td>" +
			"</tr>" +
			"<tr><td class=txtlabel style=text-align:right>Country:</td><td><select id=prevCompanyCountry style=; name=prevCompanyCountry> <option value= >Select Country</option>" +
			countryOptions + "</select></td></tr>"+
			"<tr><td class=txtlabel style=text-align:right>State:</td><td id=prevEmpmentstateTD><select id=prevCompanyState  style=; name=prevCompanyState> <option value= >Select State</option>" +
			statesOptions + "</select></td> </tr>"+
			
//			"<tr><td class=txtlabel style=text-align:right> State:</td><td><input type=text style=; name=prevCompanyState ></input></td></tr>" +
//			"<tr><td class=txtlabel style=text-align:right> Country:</td><td><input type=text style=; name=prevCompanyCountry ></input></td></tr>" +
			
			"<tr><td class=txtlabel style=text-align:right> Contact Number:</td><td><input type=text style=; name=prevCompanyContactNo onkeypress='return isNumberKey(event)'></input>" +                
			"</td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> Reporting To:</td><td> <input type=text style=; name=prevCompanyReportingTo ></input>" +                
			"</td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> Reporting Manager Phone Number:</td><td> <input type=text style=height:25px;; name=prevCompanyReportManagerPhNo  onkeypress='return isNumberKey(event)'></input>" +
			"</td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> HR Manager:</td><td> <input type=text style=height:25px;; name=prevCompanyHRManager ></input>" +
			"</td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> HR Manager Phone Number:</td><td> <input type=text style=height:25px;; name=prevCompanyHRManagerPhNo  onkeypress='return isNumberKey(event)'></input>" +
			"</td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> From:</td><td> <input type=text style=; name=prevCompanyFromDate onchange=setcompanyTodd(this.value)></input></td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> To:</td><td> <input type=text style=; name=prevCompanyToDate ></input></td></tr> " +
			"<tr><td class=txtlabel style=text-align:right> Designation:</td><td> <input type=text style=; name=prevCompanyDesination ></input>" +                "</td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> Responsibility:</td><td> <input type=text style=; name=prevCompanyResponsibilities >" +                "</input>" +  
			"</td></tr>" + 
	//===start parvez date: 08-08-2022===		
			"<tr><td class=txtlabel style=text-align:right> Skills: </td><td> <input type=text style=; name=prevCompanySkills ></input></td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> ESIC No.: </td><td> <input type=text style=; name=prevCompanyESICNo ></input></td></tr>"+
			"<tr><td class=txtlabel style=text-align:right> UAN No.: </td><td> <input type=text style=; name=prevCompanyUANNo ></input></td></tr>"
			
	//===end parvez date: 08-08-2022===		
			//+ 
//			"<tr><td></td><td class=txtlabel style=text-align:right><a href=javascript:void(0) onclick=addPrevEmployment() class=add></a>"  
	
			);
			request.setAttribute("sbPrevEmployment", sbPrevEmployment.toString());
			
			
			String empPrevEmployment = getPrevCompNameList(con, uF, uF.parseToInt(getEmpId()), null);
			sbEmpReferences.append("<table border=0 class='table form-table'>");
//			sbEmpReferences.append("<tr><td class=tdLabelheadingBg style=text-align:center colspan=2><span style=color:#68AC3B; font-size:18px;>Step 5 : </span>Enter Employee References 1:</td></tr>");
			sbEmpReferences.append("<tr><td class=txtlabel style=text-align:right>Name:"+(String)request.getAttribute("empRefNameValidAsterix")+"</td>" +
				"<td><input type=text name=refName id=refName class="+(String)request.getAttribute("empRefNameValidReqOpt")+"></td></tr>");
			sbEmpReferences.append("<tr><td class=txtlabel style=text-align:right>Company:"+(String)request.getAttribute("empRefCompanyValidAsterix")+"</td>");
			sbEmpReferences.append("<td><select name=refCompany id=refCompany class="+(String)request.getAttribute("empRefCompanyValidReqOpt")+">");
			sbEmpReferences.append("<option value=>Select Company</option>"+empPrevEmployment);
			sbEmpReferences.append("</select></td></tr>");
			sbEmpReferences.append("<tr id=refCompOtherTR style=display:none;><td></td>");
			sbEmpReferences.append("<td><input type=text name=refCompanyOther id=refCompanyOther  class=autoWidth></td></tr>");
			sbEmpReferences.append("<tr><td class=txtlabel style=text-align:right>Designation:"+(String)request.getAttribute("empRefDesigValidAsterix")+"</td>" +
				"<td><input type=text name=refDesignation class="+(String)request.getAttribute("empRefDesigValidReqOpt")+"></td></tr>");
			sbEmpReferences.append("<tr><td class=txtlabel style=text-align:right>Contact No:"+(String)request.getAttribute("empRefContactNoValidAsterix")+"</td>" +
				"<td><input type=number step=any name=refContactNo onkeypress=' return isOnlyNumberKey(event)' class="+(String)request.getAttribute("empRefContactNoValidReqOpt")+"></td></tr>");
			sbEmpReferences.append("<tr><td class=txtlabel style=text-align:right>Email Id:"+(String)request.getAttribute("empRefEmailIdValidAsterix")+"</td>" +
				"<td><input type=email name=refEmail class="+(String)request.getAttribute("empRefEmailIdValidReqOpt")+"></td></tr>");
			
			
//			System.out.println("sbPrevEmployment ===>> " + sbPrevEmployment.toString());
			request.setAttribute("sbEmpReferences", sbEmpReferences.toString());
			
			sbSibling.append("<table class='table form-table' style=width:auto;>" +
		        "<tr><td style=text-align:center class=tdLabelheadingBg style=text-align:right colspan=2>Sibling\\'s Information </td></tr>" +    
		        "<tr><td class=txtlabel style=text-align:right>Name:</td><td><input type=text style=; name=memberName class=autoWidth></input></td></tr>" + 
				"<tr><td class=txtlabel style=text-align:right>Date of birth:</td><td> <input type=text style=; name=memberDob  class=autoWidth></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Education:</td><td> <input type=text style=; name=memberEducation  class=autoWidth></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Occupation:</td><td> <input type=text style=; name=memberOccupation  class=autoWidth></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Contact Number:</td><td><input type=number style=; name=memberContactNumber class=autoWidth onkeypress= 'return isOnlyNumberKey(event)'></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Email Id:</td><td><input type=email style=; name=memberEmailId  class=autoWidth></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>MRD No.:</td><td><input type=email style=; name=siblingsMRDNo  class=autoWidth></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Gender:</td><td>"+
				"<select name= memberGender >");
			sbSibling.append("<option value=''>Select Gender</option>");
			for (int i=0; i<empGenderList.size(); i++) {
				sbSibling.append("<option value="+((FillGender)empGenderList.get(i)).getGenderId()+"> "+((FillGender)empGenderList.get(i)).getGenderName()+"</option>"); 
						
			}
				
			sbSibling.append("</select></td></tr><tr><td class=txtlabel style=text-align:right>Marital Status:</td><td>"+
				"<select name= siblingMaritalStatus ><option value=''>Select Marital Status</option>");
			for (int i=0; i<maritalStatusList.size(); i++) {
				sbSibling.append("<option value="+((FillMaritalStatus)maritalStatusList.get(i)).getMaritalStatusId()+"> "+((FillMaritalStatus)maritalStatusList.get(i)).getMaritalStatusName()+"</option>"); 
			}
			sbSibling.append("</select>" +
			"</td></tr>");
			sbSibling.append("<tr><td class=txtlabel style=text-align:right>&nbsp;</td><td><a href=javascript:void(0) onclick=addSibling() class=add-font></a>" );
			
			request.setAttribute("sbSibling", sbSibling.toString());
			

			sbChildren.append("<table class='table form-table' style=width:auto;>" +
		        "<tr><td style=text-align:center class=tdLabelheadingBg style=text-align:right colspan=2>Childern's Information </td></tr>" +    
		        "<tr><td class=txtlabel style=text-align:right>Name:</td><td><input type=text style=; name=childName class=autoWidth></input></td></tr>" + 
				"<tr><td class=txtlabel style=text-align:right>Date of birth:</td><td> <input type=text style=; name=childDob class=autoWidth ></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Education:</td><td> <input type=text style=; name=childEducation  class=autoWidth></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Occupation:</td><td> <input type=text style=; name=childOccupation  class=autoWidth></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Contact Number:</td><td><input type=number style=; name=childContactNumber class=autoWidth onkeypress='return isOnlyNumberKey(event)'></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>MRD No.:</td><td><input type=email style=; name=childMRDNo class=autoWidth ></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Email Id:</td><td><input type=email style=; name=childEmailId class=autoWidth ></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Gender:</td><td>"+
				"<select name= childGender ><option value=0>Select Gender</option>");
			
			for (int i=0; i<empGenderList.size(); i++) {
				
				sbChildren.append("<option value="+((FillGender)empGenderList.get(i)).getGenderId()+"> "+((FillGender)empGenderList.get(i)).getGenderName()+"</option>"); 
						
			}
				
			sbChildren.append("</select>" +
					"</td></tr><tr><td class=txtlabel style=text-align:right>Marital Status:</td><td>"+
				"<select name= childMaritalStatus ><option value=0>Select Marital Status</option>");
			for (int i=0; i<maritalStatusList.size(); i++) {
					
					sbChildren.append("<option value="+((FillMaritalStatus)maritalStatusList.get(i)).getMaritalStatusId()+"> "+((FillMaritalStatus)maritalStatusList.get(i)).getMaritalStatusName()+"</option>"); 
							
				}
			sbChildren.append("</select>" +
			"</td></tr>");
			sbChildren.append("<tr><td class=txtlabel style=text-align:right>&nbsp;</td><td><a href=javascript:void(0) onclick=addChildren() class=add-font></a>" );
			
			request.setAttribute("sbChildren", sbChildren.toString());
			
			
			List<String> empSkillNameValidList = hmValidationFields.get("EMP_SKILL_NAME"); 
			validReqOpt = "";
			validAsterix = "";
			if(uF.parseToBoolean(empSkillNameValidList.get(0))) {
				validReqOpt = "validateRequired";
				validAsterix = "<sup>*</sup>";
			}
			
			List<String> empSkillRatingValidList = hmValidationFields.get("EMP_SKILL_RATING"); 
			String validReqOptSR = "";
			String validAsterixSR = "";
			if(uF.parseToBoolean(empSkillRatingValidList.get(0))) {
				validReqOptSR = "validateRequired";
				validAsterixSR = "<sup>*</sup>";
			}
			sbSkills.append("<td></td><td><select name=skillName class="+validAsterix+"><option value=>Select Skill</option>");
			
			for(int k=0; k< skillsList.size(); k++) { 
				sbSkills.append("<option value="+((FillSkills)skillsList.get(k)).getSkillsId()+"> "+((FillSkills)skillsList.get(k)).getSkillsName()+"</option>");
			}
			
			sbSkills.append("</select></td><td><select name=skillValue class="+validAsterixSR+"><option value=>Select Skill Rating</option>");
			
			for(int k=1; k< 11; k++) {
				sbSkills.append("<option value="+k+">"+k+"</option>");
			}
			
			sbSkills.append("</select></td>");
//			log.debug("sbSkills==>"+sbSkills);
			request.setAttribute("sbSkills", sbSkills);
			
			
			StringBuilder sb = new StringBuilder("");
			pst = con.prepareStatement("select * from appraisal_element order by appraisal_element_id");
			rst = pst.executeQuery();
			while (rst.next()) {
				sb.append("<option value='" + rst.getString("appraisal_element_id") + "'>"+ rst.getString("appraisal_element_name") + "</option>");
			}
			rst.close();
			pst.close();
			request.setAttribute("elementOptions",sb.toString());
			
			attributeList = new ArrayList<FillAttribute>();
			
			
		}catch(Exception ex) {
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

	
	private String getCountryOptions(Connection con, String country) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder("");
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			pst = con.prepareStatement("select * from country");
			rs = pst.executeQuery();
			while (rs.next()) {
				if (uF.parseToInt(rs.getString("country_id")) == uF.parseToInt(country)) {
					sb.append("<option value=" + rs.getString("country_id") + " selected>" + rs.getString("country_name") + "</option>");
				} else {
					sb.append("<option value=" + rs.getString("country_id") + ">" + rs.getString("country_name") + "</option>");
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("country", sb.toString());

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
		return sb.toString();
	}
	
	
	private String getStatesOptions(Connection con, String country, String state) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder("");
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from state ");
			if(country != null && uF.parseToInt(country) > 0) {
				sbQuery.append("where country_id = "+country+" ");
			}
			pst = con.prepareStatement("select * from state");
			rs = pst.executeQuery();
			while (rs.next()) {
				if (uF.parseToInt(rs.getString("state_id")) == uF.parseToInt(state)) {
					sb.append("<option value=" + rs.getString("state_id") + " selected>" + rs.getString("state_name") + "</option>");
				} else {
					sb.append("<option value=" + rs.getString("state_id") + ">" + rs.getString("state_name") + "</option>");
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("states", sb.toString());

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
		return sb.toString();
	}
	
	public List<FillEmployee> getHodList() {
		return HodList;
	}



	public void setHodList(List<FillEmployee> hodList) {
		HodList = hodList;
	}



	public String[] getSiblingMaritalStatus() {
		return siblingMaritalStatus;
	}



	public void setSiblingMaritalStatus(String[] siblingMaritalStatus) {
		this.siblingMaritalStatus = siblingMaritalStatus;
	}



	public String[] getChildMaritalStatus() {
		return childMaritalStatus;
	}



	public void setChildMaritalStatus(String[] childMaritalStatus) {
		this.childMaritalStatus = childMaritalStatus;
	}



	private void generateEmpCode(Connection con) {
		
//		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
//		Database db = new Database();
		String empCodeAlpha = "" , empCodeNum = ""; 
//		Map<String, String> hmSettings;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
//			hmSettings = CF.getSettingsMap(con);
//			empCodeAlpha = hmSettings.get(O_EMP_CODE_ALPHA);
//			if(uF.parseToInt(getEmpContractor()) == 2) {
//				empCodeAlpha = hmSettings.get(O_CONTRACTOR_CODE_ALPHA);
//			}
//			empCodeNum = hmSettings.get(O_EMP_CODE_NUM);
			
			pst = con.prepareStatement("SELECT emp_code_auto_generate,emp_code_alpha,contractor_code_alpha,emp_code_numeric FROM org_details where org_id = ?");
			pst.setInt(1, uF.parseToInt(getOrgId()));
//			System.out.println("pst ===>> " + pst);
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
			
//			if(getAutoGenerate()) {
				pst = con.prepareStatement("SELECT empcode FROM employee_personal_details where empcode like ? order by emp_per_id desc limit 1");
				pst.setString(1, empCodeAlpha+"%");
				rs = pst.executeQuery();
				boolean empcodeFlag = false;
				while(rs.next()) {
					empcodeFlag = true;
					String strEmpCode = rs.getString("empcode");
					String strEmpCodeNum = strEmpCode.substring(empCodeAlpha.length(), strEmpCode.length());
//					log.debug("code Number===>"+strEmpCodeNum);
					empCodeNum = (uF.parseToInt(strEmpCodeNum)+1) + "";
					//System.out.println("empCodeNum ===>> " + empCodeNum);
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
//				System.out.println("generateEmpCode final ----- getEmpCodeAlphabet() ===>> " + getEmpCodeAlphabet() + "  getEmpCodeNumber() ===>> " + getEmpCodeNumber());
//
//			request.setAttribute("EMPLOYEE_CODE", empCodeAlpha+empCodeNum);
//			setAutoGenerate(uF.parseToBoolean(hmSettings.get("EMP_CODE_AUTO_GENERATION")));
			/***
			 * This position of code changed on 26-04-2012 for always displaying the auto generated code
			 */
			
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
//					System.out.println("generateEmpCode empCodeNum ===>> " + empCodeNum);
					getLatestEmpCode(con, uF, empCodeAlpha, empCodeNum);
				}
				rs.close();
				pst.close();
				
				if(!flag) {
//					System.out.println("empCodeNum 111 ===>> " + empCodeNum);
					setEmpCodeAlphabet(empCodeAlpha);
					setEmpCodeNumber(empCodeNum);
					request.setAttribute("EMP_CODE_ALPHA", uF.showData(getEmpCodeAlphabet(), ""));
					request.setAttribute("EMP_CODE_NUM", uF.showData(getEmpCodeNumber(), ""));
				}
			
			/***
			 * This position of code changed on 26-04-2012 for always displaying the auto generated code
			 */
			
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

//		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
//		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
//		int nEmpOffId = 0;
		
		List<List<String>> alSkills = new ArrayList<List<String>>();
		List<List<String>> alHobbies;
		List<List<String>> alLanguages;
		List<List<String>> alEducation;
		List<List<Object>> alDocuments;
		List<List<String>> alPrevEmployment;
		List<List<String>> alEmpReferences;
		
		try {

//			con = db.makeConnection(con);
			setEmpPersonalDetails(con, uF);
			setEmpReferences(con, uF);
			setEmpOfficialDetails(con, uF);
			String levelId = CF.getEmpLevelId(con, ""+getEmpId());
			
//			List<List<String>> leaveTypeListWithBalance = CF.getLevelLeaveTypeBalanceForEmp(con, levelId, ""+getEmpId(), CF);
////			Map<String, String> hmEmpLeaveBalance = CF.getEmpExistLeaveBalance(con, ""+getEmpId(), CF);
//			Map<String, String> hmEmpLeaveBalance = new HashMap<String, String>();
//			EmployeeLeaveEntryReport leaveEntryReport = new EmployeeLeaveEntryReport();
//			leaveEntryReport.request = request;
//			leaveEntryReport.session = session;
//			leaveEntryReport.CF = CF;
//			leaveEntryReport.setStrEmpId(""+getEmpId());
//			leaveEntryReport.setDataType("L");
//			leaveEntryReport.viewEmployeeLeaveEntry1();
//			
//			java.util.List leaveList = (java.util.List)request.getAttribute("leaveList");
//			for (int i=0; leaveList!=null && i<leaveList.size(); i++) {
//				java.util.List cinnerlist = (java.util.List)leaveList.get(i);
//				if(uF.parseToDouble((String)cinnerlist.get(5)) > 0.0d) {
//					hmEmpLeaveBalance.put(""+cinnerlist.get(6), ""+cinnerlist.get(5));
//				}
//			}
			Map<String, String> hmLeaveTypeMap = CF.getLeaveTypeMap(con);
			if (hmLeaveTypeMap == null) hmLeaveTypeMap = new HashMap<String, String>();
			StringBuffer sbLeaves = null;
			for (int i = 0; getProbationLeaves() != null && i < getProbationLeaves().length; i++) {
				if (sbLeaves == null) {
					if (uF.parseToInt(getProbationLeaves()[i].trim()) > 0) {
						sbLeaves = new StringBuffer();
						sbLeaves.append(getProbationLeaves()[i].trim());
					}
				} else {
					if (uF.parseToInt(getProbationLeaves()[i].trim()) > 0) {
						sbLeaves.append(","+getProbationLeaves()[i].trim());
					}
				}
			}
			if (sbLeaves == null)
				sbLeaves = new StringBuffer();
 
			List<String> alExistLeave = new ArrayList<String>();
			// List<List<String>> leaveTypeListWithBalance =
			// CF.getLevelLeaveTypeBalanceForEmp(con, levelId, getStrEmpId(),
			// CF,alExistLeave);
			Map<String, String> hmProRataLeaveBalance = CF.getLevelLeaveTypeBalanceForEmp(con, CF, uF, uF.parseToInt(getEmpId()), uF.parseToInt(getOrgId()),
					uF.parseToInt(getwLocation()), uF.parseToInt(getStrLevel()), getEmpStartDate(), getEmpStatus(), sbLeaves.toString(), alExistLeave);
			List<String> alAccrueLeave = CF.getAccrueLeave(con, CF, uF, uF.parseToInt(getOrgId()), uF.parseToInt(getwLocation()),uF.parseToInt(getStrLevel()),sbLeaves.toString());
			if(alAccrueLeave == null) alAccrueLeave = new ArrayList<String>();
			
			Map<String, String> hmEmpLeaveBalance = new HashMap<String, String>();
			EmployeeLeaveEntryReport leaveEntryReport = new EmployeeLeaveEntryReport();
			leaveEntryReport.request = request;
			leaveEntryReport.session = session;
			leaveEntryReport.CF = CF;
			leaveEntryReport.setStrEmpId("" + getEmpId());
			leaveEntryReport.setDataType("L");
			leaveEntryReport.viewEmployeeLeaveEntry1();

//			System.out.println("alExistLeave ========>> " + alExistLeave); 
			java.util.List leaveList = (java.util.List) request.getAttribute("leaveList");
//			System.out.println("AdEmp/4028---leaveList ========>> " + leaveList); 
			List<List<String>> leaveTypeListWithBalance1 = new ArrayList<List<String>>();

			for (int j = 0; leaveList != null && j < leaveList.size(); j++) {
				List<String> cinnerlist = (List<String>) leaveList.get(j);
//				System.out.println("cinnerlist.get(6) ========>> " + cinnerlist.get(6));
//				System.out.println("if ========>> ");
				if(alAccrueLeave.contains((String)cinnerlist.get(6))){
					continue;
				}
				boolean existLeaveFlag = CF.checkExistLeaveType(con, (String)cinnerlist.get(6), getEmpId(), CF);
				if (uF.parseToDouble((String) cinnerlist.get(5)) > 0.0d || existLeaveFlag) {
//					System.out.println("AdEmp/4040---if if========>> "+hmLeaveTypeMap.get("" + cinnerlist.get(6)));
					hmEmpLeaveBalance.put("" + cinnerlist.get(6), "" + cinnerlist.get(5));
					List<String> innerList = new ArrayList<String>();
					innerList.add("" + cinnerlist.get(6));
					innerList.add(uF.showData(hmLeaveTypeMap.get("" + cinnerlist.get(6)), ""));
					innerList.add("0");
					leaveTypeListWithBalance1.add(innerList);
				} else {
					List<String> innerList = new ArrayList<String>();
					innerList.add("" + cinnerlist.get(6));
					innerList.add(uF.showData(hmLeaveTypeMap.get("" + cinnerlist.get(6)), ""));
					innerList.add("" + uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProRataLeaveBalance.get("" + cinnerlist.get(6)))));
					leaveTypeListWithBalance1.add(innerList);
				}
			}
			
			setUser(con, uF);
			setProbationPolicy(con, uF);
			
			alSkills = CF.selectSkills(con,uF.parseToInt(getEmpId())); 
			alHobbies = CF.selectHobbies(con,uF.parseToInt(getEmpId()));
			alLanguages = CF.selectLanguages(con, uF, uF.parseToInt(getEmpId()));
			alEducation = CF.selectEducation(con,uF.parseToInt(getEmpId()));
			
			String filePath = request.getRealPath("/userDocuments/");
			
			alDocuments = CF.selectDocuments(con,uF.parseToInt(getEmpId()), filePath);
			setEmpFamilyMembers(con, uF);
			alPrevEmployment = selectPrevEmploment(con,uF.parseToInt(getEmpId()));
			alEmpReferences = setEmpReferences(con, uF);
//			System.out.println("alEmpReferences ===> " + alEmpReferences);
			getPrevEmpTdsDetails(con,uF);
			
			setEmpMedicalInfo(con,uF);
			
//			request.setAttribute("strEdit", strEdit);
//			setEmpPerId(strEdit);
			
			wLocationList = new FillWLocation(request).fillWLocation(getOrgId());
			bankList = new FillBank(request).fillBankDetails();
//			desigList = new FillDesig().fillDesig();
			gradeList = new FillGrade(request).fillGrade();
			deptList = new FillDepartment(request).fillDepartment(uF.parseToInt(getOrgId()));
//			supervisorList = new FillEmployee().fillEmployeeCode(strUserType, strSessionEmpId);
			serviceList = new FillServices(request).fillServices(getOrgId(), uF);
			countryList = new FillCountry(request).fillCountry();
			stateList = new FillState(request).fillState();
			
			rosterDependencyList = new FillApproval().fillYesNo();
			empTypeList = new FillEmploymentType().fillEmploymentType(request);
			paymentModeList = new FillPayMode().fillPaymentMode();
			request.setAttribute("alSkills", alSkills);
			 
			request.setAttribute("alHobbies", alHobbies);
			request.setAttribute("alLanguages", alLanguages);
			request.setAttribute("alEducation", alEducation);
			request.setAttribute("alDocuments", alDocuments);
			request.setAttribute("alPrevEmployment", alPrevEmployment);
			request.setAttribute("alEmpReferences", alEmpReferences);
			request.setAttribute("leaveTypeListWithBalance", leaveTypeListWithBalance1);
			request.setAttribute("hmEmpLeaveBalance", hmEmpLeaveBalance);
			request.setAttribute("alAccrueLeave", alAccrueLeave);
			
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

		return SUCCESS;

	}
	
	private void getPrevEmpTdsDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst =null;
		ResultSet rs = null;
		
		try {
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			pst = con.prepareStatement("SELECT * FROM prev_earn_deduct_details WHERE emp_id = ? ");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			Map<String,String> hmPrevEmpTds=new HashMap<String, String>();
			while (rs.next()) {				
				hmPrevEmpTds.put("PREV_EARN_DEDUCT_ID", rs.getString("prev_earn_deduct_id"));
				hmPrevEmpTds.put("PREV_TOTAL_EARN", rs.getString("gross_amount"));
				hmPrevEmpTds.put("PREV_TOTALDEDUCT", rs.getString("tds_amount"));
				
				String document_name=rs.getString("document_name")!=null? "<a href=\"" + CF.getStrDocRetriveLocation() + rs.getString("document_name") + "\" title=\"Form 16\" ><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a><br/>":"";
				hmPrevEmpTds.put("PREV_DOCUMENT", document_name);
				
//				"<a target=\"_blank\" href=\"" + CF.getStrDocRetriveLocation() + strDocs[k] + "\">Document " + (k + 1) + "</a><br/>"
				
				String fDate=uF.getDateFormat(rs.getString("financial_start"), DBDATE, DATE_FORMAT)+"-"+uF.getDateFormat(rs.getString("financial_end"), DBDATE, DATE_FORMAT);
				hmPrevEmpTds.put("PREV_EMPF_YEAR", fDate);
				setPrevEmpFYear(fDate);
				hmPrevEmpTds.put("PREV_EMP_TDS_DETAILS_ENABLE", hmFeatureStatus.get(F_EMP_PREV_CMP_TDS_DETAILS));
		
		//===start parvez date: 31-03-2022===		
				hmPrevEmpTds.put("PREV_PAN_NUMBER", rs.getString("org_pan_no"));
				hmPrevEmpTds.put("PREV_TAN_NUMBER", rs.getString("org_tan_no"));
		//===end parvez date: 31-03-2022===		
			}
			rs.close();
			pst.close();
			request.setAttribute("hmPrevEmpTds", hmPrevEmpTds);
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



	private List<List<String>> setEmpReferences(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst =null;
		ResultSet rs = null;
		List<List<String>> alEmpReferences = new ArrayList<List<String>>();
		try {
			pst = con.prepareStatement("SELECT * FROM emp_references WHERE emp_id = ? order by ref_id");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("ref_id"));
				innerList.add(rs.getString("ref_name"));
				innerList.add(getPrevCompNameList(con, uF, uF.parseToInt(getEmpId()), rs.getString("ref_company")));
				innerList.add(getOtherCompName(con, uF, uF.parseToInt(getEmpId()), rs.getString("ref_company")));
				innerList.add(rs.getString("ref_designation"));
				innerList.add(rs.getString("ref_contact_no"));
				innerList.add(rs.getString("ref_email_id"));
	
				alEmpReferences.add(innerList);
			}
			rs.close();
			pst.close();

//			System.out.println("alEmpReferences ==>>> " + alEmpReferences);
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
		return alEmpReferences;
	}


	private void setEmpMedicalInfo(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst =null;
		ResultSet rs = null;
		
		try {
			
			pst = con.prepareStatement("SELECT * FROM emp_medical_details WHERE emp_id = ? order by question_id");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			
			while(rs.next()) {
				
				if(rs.getInt("question_id")==1) {
					setEmpMedicalId1(rs.getString("medical_id"));
					setCheckQue1(uF.parseToBoolean(rs.getString("yes_no")));
					setQue1Desc(uF.showData(rs.getString("description"), ""));
					setQue1DocName(uF.showData(rs.getString("filepath"), ""));
					
				} else if(rs.getInt("question_id")==2) {
					setEmpMedicalId2(rs.getString("medical_id"));
					setCheckQue2(uF.parseToBoolean(rs.getString("yes_no")));
					setQue2Desc(uF.showData(rs.getString("description"), ""));
					setQue2DocName(uF.showData(rs.getString("filepath"), ""));
					
				} else if(rs.getInt("question_id")==3) {
					setEmpMedicalId3(rs.getString("medical_id"));
					setCheckQue3(uF.parseToBoolean(rs.getString("yes_no")));
					setQue3Desc(uF.showData(rs.getString("description"), ""));
					setQue3DocName(uF.showData(rs.getString("filepath"), ""));
					
//				} else if(rs.getInt("question_id")==4) {
//					setEmpMedicalId4(rs.getString("medical_id"));
//					setCheckQue4(uF.parseToBoolean(rs.getString("yes_no")));
//					setQue4Desc(uF.showData(rs.getString("description"), ""));
//					
//				} else if(rs.getInt("question_id")==5) {
//					setEmpMedicalId5(rs.getString("medical_id"));
//					setCheckQue5(uF.parseToBoolean(rs.getString("yes_no")));
//					setQue5Desc(uF.showData(rs.getString("description"), ""));
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

	public void setEmpOfficialDetails(Connection con, UtilityFunctions uF) {
//		System.out.println("setEmpOfficialDetails==");
		PreparedStatement pst =null;
		ResultSet rs = null;
		
		try {
			
//			pst = con.prepareStatement("SELECT * FROM employee_official_details eod, employee_personal_details epd, department_info " +
//					"d WHERE eod.depart_id=d.dept_id and epd.emp_per_id=eod.emp_id AND emp_id=?");
//			pst = con.prepareStatement("SELECT * FROM employee_official_details where emp_id=?");
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			
			pst = con.prepareStatement("SELECT * FROM employee_official_details eod left join grades_details gd on gd.grade_id = eod.grade_id where emp_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
//			System.out.println("in query pst =====>> " + pst);
//			log.debug("selectEmployeeR2V===>"+pst);
			if (rs.next()) {
				setwLocation(rs.getString("wlocation_id"));
				setOrgId(rs.getString("org_id"));
//				setDesignation(rs.getString("designation_id"));
				setDepartment(rs.getString("depart_id"));
				setEmpGrade(rs.getString("grade_id"));
				setStrDesignation(rs.getString("designation_id"));
				setStrLevel(hmEmpLevelMap.get(rs.getString("emp_id")));
				setSupervisor(""+rs.getInt("supervisor_emp_id"));
				setHR(""+rs.getInt("emp_hr"));
				setHod(""+rs.getInt("hod_emp_id"));
//				setService(""+rs.getInt("service_id"));
				if(rs.getString("service_id")!=null) {
					setService(rs.getString("service_id").split(","));	
				}
				setStrPaycycleDuration(rs.getString("paycycle_duration"));
				setEmpPaymentMode(rs.getString("payment_mode"));
				
//				setAvailFrom(uF.getDateFormat(rs.getString("available_time_from"), DBTIME, CF.getStrReportTimeFormat()));
//				setAvailTo(uF.getDateFormat(rs.getString("available_time_to"), DBTIME, CF.getStrReportTimeFormat()));
				setRosterDependency(new FillApproval().getBoolValue(rs.getString("is_roster")));
				setAttendanceDependency(new FillApproval().getBoolValue(rs.getString("is_attendance")));
				setEmpType(rs.getString("emptype"));
				setIsFirstAidAllowance(uF.parseToBoolean(rs.getString("first_aid_allowance")));
				setBioId(rs.getString("biometrix_id"));
//				setEmpDesignation(rs.getString("designation_name"));
				/*setEmpBankName(rs.getString("emp_bank_name"));
				setEmpBankAcctNbr(rs.getString("emp_bank_acct_nbr"));
				setEmpEmailSec(rs.getString("emp_email_sec"));
				setSkypeId(rs.getString("skype_id"));*/ 
				setIsForm16(""+uF.parseToBoolean(rs.getString("is_form16")));
				setIsForm16A(""+uF.parseToBoolean(rs.getString("is_form16_a")));
				setSlabType(rs.getString("slab_type"));
				setEmpCorporateMobileNo(rs.getString("corporate_mobile_no"));
				setEmpCorporateDesk(rs.getString("corporate_desk"));
				setEmpContractor(uF.showData(rs.getString("emp_contractor"), "1"));
				request.setAttribute("EMP_OR_CONTRACTOR", uF.showData(rs.getString("emp_contractor"), "1"));
				
				setDefaultCXO(rs.getBoolean("is_cxo") ? "1" : "0");
				setDefaultHOD(rs.getBoolean("is_hod") ? "1" : "0");
				
				String departName = CF.getDepartMentNameById(con, rs.getString("depart_id"));
//				System.out.println("departName =======>> " +departName);
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
									
			pst = con.prepareStatement("SELECT emp_code_alpha,contractor_code_alpha FROM org_details where org_id = ?");
			pst.setInt(1, uF.parseToInt(getOrgId()));
			rs = pst.executeQuery();
			boolean isAlpha = false;
			if (rs.next()) {
				if(uF.parseToInt(getEmpContractor()) == 1) {
					if(rs.getString("emp_code_alpha") != null && !rs.getString("emp_code_alpha").equals("")) {
						isAlpha = true;
					}
				} else {
					if(rs.getString("contractor_code_alpha") != null && !rs.getString("contractor_code_alpha").equals("")) {
						isAlpha = true;
					}
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("isAlpha", isAlpha+"");
			request.setAttribute("empPayMode", getEmpPaymentMode());// TODO : @author : Dattatray

//			System.out.println("getStrLevel() ===>>" + getStrLevel());
//			System.out.println("getStrDesignation() ===>>" + getStrDesignation());
			getElementDetails(con, uF);
				
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

	
	private void getElementDetails(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String sb = CF.getElementDetails(con, request, null);
//			System.out.println("sb =========>> " + sb);
			
			Map<String, List<String>> hmValidationFields = (Map<String, List<String>> ) request.getAttribute("hmValidationFields");
			List<String> empKRAElementValidList = hmValidationFields.get("EMP_KRA_ELEMENT"); 
			String KRAElementValidReqOpt = "";
			String KRAElementValidAsterix = "";
			if(uF.parseToBoolean(empKRAElementValidList.get(0))) {
				KRAElementValidReqOpt = "validateRequired";
				KRAElementValidAsterix = "<sup>*</sup>";
			}
			
			StringBuilder sbelements = new StringBuilder();
			
			sbelements.append("<span>"+KRAElementValidAsterix+"" +
					"<select name=goalElements id=goalElements class="+KRAElementValidReqOpt+" style=width:130px!important; >"+
		    "<option value= >Select Element</option>"+sb+"</select>");
			request.setAttribute("elementSelectBox", sbelements.toString());
		    
			pst = con.prepareStatement("select * from goal_kras where emp_ids like '%,"+getEmpId()+",%' and goal_type = "+EMPLOYEE_KRA+" " +
					" and desig_kra_id = 0 order by goal_kra_id");
			rs = pst.executeQuery();
			List<List<String>> empKraDetails = new ArrayList<List<String>>();
			while(rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("goal_kra_id"));
				innerList.add(rs.getString("kra_description"));
				innerList.add(CF.getElementDetails(con, request, rs.getString("element_id")));
				innerList.add(CF.getAttributeListElementwise(con, uF, request, getOrgId(), rs.getString("element_id"), rs.getString("attribute_id")));
				Map<String, String> hmKRATaskData = getKRATaskDetails(con, uF, rs.getString("goal_kra_id"));
				innerList.add(hmKRATaskData.get("ID"));
				innerList.add(hmKRATaskData.get("NAME"));
				empKraDetails.add(innerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("empKraDetails", empKraDetails);
			
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
	
	
	private Map<String, String> getKRATaskDetails(Connection con, UtilityFunctions uF, String kraId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmKraTaskData = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from goal_kra_tasks where kra_id = ?");
			pst.setInt(1, uF.parseToInt(kraId));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while(rs.next()) {
				hmKraTaskData.put("ID", rs.getString("goal_kra_task_id"));
				hmKraTaskData.put("NAME", rs.getString("task_name"));
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
		return hmKraTaskData;
	}

	public String getHod() {
		return hod;
	}

	public void setHod(String hod) {
		this.hod = hod;
	}



	private int setEmpPersonalDetails(Connection con, UtilityFunctions uF) {

		PreparedStatement pst =null;
		ResultSet rs = null;
		int nEmpOffId = 0;
		
		try {
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
//			pst = con.prepareStatement(selectEmployeeR1V);
			pst = con.prepareStatement("select emp_contractor,org_id from employee_official_details where emp_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
//			log.debug("pst selectEmployeeR1V==>"+pst);
			rs = pst.executeQuery();
			if (rs.next()) {
				setEmpContractor(uF.showData(rs.getString("emp_contractor"), "1"));
				setOrgId(rs.getString("org_id"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement(selectEmployee1Details);
			pst.setInt(1, uF.parseToInt(getEmpId()));
//			log.debug("pst selectEmployeeR1V==>"+pst);
			rs = pst.executeQuery();
			if (rs.next()) {
//				nEmpOffId = rs.getInt("emp_off_id");
				setEmpId(rs.getString("emp_per_id"));
//				setEmpCode(rs.getString("empcode"));
				
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
				
				setEmpAddress1(rs.getString("emp_address1"));
				setEmpAddress2(uF.showData(rs.getString("emp_address2"), ""));
				setCity(rs.getString("emp_city_id"));
				setState(rs.getString("emp_state_id"));
				setCountry(rs.getString("emp_country_id"));
				setEmpPincode(rs.getString("emp_pincode"));
				
				setEmpAddress1Tmp(rs.getString("emp_address1_tmp"));
				setEmpAddress2Tmp(uF.showData(rs.getString("emp_address2_tmp"), ""));
				setCityTmp(rs.getString("emp_city_id_tmp"));
				setStateTmp(rs.getString("emp_state_id_tmp"));
				setCountryTmp(rs.getString("emp_country_id_tmp"));
				setEmpPincodeTmp(rs.getString("emp_pincode_tmp"));
				
				
				setEmpContactno(rs.getString("emp_contactno"));				
				setEmpPanNo(rs.getString("emp_pan_no"));
				setEmpMRDNo(rs.getString("emp_mrd_no"));
				setEmpPFNo(rs.getString("emp_pf_no"));
				setEmpGPFNo(rs.getString("emp_gpf_no"));
				setEmpGender(rs.getString("emp_gender"));
				setEmpDateOfBirth(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT));
				setEmpDateOfMarriage(uF.getDateFormat(rs.getString("emp_date_of_marriage"), DBDATE, DATE_FORMAT));
				setEmpBankName(rs.getString("emp_bank_name"));
				setEmpBankAcctNbr(rs.getString("emp_bank_acct_nbr"));
				
				setEmpBankIFSCNbr(rs.getString("emp_other_bank_acct_ifsc_code"));
//				System.out.println("ifsc=="+getEmpBankIFSCNbr());
				
				setEmpEmailSec(rs.getString("emp_email_sec"));
				setSkypeId(rs.getString("skype_id"));
				setEmpMobileNo(rs.getString("emp_contactno_mob"));
				
				setEmpEmergencyContactName(rs.getString("emergency_contact_name"));
				setEmpEmergencyContactNo(rs.getString("emergency_contact_no"));
		//===start parvez date: 30-07-2022===
				setEmpEmergencyContactRelation(rs.getString("emergency_contact_relation"));
		//===end parvez date: 30-07-2022===		
				setEmpDoctorName(rs.getString("doctor_name"));
				setEmpDoctorNo(rs.getString("doctor_contact_no"));
				setEmpUIDNo(rs.getString("uid_no"));
				setEmpUANNo(rs.getString("uan_no"));
				setEmpPassportNo(rs.getString("passport_no"));
				setEmpPassportExpiryDate(uF.getDateFormat(rs.getString("passport_expiry_date"), DBDATE, DATE_FORMAT));
				setEmpPFStartDate(uF.getDateFormat(rs.getString("pf_start_date"), DBDATE, DATE_FORMAT));
				setEmpBloodGroup(rs.getString("blood_group"));
				setEmpMaritalStatus(rs.getString("marital_status"));
				setEmpStartDate(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				setEmpESICNo(uF.showData(rs.getString("emp_esic_no"), ""));
				setEmpBankName(rs.getString("emp_bank_name"));
				setEmpBankName2(rs.getString("emp_bank_name2"));
				setEmpBankAcctNbr2(rs.getString("emp_bank_acct_nbr_2"));
				setEmpBankIFSCNbr2(rs.getString("emp_other_bank_acct_ifsc_code_2"));
		//===start parvez date: 12-08-2022===		
				setEmpOtherBankName(rs.getString("emp_other_bank_name"));
				setEmpOtherBankName2(rs.getString("emp_other_bank_name2"));
		//===end parvez date: 12-08-2022===		
				
				setEmpOtherBankBranch(rs.getString("emp_other_bank_branch"));
				setEmpOtherBankBranch2(rs.getString("emp_other_bank_branch2"));
				
//				System.out.println("ifsc2=="+getEmpBankIFSCNbr2());
				
//				log.debug("rs.getString(emp_img)=="+rs.getString("emp_image"));
				String strEmpImgPath = "";
				if(rs.getString("emp_image")!=null && !rs.getString("emp_image").equals("")) {
					if(CF.getStrDocSaveLocation()==null) {
						strEmpImgPath =  DOCUMENT_LOCATION +"/"+rs.getString("emp_image") ;
					} else {
						strEmpImgPath = CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+I_100x100+"/"+rs.getString("emp_image");
					}
				}
				String strImage = "<img class='lazy' id=\"empImage\" border=\"0\" style=\"height: 100px; width:100px; border: 1px solid #CCCCCC; padding: 5px;\" src=\""+strEmpImgPath+"\" data-original=\""+strEmpImgPath+"\" />";
				String strEmpImgCoverPath = "";
				if(rs.getString("emp_cover_image")!=null && !rs.getString("emp_cover_image").equals("")) {
					if(CF.getStrDocSaveLocation()==null) {
						strEmpImgCoverPath =  DOCUMENT_LOCATION +"/"+rs.getString("emp_cover_image") ;
					} else {
						strEmpImgCoverPath = CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE_COVER+"/"+rs.getString("emp_per_id")+"/"+rs.getString("emp_cover_image");
					}
				}
				
				String strCoverImage = "<img class='lazy' id=\"empCoverImage\" border=\"0\" style=\"height: 100px; width:100px; border: 1px solid #CCCCCC; padding: 5px;\" src=\""+strEmpImgCoverPath+"\" data-original=\""+strEmpImgCoverPath+"\" />";
				
				setEmpSeparationDate(uF.showData(uF.getDateFormat(rs.getString("separation_date"), DBDATE, DATE_FORMAT), ""));
				setEmpConfirmationDate(uF.showData(uF.getDateFormat(rs.getString("confirmation_date"), DBDATE, DATE_FORMAT), ""));
				setEmpActConfirmDate(uF.showData(uF.getDateFormat(rs.getString("actual_confirmation_date"), DBDATE, DATE_FORMAT), ""));
				setEmpPromotionDate(uF.showData(uF.getDateFormat(rs.getString("promotion_date"), DBDATE, DATE_FORMAT), ""));
				setEmpIncrementDate(uF.showData(uF.getDateFormat(rs.getString("increment_date"), DBDATE, DATE_FORMAT),""));
				// System.out.println("is_medical_professional ==>"+rs.getString("is_medical_professional"));
				if (uF.parseToBoolean(rs.getString("is_medical_professional"))) {
					setIsMedicalCheck("1");
				}

				setMedicalProfessional(uF.parseToBoolean(rs.getString("is_medical_professional")));
				setStrKmcNo(uF.showData(rs.getString("emp_kmc_no"), ""));
				setStrKncNo(uF.showData(rs.getString("emp_knc_no"), ""));
				setStrRenewalDate(uF.showData(uF.getDateFormat(rs.getString("renewal_date"), DBDATE, DATE_FORMAT), ""));
				// System.out.println("StrRenewalDate==>"+rs.getString("renewal_date"));

				// log.debug("rs.getString(emp_img)=="+rs.getString("emp_image"));

				if (getStrKmcNo() != null && !getStrKmcNo().equals("") && !getStrKmcNo().equalsIgnoreCase("null")) {
					setStrEmpKncKmc("1");
				} else if (getStrKncNo() != null && !getStrKncNo().equals("") && !getStrKncNo().equalsIgnoreCase("null")) {
					setStrEmpKncKmc("2");
				} else {
					setStrEmpKncKmc("0");
				}
				request.setAttribute("strImage", strImage);
				request.setAttribute("strEmImgPath", strEmpImgPath);
				request.setAttribute("strCoverImage", strCoverImage);
				request.setAttribute("strEmpImgCoverPath", strEmpImgCoverPath);
				request.setAttribute("EMPLOYEE_EMAIL", rs.getString("emp_email"));
				request.setAttribute("EMPLOYEE_EMAIL2", rs.getString("emp_email_sec"));
				request.setAttribute("EMP_CODE", rs.getString("empcode"));

				dobYear = uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "yyyy");
				request.setAttribute("dobYear",dobYear);
				
				request.setAttribute("strEmpName", uF.showData(rs.getString("emp_fname"),"") +" "+strEmpMName +" "+uF.showData(rs.getString("emp_lname"),"") );
				request.setAttribute("EMPSTATUS", rs.getString("emp_status"));
				/*if(rs.getString("service_id")!=null) {
					setService(rs.getString("service_id").split(","));
				}*/
				
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
		
		return nEmpOffId;
		
	}

	private void setEmpCode(String empCode, Connection con, UtilityFunctions uF) {
		PreparedStatement pst =null;
		ResultSet rs = null;
		
		try {
			
			String empCodeAlpha = null;
			pst = con.prepareStatement("SELECT emp_code_auto_generate,emp_code_alpha,contractor_code_alpha,emp_code_numeric FROM org_details where org_id = ?");
			pst.setInt(1, uF.parseToInt(getOrgId()));
//			System.out.println("pst ===>> " + pst);
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
			//System.out.println("empCode ===>> " + empCode);
			
			if(empCode!=null && empCode.contains(empCodeAlpha)) {
				
				setEmpCodeAlphabet(empCodeAlpha);
				setEmpCodeNumber(empCode.substring(empCodeAlpha.length(), empCode.length()));
				request.setAttribute("EMP_CODE_ALPHA", uF.showData(getEmpCodeAlphabet(), ""));
				request.setAttribute("EMP_CODE_NUM", uF.showData(getEmpCodeNumber(), ""));
				//System.out.println("if setEmpCode -- getEmpCodeAlphabet() ===>> " + getEmpCodeAlphabet() + "  getEmpCodeNumber() ===>> " + getEmpCodeNumber());
				
			} else if(empCode!=null && empCode.length()==0 && getAutoGenerate()) {
				generateEmpCode(con);
				
			} else if(empCode!=null && empCodeAlpha!=null && empCode.length()>empCodeAlpha.length()) {
				setEmpCodeAlphabet(empCode.substring(0, empCodeAlpha.length()));
				setEmpCodeNumber(empCode.substring(empCodeAlpha.length(), empCode.length()));
				request.setAttribute("EMP_CODE_ALPHA", uF.showData(getEmpCodeAlphabet(), ""));
				request.setAttribute("EMP_CODE_NUM", uF.showData(getEmpCodeNumber(), ""));
				//System.out.println("else if setEmpCode -- getEmpCodeAlphabet() ===>> " + getEmpCodeAlphabet() + "  getEmpCodeNumber() ===>> " + getEmpCodeNumber());
				
			} else {
				
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

	public void setUser(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst =null;
		ResultSet rs = null;
		
		try {
			
			pst = con.prepareStatement(selectUserV1);
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				setUserName(rs.getString("username"));
				setEmpPassword(rs.getString("password"));
				setEmpUserTypeId(rs.getString("usertype_id"));
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

	public void setEmpFamilyMembers(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<String>> alSiblings = new ArrayList<List<String>>();
		List<List<String>> alchilds = new ArrayList<List<String>>();
		try {

			pst = con.prepareStatement("SELECT * FROM emp_family_members WHERE emp_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			// log.debug("pst==>"+pst);
			rs = pst.executeQuery();

			while (rs.next()) {

				// log.debug("rs.getString(member_name)==>"+rs.getString("member_name"));

				if (rs.getString("member_type").equals(MOTHER)) {

					setMotherName(uF.showData(rs.getString("member_name"), ""));
					setMotherDob(uF.getDateFormat(rs.getString("member_dob"), DBDATE, DATE_FORMAT));
					setMotherEducation(uF.showData(rs.getString("member_education"), ""));
					setMotherOccupation(uF.showData(rs.getString("member_occupation"), ""));
					setMotherContactNumber(uF.showData(rs.getString("member_contact_no"), ""));
					setMotherEmailId(uF.showData(rs.getString("member_email_id"), ""));
					setMotherMRDNo(uF.showData(rs.getString("mrd_no"), ""));
				}

				if (rs.getString("member_type").equals(FATHER)) {

					setFatherName(uF.showData(rs.getString("member_name"), ""));
					setFatherDob(uF.getDateFormat(rs.getString("member_dob"), DBDATE, DATE_FORMAT));
					setFatherEducation(uF.showData(rs.getString("member_education"), ""));
					setFatherOccupation(uF.showData(rs.getString("member_occupation"), ""));
					setFatherContactNumber(uF.showData(rs.getString("member_contact_no"), ""));
					setFatherEmailId(uF.showData(rs.getString("member_email_id"), ""));
					setFatherMRDNo(uF.showData(rs.getString("mrd_no"), ""));
				}

				if (rs.getString("member_type").equals(SPOUSE)) {

					setSpouseName(uF.showData(rs.getString("member_name"), ""));
					setSpouseDob(uF.getDateFormat(rs.getString("member_dob"), DBDATE, DATE_FORMAT));
					setSpouseEducation(uF.showData(rs.getString("member_education"), ""));
					setSpouseOccupation(uF.showData(rs.getString("member_occupation"), ""));
					setSpouseContactNumber(uF.showData(rs.getString("member_contact_no"), ""));
					setSpouseEmailId(uF.showData(rs.getString("member_email_id"), ""));
					setSpouseGender(rs.getString("member_gender"));
					setSpouseMaritalStatus(rs.getString("member_marital"));
					setSpouseMRDNo(uF.showData(rs.getString("mrd_no"), ""));
				}

				if (rs.getString("member_type").equals(SIBLING)) {

					ArrayList<String> alInner = new ArrayList<String>();
					alInner.add(rs.getString("member_id"));// 0
					alInner.add(uF.showData(rs.getString("member_name"), ""));// 1
					alInner.add(uF.getDateFormat(rs.getString("member_dob"), DBDATE, DATE_FORMAT));// 2
					alInner.add(uF.showData(rs.getString("member_education"), ""));// 3
					alInner.add(uF.showData(rs.getString("member_occupation"), ""));// 4
					alInner.add(uF.showData(rs.getString("member_contact_no"), ""));// 5
					alInner.add(uF.showData(rs.getString("member_email_id"), ""));// 6
					alInner.add(rs.getString("member_gender"));// 7
					alInner.add(rs.getString("member_marital"));// 8
					alInner.add(uF.showData(rs.getString("mrd_no"), ""));// 9
					alSiblings.add(alInner);
				}

				if (rs.getString("member_type").equals(CHILD)) {

					ArrayList<String> alInner = new ArrayList<String>();
					alInner.add(rs.getString("member_id"));// 0
					alInner.add(uF.showData(rs.getString("member_name"), ""));// 1
					alInner.add(uF.getDateFormat(rs.getString("member_dob"), DBDATE, DATE_FORMAT));// 2
					alInner.add(uF.showData(rs.getString("member_education"), ""));// 3
					alInner.add(uF.showData(rs.getString("member_occupation"), ""));// 4
					alInner.add(uF.showData(rs.getString("member_contact_no"), ""));// 5
					alInner.add(uF.showData(rs.getString("member_email_id"), ""));// 6
					alInner.add(rs.getString("member_gender"));// 7
					alInner.add(rs.getString("member_marital"));// 8
					alInner.add(uF.showData(rs.getString("mrd_no"), ""));// 9
					alchilds.add(alInner);
				}

			}
			rs.close();
			pst.close();
			request.setAttribute("alSiblings", alSiblings);
			request.setAttribute("alchilds", alchilds);

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

	
	public String updateEmployee(Connection con) {
		
//		Connection con = null;
		PreparedStatement pst = null;
//		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
//			System.out.println("getStep() ===> " + getStep());
//			con = db.makeConnection(con);

			if(uF.parseToInt(getStep()) == 1) {
				if(getEmpImage()!=null){
					uploadImage(uF.parseToInt(getEmpId()),1);
				}
				if(getEmpCoverImage()!=null){
					uploadImage(uF.parseToInt(getEmpId()),2);
				}
				
				if(getEmpFname()!=null) {
					updateEmpPersonalDetails(con, uF);
				}
			} else if(uF.parseToInt(getStep()) == 4) {
				
				updateEmpReferences(con, uF);
				
			}
			
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
//			System.out.println("sbServices=======>"+sbServices.toString());
			if(sbServices == null) {
				sbServices = new StringBuilder();
			}
			
			if (strUserType != null && ( strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(RECRUITER) || strUserType.equalsIgnoreCase(MANAGER) || strUserType.equalsIgnoreCase(EMPLOYEE) ) ) {

				if(uF.parseToInt(getStep()) == 8) {
					if(uF.parseToInt(getwLocation())>0) {
						updateEmpCodeBankInfo(con,uF);
						updateEmpJoiningDate(con,uF);
						updateEmpOfficialDetailsAdmin(con, uF, sbServices);
						updateActivity(con,uF);
						if(getMode()!=null && getMode().equalsIgnoreCase("onboard")) {
							alignEmployeeWithProject(con, uF, getEmpId());
						}
						updateProbationPeriod(con, uF);
						if(uF.parseToInt(getEmpStatus()) != 1) {
							updateEmployeeStatus(con, uF);
						}
						boolean flag = checkEmpStatus(con,uF.parseToInt(getEmpId()));
						if(flag) {
							updateEmpFilledStatus(con,uF.parseToInt(getEmpId()));
							updateEmpLiveStatus(con,uF.parseToInt(getEmpId()));
							
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
						}
						
						
//						if(getMode() != null && getMode().equals("onboard")) {
// ====================== Update Kras ============================							
						//System.out.println("addDKRA ===>> ");
						AddKRA addDKRA = new AddKRA();
						addDKRA.setCF(CF);
						addDKRA.setServletRequest(request);
						addDKRA.setEmpId(empId+"");
						addDKRA.setStrSessionEmpId(strSessionEmpId);
						addDKRA.setKraSubmit("");
						addDKRA.setStrEffectiveDate(getEmpStartDate());
						addDKRA.setOrgId(getOrgId());
						addDKRA.setLevelId(getStrLevel());
						addDKRA.setGradeId(getEmpGrade());
						addDKRA.setGoalElements(getDesigElements());
						addDKRA.setCgoalAlignAttribute(getDesigElementAttribute());
						addDKRA.setEmpKra(getDesignKRA());
						addDKRA.setEmpKraTask(getDesignKRATask());
						addDKRA.setDesigKraId(getDesigKraId());
						addDKRA.setEmpKraId(getDesigEmpKraId());
						addDKRA.setEmpKraTaskId(getDesigEmpKraTaskId());
						addDKRA.setStatus(getStatus());
						addDKRA.setKRAType("DESIG");
						addDKRA.insertKRA();
						
						//System.out.println("addEmpKRA ===>> ");
						AddKRA addEmpKRA = new AddKRA();
						addEmpKRA.setCF(CF);
						addEmpKRA.setServletRequest(request);
						addEmpKRA.setEmpId(empId+"");
						addEmpKRA.setStrSessionEmpId(strSessionEmpId);
						addEmpKRA.setKraSubmit("");
						addEmpKRA.setStrEffectiveDate(getEmpStartDate());
						addEmpKRA.setOrgId(getOrgId());
						addEmpKRA.setLevelId(getStrLevel());
						addEmpKRA.setGradeId(getEmpGrade());
						addEmpKRA.setGoalElements(getGoalElements());
						addEmpKRA.setCgoalAlignAttribute(getElementAttribute());
						addEmpKRA.setEmpKra(getEmpKRA());
						addEmpKRA.setEmpKraTask(getEmpKRATask());
						addEmpKRA.setEmpKraId(getEmpKraId());
						addEmpKRA.setEmpKraTaskId(getEmpKraTaskId());
						addDKRA.setKRAType("EMP");
						addEmpKRA.insertKRA();
//						}
						
						insertLeaveRegisterNewEmployee(con, uF.parseToInt(getEmpId()));
						CF.assignReimbursementCTCForEmployee(request,con,CF,uF,uF.parseToInt(getEmpId()));
					}
					
				} else
//					updateUser(con,uF, strEmpType);
				if(uF.parseToInt(getStep()) == 2) {
					updateSkills(con, uF);
					updateHobbies(con, uF);
					updateEmpEducation(con, uF);
					updateEmpLangues(con, uF);
				
				} else if(uF.parseToInt(getStep()) == 5) {
					updateEmpFamilyMembers(con, uF);
				
				} else if(uF.parseToInt(getStep()) == 3) {
					updateEmpPrevEmploment(con, uF);
				
				} else if(uF.parseToInt(getStep()) == 6) {
					updateEmpMedicalInfo(con, uF);
				
				} else if(uF.parseToInt(getStep()) == 7) {
					updateSupportingDocuments(con, uF);
					
				} else if (uF.parseToInt(getStep()) == 9) {
					
					//check if all salary information has been filled up.
					if(checkAllSalaryInfoFilled(con, uF)) {
						approveEmployee(con, uF);
					} else {
						approveEmployee(con, uF);
					}
				}
			}
//			request.setAttribute(MESSAGE, getEmpCode() + " updated successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in updation");
			
		} finally {
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return SUCCESS;

	}

	
	private void alignEmployeeWithProject(Connection con, UtilityFunctions uF, String empId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean flag = false; 
		try {
			pst = con.prepareStatement("select cad.*, rd.skills from candidate_application_details cad, recruitment_details rd where rd.recruitment_id = cad.recruitment_id and candididate_emp_id=?");
			pst.setInt(1, uF.parseToInt(empId));
			rs = pst.executeQuery();
			List<String> candiEmpData = new ArrayList<String>();
			while(rs.next()) {
				candiEmpData.add(rs.getString("aligned_pro_id"));
				candiEmpData.add(rs.getString("skills"));
				candiEmpData.add(rs.getString("candidate_id"));
				candiEmpData.add(rs.getString("recruitment_id"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("update project_emp_details set emp_id=?,allocation_percent=?,is_billed=? where pro_id=?");
			pst.setInt(1, uF.parseToInt(empId));
			pst.setDouble(2, 100);
			pst.setBoolean(3, true);
			pst.setInt(4, uF.parseToInt(candiEmpData.get(0)));
			pst.executeUpdate();
			pst.close();
			
			List<String> alJDSkillIds = new ArrayList<String>();
			if(candiEmpData.get(1)!=null && candiEmpData.get(1).length()>0) {
				alJDSkillIds = Arrays.asList(candiEmpData.get(1).split(","));
			}
			pst = con.prepareStatement("select * from project_resource_req_details where pro_id=?");
			pst.setInt(1, uF.parseToInt(candiEmpData.get(0)));
			rs = pst.executeQuery();
			String proResReqId = null;
			double resGap = 0;
			while(rs.next()) {
				if(alJDSkillIds.contains(rs.getString("skill_id"))) {
					proResReqId = rs.getString("project_resource_req_id");
					resGap = rs.getDouble("resource_gap");
				}
			}
			rs.close();
			pst.close();
			
			resGap--;
			pst = con.prepareStatement("update project_resource_req_details set resource_gap=? where project_resource_req_id=?");
			pst.setDouble(1, resGap);
			pst.setInt(2, uF.parseToInt(proResReqId));
			pst.executeUpdate();
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

	private void updateEmployeeStatus(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		try {
			String strEmpStatus = PERMANENT;
			if(uF.parseToInt(getEmpStatus()) == 2) {
				strEmpStatus = PERMANENT;
			} else if(uF.parseToInt(getEmpStatus()) == 4) {
				strEmpStatus = TEMPORARY;
			}
			pst = con.prepareStatement("update employee_personal_details set emp_status = ? where emp_per_id = ?");
			pst.setString(1, strEmpStatus);
			pst.setInt(2, uF.parseToInt(getEmpId()));
			int x = pst.executeUpdate();
			pst.close();

			if (x > 0) {
				/**
				 * Log Details
				 * */
				String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
				String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
				String strProcessMsg = uF.showData(strProcessByName, "") + " has update employee status as "+strEmpStatus+" of " + uF.showData(strEmpName, "")
						+ " on " + "" + uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
						+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
				LogDetails logDetails = new LogDetails();
				logDetails.session = session;
				logDetails.CF = CF;
				logDetails.request = request;
				logDetails.setProcessId(uF.parseToInt(getEmpId()));
				logDetails.setProcessType(L_EMPLOYEE);
				logDetails.setProcessActivity(L_UPDATE);
				logDetails.setProcessMsg(strProcessMsg);
				logDetails.setProcessStep(uF.parseToInt(getStep()));
				logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
				logDetails.insertLog(con, uF);
			}

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



	private boolean checkAllSalaryInfoFilled(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			setEmpOfficialDetails(con, uF);
			
			if(getService()!=null && getService().length > 0) {
				int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
				if(nSalaryStrucuterType == S_GRADE_WISE){
					String strEmpGradeId = CF.getEmpGradeId(con, getEmpId());
					int cnt = 0;
					int i = 0;
					for(; i < getService().length ; i++) {
						
						pst = con.prepareStatement("SELECT  * from emp_salary_details " +
								"WHERE emp_id = ? and service_id = ? and grade_id=? limit 1");
						pst.setInt(1, uF.parseToInt(getEmpId()));
						pst.setInt(2, uF.parseToInt(getService()[i]));
						pst.setInt(3, uF.parseToInt(strEmpGradeId));
	//					log.debug("pst====>"+pst);
						rs = pst.executeQuery();
	//					System.out.println("pst ===>>>> " + pst);
						while(rs.next()) {
							cnt++;
						}
						rs.close();
						pst.close();
					}
					if(getService().length==cnt) {
	//					log.debug("all Info available!!");
						return true;
					}
				} else {	
					String strEmpLevelId = CF.getEmpLevelId(con, getEmpId());
					int cnt = 0;
					int i = 0;
					for(; i < getService().length ; i++) {
						
						pst = con.prepareStatement("SELECT  * from emp_salary_details " +
								"WHERE emp_id = ? and service_id = ? and level_id=? limit 1");
						pst.setInt(1, uF.parseToInt(getEmpId()));
						pst.setInt(2, uF.parseToInt(getService()[i]));
						pst.setInt(3, uF.parseToInt(strEmpLevelId));
	//					log.debug("pst====>"+pst);
						rs = pst.executeQuery();
	//					System.out.println("pst ===>>>> " + pst);
						while(rs.next()) {
							cnt++;
						}
						rs.close();
						pst.close();
					}
					if(getService().length==cnt) {
	//					log.debug("all Info available!!");
						return true;
					}
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
		return false;
	}


	private void updateActivity(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;

		try {
			pst = con.prepareStatement(updateGradeDesigLevel);
			pst.setInt(1, uF.parseToInt(getEmpGrade()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			pst.setInt(3, uF.parseToInt(getDepartment()));
			pst.setInt(4, uF.parseToInt(getEmpGrade()));
			pst.setInt(5, uF.parseToInt(getEmpGrade()));
			pst.setInt(6, uF.parseToInt(getEmpId()));
			pst.setInt(7, uF.parseToInt(getEmpId()));
			pst.execute();
			pst.close();

			pst = con.prepareStatement("UPDATE employee_activity_details SET emp_status_code=?, notice_period=?, probation_period=? where emp_id=?  and "
					+ "entry_date = (select max(entry_date) from employee_activity_details WHERE emp_id = ?)");
			pst.setString(1, getEmpType());
			pst.setInt(2, getNoticeDuration());
			pst.setInt(3, getProbationDuration());
			pst.setInt(4, uF.parseToInt(getEmpId()));
			pst.setInt(5, uF.parseToInt(getEmpId()));
			int x = pst.executeUpdate();
			pst.close();

			if (x > 0) {
				/**
				 * Log Details
				 * */
				String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
				String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
				String strProcessMsg = uF.showData(strProcessByName, "") + " has updated activity of " + uF.showData(strEmpName, "") + " on " + ""
						+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
						+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
				LogDetails logDetails = new LogDetails();
				logDetails.session = session;
				logDetails.CF = CF;
				logDetails.request = request;
				logDetails.setProcessId(uF.parseToInt(getEmpId()));
				logDetails.setProcessType(L_EMPLOYEE);
				logDetails.setProcessActivity(L_UPDATE);
				logDetails.setProcessMsg(strProcessMsg);
				logDetails.setProcessStep(uF.parseToInt(getStep()));
				logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
				logDetails.insertLog(con, uF);
			}

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


	private void updateEmpJoiningDate(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("UPDATE employee_personal_details SET joining_date = ?, skype_id=?, emp_email_sec=?,separation_date=?, "
					+ " confirmation_date=?,actual_confirmation_date=?,promotion_date=?,increment_date=?where emp_per_id = ?");
			pst.setDate(1, uF.getDateFormat(getEmpStartDate(), DATE_FORMAT));
			pst.setString(2, getSkypeId());
			pst.setString(3, getEmpEmailSec());
			pst.setDate(4, uF.getDateFormat(getEmpSeparationDate(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getEmpConfirmationDate(), DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(getEmpActConfirmDate(), DATE_FORMAT));
			pst.setDate(7, uF.getDateFormat(getEmpPromotionDate(), DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(getEmpIncrementDate(), DATE_FORMAT));
			pst.setInt(9, uF.parseToInt(getEmpId()));
			int x = pst.executeUpdate();
			pst.close();

			if (x > 0) {
				/**
				 * Log Details
				 * */
				String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
				String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
				String strProcessMsg = uF.showData(strProcessByName, "") + " has updated joining date of " + uF.showData(strEmpName, "") + " on " + ""
						+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
						+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
				LogDetails logDetails = new LogDetails();
				logDetails.session = session;
				logDetails.CF = CF;
				logDetails.request = request;
				logDetails.setProcessId(uF.parseToInt(getEmpId()));
				logDetails.setProcessType(L_EMPLOYEE);
				logDetails.setProcessActivity(L_UPDATE);
				logDetails.setProcessMsg(strProcessMsg);
				logDetails.setProcessStep(uF.parseToInt(getStep()));
				logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
				logDetails.insertLog(con, uF);
			}

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


	private void updateEmpReferences(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;

		try {
			pst = con.prepareStatement("delete from emp_references where emp_id = ? ");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.executeUpdate();
			pst.close();

			// System.out.println("getRefName() ===>>>> " +
			// getRefName().length);
			// System.out.println("getRefCompany() ===>>>> " +
			// getRefCompany().length);
			// System.out.println("getRefDesignation() ===>>>> " +
			// getRefDesignation().length);
			// System.out.println("getRefContactNo() ===>>>> " +
			// getRefContactNo().length);
			// System.out.println("getRefEmail() ===>>>> " +
			// getRefEmail().length);
			if (getRefName() != null && getRefName().length > 0) {
				for (int i = 0; i < getRefName().length; i++) {
					if (getRefName()[i].length() != 0) {
						pst = con.prepareStatement("INSERT INTO emp_references (ref_name, ref_company, ref_designation, ref_contact_no, ref_email_id, emp_id) "
								+ "values(?,?,?, ?,?,?)");
						pst.setString(1, getRefName()[i]);
						pst.setString(2, (getRefCompany()[i] != null && !getRefCompany()[i].equals("") && !getRefCompany()[i].equals("Other"))
								? getRefCompany()[i]
								: getRefCompanyOther()[i]);
						pst.setString(3, getRefDesignation()[i]);
						pst.setString(4, getRefContactNo()[i]);
						pst.setString(5, getRefEmail()[i]);
						pst.setInt(6, uF.parseToInt(getEmpId()));
						int x = pst.executeUpdate();
						pst.close();

						if (x > 0) {
							/**
							 * Log Details
							 * */
							String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
							String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
							String strProcessMsg = uF.showData(strProcessByName, "") + " has updated reference (" + uF.showData(getRefName()[i], "") + ") of "
									+ uF.showData(strEmpName, "") + " on " + ""
									+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
									+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
							LogDetails logDetails = new LogDetails();
							logDetails.session = session;
							logDetails.CF = CF;
							logDetails.request = request;
							logDetails.setProcessId(uF.parseToInt(getEmpId()));
							logDetails.setProcessType(L_EMPLOYEE);
							logDetails.setProcessActivity(L_UPDATE);
							logDetails.setProcessMsg(strProcessMsg);
							logDetails.setProcessStep(uF.parseToInt(getStep()));
							logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
							logDetails.insertLog(con, uF);
						}
					}
				}
			}
			// pst =
			// con.prepareStatement("INSERT INTO emp_references (ref_name, ref_company, ref_designation, ref_contact_no, ref_email_id, emp_id) "
			// +
			// "values(?,?,?, ?,?,?)");
			// pst.setString(1, getRef2Name());
			// pst.setString(2, (getRef2Company()!= null &&
			// !getRef2Company().equals("") &&
			// !getRef2Company().equals("Other")) ? getRef2Company() :
			// getRef2CompanyOther());
			// pst.setString(3, getRef2Designation());
			// pst.setString(4, getRef2ContactNo());
			// pst.setString(5, getRef2Email());
			// pst.setInt(6, getEmpId());
			// pst.execute();

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


	public void updateProbationPeriod(Connection con, UtilityFunctions uF) {
		// System.out.println("updateProbationPeriod====>>>");
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			StringBuilder sbProbationLeaves = new StringBuilder();

			for (int i = 0; getProbationLeaves() != null && i < getProbationLeaves().length; i++) {
				if (uF.parseToInt(getProbationLeaves()[i]) > 0) {
					sbProbationLeaves.append(getProbationLeaves()[i]);
					if (i < getProbationLeaves().length - 1) {
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
			if (joiningDate != null && !joiningDate.equals("")) {
				if (getProbationDuration() > 0) {
					// lastProbationEndDate =
					// uF.getFutureDate(uF.getDateFormat(joiningDate, DBDATE),
					// getProbationDuration());
					probEndDate = uF.getFutureDate(uF.getDateFormat(getEmpStartDate(), DATE_FORMAT), (getProbationDuration() - 1));

				}
			}
			pst = con.prepareStatement("UPDATE probation_policy SET leaves_types_allowed=?, probation_duration=?, notice_duration = ?,probation_end_date=?,extend_probation_duration=? "
							+ " WHERE emp_id = ?");
			pst.setString(1, sbProbationLeaves.toString());
			pst.setInt(2, getProbationDuration());
			pst.setInt(3, getNoticeDuration());
			pst.setDate(4, probEndDate);
			pst.setInt(5, 0);
			pst.setInt(6, uF.parseToInt(getEmpId()));

			// pst.setInt(5, getEmpId());

			// System.out.println("pst updateProbationPolicy=" + pst);
			int cnt = pst.executeUpdate();
			pst.close();
			if (cnt == 0) {
				insertProbationPeriod(con, uF);
			} else if (uF.parseToInt(getEmpStatus()) == 1) {
				pst = con.prepareStatement("update employee_personal_details set emp_status = ? where emp_per_id = ?");
				pst.setString(1, PROBATION);
				pst.setInt(2, uF.parseToInt(getEmpId()));
				int x = pst.executeUpdate();
				pst.close();

				if (x > 0) {
					/**
					 * Log Details
					 * */
					String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
					String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
					String strProcessMsg = uF.showData(strProcessByName, "") + " has updated probation details of " + uF.showData(strEmpName, "") + " on " + ""
							+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
							+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
					LogDetails logDetails = new LogDetails();
					logDetails.session = session;
					logDetails.CF = CF;
					logDetails.request = request;
					logDetails.setProcessId(uF.parseToInt(getEmpId()));
					logDetails.setProcessType(L_EMPLOYEE);
					logDetails.setProcessActivity(L_UPDATE);
					logDetails.setProcessMsg(strProcessMsg);
					logDetails.setProcessStep(uF.parseToInt(getStep()));
					logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
					logDetails.insertLog(con, uF);
				}
			}

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

	public void updateUser(Connection con, UtilityFunctions uF, String strEmpType) {
		
		PreparedStatement pst = null;
		
		try {
			
			if (strEmpType != null && strEmpType.equalsIgnoreCase(ADMIN)) {
				
				pst = con.prepareStatement(updateUser1);
				pst.setString(1, getUserName());
				pst.setString(2, getEmpPassword());
				pst.setInt(3, uF.parseToInt(getEmpId()));
				pst.setInt(4, uF.parseToInt(getEmpUserTypeId()));
				
				pst.execute();
				pst.close();
				
				
			} else {
				
				pst = con.prepareStatement(updateUser1E);
				pst.setString(1, getEmpPassword());
				pst.setInt(2, uF.parseToInt(getEmpId()));
				pst.setString(3, getUserName());
				
				pst.execute();
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

	public void updateEmpOfficialDetailsAdmin(Connection con, UtilityFunctions uF, StringBuilder sbServices) {
		// System.out.println("updateEmpOfficialDetailsAdmin"+uF.parseToBoolean(getRosterDependency()));

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			int nSuperWiserId = 0;
			int nHRId = 0;
			String joiningDate = "";
			pst = con.prepareStatement("select supervisor_emp_id,emp_hr,joining_date from employee_personal_details epd, employee_official_details eod WHERE eod.emp_id = epd.emp_per_id and eod.emp_id=?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				nSuperWiserId = rs.getInt("supervisor_emp_id");
				nHRId = rs.getInt("emp_hr");
				joiningDate = rs.getString("joining_date");
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("UPDATE employee_official_details SET depart_id=?,supervisor_emp_id=?,hod_emp_id=?,service_id=?,available_days=?,"
					+ "wlocation_id=?,is_roster=?,is_attendance=?,emptype=?,first_aid_allowance=?,grade_id=?,paycycle_duration=?,payment_mode=?,org_id=?,"
					+ "emp_hr=?,biometrix_id=?,is_form16=?,is_form16_a=?,corporate_mobile_no=?,corporate_desk=?,emp_contractor=?,is_hod=?,is_cxo=?,slab_type = ? "
					+ "WHERE emp_id=?");
			pst.setInt(1, uF.parseToInt(getDepartment()));
			pst.setInt(2, uF.parseToInt(getSupervisor()));
			pst.setInt(3, uF.parseToInt(getHod()));
			pst.setString(4, sbServices.toString());
			pst.setString(5, "");
			pst.setInt(6, uF.parseToInt(getwLocation()));
			pst.setBoolean(7, uF.parseToBoolean(getRosterDependency()));
			pst.setBoolean(8, uF.parseToBoolean(getAttendanceDependency()));
			pst.setString(9, getEmpType());
			pst.setBoolean(10, getIsFirstAidAllowance());
			pst.setInt(11, uF.parseToInt(getEmpGrade()));
			pst.setString(12, getStrPaycycleDuration());
			pst.setInt(13, uF.parseToInt(getEmpPaymentMode()));
			pst.setInt(14, uF.parseToInt(getOrgId()));
			pst.setInt(15, uF.parseToInt(getHR()));
			pst.setInt(16, uF.parseToInt(getBioId()));
			pst.setBoolean(17, uF.parseToBoolean(getIsForm16()));
			pst.setBoolean(18, uF.parseToBoolean(getIsForm16A()));
			pst.setString(19, getEmpCorporateMobileNo());
			pst.setString(20, getEmpCorporateDesk());
			pst.setInt(21, uF.parseToInt(getEmpContractor()));
			pst.setBoolean(22, (uF.parseToInt(getStrCXOHOD()) == 2) ? true : false);
			pst.setBoolean(23, (uF.parseToInt(getStrCXOHOD()) == 1) ? true : false);
			pst.setInt(24, uF.parseToInt(getSlabType()));
			pst.setInt(25, uF.parseToInt(getEmpId()));
			System.out.println("update pst ===>"+pst);

			int cnt = pst.executeUpdate();
			pst.close();

			// -------------------------------- Clock On/Off Access Control
			// Start ----------------------------------------
			pst = con.prepareStatement("select * from emp_clock_on_off_access where emp_id=?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			boolean accessFlag = false;
			while (rs.next()) {
				accessFlag = true;
			}
			rs.close();
			pst.close();

			if (!accessFlag) {
				pst = con.prepareStatement("insert into emp_clock_on_off_access (emp_id,is_web_access,is_mobile_access,is_biomatric_access) values(?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.setBoolean(2, true);
				pst.setBoolean(3, true);
				pst.setBoolean(4, true);
				pst.executeUpdate();
				pst.close();
			}
			// -------------------------------- Clock On/Off Access Control End
			// ----------------------------------------

			if (uF.parseToInt(getStrCXOHOD()) == 2) {
				Map<String, String> hmUserTypeID = CF.getUserTypeIdMap(con);
				pst = con.prepareStatement("UPDATE user_details SET usertype_id=? WHERE emp_id=?");
				pst.setInt(1, uF.parseToInt(hmUserTypeID.get(HOD)));
				pst.setInt(2, uF.parseToInt(getEmpId()));
				pst.executeUpdate();
				pst.close();
			}

			if (uF.parseToInt(getStrCXOHOD()) == 1) {
				StringBuilder sbwLocId = null;
				for (int i = 0; locationCXO != null && i < locationCXO.length; i++) {
					if (uF.parseToInt(locationCXO[i]) > 0) {
						if (sbwLocId == null) {
							sbwLocId = new StringBuilder();
							sbwLocId.append("," + locationCXO[i] + ",");
						} else {
							sbwLocId.append(locationCXO[i] + ",");
						}
					}
				}
				if (uF.parseToInt(getOrgId()) > 0 && (locationCXO == null || locationCXO.length == 0)) {
					pst = con.prepareStatement("select wlocation_id from work_location_info where org_id in(" + getOrgId() + ")");
					rs = pst.executeQuery();
					while (rs.next()) {
						if (rs.getInt("wlocation_id") > 0) {
							if (sbwLocId == null) {
								sbwLocId = new StringBuilder();
								sbwLocId.append("," + rs.getInt("wlocation_id") + ",");
							} else {
								sbwLocId.append(rs.getInt("wlocation_id") + ",");
							}
						}
					}
					rs.close();
					pst.close();
				}
				if (sbwLocId == null)
					sbwLocId = new StringBuilder();

				Map<String, String> hmUserTypeID = CF.getUserTypeIdMap(con);
				pst = con.prepareStatement("UPDATE user_details SET org_id_access=?,wlocation_id_access=?,usertype_id=? WHERE emp_id=?");
				pst.setString(1, "," + getOrgId() + ",");
				pst.setString(2, sbwLocId.toString());
				pst.setInt(3, uF.parseToInt(hmUserTypeID.get(CEO)));
				pst.setInt(4, uF.parseToInt(getEmpId()));
				pst.executeUpdate();
				pst.close();
			}

			if (uF.parseToInt(getStrCXOHOD()) == 0) {
				Map<String, String> hmUserTypeID = CF.getUserTypeIdMap(con);
				pst = con
						.prepareStatement("update user_details set usertype_id=?, org_id_access=?, wlocation_id_access=? where emp_id=? and (usertype_id=? or usertype_id=?)");
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
			while (rs.next()) {
				userTypeId = rs.getInt("usertype_id");
			}
			rs.close();
			pst.close();

			if (userTypeId == uF.parseToInt(hmUserTypeId.get(EMPLOYEE))) {
				pst = con.prepareStatement("UPDATE user_details SET usertype_id=? WHERE emp_id=?");
				pst.setInt(1, uF.parseToInt(hmUserTypeId.get(MANAGER)));
				pst.setInt(2, uF.parseToInt(getSupervisor()));
				pst.executeUpdate();
				pst.close();
			}

			if (cnt == 0) {
				insertEmpOfficialDetails(con, uF, sbServices);
			}

			if ((nSuperWiserId == 0 || nSuperWiserId != uF.parseToInt(getSupervisor())) && uF.parseToInt(getSupervisor()) > 0) {

				String strDomain = request.getServerName().split("\\.")[0];
				String alertData = "<div style=\"float: left;\"> <b>" + CF.getEmpNameMapByEmpId(con, getEmpId() + "")
						+ "</b> has been added in your team by <b> " + CF.getEmpNameMapByEmpId(con, strSessionEmpId) + "</b>.</div>";
				String alertAction = "TeamStructure.action?pType=WR";
				UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(getSupervisor());
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
		//===start parvez date: 06-09-2022===		
//				userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
				userAlerts.setCurrUserTypeID(hmUserTypeId.get(MANAGER));
		//===end parvez date: 06-09-2022===		
				userAlerts.setStatus(INSERT_WR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();

				// UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				// userAlerts.setStrDomain(strDomain);
				// userAlerts.setStrEmpId(getSupervisor());
				// userAlerts.set_type(ADD_MYTEAM_MEMBER_ALERT);
				// userAlerts.setStatus(INSERT_ALERT);
				// Thread t = new Thread(userAlerts);
				// t.run();
			}

			if (nHRId == 0 && uF.parseToInt(getHR()) > 0) {
				String strDomain = request.getServerName().split("\\.")[0];
				Map<String, String> hmEmpUsertypeId = CF.getEmployeeIdUserTypeIdMap(con);
				String strJDate = "";
				if (uF.getCurrentDate(CF.getStrTimeZone()).equals(uF.getDateFormat(joiningDate, DBDATE))) {
					strJDate = "today";
				} else {
					strJDate = uF.getDateFormat(joiningDate, DBDATE, CF.getStrReportDateFormat());
				}
				String alertData = "<div style=\"float: left;\"> <b>" + CF.getEmpNameMapByEmpId(con, getEmpId() + "") + "</b> New Joinee " + strJDate
						+ ".</div>";
				String alertAction = "People.action?callFrom=ADDEMP";
				UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(getHR());
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
				userAlerts.setCurrUserTypeID(hmEmpUsertypeId.get(getHR()));
				userAlerts.setStatus(INSERT_WR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();

				// UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				// userAlerts.setStrDomain(strDomain);
				// userAlerts.setStrEmpId(getHR());
				// userAlerts.set_type(NEW_JOINEES_ALERT);
				// userAlerts.setStatus(INSERT_ALERT);
				// Thread t = new Thread(userAlerts);
				// t.run();

				// userAlerts = new UserAlerts(con, uF, CF, request);
				// userAlerts.setStrDomain(strDomain);
				// userAlerts.setStrEmpId(getHR());
				// userAlerts.set_type(NEW_JOINEE_PENDING_ALERT);
				// userAlerts.setStatus(DELETE_ALERT);
				// Thread t1 = new Thread(userAlerts);
				// t1.run();
			}

			// if(uF.parseToInt(getHR()) != uF.parseToInt(strSessionEmpId)) {
			// String strDomain = request.getServerName().split("\\.")[0];
			// UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
			// userAlerts.setStrDomain(strDomain);
			// userAlerts.setStrEmpId(strSessionEmpId);
			// userAlerts.set_type(NEW_JOINEES_ALERT);
			// userAlerts.setStatus(INSERT_ALERT);
			// Thread t = new Thread(userAlerts);
			// t.run();
			//
			// userAlerts = new UserAlerts(con, uF, CF, request);
			// userAlerts.setStrDomain(strDomain);
			// userAlerts.setStrEmpId(strSessionEmpId);
			// userAlerts.set_type(NEW_JOINEE_PENDING_ALERT);
			// userAlerts.setStatus(DELETE_ALERT);
			// Thread t1 = new Thread(userAlerts);
			// t1.run();
			// }

			/**
			 * Log Details
			 * */
			String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
			String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
			String strProcessMsg = uF.showData(strProcessByName, "") + " has updated employee official details of " + uF.showData(strEmpName, "") + " on " + ""
					+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
					+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
			LogDetails logDetails = new LogDetails();
			logDetails.session = session;
			logDetails.CF = CF;
			logDetails.request = request;
			logDetails.setProcessId(uF.parseToInt(getEmpId()));
			logDetails.setProcessType(L_EMPLOYEE);
			logDetails.setProcessActivity(L_UPDATE);
			logDetails.setProcessMsg(strProcessMsg);
			logDetails.setProcessStep(uF.parseToInt(getStep()));
			logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
			logDetails.insertLog(con, uF);

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
	

	public void updateEmpPersonalDetails(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;

		try {

			if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))) {

		//===start parvez date: 30-07-2022===		
				pst = con.prepareStatement("UPDATE employee_personal_details SET emp_fname=?, emp_mname=?, emp_lname=?, emp_email=?, emp_address1=?, "
						+ "emp_address2=?, emp_city_id=?, emp_state_id=?, emp_country_id=?, emp_pincode=?, emp_address1_tmp=?, emp_address2_tmp=?, "
						+ "emp_city_id_tmp=?, emp_state_id_tmp=?, emp_country_id_tmp=?, emp_pincode_tmp=?, emp_contactno=?, emp_pan_no = ?, "
						+ "emp_pf_no = ?, emp_gpf_no = ?, emp_gender=?, emp_date_of_birth=?, emp_contactno_mob=?, emergency_contact_name=?, "
						+ "emergency_contact_no=?, passport_no=?, passport_expiry_date=?, blood_group=?, marital_status=?, emp_date_of_marriage=?, "
						+ "salutation=?, doctor_name=?, doctor_contact_no=?, uid_no=?, uan_no=?, emp_esic_no=?,is_medical_professional=?,"
						+ " emp_kmc_no=?,emp_knc_no=?,renewal_date=?,emp_mrd_no=?,pf_start_date=?,emergency_contact_relation=? WHERE emp_per_id=?");
				pst.setString(1, getEmpFname());
				pst.setString(2, getEmpMname());
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
				pst.setString(18, getEmpPanNo());
				pst.setString(19, getEmpPFNo());
				pst.setString(20, getEmpGPFNo());
				pst.setString(21, getEmpGender());
				pst.setDate(22, uF.getDateFormat(getEmpDateOfBirth(), DATE_FORMAT));

				pst.setString(23, getEmpMobileNo());
				pst.setString(24, getEmpEmergencyContactName());
				pst.setString(25, getEmpEmergencyContactNo());
				pst.setString(26, getEmpPassportNo());
				pst.setDate(27, uF.getDateFormat(getEmpPassportExpiryDate(), DATE_FORMAT));
				pst.setString(28, getEmpBloodGroup());
				pst.setString(29, getEmpMaritalStatus());
				pst.setDate(30, uF.getDateFormat(getEmpDateOfMarriage(), DATE_FORMAT));
				pst.setString(31, getSalutation());
				pst.setString(32, getEmpDoctorName());
				pst.setString(33, getEmpDoctorNo());
				pst.setString(34, getEmpUIDNo());
				pst.setString(35, getEmpUANNo());
				pst.setString(36, getEmpESICNo());
				pst.setBoolean(37, isMedicalProfessional());
				pst.setString(38, getStrKmcNo());
				pst.setString(39, getStrKncNo());
				pst.setDate(40, uF.getDateFormat(getStrRenewalDate(), DATE_FORMAT));
				pst.setString(41, getEmpMRDNo());
				pst.setDate(42, uF.getDateFormat(getEmpPFStartDate(), DATE_FORMAT));
				pst.setString(43, getEmpEmergencyContactRelation());
				pst.setInt(44, uF.parseToInt(getEmpId()));
				// log.debug("pst updateEmployeeP==>"+pst);
				pst.execute();
				pst.close();
		//===end parvez date: 30-07-2022===		

			} else {

			//===start parvez date: 30-07-2022===	
				pst = con.prepareStatement("UPDATE employee_personal_details SET emp_fname=?,emp_mname=?, emp_lname=?, emp_email=?, emp_address1=?, "
						+ "emp_address2=?, emp_city_id=?, emp_state_id=?, emp_country_id=?, emp_pincode=?, emp_address1_tmp=?, emp_address2_tmp=?, "
						+ "emp_city_id_tmp=?, emp_state_id_tmp=?, emp_country_id_tmp=?, emp_pincode_tmp=?, emp_contactno=?, emp_pan_no = ?, "
						+ "emp_pf_no = ?, emp_gpf_no = ?, emp_gender=?, emp_date_of_birth=?, emp_contactno_mob=?, emergency_contact_name=?, "
						+ "emergency_contact_no=?, passport_no=?, passport_expiry_date=?, blood_group=?, marital_status=?, emp_date_of_marriage=?, "
						+ "salutation=?, doctor_name=?, doctor_contact_no=?, uid_no=?, uan_no=?, emp_esic_no=?,is_medical_professional=?,"
						+ " emp_kmc_no=?,emp_knc_no=?,renewal_date=?,emp_mrd_no=?,pf_start_date=?,emergency_contact_relation=? WHERE emp_per_id=?");
				pst.setString(1, getEmpFname());
				pst.setString(2, getEmpMname());
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
				pst.setString(18, getEmpPanNo());
				pst.setString(19, getEmpPFNo());
				pst.setString(20, getEmpGPFNo());
				pst.setString(21, getEmpGender());
				pst.setDate(22, uF.getDateFormat(getEmpDateOfBirth(), DATE_FORMAT));
				pst.setString(23, uF.showData(getEmpMobileNo(), ""));
				pst.setString(24, getEmpEmergencyContactName());
				pst.setString(25, getEmpEmergencyContactNo());
				pst.setString(26, getEmpPassportNo());
				pst.setDate(27, uF.getDateFormat(getEmpPassportExpiryDate(), DATE_FORMAT));
				pst.setString(28, getEmpBloodGroup());
				pst.setString(29, getEmpMaritalStatus());
				pst.setDate(30, uF.getDateFormat(getEmpDateOfMarriage(), DATE_FORMAT));
				pst.setString(31, getSalutation());
				pst.setString(32, getEmpDoctorName());
				pst.setString(33, getEmpDoctorNo());
				pst.setString(34, getEmpUIDNo());
				pst.setString(35, getEmpUANNo());
				pst.setString(36, getEmpESICNo());
				pst.setBoolean(37, isMedicalProfessional());
				pst.setString(38, getStrKmcNo());
				pst.setString(39, getStrKncNo());
				pst.setDate(40, uF.getDateFormat(getStrRenewalDate(), DATE_FORMAT));
				pst.setString(41, getEmpMRDNo());
				pst.setDate(42, uF.getDateFormat(getEmpPFStartDate(), DATE_FORMAT));
				pst.setString(43, getEmpEmergencyContactRelation());
				pst.setInt(44, uF.parseToInt(getEmpId()));
				pst.execute();
				pst.close();
			//===end parvez date: 30-07-2022===	
			}

			/**
			 * Log Details
			 * */
			String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
			String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
			String strProcessMsg = uF.showData(strProcessByName, "") + " has updated employee personal details of " + uF.showData(strEmpName, "") + " on " + ""
					+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
					+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
			LogDetails logDetails = new LogDetails();
			logDetails.session = session;
			logDetails.CF = CF;
			logDetails.request = request;
			logDetails.setProcessId(uF.parseToInt(getEmpId()));
			logDetails.setProcessType(L_EMPLOYEE);
			logDetails.setProcessActivity(L_UPDATE);
			logDetails.setProcessMsg(strProcessMsg);
			logDetails.setProcessStep(uF.parseToInt(getStep()));
			logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
			logDetails.insertLog(con, uF);

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

	public void updateEmpPrevEmploment(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
//		System.out.println("updateEmpPrevEmploment getIsTdsDetails() ===>> " + getIsTdsDetails());
		try {

			pst = con.prepareStatement("DELETE FROM emp_prev_employment WHERE emp_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			// log.debug("pst=>"+pst);
			pst.execute();
			pst.close();

			if (getPrevCompanyName() != null && getPrevCompanyName().length > 0) {
				for (int i = 0; i < getPrevCompanyName().length; i++) {

					if (getPrevCompanyName()[i].length() != 0) {

					//===start parvez date: 08-08-2022===	
//						pst = con.prepareStatement("INSERT INTO emp_prev_employment(company_name, company_location, company_city, company_state, "
//								+ "company_country, company_contact_no, reporting_to, from_date, to_date, designation, responsibilities, skills, emp_id, "
//								+ "report_manager_ph_no, hr_manager, hr_manager_ph_no)" + "VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
						pst = con.prepareStatement("INSERT INTO emp_prev_employment(company_name, company_location, company_city, company_state, "
								+ "company_country, company_contact_no, reporting_to, from_date, to_date, designation, responsibilities, skills, emp_id, "
								+ "report_manager_ph_no, hr_manager, hr_manager_ph_no, emp_esic_no, uan_no)" + "VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
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
						pst.setInt(13, uF.parseToInt(getEmpId()));
						pst.setString(14, getPrevCompanyReportManagerPhNo()[i]);
						pst.setString(15, getPrevCompanyHRManager()[i]);
						pst.setString(16, getPrevCompanyHRManagerPhNo()[i]);
				//===start parvez date: 08-08-2022===		
						pst.setString(17, getPrevCompanyESICNo()[i]);
						pst.setString(18, getPrevCompanyUANNo()[i]);
				//===end parvez date: 08-08-2022===		
						// log.debug("pst=>"+pst);
						int x = pst.executeUpdate();
						pst.close();
						if (x > 0) {
							/**
							 * Log Details
							 * */
							String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
							String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
							String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted previous employment ("
									+ uF.showData(getPrevCompanyName()[i], "") + ") of " + uF.showData(strEmpName, "") + " on " + ""
									+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
									+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
							LogDetails logDetails = new LogDetails();
							logDetails.session = session;
							logDetails.CF = CF;
							logDetails.request = request;
							logDetails.setProcessId(uF.parseToInt(getEmpId()));
							logDetails.setProcessType(L_EMPLOYEE);
							logDetails.setProcessActivity(L_ADD);
							logDetails.setProcessMsg(strProcessMsg);
							logDetails.setProcessStep(uF.parseToInt(getStep()));
							logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
							logDetails.insertLog(con, uF);
						}

					}
				}
			}

			if (getPrevEmpFYear() != null && !getPrevEmpFYear().equals("") && getPrevTotalEarning() != null && !getPrevTotalEarning().equals("")
					&& getPrevTotalDeduction() != null && !getPrevTotalDeduction().equals("")) { 
				String[] strPayCycleDates = null;
				String strFinancialYearStart = null;
				String strFinancialYearEnd = null;

				if (getPrevEmpFYear() != null) {

					strPayCycleDates = getPrevEmpFYear().split("-");
					strFinancialYearStart = strPayCycleDates[0];
					strFinancialYearEnd = strPayCycleDates[1];

				} else {
					strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
					setPrevEmpFYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);

					strFinancialYearStart = strPayCycleDates[0];
					strFinancialYearEnd = strPayCycleDates[1];
				}

//				System.out.println("getIsTdsDetails() ===>> " + getIsTdsDetails());
				if(uF.parseToBoolean(getIsTdsDetails())) {
					String filename = null;
					if(getPrevFileEarnDeduct()!=null) {
						if (CF.getStrDocSaveLocation() != null) {
							filename = uF.uploadFile(request, CF.getStrDocSaveLocation(), getPrevFileEarnDeduct(), getPrevFileEarnDeductFileName(), CF.getIsRemoteLocation(), CF);
						} else {
							filename = uF.uploadFile(request, DOCUMENT_LOCATION, getPrevFileEarnDeduct(), getPrevFileEarnDeductFileName(), CF.getIsRemoteLocation(), CF);
						}
					}
		
			//===start parvez date: 31-03-2022===		
					
//					pst = con.prepareStatement("update prev_earn_deduct_details set gross_amount=?,tds_amount=?,financial_start=?,financial_end=?,"
//							+ "document_name=?,added_by=?,added_on=? where emp_id=? and prev_earn_deduct_id=?");
					
					pst = con.prepareStatement("update prev_earn_deduct_details set gross_amount=?,tds_amount=?,financial_start=?,financial_end=?,"
						+ "document_name=?,added_by=?,added_on=?,org_pan_no=?,org_tan_no=? where emp_id=? and prev_earn_deduct_id=?");
			//===end parvez date: 31-03-2022===		
					pst.setDouble(1, uF.parseToDouble(getPrevTotalEarning()));
					pst.setDouble(2, uF.parseToDouble(getPrevTotalDeduction()));
					pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setString(5, filename);
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE + DBTIME));
			//===start parvez date: 31-03-2022===		
//					pst.setInt(8, uF.parseToInt(getEmpId()));
//					pst.setInt(9, uF.parseToInt(getPrevEarnDeductId()));
					pst.setString(8, getPrevPANNumber());
					pst.setString(9, getPrevTANNumber());
					pst.setInt(10, uF.parseToInt(getEmpId()));
					pst.setInt(11, uF.parseToInt(getPrevEarnDeductId()));
			//===end parvez date: 31-03-2022===		
					int x = pst.executeUpdate();
					pst.close();
	
					if (x == 0) {
				//===start parvez date: 31-03-2022===		
						
//						pst = con.prepareStatement("insert into prev_earn_deduct_details(emp_id,gross_amount,tds_amount,financial_start,financial_end,"
//							+ "document_name,added_by,added_on)values(?,?,?,?, ?,?,?,?)");
						
						pst = con.prepareStatement("insert into prev_earn_deduct_details(emp_id,gross_amount,tds_amount,financial_start,financial_end,"
								+ "document_name,added_by,added_on,org_pan_no,org_tan_no)values(?,?,?,?, ?,?,?,?, ?,?)");
						
				//===end parvez date: 31-03-2022===		
						pst.setInt(1, uF.parseToInt(getEmpId()));
						pst.setDouble(2, uF.parseToDouble(getPrevTotalEarning()));
						pst.setDouble(3, uF.parseToDouble(getPrevTotalDeduction()));
						pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setString(6, filename);
						pst.setInt(7, uF.parseToInt(strSessionEmpId));
						pst.setTimestamp(8, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE + DBTIME));
				//===start parvez date: 31-03-2022===		
						pst.setString(9, getPrevPANNumber());
						pst.setString(10, getPrevTANNumber());
				//===end parvez date: 31-03-2022===		
						pst.execute();
						pst.close();
					}
				} else {
					pst = con.prepareStatement("delete from	prev_earn_deduct_details where emp_id=? and prev_earn_deduct_id=?");
					pst.setInt(1, uF.parseToInt(getEmpId()));
					pst.setInt(2, uF.parseToInt(getPrevEarnDeductId()));
					int x = pst.executeUpdate();
					pst.close();
				}

			}

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

	public void updateEmpFamilyMembers(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		int updateCnt = 0;
		try {

			if (getMotherName() != null && getMotherName().length() > 0) {
				pst = con.prepareStatement("UPDATE emp_family_members SET member_name = ?, member_dob = ?, member_education = ?, "
						+ "member_occupation = ?, member_contact_no = ?, member_email_id = ?, member_gender = ?,member_marital=? , mrd_no = ?"
						+ "WHERE emp_id = ? and member_type = ?");

				pst.setString(1, getMotherName());
				pst.setDate(2, uF.getDateFormat(getMotherDob(), DATE_FORMAT));
				pst.setString(3, getMotherEducation());
				pst.setString(4, getMotherOccupation());
				pst.setString(5, getMotherContactNumber());
				pst.setString(6, getMotherEmailId());
				pst.setString(7, "F");
				pst.setString(8, "M");
				pst.setString(9, getMotherMRDNo());
				pst.setInt(10, uF.parseToInt(getEmpId()));
				pst.setString(11, MOTHER);
				// log.debug("pst=>"+pst);
				updateCnt = pst.executeUpdate();
				pst.close();

			}
			if (updateCnt == 0) {
				if (getMotherName() != null && getMotherName().length() > 0) {
					pst = con.prepareStatement("INSERT INTO emp_family_members(member_type, member_name, member_dob, member_education, "
							+ "member_occupation, member_contact_no, member_email_id, member_gender,member_marital, emp_id,mrd_no)"
							+ "VALUES (?,?,?,?,?,?,?,?,?,?,?)");

					pst.setString(1, MOTHER);
					pst.setString(2, getMotherName());
					// log.debug(getMotherDob()+"getMotherDob()");
					pst.setDate(3, uF.getDateFormat(getMotherDob(), DATE_FORMAT));
					pst.setString(4, getMotherEducation());
					pst.setString(5, getMotherOccupation());
					pst.setString(6, getMotherContactNumber());
					pst.setString(7, getMotherEmailId());
					pst.setString(8, "F");
					pst.setString(9, "M");
					pst.setInt(10, uF.parseToInt(getEmpId()));
					pst.setString(11, getMotherMRDNo());
					// log.debug("pst=>"+pst);
					int x = pst.executeUpdate();
					pst.close();

					if (x > 0) {
						/**
						 * Log Details
						 * */
						String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
						String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
						String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted mother name (" + uF.showData(getMotherName(), "") + ") of "
								+ uF.showData(strEmpName, "") + " on " + ""
								+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
								+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
						LogDetails logDetails = new LogDetails();
						logDetails.session = session;
						logDetails.CF = CF;
						logDetails.request = request;
						logDetails.setProcessId(uF.parseToInt(getEmpId()));
						logDetails.setProcessType(L_EMPLOYEE);
						logDetails.setProcessActivity(L_ADD);
						logDetails.setProcessMsg(strProcessMsg);
						logDetails.setProcessStep(uF.parseToInt(getStep()));
						logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
						logDetails.insertLog(con, uF);
					}

				}
			} else {

				/**
				 * Log Details
				 * */
				String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
				String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
				String strProcessMsg = uF.showData(strProcessByName, "") + " has updated mother name (" + uF.showData(getMotherName(), "") + ") of "
						+ uF.showData(strEmpName, "") + " on " + ""
						+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
						+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
				LogDetails logDetails = new LogDetails();
				logDetails.session = session;
				logDetails.CF = CF;
				logDetails.request = request;
				logDetails.setProcessId(uF.parseToInt(getEmpId()));
				logDetails.setProcessType(L_EMPLOYEE);
				logDetails.setProcessActivity(L_UPDATE);
				logDetails.setProcessMsg(strProcessMsg);
				logDetails.setProcessStep(uF.parseToInt(getStep()));
				logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
				logDetails.insertLog(con, uF);
			}

			if (getFatherName() != null && getFatherName().length() > 0) {
				pst = con.prepareStatement("UPDATE emp_family_members SET member_name = ?, member_dob = ?, member_education = ?, "
						+ "member_occupation = ?, member_contact_no = ?, member_email_id = ?, member_gender = ?,member_marital=?,mrd_no=? "
						+ "WHERE emp_id = ? and member_type = ?");

				pst.setString(1, getFatherName());
				pst.setDate(2, uF.getDateFormat(getFatherDob(), DATE_FORMAT));
				pst.setString(3, getFatherEducation());
				pst.setString(4, getFatherOccupation());
				pst.setString(5, getFatherContactNumber());
				pst.setString(6, getFatherEmailId());
				pst.setString(7, "M");
				pst.setString(8, "M");
				pst.setString(9, getFatherMRDNo());
				pst.setInt(10, uF.parseToInt(getEmpId()));
				pst.setString(11, FATHER);
				// log.debug("pst=>"+pst);
				updateCnt = pst.executeUpdate();
				pst.close();

			}
			if (updateCnt == 0) {

				if (getFatherName() != null && getFatherName().length() > 0) {
					pst = con.prepareStatement("INSERT INTO emp_family_members(member_type, member_name, member_dob, member_education, "
							+ "member_occupation, member_contact_no, member_email_id, member_gender,member_marital, emp_id,mrd_no)"
							+ "VALUES (?,?,?,?,?,?,?,?,?,?,?)");

					pst.setString(1, FATHER);
					pst.setString(2, getFatherName());
					pst.setDate(3, uF.getDateFormat(getFatherDob(), DATE_FORMAT));
					pst.setString(4, getFatherEducation());
					pst.setString(5, getFatherOccupation());
					pst.setString(6, getFatherContactNumber());
					pst.setString(7, getFatherEmailId());
					pst.setString(8, "M");
					pst.setString(9, "M");
					pst.setInt(10, uF.parseToInt(getEmpId()));
					pst.setString(11, getFatherMRDNo());
					int x = pst.executeUpdate();
					pst.close();

					if (x > 0) {
						/**
						 * Log Details
						 * */
						String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
						String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
						String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted father name (" + uF.showData(getFatherName(), "") + ") of "
								+ uF.showData(strEmpName, "") + " on " + ""
								+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
								+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
						LogDetails logDetails = new LogDetails();
						logDetails.session = session;
						logDetails.CF = CF;
						logDetails.request = request;
						logDetails.setProcessId(uF.parseToInt(getEmpId()));
						logDetails.setProcessType(L_EMPLOYEE);
						logDetails.setProcessActivity(L_ADD);
						logDetails.setProcessMsg(strProcessMsg);
						logDetails.setProcessStep(uF.parseToInt(getStep()));
						logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
						logDetails.insertLog(con, uF);
					}

				}

			} else {
				/**
				 * Log Details
				 * */
				String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
				String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
				String strProcessMsg = uF.showData(strProcessByName, "") + " has updated mother name (" + uF.showData(getFatherName(), "") + ") of "
						+ uF.showData(strEmpName, "") + " on " + ""
						+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
						+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
				LogDetails logDetails = new LogDetails();
				logDetails.session = session;
				logDetails.CF = CF;
				logDetails.request = request;
				logDetails.setProcessId(uF.parseToInt(getEmpId()));
				logDetails.setProcessType(L_EMPLOYEE);
				logDetails.setProcessActivity(L_UPDATE);
				logDetails.setProcessMsg(strProcessMsg);
				logDetails.setProcessStep(uF.parseToInt(getStep()));
				logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
				logDetails.insertLog(con, uF);
			}

			if (getSpouseName() != null && getSpouseName().length() > 0) {
				pst = con.prepareStatement("UPDATE emp_family_members SET member_name = ?, member_dob = ?, member_education = ?, "
						+ "member_occupation = ?, member_contact_no = ?, member_email_id = ?, member_gender = ?,member_marital=?,mrd_no=? "
						+ "WHERE emp_id = ? and member_type = ?");

				pst.setString(1, getSpouseName());
				pst.setDate(2, uF.getDateFormat(getSpouseDob(), DATE_FORMAT));
				pst.setString(3, getSpouseEducation());
				pst.setString(4, getSpouseOccupation());
				pst.setString(5, getSpouseContactNumber());
				pst.setString(6, getSpouseEmailId());
				pst.setString(7, getSpouseGender());
				// System.out.println("getSpouseMaritalStatus()==>"+getSpouseMaritalStatus());
				pst.setString(8, "M"); //getSpouseMaritalStatus()
				pst.setString(9, getSpouseMRDNo());
				pst.setInt(10, uF.parseToInt(getEmpId()));
				pst.setString(11, SPOUSE);

				// log.debug("pst=>"+pst);
				updateCnt = pst.executeUpdate();
				pst.close();

			}
			if (updateCnt == 0) {

				if (getSpouseName() != null && getSpouseName().length() > 0) {
					pst = con.prepareStatement("INSERT INTO emp_family_members(member_type, member_name, member_dob, member_education, "
							+ "member_occupation, member_contact_no, member_email_id, member_gender,member_marital, emp_id,mrd_no)"
							+ "VALUES (?,?,?,?,?,?,?,?,?,?,?)");

					pst.setString(1, SPOUSE);
					pst.setString(2, getSpouseName());
					pst.setDate(3, uF.getDateFormat(getSpouseDob(), DATE_FORMAT));
					pst.setString(4, getSpouseEducation());
					pst.setString(5, getSpouseOccupation());
					pst.setString(6, getSpouseContactNumber());
					pst.setString(7, getSpouseEmailId());
					pst.setString(8, getSpouseGender());
					pst.setString(9, "M");
					pst.setInt(10, uF.parseToInt(getEmpId()));
					pst.setString(11, getSpouseMRDNo());
					int x = pst.executeUpdate();
					pst.close();

					if (x > 0) {
						/**
						 * Log Details
						 * */
						String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
						String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
						String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted spouse name (" + uF.showData(getSpouseName(), "") + ") of "
								+ uF.showData(strEmpName, "") + " on " + ""
								+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
								+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
						LogDetails logDetails = new LogDetails();
						logDetails.session = session;
						logDetails.CF = CF;
						logDetails.request = request;
						logDetails.setProcessId(uF.parseToInt(getEmpId()));
						logDetails.setProcessType(L_EMPLOYEE);
						logDetails.setProcessActivity(L_ADD);
						logDetails.setProcessMsg(strProcessMsg);
						logDetails.setProcessStep(uF.parseToInt(getStep()));
						logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
						logDetails.insertLog(con, uF);
					}

				}
			} else {

				/**
				 * Log Details
				 * */
				String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
				String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
				String strProcessMsg = uF.showData(strProcessByName, "") + " has updated spouse name (" + uF.showData(getSpouseName(), "") + ") of "
						+ uF.showData(strEmpName, "") + " on " + ""
						+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
						+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
				LogDetails logDetails = new LogDetails();
				logDetails.session = session;
				logDetails.CF = CF;
				logDetails.request = request;
				logDetails.setProcessId(uF.parseToInt(getEmpId()));
				logDetails.setProcessType(L_EMPLOYEE);
				logDetails.setProcessActivity(L_UPDATE);
				logDetails.setProcessMsg(strProcessMsg);
				logDetails.setProcessStep(uF.parseToInt(getStep()));
				logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
				logDetails.insertLog(con, uF);
			}

			if (getMemberName().length != 0) {

				pst = con.prepareStatement("DELETE FROM emp_family_members WHERE emp_id = ? and member_type = ?");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.setString(2, SIBLING);
				// log.debug("pst=>"+pst);
				pst.execute();
				pst.close();

				for (int i = 0; i < getMemberName().length; i++) {

					if (getMemberName()[i] != null && getMemberName()[i].length() > 0) {
						pst = con.prepareStatement("INSERT INTO emp_family_members(member_type, member_name, member_dob, member_education, "
								+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id,member_marital,mrd_no)"
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?)");

						pst.setString(1, SIBLING);
						pst.setString(2, getMemberName()[i]);
						pst.setDate(3, uF.getDateFormat(getMemberDob()[i], DATE_FORMAT));
						pst.setString(4, getMemberEducation()[i]);
						pst.setString(5, getMemberOccupation()[i]);
						pst.setString(6, getMemberContactNumber()[i]);
						pst.setString(7, getMemberEmailId()[i]);
						pst.setString(8, getMemberGender()[i]);
						pst.setInt(9, uF.parseToInt(getEmpId()));
						if (getSiblingMaritalStatus() != null && getSiblingMaritalStatus().length > 0 && getSiblingMaritalStatus()[i] != null
								&& getSiblingMaritalStatus()[i] != "0") {
							pst.setString(10, getSiblingMaritalStatus()[i]);
						} else {
							pst.setString(10, "");
						}
						pst.setString(11, getSiblingsMRDNo()[i]);
						int x = pst.executeUpdate();
						pst.close();

						if (x > 0) {
							/**
							 * Log Details
							 * */
							String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
							String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
							String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted sibling name (" + uF.showData(getMemberName()[i], "")
									+ ") of " + uF.showData(strEmpName, "") + " on " + ""
									+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
									+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
							LogDetails logDetails = new LogDetails();
							logDetails.session = session;
							logDetails.CF = CF;
							logDetails.request = request;
							logDetails.setProcessId(uF.parseToInt(getEmpId()));
							logDetails.setProcessType(L_EMPLOYEE);
							logDetails.setProcessActivity(L_ADD);
							logDetails.setProcessMsg(strProcessMsg);
							logDetails.setProcessStep(uF.parseToInt(getStep()));
							logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
							logDetails.insertLog(con, uF);
						}

					}
				}

			}

			if (getChildName().length != 0) {

				pst = con.prepareStatement("DELETE FROM emp_family_members WHERE emp_id = ? and member_type = ?");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.setString(2, CHILD);
				// log.debug("pst=>"+pst);
				pst.execute();
				pst.close();

				for (int i = 0; i < getChildName().length; i++) {

					if (getChildName()[i] != null && getChildName()[i].length() > 0) {

						pst = con.prepareStatement("INSERT INTO emp_family_members(member_type, member_name, member_dob, member_education, "
								+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id,member_marital,mrd_no)"
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?)");

						pst.setString(1, CHILD);
						pst.setString(2, getChildName()[i]);
						pst.setDate(3, uF.getDateFormat(getChildDob()[i], DATE_FORMAT));
						pst.setString(4, getChildEducation()[i]);
						pst.setString(5, getChildOccupation()[i]);
						pst.setString(6, getChildContactNumber()[i]);
						pst.setString(7, getChildEmailId()[i]);
						pst.setString(8, getChildGender()[i]);
						pst.setInt(9, uF.parseToInt(getEmpId()));
						if (getChildMaritalStatus() != null && getChildMaritalStatus().length > 0 && getChildMaritalStatus()[i] != null
								&& !getChildMaritalStatus()[i].trim().equals("0")) {
							pst.setString(10, getChildMaritalStatus()[i]);
						} else {
							pst.setString(10, "");
						}
						pst.setString(11, getChildMRDNo()[i]);
						int x = pst.executeUpdate();
						pst.close();

						if (x > 0) {
							/**
							 * Log Details
							 * */
							String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
							String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
							String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted child name (" + uF.showData(getChildName()[i], "")
									+ ") of " + uF.showData(strEmpName, "") + " on " + ""
									+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
									+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
							LogDetails logDetails = new LogDetails();
							logDetails.session = session;
							logDetails.CF = CF;
							logDetails.request = request;
							logDetails.setProcessId(uF.parseToInt(getEmpId()));
							logDetails.setProcessType(L_EMPLOYEE);
							logDetails.setProcessActivity(L_ADD);
							logDetails.setProcessMsg(strProcessMsg);
							logDetails.setProcessStep(uF.parseToInt(getStep()));
							logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
							logDetails.insertLog(con, uF);
						}

					}
				}

			}

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
	

	public void updateEmpEducation(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			if (getDegreeName() != null && getDegreeName().length != 0) {

//				pst = con.prepareStatement("DELETE FROM education_details WHERE emp_id = ?");
//				pst.setInt(1, uF.parseToInt(getEmpId()));
//				// log.debug("pst=>"+pst);
//				pst.execute();
//				pst.close();

				pst = con.prepareStatement("select degree_id from education_details where emp_id=?");
				pst.setInt(1, uF.parseToInt(getEmpId()));
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
							int newOrgId = 0;
							pst = con.prepareStatement("select org_id from employee_official_details where emp_id=?");
							pst.setInt(1, uF.parseToInt(getEmpId()));
							rs = pst.executeQuery();
							while (rs.next()) {
								newOrgId = rs.getInt("org_id");
							}
							rs.close();
							pst.close();

							pst = con.prepareStatement("insert into educational_details(education_name,education_details,org_id) VALUES (?,?,?)");
							pst.setString(1, degreeNameOther[i]);
							pst.setString(2, "");
							pst.setInt(3, newOrgId);
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

							pst = con.prepareStatement("INSERT INTO education_details(education_id, degree_duration, completion_year, grade, emp_id, " +
								"inst_name) VALUES (?,?,?,?, ?,?)");
							pst.setInt(1, newEduid);
							pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
							pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
							pst.setString(4, getGrade()[i]);
							pst.setInt(5, uF.parseToInt(getEmpId()));
							pst.setString(6, getInstName()[i]);
							int x = pst.executeUpdate();
							pst.close();
							
							int intDegreeId = 0;
							pst = con.prepareStatement("select max(degree_id) as degree_id from education_details");
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
										strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_PEOPLE + "/" + I_DOCUMENT + "/" + I_EDUCATION_DOC + "/"+ getEmpId(), strCertificateDoc[j], strCertificateDocFileName[j], strCertificateDocFileName[j], CF);
									}
									pst = con.prepareStatement("INSERT INTO emp_degree_certificate_details(degree_id, emp_id, degree_certificate_name) VALUES (?,?,?)");
									pst.setInt(1, intDegreeId);
									pst.setInt(2, uF.parseToInt(getEmpId()));
									pst.setString(3, strFileName);
									pst.executeUpdate();
									pst.close();
								}
							}
							

							if (x > 0) {
								/**
								 * Log Details
								 * */
								String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
								String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
								String strProcessMsg = uF.showData(strProcessByName, "") + " has updated education (" + uF.showData(degreeNameOther[i], "")
									+ ") of " + uF.showData(strEmpName, "") + " on " + ""
									+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
									+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
								LogDetails logDetails = new LogDetails();
								logDetails.session = session;
								logDetails.CF = CF;
								logDetails.request = request;
								logDetails.setProcessId(uF.parseToInt(getEmpId()));
								logDetails.setProcessType(L_EMPLOYEE);
								logDetails.setProcessActivity(L_UPDATE);
								logDetails.setProcessMsg(strProcessMsg);
								logDetails.setProcessStep(uF.parseToInt(getStep()));
								logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
								logDetails.insertLog(con, uF);
							}
							//
						} else {
							
//							System.out.println("alEmpDegreeId ===>> " + alEmpDegreeId);
							
							if(getDegreeId() != null && i<getDegreeId().length && uF.parseToInt(getDegreeId()[i])!=0) {
								StringBuilder sbQuery = new StringBuilder();
								sbQuery.append("update education_details set education_id=?,degree_duration=?,completion_year=?,grade=?,inst_name=?");
								sbQuery.append(" where degree_id=?");
								pst = con.prepareStatement(sbQuery.toString());
								pst.setInt(1, uF.parseToInt(getDegreeName()[i]));
								pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
								pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
								pst.setString(4, getGrade()[i]);
								pst.setString(5, getInstName()[i]);
								pst.setInt(6, uF.parseToInt(getDegreeId()[i]));
								int x = pst.executeUpdate();
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
											strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_PEOPLE + "/" + I_DOCUMENT + "/" + I_EDUCATION_DOC + "/"+ getEmpId(), strCertificateDoc[j], strCertificateDocFileName[j], strCertificateDocFileName[j], CF);
										}
										pst = con.prepareStatement("INSERT INTO emp_degree_certificate_details(degree_id, emp_id, degree_certificate_name) VALUES (?,?,?)");
										pst.setInt(1, uF.parseToInt(getDegreeId()[i]));
										pst.setInt(2, uF.parseToInt(getEmpId()));
										pst.setString(3, strFileName);
										pst.executeUpdate();
										pst.close();
									}
									
									pst = con.prepareStatement("delete from emp_degree_certificate_details where degree_id=? and emp_id=?");
									pst.setInt(1, uF.parseToInt(getDegreeId()[i]));
									pst.setInt(2, uF.parseToInt(getEmpId()));
									pst.executeUpdate();
									pst.close();
								}
								
								if (x > 0) {
									/**
									 * Log Details
									 * */
									String strEducation = CF.getDegreeNameByDegreeId(con, "" + uF.parseToInt(getDegreeName()[i]));
									String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
									String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
									String strProcessMsg = uF.showData(strProcessByName, "") + " has updated education (" + uF.showData(strEducation, "") + ") of "
										+ uF.showData(strEmpName, "") + " on " + ""
										+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
										+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
									LogDetails logDetails = new LogDetails();
									logDetails.session = session;
									logDetails.CF = CF;
									logDetails.request = request;
									logDetails.setProcessId(uF.parseToInt(getEmpId()));
									logDetails.setProcessType(L_EMPLOYEE);
									logDetails.setProcessActivity(L_UPDATE);
									logDetails.setProcessMsg(strProcessMsg);
									logDetails.setProcessStep(uF.parseToInt(getStep()));
									logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
									logDetails.insertLog(con, uF);
								}
							} else {
								pst = con.prepareStatement("INSERT INTO education_details(education_id, degree_duration, completion_year, grade, emp_id, " +
									"inst_name) VALUES (?,?,?,?, ?,?)");
								pst.setInt(1, uF.parseToInt(getDegreeName()[i]));
								pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
								pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
								pst.setString(4, getGrade()[i]);
								pst.setInt(5, uF.parseToInt(getEmpId()));
								pst.setString(6, getInstName()[i]);
								int x = pst.executeUpdate();
								pst.close();
								
								int intDegreeId = 0;
								pst = con.prepareStatement("select max(degree_id) as degree_id from education_details");
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
											strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_PEOPLE + "/" + I_DOCUMENT + "/" + I_EDUCATION_DOC + "/"+ getEmpId(), strCertificateDoc[j], strCertificateDocFileName[j], strCertificateDocFileName[j], CF);
										}
										pst = con.prepareStatement("INSERT INTO emp_degree_certificate_details(degree_id, emp_id, degree_certificate_name) VALUES (?,?,?)");
										pst.setInt(1, intDegreeId);
										pst.setInt(2, uF.parseToInt(getEmpId()));
										pst.setString(3, strFileName);
										pst.executeUpdate();
										pst.close();
									}
								}
							
								/*String strFileName = null;
								System.out.println("getDegreeCertiStatus()[i] ===>> " + getDegreeCertiStatus()[i]);
								if(uF.parseToInt(getDegreeCertiStatus()[i]) == 1) {
									File[] strCertificateDoc = mpRequest.getFiles("degreeCertificate"+i);
									String[] strCertificateDocFileName = mpRequest.getFileNames("degreeCertificate"+i);
									if (CF.getStrDocSaveLocation() == null) {
										strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, strCertificateDoc[0], strCertificateDocFileName[0], strCertificateDocFileName[0], CF);
									} else {
										strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_PEOPLE + "/" + I_DOCUMENT + "/" + I_EDUCATION_DOC + "/"+ getEmpId(), strCertificateDoc[0], strCertificateDocFileName[0], strCertificateDocFileName[0], CF);
									}
								}
								pst = con.prepareStatement("INSERT INTO education_details(education_id, degree_duration, completion_year, grade, emp_id, " +
									"inst_name, degree_certificate)VALUES (?,?,?,?, ?,?,?)");
								pst.setInt(1, uF.parseToInt(getDegreeName()[i]));
								pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
								pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
								pst.setString(4, getGrade()[i]);
								pst.setInt(5, uF.parseToInt(getEmpId()));
								pst.setString(6, getInstName()[i]);
								pst.setString(7, strFileName);
								int x = pst.executeUpdate();
								pst.close();*/

								if (x > 0) {
									/**
									 * Log Details
									 * */
									String strEducation = CF.getDegreeNameByDegreeId(con, "" + uF.parseToInt(getDegreeName()[i]));
									String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
									String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
									String strProcessMsg = uF.showData(strProcessByName, "") + " has updated education (" + uF.showData(strEducation, "") + ") of "
										+ uF.showData(strEmpName, "") + " on " + ""
										+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
										+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
									LogDetails logDetails = new LogDetails();
									logDetails.session = session;
									logDetails.CF = CF;
									logDetails.request = request;
									logDetails.setProcessId(uF.parseToInt(getEmpId()));
									logDetails.setProcessType(L_EMPLOYEE);
									logDetails.setProcessActivity(L_UPDATE);
									logDetails.setProcessMsg(strProcessMsg);
									logDetails.setProcessStep(uF.parseToInt(getStep()));
									logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
									logDetails.insertLog(con, uF);
								}
							}
							
							
							for(int j=0; alEmpDegreeId!=null && j<alEmpDegreeId.size(); j++) {
								pst = con.prepareStatement("delete from education_details where degree_id =? and emp_id=?");
								pst.setInt(1, uF.parseToInt(alEmpDegreeId.get(j)));
								pst.setInt(2, uF.parseToInt(getEmpId()));
								pst.executeUpdate();
								pst.close();
								
								pst = con.prepareStatement("delete from emp_degree_certificate_details where degree_id =? and emp_id=?");
								pst.setInt(1, uF.parseToInt(alEmpDegreeId.get(j)));
								pst.setInt(2, uF.parseToInt(getEmpId()));
								pst.executeUpdate();
								pst.close();
							}
						}

					}

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

	
	
	public void updateEmpLangues(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;

		try {
			if (getLanguageName() != null && getLanguageName().length != 0) {
				pst = con.prepareStatement("DELETE FROM languages_details WHERE emp_id = ?");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.execute();
				pst.close();

				if (getIsRead() != null)
				if (getIsWrite() != null)
				if (getIsSpeak() != null)
					
//					System.out.println("Size of isMotherTounge====>"+isMotherTounge.length);
//				    System.out.println("Size of isWrite====>"+isWrite.length);
//				    System.out.println("Size of isRead====>"+isRead.length);
//				    System.out.println("Size of isSpeak====>"+isSpeak.length);
				for (int i = 0; getLanguageName() != null && i < getLanguageName().length; i++) {
					if (getLanguageName()[i].length() != 0) {
						pst = con.prepareStatement("INSERT INTO languages_details(language_name, language_read, language_write, language_speak, emp_id, language_mothertounge) VALUES (?,?,?,?,?,?)");
						pst.setString(1, getLanguageName()[i]);
						pst.setInt(2, uF.parseToInt(getIsRead()[i]));
						pst.setInt(3, uF.parseToInt(getIsWrite()[i]));
						pst.setInt(4, uF.parseToInt(getIsSpeak()[i]));
						pst.setInt(5, uF.parseToInt(getEmpId()));
						pst.setInt(6, uF.parseToInt(getIsMotherTounge()[i]));
//					    System.out.println("getIsMotherTounge()"+i+"=====>"+getIsMotherTounge()[i]);
//						System.out.println("insert pst=====>"+pst);
						int x = pst.executeUpdate();
						pst.close();
						if (x > 0) {
							/**
							 * Log Details
							 * */
							String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
							String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
							String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted language (" + uF.showData(getLanguageName()[i], "") + ") of " + uF.showData(strEmpName, "") + " on " + ""
								+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
								+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
							LogDetails logDetails = new LogDetails();
							logDetails.session = session;
							logDetails.CF = CF;
							logDetails.request = request;
							logDetails.setProcessId(uF.parseToInt(getEmpId()));
							logDetails.setProcessType(L_EMPLOYEE);
							logDetails.setProcessActivity(L_ADD);
							logDetails.setProcessMsg(strProcessMsg);
							logDetails.setProcessStep(uF.parseToInt(getStep()));
							logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
							logDetails.insertLog(con, uF);
						}
					}
				}
			}
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

	
	public void updateSupportingDocuments(Connection con, UtilityFunctions uF) {
		
		try {
			MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
			for(int i=0; i<getIdDocType().length; i++) {
//				System.out.println("getIdDocType="+getIdDocType()[i]);
				if(getDocId() != null && i<getDocId().length && uF.parseToInt(getDocId()[i])!=0) {
					if(uF.parseToInt(getIdDocStatus()[i]) == 1) {
						File[] strIdentityDoc = mpRequest.getFiles("idDoc"+i);
						String[] strIdentityDocFileNames = mpRequest.getFileNames("idDoc"+i);
						updateDocuments(con, uF, getDocId()[i], strIdentityDoc[0], getIdDocName()[i], getIdDocType()[i], getIdDocStatus()[i], strIdentityDocFileNames[0]);
					} else {
						updateDocuments(con, uF, getDocId()[i], null, getIdDocName()[i], getIdDocType()[i], getIdDocStatus()[i], null);
					}
				} else {
					if(uF.parseToInt(getIdDocStatus()[i]) == 1) {
						File[] strIdentityDoc = mpRequest.getFiles("idDoc"+i);
						String[] strIdentityDocFileNames = mpRequest.getFileNames("idDoc"+i);
						insertDocuments(con, uF, strIdentityDoc[0], getIdDocName()[i], getIdDocType()[i], getIdDocStatus()[i], strIdentityDocFileNames[0]);
					} else {
						insertDocuments(con, uF, null, getIdDocName()[i], getIdDocType()[i], getIdDocStatus()[i], null);
					}
				}
			}
//			System.out.println("getDocId1() ===>> " + getDocId1());
			/*if(uF.parseToInt(getDocId1())!=0) {
				if(getIdDoc1() != null) {
					updateDocuments(con, uF, getDocId1(), getIdDoc1(), getIdDocName1(), getIdDocType1(), getIdDocStatus1(), getIdDoc1FileName());
				}
			} else {
				insertDocuments(con, uF, getIdDoc1(), getIdDocName1(), getIdDocType1(), getIdDocStatus1(), getIdDoc1FileName());
			}*/
//			System.out.println("getDocId2() ===>> " + getDocId2());
			/*if(uF.parseToInt(getDocId2())!=0) {
				if(getIdDoc2() != null) {
					updateDocuments(con, uF, getDocId2(), getIdDoc2(), getIdDocName2(), getIdDocType2(), getIdDocStatus2(), getIdDoc2FileName());
				}
			} else {
				insertDocuments(con, uF, getIdDoc2(), getIdDocName2(), getIdDocType2(), getIdDocStatus2(), getIdDoc2FileName());
			}*/
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void updateDocuments(Connection con, UtilityFunctions uF, String docId, File idDoc, String idDocName, String idDocType, String idDocStatus, String idDocFileName) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String strFileName = null;
//			System.out.println("idDoc ===>> " + idDoc);
			if(idDoc != null) {
				if (CF.getStrDocSaveLocation() == null) {
					strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, idDoc, idDocFileName, idDocFileName, CF);
				} else {
					strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_PEOPLE + "/" + I_DOCUMENT + "/" + I_ATTACHMENT + "/"+ getEmpId(), idDoc, idDocFileName, idDocFileName, CF);
				}
			}
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("UPDATE documents_details SET documents_name=?,added_by=?,entry_date=?");
			if(strFileName != null) {
				sbQuery.append(",documents_file_name='"+strFileName+"' ");
			}
			sbQuery.append(" where documents_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setString(1, idDocName);
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt(docId));
//			System.out.println("pst ===>> " + pst);
			int x = pst.executeUpdate();
			pst.close();
			
			if(idDoc != null) {
				if (x > 0) {
					/**
					 * Log Details
					 * */
					String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
					String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
					String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted document (" + uF.showData(idDocName, "")
						+ ") of " + uF.showData(strEmpName, "") + " on " + ""
						+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
						+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
					LogDetails logDetails = new LogDetails();
					logDetails.session = session;
					logDetails.CF = CF;
					logDetails.request = request;
					logDetails.setProcessId(uF.parseToInt(getEmpId()));
					logDetails.setProcessType(L_EMPLOYEE);
					logDetails.setProcessActivity(L_UPDATE);
					logDetails.setProcessMsg(strProcessMsg);
					logDetails.setProcessStep(uF.parseToInt(getStep()));
					logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
					logDetails.insertLog(con, uF);
					
			//===start parvez date: 29-10-2022===		
					if(idDocType!=null && idDocType.equals(DOCUMENT_COMPANY_PROFILE)){
						Map<String, String> hmEmpHRId = CF.getEmpHRIdMap(con, uF);
						String strDomain = request.getServerName().split("\\.")[0];
						Map<String, String> hmEmpUsertypeId = CF.getEmployeeIdUserTypeIdMap(con);
						if(hmEmpHRId!=null && hmEmpHRId.get(getEmpId())!=null && !hmEmpHRId.get(getEmpId()).equals("")){
							String alertData = "<div style=\"float: left;\"> <b>" + uF.showData(strEmpName, "") + "</b> has updated company profile.</div>";
							String alertAction = "MyProfile.action?empId="+getEmpId()+"&pType=WR";
							UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(hmEmpHRId.get(getEmpId()));
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmEmpUsertypeId.get(hmEmpHRId.get(getEmpId())));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
						}
						
						StringBuilder sbQuery1 = new StringBuilder();
						sbQuery1.append("select * from employee_personal_details epd, employee_official_details eod, user_details ud WHERE epd.emp_per_id=eod.emp_id " +
							" and eod.emp_id=ud.emp_id and epd.emp_per_id in (select emp_id from user_details where usertype_id=1 and status=?)");
						pst = con.prepareStatement(sbQuery1.toString());
						pst.setString(1, "ACTIVE");
						rs=pst.executeQuery();
						List<String> alEmpId = new ArrayList<String>();
						while(rs.next()) {
							alEmpId.add(rs.getString("emp_per_id"));
						}
						rs.close();
						pst.close();
						
						for(int i=0; alEmpId!=null && i<alEmpId.size();i++){
							String alertData = "<div style=\"float: left;\"> <b>" + uF.showData(strEmpName, "") + "</b> has updated company profile.</div>";
							String alertAction = "MyProfile.action?empId="+getEmpId()+"&pType=WR";
							UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(alEmpId.get(i));
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmEmpUsertypeId.get(alEmpId.get(i)));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
						}
					}
			//===end parvez date: 29-10-2022===		
				}
			}

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
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void insertDocuments(Connection con, UtilityFunctions uF, File idDoc, String idDocName, String idDocType, String idDocStatus, String idDocFileName) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String strFileName = null;
//			System.out.println("idDoc ===>> " + idDoc);
			if(idDoc != null) {
				if (CF.getStrDocSaveLocation() == null) {
					strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, idDoc, idDocFileName, idDocFileName, CF);
				} else {
					strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_PEOPLE + "/" + I_DOCUMENT + "/" + I_ATTACHMENT + "/"+ getEmpId(), idDoc, idDocFileName, idDocFileName, CF);
				}
			}
			pst = con.prepareStatement(insertDocuments);
			pst.setString(1, idDocName);
			pst.setString(2, idDocType);
			pst.setInt(3, uF.parseToInt(getEmpId()));
			pst.setString(4, strFileName);
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			int x = pst.executeUpdate();
			pst.close();
			
			if(idDoc != null) {
				if (x > 0) {
					/**
					 * Log Details
					 * */
					String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
					String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
					String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted document (" + uF.showData(idDocName, "")
						+ ") of " + uF.showData(strEmpName, "") + " on " + ""
						+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
						+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
					LogDetails logDetails = new LogDetails();
					logDetails.session = session;
					logDetails.CF = CF;
					logDetails.request = request;
					logDetails.setProcessId(uF.parseToInt(getEmpId()));
					logDetails.setProcessType(L_EMPLOYEE);
					logDetails.setProcessActivity(L_ADD);
					logDetails.setProcessMsg(strProcessMsg);
					logDetails.setProcessStep(uF.parseToInt(getStep()));
					logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
					logDetails.insertLog(con, uF);
					
				//===start parvez date: 29-10-2022===	
					if(idDocType!=null && idDocType.equals(DOCUMENT_COMPANY_PROFILE)){
						Map<String, String> hmEmpHRId = CF.getEmpHRIdMap(con, uF);
						String strDomain = request.getServerName().split("\\.")[0];
						Map<String, String> hmEmpUsertypeId = CF.getEmployeeIdUserTypeIdMap(con);
						if(hmEmpHRId!=null && hmEmpHRId.get(getEmpId())!=null && !hmEmpHRId.get(getEmpId()).equals("")){
							String alertData = "<div style=\"float: left;\"> <b>" + uF.showData(strEmpName, "") + "</b> has updated company profile.</div>";
							String alertAction = "MyProfile.action?empId="+getEmpId()+"&pType=WR";
							UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(hmEmpHRId.get(getEmpId()));
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmEmpUsertypeId.get(hmEmpHRId.get(getEmpId())));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
						}
						
						StringBuilder sbQuery1 = new StringBuilder();
						sbQuery1.append("select * from employee_personal_details epd, employee_official_details eod, user_details ud WHERE epd.emp_per_id=eod.emp_id " +
							" and eod.emp_id=ud.emp_id and epd.emp_per_id in (select emp_id from user_details where usertype_id=1 and status=?)");
						pst = con.prepareStatement(sbQuery1.toString());
						pst.setString(1, "ACTIVE");
						rs=pst.executeQuery();
						List<String> alEmpId = new ArrayList<String>();
						while(rs.next()) {
							alEmpId.add(rs.getString("emp_per_id"));
						}
						rs.close();
						pst.close();
						
						for(int i=0; alEmpId!=null && i<alEmpId.size();i++){
//							if(uF.parseToInt(hmEmpHRId.get(getEmpId()))!=uF.parseToInt(alEmpId.get(i))){
								String alertData = "<div style=\"float: left;\"> <b>" + uF.showData(strEmpName, "") + "</b> has updated company profile.</div>";
								String alertAction = "MyProfile.action?empId="+getEmpId()+"&pType=WR";
								UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(alEmpId.get(i));
								userAlerts.setStrData(alertData);
								userAlerts.setStrAction(alertAction);
								userAlerts.setCurrUserTypeID(hmEmpUsertypeId.get(alEmpId.get(i)));
								userAlerts.setStatus(INSERT_WR_ALERT);
								Thread t = new Thread(userAlerts);
								t.run();
//							}
						}
					}
				//===end parvez date: 29-10-2022===	
				}
			}

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
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
//	public void updateDocuments(Connection con, UtilityFunctions uF) {
//		PreparedStatement pst = null;
//		try {
//			int fileCnt = 0;
//			if (getIdDocType() != null && getIdDocType().length != 0) {
//				for (int i = 0; i < getIdDocType().length; i++) {
//					if (uF.parseToInt(getIdDocStatus().get(i)) == 1) {
//						String strFileName = null;
//						if (CF.getStrDocSaveLocation() == null) {
//							strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getIdDoc().get(i), getIdDocFileName().get(i), getIdDocFileName().get(i), CF);
//						} else {
//							strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_PEOPLE + "/" + I_DOCUMENT + "/" + I_ATTACHMENT + "/"+ getEmpId(), getIdDoc().get(i), getIdDocFileName().get(i), getIdDocFileName().get(i), CF);
//						}
//
//						pst = con.prepareStatement(insertDocuments);
//						pst.setString(1, getIdDocName()[i]);
//						pst.setString(2, getIdDocType()[i]);
//						pst.setInt(3, getEmpId());
//						pst.setString(4, strFileName);
//						pst.setInt(5, uF.parseToInt(strSessionEmpId));
//						pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
//						int x = pst.executeUpdate();
//						pst.close();
//						fileCnt++;
//						if (x > 0) {
//							/**
//							 * Log Details
//							 * */
//							String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
//							String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
//							String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted document (" + uF.showData(getIdDocName()[i], "")
//									+ ") of " + uF.showData(strEmpName, "") + " on " + ""
//									+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
//									+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
//							LogDetails logDetails = new LogDetails();
//							logDetails.session = session;
//							logDetails.CF = CF;
//							logDetails.request = request;
//							logDetails.setProcessId(getEmpId());
//							logDetails.setProcessType(L_EMPLOYEE);
//							logDetails.setProcessActivity(L_ADD);
//							logDetails.setProcessMsg(strProcessMsg);
//							logDetails.setProcessStep(uF.parseToInt(getStep()));
//							logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
//							logDetails.insertLog(con, uF);
//						}
//					} else {
//						pst = con.prepareStatement(insertDocuments);
//						pst.setString(1, getIdDocName()[i]);
//						pst.setString(2, getIdDocType()[i]);
//						pst.setInt(3, getEmpId());
//						pst.setString(4, null);
//						pst.setInt(5, uF.parseToInt(strSessionEmpId));
//						pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
//						int x = pst.executeUpdate();
//						pst.close();
//					}
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (pst != null) {
//				try {
//					pst.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
	

	public void updateHobbies(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;

		try {
			pst = con.prepareStatement(deleteHobbies);
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.execute();

		//===start parvez date: 08-08-2022===	
			if (getHobbyName() != null && getHobbyName().length != 0) {
				for (String h : hobbyName) {
					if (h != null && !h.equalsIgnoreCase("null") && h.trim().length() > 0) {
						pst = con.prepareStatement(insertHobbies);
						pst.setString(1, h);
						pst.setInt(2, uF.parseToInt(getEmpId()));
						if (isDebug) {
							int x = pst.executeUpdate();
							pst.close();
	
							if (x > 0) {
								/**
								 * Log Details
								 * */
								String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
								String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
								String strProcessMsg = uF.showData(strProcessByName, "") + " has updated hobby (" + uF.showData(h, "") + ") of "
										+ uF.showData(strEmpName, "") + " on " + ""
										+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
										+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
								LogDetails logDetails = new LogDetails();
								logDetails.session = session;
								logDetails.CF = CF;
								logDetails.request = request;
								logDetails.setProcessId(uF.parseToInt(getEmpId()));
								logDetails.setProcessType(L_EMPLOYEE);
								logDetails.setProcessActivity(L_UPDATE);
								logDetails.setProcessMsg(strProcessMsg);
								logDetails.setProcessStep(uF.parseToInt(getStep()));
								logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
								logDetails.insertLog(con, uF);
							}
						}
	
					}
				}
			}
		//===end parvez date: 08-08-2022===	

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

	public void updateSkills(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;

		try {
			Map<String, String> hmSkillName = CF.getSkillNameMap(con);
			if (hmSkillName == null)
				hmSkillName = new HashMap<String, String>();

			pst = con.prepareStatement(deleteSkills);
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.execute();
			pst.close();

			for (int i = 0; getSkillName() != null && i < getSkillName().length; i++) {

				if (getSkillName()[i].length() != 0 && getSkillValue()[i].length() != 0) {

					pst = con.prepareStatement(insertSkill);
					pst.setInt(1, uF.parseToInt(getSkillName()[i]));
					pst.setString(2, getSkillValue()[i]);
					pst.setInt(3, uF.parseToInt(getEmpId()));
					if (isDebug) {
						int x = pst.executeUpdate();
						pst.close();

						if (x > 0) {
							/**
							 * Log Details
							 * */
							String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
							String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
							String strProcessMsg = uF.showData(strProcessByName, "") + " has updated skills ("
									+ uF.showData(hmSkillName.get(getSkillName()[i]), "") + ") of " + uF.showData(strEmpName, "") + " on " + ""
									+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
									+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
							LogDetails logDetails = new LogDetails();
							logDetails.session = session;
							logDetails.CF = CF;
							logDetails.request = request;
							logDetails.setProcessId(uF.parseToInt(getEmpId()));
							logDetails.setProcessType(L_EMPLOYEE);
							logDetails.setProcessActivity(L_UPDATE);
							logDetails.setProcessMsg(strProcessMsg);
							logDetails.setProcessStep(uF.parseToInt(getStep()));
							logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
							logDetails.insertLog(con, uF);
						}
					}

				}
			}

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

	public void insertEmployee(Connection con) {
		
		UtilityFunctions uF = new UtilityFunctions();
		try {
		
			if(uF.parseToInt(getStep()) == 1) {
				if(uF.parseToInt(getEmpId())==0) {
					setEmpId(insertEmpPersonalDetails(con, uF, CF));
					insertUser(con, uF, uF.parseToInt(getEmpId()));
					uploadImage(uF.parseToInt(getEmpId()),1);
					uploadImage(uF.parseToInt(getEmpId()),2);
				} else {
					updateEmpPersonalDetails(con, uF);
					uploadImage(uF.parseToInt(getEmpId()),1);
					uploadImage(uF.parseToInt(getEmpId()),2);
				}
				
				session.setAttribute("EMPNAME_P", getEmpFname()+" "+ getEmpMname()+" "+getEmpLname());
				session.setAttribute("EMPID_P", getEmpId()+"");
				
			} else if(uF.parseToInt(getStep()) == 2) {
				
				insertSkills(con, uF);
				insertHobbies(con, uF);
				insertEmpLangues(con, uF);
				insertEmpEducation(con, uF);
			
			} else if(uF.parseToInt(getStep()) == 5) {
				
				insertEmpFamilyMembers(con, uF);
			
			} else if(uF.parseToInt(getStep()) == 3) {
				
				insertEmpPrevEmploment(con, uF);
				
			} else if(uF.parseToInt(getStep()) == 4) {
				
				insertEmpReferences(con, uF);
				
			} else if(uF.parseToInt(getStep()) == 6) {

				insertEmpMedicalInfo(con, uF);
			
			} else if(uF.parseToInt(getStep()) == 7) {
				
				updateSupportingDocuments(con, uF);
				
			} else if(uF.parseToInt(getStep()) == 8) {
			
				Map<String, String> hmServices = CF.getServicesMap(con,false);
				List<List<String>> alServices = new ArrayList<List<String>>();
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
				
				session.setAttribute("alServices", alServices);
				updateEmpCodeBankInfo(con,uF);
				updateEmpJoiningDate(con,uF);
				updateEmpOfficialDetailsAdmin(con, uF, sbServices);
				
				
				insertProbationPeriod(con, uF); 
				if(uF.parseToInt(getEmpStatus()) != 1) {
					updateEmployeeStatus(con, uF);
				}
				
				
				boolean flag = checkEmpStatus(con,uF.parseToInt(getEmpId()));
				if(flag) {
					updateEmpFilledStatus(con,uF.parseToInt(getEmpId()));
					updateEmpLiveStatus(con,uF.parseToInt(getEmpId()));
					
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
				}
				
// ---------------- Insert kras =========================				
				AddKRA addDKRA = new AddKRA();
				addDKRA.setCF(CF);
				addDKRA.setServletRequest(request);
				addDKRA.setEmpId(empId+"");
				addDKRA.setStrSessionEmpId(strSessionEmpId);
				addDKRA.setKraSubmit("");
				addDKRA.setStrEffectiveDate(getEmpStartDate());
				addDKRA.setOrgId(getOrgId());
				addDKRA.setLevelId(getStrLevel());
				addDKRA.setGradeId(getEmpGrade());
				addDKRA.setGoalElements(getDesigElements());
				addDKRA.setCgoalAlignAttribute(getDesigElementAttribute());
				addDKRA.setEmpKra(getDesignKRA());
				addDKRA.setDesigKraId(getDesigKraId());
				addDKRA.setEmpKraId(getDesigEmpKraId());
				addDKRA.setStatus(getStatus());
				addDKRA.insertKRA();
				
				AddKRA addEmpKRA = new AddKRA();
				addEmpKRA.setCF(CF);
				addEmpKRA.setServletRequest(request);
				addEmpKRA.setEmpId(empId+"");
				addEmpKRA.setStrSessionEmpId(strSessionEmpId);
				addEmpKRA.setKraSubmit("");
				addEmpKRA.setStrEffectiveDate(getEmpStartDate());
				addEmpKRA.setOrgId(getOrgId());
				addEmpKRA.setLevelId(getStrLevel());
				addEmpKRA.setGradeId(getEmpGrade());
				addEmpKRA.setGoalElements(getGoalElements());
				addEmpKRA.setCgoalAlignAttribute(getElementAttribute());
				addEmpKRA.setEmpKra(getEmpKRA());
				addEmpKRA.setEmpKraId(getEmpKraId());
				addEmpKRA.insertKRA();
				
				setServiceId(getService()[0]);
				setServiceName(hmServices.get(getService()[0]));
				
				insertLeaveRegisterNewEmployee(con, uF.parseToInt(getEmpId()));
				CF.assignReimbursementCTCForEmployee(request,con,CF,uF,uF.parseToInt(getEmpId()));
				
			} else if(uF.parseToInt(getStep()) == 9) {
				
				int serviceCount = 0 ;
				
				if(session.getAttribute("alServices") != null) {
					serviceCount = ((List)session.getAttribute("alServices")).size();
//					log.debug("Step 9 => serviceCount===>"+serviceCount);
					
					if(serviceCount != 0) {
						List<List<String>> alServices = (List<List<String>>) session.getAttribute("alServices");
						List<String> empServicesList = alServices.get(serviceCount - 1 );
//						log.debug("Step 9 => empServicesList====>"+empServicesList);
						setServiceId(empServicesList.get(0));
						setServiceName(empServicesList.get(1));
						alServices.remove(serviceCount-1);
				
					} else {
						session.removeAttribute("alServices");
//						log.debug("Step 9 => inserted Salary For all services!!");
						updateEmpFilledStatus(con,uF.parseToInt(getEmpId()));
						approveEmployee(con, uF);
						setProbationPolicy(con, uF);
						insertEmpActivity(con,getEmpId()+"", CF, strSessionEmpId, null);
						setStep("10");
					}
				}
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		
		} finally {
			
//			db.closeStatements(pst);
//			db.closeConnection(con);
		}
	}

	
	private void approveEmployee(Connection con, UtilityFunctions uF) {
		
//		Connection con = null;
		PreparedStatement pst = null;
//		Database db = new Database();
		
		try {
			
//			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE employee_personal_details SET approved_flag = ? , is_alive = ? WHERE emp_per_id = ?");
			pst.setBoolean(1, true);
			pst.setBoolean(2, true);
			pst.setInt(3, uF.parseToInt(getEmpId()));
//			System.out.println("pst ===>>>> " + pst);
//			log.debug("pst===>"+pst);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("UPDATE candidate_personal_details SET is_emp_live = ? WHERE candididate_emp_id = ?");
			pst.setBoolean(1, true);
			pst.setInt(2, uF.parseToInt(getEmpId()));
//			log.debug("pst===>"+pst);
//			System.out.println("pst ===>>>> " + pst);
			pst.executeUpdate();
			pst.close();
//			log.debug("cnt==>"+cnt);
			
		} catch (Exception e) {
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


	private void updateEmpCodeBankInfo(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;

		try {
	//===start parvez date: 12-08-2022===		
			pst = con.prepareStatement("UPDATE employee_personal_details SET empcode = ?, emp_bank_name = ? , emp_bank_acct_nbr = ? , emp_other_bank_acct_ifsc_code= ? , "
					+ " emp_bank_name2 = ?, emp_bank_acct_nbr_2 = ? , emp_other_bank_acct_ifsc_code_2 = ?, emp_other_bank_name=?, emp_other_bank_name2=?, " +
							" emp_other_bank_branch=?, emp_other_bank_branch2=? where emp_per_id = ?");
			pst.setString(1, uF.showData(getEmpCodeAlphabet(), "") + getEmpCodeNumber());
			pst.setString(2, getEmpBankName());
			pst.setString(3, getEmpBankAcctNbr());
			pst.setString(4, getEmpBankIFSCNbr());
			pst.setString(5, getEmpBankName2());
			pst.setString(6, getEmpBankAcctNbr2());
			pst.setString(7, getEmpBankIFSCNbr2());
//			pst.setInt(8, uF.parseToInt(getEmpId()));
			pst.setString(8, getEmpOtherBankName());
			pst.setString(9, getEmpOtherBankName2());
			pst.setString(10, getEmpOtherBankBranch());
			pst.setString(11, getEmpOtherBankBranch2());
			pst.setInt(12, uF.parseToInt(getEmpId()));
	//===end parvez date: 12-08-2022===		
			int x = pst.executeUpdate();
			pst.close();

			if (x > 0) {
				/**
				 * Log Details
				 * */
				String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
				String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
				String strProcessMsg = uF.showData(strProcessByName, "") + " has updated bank details of " + uF.showData(strEmpName, "") + " on " + ""
						+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
						+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
				LogDetails logDetails = new LogDetails();
				logDetails.session = session;
				logDetails.CF = CF;
				logDetails.request = request;
				logDetails.setProcessId(uF.parseToInt(getEmpId()));
				logDetails.setProcessType(L_EMPLOYEE);
				logDetails.setProcessActivity(L_UPDATE);
				logDetails.setProcessMsg(strProcessMsg);
				logDetails.setProcessStep(uF.parseToInt(getStep()));
				logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
				logDetails.insertLog(con, uF);
			}

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


	private void insertEmpReferences(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;

		try {
			if (getRefName() != null && getRefName().length > 0) {
				for (int i = 0; i < getRefName().length; i++) {
					if (getRefName()[i].length() != 0) {
						pst = con.prepareStatement("INSERT INTO emp_references (ref_name, ref_company, ref_designation, ref_contact_no, ref_email_id, emp_id) "
								+ "values(?,?,?, ?,?,?)");
						pst.setString(1, getRefName()[i]);
						pst.setString(2, (getRefCompany()[i] != null && !getRefCompany()[i].equals("") && !getRefCompany()[i].equals("Other"))
								? getRefCompany()[i]
								: getRefCompanyOther()[i]);
						pst.setString(3, getRefDesignation()[i]);
						pst.setString(4, getRefContactNo()[i]);
						pst.setString(5, getRefEmail()[i]);
						pst.setInt(6, uF.parseToInt(getEmpId()));
						int x = pst.executeUpdate();
						pst.close();

						if (x > 0) {
							/**
							 * Log Details
							 * */
							String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
							String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
							String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted reference (" + uF.showData(getRefName()[i], "") + ") of "
									+ uF.showData(strEmpName, "") + " on " + ""
									+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
									+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
							LogDetails logDetails = new LogDetails();
							logDetails.session = session;
							logDetails.CF = CF;
							logDetails.request = request;
							logDetails.setProcessId(uF.parseToInt(getEmpId()));
							logDetails.setProcessType(L_EMPLOYEE);
							logDetails.setProcessActivity(L_ADD);
							logDetails.setProcessMsg(strProcessMsg);
							logDetails.setProcessStep(uF.parseToInt(getStep()));
							logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
							logDetails.insertLog(con, uF);
						}
					}
				}
			}

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
	
	
	private void updateEmpMedicalInfo(Connection con, UtilityFunctions uF) {
		
//		PreparedStatement pst = null;
		try {
			File file1=null;
			String filename1=null;
			int fileSize=1;
			if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(0).equals("1")) {
				 file1=getQue1DescFile().get(fileSize-1);
				 filename1=getQue1DescFileFileName().get(fileSize-1);
				 fileSize++;
			}
			
			if(uF.parseToInt(getEmpMedicalId1())!=0) {
				updateEmpMedicalInfo1(con,uF,getEmpMedicalId1(),isCheckQue1(),getQue1Desc(),file1,filename1);
			} else {
				insertEmpMedicalInfo1(con,uF,getQue1Id(),isCheckQue1(),getQue1Desc(),file1,filename1);
			}
			
			File file2=null;
			String filename2=null;
			if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(1).equals("1")) {
				 file2=getQue1DescFile().get(fileSize-1);
				 filename2=getQue1DescFileFileName().get(fileSize-1);
				 fileSize++;
			}
			
			if(uF.parseToInt(getEmpMedicalId2())!=0) {
				updateEmpMedicalInfo1(con,uF,getEmpMedicalId2(),isCheckQue2(),getQue2Desc(),file2,filename2);
			} else {
				insertEmpMedicalInfo1(con,uF,getQue2Id(),isCheckQue2(),getQue2Desc(),file2,filename2);
			}
			
			File file3=null;
			String filename3=null;
			if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(2).equals("1")) {
				 file3=getQue1DescFile().get(fileSize-1);
				 filename3=getQue1DescFileFileName().get(fileSize-1);
				 fileSize++;
			}
			
			if(uF.parseToInt(getEmpMedicalId3())!=0) {
				updateEmpMedicalInfo1(con,uF,getEmpMedicalId3(),isCheckQue3(),getQue3Desc(),file3,filename3);
			} else {
				insertEmpMedicalInfo1(con,uF,getQue3Id(),isCheckQue3(),getQue3Desc(),file3,filename3);
			}
			
//			file1=null;
//			 filename=null;
//			if(getQue1DescFile()!=null && getQue1DescFile().size()>=4 && getQue1IdFileStatus().get(3).equals("1")) {
//				 file1=getQue1DescFile().get(3);
//				 filename=getQue1DescFileFileName().get(3);
//			}
//			if(uF.parseToInt(getEmpMedicalId4())!=0)
//				updateEmpMedicalInfo1(con,uF,getEmpMedicalId4(),isCheckQue4(),getQue4Desc(),file1,filename);
//				else
//					insertEmpMedicalInfo1(con,uF,getQue4Id(),isCheckQue4(),getQue4Desc(),file1,filename);
//			
//			file1=null;
//			 filename=null;
//			if(getQue1DescFile()!=null && getQue1DescFile().size()>=5 && getQue1IdFileStatus().get(4).equals("1")) {
//				 file1=getQue1DescFile().get(4);
//				 filename=getQue1DescFileFileName().get(4);
//			}
//			if(uF.parseToInt(getEmpMedicalId5())!=0)
//				updateEmpMedicalInfo1(con,uF,getEmpMedicalId5(),isCheckQue5(),getQue5Desc(),file1,filename);
//				else
//					insertEmpMedicalInfo1(con,uF,getQue5Id(),isCheckQue5(),getQue5Desc(),file1,filename);
//			
//			String fileName =null;
//			if(getQue1DescFile()!=null) {
//			 fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getQue1DescFile(), getQue1DescFileFileName());
//			 }
//			pst = con.prepareStatement("UPDATE emp_medical_details SET yes_no=?, description=?,filepath=? WHERE medical_id = ?");
//			pst.setBoolean(1, isCheckQue1());
//			pst.setString(2, getQue1Desc());
//			pst.setString(3, fileName);
//			pst.setInt(4, uF.parseToInt(getEmpMedicalId1()));
//			log.debug("pst ==>"+pst);
//			pst.execute();
//			
//			fileName =null;
//			if(getQue2DescFile()!=null) {
//			 fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getQue2DescFile(), getQue1DescFileFileName());
//			 }	
//			pst = con.prepareStatement("UPDATE emp_medical_details SET yes_no=?, description=? ,filepath=? WHERE medical_id = ?");
//			pst.setBoolean(1, isCheckQue2());
//			pst.setString(2, getQue2Desc());
//			pst.setString(3, fileName);
//			pst.setInt(4, uF.parseToInt(getEmpMedicalId2()));
//			log.debug("pst ==>"+pst);
//			pst.execute();
//			
//			fileName =null;
//			if(getQue3DescFile()!=null) {
//			 fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getQue3DescFile(), getQue1DescFileFileName());
//			 }	
//			pst = con.prepareStatement("UPDATE emp_medical_details SET yes_no=?, description=? ,filepath=? WHERE medical_id = ?");
//			pst.setBoolean(1, isCheckQue3());
//			pst.setString(2, getQue3Desc());
//			pst.setString(3, fileName);
//			pst.setInt(4, uF.parseToInt(getEmpMedicalId3()));
//			log.debug("pst ==>"+pst);
//			pst.execute();
//			
//			fileName =null;
//			if(getQue4DescFile()!=null) {
//			 fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getQue4DescFile(), getQue1DescFileFileName());
//			 }
//			pst = con.prepareStatement("UPDATE emp_medical_details SET yes_no=?, description=? ,filepath=? WHERE medical_id = ?");
//			pst.setBoolean(1, isCheckQue4());
//			pst.setString(2, getQue4Desc());
//			pst.setString(3, fileName);
//			pst.setInt(4, uF.parseToInt(getEmpMedicalId4()));
//			log.debug("pst ==>"+pst);
//			pst.execute();
//			
//			fileName =null;
//			if(getQue5DescFile()!=null) {
//			 fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getQue5DescFile(), getQue1DescFileFileName());
//			 }
//			pst = con.prepareStatement("UPDATE emp_medical_details SET yes_no=?, description=?,filepath=?  WHERE medical_id = ?");
//			pst.setBoolean(1, isCheckQue5());
//			pst.setString(2, getQue5Desc());
//			pst.setString(3, fileName);
//			pst.setInt(4, uF.parseToInt(getEmpMedicalId5()));
//			log.debug("pst ==>"+pst);
//			pst.execute();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	public List<File> getQue1DescFile() {
		return que1DescFile;
	}
	
	public void setQue1DescFile(List<File> que1DescFile) {
		this.que1DescFile = que1DescFile;
	}
	
	public List<String> getQue1DescFileFileName() {
		return que1DescFileFileName;
	}
	
	public void setQue1DescFileFileName(List<String> que1DescFileFileName) {
		this.que1DescFileFileName = que1DescFileFileName;
	}



	private void updateEmpMedicalInfo1(Connection con, UtilityFunctions uF,String medicalid, boolean checkQue, String queDesc, File file,String fileName1) {
		PreparedStatement pst = null;
		try {
			String strFileName = null;
			if (file != null) {
				if (CF.getStrDocSaveLocation() == null) {
					strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, file, fileName1, fileName1, CF);
				} else {
					strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_PEOPLE + "/" + I_DOCUMENT + "/" + I_MEDICAL + "/"+ getEmpId(), file, fileName1, fileName1, CF);
				}
			}
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("UPDATE emp_medical_details SET yes_no=?, description=? ");
			if(checkQue && strFileName != null && !strFileName.equalsIgnoreCase("null")) {
				sbQuery.append(",filepath= '"+strFileName+"' ");
			} else if(!checkQue) {
				sbQuery.append(",filepath= "+null+" ");
			}
			sbQuery.append(" WHERE medical_id = ?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setBoolean(1, checkQue);
			pst.setString(2, queDesc);
//			pst.setString(3, strFileName); 
			pst.setInt(3, uF.parseToInt(medicalid));
//			System.out.println("pst ==>> " +pst);
			int x = pst.executeUpdate();
			pst.close();

			if (x > 0) {
				/**
				 * Log Details
				 * */
				String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
				String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
				String strProcessMsg = uF.showData(strProcessByName, "") + " has updated medical details of " + uF.showData(strEmpName, "") + " on " + ""
						+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
						+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
				LogDetails logDetails = new LogDetails();
				logDetails.session = session;
				logDetails.CF = CF;
				logDetails.request = request;
				logDetails.setProcessId(uF.parseToInt(getEmpId()));
				logDetails.setProcessType(L_EMPLOYEE);
				logDetails.setProcessActivity(L_UPDATE);
				logDetails.setProcessMsg(strProcessMsg);
				logDetails.setProcessStep(uF.parseToInt(getStep()));
				logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
				logDetails.insertLog(con, uF);
			}

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
	

	private void insertEmpMedicalInfo1(Connection con, UtilityFunctions uF,String queId,boolean checkQue,String queDesc,File file,String fileName1) {
		PreparedStatement pst = null;
		try {
			String strFileName = null;
			if (file != null) {
				if (CF.getStrDocSaveLocation() == null) {
					strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, file, fileName1, fileName1, CF);
				} else {
					strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_PEOPLE + "/" + I_DOCUMENT + "/" + I_MEDICAL + "/"
							+ getEmpId(), file, fileName1, fileName1, CF);
				}
			}

			pst = con.prepareStatement("INSERT INTO emp_medical_details (question_id, emp_id, yes_no, description,filepath) values(?,?,?,?,?)");
			pst.setInt(1, uF.parseToInt(queId));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			pst.setBoolean(3, checkQue);
			pst.setString(4, queDesc);
			pst.setString(5, strFileName);
			int x = pst.executeUpdate();
			pst.close();

			if (x > 0) {
				/**
				 * Log Details
				 * */
				String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
				String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
				String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted medical details of " + uF.showData(strEmpName, "") + " on " + ""
						+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
						+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
				LogDetails logDetails = new LogDetails();
				logDetails.session = session;
				logDetails.CF = CF;
				logDetails.request = request;
				logDetails.setProcessId(uF.parseToInt(getEmpId()));
				logDetails.setProcessType(L_EMPLOYEE);
				logDetails.setProcessActivity(L_ADD);
				logDetails.setProcessMsg(strProcessMsg);
				logDetails.setProcessStep(uF.parseToInt(getStep()));
				logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
				logDetails.insertLog(con, uF);
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

	
	private void insertEmpMedicalInfo(Connection con, UtilityFunctions uF) {
		
//		PreparedStatement pst = null;
		File file1=null;
		String filename=null;
		int fileSize=1;
		if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(0).equals("1")) {
			 file1=getQue1DescFile().get(fileSize-1);
			 filename=getQue1DescFileFileName().get(fileSize-1);
			 fileSize++;
		}
		insertEmpMedicalInfo1(con,uF,getQue1Id(),isCheckQue1(),getQue1Desc(),file1,filename);
		
		file1=null;
		 filename=null;
		if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(1).equals("1")) {
			 file1=getQue1DescFile().get(fileSize-1);
			 filename=getQue1DescFileFileName().get(fileSize-1);
			 fileSize++;
		}
		insertEmpMedicalInfo1(con,uF,getQue2Id(),isCheckQue2(),getQue2Desc(),file1,filename);
		
		
		file1=null;
		 filename=null;
		if(getQue1DescFile()!=null && getQue1DescFile().size()>=fileSize && getQue1IdFileStatus().get(2).equals("1")) {
			 file1=getQue1DescFile().get(fileSize-1);
			 filename=getQue1DescFileFileName().get(fileSize-1);
			 fileSize++;
		}
		insertEmpMedicalInfo1(con,uF,getQue3Id(),isCheckQue3(),getQue3Desc(),file1,filename);
		
		
//		file1=null;
//		 filename=null;
//		if(getQue1DescFile()!=null && getQue1DescFile().size()>=4 && getQue1IdFileStatus().get(3).equals("1")) {
//			 file1=getQue1DescFile().get(3);
//			 filename=getQue1DescFileFileName().get(3);
//		}
//		insertEmpMedicalInfo1(con,uF,getQue4Id(),isCheckQue4(),getQue4Desc(),file1,filename);
//		file1=null;
//		 filename=null;
//		if(getQue1DescFile()!=null && getQue1DescFile().size()>=5 && getQue1IdFileStatus().get(4).equals("1")) {
//			 file1=getQue1DescFile().get(4);
//			 filename=getQue1DescFileFileName().get(4);
//		}
//		insertEmpMedicalInfo1(con,uF,getQue5Id(),isCheckQue5(),getQue5Desc(),file1,filename);
//		try {
//			String fileName =null;
//			if(getQue1DescFile()!=null) {
//			 fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getQue1DescFile(), getQue1DescFileFileName());
//			 }
//			pst = con.prepareStatement("INSERT INTO emp_medical_details (question_id, emp_id, yes_no, description,filepath) values (?,?,?,?,?)");
//			pst.setInt(1, uF.parseToInt(getQue1Id()));
//			pst.setInt(2, getEmpId());
//			pst.setBoolean(3, isCheckQue1());
//			pst.setString(4, getQue1Desc());
//			pst.setString(5, fileName);
//			
//			log.debug("pst ==>"+pst);
//			pst.execute();
//			
//			fileName =null;
//			if(getQue2DescFile()!=null) {
//			 fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getQue2DescFile(), getQue1DescFileFileName());
//			 }
//			pst = con.prepareStatement("INSERT INTO emp_medical_details (question_id, emp_id, yes_no, description,filepath) values (?,?,?,?,?)");
//			pst.setInt(1, uF.parseToInt(getQue2Id()));
//			pst.setInt(2, getEmpId());
//			pst.setBoolean(3, isCheckQue2());
//			pst.setString(4, getQue2Desc());
//			pst.setString(5, fileName);
//			log.debug("pst ==>"+pst);
//			pst.execute();
//			
//			fileName =null;
//			if(getQue3DescFile()!=null) {
//			 fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getQue3DescFile(), getQue1DescFileFileName());
//			 }
//			pst = con.prepareStatement("INSERT INTO emp_medical_details (question_id, emp_id, yes_no, description,filepath) values (?,?,?,?,?)");
//			pst.setInt(1, uF.parseToInt(getQue3Id()));
//			pst.setInt(2, getEmpId());
//			pst.setBoolean(3, isCheckQue3());
//			pst.setString(4, getQue3Desc());
//			pst.setString(5, fileName);
//			log.debug("pst ==>"+pst);
//			pst.execute();
//			
//			fileName =null;
//			if(getQue4DescFile()!=null) {
//			 fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getQue4DescFile(), getQue1DescFileFileName());
//			 }
//			pst = con.prepareStatement("INSERT INTO emp_medical_details (question_id, emp_id, yes_no, description,filepath) values (?,?,?,?,?)");
//			pst.setInt(1, uF.parseToInt(getQue4Id()));
//			pst.setInt(2, getEmpId());
//			pst.setBoolean(3, isCheckQue4());
//			pst.setString(4, getQue4Desc());
//			pst.setString(5, fileName);
//			log.debug("pst ==>"+pst);
//			pst.execute();
//			
//			fileName =null;
//			if(getQue5DescFile()!=null) {
//			 fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getQue5DescFile(), getQue1DescFileFileName());
//			 }
//			pst = con.prepareStatement("INSERT INTO emp_medical_details (question_id, emp_id, yes_no, description,filepath) values (?,?,?,?,?)");
//			pst.setInt(1, uF.parseToInt(getQue5Id()));
//			pst.setInt(2, getEmpId());
//			pst.setBoolean(3, isCheckQue5());
//			pst.setString(4, getQue5Desc());
//			pst.setString(5, fileName);
//			log.debug("pst ==>"+pst);
//			pst.execute();
//		
//			
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	private void sendNotifications() {
		
//		Notifications nF = new Notifications(N_NEW_EMPLOYEE);
//		nF.setStrEmailTo(getEmpEmail());
//		nF.setStrEmpMobileNo(getEmpContactno());
//		nF.setStrEmpCode(getEmpCode());
//		nF.setStrEmpFname(getEmpFname());
//		nF.setStrEmpLname(getEmpLname());
//		nF.setStrUserName(getUserName());
//		nF.setStrPassword(getEmpPassword());
//		nF.sendNotifications();
		
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

	public void insertEmpPrevEmploment(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		try {

			if (getPrevCompanyName() != null && getPrevCompanyName().length != 0) {

				for (int i = 0; i < getPrevCompanyName().length; i++) {

					if (getPrevCompanyName()[i].length() != 0) {

				//===start parvez date: 08-08-2022===		
//						pst = con.prepareStatement("INSERT INTO emp_prev_employment(company_name, company_location, company_city, company_state, "
//								+ "company_country, company_contact_no, reporting_to, from_date, to_date, designation, responsibilities, skills, emp_id, "
//								+ "report_manager_ph_no, hr_manager, hr_manager_ph_no)" + "VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
						pst = con.prepareStatement("INSERT INTO emp_prev_employment(company_name, company_location, company_city, company_state, "
								+ "company_country, company_contact_no, reporting_to, from_date, to_date, designation, responsibilities, skills, emp_id, "
								+ "report_manager_ph_no, hr_manager, hr_manager_ph_no, emp_esic_no, uan_no)" + "VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
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
						pst.setInt(13, uF.parseToInt(getEmpId()));
						pst.setString(14, getPrevCompanyReportManagerPhNo()[i]);
						pst.setString(15, getPrevCompanyHRManager()[i]);
						pst.setString(16, getPrevCompanyHRManagerPhNo()[i]);
					//===start parvez date: 08-08-2022===	
						pst.setString(17, getPrevCompanyESICNo()[i]);
						pst.setString(18, getPrevCompanyUANNo()[i]);
					//===end parvez date: 08-08-2022===	
						int x = pst.executeUpdate();
						pst.close();

						if (x > 0) {
							/**
							 * Log Details
							 * */
							String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
							String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
							
							String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted previous employment ("
									+ uF.showData(getPrevCompanyName()[i], "") + ") of " + uF.showData(strEmpName, "") + " on " + ""
									+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
									+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
							LogDetails logDetails = new LogDetails();
							logDetails.session = session;
							logDetails.CF = CF;
							logDetails.request = request;
							logDetails.setProcessId(uF.parseToInt(getEmpId()));
							logDetails.setProcessType(L_EMPLOYEE);
							logDetails.setProcessActivity(L_ADD);
							logDetails.setProcessMsg(strProcessMsg);
							logDetails.setProcessStep(uF.parseToInt(getStep()));
							logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
							logDetails.insertLog(con, uF);
						}
					}

				}

				if (getPrevEmpFYear() != null && !getPrevEmpFYear().equals("") && getPrevTotalEarning() != null && !getPrevTotalEarning().equals("")
					&& getPrevTotalDeduction() != null && !getPrevTotalDeduction().equals("")) { // && getPrevFileEarnDeduct()!=null && !getPrevFileEarnDeduct().equals("")) { 
					String[] strPayCycleDates = null;
					String strFinancialYearStart = null;
					String strFinancialYearEnd = null;

					if (getPrevEmpFYear() != null) {

						strPayCycleDates = getPrevEmpFYear().split("-");
						strFinancialYearStart = strPayCycleDates[0];
						strFinancialYearEnd = strPayCycleDates[1];

					} else {
						strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
						setPrevEmpFYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);

						strFinancialYearStart = strPayCycleDates[0];
						strFinancialYearEnd = strPayCycleDates[1];
					}

					if(uF.parseToBoolean(getIsTdsDetails())) {
						String filename = null;
						if(getPrevFileEarnDeduct()!=null) {
							if (CF.getStrDocSaveLocation() != null) {
								filename = uF.uploadFile(request, CF.getStrDocSaveLocation(), getPrevFileEarnDeduct(), getPrevFileEarnDeductFileName(), CF.getIsRemoteLocation(), CF);
							} else {
								filename = uF.uploadFile(request, DOCUMENT_LOCATION, getPrevFileEarnDeduct(), getPrevFileEarnDeductFileName(), CF.getIsRemoteLocation(), CF);
							}
						}
	
				//===start parvez date: 31-03-2022===		
						
//						pst = con.prepareStatement("insert into prev_earn_deduct_details(emp_id,gross_amount,tds_amount,financial_start,financial_end,"
//								+ "document_name,added_by,added_on)values(?,?,?,?,?,?,?,?)");
						
						pst = con.prepareStatement("insert into prev_earn_deduct_details(emp_id,gross_amount,tds_amount,financial_start,financial_end,"
								+ "document_name,added_by,added_on,org_pan_no,org_tan_no)values(?,?,?,?, ?,?,?,?, ?,?)");
				//===end parvez date: 31-03-2022===		
						pst.setInt(1, uF.parseToInt(getEmpId()));
						pst.setDouble(2, uF.parseToDouble(getPrevTotalEarning()));
						pst.setDouble(3, uF.parseToDouble(getPrevTotalDeduction()));
						pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setString(6, filename);
						pst.setInt(7, uF.parseToInt(strSessionEmpId));
						pst.setTimestamp(8,uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE + DBTIME));
				//===start parvez date: 31-03-2022===		
						pst.setString(9, getPrevPANNumber());
						pst.setString(10, getPrevTANNumber());
				//===end parvez date: 31-03-2022===		
						pst.execute();
						pst.close();
					}
					
				}
			}
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
//
//		PreparedStatement pst = null;
//
//		try {
//
//			String filePath1 = request.getRealPath("/userDocuments/");
//			String fileName1 = "";
//
//			if (getIdDoc() != null && getIdDoc().size() != 0) {
//
//				for (int i = 0; i < getIdDoc().size(); i++) {
//
//					if (getIdDoc().get(i) != null & getIdDoc().get(i).length() != 0) {
//
//						// String fileName = uF.uploadFile(request,
//						// DOCUMENT_LOCATION, getIdDoc().get(i),
//						// getIdDocFileName().get(i));
//						String strFileName = null;
//						if (CF.getStrDocSaveLocation() == null) {
//							strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getIdDoc().get(i), getIdDocFileName().get(i), getIdDocFileName()
//									.get(i), CF);
//						} else {
//							strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_PEOPLE + "/" + I_DOCUMENT + "/" + I_ATTACHMENT + "/"
//									+ getEmpId(), getIdDoc().get(i), getIdDocFileName().get(i), getIdDocFileName().get(i), CF);
//						}
//						pst = con.prepareStatement(insertDocuments);
//						pst.setString(1, getIdDocName()[i]);
//						pst.setString(2, getIdDocType()[i]);
//						pst.setInt(3, getEmpId());
//						pst.setString(4, strFileName);
//						pst.setInt(5, uF.parseToInt(strSessionEmpId));
//						pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
//						int x = pst.executeUpdate();
//						pst.close();
//
//						if (x > 0) {
//							/**
//							 * Log Details
//							 * */
//							String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
//							String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
//							String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted document (" + uF.showData(getIdDocName()[i], "")
//									+ ") of " + uF.showData(strEmpName, "") + " on " + ""
//									+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
//									+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
//							LogDetails logDetails = new LogDetails();
//							logDetails.session = session;
//							logDetails.CF = CF;
//							logDetails.request = request;
//							logDetails.setProcessId(getEmpId());
//							logDetails.setProcessType(L_EMPLOYEE);
//							logDetails.setProcessActivity(L_ADD);
//							logDetails.setProcessMsg(strProcessMsg);
//							logDetails.setProcessStep(getStep());
//							logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
//							logDetails.insertLog(con, uF);
//						}
//					}
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		} finally {
//			if (pst != null) {
//				try {
//					pst.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//
//	}

	public void insertHobbies(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;

		try {

			if (getHobbyName() != null && getHobbyName().length != 0) {

				for (String h : hobbyName) {

					if (h != null && h.length() != 0 && h != "") {

						pst = con.prepareStatement(insertHobbies);
						pst.setString(1, h);
						pst.setInt(2, uF.parseToInt(getEmpId()));
						// log.debug("pst==>>"+pst);
						int x = pst.executeUpdate();
						pst.close();

						if (x > 0) {
							/**
							 * Log Details
							 * */
							String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
							String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
							String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted hobby (" + uF.showData(h, "") + ") of "
									+ uF.showData(strEmpName, "") + " on " + ""
									+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
									+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
							LogDetails logDetails = new LogDetails();
							logDetails.session = session;
							logDetails.CF = CF;
							logDetails.request = request;
							logDetails.setProcessId(uF.parseToInt(getEmpId()));
							logDetails.setProcessType(L_EMPLOYEE);
							logDetails.setProcessActivity(L_ADD);
							logDetails.setProcessMsg(strProcessMsg);
							logDetails.setProcessStep(uF.parseToInt(getStep()));
							logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
							logDetails.insertLog(con, uF);
						}
					}
				}
			}

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

	public void insertSkills(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;

		try {
			Map<String, String> hmSkillName = CF.getSkillNameMap(con);
			if (hmSkillName == null)
				hmSkillName = new HashMap<String, String>();

			for (int i = 0; i < getSkillName().length; i++) {
				if (getSkillName()[i].length() != 0) {
					pst = con.prepareStatement(insertSkill);
					pst.setInt(1, uF.parseToInt(getSkillName()[i]));
					pst.setString(2, getSkillValue()[i]);
					pst.setInt(3, uF.parseToInt(getEmpId()));
					// log.debug("pst==>>"+pst);
					int x = pst.executeUpdate();
					pst.close();

					if (x > 0) {
						/**
						 * Log Details
						 * */
						String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
						String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
						String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted skills ("
								+ uF.showData(hmSkillName.get(getSkillName()[i]), "") + ") of " + uF.showData(strEmpName, "") + " on " + ""
								+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
								+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
						LogDetails logDetails = new LogDetails();
						logDetails.session = session;
						logDetails.CF = CF;
						logDetails.request = request;
						logDetails.setProcessId(uF.parseToInt(getEmpId()));
						logDetails.setProcessType(L_EMPLOYEE);
						logDetails.setProcessActivity(L_ADD);
						logDetails.setProcessMsg(strProcessMsg);
						logDetails.setProcessStep(uF.parseToInt(getStep()));
						logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
						logDetails.insertLog(con, uF);
					}
				}
			}

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

	public void insertProbationPeriod(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			StringBuilder sbProbationLeaves = new StringBuilder();
			int i = 0;
			if (getProbationLeaves() != null) {
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
			if (joiningDate != null && !joiningDate.equals("")) {
				if (getProbationDuration() > 0) {
					// lastProbationEndDate =
					// uF.getFutureDate(uF.getDateFormat(joiningDate, DBDATE),
					// getProbationDuration());
					probEndDate = uF.getFutureDate(uF.getDateFormat(getEmpStartDate(), DATE_FORMAT), (getProbationDuration() - 1));
				}
			}

			// System.out.println("joiningDate==>"+joiningDate+"==>getProbationDuration()==>"+getProbationDuration());

			pst = con.prepareStatement("INSERT INTO probation_policy(emp_id, leaves_types_allowed, probation_duration, notice_duration,probation_end_date) VALUES(?,?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setString(2, sbProbationLeaves.toString());
			pst.setInt(3, getProbationDuration());
			pst.setInt(4, getNoticeDuration());
			pst.setDate(5, probEndDate);
			pst.execute();
			pst.close();

			if (getProbationDuration() > 0 && uF.parseToInt(getEmpStatus()) == 1) {

				Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
				Date probEndDate1 = uF.getFutureDate(uF.getDateFormat(getEmpStartDate(), DATE_FORMAT), (getProbationDuration() - 1));

				int dateResult = probEndDate1.compareTo(currDate);
				if (dateResult >= 0) {
					pst = con.prepareStatement("update employee_personal_details set emp_status = ? where emp_per_id = ?");
					pst.setString(1, PROBATION);
					pst.setInt(2, uF.parseToInt(getEmpId()));
					int x = pst.executeUpdate();
					pst.close();

					if (x > 0) {
						/**
						 * Log Details
						 * */
						String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
						String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
						String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted probation details of " + uF.showData(strEmpName, "") + " on "
								+ "" + uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
								+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
						LogDetails logDetails = new LogDetails();
						logDetails.session = session;
						logDetails.CF = CF;
						logDetails.request = request;
						logDetails.setProcessId(uF.parseToInt(getEmpId()));
						logDetails.setProcessType(L_EMPLOYEE);
						logDetails.setProcessActivity(L_ADD);
						logDetails.setProcessMsg(strProcessMsg);
						logDetails.setProcessStep(uF.parseToInt(getStep()));
						logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
						logDetails.insertLog(con, uF);
					}
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

	public void insertUser(Connection con, UtilityFunctions uF, int empId) {

		PreparedStatement pst = null;

		try {
			Map<String, String> userPresent = CF.getUsersMap(con);
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
			pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE + DBTIME));
			pst.execute();
			pst.close();

			/**
			 * Log Details
			 * */
			String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
			String strEmpName = CF.getEmpNameMapByEmpId(con, "" + empId);
			String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted login details of " + uF.showData(strEmpName, "") + " on " + ""
					+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
					+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
			LogDetails logDetails = new LogDetails();
			logDetails.session = session;
			logDetails.CF = CF;
			logDetails.request = request;
			logDetails.setProcessId(empId);
			logDetails.setProcessType(L_EMPLOYEE);
			logDetails.setProcessActivity(L_ADD);
			logDetails.setProcessMsg(strProcessMsg);
			logDetails.setProcessStep(uF.parseToInt(getStep()));
			logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
			logDetails.insertLog(con, uF);

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
	
	public void insertEmpOfficialDetails(Connection con, UtilityFunctions uF, StringBuilder sbServices) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			pst = con.prepareStatement("INSERT INTO employee_official_details (depart_id, supervisor_emp_id,hod_emp_id, service_id, available_days, "
					+ "emp_id, wlocation_id, is_roster, is_attendance, emptype, first_aid_allowance, grade_id,paycycle_duration,payment_mode, "
					+ "org_id,emp_hr,biometrix_id,is_form16,is_form16_a,corporate_mobile_no,corporate_desk,emp_contractor,is_hod,is_cxo,slab_type) "
					+ "VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getDepartment()));
			pst.setInt(2, uF.parseToInt(getSupervisor()));
			pst.setInt(3, uF.parseToInt(getHod()));
			pst.setString(4, sbServices.toString());
			pst.setString(5, "");
			pst.setInt(6, uF.parseToInt(getEmpId()));
			pst.setInt(7, uF.parseToInt(getwLocation()));
			pst.setBoolean(8, uF.parseToBoolean(getRosterDependency()));
			pst.setBoolean(9, uF.parseToBoolean(getAttendanceDependency()));
			pst.setString(10, getEmpType());
			pst.setBoolean(11, getIsFirstAidAllowance());
			pst.setInt(12, uF.parseToInt(getEmpGrade()));
			pst.setString(13, getStrPaycycleDuration());
			pst.setInt(14, uF.parseToInt(getEmpPaymentMode()));
			pst.setInt(15, uF.parseToInt(getOrgId()));
			pst.setInt(16, uF.parseToInt(getHR()));
			pst.setInt(17, uF.parseToInt(getBioId()));
			pst.setBoolean(18, uF.parseToBoolean(getIsForm16()));
			pst.setBoolean(19, uF.parseToBoolean(getIsForm16A()));
			pst.setString(20, getEmpCorporateMobileNo());
			pst.setString(21, getEmpCorporateDesk());
			pst.setInt(22, uF.parseToInt(getEmpContractor()));
			pst.setBoolean(23, (uF.parseToInt(getStrCXOHOD()) == 2) ? true : false);
			pst.setBoolean(24, (uF.parseToInt(getStrCXOHOD()) == 1) ? true : false);
			pst.setInt(25, uF.parseToInt(getSlabType()));


			pst.execute();
//			System.out.println("insert pst ===>"+pst);
			pst.close();

			if (uF.parseToInt(getStrCXOHOD()) == 2) {
				Map<String, String> hmUserTypeID = CF.getUserTypeIdMap(con);
				pst = con.prepareStatement("UPDATE user_details SET usertype_id=? WHERE emp_id=?");
				pst.setInt(1, uF.parseToInt(hmUserTypeID.get(MANAGER)));
				pst.setInt(2, uF.parseToInt(getEmpId()));
				pst.executeUpdate();
				pst.close();
			}

			if (uF.parseToInt(getStrCXOHOD()) == 1) {
				StringBuilder sbwLocId = null;
				for (int i = 0; locationCXO != null && i < locationCXO.length; i++) {
					if (uF.parseToInt(locationCXO[i]) > 0) {
						if (sbwLocId == null) {
							sbwLocId = new StringBuilder();
							sbwLocId.append("," + locationCXO[i] + ",");
						} else {
							sbwLocId.append(locationCXO[i] + ",");
						}
					}
				}
				if (uF.parseToInt(getOrgId()) > 0 && (locationCXO == null || locationCXO.length == 0)) {
					pst = con.prepareStatement("select wlocation_id from work_location_info where org_id in(" + getOrgId() + ")");
					rs = pst.executeQuery();
					while (rs.next()) {
						if (rs.getInt("wlocation_id") > 0) {
							if (sbwLocId == null) {
								sbwLocId = new StringBuilder();
								sbwLocId.append("," + rs.getInt("wlocation_id") + ",");
							} else {
								sbwLocId.append(rs.getInt("wlocation_id") + ",");
							}
						}
					}
					rs.close();
					pst.close();
				}
				if (sbwLocId == null)
					sbwLocId = new StringBuilder();

				Map<String, String> hmUserTypeID = CF.getUserTypeIdMap(con);
				pst = con.prepareStatement("UPDATE user_details SET org_id_access=?,wlocation_id_access=?,usertype_id=? WHERE emp_id=?");
				pst.setString(1, "," + getOrgId() + ",");
				pst.setString(2, sbwLocId.toString());
				pst.setInt(3, uF.parseToInt(hmUserTypeID.get(MANAGER)));
				pst.setInt(4, uF.parseToInt(getEmpId()));
				pst.executeUpdate();
				pst.close();
			}

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

	public String insertEmpPersonalDetails(Connection con, UtilityFunctions uF, CommonFunctions CF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		String empPerId = "";

		try {

			/*String strEmpStatus = PERMANENT;
			if(uF.parseToInt(getEmpStatus()) == 1){
				strEmpStatus = PROBATION;
			} else if(uF.parseToInt(getEmpStatus()) == 2){
				strEmpStatus = PERMANENT;
			} else if(uF.parseToInt(getEmpStatus()) == 4){
				strEmpStatus = TEMPORARY;
			}*/
			pst = con.prepareStatement(insertEmployeeP);
			pst.setString(1, uF.showData(getEmpCodeAlphabet(), "") + uF.showData(getEmpCodeNumber(), ""));
			pst.setString(2, getEmpFname());
			pst.setString(3, getEmpMname());
			pst.setString(4, getEmpLname());
			pst.setString(5, getEmpEmail());

			pst.setString(6, getEmpAddress1());
			pst.setString(7, getEmpAddress2());
			pst.setString(8, getCity());
			pst.setInt(9, uF.parseToInt(getState()));
			pst.setInt(10, uF.parseToInt(getCountry()));
			pst.setString(11, getEmpPincode());

			pst.setString(12, getEmpAddress1Tmp());
			pst.setString(13, getEmpAddress2Tmp());
			pst.setString(14, getCityTmp());
			pst.setInt(15, uF.parseToInt(getStateTmp()));
			pst.setInt(16, uF.parseToInt(getCountryTmp()));
			pst.setString(17, getEmpPincodeTmp());

			pst.setString(18, getEmpContactno());
			pst.setDate(19, uF.getDateFormat(getEmpStartDate(), DATE_FORMAT));
			pst.setString(20, getEmpPanNo());
			pst.setString(21, getEmpPFNo());
			pst.setString(22, getEmpGPFNo());
			pst.setString(23, getEmpGender());
			pst.setDate(24, uF.getDateFormat(getEmpDateOfBirth(), DATE_FORMAT));
			pst.setString(25, getEmpBankName());
			pst.setString(26, getEmpBankAcctNbr());
			
			pst.setString(27, getEmpEmailSec());
			pst.setString(28, getSkypeId());
			pst.setString(29, getEmpMobileNo());
			pst.setString(30, getEmpEmergencyContactName());
			pst.setString(31, getEmpEmergencyContactNo());
			pst.setString(32, getEmpPassportNo());
			pst.setDate(33, uF.getDateFormat(getEmpPassportExpiryDate(), DATE_FORMAT));
			pst.setString(34, getEmpBloodGroup());
			pst.setString(35, getEmpMaritalStatus());
			pst.setDate(36, uF.getDateFormat(getEmpDateOfMarriage(), DATE_FORMAT));
			pst.setBoolean(37, false);
			pst.setTimestamp(38, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.setString(39, PROBATION);
			pst.setString(40, getSalutation());
			pst.setString(41, getEmpDoctorName());
			pst.setString(42, getEmpDoctorNo());
			pst.setString(43, getEmpUIDNo());
			pst.setString(44, getEmpUANNo());
			pst.setString(45, getEmpESICNo());
			pst.setInt(46, uF.parseToInt(strSessionEmpId));
			pst.setString(47, getEmpBankName2());
			pst.setString(48, getEmpBankAcctNbr2());
			pst.setBoolean(49, isMedicalProfessional());
			pst.setString(50, getStrKmcNo());
			pst.setString(51, getStrKncNo());
			pst.setDate(52, uF.getDateFormat(getStrRenewalDate(), DATE_FORMAT));
			pst.setString(53, getEmpMRDNo());
			
			pst.setString(54, getEmpBankIFSCNbr());
			pst.setString(55, getEmpBankIFSCNbr2());
			pst.setDate(56, uF.getDateFormat(getEmpPFStartDate(), DATE_FORMAT));
	//===start parvez date: 12-08-2022===		
			pst.setString(57, getEmpEmergencyContactRelation());
			pst.setString(58, getEmpOtherBankName());
			pst.setString(59, getEmpOtherBankName2());
	//===end parvez date: 12-08-2022===	
			pst.setString(60, getEmpOtherBankBranch());
			pst.setString(61, getEmpOtherBankBranch2());
			int x = pst.executeUpdate();
			pst.close();

			if (x > 0) {
				pst = con.prepareStatement(selectMaxEmpId);
				rs = pst.executeQuery();
				while (rs.next()) {
					empPerId = rs.getString(1);
					request.setAttribute("strEmpName",
							uF.showData(getEmpFname(), "") + " " + uF.showData(getEmpMname(), "") + " " + uF.showData(getEmpLname(), ""));
				}
				rs.close();
				pst.close();

				pst = con.prepareStatement("INSERT INTO employee_official_details(emp_id) VALUES (?)");
				pst.setInt(1, uF.parseToInt(empPerId));
				pst.execute();
				pst.close();

				if (uF.parseToInt(strSessionEmpId) > 0) {
					String strDomain = request.getServerName().split("\\.")[0];
					Map<String, String> hmEmpUsertypeId = CF.getEmployeeIdUserTypeIdMap(con);
					String alertData = "<div style=\"float: left;\"> <b>" + CF.getEmpNameMapByEmpId(con, empPerId) + "</b> New Joinee Pending.</div>";
					String alertAction = "EmployeeActivity.action?pType=WR";
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(strSessionEmpId);
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(hmEmpUsertypeId.get(strSessionEmpId));
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
				}

				/**
				 * Log Details
				 * */
				String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
				String strEmpName = CF.getEmpNameMapByEmpId(con, "" + empPerId);
				String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted employee personal details of " + uF.showData(strEmpName, "") + " on "
						+ "" + uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
						+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
				LogDetails logDetails = new LogDetails();
				logDetails.session = session;
				logDetails.CF = CF;
				logDetails.request = request;
				logDetails.setProcessId(uF.parseToInt(empPerId));
				logDetails.setProcessType(L_EMPLOYEE);
				logDetails.setProcessActivity(L_ADD);
				logDetails.setProcessMsg(strProcessMsg);
				logDetails.setProcessStep(uF.parseToInt(getStep()));
				logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
				logDetails.insertLog(con, uF);
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

		return empPerId;

	}

	public void insertEmpFamilyMembers(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;

		try {

			if (getMotherName() != null && getMotherName().length() > 0) {

				pst = con.prepareStatement("INSERT INTO emp_family_members(member_type, member_name, member_dob, member_education, "
						+ "member_occupation, member_contact_no, member_email_id, member_gender,member_marital, emp_id,mrd_no)"
						+ "VALUES (?,?,?,?,?,?,?,?,?,?,?)");

				pst.setString(1, MOTHER);
				pst.setString(2, getMotherName());
				// log.debug(getMotherDob()+"getMotherDob()");
				pst.setDate(3, uF.getDateFormat(getMotherDob(), DATE_FORMAT));
				pst.setString(4, getMotherEducation());
				pst.setString(5, getMotherOccupation());
				pst.setString(6, getMotherContactNumber());
				pst.setString(7, getMotherEmailId());
				pst.setString(8, "F");
				pst.setString(9, "M");
				pst.setInt(10, uF.parseToInt(getEmpId()));
				pst.setString(11, getMotherMRDNo());
				// log.debug("pst=>"+pst);
				int x = pst.executeUpdate();
				pst.close();

				if (x > 0) {
					/**
					 * Log Details
					 * */
					String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
					String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
					String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted mother name (" + uF.showData(getMotherName(), "") + ") of "
							+ uF.showData(strEmpName, "") + " on " + ""
							+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
							+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
					LogDetails logDetails = new LogDetails();
					logDetails.session = session;
					logDetails.CF = CF;
					logDetails.request = request;
					logDetails.setProcessId(uF.parseToInt(getEmpId()));
					logDetails.setProcessType(L_EMPLOYEE);
					logDetails.setProcessActivity(L_ADD);
					logDetails.setProcessMsg(strProcessMsg);
					logDetails.setProcessStep(uF.parseToInt(getStep()));
					logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
					logDetails.insertLog(con, uF);
				}
			}

			if (getFatherName() != null && getFatherName().length() > 0) {

				pst = con.prepareStatement("INSERT INTO emp_family_members(member_type, member_name, member_dob, member_education, "
						+ "member_occupation, member_contact_no, member_email_id, member_gender,member_marital, emp_id,mrd_no)"
						+ "VALUES (?,?,?,?,?,?,?,?,?,?,?)");

				pst.setString(1, FATHER);
				pst.setString(2, getFatherName());
				pst.setDate(3, uF.getDateFormat(getFatherDob(), DATE_FORMAT));
				pst.setString(4, getFatherEducation());
				pst.setString(5, getFatherOccupation());
				pst.setString(6, getFatherContactNumber());
				pst.setString(7, getFatherEmailId());
				pst.setString(8, "M");
				pst.setString(9, "M");
				pst.setInt(10, uF.parseToInt(getEmpId()));
				pst.setString(11, getFatherMRDNo());
				int x = pst.executeUpdate();
				pst.close();

				if (x > 0) {
					/**
					 * Log Details
					 * */
					String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
					String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
					String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted father name(" + uF.showData(getFatherName(), "") + ") of "
							+ uF.showData(strEmpName, "") + " on " + ""
							+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
							+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
					LogDetails logDetails = new LogDetails();
					logDetails.session = session;
					logDetails.CF = CF;
					logDetails.request = request;
					logDetails.setProcessId(uF.parseToInt(getEmpId()));
					logDetails.setProcessType(L_EMPLOYEE);
					logDetails.setProcessActivity(L_ADD);
					logDetails.setProcessMsg(strProcessMsg);
					logDetails.setProcessStep(uF.parseToInt(getStep()));
					logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
					logDetails.insertLog(con, uF);
				}
			}

			if (getSpouseName() != null && getSpouseName().length() > 0) {

				pst = con.prepareStatement("INSERT INTO emp_family_members(member_type, member_name, member_dob, member_education, "
						+ "member_occupation, member_contact_no, member_email_id, member_gender,member_marital, emp_id,mrd_no)"
						+ "VALUES (?,?,?,?,?,?,?,?,?,?,?)");

				pst.setString(1, SPOUSE);
				pst.setString(2, getSpouseName());
				pst.setDate(3, uF.getDateFormat(getSpouseDob(), DATE_FORMAT));
				pst.setString(4, getSpouseEducation());
				pst.setString(5, getSpouseOccupation());
				pst.setString(6, getSpouseContactNumber());
				pst.setString(7, getSpouseEmailId());
				pst.setString(8, getSpouseGender());

				pst.setString(9, "M"); //getSpouseMaritalStatus()
				pst.setInt(10, uF.parseToInt(getEmpId()));
				pst.setString(11, getSpouseMRDNo());
				// log.debug("pst=>"+pst);
				int x = pst.executeUpdate();
				pst.close();

				if (x > 0) {
					/**
					 * Log Details
					 * */
					String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
					String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
					String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted spouse name(" + uF.showData(getSpouseName(), "") + ") of "
							+ uF.showData(strEmpName, "") + " on " + ""
							+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
							+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
					LogDetails logDetails = new LogDetails();
					logDetails.session = session;
					logDetails.CF = CF;
					logDetails.request = request;
					logDetails.setProcessId(uF.parseToInt(getEmpId()));
					logDetails.setProcessType(L_EMPLOYEE);
					logDetails.setProcessActivity(L_ADD);
					logDetails.setProcessMsg(strProcessMsg);
					logDetails.setProcessStep(uF.parseToInt(getStep()));
					logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
					logDetails.insertLog(con, uF);
				}
			}

			for (int i = 0; i < getMemberName().length; i++) {
				if (getMemberName()[i] != null && getMemberName()[i].length() > 0) {

					pst = con.prepareStatement("INSERT INTO emp_family_members(member_type, member_name, member_dob, member_education, "
							+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id,member_marital,mrd_no)"
							+ "VALUES (?,?,?,?,?,?,?,?,?,?,?)");

					pst.setString(1, SIBLING);
					pst.setString(2, getMemberName()[i]);
					pst.setDate(3, uF.getDateFormat(getMemberDob()[i], DATE_FORMAT));
					pst.setString(4, getMemberEducation()[i]);
					pst.setString(5, getMemberOccupation()[i]);
					pst.setString(6, getMemberContactNumber()[i]);
					pst.setString(7, getMemberEmailId()[i]);
					pst.setString(8, getMemberGender()[i]);
					pst.setInt(9, uF.parseToInt(getEmpId()));
					if (getSiblingMaritalStatus() != null && getSiblingMaritalStatus().length > 0 && getSiblingMaritalStatus()[i] != null
							&& getSiblingMaritalStatus()[i] != "0") {
						pst.setString(10, getSiblingMaritalStatus()[i]);
					} else {
						pst.setString(10, "");
					}

					pst.setString(11, getSiblingsMRDNo()[i]);
					// log.debug("pst=>"+pst);
					int x = pst.executeUpdate();
					pst.close();

					if (x > 0) {
						/**
						 * Log Details
						 * */
						String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
						String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
						String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted sibling name(" + uF.showData(getMemberName()[i], "")
								+ ") of " + uF.showData(strEmpName, "") + " on " + ""
								+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
								+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
						LogDetails logDetails = new LogDetails();
						logDetails.session = session;
						logDetails.CF = CF;
						logDetails.request = request;
						logDetails.setProcessId(uF.parseToInt(getEmpId()));
						logDetails.setProcessType(L_EMPLOYEE);
						logDetails.setProcessActivity(L_ADD);
						logDetails.setProcessMsg(strProcessMsg);
						logDetails.setProcessStep(uF.parseToInt(getStep()));
						logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
						logDetails.insertLog(con, uF);
					}
				}
			}

			for (int i = 0; i < getChildName().length; i++) {
				if (getChildName()[i] != null && getChildName()[i].length() > 0) {

					pst = con.prepareStatement("INSERT INTO emp_family_members(member_type, member_name, member_dob, member_education, "
							+ "member_occupation, member_contact_no, member_email_id, member_gender, emp_id,member_marital,mrd_no)"
							+ "VALUES (?,?,?,?,?,?,?,?,?,?,?)");

					pst.setString(1, CHILD);
					pst.setString(2, getChildName()[i]);
					pst.setDate(3, uF.getDateFormat(getChildDob()[i], DATE_FORMAT));
					pst.setString(4, getChildEducation()[i]);
					pst.setString(5, getChildOccupation()[i]);
					pst.setString(6, getChildContactNumber()[i]);
					pst.setString(7, getChildEmailId()[i]);
					pst.setString(8, getChildGender()[i]);
					pst.setInt(9, uF.parseToInt(getEmpId()));

					if (getChildMaritalStatus() != null && getChildMaritalStatus().length > 0 && getChildMaritalStatus()[i] != null
							&& !getChildMaritalStatus()[i].trim().equals("0")) {
						pst.setString(10, getChildMaritalStatus()[i]);
					} else {
						pst.setString(10, "");
					}

					pst.setString(11, getChildMRDNo()[i]);
					int x = pst.executeUpdate();
					pst.close();

					if (x > 0) {
						/**
						 * Log Details
						 * */
						String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
						String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
						String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted child name(" + uF.showData(getChildName()[i], "") + ") of "
								+ uF.showData(strEmpName, "") + " on " + ""
								+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
								+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
						LogDetails logDetails = new LogDetails();
						logDetails.session = session;
						logDetails.CF = CF;
						logDetails.request = request;
						logDetails.setProcessId(uF.parseToInt(getEmpId()));
						logDetails.setProcessType(L_EMPLOYEE);
						logDetails.setProcessActivity(L_ADD);
						logDetails.setProcessMsg(strProcessMsg);
						logDetails.setProcessStep(uF.parseToInt(getStep()));
						logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
						logDetails.insertLog(con, uF);
					}
				}
			}
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

	public void insertEmpEducation(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {

			if (getDegreeName() != null && getDegreeName().length != 0) {
				MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
				String[] degreeNameOther = request.getParameterValues("degreeNameOther");
				for (int i = 0; i < getDegreeName().length; i++) {
					if (getDegreeName()[i].length() != 0) {

						if (getDegreeName()[i].equalsIgnoreCase("other")) {
							int newOrgId = 0;
							pst = con.prepareStatement("select org_id from employee_official_details where emp_id=?");
							pst.setInt(1, uF.parseToInt(getEmpId()));
							rs = pst.executeQuery();
							while (rs.next()) {
								newOrgId = rs.getInt("org_id");
							}
							rs.close();
							pst.close();

							pst = con.prepareStatement("insert into educational_details(education_name,education_details,org_id) " + "VALUES (?,?,?)");
							pst.setString(1, degreeNameOther[i]);
							pst.setString(2, "");
							pst.setInt(3, newOrgId);
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

							pst = con.prepareStatement("INSERT INTO education_details(education_id, degree_duration, completion_year, grade, emp_id, " +
								"inst_name) VALUES (?,?,?,?, ?,?)");
							pst.setInt(1, newEduid);
							pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
							pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
							pst.setString(4, getGrade()[i]);
							pst.setInt(5, uF.parseToInt(getEmpId()));
							pst.setString(6, getInstName()[i]);
							int x = pst.executeUpdate();
							pst.close();
							
							int intDegreeId = 0;
							pst = con.prepareStatement("select max(degree_id) as degree_id from education_details");
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
										strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_PEOPLE + "/" + I_DOCUMENT + "/" + I_EDUCATION_DOC + "/"+ getEmpId(), strCertificateDoc[j], strCertificateDocFileName[j], strCertificateDocFileName[j], CF);
									}
									pst = con.prepareStatement("INSERT INTO emp_degree_certificate_details(degree_id, emp_id, degree_certificate_name) VALUES (?,?,?)");
									pst.setInt(1, intDegreeId);
									pst.setInt(2, uF.parseToInt(getEmpId()));
									pst.setString(3, strFileName);
									pst.executeUpdate();
									pst.close();
								}
							}
						
							if (x > 0) {
								/**
								 * Log Details
								 * */
								String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
								String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
								String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted education (" + uF.showData(degreeNameOther[i], "")
									+ ") of " + uF.showData(strEmpName, "") + " on " + ""
									+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
									+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
								LogDetails logDetails = new LogDetails();
								logDetails.session = session;
								logDetails.CF = CF;
								logDetails.request = request;
								logDetails.setProcessId(uF.parseToInt(getEmpId()));
								logDetails.setProcessType(L_EMPLOYEE);
								logDetails.setProcessActivity(L_ADD);
								logDetails.setProcessMsg(strProcessMsg);
								logDetails.setProcessStep(uF.parseToInt(getStep()));
								logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
								logDetails.insertLog(con, uF);
							}
						} else {

							pst = con.prepareStatement("INSERT INTO education_details(education_id, degree_duration, completion_year, grade, emp_id, " +
								"inst_name) VALUES (?,?,?,?, ?,?)");
							pst.setInt(1, uF.parseToInt(getDegreeName()[i]));
							pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
							pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
							pst.setString(4, getGrade()[i]);
							pst.setInt(5, uF.parseToInt(getEmpId()));
							pst.setString(6, getInstName()[i]);
							int x = pst.executeUpdate();
							pst.close();
							
							int intDegreeId = 0;
							pst = con.prepareStatement("select max(degree_id) as degree_id from education_details");
							rs = pst.executeQuery();
							while(rs.next()) {
								intDegreeId = rs.getInt("degree_id");
							}
							rs.close();
							pst.close();
							
//							System.out.println("getDegreeCertiStatus().length ===>> " + getDegreeCertiStatus().length);
//							System.out.println(i+" --- getDegreeCertiStatus()[i] ===>> " + getDegreeCertiStatus()[i]);
							if(uF.parseToInt(getDegreeCertiStatus()[i]) == 1) {
								File[] strCertificateDoc = mpRequest.getFiles("degreeCertificate"+i);
								String[] strCertificateDocFileName = mpRequest.getFileNames("degreeCertificate"+i);
//								System.out.println("strCertificateDoc ===>> " + strCertificateDoc.length);
								for(int j=0; strCertificateDoc != null && j<strCertificateDoc.length; j++) {
									String strFileName = null;
									if (CF.getStrDocSaveLocation() == null) {
										strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, strCertificateDoc[j], strCertificateDocFileName[j], strCertificateDocFileName[j], CF);
									} else {
										strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_PEOPLE + "/" + I_DOCUMENT + "/" + I_EDUCATION_DOC + "/"+ getEmpId(), strCertificateDoc[j], strCertificateDocFileName[j], strCertificateDocFileName[j], CF);
									}
									pst = con.prepareStatement("INSERT INTO emp_degree_certificate_details(degree_id, emp_id, degree_certificate_name) VALUES (?,?,?)");
									pst.setInt(1, intDegreeId);
									pst.setInt(2, uF.parseToInt(getEmpId()));
									pst.setString(3, strFileName);
									pst.executeUpdate();
									pst.close();
								}
							}
							
							
							if (x > 0) {
								/**
								 * Log Details
								 * */
								String strEducation = CF.getDegreeNameByDegreeId(con, "" + uF.parseToInt(getDegreeName()[i]));
								String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
								String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
								String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted education (" + uF.showData(strEducation, "")
									+ ") of " + uF.showData(strEmpName, "") + " on " + ""
									+ uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
									+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, TIME_FORMAT_AM_PM);
								LogDetails logDetails = new LogDetails();
								logDetails.session = session;
								logDetails.CF = CF;
								logDetails.request = request;
								logDetails.setProcessId(uF.parseToInt(getEmpId()));
								logDetails.setProcessType(L_EMPLOYEE);
								logDetails.setProcessActivity(L_ADD);
								logDetails.setProcessMsg(strProcessMsg);
								logDetails.setProcessStep(uF.parseToInt(getStep()));
								logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
								logDetails.insertLog(con, uF);
							}
						}
					}

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

	public void insertEmpLangues(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;

		try {

			if (getLanguageName() != null && getLanguageName().length != 0) {

				for (int i = 0; i < getLanguageName().length; i++) {

					if (getLanguageName()[i].length() != 0 && getLanguageName()[i] != "") {

						pst = con.prepareStatement("INSERT INTO languages_details(language_name, language_read, language_write, language_speak, emp_id, language_mothertounge) VALUES (?,?,?,?, ?,?)");
						pst.setString(1, getLanguageName()[i]);
						pst.setInt(2, uF.parseToInt(getIsRead()[i]));
						pst.setInt(3, uF.parseToInt(getIsWrite()[i]));
						pst.setInt(4, uF.parseToInt(getIsSpeak()[i]));
						pst.setInt(5, uF.parseToInt(getEmpId()));
						pst.setInt(6, uF.parseToInt(getIsMotherTounge()[i]));
						// log.debug("pst=>"+pst);
						int x = pst.executeUpdate();
						pst.close();

						if (x > 0) {
							/**
							 * Log Details
							 * */
							String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
							String strEmpName = CF.getEmpNameMapByEmpId(con, "" + getEmpId());
							String strProcessMsg = uF.showData(strProcessByName, "") + " has inserted language (" + uF.showData(getLanguageName()[i], "")
								+ ") of " + uF.showData(strEmpName, "") + " on " + "" + uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat()) + " " + ""
								+ uF.getTimeFormatStr("" + uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
							LogDetails logDetails = new LogDetails();
							logDetails.session = session;
							logDetails.CF = CF;
							logDetails.request = request;
							logDetails.setProcessId(uF.parseToInt(getEmpId()));
							logDetails.setProcessType(L_EMPLOYEE);
							logDetails.setProcessActivity(L_ADD);
							logDetails.setProcessMsg(strProcessMsg);
							logDetails.setProcessStep(uF.parseToInt(getStep()));
							logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
							logDetails.insertLog(con, uF);
						}
					}
				}
			}

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

	
	private List<List<String>> selectPrevEmploment(Connection con, int empId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		List<List<String>> alPrevEmployment = new ArrayList<List<String>>();

		try {
			pst = con.prepareStatement("SELECT * FROM emp_prev_employment WHERE emp_id = ? order by from_date");
			pst.setInt(1, empId);
			rs = pst.executeQuery();

			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("company_id"));
				alInner.add(rs.getString("company_name"));
				alInner.add(rs.getString("company_location"));
				alInner.add(rs.getString("company_city"));
//				getStatesOptions(con, rs.getString("company_city"), rs.getString("company_state"));
//				getCountryOptions(con, rs.getString("company_city"));
				alInner.add(getStatesOptions(con, rs.getString("company_country"), rs.getString("company_state")));
				alInner.add(getCountryOptions(con, rs.getString("company_country")));
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
			//===start parvez date: 08-08-2022===	
				alInner.add(rs.getString("uan_no"));
				alInner.add(rs.getString("emp_esic_no"));
			//===end parvez date: 08-08-2022===	
				alPrevEmployment.add(alInner);
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
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

		return alPrevEmployment;
	}
	
	public String deleteEmployee(Connection con) {

//		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
//		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		int deleteEmpId = uF.parseToInt(request.getParameter("id"));
		
		try {

			
			pst = con.prepareStatement("select emp_image,emp_cover_image from employee_personal_details where emp_per_id=?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			String emp_image = null;
			String empCoverImage = null;
			while(rs.next()) {
				emp_image = rs.getString("emp_image");
				empCoverImage = rs.getString("emp_cover_image");
			}
			rs.close();
			pst.close();
			
			String strImgFilePath = null;
			String strCoverImgFilePath = null;
			if(CF.getStrDocRetriveLocation()==null) {
				strImgFilePath = DOCUMENT_LOCATION +emp_image;
			} else {
				strImgFilePath = CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+getEmpId()+"/"+emp_image;
			}
			File file = new File(strImgFilePath);
			file.delete();
			
			if(CF.getStrDocRetriveLocation()==null) {
				strCoverImgFilePath = DOCUMENT_LOCATION +empCoverImage;
			} else {
				strCoverImgFilePath = CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE_COVER+"/"+getEmpId()+"/"+empCoverImage;
			}
			File file1 = new File(strCoverImgFilePath);
			file1.delete();
			
			pst = con.prepareStatement(deleteEmployee_P);
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement(deleteEmployee_O);
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement(deleteAllowance);
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement(deleteUserEmp);
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement(deleteSkills);
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement(deleteHobbies);
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM languages_details WHERE emp_id = ?");
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM education_details WHERE emp_id = ?");
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement(deleteDocuments);
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM emp_family_members WHERE emp_id = ?");
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM emp_prev_employment WHERE emp_id = ?");
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM emp_references WHERE emp_id = ?");
			pst.setInt(1, deleteEmpId);
			pst.execute();
			pst.close();
			
			//request.setAttribute(MESSAGE, "Deleted successfully!");
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return SUCCESS;
	}
	
	public boolean checkEmpStatus(Connection con,int nEmpId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean flag = false; 
		try {
			pst = con.prepareStatement("select * from employee_personal_details where emp_per_id=? and employment_end_date is null and approved_flag=false and is_alive=false");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			if(rs.next()) {
				flag = true;
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
		return flag;
	}
	
	public String getBioId() {
		return bioId;
	}

	public void setBioId(String bioId) {
		this.bioId = bioId;
	}

	public String getHR() {
		return HR;
	}

	public void setHR(String hR) {
		HR = hR;
	}

	public List<FillEmployee> getHRList() {
		return HRList;
	}

	public void setHRList(List<FillEmployee> hRList) {
		HRList = hRList;
	}

	private String redirectUrl;
	
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
	
	public String getEmpUANNo() {
		return empUANNo;
	}

	public void setEmpUANNo(String empUANNo) {
		this.empUANNo = empUANNo;
	}

	public String getEmpUIDNo() {
		return empUIDNo;
	}

	public void setEmpUIDNo(String empUIDNo) {
		this.empUIDNo = empUIDNo;
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
		this.empDateOfBirth=empDateOfBirth;
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

	public HttpServletRequest request;

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

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
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

	public void setProbationDurationList(
			List<FillProbationDuration> probationDurationList) {
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

	
	public String[] getInstName() {
		return instName;
	}

	public void setInstName(String[] instName) {
		this.instName = instName;
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
	public List<String> getQue1IdFileStatus() {
		return que1IdFileStatus;
	}



	public void setQue1IdFileStatus(List<String> que1IdFileStatus) {
		this.que1IdFileStatus = que1IdFileStatus;
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

	public String getEmpDoctorName() {
		return empDoctorName;
	}

	public void setEmpDoctorName(String empDoctorName) {
		this.empDoctorName = empDoctorName;
	}

	public String getEmpDoctorNo() {
		return empDoctorNo;
	}

	public void setEmpDoctorNo(String empDoctorNo) {
		this.empDoctorNo = empDoctorNo;
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
	
	public String[] getRefName() {
		return refName;
	}

	public void setRefName(String[] refName) {
		this.refName = refName;
	}

	public String[] getRefCompany() {
		return refCompany;
	}

	public void setRefCompany(String[] refCompany) {
		this.refCompany = refCompany;
	}

	public String[] getRefCompanyOther() {
		return refCompanyOther;
	}

	public void setRefCompanyOther(String[] refCompanyOther) {
		this.refCompanyOther = refCompanyOther;
	}

	public String[] getRefDesignation() {
		return refDesignation;
	}

	public void setRefDesignation(String[] refDesignation) {
		this.refDesignation = refDesignation;
	}

	public String[] getRefContactNo() {
		return refContactNo;
	}

	public void setRefContactNo(String[] refContactNo) {
		this.refContactNo = refContactNo;
	}

	public String[] getRefEmail() {
		return refEmail;
	}

	public void setRefEmail(String[] refEmail) {
		this.refEmail = refEmail;
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
	public String[] getEmpKraId() {
		return empKraId;
	}
	public String[] getEmpKRA() {
		return empKRA;
	}

	public void setEmpKRA(String[] empKRA) {
		this.empKRA = empKRA;
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

	public List<FillSalutation> getSalutationList() {
		return salutationList;
	}

	public void setSalutationList(List<FillSalutation> salutationList) {
		this.salutationList = salutationList;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}
 
	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public String getPrevEarnDeductId() {
		return prevEarnDeductId;
	}

	public void setPrevEarnDeductId(String prevEarnDeductId) {
		this.prevEarnDeductId = prevEarnDeductId;
	}

	public String getPrevEmpFYear() {
		return prevEmpFYear;
	}

	public void setPrevEmpFYear(String prevEmpFYear) {
		this.prevEmpFYear = prevEmpFYear;
	}

	public String getPrevTotalEarning() {
		return prevTotalEarning;
	}

	public void setPrevTotalEarning(String prevTotalEarning) {
		this.prevTotalEarning = prevTotalEarning;
	}

	public String getPrevTotalDeduction() {
		return prevTotalDeduction;
	}

	public void setPrevTotalDeduction(String prevTotalDeduction) {
		this.prevTotalDeduction = prevTotalDeduction;
	}

	public String getPrevEarnDeductFile() {
		return prevEarnDeductFile;
	}

	public void setPrevEarnDeductFile(String prevEarnDeductFile) {
		this.prevEarnDeductFile = prevEarnDeductFile;
	}

	public File getPrevFileEarnDeduct() {
		return prevFileEarnDeduct;
	}

	public void setPrevFileEarnDeduct(File prevFileEarnDeduct) {
		this.prevFileEarnDeduct = prevFileEarnDeduct;
	}

	public String getPrevFileEarnDeductFileName() {
		return prevFileEarnDeductFileName;
	}

	public void setPrevFileEarnDeductFileName(String prevFileEarnDeductFileName) {
		this.prevFileEarnDeductFileName = prevFileEarnDeductFileName;
	}

	public List<FillEducational> getEducationalList() {
		return educationalList;
	}

	public void setEducationalList(List<FillEducational> educationalList) {
		this.educationalList = educationalList;
	}

	public String getOrg_id() {
		return org_id;
	}

	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}

	public String getIsForm16A() {
		return isForm16A;
	}

	public void setIsForm16A(String isForm16A) {
		this.isForm16A = isForm16A;
	}

	public String getIsForm16() {
		return isForm16;
	}

	public void setIsForm16(String isForm16) {
		this.isForm16 = isForm16;
	}

	public List<FillAttribute> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<FillAttribute> attributeList) {
		this.attributeList = attributeList;
	}

	public String[] getGoalElements() {
		return goalElements;
	}

	public void setGoalElements(String[] goalElements) {
		this.goalElements = goalElements;
	}

	public String[] getElementAttribute() {
		return elementAttribute;
	}

	public void setElementAttribute(String[] elementAttribute) {
		this.elementAttribute = elementAttribute;
	}

	public String[] getDesigElements() {
		return desigElements;
	}

	public void setDesigElements(String[] desigElements) {
		this.desigElements = desigElements;
	}

	public String[] getDesigElementAttribute() {
		return desigElementAttribute;
	}

	public void setDesigElementAttribute(String[] desigElementAttribute) {
		this.desigElementAttribute = desigElementAttribute;
	}

	public String[] getDesigEmpKraId() {
		return desigEmpKraId;
	}

	public void setDesigEmpKraId(String[] desigEmpKraId) {
		this.desigEmpKraId = desigEmpKraId;
	}

	public String[] getDesigKraId() {
		return desigKraId;
	}

	public void setDesigKraId(String[] desigKraId) {
		this.desigKraId = desigKraId;
	}

	public String[] getDesignKRA() {
		return designKRA;
	}

	public void setDesignKRA(String[] designKRA) {
		this.designKRA = designKRA;
	}

	public String[] getStatus() {
		return status;
	}

	public void setStatus(String[] status) {
		this.status = status;
	}

	public List<String> getPrevCompList() {
		return prevCompList;
	}

	public void setPrevCompList(List<String> prevCompList) {
		this.prevCompList = prevCompList;
	}

	public String getEmpESICNo() {
		return empESICNo;
	}

	public void setEmpESICNo(String empESICNo) {
		this.empESICNo = empESICNo;
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

	public String getQue1DocName() {
		return que1DocName;
	}

	public void setQue1DocName(String que1DocName) {
		this.que1DocName = que1DocName;
	}

	public String getQue2DocName() {
		return que2DocName;
	}

	public void setQue2DocName(String que2DocName) {
		this.que2DocName = que2DocName;
	}

	public String getQue3DocName() {
		return que3DocName;
	}

	public void setQue3DocName(String que3DocName) {
		this.que3DocName = que3DocName;
	}

	public String getQue4DocName() {
		return que4DocName;
	}

	public void setQue4DocName(String que4DocName) {
		this.que4DocName = que4DocName;
	}

	public String getQue5DocName() {
		return que5DocName;
	}

	public void setQue5DocName(String que5DocName) {
		this.que5DocName = que5DocName;
	}

	public String[] getDesigKraTaskId() {
		return desigKraTaskId;
	}

	public void setDesigKraTaskId(String[] desigKraTaskId) {
		this.desigKraTaskId = desigKraTaskId;
	}

	public String[] getDesigEmpKraTaskId() {
		return desigEmpKraTaskId;
	}

	public void setDesigEmpKraTaskId(String[] desigEmpKraTaskId) {
		this.desigEmpKraTaskId = desigEmpKraTaskId;
	}

	public String[] getDesignKRATask() {
		return designKRATask;
	}

	public void setDesignKRATask(String[] designKRATask) {
		this.designKRATask = designKRATask;
	}

	public String[] getEmpKRATask() {
		return empKRATask;
	}

	public void setEmpKRATask(String[] empKRATask) {
		this.empKRATask = empKRATask;
	}

	public String[] getEmpKraTaskId() {
		return empKraTaskId;
	}

	public void setEmpKraTaskId(String[] empKraTaskId) {
		this.empKraTaskId = empKraTaskId;
	}

	public String getDefaultStatus() {
		return defaultStatus;
	}

	public void setDefaultStatus(String defaultStatus) {
		this.defaultStatus = defaultStatus;
	}

	public String getEmpStatus() {
		return empStatus;
	}

	public void setEmpStatus(String empStatus) {
		this.empStatus = empStatus;
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

	public List<String> getCxoLocationAccess() {
		return cxoLocationAccess;
	}

	public void setCxoLocationAccess(List<String> cxoLocationAccess) {
		this.cxoLocationAccess = cxoLocationAccess;
	}

	public String[] getLocationCXO() {
		return locationCXO;
	}

	public void setLocationCXO(String[] locationCXO) {
		this.locationCXO = locationCXO;
	}

	public String getEmpBankName2() {
		return empBankName2;
	}

	public void setEmpBankName2(String empBankName2) {
		this.empBankName2 = empBankName2;
	}

	public String getEmpBankAcctNbr2() {
		return empBankAcctNbr2;
	}

	public void setEmpBankAcctNbr2(String empBankAcctNbr2) {
		this.empBankAcctNbr2 = empBankAcctNbr2;
	}

	public String getSpouseMaritalStatus() {
		return spouseMaritalStatus;
	}

	public void setSpouseMaritalStatus(String spouseMaritalStatus) {
		this.spouseMaritalStatus = spouseMaritalStatus;
	}

	public List<String> getLeavesValue() {
		return leavesValue;
	}

	public void setLeavesValue(List<String> leavesValue) {
		this.leavesValue = leavesValue;
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

	public String[] getChildName() {
		return childName;
	}

	public void setChildName(String[] childName) {
		this.childName = childName;
	}

	public String[] getChildDob() {
		return childDob;
	}

	public void setChildDob(String[] childDob) {
		this.childDob = childDob;
	}

	public String[] getChildEducation() {
		return childEducation;
	}

	public void setChildEducation(String[] childEducation) {
		this.childEducation = childEducation;
	}

	public String[] getChildOccupation() {
		return childOccupation;
	}

	public void setChildOccupation(String[] childOccupation) {
		this.childOccupation = childOccupation;
	}

	public String[] getChildContactNumber() {
		return childContactNumber;
	}

	public void setChildContactNumber(String[] childContactNumber) {
		this.childContactNumber = childContactNumber;
	}

	public String[] getChildEmailId() {
		return childEmailId;
	}

	public void setChildEmailId(String[] childEmailId) {
		this.childEmailId = childEmailId;
	}

	public String[] getChildGender() {
		return childGender;
	}

	public void setChildGender(String[] childGender) {
		this.childGender = childGender;
	}
	
	public String getEmpMname() {
		return empMname;
	}

	public void setEmpMname(String empMname) {
		this.empMname = empMname;
	}

	public String getEmpSeparationDate() {
		return empSeparationDate;
	}

	public void setEmpSeparationDate(String empSeparationDate) {
		this.empSeparationDate = empSeparationDate;
	}

	public String getEmpConfirmationDate() {
		return empConfirmationDate;
	}

	public void setEmpConfirmationDate(String empConfirmationDate) {
		this.empConfirmationDate = empConfirmationDate;
	}

	public String getEmpActConfirmDate() {
		return empActConfirmDate;
	}

	public void setEmpActConfirmDate(String empActConfirmDate) {
		this.empActConfirmDate = empActConfirmDate;
	}

	public String getEmpPromotionDate() {
		return empPromotionDate;
	}

	public void setEmpPromotionDate(String empPromotionDate) {
		this.empPromotionDate = empPromotionDate;
	}

	public boolean isMedicalProfessional() {
		return isMedicalProfessional;
	}

	public void setMedicalProfessional(boolean isMedicalProfessional) {
		this.isMedicalProfessional = isMedicalProfessional;
	}
	
	public String getIsTdsDetails() {
		return isTdsDetails;
	}

	public void setIsTdsDetails(String isTdsDetails) {
		this.isTdsDetails = isTdsDetails;
	}

	public String getStrEmpKncKmc() {
		return strEmpKncKmc;
	}

	public void setStrEmpKncKmc(String strEmpKncKmc) {
		this.strEmpKncKmc = strEmpKncKmc;
	}

	public String getStrKmcNo() {
		return strKmcNo;
	}

	public void setStrKmcNo(String strKmcNo) {
		this.strKmcNo = strKmcNo;
	}

	public String getStrKncNo() {
		return strKncNo;
	}

	public void setStrKncNo(String strKncNo) {
		this.strKncNo = strKncNo;
	}

	public String getStrRenewalDate() {
		return strRenewalDate;
	}

	public void setStrRenewalDate(String strRenewalDate) {
		this.strRenewalDate = strRenewalDate;
	}

	public String getIsMedicalCheck() {
		return isMedicalCheck;
	}

	public void setIsMedicalCheck(String isMedicalCheck) {
		this.isMedicalCheck = isMedicalCheck;
	}

	public void setEmpTypeList(List<FillEmploymentType> empTypeList) {
		this.empTypeList = empTypeList;
	}

	public String getFatherMRDNo() {
		return fatherMRDNo;
	}

	public void setFatherMRDNo(String fatherMRDNo) {
		this.fatherMRDNo = fatherMRDNo;
	}

	public String getMotherMRDNo() {
		return motherMRDNo;
	}

	public void setMotherMRDNo(String motherMRDNo) {
		this.motherMRDNo = motherMRDNo;
	}

	public String getSpouseMRDNo() {
		return spouseMRDNo;
	}

	public void setSpouseMRDNo(String spouseMRDNo) {
		this.spouseMRDNo = spouseMRDNo;
	}

	public String[] getSiblingsMRDNo() {
		return siblingsMRDNo;
	}

	public void setSiblingsMRDNo(String[] siblingsMRDNo) {
		this.siblingsMRDNo = siblingsMRDNo;
	}

	public String[] getChildMRDNo() {
		return childMRDNo;
	}

	public void setChildMRDNo(String[] childMRDNo) {
		this.childMRDNo = childMRDNo;
	}
	public String getEmpIncrementDate() {
		return empIncrementDate;
	}

	public void setEmpIncrementDate(String empIncrementDate) {
		this.empIncrementDate = empIncrementDate;
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

	public String getDocId1() {
		return docId1;
	}

	public void setDocId1(String docId1) {
		this.docId1 = docId1;
	}

	public String getDocId2() {
		return docId2;
	}

	public void setDocId2(String docId2) {
		this.docId2 = docId2;
	}

	public String getStrStep() {
		return strStep;
	}

	public void setStrStep(String strStep) {
		this.strStep = strStep;
	}

	public String getEmpMRDNo() {
		return empMRDNo;
	}

	public void setEmpMRDNo(String empMRDNo) {
		this.empMRDNo = empMRDNo;
	}
	public String[] getIsMotherTounge() {
		return isMotherTounge;
	}

	public void setIsMotherTounge(String[] isMotherTounge) {
		this.isMotherTounge = isMotherTounge;
	}

	public String getStrDOBMaxDate() {
		return strDOBMaxDate;
	}

	public void setStrDOBMaxDate(String strDOBMaxDate) {
		this.strDOBMaxDate = strDOBMaxDate;
	}

	public String getStrCurrDate() {
		return strCurrDate;
	}

	public void setStrCurrDate(String strCurrDate) {
		this.strCurrDate = strCurrDate;
	}

	public String[] getDocId() {
		return docId;
	}

	public void setDocId(String[] docId) {
		this.docId = docId;
	}

	public File[] getIdDoc() {
		return idDoc;
	}

	public void setIdDoc(File[] idDoc) {
		this.idDoc = idDoc;
	}

	public String[] getIdDocFileName() {
		return idDocFileName;
	}

	public void setIdDocFileName(String[] idDocFileName) {
		this.idDocFileName = idDocFileName;
	}

	public String[] getIdDocStatus() {
		return idDocStatus;
	}

	public void setIdDocStatus(String[] idDocStatus) {
		this.idDocStatus = idDocStatus;
	}

	public String[] getIdDocName() {
		return idDocName;
	}

	public void setIdDocName(String[] idDocName) {
		this.idDocName = idDocName;
	}

	public String[] getIdDocType() {
		return idDocType;
	}

	public void setIdDocType(String[] idDocType) {
		this.idDocType = idDocType;
	}

	public String[] getDegreeCertiStatus() {
		return degreeCertiStatus;
	}

	public void setDegreeCertiStatus(String[] degreeCertiStatus) {
		this.degreeCertiStatus = degreeCertiStatus;
	}

	public String[] getDegreeId() {
		return degreeId;
	}

	public void setDegreeId(String[] degreeId) {
		this.degreeId = degreeId;
	}
	
	public String getEmpBankIFSCNbr() {
		return empBankIFSCNbr;
	}

	public void setEmpBankIFSCNbr(String empBankIFSCNbr) {
		this.empBankIFSCNbr = empBankIFSCNbr;
	}
	
	public String getEmpBankIFSCNbr2() {
		return empBankIFSCNbr2;
	}

	public void setEmpBankIFSCNbr2(String empBankIFSCNbr2) {
		this.empBankIFSCNbr2 = empBankIFSCNbr2;
	}

	public String getSlabType() {
		return slabType;
	}

	public void setSlabType(String slabType) {
		this.slabType = slabType;
	}

	public String getEmpPFStartDate() {
		return empPFStartDate;
	}

	public void setEmpPFStartDate(String empPFStartDate) {
		this.empPFStartDate = empPFStartDate;
	}

	public String getPrevPANNumber() {
		return prevPANNumber;
	}

	public void setPrevPANNumber(String prevPANNumber) {
		this.prevPANNumber = prevPANNumber;
	}

	public String getPrevTANNumber() {
		return prevTANNumber;
	}

	public void setPrevTANNumber(String prevTANNumber) {
		this.prevTANNumber = prevTANNumber;
	}

	public String getEmpEmergencyContactRelation() {
		return empEmergencyContactRelation;
	}

	public void setEmpEmergencyContactRelation(String empEmergencyContactRelation) {
		this.empEmergencyContactRelation = empEmergencyContactRelation;
	}
	
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
//===start parvez date: 08-08-2022===
	public String getEmpOtherBankName() {
		return empOtherBankName;
	}

	public void setEmpOtherBankName(String empOtherBankName) {
		this.empOtherBankName = empOtherBankName;
	}

	public String getEmpOtherBankName2() {
		return empOtherBankName2;
	}

	public void setEmpOtherBankName2(String empOtherBankName2) {
		this.empOtherBankName2 = empOtherBankName2;
	}
//===end parvez date: 08-08-2022===

	public String getEmpOtherBankBranch() {
		return empOtherBankBranch;
	}

	public void setEmpOtherBankBranch(String empOtherBankBranch) {
		this.empOtherBankBranch = empOtherBankBranch;
	}

	public String getEmpOtherBankBranch2() {
		return empOtherBankBranch2;
	}

	public void setEmpOtherBankBranch2(String empOtherBankBranch2) {
		this.empOtherBankBranch2 = empOtherBankBranch2;
	}
}