package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeLeaveEntryReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	public CommonFunctions CF = null;
	
	String strUserType = null;  
	String strSesionEmpId = null;
	
	private String alertStatus;
	private String alert_type;
	private String dataType;
	
	private String strEmpId; 
	
	private String type;
	
	private String alertID;
	
	public String execute() throws Exception { 
		session=request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSesionEmpId = (String)session.getAttribute(EMPID);
		
		
		request.setAttribute(PAGE, PEmployeeLeaveEntryReport);
		if(strUserType!=null && strUserType.equals(EMPLOYEE)){
			request.setAttribute(TITLE, "My Leaves"); 
		} else {
			request.setAttribute(TITLE, "Leaves");
		}
		
//		System.out.println("getDataType() ===>> " + getDataType());
		if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")) {
			setDataType("L");    
		}
		
		if(getStrEmpId()==null || getStrEmpId().trim().equals("") || getStrEmpId().trim().equalsIgnoreCase("NULL")){
			setStrEmpId((String)session.getAttribute(EMPID));
		}
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		if(strUserType!=null && strUserType.equals(EMPLOYEE)){
			viewEmployeeLeaveEntry1();
			viewEmployeeLeaveList();
		}		
		
		return loadEmployeeLeaveEntry();

	}
	
	public void viewEmployeeLeaveEntry1() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uF = new UtilityFunctions();
//		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
		int nEmpId = uF.parseToInt(getStrEmpId());
		
		try {
			con = db.makeConnection(con);
			
			int nCurrentYear = uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"));
			
			String[] strFinacialYear = CF.getFinancialYear(con, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT), CF, uF);
			
			/*String strFDate1 = uF.getDateFormat(strFinacialYear[1]+"", DATE_FORMAT, "dd/MM");
			String strFYear1 = uF.getDateFormat(strFinacialYear[1]+"", DATE_FORMAT, "yyyy");
			int strFYear2 = uF.parseToInt(uF.getDateFormat(strFinacialYear[1]+"", DATE_FORMAT, "yyyy"))+1;
			System.out.println("ELER/109--strFDate1="+strFDate1+"strFYear1="+strFYear1);
			System.out.println("ELER/110--date1="+(strFDate1+"/"+(uF.parseToInt(strFYear1)+1)));
			System.out.println("ELER/111--date="+uF.getDateFormat((strFDate1+"/"+(uF.parseToInt(strFYear1)+1)), DATE_FORMAT));
			System.out.println("ELER/112--strFinacialYear="+strFinacialYear[1]);
			System.out.println("ELER/109--strFYear2="+strFYear2);*/
			
			String strWlocationid = CF.getEmpWlocationId(con, uF, ""+nEmpId);
			String strOrgid=getOrgId(""+nEmpId,uF);
			
			String strEmpName = CF.getEmpNameMapByEmpId(con, ""+nEmpId);
			
			Map<String,String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from probation_policy where emp_id = ?");
		    pst.setInt(1, nEmpId);
		    rs = pst.executeQuery();
		    String strEmpLeaveTypeId = null;
		    while (rs.next()) { 
		    	strEmpLeaveTypeId = rs.getString("leaves_types_allowed");
		    }
		    int fyCnt=0;
			int cyCnt=0;
			
		    if(strEmpLeaveTypeId!=null && !strEmpLeaveTypeId.trim().equals("") && !strEmpLeaveTypeId.trim().equalsIgnoreCase("NULL")) {
				Map<String, String> hmLeaveDetails = new HashMap<String,String>();
				Map<String, String> hmLeaveEffectiveDateType = new HashMap<String,String>();
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from emp_leave_type elt,leave_type lt where lt.leave_type_id = elt.leave_type_id " +
						" and level_id = (select dd.level_id from level_details ld, designation_details dd, grades_details gd " +
						" where ld.level_id = dd.level_id and gd.designation_id = dd.designation_id " +
						"and gd.grade_id = (select grade_id from employee_official_details where emp_id = ?)) and wlocation_id=? and elt.org_id=?" +
						" and lt.is_compensatory=false and is_constant_balance=false ");
				if(strEmpLeaveTypeId!=null && !strEmpLeaveTypeId.trim().equals("") && !strEmpLeaveTypeId.trim().equalsIgnoreCase("NULL")){
					sbQuery.append("and lt.leave_type_id in ("+strEmpLeaveTypeId+")");
				}
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, nEmpId);
				pst.setInt(2, uF.parseToInt(strWlocationid));
				pst.setInt(3, uF.parseToInt(strOrgid));
//				System.out.println("ELER/135--1 pst=====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					hmLeaveDetails.put(rs.getString("leave_type_id"), rs.getString("leave_type_name"));
					hmLeaveEffectiveDateType.put(rs.getString("leave_type_id"), rs.getString("effective_date_type"));
				}
				rs.close();
				pst.close(); 
				
//				pst = con.prepareStatement("select leave_type_id, balance from leave_register1 where register_id in(select max(register_id) from leave_register1 " +
//						"where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type) group by emp_id,leave_type_id)");
				pst = con.prepareStatement("select leave_type_id, balance from leave_register1 where register_id in (select max(register_id) from leave_register1 " +
					"where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type) group by emp_id,leave_type_id)");
	            pst.setInt(1, nEmpId);
//	            System.out.println("ELER/149--2 pst=====>"+pst);
	            rs = pst.executeQuery();
	            Map<String, String> hmMainBalance=new HashMap<String, String>();
	            while (rs.next()) {
	                hmMainBalance.put(rs.getString("leave_type_id"), rs.getString("balance"));
	            }
//	            System.out.println("ELER/167--hmMainBalance="+hmMainBalance);
	            rs.close();
	            pst.close();
	            
	            
	            pst = con.prepareStatement("select sum(accrued) as accrued,a.leave_type_id from (select max(_date) as daa,leave_type_id " +
	            		"from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type) " +
	            		"group by emp_id,leave_type_id )as a,leave_register1 lr where emp_id=? and _type!='C' and a.leave_type_id=lr.leave_type_id " +
	            		"and a.daa<=lr._date group by a.leave_type_id");
	            pst.setInt(1, nEmpId);
	            pst.setInt(2, nEmpId);
//	            System.out.println("ELER/164--3 pst=====>"+pst);
	            rs = pst.executeQuery();
	            Map<String, String> hmAccruedBalance=new HashMap<String, String>();
	            while (rs.next()) {
	            	hmAccruedBalance.put(rs.getString("leave_type_id"), rs.getString("accrued"));                
	            }
				rs.close();
				pst.close();
				
//				System.out.println("ELER/186--hmAccruedBalance="+hmAccruedBalance);
				
