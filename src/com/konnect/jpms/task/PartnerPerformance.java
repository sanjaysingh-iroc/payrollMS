package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.views.xslt.ArrayAdapter;

import ChartDirector.AngularMeter;

import com.konnect.jpms.charts.SemiCircleMeter;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PartnerPerformance extends ActionSupport implements ServletRequestAware, IStatements {
	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF;
	 
	String selectOne;
	
	String f_start;
	String f_end;
	
	String btnSubmit;
	
	public String execute() {
		UtilityFunctions uF=new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/task/PartnerPerformance.jsp");
		request.setAttribute(TITLE, "Project Owner Performance");
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		String strUserType = (String)session.getAttribute(BASEUSERTYPE);
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}  */
		
		if(getSelectOne() == null) {
			setSelectOne("1");
		}
		
		getSelectedFilter(uF);
		
		getEmployeeKPI(strUserType, uF);
		
		if(getBtnSubmit() != null) {
			return SUCCESS;
		} else {
			return LOAD;
		}
	}

	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
			
		if(getSelectOne()!= null && !getSelectOne().equals("")) {
			alFilter.add("PERIOD");
			
			String strSelectOne="";
			if(uF.parseToInt(getSelectOne()) == 4) {
				strSelectOne="From - To";
			} else if(uF.parseToInt(getSelectOne()) == 1) {
				strSelectOne="Since last 3 months";
			} else if(uF.parseToInt(getSelectOne()) == 2) {
				strSelectOne="Since last 6 months";
			} else if(uF.parseToInt(getSelectOne()) == 3) {
				strSelectOne="Since last 1 year";
			}
			if(strSelectOne!=null && !strSelectOne.equals("")) {
				hmFilter.put("PERIOD", strSelectOne);
			}
		}
		
		if(uF.parseToInt(getSelectOne()) == 4) {
			alFilter.add("FROMTO");
			hmFilter.put("FROMTO", uF.getDateFormat(getF_start(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getF_end(), DATE_FORMAT, CF.getStrReportDateFormat()));
		} 
		
		String selectedFilter=CF.getSelectedFilter1(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	
	public void getEmployeeKPI(String strUserType, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
		
			con = db.makeConnection(con);
			
			if(uF.parseToInt(getSelectOne()) == 4 && (getF_start() == null || getF_start().equals("") || getF_end() == null || getF_end().equals(""))) {
				Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
				String startdate="01/"+uF.getDateFormat(""+currDate, DBDATE, "MM")+"/"+uF.getDateFormat(""+currDate, DBDATE, "yyyy");
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "MM"))-1);
				calendar.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "yyyy")));
				calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));

				Date date = calendar.getTime();
				DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
				String endDate=DATE_FORMAT.format(date);
				
				setF_start(startdate);
				setF_end(endDate);
			} else if(uF.parseToInt(getSelectOne()) != 4) {
				String strProDate = null;
				if(uF.parseToInt(getSelectOne()) == 3) {
					strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
				} else if(uF.parseToInt(getSelectOne()) == 2) {
					strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 180), DBDATE, DATE_FORMAT);
				} else if(uF.parseToInt(getSelectOne()) == 1) {
					strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 90), DBDATE, DATE_FORMAT);
				}
				setF_start(strProDate);
				setF_end(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT));
			}

			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null); 
			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
			
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 17-10-2022===	
//			sbQuery.append("select * from projectmntnc pmc where pro_id>0 and project_owner > 0 and approve_status != 'blocked'");
			sbQuery.append("select * from projectmntnc pmc where pro_id>0 and approve_status != 'blocked'");
		//===end parvez date: 17-10-2022===	
			
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and pmc.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and pmc.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(getF_start()!=null && getF_end()!=null) {
				sbQuery.append(" and start_date between '"+uF.getDateFormat(getF_start(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getF_end(), DATE_FORMAT, DBDATE)+"' ");
			}
			if(strUserType!=null && (strUserType.equalsIgnoreCase(MANAGER))) {
			//===start parvez date: 17-10-2022===	
//				sbQuery.append(" and project_owner="+uF.parseToInt((String)session.getAttribute(EMPID)));
				sbQuery.append(" and project_owners like '%,"+(String)session.getAttribute(EMPID)+",%' ");
			//===end parvez date: 17-10-2022===	
			}
//			sbQuery.append(" group by project_owner");
			
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			double dblActualAmt = 0;
			double dblBillableAmt = 0;
			
			double dblBugedtedTime = 0; 
			double dblActualTime = 0;
			
			double dblIdealTimeHrs = 0; 
			double dblActualTimeHrs = 0;
			Map<String, String> hmKPIM = new HashMap<String, String>();
			
			Map<String, String> hmKPIT = new HashMap<String, String>();
			
			List<String> alProOwner = new ArrayList<String>();
			Map<String, String> hmPOActBillAmt = new HashMap<String, String>();
			Map<String, String> hmPOActIdealTime = new HashMap<String, String>();
			Map<String, String> hmPOActIdealTimeHRS = new HashMap<String, String>();
			while(rs.next()) {
			//===start parvez date: 17-10-2022===	
				
			//===end parvez date: 17-10-2022===	
				
				/*
				
				if(!alProOwner.contains(rs.getString("project_owner"))) {
					alProOwner.add(rs.getString("project_owner"));
				}
				
				Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));

				Map<String, String> hmProActualCostTime = new HashMap<String, String>();
				Map<String, String> hmProBillCost = new HashMap<String, String>();
				if("M".equalsIgnoreCase(rs.getString("actual_calculation_type"))) { 
					hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData);
					hmProBillCost = CF.getMonthlyProjectBillableCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData);
				} else {
					hmProActualCostTime = CF.getProjectActualCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
					hmProBillCost = CF.getProjectBillableCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
				}
				
//				 ********************************* Money you can convert it in to % ***************************************
				
				dblBillableAmt = uF.parseToDouble(hmPOActBillAmt.get(rs.getString("project_owner")+"BILL_AMT"));
				 if("F".equalsIgnoreCase(rs.getString("billing_type"))) { 
					 dblBillableAmt += uF.parseToDouble(rs.getString("billing_amount"));
				 } else {
					 dblBillableAmt += uF.parseToDouble(hmProBillCost.get("proBillableCost"));
				 }
//				 System.out.println(rs.getString("pro_id") + " -- dblBillableAmt ===>> " + dblBillableAmt);
				 
				 dblActualAmt = uF.parseToDouble(hmPOActBillAmt.get(rs.getString("project_owner")+"ACT_AMT"));
				 dblActualAmt += uF.parseToDouble(hmProActualCostTime.get("proActualCost"));
				 hmPOActBillAmt.put(rs.getString("project_owner")+"ACT_AMT", uF.formatIntoTwoDecimalWithOutComma(dblActualAmt));
				 hmPOActBillAmt.put(rs.getString("project_owner")+"BILL_AMT", uF.formatIntoTwoDecimalWithOutComma(dblBillableAmt));
				 
				 
//				 ********************************* Time in actual format you can convert it in to % ***************************************
				 dblBugedtedTime = uF.parseToDouble(hmPOActIdealTime.get(rs.getString("project_owner")+"IDEAL_TIME"));
				 Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con, uF, rs.getString("pro_id"), hmProjectData);
				 dblBugedtedTime += uF.parseToDouble(hmProBudgetedCostAndTime.get("proBudgetedTime"));
				
				 dblActualTime = uF.parseToDouble(hmPOActIdealTime.get(rs.getString("project_owner")+"ACT_TIME"));
				 dblActualTime += uF.parseToDouble(hmProActualCostTime.get("proActualTime"));
				 hmPOActIdealTime.put(rs.getString("project_owner")+"ACT_TIME", uF.formatIntoTwoDecimalWithOutComma(dblActualTime));
				 hmPOActIdealTime.put(rs.getString("project_owner")+"IDEAL_TIME", uF.formatIntoTwoDecimalWithOutComma(dblBugedtedTime));
				 
				 
//				 ********************************* Time in HRS ***************************************
				 dblActualTimeHrs = uF.parseToDouble(hmPOActIdealTimeHRS.get(rs.getString("project_owner")+"ACT_TIME_HRS"));
				 String proActualTimeHRS = getProjectActualTimeHRS(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
				 dblActualTimeHrs += uF.parseToDouble(proActualTimeHRS);
				 
				 dblIdealTimeHrs = uF.parseToDouble(hmPOActIdealTimeHRS.get(rs.getString("project_owner")+"IDEAL_TIME_HRS"));
				 String proIdealTimeHRS = getProjectIdealTimeHRS(con, uF, rs.getString("pro_id"), hmProjectData);
				 dblIdealTimeHrs += uF.parseToDouble(proIdealTimeHRS);
				 
				 hmPOActIdealTimeHRS.put(rs.getString("project_owner")+"ACT_TIME_HRS", uF.formatIntoTwoDecimalWithOutComma(dblActualTimeHrs));
				 hmPOActIdealTimeHRS.put(rs.getString("project_owner")+"IDEAL_TIME_HRS", uF.formatIntoTwoDecimalWithOutComma(dblIdealTimeHrs));*/
				
				if(rs.getString("project_owners")==null || (rs.getString("project_owners")!=null && (rs.getString("project_owners").equals("") || rs.getString("project_owners").contains(",0,")))){
	        		continue;
	        	}
				
				if(rs.getString("project_owners")!=null){
					List<String> tempList = Arrays.asList(rs.getString("project_owners").split(","));
	        		for(int j=1; j<tempList.size();j++){
	        			if(!alProOwner.contains(tempList.get(j))) {
	    					alProOwner.add(tempList.get(j));
	    				}
	        			
	        			Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));

	    				Map<String, String> hmProActualCostTime = new HashMap<String, String>();
	    				Map<String, String> hmProBillCost = new HashMap<String, String>();
	    				if("M".equalsIgnoreCase(rs.getString("actual_calculation_type"))) { 
	    					hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData);
	    					hmProBillCost = CF.getMonthlyProjectBillableCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData);
	    				} else {
	    					hmProActualCostTime = CF.getProjectActualCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
	    					hmProBillCost = CF.getProjectBillableCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
	    				}
	    				
