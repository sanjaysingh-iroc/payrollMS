package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectTimesheetDenyByCustomer extends ActionSupport implements ServletRequestAware, IConstants {
	
	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	private HttpServletRequest request;
	String strSessionEmpId;
	String strUserType;
	
	HttpSession session;
	
	String timesheetType;
	String proID;
	String proFreqID;
	
	String strComment;
	String operation;
	
	String pageType;
	
	public String execute() {
	
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		if(getPageType() == null || getPageType().equals("") || getPageType().equals("null")) {
			setPageType(null);
		}
		
		if(getOperation()!=null && getOperation().equals("A")) {
			denyTimesheet();
			if(getPageType() != null && getPageType().equals("MP")) {
				return MYSUCCESS;
			} else {
				return SUCCESS;
			}
		}
		if(getOperation()!=null && getOperation().equals("V")) {
			getDenyComment();
		}
		
		return LOAD;
	}
	
	
	private void getDenyComment() {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		try {
			
//			System.out.println("strTaskId ===>> " + strTaskId);
			
			con = db.makeConnection(con);
			
//			if(strUserType !=null && strUserType.equals(CUSTOMER)) {
			pst = con.prepareStatement("select * from communications where module_type=? and pro_id=? and pro_freq_id=?"); // and cust_id=?
//			} else {
//				pst = con.prepareStatement("select * from communications where module_type=? and pro_id=? and pro_freq_id=? and emp_id=?");
//			}
			pst.setString(1, "Timesheet");
			pst.setInt(2, uF.parseToInt(getProID()));
			pst.setInt(3, uF.parseToInt(getProFreqID()));
//			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			while(rs.next()) {
				setStrComment(rs.getString("comments"));
			}
			rs.close();
			pst.close();
//			System.out.println("pst===>"+pst);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public void denyTimesheet() {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		try {
			
//			System.out.println("strTaskId ===>> " + strTaskId);
			
			con = db.makeConnection(con);
			
			denyTimesheetForBilling(con);
			
			if(strUserType !=null && strUserType.equals(CUSTOMER)) {
				pst = con.prepareStatement("insert into communications(module_type, pro_id, pro_freq_id, cust_id, comments, entry_time)values(?,?,?,?, ?,?)");
			} else {
				pst = con.prepareStatement("insert into communications(module_type, pro_id, pro_freq_id, emp_id, comments, entry_time)values(?,?,?,?, ?,?)");
			}
			pst.setString(1, "Timesheet");
			pst.setInt(2, uF.parseToInt(getProID()));
			pst.setInt(3, uF.parseToInt(getProFreqID()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setString(5, getStrComment());
			pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
			pst.execute();
			pst.close();
//			System.out.println("pst===>"+pst);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void denyTimesheetForBilling(Connection con) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {

//			for(int i=0; proFreqList!= null && i<proFreqList.size(); i++) {
			pst = con.prepareStatement("select pf.*,p.pro_id,actual_calculation_type from projectmntnc p, projectmntnc_frequency pf " +
				"where p.billing_type != 'F' and p.pro_id = pf.pro_id and pf.pro_freq_id=?");
			pst.setInt(1, uF.parseToInt(getProFreqID()));
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
			pst.setDate(1, uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(hmProjectData.get("PRO_ID")));
			rs = pst.executeQuery();
			while (rs.next()) {
				String activity = rs.getString("activity") != null && !rs.getString("activity").equals("") ? rs.getString("activity") : "0";
				String task_data = uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT) + "::_::" + rs.getString("emp_id")+":_:"+rs.getString("activity_id") + "::_::" + rs.getString("emp_id")+":_:"+activity;
				hmEmpTaskActivityDate.put(rs.getString("task_id"), task_data);
			}
			rs.close();
			pst.close();

			// System.out.println("getUnlock()=====>"+getUnlock());
//				if (getType() != null && getType().equalsIgnoreCase("approve")) {
					Iterator<String> it = hmEmpTaskActivityDate.keySet().iterator();
					while (it.hasNext()) {
						String task_id = (String) it.next(); // task_date
//						if (uF.parseToBoolean(CF.getIsWorkFlow())) {} else {
//							if (strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))) {
								boolean taskApproveFlag = false;
								StringBuilder sbQuery = new StringBuilder();
								sbQuery.append("select is_billable_approved from task_activity where task_id=? and is_approved=2 and is_billable_approved=1");
								pst = con.prepareStatement(sbQuery.toString());
								pst.setInt(1, uF.parseToInt(task_id));
								rs = pst.executeQuery();
								while(rs.next()) {
									taskApproveFlag = true;
								}
								rs.close();
								pst.close();
								
								if(taskApproveFlag) {
									StringBuilder sbQue = new StringBuilder();
									sbQue.append("update task_activity set cust_denied_by=?, is_billable_approved=?, cust_denied_date=? where task_id=?");
									pst = con.prepareStatement(sbQue.toString()); // and is_billable_approved=0 and is_approved=1
									pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
									pst.setInt(2, -1);
									pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(4, uF.parseToInt(task_id));
									pst.executeUpdate();
									pst.close();
								}
//							}
//						}
					}
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getTimesheetType() {
		return timesheetType;
	}

	public void setTimesheetType(String timesheetType) {
		this.timesheetType = timesheetType;
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

	public String getStrComment() {
		return strComment;
	}

	public void setStrComment(String strComment) {
		this.strComment = strComment;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

}
