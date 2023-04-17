package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectwiseTimesheetDetails extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	HttpSession session;
	String strSessionEmpId;
	String strUserType;
	
	
	String proId;
	String type;
	
	String empId;
	String submitDate;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		request.setAttribute(PAGE, "/jsp/task/ProjectTimesheet.jsp");
		request.setAttribute(TITLE, "Timesheet");
		
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		if(type == null) {
			getTimesheet2();
			
			return SUCCESS;
		} 
		
		if(type != null && type.equals("AP")) {
			approveBillableHours();
			return LOAD;
		}

		return SUCCESS;
	}


	private void approveBillableHours() {
		 
		Connection con = null;
		PreparedStatement pst = null; 
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update task_activity set is_billable_approved = ?, billable_approved_by=?, billable_approve_date=? " +
				"where emp_id = ? and submited_date=?");			
			pst.setInt(1, 1);
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt(getEmpId()));
			if(getSubmitDate() == null) {
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			} else {
				pst.setDate(5, uF.getDateFormat(uF.getDateFormat(getSubmitDate(), DBDATE, DATE_FORMAT), DATE_FORMAT));
			}
//			System.out.println("pst ===>> " + pst);
			pst.executeUpdate();
			pst.close();
			request.setAttribute("STATUS_MSG", "Approved");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getTimesheet2() {
		 
		Connection con = null;
		PreparedStatement pst = null; 
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			
			String[] strPayCycleDates = null;
			strPayCycleDates = CF.getCurrentPayCycle(con,CF.getStrTimeZone(), CF);
//			if (getPaycycle() != null) {
//				strPayCycleDates = getPaycycle().split("-");
//			} else {
//				strPayCycleDates = CF.getCurrentPayCycle(con,CF.getStrTimeZone(), CF);
//				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
//			}
//			System.out.println("paycycle====>"+strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
			
			Calendar cal1 = GregorianCalendar.getInstance();
			cal1.setTime(uF.getDateFormatUtil(strPayCycleDates[0], DATE_FORMAT));

			List<String> alDates = new ArrayList<String>();
			
			for(int i=0; i<32; i++) {
				String strDate = cal1.get(Calendar.DATE)+"/"+(cal1.get(Calendar.MONTH)+1)+"/"+cal1.get(Calendar.YEAR);
				alDates.add(uF.getDateFormat(strDate, DATE_FORMAT, DATE_FORMAT));
				if(strPayCycleDates[1]!=null && strPayCycleDates[1].equalsIgnoreCase(uF.getDateFormat(strDate, DATE_FORMAT, DATE_FORMAT))) {
					break;
				}
				cal1.add(Calendar.DATE, 1);
			}
			
			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
			String locationID=hmEmpWlocationMap.get(strSessionEmpId);
			
			pst = con.prepareStatement("select effective_id,max(member_position) as member_position from work_flow_details wf where is_approved=0 " +
					" and effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in(select ta.task_id from task_activity ta " +
					"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id");			
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmMaxPositionApproval = new HashMap<String, String>();			
			while(rs.next()) {
				hmMaxPositionApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select effective_id,min(member_position) as member_position from work_flow_details wfd " +
					" where is_approved=0 and effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in(select ta.task_id from task_activity ta " +
					"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?)  group by effective_id");	
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();
			
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			
			while(rs.next()) {
//				System.out.println("next emp_po "+rs.getString("effective_id")+"   "+hmMaxPositionApproval.get(rs.getString("effective_id"))+" "+rs.getString("member_position"));
//				if(hmMaxPositionApproval.get(rs.getString("effective_id"))!=null && !hmMaxPositionApproval.get(rs.getString("effective_id")).equals(rs.getString("member_position"))) {
					hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
//				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select effective_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
					" and is_approved=0 and effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in (select ta.task_id from task_activity ta " +
					"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id ");
			pst.setInt(1,uF.parseToInt(strSessionEmpId));	
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));			
			rs = pst.executeQuery();			
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();			
			while(rs.next()) {
				hmMemNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("=======3========"+new Date());
			
			pst = con.prepareStatement("select effective_id,member_position,emp_id from work_flow_details " +
					"where effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in(select ta.task_id from task_activity ta " +
					"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?) order by effective_id");	
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmNextApprovalMem = new HashMap<String, String>();			
			while(rs.next()) {
				hmNextApprovalMem.put(rs.getString("effective_id")+"_"+rs.getString("member_position"), rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("=======4========"+new Date());
			
			pst = con.prepareStatement("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_TIMESHEET+"'" +
					"  and effective_id in(select ta.task_id from task_activity ta where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("effective_id")))
					deniedList.add(rs.getString("effective_id"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("=======5========"+new Date());
			
			pst = con.prepareStatement("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
					" and emp_id=? and effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in(select ta.task_id from task_activity ta " +
					"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id,is_approved");
			pst.setInt(1,uF.parseToInt(strSessionEmpId));	
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproval = new HashMap<String, String>();			
			while(rs.next()) {
				hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
			}
			rs.close();
			pst.close();
			
			
//			System.out.println("=======6========"+new Date());
			
			pst = con.prepareStatement("select effective_id,emp_id from work_flow_details where member_type=3 " +
					" and effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in(select ta.task_id from task_activity ta " +
					"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id,emp_id");	
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, Set<String>> hmAnyOneApproeBy = new HashMap<String, Set<String>>();			
			while(rs.next()) {
				Set<String> innerSet = hmAnyOneApproeBy.get(rs.getString("effective_id"));;
				if(innerSet == null) innerSet = new HashSet<String>();
				innerSet.add(rs.getString("emp_id"));
				
				hmAnyOneApproeBy.put(rs.getString("effective_id"), innerSet);
			}
			rs.close();
			pst.close();
			
//			System.out.println("=======7========"+new Date());
			
			pst = con.prepareStatement("select effective_id,emp_id from work_flow_details where member_type!=3 " +
				" and effective_type='"+WORK_FLOW_TIMESHEET+"'  and effective_id in(select ta.task_id from task_activity ta " +
					"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id,emp_id");	
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();			
			while(rs.next()) {
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("=======8========"+new Date());
			
			pst = con.prepareStatement("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in(select ta.task_id from task_activity ta " +
					"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?) order by effective_id,member_position");	
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
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
			
//			System.out.println("=======9========"+new Date());
			
			pst = con.prepareStatement("select ud.emp_id from user_details ud,employee_official_details eod,employee_personal_details epd where " +
				" ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'");
			pst.setInt(1, uF.parseToInt(locationID));
			rs = pst.executeQuery();			
			Map<String, String> hmEmpByLocation = new HashMap<String, String>();			
			while(rs.next()) {
				hmEmpByLocation.put(rs.getString("emp_id"), rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("=======11========"+new Date());
			
			Map<String, String> hmEmployeeName = CF.getEmpNameMap(con, null, null);
			
			List<String> taskList = getProjectTasks(con, getProId());
			
			StringBuilder sbTasks = null;
			for(int i=0; taskList != null && i<taskList.size(); i++) {
				if(sbTasks == null) {
					sbTasks = new StringBuilder();
					sbTasks.append(taskList.get(i));
				} else {
					sbTasks.append(","+taskList.get(i));
				}
			}
			
			if(sbTasks == null) {
				sbTasks = new StringBuilder();
			}
//			Map<String, String> hmActualWorkingDays = CF.getEmpNameMap(null, null);
//			Map<String, String> hmProjectData = getProjectDetails(con, getProId());
			Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, getProId());
			
			
			Map<String, String> hmBillableHrs = new HashMap<String, String>();
			List alReport = new ArrayList();
			
			if(sbTasks.toString().length() > 0) {
				StringBuilder sb1 = new StringBuilder();
				sb1.append("select sum(billable_hrs) as billable_hrs, count(distinct task_date) as task_date, ta.emp_id, submited_date, " +
						"is_billable_approved from task_activity ta where task_date between ? and ? and is_approved = 2 and is_billable = true ");
				if(sbTasks.toString().length() > 0) {
					sb1.append(" and activity_id in("+sbTasks.toString()+") ");
				}
				sb1.append(" group by ta.emp_id,submited_date,is_billable_approved");
				
	//			pst = con.prepareStatement("select sum(billable_hrs) as billable_hrs, count(distinct task_date) as task_date, ta.emp_id, submited_date, " +
	//					"is_billable_approved from task_activity ta where task_date between ? and ? and is_approved = 2 and is_billable = true and activity_id in("+sbTasks.toString()+")  " + //and ta.is_billable_approved = 0
	//					"group by ta.emp_id,submited_date,is_billable_approved");
				pst = con.prepareStatement(sb1.toString());
				pst.setDate(1, uF.getDateFormat(hmProjectData.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_END_DATE"), DATE_FORMAT));
	//			System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				
				while(rs.next()) {
					if(hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equals("H")) {
						hmBillableHrs.put(rs.getString("emp_id")+"_"+rs.getString("submited_date")+"_"+rs.getString("is_billable_approved"), Math.round(rs.getDouble("billable_hrs"))+" h");
					} else {
						double totBillableDays = 0;
						if(hmProjectData.get("PRO_BILL_DAYS_TYPE") != null && hmProjectData.get("PRO_BILL_DAYS_TYPE").equals("2") && uF.parseToDouble(hmProjectData.get("PRO_HOURS_FOR_BILL_DAY")) > 0) {
							totBillableDays = rs.getDouble("billable_hrs") / uF.parseToDouble(hmProjectData.get("PRO_HOURS_FOR_BILL_DAY"));
	//						System.out.println(" totActualDays in ==>> " + totActualDays);
							System.out.println(" totBillableDays in ==>> " + totBillableDays);
						} else {
							totBillableDays = rs.getDouble("task_date");
						}
						System.out.println(" totBillableDays ==>> " + totBillableDays);
						hmBillableHrs.put(rs.getString("emp_id")+"_"+rs.getString("submited_date")+"_"+rs.getString("is_billable_approved"), totBillableDays+" d");
					}
				}
				rs.close();
				pst.close();
			
//			System.out.println("=======12========"+new Date());
			
//			System.out.println("hmNextApproval 11===>> " + hmNextApproval);
//			 System.out.println("hmAnyOneApproval 11===>> " + hmAnyOneApproval);
//			 System.out.println("hmMemNextApproval 11===>> " + hmMemNextApproval);
			 
				pst = con.prepareStatement("select ta.task_id,ta.emp_id,ta.is_billable_approved,ta.billable_approved_by,ta.billable_approve_date," +
					"ta.submited_date,activity_id from task_activity ta where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ? " +
					"and is_billable = true and is_approved = 2 and activity_id in("+sbTasks.toString()+") " +
					" order by ta.emp_id, ta.submited_date, is_billable_approved"); //and ta.is_billable_approved = 0 				
				pst.setDate(1, uF.getDateFormat(hmProjectData.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_END_DATE"), DATE_FORMAT));
	//			System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();			 
				
				List<String> alInner = new ArrayList<String>();
				String strEmpIdNew = null;
				String strEmpIdOld = null;
				String strSubmitDTNew = null;
				String strSubmitDTOld = null;
				String strBillableApproveNew = null;
				String strBillableApproveOld = null;
				Map<String, List<String>> hmEmpwiseTimwsheet = new HashMap<String, List<String>>();
				int count = 0;
				while(rs.next()) {
					
					strEmpIdNew = rs.getString("emp_id");
					strSubmitDTNew = rs.getString("submited_date");
					strBillableApproveNew = rs.getString("is_billable_approved");
	//				System.out.println("strEmpIdNew ====>> " + strEmpIdNew);
	//				System.out.println("strSubmitDTNew ====>> " + strSubmitDTNew);
	//				System.out.println("strBillableApproveNew ====>> " + strBillableApproveNew);
	//				System.out.println("strBillableApproveOld ====>> " + strBillableApproveOld);
					
					if(strEmpIdNew!=null && strEmpIdNew.equalsIgnoreCase(strEmpIdOld) && strSubmitDTNew!=null && strSubmitDTNew.equalsIgnoreCase(strSubmitDTOld) 
							&& strBillableApproveNew !=null && strBillableApproveNew.equalsIgnoreCase(strBillableApproveOld)) {
						continue;
					}
					
					List<String> checkEmpList=hmCheckEmp.get(rs.getString("task_id"));
					if(checkEmpList==null) checkEmpList=new ArrayList<String>();
					
					if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(HRMANAGER)) { 
						continue;
					}
					
					if(strUserType.equalsIgnoreCase(HRMANAGER) && hmEmpByLocation.get(rs.getString("emp_id"))==null) {
						continue;
					}
					
					alInner = new ArrayList<String>();
					
					StringBuilder sb = new StringBuilder();
					 int i=0;
					 
	//				 System.out.println("task_id ===>> "+ rs.getString("task_id")+" hmNextApproval ===>> "+hmNextApproval.get(rs.getString("task_id")));
	//				 System.out.println("hmAnyOneApproval ===>> " + hmAnyOneApproval.get(rs.getString("task_id")));
	//				 System.out.println("hmMemNextApproval ===>> " + hmMemNextApproval.get(rs.getString("task_id")));
					if(deniedList.contains(rs.getString("task_id"))) {
	//					System.out.println("1");
						 /*sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
						sb.append("<i class=\"fa fa-circle\" title=\"Denied\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
						
					} else if(uF.parseToInt(hmAnyOneApproval.get(rs.getString("task_id")))==1 && uF.parseToInt(hmAnyOneApproval.get(rs.getString("task_id")))==rs.getInt("is_billable_approved")) {
	//					System.out.println("3");
						 /*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
						
						i++;
					} else if(uF.parseToInt(hmNextApproval.get(rs.getString("task_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("task_id"))) && uF.parseToInt(hmNextApproval.get(rs.getString("task_id")))>0) {
	//					System.out.println("4");
						/*sb.append("<img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" />");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"click to approve/deny\"></i>");
						
						i++;
						
					} else if(uF.parseToInt(hmNextApproval.get(rs.getString("task_id")))>uF.parseToInt(hmMemNextApproval.get(rs.getString("task_id"))) || (uF.parseToInt(hmNextApproval.get(rs.getString("task_id")))==0 && uF.parseToInt(hmNextApproval.get(rs.getString("task_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("task_id"))))) {
	//					System.out.println("5");
						if(!checkEmpList.contains(strSessionEmpId) && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))) { 
							if(rs.getInt("is_billable_approved")==1) {
								if(strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER)) {
									/*sb.append("<img title=\"click to approve/deny\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
									sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"click to approve/deny\"></i>");
									
									i++;
								} else {
									/*sb.append("<img title=\"click to approve/deny\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
									sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"click to approve/deny\"></i>");
									
									i++;
								}
							} else if(rs.getInt("is_billable_approved")==1) {							
								/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
								sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
								
								i++;
							} else {
								/*sb.append("<img title=\"Denied\" src=\"images1/icons/pending.png\" border=\"0\" />");*/
								sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Denied\" ></i>");
								
							}
							
						} else {
						
							/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
							sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
							
						}
					} else if(rs.getInt("is_billable_approved")==1) {
	//					System.out.println("2");
						/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
						
						i++;
					} else {
	//					System.out.println("6");
	//					sb.append("<img src=\"images1/icons/pullout.png\" border=\"0\" />");
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\"></i>");
					}
					
					alInner.add(sb.toString());
					
					
					alInner.add(hmEmployeeName.get(rs.getString("emp_id")));
					alInner.add(uF.getDateFormat(rs.getString("submited_date"), DBDATE, CF.getStrReportDateFormat()));
					if(uF.parseToInt(rs.getString("is_billable_approved")) == 1) {
						alInner.add(uF.showData(hmEmployeeName.get(rs.getString("billable_approved_by")), ""));
					} else {
						alInner.add("");
					}
					alInner.add(alDates.size()+"");
					
					alInner.add(uF.showData(hmBillableHrs.get(rs.getString("emp_id")+"_"+rs.getString("submited_date")+"_"+rs.getString("is_billable_approved")), "0")); 
					
					alInner.add("<a href=\"AddProjectActivity1.action?strPaycycle="+uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, DATE_FORMAT)+"-"+uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, DATE_FORMAT)+"-"+strPayCycleDates[2]+"&strEmpId="+rs.getString("emp_id")+"\">View</a>");
					
					if(uF.parseToInt(rs.getString("is_billable_approved")) == 1) {
						alInner.add("Approved");
					} else {
						alInner.add("<a href=\"javascript:void(0);\" onclick=\"getContent('myProjectTimesheet_"+rs.getString("emp_id")+"_"+count+"','ProjectwiseTimesheetDetails.action?type=AP&amp;empId="+rs.getString("emp_id")+"&amp;submitDate="+rs.getString("submited_date")+"')\" >Approve for Billing</a>");
					}
	//				alInner.add("<a href=\"GenerateTimeSheet1.action?mailAction=sendMail&amp;empid="+rs.getString("emp_id")+"&amp;datefrom="+uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, DATE_FORMAT)+"&amp;dateto="+uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, DATE_FORMAT)+"&amp;downloadSubmit=0\" class=\"xls\">Download</a>");
					
					StringBuilder sbCheckApproveby=new StringBuilder();
					
					if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("task_id"))!=null) {
						Set<String> anyOneSet= hmAnyOneApproeBy.get(rs.getString("task_id"));
						if(anyOneSet==null)anyOneSet=new HashSet<String>();
						
						Iterator<String> it = anyOneSet.iterator();
						String approvedby = "";
						int x = 0;
						while(it.hasNext()) {
							String empid = it.next();
							if(x==0) {
								approvedby = uF.showData(hmEmployeeName.get(empid), "");
							} else {
								approvedby +=", "+ uF.showData(hmEmployeeName.get(empid), "");
							}
							x++;
						}
						sbCheckApproveby.append(approvedby);
					} else {
						if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("task_id"))!=null) {
							sbCheckApproveby.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("task_id")+"','"+hmEmployeeName.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");	
						} else {
							sbCheckApproveby.append("");
						}
					}
					alInner.add(sbCheckApproveby.toString());
					alInner.add(rs.getString("emp_id"));
					alInner.add(""+count);
					
					alReport.add(alInner);
					strEmpIdOld = strEmpIdNew;
					strSubmitDTOld = strSubmitDTNew;
					strBillableApproveOld = strBillableApproveNew;
					count++;
					
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("alReport", alReport);
//			System.out.println("=======13========"+new Date());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private List<String> getProjectTasks(Connection con, String proId) {
		 
		PreparedStatement pst = null; 
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
//		Map<String, String> hmProjectData = new HashMap<String, String>();
		List<String> tasksList = new ArrayList<String>();
		try {
			
			pst = con.prepareStatement("select emp_id from project_emp_details where pro_id = ?");				
			pst.setInt(1, uF.parseToInt(proId));
			rs = pst.executeQuery();			 
//			System.out.println("pst ===> " + pst);
			List<String> empList = new ArrayList<String>();
			while(rs.next()) {
				empList.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("empList ===> " + empList);
			
			
			for(int i=0; empList!= null && i<empList.size(); i++) {
				pst = con.prepareStatement("select task_id from activity_info where pro_id = ? and emp_id=?");				
				pst.setInt(1, uF.parseToInt(proId));
				pst.setInt(2, uF.parseToInt(empList.get(i)));
				rs = pst.executeQuery();			 
//				System.out.println("pst ===> " + pst);
				while(rs.next()) {
					tasksList.add(rs.getString("task_id"));
				}
				rs.close();
				pst.close();
			}
//			System.out.println("tasksList ===> " + tasksList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tasksList;
	}
	
	
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}


	public String getProId() {
		return proId;
	}


	public void setProId(String proId) {
		this.proId = proId;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getEmpId() {
		return empId;
	}


	public void setEmpId(String empId) {
		this.empId = empId;
	}


	public String getSubmitDate() {
		return submitDate;
	}


	public void setSubmitDate(String submitDate) {
		this.submitDate = submitDate;
	}
	

}
