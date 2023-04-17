package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.leave.ManagerLeaveApproval;
import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillWeekDays;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectTimesheets extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	HttpSession session;
	String strSessionEmpId;
	String strUserType;
	String strOrgId;
	
	List<FillProjectList> projectdetailslist;
	List<FillProjectOwnerList> projectOwnerList;
	List<FillBillingType> billingBasisList;
	List<FillBillingType> billingFreqList;
	List<FillClients> clientList;
	
	List<FillMonth> monthList;
	List<FillYears> yearList; 
	List<FillWeekDays> weekList;
	
	String[] proId;
	String[] projectOwner;
	String[] client;
	String[] projectType;
	String[] projectFrequency;
	String sortBy;
	String sortBy1;
	String filterBy;
	
	String strYear;
	String strMonth;
	String strWeek;
	
	String proID;
	String proFreqID;
	String operation;
	
	String fromDate;
	String toDate;
	String empId;
	String submitDate;
	
	String alertStatus;
	String alert_type;
	String alertID;
	
	String pageType;
	
	String proPage;
	String minLimit;
	
	String strSearchJob;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		request.setAttribute(PAGE, "/jsp/task/ProjectTimesheet.jsp");
		request.setAttribute(TITLE, "Timesheet");
		
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strOrgId = (String)session.getAttribute(ORGID); 
		
		if(getPageType() == null || getPageType().equals("") || getPageType().equals("null")) {
			setPageType(null);
		}
		if(getPageType() != null && getPageType().equals("MP")) {
			strUserType = MANAGER;
		}
		
//		System.out.println("getPageType() ===>> " + getPageType());
		UtilityFunctions uF = new UtilityFunctions();
		
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, strOrgId);
		
		if(getTimesheetType() != null && (getTimesheetType().equals("PC") || getTimesheetType().equals("PA") || getTimesheetType().equals("PU"))) {
//			System.out.println("getTimesheetType=====>"+getTimesheetType());
			projectdetailslist = new FillProjectList(request).fillProjectFrequencyList(strUserType, strSessionEmpId, getTimesheetType());
			projectOwnerList = new FillProjectOwnerList(request).fillProjectOwner(strUserType, strSessionEmpId);
			
			billingBasisList = new FillBillingType().fillBillingBasisTypeList();
			billingFreqList = new FillBillingType().fillBillingKindList();
			
			clientList = new FillClients(request).fillClients(false);
			
			monthList = new FillMonth().fillMonth();
			weekList = new FillWeekDays().fillWeekNos();
		}
//		getTimesheet();
//		getTimesheet1();
		
//		System.out.println("getPaycycle() ===>> " + getPaycycle());
		
		if(getPaycycle() != null && getPaycycle().equals("${paycycle}")) {
			setPaycycle(null);
		}
//		if(getAlertStatus()!=null && getAlert_type()!=null){
		if(uF.parseToInt(getAlertID()) > 0) {
			updateUserAlerts();
		}
		
		if(getOperation() != null && getOperation().equals("SaveLater")) {
			updateSaveForLater();
		}
		
		if(getOperation() != null && getOperation().equals("ApproveForBilling")) {
//			System.out.println("Operation=====>"+getOperation());
			approveTimesheetForBilling();
		}
		
		if(getOperation() != null && getOperation().equals("ApproveAndSendToCustomer")) {
//			System.out.println("in ApproveAndSendToCustomer");
			approveTimesheetForBilling();
		}
		
		if(getOperation() != null && (getOperation().equals("Approve") || getOperation().equals("Deny"))) {
//			System.out.println("in approve or deny");
			approveORDenyEmpTimesheet();
		}
		
		if(getSortBy() == null || getSortBy().trim().equals("")) {
			setSortBy("1");
		}
		setSortBy1(getSortBy());
		
		if((getTimesheetType() == null || getTimesheetType().equals("")) && strUserType!=null && strUserType.equals(CUSTOMER)) {
			setTimesheetType("PC");
		}
//		System.out.println("getPageType() ===>> " + getPageType());
		if(uF.parseToInt(getProPage()) == 0) {
			setProPage("1");
		}
		getSearchAutoCompleteData(uF);
		
		if(getTimesheetType() == null || getTimesheetType().equals("") || getTimesheetType().equals("null") || getTimesheetType().equals("EU") || getTimesheetType().equals("EA")) {
			getTimesheet2();
		} else if(getTimesheetType() != null && (getTimesheetType().equals("PC") || getTimesheetType().equals("PA") || getTimesheetType().equals("PU"))) {
			getProjectWiseTimesheet();
		}
		
		if(getPageType() != null && getPageType().equals("MP")) {
			return MYSUCCESS;
		} else {
			return SUCCESS;
		}
	}

	private void getSearchAutoCompleteData(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			SortedSet<String> setSearchList = new TreeSet<String>();
			if(getTimesheetType() == null || getTimesheetType().equals("") || getTimesheetType().equals("null") || getTimesheetType().equals("EU") || getTimesheetType().equals("EA")) {
				Map<String, String> hmEmpName = new HashMap<String, String>();
				CF.getEmpNameCodeMap(con, null, null, null, hmEmpName);
				
				String[] strPayCycleDates = null;
//				System.out.println("getPaycycle() ===>> " + getPaycycle());
//				System.out.println("getFilterBy() ===>> " + getFilterBy());
				int maxDays = 0;
				if(getFilterBy() == null || getFilterBy().equals("") || getFilterBy().equals("P")) {
					if (getPaycycle() != null && !getPaycycle().equals("")) {
						strPayCycleDates = getPaycycle().split("-");
					} else {
						strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
						setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
					}
					maxDays = 32;
					setStrYear("");
					setStrMonth("");
					setStrWeek("");
				} else if(getFilterBy() != null && getFilterBy().equals("O")) {
					if(getStrWeek()==null || getStrWeek().equals("")) {
						String strDate = "01/"+getStrMonth()+"/"+getStrYear();
						String monthminMaxDates = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
						monthminMaxDates = monthminMaxDates+"::::00";
						strPayCycleDates = monthminMaxDates.split("::::");
					} else if(getStrWeek()!=null && !getStrWeek().equals("")) {
						
					}
					
					setPaycycle("");
				}
	
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select ta.emp_id from task_activity ta, " +
					"employee_personal_details epd where ta.emp_id = epd.emp_per_id and to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ? ");
				if(getTimesheetType() == null || getTimesheetType().equals("") || getTimesheetType().equals("EU")) {
					sbQuery.append(" and ta.is_approved > 0 and ta.is_approved < 2 ");
				} else if(getTimesheetType() != null && getTimesheetType().equals("EA")) {
					sbQuery.append(" and ta.is_approved = 2 ");
				}			
				sbQuery.append(" group by ta.emp_id");
				pst = con.prepareStatement(sbQuery.toString());				
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				rs = pst.executeQuery();
//				System.out.println("1 search pst===> "+ pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					setSearchList.add(uF.showData(hmEmpName.get(rs.getString("emp_id")), ""));
				}
				rs.close();
				pst.close();
			} else if(getTimesheetType() != null && (getTimesheetType().equals("PC") || getTimesheetType().equals("PA") || getTimesheetType().equals("PU"))) {
				Map<String, String> hmProjectClientMap = CF.getProjectClientMap(con, uF);
				if(hmProjectClientMap == null) hmProjectClientMap = new HashMap<String, String>();
				Map<String, String> hmEmpName = new HashMap<String, String>();
				CF.getEmpNameCodeMap(con, null, null, null, hmEmpName);
				
		//===start parvez date: 15-10-2022===		
				StringBuilder sbQuery = new StringBuilder();
				/*sbQuery.append("select p.pro_id,p.pro_name,p.project_owner,p.client_id,pf.pro_freq_name,pf.freq_start_date,pf.freq_end_date from projectmntnc p, " +
						"projectmntnc_frequency pf where p.billing_type != 'F' and p.pro_id = pf.pro_id ");*/
				sbQuery.append("select p.pro_id,p.pro_name,p.project_owner,p.project_owners,p.client_id,pf.pro_freq_name,pf.freq_start_date,pf.freq_end_date from projectmntnc p, " +
				"projectmntnc_frequency pf where p.billing_type != 'F' and p.pro_id = pf.pro_id ");
				
				if(getProId()!=null && getProId().length > 0 && !getProId()[0].trim().equals("")) {
					sbQuery.append(" and pf.pro_freq_id in ("+StringUtils.join(getProId(), ",")+") ");
				}
				
				if(getProjectOwner()!=null && getProjectOwner().length > 0 && !getProjectOwner()[0].trim().equals("")) {
//					sbQuery.append(" and p.project_owner in ("+StringUtils.join(getProjectOwner(), ",")+") ");
					sbQuery.append(" and (");
					for(int ii=0; ii<getProjectOwner().length;ii++){
						if(ii==0){
							sbQuery.append(" p.project_owners like '%,"+getProjectOwner()[ii]+",%' ");
						}else{
							sbQuery.append(" or p.project_owners like '%,"+getProjectOwner()[ii]+",%' ");
						}
					}
					sbQuery.append(") ");
				}
				
				if(getClient()!=null && getClient().length > 0 && !getClient()[0].trim().equals("")) {
					sbQuery.append(" and p.client_id in ("+StringUtils.join(getClient(), ",")+") ");
				}
				
				if(strUserType!=null && strUserType.equalsIgnoreCase(CUSTOMER)) {
					sbQuery.append(" and p.poc ="+uF.parseToInt((String)session.getAttribute(EMPID))+" ");
				}
				
				if(getProjectType()!=null && getProjectType().length>0){
	                sbQuery.append(" and (");
	                for(int i=0; i<getProjectType().length; i++){
	                    sbQuery.append(" p.actual_calculation_type = '"+getProjectType()[i]+"'");
	                    
	                    if(i<getProjectType().length-1){
	                        sbQuery.append(" OR "); 
	                    }
	                }
	                sbQuery.append(" ) ");
	            }
				
				if(getProjectFrequency()!=null && getProjectFrequency().length>0){
	                sbQuery.append(" and (");
	                for(int i=0; i<getProjectFrequency().length; i++){
	                    sbQuery.append(" p.billing_kind = '"+getProjectFrequency()[i]+"'");
	                    if(i<getProjectFrequency().length-1){
	                        sbQuery.append(" OR "); 
	                    }
	                }
	                sbQuery.append(" ) ");
	            }
				
				if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && (getPageType() == null || !getPageType().equals("MP"))) {
					/*sbQuery.append(" and ( pf.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or p.added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
						+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");*/
					sbQuery.append(" and ( pf.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
							+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or p.added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
							+" or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%' ) ");
				}
				
				if(getPageType() != null && getPageType().equals("MP")) {
					/*sbQuery.append(" and ( pf.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt((String)session.getAttribute(EMPID))+") or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");*/
					sbQuery.append(" and ( pf.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
							+uF.parseToInt((String)session.getAttribute(EMPID))+") or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%') ");
				}
				
				if(getTimesheetType() != null && !getTimesheetType().equals("") && (getTimesheetType().equals("PC") || getTimesheetType().equals("PA"))) {
					sbQuery.append(" and pf.save_for_later = 0");
				} else if(getTimesheetType() != null && !getTimesheetType().equals("") && getTimesheetType().equals("PU")) {
					sbQuery.append(" and pf.save_for_later = 1");
				}
		//===end parvez date: 15-10-2022===		
				pst = con.prepareStatement(sbQuery.toString());				
//				System.out.println("2 search pst ===> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					
					String pendingCnt = null;
					if(strUserType != null && strUserType.equals(CUSTOMER)) {
						pendingCnt = getCustomerTimeSheetApprovalStatus(con, rs.getString("pro_id"), rs.getString("freq_start_date"), rs.getString("freq_end_date"));
					} else {
						pendingCnt = getProjectTimeSheetApprovalStatus(con, rs.getString("pro_id"), rs.getString("freq_start_date"), rs.getString("freq_end_date"));
					}
					if(getTimesheetType() != null && getTimesheetType().equals("PA") && uF.parseToInt(pendingCnt) > 0) {
						continue;
					} else if(getTimesheetType() != null && !getTimesheetType().equals("") && getTimesheetType().equals("PC") && uF.parseToInt(pendingCnt) == 0) {
						continue;
					}
					setSearchList.add(rs.getString("pro_name"));
					
					if(rs.getString("pro_freq_name")!=null && !rs.getString("pro_freq_name").trim().equals("")){
						setSearchList.add(rs.getString("pro_freq_name").trim());
					}
					
				//===start parvez date: 15-10-2022===	
					/*String strProOwnerName = hmEmpName.get(rs.getString("project_owner"));
					if(uF.parseToInt(rs.getString("project_owner")) > 0 && strProOwnerName!=null && !strProOwnerName.trim().equals("")){
						setSearchList.add(strProOwnerName.trim());
					}*/
					
					if(rs.getString("project_owners")!=null){
						List<String> tempList = Arrays.asList(rs.getString("project_owners").split(","));
						for(int j=1; j<tempList.size();j++){
							String strProOwnerName = hmEmpName.get(tempList.get(j));
							if(uF.parseToInt(tempList.get(j)) > 0 && strProOwnerName!=null && !strProOwnerName.trim().equals("")){
								setSearchList.add(strProOwnerName.trim());
							}
						}
					}
				//===end parvez date: 15-10-2022===		
					
					String strClientName = hmProjectClientMap.get(rs.getString("client_id"));
					if(uF.parseToInt(rs.getString("client_id")) > 0 && strClientName!=null && !strClientName.trim().equals("")){
						setSearchList.add(strClientName.trim());
					}
				}
				rs.close();
				pst.close();
			}
			
			StringBuilder sbData = null;
			Iterator<String> it = setSearchList.iterator();
			while (it.hasNext()){
				String strData = it.next();
				if(sbData == null){
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null){
				sbData = new StringBuilder();
			}
			request.setAttribute("sbData", sbData.toString());
//			System.out.println("sbData====>"+sbData.toString());
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	private void updateUserAlerts() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			String strDomain = request.getServerName().split("\\.")[0];
			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
			userAlerts.setStrDomain(strDomain);
			userAlerts.setAlertID(getAlertID()); 
			if(strUserType!=null && strUserType.equals(CUSTOMER)) {
				userAlerts.setStrOther("other");
			}
			userAlerts.setStatus(DELETE_TR_ALERT);
			Thread t = new Thread(userAlerts);
			t.run();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}
	
	
	/*private void updateUserAlerts() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
		try {
			con = db.makeConnection(con);
			String strType = null;
			if(getAlert_type().equals(TIMESHEET_RECEIVED_ALERT)){
				strType = TIMESHEET_RECEIVED_ALERT;
			}
			
			if(strType!=null && !strType.trim().equals("")){
				String strDomain = request.getServerName().split("\\.")[0];
				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(""+nEmpId);
				userAlerts.set_type(strType);
				userAlerts.setStatus(UPDATE_ALERT);
				Thread t = new Thread(userAlerts);
				t.run(); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}*/
	
	private void approveORDenyEmpTimesheet() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		boolean flag = false;
		
		AddProjectActivity1 apa1 = new AddProjectActivity1();
		apa1.setStrEmpId(getEmpId());
		apa1.strSessionEmpId = strSessionEmpId;
		apa1.setStrPaycycle(getFromDate()+"-"+getToDate());
		apa1.setPageType(getPageType());
		apa1.CF = CF;
		apa1.session = session;
		apa1.request = request;
