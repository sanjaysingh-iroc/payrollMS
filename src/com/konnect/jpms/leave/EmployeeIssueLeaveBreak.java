package com.konnect.jpms.leave;

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

import com.konnect.jpms.select.FillApproval;
import com.konnect.jpms.select.FillBreakType;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillUserType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;


public class EmployeeIssueLeaveBreak extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	CommonFunctions CF;
	HttpSession session;
	String orgId; 
	String strLocation;
	
	String policy;
	
	public String execute() throws Exception {
 
		request.setAttribute(PAGE, PEmployeeIssueLeave);
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		  
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		
//		viewWorkFlowPolicyReport();
		
		
		if(getAccrualSystem()==null){
			setAccrualSystem("1");
		}
		
		if(getAccrualFrom()==null){
			setAccrualFrom("1");
		}
		
		if (operation!=null && operation.equals("D")) {
			return deleteEmployeeIssueLeave(strId);
		}
		
		if (operation!=null && operation.equals("E")) {
			return viewEmployeeIssueLeave(strId);
		}
		
		if (getEmpBreakTypeId()!=null && getEmpBreakTypeId().length()>0) {
				return updateEmployeeIssueLeave();
		}
		if (getLevelType()!=null && getLevelType().length()>0) {
				return insertEmployeeIssueLeave();
		}
		
		return loadEmployeeIssueLeave();
		
	}
	private void viewWorkFlowPolicyReport() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		Map<String,String> hmOrg=getOrganization();
		Map<String,String> hmLocationName=getLocationName();
		
		
		try {

			List<List<String>> reportList=new ArrayList<List<String>>();		
							
			con = db.makeConnection(con);
			
			StringBuilder sbQuery=new StringBuilder();
			
			
			if(uF.parseToInt(getOrgId())>0 && uF.parseToInt(getStrLocation())>0){
				sbQuery.append("select * from (select max(work_flow_policy_id)as work_flow_policy_id,policy_count " +
				" from work_flow_policy where trial_status=1 group by policy_count)as a ,work_flow_policy b where a.work_flow_policy_id=b.work_flow_policy_id");
				sbQuery.append(" and org_id='"+getOrgId().trim()+"' ");
				sbQuery.append(" and location_id='"+getStrLocation().trim()+"'  ");
				
			}
			
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery(); 
			
			while(rs.next()){
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("work_flow_policy_id"));
				alInner.add(rs.getString("work_flow_member_id"));
				alInner.add(rs.getString("member_position"));
				alInner.add(rs.getString("policy_type"));
				alInner.add(rs.getString("trial_status"));
				alInner.add(rs.getString("added_by"));
				alInner.add(rs.getString("added_date")!=null?uF.getDateFormat(rs.getString("added_date"), DBDATE, DATE_FORMAT):"-");
				alInner.add(rs.getString("policy_count"));
				
				alInner.add(rs.getString("policy_name"));
				alInner.add(rs.getString("effective_date")!=null?uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT):"-");
				alInner.add(rs.getString("org_id")!=null ? hmOrg.get(rs.getString("org_id").trim()) : "");
				alInner.add(rs.getString("location_id")!=null ? hmLocationName.get(rs.getString("location_id").trim()) : "");
				
				alInner.add(rs.getString("policy_status"));
				
				reportList.add(alInner);
				
			}
			rs.close();
			pst.close();
			request.setAttribute("reportList", reportList);
			
			
			
		} catch (Exception e) {
			e.printStackTrace(); 
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	public String loadValidateEmployeeIssueLeave() {
		
		UtilityFunctions uF = new UtilityFunctions();
		
		
		//request.setAttribute(PAGE, PEmployeeIssueLeave);
		//request.setAttribute(TITLE, TAddEmployeeIssueLeave);
		userTypeList = new FillUserType(request).fillUserType();
		levelTypeList = new FillLevel(request).fillLevel(uF.parseToInt(getOrgId()));
		empBreakTypeList = new FillBreakType(request).fillBreaks(uF.parseToInt(getOrgId()));
		approvalList = new FillApproval().fillLeaveStartDate();
		return LOAD;
	}

	public String loadEmployeeIssueLeave() {
		setEmpBreakTypeId("");
		setEmployeType("");
		setTypeOfbreak("");
		setNoOfLeave("");
		return LOAD;
	}

	public String insertEmployeeIssueLeave() {

//		System.out.println("inside insert..");
		Connection con = null;
		PreparedStatement pst = null, pst1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];
			
//			Map<String, String> hmEmpJoiningDateMap = CF.getEmpJoiningDateMap();			
			
			
			con = db.makeConnection(con);
			pst=con.prepareStatement(insertLeaveBreakType);
//			pst.setInt(1, uF.parseToInt(getEmployeType())); policy_id
			pst.setInt(1, uF.parseToInt(getLevelType()));
			pst.setInt(2, uF.parseToInt(getTypeOfbreak()));
			pst.setInt(3, uF.parseToInt(getNoOfLeave()));
			pst.setBoolean(4, getIspaid());
			pst.setBoolean(5, getIsCarryForward());
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			if(getApprovalDate()!=null && getApprovalDate().contains("CY")){
				pst.setDate(7, uF.getDateFormat("01/01/"+uF.getYear()+"",DATE_FORMAT));
			}else if(getApprovalDate()!=null && getApprovalDate().contains("FY")){
				pst.setDate(7, uF.getDateFormat(strFinancialYearStart+"",DATE_FORMAT));
			}else {
				pst.setDate(7, uF.getDateFormat("01/01/"+uF.getYear()+"", DATE_FORMAT));
			}
			pst.setInt(8, uF.parseToInt((String)session.getAttribute(EMPID)));
			
			pst.setInt(9, uF.parseToInt(getMonthlyLeaveLimit()));
			
			pst.setBoolean(10, getIsMonthlyCarryForward());
			pst.setBoolean(11, getIsApproval());
			pst.setInt(12, uF.parseToInt(getOrgId()));
			pst.setInt(13, uF.parseToInt(getPolicy()));
			pst.setDouble(14, uF.parseToDouble(getNoOfLeaveMonthly()));
			pst.setInt(15, uF.parseToInt(getAccrualSystem()));
			pst.setInt(16, uF.parseToInt(getAccrualFrom()));
			pst.setInt(17, uF.parseToInt(getStrLocation()));
			
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Break policy saved successfully."+END);
//			CF.updateLeaveRegister(getLevelType(), getNoOfLeave() , getApprovalDate(),  getTypeOfLeave(), getIsCarryForward(), CF);
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in insertion");
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
//		System.out.println("returning success after insert");
		return SUCCESS;

	}

	public String viewEmployeeIssueLeave(String strId) {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from emp_leave_break_type where emp_break_type_id =?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			String policy_id="";
			while(rs.next()){
				
				setLevelType(rs.getString("level_id"));
				setTypeOfbreak(rs.getString("break_type_id"));
				setNoOfLeave(rs.getString("no_of_leave"));
				setIsCarryForward(uF.parseToBoolean(rs.getString("is_carryforward")));
				setIspaid(uF.parseToBoolean(rs.getString("is_paid")));
				setEmpBreakTypeId(rs.getString("emp_break_type_id"));
				setIsMonthlyCarryForward(uF.parseToBoolean(rs.getString("is_monthly_carryforward")));
				
				setMonthlyLeaveLimit(rs.getString("monthly_limit"));
				setIsApproval(uF.parseToBoolean(rs.getString("is_approval")));
				setOrgId(rs.getString("org_id"));
				
				setNoOfLeaveMonthly(rs.getString("no_of_break_monthly"));
				setAccrualSystem(rs.getString("accrual_system"));
				setAccrualFrom(rs.getString("accrual_from"));
			}
			rs.close();
			pst.close();			
			
			request.setAttribute("policy_id", policy_id);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
	}
	
	public String updateEmployeeIssueLeave() {

		Connection con = null;
		PreparedStatement pst =null,pst1 =null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		
		String updateEmpLeaveType = "UPDATE emp_leave_break_type SET level_id=?, break_type_id=?, no_of_leave=?, is_paid=?, is_carryforward=? , " +
				"entrydate = ?, effective_date=?, user_id=?, monthly_limit=?, " +
				"is_monthly_carryforward=?, is_approval=?,org_id=?,policy_id=?, no_of_break_monthly=?, accrual_system=?, accrual_from=?  WHERE emp_break_type_id=?";
		
		try {
		   
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];
			
			con = db.makeConnection(con);
			
			
//			Map<String, String> hmEmpJoiningDateMap = CF.getEmpJoiningDateMap(con, uF);
			
			
			pst = con.prepareStatement(updateEmpLeaveType);
			pst.setInt(1, uF.parseToInt(getLevelType()));
			pst.setInt(2, uF.parseToInt(getTypeOfbreak()));
			pst.setInt(3, uF.parseToInt(getNoOfLeave()));
			pst.setBoolean(4, getIspaid());
			pst.setBoolean(5, getIsCarryForward());
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			
			if(getApprovalDate()!=null && getApprovalDate().contains("CY")){
				pst.setDate(7, uF.getDateFormat("01/01/"+uF.getYear()+"",DATE_FORMAT));
			}else if(getApprovalDate()!=null && getApprovalDate().contains("FY")){
				pst.setDate(7, uF.getDateFormat(strFinancialYearStart+"",DATE_FORMAT));
			}else {
				pst.setDate(7, uF.getDateFormat("01/01/"+uF.getYear()+"", DATE_FORMAT));
			}
			
			pst.setInt(8, uF.parseToInt((String)session.getAttribute(EMPID)));
			
			
			pst.setInt(9, uF.parseToInt(getMonthlyLeaveLimit()));
			
			pst.setBoolean(10, getIsMonthlyCarryForward());
			pst.setBoolean(11, getIsApproval());
			pst.setInt(12, uF.parseToInt(getOrgId()));
			pst.setInt(13, uF.parseToInt(getPolicy()));
			pst.setDouble(14, uF.parseToDouble(getNoOfLeaveMonthly()));
			pst.setInt(15, uF.parseToInt(getAccrualSystem()));
			pst.setInt(16, uF.parseToInt(getAccrualFrom()));
			pst.setInt(17, uF.parseToInt(getEmpBreakTypeId()));
			
			pst.executeUpdate();
			pst.close();
			
			
			session.setAttribute(MESSAGE, SUCCESSM+"Break policy updated successfully."+END);
			
			
			
//			CF.updateLeaveRegister(getLevelType(), getNoOfLeave() , getApprovalDate(),  getTypeOfLeave(), getIsCarryForward(), CF);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	
	


	public String deleteEmployeeIssueLeave(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteEmployeeIssueLeave);
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Leave policy deleted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public void validate() {
		
	    loadValidateEmployeeIssueLeave();
	}
	private Map<String, String> getLocationName() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
				
		Map<String,String> hmLocationName=new HashMap<String, String>();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from work_location_info");
			rs=pst.executeQuery();
			
			while(rs.next()){
				hmLocationName.put(rs.getString("wlocation_id"),rs.getString("wlocation_name"));
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
		return hmLocationName;
	}
	private Map<String, String> getOrganization() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		Map<String,String> hmOrg=new HashMap<String, String>();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select org_id,org_name from org_details");
			rs=pst.executeQuery();
			while(rs.next()){
				hmOrg.put(rs.getString("org_id"),rs.getString("org_name"));
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
		return hmOrg;
	}
	String  strPolicyId;
	String  effectiveFrom;
	boolean ispaid;
	boolean isCarryForward;	
	String empBreakTypeId;
	String employeType;
	String levelType;
	String typeOfbreak;
	String noOfLeave;
	String approvalDate;
	
	String noOfLeaveMonthly;
	String accrualSystem;
	String accrualFrom;
	
	String monthlyLeaveLimit;
	boolean isMonthlyCarryForward;
	boolean isApproval;

	List<FillUserType> userTypeList;
	List<FillLevel> levelTypeList; 
	List<FillBreakType> empBreakTypeList;
	List<FillApproval> approvalList;
	
	
	public String getEmployeType() {
		return employeType;
	}
	public void setEmployeType(String employeType) {
		this.employeType = employeType;
	}
	public String getNoOfLeave() {
		return noOfLeave;
	}
	public void setNoOfLeave(String noOfLeave) {
		this.noOfLeave = noOfLeave;
	}
	
	public List<FillUserType> getUserTypeList() {
		return userTypeList;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	public List<FillApproval> getApprovalList() {
		return approvalList;
	}
	public void setApprovalList(List<FillApproval> approvalList) {
		this.approvalList = approvalList;
	}
	public String getEffectiveFrom() {
		return effectiveFrom;
	}
	public void setEffectiveFrom(String effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public String getApprovalDate() {
		return approvalDate;
	}
	public void setApprovalDate(String approvalDate) {
		this.approvalDate = approvalDate;
	}
	public void setUserTypeList(List<FillUserType> userTypeList) {
		this.userTypeList = userTypeList;
	}
	public List<FillLevel> getLevelTypeList() {
		return levelTypeList;
	}
	public String getLevelType() {
		return levelType;
	}
	public void setLevelType(String levelType) {
		this.levelType = levelType;
	}
	public boolean getIspaid() {
		return ispaid;
	}
	public void setIspaid(boolean ispaid) {
		this.ispaid = ispaid;
	}
	public boolean getIsCarryForward() {
		return isCarryForward;
	}
	public void setIsCarryForward(boolean isCarryForward) {
		this.isCarryForward = isCarryForward;
	}
	public String getStrPolicyId() {
		return strPolicyId;
	}
	public void setStrPolicyId(String strPolicyId) {
		this.strPolicyId = strPolicyId;
	}
	public String getMonthlyLeaveLimit() {
		return monthlyLeaveLimit;
	}
	public void setMonthlyLeaveLimit(String monthlyLeaveLimit) {
		this.monthlyLeaveLimit = monthlyLeaveLimit;
	}
	public boolean getIsMonthlyCarryForward() {
		return isMonthlyCarryForward;
	}
	public void setIsMonthlyCarryForward(boolean isMonthlyCarryForward) {
		this.isMonthlyCarryForward = isMonthlyCarryForward;
	}
	public boolean getIsApproval() {
		return isApproval;
	}
	public void setIsApproval(boolean isApproval) {
		this.isApproval = isApproval;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getStrLocation() {
		return strLocation;
	}
	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}
	public String getPolicy() {
		return policy;
	}
	public void setPolicy(String policy) {
		this.policy = policy;
	}
	public String getNoOfLeaveMonthly() {
		return noOfLeaveMonthly;
	}
	public void setNoOfLeaveMonthly(String noOfLeaveMonthly) {
		this.noOfLeaveMonthly = noOfLeaveMonthly;
	}
	public String getAccrualSystem() {
		return accrualSystem;
	}
	public void setAccrualSystem(String accrualSystem) {
		this.accrualSystem = accrualSystem;
	}
	public String getAccrualFrom() {
		return accrualFrom;
	}
	public void setAccrualFrom(String accrualFrom) {
		this.accrualFrom = accrualFrom;
	}
	public String getTypeOfbreak() {
		return typeOfbreak;
	}
	public void setTypeOfbreak(String typeOfbreak) {
		this.typeOfbreak = typeOfbreak;
	}
	public String getEmpBreakTypeId() {
		return empBreakTypeId;
	}
	public void setEmpBreakTypeId(String empBreakTypeId) {
		this.empBreakTypeId = empBreakTypeId;
	}
	public List<FillBreakType> getEmpBreakTypeList() {
		return empBreakTypeList;
	}
	public void setEmpBreakTypeList(List<FillBreakType> empBreakTypeList) {
		this.empBreakTypeList = empBreakTypeList;
	}
	
}