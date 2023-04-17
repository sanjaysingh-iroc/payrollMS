package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillTask;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CustomerProjectTimesheet extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;

//	List<FillProjectList> projectdetailslist;
//	List<FillClients> clientlist;
	List<FillTask> tasklist;
//	List<FillPayCycles> paycycleList;
//	String strPaycycle;
//	String clientId;
//	String clientName; 

	//Database db = new Database();
	CommonFunctions CF;
	UtilityFunctions uF = new UtilityFunctions();

	String frmDate;
	String toDate;
//	List<String> strClient;
//	List<String> strProject;
//	String totalHours;
	String strActivity;
	String strActivityTaskId;

	String type;
	String unlock;
	int nDateCount = 0;

	String save;
	String submit1;

	HttpSession session;
	String strSessionEmpId = null;
	String strUserType = null;

//	String userlocation;

	String policy_id;
	String checkTask;
//	String[] compOff;
//	String[] compOffDate;
	
//	String[] unPaidHolidays;
//	String[] unPaidHolidaysDate;
	String proId;
	String proFreqId;
	String strResourceId;
	
	String[] checkTaskId;
	
	String pageType;
	
	public String execute() {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
//		db.setRequest(request);

		strSessionEmpId = (String) session.getAttribute(EMPID);
		strUserType = (String) session.getAttribute(BASEUSERTYPE);

		request.setAttribute(PAGE, "/jsp/task/CustomerProjectTimesheet.jsp");
		request.setAttribute(TITLE, "Project Timesheet");

//		userlocation = getUserLocation();

//		System.out.println("getStrEmpId before =========>> " + getStrEmpId());
		if (getStrEmpId() == null || (getStrEmpId() != null && getStrEmpId().equalsIgnoreCase("NULL"))) {
			setStrEmpId(strSessionEmpId);
		} else {
			setStrEmpId(getStrEmpId());
		}

//		System.out.println("getStrEmpId =========>> " + getStrEmpId());
//		System.out.println("getStrResourceId =========>> " + getStrResourceId());
//		System.out.println("getproId =========>> " + getProId());
//		System.out.println("proFreqId =========>> " + getProFreqId());
//		String strEmpOrgId = CF.getEmpOrgId(uF, getStrEmpId(), request);
		
		if (getSave() != null && !getSave().equals("")) {
			saveTaskData();
		}

		if (getType() != null && !getType().equals("") && strUserType != null && strUserType.equals(CUSTOMER)) {
			customerApproveForBilling();
		}
		
		if (getType() != null && !getType().equals("") && strUserType != null && !strUserType.equals(CUSTOMER)) {
			saveTypeData1();
		}

		if (getFrmDate() != null && getToDate() != null && !getFrmDate().equalsIgnoreCase("NULL") && !getToDate().equalsIgnoreCase("NULL")) {

		} else {
			setFrmDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, DATE_FORMAT));
			setToDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, DATE_FORMAT));
		}
 