//		apa1.strOrgId = strOrgId;
		
		try {
			con = db.makeConnection(con);
			String strDomain = request.getServerName().split("\\.")[0];
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
//			Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			
			String levelID = hmEmpLevelMap.get(getEmpId());

			String arrfromToDate = getFromDate()+"-"+getToDate();
			String arr[] = arrfromToDate.split("-");
			arr[0] = getFromDate();
			arr[1] = getToDate();
			
//			String strWLocationId = hmEmpWLocation.get(getEmpId());
//			System.out.println("arr[0] =====> " + arr[0] + " -- arr[1] =====> " + arr[1]);

			pst = con.prepareStatement("select unblock_by from task_activity ta where emp_id=? and task_date between ? and ? and submited_date=? and unblock_by is not null group by unblock_by");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getSubmitDate(), DATE_FORMAT));
			rs = pst.executeQuery();
			List<String> unlockList = new ArrayList<String>();
			while (rs.next()) {
				unlockList.add(rs.getString("unblock_by"));
			}
			rs.close();
			pst.close();

			Map<String, String> hmEmpTaskActivityDate = new LinkedHashMap<String, String>();
			pst = con.prepareStatement("select * from task_activity where emp_id=? and task_date between ? and ? and submited_date=? order by task_date");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getSubmitDate(), DATE_FORMAT));
			rs = pst.executeQuery();

			while (rs.next()) {
				String activity = rs.getString("activity") != null && !rs.getString("activity").equals("") ? rs.getString("activity") : "0";
				String task_data = uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT) + ":_:" + rs.getString("activity_id") + ":_:" + activity;
				hmEmpTaskActivityDate.put(rs.getString("task_id"), task_data);
			}
			rs.close();
			pst.close();

			Map<String, String> hmEmpTaskActivityDateByhr = new LinkedHashMap<String, String>();
			pst = con.prepareStatement(" select * from task_activity where emp_id=? and task_date between ? and ? and submited_date=? and is_approved=1 and activity_id=0 order by task_date");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getSubmitDate(), DATE_FORMAT));
			rs = pst.executeQuery();

			while (rs.next()) {
				String task_data = uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT) + ":_:" + rs.getString("activity_id");
				hmEmpTaskActivityDateByhr.put(rs.getString("task_id"), task_data);
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from task_activity where emp_id=? and task_date between ? and ? and submited_date=? and is_approved=2 order by task_date");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getSubmitDate(), DATE_FORMAT));
			rs = pst.executeQuery();
			List<String> approveTaskList = new ArrayList<String>();
			while (rs.next()) {
				approveTaskList.add(rs.getString("task_id"));
			}
			rs.close();
			pst.close();

			Map<String, String> hmEmpActivity = new LinkedHashMap<String, String>();

			pst = con.prepareStatement("select * from activity_info ai,projectmntnc pmt where ai.emp_id=? "
							+ "and ai.pro_id=pmt.pro_id and pmt.added_by=?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmEmpActivity.put(rs.getString("task_id"), rs.getString("task_id"));
			}
			rs.close();
			pst.close();

			// System.out.println("getUnlock()=====>"+getUnlock());
			if (arr != null && arr.length > 1 && getOperation() != null && getOperation().equalsIgnoreCase("Deny")) {
//				System.out.println("getOperation ===>> " + getOperation());
				Iterator<String> it = hmEmpTaskActivityDate.keySet().iterator();
				while (it.hasNext()) {
					String task_id = (String) it.next(); // task_date
					String[] tempData = hmEmpTaskActivityDate.get(task_id).split(":_:");
					String task_date = tempData[0];
					String activity_id = tempData[1];
					String activity_name = tempData[2];
					// System.out.println("unlock activity_id)=====>"+activity_id);
					// System.out.println("unlock activity_name)=====>"+activity_name);

					if (approveTaskList != null && !approveTaskList.isEmpty()&& approveTaskList.contains(task_id) && (!strUserType.equalsIgnoreCase(HRMANAGER) || (getPageType() != null && getPageType().equals("MP")))) {
						continue;
					}

					if (strUserType.equalsIgnoreCase(HRMANAGER) && strUserType.equalsIgnoreCase(ADMIN)) { 

						pst = con.prepareStatement("update task_activity set is_approved = ?,is_billable_approved = ?,approved_by=null,approved_date=null,unblock_by=null,unblock_time=null, submited_date=null where emp_id=? and task_id= ?");
						pst.setInt(1, 0);
						pst.setInt(2, 0);
						pst.setInt(3, uF.parseToInt(getEmpId()));
						pst.setInt(4, uF.parseToInt(task_id));
						pst.execute();
						pst.close();
						
						pst = con.prepareStatement("delete from work_flow_details where effective_type='"+WORK_FLOW_TIMESHEET+"' "
										+ "and effective_id = (select task_id from task_activity where emp_id=? and task_id=?)");
						pst.setInt(1, uF.parseToInt(getEmpId()));
						pst.setInt(2, uF.parseToInt(task_id));
						pst.execute();
						pst.close();
						
					} else {
						pst = con.prepareStatement("update task_activity set is_approved = ?,is_billable_approved = ?,approved_by=null,approved_date=null,unblock_by=?, submited_date=null where emp_id=? and task_id= ?");
						pst.setInt(1, 0);
						pst.setInt(2, 0);
						pst.setInt(3, uF.parseToInt(strSessionEmpId));
						pst.setInt(4, uF.parseToInt(getEmpId()));
						pst.setInt(5, uF.parseToInt(task_id));
						pst.execute();
						pst.close();
					}
					
					
					pst = con.prepareStatement("update task_activity set is_approved = ?,is_billable_approved = ?,approved_by=null,approved_date=null,unblock_by=null,unblock_time=null, submited_date=null where emp_id=? and task_id= ? and is_approved != 2");
					pst.setInt(1, 0);
					pst.setInt(2, 0);
					pst.setInt(3, uF.parseToInt(getEmpId()));
					pst.setInt(4, uF.parseToInt(task_id));
					pst.execute();
					pst.close();

					pst = con.prepareStatement("delete from work_flow_details where effective_type='"+WORK_FLOW_TIMESHEET+"' "
									+ "and effective_id = (select task_id from task_activity where emp_id=? and task_id=? and is_approved != 2)");
					pst.setInt(1, uF.parseToInt(getEmpId()));
					pst.setInt(2, uF.parseToInt(task_id));
					pst.execute();
					pst.close();

				}
				
				
				/**
				 * Notification
				 * */
				
				Notifications nF = new Notifications(N_TIMESHEET_RE_OPENED, CF); 
				nF.setDomain(strDomain);
				
				nF.request = request;
				nF.setStrOrgId((String)session.getAttribute(ORGID));
				nF.setEmailTemplate(true);
				
				nF.setStrEmpId(getEmpId());
				nF.setStrFromDate(arr[0]);
				nF.setStrToDate(arr[1]);
				nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.sendNotifications();
				
				
				if (strUserType != null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
					apa1.deleteCompOffLeave(con , uF, arr[0], arr[1], false, levelID);
					apa1.deleteHolidaysCount(con , uF, arr[0], arr[1]);
					apa1.deleteAttendanceDetails(con, uF, arr[0], arr[1]);
				}

				session.setAttribute(MESSAGE,SUCCESSM+ "You have successfully unlocked the timesheet. Please ensure your team member resubmits the timesheet for your approval."+ END);

			} else if (arr != null && arr.length > 1 && getOperation() != null && getOperation().equalsIgnoreCase("Approve") && (!strUserType.equalsIgnoreCase(HRMANAGER) || (strUserType.equalsIgnoreCase(HRMANAGER) && getPageType() != null && getPageType().equals("MP"))) && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
//				System.out.println("getOperation ===>> " + getOperation());				
				Iterator<String> it = hmEmpTaskActivityDate.keySet().iterator();
				while (it.hasNext()) {
					String task_id = (String) it.next(); // task_date

					String[] tempData = hmEmpTaskActivityDate.get(task_id).split(":_:");
					String task_date = tempData[0];
					String activity_id = tempData[1];
					String activity_name = tempData[2];
//					 System.out.println("checkTaskList====>"+checkTaskList);

						pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_TIMESHEET+"' and is_approved=0 and emp_id=? order by work_flow_id");
						pst.setInt(1, uF.parseToInt(task_id));
						pst.setInt(2, uF.parseToInt(strSessionEmpId));
						rs = pst.executeQuery();
						int work_id = 0;
						while (rs.next()) {
							work_id = rs.getInt("work_flow_id");
							break;
						}
						rs.close();
						pst.close();
						
						boolean taskApproveByWFFlag = false;
						pst = con.prepareStatement("select is_approved from work_flow_details where work_flow_id=? and is_approved = 1");
						pst.setInt(1, work_id);
						rs = pst.executeQuery();
						while(rs.next()) {
							taskApproveByWFFlag = true;
						}
						rs.close();
						pst.close();
						
						if(!taskApproveByWFFlag) {
							pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,emp_id=?,approve_date=? WHERE work_flow_id=?");
							pst.setInt(1, 1);
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(4, work_id);
							pst.execute();
							pst.close();
						}
						

					if (uF.parseToBoolean(CF.getIsWorkFlow())) {
						if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN) && (getPageType() == null || !getPageType().equals("MP"))) {

							boolean taskApproveFlag = false;
							pst = con.prepareStatement("select is_approved from task_activity  where emp_id=? and task_id=? and is_approved = 2");
							pst.setInt(1, uF.parseToInt(getEmpId()));
							pst.setInt(2, uF.parseToInt(task_id));
							rs = pst.executeQuery();
							while(rs.next()) {
								taskApproveFlag = true;
							}
							rs.close();
							pst.close();
							
							if(!taskApproveFlag) {
								pst = con.prepareStatement("update task_activity set approved_by =?, is_approved = ?,is_billable_approved = ?, approved_date=?  where emp_id=? and task_id=? and is_approved = 1");
								pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
								pst.setInt(2, 2);
								pst.setInt(3, 1);
								pst.setDate(4,uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(5, uF.parseToInt(getEmpId()));
								pst.setInt(6, uF.parseToInt(task_id));
								pst.execute();
								pst.close();
							}
							flag = true;

						} else {
							pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_TIMESHEET+"' and is_approved=0 and emp_id=? order by work_flow_id");
							pst.setInt(1, uF.parseToInt(task_id));
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							rs = pst.executeQuery();
							int work_id1 = 0;
							while (rs.next()) {
								work_id1 = rs.getInt("work_flow_id");
								break;
							}
							rs.close();
							pst.close();

							boolean taskApproveByWFFlag1 = false;
							pst = con.prepareStatement("select is_approved from work_flow_details where work_flow_id=? and is_approved = 1");
							pst.setInt(1, work_id1);
							rs = pst.executeQuery();
							while(rs.next()) {
								taskApproveByWFFlag1 = true;
							}
							rs.close();
							pst.close();
							
							if(!taskApproveByWFFlag1) {
								pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,emp_id=?,approve_date=? WHERE work_flow_id=?");
								pst.setInt(1, 1);
								pst.setInt(2, uF.parseToInt(strSessionEmpId));
								pst.setDate(3,uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(4, work_id);
								pst.execute();
								pst.close();
							}
							
							
							boolean taskApproveFlag = false;
							pst = con.prepareStatement("select is_approved from task_activity  where emp_id=? and task_id=? and is_approved = 2");
							pst.setInt(1, uF.parseToInt(getEmpId()));
							pst.setInt(2, uF.parseToInt(task_id));
							rs = pst.executeQuery();
							while(rs.next()) {
								taskApproveFlag = true;
							}
							rs.close();
							pst.close();
							
							if(!taskApproveFlag) {
								pst = con.prepareStatement("update task_activity set approved_by =?, is_approved = ?,is_billable_approved = ?, approved_date=?  where emp_id=? and task_id=? and is_approved = 1");
								pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
								pst.setInt(2, 2);
								pst.setInt(3, 1);
								pst.setDate(4,uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(5, uF.parseToInt(getEmpId()));
								pst.setInt(6, uF.parseToInt(task_id));
								pst.execute();
								pst.close();
							}
						}
					} else {
						if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN) && (getPageType() == null || !getPageType().equals("MP"))) {
							boolean taskApproveFlag = false;
							pst = con.prepareStatement("select is_approved from task_activity  where emp_id=? and task_id=? and is_approved = 2");
							pst.setInt(1, uF.parseToInt(getEmpId()));
							pst.setInt(2, uF.parseToInt(task_id));
							rs = pst.executeQuery();
							while(rs.next()) {
								taskApproveFlag = true;
							}
							rs.close();
							pst.close();
							
							if(!taskApproveFlag) {
								pst = con.prepareStatement("update task_activity set approved_by =?, is_approved = ?,is_billable_approved = ?, approved_date=?  where emp_id=? and task_id=? and is_approved = 1");
								pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
								pst.setInt(2, 2);
								pst.setInt(3, 1);
								pst.setDate(4,uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(5, uF.parseToInt(getEmpId()));
								pst.setInt(6, uF.parseToInt(task_id));
								pst.execute();
								pst.close();
							}
							flag = true;

						}
					}
//					System.out.println("in ProTimesheet Approve");
				}

				
				/**
				 * Notification
				 * */
				
				Notifications nF = new Notifications(N_TIMESHEET_APPROVED, CF); 
				nF.setDomain(strDomain);
				
				nF.request = request;
				nF.setStrOrgId((String)session.getAttribute(ORGID));
				nF.setEmailTemplate(true);
				
				nF.setStrEmpId(getEmpId());
				nF.setStrFromDate(arr[0]);
				nF.setStrToDate(arr[1]);
				nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.sendNotifications();
				
				
				
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(arr[0],DATE_FORMAT, "dd")));
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(arr[0], DATE_FORMAT, "MM")) - 1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(arr[0],DATE_FORMAT, "yyyy")));
				int nDateCount = uF.parseToInt(uF.dateDifference(arr[0],DATE_FORMAT, arr[1], DATE_FORMAT,CF.getStrTimeZone()));
//				Set<String> comOffDateSet = new HashSet<String>(Arrays.asList(getCompOff()));
//				Set<String> comOffDate = new HashSet<String>(Arrays.asList(getCompOffDate()));
//				Iterator<String> iterator = comOffDate.iterator();
				ManagerLeaveApproval leaveApproval = new ManagerLeaveApproval();
				for (int i = 0; i < nDateCount; i++) {
//					String coffDate = iterator.next();
					String coffDate=uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,DATE_FORMAT);
//					System.out.println("PTS/842--coffDate="+coffDate);
					pst = con.prepareStatement("select * from emp_leave_entry  where emp_id=? and is_approved in(1,-1) and is_compensate=true and (approval_from, approval_to_date) overlaps(to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD'))");
					pst.setInt(1, uF.parseToInt(getEmpId()));
					pst.setDate(2, uF.getDateFormat(coffDate, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(coffDate, DATE_FORMAT));
//					System.out.println("PTS/846--pst="+pst);
					rs = pst.executeQuery();
					boolean flag1 = false;
					while (rs.next()) {
						flag1 = true;
					}
					rs.close();
					pst.close();
					
					if (flag1) {
//						System.out.println("PTS/857--continue");
				//===start parvez date: 15-11-2021===
						cal.add(Calendar.DATE, 1);
				//===end parvez date: 15-11-2021===
						continue;
					}
					pst = con.prepareStatement("UPDATE emp_leave_entry SET is_approved=?,manager_reason=?, approval_from=?, approval_to_date=?, emp_no_of_leave=?, user_id=? "
									+ " where emp_id=? and is_compensate=true and (approval_from, approval_to_date) overlaps(to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD'))");
					pst.setInt(1, 1);
					pst.setString(2, "Approve Request");
					pst.setDate(3, uF.getDateFormat(coffDate, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(coffDate, DATE_FORMAT));
					int nAppliedDays = 0;

					nAppliedDays = uF.parseToInt(uF.dateDifference(coffDate, DATE_FORMAT, coffDate, DATE_FORMAT,CF.getStrTimeZone()));
					pst.setInt(5, nAppliedDays);

					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.setInt(7, uF.parseToInt(getEmpId()));
					pst.setDate(8, uF.getDateFormat(coffDate, DATE_FORMAT));
					pst.setDate(9, uF.getDateFormat(coffDate, DATE_FORMAT));
//					System.out.println("PTS/873--pst ===>> " + pst);
					pst.execute();
					pst.close();
					
//					System.out.println("comOffDateSet ===>> " + comOffDateSet + " -- coffDate ===>> " + coffDate);
					pst = con.prepareStatement("select * from emp_leave_entry where emp_id=? and is_compensate=true and (approval_from, approval_to_date) overlaps(to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD'))");
					pst.setInt(1, uF.parseToInt(getEmpId()));
					pst.setDate(2,uF.getDateFormat(coffDate, DATE_FORMAT));
					pst.setDate(3,uF.getDateFormat(coffDate, DATE_FORMAT));
//					System.out.println("PTS/882--pst ===>> " + pst);
					rs = pst.executeQuery();
					String leave_id = "";
					String typeOfLeave = "";
					while (rs.next()) {
						leave_id = rs.getString("leave_id");
						typeOfLeave = rs.getString("leave_type_id");
					}
					rs.close();
					pst.close();

					if(uF.parseToInt(leave_id) > 0) {
//						System.out.println("leave_id ===>> " + leave_id);
						leaveApproval.setServletRequest(request);
						leaveApproval.setLeaveId(leave_id);
						leaveApproval.setTypeOfLeave(typeOfLeave);
						leaveApproval.setEmpId(getEmpId());
						leaveApproval.setIsapproved(1);
						leaveApproval.setApprovalFromTo(coffDate);
						leaveApproval.setApprovalToDate(coffDate);
						leaveApproval.insertLeaveBalance(con, pst, rs, uF,uF.parseToInt(hmEmpLevelMap.get(getEmpId())), CF);
					}
					cal.add(Calendar.DATE, 1);
				}
			
				
//				 System.out.println("getCompOff().length "+getCompOff().length);
//				if (getCompOffDate() != null && getCompOffDate().length > 0 && getCompOff() != null && getCompOff().length > 0) {
//					Set<String> comOffDateSet = new HashSet<String>(Arrays.asList(getCompOff()));
//					Set<String> comOffDate = new HashSet<String>(Arrays.asList(getCompOffDate()));
//					Iterator<String> iterator = comOffDate.iterator();
//					ManagerLeaveApproval leaveApproval = new ManagerLeaveApproval();
//					while (iterator.hasNext()) {
//						String coffDate = iterator.next();
//	
//						pst = con.prepareStatement("select * from emp_leave_entry where emp_id=? and is_approved in(1,-1) and is_compensate=true and (approval_from, approval_to_date) overlaps(to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD'))");
//						pst.setInt(1, uF.parseToInt(getEmpId()));
//						pst.setDate(2, uF.getDateFormat(coffDate, DATE_FORMAT));
//						pst.setDate(3, uF.getDateFormat(coffDate, DATE_FORMAT));
//						rs = pst.executeQuery();
//						boolean flag1 = false;
//						while (rs.next()) {
//							flag1 = true;
//						}
//						rs.close();
//						pst.close();
//						
//						if (flag1) {
//							continue;
//						}
//	
//						pst = con.prepareStatement("UPDATE emp_leave_entry SET is_approved=?,manager_reason=?, approval_from=?, approval_to_date=?, emp_no_of_leave=?, user_id=? "
//										+ " where emp_id=? and is_compensate=true and (approval_from, approval_to_date) overlaps(to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD'))");
//						String req = "Approve Request";
//						if (!comOffDateSet.contains(coffDate)) {
//							pst.setInt(1, -1);
//							req = "Deny request";
//						} else {
//							pst.setInt(1, 1);
//						}
//						pst.setString(2, req);
//						pst.setDate(3, uF.getDateFormat(coffDate, DATE_FORMAT));
//						pst.setDate(4, uF.getDateFormat(coffDate, DATE_FORMAT));
//						int nAppliedDays = 0;
//	
//						nAppliedDays = uF.parseToInt(uF.dateDifference(coffDate, DATE_FORMAT, coffDate, DATE_FORMAT,CF.getStrTimeZone()));
//						pst.setInt(5, nAppliedDays);
//	
//						pst.setInt(6, uF.parseToInt(strSessionEmpId));
//						pst.setInt(7, uF.parseToInt(getEmpId()));
//						pst.setDate(8, uF.getDateFormat(coffDate, DATE_FORMAT));
//						pst.setDate(9, uF.getDateFormat(coffDate, DATE_FORMAT));
//	//					System.out.println("pst ===>> " + pst);
//						pst.execute();
//						pst.close();
//						
//	//					System.out.println("comOffDateSet ===>> " + comOffDateSet + " -- coffDate ===>> " + coffDate);
//						if (comOffDateSet.contains(coffDate)) {
//	
//							pst = con.prepareStatement("select * from emp_leave_entry where emp_id=? and is_compensate=true "
//								+ "and (approval_from, approval_to_date) overlaps(to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD'))");
//							pst.setInt(1, uF.parseToInt(getEmpId()));
//							pst.setDate(2,uF.getDateFormat(coffDate, DATE_FORMAT));
//							pst.setDate(3,uF.getDateFormat(coffDate, DATE_FORMAT));
//	//						System.out.println("pst ===>> " + pst);
//							rs = pst.executeQuery();
//							String leave_id = "";
//							String typeOfLeave = "";
//							while (rs.next()) {
//								leave_id = rs.getString("leave_id");
//								typeOfLeave = rs.getString("leave_type_id");
//							}
//							rs.close();
//							pst.close();
//	
//	//						System.out.println("leave_id ===>> " + leave_id);
//							leaveApproval.setServletRequest(request);
//							leaveApproval.setLeaveId(leave_id);
//							leaveApproval.setTypeOfLeave(typeOfLeave);
//							leaveApproval.setEmpId(getEmpId());
//							leaveApproval.setIsapproved(1);
//							leaveApproval.setApprovalFromTo(coffDate);
//							leaveApproval.setApprovalToDate(coffDate);
//							leaveApproval.insertLeaveBalance(con, pst, rs, uF,uF.parseToInt(hmEmpLevelMap.get(getEmpId())), CF);
//						}
//					}
//				}
				
				boolean flag1 = true;
				pst = con.prepareStatement("select * from task_activity  where emp_id=? and is_approved in(0,1) and task_date between ? and ? "); //and timesheet_paycycle=?
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
//				pst.setInt(4, uF.parseToInt(arr[2]));	
//				System.out.println("pst======>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					flag1 = false;
				}
				rs.close();
				pst.close();
				
				if (flag1) {
					flag = true;
				}
				apa1.insertInHolidaysCount(con,uF, arr[0], arr[1]);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
		if (flag) {
			apa1.insertintoAttendance();
		}
	}
	
	
	
	
	private void approveTimesheetForBilling() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			List<String> proFreqList = new ArrayList<String>();
			
			if(getProFreqID() != null && !getProFreqID().equals("")) {
				proFreqList = Arrays.asList(getProFreqID().split(","));
			}
