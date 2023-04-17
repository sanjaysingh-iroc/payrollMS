package com.konnect.jpms.salary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.employee.AddEmployee;
import com.konnect.jpms.payroll.ApprovePayroll;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeSalaryDetails extends ActionSupport implements ServletRequestAware, IStatements{

	private static final long serialVersionUID = 5947893602821384559L;
	private String[] salary_head_id;
	private String[] salary_head_value;
//	private String[] isDisplay;
	private String[] emp_salary_id; 
	private String[] salary_type;
	private String removeId;
	private String CCID;  
	private String mode;
	private String step;
	private String strUserType;
	private String strSessionEmpId;
	private String effectiveDate;
	
	HttpSession session; 
	public CommonFunctions CF; 
	public String empId;
	
	public List<FillSalaryHeads> salaryHeadList;
	
	private String basic;
	private boolean disableSalaryStructure;
	private String oldGradeId;
	private String ctcAmt;
	
	public String execute()	{
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, PEmployeeSalaryDetails);
		request.setAttribute(TITLE, TEmployeeSalaryDetails);
		request.setAttribute("empId", getEmpId());
		
		request.setAttribute("salaryStructure", CF.getStrSalaryStructure());
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		String update = (String)request.getParameter("update");
//		System.out.println("mode==>"+getMode());
		int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
		if(nSalaryStrucuterType == S_GRADE_WISE) {
//			System.out.println("inside if");
			
			if(update!=null && update.trim().equalsIgnoreCase("Update")) {
				updateEmployeeSalaryDetailsByGrade(uF);
//				System.out.println("update if==>"+request.getParameter("update"));
//				return UPDATE;
				if(getMode()!= null && !getMode().equals("")) {
					return UPDATE;
				} else {
					return "myprofile";
				}
			}
			if(getSalary_head_id()!=null) {
				if(getBasic()!=null && getBasic().equals("basic")) {
					insertIncrementEmpSalaryDetailsByGrade(uF);
//					System.out.println("if basic");
					return "basic";
				} else {
					insertEmpSalaryDetailsByGrade(uF);
//					System.out.println("if insert");
//					return "insert";
					if(getMode()!= null && !getMode().equals("")) {
						return "insert";
					} else {
						return "myprofile";
					}
				}
			} else if(getEmpId()!=null) {
				salaryHeadList = new ArrayList<FillSalaryHeads>();
				salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads();
				
				if(getRemoveId()!=null) {
					removeEmpSalaryHeadByGrade(uF);
				}
				if(getBasic()!=null && getBasic().equals("basic") && uF.parseToInt(getOldGradeId()) > 0) {
					getOldGradeIdSelectedSalaryHead(uF);
				}
				
				if(getMode()!=null && getMode().equals("E")) {
					request.setAttribute(PAGE, "/jsp/salary/EmployeeSalaryDetailsEdit.jsp");
					boolean flag = viewUpdateEmployeeSalaryDetailsByGrade(uF);
//					System.out.println("if flag==>"+flag);
					if(!flag) {
						viewEmployeeSalaryDetailsByGrade(uF);
						return "tab";
					} else {
						return "edittab";
					}
				}else if(getMode()!=null && getMode().equals("A")) {
					viewEmployeeSalaryDetailsByGrade(uF);
//					System.out.println("if A");
					return "tab";
				}
				request.setAttribute("mode", request.getParameter("mode"));
//				System.out.println("if end");
				return SUCCESS;
			}
		} else {
			System.out.println("inside else");
//			System.out.println("update==>"+update);
			
			if(update!=null && update.trim().equalsIgnoreCase("Update")) {
//				System.out.println("update if ==>");
				updateEmployeeSalaryDetails();
				
				if(getMode()!= null && !getMode().equals("")) {
					return UPDATE;
				} else {
					return "myprofile";
				}
				
			}
			
//			System.out.println("update out ==>");
			if(getSalary_head_id()!=null) {
				insertEmpSalaryDetails();
//				System.out.println("insert else==>"+request.getParameter("update"));
				if(getMode()!= null && !getMode().equals("")) {
					return "insert";
				} else {
					return "myprofile";
				}
				
			} else if(getEmpId()!=null) {
				salaryHeadList = new ArrayList<FillSalaryHeads>();
				salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads();
				
				if(getRemoveId()!=null) {
					removeEmpSalaryHead();
				}
				
				System.out.println("getMode() ===>> " + getMode());
				if(getMode()!=null && getMode().equals("E")) {
					request.setAttribute(PAGE, "/jsp/salary/EmployeeSalaryDetailsEdit.jsp");
					boolean flag = viewUpdateEmployeeSalaryDetails();
					System.out.println("else flag==>"+flag);
					if(!flag) {
						viewEmployeeSalaryDetails();
						return "tab";
					} else {
						return "edittab";
					}
				}else if(getMode()!=null && getMode().equals("A")) {
					viewEmployeeSalaryDetails();
//					System.out.println("else A==>");
					return "tab";
				}
				request.setAttribute("mode", request.getParameter("mode"));
//				System.out.println("else end==>");
				return SUCCESS;
			}
		}
		
		return SUCCESS;
	}
	
	void getOldGradeIdSelectedSalaryHead(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
//			
			
			pst = con.prepareStatement("select * from emp_salary_details where emp_id=? " +
					"and grade_id=? and isdisplay=true and is_approved=true " +
					"and effective_date=(select max(effective_date) as effective_date " +
					"from emp_salary_details where emp_id=? and grade_id=? and is_approved=true " +
					"and effective_date<=?)");
			pst.setInt(1,uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt(getOldGradeId()));
			pst.setInt(3,uF.parseToInt(getEmpId()));
			pst.setInt(4, uF.parseToInt(getOldGradeId()));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			List<String> alOldGradeSalaryHeadId = new ArrayList<String>();
			while(rs.next()) {
				if(!alOldGradeSalaryHeadId.contains(rs.getString("salary_head_id"))) {
					alOldGradeSalaryHeadId.add(rs.getString("salary_head_id"));
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alOldGradeSalaryHeadId", alOldGradeSalaryHeadId);
			
//			System.out.println("alOldGradeSalaryHeadId==>"+alOldGradeSalaryHeadId);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void insertIncrementEmpSalaryDetailsByGrade(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			
			boolean isApproved = false;
			if(getBasic()!=null && getBasic().equals("basic")) {
				isApproved = true;
			}
			
			String strEmpGradeId = CF.getEmpGradeId(con, getEmpId());
			
			pst = con.prepareStatement("select * from salary_details where grade_id=?");
			pst.setInt(1, uF.parseToInt(strEmpGradeId));
			rs = pst.executeQuery();
			Map<String, String> hmEarningDeductionMap = new HashMap<String, String>();
			Map<String, String> hmSalaryTypeMap = new HashMap<String, String>();
			while(rs.next()) {
				hmEarningDeductionMap.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
				hmSalaryTypeMap.put(rs.getString("salary_head_id"), rs.getString("salary_type"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from emp_salary_details where effective_date = ? and emp_id = ?");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			boolean isDateExist = false;
			while(rs.next()) {
				isDateExist = true;
			}
			rs.close();
			pst.close();
			
			
			if(isDateExist) {
				pst = con.prepareStatement("delete from emp_salary_details where effective_date = ? and emp_id = ?");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(2, uF.parseToInt(getEmpId()));
				pst.execute();
				pst.close();
			}  
			
			for(int i=0; i<salary_head_id.length; i++) {
				String isDisplay = (String) request.getParameter("isDisplay_"+getSalary_head_id()[i]);
//				pst = con.prepareStatement(insertEmpSalaryDetails);
				pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id , salary_head_id, amount, entry_date, user_id, pay_type, " +
						"isdisplay, service_id, effective_date, earning_deduction, salary_type,is_approved,approved_by,approved_date,grade_id) " +
						"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.setInt(2, uF.parseToInt(getSalary_head_id()[i]));
				pst.setDouble(3, uF.parseToDouble(getSalary_head_value()[i]));
				pst.setDate	(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(5, uF.parseToInt(strSessionEmpId));
				pst.setString(6, "M");
				pst.setBoolean(7, uF.parseToBoolean(isDisplay));
//				pst.setInt(8, uF.parseToInt(getCCID()));
				pst.setInt(8, 0);
				pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(10, hmEarningDeductionMap.get(getSalary_head_id()[i]));
				pst.setString(11, hmSalaryTypeMap.get(getSalary_head_id()[i]));
				pst.setBoolean(12, isApproved);
				pst.setInt(13, uF.parseToInt(strSessionEmpId));
				pst.setDate(14, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(15, uF.parseToInt(strEmpGradeId));
//				System.out.println("pst insertEmpSalaryDetails==>"+pst);  
				pst.execute();
				pst.close();
			}
			
			CF.updateNextEmpSalaryEffectiveDate(con, uF, uF.parseToInt(getEmpId()), uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), DATE_FORMAT);
			
			pst = con.prepareStatement("update employee_official_details set is_disable_sal_calculate=? where emp_id = ?");
			pst.setBoolean(1, getDisableSalaryStructure());
			pst.setInt(2, uF.parseToInt(getEmpId()));
			pst.execute();
			pst.close();
			
			int ServiceNo = uF.parseToInt((String)session.getAttribute("ServicesLinkNo"));
//			session.setAttribute("ServicesLinkNo", (ServiceNo-1)+"");    // Uncomment this code if you wish to use salary cost center wise.
			session.setAttribute("ServicesLinkNo", 1+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void viewEmployeeSalaryDetailsByGrade(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
//			System.out.println("in new viewEmployeeSalaryDetails ...");
			String level_id = CF.getEmpLevelId(con, getEmpId());
			String gradeId = CF.getEmpGradeId(con, getEmpId());
			String strOrgId = CF.getEmpOrgId(con, uF, getEmpId());
			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMapByGrade(con, uF.parseToInt(gradeId));
			
			request.setAttribute("gradeId", gradeId);
			request.setAttribute("levelId", level_id);
			request.setAttribute("strOrgId", strOrgId);
			
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			strFinancialYearStart = strFinancialYearDates[0];
			strFinancialYearEnd = strFinancialYearDates[1];
			
			Map<String, String> hmEmpAnnualVarPolicyAmount = CF.getEmpAnnualVariablePolicyMonthAmount(con, uF, getEmpId(), strFinancialYearStart, strFinancialYearEnd);
			if(hmEmpAnnualVarPolicyAmount == null) hmEmpAnnualVarPolicyAmount = new HashMap<String, String>();
			request.setAttribute("hmEmpAnnualVarPolicyAmount", hmEmpAnnualVarPolicyAmount);
			
			List<List<String>> al = new ArrayList<List<String>>();
			
			setDisableSalaryStructure(CF.getEmpDisableSalaryCalculation(con,uF,getEmpId()));
			
			
			String strEmployeeStatus = null;
			pst = con.prepareStatement("select emp_status from employee_personal_details where emp_per_id=?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
//			System.out.println("pst salary_details ===>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				strEmployeeStatus = rs.getString("emp_status");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from vda_rate_details where desig_id = (select designation_id from grades_details where grade_id =?) " +
					"and from_date = (select max(from_date) as from_date from vda_rate_details where desig_id = (select designation_id from grades_details where grade_id =?) and from_date <=?)");
			pst.setInt(1, uF.parseToInt(gradeId));
			pst.setInt(2, uF.parseToInt(gradeId));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			double dblVDAAmount = 0.0d;
			while(rs.next()) {
				if(strEmployeeStatus!=null && strEmployeeStatus.equalsIgnoreCase("PROBATION")) {
					dblVDAAmount = rs.getDouble("vda_amount_probation");
				} else if(strEmployeeStatus!=null && strEmployeeStatus.equalsIgnoreCase("PERMANENT")) {
					dblVDAAmount = rs.getDouble("vda_amount_permanent");
				} else if(strEmployeeStatus!=null && strEmployeeStatus.equalsIgnoreCase("TEMPORARY")) {
					dblVDAAmount = rs.getDouble("vda_amount_temporary");
				}
			}
			rs.close();
			pst.close();
//			System.out.println("dblVDAAmount ===>> " + dblVDAAmount);
			
			double dblIncrementBasic = 0.0d;
			if(getBasic()!=null && getBasic().equals("basic")) {
				pst = con.prepareStatement("select * from basic_fitment_details where grade_id=? and trail_status=1");
				pst.setInt(1, uF.parseToInt(gradeId));
//				System.out.println("pst salary_details ===>> " + pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					dblIncrementBasic = uF.parseToDouble(rs.getString("amount"));
				}
			}
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE grade_id = ? and is_annual_variable=true and (is_delete is null or is_delete=false) order by weight");
			pst.setInt(1, uF.parseToInt(gradeId));
			rs = pst.executeQuery();
			List<String> alAnnualSalaryHead = new ArrayList<String>();
			while(rs.next()) {
				if(!alAnnualSalaryHead.contains(rs.getString("salary_head_id"))) {
					alAnnualSalaryHead.add(rs.getString("salary_head_id"));
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("alAnnualSalaryHead", alAnnualSalaryHead);
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE grade_id = ? and (is_delete is null or is_delete=false) order by weight");
			pst.setInt(1, uF.parseToInt(gradeId));
//			System.out.println("pst salary_details ===>> " + pst);
			rs = pst.executeQuery();
			List alSalaryDuplicationTracer = new ArrayList();
			int nEarningCnt = 0;
			while(rs.next()) {
				
				if(uF.parseToInt(rs.getString("salary_head_id")) > 0 && uF.parseToInt(rs.getString("salary_head_id"))!=CTC && rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").trim().equals("E")) {
					nEarningCnt++;
				}
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("salary_head_id"));	//0
				alInner.add(rs.getString("salary_head_name"));	//1
				alInner.add(rs.getString("earning_deduction"));	//2
				alInner.add(rs.getString("salary_head_amount_type"));	//3
				String rsHeadId = rs.getInt("sub_salary_head_id") + "";
				alInner.add(rsHeadId);	//4
				String alHeadId = "";
				
				/*for(int i=0; i<salaryHeadList.size(); i++) {
					
					alHeadId = ((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadId();
					
					if(rsHeadId.equalsIgnoreCase("0")) {
						alInner.add("0");	//5
						break;
					}else if(rsHeadId.equalsIgnoreCase(alHeadId)) {
						alInner.add(((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName());	//5
						break;
					}
				}*/
				alInner.add("0");	//5				
				
				if(getBasic()!=null && getBasic().equals("basic") && uF.parseToInt(rs.getString("salary_head_id")) == BASIC) {
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblIncrementBasic));	//6
				} else if(uF.parseToInt(rs.getString("salary_head_id")) == VDA && !getDisableSalaryStructure()) {
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblVDAAmount));		//6
				} else {
					alInner.add((rs.getString("salary_head_amount_type") != null && rs.getString("salary_head_amount_type").equals("P")) ?  uF.formatIntoFourDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))) : uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));	//6
				}
				
				StringBuilder sbMulcalType = new StringBuilder();
				/* if("M".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
					String strMulCal = rs.getString("multiple_calculation");
					String[] strMulCalTemp = strMulCal.split(",");

					CF.appendMultipleCalType(uF,sbMulcalType,strMulCalTemp,hmSalaryMap,alAnnualSalaryHead);  
				}*/
				if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
					String strMulCal = rs.getString("multiple_calculation");
					CF.appendMultiplePercentageCalType(uF,sbMulcalType,strMulCal,hmSalaryMap,alAnnualSalaryHead); 
				} 
				
				alInner.add(rs.getString("multiple_calculation")); //7
				alInner.add(sbMulcalType.toString()); //8
				alInner.add(rs.getString("max_cap_amount")); //9
				
				int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
				if(index>=0) {
					al.remove(index);
					al.add(index, alInner);
				} else {
					alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
					al.add(alInner);
				}
				
			}
			rs.close();
			pst.close();
			
