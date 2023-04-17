package com.konnect.jpms.task;

import java.sql.Connection;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectDetail extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpServletRequest request;
	String strSessionEmpId;
	
	HttpSession session;
	CommonFunctions CF;
	
	String strUserType;
	String pro_id;

	String proType;
	String pageType;
	double actual_days;
	double ideal_days;
	
	public String execute() {
	
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);

		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		getSingleProjectDetails(uF);
		getReassignRescheduleTaskRequest(uF);
//		getTaskDetails(uF,CF);
		
		return LOAD;
	}
	

	public void getSingleProjectDetails(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			ProjectGanttChart pgc = new ProjectGanttChart();
			pgc.CF = CF;
			pgc.request = request;
			pgc.session = session;
			pgc.getProjectDetails(getPro_id());
		
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
			Map<String, String> hmProFreqStartEndDate = getProjectCurrentFreqStartEndDate(con, uF, getPro_id());
			pst = con.prepareStatement("select * from projectmntnc where pro_id=?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
//			int projectOwnerId = 0;
			StringBuilder projectOwnerId = null;
			int nSbuId = 0;
			int nOrgId = 0;
			int nClientId = 0;
			int nPocId = 0;
			while(rs.next()) {
				Map<String, String> hmCurr = (Map)hmCurrency.get(rs.getString("curr_id"));
				Map<String, String> hmCurrBase = (Map)hmCurrency.get("3"); // 3 = INR
				
				request.setAttribute("PROJECT_NAME", rs.getString("pro_name"));
				request.setAttribute("PROJECT_CODE", rs.getString("project_code"));
				request.setAttribute("PROJECT_DESC", uF.showData(rs.getString("description"), ""));
				request.setAttribute("PROJECT_START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, CF.getStrReportDateFormat()));
				request.setAttribute("PROJECT_END_DATE", uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
				
				request.setAttribute("PROJECT_START_DATE_C", uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));
				request.setAttribute("PROJECT_END_DATE_C", uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));

				request.setAttribute("PROJECT_IDEALTIME", rs.getString("idealtime"));
				request.setAttribute("PROJECT_CALC_TYPE", rs.getString("actual_calculation_type"));
				request.setAttribute("SHORT_CURR", hmCurr != null ? hmCurr.get("SHORT_CURR") : "");
//				request.setAttribute("CURR_CONVERSION", currConversion+"");
				
		//===start parvez date: 14-10-2022===		
//				projectOwnerId = rs.getInt("project_owner");
				String[] proOwnerIds = null;
				if(rs.getString("project_owners")!=null){
					proOwnerIds = rs.getString("project_owners").split(",");
				}
				for(int k=1; proOwnerIds!=null && k<proOwnerIds.length; k++){
					if(projectOwnerId==null){
						projectOwnerId = new StringBuilder();
						projectOwnerId.append(proOwnerIds[k]);
					}else{
						projectOwnerId.append(","+proOwnerIds[k]);
					}
				}
		//===end parvez date: 14-10-2022===		
				
				nSbuId = rs.getInt("sbu_id");
				nOrgId = rs.getInt("org_id");
				nClientId = rs.getInt("client_id");
				nPocId = rs.getInt("poc");
				
				String strCondition = "";
				if(rs.getString("approve_status")!=null && rs.getString("approve_status").trim().equalsIgnoreCase("n")){
					request.setAttribute("PRO_STATUS", "Working");
					if(rs.getString("deadline")!=null && rs.getDate("deadline").after(uF.getCurrentDate(CF.getStrTimeZone()))){
						strCondition = "On Target";
					} else {
						strCondition = "Overdue";
					}
				} else if(rs.getString("approve_status")!=null && rs.getString("approve_status").trim().equalsIgnoreCase("approved")){
					request.setAttribute("PRO_STATUS", "Completed");
					if(rs.getString("deadline")!=null && rs.getDate("deadline").after(rs.getDate("approve_date"))){
						strCondition = "On Target";
					} else {
						strCondition = "Overdue";
					}
				} else if(rs.getString("approve_status")!=null && rs.getString("approve_status").trim().equalsIgnoreCase("blocked")){
					request.setAttribute("PRO_STATUS", "Blocked");
					if(rs.getString("deadline")!=null && rs.getDate("deadline").after(rs.getDate("approve_date"))){
						strCondition = "On Target";
					} else {
						strCondition = "Overdue";
					}
				}
				request.setAttribute("strCondition", strCondition);
				
				Map<String, String> hmProjectData = new HashMap<String, String>();
				hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
				hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
					
				Map<String, String> hmProAWDaysAndHrs = CF.getProjectActualAndBillableEfforts(con, rs.getString("pro_id"), hmProjectData);
				String strIdealTime = getProjectIdealTimeTime(con, uF, rs.getString("pro_id"), hmProjectData);
				request.setAttribute("IDEAL_TIME", uF.formatIntoOneDecimal(uF.parseToDouble(strIdealTime)));
//				System.out.println("hmProAWDaysAndHrs ===>> " + hmProAWDaysAndHrs);
				double proDealineCompletePercent = 0;
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
//					request.setAttribute("IDEAL_TIME", uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime"))));
					String actualTime = hmProAWDaysAndHrs.get("ACTUAL_DAYS");
					proDealineCompletePercent = uF.parseToDouble(actualTime);
					request.setAttribute("ACTUAL_TIME", actualTime);
					request.setAttribute("CAL_TYPE", "days");
				} else if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
//					request.setAttribute("IDEAL_TIME", uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime"))));
					String actualTime = hmProAWDaysAndHrs.get("ACTUAL_DAYS");
					double actualMonths = uF.parseToDouble(actualTime) / 30;
					proDealineCompletePercent = actualMonths;
					request.setAttribute("ACTUAL_TIME", uF.formatIntoTwoDecimalWithOutComma(actualMonths));
					request.setAttribute("CAL_TYPE", "months");
				} else {
//					request.setAttribute("IDEAL_TIME", uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime"))));
					String actualTime = hmProAWDaysAndHrs.get("ACTUAL_HRS");
					proDealineCompletePercent = uF.parseToDouble(uF.getTotalTimeMinutes100To60(actualTime));
					request.setAttribute("ACTUAL_TIME", uF.getTotalTimeMinutes100To60(actualTime));
					request.setAttribute("CAL_TYPE", "hrs");
				}
				
				String strProDeadlineColor = "";
				String strProDeadlineIndicator = "";
				if(proDealineCompletePercent < uF.parseToDouble(strIdealTime)) {
					strProDeadlineColor = "green";
					/*strProDeadlineIndicator = "<img src=\"images1/icons/approved.png\" width=\"17px\">"; */
					strProDeadlineIndicator = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>"; 
					
				} else if(proDealineCompletePercent == uF.parseToDouble(strIdealTime)) {
					strProDeadlineColor = "yellow";
					/*strProDeadlineIndicator = "<img src=\"images1/icons/re_submit.png\" width=\"17px\">";*/
					strProDeadlineIndicator = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>";
					
				} else if(proDealineCompletePercent > uF.parseToDouble(strIdealTime)) {
					strProDeadlineColor = "red";
					/*strProDeadlineIndicator = "<img src=\"images1/icons/denied.png\" width=\"17px\">";*/
					strProDeadlineIndicator = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>";
					
				} else {
					strProDeadlineColor = "";
					strProDeadlineIndicator = "";
				}
				request.setAttribute("strProDeadlineColor", strProDeadlineColor);
				request.setAttribute("strProDeadlineIndicator", strProDeadlineIndicator);
				
				String strBillingType = CF.getBillinType(rs.getString("billing_type"));;
				String strBillingKind = CF.getBillinFreq(rs.getString("billing_kind"), rs.getString("billing_type"));
				request.setAttribute("strBillingType", strBillingType+" ("+strBillingKind+")");
				
				
				double deadLinePercent = 0;
				String days = null;
				String currdays = null;
				String strProFreqStartDate = null;
				String strProFreqEndDate = null;
				if(hmProFreqStartEndDate != null && !hmProFreqStartEndDate.isEmpty()) {
					days = uF.dateDifference(hmProFreqStartEndDate.get("FREQ_START_DATE"), DBDATE, hmProFreqStartEndDate.get("FREQ_END_DATE"), DBDATE);
					if(rs.getString("approve_status") != null && !rs.getString("approve_status").equals("n")) {
						currdays = uF.dateDifference(rs.getString("start_date"), DBDATE, rs.getString("approve_date"), DBDATE);
					} else {
						currdays = uF.dateDifference(hmProFreqStartEndDate.get("FREQ_START_DATE"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
					}
					strProFreqStartDate = uF.getDateFormat(hmProFreqStartEndDate.get("FREQ_START_DATE"), DBDATE, CF.getStrReportDateFormat());
					strProFreqEndDate = uF.getDateFormat(hmProFreqStartEndDate.get("FREQ_END_DATE"), DBDATE, CF.getStrReportDateFormat());
				} else {
					days = uF.dateDifference(rs.getString("start_date"), DBDATE, rs.getString("deadline"), DBDATE);
					if(rs.getString("approve_status") != null && !rs.getString("approve_status").equals("n")) {
						currdays = uF.dateDifference(rs.getString("start_date"), DBDATE, rs.getString("approve_date"), DBDATE);
					} else {
						currdays = uF.dateDifference(rs.getString("start_date"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
					}
					
					strProFreqStartDate = uF.getDateFormat(rs.getString("start_date"), DBDATE, CF.getStrReportDateFormat());
					strProFreqEndDate = uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat());
				}
				if(uF.parseToDouble(days) > 0) {
					deadLinePercent = (uF.parseToDouble(currdays) / uF.parseToDouble(days)) * 100;
				}
				String proDeadlinePercentColor = "";
				if(deadLinePercent <= 75) {
					/*proDeadlinePercentColor = "<img src=\"images1/icons/approved.png\" width=\"17px\">";*/
					proDeadlinePercentColor = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>";
					
				} else if(deadLinePercent > 75 && deadLinePercent < 100) {
					/*proDeadlinePercentColor = "<img src=\"images1/icons/re_submit.png\" width=\"17px\">";*/
					proDeadlinePercentColor = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>";
					
				} else if(deadLinePercent >= 100) {
					/*proDeadlinePercentColor = "<img src=\"images1/icons/denied.png\" width=\"17px\">";*/
					proDeadlinePercentColor = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>";
					
				} else {
					proDeadlinePercentColor = "";
				}
				request.setAttribute("proDeadlinePercentColor", proDeadlinePercentColor);
				
				boolean proFreqFlag = false;
				if(rs.getString("billing_type")!=null && (rs.getString("billing_type").trim().equals("H") || rs.getString("billing_type").trim().equals("D") || rs.getString("billing_type").trim().equals("M"))){
					proFreqFlag = true;
				}
				request.setAttribute("strProFreqStartDate", strProFreqStartDate);
				request.setAttribute("strProFreqEndDate", strProFreqEndDate);
				request.setAttribute("proFreqFlag", proFreqFlag);
				
			}
			rs.close();
			pst.close();
			
//			System.out.println("PROJECT_NAME ===>> " + request.getAttribute("PROJECT_NAME"));
			
			pst = con.prepareStatement("select emp_id, _isteamlead, emp_fname, emp_mname,emp_lname from project_emp_details ped, employee_personal_details epd  where epd.emp_per_id = ped.emp_id and pro_id=?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			StringBuilder sbTeamLeader = new StringBuilder();
			StringBuilder sbTeamMember = new StringBuilder();
			while(rs.next()) {
				if(uF.parseToBoolean(rs.getString("_isteamlead"))) {
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
				
					
					sbTeamLeader.append(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")+"[TL], ");
				} else {
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					sbTeamMember.append(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")+", ");
				}
			}
			rs.close();
			pst.close();
			
			if(sbTeamLeader.length()>1) {
				sbTeamLeader.replace(0, sbTeamLeader.length(), sbTeamLeader.substring(0, sbTeamLeader.length()-2));
				request.setAttribute("TEAM_LEADER", sbTeamLeader.toString());
			}
			if(sbTeamMember.length()>1) {
				sbTeamMember.replace(0, sbTeamMember.length(), sbTeamMember.substring(0, sbTeamMember.length()-2));
				request.setAttribute("TEAM_MEMBER", sbTeamMember.toString());
			}
			
		//===start parvez date: 14-10-2022===	
//			pst = con.prepareStatement("select * from employee_personal_details epd  where emp_per_id = ?");
//			pst.setInt(1, projectOwnerId);
			pst = con.prepareStatement("select * from employee_personal_details epd  where emp_per_id in ("+projectOwnerId+") ");
			rs = pst.executeQuery();
//			Map<String, String> hmProOwner = new HashMap<String, String>();
			List<Map<String, String>> alProOwners = new ArrayList<Map<String,String>>();
			while(rs.next()) {
				Map<String, String> hmProOwner = new HashMap<String, String>();
				hmProOwner.put("EMP_ID", rs.getString("emp_per_id"));	
//				String strMiddleName = ""; 
//				if(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("") && !rs.getString("emp_mname").trim().equalsIgnoreCase("NULL")){
//					strMiddleName = rs.getString("emp_mname")+" ";
//				}
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmProOwner.put("EMP_NAME", uF.showData(rs.getString("emp_fname"), "")+strEmpMName+" "+uF.showData(rs.getString("emp_lname"), "")+"[PO]");
				hmProOwner.put("EMP_IMAGE", uF.showData(rs.getString("emp_image"), ""));
				alProOwners.add(hmProOwner);
			}
			rs.close();
			pst.close();
//			request.setAttribute("hmProOwner", hmProOwner);
			request.setAttribute("alProOwners", alProOwners);
	//===end parvez date: 14-10-2022===		
			
			request.setAttribute("proService", CF.getServiceNameById(con, ""+nSbuId));
			request.setAttribute("proOrg", CF.getOrgNameById(con, ""+nOrgId));
			
			pst = con.prepareStatement("select * from client_industry_details");
			rs = pst.executeQuery();
			Map<String, String> hmIndustry = new HashMap<String, String>();
			while(rs.next()) {
				hmIndustry.put(rs.getString("industry_id"), rs.getString("industry_name"));	
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from client_details where client_id = ?");
			pst.setInt(1, nClientId);
			rs = pst.executeQuery();
			Map<String, String> hmCustomer = new HashMap<String, String>();
			while(rs.next()) {
				hmCustomer.put("CLIENT_ID", rs.getString("client_id"));	
				hmCustomer.put("CLIENT_NAME", rs.getString("client_name"));	
				hmCustomer.put("CLIENT_LOGO", rs.getString("client_logo"));	
				
				String strIndustry = "";
				if(rs.getString("client_industry") !=null && !rs.getString("client_industry").trim().equals("")){
					if(rs.getString("client_industry").contains(",")){
						String[] strTemp =  rs.getString("client_industry").split(",");
						int x = 0;
						for(int i = 0; i < strTemp.length; i++){
							if(!strTemp[i].trim().equals("")){
								if(x == 0){
									strIndustry = uF.showData(hmIndustry.get(strTemp[i].trim()), "");
								} else {
									strIndustry += ","+ uF.showData(hmIndustry.get(strTemp[i].trim()), "");
								}
								x++;
							}
						}
						
					} else {
						strIndustry = uF.showData(hmIndustry.get(rs.getString("client_industry").trim()), "");
					}
				}
				
				hmCustomer.put("CLIENT_INDUSTRY", strIndustry);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmCustomer", hmCustomer);
			
			request.setAttribute("clientPoc", CF.getClientSPOCNameById(con, ""+nPocId));
			
			
			/*StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select ai.* from activity_info ai, projectmntnc pmc where ai.pro_id=pmc.pro_id and " +
				"ai.parent_task_id = 0 and task_accept_status != -1 and pmc.pro_id =?");
			pst = con.prepareStatement(sbQuery.toString()); 
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			Map<String, String> hmProIds = new HashMap<String, String>();
			Map<String, String> hmProTaskData = new HashMap<String, String>();
			while (rs.next()) {
				double taskIdealTime = uF.parseToDouble(hmProTaskData.get(rs.getString("pro_id")+"_IDEAL_TIME"));
				taskIdealTime = taskIdealTime + uF.parseToDouble(rs.getString("idealtime"));
				double taskWoredTime = uF.parseToDouble(hmProTaskData.get(rs.getString("pro_id")+"_WORKED_TIME"));
				double actWorkTime = (uF.parseToDouble(rs.getString("idealtime")) * uF.parseToDouble(rs.getString("completed"))) / 100;
				taskWoredTime = taskWoredTime + actWorkTime;
				
				hmProTaskData.put(rs.getString("pro_id")+"_IDEAL_TIME", taskIdealTime+"");
				hmProTaskData.put(rs.getString("pro_id")+"_WORKED_TIME", taskWoredTime+"");
				
				hmProIds.put(rs.getString("pro_id"), rs.getString("pro_id"));
			}
			rs.close();
			pst.close();*/
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select ai.* from activity_info ai, projectmntnc pmc where ai.pro_id=pmc.pro_id and " +
				"ai.parent_task_id = 0 and task_accept_status != -1 and pmc.pro_id =?");
			pst = con.prepareStatement(sbQuery.toString()); 
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			Map<String, String> hmProIds = new HashMap<String, String>();
			Map<String, String> hmProTaskData = new HashMap<String, String>();
			
			while (rs.next()) {
				double taskCompleted = uF.parseToDouble(hmProTaskData.get(rs.getString("pro_id")+"_COMPLETED"));
				taskCompleted = taskCompleted + uF.parseToDouble(rs.getString("completed"));
				double taskCount = uF.parseToDouble(hmProTaskData.get(rs.getString("pro_id")+"_TASK_COUNT"));
				taskCount = taskCount + 1;
				
				hmProTaskData.put(rs.getString("pro_id")+"_COMPLETED", taskCompleted+"");
				hmProTaskData.put(rs.getString("pro_id")+"_TASK_COUNT", taskCount+"");
				
				hmProIds.put(rs.getString("pro_id"), rs.getString("pro_id"));
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = hmProIds.keySet().iterator();
			
			Map<String, String> hmProCompPercent = new HashMap<String, String>();
			Map<String, Map<String, String>> hmProMileAndCMileCnt = new HashMap<String, Map<String, String>>();
			while (it.hasNext()) {
				Map<String, String> hmMilestoneAndCompletedMilestone = new HashMap<String, String>();
				String strProId = it.next();
				double dblProCompletePercent = 0;
				if(uF.parseToDouble(hmProTaskData.get(strProId+"_COMPLETED")) > 0) {
					dblProCompletePercent = uF.parseToDouble(hmProTaskData.get(strProId+"_COMPLETED")) / uF.parseToDouble(hmProTaskData.get(strProId+"_TASK_COUNT"));
				}
				hmProCompPercent.put(strProId, dblProCompletePercent+"");
				
				int milestoneCount = 0;
				int completedMilestoneCount = 0;
				pst = con.prepareStatement("select pmd.*, p.milestone_dependent_on from project_milestone_details pmd, projectmntnc p where pmd.pro_id= p.pro_id and pmd.pro_id=?");
				pst.setInt(1, uF.parseToInt(strProId));
				rs = pst.executeQuery();
				while (rs.next()) {
					if(uF.parseToInt(rs.getString("milestone_dependent_on")) == 2) {
						int intcomplete = getProTaskCompleted(con, uF, rs.getString("pro_task_id"));
						completedMilestoneCount += intcomplete;
					} else if(uF.parseToInt(rs.getString("milestone_dependent_on")) == 1) {
						if(rs.getDouble("pro_completion_percent") <= uF.parseToDouble(hmProCompPercent.get(strProId))) {
							completedMilestoneCount++;
						}
					}
					
					milestoneCount++;
				}
				rs.close();
				pst.close();
				
				hmMilestoneAndCompletedMilestone.put("MILESTONE_COUNT", milestoneCount+"");
				hmMilestoneAndCompletedMilestone.put("COMPLETED_MILESTONE_COUNT", completedMilestoneCount+"");
				hmProMileAndCMileCnt.put(strProId, hmMilestoneAndCompletedMilestone);
			}
			
			request.setAttribute("hmProCompPercent", hmProCompPercent);
			request.setAttribute("hmProMileAndCMileCnt", hmProMileAndCMileCnt);
			
			getProTasksAndSubTasksData(con, uF, uF.parseToInt(getPro_id())); // 
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public String getProjectIdealTimeTime(Connection con, UtilityFunctions uF, String proId, Map<String, String> hmProjectData) {
		PreparedStatement pst=null;
		ResultSet rs=null;
		String proIdealTimeHrs = null;
		try {
			pst = con.prepareStatement("select task_id, activity_name, resource_ids, idealtime, parent_task_id from activity_info where " +
				" parent_task_id = 0 and pro_id = ? ");
			pst.setInt(1, uF.parseToInt(proId));
//			System.out.println("pst======>"+pst); 
			rs=pst.executeQuery();
			Map<String, Map<String, String>> hmTaskData = new HashMap<String, Map<String,String>>();
			while(rs.next()) {
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put(rs.getString("task_id")+"_IDEAL_TIME", rs.getString("idealtime"));
				hmTaskData.put(rs.getString("task_id"), hmInner);
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = hmTaskData.keySet().iterator();
			double proBudgetedTime = 0;
//			System.out.println("billType ===>> " + billType);
			while (it.hasNext()) {
				String taskId = it.next();
				Map<String, String> hmInner = hmTaskData.get(taskId);
				proBudgetedTime += uF.parseToDouble(hmInner.get(taskId+"_IDEAL_TIME"));
				/*if(hmProjectData != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equals("H")) {
					proBudgetedTime += uF.parseToDouble(hmInner.get(taskId+"_IDEAL_TIME"));
				} else if(hmProjectData != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equals("D")) {
					proBudgetedTime += (uF.parseToDouble(hmInner.get(taskId+"_IDEAL_TIME"))* 8);
				} else if(hmProjectData != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equals("M")) {
					proBudgetedTime += ((uF.parseToDouble(hmInner.get(taskId+"_IDEAL_TIME"))* 8) * 30);
				}*/
//				System.out.println(taskId + "  taskResourceCnt ===>> " + taskResourceCnt + "  taskResourceCost ====>> " + taskResourceCost +" IDEAL_TIME =>>>>> " + hmInner.get(taskId+"_IDEAL_TIME"));
			}
			proIdealTimeHrs = proBudgetedTime+"";
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return proIdealTimeHrs;
	}
	
	
	Map<String, String> getProjectCurrentFreqStartEndDate(Connection con, UtilityFunctions uF, String proId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmProFreqStartEndDate = new HashMap<String, String>();
		try {
			
			pst = con.prepareStatement("select freq_start_date, freq_end_date from projectmntnc_frequency where freq_end_date >= ? and pro_id=? order by freq_end_date limit 1");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, uF.parseToInt(proId));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmProFreqStartEndDate.put("FREQ_START_DATE", rs.getString("freq_start_date"));
				hmProFreqStartEndDate.put("FREQ_END_DATE", rs.getString("freq_end_date"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmProFreqStartEndDate;
	}
	
	

	

	private int getProTaskCompleted(Connection con, UtilityFunctions uF, String taskId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int intTaskCompleted = 0;
		try {
			
			pst = con.prepareStatement("select task_id from activity_info where task_id =? and approve_status = 'approved' and task_accept_status != -1");
			pst.setInt(1, uF.parseToInt(taskId));
			rs = pst.executeQuery();
			while (rs.next()) {
				intTaskCompleted = 1;
			}
			rs.close();
			pst.close();

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
		return intTaskCompleted;
	}
	
	public void getReassignRescheduleTaskRequest(UtilityFunctions uF){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
		con = db.makeConnection(con);
		Map<String, String> hmEmpCodeName = new HashMap<String, String>();
		hmEmpCodeName = CF.getEmpNameMap(con, null,null);
//		Map<String,List<String>> hmRescheduleReassignMap = new HashMap<String,List<String>>();
		List<List<String>> alOuter = new ArrayList<List<String>>();
		pst = con.prepareStatement("select * from activity_info where task_reassign_reschedule_comment IS NOT NULL and pro_id=? and reschedule_reassign_request_status=0");
		pst.setInt(1, uF.parseToInt(getPro_id()));
//		System.out.println("rpst====>"+pst);
		rs = pst.executeQuery();
		while(rs.next()){
			List<String> innerList = new ArrayList<String>();
			if(rs.getString("r_start_date")!=null  && rs.getString("r_deadline")!=null && rs.getString("task_reassign_reschedule_comment")!=null) {
				innerList.add("Reschedule request");
				innerList.add(rs.getString("task_id"));
				innerList.add(rs.getString("activity_name"));
				innerList.add(hmEmpCodeName.get(rs.getString("requested_by")));
				innerList.add(rs.getString("task_reassign_reschedule_comment"));
				innerList.add(rs.getString("r_start_date"));
				innerList.add(rs.getString("r_deadline"));
			} else if(rs.getString("r_start_date")==null && rs.getString("r_deadline")==null && rs.getString("task_reassign_reschedule_comment")!=null){
				innerList.add("Reassign request");
				innerList.add(rs.getString("task_id"));
				innerList.add(rs.getString("activity_name"));
				innerList.add(hmEmpCodeName.get(rs.getString("requested_by")));
				innerList.add(rs.getString("task_reassign_reschedule_comment"));
				innerList.add("");
				innerList.add("");
			}
			alOuter.add(innerList);
//			System.out.println("innerList====>"+innerList);
		}
//		System.out.println("alOuter=====>"+alOuter);
		request.setAttribute("alOuter", alOuter);
		rs.close();
		pst.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	
	
	public void getProTasksAndSubTasksData(Connection con, UtilityFunctions uF, int proId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmSubTaskCountOfTask = getSubTaskCountOfTask(con, uF, proId);
			
//			System.out.println("strUserType ===>> " +strUserType);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select ai.*, pmc.start_date as pmc_start_date, pmc.deadline as pmc_deadline, pmc.bill_days_type, pmc.hours_for_bill_day, " +
				"pmc.actual_calculation_type, pmc.billing_type from activity_info ai, projectmntnc pmc where ai.pro_id=pmc.pro_id and " +
				"ai.parent_task_id = 0 ");
			if(strUserType != null && !strUserType.equals(CUSTOMER)) {
				sbQuery.append(" and (ai.task_accept_status >=0 or ai.task_accept_status = -2) ");
			} else {
				sbQuery.append(" and (ai.task_accept_status =1 or ai.task_accept_status =0 or ai.task_accept_status = -2) ");
			}
			if(proId > 0) {
				sbQuery.append(" and pmc.pro_id in ("+proId+")");
			}
			sbQuery.append(" order by ai.start_date, ai.task_id ");
			pst = con.prepareStatement(sbQuery.toString()); //and ai.pro_id=?
//			System.out.println("pst =========>> " + pst);
			rs = pst.executeQuery();
//			Map<String, Map<String, List<String>>> hmProWiseTasks = new LinkedHashMap<String, Map<String, List<String>>>();
			Map<String, List<String>> hmProTasks = new LinkedHashMap<String, List<String>>();
			Map<String, String> hmProTaskCount = new HashMap<String, String>();
			int taskAndSubtaskCount = 0;
			while (rs.next()) {
				
				
				int taskCount = uF.parseToInt(hmProTaskCount.get(rs.getString("pro_id")));
					taskCount++;
				hmProTaskCount.put(rs.getString("pro_id"), taskCount+"");
				String taskActivityCnt = CF.getTaskActivityTaskCount(con, uF, rs.getString("task_id"));
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("task_id")); //0
				alInner.add(rs.getString("parent_task_id"));
				alInner.add(rs.getString("pro_id")); //2
				alInner.add(uF.showData(rs.getString("activity_name"), "")); //3
//				String dependencyTask = getDependencyTaskOptions(rs.getString("task_id"), rs.getString("dependency_task"), dependencyList);
				if(rs.getInt("dependency_task") > 0) {
					alInner.add(CF.getProjectTaskNameByTaskId(con, uF, rs.getString("dependency_task"))); //4
				} else {
					alInner.add("-"); //4
				}
				if(rs.getString("dependency_type") == null || rs.getString("dependency_type").length()==0 ) {
					alInner.add(""); //5
	    		} else if(uF.parseToInt(rs.getString("dependency_type"))==0) {
	    			alInner.add("Start-Start"); //5
	    		} else if(uF.parseToInt(rs.getString("dependency_type"))==1) {
	    			alInner.add("Finish-Start"); //5
	    		}
				if(uF.parseToInt(rs.getString("priority"))==0) {
					alInner.add("<i class=\"fa fa-exclamation\" aria-hidden=\"true\" style=\"color:#afafaf\"></i>"); //6
				} else if(uF.parseToInt(rs.getString("priority"))==1) {
					alInner.add("<i class=\"fa fa-exclamation\" aria-hidden=\"true\" style=\"color:#ffcc00\"></i>"); //6
				}
				else if(uF.parseToInt(rs.getString("priority"))==2) {
					alInner.add("<i class=\"fa fa-exclamation\" aria-hidden=\"true\" style=\"color:#ff0000\"></i>"); //6
				}
				if(rs.getString("resource_ids") != null && rs.getString("resource_ids").length()>1) {
					String[] strRescId = rs.getString("resource_ids").substring(1, rs.getString("resource_ids").length()-1).split(",");
					StringBuilder empName = null;
					for(int i=0; i<strRescId.length; i++) {
						if(uF.parseToInt(strRescId[i]) > 0) {
							if(empName == null) {
								empName = new StringBuilder();
								empName.append(CF.getEmpNameMapByEmpId(con, strRescId[i]));
							} else {
								empName.append(", "+CF.getEmpNameMapByEmpId(con, strRescId[i]));
							}
						}
					}
					if(empName == null) {
						empName = new StringBuilder();
					}
					alInner.add(empName.toString());//7
				} else {
					alInner.add("");//7
				}
				alInner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT_STR)); //8
				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT_STR)); //9
				alInner.add(uF.showData(rs.getString("idealtime"), "")); //10
				alInner.add(uF.showData(rs.getString("color_code"), "")); //11
				alInner.add(uF.showData(taskActivityCnt, "")); //12
				String timeFilledEmp = getTimesheetFilledEmp(con, rs.getString("resource_ids"), rs.getString("task_id"));
				alInner.add(uF.showData(timeFilledEmp, "")); //13
				String subTaskCnt = hmSubTaskCountOfTask.get(rs.getString("task_id"));
				alInner.add(uF.showData(subTaskCnt, "")); //14
				alInner.add(uF.showData(rs.getString("completed"), "0")); //15
				
				String strColour = null;
				String strdaysColour = null;
				String strworkingColour = null;
				double no_of_hrs=0.0;
				double no_of_days=0.0;
				Map<String, String> hmProjectData = new HashMap<String, String>();
				hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("pmc_start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("pmc_deadline"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
				hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
				
				Map<String, String> hmTaskAWDaysAndHrs = CF.getProjectTaskActualWorkedDaysAndHrs(con, rs.getString("task_id"), hmProjectData);
				
				double dblCompleted = 0;
				double dblIdealTime = 0;
				double dblAlreadyWorked = 0;

				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					dblCompleted = uF.parseToDouble(rs.getString("completed"));
					dblIdealTime = uF.parseToDouble(rs.getString("idealtime"));
					dblAlreadyWorked = uF.parseToDouble(hmTaskAWDaysAndHrs.get("ACTUAL_DAYS"));
				} else if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
					dblCompleted = uF.parseToDouble(rs.getString("completed"));
					dblIdealTime = uF.parseToDouble(rs.getString("idealtime"));
					dblAlreadyWorked = uF.parseToDouble(hmTaskAWDaysAndHrs.get("ACTUAL_DAYS"));
				} else {
					dblCompleted = uF.parseToDouble(rs.getString("completed"));
					dblIdealTime = uF.parseToDouble(rs.getString("idealtime"));
					dblAlreadyWorked = uF.parseToDouble(hmTaskAWDaysAndHrs.get("ACTUAL_HRS"));
				}
				double idealPercent = 0;
				if(dblIdealTime > 0) {
					idealPercent = (dblAlreadyWorked / dblIdealTime) * 100;
				}
				
				if(idealPercent == dblCompleted) {
					strworkingColour = "yellow";
				} else if(idealPercent > dblCompleted) {
					strworkingColour = "red";
				} else {
					strworkingColour = "green";
				}
				
				double taskDealineCompletePercent = getTaskDeadlineCompletePercent(uF.parseToInt(rs.getString("task_id")));
				String tdcpSpan = "";
				if(taskDealineCompletePercent <= 50) {
					tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"Working\" src=\"images1/icons/approved.png\"></span>";
				} else if(taskDealineCompletePercent > 50 && taskDealineCompletePercent <= 75) {
					tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"Working\" src=\"images1/icons/re_submit.png\"></span>";
				} else if(taskDealineCompletePercent > 75) {
					tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"Working\" src=\"images1/icons/denied.png\"></span>";
				} else {
					tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"Working\" src=\"images1/icons/pending.png\"></span>";
				}
				
				if(dblCompleted > 100 || uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0 ) {
					no_of_days=getNoOfDays(uF.parseToInt(rs.getString("task_id")));
				}
				if(dblCompleted >= 100 || uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0 ) {
					if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("H")) {
						no_of_hrs = dblAlreadyWorked - dblIdealTime;
					} else {
						no_of_hrs = (dblAlreadyWorked/30) - dblIdealTime;
					}
				}
				
				if(uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"), DBDATE))>=0.0 && uF.parseToDouble(uF.dateDifference(rs.getString("end_date"), DBDATE, rs.getString("deadline"),DBDATE))>=0.0) {
					strdaysColour="green";
				} else {
					strdaysColour="red";
				}
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
					if((dblAlreadyWorked/30)>dblIdealTime) {
						strColour="red";
					} else {
						strColour="green";
					}
				} else {
					if(dblAlreadyWorked>dblIdealTime) {
						strColour="red";
					} else {
						strColour="green";
					}
				}
				String daySpan="none";
				String timeSpan = "none";
				String timeSpanLbl = " hrs";
				String strTime = uF.getTotalTimeMinutes100To60(""+no_of_hrs);
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					daySpan="inline";
				} else {
					if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
						timeSpanLbl = " months";
						strTime = uF.formatIntoTwoDecimal(no_of_hrs);
					}
					timeSpan="inline";
				}
				java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
				java.util.Date dtDeadlineDate = uF.getDateFormatUtil(rs.getString("deadline"), DBDATE);
				
				if(rs.getString("taskstatus") != null && rs.getString("taskstatus").equalsIgnoreCase("New Task")) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Confirmed\" src=\"images1/icons/icons/approve_icon.png\"></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Completed\" src=\"images1/icons/act_now.png\"></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b> "+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else {
						alInner.add(tdcpSpan);
						alInner.add("");
					}
					
				} else if(dtCurrentDate!=null && dtDeadlineDate!=null && dtCurrentDate.after(dtDeadlineDate)) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Confirmed\" src=\"images1/icons/icons/approve_icon.png\"></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>days</span>)");
					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Completed\" src=\"images1/icons/act_now.png\"></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					}
					
				} else if(rs.getInt("completed")>=100) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status") != null && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<span style=\"color:#5D862B\"><img title=\"Confirmed\" src=\"images1/icons/icons/approve_icon.png\"></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Completed\" src=\"images1/icons/act_now.png\"></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					}
				} else {
						if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/act_now.png\"></span>");
						alInner.add("");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan);
						alInner.add("");
					} else {
						alInner.add(tdcpSpan);
						alInner.add("");
					}
				}
				
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					alInner.add(uF.formatIntoTwoDecimal(dblAlreadyWorked)+" days"); //18
				} else if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
					double alreadyWorkedMnths = dblAlreadyWorked / 30;
					if(alreadyWorkedMnths < 0) 
						alreadyWorkedMnths = 0;
					alInner.add(uF.formatIntoTwoDecimal(alreadyWorkedMnths)+" months"); //18
				} else {
					alInner.add(uF.getTotalTimeMinutes100To60(""+dblAlreadyWorked)+" hrs"); //18
				}
				alInner.add(uF.showData(rs.getString("approve_status"), "")); //19
				alInner.add(uF.showData(rs.getString("task_description"), "")); //20
				alInner.add(uF.showData(hmEmpName.get(rs.getString("added_by")), "-")); //21
				alInner.add(uF.showData(rs.getString("task_freq_name"), "-")); //22
				alInner.add(uF.showData(rs.getString("recurring_task"), "0")); //23
				alInner.add(uF.showData(rs.getString("task_accept_status"), "0")); //24
				if(uF.parseToInt(rs.getString("priority"))==0) {
					alInner.add("#afafaf"); //25
				} else if(uF.parseToInt(rs.getString("priority"))==1) {
					alInner.add("#ffcc00"); //25
				}
				else if(uF.parseToInt(rs.getString("priority"))==2) {
					alInner.add("#ff0000"); //25
				}
