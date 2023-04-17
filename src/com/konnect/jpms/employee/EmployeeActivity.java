package com.konnect.jpms.employee;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import bsh.ParseException;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.document.HeaderFooterPageEvent;
import com.konnect.jpms.leave.AssignLeaveCron;
import com.konnect.jpms.payroll.CalculateAndGetArrearAmount;
import com.konnect.jpms.reports.EmployeeLeaveEntryReport;
import com.konnect.jpms.reports.MyProfile;
import com.konnect.jpms.salary.EmpSalaryApproval;
import com.konnect.jpms.select.FillActivity;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillDocument;
import com.konnect.jpms.select.FillEmpStatus;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class EmployeeActivity extends ActionSupport implements ServletRequestAware, IStatements, ServletResponseAware {

	private static final long serialVersionUID = 1L;

	public HttpSession session;
	public CommonFunctions CF;
	String strUserType;
	String strSessionEmpId;
	String strSessionOrgId;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strWLocationAccess;
	String strBaseUserTypeId = null;

	private static Logger log = Logger.getLogger(EmployeeActivity.class);

	private String[] salary_head_id;
	private String[] salary_head_value;
	private String[] isDisplay;
	private String[] hideIsDisplay;
	private String[] emp_salary_id;
	
	private List<List<String>> al = new ArrayList<List<String>>();
	
	private String f_strWLocation;
	private String f_department;
	private String f_level;
	
	private String strEmpId;
	private String strEmpId2;
	private String effectiveDate;
	private String strActivity;
	private String strDocumentName;
	private String strOrganisation;
	
	private String empType;
	private String strWLocation;
	private String strSBU;
	private String strDepartment;
	private String strLevel;
	private String strDesignation;
	private String strDesignationUpdate;
	private String strGrade;
	private String empGrade;
	private String strNoticePeriod;
	private String strProbationPeriod;
	private String strExtendProbationDays;
	private String strIncrementType;
	private String strIncrementPercentage;
	
	private String strNewStatus;
	private String strReason;
	private String strJoiningDate;
	private boolean emailNotification;
	private String strUpdate;
	private String f_org;
	private String appraisal_id;
	private String empChangeGrade;
	
	private List<FillOrganisation> organisationList;
	private List<FillOrganisation> organisationList1;
	private List<FillEmploymentType> empTypeList;
	private List<FillActivity> activityList;
	private List<FillDocument> documentList;
	private List<FillWLocation> wLocationList;
	private List<FillWLocation> wLocationList1;
	private List<FillDepartment> departmentList;
	private List<FillDepartment> departmentList1;
//	List<FillServices> serviceList;
	private List<FillServices> serviceList1;
	private List<FillLevel> levelList;
	private List<FillLevel> levelList1;
	private List<FillDesig> desigList;
	private List<FillGrade> gradeList;
	private List<FillGrade> gradeChangeList;
	private List<FillEmpStatus> empStatusList;
	private List<FillEmployee> empList;
	private String dataType;
	private String strUpdateDocument;
	private String strTransferType;
	
	private String learningId;
	private String trainingId;
	private String assessmentId;
	
	private String employmentType;
	private String proPage;
	private String minLimit;
	
	private String alertStatus;
	private String alert_type;
	
	private String fromPage;
	private String appFreqId;
	private String alertID;
	
	private boolean disableSalaryStructure;
	
	private String strApplyArrear;
	private String strIncumbentEmpId;
	private String strReviewIds;
	private String strLearningIds;
	private String strAction=null;
	
//===start parvez date: 29-06-2022===	
	private File strDocumentFile;
	private String strDocumentFileFileName;
//===end parvez date: 29-06-2022===	
	
	public String execute() throws Exception, ParseException {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		
		request.setAttribute(PAGE, PAddEmployeeActivity);
		request.setAttribute(TITLE, TEmployeeActivity);
		 
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strWLocationAccess = (String)session.getAttribute(WLOCATION_ACCESS);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		
		UtilityFunctions uF = new UtilityFunctions();
//		EncryptionUtility eU = new EncryptionUtility();
//		System.out.println("uF.parseToInt(getStrEmpId()) ===>> " +uF.parseToInt(getStrEmpId()));
//		if(getStrEmpId() != null && uF.parseToInt(getStrEmpId()) == 0) {
//			String decodeEmpId = eU.decode(getStrEmpId());
//			setStrEmpId(decodeEmpId);
//		}
//		if(getStrActivity() != null && uF.parseToInt(getStrActivity()) == 0) {
//			String decodeStrActivity = eU.decode(getStrActivity());
//			setStrActivity(decodeStrActivity);
//		}
		
//		System.out.println("getStrEmpId ===>> " + getStrEmpId());
//		System.out.println("getStrEmpId2 ===>> " + getStrEmpId2());
		//Created By Dattatray 10-6-2022
		strAction = request.getServletPath();
		if(strAction!=null) {
			strAction = strAction.replace("/","");
		}
		loadPageVisitAuditTrail(CF, uF);
		
		List<String> accessEmpList = CF.viewEmployeeIdsList(request, uF, strBaseUserType, strSessionEmpId, strWLocationAccess);
		if((strBaseUserType != null && strUserType != null && (strBaseUserType.equals(EMPLOYEE) || strUserType.equals(EMPLOYEE))) || (!accessEmpList.contains(getStrEmpId()) && uF.parseToInt(getStrEmpId()) > 0)) {
			if(uF.parseToInt(getStrEmpId2()) == 0) {
				setStrEmpId(strSessionEmpId);
			}
		}
//		System.out.println("getStrEmpId after ===>> " + getStrEmpId());
		
		if(getFromPage() == null || getFromPage().trim().equals("") || getFromPage().equalsIgnoreCase("NULL")) {
			boolean isView  = CF.getAccess(session, request, uF);
			if(!isView) {
				request.setAttribute(PAGE, PAccessDenied);
				request.setAttribute(TITLE, TAccessDenied);
				return ACCESS_DENIED;
			}
		}
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-group\"></i><a href=\"People.action\" style=\"color: #3c8dbc;\"> People</a></li>" +
			"<li class=\"active\">Employee Activity</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		
		if(getFromPage() == null || getFromPage().trim().equals("") || getFromPage().equalsIgnoreCase("NULL")) { 
			/*if(uF.parseToInt(getF_org())==0) {
				setF_org(strSessionOrgId);
			}*/		
		
			loadValidateEmpActivity(uF);
		
			if(getAlertStatus()!=null && !getAlertStatus().equals("") && getAlert_type()!=null && !getAlert_type().equals("")) {
				if(getEmpType()!=null && !getEmpType().equals("")) {
					updateUserAlerts();
				}
			}
		
			if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")) {
				setDataType("A");    
			}
		
			if(uF.parseToInt(getProPage()) == 0) {
				setProPage("1");
			}
		
			if(getEmpType() == null || getEmpType().equals("") ) {
				setEmpType("I");
			}
		
			getInductionData(uF);
			getConfirmationData(uF);
			viewResignationReport(uF);
			getRetirementData(uF);
		}
		
		request.setAttribute("salaryStructure", CF.getStrSalaryStructure());
		request.setAttribute("hmFeatureStatus", CF.getFeatureStatusMap(request));
	    
		if(getStrEmpId()!=null) {
			if(uF.parseToInt(getStrEmpId()) > 0) {
				int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
				if(nSalaryStrucuterType == S_GRADE_WISE) {
					viewUpdateEmployeeSalaryDetailsByGrade(uF);
					
					viewProfile(getStrEmpId());
					viewEmpActivity();
					getLeaveDetails(uF);
				} else {
					viewUpdateEmployeeSalaryDetails();
					
					viewProfile(getStrEmpId());
					viewEmpActivity();
					getLeaveDetails(uF);
				}
			}
		} else if(getStrEmpId()==null && getStrEmpId2()!=null) {
			insertEmpActivity();
			setEmpType("I");
			return VIEW;
		}else {
			if(getEmpType() == null) {
				setEmpType("I");
			}
		}
		loadPageVisitAuditTrail(CF, uF);
		return SUCCESS;
	}

	//Created By Dattatray 10-06-2022
	private void loadPageVisitAuditTrail(CommonFunctions CF,UtilityFunctions uF) {
		Connection con=null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			StringBuilder builder = new StringBuilder();
			builder.append("Filter:");
			builder.append("\nOrganization:"+getF_org());
			builder.append("\nLocation:"+getF_strWLocation());	
			builder.append("\nDepartment:"+getF_department());	
			builder.append("\nLevel:"+getF_level());	
			builder.append("\nEmployee Id:"+getStrEmpId());	
			if(getDataType() == null || getDataType().equals("A") ) {
				
				if(getStrActivity() !=null) {
					builder.append("\n\nNew Activity Information");	
					builder.append("\nActivity : "+getStrActivity() !=null ? getStrActivity():"No any activity select");	
					if(getEffectiveDate() !=null) {
						builder.append("\nEffective date : "+getEffectiveDate());	
					}
					if(getStrNoticePeriod() !=null && uF.parseToInt(getStrNoticePeriod()) > 0) {
						builder.append("\nNotice Period : "+getStrNoticePeriod());
					}
					if(getStrTransferType() !=null) {
						if(getStrWLocation() !=null) {
							builder.append("\nLocation : "+getStrWLocation());
						}
						
						if(getStrDepartment() !=null) {
							builder.append("\nDepartment : "+getStrDepartment());
						}
						if(getEmploymentType() !=null) {
							builder.append("\nEmployeement Type : "+getEmploymentType());
							builder.append("\nSBU : "+getStrSBU());
						}
					}
					if(getStrReason() !=null) {
						builder.append("\nReason : "+getStrReason());
					}
					if(getStrUpdate() == null && getStrUpdateDocument()!=null && !getStrUpdateDocument().trim().equals("") && !getStrUpdateDocument().trim().equalsIgnoreCase("NULL")) {
						builder.append("\nNew Activity Information updated ");
					}
				}
				
				
			} else if(getDataType() == null || getDataType().equals("D")) {
				builder.append("\n\nNew Activity W/Doc Information");	
				
				
			}
			if(getEmpType() !=null && getEmpType().trim().equalsIgnoreCase("I")) {
				builder.append("\nIndduction Tab");
				if(uF.parseToInt(getStrActivity())==7 ) {
					builder.append("\nTake action clicked from confirmation tab" );
				}else if(uF.parseToInt(getStrActivity())==38) {
					builder.append("\nTake action clicked from Retirement tab");
				}
			}
			
			CF.pageVisitAuditTrail(con,CF,uF, strSessionEmpId, strAction, strBaseUserType, builder.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}
	
	private void updateUserAlerts() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
		try {
			con = db.makeConnection(con);
			String strDomain = request.getServerName().split("\\.")[0];
			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
			userAlerts.setStrDomain(strDomain);
			userAlerts.setStrEmpId(""+nEmpId);
			if(getEmpType()!=null && getEmpType().equalsIgnoreCase("I")) {
				userAlerts.set_type(NEW_JOINEES_ALERT);
			}else if(getEmpType()!=null && getEmpType().equalsIgnoreCase("C")) {
				userAlerts.set_type(EMP_CONFIRMATIONS_ALERT);
			}else if(getEmpType()!=null && getEmpType().equalsIgnoreCase("R")) {
				userAlerts.set_type(EMP_RESIGNATIONS_ALERT);
			}else if(getEmpType()!=null && getEmpType().equalsIgnoreCase("FD")) {
				userAlerts.set_type(EMP_FINAL_DAY_ALERT);
			}
			userAlerts.setStatus(UPDATE_ALERT);
			Thread t = new Thread(userAlerts);
			t.run();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void getRetirementData(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
	//	EncryptionUtility eU = new EncryptionUtility();
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpWLocation = CF.getWLocationMap(con,null,request,null); 
			Map<String, String> hmEmpDepartment = CF.getDepartmentMap(con,null,null); 
			Map<String, String> hmEmpDesignation = CF.getEmpDesigMap(con); 
			
			Map<String, String> hmOrgRetirementAge = new HashMap<String, String>();
			pst = con.prepareStatement("select * from org_details");
			rst = pst.executeQuery();
			while(rst.next()) {
				hmOrgRetirementAge.put(rst.getString("org_id"), rst.getString("retirement_age"));
			}
			rst.close();
			pst.close();
			
//			Map<String,List<String>> upcomingRetirements = new LinkedHashMap<String, List<String>>();
			Map<String,List<String>> todaysRetirements = new LinkedHashMap<String, List<String>>();
			Map<String,List<String>> tomorrowRetirements = new LinkedHashMap<String, List<String>>();
			Map<String,List<String>> dayAfterTomorrowRetirements = new LinkedHashMap<String, List<String>>();
			Map<String,List<String>> pendingRetirements = new LinkedHashMap<String, List<String>>();
			
			java.util.Date currDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),DBDATE, DATE_FORMAT),DATE_FORMAT );
			java.util.Date tomorrowDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 1),DBDATE, DATE_FORMAT),DATE_FORMAT );
			java.util.Date dayAfterTomorrowDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 2),DBDATE, DATE_FORMAT),DATE_FORMAT );
			
			StringBuilder sbQuery = new StringBuilder();
			
			sbQuery.append("select * from employee_personal_details epd,employee_official_details eod where epd.emp_per_id = eod.emp_id " +
					"and approved_flag=true and is_alive=true and emp_filled_flag=true and joining_date is not null ");
			
			if(getStrWLocation()!=null && !getStrWLocation().equals("")) {
	            sbQuery.append(" and wlocation_id in ("+getStrWLocation()+") ");
	         } else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	} else {
	        		sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	}
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	} else {
	        		sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	}
			}
	        sbQuery.append(" order by epd.emp_per_id desc");
			pst = con.prepareStatement(sbQuery.toString());
		//	System.out.println("employee activity confirmation pst==>"+pst);
			rst = pst.executeQuery();
			while(rst.next()) {
			
			//String strEmpAge = uF.getTimeDurationBetweenDatesWithYearMonthDays(rst.getString("emp_date_of_birth"), DBDATE, ""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF, uF, request);
			if(rst.getString("emp_date_of_birth")!=null){	
			String strEmpAge = uF.getTimeDurationBetweenDatesWithYearMonthDays(rst.getString("emp_date_of_birth"), DBDATE, ""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF, uF, request);
				
		//	System.out.println("strEmpAge==>"+strEmpAge);
			
				if(strEmpAge != null) {
					String[] strTmpEmpAge = strEmpAge.split("::::");
					String strRetireAge = hmOrgRetirementAge.get(rst.getString("org_id"));
					if(uF.parseToInt(strTmpEmpAge[0]) >= uF.parseToInt(strRetireAge) || (uF.parseToInt(strTmpEmpAge[0]) == (uF.parseToInt(strRetireAge)-1) && uF.parseToInt(strTmpEmpAge[1]) == 11)) {
						List<String> alInner = new ArrayList<String>();
						alInner.add((String)rst.getString("emp_per_id"));//0
						
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rst.getString("emp_mname");
							}
						}
						
						alInner.add((String)rst.getString("emp_fname")+strEmpMName+" "+(String)rst.getString("emp_lname"));//1
						alInner.add((String)rst.getString("emp_status"));//2
						alInner.add(uF.getDateFormat(rst.getString("joining_date"), DBDATE, DATE_FORMAT));//3
						alInner.add(hmEmpName.get(rst.getString("added_by")));//4
						alInner.add(rst.getString("emp_image"));//5
						alInner.add(hmEmpWLocation.get(rst.getString("wlocation_id")));//6
						alInner.add(hmEmpDepartment.get(rst.getString("depart_id")));//7
						alInner.add(hmEmpDesignation.get(rst.getString("emp_per_id")));//8
						if(rst.getString("supervisor_emp_id")!=null && uF.parseToInt(rst.getString("supervisor_emp_id"))>0) {
							alInner.add(hmEmpName.get(rst.getString("supervisor_emp_id")));//9
						} else {
							alInner.add("-");//9
						}
						
						//check probation date
						if(rst.getString("joining_date")!=null && !rst.getString("joining_date").equals("")) {
						
						//String empDOBDate = uF.getDateFormat(rst.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT);
						String empDOBDate = rst.getString("emp_date_of_birth");
						//System.out.println("empDOBDate==>"+empDOBDate);	
							
							if(empDOBDate!=null && !empDOBDate.equals("")){
					    //	System.out.println("empDOBDate==>"+empDOBDate);
					    	
							 empDOBDate = uF.getDateFormat(rst.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT);
						   // 	System.out.println("empDOBDate==>"+empDOBDate);

							String strDay = uF.getDateFormat(empDOBDate, DATE_FORMAT, "dd");
							String strMonth = uF.getDateFormat(empDOBDate, DATE_FORMAT, "MM");
							String strYear = uF.getDateFormat(empDOBDate, DATE_FORMAT, "yyyy");
							int intRetirementYr = uF.parseToInt(strYear)+uF.parseToInt(strRetireAge);
						
							String retirementDate = strDay+"/"+strMonth+"/"+intRetirementYr;
							
							java.util.Date retireDate = uF.getDateFormatUtil(retirementDate, DATE_FORMAT);
							
							alInner.add(retirementDate);//10
							
			//				String encodeEmpId = eU.encode(rst.getString("emp_per_id"));
							String encodeEmpId = rst.getString("emp_per_id");
							alInner.add("<a href=\"EmployeeActivity.action?dataType=A&strActivity="+ACTIVITY_RETIREMENT_ID+"&strEmpId="+encodeEmpId+"\">Take Action</a>");//11
							if(retireDate.equals(currDate)) {
								todaysRetirements.put((String)rst.getString("emp_per_id"),alInner);
							} else if(retireDate.equals(tomorrowDate)) {
								tomorrowRetirements.put((String)rst.getString("emp_per_id"),alInner);
							} else if(retireDate.equals(dayAfterTomorrowDate) || retireDate.after(dayAfterTomorrowDate)) {
								dayAfterTomorrowRetirements.put((String)rst.getString("emp_per_id"),alInner);
							} else if(retireDate.before(currDate)) {
								pendingRetirements.put((String)rst.getString("emp_per_id"),alInner);
							}
						 }
						} else {
							alInner.add("N/A");//10
							alInner.add("N/A");//11
						}
					}
				}
			 }
			}
			rst.close();
			pst.close();
			
			int totalRetirements = todaysRetirements.size() + tomorrowRetirements.size() +dayAfterTomorrowRetirements.size() + pendingRetirements.size();
	//		System.out.println("totalConfirmation activity==>"+totalConfirmation);	
			
			if(totalRetirements == 0) {
				request.setAttribute("totalRetirements", "");
			} else {
				request.setAttribute("totalRetirements", ""+totalRetirements);
			}
			
			request.setAttribute("todaysRetirements", todaysRetirements);
			request.setAttribute("tomorrowRetirements", tomorrowRetirements);
			request.setAttribute("dayAfterTomorrowRetirements", dayAfterTomorrowRetirements);
			request.setAttribute("pendingRetirements", pendingRetirements);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}	
	}


	
	

	private void viewResignationReport(UtilityFunctions uF) {
		List<List<String>> al = new ArrayList<List<String>>();	
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
//		EncryptionUtility eU = new EncryptionUtility();
		
		try {

			con = db.makeConnection(con);
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
			String locationID=hmEmpWlocationMap.get(strSessionEmpId);
			
			pst = con.prepareStatement("select emp_id from emp_offboard_status where status=false group by emp_id");
			rs=pst.executeQuery();
			Map<String,String> statuaMp=new HashMap<String,String>();
			while(rs.next()) {
				statuaMp.put(rs.getString("emp_id"), rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("statuaMp",statuaMp);
			//Map<String,String> statuaMp = (Map<String,String>)request.getAttribute("statuaMp");
			Map<String, String> hmEmpWLocation = CF.getWLocationMap(con,null,request,null); 
			Map<String, String> hmEmpDepartment = CF.getDepartmentMap(con,null,null); 
			Map<String, String> hmEmpDesignation = CF.getEmpDesigMap(con); 
			Map<String,List<String>> todaysResignation = new LinkedHashMap<String, List<String>>();
			Map<String,List<String>> pendingResignation = new LinkedHashMap<String, List<String>>();
			
			Map<String,List<String>> todaysFD = new LinkedHashMap<String, List<String>>();
			Map<String,List<String>> tomorrowFD = new LinkedHashMap<String, List<String>>();
			Map<String,List<String>> dayAfterTomorrowFD = new LinkedHashMap<String, List<String>>();
			Map<String,List<String>> pendingFD = new LinkedHashMap<String, List<String>>();
			Map<String, String> hmEmpProbation = new LinkedHashMap<String, String>();
			
			java.util.Date currDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),DBDATE, DATE_FORMAT),DATE_FORMAT );
			java.util.Date tomorrowDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 1),DBDATE, DATE_FORMAT),DATE_FORMAT );
			java.util.Date dayAfterTomorrowDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 2),DBDATE, DATE_FORMAT),DATE_FORMAT );
			//1		
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where "
					+" ((is_approved=0 and effective_type='"+WORK_FLOW_RESIGN+"') or (is_approved=1 and effective_type='"+WORK_FLOW_TERMINATION+"'))" 
					+ " and effective_id in(select off_board_id from emp_off_board where emp_id in (select eod.emp_id from "
					+" employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id ");
			
			if(getStrWLocation()!=null && !getStrWLocation().equals("")) {
				 sbQuery.append(" and wlocation_id in ("+getStrWLocation()+") ");
	         }else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
			sbQuery.append(")) group by effective_id");
//			System.out.println("EA/566--pst="+pst);
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			//2
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,user_type_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
					" and ((is_approved=0 and effective_type='"+WORK_FLOW_RESIGN+"') or (is_approved=1 and effective_type='"+WORK_FLOW_TERMINATION+"')) " 
					+" and effective_id in(select off_board_id from emp_off_board where emp_id in (select eod.emp_id from employee_personal_details epd, "
					+ " employee_official_details eod where epd.emp_per_id = eod.emp_id ");
					
			if(getStrWLocation()!=null && !getStrWLocation().equals("")) {
				 sbQuery.append(" and wlocation_id in ("+getStrWLocation()+") ");
	         }else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
			sbQuery.append("))");
			if(strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HOD))) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append("group by effective_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(strUserTypeId));	
			if(strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HOD))) {
				pst.setInt(3, uF.parseToInt(strBaseUserTypeId));
			}
			
