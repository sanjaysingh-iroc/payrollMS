package com.konnect.jpms.salary;

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

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmpActivityEmpSalaryDetails extends ActionSupport implements ServletRequestAware, IStatements{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5947893602821384559L;
	
	private String[] salary_head_id;
	private String[] salary_head_value;
	private String[] isDisplay;
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
	CommonFunctions CF; 
	private String empId;
	private String strLevel;
	private String strActivity;
	private String strGrade;
	private String ctcAmt;
	private String hideCtcAmt;
	
//	private List<FillSalaryHeads> salaryHeadList;
//	private List<List<String>> al = new ArrayList<List<String>>();
	
	private boolean disableSalaryStructure;
	
	public String execute()	{
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute("empId", getEmpId());
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		
		if(getEmpId()!=null) {
//			int nEmpLevelId = CF.getEmpLevelId(getEmpId(), request);
//			salaryHeadList = new ArrayList<FillSalaryHeads>();
//			salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads(""+nEmpLevelId);
			
			request.setAttribute(PAGE, "/jsp/salary/EmpActivityEmpSalaryDetails.jsp");
			
			request.setAttribute("salaryStructure", CF.getStrSalaryStructure());
			int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
			if(nSalaryStrucuterType == S_GRADE_WISE) {
				if(uF.parseToInt(getStrGrade()) > 0) {
					viewEmployeeSalaryDetailsByGrade(uF);
				} else if(uF.parseToInt(getStrGrade()) == 0 && (getStrActivity() == null || (!getStrActivity().equals(ACTIVITY_PROMOTION_ID) && !getStrActivity().equals(ACTIVITY_DEMOTION_ID) && !getStrActivity().equals(ACTIVITY_GRADE_CHANGE_ID)))) {
					viewUpdateEmployeeSalaryDetailsByGrade(uF);
				}
			} else {
				if(uF.parseToInt(getStrLevel()) > 0) {
					viewLevelWiseSalaryDetails(uF);
				} else if(uF.parseToInt(getStrLevel()) == 0 && (getStrActivity() == null || (!getStrActivity().equals(ACTIVITY_PROMOTION_ID) && !getStrActivity().equals(ACTIVITY_DEMOTION_ID)))) {
					viewUpdateEmployeeSalaryDetails(uF);
				}
			}
			
//			EmployeeSalaryDetails esd = new EmployeeSalaryDetails();
//			esd.request = request;
//			esd.session = session;
//			esd.CF = CF;
//			esd.setEmpId(getEmpId());
//			esd.salaryHeadList = new ArrayList<FillSalaryHeads>();
//			esd.salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads();
//			boolean flag = esd.viewUpdateEmployeeSalaryDetails();
//			System.out.println("returning edittab");
			
			request.setAttribute("mode", request.getParameter("mode"));
			return SUCCESS;
			
		}
		return "edittab";
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
			String desigId = CF.getEmpDesigId(con, getEmpId());
			String strOrgId = CF.getEmpOrgId(con, uF, getEmpId());
			
			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMapByGrade(con, uF.parseToInt(getStrGrade()));
			
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
			
			EmployeeSalaryDetails employeeSalaryDetails = new EmployeeSalaryDetails();
			employeeSalaryDetails.request = request;
			employeeSalaryDetails.session = session;
			employeeSalaryDetails.CF = CF;
			employeeSalaryDetails.setEmpId(getEmpId());
			employeeSalaryDetails.setOldGradeId(gradeId);
			employeeSalaryDetails.getOldGradeIdSelectedSalaryHead(uF);
			
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
			pst.setInt(1, uF.parseToInt(getStrGrade()));
			pst.setInt(2, uF.parseToInt(getStrGrade()));
			if (getEffectiveDate() != null && !getEffectiveDate().equals("")) {
				pst.setDate(3, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			} else {
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			}
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
			
			
			double dblIncrementBasic = 0.0d;
			pst = con.prepareStatement("select * from basic_fitment_details where grade_id=? and trail_status=1");
			pst.setInt(1, uF.parseToInt(getStrGrade()));
//			System.out.println("pst salary_details ===>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				dblIncrementBasic = uF.parseToDouble(rs.getString("amount"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE grade_id = ? and is_annual_variable=true and (is_delete is null or is_delete=false) order by weight");
			pst.setInt(1, uF.parseToInt(getStrGrade()));
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
			pst.setInt(1, uF.parseToInt(getStrGrade()));
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
				alInner.add("0");	//5		
				
				if(uF.parseToInt(rs.getString("salary_head_id")) == BASIC) {
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblIncrementBasic));	//6
				} else if(uF.parseToInt(rs.getString("salary_head_id")) == VDA) {
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblVDAAmount));		//6
				} else {
					alInner.add((rs.getString("salary_head_amount_type") != null && rs.getString("salary_head_amount_type").equals("P")) ?  uF.formatIntoFourDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))) : uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));	//6
				}
				
				StringBuilder sbMulcalType = new StringBuilder();
				if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
					String strMulCal = rs.getString("multiple_calculation");
					CF.appendMultiplePercentageCalType(uF,sbMulcalType,strMulCal,hmSalaryMap,alAnnualSalaryHead); 
				} 
				
				alInner.add(rs.getString("multiple_calculation")); //7
				alInner.add(sbMulcalType.toString()); //8
				alInner.add(rs.getString("max_cap_amount"));	//9
				alInner.add(rs.getBoolean("is_contribution") ? "T" : "F");	//10
				
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
			
		}finally{
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
			if (getEffectiveDate() != null && !getEffectiveDate().equals("")) {
				pst.setDate(3, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			} else {
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			}
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
			
			List<List<String>> alE = new ArrayList<List<String>>();
			
			setDisableSalaryStructure(CF.getEmpDisableSalaryCalculation(con,uF,getEmpId()));
			
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
			
			pst = con.prepareStatement("SELECT weight,isdisplay,pay_type,user_id,entry_date,amount," +
					"emp_salary_id,salary_head_amount,sd.earning_deduction,salary_head_amount_type," +
					"sub_salary_head_id, sd.salary_head_id as salary_head_id,multiple_calculation," +
					"salary_calculate_amount,max_cap_amount.is_contribution FROM (SELECT * FROM emp_salary_details WHERE emp_id = ? " +
					"AND service_id = ? AND effective_date = (SELECT MAX(effective_date) " +
					"FROM emp_salary_details WHERE emp_id = ? and is_approved=true and grade_id=?) " +
					"AND effective_date <= ? and grade_id=?) asd RIGHT JOIN salary_details sd " +
					"ON sd.salary_head_id = asd.salary_head_id " +
					"WHERE sd.grade_id=? and (sd.is_delete is null or sd.is_delete=false) " +
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
//			System.out.println("pst viewUpdateEmployeeSalaryDetailsByGrade ===>> " + pst);
			Map<String, String> hmSalaryAmountMap = new HashMap<String, String>();
			List alSalaryDuplicationTracer = new ArrayList();
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
				
				alInner.add(uF.showData(hmSalaryMap.get(rsHeadId), ""));	//2
				alInner.add(rs.getString("earning_deduction"));	//3
				alInner.add(rs.getString("salary_head_amount_type")); //4
				rsHeadId = rs.getString("sub_salary_head_id");	
				alInner.add(rsHeadId);	//5
				
				alInner.add("");	//6
				
				if(rs.getString("salary_head_id") != null && rs.getInt("salary_head_id") == VDA) {
//					System.out.println("in VDA ...");
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblVDAAmount)); //7
				} else {
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount")))); //7
				}
				double dblAmount = 0;
				if(rs.getString("amount")==null) {
					String strAmountType = rs.getString("salary_head_amount_type");
					if(strAmountType!=null && strAmountType.equalsIgnoreCase("P")) {
						dblAmount = rs.getDouble("salary_calculate_amount");
					} else if(strAmountType!=null && strAmountType.equalsIgnoreCase("A")) {
						if(rs.getString("salary_head_id") != null && rs.getInt("salary_head_id") == VDA) {
							dblAmount = dblVDAAmount; 
						} else {
							dblAmount = rs.getDouble("salary_head_amount");
						}
					}
				} else {
					if(rs.getString("salary_head_id") != null && rs.getInt("salary_head_id") == VDA) {
						dblAmount = dblVDAAmount; 
					} else {
						dblAmount = rs.getDouble("amount") ;
					}
				}
				
//				System.out.println(rs.getInt("salary_head_id") + " -- dblAmount=="+dblAmount);
				
				StringBuilder sbMulcalType = new StringBuilder();
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
				alInner.add(rs.getBoolean("is_contribution") ? "T" : "F");	//17
				
				int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
				
				if(index>=0) {
					alE.remove(index);
					alE.add(index, alInner);
				} else {
					alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
					alE.add(alInner);
				}
				if(rs.getString("salary_head_id") != null && rs.getInt("salary_head_id") == VDA) {
//					System.out.println("alInner ===>> " + alInner);
				}
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
			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}
	
	
	private void viewLevelWiseSalaryDetails(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con, uF.parseToInt(getStrLevel()));
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			if(hmLevelMap == null) hmLevelMap = new HashMap<String, String>();
			request.setAttribute("strLevelName", uF.showData(hmLevelMap.get(getStrLevel()), ""));
			
			Map<String, String> hmActivity = CF.getActivityName(con);
			if(hmActivity == null) hmActivity = new HashMap<String, String>();
			request.setAttribute("strActivityName", uF.showData(hmActivity.get(getStrActivity()), ""));
			
			String strOrgId = CF.getEmpOrgId(con, uF, getEmpId());
			
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			strFinancialYearStart = strFinancialYearDates[0];
			strFinancialYearEnd = strFinancialYearDates[1];
			
			Map<String, String> hmEmpAnnualVarPolicyAmount = CF.getEmpAnnualVariablePolicyMonthAmount(con, uF, getEmpId(), strFinancialYearStart, strFinancialYearEnd);
			if(hmEmpAnnualVarPolicyAmount == null) hmEmpAnnualVarPolicyAmount = new HashMap<String, String>();
			request.setAttribute("hmEmpAnnualVarPolicyAmount", hmEmpAnnualVarPolicyAmount);
			
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
			pst.setInt(1, uF.parseToInt(getStrGrade()));
			pst.setInt(2, uF.parseToInt(getStrGrade()));
			if (getEffectiveDate() != null && !getEffectiveDate().equals("")) {
				pst.setDate(3, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			} else {
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			}
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
			
			
			String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String[] strPayCycleDate = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrgId);
			if(strPayCycleDate !=null && strPayCycleDate.length > 0) {
				String startDate = strPayCycleDate[0];
				String endDate = strPayCycleDate[1];
				String strPC = strPayCycleDate[2];
			
				double dblReimbursementCTC = CF.getReimbursementCTCHeadTotalAmount(con, uF, uF.parseToInt(getEmpId()), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(getStrLevel()));
				request.setAttribute("dblReimbursementCTC", ""+dblReimbursementCTC);
				
				double dblReimbursementCTCOptional = CF.getReimbursementCTCOptinalHeadTotalAmount(con, uF, uF.parseToInt(getEmpId()), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(getStrLevel()));
				request.setAttribute("dblReimbursementCTCOptional", ""+dblReimbursementCTCOptional);
			}
			
			setDisableSalaryStructure(CF.getEmpDisableSalaryCalculation(con,uF,getEmpId()));
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id = ? and is_annual_variable=true and (is_delete is null or is_delete=false) order by weight");
			pst.setInt(1, uF.parseToInt(getStrLevel()));
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
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id = ? and (is_delete is null or is_delete=false) order by weight");
			pst.setInt(1, uF.parseToInt(getStrLevel()));
