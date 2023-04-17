package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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

public class AllProjectsSummaryView  extends ActionSupport implements
		ServletRequestAware, ServletResponseAware, SessionAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	int pro_id;
	String strUsreType;
	String operation;
	 
	CommonFunctions CF;
	private HttpServletRequest request;
	UtilityFunctions uF = new UtilityFunctions();
	public String execute() {
		String result = "seperate";
			session = request.getSession();
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if (CF == null)
				return LOGIN;
			
			if (getOperation() != null && getOperation().equals("All")) {
				getActivityDetails();
				result = "combine";
			} else if (getOperation() != null && getOperation().equals("seperate")) {
				getActivityDetailsIndividual();
				result = "seperate";
			}

		return result;
	}
	public void getActivityDetailsIndividual() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			Map hmDesig = CF.getEmpDesigMap(con);
			Map hmEmpName = CF.getEmpNameMap(con, null, null);
			
			pst = con.prepareStatement("select *,ai.start_date as a_start_date,pmc.start_date as p_start_date," +
						" ai.completed as a_completed, ai.emp_id as a_emp_id, ai.deadline as a_deadline, pmc.deadline " +
						"as p_deadline from activity_info ai, projectmntnc pmc where pmc.pro_id=ai.pro_id order by task_id ");
						
			rs = pst.executeQuery();
			
			List alInner1 = new ArrayList();
			Map hmDetails=new HashMap(); 
			int count = 0;
			int val=0;