//				alInner.add(uF.showData(rs.getString("task_reassign_reschedule_comment"), "-")); //25
//				alInner.add(rs.getString("task_from_my_self")); //26
//				if(uF.parseToInt(rs.getString("is_cust_add")) == 1) {
//					alInner.add(uF.showData(hmCustName.get(rs.getString("requested_by")), "")); //27
//				} else {
//					alInner.add(uF.showData(hmEmpName.get(rs.getString("requested_by")), "")); //27
//				}
//				alInner.add(uF.showData(rs.getString("is_cust_add"), "-")); //28
				
				hmProTasks.put(rs.getString("task_id"), alInner);
				taskAndSubtaskCount++;
			}
			rs.close();
			pst.close();
//			System.out.println("hmProTasks ===>> " + hmProTasks);
			request.setAttribute("hmProTasks", hmProTasks);
			request.setAttribute("hmProTaskCount", hmProTaskCount);
			
			
			StringBuilder sbSTQuery = new StringBuilder();
			sbSTQuery.append("select ai.*, pmc.start_date as pmc_start_date, pmc.deadline as pmc_deadline, pmc.bill_days_type, pmc.hours_for_bill_day, " +
					"pmc.actual_calculation_type, pmc.billing_type from activity_info ai, projectmntnc pmc where ai.pro_id=pmc.pro_id and " +
					"ai.parent_task_id != 0 ");
			if(strUserType != null && !strUserType.equals(CUSTOMER)) {
				sbQuery.append(" and (ai.task_accept_status >=0 or ai.task_accept_status = -2) ");
			} else {
				sbQuery.append(" and (ai.task_accept_status =1 or ai.task_accept_status = -2) ");
			}
			if(proId > 0) {
				sbSTQuery.append(" and pmc.pro_id in ("+proId+")");
			}
			sbSTQuery.append(" order by ai.start_date, ai.task_id ");
			pst = con.prepareStatement(sbSTQuery.toString()); //and ai.pro_id=?
