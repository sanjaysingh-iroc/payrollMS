package com.konnect.jpms.recruitment;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
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

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.document.HeaderFooterPageEvent;
import com.konnect.jpms.payroll.ApprovePayroll;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillJD;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.util.CandidateNotifications;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class sendCandidateOffer extends ActionSupport implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId = null;
	String strUserType = null;
	private String[] salary_head_id;
	private String[] salary_head_value;
	private String[] emp_salary_id;
	private String candidateId;
	private String CandID;
	private String recruitId;
	private String strJD;
	

	private String joiningdate;
	private String hrchoice;
	private String strinterviewcommentHR;
	private List<FillJD> JDList;
	String candiApplicationId;
	
	
	CommonFunctions CF = null;
	
	private boolean disableSalaryStructure;
	private List<FillSalaryHeads> salaryHeadList;

	

	//String recruitId = "42";
	public String execute() throws Exception {
//		System.out.println("in sendCandidateoffer");
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		UtilityFunctions uF = new UtilityFunctions();
		
		JDList = new FillJD(request).fillJD(getCandidateId());
	
		request.setAttribute(PAGE,"/jsp/recruitment/sendCandidateOfferDetails.jsp");
		setCandID(getCandidateId());
		
		if(strJD == null && JDList !=null && JDList.size()>0) {
//			System.out.println("null jD assign to 0 index of JD list ");
			//System.out.println("trJD null");
			FillJD JDrecruitId = JDList.get(0);
			recruitId = JDrecruitId.getStrJDId();
			//System.out.println("recruitId:"+recruitId);
		} else if(strJD != null) {
			//System.out.println(" not null jD assign to 1 index of JD list ");
			recruitId =  strJD;
		//	System.out.println("recruitId:"+recruitId);
		}
//		System.out.println("getCandidateId ===>> " + getCandidateId());
		
		
//		request.setAttribute("recruitId", getRecruitId());
//		viewCandidateSalaryDetails();
		//request.setAttribute("RecruitID", getRecruitId());
		String hrsubmit = request.getParameter("hrsubmit");
		if (hrsubmit != null) {
			
			candidateShortlist(uF);
			insertEmpSalaryDetails(uF);
			return insertHrInterview(uF);
		}
		return LOAD;
	}
	
	

	private void candidateShortlist(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update candidate_application_details set application_status = 2,application_status_date=? where candidate_id = ? and recruitment_id = ?");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, uF.parseToInt(getCandidateId()));
			pst.setInt(3, uF.parseToInt(getRecruitId()));
			pst.executeUpdate();
		//	System.out.println("update pst===>"+pst);
			pst.close();
			
			pst=con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?)");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			pst.setInt(2,uF.parseToInt(getCandidateId()));
//			pst.setInt(3,uF.parseToInt(getPanelId()));
			pst.setString(3, "Application Shortlisted");
			pst.setInt(4,uF.parseToInt(strSessionEmpId));
			pst.setDate	(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setInt(6, CANDI_ACTIVITY_APPLI_SHORTLIST_OR_REJECT_ID);
		//	System.out.println("insert pst===>"+pst);
			pst.execute();
			pst.close();
//			System.out.println("pst shortlist ===> "+pst);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void viewCandidateSalaryDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
	//	System.out.println("recruitId11::;"+recruitId);
	//	System.out.println("candidasteId::"+candidateId);
	//	System.out.println("strJD::;"+strJD);
		try {
			con = db.makeConnection(con);
			String level_id = null;
			pst = con.prepareStatement("select level_id,is_disable_sal_calculate from candidate_application_details join recruitment_details using (recruitment_id) where candidate_id= ? and recruitment_id = ?" );
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.setInt(2,uF.parseToInt(recruitId));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			if (rs.next()) {
				level_id = rs.getString("level_id");
				setDisableSalaryStructure(uF.parseToBoolean(rs.getString("is_disable_sal_calculate")));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con, uF.parseToInt(level_id));
			salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads(level_id);
			
//			System.out.println("level_id ===> " + level_id);
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id = ? and is_annual_variable=true and (is_delete is null or is_delete=false) order by weight");
			pst.setInt(1, uF.parseToInt(level_id));
			rs = pst.executeQuery();
			List<String> alAnnualSalaryHead = new ArrayList<String>();
			while(rs.next()) {
				if(!alAnnualSalaryHead.contains(rs.getString("salary_head_id"))) {
					alAnnualSalaryHead.add(rs.getString("salary_head_id"));
				}
			}
			rs.close();
			pst.close();

			List<String> alSalaryDuplicationTracer = new ArrayList<String>();
			List<List<String>> al = new ArrayList<List<String>>();			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id = ? and (is_delete is null or is_delete=false) order by weight");
			pst.setInt(1, uF.parseToInt(level_id));
//			System.out.println("pst Salary deatils ===> " + pst);
			rs = pst.executeQuery();
//			double dblTotGrossSal = 0;
//			double dblTotDeductSal = 0;
			while (rs.next()) {
//				if(rs.getString("salary_head_amount_type") != null && !rs.getString("salary_head_amount_type").equals("P")) {
//					if(rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equals("E")) {
//						dblTotGrossSal += uF.parseToDouble(rs.getString("salary_head_amount"));
//					} else if(rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equals("D")) {
//						dblTotDeductSal += uF.parseToDouble(rs.getString("salary_head_amount"));
//					}
//				}
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("salary_head_id"));	//0
				alInner.add(rs.getString("salary_head_name"));	//1
				alInner.add(rs.getString("earning_deduction"));	//2
				alInner.add(rs.getString("salary_head_amount_type"));	//3
				String rsHeadId = rs.getInt("sub_salary_head_id") + "";
				alInner.add(rsHeadId);	//4
				
				alInner.add("0");	//5
				
				alInner.add(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));	//6
				
				StringBuilder sbMulcalType = new StringBuilder();
				if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))){
					String strMulCal = rs.getString("multiple_calculation");
					CF.appendMultiplePercentageCalType(uF,sbMulcalType,strMulCal,hmSalaryMap,alAnnualSalaryHead); 
				}
				alInner.add(rs.getString("multiple_calculation")); //7
				alInner.add(sbMulcalType.toString()); //8
				
				int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
				if(index>=0){
					al.remove(index);
					al.add(index, alInner);
				}else{
					alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
					al.add(alInner);
				}

			}
			rs.close();
			pst.close();
			
