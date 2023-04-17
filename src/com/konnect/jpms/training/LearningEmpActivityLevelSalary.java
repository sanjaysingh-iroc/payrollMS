package com.konnect.jpms.training;

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

public class LearningEmpActivityLevelSalary extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {

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

		request.setAttribute(PAGE, "/jsp/training/LearningEmpActivityLevelSalary.jsp");
		request.setAttribute(TITLE, "Comments");
		request.setAttribute("empid", getEmpid());
		
		String submit = request.getParameter("submit");
		
		if(getF_strWLocation()==null){
			setF_strWLocation((String)session.getAttribute(WLOCATIONID));
		}
		
		if(getF_org()==null){
			setF_org(strSessionOrgId);
		}
		
		getRemark();
		
		return SUCCESS;
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
				
//				viewUpdateEmployeeSalaryDetails();
				viewLevelWiseSalaryDetails(uF);
				viewProfile(getStrEmpId());
				viewEmpActivity();
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
private void viewLevelWiseSalaryDetails(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
//			System.out.println("in viewEmployeeSalaryDetails ...");
			
			
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			if(hmLevelMap == null) hmLevelMap = new HashMap<String, String>();
			request.setAttribute("strLevelName", uF.showData(hmLevelMap.get(getStrLevel()), ""));
			
			Map<String, String> hmActivity = CF.getActivityName(con);
			if(hmActivity == null) hmActivity = new HashMap<String, String>();
			request.setAttribute("strActivityName", uF.showData(hmActivity.get(getStrActivity()), ""));
			
			List<String> alInner = new ArrayList<String>();
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id = ? OR level_id = 0 order by weight");
			pst.setInt(1, uF.parseToInt(getStrLevel()));
//			System.out.println("pst salary_details ===>> " + pst);
			rs = pst.executeQuery();
			List alSalaryDuplicationTracer = new ArrayList();
			while(rs.next()) {
				
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("salary_head_id"));	//0
				alInner.add(rs.getString("salary_head_name"));	//1
				alInner.add(rs.getString("earning_deduction"));	//2
				alInner.add(rs.getString("salary_head_amount_type"));	//3
				String rsHeadId = rs.getInt("sub_salary_head_id") + "";
				alInner.add(rsHeadId);	//4
				String alHeadId = "";
				
				for(int i=0; i<salaryHeadList.size(); i++) {
					
					alHeadId = ((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadId();
					
					if(rsHeadId.equalsIgnoreCase("0")){
						alInner.add("0");	//5
						break;
					}else if(rsHeadId.equalsIgnoreCase(alHeadId)) {
						alInner.add(((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName());	//5
						break;
					}
					
					/*else{
						alInner.add("0");	//5
						break;
					}*/
				}
				
				
				alInner.add(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));	//6
				
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
			
//			System.out.println("reportList viewEmployeeSalaryDetails ===>> " + al);
			request.setAttribute("reportList", al);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void loadValidateEmpActivity() {

		UtilityFunctions uF = new UtilityFunctions();
		
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
		
//		desigList = new FillDesig(request).fillDesigFromLevel(getStrLevel());
		desigList = new FillDesig(request).fillDesigFromLevelForPromotion(getStrEmpId(),getStrLevel());
		
		gradeChangeList = new ArrayList<FillGrade>();
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

	
}