//			System.out.println("reportList viewEmployeeSalaryDetails ===>> " + al);
			request.setAttribute("reportList", al);
			request.setAttribute("nEarningCnt", ""+nEarningCnt);
			
			
			
			request.setAttribute("CCID", getCCID());
			
			pst = con.prepareStatement(selectServiceV);
			pst.setInt(1, uF.parseToInt(getCCID()));
			rs = pst.executeQuery();
			String name = "";
			while(rs.next()) {
				name = rs.getString("service_name");
			}
			rs.close();
			pst.close();
			request.setAttribute("CCNAME", name);
			
			String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String[] strPayCycleDate = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrgId);
			if(strPayCycleDate !=null && strPayCycleDate.length > 0) {
				String startDate = strPayCycleDate[0];
				String endDate = strPayCycleDate[1];
				String strPC = strPayCycleDate[2];
			
				double dblReimbursementCTC = CF.getReimbursementCTCHeadTotalAmount(con, uF, uF.parseToInt(getEmpId()), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(level_id));
				request.setAttribute("dblReimbursementCTC", ""+dblReimbursementCTC);
				
				double dblReimbursementCTCOptional = CF.getReimbursementCTCOptinalHeadTotalAmount(con, uF, uF.parseToInt(getEmpId()), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(level_id));
				request.setAttribute("dblReimbursementCTCOptional", ""+dblReimbursementCTCOptional);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private boolean viewUpdateEmployeeSalaryDetailsByGrade(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		boolean flag = false;
		try {
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];
			
			con = db.makeConnection(con);
//			System.out.println("in viewUpdateEmployeeSalaryDetails ...");
			
			String levelId = CF.getEmpLevelId(con, getEmpId());
			String gradeId = CF.getEmpGradeId(con, getEmpId());			
			String strOrgId = CF.getEmpOrgId(con, uF, getEmpId());
			
			request.setAttribute("gradeId", gradeId);
			request.setAttribute("levelId", levelId);
			request.setAttribute("strOrgId", strOrgId);
			
			Map hmEmpMertoMap = new HashMap();
			Map hmEmpWlocationMap = new HashMap();
			Map hmEmpStateMap = new HashMap();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
			
			Map<String, String> hmEmpAnnualVarPolicyAmount = CF.getEmpAnnualVariablePolicyMonthAmount(con, uF, getEmpId(), strFinancialYearStart, strFinancialYearEnd);
			if(hmEmpAnnualVarPolicyAmount == null) hmEmpAnnualVarPolicyAmount = new HashMap<String, String>();
			request.setAttribute("hmEmpAnnualVarPolicyAmount", hmEmpAnnualVarPolicyAmount);
			
			String strStateId = (String)hmEmpStateMap.get(getEmpId());
			
			List<List<String>> alE = new ArrayList<List<String>>();
			
			setDisableSalaryStructure(CF.getEmpDisableSalaryCalculation(con,uF,getEmpId()));
			
			
			String strEmployeeStatus = null;
			pst = con.prepareStatement("select emp_status from employee_personal_details where emp_per_id=?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
//			System.out.println("pst salary_details ===>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				strEmployeeStatus = rs.getString("emp_status");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from vda_rate_details where desig_id = (select designation_id from grades_details where grade_id =?) " +
					"and from_date = (select max(from_date) as from_date from vda_rate_details where desig_id = (select designation_id from grades_details where grade_id =?) and from_date <=?)");
			pst.setInt(1, uF.parseToInt(gradeId));
			pst.setInt(2, uF.parseToInt(gradeId));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			double dblVDAAmount = 0.0d;
			while(rs.next()) {
				if(strEmployeeStatus!=null && strEmployeeStatus.equalsIgnoreCase("PROBATION")) {
					dblVDAAmount = rs.getDouble("vda_amount_probation");
				} else if(strEmployeeStatus!=null && strEmployeeStatus.equalsIgnoreCase("PERMANENT")) {
					dblVDAAmount = rs.getDouble("vda_amount_permanent");
				} else if(strEmployeeStatus!=null && strEmployeeStatus.equalsIgnoreCase("TEMPORARY")) {
					dblVDAAmount = rs.getDouble("vda_amount_temporary");
				}
			}
			rs.close();
			pst.close();
//			System.out.println("dblVDAAmount ===>> " + dblVDAAmount);
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE grade_id = ? and is_annual_variable=true and (is_delete is null or is_delete=false) order by weight");
			pst.setInt(1, uF.parseToInt(gradeId));
			rs = pst.executeQuery();
			List<String> alAnnualSalaryHead = new ArrayList<String>();
			while(rs.next()) {
				if(!alAnnualSalaryHead.contains(rs.getString("salary_head_id"))) {
					alAnnualSalaryHead.add(rs.getString("salary_head_id"));
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("alAnnualSalaryHead", alAnnualSalaryHead);
			
			String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String[] strPayCycleDate = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrgId);
			double dblReimbursementCTC = 0.0d;
			double dblReimbursementCTCOptional = 0.0d;
			if(strPayCycleDate !=null && strPayCycleDate.length > 0) {
				String startDate = strPayCycleDate[0];
				String endDate = strPayCycleDate[1];
				String strPC = strPayCycleDate[2];
			
				dblReimbursementCTC = CF.getReimbursementCTCHeadTotalAmount(con, uF, uF.parseToInt(getEmpId()), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(levelId));
				request.setAttribute("dblReimbursementCTC", ""+dblReimbursementCTC);
				
				dblReimbursementCTCOptional = CF.getReimbursementCTCOptinalHeadTotalAmount(con, uF, uF.parseToInt(getEmpId()), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(levelId));
				request.setAttribute("dblReimbursementCTCOptional", ""+dblReimbursementCTCOptional);
			}
			
			
			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMapByGrade(con, uF.parseToInt(gradeId));
			
			pst = con.prepareStatement("SELECT weight,isdisplay,pay_type,user_id,entry_date,amount,emp_salary_id,salary_head_amount,sd.earning_deduction," +
				"salary_head_amount_type,sub_salary_head_id, sd.salary_head_id as salary_head_id,multiple_calculation,salary_calculate_amount,max_cap_amount,is_contribution " +
				"FROM (SELECT * FROM emp_salary_details WHERE emp_id = ? AND service_id = ? AND effective_date = (SELECT MAX(effective_date) " +
				"FROM emp_salary_details WHERE emp_id = ? and is_approved=true and grade_id=?) AND effective_date <= ? and grade_id=?) asd RIGHT JOIN " +
				"salary_details sd ON sd.salary_head_id = asd.salary_head_id WHERE sd.grade_id=? and (sd.is_delete is null or sd.is_delete=false) " +
				"order by sd.earning_deduction desc, weight");
			pst.setInt(1, uF.parseToInt(getEmpId()) );
//			pst.setInt(2, uF.parseToInt(getCCID()) );
			pst.setInt(2, 0);  // Default Service Id
			pst.setInt(3, uF.parseToInt(getEmpId()));
			pst.setInt(4, uF.parseToInt(gradeId));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt(gradeId));
			pst.setInt(7, uF.parseToInt(gradeId));
			rs = pst.executeQuery();
//			System.out.println("pst viewUpdateEmployeeSalaryDetails ===>> " + pst);
			String alHeadId = "";
			Map<String, String> hmSalaryAmountMap = new HashMap<String, String>();
			List alSalaryDuplicationTracer = new ArrayList();
			Map<String, String> hmTotal = new HashMap<String, String>();
			int nEarningCnt = 0;
			int nDisplay = 0; 
			while(rs.next()) {
				if(uF.parseToInt(rs.getString("salary_head_id")) > 0 && uF.parseToInt(rs.getString("salary_head_id"))!=CTC && rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").trim().equals("E")) {
					nEarningCnt++;
				}
				if(uF.parseToBoolean(rs.getString("isdisplay"))) {
					nDisplay++;
				}
				
				hmSalaryAmountMap.put(rs.getString("salary_head_id"), (rs.getString("salary_head_amount_type") != null && rs.getString("salary_head_amount_type").equals("P")) ?  uF.formatIntoFourDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))) : uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(uF.parseToInt(rs.getString("emp_salary_id"))+"");	//0
				String rsHeadId = rs.getString("salary_head_id");
				alInner.add(rsHeadId);	//1
				
//				for(int i=0; i<salaryHeadList.size(); i++) {
//					
//					alHeadId = ((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadId();
//					
////					System.out.println("alHeadId==>"+alHeadId);levelId
//					
//					if(rsHeadId!=null && rsHeadId.equalsIgnoreCase("0")) {
//						alInner.add("0");	//2
//						break;
//					}else if(rsHeadId!=null && rsHeadId.equalsIgnoreCase(alHeadId)) {
//						alInner.add(((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName());	//2
//						break;
//					}
//				}
				
				alInner.add(uF.showData(hmSalaryMap.get(rsHeadId), ""));	//2
				alInner.add(rs.getString("earning_deduction"));	//3
				alInner.add(rs.getString("salary_head_amount_type")); //4
				rsHeadId = rs.getString("sub_salary_head_id");	
				alInner.add(rsHeadId);	//5
				
//				for(int i=0; i<salaryHeadList.size(); i++) {
//					
//					alHeadId = ((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadId();
//					if(alHeadId==null)continue;
//					
//					
//					if(rsHeadId.equalsIgnoreCase("0")) {
//						alInner.add("0");	//6
//						break;
//					}else if(rsHeadId.equalsIgnoreCase(alHeadId)) {
//
//						if(uF.parseToInt(rsHeadId)==1) { 
//							
//							if(uF.parseToInt(rs.getString("salary_head_id"))>2) {
////								alInner.add(((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName()+" + DA ");	//6
//								
//								StringBuilder sb = new StringBuilder();
//								if(hmSalaryAmountMap.containsKey(DA+"")) {
//									sb.append(" + "+hmSalaryMap.get(DA+""));
//								}
//								if(hmSalaryAmountMap.containsKey(DA1+"")) {
//									sb.append(" + "+hmSalaryMap.get(DA1+""));
//								}
//								alInner.add(((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName()+sb.toString());	//6
//								
//							} else {   
//								alInner.add(((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName());	//6
//							}
//							
//							
//						} else {
//							alInner.add(((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName());	//6
//						}
//						
//						
//						break;
//					}
//				}
				alInner.add("");	//6
				if(rs.getString("salary_head_id") != null && rs.getInt("salary_head_id") == VDA && !getDisableSalaryStructure()) {
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblVDAAmount)); //7
				} else {
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount")))); //7
				}
				double dblAmount = 0;
				if(rs.getString("amount")==null) {
					String strAmountType = rs.getString("salary_head_amount_type");
					if(strAmountType!=null && strAmountType.equalsIgnoreCase("P")) {
						dblAmount = rs.getDouble("salary_calculate_amount");
					}else if(strAmountType!=null && strAmountType.equalsIgnoreCase("A")) {
						if(rs.getString("salary_head_id") != null && rs.getInt("salary_head_id") == VDA && !getDisableSalaryStructure()) {
							dblAmount = dblVDAAmount; 
						} else {
							dblAmount = rs.getDouble("salary_head_amount");
						}
					}
				} else {
					if(rs.getString("salary_head_id") != null && rs.getInt("salary_head_id") == VDA && !getDisableSalaryStructure()) {
						dblAmount = dblVDAAmount; 
					} else {
						dblAmount = rs.getDouble("amount") ;
					}
				}
