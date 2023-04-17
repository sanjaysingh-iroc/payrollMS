package com.konnect.jpms.util;


public interface IConstants extends IDBConstant, INotificationConstants{
	
	final public static String INDIA				= "1";
	final public static double LeaveHours			= 5.5d;
	final public static String PublicHoliday		= "Public Holiday";
	
	final public static boolean isDebug				= true;
	final public static String DELIMITTER			= ",";
	final public static String CommonFunctions		= "CF"; 
	
	     
	final public static String ADMIN				= "Global HR";
	final public static String MANAGER				= "My Team";
	final public static String EMPLOYEE				= "Myself";
	final public static String ACCOUNTANT			= "Accountant";
	final public static String CEO					= "CEO";
	final public static String CFO					= "CFO";
	final public static String HRMANAGER			= "Human Resource";
	final public static String ARTICLE				= "Article";
	final public static String CONSULTANT			= "Consultant";
	final public static String WLOCATION			= "Location";
	final public static String ORGANISATION			= "Organisation";
	final public static String USER_LEVEL			= "UserLevel";
	final public static String TRAINER				= "Trainer";
	final public static String RECRUITER			= "Recruiter";
	final public static String HOD					= "HOD";
	final public static String CUSTOMER				= "Customer";
	final public static String OTHER_HR				= "Other HR";
	
	
	/*final public static String EMPLOYEE				= "Student";
	final public static String MANAGER				= "Teacher";
	final public static String HOD					= "Coordinator";
	final public static String CFO					= "Vice Principal";
	final public static String CEO					= "Principal";
	final public static String HRMANAGER			= "School Management";
	final public static String ADMIN				= "Super Admin"; 
	
	final public static String ACCOUNTANT			= "";
	final public static String ARTICLE				= "";
	final public static String CONSULTANT			= "";
	final public static String WLOCATION			= "Location";
	final public static String ORGANISATION			= "Organisation";
	final public static String USER_LEVEL			= "UserLevel";
	final public static String TRAINER				= "";
	final public static String RECRUITER			= "";
	final public static String CUSTOMER				= "";*/
	
	final public static String EVERYONE				= "EVERYONE";
	  
	final public static String TITLE 				= "TITLE";
	final public static String PAGE 				= "PAGE";
	final public static String MENU 				= "MENU";
	final public static String SUBMENU 				= "SUBMENU";
	final public static String MESSAGE	 			= "MESSAGE";
	final public static String USERTYPE				= "USERTYPE";
	final public static String USERTYPEID			= "USERTYPEID";
	final public static String IS_SUPERVISOR		= "IS_SUPERVISOR";
	final public static String BASEUSERTYPEID		= "BASEUSERTYPEID";
	final public static String BASEUSERTYPE			= "BASEUSERTYPE";
	final public static String USERNAME				= "USERNAME";
	final public static String USERID				= "USERID";
	final public static String PROFILE_IMG			= "PROFILE_IMG";
	final public static String EMPID				= "EMPID";
	final public static String WLOCATIONID			= "WLOCATIONID";
	final public static String WLOCATION_NAME		= "WLOCATION_NAME";
	final public static String DESIGNATION			= "DESIGNATION";
	final public static String DEPARTMENT			= "DEPARTMENT";
	final public static String DEPARTMENTID			= "DEPARTMENTID";	//added by parvez date: 08-02-2023
	final public static String ORG_NAME				= "ORG_NAME";
	final public static String ORGID				= "ORGID";
	final public static String STATUS				= "STATUS";
	final public static String EMPNAME				= "EMPNAME";
	final public static String WLOCATION_ACCESS		= "WLOCATION_ACCESS";
	final public static String ORG_ACCESS			= "ORG_ACCESS";
	final public static String PRODUCT_TYPE			= "PRODUCT_TYPE";
	final public static String LOGIN_TYPE			= "LOGIN_TYPE";
	final public static String RESOURCE_OR_CONTRACTOR	= "RESOURCE_OR_CONTRACTOR";
	
	
	final public static String ACCESS_DENIED		= "accessdeny";
	final public static String LOAD					= "load";
	final public static String MYLOAD				= "myload";
	final public static String VIEW					= "view";
	final public static String MYSUCCESS			= "mysuccess";
	final public static String UPDATE				= "update";
	final public static String DELETE				= "delete";
	final public static String DASHBOARD			= "dashboard";
	final public static String MYHOME				= "myhome";
	final public static String PROFILE				= "profile";
	final public static String REPORT				= "report";
	final public static String APPROVE_PAYROLL		= "approve_payroll";
	
	final public static String UPDATE_CLOCK_ENTRIES	= "update_clock_entries";
	final public static String RATE					= "rate";
	final public static String NO_TIME_RECORD		= "No record";
	
	final public static String VIEW_ACCESS			= "VIEW_ACCESS";
	final public static String DELETE_ACCESS		= "DELETE_ACCESS";
	final public static String ADD_ACCESS			= "ADD_ACCESS";
	final public static String UPDATE_ACCESS		= "UPDATE_ACCESS";
	
	
	
//	public String ReportDateFormat 	= "dd/MM/yyyy";
//	public String CURRENCY_FULL		= "AUD$";
//	public String CURRENCY_SHORT	= "$";
//	public String ReportDayFormat 	= "EEEE";
//	public String ReportTimeFormat 	= "HH:mm"; 
	
	final public static int TRAVEL_LEAVE = -1;
	final public static int ON_DUTY = -2;
	
	
	final public static String DOCUMENT_LOCATION = "/userDocuments/";
	final public static String IMAGE_LOCATION = "userImages/";
	
	  
//	final public static String ReportTimeFormat12 		= "hh:mma";	
	final public static String DBTIMESTAMP	 			= "yyyy-MM-dd HH:mm:ss";
	final public static String DBTIMESTAMP_STR	 		="dd/MM/yyyy HH:mm:ss";
	final public static String DATE_FORMAT				= "dd/MM/yyyy";
	final public static String DATE_MM_DD_YYYY			= "MM/dd/yyyy";
	final public static String DATE_FORMAT_STR			= "dd-MMM-yy";
	final public static String DBDATE	 				= "yyyy-MM-dd";
	final public static String DBTIME	 				= "HH:mm";
	final public static String TIME_FORMAT				= "HH:mm";
	final public static String TIME_FORMAT_AM_PM		= "hh:mma";
	

	final public static String SUNDAY	 				= "SUNDAY";
	final public static String MONDAY	 				= "MONDAY";
	final public static String TUESDAY	 				= "TUESDAY";
	final public static String WEDNESDAY 				= "WEDNESDAY";
	final public static String THURSDAY	 				= "THURSDAY";
	final public static String FRIDAY	 				= "FRIDAY";
	final public static String SATURDAY	 				= "SATURDAY";
	

	
	final public static String FIRSTNAME_DOT_LASTNAME	= "FIRSTNAME_DOT_LASTNAME";
	final public static String USER_NAME 				= "USER_NAME";
	final public static String EMP_CODE					= "EMP_CODE";
	

	final public static String O_USERNAME_FORMAT 			= "USERNAME_FORMAT";
	final public static String O_COMMON_ATTEN_FORMAT		= "COMMON_ATTEN_FORMAT";
	final public static String O_LUNCH_DEDUCT 				= "LUNCH_DEDUCT";
	final public static String O_START_PAY_CLYCLE 			= "START_PAY_CLYCLE";
	final public static String O_DISPLAY_PAY_CLYCLE 		= "DISPLAY_PAY_CLYCLE";
	final public static String O_PAYCYCLE_DURATION 			= "PAYCYCLE_DURATION";
	final public static String O_DISPLAY_PAYCYCLE 			= "DISPLAY_PAY_CLYCLE";
	final public static String O_TIME_ZONE 					= "TIME_ZONE";
	final public static String O_LUNCH_DEDUCT_TIME			= "LUNCH_DEDUCT_TIME";
	final public static String O_STANDARD_FULL_TIME_HOURS 	= "STANDARD_FULL_TIME_HOURS";
	final public static String O_FLAT_TDS					= "FLAT_TDS";
	
	
	final public static String O_EMAIL_NOTIFICATIONS 		= "EMAIL_NOTIFICATIONS";
	final public static String O_TEXT_NOTIFICATIONS 		= "TEXT_NOTIFICATIONS";
	final public static String O_EMAIL_LOCAL_HOST			= "EMAIL_LOCAL_HOST";
	final public static String O_EMAIL_HOST 				= "EMAIL_HOST";
	final public static String O_EMAIL_FROM 				= "EMAIL_FROM";
	final public static String O_TEXT_FROM 					= "TEXT_FROM";
	final public static String O_HOST_PASSWORD				= "HOST_PASSWORD";
	
	final public static String O_UI_THEME					= "UI_THEME";
	final public static String O_ORG_LOGO					= "ORG_LOGO";
	final public static String O_ORG_LOGO_SMALL				= "ORG_LOGO_SMALL";
	final public static String O_ORG_FULL_NAME				= "ORG_FULL_NAME";
	final public static String O_ORG_SUB_TITLE				= "ORG_SUB_TITLE";
	final public static String O_ORG_FULL_ADDRESS			= "ORG_FULL_ADDRESS";
	final public static String O_ORG_CITY					= "ORG_CITY";
	final public static String O_ORG_STATE					= "ORG_STATE";
	final public static String O_ORG_COUNTRY				= "ORG_COUNTRY";
	final public static String O_ORG_EMAIL					= "ORG_EMAIL";
	final public static String O_ORG_PINCODE				= "ORG_PINCODE";
	final public static String O_SALARY_CALCULATION			= "SALARY_CALCULATION";
	
	final public static String O_ORG_DESCRIPTION			= "ORG_DESCRIPTION";
	final public static String O_ORG_CONTACT_NO 			= "ORG_CONTACT_NO";
	final public static String O_ORG_FAX_NO    				= "ORG_FAX_NO";
	final public static String O_ORG_WEBSITE				= "ORG_WEBSITE";
	final public static String O_ORG_INDUSTRY				= "ORG_INDUSTRY";
	
	final public static String O_SHORT_CURR 				= "SHORT_CURR";
	final public static String O_LONG_CURR 					= "LONG_CURR"; 
	final public static String O_SELECTED_CURRENCY			= "SELECTED_CURRENCY";
	
	final public static String O_DATE_FORMAT 				= "DATE_FORMAT";
	final public static String O_TIME_FORMAT 				= "TIME_FORMAT";
	final public static String O_DAY_FORMAT 				= "DAY_FORMAT";
	