//			System.out.println("al ===> "+al);
//			System.out.println("dblTotGrossSal ===>> j " + dblTotGrossSal);
//			System.out.println("dblTotDeductSal ===>> j " + dblTotDeductSal);
//			request.setAttribute("dblTotGrossSal", uF.formatIntoTwoDecimalWithOutComma(dblTotGrossSal));
//			request.setAttribute("dblTotDeductSal", uF.formatIntoTwoDecimalWithOutComma(dblTotDeductSal));
			request.setAttribute("reportList", al);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	}
	

	public Map<String, String> getCandiOfferedCTC(Connection con) {

		
		PreparedStatement pst=null;
		ResultSet rs = null;
		UtilityFunctions uF= new UtilityFunctions();
		String strD1 = null;
		String strD2 = null;
		String strPC = null;
		Map<String, String> hmCandiOffered = new HashMap<String, String>();
		try {
			
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];
			
			pst = con.prepareStatement("select level_id from candidate_application_details join recruitment_details using (recruitment_id) where candidate_id= ? and recruitment_id = ?");
//			pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			int nLevelId = 0;
			while(rs.next()){
				nLevelId = rs.getInt("level_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select state_id,rd.org_id,ismetro from state sd, work_location_info wd, recruitment_details rd " +
					"where rd.wlocation=wd.wlocation_id and wd.wlocation_state_id=sd.state_id and rd.recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String strStateId = null;
			int nOrgId = 0;
			boolean isMetro = false;
			while(rs.next()){
				strStateId = rs.getString("state_id");
				nOrgId = rs.getInt("org_id");
				isMetro = uF.parseToBoolean(rs.getString("ismetro"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from candidate_personal_details where emp_per_id=?");
//			pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String strGender = null;
			double dblCandAge = 0.0d;
			while(rs.next()){
				strGender = rs.getString("emp_gender");
				String strDays = uF.dateDifference(rs.getString("emp_date_of_birth"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE);
				dblCandAge = uF.parseToDouble(strDays) / 365;
			}
			rs.close();
			pst.close();
			
			String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String[] strPayCycleDates = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, ""+nOrgId);
			 if(strPayCycleDates != null){
			 strD1 = strPayCycleDates[0];
			 strD2 = strPayCycleDates[1];
			 strPC = strPayCycleDates[2];
			 }
			
			int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
			
			pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map hmHRAExemption = new HashMap();
			while(rs.next()){
				hmHRAExemption.put("CONDITION_1", rs.getString("condition1"));
				hmHRAExemption.put("CONDITION_2", rs.getString("condition2"));
				hmHRAExemption.put("CONDITION_3", rs.getString("condition3"));
				hmHRAExemption.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
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
				
//				dblInvestmentExemption = 100000;
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and section_id=3 order by section_code");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			double dblInvestmentExemption = 0.0d;
			if (rs.next()) {
				dblInvestmentExemption = uF.parseToDouble(rs.getString("section_exemption_limit"));
			}
			rs.close();
			pst.close();
			
			ApprovePayroll objAP = new ApprovePayroll();
			objAP.CF = CF;
			objAP.session = session;
			objAP.request = request; 
			
//			Map<String, String> hmEmpExemptionsMap = objAP.getEmpInvestmentExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd, dblInvestmentExemption);
//			Map<String, String> hmEmpHomeLoanMap = objAP.getEmpHomeLoanExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd);
			Map<String,String> hmFixedExemptions = objAP.getFixedExemption(con, uF, strFinancialYearStart, strFinancialYearEnd);
//			Map<String, String> hmEmpRentPaidMap = objAP.getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
			
			Map<String, String> hmSalaryDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from salary_details where salary_head_id not in ("+GROSS+","+CTC+") and level_id=? and (is_delete is null or is_delete=false) order by earning_deduction desc, salary_head_id, weight");
			pst.setInt(1, nLevelId);
			rs = pst.executeQuery();  
			while(rs.next()){
				hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select * from annual_variable_details where level_id=? and org_id=? " +
//					"and financial_year_start=? and financial_year_end=? and salary_head_id in (select salary_head_id from salary_details where is_annual_variable=true and (is_delete is null or is_delete = false))");
//			pst.setInt(1, nLevelId);
//			pst.setInt(2, nOrgId);
//			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
//			Map<String, String> hmAnnualVariableAmt = new HashMap<String, String>();
//			while(rs.next()){
//				hmAnnualVariableAmt.put(rs.getString("salary_head_id"), rs.getString("variable_amount"));
//			}
//			rs.close();
//			pst.close();
//			request.setAttribute("hmAnnualVariableAmt", hmAnnualVariableAmt);
			
			pst = con.prepareStatement("SELECT * FROM (select csd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id " +
					"FROM candidate_salary_details WHERE emp_id=? AND effective_date = (SELECT MAX(effective_date) FROM candidate_salary_details " +
					"WHERE emp_id=?) group by salary_head_id) a, candidate_salary_details csd WHERE a.emp_salary_id=csd.emp_salary_id " +
					"and a.salary_head_id=csd.salary_head_id and emp_id=? AND effective_date= (SELECT MAX(effective_date) " +
					"FROM candidate_salary_details WHERE emp_id=?)) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
					"WHERE level_id=? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) " +
					"order by sd.earning_deduction desc, weight");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.setInt(2, uF.parseToInt(getCandidateId()));
			pst.setInt(3, uF.parseToInt(getCandidateId()));
			pst.setInt(4, uF.parseToInt(getCandidateId()));
			pst.setInt(5, nLevelId);
//			System.out.println("pst =====> " + pst);
			rs = pst.executeQuery();
			List<List<String>> salaryHeadDetailsList = new ArrayList<List<String>>();
			Map<String, String> hmTotal = new HashMap<String, String>();
			double dblGrossTDS = 0.0d;
			boolean isEPF = false;
			boolean isESIC = false;
			boolean isLWF = false;
			double grossAmount = 0.0d;
			double grossYearAmount = 0.0d;
			double deductAmount = 0.0d;
			double deductYearAmount = 0.0d;
			List<List<String>> salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
			while(rs.next()) {
				
				if(uF.parseToInt(rs.getString("salary_head_id")) == CTC){
					continue;
				}
				
				if(!uF.parseToBoolean(rs.getString("isdisplay"))){
					continue;
				}

				if(rs.getString("earning_deduction").equals("E")) {
					if(!uF.parseToBoolean(rs.getString("is_variable")) && uF.parseToBoolean(rs.getString("is_annual_variable"))){
						
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("salary_head_name"));
						innerList.add(rs.getString("earning_deduction"));
						
						if(uF.parseToBoolean(rs.getString("isdisplay"))){
							double dblAmount = 0.0d;
//							double dblYearAmount = uF.parseToDouble(hmAnnualVariableAmt.get(rs.getString("salary_head_id")));
							double dblYearAmount = 0.0d;
							
							innerList.add(""+dblAmount);
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							
							grossAmount += dblAmount;
							grossYearAmount += dblYearAmount;
							
							if(uF.parseToInt(rs.getString("salary_head_id")) != REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							} else if(uF.parseToInt(rs.getString("salary_head_id")) != TRAVEL_REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							} else if(uF.parseToInt(rs.getString("salary_head_id")) != MOBILE_REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							} else if(uF.parseToInt(rs.getString("salary_head_id")) != OTHER_REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							}
							
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
						} else {
							innerList.add("0.0");
							innerList.add("0.0");
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
						}
						salaryAnnualVariableDetailsList.add(innerList);
					
					} else {	
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("salary_head_name"));
						innerList.add(rs.getString("earning_deduction"));
						
						if(uF.parseToBoolean(rs.getString("isdisplay"))){
							double dblAmount = 0.0d;
							double dblYearAmount = 0.0d;
							if(uF.parseToBoolean(rs.getString("is_variable")) && uF.parseToBoolean(rs.getString("is_annual_variable"))){
								dblAmount = 0.0d;
//								dblYearAmount = uF.parseToDouble(hmAnnualVariableAmt.get(rs.getString("salary_head_id")));
								dblYearAmount = 0.0d;
							} else {
								dblAmount = rs.getDouble("amount");
//								if(hmPerkAlignAmount.containsKey(rs.getString("salary_head_id"))){
//									dblAmount = uF.parseToDouble(hmPerkAlignAmount.get(rs.getString("salary_head_id")));
//								}
								dblYearAmount = dblAmount * 12;
							}
							
							innerList.add(""+dblAmount);
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							
							grossAmount += dblAmount;
							grossYearAmount += dblYearAmount;
							
							if(uF.parseToInt(rs.getString("salary_head_id")) != REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							} else if(uF.parseToInt(rs.getString("salary_head_id")) != TRAVEL_REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							} else if(uF.parseToInt(rs.getString("salary_head_id")) != MOBILE_REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							} else if(uF.parseToInt(rs.getString("salary_head_id")) != OTHER_REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							}
							
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
						} else {
							innerList.add("0.0");
							innerList.add("0.0");
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
						}
						salaryHeadDetailsList.add(innerList);
					}
				} else if(rs.getString("earning_deduction").equals("D")) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("salary_head_name"));
					innerList.add(rs.getString("earning_deduction"));
					if(uF.parseToBoolean(rs.getString("isdisplay"))){
//							int nPayMonth = uF.parseToInt(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, "MM"));
						switch(rs.getInt("salary_head_id")){
													
							case PROFESSIONAL_TAX :
								  
								double dblAmount = calculateProfessionalTax(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, nPayMonth, strStateId,strGender);
								dblAmount = Math.round(dblAmount);
//								double dblYearAmount =  dblAmount * 12;
								double dblYearAmount =  getAnnualProfessionalTax(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, strStateId, strGender);
								
								deductAmount += dblAmount;
//								deductYearAmount += dblYearAmount > 0.0d ? dblYearAmount + 100 : 0.0d;
								deductYearAmount += dblYearAmount;
								
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount));
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
								innerList.add(rs.getString("salary_head_id"));
								innerList.add(rs.getString("is_variable"));
								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
								
								break;
							
							case EMPLOYEE_EPF :
								isEPF = true;	
								double dblAmount1 = objAP.calculateCandiEEPF(con, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, getCandidateId(),nLevelId,nOrgId);
								dblAmount1 = Math.round(dblAmount1);
								double dblYearAmount1 = dblAmount1 * 12;
								
								deductAmount += dblAmount1;
								deductYearAmount += dblYearAmount1;
								
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount1));
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount1));
								innerList.add(rs.getString("salary_head_id"));
								innerList.add(rs.getString("is_variable"));
								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount1));
								
								break;
							
							case EMPLOYEE_ESI :
								isESIC = true;
								double dblAmount4 = objAP.calculateCandiEEESI(con, uF, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, getCandidateId(),nLevelId,nOrgId);
								dblAmount4 = Math.ceil(dblAmount4);
								double dblYearAmount4 = dblAmount4 * 12;
								dblYearAmount4 = Math.ceil(dblYearAmount4);
								
								deductAmount += dblAmount4;
								deductYearAmount += dblYearAmount4;
