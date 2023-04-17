package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillApproval;
import com.konnect.jpms.select.FillCountry;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesignation;
import com.konnect.jpms.select.FillEducation;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillEmployeeStatus;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strBaseUserType = null;
	String strSessionEmpId = null;
	String strUsertypeId;
	
	private String fromDate;
	private String toDate;
	private String alertStatus;
	private String alert_type;
	
	
	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}


	private List<FillApproval> rosterDependencyList;
	private List<FillDesignation> desigList; 
	private List<FillDepartment> deptList;
	private List<FillEmployee> supervisorList;
	private List<FillServices> serviceList;
	private List<FillCountry> countryList;       
	private List<FillState> stateList;
	private List<FillEmployeeStatus> empStatusList;
//	List<FillStatus> statusList;
	
	private String page;  
	 
	private String strLocation;
	private String strDepartment;
	private String strSbu;
	private String strLevel;
	private String strStatus;
	private String strEducation;
	
	String strGrade;
	String strEmployeType;
	
	private String[] f_status;
	private String[] f_strWLocation; 
	private String[] f_department;
	private String[] f_level; 
	private String[] f_service;
	private String[] f_education;
	private String[] f_grade;
	private String[] f_employeType;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	
	
//	List<FillCity> cityList;
	private List<FillWLocation> wLocationList;
	private List<FillOrganisation> organisationList;
	
	List<FillGrade> gradeList;
	List<FillEmploymentType> employementTypeList;
	private String f_org;
	
	CommonFunctions CF=null;
	String strAction = null;
	
	private String advanceFilter;
	private String afParam;
	private String strBirthStartDate;
	private String strBirthEndDate;
	private String strJoiningStartDate;
	private String strJoiningEndDate;
	private String strTerminateStartDate;
	private String strTerminateEndDate;

	
	private List<FillEducation> eduList;
	
	private String alertID;
	private String fromPage;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strUsertypeId = (String) session.getAttribute(BASEUSERTYPEID);
		
		request.setAttribute(PAGE, PReportEmployee);
		
		String alphaValue = (String) request.getParameter("alphaValue");

		strAction = request.getServletPath();
		if(strAction!=null) {
			strAction = strAction.replace("/","");
		}
		
		/*if(getF_strWLocation()==null){
			setF_strWLocation((String)session.getAttribute(WLOCATIONID));
		}*/
		
