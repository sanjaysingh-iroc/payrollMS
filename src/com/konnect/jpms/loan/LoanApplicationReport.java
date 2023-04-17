package com.konnect.jpms.loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class LoanApplicationReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	HttpSession session;
	String strUserType;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null; 
	
	String f_org;
	String strLoanCode;
	String f_strWLocation;
	String f_department;
	String f_level;
	String strEmpId;
	
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillEmployee> empList;
//	List<FillLoanCode> loanList;
	List<FillOrganisation> organisationList;
	
	String alertStatus;
	String alert_type;
	
	String currUserType;
	String alertID;
	
	public String execute() throws Exception {
		
		session = request.getSession(); 
		CF= (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, PLoanApplicationReport);  
		request.setAttribute(TITLE, "Apply & Approve Loan");
		strUserType = (String)session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		String action = request.getParameter("action");
		String loanAppliedId = request.getParameter("loanAppliedId");
		String empId = request.getParameter("empId");
		
		if(action!=null && action.trim().equalsIgnoreCase("cancel") && uF.parseToInt(loanAppliedId) > 0 && uF.parseToInt(empId) > 0){
			cancelLoan(uF, uF.parseToInt(loanAppliedId), uF.parseToInt(empId));
			return "ajax";
		}
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
//		if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(LOAN_REQUEST_ALERT)) {
//			String strDomain = request.getServerName().split("\\.")[0];
//			CF.updateUserAlerts(CF,request,strSessionEmpId,strDomain,LOAN_REQUEST_ALERT,UPDATE_ALERT);
//		} else if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(LOAN_APPROVAL_ALERT)) {
//			String strDomain = request.getServerName().split("\\.")[0];
//			CF.updateUserAlerts(CF,request,strSessionEmpId,strDomain,LOAN_APPROVAL_ALERT,UPDATE_ALERT);
//		}
//		System.out.println("redirect ===>> ");
		if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)) {
			setStrEmpId((String)session.getAttribute(EMPID));
		}
		if(getF_org()==null) {
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		
		boolean isEmpLoanAutoApprove = CF.getFeatureManagementStatus(request, uF, F_EMP_LOAN_AUTO_APPROVE);
		request.setAttribute("isEmpLoanAutoApprove", ""+isEmpLoanAutoApprove);
		
		if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)) {
			viewEmployeeLoan(uF,isEmpLoanAutoApprove);
		} else {
			viewEmployeeLoanByApproval(uF,isEmpLoanAutoApprove);
		}
		
		return loadEmployeeLoan(uF);
	}
	
	private void cancelLoan(UtilityFunctions uF, int loanAppliedId, int empId) {
		Connection con = null;
		PreparedStatement pst=null;		
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from loan_applied_details where emp_id=? and loan_applied_id=?");
			pst.setInt(1, empId);
			pst.setInt(2, loanAppliedId);
//			System.out.println("pst==>"+pst);
			int x = pst.executeUpdate();
			
			if(x > 0){
				request.setAttribute("STATUS_MSG", "Canceled");
				
				pst = con.prepareStatement("delete from loan_payments where emp_id=? and loan_applied_id=?");
				pst.setInt(1, empId);
				pst.setInt(2, loanAppliedId);
//				System.out.println("pst==>"+pst);
				pst.execute();
			} else {
				request.setAttribute("STATUS_MSG", "Not Canceled");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}	
	
	private void viewEmployeeLoanByApproval(UtilityFunctions uF, boolean isEmpLoanAutoApprove) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
  
			con = db.makeConnection(con);
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			Map<String, String> hmEmpNamMap = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
			String locationID=hmEmpWlocationMap.get(strSessionEmpId);
			
//			if(uF.parseToInt(getStrEmpId())>0) {
//				loanList = new FillLoanCode(request).fillLoanCode(uF.parseToInt(getStrEmpId()), CF);
//			}
			 
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
					"and effective_type='"+WORK_FLOW_LOAN+"' and effective_id in(select loan_applied_id from loan_details ld, loan_applied_details lad where lad.loan_id=ld.loan_id and " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(")) group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,user_type_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
					" and is_approved=0 and effective_type='"+WORK_FLOW_LOAN+"' and effective_id in(select loan_applied_id from loan_details ld, loan_applied_details lad where lad.loan_id=ld.loan_id and " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append("))");
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
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmMemNextApproval.put(rs.getString("effective_id")+"_"+rs.getString("user_type_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_LOAN+"'" +
					" and effective_id in(select loan_applied_id from loan_details ld, loan_applied_details lad where lad.loan_id=ld.loan_id and " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(")) group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("effective_id"))) {
					deniedList.add(rs.getString("effective_id"));
				} 
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select loan_applied_id from loan_details ld, loan_applied_details lad where lad.loan_id=ld.loan_id and " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(") and is_approved=-1");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("loan_applied_id"))) {
					deniedList.add(rs.getString("loan_applied_id"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
					" and effective_type='"+WORK_FLOW_LOAN+"' and effective_id in(select loan_applied_id from loan_details ld, loan_applied_details lad where lad.loan_id=ld.loan_id and " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append("))  group by effective_id,is_approved");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproval = new HashMap<String, String>();			
			while(rs.next()) {
				hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
					" and effective_type='"+WORK_FLOW_LOAN+"' and effective_id in(select loan_applied_id from loan_details ld, loan_applied_details lad where lad.loan_id=ld.loan_id and " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(")) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();	
			Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();
			while(rs.next()) {
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
				" and effective_type='"+WORK_FLOW_LOAN+"' and effective_id in(select loan_applied_id from loan_details ld, loan_applied_details lad where lad.loan_id=ld.loan_id and " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(")) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();	
			while(rs.next()) {
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id,user_type_id from work_flow_details where effective_type='"+WORK_FLOW_LOAN+"'" +
					" and effective_id in(select loan_applied_id from loan_details ld, loan_applied_details lad where lad.loan_id=ld.loan_id and " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append("))");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append(" order by effective_id,member_position");
			pst = con.prepareStatement(sbQuery.toString());
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(1, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(1, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(2, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
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
			
			sbQuery=new StringBuilder();
			sbQuery.append("select * from loan_applied_details where is_approved=1 and loan_applied_id not in (select loan_applied_id from loan_payments) " +
					"and emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id ");
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(")order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();			
			List<String> alNotPaymentLoan = new ArrayList<String>();	
			while(rs.next()) {
				alNotPaymentLoan.add(rs.getString("emp_id")+"_"+rs.getString("loan_applied_id"));
			}
			rs.close();
			pst.close();
//			System.out.println("alNotPaymentLoan==>"+alNotPaymentLoan);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select l.*,wfd.user_type_id as user_type from (select * from loan_details ld, loan_applied_details lad where lad.loan_id=ld.loan_id and " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			if(uF.parseToInt(getStrEmpId())>0) {
				sbQuery.append(" and eod.emp_id="+uF.parseToInt(getStrEmpId()));
			}
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            sbQuery.append(")) l, work_flow_details wfd where l.loan_applied_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_LOAN+"' ");
			if(strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
//					sbQuery.append(" and (wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ) ");
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
			}
			sbQuery.append(" order by l.applied_date desc");
			pst = con.prepareStatement(sbQuery.toString());
			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			List<List<String>> alLoanReport = new ArrayList<List<String>>();	
			List<String> alList = new ArrayList<String>();	
			int count=0;
			while(rs.next()) {
				
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("loan_applied_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("loan_applied_id")+"_"+strSessionEmpId);
				if(checkEmpUserTypeList==null) checkEmpUserTypeList = new ArrayList<String>();
				
				boolean checkGHRInWorkflow = true;
				if(checkEmpUserTypeList.contains(hmUserTypeIdMap.get(HRMANAGER)) && !checkEmpUserTypeList.contains(hmUserTypeIdMap.get(ADMIN)) && strUserType != null && strUserType.equals(ADMIN)) {
					checkGHRInWorkflow = false;
				}
				
//				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(HRMANAGER)) {
				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN)) {
					continue;
				}
				
//				if(strUserType.equalsIgnoreCase(HRMANAGER) && hmEmpByLocation.get(rs.getString("emp_id"))==null) {
//					continue;
//				}
				String userType = rs.getString("user_type");				
				if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && alList.contains(rs.getString("loan_applied_id"))) {
					continue;
				} else if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("loan_applied_id"))) {
					userType = strUserTypeId;
					alList.add(rs.getString("loan_applied_id"));
				} else if(!checkEmpUserTypeList.contains(userType)) {
					continue;
				}
				
				String strCurrency = "";
				if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp_id"))) > 0) {
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp_id")));
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				} 
				
				List<String> alLoanInner = new ArrayList<String>();
				alLoanInner.add(rs.getString("loan_applied_id"));
				alLoanInner.add(uF.showData(hmEmpNamMap.get(rs.getString("emp_id")),""));
				alLoanInner.add(rs.getString("loan_code"));
				alLoanInner.add(uF.showData(uF.getDateFormat(rs.getString("applied_date"), DBDATE, CF.getStrReportDateFormat()), ""));
				alLoanInner.add(uF.showData(hmEmpNamMap.get(rs.getString("approved_by")),""));
				alLoanInner.add(uF.showData(uF.getDateFormat(rs.getString("approved_date"), DBDATE, CF.getStrReportDateFormat()), ""));
				alLoanInner.add(rs.getString("duration_months")+" months");
				alLoanInner.add(uF.showData(rs.getString("loan_acc_no"), ""));
				alLoanInner.add(strCurrency+ uF.formatIntoComma(uF.parseToDouble(rs.getString("amount_paid"))));
				
				double dblAmount = uF.getEMI(uF.parseToDouble(rs.getString("amount_paid")), uF.parseToDouble(rs.getString("loan_interest")), uF.parseToInt(rs.getString("duration_months")));
				
				if(rs.getString("balance_amount")!=null) {
					alLoanInner.add(strCurrency+uF.formatIntoComma(uF.parseToDouble(rs.getString("balance_amount"))));
				} else {
					alLoanInner.add("-"); 
				}
				alLoanInner.add(strCurrency+uF.formatIntoTwoDecimal(dblAmount / uF.parseToInt(rs.getString("duration_months"))));
				
				if(deniedList.contains(rs.getString("loan_applied_id"))) {
					 /*alLoanInner.add("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\"/>");*/
					alLoanInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
				} else if(rs.getInt("is_approved")==1) {							
					/*alLoanInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					alLoanInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
				} else if(uF.parseToInt(hmAnyOneApproval.get(rs.getString("loan_applied_id")))==1 && uF.parseToInt(hmAnyOneApproval.get(rs.getString("loan_applied_id")))==rs.getInt("is_approved")) {
					/*alLoanInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					alLoanInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("loan_applied_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("loan_applied_id")+"_"+userType)) && uF.parseToInt(hmNextApproval.get(rs.getString("loan_applied_id")))>0) {
					alLoanInner.add("<a href=\"javascript:void(0);\" onclick=\"approveDeny('"+rs.getString("loan_applied_id")+"','1','"+userType+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a> " +
							" <a href=\"javascript:void(0);\" onclick=\"approveDeny('"+rs.getString("loan_applied_id")+"','-1','"+userType+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a> ");
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("loan_applied_id")))<uF.parseToInt(hmMemNextApproval.get(rs.getString("loan_applied_id")+"_"+userType)) || (uF.parseToInt(hmNextApproval.get(rs.getString("loan_applied_id")))==0 && uF.parseToInt(hmNextApproval.get(rs.getString("loan_applied_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("loan_applied_id")+"_"+userType)))) {
					if(rs.getInt("is_approved")==0) {
						if(strUserType.equalsIgnoreCase(ADMIN) && uF.parseToInt(strUserTypeId) == uF.parseToInt(userType)) {
							alLoanInner.add("<a href=\"javascript:void(0);\" onclick=\"approveDeny('"+rs.getString("loan_applied_id")+"','1','"+userType+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a> " +
									" <a href=\"javascript:void(0);\" onclick=\"approveDeny('"+rs.getString("loan_applied_id")+"','-1','"+userType+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a> ");
						} else {
							/*alLoanInner.add("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
							alLoanInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Waiting for workflow\"></i>");
							
							if(!checkGHRInWorkflow) {
								alLoanInner.add("&nbsp;|&nbsp;&nbsp;<a href=\"javascript:void(0);\" onclick=\"approveDeny('"+rs.getString("loan_applied_id")+"','1','');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved ("+ADMIN+")\"></i></a> " +
									" <a href=\"javascript:void(0);\" onclick=\"approveDeny('"+rs.getString("loan_applied_id")+"','-1','');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied ("+ADMIN+")\"></i></a> ");
							}
						}
					} else if(rs.getInt("is_approved")==1) {							
						/*alLoanInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
						alLoanInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					} else {
						/*alLoanInner.add("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
						alLoanInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
					}
				} else {
					if(strUserType.equalsIgnoreCase(ADMIN)) {
						alLoanInner.add("<a href=\"javascript:void(0);\" onclick=\"approveDeny('"+rs.getString("loan_applied_id")+"','1','"+userType+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a> " +
								" <a href=\"javascript:void(0);\" onclick=\"approveDeny('"+rs.getString("loan_applied_id")+"','-1','"+userType+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a> ");
					} else {
						/*alLoanInner.add("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
						alLoanInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Waiting for workflow\" style=\"color:#f7ee1d\"></i>");
						
					}
				}
				
				StringBuilder sb = new StringBuilder();
				boolean flagStatus = false;
				if(rs.getInt("is_approved")==1 && !uF.parseToBoolean(rs.getString("is_paid")) && uF.parseToInt(rs.getString("approved_by"))>0 && strUserType!=null && !strUserType.equals(EMPLOYEE)  && !strUserType.equals(CONSULTANT)  && !strUserType.equals(ARTICLE)  && !strUserType.equals(MANAGER)) { 
					sb.append("<a href=\"javascript:none(0)\" onclick=\"payLoan("+rs.getString("loan_applied_id")+")\">Pay</a> ");
					flagStatus = true;
				} else {

					if(rs.getInt("is_approved")==0) {
						sb.append("");
						flagStatus = true;
					} else if(rs.getInt("is_approved")==1 && uF.parseToBoolean(rs.getString("is_completed"))) {
						sb.append("<a href=\"javascript:none(0)\" onclick=\"viewLoanDetails("+rs.getString("loan_applied_id")+")\">View Details</a>");
						flagStatus = true;
					} else if(rs.getInt("is_approved")==1 && !uF.parseToBoolean(rs.getString("is_completed")) && strUserType!=null && !strUserType.equals(EMPLOYEE)  && !strUserType.equals(CONSULTANT)  && !strUserType.equals(ARTICLE)  && !strUserType.equals(MANAGER)) {
						sb.append("<a href=\"javascript:none(0)\" onclick=\"viewLoanDetails("+rs.getString("loan_applied_id")+")\">View Details</a> | <a href=\"javascript:void();\" onclick=\"viewPayments("+rs.getString("emp_id")+","+rs.getString("loan_id")+","+rs.getString("loan_applied_id")+");\">Payments</a>");
						flagStatus = true;
					} else if(rs.getInt("is_approved")==1 && !uF.parseToBoolean(rs.getString("is_completed"))) {
						sb.append("<a href=\"javascript:none(0)\" onclick=\"viewLoanDetails("+rs.getString("loan_applied_id")+")\">View Details</a>");
						flagStatus = true;
					} else {
						sb.append("");
						flagStatus = true;
					}
				}
				if(rs.getInt("is_approved")==1 && alNotPaymentLoan.contains(rs.getString("emp_id")+"_"+rs.getString("loan_applied_id"))){
					if(flagStatus){
						sb.append(" | ");
					}
					String strCancelDivId = "cancelId_"+rs.getString("loan_applied_id");
					sb.append("<div style=\"float: right\" id=\""+strCancelDivId+"\"><a href=\"javascript:none(0)\" onclick=\"cancelLoan('"+strCancelDivId+"','"+rs.getString("loan_applied_id")+"','"+rs.getString("emp_id")+"')\">Cancel</a></div>");
				}
				alLoanInner.add(sb.toString());
				
				if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("loan_applied_id"))!=null) {
//					String approvedby=hmAnyOneApproeBy.get(rs.getString("loan_applied_id"));
//					String strUserTypeName = uF.parseToInt(hmWorkFlowUserTypeId.get(rs.getString("loan_applied_id"))) > 0 ? " ("+uF.showData(hmUserTypeMap.get(hmWorkFlowUserTypeId.get(rs.getString("loan_applied_id"))), "")+")" : "";
//					alLoanInner.add(hmEmpNamMap.get(approvedby)+strUserTypeName);
					alLoanInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("loan_applied_id")+"','"+hmEmpNamMap.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("loan_applied_id"))!=null) {
					alLoanInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("loan_applied_id")+"','"+hmEmpNamMap.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else {
					alLoanInner.add("");
				}
				alLoanInner.add(uF.showData(hmUserTypeMap.get(userType), ""));
				alLoanInner.add(uF.showData(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()), ""));
				alLoanReport.add(alLoanInner);
				
				count++;
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from loan_details ld, loan_applied_details lad where lad.loan_id=ld.loan_id " +
					" and lad.is_approved=1 and lad.emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			if(uF.parseToInt(getStrEmpId())>0) {
				sbQuery.append(" and eod.emp_id="+uF.parseToInt(getStrEmpId()));
			}
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            sbQuery.append(") and lad.loan_applied_id not in (select effective_id from work_flow_details where effective_type = '"+WORK_FLOW_LOAN+"')");
			sbQuery.append(" order by lad.applied_date desc");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				String strCurrency = "";
				if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp_id"))) > 0) {
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp_id")));
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				} 
				
				List<String> alLoanInner = new ArrayList<String>();
				alLoanInner.add(rs.getString("loan_applied_id"));
				alLoanInner.add(uF.showData(hmEmpNamMap.get(rs.getString("emp_id")),""));
				alLoanInner.add(rs.getString("loan_code"));
				alLoanInner.add(uF.showData(uF.getDateFormat(rs.getString("applied_date"), DBDATE, CF.getStrReportDateFormat()), ""));
				alLoanInner.add(uF.showData(hmEmpNamMap.get(rs.getString("approved_by")),""));
				alLoanInner.add(uF.showData(uF.getDateFormat(rs.getString("approved_date"), DBDATE, CF.getStrReportDateFormat()), ""));
				alLoanInner.add(rs.getString("duration_months")+" months");
				alLoanInner.add(uF.showData(rs.getString("loan_acc_no"), ""));
				alLoanInner.add(strCurrency+ uF.formatIntoComma(uF.parseToDouble(rs.getString("amount_paid"))));
				
				double dblAmount = uF.getEMI(uF.parseToDouble(rs.getString("amount_paid")), uF.parseToDouble(rs.getString("loan_interest")), uF.parseToInt(rs.getString("duration_months")));
				
				if(rs.getString("balance_amount")!=null) {
					alLoanInner.add(strCurrency+uF.formatIntoComma(uF.parseToDouble(rs.getString("balance_amount"))));
				} else {
					alLoanInner.add("-"); 
				}
				alLoanInner.add(strCurrency+uF.formatIntoTwoDecimal(dblAmount / uF.parseToInt(rs.getString("duration_months"))));
				
				if(rs.getInt("is_approved")==1) {							
					alLoanInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
				} else {
					alLoanInner.add("");
				}
				
				StringBuilder sb = new StringBuilder();
				boolean flagStatus = false;
				if(rs.getInt("is_approved")==1 && !uF.parseToBoolean(rs.getString("is_paid")) && uF.parseToInt(rs.getString("approved_by"))>0 && strUserType!=null && !strUserType.equals(EMPLOYEE)  && !strUserType.equals(CONSULTANT)  && !strUserType.equals(ARTICLE)  && !strUserType.equals(MANAGER)) { 
					sb.append("<a href=\"javascript:none(0)\" onclick=\"payLoan("+rs.getString("loan_applied_id")+")\">Pay</a> ");
					flagStatus = true;
				} else {

					if(rs.getInt("is_approved")==0) {
						sb.append("");
						flagStatus = true;
					} else if(rs.getInt("is_approved")==1 && uF.parseToBoolean(rs.getString("is_completed"))) {
						sb.append("<a href=\"javascript:none(0)\" onclick=\"viewLoanDetails("+rs.getString("loan_applied_id")+")\">View Details</a>");
						flagStatus = true;
					} else if(rs.getInt("is_approved")==1 && !uF.parseToBoolean(rs.getString("is_completed")) && strUserType!=null && !strUserType.equals(EMPLOYEE)  && !strUserType.equals(CONSULTANT)  && !strUserType.equals(ARTICLE)  && !strUserType.equals(MANAGER)) {
						sb.append("<a href=\"javascript:none(0)\" onclick=\"viewLoanDetails("+rs.getString("loan_applied_id")+")\">View Details</a> | <a href=\"javascript:void();\" onclick=\"viewPayments("+rs.getString("emp_id")+","+rs.getString("loan_id")+","+rs.getString("loan_applied_id")+");\">Payments</a>");
						flagStatus = true;
					} else if(rs.getInt("is_approved")==1 && !uF.parseToBoolean(rs.getString("is_completed"))) {
						sb.append("<a href=\"javascript:none(0)\" onclick=\"viewLoanDetails("+rs.getString("loan_applied_id")+")\">View Details</a>");
						flagStatus = true;
					} else {
						sb.append("");
						flagStatus = true;
					}
				}
				if(rs.getInt("is_approved")==1 && alNotPaymentLoan.contains(rs.getString("emp_id")+"_"+rs.getString("loan_applied_id"))){
					if(flagStatus){
						sb.append(" | ");
					}
					String strCancelDivId = "cancelId_"+rs.getString("loan_applied_id");
					sb.append("<div style=\"float: right\" id=\""+strCancelDivId+"\"><a href=\"javascript:none(0)\" onclick=\"cancelLoan('"+strCancelDivId+"','"+rs.getString("loan_applied_id")+"','"+rs.getString("emp_id")+"')\">Cancel</a></div>");
				}
				alLoanInner.add(sb.toString());
				
				alLoanInner.add("<a href=\"javascript:void(0)\" onclick=\"alert('This loan is system approved.');\" style=\"margin-left: 10px;\">View</a>");
				alLoanInner.add("");
				alLoanInner.add(uF.showData(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()), ""));
				
				
				alLoanReport.add(alLoanInner);
				
				count++;
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("alLoanReport", alLoanReport);
			
		} catch (Exception e) {
			e.printStackTrace(); 
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public String loadEmployeeLoan(UtilityFunctions uF) {
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
//			loanList = new FillLoanCode(request).fillLoanCode(uF.parseToInt(getStrEmpId()),CF);
			departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
			levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
				wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATION_ACCESS));
				organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			} else {
				organisationList = new FillOrganisation(request).fillOrganisation();
				wLocationList = new FillWLocation(request).fillWLocation(getF_org());
			}
			
			empList=getEmployeeList(uF);
		}
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if((strUserType != null && !strUserType.equals(MANAGER)) || (getCurrUserType() != null && getCurrUserType().equals(strBaseUserType))) {
			alFilter.add("ORGANISATION");
			if(getF_org()!=null) {
				String strOrg="";
				for(int i=0;organisationList!=null && i<organisationList.size();i++) {
					if(getF_org().equals(organisationList.get(i).getOrgId())) {
						strOrg=organisationList.get(i).getOrgName();
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
			if(getF_strWLocation()!=null) {
				String strLocation="";
				for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
					if(getF_strWLocation().equals(wLocationList.get(i).getwLocationId())) {
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
			
			
			alFilter.add("DEPARTMENT");
			if(getF_department()!=null) {
				String strDepart = "";
				for(int i=0; departmentList!=null && i<departmentList.size();i++) {
					if(getF_department().equals(departmentList.get(i).getDeptId())) {
						strDepart=departmentList.get(i).getDeptName();
					}
				}
				if(strDepart!=null && !strDepart.equals("")) {
					hmFilter.put("DEPARTMENT", strDepart);
				} else {
					hmFilter.put("DEPARTMENT", "All Departments");
				}
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
			
			
			alFilter.add("LEVEL");
			if(getF_level()!=null) {
				String strLevel="";
				for(int i=0; levelList!=null && i<levelList.size();i++) {
					if(getF_level().equals(levelList.get(i).getLevelId())) {
						strLevel=levelList.get(i).getLevelCodeName();
					}
				}
				if(strLevel!=null && !strLevel.equals("")) {
					hmFilter.put("LEVEL", strLevel);
				} else {
					hmFilter.put("LEVEL", "All Levels");
				}
			} else {
				hmFilter.put("LEVEL", "All Levels");
			}
			
			alFilter.add("EMP");
			if(getStrEmpId()!=null) {
				String strEmpName="";
				for(int i=0;empList!=null && i<empList.size();i++) {
					if(getStrEmpId().equals(empList.get(i).getEmployeeId())) {
						strEmpName = empList.get(i).getEmployeeCode();
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

		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	
	private List<FillEmployee> getEmployeeList(UtilityFunctions uF) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and is_alive=true ");
			
			if(uF.parseToInt(getF_department())>0) {
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			
			if(getF_strWLocation()!=null && getF_strWLocation().length()>0) {
                sbQuery.append(" and eod.wlocation_id ="+uF.parseToInt(getF_strWLocation()) );
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	            
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				
				String strEmpMName = "";
				
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
			
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	public String viewEmployeeLoan(UtilityFunctions uF, boolean isEmpLoanAutoApprove) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
  
			con = db.makeConnection(con);
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmEmpNamMap = CF.getEmpNameMap(con, null, null);
			
//			if(uF.parseToInt(getStrEmpId())>0) {
//				loanList = new FillLoanCode(request).fillLoanCode(uF.parseToInt(getStrEmpId()), CF);
//			}
			 
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
					" and effective_type='"+WORK_FLOW_LOAN+"' and effective_id in(select loan_applied_id from loan_details ld, loan_applied_details lad where lad.loan_id=ld.loan_id and " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			if(uF.parseToInt(getStrEmpId())>0) {
				sbQuery.append(" and eod.emp_id="+uF.parseToInt(getStrEmpId()));
			}
			sbQuery.append(")) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();	
			Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();
			while(rs.next()) {
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
				" and effective_type='"+WORK_FLOW_LOAN+"' and effective_id in(select loan_applied_id from loan_details ld, loan_applied_details lad where lad.loan_id=ld.loan_id and " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			if(uF.parseToInt(getStrEmpId())>0) {
				sbQuery.append(" and eod.emp_id="+uF.parseToInt(getStrEmpId()));
			}
			sbQuery.append(")) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();	
			while(rs.next()) {
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from loan_details ld, loan_applied_details lad where lad.loan_id=ld.loan_id and " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			if(uF.parseToInt(getStrEmpId())>0) {
				sbQuery.append(" and eod.emp_id="+uF.parseToInt(getStrEmpId()));
			}
			sbQuery.append(") and lad.loan_applied_id in (select effective_id from work_flow_details where effective_type = '"+WORK_FLOW_LOAN+"')");
			sbQuery.append(" order by applied_date desc ");
			pst = con.prepareStatement(sbQuery.toString());
			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			List<List<String>> alLoanReport = new ArrayList<List<String>>();
			int count=0;
			while(rs.next()) {				
				String strCurrency = "";
				if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp_id"))) > 0) {
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp_id")));
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				} 
				
				List<String> alLoanInner = new ArrayList<String>();
				alLoanInner.add(rs.getString("loan_applied_id"));
				alLoanInner.add(uF.showData(hmEmpNamMap.get(rs.getString("emp_id")),""));
				alLoanInner.add(rs.getString("loan_code"));
				alLoanInner.add(uF.showData(uF.getDateFormat(rs.getString("applied_date"), DBDATE, CF.getStrReportDateFormat()), ""));
				alLoanInner.add(uF.showData(hmEmpNamMap.get(rs.getString("approved_by")),""));
				alLoanInner.add(uF.showData(uF.getDateFormat(rs.getString("approved_date"), DBDATE, CF.getStrReportDateFormat()), ""));
				alLoanInner.add(rs.getString("duration_months")+" months");
				alLoanInner.add(uF.showData(rs.getString("loan_acc_no"), ""));
				alLoanInner.add(strCurrency+ uF.formatIntoComma(uF.parseToDouble(rs.getString("amount_paid"))));
				
				
				double dblAmount = uF.getEMI(uF.parseToDouble(rs.getString("amount_paid")), uF.parseToDouble(rs.getString("loan_interest")), uF.parseToInt(rs.getString("duration_months")));
				
				if(rs.getString("balance_amount")!=null) {
					alLoanInner.add(strCurrency+uF.formatIntoComma(uF.parseToDouble(rs.getString("balance_amount"))));
				} else {
					alLoanInner.add("-"); 
				}
				alLoanInner.add(strCurrency+uF.formatIntoTwoDecimal(dblAmount / uF.parseToInt(rs.getString("duration_months"))));
				alLoanInner.add(getStatus(rs.getInt("is_approved")));	
				
				if(rs.getInt("is_approved")==1 && !uF.parseToBoolean(rs.getString("is_paid")) && uF.parseToInt(rs.getString("approved_by"))>0 && strUserType!=null && !strUserType.equals(EMPLOYEE)  && !strUserType.equals(CONSULTANT)  && !strUserType.equals(ARTICLE)  && !strUserType.equals(MANAGER)) { 
					alLoanInner.add("<a href=\"javascript:none(0)\" onclick=\"payLoan("+rs.getString("loan_applied_id")+")\">Pay</a> ");
				} else {

					if(rs.getInt("is_approved")==0) {
						alLoanInner.add("");
					} else if(rs.getInt("is_approved")==1 && uF.parseToBoolean(rs.getString("is_completed"))) {
						alLoanInner.add("<a href=\"javascript:none(0)\" onclick=\"viewLoanDetails("+rs.getString("loan_applied_id")+")\">View Details</a>");
					} else if(rs.getInt("is_approved")==1 && !uF.parseToBoolean(rs.getString("is_completed")) && strUserType!=null && !strUserType.equals(EMPLOYEE)  && !strUserType.equals(CONSULTANT)  && !strUserType.equals(ARTICLE)  && !strUserType.equals(MANAGER)) {
						alLoanInner.add("<a href=\"javascript:none(0)\" onclick=\"viewLoanDetails("+rs.getString("loan_applied_id")+")\">View Details</a> | <a href=\"javascript:void();\" onclick=\"viewPayments("+rs.getString("emp_id")+","+rs.getString("loan_id")+","+rs.getString("loan_applied_id")+");\">Payments</a>");
					} else if(rs.getInt("is_approved")==1 && !uF.parseToBoolean(rs.getString("is_completed"))) {
						alLoanInner.add("<a href=\"javascript:none(0)\" onclick=\"viewLoanDetails("+rs.getString("loan_applied_id")+")\">View Details</a>");
					} else {
						alLoanInner.add("");
					}
				}
				
				if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("loan_applied_id"))!=null) {
//					String approvedby=hmAnyOneApproeBy.get(rs.getString("loan_applied_id"));
//					String strUserTypeName = uF.parseToInt(hmWorkFlowUserTypeId.get(rs.getString("loan_applied_id"))) > 0 ? " ("+uF.showData(hmUserTypeMap.get(hmWorkFlowUserTypeId.get(rs.getString("loan_applied_id"))), "")+")" : "";
//					alLoanInner.add(hmEmpNamMap.get(approvedby)+strUserTypeName);
					alLoanInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("loan_applied_id")+"','"+hmEmpNamMap.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("loan_applied_id"))!=null) {
					alLoanInner.add("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("loan_applied_id")+"','"+hmEmpNamMap.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
				} else {
					alLoanInner.add("");
				}
				alLoanInner.add("");
				alLoanInner.add(uF.showData(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()), ""));
				
				alLoanReport.add(alLoanInner);
				count++;
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from loan_details ld, loan_applied_details lad where lad.loan_id=ld.loan_id and " +
					"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id ");
			if(uF.parseToInt(getStrEmpId())>0) {
				sbQuery.append(" and eod.emp_id="+uF.parseToInt(getStrEmpId()));
			}
			sbQuery.append(") and lad.loan_applied_id not in (select effective_id from work_flow_details where effective_type = '"+WORK_FLOW_LOAN+"')");
			sbQuery.append(" order by applied_date desc ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {				
				String strCurrency = "";
				if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp_id"))) > 0) {
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp_id")));
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				} 
				
				List<String> alLoanInner = new ArrayList<String>();
				alLoanInner.add(rs.getString("loan_applied_id"));
				alLoanInner.add(uF.showData(hmEmpNamMap.get(rs.getString("emp_id")),""));
				alLoanInner.add(rs.getString("loan_code"));
				alLoanInner.add(uF.showData(uF.getDateFormat(rs.getString("applied_date"), DBDATE, CF.getStrReportDateFormat()), ""));
				alLoanInner.add(uF.showData(hmEmpNamMap.get(rs.getString("approved_by")),""));
				alLoanInner.add(uF.showData(uF.getDateFormat(rs.getString("approved_date"), DBDATE, CF.getStrReportDateFormat()), ""));
				alLoanInner.add(rs.getString("duration_months")+" months");
				alLoanInner.add(uF.showData(rs.getString("loan_acc_no"), ""));
				alLoanInner.add(strCurrency+ uF.formatIntoComma(uF.parseToDouble(rs.getString("amount_paid"))));
				
				
				double dblAmount = uF.getEMI(uF.parseToDouble(rs.getString("amount_paid")), uF.parseToDouble(rs.getString("loan_interest")), uF.parseToInt(rs.getString("duration_months")));
				
				if(rs.getString("balance_amount")!=null) {
					alLoanInner.add(strCurrency+uF.formatIntoComma(uF.parseToDouble(rs.getString("balance_amount"))));
				} else {
					alLoanInner.add("-"); 
				}
				alLoanInner.add(strCurrency+uF.formatIntoTwoDecimal(dblAmount / uF.parseToInt(rs.getString("duration_months"))));
				alLoanInner.add(getStatus(rs.getInt("is_approved")));	
				
				if(rs.getInt("is_approved")==1 && !uF.parseToBoolean(rs.getString("is_paid")) && uF.parseToInt(rs.getString("approved_by"))>0 && strUserType!=null && !strUserType.equals(EMPLOYEE)  && !strUserType.equals(CONSULTANT)  && !strUserType.equals(ARTICLE)  && !strUserType.equals(MANAGER)) { 
					alLoanInner.add("<a href=\"javascript:none(0)\" onclick=\"payLoan("+rs.getString("loan_applied_id")+")\">Pay</a> ");
				} else {
					if(rs.getInt("is_approved")==0) {
						alLoanInner.add("");
					} else if(rs.getInt("is_approved")==1 && uF.parseToBoolean(rs.getString("is_completed"))) {
						alLoanInner.add("<a href=\"javascript:none(0)\" onclick=\"viewLoanDetails("+rs.getString("loan_applied_id")+")\">View Details</a>");
					} else if(rs.getInt("is_approved")==1 && !uF.parseToBoolean(rs.getString("is_completed")) && strUserType!=null && !strUserType.equals(EMPLOYEE)  && !strUserType.equals(CONSULTANT)  && !strUserType.equals(ARTICLE)  && !strUserType.equals(MANAGER)) {
						alLoanInner.add("<a href=\"javascript:none(0)\" onclick=\"viewLoanDetails("+rs.getString("loan_applied_id")+")\">View Details</a> | <a href=\"javascript:void();\" onclick=\"viewPayments("+rs.getString("emp_id")+","+rs.getString("loan_id")+","+rs.getString("loan_applied_id")+");\">Payments</a>");
					} else if(rs.getInt("is_approved")==1 && !uF.parseToBoolean(rs.getString("is_completed"))) {
						alLoanInner.add("<a href=\"javascript:none(0)\" onclick=\"viewLoanDetails("+rs.getString("loan_applied_id")+")\">View Details</a>");
					} else {
						alLoanInner.add("");
					}
				}
				
				alLoanInner.add("<a href=\"javascript:void(0)\" onclick=\"alert('This loan is system approved.');\" style=\"margin-left: 10px;\">View</a>");
				alLoanInner.add("");
				alLoanInner.add(uF.showData(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()), ""));
				
				alLoanReport.add(alLoanInner);
				count++;
			}
			rs.close();
			pst.close();			
			
			request.setAttribute("alLoanReport", alLoanReport);
			
		} catch (Exception e) {
			e.printStackTrace(); 
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	private String getStatus(int status) {
		String strStatus = null;
		switch(status) {
			case 0:
				/*strStatus = "<img src=\"images1/icons/pending.png\" border=\"0\" title=\"Pending\" >";*/
				strStatus = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Pending\"></i>";
				break;
			case 1:
				/*strStatus = "<img src=\"images1/icons/approved.png\" border=\"0\" title=\"Approved\" >";*/
				strStatus = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"  title=\"Approved\"></i>";
				break;
			case -1:
				/*strStatus = "<img src=\"images1/icons/denied.png\" border=\"0\" title=\"Denied\" >";*/
				strStatus = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>";
				break;
			case -2:
				/*strStatus = "<img src=\"images1/icons/pullout.png\" border=\"0\" title=\"Pulled out\" >";*/
				strStatus = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" title=\"Pulled out\"></i>";
				break;
		}
		return strStatus;
	} 
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String getF_department() {
		return f_department;
	}

	public void setF_department(String f_department) {
		this.f_department = f_department;
	}

	public String getF_level() {
		return f_level;
	}

	public void setF_level(String f_level) {
		this.f_level = f_level;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public String getStrLoanCode() {
		return strLoanCode;
	}

	public void setStrLoanCode(String strLoanCode) {
		this.strLoanCode = strLoanCode;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
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

}