//	    				 ********************************* Money you can convert it in to % ***************************************
	    				
	    				dblBillableAmt = uF.parseToDouble(hmPOActBillAmt.get(tempList.get(j)+"BILL_AMT"));
	    				 if("F".equalsIgnoreCase(rs.getString("billing_type"))) { 
	    					 dblBillableAmt += uF.parseToDouble(rs.getString("billing_amount"));
	    				 } else {
	    					 dblBillableAmt += uF.parseToDouble(hmProBillCost.get("proBillableCost"));
	    				 }
//	    				 System.out.println(rs.getString("pro_id") + " -- dblBillableAmt ===>> " + dblBillableAmt);
	    				 
	    				 dblActualAmt = uF.parseToDouble(hmPOActBillAmt.get(tempList.get(j)+"ACT_AMT"));
	    				 dblActualAmt += uF.parseToDouble(hmProActualCostTime.get("proActualCost"));
	    				 hmPOActBillAmt.put(tempList.get(j)+"ACT_AMT", uF.formatIntoTwoDecimalWithOutComma(dblActualAmt));
	    				 hmPOActBillAmt.put(tempList.get(j)+"BILL_AMT", uF.formatIntoTwoDecimalWithOutComma(dblBillableAmt));
	    				 
	    				 
//	    				 ********************************* Time in actual format you can convert it in to % ***************************************
	    				 dblBugedtedTime = uF.parseToDouble(hmPOActIdealTime.get(tempList.get(j)+"IDEAL_TIME"));
	    				 Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con, uF, rs.getString("pro_id"), hmProjectData);
	    				 dblBugedtedTime += uF.parseToDouble(hmProBudgetedCostAndTime.get("proBudgetedTime"));
	    				
	    				 dblActualTime = uF.parseToDouble(hmPOActIdealTime.get(tempList.get(j)+"ACT_TIME"));
	    				 dblActualTime += uF.parseToDouble(hmProActualCostTime.get("proActualTime"));
	    				 hmPOActIdealTime.put(tempList.get(j)+"ACT_TIME", uF.formatIntoTwoDecimalWithOutComma(dblActualTime));
	    				 hmPOActIdealTime.put(tempList.get(j)+"IDEAL_TIME", uF.formatIntoTwoDecimalWithOutComma(dblBugedtedTime));
	    				 
	    				 
