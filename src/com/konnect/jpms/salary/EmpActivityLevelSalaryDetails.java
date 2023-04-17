package com.konnect.jpms.salary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillActivity;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmpStatus;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmpActivityLevelSalaryDetails extends ActionSupport implements ServletRequestAware, IStatements, ServletResponseAware {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	String strSessionEmpId;
	String strSessionOrgId;
	
	String[] salary_head_id;
	String[] salary_head_value;
	String[] isDisplay;
	String[] hideIsDisplay;
	String[] emp_salary_id;
	 
	private List<FillSalaryHeads> salaryHeadList;
	
	
	String f_strWLocation;
	String f_department;
	String f_level;
	
	String strEmpId;
	String strEmpId2;
	String effectiveDate;
	String strActivity;
	String strOrganisation;
	String strWLocation;
	String strSBU;
	String strDepartment;
	String strLevel;
	String strDesignation;
	String strDesignationUpdate;
	String strGrade;
	String empGrade;
	String strNoticePeriod;
	String strProbationPeriod;
	String strExtendProbationDays;
	String strIncrementType;
	String strIncrementPercentage;
	
	String strNewStatus;
	String strReason;	
	String strJoiningDate;
	boolean emailNotification;
	String strUpdate;
	String f_org;
	String appraisal_id;
	String empChangeGrade;
	
	List<FillOrganisation> organisationList;
	List<FillOrganisation> organisationList1;
	List<FillActivity> activityList;
	List<FillWLocation> wLocationList;
	List<FillWLocation> wLocationList1;
	List<FillDepartment> departmentList;
	List<FillDepartment> departmentList1;
//	List<FillServices> serviceList;
	List<FillServices> serviceList1;
	List<FillLevel> levelList;
	List<FillLevel> levelList1;
	List<FillDesig> desigList;
	List<FillGrade> gradeList;
	List<FillGrade> gradeChangeList;
	List<FillEmpStatus> empStatusList;
	List<FillEmployee> empList;
	
	String dataType;   
	String strUpdateDocument;
	
	private boolean disableSalaryStructure;
	
	public String execute() throws Exception {
		session = request.getSession(); 
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;

		request.setAttribute(PAGE, "/jsp/salary/EmpActivityLevelSalaryDetails.jsp");
		request.setAttribute(TITLE, TEmployeeActivity);
		
		UtilityFunctions uF = new UtilityFunctions();
		 
		strSessionEmpId = (String)session.getAttribute(EMPID); 
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		
		if(getF_strWLocation()==null){
			setF_strWLocation((String)session.getAttribute(WLOCATIONID));
		}
		
		if(getF_org()==null){
			setF_org(strSessionOrgId);
		}
		
		loadValidateEmpActivity(uF);
		
		viewLevelWiseSalaryDetails(uF);
		viewProfile(uF,getStrEmpId());
		viewEmpActivity(uF);
		
		
		return SUCCESS;
		
	}
	
	private void viewLevelWiseSalaryDetails(UtilityFunctions uF) {
	
	Connection con = null;
	PreparedStatement pst=null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	
	try {
		con = db.makeConnection(con);
		
		Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con, uF.parseToInt(getStrLevel()));
		Map<String, String> hmLevelMap = CF.getLevelMap(con);
		if(hmLevelMap == null) hmLevelMap = new HashMap<String, String>();
		request.setAttribute("strLevelName", uF.showData(hmLevelMap.get(getStrLevel()), ""));
		
		Map<String, String> hmActivity = CF.getActivityName(con);
		if(hmActivity == null) hmActivity = new HashMap<String, String>();
		request.setAttribute("strActivityName", uF.showData(hmActivity.get(getStrActivity()), ""));
		
		String strOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
		
		String strFinancialYearEnd = null;
		String strFinancialYearStart = null;
		String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
		strFinancialYearStart = strFinancialYearDates[0];
		strFinancialYearEnd = strFinancialYearDates[1];
		
		Map<String, String> hmEmpAnnualVarPolicyAmount = CF.getEmpAnnualVariablePolicyMonthAmount(con, uF, getStrEmpId(), strFinancialYearStart, strFinancialYearEnd);
		if(hmEmpAnnualVarPolicyAmount == null) hmEmpAnnualVarPolicyAmount = new HashMap<String, String>();
		request.setAttribute("hmEmpAnnualVarPolicyAmount", hmEmpAnnualVarPolicyAmount);
		
		String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
		String[] strPayCycleDate = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrgId);
		if(strPayCycleDate !=null && strPayCycleDate.length > 0){
			String startDate = strPayCycleDate[0];
			String endDate = strPayCycleDate[1];
			String strPC = strPayCycleDate[2];
		
			double dblReimbursementCTC = CF.getReimbursementCTCHeadTotalAmount(con, uF, uF.parseToInt(getStrEmpId()), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(getStrLevel()));
			request.setAttribute("dblReimbursementCTC", ""+dblReimbursementCTC);
			
			double dblReimbursementCTCOptional = CF.getReimbursementCTCOptinalHeadTotalAmount(con, uF, uF.parseToInt(getStrEmpId()), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(getStrLevel()));
			request.setAttribute("dblReimbursementCTCOptional", ""+dblReimbursementCTCOptional);
		}
		
		setDisableSalaryStructure(CF.getEmpDisableSalaryCalculation(con,uF,getStrEmpId()));
		
		pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id = ? and is_annual_variable=true and (is_delete is null or is_delete=false) order by weight");
		pst.setInt(1, uF.parseToInt(getStrLevel()));
		rs = pst.executeQuery();
		List<String> alAnnualSalaryHead = new ArrayList<String>();
		while(rs.next()) {
			if(!alAnnualSalaryHead.contains(rs.getString("salary_head_id"))){
				alAnnualSalaryHead.add(rs.getString("salary_head_id"));
			}
		}
		rs.close();
		pst.close();
		
		List<List<String>> al = new ArrayList<List<String>>();
		pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id = ? and (is_delete is null or is_delete=false) order by weight");
		pst.setInt(1, uF.parseToInt(getStrLevel()));
//		System.out.println("pst salary_details ===>> " + pst);
		rs = pst.executeQuery();
		List alSalaryDuplicationTracer = new ArrayList();
		while(rs.next()) {
			
			List<String> alInner = new ArrayList<String>();
			alInner.add(rs.getString("salary_head_id"));	//0
			alInner.add(rs.getString("salary_head_name"));	//1
			alInner.add(rs.getString("earning_deduction"));	//2
			alInner.add(rs.getString("salary_head_amount_type"));	//3
			String rsHeadId = rs.getInt("sub_salary_head_id") + "";
			alInner.add(rsHeadId);	//4
			String alHeadId = "";
			
//			for(int i=0; i<salaryHeadList.size(); i++) {
//				alHeadId = ((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadId();
//				if(rsHeadId.equalsIgnoreCase("0")){
//					alInner.add("0");	//5
//					break;
//				}else if(rsHeadId.equalsIgnoreCase(alHeadId)) {
//					alInner.add(((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName());	//5
//					break;
//				}
//			}
			alInner.add("0");	//5
			
			alInner.add(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));	//6
			
			StringBuilder sbMulcalType = new StringBuilder();
//			if("M".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
//				String strMulCal = rs.getString("multiple_calculation");
//				String[] strMulCalTemp = strMulCal.split(",");
//
//				CF.appendMultipleCalType(uF,sbMulcalType,strMulCalTemp,hmSalaryMap,alAnnualSalaryHead); 
//			}
			if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))){
				String strMulCal = rs.getString("multiple_calculation");
				CF.appendMultiplePercentageCalType(uF,sbMulcalType,strMulCal,hmSalaryMap,alAnnualSalaryHead); 
			}
			
			alInner.add(rs.getString("multiple_calculation")); //7
			alInner.add(sbMulcalType.toString()); //8
			
			int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
			if(index>=0){
				al.remove(index);
				al.add(index, alInner);
			}else{
				alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
				al.add(alInner);
			}
			
		}
		rs.close();
		pst.close();
		
