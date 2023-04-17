package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author workrig
 *
 */
public class ProjectSnapshot extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType;
	String strSessionEmpId;
	CommonFunctions CF;

	String pro_id;
	String pageFrom;
	String pageType;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)return LOGIN;

		strSessionEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		
		request.setAttribute(PAGE, "/jsp/task/ProjectSnapshot.jsp");
		
		
		request.setAttribute("arrEnabledModules", CF.getArrEnabledModules());
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		request.setAttribute("IS_DEVICE_INTEGRATION", CF.getIsDeviceIntegration());
		UtilityFunctions uF = new UtilityFunctions();
		
		if(uF.parseToInt(getPro_id()) > 0) {
			getProjectDetails(uF);
			if(getPageType() != null && getPageType().equalsIgnoreCase("MP")) {
				checkSessionEmpIsProjectOwnerOrTL(uF);
			}
		}
		
		if(getPageFrom()!=null && getPageFrom().trim().equalsIgnoreCase("Project")) {
			return "tab";
		} else {
			return LOAD;
		}
	}
	
	
	private void checkSessionEmpIsProjectOwnerOrTL(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String strProOwnerOrTL = "0";
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from project_emp_details where _isteamlead=true and pro_id=? and emp_id=? ");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {
				strProOwnerOrTL = "2";
			}
			rs.close();
			pst.close();
			
		//===start parvez date: 14-10-2022===	
//			pst = con.prepareStatement("select * from projectmntnc where pro_id=? and project_owner=? ");
			pst = con.prepareStatement("select * from projectmntnc where pro_id=? and project_owners like '%,"+strSessionEmpId+",%' ");
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			pst.setInt(2, uF.parseToInt(strSessionEmpId));
		//===end parvez date: 14-10-2022===	
			rs = pst.executeQuery();
			while (rs.next()) {
				strProOwnerOrTL = "1";
			}
			rs.close();
			pst.close();
			