//				System.out.println("dblAmount=="+dblAmount);
				
				StringBuilder sbMulcalType = new StringBuilder();
//				ApprovePayroll objAP = new ApprovePayroll();
//				objAP.CF = CF;
//				objAP.session = session;
//				objAP.request = request; 
//				
//				if(uF.parseToBoolean(rs.getString("isdisplay"))) {
//					switch(rs.getInt("salary_head_id")) {
//					
//						case TDS:
//							
//	//						dblAmount = objAP.calculateTDS(dblGross, dblCess1, dblCess2, dblHRA, dblBasicDA, nPayMonth, strPaycycleStart, strFinancialYearStart, strFinancialYearEnd, strEmpId, strGender, strAge, strWLocationStateId, hmEmpExemptionsMap, hmFixedExemptions, hmEmpMertoMap, hmEmpRentPaidMap, hmPaidSalaryDetails, hmTotal, hmSalaryDetails);
//						
//	//						System.out.println("======dblAmount"+dblAmount);
//							alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblAmount));	//6
//	//						System.out.println("======dblAmount"+uF.formatIntoTwoDecimal(dblAmount));
//							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
//							
//						break; 
//					
//						case EMPLOYEE_EPF :
//								
////							dblAmount = objAP.calculateEEPF(con, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmTotal, null, null, null, false);
//							dblAmount = objAP.calculateEEPF(con, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmTotal, getEmpId(), null, null, false, null);
////							System.out.println("dblAmount=======>"+dblAmount);
//							alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblAmount));	//6
//							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
//							
//						break;
//						
//						case EMPLOYER_EPF :
//							
//							dblAmount = objAP.calculateERPF(con, CF, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, getEmpId(), null, null, false, null);
//						
//							alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblAmount));	//6
//							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
//							
//						break;  
//						
//						case EMPLOYER_ESI :
//							
//							dblAmount = objAP.calculateERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId,getEmpId(), null, null);
//						
//							alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblAmount));	//6
//							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
//							
//						break;
//						
//						case EMPLOYEE_ESI :
//							
//							dblAmount = objAP.calculateEEESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, null);
//						
//							alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblAmount));	//6
//							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
//							
//						break;
//						
//						case MOBILE_RECOVERY:
//							
//							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
//							alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblAmount));	//6
//							break;
//							
//						default:
//							alInner.add(uF.formatIntoOneDecimalWithOutComma(dblAmount));	//6
//							
//							if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
//								
//								double dbl = uF.parseToDouble((String)hmTotal.get(rs.getString("sub_salary_head_id")));
//								if(uF.parseToInt(rs.getString("sub_salary_head_id")) == REIMBURSEMENT_CTC) {
//									dbl = dblReimbursementCTC;
//								}
//								dbl = dbl * rs.getDouble("salary_head_amount") /100;
//								hmTotal.put(rs.getString("salary_head_id"), dbl+"");
//							}else if("M".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
//								String strMulCal = rs.getString("multiple_calculation");
//								String[] strMulCalTemp = strMulCal.split(",");
//
//								CF.appendMultipleCalType(uF,sbMulcalType,strMulCalTemp,hmSalaryMap,alAnnualSalaryHead);
//								
//								double dblMulAmt = CF.getMultipleCalAmount(uF,strMulCalTemp[0].trim(),hmTotal.get(strMulCalTemp[0].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//								if(strMulCalTemp[1].trim().equals("+")) {
//									dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[2].trim(),hmTotal.get(strMulCalTemp[2].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//								} else if(strMulCalTemp[1].trim().equals("-")) {
//									dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[2].trim(),hmTotal.get(strMulCalTemp[2].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//								}
//								if(strMulCalTemp[3].trim().equals("+")) {
//									dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[4].trim(),hmTotal.get(strMulCalTemp[4].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//								} else if(strMulCalTemp[3].trim().equals("-")) {
//									dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[4].trim(),hmTotal.get(strMulCalTemp[4].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//								}
//								if(strMulCalTemp[5].trim().equals("+")) {
//									dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[6].trim(),hmTotal.get(strMulCalTemp[6].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//								} else if(strMulCalTemp[5].trim().equals("-")) {
//									dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[6].trim(),hmTotal.get(strMulCalTemp[6].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//								}
//								if(strMulCalTemp[7].trim().equals("+")) {
//									dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[8].trim(),hmTotal.get(strMulCalTemp[8].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//								} else if(strMulCalTemp[7].trim().equals("-")) {
//									dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[8].trim(),hmTotal.get(strMulCalTemp[8].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//								}
//								if(strMulCalTemp[9].trim().equals("+")) {
//									dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[10].trim(),hmTotal.get(strMulCalTemp[10].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//								} else if(strMulCalTemp[9].trim().equals("-")) {
//									dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[10].trim(),hmTotal.get(strMulCalTemp[10].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//								}
//								if(strMulCalTemp[11].trim().equals("+")) {
//									dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[12].trim(),hmTotal.get(strMulCalTemp[12].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//								} else if(strMulCalTemp[11].trim().equals("-")) {
//									dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[12].trim(),hmTotal.get(strMulCalTemp[12].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//								}
//								if(strMulCalTemp[13].trim().equals("+")) {
//									dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[14].trim(),hmTotal.get(strMulCalTemp[14].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//								} else if(strMulCalTemp[13].trim().equals("-")) {
//									dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[14].trim(),hmTotal.get(strMulCalTemp[14].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//								}
//								if(strMulCalTemp[15].trim().equals("+")) {
//									dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[16].trim(),hmTotal.get(strMulCalTemp[16].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//								} else if(strMulCalTemp[15].trim().equals("-")) {
//									dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[16].trim(),hmTotal.get(strMulCalTemp[16].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//								}
//								if(strMulCalTemp[17].trim().equals("+")) {
//									dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[18].trim(),hmTotal.get(strMulCalTemp[18].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//								} else if(strMulCalTemp[17].trim().equals("-")) {
//									dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[18].trim(),hmTotal.get(strMulCalTemp[18].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//								}
//								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblMulAmt));
//							} else {
//								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoOneDecimalWithOutComma(dblAmount));
//							}
//							
//	//						hmTotal.put(rs.getString("salary_head_id"), rs.getString("salary_head_amount"));
//						break;
//					}
//				} else if("M".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
//					String strMulCal = rs.getString("multiple_calculation");
//					String[] strMulCalTemp = strMulCal.split(",");
//
//					CF.appendMultipleCalType(uF,sbMulcalType,strMulCalTemp,hmSalaryMap,alAnnualSalaryHead);
//					
//					double dblMulAmt = CF.getMultipleCalAmount(uF,strMulCalTemp[0].trim(),hmTotal.get(strMulCalTemp[0].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//					if(strMulCalTemp[1].trim().equals("+")) {
//						dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[2].trim(),hmTotal.get(strMulCalTemp[2].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//					} else if(strMulCalTemp[1].trim().equals("-")) {
//						dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[2].trim(),hmTotal.get(strMulCalTemp[2].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//					}
//					if(strMulCalTemp[3].trim().equals("+")) {
//						dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[4].trim(),hmTotal.get(strMulCalTemp[4].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//					} else if(strMulCalTemp[3].trim().equals("-")) {
//						dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[4].trim(),hmTotal.get(strMulCalTemp[4].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//					}
//					if(strMulCalTemp[5].trim().equals("+")) {
//						dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[6].trim(),hmTotal.get(strMulCalTemp[6].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//					} else if(strMulCalTemp[5].trim().equals("-")) {
//						dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[6].trim(),hmTotal.get(strMulCalTemp[6].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//					}
//					if(strMulCalTemp[7].trim().equals("+")) {
//						dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[8].trim(),hmTotal.get(strMulCalTemp[8].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//					} else if(strMulCalTemp[7].trim().equals("-")) {
//						dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[8].trim(),hmTotal.get(strMulCalTemp[8].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//					}
//					if(strMulCalTemp[9].trim().equals("+")) {
//						dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[10].trim(),hmTotal.get(strMulCalTemp[10].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//					} else if(strMulCalTemp[9].trim().equals("-")) {
//						dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[10].trim(),hmTotal.get(strMulCalTemp[10].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//					}
//					if(strMulCalTemp[11].trim().equals("+")) {
//						dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[12].trim(),hmTotal.get(strMulCalTemp[12].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//					} else if(strMulCalTemp[11].trim().equals("-")) {
//						dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[12].trim(),hmTotal.get(strMulCalTemp[12].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//					}
//					if(strMulCalTemp[13].trim().equals("+")) {
//						dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[14].trim(),hmTotal.get(strMulCalTemp[14].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//					} else if(strMulCalTemp[13].trim().equals("-")) {
//						dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[14].trim(),hmTotal.get(strMulCalTemp[14].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//					}
//					if(strMulCalTemp[15].trim().equals("+")) {
//						dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[16].trim(),hmTotal.get(strMulCalTemp[16].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//					} else if(strMulCalTemp[15].trim().equals("-")) {
//						dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[16].trim(),hmTotal.get(strMulCalTemp[16].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//					}
//					if(strMulCalTemp[17].trim().equals("+")) {
//						dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[18].trim(),hmTotal.get(strMulCalTemp[18].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//					} else if(strMulCalTemp[17].trim().equals("-")) {
//						dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[18].trim(),hmTotal.get(strMulCalTemp[18].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
//					}
//					hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblMulAmt));
//					alInner.add(uF.formatIntoOneDecimalWithOutComma(dblMulAmt));
//				} else {
////					alInner.add("0");	//6
//					alInner.add(uF.formatIntoOneDecimalWithOutComma(dblAmount));
//				}
				
				
				if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
					String strMulCal = rs.getString("multiple_calculation");
					CF.appendMultiplePercentageCalType(uF,sbMulcalType,strMulCal,hmSalaryMap,alAnnualSalaryHead); 
					
					alInner.add(uF.formatIntoOneDecimalWithOutComma(dblAmount)); //8
				} else {
					alInner.add(uF.formatIntoOneDecimalWithOutComma(dblAmount)); //8
				}
				
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat())); //9
				alInner.add(rs.getString("user_id"));	//10
				alInner.add(rs.getString("pay_type"));	//11
				alInner.add(uF.parseToBoolean(rs.getString("isdisplay"))+"");	//12
				alInner.add(rs.getString("weight"));	//13
				alInner.add(rs.getString("multiple_calculation"));	//14
				alInner.add(sbMulcalType.toString());	//15
				alInner.add(rs.getString("max_cap_amount")); //16
				
				int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
				
				if(index>=0) {
					alE.remove(index);
					alE.add(index, alInner);
				} else {
					alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
					alE.add(alInner);
				}
				
