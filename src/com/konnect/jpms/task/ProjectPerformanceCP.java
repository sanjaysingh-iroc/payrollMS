package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectPerformanceCP extends ActionSupport implements ServletRequestAware, IStatements{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2667880088621857068L;
	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF;
	
	public String execute(){
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/task/ProjectPerformanceCP.jsp");
		request.setAttribute(TITLE, "Project Performance");
		UtilityFunctions uF=new UtilityFunctions();
		String strUserType = (String)session.getAttribute(BASEUSERTYPE);
		String strReqEmpId = (String)request.getParameter("empId");
		
		if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
			getProjectDetails(uF.parseToInt((String)session.getAttribute(EMPID)), uF, CF, 0);
		}else if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))){
			getProjectDetails(uF.parseToInt(strReqEmpId), uF, CF, 0);
		}
		
		return SUCCESS;
	}

	
	public void getProjectDetails(int nManagerId, UtilityFunctions uF, CommonFunctions CF, int nLimit) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", true, null, null, uF);
			
			if(nManagerId==0){
				pst = con.prepareStatement("select * from projectmntnc pmntc where approve_status = 'approved' order by pmntc.deadline desc "+((nLimit>0)?" limit "+nLimit:""));
			}else{
				pst = con.prepareStatement("select * from projectmntnc pmntc where approve_status = 'approved' and added_by=? order by pmntc.deadline desc "+((nLimit>0)?" limit "+nLimit:""));
				pst.setInt(1, nManagerId);
			}
//			System.out.println("pst======>"+pst); 
			rs=pst.executeQuery();
			List<List<String>> alOuter=new ArrayList<List<String>>();
			
			StringBuilder budgeted_cost 	= new StringBuilder();
			StringBuilder billable_amount 	= new StringBuilder();
			StringBuilder actual_amount 	= new StringBuilder();
			StringBuilder pro_name 			= new StringBuilder();
			 
			while(rs.next()) {
				String strBudgetedCost = getProjectBudgetedCost(con, uF, rs.getString("pro_id"), rs.getString("actual_calculation_type"));
				Map<String, String> hmProActualAndBillableCost = getProjectActualCost(con, uF, rs.getString("pro_id"), rs.getString("actual_calculation_type"));
				
				double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("pro_id")));
//				double dblBudgeted = uF.parseToDouble(rs.getString("budgeted_cost"));
				double dblBudgeted = uF.parseToDouble(strBudgetedCost);
