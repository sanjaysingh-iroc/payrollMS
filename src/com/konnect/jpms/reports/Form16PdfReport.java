package com.konnect.jpms.reports;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.payroll.ApprovePayroll;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;


public class Form16PdfReport implements ServletRequestAware,ServletResponseAware, IStatements {

	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	
	String strUserType;
	String strSessionEmpId;
	String formType;
	String strMonth;
	String strSubmit;
	String financialYear;
	String strSelectedEmpId;
	String emp_id;
	double aggregateDeductionAmount;
	double TDSPaidAmount;
	double totalEducationCess;
	double totalStandardCess;
	double totalTax;
	double totalSalary;
	double totalTaxableAmount;
	double totalTaxFreeAmount;
//	List<FillMonth> monthList;
//	List<FillFinancialYears> financialYearList; 
//	List<FillEmployee> empNamesList;
	CommonFunctions CF;
	UtilityFunctions uF = new UtilityFunctions();
	public void execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
//		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		
//		empNamesList = new FillEmployee(request).fillEmployeeName(null, null, session); 

//		if(getStrMonth()==null){
//			setStrMonth("1");
//		}
//		if(getStrSelectedEmpId()==null && empNamesList!=null){
//			setStrSelectedEmpId(empNamesList.get(0).getEmployeeId());
//		}