//								System.out.println("dblAmount4====>"+dblAmount4);
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount4));
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount4));
								innerList.add(rs.getString("salary_head_id"));
								innerList.add(rs.getString("is_variable"));
								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount4));
								
								break;
							
							case EMPLOYEE_LWF :
								isLWF = true;
								double dblAmount6 = objAP.calculateEELWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, getCandidateId(), nPayMonth, ""+nOrgId);
								dblAmount6 = Math.round(dblAmount6);
								double dblYearAmount6 = dblAmount6 * 12;
								
								deductAmount += dblAmount6;
								deductYearAmount += dblYearAmount6;
								
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount6));
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount6));
								innerList.add(rs.getString("salary_head_id"));
								innerList.add(rs.getString("is_variable"));
								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount6));
								
								break;
							
							case TDS :
								
								double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
								double dblDA = uF.parseToDouble(hmTotal.get(DA+""));
								double dblHRA = uF.parseToDouble(hmTotal.get(HRA+""));
								
								String[] hraSalaryHeads = null;
								if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null){
									hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
								}
								
								double dblHraSalHeadsAmount = 0;
								for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++){
									dblHraSalHeadsAmount += uF.parseToDouble((String)hmTotal.get(hraSalaryHeads[i]));
								}
								
								
								double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_EDU_TAX"));
								double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_STD_TAX"));
								double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_FLAT_TDS"));
								 
								double dblAmount7 = objAP.calculateCandidateTDS(con, uF,strD2,strD1, dblGrossTDS, dblCess1, dblCess2, dblFlatTDS, dblInvestmentExemption, dblHRA, dblHraSalHeadsAmount,
										nPayMonth,strD1, strFinancialYearStart, strFinancialYearEnd, uF.parseToInt(getCandidateId()), strGender,  dblCandAge, strStateId,
										hmFixedExemptions, isMetro, hmTotal, hmSalaryDetails, nLevelId, CF,hmOtherTaxDetails,nOrgId);
								dblAmount7 = Math.round(dblAmount7);
								double dblYearAmount7 = dblAmount7 * 12;
								
								deductAmount += dblAmount7;
								deductYearAmount += dblYearAmount7;
								
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount7));
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount7));
								innerList.add(rs.getString("salary_head_id"));
								innerList.add(rs.getString("is_variable"));
								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount7));
								
								break;
							
							default:
								
								double dblAmount9 = uF.parseToDouble(rs.getString("amount"));
								double dblYearAmount9 = dblAmount9 * 12;
								
								deductAmount += dblAmount9;
								deductYearAmount += dblYearAmount9;
								
								innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblAmount9));
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount9));
								innerList.add(rs.getString("salary_head_id"));
								innerList.add(rs.getString("is_variable"));
								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount9));
								
								break;
						}
					}  else {
						innerList.add("0.0");
						innerList.add("0.0");
						innerList.add(rs.getString("salary_head_id"));
						innerList.add(rs.getString("is_variable"));
						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
					}
					
					salaryHeadDetailsList.add(innerList);
				}
				
			}
			rs.close();
			pst.close();
			
			/**
			 * Employer Contribution
			 * */ 
			Map<String,String> hmContribution = new HashMap<String, String>();
			if(isEPF){
				double dblAmount = objAP.calculateCandiERPF(con,CF, null, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, getCandidateId(), nLevelId, nOrgId);
				dblAmount = Math.round(dblAmount);
				double dblYearAmount = dblAmount * 12;
				hmContribution.put("EPF_MONTHLY", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
				hmContribution.put("EPF_ANNUALY", uF.formatIntoTwoDecimalWithOutComma(dblYearAmount));
			}
			if(isESIC){
				double dblAmount = objAP.calculateCandiERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId,getCandidateId(), nOrgId, nLevelId);
				dblAmount = Math.ceil(dblAmount);
				double dblYearAmount = dblAmount * 12;
				dblYearAmount = Math.ceil(dblYearAmount);
				
				hmContribution.put("ESI_MONTHLY", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
				hmContribution.put("ESI_ANNUALY", uF.formatIntoTwoDecimalWithOutComma(dblYearAmount));
			}
			if(isLWF){
				double dblAmount = objAP.calculateERLWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, nPayMonth, ""+nOrgId);
				dblAmount = Math.round(dblAmount);
				double dblYearAmount = dblAmount * 12;
				hmContribution.put("LWF_MONTHLY", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
				hmContribution.put("LWF_ANNUALY", uF.formatIntoTwoDecimalWithOutComma(dblYearAmount));				
			}
			
			/**
			 * Employer Contribution End
			 * */ 
			
			
			/**
			 * Salary Structure Table
			 * */
			if(salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && salaryHeadDetailsList.size() > 0){
				grossAmount = 0.0d;
				grossYearAmount = 0.0d;
				double netTakeHome = 0.0d;
				for(int i=0; i<salaryHeadDetailsList.size(); i++) {
					List<String> innerList = salaryHeadDetailsList.get(i);
						if(innerList.get(1).equals("E")) {
							double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
							double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
							grossAmount += dblEarnMonth;
							grossYearAmount += dblEarnAnnual;
							
							netTakeHome += dblEarnMonth;
					} 
				}
		
//				deductAmount = 0.0d;
//				deductYearAmount = 0.0d;
//			
//				for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
//					List<String> innerList = salaryHeadDetailsList.get(i);
//					if(innerList.get(1).equals("D")) {
//						double dblDeductMonth = Math.round(uF.parseToDouble(innerList.get(2)));
//						double dblDeductAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
//						deductAmount += dblDeductMonth;
//						deductYearAmount += dblDeductAnnual;
//						
//						netTakeHome -= dblDeductMonth;
//					}
//				}
//				
//				sbCandiSalTable.append("<tr>");
//				sbCandiSalTable.append("<td align=\"right\"><strong>Deduction</strong></td>");
//				sbCandiSalTable.append("<td align=\"right\"><strong>"+uF.formatIntoTwoDecimal(deductAmount)+"</strong></td>");
//				sbCandiSalTable.append("<td align=\"right\"><strong>"+uF.formatIntoTwoDecimal(deductYearAmount)+"</strong></td>");
//				sbCandiSalTable.append("</tr>");
//				sbCandiSalTable.append("</table>");
//				sbCandiSalTable.append("</td>");
//				sbCandiSalTable.append("</tr>");
			
				double dblMonthContri = 0.0d;
				double dblAnnualContri = 0.0d;
				if(isEPF || isESIC || isLWF){
					if(isEPF){
						double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
						double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
						dblMonthContri += dblEPFMonth;
						dblAnnualContri += dblEPFAnnual;
					} 
					if(isESIC){
						
						double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
						double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
						dblMonthContri += dblESIMonth;
						dblAnnualContri += dblESIAnnual;
					}
					if(isLWF){
						double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
						double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
						dblMonthContri += dblLWFMonth;
						dblAnnualContri += dblLWFAnnual;
					}
				}
				
				double dblCTCMonthly = grossAmount + dblMonthContri;
				double dblCTCAnnualy = grossYearAmount + dblAnnualContri;
				
				int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
				if(nAnnualVariSize > 0){
					double grossAnnualAmount = 0.0d;
					double grossAnnualYearAmount = 0.0d;
					for(int i = 0; i < nAnnualVariSize; i++){
						List<String> innerList = salaryAnnualVariableDetailsList.get(i);
						double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
						double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
						grossAnnualAmount += dblEarnMonth;
						grossAnnualYearAmount += dblEarnAnnual;
					} 
					dblCTCMonthly += grossAnnualAmount;
					dblCTCAnnualy += grossAnnualYearAmount;
				}
				
				hmCandiOffered.put("CANDI_CTC", uF.formatIntoTwoDecimal(dblCTCMonthly));
				hmCandiOffered.put("CANDI_ANNUAL_CTC", uF.formatIntoTwoDecimal(dblCTCAnnualy));
			}
			/**
			 * Salary Structure End
			 * */
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return hmCandiOffered;


	}
	
	private double calculateProfessionalTax(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd,
			int nPayMonth, String strStateId,String strGender) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblAmount= 0;
		
		
		try {
			pst = con.prepareStatement("select * from deduction_details_india where income_from<=? and income_to>=? and state_id=? " +
					"and financial_year_from=? and financial_year_to=? and gender =? limit 1");
			pst.setDouble(1, dblGross);
			pst.setDouble(2, dblGross);
			pst.setInt(3, uF.parseToInt(strStateId));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(6, strGender);
			rs = pst.executeQuery();  
			while(rs.next()){
				dblAmount = rs.getDouble("deduction_paycycle");
			}
			rs.close();
			pst.close();
			
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
		return dblAmount;
	}
	private void insertEmpSalaryDetails(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			//code for getting userid
			con = db.makeConnection(con);
			pst = con.prepareStatement("select level_id from candidate_application_details join recruitment_details using (recruitment_id) where candidate_id= ? and recruitment_id = ?");
//			pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.setInt(2, uF.parseToInt(recruitId));
			rs = pst.executeQuery();
		//	System.out.println("new Date ===> " + new Date());
			boolean isCurrentDateExist = false;
			int level_id = 0;
			while(rs.next()){
				isCurrentDateExist = true;
				level_id = rs.getInt("level_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id = ? and (is_delete is null or is_delete=false) order by weight");
			pst.setInt(1, level_id);
		//	System.out.println("pst ===> " + pst);
			rs = pst.executeQuery();
			Map<String, String> hmEarningDeductionMap = new HashMap<String, String>();
			while(rs.next()){
				hmEarningDeductionMap.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
			}
			rs.close();
			pst.close();
			
//			if(isCurrentDateExist){
				pst = con.prepareStatement("delete from candidate_salary_details where emp_id = ? and recruitment_id = ? "); //entry_date = ? and 
//				pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
				pst.setInt(1, uF.parseToInt(getCandidateId()));
				pst.setInt(2, uF.parseToInt(recruitId));
		//		System.out.println("pst  delete ===> " + pst);
				
				pst.execute();
				pst.close();
//			}
			
			String total_earning_value = request.getParameter("hide_total_earning_value");
			String total_earning_value1 = request.getParameter("total_earning_value");
	//		System.out.println("salary_head_id==>"+Arrays.asList(salary_head_id));
			for(int i=0; i<salary_head_id.length; i++) {
				String isDisplay = (String) request.getParameter("isDisplay_"+getSalary_head_id()[i]);
				pst = con.prepareStatement("INSERT INTO candidate_salary_details (emp_id , salary_head_id, amount, entry_date, user_id, pay_type," +
						"service_id, effective_date, earning_deduction,recruitment_id,isdisplay) VALUES (?,?,?,?, ?,?,?,?, ?,?,?)");
				
				pst.setInt(1, uF.parseToInt(getCandidateId()));
				pst.setInt(2, uF.parseToInt(getSalary_head_id()[i]));
				pst.setDouble(3, uF.parseToDouble(getSalary_head_value()[i]));
				pst.setDate	(4, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
				pst.setInt(5, 1);
				pst.setString(6, "M");
				pst.setInt(7, uF.parseToInt("0"));
				pst.setDate	(8, uF.getDateFormat(getJoiningdate(), DATE_FORMAT));
				pst.setString(9, hmEarningDeductionMap.get(getSalary_head_id()[i]));
				pst.setInt(10, uF.parseToInt(getRecruitId()));
				pst.setBoolean(11, uF.parseToBoolean(isDisplay));
		//		System.out.println("pst salary heads ==>"+i+"==>"+pst);
				pst.execute();
				pst.close();
			}
			
			pst = con.prepareStatement("update candidate_application_details set is_disable_sal_calculate=? where candidate_id = ? and recruitment_id=? ");
			pst.setBoolean(1, getDisableSalaryStructure());
			pst.setInt(2, uF.parseToInt(getCandidateId()));
			pst.setInt(3, uF.parseToInt(getRecruitId()));
		//	System.out.println("update pst ===> " + pst);
			pst.execute();
			pst.close();
			
//			String Amount =null;
//			pst=con.prepareStatement("select sum(amount) as amt from candidate_salary_details where emp_id=? and recruitment_id =? and entry_date = ? and earning_deduction='E' ");
//			pst.setInt(1, uF.parseToInt(getCandID()));
//			pst.setInt(2, uF.parseToInt(getRecruitId()));
//			pst.setDate(3, uF.getDateFormat(getJoiningdate(), DATE_FORMAT));
//			System.out.println("pst salary heads ==>"+i+"==>"+pst);
//			rs=pst.executeQuery();
////			System.out.println("new Date ===> " + new Date());
//			if(rs.next()){
//				Amount=rs.getString("amt");
//			}
//			rs.close();
//			pst.close();
			
//			System.out.println("total_earning_value ===> " + total_earning_value);
			
			Map<String, String> hmCandiOffered = getCandiOfferedCTC(con);
			if(hmCandiOffered == null) hmCandiOffered = new HashMap<String, String>();
			pst = con.prepareStatement("update candidate_application_details set ctc_offered=?, annual_ctc_offered=? where candidate_id = ? and recruitment_id = ? ");
			pst.setDouble(1,uF.parseToDouble(hmCandiOffered.get("CANDI_CTC")));
			pst.setDouble(2,uF.parseToDouble(hmCandiOffered.get("CANDI_ANNUAL_CTC")));
			pst.setInt(3, uF.parseToInt(getCandidateId()));
			pst.setInt(4, uF.parseToInt(getRecruitId()));
		//	System.out.println("pst ===> " + pst);
			pst.execute();
			pst.close();
			
			int ServiceNo = uF.parseToInt((String)session.getAttribute("ServicesLinkNo"));
//			session.setAttribute("ServicesLinkNo", (ServiceNo-1)+"");    // Uncomment this code if you wish to use salary cost center wise.
			session.setAttribute("ServicesLinkNo", 1+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	
	}
	
	private double getAnnualProfessionalTax(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd,String strStateId, String strEmpGender) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblDeductionAnnual= 0;
		
		
		try {
			pst = con.prepareStatement("select * from deduction_details_india where income_from<=? and income_to>=? and state_id=? " +
					"and financial_year_from=? and financial_year_to=? and gender =? limit 1");
			pst.setDouble(1, dblGross);
			pst.setDouble(2, dblGross);
			pst.setInt(3, uF.parseToInt(strStateId));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(6, strEmpGender);	
			rs = pst.executeQuery();  
			while(rs.next()){
				dblDeductionAnnual = rs.getDouble("deduction_amount");
			}
			rs.close();
			pst.close();
			
			
			
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
		return dblDeductionAnnual;
	}
	private String insertHrInterview(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
//			pst = con.prepareStatement("update candidate_personal_details set candidate_final_status=?,candidate_hr_comments=?, candidate_joining_date=?,ctc_offered=? where emp_per_id=? ");
			pst = con.prepareStatement("update candidate_application_details set candidate_final_status=?,candidate_hr_comments=?, candidate_joining_date=?,candidate_final_status_date=?  where candidate_id=? and recruitment_id = ?");
			
			pst.setInt(1, 1);
			pst.setString(2, getStrinterviewcommentHR());
			pst.setDate(3, uF.getDateFormat(getJoiningdate(), DATE_FORMAT));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.setDouble(4, uF.parseToDouble(getCtcOffer()));
			pst.setInt(5, uF.parseToInt(getCandidateId()));
			
			pst.setInt(6,uF.parseToInt(getRecruitId()));
		//	System.out.println("pst update  ===> " + pst);
			pst.executeUpdate();
			pst.close();
			
			pst=con.prepareStatement("delete from candidate_activity_details where recruitment_id=? and candi_id=? and user_id=? and " +
					"activity_id = ?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getCandidateId()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, CANDI_ACTIVITY_FINALIZE_AND_OFFER_ID);
	//	System.out.println("pst delete  ===> " + pst);
			pst.executeUpdate();
			pst.close();
			
			pst=con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getCandidateId()));
//			pst.setInt(3,uF.parseToInt(getRoundID()));
			pst.setString(3, "Finalisation & Offer");
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setDate	(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setInt(6, CANDI_ACTIVITY_FINALIZE_AND_OFFER_ID);
//			 System.out.println("pst insert  ===> " + pst);

			pst.execute();
			pst.close();
			
			if (hrchoice != null && hrchoice.equals("1")) {
				sendMail();
			}
			
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "returnhr";

	

	}
	
	public StringBuilder getCandiSalaryDetails(Connection con) {

		
		PreparedStatement pst=null;
		ResultSet rs = null;
		UtilityFunctions uF= new UtilityFunctions();
		String strD1 = null;
		String strD2 = null;
		String strPC = null;
		StringBuilder sbCandiSalTable = new StringBuilder();
		try {
			
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];
			
			pst = con.prepareStatement("select level_id from candidate_application_details join recruitment_details using (recruitment_id) where candidate_id= ? and recruitment_id = ?");
//			pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			int nLevelId = 0;
			while(rs.next()){
				nLevelId = rs.getInt("level_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select state_id,rd.org_id,ismetro from state sd, work_location_info wd, recruitment_details rd " +
					"where rd.wlocation=wd.wlocation_id and wd.wlocation_state_id=sd.state_id and rd.recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String strStateId = null;
			int nOrgId = 0;
			boolean isMetro = false;
			while(rs.next()){
				strStateId = rs.getString("state_id");
				nOrgId = rs.getInt("org_id");
				isMetro = uF.parseToBoolean(rs.getString("ismetro"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from candidate_personal_details where emp_per_id=?");
//			pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String strGender = null;
			double dblCandAge = 0.0d;
			while(rs.next()){
				strGender = rs.getString("emp_gender");
				String strDays = uF.dateDifference(rs.getString("emp_date_of_birth"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE);
				dblCandAge = uF.parseToDouble(strDays) / 365;
			}
			rs.close();
			pst.close();
			
			String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String[] strPayCycleDates = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, ""+nOrgId);
//			System.out.println("strPayCycleDates:"+strPayCycleDates);
			if(strPayCycleDates.length > 0)
			{
			 strD1 = strPayCycleDates[0];
			 strD2 = strPayCycleDates[1];
			 strPC = strPayCycleDates[2];
			}
			int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
			
			pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map hmHRAExemption = new HashMap();
			while(rs.next()){
				hmHRAExemption.put("CONDITION_1", rs.getString("condition1"));
				hmHRAExemption.put("CONDITION_2", rs.getString("condition2"));
				hmHRAExemption.put("CONDITION_3", rs.getString("condition3"));
				hmHRAExemption.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
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
				
//				dblInvestmentExemption = 100000;
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and section_id=3 order by section_code");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			double dblInvestmentExemption = 0.0d;
			if (rs.next()) {
				dblInvestmentExemption = uF.parseToDouble(rs.getString("section_exemption_limit"));
			}
			rs.close();
			pst.close();
			
			ApprovePayroll objAP = new ApprovePayroll();
			objAP.CF = CF;
			objAP.session = session;
			objAP.request = request; 
			
//			Map<String, String> hmEmpExemptionsMap = objAP.getEmpInvestmentExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd, dblInvestmentExemption);
//			Map<String, String> hmEmpHomeLoanMap = objAP.getEmpHomeLoanExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd);
			Map<String,String> hmFixedExemptions = objAP.getFixedExemption(con, uF, strFinancialYearStart, strFinancialYearEnd);
//			Map<String, String> hmEmpRentPaidMap = objAP.getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
			
			Map<String, String> hmSalaryDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from salary_details where salary_head_id not in ("+GROSS+","+CTC+") and level_id=? and (is_delete is null or is_delete=false) order by earning_deduction desc, salary_head_id, weight");
			pst.setInt(1, nLevelId);
			rs = pst.executeQuery();  
			while(rs.next()){
				hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select * from annual_variable_details where level_id=? and org_id=? " +
//					"and financial_year_start=? and financial_year_end=? and salary_head_id in (select salary_head_id from salary_details where is_annual_variable=true and (is_delete is null or is_delete = false))");
//			pst.setInt(1, nLevelId);
//			pst.setInt(2, nOrgId);
//			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
//			Map<String, String> hmAnnualVariableAmt = new HashMap<String, String>();
//			while(rs.next()){
//				hmAnnualVariableAmt.put(rs.getString("salary_head_id"), rs.getString("variable_amount"));
//			}
//			rs.close();
//			pst.close();
//			request.setAttribute("hmAnnualVariableAmt", hmAnnualVariableAmt);
			
			pst = con.prepareStatement("SELECT * FROM (select csd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id " +
					"FROM candidate_salary_details WHERE emp_id=? AND effective_date = (SELECT MAX(effective_date) FROM candidate_salary_details " +
					"WHERE emp_id=?) group by salary_head_id) a, candidate_salary_details csd WHERE a.emp_salary_id=csd.emp_salary_id " +
					"and a.salary_head_id=csd.salary_head_id and emp_id=? AND effective_date= (SELECT MAX(effective_date) " +
					"FROM candidate_salary_details WHERE emp_id=?)) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
					"WHERE level_id=? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) " +
					"order by sd.earning_deduction desc, weight");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.setInt(2, uF.parseToInt(getCandidateId()));
			pst.setInt(3, uF.parseToInt(getCandidateId()));
			pst.setInt(4, uF.parseToInt(getCandidateId()));
			pst.setInt(5, nLevelId);
//			System.out.println("pst =====> " + pst); 
			rs = pst.executeQuery();
			List<List<String>> salaryHeadDetailsList = new ArrayList<List<String>>();
			Map<String, String> hmTotal = new HashMap<String, String>();
			double dblGrossTDS = 0.0d;
			boolean isEPF = false;
			boolean isESIC = false;
			boolean isLWF = false;
			double grossAmount = 0.0d;
			double grossYearAmount = 0.0d;
			double deductAmount = 0.0d;
			double deductYearAmount = 0.0d;
			List<List<String>> salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
			while(rs.next()) {
				
				if(uF.parseToInt(rs.getString("salary_head_id")) == CTC){
					continue;
				}
				
				if(!uF.parseToBoolean(rs.getString("isdisplay"))){
					continue;
				}

				if(rs.getString("earning_deduction").equals("E")) {
					if(!uF.parseToBoolean(rs.getString("is_variable")) && uF.parseToBoolean(rs.getString("is_annual_variable"))){
						
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("salary_head_name"));
						innerList.add(rs.getString("earning_deduction"));
						
						if(uF.parseToBoolean(rs.getString("isdisplay"))){
							double dblAmount = 0.0d;
//							double dblYearAmount = uF.parseToDouble(hmAnnualVariableAmt.get(rs.getString("salary_head_id")));
							double dblYearAmount = 0.0d;
							
							innerList.add(""+dblAmount);
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							
							grossAmount += dblAmount;
							grossYearAmount += dblYearAmount;
							
							if(uF.parseToInt(rs.getString("salary_head_id")) != REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							} else if(uF.parseToInt(rs.getString("salary_head_id")) != TRAVEL_REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							} else if(uF.parseToInt(rs.getString("salary_head_id")) != MOBILE_REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							} else if(uF.parseToInt(rs.getString("salary_head_id")) != OTHER_REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							}
							
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
						} else {
							innerList.add("0.0");
							innerList.add("0.0");
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
						}
						salaryAnnualVariableDetailsList.add(innerList);
					
					} else {	
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("salary_head_name"));
						innerList.add(rs.getString("earning_deduction"));
						
						if(uF.parseToBoolean(rs.getString("isdisplay"))){
							double dblAmount = 0.0d;
							double dblYearAmount = 0.0d;
							if(uF.parseToBoolean(rs.getString("is_variable")) && uF.parseToBoolean(rs.getString("is_annual_variable"))){
								dblAmount = 0.0d;
//								dblYearAmount = uF.parseToDouble(hmAnnualVariableAmt.get(rs.getString("salary_head_id")));
								dblYearAmount = 0.0d;
							} else {
								dblAmount = rs.getDouble("amount");
//								if(hmPerkAlignAmount.containsKey(rs.getString("salary_head_id"))){
//									dblAmount = uF.parseToDouble(hmPerkAlignAmount.get(rs.getString("salary_head_id")));
//								}
								dblYearAmount = dblAmount * 12;
							}
							
							innerList.add(""+dblAmount);
							innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							
							grossAmount += dblAmount;
							grossYearAmount += dblYearAmount;
							
							if(uF.parseToInt(rs.getString("salary_head_id")) != REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							} else if(uF.parseToInt(rs.getString("salary_head_id")) != TRAVEL_REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							} else if(uF.parseToInt(rs.getString("salary_head_id")) != MOBILE_REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							} else if(uF.parseToInt(rs.getString("salary_head_id")) != OTHER_REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							}
							
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
						} else {
							innerList.add("0.0");
							innerList.add("0.0");
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_variable"));
							hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
						}
						salaryHeadDetailsList.add(innerList);
					}
				} else if(rs.getString("earning_deduction").equals("D")) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("salary_head_name"));
					innerList.add(rs.getString("earning_deduction"));
					if(uF.parseToBoolean(rs.getString("isdisplay"))){
//							int nPayMonth = uF.parseToInt(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, "MM"));
						switch(rs.getInt("salary_head_id")){
													
							case PROFESSIONAL_TAX :
								  
								double dblAmount = calculateProfessionalTax(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, nPayMonth, strStateId,strGender);
								dblAmount = Math.round(dblAmount);
//								double dblYearAmount =  dblAmount * 12;
								double dblYearAmount =  getAnnualProfessionalTax(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, strStateId, strGender);
								
								deductAmount += dblAmount;
//								deductYearAmount += dblYearAmount > 0.0d ? dblYearAmount + 100 : 0.0d;
								deductYearAmount += dblYearAmount;
								
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount));
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
								innerList.add(rs.getString("salary_head_id"));
								innerList.add(rs.getString("is_variable"));
								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
								
								break;
							
							case EMPLOYEE_EPF :
								isEPF = true;	
								double dblAmount1 = objAP.calculateCandiEEPF(con, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, getCandidateId(),nLevelId,nOrgId);
								dblAmount1 = Math.round(dblAmount1);
								double dblYearAmount1 = dblAmount1 * 12;
								
								deductAmount += dblAmount1;
								deductYearAmount += dblYearAmount1;
								
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount1));
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount1));
								innerList.add(rs.getString("salary_head_id"));
								innerList.add(rs.getString("is_variable"));
								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount1));
								
								break;
							
//							case EMPLOYER_EPF :
//								
//								double dblAmount2 = objAP.calculateERPF(con, CF, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, getCandID(), null, null, false, null);
//								dblAmount2 = Math.round(dblAmount2);
//								double dblYearAmount2 = dblAmount2 * 12;
//								
//								deductAmount += dblAmount2;
//								deductYearAmount += dblYearAmount2;
//								
//								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount2));
//								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount2));
//								innerList.add(rs.getString("salary_head_id"));
//								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount2));
//								
//								break;  
							
//							case EMPLOYER_ESI :
//								
//								double dblAmount3 = objAP.calculateERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId,getCandID());
//								dblAmount3 = Math.round(dblAmount3);
//								double dblYearAmount3 = dblAmount3 * 12;
//								
//								deductAmount += dblAmount3;
//								deductYearAmount += dblYearAmount3;
//								
//								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount3));
//								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount3));
//								innerList.add(rs.getString("salary_head_id"));
//								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount3));
//								
//								break;
							
							case EMPLOYEE_ESI :
								isESIC = true;
								double dblAmount4 = objAP.calculateCandiEEESI(con, uF, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, getCandidateId(),nLevelId,nOrgId);
								dblAmount4 = Math.ceil(dblAmount4);
								double dblYearAmount4 = dblAmount4 * 12;
								
								deductAmount += dblAmount4;
								deductYearAmount += dblYearAmount4;
//								System.out.println("dblAmount4====>"+dblAmount4);
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount4));
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount4));
								innerList.add(rs.getString("salary_head_id"));
								innerList.add(rs.getString("is_variable"));
								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount4));
								
								break;
							