//			System.out.println("proFreqList ===>> " + proFreqList);
			
			for(int i=0; proFreqList!= null && i<proFreqList.size(); i++) {
			pst = con.prepareStatement("select pf.*,p.pro_id,actual_calculation_type from projectmntnc p, projectmntnc_frequency pf " +
				"where p.billing_type != 'F' and p.pro_id = pf.pro_id and pf.pro_freq_id=?");
			pst.setInt(1, uF.parseToInt(proFreqList.get(i)));
//			System.out.println("pst1======>"+pst);
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
			pst.setInt(3, uF.parseToInt(hmProjectData.get("PRO_ID")));
//			System.out.println("pst2======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				String activity = rs.getString("activity") != null && !rs.getString("activity").equals("") ? rs.getString("activity") : "0";
				String task_data = uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT) + "::_::" + rs.getString("emp_id")+":_:"+rs.getString("activity_id") + "::_::" + rs.getString("emp_id")+":_:"+activity;
				hmEmpTaskActivityDate.put(rs.getString("task_id"), task_data);
			}
			rs.close();
			pst.close();

//			 System.out.println("getUnlock()=====>"+getUnlock());
//				if (getType() != null && getType().equalsIgnoreCase("approve")) {
					Iterator<String> it = hmEmpTaskActivityDate.keySet().iterator();
					while (it.hasNext()) {
						String task_id = (String) it.next(); // task_date
						if (uF.parseToBoolean(CF.getIsWorkFlow())) {
								boolean taskApproveFlag = false;
								StringBuilder sbQuery = new StringBuilder();
								if(getOperation() != null && getOperation().equals("ApproveAndSendToCustomer")) {
									sbQuery.append("select is_billable_approved from task_activity where task_id=? and is_approved=2 and is_billable_approved=1");
								} else if(getOperation() != null && getOperation().equals("ApproveForBilling")) {
									sbQuery.append("select is_billable_approved from task_activity where task_id=? and is_approved=2 and is_billable_approved=2");
								}
								pst = con.prepareStatement(sbQuery.toString());
								pst.setInt(1, uF.parseToInt(task_id));
								rs = pst.executeQuery();
//							System.out.println("chk pst ===>> " + pst);
								while(rs.next()) {
									taskApproveFlag = true;
								}
								rs.close();
								pst.close();
								
//								System.out.println("taskApproveFlag ........." + taskApproveFlag);
								if(!taskApproveFlag) {
									StringBuilder sbQue = new StringBuilder();
									sbQue.append("update task_activity set is_approved=?, billable_approved_by=?, is_billable_approved=?, billable_approve_date=? where task_id=?");
									if(getOperation() != null && getOperation().equals("ApproveAndSendToCustomer")) {
										sbQue.append(" and is_billable_approved = 0");
									} else if(getOperation() != null && getOperation().equals("ApproveForBilling") && strUserType !=null && strUserType.equals(CUSTOMER)) {
										sbQue.append(" and is_billable_approved = 1");
									}
									pst = con.prepareStatement(sbQue.toString()); // and is_billable_approved=0 and is_approved=1
									pst.setInt(1, 2);
									pst.setInt(2, uF.parseToInt((String) session.getAttribute(EMPID)));
									if(getOperation() != null && getOperation().equals("ApproveAndSendToCustomer")) {
										pst.setInt(3, 1);
									} else if(getOperation() != null && getOperation().equals("ApproveForBilling")) {
										pst.setInt(3, 2);
									}
									pst.setDate(4,uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(5, uF.parseToInt(task_id));
									pst.executeUpdate();
									pst.close();
//									System.out.println("update pst ===>> " + pst);
									
									pst = con.prepareStatement("update projectmntnc_frequency set save_for_later = 2 where pro_freq_id=?"); // on approval it will set save_for_later = 2 
									pst.setInt(1, uF.parseToInt(proFreqList.get(i)));
									pst.executeUpdate();
									pst.close();
								}
						} else {
//							System.out.println("strUserType==>"+strUserType);
							if (strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))) {
								boolean taskApproveFlag = false;
								StringBuilder sbQuery = new StringBuilder();
								if(getOperation() != null && getOperation().equals("ApproveAndSendToCustomer")) {
									sbQuery.append("select is_billable_approved from task_activity where task_id=? and is_approved=2 and is_billable_approved=1");
								} else if(getOperation() != null && getOperation().equals("ApproveForBilling")) {
									sbQuery.append("select is_billable_approved from task_activity where task_id=? and is_approved=2 and is_billable_approved=2");
								}
								pst = con.prepareStatement(sbQuery.toString());
	//							pst.setInt(1, uF.parseToInt(getStrEmpId()));
								pst.setInt(1, uF.parseToInt(task_id));
//								System.out.println("strUserType pst=====>"+pst);
								rs = pst.executeQuery();
								while(rs.next()) {
									taskApproveFlag = true;
								}
								rs.close();
								pst.close();
//								System.out.println("taskApproveFlag==>"+taskApproveFlag);
								if(!taskApproveFlag) {
									StringBuilder sbQue = new StringBuilder();
									sbQue.append("update task_activity set is_approved=?, billable_approved_by=?, is_billable_approved=?, billable_approve_date=? where task_id=?");
									if(getOperation() != null && getOperation().equals("ApproveAndSendToCustomer")) {
										sbQue.append(" and is_billable_approved = 0");
									} else if(getOperation() != null && getOperation().equals("ApproveForBilling") && strUserType !=null && strUserType.equals(CUSTOMER)) {
										sbQue.append(" and is_billable_approved = 1");
									}
									pst = con.prepareStatement(sbQue.toString()); // and is_billable_approved=0 and is_approved=1
									pst.setInt(1, 2);
									pst.setInt(2, uF.parseToInt((String) session.getAttribute(EMPID)));
									if(getOperation() != null && getOperation().equals("ApproveAndSendToCustomer")) {
										pst.setInt(3, 1);
									} else if(getOperation() != null && getOperation().equals("ApproveForBilling")) {
//										System.out.println("is_billable_approved set to 2==>"+getOperation());
										pst.setInt(3, 2);
									}
									pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(5, uF.parseToInt(task_id));
//									System.out.println("update pst ===>> " + pst);
									pst.executeUpdate();
									pst.close();
									
									pst = con.prepareStatement("update projectmntnc_frequency set save_for_later = 2 where pro_freq_id=?"); // on approval it will set save_for_later = 2 
									pst.setInt(1, uF.parseToInt(proFreqList.get(i)));
									pst.executeUpdate();
									pst.close();
								}
							}
						}
					}
					
					if(getOperation() != null && getOperation().equals("ApproveAndSendToCustomer")) {
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
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void updateSaveForLater() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update projectmntnc_frequency set save_for_later = 1 where pro_freq_id in ("+proFreqID+")");
			pst.execute();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}



	private void getProjectWiseTimesheet() {
		 
		Connection con = null;
		PreparedStatement pst = null; 
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			getSelectedFilter(uF, hmEmpName);
			
			String[] strPayCycleDates = null;
			if (getPaycycle() != null && !getPaycycle().equals("")) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycle(con,CF.getStrTimeZone(), CF);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
			}
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
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select pf.*,p.pro_id,actual_calculation_type from projectmntnc p, projectmntnc_frequency pf " +
					"where p.billing_type != 'F' and p.pro_id = pf.pro_id ");
			
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and p.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and p.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			
			if(getProId()!=null && getProId().length > 0 && !getProId()[0].trim().equals("")) {
				sbQuery.append(" and pf.pro_freq_id in ("+StringUtils.join(getProId(), ",")+") ");
			}
			
			if(getProjectOwner()!=null && getProjectOwner().length > 0 && !getProjectOwner()[0].trim().equals("")) {
			//===start parvez date: 15-10-2022===	
//				sbQuery.append(" and p.project_owner in ("+StringUtils.join(getProjectOwner(), ",")+") ");
				sbQuery.append(" and (");
				for(int ii=0; ii<getProjectOwner().length;ii++){
					if(ii==0){
						sbQuery.append(" p.project_owners like '%,"+getProjectOwner()[ii]+",%' ");
					}else{
						sbQuery.append(" or p.project_owners like '%,"+getProjectOwner()[ii]+",%' ");
					}
				}
				sbQuery.append(") ");
			//===end parvez date: 15-10-2022===	
			}
			
			if(getClient()!=null && getClient().length > 0 && !getClient()[0].trim().equals("")) {
				sbQuery.append(" and p.client_id in ("+StringUtils.join(getClient(), ",")+") ");
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(CUSTOMER)) {
				sbQuery.append(" and p.poc ="+uF.parseToInt((String)session.getAttribute(EMPID))+" ");
			}
			
			if(getProjectType()!=null && getProjectType().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getProjectType().length; i++){
                    sbQuery.append(" p.actual_calculation_type = '"+getProjectType()[i]+"'");
                    
                    if(i<getProjectType().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
			
			if(getProjectFrequency()!=null && getProjectFrequency().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getProjectFrequency().length; i++){
                    sbQuery.append(" p.billing_kind = '"+getProjectFrequency()[i]+"'");
                    
                    if(i<getProjectFrequency().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
			
		//===start parvez date: 15-10-2022===	
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && (getPageType() == null || !getPageType().equals("MP"))) {
				/*sbQuery.append(" and ( pf.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or p.added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");*/
				sbQuery.append(" and ( pf.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or p.added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
						+" or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%' ) ");
			}
			
			if(getPageType() != null && getPageType().equals("MP")) {
				/*sbQuery.append(" and ( pf.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt((String)session.getAttribute(EMPID))+") or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");*/
				sbQuery.append(" and ( pf.pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt((String)session.getAttribute(EMPID))+") or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%') ");
			}
		//===end parvez date: 15-10-2022===	
			
			
			if(getTimesheetType() != null && !getTimesheetType().equals("") && (getTimesheetType().equals("PC") || getTimesheetType().equals("PA"))) {
				sbQuery.append(" and pf.save_for_later = 0");
			} else if(getTimesheetType() != null && !getTimesheetType().equals("") && getTimesheetType().equals("PU")) {
				sbQuery.append(" and pf.save_for_later = 1");
			}
			
		//Need to change	
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
				sbQuery.append(" and (upper(p.pro_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(pf.pro_freq_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'" +
					" or p.client_id in (select client_id from client_details where upper(client_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%') " +
					"or p.project_owner in (select eod.emp_id from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id " +
					"and upper(epd.emp_fname)|| ' '||upper(epd.emp_lname) like'%"+getStrSearchJob().trim().toUpperCase()+"%'))");
			}
			
			if(getSortBy() != null && uF.parseToInt(getSortBy()) == 1) {
				sbQuery.append(" order by freq_start_date desc ");
			} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 2) {
				sbQuery.append(" order by freq_start_date ");
			} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 3) {
				sbQuery.append(" order by pro_name,pro_freq_name ");
			} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 4) {
				sbQuery.append(" order by pro_name desc, pro_freq_name desc ");
			} else {
				sbQuery.append(" order by freq_start_date desc ");
			}
//			sbQuery.append(" order by pro_freq_id desc");
			
			pst = con.prepareStatement(sbQuery.toString());				
//			System.out.println("pst ===> " + pst);
			rs = pst.executeQuery();			 
			List<List<String>> projectList = new LinkedList<List<String>>();
			while(rs.next()) {
				String pendingCnt = null;
				if(strUserType != null && strUserType.equals(CUSTOMER)) {
					pendingCnt = getCustomerTimeSheetApprovalStatus(con, rs.getString("pro_id"), rs.getString("freq_start_date"), rs.getString("freq_end_date"));
				} else {
					pendingCnt = getProjectTimeSheetApprovalStatus(con, rs.getString("pro_id"), rs.getString("freq_start_date"), rs.getString("freq_end_date"));
				}
//				String clientApprovalStatus = getProjectTimeSheetClientApprovalStatus(con, rs.getString("pro_id"), rs.getString("freq_start_date"), rs.getString("freq_end_date"));
				String submitPendingCnt = getProjectTimeSheetSubmitPendingCount(con, rs.getString("pro_id"), rs.getString("freq_start_date"), rs.getString("freq_end_date"));
				
//				System.out.println("pendingCnt ===>> " + pendingCnt);
//				System.out.println("getTimesheetType ===>> " + getTimesheetType());
				if(getTimesheetType() != null && getTimesheetType().equals("PA") && uF.parseToInt(pendingCnt) > 0) {
					continue;
				} else if(getTimesheetType() != null && !getTimesheetType().equals("") && getTimesheetType().equals("PC") && uF.parseToInt(pendingCnt) == 0) {
					continue;
				}
				
				String billApproveStatus = getTimeSheetBillApprovalStatus(con, rs.getString("pro_id"), rs.getString("freq_start_date"), rs.getString("freq_end_date"));
				
				String[] billApproveStatusAndDate = billApproveStatus.split("::::");
				
				boolean isView = false;
				Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
//				System.out.println("hmProjectData ===>> " + hmProjectData);
				hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT));
				
				Map<String, String> hmActualAndBillEfferts = getProjectFreqActualAndBillableEfforts(con, rs.getString("pro_id"), hmProjectData);
				Map<String, String> hmProActualTimeAndCost = CF.getProjectActualCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData, true, true);
				Map<String, String> hmProBillableTimeAndCost = CF.getProjectBillableCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData, true, true);
				
				List<String> innerList = new ArrayList<String>();
				StringBuilder sb = new StringBuilder();
