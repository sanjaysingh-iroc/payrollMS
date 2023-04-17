package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.employee.EmployeeActivity;
import com.konnect.jpms.payroll.ApprovePayroll;
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
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LearningPlanAssessmentFinalize extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId;
	String strUserType;
	String strUserTypeId; 
	String strSessionWLocation;
	String strSessionOrgId;
	CommonFunctions CF;
	
	private String learningId;
	private String trainingId;
	private String assessmentId;
	private String courseId;
	private String finalizeRemark;
	private String certificateStatus;
	private String thumbsupStatus;
	private String sendtoGapStatus;
	
	private String empid;
	private String strDomain;
	private String remarktype;
	
	
	private String[] salary_head_id;
	private String[] salary_head_value;
	private String[] isDisplay;
	private String[] hideIsDisplay;
	private String[] emp_salary_id;	 	
	private String f_strWLocation;
	private String f_department;
	private String f_level;	
	private String strEmpId;
	private String strEmpId2;
	private String effectiveDate;
	private String strActivity;
	private String strOrganisation;
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
	
	private List<FillSalaryHeads> salaryHeadList;
	private List<List<String>> al = new ArrayList<List<String>>();
	
	private List<FillOrganisation> organisationList;
	private List<FillOrganisation> organisationList1;
	private List<FillActivity> activityList;
	private List<FillWLocation> wLocationList;
	private List<FillWLocation> wLocationList1;
	private List<FillDepartment> departmentList;
	private List<FillDepartment> departmentList1;
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
	private String appFreqId;

	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strSessionWLocation = (String) session.getAttribute(WLOCATIONID);
		strSessionOrgId = (String)session.getAttribute(ORGID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();

		request.setAttribute(PAGE, "/jsp/training/LearningPlanAssessmentFinalize.jsp");
		request.setAttribute(TITLE, "Comments");
		request.setAttribute("empid", getEmpid());
		
		String submit = request.getParameter("submit");
		
		if(getF_strWLocation()==null){
			setF_strWLocation((String)session.getAttribute(WLOCATIONID));
		}
		
		if(getF_org()==null){
			setF_org(strSessionOrgId);
		}
		
	
		if (submit != null) {
			boolean flag=insertComment();
			
			if(flag){
				insertEmployeeActivity();
				return "success";
			}else{
				return ERROR;
			}			
		} else {
			
			getRemark();
			
			if(uF.parseToInt(getRemarktype()) == 1){
				return "view";
			} else {	
				return LOAD;
			}
		}
	}
	
	private void insertEmployeeActivity() {
		try {
			
			loadValidateEmpActivity();
			EmployeeActivity activity=new EmployeeActivity();
			activity.request=request;
			activity.session = session;
			activity.CF = CF;
			activity.setStrEmpId2(getEmpid());
			activity.setActivityList(activityList);
			activity.setAl(al);
			activity.setDataType(getDataType());
			activity.setDepartmentList1(departmentList1);
			activity.setDesigList(desigList);
			activity.setEffectiveDate(getEffectiveDate());
			activity.setEmailNotification(getEmailNotification());
			activity.setEmp_salary_id(getEmp_salary_id());
			activity.setEmpChangeGrade(getEmpChangeGrade());
			activity.setEmpGrade(getEmpGrade());
			activity.setEmpList(empList);
			activity.setF_department(getF_department());
			activity.setF_level(getF_level());
			activity.setF_org(getF_org());
			activity.setF_strWLocation(getF_strWLocation());
			activity.setGradeChangeList(gradeChangeList);
			activity.setGradeList(gradeList);
			activity.setHideIsDisplay(getHideIsDisplay());
			activity.setIsDisplay(getIsDisplay());
			activity.setLevelList(levelList);
			activity.setLevelList1(levelList1);
			activity.setOrganisationList(organisationList);
			activity.setOrganisationList1(organisationList1);
			activity.setSalary_head_id(getSalary_head_id());
			activity.setSalary_head_value(getSalary_head_value());
//			activity.setSalaryHeadList(salaryHeadList);
			activity.setServiceList1(serviceList1);
			activity.setStrActivity(getStrActivity());
			activity.setStrDepartment(getStrDepartment());
			activity.setStrDesignation(getStrDesignation());
			activity.setStrDesignationUpdate(getStrDesignationUpdate());
			activity.setStrEmpId(null);
			activity.setStrExtendProbationDays(getStrExtendProbationDays());
			activity.setStrGrade(getStrGrade());
			activity.setStrIncrementPercentage(getStrIncrementPercentage());
			activity.setStrIncrementType(getStrIncrementType());
			activity.setStrJoiningDate(getStrJoiningDate());
			activity.setStrLevel(getStrLevel());
			activity.setStrNewStatus(getStrNewStatus());
			activity.setStrNoticePeriod(getStrNoticePeriod());
			activity.setStrOrganisation(getStrOrganisation());
			activity.setStrProbationPeriod(getStrProbationPeriod());
			activity.setStrReason(getStrReason());
			activity.setStrSBU(getStrSBU());
			activity.setStrTransferType(getStrTransferType());
			activity.setStrUpdate(null);
			activity.setStrUpdateDocument("updateDocument");
			activity.setStrWLocation(getStrWLocation());
			activity.setwLocationList1(wLocationList1);
			activity.setLearningId(getLearningId());
			activity.setAssessmentId(getAssessmentId());
			activity.setTrainingId(getTrainingId());
			activity.setFromPage("LPAF");
			activity.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	private void getRemark() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		Database db = new Database();
		db.setRequest(request);
		boolean flag = false;
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			
			sbQuery.append("select * from learning_plan_finalize_details where learning_plan_id = ? and emp_id = ? ");
			if(getTrainingId() != null && uF.parseToInt(getTrainingId()) > 0) {
				sbQuery.append(" and training_id = "+uF.parseToInt(getTrainingId())+"");
			}
			if(getAssessmentId() != null && uF.parseToInt(getAssessmentId()) > 0) {
				sbQuery.append(" and assessment_id = "+uF.parseToInt(getAssessmentId())+" ");
			}
			if(getCourseId() != null && uF.parseToInt(getCourseId()) > 0) {
				sbQuery.append(" and course_id = "+uF.parseToInt(getCourseId())+" ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getLearningId()));
			pst.setInt(2, uF.parseToInt(getEmpid()));
//			System.out.println("pst1==>"+pst);
			rs = pst.executeQuery();
			String finalizeByID = null;
			while (rs.next()) {
				setCertificateStatus(rs.getString("certificate_status"));
				setThumbsupStatus(rs.getString("thumbsup_status"));
				setSendtoGapStatus(rs.getString("send_to_gap_status"));
				setFinalizeRemark(rs.getString("finalize_remark"));
				finalizeByID = rs.getString("added_by");
				flag = true;
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			String finalizedBy = hmEmpName.get(finalizeByID);
			
			request.setAttribute("flag", flag);
			request.setAttribute("finalizedBy", finalizedBy);
			

			if(!flag){
				
				setStrEmpId(getEmpid());
				
				loadValidateEmpActivity();
				
				int nEmpLevelId = CF.getEmpLevelId(getEmpid(), request);
				salaryHeadList = new ArrayList<FillSalaryHeads>();
				salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads(""+nEmpLevelId);
				
				viewUpdateEmployeeSalaryDetails();
				
				viewProfile(getStrEmpId());
				viewEmpActivity();
			} else {
				Map<String, String> hmActivity = CF.getActivityName(con);
				if(hmActivity == null) hmActivity = new HashMap<String, String>();
				
				Map<String, String> hmGradeMap = CF.getGradeMap(con);
				if (hmGradeMap == null) hmGradeMap = new HashMap<String, String>();
				Map<String, String> hmLevelMap = CF.getLevelMap(con);
				if(hmLevelMap == null) hmLevelMap = new HashMap<String, String>();
				Map<String, String> hmDesig = CF.getDesigMap(con);
				if(hmDesig == null) hmDesig = new HashMap<String, String>();
				Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con);
				if(hmWorkLocation == null) hmWorkLocation = new HashMap<String, Map<String, String>>();
				Map<String, String> hmDepartment = CF.getDepartmentMap(con, null, null);
				if(hmDepartment == null) hmDepartment = new HashMap<String, String>();
				Map<String, Map<String, String>> hmOrgMap = CF.getOrgDetails(con, uF);
				if(hmOrgMap == null) hmOrgMap = new HashMap<String, Map<String, String>>();
				Map<String, String> hmServices = CF.getServicesMap(con, false);
				if(hmServices == null) hmServices = new HashMap<String, String>();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select * from employee_activity_details where emp_id=? and learning_plan_id=? ");
				if(getTrainingId() != null && uF.parseToInt(getTrainingId()) > 0) {
					sbQuery.append(" and training_id = "+uF.parseToInt(getTrainingId())+"");
				}
				if(getAssessmentId() != null && uF.parseToInt(getAssessmentId()) > 0) {
					sbQuery.append(" and assessment_id = "+uF.parseToInt(getAssessmentId())+" ");
				}
				sbQuery.append(" order by emp_activity_id desc limit 1");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getEmpid()));
				pst.setInt(2, uF.parseToInt(getLearningId()));
//				System.out.println("pst2==>"+pst);
				rs = pst.executeQuery();
				Map<String, String> hmActivityMap = new HashMap<String, String>(); 
				if (rs.next()) {
					hmActivityMap.put("ACTIVITY_ID", rs.getString("activity_id"));
					hmActivityMap.put("ACTIVITY_NAME", uF.showData(hmActivity.get(rs.getString("activity_id")), ""));
					hmActivityMap.put("EFFECTIVE_DATE", uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()));
					
					String strNoOfDays = "";
					if(uF.parseToInt(rs.getString("activity_id")) == uF.parseToInt(ACTIVITY_EXTEND_PROBATION_ID)){
						strNoOfDays = uF.showData(rs.getString("extend_probation_period"), "0");
					} else if(uF.parseToInt(rs.getString("activity_id")) == uF.parseToInt(ACTIVITY_NOTICE_PERIOD_ID)){
						strNoOfDays = uF.showData(rs.getString("notice_period"), "0");
					} else if(uF.parseToInt(rs.getString("activity_id")) == uF.parseToInt(ACTIVITY_PROBATION_ID)){
						strNoOfDays = uF.showData(rs.getString("probation_period"), "0");
					}
					hmActivityMap.put("NO_OF_DAYS", strNoOfDays);
					hmActivityMap.put("REASON", uF.showData(rs.getString("reason"), ""));
					hmActivityMap.put("INCREMENT_PERCENTAGE", uF.showData(rs.getString("increment_percent"), "0"));
					hmActivityMap.put("GRADE_NAME", uF.showData(hmGradeMap.get(rs.getString("grade_id")), ""));
					
					String strIncrType = "";
					if(uF.parseToInt(rs.getString("increment_type")) == 1){
						strIncrType = "Single";
					} else if(uF.parseToInt(rs.getString("increment_type")) == 2){
						strIncrType = "Double";
					} 
					hmActivityMap.put("INCREMENT_TYPE", strIncrType);
					hmActivityMap.put("LEVEL_NAME", uF.showData(hmLevelMap.get(rs.getString("level_id")), ""));
					hmActivityMap.put("DESIG_NAME", uF.showData(hmDesig.get(rs.getString("desig_id")), ""));
					
					String strTransType = "";
					if(rs.getString("transfer_type") != null && rs.getString("transfer_type").equals("WL")){
						strTransType = "Work Location";
					} else if(rs.getString("transfer_type") != null && rs.getString("transfer_type").equals("DEPT")){
						strTransType = "Department";
					} else if(rs.getString("transfer_type") != null && rs.getString("transfer_type").equals("LE")){
						strTransType = "Legal Entity";
					}
					
					hmActivityMap.put("TRANSFER_TYPE", rs.getString("transfer_type"));
					hmActivityMap.put("TRANSFER_TYPE_NAME", strTransType);
					Map<String, String> hm =  hmWorkLocation.get(rs.getString("wlocation_id"));
					if(hm == null) hm = new HashMap<String, String>();					
					hmActivityMap.put("WORK_LOCATION_NAME", uF.showData(hm.get("WL_NAME"), ""));
					hmActivityMap.put("DEPARTMENT_NAME", uF.showData(hmDepartment.get(rs.getString("department_id")), ""));
					Map<String, String> hmOrg =  hmOrgMap.get(rs.getString("org_id"));
					if(hmOrg == null) hmOrg = new HashMap<String, String>();
					hmActivityMap.put("ORG_NAME", uF.showData(hmOrg.get("ORG_NAME"), ""));
					String serviceId = rs.getString("service_id")!= null && rs.getString("service_id").contains(",") ? rs.getString("service_id").substring(1,rs.getString("service_id").length()-1) : "";
					hmActivityMap.put("SERVICE_NAME", uF.showData(hmServices.get(serviceId), ""));
				}
				rs.close();
				pst.close();
//				System.out.println("hmActivityMap==>"+hmActivityMap);
				request.setAttribute("hmActivityMap", hmActivityMap);
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	public void loadValidateEmpActivity() {

		UtilityFunctions uF = new UtilityFunctions();
		if(getDataType() == null || getDataType().equals("A")) {
			activityList = new FillActivity(request).fillActivityByNode(true, false,uF.parseToInt(getStrEmpId()));
		} else if(getDataType() == null || getDataType().equals("D")) {
			activityList = new FillActivity(request).fillActivityByNode(true, true,uF.parseToInt(getStrEmpId()));
		}
		
		
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		desigList = new ArrayList<FillDesig>();
		gradeList = new ArrayList<FillGrade>();
		empStatusList = new FillEmpStatus(request).fillEmpStatus();
		empList = new FillEmployee(request).fillEmployeeName(strUserType, strSessionEmpId, session);
		
		organisationList = new FillOrganisation(request).fillOrganisation();
		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		organisationList1 = new FillOrganisation(request).fillOrganisationWithoutCurrentOrgId(getF_org());
		wLocationList1 = new FillWLocation(request).fillWLocationWithoutCurrentLocation(uF.parseToInt(getStrEmpId()));
		departmentList1 = new FillDepartment(request).fillDepartmentWithoutCurrentDepartment(uF.parseToInt(getStrEmpId()));
		serviceList1 = new FillServices(request).fillServicesWithoutCurrentService(uF.parseToInt(getStrEmpId()));
		levelList1 = new FillLevel(request).fillLevelWithoutCurrentLevel(uF.parseToInt(getStrEmpId()));
		gradeChangeList = new FillGrade(request).fillGradeFromEmpDesignationWithoutCurrentGrade(uF.parseToInt(getStrEmpId()));
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
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
			Map<String, String> hmEmpLevel = CF.getEmpLevelMap(con);
			String levelId = hmEmpLevel.get(getStrEmpId());
			Map hmEmpMertoMap = new HashMap();
			Map hmEmpWlocationMap = new HashMap();
			Map hmEmpStateMap = new HashMap();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
			
			String strStateId = (String)hmEmpStateMap.get(getStrEmpId());
			
			List<List<String>> alE = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			
			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con);
			
			pst = con.prepareStatement("SELECT weight,isdisplay,pay_type,user_id,entry_date,amount," +
					"emp_salary_id,salary_head_amount,sd.earning_deduction,salary_head_amount_type," +
					"sub_salary_head_id, sd.salary_head_id as salary_head_id,multiple_calculation," +
					"salary_calculate_amount FROM (SELECT * FROM emp_salary_details WHERE emp_id = ? " +
					"AND service_id = ? AND effective_date = (SELECT MAX(effective_date) " +
					"FROM emp_salary_details WHERE emp_id = ? and is_approved=true and level_id = ?) " +
					"AND effective_date <= ? and level_id = ?) asd RIGHT JOIN salary_details sd " +
					"ON sd.salary_head_id = asd.salary_head_id WHERE sd.level_id = ? " +
					"and (sd.is_delete is null or sd.is_delete=false) order by sd.earning_deduction desc, weight");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, 0);  // Default Service Id
			pst.setInt(3, uF.parseToInt(getStrEmpId()));
			pst.setInt(4, uF.parseToInt(levelId));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt(levelId));
			pst.setInt(7, uF.parseToInt(levelId));
			rs = pst.executeQuery();