	final public static String O_FINANCIAL_YEAR_START		= "FINANCIAL_YEAR_START";
	final public static String O_FINANCIAL_YEAR_END			= "FINANCIAL_YEAR_END";
	final public static String O_INVESTMENT_EXEMPTION		= "INVESTMENT_EXEMPTION";
	final public static String O_EDUCATION_CESS				= "EDUCATION_CESS";
	final public static String O_STANDARD_CESS				= "STANDARD_CESS";
	final public static String O_SERVICE_TAX				= "SERVICE_TAX";
	final public static String O_EMP_CODE_ALPHA				= "EMP_CODE_ALPHA";
	final public static String O_CONTRACTOR_CODE_ALPHA		= "CONTRACTOR_CODE_ALPHA";
	final public static String O_EMP_CODE_NUM				= "EMP_CODE_NUM"; 
	final public static String O_EMP_CODE_AUTO_GENERATION	= "EMP_CODE_AUTO_GENERATION";
	final public static String O_ATTENDANCE_INTEGRATED_WITH_ACTIVITY	= "ATTENDANCE_INTEGRATED_WITH_ACTIVITY";
	final public static String O_SHOW_PASSWORD				= "SHOW_PASSWORD";
	final public static String O_IS_WORKFLOW				= "IS_WORKFLOW";
	final public static String O_IS_BONUS_PAYROLL			= "IS_BONUS_PAYROLL";
	final public static String O_IS_SPECIFIC_EMP			= "IS_SPECIFIC_EMP";
	final public static String O_BACKUP_LOCATION			= "BACKUP_LOCATION";
	final public static String O_PGDUMP_LOCATION			= "PGDUMP_LOCATION";
	final public static String O_IS_PAYCYCLE_MONTH_ADJUSTMENT	= "IS_PAYCYCLE_MONTH_ADJUSTMENT";
	final public static String O_DOC_SAVE_LOCATION			= "DOC_SAVE_LOCATION";
	final public static String O_DOC_RETRIVE_LOCATION		= "DOC_RETRIVE_LOCATION"; 
	final public static String O_IS_REMOTE_LOCATION 		= "IS_REMOTE_LOCATION";
	final public static String FIXED_MONTH_DAYS 			= "FIXED_MONTH_DAYS";
	final public static String O_EXCEPTION_IS_AUTO_APPROVE	= "EXCEPTION_IS_AUTO_APPROVE"; 
	final public static String O_IS_ARREAR					= "IS_ARREAR";  
	final public static String O_IS_DEVICE_INTEGRATION		= "IS_DEVICE_INTEGRATION";
	final public static String O_IS_BREAK_POLICY			= "IS_BREAK_POLICY";
	final public static String MAX_TIME_LIMIT_OUT			= "MAX_TIME_LIMIT_OUT";
	final public static String O_PROJECT_DOCUMENT_FOLDER	= "PROJECT_DOCUMENT_FOLDER";
	final public static String O_RETRIVE_PROJECT_DOCUMENT_FOLDER	= "RETRIVE_PROJECT_DOCUMENT_FOLDER";
	final public static String O_BACKUP_RETRIVE_LOCATION	= "BACKUP_RETRIVE_LOCATION";  
	final public static String O_HOST_PORT					= "HOST_PORT";
	
	final public static String O_IS_WORKRIG					= "IS_WORKRIG";
	final public static String O_IS_TASKRIG					= "IS_TASKRIG";
	final public static String O_IS_SUSPENDED 				= "IS_SUSPENDED";
	final public static String O_IS_SERVICE_TERMINATED 		= "IS_SERVICE_TERMINATED";
	final public static String O_IS_UNDER_IMPLEMENTATION 	= "IS_UNDER_IMPLEMENTATION";
	
	final public static String O_TRACKER_PASSWORD			= "TRACKER_PASSWORD";
	

	final public static String O_IS_CLOUD					= "IS_CLOUD";
	final public static String O_IS_OFFICE_365_SMTP			= "IS_OFFICE_365_SMTP";
	
	final public static String O_IS_REQUIRED_AUTHENTICATION		= "IS_REQUIRED_AUTHENTICATION";
	final public static String O_EMAIL_AUTHENTICATION_USER		= "EMAIL_AUTHENTICATION_USER";  
	final public static String O_EMAIL_AUTHENTICATION_PASSWORD	= "EMAIL_AUTHENTICATION_PASSWORD";
	
	final public static String O_IS_CLOCK_ON_OFF				= "IS_CLOCK_ON_OFF";
	final public static String O_SALARY_STRUCTURE				= "SALARY_STRUCTURE";
	final public static String O_TERMS_CONDITIONS_TYPE			= "TERMS_CONDITIONS_TYPE";
	final public static String O_IS_RECEIPT						= "IS_RECEIPT";
	final public static String O_IS_HALF_DAY_LEAVE 				= "IS_HALF_DAY_LEAVE";
	final public static String O_ROUND_OFF_CONDITION 			= "ROUND_OFF_CONDITION";
	final public static String O_PRODUCTION_LINE 				= "PRODUCTION_LINE";
	
	final public static String O_IS_ORG_AUTHORIZED_MOBILE		= "IS_ORG_AUTHORIZED_MOBILE";
	final public static String O_IS_MOBILE_APP_TIME				= "IS_MOBILE_APP_TIME";
	
	final public static String O_IS_SANDWICH_ABSENT 				= "IS_SANDWICH_ABSENT";
	final public static String O_IS_TERMINATE_WITHOUT_FULLANDFINAL 	= "IS_TERMINATE_WITHOUT_FULLANDFINAL";
	final public static String O_IS_TDS_AUTO_APPROVE 				= "IS_TDS_AUTO_APPROVE";
	final public static String O_IS_SHOW_TIME_VARIANCE 				= "IS_SHOW_TIME_VARIANCE";
	final public static String O_IS_CAL_LEAVE_IN_ATTENDANCE_DEPENDANT_NO 	= "IS_CAL_LEAVE_IN_ATTENDANCE_DEPENDANT_NO";
	final public static String O_EMAIL_ID_CC 						= "EMAIL_ID_CC";										// Created By Dattatray Date : 02-July-2021
	final public static String O_HIRING_TEAM_EMAIL_ID 				= "HIRING_TEAM_EMAIL_ID";						// Created By Parvez Date : 02-November-2021
	final public static String O_EMAIL_INBOX_HOST					= "EMAIL_INBOX_HOST";								// Created By Parvez Date : 17-12-2022
	final public static String O_EMAIL_INBOX_AUTHENTICATION_USER	= "EMAIL_INBOX_AUTHENTICATION_USER";// Created By Parvez Date : 17-12-2022
	final public static String O_EMAIL_INBOX_AUTHENTICATION_PASSWORD = "EMAIL_INBOX_AUTHENTICATION_PASSWORD";	//created by parvez date: 17-12-2022
	
	/**
	 * O_EPF_CONDITION_1
	 * Exclusive condition for EPF
	 * @author konnect
	 * 
	 * limit=6500; Actual=Basic+DA+VDA1;
	 * limit*8.33/100;
	 * actual*8.33/100;
	 * actual-limit;
	 * */ 
	final public static String O_EPF_CONDITION_1 	    = "EPF_CONDITION_1";      
	
	 
	final public static String MISC_FLAT_TDS			= "FLAT_TDS";
	final public static String MISC_SERVICE_TAX			= "SERVICE_TAX";
	final public static String MISC_STANDARD_TAX		= "STANDARD_TAX";
	final public static String MISC_EDUCATION_CESS		= "EDUCATION_CESS"; 
	
	
	final public static int NET							= 30000;
	final public static int GROSS						= 500;
	final public static int BASIC			 			= 1;
	final public static int DA				 			= 2;
	final public static int DA1				 			= 20001;
	final public static int HRA				 			= 3;
	final public static int CONVEYANCE_ALLOWANCE		= 4;
	final public static int OVER_TIME					= 5;
	final public static int GRATUITY					= 6;
	final public static int BONUS						= 7;
	final public static int AREARS						= 8;
	final public static int INCENTIVES					= 9;
	final public static int REIMBURSEMENT				= 10;
	final public static int OTHER_EARNING				= 101;
	final public static int MOBILE_REIMBURSEMENT		= 102;
	final public static int TRAVEL_REIMBURSEMENT		= 103;
	final public static int OTHER_REIMBURSEMENT			= 104;
	final public static int ATTENDANCE_BONUS			= 203;
	final public static int VDA							= 21;
	final public static int CANTEEN_ALLOWANCE			= 22;

	final public static int SERVICE_TAX					= 120;
	final public static int SWACHHA_BHARAT_CESS			= 121;
	final public static int KRISHI_KALYAN_CESS			= 122;
	final public static int CGST 						= 310;
	final public static int SGST 						= 311;
	
	final public static int LEAVE_ENCASHMENT			= 219;
	final public static int GRINDING_ALLOWANCE			= 210;
	final public static int LAPPING_ALLOWANCE			= 211;
	final public static int CARBON_ALLOWANCE			= 212;
	final public static int LATE_COMING					= 218;

	final public static int STANDARD_DEDUCATION 		= 0;// Created By dattatray 16-06-2022
	final public static int PROFESSIONAL_TAX 			= 11;
	final public static int TDS				 			= 12;
	final public static int EMPLOYEE_EPF				= 13;
	final public static int EMPLOYER_EPF				= 14;
	final public static int EMPLOYER_ESI				= 15;
	final public static int EMPLOYEE_ESI				= 16;
	final public static int LOAN						= 17;
	final public static int VOLUNTARY_EPF				= 18;
	final public static int EMPLOYEE_LWF				= 19;
	final public static int EMPLOYER_LWF				= 20;
	
	
	final public static int BREAKS						= 200;
	final public static int OTHER_DEDUCTION				= 201;
	final public static int MOBILE_RECOVERY				= 202;
	final public static int LTA							= 220;
	
	final public static int EXGRATIA					= 222;
	final public static int EDUCATION_ALLOWANCE		    = 446;
	final public static int MEDICAL_ALLOWANCE		    = 445;
	
	final public static int CTC						    = 301;
	final public static int REIMBURSEMENT_CTC 			= 51;
	
	
	
	final public static int MODULE_TIME_ATTENDANCE				= 1;
	final public static int MODULE_TIME_ATTENDANCE_WEB			= 101;
	final public static int MODULE_TIME_ATTENDANCE_DESKTOP		= 102;
	final public static int MODULE_TIME_ATTENDANCE_MOBILE		= 103;
	final public static int MODULE_ROSTER						= 2;
	final public static int MODULE_PAYROLL_TAX					= 3;
	final public static int MODULE_HCM							= 4;
	final public static int MODULE_LEAVE						= 5;
	final public static int MODULE_POLICY						= 6;
	final public static int MODULE_ONBOARDING_OFFBOARDING		= 7;
	final public static int MODULE_FORMS_REQUESTS				= 8;
	final public static int MODULE_SELF_SERVICE					= 9;
//	final public static int MODULE_PROJECT_MGMT					= 10;
	 
	
	final public static int MODULE_DEFAULT						= 0;
	final public static int MODULE_HUMAN_CAPITAL_MANAGEMENT		= 1;
	final public static int MODULE_ONBOARDING					= 101;
	final public static int MODULE_CAREER_DEV_PLANNING			= 102;
	final public static int MODULE_PERFORMANCE_MANAGEMENT		= 103;
	final public static int MODULE_GOAL_TARGET					= 104;
	final public static int MODULE_PEOPLE_MANAGEMENT			= 105;
	final public static int MODULE_RECRUITMENT_MANAGEMENT		= 106;
	final public static int MODULE_COMPENSATION_MANAGEMENT		= 2;
	final public static int MODULE_EXPENSE_MANAGEMENT			= 201;
	final public static int MODULE_LEAVE_MANAGEMENT				= 3;
	final public static int MODULE_STATUORY_COMPLIANCE			= 4;
	final public static int MODULE_TIME_SCHEDUING				= 5;
	final public static int MODULE_TIME_AND_ATTENDANCE			= 501;
	final public static int MODULE_SCHEDUING					= 502;
	final public static int MODULE_PROJECT_MANAGEMENT			= 6;
	final public static int MODULE_PROJECT_BILLING_MGMT			= 601;
	final public static int MODULE_DOCUMENT_MGMT				= 7;
	final public static int MODULE_DBBACKUP_MGMT				= 8;
	final public static int MODULE_TIMESHEET_MANAGEMENT			= 602;
	final public static int MODULE_RESOURCE_MANAGEMENT			= 11;
	final public static int MODULE_CUSTOMER_MANAGEMENT			= 12;
	