//			System.out.println("EA/616--pst="+pst);
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmMemNextApproval.put(rs.getString("effective_id")+"_"+rs.getString("user_type_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			//3
			sbQuery=new StringBuilder();
			sbQuery.append("select off_board_id from emp_off_board where " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
						
			if(getStrWLocation()!=null && !getStrWLocation().equals("")) {
				 sbQuery.append(" and wlocation_id in ("+getStrWLocation()+") ");
	         }else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
			sbQuery.append(") and approved_1=-1 and approved_2=-1 ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("EA/652--pst3==>"+pst);
			rs = pst.executeQuery();	
			List<String> deniedList=new ArrayList<String>();
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("off_board_id")) ) {
					deniedList.add(rs.getString("off_board_id"));
				}
			}
			rs.close();
			pst.close();
			
			
			//4
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_RESIGN+"' " +
					"and effective_id in(select off_board_id from emp_off_board where " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			
			if(getStrWLocation()!=null && !getStrWLocation().equals("")) {
				 sbQuery.append(" and wlocation_id in ("+getStrWLocation()+") ");
	         }else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
			sbQuery.append(")) group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("EA/692--pst4==>"+pst);
			rs = pst.executeQuery();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("effective_id"))) {
					deniedList.add(rs.getString("effective_id"));
				}
			}
			rs.close();
			pst.close();
			
			//5
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
					" and (effective_type='"+WORK_FLOW_RESIGN+"' or effective_type='"+WORK_FLOW_TERMINATION+"') and effective_id in(select off_board_id from emp_off_board where " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
						
			if(getStrWLocation()!=null && !getStrWLocation().equals("")) {
				 sbQuery.append(" and wlocation_id in ("+getStrWLocation()+") ");
	         }else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
			sbQuery.append("))  group by effective_id,is_approved");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("EA/730--pst5==>"+pst);
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproval = new HashMap<String, String>();			
			while(rs.next()) {
				hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
			}
			rs.close();
			pst.close();
			
			//6
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
					" and (effective_type='"+WORK_FLOW_RESIGN+"' or effective_type='"+WORK_FLOW_TERMINATION+"') and effective_id in(select off_board_id from emp_off_board where " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
				
			if(getStrWLocation()!=null && !getStrWLocation().equals("")) {
				 sbQuery.append(" and wlocation_id in ("+getStrWLocation()+") ");
	         }else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
			sbQuery.append(")) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("EA/767--pst6==>"+pst);
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();	
			Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();
			while(rs.next()) {
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			//7
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
				" and (effective_type='"+WORK_FLOW_RESIGN+"' or effective_type='"+WORK_FLOW_TERMINATION+"') and effective_id in(select off_board_id from emp_off_board where " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
					
			if(getStrWLocation()!=null && !getStrWLocation().equals("")) {
				 sbQuery.append(" and wlocation_id in ("+getStrWLocation()+") ");
	         }else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
			
			sbQuery.append(")) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("EA/807--pst7==>"+pst);
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();	
			while(rs.next()) {
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			//8
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id from work_flow_details where (effective_type='"+WORK_FLOW_RESIGN+"' or effective_type='"+WORK_FLOW_TERMINATION+"')" +
					" and effective_id in(select off_board_id from emp_off_board where emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " 
					+" where epd.emp_per_id = eod.emp_id ");
			
			if(getStrWLocation()!=null && !getStrWLocation().equals("")) {
				 sbQuery.append(" and wlocation_id in ("+getStrWLocation()+") ");
	         }else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
			
			sbQuery.append(")) ");
			if(strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HOD))) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append(" order by effective_id,member_position");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strUserTypeId));
			if(strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HOD))) {
				pst.setInt(2, uF.parseToInt(strBaseUserTypeId));
			}
//			System.out.println("EA/855--pst8==>"+pst);
			rs = pst.executeQuery();			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	
			while(rs.next()) {
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
			}
			rs.close();
			pst.close();
			

			//9
			sbQuery=new StringBuilder();
			sbQuery.append("select e.*,wfd.user_type_id as user_type from (select * from emp_off_board eob, employee_official_details eod, employee_personal_details epd where eod.emp_id = eob.emp_id " +
					" and epd.emp_per_id = eob.emp_id and eod.emp_id = epd.emp_per_id and epd.emp_per_id in (select emp_id from user_details where status != 'INACTIVE') ");
			if(getStrWLocation()!=null && !getStrWLocation().equals("")) {
				 sbQuery.append(" and eod.wlocation_id in ("+getStrWLocation()+") ");
	         }else if(strUserType!=null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(RECRUITER))) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(RECRUITER))) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
	        
			sbQuery.append(") e, work_flow_details wfd where e.off_board_id = wfd.effective_id and (wfd.effective_type = '"+WORK_FLOW_RESIGN+"' or wfd.effective_type = '"+WORK_FLOW_TERMINATION+"') ");
			if(strUserType != null && !strUserType.equals(ADMIN) && !strUserType.equalsIgnoreCase(RECRUITER)) {
				sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if (strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(CEO) || strBaseUserType.equalsIgnoreCase(HOD))) {
					sbQuery.append(" and (wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ) ");
				} else {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
			}
			sbQuery.append(" order by e.entry_date desc");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			System.out.println("EA/991--pst ===>> " + pst);
			List<String> alList = new ArrayList<String>();	
			while(rs.next()) {
			
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("off_board_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();

				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(RECRUITER)) {
					continue;
				}
				
				String userType = rs.getString("user_type");				
				if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(RECRUITER)) && alList.contains(rs.getString("off_board_id"))) {
					continue;
				} else if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(RECRUITER)) && !alList.contains(rs.getString("off_board_id"))) {
					userType = strUserTypeId;
				
					alList.add(rs.getString("off_board_id"));
				}
				
//				System.out.println("EA/925--off_board_id="+rs.getString("off_board_id"));
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("off_board_id"));//0
				alInner.add(hmEmpNames.get(rs.getString("emp_id")));//1
				alInner.add(uF.showData(rs.getString("off_board_type"),""));//2
				alInner.add(uF.getDateFormat(rs.getString("entry_date"),DBTIMESTAMP, CF.getStrReportDateFormat()));//3
				
				alInner.add(uF.showData(rs.getString("emp_reason"),""));//4
				
				alInner.add(uF.parseToInt(rs.getString("notice_days"))+"");//5
				alInner.add(uF.getDateFormat(rs.getString("last_day_date"), DBDATE, CF.getStrReportDateFormat()));//6
				
				if(deniedList.contains(rs.getString("off_board_id"))) {
					/*alInner.add("<img title=\"Denied\" src=\"images1/icons/denied.png\" width=\"16px\" height=\"16px\" border=\"0\" />");*///7
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
					
				} else if(rs.getInt("approved_1")==1) {							
					 /*alInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" width=\"16px\" height=\"16px\" border=\"0\" />");*///7
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
					
				} else if(uF.parseToInt(hmAnyOneApproval.get(rs.getString("off_board_id")))==1 && uF.parseToInt(hmAnyOneApproval.get(rs.getString("off_board_id")))==rs.getInt("approved_1")) {
					alInner.add("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i>");//7
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("off_board_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("off_board_id")+"_"+userType)) && uF.parseToInt(hmNextApproval.get(rs.getString("off_board_id")))>0) {
					alInner.add("<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, you want to approve this request?'))updateApproveDenyStatus('1','1','"+rs.getString("off_board_id")+"','"+userType+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a> " +
							" <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, you want to deny this request?'))updateApproveDenyStatus('-1','1','"+rs.getString("off_board_id")+"','"+userType+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a> ");//7
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("off_board_id")))<uF.parseToInt(hmMemNextApproval.get(rs.getString("off_board_id")+"_"+userType)) || (uF.parseToInt(hmNextApproval.get(rs.getString("off_board_id")))==0 && uF.parseToInt(hmNextApproval.get(rs.getString("off_board_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("off_board_id")+"_"+userType)))) {
					if(rs.getInt("approved_1")==0) {
						if(strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(RECRUITER)) {
							alInner.add("<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, you want to approve this request?'))updateApproveDenyStatus('1','1','"+rs.getString("off_board_id")+"','"+userType+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a> " +
									" <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, you want to deny this request?'))updateApproveDenyStatus('-1','1','"+rs.getString("off_board_id")+"','"+userType+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a> ");//7
						} else {
							/*alInner.add("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*///7
							alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"  title=\"Waiting for workflow\"></i>");
							
						}
					}else if(rs.getInt("approved_1")==1) {							
						/*alInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" width=\"16px\" height=\"16px\" border=\"0\" />");*///7
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
						
						
					} else {
						/*alInner.add("<img title=\"Denied\" src=\"images1/icons/denied.png\" width=\"16px\" height=\"16px\" border=\"0\" />");*///7
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");//7
					}
				} else {
					if(strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(RECRUITER)) {
						alInner.add("<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, you want to approve this request?'))updateApproveDenyStatus('1','1','"+rs.getString("off_board_id")+"','"+userType+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a> " +
							" <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, you want to deny this request?'))updateApproveDenyStatus('-1','1','"+rs.getString("off_board_id")+"','"+userType+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a> ");//7
					} else {
						/*alInner.add("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" width=\"16px\" height=\"16px\" />");*///7
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Waiting for workflow\"></i>");
					}
				}
				
				String status = "R";
				if(rs.getString("emp_status") != null && rs.getString("emp_status").equalsIgnoreCase(TERMINATED)) {
					status = "T";
				}
				if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("off_board_id"))!=null) {

					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("off_board_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"','"+status+"');\" style=\"margin-left: 10px;\">View</a>");//8
				} else if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("off_board_id"))!=null) {
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("off_board_id")+"','"+hmEmpNames.get(rs.getString("emp_id"))+"','"+status+"');\" style=\"margin-left: 10px;\">View</a>");//8
				} else{
					alInner.add("");//8
				}
								
				if(rs.getInt("approved_1")==1 && rs.getInt("approved_2")==1) {
//					String encodeEmpId = eU.encode(rs.getString("emp_id"));
//					String encodeOffBoardId = eU.encode(rs.getString("off_board_id"));
					String encodeEmpId = rs.getString("emp_id");
					String encodeOffBoardId = rs.getString("off_board_id");
					if(statuaMp!=null && statuaMp.get(rs.getString("emp_id"))!=null ) {
						alInner.add("<a href=\"ExitForm.action?id="+encodeEmpId+"&resignId="+encodeOffBoardId+"\">Final</a>");//9
					} else {
						if(!uF.parseToBoolean(rs.getString("is_alive")) && !rs.getString("emp_status").equalsIgnoreCase(TERMINATED)) {
							alInner.add("<a href=\"ExitForm.action?id="+encodeEmpId+"&resignId="+encodeOffBoardId+"\">Account closed</a>");//9
						} else {
							alInner.add("<a href=\"ExitForm.action?id="+encodeEmpId+"&resignId="+encodeOffBoardId+"\">Full & Final</a>");//9
						}
					}
				} else {
					alInner.add("-");//9
				}
				
				alInner.add(uF.showData(hmUserTypeMap.get(userType), ""));//10
				alInner.add(uF.showData(rs.getString("emp_status"), ""));//11
				alInner.add(uF.showData(hmEmpWLocation.get(rs.getString("wlocation_id")),"-"));//12
				alInner.add(uF.showData(hmEmpDepartment.get(rs.getString("depart_id")),"-"));//13
				alInner.add(uF.showData(hmEmpDesignation.get(rs.getString("emp_per_id")),"-"));//14
				alInner.add(rs.getString("emp_image"));//15
				if(rs.getString("added_by")!=null && !rs.getString("added_by").equals("")) {
					alInner.add(uF.showData(hmEmpNames.get(rs.getString("added_by")),"-"));//16
				} else {
					alInner.add("-");//16
				}
				
								
				if(rs.getString("supervisor_emp_id")!=null && uF.parseToInt(rs.getString("supervisor_emp_id"))>0) {
					alInner.add(uF.showData(hmEmpNames.get(rs.getString("supervisor_emp_id")),"-"));//17
				} else {
					alInner.add("-");//17
				}
				alInner.add(rs.getString("emp_id"));//18
				
				//===start parvez date: 27-10-2021===
				Date strResigDate = uF.getFutureDate(uF.getDateFormat(rs.getString("last_day_date"), DBDATE), -uF.parseToInt(rs.getString("notice_days")));
				alInner.add(uF.getDateFormat(strResigDate+"", DBDATE, CF.getStrReportDateFormat()));//19
				//===end parvez date: 27-10-2021===
				
//				System.out.println("EA/1029--off_board_id="+rs.getString("off_board_id")+"===rs.getString(emp_status)="+rs.getString("emp_status"));
			
				if(rs.getString("emp_status")!=null && rs.getString("emp_status").equalsIgnoreCase(RESIGNED)) {
					if(rs.getString("entry_date")!=null && !rs.getString("entry_date").equals("")) {
						String lastDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT);
						java.util.Date regDate = uF.getDateFormatUtil(lastDate,DATE_FORMAT );
//						System.out.println("EA/1032--off_board_id="+rs.getString("off_board_id"));
						if(regDate!= null && regDate.equals(currDate) ) {
							todaysResignation.put(rs.getString("off_board_id"), alInner);
//							System.out.println("EA/1034--off_board_id="+rs.getString("off_board_id"));
						} else {
							if(rs.getBoolean("is_alive")) {
								//if(rs.getString("emp_status")!=null && !rs.getString("emp_status").equalsIgnoreCase("TERMINATED")) {
									pendingResignation.put(rs.getString("off_board_id"), alInner);
//									System.out.println("EA/1038--off_board_id="+rs.getString("off_board_id"));
//								}
							}
						}
				    }
				}
			    //System.out.println("empId==>"+rs.getString("emp_id")+"==>status==>"+rs.getString("emp_status")+"==>isLive==>"+rs.getBoolean("is_alive"));
				if(rs.getString("emp_status")!=null && (rs.getString("emp_status").equalsIgnoreCase(RESIGNED) || rs.getString("emp_status").equalsIgnoreCase(TERMINATED))) {	
//					System.out.println("empId==>"+rs.getString("emp_id")+"==>name==>"+hmEmpNames.get(rs.getString("emp_id"))+"==>status==>"+rs.getString("emp_status"));
				    if(rs.getString("last_day_date")!=null && !rs.getString("last_day_date").equals("")) {
						String resignDate = uF.getDateFormat(rs.getString("last_day_date"), DBDATE, DATE_FORMAT);
						java.util.Date resignationDate = uF.getDateFormatUtil(resignDate,DATE_FORMAT );
					
						alInner.add(resignDate);//18
						
						if(rs.getInt("approved_1")==1 && rs.getInt("approved_2")==1) {
							if(resignationDate.equals(currDate) ) {
								todaysFD.put((String)rs.getString("off_board_id"),alInner);
							}else if(resignationDate.equals(tomorrowDate)) {
								tomorrowFD.put((String)rs.getString("off_board_id"),alInner);
							}else if(resignationDate.equals(dayAfterTomorrowDate)) {
								dayAfterTomorrowFD.put((String)rs.getString("off_board_id"),alInner);
							}else if(resignationDate.before(currDate)) {
								if(rs.getBoolean("is_alive") || rs.getString("emp_status").equalsIgnoreCase(TERMINATED)) {
									pendingFD.put((String)rs.getString("off_board_id"),alInner);
								}
							}
						}
					}
				}
			}
			
			rs.close();
			pst.close();

			int totalResignation = todaysResignation.size() + pendingResignation.size();
			int totalFD = todaysFD.size() + tomorrowFD.size() +dayAfterTomorrowFD.size() + pendingFD.size();
					
			if(totalResignation == 0) {
				request.setAttribute("totalResignation","");
			} else {
				request.setAttribute("totalResignation",""+totalResignation);
			}
			
			if(totalFD == 0) {
				request.setAttribute("totalFD", "");
			} else {
				request.setAttribute("totalFD", ""+totalFD);
			}

			request.setAttribute("todaysResignation", todaysResignation);
			request.setAttribute("pendingResignation", pendingResignation);
			
			request.setAttribute("todaysFD", todaysFD);
			request.setAttribute("tomorrowFD", tomorrowFD);
			request.setAttribute("dayAfterTomorrowFD", dayAfterTomorrowFD);
			request.setAttribute("pendingFD", pendingFD);
				
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getConfirmationData(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
//		EncryptionUtility eU = new EncryptionUtility();
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpWLocation = CF.getWLocationMap(con,null,request,null); 
			Map<String, String> hmEmpDepartment = CF.getDepartmentMap(con,null,null); 
			Map<String, String> hmEmpDesignation = CF.getEmpDesigMap(con); 
			
			Map<String,List<String>> todaysConfirmation = new LinkedHashMap<String, List<String>>();
			Map<String,List<String>> tomorrowConfirmation = new LinkedHashMap<String, List<String>>();
			Map<String,List<String>> dayAfterTomorrowConfirmation = new LinkedHashMap<String, List<String>>();
			Map<String,List<String>> pendingConfirmation = new LinkedHashMap<String, List<String>>();
			Map<String, String> hmEmpProbation = new LinkedHashMap<String, String>();
			
			java.util.Date currDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),DBDATE, DATE_FORMAT),DATE_FORMAT );
			java.util.Date tomorrowDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 1),DBDATE, DATE_FORMAT),DATE_FORMAT );
			java.util.Date dayAfterTomorrowDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 2),DBDATE, DATE_FORMAT),DATE_FORMAT );
			
			StringBuilder sbQuery1 = new StringBuilder();
			sbQuery1.append("select * from probation_policy order by emp_id desc");
			pst = con.prepareStatement(sbQuery1.toString());
			rst = pst.executeQuery();
			while(rst.next()) {
				int probation = uF.parseToInt((String)rst.getString("probation_duration")) + uF.parseToInt((String)rst.getString("extend_probation_duration"));
				hmEmpProbation.put((String)rst.getString("emp_id"),String.valueOf(probation) );
			}
			rst.close();
			pst.close();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd,employee_official_details eod where epd.emp_per_id = eod.emp_id " +
					"and epd.emp_status = 'PROBATION' and approved_flag=true and is_alive=true and emp_filled_flag=true and joining_date is not null ");
			
			if(getStrWLocation()!=null && !getStrWLocation().equals("")) {
	            sbQuery.append(" and wlocation_id in ("+getStrWLocation()+") ");
	         } else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
	        
	        sbQuery.append(" order by epd.emp_per_id desc");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("employee activity confirmation pst==>"+pst);
			rst = pst.executeQuery();
			while(rst.next()) {
				
				List<String> alInner = new ArrayList<String>();
				alInner.add((String)rst.getString("emp_per_id"));//0
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rst.getString("emp_mname");
					}
				}
				
				alInner.add((String)rst.getString("emp_fname")+strEmpMName+" "+(String)rst.getString("emp_lname"));//1
				alInner.add((String)rst.getString("emp_status"));//2
				alInner.add(uF.getDateFormat(rst.getString("joining_date"), DBDATE, DATE_FORMAT));//3
				alInner.add(hmEmpName.get(rst.getString("added_by")));//4
				alInner.add(rst.getString("emp_image"));//5
				alInner.add(hmEmpWLocation.get(rst.getString("wlocation_id")));//6
				alInner.add(hmEmpDepartment.get(rst.getString("depart_id")));//7
				alInner.add(hmEmpDesignation.get(rst.getString("emp_per_id")));//8
				if(rst.getString("supervisor_emp_id")!=null && uF.parseToInt(rst.getString("supervisor_emp_id"))>0) {
					alInner.add(hmEmpName.get(rst.getString("supervisor_emp_id")));//9
				} else {
					alInner.add("-");//9
				}
				
				//check probation date
				if(rst.getString("joining_date")!=null && !rst.getString("joining_date").equals("")) {
					String joiningDate = uF.getDateFormat(rst.getString("joining_date"), DBDATE, DATE_FORMAT);
					
					java.util.Date startDate = uF.getDateFormatUtil(joiningDate,DATE_FORMAT );
					
					int probation = uF.parseToInt(hmEmpProbation.get((String)rst.getString("emp_per_id")));
										
					String futureDate = uF.getDateFormat(""+uF.getFutureDate(startDate, probation),DBDATE, DATE_FORMAT);
					java.util.Date confDate = null;
					if(probation>0) {
						confDate = uF.getDateFormatUtil(futureDate,DATE_FORMAT );
					} else {
						confDate = uF.getDateFormatUtil(joiningDate,DATE_FORMAT );
					}
					
					if(probation>0) {
						alInner.add(""+ futureDate);//10
					} else {
						alInner.add(joiningDate);//10
					}
//					String encodeEmpId = eU.encode(rst.getString("emp_per_id"));
					String encodeEmpId = rst.getString("emp_per_id");
					alInner.add("<a href=\"EmployeeActivity.action?dataType=D&strActivity=7&strEmpId="+encodeEmpId+"\">Take Action</a>");//11
					if(confDate.equals(currDate) ) {
						todaysConfirmation.put((String)rst.getString("emp_per_id"),alInner);
					} else if(confDate.equals(tomorrowDate)) {
						tomorrowConfirmation.put((String)rst.getString("emp_per_id"),alInner);
					} else if(confDate.equals(dayAfterTomorrowDate)) {
						dayAfterTomorrowConfirmation.put((String)rst.getString("emp_per_id"),alInner);
					} else if(confDate.before(currDate)) {
						pendingConfirmation.put((String)rst.getString("emp_per_id"),alInner);
					}
				} else {
					alInner.add("N/A");//10
					alInner.add("N/A");//11
				}
			}
			rst.close();
			pst.close();
			
			int totalConfirmation = todaysConfirmation.size() + tomorrowConfirmation.size() +dayAfterTomorrowConfirmation.size() + pendingConfirmation.size();
//			System.out.println("totalConfirmation activity==>"+totalConfirmation);	
			
			if(totalConfirmation == 0) {
				request.setAttribute("totalConfirmation", "");
			} else {
				request.setAttribute("totalConfirmation", ""+totalConfirmation);
			}
			
			request.setAttribute("todaysConfirmation", todaysConfirmation);
			request.setAttribute("tomorrowConfirmation", tomorrowConfirmation);
			request.setAttribute("dayAfterTomorrowConfirmation", dayAfterTomorrowConfirmation);
			request.setAttribute("pendingConfirmation", pendingConfirmation);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}	
	}
		
	private void getInductionData(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
//		EncryptionUtility eU = new EncryptionUtility();
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmEmpWLocation = CF.getWLocationMap(con,null,request,null); 
			Map<String, String> hmEmpDepartment = CF.getDepartmentMap(con,null,null); 
			Map<String, String> hmEmpDesignation = CF.getEmpDesigMap(con); 
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			Map<String,List<String>> todaysInduction = new LinkedHashMap<String, List<String>>();
			Map<String,List<String>> tomorrowInduction = new LinkedHashMap<String, List<String>>();
			Map<String,List<String>> dayAfterTomorrowInduction = new LinkedHashMap<String, List<String>>();
			
			java.sql.Date dayAfterTomorrowDate1 =  uF.getFutureDate(CF.getStrTimeZone(),2);
			java.sql.Date tomorrowDate1 =  uF.getFutureDate(CF.getStrTimeZone(),1);
			
			java.util.Date tomorrowDate = uF.getDateFormatUtil(uF.getDateFormat(""+tomorrowDate1,DBDATE, DATE_FORMAT),DATE_FORMAT );
			java.util.Date dayAfterTomorrowDate = uF.getDateFormatUtil(uF.getDateFormat(""+dayAfterTomorrowDate1,DBDATE, DATE_FORMAT),DATE_FORMAT );
			
			Map<String, String> hmEmpname = CF.getEmpNameMap(con, null, null);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd,employee_official_details eod where epd.emp_per_id = eod.emp_id "
					        +"and (joining_date =? or joining_date =? or joining_date =?) and joining_date is not null and is_delete = false ");
			
			if(getStrWLocation()!=null && !getStrWLocation().equals("")) {
	            sbQuery.append(" and wlocation_id in ("+getStrWLocation()+") ");
	         }else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	  }
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	  } else {
	        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	  }
			}
	        
	        sbQuery.append(" order by emp_per_id desc");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(2, tomorrowDate1);
			pst.setDate(3, dayAfterTomorrowDate1);
