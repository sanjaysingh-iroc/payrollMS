package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectGanttChart extends ActionSupport implements
		ServletRequestAware, ServletResponseAware, SessionAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	int pro_id;
	String emp_name;
	String pro_name;
	String actual_hrs;
	String hrs_or_days;
	String e_hrs; 
	double actual_days; 
	double ideal_days;
	int teamsize; 
	String strUsreType;
	
	CommonFunctions CF;
	public HttpServletRequest request;
	List<String> proj_time = new ArrayList<String>();
//	List<Integer> indexPerTask = new ArrayList<Integer>();
//	Map<Integer, List<String>> alPerTask = new HashMap<Integer, List<String>>();
	List<String> proreportList = new ArrayList<String>();
	List<Integer> activity_index = new ArrayList<Integer>();
	Map<Integer, List<String>> activity_al = new HashMap<Integer, List<String>>();
	List<String> sentreport = new ArrayList<String>();
	Map<Integer, String> EmpNameMap = new HashMap<Integer, String>();
//	Map<String,String> 	hmServiceDesc=new HashMap<String, String>();
	public String execute() {
			
			session = request.getSession();
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if (CF == null)
				return LOGIN;
			UtilityFunctions uF = new UtilityFunctions();
			
			strUsreType = (String)session.getAttribute(BASEUSERTYPE);
			request.setAttribute(PAGE, "/jsp/task/ProjectSummaryView.jsp");
			request.setAttribute(TITLE, "Project Summary View");
//			hmServiceDesc=CF.getServiceDesc();
			if (session.getAttribute("pro_id") != null) {
				pro_id =uF.parseToInt((String) session.getAttribute("pro_id"));
			}
			getProjectDetails(pro_id+"");

		session.getAttribute("pro_id"); 
		return SUCCESS;
	}

	public void getActivityDetails(Connection con, int proId) {
		
		ResultSet rs = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			Map<String, String> hmTeamLead = CF.getProjectTeamLeads(con, uF, proId+"");
			
			if(strUsreType!=null && strUsreType.equalsIgnoreCase(EMPLOYEE)) {
				pst = con.prepareStatement("select * from activity_info where resource_ids like '%"+(String)session.getAttribute(EMPID)+"%' and _isfinish = false and parent_task_id = 0 order by task_id");
//				pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
			} else {
				pst = con.prepareStatement("select *,ai.start_date as a_start_date,pmc.start_date as p_start_date, ai.parent_task_id, ai.resource_ids, ai.completed as a_completed, ai.emp_id as a_emp_id, ai.deadline as a_deadline, pmc.deadline as p_deadline from activity_info ai, projectmntnc pmc where ai.pro_id=? and pmc.pro_id=ai.pro_id and ai.parent_task_id = 0 order by task_id ");
				pst.setInt(1, proId);
			}
			rs = pst.executeQuery();
			List<String> alInner1 = new ArrayList<String>();
			List<List<String>> alGanntChart = new ArrayList<List<String>>();
			int count = 0;
			while (rs.next()) {
				count++;
				alInner1 = new ArrayList<String>();
				
				java.util.Date dtEndDate = uF.getDateFormatUtil(rs.getString("end_date"), DBDATE);
				java.util.Date dtDeadlineDate = uF.getDateFormatUtil(rs.getString("deadline"), DBDATE);
				
				String color=rs.getString("color_code");
				String c_code="ff33ff";
				if(color != null) {
					c_code=color.replaceAll("#", "");
				}
				
				if(count==1) {
					
					alInner1.add(rs.getString("pro_id")); 		//pID
					alInner1.add(rs.getString("pro_name")); 		//pName
					alInner1.add(uF.getDateFormat(rs.getString("p_start_date"), DBDATE, DATE_FORMAT)); 		//pStart
					alInner1.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT)); 		//pEnd
					alInner1.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT)); 		//pDeadline
					
					
					alInner1.add(c_code); 		//pColor
					alInner1.add(""); 		//pLink
					alInner1.add("0"); 		//pMile
					alInner1.add(""); 		//pRes
					alInner1.add(rs.getString("completed")); 		//pComp
					alInner1.add("1"); 		//pGroup
					alInner1.add("0"); 		//pParent
					alInner1.add("1"); 		//pOpen
					alInner1.add("0"); 		//pDepend
					alInner1.add("Caption"); 		//pCaption
					
					alGanntChart.add(alInner1);
					alInner1 = new ArrayList<String>();
					
					alInner1.add(rs.getString("task_id")); 		//pID
					alInner1.add(rs.getString("activity_name")); 		//pName
					alInner1.add(uF.getDateFormat(rs.getString("a_start_date"), DBDATE, DATE_FORMAT)); 		//pStart
					if(rs.getString("end_date")!=null) {
						alInner1.add(uF.getDateFormat(rs.getString("end_date"), DBDATE, DATE_FORMAT)); 		//pEnd
					} else {
						alInner1.add(uF.getDateFormat(rs.getString("a_deadline"), DBDATE, DATE_FORMAT)); 		//pEnd
					}
					alInner1.add(uF.getDateFormat(rs.getString("a_deadline"), DBDATE, DATE_FORMAT)); 		//pDeadline
					
					if(dtEndDate!=null && dtDeadlineDate!=null && dtEndDate.after(dtDeadlineDate)) {
						alInner1.add("ff3333"); 		//pColor
					} else {
						alInner1.add(c_code); 		//pColor
					}
					
					alInner1.add(""); 		//pLink
					if(rs.getBoolean("is_milestone")) {
						alInner1.add("1"); 		//pMile
					} else {
						alInner1.add("0"); 		//pMile
					}
					alInner1.add("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+uF.showData(CF.getResourcesName(con, rs.getString("resource_ids"), hmTeamLead), "-")+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"); 		//pRes
					alInner1.add(rs.getString("a_completed")); 		//pComp
					
					if(rs.getBoolean("is_milestone")) {
						alInner1.add(rs.getString("pro_id")); 		//pGroup
					} else {
						alInner1.add("0"); 		//pGroup
					}
					alInner1.add(rs.getString("pro_id")); 		//pParent
					alInner1.add("0"); 		//pOpen
					alInner1.add(uF.parseToInt(rs.getString("dependency_task"))+""); 		//pDepend
					alInner1.add("Task Caption"); 		//pCaption
				} else {
					alInner1.add(rs.getString("task_id")); 		//pID
					alInner1.add(rs.getString("activity_name")); 		//pName
					alInner1.add(uF.getDateFormat(rs.getString("a_start_date"), DBDATE, DATE_FORMAT)); 		//pStart
					
					if(rs.getString("end_date")!=null) {
						alInner1.add(uF.getDateFormat(rs.getString("end_date"), DBDATE, DATE_FORMAT)); 		//pEnd
					} else {
						alInner1.add(uF.getDateFormat(rs.getString("a_deadline"), DBDATE, DATE_FORMAT)); 		//pEnd
					}
					alInner1.add(uF.getDateFormat(rs.getString("a_deadline"), DBDATE, DATE_FORMAT)); 		//pDeadline
					
					if(dtEndDate!=null && dtDeadlineDate!=null && dtEndDate.after(dtDeadlineDate)) {
						alInner1.add("ff3333"); 		//pColor
					} else {
						alInner1.add(c_code); 		//pColor
					}
					
					alInner1.add(""); 		//pLink
					if(rs.getBoolean("is_milestone")) {
						alInner1.add("77"); 		//pMile
					} else {
						alInner1.add("0"); 		//pMile
					}
					
					alInner1.add("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+uF.showData(CF.getResourcesName(con, rs.getString("resource_ids"), hmTeamLead), "-")+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"); 		//pRes
					alInner1.add(rs.getString("a_completed")); 		//pComp
					alInner1.add("0"); 		//pGroup
					alInner1.add(rs.getString("pro_id")); 		//pParent
					alInner1.add("0"); 		//pOpen
					alInner1.add(uF.parseToInt(rs.getString("dependency_task"))+""); 		//pDepend
					alInner1.add("Task Caption"); 		//pCaption
				}
				alGanntChart.add(alInner1);
			}
			rs.close();
			pst.close();
			
			
			if(strUsreType!=null && strUsreType.equalsIgnoreCase(EMPLOYEE)) {
				pst = con.prepareStatement("select * from activity_info where resource_ids like '%"+(String)session.getAttribute(EMPID)+"%' and _isfinish = false and parent_task_id != 0 order by task_id");
//				pst = con.prepareStatement("select * from activity_info where emp_id =? and _isfinish = false order by task_id");
//				pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
			} else {
				pst = con.prepareStatement("select *,ai.start_date as a_start_date,pmc.start_date as p_start_date, ai.parent_task_id, ai.resource_ids, ai.completed as a_completed, ai.emp_id as a_emp_id, ai.deadline as a_deadline, pmc.deadline as p_deadline from activity_info ai, projectmntnc pmc where ai.pro_id=? and pmc.pro_id=ai.pro_id and ai.parent_task_id != 0 order by task_id ");
				pst.setInt(1, proId);
			}
			rs = pst.executeQuery();
			Map<String, List<List<String>>> hmSubTaskGanntChart = new HashMap<String, List<List<String>>>();
			List<List<String>> alSubTaskGanntChart = new ArrayList<List<String>>();
			while (rs.next()) {
				alSubTaskGanntChart = hmSubTaskGanntChart.get(rs.getString("parent_task_id"));
				if(alSubTaskGanntChart == null) alSubTaskGanntChart = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				
				java.util.Date dtEndDate = uF.getDateFormatUtil(rs.getString("end_date"), DBDATE);
				java.util.Date dtDeadlineDate = uF.getDateFormatUtil(rs.getString("deadline"), DBDATE);
				
				String color=rs.getString("color_code");
				String c_code="ff33ff";
				if(color != null) {
					c_code=color.replaceAll("#", "");
				}
				
				innerList.add(rs.getString("task_id")); 		//STID 0
				innerList.add(rs.getString("activity_name") +" [ST]"); 		//STName 1
				innerList.add(uF.getDateFormat(rs.getString("a_start_date"), DBDATE, DATE_FORMAT)); 		//pStart 2
				
				if(rs.getString("end_date")!=null) {
					innerList.add(uF.getDateFormat(rs.getString("end_date"), DBDATE, DATE_FORMAT)); 		//pEnd 3
				} else {
					innerList.add(uF.getDateFormat(rs.getString("a_deadline"), DBDATE, DATE_FORMAT)); 		//pEnd 3
				}
				innerList.add(uF.getDateFormat(rs.getString("a_deadline"), DBDATE, DATE_FORMAT)); 		//pDeadline 4
				
				if(dtEndDate!=null && dtDeadlineDate!=null && dtEndDate.after(dtDeadlineDate)) {
					innerList.add("ff3333"); 		//pColor 5
				} else {
					innerList.add(c_code); 		//pColor 5
				}
				
				innerList.add(""); 		//pLink 6 
				if(rs.getBoolean("is_milestone")) {
					innerList.add("77"); 		//pMile 7
				} else {
					innerList.add("0"); 		//pMile 7
				}
				
				innerList.add("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+uF.showData(CF.getResourcesName(con, rs.getString("resource_ids"), hmTeamLead), "-")+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"); 		//pRes 8
				innerList.add(rs.getString("a_completed")); 		//pComp 9
				innerList.add("0"); 		//pGroup 10
				innerList.add(rs.getString("pro_id")); 		//pParent 11
				innerList.add("0"); 		//pOpen 12 
				innerList.add(uF.parseToInt(rs.getString("dependency_task"))+""); 		//pDepend 13
				innerList.add("Task Caption"); 		//pCaption 14
				
				alSubTaskGanntChart.add(innerList);
				hmSubTaskGanntChart.put(rs.getString("parent_task_id"), alSubTaskGanntChart);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmSubTaskGanntChart", hmSubTaskGanntChart);
			request.setAttribute("alGanntChart", alGanntChart);
			
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

	
	public void getNoOfDays(Connection con, int t_id) {
		ResultSet rs1 = null;
		PreparedStatement pst1 = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			String dl_date = "";
			String start_date = "";
			String end_date = "";
			pst1 = con.prepareStatement("select deadline,start_date,end_date from activity_info where task_id=?");
			pst1.setInt(1, t_id);
			rs1 = pst1.executeQuery();
			while (rs1.next()) {
				dl_date = rs1.getString(1);
				start_date = rs1.getString(2);
				end_date = rs1.getString(3);
			} 
			rs1.close();
			pst1.close();
			ideal_days = uF.parseToDouble(uF.dateDifference(start_date,DBDATE, dl_date, DBDATE));
			if(end_date!=null)
				actual_days = uF.parseToDouble(uF.dateDifference(start_date,DBDATE, end_date, DBDATE));
			else
				actual_days = uF.parseToDouble(uF.dateDifference(start_date,DBDATE, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs1 !=null){
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst1 !=null){
				try {
					pst1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	public void getActivityDates(Connection con, int proId) {

		ResultSet rs = null;
		PreparedStatement pst = null;
		List<String> taskdateList = new ArrayList<String>();
		List<String> empNameList = new ArrayList<String>();
		List<String> linkList = new ArrayList<String>();
		UtilityFunctions uF = new UtilityFunctions();
//			String ename = "";
//			int supervisor_emp_id = 0;
			try {
			Map<String, String> hmEmpNameMap = CF.getEmpNameMap(con, null, null);
			
			/*pst = con
			.prepareStatement("select task_date,emp_id from (select * from task_activity where task_date in " +
					"(select distinct(task_date) from task_activity where issent_report='true') and " +
					"activity_id in(select task_id from activity_info where pro_id=?) order by " +
					"task_date desc) a group by task_date,emp_id");*/
			
			pst = con.prepareStatement("select * from task_activity where issent_report = true and activity_id = ?");
			
			pst.setInt(1, proId);
			rs = pst.executeQuery();
			while(rs.next()) {
				taskdateList.add(uF.getDateFormat(rs.getString("task_date"), DBDATE, CF.getStrReportDateFormat()));
				empNameList.add(uF.showData((String)hmEmpNameMap.get(rs.getString("emp_id")), ""));
				linkList.add("<a href=\"GenerateTimeSheet.action?task_date="+rs.getString("task_date")+"" +
				"&emptype=admin&emp_id="+rs.getString("emp_id")+"&pro_id="+getPro_id()+"\" class=\"pdf\" >Pdf </a>" +
				"<a href=\"ExportToExcelTimeSheet.action?task_date="+rs.getString("task_date")+"" +
				"&emptype=admin&emp_id="+rs.getString("emp_id")+"&pro_id="+getPro_id()+"\" class=\"xls\">Excel </a>");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("taskdateList", taskdateList);
			request.setAttribute("empNameList", empNameList);
			request.setAttribute("linkList", linkList);
			/*pst = con
					.prepareStatement("select * from task_activity ta,activity_info ai where ta.emp_id=ai.emp_id and ta.task_date='2012-06-02' and ta.activity_id >0 and ai.pro_id=28 and ta.emp_id=645");
			pst.setInt(1,supervisor_emp_id);
			rs = pst.executeQuery();
			while(rs.next())
			{
				
				ename=rs.getString("emp_fname")+" "+rs.getString("emp_lname");
			}*/
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	public void getHrsOfProject(Connection con, int i) {
		
		ResultSet rs = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			pst = con.prepareStatement("select sum(idealtime) as idealtime from activity_info where pro_id=?");
			pst.setInt(1, i);
			rs = pst.executeQuery();
			while (rs.next()) {
				proj_time.add(uF.roundOffInTimeInHoursMins(uF.parseToDouble(rs.getString("idealtime"))));
			}
			rs.close();
			pst.close();
			proj_time.add(uF.formatIntoTwoDecimal(getTotalHrs(con, i)));
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

	public double getTotalHrs(Connection con, int proid) {
		ResultSet rs = null;
		PreparedStatement pst = null;
		
		double total_hrs = 0.0;
		try {
			List<Integer> ac = getTaskIds(con, proid);

//			DecimalFormat df = new DecimalFormat("#.##");
			for (int j = 0; j < ac.size(); j++) {
				pst = con.prepareStatement("select sum(actual_hrs) from task_activity where activity_id=?");
				pst.setInt(1, ac.get(j));
				rs = pst.executeQuery();
				while (rs.next()) {
					total_hrs += rs.getDouble(1);
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
		return total_hrs;
	}

	public List<Integer> getTaskIds(Connection con, int i) {
		ResultSet rs = null;
		PreparedStatement pst = null;
		
		List<Integer> ac_index = new ArrayList<Integer>();
		try {
			pst = con.prepareStatement("select task_id from activity_info where pro_id=?");
			pst.setInt(1, i);
			rs = pst.executeQuery();
			while (rs.next()) {
				ac_index.add(rs.getInt(1));
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
		return ac_index;
	}

	
	public void getProjectDetails(String proId) {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmServicemap = CF.getProjectServicesMap(con, true);
			String approve_date;
			getTeamSize(con, uF.parseToInt(proId));
			getTeamTaskDetails(con, uF.parseToInt(proId));
			getClientDetails(con, uF.parseToInt(proId));
			getTeamLeadName(con);
			getActivityDetails(con, uF.parseToInt(proId));
			getActivityDates(con, uF.parseToInt(proId));
//			getSentReportDetails();
			getHrsOfProject(con, uF.parseToInt(proId));
			pst = con.prepareStatement("select * from projectmntnc where pro_id=?");
			pst.setInt(1, uF.parseToInt(proId));
			rs = pst.executeQuery();
			while (rs.next()) {
//				List<String> alInner = new ArrayList<String>();
				int p_id = rs.getInt("pro_id");
				pro_name = rs.getString("pro_name");
				String description = rs.getString("short_description");
				String long_Description = rs.getString("description");
				String service = uF.showData(hmServicemap.get(rs.getString("service")), "");
				String deadline = rs.getString("deadline");
				e_hrs = rs.getString("idealtime");
				if(rs.getString("actual_calculation_type") != null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					hrs_or_days = " days";
					actual_hrs = CF.getProjectActualTime(con, rs.getString("pro_id"), rs.getString("actual_calculation_type"));
					e_hrs = rs.getString("idealtime");
				} else {
					hrs_or_days = " hrs";
					actual_hrs = CF.getProjectActualTime(con, rs.getString("pro_id"), rs.getString("actual_calculation_type"));
					e_hrs = rs.getString("idealtime");
				}
				String documents = rs.getString("document_name");
				String delivery_status = rs.getString("approve_status");
				approve_date = uF.getDateFormat(rs.getString("approve_date"),DBDATE, CF.getStrReportDateFormat());

				proreportList.add(pro_name);
				proreportList.add(service);
				proreportList.add("<a href=\"javascript:void(0)\" onclick=\"view('<b>Short Description:</b> <br/>"+uF.showData(description, "")+" <br/><br/><b>Long Description:</b> <br/>"+uF.showData(long_Description, "")+"')\">View");
//				alInner.add(description);
				if (documents != null)
					proreportList.add("<a href=\"taskuploads/" + documents+ "\" target=\"_blank\">" + documents + "</a>");
				else
					proreportList.add("No Attachments");
				proreportList.add(teamsize + "");
				proreportList.add(uF.removeNull(e_hrs) + hrs_or_days);
//				System.out.println("actual_hrs ===>>> " + actual_hrs);
				proreportList.add(uF.roundOffInTimeInHoursMins(uF.parseToDouble(actual_hrs)) + hrs_or_days);
				proreportList.add(uF.getDateFormat(deadline, DBDATE, CF.getStrReportDateFormat()));
				if (delivery_status.equals("n"))
					proreportList.add("Working");
				else
					proreportList.add("Completed");
				if (delivery_status.equals("n"))
					proreportList.add("Not Delivered");
				else
					proreportList.add("Delivered to Client");
				if(rs.getBoolean("ismonthly")) {
					proreportList.add("Monthly");
				} else {
					proreportList.add("Fixed");
				}
				if (approve_date.equals("-")) {
					sentreport.add("Not Sended");
				} else {
					sentreport.add(approve_date + ", Sent to Manager");
				}
			}
			rs.close();
			pst.close();
			
			
			Map<String, String> hmProjectInvoiceDetails = new HashMap<String, String>();
			
			/*
			 * pst = con.
			 * prepareStatement("select * from projectmntnc pmc, (select billable_amount, pc.pro_id, invoice_amount, invoice_date, invoice_number, paid_amount, pro_billing_id from project_cost pc left join projectmntnc_billing pmcb on pc.pro_id = pmcb.pro_id ) a where pmc.pro_id = a.pro_id and pmc.pro_id=?"
			 * ); pst.setInt(1, uF.parseToInt(proId)); rs = pst.executeQuery();
			 * 
			 * while(rs.next()) { if(rs.getString("invoice_date") != null) {
			 * hmProjectInvoiceDetails.put("INVOICE_DATE",
			 * uF.getDateFormat(rs.getString("invoice_date"), DBDATE,
			 * CF.getStrReportDateFormat()));
			 * hmProjectInvoiceDetails.put("INVOICE_NUMBER",
			 * rs.getString("invoice_number"));
			 * hmProjectInvoiceDetails.put("INVOICE_ID",
			 * rs.getString("pro_billing_id")); }
			 * 
			 * hmProjectInvoiceDetails.put("PAID_AMOUNT",
			 * uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString(
			 * "paid_amount")))); hmProjectInvoiceDetails.put("BILLING_AMOUNT",
			 * uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString(
			 * "billable_amount"))));
			 * 
			 * if(rs.getString("billing_type")!=null &&
			 * rs.getString("billing_type").equalsIgnoreCase("D")) {
			 * hmProjectInvoiceDetails.put("BILLING_TYPE", "Daily"); } else
			 * if(rs.getString("billing_type")!=null &&
			 * rs.getString("billing_type").equalsIgnoreCase("H")) {
			 * hmProjectInvoiceDetails.put("BILLING_TYPE", "Hourly"); } else
			 * if(rs.getString("billing_type")!=null &&
			 * rs.getString("billing_type").equalsIgnoreCase("F")) {
			 * hmProjectInvoiceDetails.put("BILLING_TYPE", "Fixed"); }
			 * 
			 * if(rs.getString("approve_status")!=null &&
			 * rs.getString("approve_status").equalsIgnoreCase("n")) {
			 * hmProjectInvoiceDetails.put("COMPLETED",
			 * "<img src=\"images1/icons/exclamation_mark_icon.png\" width=\"30\">"
			 * ); } else { hmProjectInvoiceDetails.put("COMPLETED",
			 * "<img src=\"images1/icons/hd_tick_20x20.png\">"); } } rs.close();
			 * pst.close();
			 */
			
			request.setAttribute("hmProjectInvoiceDetails", hmProjectInvoiceDetails);
			request.setAttribute("strProID", proId+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void getTeamLeadName(Connection con) {
		
		ResultSet rs = null;
		PreparedStatement pst = null;
		
		try {
			pst = con.prepareStatement("select * from employee_personal_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				EmpNameMap.put(rs.getInt("emp_per_id"), rs.getString("emp_fname"));
			}
			rs.close();
			pst.close();
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

	
	public void getTeamSize(Connection con, int proId) {
		ResultSet rs = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			pst = con.prepareStatement("select count(distinct(emp_id)) as team from project_emp_details where pro_id=?");
			pst.setInt(1, proId);
			rs = pst.executeQuery();
			while (rs.next()) {
				teamsize = uF.parseToInt(rs.getString("team"));
			}
			rs.close();
			pst.close();
			
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
	

	public void getTeamTaskDetails(Connection con, int proId) {

		ResultSet rs = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			Map<String, String> hmEmpNameMap = CF.getEmpNameMap(con, null, null);
			
			Map<String, String> hmTeamLead = CF.getProjectTeamLeads(con, uF, getPro_id()+"");
			
			pst = con.prepareStatement("select * from activity_info where pro_id=? and parent_task_id = 0 order by deadline desc");
			pst.setInt(1, proId);
			rs = pst.executeQuery();
			List<List<String>> alTeamActivities = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("task_id"));
				innerList.add(uF.showData(CF.getResourcesName(con, rs.getString("resource_ids"), hmTeamLead), "-"));
//				innerList.add(uF.showData(hmEmpNameMap.get(rs.getString("emp_id")), "") + (uF.parseToBoolean(hmTeamLead.get(rs.getString("emp_id")))==true ? " [TL]":""));
				innerList.add(uF.showData(rs.getString("activity_name"), "N/A"));
				innerList.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
				innerList.add(uF.showData(rs.getString("completed"), "0") +"%");
				
				alTeamActivities.add(innerList);
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select * from activity_info where pro_id=? and parent_task_id != 0 order by task_id desc");
			pst.setInt(1, proId);
			rs = pst.executeQuery();
			Map<String, List<List<String>>> hmTeamSubTask = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alTeamSubTask = new ArrayList<List<String>>();
			while (rs.next()) {
				alTeamSubTask = hmTeamSubTask.get(rs.getString("parent_task_id"));
				if(alTeamSubTask == null) alTeamSubTask = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("task_id"));
				innerList.add(uF.showData(CF.getResourcesName(con, rs.getString("resource_ids"), hmTeamLead), "-"));
//				innerList.add(uF.showData(hmEmpNameMap.get(rs.getString("emp_id")), "") + (uF.parseToBoolean(hmTeamLead.get(rs.getString("emp_id")))==true ? " [TL]":""));
				innerList.add(uF.showData(rs.getString("activity_name"), "N/A"));
				innerList.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
				innerList.add(uF.showData(rs.getString("completed"), "0") +"%");
				
				alTeamSubTask.add(innerList);
				hmTeamSubTask.put(rs.getString("parent_task_id"), alTeamSubTask);
			}
			rs.close();
			pst.close();
//			System.out.println("hmTeamSubTask ===>> " + hmTeamSubTask);
			
			request.setAttribute("hmTeamSubTask", hmTeamSubTask);
			request.setAttribute("alTeamActivities", alTeamActivities);
			
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
	
	
	public void getClientDetails(Connection con, int proId) {
		
		ResultSet rs = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
		
			Map<String, String> hmClientIndustries = new HashMap<String, String>();
			pst = con.prepareStatement("select * from client_industry_details");
			rs = pst.executeQuery();
			while(rs.next()) {
				hmClientIndustries.put(rs.getString("industry_id"), rs.getString("industry_name"));
			}
			rs.close();
			pst.close();
			
			List<String> alClientDetails = new ArrayList<String>();
			
			pst = con.prepareStatement("select * from projectmntnc p, client_poc cp where p.poc = cp.poc_id and pro_id = ?");
			pst.setInt(1, proId);
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
//				strEmpIdNew = rs.getString("emp_id");
				alClientDetails.add(uF.showData(CF.getClientNameById(con, rs.getString("client_id")), "N/A"));
				alClientDetails.add(uF.showData(rs.getString("contact_fname")+" "+rs.getString("contact_lname"), "N/A"));
				alClientDetails.add(uF.showData(rs.getString("contact_desig"), "N/A"));
				alClientDetails.add(uF.showData(rs.getString("contact_department"), "N/A"));
				alClientDetails.add(uF.showData(rs.getString("contact_email"), "N/A"));
				alClientDetails.add(uF.showData(rs.getString("contact_number"), "N/A"));
//				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alClientDetails", alClientDetails);
			
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
	
	
	public List<String> getProreportList() {
		return proreportList;
	}

	public void setProreportList(List<String> proreportList) {
		this.proreportList = proreportList;
	}

	public List<String> getSentreport() {
		return sentreport;
	}

	public void setSentreport(List<String> sentreport) {
		this.sentreport = sentreport;
	}

	public List<Integer> getActivity_index() {
		return activity_index;
	}

	public void setActivity_index(List<Integer> activity_index) {
		this.activity_index = activity_index;
	}

	public Map<Integer, List<String>> getActivity_al() {
		return activity_al;
	}

	public void setActivity_al(Map<Integer, List<String>> activity_al) {
		this.activity_al = activity_al;
	}

	public List<String> getProj_time() {
		return proj_time;
	}

	public void setProj_time(List<String> proj_time) {
		this.proj_time = proj_time;
	}

	public int getPro_id() {
		return pro_id;
	}

	public void setPro_id(int pro_id) {
		this.pro_id = pro_id;
	}

	public String getEmp_name() {
		return emp_name;
	}

	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
	}

	public String getPro_name() {
		return pro_name;
	}

	public void setPro_name(String pro_name) {
		this.pro_name = pro_name;
	}

	public String getActual_hrs() {
		return actual_hrs;
	}

	public void setActual_hrs(String actual_hrs) {
		this.actual_hrs = actual_hrs;
	}

	public String getHrs_or_days() {
		return hrs_or_days;
	}

	public void setHrs_or_days(String hrs_or_days) {
		this.hrs_or_days = hrs_or_days;
	}

	public String getE_hrs() {
		return e_hrs;
	}

	public void setE_hrs(String e_hrs) {
		this.e_hrs = e_hrs;
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setSession(Map arg0) {
		// TODO Auto-generated method stub
	}
}