//		System.out.println("EmpR/152---getAlertID()="+getAlertID());
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
//		if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(EMP_TERMINATED_ALERT)){
//			updateUserAlerts1();
//		}
		
		loadPageVisitAuditTrail(CF, uF);//Created By Dattatray 09-6-2022
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if(uF.parseToInt(getF_org())<=0) {
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
		
		/*if (uF.parseToInt(getF_org()) <= 0) {
			setF_org((String) session.getAttribute(ORGID));
		}*/
		
		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		if(getStrSbu() != null && !getStrSbu().equals("")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		if(getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		if(getStrStatus() != null && !getStrStatus().equals("")) {
			setF_status(getStrStatus().split(","));
		} else {
			setF_status(null);
		}
		
		if(getStrEmployeType() !=null && !getStrEmployeType().equals("")) {
			setF_employeType(getStrEmployeType().split(","));
//			System.out.println("====>"+getF_employeType().length);
		} else {
			setF_employeType(null);
		}

		if(getStrGrade() !=null && !getStrGrade().equals("")) {
			setF_grade(getStrGrade().split(","));
		} else {
			setF_grade(null);
		}
		
		if(getStrEducation() != null && !getStrEducation().equals("")) {
			setF_education(getStrEducation().split(","));
		} else {
			setF_education(null);
		}
		
		
		if(f_level!=null) {
			String level_id ="";
			for (int i = 0; i < f_level.length; i++) {
				if(i==0) {
					level_id = f_level[i];
					level_id.concat(f_level[i]);
				} else {
					level_id =level_id+","+f_level[i];
				}
			}
			gradeList = new FillGrade(request).fillGrade(level_id,getF_org());
		} else {
			gradeList = new FillGrade(request).fillGradebyorganisation(getF_org());
		}
		if(strAction!=null && (strAction.equalsIgnoreCase("EmployeeReport.action") || strAction.equalsIgnoreCase("EmployeeRReport.action"))) {
			request.setAttribute(TITLE, TViewEmployee);
			if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(NEW_JOINEES_ALERT)) {
				updateUserAlerts();
			}
		} else if(strAction!=null && (strAction.equalsIgnoreCase("ExEmployeeReport.action"))) {
			
			request.setAttribute(TITLE, TViewExEmployee);
		} else if(strAction!=null && (strAction.equalsIgnoreCase("PendingEmployeeReport.action"))) {
			request.setAttribute(TITLE, TViewPendingEmployee);
			if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(NEW_JOINEE_PENDING_ALERT)) {
				updateUserAlerts();
			}
		}
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		if(getAfParam()==null){
			setAfParam("1");
		}

		setStartDateAndEndDate(uF);
		
		viewEmployee(alphaValue, uF);
		return loadEmployee(uF);
		
	}

	//Created By Dattatray 09-6-2022
	private void loadPageVisitAuditTrail(CommonFunctions CF,UtilityFunctions uF) {
		Connection con=null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			StringBuilder builder = new StringBuilder();
			builder.append("Filter:");
			builder.append("\nOrganization:"+getF_org());
			builder.append("\nLocation:"+getStrLocation());	
			builder.append("\nDepartment:"+getStrDepartment());	
			builder.append("\nService:"+getStrSbu());	
			builder.append("\nLevel:"+StringUtils.join(f_level));	
			builder.append("\nGrade:"+getStrGrade());	
			builder.append("\nEmployee Type:"+getStrEmployeType());	
			if(strAction!=null && (strAction.equalsIgnoreCase("EmployeeReport.action") || strAction.equalsIgnoreCase("EmployeeRReport.action"))) {
				builder.append("\nStatus:"+getStrStatus());	
				if(getAdvanceFilter()!=null && getAdvanceFilter().equals("AF") && getAfParam()!=null && getAfParam().equals("1")) {
					builder.append("\nBirth Date from:"+getStrBirthStartDate()+" - To:"+getStrBirthEndDate());	
					builder.append("\nJoining Date From:"+getStrJoiningStartDate()+" - To:"+getStrJoiningEndDate());	
					builder.append("\nTerminated Date From:"+getStrTerminateStartDate()+" - To:"+getStrTerminateEndDate());	
				}
			}
			
			CF.pageVisitAuditTrail(con, CF,uF, strSessionEmpId, strAction, strBaseUserType, builder.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}
	
//	private void updateUserAlerts1() {
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
//		try {
//			con = db.makeConnection(con);
//			String strDomain = request.getServerName().split("\\.")[0];
//			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//			userAlerts.setStrDomain(strDomain);
//			userAlerts.setStrEmpId(""+nEmpId);
//			userAlerts.set_type(EMP_TERMINATED_ALERT);
//			userAlerts.setStatus(UPDATE_ALERT);
//			Thread t = new Thread(userAlerts);
//			t.run();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
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
			if(strAction!=null && (strAction.equalsIgnoreCase("EmployeeReport.action") || strAction.equalsIgnoreCase("EmployeeRReport.action"))) {
				userAlerts.set_type(NEW_JOINEES_ALERT);
			} else if(strAction!=null && (strAction.equalsIgnoreCase("PendingEmployeeReport.action"))) {
				userAlerts.set_type(NEW_JOINEE_PENDING_ALERT);
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
	
	
	private void setStartDateAndEndDate(UtilityFunctions uF) {
		
//		System.out.println("Date"+getFromDate()+" "+getToDate());
		// updated by suraj on 17/6/17 start to get the advance filter data 
		if((getFromDate()==null || getFromDate().equals("")) || (getToDate()==null || getToDate().equals(""))){
		
			Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			int min = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
			int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			
			calendar.set(Calendar.DAY_OF_MONTH, min);
			Date fstdt = calendar.getTime();
			String firstdate = sdf.format(fstdt);
			calendar.set(Calendar.DAY_OF_MONTH, max);
			Date lstdt = calendar.getTime();
			String lastdate = sdf.format(lstdt);
			
			if((getStrBirthStartDate()==null || getStrBirthStartDate().equals("")) || (getStrBirthEndDate()==null || getStrBirthEndDate().equals(""))){
				setStrBirthStartDate(firstdate);
				setStrBirthEndDate(lastdate);
			}
			if((getStrJoiningStartDate()==null || getStrJoiningStartDate().equals("")) || (getStrJoiningEndDate()==null || getStrJoiningEndDate().equals(""))){
				setStrJoiningStartDate(firstdate);
				setStrJoiningEndDate(lastdate);
			}
			if((getStrTerminateStartDate()==null || getStrTerminateStartDate().equals("")) || (getStrTerminateEndDate()==null || getStrTerminateEndDate().equals(""))){
				setStrTerminateStartDate(firstdate);
				setStrTerminateEndDate(lastdate);
			}	
		} else{
			switch (Integer.valueOf(getAfParam())) {
				case 1 :
					setStrBirthStartDate(getFromDate());
					setStrBirthEndDate(getToDate());
					break;
				case 2:
					setStrJoiningStartDate(getFromDate());
					setStrJoiningEndDate(getToDate());
					break;
				case 3:
					setStrTerminateStartDate(getFromDate());
					setStrTerminateEndDate(getToDate());
					break;
				default :
					
					
					break;
			}
		}
		// updated by suraj on 17/6/17 end
			
 }

	public String loadEmployee(UtilityFunctions uF) {
		
		empStatusList = new FillEmployeeStatus().fillEmployeeStatus();
//		statusList = new FillActivity(request).fillActivity(true);
		
		departmentList = new FillDepartment(request).fillDepartmentByOrgOrAccessOrg(uF.parseToInt(getF_org()), (String)session.getAttribute(ORG_ACCESS));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
		} else {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		
		int i;
		
		if(empStatusList.size()!=0) {
			String statusName, statusId;
			StringBuilder sbStatusList = new StringBuilder();
			sbStatusList.append("{");
			for (i = 0; i < empStatusList.size() - 1; i++) {
				statusId = (empStatusList.get(i)).getStatusId();
				statusName = empStatusList.get(i).getStatusName();
				sbStatusList.append("\"" + statusId + "\":\"" + statusName + "\",");
			}
			statusId = (empStatusList.get(i)).getStatusId();
			statusName = empStatusList.get(i).getStatusName();
			sbStatusList.append("\"" + statusId + "\":\"" + statusName + "\"}");
			request.setAttribute("sbStatusList", sbStatusList.toString());
		}
		eduList=new FillEducation(request).fillEducationWithId();
		employementTypeList = new FillEmploymentType().fillEmploymentType(request);
		getSelectedFilter(uF);
		
		return LOAD;
	}

	public String viewEmployee(String strEmpName, UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null, pst_sid = null;
		ResultSet rs = null, rs_sid = null;
		Database db = new Database();
		db.setRequest(request);
//		EncryptionUtility eU = new EncryptionUtility();

		try {
			List<List<String>> al = new ArrayList<List<String>>();
			List<UserInfo> alNew = new ArrayList<UserInfo>();
			List<String> alInner = new ArrayList<String>();
			Map<String, List<String>> hm = new HashMap<String, List<String>>();
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			Map hmWorkLocationMap = CF.getWorkLocationMap(con); 
			List<String> alUnApprovedEmplyees  = null;
			String empIds = null;
			if(getAdvanceFilter()!=null && getAdvanceFilter().equals("AF") && getAfParam()!=null && getAfParam().equals("1")){
				if(getStrBirthStartDate()!=null && !getStrBirthStartDate().equals("") && getStrBirthEndDate()!=null && !getStrBirthEndDate().equals(""))
				empIds = getBirthDateEmpData(con,uF);
			} else {
				empIds="0";
			}
			
			if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(ACCOUNTANT) ||  strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(RECRUITER) )) {
				if(strAction!=null && (strAction.equalsIgnoreCase("EmployeeReport.action") || strAction.equalsIgnoreCase("EmployeeRReport.action"))) {
					setPage("Live");
						
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("select * from(Select * from ( Select * from ( Select * from ( Select * from employee_personal_details epd, employee_official_details eod WHERE epd.is_alive = ? and eod.emp_id >0 and epd.emp_per_id=eod.emp_id and approved_flag = ? ");

					if(getAdvanceFilter()!=null && getAdvanceFilter().equals("AF")) {
						if(getAfParam()!=null && getAfParam().equals("1")) {
							sbQuery.append(" and epd.emp_per_id in ("+empIds+")");
//							sbQuery.append(" and to_date(emp_date_of_birth::text,'"+DBDATE+"') between '"+uF.getDateFormat(getStrBirthStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrBirthEndDate(), DATE_FORMAT, DBDATE)+"'");
						} else if(getAfParam()!=null && getAfParam().equals("2")) {
							sbQuery.append(" and to_date(joining_date::text,'"+DBDATE+"') between '"+uF.getDateFormat(getStrJoiningStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrJoiningEndDate(), DATE_FORMAT, DBDATE)+"'");
						} else if(getAfParam()!=null && getAfParam().equals("3")) {
							sbQuery.append(" and epd.emp_status='TERMINATED' and to_date(employment_end_date::text,'"+DBDATE+"') between '"+uF.getDateFormat(getStrTerminateStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrTerminateEndDate(), DATE_FORMAT, DBDATE)+"'");
						} else if(getAfParam()!=null && getAfParam().equals("4") && getF_education()!=null && !getF_education().equals("") ) {
							sbQuery.append(" and epd.emp_per_id in (select emp_id from education_details where education_id in (" + StringUtils.join(getF_education(), ",") +") ) ");
						}
					}
					
					if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0) {
			            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
			        } else {
		            	 if(getF_level()!=null && getF_level().length>0){
		                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
		                 }
		            	 if(getF_grade()!=null && getF_grade().length>0){
		                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
		                 }
					}
					if (getF_employeType() != null && getF_employeType().length > 0) {
						sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
					}
					
					if(getF_status()!=null && getF_status().length>0) {
						sbQuery.append(" and emp_status  in ("+getDataFromArray(getF_status())+") ");
					}
					if(getF_level()!=null && getF_level().length>0){
		                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
		            }
		            if(getF_department()!=null && getF_department().length>0){
		                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
		            }
		            
		            if(getF_service()!=null && getF_service().length>0){
		                sbQuery.append(" and (");
		                for(int i=0; i<getF_service().length; i++){
		                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
		                    
		                    if(i<getF_service().length-1){
		                        sbQuery.append(" OR "); 
		                    }
		                }
		                sbQuery.append(" ) ");
		            } 
		            
		            if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
						sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					}
		            
		            if(uF.parseToInt(getF_org())>0) {
						sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
					} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					}
		            sbQuery.append(" order by empcode, emp_status, emp_fname,emp_lname) ast left join state s on ast.emp_state_id = s.state_id ) aco left join country co on aco.emp_country_id = co.country_id ) aud left join user_details ud on aud.emp_id = ud.emp_id) pr LEFT JOIN city cc on pr.emp_city_id=cast(cc.city_id as text)");
		       //===start parvez date: 28-10-2022===
		            sbQuery.append("order by emp_fname,emp_lname");
		       //=== end parvez date: 28-10-2022===      
		            pst = con.prepareStatement(sbQuery.toString());
					if(getAdvanceFilter()!=null && getAdvanceFilter().equals("AF") && getAfParam()!=null && getAfParam().equals("3") ) {
						pst.setBoolean(1, false);
					} else {
						pst.setBoolean(1, true);
					}
					pst.setBoolean(2, true);
//					System.out.println("pst in workring for GHR HR REC CEO CFO ACC ===>> " + pst);
				} else if(strAction!=null && strAction.equalsIgnoreCase("PendingEmployeeReport.action")) {
					setPage("Pending");
					alUnApprovedEmplyees = new ArrayList<String>();
					pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and approved_flag = false and org_id > 0");
					rs = pst.executeQuery();
					while(rs.next()){
						alUnApprovedEmplyees.add(rs.getString("emp_per_id"));
					}
//					System.out.println("alUnApprovedEmplyees ===>> " + alUnApprovedEmplyees);
					rs.close();
					pst.close();
					
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("select * from employee_personal_details epd left join employee_official_details eod on eod.emp_id = epd.emp_per_id where approved_flag=? and is_alive=? and emp_id > 0 ");
					if(getF_status()!=null && getF_status().length>0){
						sbQuery.append(" and emp_status in ("+getDataFromArray(getF_status())+") ");
					}
					if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0) {
			            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
		            } else {
		            	 if(getF_level()!=null && getF_level().length>0){
		                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
		                 }
		            	 if(getF_grade()!=null && getF_grade().length>0){
		                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
		                 }
					}
					if (getF_employeType() != null && getF_employeType().length > 0) {
						sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
					}
		            if(getF_department()!=null && getF_department().length>0){
		                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
		            }
		            
		            if(getF_service()!=null && getF_service().length>0){
		                sbQuery.append(" and (");
		                for(int i=0; i<getF_service().length; i++){
		                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
		                    
		                    if(i<getF_service().length-1){
		                        sbQuery.append(" OR "); 
		                    }
		                }
		                sbQuery.append(" ) ");
		            }
		            
		            if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		            }
					if(uF.parseToInt(getF_org())>0) {
						sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
					}
					
					else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
						sbQuery.append(" and added_by = "+uF.parseToInt(strSessionEmpId)+" ");
					}
					