//							case EMPLOYER_LWF :
//								
//								double dblAmount5 = objAP.calculateERLWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, nPayMonth);
//								dblAmount5 = Math.round(dblAmount5);
//								double dblYearAmount5 = dblAmount5 * 12;
//								
//								deductAmount += dblAmount5;
//								deductYearAmount += dblYearAmount5;
//								
//								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount5));
//								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount5));
//								innerList.add(rs.getString("salary_head_id"));
//								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount5));
//								
//								break;
							
							case EMPLOYEE_LWF :
								isLWF = true;
								double dblAmount6 = objAP.calculateEELWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, getCandidateId(), nPayMonth, ""+nOrgId);
								dblAmount6 = Math.round(dblAmount6);
								double dblYearAmount6 = dblAmount6 * 12;
								
								deductAmount += dblAmount6;
								deductYearAmount += dblYearAmount6;
								
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount6));
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount6));
								innerList.add(rs.getString("salary_head_id"));
								innerList.add(rs.getString("is_variable"));
								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount6));
								
								break;
							
							case TDS :
								
								double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
								double dblDA = uF.parseToDouble(hmTotal.get(DA+""));
								double dblHRA = uF.parseToDouble(hmTotal.get(HRA+""));
								
								String[] hraSalaryHeads = null;
								if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null){
									hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
								}
								
								double dblHraSalHeadsAmount = 0;
								for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++){
									dblHraSalHeadsAmount += uF.parseToDouble((String)hmTotal.get(hraSalaryHeads[i]));
								}
								
								
								double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_EDU_TAX"));
								double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_STD_TAX"));
								double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_FLAT_TDS"));
								 
								double dblAmount7 = objAP.calculateCandidateTDS(con, uF,strD2,strD1, dblGrossTDS, dblCess1, dblCess2, dblFlatTDS, dblInvestmentExemption, dblHRA, dblHraSalHeadsAmount,
										nPayMonth,strD1, strFinancialYearStart, strFinancialYearEnd, uF.parseToInt(getCandidateId()), strGender,  dblCandAge, strStateId,
										hmFixedExemptions, isMetro, hmTotal, hmSalaryDetails, nLevelId, CF,hmOtherTaxDetails,nOrgId);
								dblAmount7 = Math.round(dblAmount7);
								double dblYearAmount7 = dblAmount7 * 12;
								
								deductAmount += dblAmount7;
								deductYearAmount += dblYearAmount7;
								
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount7));
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount7));
								innerList.add(rs.getString("salary_head_id"));
								innerList.add(rs.getString("is_variable"));
								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount7));
								
								break;
							
							default:
								
								double dblAmount9 = uF.parseToDouble(rs.getString("amount"));
								double dblYearAmount9 = dblAmount9 * 12;
								
								deductAmount += dblAmount9;
								deductYearAmount += dblYearAmount9;
								
								innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblAmount9));
								innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount9));
								innerList.add(rs.getString("salary_head_id"));
								innerList.add(rs.getString("is_variable"));
								hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount9));
								
								break;
						}
					}  else {
						innerList.add("0.0");
						innerList.add("0.0");
						innerList.add(rs.getString("salary_head_id"));
						innerList.add(rs.getString("is_variable"));
						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
					}
					
					salaryHeadDetailsList.add(innerList);
				}
				
			}
			rs.close();
			pst.close();
			
			/**
			 * Employer Contribution
			 * */ 
			Map<String,String> hmContribution = new HashMap<String, String>();
			if(isEPF){
				double dblAmount = objAP.calculateCandiERPF(con,CF, null, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, getCandidateId(), nLevelId, nOrgId);
				dblAmount = Math.round(dblAmount);
				double dblYearAmount = dblAmount * 12;
				hmContribution.put("EPF_MONTHLY", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
				hmContribution.put("EPF_ANNUALY", uF.formatIntoTwoDecimalWithOutComma(dblYearAmount));
			}
			if(isESIC){
				double dblAmount = objAP.calculateCandiERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId,getCandidateId(), nOrgId, nLevelId);
				dblAmount = Math.ceil(dblAmount);
				double dblYearAmount = dblAmount * 12;
				dblYearAmount = Math.ceil(dblYearAmount);
				
				hmContribution.put("ESI_MONTHLY", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
				hmContribution.put("ESI_ANNUALY", uF.formatIntoTwoDecimalWithOutComma(dblYearAmount));
			}
			if(isLWF){
				double dblAmount = objAP.calculateERLWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, nPayMonth, ""+nOrgId);
				dblAmount = Math.round(dblAmount);
				double dblYearAmount = dblAmount * 12;
				hmContribution.put("LWF_MONTHLY", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
				hmContribution.put("LWF_ANNUALY", uF.formatIntoTwoDecimalWithOutComma(dblYearAmount));				
			}
			
			/**
			 * Employer Contribution End
			 * */ 
			
			
			/**
			 * Salary Structure Table
			 * */
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagDisableNetTakeHomeSal = uF.parseToBoolean(hmFeatureStatus.get(F_DISABLE_NET_TAKE_HOME_SALARY));
			if(salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && salaryHeadDetailsList.size() > 0) {

				sbCandiSalTable.append("<table border=\"1\" width=\"50%\">");
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td bgcolor=\"#FFC300\" align=\"center\"><b>Salary Head</b></td>");
				sbCandiSalTable.append("<td bgcolor=\"#FFC300\" width=\"30%\" align=\"right\"><b>Annual</b></td>");
				sbCandiSalTable.append("</tr>");
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td colspan=\"2\" nowrap=\"nowrap\" align=\"center\"><b>EARNING DETAILS</b></td>");
				sbCandiSalTable.append("</tr>");
				
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td colspan=\"2\"><b>Fixed Component- A</b></td>");
				sbCandiSalTable.append("</tr>");
				
				grossAmount = 0.0d;
				grossYearAmount = 0.0d;
				double netTakeHome = 0.0d;
				boolean veriableFlag = false;
				for (int i = 0; i < salaryHeadDetailsList.size(); i++) {
					List<String> innerList = salaryHeadDetailsList.get(i);
					if (innerList.get(1).equals("E") && !uF.parseToBoolean(innerList.get(5))) {
						double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
						double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
						grossAmount += dblEarnMonth;
						grossYearAmount += dblEarnAnnual;

						netTakeHome += dblEarnMonth;

						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td>" + uF.showData(innerList.get(0), "-") + "</td>");
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEarnAnnual) + "</td>");
						sbCandiSalTable.append("</tr>");
					} else if (innerList.get(1).equals("E")) {
						veriableFlag = true;
					}
				}
				
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td><strong>Sub Total</strong></td>");
				sbCandiSalTable.append("<td width=\"30%\" align=\"right\"><strong>" + uF.formatIntoTwoDecimal(grossYearAmount) + "</strong></td>");
				sbCandiSalTable.append("</tr>");
				
				if(veriableFlag) {
					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td colspan=\"2\"><b>Variable Component- B</b></td>");
					sbCandiSalTable.append("</tr>");
				}
				
				for (int i = 0; i < salaryHeadDetailsList.size(); i++) {
					List<String> innerList = salaryHeadDetailsList.get(i);
					if (innerList.get(1).equals("E") && uF.parseToBoolean(innerList.get(5))) {
						double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
						double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
						grossAmount += dblEarnMonth;
						grossYearAmount += dblEarnAnnual;

						netTakeHome += dblEarnMonth;

						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td>" + uF.showData(innerList.get(0), "-") + "</td>");
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEarnAnnual) + "</td>");
						sbCandiSalTable.append("</tr>");
					}
				}

				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td><strong>Gross Salary</strong></td>");
				sbCandiSalTable.append("<td width=\"30%\" align=\"right\"><strong>" + uF.formatIntoTwoDecimal(grossYearAmount) + "</strong></td>");
				sbCandiSalTable.append("</tr>");

				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td colspan=\"2\" nowrap=\"nowrap\" align=\"center\"><b>DEDUCTION DETAILS</b></td>");
				sbCandiSalTable.append("</tr>");

				deductAmount = 0.0d;
				deductYearAmount = 0.0d;

				for (int i = 0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i < salaryHeadDetailsList.size(); i++) {
					List<String> innerList = salaryHeadDetailsList.get(i);
					if (innerList.get(1).equals("D")) {
						double dblDeductMonth = 0.0d;;
						double dblDeductAnnual = 0.0d;
						if (uF.parseToInt(innerList.get(4)) == EMPLOYEE_ESI) {
							dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
							dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
						} else if (uF.parseToInt(innerList.get(4)) == EMPLOYER_ESI) {
							dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
							dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
						} else {
							dblDeductMonth = Math.round(uF.parseToDouble(innerList.get(2)));
							dblDeductAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
						}

						deductAmount += dblDeductMonth;
						deductYearAmount += dblDeductAnnual;

						netTakeHome -= dblDeductMonth;

						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td>" + uF.showData(innerList.get(0), "-") + "</td>");
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblDeductAnnual) + "</td>");
						sbCandiSalTable.append("</tr>");
					}
				}

				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td align=\"center\"><strong>Total Deduction</strong></td>");
				sbCandiSalTable.append("<td width=\"30%\" align=\"right\"><strong>" + uF.formatIntoTwoDecimal(deductYearAmount) + "</strong></td>");
				sbCandiSalTable.append("</tr>");