//			System.out.println("employee activity induction pst1==>"+pst);
			rst = pst.executeQuery();
			while(rst.next()) {
				
				List<String> alInner = new ArrayList<String>();
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rst.getString("emp_mname");
					}
				}
				
				alInner.add((String)rst.getString("emp_fname")+strEmpMName+" "+(String)rst.getString("emp_lname"));//0
				alInner.add(uF.getDateFormat(rst.getString("joining_date"), DBDATE, DATE_FORMAT));//1
				alInner.add(hmEmpname.get(rst.getString("added_by")));//2
				alInner.add(rst.getString("emp_image"));//3
				if(rst.getBoolean("approved_flag") == false && rst.getBoolean("is_alive") == false && rst.getBoolean("emp_filled_flag") == false) {
//					String encodeEmpId = eU.encode(rst.getString("emp_per_id"));
					String encodeEmpId = rst.getString("emp_per_id");
					alInner.add("<a href=\"javascript:void(0)\" name=\"addclick\" onclick=\" window.location='AddEmployee.action?mode=onboard&step=7&operation=EO&empId="+encodeEmpId+"';\">Onboarding Form</a>");//4
				} else {
					alInner.add("Onboarding Form Filled up.");//4
				}
				
				if(rst.getString("wlocation_id")!=null && rst.getString("wlocation_id")!=null) {
					alInner.add(hmEmpWLocation.get(rst.getString("wlocation_id")));//5
				} else {
					alInner.add("-");//5
				}
				
				if(rst.getString("depart_id")!=null && rst.getString("depart_id")!=null) {
					alInner.add(hmEmpDepartment.get(rst.getString("depart_id")));//6
				} else {
					alInner.add("-");//6
				}
				
				if(hmEmpDesignation.get(rst.getString("emp_per_id"))!=null && !hmEmpDesignation.get(rst.getString("emp_per_id")).equals("")) {
					alInner.add(hmEmpDesignation.get(rst.getString("emp_per_id")));//7
				} else {
					alInner.add("-");//7
				}
				
				if(rst.getString("supervisor_emp_id")!=null && uF.parseToInt(rst.getString("supervisor_emp_id"))>0) {
					alInner.add(hmEmpName.get(rst.getString("supervisor_emp_id")));//8
				} else {
					alInner.add("-");//8
				}
				
				if(rst.getBoolean("approved_flag") == true && rst.getBoolean("is_alive") == true && rst.getBoolean("emp_filled_flag") == true) {
//					String encodeEmpId = eU.encode(rst.getString("emp_per_id"));
					String encodeEmpId = rst.getString("emp_per_id");
					alInner.add("<a href=\"javascript:void(0)\" name=\"addclick\" onclick=\" window.location='AddEmployee.action?operation=SA&empId="+encodeEmpId+"';\">Resend appointment letter to employee</a>");//9
				} else {
					alInner.add("");//9
				}
				
				String strJoiningDate = uF.getDateFormat(rst.getString("joining_date"), DBDATE, DATE_FORMAT);
				java.util.Date joiningDate = uF.getDateFormatUtil(strJoiningDate,DATE_FORMAT );
				java.util.Date currDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),DBDATE, DATE_FORMAT),DATE_FORMAT );
				
				
				if(joiningDate.equals(currDate)) {
					todaysInduction.put((String)rst.getString("emp_per_id"),alInner);
				}else if(joiningDate.equals(tomorrowDate)) {
					tomorrowInduction.put((String)rst.getString("emp_per_id"),alInner);
				}else if(joiningDate.equals(dayAfterTomorrowDate)) {
						dayAfterTomorrowInduction.put((String)rst.getString("emp_per_id"),alInner);
				}
			}
			rst.close();
			pst.close();
			
			request.setAttribute("todaysInduction", todaysInduction);
			request.setAttribute("tomorrowInduction", tomorrowInduction);
			request.setAttribute("dayAfterTomorrowInduction", dayAfterTomorrowInduction);
			
		
			
			Map<String,List<String>> pendingInduction = new LinkedHashMap<String, List<String>>();
			StringBuilder sbQuery3 = new StringBuilder();
			sbQuery3.append("select * from employee_personal_details  epd,employee_official_details eod where epd.emp_per_id = eod.emp_id " +
				"and (joining_date is null or joining_date < ?) and ((approved_flag=false and is_alive=false and emp_filled_flag=false) " +
				"or (approved_flag=false and is_alive=false and emp_filled_flag=true)) and is_delete = false ");
			
			if(getStrWLocation()!=null && !getStrWLocation().equals("")) {
	            sbQuery3.append(" and (wlocation_id is null or wlocation_id in ("+getStrWLocation()+")) ");
	         }else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		  sbQuery3.append(" and (wlocation_id is null or wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+"))");
	        	  } else {
	        		  sbQuery3.append(" and (wlocation_id is null or wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+"))");
	        	  }
			}
	            
	        if(uF.parseToInt(getF_org())>0) {
				sbQuery3.append(" and (org_id is null or org_id = "+uF.parseToInt(getF_org())+") ");
			}else if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		  sbQuery3.append(" and (org_id is null or org_id in ("+(String)session.getAttribute(ORG_ACCESS)+"))");
	        	  } else {
	        		  sbQuery3.append(" and (org_id is null or org_id in ("+(String)session.getAttribute(ORGID)+"))");
	        	  }
			}
	        
	        sbQuery3.append(" order by emp_per_id desc");
			pst = con.prepareStatement(sbQuery3.toString());
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("employee activity induction pst2==>"+pst);
			rst = pst.executeQuery();
			while(rst.next()) {
				
				List<String> alInner = new ArrayList<String>();
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rst.getString("emp_mname");
					}
				}
				
				alInner.add((String)rst.getString("emp_fname")+strEmpMName+" "+(String)rst.getString("emp_lname"));
				alInner.add(uF.getDateFormat(rst.getString("joining_date"), DBDATE, DATE_FORMAT));
				alInner.add(hmEmpname.get(rst.getString("added_by")));//2
				alInner.add(rst.getString("emp_image"));//3
//				String encodeEmpId = eU.encode(rst.getString("emp_per_id"));
				String encodeEmpId = rst.getString("emp_per_id");
				alInner.add("<a href=\"javascript:void(0)\" name=\"addclick\" onclick=\" window.location='AddEmployee.action?mode=onboard&step=7&operation=EO&empId="+encodeEmpId+"';\">Onboarding Form</a>");//4
				
				if(rst.getString("wlocation_id")!=null && rst.getString("wlocation_id")!=null) {
					alInner.add(hmEmpWLocation.get(rst.getString("wlocation_id")));//5
				} else {
					alInner.add("-");//5
				}
				
				if(rst.getString("depart_id")!=null && rst.getString("depart_id")!=null) {
					alInner.add(hmEmpDepartment.get(rst.getString("depart_id")));//6
				} else {
					alInner.add("-");//6
				}
				
				if(hmEmpDesignation.get(rst.getString("emp_per_id"))!=null && !hmEmpDesignation.get(rst.getString("emp_per_id")).equals("")) {
					alInner.add(hmEmpDesignation.get(rst.getString("emp_per_id")));//7
				} else {
					alInner.add("-");//7
				}
				
				if(rst.getString("supervisor_emp_id")!=null && uF.parseToInt(rst.getString("supervisor_emp_id"))>0) {
					alInner.add(hmEmpName.get(rst.getString("supervisor_emp_id")));//8
				} else {
					alInner.add("-");//8
				}
				

				if(rst.getBoolean("approved_flag") == true && rst.getBoolean("is_alive") == true && rst.getBoolean("emp_filled_flag") == true) {
					alInner.add("<a href=\"javascript:void(0)\" name=\"addclick\" onclick=\"window.location='AddEmployee.action?operation=SA&empId="+encodeEmpId+"';\">Resend appointment letter to employee</a>");//9
				} else {
					alInner.add("");//9
				}
				
				pendingInduction.put((String)rst.getString("emp_per_id"),alInner);
			
			}
			rst.close();
			pst.close();
			
			request.setAttribute("pendingInduction", pendingInduction);
			int totalInduction = todaysInduction.size() + tomorrowInduction.size() +dayAfterTomorrowInduction.size() + pendingInduction.size();
			
			if(totalInduction == 0) {
				request.setAttribute("totalInduction", "");
			} else {
				request.setAttribute("totalInduction", ""+totalInduction);
			}
			
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getLeaveDetails(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
//			String levelId = CF.getEmpLevelId(con, getStrEmpId());
//			List<List<String>> leaveTypeListWithBalance = CF.getLevelLeaveTypeBalanceForEmp(con, levelId, ""+getStrEmpId(), CF);
//			request.setAttribute("leaveTypeListWithBalance", leaveTypeListWithBalance);
			
			EmployeeLeaveEntryReport leaveEntryReport = new EmployeeLeaveEntryReport();
			leaveEntryReport.request = request;
			leaveEntryReport.session = session;
			leaveEntryReport.CF = CF;
			leaveEntryReport.setStrEmpId(getStrEmpId());
			leaveEntryReport.setDataType("L");
			leaveEntryReport.viewEmployeeLeaveEntry1();
			
			List<List<String>> leaveTypeListWithBalance = (List<List<String>>)request.getAttribute("leaveList");
			request.setAttribute("leaveTypeListWithBalance", leaveTypeListWithBalance);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadValidateEmpActivity(UtilityFunctions uF) {
	
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if (uF.parseToInt(getF_org()) <= 0) {
				for(int i=0; organisationList != null && i<organisationList.size(); i++) {
					if(uF.parseToInt(organisationList.get(i).getOrgId()) == uF.parseToInt((String) session.getAttribute(ORGID))) {
						setF_org((String) session.getAttribute(ORGID));
					} else {
						if(i==0) {
							setF_org(organisationList.get(0).getOrgId());
						}
					}
				}
			}
		} else {
			if (uF.parseToInt(getF_org()) <= 0) {
				setF_org((String) session.getAttribute(ORGID));
			}
			organisationList = new FillOrganisation(request).fillOrganisation();
		}
		
		if(getDataType() == null || getDataType().equals("A")) {
			activityList = new FillActivity(request).fillActivityByNode(true, false,uF.parseToInt(getStrEmpId()));
		} else if(getDataType() == null || getDataType().equals("D")) {
			activityList = new FillActivity(request).fillActivityByNode(true, true,uF.parseToInt(getStrEmpId()));
		}
		documentList = new FillDocument(request).fillDocumentList(null, getF_org());
		
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		desigList = new ArrayList<FillDesig>();
		gradeList = new ArrayList<FillGrade>();
		empStatusList = new FillEmpStatus(request).fillEmpStatus();
		
		empList = new FillEmployee(request).fillEmployeeNameOrgLocationDepartSBUDesigGrade(CF, getF_org(), getF_strWLocation(), getF_department(), null, getF_level(), null, null, null, false);
//		empList = new FillEmployee(request).fillAllLiveEmployees(CF, strUserType, strSessionEmpId, uF.parseToInt(getF_org()));
		organisationList1 = new FillOrganisation(request).fillOrganisationWithoutCurrentOrgId(getF_org());
//		empTypeList = new FillEmploymentType(request).fillEmploymentTypeWithoutCurrentEmployeeType(request, getStrEmpId());
		empTypeList = new FillEmploymentType().fillEmploymentType(request);
		wLocationList1 = new FillWLocation(request).fillWLocationWithoutCurrentLocation(uF.parseToInt(getStrEmpId()));
		departmentList1 = new FillDepartment(request).fillDepartmentWithoutCurrentDepartment(uF.parseToInt(getStrEmpId()));
		serviceList1 = new FillServices(request).fillServicesWithoutCurrentService(uF.parseToInt(getStrEmpId()));
//		levelList1 = new FillLevel(request).fillLevelWithoutCurrentLevel(uF.parseToInt(getStrEmpId()));
		levelList1 = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		gradeChangeList = new FillGrade(request).fillGradeFromEmpDesignationWithoutCurrentGrade(uF.parseToInt(getStrEmpId()));
	
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());

		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
//			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			
		} else {
//			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		
		return LOAD;
	}

	public void viewProfile(String strEmpIdReq) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			CF.getEmpProfileDetail(con, request, session, CF, uF, strUserType, strEmpIdReq);
			
			request.setAttribute(TITLE, TEmployeeActivity);
			
			List<List<String>> alSkills = new ArrayList<List<String>>();
			alSkills = CF.selectSkills(con, uF.parseToInt(strEmpIdReq));
			request.setAttribute("alSkills", alSkills);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String viewEmpActivity() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
//			Map<String, String> hmServiceName = CF.getServicesMap(con, false);
			String educationsName = CF.getEmpEducations(con, getStrEmpId());
			pst = con.prepareStatement("select * from status_details s right join(select a.*,activity_code,activity_name from activity_details ad right join(" +
					"select a.*, org_name from org_details od right join(select a.*,wlocation_name from work_location_info wl right join(" +
					"select a.*,dept_name,dept_code from department_info d right join(select a.*,level_code,level_name from level_details ld right join(" +
					"select a.*,dd.designation_id,designation_code,designation_name,level_id from designation_details dd right join(" +
					"select * from grades_details gd right join(select a.activity_id,a.effective_date,a.emp_status_code,a.reason,a.entry_date," +
					"a.probation_period,a.notice_period,epd.*,eod.* from(select activity_id,effective_date,reason,emp_id,emp_status_code,entry_date," +
					"probation_period,notice_period from employee_activity_details where emp_id= ? and emp_activity_id=(select max(emp_activity_id) " +
					"from employee_activity_details where emp_id= ?)) as a ,employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id=eod.emp_id and a.emp_id=eod.emp_id) a on a.grade_id=gd.grade_id) a on a.designation_id=dd.designation_id" +
					") a on a.level_id=ld.level_id) a on a.depart_id=d.dept_id) a on a.wlocation_id=wl.wlocation_id) a on a.org_id = od.org_id" +
					") a on a.activity_id = ad.activity_id) a on a.emp_status_code=s.status_code order by effective_date desc, entry_date desc");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrEmpId()));
			rst = pst.executeQuery();
			String joining_date = null;
			Map<String, String> hmEmpActivityDetails = new HashMap<String, String>();
			Map<String, String> hmEmpLevel = CF.getEmpLevelMap(con);
			while (rst.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rst.getString("emp_mname");
					}
				}
				
				hmEmpActivityDetails.put("FULL_NAME", rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname"));
				hmEmpActivityDetails.put("EFFECTIVE_DATE", uF.getDateFormat(rst.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()));
				hmEmpActivityDetails.put("JOINING_DATE", uF.getDateFormat(rst.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));
				hmEmpActivityDetails.put("LEVEL_ID", hmEmpLevel.get(getStrEmpId()));
				hmEmpActivityDetails.put("LEVEL", "["+uF.showData(rst.getString("level_code"), "")+"] "+uF.showData(rst.getString("level_name"), ""));
				hmEmpActivityDetails.put("GRADE", "["+uF.showData(rst.getString("grade_code"), "")+"] "+uF.showData(rst.getString("grade_name"), ""));
				hmEmpActivityDetails.put("DESIGNATION", "["+uF.showData(rst.getString("designation_code"), "")+"] "+uF.showData(rst.getString("designation_name"), ""));
				hmEmpActivityDetails.put("WLOCATION", rst.getString("wlocation_name"));
				hmEmpActivityDetails.put("ORG_NAME", rst.getString("org_name"));
				hmEmpActivityDetails.put("DEPT", rst.getString("dept_name"));
				hmEmpActivityDetails.put("STATUS", rst.getString("status_name"));
//				hmEmpActivityDetails.put("ACTIVITY_STATUS", rst.getString("activity_name"));
				hmEmpActivityDetails.put("REASON", rst.getString("reason"));
				hmEmpActivityDetails.put("PROBATION_PERIOD", rst.getString("probation_period"));
				hmEmpActivityDetails.put("NOTICE_PERIOD", rst.getString("notice_period"));
				
				joining_date = rst.getString("joining_date");
				setEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
			}
			rst.close();
			pst.close();
			
			if(joining_date != null && !joining_date.trim().equals("") && !joining_date.trim().equalsIgnoreCase("NULL")) {
				uF.getTimeDuration(joining_date, CF, uF, request); // expWithUs
			}

			setStrEmpId2(getStrEmpId());
			
			hmEmpActivityDetails.put("EMP_ID", getStrEmpId());
			
			request.setAttribute("educationsName", educationsName);
			request.setAttribute("hmEmpActivityDetails", hmEmpActivityDetails);
						
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	public String insertEmpActivity() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			int departHod = 0; 
			pst = con.prepareStatement("select emp_id from employee_official_details where depart_id = ? and is_hod = true limit 1");
			pst.setInt(1, uF.parseToInt(getStrDepartment()));
			rs = pst.executeQuery();
//			System.out.println("inside emp_Activity pst1==>"+pst);
			while(rs.next()) {
				departHod = rs.getInt("emp_id");
			}
			rs.close();
			pst.close();
//			System.out.println("getEmpChangeGrade ===>> " + getEmpChangeGrade() + " -- getEmpGrade ===>> " + getEmpGrade());
			
	//===start parvez date: 29-06-2022===		