//	    				 ********************************* Time in HRS ***************************************
	    				 dblActualTimeHrs = uF.parseToDouble(hmPOActIdealTimeHRS.get(tempList.get(j)+"ACT_TIME_HRS"));
	    				 String proActualTimeHRS = getProjectActualTimeHRS(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
	    				 dblActualTimeHrs += uF.parseToDouble(proActualTimeHRS);
	    				 
	    				 dblIdealTimeHrs = uF.parseToDouble(hmPOActIdealTimeHRS.get(tempList.get(j)+"IDEAL_TIME_HRS"));
	    				 String proIdealTimeHRS = getProjectIdealTimeHRS(con, uF, rs.getString("pro_id"), hmProjectData);
	    				 dblIdealTimeHrs += uF.parseToDouble(proIdealTimeHRS);
	    				 
	    				 hmPOActIdealTimeHRS.put(tempList.get(j)+"ACT_TIME_HRS", uF.formatIntoTwoDecimalWithOutComma(dblActualTimeHrs));
	    				 hmPOActIdealTimeHRS.put(tempList.get(j)+"IDEAL_TIME_HRS", uF.formatIntoTwoDecimalWithOutComma(dblIdealTimeHrs));
	        		}
				}
					
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("alProOwner", alProOwner);
			request.setAttribute("hmPOActBillAmt", hmPOActBillAmt);
			request.setAttribute("hmPOActIdealTime", hmPOActIdealTime);
			request.setAttribute("hmPOActIdealTimeHRS", hmPOActIdealTimeHRS);
			
			request.setAttribute("hmKPIM", hmKPIM);
			request.setAttribute("hmKPIT", hmKPIT);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpDesigMap", hmEmpDesigMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public String getProjectIdealTimeHRS(Connection con, UtilityFunctions uF, String proId, Map<String, String> hmProjectData) {
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

				if(hmProjectData != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equals("H")) {
					proBudgetedTime += uF.parseToDouble(hmInner.get(taskId+"_IDEAL_TIME"));
				} else if(hmProjectData != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equals("D")) {
					proBudgetedTime += (uF.parseToDouble(hmInner.get(taskId+"_IDEAL_TIME"))* 8);
				} else if(hmProjectData != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equals("M")) {
					proBudgetedTime += ((uF.parseToDouble(hmInner.get(taskId+"_IDEAL_TIME"))* 8) * 30);
				}