//	            pst = con.prepareStatement("select sum(leave_no) as count,leave_type_id from(select a.daa,lar.* from (select max(_date) as daa,leave_type_id " +
//	            		"from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type) " +
//	            		"group by emp_id,leave_type_id) as a,leave_application_register lar where emp_id=? and is_paid=true and (is_modify is null or is_modify=false) " +
//	            		"and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date) as a group by leave_type_id");
				
		//===start parvez date: 31-10-2022===		
				/*pst = con.prepareStatement("select sum(leave_no) as count,leave_type_id from(select a.daa,lar.* from (select max(_date) as daa,leave_type_id " +
		            		"from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type) " +
		            		"and register_id in (select max(register_id) from leave_register1 where emp_id=? and _type='C' " +
		            		"and leave_type_id in (select leave_type_id from leave_type) group by emp_id,leave_type_id) " +
		            		"group by emp_id,leave_type_id) as a,leave_application_register lar where emp_id=? and is_paid=true and (is_modify is null or is_modify=false) " +
		            		"and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date) as a group by leave_type_id");
	            pst.setInt(1, nEmpId);
	            pst.setInt(2, nEmpId);
	            pst.setInt(3, nEmpId);
	            System.out.println("ELER/201--4 pst=====>"+pst);
	            rs = pst.executeQuery();
	            Map<String, String> hmPaidBalance=new HashMap<String, String>();
	            while (rs.next()) {
	            	hmPaidBalance.put(rs.getString("leave_type_id"), rs.getString("count"));
	            }
	            System.out.println("ELER/207---hmPaidBalance="+hmPaidBalance);
				rs.close();
				pst.close();*/
				
				Map<String, String> hmPaidBalance=new HashMap<String, String>();
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_COLOR_FOR_NIGATIVE_LEAVE_BALANCE))){
					/*pst = con.prepareStatement("select sum(leave_no) as count,leave_type_id from(select a.daa,lar.* from (select max(_date) as daa,leave_type_id " +
			            		"from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type) " +
			            		"and register_id in (select max(register_id) from leave_register1 where emp_id=? and _type='C' " +
			            		"and leave_type_id in (select leave_type_id from leave_type) group by emp_id,leave_type_id) " +
			            		"group by emp_id,leave_type_id) as a,leave_application_register lar where emp_id=? and (is_modify is null or is_modify=false) " +
			            		"and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date) as a group by leave_type_id");
		            pst.setInt(1, nEmpId);
		            pst.setInt(2, nEmpId);
		            pst.setInt(3, nEmpId);
		            System.out.println("ELER/201--4 pst=====>"+pst);
		            rs = pst.executeQuery();
		            while (rs.next()) {
		            	hmPaidBalance.put(rs.getString("leave_type_id"), rs.getString("count"));
		            }
					rs.close();
					pst.close();*/
					
					pst = con.prepareStatement("select *,leave_type_id from(select a.daa,lar.* from (select max(_date) as daa,leave_type_id " +
			            		"from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type) " +
			            		"and register_id in (select max(register_id) from leave_register1 where emp_id=? and _type='C' " +
			            		"and leave_type_id in (select leave_type_id from leave_type) group by emp_id,leave_type_id) " +
			            		"group by emp_id,leave_type_id) as a,leave_application_register lar where emp_id=? and (is_modify is null or is_modify=false) " +
			            		"and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date) as a ");
		            pst.setInt(1, nEmpId);
		            pst.setInt(2, nEmpId);
		            pst.setInt(3, nEmpId);
//		            System.out.println("ELER/244--4 pst=====>"+pst);
		            rs = pst.executeQuery();
		            while (rs.next()) {
		            	double leaveBalance = uF.parseToDouble(hmPaidBalance.get(rs.getString("leave_type_id")))+uF.parseToDouble(rs.getString("leave_no"));
		            	hmPaidBalance.put(rs.getString("leave_type_id"), leaveBalance+"");
		            	boolean nFlag = false;
		            	if(uF.parseToDouble(rs.getString("leave_no"))<0){
		            		nFlag = true;
		            	}
		            	hmPaidBalance.put(rs.getString("leave_type_id")+"_NIGATIVE_BALANCE", nFlag+"");
		            }
					rs.close();
					pst.close();
				} else{
					pst = con.prepareStatement("select sum(leave_no) as count,leave_type_id from(select a.daa,lar.* from (select max(_date) as daa,leave_type_id " +
			            		"from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type) " +
			            		"and register_id in (select max(register_id) from leave_register1 where emp_id=? and _type='C' " +
			            		"and leave_type_id in (select leave_type_id from leave_type) group by emp_id,leave_type_id) " +
			            		"group by emp_id,leave_type_id) as a,leave_application_register lar where emp_id=? and is_paid=true and (is_modify is null or is_modify=false) " +
			            		"and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date) as a group by leave_type_id");
		            pst.setInt(1, nEmpId);
		            pst.setInt(2, nEmpId);
		            pst.setInt(3, nEmpId);
//		            System.out.println("ELER/201--4 pst=====>"+pst);
		            rs = pst.executeQuery();
		            while (rs.next()) {
		            	hmPaidBalance.put(rs.getString("leave_type_id"), rs.getString("count"));
		            }
//		            System.out.println("ELER/207---hmPaidBalance="+hmPaidBalance);
					rs.close();
					pst.close();
				}
				
				Map<String,String> hmApproveLeaveStatus=new HashMap<String, String>();
				Map<String,String> hmLeaveStatus=new HashMap<String, String>();
				
				Iterator<String> it1 = hmLeaveEffectiveDateType.keySet().iterator();
				
				while(it1.hasNext()) {
					String strLeaveTypeId = it1.next();
					String strLeaveEffectiveDateType = hmLeaveEffectiveDateType.get(strLeaveTypeId);
					pst = con.prepareStatement("select sum(emp_no_of_leave) as cnt,leave_type_id,is_approved from emp_leave_entry where emp_id = ? and is_approved !=1 " +
							" and entrydate>=? and leave_type_id=? group by leave_type_id,is_approved");
		//			pst = con.prepareStatement("select sum(emp_no_of_leave) as cnt,leave_type_id,is_approved from emp_leave_entry where emp_id = ? and is_approved !=1 " +
		//			" and leave_from>=? group by leave_type_id,is_approved");
					pst.setInt(1, nEmpId);
					if(strLeaveEffectiveDateType != null && strLeaveEffectiveDateType.equalsIgnoreCase("FY")) {
						pst.setDate(2, uF.getDateFormat(strFinacialYear[0], DATE_FORMAT));
						fyCnt++;
					} else {
						pst.setDate(2, uF.getDateFormat(nCurrentYear+"-01-01", DBDATE));
						cyCnt++;
					}
					pst.setInt(3, uF.parseToInt(strLeaveTypeId));
//					System.out.println("ELER/215--5 pst=====>"+pst);
					rs = pst.executeQuery();
					while(rs.next()){
						hmLeaveStatus.put(rs.getString("leave_type_id")+"_"+rs.getString("is_approved"), ""+rs.getDouble("cnt"));				
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select leave_register_id from leave_application_register " +
							" where emp_id = ? and is_paid=true and (is_modify is null or is_modify=false) " +
							" and _date between ? and ? and leave_type_id =?");
					pst.setInt(1, nEmpId);
					
					pst.setDate(2, uF.getDateFormat(nCurrentYear+1+"-01-01", DBDATE));
					pst.setDate(3, uF.getDateFormat(nCurrentYear+1+"-12-31", DBDATE));
					
					pst.setInt(4, uF.parseToInt(strLeaveTypeId));
					rs = pst.executeQuery();
					boolean strFlag = false;
					while(rs.next()){
						strFlag = true;
					}
					rs.close();
					pst.close();
					
				//===start parvez date: 31-10-2022===	
					if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_COLOR_FOR_NIGATIVE_LEAVE_BALANCE))){
						pst = con.prepareStatement("select sum(abs(leave_no)) as cnt,leave_type_id from leave_application_register " +
								" where emp_id = ? and (is_modify is null or is_modify=false) " +
								" and _date between ? and ? and leave_type_id =? group by leave_type_id");
						pst.setInt(1, nEmpId);
						if(strLeaveEffectiveDateType != null && strLeaveEffectiveDateType.equalsIgnoreCase("FY")) {
							if(strFlag){
								pst.setDate(2, uF.getDateFormat(strFinacialYear[0], DATE_FORMAT));
								
								String strFDate = uF.getDateFormat(strFinacialYear[1]+"", DATE_FORMAT, "dd/MM");
								String strFYear = uF.getDateFormat(strFinacialYear[1]+"", DATE_FORMAT, "yyyy");
								
								pst.setDate(3, uF.getDateFormat((strFDate+"/"+(uF.parseToInt(strFYear)+1)), DATE_FORMAT));
								
							} else{
								pst.setDate(2, uF.getDateFormat(strFinacialYear[0], DATE_FORMAT));
								pst.setDate(3, uF.getDateFormat(strFinacialYear[1], DATE_FORMAT));
							}
							
	//						pst.setDate(2, uF.getDateFormat(strFinacialYear[0], DATE_FORMAT));
	//						pst.setDate(3, uF.getDateFormat(strFinacialYear[1], DATE_FORMAT));
							
						} else {
							if(strFlag){
								pst.setDate(2, uF.getDateFormat(nCurrentYear+"-01-01", DBDATE));
								pst.setDate(3, uF.getDateFormat(nCurrentYear+1+"-12-31", DBDATE));
							} else{
								pst.setDate(2, uF.getDateFormat(nCurrentYear+"-01-01", DBDATE));
								pst.setDate(3, uF.getDateFormat(nCurrentYear+"-12-31", DBDATE));
							}
	//						pst.setDate(2, uF.getDateFormat(nCurrentYear+"-01-01", DBDATE));
	//						pst.setDate(3, uF.getDateFormat(nCurrentYear+"-12-31", DBDATE));
						}
						pst.setInt(4, uF.parseToInt(strLeaveTypeId));
						rs = pst.executeQuery();
//						System.out.println("pst===>"+pst);
						while(rs.next()){
							hmApproveLeaveStatus.put(rs.getString("leave_type_id"), ""+rs.getDouble("cnt"));				
						}
						rs.close();
						pst.close();
						
					} else{
						pst = con.prepareStatement("select sum(leave_no) as cnt,leave_type_id from leave_application_register " +
								" where emp_id = ? and is_paid=true and (is_modify is null or is_modify=false) " +
								" and _date between ? and ? and leave_type_id =? group by leave_type_id");
						pst.setInt(1, nEmpId);
						if(strLeaveEffectiveDateType != null && strLeaveEffectiveDateType.equalsIgnoreCase("FY")) {
							if(strFlag){
								pst.setDate(2, uF.getDateFormat(strFinacialYear[0], DATE_FORMAT));
								
								String strFDate = uF.getDateFormat(strFinacialYear[1]+"", DATE_FORMAT, "dd/MM");
								String strFYear = uF.getDateFormat(strFinacialYear[1]+"", DATE_FORMAT, "yyyy");
								
								pst.setDate(3, uF.getDateFormat((strFDate+"/"+(uF.parseToInt(strFYear)+1)), DATE_FORMAT));
								
							} else{
								pst.setDate(2, uF.getDateFormat(strFinacialYear[0], DATE_FORMAT));
								pst.setDate(3, uF.getDateFormat(strFinacialYear[1], DATE_FORMAT));
							}
							
	//						pst.setDate(2, uF.getDateFormat(strFinacialYear[0], DATE_FORMAT));
	//						pst.setDate(3, uF.getDateFormat(strFinacialYear[1], DATE_FORMAT));
							
						} else {
							if(strFlag){
								pst.setDate(2, uF.getDateFormat(nCurrentYear+"-01-01", DBDATE));
								pst.setDate(3, uF.getDateFormat(nCurrentYear+1+"-12-31", DBDATE));
							} else{
								pst.setDate(2, uF.getDateFormat(nCurrentYear+"-01-01", DBDATE));
								pst.setDate(3, uF.getDateFormat(nCurrentYear+"-12-31", DBDATE));
							}
	//						pst.setDate(2, uF.getDateFormat(nCurrentYear+"-01-01", DBDATE));
	//						pst.setDate(3, uF.getDateFormat(nCurrentYear+"-12-31", DBDATE));
						}
						pst.setInt(4, uF.parseToInt(strLeaveTypeId));
						rs = pst.executeQuery();
//						System.out.println("pst===>"+pst);
						while(rs.next()){
							hmApproveLeaveStatus.put(rs.getString("leave_type_id"), ""+rs.getDouble("cnt"));				
						}
						rs.close();
						pst.close();
					}
					
				//===end parvez date: 31-10-2022===	
				}
				