//				
				innerList.add(hmProjectData.get("PRO_ID")); //0
				innerList.add(uF.showData(hmProjectData.get("PRO_NAME"), "-") + " ("+uF.showData(rs.getString("pro_freq_name"), "-")+")"); //1
				innerList.add(uF.showData(hmProjectData.get("PRO_CUSTOMER_NAME"), "-")); //2
				innerList.add(uF.showData(hmProjectData.get("PRO_OWNER_NAME"), "-")); //3
				innerList.add(uF.showData(hmProjectData.get("PRO_BILLING_TYPE"), "-")); //4
				innerList.add(uF.showData(hmProjectData.get("PRO_BILLING_FREQUENCY"), "-")); //5
				innerList.add(uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, CF.getStrReportDateFormat())  +" to " + uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, CF.getStrReportDateFormat())); //6
				innerList.add(uF.showData(hmActualAndBillEfferts.get("APPROVED_BY"), "-")); //7
				innerList.add(""); //8
				if(rs.getString("actual_calculation_type") != null && rs.getString("actual_calculation_type").equals("H")) {
//					innerList.add(hmActualAndBillEfferts.get("ACTUAL_HRS")+" h"); //9
//					innerList.add(hmActualAndBillEfferts.get("BILLABLE_HRS")+" h"); //10
					innerList.add(uF.getTotalTimeMinutes100To60(hmProActualTimeAndCost.get("proActualTime"))+" h"); //9
					innerList.add(uF.getTotalTimeMinutes100To60(hmProBillableTimeAndCost.get("proBillableTime"))+" h"); //10
				} else if(rs.getString("actual_calculation_type") != null && rs.getString("actual_calculation_type").equals("D")) {
//					innerList.add(hmActualAndBillEfferts.get("ACTUAL_DAYS")+" d"); //9
//					innerList.add(hmActualAndBillEfferts.get("BILLABLE_DAYS")+" d"); //10
					innerList.add(uF.formatIntoTwoDecimal(uF.parseToDouble(hmProActualTimeAndCost.get("proActualTime")))+" d"); //9
					innerList.add(uF.formatIntoTwoDecimal(uF.parseToDouble(hmProBillableTimeAndCost.get("proBillableTime")))+" d"); //10
				} else if(rs.getString("actual_calculation_type") != null && rs.getString("actual_calculation_type").equals("M")) {
					innerList.add(uF.formatIntoTwoDecimal(uF.parseToDouble(hmProActualTimeAndCost.get("proActualTime")))+" m"); //9
					innerList.add(uF.formatIntoTwoDecimal(uF.parseToDouble(hmProBillableTimeAndCost.get("proBillableTime")))+" m"); //10
				} else {
					innerList.add(""); //9
					innerList.add(""); //10
				}
				
				if(uF.parseToInt(billApproveStatusAndDate[0]) == -1) {
					/*sb.append("<img src=\"images1/icons/denied.png\" border=\"0\" />"); */
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>"); 
					
				} else if(uF.parseToInt(billApproveStatusAndDate[0]) == 0) {
					/*sb.append("<img src=\"images1/icons/pending.png\" border=\"0\" />"); */
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\"></i>"); 
					
				} else if(uF.parseToInt(billApproveStatusAndDate[0]) == 1) {
					/*sb.append("<img src=\"images1/icons/re_submit.png\" border=\"0\" />"); */
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>"); 
					
				} else {
					/*sb.append("<img src=\"images1/icons/approved.png\" border=\"0\" />");*/
					sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
					
				}
				innerList.add(sb.toString()); //11
				innerList.add(rs.getString("pro_freq_id")); //12
				if(uF.parseToDouble(hmProActualTimeAndCost.get("proActualTime")) > 0) {
					isView = true;
				}
				innerList.add(isView+""); //13
				innerList.add("<a href=\"GenerateProjectwiseTimesheet.action?proId="+rs.getString("pro_id")+"&proFreqId="+rs.getString("pro_freq_id")+"&amp;frmDate="+hmProjectData.get("PRO_START_DATE")+"&toDate="+hmProjectData.get("PRO_END_DATE")+"&downloadSubmit=0\" class=\"xls\">Download</a>"); //14
				