//		System.out.println("=========>> 1 ");
//		System.out.println("=========>> otherProId " + otherProId + " proId ===>> " + proId);
		tasklist = new FillTask(request).fillTaskByMultiProjects(CF, getProId(), uF.parseToInt(getStrResourceId()));
		
		fillTaskRows();

		getProjectManagerList();
		getApprovalTaskStatus();
		getData();

		return SUCCESS;
	}


	
	public void saveTaskData() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		try {

			con = db.makeConnection(con);
			
			for (int i = 0; getTaskId() != null && i < getTaskId().length && uF.parseToInt(getStrEmpId()) > 0; i++) {

				if (uF.parseToInt(getTaskId()[i]) > 0 && uF.parseToDouble(getStrTime()[i]) > 0) {
					pst = con.prepareStatement("update task_activity set is_billable=?, task_location=?, billable_hrs=? where task_id=?");
					
					pst.setBoolean(1, uF.parseToBoolean(strBillableYesNoT[i]));
					pst.setString(2,((strTaskOnOffSiteT[i].equalsIgnoreCase("1")) ? "ONS": "OFS"));
					pst.setDouble(3, uF.parseToBoolean(strBillableYesNoT[i]) ? uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrBillableTime()[i])) : 0.0d);
					pst.setInt(4, uF.parseToInt(getTaskId()[i]));
					pst.execute();
					pst.close();
					session.setAttribute(MESSAGE, SUCCESSM + "Your billable hrs has been updated successfully." + END);

				} else if (uF.parseToDouble(getStrTime()[i]) > 0 && strUserType != null && strUserType.equalsIgnoreCase(EMPLOYEE) && getStrEmpId().equals(strSessionEmpId)) {
					
				} else if (uF.parseToDouble(getStrTime()[i]) > 0 && strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ADMIN))) {
					
				}
			}
			setTaskId(null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void saveTypeData1() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			List<String> alResources = new ArrayList<String>();
			List<String> alActivityIds = new ArrayList<String>();
//			System.out.println("checkTaskId ===>> " + checkTaskId);
			
			for(int i=0; checkTaskId != null && i<checkTaskId.length; i++) {
				String[] strResourceAndActId = checkTaskId[i].split(":_:");
				if(!alResources.contains(strResourceAndActId[0])) {
					alResources.add(strResourceAndActId[0]);
				}
				if(!alActivityIds.contains(strResourceAndActId[1])) {
					alActivityIds.add(strResourceAndActId[1]);
				}
			}
			pst = con.prepareStatement("select pf.*,p.pro_id,actual_calculation_type from projectmntnc p, projectmntnc_frequency pf " +
				"where p.billing_type != 'F' and p.pro_id = pf.pro_id and pf.pro_freq_id=?");
			pst.setInt(1, uF.parseToInt(getProFreqId()));
			rs = pst.executeQuery();
			Map<String, String> hmProjectData = new HashMap<String, String>();
			while(rs.next()) {
				hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
				hmProjectData.put("PRO_FREQ_NAME", rs.getString("pro_freq_name"));
				hmProjectData.put("PRO_FREQ_START_DATE", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_FREQ_END_DATE", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
		
			Map<String, String> hmEmpTaskActivityDate = new LinkedHashMap<String, String>();
			pst = con.prepareStatement("select * from task_activity where task_date between ? and ? and activity_id in (select task_id from " +
				"activity_info where pro_id=?) order by task_date");
//			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(1, uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			while (rs.next()) {
//				String activity = rs.getString("activity") != null && !rs.getString("activity").equals("") ? rs.getString("activity") : "0";
//				String task_data = uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT) + "::_::" + rs.getString("emp_id")+":_:"+rs.getString("activity_id") + "::_::" + rs.getString("emp_id")+":_:"+activity;
				String task_data = rs.getString("emp_id")+":_:"+rs.getString("activity_id");
				hmEmpTaskActivityDate.put(rs.getString("task_id"), task_data);
			}
			rs.close();
			pst.close();

			// System.out.println("getUnlock()=====>"+getUnlock());
//			if (getType() != null && getType().equalsIgnoreCase("approve")) {
								
				Iterator<String> it = hmEmpTaskActivityDate.keySet().iterator();
				
//				System.out.println("alResources ===>> " + alResources +" alActivityIds ===>> " + alActivityIds);
				while (it.hasNext()) {
					String task_id = (String) it.next(); // task_date
					
					String[] tempData = hmEmpTaskActivityDate.get(task_id).split(":_:");
					String resourceId = tempData[0];
					String activityId = tempData[1];
					
					if (alResources.contains(resourceId) && alActivityIds.contains(activityId)) {
//						System.out.println("resourceId ===>> " + resourceId +" activityId ===>> " + activityId);
						if (uF.parseToBoolean(CF.getIsWorkFlow())) {
							
							boolean taskApproveFlag = false;
							StringBuilder sbQuery = new StringBuilder();
							if(getType() != null && getType().equals("approveAndSendToCustomer")) {
								sbQuery.append("select is_billable_approved from task_activity where task_id=? and is_approved=2 and is_billable_approved=1");
							} else if(getType() != null && getType().equals("approveForBilling")) {
								sbQuery.append("select is_billable_approved from task_activity where task_id=? and is_approved=2 and is_billable_approved=2");
							}
							pst = con.prepareStatement(sbQuery.toString());
//							pst.setInt(1, uF.parseToInt(getStrEmpId()));
							pst.setInt(1, uF.parseToInt(task_id));
							rs = pst.executeQuery();
//							System.out.println("pst ===>> " + pst);
							while(rs.next()) {
								taskApproveFlag = true;
							}
							rs.close();
							pst.close();
							
//							System.out.println("taskApproveFlag ........." + taskApproveFlag);
							if(!taskApproveFlag) {
								pst = con.prepareStatement("update task_activity set is_approved = ?, billable_approved_by=?, is_billable_approved=?, billable_approve_date=? where task_id=?"); // and is_billable_approved=0 and is_approved=1
								pst.setInt(1, 2);
								pst.setInt(2, uF.parseToInt((String) session.getAttribute(EMPID)));
								if(getType() != null && getType().equals("approveAndSendToCustomer")) {
									pst.setInt(3, 1);
								} else if(getType() != null && getType().equals("approveForBilling")) {
									pst.setInt(3, 2);
								}
								pst.setDate(4,uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(5, uF.parseToInt(task_id));
								pst.executeUpdate();
//								System.out.println("pst ===>> " + pst);
								pst.close();
							}
						} else {
							if (strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))) {
								boolean taskApproveFlag = false;
								StringBuilder sbQuery = new StringBuilder();
								if(getType() != null && getType().equals("approveAndSendToCustomer")) {
									sbQuery.append("select is_billable_approved from task_activity where task_id=? and is_approved=2 and is_billable_approved=1");
								} else if(getType() != null && getType().equals("approveForBilling")) {
									sbQuery.append("select is_billable_approved from task_activity where task_id=? and is_approved=2 and is_billable_approved=2");
								}
								pst = con.prepareStatement(sbQuery.toString());
	//							pst.setInt(1, uF.parseToInt(getStrEmpId()));
								pst.setInt(1, uF.parseToInt(task_id));
								rs = pst.executeQuery();
								while(rs.next()) {
									taskApproveFlag = true;
								}
								rs.close();
								pst.close();
								
								if(!taskApproveFlag) {
									pst = con.prepareStatement("update task_activity set is_approved = ?, billable_approved_by=?, is_billable_approved=?, billable_approve_date=? where task_id=?"); // and is_billable_approved=0 and is_approved=1
									pst.setInt(1, 2);
									pst.setInt(2, uF.parseToInt((String) session.getAttribute(EMPID)));
									if(getType() != null && getType().equals("approveAndSendToCustomer")) {
										pst.setInt(3, 1);
									} else if(getType() != null && getType().equals("approveForBilling")) {
										pst.setInt(3, 2);
									}
									pst.setDate(4,uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(5, uF.parseToInt(task_id));
									pst.executeUpdate();
//									System.out.println("pst ===>> " + pst);
									pst.close();
								}
							}
						}
					}
					//
				}
				
				if(getType() != null && getType().equals("approveAndSendToCustomer")) {
					String strDomain = request.getServerName().split("\\.")[0];
					Notifications nF = new Notifications(N_TIMESHEET_SUBMITED_TO_CUST, CF); 
					nF.setDomain(strDomain);
					
					nF.request = request;
					nF.setStrOrgId((String)session.getAttribute(ORGID));
					nF.setEmailTemplate(true);
		
					nF.setStrEmpId(strSessionEmpId);
					pst = con.prepareStatement("select * from client_poc where poc_id = ?");
					pst.setInt(1, uF.parseToInt(hmProjectData.get("PRO_CUST_SPOC_ID")));
					rs = pst.executeQuery();
					boolean flg=false;
					while(rs.next()) {
						nF.setStrCustFName(rs.getString("contact_fname"));
						nF.setStrCustLName(rs.getString("contact_lname"));
						nF.setStrEmpMobileNo(rs.getString("contact_number"));
						if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
							nF.setStrEmpEmail(rs.getString("contact_email"));
							nF.setStrEmailTo(rs.getString("contact_email"));
						}
						flg = true;
					}
					rs.close();
					pst.close();
					
					if(flg) {
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrProjectFreqName(hmProjectData.get("PRO_NAME")+" ("+hmProjectData.get("PRO_FREQ_NAME")+")");
						nF.setStrFromDate(hmProjectData.get("PRO_FREQ_START_DATE"));
						nF.setStrToDate(hmProjectData.get("PRO_FREQ_END_DATE"));
						nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
						nF.sendNotifications(); 
					}
				}
				
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void customerApproveForBilling() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select pf.*,p.pro_id,actual_calculation_type from projectmntnc p, projectmntnc_frequency pf " +
				"where p.billing_type != 'F' and p.pro_id = pf.pro_id and pf.pro_freq_id=?");
			pst.setInt(1, uF.parseToInt(getProFreqId()));
			rs = pst.executeQuery();
			Map<String, String> hmProjectData = new HashMap<String, String>();
			while(rs.next()) {
				hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
				hmProjectData.put("PRO_FREQ_START_DATE", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_FREQ_END_DATE", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
		
			Map<String, String> hmEmpTaskActivityDate = new LinkedHashMap<String, String>();
			pst = con.prepareStatement("select * from task_activity where task_date between ? and ? and activity_id in (select task_id from " +
				"activity_info where pro_id=?) order by task_date");
//			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(1, uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			while (rs.next()) {
//				String activity = rs.getString("activity") != null && !rs.getString("activity").equals("") ? rs.getString("activity") : "0";
//				String task_data = uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT) + "::_::" + rs.getString("emp_id")+":_:"+rs.getString("activity_id") + "::_::" + rs.getString("emp_id")+":_:"+activity;
				String task_data = rs.getString("emp_id")+":_:"+rs.getString("activity_id");
				hmEmpTaskActivityDate.put(rs.getString("task_id"), task_data);
			}
			rs.close();
			pst.close();

				Iterator<String> it = hmEmpTaskActivityDate.keySet().iterator();
				
//				System.out.println("alResources ===>> " + alResources +" alActivityIds ===>> " + alActivityIds);
				while (it.hasNext()) {
					String task_id = (String) it.next(); // task_date
					
					boolean taskApproveFlag = false;
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("select is_billable_approved from task_activity where task_id=? and is_approved=2 and is_billable_approved=1");
					pst = con.prepareStatement(sbQuery.toString());
//							pst.setInt(1, uF.parseToInt(getStrEmpId()));
					pst.setInt(1, uF.parseToInt(task_id));
					rs = pst.executeQuery();
					while(rs.next()) {
						taskApproveFlag = true;
					}
					rs.close();
					pst.close();
					
					if(taskApproveFlag) {
						pst = con.prepareStatement("update task_activity set is_approved = ?, billable_approved_by=?, is_billable_approved=?, billable_approve_date=? where task_id=?"); // and is_billable_approved=0 and is_approved=1
						pst.setInt(1, 2);
						pst.setInt(2, uF.parseToInt((String) session.getAttribute(EMPID)));
						pst.setInt(3, 2);
						pst.setDate(4,uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(5, uF.parseToInt(task_id));
						pst.executeUpdate();
//						System.out.println("pst ===>> " + pst);
						pst.close();
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	private void getApprovalTaskStatus() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select pf.*,p.pro_id,actual_calculation_type from projectmntnc p, projectmntnc_frequency pf " +
				"where p.pro_id = pf.pro_id and pf.pro_freq_id=?");
			pst.setInt(1, uF.parseToInt(getProFreqId()));
			rs = pst.executeQuery();
			Map<String, String> hmProjectData = new HashMap<String, String>();
			while(rs.next()) {
				hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
				hmProjectData.put("PRO_FREQ_START_DATE", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_FREQ_END_DATE", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select activity_id,emp_id,is_approved from task_activity where (is_approved=1 or is_approved=2) and is_billable_approved=0 " +
				"and task_date  between ? and ? and activity_id in (select task_id from activity_info where pro_id=?) order by emp_id");
			pst.setDate(1, uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			Map<String, String> hmCheckTaskStatus = new HashMap<String, String>();
			while (rs.next()) {
				hmCheckTaskStatus.put(rs.getString("emp_id")+"_"+rs.getString("activity_id"), rs.getString("is_approved"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmCheckTaskStatus ===>> " + hmCheckTaskStatus);
			request.setAttribute("hmCheckTaskStatus", hmCheckTaskStatus);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	private void getProjectManagerList() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			pst = con.prepareStatement("select pf.*,p.pro_id,actual_calculation_type from projectmntnc p, projectmntnc_frequency pf " +
				"where p.pro_id = pf.pro_id and pf.pro_freq_id=?");
			pst.setInt(1, uF.parseToInt(getProFreqId()));
			rst = pst.executeQuery();
			Map<String, String> hmProjectData = new HashMap<String, String>();
			while(rst.next()) {
				hmProjectData = CF.getProjectDetailsByProId(con, rst.getString("pro_id"));
				hmProjectData.put("PRO_FREQ_START_DATE", uF.getDateFormat(rst.getString("freq_start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_FREQ_END_DATE", uF.getDateFormat(rst.getString("freq_end_date"), DBDATE, DATE_FORMAT));
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select wfd.emp_id,wfd.approve_date,ta.submited_date from task_activity ta, work_flow_details wfd " +
					"where ta.task_id = wfd.effective_id and effective_type='"+WORK_FLOW_TIMESHEET+"' and task_date between ? and ? and ta.is_approved > 0 " +
					"and activity_id in (select task_id from activity_info where pro_id=?) group by wfd.emp_id, wfd.approve_date, ta.submited_date ");
//			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(1, uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getProId()));
//			System.out.println("pst ===>> " + pst);
			rst = pst.executeQuery();
			List<List<String>> mOuterList = new ArrayList<List<String>>();
			while (rst.next()) {
				List<String> mInnerList = new ArrayList<String>();
				mInnerList.add(hmEmpName.get(rst.getString("emp_id")));
				mInnerList.add(rst.getString("emp_id"));
				mInnerList.add(uF.getDateFormat(rst.getString("submited_date"), DBDATE, DATE_FORMAT));
				
				if(rst.getString("approve_date") != null && !rst.getString("approve_date").equals("")) {
					mInnerList.add(uF.getDateFormat(rst.getString("approve_date"), DBDATE, DATE_FORMAT));
				} else {
					String gHrApproveDate = getGlobalHRApproveDate(con, getProId(), rst.getString("submited_date"), hmProjectData.get("PRO_FREQ_START_DATE"), hmProjectData.get("PRO_FREQ_END_DATE"));
					mInnerList.add(gHrApproveDate);
				}
				
				mInnerList.add(uF.getDateFormat(rst.getString("approve_date"), DBDATE, DATE_FORMAT));

				mOuterList.add(mInnerList);
			}
			rst.close();
			pst.close();
			
			request.setAttribute("mOuterList", mOuterList);

			pst = con.prepareStatement("select unblock_by from task_activity ta where emp_id=? and task_date between ? and ? "
				+ " and is_approved=0 and unblock_by is not null group by unblock_by");
			pst.setInt(1, uF.parseToInt(getStrResourceId()));
			pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT));
			rst = pst.executeQuery();
			List<String> unlockList = new ArrayList<String>();
			while (rst.next()) {
				unlockList.add(rst.getString("unblock_by"));
			}
			rst.close();
			pst.close();
			// System.out.println("unlockList====>"+unlockList);
			request.setAttribute("unlockList", unlockList); 

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private String getGlobalHRApproveDate(Connection con, String proId, String submitedDate, String stDate, String endDate) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		String approvedDate = "-";
		UtilityFunctions uF = new UtilityFunctions();
		try {
			pst = con.prepareStatement("select approved_date from task_activity where activity_id in (select task_id from activity_info where pro_id=?) and task_date between ? and ? and submited_date=?"); // and approved_by=?
			pst.setInt(1, uF.parseToInt(proId));
//			pst.setInt(2, uF.parseToInt(paycycleId));
			pst.setDate(2, uF.getDateFormat(stDate, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(endDate, DATE_FORMAT));
//			pst.setInt(4, uF.parseToInt(approverId));
			pst.setDate(4, uF.getDateFormat(submitedDate, DBDATE));
			rs = pst.executeQuery();
			while (rs.next()) {
				approvedDate = uF.getDateFormat(rs.getString("approved_date"), DBDATE, DATE_FORMAT) ;
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return approvedDate;
	}
	

	String[] statusTaskId;
	String[] status;

	String[] taskId;
	String[] strDate;
	String[] strTask;
	String[] strTime;
	String[] strBillableTime;
	String[] taskDescription;
	String[] strTaskOnOffSite;
	String[] strTaskOnOffSiteT;
	String[] strBillableYesNo;
	String[] strBillableYesNoT;

	
	public void fillTaskRows() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
//		System.out.println("=========>> 1 fillTaskRow ");
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select pf.*,p.pro_id,actual_calculation_type from projectmntnc p, projectmntnc_frequency pf " +
				"where p.pro_id = pf.pro_id and pf.pro_freq_id=?");
			pst.setInt(1, uF.parseToInt(getProFreqId()));
			rs = pst.executeQuery();
			Map<String, String> hmProjectData = new HashMap<String, String>();
			while(rs.next()) {
				hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
				hmProjectData.put("PRO_FREQ_START_DATE", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_FREQ_END_DATE", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
		
			Map<String, String> hmProjectResourceMap = new HashMap<String, String>();
			Map<String, String> hmProjectResourceWLocationMap = new HashMap<String, String>();
			StringBuilder projectResources = new StringBuilder();
			pst = con.prepareStatement("select resource_ids from activity_info where pro_id = ? ");
			pst.setInt(1, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				projectResources.append(rs.getString("resource_ids"));
			}
			rs.close();
			pst.close();
			
			List<String> alProResources = new ArrayList<String>();
			List<String> pResourceList = Arrays.asList(projectResources.toString().split(","));
			for(int a=0; pResourceList != null && a<pResourceList.size(); a++) {
				if(pResourceList.get(a) != null && !pResourceList.get(a).equals("")) {
					if(!alProResources.contains(pResourceList.get(a))) {
						alProResources.add(pResourceList.get(a));
					}
					hmProjectResourceWLocationMap.put(pResourceList.get(a), CF.getEmpWlocationId(con, uF, pResourceList.get(a)));
					hmProjectResourceMap.put(pResourceList.get(a), CF.getEmpNameMapByEmpId(con, pResourceList.get(a)));
				}
			}
			
//			System.out.println("alProResources ===>> " + alProResources);
//			System.out.println("hmProjectResourceMap ===>> " + hmProjectResourceMap);
			request.setAttribute("alProResources", alProResources);
			
			Map<String, Map<String, String>> hmLeaveDays = new HashMap<String, Map<String, String>>();
			Map hmLeavesColour = new HashMap();
			CF.getLeavesColour(con, hmLeavesColour);
			
			Map<String, String> hmLeaveCode = new HashMap<String, String>();
			
			hmLeaveDays = getLeaveDetails(hmProjectData.get("PRO_FREQ_START_DATE"), hmProjectData.get("PRO_FREQ_END_DATE"), uF, hmLeaveCode);

			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekendMap = CF.getWeekEndDateList(con, hmProjectData.get("PRO_FREQ_START_DATE"), hmProjectData.get("PRO_FREQ_END_DATE"), CF, uF, hmWeekEndHalfDates, null);
			String strWLocationId = hmEmpWLocation.get(getStrResourceId()); 
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con, uF, CF, hmProjectData.get("PRO_FREQ_START_DATE"), hmProjectData.get("PRO_FREQ_END_DATE"), alEmpCheckRosterWeektype, hmRosterWeekEndDates, hmWeekendMap, hmEmpLevelMap, hmEmpWLocation, hmWeekEndHalfDates);
			

			Map hmHolidays = new HashMap();
			Map hmHolidayDates = new HashMap();
			CF.getHolidayList(con,request, hmProjectData.get("PRO_FREQ_START_DATE"), hmProjectData.get("PRO_FREQ_END_DATE"), CF,hmHolidayDates, hmHolidays, true);

//			Map hmLeaveConstant = new HashMap();
//			pst = con.prepareStatement("select * from leave_application_register where leave_type_id in (select leave_type_id from emp_leave_type where is_constant_balance = true) and _date between ? and ? and emp_id =?");
//			pst.setDate(1, uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT));
//			pst.setInt(3, uF.parseToInt(getStrEmpId()));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				hmLeaveConstant.put(uF.getDateFormat(rs.getString("_date"), DBDATE,CF.getStrReportDateFormat()),uF.getDateFormat(rs.getString("_date"), DBDATE,CF.getStrReportDateFormat()));
//			}
//			rs.close();
//			pst.close();

			if (uF.getDateFormat(getFrmDate(), DATE_FORMAT).before(uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT)) || uF.getDateFormat(getFrmDate(), DATE_FORMAT).after(uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT))) {
				setFrmDate(hmProjectData.get("PRO_FREQ_START_DATE"));
			}

			if (uF.getDateFormat(getToDate(), DATE_FORMAT).before(uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT)) || uF.getDateFormat(getToDate(), DATE_FORMAT).after(uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT))) {
				setToDate(hmProjectData.get("PRO_FREQ_END_DATE"));
			}

			nDateCount = uF.parseToInt(uF.dateDifference(getFrmDate(),DATE_FORMAT, getToDate(), DATE_FORMAT,CF.getStrTimeZone()));
			
//			pst = con.prepareStatement("select sum(a.actual_hrs) as actual_hrs, sum(billable_hrs) as billable_hrs,ai.parent_task_id,a.task_date," +
//				"a.task_location,ai.pro_id from (select sum(actual_hrs) as actual_hrs, sum(billable_hrs) as billable_hrs, ta.activity_id," +
//				"ta.task_location,ta.task_date from task_activity ta where ta.emp_id = ? and task_date between ? and ? and ta.activity_id in (" +
//				"select task_id from activity_info where resource_ids like '%,"+getStrEmpId()+",%' and parent_task_id in (select task_id from " +
//				"activity_info where resource_ids like '%,"+getStrEmpId()+",%')) group by ta.activity_id,ta.task_date,ta.task_location) as a, " +
//				"activity_info ai where a.activity_id = ai.task_id group by ai.pro_id,ai.parent_task_id,a.task_date, a.task_location order by ai.parent_task_id");
			pst = con.prepareStatement("select sum(a.actual_hrs) as actual_hrs, sum(billable_hrs) as billable_hrs,ai.parent_task_id,a.task_date," +
				" a.task_location,a.emp_id from (select sum(actual_hrs) as actual_hrs, sum(billable_hrs) as billable_hrs, ta.activity_id," +
				" ta.task_location,ta.task_date, ta.emp_id from task_activity ta where task_date between ? and ? and (is_approved = 1 or is_approved = 2) and ta.activity_id in (" +
				" select task_id from activity_info where pro_id = ? and parent_task_id in (select task_id from activity_info where pro_id = ?))" +
				" group by ta.emp_id,ta.activity_id,ta.task_date,ta.task_location) as a, activity_info ai where a.activity_id = ai.task_id and " +
				" ai.pro_id = ? group by a.emp_id,ai.parent_task_id,a.task_date, a.task_location order by ai.parent_task_id");
//			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(1, uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getProId()));
			pst.setInt(4, uF.parseToInt(getProId()));
			pst.setInt(5, uF.parseToInt(getProId()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
		
			Map<String, String> hmDateT = new HashMap<String, String>();
			Map<String, String> hmDateBillableHrsT = new HashMap<String, String>();
			Map<String, String> hmTaskIsBillableT = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmployeeT = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmployeeBillableHrsT = new HashMap<String, Map<String, String>>();
			Map<String, String> hmTasksT = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmployeeTasksT = new LinkedHashMap<String, Map<String, String>>();
		
			Map<String, String> hmEmployeeCountT = new HashMap<String, String>();
			Map<String, String> hmEmployeeBillCountT = new HashMap<String, String>();
		
			int nCountT = 0;
			int nBillCountT = 0;
			double dblTotalHrsT = 0;
			double dblTotalBillableHrsT = 0;
			boolean isBillableT = false;
			String strActivityIdNewT = null;
			String strActivityIdOldT = null;
			String strEmpIdNewT = null;
			String strEmpIdOldT = null;
			while (rs.next()) {
		
				strActivityIdNewT = rs.getString("parent_task_id");
				strEmpIdNewT = rs.getString("emp_id");
				
				if (strEmpIdNewT != null && !strEmpIdNewT.equalsIgnoreCase(strEmpIdOldT)) {
					nCountT = 0;
					nBillCountT = 0;
					isBillableT = false;
					strActivityIdOldT = null;
				} else if (strEmpIdNewT == null && strEmpIdOldT != null) {
					nCountT = 0;
					nBillCountT = 0;
					isBillableT = false;
					strActivityIdOldT = null;
				}
				
				if (strActivityIdNewT != null && !strActivityIdNewT.equalsIgnoreCase(strActivityIdOldT)) {
					dblTotalHrsT = 0;
					dblTotalBillableHrsT = 0;
					hmDateT = new HashMap<String, String>();
					hmDateBillableHrsT = new HashMap<String, String>();
					hmTasksT = new HashMap<String, String>();
					nCountT++;
				}
		
				if (strActivityIdNewT != null && !strActivityIdNewT.equalsIgnoreCase(strActivityIdOldT) && uF.parseToDouble(rs.getString("billable_hrs")) > 0) {
					nBillCountT++;
					isBillableT = true;
				} else if (!isBillableT && strActivityIdNewT != null && strActivityIdNewT.equalsIgnoreCase(strActivityIdOldT) && uF.parseToDouble(rs.getString("billable_hrs")) > 0) {
					nBillCountT++;
					isBillableT = true;
				}
				
				double dblHrs = uF.parseToDouble((String) hmDateT.get(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_" + rs.getString("task_location")));
				dblTotalHrsT = dblHrs+ uF.parseToDouble(rs.getString("actual_hrs"));
		
				hmDateT.put(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_"+ rs.getString("task_location"), uF.formatIntoTwoDecimalWithOutComma(dblTotalHrsT));
				
				double dblBillableHrs = uF.parseToDouble((String) hmDateBillableHrsT.get(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_" + rs.getString("task_location")));
				dblTotalBillableHrsT = dblBillableHrs + uF.parseToDouble(rs.getString("billable_hrs"));
		
				hmDateBillableHrsT.put(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_"+ rs.getString("task_location"), uF.formatIntoTwoDecimalWithOutComma(dblTotalBillableHrsT));
				
				hmEmployeeT.put(strEmpIdNewT+"_"+strActivityIdNewT, hmDateT);
		
				hmEmployeeBillableHrsT.put(strEmpIdNewT+"_"+strActivityIdNewT, hmDateBillableHrsT);
				
				hmTaskIsBillableT.put(strEmpIdNewT+"_"+strActivityIdNewT, isBillableT+"");
				
				hmTasksT.put(strEmpIdNewT+"_"+strActivityIdNewT + "_T", CF.getProjectTaskNameByTaskId(con, uF, rs.getString("parent_task_id")));
					
				hmTasksT.put(strEmpIdNewT+"_"+strActivityIdNewT + "_E", rs.getString("emp_id"));
				hmEmployeeTasksT.put(strEmpIdNewT+"_"+strActivityIdNewT, hmTasksT);
		
				hmEmployeeCountT.put(rs.getString("emp_id"), nCountT + "");
				hmEmployeeBillCountT.put(rs.getString("emp_id"), nBillCountT + "");
				
		//		System.out.println("hmProjectBillCount ===>>> " + hmProjectBillCount); 
				
				strActivityIdOldT = strActivityIdNewT;
				strEmpIdOldT = strEmpIdNewT;
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmProjectsT ===>> " + hmProjectsT);
//			System.out.println("hmEmployeeCountT ===>> " + hmEmployeeCountT);
//			System.out.println("hmEmployeeBillCountT ===>> " + hmEmployeeBillCountT);
			
			request.setAttribute("hmEmployeeT", hmEmployeeT);
			request.setAttribute("hmEmployeeBillableHrsT", hmEmployeeBillableHrsT);
			request.setAttribute("hmTaskIsBillableT", hmTaskIsBillableT);
			request.setAttribute("hmEmployeeTasksT", hmEmployeeTasksT);
			
			request.setAttribute("hmEmployeeCountT", hmEmployeeCountT);
			request.setAttribute("hmEmployeeBillCountT", hmEmployeeBillCountT);
			
			
			
			pst = con.prepareStatement("select actual_hrs, activity_id, ta.emp_id, task_date, activity_name, activity, task_location, billable_hrs, " +
				"is_billable, is_approved, ta.task_id, ta._comment,is_billable_approved from task_activity ta left join activity_info ai " +
				"on ta.activity_id = ai.task_id where ai.pro_id =? and task_date between ? and ? and (is_approved = 1 or is_approved = 2) order by ta.emp_id, activity_id desc, activity");
			pst.setInt(1, uF.parseToInt(getProId()));
			pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();

			Map<String, String> hmDate = new HashMap<String, String>();
			Map<String, String> hmDateHrsIsApproved = new HashMap<String, String>();
			Map<String, String> hmDateBillableHrsIsApproved = new HashMap<String, String>();
			Map<String, String> hmDateTaskId = new HashMap<String, String>();
			Map<String, String> hmDateBillableHrs = new HashMap<String, String>();
			Map<String, String> hmTaskIsBillable = new HashMap<String, String>();
			Map<String, String> hmTaskDescri = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmployee = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmployeeHrsIsApproved = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmployeeBillableHrsIsApproved = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmployeeTaskId = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmployeeBillableHrs = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmployeeTaskDescri = new HashMap<String, Map<String, String>>();

			Map<String, String> hmTasks = new HashMap<String, String>();
			Map hmEmployeeTasks = new LinkedHashMap();

			Map<String, String> hmEmployeeCount = new HashMap<String, String>();
			Map<String, String> hmEmployeeBillCount = new HashMap<String, String>();

			int nCount = 0;
			int nBillCount = 0;
			double dblTotalHrs = 0;
			double dblTotalBillableHrs = 0;
			boolean isBillable = false;
			String strActivityIdNew = null;
			String strActivityIdOld = null;
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			while (rs.next()) {

				strActivityIdNew = rs.getString("activity_id");
				strEmpIdNew = rs.getString("emp_id");
				if (uF.parseToInt(strActivityIdNew) == 0) {
					strActivityIdNew = rs.getString("activity");
				}

				if (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					nCount = 0;
					nBillCount = 0;
					strActivityIdOld = null;
				} else if (strEmpIdNew == null && strEmpIdOld != null) {
					nCount = 0;
					nBillCount = 0;
					strActivityIdOld = null;
				}
				
				if (strActivityIdNew != null && !strActivityIdNew.equalsIgnoreCase(strActivityIdOld)) {
					dblTotalHrs = 0;
					dblTotalBillableHrs = 0;
					isBillable = false;
					hmDate = new HashMap<String, String>();
					hmDateBillableHrs = new HashMap<String, String>();
					hmDateHrsIsApproved = new HashMap<String, String>();
					hmDateBillableHrsIsApproved = new HashMap<String, String>();
					hmDateTaskId = new HashMap<String, String>();
					hmTasks = new HashMap<String, String>();
					nCount++;
				}

				if(!isBillable) {
					isBillable = rs.getBoolean("is_billable");
					if(isBillable) {
						nBillCount++;
					}
				}
				
				double dblHrs = uF.parseToDouble((String) hmDate.get(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_" + rs.getString("task_location")));
				dblTotalHrs = dblHrs+ uF.parseToDouble(rs.getString("actual_hrs"));

				hmDate.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_"+ rs.getString("task_location"), uF.formatIntoTwoDecimalWithOutComma(dblTotalHrs));
				
				double dblBillableHrs = uF.parseToDouble((String) hmDateBillableHrs.get(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_" + rs.getString("task_location")));
				dblTotalBillableHrs = dblBillableHrs + uF.parseToDouble(rs.getString("billable_hrs"));

				hmDateBillableHrs.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_"+ rs.getString("task_location"), uF.formatIntoTwoDecimalWithOutComma(dblTotalBillableHrs));
				
				hmDateHrsIsApproved.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_"+ rs.getString("task_location"), rs.getString("is_approved"));
				
				hmDateBillableHrsIsApproved.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_"+ rs.getString("task_location"), rs.getString("is_billable_approved"));
				
				hmDateTaskId.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_"+ rs.getString("task_location"), rs.getString("task_id"));
				
				hmTaskDescri.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_"+ rs.getString("task_location"), rs.getString("_comment"));

				hmEmployee.put(strEmpIdNew+"_"+strActivityIdNew, hmDate);

				hmEmployeeBillableHrs.put(strEmpIdNew+"_"+strActivityIdNew, hmDateBillableHrs);
				
				hmEmployeeHrsIsApproved.put(strEmpIdNew+"_"+strActivityIdNew, hmDateHrsIsApproved);
				
				hmEmployeeBillableHrsIsApproved.put(strEmpIdNew+"_"+strActivityIdNew, hmDateBillableHrsIsApproved);
				
				hmEmployeeTaskDescri.put(strEmpIdNew+"_"+strActivityIdNew, hmTaskDescri);
				
				hmEmployeeTaskId.put(strEmpIdNew+"_"+strActivityIdNew, hmDateTaskId);
				
				hmTaskIsBillable.put(strEmpIdNew+"_"+strActivityIdNew, isBillable+"");
				
				if (uF.parseToInt(rs.getString("activity_id")) == 0) {
					hmTasks.put(strEmpIdNew+"_"+strActivityIdNew + "_T",rs.getString("activity"));
				} else {
					hmTasks.put(strEmpIdNew+"_"+strActivityIdNew + "_T",rs.getString("activity_name"));
				}

				hmTasks.put(strEmpIdNew+"_"+strActivityIdNew + "_E", rs.getString("emp_id"));
				hmEmployeeTasks.put(strEmpIdNew+"_"+strActivityIdNew, hmTasks);

				hmEmployeeCount.put(rs.getString("emp_id"), nCount + "");
				hmEmployeeBillCount.put(rs.getString("emp_id"), nBillCount + "");
				
//				System.out.println("hmProjectBillCount ===>>> " + hmProjectBillCount); 
				
				strActivityIdOld = strActivityIdNew;
				strEmpIdOld = strEmpIdNew;
			}
			
			rs.close();
			pst.close();
			
//			System.out.println("hmEmployeeBillableHrsIsApproved ===>>> " + hmEmployeeBillableHrsIsApproved);

			request.setAttribute("hmEmployee", hmEmployee);
			request.setAttribute("hmEmployeeBillableHrs", hmEmployeeBillableHrs);
			request.setAttribute("hmEmployeeHrsIsApproved", hmEmployeeHrsIsApproved);
			request.setAttribute("hmEmployeeBillableHrsIsApproved", hmEmployeeBillableHrsIsApproved);
			
			request.setAttribute("hmEmployeeTaskDescri", hmEmployeeTaskDescri);
			request.setAttribute("hmEmployeeTaskId", hmEmployeeTaskId);
			request.setAttribute("hmTaskIsBillable", hmTaskIsBillable);
			request.setAttribute("hmEmployeeTasks", hmEmployeeTasks);
			
			request.setAttribute("hmEmployeeCount", hmEmployeeCount);
			request.setAttribute("hmEmployeeBillCount", hmEmployeeBillCount);
			
			
			List<String> alTaskIds = new ArrayList<String>();
			
			pst = con.prepareStatement("select task_id, pro_id, activity_name, parent_task_id from activity_info ai where pro_id=?");
			pst.setInt(1, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			Map<String, List<String>> hmTaskAndSubTaskIds = new LinkedHashMap<String, List<String>>();
			List<String> alSubTaskIds = new ArrayList<String>();
//			List<String> alTaskIds = new ArrayList<String>();
			while(rs.next()) {
				if(rs.getInt("parent_task_id") > 0) {
					alSubTaskIds = hmTaskAndSubTaskIds.get(rs.getString("parent_task_id"));
					if(alSubTaskIds == null) alSubTaskIds = new ArrayList<String>();
					alSubTaskIds.add(rs.getString("task_id"));
					hmTaskAndSubTaskIds.put(rs.getString("parent_task_id"), alSubTaskIds);
				}
				if(rs.getInt("parent_task_id") == 0) {
					alTaskIds.add(rs.getString("task_id"));
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alTaskIds", alTaskIds);
			request.setAttribute("hmTaskAndSubTaskIds", hmTaskAndSubTaskIds);
			
//			System.out.println("alTaskIds ===>> " + alTaskIds);
//			System.out.println("hmTaskAndSubTaskIds ===>> " + hmTaskAndSubTaskIds);
			
//			System.out.println("=========>> 1 fillTaskRow getStrProject " + getStrProject());
//			System.out.println("=========>> 1 fillTaskRow otherProId " + otherProId);
			
//			if (uF.parseToInt(getStrActivity()) > 0) {
				// pst =
				// con.prepareStatement("select actual_hrs,activity_id, ai.pro_id, task_date, activity_name , ta.task_id, activity_id, pcmc.client_id from task_activity ta, activity_info ai, projectmntnc pcmc where  ta.activity_id = ai.task_id and pcmc.pro_id = ai.pro_id and task_date between ? and ? and ai.pro_id = ? and activity_id =?");
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select a.actual_hrs, a.is_billable, a.billable_hrs, a.pro_id, a.task_date, a.activity_name, a.task_id, a.activity_id," +
					" a.activity_description, a.task_location, client_id from (select actual_hrs,is_billable, billable_hrs,activity_id, ai.pro_id, " +
					"task_date, activity_name , ta.task_id, ta.activity_description, ta.task_location from task_activity ta left join activity_info ai " +
					"on ta.activity_id = ai.task_id  and ta.emp_id = ?) a left join projectmntnc pcmc on a.pro_id = pcmc.pro_id where task_date " +
					"between ? and ? and a.activity_id =? ");
				if(getProId() != null && uF.parseToInt(getProId())>0) {
					sbQuery.append(" and a.pro_id = "+uF.parseToInt(getProId())+"");
				}
				
//				pst = con.prepareStatement("select a.actual_hrs, a.is_billable, a.billable_hrs, a.pro_id, a.task_date, a.activity_name, a.task_id, a.activity_id, a.activity_description, a.task_location, client_id from (select actual_hrs,is_billable, billable_hrs,activity_id, ai.pro_id, task_date, activity_name , ta.task_id, ta.activity_description, ta.task_location from task_activity ta left join activity_info ai on ta.activity_id = ai.task_id  and ta.emp_id = ?) a left join projectmntnc pcmc on a.pro_id = pcmc.pro_id where task_date between ? and ? and a.pro_id = ? and a.activity_id =? ");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getStrResourceId()));
				pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(getStrActivity()));
//				System.out.println("pst uF.parseToInt(getStrActivity()) > 0 == 0 ===>>> " + pst);
				
			rs = pst.executeQuery();
			Map hmProjectDates = new HashMap();
			Map hmProjectDatesNaTask = new HashMap();
			List alProDates = new ArrayList();
			List<String> alProDatesNaTask = new ArrayList<String>();

			Map<String, String> hmTaskProjectId = new HashMap<String, String>();
			Map<String, String> hmTaskActivityId = new HashMap<String, String>();
			Map<String, String> hmTaskClientId = new HashMap<String, String>();
			Map<String, String> hmTaskHoursId = new HashMap<String, String>();
			Map<String, String> hmTaskBillableHoursId = new HashMap<String, String>();
			Map<String, String> hmTaskDescription = new HashMap<String, String>();
			Map<String, String> hmTaskOnSite = new HashMap<String, String>();
			Map<String, String> hmTaskIsBill = new HashMap<String, String>();

			String strDateNew = null;
			String strDateOld = null;
			while (rs.next()) {
				strDateNew = uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT);
				if (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld)) {
					alProDates = new ArrayList();
					alProDatesNaTask = new ArrayList<String>();
				}

				alProDates = (List) hmProjectDates.get(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT));
				if (alProDates == null)
					alProDates = new ArrayList();
				if(!alProDates.contains(rs.getString("task_id"))) {
					alProDates.add(rs.getString("task_id"));
				}
				hmProjectDates.put(uF.getDateFormat(rs.getString("task_date"),DBDATE, DATE_FORMAT), alProDates);
 
				alProDatesNaTask = (List<String>) hmProjectDatesNaTask.get(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT));
				if (alProDatesNaTask == null)
					alProDatesNaTask = new ArrayList<String>();
//				System.out.println("alProDatesNaTask before ===>>> " + alProDatesNaTask);
				if(!alProDatesNaTask.contains(rs.getString("task_id"))) {
					alProDatesNaTask.add(rs.getString("task_id"));
				}
//				System.out.println("alProDatesNaTask after ===>>> " + alProDatesNaTask);
				hmProjectDatesNaTask.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT),alProDatesNaTask);

				strDateOld = strDateNew;

				if (uF.parseToInt(proId) > 0 || uF.parseToInt(getStrActivity()) > 0) {
					hmTaskProjectId.put(rs.getString("task_id"), rs.getString("pro_id"));
				}

				if (uF.parseToInt(proId) > 0) {
					hmTaskClientId.put(rs.getString("task_id"), rs.getString("client_id"));
					hmTaskActivityId.put(rs.getString("task_id"), rs.getString("activity_id"));
				} else {
					hmTaskActivityId.put(rs.getString("task_id"), rs.getString("activity"));
				}

				hmTaskHoursId.put(rs.getString("task_id"), uF.getTotalTimeMinutes100To60(rs.getString("actual_hrs")));
				hmTaskBillableHoursId.put(rs.getString("task_id"), uF.getTotalTimeMinutes100To60(rs.getString("billable_hrs")));
				hmTaskDescription.put(rs.getString("task_id"), rs.getString("activity_description"));

				hmTaskOnSite.put(rs.getString("task_id"), rs.getString("task_location"));
				hmTaskIsBill.put(rs.getString("task_id"), rs.getString("is_billable"));
			}
			rs.close();
			pst.close();
			
			
			StringBuilder sbTasks = new StringBuilder();

			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(getFrmDate(),DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getFrmDate(),DATE_FORMAT, "yyyy")));

			StringBuilder sbTaskList = new StringBuilder();
			for (int i = 0; i < tasklist.size(); i++) {
				sbTaskList.append("<option value=\'" + tasklist.get(i).getTaskId() + "\'>" + tasklist.get(i).getTaskName() + "</option>");
			}

			request.setAttribute("strTaskList", sbTaskList.toString());
			int i=0;
//			System.out.println("nDateCount======>"+nDateCount);
//			System.out.println("joiningDate======>"+joiningDate);
			
			List<String> approvedDates = getTimesheetApprovedDates(con, uF, getFrmDate(), getToDate(), getStrResourceId());
			
			StringBuilder sbApproveDates = null;
			for(int ii=0; approvedDates!= null && !approvedDates.isEmpty() && ii<approvedDates.size(); ii++) {
				if(sbApproveDates == null) {
					sbApproveDates = new StringBuilder();
					sbApproveDates.append(approvedDates.get(ii));
				} else {
					sbApproveDates.append(", "+approvedDates.get(ii));
				}
			}
			
			if(approvedDates.size()>0 && getSubmit1() != null && nDateCount>0) {
				sbTasks.append("" + "<div id=\"approvedDateDIV\"><div id=\"row_task_00\" style=\"float:left; width:750px; padding-bottom:10px; color:red;\">");
				sbTasks.append("The following dates have already been approved. Dates: " + sbApproveDates.toString());
				sbTasks.append("</div>" + "</div>");
			}
			
			for (i = 0; getSubmit1() != null && i < nDateCount; i++) {
				String taskDate=uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,DATE_FORMAT);
				
//				System.out.println("taskDate ======> " + taskDate);
//				System.out.println("joiningDate ======> " + joiningDate);
				
				List<String> alTaskId = (List) hmProjectDates.get(uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));

				Set<String> weeklyOffSet= hmWeekendMap.get(strWLocationId);
				if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
				
				Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(getStrResourceId());
				if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
				
				if (alTaskId == null) {
					alTaskId = (List) hmProjectDatesNaTask.get(uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1)+ "/" + cal.get(Calendar.YEAR),DATE_FORMAT, DATE_FORMAT));
				}

				if (alTaskId == null) {
					alTaskId = new ArrayList<String>();
					alTaskId.add("0");
				}
				
//				System.out.println("alTaskId ========>> " + alTaskId);
//				System.out.println("tasklist.size() ========>> " + tasklist.size());
				
					for (int j = 0; j < alTaskId.size() && tasklist.size() > 0; j++) {
						String tdate = uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,DATE_FORMAT);
	
						if(approvedDates.contains(tdate)) {
							
						} else {
						double dblVal = uF.parseToDouble(hmTaskHoursId.get(alTaskId.get(j)));
						double dblBillableVal = uF.parseToDouble(hmTaskBillableHoursId.get(alTaskId.get(j)));
	//					System.out.println("date "+uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1)+ "/" + cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
						sbTasks.append("" + "<div id=\"" + uF.getDateFormat( cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" 
								+ cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT)+"_"+j+"\">" +
								"<div id=\"row_task_"+i+"_"+j+"\" style=\"float:left;width:750px;padding-bottom:10px;\">");
						
	//	******************************** task date textfield ********************************************
						sbTasks.append("<div style=\"float:left;width:80px;\"><input type=\"hidden\" name=\"taskId\" value=\"" + alTaskId.get(j)
								+ "\"><input type=\"text\" style=\"width:62px\" name=\"strDate\" id=\"strDate_"+i+"_"+j+"\" value=\"" 
								+ uF.getDateFormat( cal.get(Calendar.DATE) + "/"
								+ (cal.get(Calendar.MONTH) + 1) 
								+ "/" + cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT)
								+ "\" readonly=\"readonly\" ></div>");
		
	//	******************************** task list select box ********************************************				
						sbTasks.append("<div style=\"float:left;width:225px;\">"
								//+ "<input type=\"hidden\" name=\"strTask\" value=\"" + hmTaskActivityId.get(alTaskId.get(j))+ "\">"
								+ "<select name=\"strTask\" class=\"validateRequired\" disabled>");
						for (int c = 0; c < tasklist.size(); c++) {
							sbTasks.append("<option value=\'"
									+ tasklist.get(c).getTaskId()
									+ "\' "
									+ ((((String) tasklist.get(c).getTaskId()).equalsIgnoreCase(hmTaskActivityId.get(alTaskId.get(j)))) ? "selected": "") + ">"
									+ tasklist.get(c).getTaskName() + "</option>");
						}
						sbTasks.append("</select>" + "</div>");
						
	//	******************************** total hrs textfield ********************************************					
						sbTasks.append("<div style=\"float:left;width:120px;\"><input onblur=\"checkHours('"+i+"_"+j+"');\" type=\"text\" style=\"width:62px\" name=\"strTime\" id=\"strTime_"+i+"_"+j+"\" value=\""
								+ uF.formatIntoTwoDecimal(dblVal) + "\" onkeyup=\"checkAndAddBillableTime('"+i+"_"+j+"','"+i+"_"+j+"');\" readonly=\"readonly\">");
						// "<div style=\"float:left;width:150px;\"><input onblur=\"checkHours();\" type=\"text\" style=\"width:62px\" name=\"strTime\" value=\""+uF.showData((String)hmAttendance.get(uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR),
						// DATE_FORMAT, DATE_FORMAT)),
						// uF.formatIntoTwoDecimal(dblVal))+"\">");
	
						if (hmHolidayDates.containsKey(uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,CF.getStrReportDateFormat())+ "_" + (String) session.getAttribute(WLOCATIONID))) {
							sbTasks.append("<input type=\"hidden\" name=\"holiday\" value=\"H\"><span style=\"padding-left:10px\">H</span>");
						}
	//					if (hmWeekendMap.containsKey(uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,DATE_FORMAT)+ "_" + (String) session.getAttribute(WLOCATIONID))) {
	//						sbTasks.append("<input type=\"hidden\" name=\"weekend\" value=\"W/O\"><span style=\"padding-left:10px\">W/O</span>");
	//					}
						
						if(alEmpCheckRosterWeektype.contains(getStrResourceId())) {
							if(rosterWeeklyOffSet.contains(uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,DATE_FORMAT))) {
								sbTasks.append("<input type=\"hidden\" name=\"weekend\" value=\"W/O\"><span style=\"padding-left:10px\">W/O</span>");
							}
						} else if(weeklyOffSet.contains(uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,DATE_FORMAT))) {
							sbTasks.append("<input type=\"hidden\" name=\"weekend\" value=\"W/O\"><span style=\"padding-left:10px\">W/O</span>");
						}
						sbTasks.append("</div>");
						
	// ******************************** billable true false checkbox ********************************************
						sbTasks.append(((uF.parseToInt(alTaskId.get(j)) > 0) ?
								"<div style=\"float: left; width: 55px; text-align: center;\"><input type=\"hidden\" style=\"width: 30px;\" id=\"strBillableYesNoT_"+i+"_"+j+"\" name=\"strBillableYesNoT\" " +
								 ((uF.parseToBoolean(hmTaskIsBill.get(alTaskId.get(j)))) ? "value=\"1\"": "value=\"0\"" ) + "" + ">" +
								"<input type=\"checkbox\" style=\"width: 30px;\"  onchange=\"setBillableValue('"+i+"_"+j+"','"+i+"_"+j+"')\" id=\"strBillableYesNo_"+i+"_"+j+"\" name=\"strBillableYesNo\" value=\""
								+ alTaskId.get(j) + "\" " + ((uF.parseToBoolean(hmTaskIsBill.get(alTaskId.get(j)))) ? "checked" : "") + "></div>"
								: "<div style=\"float: left; width: 55px; text-align: center;\"><input type=\"hidden\" style=\"width: 30px;\" id=\"strBillableYesNoT_"+i+"_"+j+"\" name=\"strBillableYesNoT\" value=\"1\">" +
								"<input type=\"checkbox\" style=\"width: 30px;\" id=\"strBillableYesNo_"+i+"_"+j+"\" onchange=\"setBillableValue('"+i+"_"+j+"','"+i+"_"+j+"')\" name=\"strBillableYesNo\" value=\""
								+ alTaskId.get(j) + "\" checked></div>"));
						
	//	******************************** bill hrs textfield ********************************************				
						sbTasks.append("<div style=\"float:left;width:85px;\"><input onblur=\"checkBillHours('"+i+"_"+j+"');\" type=\"text\" style=\"width:62px\" name=\"strBillableTime\" id=\"strBillableTime_"+i+"_"+j+"\" value=\""
								+((uF.parseToBoolean(hmTaskIsBill.get(alTaskId.get(j)))) ? uF.formatIntoTwoDecimal(dblBillableVal) : "0") + "\"></div>");
						
	//	******************************** onsite true false checkbox ********************************************					
						sbTasks.append(((uF.parseToInt(alTaskId.get(j)) > 0) ?
								"<div style=\"float: left; width: 50px; text-align: center;\"><input type=\"hidden\" style=\"width: 30px;\" id=\"strTaskOnOffSiteT_"+i+"_"+j+"\" name=\"strTaskOnOffSiteT\" " +
								 (("ONS".equalsIgnoreCase(hmTaskOnSite.get(alTaskId.get(j)))) ? "value=\"1\"": "value=\"0\"" ) + "" + ">" +
								"<input type=\"checkbox\" style=\"width: 30px;\"  onchange=\"setValue('"+i+"_"+j+"')\" id=\"strTaskOnOffSite_"+i+"_"+j+"\" name=\"strTaskOnOffSite\" value=\""
								+ alTaskId.get(j) + "\" " + (("ONS".equalsIgnoreCase(hmTaskOnSite.get(alTaskId.get(j)))) ? "checked" : "") + "></div>"
								: "<div style=\"float: left; width: 50px; text-align: center;\"><input type=\"hidden\" style=\"width: 30px;\" id=\"strTaskOnOffSiteT_"+i+"_"+j+"\" name=\"strTaskOnOffSiteT\" value=\"1\">" +
								"<input type=\"checkbox\" style=\"width: 30px;\" id=\"strTaskOnOffSite_"+i+"_"+j+"\" onchange=\"setValue('"+i+"_"+j+"')\" name=\"strTaskOnOffSite\" value=\""
								+ alTaskId.get(j) + "\" checked></div>"));
						
						sbTasks.append("</div>" + "</div>");
					}
				
				}
				cal.add(Calendar.DATE, 1);
				
			}

			StringBuilder sbNoTasks = new StringBuilder(); 
			if (uF.parseToInt(getStrResourceId())>0 && tasklist.size() == 0) {
				
				sbNoTasks.append("<div style=\"float: left; width: 700px; padding: 2px;\">"
						+ "<div class=\"msg nodata\"><span>No project selected</span></div>"
						+ "</div>");

			}
			
			
			String currDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
			
			request.setAttribute("currDate", currDate);
			
			request.setAttribute("sbNoTasks", sbNoTasks.toString());
			request.setAttribute("sbTasks", sbTasks.toString());
			request.setAttribute("hmProjectResourceMap", hmProjectResourceMap);
			request.setAttribute("hmProjectResourceWLocationMap", hmProjectResourceWLocationMap);
			
			request.setAttribute("i", i);
			
			int nDateDiff = uF.parseToInt(uF.dateDifference(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT, hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT, CF.getStrTimeZone()));

			cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT, "yyyy")));
			List<String> alDates = new ArrayList<String>();
			for (i = 0; i < nDateDiff; i++) {
				alDates.add(uF.getDateFormat(cal.get(Calendar.DATE) + "/"
					+ (cal.get(Calendar.MONTH) + 1) + "/"
					+ cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
				cal.add(Calendar.DATE, 1);

				if (uF.getDateFormat(cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT).equalsIgnoreCase(hmProjectData.get("PRO_FREQ_END_DATE"))) {
					alDates.add(uF.getDateFormat( cal.get(Calendar.DATE) + "/"
					+ (cal.get(Calendar.MONTH) + 1) + "/"
					+ cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
					break;
				}
			}

			request.setAttribute("hmLeaveDays", hmLeaveDays);
			request.setAttribute("hmWeekendMap", hmWeekendMap);
			request.setAttribute("strWLocationId", strWLocationId);
			request.setAttribute("hmHolidayDates", hmHolidayDates);
			request.setAttribute("hmLeavesColour", hmLeavesColour);
//			request.setAttribute("hmLeaveConstant", hmLeaveConstant);

			request.setAttribute("timesheet_title", "Timesheet details from " + hmProjectData.get("PRO_FREQ_START_DATE") + " to " + hmProjectData.get("PRO_FREQ_END_DATE"));
			request.setAttribute("PRO_PERIOD", uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT, CF.getStrReportDateFormat()) + " to " + uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT, CF.getStrReportDateFormat()));
			request.setAttribute("alDates", alDates);

			request.setAttribute("strEmpId", getStrEmpId());

//			request.setAttribute("sbTaskStatus", sbTaskStatus);
			request.setAttribute("hmLeaveCode", hmLeaveCode);
			
			request.setAttribute("hmRosterWeekEndDates", hmRosterWeekEndDates);
			request.setAttribute("alEmpCheckRosterWeektype", alEmpCheckRosterWeektype);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	
	private List<String> getTimesheetApprovedDates(Connection con, UtilityFunctions uF, String strDate1, String strDate2, String strEmpId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> approvedDateList = new ArrayList<String>();
		try {
			pst=con.prepareStatement("select task_date from task_activity where task_date between ? and ? and emp_id = ? and is_approved = 2 and is_billable_approved = 1 group by task_date order by task_date");
			pst.setDate(1, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDate2, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));
//			System.out.println("pst=====>"+pst);
			rs=pst.executeQuery();
			
			while(rs.next()) {
				approvedDateList.add(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return approvedDateList;
	}

	
	
	private Map<String, Map<String, String>> getLeaveDetails(String strDate1, String strDate2, UtilityFunctions uF, Map<String, String> hmLeavesCode) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, Map<String, String>> getMap = new HashMap<String, Map<String, String>>();
		try{
			con=db.makeConnection(con);
			pst=con.prepareStatement("select lar.*,lt.leave_type_code from leave_application_register lar,leave_type lt where lar.leave_type_id=lt.leave_type_id and is_modify = false and _date between ? and ? order by emp_id");
			pst.setDate(1, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDate2, DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs=pst.executeQuery();
			
			while(rs.next()) {
				Map<String, String> a = getMap.get(rs.getString("emp_id"));
				if(a == null) a = new HashMap<String, String>(); 
				
//				a.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("leave_type_code"));
				a.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("leave_type_id"));
				getMap.put(rs.getString("emp_id"), a);
				hmLeavesCode.put(rs.getString("leave_type_id"), rs.getString("leave_type_code"));
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return getMap;
	}

	
	public void getData() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
//			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			String projectName = CF.getProjectNameById(con, getProId());
			String proFreqName = CF.getProFreqNameById(con, getProFreqId());
			request.setAttribute("PROJECT_NAME", projectName + " ("+ proFreqName+")");
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select pf.*,p.pro_id,actual_calculation_type from projectmntnc p, projectmntnc_frequency pf " +
				"where p.pro_id = pf.pro_id and pf.pro_freq_id=?");
			pst.setInt(1, uF.parseToInt(getProFreqId()));
			rs = pst.executeQuery();
			Map<String, String> hmProjectData = new HashMap<String, String>();
			while(rs.next()) {
				hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
				hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmProActualTimeAndCost = CF.getProjectActualCostAndTime(con,request, CF, uF, hmProjectData.get("PRO_ID"), hmProjectData, true, true);
			Map<String, String> hmProBillableTimeAndCost = CF.getProjectBillableCostAndTime(con,request, CF, uF, hmProjectData.get("PRO_ID"), hmProjectData, true, true);
			String submitPendingCnt = getProjectTimeSheetSubmitPendingCount(con, hmProjectData.get("PRO_ID"), hmProjectData.get("PRO_START_DATE"), hmProjectData.get("PRO_END_DATE"));
			String strActEfforts = null;
			String strBillEfforts = null;
			String strSubmitions = null;
			
			if(hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equals("H")) {
				strActEfforts = uF.getTotalTimeMinutes100To60(hmProActualTimeAndCost.get("proActualTime"))+" h"; //9
				strBillEfforts = uF.getTotalTimeMinutes100To60(hmProBillableTimeAndCost.get("proBillableTime"))+" h"; //10
			} else if(hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equals("D")) {
				strActEfforts = uF.formatIntoTwoDecimal(uF.parseToDouble(hmProActualTimeAndCost.get("proActualTime")))+" d"; //9
				strBillEfforts = uF.formatIntoTwoDecimal(uF.parseToDouble(hmProBillableTimeAndCost.get("proBillableTime")))+" d"; //10
			} else if(hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equals("M")) {
				strActEfforts = uF.formatIntoTwoDecimal(uF.parseToDouble(hmProActualTimeAndCost.get("proActualTime")))+" m"; //9
				strBillEfforts = uF.formatIntoTwoDecimal(uF.parseToDouble(hmProBillableTimeAndCost.get("proBillableTime")))+" m"; //10
			}
			
			String[] tmpSPStatus = submitPendingCnt.split("::::");
			StringBuilder sbSP = new StringBuilder();
			sbSP.append("<span style='float:left; margin-right:9px; color:green; font-weight: bold; font-size: 13px;'>"+tmpSPStatus[0]+" <img title=\"Submitted\" style='height:11px;' src=\"images1/icons/hd_tick_20x20.png\"></span>");
			sbSP.append("<span style='float:left; color:red; font-weight: bold; font-size: 13px;'>"+tmpSPStatus[1]+" <img title=\"Pending\" style='height:10px;' src=\"images1/icons/hd_cross_16x16.png\"></span>");
			strSubmitions = sbSP.toString(); //16
			
			request.setAttribute("strActEfforts", strActEfforts);
			request.setAttribute("strBillEfforts", strBillEfforts);
			request.setAttribute("strSubmitions", strSubmitions);
			
			request.setAttribute("PRO_CUSTOMER_NAME", hmProjectData.get("PRO_CUSTOMER_NAME"));
			request.setAttribute("PRO_CUST_SPOC_NAME", hmProjectData.get("PRO_CUST_SPOC_NAME"));
			request.setAttribute("PRO_OWNER_NAME", hmProjectData.get("PRO_OWNER_NAME"));
			request.setAttribute("PRO_BILLING_FREQUENCY", hmProjectData.get("PRO_BILLING_FREQUENCY"));
			
			boolean pendingProBillableApprovedFlag = false;
			pst = con.prepareStatement("select activity_id,emp_id,is_billable_approved from task_activity where (is_approved=1 or is_approved=2) and is_billable_approved=0 and " +
				"task_date between ? and ? and activity_id in (select task_id from activity_info where pro_id=?) order by emp_id");
			pst.setDate(1, uF.getDateFormat(hmProjectData.get("PRO_START_DATE"), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_END_DATE"), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				pendingProBillableApprovedFlag = true;
			}
			rs.close();
			pst.close();
			request.setAttribute("pendingProBillableApprovedFlag", pendingProBillableApprovedFlag);
			
			boolean pendingCustApprovedFlag = false;
			pst = con.prepareStatement("select activity_id,emp_id,is_billable_approved from task_activity where (is_approved=1 or is_approved=2) and is_billable_approved=1 and " +
				"task_date between ? and ? and activity_id in (select task_id from activity_info where pro_id=?) order by emp_id");
			pst.setDate(1, uF.getDateFormat(hmProjectData.get("PRO_START_DATE"), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_END_DATE"), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				pendingCustApprovedFlag = true;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("pendingCustApprovedFlag", pendingCustApprovedFlag);
			
			request.setAttribute("datefrom", hmProjectData.get("PRO_START_DATE"));
			request.setAttribute("dateto", hmProjectData.get("PRO_END_DATE"));


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private String getProjectTimeSheetSubmitPendingCount(Connection con, String proId, String freqStDate, String freqEndDate) {
		 
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		String submitPendingCnt = "0::::0";
		try {
			
			int timesheetSubmitCnt = 0;
			int proEmpCnt = 0;
			StringBuilder sbTasks = null;
			pst = con.prepareStatement("select task_id from activity_info where pro_id = ?");				
			pst.setInt(1, uF.parseToInt(proId));
			rs = pst.executeQuery();
//			System.out.println("pst ===> " + pst);
			while(rs.next()) {
				if(sbTasks == null) {
					sbTasks = new StringBuilder();
					sbTasks.append(rs.getString("task_id"));
				} else {
					sbTasks.append(","+rs.getString("task_id"));
				}
			}
			rs.close();
			pst.close();
			
			if(sbTasks == null) {
				sbTasks = new StringBuilder();
			}
			
			StringBuilder sbProEmp = null;
			pst = con.prepareStatement("select emp_id from project_emp_details where pro_id=?");				
			pst.setInt(1, uF.parseToInt(proId));
			rs = pst.executeQuery();			 
//			System.out.println("pst ===> " + pst);
			while(rs.next()) {
				if(sbProEmp == null) {
					sbProEmp = new StringBuilder();
					sbProEmp.append(rs.getString("emp_id"));
				} else {
					sbProEmp.append(","+rs.getString("emp_id"));
				}
				proEmpCnt++;
			}
			rs.close();
			pst.close();
			
			if(sbProEmp == null) {
				sbProEmp = new StringBuilder();
			}
			
			if(sbTasks.toString().length()>0 && sbProEmp.toString().length()>0) {
				
				pst = con.prepareStatement("select emp_id from task_activity where is_approved >= 1 and " +
					"activity_id in("+sbTasks.toString()+") and emp_id in("+sbProEmp.toString()+") and " +
					"task_date between ? and ? group by emp_id");
				pst.setDate(1, uF.getDateFormat(freqStDate, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(freqEndDate, DATE_FORMAT));
				rs = pst.executeQuery();
//				System.out.println("pst ===> " + pst);
				while(rs.next()) {
					timesheetSubmitCnt++;
				}
				rs.close();
				pst.close();
			}
			
			int pendingCnt = proEmpCnt - timesheetSubmitCnt;
			submitPendingCnt = timesheetSubmitCnt+"::::"+pendingCnt;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return submitPendingCnt;
	}
	
	String timesheetId;
	String strEmpId;

	public String getFrmDate() {
		return frmDate;
	}

	public void setFrmDate(String frmDate) {
		this.frmDate = frmDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public List<FillTask> getTasklist() {
		return tasklist;
	}

	public void setTasklist(List<FillTask> tasklist) {
		this.tasklist = tasklist;
	}

	public String getSave() {
		return save;
	}

	public void setSave(String save) {
		this.save = save;
	}

	public String[] getStrDate() {
		return strDate;
	}

	public void setStrDate(String[] strDate) {
		this.strDate = strDate;
	}

	public String[] getStrTask() {
		return strTask;
	}

	public void setStrTask(String[] strTask) {
		this.strTask = strTask;
	}

	public String[] getStrTime() {
		return strTime;
	}

	public void setStrTime(String[] strTime) {
		this.strTime = strTime;
	}

	public String[] getTaskId() {
		return taskId;
	}

	public void setTaskId(String[] taskId) {
		this.taskId = taskId;
	}

	public String getStrActivity() {
		return strActivity;
	}

	public void setStrActivity(String strActivity) {
		this.strActivity = strActivity;
	}

	public String getStrActivityTaskId() {
		return strActivityTaskId;
	}

	public void setStrActivityTaskId(String strActivityTaskId) {
		this.strActivityTaskId = strActivityTaskId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTimesheetId() {
		return timesheetId;
	}

	public void setTimesheetId(String timesheetId) {
		this.timesheetId = timesheetId;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getSubmit1() {
		return submit1;
	}

	public void setSubmit1(String submit1) {
		this.submit1 = submit1;
	}

	public String[] getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String[] taskDescription) {
		this.taskDescription = taskDescription;
	}

	public String[] getStrBillableTime() {
		return strBillableTime;
	}

	public void setStrBillableTime(String[] strBillableTime) {
		this.strBillableTime = strBillableTime;
	}

	public String[] getStrBillableYesNo() {
		return strBillableYesNo;
	}

	public void setStrBillableYesNo(String[] strBillableYesNo) {
		this.strBillableYesNo = strBillableYesNo;
	}

	public String[] getStrBillableYesNoT() {
		return strBillableYesNoT;
	}

	public void setStrBillableYesNoT(String[] strBillableYesNoT) {
		this.strBillableYesNoT = strBillableYesNoT;
	}

	public String[] getStrTaskOnOffSite() {
		return strTaskOnOffSite;
	}

	public void setStrTaskOnOffSite(String[] strTaskOnOffSite) {
		this.strTaskOnOffSite = strTaskOnOffSite;
	}

	public String[] getStrTaskOnOffSiteT() {
		return strTaskOnOffSiteT;
	}

	public void setStrTaskOnOffSiteT(String[] strTaskOnOffSiteT) {
		this.strTaskOnOffSiteT = strTaskOnOffSiteT;
	}

	public String getUnlock() {
		return unlock;
	}

	public void setUnlock(String unlock) {
		this.unlock = unlock;
	}

	public String[] getStatusTaskId() {
		return statusTaskId;
	}

	public void setStatusTaskId(String[] statusTaskId) {
		this.statusTaskId = statusTaskId;
	}

	public String[] getStatus() {
		return status;
	}

	public void setStatus(String[] status) {
		this.status = status;
	}

	public String getPolicy_id() {
		return policy_id;
	}

	public void setPolicy_id(String policy_id) {
		this.policy_id = policy_id;
	}

	public String getCheckTask() {
		return checkTask;
	}

	public void setCheckTask(String checkTask) {
		this.checkTask = checkTask;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getStrResourceId() {
		return strResourceId;
	}

	public void setStrResourceId(String strResourceId) {
		this.strResourceId = strResourceId;
	}

	public String getProFreqId() {
		return proFreqId;
	}

	public void setProFreqId(String proFreqId) {
		this.proFreqId = proFreqId;
	}

	public String[] getCheckTaskId() {
		return checkTaskId;
	}

	public void setCheckTaskId(String[] checkTaskId) {
		this.checkTaskId = checkTaskId;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

}