//				hmContribution = (Map<String, String>) request.getAttribute("hmContribution");
//				if (hmContribution == null)
//					hmContribution = new HashMap<String, String>();
				double dblMonthContri = 0.0d;
				double dblAnnualContri = 0.0d;
//				boolean isEPF = uF.parseToBoolean((String) request.getAttribute("isEPF"));
//				boolean isESIC = uF.parseToBoolean((String) request.getAttribute("isESIC"));
//				boolean isLWF = uF.parseToBoolean((String) request.getAttribute("isLWF"));

				if (isEPF || isESIC || isLWF) {
					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td colspan=\"2\" nowrap=\"nowrap\" align=\"center\"><b>CONTRIBUTION DETAILS</b></td>");
					sbCandiSalTable.append("</tr>");

					if (isEPF) {
						double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
						double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
						dblMonthContri += dblEPFMonth;
						dblAnnualContri += dblEPFAnnual;

						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td>Employer PF</td>");
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEPFAnnual) + "</td>");
						sbCandiSalTable.append("</tr>");
					}
					if (isESIC) {
						double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
						double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
						dblMonthContri += dblESIMonth;
						dblAnnualContri += dblESIAnnual;

						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td>Employer ESI</td>");
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblESIAnnual) + "</td>");
						sbCandiSalTable.append("</tr>");
					}
					if (isLWF) {
						double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
						double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
						dblMonthContri += dblLWFMonth;
						dblAnnualContri += dblLWFAnnual;

						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td>Employer LWF</td>");
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblLWFAnnual) + "</td>");
						sbCandiSalTable.append("</tr>");
					}

					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td align=\"right\"><strong>Contribution Total</strong></td>");
					sbCandiSalTable.append("<td width=\"30%\" align=\"right\"><strong>" + uF.formatIntoTwoDecimal(dblAnnualContri) + "</strong></td>");

					sbCandiSalTable.append("</tr>");
				}

				double dblCTCMonthly = grossAmount + dblMonthContri;
				double dblCTCAnnualy = grossYearAmount + dblAnnualContri;

//				List<List<String>> salaryAnnualVariableDetailsList = (List<List<String>>) request.getAttribute("salaryAnnualVariableDetailsList");
//				if (salaryAnnualVariableDetailsList == null)
//					salaryAnnualVariableDetailsList = new ArrayList<List<String>>();

				int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
				if (nAnnualVariSize > 0) {

					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td colspan=\"2\" nowrap=\"nowrap\" align=\"center\"><b>ANNUAL EARNING DETAILS</b></td>");
					sbCandiSalTable.append("</tr>");

					double grossAnnualAmount = 0.0d;
					double grossAnnualYearAmount = 0.0d;
					for (int i = 0; i < nAnnualVariSize; i++) {
						List<String> innerList = salaryAnnualVariableDetailsList.get(i);
						double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
						double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
						grossAnnualAmount += dblEarnMonth;
						grossAnnualYearAmount += dblEarnAnnual;

						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td>" + uF.showData(innerList.get(0), "-") + "</td>");
//						sbEmpSalTable.append("<td align=\"right\" valign=\"bottom\">" + dblEarnMonth + "</td>");
						sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\">" + uF.formatIntoTwoDecimal(dblEarnAnnual) + "</td>");
						sbCandiSalTable.append("</tr>");
					}
					dblCTCMonthly += grossAnnualAmount;
					dblCTCAnnualy += grossAnnualYearAmount;

					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td><strong>Total</strong></td>");