//		System.out.println("reportList viewEmployeeSalaryDetails ===>> " + al);
		request.setAttribute("reportList", al);
		
	} catch (Exception e) {
		e.printStackTrace();
		
	}finally{
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}

	public String loadValidateEmpActivity(UtilityFunctions uF) {
		
		salaryHeadList = new ArrayList<FillSalaryHeads>();
		salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads();
		
		if(getDataType() == null || getDataType().equals("A")) {
			activityList = new FillActivity(request).fillActivityByNode(true, false);
		} else if(getDataType() == null || getDataType().equals("D")) {
			activityList = new FillActivity(request).fillActivityByNode(true, true);
		}
		
		organisationList = new FillOrganisation(request).fillOrganisation();
		wLocationList = new FillWLocation(request).fillWLocation(getF_org());		
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		empList = new FillEmployee(request).fillEmployeeName(strUserType, strSessionEmpId, session);
		
		desigList = new FillDesig(request).fillDesigFromLevel(getStrLevel());
		gradeChangeList = new ArrayList<FillGrade>();
		
		
		
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		return LOAD;
	}

	
	
	
	
	public void viewProfile(UtilityFunctions uF, String strEmpIdReq) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

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
//		return SUCCESS;

	}
	
	
	
	public String viewEmpActivity(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
//			CF.getEmpInfoMap(con, false);
//			Map<String, String> hmServiceName = CF.getServicesMap(con, false);
			String educationsName = CF.getEmpEducations(con, getStrEmpId());
//			pst = con.prepareStatement(selectEmpActivityDetails);
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
			String serviceIds = null;
			String joining_date = null;
			Map<String, String> hmEmpActivityDetails = new HashMap<String, String>();
			Map<String, String> hmEmpLevel = CF.getEmpLevelMap(con);
			while (rst.next()) {
				serviceIds = rst.getString("service_id");
				
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
				
				
			}
			rst.close();
			pst.close();
			
			if(getEffectiveDate()==null || getEffectiveDate().trim().equals("") || getEffectiveDate().trim().equalsIgnoreCase("NULL")){
				setEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT)); 
			}
			uF.getTimeDuration(joining_date, CF, uF, request); // expWithUs
			
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

	private HttpServletRequest request;
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

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
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
	
	public boolean getDisableSalaryStructure() {
		return disableSalaryStructure;
	}

	public void setDisableSalaryStructure(boolean disableSalaryStructure) {
		this.disableSalaryStructure = disableSalaryStructure;
	}
}