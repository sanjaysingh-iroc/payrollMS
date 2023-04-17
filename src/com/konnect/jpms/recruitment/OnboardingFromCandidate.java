package com.konnect.jpms.recruitment;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.employee.AddEmployee;
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
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CandidateNotifications;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class OnboardingFromCandidate extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
//	String strUserType = null;
//	String strSessionEmpId = null; 
	private String mode;
	private int empId;
	String operation;
	Boolean autoGenerate = false; 
	private int step; 
	private String serviceId;
	private String ServiceName;
	HttpSession session;
	String username;
	String password;
    CommonFunctions CF=null;

	
	StringBuilder sbServicesLink = new StringBuilder();
	CommonFunctions cF = null;
	
	public static String MOTHER = "MOTHER";
	public static String FATHER = "FATHER";
	public static String SPOUSE = "SPOUSE";
	public static String SIBLING = "SIBLING";
	public static String CHILD = "CHILD";
	public String dobYear="1977";

	private static Logger log = Logger.getLogger(AddEmployee.class);
	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);

		cF = new CommonFunctions();
		cF.setRequest(request);
//		strTimezone = "Asia/Calcutta";
//		session.setAttribute(O_TIME_ZONE, strTimezone);
		cF.setStrTimeZone("Asia/Calcutta");
//		if (CF==null){
//			CF = new CommonFunctions();
//		}
		
		
//		strUserType = (String)session.getAttribute(USERTYPE);
		