//				double nPending 	= 0; 
//				double nApproved 	= 0;
//				double nDenied 		= 0;
//				double nRemaining 	= 0;
//				double nTotal 		= 0;
//				System.out.println("hmApproveLeaveStatus="+hmApproveLeaveStatus);
				
				Iterator it = hmLeaveDetails.keySet().iterator();
				List<List<String>> reportList = new ArrayList<List<String>>();
				StringBuilder sbRemainLeave=new StringBuilder();
				StringBuilder sbApprovedLeave=new StringBuilder();
				StringBuilder sbPendingLeave=new StringBuilder();
				StringBuilder sbLeaveTypeName=new StringBuilder();
				
				while(it.hasNext()){
					String strLeaveTypeId = (String)it.next();
					String leaveTypeName = hmLeaveDetails.get(strLeaveTypeId);
					
					double dblBalance = uF.parseToDouble(hmMainBalance.get(strLeaveTypeId));
					dblBalance += uF.parseToDouble(hmAccruedBalance.get(strLeaveTypeId));
					
					double dblPaidBalance = uF.parseToDouble(hmPaidBalance.get(strLeaveTypeId));
					
				//===start parvez date: 31-10-2022===	
//					if(dblBalance > 0 && dblBalance >= dblPaidBalance){
//			            dblBalance = dblBalance - dblPaidBalance; 
//			        }
//						System.out.println("ELER/371--if--strLeaveTypeId="+strLeaveTypeId+"--dblBalance="+dblBalance+"--dblPaidBalance="+dblPaidBalance);
					
					if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_COLOR_FOR_NIGATIVE_LEAVE_BALANCE))){
						if(uF.parseToBoolean(hmPaidBalance.get(strLeaveTypeId+"_NIGATIVE_BALANCE"))){
							dblBalance = dblPaidBalance-dblBalance;
						} else{
							dblBalance = dblBalance - dblPaidBalance;
						}
//						dblBalance = dblBalance - dblPaidBalance;
					} else{
						if(dblBalance > 0 && dblBalance >= dblPaidBalance){
				            dblBalance = dblBalance - dblPaidBalance;
				        }
					}
				//===end parvez date: 31-10-2022===	
					
					double dblPending = uF.parseToDouble(hmLeaveStatus.get(strLeaveTypeId+"_0"));
					double dblApproved = uF.parseToDouble(hmApproveLeaveStatus.get(strLeaveTypeId));
					double dblDenied = uF.parseToDouble(hmLeaveStatus.get(strLeaveTypeId+"_-1"));
					double dblRemaining = dblBalance;
					
					double dblTotal = dblRemaining + dblApproved;
					
					List<String> innerList=new ArrayList<String>();   
					innerList.add(leaveTypeName);
				//===start parvez date: 31-10-2022===	