	final public static int MODULE_UTILITY						= 13;
	final public static int MODULE_LIBRARY						= 1301;
	final public static int MODULE_CAFETERIA					= 1302;
	final public static int MODULE_MEETING_ROOM					= 1303;
	
	final public static int MODULE_TRACKER						= 1001;
	final public static int MODULE_GHOST_TRACKER				= 1002;
	
	
	final public static String PAYROLL_USER_ID_KEY_VALUE			= "dYkHyn#erFtd@53+546*542344";
	
	
	
	
	/**
	 * 		Status constants
	 * */
	
	public String PERMANENT 		= "PERMANENT";
	public String RESIGNED 			= "RESIGNED";
	public String PROBATION 		= "PROBATION";
	public String TEMPORARY 		= "TEMPORARY";
	public String NOTICE 			= "NOTICE";
	public String TERMINATED 		= "TERMINATED";
	public String RETIRED 			= "RETIRED";
	
	
	final public static String ACTIVITY_OFFER_ID 							= "1";
	final public static String ACTIVITY_APPOINTMENT_ID 						= "2";
	final public static String ACTIVITY_PROBATION_ID 						= "3";
	final public static String ACTIVITY_EXTEND_PROBATION_ID 				= "4";
	final public static String ACTIVITY_CONFIRMATION_ID 					= "5";
	final public static String ACTIVITY_TEMPORARY_ID 						= "6"; //Temporary
	final public static String ACTIVITY_PERMANENT_ID 						= "7"; //Permanent
	final public static String ACTIVITY_TRANSFER_ID 						= "8"; //Transfer
	final public static String ACTIVITY_PROMOTION_ID 						= "9"; //Promotion
	final public static String ACTIVITY_INCREMENT_ID 						= "10"; //Increment
	final public static String ACTIVITY_GRADE_CHANGE_ID 					= "11"; //Grade Change
	final public static String ACTIVITY_TERMINATE_ID 						= "12"; //Terminate
	final public static String ACTIVITY_NEW_JOINING 						= "1013"; //New Joining
	final public static String ACTIVITY_NOTICE_PERIOD_ID 					= "13"; //New Joining
	final public static String ACTIVITY_RESIGNATION_WITHDRWAL_ID			= "14"; //Withdrawn Resignation
	final public static String ACTIVITY_FULL_FINAL_ID 						= "15"; //Full & Final
	final public static String ACTIVITY_NEW_JOINEE_PENDING_ID				= "16"; //New Joinee Pending
	final public static String ACTIVITY_RESIGNED_ID							= "17"; //Resigned
	final public static String ACTIVITY_EMP_STATUS_CHANGE_ID				= "18"; //Emp Status Change
	final public static String ACTIVITY_USER_TYPE_CHANGE_ID					= "19"; //User Type Change
	final public static String ACTIVITY_SALARY_APPROVAL_ID					= "20"; //Salary Approval
	final public static String ACTIVITY_EXIT_FEEDBACK_FORM_SUBMIT_ID		= "21"; //Exit Feedback Form Submited
	final public static String ACTIVITY_EXIT_FEEDBACK_FORM_APPROVAL_ID		= "22"; //Exit Feedback Form Approved
	final public static String ACTIVITY_HANDOVER_DOCUMENTS_SUBMIT_ID		= "23"; //Handover Document Submited
	final public static String ACTIVITY_HANDOVER_DOCUMENTS_APPROVAL_ID		= "24"; //Handover Document Approved
	final public static String ACTIVITY_HR_DOCUMENTS_SAVE_APPROVAL_ID		= "25"; //HR Documents Save & Approved
	final public static String ACTIVITY_BONAFIDE_ID							= "26"; //Bonafide
	final public static String ACTIVITY_CANDIDATE_OFFER_ID					= "27"; //Candidate Offer
	final public static String ACTIVITY_CANDIDATE_APPOINTMENT_ID			= "28"; //Candidate Appointment
	final public static String ACTIVITY_RELEVING_ID							= "29"; //Releaving
	final public static String ACTIVITY_EXPERIENCE_ID						= "30"; //Experience
	final public static String ACTIVITY_BANK_ORDER_ID						= "31"; //Bank Order
	final public static String ACTIVITY_LIFE_EVENT_ID 						= "32"; //Life Event Increment
//	final public static String ACTIVITY_NOTICE_PERIOD_ID					= "33"; //Notice Period
	final public static String ACTIVITY_ICARD_ID							= "33"; //I-Card
	final public static String ACTIVITY_LOU_ID								= "34"; //LOU
	final public static String ACTIVITY_NDA_ID								= "35"; //NDA
	final public static String ACTIVITY_OUT_LOCATION_NDA_ID					= "36"; //Out Location NDA
	final public static String ACTIVITY_DEMOTION_ID 						= "37"; //Demotion
	final public static String ACTIVITY_RETIREMENT_ID 						= "38"; //Retirement
	final public static String ACTIVITY_EMPLOYEE_AGREEMENT_ID				= "39"; //Employee Agreement
	final public static String ACTIVITY_ADDRESS_PROOF_BANK_ID				= "40"; //Address Proof Bank Letter
	final public static String ACTIVITY_INTERNSHIP_LETTER_ID				= "41"; //Internship Letter
	
	final public static String ACTIVITY_SUCCESSION_PLAN_ID					= "91"; //Succession Plan
	
	final public static String NODE_OFFER_ID 							= "1";
	final public static String NODE_APPOINTMENT_ID 						= "2";
	final public static String NODE_CONFIRMATION_ID 					= "3";
	final public static String NODE_TEMPORARY_ID 						= "4"; //Temporary
	final public static String NODE_PERMANENT_ID 						= "5"; //Permanent
	final public static String NODE_PROMOTION_ID 						= "6"; //Promotion
	final public static String NODE_INCREMENT_ID 						= "7"; //Increment
	final public static String NODE_GRADE_CHANGE_ID 					= "8"; //Grade Change
	final public static String NODE_BONAFIDE_ID 					    = "9"; //Bonafide
	final public static String NODE_CANDIDATE_OFFER_ID					= "10"; //Candidate Offer
	final public static String NODE_CANDIDATE_APPOINTMENT_ID			= "11"; //candidate Appointment
	final public static String NODE_RELEVING_ID							= "12"; //Releaving
	final public static String NODE_EXPERIENCE_ID						= "13"; //Experience
	final public static String NODE_BANK_ORDER_ID						= "14"; //Bank Order
	final public static String NODE_EXIT_FORM_ID						= "15"; //Experience
	final public static String NODE_CLEARANCE_FORM_ID					= "16"; //Bank Order
	final public static String NODE_LIFE_EVENT_ID 						= "17"; //Life Event Increment
	final public static String NODE_DEMOTION_ID 						= "18"; //Demotion
	final public static String NODE_RETIREMENT_ID 						= "19"; //Retirement
	final public static String NODE_EMPLOYEE_AGREEMENT_ID				= "20"; //Employee Agreement
	final public static String NODE_ADDRESS_PROOF_BANK_ID				= "21"; //Address Proof Bank Letter
	final public static String NODE_INTERNSHIP_LETTER_ID				= "22"; //Internship Letter
	
	final public static String DOCUMENT_RESUME 				= "Resume";
	final public static String DOCUMENT_ID_PROOF 			= "Identity proof(PAN CARD)";
	final public static String DOCUMENT_ADDRESS_PROOF 		= "Address proof(AADHAR CARD)";
	final public static String DOCUMENT_CURR_ADDRESS_PROOF 	= "Current Address proof";
	final public static String DOCUMENT_UAN			 		= "UAN Document";
	final public static String DOCUMENT_PASSPORT	 		= "Passport";
	final public static String DOCUMENT_BANK_ACCOUNT 		= "Bank Account Detail";
	final public static String DOCUMENT_OTHER 				= "Additional Document";
	final public static String DOCUMENT_COMPANY_PROFILE 	= "Company Profile";		//created by parvez date: 28-10-2022

	
	
	
	final public static String LABEL_FROM_DATE = "From Date";
	final public static String LABEL_TO_DATE = "To Date";
	
	final public static int OFFBOARD_FEEDBACKFORM_SECTION	 		= 1;
	final public static int OFFBOARD_HANDOVER_DOC_SECTION 			= 2;
	final public static int OFFBOARD_HRMANAGER_DOC_SECTION 			= 3;
	final public static int OFFBOARD_FULL_FINAL_SETTELMENT_SECTION 	= 4;
	
	final public static int REVIEW_FORM 		= 1;
	final public static int EXIT_FEEDBACK_FORM 	= 2;
	final public static int ASSESSMENT_FORM 	= 3;
	
	final public static int CORPORATE_GOAL 		= 1;
	final public static int MANAGER_GOAL 		= 2;
	final public static int TEAM_GOAL 			= 3;
	final public static int INDIVIDUAL_GOAL 	= 4;
	final public static int PERSONAL_GOAL 		= 5;
	final public static int INDIVIDUAL_TARGET 	= 6;
	final public static int INDIVIDUAL_KRA 		= 7;
	final public static int EMPLOYEE_KRA 		= 8;
     
	final public static int PROJECT_INVOICE 			= 1;
	final public static int ADHOC_INVOICE 				= 2;
	final public static int PRORETA_INVOICE 			= 3; 
	final public static int PARTIAL_INVOICE 			= 4;
	final public static int ADHOC_PRORETA_INVOICE 		= 5;
	
	final public static int CLIENT_TYPE_REGULAR 	= 0;
	final public static int CLIENT_TYPE_OTHER 		= 1;
	
	final public static int PROJECT_TYPE_REGULAR 	= 0;
	final public static int PROJECT_TYPE_OTHER 		= 1;
	
	
	/**
	 * User Alerts
	 * */
	final public static String INSERT_ALERT	 		 = "insert";
	final public static String UPDATE_ALERT	 		 = "update";
	final public static String DELETE_ALERT	 		 = "delete";
	
	final public static String INSERT_TR_ALERT	 		 = "insertTR";
	final public static String DELETE_TR_ALERT	 		 = "deleteTR";
	final public static String INSERT_WR_ALERT	 		 = "insertWR";
	final public static String DELETE_WR_ALERT	 		 = "deleteWR";
	
	final public static String INSERT_TR_ACTIVITY 		 = "insertTRActivity";
	final public static String DELETE_TR_ACTIVITY 		 = "deleteTRActivity";
	
	final public static String MY_PAY_ALERT	 		 		= "myPay";
	final public static String LEAVE_REQUEST_ALERT	 		= "leaveRequest";
	final public static String LEAVE_APPROVAl_ALERT	 		= "leaveApproval";
	final public static String REIM_REQUEST_ALERT	 		= "reimbursementRequest";
	final public static String REIM_APPROVAL_ALERT	 		= "reimbursementApproval";
	final public static String PAY_REIM	 		 			= "payReimbursement";
	final public static String TRAVEL_REQUEST_ALERT	 		= "travelRequest";
	final public static String TRAVEL_APPROVAL_ALERT 		= "travelApproval";
	final public static String UNREAD_MAIL_ALERT 			= "myMail";
	final public static String PERK_REQUEST_ALERT	 		= "perkRequest";
	final public static String PERK_APPROVAL_ALERT	 		= "perkApproval";
	final public static String PAY_PERK	 		 			= "payPerk";
	final public static String LTA_REQUEST_ALERT	 		= "ltaRequest";
	final public static String LTA_APPROVAL_ALERT	 		= "ltaApproval";
	final public static String PAY_LTA	 		 			= "payLta";
	final public static String LEAVE_ENCASH_REQUEST_ALERT	= "leaveEncashRequest";
	final public static String LEAVE_ENCASH_APPROVAL_ALERT	= "leaveEncashApproval";
	final public static String PAY_GRATUITY	 		 		= "payGratuity";
	