//			System.out.println("pst viewUpdateEmployeeSalaryDetails ===>> " + pst);
			
			String alHeadId = "";
			Map<String, String> hmSalaryAmountMap = new HashMap<String, String>();
			List alSalaryDuplicationTracer = new ArrayList();
			Map<String, String> hmTotal = new HashMap<String, String>();
			
			while(rs.next()) {
				
				hmSalaryAmountMap.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));
				
				
				alInner = new ArrayList<String>();
				alInner.add(uF.parseToInt(rs.getString("emp_salary_id"))+"");	//0
				String rsHeadId = rs.getString("salary_head_id");
				alInner.add(rsHeadId);	//1
				
				
				for(int i=0; i<salaryHeadList.size(); i++) {
					
					alHeadId = ((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadId();
					
					
//					System.out.println("alHeadId==>"+alHeadId);
					
					
					if(rsHeadId!=null && rsHeadId.equalsIgnoreCase("0")){
						alInner.add("0");	//2
						break;
					}else if(rsHeadId!=null && rsHeadId.equalsIgnoreCase(alHeadId)) {
						alInner.add(((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName());	//2
						break;
					}
				}
				
				alInner.add(rs.getString("earning_deduction"));	//3
				alInner.add(rs.getString("salary_head_amount_type")); //4
				rsHeadId = rs.getString("sub_salary_head_id");	
				alInner.add(rsHeadId);	//5
				
				for(int i=0; i<salaryHeadList.size(); i++) {
					
					alHeadId = ((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadId();
					if(alHeadId==null)continue;
					
					
					if(rsHeadId.equalsIgnoreCase("0")){
						alInner.add("0");	//6
						break;
					}else if(rsHeadId.equalsIgnoreCase(alHeadId)) {

						if(uF.parseToInt(rsHeadId)==1){ 
							
							if(uF.parseToInt(rs.getString("salary_head_id"))>2){
//								alInner.add(((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName()+" + DA ");	//6
								
								StringBuilder sb = new StringBuilder();
								if(hmSalaryAmountMap.containsKey(DA+"")){
									sb.append(" + "+hmSalaryMap.get(DA+""));
								}
								if(hmSalaryAmountMap.containsKey(DA1+"")){
									sb.append(" + "+hmSalaryMap.get(DA1+""));
								}
								alInner.add(((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName()+sb.toString());	//6
								
							}else{   
								alInner.add(((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName());	//6
							}
							
							
						}else{
							alInner.add(((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName());	//6
						}
						
						
						break;
					}
				}
				
				
				alInner.add(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount")))); //7
				 
				
				double dblAmount = 0;
				if(rs.getString("amount")==null){
					String strAmountType = rs.getString("salary_head_amount_type");
					if(strAmountType!=null && strAmountType.equalsIgnoreCase("P")){
						dblAmount = rs.getDouble("salary_head_amount") *   uF.parseToDouble(hmSalaryAmountMap.get("salary_head_id")) /100 ;
					}else if(strAmountType!=null && strAmountType.equalsIgnoreCase("A")){
						dblAmount = rs.getDouble("salary_head_amount");
					}
				}else{
					dblAmount = rs.getDouble("amount") ;
				}
				
//				System.out.println("dblAmount=="+dblAmount);
				
				
				
				ApprovePayroll objAP = new ApprovePayroll();
				objAP.CF = CF;
				objAP.session = session;
				objAP.request = request; 
				
				if(uF.parseToBoolean(rs.getString("isdisplay"))){
					switch(rs.getInt("salary_head_id")){
					
					case TDS:
						
//						dblAmount = objAP.calculateTDS(dblGross, dblCess1, dblCess2, dblHRA, dblBasicDA, nPayMonth, strPaycycleStart, strFinancialYearStart, strFinancialYearEnd, strEmpId, strGender, strAge, strWLocationStateId, hmEmpExemptionsMap, hmFixedExemptions, hmEmpMertoMap, hmEmpRentPaidMap, hmPaidSalaryDetails, hmTotal, hmSalaryDetails);
					
//						System.out.println("======dblAmount"+dblAmount);
						alInner.add(uF.formatIntoTwoDecimal(dblAmount));	//6
//						System.out.println("======dblAmount"+uF.formatIntoTwoDecimal(dblAmount));
						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimal(dblAmount));
						
					break; 
				
					case EMPLOYEE_EPF :
							
						dblAmount = objAP.calculateEEPF(con, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmTotal, null, null, null, false, null);
					   
						alInner.add(uF.formatIntoTwoDecimal(dblAmount));	//6
						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimal(dblAmount));
						
					break;
					
					case EMPLOYER_EPF :
						
						dblAmount = objAP.calculateERPF(con, CF, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, null, null, null, false, null);
					
						alInner.add(uF.formatIntoTwoDecimal(dblAmount));	//6
						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimal(dblAmount));
						
					break;  
					
					case EMPLOYER_ESI :
						
						dblAmount = objAP.calculateERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId,getStrEmpId(), null, null);
						dblAmount = Math.ceil(dblAmount);
						
						alInner.add(uF.formatIntoTwoDecimal(dblAmount));	//6
						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimal(dblAmount));
						
					break;
					
					case EMPLOYEE_ESI :
						
						dblAmount = objAP.calculateEEESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, null);
						dblAmount = Math.ceil(dblAmount);
						
						alInner.add(uF.formatIntoTwoDecimal(dblAmount));	//6
						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimal(dblAmount));
						
					break;
					
					case MOBILE_RECOVERY:
						
						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimal(dblAmount));
						alInner.add(uF.formatIntoTwoDecimal(dblAmount));	//6
						break;
						
					default:
						alInner.add(uF.formatIntoOneDecimalWithOutComma(dblAmount));	//6
						
						if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))){
							
							double dbl = uF.parseToDouble((String)hmTotal.get(rs.getString("sub_salary_head_id")));
							dbl = dbl * rs.getDouble("salary_head_amount") /100;
							hmTotal.put(rs.getString("salary_head_id"), dbl+"");
						}else{
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoOneDecimalWithOutComma(dblAmount));
						}
						
//						hmTotal.put(rs.getString("salary_head_id"), rs.getString("salary_head_amount"));
					break;
				}
				}else{
					alInner.add("0");	//6
				}
				
//				alInner.add(uF.formatIntoOneDecimalWithOutComma(dblAmount)); //8
				
				
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(rs.getString("user_id"));
				alInner.add(rs.getString("pay_type"));
				alInner.add(uF.parseToBoolean(rs.getString("isdisplay"))+"");
				alInner.add(rs.getString("weight"));
				
				
				int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
				
				if(index>=0){
					alE.remove(index);
					alE.add(index, alInner);
				}else{
					alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
					alE.add(alInner);
				}
				
//				System.out.println("alInner ===>> " + alInner);
				flag = true;	
			}
			rs.close();
			pst.close();
			
			setEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
			
			