//				String[] tmpCAStatus = clientApprovalStatus.split("::::");
				if(uF.parseToInt(billApproveStatusAndDate[0]) == 1 || uF.parseToInt(billApproveStatusAndDate[0]) == 0) {
					innerList.add("Not updated"); //15
				} else if(uF.parseToInt(billApproveStatusAndDate[0]) == -1) {
					innerList.add("No, <a href=\"javascript:void(0);\" onclick=\"viewComments('"+rs.getString("pro_id")+"', '"+rs.getString("pro_freq_id")+"')\">Comment</a> (" +billApproveStatusAndDate[1]+")"); //15
				} else if(uF.parseToInt(billApproveStatusAndDate[0]) == 2) {
					innerList.add("Yes, ("+billApproveStatusAndDate[1]+")"); //15
				}
				
				String[] tmpSPStatus = submitPendingCnt.split("::::");
				StringBuilder sbSP = new StringBuilder();
				sbSP.append("<span style='float:left; margin-right:9px; color:green; font-weight: bold; font-size: 13px;'>"+tmpSPStatus[0]+" <img title=\"Submitted\" style='height:11px;' src=\"images1/icons/hd_tick_20x20.png\"></span>");
				sbSP.append("<span style='float:left; color:red; font-weight: bold; font-size: 13px;'>"+tmpSPStatus[1]+" <img title=\"Pending\" style='height:10px;' src=\"images1/icons/hd_cross_16x16.png\"></span>");
				innerList.add(sbSP.toString()); //16
				innerList.add(billApproveStatusAndDate[0]); //17
				
				projectList.add(innerList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("projectList ===>>>> " + projectList);
			
			int proCnt = projectList.size();
			request.setAttribute("proCnt", proCnt+"");
			
			if(projectList.size() > 0) {
				int proCount = projectList.size()/100;
				if(projectList.size()%100 != 0) {
					proCount++;
				}
				
				request.setAttribute("proCount", proCount+"");
				
//				System.out.println("=======pro wise proCount========"+proCount);
//				System.out.println("=======proCnt========"+proCnt);
//				System.out.println("=======uF.parseToInt(getMinLimit())========"+uF.parseToInt(getMinLimit()));
				if(projectList.size() > 100){
					int nStart = 0;
					int nEnd = 100;
					if(uF.parseToInt(getMinLimit())>0){
						nStart = uF.parseToInt(getMinLimit());
						nEnd = uF.parseToInt(getMinLimit())+100;
					}
					
//					System.out.println("=======nStart========"+nStart);
//					System.out.println("=======nEnd========"+nEnd);
					if(nEnd > projectList.size()){
						nEnd = projectList.size();
//						System.out.println("=======after nEnd========"+nEnd);
					}
					
//					System.out.println("=======alReport========"+alReport.toString());
					projectList = projectList.subList(nStart, nEnd);
				}
			} 
			
			request.setAttribute("projectList", projectList);
			
//			System.out.println("projectList ========>>> " + projectList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	
	private String getTimeSheetBillApprovalStatus(Connection con, String proId, String freqStDate, String freqEndDate) {
		 
		PreparedStatement pst = null; 
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		String strStatus = "0";
		try {
			
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
			
			if(sbTasks.toString().length() > 0) {
				pst = con.prepareStatement("select is_billable_approved,cust_denied_date,billable_approve_date from task_activity where " +
					"activity_id in("+sbTasks.toString()+") and task_date between ? and ? order by is_billable_approved");
				pst.setDate(1, uF.getDateFormat(freqStDate, DBDATE));
				pst.setDate(2, uF.getDateFormat(freqEndDate, DBDATE));
				rs = pst.executeQuery();
				while(rs.next()) {
					if(rs.getInt("is_billable_approved") == -1) {
						strStatus = rs.getString("is_billable_approved")+"::::"+uF.getDateFormat(rs.getString("cust_denied_date"), DBDATE, CF.getStrReportDateFormat());
						break;
					} else if(rs.getInt("is_billable_approved") == 0) {
						strStatus = rs.getString("is_billable_approved")+"::::0";
						break;
					} else if(rs.getInt("is_billable_approved") == 1) {
						strStatus = rs.getString("is_billable_approved")+"::::"+uF.getDateFormat(rs.getString("billable_approve_date"), DBDATE, CF.getStrReportDateFormat());
						break;
					} else {
						strStatus = rs.getString("is_billable_approved")+"::::"+uF.getDateFormat(rs.getString("billable_approve_date"), DBDATE, CF.getStrReportDateFormat());
					}
				}
				rs.close();
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
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
		return strStatus;
	}





	private String getCustomerTimeSheetApprovalStatus(Connection con, String proId, String freqStDate, String freqEndDate) {
		 
		PreparedStatement pst = null; 
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		String pendingCnt = "0";
		try {
			
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
			
			if(sbTasks.toString().length() > 0) {
				boolean flag1 = false;
				pst = con.prepareStatement("select is_billable_approved from task_activity where is_billable_approved = 2 and " +
						"activity_id in("+sbTasks.toString()+") and task_date between ? and ? group by is_billable_approved");
				pst.setDate(1, uF.getDateFormat(freqStDate, DBDATE));
				pst.setDate(2, uF.getDateFormat(freqEndDate, DBDATE));
				rs = pst.executeQuery();
//				System.out.println("pst ===> " + pst);
				while(rs.next()) {
					flag1 = true;
//					pendingCnt = "1";
				}
				rs.close();
				pst.close();
				
				boolean flag = false;
				pst = con.prepareStatement("select is_billable_approved from task_activity where activity_id in("+sbTasks.toString()+") and " +
						"task_date between ? and ? and is_billable_approved = 1");
				pst.setDate(1, uF.getDateFormat(freqStDate, DBDATE));
				pst.setDate(2, uF.getDateFormat(freqEndDate, DBDATE));
				rs = pst.executeQuery();
//				System.out.println("pst ===> " + pst);
				while(rs.next()) {
					flag = true;
				}
				rs.close();
				pst.close();
				
				if(getTimesheetType() != null && getTimesheetType().equals("PA")) {
					if(flag1) {
						pendingCnt = "0";
					} else {
						pendingCnt = "1";
					}
				} else {
					if(flag) {
						pendingCnt = "1";
					} else {
						pendingCnt = "0";
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pendingCnt;
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
				pst.setDate(1, uF.getDateFormat(freqStDate, DBDATE));
				pst.setDate(2, uF.getDateFormat(freqEndDate, DBDATE));
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



	@SuppressWarnings("unused")
	private String getProjectTimeSheetClientApprovalStatus(Connection con, String proId, String freqStDate, String freqEndDate) {
		 
		PreparedStatement pst = null; 
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		String unapprvedStatus = "1::::-";
		try {
			
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
			
			if(sbTasks.toString().length() > 0) {
				boolean flag1 = false;
				String strUADate = "-";
				pst = con.prepareStatement("select billable_approve_date from task_activity where is_billable_approved <= 1 and activity_id " +
					" in ("+sbTasks.toString()+") and task_date between ? and ? group by billable_approve_date order by billable_approve_date");
				pst.setDate(1, uF.getDateFormat(freqStDate, DBDATE));
				pst.setDate(2, uF.getDateFormat(freqEndDate, DBDATE));
				rs = pst.executeQuery();
//				System.out.println("pst ===> " + pst);
				while(rs.next()) {
					flag1 = true;
					if(rs.getString("billable_approve_date") != null) {
						strUADate = uF.getDateFormat(rs.getString("billable_approve_date"), DBDATE, CF.getStrReportDateFormat());
					}
				}
				rs.close();
				pst.close();
//				System.out.println("strUADate ===>> " + strUADate);
				boolean flag = false;
				pst = con.prepareStatement("select is_billable_approved from task_activity where activity_id in("+sbTasks.toString()+") and " +
						"task_date between ? and ? ");
				pst.setDate(1, uF.getDateFormat(freqStDate, DBDATE));
				pst.setDate(2, uF.getDateFormat(freqEndDate, DBDATE));
				rs = pst.executeQuery();
//				System.out.println("pst ===> " + pst);
				while(rs.next()) {
					flag = true;
				}
				rs.close();
				pst.close();
				
				if(flag1 && flag) {
					unapprvedStatus = "1::::"+strUADate;
				} else if(!flag1 && flag) {
					pst = con.prepareStatement("select billable_approve_date from task_activity where is_billable_approved = 2 and activity_id " +
						" in ("+sbTasks.toString()+") and task_date between ? and ? group by billable_approve_date order by billable_approve_date");
					pst.setDate(1, uF.getDateFormat(freqStDate, DBDATE));
					pst.setDate(2, uF.getDateFormat(freqEndDate, DBDATE));
					rs = pst.executeQuery();
//					System.out.println("pst ===> " + pst);
					while(rs.next()) {
						flag1 = true;
						strUADate = uF.getDateFormat(rs.getString("billable_approve_date"), DBDATE, CF.getStrReportDateFormat());
					}
					rs.close();
					pst.close();
//					System.out.println("strUADate 1 ===>> " + strUADate);
					
					unapprvedStatus = "0::::"+strUADate;
				} else {
					unapprvedStatus = "1::::"+strUADate;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return unapprvedStatus;
	}


	private void getSelectedFilterEmpwise(UtilityFunctions uF) {
		
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
//		System.out.println("getPaycycle() ===>> " + getPaycycle());
		if(getPaycycle() != null && !getPaycycle().equals("") && !getPaycycle().equalsIgnoreCase("null")) {
			String[] strPayCycleDates = null;
			alFilter.add("PAYCYCLE");
			strPayCycleDates = getPaycycle().split("-");
			String strPaycycle = "Cycle "+strPayCycleDates[2]+", "+ uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat())+" - "+ uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat());
			hmFilter.put("PAYCYCLE", strPaycycle);
		}
		
		if(getStrYear()!=null && !getStrYear().equals("")) {
			alFilter.add("YEAR");
			hmFilter.put("YEAR", getStrYear());
		}
		
		if(getStrMonth()!=null && !getStrMonth().equals("")) {
			alFilter.add("MONTH");
			hmFilter.put("MONTH", uF.getMonth(uF.parseToInt(getStrMonth())));
		}
		
		String selectedFilter=getSelectedFilter(uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	private void getSelectedFilter(UtilityFunctions uF, Map<String, String> hmEmpName) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		
		alFilter.add("PROJECT");
		if(getProId()!=null) {
			String strProjects="";
			int k=0;
			for(int i=0;projectdetailslist!=null && i<projectdetailslist.size();i++) {
				for(int j=0;j<getProId().length;j++) {
					if(getProId()[j].equals(projectdetailslist.get(i).getProjectID())) {
						if(k==0) {
							strProjects=projectdetailslist.get(i).getProjectName();
						} else {
							strProjects+=", "+projectdetailslist.get(i).getProjectName();
						}
						k++;
					}
				}
			}
			if(strProjects!=null && !strProjects.equals("")) {
				hmFilter.put("PROJECT", strProjects);
			} else {
				hmFilter.put("PROJECT", "All Projects");
			}
		} else {
			hmFilter.put("PROJECT", "All Projects");
		}
		
		
		alFilter.add("PROJECT_OWNER");
		if(getProjectOwner()!=null) {
			String strProOwner="";
			int k=0;
			for(int i=0;projectOwnerList!=null && i<projectOwnerList.size();i++) {
				for(int j=0;j<getProjectOwner().length;j++) {
					if(getProjectOwner()[j].equals(projectOwnerList.get(i).getProOwnerId())) {
						if(k==0) {
							strProOwner=projectOwnerList.get(i).getProOwnerName();
						} else {
							strProOwner+=", "+projectOwnerList.get(i).getProOwnerName();
						}
						k++;
					}
				}
			}
			if(strProOwner!=null && !strProOwner.equals("")) {
				hmFilter.put("PROJECT_OWNER", strProOwner);
			} else {
				hmFilter.put("PROJECT_OWNER", "All Owners");
			}
		} else {
			hmFilter.put("PROJECT_OWNER", "All Owners");
		}
		
		
		if(strUserType != null && !strUserType.equals(CUSTOMER)) {
			alFilter.add("CLIENT");
			if(getClient()!=null) {
				String strClient="";
				int k=0;
				for(int i=0; clientList!=null && i<clientList.size();i++) {
					for(int j=0;j<getClient().length;j++) {
						if(getClient()[j].equals(clientList.get(i).getClientId())) {
							if(k==0) {
								strClient=clientList.get(i).getClientName();
							} else {
								strClient+=", "+clientList.get(i).getClientName();
							}
							k++;
						}
					}
				}
				if(strClient!=null && !strClient.equals("")) {
					hmFilter.put("CLIENT", strClient);
				} else {
					hmFilter.put("CLIENT", "All Customers");
				}
			} else {
				hmFilter.put("CLIENT", "All Customers");
			}
		}
		
		alFilter.add("PROJECT_TYPE");
		if(getProjectType()!=null) {
			String strProType="";
			int k=0;
			for(int i=0; billingBasisList!=null && i<billingBasisList.size();i++) {
				for(int j=0;j<getProjectType().length;j++) {
					if(getProjectType()[j].equals(billingBasisList.get(i).getBillingId())) {
						if(k==0) {
							strProType=billingBasisList.get(i).getBillingName();
						} else {
							strProType+=", "+billingBasisList.get(i).getBillingName();
						}
						k++;
					}
				}
			}
			if(strProType!=null && !strProType.equals("")) {
				hmFilter.put("PROJECT_TYPE", strProType);
			} else {
				hmFilter.put("PROJECT_TYPE", "All Type");
			}
		} else {
			hmFilter.put("PROJECT_TYPE", "All Type");
		}
		
		
		alFilter.add("PROJECT_FREQUENCY");
		if(getProjectFrequency()!=null) {
			String strProFreq="";
			int k=0;
			for(int i=0; billingFreqList!=null && i<billingFreqList.size();i++) {
				for(int j=0;j<getProjectFrequency().length;j++) {
					if(getProjectFrequency()[j].equals(billingFreqList.get(i).getBillingId())) {
						if(k==0) {
							strProFreq=billingFreqList.get(i).getBillingName();
						} else {
							strProFreq+=", "+billingFreqList.get(i).getBillingName();
						}
						k++;
					}
				}
			}
			if(strProFreq!=null && !strProFreq.equals("")) {
				hmFilter.put("PROJECT_FREQUENCY", strProFreq);
			} else {
				hmFilter.put("PROJECT_FREQUENCY", "All Frequency");
			}
		} else {
			hmFilter.put("PROJECT_FREQUENCY", "All Frequency");
		}
		
		
		/*alFilter.add("SORTBY");
		if(getSortBy()!=null ) {
			String strSortBy="";
			if(uF.parseToInt(getSortBy()) == 1) {
				strSortBy = "Latest on top";
			} else if(uF.parseToInt(getSortBy()) == 2) {
				strSortBy = "Oldest on top";
			} else if(uF.parseToInt(getSortBy()) == 3) {
				strSortBy = "A-Z";
			} else if(uF.parseToInt(getSortBy()) == 4) {
				strSortBy = "Z-A";
			}
			if(strSortBy!=null && !strSortBy.equals("")) {
				hmFilter.put("SORTBY", strSortBy);
			} else {
				hmFilter.put("SORTBY", "Latest on top");
			}
		} else {
			hmFilter.put("SORTBY", "Latest on top");
		}*/
		
		String selectedFilter=getSelectedFilter(uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	public String getSelectedFilter(UtilityFunctions uF, List<String> alFilter, Map<String, String> hmFilter) {
		StringBuilder sbFilter=new StringBuilder("<strong>Filter Summary: </strong>");
		
		for(int i=0;alFilter!=null && i<alFilter.size();i++) {
			if(i>0) {
					sbFilter.append(", ");
			} 
//			    
			if(alFilter.get(i).equals("PROJECT")) {
				sbFilter.append("<strong>PROJECT:</strong> ");
				sbFilter.append(hmFilter.get("PROJECT"));
//			 
			} else if(alFilter.get(i).equals("PROJECT_OWNER")) {
				sbFilter.append("<strong>PROJECT OWNER:</strong> ");
				sbFilter.append(hmFilter.get("PROJECT_OWNER"));
			
			} else if(alFilter.get(i).equals("CLIENT")) {
				sbFilter.append("<strong>CUSTOMER:</strong> ");
				sbFilter.append(hmFilter.get("CLIENT"));
			
			} else if(alFilter.get(i).equals("PROJECT_TYPE")) {
				sbFilter.append("<strong>PROJECT TYPE:</strong> ");
				sbFilter.append(hmFilter.get("PROJECT_TYPE"));
			
			} else if(alFilter.get(i).equals("PROJECT_FREQUENCY")) {
				sbFilter.append("<strong>PROJECT FREQUENCY:</strong> ");
				sbFilter.append(hmFilter.get("PROJECT_FREQUENCY"));
			
			} else if(alFilter.get(i).equals("PAYCYCLE")) {
				sbFilter.append("<strong>PAYCYCLE:</strong> ");
				sbFilter.append(hmFilter.get("PAYCYCLE"));
				
			} else if(alFilter.get(i).equals("YEAR")) {
				sbFilter.append("<strong>YEAR:</strong> ");
				sbFilter.append(hmFilter.get("YEAR"));
				
			} else if(alFilter.get(i).equals("MONTH")) {
				sbFilter.append("<strong>MONTH:</strong> ");
				sbFilter.append(hmFilter.get("MONTH"));
			} 
		}
		return sbFilter.toString();
	}
	


	private String getProjectTimeSheetApprovalStatus(Connection con, String proId, String freqStDate, String freqEndDate) {
		 
		PreparedStatement pst = null; 
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		String pendingCnt = "0";
		try {
			
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
			
			if(sbTasks.toString().length() > 0) {
				boolean flag1 = false;
				pst = con.prepareStatement("select is_billable_approved from task_activity where is_billable_approved <= 1 and " +
						"activity_id in("+sbTasks.toString()+") and task_date between ? and ? group by is_billable_approved");
				pst.setDate(1, uF.getDateFormat(freqStDate, DBDATE));
				pst.setDate(2, uF.getDateFormat(freqEndDate, DBDATE));
				rs = pst.executeQuery();
//				System.out.println("pst flag1 ===> " + pst);
				while(rs.next()) {
					flag1 = true;
//					pendingCnt = "1";
				}
				rs.close();
				pst.close();
				
				boolean flag = false;
				pst = con.prepareStatement("select is_billable_approved from task_activity where activity_id in("+sbTasks.toString()+") and " +
						"task_date between ? and ? ");
				pst.setDate(1, uF.getDateFormat(freqStDate, DBDATE));
				pst.setDate(2, uF.getDateFormat(freqEndDate, DBDATE));
				rs = pst.executeQuery();
//				System.out.println("pst flag ===> " + pst);
				while(rs.next()) {
					flag = true;
				}
				rs.close();
				pst.close();
				
				if(getTimesheetType() != null && getTimesheetType().equals("PA")) {
					if(!flag1 && flag) {
						pendingCnt = "0";
					} else {
						pendingCnt = "1";
					}
				} else {
					if((flag1 && flag) || !flag) {
						pendingCnt = "1";
					} else {
						pendingCnt = "0";
					}
				}
				
				
//				if(!flag1 && flag) {
//					pendingCnt = "0";
//				} else {
//					pendingCnt = "1";
//				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pendingCnt;
	}



	private Map<String, String> getProjectFreqActualAndBillableEfforts(Connection con, String proId, Map<String, String> hmProjectData) {
		 
		PreparedStatement pst = null; 
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, String> hmProjectEfforts = new HashMap<String, String>();
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
			
			List<String> tasksList = new ArrayList<String>();
			for(int i=0; empList!= null && i<empList.size(); i++) {
				pst = con.prepareStatement("select task_id from activity_info where pro_id = ? and resource_ids like '%,"+empList.get(i)+",%'");				
				pst.setInt(1, uF.parseToInt(proId));
//				pst.setInt(2, uF.parseToInt(empList.get(i)));
				rs = pst.executeQuery();			 
//				System.out.println("pst ===> " + pst);
				while(rs.next()) {
					if(!tasksList.contains(rs.getString("task_id")))
						tasksList.add(rs.getString("task_id"));
					}
				rs.close();
				pst.close();
				}
//			System.out.println("tasksList ===> " + tasksList);
			
			StringBuilder sbtaskIds = null;
			for(int i=0; tasksList != null && i<tasksList.size(); i++) {
				if(sbtaskIds == null) {
					sbtaskIds = new StringBuilder();
					sbtaskIds.append(tasksList.get(i));
				} else {
					sbtaskIds.append(","+tasksList.get(i));
				}
			}
			if(sbtaskIds == null) {
				sbtaskIds = new StringBuilder();
			}
			
			double totActualHrs = 0;
			double totBillableHrs = 0;
			double totActualDays = 0;
			double totBillableDays = 0;
			Map<String, String> hmApprovedBy = new HashMap<String, String>();
			for(int i=0; tasksList != null && i<tasksList.size(); i++) {
				pst = con.prepareStatement("select sum(actual_hrs) as actual_hrs, approved_by from task_activity where activity_id=? and is_approved = 2 and task_date between ? and ? group by approved_by");				
				pst.setInt(1, uF.parseToInt(tasksList.get(i)));
				pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(hmProjectData.get("PRO_END_DATE"), DATE_FORMAT));
				rs = pst.executeQuery();			 
//				System.out.println(proId+ " -- pst ===> " + pst);
				while(rs.next()) {
					hmApprovedBy.put(rs.getString("approved_by"), rs.getString("approved_by"));
					totActualHrs += rs.getDouble("actual_hrs");
				}
				rs.close();
				pst.close();
			}
			
			
			if(sbtaskIds != null && sbtaskIds.toString().length()>0) {
				pst = con.prepareStatement("select count(distinct task_date) as days from task_activity where activity_id in ("+sbtaskIds.toString()+") and is_approved = 2 and task_date between ? and ? group by emp_id");
				pst.setDate(1, uF.getDateFormat(hmProjectData.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_END_DATE"), DATE_FORMAT));
				rs = pst.executeQuery();			 
//				System.out.println(proId+ " -- pst ===> " + pst);
				while(rs.next()) {
//					totActualDays++;
					totActualDays += rs.getInt("days");
				}
				rs.close();
				pst.close();
			}
			
			
			
			for(int i=0; tasksList != null && i<tasksList.size(); i++) {
				pst = con.prepareStatement("select sum(billable_hrs) as billable_hrs, count(distinct task_date) as task_date, approved_by from task_activity where activity_id=? and is_approved = 2 and is_billable = true and task_date between ? and ? group by approved_by");				
				pst.setInt(1, uF.parseToInt(tasksList.get(i)));
				pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(hmProjectData.get("PRO_END_DATE"), DATE_FORMAT));
				rs = pst.executeQuery();			 
//				System.out.println(proId+ " -- pst ===> " + pst);
				while(rs.next()) {
					totBillableHrs += rs.getDouble("billable_hrs");
				}
				rs.close();
				pst.close();
			}
			
			if(sbtaskIds != null && sbtaskIds.toString().length()>0) {
				pst = con.prepareStatement("select count(distinct task_date) as days from task_activity where activity_id in ("+sbtaskIds.toString()+") and is_approved = 2 and is_billable = true and task_date between ? and ? group by emp_id");
				pst.setDate(1, uF.getDateFormat(hmProjectData.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_END_DATE"), DATE_FORMAT));
				rs = pst.executeQuery();			 
//				System.out.println(proId+ " -- pst ===> " + pst);
				while(rs.next()) {
//					totBillableDays++;
					totBillableDays += rs.getInt("days");
				}
				rs.close();
				pst.close();
			}
			
//			System.out.println(" totActualDays ==>> " + totActualDays);
//			System.out.println(" totBillableDays ==>> " + totBillableDays);
//			
//			System.out.println(" totActualHrs ==>> " + totActualHrs);
//			System.out.println(" totBillableHrs ==>> " + totBillableHrs);
//			
//			System.out.println("PRO_BILL_DAYS_TYPE ===>> " + hmProjectData.get("PRO_BILL_DAYS_TYPE"));
//			System.out.println("PRO_HOURS_FOR_BILL_DAY ===>> " + hmProjectData.get("PRO_HOURS_FOR_BILL_DAY"));
			
			if(hmProjectData.get("PRO_BILL_DAYS_TYPE") != null && hmProjectData.get("PRO_BILL_DAYS_TYPE").equals("2") && uF.parseToDouble(hmProjectData.get("PRO_HOURS_FOR_BILL_DAY")) > 0) {
				totActualDays = uF.parseToDouble(uF.roundOffInTimeInHoursMins(totActualHrs)) / uF.parseToDouble(hmProjectData.get("PRO_HOURS_FOR_BILL_DAY"));
				totBillableDays = uF.parseToDouble(uF.roundOffInTimeInHoursMins(totBillableHrs)) / uF.parseToDouble(hmProjectData.get("PRO_HOURS_FOR_BILL_DAY"));
//				System.out.println(" totActualDays in ==>> " + totActualDays);
//				System.out.println(" totBillableDays in ==>> " + totBillableDays);
			}

			StringBuilder sbApproveBy = null;
			Iterator<String> it = hmApprovedBy.keySet().iterator();
			
			while(it.hasNext()) {
				String approveby = it.next();
				if(uF.parseToInt(approveby) > 0) {
					if(sbApproveBy == null) {
						sbApproveBy = new StringBuilder();
						sbApproveBy.append(CF.getEmpNameMapByEmpId(con, approveby));
					} else {
						sbApproveBy.append(","+CF.getEmpNameMapByEmpId(con, approveby));
					}
				}
			}
			if(sbApproveBy == null) {
				sbApproveBy = new StringBuilder();
			}
			
//			System.out.println("totActualHrs ===>>>> " + totActualHrs);
//			System.out.println("totBillableHrs ===>>>> " + totBillableHrs);
//			System.out.println("totActualDays ===>>>> " + totActualDays);
//			System.out.println("totBillableDays ===>>>> " + totBillableDays);
			
			hmProjectEfforts.put("ACTUAL_HRS", ""+uF.roundOffInTimeInHoursMins(totActualHrs));
			hmProjectEfforts.put("BILLABLE_HRS", ""+uF.roundOffInTimeInHoursMins(totBillableHrs));
			hmProjectEfforts.put("ACTUAL_DAYS", ""+uF.formatIntoOneDecimalWithOutComma(totActualDays));
			hmProjectEfforts.put("BILLABLE_DAYS", ""+uF.formatIntoOneDecimalWithOutComma(totBillableDays));
			
			hmProjectEfforts.put("APPROVED_BY", sbApproveBy.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmProjectEfforts;
	}



//	private Map<String, String> getProjectDetails(Connection con, String proId) {
//		 
//		PreparedStatement pst = null; 
//		ResultSet rs = null;
//		UtilityFunctions uF = new UtilityFunctions();
//		Map<String, String> hmProjectData = new HashMap<String, String>();
//		try {
//			
//			pst = con.prepareStatement("select * from projectmntnc where pro_id = ?");				
//			pst.setInt(1, uF.parseToInt(proId));
//			rs = pst.executeQuery();			 
////			System.out.println("pst ===> " + pst);
//			while(rs.next()) {
//				hmProjectData.put("PRO_ID", rs.getString("pro_id"));
//				hmProjectData.put("PRO_NAME", rs.getString("pro_name"));
//				hmProjectData.put("PRO_CUSTOMER_NAME", CF.getClientNameById(con, rs.getString("client_id")));
//				hmProjectData.put("PRO_OWNER_NAME", CF.getEmpNameMapByEmpId(con, rs.getString("project_owner")));
//				hmProjectData.put("PRO_BILLING_TYPE", getBillinType(rs.getString("billing_type")));
//				hmProjectData.put("PRO_BILLING_FREQUENCY", CF.getBillinFreq(rs.getString("billing_kind"), rs.getString("billing_type")));
//				hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));
//				hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));
//				hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
//				hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return hmProjectData;
//	}

//	private String getBillinType(String billingType) {
//		String billType ="";
//		
//		if(billingType != null && billingType.endsWith("F")) {
//			billType ="Fixed";
//		} else if(billingType != null && billingType.endsWith("H")) {
//			billType ="Hourly";
//		} if(billingType != null && billingType.endsWith("D")) {
//			billType ="Daily";
//		} 
//	return billType;	
//	}

	
//	private String getBillinFreq(String billFreq) {
//		String billingFreq ="";
//		
//		if(billFreq != null && billFreq.endsWith("M")) {
//			billingFreq ="Monthly";
//		} else if(billFreq != null && billFreq.endsWith("B")) {
//			billingFreq ="Biweekly";
//		} else if(billFreq != null && billFreq.endsWith("W")) {
//			billingFreq ="Weekly";
//		} else  if(billFreq != null && billFreq.endsWith("O")) {
//			billingFreq ="One Time";
//		} 
//	return billingFreq;	
//	}

	private void getTimesheet2() {
		 
		Connection con = null;
		PreparedStatement pst = null; 
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("select financial_year_from FROM financial_year_details order by financial_year_from limit 1");
			rs = pst.executeQuery();
			String fYear = "";
			while(rs.next()) {
				fYear = rs.getString("financial_year_from");
			}
			rs.close();
			pst.close();
			
			int fStartYear = uF.parseToInt(uF.getDateFormat(fYear, DBDATE, "yyyy"));
			yearList = new FillYears().fillYears(uF.getCurrentDate(CF.getStrTimeZone()), ""+fStartYear); 
			
			String[] strPayCycleDates = null;
//			System.out.println("getPaycycle() ===>> " + getPaycycle());
//			System.out.println("getFilterBy() ===>> " + getFilterBy());
			int maxDays = 0;
			if(getFilterBy() == null || getFilterBy().equals("") || getFilterBy().equals("P")) {
				if (getPaycycle() != null && !getPaycycle().equals("") && !getPaycycle().equalsIgnoreCase("null")) {
					strPayCycleDates = getPaycycle().split("-");
				} else {
					strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
//					System.out.println("strPayCycleDates ===>> " + strPayCycleDates);
					setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
				}
				maxDays = 32;
				setStrYear("");
				setStrMonth("");
				setStrWeek("");
			} else if(getFilterBy() != null && getFilterBy().equals("O")) {
				if(getStrWeek()==null || getStrWeek().equals("")) {
					String strDate = "01/"+getStrMonth()+"/"+getStrYear();
					String monthminMaxDates = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
					monthminMaxDates = monthminMaxDates+"::::00";
					strPayCycleDates = monthminMaxDates.split("::::");
				} else if(getStrWeek()!=null && !getStrWeek().equals("")) {
					
				}
				
				setPaycycle("");
//				CF.getPayCycleFromDate(con, strDate, strTimeZone, CF, orgId)
			}
			
//			System.out.println("strUserType ===>> " + strUserType);
			
			getSelectedFilterEmpwise(uF);
			
//			System.out.println("paycycle====>"+strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
			
			Calendar cal1 = GregorianCalendar.getInstance();
			cal1.setTime(uF.getDateFormatUtil(strPayCycleDates[0], DATE_FORMAT));

			if(getFilterBy() != null && getFilterBy().equals("O")) {
				maxDays = cal1.getActualMaximum(Calendar.DATE);
			}
			List<String> alDates = new ArrayList<String>();
			
			for(int i=0; i<maxDays; i++) {
				String strDate = cal1.get(Calendar.DATE)+"/"+(cal1.get(Calendar.MONTH)+1)+"/"+cal1.get(Calendar.YEAR);
				alDates.add(uF.getDateFormat(strDate, DATE_FORMAT, DATE_FORMAT));
				if(strPayCycleDates[1]!=null && strPayCycleDates[1].equalsIgnoreCase(uF.getDateFormat(strDate, DATE_FORMAT, DATE_FORMAT))) {
					break;
				}
				cal1.add(Calendar.DATE, 1);
			}
			
			Map<String, String> hmEmpWlocationMap = CF.getEmpWlocationMap(con);
			String locationID = hmEmpWlocationMap.get(strSessionEmpId);
			
//			pst = con.prepareStatement("select effective_id,max(member_position) as member_position from work_flow_details wf where is_approved=0 " +
//					" and effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in(select ta.task_id from task_activity ta " +
//					"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id");			
//			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			rs = pst.executeQuery();
//			Map<String, String> hmMaxPositionApproval = new HashMap<String, String>();			
//			while(rs.next()) {
//				hmMaxPositionApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
//			}
//			rs.close();
//			pst.close();
			
//			select ta.emp_id from task_activity ta, work_flow_details wfd where ta.task_id = wfd.effective_id and ta.task_date between '2018-01-26' and '2018-02-25' and ta.is_approved = 1 and wfd.emp_id=617 group by ta.emp_id
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select emp_id from task_activity where task_date between ? and ? ");
			sbQuery.append("select ta.emp_id from task_activity ta, work_flow_details wfd where ta.task_id = wfd.effective_id and " +
				"ta.task_date between ? and ? ");
			if(getTimesheetType() != null && getTimesheetType().equals("EU")) {
				sbQuery.append(" and ta.is_approved = 1 ");
			} else if(getTimesheetType() != null && getTimesheetType().equals("EA")) {
				sbQuery.append(" and ta.is_approved = 2 ");
			}
			if(strUserType != null && !strUserType.equals(ADMIN) && !strUserType.equals(OTHER_HR)) {
//				sbQuery.append(" and wfd.emp_id="+uF.parseToInt(strSessionEmpId)+" ");
				sbQuery.append(" and wfd.effective_id in (select wfd.effective_id from work_flow_details wfd where wfd.emp_id="+uF.parseToInt(strSessionEmpId)+") ");
			}
			sbQuery.append(" group by ta.emp_id ");
			pst = con.prepareStatement(sbQuery.toString());	
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			if(strUserType != null && !strUserType.equals(ADMIN)) {
//				pst.setDate(3, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//				pst.setDate(4, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//				pst.setInt(5,uF.parseToInt(strSessionEmpId));
//			}
//			System.out.println("=======pst========"+pst);
			rs = pst.executeQuery();
			List<String> alEmp = new ArrayList<String>();
			while(rs.next()) {
				alEmp.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
//			System.out.println("=======alEmp========"+alEmp);
			
			if(alEmp!=null && alEmp.size() > 0){
				String strEmpIds = StringUtils.join(alEmp.toArray(),",");
			
				pst = con.prepareStatement("select emp_id from task_activity ta where task_date between ? and ? and is_approved = 2 and emp_id in("+strEmpIds+") group by emp_id ");
//				pst = con.prepareStatement("select emp_id from task_activity ta left join activity_info ai on ta.activity_id = ai.task_id where " +
//					"ta.emp_id =683 and (ai.resource_ids like '%,683,%' or activity_id = 0) where task_date between ? and ? and is_approved = 2 and emp_id in("+strEmpIds+") group by emp_id ");
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				rs = pst.executeQuery();
				Map<String, String> hmIsApproved = new HashMap<String, String>();
				while(rs.next()) {
					hmIsApproved.put(rs.getString("emp_id"), ""+true);
				}
				rs.close();
				pst.close();
				
				
				pst = con.prepareStatement("select effective_id,min(member_position) as member_position from work_flow_details wfd " +
						" where is_approved=0 and effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in(select ta.task_id from task_activity ta " +
						"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ? and ta.emp_id in("+strEmpIds+"))  group by effective_id");	
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
						"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ? and ta.emp_id in("+strEmpIds+")) group by effective_id ");
				pst.setInt(1,uF.parseToInt(strSessionEmpId));	
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));			
				rs = pst.executeQuery();			
				Map<String, String> hmMemApprovalPos = new HashMap<String, String>();			
				while(rs.next()) {
					hmMemApprovalPos.put(rs.getString("effective_id"), rs.getString("member_position"));
				}
				rs.close();
				pst.close();
	//			System.out.println("=======3========"+new Date());
				
	//			pst = con.prepareStatement("select effective_id,member_position,emp_id from work_flow_details " +
	//					"where effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in(select ta.task_id from task_activity ta " +
	//					"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?) order by effective_id");	
	//			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
	//			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
	//			rs = pst.executeQuery();			
	//			Map<String, String> hmNextApprovalMem = new HashMap<String, String>();			
	//			while(rs.next()) {
	//				hmNextApprovalMem.put(rs.getString("effective_id")+"_"+rs.getString("member_position"), rs.getString("emp_id"));
	//			}
	//			rs.close();
	//			pst.close();
	//			
	////			System.out.println("=======4========"+new Date());
				
				pst = con.prepareStatement("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
						" and emp_id=? and effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in(select ta.task_id from task_activity ta " +
						"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ? and ta.emp_id in("+strEmpIds+")) group by effective_id,is_approved");
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
					"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ? and ta.emp_id in("+strEmpIds+")) group by effective_id,emp_id");	
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
						"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ? and ta.emp_id in("+strEmpIds+")) group by effective_id,emp_id");	
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
						"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ? and ta.emp_id in("+strEmpIds+")) order by effective_id,member_position");	
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
				
				sbQuery = new StringBuilder();
				sbQuery.append("select ud.emp_id from user_details ud,employee_official_details eod, employee_personal_details epd where " +
					" ud.emp_id = eod.emp_id ");
				if(strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS) != null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") ");
				} else {
					sbQuery.append(" and wlocation_id = "+uF.parseToInt(locationID)+" ");
				}
				sbQuery.append(" and ud.emp_id = epd.emp_per_id and ud.status = 'ACTIVE' and eod.emp_id in("+strEmpIds+")");
				pst = con.prepareStatement(sbQuery.toString());
	//			pst.setInt(1, uF.parseToInt(locationID));
				rs = pst.executeQuery();			
				Map<String, String> hmEmpByLocation = new HashMap<String, String>();			
				while(rs.next()) {
					hmEmpByLocation.put(rs.getString("emp_id"), rs.getString("emp_id"));
				}
				rs.close();
				pst.close();
				
	//			System.out.println("=======11========"+new Date());
				
				Map<String, String> hmEmployeeName = CF.getEmpNameMap(con, null, null);
				
	//			Map<String, String> hmActualWorkingDays = CF.getEmpNameMap(null, null);
				Map<String, String> hmActualWorkingDays = new HashMap<String, String>();
				if(strUserType != null && (strUserType.equals(HRMANAGER) || strUserType.equals(ADMIN) || strUserType.equals(OTHER_HR)) && (getPageType() == null || !getPageType().equals("MP"))) {
					pst = con.prepareStatement("select count(distinct task_date) as days, sum(actual_hrs) as actual_hrs, sum(billable_hrs) as billable_hrs, " +
						"emp_id from task_activity ta where task_date between ? and ? and ta.is_approved > 0 and ta.emp_id in("+strEmpIds+") group by ta.emp_id");
					pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				} else {
					pst = con.prepareStatement("select count(distinct task_date) as days, sum(actual_hrs) as actual_hrs, sum(billable_hrs) as billable_hrs, " +
						"ta.emp_id, submited_date from task_activity ta, work_flow_details wfd where task_date between ? and ? and ta.task_id = wfd.effective_id " +
						"and wfd.emp_id = ? and ta.is_approved > 0 and ta.emp_id in("+strEmpIds+") group by ta.emp_id,submited_date");
					pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
				}
				rs = pst.executeQuery();
				while(rs.next()) {
					if(strUserType != null && (strUserType.equals(HRMANAGER) || strUserType.equals(ADMIN) || strUserType.equals(OTHER_HR)) && (getPageType() == null || !getPageType().equals("MP"))) {
						hmActualWorkingDays.put(rs.getString("emp_id")+"_DAYS", rs.getString("days"));
						hmActualWorkingDays.put(rs.getString("emp_id")+"_ACT_HRS", uF.getTotalTimeMinutes100To60(rs.getString("actual_hrs")));
						hmActualWorkingDays.put(rs.getString("emp_id")+"_BILL_HRS", uF.getTotalTimeMinutes100To60(rs.getString("billable_hrs")));
					} else {
						hmActualWorkingDays.put(rs.getString("emp_id")+"_"+rs.getString("submited_date")+"_DAYS", rs.getString("days"));
						hmActualWorkingDays.put(rs.getString("emp_id")+"_"+rs.getString("submited_date")+"_ACT_HRS", uF.getTotalTimeMinutes100To60(rs.getString("actual_hrs")));
						hmActualWorkingDays.put(rs.getString("emp_id")+"_"+rs.getString("submited_date")+"_BILL_HRS", uF.getTotalTimeMinutes100To60(rs.getString("billable_hrs")));
					}
					
				}
				rs.close();
				pst.close();
				
	//			System.out.println("=======12========"+new Date());
				
	//			System.out.println("hmNextApproval 11===>> " + hmNextApproval);
	//			 System.out.println("hmAnyOneApproval 11===>> " + hmAnyOneApproval);
	//			 System.out.println("hmMemNextApproval 11===>> " + hmMemNextApproval);
				sbQuery = new StringBuilder();
				sbQuery.append("select ta.task_id,ta.emp_id,ta.is_approved,ta.generated_date,ta.approved_by,ta.submited_date from task_activity ta, " +
					"employee_personal_details epd where ta.emp_id = epd.emp_per_id and to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ? and ta.emp_id in("+strEmpIds+") ");
				
				if(getTimesheetType() == null || getTimesheetType().equals("") || getTimesheetType().equals("EU")) {
					sbQuery.append(" and ta.is_approved > 0 and ta.is_approved < 2 ");
				}
				if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
					sbQuery.append(" and ta.emp_id in (select eod.emp_id from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id ");
					if(flagMiddleName) {
						sbQuery.append(" and (upper(epd.emp_fname)||' '||upper(epd.emp_mname)||' '||upper(epd.emp_lname) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or upper(epd.emp_fname)||' '||upper(epd.emp_lname) like '%"+getStrSearchJob().trim().toUpperCase()+"%'))");
					} else {
						sbQuery.append(" and upper(epd.emp_fname)||' '||upper(epd.emp_lname) like '%"+getStrSearchJob().trim().toUpperCase()+"%')");
					}
				}
				
//				System.out.println("getSortBy() ===>> " + getSortBy());
				if(getSortBy() != null && uF.parseToInt(getSortBy()) == 1) {
					sbQuery.append(" order by ta.emp_id,ta.is_approved,ta.submited_date desc ");
				} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 2) {
					sbQuery.append(" order by ta.emp_id,ta.is_approved,ta.submited_date ");
				} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 3) {
					sbQuery.append(" order by ta.emp_id,ta.is_approved,emp_fname,ta.submited_date desc ");
				} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 4) {
					sbQuery.append(" order by ta.emp_id,ta.is_approved,emp_fname desc,ta.submited_date desc ");
				} else {
					sbQuery.append(" order by ta.emp_id,ta.is_approved,ta.submited_date desc ");
				}
//				System.out.println("sbQuery ===>> " + sbQuery.toString());
				
	//			order by ta.emp_id, ta.submited_date
				pst = con.prepareStatement(sbQuery.toString());				
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//				System.out.println("pst Project Timesheet ===>> " + pst);
				rs = pst.executeQuery();
				
				List<List<String>> alReport = new LinkedList<List<String>>();
	//			List<String> alInner = new ArrayList<String>();
				String strEmpIdNew = null;
				String strEmpIdOld = null;
				String strSubmitDTNew = null;
				String strSubmitDTOld = null;
				int count = 0;
				List<String> alPendingEmpId = new ArrayList<String>();
				while(rs.next()) {
					count++;
					strEmpIdNew = rs.getString("emp_id");
					strSubmitDTNew = rs.getString("submited_date");
					
//					System.out.println("PT/2847---1----");
					if(getTimesheetType() != null && getTimesheetType().equals("EA") && !alPendingEmpId.contains(rs.getString("emp_id")) && uF.parseToInt(rs.getString("is_approved"))<2 && uF.parseToInt(rs.getString("is_approved"))>=0) {
						alPendingEmpId.add(rs.getString("emp_id"));
					}
					if(strUserType != null && (strUserType.equals(HRMANAGER) || strUserType.equals(RECRUITER) || strUserType.equals(ADMIN) || strUserType.equals(OTHER_HR)) && (getPageType() == null || !getPageType().equals("MP"))) {
						if(strEmpIdNew!=null && strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
							continue;
						}
					} else {
						if(strEmpIdNew!=null && strEmpIdNew.equalsIgnoreCase(strEmpIdOld) && strSubmitDTNew!=null && strSubmitDTNew.equalsIgnoreCase(strSubmitDTOld)) {
							continue;
						}
					}
//					System.out.println("PT/2860---2----");
					List<String> checkEmpList=hmCheckEmp.get(rs.getString("task_id"));
					if(checkEmpList==null) checkEmpList=new ArrayList<String>();
					
//					System.out.println("PT/2860---checkEmpList=="+!checkEmpList.contains(strSessionEmpId));
					if(!checkEmpList.contains(strSessionEmpId) && (!strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(HRMANAGER) && !strUserType.equalsIgnoreCase(OTHER_HR) || (getPageType() != null && getPageType().equals("MP")))) { 
						continue;
					}
//					System.out.println("PT/2847---3----");
					if((strUserType.equalsIgnoreCase(HRMANAGER) && (getPageType() == null || !getPageType().equals("MP"))) && hmEmpByLocation.get(rs.getString("emp_id"))==null) {
						continue;
					}
//					System.out.println("PT/2847---4----");
					if(getTimesheetType() != null && getTimesheetType().equals("EA") && alPendingEmpId.contains(rs.getString("emp_id"))) { //(strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ADMIN)) && (getPageType() == null || !getPageType().equals("MP"))	&&
							continue;
					}
//					System.out.println("PT/2847---5----");
					List<String> alInner = new ArrayList<String>();
					
					StringBuilder sb = new StringBuilder();
					 int i=0;
					 
	//				 System.out.println("task_id ===>> "+ rs.getString("task_id")+" hmNextApproval ===>> "+hmNextApproval.get(rs.getString("task_id")));
	//				 System.out.println("hmAnyOneApproval ===>> " + hmAnyOneApproval.get(rs.getString("task_id")));
	//				 System.out.println("hmMemApprovalPos ===>> " + hmMemApprovalPos.get(rs.getString("task_id")));
					if(uF.parseToInt(hmAnyOneApproval.get(rs.getString("task_id")))==1 && uF.parseToInt(hmAnyOneApproval.get(rs.getString("task_id")))==rs.getInt("is_approved")) {
//						System.out.println("1");
						 /*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Approved\"  style=\"color:#54aa0d\"></i>");
						
						i++;
					} else if(uF.parseToInt(hmNextApproval.get(rs.getString("task_id")))==uF.parseToInt(hmMemApprovalPos.get(rs.getString("task_id"))) && uF.parseToInt(hmNextApproval.get(rs.getString("task_id")))>0 && rs.getInt("is_approved") < 2) {
//						System.out.println("2");
						/*sb.append("<img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" />");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"waiting for approval\" ></i>");
						
						i++;
						
					} else if(uF.parseToInt(hmNextApproval.get(rs.getString("task_id")))>uF.parseToInt(hmMemApprovalPos.get(rs.getString("task_id"))) || (uF.parseToInt(hmNextApproval.get(rs.getString("task_id")))==0 && uF.parseToInt(hmNextApproval.get(rs.getString("task_id")))==uF.parseToInt(hmMemApprovalPos.get(rs.getString("task_id"))))) {
//						System.out.println("3");
						if(!checkEmpList.contains(strSessionEmpId) && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(OTHER_HR)) && (getPageType() == null || !getPageType().equals("MP"))) { 
							if(rs.getInt("is_approved")==1) {
//								System.out.println("3.1");
								 /*sb.append("<img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" />");*/
								sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"waiting for approval\"></i>");
								
								i++;
							} else if(rs.getInt("is_approved")==2) {
//								System.out.println("3.2");
								/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
								sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
								i++;
							}/* else {
								sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");
							}*/
							
						} else {
//							System.out.println("3 else ");
							/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
							sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
							
						}
					} else if(rs.getInt("is_approved")==2) {
//						System.out.println("4 ");
						/*sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
						i++;
					} else {
//						System.out.println("5");
	//					sb.append("<img src=\"images1/icons/pullout.png\" border=\"0\" />");
						sb.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\"></i>");
					}
					
					alInner.add(sb.toString()); //0
					
					String strSubmitDate = uF.getDateFormat(rs.getString("submited_date"), DBDATE, DATE_FORMAT);
					
					boolean clientApproveFlag = getClientApprovalStatus(con, strSubmitDate, rs.getString("emp_id"));
					
					alInner.add(hmEmployeeName.get(rs.getString("emp_id"))); //1
					alInner.add(uF.getDateFormat(rs.getString("submited_date"), DBDATE, CF.getStrReportDateFormat())); //2
					if(uF.parseToInt(rs.getString("is_approved")) == 2) {
						alInner.add(uF.showData(hmEmployeeName.get(rs.getString("approved_by")), "")); //3
					} else {
						alInner.add(""); //3
					}
					alInner.add(alDates.size()+""); //4
					if(strUserType != null && (strUserType.equals(HRMANAGER) || strUserType.equals(ADMIN) || strUserType.equals(OTHER_HR)) && (getPageType() == null || !getPageType().equals("MP"))) {
						alInner.add(uF.showData(hmActualWorkingDays.get(rs.getString("emp_id")+"_DAYS"), "0")); //5
//						System.out.println("PT/2949---hmActualWorkingDays=="+hmActualWorkingDays.get(rs.getString("emp_id")+"_DAYS"));
					} else {
						alInner.add(uF.showData(hmActualWorkingDays.get(rs.getString("emp_id")+"_"+rs.getString("submited_date")+"_DAYS"), "0")); //5 
//						System.out.println("PT/2952---hmActualWorkingDays=="+hmActualWorkingDays.get(rs.getString("emp_id")+"_"+rs.getString("submited_date")+"_DAYS"));
					}
	//				System.out.println(" View ===>> " + uF.parseToInt(hmNextApproval.get(rs.getString("task_id"))) + " --->> " + uF.parseToInt(hmMemApprovalPos.get(rs.getString("task_id"))));
					
					if(uF.parseToInt(hmNextApproval.get(rs.getString("task_id")))<uF.parseToInt(hmMemApprovalPos.get(rs.getString("task_id")))) {
						alInner.add("Waiting");
						alInner.add("");
					} else {
						alInner.add("<a href=\"ResourceTimesheet.action?strPaycycle="+uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, DATE_FORMAT)+"-"+uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, DATE_FORMAT)+"-"+strPayCycleDates[2]+"&strEmpId="+rs.getString("emp_id")+"&strUserType="+strUserType+"&pageType="+getPageType()+"&fillUserType=OTHER"+"\">View</a>"); //6
						
						alInner.add("<a href=\"GenerateTimeSheet1.action?mailAction=sendMail&amp;empid="+rs.getString("emp_id")+"&amp;datefrom="+uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, DATE_FORMAT)+"&amp;dateto="+uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, DATE_FORMAT)+"&amp;downloadSubmit=0\" class=\"xls\">Download</a>"); //7
					}
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
					alInner.add(sbCheckApproveby.toString()); //8
					if(strUserType != null && (strUserType.equals(HRMANAGER) || strUserType.equals(ADMIN) || strUserType.equals(OTHER_HR)) && (getPageType() == null || !getPageType().equals("MP"))) {
						alInner.add(uF.showData(hmActualWorkingDays.get(rs.getString("emp_id")+"_ACT_HRS"), "0")); //9
						alInner.add(uF.showData(hmActualWorkingDays.get(rs.getString("emp_id")+"_BILL_HRS"), "0")); //10
					} else {
						alInner.add(uF.showData(hmActualWorkingDays.get(rs.getString("emp_id")+"_"+rs.getString("submited_date")+"_ACT_HRS"), "0")); //9
						alInner.add(uF.showData(hmActualWorkingDays.get(rs.getString("emp_id")+"_"+rs.getString("submited_date")+"_BILL_HRS"), "0")); //10
					}
					
					if(getTimesheetType() != null && getTimesheetType().equals("EA")) {
						if(strUserType != null && ((strUserType.equals(HRMANAGER)|| strUserType.equals(OTHER_HR)) && (getPageType() == null || !getPageType().equals("MP")))) {
						alInner.add("<select name=\"empTimesheetActions\" id=\"empTimesheetActions"+count+"\" style=\"width: 100px !important;\" onchange=\"executeEmpTimesheetActions(this.value, '"+count+"', '"+uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, DATE_FORMAT)+"', '"+uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, DATE_FORMAT)+"', '"+rs.getString("emp_id")+"', '"+strSubmitDate+"', '"+getTimesheetType()+"', '"+getPaycycle()+"');\">" +
							"<option value=''>Action</option>" +
							"<option value=\"2\">Unlock</option>" +
						"</select>"); //11
						} else {
							alInner.add(""); //11
						}
					} else {
	//					System.out.println(" Action ===>> " + uF.parseToInt(hmNextApproval.get(rs.getString("task_id"))) + " --->> " + uF.parseToInt(hmMemApprovalPos.get(rs.getString("task_id"))));
						
						if(uF.parseToInt(hmNextApproval.get(rs.getString("task_id")))<uF.parseToInt(hmMemApprovalPos.get(rs.getString("task_id")))) {
	//						System.out.println(" asdfg .......... !");
							alInner.add("");
						}else {
							if(strUserType != null && ((strUserType.equals(HRMANAGER) || strUserType.equals(OTHER_HR)) && (getPageType() == null || !getPageType().equals("MP")))) {
								if(hmIsApproved != null && uF.parseToBoolean(hmIsApproved.get(rs.getString("emp_id"))) ) {
									alInner.add("<select name=\"empTimesheetActions\" id=\"empTimesheetActions"+count+"\" style=\"width: 100px !important;\" onchange=\"executeEmpTimesheetActions(this.value, '"+count+"', '"+uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, DATE_FORMAT)+"', '"+uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, DATE_FORMAT)+"', '"+rs.getString("emp_id")+"', '"+strSubmitDate+"', '"+getTimesheetType()+"', '"+getPageType()+"', '"+getPaycycle()+"');\">" +
										"<option value=''>Action</option>" +
										"<option value=\"2\">Unlock</option>" +
									"</select>"); //11
								} else {
									alInner.add(""); //11
								}
							} else {
								alInner.add("<select name=\"empTimesheetActions\" id=\"empTimesheetActions"+count+"\" style=\"width: 100px !important;\" onchange=\"executeEmpTimesheetActions(this.value, '"+count+"', '"+uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, DATE_FORMAT)+"', '"+uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, DATE_FORMAT)+"', '"+rs.getString("emp_id")+"', '"+strSubmitDate+"', '"+getTimesheetType()+"', '"+getPageType()+"', '"+getPaycycle()+"');\">" +
									"<option value=''>Action</option>" +
									"<option value=\"1\">Approve</option>" +
									"<option value=\"2\">Deny</option>" +
								"</select>"); //11
							}
						}
					}
					alInner.add(""+clientApproveFlag); //12
					alReport.add(alInner);
					strEmpIdOld = strEmpIdNew;
					strSubmitDTOld = strSubmitDTNew;
				}
				rs.close();
				pst.close();
	//			System.out.println("=======alReport========"+alReport.toString());
				
				int proCnt = alReport.size();
				request.setAttribute("proCnt", proCnt+"");
				if(alReport.size() > 0) {
					
					int proCount = alReport.size()/100;
					if(alReport.size()%100 != 0) {
						proCount++;
					}
					
					request.setAttribute("proCount", proCount+"");
	//				System.out.println("=======proCount========"+proCount);
	//				System.out.println("=======proCnt========"+proCnt);
	//				System.out.println("=======uF.parseToInt(getMinLimit())========"+uF.parseToInt(getMinLimit()));
					if(alReport.size() > 100) {
						int nStart = 0;
						int nEnd = 100;
						if(uF.parseToInt(getMinLimit())>0) {
							nStart = uF.parseToInt(getMinLimit());
							nEnd = uF.parseToInt(getMinLimit())+100;
						}
						
	//					System.out.println("=======nStart========"+nStart);
	//					System.out.println("=======nEnd========"+nEnd);
						if(nEnd > alReport.size()) {
							nEnd = alReport.size();
	//						System.out.println("=======after nEnd========"+nEnd);
						}
						
	//					System.out.println("=======alReport========"+alReport.toString());
						alReport = alReport.subList(nStart, nEnd);
					}
				}
//				System.out.println("=======after alReport========"+alReport.toString());
				request.setAttribute("alReport", alReport);
	//			System.out.println("=======13========"+new Date());
			}	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
private boolean getClientApprovalStatus(Connection con, String strSubmitDate, String empId) {
	
	PreparedStatement pst = null;
	ResultSet rs =null;
	UtilityFunctions uF = new UtilityFunctions();
	boolean flag = false;
	try {
		pst = con.prepareStatement("select is_billable_approved from task_activity where emp_id = ? and submited_date =? and is_billable_approved=2");
		pst.setInt(1, uF.parseToInt(empId));
		pst.setDate(2, uF.getDateFormat(strSubmitDate, DATE_FORMAT)); 
		rs = pst.executeQuery();
		while(rs.next()) {
			flag = true;
		}
		rs.close();
		pst.close();
		
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
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
	return flag;
}




//	private void getTimesheet1() {
//		 
//		Connection con = null;
//		PreparedStatement pst = null; 
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		
//		
//		try {
//			
//			con = db.makeConnection(con);
//			
//			String[] strPayCycleDates = null;
//			if (getPaycycle() != null) {
//				strPayCycleDates = getPaycycle().split("-");
//			} else {
//				strPayCycleDates = CF.getCurrentPayCycle(con,CF.getStrTimeZone(), CF);
//				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
//			}
////			System.out.println("paycycle====>"+strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
//			
//			Calendar cal1 = GregorianCalendar.getInstance();
//			cal1.setTime(uF.getDateFormatUtil(strPayCycleDates[0], DATE_FORMAT));
//
//			List<String> alDates = new ArrayList<String>();
//			
//			for(int i=0; i<32; i++) {
//				String strDate = cal1.get(Calendar.DATE)+"/"+(cal1.get(Calendar.MONTH)+1)+"/"+cal1.get(Calendar.YEAR);
//				alDates.add(uF.getDateFormat(strDate, DATE_FORMAT, DATE_FORMAT));
//				if(strPayCycleDates[1]!=null && strPayCycleDates[1].equalsIgnoreCase(uF.getDateFormat(strDate, DATE_FORMAT, DATE_FORMAT))) {
//					break;
//				}
//				cal1.add(Calendar.DATE, 1);
//			}
//			
//			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
//			String locationID=hmEmpWlocationMap.get(strSessionEmpId);
//			
//			/*pst = con.prepareStatement("select effective_id,min(member_position)as member_position from work_flow_details wfd where is_approved=0 " +
//					" and effective_type='"+WORK_FLOW_TIMESHEET+"' and member_position not in (select max(member_position) from work_flow_details wf where is_approved=1 " +
//					" and effective_type='"+WORK_FLOW_TIMESHEET+"' and  wfd.effective_id=wf.effective_id  group by effective_id) group by effective_id ");
//			rs = pst.executeQuery();
//			
//			Map<String, String> hmNextApproval = new HashMap<String, String>();
//			
//			while(rs.next()) {
//				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
//			}*/
//			
//			pst = con.prepareStatement("select effective_id,max(member_position) as member_position from work_flow_details wf where is_approved=0 " +
//					" and effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in(select ta.task_id from task_activity ta " +
//					"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id");			
//			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			rs = pst.executeQuery();			
//			Map<String, String> hmMaxPositionApproval = new HashMap<String, String>();			
//			while(rs.next()) {
//				hmMaxPositionApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
//			}
//			
//			pst = con.prepareStatement("select effective_id,min(member_position) as member_position from work_flow_details wfd " +
//					" where is_approved=0 and effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in(select ta.task_id from task_activity ta " +
//					"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?)  group by effective_id");	
//			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			rs = pst.executeQuery();
//			
//			Map<String, String> hmNextApproval = new HashMap<String, String>();
//			
//			while(rs.next()) {
////				System.out.println("next emp_po "+rs.getString("effective_id")+"   "+hmMaxPositionApproval.get(rs.getString("effective_id"))+" "+rs.getString("member_position"));
////				if(hmMaxPositionApproval.get(rs.getString("effective_id"))!=null && !hmMaxPositionApproval.get(rs.getString("effective_id")).equals(rs.getString("member_position"))) {
//					hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
////				}
//			}
//						
////			System.out.println("hmNextApproval=====>"+hmNextApproval);
////			System.out.println("=======2========"+new Date());
//			
//			/*pst = con.prepareStatement("select effective_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
//					" and is_approved=0 and effective_type='"+WORK_FLOW_TIMESHEET+"' and member_position not in (select max(member_position) from work_flow_details wf " +
//					" where is_approved=1 and effective_type='"+WORK_FLOW_TIMESHEET+"' and  wfd.effective_id=wf.effective_id  group by effective_id) group by effective_id ");
//			pst.setInt(1,uF.parseToInt(strSessionEmpId));			
//			rs = pst.executeQuery();			
//			Map<String, String> hmMemNextApproval = new HashMap<String, String>();			
//			while(rs.next()) {
//				hmMemNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
//			}*/
//			
//			pst = con.prepareStatement("select effective_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
//					" and is_approved=0 and effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in (select ta.task_id from task_activity ta " +
//					"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id ");
//			pst.setInt(1,uF.parseToInt(strSessionEmpId));	
//			pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));			
//			rs = pst.executeQuery();			
//			Map<String, String> hmMemNextApproval = new HashMap<String, String>();			
//			while(rs.next()) {
//				hmMemNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
//			}					
////			System.out.println("=======3========"+new Date());
//			
//			pst = con.prepareStatement("select effective_id,member_position,emp_id from work_flow_details " +
//					"where effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in(select ta.task_id from task_activity ta " +
//					"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?) order by effective_id");	
//			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			rs = pst.executeQuery();			
//			Map<String, String> hmNextApprovalMem = new HashMap<String, String>();			
//			while(rs.next()) {
//				hmNextApprovalMem.put(rs.getString("effective_id")+"_"+rs.getString("member_position"), rs.getString("emp_id"));
//			}
//			
//			
////			System.out.println("=======4========"+new Date());
//			
//			pst = con.prepareStatement("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_TIMESHEET+"'" +
//					"  and effective_id in(select ta.task_id from task_activity ta where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id");
//			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			rs = pst.executeQuery();			
//			List<String> deniedList=new ArrayList<String>();			
//			while(rs.next()) {
//				if(!deniedList.contains(rs.getString("effective_id")))
//					deniedList.add(rs.getString("effective_id"));
//			}
//			
////			System.out.println("=======5========"+new Date());
//			
//			pst = con.prepareStatement("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
//					" and emp_id=? and effective_type='"+WORK_FLOW_TIMESHEET+"'  and effective_id in(select ta.task_id from task_activity ta " +
//					"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id,is_approved");
//			pst.setInt(1,uF.parseToInt(strSessionEmpId));	
//			pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			rs = pst.executeQuery();			
//			Map<String, String> hmAnyOneApproval = new HashMap<String, String>();			
//			while(rs.next()) {
//				hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
//			}
//			
//			
////			System.out.println("=======6========"+new Date());
//			
//			pst = con.prepareStatement("select effective_id,emp_id from work_flow_details where member_type=3 " +
//					" and effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in(select ta.task_id from task_activity ta " +
//					"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id,emp_id");	
//			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			rs = pst.executeQuery();			
//			Map<String, Set<String>> hmAnyOneApproeBy = new HashMap<String, Set<String>>();			
//			while(rs.next()) {
//				Set<String> innerSet = hmAnyOneApproeBy.get(rs.getString("effective_id"));;
//				if(innerSet == null) innerSet = new HashSet<String>();
//				innerSet.add(rs.getString("emp_id"));
//				
//				hmAnyOneApproeBy.put(rs.getString("effective_id"), innerSet);
//			}
//			
////			System.out.println("=======7========"+new Date());
//			
//			pst = con.prepareStatement("select effective_id,emp_id from work_flow_details where member_type!=3 " +
//				" and effective_type='"+WORK_FLOW_TIMESHEET+"'  and effective_id in(select ta.task_id from task_activity ta " +
//					"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?) group by effective_id,emp_id");	
//			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			rs = pst.executeQuery();			
//			Map<String, String> hmotherApproveBy = new HashMap<String, String>();			
//			while(rs.next()) {
//				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
//			}
//			
////			System.out.println("=======8========"+new Date());
//			
//			pst = con.prepareStatement("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in(select ta.task_id from task_activity ta " +
//					"where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?) order by effective_id,member_position");	
//			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			rs = pst.executeQuery();			
//			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	
//			while(rs.next()) {
//				List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
//				if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
//				checkEmpList.add(rs.getString("emp_id"));
//				
//				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
//			}
//			
////			System.out.println("=======9========"+new Date());
//			
//			pst = con.prepareStatement("select ud.emp_id from user_details ud,employee_official_details eod,employee_personal_details epd where " +
//				" ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'");
//			pst.setInt(1, uF.parseToInt(locationID));
//			rs = pst.executeQuery();			
//			Map<String, String> hmEmpByLocation = new HashMap<String, String>();			
//			while(rs.next()) {
//				hmEmpByLocation.put(rs.getString("emp_id"), rs.getString("emp_id"));
//			}
//			
//			
////			System.out.println("=======10========"+new Date());
//			
//			
//			
//			
////			System.out.println("=======11========"+new Date());
//			
//			Map<String, String> hmEmployeeName = CF.getEmpNameMap(con, null, null);
//			
////			Map<String, String> hmActualWorkingDays = CF.getEmpNameMap(null, null);
//			Map<String, String> hmActualWorkingDays = new HashMap<String, String>();
//			pst = con.prepareStatement("select count(distinct task_date) as days, emp_id from task_activity where task_date between ? and ? group by emp_id");
//			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			rs = pst.executeQuery();
//			
//			while(rs.next()) {
//				hmActualWorkingDays.put(rs.getString("emp_id"), rs.getString("days"));
//			}
//					
//					
////			System.out.println("=======12========"+new Date());
//			
//			
//			/*if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
//				pst = con.prepareStatement("select * from task_activity where emp_id in ( select emp_id from  employee_official_details where wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) ) and timesheet_paycycle=? order by generated_date");
//				pst.setInt(1, uF.parseToInt(strSessionEmpId));
//				pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
//				rs = pst.executeQuery();
//			} else {
//				//pst = con.prepareStatement("select * from task_activity where emp_id in ( select emp_id from  project_emp_details where pro_id in (select pro_id from projectmntnc where added_by = ?) ) and timesheet_paycycle=? order by generated_date");
//				pst = con.prepareStatement("select * from task_activity where timesheet_paycycle=?  and activity_id in " +
//						" (select task_id from  activity_info where  pro_id in (select pmt.pro_id from projectmntnc pmt where pmt.added_by = ? ))" +
//						"  order by generated_date");				
//				pst.setInt(1, uF.parseToInt(strPayCycleDates[2]));
//				pst.setInt(2, uF.parseToInt(strSessionEmpId));
//				rs = pst.executeQuery();
//			}*/
//			
//			
//			/*pst = con.prepareStatement("select ta.task_id,ta.emp_id,ta.is_approved,ta.generated_date,ta.approved_by,wfd.effective_id," +
//					"wfd.member_position,wfd.is_approved from task_activity ta,work_flow_details wfd where ta.timesheet_paycycle=? " +
//					" and ta.task_id=wfd.effective_id  and wfd.effective_type='"+WORK_FLOW_TIMESHEET+"' and wfd.emp_id=? and ta.is_approved > 0");				
//			pst.setInt(1, uF.parseToInt(strPayCycleDates[2]));
//			pst.setInt(2, uF.parseToInt(strSessionEmpId));*/
//			/*pst = con.prepareStatement("select ta.task_id,ta.emp_id,ta.is_approved,ta.generated_date,ta.approved_by from " +
//					" task_activity ta where ta.timesheet_paycycle=?  and ta.is_approved > 0 order by ta.emp_id");				
//			pst.setInt(1, uF.parseToInt(strPayCycleDates[2]));
//			rs = pst.executeQuery();			 */
//			pst = con.prepareStatement("select ta.task_id,ta.emp_id,ta.is_approved,ta.generated_date,ta.approved_by from " +
//				" task_activity ta where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?  and ta.is_approved > 0 order by ta.emp_id");				
//			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			rs = pst.executeQuery();			 
////			System.out.println("pst===>"+pst);
//			
//			List alReport = new ArrayList();
//			List<String> alInner = new ArrayList<String>();
//			String strEmpIdNew = null;
//			String strEmpIdOld = null;
//			
//			while(rs.next()) {
//				
//				strEmpIdNew = rs.getString("emp_id");
//				if(strEmpIdNew!=null && strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
//					continue;
//				}
//				
//				List<String> checkEmpList=hmCheckEmp.get(rs.getString("task_id"));
//				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
//				
//				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(HRMANAGER)) { 
//					continue;
//				}
//				
//				if(strUserType.equalsIgnoreCase(HRMANAGER) && hmEmpByLocation.get(rs.getString("emp_id"))==null) {
//					continue;
//				}
//				
//				
//				
//				alInner = new ArrayList<String>();
//				/*if(uF.parseToInt(rs.getString("is_approved"))==2) {
//					alInner.add("<img src=\"images1/icons/approved.png\" title=\"Approved\"/>");
//				} else if(uF.parseToInt(rs.getString("is_approved"))==1) {
//					alInner.add("<img src=\"images1/icons/pending.png\" title=\"Waiting for Approval\"/>");
//				} else if(uF.parseToInt(rs.getString("is_approved"))==0) {
//					alInner.add("<img src=\"images1/icons/re_submit.png\" title=\"Unlocked for Modification\"/>");
//				}*/
//				
//				
//				
//				
//				
//				
//				StringBuilder sb = new StringBuilder();
//				 int i=0;
//				if(deniedList.contains(rs.getString("task_id"))) {
//					System.out.println("1");
//					sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");
//				}
////				else if(rs.getInt("is_approved")==2) {
////					System.out.println("2");
////					sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");
////					i++;
////				}
//				else if(uF.parseToInt(hmAnyOneApproval.get(rs.getString("task_id")))==1 && uF.parseToInt(hmAnyOneApproval.get(rs.getString("task_id")))==rs.getInt("is_approved")) {
//					System.out.println("3");
//					sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");
//					i++;
//				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("task_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("task_id"))) && uF.parseToInt(hmNextApproval.get(rs.getString("task_id")))>0) {
//					System.out.println("4");
//					sb.append("<img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" />");
//					i++;
//					
//				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("task_id")))>uF.parseToInt(hmMemNextApproval.get(rs.getString("task_id"))) || (uF.parseToInt(hmNextApproval.get(rs.getString("task_id")))==0 && uF.parseToInt(hmNextApproval.get(rs.getString("task_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("task_id"))))) {
//					System.out.println("5");
//					if(!checkEmpList.contains(strSessionEmpId) && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))) { 
//						if(rs.getInt("is_approved")==1) {		
//							if(strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER)) {
//								sb.append("<img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" />");
//								i++;
//							} else {
//								sb.append("<img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" />");
//								i++;
//							}
//						} else if(rs.getInt("is_approved")==2) {							
//							sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");
//							i++;
//						} else {
//							sb.append("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");
//						}
//						
//					} else {
//					
//						sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");
//					}
//				} else if(rs.getInt("is_approved")==2) {
//					System.out.println("2");
//					sb.append("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");
//					i++;
//				} else {
//					System.out.println("6");
////					sb.append("<img src=\"images1/icons/pullout.png\" border=\"0\" />");
//					sb.append("<img src=\"images1/icons/pending.png\" border=\"0\" />");
//				}
//				
//				
//				
//				alInner.add(sb.toString());
//				
//				
//				alInner.add(hmEmployeeName.get(rs.getString("emp_id")));
//				alInner.add(uF.getDateFormat(rs.getString("generated_date"), DBDATE, CF.getStrReportDateFormat()));
//				if(uF.parseToInt(rs.getString("is_approved")) == 2) {
//					alInner.add(uF.showData(hmEmployeeName.get(rs.getString("approved_by")), ""));
//				} else {
//					alInner.add("");
//				}
//				alInner.add(alDates.size()+"");
//				alInner.add(uF.showData(hmActualWorkingDays.get(rs.getString("emp_id")), "0")); 
//				
////				if(i>0) {
////					alInner.add("<a href=\"AddProjectActivity1.action?strPaycycle="+uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, DATE_FORMAT)+"-"+uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, DATE_FORMAT)+"-"+strPayCycleDates[2]+"&strEmpId="+rs.getString("emp_id")+"\">View</a>");
////				} else {
////					String nextMem=hmNextApprovalMem.get(rs.getString("task_id")+"_"+hmNextApproval.get(rs.getString("task_id")));
////					String memname=hmEmployeeName.get(nextMem);
////					
////					alInner.add("Waiting for "+memname+"'s approval ");
////				}
//				alInner.add("<a href=\"AddProjectActivity1.action?strPaycycle="+uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, DATE_FORMAT)+"-"+uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, DATE_FORMAT)+"-"+strPayCycleDates[2]+"&strEmpId="+rs.getString("emp_id")+"\">View</a>");
//				
//				alInner.add("<a href=\"GenerateTimeSheet1.action?mailAction=sendMail&amp;empid="+rs.getString("emp_id")+"&amp;datefrom="+uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, DATE_FORMAT)+"&amp;dateto="+uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, DATE_FORMAT)+"&amp;downloadSubmit=0\" class=\"xls\">Download</a>");
//				
//				StringBuilder sbCheckApproveby=new StringBuilder();
//				
//				if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("task_id"))!=null) {
////					String approvedby=hmAnyOneApproeBy.get(rs.getString("task_id"));
////					sbCheckApproveby.append(hmEmployeeName.get(approvedby.trim()));
//					Set<String> anyOneSet= hmAnyOneApproeBy.get(rs.getString("task_id"));
//					if(anyOneSet==null)anyOneSet=new HashSet<String>();
//					
//					Iterator<String> it = anyOneSet.iterator();
//					String approvedby = "";
//					int x = 0;
//					while(it.hasNext()) {
//						String empid = it.next();
//						if(x==0) {
//							approvedby = uF.showData(hmEmployeeName.get(empid), "");
//						} else {
//							approvedby +=", "+ uF.showData(hmEmployeeName.get(empid), "");
//						}
//						x++;
//					}
//					sbCheckApproveby.append(approvedby);
//				} else {
//					if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("task_id"))!=null) {
//						sbCheckApproveby.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("task_id")+"','"+hmEmployeeName.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");	
//					} else {
//						sbCheckApproveby.append("");
//					}
//					
//				}
//				alInner.add(sbCheckApproveby.toString());
//				
//				
//				alReport.add(alInner);
//				
//				strEmpIdOld = strEmpIdNew;
//			}
//			
//			request.setAttribute("alReport", alReport);
//			
//			
////			System.out.println("=======13========"+new Date());
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			db.closeConnection(con);
//		}
//	}

	String paycycle;
	String timesheetType;
	
	List<FillPayCycles> paycycleList;
	
//	public void getTimesheet() {
//		
//		Connection con = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		PreparedStatement pst = null; 
//		ResultSet rs = null;
//		
//		try {
//			
//			con = db.makeConnection(con);
//			String[] strPayCycleDates = null;
////			if(getFilterBy() == null || getFilterBy().equals("P")) {
//				if (getPaycycle() != null) {
//					strPayCycleDates = getPaycycle().split("-");
//				} else {
//					strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
//					setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
//				}
////			} else if(getFilterBy() != null && getFilterBy().equals("O")) {
////				if()
////			}
//			Calendar cal1 = GregorianCalendar.getInstance();
//			cal1.setTime(uF.getDateFormatUtil(strPayCycleDates[0], DATE_FORMAT));
//
//			List<String> alDates = new ArrayList<String>();
//			
//			for(int i=0; i<32; i++) {
//				String strDate = cal1.get(Calendar.DATE)+"/"+(cal1.get(Calendar.MONTH)+1)+"/"+cal1.get(Calendar.YEAR);
//				alDates.add(uF.getDateFormat(strDate, DATE_FORMAT, DATE_FORMAT));
//				if(strPayCycleDates[1]!=null && strPayCycleDates[1].equalsIgnoreCase(uF.getDateFormat(strDate, DATE_FORMAT, DATE_FORMAT))) {
//					break;
//				}
//				cal1.add(Calendar.DATE, 1);
//			}
//			
//			Map<String, String> hmEmployeeName = CF.getEmpNameMap(con, null, null);
//			
//			
////			Map<String, String> hmActualWorkingDays = CF.getEmpNameMap(null, null);
//			Map<String, String> hmActualWorkingDays = new HashMap<String, String>();
//			pst = con.prepareStatement("select count(distinct task_date) as days, emp_id from task_activity where task_date between ? and ? group by emp_id");
//			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			rs = pst.executeQuery();
//			
//			while(rs.next()) {
//				hmActualWorkingDays.put(rs.getString("emp_id"), rs.getString("days"));
//			}
//			rs.close();
//			pst.close();
//					
//			/*if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
//				pst = con.prepareStatement("select * from project_timesheet where emp_id in ( select emp_id from  employee_official_details where wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) ) and timesheet_paycycle = ? order by timesheet_generated_date");
//				pst.setInt(1, uF.parseToInt(strSessionEmpId));
//				pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
//				rs = pst.executeQuery();
//			} else {
//				pst = con.prepareStatement("select * from project_timesheet where emp_id in ( select emp_id from  project_emp_details where pro_id in (select pro_id from projectmntnc where added_by = ?) ) and timesheet_paycycle = ? order by timesheet_generated_date");
//				pst.setInt(1, uF.parseToInt(strSessionEmpId));
//				pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
//				rs = pst.executeQuery();
//			}*/
//			
//			if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
//				pst = con.prepareStatement("select * from task_activity where emp_id in ( select emp_id from  employee_official_details where wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) ) and timesheet_paycycle=? order by generated_date");
//				pst.setInt(1, uF.parseToInt(strSessionEmpId));
//				pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
//				rs = pst.executeQuery();
//			} else {
//				//pst = con.prepareStatement("select * from task_activity where emp_id in ( select emp_id from  project_emp_details where pro_id in (select pro_id from projectmntnc where added_by = ?) ) and timesheet_paycycle=? order by generated_date");
//				pst = con.prepareStatement("select * from task_activity where timesheet_paycycle=?  and activity_id in " +
//						" (select task_id from  activity_info where  pro_id in (select pmt.pro_id from projectmntnc pmt where pmt.added_by = ? ))" +
//						"  order by generated_date");				
//				pst.setInt(1, uF.parseToInt(strPayCycleDates[2]));
//				pst.setInt(2, uF.parseToInt(strSessionEmpId));
//				rs = pst.executeQuery();
//			}
//			List alReport = new ArrayList();
//			List<String> alInner = new ArrayList<String>();
//			String strEmpIdNew = null;
//			String strEmpIdOld = null;
//			
//			while(rs.next()) {
//				
//				strEmpIdNew = rs.getString("emp_id");
//				if(strEmpIdNew!=null && strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
//					continue;
//				}
//				alInner = new ArrayList<String>();
//				if(uF.parseToInt(rs.getString("is_approved"))==2) {
//					alInner.add("<img src=\"images1/icons/approved.png\" title=\"Approved\"/>");
//				} else if(uF.parseToInt(rs.getString("is_approved"))==1) {
//					alInner.add("<img src=\"images1/icons/pending.png\" title=\"Waiting for Approval\"/>");
//				} else if(uF.parseToInt(rs.getString("is_approved"))==0) {
//					alInner.add("<img src=\"images1/icons/re_submit.png\" title=\"Unlocked for Modification\"/>");
//				}
//				
//				
//				alInner.add(hmEmployeeName.get(rs.getString("emp_id")));
//				alInner.add(uF.getDateFormat(rs.getString("generated_date"), DBDATE, CF.getStrReportDateFormat()));
//				alInner.add(uF.showData(hmEmployeeName.get(rs.getString("approved_by")), ""));
//				
//				alInner.add(alDates.size()+"");
//				alInner.add(uF.showData(hmActualWorkingDays.get(rs.getString("emp_id")), "0"));
//				
//				alInner.add("<a href=\"AddProjectActivity1.action?strPaycycle="+uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, DATE_FORMAT)+"-"+uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, DATE_FORMAT)+"-"+strPayCycleDates[2]+"&strEmpId="+rs.getString("emp_id")+"\">View</a>");
//				alInner.add("<a href=\"GenerateTimeSheet1.action?mailAction=sendMail&amp;empid="+rs.getString("emp_id")+"&amp;datefrom="+uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, DATE_FORMAT)+"&amp;dateto="+uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, DATE_FORMAT)+"&amp;downloadSubmit=0\" class=\"xls\">Download</a>");
//				alReport.add(alInner);
//				
//				strEmpIdOld = strEmpIdNew;
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("alReport", alReport);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String getPaycycle() {
		return paycycle;
	}
	
	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}
	
	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public String getTimesheetType() {
		return timesheetType;
	}

	public void setTimesheetType(String timesheetType) {
		this.timesheetType = timesheetType;
	}

	public List<FillProjectOwnerList> getProjectOwnerList() {
		return projectOwnerList;
	}

	public void setProjectOwnerList(List<FillProjectOwnerList> projectOwnerList) {
		this.projectOwnerList = projectOwnerList;
	}

	public List<FillBillingType> getBillingBasisList() {
		return billingBasisList;
	}

	public void setBillingBasisList(List<FillBillingType> billingBasisList) {
		this.billingBasisList = billingBasisList;
	}

	public List<FillBillingType> getBillingFreqList() {
		return billingFreqList;
	}

	public void setBillingFreqList(List<FillBillingType> billingFreqList) {
		this.billingFreqList = billingFreqList;
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}

	public List<FillProjectList> getProjectdetailslist() {
		return projectdetailslist;
	}

	public void setProjectdetailslist(List<FillProjectList> projectdetailslist) {
		this.projectdetailslist = projectdetailslist;
	}

	public String[] getProId() {
		return proId;
	}

	public void setProId(String[] proId) {
		this.proId = proId;
	}

	public String[] getProjectOwner() {
		return projectOwner;
	}

	public void setProjectOwner(String[] projectOwner) {
		this.projectOwner = projectOwner;
	}

	public String[] getClient() {
		return client;
	}

	public void setClient(String[] client) {
		this.client = client;
	}

	public String[] getProjectType() {
		return projectType;
	}

	public void setProjectType(String[] projectType) {
		this.projectType = projectType;
	}

	public String[] getProjectFrequency() {
		return projectFrequency;
	}

	public void setProjectFrequency(String[] projectFrequency) {
		this.projectFrequency = projectFrequency;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public String getProID() {
		return proID;
	}

	public void setProID(String proID) {
		this.proID = proID;
	}

	public String getProFreqID() {
		return proFreqID;
	}

	public void setProFreqID(String proFreqID) {
		this.proFreqID = proFreqID;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getSortBy1() {
		return sortBy1;
	}

	public void setSortBy1(String sortBy1) {
		this.sortBy1 = sortBy1;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public List<FillYears> getYearList() {
		return yearList;
	}

	public void setYearList(List<FillYears> yearList) {
		this.yearList = yearList;
	}

	public List<FillWeekDays> getWeekList() {
		return weekList;
	}

	public void setWeekList(List<FillWeekDays> weekList) {
		this.weekList = weekList;
	}

	public String getFilterBy() {
		return filterBy;
	}

	public void setFilterBy(String filterBy) {
		this.filterBy = filterBy;
	}

	public String getStrYear() {
		return strYear;
	}

	public void setStrYear(String strYear) {
		this.strYear = strYear;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getStrWeek() {
		return strWeek;
	}

	public void setStrWeek(String strWeek) {
		this.strWeek = strWeek;
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

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
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


	public String getStrSearchJob() {
		return strSearchJob;
	}


	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}

}