	final public static String REQUISITION_REQUEST_ALERT	= "requisitionRequest";
	final public static String REQUISITION_APPROVAL_ALERT 	= "requisitionApproval";
	
	final public static String LOAN_REQUEST_ALERT	 		= "loan_request";
	final public static String LOAN_APPROVAL_ALERT	 		= "loan_approval";
	
	final public static String REQUIREMENT_REQUEST_ALERT				= "requirementRequest";
	final public static String REQUIREMENT_APPROVAL_ALERT				= "requirementApproval";
	final public static String JOBCODE_REQUEST_ALERT	 				= "jobcodeRequest";
	final public static String JOBCODE_APPROVAL_ALERT	 				= "jobcodeApproval";
	final public static String MY_REVIEW_ALERT	 						= "myReviews";
	final public static String SELF_REVIEW_REQUEST_ALERT	 			= "selfReviewRequest";
	final public static String SELF_REVIEW_APPROVAL_ALERT	 			= "selfReviewApproval";
	final public static String HR_REVIEW_ALERT							= "hrReviews";
	final public static String MANAGER_REVIEW_ALERT	 					= "managerReviews";
	final public static String PEER_REVIEW_ALERT						= "peerReviews";
	final public static String CEO_REVIEW_ALERT	 					    = "ceoReviews";
	final public static String HOD_REVIEW_ALERT	 					    = "hodReviews";
	final public static String MY_GOAL_ALERT	 						= "myGoals";
	final public static String MY_KRA_ALERT	 							= "myKRAs";
	final public static String MY_TARGET_ALERT	 						= "myTargets";
	final public static String MY_PERSONAL_GOAL_ALERT	 				= "myPersonalGoal";
	final public static String MANAGER_GOALS_ALERT	 					= "managerGoals";
	final public static String GOAL_KRA_TARGET_ALERT	 				= "goalKRATarget";
	final public static String MY_LEARNING_PLAN_ALERT	 				= "myLearningPlan";
	final public static String NEW_REVIEW_ALERT	 						= "newReview";
	final public static String REVIEW_FINALIZATION_ALERT	 			= "reviewFinalization";
	final public static String INTERVIEWS_ALERT	 						= "interviews";
	final public static String NEW_JOINEES_ALERT	 					= "newJoinees";
	final public static String MANAGER_LEARNING_GAPS_ALERT				= "managerLearningGaps";
	final public static String HR_LEARNING_FINALIZATION_ALERT			= "hrLearningFinalization";
	final public static String HR_LEARNING_GAPS_ALERT					= "hrLearningGaps";
	final public static String ADD_MY_INTERVIEWS_SCHEDULED_ALERT		= "addMyINterviewsScheduled";
	final public static String REMOVE_MY_INTERVIEWS_SCHEDULED_ALERT		= "removeMyINterviewsScheduled";
	final public static String CANDIDATE_FINALIZATION_ALERT				= "candidateFinalization";
	final public static String CANDIDATE_OFFER_ACCEPTREJECT_ALERT		= "candidateOfferAcceptReject";
	final public static String EMPLOYEE_RESIGNED_ALERT					= "employeeResigned";
	final public static String EMPLOYEE_RESIGNED_UPDATE_ALERT			= "employeeResignedUpdate";
	final public static String RESIGN_APPROVAL_MANAGER_ALERT			= "resignAppovalManager";
	final public static String RESIGN_APPROVAL_HRMANAGER_ALERT			= "resignAppovalHRManager";
	
	final public static String PRO_RECURRING_BILLING_ALERT				= "proRecurringBilling";
	final public static String TRAVEL_BOOKING_ALERT 					= "travelbooking";
	final public static String LIBRARY_REQUEST_ALERT 					= "libraryRequest";
	final public static String LIBRARY_REQUEST_APPROVED_ALERT 			= "libraryRequestApproved";
	final public static String NEW_MANUAL_ALERT 						= "newManual";
	final public static String MEETING_ROOM_BOOKING_REQUEST_ALERT		= "meetingRoomBookingRequest";
	final public static String MEETING_ROOM_BOOKING_REQUEST_APPROVED_ALERT		= "meetingRoomBookingRequestApproved";
	final public static String FOOD_REQUEST_ALERT						= "foodRequest";
	final public static String NEW_CANDIDATE_FILL_ALERT					= "newCandidateFill";
	
	final public static String EMP_TERMINATED_ALERT						= "employeeTerminated";
	final public static String FORM16_RELEASE_ALERT						= "form16_release";
	
	final public static String ADD_MYTEAM_MEMBER_ALERT					= "add_myteam_member";
	final public static String NEW_JOINEE_PENDING_ALERT					= "new_joinee_pending";
	final public static String NEWS_AND_ALERTS	 		                = "news_and_alerts";
	final public static String EMP_CONFIRMATIONS_ALERT	 		        = "emp_confirmations";
	final public static String EMP_RESIGNATIONS_ALERT	 		        = "emp_resignations";
	final public static String EMP_FINAL_DAY_ALERT	 		            = "emp_final_day";
	
	/**
	 * Project Alerts
	 * */
	final public static String PRO_REQUEST_ALERT						= "pro_request";
	final public static String PRO_CREATED_ALERT						= "pro_created";
	final public static String PRO_COMPLETED_ALERT						= "pro_completed";
	final public static String PRO_NEW_RESOURCE_ALERT					= "pro_new_resource";
	final public static String TASK_ALLOCATE_ALERT						= "task_allocate";
	final public static String TASK_NEW_REQUEST_ALERT					= "task_new_request";
	final public static String TASK_ACCEPT_ALERT						= "task_accept";
	final public static String TASK_REQUEST_RESCHEDULE_ALERT			= "task_request_reschedule";
	final public static String TASK_REQUEST_REASSIGN_ALERT				= "task_request_reassign";
	final public static String TASK_REASSIGN_ALERT						= "task_reassign";
	final public static String TASK_RESCHEDULE_ALERT					= "task_reschedule";
	final public static String TASK_COMPLETED_ALERT						= "task_completed";
	final public static String TIMESHEET_RECEIVED_ALERT					= "timesheet_received";
	final public static String INVOICE_GENERATED_ALERT					= "invoice_generated";
	final public static String SHARE_DOCUMENTS_ALERT					= "share_document";
	
	
	
	

	final public static String WEEKLYOFF_COLOR		= "#a9cfff";
	final public static String HALFWEEKLYOFF_COLOR		= "#c8e1ff";

	
	
// 	******************** Billing Heads Data Type **********************
	final public static int DT_FIXED					= 1;
	final public static int DT_PRORATA_INDIVIDUAL		= 2;
	final public static int DT_PRORATA_OVERALL			= 3;
	final public static int DT_OPE_INDIVIDUAL			= 4;
	final public static int DT_OPE_OVERALL				= 5;
	final public static int DT_MILESTONE				= 6;
	final public static int DT_OPE						= 7;
	
//	******************** Billing Heads Other Variables **********************
	final public static int OV_ONLY_RESOURCE		= 1;
	final public static int OV_ONLY_TASK			= 2;
	final public static int OV_BOTH					= 3;
	
//	******************** Tax Dedution Type **********************
	final public static int TD_INVOICE				= 1;
	final public static int TD_CUSTOMER				= 2;
//	final public static int TD_BOTH					= 3;
	
	
//	******************** Invoice Type **********************
	final public static int INVC_FORMAT_ONE				= 1;
	final public static int INVC_FORMAT_TWO				= 2;
	
//	******************** Head Type **********************
	final public static String HEAD_PARTI				= "PARTI";
	final public static String HEAD_OPE					= "OPE";
	final public static String HEAD_TAX					= "TAX";

//	******************** Amount Receive Type **********************
	final public static int AMT_RECEIVE_BILL_AMT				= 1;
	final public static int AMT_RECEIVE_TAX_DEDUCT				= 2;
	final public static int AMT_RECEIVE_WRITE_OFF				= 3;
	final public static int AMT_RECEIVE_TAX_DEDUCT_PREV_YR		= 4;

//	******************** Currency Ids **********************
	final public static String INR_CURR_ID		= "3";
	
//	 ******************** Validation Form Names ********************** 
	
	final public static String ADD_UPDATE_COMPANY						= "ADD_UPDATE_COMPANY";
	final public static String ADD_UPDATE_EMPLOYEE						= "ADD_UPDATE_EMPLOYEE";
	final public static String ADD_UPDATE_CANDIDATE						= "ADD_UPDATE_CANDIDATE";
	final public static String REQUIREMENT_REQUEST						= "REQUIREMENT_REQUEST";
	
	
//	******************** VDA Formula Fix Values ************************
	final public static double VDA_FORMULA_FIX_VAL_1 		= 4.96;
	final public static double VDA_FORMULA_FIX_VAL_2 		= 4.53;
	final public static double VDA_FORMULA_FIX_VAL_3 		= 450;
	
	/**
	 * Images and Documents Path 
	 * */
	final public static String I_COMPANY			= "Company";
	final public static String I_IMAGE				= "Image";
	final public static String I_DOC_SIGN			= "Doc_Sign";
	final public static String I_IMAGE_SMALL		= "ImageSmall";
	final public static String I_IMAGE_COVER		= "Cover";
	final public static String I_DOCUMENT			= "Document";
	final public static String I_ORGANISATION		= "Organisation";
	final public static String I_PEOPLE				= "People";
	final public static String I_CANDIDATE			= "Candidate";
	final public static String I_TRAINER			= "Trainer";
	final public static String I_CUSTOMER			= "Customer";
	final public static String I_CUSTOMER_SPOC		= "SPOC";
	final public static String I_CUSTOMER_BRAND		= "BRAND";
	final public static String I_PERFORMANCE		= "Performance";
	final public static String I_RECRUITMENT		= "Recruitment";
	final public static String I_COLLATERAL			= "Collateral";
	final public static String I_REIMBURSEMENTS		= "Reimbursements";
	final public static String I_PERKS				= "Perks"; 
	final public static String I_CTCVARIABLES		= "CTCVariables";
	final public static String I_INVESTMENTS		= "Investments";
	final public static String I_MEDICAL			= "Medical";
	final public static String I_ATTACHMENT			= "Attachment";
	final public static String I_EDUCATION_DOC		= "Education_Doc";
	final public static String I_16x16				= "16x16";
	final public static String I_22x22				= "22x22";
	final public static String I_60x60				= "60x60";
	final public static String I_100x100			= "100x100";
	final public static String I_TEMP				= "Temp";
	final public static String I_FEEDS				= "Feeds";
	final public static String I_OFFBOARD			= "OffBoard";
	final public static String I_EVENTS			    = "Events";
	final public static String I_BOOKS			    = "Books";
	final public static String I_DISHES			    = "Dishes";
	final public static String I_TRAVELS			= "Travels";
	final public static String I_FORM16				= "Form16";
	final public static String I_COMPANY_MANUAL		= "COMPANY_MANUAL";
	final public static String I_PERKS_SALARY 		= "PerksSalary";
	final public static String I_REIMBURSEMENTS_CTC_HEAD = "ReimbursementCTCHead";
	final public static String I_BANK_EXCEL_STMT 	= "ExcelBankStatements";
	final public static String I_PREVIOUS_EMPLOYMENT_DOC = "Previous_Employment_Doc";
	final public static String I_OFFER_LETTER 		= "OFFER_LETTER";		//added by parvez date: 29-06-2022
	final public static String I_INBOX_ATTACHMENT 	= "INBOX_ATTACHMENT";		//added by Parvez date: 16-12-2022
	//===end parvez date: 29-06-2022===
	
