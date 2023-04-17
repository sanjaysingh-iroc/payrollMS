package com.konnect.jpms.performance;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.document.HeaderFooterPageEvent;
import com.konnect.jpms.employee.EmployeeActivity;
import com.konnect.jpms.leave.AssignLeaveCron;
import com.konnect.jpms.reports.MyProfile;
import com.konnect.jpms.roster.FillShift;
import com.konnect.jpms.salary.EmpSalaryApproval;
import com.konnect.jpms.salary.EmployeeSalaryDetails;
import com.konnect.jpms.select.FillActivity;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSalaryHeads;
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

public class AppraisalBulkFinalization extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	String strSessionEmpId;
	String strSessionOrgId;
	
	private String id;
	private String dataType;
	
	private List<String> strIsAssigActivity;
	private String strEmpIds; 
	
	private String strActivity;
	private String effectiveDate;
	private String strExtendProbationDays;
	private String strTransferType;
	private String strIncrementType;
	private String strOrganisation;
	private String strWLocation;
	private String strSBU;
	private String strDepartment; 
	private String strLevel;
	private String strDesignation;
	private String empGrade;
	private String empChangeGrade;
	private String strNoticePeriod;
	private String strProbationPeriod;
	private String strIncrementPercentage;
	private String strReason;
	private String strUpdate;
	private String strUpdateDocument;
	
	private String strNewStatus;
	
	private List<FillActivity> activityList;
	private List<FillOrganisation> organisationList1;
	private List<FillWLocation> wLocationList1;
	private List<FillServices> serviceList1;
	private List<FillDepartment> departmentList1;
	private List<FillLevel> levelList1;
	private List<FillDesig> desigList;
	private List<FillGrade> gradeChangeList;
	
	private String empIds;
	private String remEmpId;
	
	private String sendtoGapStatus;
	private String remark;
	private String areasOfStrength;
	private String areasOfDevelopment;
	
	private String strGapEmp;
	private String strMessage;
	private String appFreqId;
	private String fromPage;
	private String[] learningIds;
	
	public String execute() throws Exception {
		session = request.getSession(); 
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/performance/AppraisalBulkFinalization.jsp");
		request.setAttribute(TITLE, "Bulk Appraisal Finalization");
		
		strSessionEmpId = (String)session.getAttribute(EMPID); 
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		loadValidateEmpActivity(uF);

		if(getEmpIds()!=null && !getEmpIds().equals("")){
			insertEmpFinalization(uF);
			
			return SUCCESS;
		}
		getAppraisalData();
		getEmployeeList(uF);
	
		return LOAD;
	}
	
	private void insertEmpFinalization(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);//Start Dattatray
//			System.out.println("Outer hmFeatureStatus : "+uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DISABLE_LEARNING_GAP_IN_REVIEW_FINALIZATION)));
			List<String> alGapEmp = null;
			if(getStrGapEmp()!=null && !getStrGapEmp().trim().equals("")){
				alGapEmp = Arrays.asList(getStrGapEmp().trim().split(","));
			} 
			if(alGapEmp == null || alGapEmp.isEmpty() || alGapEmp.size() == 0){
				alGapEmp = new ArrayList<String>();
			}

			String learningPlanIds = "";
			StringBuilder sbLPlanIds = null;
			if(getLearningIds() != null) {
				for(int i=0; i<getLearningIds().length; i++) {
					if(uF.parseToInt(getLearningIds()[i])>0) {
						if(learningPlanIds.equals("")) {
							learningPlanIds = ","+getLearningIds()[i]+",";
							sbLPlanIds = new StringBuilder();
							sbLPlanIds.append(getLearningIds()[i]);
						} else {
							learningPlanIds += getLearningIds()[i]+",";
							sbLPlanIds.append(","+getLearningIds()[i]);
						}
					}
				}
			}
			
			if(sbLPlanIds == null) {
				sbLPlanIds = new StringBuilder();
			}
			
			System.out.println("getEmpIds() ===>> " + getEmpIds());
			
			List<String> alEmpList = Arrays.asList(getEmpIds().split(","));
			if(alEmpList == null) alEmpList = new ArrayList<String>();
			System.out.println("alEmpList ===>> " + alEmpList);
			for(int i=0; alEmpList!=null && i < alEmpList.size(); i++) {
				if(uF.parseToInt(alEmpList.get(i)) == 0) {
					continue;
				}
				// Start Dattatray
				if (hmFeatureStatus.get(IConstants.F_DISABLE_LEARNING_GAP_IN_REVIEW_FINALIZATION) !=null && !uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DISABLE_LEARNING_GAP_IN_REVIEW_FINALIZATION))){
					System.out.println("Inner hmFeatureStatus : "+uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DISABLE_LEARNING_GAP_IN_REVIEW_FINALIZATION)));
//					@DT:29-07-21 Rahul Patil - user_type_id=2 
					pst = con.prepareStatement("select * from reviewee_strength_improvements where user_type_id=2 and review_id=? and review_freq_id=? and emp_id=?");
					pst.setInt(1, uF.parseToInt(getId()));
					pst.setInt(2, uF.parseToInt(getAppFreqId()));
					pst.setInt(3, uF.parseToInt(alEmpList.get(i).trim()));
					rs = pst.executeQuery();