//			int x = 0;
//			System.out.println("Name="+getStrDocumentFileFileName());
			if(getStrDocumentFile()!=null){
				
				String strFileName = null;
				if (CF.getStrDocSaveLocation() == null) {
					strFileName = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, getStrDocumentFile(), getStrDocumentFileFileName(), getStrDocumentFileFileName(), CF);
				} else{
					strFileName = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation() + I_PEOPLE + "/" + I_DOCUMENT + "/" + I_OFFER_LETTER + "/"+ getStrEmpId2(), getStrDocumentFile(), getStrDocumentFileFileName(), getStrDocumentFileFileName(), CF);
				}
				
				pst = con.prepareStatement("insert into employee_activity_details (wlocation_id, department_id, level_id, desig_id, grade_id, " +
						"emp_status_code, activity_id, reason, effective_date, entry_date, user_id, emp_id, notice_period, probation_period, appraisal_id, " +
						"extend_probation_period, org_id,service_id,increment_type,increment_percent,transfer_type,learning_plan_id,assessment_id," +
						"training_id,emp_hod_id,appraisal_freq_id,emptype,review_ids,learning_ids,incumbent_emp_id,document_file_name) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setInt(1, uF.parseToInt(getStrWLocation()));
				pst.setInt(2, uF.parseToInt(getStrDepartment()));
				pst.setInt(3, uF.parseToInt(getStrLevel()));
//				pst.setInt(4, uF.parseToInt(getStrDesignationUpdate()));
				pst.setInt(4, uF.parseToInt(getStrDesignation()));
				if(uF.parseToInt(getStrActivity()) == uF.parseToInt(ACTIVITY_GRADE_CHANGE_ID)) {
					pst.setInt(5, ((getEmpChangeGrade()!=null && getEmpChangeGrade().length()>0) ? uF.parseToInt(getEmpChangeGrade()) : uF.parseToInt(getStrGrade())));
				} else {
					pst.setInt(5, ((getEmpGrade()!=null && getEmpGrade().length()>0) ? uF.parseToInt(getEmpGrade()) : uF.parseToInt(getStrGrade())));
				}
				pst.setString(6, getStrNewStatus());
				pst.setInt(7, uF.parseToInt(getStrActivity()));
				pst.setString(8, getStrReason());
				pst.setDate(9, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(11, uF.parseToInt(strSessionEmpId));
				pst.setInt(12, uF.parseToInt(getStrEmpId2()));
				pst.setInt(13, uF.parseToInt(getStrNoticePeriod()));
				pst.setInt(14, uF.parseToInt(getStrProbationPeriod()));
				pst.setInt(15, uF.parseToInt(getAppraisal_id()));
				pst.setInt(16, uF.parseToInt(getStrExtendProbationDays()));
				pst.setInt(17, uF.parseToInt(getStrOrganisation()));
				pst.setString(18, getStrSBU()!=null && !getStrSBU().equals("") ? ","+getStrSBU()+"," : null);
				pst.setInt(19, uF.parseToInt(getStrIncrementType()));
				pst.setDouble(20, uF.parseToDouble(getStrIncrementPercentage()));
				pst.setString(21, getStrTransferType());
				pst.setInt(22, uF.parseToInt(getLearningId()));
				pst.setInt(23, uF.parseToInt(getAssessmentId()));
				pst.setInt(24, uF.parseToInt(getTrainingId()));
				pst.setInt(25, departHod);
				pst.setInt(26, uF.parseToInt(getAppFreqId()));
				pst.setString(27, getEmploymentType());
				pst.setString(28, getStrReviewIds());
				pst.setString(29, getStrLearningIds());
				pst.setInt(30, uF.parseToInt(getStrIncumbentEmpId()));
				pst.setString(31, strFileName);
			
			} else{
				pst = con.prepareStatement("insert into employee_activity_details (wlocation_id, department_id, level_id, desig_id, grade_id, " +
						"emp_status_code, activity_id, reason, effective_date, entry_date, user_id, emp_id, notice_period, probation_period, appraisal_id, " +
						"extend_probation_period, org_id,service_id,increment_type,increment_percent,transfer_type,learning_plan_id,assessment_id," +
						"training_id,emp_hod_id,appraisal_freq_id,emptype,review_ids,learning_ids,incumbent_emp_id) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
				pst.setInt(1, uF.parseToInt(getStrWLocation()));
				pst.setInt(2, uF.parseToInt(getStrDepartment()));
				pst.setInt(3, uF.parseToInt(getStrLevel()));
//				pst.setInt(4, uF.parseToInt(getStrDesignationUpdate()));
				pst.setInt(4, uF.parseToInt(getStrDesignation()));
				if(uF.parseToInt(getStrActivity()) == uF.parseToInt(ACTIVITY_GRADE_CHANGE_ID)) {
					pst.setInt(5, ((getEmpChangeGrade()!=null && getEmpChangeGrade().length()>0) ? uF.parseToInt(getEmpChangeGrade()) : uF.parseToInt(getStrGrade())));
				} else {
					pst.setInt(5, ((getEmpGrade()!=null && getEmpGrade().length()>0) ? uF.parseToInt(getEmpGrade()) : uF.parseToInt(getStrGrade())));
				}
				pst.setString(6, getStrNewStatus());
				pst.setInt(7, uF.parseToInt(getStrActivity()));
				pst.setString(8, getStrReason());
				pst.setDate(9, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(11, uF.parseToInt(strSessionEmpId));
				pst.setInt(12, uF.parseToInt(getStrEmpId2()));
				pst.setInt(13, uF.parseToInt(getStrNoticePeriod()));
				pst.setInt(14, uF.parseToInt(getStrProbationPeriod()));
				pst.setInt(15, uF.parseToInt(getAppraisal_id()));
				pst.setInt(16, uF.parseToInt(getStrExtendProbationDays()));
				pst.setInt(17, uF.parseToInt(getStrOrganisation()));
				pst.setString(18, getStrSBU()!=null && !getStrSBU().equals("") ? ","+getStrSBU()+"," : null);
				pst.setInt(19, uF.parseToInt(getStrIncrementType()));
				pst.setDouble(20, uF.parseToDouble(getStrIncrementPercentage()));
				pst.setString(21, getStrTransferType());
				pst.setInt(22, uF.parseToInt(getLearningId()));
				pst.setInt(23, uF.parseToInt(getAssessmentId()));
				pst.setInt(24, uF.parseToInt(getTrainingId()));
				pst.setInt(25, departHod);
				pst.setInt(26, uF.parseToInt(getAppFreqId()));
				pst.setString(27, getEmploymentType());
				pst.setString(28, getStrReviewIds());
				pst.setString(29, getStrLearningIds());
				pst.setInt(30, uF.parseToInt(getStrIncumbentEmpId()));
				
			}
			
			/*pst = con.prepareStatement("insert into employee_activity_details (wlocation_id, department_id, level_id, desig_id, grade_id, " +
				"emp_status_code, activity_id, reason, effective_date, entry_date, user_id, emp_id, notice_period, probation_period, appraisal_id, " +
				"extend_probation_period, org_id,service_id,increment_type,increment_percent,transfer_type,learning_plan_id,assessment_id," +
				"training_id,emp_hod_id,appraisal_freq_id,emptype,review_ids,learning_ids,incumbent_emp_id) " +
				"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
			pst.setInt(1, uF.parseToInt(getStrWLocation()));
			pst.setInt(2, uF.parseToInt(getStrDepartment()));
			pst.setInt(3, uF.parseToInt(getStrLevel()));
//			pst.setInt(4, uF.parseToInt(getStrDesignationUpdate()));
			pst.setInt(4, uF.parseToInt(getStrDesignation()));
			if(uF.parseToInt(getStrActivity()) == uF.parseToInt(ACTIVITY_GRADE_CHANGE_ID)) {
				pst.setInt(5, ((getEmpChangeGrade()!=null && getEmpChangeGrade().length()>0) ? uF.parseToInt(getEmpChangeGrade()) : uF.parseToInt(getStrGrade())));
			} else {
				pst.setInt(5, ((getEmpGrade()!=null && getEmpGrade().length()>0) ? uF.parseToInt(getEmpGrade()) : uF.parseToInt(getStrGrade())));
			}
			pst.setString(6, getStrNewStatus());
			pst.setInt(7, uF.parseToInt(getStrActivity()));
			pst.setString(8, getStrReason());
			pst.setDate(9, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(11, uF.parseToInt(strSessionEmpId));
			pst.setInt(12, uF.parseToInt(getStrEmpId2()));
			pst.setInt(13, uF.parseToInt(getStrNoticePeriod()));
			pst.setInt(14, uF.parseToInt(getStrProbationPeriod()));
			pst.setInt(15, uF.parseToInt(getAppraisal_id()));
			pst.setInt(16, uF.parseToInt(getStrExtendProbationDays()));
			pst.setInt(17, uF.parseToInt(getStrOrganisation()));
			pst.setString(18, getStrSBU()!=null && !getStrSBU().equals("") ? ","+getStrSBU()+"," : null);
			pst.setInt(19, uF.parseToInt(getStrIncrementType()));
			pst.setDouble(20, uF.parseToDouble(getStrIncrementPercentage()));
			pst.setString(21, getStrTransferType());
			pst.setInt(22, uF.parseToInt(getLearningId()));
			pst.setInt(23, uF.parseToInt(getAssessmentId()));
			pst.setInt(24, uF.parseToInt(getTrainingId()));
			pst.setInt(25, departHod);
			pst.setInt(26, uF.parseToInt(getAppFreqId()));
			pst.setString(27, getEmploymentType());
			pst.setString(28, getStrReviewIds());
			pst.setString(29, getStrLearningIds());
			pst.setInt(30, uF.parseToInt(getStrIncumbentEmpId()));
			*/
			
			int x = pst.executeUpdate();
			
	//===end parvez date: 29-06-2022===		
			
			String strDate = uF.getCurrentDate(CF.getStrTimeZone())+"";
			if(x > 0) {
				pst = con.prepareStatement("select max(emp_activity_id) as emp_activity_id from employee_activity_details");
				rs = pst.executeQuery();
				int nEmpActivityId = 0;
				while(rs.next()) {
					nEmpActivityId = uF.parseToInt(rs.getString("emp_activity_id"));
				}
				
				Map<String, String>  hmActivity = CF.getActivityName(con);
				if(hmActivity == null) hmActivity = new HashMap<String, String>();
				
				int activityType = uF.parseToInt(getStrActivity());
			//Document List
				int documentType = uF.parseToInt((getStrDocumentName()));
				processActivity(con, activityType,documentType, uF.parseToInt(getStrEmpId2()), strDate, CF, uF, uF.showData(hmActivity.get(""+activityType), ""),nEmpActivityId);
				
				request.setAttribute(MESSAGE, SUCCESSM+"Employee activity successfully updated"+END);
			}
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+"Oops! Something went wrong. Please try again."+END);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	private boolean viewUpdateEmployeeSalaryDetailsByGrade(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		boolean flag = false;
		try {
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];
			
			con = db.makeConnection(con);
//			System.out.println("in viewUpdateEmployeeSalaryDetails ...");
			
			String levelId = CF.getEmpLevelId(con, getStrEmpId());
			String gradeId = CF.getEmpGradeId(con, getStrEmpId());			
			String strOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
			
			request.setAttribute("gradeId", gradeId);
			request.setAttribute("levelId", levelId);
			request.setAttribute("strOrgId", strOrgId);
			
			Map hmEmpMertoMap = new HashMap();
			Map hmEmpWlocationMap = new HashMap();
			Map hmEmpStateMap = new HashMap();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
			
			Map<String, String> hmEmpAnnualVarPolicyAmount = CF.getEmpAnnualVariablePolicyMonthAmount(con, uF, getStrEmpId(), strFinancialYearStart, strFinancialYearEnd);
			if(hmEmpAnnualVarPolicyAmount == null) hmEmpAnnualVarPolicyAmount = new HashMap<String, String>();
			request.setAttribute("hmEmpAnnualVarPolicyAmount", hmEmpAnnualVarPolicyAmount);
			
			List<List<String>> alE = new ArrayList<List<String>>();
			
			setDisableSalaryStructure(CF.getEmpDisableSalaryCalculation(con,uF,getStrEmpId()));
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE grade_id = ? and is_annual_variable=true and (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false) order by weight");
			pst.setInt(1, uF.parseToInt(gradeId));
			rs = pst.executeQuery();
			List<String> alAnnualSalaryHead = new ArrayList<String>();
			while(rs.next()) {
				if(!alAnnualSalaryHead.contains(rs.getString("salary_head_id"))) {
					alAnnualSalaryHead.add(rs.getString("salary_head_id"));
				}
			}
			rs.close();
			pst.close();
			
			String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String[] strPayCycleDate = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrgId);
			double dblReimbursementCTC = 0.0d;
			double dblReimbursementCTCOptional = 0.0d;
			if(strPayCycleDate !=null && strPayCycleDate.length > 0) {
				String startDate = strPayCycleDate[0];
				String endDate = strPayCycleDate[1];
				String strPC = strPayCycleDate[2];
			
				dblReimbursementCTC = CF.getReimbursementCTCHeadTotalAmount(con, uF, uF.parseToInt(getStrEmpId()), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(levelId));
				request.setAttribute("dblReimbursementCTC", ""+dblReimbursementCTC);
				
				dblReimbursementCTCOptional = CF.getReimbursementCTCOptinalHeadTotalAmount(con, uF, uF.parseToInt(getStrEmpId()), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(levelId));
				request.setAttribute("dblReimbursementCTCOptional", ""+dblReimbursementCTCOptional);
			}
			
			
			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMapByGrade(con, uF.parseToInt(gradeId));
			
			pst = con.prepareStatement("SELECT weight,isdisplay,pay_type,user_id,entry_date,amount," +
					"emp_salary_id,salary_head_amount,sd.earning_deduction,salary_head_amount_type," +
					"sub_salary_head_id, sd.salary_head_id as salary_head_id,multiple_calculation," +
					"salary_calculate_amount FROM (SELECT * FROM emp_salary_details WHERE emp_id = ? " +
					"AND service_id = ? AND effective_date = (SELECT MAX(effective_date) " +
					"FROM emp_salary_details WHERE emp_id = ? and is_approved=true and grade_id=?) " +
					"AND effective_date <= ? and grade_id=?) asd RIGHT JOIN salary_details sd " +
					"ON sd.salary_head_id = asd.salary_head_id " +
					"WHERE sd.grade_id=? and (sd.is_delete is null or sd.is_delete=false) " +
					"order by sd.earning_deduction desc, weight");
			pst.setInt(1, uF.parseToInt(getStrEmpId()) );
//			pst.setInt(2, uF.parseToInt(getCCID()) );
			pst.setInt(2, 0);  // Default Service Id
			pst.setInt(3, uF.parseToInt(getStrEmpId()));
			pst.setInt(4, uF.parseToInt(gradeId));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt(gradeId));
			pst.setInt(7, uF.parseToInt(gradeId));
			rs = pst.executeQuery();
//			System.out.println("pst viewUpdateEmployeeSalaryDetailsByGrade ===>> " + pst);
			Map<String, String> hmSalaryAmountMap = new HashMap<String, String>();
			List alSalaryDuplicationTracer = new ArrayList();
			int nEarningCnt = 0;
			int nDisplay = 0; 
			while(rs.next()) {
				
				if(uF.parseToInt(rs.getString("salary_head_id")) > 0 && uF.parseToInt(rs.getString("salary_head_id"))!=CTC && rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").trim().equals("E")) {
					nEarningCnt++;
				}
				
				if(uF.parseToBoolean(rs.getString("isdisplay"))) {
					nDisplay++;
				}				
				
				hmSalaryAmountMap.put(rs.getString("salary_head_id"), (rs.getString("salary_head_amount_type") != null && rs.getString("salary_head_amount_type").equals("P")) ?  uF.formatIntoFourDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))) : uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(uF.parseToInt(rs.getString("emp_salary_id"))+"");	//0
				String rsHeadId = rs.getString("salary_head_id");
				alInner.add(rsHeadId);	//1
				
				alInner.add(uF.showData(hmSalaryMap.get(rsHeadId), ""));	//2
				alInner.add(rs.getString("earning_deduction"));	//3
				alInner.add(rs.getString("salary_head_amount_type")); //4
				rsHeadId = rs.getString("sub_salary_head_id");	
				alInner.add(rsHeadId);	//5
				
				alInner.add("");	//6
				
				alInner.add(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount")))); //7
				 
				double dblAmount = 0;
				if(rs.getString("amount")==null) {
					String strAmountType = rs.getString("salary_head_amount_type");
					if(strAmountType!=null && strAmountType.equalsIgnoreCase("P")) {
						dblAmount = rs.getDouble("salary_calculate_amount");
					}else if(strAmountType!=null && strAmountType.equalsIgnoreCase("A")) {
						dblAmount = rs.getDouble("salary_head_amount");
					}
				} else {
					dblAmount = rs.getDouble("amount") ;
				}
				
//				System.out.println("dblAmount=="+dblAmount);
				
				StringBuilder sbMulcalType = new StringBuilder();
				if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
					String strMulCal = rs.getString("multiple_calculation");
					CF.appendMultiplePercentageCalType(uF,sbMulcalType,strMulCal,hmSalaryMap,alAnnualSalaryHead); 
					
					alInner.add(uF.formatIntoOneDecimalWithOutComma(dblAmount)); //8
				} else {
					alInner.add(uF.formatIntoOneDecimalWithOutComma(dblAmount)); //8
				}
				
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat())); //9
				alInner.add(rs.getString("user_id"));	//10
				alInner.add(rs.getString("pay_type"));	//11
				alInner.add(uF.parseToBoolean(rs.getString("isdisplay"))+"");	//12
				alInner.add(rs.getString("weight"));	//13
				alInner.add(rs.getString("multiple_calculation"));	//14
				alInner.add(sbMulcalType.toString());	//15
				
				int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
				
				if(index>=0) {
					alE.remove(index);
					alE.add(index, alInner);
				} else {
					alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
					alE.add(alInner);
				}
				
//				System.out.println("alInner ===>> " + alInner);
				flag = true;	
			}
			rs.close();
			pst.close();
			
			setEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
			
//			System.out.println("hmTotal ===>> " + hmTotal);  
//			System.out.println("reportList alE ===>> " + alE.toString());  
			request.setAttribute("reportList", alE);
			request.setAttribute("nEarningCnt", ""+nEarningCnt);
			
			boolean displayFlag = false;
			if(nDisplay == 0) {
				displayFlag = true;
			}
			request.setAttribute("displayFlag", ""+displayFlag);
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}
	
	private boolean viewUpdateEmployeeSalaryDetails() {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF= new UtilityFunctions();
		boolean flag = false;
		try {
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];
			
			con = db.makeConnection(con);
//			System.out.println("in viewUpdateEmployeeSalaryDetails ...");
			
			String levelId = CF.getEmpLevelId(con, getStrEmpId());
			
			Map hmEmpMertoMap = new HashMap();
			Map hmEmpWlocationMap = new HashMap();
			Map hmEmpStateMap = new HashMap();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
			
			String strOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
			
			setDisableSalaryStructure(CF.getEmpDisableSalaryCalculation(con,uF,getStrEmpId()));
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id = ? and is_annual_variable=true and (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false) order by weight");
			pst.setInt(1, uF.parseToInt(levelId));
			rs = pst.executeQuery();
			List<String> alAnnualSalaryHead = new ArrayList<String>();
			while(rs.next()) {
				if(!alAnnualSalaryHead.contains(rs.getString("salary_head_id"))) {
					alAnnualSalaryHead.add(rs.getString("salary_head_id"));
				}
			}
			rs.close();
			pst.close();
			
			String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String[] strPayCycleDate = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrgId);
			double dblReimbursementCTC = 0.0d;
			double dblReimbursementCTCOptional = 0.0d;
			if(strPayCycleDate !=null && strPayCycleDate.length > 0) {
				String startDate = strPayCycleDate[0];
				String endDate = strPayCycleDate[1];
				String strPC = strPayCycleDate[2];
			
				dblReimbursementCTC = CF.getReimbursementCTCHeadTotalAmount(con, uF, uF.parseToInt(getStrEmpId()), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(levelId));
				request.setAttribute("dblReimbursementCTC", ""+dblReimbursementCTC);
				
				dblReimbursementCTCOptional = CF.getReimbursementCTCOptinalHeadTotalAmount(con, uF, uF.parseToInt(getStrEmpId()), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(levelId));
				request.setAttribute("dblReimbursementCTCOptional", ""+dblReimbursementCTCOptional);
			}
			
			Map<String, String> hmEmpAnnualVarPolicyAmount = CF.getEmpAnnualVariablePolicyMonthAmount(con, uF, getStrEmpId(), strFinancialYearStart, strFinancialYearEnd);
			if(hmEmpAnnualVarPolicyAmount == null) hmEmpAnnualVarPolicyAmount = new HashMap<String, String>();
			request.setAttribute("hmEmpAnnualVarPolicyAmount", hmEmpAnnualVarPolicyAmount);
			
			List<List<String>> alE = new ArrayList<List<String>>();			
			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con,uF.parseToInt(levelId));
			
			pst = con.prepareStatement("SELECT weight,isdisplay,pay_type,user_id,entry_date,amount," +
					"emp_salary_id,salary_head_amount,sd.earning_deduction,salary_head_amount_type," +
					"sub_salary_head_id, sd.salary_head_id as salary_head_id,multiple_calculation," +
					"salary_calculate_amount FROM (SELECT * FROM emp_salary_details WHERE emp_id = ? " +
					"AND service_id = ? AND effective_date = (SELECT MAX(effective_date) " +
					"FROM emp_salary_details WHERE emp_id = ? and is_approved=true and level_id = ?) " +
					"AND effective_date <= ? and level_id = ?) asd RIGHT JOIN salary_details sd " +
					"ON sd.salary_head_id = asd.salary_head_id WHERE sd.level_id = ? " +
					"and (sd.is_delete is null or sd.is_delete=false) order by sd.earning_deduction desc, weight");
			pst.setInt(1, uF.parseToInt(getStrEmpId()) );
			pst.setInt(2, 0);  // Default Service Id
			pst.setInt(3, uF.parseToInt(getStrEmpId()) );
			pst.setInt(4, uF.parseToInt(levelId) );
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt(levelId) );
			pst.setInt(7, uF.parseToInt(levelId) );
			rs = pst.executeQuery();