//					sbQuery.append(" order by empcode, emp_fname,emp_lname ");
					sbQuery.append(" order by emp_fname,emp_lname ");
					
					pst = con.prepareStatement(sbQuery.toString());
					pst.setBoolean(1, false);
					pst.setBoolean(2, false);
//					System.out.println("pst in pending for GHR HR REC CEO CFO ACC ===>> " + pst);
				} else {
					setPage("Ex");
					
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("select * from employee_personal_details epd left join employee_official_details eod on eod.emp_id = epd.emp_per_id where approved_flag = ? and is_alive = ? ");

					if(getF_status()!=null && getF_status().length>0) {
						sbQuery.append(" and emp_status  in ("+getDataFromArray(getF_status())+") ");
					}
					if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0) {
			            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
			            } else {
			            	if(getF_level()!=null && getF_level().length>0){
			                    sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
			                }
			            	if(getF_grade()!=null && getF_grade().length>0){
			                    sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
			                }
						}
					if (getF_employeType() != null && getF_employeType().length > 0) {
						sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
					}
		            if(getF_department()!=null && getF_department().length>0) {
		                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
		            }
		            
		            if(getF_service()!=null && getF_service().length>0) {
		                sbQuery.append(" and (");
		                for(int i=0; i<getF_service().length; i++) {
		                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
		                    
		                    if(i<getF_service().length-1){
		                        sbQuery.append(" OR "); 
		                    }
		                }
		                sbQuery.append(" ) ");
		            }
		            
		            if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
						sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					}
					
					if(uF.parseToInt(getF_org())>0) {
						sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
					} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					}
//					sbQuery.append(" order by empcode, emp_fname,emp_lname ");
					sbQuery.append(" order by emp_fname,emp_lname,empcode ");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setBoolean(1, true);
					pst.setBoolean(2, false);