	/**
	 * Requisition Type
	 * */
	final public static int R_N_REQUI_DOCUMENT 			= 1;
	final public static int R_N_REQUI_INFRASTRUCTURE	= 2; 
	final public static int R_N_REQUI_OTHER		 		= 3;
	
	final public static String R_S_REQUI_DOCUMENT		= "Document";
	final public static String R_S_REQUI_INFRASTRUCTURE	= "Infrastructure";
	final public static String R_S_REQUI_OTHER			= "Other";
	/**
	 * Requisition Type End
	 * */
	
	
	/**
	 * Check Login Type Start
	 **/
	final public static int NORMAL_LOGIN					= 1;
	final public static int CUSTOMER_LOGIN					= 2;
	/**
	 * Check Login Type End
	 **/
	
	
	/**
	 * Feed Aligned Type Start
	 **/
	final public static int PROJECT						= 1;
	final public static int TASK						= 2; 
	final public static int PRO_TIMESHEET				= 3;
	final public static int DOCUMENT					= 4;
	final public static int INVOICE						= 5;
	/**
	 * Feed Aligned Type End
	 **/
	
	
	/**
	 * Feed Shared Type Start
	 **/
	final public static int S_PUBLIC					= 0;
	final public static int S_TEAM						= 1; 
	final public static int S_RESOURCE					= 2;
	final public static int S_CUSTOMER					= 3;
	/**
	 * Feed Shared Type End
	 **/
	
	/**
	 * Feed Type Start
	 **/
	final public static int FT_FEED					= 0;
	final public static int FT_ACTIVITY				= 1;
	/**
	 * Feed Type End
	 **/
	
	/**
	 * Recruitment Source Type start
	 **/
	final public static int SOURCE_HR						= 0;
	final public static int SOURCE_RECRUITER				= 1;
	final public static int SOURCE_REFERENCE				= 2;
	final public static int SOURCE_WEBSITE					= 3;
	final public static int SOURCE_JOB_PORTAL				= 4;
	final public static int SOURCE_SOCIAL_SITES				= 5;
	final public static int SOURCE_CONSULTANT				= 6;
	final public static int SOURCE_WALK_IN					= 7;
	final public static int SOURCE_OTHER					= 8;
	
	
	final public static String SOURCE_HR_LBL				= "HR Manager";
	final public static String SOURCE_RECRUITER_LBL			= "Recruiter";
	final public static String SOURCE_REFERENCE_LBL			= "Employee Referral";
	final public static String SOURCE_WEBSITE_LBL			= "Website";
	final public static String SOURCE_JOB_PORTAL_LBL		= "Online Job Portal";
	final public static String SOURCE_SOCIAL_SITES_LBL		= "Social Sites";
	final public static String SOURCE_CONSULTANT_LBL		= "Consultant";
	final public static String SOURCE_WALK_IN_LBL			= "Walk-in";
	final public static String SOURCE_OTHER_LBL				= "Other";
	
	/**
	 * Recruitment Source Type End
	 **/
	
	
	/**
	 * Candidate Activity Id start
	 **/
	final public static int CANDI_ACTIVITY_APPLY_ID 							= 1;
	final public static int CANDI_ACTIVITY_APPLI_SHORTLIST_OR_REJECT_ID			= 2;
	final public static int CANDI_ACTIVITY_INTERVIEW_SCHEDULE_ID 				= 3;
	final public static int CANDI_ACTIVITY_ROUND_SHORTLIST_OR_REJECT_ID			= 4;
	final public static int CANDI_ACTIVITY_FINALIZE_AND_OFFER_ID				= 5;
	final public static int CANDI_ACTIVITY_SALARY_RENIGOTIATION_ID				= 6;
	final public static int CANDI_ACTIVITY_OFFER_ACCEPT_OR_REJECT_ID			= 7;
	final public static int CANDI_ACTIVITY_ONBOARDING_ID						= 8;
	final public static int CANDI_ACTIVITY_OFFER_BACKOUT_ID						= 9;
	final public static int CANDI_ACTIVITY_OFFER_ONHOLD_ID						= 10;
	final public static int CANDI_ACTIVITY_CANDI_JD_CHANGE_ID					= 11;
	/**
	 * Candidate Activity Id end
	 **/
	