		if(getFormType()!=null && getFormType().equals("form16")){
			generateForm16KPCANew();
		}

	}
	
	private void generateForm16KPCANew() {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF =new UtilityFunctions();
		try {
			
			String[] strFianacialDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
	
			if (getFinancialYear() != null) {				
				strFianacialDates = getFinancialYear().split("-");
				strFinancialYearStart = strFianacialDates[0];
				strFinancialYearEnd = strFianacialDates[1];			
			} else {				
				strFianacialDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFianacialDates[0] + "-" + strFianacialDates[1]);				
				strFinancialYearStart = strFianacialDates[0];
				strFinancialYearEnd = strFianacialDates[1];				 
			}
			
			con = db.makeConnection(con);
			
			Map<String, String> hmSectionMap = CF.getSectionMap(con,strFinancialYearStart,strFinancialYearEnd);
			
	//		Map<String,String> hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
			int nEmpLevelId = CF.getEmpLevelId(getEmp_id(), request);
			Map<String, String> hmSalaryHeadMap = CF.getSalaryHeadsMap(con, nEmpLevelId);
			
			Map<String, String> hmDept =CF.getDeptMap(con);	
			Map<String, String> hmEmpAgeMap =CF.getEmpAgeMap(con, CF);
//			Map<String, String> hmEmpSlabMap =CF.getEmpSlabMap(con, CF);

			Map hmEmpMertoMap = new HashMap();
			Map hmEmpWlocationMap = new HashMap();
			Map hmEmpStateMap = new HashMap();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
			Map<String, String> hmOtherDetailsMap = getOtherDetailsMap(uF.parseToInt(getEmp_id()));
			Map<String, String> hmEmpCodeDesig =CF.getEmpDesigMap(con);
			
			Map<String,Map<String,String>> hmPrevEmpTds = CF.getPrevEmpTdsDetails(con,uF,strFinancialYearStart,strFinancialYearEnd);
			
			Map hmEmpGenderMap = CF.getEmpGenderMap(con); 
			
			Map<String, String> hmStates = CF.getStateMap(con);
			String orgId = CF.getEmpOrgId(con, uF, getEmp_id());
			
			String locationID=""+hmEmpWlocationMap.get(getEmp_id());
			
			pst = con.prepareStatement("select * from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id" +
					" and epd.emp_per_id in (select emp_id from authorised_details where wlocation_id=? and financial_year_start=?" +
					" and financial_year_end=?)");
			pst.setInt(1, uF.parseToInt(locationID));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
	//		System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmAuthorised = new HashMap<String, String>();
			while (rs.next()) {
				String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
				String strEmpName = rs.getString("emp_fname") + " " + uF.showData(strMiddleName, "")+ rs.getString("emp_lname");
				hmAuthorised.put("EMP_NAME", strEmpName);
				hmAuthorised.put("EMP_MIDDLE_NAME", strMiddleName);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from org_details where org_id = ? ");
			pst.setInt(1, uF.parseToInt(orgId));
	//		System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmOrg=new HashMap<String, String>();
			while (rs.next()) {
				hmOrg.put("ORG_ID", rs.getString("org_id"));
				hmOrg.put("ORG_NAME", rs.getString("org_name"));
				hmOrg.put("ORG_LOGO", rs.getString("org_logo"));
				hmOrg.put("ORG_ADDRESS", rs.getString("org_address"));
				hmOrg.put("ORG_PINCODE", rs.getString("org_pincode"));
				hmOrg.put("ORG_CONTACT", rs.getString("org_contact1"));
				hmOrg.put("ORG_EMAIL", rs.getString("org_email"));
				hmOrg.put("ORG_STATE_ID", rs.getString("org_state_id"));
				hmOrg.put("ORG_COUNTRY_ID", rs.getString("org_country_id"));
				hmOrg.put("ORG_CITY", rs.getString("org_city"));
				hmOrg.put("ORG_CODE", rs.getString("org_code"));
				hmOrg.put("ORG_DISPLAY_PAYCYCLE", rs.getString("display_paycycle"));
				hmOrg.put("ORG_DURATION_PAYCYCLE", rs.getString("duration_paycycle"));
				hmOrg.put("ORG_SALARY_CAL_BASIS", rs.getString("salary_cal_basis"));
				hmOrg.put("ORG_START_PAYCYCLE",uF.getDateFormat(rs.getString("start_paycycle"), DBDATE, DATE_FORMAT) );
			}
			rs.close();
			pst.close();  
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("SELECT eod.emp_id,epd.emp_fname,epd.emp_lname,epd.empcode,eod.depart_id,epd.emp_pan_no,epd.employment_end_date,epd.joining_date,epd.emp_address1,epd.emp_city_id FROM employee_official_details eod, employee_personal_details epd WHERE epd.emp_per_id=eod.emp_id  " +
					"and eod.emp_id in (select distinct emp_id from attendance_details where to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD') between ? and ?) and eod.emp_id=? ");
			       
			sbQuery.append(" order by emp_id"); 
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getEmp_id()));
	//		System.out.println("==generateForm16KPCA=====0========"+new Date());
	//		System.out.println("pst=0=="+pst);
			rs = pst.executeQuery();
			
			Map<String, String> hmEmployeeMap = new HashMap<String, String>();
			Map<String, String> hmEmpJoiningDate = new HashMap<String, String>();
			Map<String, String> hmEmpCode = new HashMap<String, String>();
			Map<String, String> hmEmpDepartment = new HashMap<String, String>();
			Map<String, String> hmEmpPanNo = new HashMap<String, String>();
			Map<String, String> hmEmpEndDate = new HashMap<String, String>(); 
			Map<String, String> hmEmpAddressMap = new HashMap<String, String>();
			while (rs.next()) {
				if (rs.getInt("emp_id") < 0) {
					continue;
				} 
				hmEmployeeMap.put(rs.getString("emp_id"), rs.getString("emp_fname") + " " + rs.getString("emp_lname"));
				hmEmpJoiningDate.put(rs.getString("emp_id"), uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				hmEmpCode.put(rs.getString("emp_id"), rs.getString("empcode"));
				hmEmpDepartment.put(rs.getString("emp_id"), rs.getString("depart_id"));
				hmEmpPanNo.put(rs.getString("emp_id"), uF.showData(rs.getString("emp_pan_no"), ""));
				hmEmpEndDate.put(rs.getString("emp_id"), uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT));
				
				String empAddress = rs.getString("emp_address1")!=null && !rs.getString("emp_address1").equals("") ? rs.getString("emp_address1") : "";
				hmEmpAddressMap.put(rs.getString("emp_id"), empAddress+"\n"+uF.showData(rs.getString("emp_city_id"), ""));
			}
			rs.close();
			pst.close();
	//		System.out.println("=======1========"+new Date());
			
			pst = con.prepareStatement("SELECT count(*) as cnt,emp_id FROM emp_family_members WHERE member_type='CHILD' and emp_id=? group by emp_id");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
	//		System.out.println(" pst==>"+pst);
			Map<String, String> hmEmpChildCnt = new HashMap<String, String>();
			while(rs.next()){
				hmEmpChildCnt.put(rs.getString("emp_id"), rs.getString("cnt"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(*) as cnt,emp_id from (select distinct(paycycle),emp_id from payroll_generation pg " +
					"where financial_year_from_date=? and financial_year_to_date =? and emp_id=? and is_paid = true " +
					"and salary_head_id not in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+") " +
					" and earning_deduction='E' group by paycycle,emp_id order by emp_id,paycycle) a group by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
	//		System.out.println(" pst==>"+pst);
			Map<String, String> hmMonthPaid = new HashMap<String, String>();
			while(rs.next()){
				hmMonthPaid.put(rs.getString("emp_id"), rs.getString("cnt"));
			}
			rs.close();
			pst.close();
	//		System.out.println(" hmMonthPaid==>"+hmMonthPaid);
			
			pst = con.prepareStatement("select distinct(pg.emp_id),pg.month,pg.year,pg.paycycle,pg.paid_days,pg.total_days from (" +
					"select max(paycycle) as paycycle,emp_id from (select distinct(paycycle),emp_id from payroll_generation pg " +
					"where financial_year_from_date=? and financial_year_to_date =? and is_paid = true " +
					"and salary_head_id not in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+") " +
					"and earning_deduction='E' and emp_id=? group by paycycle,emp_id order by emp_id,paycycle) a group by emp_id ) a,payroll_generation pg " +
					"where a.emp_id=pg.emp_id and a.paycycle=pg.paycycle and pg.emp_id in (select emp_per_id from employee_personal_details " +
					"where is_alive=false and employment_end_date between ? and ?) order by pg.emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getEmp_id()));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
	//		System.out.println(" pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmLastPaycycle = new HashMap<String, String>();
			while(rs.next()){
				hmLastPaycycle.put(rs.getString("emp_id"), rs.getString("emp_id"));
				hmLastPaycycle.put(rs.getString("emp_id")+"_MONTH", rs.getString("month"));
				hmLastPaycycle.put(rs.getString("emp_id")+"_YEAR", rs.getString("year"));
				hmLastPaycycle.put(rs.getString("emp_id")+"_PAYCYCLE", rs.getString("paycycle"));
				hmLastPaycycle.put(rs.getString("emp_id")+"_PAIDDAYS", rs.getString("paid_days"));
				hmLastPaycycle.put(rs.getString("emp_id")+"_TOTALDAYS", rs.getString("total_days"));
			}
			rs.close();
			pst.close();
	//		System.out.println("hmLastPaycycle==>"+hmLastPaycycle);
			
			pst = con.prepareStatement("select * from employee_personal_details where is_alive=false and employment_end_date between ? and ? and emp_per_id=? order by emp_per_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getEmp_id()));
	//		System.out.println(" pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmExEmp = new HashMap<String, String>();
			while(rs.next()){
				hmExEmp.put(rs.getString("emp_per_id"), uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
	//		System.out.println("hmExEmp==>"+hmExEmp);
			
			Map hmPayrollDetails = new HashMap();			
			List al = new ArrayList();			
			double dblInvestmentExemption = 0.0d;
			
			Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1 ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
	//		System.out.println(" pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SERVICE_TAX", rs.getString("service_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_EDU_TAX", rs.getString("education_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_STD_TAX", rs.getString("standard_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_FLAT_TDS", rs.getString("flat_tds"));
				
				hmOtherTaxDetails.put(rs.getString("state_id")+"_MAX_TAX_INCOME", rs.getString("max_net_tax_income"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_REBATE_AMOUNT", rs.getString("rebate_amt"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SWACHHA_BHARAT_CESS", rs.getString("swachha_bharat_cess"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_KRISHI_KALYAN_CESS", rs.getString("krishi_kalyan_cess"));
				
				hmOtherTaxDetails.put(rs.getString("state_id")+"_CGST", rs.getString("cgst"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SGST", rs.getString("sgst"));
				
	//			dblInvestmentExemption = 100000;
			}
			rs.close();
			pst.close();
	//		System.out.println("=======2========"+new Date());
			
			pst = con.prepareStatement("select sum(amount) as amount,salary_head_id,emp_id,earning_deduction from payroll_generation pg where financial_year_from_date=? and financial_year_to_date =? and is_paid = true and emp_id=? and salary_head_id not in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+") group by salary_head_id, emp_id,earning_deduction order by emp_id,earning_deduction desc");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getEmp_id()));
	//		System.out.println(" pst==>"+pst);
			rs = pst.executeQuery();					
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			Map hmInner1 = new HashMap();
			double dblGross = 0.0d;
			Map<String,String> hmLeaveEncashmet = new HashMap<String, String>();
			while(rs.next()){
				strEmpIdNew = rs.getString("emp_id");
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					hmInner1 = new HashMap();
					dblGross = 0.0d;
				}
								
				hmInner1.put(rs.getString("salary_head_id"), rs.getString("amount"));   
				if(rs.getString("earning_deduction").equalsIgnoreCase("E")){
					dblGross += rs.getDouble("amount");
				}
				hmInner1.put("GROSS", dblGross+"");
				hmPayrollDetails.put(strEmpIdNew, hmInner1);
				
				if(uF.parseToInt(rs.getString("salary_head_id")) == LEAVE_ENCASHMENT && hmExEmp.containsKey(rs.getString("emp_id"))){
					hmLeaveEncashmet.put(rs.getString("emp_id"), rs.getString("amount"));
				}
				
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
	//		System.out.println("=======3========"+new Date());
			
			pst = con.prepareStatement("select * from exemption_details where exemption_from=? and exemption_to=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
	//		System.out.println(" pst==>"+pst);
			Map hmExemption = new HashMap();
			while(rs.next()){
	//			hmExemption.put(rs.getString("exemption_name"), rs.getString("exemption_limit"));
				hmExemption.put(rs.getString("salary_head_id"), rs.getString("exemption_limit"));
			}
			rs.close();
			pst.close();
	//		System.out.println("=======4========"+new Date());
			
			
			pst = con.prepareStatement("select * from section_details where under_section in (8,9) and financial_year_start=? and financial_year_end=? ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
	//		System.out.println(" pst==>"+pst);
			List<String> chapter1SectionList = new ArrayList<String>();
			List<String> chapter2SectionList = new ArrayList<String>();
			while(rs.next()){
				if(uF.parseToInt(rs.getString("under_section"))==8) {
					chapter1SectionList.add(rs.getString("section_id"));
				} else {
					chapter2SectionList.add(rs.getString("section_id"));
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
	//		System.out.println(" pst==>"+pst);
			Map hmHRAExemption = new HashMap();
			while(rs.next()){
				hmHRAExemption.put("CONDITION_1", rs.getString("condition1"));
				hmHRAExemption.put("CONDITION_2", rs.getString("condition2"));
				hmHRAExemption.put("CONDITION_3", rs.getString("condition3"));
				hmHRAExemption.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
			}
			rs.close();
			pst.close();
	//		System.out.println("=======5========"+new Date());
			
			Map hmSectionLimitA = new HashMap();
			Map hmSectionLimitP = new HashMap();			
			Map hmSectionLimitEmp = new HashMap();
			
	//		pst = con.prepareStatement(selectSection);
			pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? order by section_code");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
	//		System.out.println(" pst==>"+pst);
			while (rs.next()) {
				
				if(rs.getString("section_limit_type").equalsIgnoreCase("A")){
					hmSectionLimitA.put(rs.getString("section_id"), rs.getString("section_exemption_limit"));
				}else{
					hmSectionLimitP.put(rs.getString("section_id"), rs.getString("section_exemption_limit"));
				}
			}
			rs.close();
			pst.close();
	//		System.out.println("=======6========"+new Date());
			dblInvestmentExemption = uF.parseToDouble(""+hmSectionLimitA.get("3"));
			
			//pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details id, section_details sd where sd.section_id=id.section_id and fy_from = ? and fy_to=? and status = true and section_code in ('HRA') group by emp_id ");
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details id, exemption_details ed " +
					"where ed.salary_head_id=id.salary_head_id and id.fy_from = ? and id.fy_to=? and status = true and ed.salary_head_id=? " +
					"and trail_status = 1 and ed.exemption_from=? and ed.exemption_to=? and emp_id=? group by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, HRA);
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
	//		System.out.println(" pst==>"+pst);
			Map hmRentPaid = new HashMap();
			while(rs.next()){
				hmRentPaid.put(rs.getString("emp_id"), rs.getString("amount_paid"));
			}
			rs.close();
			pst.close();
	//		System.out.println("=======7========"+new Date());
			
	//		pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd  where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and section_code not in ('HRA') and sd.section_id not in (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%') and isdisplay=true and parent_section=0 group by emp_id, sd.section_id order by emp_id ");
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
					"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 " +
					"and sd.financial_year_start=? and sd.financial_year_end=? and section_code not in ('HRA') and sd.section_id !=11 " +
					"and isdisplay=true and parent_section=0 and under_section=8 and emp_id=? group by emp_id, sd.section_id order by emp_id ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
	//		System.out.println(" pst==>"+pst);
			Map hmInvestment = new HashMap();
			Map<String, String> hmEmpExemptionsCH1Map = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmpInvestment = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmpActualInvestment = new HashMap<String, Map<String, String>>();
			double dblInvestmentLimit = 0;
			double dblInvestmentEmp = 0;
			
			while(rs.next()){
				String strSectionId = rs.getString("section_id");
				double dblInvestment = rs.getDouble("amount_paid");
				
				if(hmSectionLimitA.containsKey(strSectionId)){
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
				}else{
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
				}
				Map<String,String> hmInvest=hmEmpInvestment.get(rs.getString("emp_id"));
				if(hmInvest==null) hmInvest=new HashMap<String, String>();
				
				if(dblInvestment>=dblInvestmentLimit){
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH1Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
					hmEmpExemptionsCH1Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
					hmInvest.put(strSectionId, ""+dblInvestmentLimit);
				}else{
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH1Map.get(rs.getString("emp_id"))) + dblInvestment;
					hmEmpExemptionsCH1Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
					hmInvest.put(strSectionId, ""+dblInvestment);
				}
				hmEmpInvestment.put(rs.getString("emp_id"), hmInvest);
				
				Map<String,String> hmActualInvest=hmEmpActualInvestment.get(rs.getString("emp_id"));
				if(hmActualInvest==null) hmActualInvest=new HashMap<String, String>();
				hmActualInvest.put(strSectionId, ""+dblInvestment);
				
				hmEmpActualInvestment.put(rs.getString("emp_id"), hmActualInvest);
				
				if(rs.getString("emp_id").equals("654")){
	//				System.out.println("dblInvestment==="+dblInvestment);
	//				System.out.println("dblInvestmentLimit==="+dblInvestmentLimit);
	//				System.out.println("dblInvestmentEmp==="+dblInvestmentEmp);					
				}
			}
			rs.close();
			pst.close();			
	//		System.out.println("=======8========"+new Date());
	//		System.out.println("hmEmpInvestment==="+hmEmpInvestment.get("654"));    
			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
					"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 " +
					"and sd.financial_year_start=? and sd.financial_year_end=? and section_code not in ('HRA') and sd.section_id !=11 " +
					"and isdisplay=true and parent_section=0 and under_section=9 and emp_id=? group by emp_id, sd.section_id order by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
	//		System.out.println(" pst==>"+pst);
			Map<String, Map<String, String>> hmEmpInvestment1 = new HashMap<String, Map<String, String>>();
			Map<String, String> hmEmpExemptionsCH2Map = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmpActualInvestment1 = new HashMap<String, Map<String, String>>();
			dblInvestmentLimit = 0;
			dblInvestmentEmp = 0;
			
			while(rs.next()){
				String strSectionId = rs.getString("section_id");
				double dblInvestment = rs.getDouble("amount_paid");
				
				if(hmSectionLimitA.containsKey(strSectionId)){
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
				}else{
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
				}
				Map<String,String> hmInvest=hmEmpInvestment1.get(rs.getString("emp_id"));
				if(hmInvest==null) hmInvest=new HashMap<String, String>();
				
				if(dblInvestment>=dblInvestmentLimit){
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
					hmEmpExemptionsCH2Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
					hmInvest.put(strSectionId, ""+dblInvestmentLimit);
				}else{
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestment;
					hmEmpExemptionsCH2Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
					hmInvest.put(strSectionId, ""+dblInvestment);
				}
				hmEmpInvestment1.put(rs.getString("emp_id"), hmInvest);
				
				Map<String,String> hmActualInvest=hmEmpActualInvestment1.get(rs.getString("emp_id"));
				if(hmActualInvest==null) hmActualInvest=new HashMap<String, String>();
				hmActualInvest.put(strSectionId, ""+dblInvestment);
				
				hmEmpActualInvestment1.put(rs.getString("emp_id"), hmActualInvest);
				
				if(rs.getString("emp_id").equals("654")){
	//				System.out.println("dblInvestment==="+dblInvestment);
	//				System.out.println("dblInvestmentLimit==="+dblInvestmentLimit);
	//				System.out.println("dblInvestmentEmp==="+dblInvestmentEmp);					
				}
			}
			rs.close();
			pst.close();			
	//		System.out.println("hmEmpInvestment1==="+hmEmpInvestment1.get("654"));
	//		System.out.println("=======9========"+new Date());
			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
					"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.section_id=3 " +
					"and isdisplay=true and parent_section=0 and sd.financial_year_start=? and sd.financial_year_end=? and emp_id=? group by emp_id, sd.section_id order by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
	//		System.out.println(" pst==>"+pst);
			Map<String, String> hmEmp80C = new HashMap<String, String>();
			dblInvestmentLimit = 0;
			dblInvestmentEmp = 0;
			
			while(rs.next()){
				String strSectionId = rs.getString("section_id"); 
				double dblInvestment = rs.getDouble("amount_paid");
				
				if(hmSectionLimitA.containsKey(strSectionId)){
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
				}else{
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
				}
				
				
				if(dblInvestment>=dblInvestmentLimit){
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
					hmEmp80C.put(rs.getString("emp_id"), dblInvestmentLimit+"");
				}else{
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestment;
					hmEmp80C.put(rs.getString("emp_id"), dblInvestment+"");
				}
				
			}
			rs.close();
			pst.close();			
	//		System.out.println("hmEmpInvestment1==="+hmEmpInvestment1.get("654"));
	//		System.out.println("=======10========"+new Date());
			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
					"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to =? and status = true and trail_status = 1 and sd.section_id=1 " +
					"and isdisplay=true and parent_section=0 and sd.financial_year_start=? and sd.financial_year_end=? and emp_id=? group by emp_id, sd.section_id order by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
	//		System.out.println(" pst==>"+pst);
			Map<String, String> hmEmp80D = new HashMap<String, String>();
			dblInvestmentLimit = 0;
			dblInvestmentEmp = 0;
			
			while(rs.next()){
				String strSectionId = rs.getString("section_id");
				double dblInvestment = rs.getDouble("amount_paid");
				
				if(hmSectionLimitA.containsKey(strSectionId)){
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
				}else{
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
				}
				
				
				if(dblInvestment>=dblInvestmentLimit){
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
					hmEmp80D.put(rs.getString("emp_id"), dblInvestmentLimit+"");
				}else{
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestment;
					hmEmp80D.put(rs.getString("emp_id"), dblInvestment+"");
				}
				
			}
			rs.close();
			pst.close();
	//		System.out.println("=======11========"+new Date());
			
			pst = con.prepareStatement("select * from investment_details id, section_details sd  where sd.section_id=id.section_id " +
					"and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.financial_year_start=? " +
					"and sd.financial_year_end=? and section_code not in ('HRA') and sd.section_id !=11 and isdisplay=true " +
					"and parent_section>0 and emp_id=? order by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
	//		System.out.println(" pst==>"+pst);
			
			Map<String, Map<String, List<Map<String, String>>>> hmEmpSubInvestment = new HashMap<String, Map<String, List<Map<String, String>>>>();
	//		Map<String, List<Map<String, String>>> hmSubInvestment = new HashMap<String, List<Map<String, String>>>();	
			dblInvestmentLimit = 0;
			dblInvestmentEmp = 0;
			
			while(rs.next()){
				String strSectionId = rs.getString("parent_section");
				double dblInvestment = rs.getDouble("amount_paid");
				
				Map<String, List<Map<String, String>>> hmSubInvestment =hmEmpSubInvestment.get(rs.getString("emp_id"));
				if(hmSubInvestment ==null)hmSubInvestment = new HashMap<String, List<Map<String,String>>>();
				
				List<Map<String, String>> alSubInvestment =hmSubInvestment.get(rs.getString("parent_section"));
				if(alSubInvestment ==null)alSubInvestment = new ArrayList<Map<String, String>>();
				
				Map<String, String> hm = new HashMap<String, String>();
				hm.put("SECTION_ID", rs.getString("parent_section"));
				hm.put("SECTION_NAME", rs.getString("child_section"));
				hm.put("INVESTMENT_ID", rs.getString("investment_id"));
				hm.put("PAID_AMOUNT", ""+dblInvestment);
				hm.put("STATUS", rs.getString("status"));
				
				alSubInvestment.add(hm);
				
				hmSubInvestment.put(rs.getString("parent_section"), alSubInvestment);
				hmEmpSubInvestment.put(rs.getString("emp_id"), hmSubInvestment);
			}
			rs.close();
			pst.close();
			
	//		System.out.println("hmEmpSubInvestment==="+hmEmpSubInvestment.get("683"));
			
			
			
			/**
			 * HOME LOAN INTEREST EXEMPTION 
			 */
			pst = con.prepareStatement("select * from section_details where section_id = 11 and financial_year_start=? and financial_year_end=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
	//		System.out.println(" pst==>"+pst);
			double dblLoanExemptionLimit = 0;
			while (rs.next()) {
				dblLoanExemptionLimit = rs.getDouble("section_exemption_limit");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details where fy_from =? and fy_to =? and status = true" +
					" and trail_status = 1 and parent_section=0 and  section_id in (select section_id from section_details where section_id = 11 and financial_year_start=? " +
					"and financial_year_end=?) and emp_id=? group by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
	//		System.out.println(" pst==>"+pst);
			Map<String, String> hmEmpHomeLoanMap = new HashMap<String, String>();
			while (rs.next()) {
				
				if(uF.parseToDouble(rs.getString("amount_paid"))>dblLoanExemptionLimit){
					hmEmpHomeLoanMap.put(rs.getString("emp_id"), dblLoanExemptionLimit+"");
				}else{
					hmEmpHomeLoanMap.put(rs.getString("emp_id"), rs.getString("amount_paid"));
				}
			}
			rs.close();
			pst.close();
	//		System.out.println("=======12========"+new Date());
			/**
			 * HOME LOAN INTEREST EXEMPTION 
			 */
			
			Map<String,String> hmEmpIncomeFromOtherSourcesMap = new HashMap<String,String>();			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
					"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and status = true and sd.section_id =13 " +
					"and isdisplay=false and financial_year_start=? and financial_year_end=? and emp_id=? group by emp_id, sd.section_id order by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, uF.parseToInt(getEmp_id()));
	//		System.out.println("pst========"+pst);
			rs = pst.executeQuery();			
			double dblInvestmentIncomeSourcesEmp = 0;		
			while (rs.next()) {
				String strSectionId = rs.getString("section_id");
				double dblInvestment = rs.getDouble("amount_paid");
				
				dblInvestmentIncomeSourcesEmp = uF.parseToDouble(hmEmpIncomeFromOtherSourcesMap.get(rs.getString("emp_id"))) + dblInvestment;
				hmEmpIncomeFromOtherSourcesMap.put(rs.getString("emp_id"), dblInvestmentIncomeSourcesEmp+"");
			}
			rs.close();
			pst.close();
	//		System.out.println("=======13========"+new Date());
			
			Map<String,String> hmEmpUnderSection10Map = new HashMap<String,String>();			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, ed.salary_head_id, emp_id from investment_details id, exemption_details ed " +
					"where ed.salary_head_id=id.salary_head_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and status = true and under_section=4 " +
					"and exemption_from=? and exemption_to=? and id.salary_head_id>0 and id.parent_section=0 and emp_id=? group by emp_id, ed.salary_head_id order by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, uF.parseToInt(getEmp_id()));
	//		System.out.println("pst========"+pst);
			rs = pst.executeQuery();			
			double dblUnderSection10Emp = 0;		
			Map<String,Map<String,String>> hmUnderSection10Map = new HashMap<String,Map<String,String>>();
			Map<String,Map<String,String>> hmUnderSection10PaidMap = new HashMap<String,Map<String,String>>();
			while (rs.next()) {
				String strsalaryheadid = rs.getString("salary_head_id");
				double dblInvestment = rs.getDouble("amount_paid");				
				
	//			if(hmSectionLimitA.containsKey(strSectionId)){
	//				dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
	//			}else{
	//				dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
	//				dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
	//			}
				
				dblInvestmentLimit = uF.parseToDouble((String)hmExemption.get(strsalaryheadid));
				
				Map<String, String> hmInner= (Map<String, String>)hmUnderSection10Map.get(rs.getString("emp_id"));
				if(hmInner==null) hmInner=new HashMap<String, String>();
				
				double dblAllowanceExempt = Math.min(dblInvestment, dblInvestmentLimit);
				
				hmInner.put(rs.getString("salary_head_id"), ""+dblAllowanceExempt);				
				hmUnderSection10Map.put(rs.getString("emp_id"), hmInner);
				
				dblUnderSection10Emp = uF.parseToDouble(hmEmpUnderSection10Map.get(rs.getString("emp_id"))) + dblAllowanceExempt;
				hmEmpUnderSection10Map.put(rs.getString("emp_id"), dblInvestmentIncomeSourcesEmp+"");
				
				Map<String, String> hmInner11 = (Map<String, String>)hmUnderSection10PaidMap.get(rs.getString("emp_id"));
				if(hmInner11 == null) hmInner11 = new HashMap<String, String>();
				
				hmInner11.put(rs.getString("salary_head_id"), ""+dblInvestment);				
				hmUnderSection10PaidMap.put(rs.getString("emp_id"), hmInner11);
				  
				
			}
			rs.close();
			pst.close();
	//		System.out.println("=======13========"+new Date());
	//		System.out.println("hmUnderSection10Map====="+hmUnderSection10Map);
			
			Map hmTaxLiability = new HashMap();
			Set set = hmPayrollDetails.keySet();
			Iterator it = set.iterator();
			while(it.hasNext()){
				Map hmTaxInner = new HashMap();
				String strEmpId = (String)it.next();
				Map hmEmpPayrollDetails = (Map)hmPayrollDetails.get(strEmpId);
				
				double dblBasic = uF.parseToDouble((String)hmEmpPayrollDetails.get(BASIC+""));
				double dblDA = uF.parseToDouble((String)hmEmpPayrollDetails.get(DA+""));
				
				String strTDSAmt = (String)hmSalaryHeadMap.get(TDS+"");
				
				String strConveyanceAllowance = (String)hmSalaryHeadMap.get(CONVEYANCE_ALLOWANCE+"");
				String strProfessionalTax = (String)hmSalaryHeadMap.get(PROFESSIONAL_TAX+"");
				
				double dblGross1 = uF.parseToDouble((String)hmEmpPayrollDetails.get("GROSS") );
				
				double dblProfessionalTaxPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(PROFESSIONAL_TAX+""));
	//			double dblProfessionalTaxExemptLimit = uF.parseToDouble((String)hmExemption.get(strProfessionalTax));
				double dblProfessionalTaxExemptLimit = uF.parseToDouble((String)hmExemption.get(PROFESSIONAL_TAX+""));
				double dblProfessionalTaxExempt = Math.min(dblProfessionalTaxPaid, dblProfessionalTaxExemptLimit);
				
				double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_EDU_TAX"));
				double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_STD_TAX"));
				double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_FLAT_TDS"));
				
				
				
				
				
				// Exemptions needs to worked out as other exemptions				
				
				   
				double dblHomeLoanTaxExempt = uF.parseToDouble(hmEmpHomeLoanMap.get(strEmpId));
				
				
					
				double dblOtherExemptions = 0;
	//			Set setSalaryMap = hmSalaryHeadMap.keySet();
	//			Iterator itSalaryMap = setSalaryMap.iterator();
	//			while(itSalaryMap.hasNext()){
	//				String strSalaryHead = (String)itSalaryMap.next();
	//				String strSalaryHeadName = (String)hmSalaryHeadMap.get(strSalaryHead);
	//				
	//				if(uF.parseToInt(strSalaryHead) == PROFESSIONAL_TAX ||  uF.parseToInt(strSalaryHead) == CONVEYANCE_ALLOWANCE ||  uF.parseToInt(strSalaryHead) == EDUCATION_ALLOWANCE || uF.parseToInt(strSalaryHead) == MEDICAL_ALLOWANCE){
	//					continue;
	//				}
	//				
	//				double dblOtherExemptionsPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(strSalaryHead));
	//				double dblOtherExemptionsExemptLimit = uF.parseToDouble((String)hmExemption.get(strSalaryHead));
	//				double dblOtherExemptionsTaxExempt = Math.min(dblOtherExemptionsPaid, dblOtherExemptionsExemptLimit);
	//				dblOtherExemptions += dblOtherExemptionsTaxExempt; 
	//			}
				dblOtherExemptions = uF.parseToDouble(hmLeaveEncashmet.get(strEmpId));
					
				double dblInvestment = uF.parseToDouble((String)hmInvestment.get(strEmpId));
	//			double dblInvestment = uF.parseToDouble((String)hmEmpExemptionsMap.get(strEmpId));
				double dblEPFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(EMPLOYEE_EPF+""));
				double dblEPRFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(EMPLOYER_EPF+""));
				double dblEPVFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(VOLUNTARY_EPF+""));
				
				double dblChapterVIA1=uF.parseToDouble(hmEmpExemptionsCH1Map.get(strEmpId));				
				double dblChapterVIA2=uF.parseToDouble(hmEmpExemptionsCH2Map.get(strEmpId));
				double dbl80C=uF.parseToDouble(hmEmp80C.get(strEmpId));
				double dbl80D=uF.parseToDouble(hmEmp80D.get(strEmpId));
	//			double dblTotalInvestment =dblChapterVIA1+dblChapterVIA2;
	//			double dblTotalInvestment =dblChapterVIA1+dblChapterVIA2 + dblEPFPaid;
				double dblTotalInvestment =dbl80C + dblEPFPaid + dblEPRFPaid + dblEPVFPaid;
				double dblEmpPF =dblEPFPaid + dblEPRFPaid + dblEPVFPaid;
	//			double dblTotalInvestment =0.0d; //dblInvestment + dblEPFPaid;  
				double dblInvestmentExempt = Math.min(dblTotalInvestment, dblInvestmentExemption);
				  
				
				if(strEmpId.equals("654")){
	//				System.out.println("dblInvestment==="+dblInvestment);
	//				System.out.println("dblEPFPaid==="+dblEPFPaid);
	//				System.out.println("dblEPRFPaid==="+dblEPRFPaid);
	//				System.out.println("dblEPVFPaid==="+dblEPVFPaid);
	//				System.out.println("dblInvestmentExempt==="+dblInvestmentExempt);
	//				System.out.println("dblTotalInvestment===="+dblTotalInvestment); 
	//				System.out.println("dblInvestmentExemption="+dblInvestmentExemption);
				} 
	//			
				double dblConAllLimit = 0.0d;
				if(hmExEmp.containsKey(strEmpId)){
					int nMonth = uF.parseToInt(hmMonthPaid.get(strEmpId));
					double dblConAmt = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) * (nMonth - 1);
					double dblTotalDaysAmt = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) / uF.parseToDouble(hmLastPaycycle.get(strEmpId+"_TOTALDAYS"));
					double dblPaidDaysAmt = dblTotalDaysAmt * uF.parseToDouble(hmLastPaycycle.get(strEmpId+"_PAIDDAYS")); 
					
					dblConAllLimit = dblConAmt + dblPaidDaysAmt;
				} else {
					dblConAllLimit = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) * uF.parseToInt(hmMonthPaid.get(strEmpId));
				}
				double dblConveyanceAllowanceLimit = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblConAllLimit));
	//			double dblConveyanceAllowanceLimit = strEmpId.equals("924") ? 15071 : (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) * uF.parseToInt(hmMonthPaid.get(strEmpId));
				double dblConveyanceAllowancePaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(CONVEYANCE_ALLOWANCE+""));
				double dblConveyanceAllowanceExempt = Math.min(dblConveyanceAllowancePaid, dblConveyanceAllowanceLimit);
				
				int nEmpChildCnt = uF.parseToInt(hmEmpChildCnt.get(strEmpId)) > 2 ? 2 : uF.parseToInt(hmEmpChildCnt.get(strEmpId));
				double dblEducationAllowanceLimit = ((uF.parseToDouble((String)hmExemption.get(EDUCATION_ALLOWANCE+""))/12) * nEmpChildCnt) * uF.parseToInt(hmMonthPaid.get(strEmpId));
				double dblEducationAllowancePaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(EDUCATION_ALLOWANCE+""));
				double dblEducationAllowanceExempt = Math.min(dblEducationAllowancePaid, dblEducationAllowanceLimit);
				
				
				String[] hraSalaryHeads = null;
				if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null){
					hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
				}
				
				double dblHraSalHeadsAmount = 0;
				for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++){
					dblHraSalHeadsAmount += uF.parseToDouble((String)hmEmpPayrollDetails.get(hraSalaryHeads[i]));
				}
				
	//			String strHRA = (String)hmSalaryHeadMap.get(HRA+"");
				String strHRA = (String)hmEmpPayrollDetails.get(HRA+"");
				
				String strCondition1 = (String)hmHRAExemption.get("CONDITION_1");
				String strCondition2 = (String)hmHRAExemption.get("CONDITION_2");
				String strCondition3 = (String)hmHRAExemption.get("CONDITION_3");
				
				double dblCondition1 = uF.parseToDouble(strCondition1);
				double dblCondition2 = uF.parseToDouble(strCondition2);
				double dblCondition3 = uF.parseToDouble(strCondition3);
				
	//			double dblHRA1 = dblCondition1 * (dblBasic + dblDA) / 100;
	//			double dblHRA2 = dblCondition2 * (dblBasic + dblDA) / 100;
	//			double dblHRA3 = dblCondition3 * (dblBasic + dblDA) / 100;
				double dblHRA1 = dblCondition1 * dblHraSalHeadsAmount / 100;
				double dblHRA2 = dblCondition2 * dblHraSalHeadsAmount / 100;
				double dblHRA3 = dblCondition3 * dblHraSalHeadsAmount / 100;
				
				
				double dblActualHRAPaid = uF.parseToDouble(strHRA);
				double dblActualRentPaid = uF.parseToDouble((String)hmRentPaid.get(strEmpId));
				
				double dblHRAExemption = Math.min(dblActualHRAPaid, dblActualRentPaid);
				boolean isMetro = uF.parseToBoolean((String)hmEmpMertoMap.get(strEmpId));
				double dblHRA = 0;
				if(isMetro){
					if(dblHRA1<dblHRA2){
						dblHRA = dblHRA1; 
						hmTaxInner.put("dblHRA", "Rent paid over "+dblCondition1+"% of salary");
					}else{
						dblHRA = dblHRA2;
						hmTaxInner.put("dblHRA", "");
						hmTaxInner.put("dblHRA", dblCondition2+"% of salary in metro cities");
					}
				}else{
					if(dblHRA1<dblHRA3){
						dblHRA = dblHRA1;
						hmTaxInner.put("dblHRA", "");
						hmTaxInner.put("dblHRA", "Rent paid over "+dblCondition1+"% of salary");
					}else{
						dblHRA = dblHRA3;
						hmTaxInner.put("dblHRA", "");
						hmTaxInner.put("dblHRA", dblCondition3+"% of salary in other cities");
					}
				}
				
				dblHRAExemption = Math.min(dblHRAExemption, dblHRA);   
				
				ApprovePayroll objApprovePayroll = new ApprovePayroll();
				objApprovePayroll.setServletRequest(request);
				objApprovePayroll.session = session;
				objApprovePayroll.CF = CF;
				
				Map hmEmpRentPaidMap = objApprovePayroll.getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
	//			double dblHRAExemptions = objApprovePayroll.getHRAExemptionCalculation(con, uF,null, hmEmpPayrollDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, 0, 0, hmEmpMertoMap, hmEmpRentPaidMap);
				double dblHRAExemptions = objApprovePayroll.getHRAExemptionCalculation(con, uF,null, hmEmpPayrollDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, dblActualHRAPaid, dblHraSalHeadsAmount, hmEmpMertoMap, hmEmpRentPaidMap);
				dblHRAExemption = dblHRAExemptions;
				
				double dblIncomeFromOther=uF.parseToDouble(hmEmpIncomeFromOtherSourcesMap.get(strEmpId));
				
				
				Map<String, String> hmUS10Inner= (Map<String, String>)hmUnderSection10Map.get(strEmpId);
				if(hmUS10Inner==null) hmUS10Inner=new HashMap<String, String>();
				double dblMedicalAllowanceExemptLimit =uF.parseToDouble((String)hmUS10Inner.get(""+MEDICAL_ALLOWANCE));
				
				Map<String, String> hmUS10InnerPaid= (Map<String, String>)hmUnderSection10PaidMap.get(strEmpId);
				if(hmUS10InnerPaid==null) hmUS10InnerPaid=new HashMap<String, String>();
				double dblMedicalAllowancePaid =uF.parseToDouble((String)hmUS10InnerPaid.get(""+MEDICAL_ALLOWANCE));
				double dblMedicalAllowanceExempt = Math.min(dblMedicalAllowancePaid, dblMedicalAllowanceExemptLimit);
				
				double dblLTAExemptLimit =uF.parseToDouble((String)hmUS10Inner.get(""+LTA));
				double dblLTAPaid =uF.parseToDouble((String)hmUS10InnerPaid.get(""+LTA));
				double dblLTAExempt = Math.min(dblLTAPaid, dblLTAExemptLimit);
				
	//			double dblNetTaxableIncome = dblGross1 - dblProfessionalTaxExempt - dblHRAExemption - dblConveyanceAllowanceExempt - dblInvestment;
	//			double dblNetTaxableIncome = dblGross1 - dblProfessionalTaxExempt - dblHRAExemption - dblConveyanceAllowanceExempt - dblInvestmentExempt;
				double dblNetTaxableIncome = dblGross1 - dblProfessionalTaxExempt - dblHRAExemption - dblConveyanceAllowanceExempt - dblInvestmentExempt -  dblHomeLoanTaxExempt - dbl80D - dblEducationAllowanceExempt - dblMedicalAllowanceExempt -dblLTAExempt - dblOtherExemptions;
	//			double dblNetTaxableIncome = dblGross1 - dblProfessionalTaxExempt - dblHRAExemption - dblConveyanceAllowanceExempt - dblInvestmentExempt - dblOtherExemptions - dblHomeLoanTaxExempt - dblIncomeFromOther;
				
				if(strEmpId.equalsIgnoreCase("654")){
	//				System.out.println(strEmpId+" dblNetTaxableIncome=====>"+dblNetTaxableIncome);  
				}
				
				hmTaxInner.put("dblIncomeFromOther", dblIncomeFromOther+"");  
				hmTaxInner.put("dblActualHRAPaid", dblActualHRAPaid+"");  
				hmTaxInner.put("dblActualRentPaid", dblActualRentPaid+"");
				hmTaxInner.put("dblHRA1", dblHRA+"");
				hmTaxInner.put("dblCondition1", dblCondition1+"");
				hmTaxInner.put("dblHRAExemption", dblHRAExemption+"");
				
				hmTaxInner.put("dblProfessionalTaxPaid", dblProfessionalTaxPaid+"");
				hmTaxInner.put("dblProfessionalTaxExempt", dblProfessionalTaxExempt+"");
				
				hmTaxInner.put("dblConveyanceAllowancePaid", dblConveyanceAllowancePaid+"");
				hmTaxInner.put("dblConveyanceAllowanceExempt", dblConveyanceAllowanceExempt+"");
				
				hmTaxInner.put("dblOtherExemptions", dblOtherExemptions+"");
				hmTaxInner.put("dblHomeLoanTaxExempt", dblHomeLoanTaxExempt+"");
				
				hmTaxInner.put("dblInvestment", dblInvestmentExempt+"");
	//			hmTaxInner.put("dblInvestment", hmEmpExemptionsMap.get(strEmpId) +"");
				hmTaxInner.put("hmEmpExemptionsCH1Map", hmEmpExemptionsCH1Map.get(strEmpId) +"");
				hmTaxInner.put("hmEmpExemptionsCH2Map", hmEmpExemptionsCH2Map.get(strEmpId) +"");
				hmTaxInner.put(TDS, strTDSAmt);
				
				hmTaxInner.put("dblEducationAllowancePaid", dblEducationAllowancePaid+"");
				hmTaxInner.put("dblEducationAllowanceExempt", dblEducationAllowanceExempt+""); 
				
				hmTaxInner.put("dblMedicalAllowanceExempt", dblMedicalAllowanceExempt+"");
				hmTaxInner.put("dblMedicalAllowancePaid", dblMedicalAllowancePaid+"");
				hmTaxInner.put("dblEmpPF", dblEmpPF+""); 
				
				hmTaxInner.put("dblLTAExempt", dblLTAExempt+"");
				hmTaxInner.put("dblLTAPaid", dblLTAPaid+"");
	
				double TDSPayable = 0;
				if(strEmpId!=null && strEmpId.equalsIgnoreCase("654")){					
					
	//				System.out.println("dblGross="+dblGross1);
	//				System.out.println("dblProfessionalTaxExempt="+dblProfessionalTaxExempt);
	//				System.out.println("dblHRAExemption="+dblHRAExemption);
	//				System.out.println("dblConveyanceAllowanceExempt="+dblConveyanceAllowanceExempt); 
	//				System.out.println("dblInvestment="+dblInvestmentExempt);
	//				System.out.println("dblHomeLoanTaxExempt="+dblHomeLoanTaxExempt);
	//				System.out.println("dblOtherExemptions="+dblOtherExemptions);
	//				System.out.println("dblIncomeFromOther="+dblIncomeFromOther);
	//				System.out.println("dblNetTaxableIncome="+dblNetTaxableIncome);    
	//				System.out.println("dblCess1="+dblCess1);
	//				System.out.println("dblCess2="+dblCess2);
	//				System.out.println("strFinancialYearStart="+strFinancialYearStart);
	//				System.out.println("strFinancialYearEnd="+strFinancialYearEnd);
	//				System.out.println("Gender="+(String)hmEmpGenderMap.get(strEmpId));
	//				System.out.println("Age="+(String)hmEmpAgeMap.get(strEmpId));					
	//				System.out.println("dblEducationAllowancePaid="+dblEducationAllowancePaid);
	//				System.out.println("dblEducationAllowanceExempt="+dblEducationAllowanceExempt);
					
	//				TDSPayable = calculateTDS(dblNetTaxableIncome, dblCess1, dblCess2, strFinancialYearStart, strFinancialYearEnd, strEmpId, (String)hmEmpGenderMap.get(strEmpId), (String)hmEmpAgeMap.get(strEmpId));		
				}
				
				String strSlabType = CF.getEmpIncomeTaxSlabType(con, CF, strEmpId, strFinancialYearStart, strFinancialYearEnd);
				
				TDSPayable = calculateTDS(con, dblNetTaxableIncome, dblCess1, dblCess2, strFinancialYearStart, strFinancialYearEnd, strEmpId, (String)hmEmpGenderMap.get(strEmpId), (String)hmEmpAgeMap.get(strEmpId), strSlabType);
				
	//			double dblRebate = 0;
	//			if(dblNetTaxableIncome<=500000 && TDSPayable<=500000){
	//				if(TDSPayable>=2000){
	//					dblRebate = 2000;
	//				}else if(TDSPayable>0 && TDSPayable<2000){
	//					dblRebate = TDSPayable;
	//				}
	//			}
				double dblMaxTaxableIncome = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_MAX_TAX_INCOME"));
				double dblRebateAmt = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_REBATE_AMOUNT"));
				double dblRebate = 0;
				if(dblNetTaxableIncome <= dblMaxTaxableIncome && TDSPayable <= dblMaxTaxableIncome){
					if(TDSPayable>=dblRebateAmt){
						dblRebate = dblRebateAmt;
					}else if(TDSPayable > 0 && TDSPayable < dblRebateAmt){
						dblRebate = TDSPayable;
					}
				}
				
				double dblCess1Amount = (dblCess1 * (TDSPayable-dblRebate) / 100);
				double dblCess2Amount = (dblCess2 * (TDSPayable-dblRebate) / 100);
				
				if(strEmpId.equals("654")){
	//				System.out.println(" TAX_LIABILITY==>"+TDSPayable);        
	//				System.out.println(" CESS1==>"+dblCess1);
	//				System.out.println(" CESS2==>"+dblCess2);
	//				System.out.println(" CESS1_AMOUNT==>"+dblCess1Amount);
	//				System.out.println(" CESS2_AMOUNT==>"+dblCess2Amount);
	//				System.out.println(" dblRebate==>"+dblRebate);
	//				System.out.println(" TOTAL_TAX_LIABILITY==>"+((TDSPayable-dblRebate) + dblCess1Amount+ dblCess2Amount));
				}
				
				hmTaxInner.put("TAX_LIABILITY", TDSPayable+"");
				hmTaxInner.put("CESS1", dblCess1+"");
				hmTaxInner.put("CESS2", dblCess2+"");
				hmTaxInner.put("CESS1_AMOUNT", dblCess1Amount+"");
				hmTaxInner.put("CESS2_AMOUNT", dblCess2Amount+"");
				hmTaxInner.put("TOTAL_TAX_LIABILITY", ((TDSPayable-dblRebate) + dblCess1Amount+ dblCess2Amount)+""); 
				hmTaxInner.put("TAX_REBATE", dblRebate+"");
				hmTaxLiability.put(strEmpId, hmTaxInner);
				
				
			}
			   
			
			
			pst = con.prepareStatement("select sum(amount) as amount, emp_id from payroll_generation where salary_head_id = ? and financial_year_from_date=? and financial_year_to_date=? and emp_id=? and is_paid = true group by emp_id");
			pst.setInt(1, TDS);
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
	//		System.out.println(" pst==>"+pst);
	//		System.out.println("9 date==>"+new Date());
			Map<String, String> hmPaidTdsMap = new HashMap<String, String>();
			while(rs.next()){
				hmPaidTdsMap.put(rs.getString("emp_id"), rs.getString("amount"));
			}
			rs.close();
			pst.close();
	//		System.out.println("=======14========"+new Date());      
			
			pst = con.prepareStatement("select * from document_activities where effective_date =? and document_name=? and emp_id=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(2, "Form 16");
			pst.setInt(3, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
	//		System.out.println(" pst==>"+pst);
	//		System.out.println("10 date==>"+new Date());
			Map<String, String> hmForm16GeneratedFiles = new HashMap<String, String>();
			while(rs.next()){
				hmForm16GeneratedFiles.put(rs.getString("emp_id"), rs.getString("document_id"));
			}
			rs.close();
			pst.close();
			
			Map hmTaxInner = (Map)hmTaxLiability.get(getEmp_id());
			if(hmTaxInner==null)hmTaxInner=new HashMap();
			double dblConveyanceAllowancePaid = uF.parseToDouble((String)hmTaxInner.get("dblConveyanceAllowancePaid"));
			double dblConveyanceAllowanceExempt = uF.parseToDouble((String)hmTaxInner.get("dblConveyanceAllowanceExempt"));
			
			Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
			Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
			Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
			Font small = new Font(Font.FontFamily.HELVETICA,7);
			Font smallBold = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD);
			Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	        Document document = new Document(PageSize.A4);
	        PdfWriter.getInstance(document,buffer);
	        document.open();
	        
	        PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);        
	        
	        //New Row
			//Bank Copy
	        PdfPCell row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("FORM NO. 16",normalwithbold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("[See rule 31(1)(a)]",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	
	        row1 =new PdfPCell(new Paragraph("PART A",normalwithbold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Certificate under section 203 of the Income- Tax Act, 1961 for tax deducted at source on salary",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Certificate No. ",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Last Update On",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Name and address of the Employer",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Name and address of the Employee",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	//        row1 =new PdfPCell(new Paragraph(CF.getStrOrgName()+"\n"+CF.getStrOrgAddress(),small));
	//        String orgAddress="KIRTANE & PANDIT -M/S\n" +
	//        		"73/2/2,SANGATI,BHAKTI MARG,\n" +
	//        		"OFF LAW COLLEGE ROAD,PUNE-41104\n" +
	//        		"Maharashtra\n" +
	//        		"+(91)20-25433104\n" +
	//        		"accounts@kirtanepandit.com";
	//        row1 =new PdfPCell(new Paragraph(orgAddress,small)); 
	        String orgAddress = uF.showData(hmOrg.get("ORG_NAME"), "")+"\n"+uF.showData(hmOrg.get("ORG_ADDRESS"), "")+"\n"+uF.showData(hmStates.get(hmOrg.get("ORG_STATE_ID")), "")+"\n"+uF.showData(hmOrg.get("ORG_CONTACT"), "")+"\n"+uF.showData(hmOrg.get("ORG_EMAIL"), ""); 
	        row1 =new PdfPCell(new Paragraph(orgAddress,small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmEmployeeMap.get(getEmp_id()), "")+"\n"+uF.showData(hmEmpAddressMap.get(getEmp_id()), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("PAN of the Deductor",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("TAN of the Deductor",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("PAN of the Employee",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Employee Reference No.\nprovided by the Employer\n(If applicable)",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmOtherDetailsMap.get("DEDUCTOR_PAN"), " "),small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmOtherDetailsMap.get("DEDUCTOR_TAN"), " "),small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmOtherDetailsMap.get("EMPLOYEE_PAN"), " "),small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("CIT(TDS)",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Assessment Year",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Period With the Employer",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	//        String citData="The Commissioner of Income Tax (TDS)\n" +
	//		"4th Floor, A Wing , PMT Commercial Complex, Shankar Sheth  Road,\nSwargate , Pune – 411037";
	        String citData=uF.showData(hmOtherDetailsMap.get("CIT_ADDRESS"), " ");
	        
	        row1 =new PdfPCell(new Paragraph(citData,small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph((uF.parseToInt(uF.getDateFormat(strFinancialYearStart,DATE_FORMAT, "yyyy"))+1) +"-"+ (uF.parseToInt(uF.getDateFormat(strFinancialYearEnd,DATE_FORMAT, "yyyy"))+1),small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.getDateFormat(strFinancialYearStart,DATE_FORMAT, "yyyy"),small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.getDateFormat(strFinancialYearEnd,DATE_FORMAT, "yyyy"),small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Summary of amount paid / credited and tax deducted at source thereon in respect of the employee",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Quarter(s)",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Receipt Numbers of original\n statements of TDS\nunder sub-section (3) of\nsection 200",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Amount Paid/ credited",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Amount of Tax Deducted (Rs.)",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Amount of Tax deposited/ remitted\n(Rs.)",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Q1",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));//GDIXXFIG
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Q2",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small)); //GDIXCEAE
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Q3",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small)); //QARNYPZF
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Q4",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small)); //QQOGQCVA
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Total(Rs.)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("I. DETAILS OF TAX DEDUCTED AND DEPOSITED IN THE CENTRAL GOVERNMENT ACCOUNT THROUGH BOOK ADJUSTMENT\n" +
	        		"(The deductor to provide payment wise details of tax deducted and deposited with respect to the deductee)",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("SI. No.",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setRowspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Tax Deducted in respect of the Deductee\n (Rs.)",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setRowspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Book Identification Number (BIN)",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Receipt Numbers of\n Form No. 24G",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("DDO serial number in \nForm No.24G",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Date of transfer voucher\n (dd/mm/yyyy)",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Status of matching with\n Form No. 24G",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Total (Rs.)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("II. DETAILS OF TAX DEDUCTED AND DEPOSITED IN THE CENTRAL GOVERNMENT ACCOUNT THROUGH CHALLAN\n" +
	        		"(The deductor to provide payment wise details of tax deducted and deposited with respect to the deductee)",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("SI. No.",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setRowspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Tax Deducted in respect of the Deductee\n (Rs.)",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setRowspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Challan Identification Number (CIN)",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("BSR Code of the Bank Branch",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Date on which tax deposited\n(dd/mm/yyyy)",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Challan Serial Number",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Status of matching with\n OLTAS",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("1",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("2",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("3",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("4",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        document.add(table);
	        
	        document.newPage();
	        
	        PdfPTable table1 = new PdfPTable(6);
			table1.setWidthPercentage(100);        
	        
	        
	        PdfPCell row2 =new PdfPCell(new Paragraph("Emp Code ",smallBold));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.NO_BORDER);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(uF.showData(hmEmpCode.get(getEmp_id()), ""),small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.NO_BORDER);
	        row2.setPadding(2.5f);
	        row2.setColspan(4);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("PAN  "+uF.showData(hmEmpPanNo.get(getEmp_id()), ""),smallBold));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.NO_BORDER);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("Emp Name ",smallBold));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.NO_BORDER);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(uF.showData(hmEmployeeMap.get(getEmp_id()), ""),small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.NO_BORDER);
	        row2.setPadding(2.5f);
	        row2.setColspan(4);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("Certificate No. ",smallBold));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.NO_BORDER);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("Part B (Annexure)",normalwithbold));
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setPadding(2.5f);
	        row2.setColspan(6);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("Details of salary paid and other tax deducted",smallBold));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(6);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("RS.",small));
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("RS.",small));
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("RS.",small));
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("RS.",small));
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("1. Gross Salary",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblGross),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("  a)Salary as per provisions contained in sc 17 (1)",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        row2.setIndent(5.0f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("  b) Value of perquisites u/s 17(2)\n(as per Form No. 12BA, wherever applicable)",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setIndent(5.0f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("  c) Profits in Lieu of salary under 17(3)\n(as per Form No. 12BA, wherever applicable)",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setIndent(5.0f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("  d)Total",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setIndent(5.0f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblGross),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("2. Less: allowance to the extent exempt u/s 10",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("  a)Conveyance Allowance",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setIndent(5.0f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblConveyanceAllowancePaid),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblConveyanceAllowanceExempt),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        double dblMedicalAllowanceExempt =uF.parseToDouble((String)hmTaxInner.get("dblMedicalAllowanceExempt"));
	        double dblMedicalAllowancePaid =uF.parseToDouble((String)hmTaxInner.get("dblMedicalAllowancePaid"));
	        row2 =new PdfPCell(new Paragraph("  b)Medical Allowance",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setIndent(5.0f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblMedicalAllowancePaid),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblMedicalAllowanceExempt),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        double dblHRAExemption = uF.parseToDouble((String)hmTaxInner.get("dblHRAExemption"));
	        row2 =new PdfPCell(new Paragraph("  c)HRA",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setIndent(5.0f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblHRAExemption),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        double dblEducationAllowancePaid = uF.parseToDouble((String)hmTaxInner.get("dblEducationAllowancePaid"));
			double dblEducationAllowanceExempt = uF.parseToDouble((String)hmTaxInner.get("dblEducationAllowanceExempt"));
	        row2 =new PdfPCell(new Paragraph("  d) Education Allowance",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setIndent(5.0f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblEducationAllowancePaid),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblEducationAllowanceExempt),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        double dblLTAPaid = uF.parseToDouble((String)hmTaxInner.get("dblLTAPaid"));
			double dblLTAExempt = uF.parseToDouble((String)hmTaxInner.get("dblLTAExempt"));
	        row2 =new PdfPCell(new Paragraph("  e) Leave Travel Allowances[L.T.C.]",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setIndent(5.0f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblLTAPaid),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblLTAExempt),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        double dblOtherExemption = uF.parseToDouble((String)hmTaxInner.get("dblOtherExemptions"));
	        row2 =new PdfPCell(new Paragraph("   f) Other Exemptions",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setIndent(5.0f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblOtherExemption),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	//        double dblGrossNet = Math.round(dblGross) - Math.round(dblHRAExemption) - Math.round(dblConveyanceAllowanceExempt) - Math.round(dblOtherExemption) - Math.round(dblHomeLoanTaxExempt) - Math.round(dblMedicalAllowanceExempt);
	        double dblGrossNet = Math.round(dblGross) - Math.round(dblHRAExemption) - Math.round(dblConveyanceAllowanceExempt) - Math.round(dblOtherExemption) - Math.round(dblMedicalAllowanceExempt) - Math.round(dblEducationAllowanceExempt) - Math.round(dblLTAExempt);
	        row2 =new PdfPCell(new Paragraph("3. Balance (1-2)",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblGrossNet),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("4. Less: allowance to the extent exempt u/s 16",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("  a)Entertainment Allowance",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setIndent(5.0f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        double dblProfessionalTaxPaid = uF.parseToDouble((String)hmTaxInner.get("dblProfessionalTaxPaid"));
			double dblProfessionalTaxExempt = uF.parseToDouble((String)hmTaxInner.get("dblProfessionalTaxExempt"));
	        row2 =new PdfPCell(new Paragraph("  b) Professional Tax",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setIndent(5.0f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	//        row2 =new PdfPCell(new Paragraph(""+Math.round(dblProfessionalTaxPaid),small));
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblProfessionalTaxExempt),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	//        row2 =new PdfPCell(new Paragraph(""+Math.round(dblProfessionalTaxExempt),small));
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("5. Aggregate of 4(a) and (b)",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+(Math.round(dblProfessionalTaxExempt)),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        double dblHomeLoanTaxExempt = uF.parseToDouble((String)hmTaxInner.get("dblHomeLoanTaxExempt"));
	        row2 =new PdfPCell(new Paragraph("6. Home Loan Interest",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));  
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblHomeLoanTaxExempt),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	//        dblGrossNet -=  (Math.round(dblProfessionalTaxExempt)+Math.round(dblEducationAllowanceExempt));
	        dblGrossNet -=  (Math.round(dblProfessionalTaxExempt));
	        dblGrossNet -=  Math.round(dblHomeLoanTaxExempt);
	        row2 =new PdfPCell(new Paragraph("7. Income Chargeable under the \nhead 'salaries ((3-5)-6)",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblGrossNet),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        double dblIncomeFromOther=uF.parseToDouble((String)hmTaxInner.get("dblIncomeFromOther"));
	        row2 =new PdfPCell(new Paragraph("8. + Any other income reported by the employee",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblIncomeFromOther),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        dblGrossNet += Math.round(dblIncomeFromOther);
	        row2 =new PdfPCell(new Paragraph("9. Gross Total Income (7+8)",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblGrossNet),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("10. Less: Deductions under Chapter VI-A",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        double dblInvestment = uF.parseToDouble((String)hmTaxInner.get("dblInvestment"));
			double dblChapterVIA=0.0d;
			List<String> alUnderSection=new ArrayList<String>();
			Map<String,String> hmInvest=hmEmpInvestment.get(getEmp_id());
			if(hmInvest==null) hmInvest = new HashMap<String, String>();
	//		if(hmInvest!=null && hmInvest.size()>0){
	//			Iterator<String> it1=hmInvest.keySet().iterator();
				int ii=0;
	//		  	while(it1.hasNext()){
				for(int a=0;chapter1SectionList!=null && a<chapter1SectionList.size();a++){
	//		  		String strSectionId=it1.next();
					String strSectionId=chapter1SectionList.get(a);
	//		  		String strAmt=hmInvest.get(strSectionId);
					String strAmt = uF.showData(hmInvest.get(strSectionId), "");
			  		Map<String, List<Map<String, String>>> hmSubInvestment = (Map<String, List<Map<String, String>>>) hmEmpSubInvestment.get("" + getEmp_id());
			  		if(hmSubInvestment==null) hmSubInvestment= new HashMap<String, List<Map<String, String>>>();
			  		List<Map<String, String>> subInvestList = (List<Map<String, String>>) hmSubInvestment.get("" +strSectionId);
			  		String strUnderSection="";
			  		
	//		  		if(hmSectionMap.get(strSectionId).equals("80C and 80CCC")){
			  		if(hmSectionMap.containsKey(strSectionId)){
			  			strAmt=""+Math.round(dblInvestment);
			  		}
			  		dblChapterVIA+=uF.parseToDouble(strAmt);
			  		if(ii==0){ 
			  			row2 =new PdfPCell(new Paragraph("A). Sections 80C, 80CCC and  80CCD",small));
				        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        row2.setIndent(5.0f);
				        row2.setColspan(2);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small)); 
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
			  			
			  		}
			  		ii++;
			  		
			  		row2 =new PdfPCell(new Paragraph("   "+ii+". "+uF.showData(hmSectionMap.get(strSectionId), ""),small));
			        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        row2.setIndent(10.0f);
			        row2.setColspan(2);
			        table1.addCell(row2);
			        
			        Map<String,String> hmAcutualInvest=hmEmpActualInvestment.get(getEmp_id());
			        if(hmAcutualInvest==null) hmAcutualInvest= new HashMap<String, String>();
			        
			        row2 =new PdfPCell(new Paragraph((subInvestList==null ? ""+Math.round(uF.parseToDouble(hmAcutualInvest.get(strSectionId))) : ""),small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("",small)); 
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("",small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
	//		        row2 =new PdfPCell(new Paragraph((subInvestList==null && !hmSectionMap.get(strSectionId).equals("80C and 80CCC") ? ""+Math.round(uF.parseToDouble(strAmt)) : ""),small));
			        row2 =new PdfPCell(new Paragraph((subInvestList==null && !hmSectionMap.containsKey(strSectionId) ? ""+Math.round(uF.parseToDouble(strAmt)) : ""),small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			  		
			        int k=0;
	//		        if(hmSectionMap.get(strSectionId).equals("80C and 80CCC")){
			        if(hmSectionMap.containsKey(strSectionId)){
			        	k++;
			        	row2 =new PdfPCell(new Paragraph("   "+(k)+". PF",small));
				        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        row2.setIndent(15.0f);
				        row2.setColspan(2);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph(""+Math.round(uF.parseToDouble(""+hmTaxInner.get("dblEmpPF")) ),small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small)); 
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
			  		}
			  		
			  		for (int j = 0; subInvestList != null && j < subInvestList.size(); j++) {
						Map<String, String> hm = (Map<String, String>) subInvestList.get(j);
						k++;
						row2 =new PdfPCell(new Paragraph("   "+(k)+". "+uF.showData(hm.get("SECTION_NAME"), ""),small));
				        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        row2.setIndent(15.0f);
				        row2.setColspan(2);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph(""+uF.showData(hm.get("PAID_AMOUNT"), ""),small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small)); 
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
						
			  		}
			  		
	//		  		if(subInvestList!=null || hmSectionMap.get(strSectionId).equals("80C and 80CCC")){
			  		if(subInvestList!=null || hmSectionMap.containsKey(strSectionId)){
			  			row2 =new PdfPCell(new Paragraph("",small));
				        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        row2.setColspan(2);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small)); 
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph(""+Math.round(uF.parseToDouble(strAmt)),small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
			  		}
			  	}
	//		}
			
			
			Map<String,String> hmInvest1=hmEmpInvestment1.get(getEmp_id());
			if(hmInvest1==null) hmInvest1 = new HashMap<String, String>();
	//		if(hmInvest1!=null && hmInvest1.size()>0){
	//			Iterator<String> it2=hmInvest1.keySet().iterator();
				ii=0;
	//		  	while(it2.hasNext()){
				for(int a=0;chapter2SectionList!=null && a<chapter2SectionList.size();a++){
	//		  		String strSectionId=it2.next();
					String strSectionId=chapter2SectionList.get(a);
	//		  		String strAmt=hmInvest1.get(strSectionId);
					String strAmt = uF.showData(hmInvest1.get(strSectionId), "");
			  		dblChapterVIA+=uF.parseToDouble(strAmt);
			  		
			  		Map<String, List<Map<String, String>>> hmSubInvestment = (Map<String, List<Map<String, String>>>) hmEmpSubInvestment.get("" + getEmp_id());
			  		if(hmSubInvestment==null) hmSubInvestment= new HashMap<String, List<Map<String, String>>>();
			  		List<Map<String, String>> subInvestList = (List<Map<String, String>>) hmSubInvestment.get("" +strSectionId);
			  		String strUnderSection="";
			  		
			  		if(ii==0){
			  			row2 =new PdfPCell(new Paragraph("B). Other sections (eg. 80E, 80G, 80TTA, etc.)\n under VI-A",small));
				        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        row2.setIndent(5.0f);
				        row2.setColspan(2);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small)); 
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
			  		}
			  		ii++;
			  		
			  		row2 =new PdfPCell(new Paragraph("    "+ii+". "+uF.showData(hmSectionMap.get(strSectionId), ""),small));
			        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        row2.setIndent(10.0f);
			        row2.setColspan(2);
			        table1.addCell(row2);
			        
			        Map<String,String> hmAcutualInvest=hmEmpActualInvestment1.get(getEmp_id());
			        if(hmAcutualInvest==null) hmAcutualInvest= new HashMap<String, String>();
			        
			        row2 =new PdfPCell(new Paragraph((subInvestList==null ? ""+Math.round(uF.parseToDouble(hmAcutualInvest.get(strSectionId))) : ""),small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("",small)); 
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph("",small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			        
			        row2 =new PdfPCell(new Paragraph(""+(subInvestList==null ? ""+Math.round(uF.parseToDouble(strAmt)) : ""),small));
			        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
			        row2.setPadding(2.5f);
			        table1.addCell(row2);
			  		
			  		for (int j = 0; subInvestList != null && j < subInvestList.size(); j++) {
						Map<String, String> hm = (Map<String, String>) subInvestList.get(j);
						
						row2 =new PdfPCell(new Paragraph("   "+(j+1)+". "+uF.showData(hm.get("SECTION_NAME"), ""),small));
				        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        row2.setIndent(10.0f);
				        row2.setColspan(2);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph(""+uF.showData(hm.get("PAID_AMOUNT"), ""),small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small)); 
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
						
			  		}
			  		
			  		if(subInvestList!=null){
			  			row2 =new PdfPCell(new Paragraph("",small));
				        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        row2.setColspan(2);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small)); 
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph("",small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
				        
				        row2 =new PdfPCell(new Paragraph(""+Math.round(uF.parseToDouble(strAmt)),small));
				        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
				        row2.setPadding(2.5f);
				        table1.addCell(row2);
			  		}
			  	}
	//		}
	        
			row2 =new PdfPCell(new Paragraph("11. Aggregate of deductible Amount under Chapter VI-A",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        row2.setNoWrap(true);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblChapterVIA),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        dblGrossNet -= Math.round(dblChapterVIA);
	        row2 =new PdfPCell(new Paragraph("12. Total Income (9-11) [Round off u/s 288B]",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblGrossNet),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        double dblTaxLiability = uF.parseToDouble((String)hmTaxInner.get("TAX_LIABILITY"));
	        row2 =new PdfPCell(new Paragraph("13. Tax on Total Income",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblTaxLiability),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        double dblRebate = uF.parseToDouble((String)hmTaxInner.get("TAX_REBATE")) ;
	        row2 =new PdfPCell(new Paragraph("14. less Tax Rebate u/s 87 A",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblRebate),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        double dblCess1 = uF.parseToDouble((String)hmTaxInner.get("CESS1"));
			double dblCess1Amount = uF.parseToDouble((String)hmTaxInner.get("CESS1_AMOUNT"));
	        row2 =new PdfPCell(new Paragraph("15. Add: Ed. Cess @ "+dblCess1+"%",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblCess1Amount),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        double dblCess2 = uF.parseToDouble((String)hmTaxInner.get("CESS2")) ;
			double dblCess2Amount = uF.parseToDouble((String)hmTaxInner.get("CESS2_AMOUNT"));
	        row2 =new PdfPCell(new Paragraph("16. Add: She Cess @ "+dblCess2+"%",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblCess2Amount),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        double dblTotalTaxLiability = uF.parseToDouble((String)hmTaxInner.get("TOTAL_TAX_LIABILITY"));
	        row2 =new PdfPCell(new Paragraph("17. Tax Payable ((13-14)+15+16)",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblTotalTaxLiability),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("18. Less: relief under section 89 (attach form 10E)",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("19. Tax Payable (17+18)  [Round off u/s 288B]",small));  
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(dblTotalTaxLiability),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("20. Tax deducted at source u/s 192 (1)",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(uF.parseToDouble((String)hmPaidTdsMap.get(getEmp_id()))),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("21. Tax Payable (19-20)",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small)); 
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph(""+Math.round(Math.round(dblTotalTaxLiability) - Math.round(uF.parseToDouble((String)hmPaidTdsMap.get(getEmp_id())))),small));
	        row2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row2.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("Verification",smallBold));
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setPadding(2.5f);
	        row2.setColspan(6);
	        table1.addCell(row2);
	        
	        String strAuthorisedName=uF.showData(hmAuthorised.get("EMP_NAME"), "");
	//        String str1="Suhas Deshpande";
	        String strAuthorisedMiddleName=uF.showData(hmAuthorised.get("EMP_MIDDLE_NAME"), "");
	//        String str2="Gajanan";
	        
	        row2 =new PdfPCell(new Paragraph("I "+strAuthorisedName+", son/ daughter of "+strAuthorisedMiddleName+" working " +
			"in the capacity of PARTNER (designation) do hereby certify that the information given above is ture," +
			"complete and correct and is based on the books of account,documents,TDS statements," +
			"TDS deposited and other available records.",smallBold));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(6);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("Place:",smallBold));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("Pune",smallBold));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("\n\n(Signature of person responsible for deduction of tax)",smallBold));
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setPadding(2.5f);
	        row2.setColspan(3);
	        row2.setRowspan(2);	        
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("Date:",smallBold));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	//        String strDate="31.05.2014";
	        String strDate = strFinancialYearEnd;
	        row2 =new PdfPCell(new Paragraph(strDate,smallBold));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("Designation",smallBold));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setPadding(2.5f);
	        table1.addCell(row2);
	        
	        row2 =new PdfPCell(new Paragraph("PARTNER",smallBold));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(2);
	        table1.addCell(row2);
	        
	//        String strPartner = "SUHAS GAJANAN DESHPANDE";
	        row2 =new PdfPCell(new Paragraph("Full Name: "+strAuthorisedName,smallBold));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setPadding(2.5f);
	        row2.setColspan(3);
	        table1.addCell(row2);
	        
	        document.add(table1);
	        
	        document.close();
	          
			String filename="Form16_"+getEmp_id()+"_"+uF.getDateFormat(strFinancialYearStart,DATE_FORMAT, "yyyy") +".pdf";
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename="+filename+"");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	public Map<String, String> getDeductionDetailsMap(int empid, String strFinancialYearStart, String strFinancialYearEnd) {
		Map hmDeductionDetailsMap = new HashMap();

		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);


			pst = con.prepareStatement("select distinct(section_code) as section_code,amount_paid from investment_details id," +
					"section_details sd where sd.section_id=id.section_id and fy_from=? and fy_to=? and status = true and section_code not in ('HRA') and sd.section_id not in (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%') and emp_id=? group by " +
					"section_code,amount_paid");
			
			
			pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3,empid);
			rs = pst.executeQuery();
			
			int count = 0;
			while (rs.next()) {
				Map hmInner=new HashMap();
				aggregateDeductionAmount+=rs.getDouble("amount_paid");
				hmInner.put("AMOUNTPAID", rs.getString("amount_paid"));
				hmInner.put("SECTIONCODE", rs.getString("section_code"));
				hmDeductionDetailsMap.put("DECUCTION"+count,hmInner);
				count++;
			}
			rs.close();
			pst.close();
			
			Map hmInner=new HashMap();
			
			pst = con.prepareStatement("select sum(amount) as amount from payroll_generation where financial_year_from_date=? and financial_year_to_date=? and emp_id = ? and salary_head_id =? and is_paid=true");
			pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, empid);
			pst.setInt(4, EMPLOYEE_EPF);
			rs = pst.executeQuery();
			
			while(rs.next()){
				
				aggregateDeductionAmount+=rs.getDouble("amount");
				hmInner.put("AMOUNTPAID", rs.getString("amount"));
				hmInner.put("SECTIONCODE", "EPF");
				hmDeductionDetailsMap.put("DECUCTION"+count,hmInner);
				count++;
			}
			rs.close();
			pst.close();
			
			
			double dblInvestmentExemption = 0;
			pst = con.prepareStatement("select * from settings");
			rs = pst.executeQuery();
			while(rs.next()){
				if(rs.getString("options").equalsIgnoreCase("INVESTMENT_EXEMPTION")){
					dblInvestmentExemption = uF.parseToDouble(rs.getString("value"));
				}
			}
			rs.close();
			pst.close();
			
			aggregateDeductionAmount = Math.min(dblInvestmentExemption, aggregateDeductionAmount);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmDeductionDetailsMap;
	} 
	
	
	public Map<String, String> getOtherDetailsMap(int empid) {
		Map<String, String> hmOtherDetailsMap = new HashMap<String, String>();

		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);

			pst = con.prepareStatement("select org_tan_no,org_pan_no from org_details where org_id=(select org_id from employee_official_details where emp_id=?)");
			pst.setInt(1,empid);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmOtherDetailsMap.put("DEDUCTOR_TAN", rs.getString("org_tan_no"));
				hmOtherDetailsMap.put("DEDUCTOR_PAN", rs.getString("org_pan_no"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select emp_pan_no from employee_personal_details where emp_per_id=?");
			pst.setInt(1, empid);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmOtherDetailsMap.put("EMPLOYEE_PAN",rs.getString("emp_pan_no"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select wlocation_cit_address from work_location_info where wlocation_id=(select wlocation_id from employee_official_details where emp_id=?)");
			pst.setInt(1, empid);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmOtherDetailsMap.put("CIT_ADDRESS",rs.getString("wlocation_cit_address"));
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
		return hmOtherDetailsMap;
	} 
	
	public Map<String, String> getDetailsOfSalaryMap(int empid) {
		Map hmDetailsOfSalaryMap = new HashMap();

		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			
			
			if (getFinancialYear() != null) {
				
				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			
			} else {
				
				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
				
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
				 
			}
			con = db.makeConnection(con);
			
			Map hmSalaryHeadDetails=new HashMap();
			pst = con.prepareStatement("select distinct(salary_head_id) as salary_head_id,salary_head_name from salary_details order by salary_head_id");
			rs = pst.executeQuery();
			while(rs.next()){
				hmSalaryHeadDetails.put( rs.getString("salary_head_id"), rs.getString("salary_head_name"));
			}
			rs.close();
			pst.close();
				

			
			Map hmExemptionDetails=new HashMap();
			pst = con.prepareStatement("select exemption_description,exemption_limit from exemption_details where " +
					"exemption_from=? and exemption_to=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
			rs = pst.executeQuery();
			while(rs.next()){
				hmExemptionDetails.put( rs.getString("exemption_description"), rs.getString("exemption_limit"));
			}
			rs.close();
			pst.close();
				
//			System.out.println("hmExemptionDetails====>"+hmExemptionDetails);
			pst = con.prepareStatement("select sum(amount) as amount,salary_head_id from payroll_generation where" +
					" financial_year_from_date=? and financial_year_to_date=? and amount>0 and " +
					"earning_deduction=? and emp_id=? group by salary_head_id order by salary_head_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(3,"E");
			pst.setInt(4, empid);
			
			rs = pst.executeQuery();
			
			double taxableAmount=0.0;
			double taxFreeAmount=0.0;
			int count = 0;
			while (rs.next()) {
				Map hmInner = new HashMap();
				totalSalary+=rs.getDouble("amount");
				hmInner.put("GROSSAMOUNT", rs.getString("amount"));
				hmInner.put("SALARYHEAD", hmSalaryHeadDetails.get(rs.getString("salary_head_id")));
				
				if(hmExemptionDetails.containsKey(hmSalaryHeadDetails.get(rs.getString("salary_head_id")))){
					
					if(rs.getDouble("amount")< uF.parseToDouble(hmExemptionDetails.get(hmSalaryHeadDetails.get(rs.getString("salary_head_id")))+"")){
						
						hmInner.put("TAXFREEAMOUNT", rs.getString("amount"));
						taxFreeAmount=rs.getDouble("amount");
						totalTaxFreeAmount+=taxFreeAmount;
					} else{ 
					hmInner.put("TAXFREEAMOUNT", hmExemptionDetails.get(hmSalaryHeadDetails.get(rs.getString("salary_head_id"))));
					taxFreeAmount=uF.parseToDouble(hmExemptionDetails.get(hmSalaryHeadDetails.get(rs.getString("salary_head_id")))+"");
					totalTaxFreeAmount+=taxFreeAmount;
					}
				}else{
					taxFreeAmount=0.0;
				}
				
				taxableAmount=rs.getDouble("amount")-taxFreeAmount;
				totalTaxableAmount+=taxableAmount;
				hmInner.put("TAXABLEAMOUNT",taxableAmount);
				
				hmDetailsOfSalaryMap.put("SALARYDETAILS" + count, hmInner);
				count++;
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
		return hmDetailsOfSalaryMap;
	} 
	
	public Map<String, Map<String, String>> getTaxPaidMap(int empid) {
		Map<String, Map<String, String>> hmTaxPaidMap = new HashMap<String, Map<String, String>>();

		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			
			
			if (getFinancialYear() != null) {
				
				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			
			} else {
				
				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
				
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
				 
			}
			con = db.makeConnection(con);
			
			double dbCess1 = 0;
			double dbCess2 = 0;
			pst = con.prepareStatement("Select * FROM settings");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(rs.getString("options").equalsIgnoreCase(O_STANDARD_CESS)){
					dbCess1 = uF.parseToDouble(rs.getString("value"));
				}
				if(rs.getString("options").equalsIgnoreCase(O_EDUCATION_CESS)){
					dbCess2 = uF.parseToDouble(rs.getString("value"));
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select amount,paid_date,challan_no,cheque_no,brc_code from challan_details where emp_id=? and is_paid=? and challan_type=? and "
							+ "financial_year_from_date=? and financial_year_to_date=?");
			pst.setInt(1, empid);
			pst.setBoolean(2, true);
			pst.setInt(3, TDS);
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			double eduCess=0.0;
			double stdCess=0.0;
			double TDSpaid=0.0;
			int count = 0;
			while (rs.next()) {
				Map<String, String> hmInner = new HashMap<String, String>();
				totalTax += rs.getDouble("amount");
				
				double dblX = totalTax *100 / (100 + dbCess1 + dbCess2);
				
				
				
				eduCess=(dblX*dbCess2)/100;
				stdCess=(dblX*dbCess1)/100;
				
//				TDSpaid=rs.getDouble("amount")-eduCess - stdCess;
				TDSpaid = dblX;
				
				TDSPaidAmount+=TDSpaid;
				totalEducationCess+=eduCess;
				totalStandardCess+=stdCess;
				
				hmInner.put("TOTALTAX", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
				hmInner.put("EDUCESS", uF.formatIntoTwoDecimalWithOutComma(eduCess));
				hmInner.put("STDCESS", uF.formatIntoTwoDecimalWithOutComma(stdCess));
				hmInner.put("TDSPAID", uF.formatIntoTwoDecimalWithOutComma(TDSpaid));
				hmInner.put("PAIDDATE", rs.getString("paid_date"));
				hmInner.put("CHALLANNO", rs.getString("challan_no"));
				hmInner.put("CHEQUENO", rs.getString("cheque_no"));
				hmInner.put("BRSCODE", rs.getString("brc_code"));
				
				hmTaxPaidMap.put("TAXDETAILS" + count, hmInner);
				count++;
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
		return hmTaxPaidMap;
	} 
	public double getTaxAndSalaryDetails()
	{

		double total_salary = 0.0;

		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			
			
			if (getFinancialYear() != null) {
				
				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			
			} else {
				
				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
				
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
				 
			}
			
			
			con = db.makeConnection(con);

			
			pst = con.prepareStatement("select sum(amount) as amount from payroll_generation where emp_id=? and " +
					"financial_year_from_date=? and financial_year_to_date=? and earning_deduction=? and is_paid = true");
			pst.setInt(1,uF.parseToInt(getEmp_id()));
			pst.setDate(2,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(4,"E");
			rs = pst.executeQuery();
			while (rs.next()) {
				total_salary=rs.getDouble("amount");

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
	
	
		return total_salary;
	}
	
	
private double calculateTDS(Connection con, double dblTotalTaxableSalary, double dblCess1, double dblCess2, String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, String strGender, String strAge,String slabType) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		double dblTotalTDSPayable = 0.0d;
		
		try {
			
			int countBug = 0;
			double dblUpperDeductionSlabLimit = 0;
			double dblLowerDeductionSlabLimit = 0;
			double dblTotalNetTaxableSalary = 0; 
			do{
				
				pst = con.prepareStatement("select * from deduction_tax_details where age_from <= ? and age_to > ? and gender = ? and financial_year_from = ? and financial_year_to = ?  and _from <= ? and _to>?  and slab_type = ? order by _from limit 1");
				pst.setDouble(1, uF.parseToDouble(strAge));
				pst.setDouble(2, uF.parseToDouble(strAge));
				pst.setString(3, strGender);
				pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDouble(6, dblTotalTaxableSalary);
				pst.setDouble(7, dblUpperDeductionSlabLimit);
				pst.setInt(8,uF.parseToInt(slabType));

				rs = pst.executeQuery();
				
//				System.out.println("pst=====>"+pst);
//				System.out.println("11 date==>"+new Date());  
				double dblDeductionAmount = 0;
				String strDeductionType = null;
				if(rs.next()){
					dblDeductionAmount = rs.getDouble("deduction_amount");
					strDeductionType = rs.getString("deduction_type");
					dblUpperDeductionSlabLimit = rs.getDouble("_to");
					dblLowerDeductionSlabLimit = rs.getDouble("_from");
				}
				rs.close();
				pst.close();
				
				if(countBug==0){
					dblTotalNetTaxableSalary = dblTotalTaxableSalary;
//					System.out.println("dblUpperDeductionSlabLimit=====>"+dblUpperDeductionSlabLimit);
//					System.out.println("dblLowerDeductionSlabLimit=====>"+dblLowerDeductionSlabLimit);
				}
//				System.out.println("dblTotalNetTaxableSalary=====>"+dblTotalNetTaxableSalary);
				if(dblTotalTaxableSalary>=dblUpperDeductionSlabLimit){
					dblTotalTDSPayable += ((dblDeductionAmount /100) *  (dblUpperDeductionSlabLimit - dblLowerDeductionSlabLimit) );
				}else{
					dblTotalTDSPayable += ((dblDeductionAmount /100) *  dblTotalNetTaxableSalary );
				}
				
				dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblUpperDeductionSlabLimit;
				
//				System.out.println("dblTotalTDSPayable=====>"+dblTotalTDSPayable);

				if(countBug==15)break;		// in case of any bug, this condition is used to avoid any stoppage 
				countBug++;
				
			}while(dblTotalNetTaxableSalary>0);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblTotalTDSPayable;

	}

	
	public double calculateTDS(double dblTotalTaxableSalary, double dblCess1, double dblCess2, String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, String strGender, String strAge,String slabType) {

		Connection con = null;
		PreparedStatement pst = null, pst1 = null;
		ResultSet rst = null, rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		double dblTotalTDSPayable = 0.0d;
		
		try {

			con = db.makeConnection(con);

			int countBug = 0;
			
			double dblUpperDeductionSlabLimit = 0;
			// double dbGrossSalarySlab = 0;

			do {
				pst = con.prepareStatement("select * from deduction_tax_details where age_from <= ? and age_to >= ? and gender = ? and _year = ?  and _from <= ? and _from>=? and slab_type = ? order by _from limit 1");

				pst.setDouble(1, uF.parseToDouble(strAge));
				pst.setDouble(2, uF.parseToDouble(strAge));
				pst.setString(3, strGender);
				pst.setInt(4, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
				pst.setDouble(5, dblTotalTaxableSalary);
				pst.setDouble(6, dblUpperDeductionSlabLimit);
				pst.setInt(7,uF.parseToInt(slabType));


//				System.out.println(strAge + " = " + uF.parseToInt(strAge) + " strAge " + strEmpId + " Deduction PST==>" + pst);

				rs = pst.executeQuery();

				double dblDeductionAmount = 0;
				String strDeductionType = null;

				if (rs.next()) {
					dblDeductionAmount = rs.getDouble("deduction_amount");
					strDeductionType = rs.getString("deduction_type");
					dblUpperDeductionSlabLimit = rs.getDouble("_to");
				}
				rs.close();
				pst.close();

				// if(dbGrossSalarySlab==0){
				// dbGrossSalarySlab = dblTotalTaxableSalary;
				// }

				if (dblTotalTaxableSalary >= dblUpperDeductionSlabLimit) {
					dblTotalTDSPayable += ((dblDeductionAmount / 100) * dblUpperDeductionSlabLimit);
				} else {
					dblTotalTDSPayable += ((dblDeductionAmount / 100) * dblTotalTaxableSalary);
				}

				dblTotalTaxableSalary = dblTotalTaxableSalary - dblUpperDeductionSlabLimit;

//				System.out.println("dblTotalTaxableSalary=" + dblTotalTaxableSalary);
//				System.out.println("dblUpperDeductionSlabLimit=" + dblUpperDeductionSlabLimit);
//				System.out.println("dblTotalTDSPayable=" + dblTotalTDSPayable);
//				System.out.println("dblTotalTaxableSalary=" + dblTotalTaxableSalary);
//				System.out.println("dblDeductionAmount=" + dblDeductionAmount);

				// System.out.println("dblExemptions="+dblExemptions);
				// System.out.println("dblTotalGrossSalary="+dblTotalGrossSalary);
				// System.out.println("dblTotalTDSPayable="+dblTotalTDSPayable);
				// System.out.println("dbGrossSalarySlab="+dbGrossSalarySlab);
				// System.out.println("dblTDSMonth="+dblTDSMonth);

				if (countBug == 15)
					break; // in case of any bug, this condition is used to
							// avoid any stoppage
				countBug++;

			} while (dblTotalTaxableSalary > 0);

			// Service tax + Education cess

			double dblCess = dblTotalTDSPayable * (dblCess1 / 100);
			dblCess += dblTotalTDSPayable * (dblCess2 / 100);

			dblTotalTDSPayable += dblCess;


//			System.out.println("dblTotalGrossSalary=" + dblTotalTaxableSalary);
//			System.out.println("dblTotalTDSPayable=" + dblTotalTDSPayable);
//			System.out.println("dblTotalTDSPayable=" + dblTotalTDSPayable);
//			System.out.println("=============" + strEmpId + "=================");

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeResultSet(rs);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return dblTotalTDSPayable;

	}
	
	
	
	
	
	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public String getStrSessionEmpId() {
		return strSessionEmpId;
	}

	public void setStrSessionEmpId(String strSessionEmpId) {
		this.strSessionEmpId = strSessionEmpId;
	}

	public String getStrSubmit() {
		return strSubmit;
	}

	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getStrSelectedEmpId() {
		return strSelectedEmpId;
	}

	public void setStrSelectedEmpId(String strSelectedEmpId) {
		this.strSelectedEmpId = strSelectedEmpId;
	}

	/*public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}*/

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	private HttpServletRequest request;
	private HttpServletResponse response;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	public String getStrMonth() {
		return strMonth;
	}
	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}
	/*public List<FillMonth> getMonthList() {
		return monthList;
	}
	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}*/
	public String getFormType() {
		return formType;
	}
	public void setFormType(String formType) {
		this.formType = formType;
	}
	

}