//			System.out.println("pst =========>> " + pst);
			rs = pst.executeQuery();
			Map<String, List<List<String>>> hmProSubTasks = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> proSubTaskList = new ArrayList<List<String>>();
			while (rs.next()) {
				proSubTaskList = hmProSubTasks.get(rs.getString("parent_task_id"));
				if(proSubTaskList == null) proSubTaskList = new ArrayList<List<String>>();
				
				List<String> alInner = new ArrayList<String>();
				String taskActivityCnt = CF.getTaskActivityTaskCount(con, uF, rs.getString("task_id"));
				alInner.add(rs.getString("task_id")); //0
				alInner.add(rs.getString("parent_task_id")); //1
				alInner.add(rs.getString("pro_id")); //2
				alInner.add(uF.showData(rs.getString("activity_name"), "")); //3
				if(rs.getInt("dependency_task") > 0) {
					alInner.add(CF.getProjectTaskNameByTaskId(con, uF, rs.getString("dependency_task"))); //4
				} else {
					alInner.add("-"); //4
				}
				if(rs.getString("dependency_type") == null || rs.getString("dependency_type").length()==0 ) {
					alInner.add(""); //5
	    		} else if(uF.parseToInt(rs.getString("dependency_type"))==0) {
	    			alInner.add("Start-Start"); //5
	    		} else if(uF.parseToInt(rs.getString("dependency_type"))==1) {
	    			alInner.add("Finish-Start"); //5
	    		}
				if(uF.parseToInt(rs.getString("priority"))==0) {
					alInner.add("<i class=\"fa fa-exclamation\" aria-hidden=\"true\" style=\"color:#afafaf\"></i>"); //6
				} else if(uF.parseToInt(rs.getString("priority"))==1) {
					alInner.add("<i class=\"fa fa-exclamation\" aria-hidden=\"true\" style=\"color:#ffcc00\"></i>"); //6
				}
				else if(uF.parseToInt(rs.getString("priority"))==2) {
					alInner.add("<i class=\"fa fa-exclamation\" aria-hidden=\"true\" style=\"color:#ff0000\"></i>"); //6
				}
				if(rs.getString("resource_ids") != null && rs.getString("resource_ids").length()>1) {
					String[] strRescId = rs.getString("resource_ids").substring(1, rs.getString("resource_ids").length()-1).split(",");
					StringBuilder empName = null;
					for(int i=0; i<strRescId.length; i++) {
						if(uF.parseToInt(strRescId[i]) > 0) {
							if(empName == null) {
								empName = new StringBuilder();
								empName.append(CF.getEmpNameMapByEmpId(con, strRescId[i]));
							} else {
								empName.append(", "+CF.getEmpNameMapByEmpId(con, strRescId[i]));
							}
						}
					}
					if(empName == null) {
						empName = new StringBuilder();
					}
					alInner.add(empName.toString());//7
				} else {
					alInner.add("");//7
				}
				alInner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT_STR));//8
				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT_STR)); //9
				alInner.add(uF.showData(rs.getString("idealtime"), "")); //10
				alInner.add(uF.showData(rs.getString("color_code"), "")); //11
				alInner.add(uF.showData(taskActivityCnt, "")); //12
				String timeFilledEmp = getTimesheetFilledEmp(con, rs.getString("resource_ids"), rs.getString("task_id"));
				alInner.add(uF.showData(timeFilledEmp, "")); //13
				alInner.add(uF.showData("", "")); //14 subtsk cnt in task list
				alInner.add(uF.showData(rs.getString("completed"), "0")); //15
				
				String strColour = null;
				String strdaysColour = null;
				String strworkingColour = null;
				double no_of_hrs=0.0;
				double no_of_days=0.0;
				Map<String, String> hmProjectData = new HashMap<String, String>();
				hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("pmc_start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("pmc_deadline"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
				hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
				
				Map<String, String> hmTaskAWDaysAndHrs = CF.getProjectTaskActualWorkedDaysAndHrs(con, rs.getString("task_id"), hmProjectData);
				
				double dblCompleted = 0;
				double dblIdealTime = 0;
				double dblAlreadyWorked = 0;

				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					dblCompleted = uF.parseToDouble(rs.getString("completed"));
					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
					dblAlreadyWorked = uF.parseToDouble(hmTaskAWDaysAndHrs.get("ACTUAL_DAYS"));
				} else if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
					dblCompleted = uF.parseToDouble(rs.getString("completed"));
					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
					dblAlreadyWorked = uF.parseToDouble(hmTaskAWDaysAndHrs.get("ACTUAL_DAYS"));
				} else {
					dblCompleted = uF.parseToDouble(rs.getString("completed"));
					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
					dblAlreadyWorked = uF.parseToDouble(hmTaskAWDaysAndHrs.get("ACTUAL_HRS"));
				}
				double idealPercent = 0;
				if(dblIdealTime > 0) {
					idealPercent = (dblAlreadyWorked / dblIdealTime) * 100;
				}
				
				if(idealPercent == dblCompleted) {
					strworkingColour = "yellow";
				} else if(idealPercent > dblCompleted) {
					strworkingColour = "red";
				} else {
					strworkingColour = "green";
				}
				
				double taskDealineCompletePercent = getTaskDeadlineCompletePercent(uF.parseToInt(rs.getString("task_id")));
				String tdcpSpan = "";
				if(taskDealineCompletePercent <= 50) {
					tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"Working\" src=\"images1/icons/approved.png\"></span>";
				} else if(taskDealineCompletePercent > 50 && taskDealineCompletePercent <= 75) {
					tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"Working\" src=\"images1/icons/re_submit.png\"></span>";
				} else if(taskDealineCompletePercent > 75) {
					tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"Working\" src=\"images1/icons/denied.png\"></span>";
				} else {
					tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"Working\" src=\"images1/icons/pending.png\"></span>";
				}
				
				if(dblCompleted > 100 || uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE)) < 0.0) {
					no_of_days = getNoOfDays(uF.parseToInt(rs.getString("task_id")));
				}
				if(dblCompleted >= 100 || uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE)) < 0.0) {
					if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("H")) {
						no_of_hrs = dblAlreadyWorked - dblIdealTime;
					} else {
						no_of_hrs = (dblAlreadyWorked/30) - dblIdealTime;
					}
				}
				
				if(uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))>=0.0 && uF.parseToDouble(uF.dateDifference(rs.getString("end_date"), DBDATE, rs.getString("deadline"),DBDATE))>=0.0) {
					strdaysColour="green";
				} else {
					strdaysColour="red";
				}
				if(dblAlreadyWorked>dblIdealTime) {
					strColour="red";
				} else {
					strColour="green";
				}
				
				String daySpan="none";
				String timeSpan = "none";
				String timeSpanLbl = " hrs";
				String strTime = uF.getTotalTimeMinutes100To60(""+no_of_hrs);
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					daySpan="inline";
				} else {
					if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
						timeSpanLbl = " months";
						strTime = uF.formatIntoTwoDecimal(no_of_hrs);
					}
					timeSpan="inline";
				}
				
				java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
				java.util.Date dtDeadlineDate = uF.getDateFormatUtil(rs.getString("deadline"), DBDATE);
				
				if(rs.getString("taskstatus") != null && rs.getString("taskstatus").equalsIgnoreCase("New Task")) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Confirmed\" src=\"images1/icons/icons/approve_icon.png\"></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Completed\" src=\"images1/icons/act_now.png\"></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b> "+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else {
						alInner.add(tdcpSpan);
						alInner.add("");
					}
					
				} else if(dtCurrentDate!=null && dtDeadlineDate!=null && dtCurrentDate.after(dtDeadlineDate)) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Confirmed\" src=\"images1/icons/icons/approve_icon.png\"></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>days</span>)");
					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Completed\" src=\"images1/icons/act_now.png\"></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					}
					
				} else if(rs.getInt("completed")>=100) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status") != null && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<span style=\"color:#5D862B\"><img title=\"Confirmed\" src=\"images1/icons/icons/approve_icon.png\"></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Completed\" src=\"images1/icons/act_now.png\"></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					}
				} else {
						if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/act_now.png\"></span>");
						alInner.add("");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan);
						alInner.add("");
					} else {
						alInner.add(tdcpSpan);
						alInner.add("");
					}
				}
				
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					alInner.add(uF.formatIntoOneDecimal(dblAlreadyWorked)+" days"); //18
				} else if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
					double alreadyWorkedMnths = dblAlreadyWorked / 30;
					if(alreadyWorkedMnths < 0) 
						alreadyWorkedMnths = 0;
					alInner.add(uF.formatIntoOneDecimal(alreadyWorkedMnths)+" months"); //18
				} else {
					alInner.add(uF.getTotalTimeMinutes100To60(""+dblAlreadyWorked)+" hrs"); //18
				}
				alInner.add(uF.showData(rs.getString("approve_status"), "")); //19
				alInner.add(uF.showData(rs.getString("task_description"), "")); //20
				alInner.add(uF.showData(hmEmpName.get(rs.getString("added_by")), "-")); //21
				alInner.add(uF.showData(rs.getString("task_freq_name"), "-")); //22
				alInner.add(uF.showData(rs.getString("recurring_task"), "0")); //23
				alInner.add(uF.showData(rs.getString("task_accept_status"), "0")); //24