//					innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblTotal)+"");
					if(dblTotal<0){
						innerList.add("<font color=\"orange\">"+uF.formatIntoTwoDecimalWithOutComma(dblTotal)+"</font>");
					} else{
						innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblTotal)+"");
					}
				//===end parvez date: 31-10-2022===	
					innerList.add("<font color=\"orange\">"+uF.formatIntoTwoDecimalWithOutComma(dblPending)+"</font>");
					innerList.add("<font color=\"green\">"+uF.formatIntoTwoDecimalWithOutComma(dblApproved)+"</font>");
					innerList.add("<font color=\"red\">"+uF.formatIntoTwoDecimalWithOutComma(dblDenied)+"</font>");
				//===start parvez date: 31-10-2022===	
//					innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblRemaining)+"");
					if(dblRemaining<0){
						innerList.add("<font color=\"orange\">"+uF.formatIntoTwoDecimalWithOutComma(dblRemaining)+"</font>");
					} else{
						innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblRemaining)+"");
					}
				//===end parvez date: 31-10-2022===	
					innerList.add(strLeaveTypeId);
					innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblApproved));
					innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblPending));
					innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblRemaining));
					//System.out.println("approved=="+uF.formatIntoTwoDecimalWithOutComma(dblApproved));
					//System.out.println("pending=="+uF.formatIntoTwoDecimalWithOutComma(dblPending));
					
					sbLeaveTypeName.append("'"+leaveTypeName+"'");
					sbApprovedLeave.append(uF.formatIntoTwoDecimalWithOutComma(dblApproved));
					sbPendingLeave.append(uF.formatIntoTwoDecimalWithOutComma(dblPending));
					sbRemainLeave.append(uF.formatIntoTwoDecimalWithOutComma(dblTotal-(dblApproved+dblPending)));
					
					if(it.hasNext()){
						sbLeaveTypeName.append(",");
						sbApprovedLeave.append(",");
						sbRemainLeave.append(",");
						sbPendingLeave.append(",");
					}
					
					reportList.add(innerList);
				}
				
				//System.out.println("sbApprovedLeave==>"+sbApprovedLeave);
				//System.out.println("sbRemainLeave==>"+sbRemainLeave);
				//System.out.println("sbPendingLeave==>"+sbPendingLeave);
				//System.out.println("sbLeaveTypeName==>"+sbLeaveTypeName);
				
				StringBuilder sbLeaveChartData = new StringBuilder();
				sbLeaveChartData.append("{name: 'Pending',data:["+sbPendingLeave+"]},{name:'Approved',data:["+sbApprovedLeave+"]},{name: 'Remaining',data:["+sbRemainLeave+"]}");
				
				//System.out.println("sbLeaveChartData==>"+sbLeaveChartData);
				
				request.setAttribute("leaveList", reportList);
				request.setAttribute("sbLeaveChartData", sbLeaveChartData);
				request.setAttribute("sbLeaveTypeName", sbLeaveTypeName);
				
		    }
//		    System.out.println("fyCnt ===>> " + fyCnt + " -- cyCnt ===>> " + cyCnt);
		    
		    String strFromDate = "01/01/"+nCurrentYear;
		    if(fyCnt > cyCnt) {
		    	strFromDate = strFinacialYear[0];
		    }
			request.setAttribute("msg", "<h5>Consolidated leave information of "+uF.showData(strEmpName, "")+" since "+strFromDate+"</h5>");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadEmployeeLeaveEntry(){	
		return "load";
	}
	