//		strSessionEmpId = (String)session.getAttribute(EMPID);
		request.setAttribute("dobYear",dobYear);
		request.setAttribute(PAGE, "/jsp/recruitment/OnboardingFromCandidate.jsp");
		request.setAttribute(TITLE, "Edit Employee");
		Connection con=null;
		Database db=new Database();
		db.setRequest(request);
		con=db.makeConnection(con);
		try {
			
//			System.out.println("getMode() ===> "+getMode());
		if (getOperation()!=null && getOperation().equals("U") && getEmpId()!=0 && getMode()!=null) {
//			System.out.println("step1");
			request.setAttribute(TITLE, TEditEmployee);
//			System.out.println("1");
			viewEmployee(con);
			loadValidateEmployee(con);
//			log.debug("???====>"+getMode()+"getAutoGenerate()==>"+getAutoGenerate());
			return SUCCESS;
			
		} else if (getEmpId()!=0 && getMode()!=null && (getMode().equals("profile") || getMode().equals("report") || getMode().equals("onboard"))) {
			
			updateEmployee(con);
//			System.out.println("2");
			if(getStep()==1) {
				setStep(2);
			}else if(getStep()==2) {
				setStep(3);
			}else if(getStep()==3) {
				setStep(4);
			}else if(getStep()==4) {
				setStep(5);
			}else if(getStep()==5) {
				setStep(6);
			}else if(getStep()==6) {
				setStep(7);
			}else if(getStep()==7) {
// 					generateEmpCode(con);
//					updateEmpFilledStatus(con,getEmpId());
					setStep(11);
//				
			}else if(getStep()==8) {
				setStep(9);
			}else if(getStep()==11) {
//				insertAvailability(con,uF, getEmpId()+"");
//				setStep(10);
				getUserLoginDetails(con, getEmpId());
				return "EMPLOGIN";
			}
			viewEmployee(con);
			loadValidateEmployee(con);
			return SUCCESS;
		}
		
		return SUCCESS;
	} finally {  
		db.closeConnection(con);
	}	
	}
	
	
	private void getUserLoginDetails(Connection con, int empId) {
		
		PreparedStatement pst = null;
		ResultSet rst = null;
	
		try {
			pst = con.prepareStatement("select username, password from user_details where emp_id = ?");
			pst.setInt(1, empId);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("Login pst ===> " + pst);
			while (rst.next()) {
				setUsername(rst.getString("username"));
				setPassword(rst.getString("password"));
			}
			rst.close();
			pst.close();
			
			sendMail(empId);
//			System.out.println("Login Username ===> " + getUsername());
//			System.out.println("Login Password ===> " + getPassword());
			
		} catch (Exception e) {
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
	}


	public void sendMail(int empId) {

		Connection con = null;
		ResultSet rst = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {
//			System.out.println("empId 1111 ====> 0 "+empId);
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmEmpName = cF.getEmpInfoMap(con, false);
			Map<String, String> hmEmpInner = hmEmpName.get(""+empId);
//				Map<String,String> hmDepartment= cF.getDepartmentMap(con,null,null);
//				System.out.println("hmEmpInner ===> " + hmEmpInner);
			String strDomain = request.getServerName().split("\\.")[0];
			Notifications nF = new Notifications(N_EMP_LOGIN_DETAILS, cF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrEmpId(""+empId);
//			System.out.println("empId ===> " + empId);
//			System.out.println("CF.getStrEmailLocalHost() is ========= "+CF.getStrEmailLocalHost());
//			System.out.println("request.getContextPath() is ========= "+request.getContextPath());
			 nF.setStrHostAddress(cF.getStrEmailLocalHost());
			 nF.setStrHostPort(cF.getStrHostPort());
			 nF.setStrContextPath(request.getContextPath());
			 
			 nF.setStrEmpFname(hmEmpInner.get("FNAME"));
			 nF.setStrEmpLname(hmEmpInner.get("LNAME"));
			 nF.setStrCandiUsername(getUsername());
			 nF.setStrCandiPassword(getPassword());
//			 nF.setStrRecruitmentDesignation(hmCandiDesig.get(""+getEmpId()));
//			 nF.setOnboardingData("?depart_id="+hmDepartment.get(strSessionEmpId)+"&candidateId="+getCandidateID()+"&recruitId="+getRecruitID());
			 nF.setEmailTemplate(true);
			 nF.sendNotifications();
//			}
			 
			 List<String> hrIDList = null; 
			 pst = con.prepareStatement("select emp_id from user_details where usertype_id=7");
				rst = pst.executeQuery();
//				System.out.println("new Date ===> " + new Date());
				hrIDList = new ArrayList<String>();
				while (rst.next()) {
					hrIDList.add(rst.getString(1));
				}
				rst.close();
				pst.close();
				
				Map<String, Map<String, String>> hmEmpInfo = cF.getEmpInfoMap(con, false);
				
				for (int i = 0; hrIDList != null && i < hrIDList.size(); i++) {
//					System.out.println("hrIDList.get(i) ===> "+hrIDList.get(i));
					Map<String, String> hmEmpInfoInner = hmEmpInfo.get(hrIDList.get(i));
//					System.out.println("hmEmpInner ===> "+hmEmpInner);
			 	nF = new Notifications(N_EMP_ONBOARDING_TO_HR, cF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrEmpId(""+empId);
//				System.out.println("empId ===> " + empId);
//				System.out.println("CF.getStrEmailLocalHost() is ========= "+CF.getStrEmailLocalHost());
//				System.out.println("request.getContextPath() is ========= "+request.getContextPath());
				 nF.setStrHostAddress(cF.getStrEmailLocalHost());
				 nF.setStrHostPort(cF.getStrHostPort());
				 nF.setStrContextPath(request.getContextPath());
				 
				 nF.setStrEmpFname(hmEmpInfoInner.get("FNAME"));
				 nF.setStrEmpLname(hmEmpInfoInner.get("LNAME"));
				 nF.setStrCandiFname(hmEmpInner.get("FNAME"));
				 nF.setStrCandiLname(hmEmpInner.get("LNAME"));
				 nF.setStrCandiUsername(getUsername());
				 nF.setStrCandiPassword(getPassword());
				 nF.setEmailTemplate(true);
				 nF.sendNotifications();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	
	public void getEmpMiscInfo(Connection con,UtilityFunctions uF, String strEmpId){
		
//		Database db = new Database();
		PreparedStatement pst = null;
		ResultSet rst = null;
//		Connection con = null;
		
		try {
			
//			con = db.makeConnection(con);
			pst = con.prepareStatement("select added_by, emp_fname, emp_lname from employee_personal_details where emp_per_id =?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			int nAddedBy = 0;
			while(rst.next()){
				request.setAttribute("EMP_FNAME", rst.getString("emp_fname"));
				nAddedBy = uF.parseToInt(rst.getString("added_by"));
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select emp_email, emp_email_sec from employee_personal_details where emp_per_id =?");
			pst.setInt(1, nAddedBy);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				if(rst.getString("emp_email_sec")!=null && rst.getString("emp_email_sec").length()>0){
					request.setAttribute("ADDED_BY_EMAIL", rst.getString("emp_email_sec"));
				}else{
					request.setAttribute("ADDED_BY_EMAIL", rst.getString("emp_email"));
				}
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
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
	}
	
	
	String []strTime;
	String []strDate;

	public boolean insertAvailability(Connection con,UtilityFunctions uF, String strEmpId){
		
		PreparedStatement pst = null;
		boolean isValidSesseion = false;
		try {
			
//			con = db.makeConnection(con);
			
			
			
			for(int i=0; getStrDate()!=null && i<getStrDate().length; i++){
				
				if(getStrDate()[i]!=null && getStrDate()[i].length()>0){
					pst = con.prepareStatement("insert into emp_interview_availability (emp_id, ip_address, _timestamp, _date, _time) values (?,?,?,?,?)");		
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setString(2, "");
					pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(cF.getStrTimeZone())+""+uF.getCurrentTime(cF.getStrTimeZone()), DBDATE+DBTIME));
					pst.setDate(4, uF.getDateFormat(getStrDate()[i], DATE_FORMAT));
					pst.setTime(5, uF.getTimeFormat(getStrTime()[i], TIME_FORMAT));
					pst.execute();
					pst.close();
				}
				
			}
			
			
			pst = con.prepareStatement("update employee_personal_details set session_id =? where emp_per_id=?");
			pst.setString(1, "");
			pst.setInt(2, empId);
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
		return isValidSesseion;
	}
	
	
	public boolean validateSession(Connection con,UtilityFunctions uF){
		
//		Database db = new Database();
//		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean isValidSesseion = false;
		try {
//			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from employee_personal_details where emp_per_id=?");
			pst.setInt(1, uF.parseToInt(request.getParameter("empId")));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rs.next()){
				if(rs.getString("session_id")!=null && rs.getString("session_id").equals((String)request.getParameter("sessionId"))){
					isValidSesseion = true;	
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
		
		return isValidSesseion;
	}
	
	
	void insertEmpActivity(Connection con,int nActivityId, String strEmpId, CommonFunctions CF, UtilityFunctions uF) {
		
		
//		Connection con = null;
		ResultSet rst = null;
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
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			
//			log.debug(strEmpId+" getEmpId()===>"+getEmpId()+" pst===>"+pst);
			while(rst.next()){
				nWLocation = rst.getInt("wlocation_id");
				nDepartment = rst.getInt("department_id");
				nLevel = rst.getInt("level_id");
				nDesignation = rst.getInt("desig_id");
				nGrade = rst.getInt("grade_id");
				nActivity = rst.getInt("activity_id");
				nNoticePeriod = rst.getInt("notice_period");
				nProbationPeriod = rst.getInt("probation_period");
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select joining_date from employee_personal_details where emp_per_id=?");
			pst.setInt(1,  uF.parseToInt(strEmpId));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String strJoiningDt = null;
			while(rst.next()){
				strJoiningDt = rst.getString("joining_date");
			}
			rst.close();
			pst.close();
			
//			System.out.println("========== 2 ============");
			String strReason = "";

			pst = con.prepareStatement(insertEmpActivity);
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
			pst.setInt(11, uF.parseToInt("2"));
			pst.setInt(12, uF.parseToInt(strEmpId));
			pst.setInt(13, nNoticePeriod);
			pst.setInt(14, nProbationPeriod);
			pst.execute();
			pst.close();
//			log.debug("pst==>"+pst);  
			
			
		}catch(Exception e) {   
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
		
	}
	
	public void updateEmpLiveStatus(Connection con,int nEmpId) {
		
//		Connection con = null;
		PreparedStatement pst = null;
//		Database db = new Database();
		
		try {
			
//			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE employee_personal_details SET is_alive = ?, approved_flag = ? " +
					"WHERE emp_per_id = ?");
			pst.setBoolean(1, true);
			pst.setBoolean(2, true);
			pst.setInt(3, nEmpId);
//			log.debug("pst===>"+pst);
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
	
	
	public String loadValidateEmployee(Connection con) {
		
		try {
			FillEmployee fillEmployee=new FillEmployee(request);
			UtilityFunctions uF = new UtilityFunctions();
			
			salutationList = new FillSalutation(request).fillSalutation();
			wLocationList = new FillWLocation(request).fillWLocation(getOrgId());
			orgList = new FillOrganisation(request).fillOrganisation();
			bankList = new FillBank(request).fillBankDetails();
			gradeList = new FillGrade(request).fillGrade();
			desigList = new FillDesig(request).fillDesig(uF.parseToInt(getOrgId()));
			levelList = new FillLevel(request).fillLevel(uF.parseToInt(getOrgId()));
			deptList = new FillDepartment(request).fillDepartment(uF.parseToInt(getOrgId()));
//			supervisorList = new FillEmployee().fillEmployeeCode(strUserType, strSessionEmpId);
			supervisorList = fillEmployee.fillSupervisorNameCode(getEmpId(), getOrgId(), getDepartment()); 
			HodList = fillEmployee.fillHODNameCode(""+getEmpId(), getOrgId(), uF.parseToInt(getwLocation()), cF);
		
			serviceList = new FillServices(request).fillServices(getOrgId(), uF);
			countryList = new FillCountry(request).fillCountry();
			stateList = new FillState(request).fillState();
			
			rosterDependencyList = new FillApproval().fillYesNo();
			empTypeList = new FillEmploymentType().fillEmploymentType(request);
			paymentModeList = new FillPayMode().fillPaymentMode();
			empGenderList = new FillGender().fillGender();
			probationDurationList = new FillProbationDuration().fillProbationDuration();
			noticeDurationList = new FillNoticeDuration().fillNoticeDuration();
			if (getOrgId() != null && !getOrgId().equals("0")) {
				leaveTypeList = new FillLeaveType(request).fillLeave(uF.parseToInt(getOrgId()));
			}else{
				leaveTypeList = new FillLeaveType(request).fillLeave();
			}
			if(getwLocation()!= null && !getwLocation().equals("0")){
				HRList=fillEmployee.fillEmployeeNameHR(""+getEmpId(), 0, uF.parseToInt(getwLocation()),cF,uF); 

			}else if (getOrgId() != null && !getOrgId().equals("0")) {
				HRList=fillEmployee.fillEmployeeNameHR(""+getEmpId(), uF.parseToInt(getOrgId()), 0,cF,uF); 
			}else{
				HRList=fillEmployee.fillEmployeeNameHR(""+getEmpId(), 0, 0,cF,uF); 
			}
			maritalStatusList = new FillMaritalStatus().fillMaritalStatus();
			bloodGroupList = new FillBloodGroup().fillBloodGroup();
			degreeDurationList = new FillDegreeDuration().fillDegreeDuration();
			if(getEmpDateOfBirth()!=null){
//				System.out.println(""+getEmpDateOfBirth());
				yearsList = new FillYears().fillYears(uF.getCurrentDate(cF.getStrTimeZone()),uF.getDateFormat(getEmpDateOfBirth(), DATE_FORMAT, "yyyy"));
			}/*else if (dobYear!=null){
				yearsList = new FillYears().fillYears(uF.getCurrentDate(cF.getStrTimeZone()),""+dobYear);
			}*/else{
				yearsList = new FillYears().fillYears(uF.getCurrentDate(cF.getStrTimeZone()));
			}
			skillsList = new FillSkills(request).fillSkillsWithId();
			educationalList = new FillEducational(request).fillEducationalQual();
			
			paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();
			
			
			request.setAttribute("yearsList", yearsList);
			request.setAttribute("degreeDurationList", degreeDurationList);
			request.setAttribute("empGenderList", empGenderList);
			request.setAttribute("currentYear", (uF.getDateFormat(uF.getCurrentDate(cF.getStrTimeZone()).toString(), DBDATE, "yyyy")));
			StringBuilder sbdegreeDuration = new StringBuilder();
			StringBuilder sbPrevEmployment = new StringBuilder();
			StringBuilder sbSibling = new StringBuilder();
			StringBuilder sbChildren = new StringBuilder();

			StringBuilder sbSkills = new StringBuilder();
			
			sbdegreeDuration.append("<tr><td><input type=text style=height:25px;width:110px; name=degreeName></input></td>" +
					"<td><select name=degreeDuration style=width:110px;>" +
					"<option value=0>Duration</option>");
				
			for (int i=0; i<degreeDurationList.size(); i++) {
				String ddID=String.valueOf(((FillDegreeDuration)degreeDurationList.get(i)).getDegreeDurationID());
				String ddName=((FillDegreeDuration)degreeDurationList.get(i)).getDegreeDurationName();
				sbdegreeDuration.append("<option value="+ddID+">"+ddName+"</option>");						
			}
					
			sbdegreeDuration.append("</select></td><td><select name=completionYear style=width:110px;>"+
					"<option value=>Completion Year</option>");
			
			for (int i=0; i<yearsList.size(); i++) {
				sbdegreeDuration.append("<option value="+((FillYears)yearsList.get(i)).getYearsID()+">"+((FillYears)yearsList.get(i)).getYearsName()+"</option>"); 
						     
			}
			
			sbdegreeDuration.append("</select></td><td><input type=text  style=height:25px;width:110px; name=grade ></input></td>" +
					"<td><a href=javascript:void(0); onclick=addEducation(); class=add >Add</a>");
			
			request.setAttribute("sbdegreeDuration", sbdegreeDuration.toString());
			
			
			sbPrevEmployment.append(
			"<table>"+
		 	"<tr><td class=txtlabel style=text-align:right> Company Name:</td><td><input type=text name=prevCompanyName style=height:25px;width:207px; name=prevCompanyLocation ></input></td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> Location:</td><td> <input type=text style=height:25px;width:207px; name=prevCompanyLocation ></input></td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> City: </td><td><input type=text style=height:25px;width:207px; name=prevCompanyCity ></input></td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> State:</td><td><input type=text style=height:25px;width:207px; name=prevCompanyState ></input></td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> Country:</td><td><input type=text style=height:25px;width:207px; name=prevCompanyCountry ></input></td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> Contact Number:</td><td><input type=text style=height:25px;width:207px; name=prevCompanyContactNo ></input>" +                
			"</td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> Reporting To:</td><td> <input type=text style=height:25px;width:207px; name=prevCompanyReportingTo ></input>" +                
			"</td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> Reporting Manager Phone Number:</td><td> <input type=text style=height:25px;width:180px; name=prevCompanyReportManagerPhNo ></input>" +
			"</td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> HR Manager:</td><td> <input type=text style=height:25px;width:180px; name=prevCompanyHRManager ></input>" +
			"</td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> HR Manager Phone Number:</td><td> <input type=text style=height:25px;width:180px; name=prevCompanyHRManagerPhNo ></input>" +
			"</td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> From:</td><td> <input type=text style=height:25px;width:207px; name=prevCompanyFromDate onchange=setcompanyTodd(this.value)></input></td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> To:</td><td> <input type=text style=height:25px;width:207px; name=prevCompanyToDate ></input></td></tr> " +
			"<tr><td class=txtlabel style=text-align:right> Designation:</td><td> <input type=text style=height:25px;width:207px; name=prevCompanyDesination ></input>" +                "</td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> Responsibility:</td><td> <input type=text style=height:25px;width:207px; name=prevCompanyResponsibilities >" +                "</input>" +  
			"</td></tr>" + 
			"<tr><td class=txtlabel style=text-align:right> Skills: </td><td> <input type=text style=height:25px;width:207px; name=prevCompanySkills ></input></td></tr>" + 
		//===start parvez date: 08-08-2022===
			"<tr><td class=txtlabel style=text-align:right> UAN No.: </td><td> <input type=text style=height:25px;width:207px; name=prevCompanyUANNo ></input></td></tr>" +
			"<tr><td class=txtlabel style=text-align:right> ESIC No.: </td><td> <input type=text style=height:25px;width:207px; name=prevCompanyESICNo ></input></td></tr>" +
		//===end parvez date: 08-08-2022===	
			
			"<tr><td></td><td class=txtlabel style=text-align:right><a href=javascript:void(0) onclick=addPrevEmployment() class=add></a>"  
	
			);
			
			request.setAttribute("sbPrevEmployment", sbPrevEmployment.toString());
			
			sbSibling.append(
				"<table>" +
		        "<tr><td style=text-align:center class=tdLabelheadingBg style=text-align:right colspan=2>Sibling\\'s Information </td></tr>" +    
		        "<tr><td class=txtlabel style=text-align:right>Name:</td><td><input type=text style=height:25px;width:207px; name=memberName ></input></td></tr>" + 
				"<tr><td class=txtlabel style=text-align:right>Date of birth:</td><td> <input type=text style=height:25px;width:207px; name=memberDob ></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Education:</td><td> <input type=text style=height:25px;width:207px; name=memberEducation ></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Occupation:</td><td> <input type=text style=height:25px;width:207px; name=memberOccupation ></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Contact Number:</td><td><input type=text style=height:25px;width:207px; name=memberContactNumber ></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Email Id:</td><td><input type=text style=height:25px;width:207px; name=memberEmailId ></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Gender:</td><td>"+
				"<select name= memberGender>");
			
			for (int i=0; i<empGenderList.size(); i++) {
				
				sbSibling.append("<option value="+((FillGender)empGenderList.get(i)).getGenderId()+"> "+((FillGender)empGenderList.get(i)).getGenderName()+"</option>"); 
						
			}
				
			sbSibling.append("</select>" +
					"</td></tr><tr><td class=txtlabel style=text-align:right>Marital Status:</td><td>"+
				"<select name= siblingMaritalStatus><option value=0>Select Marital Status</option>");
for (int i=0; i<maritalStatusList.size(); i++) {
				
	sbSibling.append("<option value="+((FillMaritalStatus)maritalStatusList.get(i)).getMaritalStatusId()+"> "+((FillMaritalStatus)maritalStatusList.get(i)).getMaritalStatusName()+"</option>"); 
						
			}
sbSibling.append("</select>" +
		"</td></tr>");
			sbSibling.append("<tr><td class=txtlabel style=text-align:right>&nbsp;</td><td><a href=javascript:void(0) onclick=addSibling() class=add>Add another..</a>" );
			
			request.setAttribute("sbSibling", sbSibling.toString());
			

			sbChildren.append(
				"<table>" +
		        "<tr><td style=text-align:center class=tdLabelheadingBg style=text-align:right colspan=2>Childern's Information </td></tr>" +    
		        "<tr><td class=txtlabel style=text-align:right>Name:</td><td><input type=text style=height:25px;width:207px; name=childName ></input></td></tr>" + 
				"<tr><td class=txtlabel style=text-align:right>Date of birth:</td><td> <input type=text style=height:25px;width:207px; name=childDob ></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Education:</td><td> <input type=text style=height:25px;width:207px; name=childEducation ></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Occupation:</td><td> <input type=text style=height:25px;width:207px; name=childOccupation ></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Contact Number:</td><td><input type=text style=height:25px;width:207px; name=childContactNumber ></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Email Id:</td><td><input type=text style=height:25px;width:207px; name=childEmailId ></input></td></tr>" +
				"<tr><td class=txtlabel style=text-align:right>Gender:</td><td>"+
				"<select name= childGender><option value=0>Select Gender</option>");
			
			for (int i=0; i<empGenderList.size(); i++) {
				
				sbChildren.append("<option value="+((FillGender)empGenderList.get(i)).getGenderId()+"> "+((FillGender)empGenderList.get(i)).getGenderName()+"</option>"); 
						
			}
				
			sbChildren.append("</select>" +
					"</td></tr><tr><td class=txtlabel style=text-align:right>Marital Status:</td><td>"+
				"<select name= childMaritalStatus><option value=0>Select Marital Status</option>");
for (int i=0; i<maritalStatusList.size(); i++) {
				
				sbChildren.append("<option value="+((FillMaritalStatus)maritalStatusList.get(i)).getMaritalStatusId()+"> "+((FillMaritalStatus)maritalStatusList.get(i)).getMaritalStatusName()+"</option>"); 
						
			}
sbChildren.append("</select>" +
		"</td></tr>");
			sbChildren.append("<tr><td class=txtlabel style=text-align:right>&nbsp;</td><td><a href=javascript:void(0) onclick=addChildren() class=add>Add another..</a>" );
			
			request.setAttribute("sbChildren", sbChildren.toString());
			
			sbSkills.append(" <table><tr><td>"+
	                        	"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<select name=skillName>" +
	                        	"<option value=>Select Skill Name</option>"
							);
			
			for(int k=0; k< skillsList.size(); k++) { 
				                		
				sbSkills.append("<option value="+((FillSkills)skillsList.get(k)).getSkillsId()+"> "+((FillSkills)skillsList.get(k)).getSkillsName()+"</option>");
			}
			
			sbSkills.append("</select></td><td>" +
	                        "<select name=skillValue>" +
					"<option value=>Select Skill Value</option>"
					);
			
			for(int k=1; k< 11; k++) {
				sbSkills.append("<option value="+k+">"+k+"</option>");
			}
			
			sbSkills.append("</select></td>");
//			log.debug("sbSkills==>"+sbSkills);
			request.setAttribute("sbSkills", sbSkills);
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return LOAD;
		
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
//					System.out.println("generateEmpCode empCodeNum ===>> " + empCodeNum);
					getLatestEmpCode(con, uF, empCodeAlpha, empCodeNum);
				}
				rs.close();
				pst.close();
				
				if(!flag) {
					System.out.println("empCodeNum 111 ===>> " + empCodeNum);
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

		UtilityFunctions uF = new UtilityFunctions();
		
		List<List<String>> alSkills = new ArrayList<List<String>>();
		List<List<String>> alHobbies;
		List<List<String>> alLanguages;
		List<List<String>> alEducation;
		List<List<Object>> alDocuments;
		List<List<String>> alPrevEmployment;
		
		try {

			setEmpPersonalDetails(con, uF);
			setEmpReferences(con, uF);
			setEmpOfficialDetails(con, uF);
			setUser(con, uF);
			setProbationPolicy(con, uF);
			
			alSkills = cF.selectSkills(con,getEmpId());
			alHobbies = cF.selectHobbies(con,getEmpId());
			alLanguages = cF.selectLanguages(con, uF, getEmpId());
			alEducation = cF.selectEducation(con,getEmpId());
			
			String filePath = request.getRealPath("/userDocuments/");
			
			alDocuments = cF.selectDocuments(con,getEmpId(), filePath);
			setEmpFamilyMembers(con, uF);
			alPrevEmployment = selectPrevEmploment(con,getEmpId());
			setEmpMedicalInfo(con,uF);
			
//			request.setAttribute("strEdit", strEdit);
//			setEmpPerId(strEdit);
			
			wLocationList = new FillWLocation(request).fillWLocation(getOrgId());
			bankList = new FillBank(request).fillBankDetails();
//			desigList = new FillDesig().fillDesig();
			gradeList = new FillGrade(request).fillGrade();
			deptList = new FillDepartment(request).fillDepartment(uF.parseToInt(getOrgId()));
//			supervisorList = new FillEmployee().fillEmployeeCode(null, null);
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
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return SUCCESS;

	}
	
	private void setEmpReferences(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst =null;
		ResultSet rst = null;
		
		try {
			
			pst = con.prepareStatement("SELECT * FROM emp_references WHERE emp_id = ? order by ref_name");
			pst.setInt(1, getEmpId());
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			if(rst.next()) {
			
				setRef1Name(rst.getString("ref_name"));
				setRef1Company(rst.getString("ref_company"));
				setRef1Designation(rst.getString("ref_designation"));
				setRef1ContactNo(rst.getString("ref_contact_no"));
				setRef1Email(rst.getString("ref_email_id"));
			}

			if(rst.next()) {
			
				setRef2Name(rst.getString("ref_name"));
				setRef2Company(rst.getString("ref_company"));
				setRef2Designation(rst.getString("ref_designation"));
				setRef2ContactNo(rst.getString("ref_contact_no"));
				setRef2Email(rst.getString("ref_email_id"));
			}
			rst.close();
			pst.close();
			
						
		} catch (Exception e) {
			
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
	}


	private void setEmpMedicalInfo(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst =null;
		ResultSet rst = null;
		
		try {
			
			pst = con.prepareStatement("SELECT * FROM emp_medical_details WHERE emp_id = ? order by question_id");
			pst.setInt(1, getEmpId());
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				
				if(rst.getInt("question_id")==1) {
					setEmpMedicalId1(rst.getString("medical_id"));
					setCheckQue1(uF.parseToBoolean(rst.getString("yes_no")));
					setQue1Desc(uF.showData(rst.getString("description"), ""));
					
				}else if(rst.getInt("question_id")==2) {
					setEmpMedicalId2(rst.getString("medical_id"));
					setCheckQue2(uF.parseToBoolean(rst.getString("yes_no")));
					setQue2Desc(uF.showData(rst.getString("description"), ""));
					
				}else if(rst.getInt("question_id")==3) {
					setEmpMedicalId3(rst.getString("medical_id"));
					setCheckQue3(uF.parseToBoolean(rst.getString("yes_no")));
					setQue3Desc(uF.showData(rst.getString("description"), ""));
					
//				}else if(rs.getInt("question_id")==4) {
//					setEmpMedicalId4(rs.getString("medical_id"));
//					setCheckQue4(uF.parseToBoolean(rs.getString("yes_no")));
//					setQue4Desc(uF.showData(rs.getString("description"), ""));
//					
//				}else if(rs.getInt("question_id")==5) {
//					setEmpMedicalId5(rs.getString("medical_id"));
//					setCheckQue5(uF.parseToBoolean(rs.getString("yes_no")));
//					setQue5Desc(uF.showData(rs.getString("description"), ""));
				}
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
			
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
	}

	public void setProbationPolicy(Connection con, UtilityFunctions uF) {
//		System.out.println("setProbationPolicy===>");
		PreparedStatement pst =null;
		ResultSet rst = null;
		
		try {
			pst = con.prepareStatement(selectProbationPolicy);
			pst.setInt(1, getEmpId());
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				setProbationDuration(rst.getInt("probation_duration"));
				setNoticeDuration(rst.getInt("notice_duration"));
				if(rst.getString("leaves_types_allowed")!=null){
					setProbationLeaves(rst.getString("leaves_types_allowed").split(","));
				}
			}
			rst.close();
			pst.close();
					
		} catch (Exception e) {
			
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
		
	}

	public void setEmpOfficialDetails(Connection con, UtilityFunctions uF) {
//		System.out.println("setEmpOfficialDetails==");
		PreparedStatement pst =null;
		ResultSet rst = null;
		
		try {
			
//			pst = con.prepareStatement("SELECT * FROM employee_official_details eod, employee_personal_details epd, department_info " +
//					"d WHERE eod.depart_id=d.dept_id and epd.emp_per_id=eod.emp_id AND emp_id=?");
//			pst = con.prepareStatement("SELECT * FROM employee_official_details where emp_id=?");
			
			Map<String, String> hmEmpLevelMap = cF.getEmpLevelMap(con);
			
			pst = con.prepareStatement("SELECT * FROM employee_official_details eod left join grades_details gd on gd.grade_id = eod.grade_id where emp_id = ?");
			pst.setInt(1, getEmpId());
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			log.debug("selectEmployeeR2V===>"+pst);
			if (rst.next()) {
//				System.out.println("in query==");

				setwLocation(rst.getString("wlocation_id"));
				setOrgId(rst.getString("org_id"));
//				setDesignation(rs.getString("designation_id"));
				setDepartment(rst.getString("depart_id"));
				setEmpGrade(rst.getString("grade_id"));
				setStrDesignation(rst.getString("designation_id"));
				setStrLevel(hmEmpLevelMap.get(rst.getString("emp_id")));
				setSupervisor(rst.getString("supervisor_emp_id"));
				setHR(rst.getString("emp_hr"));
				setHod(rst.getString("hod_emp_id"));
				if(rst.getString("service_id")!=null){
					setService(rst.getString("service_id").split(","));	
				}
				setStrPaycycleDuration(rst.getString("paycycle_duration"));
				setEmpPaymentMode(rst.getString("payment_mode"));
				
				
//				setAvailFrom(uF.getDateFormat(rs.getString("available_time_from"), DBTIME, CF.getStrReportTimeFormat()));
//				setAvailTo(uF.getDateFormat(rs.getString("available_time_to"), DBTIME, CF.getStrReportTimeFormat()));
				setRosterDependency(new FillApproval().getBoolValue(rst.getString("is_roster")));
				setAttendanceDependency(new FillApproval().getBoolValue(rst.getString("is_attendance")));
				setEmpType(rst.getString("emptype"));
				setIsFirstAidAllowance(uF.parseToBoolean(rst.getString("first_aid_allowance")));
//				setEmpDesignation(rs.getString("designation_name"));
				/*setEmpBankName(rs.getString("emp_bank_name"));
				setEmpBankAcctNbr(rs.getString("emp_bank_acct_nbr"));
				setEmpEmailSec(rs.getString("emp_email_sec"));
				setSkypeId(rs.getString("skype_id"));*/
				
			}
			rst.close();
			pst.close();
				
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
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
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("select emp_contractor,org_id from employee_official_details where emp_id = ?");
			pst.setInt(1, getEmpId());
//			log.debug("pst selectEmployeeR1V==>"+pst);
			rs = pst.executeQuery();
			if (rs.next()) {
				setEmpContractor(uF.showData(rs.getString("emp_contractor"), "1"));
				setOrgId(rs.getString("org_id"));
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement(selectEmployee1Details);
			pst.setInt(1, getEmpId());
//			log.debug("pst selectEmployeeR1V==>"+pst);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			if (rs.next()) {
//				nEmpOffId = rs.getInt("emp_off_id");
				setEmpId(rs.getInt("emp_per_id"));
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
				setEmpStartDate(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
//				log.debug("rs.getString(emp_img)=="+rs.getString("emp_image"));
				request.setAttribute("strImage", rs.getString("emp_image"));
				request.setAttribute("EMPLOYEE_EMAIL", rs.getString("emp_email"));
				request.setAttribute("EMPLOYEE_EMAIL2", rs.getString("emp_email_sec"));
				request.setAttribute("EMP_CODE", rs.getString("empcode"));

				dobYear=uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "yyyy");
				request.setAttribute("dobYear",dobYear);
				request.setAttribute("strEmpName", uF.showData(rs.getString("emp_fname"),"") +strEmpMName+" "+uF.showData(rs.getString("emp_mname"),"") +" "+uF.showData(rs.getString("emp_lname"),"") );
				
				/*if(rs.getString("service_id")!=null){
					setService(rs.getString("service_id").split(","));
				}*/
				
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
			pst.setInt(1, getEmpId());
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rs.next()){
				setUserName(rs.getString("username"));
				setEmpPassword(rs.getString("password"));
				setEmpUserTypeId(rs.getString("usertype_id"));
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

	public void setEmpFamilyMembers(Connection con, UtilityFunctions uF) {

		PreparedStatement pst =null;
		ResultSet rs = null;
		ArrayList<ArrayList<String>> alSiblings = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> alchilds = new ArrayList<ArrayList<String>>();
		try {
			
			pst = con.prepareStatement("SELECT * FROM emp_family_members WHERE emp_id = ?");
			pst.setInt(1, getEmpId());
//			log.debug("pst==>"+pst);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rs.next()) {
				
//				log.debug("rs.getString(member_name)==>"+rs.getString("member_name"));
				
				if(rs.getString("member_type").equals(MOTHER)) {
					
					setMotherName(uF.showData(rs.getString("member_name"), ""));
					setMotherDob(uF.getDateFormat(rs.getString("member_dob"), DBDATE, DATE_FORMAT));
					setMotherEducation(uF.showData(rs.getString("member_education"), ""));
					setMotherOccupation(uF.showData(rs.getString("member_occupation"), ""));
					setMotherContactNumber(uF.showData(rs.getString("member_contact_no"), ""));
					setMotherEmailId(uF.showData(rs.getString("member_email_id"), ""));
				}
				
				if(rs.getString("member_type").equals(FATHER)) {
					
					setFatherName(uF.showData(rs.getString("member_name"), ""));
					setFatherDob(uF.getDateFormat(rs.getString("member_dob"), DBDATE, DATE_FORMAT));
					setFatherEducation(uF.showData(rs.getString("member_education"), ""));
					setFatherOccupation(uF.showData(rs.getString("member_occupation"), ""));
					setFatherContactNumber(uF.showData(rs.getString("member_contact_no"), ""));
					setFatherEmailId(uF.showData(rs.getString("member_email_id"), ""));
					
				}
				
				if(rs.getString("member_type").equals(SPOUSE)) {
					
					setSpouseName(uF.showData(rs.getString("member_name"), ""));
					setSpouseDob(uF.getDateFormat(rs.getString("member_dob"), DBDATE, DATE_FORMAT));
					setSpouseEducation(uF.showData(rs.getString("member_education"), ""));
					setSpouseOccupation(uF.showData(rs.getString("member_occupation"), ""));
					setSpouseContactNumber(uF.showData(rs.getString("member_contact_no"), ""));
					setSpouseEmailId(uF.showData(rs.getString("member_email_id"), ""));
					setSpouseGender(rs.getString("member_gender"));
					
				}
				
				if(rs.getString("member_type").equals(SIBLING)) {
					
					ArrayList<String> alInner = new ArrayList<String>();
					alInner.add(rs.getString("member_id"));
					alInner.add(uF.showData(rs.getString("member_name"), ""));
					alInner.add(uF.getDateFormat(rs.getString("member_dob"), DBDATE, DATE_FORMAT));
					alInner.add(uF.showData(rs.getString("member_education"), ""));
					alInner.add(uF.showData(rs.getString("member_occupation"), ""));
					alInner.add(uF.showData(rs.getString("member_contact_no"), ""));
					alInner.add(uF.showData(rs.getString("member_email_id"), ""));
					alInner.add(rs.getString("member_gender"));
					alSiblings.add(alInner);
				}
				
				if(rs.getString("member_type").equals(CHILD)) {
					
					ArrayList<String> alInner = new ArrayList<String>();
					alInner.add(rs.getString("member_id"));
					alInner.add(uF.showData(rs.getString("member_name"), ""));
					alInner.add(uF.getDateFormat(rs.getString("member_dob"), DBDATE, DATE_FORMAT));
					alInner.add(uF.showData(rs.getString("member_education"), ""));
					alInner.add(uF.showData(rs.getString("member_occupation"), ""));
					alInner.add(uF.showData(rs.getString("member_contact_no"), ""));
					alInner.add(uF.showData(rs.getString("member_email_id"), ""));
					alInner.add(rs.getString("member_gender"));
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

	
	public String updateEmployee(Connection con) {
		
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			if(getStep()==1) {
				if(getEmpImage()!=null)
					uploadImage(getEmpId());
				
				if(getEmpFname()!=null){
					updateEmpPersonalDetails(con, uF);
				}
			}else if(getStep()==5) {
				
				updateEmpReferences(con, uF);
			}
			
			/*StringBuilder sbServices = new StringBuilder();
			
			for (int i = 0; getService() != null && i < getService().length; i++) {
				
				if (uF.parseToInt(getService()[i]) > 0) {
					
					sbServices.append(getService()[i] + ",");
				}
				
			}*/
			
//				if(getStep()==8) {
//					if(uF.parseToInt(getwLocation())>0){
//						updateEmpCodeBankInfo(con,uF);
//						updateEmpJoiningDate(con,uF);
//						updateEmpOfficialDetailsAdmin(con, uF, sbServices);
//						updateActivity(con,uF);
//						updateProbationPeriod(con, uF);
//					}
//					
//				}
//				else
//					updateUser(con,uF, strEmpType);
				if(getStep()==2) {
					updateSkills(con, uF);
					updateHobbies(con, uF);
					updateEmpEducation(con, uF);
					updateEmpLangues(con, uF);
				
				}else if(getStep()==3) {
					updateEmpFamilyMembers(con, uF);
				
				}else if(getStep()==4) {
					updateEmpPrevEmploment(con, uF);
				
				}else if(getStep()==6) {
					updateEmpMedicalInfo(con, uF);
				
				}else if(getStep()==7) {
					updateDocuments(con, uF);
					
				}else if (getStep()==9) {
					
					//check if all salary information has been filled up.
					if(checkAllSalaryInfoFilled(con, uF)) {
						approveEmployee(con);
					}
				}
//			}
			
//			request.setAttribute(MESSAGE, getEmpCode() + " updated successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in updation");
			
		} finally {
			
//			db.closeStatements(pst);
//			db.closeConnection(con);
		}
		
		return SUCCESS;

	}

	private boolean checkAllSalaryInfoFilled(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			setEmpOfficialDetails(con, uF);
			
			if(getService()!=null && getService().length > 0) {
				
				int cnt = 0;
				int i = 0;
				for(; i < getService().length ; i++) {
					
					pst = con.prepareStatement("SELECT  * from emp_salary_details WHERE emp_id = ? and service_id = ? limit 1");
					pst.setInt(1, getEmpId());
					pst.setInt(2, uF.parseToInt(getService()[i]));
//					log.debug("pst====>"+pst);
					rs = pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
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
		
		return false;
	}




//	private void updateActivity(Connection con, UtilityFunctions uF) {
//		
//		PreparedStatement pst = null;
//		
//		try {
//			pst = con.prepareStatement(updateGradeDesigLevel);
//			pst.setInt(1, uF.parseToInt(getEmpGrade()));
//			pst.setInt(2, uF.parseToInt(getwLocation()));
//			pst.setInt(3, uF.parseToInt(getDepartment()));
//			pst.setInt(4, uF.parseToInt(getEmpGrade()));
//			pst.setInt(5, uF.parseToInt(getEmpGrade()));
//			pst.setInt(6, getEmpId());
//			pst.setInt(7, getEmpId());
//			pst.execute();
//			
//			
//			pst = con.prepareStatement("UPDATE employee_activity_details SET emp_status_code=?, notice_period=?, probation_period=? where emp_id=?  and " +
//										"entry_date = (select max(entry_date) from employee_activity_details WHERE emp_id = ?)");
//			pst.setString(1, getEmpType());
//			pst.setInt(2, getNoticeDuration());
//			pst.setInt(3, getProbationDuration());   
//			pst.setInt(4, getEmpId());
//			pst.setInt(5, getEmpId());
//			pst.execute();
//			
//			
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}


//	private void updateEmpJoiningDate(Connection con, UtilityFunctions uF) {
//		
//		PreparedStatement pst = null;
//		try {
//			pst = con.prepareStatement("UPDATE employee_personal_details SET joining_date = ?, skype_id=?, emp_email_sec=? where emp_per_id = ?");
//			pst.setDate(1, uF.getDateFormat(getEmpStartDate(), DATE_FORMAT));
//			pst.setString(2, getSkypeId());
//			pst.setString(3, getEmpEmailSec());
//			pst.setInt(4, getEmpId());
//			
////			log.debug("pst==>"+pst);
//			
//			pst.execute();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//	}


	private void updateEmpReferences(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {
			
			pst = con.prepareStatement("UPDATE emp_references SET ref_name = ?, ref_company = ? , ref_designation =? , " +
							"ref_contact_no = ?, ref_email_id = ? ");
			pst.setString(1, getRef1Name());
			pst.setString(2, getRef1Company());
			pst.setString(3, getRef1Designation());
			pst.setString(4, getRef1ContactNo());
			pst.setString(5, getRef1Email());
//			log.debug("pst==>"+pst);
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("UPDATE emp_references SET ref_name = ?, ref_company = ? , ref_designation =? , " +
						"ref_contact_no = ?, ref_email_id = ? ");
			pst.setString(1, getRef2Name());
			pst.setString(2, getRef2Company());
			pst.setString(3, getRef2Designation());
			pst.setString(4, getRef2ContactNo());
			pst.setString(5, getRef2Email());
//			log.debug("pst==>"+pst);
			pst.execute();
			pst.close();
			
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


	public void updateProbationPeriod(Connection con, UtilityFunctions uF) {
//		System.out.println("updateProbationPeriod====>>>");
		PreparedStatement pst = null;
		
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
				
				pst = con.prepareStatement("UPDATE probation_policy SET leaves_types_allowed=?, probation_duration=?, notice_duration = ? " +
						"WHERE emp_id = ?");
				pst.setString(1, sbProbationLeaves.toString() );
				pst.setInt(2, getProbationDuration());
				pst.setInt(3, getNoticeDuration());
				pst.setInt(4, getEmpId());
//				log.debug("pst updateProbationPolicy=" + pst);
				int cnt=pst.executeUpdate();
				pst.close();
				if(cnt==0){
					insertProbationPeriod(con,uF);
				}
				
		}catch(Exception e) {
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

	public void updateUser(Connection con, UtilityFunctions uF, String strEmpType) {
		
		PreparedStatement pst = null;
		
		try {
			
			if (strEmpType != null && strEmpType.equalsIgnoreCase(ADMIN)) {
				
				pst = con.prepareStatement(updateUser1);
				pst.setString(1, getUserName());
				pst.setString(2, getEmpPassword());
				pst.setInt(3, getEmpId());
				pst.setInt(4, uF.parseToInt(getEmpUserTypeId()));
				pst.execute();
				pst.close();
				
			}else {
				
				pst = con.prepareStatement(updateUser1E);
				pst.setString(1, getEmpPassword());
				pst.setInt(2, getEmpId());
				pst.setString(3, getUserName());
				pst.execute();
				pst.close();
				
			}
			
		}catch(Exception e) {
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

	public void updateEmpOfficialDetailsAdmin(Connection con, UtilityFunctions uF, StringBuilder sbServices) {
//		System.out.println("updateEmpOfficialDetailsAdmin"+uF.parseToBoolean(getRosterDependency()));
//		System.out.println("updateEmpOfficialDetailsAdmin"+uF.parseToBoolean(getRosterDependency()));

		PreparedStatement pst = null;
		
		try {
			
			pst = con.prepareStatement("UPDATE employee_official_details SET depart_id=?, supervisor_emp_id=?,hod_emp_id=?, service_id=?, " +
					"available_days=?, wlocation_id=?, is_roster=?, is_attendance=?, emptype=?, first_aid_allowance=?, grade_id = ?,paycycle_duration=?, payment_mode=?, org_id=?,emp_hr=? WHERE emp_id=?");
			pst.setInt(1, uF.parseToInt(getDepartment()));
			pst.setInt(2, uF.parseToInt(getSupervisor()));
			pst.setInt(3, uF.parseToInt(getHod()));
//			setHod(rs.getString("supervisor_emp_id"));
			pst.setString(4, sbServices.toString());
//			pst.setTime(5, uF.getTimeFormat((getAvailFrom() != null) ? getAvailFrom().replace("T", "") : "", CF.getStrReportTimeFormat()));
//			pst.setTime(6, uF.getTimeFormat((getAvailTo() != null) ? getAvailTo().replace("T", "") : "", CF.getStrReportTimeFormat()));
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
			pst.setInt(16, getEmpId());
//			log.debug("pst officialDetails=>"+pst);
			int cnt = pst.executeUpdate();
			pst.close();
			
//			log.debug("cnt==>"+cnt);
			if(cnt==0) {
				insertEmpOfficialDetails(con, uF, sbServices);
			}
		
		}catch(Exception e) {
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

	public void updateEmpPersonalDetails(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {
			
				pst = con.prepareStatement("UPDATE employee_personal_details SET emp_fname=?,emp_mname=?, emp_lname=?, emp_email=?, emp_address1=?, " +
						"emp_address2=?, emp_city_id=?, emp_state_id=?, emp_country_id=?, emp_pincode=?, emp_address1_tmp=?, emp_address2_tmp=?, " +
						"emp_city_id_tmp=?, emp_state_id_tmp=?, emp_country_id_tmp=?, emp_pincode_tmp=?, emp_contactno=?, emp_pan_no = ?, emp_pf_no = ?, " +
						"emp_gpf_no = ?, emp_gender=?, emp_date_of_birth=?, emp_bank_name=?, emp_bank_acct_nbr=?,  emp_contactno_mob=?, " +
						"emergency_contact_name=?, emergency_contact_no=?, passport_no=?, passport_expiry_date=?, blood_group=?, marital_status=?, " +
						"emp_date_of_marriage=?, salutation=?, is_alive=?, emp_filled_flag=?, doctor_name=?, doctor_contact_no=?, uid_no=?, uan_no=? " +
						"WHERE emp_per_id=?");
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
		//			pst.setString(11, (fileName.length() > 0) ? fileName : "avatar_photo.png");
				pst.setString(18, getEmpPanNo());
				pst.setString(19, getEmpPFNo());
				pst.setString(20, getEmpGPFNo());
				pst.setString(21, getEmpGender());
				pst.setDate(22, uF.getDateFormat(getEmpDateOfBirth(), DATE_FORMAT));
				pst.setString(23, getEmpBankName());
				pst.setString(24, getEmpBankAcctNbr());
//				pst.setString(25, uF.showData(getEmpEmailSec(),""));
//				pst.setString(26, uF.showData(getSkypeId(),""));
				pst.setString(25, uF.showData(getEmpMobileNo(),""));
				
				pst.setString(26, getEmpEmergencyContactName());
				pst.setString(27, getEmpEmergencyContactNo());
				pst.setString(28, getEmpPassportNo());
				pst.setDate(29, uF.getDateFormat(getEmpPassportExpiryDate(),DATE_FORMAT));
				pst.setString(30, getEmpBloodGroup());
				pst.setString(31, getEmpMaritalStatus());
				
				pst.setDate(32, uF.getDateFormat(getEmpDateOfMarriage(), DATE_FORMAT));
				pst.setString(33, getSalutation());
				pst.setBoolean(34, true);
				pst.setBoolean(35, true);
				pst.setString(36, getEmpDoctorName());
				pst.setString(37, getEmpDoctorNo());
				pst.setString(38, getEmpUIDNo());
				pst.setString(39, getEmpUANNo());
				pst.setInt(40, getEmpId());
//				log.debug("pst updateEmployeePE"+pst);
				pst.execute();
				pst.close();
//			}
			
		} catch(Exception e) {
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

	
	public void updateEmpPrevEmploment(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {
			
			pst = con.prepareStatement("DELETE FROM emp_prev_employment WHERE emp_id = ?");
			pst.setInt(1, getEmpId());
//			log.debug("pst=>"+pst);
			pst.execute();
			pst.close();
			
			if(getPrevCompanyName()!=null && getPrevCompanyName().length > 0) {
				for(int i=0; i<getPrevCompanyName().length; i++) {
					
					if(getPrevCompanyName()[i].length()!=0) {
						
				//===start parvez date: 08-08-2022===	
//						pst = con.prepareStatement("INSERT INTO emp_prev_employment(company_name, company_location, company_city, company_state, " +
//								"company_country, company_contact_no, reporting_to, from_date, to_date, designation, responsibilities, skills, emp_id, " +
//									"report_manager_ph_no, hr_manager, hr_manager_ph_no)" +
//											"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
						pst = con.prepareStatement("INSERT INTO emp_prev_employment(company_name, company_location, company_city, company_state, " +
								"company_country, company_contact_no, reporting_to, from_date, to_date, designation, responsibilities, skills, emp_id, " +
									"report_manager_ph_no, hr_manager, hr_manager_ph_no, emp_esic_no, uan_no)" +
											"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
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
						pst.setInt(13, getEmpId());
						pst.setString(14, getPrevCompanyReportManagerPhNo()[i]);
						pst.setString(15, getPrevCompanyHRManager()[i]);
						pst.setString(16, getPrevCompanyHRManagerPhNo()[i]);
				//===start parvez date: 08-08-2022===		
						pst.setString(17, getPrevCompanyESICNo()[i]);
						pst.setString(18, getPrevCompanyUANNo()[i]);
				//===end parvez date: 08-08-2022===		
//						log.debug("pst=>"+pst);
						pst.execute();
						pst.close();
					}
				}
			}
			
		}catch (Exception e) {
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
			
			if(getMotherName()!=null && getMotherName().length()>0){
			pst = con.prepareStatement("UPDATE emp_family_members SET member_name = ?, member_dob = ?, member_education = ?, " +
							"member_occupation = ?, member_contact_no = ?, member_email_id = ?, member_gender = ?,member_marital=? " +
							"WHERE emp_id = ? and member_type = ?");
			
			pst.setString(1, getMotherName());
			pst.setDate(2, uF.getDateFormat(getMotherDob(), DATE_FORMAT));
			pst.setString(3, getMotherEducation());
			pst.setString(4, getMotherOccupation());
			pst.setString(5, getMotherContactNumber());
			pst.setString(6, getMotherEmailId());
			pst.setString(7, "8");
			pst.setString(8, "M");
			pst.setInt(9, getEmpId());
			pst.setString(10, MOTHER);
//			log.debug("pst=>"+pst);
			updateCnt = pst.executeUpdate();
			pst.close();
			
			}
			if(updateCnt==0) {
				if(getMotherName()!=null && getMotherName().length()>0){
				pst = con.prepareStatement("INSERT INTO emp_family_members(member_type, member_name, member_dob, member_education, " +
						"member_occupation, member_contact_no, member_email_id, member_gender,member_marital, emp_id)" +
							"VALUES (?,?,?,?,?,?,?,?,?,?)");
				pst.setString(1, MOTHER);
				pst.setString(2, getMotherName());
//				log.debug(getMotherDob()+"getMotherDob()");
				pst.setDate(3, uF.getDateFormat(getMotherDob(), DATE_FORMAT));
				pst.setString(4, getMotherEducation());
				pst.setString(5, getMotherOccupation());
				pst.setString(6, getMotherContactNumber());
				pst.setString(7, getMotherEmailId());
				pst.setString(8, "F");
				pst.setString(9, "M");
				pst.setInt(10, getEmpId());
//				log.debug("pst=>"+pst);
				pst.execute();
				pst.close();
				}
			}
			
			if(getFatherName()!=null && getFatherName().length()>0){
			pst = con.prepareStatement("UPDATE emp_family_members SET member_name = ?, member_dob = ?, member_education = ?, " +
					"member_occupation = ?, member_contact_no = ?, member_email_id = ?, member_gender = ?,member_marital=? " +
					"WHERE emp_id = ? and member_type = ?");
			pst.setString(1, getFatherName());
			pst.setDate(2, uF.getDateFormat(getFatherDob(), DATE_FORMAT));
			pst.setString(3, getFatherEducation());
			pst.setString(4, getFatherOccupation());
			pst.setString(5, getFatherContactNumber());
			pst.setString(6, getFatherEmailId());
			pst.setString(7, "F");
			pst.setString(8, "M");
			pst.setInt(9,getEmpId());
			pst.setString(10, FATHER);
//			log.debug("pst=>"+pst);
			updateCnt = pst.executeUpdate();
			pst.close();
			
			}
			if(updateCnt==0) {
				
				if(getFatherName()!=null && getFatherName().length()>0){
				pst = con.prepareStatement("INSERT INTO emp_family_members(member_type, member_name, member_dob, member_education, " +
						"member_occupation, member_contact_no, member_email_id, member_gender,member_marital, emp_id)" +
							"VALUES (?,?,?,?,?,?,?,?,?,?)");
			
				pst.setString(1, FATHER);
				pst.setString(2, getFatherName());
				pst.setDate(3, uF.getDateFormat(getFatherDob(), DATE_FORMAT));
				pst.setString(4, getFatherEducation());
				pst.setString(5, getFatherOccupation());
				pst.setString(6, getFatherContactNumber());
				pst.setString(7, getFatherEmailId());
				pst.setString(8, "M");
				pst.setString(9, "M");
				pst.setInt(10, getEmpId());
//				log.debug("pst=>"+pst);
				pst.execute();
				pst.close();
				}
				
			}
			
			if(getSpouseName()!=null && getSpouseName().length()>0){
			pst = con.prepareStatement("UPDATE emp_family_members SET member_name = ?, member_dob = ?, member_education = ?, " +
					"member_occupation = ?, member_contact_no = ?, member_email_id = ?, member_gender = ?,member_marital=? " +
					"WHERE emp_id = ? and member_type = ?");
			pst.setString(1, getSpouseName());
			pst.setDate(2, uF.getDateFormat(getSpouseDob(), DATE_FORMAT));
			pst.setString(3, getSpouseEducation());
			pst.setString(4, getSpouseOccupation());
			pst.setString(5, getSpouseContactNumber());
			pst.setString(6, getSpouseEmailId());
			pst.setString(7, getSpouseGender());
			pst.setString(8, "M");
			pst.setInt(9, getEmpId());
			pst.setString(10, SPOUSE);
//			log.debug("pst=>"+pst);
			updateCnt = pst.executeUpdate();
			pst.close();
			
			}
			if(updateCnt==0) {
				
				if(getSpouseName()!=null && getSpouseName().length()>0){
				pst = con.prepareStatement("INSERT INTO emp_family_members(member_type, member_name, member_dob, member_education, " +
						"member_occupation, member_contact_no, member_email_id, member_gender,member_marital, emp_id)" +
							"VALUES (?,?,?,?,?,?,?,?,?,?)");
			
				pst.setString(1, SPOUSE);
				pst.setString(2, getSpouseName());
				pst.setDate(3, uF.getDateFormat(getSpouseDob(), DATE_FORMAT));
				pst.setString(4, getSpouseEducation());
				pst.setString(5, getSpouseOccupation());
				pst.setString(6, getSpouseContactNumber());
				pst.setString(7, getSpouseEmailId());
				pst.setString(8, getSpouseGender());
				pst.setString(9, "M");
				pst.setInt(10, getEmpId());
//				log.debug("pst=>"+pst);
				pst.execute();
				pst.close();
				}
			}
			
			if(getMemberName().length != 0) {
				
				pst = con.prepareStatement("DELETE FROM emp_family_members WHERE emp_id = ? and member_type = ?");
				pst.setInt(1, getEmpId());
				pst.setString(2, SIBLING);	
//				log.debug("pst=>"+pst);
				pst.execute();
				pst.close();
				
				for(int i=0; i<getMemberName().length; i++) {
					
					if(getMemberName()[i]!=null && getMemberName()[i].length()>0){
					pst = con.prepareStatement("INSERT INTO emp_family_members(member_type, member_name, member_dob, member_education, " +
							"member_occupation, member_contact_no, member_email_id, member_gender, emp_id,member_marital)" +
								"VALUES (?,?,?,?,?,?,?,?,?,?)");
					pst.setString(1, SIBLING);
					pst.setString(2, getMemberName()[i]);
					pst.setDate(3, uF.getDateFormat(getMemberDob()[i], DATE_FORMAT));
					pst.setString(4, getMemberEducation()[i]);
					pst.setString(5, getMemberOccupation()[i]);
					pst.setString(6, getMemberContactNumber()[i]);
					pst.setString(7, getMemberEmailId()[i]);
					pst.setString(8, getMemberGender()[i]);
					pst.setInt(9, getEmpId());
					pst.setString(10, getSiblingMaritalStatus()[i]);
//					log.debug("pst=>"+pst);
					pst.execute();
					pst.close();
					}
				}
				
			}
			
			if(getChildName().length != 0) {
				
				pst = con.prepareStatement("DELETE FROM emp_family_members WHERE emp_id = ? and member_type = ?");
				pst.setInt(1, getEmpId());
				pst.setString(2, CHILD);	
//				log.debug("pst=>"+pst);
				pst.execute();
				pst.close();
				
				for(int i=0; i<getChildName().length; i++) {
					
					if(getChildName()[i]!=null && getChildName()[i].length()>0){
					
					pst = con.prepareStatement("INSERT INTO emp_family_members(member_type, member_name, member_dob, member_education, " +
							"member_occupation, member_contact_no, member_email_id, member_gender, emp_id,member_marital)" +
								"VALUES (?,?,?,?,?,?,?,?,?,?)");
					pst.setString(1, CHILD);
					pst.setString(2, getChildName()[i]);
					pst.setDate(3, uF.getDateFormat(getChildDob()[i], DATE_FORMAT));
					pst.setString(4, getChildEducation()[i]);
					pst.setString(5, getChildOccupation()[i]);
					pst.setString(6, getChildContactNumber()[i]);
					pst.setString(7, getChildEmailId()[i]);
					pst.setString(8, getChildGender()[i]);
					pst.setInt(9, getEmpId());
					pst.setString(10, getChildMaritalStatus()[i]);
//					log.debug("pst=>"+pst);
					pst.execute();
					pst.close();
					
					}
				}
				
			}
			
		}catch (Exception e) {
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

	
	public void updateEmpEducation(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {
			
			if(getDegreeName()!=null && getDegreeName().length!= 0) {
				
				pst = con.prepareStatement("DELETE FROM education_details WHERE emp_id = ?");
				pst.setInt(1, getEmpId());
//				log.debug("pst=>"+pst);
				pst.execute();
				pst.close();
				
				for(int i=0; i<getDegreeName().length; i++) {
					
					if(getDegreeName()[i].length()!=0) {
						
						pst = con.prepareStatement("INSERT INTO education_details(education_id, degree_duration, completion_year, grade, emp_id)" +
						"VALUES (?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(getDegreeName()[i]));
						pst.setInt(2, uF.parseToInt(getDegreeDuration()[i]));
						pst.setInt(3, uF.parseToInt(getCompletionYear()[i]));
						pst.setString(4, getGrade()[i]);
						pst.setInt(5, getEmpId());
//						log.debug("pst=>"+pst);
						pst.execute();
						pst.close();
					}
				}
			}
			
		}catch (Exception e) {
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

	public void updateEmpLangues(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {
		
			if(getLanguageName()!=null && getLanguageName().length!=0) {
				
				pst = con.prepareStatement("DELETE FROM languages_details WHERE emp_id = ?");
				pst.setInt(1, getEmpId());
//				log.debug("pst=>"+pst);
				pst.execute();
				pst.close();
				
//				for(int i=0; getIsRead()!=null && i<getIsRead().length; i++) {
//					
//					log.debug("Read"+i+"==>"+getIsRead()[i]);
//					
//				}
				
				if(getIsRead()!=null)
				if(getIsWrite()!=null)
				if(getIsSpeak()!=null)
				
				for(int i=0; getLanguageName()!=null && i<getLanguageName().length; i++) {
					
					if(getLanguageName()[i].length()!=0) {
						pst = con.prepareStatement("INSERT INTO languages_details(language_name, language_read, language_write, language_speak, emp_id, language_mothertounge)" +
						"VALUES (?,?,?,?, ?,?)");
						pst.setString(1, getLanguageName()[i]);
						pst.setInt(2, uF.parseToInt(getIsRead()[i]));
						pst.setInt(3, uF.parseToInt(getIsWrite()[i]));
						pst.setInt(4, uF.parseToInt(getIsSpeak()[i]));
						pst.setInt(5, getEmpId());
						pst.setInt(6, uF.parseToInt(getIsMotherTounge()[i]));
//						log.debug("pst=>"+pst);
						pst.execute();
						pst.close();
					}
					
				}
				
			}
			
		}catch (Exception e) {
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

	public void updateDocuments(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
//			pst = con.prepareStatement(deleteDocuments);
//			pst.setInt(1, getEmpId());
//			log.debug("pst deleteDocuments=>"+pst);
//			pst.execute();
			
//			String filePath1 = request.getRealPath("/userDocuments/");
//			String fileName1 = "";

			int i=0;
			
			if(idDoc!=null) {
				
//				log.debug("idDoc.length==>"+idDoc.length+" "+idDocName.length);
				
//				for (i=0; getDocId()!=null && i<getDocId().length; i++) {
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
			            pst = con.prepareStatement(insertDocuments);
			            pst.setString(1, getIdDocName()[i]);
			            pst.setString(2, getIdDocType()[i]);
			            pst.setInt(3, getEmpId());
//			            pst.setString(4, fileName1);
			            pst.setString(4, filename);
//			            uF.get
			            pst.setInt(5, uF.parseToInt(null));
			            pst.setDate(6, uF.getCurrentDate(cF.getStrTimeZone()));
//			            log.debug("pst insertDocuments==>"+pst);
//			            pst.execute();
			            int x = pst.executeUpdate();
			            pst.close();
			            
			            if (x > 0) {
			            	if(idDocType!=null && idDocType.equals(DOCUMENT_COMPANY_PROFILE)){
								Map<String, String> hmEmpHRId = CF.getEmpHRIdMap(con, uF);
								String strDomain = request.getServerName().split("\\.")[0];
								Map<String, String> hmEmpUsertypeId = CF.getEmployeeIdUserTypeIdMap(con);
								if(hmEmpHRId!=null && hmEmpHRId.get(getEmpId())!=null && !hmEmpHRId.get(getEmpId()).equals("")){
									String alertData = "<div style=\"float: left;\"> <b>" + uF.showData(CF.getEmpNameMapByEmpId(con, "" + getEmpId()), "") + "</b> has updated company profile.</div>";
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
								
								for(int j=0; alEmpId!=null && j<alEmpId.size();j++){
									String alertData = "<div style=\"float: left;\"> <b>" + uF.showData(CF.getEmpNameMapByEmpId(con, "" + getEmpId()), "") + "</b> has updated company profile.</div>";
									String alertAction = "MyProfile.action?empId="+getEmpId()+"&pType=WR";
									UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
									userAlerts.setStrDomain(strDomain);
									userAlerts.setStrEmpId(alEmpId.get(j));
									userAlerts.setStrData(alertData);
									userAlerts.setStrAction(alertAction);
									userAlerts.setCurrUserTypeID(hmEmpUsertypeId.get(alEmpId.get(j)));
									userAlerts.setStatus(INSERT_WR_ALERT);
									Thread t = new Thread(userAlerts);
									t.run();
								}
							}
			            }
					}
			            
//					}
				}
			}
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
		
		
	}

	public void updateHobbies(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		
		try {
			
			pst = con.prepareStatement(deleteHobbies);
			pst.setInt(1, getEmpId());
//			if(isDebug)
//				log.debug("pst deleteHobbies=>"+pst);
			pst.execute();
			pst.close();
			
			for(String h: hobbyName) {
				
				if(h!=null && h.length()>0){
				pst = con.prepareStatement(insertHobbies);
				pst.setString(1, h);
				pst.setInt(2,getEmpId());
//				if(isDebug)
//					log.debug("pst==>>"+pst);
				pst.execute();
				pst.close();
				}
			}

		}catch (Exception e) {
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
		
		try {
			
			pst = con.prepareStatement(deleteSkills);
			pst.setInt(1, getEmpId());
//			log.debug("pst deleteSkills=>"+pst);
			pst.execute();
			pst.close();
			
			for(int i=0; getSkillName()!= null && i<getSkillName().length; i++) {
				
				if(getSkillName()[i].length()!=0 || getSkillValue()[i].length()!=0) {
				
					pst = con.prepareStatement(insertSkill);
					pst.setInt(1, uF.parseToInt(getSkillName()[i]));
					pst.setString(2, getSkillValue()[i]);
					pst.setInt(3,getEmpId());
//					if(isDebug)
//						log.debug("pst==>>"+pst);
					pst.execute();
					pst.close();
					
				}
			}
			
		}catch (Exception e) {
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

	
	private void approveEmployee(Connection con) {
		
//		Connection con = null;
		PreparedStatement pst = null;
//		Database db = new Database();
		
		try {
			
//			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE employee_personal_details SET approved_flag = ? , is_alive = ? WHERE emp_per_id = ?");
			pst.setBoolean(1, true);
			pst.setBoolean(2, true);
			pst.setInt(3, getEmpId());
//			log.debug("pst===>"+pst);
			int cnt =pst.executeUpdate();
			pst.close();
//			log.debug("cnt==>"+cnt);
			
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


//	private void updateEmpCodeBankInfo(Connection con, UtilityFunctions uF) {
//		
//		PreparedStatement pst = null;
//		
//		try {
//			pst = con.prepareStatement("UPDATE employee_personal_details SET empcode = ?, emp_bank_name = ? , emp_bank_acct_nbr = ? " +
//					"where emp_per_id = ?");
//			pst.setString(1, uF.showData(getEmpCodeAlphabet(), "")+getEmpCodeNumber());
//			pst.setString(2, getEmpBankName());
//			pst.setString(3, getEmpBankAcctNbr());
//			pst.setInt(4, getEmpId());
//			
////			log.debug("pst==>"+pst);
//			pst.execute();
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}


private void updateEmpMedicalInfo(Connection con, UtilityFunctions uF) {
		
//		PreparedStatement pst = null;
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
			String fileName = null;
			if (file != null) {
				fileName = uF.uploadFile(request, DOCUMENT_LOCATION, file,
						fileName1);
				pst = con
						.prepareStatement("UPDATE emp_medical_details SET yes_no=?, description=?,filepath=? WHERE medical_id = ?");
				pst.setBoolean(1, checkQue);
				pst.setString(2, queDesc);
				pst.setString(3, fileName);
				pst.setInt(4, uF.parseToInt(medicalid));
			} else {
				pst = con
						.prepareStatement("UPDATE emp_medical_details SET yes_no=?, description=? WHERE medical_id = ?");
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
			pst = con.prepareStatement("INSERT INTO emp_medical_details (question_id, emp_id, yes_no, description,filepath) values(?,?,?,?,?)");
			pst.setInt(1, uF.parseToInt(queId));
			pst.setInt(2, getEmpId());
			pst.setBoolean(3, checkQue);
			pst.setString(4, queDesc);
			pst.setString(5, fileName);
//			log.debug("pst ==>"+pst);
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

	private void uploadImage(int empId2) {
		
		try {
			
			UploadImage uI = new UploadImage();
			uI.setServletRequest(request);
			uI.setImageType("EMPLOYEE_IMAGE");
			uI.setEmpImage(getEmpImage());
			uI.setEmpImageFileName(getEmpImageFileName());
			uI.setEmpId(empId2+"");
			uI.setCF(cF);
			uI.upoadImage();
			
		}catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}


	public List<File> getIdDoc() {
		return idDoc;
	}

	public void setIdDoc(List<File> idDoc) {
		this.idDoc = idDoc;
	}

	public List<String> getIdDocFileName() {
		return idDocFileName;
	}

	public void setIdDocFileName(List<String> idDocFileName) {
		this.idDocFileName = idDocFileName;
	}

	public void insertProbationPeriod(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		try {
			StringBuilder sbProbationLeaves = new StringBuilder();
			int i = 0;
			if(getProbationLeaves() != null) {
				for (;  i < getProbationLeaves().length - 1; i++) {
	
					if (uF.parseToInt(getProbationLeaves()[i]) > 0) {
						sbProbationLeaves.append(getProbationLeaves()[i] + ",");
					}
				}
				if (uF.parseToInt(getProbationLeaves()[i]) > 0) {
					sbProbationLeaves.append(getProbationLeaves()[i]);
				}
			}
			pst = con.prepareStatement("INSERT INTO probation_policy(emp_id, leaves_types_allowed, probation_duration, notice_duration) VALUES(?,?,?,?)");
			pst.setInt(1, getEmpId());
			pst.setString(2, sbProbationLeaves.toString());
			pst.setInt(3, getProbationDuration());
			pst.setInt(4, getNoticeDuration());
//			log.debug("pst insertProbationPolicy==>>"+pst);
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

	
	public void insertEmpOfficialDetails(Connection con, UtilityFunctions uF, StringBuilder sbServices) {
//		System.out.println("insertEmpOfficialDetails"+getRosterDependency());
//		System.out.println("insertEmpOfficialDetails"+getRosterDependency());
		PreparedStatement pst = null;
		
		try {
			
			pst = con.prepareStatement(insertEmployeeO);
			pst.setInt(1, uF.parseToInt(getDepartment()));
			pst.setInt(2, uF.parseToInt(getSupervisor()));
			pst.setInt(3, uF.parseToInt(getHod()));
			pst.setString(4, sbServices.toString());
			pst.setString(5, "");
			pst.setInt(6, getEmpId());
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
//			log.debug("pst insertEmployeeO=>"+pst);
//			pst.setString(13, getEmpDesignation());
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

		return alPrevEmployment;
	}

	private String empUserTypeId;
//	private String empPerId;
//	private String empId;
	private String empContractor;
	private String empCodeAlphabet;
	private String empCodeNumber;
	private String empFname;
	public String getEmpMname() {
		return empMname;
	}



	public void setEmpMname(String empMname) {
		this.empMname = empMname;
	}


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
	private String empBloodGroup;
	private String empMaritalStatus;
	private boolean approvedFlag;
	
	private Timestamp empFilledFlagDate;
	
	private String empImageFileName;
	private File empImage;
	
	private String empUANNo;
	private String empUIDNo;
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
	private String orgId;
	private String empGrade;
	private String strLevel;
	private String strDesignation;
	private String department;
	private String supervisor;
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


	private String hod;
	private String HR;
	private String[] service;
	private String availFrom;
	private String availTo;
	private String rosterDependency;
	private String attendanceDependency;
	private String empType;
	private boolean isFirstAidAllowance;
	private int probationDuration;
	private int noticeDuration;
	
	
	private String []empKra;
	private String []empKraId;
	
	
	private String[] probationLeaves;

	
	private String[] skillName;
	private String[] skillValue;
	
	private String[] hobbyName;
	
	private String[] languageName;
	private String[] isRead;
	private String[] isWrite;
	private String[] isSpeak;
	private String[] isMotherTounge;
	
	private String[] degreeName;
	private String[] degreeDuration;
	private String[] completionYear;
	private String[] grade;
	
	private List<File> idDoc;
	private List<String> idDocFileName;
	private List<String> idDocStatus;
	public List<String> getIdDocStatus() {
		return idDocStatus;
	}



	public void setIdDocStatus(List<String> idDocStatus) {
		this.idDocStatus = idDocStatus;
	}


	private int[] docId;
	private String[] idDocName;
	private String[] idDocType;
	
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
	private String[] siblingMaritalStatus;
	
	private String[] childName;
	private String[] childDob;
	private String[] childEducation;
	private String[] childOccupation;
	private String[] childContactNumber;
	private String[] childEmailId;
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
	private List<File> que1DescFile;
	private List<String> que1IdFileStatus;


	//	private File que2DescFile;
//	private File que3DescFile;
//	private File que4DescFile;
//	private File que5DescFile;
	private List<String> que1DescFileFileName;
//	private String que2DescFileFileName;
//	private String que3DescFileFileName;
//	private String que4DescFileFileName;
//	private String que5DescFileFileName;
	
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
	
	List<FillEmploymentType> empTypeList;
	List<FillApproval> rosterDependencyList;
	List<FillGrade> gradeList;
	List<FillDesig> desigList;
	List<FillLevel> levelList;
	List<FillDepartment> deptList;
	List<FillEmployee> supervisorList;
	List<FillEmployee> HRList;
	List<FillEmployee> HodList;
	List<FillServices> serviceList;
	List<FillCountry> countryList;
	List<FillState> stateList;
	List<FillCity> cityList;
	List<FillWLocation> wLocationList;
	List<FillOrganisation> orgList;
	List<FillBank> bankList;
	List<FillGender> empGenderList;
	List<FillProbationDuration> probationDurationList;
	List<FillNoticeDuration> noticeDurationList;
	List<FillLeaveType> leaveTypeList;
	List<FillMaritalStatus> maritalStatusList;
	List<FillBloodGroup> bloodGroupList;
	List<FillEducational> educationalList;
	List<FillDegreeDuration> degreeDurationList;
	List<FillYears> yearsList;
	List<FillSkills> skillsList;
	List<FillSalutation> salutationList;
	
	String salutation;
	
	String empPaymentMode;
	List<FillPayMode> paymentModeList;
	
	List<FillPayCycleDuration> paycycleDurationList;
	String strPaycycleDuration;
	
	/*public void validate() {
		
		log.debug("Inside Validate..");
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		loadValidateEmployee();
		String referer = request.getHeader("Referer");
		strUserType = (String)session.getAttribute(USERTYPE);
		request.setAttribute(PAGE, PAddEmployee);
		
		if (getMode()!=null && getMode().length()>0 ) {
			request.setAttribute(TITLE, TEditEmployee);
		}else
			request.setAttribute(TITLE, TAddEmployee);
		
		if (referer != null) {
			int index1 = referer.indexOf(request.getContextPath());
			int index2 = request.getContextPath().length();
			referer = referer.substring(index1 + index2 + 1);
		}
		setRedirectUrl(referer);
		
		if(strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER)) {
		
			if (getEmpCodeAlphabet() != null && getEmpCodeAlphabet().length() == 0) {
				addFieldError("empCodeAlphabet", "Employee Code Alphabet is required");
			}
			if (getEmpCodeNumber() != null && getEmpCodeNumber().length() == 0) {
				addFieldError("empCodeNumber", "Employee Code Number is required");
			}
			
			if(getEmpCodeAlphabet() != null && getEmpCodeAlphabet().length() != 0 && 
					getEmpCodeNumber() != null && getEmpCodeNumber().length() != 0  &&
					!getAutoGenerate() && getEmpId()== 0) {
				
				Map<String, String> hmEmpCode = CF.getEmpCodeMap1();
				
				log.debug("hmEmpCode.get(getEmpCodeAlphabet()+getEmpCodeNumber())====>" +
						""+hmEmpCode.get(getEmpCodeAlphabet()+getEmpCodeNumber()));
				
				if(hmEmpCode.get(getEmpCodeAlphabet()+getEmpCodeNumber()) != null) {
				
					addFieldError("empCodeAlphabet", "Employee Code already Exist");
				}
			
			}
		}
		if (getEmpFname() != null && getEmpFname().length() == 0) {
			addFieldError("empFname", "Employee First Name is required");
		}
		if (getEmpLname() != null && getEmpLname().length() == 0) {
			addFieldError("empLname", "Employee Last Name is required");
		}
		if (getEmpFname() != null && getEmpLname() != null && getEmpFname().length()>0 && getEmpLname().length()>0 && getEmpFname().equalsIgnoreCase(getEmpLname())) {
			addFieldError("empFLname", "First name and last name can not be same");
		}
		if (getUserName() != null && getUserName().length() == 0) {
			addFieldError("userName", "Username is required");
		}
		if (getEmpPassword() != null && getEmpPassword().length() == 0) {
			addFieldError("empPassword", "Password is required");
		}
		if (getEmpEmail() != null && getEmpEmail().length() == 0) {
			addFieldError("empEmail", "Employee Email is required");
		}else if(getEmpEmail() != null){
			Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
			Matcher m=p.matcher(getEmpEmail());
			if(!m.matches()){
				log.debug("Field Error email");
				addFieldError("empEmail", "Please enter valid email address");	
			}
		}
		
		if (getEmpAddress1() != null && getEmpAddress1().length() == 0) {
			addFieldError("empAddress1", "Employee Address1 is required");
		}
		if (getCountry() != null && uF.parseToInt(getCountry()) == 0) {
			addFieldError("country", "Select Country is required");
		}
		if (getState() != null && uF.parseToInt(getState()) == 0) {
			addFieldError("state", "Select State is required");
		}
		if (getCity() != null && getCity().length() == 0) {
			addFieldError("city", "Suburb is required");
		}
		if(getEmpEmergencyContactNo()!=null && getEmpEmergencyContactNo().length() == 0){
			addFieldError("empEmergencyContactNo", "Emergency Contact No is required");
		}
		if (getEmpGender() != null && getEmpGender().equals("0")) {
			addFieldError("empGender", "Gender is required");
		}
		if (getEmpDateOfBirth() != null && getEmpDateOfBirth().length() == 0) {
			addFieldError("empDateOfBirth", "Date Of Birth is required");
		}

		if(strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER)) {
			
			if (getEmpType() != null && getEmpType().equalsIgnoreCase("0")) {
				addFieldError("empType", "Select Employment Type is required");
			}
			if (getwLocation() != null && uF.parseToInt(getwLocation()) == 0) {
				addFieldError("wLocation", "Select Work Location is required");
			}
			if (getDepartment() != null && uF.parseToInt(getDepartment()) == 0) {
				addFieldError("department", "Department is required");
			}
			if (getEmpGrade() != null && uF.parseToInt(getEmpGrade()) == 0) {
				
				addFieldError("empGrade", "Grade is required");
			}
			if (getSupervisor() != null && uF.parseToInt(getSupervisor()) == 0) {
				addFieldError("supervisor", "Supervisor is required");
			}
			if ( (getEmpFname()!=null && getService()==null) || (getService() != null && getService().length==0)) {
				addFieldError("service", "Cost-center is required");
			}
			if (getRosterDependency() != null && getRosterDependency().equalsIgnoreCase("0")) {
				addFieldError("rosterDependency", "Roster Dependency is required");
			}
			
			if (getAvailFrom() != null && getAvailFrom().equalsIgnoreCase("0")) {
				addFieldError("availFrom", "Available from time is required");
			}else if(getAvailFrom() != null && uF.getTimeFormat(getAvailFrom(), CF.getStrReportTimeFormat())==null){
				addFieldError("availFrom", "Please enter available from time in correct format. For example 12:00PM");
			}
			
			if (getAvailTo() != null && getAvailTo().equalsIgnoreCase("0")) {
				addFieldError("availTo", "Available to time is required");
			}else if(getAvailTo() != null && uF.getTimeFormat(getAvailTo(), CF.getStrReportTimeFormat())==null){
				addFieldError("availFrom", "Please enter available to time in correct format. For example 12:00PM");
			}
			
			if(getProbationDuration() == -1 ) {
				addFieldError("probationDuration", "Probation Duration time is required");
			}
			log.debug("getProbationLeaves()=-==="+getProbationLeaves());
			if(((getEmpFname()!=null && getProbationLeaves()==null)  || (getProbationLeaves()!=null && getProbationLeaves().length==0))){
				addFieldError("probationLeaves", "Probation Leaves are required");
			}
			
			log.debug("getEmpCodeAlphabet()==>"+getEmpCodeAlphabet());
				
			}
		}*/
		
	private String redirectUrl;
	
	public String getEmpFname() {
		return empFname;
	}

	public void setEmpFname(String empFname) {
		this.empFname = empFname;
	}
//	public String getEmpMname() {
//		return empMname;
//	}
//
//
//
//	public void setEmpMname(String empMname) {
//		this.empMname = empMname;
//	}


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

//	public String getDesignation() {
//		return designation;
//	}
//
//	public void setDesignation(String designation) {
//		this.designation = designation;
//	}

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

//	public List<FillDesig> getDesigList() {
//		return desigList;
//	}
//
//	public void setDesigList(List<FillDesig> desigList) {
//		this.desigList = desigList;
//	}

	
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

	public int getEmpId() {
		return empId;
	}

	public void setEmpId(int empId) {
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

//	public File[] getIdDoc() {
//		return idDoc;
//	}
//
//	public void setIdDoc(File[] idDoc) {
//		this.idDoc = idDoc;
//	}

	public String[] getIdDocName() {
		return idDocName;
	}

	public void setIdDocName(String[] idDocName) {
		this.idDocName = idDocName;
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

	public String[] getIsMotherTounge() {
		return isMotherTounge;
	}

	public void setIsMotherTounge(String[] isMotherTounge) {
		this.isMotherTounge = isMotherTounge;
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

	/*public String getStrEdit() {
		return strEdit;
	}

	public void setStrEdit(String strEdit) {
		this.strEdit = strEdit;
	}*/

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
	public int getStep() {
		return step;
	}
	public void setStep(int step) {
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
	public String[] getIdDocType() {
		return idDocType;
	}
	public void setIdDocType(String[] idDocType) {
		this.idDocType = idDocType;
	}
	public int[] getDocId() {
		return docId;
	}
	public void setDocId(int[] docId) {
		this.docId = docId;
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
//	public String[] getIdDocFileName() {
//		return idDocFileName;
//	}
//	public void setIdDocFileName(String[] idDocFileName) {
//		this.idDocFileName = idDocFileName;
//	}
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<FillEducational> getEducationalList() {
		return educationalList;
	}

	public void setEducationalList(List<FillEducational> educationalList) {
		this.educationalList = educationalList;
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
//===end parvez date: 08-08-2022===
}