//				alInner.add(uF.showData(rs.getString("task_reassign_reschedule_comment"), "-")); //25
//				alInner.add(rs.getString("task_from_my_self")); //26
//				if(uF.parseToInt(rs.getString("is_cust_add")) == 1) {
//					alInner.add(uF.showData(hmCustName.get(rs.getString("requested_by")), "")); //27
//				} else {
//					alInner.add(uF.showData(hmEmpName.get(rs.getString("requested_by")), "")); //27
//				}
//				alInner.add(uF.showData(rs.getString("is_cust_add"), "-")); //28
				
				proSubTaskList.add(alInner);
				hmProSubTasks.put(rs.getString("parent_task_id"), proSubTaskList);
				taskAndSubtaskCount++;
			}
			rs.close();
			pst.close();
//			System.out.println("hmProSubTasks ===>> " + hmProSubTasks);
			request.setAttribute("hmProSubTasks", hmProSubTasks);
			request.setAttribute("taskAndSubtaskCount", taskAndSubtaskCount);
			 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}


	private Map<String, String> getSubTaskCountOfTask(Connection con, UtilityFunctions uF, int proId) { //, String taskId
	//	String subTaskCnt = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmSubTaskCountOfTask = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select count(task_id) as stCnt, parent_task_id from activity_info where pro_id = ? and task_accept_status != -1 group by parent_task_id"); //where parent_task_id = ?
			pst.setInt(1, proId);
			rs = pst.executeQuery();
			while(rs.next()) {
				hmSubTaskCountOfTask.put(rs.getString("parent_task_id"), rs.getString("stCnt"));
	//			subTaskCnt = rs.getString("stCnt");
			}
			rs.close();
			pst.close();
	//		System.out.println("PROJECT_NAME ===>> " + request.getAttribute("PROJECT_NAME"));
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
		return hmSubTaskCountOfTask;
	}


	private String getTimesheetFilledEmp(Connection con, String resourceIds, String taskId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder timeFilledEmp = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			if(resourceIds != null && !resourceIds.trim().equals("") && resourceIds.length() > 1) {
	//			System.out.println("resourceIds ===>> " + resourceIds);
				resourceIds = resourceIds.trim().substring(1, resourceIds.length()-1);
				if(!resourceIds.trim().equals("") && resourceIds.length() > 0) {
					pst = con.prepareStatement("select * from task_activity where emp_id in("+resourceIds+") and activity_id=?");
					pst.setInt(1, uF.parseToInt(taskId));
	//				System.out.println("pst======main===" + pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						if(timeFilledEmp == null) {
							timeFilledEmp = new StringBuilder();
							timeFilledEmp.append(rs.getString("emp_id"));
						} else {
							timeFilledEmp.append(","+rs.getString("emp_id"));
						}
					}
					rs.close();
					pst.close();
					if(timeFilledEmp == null) {
						timeFilledEmp = new StringBuilder();
					}
				}
	//			System.out.println("timeFilledEmp ===>> " + timeFilledEmp.toString());
			}
			if(timeFilledEmp == null) {
				timeFilledEmp = new StringBuilder();
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
		return timeFilledEmp.toString();
	}


	public double getTaskDeadlineCompletePercent(int t_id) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		double completedPercent=0.0f;
		try {
			con = db.makeConnection(con);
			String dl_date = "";
			String start_date = "";
			String end_date = "";
			pst = con.prepareStatement("select deadline,start_date,end_date from activity_info where task_id=?");
			pst.setInt(1, t_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				dl_date = rs.getString(1);
				start_date = rs.getString(2);
				end_date = rs.getString(3);
			}
			rs.close();
			pst.close();
			
			if(start_date!= null && !start_date.equals("") && dl_date != null && !dl_date.equals("")) {
				ideal_days = uF.parseToDouble(uF.dateDifference(start_date, DBDATE, dl_date, DBDATE));
			}
			if(start_date!= null && !start_date.equals("")) {
				actual_days = uF.parseToDouble(uF.dateDifference(start_date, DBDATE, uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE));
			}
			if(ideal_days > 0) {
				completedPercent = (actual_days / ideal_days) * 100;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return completedPercent;
	}


	public double getNoOfDays(int t_id) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		double no_of_days=0.0f;
		try {
			con = db.makeConnection(con);
			String dl_date = "";
			String start_date = "";
			String end_date = "";
			
			pst = con.prepareStatement("select deadline,start_date,end_date from activity_info where task_id=?");
			pst.setInt(1, t_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				dl_date = rs.getString(1);
				start_date = rs.getString(2);
				end_date = rs.getString(3);
			}
			rs.close();
			pst.close();
			
			if(start_date!=null && !start_date.equals("") && dl_date!=null && !dl_date.equals("")) {
				ideal_days = uF.parseToDouble(uF.dateDifference(start_date, DBDATE, dl_date, DBDATE));
			}
			if(start_date!=null && !start_date.equals("") && end_date!=null && !end_date.equals("")) {
				actual_days = uF.parseToDouble(uF.dateDifference(start_date, DBDATE, end_date, DBDATE));
			} else if(start_date!=null && !start_date.equals("")) {
				actual_days = uF.parseToDouble(uF.dateDifference(start_date, DBDATE, uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE));
			}
			
			no_of_days = actual_days - ideal_days;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return no_of_days;
	}
	
	

//	public void getTaskDetails(UtilityFunctions uF, CommonFunctions CF) {
//		Database db = new Database();
//		db.setRequest(request);
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		List<List<String>> taskList = new ArrayList<List<String>>();
//		List<String> innerList;
//		String priorityColor = "";
////		String estimateLbl = "hours";
//		try {
//		con = db.makeConnection(con);
//		String strResourceIds = null; 
//		pst = con.prepareStatement("select pro_id,activity_name,priority,resource_ids,deadline,start_date,dependency_task,dependency_type,idealtime from activity_info where pro_id=?");
//		pst.setInt(1, uF.parseToInt(getPro_id()));
//		rs = pst.executeQuery();
//		while(rs.next()) {
//			innerList = new ArrayList<String>();
//			innerList.add(rs.getString("activity_name"));
//			if(rs.getInt("dependency_task") > 0) {
//				innerList.add(CF.getProjectTaskNameByTaskId(con, uF, rs.getString("dependency_task")));
//			} else {
//				innerList.add("-");
//			}
//    		if(rs.getString("dependency_type") == null || rs.getString("dependency_type").length()==0 ) {
//    			innerList.add("");
//    		} else if(uF.parseToInt(rs.getString("dependency_type"))==0) {
//    			innerList.add("Start-Start");
//    		} else if(uF.parseToInt(rs.getString("dependency_type"))==1) {
//    			innerList.add("Finish-Start");
//    		}
//			if(uF.parseToInt(rs.getString("priority"))==0) {
//				priorityColor = "<i class=\"fa fa-exclamation\" aria-hidden=\"true\" style=\"color:#afafaf\"></i>";
//				innerList.add(priorityColor);
//			} else if(uF.parseToInt(rs.getString("priority"))==1) {
//				priorityColor = "<i class=\"fa fa-exclamation\" aria-hidden=\"true\" style=\"color:#ffcc00\"></i>";
//				innerList.add(priorityColor);
//			}
//			else if(uF.parseToInt(rs.getString("priority"))==2) {
//				priorityColor = "<i class=\"fa fa-exclamation\" aria-hidden=\"true\" style=\"color:#ff0000\"></i>";
//				innerList.add(priorityColor);
//			}
//			strResourceIds = rs.getString("resource_ids");
//			if(strResourceIds != null && !strResourceIds.equals("")) {
//				List<String> resourceIdList = new ArrayList<String>(Arrays.asList(strResourceIds.split(",")));
//				if(resourceIdList.size()>1) {
//					StringBuilder empName = null;
//					for(int i=0; i<resourceIdList.size(); i++) {
//						if(uF.parseToInt(resourceIdList.get(i)) > 0) {
//							if(empName == null) {
//								empName = new StringBuilder();
//								empName.append(CF.getEmpNameMapByEmpId(con,resourceIdList.get(i)));
//							} else {
//								empName.append(", "+CF.getEmpNameMapByEmpId(con,resourceIdList.get(i)));
//							}
//						}
//					}
//					if(empName == null) {
//						empName = new StringBuilder();
//					}
//					innerList.add(empName.toString());
//				} else {
//					innerList.add("");
//				}
//			} else {
//				innerList.add("");
//			}
//			innerList.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT_STR));
//			innerList.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT_STR));
//			innerList.add(rs.getString("idealtime"));
//			taskList.add(innerList);
//		}
//		rs.close();
//		pst.close();
//		request.setAttribute("taskList", taskList);
////		System.out.println("TaskList====>"+TaskList);
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
	}

	public String getPro_id() {
		return pro_id;
	}

	public void setPro_id(String pro_id) {
		this.pro_id = pro_id;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}
	
}