public boolean getMaternityFrequency(int nLevelId, int nEmpId,int nOrgId, int nLocationId){

	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	
	UtilityFunctions uF = new UtilityFunctions();

	try {
		
		con = db.makeConnection(con);
		
		int totalMaternityTaken=0;
		pst = con.prepareStatement("select count(*) as leavecnt from emp_leave_entry where emp_id=? and leave_type_id " +
				"in(select leave_type_id from leave_type where is_maternity=true) group by emp_id ");
		pst.setInt(1, nEmpId);
		rs = pst.executeQuery();
		while(rs.next()){
			totalMaternityTaken=uF.parseToInt(rs.getString("leavecnt"));
		}
		rs.close();
		pst.close();
		
		pst = con.prepareStatement("SELECT * FROM leave_type lt, emp_leave_type elt where is_maternity=true and lt.leave_type_id = elt.leave_type_id " +
				" and level_id=? and lt.org_id=? and wlocation_id=? and effective_date =(select max(effective_date) from emp_leave_type " +
				" where level_id = ?  and lt.org_id=? and wlocation_id=?  and is_compensatory = false ) and lt.is_compensatory = false ");
//		pst.setBoolean(1, true);
//		pst = con.prepareStatement(query.toString());
		pst.setInt(1, nLevelId);
		pst.setInt(2, nOrgId);
		pst.setInt(3, nLocationId);
		pst.setInt(4, nLevelId);
		pst.setInt(5, nOrgId);
		pst.setInt(6, nLocationId); 
//		rs = pst.executeQuery();
		rs = pst.executeQuery();
		
		StringBuilder sbDocumentCondition = new StringBuilder();
		int count = 0;
		while(rs.next()){
			count+=uF.parseToInt(rs.getString("maternity_type_frequency"));
		}
		rs.close();
		pst.close();
		
		if(count<=totalMaternityTaken){
			return false;
		}

		request.setAttribute("idDocCondition", sbDocumentCondition.toString());
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}

	return true;
}
	
	private String getOrgId(String empId, UtilityFunctions uF) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		String orgId=null;
		 PreparedStatement pst =null;
		try {
			con = db.makeConnection(con);
			 pst = con.prepareStatement("SELECT org_id FROM employee_official_details eod, employee_personal_details epd " +
			 		" WHERE epd.emp_per_id=eod.emp_id and eod.emp_id = ? order by emp_id");
			 pst.setInt(1,uF.parseToInt(empId));
			 rs = pst.executeQuery();

			while (rs.next()) {
				orgId= rs.getString("org_id");
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
		return orgId;

	}

	public String viewEmployeeLeaveList(){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		
		try {
						
//			int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
			int nEmpId = uF.parseToInt(getStrEmpId());
			
			con = db.makeConnection(con);
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			
//			Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmployeName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			
			pst = con.prepareStatement("select * from travel_advance");
			rs = pst.executeQuery();
			Map<String, String> hmTravelAdvance = new HashMap<String, String>();
			Map<String, String> hmTravelAdvanceApproved = new HashMap<String, String>();
			Map<String, String> hmTravelAdvanceDenied = new HashMap<String, String>();
			while(rs.next()){
				hmTravelAdvance.put(rs.getString("travel_id"), rs.getString("advance_amount"));
				
				if(uF.parseToInt(rs.getString("advance_status"))==1){
					hmTravelAdvanceApproved.put(rs.getString("travel_id"), rs.getString("approved_amount"));
				}else if(uF.parseToInt(rs.getString("advance_status"))==-1){
					hmTravelAdvanceDenied.put(rs.getString("travel_id"), rs.getString("approved_amount"));
				}
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select * from leave_type");
			rs = pst.executeQuery();
			Map<String, String> hmLeaveType = new HashMap<String, String>();
			Map<String, String> hmLeaveColour = new HashMap<String, String>();
			while(rs.next()){
				hmLeaveType.put(rs.getString("leave_type_id"), rs.getString("leave_type_name"));
				hmLeaveColour.put(rs.getString("leave_type_id"), rs.getString("leave_type_colour"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
					" and effective_type='"+WORK_FLOW_LEAVE+"' group by effective_id,emp_id,user_type_id");
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();	
			Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();
			while(rs.next()){
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
				" and effective_type='"+WORK_FLOW_LEAVE+"' group by effective_id,emp_id,user_type_id");
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();	
			while(rs.next()){
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select effective_id,emp_id,user_type_id from work_flow_details where  member_type=3 " +
					" and effective_type='"+WORK_FLOW_TRAVEL+"' group by effective_id,emp_id,user_type_id");
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneTravelApproveBy = new HashMap<String, String>();			
			while(rs.next()){
				hmAnyOneTravelApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
				" and effective_type='"+WORK_FLOW_TRAVEL+"' group by effective_id,emp_id,user_type_id");
			rs = pst.executeQuery();			
			Map<String, String> hmOtherTravelApproveBy = new HashMap<String, String>();			
			while(rs.next()){
				hmOtherTravelApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			boolean isTravel = false;
			if(getDataType() != null && getDataType().equalsIgnoreCase("T")) {
				isTravel = true;
			}
			
			if(!isTravel && strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))){
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select sum(leave_register_id) as leave_register_id, leave_id from leave_application_register where leave_id in (select leave_id " +
						"from emp_leave_entry where entrydate is not null and (istravel is null or istravel=false)) group by leave_id");
				pst = con.prepareStatement(sbQuery.toString());
			} else{
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select count(leave_register_id) as leave_register_id, leave_id from leave_application_register where emp_id=? and leave_id in (select leave_id " +
						"from emp_leave_entry where entrydate is not null and (istravel is null or istravel=false) and emp_id=?) group by leave_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, nEmpId);
				pst.setInt(2, nEmpId);
			}			
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmApproveLeave = new HashMap<String, String>();
			while(rs.next()){
				hmApproveLeave.put(rs.getString("leave_id"), rs.getString("leave_register_id"));
			}
			rs.close();
			pst.close();
			
			if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN))){
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from emp_leave_entry  where entrydate is not null ");
				if(isTravel){
					sbQuery.append(" and istravel=true");
				} else {
					sbQuery.append(" and (istravel is null or istravel=false)");
				}
				sbQuery.append(" order by leave_from desc, entrydate desc");
				pst = con.prepareStatement(sbQuery.toString());
			}else if(strUserType!=null && (strUserType.equalsIgnoreCase(HRMANAGER))){
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from emp_leave_entry ele, employee_official_details eod where eod.emp_id = ele.emp_id and entrydate is not null ");
				if(isTravel){
					sbQuery.append(" and istravel=true");
				} else {
					sbQuery.append(" and (istravel is null or istravel=false)");
				}
				sbQuery.append(" order by leave_from desc, entrydate desc");
				pst = con.prepareStatement(sbQuery.toString());
			}else{
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from emp_leave_entry  where emp_id = ? and entrydate is not null ");
				if(isTravel){
					sbQuery.append(" and istravel=true");
				} else {
					sbQuery.append(" and (istravel is null or istravel=false)");
				}
				sbQuery.append(" order by leave_from desc, entrydate desc");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, nEmpId);
			}
//			System.out.println("pst emp leave =====> " + pst);
			rs = pst.executeQuery();
			StringBuilder sbAdvance = new StringBuilder();
			List<List<String>> alLeaveList = new ArrayList<List<String>>();
			int i=0;
			List<String> alTravelId = new ArrayList<String>();
			while(rs.next()){
				
				String strCurrency = "";
				if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp_id"))) > 0){
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp_id")));
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				} 
				String strModify ="";
				String strModifyReason = "";
				
				if(uF.parseToBoolean(rs.getString("is_modify"))){
					if(uF.parseToBoolean(rs.getString("istravel"))){
						strModify = "<div title=\"Canceled\" class=\"leftearly\">&nbsp;</div>";
						strModifyReason = "This travel has been canceled by "+uF.showData(hmEmployeName.get(rs.getString("modify_by")), "")+ " on "+uF.getDateFormat(rs.getString("modify_date"), DBDATE, CF.getStrReportDateFormat());
					} else {
						strModify = "<div title=\"Canceled\" class=\"leftearly\">&nbsp;</div>";
						strModifyReason = "This leave has been canceled by "+uF.showData(hmEmployeName.get(rs.getString("modify_by")), "")+ " on "+uF.getDateFormat(rs.getString("modify_date"), DBDATE, CF.getStrReportDateFormat())+". Reason:["+uF.showData(rs.getString("cancel_reason"), "")+"]";
					}
				}
				
				List<String> alInnerLeaveList = new ArrayList<String>();
				if(rs.getInt("is_approved")==0 && !rs.getBoolean("istravel")){
					/*alInnerLeaveList.add("<a href=\"EmployeeLeaveEntry.action?E="+rs.getString("leave_id")+"\">"+getStatus(rs.getInt("is_approved"))+"</a>" +
							"&nbsp; <a href=\"javascript:void();\" onclick=\"((confirm('Are You sure you want to cancel this leave?'))?getContent('myDiv_"+i+"','UpdateRequest.action?T=LC&S=2&RID="+rs.getString("leave_id")+"'):'')\" ><img title=\"Cancel Leave\" src=\"images1/icons/pullout.png\" border=\"0\" /></a>" +
							"");*/
					String strLeaveLbl = "leave";
					if(rs.getBoolean("is_compensate")) {
						strLeaveLbl = "extra working";
					}
					
					/*alInnerLeaveList.add(getStatus(rs.getInt("is_approved"))+"&nbsp; <a href=\"javascript:void();\" onclick=\"((confirm('Are You sure you want to cancel this "+strLeaveLbl+"?'))?getContent('myDiv_"+i+"','UpdateRequest.action?T=LC&S=2&RID="+rs.getString("leave_id")+"'):'')\" ><img title=\"Cancel Leave\" src=\"images1/icons/pullout.png\" border=\"0\" /></a>");*/
					alInnerLeaveList.add(getStatus(rs.getInt("is_approved"))+"&nbsp; <a href=\"javascript:void();\" onclick=\"((confirm('Are You sure you want to cancel this "+strLeaveLbl+"?'))?getContent('myDiv_"+i+"','UpdateRequest.action?T=LC&S=2&RID="+rs.getString("leave_id")+"'):'')\" ><i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Cancel Leave\"  style=\"color:#ea9900\"></i></a>");//0
					
				}else if(rs.getInt("is_approved")==0 && rs.getBoolean("istravel")){
					/*alInnerLeaveList.add("<a href=\"ApplyTravel.action?E="+rs.getString("leave_id")+"\">"+getStatus(rs.getInt("is_approved"))+"</a>" +
							"&nbsp; <a href=\"javascript:void();\" onclick=\"((confirm('This leave is already approved.\nAre You sure you want to cancel this leave?'))?getContent('myDiv_"+i+"','UpdateRequest.action?T=LC&S=2&RID="+rs.getString("leave_id")+"'):'')\" ><img title=\"Cancel Leave\" src=\"images1/icons/pullout.png\" border=\"0\" /></a>" +
							"");*/
					/*alInnerLeaveList.add(getStatus(rs.getInt("is_approved"))+"&nbsp; <a href=\"javascript:void();\" onclick=\"((confirm('This travel is already approved.\nAre You sure you want to cancel this travel?'))?getContent('myDiv_"+i+"','UpdateRequest.action?T=LC&S=2&RID="+rs.getString("leave_id")+"'):'')\" ><img title=\"Cancel Travel\" src=\"images1/icons/pullout.png\" border=\"0\" /></a>");*/
					/*alInnerLeaveList.add(getStatus(rs.getInt("is_approved"))+"&nbsp; <a href=\"javascript:void();\" onclick=\"((confirm('Are You sure you want to cancel this Travel?'))?getContent('myDiv_"+i+"','UpdateRequest.action?T=LC&S=2&RID="+rs.getString("leave_id")+"'):'')\" ><img title=\"Cancel Travel\" src=\"images1/icons/pullout.png\" border=\"0\" /></a>");*/
					alInnerLeaveList.add(getStatus(rs.getInt("is_approved"))+"&nbsp; <a href=\"javascript:void();\" onclick=\"((confirm('Are You sure you want to cancel this Travel?'))?getContent('myDiv_"+i+"','UpdateRequest.action?T=LC&S=2&RID="+rs.getString("leave_id")+"'):'')\" ><i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Cancel Travel\"  style=\"color:#ea9900\"></i></a>");//0
					
				}else{
					if(rs.getInt("is_approved") == 1){
						if(uF.parseToBoolean(rs.getString("is_modify"))){
							 /*alInnerLeaveList.add("<a href=\"javascript:void();\" onclick=\"alert('You have already cancel this leave.');\" ><img src=\"images1/icons/approved.png\" border=\"0\" title=\"Approved, Click for Cancel Leave\" ></a>");*/
							alInnerLeaveList.add("<a href=\"javascript:void(0);\" onclick=\"alert('You have already cancel this leave.');\"> <i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved, Click for Cancel Leave\"></i></a>");//0
						} else {
							 /*alInnerLeaveList.add("<a href=\"javascript:void();\" onclick=\"cancelLeave('"+rs.getString("leave_id")+"');\" ><img src=\"images1/icons/approved.png\" border=\"0\" title=\"Approved, Click for Cancel Leave\" ></a>");*/
							alInnerLeaveList.add("<a href=\"javascript:void();\" onclick=\"cancelLeave('"+rs.getString("leave_id")+"');\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved, Click for Cancel Leave\"></i></a>");//0
						}
					} else {
						alInnerLeaveList.add(getStatus(rs.getInt("is_approved")));//0
					}	
				}
				
				alInnerLeaveList.add(hmEmployeName.get(rs.getString("emp_id")));//1
				if(rs.getInt("is_approved")==1){
					alInnerLeaveList.add(uF.getDateFormat(rs.getString("approval_from"), DBDATE, CF.getStrReportDateFormat()));//2
					alInnerLeaveList.add(uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, CF.getStrReportDateFormat()));//3
				}else{
					alInnerLeaveList.add(uF.getDateFormat(rs.getString("leave_from"), DBDATE, CF.getStrReportDateFormat()));//2
					alInnerLeaveList.add(uF.getDateFormat(rs.getString("leave_to"), DBDATE, CF.getStrReportDateFormat()));//3
				}
				
				String strApproveLeaveDays = "";
				if(!rs.getBoolean("istravel") && rs.getInt("is_approved") == 1 
						&& !uF.parseToBoolean(rs.getString("ishalfday")) && uF.parseToDouble(hmApproveLeave.get(rs.getString("leave_id"))) > 0.0d){
					strApproveLeaveDays = ""+uF.parseToDouble(hmApproveLeave.get(rs.getString("leave_id")));
				} else {
					strApproveLeaveDays = ""+uF.parseToDouble(rs.getString("emp_no_of_leave"));
				}
				
//				alInnerLeaveList.add(rs.getString("emp_no_of_leave") +  ((uF.parseToBoolean(rs.getString("is_modify")))?"<div title=\"Canceled\" class=\"leftearly\">&nbsp;</div>":""));
				// TODO : Start Dattatray
				if (uF.parseToInt(rs.getString("leave_type_id")) == ON_DUTY) {
					alInnerLeaveList.add(strApproveLeaveDays +  strModify+" (OD)");//4
				}// TODO : End Dattatray 
				else {
					alInnerLeaveList.add(strApproveLeaveDays +  strModify);//4
				}
				alInnerLeaveList.add(rs.getString("reason"));//5
				
				if(rs.getString("document_attached")!=null){
					alInnerLeaveList.add(uF.showData(hmLeaveType.get(rs.getString("leave_type_id")), "") +"<div style=\"float:right\"><a href=\""+CF.getStrDocRetriveLocation()+rs.getString("document_attached")+"\" target=\"_blank\" title=\"Supported Document\"><i class=\"fa fa-file-o\" aria-hidden=\"true\" style=\"padding-top:2px\"></i></a></div>");//6
				}else if(uF.parseToBoolean(rs.getString("istravel"))){
					sbAdvance.replace(0, sbAdvance.length(), ""); 
					sbAdvance.append("Travel");
					if(hmTravelAdvance.containsKey(rs.getString("leave_id"))){
						sbAdvance.append("<br/>Advance Requested: "+strCurrency+hmTravelAdvance.get(rs.getString("leave_id")));
						if(hmTravelAdvanceApproved.containsKey(rs.getString("leave_id"))){
							if(uF.parseToInt(hmTravelAdvanceApproved.get(rs.getString("leave_id")))>0){
								sbAdvance.append("<br/><font color=\"green\">[Approved Amount: "+strCurrency+hmTravelAdvanceApproved.get(rs.getString("leave_id"))+"]</font>");
							}
						}else if(hmTravelAdvanceDenied.containsKey(rs.getString("leave_id"))){
							sbAdvance.append("<br/><font color=\"red\">[Request for advance denied]</font>");
						}
					}
					alInnerLeaveList.add(sbAdvance.toString());//6
				}else{
					alInnerLeaveList.add(uF.showData(hmLeaveType.get(rs.getString("leave_type_id")), ""));//6
				}
				
				alInnerLeaveList.add(uF.showData(hmLeaveColour.get(rs.getString("leave_type_id")), ""));//7
				
				if(uF.parseToInt(rs.getString("is_approved"))!=0){
					if(uF.parseToInt(rs.getString("user_id"))!=0){
						alInnerLeaveList.add(uF.showData(hmEmployeName.get(rs.getString("user_id")), "N/a"));//8
					}else{
						alInnerLeaveList.add("System Approved");//8
					}
				}else{
					alInnerLeaveList.add("");//8
				}
				
				
				if(rs.getBoolean("encashment_status")){
					alInnerLeaveList.add("Leave encashment");	//9
				}else{
					alInnerLeaveList.add((rs.getString("manager_reason")!=null)?rs.getString("manager_reason"):"");//9
				} 
				
				if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("leave_id"))!=null){
					alInnerLeaveList.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("leave_id")+"','"+hmEmployeName.get(rs.getString("emp_id"))+"','1');\" style=\"margin-left: 10px;\">View</a>");
				} else if(hmAnyOneTravelApproveBy!=null && hmAnyOneTravelApproveBy.get(rs.getString("leave_id"))!=null){
					alInnerLeaveList.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("leave_id")+"','"+hmEmployeName.get(rs.getString("emp_id"))+"','4');\" style=\"margin-left: 10px;\">View</a>");
				} else if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("leave_id"))!=null){
					alInnerLeaveList.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("leave_id")+"','"+hmEmployeName.get(rs.getString("emp_id"))+"','1');\" style=\"margin-left: 10px;\">View</a>");
				} else if(hmOtherTravelApproveBy!=null && hmOtherTravelApproveBy.get(rs.getString("leave_id"))!=null){
					alInnerLeaveList.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("leave_id")+"','"+hmEmployeName.get(rs.getString("emp_id"))+"','4');\" style=\"margin-left: 10px;\">View</a>");
				} else{
					alInnerLeaveList.add("");//10
				}
				
				alInnerLeaveList.add(strModifyReason);//11
				alInnerLeaveList.add(rs.getString("leave_id"));//12
				alInnerLeaveList.add(rs.getString("emp_id"));//13
				
				if(uF.parseToBoolean(rs.getString("is_concierge"))){
					alInnerLeaveList.add("Yes");//14
					if(rs.getString("travel_mode")!=null && !rs.getString("travel_mode").trim().equals("")){
						List<String> alTravelMode=Arrays.asList(rs.getString("travel_mode").trim().split(","));
						StringBuilder sbMode = null;
						for(int j=0;alTravelMode!=null && !alTravelMode.isEmpty() && j<alTravelMode.size();j++){
							if(uF.parseToInt(alTravelMode.get(j).trim()) > 0){
								if(sbMode == null){
									sbMode = new StringBuilder();
									sbMode.append(uF.getTravelMode(uF.parseToInt(alTravelMode.get(j).trim())));
								} else {
									sbMode.append(", "+uF.getTravelMode(uF.parseToInt(alTravelMode.get(j).trim())));
								}
							}
						}
						alInnerLeaveList.add(sbMode!=null ? sbMode.toString() : "");//15
						
					} else {
						alInnerLeaveList.add("");//15
					}
					
					String strBooking = "No";
					String strBookingInfo = "";
					if(uF.parseToBoolean(rs.getString("is_booking"))){
						strBooking = "Yes";
						strBookingInfo = uF.showData(rs.getString("booking_info"), "");
					}
					alInnerLeaveList.add(strBooking);//16
					alInnerLeaveList.add(strBookingInfo);//17
					
					String strAccommodation = "No";
					String strAccommodationInfo = "";
					if(uF.parseToBoolean(rs.getString("is_accommodation"))){
						strAccommodation = "Yes";
						strAccommodationInfo = uF.showData(rs.getString("accommodation_info"), "");
					}
					alInnerLeaveList.add(strAccommodation);//18
					alInnerLeaveList.add(strAccommodationInfo);//19
				} else {
					alInnerLeaveList.add("No");//14
					alInnerLeaveList.add("");//15
					alInnerLeaveList.add("No");//16
					alInnerLeaveList.add("");//17
					alInnerLeaveList.add("No");//18
					alInnerLeaveList.add("");//19
				}
				
				alInnerLeaveList.add(uF.showData(rs.getString("plan_name"), ""));//20
				alInnerLeaveList.add(uF.showData(rs.getString("place_from"), ""));//21
				alInnerLeaveList.add(uF.showData(rs.getString("destinations"), ""));//22
				if(rs.getString("from_time") !=null && rs.getString("to_time")!= null)
				{
					alInnerLeaveList.add(uF.getDateFormat(rs.getString("from_time"), DBTIME, TIME_FORMAT));//23
					alInnerLeaveList.add(uF.getDateFormat(rs.getString("to_time"), DBTIME, TIME_FORMAT));//24
			//===start parvez date:18-03-2023===	
				}else{
					alInnerLeaveList.add(null);//23
					alInnerLeaveList.add(null);//24
				}
				
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_BACKUP_EMPLOYEE_FOR_LEAVE))){
					alInnerLeaveList.add(rs.getString("backup_emp_name"));//25
				}
			//===end parvez date:18-03-2023===
				
	//				System.out.println("alInnerLeaveList:"+alInnerLeaveList);
	//				System.out.println("alInnerLeaveList size:"+alInnerLeaveList.size());
				
				alLeaveList.add(alInnerLeaveList);
				i++;
				
				if(uF.parseToBoolean(rs.getString("istravel")) && !alTravelId.contains(rs.getString("leave_id"))){
					alTravelId.add(rs.getString("leave_id"));
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("alLeaveList", alLeaveList);
			
			if(strUserType!=null && (strUserType.equalsIgnoreCase(EMPLOYEE))) {
				boolean isOptHoolidayLeave = false;
				boolean isWorkFromHome = false;
				pst= con.prepareStatement("select * from emp_leave_type elt, leave_type lt where elt.leave_type_id=lt.leave_type_id " +
					"and (lt.is_leave_opt_holiday=true or lt.is_work_from_home=true) and elt.org_id=? and elt.wlocation_id=? and " +
					"elt.level_id in (select level_id from level_details where level_id in (select level_id from designation_details " +
					"where designation_id in (select designation_id from grades_details where grade_id in (select grade_id from " +
					"employee_official_details, employee_personal_details epd where emp_id=emp_per_id and emp_id=?))))");
				pst.setInt(1, uF.parseToInt((String)session.getAttribute(ORGID)));
				pst.setInt(2, uF.parseToInt((String)session.getAttribute(WLOCATIONID)));
				pst.setInt(3, uF.parseToInt(strSesionEmpId));
//				System.out.println("pst=====>"+pst);
				rs = pst.executeQuery(); 
				while(rs.next()) {
					if(rs.getBoolean("is_leave_opt_holiday")) {
						isOptHoolidayLeave = true;
					}
					if(rs.getBoolean("is_work_from_home")) {
						isWorkFromHome = true;
					}
				}
//				System.out.println("isWorkFromHome ===>> " + isWorkFromHome);
				request.setAttribute("isOptHoolidayLeave", ""+isOptHoolidayLeave);
				request.setAttribute("isWorkFromHome", ""+isWorkFromHome);
			}
			
			if(isTravel && alTravelId.size() > 0) {
				String strTravelds = StringUtils.join(alTravelId.toArray(),",");
				
				pst = con.prepareStatement("select * from travel_booking_documents where travel_id in ("+strTravelds+")");
				rs = pst.executeQuery();
				Map<String, List<Map<String, String>>> hmBooking = new HashMap<String, List<Map<String, String>>>();
				while(rs.next()) {
					List<Map<String, String>> alData = (List<Map<String, String>>) hmBooking.get(rs.getString("travel_id"));
					if(alData == null) alData = new ArrayList<Map<String,String>>();
					
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("TRAVEL_BOOKING_ID", rs.getString("travel_booking_id"));
					hmInner.put("TRAVEL_ID", rs.getString("travel_id"));
					hmInner.put("EMP_ID", rs.getString("emp_id"));
					hmInner.put("DOCUMENT_NAME", rs.getString("document_name"));
					hmInner.put("ADDED_BY", rs.getString("added_by"));
					hmInner.put("ADDED_DATE", uF.getDateFormat(rs.getString("added_date"), DBDATE, CF.getStrReportDateFormat()));
					
					String strFilePath = null;
					if(rs.getString("document_name")!=null && !rs.getString("document_name").trim().equals("") && !rs.getString("document_name").trim().equalsIgnoreCase("NULL")) {
						if(CF.getStrDocRetriveLocation()==null) {
							strFilePath = "<a target=\"blank\" href=\"" + request.getContextPath()+DOCUMENT_LOCATION + rs.getString("document_name") + "\" title=\""+rs.getString("document_name").trim()+"\" ><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>";
						} else {
							strFilePath = "<a target=\"blank\" href=\""+CF.getStrDocRetriveLocation() +I_TRAVELS+"/"+I_DOCUMENT+"/"+rs.getString("emp_id")+"/"+rs.getString("travel_id") +"/"+ rs.getString("document_name") + "\" title=\""+rs.getString("document_name").trim()+"\" ><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>";
						}
					}
					hmInner.put("FILE_PATH", strFilePath);
					alData.add(hmInner);
					hmBooking.put(rs.getString("travel_id"), alData);
				}
				rs.close();
				pst.close();
				request.setAttribute("hmBooking", hmBooking);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	
	private String getStatus(int status){
		String strStatus = null;
		switch(status){
			case 0:
				/* strStatus = "<img src=\"images1/icons/pending.png\" border=\"0\" title=\"Pending\" >";*/
				 strStatus = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Pending\"></i>";
				break;
			case 1:
				/*strStatus = "<img src=\"images1/icons/approved.png\" border=\"0\" title=\"Approved\" >";*/
				strStatus = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\" ></i>";
				break;
			case -1:
				/*strStatus = "<img src=\"images1/icons/denied.png\" border=\"0\" title=\"Denied\" >";*/
				strStatus = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>";
				break;
			case -2:
				/*strStatus = "<img src=\"images1/icons/pullout.png\" border=\"0\" title=\"Pulled out\" >";*/
				strStatus = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\"></i>";
				break;
		}
		
		return strStatus;
	} 
	
	public HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
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

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}
	
}