//				System.out.println("alInner ===>> " + alInner);
				flag = true;	
			}
			rs.close();
			pst.close();
			
			setEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
			
//			System.out.println("hmTotal ===>> " + hmTotal);  
//			System.out.println("reportList alE ===>> " + alE.toString());  
			request.setAttribute("reportList", alE);
			request.setAttribute("nEarningCnt", ""+nEarningCnt);
			
			boolean displayFlag = false;
			if(nDisplay == 0) {
				displayFlag = true;
			}
			request.setAttribute("displayFlag", ""+displayFlag);
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}

	private void removeEmpSalaryHeadByGrade(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteEmpSalaryDetails);
			pst.setInt(1, uF.parseToInt((getRemoveId().split("_"))[1]));
//			System.out.println("pst for remove==>>"+pst);
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void insertEmpSalaryDetailsByGrade(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			
			String strEmpJoiningDate = CF.getEmpJoiningDate(con, uF, uF.parseToInt(getEmpId()));
			
			String strEmpGradeId = CF.getEmpGradeId(con, getEmpId());
			
			pst = con.prepareStatement("select * from salary_details where grade_id=?");
			pst.setInt(1, uF.parseToInt(strEmpGradeId));
			rs = pst.executeQuery();
			Map<String, String> hmEarningDeductionMap = new HashMap<String, String>();
			Map<String, String> hmSalaryTypeMap = new HashMap<String, String>();
			while(rs.next()) {
				hmEarningDeductionMap.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
				hmSalaryTypeMap.put(rs.getString("salary_head_id"), rs.getString("salary_type"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from emp_salary_details where effective_date = ? and emp_id = ?");
			pst.setDate(1, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			pst.setInt(2, uF.parseToInt(getEmpId()));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			boolean isDateExist = false;
			while(rs.next()) {
				isDateExist = true;
			}
			rs.close();
			pst.close();
			
			
			if(isDateExist) {
				pst = con.prepareStatement("delete from emp_salary_details where effective_date = ? and emp_id = ?");
				pst.setDate(1, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setInt(2, uF.parseToInt(getEmpId()));
				pst.execute();
				pst.close();
			}  
			
			for(int i=0; i<salary_head_id.length; i++) {
				String isDisplay = (String) request.getParameter("isDisplay_"+getSalary_head_id()[i]);
				pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id, salary_head_id, " +
						"amount, entry_date, user_id, pay_type, isdisplay, service_id, effective_date, " +
						"earning_deduction, salary_type,grade_id) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.setInt(2, uF.parseToInt(getSalary_head_id()[i]));
				pst.setDouble(3, uF.parseToDouble(getSalary_head_value()[i]));
				pst.setDate	(4,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(5, 1);
				pst.setString(6, "M");
				pst.setBoolean(7, uF.parseToBoolean(isDisplay));
//				pst.setInt(8, uF.parseToInt(getCCID()));
				pst.setInt(8, 0);
//				pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDate(9, uF.getDateFormat(strEmpJoiningDate, DATE_FORMAT));
				pst.setString(10, hmEarningDeductionMap.get(getSalary_head_id()[i]));
				pst.setString(11, hmSalaryTypeMap.get(getSalary_head_id()[i]));
				pst.setInt(12, uF.parseToInt(strEmpGradeId));
//				System.out.println("pst insertEmpSalaryDetails==>"+pst);
				pst.execute();
				pst.close();
			}
			
			CF.updateNextEmpSalaryEffectiveDate(con, uF, uF.parseToInt(getEmpId()), strEmpJoiningDate, DATE_FORMAT);
			
			pst = con.prepareStatement("update employee_official_details set is_disable_sal_calculate=? where emp_id = ?");
			pst.setBoolean(1, getDisableSalaryStructure());
			pst.setInt(2, uF.parseToInt(getEmpId()));
			pst.execute();
			pst.close();
			
			int ServiceNo = uF.parseToInt((String)session.getAttribute("ServicesLinkNo"));
//			session.setAttribute("ServicesLinkNo", (ServiceNo-1)+"");    // Uncomment this code if you wish to use salary cost center wise.
			session.setAttribute("ServicesLinkNo", 1+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void updateEmployeeSalaryDetailsByGrade(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
//			System.out.println("in updateEmployeeSalaryDetailsByGrade====>");
			
			con = db.makeConnection(con);
			
			String strEmpGradeId = CF.getEmpGradeId(con, getEmpId());
			
			pst = con.prepareStatement("select * from salary_details where grade_id=?");
			pst.setInt(1, uF.parseToInt(strEmpGradeId));
			rs = pst.executeQuery();
			Map<String, String> hmEarningDeductionMap = new HashMap<String, String>();
			Map<String, String> hmSalaryTypeMap = new HashMap<String, String>();
			while(rs.next()) {
				hmEarningDeductionMap.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
				hmSalaryTypeMap.put(rs.getString("salary_head_id"), rs.getString("salary_type"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from emp_salary_details where effective_date = ? and emp_id = ?");
			pst.setDate(1, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			pst.setInt(2, uF.parseToInt(getEmpId()));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			boolean isDateExist = false;
			while(rs.next()) {
				isDateExist = true;
			}
			rs.close();
			pst.close();
			
			
			if(isDateExist) {
				pst = con.prepareStatement("delete from emp_salary_details where effective_date = ? and emp_id = ?");
				pst.setDate(1, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setInt(2, uF.parseToInt(getEmpId()));
				pst.execute();
				pst.close();
			}  
			
			
			for(int i=0; emp_salary_id != null && i<emp_salary_id.length; i++) {
				int cnt = 0;
				
				if(cnt==0) {
					String isDisplay = (String) request.getParameter("isDisplay_"+getSalary_head_id()[i]);
					pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id, salary_head_id, " +
							"amount, entry_date, user_id, pay_type, isdisplay, service_id, effective_date, " +
							"earning_deduction, salary_type,grade_id) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?)");
					pst.setInt(1, uF.parseToInt(getEmpId()));
					pst.setInt(2, uF.parseToInt(getSalary_head_id()[i]));
					pst.setDouble(3, uF.parseToDouble(getSalary_head_value()[i]));
					pst.setDate	(4, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
					pst.setInt(5, 1);
					pst.setString(6, "M");
					pst.setBoolean(7, uF.parseToBoolean(isDisplay));
//					pst.setInt(8, uF.parseToInt(getCCID()));
					pst.setInt(8, 0);
					pst.setDate(9, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
					pst.setString(10, hmEarningDeductionMap.get(getSalary_head_id()[i]));
					pst.setString(11, hmSalaryTypeMap.get(getSalary_head_id()[i]));
					pst.setInt(12, uF.parseToInt(strEmpGradeId));
//					System.out.println("pst updateEmployeeSalaryDetails==>"+pst);
					pst.execute();
					pst.close();
				}
			}

			CF.updateNextEmpSalaryEffectiveDate(con, uF, uF.parseToInt(getEmpId()), getEffectiveDate(), DATE_FORMAT);
			
			pst = con.prepareStatement("update employee_official_details set is_disable_sal_calculate=? where emp_id = ?");
			pst.setBoolean(1, getDisableSalaryStructure());
			pst.setInt(2, uF.parseToInt(getEmpId()));
			pst.execute();
			pst.close();
			
			AddEmployee objAddEmp = new AddEmployee();
			objAddEmp.request = request;
			objAddEmp.session = session;
			objAddEmp.CF = CF;
			boolean isFilledStatus = false;
			
			if(!isFilledStatus) {
				boolean flag = objAddEmp.checkEmpStatus(con,uF.parseToInt(getEmpId()));
				if(flag) {
					objAddEmp.updateEmpFilledStatus(con,uF.parseToInt(getEmpId()));
					objAddEmp.updateEmpLiveStatus(con,uF.parseToInt(getEmpId()));
					if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
						objAddEmp.insertEmpActivity(con,getEmpId(), CF, strSessionEmpId, ACTIVITY_SALARY_APPROVAL_ID);
					}
					
					if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
						String strDomain = request.getServerName().split("\\.")[0];
						Notifications nF = new Notifications(N_NEW_EMPLOYEE, CF);
						nF.setDomain(strDomain);
						nF.request = request;
						nF.setStrEmpId(getEmpId()+"");
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setEmailTemplate(true);
						nF.sendNotifications();
					}
				}
			}
			
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void updateEmployeeSalaryDetails() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		
		try {
//			System.out.println("in updateEmployeeSalaryDetails====>");
			//code for getting userid
//			System.out.println("updateEmployeeSalaryDetails: getEmpId==>"+getEmpId());
			con = db.makeConnection(con);
			
			String strEmpLevelId = CF.getEmpLevelId(con, getEmpId());
			
			pst = con.prepareStatement("select * from salary_details where level_id=?");
			pst.setInt(1, uF.parseToInt(strEmpLevelId));
			rs = pst.executeQuery();
			Map<String, String> hmEarningDeductionMap = new HashMap<String, String>();
			Map<String, String> hmSalaryTypeMap = new HashMap<String, String>();
			while(rs.next()) {
				hmEarningDeductionMap.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
				hmSalaryTypeMap.put(rs.getString("salary_head_id"), rs.getString("salary_type"));
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select * from emp_salary_details where entry_date = ? and emp_id = ?");
//			pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
//			pst.setInt(2, uF.parseToInt(getEmpId()));
//			rs = pst.executeQuery();
//			boolean isCurrentDateExist = false;
//			while(rs.next()) {
//				isCurrentDateExist = true;
//			}
//			rs.close();
//			pst.close();
//			
//			
//			if(isCurrentDateExist) {
//				pst = con.prepareStatement("delete from emp_salary_details where entry_date = ? and emp_id = ?");
//				pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
//				pst.setInt(2, uF.parseToInt(getEmpId()));
//				pst.execute();
//				pst.close();
//			}  
			
			pst = con.prepareStatement("select * from emp_salary_details where effective_date = ? and emp_id = ?");
			pst.setDate(1, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			pst.setInt(2, uF.parseToInt(getEmpId()));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			boolean isDateExist = false;
			while(rs.next()) {
				isDateExist = true;
			}
			rs.close();
			pst.close();
			
			
			if(isDateExist) {
				pst = con.prepareStatement("delete from emp_salary_details where effective_date = ? and emp_id = ?");
				pst.setDate(1, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setInt(2, uF.parseToInt(getEmpId()));
				pst.execute();
				pst.close();
			}  
			
			
			for(int i=0; emp_salary_id != null && i<emp_salary_id.length; i++) {
				int cnt = 0;
//				pst = con.prepareStatement(updateEmpSalaryDetails);
				/*
				if(salary_head_value.length>i) {
					
					pst.setDouble(1, uF.parseToDouble(salary_head_value[i]));
					pst.setDate	(2, uF.getDateFormat(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT), DATE_FORMAT));
//					pst.setBoolean(3, isDisplay[i]);
					
					pst.setBoolean(3, ArrayUtils.contains(isDisplay, emp_salary_id[i])>=0);
					
					
					pst.setInt(4, uF.parseToInt(emp_salary_id[i]));
					
					System.out.println("pst updateEmpSalaryDetails==>"+pst);
					
					
					cnt = pst.executeUpdate();
					
				}
				*/
				
				if(cnt==0) {
					String isDisplay = (String) request.getParameter("isDisplay_"+getSalary_head_id()[i]);
					pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id, salary_head_id, " +
							"amount, entry_date, user_id, pay_type, isdisplay, service_id, effective_date, " +
							"earning_deduction, salary_type,level_id) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?)");
					pst.setInt(1, uF.parseToInt(getEmpId()));
					pst.setInt(2, uF.parseToInt(getSalary_head_id()[i]));
					pst.setDouble(3, uF.parseToDouble(getSalary_head_value()[i]));
					pst.setDate	(4, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
					pst.setInt(5, 1);
					pst.setString(6, "M");
//					pst.setBoolean(7, isDisplay[i]);
//					pst.setBoolean(7, true);
//					pst.setBoolean(7, (isDisplay!= null  && isDisplay.length > 0) ? ArrayUtils.contains(isDisplay, emp_salary_id[i])>=0 : false);
					pst.setBoolean(7, uF.parseToBoolean(isDisplay));
//					pst.setInt(8, uF.parseToInt(getCCID()));
					pst.setInt(8, 0);
					pst.setDate(9, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
					pst.setString(10, hmEarningDeductionMap.get(getSalary_head_id()[i]));
					pst.setString(11, hmSalaryTypeMap.get(getSalary_head_id()[i]));
					pst.setInt(12, uF.parseToInt(strEmpLevelId));
//					System.out.println("pst updateEmployeeSalaryDetails==>"+pst);
					pst.execute();
					pst.close();
				}
			}
			
			CF.updateNextEmpSalaryEffectiveDate(con, uF, uF.parseToInt(getEmpId()), getEffectiveDate(), DATE_FORMAT);
			
			pst = con.prepareStatement("update employee_official_details set is_disable_sal_calculate=? where emp_id = ?");
			pst.setBoolean(1, getDisableSalaryStructure());
			pst.setInt(2, uF.parseToInt(getEmpId()));
			pst.execute();
			pst.close();

	//===start parvez date: 12-01-2022===
			
			String strEmpJoiningDate = CF.getEmpJoiningDate(con, uF, uF.parseToInt(getEmpId()));			
			String strOrgId = CF.getEmpOrgId(con, uF, getEmpId());
		
			boolean isJoiningBonus = CF.getFeatureManagementStatus(request, uF, F_ENABLE_JOINING_BONUS_DETAILS);
			if(isJoiningBonus){
				String joiningBonusDetails = "";
				pst = con.prepareStatement("select * from candidate_application_details where candididate_emp_id = ?");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				rs = pst.executeQuery();
				while(rs.next()) {
					joiningBonusDetails = rs.getString("joining_bonus_amount_details");
				}
				rs.close();
				pst.close();
				
				String[] strPayCycleDate = CF.getPayCycleFromDate(con, strEmpJoiningDate, CF.getStrTimeZone(), CF, strOrgId);
				String startDate = "";
				String endDate = "";
				String strPC = "";
				if(strPayCycleDate !=null && strPayCycleDate.length > 0) {
					startDate = strPayCycleDate[0];
					endDate = strPayCycleDate[1];
					strPC = strPayCycleDate[2];
					//System.out.println("ESD/1421--strPC="+strPC+"--startDate="+startDate+"---endDate="+endDate);
				}
				
				if(joiningBonusDetails != null && joiningBonusDetails.length() > 0){
					String[] strJoiningBonus = joiningBonusDetails.split("::");
					pst = con.prepareStatement("insert into otherearning_individual_details (emp_id, pay_paycycle, percent, salary_head_id, amount, pay_amount, added_by,  entry_date, paid_from, paid_to,is_approved,earning_deduction) " +
							"values (?,?,?,?,?,?,?,?,?,?,?,?)");
					pst.setInt(1, uF.parseToInt(getEmpId()));
					pst.setInt(2, uF.parseToInt(strPC));
					pst.setDouble(3, 0);
					pst.setInt(4, uF.parseToInt(strJoiningBonus[0].trim()));
					pst.setDouble(5, uF.parseToDouble(strJoiningBonus[1].trim()));
					pst.setDouble(6, uF.parseToDouble(strJoiningBonus[1].trim()));
					pst.setInt(7, uF.parseToInt(strSessionEmpId));
					pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setDate(9, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(10, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setInt(11, 2);
					pst.setString(12, "E");
					pst.execute();
		            pst.close();
				}
			}
			
	//===end parvez date: 12-01-2022===

			
			AddEmployee objAddEmp = new AddEmployee();
			objAddEmp.request = request;
			objAddEmp.session = session;
			objAddEmp.CF = CF;
//			boolean isFilledStatus = CF.getEmpFilledStatus(con, getEmpId()); 
			boolean isFilledStatus = false;
			
			if(!isFilledStatus) {
				boolean flag = objAddEmp.checkEmpStatus(con,uF.parseToInt(getEmpId()));
				if(flag) {
					objAddEmp.updateEmpFilledStatus(con,uF.parseToInt(getEmpId()));
					objAddEmp.updateEmpLiveStatus(con,uF.parseToInt(getEmpId()));
					if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
						objAddEmp.insertEmpActivity(con,getEmpId(), CF, strSessionEmpId, ACTIVITY_SALARY_APPROVAL_ID);
					}
					
					if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
						String strDomain = request.getServerName().split("\\.")[0];
						Notifications nF = new Notifications(N_NEW_EMPLOYEE, CF);
						nF.setDomain(strDomain);
						nF.request = request;
						nF.setStrEmpId(getEmpId()+"");
	//					nF.setStrHostAddress(request.getRemoteHost());
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setEmailTemplate(true);
						nF.sendNotifications();
					}
				}
			}
			
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	public boolean viewUpdateEmployeeSalaryDetails() {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF= new UtilityFunctions();
		boolean flag = false;
		try {
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];
			
			con = db.makeConnection(con);
//			System.out.println("in viewUpdateEmployeeSalaryDetails ...");
			String levelId = CF.getEmpLevelId(con, getEmpId());
			String salaryBandId = CF.getSalaryBandId(con, getCtcAmt(), levelId);
			String strOrgId = CF.getEmpOrgId(con, uF, getEmpId());
			
			if(uF.parseToDouble(getCtcAmt())== 0) {
				pst = con.prepareStatement("SELECT * FROM emp_salary_details WHERE emp_id=? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
					"WHERE salary_head_id in ("+CTC+") and emp_id=? and is_approved = true and isdisplay=true and level_id=?) and salary_head_id in ("+CTC+") AND effective_date<=? and level_id=?");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.setInt(2, uF.parseToInt(getEmpId()));
				pst.setInt(3, uF.parseToInt(levelId));
				pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(5, uF.parseToInt(levelId));
				rs = pst.executeQuery();
				String ctcAmt = "0";
				while(rs.next()){
					ctcAmt = rs.getString("amount");
				}
				rs.close();
				pst.close();
				salaryBandId = CF.getSalaryBandId(con, ctcAmt, levelId);
			}
			
//			System.out.println("salaryBandId ====================>> " + salaryBandId);
			request.setAttribute("levelId", levelId);
			request.setAttribute("strOrgId", strOrgId);
			
			Map hmEmpMertoMap = new HashMap();
			Map hmEmpWlocationMap = new HashMap();
			Map hmEmpStateMap = new HashMap();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
			
			Map<String, String> hmEmpAnnualVarPolicyAmount = CF.getEmpAnnualVariablePolicyMonthAmount(con, uF, getEmpId(), strFinancialYearStart, strFinancialYearEnd);
			if(hmEmpAnnualVarPolicyAmount == null) hmEmpAnnualVarPolicyAmount = new HashMap<String, String>();
			request.setAttribute("hmEmpAnnualVarPolicyAmount", hmEmpAnnualVarPolicyAmount);
			
			String strStateId = (String)hmEmpStateMap.get(getEmpId());
			
			setDisableSalaryStructure(CF.getEmpDisableSalaryCalculation(con,uF,getEmpId()));
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id=? and salary_band_id=? and is_annual_variable=true and (is_delete is null or is_delete=false) order by weight");
			pst.setInt(1, uF.parseToInt(levelId));
			pst.setInt(2, uF.parseToInt(salaryBandId));
			rs = pst.executeQuery();
			List<String> alAnnualSalaryHead = new ArrayList<String>();
			while(rs.next()) {
				if(!alAnnualSalaryHead.contains(rs.getString("salary_head_id"))) {
					alAnnualSalaryHead.add(rs.getString("salary_head_id"));
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("alAnnualSalaryHead", alAnnualSalaryHead);
			
			String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String[] strPayCycleDate = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrgId);
			double dblReimbursementCTC = 0.0d;
			double dblReimbursementCTCOptional = 0.0d;
			if(strPayCycleDate !=null && strPayCycleDate.length > 0) {
				String startDate = strPayCycleDate[0];
				String endDate = strPayCycleDate[1];
				String strPC = strPayCycleDate[2];
			
				dblReimbursementCTC = CF.getReimbursementCTCHeadTotalAmount(con, uF, uF.parseToInt(getEmpId()), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(levelId));
				request.setAttribute("dblReimbursementCTC", ""+dblReimbursementCTC);
				
				dblReimbursementCTCOptional = CF.getReimbursementCTCOptinalHeadTotalAmount(con, uF, uF.parseToInt(getEmpId()), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(levelId));
				request.setAttribute("dblReimbursementCTCOptional", ""+dblReimbursementCTCOptional);
			}
			
			
			List<List<String>> alE = new ArrayList<List<String>>();
			
			
			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con,uF.parseToInt(levelId));
			int nEarningCnt = 0;
			int nDisplay = 0; 
			if(uF.parseToDouble(getCtcAmt()) > 0) {
				pst = con.prepareStatement("SELECT * from salary_details sd WHERE sd.level_id=? and salary_band_id=? " +
						"and (sd.is_delete is null or sd.is_delete=false) order by sd.earning_deduction desc, weight");
				pst.setInt(1, uF.parseToInt(levelId));
				pst.setInt(2, uF.parseToInt(salaryBandId));
				System.out.println("pst viewSalaryDetails ===>> " + pst);
				rs = pst.executeQuery();
				String alHeadId = "";
				Map<String, String> hmSalaryAmountMap = new HashMap<String, String>();
				List alSalaryDuplicationTracer = new ArrayList();
				Map<String, String> hmTotal = new HashMap<String, String>();
				while(rs.next()) {
					
					if(uF.parseToInt(rs.getString("salary_head_id")) > 0 && uF.parseToInt(rs.getString("salary_head_id"))!=CTC && rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").trim().equals("E")) {
						nEarningCnt++;
					}
					
					nDisplay++;
					
					List<String> alInner = new ArrayList<String>();
					alInner.add("0");	//0
					String rsHeadId = rs.getString("salary_head_id");
					alInner.add(rsHeadId);	//1
					
					alInner.add(uF.showData(hmSalaryMap.get(rsHeadId), ""));	//2
					
					alInner.add(rs.getString("earning_deduction"));	//3
					alInner.add(rs.getString("salary_head_amount_type")); //4
					rsHeadId = rs.getString("sub_salary_head_id");	
					alInner.add(rsHeadId);	//5
					
					alInner.add("");	//6
					
					alInner.add((rs.getString("salary_head_amount_type") != null && rs.getString("salary_head_amount_type").equals("P")) ?  uF.formatIntoFourDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))) : uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount")))); //7
					 
					
					double dblAmount = 0;
					String strAmountType = rs.getString("salary_head_amount_type");
					if(strAmountType!=null && strAmountType.equalsIgnoreCase("P")) {
						dblAmount = rs.getDouble("salary_calculate_amount");
					} else if(strAmountType!=null && strAmountType.equalsIgnoreCase("A")) {
						dblAmount = rs.getDouble("salary_head_amount");
					}

//					System.out.println(uF.parseToInt(rs.getString("salary_head_id")) + " -- getCtcAmt() ===>> " + getCtcAmt());
					if(rs.getInt("salary_head_id") == CTC && uF.parseToDouble(getCtcAmt())>0) {
						dblAmount = uF.parseToDouble(getCtcAmt());
					}
//					System.out.println("dblAmount=="+dblAmount);
					
					
					StringBuilder sbMulcalType = new StringBuilder();
					
					if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
						String strMulCal = rs.getString("multiple_calculation");
						CF.appendMultiplePercentageCalType(uF,sbMulcalType,strMulCal,hmSalaryMap,alAnnualSalaryHead); 
						
						alInner.add(uF.formatIntoOneDecimalWithOutComma(dblAmount)); //8
					} else {
						alInner.add(uF.formatIntoOneDecimalWithOutComma(dblAmount)); //8
					}
					
					alInner.add(""); //9
					alInner.add("");	//10
					alInner.add("");	//11
					alInner.add("true");	//12
					alInner.add(rs.getString("weight"));	//13
					alInner.add(rs.getString("multiple_calculation"));	//14
					alInner.add(sbMulcalType.toString());	//15
					alInner.add(rs.getString("max_cap_amount"));	//16
					alInner.add(rs.getBoolean("is_contribution")? "T" : "F");	//17
					
					int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
					
					if(index>=0) {
						alE.remove(index);
						alE.add(index, alInner);
					} else {
						alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
						alE.add(alInner);
					}
					
					flag = true;	
				}
				rs.close();
				pst.close();
			} else {
				pst = con.prepareStatement("SELECT weight,isdisplay,pay_type,user_id,entry_date,amount," +
						"emp_salary_id,salary_head_amount,sd.earning_deduction,salary_head_amount_type," +
						"sub_salary_head_id, sd.salary_head_id as salary_head_id,multiple_calculation," +
						"salary_calculate_amount,max_cap_amount,is_contribution FROM (SELECT * FROM emp_salary_details WHERE emp_id=? " +
						"AND service_id=? AND effective_date = (SELECT MAX(effective_date) " +
						"FROM emp_salary_details WHERE emp_id=? and is_approved=true and level_id=?) " +
						"AND effective_date<=? and level_id=?) asd RIGHT JOIN salary_details sd " +
						"ON sd.salary_head_id = asd.salary_head_id WHERE sd.level_id=? and salary_band_id=? " +
						"and (sd.is_delete is null or sd.is_delete=false) order by sd.earning_deduction desc, weight");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.setInt(2, 0);  // Default Service Id
				pst.setInt(3, uF.parseToInt(getEmpId()));
				pst.setInt(4, uF.parseToInt(levelId));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(6, uF.parseToInt(levelId));
				pst.setInt(7, uF.parseToInt(levelId));
				pst.setInt(8, uF.parseToInt(salaryBandId));
				System.out.println("pst viewUpdateEmployeeSalaryDetails ===>> " + pst);
				rs = pst.executeQuery();
				String alHeadId = "";
				Map<String, String> hmSalaryAmountMap = new HashMap<String, String>();
				List alSalaryDuplicationTracer = new ArrayList();
				Map<String, String> hmTotal = new HashMap<String, String>();
				
				while(rs.next()) {
					
					if(uF.parseToInt(rs.getString("salary_head_id")) > 0 && uF.parseToInt(rs.getString("salary_head_id"))!=CTC && rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").trim().equals("E")) {
						nEarningCnt++;
					}
					
					if(uF.parseToBoolean(rs.getString("isdisplay"))) {
						nDisplay++;
					}				
					
					hmSalaryAmountMap.put(rs.getString("salary_head_id"), (rs.getString("salary_head_amount_type") != null && rs.getString("salary_head_amount_type").equals("P")) ?  uF.formatIntoFourDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))) : uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));
					
					List<String> alInner = new ArrayList<String>();
					alInner.add(uF.parseToInt(rs.getString("emp_salary_id"))+"");	//0
					String rsHeadId = rs.getString("salary_head_id");
					alInner.add(rsHeadId);	//1
					
	//				for(int i=0; i<salaryHeadList.size(); i++) {
	//					
	//					alHeadId = ((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadId();
	//					
	////					System.out.println("alHeadId==>"+alHeadId);
	//					
	//					if(rsHeadId!=null && rsHeadId.equalsIgnoreCase("0")) {
	//						alInner.add("0");	//2
	//						break;
	//					}else if(rsHeadId!=null && rsHeadId.equalsIgnoreCase(alHeadId)) {
	//						alInner.add(((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName());	//2
	//						break;
	//					}
	//				}
					
					alInner.add(uF.showData(hmSalaryMap.get(rsHeadId), ""));	//2
					
					alInner.add(rs.getString("earning_deduction"));	//3
					alInner.add(rs.getString("salary_head_amount_type")); //4
					rsHeadId = rs.getString("sub_salary_head_id");	
					alInner.add(rsHeadId);	//5
					
	//				for(int i=0; i<salaryHeadList.size(); i++) {
	//					
	//					alHeadId = ((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadId();
	//					if(alHeadId==null)continue;
	//					
	//					
	//					if(rsHeadId.equalsIgnoreCase("0")) {
	//						alInner.add("0");	//6
	//						break;
	//					}else if(rsHeadId.equalsIgnoreCase(alHeadId)) {
	//
	//						if(uF.parseToInt(rsHeadId)==1) { 
	//							
	//							if(uF.parseToInt(rs.getString("salary_head_id"))>2) {
	////								alInner.add(((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName()+" + DA ");	//6
	//								
	//								StringBuilder sb = new StringBuilder();
	//								if(hmSalaryAmountMap.containsKey(DA+"")) {
	//									sb.append(" + "+hmSalaryMap.get(DA+""));
	//								}
	//								if(hmSalaryAmountMap.containsKey(DA1+"")) {
	//									sb.append(" + "+hmSalaryMap.get(DA1+""));
	//								}
	//								alInner.add(((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName()+sb.toString());	//6
	//								
	//							} else {   
	//								alInner.add(((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName());	//6
	//							}
	//							
	//							
	//						} else {
	//							alInner.add(((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName());	//6
	//						}
	//						
	//						
	//						break;
	//					}
	//				}
					alInner.add("");	//6
					
					alInner.add((rs.getString("salary_head_amount_type") != null && rs.getString("salary_head_amount_type").equals("P")) ?  uF.formatIntoFourDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))) : uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount")))); //7
					 
					
					double dblAmount = 0;
					if(rs.getString("amount")==null) {
						String strAmountType = rs.getString("salary_head_amount_type");
						if(strAmountType!=null && strAmountType.equalsIgnoreCase("P")) {
							dblAmount = rs.getDouble("salary_calculate_amount");
						}else if(strAmountType!=null && strAmountType.equalsIgnoreCase("A")) {
							dblAmount = rs.getDouble("salary_head_amount");
						}
					} else {
						dblAmount = rs.getDouble("amount");
					}
	
	//				System.out.println(uF.parseToInt(rs.getString("salary_head_id")) + " -- getCtcAmt() ===>> " + getCtcAmt());
					if(rs.getInt("salary_head_id") == CTC && uF.parseToDouble(getCtcAmt())>0) {
						dblAmount = uF.parseToDouble(getCtcAmt());
					}
	//				System.out.println("dblAmount=="+dblAmount);
					
					
					StringBuilder sbMulcalType = new StringBuilder();
	//				ApprovePayroll objAP = new ApprovePayroll();
	//				objAP.CF = CF;
	//				objAP.session = session;
	//				objAP.request = request; 
	//				
	//				if(uF.parseToBoolean(rs.getString("isdisplay"))) {
	//					switch(rs.getInt("salary_head_id")) {
	//					
	//						case TDS:
	//							
	//	//						dblAmount = objAP.calculateTDS(dblGross, dblCess1, dblCess2, dblHRA, dblBasicDA, nPayMonth, strPaycycleStart, strFinancialYearStart, strFinancialYearEnd, strEmpId, strGender, strAge, strWLocationStateId, hmEmpExemptionsMap, hmFixedExemptions, hmEmpMertoMap, hmEmpRentPaidMap, hmPaidSalaryDetails, hmTotal, hmSalaryDetails);
	//						
	//	//						System.out.println("======dblAmount"+dblAmount);
	//							alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblAmount));	//6
	//	//						System.out.println("======dblAmount"+uF.formatIntoTwoDecimal(dblAmount));
	//							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
	//							
	//						break; 
	//					
	//						case EMPLOYEE_EPF :
	//								
	////							dblAmount = objAP.calculateEEPF(con, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmTotal, null, null, null, false);
	//							dblAmount = objAP.calculateEEPF(con, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmTotal, getEmpId(), null, null, false, null);
	////							System.out.println("dblAmount=======>"+dblAmount);
	//							alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblAmount));	//6
	//							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
	//							
	//						break;
	//						
	//						case EMPLOYER_EPF :
	//							
	//							dblAmount = objAP.calculateERPF(con, CF, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, getEmpId(), null, null, false, null);
	//						
	//							alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblAmount));	//6
	//							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
	//							
	//						break;  
	//						
	//						case EMPLOYER_ESI :
	//							
	//							dblAmount = objAP.calculateERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId,getEmpId(), null, null);
	//						
	//							alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblAmount));	//6
	//							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
	//							
	//						break;
	//						
	//						case EMPLOYEE_ESI :
	//							
	//							dblAmount = objAP.calculateEEESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, null);
	//						
	//							alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblAmount));	//6
	//							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
	//							
	//						break;
	//						
	//						case MOBILE_RECOVERY:
	//							
	//							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
	//							alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblAmount));	//6
	//							break;
	//							
	//						default:
	//							alInner.add(uF.formatIntoOneDecimalWithOutComma(dblAmount));	//6
	//							
	//							if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
	//								double dbl = uF.parseToDouble((String)hmTotal.get(rs.getString("sub_salary_head_id")));
	//								if(uF.parseToInt(rs.getString("sub_salary_head_id")) == REIMBURSEMENT_CTC) {
	//									dbl = dblReimbursementCTC;
	//								}
	//								dbl = dbl * rs.getDouble("salary_head_amount") /100;
	//								hmTotal.put(rs.getString("salary_head_id"), dbl+"");
	//							}else if("M".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
	//								String strMulCal = rs.getString("multiple_calculation");
	//								String[] strMulCalTemp = strMulCal.split(",");
	//
	//								CF.appendMultipleCalType(uF,sbMulcalType,strMulCalTemp,hmSalaryMap,alAnnualSalaryHead);
	//								
	//								double dblMulAmt = CF.getMultipleCalAmount(uF,strMulCalTemp[0].trim(),hmTotal.get(strMulCalTemp[0].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//								if(strMulCalTemp[1].trim().equals("+")) {
	//									dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[2].trim(),hmTotal.get(strMulCalTemp[2].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//								} else if(strMulCalTemp[1].trim().equals("-")) {
	//									dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[2].trim(),hmTotal.get(strMulCalTemp[2].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//								}
	//								if(strMulCalTemp[3].trim().equals("+")) {
	//									dblMulAmt = dblMulAmt +CF.getMultipleCalAmount(uF,strMulCalTemp[4].trim(),hmTotal.get(strMulCalTemp[4].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//								} else if(strMulCalTemp[3].trim().equals("-")) {
	//									dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[4].trim(),hmTotal.get(strMulCalTemp[4].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//								}
	//								if(strMulCalTemp[5].trim().equals("+")) {
	//									dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[6].trim(),hmTotal.get(strMulCalTemp[6].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//								} else if(strMulCalTemp[5].trim().equals("-")) {
	//									dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[6].trim(),hmTotal.get(strMulCalTemp[6].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//								}
	//								if(strMulCalTemp[7].trim().equals("+")) {
	//									dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[8].trim(),hmTotal.get(strMulCalTemp[8].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//								} else if(strMulCalTemp[7].trim().equals("-")) {
	//									dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[8].trim(),hmTotal.get(strMulCalTemp[8].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//								}
	//								if(strMulCalTemp[9].trim().equals("+")) {
	//									dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[10].trim(),hmTotal.get(strMulCalTemp[10].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//								} else if(strMulCalTemp[9].trim().equals("-")) {
	//									dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[10].trim(),hmTotal.get(strMulCalTemp[10].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//								}
	//								if(strMulCalTemp[11].trim().equals("+")) {
	//									dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[12].trim(),hmTotal.get(strMulCalTemp[12].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//								} else if(strMulCalTemp[11].trim().equals("-")) {
	//									dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[12].trim(),hmTotal.get(strMulCalTemp[12].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//								}
	//								if(strMulCalTemp[13].trim().equals("+")) {
	//									dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[14].trim(),hmTotal.get(strMulCalTemp[14].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//								} else if(strMulCalTemp[13].trim().equals("-")) {
	//									dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[14].trim(),hmTotal.get(strMulCalTemp[14].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//								}
	//								if(strMulCalTemp[15].trim().equals("+")) {
	//									dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[16].trim(),hmTotal.get(strMulCalTemp[16].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//								} else if(strMulCalTemp[15].trim().equals("-")) {
	//									dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[16].trim(),hmTotal.get(strMulCalTemp[16].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//								}
	//								if(strMulCalTemp[17].trim().equals("+")) {
	//									dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[18].trim(),hmTotal.get(strMulCalTemp[18].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//								} else if(strMulCalTemp[17].trim().equals("-")) {
	//									dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[18].trim(),hmTotal.get(strMulCalTemp[18].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//								}
	//								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblMulAmt));
	//							} else {
	//								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoOneDecimalWithOutComma(dblAmount));
	//							}
	//							
	//	//						hmTotal.put(rs.getString("salary_head_id"), rs.getString("salary_head_amount"));
	//						break;
	//					}
	//				} else if("M".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
	//					String strMulCal = rs.getString("multiple_calculation");
	//					String[] strMulCalTemp = strMulCal.split(",");
	//
	//					CF.appendMultipleCalType(uF,sbMulcalType,strMulCalTemp,hmSalaryMap,alAnnualSalaryHead);
	//					
	//					double dblMulAmt = CF.getMultipleCalAmount(uF,strMulCalTemp[0].trim(),hmTotal.get(strMulCalTemp[0].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//					if(strMulCalTemp[1].trim().equals("+")) {
	//						dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[2].trim(),hmTotal.get(strMulCalTemp[2].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//					} else if(strMulCalTemp[1].trim().equals("-")) {
	//						dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[2].trim(),hmTotal.get(strMulCalTemp[2].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//					}
	//					if(strMulCalTemp[3].trim().equals("+")) {
	//						dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[4].trim(),hmTotal.get(strMulCalTemp[4].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//					} else if(strMulCalTemp[3].trim().equals("-")) {
	//						dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[4].trim(),hmTotal.get(strMulCalTemp[4].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//					}
	//					if(strMulCalTemp[5].trim().equals("+")) {
	//						dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[6].trim(),hmTotal.get(strMulCalTemp[6].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//					} else if(strMulCalTemp[5].trim().equals("-")) {
	//						dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[6].trim(),hmTotal.get(strMulCalTemp[6].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//					}
	//					if(strMulCalTemp[7].trim().equals("+")) {
	//						dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[8].trim(),hmTotal.get(strMulCalTemp[8].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//					} else if(strMulCalTemp[7].trim().equals("-")) {
	//						dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[8].trim(),hmTotal.get(strMulCalTemp[8].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//					}
	//					if(strMulCalTemp[9].trim().equals("+")) {
	//						dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[10].trim(),hmTotal.get(strMulCalTemp[10].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//					} else if(strMulCalTemp[9].trim().equals("-")) {
	//						dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[10].trim(),hmTotal.get(strMulCalTemp[10].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//					}
	//					if(strMulCalTemp[11].trim().equals("+")) {
	//						dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[12].trim(),hmTotal.get(strMulCalTemp[12].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//					} else if(strMulCalTemp[11].trim().equals("-")) {
	//						dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[12].trim(),hmTotal.get(strMulCalTemp[12].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//					}
	//					if(strMulCalTemp[13].trim().equals("+")) {
	//						dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[14].trim(),hmTotal.get(strMulCalTemp[14].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//					} else if(strMulCalTemp[13].trim().equals("-")) {
	//						dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[14].trim(),hmTotal.get(strMulCalTemp[14].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//					}
	//					if(strMulCalTemp[15].trim().equals("+")) {
	//						dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[16].trim(),hmTotal.get(strMulCalTemp[16].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//					} else if(strMulCalTemp[15].trim().equals("-")) {
	//						dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[16].trim(),hmTotal.get(strMulCalTemp[16].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//					}
	//					if(strMulCalTemp[17].trim().equals("+")) {
	//						dblMulAmt = dblMulAmt + CF.getMultipleCalAmount(uF,strMulCalTemp[18].trim(),hmTotal.get(strMulCalTemp[18].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//					} else if(strMulCalTemp[17].trim().equals("-")) {
	//						dblMulAmt = dblMulAmt - CF.getMultipleCalAmount(uF,strMulCalTemp[18].trim(),hmTotal.get(strMulCalTemp[18].trim()),hmEmpAnnualVarPolicyAmount,dblReimbursementCTC);
	//					}
	//					hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblMulAmt));
	//					alInner.add(uF.formatIntoOneDecimalWithOutComma(dblMulAmt));
	//				} else {
	////					alInner.add("0");	//6
	//					alInner.add(uF.formatIntoOneDecimalWithOutComma(dblAmount));
	//				}
					
					if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
						String strMulCal = rs.getString("multiple_calculation");
						CF.appendMultiplePercentageCalType(uF,sbMulcalType,strMulCal,hmSalaryMap,alAnnualSalaryHead); 
						
						alInner.add(uF.formatIntoOneDecimalWithOutComma(dblAmount)); //8
					} else {
						alInner.add(uF.formatIntoOneDecimalWithOutComma(dblAmount)); //8
					}
					
					alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat())); //9
					alInner.add(rs.getString("user_id"));	//10
					alInner.add(rs.getString("pay_type"));	//11
					alInner.add(uF.parseToBoolean(rs.getString("isdisplay"))+"");	//12
					alInner.add(rs.getString("weight"));	//13
					alInner.add(rs.getString("multiple_calculation"));	//14
					alInner.add(sbMulcalType.toString());	//15
					alInner.add(rs.getString("max_cap_amount"));	//16
					alInner.add(rs.getBoolean("is_contribution")? "T" : "F");	//17
					
					int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
					
					if(index>=0) {
						alE.remove(index);
						alE.add(index, alInner);
					} else {
						alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
						alE.add(alInner);
					}
					
	//				System.out.println("alInner ===>> " + alInner);
					flag = true;	
				}
				rs.close();
				pst.close();
			}
			
			setEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
			
//			System.out.println("hmTotal ===>> " + hmTotal);  
//			System.out.println("reportList alE ===>> " + alE.toString());  
			request.setAttribute("reportList", alE);
			request.setAttribute("nEarningCnt", ""+nEarningCnt);
			
			boolean displayFlag = false;
			if(nDisplay == 0) {
				displayFlag = true;
			}
			request.setAttribute("displayFlag", ""+displayFlag);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}

	
	public double calculateERPF(CommonFunctions CF, UtilityFunctions uF, Map<String, String> hmTotal, String strEmpId) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		double dblEPS1 = 0;
		double dblEPS = 0;
		double dblEPF = 0;
		double dblEDLI = 0;
		
		double dblEPFAdmin = 0;
		double dblEDLIAdmin = 0;
		
		double dblTotalEPF = 0;
		double dblTotalEDLI = 0;
		
		Database db = null;
		try {
			if(con==null) {
				db = new Database();
				db.setRequest(request);
				con = db.makeConnection(con);
			}
			
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String []strFinancialYear = CF.getFinancialYear(con, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT), CF, uF);
			if(strFinancialYear!=null) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? " +
					"and org_id in (select org_id from employee_official_details where emp_id=?) and level_id in (select ld.level_id from grades_details gd, " +
					"level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  " +
					"and gd.grade_id in (select grade_id from employee_official_details where emp_id=? ))");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));	
			pst.setInt(4, uF.parseToInt(strEmpId)); 
			rs = pst.executeQuery();
			
			double dblERPFAmount = 0;
			double dblERPSAmount = 0;
			double dblERDLIAmount = 0;
			double dblPFAdminAmount = 0;
			double dblEDLIAdminAmount = 0;
			double dblEPFMaxAmount = 0;
			double dblEPRMaxAmount = 0;
			double dblEPSMaxAmount = 0;
			double dblEDLIMaxAmount = 0;
			String strSalaryHeads = null;
			
			boolean erpfContributionchbox = false;
			boolean erpsContributionchbox = false;
			boolean pfAdminChargeschbox = false;
			boolean edliAdminChargeschbox = false;
			boolean erdliContributionchbox = false;
			Map<String, String> hmERPFData = new HashMap<String, String>();
			while(rs.next()) {   
				
				hmERPFData.put("dblERPFAmount", rs.getString("erpf_contribution"));
				hmERPFData.put("dblERPSAmount", rs.getString("erps_contribution"));
				hmERPFData.put("dblERDLIAmount", rs.getString("erdli_contribution"));
				hmERPFData.put("dblPFAdminAmount", rs.getString("pf_admin_charges"));
				hmERPFData.put("dblEDLIAdminAmount", rs.getString("edli_admin_charges"));
				
				hmERPFData.put("dblEPRMaxAmount", rs.getString("erpf_max_limit"));
				hmERPFData.put("dblEPFMaxAmount", rs.getString("epf_max_limit"));
				hmERPFData.put("dblEPSMaxAmount", rs.getString("eps_max_limit"));
				hmERPFData.put("dblEDLIMaxAmount", rs.getString("edli_max_limit"));
				
				hmERPFData.put("strSalaryHeads", rs.getString("salary_head_id"));
				
				hmERPFData.put("erpfContributionchbox", rs.getString("is_erpf_contribution"));
				hmERPFData.put("erpsContributionchbox", rs.getString("is_erps_contribution"));
				hmERPFData.put("pfAdminChargeschbox", rs.getString("is_pf_admin_charges"));
				hmERPFData.put("edliAdminChargeschbox", rs.getString("is_edli_admin_charges"));
				hmERPFData.put("erdliContributionchbox", rs.getString("is_erdli_contribution"));
			}
			rs.close();
			pst.close();

			request.setAttribute("hmERPFData", hmERPFData);
			
			String []arrSalaryHeads = null;
			if(strSalaryHeads!=null) {
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			double dblAmount = 0;
			double dblAmountERPF = 0;
			double dblAmountEEPF = 0;
			double dblAmountERPS = 0;
			double dblAmountERPS1 = 0;
			double dblAmountEREDLI = 0;
			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++) {
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
			}
			
//			System.out.println("strEmpID=========>"+strEmpID);
//			System.out.println("dblAmount===="+dblAmount); 
//			System.out.println("dblEPFMaxAmount===="+dblEPFMaxAmount);
			
			if(dblAmount>=dblEPRMaxAmount) {
				dblAmountERPF = dblEPRMaxAmount;
			} else {
				dblAmountERPF = dblAmount;
			}
			
			if(dblAmount>=dblEPFMaxAmount) {
				dblAmountEEPF = dblEPFMaxAmount;
			} else {
				dblAmountEEPF = dblAmount;
			}
			
			
			dblAmountERPS1 = dblAmount;
			if(dblAmount>=dblEPSMaxAmount) {
				dblAmountERPS = dblEPSMaxAmount;
			} else {
				dblAmountERPS = dblAmount;
			}
			
			if(dblAmount>=dblEDLIMaxAmount) {
				dblAmountEREDLI = dblEDLIMaxAmount;
			} else {
				dblAmountEREDLI = dblAmount;
			}
			
			
			if(erpfContributionchbox) {
//					System.out.println("erpfContributionchbox====");
				dblEPF = (( dblERPFAmount * dblAmountERPF ) / 100);
			}
//				System.out.println("erpfContributionchbox====dblERPFAmount==>"+dblERPFAmount+"====dblAmountERPF==>"+dblAmountERPF+"====dblEPF==>"+dblEPF);
			if(erpsContributionchbox) {
				dblEPS = (( dblERPSAmount * dblAmountERPS ) / 100);
				dblEPS1 = (( dblERPSAmount * dblAmountERPS1 ) / 100);
			}
//				System.out.println("erpsContributionchbox====dblERPSAmount==>"+dblERPSAmount+"====dblAmountERPS==>"+dblAmountERPS+"====dblEPF==>"+dblEPS);	
			if(erdliContributionchbox) {
				dblEDLI = (( dblERDLIAmount * dblAmountEREDLI ) / 100);
			}
//				System.out.println("erdliContributionchbox====dblERDLIAmount==>"+dblERDLIAmount+"====dblAmountEREDLI==>"+dblAmountEREDLI+"====dblEDLI==>"+dblEDLI);
			
			if(edliAdminChargeschbox) {
				dblEDLIAdmin = (( dblEDLIAdminAmount * dblAmountEREDLI ) / 100);
			}
//				System.out.println("edliAdminChargeschbox====dblEDLIAdminAmount==>"+dblEDLIAdminAmount+"====dblAmountEREDLI==>"+dblAmountEREDLI+"====dblEDLIAdmin==>"+dblEDLIAdmin);
			
			if(pfAdminChargeschbox) {
				dblEPFAdmin = (( dblPFAdminAmount * dblAmountEEPF ) / 100);
			}
//				System.out.println("pfAdminChargeschbox====dblPFAdminAmount==>"+dblPFAdminAmount+"====dblAmountEEPF==>"+dblAmountEEPF+"====dblEPFAdmin==>"+dblEPFAdmin);

			if(CF.isEPF_Condition1()) {
//				System.out.println("isEPF_Condition1====");
				dblEPF += dblEPS1 - dblEPS;
			}
			
			dblTotalEDLI = dblEDLI + dblEDLIAdmin;
			dblTotalEPF = dblEPF + dblEPS + dblEPFAdmin;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(db!=null) {
				db.closeConnection(con);
			}
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return (dblTotalEPF + dblTotalEDLI);
	}
	
	

	public void viewEmployeeSalaryDetails() {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
//			System.out.println("in new viewEmployeeSalaryDetails ...");
			String level_id = CF.getEmpLevelId(con, getEmpId());
			String salaryBandId = CF.getSalaryBandId(con, getCtcAmt(), level_id);
			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con, uF.parseToInt(level_id));
			String strOrgId = CF.getEmpOrgId(con, uF, getEmpId());
			
			request.setAttribute("levelId", level_id);
			request.setAttribute("strOrgId", strOrgId);
			
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			strFinancialYearStart = strFinancialYearDates[0];
			strFinancialYearEnd = strFinancialYearDates[1];
			
			Map<String, String> hmEmpAnnualVarPolicyAmount = CF.getEmpAnnualVariablePolicyMonthAmount(con, uF, getEmpId(), strFinancialYearStart, strFinancialYearEnd);
			if(hmEmpAnnualVarPolicyAmount == null) hmEmpAnnualVarPolicyAmount = new HashMap<String, String>();
			request.setAttribute("hmEmpAnnualVarPolicyAmount", hmEmpAnnualVarPolicyAmount);
			
			setDisableSalaryStructure(CF.getEmpDisableSalaryCalculation(con,uF,getEmpId()));
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id=? and salary_band_id=? and is_annual_variable=true and (is_delete is null or is_delete=false) order by weight");
			pst.setInt(1, uF.parseToInt(level_id));
			pst.setInt(2, uF.parseToInt(salaryBandId));
			rs = pst.executeQuery();
			List<String> alAnnualSalaryHead = new ArrayList<String>();
			while(rs.next()) {
				if(!alAnnualSalaryHead.contains(rs.getString("salary_head_id"))) {
					alAnnualSalaryHead.add(rs.getString("salary_head_id"));
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("alAnnualSalaryHead", alAnnualSalaryHead);
			
			List<List<String>> al = new ArrayList<List<String>>();
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id=? and salary_band_id=? and (is_delete is null or is_delete=false) order by weight");
			pst.setInt(1, uF.parseToInt(level_id));
			pst.setInt(2, uF.parseToInt(salaryBandId));
			System.out.println("pst salary_details ===>> " + pst);
			rs = pst.executeQuery();
			List alSalaryDuplicationTracer = new ArrayList();
			int nEarningCnt = 0;
			while(rs.next()) {
				
				if(uF.parseToInt(rs.getString("salary_head_id")) > 0 && uF.parseToInt(rs.getString("salary_head_id"))!=CTC && rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").trim().equals("E")) {
					nEarningCnt++;
				}
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("salary_head_id"));	//0
				alInner.add(rs.getString("salary_head_name"));	//1
				alInner.add(rs.getString("earning_deduction"));	//2
				alInner.add(rs.getString("salary_head_amount_type"));	//3
				String rsHeadId = rs.getInt("sub_salary_head_id") + "";
				alInner.add(rsHeadId);	//4
				
//				String alHeadId = "";
//				for(int i=0; i<salaryHeadList.size(); i++) {
//					
//					alHeadId = ((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadId();
//					
//					if(rsHeadId.equalsIgnoreCase("0")) {
//						alInner.add("0");	//5
//						break;
//					}else if(rsHeadId.equalsIgnoreCase(alHeadId)) {
//						alInner.add(((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName());	//5
//						break;
//					}
//					
//					/*else{
//						alInner.add("0");	//5
//						break;
//					}*/
//				}
				alInner.add("0");	//5
				
				if(rs.getInt("salary_head_id") == CTC && uF.parseToDouble(getCtcAmt())>0) {
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(getCtcAmt())));	//6
				} else {
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));	//6
				}
				StringBuilder sbMulcalType = new StringBuilder();