// 		========================================================= End =========================================================			
		
			request.setAttribute("strProOwnerOrTL", strProOwnerOrTL);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getProjectDetails(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		try {
			con = db.makeConnection(con);
			
			getTeamTaskDetails(con, uF);
			getProjectandBusinessDetails(con, uF);
			getProjectEffort(con, uF);
			getWeeklyWorkProgress(con, uF);
			getProjectCosting(con, uF);
			getProTaskPerformanceDetails(con, uF);
			getProjectBillingDetails(con, uF);
			getProTaskCountDetails(con, uF);
			getProjectMilestone(con, uF);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}
	

	private void getProjectMilestone(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select count(project_milestone_id) as cnt from project_milestone_details where pro_id in (select pro_id from projectmntnc where pro_id=?)");
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			int nTotalMilestone = 0;
			while(rs.next()) {
				nTotalMilestone = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("nTotalMilestone", ""+nTotalMilestone);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void getProTaskCountDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select count(task_id)as cnt from activity_info where pro_id in (select pro_id from projectmntnc where pro_id=?) and approve_status='approved'");
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			int nTaskComplete = 0;
			while(rs.next()) {
				nTaskComplete = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(task_id)as cnt from activity_info where pro_id in (select pro_id from projectmntnc where pro_id=?) and approve_status='n'" +
					" and deadline < ?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			int nTaskDelay = 0;
			while(rs.next()) {
				nTaskDelay= rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(task_id)as cnt from activity_info where pro_id in (select pro_id from projectmntnc where pro_id=?) and approve_status='n'" +
					" and (resource_ids is null or resource_ids ='')");
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			int nTaskUnassign = 0;
			while(rs.next()) {
				nTaskUnassign = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(task_id)as cnt from activity_info where pro_id in (select pro_id from projectmntnc where pro_id=?) and approve_status='n'" +
					" and start_date > ?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			int nTaskYetStart = 0;
			while(rs.next()) {
				nTaskYetStart = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(task_id)as cnt from activity_info where pro_id in (select pro_id from projectmntnc where pro_id=?) and approve_status='n'" +
					" and ? between start_date and deadline");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			int nTaskOnTime = 0;
			while(rs.next()) {
				nTaskOnTime = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			StringBuilder sbProTaskDonut 	= new StringBuilder();
			sbProTaskDonut.append("{'task':'Tasks Completed', 'count': "+nTaskComplete+"},");
			sbProTaskDonut.append("{'task':'Overdue Tasks', 'count': "+nTaskDelay+"},");
			sbProTaskDonut.append("{'task':'On Time Tasks', 'count': "+nTaskOnTime+"},");
			sbProTaskDonut.append("{'task':'Tasks Unassigned', 'count': "+nTaskUnassign+"},");
			sbProTaskDonut.append("{'task':'Yet to start Tasks', 'count': "+nTaskYetStart+"}");
			
			request.setAttribute("sbProTaskDonut", sbProTaskDonut.toString());
			request.setAttribute("strProTaskCount", ""+(nTaskComplete + nTaskDelay + nTaskOnTime + nTaskUnassign + nTaskYetStart));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void getProjectBillingDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select * from promntc_invoice_details where pro_id=? order by entry_date desc");
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alBillInvoice = new ArrayList<Map<String,String>>();
			while(rs.next()) {
				Map<String, String> hmInvoice = new HashMap<String, String>();
				hmInvoice.put("INVOICE_ID", rs.getString("promntc_invoice_id"));
				hmInvoice.put("INVOICE_CODE", rs.getString("invoice_code"));
				hmInvoice.put("INVOICE_AMT", rs.getString("oc_invoice_amount"));
				
				alBillInvoice.add(hmInvoice); 
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select sum(received_amount) as received_amount, invoice_id from promntc_bill_amt_details where pro_id=? group by invoice_id");
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmReceivedAmt = new HashMap<String, String>();
			while(rs.next()) {
				hmReceivedAmt.put(rs.getString("invoice_id"), rs.getString("received_amount"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alBillInvoice", alBillInvoice);
			request.setAttribute("hmReceivedAmt", hmReceivedAmt);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void getProTaskPerformanceDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select * from activity_info where pro_id>0 and (activity_name is not null and activity_name !='')" +
					" and pro_id in(select pro_id from projectmntnc where pro_id=?) order by activity_name");
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alTask = new ArrayList<Map<String,String>>();
			while(rs.next()) {
				Map<String, String> hmTask = new HashMap<String, String>();
				hmTask.put("TASK_ID", rs.getString("task_id"));
				hmTask.put("PRO_ID", rs.getString("pro_id"));
				hmTask.put("TASK_NAME", uF.showData(rs.getString("activity_name"), ""));
				hmTask.put("TASK_EST_TIME", uF.showData(rs.getString("idealtime"), ""));
				String resourceIds = (rs.getString("resource_ids")!=null && !rs.getString("resource_ids").trim().equals("")) ? rs.getString("resource_ids").substring(1, rs.getString("resource_ids").length()-1) : "";
				hmTask.put("TASK_RESOURCE_IDS", resourceIds);
				hmTask.put("TASK_START_DATE", rs.getString("start_date"));
				hmTask.put("TASK_DEADLINE", rs.getString("deadline"));
				alTask.add(hmTask); 
				
			}
			rs.close();
			pst.close();
			
			List<Map<String, String>> alProTask = new ArrayList<Map<String,String>>();
			for(int i=0; alTask != null && !alTask.isEmpty() && i< alTask.size(); i++) {
				Map<String, String> hmTask = alTask.get(i);
				
				Map<String, String> hmProTask = new HashMap<String, String>();
				hmProTask.put("TASK_ID", hmTask.get("TASK_ID"));
				hmProTask.put("PRO_ID", hmTask.get("PRO_ID"));
				hmProTask.put("TASK_NAME", hmTask.get("TASK_NAME"));
				
				double idealTime = uF.parseToDouble(hmTask.get("TASK_EST_TIME"));
				
				Map<String, String> hmProData = CF.getProjectDetailsByProId(con, hmTask.get("PRO_ID"));
				double hrsIdealTime = 0;
				if(hmProData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProData.get("PRO_BILLING_ACTUAL_TYPE").equals("M")) {
					hrsIdealTime = (idealTime * 30) * 8;
				} else if(hmProData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProData.get("PRO_BILLING_ACTUAL_TYPE").equals("D")) {
					hrsIdealTime = idealTime * 8;
				} else if(hmProData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProData.get("PRO_BILLING_ACTUAL_TYPE").equals("H")) {
					hrsIdealTime = idealTime;
				}
				hmProTask.put("TASK_EST_TIME", uF.formatIntoTwoDecimalWithOutComma(hrsIdealTime));
				
				pst = con.prepareStatement("select sum(actual_hrs) as actual_hrs from task_activity where activity_id = ?");
				pst.setInt(1, uF.parseToInt(hmTask.get("TASK_ID")));
//				System.out.println("pst======>"+pst);
				double hrsActualTime = 0;
				rs = pst.executeQuery();
				while(rs.next()) {
					hrsActualTime = rs.getDouble("actual_hrs");
				}
				rs.close();
				pst.close();
				
				hmProTask.put("TASK_SPENT_TIME", uF.formatIntoTwoDecimalWithOutComma(hrsActualTime));
				 
				Date dtDeadline = uF.getDateFormat(hmTask.get("TASK_DEADLINE"), DBDATE);
				Date dtCurrentDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
				
				if(dtDeadline!=null && dtCurrentDate!=null && dtDeadline.after(dtCurrentDate)) {
					if(hrsActualTime <= hrsIdealTime) {
						 /*hmProTask.put("TASK_TIME_INDICATOR", "<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
						hmProTask.put("TASK_TIME_INDICATOR", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Actual Time < Estimated\"></i>");
						
					} else {
						/*hmProTask.put("TASK_TIME_INDICATOR", "<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
						hmProTask.put("TASK_TIME_INDICATOR", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Actual Time > Estimated\"></i>");
						
					}
				} else if(dtDeadline!=null && dtCurrentDate!=null && (dtCurrentDate.after(dtDeadline) || dtCurrentDate.equals(dtDeadline))) {
					/*hmProTask.put("TASK_TIME_INDICATOR", "<img src=\"images1/icons/denied.png\" width=\"17px\">");*/
					hmProTask.put("TASK_TIME_INDICATOR", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Actual Date > Deadline\"></i>");
					
				} else {
					hmProTask.put("TASK_TIME_INDICATOR", "");
				}
				
				alProTask.add(hmProTask);
			}
			
			request.setAttribute("alProTask", alProTask);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	private void getProjectCosting(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			
			String strStartDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 30), DBDATE, DATE_FORMAT);
			String strEndDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			
//			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", true, strStartDate, strEndDate, uF);
			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", true, null, null, uF);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from projectmntnc pmc where pmc.approve_status = 'approved' and pro_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst ===> " + pst);  
			rs=pst.executeQuery();
			StringBuilder sbProCosting = new StringBuilder();
			while(rs.next()) {
				
				Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
				Map<String, String> hmProActualCostTimeAndBillCost = CF.getProjectActualCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
				double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("pro_id")));
				
				sbProCosting.append("{'project':'"+rs.getString("pro_name").replaceAll("[^a-zA-Z0-9]", "")+"',");
				sbProCosting.append("'salary': "+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProActualCostTimeAndBillCost.get("proActualCost")))+",");
				sbProCosting.append("'reimbursement': "+uF.formatIntoTwoDecimalWithOutComma(dblReimbursement)+"");
				sbProCosting.append("},");
				
			}
			rs.close();
			pst.close();
			
			if(sbProCosting.length()>1) {
				sbProCosting.replace(0, sbProCosting.length(), sbProCosting.substring(0, sbProCosting.length()-1));
	        }
			
			request.setAttribute("sbProCosting", sbProCosting.toString());
//			System.out.println("sbProCosting ===> " + sbProCosting.toString());  
			
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
	}
	
	private void getWeeklyWorkProgress(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(currDate+"", DBDATE, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(currDate+"", DBDATE, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(currDate+"", DBDATE, "yyyy")));
			
            int nMonthStart = cal.getActualMinimum(Calendar.DATE);
			int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
			int nMonth = cal.get(Calendar.MONTH);
			
			String strStartDate =  (nMonthStart < 10 ? "0"+nMonthStart : nMonthStart) + "/" + ((cal.get(Calendar.MONTH) + 1) < 10 ? "0"+(cal.get(Calendar.MONTH) + 1):(cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
			String strEndDate =  (nMonthEnd < 10 ? "0"+nMonthEnd : nMonthEnd) + "/" + ((cal.get(Calendar.MONTH) + 1) < 10 ? "0"+(cal.get(Calendar.MONTH) + 1):(cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
			
			List<List<String>> weekdates = new ArrayList<List<String>>();
			uF.getMonthWeeksDate(weekdates,""+((cal.get(Calendar.MONTH) + 1) < 10 ? "0"+(cal.get(Calendar.MONTH) + 1):(cal.get(Calendar.MONTH) + 1)),""+cal.get(Calendar.YEAR),DATE_FORMAT);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from projectmntnc where pro_id > 0 and pro_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmProject = new LinkedHashMap<String, String>();
			while(rs.next()){
				hmProject.put(rs.getString("pro_id"), uF.showData(rs.getString("pro_name"), ""));
				
				request.setAttribute(TITLE, "Project Snapshot of "+uF.showData(rs.getString("pro_name"), ""));
			}
			rs.close(); 
			pst.close();
			
			Map<String, String> hmCompleteTotalCnt = new HashMap<String, String>();
	        Map<String, String> hmActiveTotalCnt = new HashMap<String, String>();
	        Map<String, String> hmOverdueTotalCnt = new HashMap<String, String>();
			if(hmProject != null && hmProject.size() > 0){
                Iterator<String> it = hmProject.keySet().iterator();
				while(it.hasNext()){
					String strProId = it.next();
					
					int x = 0;
					for(int i = 0; weekdates!=null && i < weekdates.size();i++){
						List<String> week = weekdates.get(i);
						x++;
						/**Complete Task Count
						 * */
						pst = con.prepareStatement("select count(*) as cnt,ai.pro_id from activity_info ai,projectmntnc p where ai.pro_id=p.pro_id " +
								"and ai.pro_id=? and end_date between ? and ? and ai.approve_status='approved' " +
								"group by ai.pro_id,ai.end_date order by ai.pro_id,ai.end_date");
						pst.setInt(1, uF.parseToInt(strProId));
						pst.setDate(2, uF.getDateFormat(week.get(0), DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(week.get(1), DATE_FORMAT));
//						System.out.println("pst======>"+pst);
						rs = pst.executeQuery();
						int nComplete = 0;
						while(rs.next()){
							nComplete = rs.getInt("cnt");
						}
						rs.close();
						pst.close();
						
						int nCompleteTotalCnt = uF.parseToInt(hmCompleteTotalCnt.get(x+"week"));
						nCompleteTotalCnt +=nComplete;
						hmCompleteTotalCnt.put(x+"week", ""+nCompleteTotalCnt);
						
						/**Complete Task Count end
						 * */
						
						/**Active Task Count
						 * */
						pst = con.prepareStatement("select count(*) as cnt from activity_info ai,projectmntnc p where ai.pro_id=p.pro_id " +
								"and ai.approve_status='n' and ai.pro_id=? and ai.deadline >= ? and ai.start_date < ?");
						pst.setInt(1, uF.parseToInt(strProId));
						pst.setDate(2, uF.getDateFormat(week.get(1), DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(week.get(1), DATE_FORMAT));
						rs = pst.executeQuery();
						int nActive = 0;
						while(rs.next()){
							nActive = rs.getInt("cnt");
						}
						rs.close();
						pst.close();
						
						int nActiveTotalCnt = uF.parseToInt(hmActiveTotalCnt.get(x+"week"));
						nActiveTotalCnt +=nActive;
						hmActiveTotalCnt.put(x+"week", ""+nActiveTotalCnt);
						
						/**Active Task Count
						 * */
						
						/**Overdue Task Count
						 * */
						pst = con.prepareStatement("select count(*) as cnt from activity_info ai,projectmntnc p where ai.pro_id=p.pro_id " +
								"and ai.approve_status='n' and ai.pro_id=? and ai.deadline < ?");
						pst.setInt(1, uF.parseToInt(strProId));
						pst.setDate(2, uF.getDateFormat(week.get(1), DATE_FORMAT));
						rs = pst.executeQuery();
						int nOverdue = 0;
						while(rs.next()){
							nOverdue = rs.getInt("cnt");
						}
						rs.close();
						pst.close();
						
						int nOverdueTotalCnt = uF.parseToInt(hmOverdueTotalCnt.get(x+"week"));
						nOverdueTotalCnt +=nOverdue;
						hmOverdueTotalCnt.put(x+"week", ""+nOverdueTotalCnt);
						
						/**Overdue Task Count
						 * */
					}
                }
                
			}
			
			StringBuilder sbWork 	= new StringBuilder();
			int x = 0;
			for(int i = 0; weekdates!=null && i < weekdates.size();i++){
				x++;
				sbWork.append("{'week':'"+x+"wk', " +
						"'completed': "+uF.parseToInt(hmCompleteTotalCnt.get(x+"week"))+"," +
						"'active': "+uF.parseToInt(hmActiveTotalCnt.get(x+"week"))+"," +
						"'overdue': "+uF.parseToInt(hmOverdueTotalCnt.get(x+"week"))+"},");
				
            }
            if(sbWork.length()>1) {
				sbWork.replace(0, sbWork.length(), sbWork.substring(0, sbWork.length()-1));
            }
			
			request.setAttribute("sbWork", sbWork.toString());
			
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
	}
	
	
	private void getProjectEffort(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			
			String[] strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
			String strCalendarYearStart = strCalendarYearDates[0];
			String strCalendarYearEnd = strCalendarYearDates[1];
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			String strMonth = ""+uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"));
			String strYear = ""+uF.parseToInt(uF.getDateFormat(strCalendarYearEnd+"", DATE_FORMAT, "yyyy"));
			
			List<List<String>> weekdates = new ArrayList<List<String>>();
			uF.getMonthWeeksDate(weekdates,strMonth,strYear,DATE_FORMAT);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select eod.emp_id,epd.emp_fname,epd.emp_mname,epd.emp_lname,epd.emp_image from employee_personal_details epd," +
					"employee_official_details eod where epd.emp_per_id=eod.emp_id ");
			sbQuery.append("and eod.emp_id in(select distinct(emp_id) from project_emp_details where emp_id > 0 ");
			sbQuery.append(" and pro_id in (select pro_id from projectmntnc where pro_id > 0 and pro_id=?))");
			sbQuery.append(" order by epd.emp_fname,epd.emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alPeople = new ArrayList<Map<String,String>>();
			while(rs.next()) {
				Map<String, String> hmPeople = new HashMap<String, String>();
				hmPeople.put("EMP_ID", rs.getString("emp_id"));	
				
				/*String strMiddleName = "";
				if(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("") && !rs.getString("emp_mname").trim().equalsIgnoreCase("NULL")){
					strMiddleName = rs.getString("emp_mname")+" ";
				}*/
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmPeople.put("EMP_NAME", uF.showData(rs.getString("emp_fname"), "")+strEmpMName+" "+uF.showData(rs.getString("emp_lname"), ""));
				hmPeople.put("EMP_IMAGE", uF.showData(rs.getString("emp_image"), ""));
				
				alPeople.add(hmPeople); 
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmBillable = new HashMap<String, String>();
            Map<String, String> hmNonBillable = new HashMap<String, String>();
            
			if(alPeople != null && alPeople.size() > 0){
				for(int j =0; j<alPeople.size(); j++){
					Map<String, String> hmPeople = alPeople.get(j);
					String strEmpId = hmPeople.get("EMP_ID");
					StringBuilder sbBillableCount = null;
					StringBuilder sbNonBillableCount = null;
					double dblBillableCnt = 0.0d;
					double dblNonBillableCnt = 0.0d;
					int x = 0;
					
					for(int i = 0; weekdates!=null && i < weekdates.size();i++){
						List<String> week = weekdates.get(i);
						x++;
						/**Billable Count
						 * */
						pst = con.prepareStatement("select sum(billable_hrs) as billable_hrs from task_activity where emp_id = ? and activity_id > 0 " +
								"and task_date between ? and ? and is_billable =true and activity_id in (select ai.task_id from activity_info ai, " +
								"projectmntnc pmc where ai.pro_id = pmc.pro_id and pmc.pro_id=?) ");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getDateFormat(week.get(0), DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(week.get(1), DATE_FORMAT));
						pst.setInt(4, uF.parseToInt(getPro_id()));
//						System.out.println("pst======>"+pst);
						rs = pst.executeQuery();
						double dblBillable = 0.0d;
						while(rs.next()){
							dblBillable = uF.parseToDouble(rs.getString("billable_hrs"));
						}
						rs.close();
						pst.close();
						
						if(sbBillableCount == null){
							sbBillableCount = new StringBuilder();
							sbBillableCount.append(""+dblBillable);
						} else {
							sbBillableCount.append(","+dblBillable);
						}
						dblBillableCnt +=dblBillable;
						/**Billable Count end
						 * */
						
						/**NonBillable Count
						 * */
						pst = con.prepareStatement("select sum(billable_hrs) as billable_hrs, sum(actual_hrs) as actual_hrs from task_activity " +
								"where emp_id = ? and activity_id > 0 and task_date between ? and ? and activity_id in (select ai.task_id from activity_info ai, " +
								"projectmntnc pmc where ai.pro_id = pmc.pro_id and pmc.pro_id=?)");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getDateFormat(week.get(0), DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(week.get(1), DATE_FORMAT));
						pst.setInt(4, uF.parseToInt(getPro_id()));
//						System.out.println("pst======>"+pst);
						rs = pst.executeQuery();
						double dblNonBillable = 0.0d;
						while(rs.next()){
							dblNonBillable = uF.parseToDouble(rs.getString("actual_hrs")) - uF.parseToDouble(rs.getString("billable_hrs"));
						}
						rs.close();
						pst.close();
						
						if(sbNonBillableCount == null){
							sbNonBillableCount = new StringBuilder();
							sbNonBillableCount.append(""+dblNonBillable);
						} else {
							sbNonBillableCount.append(","+dblNonBillable);
						}
						dblNonBillableCnt +=dblNonBillable;
						/**NonBillable Count
						 * */
						
					}
					
					hmBillable.put(strEmpId+"_BILLABLE", sbBillableCount.toString());
					hmBillable.put(strEmpId+"_BILLABLE_COUNT", ""+dblBillableCnt);
					
					hmNonBillable.put(strEmpId+"_NON_BILLABLE", sbNonBillableCount.toString());
					hmNonBillable.put(strEmpId+"_NON_BILLABLE_COUNT", ""+dblNonBillableCnt);
				}
			}
			
			request.setAttribute("alPeople", alPeople);
			request.setAttribute("hmBillable", hmBillable);
			request.setAttribute("hmNonBillable", hmNonBillable);
			
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
	}
	
	
	private void getProjectandBusinessDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			
			/**
			 * Project Profit
			 * */
			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", false, null, null, uF);
			
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select pro_id, pro_name, billing_type, billing_amount, idealtime, start_date, deadline, client_id, actual_calculation_type, " +
			"bill_days_type, hours_for_bill_day, added_by, curr_id,billing_curr_id from projectmntnc pmc where pmc.pro_id > 0 and pmc.pro_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			double dblBugedtedAmt = 0;
			double dblActualAmt = 0;
			double dblBillableAmt = 0;
			String strCurr = null;
			while(rs.next()){
				Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
				Map<String, String> hmCurr = hmCurrencyMap.get(rs.getString("curr_id"));
				
				strCurr = hmCurr != null ? hmCurr.get("SHORT_CURR") : "";
				
				Map<String, String> hmProActualCostTime = new HashMap<String, String>();
				Map<String, String> hmProBillCost = new HashMap<String, String>();
				if("M".equalsIgnoreCase(rs.getString("actual_calculation_type"))) { 
					hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData);
					hmProBillCost = CF.getMonthlyProjectBillableCostAndTime(con,request,CF, uF, rs.getString("pro_id"), hmProjectData);
				} else {
					hmProActualCostTime = CF.getProjectActualCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
					hmProBillCost = CF.getProjectBillableCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
				}
				Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con, uF, rs.getString("pro_id"), hmProjectData);
				double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("pro_id")));
				if("F".equalsIgnoreCase(rs.getString("billing_type"))) { 
					dblBillableAmt += uF.parseToDouble(rs.getString("billing_amount"));
				} else {
					dblBillableAmt += uF.parseToDouble(hmProBillCost.get("proBillableCost"));
				}
				 
				 
				dblBugedtedAmt += uF.parseToDouble(hmProBudgetedCostAndTime.get("proBudgetedCost"));
				dblActualAmt += (uF.parseToDouble(hmProActualCostTime.get("proActualCost")) + dblReimbursement);
				
			}
			rs.close(); 
			pst.close();
			
			double dblProfit = 0.0d;
			double dblProfitMargin = 0.0d;
			if(dblBillableAmt > 0) {
				dblProfit = (dblBillableAmt - dblActualAmt);
				dblProfitMargin = (dblProfit/dblBillableAmt) * 100;
			}
			
			request.setAttribute("strCurr", strCurr);
			request.setAttribute("dblProfit", uF.formatIntoTwoDecimal(dblProfit));
			request.setAttribute("dblProfitMargin", uF.formatIntoTwoDecimal(dblProfitMargin));
			request.setAttribute("dblBugedtedAmt", uF.formatIntoTwoDecimal(dblBugedtedAmt));
			request.setAttribute("dblActualAmt", uF.formatIntoTwoDecimal(dblActualAmt));
			/**
			 * Project Profit end 
			 * */
			
			/**
			 * Bills & receipts
			 * */
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(oc_invoice_amount) as oc_invoice_amount " +
					"from promntc_invoice_details where pro_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			double dblBilled = 0.0d;
			while(rs.next()){
				dblBilled = uF.parseToDouble(rs.getString("oc_invoice_amount"));
			}
			rs.close();
			pst.close();
			request.setAttribute("dblBilled", uF.formatIntoTwoDecimal(dblBilled));
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(received_amount) as received_amount from promntc_bill_amt_details " +
					"where pro_id =? ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			double dblReceived = 0.0d;
			while(rs.next()){
				dblReceived = uF.parseToDouble(rs.getString("received_amount"));
			}
			rs.close();
			pst.close();
			request.setAttribute("dblReceived", uF.formatIntoTwoDecimal(dblReceived));
			
//			StringBuilder sbBillsDonut 	= new StringBuilder();
//			sbBillsDonut.append("{'protype':'Bills', 'amt': "+uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblBilled))+"},");
//			sbBillsDonut.append("{'protype':'Received', 'amt': "+uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblReceived))+"}");
			
			StringBuilder sbBillsDonut 	= new StringBuilder();
			double dblReceivePercentage = (uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblReceived))/uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblBilled))) * 100;
			double dblBillPercentage = 100 - dblReceivePercentage;
			sbBillsDonut.append("{'category': 'Received from Billed','Pending': "+uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblBillPercentage))+"," +
					"'Received': "+uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblReceivePercentage))+"}");
			request.setAttribute("sbBillsDonut", sbBillsDonut.toString());
			/**
			 * Bills & receipts end
			 * */
			
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
	}
	
	private void getTeamTaskDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			List<Map<String, String>> alTeamMember = new ArrayList<Map<String, String>>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select epd.* from (select distinct(ped.emp_id) as emp_id from project_emp_details ped,projectmntnc pmt " +
					"where ped.pro_id = pmt.pro_id and ped.pro_id=?) a, employee_personal_details epd," +
					"employee_official_details eod where a.emp_id=epd.emp_per_id and a.emp_id=eod.emp_id and epd.emp_per_id=eod.emp_id " +
					"and epd.is_alive=true order by epd.emp_fname,epd.emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getPro_id()));  
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			List<String> alEmp = new ArrayList<String>();
			while(rs.next()){
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				String strShortLName = (rs.getString("emp_lname") !=null && !rs.getString("emp_lname").trim().equals("")) ? String.valueOf(rs.getString("emp_lname").trim().charAt(0)) : "";
				String strEmpShortLName = uF.showData(rs.getString("emp_fname"), "")+" "+strShortLName.toUpperCase();
				
				
				String strEmpName = rs.getString("emp_fname") + strEmpMName + " " + rs.getString("emp_lname");
				String strEmpImage = uF.showData(rs.getString("emp_image"), "");
				
				
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("EMP_ID", rs.getString("emp_per_id"));
				hmInner.put("EMP_SHORT_NAME", strEmpShortLName);
				hmInner.put("EMP_NAME", strEmpName); 
				hmInner.put("EMP_IMAGE", strEmpImage);
				
				alTeamMember.add(hmInner);
				
				alEmp.add(rs.getString("emp_per_id"));
			}
			rs.close();
			pst.close();
			
			int nMemAssigned = 0;
			int nMemUnAssigned = 0;
			for(int i = 0; alEmp!=null && i < alEmp.size(); i++){
				sbQuery = new StringBuilder();
				sbQuery.append("select * from activity_info where pro_id=? and resource_ids like '%,"+alEmp.get(i)+",%'");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getPro_id()));
				rs = pst.executeQuery();
				boolean flag = false;
				if(rs.next()){
					flag = true;
				}
				rs.close();
				pst.close();
				
				if(flag){
					nMemAssigned++;
				} else {
					nMemUnAssigned++;
				}
			}
			request.setAttribute("nMemAssigned", ""+nMemAssigned);
			request.setAttribute("nMemUnAssigned", ""+nMemUnAssigned);
			
			int nTotalTask = 0;
			sbQuery = new StringBuilder();
			sbQuery.append("select count(task_id) as cnt from activity_info where pro_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			while(rs.next()){
				nTotalTask = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			request.setAttribute("nTotalTask", ""+nTotalTask);
			
			int nTaskAssigned = 0;
			sbQuery = new StringBuilder();
			sbQuery.append("select count(task_id) as cnt from activity_info where pro_id=? " +
					"and (resource_ids is not null and resource_ids !='' and resource_ids!=',,')");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			while(rs.next()){
				nTaskAssigned = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			request.setAttribute("nTaskAssigned", ""+nTaskAssigned);
			
			int nTaskUnAssigned = 0;
			sbQuery = new StringBuilder();
			sbQuery.append("select count(task_id) as cnt from activity_info where pro_id=? " +
					"and (resource_ids is null or resource_ids ='' or resource_ids=',,')");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			while(rs.next()){
				nTaskUnAssigned = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			request.setAttribute("nTaskUnAssigned", ""+nTaskUnAssigned);
			
			request.setAttribute("alTeamMember", alTeamMember);
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
	}
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getPro_id() {
		return pro_id;
	}

	public void setPro_id(String pro_id) {
		this.pro_id = pro_id;
	}

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

}