//			System.out.println("reportList alE ===>> " + alE);
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
		
//		request.setAttribute(TITLE, TEmployeeActivity);
		
		List<List<String>> alSkills = new ArrayList<List<String>>();
		alSkills = CF.selectSkills(con, uF.parseToInt(strEmpIdReq));
		request.setAttribute("alSkills", alSkills);
		
//		request.setAttribute("alActivityDetails", alActivityDetails);
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
//	return SUCCESS;

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
		
		
//		CF.getEmpInfoMap(con, false);
		Map<String, String> hmServiceName = CF.getServicesMap(con, false);
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
//			hmEmpActivityDetails.put("ACTIVITY_STATUS", rst.getString("activity_name"));
			hmEmpActivityDetails.put("REASON", rst.getString("reason"));
			hmEmpActivityDetails.put("PROBATION_PERIOD", rst.getString("probation_period"));
			hmEmpActivityDetails.put("NOTICE_PERIOD", rst.getString("notice_period"));
			
			joining_date = rst.getString("joining_date");
			setEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
//			setStrActivity(rst.getString("activity_id"));
//			setStrLevel(rst.getString("level_id"));
//			setStrDesignation(rst.getString("desig_id"));
//			setStrGrade(rst.getString("grade_id"));
//			setEmpGrade(rst.getString("grade_id"));
//			setStrWLocation(rst.getString("wlocation_id"));
//			setStrDepartment(rst.getString("department_id"));
//			setStrNewStatus(rst.getString("emp_status_code"));
//			setStrNoticePeriod(rst.getString("notice_period"));
//			setStrProbationPeriod(rst.getString("probation_period"));
			