//			System.out.println("pst viewUpdateEmployeeSalaryDetails ===>> " + pst);
			Map<String, String> hmSalaryAmountMap = new HashMap<String, String>();
			List alSalaryDuplicationTracer = new ArrayList();
			while(rs.next()) {
				hmSalaryAmountMap.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(uF.parseToInt(rs.getString("emp_salary_id"))+"");	//0
				String rsHeadId = rs.getString("salary_head_id");
				alInner.add(rsHeadId);	//1
				
				alInner.add(uF.showData(hmSalaryMap.get(rsHeadId), ""));	//2
				
				alInner.add(rs.getString("earning_deduction"));	//3
				alInner.add(rs.getString("salary_head_amount_type")); //4
				rsHeadId = rs.getString("sub_salary_head_id");	
				alInner.add(rsHeadId);	//5
				alInner.add("");	//6
				
				alInner.add(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount")))); //7
				
				double dblAmount = 0;
				if(rs.getString("amount")==null) {
					String strAmountType = rs.getString("salary_head_amount_type");
					if(strAmountType!=null && strAmountType.equalsIgnoreCase("P")) {
						dblAmount = rs.getDouble("salary_calculate_amount");
					} else if(strAmountType!=null && strAmountType.equalsIgnoreCase("A")) {
						dblAmount = rs.getDouble("salary_head_amount");
					}
				} else {
					dblAmount = rs.getDouble("amount") ;
				}
				StringBuilder sbMulcalType = new StringBuilder();
				if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
					String strMulCal = rs.getString("multiple_calculation");
					CF.appendMultiplePercentageCalType(uF,sbMulcalType,strMulCal,hmSalaryMap,alAnnualSalaryHead); 
					
					alInner.add(uF.formatIntoOneDecimalWithOutComma(dblAmount)); //8
				} else {
					alInner.add(uF.formatIntoOneDecimalWithOutComma(dblAmount)); //8
				}				
				
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));	//9
				alInner.add(rs.getString("user_id"));	//10
				alInner.add(rs.getString("pay_type"));	//11
				alInner.add(uF.parseToBoolean(rs.getString("isdisplay"))+"");
				alInner.add(rs.getString("weight"));	//13
				alInner.add(rs.getString("multiple_calculation"));	//14
				alInner.add(sbMulcalType.toString());	//15				
				
				int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
				
				if(index>=0) {
					alE.remove(index);
					alE.add(index, alInner);
				} else {
					alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
					alE.add(alInner);
				}				
				
				flag = true;	
			}
			rs.close();
			pst.close();
			
			setEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
			request.setAttribute("reportList", alE);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}
	
	
	public void processActivity(Connection con, int activityType,int documentType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		
		this.CF= CF;
		switch(activityType) {
		
			case 1:
					processOfferLetter(con,activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 2:
					processAppointmentLetter(con,activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 3:
					processProbation(con,activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 4:
					processExtendProbation(con,activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	//				processResignation(con,activityType, nEmpId);
					break;
				
			case 5:
					processConfirmation(con,activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
					
			case 6:
					processTemporary(con,activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
					
			case 7:
					processPermanent(con, activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
					
			case 8:
					processTransfer(con,activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 9:
					processPromotion(con,activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 10:
					processIncrement(con, activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 11:
					processGradeChange(con,activityType, documentType, nEmpId,  strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 12:
					processTerminate(con,activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 13:
					processNoticePeriod(con,activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 14:
					processWithdrawnResignation(con, activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
					
			case 15:
					processFullAndFinal(con,activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
			
			case 29:
					processRelieving(con,activityType, documentType, nEmpId, strDate, CF, uF, strActivityName,nEmpActivityId);
					break;
				
			case 30:
					processExperience(con,activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;	
					
			case 32:
					processLifeEventIncrement(con,activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 33:
					processOfferICard(con,activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 34:
					processLOU(con,activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;	
				
			case 35:
					processNDA(con,activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 36:
					processoutLocationNDA(con,activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
					
			case 37:
				processDemotion(con,activityType, documentType, nEmpId,  strDate, CF, uF, strActivityName, nEmpActivityId);
				break;
				
			case 38:
				processRetirement(con,activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
				break;
				
			case 39:
				processEmployeeAgrement(con,activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
				break;
				
			case 40:
				processAddressProofBankLetter(con,activityType, documentType,  nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
				break;
				
			case 41:
				processInternshipLetter(con,activityType, documentType, nEmpId,  strDate, CF, uF, strActivityName, nEmpActivityId);
				break;
		}
	}

	private void processInternshipLetter(Connection con, int activityType, int documentType, int nEmpId, String strDate, com.konnect.jpms.util.CommonFunctions cF2,
			UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		sendAttachDocument(con, activityType,documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}


	private void processAddressProofBankLetter(Connection con, int activityType,int documentType, int nEmpId, String strDate, com.konnect.jpms.util.CommonFunctions cF2,
			UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		sendAttachDocument(con, activityType,documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
		
	}


	private void processEmployeeAgrement(Connection con, int activityType,int documentType, int nEmpId, String strDate, com.konnect.jpms.util.CommonFunctions cF2,
			UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		sendAttachDocument(con, activityType,documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
		
	}


	private void processRetirement(Connection con, int activityType,int documentType, int nEmpId, String strDate, com.konnect.jpms.util.CommonFunctions cF2,
			UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		try {
			pst = con.prepareStatement("UPDATE user_details SET status=? where emp_id=?");
			pst.setString(1, "INACTIVE");
			pst.setInt(2, nEmpId);
//			System.out.println("pst1==>"+pst);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("UPDATE employee_personal_details SET emp_status=?, is_alive=?, employment_end_date=? where emp_per_id=?");
			pst.setString(1, RETIRED);
			pst.setBoolean(2, false);
			pst.setDate(3, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			pst.setInt(4, nEmpId);
//			System.out.println("pst2==>"+pst);
			int y = pst.executeUpdate();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
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
	}


	private void processLifeEventIncrement(Connection con, int activityType, int documentType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
		if(nSalaryStrucuterType == S_GRADE_WISE) {
			updateEmployeeSalaryDetailsByGrade(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
		} else {
			updateEmployeeSalaryDetails(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
		}
		sendAttachDocument(con, activityType,documentType,nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}
	private void processExperience(Connection con, int activityType, int documentType,int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		sendAttachDocument(con, activityType,documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}

	private void processRelieving(Connection con, int activityType, int documentType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		sendAttachDocument(con, activityType, documentType,nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}

	private void processOfferLetter(Connection con, int activityType, int documentType,  int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
	
	//===start parvez date: 28-06-2022===	
//		System.out.println("EA/2481---activityType="+activityType+"--documentType="+documentType+"--strActivityName="+strActivityName);
		sendAttachDocument(con, activityType,documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
		/*Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
		if(uF.parseToBoolean(hmFeatureStatus.get(F_DOCUMENT_ATTACH_OFFER_LETTER))){
			
		} else{
			sendAttachDocument(con, activityType,documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
		}*/
	//===end parvez date: 28-06-2022===
	}
	
	private void processLOU(Connection con, int activityType, int documentType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		sendAttachDocument(con, activityType,documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}
	
	private void processOfferICard(Connection con, int activityType,int documentType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		sendAttachDocument(con, activityType, documentType,nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}
	
	private void processNDA(Connection con, int activityType,int documentType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		sendAttachDocument(con, activityType,documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}
	
	private void processoutLocationNDA(Connection con, int activityType,int documentType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		sendAttachDocument(con, activityType,documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}

	private void sendAttachDocument(Connection con, int activityType, int documentType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		ResultSet rst = null;
		PreparedStatement pst = null;
		try {
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
//			System.out.println(nEmpId+ " -- hmEmpInfo ===>> " + hmEmpInfo);
			Map<String, String> hmEmpInner = hmEmpInfo.get(""+nEmpId);
			String empOrgId = CF.getEmpOrgId(con, uF, ""+nEmpId);
			
			StringBuilder sbEmpSalTable = CF.getEmployeeSalaryDetails(con, CF, uF, ""+nEmpId, request, session);
			if(sbEmpSalTable == null) sbEmpSalTable = new StringBuilder();
			
			StringBuilder sbEmpAnnualBonusTable = CF.getEmployeeSalaryAnnualBonusDetails(con, CF, uF, ""+nEmpId, request, session);
			if(sbEmpAnnualBonusTable == null) sbEmpAnnualBonusTable = new StringBuilder();
			
	//===start parvez date: 29-06-2022===		
			Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	//===end parvez date: 29-06-2022===		
			
			Map<String, Map<String, String>> hmHeader=new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmFooter=new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select * from document_collateral");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()) {
				if(rst.getString("_type").equals("H")) {
					Map<String, String> hmInner=new HashMap<String, String>();
					hmInner.put("COLLATERAL_ID", rst.getString("collateral_id"));
					hmInner.put("COLLATERAL_PATH", rst.getString("collateral_image"));
					hmInner.put("COLLATERAL_IMG_ALIGN", rst.getString("image_align"));
					hmInner.put("COLLATERAL_TEXT", uF.showData(rst.getString("collateral_text"),""));
					
					hmHeader.put(rst.getString("collateral_id"), hmInner);
				} else {
					Map<String, String> hmInner=new HashMap<String, String>();
					hmInner.put("COLLATERAL_ID", rst.getString("collateral_id"));
					hmInner.put("COLLATERAL_PATH", rst.getString("collateral_image"));
					hmInner.put("COLLATERAL_IMG_ALIGN", rst.getString("image_align"));
					hmInner.put("COLLATERAL_TEXT", uF.showData(rst.getString("collateral_text"),""));
					
					hmFooter.put(rst.getString("collateral_id"), hmInner);
				}
			}
			rst.close();
			pst.close();
			
			pst=con.prepareStatement("select * from nodes");
			rst = pst.executeQuery();
			Map<String, String> hmMapActivityNode = new HashMap<String, String>();
			while(rst.next()) {
				hmMapActivityNode.put(rst.getString("mapped_activity_id"), rst.getString("node_id"));
			}
			rst.close();
			pst.close();
			int nTriggerNode = uF.parseToInt(hmMapActivityNode.get(""+activityType));
			
			
			String strDocumentName = null;
			String strDocumentContent = null;
//			String strDocumentHeader = null;
//			String strDocumentFooter = null;
			String strHeader = null;
//			String strFooter = null;
//			String strHeaderImageAlign="";
//			String strHeaderCollateralText="";
//			String strHeaderTextAlign="";
//			String strFooterImageAlign="";
			String strFooterCollateralText="";
//			String strFooterTextAlign="";
			
			if(nTriggerNode > 0) {
				pst = con.prepareStatement("select * from document_comm_details where trigger_nodes like '%,"+nTriggerNode+",%' and document_id = "+documentType+" and status=1 and org_id=? order by document_id desc limit 1");
				pst.setInt(1, uF.parseToInt(empOrgId));
				rst = pst.executeQuery();
	//			System.out.println("new Date ===> " + new Date());
				while (rst.next()) {  
					strDocumentName = rst.getString("document_name");
					strDocumentContent = rst.getString("document_text");
					if(rst.getString("collateral_header")!=null && !rst.getString("collateral_header").equals("") && hmHeader.get(rst.getString("collateral_header"))!=null) {
						Map<String, String> hmInner=hmHeader.get(rst.getString("collateral_header"));
						strHeader = uF.showData(hmInner.get("COLLATERAL_PATH"),"");
//						strHeaderImageAlign=uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"),"");
//						strHeaderCollateralText=uF.showData(hmInner.get("COLLATERAL_TEXT"),"");
//						strHeaderTextAlign=uF.showData(hmInner.get("COLLATERAL_TEXT_ALIGN"),"");
					}
					if(rst.getString("collateral_footer")!=null && !rst.getString("collateral_footer").equals("") && hmFooter.get(rst.getString("collateral_footer"))!=null) {
						Map<String, String> hmInner=hmFooter.get(rst.getString("collateral_footer"));
//						strFooter = uF.showData(hmInner.get("COLLATERAL_PATH"),"");
//						strFooterImageAlign=uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"),"");
						strFooterCollateralText=uF.showData(hmInner.get("COLLATERAL_TEXT"),"");
//						strFooterTextAlign=uF.showData(hmInner.get("COLLATERAL_TEXT_ALIGN"),"");
					}
				}
				rst.close();
				pst.close();
			}
			
			pst = con.prepareStatement("select * from document_signature where org_id =?");
			pst.setInt(1, uF.parseToInt(empOrgId));
			rst = pst.executeQuery();
			String strAuthSign = null;
			String strHrSign = null;
			String strRecruiterSign = null;
			while (rst.next()) {
				if(rst.getInt("signature_type") == 1) {
					strAuthSign = rst.getString("signature_image");
				}
				if(rst.getInt("signature_type") == 2) {
					strHrSign = rst.getString("signature_image");
				}
				if(rst.getInt("signature_type") == 3) {
					if(rst.getInt("user_id") == uF.parseToInt((String)session.getAttribute(EMPID))) {
						strRecruiterSign = rst.getString("signature_image");
					}
				}
			}
			rst.close();
			pst.close();
			
			if(strDocumentName!=null) {
//				strDocumentName = strDocumentName.replace(" ", "");
				strDocumentName = strDocumentName!=null ? strDocumentName.trim() : "";
			}
			
			String strDomain = request.getServerName().split("\\.")[0];
			
//			System.out.println("hmEmpInner ===>> " + hmEmpInner);
			Notifications nF = new Notifications(N_NEW_ACTIVITY, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.session = session;
			nF.setStrEmpId(""+nEmpId);  
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
//			nF.setStrContextPath(request.getServletPath());
			nF.setStrContextPath(request.getContextPath());
			
			nF.setStrSalutation(uF.showData(hmEmpInner.get("SALUTATION"), ""));
			nF.setStrEmpFname(hmEmpInner.get("FNAME"));
			nF.setStrEmpMname(hmEmpInner.get("MNAME"));
			nF.setStrEmpLname(hmEmpInner.get("LNAME"));
			nF.setStrLastDateAtOffice(uF.getDateFormat(hmEmpInner.get("END_DATE"), DBDATE, CF.getStrReportDateFormat()));
			nF.setStrSalaryStructure(sbEmpSalTable.toString());
			String[] tmpEmpAnnBonus = sbEmpAnnualBonusTable.toString().split("::::");
//			System.out.println("tmpEmpAnnBonus[0] ===>> " + tmpEmpAnnBonus[0]);
//			System.out.println("tmpEmpAnnBonus[1] ===>> " + tmpEmpAnnBonus[1]);
			nF.setStrAnnualBonusStructure(tmpEmpAnnBonus[0]);
			if(uF.parseToDouble(getStrIncrementPercentage()) == 0 && tmpEmpAnnBonus.length>1) {
				setStrIncrementPercentage(tmpEmpAnnBonus[1]);
			}
//			nF.setStrIncrementPercent(tmpEmpAnnBonus[1]);
			nF.setStrActivityName(strActivityName);
			
			nF.setStrEffectiveDate(uF.getDateFormat(getEffectiveDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
			nF.setStrPromotionDate(uF.getDateFormat(getEffectiveDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
			nF.setStrIncrementPercent(""+uF.parseToDouble(getStrIncrementPercentage()));
//			System.out.println("getStrIncrementPercentage() ===>> " + getStrIncrementPercentage());
			Date dtNDAEndDate = uF.getFutureDate(uF.getDateFormatUtil(getEffectiveDate(), DATE_FORMAT), 365);
			nF.setStrCloseDate(uF.getDateFormat(dtNDAEndDate+"", DBDATE, CF.getStrReportDateFormat()));
//			nF.setStrPayStructure(sbEmpSalTable.toString());
			
			Map<String, String> hmParsedContent = null;
//			Document document = new Document(PageSize.A4);
			Document document = new Document(PageSize.A4,40, 40, 10, 60); 
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			StringBuilder sbHeader = new StringBuilder();
			StringBuilder sbFooter = new StringBuilder();
			String strDocName = null;
			String strDocContent = null;
			if(strDocumentContent!=null) {
//				hmParsedContent  = nF.parseContent(strDocumentContent, "", "");
				
				hmParsedContent  = nF.parseContent(strDocumentContent, "", "");
				strDocName = strDocumentName;
				strDocContent = hmParsedContent.get("MAIL_BODY");
			//	System.out.print("mail_body" +strDocContent);
				String strDocument = hmParsedContent.get("MAIL_BODY");
				if(strDocument!=null) {
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
//						System.out.println("Else");
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
				
				String headerPath="";
				if(strHeader!=null && !strHeader.equals("")) {
//					headerPath=CF.getStrDocRetriveLocation()+strHeader;
					if(CF.getStrDocRetriveLocation()==null) { 
						headerPath =  DOCUMENT_LOCATION + strHeader;
					} else { 
//						headerPath = CF.getStrDocRetriveLocation() +I_COLLATERAL+"/"+I_IMAGE+"/"+strHeader;
						headerPath = CF.getStrDocSaveLocation() +I_COLLATERAL+"/"+I_IMAGE+"/"+strHeader;
					}
				}
				
				if(headerPath != null && !headerPath.equals("")) {
					sbHeader.append("<table style=\"width: 100%;\"><tr>");
					sbHeader.append("<td>");
					if(strHeader!=null && !strHeader.equals("")) {
						sbHeader.append("<img src=\""+headerPath+"\">");
					}
					sbHeader.append("</td>");
					sbHeader.append("</tr></table>");
				}
				
				
				PdfWriter writer = PdfWriter.getInstance(document, buffer);
				HeaderFooterPageEvent event = new HeaderFooterPageEvent(sbHeader.toString(),strFooterCollateralText);
			    writer.setPageEvent(event);
				document.open();
//				System.out.println("strDocument ====> " +strDocument);
				HTMLWorker hw = new HTMLWorker(document);
				
				if(strDocument.contains(HR_SIGNATURE)) {
					String imageUrl=CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+empOrgId+"/"+I_DOC_SIGN+"/"+strHrSign;
					String 	strSignature ="<img src=\""+imageUrl+"\">";
					strDocument = strDocument.replace(HR_SIGNATURE, strSignature);
				}
			
				if(strDocument.contains(AUTHORITY_SIGNATURE)) {
				    String imageUrl=CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+empOrgId+"/"+I_DOC_SIGN+"/"+strAuthSign;
					String 	strSignature ="<img src=\""+imageUrl+"\">";
					strDocument = strDocument.replace(AUTHORITY_SIGNATURE, strSignature);
				}
				
//				System.out.println("strDocument ===>> " + strDocument);
				if(strDocument.contains(RECRUITER_SIGNATURE)) {
				    String imageUrl=CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+empOrgId+"/"+I_DOC_SIGN+"/"+(String)session.getAttribute(EMPID)+"/"+strRecruiterSign;
					String 	strSignature ="<img src=\""+imageUrl+"\">";
//					System.out.println("strSignature ===>> " + strSignature);
					strDocument = strDocument.replace(RECRUITER_SIGNATURE, strSignature);
				}
				
//				hw.parse(new StringReader(sbHeader.toString())); 
				hw.parse(new StringReader(strDocument));
//				hw.parse(new StringReader(sbFooter.toString()));
				document.close();  
			
			}
			
	//===start parvez date: 29-06-2022===		
			/*byte[] bytes = buffer.toByteArray();			
			if(strDocumentContent!=null && !strDocumentContent.trim().equals("") && !strDocumentContent.trim().equalsIgnoreCase("NULL") 
					&& getStrUpdate() == null && getStrUpdateDocument()!=null && !getStrUpdateDocument().trim().equals("") && !getStrUpdateDocument().trim().equalsIgnoreCase("NULL")) {
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName(strDocumentName+".pdf");
			}*/
			
			if(getStrDocumentFile()!=null && hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_DOCUMENT_ATTACH_OFFER_LETTER))){
				
				String strFileName = "";
				pst = con.prepareStatement("select * from employee_activity_details where emp_activity_id =?");
				pst.setInt(1, nEmpActivityId);
				rst = pst.executeQuery();
				while(rst.next()){
					strFileName = rst.getString("document_file_name");
				}
				rst.close();
				pst.close();
				
				nF.setStrAttachmentFileName(strFileName);
				if(CF.getStrDocSaveLocation() == null){
					nF.setStrAttachmentFileSource(DOCUMENT_LOCATION+strFileName.trim());
				} else {
					nF.setStrAttachmentFileSource(CF.getStrDocSaveLocation() + I_PEOPLE + "/" + I_DOCUMENT + "/" + I_OFFER_LETTER + "/"+ nEmpId+ "/" + strFileName.trim());
				}
				
				/*String strMailSubject = nF.getStrEmailSubject();
				String strMailBody = nF.getStrNewEmailBody();

				nF.setEmailTemplate(true);
				nF.sendNotifications();
				
				String strDocFileName = "";
				int pos = strFileName.lastIndexOf(".");
				if(pos != -1) {
				   strDocFileName = strFileName.substring(0, pos);
				}
				
				saveDocumentActivity(con, uF, CF, strDocFileName, sbHeader.toString(), strDocContent, strFooterCollateralText, strMailSubject, strMailBody, nEmpActivityId);*/
				
			} else{
				byte[] bytes = buffer.toByteArray();			
				if(strDocumentContent!=null && !strDocumentContent.trim().equals("") && !strDocumentContent.trim().equalsIgnoreCase("NULL") 
						&& getStrUpdate() == null && getStrUpdateDocument()!=null && !getStrUpdateDocument().trim().equals("") && !getStrUpdateDocument().trim().equalsIgnoreCase("NULL")) {
					nF.setPdfData(bytes);
					nF.setStrAttachmentFileName(strDocumentName+".pdf");
				}
				/*String strMailSubject = nF.getStrEmailSubject();
				String strMailBody = nF.getStrNewEmailBody(); 

				nF.setEmailTemplate(true);
				nF.sendNotifications();
				
				saveDocumentActivity(con, uF, CF, strDocName, sbHeader.toString(), strDocContent, strFooterCollateralText, strMailSubject, strMailBody, nEmpActivityId);*/
			}
	//===end parvez date: 29-06-2022===		
			
			String strMailSubject = nF.getStrEmailSubject();
			String strMailBody = nF.getStrNewEmailBody();

			nF.setEmailTemplate(true);
			nF.sendNotifications();
			
			saveDocumentActivity(con, uF, CF, strDocName, sbHeader.toString(), strDocContent, strFooterCollateralText, strMailSubject, strMailBody, nEmpActivityId);
			
		} catch (Exception e) {  
			e.printStackTrace();
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
	}

	private void processAppointmentLetter(Connection con, int activityType, int documentType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		sendAttachDocument(con, activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);		
	}

	private void processProbation(Connection con, int activityType, int documentType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			String joiningDate = null;
			pst = con.prepareStatement(" select emp_per_id,joining_date from employee_personal_details where emp_per_id = ?");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				joiningDate = rs.getString("joining_date");
				
			}
			rs.close();
			pst.close();
			
			Date lastProbationEndDate = null;
			if(joiningDate != null && !joiningDate.equals("")) {
				lastProbationEndDate = uF.getFutureDate(uF.getDateFormat(joiningDate, DBDATE),uF.parseToInt(getStrProbationPeriod()));
			
			
				Date currentProbationEndDate = uF.getFutureDate(lastProbationEndDate != null ? lastProbationEndDate : uF.getDateFormat(getEffectiveDate(), DATE_FORMAT) , uF.parseToInt(getStrExtendProbationDays())-1);
			//	Date currentProbationEndDate = uF.getFutureDate(uF.getDateFormat(joiningDate, DBDATE) , uF.parseToInt(getStrProbationPeriod())-1);
								
				pst = con.prepareStatement("update probation_policy set is_probation = true, probation_duration = ?, probation_end_date = ?,extend_probation_duration=?  where emp_id=?");
				pst.setInt(1, uF.parseToInt(getStrProbationPeriod()));
				pst.setDate(2, currentProbationEndDate);
				pst.setInt(3,0);
				pst.setInt(4, nEmpId);
				
				int x = pst.executeUpdate();
				pst.close();
				boolean flag = false;
				if(x==0) {
					pst = con.prepareStatement("insert into probation_policy (is_probation, probation_duration, probation_end_date, emp_id) values (?,?,?,?)");
					pst.setBoolean(1, true);
					pst.setInt(2, uF.parseToInt(getStrProbationPeriod()));
					pst.setDate(3, currentProbationEndDate);
					pst.setInt(4, uF.parseToInt(getStrEmpId2()));
					pst.execute();
					pst.close();
					
					flag = true;
				} else {
					flag = true;
				}
				
				if(flag) {
					
					pst = con.prepareStatement("update employee_personal_details set emp_status =?  where emp_per_id=?");
					pst.setString(1, PROBATION);
					pst.setInt(2, nEmpId);
					pst.executeUpdate();
					pst.close();
				}
				
				sendAttachDocument(con, activityType,documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
			}
			
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

	
	private void processExtendProbation(Connection con, int activityType, int documentType ,int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		
		try {
			//String effectiveDate = null;
			int probationPeriod = 0;
			String joiningDate = null;
			pst = con.prepareStatement("select * from employee_personal_details epd,probation_policy p where epd.emp_per_id = p.emp_id and emp_id=?");
			pst.setInt(1, nEmpId);
			rst = pst.executeQuery();
			
			while (rst.next()) {
				probationPeriod = rst.getInt("probation_duration");
				joiningDate = rst.getString("joining_date");
			}
			rst.close();
			pst.close();
						
			Date lastProbationEndDate = null;
			if(joiningDate != null && !joiningDate.equals("")) {
				lastProbationEndDate = uF.getFutureDate(uF.getDateFormat(joiningDate, DBDATE),(probationPeriod));
		        Date currentProbationEndDate = uF.getFutureDate(lastProbationEndDate != null ? lastProbationEndDate : uF.getDateFormat(getEffectiveDate(), DATE_FORMAT) , uF.parseToInt(getStrExtendProbationDays())-1);
				pst = con.prepareStatement("update probation_policy set is_probation = true, extend_probation_duration = ?, probation_end_date = ? where emp_id=?");
				pst.setInt(1, uF.parseToInt(getStrExtendProbationDays()));
				pst.setDate(2, currentProbationEndDate);
				pst.setInt(3, nEmpId);
	
				int x = pst.executeUpdate();
				pst.close();
				boolean flag = false;
				if(x==0) {
					pst = con.prepareStatement("insert into probation_policy (is_probation, extend_probation_duration, probation_end_date, emp_id) values (?,?,?,?)");
					pst.setBoolean(1, true);
					pst.setInt(2, uF.parseToInt(getStrExtendProbationDays()));
					pst.setDate(3, currentProbationEndDate);
					pst.setInt(4, uF.parseToInt(getStrEmpId2()));
	
					pst.execute();
					pst.close();
					
					flag = true;
				} else {
					flag = true;
				}
				
				if(flag) {
					pst = con.prepareStatement("update employee_personal_details set emp_status =?  where emp_per_id=?");
					pst.setString(1, PROBATION);
					pst.setInt(2, nEmpId);
					pst.executeUpdate();
					pst.close();
				}
				
				sendAttachDocument(con, activityType,documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
		  }
		} catch (Exception e) {
			e.printStackTrace();
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
	}
	
	private void updateEmployeeSalaryDetailsByGrade(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
//			String levelId = CF.getEmpLevelId(con, ""+nEmpId);
			String gradeId = CF.getEmpGradeId(con, ""+nEmpId);
			
			pst = con.prepareStatement("select * from salary_details where grade_id=?");
			pst.setInt(1, uF.parseToInt(gradeId));
			rs = pst.executeQuery();
			Map<String, String> hmEarningDeductionMap = new HashMap<String, String>();
			Map<String, String> hmSalaryTypeMap = new HashMap<String, String>();
			while(rs.next()) {
				hmEarningDeductionMap.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
				hmSalaryTypeMap.put(rs.getString("salary_head_id"), rs.getString("salary_type"));
			}
			rs.close();
			pst.close();
			 
			pst = con.prepareStatement("select * from emp_salary_details where effective_date = ? and emp_id = ?");
//			pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setDate	(1, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			pst.setInt(2, nEmpId);
			rs = pst.executeQuery();
			boolean isCurrentDateExist = false;
			while(rs.next()) {
				isCurrentDateExist = true;
			}
			rs.close();
			pst.close();
			
			if(isCurrentDateExist) {
				pst = con.prepareStatement("delete from emp_salary_details where effective_date = ? and emp_id = ?");
//				pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
				pst.setDate	(1, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setInt(2, nEmpId);
				pst.execute();
				pst.close();
			}
			
//			for(int i=0; i<emp_salary_id.length; i++) {
			for(int i=0; i<getSalary_head_id().length; i++) {
				
				String isDiplaySalaryHead = (String) request.getParameter("isDisplay_"+getSalary_head_id()[i]);
//					pst = con.prepareStatement(insertEmpSalaryDetails);
				pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id, salary_head_id, amount, entry_date, user_id, pay_type, " +
					"isdisplay, service_id, effective_date, earning_deduction, salary_type, is_approved,grade_id) " +
					"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
				pst.setInt(1, nEmpId);
				pst.setInt(2, uF.parseToInt(getSalary_head_id()[i]));
				pst.setDouble(3, uF.parseToDouble(getSalary_head_value()[i]));
				pst.setDate	(4, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
				pst.setInt(5, 1);
				pst.setString(6, "M");
//					pst.setBoolean(7, ArrayUtils.contains(hideIsDisplay, emp_salary_id[i])>=0);
				pst.setBoolean(7, uF.parseToBoolean(isDiplaySalaryHead));
				pst.setInt(8, 0);
				pst.setDate(9, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setString(10, hmEarningDeductionMap.get(getSalary_head_id()[i]));
				pst.setString(11, hmSalaryTypeMap.get(getSalary_head_id()[i]));
				pst.setBoolean(12, true);
				pst.setInt(13, uF.parseToInt(gradeId));
//				System.out.println("isDiplaySalaryHead==>"+isDiplaySalaryHead+"--pst insertEmpSalaryDetails==>"+pst);
				pst.execute();
				pst.close();
			}
			
			/*if(uF.parseToBoolean(getStrApplyArrear())) {
				CalculateAndGetArrearAmount cagaAmt = new CalculateAndGetArrearAmount();
				cagaAmt.request = request;
				cagaAmt.session = session;
				cagaAmt.CF = CF;
				cagaAmt.setEmpId(""+nEmpId);
				cagaAmt.setEffectiveDate(getEffectiveDate());
				cagaAmt.setCallFrom("EMPACTIVITY");
				if(nEmpActivityId == uF.parseToInt(ACTIVITY_INCREMENT_ID)) {
					cagaAmt.setCallFrom("Increment");
				} else if(nEmpActivityId == uF.parseToInt(ACTIVITY_LIFE_EVENT_ID)) {
					cagaAmt.setCallFrom("Life Event Increment");
				} else if(nEmpActivityId == uF.parseToInt(ACTIVITY_CONFIRMATION_ID)) {
					cagaAmt.setCallFrom("Confirmation");
				}
				cagaAmt.viewCalculatedArrearAmount(uF);
			}*/
			
			CF.updateNextEmpSalaryEffectiveDate(con, uF, nEmpId, getEffectiveDate(), DATE_FORMAT);
					
			pst = con.prepareStatement("update employee_official_details set is_disable_sal_calculate=? where emp_id = ?");
			pst.setBoolean(1, getDisableSalaryStructure());
			pst.setInt(2, nEmpId);
			pst.execute();
			pst.close();
			
			/**
			 * Calaculate CTC
			 * */
			Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, null, ""+nEmpId);
			
			MyProfile myProfile = new MyProfile();
			myProfile.session = session;
			myProfile.request = request;
			myProfile.CF = CF;
			myProfile.getSalaryHeadsforEmployeeByGrade(con, uF, nEmpId, hmEmpProfile);
			
			double grossAmount = 0.0d;
			double grossYearAmount = 0.0d;
			double deductAmount = 0.0d;
			double deductYearAmount = 0.0d;
			double netAmount = 0.0d;
			double netYearAmount = 0.0d;			
			List<List<String>> salaryHeadDetailsList = (List<List<String>>) request.getAttribute("salaryHeadDetailsList");
			for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
				List<String> innerList = salaryHeadDetailsList.get(i);
				if(innerList.get(1).equals("E")) {
					grossAmount +=uF.parseToDouble(innerList.get(2));
					grossYearAmount +=uF.parseToDouble(innerList.get(3));
				} else if(innerList.get(1).equals("D")) {
					double dblDeductMonth = 0.0d;
					double dblDeductAnnual = 0.0d;
					if(uF.parseToInt(innerList.get(4)) == EMPLOYEE_ESI) {
						dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
						dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
					} else if(uF.parseToInt(innerList.get(4)) == EMPLOYER_ESI) {
						dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
						dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
					} else {
						dblDeductMonth += Math.round(uF.parseToDouble(innerList.get(2)));
						dblDeductAnnual += Math.round(uF.parseToDouble(innerList.get(3)));
					}
					deductAmount += dblDeductMonth;
					deductYearAmount += dblDeductAnnual;
				}
			}
			
			Map<String,String> hmContribution = (Map<String,String>) request.getAttribute("hmContribution");
			if(hmContribution == null) hmContribution = new HashMap<String, String>();
			double dblMonthContri = 0.0d;
			double dblAnnualContri = 0.0d;
			boolean isEPF = uF.parseToBoolean((String)request.getAttribute("isEPF"));
			boolean isESIC = uF.parseToBoolean((String)request.getAttribute("isESIC"));
			boolean isLWF = uF.parseToBoolean((String)request.getAttribute("isLWF"));
			if(isEPF || isESIC || isLWF) {
				if(isEPF) {
					double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
					double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
					dblMonthContri += dblEPFMonth;
					dblAnnualContri += dblEPFAnnual;
				}
				if(isESIC) {
					double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
					double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
					dblMonthContri += dblESIMonth;
					dblAnnualContri += dblESIAnnual;
				}
				if(isLWF) {
					double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
					double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
					dblMonthContri += dblLWFMonth;
					dblAnnualContri += dblLWFAnnual;
				}
			}
			
			double dblCTCMonthly = grossAmount + dblMonthContri;
			double dblCTCAnnualy = grossYearAmount + dblAnnualContri;
			
			List<List<String>> salaryAnnualVariableDetailsList = (List<List<String>>)request.getAttribute("salaryAnnualVariableDetailsList");
			if(salaryAnnualVariableDetailsList == null) salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
			int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
			if(nAnnualVariSize > 0) {
				double grossAnnualAmount = 0.0d;
				double grossAnnualYearAmount = 0.0d;
				for(int i = 0; i < nAnnualVariSize; i++) {
					List<String> innerList = salaryAnnualVariableDetailsList.get(i);
					double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
					double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
					grossAnnualAmount += dblEarnMonth;
					grossAnnualYearAmount += dblEarnAnnual;
				}
				dblCTCMonthly += grossAnnualAmount;
				dblCTCAnnualy += grossAnnualYearAmount;
			}
			
			netAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCMonthly));							 
			netYearAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCAnnualy));
			
			EmpSalaryApproval salaryApproval = new EmpSalaryApproval();
			salaryApproval.request = request;
			salaryApproval.session = session;
			salaryApproval.CF = CF;
			Map<String, String> hmPrevCTC = salaryApproval.getPrevCTCDetailsByGrade(con, uF, ""+nEmpId);
			
			if(hmPrevCTC == null) hmPrevCTC = new HashMap<String, String>();
			double dblIncrementMonthAmt = netAmount - uF.parseToDouble(hmPrevCTC.get("PREV_MONTH_CTC"));
			double dblIncrementAnnualAmt = netYearAmount - uF.parseToDouble(hmPrevCTC.get("PREV_ANNUAL_CTC"));
	        
			pst = con.prepareStatement("update employee_official_details set month_ctc=?,annual_ctc=?,prev_month_ctc=?," +
					"prev_annual_ctc=?,incre_month_amount=?,incre_annual_amount=? where emp_id=?");
			pst.setDouble(1, netAmount);
			pst.setDouble(2, netYearAmount);
			pst.setDouble(3, uF.parseToDouble(hmPrevCTC.get("PREV_MONTH_CTC")));
			pst.setDouble(4, uF.parseToDouble(hmPrevCTC.get("PREV_ANNUAL_CTC")));
			pst.setDouble(5, dblIncrementMonthAmt);
			pst.setDouble(6, dblIncrementAnnualAmt);
			pst.setInt(7, nEmpId);
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
		
	}

	private void updateEmployeeSalaryDetails(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			//code for getting userid
	//		System.out.println("updateEmployeeSalaryDetails: getEmpId==>"+getEmpId());
			String levelId = CF.getEmpLevelId(con, ""+nEmpId);
			
			pst = con.prepareStatement("select * from salary_details where level_id=?");
			pst.setInt(1, uF.parseToInt(levelId));
			rs = pst.executeQuery();
			Map<String, String> hmEarningDeductionMap = new HashMap<String, String>();
			Map<String, String> hmSalaryTypeMap = new HashMap<String, String>();
			while(rs.next()) {
				hmEarningDeductionMap.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
				hmSalaryTypeMap.put(rs.getString("salary_head_id"), rs.getString("salary_type"));
			}
			rs.close();
			pst.close();
			 
			pst = con.prepareStatement("select * from emp_salary_details where effective_date = ? and emp_id = ?");
	//		pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setDate	(1, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			pst.setInt(2, nEmpId);
			rs = pst.executeQuery();
			boolean isCurrentDateExist = false;
			while(rs.next()) {
				isCurrentDateExist = true;
			}
			rs.close();
			pst.close();
			
			if(isCurrentDateExist) {
				pst = con.prepareStatement("delete from emp_salary_details where effective_date = ? and emp_id = ?");
	//			pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
				pst.setDate	(1, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setInt(2, nEmpId);
				pst.execute();
				pst.close();
			}
			
			
	//		for(int i=0; i<emp_salary_id.length; i++) {
			for(int i=0; i<getSalary_head_id().length; i++) {
//				System.out.println("EA/3152---salaryHeadId="+getSalary_head_id()[i]+"---Amont="+getSalary_head_value()[i]);
				String isDiplaySalaryHead = (String) request.getParameter("isDisplay_"+getSalary_head_id()[i]);
				
	//				pst = con.prepareStatement(insertEmpSalaryDetails);
				pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id, salary_head_id, amount, entry_date, user_id, pay_type, isdisplay, " +
					"service_id, effective_date, earning_deduction, salary_type, is_approved,level_id) " +
					"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
				pst.setInt(1, nEmpId);
				pst.setInt(2, uF.parseToInt(getSalary_head_id()[i]));
				pst.setDouble(3, uF.parseToDouble(getSalary_head_value()[i]));
				pst.setDate	(4, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
				pst.setInt(5, 1);
				pst.setString(6, "M");
	//				pst.setBoolean(7, ArrayUtils.contains(hideIsDisplay, emp_salary_id[i])>=0);
				pst.setBoolean(7, uF.parseToBoolean(isDiplaySalaryHead));
				pst.setInt(8, 0);
				pst.setDate(9, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setString(10, hmEarningDeductionMap.get(getSalary_head_id()[i]));
				pst.setString(11, hmSalaryTypeMap.get(getSalary_head_id()[i]));
				pst.setBoolean(12, true);
				pst.setInt(13, uF.parseToInt(levelId));
	//			System.out.println("isDiplaySalaryHead==>"+isDiplaySalaryHead+"--pst insertEmpSalaryDetails==>"+pst);
				pst.execute();
				pst.close();
			}
			
			
			if(uF.parseToBoolean(getStrApplyArrear())) {
				CalculateAndGetArrearAmount cagaAmt = new CalculateAndGetArrearAmount();
				cagaAmt.request = request;
				cagaAmt.session = session;
				cagaAmt.CF = CF;
				cagaAmt.setEmpId(""+nEmpId);
				cagaAmt.setEffectiveDate(getEffectiveDate());
				cagaAmt.setSalary_head_id(getSalary_head_id());
				cagaAmt.setSalary_head_value(getSalary_head_value());
				cagaAmt.setCallFrom("EMPACTIVITY");
				if(activityType == uF.parseToInt(ACTIVITY_INCREMENT_ID)) {
					cagaAmt.setStrArearName("Increment");
				} else if(activityType == uF.parseToInt(ACTIVITY_LIFE_EVENT_ID)) {
					cagaAmt.setStrArearName("Life Event Increment");
				} else if(activityType == uF.parseToInt(ACTIVITY_CONFIRMATION_ID)) {
					cagaAmt.setStrArearName("Confirmation");
				}
				cagaAmt.viewCalculatedArrearAmount(uF);
			}
			
			
			CF.updateNextEmpSalaryEffectiveDate(con, uF, nEmpId, getEffectiveDate(), DATE_FORMAT);
					
			pst = con.prepareStatement("update employee_official_details set is_disable_sal_calculate=? where emp_id=?");
			pst.setBoolean(1, getDisableSalaryStructure());
			pst.setInt(2, nEmpId);
			pst.execute();
			pst.close();
			
			/**
			 * Calaculate CTC
			 * */
			Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, null, ""+nEmpId);
			
			MyProfile myProfile = new MyProfile();
			myProfile.session = session;
			myProfile.request = request;
			myProfile.CF = CF;
			myProfile.getSalaryHeadsforEmployee(con, uF, nEmpId, hmEmpProfile);
			
			double grossAmount = 0.0d;
			double grossYearAmount = 0.0d;
			double deductAmount = 0.0d;
			double deductYearAmount = 0.0d;
			double netAmount = 0.0d;
			double netYearAmount = 0.0d;			
			List<List<String>> salaryHeadDetailsList = (List<List<String>>) request.getAttribute("salaryHeadDetailsList");
			for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
				List<String> innerList = salaryHeadDetailsList.get(i);
				if(innerList.get(1).equals("E")) {
					grossAmount +=uF.parseToDouble(innerList.get(2));
					grossYearAmount +=uF.parseToDouble(innerList.get(3));
				} else if(innerList.get(1).equals("D")) {
					double dblDeductMonth = 0.0d;
					double dblDeductAnnual = 0.0d;
					if(uF.parseToInt(innerList.get(4)) == EMPLOYEE_ESI) {
						dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
						dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
					} else if(uF.parseToInt(innerList.get(4)) == EMPLOYER_ESI) {
						dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
						dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
					} else {
						dblDeductMonth += Math.round(uF.parseToDouble(innerList.get(2)));
						dblDeductAnnual += Math.round(uF.parseToDouble(innerList.get(3)));
					}
					deductAmount += dblDeductMonth;
					deductYearAmount += dblDeductAnnual;
				}
			}
			
			Map<String,String> hmContribution = (Map<String,String>) request.getAttribute("hmContribution");
			if(hmContribution == null) hmContribution = new HashMap<String, String>();
			double dblMonthContri = 0.0d;
			double dblAnnualContri = 0.0d;
			boolean isEPF = uF.parseToBoolean((String)request.getAttribute("isEPF"));
			boolean isESIC = uF.parseToBoolean((String)request.getAttribute("isESIC"));
			boolean isLWF = uF.parseToBoolean((String)request.getAttribute("isLWF"));
			if(isEPF || isESIC || isLWF) {
				if(isEPF) {
					double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
					double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
					dblMonthContri += dblEPFMonth;
					dblAnnualContri += dblEPFAnnual;
				}
				if(isESIC) {
					double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
					double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
					dblMonthContri += dblESIMonth;
					dblAnnualContri += dblESIAnnual;
				}
				if(isLWF) {
					double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
					double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
					dblMonthContri += dblLWFMonth;
					dblAnnualContri += dblLWFAnnual;
				}
			}
			
			double dblCTCMonthly = grossAmount + dblMonthContri;
			double dblCTCAnnualy = grossYearAmount + dblAnnualContri;
			
			List<List<String>> salaryAnnualVariableDetailsList = (List<List<String>>)request.getAttribute("salaryAnnualVariableDetailsList");
			if(salaryAnnualVariableDetailsList == null) salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
			int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
			if(nAnnualVariSize > 0) {
				double grossAnnualAmount = 0.0d;
				double grossAnnualYearAmount = 0.0d;
				for(int i = 0; i < nAnnualVariSize; i++) {
					List<String> innerList = salaryAnnualVariableDetailsList.get(i);
					double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
					double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
					grossAnnualAmount += dblEarnMonth;
					grossAnnualYearAmount += dblEarnAnnual;
				}
				dblCTCMonthly += grossAnnualAmount;
				dblCTCAnnualy += grossAnnualYearAmount;
			}
			
			netAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCMonthly));							 
			netYearAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCAnnualy));
			
			EmpSalaryApproval salaryApproval = new EmpSalaryApproval();
			salaryApproval.request = request;
			salaryApproval.session = session;
			salaryApproval.CF = CF;
			Map<String, String> hmPrevCTC = salaryApproval.getPrevCTCDetails(con, uF, ""+nEmpId);
			
			if(hmPrevCTC == null) hmPrevCTC = new HashMap<String, String>();
			double dblIncrementMonthAmt = netAmount - uF.parseToDouble(hmPrevCTC.get("PREV_MONTH_CTC"));
			double dblIncrementAnnualAmt = netYearAmount - uF.parseToDouble(hmPrevCTC.get("PREV_ANNUAL_CTC"));
	        
			pst = con.prepareStatement("update employee_official_details set month_ctc=?,annual_ctc=?,prev_month_ctc=?," +
					"prev_annual_ctc=?,incre_month_amount=?,incre_annual_amount=? where emp_id=?");
			pst.setDouble(1, netAmount);
			pst.setDouble(2, netYearAmount);
			pst.setDouble(3, uF.parseToDouble(hmPrevCTC.get("PREV_MONTH_CTC")));
			pst.setDouble(4, uF.parseToDouble(hmPrevCTC.get("PREV_ANNUAL_CTC")));
			pst.setDouble(5, dblIncrementMonthAmt);
			pst.setDouble(6, dblIncrementAnnualAmt);
			pst.setInt(7, nEmpId);
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
		
	}


	private void processConfirmation(Connection con, int activityType, int documentType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
				pst = con.prepareStatement("update employee_personal_details set emp_status=? where emp_per_id=?");
				pst.setString(1, PERMANENT);
				pst.setInt(2, nEmpId);
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
		int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
		if(nSalaryStrucuterType == S_GRADE_WISE) {
			updateEmployeeSalaryDetailsByGrade(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
		} else {
			updateEmployeeSalaryDetails(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
		}
		sendAttachDocument(con, activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	
	}

	private void processTransfer(Connection con, int activityType,int documentType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
			Date effectDate = uF.getDateFormat(getEffectiveDate(), DATE_FORMAT);
			int dateResult = effectDate.compareTo(currDate);
			
			if(dateResult < 1) {
				int departHod = 0;
				pst = con.prepareStatement("select emp_id from employee_official_details where depart_id=? and is_hod=true limit 1");
				pst.setInt(1, uF.parseToInt(getStrDepartment()));
				rs = pst.executeQuery();
				while(rs.next()) {
					departHod = rs.getInt("emp_id");
				}
				rs.close();
				pst.close();
				
				StringBuilder sb = new StringBuilder();
				sb.append("UPDATE employee_official_details SET emp_id = "+nEmpId+"");
				if(uF.parseToInt(getStrDepartment()) > 0) {
					sb.append(", depart_id= "+uF.parseToInt(getStrDepartment())+"");
				}
				if(uF.parseToInt(getStrSBU()) > 0) {
					sb.append(", service_id= ',"+uF.parseToInt(getStrSBU())+",'");
				}
				if(uF.parseToInt(getStrWLocation()) > 0) {
					sb.append(", wlocation_id= "+uF.parseToInt(getStrWLocation())+" ");
				}
				if(uF.parseToInt(getStrGrade()) > 0) {
					sb.append(", grade_id= "+uF.parseToInt(getStrGrade())+" ");
				}
				if(uF.parseToInt(getStrOrganisation()) > 0) {
					sb.append(", org_id= "+uF.parseToInt(getStrOrganisation())+" ");
				}
				if(departHod > 0) {
					sb.append(", hod_emp_id= "+departHod+" ");
				}
				if(getEmploymentType() != null) {
					sb.append(", emptype= '"+getEmploymentType()+"' ");
				}
				sb.append(" WHERE emp_id=?");
				pst = con.prepareStatement(sb.toString());
				pst.setInt(1, nEmpId);
				pst.execute();
				pst.close();
				
				if(uF.parseToInt(getStrWLocation()) > 0 && getStrTransferType() !=null && getStrTransferType().trim().equalsIgnoreCase("WL")) {
					String strDomain = request.getServerName().split("\\.")[0];
					AssignLeaveCron leaveCron = new AssignLeaveCron();
					leaveCron.request = request;
					leaveCron.session = session;
					leaveCron.CF = CF;
					leaveCron.strDomain = strDomain;
					leaveCron.strWlocationId = getStrWLocation();
					leaveCron.strEmpId = ""+nEmpId;
					leaveCron.setCronData();
				} else if(uF.parseToInt(getStrLevel()) > 0 && getStrTransferType() !=null && getStrTransferType().trim().equalsIgnoreCase("LE")) {
					String strDomain = request.getServerName().split("\\.")[0];
					AssignLeaveCron leaveCron = new AssignLeaveCron();
					leaveCron.request = request;
					leaveCron.session = session;
					leaveCron.CF = CF;
					leaveCron.strDomain = strDomain;
					leaveCron.strLevelId = getStrLevel();
					leaveCron.strEmpId = ""+nEmpId;
					leaveCron.setCronData();
				}
				
				sendAttachDocument(con, activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
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

	private void processPromotion(Connection con, int activityType,int documentType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
		if(nSalaryStrucuterType == S_GRADE_WISE) {
			insertEmpSalaryDetailsByGrade(con, activityType, nEmpId, uF, strActivityName, nEmpActivityId);
		} else {
			insertEmpSalaryDetails(con, activityType, nEmpId, uF, strActivityName, nEmpActivityId);
		}
		sendAttachDocument(con, activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}

	
	private void processDemotion(Connection con, int activityType, int documentType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
		if(nSalaryStrucuterType == S_GRADE_WISE) {
			insertEmpSalaryDetailsByGrade(con, activityType, nEmpId, uF, strActivityName, nEmpActivityId);
		} else {
			insertEmpSalaryDetails(con, activityType, nEmpId, uF, strActivityName, nEmpActivityId);
		}
		sendAttachDocument(con, activityType,documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}
	
	
	private void insertEmpSalaryDetails(Connection con, int activityType, int nEmpId, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select * from salary_details where level_id=?");
			pst.setInt(1, uF.parseToInt(getStrLevel()));
			rs = pst.executeQuery();
			Map<String, String> hmEarningDeductionMap = new HashMap<String, String>();
			Map<String, String> hmSalaryTypeMap = new HashMap<String, String>();
			while(rs.next()) {
				hmEarningDeductionMap.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
				hmSalaryTypeMap.put(rs.getString("salary_head_id"), rs.getString("salary_type"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from emp_salary_details where effective_date=? and emp_id=?");
//			pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setDate	(1, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			pst.setInt(2, nEmpId);
			rs = pst.executeQuery();
			boolean isCurrentDateExist = false;
			while(rs.next()) {
				isCurrentDateExist = true;
			}
			rs.close();
			pst.close();
			
			if(isCurrentDateExist) {
				pst = con.prepareStatement("delete from emp_salary_details where effective_date=? and emp_id=?");
//				pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
				pst.setDate	(1, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setInt(2, nEmpId);
				pst.execute();
				pst.close();
			}
			
			pst = con.prepareStatement("UPDATE employee_official_details SET grade_id=? where emp_id=?");
			pst.setInt(1, ((getEmpGrade()!=null && getEmpGrade().length()>0) ? uF.parseToInt(getEmpGrade()) : uF.parseToInt(getStrGrade())));
			pst.setInt(2, nEmpId);
			pst.execute();
			pst.close();
			
			for(int i=0; i<getSalary_head_id().length; i++) {
				
				String isDiplaySalaryHead = (String) request.getParameter("isDisplay_"+getSalary_head_id()[i]); 
				
//				pst = con.prepareStatement(insertEmpSalaryDetails);
				pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id, salary_head_id, " +
						"amount, entry_date, user_id, pay_type, isdisplay, service_id, effective_date, " +
						"earning_deduction, salary_type, is_approved,level_id) " +
						"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
				pst.setInt(1, nEmpId);
				pst.setInt(2, uF.parseToInt(getSalary_head_id()[i]));
				pst.setDouble(3, uF.parseToDouble(getSalary_head_value()[i]));
				pst.setDate	(4, uF.getDateFormat(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT), DATE_FORMAT));
				pst.setInt(5, 1);
				pst.setString(6, "M");
				pst.setBoolean(7, uF.parseToBoolean(isDiplaySalaryHead)); 
				pst.setInt(8, 0); 
				pst.setDate	(9, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setString(10, hmEarningDeductionMap.get(getSalary_head_id()[i]));
				pst.setString(11, hmSalaryTypeMap.get(getSalary_head_id()[i]));
				pst.setBoolean(12, true);
				pst.setInt(13, uF.parseToInt(getStrLevel()));
//				System.out.println("pst ==>"+pst);
				pst.execute();
				pst.close();
			}
			
			CF.updateNextEmpSalaryEffectiveDate(con, uF, nEmpId, getEffectiveDate(), DATE_FORMAT);
			
			pst = con.prepareStatement("update employee_official_details set is_disable_sal_calculate=? where emp_id=?");
			pst.setBoolean(1, getDisableSalaryStructure());
			pst.setInt(2, nEmpId);
			pst.execute();
			pst.close();
			
			int ServiceNo = uF.parseToInt((String)session.getAttribute("ServicesLinkNo"));
//			session.setAttribute("ServicesLinkNo", (ServiceNo-1)+"");    // Uncomment this code if you wish to use salary cost center wise.
			session.setAttribute("ServicesLinkNo", 1+"");
			
			
			/**
			 * Calaculate CTC
			 * */
			Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, null, ""+nEmpId);
			
			MyProfile myProfile = new MyProfile();
			myProfile.session = session;
			myProfile.request = request;
			myProfile.CF = CF;
			myProfile.getSalaryHeadsforEmployee(con, uF, nEmpId, hmEmpProfile);
			
			double grossAmount = 0.0d;
			double grossYearAmount = 0.0d;
			double deductAmount = 0.0d;
			double deductYearAmount = 0.0d;
			double netAmount = 0.0d;
			double netYearAmount = 0.0d;			
			List<List<String>> salaryHeadDetailsList = (List<List<String>>) request.getAttribute("salaryHeadDetailsList");
			for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
				List<String> innerList = salaryHeadDetailsList.get(i);
				if(innerList.get(1).equals("E")) {
					grossAmount +=uF.parseToDouble(innerList.get(2));
					grossYearAmount +=uF.parseToDouble(innerList.get(3));
				} else if(innerList.get(1).equals("D")) {
					double dblDeductMonth = 0.0d;
					double dblDeductAnnual = 0.0d;
					if(uF.parseToInt(innerList.get(4)) == EMPLOYEE_ESI) {
						dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
						dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
					} else if(uF.parseToInt(innerList.get(4)) == EMPLOYER_ESI) {
						dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
						dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
					} else {
						dblDeductMonth += Math.round(uF.parseToDouble(innerList.get(2)));
						dblDeductAnnual += Math.round(uF.parseToDouble(innerList.get(3)));
					}
					deductAmount += dblDeductMonth;
					deductYearAmount += dblDeductAnnual;
				}
			}
			
			Map<String,String> hmContribution = (Map<String,String>) request.getAttribute("hmContribution");
			if(hmContribution == null) hmContribution = new HashMap<String, String>();
			double dblMonthContri = 0.0d;
			double dblAnnualContri = 0.0d;
			boolean isEPF = uF.parseToBoolean((String)request.getAttribute("isEPF"));
			boolean isESIC = uF.parseToBoolean((String)request.getAttribute("isESIC"));
			boolean isLWF = uF.parseToBoolean((String)request.getAttribute("isLWF"));
			if(isEPF || isESIC || isLWF) {
				if(isEPF) {
					double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
					double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
					dblMonthContri += dblEPFMonth;
					dblAnnualContri += dblEPFAnnual;
				}
				if(isESIC) {
					double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
					double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
					dblMonthContri += dblESIMonth;
					dblAnnualContri += dblESIAnnual;
				}
				if(isLWF) {
					double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
					double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
					dblMonthContri += dblLWFMonth;
					dblAnnualContri += dblLWFAnnual;
				}
			}
			
			double dblCTCMonthly = grossAmount + dblMonthContri;
			double dblCTCAnnualy = grossYearAmount + dblAnnualContri;
			
			List<List<String>> salaryAnnualVariableDetailsList = (List<List<String>>)request.getAttribute("salaryAnnualVariableDetailsList");
			if(salaryAnnualVariableDetailsList == null) salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
			int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
			if(nAnnualVariSize > 0) {
				double grossAnnualAmount = 0.0d;
				double grossAnnualYearAmount = 0.0d;
				for(int i = 0; i < nAnnualVariSize; i++) {
					List<String> innerList = salaryAnnualVariableDetailsList.get(i);
					double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
					double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
					grossAnnualAmount += dblEarnMonth;
					grossAnnualYearAmount += dblEarnAnnual;
				}
				dblCTCMonthly += grossAnnualAmount;
				dblCTCAnnualy += grossAnnualYearAmount;
			}
			
			netAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCMonthly));							 
			netYearAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCAnnualy));
			
			EmpSalaryApproval salaryApproval = new EmpSalaryApproval();
			salaryApproval.request = request;
			salaryApproval.session = session;
			salaryApproval.CF = CF;
			Map<String, String> hmPrevCTC = salaryApproval.getPrevCTCDetails(con, uF, ""+nEmpId);
			if(hmPrevCTC == null) hmPrevCTC = new HashMap<String, String>();
			
			double dblIncrementMonthAmt = netAmount - uF.parseToDouble(hmPrevCTC.get("PREV_MONTH_CTC"));
			double dblIncrementAnnualAmt = netYearAmount - uF.parseToDouble(hmPrevCTC.get("PREV_ANNUAL_CTC"));
            
			pst = con.prepareStatement("update employee_official_details set month_ctc=?,annual_ctc=?,prev_month_ctc=?," +
					"prev_annual_ctc=?,incre_month_amount=?,incre_annual_amount=? where emp_id=?");
			pst.setDouble(1, netAmount);
			pst.setDouble(2, netYearAmount);
			pst.setDouble(3, uF.parseToDouble(hmPrevCTC.get("PREV_MONTH_CTC")));
			pst.setDouble(4, uF.parseToDouble(hmPrevCTC.get("PREV_ANNUAL_CTC")));
			pst.setDouble(5, dblIncrementMonthAmt);
			pst.setDouble(6, dblIncrementAnnualAmt);
			pst.setInt(7, nEmpId);
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
	}

	private void insertEmpSalaryDetailsByGrade(Connection con, int activityType, int nEmpId, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			String strNewGrade = null;
			if(getStrActivity() !=null && (getStrActivity().trim().equalsIgnoreCase(ACTIVITY_PROMOTION_ID) || getStrActivity().trim().equalsIgnoreCase(ACTIVITY_DEMOTION_ID))) {
				strNewGrade = getEmpGrade();
			} else if(getStrActivity() !=null && getStrActivity().trim().equalsIgnoreCase(ACTIVITY_GRADE_CHANGE_ID)) {
				strNewGrade = getEmpChangeGrade(); 
			} 
			
			
			pst = con.prepareStatement("select * from salary_details where grade_id=?");
			pst.setInt(1, uF.parseToInt(strNewGrade));
			rs = pst.executeQuery();
			Map<String, String> hmEarningDeductionMap = new HashMap<String, String>();
			Map<String, String> hmSalaryTypeMap = new HashMap<String, String>();
			while(rs.next()) {
				hmEarningDeductionMap.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
				hmSalaryTypeMap.put(rs.getString("salary_head_id"), rs.getString("salary_type"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from emp_salary_details where effective_date = ? and emp_id = ?");
//			pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setDate	(1, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			pst.setInt(2, nEmpId);
			rs = pst.executeQuery();
			boolean isCurrentDateExist = false;
			while(rs.next()) {
				isCurrentDateExist = true;
			}
			rs.close();
			pst.close();
			
			if(isCurrentDateExist) {
				pst = con.prepareStatement("delete from emp_salary_details where effective_date = ? and emp_id = ?");
//				pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
				pst.setDate	(1, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setInt(2, nEmpId);
				pst.execute();
				pst.close();
			}
			
			pst = con.prepareStatement("UPDATE employee_official_details SET grade_id=? where emp_id=?");
			pst.setInt(1, uF.parseToInt(strNewGrade));
			pst.setInt(2, nEmpId);
			pst.execute();
			pst.close();
			
			for(int i=0; i<getSalary_head_id().length; i++) {
				
				String isDiplaySalaryHead = (String) request.getParameter("isDisplay_"+getSalary_head_id()[i]); 
				
//				pst = con.prepareStatement(insertEmpSalaryDetails);
				pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id, salary_head_id, " +
						"amount, entry_date, user_id, pay_type, isdisplay, service_id, effective_date, " +
						"earning_deduction, salary_type, is_approved,grade_id) " +
						"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
				pst.setInt(1, nEmpId);
				pst.setInt(2, uF.parseToInt(getSalary_head_id()[i]));
				pst.setDouble(3, uF.parseToDouble(getSalary_head_value()[i]));
				pst.setDate	(4, uF.getDateFormat(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT), DATE_FORMAT));
				pst.setInt(5, 1);
				pst.setString(6, "M");
				pst.setBoolean(7, uF.parseToBoolean(isDiplaySalaryHead)); 
				pst.setInt(8, 0); 
				pst.setDate	(9, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setString(10, hmEarningDeductionMap.get(getSalary_head_id()[i]));
				pst.setString(11, hmSalaryTypeMap.get(getSalary_head_id()[i]));
				pst.setBoolean(12, true);
				pst.setInt(13, uF.parseToInt(strNewGrade));
//				System.out.println("pst ==>"+pst);
				pst.execute();
				pst.close();
			}
			
			CF.updateNextEmpSalaryEffectiveDate(con, uF, nEmpId, getEffectiveDate(), DATE_FORMAT);
			
			pst = con.prepareStatement("update employee_official_details set is_disable_sal_calculate=? where emp_id = ?");
			pst.setBoolean(1, getDisableSalaryStructure());
			pst.setInt(2, nEmpId);
			pst.execute();
			pst.close();
			
			int ServiceNo = uF.parseToInt((String)session.getAttribute("ServicesLinkNo"));
//			session.setAttribute("ServicesLinkNo", (ServiceNo-1)+"");    // Uncomment this code if you wish to use salary cost center wise.
			session.setAttribute("ServicesLinkNo", 1+"");
			
			
			/**
			 * Calaculate CTC
			 * */
			Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, null, ""+nEmpId);
			
			MyProfile myProfile = new MyProfile();
			myProfile.session = session;
			myProfile.request = request;
			myProfile.CF = CF;
			myProfile.getSalaryHeadsforEmployeeByGrade(con, uF, nEmpId, hmEmpProfile);
			
			double grossAmount = 0.0d;
			double grossYearAmount = 0.0d;
			double deductAmount = 0.0d;
			double deductYearAmount = 0.0d;
			double netAmount = 0.0d;
			double netYearAmount = 0.0d;			
			List<List<String>> salaryHeadDetailsList = (List<List<String>>) request.getAttribute("salaryHeadDetailsList");
			for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
				List<String> innerList = salaryHeadDetailsList.get(i);
				if(innerList.get(1).equals("E")) {
					grossAmount +=uF.parseToDouble(innerList.get(2));
					grossYearAmount +=uF.parseToDouble(innerList.get(3));
				} else if(innerList.get(1).equals("D")) {
					double dblDeductMonth = 0.0d;
					double dblDeductAnnual = 0.0d;
					if(uF.parseToInt(innerList.get(4)) == EMPLOYEE_ESI) {
						dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
						dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
					} else if(uF.parseToInt(innerList.get(4)) == EMPLOYER_ESI) {
						dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
						dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
					} else {
						dblDeductMonth += Math.round(uF.parseToDouble(innerList.get(2)));
						dblDeductAnnual += Math.round(uF.parseToDouble(innerList.get(3)));
					}
					deductAmount += dblDeductMonth;
					deductYearAmount += dblDeductAnnual;
				}
			}
			
			Map<String,String> hmContribution = (Map<String,String>) request.getAttribute("hmContribution");
			if(hmContribution == null) hmContribution = new HashMap<String, String>();
			double dblMonthContri = 0.0d;
			double dblAnnualContri = 0.0d;
			boolean isEPF = uF.parseToBoolean((String)request.getAttribute("isEPF"));
			boolean isESIC = uF.parseToBoolean((String)request.getAttribute("isESIC"));
			boolean isLWF = uF.parseToBoolean((String)request.getAttribute("isLWF"));
			if(isEPF || isESIC || isLWF) {
				if(isEPF) {
					double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
					double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
					dblMonthContri += dblEPFMonth;
					dblAnnualContri += dblEPFAnnual;
				}
				if(isESIC) {
					double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
					double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
					dblMonthContri += dblESIMonth;
					dblAnnualContri += dblESIAnnual;
				}
				if(isLWF) {
					double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
					double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
					dblMonthContri += dblLWFMonth;
					dblAnnualContri += dblLWFAnnual;
				}
			}
			
			double dblCTCMonthly = grossAmount + dblMonthContri;
			double dblCTCAnnualy = grossYearAmount + dblAnnualContri;
			
			List<List<String>> salaryAnnualVariableDetailsList = (List<List<String>>)request.getAttribute("salaryAnnualVariableDetailsList");
			if(salaryAnnualVariableDetailsList == null) salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
			int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
			if(nAnnualVariSize > 0) {
				double grossAnnualAmount = 0.0d;
				double grossAnnualYearAmount = 0.0d;
				for(int i = 0; i < nAnnualVariSize; i++) {
					List<String> innerList = salaryAnnualVariableDetailsList.get(i);
					double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
					double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
					grossAnnualAmount += dblEarnMonth;
					grossAnnualYearAmount += dblEarnAnnual;
				}
				dblCTCMonthly += grossAnnualAmount;
				dblCTCAnnualy += grossAnnualYearAmount;
			}
			
			netAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCMonthly));							 
			netYearAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCAnnualy));
			
			EmpSalaryApproval salaryApproval = new EmpSalaryApproval();
			salaryApproval.request = request;
			salaryApproval.session = session;
			salaryApproval.CF = CF;
			Map<String, String> hmPrevCTC = salaryApproval.getPrevCTCDetailsByGrade(con, uF, ""+nEmpId);
			if(hmPrevCTC == null) hmPrevCTC = new HashMap<String, String>();
			
			double dblIncrementMonthAmt = netAmount - uF.parseToDouble(hmPrevCTC.get("PREV_MONTH_CTC"));
			double dblIncrementAnnualAmt = netYearAmount - uF.parseToDouble(hmPrevCTC.get("PREV_ANNUAL_CTC"));
            
			pst = con.prepareStatement("update employee_official_details set month_ctc=?,annual_ctc=?,prev_month_ctc=?," +
					"prev_annual_ctc=?,incre_month_amount=?,incre_annual_amount=? where emp_id=?");
			pst.setDouble(1, netAmount);
			pst.setDouble(2, netYearAmount);
			pst.setDouble(3, uF.parseToDouble(hmPrevCTC.get("PREV_MONTH_CTC")));
			pst.setDouble(4, uF.parseToDouble(hmPrevCTC.get("PREV_ANNUAL_CTC")));
			pst.setDouble(5, dblIncrementMonthAmt);
			pst.setDouble(6, dblIncrementAnnualAmt);
			pst.setInt(7, nEmpId);
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
	}
	
	private void processGradeChange(Connection con, int activityType, int documentType ,int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("UPDATE employee_official_details SET grade_id=? where emp_id=?");
			pst.setInt(1, ((getEmpChangeGrade()!=null && getEmpChangeGrade().length()>0) ? uF.parseToInt(getEmpChangeGrade()) : uF.parseToInt(getStrGrade())));
			pst.setInt(2, nEmpId);
			pst.execute();
			pst.close();
			
			int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
			if(nSalaryStrucuterType == S_GRADE_WISE) {
				insertEmpSalaryDetailsByGrade(con, activityType, nEmpId, uF, strActivityName, nEmpActivityId);
			}
			
			sendAttachDocument(con, activityType,documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
			
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

	private void processTerminate(Connection con, int activityType, int documentType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
//			System.out.println("CF.getIsTerminateWithoutFullAndFinal()==>"+CF.getIsTerminateWithoutFullAndFinal());
			if(CF.getIsTerminateWithoutFullAndFinal()) {
				pst = con.prepareStatement("UPDATE user_details SET status=? where emp_id=?");
				pst.setString(1, "INACTIVE");
				pst.setInt(2, nEmpId);
//				System.out.println("pst1==>"+pst);
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("UPDATE employee_personal_details SET emp_status=?, is_alive=?, employment_end_date=? where emp_per_id=?");
				pst.setString(1, TERMINATED);
				pst.setBoolean(2, false);
				pst.setDate(3, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setInt(4, nEmpId);
//				System.out.println("pst2==>"+pst);
				int y = pst.executeUpdate();
				pst.close();
				
				if(y > 0) {
					pst = con.prepareStatement("delete from leave_register1 where emp_id=? and _date>?");
					pst.setInt(1, nEmpId);
					pst.setDate(2, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
					pst.executeUpdate();
					pst.close();
				}
				
			} else{
				
				Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
				Date effectDate = uF.getDateFormat(getEffectiveDate(), DATE_FORMAT);
				int dateResult = effectDate.compareTo(currDate);
				
				if(dateResult < 1) {
					pst = con.prepareStatement(updateUserDetailsStatus);
					pst.setString(1, TERMINATED);
					pst.setInt(2, nEmpId);
					pst.execute();
					pst.close();
					
					pst = con.prepareStatement("UPDATE employee_personal_details SET emp_status=?, employment_end_date=? where emp_per_id=?");
					pst.setString(1, TERMINATED);
					pst.setDate(2, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
					pst.setInt(3, nEmpId);
					int y = pst.executeUpdate();
					pst.close();
					
					if(y > 0) {
						pst = con.prepareStatement("delete from leave_register1 where emp_id=? and _date>?");
						pst.setInt(1, nEmpId);
						pst.setDate(2, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
						pst.executeUpdate();
						pst.close();
					}
					
					if(strUserType!=null && strUserType.equals(ADMIN)) {
						pst = con.prepareStatement("insert into emp_off_board (emp_id, off_board_type, emp_reason, entry_date, notice_days, last_day_date,approved_1_by,approved_2_by, approved_1, approved_2) values (?,?,?,?,?,?,?,?,?,?)");
						pst.setInt(1, nEmpId);
						pst.setString(2, IConstants.TERMINATED);
						pst.setString(3, "Direct termination");
						pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(5, 0);
						pst.setDate(6,  uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
						pst.setInt(7,uF.parseToInt(strSessionEmpId));
						pst.setInt(8, uF.parseToInt(strSessionEmpId));
						pst.setInt(9, 1);
						pst.setInt(10, 1);
					} else {
						pst = con.prepareStatement("insert into emp_off_board (emp_id, off_board_type, emp_reason, entry_date, notice_days, last_day_date,approved_1_by, approved_1, approved_2) values (?,?,?,?,?,?,?,?)");
						pst.setInt(1, nEmpId);
						pst.setString(2, IConstants.TERMINATED);
						pst.setString(3, "Direct termination");
						pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(5, 0);
						pst.setDate(6,  uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
						pst.setInt(7,uF.parseToInt(strSessionEmpId));
						pst.setInt(8, 1);
						pst.setInt(9, 1);
					}
					int x = pst.executeUpdate();
					pst.close();
					
					if(x > 0) {
						pst = con.prepareStatement("select max(off_board_id) as off_board_id from emp_off_board");
						rs = pst.executeQuery();
						int nOffBoardId = 0;
						while(rs.next()) {
							nOffBoardId = uF.parseToInt(rs.getString("off_board_id"));
						}
						
						rs.close();
						pst.close();
						
						if(nOffBoardId > 0) {
							String policy_id=null;
							int empId = 0;
							int userTypeId = 0;
							Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
							String empLevelId=hmEmpLevelMap.get(""+nEmpId);
							Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
							String locationID=hmEmpWlocationMap.get(""+nEmpId);
							
//							Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
						
							pst = con.prepareStatement("select policy_id from work_flow_policy_details where type='"+WORK_FLOW_TERMINATION+"' and level_id=? and wlocation_id=?");
							pst.setInt(1, uF.parseToInt(empLevelId));
							pst.setInt(2, uF.parseToInt(locationID));
	//						System.out.println("pst====>"+pst);
							rs = pst.executeQuery();
							while(rs.next()) {
								policy_id=rs.getString("policy_id");
							}
							rs.close();
							pst.close();
							
							if(uF.parseToInt(policy_id) == 0) {
								pst = con.prepareStatement("select policy_count from work_flow_member wfm,work_flow_policy wfp where wfp.group_id=wfm.group_id " +
										"and wfp.work_flow_member_id=wfm.work_flow_member_id and wfm.wlocation_id=? and wfm.is_default = true");
								pst.setInt(1, uF.parseToInt(locationID));
								rs = pst.executeQuery();
								while(rs.next()) {
									policy_id=rs.getString("policy_count");
								}
								rs.close();
								pst.close();
							}
	//						System.out.println("policyId == >"+ policy_id);
							if(uF.parseToInt(policy_id) > 0) {
								pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
								" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
								pst.setInt(1,uF.parseToInt(policy_id));
	//							System.out.println("pst 1==>"+pst);
								rs=pst.executeQuery();
								Map<String,List<String>> hmMemberMap=new LinkedHashMap<String, List<String>>();
								while(rs.next()) {
									List<String> innerList=new ArrayList<String>();
									innerList.add(rs.getString("member_type"));
									innerList.add(rs.getString("member_id"));
									innerList.add(rs.getString("member_position"));
									innerList.add(rs.getString("work_flow_mem"));
									innerList.add(rs.getString("work_flow_member_id"));
									
									hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
								}
								rs.close();
								pst.close();
						
								String strDomain = request.getServerName().split("\\.")[0];
								Map<String,String> hmMemberOption=new LinkedHashMap<String,String>();
								Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
								if(hmUserTypeIdMap==null) hmUserTypeIdMap = new HashMap<String, String>();
								Iterator<String> it=hmMemberMap.keySet().iterator();
								
								while(it.hasNext()) {
									String work_flow_member_id=it.next();
									List<String> innerList=hmMemberMap.get(work_flow_member_id);
									
									if(uF.parseToInt(innerList.get(0))==1) {
										int memid=uF.parseToInt(innerList.get(1));
										switch(memid) {
											case 1:
													pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud,"
																	+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=1 "
																	+ " and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and eod.emp_id=epd.emp_per_id and ud.status='ACTIVE'" 
																	+ " and ud.emp_id not in(?) and epd.is_alive=true limit 1");
													pst.setInt(1, nEmpId);
													rs = pst.executeQuery();
													
													while (rs.next()) {
														empId = rs.getInt("emp_id");
														userTypeId = rs.getInt("usertype_id");
													
													}
													rs.close();
													pst.close();
													
													pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
															"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
															"values(?,?,?,?, ?,?,?,?, ?,?)");
													pst.setInt(1,empId);
													pst.setInt(2,nOffBoardId);
													pst.setString(3,WORK_FLOW_TERMINATION);
													pst.setInt(4,uF.parseToInt(innerList.get(0)));
													pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
													pst.setInt(6,uF.parseToInt(innerList.get(4)));
													pst.setInt(7,1);
													pst.setInt(8,1);
													pst.setInt(9,userTypeId);
													pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
													pst.execute();
													pst.close();
													break;
												
											case 2:
													pst = con.prepareStatement("select * from (select supervisor_emp_id from employee_official_details where emp_id= ? and supervisor_emp_id!=0) as a," +
															"employee_personal_details epd,user_details ud where a.supervisor_emp_id=epd.emp_per_id and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" +
															" and epd.is_alive=true limit 1");
													pst.setInt(1, nEmpId);
													rs = pst.executeQuery();
												
													while (rs.next()) {
														empId = rs.getInt("emp_id");
														userTypeId = rs.getInt("usertype_id");
																			
													}
													rs.close();
													pst.close();
													
													pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
															"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
															"values(?,?,?,?, ?,?,?,?, ?,?)");
													pst.setInt(1,empId);
													pst.setInt(2,nOffBoardId);
													pst.setString(3,WORK_FLOW_TERMINATION);
													pst.setInt(4,uF.parseToInt(innerList.get(0)));
													pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
													pst.setInt(6,uF.parseToInt(innerList.get(4)));
													pst.setInt(7,1);
													pst.setInt(8,1);
													pst.setInt(9,userTypeId);
													pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
													pst.execute();
													pst.close();
													break;
												
											case 3:
													pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud,"
																	+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=3 "
																	+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and eod.emp_id = epd.emp_per_id " 
																	+" and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true limit 1");
													pst.setInt(1, uF.parseToInt(locationID));
													pst.setInt(2, nEmpId);
													rs = pst.executeQuery();
													while (rs.next()) {
														empId = rs.getInt("emp_id");
														userTypeId = rs.getInt("usertype_id");
													
													}
													rs.close();
													pst.close();
													
													pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
															"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
															"values(?,?,?,?, ?,?,?,?, ?,?)");
													pst.setInt(1,empId);
													pst.setInt(2,nOffBoardId);
													pst.setString(3,WORK_FLOW_TERMINATION);
													pst.setInt(4,uF.parseToInt(innerList.get(0)));
													pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
													pst.setInt(6,uF.parseToInt(innerList.get(4)));
													pst.setInt(7,1);
													pst.setInt(8,1);
													pst.setInt(9,userTypeId);
													pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
													pst.execute();
													pst.close();
													break;
											
											case 4:
													pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud,"
															+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=4 "
															+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id "
															+"  and eod.emp_id=epd.emp_per_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true limit 1");
													
													pst.setInt(1, nEmpId);
													rs = pst.executeQuery();
													while (rs.next()) {
														empId = rs.getInt("emp_id");
														userTypeId = rs.getInt("usertype_id");
																		
													}
													rs.close();
													pst.close();
													
													pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
															"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
															"values(?,?,?,?, ?,?,?,?, ?,?)");
													pst.setInt(1,empId);
													pst.setInt(2,nOffBoardId);
													pst.setString(3,WORK_FLOW_TERMINATION);
													pst.setInt(4,uF.parseToInt(innerList.get(0)));
													pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
													pst.setInt(6,uF.parseToInt(innerList.get(4)));
													pst.setInt(7,1);
													pst.setInt(8,1);
													pst.setInt(9,userTypeId);
													pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
													pst.execute();
													pst.close();
													break;
											
											case 5:
													pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud,"
															+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=5 "
															+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id "
															+ " and eod.emp_id=epd.emp_per_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true limit 1");
													
													pst.setInt(1, nEmpId);
													rs = pst.executeQuery();
													
													while (rs.next()) {
														empId = rs.getInt("emp_id");
														userTypeId = rs.getInt("usertype_id");
																				
													}
													rs.close();
													pst.close();
													
													pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
															"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
															"values(?,?,?,?, ?,?,?,?, ?,?)");
													pst.setInt(1,empId);
													pst.setInt(2,nOffBoardId);
													pst.setString(3,WORK_FLOW_TERMINATION);
													pst.setInt(4,uF.parseToInt(innerList.get(0)));
													pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
													pst.setInt(6,uF.parseToInt(innerList.get(4)));
													pst.setInt(7,1);
													pst.setInt(8,1);
													pst.setInt(9,userTypeId);
													pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
													pst.execute();
													pst.close();
													break;
												
											case 6:
													pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud,"
															+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=6 "
															+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id "
															+ " and eod.emp_id=epd.emp_per_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true limit 1");
													pst.setInt(1, uF.parseToInt(locationID));
													pst.setInt(2, nEmpId);
													rs = pst.executeQuery();
													
													while (rs.next()) {
														empId = rs.getInt("emp_id");
														userTypeId = rs.getInt("usertype_id");
																				
													}
													rs.close();
													pst.close();
													pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
															"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
															"values(?,?,?,?, ?,?,?,?, ?,?)");
													pst.setInt(1,empId);
													pst.setInt(2,nOffBoardId);
													pst.setString(3,WORK_FLOW_TERMINATION);
													pst.setInt(4,uF.parseToInt(innerList.get(0)));
													pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
													pst.setInt(6,uF.parseToInt(innerList.get(4)));
													pst.setInt(7,1);
													pst.setInt(8,1);
													pst.setInt(9,userTypeId);
													pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
													pst.execute();
													pst.close();
													break;
												
											case 7:
												pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud, employee_official_details eod," +
														"employee_personal_details epd where ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id " +
														"and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true and ud.emp_id in (select eod.emp_hr from employee_official_details eod," +
														"employee_personal_details epd where epd.emp_per_id=eod.emp_id and eod.emp_id=?)" +
														" union " +
														"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud, employee_official_details eod," +
														"employee_personal_details epd where ud.usertype_id=7 and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' " +
														"and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true " +
														" union " +
														"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud, employee_official_details eod," +
														"employee_personal_details epd where ud.usertype_id=1 and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and " +
														"epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true limit 1");
													pst.setInt(1, nEmpId);
													pst.setInt(2, nEmpId);
													pst.setInt(3, nEmpId);
													pst.setInt(4, nEmpId);
													rs = pst.executeQuery();
													while (rs.next()) {
														empId = rs.getInt("emp_id");
														userTypeId = rs.getInt("usertype_id");
																				
													}
													rs.close();
													pst.close();
													
													pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
															"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
															"values(?,?,?,?, ?,?,?,?, ?,?)");
													pst.setInt(1,empId);
													pst.setInt(2,nOffBoardId);
													pst.setString(3,WORK_FLOW_TERMINATION);
													pst.setInt(4,uF.parseToInt(innerList.get(0)));
													pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
													pst.setInt(6,uF.parseToInt(innerList.get(4)));
													pst.setInt(7,1);
													pst.setInt(8,1);
													pst.setInt(9,userTypeId);
													pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
													pst.execute();
													pst.close();
													break;		
												
											case 13:
												pst = con.prepareStatement("select * from (select distinct(hod_emp_id) as hod_emp_id from employee_official_details where " +
														"emp_id=? and hod_emp_id!=0) as a,employee_personal_details epd,user_details ud where a.hod_emp_id=epd.emp_per_id " +
														"and ud.emp_id=epd.emp_per_id  and ud.status='ACTIVE' and epd.is_alive=true order by epd.emp_fname limit 1");
												pst.setInt(1, nEmpId);
												rs = pst.executeQuery();
												
												while (rs.next()) {
													empId = rs.getInt("emp_id");
													userTypeId = rs.getInt("usertype_id");
																				
												}
												rs.close();
												pst.close();
												
												pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
														"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
														"values(?,?,?,?, ?,?,?,?, ?,?)");
												pst.setInt(1,empId);
												pst.setInt(2,nOffBoardId);
												pst.setString(3,WORK_FLOW_TERMINATION);
												pst.setInt(4,uF.parseToInt(innerList.get(0)));
												pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
												pst.setInt(6,uF.parseToInt(innerList.get(4)));
												pst.setInt(7,1);
												pst.setInt(8,1);
												pst.setInt(9,userTypeId);
												pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
												pst.execute();
												pst.close();
												break;
											
										}
										
										/*UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
										userAlerts.setStrDomain(strDomain);
										userAlerts.setStrEmpId(""+empId);
										userAlerts.set_type(EMP_TERMINATED_ALERT);
										userAlerts.setStatus(INSERT_ALERT);
										Thread t = new Thread(userAlerts);
										t.run();*/
									}
								}
							}
							
						}
						sendAttachDocument(con, activityType,documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
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
	}

	private void processWithdrawnResignation(Connection con, int activityType,int documentType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		PreparedStatement pst = null;
		try {
			
			Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
			Date effectDate = uF.getDateFormat(getEffectiveDate(), DATE_FORMAT);
			int dateResult = effectDate.compareTo(currDate);
			if(dateResult < 1) {
				pst = con.prepareStatement(updateUserDetailsStatus);
				pst.setString(1, "ACTIVE");
				pst.setInt(2, nEmpId);
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement(updateUserStatus2);
				pst.setString(1, PERMANENT);
				pst.setBoolean(2, true);
				pst.setDate(3, null);
				pst.setInt(4, nEmpId);
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("delete from emp_off_board where emp_id=?");
				pst.setInt(1, nEmpId);
				pst.execute();
				pst.close();
			
				sendAttachDocument(con, activityType,documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
			}
			
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

	private void processFullAndFinal(Connection con, int activityType,int documentType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		
	}

	private void processNoticePeriod(Connection con, int activityType, int documentType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			pst = con.prepareStatement("update probation_policy set notice_duration = ? where emp_id=?");
			pst.setInt(1, uF.parseToInt(getStrNoticePeriod()));
			pst.setInt(2, nEmpId);
			int x = pst.executeUpdate();
			pst.close();
			
			if(x==0) {
				pst = con.prepareStatement("insert into probation_policy (notice_duration, emp_id) values (?,?)");
				pst.setInt(1, uF.parseToInt(getStrNoticePeriod()));
				pst.setInt(2, nEmpId);
				pst.execute();
				pst.close();
			}
			
			String resignationDate = null;
			pst = con.prepareStatement("select * from emp_off_board where emp_id = ?");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			while(rs.next()) {
				resignationDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT);
			}
			rs.close();
			pst.close();
			
			if(resignationDate != null && !resignationDate.equals("")) {
				java.sql.Date lastDate = uF.getFutureDate(resignationDate, uF.parseToInt(getStrNoticePeriod()));
				
				pst = con.prepareStatement("update emp_off_board set notice_days = ?, last_day_date = ? where emp_id = ?");
				pst.setInt(1, uF.parseToInt(getStrNoticePeriod()));
				pst.setDate(2,lastDate);
				pst.setInt(3, nEmpId);
//				System.out.println("pst==>"+pst);
				pst.executeUpdate();
				pst.close();
			}
			sendAttachDocument(con, activityType,documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
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

	private void processPermanent(Connection con, int activityType, int documentType,int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		PreparedStatement pst = null;
		
		try {
			
			pst = con.prepareStatement(updateUserStatus);
			pst.setString(1, IConstants.PERMANENT);
			pst.setInt(2, nEmpId);
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0) {
				String[] leaveTypeId = (String[]) request.getParameterValues("leaveTypeId");
				for(int i = 0; leaveTypeId != null && i < leaveTypeId.length; i++) {
					String strLeaveBalance = (String) request.getParameter("leaveBal_"+leaveTypeId[i]);
					
				//===start parvez date: 19-11-2021===	
//					pst=con.prepareStatement("delete from leave_register1 where emp_id=? and _date=? and leave_type_id=? and _type=?");
					pst=con.prepareStatement("delete from leave_register1 where emp_id=? and _date>=? and leave_type_id=? and _type=?");
				//===end parvez date: 19-11-2021===
					pst.setInt(1, nEmpId);
					pst.setDate(2, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(leaveTypeId[i]));
					pst.setString(4, "C");
					pst.execute();
					pst.close();
					
					pst=con.prepareStatement("insert into leave_register1(emp_id,_date,balance,leave_type_id,_type)values(?,?,?,?,?)");
					pst.setInt(1, nEmpId);
					pst.setDate(2, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
					pst.setDouble(3, uF.parseToDouble(strLeaveBalance));
					pst.setInt(4, uF.parseToInt(leaveTypeId[i]));
					pst.setString(5, "C");
					pst.execute();
					pst.close();
				}
			}
			sendAttachDocument(con, activityType,documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
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

	private void processTemporary(Connection con, int activityType, int documentType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {

		PreparedStatement pst = null;
		
		try {
			
			pst = con.prepareStatement(updateUserStatus);
			pst.setString(1, IConstants.TEMPORARY);
			pst.setInt(2, nEmpId);
			pst.execute();
			pst.close();
			
			sendAttachDocument(con, activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
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

	public void processUserStatus(Connection con, int activityType, int nEmpId) {
		
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			pst = con.prepareStatement(updateUserStatus);
			if(activityType==uF.parseToInt(ACTIVITY_RESIGNATION_WITHDRWAL_ID)) {
				pst.setString(1,  PERMANENT);
			} else {
				pst.setString(1, RESIGNED);
			}
			pst.setInt(2, nEmpId);
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
	
	private void processIncrement(Connection con, int activityType, int documentType,  int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
		if(nSalaryStrucuterType == S_GRADE_WISE) {
			updateEmployeeSalaryDetailsByGrade(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
		} else {
			updateEmployeeSalaryDetails(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
		}
		sendAttachDocument(con, activityType, documentType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}
	
	
	public String insertEmpActivity(UtilityFunctions uF, int nActivityId, String strEmpId) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from employee_activity_details where emp_id = ? order by emp_activity_id desc limit 1");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			int  wlocation_id=0;
			int  department_id=0;
			int  level_id=0;
			int  desig_id=0;
			int  grade_id=0;
			int  emp_id=0;
			String  emp_status_code=null;
			int  period=0;
			
			while(rs.next()) {
				wlocation_id = rs.getInt("wlocation_id");
				department_id = rs.getInt("department_id");
				level_id = rs.getInt("level_id");
				desig_id = rs.getInt("desig_id");
				grade_id = rs.getInt("grade_id");
				emp_id = rs.getInt("emp_id");
				emp_status_code = rs.getString("emp_status_code");
				period = rs.getInt("period");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement(insertEmpActivity);

			pst.setInt(1, wlocation_id);
			pst.setInt(2, department_id);
			pst.setInt(3, level_id);
			pst.setInt(4, desig_id);
			pst.setInt(5, grade_id);
			pst.setString(6, emp_status_code);
			pst.setInt(7, nActivityId);   
			pst.setString(8, "");
			pst.setDate(9, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(11, uF.parseToInt((String)session.getAttribute(USERID)));
			pst.setInt(12, emp_id);
			pst.setInt(13, period);
			
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	
	private void saveDocumentActivity(Connection con, UtilityFunctions uF,CommonFunctions CF, String strDocumentName, String strDocumentHeader, String strDocumentContent, String strDocumentFooter, String strMailSubject, String strMailBody, int nEmpActivityId) {
		PreparedStatement pst = null;
		try {
		
			pst = con.prepareStatement("insert into document_activities (document_name, document_content, effective_date, entry_date, user_id, emp_id, " +
					"mail_subject, mail_body, document_header, document_footer,emp_activity_id) values (?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setString(1, strDocumentName);
			pst.setString(2, strDocumentContent);
			pst.setDate(3, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(5, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(6, uF.parseToInt(getStrEmpId2()));
			pst.setString(7, strMailSubject);
			pst.setString(8, strMailBody);
			pst.setString(9, strDocumentHeader);
			pst.setString(10, strDocumentFooter);
			pst.setInt(11, nEmpActivityId);
			pst.executeUpdate();
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

	
	public void updateEmployeeOfficialDetails() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
				
			pst = con.prepareStatement(updateEmployeeO1);
			pst.setInt(1, uF.parseToInt(getStrDepartment()));
			pst.setInt(2, uF.parseToInt(getStrWLocation()));
			pst.setString(3, getStrNewStatus());
			pst.setInt(4, ((getEmpGrade()!=null && getEmpGrade().length()>0)?uF.parseToInt(getEmpGrade()):uF.parseToInt(getStrGrade())));
			pst.setInt(5, uF.parseToInt(getStrEmpId2()));
			pst.execute();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public HttpServletRequest request;
	private HttpServletResponse response;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
		
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public String getStrActivity() {
		return strActivity;
	}
	public void setStrActivity(String strActivity) {
		this.strActivity = strActivity;
	}
	
	public String getStrOrganisation() {
		return strOrganisation;
	}

	public void setStrOrganisation(String strOrganisation) {
		this.strOrganisation = strOrganisation;
	}

	public String getStrWLocation() {
		return strWLocation;
	}
	public void setStrWLocation(String strWLocation) {
		this.strWLocation = strWLocation;
	}
	
	public String getStrSBU() {
		return strSBU;
	}

	public void setStrSBU(String strSBU) {
		this.strSBU = strSBU;
	}

	public String getStrDepartment() {
		return strDepartment;
	}
	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}
	public String getStrLevel() {
		return strLevel;
	}
	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}
	public String getStrDesignation() {
		return strDesignation;
	}
	public void setStrDesignation(String strDesignation) {
		this.strDesignation = strDesignation;
	}
	public String getStrGrade() {
		return strGrade;
	}
	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}
	public String getStrNewStatus() {
		return strNewStatus;
	}
	public void setStrNewStatus(String strNewStatus) {
		this.strNewStatus = strNewStatus;
	}
	public String getStrReason() {
		return strReason;
	}
	public void setStrReason(String strReason) {
		this.strReason = strReason;
	}
	public List<FillLevel> getLevelList() {
		return levelList;
	}
	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}
	public List<FillGrade> getGradeList() {
		return gradeList;
	}
	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}
	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}
	
	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}
	
	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public List<FillEmpStatus> getEmpStatusList() {
		return empStatusList;
	}
	public List<FillActivity> getActivityList() {
		return activityList;
	}
	public void setActivityList(List<FillActivity> activityList) {
		this.activityList = activityList;
	}
	
	public String getStrEmpId() {
		return strEmpId;
	}
	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}
	public List<FillEmployee> getEmpList() {
		return empList;
	}
	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}
	
	public String getStrEmpId2() {
		return strEmpId2;
	}
	public void setStrEmpId2(String strEmpId2) {
		this.strEmpId2 = strEmpId2;
	}
	public String getStrJoiningDate() {
		return strJoiningDate;
	}
	public void setStrJoiningDate(String strJoiningDate) {
		this.strJoiningDate = strJoiningDate;
	}
	public boolean getEmailNotification() {
		return emailNotification;
	}
	public void setEmailNotification(boolean emailNotification) {
		this.emailNotification = emailNotification;
	}
	public String getStrUpdate() {
		return strUpdate;
	}
	public void setStrUpdate(String strUpdate) {
		this.strUpdate = strUpdate;
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
	public String getF_strWLocation() {
		return f_strWLocation;
	}
	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}
	public String getF_department() {
		return f_department;
	}
	public void setF_department(String f_department) {
		this.f_department = f_department;
	}
	public String getF_level() {
		return f_level;
	}
	public void setF_level(String f_level) {
		this.f_level = f_level;
	}
	public String getStrNoticePeriod() {
		return strNoticePeriod;
	}
	public void setStrNoticePeriod(String strNoticePeriod) {
		this.strNoticePeriod = strNoticePeriod;
	}
	public String getStrProbationPeriod() {
		return strProbationPeriod;
	}
	public void setStrProbationPeriod(String strProbationPeriod) {
		this.strProbationPeriod = strProbationPeriod;
	}

	public String getStrExtendProbationDays() {
		return strExtendProbationDays;
	}

	public void setStrExtendProbationDays(String strExtendProbationDays) {
		this.strExtendProbationDays = strExtendProbationDays;
	}

	public String getStrIncrementType() {
		return strIncrementType;
	}

	public void setStrIncrementType(String strIncrementType) {
		this.strIncrementType = strIncrementType;
	}

	public String getStrIncrementPercentage() {
		return strIncrementPercentage;
	}

	public void setStrIncrementPercentage(String strIncrementPercentage) {
		this.strIncrementPercentage = strIncrementPercentage;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public List<FillOrganisation> getOrganisationList1() {
		return organisationList1;
	}

	public void setOrganisationList1(List<FillOrganisation> organisationList1) {
		this.organisationList1 = organisationList1;
	}

	public List<FillWLocation> getwLocationList1() {
		return wLocationList1;
	}

	public void setwLocationList1(List<FillWLocation> wLocationList1) {
		this.wLocationList1 = wLocationList1;
	}

	public List<FillDepartment> getDepartmentList1() {
		return departmentList1;
	}

	public void setDepartmentList1(List<FillDepartment> departmentList1) {
		this.departmentList1 = departmentList1;
	}

	public List<FillServices> getServiceList1() {
		return serviceList1;
	}

	public void setServiceList1(List<FillServices> serviceList1) {
		this.serviceList1 = serviceList1;
	}

	public List<FillLevel> getLevelList1() {
		return levelList1;
	}

	public void setLevelList1(List<FillLevel> levelList1) {
		this.levelList1 = levelList1;
	}

	public String getAppraisal_id() {
		return appraisal_id;
	}

	public void setAppraisal_id(String appraisal_id) {
		this.appraisal_id = appraisal_id;
	}

	public List<List<String>> getAl() {
		return al;
	}

	public void setAl(List<List<String>> al) {
		this.al = al;
	}

	public String[] getSalary_head_id() {
		return salary_head_id;
	}

	public void setSalary_head_id(String[] salary_head_id) {
		this.salary_head_id = salary_head_id;
	}

	public String[] getSalary_head_value() {
		return salary_head_value;
	}

	public void setSalary_head_value(String[] salary_head_value) {
		this.salary_head_value = salary_head_value;
	}

	public String[] getIsDisplay() {
		return isDisplay;
	}

	public void setIsDisplay(String[] isDisplay) {
		this.isDisplay = isDisplay;
	}

	public String[] getEmp_salary_id() {
		return emp_salary_id;
	}

	public void setEmp_salary_id(String[] emp_salary_id) {
		this.emp_salary_id = emp_salary_id;
	}

	public String[] getHideIsDisplay() {
		return hideIsDisplay;
	}

	public void setHideIsDisplay(String[] hideIsDisplay) {
		this.hideIsDisplay = hideIsDisplay;
	}

	public List<FillGrade> getGradeChangeList() {
		return gradeChangeList;
	}

	public void setGradeChangeList(List<FillGrade> gradeChangeList) {
		this.gradeChangeList = gradeChangeList;
	}

	public String getEmpChangeGrade() {
		return empChangeGrade;
	}

	public void setEmpChangeGrade(String empChangeGrade) {
		this.empChangeGrade = empChangeGrade;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getStrUpdateDocument() {
		return strUpdateDocument;
	}

	public void setStrUpdateDocument(String strUpdateDocument) {
		this.strUpdateDocument = strUpdateDocument;
	}

	public String getStrTransferType() {
		return strTransferType;
	}

	public void setStrTransferType(String strTransferType) {
		this.strTransferType = strTransferType;
	}

	public String getLearningId() {
		return learningId;
	}

	public void setLearningId(String learningId) {
		this.learningId = learningId;
	}

	public String getTrainingId() {
		return trainingId;
	}

	public void setTrainingId(String trainingId) {
		this.trainingId = trainingId;
	}

	public String getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(String assessmentId) {
		this.assessmentId = assessmentId;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public void setEmpStatusList(List<FillEmpStatus> empStatusList) {
		this.empStatusList = empStatusList;
	}

	public String getEmpType() {
		return empType;
	}

	public void setEmpType(String empType) {
		this.empType = empType;
	}

	public String getProPage() {
		return proPage;
	}

	public void setProPage(String proPage) {
		this.proPage = proPage;
	}

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}

	public String getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}
	
	public boolean getDisableSalaryStructure() {
		return disableSalaryStructure;
	}

	public void setDisableSalaryStructure(boolean disableSalaryStructure) {
		this.disableSalaryStructure = disableSalaryStructure;
	}

	public String getStrApplyArrear() {
		return strApplyArrear;
	}

	public void setStrApplyArrear(String strApplyArrear) {
		this.strApplyArrear = strApplyArrear;
	}

	public List<FillEmploymentType> getEmpTypeList() {
		return empTypeList;
	}


	public void setEmpTypeList(List<FillEmploymentType> empTypeList) {
		this.empTypeList = empTypeList;
	}

	public String getEmploymentType() {
		return employmentType;
	}

	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}

	public List<FillDocument> getDocumentList() {
		return documentList;
	}

	public void setDocumentList(List<FillDocument> documentList) {
		this.documentList = documentList;
	}
	
	public String getStrDocumentName() {
		return strDocumentName;
	}

	public void setStrDocumentName(String strDocumentName) {
		this.strDocumentName = strDocumentName;
	}

	public String getStrIncumbentEmpId() {
		return strIncumbentEmpId;
	}

	public void setStrIncumbentEmpId(String strIncumbentEmpId) {
		this.strIncumbentEmpId = strIncumbentEmpId;
	}

	public String getStrReviewIds() {
		return strReviewIds;
	}

	public void setStrReviewIds(String strReviewIds) {
		this.strReviewIds = strReviewIds;
	}

	public String getStrLearningIds() {
		return strLearningIds;
	}

	public void setStrLearningIds(String strLearningIds) {
		this.strLearningIds = strLearningIds;
	}

//===start parvez date: 29-06-2022===
	public File getStrDocumentFile() {
		return strDocumentFile;
	}

	public void setStrDocumentFile(File strDocumentFile) {
		this.strDocumentFile = strDocumentFile;
	}

	public String getStrDocumentFileFileName() {
		return strDocumentFileFileName;
	}

	public void setStrDocumentFileFileName(String strDocumentFileFileName) {
		this.strDocumentFileFileName = strDocumentFileFileName;
	}

//===end parvez date: 29-06-2022===	
}
