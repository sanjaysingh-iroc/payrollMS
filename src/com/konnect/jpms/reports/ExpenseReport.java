package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

 public class ExpenseReport extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements,IConstants {

	private String exceldownload;
		
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null; 
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null;  
	CommonFunctions CF = null; 
	 
	private List<FillEmployee> empNamesList;
	private List<FillWLocation> wLocationList;
	private List<FillOrganisation> organisationList;
	private String f_org;
	private String strf_WLocation;
	private String strStartDate;
	private String strEndDate;
	private String strSelectedEmpId;
	private String paycycle;
	private List<FillPayCycles> paycycleListFull;
	private String paycycleDate;
	private String alertStatus;
	private String alert_type;
	private String approveStatus;
	private String currUserType;
	private String alertID;
	private String type;
	private String RID;
	
	private String f_strWLocation;
	
	 public String execute() throws Exception{
		 
		 UtilityFunctions uF = new UtilityFunctions();
		 session = request.getSession();
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if(CF==null)return LOGIN; 
			strUserType = (String) session.getAttribute(USERTYPE);
			strUserTypeId = (String) session.getAttribute(USERTYPEID);
			strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
			strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
			strSessionEmpId = (String) session.getAttribute(EMPID);
			
			request.setAttribute(TITLE, "Expense Report");
			request.setAttribute(PAGE,"/jsp/reports/ExpenseReport.jsp");

			if(uF.parseToInt(getF_org()) == 0) {
				setF_org((String)session.getAttribute(ORGID));
			}
			
			if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
				setCurrUserType("MYTEAM");
			}
			
			if(getStrf_WLocation()  != null && !getStrf_WLocation().equals("") && !getStrf_WLocation().equalsIgnoreCase("null")) {
				setF_strWLocation(getStrf_WLocation());
			}
			
			//System.out.println("ApprovalStatus===>"+getApproveStatus());
			
			if(getApproveStatus() == null || getApproveStatus().equalsIgnoreCase("null") || getApproveStatus().equals("")) {
				setApproveStatus("2");
			}
			 
			String[] arrDates = null;
			if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
				arrDates = getPaycycle().split("-");
				setPaycycle(arrDates[0] + "-" + arrDates[1] + "-" + arrDates[2]);
			} else {
				arrDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
				setPaycycle(arrDates[0] + "-" + arrDates[1] + "-" + arrDates[2]);
			}

			if(getStrStartDate()==null) {
				setStrStartDate(arrDates[0]);
			}
			if(getStrEndDate()==null) {
				setStrEndDate(arrDates[1]);
			}
			
			String strDomain = request.getServerName().split("\\.")[0];
			CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
			
			if(getPaycycleDate()==null) {
				setPaycycleDate("1");
			}
				
		System.out.println("getType() ===>> " + getType());
			
			viewReimbursementRequest(uF);
			return loadReimbursements(uF);
			}
	
	 
	 public String viewReimbursementRequest(UtilityFunctions uF) {
		 
		 //System.out.println("in viewReimbursementRequest ");
		  
		 	Connection con = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			Database db = new Database();
			db.setRequest(request);
			
			try {
				con = db.makeConnection(con);
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select er.emp_id from emp_reimbursement er, work_flow_details wfd " +
						"where er.reimbursement_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_REIMBURSEMENTS+"' and er.emp_id >0 ");
				if(getPaycycleDate()!=null && getPaycycleDate().equals("2")) {
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')>='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')<='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");
				} else {
					String[] arrDates = getPaycycle().split("-");
					
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[0], DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(to_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[1], DATE_FORMAT, DBDATE)+"'  ");
					
				}
				if (uF.parseToInt(getStrSelectedEmpId()) > 0) {
					sbQuery.append(" and er.emp_id = " + uF.parseToInt(getStrSelectedEmpId()));
				}
				
				if (uF.parseToInt(getStrf_WLocation()) > 0 || uF.parseToInt(getF_org()) > 0) {
					sbQuery.append(" and er.emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
					if (uF.parseToInt(getStrf_WLocation()) > 0) {
						sbQuery.append(" and wlocation_id = "+uF.parseToInt(getStrf_WLocation())+" ");
					}
					if (uF.parseToInt(getF_org()) > 0) {
						sbQuery.append(" and org_id = "+uF.parseToInt(getF_org())+" ");
					}
					sbQuery.append(")");
				}
				
				if(uF.parseToInt(getApproveStatus())==1) { 
					sbQuery.append(" and approval_1=1 and approval_2=1");
				} else if(uF.parseToInt(getApproveStatus())==2) {
					sbQuery.append(" and approval_1=0 and approval_2=0");
				} else if(uF.parseToInt(getApproveStatus())==3) {
					sbQuery.append(" and approval_1=-1 and approval_2=-1");
				} else if(uF.parseToInt(getApproveStatus())==4) {
					sbQuery.append(" and approval_1=-2 and approval_2=-2");
				}
				if(strUserType != null && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
					if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
//						sbQuery.append(" and (wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ) ");
						sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
					} else {
						sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
					}
				}
				sbQuery.append(" group by er.emp_id");
				// con.prepareStatement("select * from emp_reimbursement order by entry_date desc");
				pst = con.prepareStatement(sbQuery.toString());
		//System.out.println("pst 1==>"+pst);
				rs = pst.executeQuery();
				List<String> alEmp = new ArrayList<String>();
				while(rs.next()) {
					alEmp.add(rs.getString("emp_id"));
				}
				rs.close();
				pst.close();
		//	System.out.println("=======alEmp in expense Report========"+alEmp);
			
			List<List<String>> alOuter = new ArrayList<List<String>>();

     		if(alEmp!=null && alEmp.size() > 0) {
				String strEmpIds = StringUtils.join(alEmp.toArray(),",");
				//System.out.println("strEmpIds==>"+strEmpIds);
				
				Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
				if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
				Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
				
				Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
				Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
				if(hmEmpCode == null) hmEmpCode = new HashMap<String, String>();
				
				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
				Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
				Map<String, String> hmProjectMap = CF.getProjectNameMap(con);
				Map<String, String> hmTravelPlanMap = CF.getTravelPlanMap(con);
			
				//*****************1st query*****************
				sbQuery=new StringBuilder();	
				sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
						" and effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' and effective_id in(select reimbursement_id from emp_reimbursement " +
						"where reimbursement_id>0  and emp_id in("+strEmpIds+") ");
				if(getPaycycleDate()!=null && getPaycycleDate().equals("2")) {
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')>='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')<='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");
				} else {
					String[] arrDates = getPaycycle().split("-");				
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[0], DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(to_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[1], DATE_FORMAT, DBDATE)+"'  ");				
				}
				sbQuery.append(") group by effective_id");
				pst=con.prepareStatement(sbQuery.toString());
		//		System.out.println("pst 1 =========>> " + pst);
				rs = pst.executeQuery();
				Map<String, String> hmNextApproval = new HashMap<String, String>();
				while(rs.next()) {
					hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
				}
				rs.close();
				pst.close();
				
				
				//******************2nd Query*****************
				
				sbQuery=new StringBuilder();	
				sbQuery.append("select effective_id,user_type_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
						" and is_approved=0 and effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' and effective_id in(select reimbursement_id " +
						"from emp_reimbursement where reimbursement_id>0  and emp_id in("+strEmpIds+") ");
				if(getPaycycleDate()!=null && getPaycycleDate().equals("2")) {
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')>='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')<='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");
				} else {
					String[] arrDates = getPaycycle().split("-");				
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[0], DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(to_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[1], DATE_FORMAT, DBDATE)+"'  ");				
				}
				sbQuery.append(")");
				if(strUserType != null && strUserType.equals(ADMIN)) {
					sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
				} else {
					sbQuery.append(" and user_type_id=? ");
				}
				sbQuery.append("group by effective_id,user_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1,uF.parseToInt(strSessionEmpId));
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
					pst.setInt(2, uF.parseToInt(strBaseUserTypeId));
				} else {
					pst.setInt(2, uF.parseToInt(strUserTypeId));
				}
				if(strUserType != null && strUserType.equals(ADMIN)) {
					pst.setInt(3, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
				}
		//	System.out.println("pst 2 =========>> " + pst);
				rs = pst.executeQuery();
				Map<String, String> hmMemNextApproval = new HashMap<String, String>();
				while(rs.next()) {
					hmMemNextApproval.put(rs.getString("effective_id")+"_"+rs.getString("user_type_id"), rs.getString("member_position"));
				}
				rs.close();
				pst.close();
				
				//************************3rd Query****************
				
				sbQuery=new StringBuilder();	
				sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' and status=0 " +
						" and effective_id in(select reimbursement_id from emp_reimbursement where reimbursement_id>0 and emp_id in("+strEmpIds+") ");
				if(getPaycycleDate()!=null && getPaycycleDate().equals("2")) {
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')>='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')<='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
				} else {
					String[] arrDates = getPaycycle().split("-");				
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[0], DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(to_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[1], DATE_FORMAT, DBDATE)+"'  ");				
				}
				sbQuery.append(") group by effective_id");
				pst=con.prepareStatement(sbQuery.toString());
		//	System.out.println("pst 3 =========>> " + pst);
				rs = pst.executeQuery();			
				List<String> deniedList=new ArrayList<String>();			
				while(rs.next()) {
					if(!deniedList.contains(rs.getString("effective_id"))) {
						deniedList.add(rs.getString("effective_id"));
					}
				}	
				rs.close();
				pst.close();
				
				//********************4rth Query*****************
				sbQuery=new StringBuilder();	
				sbQuery.append("select reimbursement_id from emp_reimbursement where approval_1=-1 and approval_2=-1 and reimbursement_id>0 and emp_id in("+strEmpIds+") ");
				if(getPaycycleDate()!=null && getPaycycleDate().equals("2")) {
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')>='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')<='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");
				} else {
					String[] arrDates = getPaycycle().split("-");				
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[0], DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(to_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[1], DATE_FORMAT, DBDATE)+"'  ");				
				}
				pst=con.prepareStatement(sbQuery.toString());
		//	System.out.println("pst 4 =========>> " + pst);
				rs = pst.executeQuery();			
				while(rs.next()) {
					if(!deniedList.contains(rs.getString("reimbursement_id"))) {
						deniedList.add(rs.getString("reimbursement_id"));
					}
				}
				rs.close();
				pst.close();
	
				//***************5th Query******************
				sbQuery=new StringBuilder();	
				sbQuery.append("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
						" and emp_id=? and effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' and status=0 " +
						"and effective_id in(select reimbursement_id from emp_reimbursement where reimbursement_id>0 and emp_id in("+strEmpIds+") ");
				if(getPaycycleDate()!=null && getPaycycleDate().equals("2")) {
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')>='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')<='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");
				} else {
					String[] arrDates = getPaycycle().split("-");				
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[0], DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(to_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[1], DATE_FORMAT, DBDATE)+"'  ");				
				}
				sbQuery.append(") group by effective_id,is_approved");
				pst=con.prepareStatement(sbQuery.toString());
		//	System.out.println("pst 5 =========>> " + pst);
				pst.setInt(1,uF.parseToInt(strSessionEmpId));
				rs = pst.executeQuery();			
				Map<String, String> hmAnyOneApproval = new HashMap<String, String>();			
				while(rs.next()) {
					hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
				}
				rs.close();
				pst.close();
				
				//***********************6th Query****************
				sbQuery=new StringBuilder();
				sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
						" and effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' and effective_id in(select reimbursement_id from " +
						"emp_reimbursement where reimbursement_id>0 and emp_id in("+strEmpIds+") ");
				if(getPaycycleDate()!=null && getPaycycleDate().equals("2")) {
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')>='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')<='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");
				} else {
					String[] arrDates = getPaycycle().split("-");				
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[0], DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(to_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[1], DATE_FORMAT, DBDATE)+"'  ");				
				}
				sbQuery.append(") group by effective_id,emp_id,user_type_id");
				pst = con.prepareStatement(sbQuery.toString());
	//	System.out.println("pst 6 =========>> " + pst);
				rs = pst.executeQuery();			
				Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();	
				Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();
				while(rs.next()) {
					hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
					hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
				}
				rs.close();
				pst.close();
				
				//**************************7th Query*************
				sbQuery=new StringBuilder();
				sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
					" and effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' and effective_id in(select reimbursement_id " +
					"from emp_reimbursement where reimbursement_id>0 and emp_id in("+strEmpIds+") ");
				if(getPaycycleDate()!=null && getPaycycleDate().equals("2")) {
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')>='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')<='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");
				} else {
					String[] arrDates = getPaycycle().split("-");				
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[0], DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(to_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[1], DATE_FORMAT, DBDATE)+"'  ");				
				}
				sbQuery.append(") group by effective_id,emp_id,user_type_id");
				pst = con.prepareStatement(sbQuery.toString());
		//	System.out.println("pst 7 =========>> " + pst);
				rs = pst.executeQuery();			
				Map<String, String> hmotherApproveBy = new HashMap<String, String>();	
				while(rs.next()) {
					hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
					hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
				}
				rs.close();
				pst.close();
				
				
				//********************8th Query*******************
				sbQuery=new StringBuilder();	
				sbQuery.append("select emp_id,effective_id,user_type_id from work_flow_details where effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' and status=0 " +
						"and effective_id in(select reimbursement_id from emp_reimbursement where reimbursement_id>0 and emp_id in("+strEmpIds+") ");
				if(getPaycycleDate()!=null && getPaycycleDate().equals("2")) {
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')>='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')<='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");
				} else {
					String[] arrDates = getPaycycle().split("-");				
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[0], DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(to_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[1], DATE_FORMAT, DBDATE)+"'  ");				
				}
				if(strUserType != null && strUserType.equals(ADMIN)) {
					sbQuery.append(") and (user_type_id=? or user_type_id=?) ");
				} else {
					sbQuery.append(") and user_type_id=? ");
				}
				sbQuery.append(" order by effective_id,member_position");
				pst=con.prepareStatement(sbQuery.toString());
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
					pst.setInt(1, uF.parseToInt(strBaseUserTypeId));
				} else {
					pst.setInt(1, uF.parseToInt(strUserTypeId));
				}
				if(strUserType != null && strUserType.equals(ADMIN)) {
					pst.setInt(2, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
				}
		//	System.out.println("pst 8 =========>> " + pst);
				rs = pst.executeQuery();			
				Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();
				Map<String, List<String>> hmCheckEmpUserType = new HashMap<String, List<String>>();
				while(rs.next()) {
					List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
					if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
					checkEmpList.add(rs.getString("emp_id"));
					
					List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("effective_id")+"_"+rs.getString("emp_id"));
					if(checkEmpUserTypeList == null)checkEmpUserTypeList = new ArrayList<String>();				
					checkEmpUserTypeList.add(rs.getString("user_type_id"));
					
					hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
					hmCheckEmpUserType.put(rs.getString("effective_id")+"_"+rs.getString("emp_id"), checkEmpUserTypeList);
				}
				rs.close();
				pst.close();
				
				//**********************9th Query*************
				sbQuery=new StringBuilder();	
				sbQuery.append("select status,effective_id from work_flow_details where effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' and status=0 " +
						"and effective_id in(select reimbursement_id from emp_reimbursement where reimbursement_id>0 and emp_id in("+strEmpIds+") ");
				if(getPaycycleDate()!=null && getPaycycleDate().equals("2")) {
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')>='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')<='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");
				} else {
					String[] arrDates = getPaycycle().split("-");				
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[0], DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(to_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[1], DATE_FORMAT, DBDATE)+"'  ");				
				}
				sbQuery.append(") order by effective_id,status");
				pst=con.prepareStatement(sbQuery.toString());
		//	System.out.println("pst 9 =========>> " + pst);
				rs = pst.executeQuery();			
				Map<String, String> hmCheckStatus = new HashMap<String,String>();	
				while(rs.next()) {
					String status=rs.getString("status");
					hmCheckStatus.put(rs.getString("effective_id"), status);
				}
				rs.close();
				pst.close();
				
				//*******************10th Query*************
				
				
				sbQuery = new StringBuilder();
				sbQuery.append("select er.*,wfd.user_type_id as user_type from emp_reimbursement er, work_flow_details wfd " +
						"where er.reimbursement_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_REIMBURSEMENTS+"' and er.emp_id >0 " +
						"and er.emp_id in("+strEmpIds+") and er.parent_id=0 ");
				if(getPaycycleDate()!=null && getPaycycleDate().equals("2")) {
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')>='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')<='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");
				} else {
					String[] arrDates = getPaycycle().split("-");
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[0], DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(to_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[1], DATE_FORMAT, DBDATE)+"'  ");
				}
				if (uF.parseToInt(getStrSelectedEmpId()) > 0) {
					sbQuery.append(" and er.emp_id = " + uF.parseToInt(getStrSelectedEmpId()));
				}
				
				if (uF.parseToInt(getStrf_WLocation()) > 0 || uF.parseToInt(getF_org()) > 0) {
					sbQuery.append(" and er.emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
					if (uF.parseToInt(getStrf_WLocation()) > 0) {
						sbQuery.append(" and wlocation_id = "+uF.parseToInt(getStrf_WLocation())+" ");
					}
					if (uF.parseToInt(getF_org()) > 0) {
						sbQuery.append(" and org_id = "+uF.parseToInt(getF_org())+" ");
					}
					sbQuery.append(")");
				}
				
				if(uF.parseToInt(getApproveStatus())==1) {
					sbQuery.append(" and approval_1=1 and approval_2=1");
				} else if(uF.parseToInt(getApproveStatus())==2) {
					sbQuery.append(" and approval_1=0 and approval_2=0");
				} else if(uF.parseToInt(getApproveStatus())==3) {
					sbQuery.append(" and approval_1=-1 and approval_2=-1");
				} else if(uF.parseToInt(getApproveStatus())==4) {
					sbQuery.append(" and approval_1=-2 and approval_2=-2");
				}
				if(strUserType != null && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
					if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
	//					sbQuery.append(" and (wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ) ");
						sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
					} else {
						sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
					}
				}
				sbQuery.append(" order by er.entry_date desc");
				// con.prepareStatement("select * from emp_reimbursement order by entry_date desc");
				pst = con.prepareStatement(sbQuery.toString());
				
				List<String> alList = new ArrayList<String>();		
	//		System.out.println("pst 10 in expense report=========>> " + pst);
	//			System.out.println("admin pst ====>"+pst);
				rs = pst.executeQuery();
				int nCount = 0;
				while(rs.next()){
					if (rs.getInt("emp_id") < 0) {
						continue;
					}
					
					List<String> checkEmpList=hmCheckEmp.get(rs.getString("reimbursement_id"));
					if(checkEmpList==null) checkEmpList=new ArrayList<String>();
					
					List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("reimbursement_id")+"_"+strSessionEmpId);
					if(checkEmpUserTypeList==null) checkEmpUserTypeList = new ArrayList<String>();
					
					boolean checkGHRInWorkflow = true;
					if(checkEmpUserTypeList.contains(hmUserTypeIdMap.get(HRMANAGER)) && !checkEmpUserTypeList.contains(hmUserTypeIdMap.get(ADMIN)) && strUserType != null && strUserType.equals(ADMIN)) {
						checkGHRInWorkflow = false;
					}
					
					if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN)) {
						continue;
					}
					String userType = rs.getString("user_type");				
					if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && alList.contains(rs.getString("reimbursement_id"))) {
						continue;
					} else if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("reimbursement_id"))) {
						userType = strUserTypeId;
						alList.add(rs.getString("reimbursement_id"));
					} else if(!checkEmpUserTypeList.contains(userType)) {
						continue;
					}
					
					String strCurrId = hmEmpCurrency.get(rs.getString("emp_id"));
					if(rs.getInt("reimb_currency") > 0) {
						strCurrId = rs.getString("reimb_currency"); 
					}
					Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
					if (hmCurrencyInner == null) hmCurrencyInner = new HashMap<String, String>();
					String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
	
					String strReimbursementType = null;
					if (rs.getString("reimbursement_type1") != null && rs.getString("reimbursement_type1").equalsIgnoreCase("P")) {
						strReimbursementType = "project " + uF.showData(hmProjectMap.get(rs.getString("pro_id")), "");
					} else if (rs.getString("reimbursement_type1") != null && rs.getString("reimbursement_type1").equalsIgnoreCase("T")) {
						strReimbursementType = "travel plan " + uF.showData(hmTravelPlanMap.get(rs.getString("reimbursement_type")), "");
					} else {
						strReimbursementType = uF.showData(rs.getString("reimbursement_type"), "");
					}
	
					String strBulkReim="";
				
					
				nCount++;
				
				List <String> alInner = new ArrayList<String>();
				alInner.add(uF.showData(String.valueOf(nCount),""));
				alInner.add((uF.showData((String)hmEmpCode.get(rs.getString("emp_id")),"")));
				alInner.add(uF.showData(hmEmpNames.get(rs.getString("emp_id")),""));
				alInner.add(uF.showData(uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()),""));
				alInner.add(uF.showData(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()),""));
				alInner.add(uF.showData((rs.getString("reimb_from_date") != null) ? uF.getDateFormat(rs.getString("reimb_from_date"), DBDATE, CF.getStrReportDateFormat()) : "-",""));
				alInner.add(uF.showData(rs.getString("reimbursement_info"),""));
				alInner.add(uF.showData(rs.getString("reimbursement_purpose"),""));
				alInner.add(uF.showData(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("reimbursement_amount"))),""));
				alInner.add(uF.showData((rs.getString("entry_date") != null) ? uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()) : "-",""));
				
				String strApprovalStatus = "";
				String strApprovalBy = "";
				String strApprovalDate = "";
				if (rs.getInt("approval_1") == -1) {
					strApprovalBy = hmEmpNames.get(rs.getString("approval_1_emp_id"));
					strApprovalDate = rs.getString("approval_1_date");
					strApprovalStatus = "Denied";
				} else if (rs.getInt("approval_1") == 0) {
					strApprovalStatus = "Pending";
				} else if (rs.getInt("approval_1") == 1) {
					strApprovalBy = hmEmpNames.get(rs.getString("approval_1_emp_id"));
					strApprovalDate = rs.getString("approval_1_date");
					strApprovalStatus = "Approved";
				} else if (rs.getInt("approval_1") == -2) {
					strApprovalBy = hmEmpNames.get(rs.getString("cancel_by"));
					strApprovalDate = rs.getString("cancel_date");
					strApprovalStatus = "Canceled";
				}
				alInner.add(uF.showData(strApprovalStatus, ""));
				alInner.add(uF.showData(strApprovalBy,""));
				alInner.add(uF.showData((strApprovalDate != null && !strApprovalDate.equals("")) ? uF.getDateFormat(strApprovalDate, DBDATE, CF.getStrReportDateFormat()) : "", ""));

		//		System.out.println("alInner==>"+alInner);
				alOuter.add(alInner);
				
				
			}
				rs.close();
				pst.close();
				
			//*******************bulk Pst******************
				sbQuery = new StringBuilder();
				sbQuery.append("select er.*,wfd.user_type_id as user_type from emp_reimbursement er, work_flow_details wfd " +
						"where er.reimbursement_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_REIMBURSEMENTS+"' and er.emp_id >0 " +
						"and er.emp_id in("+strEmpIds+") and er.parent_id>0 ");
				if(getPaycycleDate()!=null && getPaycycleDate().equals("2")) {
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')>='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')<='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");
				} else {
					String[] arrDates = getPaycycle().split("-");
					sbQuery.append(" and to_date(from_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[0], DATE_FORMAT, DBDATE)+"' ");
					sbQuery.append(" and to_date(to_date::text,'"+DBDATE+"')='"+uF.getDateFormat(arrDates[1], DATE_FORMAT, DBDATE)+"'  ");
				}
				if (uF.parseToInt(getStrSelectedEmpId()) > 0) {
					sbQuery.append(" and er.emp_id = " + uF.parseToInt(getStrSelectedEmpId()));
				}
				
				if (uF.parseToInt(getStrf_WLocation()) > 0 || uF.parseToInt(getF_org()) > 0) {
					sbQuery.append(" and er.emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
					if (uF.parseToInt(getStrf_WLocation()) > 0) {
						sbQuery.append(" and wlocation_id = "+uF.parseToInt(getStrf_WLocation())+" ");
					}
					if (uF.parseToInt(getF_org()) > 0) {
						sbQuery.append(" and org_id = "+uF.parseToInt(getF_org())+" ");
					}
					sbQuery.append(")");
				}
				
				if(uF.parseToInt(getApproveStatus())==1) {
					sbQuery.append(" and approval_1=1 and approval_2=1");
				} else if(uF.parseToInt(getApproveStatus())==2) {
					sbQuery.append(" and approval_1=0 and approval_2=0");
				} else if(uF.parseToInt(getApproveStatus())==3) {
					sbQuery.append(" and approval_1=-1 and approval_2=-1");
				} else if(uF.parseToInt(getApproveStatus())==4) {
					sbQuery.append(" and approval_1=-2 and approval_2=-2");
				}
				if(strUserType != null && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
					if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
	//					sbQuery.append(" and (wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ) ");
						sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
					} else {
						sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
					}
				}
				sbQuery.append(" order by er.entry_date desc");
				pst = con.prepareStatement(sbQuery.toString());
	//		System.out.println("bulk pst ===>> " + pst);
				rs = pst.executeQuery();
				
				Map<String, Map<String, String>> hmBulkExpenseData = new LinkedHashMap<String, Map<String,String>>();
				while (rs.next()) {
					if (rs.getInt("emp_id") < 0) {
						continue;
					}
	//				System.out.println("in while 2");
					List<String> checkEmpList=hmCheckEmp.get(rs.getString("reimbursement_id"));
					if(checkEmpList==null) checkEmpList=new ArrayList<String>();
					
					List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("reimbursement_id")+"_"+strSessionEmpId);
					if(checkEmpUserTypeList==null) checkEmpUserTypeList = new ArrayList<String>();
					
					boolean checkGHRInWorkflow = true;
					if(checkEmpUserTypeList.contains(hmUserTypeIdMap.get(HRMANAGER)) && !checkEmpUserTypeList.contains(hmUserTypeIdMap.get(ADMIN)) && strUserType != null && strUserType.equals(ADMIN)) {
						checkGHRInWorkflow = false;
					}
					
					if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN)) {
						continue;
					}
					String userType = rs.getString("user_type");				
					if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && alList.contains(rs.getString("reimbursement_id"))) {
						continue;
					} else if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("reimbursement_id"))) {
						userType = strUserTypeId;
						alList.add(rs.getString("reimbursement_id"));
					} else if(!checkEmpUserTypeList.contains(userType)) {
						continue;
					}
					
					String strApprovalStatus = "";
					String strApprovalBy = "";
					String strApprovalDate = "";
					
					String strCurrId = hmEmpCurrency.get(rs.getString("emp_id"));
					if(rs.getInt("reimb_currency") > 0) {
						strCurrId = rs.getString("reimb_currency"); 
					}
					Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
					if (hmCurrencyInner == null) hmCurrencyInner = new HashMap<String, String>();
					String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");

					String strBulkReim="";

					boolean isApproval1 = false;
					if (rs.getInt("approval_1") == -1) {
					//	sb.append(" has been denied by " + hmEmpNames.get(rs.getString("approval_1_emp_id")));
						isApproval1 = true;
						strApprovalBy = hmEmpNames.get(rs.getString("approval_1_emp_id"));
						strApprovalDate = rs.getString("approval_1_date");
						strApprovalStatus = "Denied";
					} else if (rs.getInt("approval_1") == 0) {
					//	sb.append(" is waiting for manager's approval");
						isApproval1 = true; 
						strApprovalStatus = "Pending";
					} else if (rs.getInt("approval_1") == 1) {
					//	sb.append(" is approved by " + hmEmpNames.get(rs.getString("approval_1_emp_id")) + " on " + uF.getDateFormat(rs.getString("approval_1_date"), DBDATE, CF.getStrReportDateFormat()));
						isApproval1 = true;
						strApprovalBy = hmEmpNames.get(rs.getString("approval_1_emp_id"));
						strApprovalDate = rs.getString("approval_1_date");
						strApprovalStatus = "Approved";
					} else if (rs.getInt("approval_1") == -2) {
					//	sb.append(" has been canceled by " + uF.showData(hmEmpNames.get(rs.getString("cancel_by")),"") + " on " + uF.getDateFormat(rs.getString("cancel_date"), DBDATE, CF.getStrReportDateFormat()));
						isApproval1 = true;
						strApprovalBy = hmEmpNames.get(rs.getString("cancel_by"));
						strApprovalDate = rs.getString("cancel_date");
						strApprovalStatus = "Canceled";
					}
					nCount++;
					
					List <String> alInner = new ArrayList<String>();
					alInner.add(uF.showData(String.valueOf(nCount),""));
					alInner.add((uF.showData((String)hmEmpCode.get(rs.getString("emp_id")),"")));
					alInner.add(uF.showData(hmEmpNames.get(rs.getString("emp_id")),""));
					alInner.add(uF.showData(uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()),""));
					alInner.add(uF.showData(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()),""));
					alInner.add(uF.showData((rs.getString("reimb_from_date") != null) ? uF.getDateFormat(rs.getString("reimb_from_date"), DBDATE, CF.getStrReportDateFormat()) : "-",""));
					alInner.add(uF.showData(rs.getString("reimbursement_info"),""));
					alInner.add(uF.showData(rs.getString("reimbursement_purpose"),""));
					alInner.add(uF.showData(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("reimbursement_amount"))),""));
					alInner.add(uF.showData((rs.getString("entry_date") != null) ? uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()) : "-",""));
					alInner.add(uF.showData(strApprovalStatus, ""));
					alInner.add(uF.showData(strApprovalBy,""));
					alInner.add(uF.showData((strApprovalDate != null && !strApprovalDate.equals("")) ? uF.getDateFormat(strApprovalDate, DBDATE, CF.getStrReportDateFormat()) : "", ""));
					
					alOuter.add(alInner);
				}
				rs.close();
				pst.close();
			
     		}
    // 		System.out.println("alOuter.size()==>"+alOuter.size());
     		request.setAttribute("alOuter", alOuter);
     		
			}catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
			return SUCCESS;
	 }
	 
	
	 public String loadReimbursements(UtilityFunctions uF) {
			paycycleListFull = new FillPayCycles(request).fillPayCycles(CF, getF_org());
			
			if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
		//		empNamesList = new FillEmployee(request).fillEmployeeNameByAccess(getF_org(), (String)session.getAttribute(ORG_ACCESS),getStrf_WLocation(), (String)session.getAttribute(WLOCATION_ACCESS),strUserType,false);
				if(getPaycycleDate()!=null && getPaycycleDate().equals("2")) {
					empNamesList = new FillEmployee(request).fillEmployeeNameByAccess(getF_org(), (String)session.getAttribute(ORG_ACCESS),getStrf_WLocation(), (String)session.getAttribute(WLOCATION_ACCESS),strUserType,false,getStrStartDate(),getStrEndDate());
				} else {
					String[] arrDates1 = getPaycycle().split("-");
					empNamesList = new FillEmployee(request).fillEmployeeNameByAccess(getF_org(), (String)session.getAttribute(ORG_ACCESS),getStrf_WLocation(), (String)session.getAttribute(WLOCATION_ACCESS),strUserType,false,arrDates1[0],arrDates1[1]);
				}
				
				if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
					wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
					organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
				} else {
					organisationList = new FillOrganisation(request).fillOrganisation();
					wLocationList = new FillWLocation(request).fillWLocation(getF_org());
				}
			}
			getSelectedFilter(uF);
			
			return LOAD;
		}

	 
		private void getSelectedFilter(UtilityFunctions uF) {
			Map<String,String> hmFilter=new HashMap<String, String>();
			List<String> alFilter = new ArrayList<String>();
			
			if((strUserType != null && !strUserType.equals(MANAGER) && !strUserType.equals(EMPLOYEE)) || (getCurrUserType() != null && getCurrUserType().equals(strBaseUserType))) {
				alFilter.add("ORGANISATION");
				if(getF_org()!=null) {
					String strOrg="";
					for(int i=0;organisationList!=null && i<organisationList.size();i++) {
						if(getF_org().equals(organisationList.get(i).getOrgId())) {
							strOrg = organisationList.get(i).getOrgName();
						}
					}
					if(strOrg!=null && !strOrg.equals("")) {
						hmFilter.put("ORGANISATION", strOrg);
					} else {
						hmFilter.put("ORGANISATION", "All Organisation");
					}
				} else {
					hmFilter.put("ORGANISATION", "All Organisation");
				}
				
				alFilter.add("LOCATION");
				if(getStrf_WLocation()!=null) {
					String strLocation="";
					for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
						if(getStrf_WLocation().equals(wLocationList.get(i).getwLocationId())) {
							strLocation=wLocationList.get(i).getwLocationName();
						}
					}
					if(strLocation!=null && !strLocation.equals("")) {
						hmFilter.put("LOCATION", strLocation);
					} else {
						hmFilter.put("LOCATION", "All Locations");
					}
				} else {
					hmFilter.put("LOCATION", "All Locations");
				}
				
				
				alFilter.add("EMP");
//				System.out.println("getStrSelectedEmpId() ===>> " + getStrSelectedEmpId());
				if(getStrSelectedEmpId()!=null) {
					String strEmpName="";
					for(int i=0;empNamesList!=null && i<empNamesList.size();i++) {
						if(getStrSelectedEmpId().equals(empNamesList.get(i).getEmployeeId())) {
//							System.out.println("in if getStrSelectedEmpId() ===>> " + getStrSelectedEmpId());
							strEmpName=empNamesList.get(i).getEmployeeName();
						}
					}
					if(strEmpName!=null && !strEmpName.equals("")) {
						hmFilter.put("EMP", strEmpName);
					} else {
						hmFilter.put("EMP", "All Employee");
					}
				} else {
					hmFilter.put("EMP", "All Employee");
				}
			}

			if(getPaycycleDate()!=null && getPaycycleDate().equals("2")) {
				alFilter.add("FROMTO");
				if(getStrStartDate() != null && getStrEndDate() != null) {
					hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
				}
			} else {
				alFilter.add("PAYCYCLE");
				String strPaycycle = "";
				String[] strPayCycleDates = null;
				if (getPaycycle() != null) {
					strPayCycleDates = getPaycycle().split("-");
					strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
				}
				hmFilter.put("PAYCYCLE", strPaycycle + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
			}
			
			alFilter.add("STATUS");
			if(uF.parseToInt(getApproveStatus())==1) {
				hmFilter.put("STATUS", "Approved");
			} else if(uF.parseToInt(getApproveStatus())==2) {
				hmFilter.put("STATUS", "Pending");
			} else if(uF.parseToInt(getApproveStatus())==3) {
				hmFilter.put("STATUS", "Denied");
			} else if(uF.parseToInt(getApproveStatus())==4) {
				hmFilter.put("STATUS", "Canceled");
			} else {
				hmFilter.put("STATUS", "All");
			}
			
			String selectedFilter= CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
			request.setAttribute("selectedFilter", selectedFilter);
		}
 
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public List<FillEmployee> getEmpNamesList() {
		return empNamesList;
	}

	public void setEmpNamesList(List<FillEmployee> empNamesList) {
		this.empNamesList = empNamesList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getStrf_WLocation() {
		return strf_WLocation;
	}

	public void setStrf_WLocation(String strf_WLocation) {
		this.strf_WLocation = strf_WLocation;
	}

	public String getStrStartDate() {
		return strStartDate;
	}

	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}

	public String getStrEndDate() {
		return strEndDate;
	}

	public void setStrEndDate(String strEndDate) {
		this.strEndDate = strEndDate;
	}

	public String getStrSelectedEmpId() {
		return strSelectedEmpId;
	}

	public void setStrSelectedEmpId(String strSelectedEmpId) {
		this.strSelectedEmpId = strSelectedEmpId;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public List<FillPayCycles> getPaycycleListFull() {
		return paycycleListFull;
	}

	public void setPaycycleListFull(List<FillPayCycles> paycycleListFull) {
		this.paycycleListFull = paycycleListFull;
	}

	public String getPaycycleDate() {
		return paycycleDate;
	}

	public void setPaycycleDate(String paycycleDate) {
		this.paycycleDate = paycycleDate;
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

	public String getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(String approveStatus) {
		this.approveStatus = approveStatus;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRID() {
		return RID;
	}

	public void setRID(String rID) {
		RID = rID;
	}

	public String getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}
}