//				 if("M".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
//					String strMulCal = rs.getString("multiple_calculation");
//					String[] strMulCalTemp = strMulCal.split(",");
//
//					CF.appendMultipleCalType(uF,sbMulcalType,strMulCalTemp,hmSalaryMap,alAnnualSalaryHead); 
//				}
				
				if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
					String strMulCal = rs.getString("multiple_calculation");
					CF.appendMultiplePercentageCalType(uF,sbMulcalType,strMulCal,hmSalaryMap,alAnnualSalaryHead); 
				} 
				 
				alInner.add(rs.getString("multiple_calculation")); //7
				alInner.add(sbMulcalType.toString()); //8
				alInner.add(rs.getString("max_cap_amount")); //9
				alInner.add(rs.getBoolean("is_contribution")? "T" : "F");	//10
				
				int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
				if(index>=0) {
					al.remove(index);
					al.add(index, alInner);
				} else {
					alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
					al.add(alInner);
				}
				
			}
			rs.close();
			pst.close();
			
//			System.out.println("reportList viewEmployeeSalaryDetails ===>> " + al);
			request.setAttribute("reportList", al);
			request.setAttribute("nEarningCnt", ""+nEarningCnt);
			
			request.setAttribute("CCID", getCCID());
			
			pst = con.prepareStatement(selectServiceV);
			pst.setInt(1, uF.parseToInt(getCCID()));
			rs = pst.executeQuery();
			String name = "";
			while(rs.next()) {
				name = rs.getString("service_name");
			}
			rs.close();
			pst.close();
			request.setAttribute("CCNAME", name);
			
			String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String[] strPayCycleDate = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrgId);
			if(strPayCycleDate !=null && strPayCycleDate.length > 0) {
				String startDate = strPayCycleDate[0];
				String endDate = strPayCycleDate[1];
				String strPC = strPayCycleDate[2];
			
				double dblReimbursementCTC = CF.getReimbursementCTCHeadTotalAmount(con, uF, uF.parseToInt(getEmpId()), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(level_id));
				request.setAttribute("dblReimbursementCTC", ""+dblReimbursementCTC);
				
				double dblReimbursementCTCOptional = CF.getReimbursementCTCOptinalHeadTotalAmount(con, uF, uF.parseToInt(getEmpId()), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(level_id));
				request.setAttribute("dblReimbursementCTCOptional", ""+dblReimbursementCTCOptional);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void removeEmpSalaryHead() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteEmpSalaryDetails);
			pst.setInt(1, uF.parseToInt((getRemoveId().split("_"))[1]));