//					sbEmpSalTable.append("<td align=\"right\"><strong>" + uF.formatIntoTwoDecimal(grossAnnualAmount) + "</strong></td>");
					sbCandiSalTable.append("<td width=\"30%\" align=\"right\"><strong>" + uF.formatIntoTwoDecimal(grossAnnualYearAmount) + "</strong></td>");
					sbCandiSalTable.append("</tr>");
//					sbEmpSalTable.append("</table>");
//					sbEmpSalTable.append("</td>");
//					sbEmpSalTable.append("<td>&nbsp;</td>");
//					sbEmpSalTable.append("</tr>");
				}
				sbCandiSalTable.append("</table>");

				sbCandiSalTable.append("<table border=\"1\" width=\"50%\">");
				if(!flagDisableNetTakeHomeSal) {
					sbCandiSalTable.append("<tr>");
			//===start parvez date: 12-08-2022===		
					/*sbCandiSalTable.append("<td>Net Take Home Per Month:</td>");*/
					sbCandiSalTable.append("<td>Net Earning:</td>");
			//===end parvez date: 12-08-2022===
					
					sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\"> " + uF.formatIntoTwoDecimal(netTakeHome) + "</td>");
					sbCandiSalTable.append("</tr>");
				}
//				sbEmpSalTable.append("<tr>");
//				sbEmpSalTable.append("<td>Cost To Company (Monthly):</td>");
//				sbEmpSalTable.append("<td valign=\"bottom\"> " + uF.formatIntoTwoDecimal(dblCTCMonthly) + "</td>");
//				sbEmpSalTable.append("</tr>");
//				sbEmpSalTable.append("<tr>");
		//===start parvez date: 12-08-2022===		