	/**
	 * Feature Management start
	 **/
	final public static String F_ADD_LEGAL_ENTITY							= "ADD_LEGAL_ENTITY";
	final public static String F_DELETE_LEGAL_ENTITY						= "DELETE_LEGAL_ENTITY";
	final public static String F_ADD_IN_LOGIN_USER_ORG						= "ADD_IN_LOGIN_USER_ORG";
	final public static String F_LOGIN_USER_ORG_IN_FILTER					= "LOGIN_USER_ORG_IN_FILTER";
	final public static String F_ADD_EMPLOYEE_8_STEP						= "ADD_EMPLOYEE_8_STEP";
	final public static String F_BULK_EMPLOYEE_ACTIVITY						= "BULK_EMPLOYEE_ACTIVITY";
	final public static String F_LOGIN_USER_ORG_IN_CHANGE_USER_TYPE			= "LOGIN_USER_ORG_IN_CHANGE_USER_TYPE";
	final public static String F_SHOW_ONLY_LOGIN_USER_ORG					= "SHOW_ONLY_LOGIN_USER_ORG";
	final public static String F_SHOW_LOGIN_USER_ORG_SBU_IN_ADD_SERVICE		= "SHOW_LOGIN_USER_ORG_SBU_IN_ADD_SERVICE";
	final public static String F_NEW_REQUIREMENT_WO_WORKFLOW				= "NEW_REQUIREMENT_WO_WORKFLOW";
	final public static String F_PROJECT_DASH_PROJECT_EXPENSE				= "PROJECT_DASH_PROJECT_EXPENSE";
	final public static String F_PROJECT_DASH_SKILL_CHART					= "PROJECT_DASH_SKILL_CHART";
	final public static String F_MY_HOME_MY_TEAM							= "MY_HOME_MY_TEAM";
	final public static String F_PROJECT_DASH_WEEKLY_WORK_PROGRESS			= "PROJECT_DASH_WEEKLY_WORK_PROGRESS";
	final public static String F_PROJECT_DASH_WORK_PROGRESS					= "PROJECT_DASH_WORK_PROGRESS";
	final public static String F_GOAL_ATTRIBUTE_ALIGN				    	= "GOAL_ATTRIBUTE_ALIGN";
	final public static String F_GOAL_FREQUENCY					            = "GOAL_FREQUENCY";
	final public static String F_3_STEP_PAYROLL_PROCESS 					= "3_STEP_PAYROLL_PROCESS";
	final public static String F_USERTYPE_HEALTH_CARE 						= "USERTYPE_HEALTH_CARE";
	final public static String F_USERTYPE_GENERAL 							= "USERTYPE_GENERAL";
	final public static String F_USERTYPE_FINANCE 							= "USERTYPE_FINANCE";
	final public static String F_USERTYPE_GENERAL_MANAGER 					= "USERTYPE_GENERAL_MANAGER";
	final public static String F_USERTYPE_ONLY_MANAGER 						= "USERTYPE_ONLY_MANAGER";
	final public static String F_USERTYPE_GENERAL_HOD 						= "USERTYPE_GENERAL_HOD";
	final public static String F_USERTYPE_ONLY_HOD 							= "USERTYPE_ONLY_HOD";
	final public static String F_EMP_PREV_CMP_TDS_DETAILS 					= "EMP_PREV_CMP_TDS_DETAILS";
	final public static String F_FILTER_PAYCYCLE_OR_MONTH 					= "FILTER_PAYCYCLE_OR_MONTH";
	final public static String F_SELECT_PREV_PAYCYCLE 						= "SELECT_PREV_PAYCYCLE";
	final public static String F_EMP_LOAN_AUTO_APPROVE 						= "EMP_LOAN_AUTO_APPROVE";
	final public static String F_CHECK_WEEKLYOFF_HOLIDAY_APPLY_LEAVE 		= "CHECK_WEEKLYOFF_HOLIDAY_APPLY_LEAVE";
	final public static String F_TASKRIG_CLAIM_EXTRA_WORKING		 		= "TASKRIG_CLAIM_EXTRA_WORKING";
	final public static String F_SHOW_ALL_PRO_DATA_TO_TL			 		= "SHOW_ALL_PRO_DATA_TO_TL";
	final public static String F_SHOW_CONTROL_PANEL_ALL_USER			 	= "SHOW_CONTROL_PANEL_ALL_USER";
	final public static String F_SHOW_BULK_EXPENSES_LINK				 	= "SHOW_BULK_EXPENSES_LINK";
	final public static String F_CANARA_BANK_CODE						 	= "CANARA_BANK_CODE";
	final public static String F_PNB_BANK_CODE							 	= "PNB_BANK_CODE";
	final public static String F_ADD_EMPLYOER_PF_IN_TDS_CALCLATION			= "ADD_EMPLYOER_PF_IN_TDS_CALCLATION";
	final public static String F_SHOW_GEO_FENCE_ACCESS_BUTTON				= "SHOW_GEO_FENCE_ACCESS_BUTTON";
	final public static String F_CLOCK_ON_OFF_BLOCK_SHOW					= "CLOCK_ON_OFF_BLOCK_SHOW";
	final public static String F_SHOW_SALARY_IN_PROFILE_USERWISE			= "SHOW_SALARY_IN_PROFILE_USERWISE";
	final public static String F_SHOW_GOAL_KRA_TARGET						= "SHOW_GOAL_KRA_TARGET";
	final public static String F_SHOW_ACTIVITY_REVIEW_FINALIZE				= "SHOW_ACTIVITY_REVIEW_FINALIZE";
	final public static String F_SHOW_SELF_REVIEW_LINK						= "SHOW_SELF_REVIEW_LINK";
	final public static String F_SHOW_REVIEW_BALANCE_SCORE_IN_MYSELF		= "SHOW_REVIEW_BALANCE_SCORE_IN_MYSELF";
	final public static String F_SHOW_REVIEW_FEEDBACK_IN_PDF_FORMAT			= "SHOW_REVIEW_FEEDBACK_IN_PDF_FORMAT";
	final public static String F_REVIEW_FEEDBACK_EDIT_AFTER_REVIEW			= "REVIEW_FEEDBACK_EDIT_AFTER_REVIEW";
	final public static String F_AFTER_PAYROLL_PROCESS_ACTIVE_ABSENCE_AND_TIME	= "AFTER_PAYROLL_PROCESS_ACTIVE_ABSENCE_AND_TIME";
	final public static String F_SHOW_UPDATE_TIME_ENTRIES_LINK				= "SHOW_UPDATE_TIME_ENTRIES_LINK";
	final public static String F_SHOW_EMPLOYEE_MIDDLE_NAME					= "SHOW_EMPLOYEE_MIDDLE_NAME";
	final public static String F_HIDE_ORG_DETAILS_IN_REPORT					= "HIDE_ORG_DETAILS_IN_REPORT";
	final public static String F_SHOW_ITR_FILLING_REQUEST_BLOCK				= "SHOW_ITR_FILLING_REQUEST_BLOCK";
	final public static String F_DISABLE_NET_TAKE_HOME_SALARY				= "DISABLE_NET_TAKE_HOME_SALARY";
	final public static String F_ASSIGN_SHIFT_ON_BASIS_OF_RULES				= "ASSIGN_SHIFT_ON_BASIS_OF_RULES";
	final public static String F_ENABLE_HD_FD_EXCEPTION_RULE				= "ENABLE_HD_FD_EXCEPTION_RULE";
	final public static String F_ENABLE_APPLY_LEAVE_FREEZE					= "ENABLE_APPLY_LEAVE_FREEZE";
	final public static String F_NON_DEDUCT_SAL_HEAD_IN_ARREAR				= "F_NON_DEDUCT_SAL_HEAD_IN_ARREAR";
	final public static String F_AUTO_DELETE_ATTENDANCE_AT_ROSTER_IMPORT	= "AUTO_DELETE_ATTENDANCE_AT_ROSTER_IMPORT";
	final public static String F_SHOW_MONTHLY_SALARY_IN_OFFER_LETTER		= "SHOW_MONTHLY_SALARY_IN_OFFER_LETTER";
	final public static String F_SHOW_DEFAULT_TWO_REFERENCE_IN_ADD_EMPLOYEE	= "SHOW_DEFAULT_TWO_REFERENCE_IN_ADD_EMPLOYEE";
	final public static String F_SHOW_CURR_ADDRESS_PROOF_IN_DOCUMENT		= "SHOW_CURR_ADDRESS_PROOF_IN_DOCUMENT";
	final public static String F_SHOW_UAN_IN_DOCUMENT						= "SHOW_UAN_IN_DOCUMENT";
	final public static String F_SHOW_PASSPORT_IN_DOCUMENT					= "SHOW_PASSPORT_IN_DOCUMENT";
	final public static String F_SHOW_BANK_ACCOUNT_IN_DOCUMENT				= "SHOW_BANK_ACCOUNT_IN_DOCUMENT";
	final public static String F_SHOW_PF_START_DATE_IN_ADD_EMPLOYEE			= "SHOW_PF_START_DATE_IN_ADD_EMPLOYEE";
	final public static String F_ENABLE_SALARY_BANK_UPLOADER				= "ENABLE_SALARY_BANK_UPLOADER";
	final public static String F_ENABLE_GLOBAL_MANAGER_ACCESS				= "ENABLE_GLOBAL_MANAGER_ACCESS";
	final public static String F_ENABLE_ORG_ATTENDANCE_APPROVAL_STATUS_MAIL	= "ENABLE_ORG_ATTENDANCE_APPROVAL_STATUS_MAIL";
	final public static String F_ENABLE_ORG_SALARY_APPROVAL_STATUS_MAIL		= "ENABLE_ORG_SALARY_APPROVAL_STATUS_MAIL";
	final public static String F_DISABLE_LEARNING_GAP_IN_REVIEW_FINALIZATION		= "DISABLE_LEARNING_GAP_IN_REVIEW_FINALIZATION";
	final public static String F_EDIT_EMPLOYEE_SUPPORTING_DOCUMENT			= "EDIT_EMPLOYEE_SUPPORTING_DOCUMENT";
	final public static String F_SET_TIME_EXCEPTION_APPLY_LIMIT				= "SET_TIME_EXCEPTION_APPLY_LIMIT";
	final public static String F_INSTANCE_NAMEWISE_FEATURE					= "INSTANCE_NAMEWISE_FEATURE";
	final public static String F_FINALISATION_WITH_FIRST_ROUND_FEEDBACK		= "FINALISATION_WITH_FIRST_ROUND_FEEDBACK";//Created By Dattatray Date:18-10-21
	final public static String F_IT_DECLRATION_SUBMISSION_OPEN_MONTH		= "IT_DECLRATION_SUBMISSION_OPEN_MONTH";//Created By Dattatray Date:21-10-21
	final public static String F_TDS_CALCULATION_ON_IT_DECLARATION_SUBMISSION_ONLY		= "TDS_CALCULATION_ON_IT_DECLARATION_SUBMISSION_ONLY";//Created By Dattatray Date:22-10-21
	final public static String F_REVIEW_PUBLISH_MAIL_SENT_TO_REVIEWEE		= "REVIEW_PUBLISH_MAIL_SENT_TO_REVIEWEE";//Created By Dattatray Date:11-11-21
	final public static String F_AUTO_CALCULATE_COMPOFF_FOR_EXTRA_WORKING	= "AUTO_CALCULATE_COMPOFF_FOR_EXTRA_WORKING";
	final public static String F_DISABLE_RESIGNATION_APPROVAL_REASON_FULL_AND_FIANL_PDF		= "DISABLE_RESIGNATION_APPROVAL_REASON_FULL_AND_FIANL_PDF";
	final public static String F_EDIT_PERSONAL_GOAL_BY_MANAGER				= "EDIT_PERSONAL_GOAL_BY_MANAGER";
	final public static String F_APPROVE_DENY_PERSONAL_GOAL_BY_MANAGER		= "APPROVE_DENY_PERSONAL_GOAL_BY_MANAGER";
	final public static String F_DISABLE_SELF_APPRAISAL_RATING_DURING_FINAL_RATING_CALCULATION		= "DISABLE_SELF_APPRAISAL_RATING_DURING_FINAL_RATING_CALCULATION";
	final public static String F_GOAL_KRA_TARGET_TASK_SHOWN_IN_REVIEW_CREATION_FORM		= "GOAL_KRA_TARGET_TASK_SHOWN_IN_REVIEW_CREATION_FORM";
	final public static String F_ENABLE_JOINING_BONUS_DETAILS				= "ENABLE_JOINING_BONUS_DETAILS";	
	final public static String F_ENABLE_ADD_AMOUNT_IN_EMPLOYER_PF_ONLY		= "ENABLE_ADD_AMOUNT_IN_EMPLOYER_PF_ONLY";	
	final public static String F_ENABLE_HIRING_PROCEDURE_IN_CLOSED_JOB_REQUIREMENT		= "ENABLE_HIRING_PROCEDURE_IN_CLOSED_JOB_REQUIREMENT";	
	final public static String F_ENABLE_BALANCE_SCORECARD_CALCULATION_USERTYPE		= "ENABLE_BALANCE_SCORECARD_CALCULATION_USERTYPE";	
	final public static String F_ADD_BUDGET_LINK_UNABLE						= "ADD_BUDGET_LINK_UNABLE";	
	final public static String F_SHOW_SELF_MANAGER_REVIEW_FEEDBACK_TO_HOD 	= "SHOW_SELF_MANAGER_REVIEW_FEEDBACK_TO_HOD";	
	final public static String F_ENABLE_MIS_REPORT_QUICK_LINK 				= "ENABLE_MIS_REPORT_QUICK_LINK";	
	final public static String F_DOCUMENT_ATTACH_OFFER_LETTER 				= "DOCUMENT_ATTACH_OFFER_LETTER";	
	final public static String F_REVIEW_REOPEN_BY_HR_GHR_FOR_UPDATE_FEEDBACK = "REVIEW_REOPEN_BY_HR_GHR_FOR_UPDATE_FEEDBACK";	
	final public static String F_REVIEW_REOPEN_BY_MANAGER_FOR_UPDATE_FEEDBACK = "REVIEW_REOPEN_BY_MANAGER_FOR_UPDATE_FEEDBACK";	
	final public static String F_HR_APPROVAL_REQUIRED_FOR_HOD_FEEDBACK 		= "HR_APPROVAL_REQUIRED_FOR_HOD_FEEDBACK";	
	final public static String F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT 	= "HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT";	
	final public static String F_MY_TIME_DISABLE_TAB 						= "MY_TIME_DISABLE_TAB";	//Created By Parvez Date:05-08-2022
	final public static String F_MY_TIME_DISABLE_LINK 						= "MY_TIME_DISABLE_LINK";	//Created By Parvez Date:05-08-2022
	final public static String F_MY_PAY_DISABLE_TAB 						= "MY_PAY_DISABLE_TAB";	//Created By Parvez Date:05-08-2022
	final public static String F_MY_PAY_DISABLE_LINK 						= "MY_PAY_DISABLE_LINK";	//Created By Parvez Date:05-08-2022
	final public static String F_MY_HR_DISABLE_TAB 							= "MY_HR_DISABLE_TAB";	//Created By Parvez Date:06-08-2022
	final public static String F_TIMELINE_PREVIOUS_EMPLOYMENT_DISABLE 		= "TIMELINE_PREVIOUS_EMPLOYMENT_DISABLE";	//Created By Parvez Date:06-08-2022
	final public static String F_EMPLOYEE_HOBBIES_DISABLE 					= "EMPLOYEE_HOBBIES_DISABLE";	//Created By Parvez Date:06-08-2022
	final public static String F_DISABLE_AVAILABILITY_OF_INTERVIEW 			= "DISABLE_AVAILABILITY_OF_INTERVIEW";	//Created By Parvez Date:09-08-2022
	final public static String F_DISABLE_WORK_TIME_IN_ABOUT_ME 				= "DISABLE_WORK_TIME_IN_ABOUT_ME";	//Created By Parvez Date:12-08-2022
	final public static String F_DISABLE_EMPLOYEE_FAMILY_INFORMATION 		= "DISABLE_EMPLOYEE_FAMILY_INFORMATION";	//Created By Parvez Date:06-09-2022
	final public static String F_DISABLE_MEDICAL_DETAILS 					= "DISABLE_MEDICAL_DETAILS";	//Created By Parvez Date:06-09-2022
	final public static String F_READ_ONLY_WORKFLOW 						= "READ_ONLY_WORKFLOW";	//Created By Parvez Date:13-09-2022
	final public static String F_EXTRA_WORKING_LAPS_DAYS_LIMIT_FOR_COMPOFF_LEAVE 		= "EXTRA_WORKING_LAPS_DAYS_LIMIT_FOR_COMPOFF_LEAVE";	//Created By Parvez Date:16-09-2022
	final public static String F_HR_GHR_ONLY_COMMENT_FOR_LEAVE 				= "HR_GHR_ONLY_COMMENT_FOR_LEAVE";	//Created By Parvez Date:01-10-2022
	final public static String F_LEAVE_WORK_FLOW_REQUEST_FOR_SAME_MEMBER 	= "LEAVE_WORK_FLOW_REQUEST_FOR_SAME_EMPLOYEE";	//Created By Parvez Date:18-10-2022
	final public static String F_USER_BACKGROUND_PHOTO 						= "USER_BACKGROUND_PHOTO";	//Created By Parvez Date:27-10-2022
	final public static String F_COMPANY_PROFILE_DOCUMENT 					= "COMPANY_PROFILE_DOCUMENT";	//Created By Parvez Date:28-10-2022
	final public static String F_COLOR_FOR_NIGATIVE_LEAVE_BALANCE 			= "COLOR_FOR_NIGATIVE_LEAVE_BALANCE";	//Created By Parvez Date:28-10-2022
	final public static String F_PASSWORD_PROTECTED_SALARY_SLIP				= "PASSWORD_PROTECTED_SALARY_SLIP";	//Created By Parvez Date:19-10-2022
	final public static String F_SHOW_ADDITIONAL_DETAILS_IN_PROJECT_CREATION= "SHOW_ADDITIONAL_DETAILS_IN_PROJECT_CREATION";	//Created By Parvez Date:21-11-2022
	final public static String F_SHOW_WORK_LOCATION_IN_APPROVE_PAY_AND_PAY	= "SHOW_WORK_LOCATION_IN_APPROVE_PAY_AND_PAY";	//Created By Parvez Date:27-12-2022
	final public static String F_DISABLE_ON_SITE_IN_TIMESHEET				= "DISABLE_ON_SITE_IN_TIMESHEET";	//Created By Parvez Date:27-12-2022
	final public static String F_SHOW_BILLABLE_NONBILLABLE_TASK_MESSAGE		= "SHOW_BILLABLE_NONBILLABLE_TASK_MESSAGE";	//Created By Parvez Date:02-01-2023
	final public static String F_LEAVE_BALANCE_FOR_EMPLOYEE_JOINING_MONTH	= "LEAVE_BALANCE_FOR_EMPLOYEE_JOINING_MONTH";	//Created By Parvez Date:04-01-2023
	final public static String F_LEAVE_PRIOR_DAYS_NOTIFICATION				= "LEAVE_PRIOR_DAYS_NOTIFICATION";	//Created By Parvez Date:06-01-2023
	final public static String F_GAP_BETWEEN_TWO_APPLIED_LONG_LEAVE			= "GAP_BETWEEN_TWO_APPLIED_LONG_LEAVE";	//Created By Parvez Date:07-01-2023
	final public static String F_PF_ESI_INCLUDE_IN_ARREAR					= "PF_ESI_INCLUDE_IN_ARREAR";	//Created By Parvez Date:16-01-2023
	final public static String F_GRATUITY_PAID_ON_APPROVED_AMOUNT			= "GRATUITY_PAID_ON_APPROVED_AMOUNT";	//Created By Parvez Date:20-01-2023
	final public static String F_ENABLE_GLOBAL_HOD_ACCESS					= "ENABLE_GLOBAL_HOD_ACCESS";	//Created By Parvez Date:13-02-2023
	final public static String F_ROSTER_TIME_LESS_THAN_ONE_HOUR				= "ROSTER_TIME_LESS_THAN_ONE_HOUR";	//Created By Parvez Date:17-02-2023
	final public static String F_DIRECT_OVER_TIME_CALCULATE					= "DIRECT_OVER_TIME_CALCULATE";	//Created By Parvez Date:17-02-2023
	final public static String F_TEN_STAR_RATING_FOR_REVIEW					= "TEN_STAR_RATING_FOR_REVIEW";		//Created By Parvez Date: 09-03-2023
	final public static String F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD			= "GOAL_NOT_SHOWING_IN_PEER_AND_HOD";		//Created By Parvez Date: 14-03-2023
	final public static String F_BACKUP_EMPLOYEE_FOR_LEAVE			        = "BACKUP_EMPLOYEE_FOR_LEAVE";		//Created By Parvez Date: 18-03-2023
	final public static String F_DISABLE_REVIEW_REOPEN			        	= "DISABLE_REVIEW_REOPEN";		//Created By Parvez Date: 20-03-2023
	final public static String F_HR_GHR_APPROVAL_FOR_FINAL_RATING_AND_COMMENT = "HR_GHR_APPROVAL_FOR_FINAL_RATING_AND_COMMENT";	//Created By Parvez Date: 20-03-2023
	