//			System.out.println("pst for remove==>>"+pst);
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void insertEmpSalaryDetails() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		
		try {
//			System.out.println("in insertEmpSalaryDetails====>");
			//code for getting userid
			
			con = db.makeConnection(con);
			
			String strEmpJoiningDate = CF.getEmpJoiningDate(con, uF, uF.parseToInt(getEmpId()));			
			String strEmpLevelId = CF.getEmpLevelId(con, getEmpId());
			
			pst = con.prepareStatement("select * from salary_details where level_id=?");
			pst.setInt(1, uF.parseToInt(strEmpLevelId));
			rs = pst.executeQuery();
			Map<String, String> hmEarningDeductionMap = new HashMap<String, String>();
			Map<String, String> hmSalaryTypeMap = new HashMap<String, String>();
			while(rs.next()) {
				hmEarningDeductionMap.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
				hmSalaryTypeMap.put(rs.getString("salary_head_id"), rs.getString("salary_type"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from emp_salary_details where effective_date = ? and emp_id = ?");
			pst.setDate(1, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			pst.setInt(2, uF.parseToInt(getEmpId()));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			boolean isDateExist = false;
			while(rs.next()) {
				isDateExist = true;
			}
			rs.close();
			pst.close();
			
			if(isDateExist) {
				pst = con.prepareStatement("delete from emp_salary_details where effective_date = ? and emp_id = ?");
				pst.setDate(1, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setInt(2, uF.parseToInt(getEmpId()));
				pst.execute();
				pst.close();
			}  
			
			for(int i=0; i<salary_head_id.length; i++) {
				
				String isDisplay = (String) request.getParameter("isDisplay_"+getSalary_head_id()[i]);
				pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id, salary_head_id, " +
						"amount, entry_date, user_id, pay_type, isdisplay, service_id, effective_date, " +
						"earning_deduction, salary_type,level_id) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getEmpId()));
				pst.setInt(2, uF.parseToInt(getSalary_head_id()[i]));
				pst.setDouble(3, uF.parseToDouble(getSalary_head_value()[i]));
				pst.setDate	(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(5, 1);
				pst.setString(6, "M");
//				pst.setBoolean(7, (isDisplay!= null  && isDisplay.length > 0) ? ArrayUtils.contains(isDisplay, emp_salary_id[i])>=0 : false ? ArrayUtils.contains(isDisplay, emp_salary_id[i])>=0 : false);
				pst.setBoolean(7, uF.parseToBoolean(isDisplay));
//				pst.setInt(8, uF.parseToInt(getCCID()));
				pst.setInt(8, 0);
//				pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDate(9, uF.getDateFormat(strEmpJoiningDate, DATE_FORMAT));
				pst.setString(10, hmEarningDeductionMap.get(getSalary_head_id()[i]));
				pst.setString(11, hmSalaryTypeMap.get(getSalary_head_id()[i]));
				pst.setInt(12, uF.parseToInt(strEmpLevelId));
//				System.out.println("pst insertEmpSalaryDetails==>"+pst);
				pst.execute();
				pst.close();
			}
			
			CF.updateNextEmpSalaryEffectiveDate(con, uF, uF.parseToInt(getEmpId()), strEmpJoiningDate, DATE_FORMAT);
			
			pst = con.prepareStatement("update employee_official_details set is_disable_sal_calculate=? where emp_id = ?");
			pst.setBoolean(1, getDisableSalaryStructure());
			pst.setInt(2, uF.parseToInt(getEmpId()));
			pst.execute();
			pst.close();
			
			int ServiceNo = uF.parseToInt((String)session.getAttribute("ServicesLinkNo"));
//			session.setAttribute("ServicesLinkNo", (ServiceNo-1)+"");    // Uncomment this code if you wish to use salary cost center wise.
			session.setAttribute("ServicesLinkNo", 1+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
//	public String[] getIsDisplay() {
//		return isDisplay;
//	}
//
//	public void setIsDisplay(String[] isDisplay) {
//		this.isDisplay = isDisplay;
//	}

	public String[] getSalary_head_id() {
		return salary_head_id;
	}

	public void setSalary_head_id(String[] salary_head_id) {
		this.salary_head_id = salary_head_id;
	}

	public String[] getSalary_head_value() {
		return salary_head_value;
	}

	public void setSalary_head_value(String[] salary_head_value) {
		this.salary_head_value = salary_head_value;
	}
	
	
	public HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getRemoveId() {
		return removeId;
	}

	public void setRemoveId(String removeId) {
		this.removeId = removeId;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String[] getEmp_salary_id() {
		return emp_salary_id;
	}

	public void setEmp_salary_id(String[] emp_salary_id) {
		this.emp_salary_id = emp_salary_id;
	}

	public String getCCID() {
		return CCID;
	}

	public void setCCID(String cCID) {
		CCID = cCID;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String[] getSalary_type() {
		return salary_type;
	}

	public void setSalary_type(String[] salary_type) {
		this.salary_type = salary_type;
	}

	public String getBasic() {
		return basic;
	}

	public void setBasic(String basic) {
		this.basic = basic;
	}
	public boolean getDisableSalaryStructure() {
		return disableSalaryStructure;
	}

	public void setDisableSalaryStructure(boolean disableSalaryStructure) {
		this.disableSalaryStructure = disableSalaryStructure;
	}

	public String getOldGradeId() {
		return oldGradeId;
	}

	public void setOldGradeId(String oldGradeId) {
		this.oldGradeId = oldGradeId;
	}

	public String getCtcAmt() {
		return ctcAmt;
	}

	public void setCtcAmt(String ctcAmt) {
		this.ctcAmt = ctcAmt;
	}
	
}