//				double dblBillable=(uF.parseToDouble(rs.getString("billable_amount"))+uF.parseToDouble(rs.getString("variable_cost")));
				double dblBillable = 0;
				if(rs.getString("billing_type") != null && rs.getString("billing_type").equals("F")) {
					dblBillable = uF.parseToDouble(rs.getString("billing_amount"));
				} else {
					dblBillable = uF.parseToDouble(hmProActualAndBillableCost.get("proBillableCost"));
				}
				double dblActual = uF.parseToDouble(hmProActualAndBillableCost.get("proActualCost")) + dblReimbursement;
				double diff = 0;
						
				if(dblBillable>0){
					diff = ((dblBillable-dblActual)/dblBillable) * 100;
				}
				
				List<String> alInner=new ArrayList<String>();
				alInner.add(rs.getString("pro_id"));
				alInner.add(rs.getString("pro_name"));
				alInner.add(uF.formatIntoTwoDecimal(dblBudgeted));
				alInner.add(uF.formatIntoTwoDecimal(dblActual));
				alInner.add(uF.formatIntoTwoDecimal(dblBillable));
				
				alInner.add(uF.formatIntoTwoDecimal(diff)+"");
				
				
				if (dblActual > dblBudgeted && dblActual < dblBillable) {
					/*alInner.add("<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>");
					
				} else if(dblActual < dblBudgeted) {
					/*alInner.add("<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
					
				} else if(dblActual > dblBillable) {
					/*alInner.add("<img src=\"images1/icons/denied.png\" width=\"17px\">");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
					
				} else {
					alInner.add("&nbsp;");
				}

				alOuter.add(alInner);
				
				pro_name.append("'"+rs.getString("pro_name")+"',");
				billable_amount.append(dblBillable+",");
				budgeted_cost.append(dblBudgeted+",");
				actual_amount.append(dblActual+",");
			}
			rs.close();
			pst.close();
			
			if(pro_name.length()>1){
				pro_name.replace(0, pro_name.length(), pro_name.substring(0, pro_name.length()-1));
				billable_amount.replace(0, billable_amount.length(), billable_amount.substring(0, billable_amount.length()-1));
				budgeted_cost.replace(0, budgeted_cost.length(), budgeted_cost.substring(0, budgeted_cost.length()-1));
				actual_amount.replace(0, actual_amount.length(), actual_amount.substring(0, actual_amount.length()-1));
			}
			
			request.setAttribute("alOuter",alOuter);
			request.setAttribute("pro_name",pro_name);
			request.setAttribute("billable_amount",billable_amount);
			request.setAttribute("budgeted_cost",budgeted_cost);
			request.setAttribute("actual_amount",actual_amount);

			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private Map<String, String> getProjectActualCost(Connection con, UtilityFunctions uF, String proId, String billType) {
		PreparedStatement pst=null;
		ResultSet rs=null;
		Map<String, String> hmProActualAndBillableCost = new HashMap<String, String>();
		try {
			
			pst = con.prepareStatement("select * from project_emp_details where pro_id=?");
			pst.setInt(1, uF.parseToInt(proId));
			rs=pst.executeQuery();
			Map<String, String> hmProEmpRate = new HashMap<String, String>();
			while(rs.next()) {
				hmProEmpRate.put(rs.getString("emp_id")+"_PER_HOUR", rs.getString("emp_actual_rate_per_hour"));
				hmProEmpRate.put(rs.getString("emp_id")+"_PER_DAY", rs.getString("emp_actual_rate_per_day"));
				hmProEmpRate.put(rs.getString("emp_id")+"_RATE_PER_HOUR", rs.getString("emp_rate_per_hour"));
				hmProEmpRate.put(rs.getString("emp_id")+"_RATE_PER_DAY", rs.getString("emp_rate_per_day"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select sum(a1.hrs) actual_hrs, sum(distinct a1.days) actual_days, a1.emp_id from (" +
				"select sum(ta.actual_hrs) hrs, count(distinct ta.task_date) days, ta.emp_id, ta.activity_id from task_activity ta group by " +
				"ta.activity_id, ta.emp_id) as a1, activity_info ai where ai.task_id = a1.activity_id and ai.pro_id = ? group by a1.emp_id ");
			pst.setInt(1, uF.parseToInt(proId));
//			System.out.println("pst======>"+pst); 
			rs=pst.executeQuery();
			Map<String, String> hmResourceActualTime = new HashMap<String,String>();
			while(rs.next()) {
				if(billType != null && billType.equals("H")) {
					hmResourceActualTime.put(rs.getString("emp_id"), rs.getString("actual_hrs"));
				} else {
					hmResourceActualTime.put(rs.getString("emp_id"), rs.getString("actual_days"));
				}
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = hmResourceActualTime.keySet().iterator();
			double proActualCost = 0;
			double proBillableCost = 0;
			while (it.hasNext()) {
				String empId = it.next();
				String actualTime = hmResourceActualTime.get(empId);
				
				double taskResourceActualCost = 0;
				double taskResourceBillableCost = 0;
				if(billType != null && billType.equals("H")) {
					taskResourceActualCost = uF.parseToDouble(actualTime) * uF.parseToDouble(hmProEmpRate.get(empId+"_PER_HOUR"));
					taskResourceBillableCost = uF.parseToDouble(actualTime) * uF.parseToDouble(hmProEmpRate.get(empId+"_RATE_PER_HOUR"));
				} else {
					taskResourceActualCost = uF.parseToDouble(actualTime) * uF.parseToDouble(hmProEmpRate.get(empId+"_PER_DAY"));
					taskResourceBillableCost = uF.parseToDouble(actualTime) * uF.parseToDouble(hmProEmpRate.get(empId+"_RATE_PER_DAY"));
				}
				
				proActualCost += taskResourceActualCost;
				proBillableCost += taskResourceBillableCost;
				
//				System.out.println("taskResourceActualCost ===>> " + taskResourceActualCost);
			}
			
			hmProActualAndBillableCost.put("proActualCost", ""+proActualCost);
			hmProActualAndBillableCost.put("proBillableCost", ""+proBillableCost);
			
//		System.out.println("hmProActualAndBillableCost ===>> " + hmProActualAndBillableCost);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return hmProActualAndBillableCost;
	}


	private String getProjectBudgetedCost(Connection con, UtilityFunctions uF, String proId, String billType) {
		PreparedStatement pst=null;
		ResultSet rs=null;
		String strBudgetedCost = "";
		
		try {
			
			pst = con.prepareStatement("select * from project_emp_details where pro_id=?");
			pst.setInt(1, uF.parseToInt(proId));
//			System.out.println("pst======>"+pst); 
			rs=pst.executeQuery();
			Map<String, String> hmProEmpRate = new HashMap<String, String>();
			while(rs.next()) {
				hmProEmpRate.put(rs.getString("emp_id")+"_PER_HOUR", rs.getString("emp_actual_rate_per_hour"));
				hmProEmpRate.put(rs.getString("emp_id")+"_PER_DAY", rs.getString("emp_actual_rate_per_day"));
			}
			rs.close();
			pst.close();
			
			double proVariableCost = 0;
			pst = con.prepareStatement("select sum(variable_cost) as variable_cost from variable_cost where pro_id=?");
			pst.setInt(1, uF.parseToInt(proId));
			rs=pst.executeQuery();
			while(rs.next()) {
				proVariableCost = rs.getDouble("variable_cost");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select task_id, activity_name, resource_ids, idealtime, parent_task_id from activity_info where " +
				"task_id not in (select parent_task_id from activity_info where parent_task_id is not null) and (parent_task_id in (" +
				"select task_id from activity_info) or parent_task_id = 0) and pro_id = ? ");
			pst.setInt(1, uF.parseToInt(proId));
//			System.out.println("pst======>"+pst); 
			rs=pst.executeQuery();
			Map<String, Map<String, String>> hmTaskData = new HashMap<String, Map<String,String>>();
			while(rs.next()) {
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put(rs.getString("task_id")+"_IDEAL_TIME", rs.getString("idealtime"));
				hmInner.put(rs.getString("task_id")+"_RESOURCES", rs.getString("resource_ids"));
				hmTaskData.put(rs.getString("task_id"), hmInner);
			}
//			System.out.println("hmTaskData ===>> " + hmTaskData);
			
			rs.close();
			pst.close();
			
			Iterator<String> it = hmTaskData.keySet().iterator();
			double proBudgetedCost = 0;
//			System.out.println("billType ===>> " + billType);
			while (it.hasNext()) {
				String taskId = it.next();
				Map<String, String> hmInner = hmTaskData.get(taskId);
				List<String> alResources = Arrays.asList(hmInner.get(taskId+"_RESOURCES").split(","));
//				System.out.println(taskId + "  -- alResources ===>> " + alResources);
//				System.out.println(taskId + "  -- hmProEmpRate ===>> " + hmProEmpRate);
				int taskResourceCnt = 0;
				double taskResourceCost = 0;
				for(int i=0; alResources!=null && !alResources.isEmpty() && i<alResources.size(); i++) {
					if(alResources.get(i) != null && !alResources.get(i).equals("")) {
//						System.out.println(taskId + "  -- alResources.get(i) ===>> " + alResources.get(i) + "  billType ===>> " + billType);
						if(billType != null && billType.equals("H")) {
							taskResourceCost += uF.parseToDouble(hmProEmpRate.get(alResources.get(i)+"_PER_HOUR"));
						} else {
							taskResourceCost += uF.parseToDouble(hmProEmpRate.get(alResources.get(i)+"_PER_DAY"));
						}
						taskResourceCnt++;
					}
				}
//				System.out.println(taskId + "  taskResourceCnt ===>> " + taskResourceCnt + "  taskResourceCost ====>> " + taskResourceCost +" IDEAL_TIME =>>>>> " + hmInner.get(taskId+"_IDEAL_TIME"));
				double taskAvgResourceCost = 0;
				if(taskResourceCnt > 0) {
					taskAvgResourceCost = taskResourceCost / taskResourceCnt;
				}
				double taskBudgetedCost = taskAvgResourceCost * uF.parseToDouble(hmInner.get(taskId+"_IDEAL_TIME"));
//				System.out.println(taskId + "  taskBudgetedCost ===>> " + taskBudgetedCost);
				proBudgetedCost += taskBudgetedCost;
			}
			proBudgetedCost += proVariableCost;
			
//		System.out.println("proBudgetedCost ===>> " + proBudgetedCost);
		strBudgetedCost = proBudgetedCost+"";
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return strBudgetedCost;
	}


	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

}