//					System.out.println("pst in EX-EMP for GHR HR REC CEO CFO ACC ===>> " + pst);
				}
				
			} else if (strUserType != null	&& (strUserType.equalsIgnoreCase(MANAGER) || strUserType.equalsIgnoreCase(EMPLOYEE))) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from(Select * from ( Select * from ( Select * from ( Select * from employee_personal_details epd, " +
					"employee_official_details eod WHERE epd.emp_per_id=eod.emp_id ");
				if(strAction!=null && (strAction.equalsIgnoreCase("EmployeeReport.action") || strAction.equalsIgnoreCase("EmployeeRReport.action"))) {
					setPage("Live");
					sbQuery.append(" and epd.is_alive=true and approved_flag=true ");
				} else if(strAction!=null && strAction.equalsIgnoreCase("PendingEmployeeReport.action")) {
					setPage("Pending");
					sbQuery.append(" approved_flag=false and org_id>0 ");
				} else {
					setPage("Ex");
					sbQuery.append(" and epd.is_alive=false and approved_flag=true ");
				}
				if(strBaseUserType != null && strBaseUserType.equals(HOD) && strUserType != null && strUserType.equals(MANAGER)) {
					sbQuery.append(" and (supervisor_emp_id = "+uF.parseToInt(strSessionEmpId)+" or hod_emp_id = "+uF.parseToInt(strSessionEmpId)+" or eod.emp_id = "+uF.parseToInt(strSessionEmpId)+")");
				} else if(strUserType != null && strUserType.equals(MANAGER)) {
					sbQuery.append(" and (supervisor_emp_id = "+uF.parseToInt(strSessionEmpId)+" or eod.emp_id = "+uF.parseToInt(strSessionEmpId)+")");
				} else {
					sbQuery.append(" and eod.emp_id = "+uF.parseToInt(strSessionEmpId));
				}
				sbQuery.append(" order by emp_fname,emp_lname) ast left join state s on ast.emp_state_id = s.state_id ) aco left join country co on aco.emp_country_id = co.country_id) aud left join user_details ud on aud.emp_id = ud.emp_id) pr LEFT JOIN city cc on pr.emp_city_id=cast(cc.city_id as text) ");
				
				sbQuery.append("order by emp_fname,emp_lname");
				
				pst = con.prepareStatement(sbQuery.toString());
//				pst.setBoolean(1, true);
//				pst.setBoolean(2, true);
//				System.out.println("pst in manager/ employee ===>> " + pst);
			} else {
				return ACCESS_DENIED;
			}
//			System.out.println("pst1 =======> " + pst); 
			rs = pst.executeQuery();
			String strEmpId = null;
//			EncryptionUtils encryption = new EncryptionUtils();// Created By Dattatray Date : 20-July-2021 Note : Encryption
			if(strAction!=null && (strAction.equalsIgnoreCase("EmployeeReport.action") || strAction.equalsIgnoreCase("EmployeeRReport.action"))) {
				Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
				Map<String, String> hmEmpDepartment = CF.getEmpDepartmentMap(con);
				Map<String, String> hmDeptMap = CF.getDeptMap(con);
				Map<String, String> hmEmpWlocationMap = CF.getEmpWlocationMap(con);
				Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con);
				while (rs.next()) {
					if(rs.getInt("emp_per_id")<=0) {
						continue;
					}
					alInner = new ArrayList<String>();
					strEmpId = rs.getString("emp_per_id");
					alInner.add(rs.getString("emp_per_id")); //0
					//updated by kalpana on 4 nov 2016
//					...start...
					String strContractor = "";
					if(uF.parseToInt(rs.getString("emp_contractor")) == 2) {
						strContractor = " (C)";
					}
					alInner.add(rs.getString("empcode")+strContractor); //1
					//....end...
					StringBuilder sbEmpName = new StringBuilder();
					sbEmpName.append(uF.showData(rs.getString("emp_fname"), ""));
					if(rs.getString("emp_mname") != null && !rs.getString("emp_mname").equals("")) {
						sbEmpName.append(" " + uF.showData(rs.getString("emp_mname"), ""));
					}
					sbEmpName.append(" " + uF.showData(rs.getString("emp_lname"), ""));
					
					alInner.add(sbEmpName.toString()); //2
//					alInner.add(uF.showData(rs.getString("emp_mname"), ""));
//					alInner.add(rs.getString("emp_lname"));
					alInner.add(""); //3
					alInner.add(""); //4
					
					alInner.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat())); //5
					
					Map<String, String> hmWLocation = (Map)hmWorkLocationMap.get(rs.getString("wlocation_id"));
					if(hmWLocation==null)hmWLocation=new HashMap<String, String>();
					
					alInner.add(uF.showData(hmEmpCodeDesig.get(strEmpId),"")); //6
					alInner.add(uF.showData(hmDeptMap.get(hmEmpDepartment.get(strEmpId)), "")); //7
					
					String wlocation = hmWLocation.get("WL_NAME");
					
					alInner.add(uF.showData(wlocation, "")); //8
					alInner.add(uF.showData(rs.getString("emp_status"), "")); //9
					alInner.add(uF.showData(uF.stringMapping(rs.getString("emptype")), "")); //10
					
					if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(RECRUITER))) {
//						if(rs.getInt("emp_per_id") == 683) {
//						System.out.println(strEmpId + " ===>> " + rs.getBoolean("is_one_step"));
//						}
						if(rs.getBoolean("is_one_step")) {
//							String encodeEmpId = eU.encode(strEmpId);
							String encodeEmpId = strEmpId;
//							System.out.println("encodeEmpId ===>> " + encodeEmpId);
//							System.out.println("encryption.encrypt(encodeEmpId) ===>> " + encryption.encrypt(encodeEmpId));
							// Created By Dattatray Date : 20-July-2021 Note : empId Encryption encryption.encrypt(encodeEmpId)
							alInner.add("<a class=\"factsheet\" style=\"float: left; position: inherit;\" href=\"MyProfile.action?fromPage=P&empId=" + encodeEmpId + "\" > </a>" +
								"<a class=\"fa fa-edit\" style=\"padding: 0px 0px 0px 2px;\" href=\"AddEmployeeInOneStep.action?operation=U&empId=" + encodeEmpId + "\" > </a>" +
								"<a class=\"fa fa-trash\" style=\"float:right; padding: 0px;\" href=\"javascript:void(0)\" onclick=\"(confirm('Are you sure, you want to delete this employee?') ? window.location='AddEmployeeInOneStep.action?pageType=WE&operation=D&empId="+encodeEmpId+"' : '')\"> </a>"); //11
//							System.out.println(strEmpId + " in if ===>> " + rs.getBoolean("is_one_step"));
						} else {
//							String encodeEmpId = eU.encode(strEmpId);
							String encodeEmpId = strEmpId;
							// Created By Dattatray Date : 20-July-2021 Note : empId Encryption
							alInner.add("<a class=\"factsheet\" href=\"MyProfile.action?fromPage=P&empId="+encodeEmpId+"\" > </a>"); //11
						}
//					} else if((uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("supervisor_emp_id")) || uF.parseToInt(strSessionEmpId) == uF.parseToInt(rs.getString("emp_per_id"))) && strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strUserType.equalsIgnoreCase(HOD) || strUserType.equalsIgnoreCase(CEO))) {
					} else if(strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strUserType.equalsIgnoreCase(HOD) || strUserType.equalsIgnoreCase(CEO))) {
//						String encodeEmpId = eU.encode(rs.getString("emp_per_id"));
						String encodeEmpId = rs.getString("emp_per_id");
						// Created By Dattatray Date : 20-July-2021 Note : empId Encryption
						alInner.add("<a class=\"factsheet\" href=\"MyProfile.action?empId=" + encodeEmpId + "\" > </a>");//11
						// End : Dattatray Date : 20-July-2021 Note : EncodedEmpId
					} else {
						alInner.add(""); //11
					}
					
					if(getAdvanceFilter()!=null && getAdvanceFilter().equals("AF")) {
						if(getAfParam()!=null && getAfParam().equals("1")) {
							alInner.add(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, CF.getStrReportDateFormat())); //12
						} else if(getAfParam()!=null && getAfParam().equals("2")) {
							alInner.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat())); //12
						} else if(getAfParam()!=null && getAfParam().equals("3")) {
							alInner.add(uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, CF.getStrReportDateFormat())); //12
						} else {
							alInner.add(""); //12
						}
					} else {
						alInner.add(""); //12
					}
					alInner.add(uF.showData(rs.getString("emp_image"), "")); //13
					
					al.add(alInner);
					hm.put(rs.getString("emp_off_id"), alInner);
				}
				rs.close();
				pst.close();

				boolean isEmpLimit = false;