//			String strProjectIdNew=null;
//			String strProjectIdOld=null;
			
			while (rs.next()) {
				
				count++;
				List alGanntChart = new ArrayList();
				alInner1 = new ArrayList();
				
			
				java.util.Date dtEndDate = uF.getDateFormatUtil(rs.getString("end_date"), DBDATE);
				java.util.Date dtDeadlineDate = uF.getDateFormatUtil(rs.getString("deadline"), DBDATE);
				
				
				String color=rs.getString("color_code");
				String c_code="ff33ff";
				if(color!=null){
					c_code=color.replaceAll("#","");
				}
				
				
				if(count==1){
				
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
					alInner1 = new ArrayList();
					
					alInner1.add(rs.getString("task_id")); 		//pID
					alInner1.add(rs.getString("activity_name")); 		//pName
					alInner1.add(uF.getDateFormat(rs.getString("a_start_date"), DBDATE, DATE_FORMAT)); 		//pStart
					if(rs.getString("end_date")!=null){
						alInner1.add(uF.getDateFormat(rs.getString("end_date"), DBDATE, DATE_FORMAT)); 		//pEnd
					}else{
						alInner1.add(uF.getDateFormat(rs.getString("a_deadline"), DBDATE, DATE_FORMAT)); 		//pEnd
					}
					alInner1.add(uF.getDateFormat(rs.getString("a_deadline"), DBDATE, DATE_FORMAT)); 		//pDeadline
					
					if(dtEndDate!=null && dtDeadlineDate!=null && dtEndDate.after(dtDeadlineDate)){
						alInner1.add("ff3333"); 		//pColor
					}else{
						alInner1.add(c_code); 		//pColor
					}
					
					alInner1.add(""); 		//pLink
					alInner1.add("0"); 		//pMile
					alInner1.add("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+hmEmpName.get(rs.getString("a_emp_id"))+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"); 		//pRes
					alInner1.add(rs.getString("a_completed")); 		//pComp
					alInner1.add("0"); 		//pGroup
					alInner1.add(rs.getString("pro_id")); 		//pParent
					alInner1.add("0"); 		//pOpen
					alInner1.add(uF.parseToInt(rs.getString("dependency_task"))); 		//pDepend
					alInner1.add("Task Caption"); 		//pCaption
					
				}else{
					

					alInner1.add(rs.getString("task_id")); 		//pID
					alInner1.add(rs.getString("activity_name")); 		//pName
					alInner1.add(uF.getDateFormat(rs.getString("a_start_date"), DBDATE, DATE_FORMAT)); 		//pStart
					
					if(rs.getString("end_date")!=null){
						alInner1.add(uF.getDateFormat(rs.getString("end_date"), DBDATE, DATE_FORMAT)); 		//pEnd
					}else{
						alInner1.add(uF.getDateFormat(rs.getString("a_deadline"), DBDATE, DATE_FORMAT)); 		//pEnd
					}
					alInner1.add(uF.getDateFormat(rs.getString("a_deadline"), DBDATE, DATE_FORMAT)); 		//pDeadline
					
					if(dtEndDate!=null && dtDeadlineDate!=null && dtEndDate.after(dtDeadlineDate)){
						alInner1.add("ff3333"); 		//pColor
					}else{
						alInner1.add(c_code); 		//pColor
					}
					
					alInner1.add(""); 		//pLink
					alInner1.add("0"); 		//pMile
					alInner1.add("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+hmEmpName.get(rs.getString("a_emp_id"))+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"); 		//pRes
					alInner1.add(rs.getString("a_completed")); 		//pComp
					alInner1.add("0"); 		//pGroup
					alInner1.add(rs.getString("pro_id")); 		//pParent
					alInner1.add("0"); 		//pOpen
					alInner1.add(uF.parseToInt(rs.getString("dependency_task"))); 		//pDepend
					alInner1.add("Task Caption"); 		//pCaption
					
					
				}
				
				
				
				
				alGanntChart.add(alInner1);
				hmDetails.put(val,alGanntChart);
				val++;
//				strProjectIdOld = strProjectIdNew;
				count=0;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmDetails", hmDetails);
//			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getActivityDetails() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			//Map hmDesig = CF.getEmpDesigMap(con);
			Map hmEmpName = CF.getEmpNameMap(con, null, null);
			
			
			pst = con.prepareStatement("select *,ai.start_date as a_start_date,pmc.start_date as p_start_date," +
						" ai.completed as a_completed, ai.emp_id as a_emp_id, ai.deadline as a_deadline, pmc.deadline " +
						"as p_deadline from activity_info ai, projectmntnc pmc where pmc.pro_id=ai.pro_id  and pmc.approve_status = 'n' order by ai.pro_id ");
			
			rs = pst.executeQuery();
			List alInner1 = new ArrayList();
			Map hmDetails=new HashMap(); 
			int count = 0;
			int val=0;
			String strProjectIdNew=null;
			String strProjectIdOld=null;
			while (rs.next()) {
				strProjectIdNew = rs.getString("pro_id");
				if(strProjectIdNew!=null && !strProjectIdNew.equalsIgnoreCase(strProjectIdOld)){
					count = 0;
				}
				
				count++;
				List alGanntChart = new ArrayList();
				alInner1 = new ArrayList();
			
				java.util.Date dtEndDate = uF.getDateFormatUtil(rs.getString("end_date"), DBDATE);
				java.util.Date dtDeadlineDate = uF.getDateFormatUtil(rs.getString("deadline"), DBDATE);
				
				String color=rs.getString("color_code");
				String c_code="ff33ff";
				if(color!=null){
					c_code=color.replaceAll("#","");
				}
				
				if(count==1){

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
					alInner1 = new ArrayList();
					
					alInner1.add(rs.getString("task_id")); 		//pID
					alInner1.add(rs.getString("activity_name")); 		//pName
					alInner1.add(uF.getDateFormat(rs.getString("a_start_date"), DBDATE, DATE_FORMAT)); 		//pStart
					if(rs.getString("end_date")!=null){
						alInner1.add(uF.getDateFormat(rs.getString("end_date"), DBDATE, DATE_FORMAT)); 		//pEnd
					}else{
						alInner1.add(uF.getDateFormat(rs.getString("a_deadline"), DBDATE, DATE_FORMAT)); 		//pEnd
					}
					alInner1.add(uF.getDateFormat(rs.getString("a_deadline"), DBDATE, DATE_FORMAT)); 		//pDeadline
					
					if(dtEndDate!=null && dtDeadlineDate!=null && dtEndDate.after(dtDeadlineDate)){
						alInner1.add("ff3333"); 		//pColor
					}else{
						alInner1.add(c_code); 		//pColor
					}
					
					alInner1.add(""); 		//pLink
					alInner1.add("0"); 		//pMile
					alInner1.add("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+hmEmpName.get(rs.getString("a_emp_id"))+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"); 		//pRes
					alInner1.add(rs.getString("a_completed")); 		//pComp
					alInner1.add("0"); 		//pGroup
					alInner1.add(rs.getString("pro_id")); 		//pParent
					alInner1.add("0"); 		//pOpen
					alInner1.add(uF.parseToInt(rs.getString("dependency_task"))); 		//pDepend
					alInner1.add("Task Caption"); 		//pCaption
					
				}else{
					

					alInner1.add(rs.getString("task_id")); 		//pID
					alInner1.add(rs.getString("activity_name")); 		//pName
					alInner1.add(uF.getDateFormat(rs.getString("a_start_date"), DBDATE, DATE_FORMAT)); 		//pStart
					
					if(rs.getString("end_date")!=null){
						alInner1.add(uF.getDateFormat(rs.getString("end_date"), DBDATE, DATE_FORMAT)); 		//pEnd
					}else{
						alInner1.add(uF.getDateFormat(rs.getString("a_deadline"), DBDATE, DATE_FORMAT)); 		//pEnd
					}
					alInner1.add(uF.getDateFormat(rs.getString("a_deadline"), DBDATE, DATE_FORMAT)); 		//pDeadline
					
					if(dtEndDate!=null && dtDeadlineDate!=null && dtEndDate.after(dtDeadlineDate)){
						alInner1.add("ff3333"); 		//pColor
					}else{
						alInner1.add(c_code); 		//pColor
					}
					
					alInner1.add(""); 		//pLink
					alInner1.add("0"); 		//pMile
					alInner1.add("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+hmEmpName.get(rs.getString("a_emp_id"))+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"); 		//pRes
					alInner1.add(rs.getString("a_completed")); 		//pComp
					alInner1.add("0"); 		//pGroup
					alInner1.add(rs.getString("pro_id")); 		//pParent
					alInner1.add("1"); 		//pOpen
					alInner1.add(uF.parseToInt(rs.getString("dependency_task"))); 		//pDepend
					alInner1.add("Task Caption"); 		//pCaption
					
				}
				
				alGanntChart.add(alInner1);
				hmDetails.put(val,alGanntChart);
				val++;
				
				strProjectIdOld = strProjectIdNew;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmDetails", hmDetails);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	@Override
	public void setSession(Map arg0) {
		// TODO Auto-generated method stub
		
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request=request;
	}


}