//			System.out.println("pst salary_details ===>> " + pst);
			rs = pst.executeQuery();
			List alSalaryDuplicationTracer = new ArrayList();
			while(rs.next()) {
				
				List<String> alInner = new ArrayList<String>();
				
				alInner.add(rs.getString("salary_head_id"));	//0
				alInner.add(rs.getString("salary_head_name"));	//1
				alInner.add(rs.getString("earning_deduction"));	//2
				alInner.add(rs.getString("salary_head_amount_type"));	//3
				String rsHeadId = rs.getInt("sub_salary_head_id") + "";
				alInner.add(rsHeadId);	//4
				String alHeadId = "";
				alInner.add("0");	//5
				if(uF.parseToInt(rs.getString("salary_head_id")) == VDA) {
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblVDAAmount));		//6
				} else {
					alInner.add((rs.getString("salary_head_amount_type") != null && rs.getString("salary_head_amount_type").equals("P")) ?  uF.formatIntoFourDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))) : uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));	//6
				}
				StringBuilder sbMulcalType = new StringBuilder();
				if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
					String strMulCal = rs.getString("multiple_calculation");
					CF.appendMultiplePercentageCalType(uF,sbMulcalType,strMulCal,hmSalaryMap,alAnnualSalaryHead); 
				}
				
				alInner.add(rs.getString("multiple_calculation")); //7
				alInner.add(sbMulcalType.toString()); //8
				alInner.add(rs.getString("max_cap_amount"));	//9
				alInner.add(rs.getBoolean("is_contribution") ? "T" : "F");	//10
				
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
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private boolean viewUpdateEmployeeSalaryDetails(UtilityFunctions uF) {
		
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
			String salaryBandId = CF.getSalaryBandId(con, getCtcAmt(), levelId);
			
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
			
			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con, uF.parseToInt(levelId));
			String strOrgId = CF.getEmpOrgId(con, uF, getEmpId());
			
			Map hmEmpMertoMap = new HashMap();
			Map hmEmpWlocationMap = new HashMap();
			Map hmEmpStateMap = new HashMap();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
			
			Map<String, String> hmEmpAnnualVarPolicyAmount = CF.getEmpAnnualVariablePolicyMonthAmount(con, uF, getEmpId(), strFinancialYearStart, strFinancialYearEnd);
			if(hmEmpAnnualVarPolicyAmount == null) hmEmpAnnualVarPolicyAmount = new HashMap<String, String>();
			request.setAttribute("hmEmpAnnualVarPolicyAmount", hmEmpAnnualVarPolicyAmount);
			
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
			pst.setInt(1, uF.parseToInt(getStrGrade()));
			pst.setInt(2, uF.parseToInt(getStrGrade()));
			if (getEffectiveDate() != null && !getEffectiveDate().equals("")) {
				pst.setDate(3, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			} else {
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			}
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
			
			String strStateId = (String)hmEmpStateMap.get(getEmpId());
			
			List<List<String>> alE = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
//			System.out.println("getCtcAmt() ===>> " + getCtcAmt());
			if(uF.parseToDouble(getCtcAmt())>0) {
				pst = con.prepareStatement("SELECT * from salary_details sd WHERE sd.level_id=? and salary_band_id=? " +
						"and (sd.is_delete is null or sd.is_delete=false) order by sd.earning_deduction desc, weight");
				pst.setInt(1, uF.parseToInt(levelId));
				pst.setInt(2, uF.parseToInt(salaryBandId));
				rs = pst.executeQuery();
//				System.out.println("pst if viewUpdateEmployeeSalaryDetails ===>> " + pst);			
				String alHeadId = "";
				Map<String, String> hmSalaryAmountMap = new HashMap<String, String>();
				List alSalaryDuplicationTracer = new ArrayList();
				Map<String, String> hmTotal = new HashMap<String, String>();			
				while(rs.next()) {
					
					hmSalaryAmountMap.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));				
					
					alInner = new ArrayList<String>();
					alInner.add("0");	//0
					String rsHeadId = rs.getString("salary_head_id");
					alInner.add(rsHeadId);	//1
					
					alInner.add(uF.showData(hmSalaryMap.get(rsHeadId), ""));	//2
					
					alInner.add(rs.getString("earning_deduction"));	//3
					alInner.add(rs.getString("salary_head_amount_type")); //4
					rsHeadId = rs.getString("sub_salary_head_id");	
					alInner.add(rsHeadId);	//5
					
					alInner.add("");	//6
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount")))); //7
					double dblAmount = 0;
					String strAmountType = rs.getString("salary_head_amount_type");
					if(strAmountType!=null && strAmountType.equalsIgnoreCase("P")) {
						dblAmount = rs.getDouble("salary_calculate_amount");
					}else if(strAmountType!=null && strAmountType.equalsIgnoreCase("A")) {
						dblAmount = rs.getDouble("salary_head_amount");
					}
					
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
					
					alInner.add("");	//9
					alInner.add("");	//10
					alInner.add("");	//11
					alInner.add("true");	//12
					alInner.add(rs.getString("weight"));	//13
					alInner.add(rs.getString("multiple_calculation"));	//14
					alInner.add(sbMulcalType.toString());	//15
					alInner.add(rs.getString("max_cap_amount"));	//16
					alInner.add(rs.getBoolean("is_contribution") ? "T" : "F");	//17
					
					int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
					
					if(index>=0) {
						alE.remove(index);
						alE.add(index, alInner);
					} else {
						alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
						alE.add(alInner);
					}
					
//					System.out.println("alInner ===>> " + alInner);
					flag = true;	
				}
				rs.close();
				pst.close();
			} else {
			
				pst = con.prepareStatement("SELECT weight,isdisplay,pay_type,user_id,entry_date,amount," +
						"emp_salary_id,salary_head_amount,sd.earning_deduction,salary_head_amount_type," +
						"sub_salary_head_id, sd.salary_head_id as salary_head_id,multiple_calculation," +
						"salary_calculate_amount,max_cap_amount,is_contribution FROM (SELECT * FROM emp_salary_details WHERE emp_id=? " +
						"AND service_id = ? AND effective_date = (SELECT MAX(effective_date) " +
						"FROM emp_salary_details WHERE emp_id=? and is_approved=true and level_id=?) " +
						"AND effective_date <= ? and level_id=?) asd RIGHT JOIN salary_details sd " +
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
				rs = pst.executeQuery();
//				System.out.println("pst else viewUpdateEmployeeSalaryDetails ===>> " + pst);			
				String alHeadId = "";
				Map<String, String> hmSalaryAmountMap = new HashMap<String, String>();
				List alSalaryDuplicationTracer = new ArrayList();
				Map<String, String> hmTotal = new HashMap<String, String>();			
				while(rs.next()) {
					
					hmSalaryAmountMap.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));				
					
					alInner = new ArrayList<String>();
					alInner.add(uF.parseToInt(rs.getString("emp_salary_id"))+"");	//0
					String rsHeadId = rs.getString("salary_head_id");
					alInner.add(rsHeadId);	//1
					
					alInner.add(uF.showData(hmSalaryMap.get(rsHeadId), ""));	//2
					
					alInner.add(rs.getString("earning_deduction"));	//3
					alInner.add(rs.getString("salary_head_amount_type")); //4
					rsHeadId = rs.getString("sub_salary_head_id");	
					alInner.add(rsHeadId);	//5
					
					alInner.add("");	//6
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount")))); //7
					double dblAmount = 0;
					if(rs.getString("amount")==null) {
						String strAmountType = rs.getString("salary_head_amount_type");
						if(strAmountType!=null && strAmountType.equalsIgnoreCase("P")) {
							dblAmount = rs.getDouble("salary_calculate_amount");
						}else if(strAmountType!=null && strAmountType.equalsIgnoreCase("A")) {
							dblAmount = rs.getDouble("salary_head_amount");
						}
					} else {
						dblAmount = rs.getDouble("amount") ;
					}
					
					if(rs.getInt("salary_head_id") == CTC && uF.parseToDouble(getCtcAmt())>0) {
						dblAmount = uF.parseToDouble(getCtcAmt());
					}
	//				System.out.println("dblAmount=="+dblAmount);
					
					StringBuilder sbMulcalType = new StringBuilder();
					if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))) {
						String strMulCal = rs.getString("multiple_calculation");
						CF.appendMultiplePercentageCalType(uF,sbMulcalType,strMulCal,hmSalaryMap,alAnnualSalaryHead); 
						
						alInner.add(uF.formatIntoOneDecimalWithOutComma(dblAmount)); //8
					} else {
						alInner.add(uF.formatIntoOneDecimalWithOutComma(dblAmount)); //8
					}				
					
					alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));	//9
					alInner.add(rs.getString("user_id"));	//10
					alInner.add(rs.getString("pay_type"));	//11
					alInner.add(uF.parseToBoolean(rs.getString("isdisplay"))+"");	//12
					alInner.add(rs.getString("weight"));	//13
					alInner.add(rs.getString("multiple_calculation"));	//14
					alInner.add(sbMulcalType.toString());	//15
					alInner.add(rs.getString("max_cap_amount"));	//16
					alInner.add(rs.getBoolean("is_contribution") ? "T" : "F");	//17
					
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
			