//				sbCandiSalTable.append("<td>Cost To Company (Annually):</td>");
				sbCandiSalTable.append("<td>Annual Gross Earning:</td>");
		//===end parvez date: 12-08-2022===		
				sbCandiSalTable.append("<td width=\"30%\" align=\"right\" valign=\"bottom\"> " + uF.formatIntoTwoDecimal(dblCTCAnnualy) + "</td>");
				sbCandiSalTable.append("</tr>");
				sbCandiSalTable.append("</table>");
			
				
				
				
				
				
				
				
				/*sbCandiSalTable.append("<table>");  
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td valign=\"top\">");
				sbCandiSalTable.append("<table>");
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td colspan=\"3\" nowrap=\"nowrap\" align=\"center\"><h5>EARNING DETAILS</h5></td>");
				sbCandiSalTable.append("</tr>");
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td align=\"right\">Salary Head</td>");
				sbCandiSalTable.append("<td width=\"30%\" align=\"right\">Monthly</td>");
				sbCandiSalTable.append("<td width=\"30%\" align=\"right\">Annual</td>");
				sbCandiSalTable.append("</tr>");
							
				grossAmount = 0.0d;
				grossYearAmount = 0.0d;
				double netTakeHome = 0.0d;
				
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td colspan=\"3\" align=\"center\"><b>Fixed Component- A</b></td>");
//				sbEmpSalTable.append("<td align=\"right\" valign=\"bottom\"></td>");
//				sbEmpSalTable.append("<td align=\"right\" valign=\"bottom\"></td>");
				sbCandiSalTable.append("</tr>");
				
				boolean veriableFlag = false;
				for(int i=0; i<salaryHeadDetailsList.size(); i++) {
					List<String> innerList = salaryHeadDetailsList.get(i);
						if(innerList.get(1).equals("E") && !uF.parseToBoolean(innerList.get(5))) {
							double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
							double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
							grossAmount += dblEarnMonth;
							grossYearAmount += dblEarnAnnual;
							
							netTakeHome += dblEarnMonth;
							
							sbCandiSalTable.append("<tr>");
							sbCandiSalTable.append("<td align=\"right\">"+uF.showData(innerList.get(0), "-")+"</td>");
							sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblEarnMonth +"</td>");
							sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblEarnAnnual+"</td>");
							sbCandiSalTable.append("</tr>");
					} 
				}
				
				if(veriableFlag) {
					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td colspan=\"3\" align=\"center\"><b>Variable Component- B</b></td>");
//					sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\"></td>");
//					sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\"></td>");
					sbCandiSalTable.append("</tr>");
				}
				
				for (int i = 0; i < salaryHeadDetailsList.size(); i++) {
					List<String> innerList = salaryHeadDetailsList.get(i);
					if (innerList.get(1).equals("E") && uF.parseToBoolean(innerList.get(5))) {
						double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
						double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
						grossAmount += dblEarnMonth;
						grossYearAmount += dblEarnAnnual;

						netTakeHome += dblEarnMonth;

						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td align=\"right\">" + uF.showData(innerList.get(0), "-") + "</td>");
						sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">" + dblEarnMonth + "</td>");
						sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">" + dblEarnAnnual + "</td>");
						sbCandiSalTable.append("</tr>");
					}
				}
				
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td align=\"right\"><strong>Gross Salary</strong></td>");
				sbCandiSalTable.append("<td align=\"right\"><strong>"+uF.formatIntoTwoDecimal(grossAmount)+"</strong></td>");
				sbCandiSalTable.append("<td align=\"right\"><strong>"+uF.formatIntoTwoDecimal(grossYearAmount)+"</strong></td>");
				sbCandiSalTable.append("</tr>");
				sbCandiSalTable.append("</table>");
				sbCandiSalTable.append("</td>");
			
				sbCandiSalTable.append("<td valign=\"top\">");
				sbCandiSalTable.append("<table>");
				
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td colspan=\"3\" nowrap=\"nowrap\" align=\"center\"><h5>DEDUCTION DETAILS</h5></td>");
				sbCandiSalTable.append("</tr>");
				
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td align=\"right\">Salary Head</td>");
				sbCandiSalTable.append("<td width=\"30%\" align=\"right\">Monthly</td>");
				sbCandiSalTable.append("<td width=\"30%\" align=\"right\">Annual</td>");
				sbCandiSalTable.append("</tr>");
							
							
		
				deductAmount = 0.0d;
				deductYearAmount = 0.0d;
			
				for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
					List<String> innerList = salaryHeadDetailsList.get(i);
					if(innerList.get(1).equals("D")) {
						double dblDeductMonth = 0.0d;
						double dblDeductAnnual = 0.0d;
						if(uF.parseToInt(innerList.get(4)) == EMPLOYEE_ESI){
							dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
							dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
						} else if(uF.parseToInt(innerList.get(4)) == EMPLOYER_ESI){
							dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
							dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
						} else {
							dblDeductMonth += Math.round(uF.parseToDouble(innerList.get(2)));
							dblDeductAnnual += Math.round(uF.parseToDouble(innerList.get(3)));
						}
						deductAmount += dblDeductMonth;
						deductYearAmount += dblDeductAnnual;
						
						netTakeHome -= dblDeductMonth;
			
						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td align=\"right\">"+uF.showData(innerList.get(0), "-")+"</td>");
						sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblDeductMonth +"</td>");
						sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblDeductAnnual +"</td>");
						sbCandiSalTable.append("</tr>");
					}
				}
				
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td align=\"right\"><strong>Deduction</strong></td>");
				sbCandiSalTable.append("<td align=\"right\"><strong>"+uF.formatIntoTwoDecimal(deductAmount)+"</strong></td>");
				sbCandiSalTable.append("<td align=\"right\"><strong>"+uF.formatIntoTwoDecimal(deductYearAmount)+"</strong></td>");
				sbCandiSalTable.append("</tr>");
				sbCandiSalTable.append("</table>");
				sbCandiSalTable.append("</td>");
				sbCandiSalTable.append("</tr>");
			
				double dblMonthContri = 0.0d;
				double dblAnnualContri = 0.0d;
				if(isEPF || isESIC || isLWF){
			
					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td valign=\"top\">");
					sbCandiSalTable.append("<table>");
					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td colspan=\"3\" nowrap=\"nowrap\" align=\"center\"><h5>CONTRIBUTION DETAILS</h5></td>");
					sbCandiSalTable.append("</tr>");
							
					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td align=\"right\">Contribution Head</td>");
					sbCandiSalTable.append("<td width=\"30%\" align=\"right\">Monthly</td>");
					sbCandiSalTable.append("<td width=\"30%\" align=\"right\">Annual</td>");
					sbCandiSalTable.append("</tr>");
					if(isEPF){
						double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
						double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
						dblMonthContri += dblEPFMonth;
						dblAnnualContri += dblEPFAnnual;
					
						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td align=\"right\">Employer PF</td>");
						sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblEPFMonth +"</td>");
						sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblEPFAnnual +"</td>");
						sbCandiSalTable.append("</tr>");
					} 
					if(isESIC){
						
						double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
						double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
						dblMonthContri += dblESIMonth;
						dblAnnualContri += dblESIAnnual;
					
						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td align=\"right\">Employer ESI</td>");
						sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblESIMonth +"</td>");
						sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblESIAnnual +"</td>");
						sbCandiSalTable.append("</tr>");
					}
					if(isLWF){
						double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
						double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
						dblMonthContri += dblLWFMonth;
						dblAnnualContri += dblLWFAnnual;
					
						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td align=\"right\">Employer LWF</td>");
						sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblLWFMonth +"</td>");
						sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblLWFAnnual +"</td>");
						sbCandiSalTable.append("</tr>");
					}
					
					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td align=\"right\"><strong>Contribution Total</strong></td>");
					sbCandiSalTable.append("<td align=\"right\"><strong>"+uF.formatIntoTwoDecimal(dblMonthContri)+"</strong></td>");
					sbCandiSalTable.append("<td align=\"right\"><strong>"+uF.formatIntoTwoDecimal(dblAnnualContri)+"</strong></td>");
						
					sbCandiSalTable.append("</tr>");
					sbCandiSalTable.append("</table>");
					sbCandiSalTable.append("</td>");
					sbCandiSalTable.append("<td>&nbsp;</td>");
					sbCandiSalTable.append("</tr>");
				}
				
				double dblCTCMonthly = grossAmount + dblMonthContri;
				double dblCTCAnnualy = grossYearAmount + dblAnnualContri;
				
				int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
				if(nAnnualVariSize > 0){
				
					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td valign=\"top\">");
					sbCandiSalTable.append("<table>");
					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td colspan=\"3\" nowrap=\"nowrap\" align=\"center\"><h5>ANNUAL EARNING DETAILS</h5></td>");
					sbCandiSalTable.append("</tr>");
					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td align=\"right\">Salary Head</td>");
					sbCandiSalTable.append("<td width=\"30%\" align=\"right\">Monthly</td>");
					sbCandiSalTable.append("<td width=\"30%\" align=\"right\">Annual</td>");
					sbCandiSalTable.append("</tr>");
							
					double grossAnnualAmount = 0.0d;
					double grossAnnualYearAmount = 0.0d;
					for(int i = 0; i < nAnnualVariSize; i++){
						List<String> innerList = salaryAnnualVariableDetailsList.get(i);
						double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
						double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
						grossAnnualAmount += dblEarnMonth;
						grossAnnualYearAmount += dblEarnAnnual;
			
						sbCandiSalTable.append("<tr>");
						sbCandiSalTable.append("<td align=\"right\">"+uF.showData(innerList.get(0), "-")+"</td>");
						sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblEarnMonth +"</td>");
						sbCandiSalTable.append("<td align=\"right\" valign=\"bottom\">"+dblEarnAnnual+"</td>");
						sbCandiSalTable.append("</tr>");
					} 
					dblCTCMonthly += grossAnnualAmount;
					dblCTCAnnualy += grossAnnualYearAmount;
				
					sbCandiSalTable.append("<tr>");
					sbCandiSalTable.append("<td align=\"right\"><strong>Total</strong></td>");
					sbCandiSalTable.append("<td align=\"right\"><strong>"+uF.formatIntoTwoDecimal(grossAnnualAmount)+"</strong></td>");
					sbCandiSalTable.append("<td align=\"right\"><strong>"+uF.formatIntoTwoDecimal(grossAnnualYearAmount)+"</strong></td>");
					sbCandiSalTable.append("</tr>");
					sbCandiSalTable.append("</table>");
					sbCandiSalTable.append("</td>");
					sbCandiSalTable.append("<td>&nbsp;</td>");
					sbCandiSalTable.append("</tr>");
				}
				sbCandiSalTable.append("</table>");
				
				sbCandiSalTable.append("<table>");
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td align=\"right\">Net Take Home Per Month:</td>");
				sbCandiSalTable.append("<td valign=\"bottom\"> "+uF.formatIntoTwoDecimal(netTakeHome)+"</td>");
				sbCandiSalTable.append("</tr>");                  
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td align=\"right\">Cost To Company (Monthly):</td>");
				sbCandiSalTable.append("<td valign=\"bottom\"> "+uF.formatIntoTwoDecimal(dblCTCMonthly)+"</td>");
				sbCandiSalTable.append("</tr>");
				sbCandiSalTable.append("<tr>");
				sbCandiSalTable.append("<td align=\"right\">Cost To Company (Annually):</td>");
				sbCandiSalTable.append("<td valign=\"bottom\"> "+uF.formatIntoTwoDecimal(dblCTCAnnualy)+"</td>");
				sbCandiSalTable.append("</tr>");
				sbCandiSalTable.append("</table>");
			*/
				
			}
			
			/**
			 * Salary Structure End
			 * */
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return sbCandiSalTable;

	
	}
	public void sendMail() {
		


		Connection con = null;
		ResultSet rst = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmCandiInfo = CF.getCandiInfoMap(con, false);

			StringBuilder sbCandiSalTable = getCandiSalaryDetails(con);	
			if(sbCandiSalTable == null) sbCandiSalTable = new StringBuilder();
			
			Map<String, String> hmCandiInner = hmCandiInfo.get(getCandidateId());
			
			pst = con.prepareStatement("select e.candi_application_deatils_id,e.candidate_id,r.org_id,d.designation_code,d.designation_name from recruitment_details r," +
					"designation_details d,candidate_application_details e where r.recruitment_id = e.recruitment_id " +
					"and r.designation_id=d.designation_id and candidate_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, String> hmCandiDesig = new HashMap<String, String>();
			Map<String, String> hmCandiOrg = new HashMap<String, String>();
			while(rst.next()){				
				hmCandiDesig.put(rst.getString("candidate_id"), rst.getString("designation_name"));
				hmCandiOrg.put(rst.getString("candidate_id"), rst.getString("org_id"));
				setCandiApplicationId(rst.getString("candi_application_deatils_id"));
			}
			rst.close();
			pst.close();
			
			Map<String, Map<String, String>> hmHeader=new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmFooter=new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select * from document_collateral");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				if(rst.getString("_type").equals("H")){
					Map<String, String> hmInner=new HashMap<String, String>();
					hmInner.put("COLLATERAL_ID", rst.getString("collateral_id"));
					hmInner.put("COLLATERAL_PATH", rst.getString("collateral_image"));
					hmInner.put("COLLATERAL_IMG_ALIGN", rst.getString("image_align"));
					hmInner.put("COLLATERAL_TEXT", uF.showData(rst.getString("collateral_text"),""));
					
					hmHeader.put(rst.getString("collateral_id"), hmInner);
				}else{
					Map<String, String> hmInner=new HashMap<String, String>();
					hmInner.put("COLLATERAL_ID", rst.getString("collateral_id"));
					hmInner.put("COLLATERAL_PATH", rst.getString("collateral_image"));
					hmInner.put("COLLATERAL_IMG_ALIGN", rst.getString("image_align"));
					hmInner.put("COLLATERAL_TEXT", uF.showData(rst.getString("collateral_text"),""));
					
					hmFooter.put(rst.getString("collateral_id"), hmInner);
				}
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select * from document_comm_details where trigger_nodes like '%,"+NODE_CANDIDATE_OFFER_ID+",%' and status=1 and org_id=? order by document_id desc limit 1");
			pst.setInt(1, uF.parseToInt(hmCandiOrg.get(getCandidateId())));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String strDocumentName = null;
			String strDocumentContent = null;
			String strDocumentHeader = null;
			String strDocumentFooter = null;
			String strHeader = null;
			String strFooter = null;
			String strHeaderImageAlign="";
			String strHeaderCollateralText="";
			String strHeaderTextAlign="";
			String strFooterImageAlign="";
			String strFooterCollateralText="";
			String strFooterTextAlign="";
			
			while (rst.next()) {  
				strDocumentName = rst.getString("document_name");
				strDocumentContent = rst.getString("document_text");
				
				if(rst.getString("collateral_header")!=null && !rst.getString("collateral_header").equals("") && hmHeader.get(rst.getString("collateral_header"))!=null){
					Map<String, String> hmInner=hmHeader.get(rst.getString("collateral_header"));
					strHeader = uF.showData(hmInner.get("COLLATERAL_PATH"),"");
					strHeaderImageAlign=uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"),"");
					strHeaderCollateralText=uF.showData(hmInner.get("COLLATERAL_TEXT"),"");
					strHeaderTextAlign=uF.showData(hmInner.get("COLLATERAL_TEXT_ALIGN"),"");
				}
				if(rst.getString("collateral_footer")!=null && !rst.getString("collateral_footer").equals("") && hmFooter.get(rst.getString("collateral_footer"))!=null){
					Map<String, String> hmInner=hmFooter.get(rst.getString("collateral_footer"));
					strFooter = uF.showData(hmInner.get("COLLATERAL_PATH"),"");
					strFooterImageAlign=uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"),"");
					strFooterCollateralText=uF.showData(hmInner.get("COLLATERAL_TEXT"),"");
					strFooterTextAlign=uF.showData(hmInner.get("COLLATERAL_TEXT_ALIGN"),"");
				}
			}
			rst.close();
			pst.close();
			
			if(strDocumentName!=null){
				strDocumentName = strDocumentName.replace(" ", "");
			}
			
			String strDomain = request.getServerName().split("\\.")[0];
			
			CandidateNotifications nF = new CandidateNotifications(N_CANDI_JOINING_OFFER_CTC, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrEmpId(getCandiApplicationId());
			nF.setStrRecruitmentId(getRecruitId());
			nF.setStrHostAddress(CF.getStrEmailLocalHost()); 
			nF.setStrHostPort(CF.getStrHostPort()); 
//			nF.setStrContextPath(request.getServletPath());
			nF.setStrContextPath(request.getContextPath());
			 
			nF.setStrCandiFname(hmCandiInner.get("FNAME"));
			nF.setStrCandiLname(hmCandiInner.get("LNAME"));
			nF.setStrCandiCTC(hmCandiInner.get("OFFERED_CTC"));
			nF.setStrCandiAnnualCTC(hmCandiInner.get("OFFERED_ANNUAL_CTC"));
			nF.setStrCandiJoiningDate(hmCandiInner.get("JOINING_DATE"));
			nF.setStrOfferedSalaryStructure(sbCandiSalTable.toString());
			nF.setStrCandidateId(hmCandiInner.get("CANDI_ID"));//Created By Dattatray Date : 05-10-21
			nF.setStrRecruitmentDesignation(hmCandiDesig.get(getCandidateId()));
			nF.setOfferAcceptData("?candidateID="+getCandidateId()+"&recruitID="+getRecruitId()+"&candiOfferAccept=yes&updateRemark=Update");
			
			Map<String, String> hmParsedContent = null;

//			Document document = new Document(PageSize.A4);
			Document document = new Document(PageSize.A4,40, 40, 10, 60); 

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			StringBuilder sbHeader = new StringBuilder();
			StringBuilder sbFooter = new StringBuilder();
			String strDocName = null;
			String strDocContent = null;
			if(strDocumentContent!=null){
				
//				hmParsedContent  = nF.parseContent(strDocumentContent, "", "");
				hmParsedContent  = nF.parseContent(strDocumentContent, "", "");
				
				
				strDocName = strDocumentName;
				strDocContent = hmParsedContent.get("MAIL_BODY");
				String strDocument = hmParsedContent.get("MAIL_BODY");
				
				//Start By Dattatray Date:07-10-21
				String brTag="";
				if(nF.getStrCandiAddress().trim().isEmpty()) {
					brTag = "<br/><br/><br/>";
				}
				if (nF.getIntAddressLineCnt() == 1) {
					brTag = "<br/><br/>";
				}else if (nF.getIntAddressLineCnt() == 2) {
					brTag = "<br/>";
				}
				//Ended By Dattatray Date:07-10-21
				if(strDocument!=null) {
//					strDocument = strDocument.replaceAll("<br/>", "");
					
					//Satrt Dattatray Date : 31-07-21  
					if (strDocument.contains("<pre style=\"text-align:justify\">") || strDocument.contains("<pre style=\"text-align:justify;\">") || strDocument.contains("<pre style=\"text-align: justify;\">") || strDocument.contains("<pre style=\"text-align: justify\">")) {
//						System.out.println("if");
						if (strDocument.contains("<pre ")) {
							strDocument = strDocument.replaceAll("<pre ", "<p ");
						}
						 if(strDocument.contains("<pre>") ){
							 strDocument = strDocument.replaceAll("<pre>", "<p>");
						 }
						//
						/*if (strDocument.contains("><span")) {
							strDocument = strDocument.replaceAll("><span ", "><p style=\"text-align: justify\"><span ");
						}
						if (strDocument.contains("</span>")) {
							strDocument = strDocument.replaceAll("</span>", "</span></p>");
						}*/

						if (strDocument.contains("</pre>")) {
							strDocument = strDocument.replaceAll("</pre>", "</p>");
						}
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p>", "<br/><p style=\"text-align: justify\">", true, true, "<p>");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<li>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
//						strDocument = replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>", "<p style=\"text-align: justify\">", true, true, "<p style=\"text-align: justify\">");
//						strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
//						strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</p>", true, true, "<p>");
//						strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
						//Created By Dattatray Date:07-10-21
						if(strDocument.contains("Director of People")) {
							strDocument = uF.replaceBetweenTwoString(strDocument, "Director of ", "People", true, true,"Director of People"+brTag);//Created By Dattatray Date:05-10-21
						}
					}else {
						System.out.println("Else");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<li>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<p>", true, true, "<p>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", " </li>	", true, true, "</li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<pre>", true, true, "<pre>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<pre>", "<br/>", true, true, "<pre>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
						//Created By Dattatray Date:07-10-21
						if(strDocument.contains("Director of People")) {
							strDocument = uF.replaceBetweenTwoString(strDocument, "Director of ", "People", true, true,"Director of People"+brTag);//Created By Dattatray Date:05-10-21
						}
					}
					//End Dattatray Date : 31-07-21 
				}
				
				String headerPath="";
				if(strHeader!=null && !strHeader.equals("")){
//					headerPath=CF.getStrDocRetriveLocation()+strHeader;
					if(CF.getStrDocRetriveLocation()==null) { 
						headerPath =  DOCUMENT_LOCATION + strHeader;
					} else { 
						headerPath = CF.getStrDocSaveLocation() +I_COLLATERAL+"/"+I_IMAGE+"/"+strHeader;
					}
				}
				
				
				/*if(headerPath != null && !headerPath.equals("")) {
					//sbHeader.append("<table><tr><td><img height=\"60\" src=\""+strDocumentHeader+"\"></td></tr></table>");
					sbHeader.append("<table style=\"width: 100%;\"><tr>");
					if(strHeaderImageAlign!=null && strHeaderImageAlign.equals("R")) { 
						sbHeader.append("<td width=\"70%\" valign=\"middle\" style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>" +
								"<td align=\"right\">");
						if(headerPath != null && !headerPath.equals("")) {
							sbHeader.append("<img height=\"30\" src=\""+headerPath+"\">");
						}
						sbHeader.append("</td>");						
					
					} else if(strHeaderImageAlign !=null && strHeaderImageAlign.equals("C")) { 
						sbHeader.append("<td colspan=\"2\" align=\"Center\">");
						if(headerPath != null && !headerPath.equals("")) {
							sbHeader.append("<img height=\"30\" src=\""+headerPath+"\"><br/>");
						}
						sbHeader.append(""+strHeaderCollateralText+"</td>");
					} else {
						sbHeader.append("<td>");
						if(headerPath != null && !headerPath.equals("")) {
							sbHeader.append("<img height=\"30\" src=\""+headerPath+"\">");
						}
						sbHeader.append("</td> <td valign=\"middle\" style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>");
					}
					sbHeader.append("</tr></table>");
					
				} else {
					
					sbHeader.append("<table style=\"width: 100%;\"><tr>");
					if(strHeaderTextAlign!=null && strHeaderTextAlign.equals("R")) { 
						sbHeader.append("<td colspan=\"2\" align=\"right\" valign=\"middle\" style=\"padding-right: 50px;\">"+strHeaderCollateralText+"</td>");
						
					} else if(strHeaderTextAlign!=null && strHeaderTextAlign.equals("C")) { 
						sbHeader.append("<td colspan=\"2\" align=\"center\" valign=\"middle\">"+strHeaderCollateralText+"</td>");
					} else { 
						sbHeader.append("<td colspan=\"2\" valign=\"middle\" style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>");
					}
					sbHeader.append("</tr></table>");
				
				}*/
				
				if(headerPath != null && !headerPath.equals("")) {
					sbHeader.append("<table style=\"width: 100%;\"><tr>");
					sbHeader.append("<td>");
					if(strHeader!=null && !strHeader.equals("")) {
						sbHeader.append("<img src=\""+headerPath+"\">");
					}
					sbHeader.append("</td>");	
					sbHeader.append("</tr></table>");
				}
				
				
//				String footerPath="";   
//				if(strFooter!=null && !strFooter.equals("")){
////					footerPath=CF.getStrDocRetriveLocation()+strFooter;
//					if(CF.getStrDocRetriveLocation()==null) { 
//						footerPath =  DOCUMENT_LOCATION + strFooter;
//					} else { 
//						footerPath = CF.getStrDocRetriveLocation() +I_COLLATERAL+"/"+I_IMAGE+"/"+strFooter;
//					}
//				}
//				
//				
//				if(footerPath != null && !footerPath.equals("")) {
//					//sbFooter.append("<table><tr><td><img height=\"60\" src=\""+strDocumentFooter+"\"></td></tr></table>");
//					
//					sbFooter.append("<table><tr>");
//					if(strFooterImageAlign!=null && strFooterImageAlign.equals("R")) { 
//						sbFooter.append("<td width=\"70%\" valign=\"middle\" style=\"padding-left: 50px;\">"+strFooterCollateralText+"</td> <td align=\"right\">");
//						if(footerPath != null && !footerPath.equals("")) {
//							sbFooter.append("<img height=\"60\" src=\""+footerPath+"\">");
//						}
//						sbFooter.append("</td>");						
//					
//					} else if(strFooterImageAlign!=null && strFooterImageAlign.equals("C")) { 
//						sbFooter.append("<td align=\"Center\">");
//						if(footerPath != null && !footerPath.equals("")) {
//							sbFooter.append("<img height=\"60\" src=\""+footerPath+"\"><br/>");
//						}
//						sbFooter.append(""+strFooterCollateralText+"</td>");
//					} else { 
//						sbFooter.append("<td>");
//						if(footerPath != null && !footerPath.equals("")) {
//							sbFooter.append("<img height=\"60\" src=\""+footerPath+"\">");
//						}
//						sbFooter.append("</td> <td valign=\"middle\" style=\"padding-left: 50px;\">"+strFooterCollateralText+"</td>");
//					}
//					sbFooter.append("</tr></table>");
//				} else {
//
//					sbFooter.append("<table><tr>");
//					if(strFooterTextAlign!=null && strFooterTextAlign.equals("R")) { 
//						sbFooter.append("<td colspan=\"2\" align=\"right\" valign=\"middle\" style=\"padding-right: 50px;\">"+strFooterCollateralText+"</td>");
//					
//					} else if(strFooterTextAlign!=null && strFooterTextAlign.equals("C")) { 
//						sbFooter.append("<td colspan=\"2\" align=\"center\" valign=\"middle\">"+strFooterCollateralText+"</td>");
//					} else { 
//						sbFooter.append("<td colspan=\"2\" valign=\"middle\" style=\"padding-right: 50px;\">"+strFooterCollateralText+"</td>");
//					}
//					sbFooter.append("</tr></table>");
//				}
				
				PdfWriter writer = PdfWriter.getInstance(document, buffer);
				HeaderFooterPageEvent event = new HeaderFooterPageEvent(sbHeader.toString(),strFooterCollateralText);
			    writer.setPageEvent(event);
				document.open();
				HTMLWorker hw = new HTMLWorker(document);
//				hw.parse(new StringReader(sbHeader.toString())); 
				hw.parse(new StringReader(strDocument));
//				hw.parse(new StringReader(sbFooter.toString()));
				document.close();  
			}
			 
			/*nF.setStrCandiSalBasic(hmSalaryAmountMap.get("BASIC"));
			nF.setStrCandiSalHRA(hmSalaryAmountMap.get("HRA"));
			nF.setStrCandiSalConvAllow(hmSalaryAmountMap.get("CONVEYANCE_ALLOWANCE"));
			nF.setStrCandiSalOverTime(hmSalaryAmountMap.get("OVER_TIME"));
			nF.setStrCandiSalGratuity(hmSalaryAmountMap.get("GRATUITY"));
			nF.setStrCandiSalBonus(hmSalaryAmountMap.get("BONUS"));
			nF.setStrCandiSalMobExpenses(hmSalaryAmountMap.get("MOBILE_EXPENSES"));
			nF.setStrCandiSalMedicalAllow(hmSalaryAmountMap.get("MEDICAL_ALLOW"));
			nF.setStrCandiSalSpecialAllow(hmSalaryAmountMap.get("SPECIAL_ALLOW"));
			nF.setStrCandiSalArrearsAndOtherAllow(hmSalaryAmountMap.get("ARREARS_AND_OTHER_ALLOW"));
			nF.setStrCandiTotGrossSalary(hmSalaryAmountMap.get("TOT_GROSS_SALARY"));
			
			nF.setStrCandiSalDeductProftax(hmSalaryAmountMap.get("PROFESSIONAL_TAX"));
			nF.setStrCandiSalDeductTDS(hmSalaryAmountMap.get("TDS"));
			nF.setStrCandiSalDeductPFEmpCont(hmSalaryAmountMap.get("EMPLOYEE_EPF"));
			nF.setStrCandiSalDeductPFEmprCont(hmSalaryAmountMap.get("EMPLOYER_EPF"));
			nF.setStrCandiSalDeductESIEmpr(hmSalaryAmountMap.get("EMPLOYER_ESI"));
			nF.setStrCandiSalDeductESIEmp(hmSalaryAmountMap.get("EMPLOYEE_ESI"));
			nF.setStrCandiSalDeductLoan(hmSalaryAmountMap.get("LOAN"));
			nF.setStrCandiTotDeduction(hmSalaryAmountMap.get("TOT_DEDUCTION"));*/
			
			byte[] bytes = buffer.toByteArray();			
			
			if(strDocumentContent!=null) {
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName(strDocumentName+".pdf");
			}
			String strMailSubject = nF.getStrEmailSubject();
			String strMailBody = nF.getStrNewEmailBody();
			nF.sendNotifications();
			
			saveDocumentActivity(con, uF, CF, strDocName, sbHeader.toString(), strDocContent, strFooterCollateralText, strMailSubject, strMailBody);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
		
		
		
		
	}