	/**
	 * Feature Management end
	 **/
	
	/**
	 * Allowance Conditon and Logic start
	 * **/
//	 ******************** CONDITION ********************** 
	final public static int A_NO_OF_DAYS_ID 				= 1;
	final public static int A_NO_OF_HOURS_ID				= 2;
	final public static int A_CUSTOM_FACTOR_ID 				= 3;
	final public static int A_GOAL_KRA_TARGET_ID			= 4;
	final public static int A_KRA_ID						= 5;
	final public static int A_NO_OF_DAYS_ABSENT_ID 			= 6;
	
	final public static String A_NO_OF_DAYS					= "No. of Days Worked";
	final public static String A_NO_OF_HOURS				= "No. of Earned Hours";
	final public static String A_CUSTOM_FACTOR				= "Custom Factor";
	final public static String A_GOAL_KRA_TARGET			= "Goal/Target";
	final public static String A_KRA						= "KRA";
	final public static String A_NO_OF_DAYS_ABSENT			= "No. of Days Absent";
	
//	 ******************** LOGIC ********************** 
	final public static int A_FIXED_ONLY_ID 						= 1;
	final public static int A_FIXED_X_DAYS_ID 						= 2;
	final public static int A_FIXED_X_HOURS_ID 						= 3;
	final public static int A_FIXED_X_CUSTOM_ID 					= 4;
	final public static int A_EQUAL_TO_SALARY_HEAD_ID 				= 5;
	final public static int A_SALARY_HEAD_X_DAYS_ID 				= 6;
	final public static int A_SALARY_HEAD_X_HOURS_ID 				= 7;
	final public static int A_SALARY_HEAD_X_CUSTOM_ID 				= 8;
	final public static int A_FIXED_X_ACHIEVED_ID 					= 9;
	final public static int A_SALARY_HEAD_X_ACHIEVED_ID 			= 10;
	final public static int A_FIXED_AND_PER_HOUR_ID					= 11;
	final public static int A_FIXED_AND_PER_DAY_ID					= 12;
	final public static int A_FIXED_ONLY_DEDUCTION_ID 				= 13;
	
	final public static String A_FIXED_ONLY 						= "Fixed only";
	final public static String A_FIXED_X_DAYS 						= "Fixed x Days";
	final public static String A_FIXED_X_HOURS 						= "Fixed x Hours";
	final public static String A_FIXED_X_CUSTOM 					= "Fixed x Custom";
	final public static String A_EQUAL_TO_SALARY_HEAD 				= "Equal to Salary Head";
	final public static String A_SALARY_HEAD_X_DAYS 				= "Salary Head x Days";
	final public static String A_SALARY_HEAD_X_HOURS 				= "Salary Head x hours";
	final public static String A_SALARY_HEAD_X_CUSTOM 				= "Salary Head x Custom";
	final public static String A_FIXED_X_ACHIEVED 					= "Fixed x Achieved";
	final public static String A_SALARY_HEAD_X_ACHIEVED 			= "Salary Head x Achieved";
	final public static String A_FIXED_AND_PER_HOUR					= "Fixed and Per Hour";
	final public static String A_FIXED_AND_PER_DAY					= "Fixed and Per Day";
	final public static String A_FIXED_ONLY_DEDUCTION				= "Fixed only Deduction";
	
	/**
	 * Allowance Conditon and Logic End
	 * **/
	
	/**
	 * Work Flow start
	 * */
	final public static String WORK_FLOW_LEAVE 						= "Leave";
	final public static String WORK_FLOW_REIMBURSEMENTS				= "Reimbursements";
	final public static String WORK_FLOW_TIMESHEET					= "TimeSheet";
	final public static String WORK_FLOW_TRAVEL						= "Travel";
	final public static String WORK_FLOW_PERK						= "Perk";
	final public static String WORK_FLOW_LEAVE_ENCASH				= "LeaveEncash";
	final public static String WORK_FLOW_LTA						= "LTA";
	final public static String WORK_FLOW_REQUISITION				= "Requisition";
	final public static String WORK_FLOW_LOAN						= "Loan";
	final public static String WORK_FLOW_RESIGN						= "Resign";
	final public static String WORK_FLOW_RECRUITMENT				= "Recruitment";
	final public static String WORK_FLOW_RESUME_SHORTLIST			= "Resume_Shortlist";
	final public static String WORK_FLOW_SELF_REVIEW				= "SelfReview";
	final public static String WORK_FLOW_TERMINATION				= "Termination";
	final public static String WORK_FLOW_EXCEPTION					= "Exception";
	final public static String WORK_FLOW_PERSONAL_GOAL				= "PersonalGoal";//Created By Dattatray Date:26-08-21 Note : Added WORK_FLOW_PERSONAL_GOAL
	final public static String WORK_FLOW_LEARNING_REQUEST			= "LearningRequest";//Created By Parvez Date:27-09-21 
	
	
	/**
	 * Work Flow End
	 * */
	
	/**
	 * Cafeteria Start
	 * */
	final public static String BREAKFAST 					= "1";
	final public static String LUNCH 						= "2";
	final public static String DINNER 						= "3";
	final public static String OTHER 						= "4";
	/**
	 * Cafeteria End 
	 * */
	
	/**
	 * Salary Structure Type Start
	 * */
	final public static int S_LEVEL_WISE				= 1;
	final public static int S_GRADE_WISE				= 2;
	/**
	 * Salary Structure Type End
	 * */ 
	
	/**
	 * Reimbursement Policy Start
	 * */
	final public static int REIMBURSEMENTS_MOBILE_BILL				= 1;
	final public static int REIMBURSEMENTS_LOCAL					= 2;
	final public static int REIMBURSEMENTS_TRAVEL_ADVANCE			= 3;
	final public static int REIMBURSEMENTS_CLAIM					= 4;
	/**
	 * Reimbursement Policy End
	 * */
	
	/**
	 * Log Start
	 * */
	final public static int L_ADD 		= 1;
	final public static int L_UPDATE 	= 2;
	final public static int L_DELETE 	= 3;

	final public static String L_EMPLOYEE 			= "Employee";
	final public static String L_CANDIDATE 			= "Candidate";
	final public static String L_APPROVE_SALARY 	= "Approve Salary";
	final public static String L_PAID_SALARY 		= "Paid Salary";
	final public static String L_ADD_REIMBURSEMENTS = "Add Reimbursements";
	/**
	 * Log End
	 * */
	
	/**
	 * by kalpana on 2/1/2017
	 * added from history need to remove after updating whole UI
	 */
	final public static String NAVI_103					= "CS";
	final public static String NAVI_104					= "LEOL";
	final public static String NAVI_105					= "SBU";
	final public static String NAVI_106					= "DEPT";
	final public static String NAVI_107					= "SRVS";
	final public static String NAVI_108					= "LDG";
	final public static String NAVI_109					= "BNK";
	
	final public static String NAVI_111					= "MACL";
	final public static String NAVI_112					= "MW";
	final public static String NAVI_113					= "NS";
	
	final public static String NAVI_115					= "AC";
	final public static String NAVI_116					= "SS";
	final public static String NAVI_117					= "ORNT";
	
	final public static String NAVI_119					= "BR";
	final public static String NAVI_120					= "RP";
	final public static String NAVI_121					= "LP";
	final public static String NAVI_122					= "OP";
	final public static String NAVI_123					= "HLD";
	final public static String NAVI_124					= "CM";
	
	final public static String NAVI_126					= "PS";
	final public static String NAVI_127					= "ISP";
	final public static String NAVI_128					= "BSP";
	final public static String NAVI_129					= "PRKS";
	final public static String NAVI_130					= "LA";
	
	final public static String NAVI_132					= "SRI";
	final public static String NAVI_133					= "PTS";
	final public static String NAVI_134					= "ITS";
	final public static String NAVI_135					= "ITUS";
	final public static String NAVI_136					= "ITDUS";
	final public static String NAVI_137					= "ITHE";
	final public static String NAVI_138					= "EPFS";
	final public static String NAVI_139					= "ESICS";
	final public static String NAVI_140					= "LWFS";
	final public static String NAVI_141					= "GS";
	final public static String NAVI_142					= "OS";
	final public static String NAVI_143					= "DM";
	final public static String NAVI_144					= "ES";
	final public static String NAVI_145					= "SP";
	final public static String NAVI_146					= "EG";
	final public static String NAVI_147					= "WO";
	final public static String NAVI_148					= "PAYS";
	final public static String NAVI_149					= "TBS";
	final public static String NAVI_150					= "IS";
	
	final public static String NAVI_152					= "ID";
	final public static String NAVI_153					= "TS";
	