//					System.out.println("reviewee_strength_improvements pst ========> " + pst);
					StringBuilder sbAreaOfStrength = null;
					StringBuilder sbAreaOfImprovement = null;
					Map<String, Map<String, String>> hmOuter = new HashMap<String, Map<String, String>>();
					Map<String, String> hmInner = new HashMap<String, String>();
					while (rs.next()) {
						
						if (sbAreaOfStrength == null) {
							sbAreaOfStrength = new StringBuilder();
							sbAreaOfStrength.append(rs.getString("areas_of_strength"));
						} else {
							sbAreaOfStrength.append("\n" + rs.getString("areas_of_strength"));
						}
						if (sbAreaOfImprovement == null) {
							sbAreaOfImprovement = new StringBuilder();
							sbAreaOfImprovement.append(rs.getString("areas_of_improvement"));
						} else {
							sbAreaOfImprovement.append("\n" + rs.getString("areas_of_improvement"));
						}
						hmInner.put("sbAreaOfStrength", sbAreaOfStrength.toString());
						hmInner.put("sbAreaOfImprovement", sbAreaOfImprovement.toString());
						hmOuter.put(rs.getString("emp_id"), hmInner);
					}
					rs.close();
					pst.close();
					Iterator<Entry<String, Map<String, String>>> itr = hmOuter.entrySet().iterator();
					while (itr.hasNext()) {
						Entry<String, Map<String, String>> entry = itr.next();
						hmInner = hmOuter.get(entry.getKey());
						setAreasOfStrength(hmInner.get("sbAreaOfStrength"));
						setAreasOfDevelopment(hmInner.get("sbAreaOfImprovement"));
					}
				}
				// End Dattatray
				
				boolean flag = false;
				pst = con.prepareStatement("delete from appraisal_final_sattlement where emp_id=? and appraisal_id=? and appraisal_freq_id = ?");
				pst.setInt(1, uF.parseToInt(alEmpList.get(i).trim()));
				pst.setInt(2, uF.parseToInt(getId()));
				pst.setInt(3, uF.parseToInt(getAppFreqId()));
				pst.execute();
				System.out.println("pst =====>> " + pst);
				pst.close();
				
				
				pst = con.prepareStatement("insert into appraisal_final_sattlement(emp_id,appraisal_id,user_id,sattlement_comment,if_approved,_date,activity_id1, appraisal_freq_id," +
						"areas_of_strength,areas_of_development,learning_ids)values(?,?,?,? ,?,?,?,? ,?,?,?)");
				pst.setInt(1, uF.parseToInt(alEmpList.get(i).trim()));
				pst.setInt(2, uF.parseToInt(getId()));
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
				pst.setString(4, getRemark());
				pst.setBoolean(5, true);
				pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(7, 0);
				pst.setInt(8, uF.parseToInt(getAppFreqId()));
				pst.setString(9, getAreasOfStrength());
				pst.setString(10, getAreasOfDevelopment());
				pst.setString(11, learningPlanIds);
				System.out.println("insert final==>"+pst);
				int x = pst.executeUpdate();
				pst.close();
				
				if(x > 0){
					flag = true;
				}
				if (flag && uF.parseToBoolean(getSendtoGapStatus()) && alGapEmp.contains(alEmpList.get(i).trim())) {
					sendToLearningGap(con, alEmpList.get(i).trim(), sbLPlanIds.toString()); 
	//				String strDomain = request.getServerName().split("\\.")[0];
	//				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
	//				userAlerts.setStrDomain(strDomain);
	//				userAlerts.setStrEmpId(strSessionEmpId);
	//				userAlerts.set_type(REVIEW_FINALIZATION_ALERT);
	//				userAlerts.setStatus(INSERT_ALERT);
	//				Thread t = new Thread(userAlerts);
	//				t.run();
					
				}
				
				if(flag) {
					insertEmployeeActivity(con, uF, alEmpList.get(i).trim());
					Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
					String reviewName = CF.getReviewNameById(con, uF, getId());
					
//					pst=con.prepareStatement("select emp_per_id from employee_official_details eod,user_details ud, employee_personal_details epd where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=?");
//					pst.setInt(1, uF.parseToInt(hmUserTypeId.get(ADMIN)));
//					rs=pst.executeQuery();
//					List<String> empList = new ArrayList<String>();
//					while(rs.next()) {
//						if(!empList.contains(rs.getString("emp_per_id").trim())) {
//							empList.add(rs.getString("emp_per_id").trim());	
//						}
//					}
//					rs.close();
//					pst.close();
					sendMail(con, getId(), "", uF.parseToBoolean(getSendtoGapStatus()), alEmpList.get(i).trim());
					
					String alertData = "<div style=\"float: left;\"> A Review ("+reviewName+") is finalized of ("+CF.getEmpNameMapByEmpId(con, alEmpList.get(i))+") by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
					String alertAction = "Reviews.action?pType=WR";
					String strDomain = request.getServerName().split("\\.")[0];
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(alEmpList.get(i));
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(hmUserTypeId.get(ADMIN));
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
				}
			}
			
			if(alEmpList.size() > 0){
				setStrMessage(URLEncoder.encode(SUCCESSM+"Appraisal bulk finalization successfully updated"+END));
			} else {
				setStrMessage(URLEncoder.encode(ERRORM+"Appraisal bulk finalization successfully updated"+END));
			}
		} catch (Exception e) {
			e.printStackTrace();
			setStrMessage(URLEncoder.encode(ERRORM+"Appraisal bulk finalization successfully updated"+END));
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void insertEmployeeActivity(Connection con, UtilityFunctions uF, String strEmpId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
		
			pst = con.prepareStatement("insert into employee_activity_details (wlocation_id, department_id, level_id, desig_id, grade_id, " +
					"emp_status_code, activity_id, reason, effective_date, entry_date, user_id, emp_id, notice_period, probation_period, appraisal_id, " +
					"extend_probation_period, org_id,service_id,increment_type,increment_percent,transfer_type,appraisal_freq_id) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getStrWLocation()));
			pst.setInt(2, uF.parseToInt(getStrDepartment()));
			pst.setInt(3, uF.parseToInt(getStrLevel()));
			pst.setInt(4, uF.parseToInt(getStrDesignation()));
			if(uF.parseToInt(getStrActivity()) == uF.parseToInt(ACTIVITY_GRADE_CHANGE_ID)){
				pst.setInt(5, uF.parseToInt(getEmpChangeGrade()));
			}else{
				pst.setInt(5, uF.parseToInt(getEmpGrade()));
			}
			pst.setString(6, getStrNewStatus());
			pst.setInt(7, uF.parseToInt(getStrActivity()));
			pst.setString(8, getStrReason());
			pst.setDate(9, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(11, uF.parseToInt(strSessionEmpId));
			pst.setInt(12, uF.parseToInt(strEmpId));
			pst.setInt(13, uF.parseToInt(getStrNoticePeriod()));
			pst.setInt(14, uF.parseToInt(getStrProbationPeriod()));
			pst.setInt(15, uF.parseToInt(getId()));
			pst.setInt(16, uF.parseToInt(getStrExtendProbationDays()));
			pst.setInt(17, uF.parseToInt(getStrOrganisation()));
			pst.setString(18, getStrSBU()!=null && !getStrSBU().equals("") ? ","+getStrSBU()+"," : null);
			pst.setInt(19, uF.parseToInt(getStrIncrementType()));
			pst.setDouble(20, uF.parseToDouble(getStrIncrementPercentage()));
			pst.setString(21, getStrTransferType());
			pst.setInt(22, uF.parseToInt(getAppFreqId()));
			int x = pst.executeUpdate();
			pst.close();
			
			String strDate = uF.getCurrentDate(CF.getStrTimeZone())+"";
			if(x > 0){
				pst = con.prepareStatement("select max(emp_activity_id) as emp_activity_id  from employee_activity_details");
				rs = pst.executeQuery();
				int nEmpActivityId = 0;
				while(rs.next()){
					nEmpActivityId = uF.parseToInt(rs.getString("emp_activity_id"));
				}
				rs.close();
				pst.close();
				
				Map<String, String>  hmActivity = CF.getActivityName(con);
				if(hmActivity == null) hmActivity = new HashMap<String, String>();
				
				int activityType = uF.parseToInt(getStrActivity());
				processActivity(con, activityType, uF.parseToInt(strEmpId), strDate, CF, uF, uF.showData(hmActivity.get(""+activityType), ""), nEmpActivityId);
				
				request.setAttribute(MESSAGE, SUCCESSM+"Employee activity successfully updated"+END);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void processActivity(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		
		this.CF= CF;
		switch(activityType){
		
			case 1:
					processOfferLetter(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 2:
					processAppointmentLetter(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 3:
					processProbation(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 4:
					processExtendProbation(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 5:
					processConfirmation(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
					
			case 6:
					processTemporary(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
					
			case 7:
					processPermanent(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
					
			case 8:
					processTransfer(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 9:
					processPromotion(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 10:
					processIncrement(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 11:
					processGradeChange(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 12:
					processTerminate(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 13:
					processNoticePeriod(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 14:
					processWithdrawnResignation(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
					
			case 15:
	//				processFullAndFinal(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
			case 29:
					processRelieving(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
			
			case 30:
					processExperience(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
			case 32:
				processLifeEventIncrement(con,activityType, nEmpId, strDate, CF, uF, strActivityName,nEmpActivityId);
				break;
		}
	}

	private void processLifeEventIncrement(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		updateEmployeeSalaryDetails(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
		sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}

	private void processExperience(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}

	private void processRelieving(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}

	private void processPromotion(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		insertEmpSalaryDetails(con, activityType, nEmpId, uF, strActivityName, nEmpActivityId);
		sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}

	private void insertEmpSalaryDetails(Connection con, int activityType, int nEmpId, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			pst = con.prepareStatement("select * from salary_details where level_id=?");
			pst.setInt(1, uF.parseToInt(getStrLevel()));
			rs = pst.executeQuery();
			Map<String, String> hmEarningDeductionMap = new HashMap<String, String>();
			Map<String, String> hmSalaryTypeMap = new HashMap<String, String>();
			while(rs.next()){
				hmEarningDeductionMap.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
				hmSalaryTypeMap.put(rs.getString("salary_head_id"), rs.getString("salary_type"));
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("UPDATE employee_official_details SET grade_id=? where emp_id=?");
			pst.setInt(1, uF.parseToInt(getEmpGrade()));
			pst.setInt(2, nEmpId);
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0){
				
				List<FillSalaryHeads> salaryHeadList = new ArrayList<FillSalaryHeads>();
//				salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads();
				salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads(getStrLevel());
				
				EmployeeSalaryDetails salaryDetails = new EmployeeSalaryDetails();
				salaryDetails.CF = CF;
				salaryDetails.request = request;
				salaryDetails.empId = ""+nEmpId;
				salaryDetails.salaryHeadList=salaryHeadList;
				salaryDetails.viewEmployeeSalaryDetails();		
//				System.out.println("reportList======>"+request.getAttribute("reportList"));
				List<List<String>> reportList = (List<List<String>>) request.getAttribute("reportList");
				if(reportList == null) reportList = new ArrayList<List<String>>();
				
				if(reportList!=null && !reportList.isEmpty()){
					Map<String, Map<String, String>> hmEmpSalaryMap = new HashMap<String, Map<String, String>>(); 
					for(int i = 0; i < reportList.size(); i++){
						List<String> alInner = (List<String>) reportList.get(i);
						Map<String, String> hmInner = new HashMap<String, String>();
						hmInner.put("SALARY_HEAD_ID", alInner.get(0));
						hmInner.put("SALARY_HEAD_NAME", alInner.get(1));
						hmInner.put("EARNING_DEDUCTION", alInner.get(2));
						hmInner.put("SALARY_HEAD_AMOUNT_TYPE", alInner.get(3));
						hmInner.put("SUB_SALARY_HEAD_ID", alInner.get(4));
						hmInner.put("SUB_SALARY_HEAD_NAME", alInner.get(5));
						hmInner.put("SALARY_HEAD_AMOUNT", alInner.get(6));
						
						hmEmpSalaryMap.put(alInner.get(0), hmInner);
					}
//					System.out.println("hmEmpSalaryMap======>"+hmEmpSalaryMap);
					
					if (hmEmpSalaryMap!=null && !hmEmpSalaryMap.isEmpty()) {
						Map<String,Map<String,String>> hmCalSalaryMap = new HashMap<String, Map<String,String>>();
						Iterator<String> it = hmEmpSalaryMap.keySet().iterator();
						while (it.hasNext()) {
							String slaryHeadId = (String) it.next();
							Map<String, String> hmInner = hmEmpSalaryMap.get(slaryHeadId);
							if(hmInner.get("EARNING_DEDUCTION").equals("E")){
								if(hmInner.get("SALARY_HEAD_AMOUNT_TYPE").equals("P")){
									double dblSalaryHeadPercentage = uF.parseToDouble(hmInner.get("SALARY_HEAD_AMOUNT"));
									double dblPerAmt = 0.00;
									Map<String, String> hmSalaryHead= hmEmpSalaryMap.get(hmInner.get("SUB_SALARY_HEAD_ID"));
									
									double dblSumSalAmt = uF.parseToDouble(hmSalaryHead.get("AMOUNT"));
									dblPerAmt = (dblSumSalAmt * dblSalaryHeadPercentage)/100;
									
									Map<String, String> hm = new HashMap<String, String>();
									hm.put("SALARY_HEAD_ID", slaryHeadId);
									hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
									hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(dblPerAmt));
									
									hmCalSalaryMap.put(slaryHeadId, hm);
								} else {
									Map<String, String> hm = new HashMap<String, String>();
									hm.put("SALARY_HEAD_ID", slaryHeadId);
									hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
									hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("AMOUNT"))));
									
									hmCalSalaryMap.put(slaryHeadId, hm);
								}
							} else if(hmInner.get("EARNING_DEDUCTION").equals("D")){
								if(hmInner.get("SALARY_HEAD_AMOUNT_TYPE").equals("P")){
									double dblSalaryHeadPercentage = uF.parseToDouble(hmInner.get("SALARY_HEAD_AMOUNT"));
									double dblPerAmt = 0.00;
									Map<String, String> hmSalaryHead= hmEmpSalaryMap.get(hmInner.get("SUB_SALARY_HEAD_ID"));
									
									double dblSumSalAmt = uF.parseToDouble(hmSalaryHead.get("AMOUNT"));
									dblPerAmt = (dblSumSalAmt * dblSalaryHeadPercentage)/100;
									
									Map<String, String> hm = new HashMap<String, String>();
									hm.put("SALARY_HEAD_ID", slaryHeadId);
									hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
									hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(dblPerAmt));
									
									hmCalSalaryMap.put(slaryHeadId, hm);
								} else {
									Map<String, String> hm = new HashMap<String, String>();
									hm.put("SALARY_HEAD_ID", slaryHeadId);
									hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
									hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("AMOUNT"))));
									
									hmCalSalaryMap.put(slaryHeadId, hm);
								}
							}
						}
						
//						System.out.println("hmCalSalaryMap======>"+hmCalSalaryMap);
						Iterator<String> it1 = hmCalSalaryMap.keySet().iterator();
						while (it1.hasNext()) {
							String strSalaryHeadId = (String) it1.next();
							Map<String, String> hm = hmCalSalaryMap.get(strSalaryHeadId);
								
							pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id, salary_head_id, " +
									"amount, entry_date, user_id, pay_type, isdisplay, service_id, effective_date, " +
									"earning_deduction, salary_type,is_approved,level_id) " +
									"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
							pst.setInt(1, nEmpId);
							pst.setInt(2, uF.parseToInt(strSalaryHeadId));
							pst.setDouble(3, uF.parseToDouble(hm.get("AMOUNT")));
							pst.setDate	(4, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
//							pst.setDate(4, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setString(6, "M");
							pst.setBoolean(7, true);
							pst.setInt(8, 0);
							pst.setDate	(9, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
							pst.setString(10, hm.get("EARNING_DEDUCTION"));
							pst.setString(11, hmSalaryTypeMap.get(strSalaryHeadId));
							pst.setBoolean(12, true);
							pst.setInt(13, uF.parseToInt(getStrLevel()));
							pst.execute();
							pst.close();
							
						}  
						
						CF.updateNextEmpSalaryEffectiveDate(con, uF, nEmpId, getEffectiveDate(), DATE_FORMAT);
						
						/**
						 * Calaculate CTC
						 * */					
						Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, null, ""+nEmpId);
						
						MyProfile myProfile = new MyProfile();
						myProfile.session = session;
						myProfile.request = request;
						myProfile.CF = CF;
						int intEmpIdReq = nEmpId;
						myProfile.getSalaryHeadsforEmployee(con, uF, intEmpIdReq, hmEmpProfile);
						
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
								if(uF.parseToInt(innerList.get(4)) == EMPLOYEE_ESI){
									dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
									dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
								} else if(uF.parseToInt(innerList.get(4)) == EMPLOYER_ESI){
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
						if(isEPF || isESIC || isLWF){
							if(isEPF){
								double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
								double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
								dblMonthContri += dblEPFMonth;
								dblAnnualContri += dblEPFAnnual;
							}
							if(isESIC){
								double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
								double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
								dblMonthContri += dblESIMonth;
								dblAnnualContri += dblESIAnnual;
							}
							if(isLWF){
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
						if(nAnnualVariSize > 0){
							double grossAnnualAmount = 0.0d;
							double grossAnnualYearAmount = 0.0d;
							for(int i = 0; i < nAnnualVariSize; i++){
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
					}				
				}			
			}
			
			int ServiceNo = uF.parseToInt((String)session.getAttribute("ServicesLinkNo"));
			session.setAttribute("ServicesLinkNo", 1+"");
			
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

	private void processIncrement(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
	
		updateEmployeeSalaryDetails(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
		sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}

	private void processConfirmation(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
				pst = con.prepareStatement("update employee_personal_details set emp_status =?  where emp_per_id=?");
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
		
		updateEmployeeSalaryDetails(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
		sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}

	private void updateEmployeeSalaryDetails(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			String nEmpLevelId = CF.getEmpLevelId(con, ""+nEmpId);
			
			//code for getting userid
			pst = con.prepareStatement("select * from salary_details where level_id=?");
			pst.setInt(1, uF.parseToInt(nEmpLevelId));
			rs = pst.executeQuery();
			Map<String, String> hmEarningDeductionMap = new HashMap<String, String>();
			Map<String, String> hmSalaryTypeMap = new HashMap<String, String>();
			while(rs.next()){
				hmEarningDeductionMap.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
				hmSalaryTypeMap.put(rs.getString("salary_head_id"), rs.getString("salary_type"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from emp_salary_details where entry_date = ? and emp_id = ?");
			pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setInt(2, nEmpId);
			rs = pst.executeQuery();
			boolean isCurrentDateExist = false;
			while(rs.next()){
				isCurrentDateExist = true;
			}
			rs.close();
			pst.close();
			
			if(isCurrentDateExist){
				pst = con.prepareStatement("delete from emp_salary_details where entry_date = ? and emp_id = ?");
				pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
				pst.setInt(2, nEmpId);
				pst.execute();
				pst.close();
			}
			
			List<FillSalaryHeads> salaryHeadList = new ArrayList<FillSalaryHeads>();
//			salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads();
			salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads(""+nEmpLevelId);
			
			int strIncrementType = uF.parseToInt(getStrIncrementType());
			double dblIncremnetPercent = 0.00;
			if(strIncrementType == 2){
				dblIncremnetPercent = uF.parseToDouble(getStrIncrementPercentage()) * 2;
			} else {
				dblIncremnetPercent = uF.parseToDouble(getStrIncrementPercentage());
			}
			
			
			if(dblIncremnetPercent > 0.0) {
				EmployeeSalaryDetails salaryDetails = new EmployeeSalaryDetails();
				salaryDetails.CF = CF;
				salaryDetails.request = request;
				salaryDetails.empId = ""+nEmpId;
				salaryDetails.salaryHeadList=salaryHeadList;
				salaryDetails.viewUpdateEmployeeSalaryDetails();		
//				System.out.println("reportList======>"+request.getAttribute("reportList"));
				List<List<String>> reportList = (List<List<String>>) request.getAttribute("reportList");
				if(reportList == null) reportList = new ArrayList<List<String>>();
				
				if(reportList!=null && !reportList.isEmpty()){
					Map<String, Map<String, String>> hmEmpSalaryMap = new HashMap<String, Map<String, String>>(); 
					for(int i = 0; i < reportList.size(); i++){
						List<String> alInner = (List<String>) reportList.get(i);
						Map<String, String> hmInner = new HashMap<String, String>();
						hmInner.put("EMP_SALARY_ID", alInner.get(0));
						hmInner.put("SALARY_HEAD_ID", alInner.get(1));
						hmInner.put("SALARY_HEAD_NAME", alInner.get(2));
						hmInner.put("EARNING_DEDUCTION", alInner.get(3));
						hmInner.put("SALARY_HEAD_AMOUNT_TYPE", alInner.get(4));
						hmInner.put("SUB_SALARY_HEAD_ID", alInner.get(5));
						hmInner.put("SUB_SALARY_HEAD_NAME", alInner.get(6));
						hmInner.put("SALARY_HEAD_AMOUNT", alInner.get(7));
						hmInner.put("AMOUNT", alInner.get(8));
						hmInner.put("ENTRY_DATE", alInner.get(9));
						hmInner.put("USER_ID", alInner.get(10));
						hmInner.put("PAY_TYPE", alInner.get(11));
						hmInner.put("IS_DISPLAY", alInner.get(12));
						hmInner.put("WEIGHT", alInner.get(13));
						
						hmEmpSalaryMap.put(alInner.get(1), hmInner);
					}
//					System.out.println("hmEmpSalaryMap======>"+hmEmpSalaryMap);
					
					if (hmEmpSalaryMap!=null && !hmEmpSalaryMap.isEmpty()) {
						
						Map<String,Map<String,String>> hmCalSalaryMap = new HashMap<String, Map<String,String>>();
						
						Map<String, String> hmSalaryHeadId1= hmEmpSalaryMap.get(""+1);
						if(hmSalaryHeadId1!=null){
							double txt_amount = uF.parseToDouble(hmSalaryHeadId1.get("AMOUNT"));
							double newBasic = (txt_amount * dblIncremnetPercent) / 100;						
							double finalBasic = txt_amount + newBasic;
//							System.out.println("txt_amount======>"+txt_amount);
//							System.out.println("newBasic======>"+newBasic);
//							System.out.println("finalBasic======>"+finalBasic);
							
							Iterator<String> it = hmEmpSalaryMap.keySet().iterator();
							while (it.hasNext()) {
								String slaryHeadId = (String) it.next();
								Map<String, String> hmInner = hmEmpSalaryMap.get(slaryHeadId);
								
								if(hmInner.get("EARNING_DEDUCTION").equals("E")){
									if(hmInner.get("SALARY_HEAD_AMOUNT_TYPE").equals("P")){
										if(uF.parseToBoolean(hmInner.get("IS_DISPLAY"))){
											double dblSalaryHeadPercentage = uF.parseToDouble(hmInner.get("SALARY_HEAD_AMOUNT"));
											double dblPerAmt = 0.00;
											Map<String, String> hmSalaryHead= hmEmpSalaryMap.get(hmInner.get("SUB_SALARY_HEAD_ID"));
											if(uF.parseToInt(hmSalaryHead.get("SALARY_HEAD_ID")) == 1){
												dblPerAmt = (finalBasic * dblSalaryHeadPercentage)/100;
											} else {
												double dblSumSalAmt = uF.parseToDouble(hmSalaryHead.get("AMOUNT"));
												dblPerAmt = (dblSumSalAmt * dblSalaryHeadPercentage)/100;
											}
											
											Map<String, String> hm = new HashMap<String, String>();
											hm.put("SALARY_HEAD_ID", slaryHeadId);
											hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
											hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(dblPerAmt));
											hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
											
											hmCalSalaryMap.put(slaryHeadId, hm);
										} else {
											Map<String, String> hm = new HashMap<String, String>();
											hm.put("SALARY_HEAD_ID", slaryHeadId);
											hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
											hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("AMOUNT"))));
											hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
											
											hmCalSalaryMap.put(slaryHeadId, hm);
										}
									} else {
										
										if(uF.parseToInt(slaryHeadId) == 1){
											Map<String, String> hm = new HashMap<String, String>();
											hm.put("SALARY_HEAD_ID", slaryHeadId);
											hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
											hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(finalBasic));
											hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
											
											hmCalSalaryMap.put(slaryHeadId, hm);
											
										} else{
											Map<String, String> hm = new HashMap<String, String>();
											hm.put("SALARY_HEAD_ID", slaryHeadId);
											hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
											hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("AMOUNT"))));
											hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
											
											hmCalSalaryMap.put(slaryHeadId, hm);
										}
									}
								} else if(hmInner.get("EARNING_DEDUCTION").equals("D")){
									if(hmInner.get("SALARY_HEAD_AMOUNT_TYPE").equals("P")){
										if(uF.parseToBoolean(hmInner.get("IS_DISPLAY"))){
											double dblSalaryHeadPercentage = uF.parseToDouble(hmInner.get("SALARY_HEAD_AMOUNT"));
											double dblPerAmt = 0.00;
											Map<String, String> hmSalaryHead= hmEmpSalaryMap.get(hmInner.get("SUB_SALARY_HEAD_ID"));
											if(uF.parseToInt(hmSalaryHead.get("SALARY_HEAD_ID")) == 1){
												dblPerAmt = (finalBasic * dblSalaryHeadPercentage)/100;
											} else {
												double dblSumSalAmt = uF.parseToDouble(hmSalaryHead.get("AMOUNT"));
												dblPerAmt = (dblSumSalAmt * dblSalaryHeadPercentage)/100;
											}
											
											Map<String, String> hm = new HashMap<String, String>();
											hm.put("SALARY_HEAD_ID", slaryHeadId);
											hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
											hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(dblPerAmt));
											hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
											
											hmCalSalaryMap.put(slaryHeadId, hm);
										} else {
											Map<String, String> hm = new HashMap<String, String>();
											hm.put("SALARY_HEAD_ID", slaryHeadId);
											hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
											hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("AMOUNT"))));
											hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
											
											hmCalSalaryMap.put(slaryHeadId, hm);
										}
									} else {
										Map<String, String> hm = new HashMap<String, String>();
										hm.put("SALARY_HEAD_ID", slaryHeadId);
										hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
										hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("AMOUNT"))));
										hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
										
										hmCalSalaryMap.put(slaryHeadId, hm);
									}
								}
							}
							
							
						} else {
							
							Iterator<String> it = hmEmpSalaryMap.keySet().iterator();
							while (it.hasNext()) {
								String slaryHeadId = (String) it.next();
								Map<String, String> hmInner = hmEmpSalaryMap.get(slaryHeadId);
								
								if(hmInner.get("EARNING_DEDUCTION").equals("E")){
									if(hmInner.get("SALARY_HEAD_AMOUNT_TYPE").equals("P")){
										if(uF.parseToBoolean(hmInner.get("IS_DISPLAY"))){
											double dblSalaryHeadPercentage = uF.parseToDouble(hmInner.get("SALARY_HEAD_AMOUNT"));
											double dblPerAmt = 0.00;
											Map<String, String> hmSalaryHead= hmEmpSalaryMap.get(hmInner.get("SUB_SALARY_HEAD_ID"));										
											double txt_amount = uF.parseToDouble(hmSalaryHead.get("AMOUNT"));
											double dblNew = (txt_amount * dblIncremnetPercent) / 100;						
											double dblFinal = txt_amount + dblNew;
											dblPerAmt = (dblFinal * dblSalaryHeadPercentage)/100;
											
											Map<String, String> hm = new HashMap<String, String>();
											hm.put("SALARY_HEAD_ID", slaryHeadId);
											hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
											hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(dblPerAmt));
											hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
											
											hmCalSalaryMap.put(slaryHeadId, hm);
										} else {
											
											Map<String, String> hm = new HashMap<String, String>();
											hm.put("SALARY_HEAD_ID", slaryHeadId);
											hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
											hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("AMOUNT"))));
											hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
											
											hmCalSalaryMap.put(slaryHeadId, hm);
										}
									} else {
										double dblPerAmt = 0.00;
										double txt_amount = uF.parseToDouble(hmInner.get("AMOUNT"));
										double dblNew = (txt_amount * dblIncremnetPercent) / 100;						
										double dblFinal = txt_amount + dblNew;
										dblPerAmt = dblFinal;
										
										Map<String, String> hm = new HashMap<String, String>();
										hm.put("SALARY_HEAD_ID", slaryHeadId);
										hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
										hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(dblPerAmt));
										hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
										
										hmCalSalaryMap.put(slaryHeadId, hm);
									}
								} else if(hmInner.get("EARNING_DEDUCTION").equals("D")){
									if(hmInner.get("SALARY_HEAD_AMOUNT_TYPE").equals("P")){
										if(uF.parseToBoolean(hmInner.get("IS_DISPLAY"))){
											double dblSalaryHeadPercentage = uF.parseToDouble(hmInner.get("SALARY_HEAD_AMOUNT"));
											double dblPerAmt = 0.00;
											Map<String, String> hmSalaryHead= hmEmpSalaryMap.get(hmInner.get("SUB_SALARY_HEAD_ID"));										
											double txt_amount = uF.parseToDouble(hmSalaryHead.get("AMOUNT"));
											double dblNew = (txt_amount * dblIncremnetPercent) / 100;						
											double dblFinal = txt_amount + dblNew;
											dblPerAmt = (dblFinal * dblSalaryHeadPercentage)/100;
											
											Map<String, String> hm = new HashMap<String, String>();
											hm.put("SALARY_HEAD_ID", slaryHeadId);
											hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
											hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(dblPerAmt));
											hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
											
											hmCalSalaryMap.put(slaryHeadId, hm);
										} else {
											
											Map<String, String> hm = new HashMap<String, String>();
											hm.put("SALARY_HEAD_ID", slaryHeadId);
											hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
											hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("AMOUNT"))));
											hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
											
											hmCalSalaryMap.put(slaryHeadId, hm);
										}
									} else {
										double dblPerAmt = 0.00;
										double txt_amount = uF.parseToDouble(hmInner.get("AMOUNT"));
										double dblNew = (txt_amount * dblIncremnetPercent) / 100;						
										double dblFinal = txt_amount + dblNew;
										dblPerAmt = dblFinal;
										
										Map<String, String> hm = new HashMap<String, String>();
										hm.put("SALARY_HEAD_ID", slaryHeadId);
										hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
										hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(dblPerAmt));
										hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
										
										hmCalSalaryMap.put(slaryHeadId, hm);
									}
								}
							}
						}
						
//						System.out.println("hmCalSalaryMap======>"+hmCalSalaryMap);
						Iterator<String> it = hmCalSalaryMap.keySet().iterator();
						while (it.hasNext()) {
							String strSalaryHeadId = (String) it.next();
							Map<String, String> hm = hmCalSalaryMap.get(strSalaryHeadId);
								
							pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id, " +
									"salary_head_id, amount, entry_date, user_id, pay_type, isdisplay, " +
									"service_id, effective_date, earning_deduction, salary_type,is_approved,level_id) " +
									"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
							pst.setInt(1, nEmpId);
							pst.setInt(2, uF.parseToInt(strSalaryHeadId));
							pst.setDouble(3, uF.parseToDouble(hm.get("AMOUNT")));
							pst.setDate	(4, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setString(6, "M");
							pst.setBoolean(7, uF.parseToBoolean(hm.get("IS_DISPLAY")));
							pst.setInt(8, 0);
							pst.setDate	(9, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
							pst.setString(10, hm.get("EARNING_DEDUCTION"));
							pst.setString(11, hmSalaryTypeMap.get(strSalaryHeadId));
							pst.setBoolean(12, true);
							pst.setInt(13, uF.parseToInt(nEmpLevelId));
							pst.execute();
							pst.close();
						}
						
						CF.updateNextEmpSalaryEffectiveDate(con, uF, nEmpId, getEffectiveDate(), DATE_FORMAT);
						
						/**
						 * Calaculate CTC
						 * */					
						Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, null, ""+nEmpId);
						
						MyProfile myProfile = new MyProfile();
						myProfile.session = session;
						myProfile.request = request;
						myProfile.CF = CF;
						int intEmpIdReq = nEmpId;
						myProfile.getSalaryHeadsforEmployee(con, uF, intEmpIdReq, hmEmpProfile);
						
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
								if(uF.parseToInt(innerList.get(4)) == EMPLOYEE_ESI){
									dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
									dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
								} else if(uF.parseToInt(innerList.get(4)) == EMPLOYER_ESI){
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
						if(isEPF || isESIC || isLWF){
							if(isEPF){
								double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
								double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
								dblMonthContri += dblEPFMonth;
								dblAnnualContri += dblEPFAnnual;
							}
							if(isESIC){
								double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
								double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
								dblMonthContri += dblESIMonth;
								dblAnnualContri += dblESIAnnual;
							}
							if(isLWF){
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
						if(nAnnualVariSize > 0){
							double grossAnnualAmount = 0.0d;
							double grossAnnualYearAmount = 0.0d;
							for(int i = 0; i < nAnnualVariSize; i++){
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
					}
					
				}
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
	
	private void processTemporary(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		PreparedStatement pst = null;
		try {
			
			pst = con.prepareStatement(updateUserStatus);
			pst.setString(1, IConstants.TEMPORARY);
			pst.setInt(2, nEmpId);
			pst.execute();
			pst.close();
			
			sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
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

	
	private void processPermanent(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		PreparedStatement pst = null;
		
		try {
			
			pst = con.prepareStatement(updateUserStatus);
			pst.setString(1, IConstants.PERMANENT);
			pst.setInt(2, nEmpId);
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0){
				String[] leaveTypeId = (String[]) request.getParameterValues("leaveTypeId");
				for(int i = 0; leaveTypeId != null && i < leaveTypeId.length; i++){
					String strLeaveBalance = (String) request.getParameter("leaveBal_"+leaveTypeId[i]);
					
					pst=con.prepareStatement("delete from leave_register1 where emp_id=? and _date=? and leave_type_id=? and _type=?");
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
			sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
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
	
	
	private void processOfferLetter(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}

	
	private void processGradeChange(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
	
		PreparedStatement pst = null;
		
		try {
			
			pst = con.prepareStatement("UPDATE employee_official_details SET grade_id=? where emp_id=?");
			pst.setInt(1, ((getEmpChangeGrade()!=null && getEmpChangeGrade().length()>0) ? uF.parseToInt(getEmpChangeGrade()) : uF.parseToInt(getEmpGrade())));
			pst.setInt(2, nEmpId);
			pst.execute();
			pst.close();
			
			sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
			
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

	
	private void processAppointmentLetter(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);		
	}

	private void processWithdrawnResignation(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
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
			
				sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
			}
			
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

	
	private void processTransfer(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
			Date effectDate = uF.getDateFormat(getEffectiveDate(), DATE_FORMAT);
			int dateResult = effectDate.compareTo(currDate);
			
			if(dateResult < 1) {
				int departHod = 0;
				pst = con.prepareStatement("select emp_id from employee_official_details where depart_id = ? and is_hod = true limit 1");
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
				if(uF.parseToInt(getEmpGrade()) > 0) {
					sb.append(", grade_id= "+uF.parseToInt(getEmpGrade())+" ");
				}
				if(uF.parseToInt(getStrOrganisation()) > 0) {
					sb.append(", org_id= "+uF.parseToInt(getStrOrganisation())+" ");
				}
				if(departHod > 0) {
					sb.append(", hod_emp_id= "+departHod+" ");
				}
				sb.append(" WHERE emp_id=?");
				pst = con.prepareStatement(sb.toString());
				pst.setInt(1, nEmpId);
				pst.execute();
				pst.close();
				
				if(uF.parseToInt(getStrWLocation()) > 0 && getStrTransferType() !=null && getStrTransferType().trim().equalsIgnoreCase("WL")){
					String strDomain = request.getServerName().split("\\.")[0];
					AssignLeaveCron leaveCron = new AssignLeaveCron();
					leaveCron.request = request;
					leaveCron.session = session;
					leaveCron.CF = CF;
					leaveCron.strDomain = strDomain;
					leaveCron.strWlocationId = getStrWLocation();
					leaveCron.strEmpId = ""+nEmpId;
					leaveCron.setCronData();
				} else if(uF.parseToInt(getStrLevel()) > 0 && getStrTransferType() !=null && getStrTransferType().trim().equalsIgnoreCase("LE")){
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
				
				sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
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

	
	private void processTerminate(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			if (CF.getIsTerminateWithoutFullAndFinal()) {
				pst = con.prepareStatement("UPDATE user_details SET status=? where emp_id=?");
				pst.setString(1, "INACTIVE");
				pst.setInt(2, nEmpId);
				// System.out.println("pst1==>"+pst);
				pst.execute();
				pst.close();

				pst = con.prepareStatement("UPDATE employee_personal_details SET emp_status=?, is_alive=?, employment_end_date=? where emp_per_id=?");
				pst.setString(1, TERMINATED);
				pst.setBoolean(2, false);
				pst.setDate(3, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setInt(4, nEmpId);
				// System.out.println("pst2==>"+pst);
				pst.execute();
				pst.close();

			} else {
				Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
				Date effectDate = uF.getDateFormat(getEffectiveDate(), DATE_FORMAT);
				int dateResult = effectDate.compareTo(currDate);
				
	//			System.out.println("dateResult ===>> " + dateResult);
				
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
					pst.execute();
					pst.close();
					
					if(strUserType!=null && strUserType.equals(ADMIN)){
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
					}else{
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
					
					if(x > 0){
						pst = con.prepareStatement("select max(off_board_id) as off_board_id from emp_off_board");
						rs = pst.executeQuery();
						int nOffBoardId = 0;
						while(rs.next()){
							nOffBoardId = uF.parseToInt(rs.getString("off_board_id"));
						}
						
						rs.close();
						pst.close();
						
						if(nOffBoardId > 0){
							String policy_id=null;
							int empId = 0;
							int userTypeId = 0;
							Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
							String empLevelId=hmEmpLevelMap.get(""+nEmpId);
							Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
							String locationID=hmEmpWlocationMap.get(""+nEmpId);
							
							Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
						
							pst = con.prepareStatement("select policy_id from work_flow_policy_details where type='"+WORK_FLOW_TERMINATION+"' and level_id=? and wlocation_id=?");
							pst.setInt(1, uF.parseToInt(empLevelId));
							pst.setInt(2, uF.parseToInt(locationID));
	//						System.out.println("pst====>"+pst);
							rs = pst.executeQuery();
							while(rs.next()){
								policy_id=rs.getString("policy_id");
							}
							rs.close();
							pst.close(); 
							
							if(uF.parseToInt(policy_id) == 0){
								pst = con.prepareStatement("select policy_count from work_flow_member wfm,work_flow_policy wfp where wfp.group_id=wfm.group_id " +
										"and wfp.work_flow_member_id=wfm.work_flow_member_id and wfm.wlocation_id=? and wfm.is_default = true");
								pst.setInt(1, uF.parseToInt(locationID));
								rs = pst.executeQuery();
								while(rs.next()){
									policy_id=rs.getString("policy_count");
								}
								rs.close();
								pst.close();
							}
	//						System.out.println("policyId == >"+ policy_id);
							if(uF.parseToInt(policy_id) > 0){
								pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
								" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
								pst.setInt(1,uF.parseToInt(policy_id));
	//							System.out.println("pst 1==>"+pst);
								rs=pst.executeQuery();
								Map<String,List<String>> hmMemberMap=new LinkedHashMap<String, List<String>>();
								while(rs.next()){
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
								
								while(it.hasNext()){
									String work_flow_member_id=it.next();
									List<String> innerList=hmMemberMap.get(work_flow_member_id);
									
									if(uF.parseToInt(innerList.get(0))==1){
										int memid=uF.parseToInt(innerList.get(1));
										switch(memid){
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
										
									}
								}
							}
							
						}
						sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					}
				}
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

	private void processProbation(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
	
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
					pst.setInt(4, nEmpId);
					pst.execute();
					pst.close();
					
					flag = true;
				} else {
					flag = true;
				}
				
				if(flag){
					
					pst = con.prepareStatement("update employee_personal_details set emp_status =?  where emp_per_id=?");
					pst.setString(1, PROBATION);
					pst.setInt(2, nEmpId);
					pst.executeUpdate();
					pst.close();
				}
				
				sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
			}
			
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

	
	private void processNoticePeriod(Connection con, int activityType,int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
	
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
	//			System.out.println("pst==>"+pst);
				pst.executeUpdate();
				pst.close();
			}
			
			
			sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
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
	

	private void processExtendProbation(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
	
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		try {
			String effectiveDate = null;
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
					pst.setInt(4,nEmpId);
					pst.execute();
					pst.close();
					
					flag = true;
				}else{
					flag = true;
				}
				
				if(flag){
					pst = con.prepareStatement("update employee_personal_details set emp_status =?  where emp_per_id=?");
					pst.setString(1, PROBATION);
					pst.setInt(2, nEmpId);
					pst.executeUpdate();
					pst.close();
				}
				
				sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
		  }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null){
				try {
					rst.close();
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

	
	private void sendAttachDocument(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
	
	
		ResultSet rst = null;
		PreparedStatement pst = null;
	
		try {
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
			Map<String, String> hmEmpInner = hmEmpInfo.get(""+nEmpId);
			
			StringBuilder sbEmpSalTable = CF.getEmployeeSalaryDetails(con, CF, uF, ""+nEmpId, request, session);
			if(sbEmpSalTable == null) sbEmpSalTable = new StringBuilder();
			
			String empOrgId = CF.getEmpOrgId(con, uF, ""+nEmpId);
			
			Map<String, Map<String, String>> hmHeader=new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmFooter=new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select * from document_collateral");
			rst = pst.executeQuery();
	//		System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				if(rst.getString("_type").equals("H")){
					Map<String, String> hmInner=new HashMap<String, String>();
					hmInner.put("COLLATERAL_ID", rst.getString("collateral_id"));
					hmInner.put("COLLATERAL_PATH", rst.getString("collateral_image"));
					hmInner.put("COLLATERAL_IMG_ALIGN", rst.getString("image_align"));
					hmInner.put("COLLATERAL_TEXT", uF.showData(rst.getString("collateral_text"),""));
					
					hmHeader.put(rst.getString("collateral_id"), hmInner);
				}else{
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
			while(rst.next()){
				hmMapActivityNode.put(rst.getString("mapped_activity_id"), rst.getString("node_id"));
			}
			rst.close();
			pst.close();
			int nTriggerNode = uF.parseToInt(hmMapActivityNode.get(""+activityType));
			
			String strDocumentName = null;
			String strDocumentContent = null;
			String strDocumentHeader = null;
			String strDocumentFooter = null;
			String strHeader = null;
			String strFooter = null;
			String strHeaderImageAlign="";
			String strHeaderCollateralText="";
			String strHeaderTextAlign="";
			String strFooterImageAlign="";
			String strFooterCollateralText="";
			String strFooterTextAlign="";
			
			if(nTriggerNode > 0){
				pst = con.prepareStatement("select * from document_comm_details where trigger_nodes like '%,"+nTriggerNode+",%' and status=1 and org_id=? order by document_id desc limit 1");
				pst.setInt(1, uF.parseToInt(empOrgId));
				rst = pst.executeQuery();
		//		System.out.println("new Date ===> " + new Date());
				while (rst.next()) {  
					strDocumentName = rst.getString("document_name");
					strDocumentContent = rst.getString("document_text");
					
					if(rst.getString("collateral_header")!=null && !rst.getString("collateral_header").equals("") && hmHeader.get(rst.getString("collateral_header"))!=null){
						Map<String, String> hmInner=hmHeader.get(rst.getString("collateral_header"));
						strHeader = uF.showData(hmInner.get("COLLATERAL_PATH"),"");
						strHeaderImageAlign=uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"),"");
						strHeaderCollateralText=uF.showData(hmInner.get("COLLATERAL_TEXT"),"");
						strHeaderTextAlign=uF.showData(hmInner.get("COLLATERAL_TEXT_ALIGN"),"");
					}
					if(rst.getString("collateral_footer")!=null && !rst.getString("collateral_footer").equals("") && hmFooter.get(rst.getString("collateral_footer"))!=null){
						Map<String, String> hmInner=hmFooter.get(rst.getString("collateral_footer"));
						strFooter = uF.showData(hmInner.get("COLLATERAL_PATH"),"");
						strFooterImageAlign=uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"),"");
						strFooterCollateralText=uF.showData(hmInner.get("COLLATERAL_TEXT"),"");
						strFooterTextAlign=uF.showData(hmInner.get("COLLATERAL_TEXT_ALIGN"),"");
					}
				}
				rst.close();
				pst.close();
			}
			if(strDocumentName!=null){
	//			strDocumentName = strDocumentName.replace(" ", "");
				strDocumentName = strDocumentName!=null ? strDocumentName.trim() : "";
			}
			
			String strDomain = request.getServerName().split("\\.")[0];
			
			Notifications nF = new Notifications(N_NEW_ACTIVITY, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrEmpId(""+nEmpId);  
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
	//		nF.setStrContextPath(request.getServletPath());
			nF.setStrContextPath(request.getContextPath());
			 
			nF.setStrEmpFname(hmEmpInner.get("FNAME"));
			nF.setStrEmpLname(hmEmpInner.get("LNAME"));
			nF.setStrSalaryStructure(sbEmpSalTable.toString());
			nF.setStrActivityName(strActivityName);
			
			if(getEffectiveDate() == null || getEffectiveDate().equals("")) {
				setEffectiveDate(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT));
			}
			
			if(getEffectiveDate() != null && !getEffectiveDate().equals("") ) {
				nF.setStrEffectiveDate(uF.getDateFormat(getEffectiveDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
			}
			nF.setStrPromotionDate(uF.getDateFormat(getEffectiveDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
			nF.setStrIncrementPercent(""+uF.parseToDouble(getStrIncrementPercentage()));
//			nF.setStrPayStructure(sbEmpSalTable.toString());
			
			Map<String, String> hmParsedContent = null;
	
	//		Document document = new Document(PageSize.A4);
			Document document = new Document(PageSize.A4,40, 40, 10, 60); 
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			StringBuilder sbHeader = new StringBuilder();
			StringBuilder sbFooter = new StringBuilder();
			String strDocName = null;
			String strDocContent = null;
			if(strDocumentContent!=null){
				
	//			hmParsedContent  = nF.parseContent(strDocumentContent, "", "");
				hmParsedContent  = nF.parseContent(strDocumentContent, "", "");
				
				
				strDocName = strDocumentName;
				strDocContent = hmParsedContent.get("MAIL_BODY");
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
				
				String headerPath="";
				if(strHeader!=null && !strHeader.equals("")){
	//				headerPath=CF.getStrDocRetriveLocation()+strHeader;
					if(CF.getStrDocRetriveLocation()==null) { 
						headerPath =  DOCUMENT_LOCATION + strHeader;
					} else { 
						headerPath = CF.getStrDocSaveLocation() +I_COLLATERAL+"/"+I_IMAGE+"/"+strHeader;
					}
				}
				
				
	//			if(headerPath != null && !headerPath.equals("")) {
	//				//sbHeader.append("<table><tr><td><img height=\"60\" src=\""+strDocumentHeader+"\"></td></tr></table>");
	//				sbHeader.append("<table style=\"width: 100%;\"><tr>");
	//				if(strHeaderImageAlign!=null && strHeaderImageAlign.equals("R")) { 
	//					sbHeader.append("<td width=\"70%\" valign=\"middle\" style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>" +
	//							"<td align=\"right\">");
	//					if(headerPath != null && !headerPath.equals("")) {
	//						sbHeader.append("<img height=\"30\" src=\""+headerPath+"\">");
	//					}
	//					sbHeader.append("</td>");						
	//				
	//				} else if(strHeaderImageAlign !=null && strHeaderImageAlign.equals("C")) { 
	//					sbHeader.append("<td colspan=\"2\" align=\"Center\">");
	//					if(headerPath != null && !headerPath.equals("")) {
	//						sbHeader.append("<img height=\"30\" src=\""+headerPath+"\"><br/>");
	//					}
	//					sbHeader.append(""+strHeaderCollateralText+"</td>");
	//				} else {
	//					sbHeader.append("<td>");
	//					if(headerPath != null && !headerPath.equals("")) {
	//						sbHeader.append("<img height=\"30\" src=\""+headerPath+"\">");
	//					}
	//					sbHeader.append("</td> <td valign=\"middle\" style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>");
	//				}
	//				sbHeader.append("</tr></table>");
	//				
	//			} else {
	//				
	//				sbHeader.append("<table style=\"width: 100%;\"><tr>");
	//				if(strHeaderTextAlign!=null && strHeaderTextAlign.equals("R")) { 
	//					sbHeader.append("<td colspan=\"2\" align=\"right\" valign=\"middle\" style=\"padding-right: 50px;\">"+strHeaderCollateralText+"</td>");
	//					
	//				} else if(strHeaderTextAlign!=null && strHeaderTextAlign.equals("C")) { 
	//					sbHeader.append("<td colspan=\"2\" align=\"center\" valign=\"middle\">"+strHeaderCollateralText+"</td>");
	//				} else { 
	//					sbHeader.append("<td colspan=\"2\" valign=\"middle\" style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>");
	//				}
	//				sbHeader.append("</tr></table>");
	//			
	//			}
				
				if(headerPath != null && !headerPath.equals("")) {
					sbHeader.append("<table style=\"width: 100%;\"><tr>");
					sbHeader.append("<td>");
					if(strHeader!=null && !strHeader.equals("")) {
						sbHeader.append("<img src=\""+headerPath+"\">");
					}
					sbHeader.append("</td>");	
					sbHeader.append("</tr></table>");
				}
				
				
	//			String footerPath="";   
	//			if(strFooter!=null && !strFooter.equals("")){
	////				footerPath=CF.getStrDocRetriveLocation()+strFooter;
	//				if(CF.getStrDocRetriveLocation()==null) { 
	//					footerPath =  DOCUMENT_LOCATION + strFooter;
	//				} else { 
	//					footerPath = CF.getStrDocRetriveLocation() +I_COLLATERAL+"/"+I_IMAGE+"/"+strFooter;
	//				}
	//			}
	//			
	//			if(footerPath != null && !footerPath.equals("")) {
	//				sbFooter.append("<table><tr>");
	//				if(strFooterImageAlign!=null && strFooterImageAlign.equals("R")) { 
	//					sbFooter.append("<td width=\"70%\" valign=\"middle\" style=\"padding-left: 50px;\">"+strFooterCollateralText+"</td> <td align=\"right\">");
	//					if(footerPath != null && !footerPath.equals("")) {
	//						sbFooter.append("<img height=\"60\" src=\""+footerPath+"\">");
	//					}
	//					sbFooter.append("</td>");						
	//				
	//				} else if(strFooterImageAlign!=null && strFooterImageAlign.equals("C")) { 
	//					sbFooter.append("<td align=\"Center\">");
	//					if(footerPath != null && !footerPath.equals("")) {
	//						sbFooter.append("<img height=\"60\" src=\""+footerPath+"\"><br/>");
	//					}
	//					sbFooter.append(""+strFooterCollateralText+"</td>");
	//				} else { 
	//					sbFooter.append("<td>");
	//					if(footerPath != null && !footerPath.equals("")) {
	//						sbFooter.append("<img height=\"60\" src=\""+footerPath+"\">");
	//					}
	//					sbFooter.append("</td> <td valign=\"middle\" style=\"padding-left: 50px;\">"+strFooterCollateralText+"</td>");
	//				}
	//				sbFooter.append("</tr></table>");
	//			} else {
	//
	//				sbFooter.append("<table><tr>");
	//				if(strFooterTextAlign!=null && strFooterTextAlign.equals("R")) { 
	//					sbFooter.append("<td colspan=\"2\" align=\"right\" valign=\"middle\" style=\"padding-right: 50px;\">"+strFooterCollateralText+"</td>");
	//				
	//				} else if(strFooterTextAlign!=null && strFooterTextAlign.equals("C")) { 
	//					sbFooter.append("<td colspan=\"2\" align=\"center\" valign=\"middle\">"+strFooterCollateralText+"</td>");
	//				} else { 
	//					sbFooter.append("<td colspan=\"2\" valign=\"middle\" style=\"padding-right: 50px;\">"+strFooterCollateralText+"</td>");
	//				}
	//				sbFooter.append("</tr></table>");
	//			
	//			}
				
				PdfWriter writer = PdfWriter.getInstance(document, buffer);
				HeaderFooterPageEvent event = new HeaderFooterPageEvent(sbHeader.toString(),strFooterCollateralText);
			    writer.setPageEvent(event);
				
				document.open();
				
	//			System.out.println("strDocument ====> " +strDocument);
				HTMLWorker hw = new HTMLWorker(document);
	//			hw.parse(new StringReader(sbHeader.toString())); 
				hw.parse(new StringReader(strDocument));
	//			hw.parse(new StringReader(sbFooter.toString()));
				document.close();  
			
			}
			
			byte[] bytes = buffer.toByteArray();			
			
			if(strDocumentContent!=null && !strDocumentContent.trim().equals("") && !strDocumentContent.trim().equalsIgnoreCase("NULL") 
					&& getStrUpdate() == null && getStrUpdateDocument()!=null && !getStrUpdateDocument().trim().equals("") && !getStrUpdateDocument().trim().equalsIgnoreCase("NULL")) {
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName(strDocumentName+".pdf");
			}
			String strMailSubject = nF.getStrEmailSubject();
			String strMailBody = nF.getStrNewEmailBody();
	
			nF.setEmailTemplate(true);
			nF.sendNotifications(); 
			
	//		if(strDocumentContent!=null && getStrUpdateDocument()!=null) {
				saveDocumentActivity(con, uF, CF,nEmpId, strDocName, sbHeader.toString(), strDocContent, strFooterCollateralText, strMailSubject, strMailBody, nEmpActivityId);
	//		}
			
		} catch (Exception e) {  
			e.printStackTrace();
		} finally {
			if(rst !=null){
				try {
					rst.close();
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

	
	private void saveDocumentActivity(Connection con, UtilityFunctions uF,CommonFunctions CF, int nEmpId, String strDocumentName, String strDocumentHeader, String strDocumentContent, String strDocumentFooter, String strMailSubject, String strMailBody,int nEmpActivityId){
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("insert into document_activities (document_name, document_content, effective_date, entry_date, user_id, emp_id, " +
					"mail_subject, mail_body, document_header, document_footer,emp_activity_id) values (?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setString(1, strDocumentName);
			pst.setString(2, strDocumentContent);
			pst.setDate(3, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(5, uF.parseToInt((String)session.getAttribute(EMPID)));
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
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	private void sendToLearningGap(Connection con, String strEmpId, String lPlanIds) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			Map<String, String> hmDesignation = CF.getEmpDesigMapId(con);
			String wLocationId = CF.getEmpWlocationId(con, uF, strEmpId);
			
			Map<String, String> hmAttributeThreshhold = new HashMap<String, String>();
			pst = con.prepareStatement("select attribute_id,threshhold from appraisal_attribute_level");
			rs = pst.executeQuery(); 
			while (rs.next()) {
				hmAttributeThreshhold.put(rs.getString("attribute_id"),rs.getString("threshhold"));
			}
			rs.close();
			pst.close();
			
			double dblTotalMarks = 0;
			double dblTotalWeightage = 0;
			double dblTotalAggregate = 0;
			Map<String, String> hmScoreAggregateMap = new HashMap<String, String>();
	
			pst = con.prepareStatement("select sum(marks) as marks, sum(weightage) as weightage, aqw.appraisal_attribute from appraisal_question_answer aqw where aqw.appraisal_id=? and emp_id=? and appraisal_freq_id = ? group by aqw.appraisal_attribute");
			pst.setInt(1, uF.parseToInt(id));
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				dblTotalMarks = uF.parseToDouble(rs.getString("marks"));
				dblTotalWeightage = uF.parseToDouble(rs.getString("weightage"));
				dblTotalAggregate = uF.parseToDouble(uF.formatIntoTwoDecimal(((dblTotalMarks / dblTotalWeightage) * 100)));
				hmScoreAggregateMap.put(rs.getString("appraisal_attribute"), uF.showData("" + dblTotalAggregate, "0"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbHrIds = new StringBuilder();
			StringBuilder sbManagerIds = new StringBuilder();
			StringBuilder sbCeoIds = new StringBuilder();
			StringBuilder sbHodIds = new StringBuilder();
			pst = con.prepareStatement("select supervisor_id,hr_ids from appraisal_details where appraisal_details_id = ?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery(); 
			while (rs.next()) {
				sbManagerIds.append(rs.getString("supervisor_id"));
				sbHrIds.append(rs.getString("hr_ids"));
				sbCeoIds.append(rs.getString("ceo_ids"));
				sbHodIds.append(rs.getString("hod_ids"));
			}
			rs.close();
			pst.close();
			
			Map<String, List<String>> hmLearningAttributes = new HashMap<String, List<String>>();
			if(lPlanIds != null && !lPlanIds.equals("")) {
				pst = con.prepareStatement("update learning_plan_details set learner_ids = learner_ids||'"+strEmpId+"'||',' where learning_plan_id in ("+lPlanIds+")");
				pst.execute();
//				System.out.println("pst ===>> " + pst);
				pst.close();
	
				pst = con.prepareStatement("select * from learning_plan_details where learning_plan_id in ("+lPlanIds+")");
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList = Arrays.asList(rs.getString("attribute_id").split(","));
					hmLearningAttributes.put(rs.getString("learning_plan_id"), innerList);
				}
				rs.close();
				pst.close();
			}
			
			if (!hmScoreAggregateMap.isEmpty()) {
				Iterator<String> it = hmScoreAggregateMap.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					double aggregate = uF.parseToDouble(hmScoreAggregateMap.get(key));
	
//					if (aggregate < uF.parseToDouble(hmAttributeThreshhold.get(key))) {
					if (!hmLearningAttributes.isEmpty()) {
						Iterator<String> itL = hmLearningAttributes.keySet().iterator();
						int learningCnt=0;
						while (itL.hasNext()) {
							String lPlanId = itL.next();
							List<String> attribList = hmLearningAttributes.get(lPlanId);
							if(attribList.contains(key)) {
								pst = con.prepareStatement("insert into training_gap_details(emp_id,designation_id,wlocation_id,attribute_id,appraisal_id,"
									+ "actual_score,required_score,training_completed_status,is_training_schedule,added_by,entry_date, appraisal_freq_id," +
									"assign_learning_plan_id)" +
									" values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
		//						pst=con.prepareStatement(insertTrainingGap);
								pst.setInt(1,uF.parseToInt(strEmpId));
								pst.setInt(2,uF.parseToInt(hmDesignation.get(strEmpId)));
		//						pst.setInt(3,uF.parseToInt(hmWlocationMap.get(empid)));
								pst.setInt(3,uF.parseToInt(wLocationId));
								pst.setInt(4, uF.parseToInt(key));
								pst.setInt(5, uF.parseToInt(id));
								pst.setDouble(6, aggregate);
								pst.setDouble(7, uF.parseToDouble(hmAttributeThreshhold.get(key)));
								pst.setBoolean(8, false);
								pst.setBoolean(9, true);
								pst.setInt(10, uF.parseToInt(strSessionEmpId));
								pst.setDate(11, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(12, uF.parseToInt(getAppFreqId()));
								pst.setInt(13, uF.parseToInt(lPlanId));
								pst.execute();
								pst.close();
//								System.out.println("pst with lplan ===>> " + pst);
								learningCnt++;
							} else if(learningCnt == 0) {
								pst = con.prepareStatement("insert into training_gap_details(emp_id,designation_id,wlocation_id,attribute_id,appraisal_id,"
									+ "actual_score,required_score,training_completed_status,is_training_schedule,added_by,entry_date, appraisal_freq_id)" +
									" values(?,?,?,?, ?,?,?,?, ?,?,?,?)");
		//						pst=con.prepareStatement(insertTrainingGap);
								pst.setInt(1,uF.parseToInt(strEmpId));
								pst.setInt(2,uF.parseToInt(hmDesignation.get(strEmpId)));
		//						pst.setInt(3,uF.parseToInt(hmWlocationMap.get(empid)));
								pst.setInt(3,uF.parseToInt(wLocationId));
								pst.setInt(4, uF.parseToInt(key));
								pst.setInt(5, uF.parseToInt(id));
								pst.setDouble(6, aggregate);
								pst.setDouble(7, uF.parseToDouble(hmAttributeThreshhold.get(key)));
								pst.setBoolean(8, false);
								pst.setBoolean(9, false);
								pst.setInt(10, uF.parseToInt(strSessionEmpId));
								pst.setDate(11, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(12, uF.parseToInt(getAppFreqId()));
								pst.execute();
								pst.close();
//								System.out.println("pst without lplan ===>> " + pst);
							}
						}
					} else {
						pst = con.prepareStatement("insert into training_gap_details(emp_id,designation_id,wlocation_id,attribute_id,appraisal_id,"
							+ "actual_score,required_score,training_completed_status,is_training_schedule,added_by,entry_date, appraisal_freq_id)" +
							" values(?,?,?,?, ?,?,?,?, ?,?,?,?)");
//						pst=con.prepareStatement(insertTrainingGap);
						pst.setInt(1,uF.parseToInt(strEmpId));
						pst.setInt(2,uF.parseToInt(hmDesignation.get(strEmpId)));
//						pst.setInt(3,uF.parseToInt(hmWlocationMap.get(empid)));
						pst.setInt(3,uF.parseToInt(wLocationId));
						pst.setInt(4, uF.parseToInt(key));
						pst.setInt(5, uF.parseToInt(id));
						pst.setDouble(6, aggregate);
						pst.setDouble(7, uF.parseToDouble(hmAttributeThreshhold.get(key)));
						pst.setBoolean(8, false);
						pst.setBoolean(9, false);
						pst.setInt(10, uF.parseToInt(strSessionEmpId));
						pst.setDate(11, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(12, uF.parseToInt(getAppFreqId()));
						pst.execute();
						pst.close();
//						System.out.println("pst else ===>> " + pst);
					}
//					}
					
//					if (aggregate < uF.parseToDouble(hmAttributeThreshhold.get(key))) {
//						
//						pst = con.prepareStatement("insert into training_gap_details(emp_id,designation_id,wlocation_id,attribute_id,appraisal_id,"
//							+ "actual_score,required_score,training_completed_status,is_training_schedule,added_by,entry_date,appraisal_freq_id)" +
//							" values(?,?,?,?, ?,?,?,?, ?,?,?,?)");
//						pst.setInt(1,uF.parseToInt(strEmpId));
//						pst.setInt(2,uF.parseToInt(hmDesignation.get(strEmpId)));
//						pst.setInt(3,uF.parseToInt(wLocationId));
//						pst.setInt(4, uF.parseToInt(key));
//						pst.setInt(5, uF.parseToInt(id));
//						pst.setDouble(6, aggregate);
//						pst.setDouble(7, uF.parseToDouble(hmAttributeThreshhold.get(key)));
//						pst.setBoolean(8, false);
//						pst.setBoolean(9, false);
//						pst.setInt(10, uF.parseToInt(strSessionEmpId));
//						pst.setDate(11, uF.getCurrentDate(CF.getStrTimeZone()));
//						pst.setInt(12, uF.parseToInt(getAppFreqId()));
//						pst.execute();
//						pst.close();
//					}
					
					List<String> hrIdList = Arrays.asList(sbHrIds.toString().split(","));
					Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
					
					String strDomain = request.getServerName().split("\\.")[0];
					for(int i=0; hrIdList!= null && !hrIdList.isEmpty() && i<hrIdList.size(); i++) {
						if(!hrIdList.get(i).equals("") && uF.parseToInt(hrIdList.get(i)) > 0) {
							String alertData = "<div style=\"float: left;\"> Learning Gap has emerged for ("+CF.getEmpNameMapByEmpId(con, strEmpId)+") by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
							String alertAction = "Learnings.action?callFrom=LA&pType=WR";
							
							UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(hrIdList.get(i));
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmUserTypeId.get(HRMANAGER));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
	
	//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
	//						userAlerts.setStrDomain(strDomain);
	//						userAlerts.setStrEmpId(hrIdList.get(i));
	//						userAlerts.set_type(HR_LEARNING_GAPS_ALERT);
	//						userAlerts.setStatus(INSERT_ALERT);
	//						Thread t = new Thread(userAlerts);
	//						t.run();
						}
					}
					
					List<String> managerIdList = Arrays.asList(sbManagerIds.toString().split(","));
					for(int i=0; managerIdList!= null && !managerIdList.isEmpty() && i<managerIdList.size(); i++) {
						if(!managerIdList.get(i).equals("") && uF.parseToInt(managerIdList.get(i)) > 0) {
							String alertData = "<div style=\"float: left;\"> Learning Gap has emerged for ("+CF.getEmpNameMapByEmpId(con, strEmpId)+") by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
							String alertAction = "Learnings.action?callFrom=LA&pType=WR";
							
							UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(managerIdList.get(i));
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmUserTypeId.get(MANAGER));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
	//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
	//						userAlerts.setStrDomain(strDomain);
	//						userAlerts.setStrEmpId(managerIdList.get(i));
	//						userAlerts.set_type(HR_LEARNING_GAPS_ALERT);
	//						userAlerts.setStatus(INSERT_ALERT);
	//						Thread t = new Thread(userAlerts);
	//						t.run();
						}
					}
					
					List<String> ceoIdList = Arrays.asList(sbCeoIds.toString().split(","));
					for(int i=0; ceoIdList!= null && !ceoIdList.isEmpty() && i<ceoIdList.size(); i++) {
						if(!ceoIdList.get(i).equals("") && uF.parseToInt(ceoIdList.get(i)) > 0) {
							String alertData = "<div style=\"float: left;\"> Learning Gap has emerged for ("+CF.getEmpNameMapByEmpId(con, strEmpId)+") by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
							String alertAction = "Learnings.action?callFrom=LA&pType=WR";
							
							UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(ceoIdList.get(i));
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmUserTypeId.get(CEO));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
	//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
	//						userAlerts.setStrDomain(strDomain);
	//						userAlerts.setStrEmpId(ceoIdList.get(i));
	//						userAlerts.set_type(HR_LEARNING_GAPS_ALERT);
	//						userAlerts.setStatus(INSERT_ALERT);
	//						Thread t = new Thread(userAlerts);
	//						t.run();
						}
					}
					
					List<String> hodIdList = Arrays.asList(sbHodIds.toString().split(","));
					for(int i=0; hodIdList!= null && !hodIdList.isEmpty() && i<hodIdList.size(); i++) {
						if(!hodIdList.get(i).equals("") && uF.parseToInt(hodIdList.get(i)) > 0) {
							String alertData = "<div style=\"float: left;\"> Learning Gap has emerged for ("+CF.getEmpNameMapByEmpId(con, strEmpId)+") by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
							String alertAction = "Learnings.action?callFrom=LA&pType=WR";
							
							UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(hodIdList.get(i));
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmUserTypeId.get(HOD));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
	//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
	//						userAlerts.setStrDomain(strDomain);
	//						userAlerts.setStrEmpId(hodIdList.get(i));
	//						userAlerts.set_type(HR_LEARNING_GAPS_ALERT);
	//						userAlerts.setStatus(INSERT_ALERT);
	//						Thread t = new Thread(userAlerts);
	//						t.run();
						}
					}
					
					sendMail(con, hrIdList, id, key, strEmpId);
					sendMail(con, managerIdList, id, key, strEmpId);
					sendMail(con, ceoIdList, id, key, strEmpId);
					sendMail(con, hodIdList, id, key, strEmpId);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	public void sendMail(Connection con, List<String> hrIdList, String appraisalId, String attribId , String empId) {

		ResultSet rs = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
//			System.out.println("Req id is ========= "+getStrId());
			StringBuilder reviewName = new StringBuilder();
//			pst = con.prepareStatement("select appraisal_name from appraisal_details where appraisal_details_id = ?");
			pst = con.prepareStatement("select appraisal_name, appraisal_freq_name from appraisal_details ad, appraisal_details_frequency adf "
					+" where ad.appraisal_details_id = adf.appraisal_id and appraisal_details_id = ? and appraisal_freq_id= ?");
			pst.setInt(1, uF.parseToInt(appraisalId));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				reviewName.append(rs.getString("appraisal_name"));
				
				if(rs.getString("appraisal_name")!= null && !rs.getString("appraisal_name").equals("")) {
					reviewName.append(" ("+rs.getString("appraisal_name")+")");
				}
			}
			rs.close();
			pst.close();
			
			if(reviewName != null && !reviewName.equals("")) {
				Map<String, String> hmEmpInner1 = hmEmpInfo.get(strSessionEmpId);
				StringBuilder sbRevieweeName = new StringBuilder();
				sbRevieweeName.append(hmEmpInner1.get("FNAME")+" " +hmEmpInner1.get("LNAME"));
				
				String attributeName = CF.getAttributeNameByAttributeId(con, attribId);
				
				for(int i=0; hrIdList!= null && i<hrIdList.size(); i++) {
					
					Map<String, String> hmEmpInner = hmEmpInfo.get(hrIdList.get(i));
					if(hrIdList.get(i) != null && !hrIdList.get(i).equals("")){
						String strDomain = request.getServerName().split("\\.")[0];	
						Notifications nF = new Notifications(N_LEARNING_GAP_FOR_HR, CF); 
						nF.setDomain(strDomain);
						nF.request = request;
						nF.setStrEmpId(hrIdList.get(i));
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrReviewName(reviewName.toString());
						nF.setStrRevieweeName(sbRevieweeName.toString());
						nF.setStrAttributeName(attributeName);
						nF.setStrEmpFname(hmEmpInner.get("FNAME"));
						nF.setStrEmpLname(hmEmpInner.get("LNAME"));
						nF.setEmailTemplate(true);
						nF.sendNotifications();
					}
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	public void sendMail(Connection con, String appraisalId, String activityIds, Boolean sendToGap , String empId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		UtilityFunctions uF = new UtilityFunctions();
	
		try {
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
			StringBuilder appraisalName = new StringBuilder();
			pst = con.prepareStatement("select appraisal_name, appraisal_freq_name from appraisal_details ad, appraisal_details_frequency adf "
					+" where ad.appraisal_details_id = adf.appraisal_id and appraisal_details_id = ? and appraisal_freq_id= ?");
			pst.setInt(1, uF.parseToInt(appraisalId));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			rst = pst.executeQuery();
			while (rst.next()) {
				appraisalName.append(rst.getString("appraisal_name"));
				
				if(rst.getString("appraisal_freq_name") != null && !rst.getString("appraisal_freq_name").equals("")) {
					appraisalName.append(" ("+rst.getString("appraisal_freq_name")+")");
				}
			}
			rst.close();
			pst.close();
			
			if(appraisalName != null && !appraisalName.equals("")) {
				Map<String, String> hmEmpInner1 = hmEmpInfo.get(empId);
				StringBuilder sbRevieweeName = new StringBuilder();
				sbRevieweeName.append(hmEmpInner1.get("FNAME")+" " +hmEmpInner1.get("LNAME"));
				
				Map<String, String> hmEmpInner = hmEmpInfo.get(strSessionEmpId);
				String strDomain = request.getServerName().split("\\.")[0];
				/*Notifications nF = new Notifications(N_REVIEW_FINALIZATION_FOR_HR, CF); 
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrEmpId(strSessionEmpId);
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrRevieweeName(sbRevieweeName.toString());
				nF.setStrReviewName(appraisalName);
				nF.setStrEmpFname(hmEmpInner.get("FNAME"));
				nF.setStrEmpLname(hmEmpInner.get("LNAME"));
				nF.setEmailTemplate(true);
				nF.sendNotifications();*/
				 
				StringBuilder sbFinalizerName = new StringBuilder();
				sbFinalizerName.append(hmEmpInner.get("FNAME")+" " +hmEmpInner.get("LNAME"));
				
				Notifications nF1 = new Notifications(N_REVIEW_FINALIZATION_FOR_EMP, CF); 
				nF1.setDomain(strDomain);
				nF1.request = request;
				nF1.setStrEmpId(empId);
				nF1.setStrHostAddress(CF.getStrEmailLocalHost());
				nF1.setStrHostPort(CF.getStrHostPort()); 
				nF1.setStrContextPath(request.getContextPath());
				nF1.setStrFinalizerName(sbFinalizerName.toString());
				nF1.setStrReviewName(appraisalName.toString());
				nF1.setStrEmpFname(hmEmpInner1.get("FNAME"));
				nF1.setStrEmpLname(hmEmpInner1.get("LNAME"));
				nF1.setEmailTemplate(true);
				nF1.sendNotifications();	 
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst != null){
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	public String loadValidateEmpActivity(UtilityFunctions uF) {
		
		if(getDataType() == null || getDataType().equals("A")) {
			activityList = new FillActivity(request).fillActivityByNode(true, false);
		} else if(getDataType() == null || getDataType().equals("D")) {
			activityList = new FillActivity(request).fillActivityByNode(true, true);
		}
		
		organisationList1 = new FillOrganisation(request).fillOrganisation();
		wLocationList1 = new FillWLocation(request).fillWLocation((String)session.getAttribute(ORGID));
		serviceList1 = new FillServices(request).fillServices((String)session.getAttribute(ORGID), uF);
		departmentList1 = new FillDepartment(request).fillDepartment(uF.parseToInt((String)session.getAttribute(ORGID)));
		levelList1 = new FillLevel(request).fillLevel(uF.parseToInt((String)session.getAttribute(ORGID)));
		desigList = new ArrayList<FillDesig>();
		gradeChangeList =  new ArrayList<FillGrade>();
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		return LOAD;
	}
	

	private void getAppraisalData() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
	
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
	//		System.out.println("pst ========> "+pst);
			String oriented_type="";
			List<String> memberList = new ArrayList<String>();
			while (rs.next()) {
				oriented_type = rs.getString("oriented_type");
				memberList = Arrays.asList(rs.getString("usertype_member").split(","));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select distinct(attribute_id) as attribute_id from appraisal_main_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
	//		System.out.println("pst ========> "+pst);
			List<String> attributeIds = new ArrayList<String>();
			while (rs.next()) {
				attributeIds.add(rs.getString("attribute_id"));
			}
			rs.close();
			pst.close();
	
			StringBuilder sbOptions = new StringBuilder();
			if(attributeIds != null && attributeIds.size()>0) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select learning_plan_id,learning_plan_name from learning_plan_details where learning_plan_id > 0 ");
	            sbQuery.append(" and (");
	            for(int i=0; i<attributeIds.size(); i++) {
	                sbQuery.append(" attribute_id like '%,"+attributeIds.get(i)+",%'");
	                if(i<attributeIds.size()-1) {
	                    sbQuery.append(" OR "); 
	                }
	            }
	            sbQuery.append(" ) ");
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
	//			System.out.println("pst ========> " + pst);
				while (rs.next()) {
					sbOptions.append("<option value=\""+rs.getString("learning_plan_id")+"\">"+rs.getString("learning_plan_name")+"</option>");
				}
				rs.close();
				pst.close();
			}
	//		List<String> memberList = CF.getOrientationMemberDetails(con,uF.parseToInt(oriented_type));
			Map<String, String> orientationMemberMp = getOrientationMember();
	//		System.out.println("memberList ==>" + memberList);
			// request.setAttribute("hmUserTypeID", hmUserTypeID);
			request.setAttribute("memberList", memberList);
			request.setAttribute("orientationMemberMp", orientationMemberMp);
			request.setAttribute("sbOptions", sbOptions.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	}

	
	private Map<String, String> getOrientationMember() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> orientationMemberMp = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				//orientationMemberMp.put(rs.getString("orientation_member_id"), rs.getString("member_name"));
				orientationMemberMp.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();

			request.setAttribute("orientationMemberMp", orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
		return orientationMemberMp;
	}
	

	private void getEmployeeList(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			List<String> alGapEmp = null;
			if(getStrGapEmp()!=null && !getStrGapEmp().trim().equals("")){
				alGapEmp = Arrays.asList(getStrGapEmp().trim().split(","));
			} 
			if(alGapEmp == null || alGapEmp.isEmpty() || alGapEmp.size() == 0){
				alGapEmp = new ArrayList<String>();
			}
			
			StringBuilder sbEmp = null;
			if(getStrEmpIds()!=null && !getStrEmpIds().trim().equals("") && !getStrEmpIds().trim().equalsIgnoreCase("NULL")) {
				sbEmp = new StringBuilder();
				sbEmp.append(getStrEmpIds());
			} else if(strIsAssigActivity!=null) {
				for(int i = 0; i<strIsAssigActivity.size(); i++) {
					if(sbEmp == null) {
						sbEmp = new StringBuilder();
						sbEmp.append(","+strIsAssigActivity.get(i)+",");
					} else {
						sbEmp.append(strIsAssigActivity.get(i)+",");
					}
				}
			}

				
			if(sbEmp !=null) {
				String strDataType = null;
				if(getDataType() != null) {
					strDataType= getDataType();
				} else {
					strDataType= "A";
				}
				String strEmptemp = sbEmp.substring(1, sbEmp.length()-1);
				
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
						"and eod.emp_id in ("+strEmptemp+") ");
				if(uF.parseToInt(getRemEmpId()) > 0) {
					sbQuery.append("and eod.emp_id !="+uF.parseToInt(getRemEmpId()));
				}
				sbQuery.append(" order by epd.emp_fname, epd.emp_lname");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst=======>"+pst);
				rs = pst.executeQuery();
				sbEmp = null;
				while(rs.next()) {
					if(sbEmp == null) {
						sbEmp = new StringBuilder();
						sbEmp.append(","+rs.getString("emp_id")+",");
					} else {
						sbEmp.append(rs.getString("emp_id")+",");
					}
					
				}
				rs.close();
				pst.close();
				
				if(sbEmp != null) {
					String strEmptp = sbEmp.substring(1, sbEmp.length()-1);
					
					sbQuery = new StringBuilder();
					sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
							"and eod.emp_id in ("+strEmptp+") ");	
					sbQuery.append(" order by epd.emp_fname, epd.emp_lname");
					pst = con.prepareStatement(sbQuery.toString());
//					System.out.println("pst=======>"+pst);
					rs = pst.executeQuery();
					StringBuilder sbEmpList = null;
					boolean thumbsFlag = false;
					while(rs.next()){
						
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						String strEmpName = uF.showData(rs.getString("emp_fname"), "")+strEmpMName+" "+uF.showData(rs.getString("emp_lname"), "") +" ["+uF.showData(rs.getString("empcode"), "")+"]";
						
						
						String removePath = "<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to remove "+strEmpName+"?')) window.location='AppraisalBulkFinalization.action?dataType="+strDataType+"&strEmpIds="+URLEncoder.encode(sbEmp.toString())+"&remEmpId="+rs.getString("emp_id")+"&id="+getId()+"&strGapEmp=" +URLEncoder.encode(getStrGapEmp())+"';\">" +
								"<img title=\"Remove\" src=\"images1/icons/hd_cross_16x16.png\" border=\"0\" /></a> ";
						if(alGapEmp.contains(rs.getString("emp_id"))){
							thumbsFlag = true;
							strEmpName = "<font color=\"red\">"+strEmpName+"</font>";
						}
						if(sbEmpList == null){
							sbEmpList = new StringBuilder();
							sbEmpList.append(strEmpName+" "+removePath);
						} else {
							sbEmpList.append(" , "+strEmpName+" "+removePath);
						}
						
						
					}
					rs.close();
					pst.close();
					
					request.setAttribute("thumbsFlag",""+thumbsFlag);
					request.setAttribute("sbEmpList", sbEmpList.toString());
					request.setAttribute("sbEmp", sbEmp.toString());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public List<String> getStrIsAssigActivity() {
		return strIsAssigActivity;
	}

	public void setStrIsAssigActivity(List<String> strIsAssigActivity) {
		this.strIsAssigActivity = strIsAssigActivity;
	}

	public String getStrEmpIds() {
		return strEmpIds;
	}

	public void setStrEmpIds(String strEmpIds) {
		this.strEmpIds = strEmpIds;
	}

	public String getStrActivity() {
		return strActivity;
	}

	public void setStrActivity(String strActivity) {
		this.strActivity = strActivity;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getStrExtendProbationDays() {
		return strExtendProbationDays;
	}

	public void setStrExtendProbationDays(String strExtendProbationDays) {
		this.strExtendProbationDays = strExtendProbationDays;
	}

	public String getStrTransferType() {
		return strTransferType;
	}

	public void setStrTransferType(String strTransferType) {
		this.strTransferType = strTransferType;
	}

	public String getStrIncrementType() {
		return strIncrementType;
	}

	public void setStrIncrementType(String strIncrementType) {
		this.strIncrementType = strIncrementType;
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

	public String getEmpGrade() {
		return empGrade;
	}

	public void setEmpGrade(String empGrade) {
		this.empGrade = empGrade;
	}

	public String getEmpChangeGrade() {
		return empChangeGrade;
	}

	public void setEmpChangeGrade(String empChangeGrade) {
		this.empChangeGrade = empChangeGrade;
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

	public String getStrIncrementPercentage() {
		return strIncrementPercentage;
	}

	public void setStrIncrementPercentage(String strIncrementPercentage) {
		this.strIncrementPercentage = strIncrementPercentage;
	}

	public String getStrReason() {
		return strReason;
	}

	public void setStrReason(String strReason) {
		this.strReason = strReason;
	}

	public String getStrUpdate() {
		return strUpdate;
	}

	public void setStrUpdate(String strUpdate) {
		this.strUpdate = strUpdate;
	}

	public String getStrUpdateDocument() {
		return strUpdateDocument;
	}

	public void setStrUpdateDocument(String strUpdateDocument) {
		this.strUpdateDocument = strUpdateDocument;
	}

	public String getStrNewStatus() {
		return strNewStatus;
	}

	public void setStrNewStatus(String strNewStatus) {
		this.strNewStatus = strNewStatus;
	}

	public List<FillActivity> getActivityList() {
		return activityList;
	}

	public void setActivityList(List<FillActivity> activityList) {
		this.activityList = activityList;
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

	public List<FillServices> getServiceList1() {
		return serviceList1;
	}

	public void setServiceList1(List<FillServices> serviceList1) {
		this.serviceList1 = serviceList1;
	}

	public List<FillDepartment> getDepartmentList1() {
		return departmentList1;
	}

	public void setDepartmentList1(List<FillDepartment> departmentList1) {
		this.departmentList1 = departmentList1;
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

	public List<FillGrade> getGradeChangeList() {
		return gradeChangeList;
	}

	public void setGradeChangeList(List<FillGrade> gradeChangeList) {
		this.gradeChangeList = gradeChangeList;
	}

	public String getEmpIds() {
		return empIds;
	}

	public void setEmpIds(String empIds) {
		this.empIds = empIds;
	}

	public String getRemEmpId() {
		return remEmpId;
	}

	public void setRemEmpId(String remEmpId) {
		this.remEmpId = remEmpId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSendtoGapStatus() {
		return sendtoGapStatus;
	}

	public void setSendtoGapStatus(String sendtoGapStatus) {
		this.sendtoGapStatus = sendtoGapStatus;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getStrGapEmp() {
		return strGapEmp;
	}

	public void setStrGapEmp(String strGapEmp) {
		this.strGapEmp = strGapEmp;
	}

	public String getStrMessage() {
		return strMessage;
	}

	public void setStrMessage(String strMessage) {
		this.strMessage = strMessage;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getAreasOfStrength() {
		return areasOfStrength;
	}

	public void setAreasOfStrength(String areasOfStrength) {
		this.areasOfStrength = areasOfStrength;
	}

	public String getAreasOfDevelopment() {
		return areasOfDevelopment;
	}

	public void setAreasOfDevelopment(String areasOfDevelopment) {
		this.areasOfDevelopment = areasOfDevelopment;
	}

	public String[] getLearningIds() {
		return learningIds;
	}

	public void setLearningIds(String[] learningIds) {
		this.learningIds = learningIds;
	}
	
}
