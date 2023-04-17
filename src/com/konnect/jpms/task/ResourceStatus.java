package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ResourceStatus extends ActionSupport implements ServletRequestAware, IStatements {
	
	public HttpSession session;
	public CommonFunctions CF = null;
	UtilityFunctions uF = new UtilityFunctions();
	
	public String execute() throws Exception { 
		session = request.getSession();
		request.setAttribute(PAGE, "/jsp/task/resourceStatus.jsp");
		request.setAttribute(TITLE, "Resources Status");
		 CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		getEmployeeDetails();
		return LOAD;
	}
	public void getEmployeeDetails(){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
	//	List<List<String>> alEmpDetails = new ArrayList<List<String>>();
		try{
			 con = db.makeConnection(con);
			 Map<String, String> hmEmpNames = CF.getEmpNameMap(con,null, null);
		
			 List<String> alProductList = new ArrayList<String>();
			 pst = con.prepareStatement("select distinct pro_id from projectmntnc order by pro_id");
			 rs = pst.executeQuery();
			 while(rs.next()){
				 alProductList.add(rs.getString("pro_id"));
			 }
			 rs.close();
			 pst.close();
			 
			 List<String> liveProEmpIds = new ArrayList<String>();
			
			for(int i = 0;i<alProductList.size();i++){
				Map<String, String> hmProDetails = CF.getProjectDetailsByProId(con, alProductList.get(i));
				pst = con.prepareStatement("select resource_ids from activity_info ai, projectmntnc p where ai.pro_id = p.pro_id and p.approve_status = 'n' " +
				"and (ai.completed < 100 or ai.completed is null) and ((ai.start_date >= ? and ai.deadline <= ?) or (ai.start_date <= ? and ai.deadline >= ?) or (ai.start_date >= ? and ai.start_date <= ?))");
				pst.setDate(1, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(hmProDetails.get("PRO_END_DATE"), DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(hmProDetails.get("PRO_END_DATE"), DATE_FORMAT));
				rs = pst.executeQuery();
				while(rs.next()) {
					if(rs.getString("resource_ids") != null && !rs.getString("resource_ids").equals("")) {
						List<String> empIdList = Arrays.asList(rs.getString("resource_ids").split(","));
							for(String empId : empIdList) {
								if(empId!=null && !empId.equals("")) {
									if(!liveProEmpIds.contains(empId)) {
										liveProEmpIds.add(empId);
									}
								}
							}  
					}
				}
				rs.close();
				pst.close();
			}
			 Map<String,List<String>> hmProEmpIds = new  HashMap<String,List<String>>();
			 
			for(int i=0; liveProEmpIds != null && i< liveProEmpIds.size(); i++) {
				pst = con.prepareStatement("select * from project_emp_details where emp_id = ?");
				pst.setInt(1, uF.parseToInt(liveProEmpIds.get(i)));
				rs = pst.executeQuery();
				List<String> alEmpPro =  new ArrayList<String>();
				  while(rs.next()) {
					  alEmpPro.add(rs.getString("pro_id"));
				  }
			 	hmProEmpIds.put(liveProEmpIds.get(i), alEmpPro);
			 	rs.close();
			 	pst.close();
			}	
//			 System.out.println("hmProEmpIds-->"+hmProEmpIds);
			
			 Date currentDate = uF.getCurrentDate(CF.getStrTimeZone());
			 List<String> alCurrentWorkingEmp = new ArrayList<String>();
			 pst = con.prepareStatement("select resource_ids from activity_info where start_date < ? and deadline > ? and task_id is not  null");
			 pst.setDate(1, currentDate);
			 pst.setDate(2, currentDate);
			 rs = pst.executeQuery();
				while(rs.next())	{
				 String resources = rs.getString("resource_ids");
				 String[] resourceIds = resources.split(",");
					 for(int j = 0; j<resourceIds.length; j++){
						if(resourceIds[j] != null && resourceIds[j].length() > 0){
								alCurrentWorkingEmp.add(resourceIds[j]);
						}
					 }
				}
			rs.close();
			pst.close();
						
//			System.out.println("alCurrentWorkingEmp"+alCurrentWorkingEmp);
			
			Map<String, String> hmTaskAllocation  = new HashMap<String, String>();
			Map<String, List<String>> hmEmpTransition   = new HashMap<String,List<String>>();
			Map<String, String> hmEmpTransition1   = new HashMap<String,String>();
			
			
			for(int a=0; liveProEmpIds != null && a< liveProEmpIds.size(); a++) {
				if(alCurrentWorkingEmp.contains(liveProEmpIds.get(a))){
					
				List<String> EmpProdId = hmProEmpIds.get(liveProEmpIds.get(a));
				for(int j = 0 ;EmpProdId !=null &&  j < EmpProdId.size(); j++){
				
				Map<String, String> hmProDetails = CF.getProjectDetailsByProId(con,EmpProdId.get(j));
				
				/*pst = con.prepareStatement("select count(a.*) as task_no from (select task_id,activity_name,parent_task_id,pro_id,start_date,deadline," +
					"completed from activity_info where resource_ids like '%,"+liveProEmpIds.get(a)+",%' and task_id not in (select parent_task_id " +
					" from activity_info where resource_ids like '%,"+liveProEmpIds.get(a)+",%' and parent_task_id is not null)) a, projectmntnc pmc " +
					" where pmc.pro_id=a.pro_id and pmc.approve_status='n' and (parent_task_id in (select task_id from activity_info where resource_ids " +
					" like '%,"+liveProEmpIds.get(a)+",%') or parent_task_id = 0) and (a.completed < 100 or a.completed is null) and ((a.start_date >= ? and a.deadline <= ?) or " +
					" (a.start_date <= ? and a.deadline >= ?) or (a.start_date >= ? and a.start_date <= ?))");
					pst.setDate(1, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(hmProDetails.get("PRO_END_DATE"), DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
					pst.setDate(6, uF.getDateFormat(hmProDetails.get("PRO_END_DATE"), DATE_FORMAT));
				 * 
				 * 
				 */
				pst = con.prepareStatement("select count(a.*) as task_no from (select task_id,activity_name,parent_task_id,pro_id,start_date,deadline," +
						"completed from activity_info where resource_ids like '%,"+liveProEmpIds.get(a)+",%' and task_id not in (select parent_task_id " +
						" from activity_info where resource_ids like '%,"+liveProEmpIds.get(a)+",%' and parent_task_id is not null)) a, projectmntnc pmc " +
						" where pmc.pro_id=a.pro_id and pmc.approve_status='n' and (parent_task_id in (select task_id from activity_info where resource_ids " +
						" like '%,"+liveProEmpIds.get(a)+",%') or parent_task_id = 0)");
				
				rs = pst.executeQuery();
//				System.out.println("pst ===>> " + pst);
				
				while(rs.next()) {
					if(uF.parseToInt(rs.getString("task_no")) > 5) {
						hmTaskAllocation.put(liveProEmpIds.get(a), "<div style=\"width: 100%; height: 100%; text-align: center; background-color: red;\"><a href=\"javascript:void(0);\" onclick=\"viewWorkAllocation('"+liveProEmpIds.get(a)+"','"+hmProDetails.get("PRO_START_DATE")+"','"+hmProDetails.get("PRO_END_DATE")+"')\">"+uF.parseToInt(rs.getString("task_no"))+"</a></div>");
					} else if(uF.parseToInt(rs.getString("task_no")) >= 2) {
						hmTaskAllocation.put(liveProEmpIds.get(a), "<div style=\"width: 100%; height: 100%; text-align: center; background-color: yellow;\"><a href=\"javascript:void(0);\" onclick=\"viewWorkAllocation('"+liveProEmpIds.get(a)+"','"+hmProDetails.get("PRO_START_DATE")+"','"+hmProDetails.get("PRO_END_DATE")+"')\">"+uF.parseToInt(rs.getString("task_no"))+"</a></div>");
					} else {
						hmTaskAllocation.put(liveProEmpIds.get(a), "<div style=\"width: 100%; height: 100%; text-align: center; background-color: lightgreen;\"><a href=\"javascript:void(0);\" onclick=\"viewWorkAllocation('"+liveProEmpIds.get(a)+"','"+hmProDetails.get("PRO_START_DATE")+"','"+hmProDetails.get("PRO_END_DATE")+"')\">"+uF.parseToInt(rs.getString("task_no"))+"</a></div>");
						
					}
					
				}
				rs.close();
				pst.close();
			}
			}else{
						hmTaskAllocation.put(liveProEmpIds.get(a), " ");
				}
		}
		Map<String,String> hmonBechEmp = new HashMap<String,String>();
			for(int i= 0 ;liveProEmpIds!=null && i < liveProEmpIds.size() ;i++){
			  String empId = liveProEmpIds.get(i);
				if(alCurrentWorkingEmp.contains(empId)){
					hmonBechEmp.put(liveProEmpIds.get(i), "NO");
				}else{
						hmonBechEmp.put(liveProEmpIds.get(i), "YES");
				}
			}
			for(int i=0;i<liveProEmpIds.size();i++){
				if(alCurrentWorkingEmp.contains(liveProEmpIds.get(i))){
					pst = con.prepareStatement("select * from activity_info where resource_ids like '%,"+liveProEmpIds.get(i)+",%'  order by deadline desc ");
					rs = pst.executeQuery();
					List<String> transitionDate = new ArrayList<String>();
					 while(rs.next()){
						transitionDate.add(rs.getString("deadline"));
					 }
					rs.close();
					pst.close();
				    hmEmpTransition1.put(liveProEmpIds.get(i), transitionDate.get(0));
			  }else{
						hmEmpTransition1.put(liveProEmpIds.get(i)," ");
					}
					
		 }
	//	System.out.println("hmEmpTransition1=======>"+hmEmpTransition1);
	//	System.out.println("hmTaskAllocation::::"+hmTaskAllocation);
	//	System.out.println("hmEmpTransition::::"+hmEmpTransition);
	//	System.out.println("hmEmpTransition1::::"+hmEmpTransition1);
	//	System.out.println("hmonBechEmp::::"+hmonBechEmp);
	//	System.out.println("LiveEmpIdds::::"+liveProEmpIds);
		request.setAttribute("liveProEmpIds",liveProEmpIds);
		request.setAttribute("hmEmpNames", hmEmpNames);
		request.setAttribute("hmTaskAllocation", hmTaskAllocation);
		request.setAttribute("hmEmpTransition", hmEmpTransition1);
		request.setAttribute("hmonBechEmp", hmonBechEmp);
		
		}catch (Exception e) {
			 e.printStackTrace();
		 } finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
				
	}
	public HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	
}