	final public static String NAVI_155					= "DC";
	final public static String NAVI_156					= "AP";
	final public static String NAVI_157					= "FM";
	final public static String NAVI_158					= "RMBP";
	final public static String NAVI_159					= "PL";
	final public static String NAVI_160					= "PP";
	
	final public static String NAVI_161					= "CD";
	final public static String NAVI_162					= "SUB";
	final public static String NAVI_163					= "PARAM";
	final public static String NAVI_164					= "DOMAN";		//added by parvez date: 21-11-2022
	
	final public static String NAVI_711					= "NAVI_711";
	final public static String NAVI_1111				= "NAVI_1111";
	final public static String NAVI_871					= "NAVI_871";
	final public static String NAVI_1271				= "NAVI_1271";
	final public static String NAVI_712					= "NAVI_712";
	final public static String NAVI_1112				= "NAVI_1112";
	final public static String NAVI_713					= "NAVI_713";
	final public static String NAVI_1113				= "NAVI_1113";
	final public static String NAVI_714					= "NAVI_714";
	final public static String NAVI_1114				= "NAVI_1114";
	final public static String NAVI_715					= "NAVI_715";
	final public static String NAVI_1115				= "NAVI_1115";
	final public static String NAVI_716					= "NAVI_716";
	final public static String NAVI_1116				= "NAVI_1116";
	final public static String NAVI_717					= "NAVI_717";
	final public static String NAVI_1117				= "NAVI_1117";
	final public static String NAVI_718					= "NAVI_718";
	final public static String NAVI_1118				= "NAVI_1118";
	
	final public static String NAVI_720					= "NAVI_720";
	final public static String NAVI_1120				= "NAVI_1120";
	final public static String NAVI_721					= "NAVI_721";
	final public static String NAVI_1121				= "NAVI_1121";
	final public static String NAVI_722					= "NAVI_722";
	final public static String NAVI_1122				= "NAVI_1122";
	
	final public static String NAVI_725					= "NAVI_725";
	final public static String NAVI_1125				= "NAVI_1125";
	final public static String NAVI_726					= "NAVI_726";
	final public static String NAVI_1126				= "NAVI_1126";
	final public static String NAVI_727					= "NAVI_727";
	final public static String NAVI_1127				= "NAVI_1127";
	final public static String NAVI_728					= "NAVI_728";
	final public static String NAVI_1128				= "NAVI_1128";
	final public static String NAVI_729					= "NAVI_729";
	final public static String NAVI_1129				= "NAVI_1129";
	final public static String NAVI_730					= "NAVI_730";
	final public static String NAVI_1130				= "NAVI_1130";
	final public static String NAVI_731					= "NAVI_731";
	final public static String NAVI_1131				= "NAVI_1131";
	final public static String NAVI_732					= "NAVI_732";
	final public static String NAVI_1132				= "NAVI_1132";
	final public static String NAVI_733					= "NAVI_733";
	final public static String NAVI_1133				= "NAVI_1133";
	final public static String NAVI_734					= "NAVI_734";
	final public static String NAVI_1134				= "NAVI_1134";
	final public static String NAVI_735					= "NAVI_735";
	final public static String NAVI_1135				= "NAVI_1135";
	final public static String NAVI_736					= "NAVI_736";
	final public static String NAVI_1136				= "NAVI_1136";
	final public static String NAVI_737					= "NAVI_737";
	final public static String NAVI_1137				= "NAVI_1137";
	final public static String NAVI_738					= "NAVI_738";
	final public static String NAVI_1138				= "NAVI_1138";
	final public static String NAVI_739					= "NAVI_739";
	final public static String NAVI_1139				= "NAVI_1139";
	final public static String NAVI_740					= "NAVI_740";
	final public static String NAVI_1140				= "NAVI_1140";
	final public static String NAVI_741					= "NAVI_741";
	final public static String NAVI_1141				= "NAVI_1141";
	final public static String NAVI_742					= "NAVI_742";
	final public static String NAVI_1142				= "NAVI_1142";
	final public static String NAVI_743					= "NAVI_743";
	final public static String NAVI_1143				= "NAVI_1143";
	final public static String NAVI_744					= "NAVI_744";
	final public static String NAVI_1144				= "NAVI_1144";
	final public static String NAVI_868					= "NAVI_868";
	final public static String NAVI_1268				= "NAVI_1268";
	
	final public static String NAVI_747					= "NAVI_747";
	final public static String NAVI_1147				= "NAVI_1147";
	final public static String NAVI_750					= "NAVI_750";
	final public static String NAVI_1150				= "NAVI_1150";
	final public static String NAVI_751					= "NAVI_751";
	final public static String NAVI_1151				= "NAVI_1151";
	final public static String NAVI_752					= "NAVI_752";
	final public static String NAVI_1152				= "NAVI_1152";
	final public static String NAVI_753					= "NAVI_753";
	final public static String NAVI_1153				= "NAVI_1153";
	
	final public static String NAVI_756					= "NAVI_756";
	final public static String NAVI_1156				= "NAVI_1156";
	final public static String NAVI_757					= "NAVI_757";
	final public static String NAVI_1157				= "NAVI_1157";
	final public static String NAVI_758					= "NAVI_758";
	final public static String NAVI_1158				= "NAVI_1158";
	final public static String NAVI_759					= "NAVI_759";
	final public static String NAVI_1159				= "NAVI_1159";
	final public static String NAVI_760					= "NAVI_760";
	final public static String NAVI_1160				= "NAVI_1160";
	
	final public static String NAVI_762					= "NAVI_762";
	final public static String NAVI_1162				= "NAVI_1162";
	
	final public static String NAVI_764					= "NAVI_764";
	final public static String NAVI_1164				= "NAVI_1164";
	final public static String NAVI_765					= "NAVI_765";
	final public static String NAVI_1165				= "NAVI_1165";
	final public static String NAVI_766					= "NAVI_766";
	final public static String NAVI_1166				= "NAVI_1166";
	final public static String NAVI_767					= "NAVI_767";
	final public static String NAVI_1167				= "NAVI_1167";
	final public static String NAVI_768					= "NAVI_768";
	final public static String NAVI_1168				= "NAVI_1168";
	final public static String NAVI_769					= "NAVI_769";
	final public static String NAVI_1169				= "NAVI_1169";
	final public static String NAVI_770					= "NAVI_770";
	final public static String NAVI_1170				= "NAVI_1170";
	final public static String NAVI_771					= "NAVI_771";
	final public static String NAVI_1171				= "NAVI_1171";
	final public static String NAVI_772					= "NAVI_772";
	final public static String NAVI_1172				= "NAVI_1172";
	final public static String NAVI_773					= "NAVI_773";
	final public static String NAVI_1173				= "NAVI_1173";
	final public static String NAVI_774					= "NAVI_774";
	final public static String NAVI_1174				= "NAVI_1174";
	final public static String NAVI_775					= "NAVI_775";
	final public static String NAVI_1175				= "NAVI_1175";
	final public static String NAVI_776					= "NAVI_776";
	final public static String NAVI_1176				= "NAVI_1176";
	final public static String NAVI_777					= "NAVI_777";
	final public static String NAVI_1177				= "NAVI_1177";
	final public static String NAVI_778					= "NAVI_778";
	final public static String NAVI_1178				= "NAVI_1178";
	final public static String NAVI_779					= "NAVI_779";
	final public static String NAVI_1179				= "NAVI_1179";
	final public static String NAVI_780					= "NAVI_780";
	final public static String NAVI_1180				= "NAVI_1180";
	final public static String NAVI_781					= "NAVI_781";
	final public static String NAVI_1181				= "NAVI_1181";
//	final public static String NAVI_820					= "NAVI_820";
//	final public static String NAVI_1221				= "NAVI_1221";
//	final public static String NAVI_889					= "NAVI_889";
//	final public static String NAVI_1290				= "NAVI_1290";
	
	/**
	 * Navigation Label Code End
	 **/
	
	final public static int ESI_PERIOD_1_START = 4;
	final public static int ESI_PERIOD_1_END = 9;
	final public static int ESI_PERIOD_2_START = 10;
	final public static int ESI_PERIOD_2_END = 3;
	
	
	/**
	 * Section Id of IT Declaration
	 * */
	final public static int SECTION_80D 							= 1; //Medical Insurance Premium and Health Checkup
	final public static int SECTION_80CCF			 				= 2; //Long Term Infra Bond
	final public static int SECTION_80C_AND_80CCC 					= 3; //Life Insurance Premium, Provident Fund, National Saving Certificates, ELSS, Tuition Fees, Mutual Fund, Repayment of Principal of Housing Loan, Bank Fixed Deposit of 5 years period, Bonds of NABARD, 5 years Term deposit
	final public static int SECTION_80DD		 					= 4; //Any Expenditure incurred on Disabled Dependant on Medical, Nursing & Rehab
	final public static int SECTION_80DDB_G					 		= 5; //Actual expenditure incurred on medical treatment of self or dependant (General)
	final public static int SECTION_80DDB_SC				 		= 6; //Actual expenditure incurred on medical treatment of self or dependant (Senior Citizen)
	final public static int SECTION_80E						 		= 7; //Educational Loan Self/Dependant
	final public static int SECTION_80G_GN					 		= 8; //Donations Govt. Notified
	final public static int SECTION_80G_O							= 9; //Donations Others
	final public static int SECTION_80CCD							= 10; //NPS
	final public static int SECTION_HOME_LOAN_INTEREST				= 11; //Home Loan Interest
	final public static int SECTION_INCOME_FROM_OTHER_SOURCE		= 13; //Income from Other Sources
	final public static int SECTION_INCOME_FROM_HOUSE_PROPERTY		= 14; //Income from House Property
	final public static int SECTION_LONG_TERM_CAPITAL_GAIN			= 15; //Long term capital gain
	final public static int SECTION_111A							= 16; //111A (STCG from shares and MF)
	final public static int SECTION_STCG							= 17; //STCG (other than 111A)
	
	final public static int SECTION_80EE							= 21; //Interest of Housing Loan
	final public static int SECTION_80EEA							= 22; //Interest of Housing Loan
	final public static int SECTION_80EEB							= 23; //Interest of Electric vehicle loan
	final public static int SECTION_80GG							= 24; //Rent paid if HRA is not a salary part
	final public static int SECTION_80GGA							= 25; // Donation for scientific research etc.
	final public static int SECTION_80RRB							= 26; // Royalty on patents
	final public static int SECTION_80QQB							= 27; // Royalty income of authors
	final public static int SECTION_80U								= 28; //Income of person with disability/ Self disability (In case of severe disablility, deduction upto 1.25 lakhs applicable)
	final public static int SECTION_80TTA							= 29; //Interest income earned on Savings Bank Account
	final public static int SECTION_80CCG							= 30; //80CCG
	

	/**
	 * Instance Name for Feature Management
	 * */

	final public static String INTELIMENT				= "INTELIMENT";
	final public static String QULOI					= "QULOI";
	
	final public static int SANDWITCH_TYPE_NONE = 0;//Start Dattatray Date: 20-09-21
	
	//Start Dattatray Date: 22-10-21
	final public static int JANUARY							= 1;
	final public static int MARCH							= 3;
	final public static int APRIL							= 4;
	final public static int DECEMBER						= 12;
	//Start Dattatray Date: 22-10-21
	
	//===start parvez date: 10-02-2022===
	final public static String OUT_OF_POCKET_EXPENSES	 		= "Out of Pocket Expenses";
	final public static String PROFESSIONAL_FEES	 		= "Professional Fees";
	//===end parvez date: 10-02-2022===
	
}