//			System.out.println("reportList alE ===>> " + alE);
			request.setAttribute("reportList", alE);
			
//			pst = con.prepareStatement(selectServiceV);
//			pst.setInt(1, uF.parseToInt(getCCID()));
//			rs = pst.executeQuery();
//			String name = "";
//			while(rs.next()) {
//				name = rs.getString("service_name");
//			}
//			request.setAttribute("CCNAME", name);
//			
//			pst = con.prepareStatement(selectEmployee1Details);
//			pst.setInt(1, uF.parseToInt(getEmpId()));
//			rs = pst.executeQuery();
//			String empName = "";
//			
//			while(rs.next()) {
//				empName = rs.getString("emp_fname") + " " + rs.getString("emp_lname");;
//			}
//			
//			request.setAttribute("EMPNAMEFORCC", empName);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}

	public String[] getIsDisplay() {
		return isDisplay;
	}

	public void setIsDisplay(String[] isDisplay) {
		this.isDisplay = isDisplay;
	}

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
	
	
	HttpServletRequest request;
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
	public boolean getDisableSalaryStructure() {
		return disableSalaryStructure;
	}

	public void setDisableSalaryStructure(boolean disableSalaryStructure) {
		this.disableSalaryStructure = disableSalaryStructure;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getStrActivity() {
		return strActivity;
	}

	public void setStrActivity(String strActivity) {
		this.strActivity = strActivity;
	}

	public String getStrGrade() {
		return strGrade;
	}

	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}

	public String getCtcAmt() {
		return ctcAmt;
	}

	public void setCtcAmt(String ctcAmt) {
		this.ctcAmt = ctcAmt;
	}

	public String getHideCtcAmt() {
		return hideCtcAmt;
	}

	public void setHideCtcAmt(String hideCtcAmt) {
		this.hideCtcAmt = hideCtcAmt;
	}
	
}