//			setStrReason(rst.getString("reason"));
			
		}
		rst.close();
		pst.close();
		
		
		uF.getTimeDuration(joining_date, CF, uF, request); // expWithUs
		
//		List<String> serviceIdList = Arrays.asList(serviceIds.split(","));  
//		String servicesName = CF.getAppendData(serviceIdList, hmServiceName);
		setStrEmpId2(getStrEmpId());
		
		hmEmpActivityDetails.put("EMP_ID", getStrEmpId());
		
		request.setAttribute("educationsName", educationsName);
//		request.setAttribute("totExp", totExp);
//		request.setAttribute("servicesName", servicesName);
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

	private boolean insertComment() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
 
		Database db = new Database();
		db.setRequest(request);
		boolean flag = false;
		try {
			con = db.makeConnection(con);    

			pst = con.prepareStatement("insert into learning_plan_finalize_details(learning_plan_id,training_id,assessment_id,course_id," +
							"emp_id,training_certificate_id,certificate_status,thumbsup_status,send_to_gap_status,added_by,entry_date," +
							"finalize_remark)values(?,?,?,?, ?,?,?,?, ?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getLearningId()));
			pst.setInt(2, uF.parseToInt(getTrainingId()));
			pst.setInt(3, uF.parseToInt(getAssessmentId()));
			pst.setInt(4, uF.parseToInt(getCourseId()));
			pst.setInt(5, uF.parseToInt(getEmpid()));
			pst.setInt(6, uF.parseToInt("0"));
			pst.setBoolean(7, uF.parseToBoolean(getCertificateStatus()));
			pst.setBoolean(8, uF.parseToBoolean(getThumbsupStatus()));
			pst.setBoolean(9, uF.parseToBoolean(getSendtoGapStatus()));
			pst.setInt(10, uF.parseToInt(strSessionEmpId));
			pst.setDate(11, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(12, getFinalizeRemark());
			int x = pst.executeUpdate();
			pst.close();
//			System.out.println("x=====>"+x);
			if(x > 0){
				flag = true;
				
				pst = con.prepareStatement("update training_gap_details set training_completed_status= ?, training_complete_date=? where assign_learning_plan_id=? and emp_id= ? ");
				pst.setBoolean(1, true);
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(3,uF.parseToInt(getLearningId()));
				pst.setInt(4,uF.parseToInt(getEmpid()));
				pst.execute();
				pst.close();
			}
			if (flag) {
				if(uF.parseToBoolean(getSendtoGapStatus())) {
					sendEmpToGap(con, uF);
				}
			
				if (uF.parseToBoolean(getCertificateStatus()) == true || uF.parseToBoolean(getThumbsupStatus()) == true) {
					Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
					String lPlanName = CF.getLearningPlanNameById(con, uF, getLearningId());
					String strDomain = request.getServerName().split("\\.")[0];
					pst=con.prepareStatement("select emp_per_id from employee_official_details eod,user_details ud, employee_personal_details epd where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=?");
					pst.setInt(1, uF.parseToInt(hmUserTypeId.get(ADMIN)));
					rs=pst.executeQuery();
					while(rs.next()) {
						String alertData = "<div style=\"float: left;\"> A Learning ("+lPlanName+") Finalized of ("+CF.getEmpNameMapByEmpId(con, getEmpid())+") by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
						String alertAction = "LearningPlanAssessmentStatus.action?pType=WR&lPlanId="+getLearningId();
						
						UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(rs.getString("emp_per_id"));
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(ADMIN));
						userAlerts.setStatus(INSERT_WR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
					}
					rs.close();
					pst.close();
					
//					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//					userAlerts.setStrDomain(strDomain);
//					userAlerts.setStrEmpId(strSessionEmpId);
//					userAlerts.set_type(HR_LEARNING_FINALIZATION_ALERT);
//					userAlerts.setStatus(INSERT_ALERT);
//					Thread t = new Thread(userAlerts);
//					t.run();
					
					sendMail(con, getLearningId(), getTrainingId(), getAssessmentId(), getEmpid());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}

	
	public void sendMail(Connection con, String learningPlanId, String trainingId, String assessmentId , String empId) {

		ResultSet rs = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
//			System.out.println("Req id is ========= "+getStrId());
			String lPlanName = "";
			pst = con.prepareStatement("select learning_plan_name from learning_plan_details where learning_plan_id = ?");
			pst.setInt(1, uF.parseToInt(learningPlanId));
			rs = pst.executeQuery();
			while (rs.next()) {
				lPlanName = rs.getString("learning_plan_name");
			}
			rs.close();
			pst.close();
			
			String trainingName = CF.getTrainingNameByTrainingId(con, uF, trainingId);
			String assessmentName = CF.getAssessmentNameByAssessId(con, uF, assessmentId);
			if(lPlanName != null && !lPlanName.equals("")) {
				Map<String, String> hmEmpInner1 = hmEmpInfo.get(empId);
					Map<String, String> hmEmpInner = hmEmpInfo.get(strSessionEmpId);
					StringBuilder sbFinalizerName = new StringBuilder();
					sbFinalizerName.append(hmEmpInner.get("FNAME")+" " +hmEmpInner.get("LNAME"));
						String strDomain = request.getServerName().split("\\.")[0];
						int NOTIFICATION_NAME = N_LEARNING_TRAINING_FINALIZATION_TO_EMP;
						if(assessmentName != null && !assessmentName.equals("")) {
							NOTIFICATION_NAME = N_LEARNING_ASSESS_FINALIZATION_TO_EMP;
						 }
						Notifications nF = new Notifications(NOTIFICATION_NAME, CF); 
			//			System.out.println("Emp ID is ========= "+rst1.getString("panel_emp_id"));
						 nF.setDomain(strDomain);
						 nF.request = request;
						 nF.setStrEmpId(empId);
						 nF.setStrEmpFname(hmEmpInner1.get("FNAME"));
						 nF.setStrEmpLname(hmEmpInner1.get("LNAME"));
						 nF.setStrHostAddress(CF.getStrEmailLocalHost());
						 nF.setStrHostPort(CF.getStrHostPort());
						 nF.setStrContextPath(request.getContextPath());
						 if(trainingName != null && !trainingName.equals("")) {
							 nF.setStrTrainingName(trainingName);
						 }
						 if(assessmentName != null && !assessmentName.equals("")) {
							 nF.setStrAssessmentName(assessmentName);
						 }
						 nF.setStrFinalizerName(sbFinalizerName.toString());
						 nF.setStrLearningPlanName(lPlanName);
						 nF.setEmailTemplate(true);
						 nF.sendNotifications();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void sendEmpToGap(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String wLocationId = CF.getEmpWlocationId(con, uF, getEmpid());
			Map<String, String> hmDesignation = CF.getEmpDesigMapId(con);
			
			Map<String, String> hmAttributeThreshhold = new HashMap<String, String>();
			pst = con.prepareStatement("select attribute_id,threshhold from appraisal_attribute_level");
//			pst=con.prepareStatement(selectAttribute);
			rs = pst.executeQuery(); 
			while (rs.next()) {
				hmAttributeThreshhold.put(rs.getString("attribute_id"),rs.getString("threshhold"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select attribute_id,skills from learning_plan_details where learning_plan_id = ?");
			pst.setInt(1, uF.parseToInt(getLearningId()));
//			pst=con.prepareStatement(selectAttribute);
			System.out.println("");
			rs = pst.executeQuery();
			String attribIds = null;
			String skillsIds = null;
			while (rs.next()) {
				attribIds = rs.getString("attribute_id");
				skillsIds = rs.getString("skills");
			}
			rs.close();
			pst.close();
			
			double dblAggregate = 0.0d;
			
			if(uF.parseToInt(getAssessmentId()) > 0){
			
				pst = con.prepareStatement("select * from(select sum(marks) as marks ,sum(weightage) as weightage,assessment_details_id,user_type_id,emp_id " +
						"from assessment_question_answer where learning_plan_id= ? and assessment_details_id=? and emp_id=? " +
						"group by user_type_id,emp_id,assessment_details_id)as a");
				pst.setInt(1, uF.parseToInt(getLearningId()));
				pst.setInt(2, uF.parseToInt(getAssessmentId()));
				pst.setInt(3, uF.parseToInt(getEmpid()));
				rs = pst.executeQuery();
				while (rs.next()) {
					double dblMarks = uF.parseToDouble(rs.getString("marks"));
					double dblWeightage = uF.parseToDouble(rs.getString("weightage"));
					String aggregate = "0";
					if(dblWeightage>0){
						aggregate = uF.formatIntoTwoDecimal((dblMarks * 100)/dblWeightage);
					}
					dblAggregate = uF.parseToDouble(aggregate);
				}
				rs.close();
				pst.close();
			}
			
			List<String> attribIdList = Arrays.asList(attribIds.split(","));
			for(int i=0; attribIdList!=null && !attribIdList.isEmpty() && i<attribIdList.size(); i++) {
				int nAttributeId = uF.parseToInt(attribIdList.get(i));
				if(nAttributeId == 0) {
					continue;
				}
				double dblActualScore = uF.parseToInt(getAssessmentId()) > 0 ? uF.parseToDouble(hmAttributeThreshhold.get(""+nAttributeId)) : 0.0d;
				
				pst = con.prepareStatement("insert into training_gap_details(emp_id,learning_id,learning_attribute_ids,learning_skill_ids"
					+ ",training_completed_status,is_training_schedule,actual_score,required_score,assessment_id,training_id,designation_id," +
					" wlocation_id,added_by,entry_date) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
				pst.setInt(1,uF.parseToInt(getEmpid()));
				pst.setInt(2,uF.parseToInt(getLearningId()));
				pst.setString(3, ","+nAttributeId+",");
				pst.setString(4, skillsIds);
				pst.setBoolean(5, false);
				pst.setBoolean(6, false);
				pst.setDouble(7, dblAggregate);
				pst.setDouble(8, dblActualScore);
				pst.setInt(9, uF.parseToInt(getAssessmentId()));
				pst.setInt(10, uF.parseToInt(getTrainingId()));
				pst.setInt(11, uF.parseToInt(hmDesignation.get(getEmpid())));
				pst.setInt(12, uF.parseToInt(wLocationId));
				pst.setInt(13, uF.parseToInt(strSessionEmpId));
				pst.setDate(14, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.execute();
				pst.close();
				
				
				Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
				String strDomain = request.getServerName().split("\\.")[0];
				pst=con.prepareStatement("select emp_per_id from employee_official_details eod,user_details ud, employee_personal_details epd where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=?");
				pst.setInt(1, uF.parseToInt(hmUserTypeId.get(ADMIN)));
				rs=pst.executeQuery();
				while(rs.next()) {
					String alertData = "<div style=\"float: left;\"> Learning Gap has emerged for ("+CF.getEmpNameMapByEmpId(con, getEmpid())+") by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
					String alertAction = "Learnings.action?callFrom=LA&pType=WR";
					
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(rs.getString("emp_per_id"));
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(hmUserTypeId.get(ADMIN));
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
				}
				rs.close();
				pst.close();
				
//				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//				userAlerts.setStrDomain(strDomain);
//				userAlerts.setStrEmpId(strSessionEmpId);
//				userAlerts.set_type(HR_LEARNING_GAPS_ALERT);
//				userAlerts.setStatus(INSERT_ALERT);
//				Thread t = new Thread(userAlerts);
//				t.run();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
		
	}


	public String getLearningId() {
		return learningId;
	}


	public void setLearningId(String learningId) {
		this.learningId = learningId;
	}


	public String getAssessmentId() {
		return assessmentId;
	}


	public void setAssessmentId(String assessmentId) {
		this.assessmentId = assessmentId;
	}


	public String getCourseId() {
		return courseId;
	}


	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}


	public String getFinalizeRemark() {
		return finalizeRemark;
	}


	public void setFinalizeRemark(String finalizeRemark) {
		this.finalizeRemark = finalizeRemark;
	}


	public String getCertificateStatus() {
		return certificateStatus;
	}


	public void setCertificateStatus(String certificateStatus) {
		this.certificateStatus = certificateStatus;
	}


	public String getSendtoGapStatus() {
		return sendtoGapStatus;
	}


	public void setSendtoGapStatus(String sendtoGapStatus) {
		this.sendtoGapStatus = sendtoGapStatus;
	}


	public String getEmpid() {
		return empid;
	}


	public void setEmpid(String empid) {
		this.empid = empid;
	}


	public String getStrDomain() {
		return strDomain;
	}


	public void setStrDomain(String strDomain) {
		this.strDomain = strDomain;
	}


	public String getTrainingId() {
		return trainingId;
	}


	public void setTrainingId(String trainingId) {
		this.trainingId = trainingId;
	}


	public String getThumbsupStatus() {
		return thumbsupStatus;
	}


	public void setThumbsupStatus(String thumbsupStatus) {
		this.thumbsupStatus = thumbsupStatus;
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


	public String[] getHideIsDisplay() {
		return hideIsDisplay;
	}


	public void setHideIsDisplay(String[] hideIsDisplay) {
		this.hideIsDisplay = hideIsDisplay;
	}


	public String[] getEmp_salary_id() {
		return emp_salary_id;
	}


	public void setEmp_salary_id(String[] emp_salary_id) {
		this.emp_salary_id = emp_salary_id;
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


	public String getStrEmpId() {
		return strEmpId;
	}


	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}


	public String getStrEmpId2() {
		return strEmpId2;
	}


	public void setStrEmpId2(String strEmpId2) {
		this.strEmpId2 = strEmpId2;
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


	public String getStrDesignationUpdate() {
		return strDesignationUpdate;
	}


	public void setStrDesignationUpdate(String strDesignationUpdate) {
		this.strDesignationUpdate = strDesignationUpdate;
	}


	public String getStrGrade() {
		return strGrade;
	}


	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}


	public String getEmpGrade() {
		return empGrade;
	}


	public void setEmpGrade(String empGrade) {
		this.empGrade = empGrade;
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


	public String getF_org() {
		return f_org;
	}


	public void setF_org(String f_org) {
		this.f_org = f_org;
	}


	public String getAppraisal_id() {
		return appraisal_id;
	}


	public void setAppraisal_id(String appraisal_id) {
		this.appraisal_id = appraisal_id;
	}


	public String getEmpChangeGrade() {
		return empChangeGrade;
	}


	public void setEmpChangeGrade(String empChangeGrade) {
		this.empChangeGrade = empChangeGrade;
	}


	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}


	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}


	public List<List<String>> getAl() {
		return al;
	}


	public void setAl(List<List<String>> al) {
		this.al = al;
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


	public List<FillActivity> getActivityList() {
		return activityList;
	}


	public void setActivityList(List<FillActivity> activityList) {
		this.activityList = activityList;
	}


	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}


	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}


	public List<FillWLocation> getwLocationList1() {
		return wLocationList1;
	}


	public void setwLocationList1(List<FillWLocation> wLocationList1) {
		this.wLocationList1 = wLocationList1;
	}


	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}


	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
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


	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}


	public List<FillLevel> getLevelList1() {
		return levelList1;
	}


	public void setLevelList1(List<FillLevel> levelList1) {
		this.levelList1 = levelList1;
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


	public List<FillGrade> getGradeChangeList() {
		return gradeChangeList;
	}


	public void setGradeChangeList(List<FillGrade> gradeChangeList) {
		this.gradeChangeList = gradeChangeList;
	}


	public List<FillEmpStatus> getEmpStatusList() {
		return empStatusList;
	}


	public void setEmpStatusList(List<FillEmpStatus> empStatusList) {
		this.empStatusList = empStatusList;
	}


	public List<FillEmployee> getEmpList() {
		return empList;
	}


	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
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


	public String getRemarktype() {
		return remarktype;
	}


	public void setRemarktype(String remarktype) {
		this.remarktype = remarktype;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

	
}