//				System.out.println(taskId + "  taskResourceCnt ===>> " + taskResourceCnt + "  taskResourceCost ====>> " + taskResourceCost +" IDEAL_TIME =>>>>> " + hmInner.get(taskId+"_IDEAL_TIME"));
			}
			proIdealTimeHrs = proBudgetedTime+"";
//			System.out.println("proId ===>> " + proId + " -- proBudgetedTime ===>> " + proBudgetedTime);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return proIdealTimeHrs;
	}
	
	
	public String getProjectActualTimeHRS(Connection con, CommonFunctions CF, UtilityFunctions uF, String proId, Map<String, String> hmProjectData, boolean isSubmit, boolean isApprove) {
		PreparedStatement pst=null;
		ResultSet rs=null;
		String proActualTimeHrs = null;
		try {
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(a1.hrs) actual_hrs, sum(a1.days) actual_days, a1.emp_id from (select sum(ta.actual_hrs) hrs, " +
				"count(distinct ta.task_date) days, ta.emp_id, ta.activity_id from task_activity ta where ta.emp_id>0 "); //task_date between ? and ? 
			if(isSubmit && isApprove) {
				sbQuery.append(" and (is_approved = 1 or is_approved = 2)");
			} else if(isSubmit) {
				sbQuery.append(" and is_approved = 1 ");
			} else if(isApprove) {
				sbQuery.append(" and is_approved = 2 ");
			}
			sbQuery.append(" group by ta.activity_id, ta.emp_id) as a1, activity_info ai where ai.task_id = a1.activity_id and ai.pro_id = ? " +
				"group by a1.emp_id");
			
			pst = con.prepareStatement(sbQuery.toString()); // and (is_approved = 1 or is_approved = 2) 
//			pst.setDate(1, uF.getDateFormat(hmProjectData.get("PRO_START_DATE"), DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_END_DATE"), DATE_FORMAT));
			pst.setInt(1, uF.parseToInt(proId));
//			System.out.println("pst======> " + pst);
			rs=pst.executeQuery();
			Map<String, String> hmResourceActualTime = new HashMap<String,String>();
			while(rs.next()) {
				hmResourceActualTime.put(rs.getString("emp_id"), rs.getString("actual_hrs"));
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = hmResourceActualTime.keySet().iterator();
			double proActualTime = 0;
			while (it.hasNext()) {
				String empId = it.next();
				String actualTime = hmResourceActualTime.get(empId);
				proActualTime += uF.parseToDouble(actualTime);
//				System.out.println(proId +"  empId ===>> " + empId + "  actualTime ===>>> " + actualTime + " taskResourceActualCost ===>> " + taskResourceActualCost);
			}
			proActualTimeHrs = ""+proActualTime;
//		System.out.println(proId + "   hmProActualAndBillableCost ===>> " + hmProActualAndBillableCost);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return proActualTimeHrs;
	}

//	public void getEmployeeKPI(String strUserType, UtilityFunctions uF){
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Connection con = null;
//		Database db = new Database();
//		db.setRequest(request);
//		
//		try {
//		
//			con = db.makeConnection(con);
//			
//			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null); 
//			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
//			
//			
//			
//			double []PRESENT_ABSENT_DATA_MONTH  = new double[2];
//			String []PRESENT_ABSENT_LABEL_MONTH  = new String[2];
//			
//			
///*			if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || 
//					strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))){
//				pst = con.prepareStatement("select sum(actual_amount) as actual_amount, sum(billable_amount) as billable_amount, added_by from project_cost pc right join projectmntnc pmc on pc.pro_id = pmc.pro_id  group by added_by");
////				pst.setString(1, sbEmpIds.toString());
//			}else if(strUserType!=null && (strUserType.equalsIgnoreCase(MANAGER))){
//				pst = con.prepareStatement("select sum(hours_worked) as hours_worked, sum(actual_hours) as actual_hours, rd.emp_id from attendance_details ad, roster_details rd where ad.emp_id=rd.emp_id and ad.service_id = rd.service_id and rd._date = to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.in_out = 'OUT' and ad.emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) group by rd.emp_id");
//				pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
//			}
//*/			
//			
//			
//			double dblMin=0;
//			double dblMax=0;
//			double dblActualHourWorked=0;
//			double dblActualHourAssigned=0;
//			
//			
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select sum(actual_amount) as actual_amount, sum(billable_amount) as billable_amount, added_by from project_cost pc right join projectmntnc pmc on pc.pro_id = pmc.pro_id where pc.pro_id=pmc.pro_id ");
//			
//			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
//					sbQuery.append(" and pmc.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
//			}
//			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
//				sbQuery.append(" and pmc.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//			}
//			if(getF_start()!=null && getF_end()!=null) {
//				sbQuery.append(" and start_date between '"+uF.getDateFormat(getF_start(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getF_end(), DATE_FORMAT, DBDATE)+"' ");
//			}
//			if(strUserType!=null && (strUserType.equalsIgnoreCase(MANAGER))) {
//				sbQuery.append(" and added_by="+uF.parseToInt((String)session.getAttribute(EMPID)));
//			}
//			sbQuery.append(" group by added_by");
//			
//			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=====>"+pst);
//			rs = pst.executeQuery();
//			Map<String, AngularMeter> hmKPIM = new HashMap<String, AngularMeter>();
//			Map<String, AngularMeter> hmKPIT = new HashMap<String, AngularMeter>();
//			while(rs.next()){
//				PRESENT_ABSENT_DATA_MONTH  = new double[2];
//				PRESENT_ABSENT_DATA_MONTH [0] = rs.getDouble("actual_amount");
//				PRESENT_ABSENT_DATA_MONTH [1] = rs.getDouble("billable_amount");
//				
//				dblActualHourAssigned = uF.parseToDouble(rs.getString("billable_amount"));
//				dblActualHourWorked = uF.parseToDouble(rs.getString("actual_amount"));
//				
//				hmKPIM.put(rs.getString("added_by"), new SemiCircleMeter().getSemiCircleChart(PRESENT_ABSENT_DATA_MONTH, PRESENT_ABSENT_LABEL_MONTH, "KPI-Money"));
//			}
//			rs.close();
//			pst.close();
//			
//			
//			if(dblActualHourWorked>dblActualHourAssigned){
//				dblMin = 0;
//				dblMax = (dblActualHourAssigned+(dblActualHourWorked-dblActualHourAssigned));
//				
//				request.setAttribute("KPI_MIN", dblMin+"");
//				request.setAttribute("KPI_MAX", dblMax+"");
//			}else{
//				dblMin = 0;
//				dblMax = dblActualHourAssigned;
//				
//				request.setAttribute("KPI_MIN", dblMin+"");
//				request.setAttribute("KPI_MAX", dblMax+"");
//			}
//			request.setAttribute("KPI_W", dblActualHourWorked+"");
//			
//			double dbl1 = (int)(40 * dblMax / 100);
//			double dbl2 = (int)(80 * dblMax / 100);
//			
//			request.setAttribute("KPI_1", dbl1+"");
//			request.setAttribute("KPI_2", dbl2+"");
//			request.setAttribute("KPI_HEADING", "KPI-Money");
//			request.setAttribute("KPI_PREFIX", "Money");
//			request.setAttribute("KPI_SUFFIX", "INR");
//			
//			dblMin=0;
//			dblMax=0;
//			dblActualHourWorked=0;
//			dblActualHourAssigned=0;
//			
//			if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))){
//				pst = con.prepareStatement("select sum(actual_hrs) as actual_hrs, sum(pt.idealtime) as idealtime, added_by from project_time pt right join projectmntnc pmc on pt.pro_id = pmc.pro_id group by added_by");
////				pst.setString(1, sbEmpIds.toString());
//			} else if(strUserType!=null && (strUserType.equalsIgnoreCase(MANAGER))) {
////				pst = con.prepareStatement("select sum(hours_worked) as hours_worked, sum(actual_hours) as actual_hrs, rd.emp_id from attendance_details ad, roster_details rd where ad.emp_id=rd.emp_id and ad.service_id = rd.service_id and rd._date = to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.in_out = 'OUT' and ad.emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) group by rd.emp_id");
//				pst = con.prepareStatement("select sum(actual_hrs) as actual_hrs, sum(pt.idealtime) as idealtime, added_by from project_time pt right join projectmntnc pmc on pt.pro_id = pmc.pro_id group by added_by");
////				pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
//			} else {
//				pst = con.prepareStatement("select sum(actual_hrs) as actual_hrs, sum(pt.idealtime) as idealtime, added_by from project_time pt right join projectmntnc pmc on pt.pro_id = pmc.pro_id group by added_by");
//			}
//			System.out.println("pst=====>"+pst);
//			rs = pst.executeQuery();
//			while(rs.next()){
//				PRESENT_ABSENT_DATA_MONTH  = new double[2];
//				PRESENT_ABSENT_DATA_MONTH [0] = rs.getDouble("actual_hrs");
//				PRESENT_ABSENT_DATA_MONTH [1] = rs.getDouble("idealtime");
//				
//				dblActualHourAssigned = uF.parseToDouble(rs.getString("idealtime"));
//				dblActualHourWorked = uF.parseToDouble(rs.getString("actual_hrs"));
//				
//				hmKPIT.put(rs.getString("added_by"), new SemiCircleMeter().getSemiCircleChartReverse(PRESENT_ABSENT_DATA_MONTH, PRESENT_ABSENT_LABEL_MONTH, "KPI-Time"));
//				
//			}
//			rs.close();
//			pst.close();
//			
//			if(dblActualHourWorked>dblActualHourAssigned){
//				dblMin = 0;
//				dblMax = (dblActualHourAssigned+(dblActualHourWorked-dblActualHourAssigned));
//				request.setAttribute("KPI_P_MIN", dblMin+"");
//				request.setAttribute("KPI_P_MAX", dblMax+"");
//			}else{
//				dblMin = 0;
//				dblMax = dblActualHourAssigned;
//				request.setAttribute("KPI_P_MIN", dblMin+"");
//				request.setAttribute("KPI_P_MAX", dblMax+"");
//			}
//			request.setAttribute("KPI_P_W", dblActualHourWorked+"");
//			dbl1 = (int)(40 * dblMax / 100);
//			dbl2 = (int)(80 * dblMax / 100);
//			
//			request.setAttribute("KPI_P_1", dbl1+"");
//			request.setAttribute("KPI_P_2", dbl2+"");
//			request.setAttribute("KPI_P_HEADING", "KPI-Time");
//			request.setAttribute("KPI_P_PREFIX", "Project");
//			request.setAttribute("KPI_P_SUFFIX", "hours");
//			
//			request.setAttribute("hmKPIM", hmKPIM);
//			request.setAttribute("hmKPIT", hmKPIT);
//			request.setAttribute("hmEmpName", hmEmpName);
//			request.setAttribute("hmEmpDesigMap", hmEmpDesigMap);
//			
//			System.out.println("hmKPIM=====>"+hmKPIM);
//			System.out.println("hmKPIT=====>"+hmKPIT);
//			System.out.println("hmEmpName=====>"+hmEmpName);
//			System.out.println("hmEmpDesigMap=====>"+hmEmpDesigMap);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public String getF_start() {
		return f_start;
	}

	public void setF_start(String f_start) {
		this.f_start = f_start;
	}

	public String getF_end() {
		return f_end;
	}

	public void setF_end(String f_end) {
		this.f_end = f_end;
	}

	public String getSelectOne() {
		return selectOne;
	}

	public void setSelectOne(String selectOne) {
		this.selectOne = selectOne;
	}

	public String getBtnSubmit() {
		return btnSubmit;
	}

	public void setBtnSubmit(String btnSubmit) {
		this.btnSubmit = btnSubmit;
	}

}