//				System.out.println("al.size() ===>> " + al.size() + "uF.parseToInt(CF.getStrMaxEmployee()) ===>> " + uF.parseToInt(CF.getStrMaxEmployee()));
				if(al != null && al.size()>=uF.parseToInt(CF.getStrMaxEmployee())) {
					isEmpLimit = true;
				}
				request.setAttribute("isEmpLimit", ""+isEmpLimit);
				request.setAttribute("strEmpLimit", CF.getStrMaxEmployee());
			} else if(strAction!=null && strAction.equalsIgnoreCase("ExEmployeeReport.action")) {
				while (rs.next()) {
					alInner = new ArrayList<String>();
					
					strEmpId = rs.getString("emp_per_id");
					alInner.add(rs.getString("emp_per_id"));
					StringBuilder sbEmpName = new StringBuilder();
					sbEmpName.append(uF.showData(rs.getString("emp_fname"), ""));
					if(rs.getString("emp_mname") != null && !rs.getString("emp_mname").equals("")) {
						sbEmpName.append(" " + uF.showData(rs.getString("emp_mname"), ""));
					}
					sbEmpName.append(" " + uF.showData(rs.getString("emp_lname"), ""));
					
					alInner.add(sbEmpName.toString());
//					alInner.add(uF.showData(rs.getString("emp_mname"), ""));
//					alInner.add(rs.getString("emp_lname"));
					alInner.add("");
					alInner.add("");
					alInner.add(rs.getString("emp_email"));
					alInner.add(uF.showData(rs.getString("emp_contactno_mob"),""));
					
//					alInner.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));
//					String encodeEmpId = eU.encode(strEmpId);
					String encodeEmpId = strEmpId;
					// Created By Dattatray Date : 20-July-2021 Note : empId Encryption
					alInner.add("<a class=\"factsheet\" title=\"profile\" href=\"MyProfile.action?empId=" + encodeEmpId + "\" > </a>");
					alInner.add(uF.showData(rs.getString("emp_image"), ""));

					al.add(alInner);
					
					hm.put(rs.getString("emp_off_id"), alInner);
				}
				rs.close();
				pst.close();
				
			} else {
				List<List<Object>> alReminderDetails = CF.getReminderDetailsList(con);
				int count=0;
				while (rs.next()) {
					alInner = new ArrayList<String>();
					
					strEmpId = rs.getString("emp_per_id");
					alInner.add(rs.getString("emp_per_id"));
					StringBuilder sbEmpName = new StringBuilder();
					sbEmpName.append(uF.showData(rs.getString("emp_fname"), ""));
					if(rs.getString("emp_mname") != null && !rs.getString("emp_mname").equals("")) {
						sbEmpName.append(" " + uF.showData(rs.getString("emp_mname"), ""));
					}
					sbEmpName.append(" " + uF.showData(rs.getString("emp_lname"), ""));
					
					alInner.add(sbEmpName.toString());
					alInner.add("");
					alInner.add("");
					alInner.add(rs.getString("emp_email"));
					alInner.add(uF.showData(rs.getString("emp_contactno_mob"),""));
					
					if(rs.getBoolean("emp_filled_flag")) {
						String encodeEmpId = strEmpId;
						// Created By Dattatray Date : 20-July-2021 Note : empId Encryption
						alInner.add("<a class=\"factsheet_live\" title=\"profile\" href=\"MyProfile.action?empId=" + encodeEmpId + "\" > </a>"
							+ "<a class=\"fa fa-trash\" style=\"margin-left: 4px;\" href=\"javascript:void(0)\" onclick=\"(confirm('Are you sure, Do you want to delete this employee?') ? window.location='AddEmployeeInOneStep.action?pageType=PE&operation=D&empId="+encodeEmpId+"' : '')\"> </a>"+
							((alUnApprovedEmplyees.contains(strEmpId)) ? 
							"<div id=\"myDiv\" style=\"float:left;padding-left:10px;width:17px\"> <a href=\"javascript:void(0)\" title=\"Approve\" onclick=\"(confirm('Are you sure, Do you want to approve this employee?')?getContent('myDiv','UpdateRequest.action?S=1&T=EPD&RID=" + strEmpId + "'):'')\" > <i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\"></i> </a> </div>" : "") 
						);
						
					} else {
						if(strAction!=null && strAction.equalsIgnoreCase("PendingEmployeeReport.action")) {
							Date latestdate = null;
							if(alReminderDetails.size()!=0) {
								for(int i=0; i<alReminderDetails.size(); i++) {
									List <Object> innerList = alReminderDetails.get(i);
									if(innerList.get(2).equals(strEmpId)) {
										latestdate = (Date) innerList.get(1);
									}
								}
								
								Date prevDate = uF.getPrevDate(CF.getStrTimeZone(), 1);
								if(latestdate!=null && uF.isDateBetween(prevDate, uF.getCurrentDate(CF.getStrTimeZone()), latestdate)) {
//									String encodeEmpId = eU.encode(strEmpId);
									String encodeEmpId = strEmpId;
									// Created By Dattatray Date : 20-July-2021 Note : empId Encryption
									alInner.add("<a class=\"factsheet_cancel\" href=\"MyProfile.action?empId=" + encodeEmpId + "\" > </a>");
								} else {
//									String encodeEmpId = eU.encode(strEmpId);
									String encodeEmpId = strEmpId;
									// Created By Dattatray Date : 20-July-2021 Note : empId Encryption
									alInner.add("<a class=\"factsheet_cancel\" title=\"profile\" href=\"MyProfile.action?empId=" + encodeEmpId + "\" > </a>" 
									+ "<a class=\"fa fa-trash\" style=\"margin-left: 4px;\" href=\"javascript:void(0)\" onclick=\"(confirm('Are you sure, you want to delete this employee?') ? window.location='AddEmployeeInOneStep.action?pageType=PE&operation=D&empId="+encodeEmpId+"' : '')\"> </a>"+
									"<div id=\"myDiv_"+count+"\"><a class=\"factsheet_cancel_remind\" title=\"Send Reminder\" href=\"javascript:void(0)\"  onclick=\"(confirm('Are you sure, Do you want to send reminder?')?getContent('myDiv_"+count+"', 'SendReminder.action?strEmpId="+strEmpId+"&strNotificationId="+N_NEW_EMPLOYEE_JOINING+"'):'')\"> </a></div>");
								}
							} else {
//								String encodeEmpId = eU.encode(strEmpId);
								String encodeEmpId = strEmpId;
								// Created By Dattatray Date : 20-July-2021 Note : empId Encryption
								alInner.add("<a class=\"factsheet_cancel\" title=\"profile\" href=\"MyProfile.action?empId=" + encodeEmpId + "\" > </a>"  
								+ "<a class=\"fa fa-trash\" style=\"margin-left: 4px;\" href=\"javascript:void(0)\" onclick=\"(confirm('Are you sure, you want to delete this employee?') ? window.location='AddEmployeeInOneStep.action?pageType=PE&operation=D&empId="+encodeEmpId+"' : '')\"> </a>"+
								"<a class=\"factsheet_cancel_remind\" title=\"reminder\" href=\"AddEmployeeMode.action?fname="+rs.getString("emp_fname")+"&lname="+rs.getString("emp_lname")+"&email="+rs.getString("emp_email")+"&notification=reminder&empId=" + encodeEmpId + "\" > </a>");
							}
						} else {
//							String encodeEmpId = eU.encode(strEmpId);
							String encodeEmpId = strEmpId;
							// Created By Dattatray Date : 20-July-2021 Note : empId Encryption
							alInner.add("<a class=\"factsheet_cancel\" href=\"MyProfile.action?empId=" + encodeEmpId + "\" > </a>");
						}
					}
					alInner.add(uF.showData(rs.getString("emp_image"), ""));

					al.add(alInner);
					hm.put(rs.getString("emp_off_id"), alInner);
					count++;
				}
//				System.out.println("al pending ===>> " + al);
				rs.close();
				pst.close();
			}
			request.setAttribute("reportList", al);
			
			pst = con.prepareStatement("select * from education_details"); 
			rs = pst.executeQuery();
			Map<String, String> hmEducation=new HashMap<String, String>();
			Map<String, String> hmDegreeName = CF.getDegreeNameMap(con);
			while (rs.next()) {
				if(rs.getString("education_id")!=null && !rs.getString("education_id").trim().equals("")) {
					String degree_name=hmEducation.get(rs.getString("emp_id"));
					if(degree_name==null) {
						hmEducation.put(rs.getString("emp_id"), hmDegreeName.get(rs.getString("education_id")));
					} else {
						hmEducation.put(rs.getString("emp_id"),degree_name+", "+ hmDegreeName.get(rs.getString("education_id")));
					}
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEducation", hmEducation);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeResultSet(rs_sid);
			db.closeStatements(pst);
			db.closeStatements(pst_sid);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	
	private String getDataFromArray(String[] strVal) {
		StringBuilder sb=new StringBuilder();
		for(int i=0;strVal!=null && i<strVal.length;i++){
			if(i==0){
				sb.append("'"+strVal[i]+"'");
				
			}else{
				sb.append(",'"+strVal[i]+"'");
			}
			if(strVal[i].equals("PROBATION")){
				sb.append(",'ACTIVE'");
			}
		}
		
		return sb.toString();
	}

	private String getBirthDateEmpData(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder sbEmpIds=null;
		try {
			
			StringBuilder sbQuery=new StringBuilder();
			
			/*sbQuery.append("select * from(select * ,years::text||'-'||months||'-'||days as virdate,years1::text||'-'||months||'-'||days as virdate1 " +
					" from(Select *,EXTRACT(day FROM  emp_date_of_birth) as days,EXTRACT(month FROM  emp_date_of_birth) as months,");
			sbQuery.append(" EXTRACT(year FROM  to_date('"+uF.getDateFormat(getStrBirthStartDate(), DATE_FORMAT, DBDATE)+"','yyyy-MM-dd'))as years," +
					" EXTRACT(year FROM  to_date('"+uF.getDateFormat(getStrBirthEndDate(), DATE_FORMAT, DBDATE)+"','yyyy-MM-dd'))as years1 from employee_personal_details epd,");
			sbQuery.append(" employee_official_details eod WHERE epd.is_alive = true and epd.emp_per_id=eod.emp_id and approved_flag = true)a) b"); 
			sbQuery.append(" where to_date(virdate,'yyyy-MM-dd') between '"+uF.getDateFormat(getStrBirthStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrBirthEndDate(), DATE_FORMAT, DBDATE)+"' ");
			sbQuery.append(" or to_date(virdate1,'yyyy-MM-dd') between '"+uF.getDateFormat(getStrBirthStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrBirthEndDate(), DATE_FORMAT, DBDATE)+"' order by months,days");	
			pst = con.prepareStatement(sbQuery.toString());
			System.out.println("EmpR/1011---pst===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				if(sbEmpIds==null){
					sbEmpIds=new StringBuilder();
					sbEmpIds.append(rs.getString("emp_id"));
				}else{
					sbEmpIds.append(","+rs.getString("emp_id"));
				}

			}
			rs.close();
			pst.close();*/
			
	//===start parvez date: 06-10-2022===
			
			Date birthStartDate = uF.getDateFormatUtil(getStrBirthStartDate(), DATE_FORMAT);
			Date birthEndDate = uF.getDateFormatUtil(getStrBirthEndDate(), DATE_FORMAT);
			sbQuery.append("select * ,years::text||'-'||months||'-'||days as virdate,years1::text||'-'||months||'-'||days as virdate1 " +
					" from(Select *,EXTRACT(day FROM  emp_date_of_birth) as days,EXTRACT(month FROM  emp_date_of_birth) as months,");
			sbQuery.append(" EXTRACT(year FROM  to_date('"+uF.getDateFormat(getStrBirthStartDate(), DATE_FORMAT, DBDATE)+"','yyyy-MM-dd'))as years," +
					" EXTRACT(year FROM  to_date('"+uF.getDateFormat(getStrBirthEndDate(), DATE_FORMAT, DBDATE)+"','yyyy-MM-dd'))as years1 from employee_personal_details epd,");
			sbQuery.append(" employee_official_details eod WHERE epd.is_alive = true and epd.emp_per_id=eod.emp_id and approved_flag = true)a order by months,days"); 
				
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("EmpR/1011---pst===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				
				if(rs.getString("virdate") !=null || rs.getString("virdate1") != null){
					Date d1 = uF.getDateFormatUtil(rs.getString("virdate"), DBDATE);
					Date d2 = uF.getDateFormatUtil(rs.getString("virdate1"), DBDATE);
					if(((d1.after(birthStartDate) && d1.before(birthEndDate)) || (d1.equals(birthStartDate) || d1.equals(birthEndDate))) 
							||((d2.after(birthStartDate) && d2.before(birthEndDate)) || (d2.equals(birthStartDate) || d2.equals(birthEndDate)))){
						if(sbEmpIds==null){
							sbEmpIds=new StringBuilder();
							sbEmpIds.append(rs.getString("emp_id"));
						}else{
							sbEmpIds.append(","+rs.getString("emp_id"));
						}
					}
				}
			}
			rs.close();
			pst.close();
	//===end parvez date: 06-10-2022===		
			

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
		if(sbEmpIds==null){
			sbEmpIds=new StringBuilder("0");
		}
		return sbEmpIds.toString();
	}

 private void getSelectedFilter(UtilityFunctions uF) {
		
	Map<String,String> hmFilter=new HashMap<String, String>();
	List<String> alFilter = new ArrayList<String>();

//	System.out.println("getF_service()----"+getF_service());
	alFilter.add("ORGANISATION");
	if(getF_org()!=null)  {
		String strOrg="";
		int k=0;
		for(int i=0;organisationList!=null && i<organisationList.size();i++){
			if(getF_org().equals(organisationList.get(i).getOrgId())) {
				if(k==0) {
					strOrg=organisationList.get(i).getOrgName();
				} else {
					strOrg+=", "+organisationList.get(i).getOrgName();
				}
				k++;
			}
		}
		if(strOrg!=null && !strOrg.equals("")) {
			hmFilter.put("ORGANISATION", strOrg);
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
	} else {
		hmFilter.put("ORGANISATION", "All Organisation");
	}
	
	alFilter.add("LOCATION");
	if(getF_strWLocation()!=null) {
		String strLocation="";
		int k=0;
		for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
			for(int j=0;j<getF_strWLocation().length;j++) {
				if(getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
					if(k==0) {
						strLocation=wLocationList.get(i).getwLocationName();
					} else {
						strLocation+=", "+wLocationList.get(i).getwLocationName();
					}
					k++;
				}
			}
		}
		if(strLocation!=null && !strLocation.equals("")) {
			hmFilter.put("LOCATION", strLocation);
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}
	} else {
		hmFilter.put("LOCATION", "All Locations");
	}
	
	alFilter.add("DEPARTMENT");
	if(getF_department()!=null) {
		String strDepartment="";
		int k=0;
		for(int i=0;departmentList!=null && i<departmentList.size();i++) {
			for(int j=0;j<getF_department().length;j++) {
				if(getF_department()[j].equals(departmentList.get(i).getDeptId())) {
					if(k==0) {
						strDepartment=departmentList.get(i).getDeptName();
					} else {
						strDepartment+=", "+departmentList.get(i).getDeptName();
					}
					k++;
				}
			}
		}
		if(strDepartment!=null && !strDepartment.equals("")) {
			hmFilter.put("DEPARTMENT", strDepartment);
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}
	} else {
		hmFilter.put("DEPARTMENT", "All Departments");
	}
	
	alFilter.add("SERVICE");
	if(getF_service()!=null) {
		String strService="";
		int k=0;
		for(int i=0;serviceList!=null && i<serviceList.size();i++) {
			for(int j=0;j<getF_service().length;j++) {
				if(getF_service()[j].equals(serviceList.get(i).getServiceId())) {
					if(k==0) {
						strService=serviceList.get(i).getServiceName();
					} else {
						strService+=", "+serviceList.get(i).getServiceName();
					}
					k++;
				}
			}
		}
		if(strService!=null && !strService.equals("")) {
			hmFilter.put("SERVICE", strService);
		} else {
			hmFilter.put("SERVICE", "All SBUs");
		}
	} else {
		hmFilter.put("SERVICE", "All SBUs");
	}
	
	alFilter.add("LEVEL");
	if(getF_level()!=null){
		String strLevel="";
		int k=0;
		for(int i=0;levelList!=null && i<levelList.size();i++){
			for(int j=0;j<getF_level().length;j++){
				if(getF_level()[j].equals(levelList.get(i).getLevelId())){
					if(k==0){
						strLevel=levelList.get(i).getLevelCodeName();
					}else{
						strLevel+=", "+levelList.get(i).getLevelCodeName();
					}
					k++;
				}
			}
		}
		if(strLevel!=null && !strLevel.equals("")){
			hmFilter.put("LEVEL", strLevel);
		}else{
			hmFilter.put("LEVEL", "All Levels");
		}
	}else{
		hmFilter.put("LEVEL", "All Levels");
	}
	
	if(strAction!=null && (strAction.equalsIgnoreCase("EmployeeReport.action") || strAction.equalsIgnoreCase("EmployeeRReport.action"))){
		alFilter.add("STATUS");
		if(getF_status()!=null){
			String strStatus="";
			int k=0;
			for(int i=0;empStatusList!=null && i<empStatusList.size();i++){
				for(int j=0;j<getF_status().length;j++){
					if(getF_status()[j].equals(empStatusList.get(i).getStatusId())){
						if(k==0){
							strStatus=empStatusList.get(i).getStatusName();
						}else{
							strStatus+=", "+empStatusList.get(i).getStatusName();
						}
						k++;
					}
				}
			}
			if(strStatus!=null && !strStatus.equals("")){
				hmFilter.put("STATUS", strStatus);
			}else{
				hmFilter.put("STATUS", "All Status");
			}
		}else{
			hmFilter.put("STATUS", "All Status");
		}
	}
	
	
	alFilter.add("LEVEL");
	if (getF_level() != null) {
		String strLevel = "";
		int k = 0;
		for (int i = 0; levelList != null && i < levelList.size(); i++) {
			for (int j = 0; j < getF_level().length; j++) {
				if (getF_level()[j].equals(levelList.get(i).getLevelId())) {
					if (k == 0) {
						strLevel = levelList.get(i).getLevelCodeName();
					} else {
						strLevel += ", " + levelList.get(i).getLevelCodeName();
					}
					k++;
				}
			}
		}
		if (strLevel != null && !strLevel.equals("")) {
			hmFilter.put("LEVEL", strLevel);
		} else {
			hmFilter.put("LEVEL", "All Levels");
		}
	} else {
		hmFilter.put("LEVEL", "All Levels");
	}
	
	alFilter.add("EMPTYPE");
	if (getF_employeType() != null) {
		String stremptype = "";
		int k = 0;
		for (int i = 0; employementTypeList != null && i < employementTypeList.size(); i++) {
			for (int j = 0; j < getF_employeType().length; j++) {
				if (getF_employeType()[j].equals(employementTypeList.get(i).getEmpTypeId())) {
					if (k == 0) {
						stremptype = employementTypeList.get(i).getEmpTypeName();
					} else {
						stremptype += ", " + employementTypeList.get(i).getEmpTypeName();
					}
					k++;
				}
			}
		}
		if (stremptype != null && !stremptype.equals("")) {
			hmFilter.put("EMPTYPE", stremptype);
		} else {
			hmFilter.put("EMPTYPE", "All Employee Type");
		}
	} else {
		hmFilter.put("EMPTYPE", "All Employee Type");
	}
	
	alFilter.add("GRADE");
	if (getF_grade() != null) {
		String strgrade = "";
		int k = 0;
		for (int i = 0; gradeList != null && i < gradeList.size(); i++) {
			for (int j = 0; j < getF_grade().length; j++) {
				if (getF_grade()[j].equals(gradeList.get(i).getGradeId())) {
					if (k == 0) {
						strgrade = gradeList.get(i).getGradeCode();
					} else {
						strgrade += ", " + gradeList.get(i).getGradeCode();
					}
					k++;
				}
			}
		}
		if (strgrade != null && !strgrade.equals("")) {
			hmFilter.put("GRADE", strgrade);
		} else {
			hmFilter.put("GRADE", "All Grade's");
		}
	} else {
		hmFilter.put("GRADE", "All Grade's");
	}
	
	String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
	request.setAttribute("selectedFilter", selectedFilter);
}
	
	
	String empName;

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public List<FillEmployeeStatus> getEmpStatusList() {
		return empStatusList;
	}

	public void setEmpStatusList(List<FillEmployeeStatus> empStatusList) {
		this.empStatusList = empStatusList;
	}

	
	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getAdvanceFilter() {
		return advanceFilter;
	}

	public void setAdvanceFilter(String advanceFilter) {
		this.advanceFilter = advanceFilter;
	}

	public String getAfParam() {
		return afParam;
	}

	public void setAfParam(String afParam) {
		this.afParam = afParam;
	}

	public String getStrBirthStartDate() {
		return strBirthStartDate;
	}

	public void setStrBirthStartDate(String strBirthStartDate) {
		this.strBirthStartDate = strBirthStartDate;
	}

	public String getStrBirthEndDate() {
		return strBirthEndDate;
	}

	public void setStrBirthEndDate(String strBirthEndDate) {
		this.strBirthEndDate = strBirthEndDate;
	}

	public String getStrJoiningStartDate() {
		return strJoiningStartDate;
	}

	public void setStrJoiningStartDate(String strJoiningStartDate) {
		this.strJoiningStartDate = strJoiningStartDate;
	}

	public String getStrJoiningEndDate() {
		return strJoiningEndDate;
	}

	public void setStrJoiningEndDate(String strJoiningEndDate) {
		this.strJoiningEndDate = strJoiningEndDate;
	}

	public String getStrTerminateStartDate() {
		return strTerminateStartDate;
	}

	public void setStrTerminateStartDate(String strTerminateStartDate) {
		this.strTerminateStartDate = strTerminateStartDate;
	}

	public String getStrTerminateEndDate() {
		return strTerminateEndDate;
	}

	public void setStrTerminateEndDate(String strTerminateEndDate) {
		this.strTerminateEndDate = strTerminateEndDate;
	}

	public List<FillEducation> getEduList() {
		return eduList;
	}

	public void setEduList(List<FillEducation> eduList) {
		this.eduList = eduList;
	}

	

	public String[] getF_status() {
		return f_status;
	}

	public void setF_status(String[] f_status) {
		this.f_status = f_status;
	}

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
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

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public String getStrSbu() {
		return strSbu;
	}

	public void setStrSbu(String strSbu) {
		this.strSbu = strSbu;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getStrStatus() {
		return strStatus;
	}

	public void setStrStatus(String strStatus) {
		this.strStatus = strStatus;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	
	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	
	public String[] getF_education() {
		return f_education;
	}

	public void setF_education(String[] f_education) {
		this.f_education = f_education;
	}

	public String getStrGrade() {
		return strGrade;
	}

	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}

	public String getStrEmployeType() {
		return strEmployeType;
	}

	public void setStrEmployeType(String strEmployeType) {
		this.strEmployeType = strEmployeType;
	}

	public String[] getF_grade() {
		return f_grade;
	}

	public void setF_grade(String[] f_grade) {
		this.f_grade = f_grade;
	}

	public String[] getF_employeType() {
		return f_employeType;
	}

	public void setF_employeType(String[] f_employeType) {
		this.f_employeType = f_employeType;
	}

	public List<FillEmploymentType> getEmployementTypeList() {
		return employementTypeList;
	}

	public void setEmployementTypeList(List<FillEmploymentType> employementTypeList) {
		this.employementTypeList = employementTypeList;
	}

	public String getStrEducation() {
		return strEducation;
	}

	public void setStrEducation(String strEducation) {
		this.strEducation = strEducation;
	}
	
}