private void saveDocumentActivity(Connection con, UtilityFunctions uF,CommonFunctions CF, String strDocumentName, String strDocumentHeader, String strDocumentContent, String strDocumentFooter, String strMailSubject, String strMailBody){
		
		PreparedStatement pst = null;
		
		try {
			pst = con.prepareStatement("insert into document_activities (document_name, document_content, effective_date, entry_date, user_id, candi_id, mail_subject, mail_body, document_header, document_footer) values (?,?,?,?,?,?,?,?,?,?)");
			pst.setString(1, strDocumentName);
			pst.setString(2, strDocumentContent);
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(5, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(6, uF.parseToInt(getCandidateId()));
			pst.setString(7, strMailSubject);
			pst.setString(8, strMailBody);
			pst.setString(9, strDocumentHeader);
			pst.setString(10, strDocumentFooter);
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getCandidateId() {
		return candidateId;
	}
	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
	}
	public boolean isDisableSalaryStructure() {
		return disableSalaryStructure;
	}
	public boolean getDisableSalaryStructure() {
		return disableSalaryStructure;
	}

	public void setDisableSalaryStructure(boolean disableSalaryStructure) {
		this.disableSalaryStructure = disableSalaryStructure;
	}
	
	public String getJoiningdate() {
		return joiningdate;
	}
	public void setJoiningdate(String joiningdate) {
		this.joiningdate = joiningdate;
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
	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}
	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}
	public String getRecruitId() {
		return recruitId;
	}
	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}
	public String getHrchoice() {
		return hrchoice;
	}

	public void setHrchoice(String hrchoice) {
		this.hrchoice = hrchoice;
	}
	public String getStrinterviewcommentHR() {
		return strinterviewcommentHR;
	}

	public void setStrinterviewcommentHR(String strinterviewcommentHR) {
		this.strinterviewcommentHR = strinterviewcommentHR;
	}

	public List<FillJD> getJDList() {
		return JDList;
	}

	public void setJDList(List<FillJD> jDList) {
		JDList = jDList;
	}
	public String getCandiApplicationId() {
		return candiApplicationId;
	}

	public void setCandiApplicationId(String candiApplicationId) {
		this.candiApplicationId = candiApplicationId;
	}

	public String getStrJD() {
		return strJD;
	}

	public void setStrJD(String strJD) {
		this.strJD = strJD;
	}

	public String getCandID() {
		return CandID;
	}

	public void setCandID(String candID) {
		CandID = candID;
	}

}