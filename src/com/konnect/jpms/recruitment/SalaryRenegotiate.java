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
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.util.CandidateNotifications;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SalaryRenegotiate extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = 6007469343248300578L;

	HttpSession session;
	CommonFunctions CF;

	private List<FillSalaryHeads> salaryHeadList;
	List<List<String>> al = new ArrayList<List<String>>();

	private String[] salary_head_id;
	private String[] salary_head_value;
	private String[] isDisplay;
	private String[] emp_salary_id;
	private String[] salary_type;
	private String CCID; 
	private String strUserType;
	private String strSessionEmpId;
	private String effectiveDate;
	private String renegotiateRemark; 
	
	private String candidateID;
	private String recruitId;
	private String tableMode;
	
	String candiApplicationId;
	String ctcAmt;
	
	private boolean disableSalaryStructure;
	
	public String execute() {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		request.setAttribute(PAGE, "/jsp/recruitment/SalaryRenegotiate.jsp");
		request.setAttribute("empId", getCandidateID());
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));

//		salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads();
		String update = request.getParameter("salryUpdate");
		System.out.println("update ===>> " + update);
		
		if(update != null && update.equalsIgnoreCase("Update")){
			updateCandidateSalaryDetails();
			return SUCCESS;
		} else {
//			viewCandidateSalaryDetails();
			viewUpdateEmployeeSalaryDetails();
			return LOAD;
		}
		//return LOAD;
	}

	private void updateCandidateSalaryDetails() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		
		try {
			
			con = db.makeConnection(con);
			
			String level_id = null;
			pst = con.prepareStatement("select level_id from candidate_application_details join recruitment_details using (recruitment_id) where candidate_id= ? and recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateID()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("pst level id ===> " + pst);
//			System.out.println("new Date ===> " + new Date());
			if (rs.next()) {
				level_id = rs.getString("level_id");
			}
			rs.close();
			pst.close();
			
			salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads(level_id);
			
			pst = con.prepareStatement("select * from salary_details where level_id=?");
			pst.setInt(1, uF.parseToInt(level_id));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, String> hmEarningDeductionMap = new HashMap<String, String>();
//			Map<String, String> hmSalaryTypeMap = new HashMap<String, String>();
			while(rs.next()){
				hmEarningDeductionMap.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
//				hmSalaryTypeMap.put(rs.getString("salary_head_id"), rs.getString("salary_type"));
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select * from candidate_salary_details where entry_date = ? and emp_id = ? and recruitment_id = ?");
//			pst.setDate	(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
//			pst.setInt(2, uF.parseToInt(getCandidateID()));
//			pst.setInt(3, uF.parseToInt(getRecruitId()));
//			rs = pst.executeQuery();
////			System.out.println("new Date ===> " + new Date());
//			boolean isCurrentDateExist = false;
//			while(rs.next()){
//				isCurrentDateExist = true;
//			}
//			rs.close();
//			pst.close();
			
			pst = con.prepareStatement("select * from candidate_salary_details where emp_id = ? and recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateID()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			boolean isCurrentDateExist = false;
			while(rs.next()){
				isCurrentDateExist = true;
			}
			rs.close();
			pst.close();
			
			if(isCurrentDateExist){
				pst = con.prepareStatement("delete from candidate_salary_details where emp_id = ? and recruitment_id = ?");
				pst.setInt(1, uF.parseToInt(getCandidateID()));
				pst.setInt(2, uF.parseToInt(getRecruitId()));
				pst.execute();
				pst.close();
			}
			
			
//			String salary_head_value1[] = request.getParameterValues("salary_head_value");
			for(int i=0; i<emp_salary_id.length; i++) {
//				System.out.println("salary_head_value1===>"+salary_head_value[i]);
				String isDisplay = (String) request.getParameter("isDisplay_"+getSalary_head_id()[i]);
				pst = con.prepareStatement("INSERT INTO candidate_salary_details (emp_id, salary_head_id, amount, entry_date, user_id, pay_type, isdisplay, " +
						"service_id, effective_date, earning_deduction, recruitment_id) VALUES (?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setInt(1, uF.parseToInt(getCandidateID()));
				pst.setInt(2, uF.parseToInt(getSalary_head_id()[i]));
				pst.setDouble(3, uF.parseToDouble(salary_head_value[i]));
				pst.setDate	(4, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
				pst.setInt(5, 1);
				pst.setString(6, "M");
				pst.setBoolean(7, uF.parseToBoolean(isDisplay));
				pst.setInt(8, uF.parseToInt(getCCID()));
				if(getEffectiveDate() == null || getEffectiveDate().equals("")){
					pst.setDate	(9, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
				}else{
					pst.setDate	(9, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				}
				pst.setString(10, hmEarningDeductionMap.get(getSalary_head_id()[i]));
				pst.setInt(11, uF.parseToInt(getRecruitId()));
//					System.out.println("pst insertEmpSalaryDetails==>"+pst);
				pst.execute();
				pst.close();
//					System.out.println("pst insertEmpSalaryDetails==>"+pst);
			}
			
			pst = con.prepareStatement("update candidate_application_details set is_disable_sal_calculate=? where candidate_id = ? and recruitment_id=? ");
			pst.setBoolean(1, getDisableSalaryStructure());
			pst.setInt(2, uF.parseToInt(getCandidateID()));
			pst.setInt(3, uF.parseToInt(getRecruitId()));
			pst.execute();
			pst.close();
			
			CandidateMyProfilePopup candidateMyProfilePopup = new CandidateMyProfilePopup();
			candidateMyProfilePopup.request = request;
			candidateMyProfilePopup.session = session;
			candidateMyProfilePopup.CF = CF;
			candidateMyProfilePopup.setCandID(getCandidateID());
			candidateMyProfilePopup.setRecruitId(getRecruitId());
			Map<String, String> hmCandiOffered = candidateMyProfilePopup.getCandiOfferedCTC(con);
			if(hmCandiOffered == null) hmCandiOffered = new HashMap<String, String>();
			
//			String total_earning_value = request.getParameter("hide_total_earning_value");
			if(getTableMode().equals("reject")){
				pst = con.prepareStatement("update candidate_application_details set ctc_offered=?, annual_ctc_offered=?, renegotiate_remark=?, candidate_status = 0 where candidate_id = ? and recruitment_id = ?");
			}else{
				pst = con.prepareStatement("update candidate_application_details set ctc_offered=?, annual_ctc_offered=?, renegotiate_remark=? where candidate_id = ? and recruitment_id = ?");
			}
			pst.setDouble(1, uF.parseToDouble(hmCandiOffered.get("CANDI_CTC")));
			pst.setDouble(2, uF.parseToDouble(hmCandiOffered.get("CANDI_ANNUAL_CTC")));
			pst.setString(3, uF.showData(getRenegotiateRemark(), ""));
			pst.setInt(4, uF.parseToInt(getCandidateID()));
			pst.setInt(5, uF.parseToInt(getRecruitId()));
			pst.execute();
			pst.close();
//			System.out.println("pst updateCTCOfferd ==>"+pst);
			
			
			pst=con.prepareStatement("delete from candidate_activity_details where recruitment_id=? and candi_id=? and user_id=? and " +
					"activity_id = ?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getCandidateID()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, CANDI_ACTIVITY_SALARY_RENIGOTIATION_ID);
			pst.executeUpdate();
			pst.close();
			
			pst=con.prepareStatement("insert into candidate_activity_details(recruitment_id,candi_id,activity_name,user_id,entry_date,activity_id) values(?,?,?,?,?,?)");
			pst.setInt(1,uF.parseToInt(getRecruitId()));
			pst.setInt(2,uF.parseToInt(getCandidateID()));
			pst.setString(3, "Salary Renegotiation");
			pst.setInt(4,uF.parseToInt(strSessionEmpId));
			pst.setDate(5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setInt(6, CANDI_ACTIVITY_SALARY_RENIGOTIATION_ID);
			pst.execute();
			pst.close();
			
			sendMail();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void viewUpdateEmployeeSalaryDetails() {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF= new UtilityFunctions();
		
		try {
			
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];
			
			con = db.makeConnection(con);
			
			
			List<List<String>> alE = new ArrayList<List<String>>();
			
			pst = con.prepareStatement("select level_id,is_disable_sal_calculate from candidate_application_details join recruitment_details using (recruitment_id) " +
					"where candidate_id = ? and recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateID()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
			String levelId = null;
	//		System.out.println("new Date ===> " + new Date());
			if (rs.next()) {
				levelId = rs.getString("level_id");
				setDisableSalaryStructure(uF.parseToBoolean(rs.getString("is_disable_sal_calculate")));
			}
			rs.close();
			pst.close();
			
			String salaryBandId = CF.getSalaryBandId(con, getCtcAmt(), levelId);
//			System.out.println("salaryBandId ===>> " + salaryBandId);
			
			salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads(levelId);
			
			pst = con.prepareStatement("SELECT * FROM salary_details WHERE level_id = ? and is_annual_variable=true and (is_delete is null or is_delete=false) order by weight");
			pst.setInt(1, uF.parseToInt(levelId));
			rs = pst.executeQuery();
			List<String> alAnnualSalaryHead = new ArrayList<String>();
			while(rs.next()) {
				if(!alAnnualSalaryHead.contains(rs.getString("salary_head_id"))){
					alAnnualSalaryHead.add(rs.getString("salary_head_id"));
				}
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmSalaryMap = CF.getSalaryHeadsMap(con, uF.parseToInt(levelId));
			int nEarningCnt = 0;
			int nDisplay = 0;
			if(uF.parseToDouble(getCtcAmt())>0) {
				pst = con.prepareStatement("SELECT * from salary_details sd WHERE (level_id = ? OR level_id = 0) and salary_band_id=? " +
						"and (sd.is_delete is null or sd.is_delete=false) order by sd.earning_deduction desc, weight");
				pst.setInt(1, uF.parseToInt(levelId));
				pst.setInt(2, uF.parseToInt(salaryBandId));
//				System.out.println("pst ===> "+pst);
				rs = pst.executeQuery();
		//		System.out.println("new Date ===> " + new Date());
				String alHeadId = "";
				Map<String, String> hmSalaryAmountMap = new HashMap<String, String>();
				List alSalaryDuplicationTracer = new ArrayList();
				Map<String, String> hmTotal = new HashMap<String, String>();
				while(rs.next()) {
					if(uF.parseToInt(rs.getString("salary_head_id")) > 0 && uF.parseToInt(rs.getString("salary_head_id"))!=CTC && rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").trim().equals("E")) {
						nEarningCnt++;
					}
					
					nDisplay++;
					hmSalaryAmountMap.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));
					
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
					
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount")))); //7
					
					double dblAmount = 0;
					String strAmountType = rs.getString("salary_head_amount_type");
					if(strAmountType!=null && strAmountType.equalsIgnoreCase("P")){
						dblAmount = rs.getDouble("salary_calculate_amount");
					}else if(strAmountType!=null && strAmountType.equalsIgnoreCase("A")){
						dblAmount = rs.getDouble("salary_head_amount");
					}
					if(rs.getInt("salary_head_id") == CTC && uF.parseToDouble(getCtcAmt())>0) {
						dblAmount = uF.parseToDouble(getCtcAmt());
					}
					StringBuilder sbMulcalType = new StringBuilder();
					if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))){
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
					
					int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
					
					if(index>=0){
						alE.remove(index);
						alE.add(index, alInner);
					}else{
						alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
						alE.add(alInner);
					}			
				}
				rs.close();
				pst.close();
			} else {
			
				pst = con.prepareStatement("SELECT effective_date,weight,isdisplay,pay_type,user_id,entry_date,amount,emp_salary_id,salary_head_amount,salary_calculate_amount," +
						"sd.earning_deduction,salary_head_amount_type,sub_salary_head_id, sd.salary_head_id as salary_head_id,multiple_calculation,max_cap_amount FROM " +
						"(SELECT * FROM candidate_salary_details WHERE emp_id = ? and recruitment_id =? AND service_id = ? AND effective_date = (SELECT MAX(effective_date) " +
						"FROM candidate_salary_details WHERE emp_id = ? and recruitment_id =?)) asd RIGHT JOIN salary_details sd " + // AND effective_date <= ?
						"ON sd.salary_head_id = asd.salary_head_id WHERE (level_id = ? OR level_id = 0) and salary_band_id=? and (sd.is_delete is null or sd.is_delete=false) order by sd.earning_deduction desc, weight");
				pst.setInt(1, uF.parseToInt(getCandidateID()));
				pst.setInt(2, uF.parseToInt(getRecruitId()));
				pst.setInt(3, 0);  // Default Service Id
				pst.setInt(4, uF.parseToInt(getCandidateID()));
				pst.setInt(5, uF.parseToInt(getRecruitId()));
		//		pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(6, uF.parseToInt(levelId));
				pst.setInt(7, uF.parseToInt(salaryBandId));
				System.out.println("pst ===> "+pst);
				rs = pst.executeQuery();
		//		System.out.println("new Date ===> " + new Date());
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
					
					if(getEffectiveDate() == null) {
						setEffectiveDate(uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
					}
					hmSalaryAmountMap.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount"))));
					
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
					
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("salary_head_amount")))); //7
					
					double dblAmount = 0;
					if(rs.getString("amount")==null){
						String strAmountType = rs.getString("salary_head_amount_type");
						if(strAmountType!=null && strAmountType.equalsIgnoreCase("P")){
							dblAmount = rs.getDouble("salary_calculate_amount");
						}else if(strAmountType!=null && strAmountType.equalsIgnoreCase("A")){
							dblAmount = rs.getDouble("salary_head_amount");
						}
					}else{
						dblAmount = rs.getDouble("amount") ;
					}
					if(rs.getInt("salary_head_id") == CTC && uF.parseToDouble(getCtcAmt())>0) {
						dblAmount = uF.parseToDouble(getCtcAmt());
					}
					StringBuilder sbMulcalType = new StringBuilder();
					if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))){
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
					
					int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
					
					if(index>=0){
						alE.remove(index);
						alE.add(index, alInner);
					}else{
						alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
						alE.add(alInner);
					}			
				}
				rs.close();
				pst.close();
			}
			
			if(getEffectiveDate() == null || getEffectiveDate().equals("")){
				setEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
			}
			request.setAttribute("reportList", alE);
			request.setAttribute("nEarningCnt", ""+nEarningCnt);
			
			boolean displayFlag = false;
			if(nDisplay == 0) {
				displayFlag = true;
			}
			request.setAttribute("displayFlag", ""+displayFlag);
			
	//		request.setAttribute("effectiveDate", effectiveDate);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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
			Map<String, Map<String, String>> hmCandiInfo = getCandiInfoMap(con, false);
	
			CandidateMyProfilePopup candidateMyProfilePopup = new CandidateMyProfilePopup();
			candidateMyProfilePopup.request = request;
			candidateMyProfilePopup.session = session;
			candidateMyProfilePopup.CF = CF;
			candidateMyProfilePopup.setCandID(getCandidateID());
			candidateMyProfilePopup.setRecruitId(getRecruitId());
			
//			StringBuilder sbCandiSalTable = candidateMyProfilePopup.getCandiSalaryDetails(con);
//			if(sbCandiSalTable == null) sbCandiSalTable = new StringBuilder();
			
			Map<String, String> hmCandiInner = hmCandiInfo.get(getCandidateID());
			
			pst = con.prepareStatement("select e.candi_application_deatils_id,e.candidate_id,r.org_id,d.designation_code,d.designation_name from recruitment_details r, designation_details d, " +
					"candidate_application_details e where r.recruitment_id = e.recruitment_id and r.designation_id=d.designation_id and candidate_id = ?");
			pst.setInt(1, uF.parseToInt(getCandidateID()));
			rst = pst.executeQuery();
	//		System.out.println("new Date ===> " + new Date());
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
	//		System.out.println("new Date ===> " + new Date());
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
			pst.setInt(1, uF.parseToInt(hmCandiOrg.get(getCandidateID())));
			rst = pst.executeQuery();
	//		System.out.println("new Date ===> " + new Date());
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
	//		nF.setStrContextPath(request.getServletPath());
			nF.setStrContextPath(request.getContextPath());
			 
			nF.setStrCandiFname(hmCandiInner.get("FNAME"));
			nF.setStrCandiLname(hmCandiInner.get("LNAME"));
			nF.setStrCandiCTC(hmCandiInner.get("OFFERED_CTC"));
			nF.setStrCandiAnnualCTC(hmCandiInner.get("OFFERED_ANNUAL_CTC"));
			nF.setStrCandiJoiningDate(hmCandiInner.get("JOINING_DATE"));
			nF.setStrRecruitmentDesignation(hmCandiDesig.get(getCandidateID()));
			nF.setStrCandidateId(hmCandiInner.get("CANDI_ID"));//Created By Dattatray Date : 05-10-21
			
			//Started by Dattatray Date:29-09-21
			String name = uF.showData(nF.getStrCandiSalutation(), "")+" "+uF.showData(nF.getStrCandiFname(),"")+" "+uF.showData(nF.getStrCandiLname(),"");
			Map<String, String> hmCandiSalDetails = candidateMyProfilePopup.getCandiSalaryDetails(con, name, nF.getStrRecruitmentDesignation(), nF.getStrRecruitmentWLocation(), nF.getStrCandiJoiningDate(), nF.getStrRecruitmentLevel(), nF.getStrRecruitmentGrade(),nF.getStrLegalEntityName());;
			nF.setStrOfferedSalaryStructure(hmCandiSalDetails.get("OFFERED_SALARY_STRUCTURE"));
			//Ended by Dattatray Date:29-09-21
			
			nF.setOfferAcceptData("?candidateID="+getCandidateID()+"&recruitID="+getRecruitId()+"&candiOfferAccept=yes&updateRemark=Update");
			
			Map<String, String> hmParsedContent = null;
	
	//		Document document = new Document(PageSize.A4);
			Document document = new Document(PageSize.A4,40, 40, 10, 60); 
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			StringBuilder sbHeader = new StringBuilder();
			StringBuilder sbFooter = new StringBuilder();
			String strDocName = null;
			String strDocContent = null;
			if(strDocumentContent!=null){
				
	//			hmParsedContent  = nF.parseContent(strDocumentContent, "", "");
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
	//				headerPath=CF.getStrDocRetriveLocation()+strHeader;
					if(CF.getStrDocRetriveLocation()==null) { 
						headerPath =  DOCUMENT_LOCATION + strHeader;
					} else { 
						headerPath = CF.getStrDocSaveLocation() +I_COLLATERAL+"/"+I_IMAGE+"/"+strHeader;
					}
				}
				
				
	//			if(headerPath != null && !headerPath.equals("")) {
	//				//sbHeader.append("<table><tr><td><img height=\"60\" src=\""+strDocumentHeader+"\"></td></tr></table>");
	//				sbHeader.append("<table style=\"width: 100%;\"><tr>");
	//				if(strHeaderImageAlign!=null && strHeaderImageAlign.equals("R")) { 
	//					sbHeader.append("<td width=\"70%\" valign=\"middle\" style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>" +
	//							"<td align=\"right\">");
	//					if(headerPath != null && !headerPath.equals("")) {
	//						sbHeader.append("<img height=\"30\" src=\""+headerPath+"\">");
	//					}
	//					sbHeader.append("</td>");						
	//				
	//				} else if(strHeaderImageAlign !=null && strHeaderImageAlign.equals("C")) { 
	//					sbHeader.append("<td colspan=\"2\" align=\"Center\">");
	//					if(headerPath != null && !headerPath.equals("")) {
	//						sbHeader.append("<img height=\"30\" src=\""+headerPath+"\"><br/>");
	//					}
	//					sbHeader.append(""+strHeaderCollateralText+"</td>");
	//				} else {
	//					sbHeader.append("<td>");
	//					if(headerPath != null && !headerPath.equals("")) {
	//						sbHeader.append("<img height=\"30\" src=\""+headerPath+"\">");
	//					}
	//					sbHeader.append("</td> <td valign=\"middle\" style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>");
	//				}
	//				sbHeader.append("</tr></table>");
	//				
	//			} else {
	//				
	//				sbHeader.append("<table style=\"width: 100%;\"><tr>");
	//				if(strHeaderTextAlign!=null && strHeaderTextAlign.equals("R")) { 
	//					sbHeader.append("<td colspan=\"2\" align=\"right\" valign=\"middle\" style=\"padding-right: 50px;\">"+strHeaderCollateralText+"</td>");
	//					
	//				} else if(strHeaderTextAlign!=null && strHeaderTextAlign.equals("C")) { 
	//					sbHeader.append("<td colspan=\"2\" align=\"center\" valign=\"middle\">"+strHeaderCollateralText+"</td>");
	//				} else { 
	//					sbHeader.append("<td colspan=\"2\" valign=\"middle\" style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>");
	//				}
	//				sbHeader.append("</tr></table>");
	//			}
				if(headerPath != null && !headerPath.equals("")) {
					sbHeader.append("<table style=\"width: 100%;\"><tr>");
					sbHeader.append("<td>");
					if(strHeader!=null && !strHeader.equals("")) {
						sbHeader.append("<img src=\""+headerPath+"\">");
					}
					sbHeader.append("</td>");	
					sbHeader.append("</tr></table>");
				}
				
				
	//			String footerPath="";   
	//			if(strFooter!=null && !strFooter.equals("")){
	////				footerPath=CF.getStrDocRetriveLocation()+strFooter;
	//				if(CF.getStrDocRetriveLocation()==null) { 
	//					footerPath =  DOCUMENT_LOCATION + strFooter;
	//				} else { 
	//					footerPath = CF.getStrDocRetriveLocation() +I_COLLATERAL+"/"+I_IMAGE+"/"+strFooter;
	//				}
	//			}
	//			
	//			
	//			if(footerPath != null && !footerPath.equals("")) {
	//				//sbFooter.append("<table><tr><td><img height=\"60\" src=\""+strDocumentFooter+"\"></td></tr></table>");
	//				
	//				sbFooter.append("<table><tr>");
	//				if(strFooterImageAlign!=null && strFooterImageAlign.equals("R")) { 
	//					sbFooter.append("<td width=\"70%\" valign=\"middle\" style=\"padding-left: 50px;\">"+strFooterCollateralText+"</td> <td align=\"right\">");
	//					if(footerPath != null && !footerPath.equals("")) {
	//						sbFooter.append("<img height=\"60\" src=\""+footerPath+"\">");
	//					}
	//					sbFooter.append("</td>");						
	//				
	//				} else if(strFooterImageAlign!=null && strFooterImageAlign.equals("C")) { 
	//					sbFooter.append("<td align=\"Center\">");
	//					if(footerPath != null && !footerPath.equals("")) {
	//						sbFooter.append("<img height=\"60\" src=\""+footerPath+"\"><br/>");
	//					}
	//					sbFooter.append(""+strFooterCollateralText+"</td>");
	//				} else { 
	//					sbFooter.append("<td>");
	//					if(footerPath != null && !footerPath.equals("")) {
	//						sbFooter.append("<img height=\"60\" src=\""+footerPath+"\">");
	//					}
	//					sbFooter.append("</td> <td valign=\"middle\" style=\"padding-left: 50px;\">"+strFooterCollateralText+"</td>");
	//				}
	//				sbFooter.append("</tr></table>");
	//			} else {
	//
	//				sbFooter.append("<table><tr>");
	//				if(strFooterTextAlign!=null && strFooterTextAlign.equals("R")) { 
	//					sbFooter.append("<td colspan=\"2\" align=\"right\" valign=\"middle\" style=\"padding-right: 50px;\">"+strFooterCollateralText+"</td>");
	//				
	//				} else if(strFooterTextAlign!=null && strFooterTextAlign.equals("C")) { 
	//					sbFooter.append("<td colspan=\"2\" align=\"center\" valign=\"middle\">"+strFooterCollateralText+"</td>");
	//				} else { 
	//					sbFooter.append("<td colspan=\"2\" valign=\"middle\" style=\"padding-right: 50px;\">"+strFooterCollateralText+"</td>");
	//				}
	//				sbFooter.append("</tr></table>");
	//			
	//			}
				
				PdfWriter writer = PdfWriter.getInstance(document, buffer);
				HeaderFooterPageEvent event = new HeaderFooterPageEvent(sbHeader.toString(),strFooterCollateralText);
			    writer.setPageEvent(event);
				document.open();
				
				HTMLWorker hw = new HTMLWorker(document);
	//			hw.parse(new StringReader(sbHeader.toString())); 
				hw.parse(new StringReader(strDocument));
	//			hw.parse(new StringReader(sbFooter.toString()));
				document.close();  
			
			}
		
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
			pst.setInt(6, uF.parseToInt(getCandidateID()));
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


	public Map<String, Map<String, String>> getCandiInfoMap(Connection con, boolean isFamilyInfo) {
		Map<String, Map<String, String>> hmCandiInfo = new HashMap<String, Map<String, String>>();
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			Map<String, String> hmCandiInner = new HashMap<String, String>();
			if(isFamilyInfo) {
				pst = con.prepareStatement("select * from candidate_family_members order by emp_id");
				rs = pst.executeQuery();
				while(rs.next()) {
					hmCandiInner = hmCandiInfo.get(rs.getString("emp_id"));
					if(hmCandiInner==null)hmCandiInner=new HashMap<String, String>();
					
					hmCandiInner.put(rs.getString("member_type"), rs.getString("member_name"));
					hmCandiInfo.put(rs.getString("emp_id"), hmCandiInner);
				}
				rs.close();
				pst.close();
			}
			
			pst = con.prepareStatement("SELECT cpd.emp_per_id, cpd.emp_fname, cpd.emp_lname, cpd.empcode, cpd.emp_image, cpd.emp_email, " +
					"cpd.emp_date_of_birth, cad.candidate_joining_date, cpd.emp_gender, cpd.marital_status, cad.ctc_offered, cad.annual_ctc_offered FROM " +
					"candidate_personal_details cpd, candidate_application_details cad where cpd.emp_per_id = cad.candidate_id order by emp_per_id");
			rs = pst.executeQuery();
	//		System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				if (rs.getInt("emp_per_id") < 0) {
					continue;
				}
				hmCandiInner = hmCandiInfo.get(rs.getString("emp_per_id"));
				if(hmCandiInner==null)hmCandiInner=new HashMap<String, String>();
	
				hmCandiInner.put("FNAME", rs.getString("emp_fname"));
				hmCandiInner.put("LNAME", rs.getString("emp_lname"));
				hmCandiInner.put("FULLNAME", rs.getString("emp_lname")+" "+rs.getString("emp_lname"));
				hmCandiInner.put("EMPCODE", rs.getString("empcode"));
				hmCandiInner.put("IMAGE", rs.getString("emp_image"));
				hmCandiInner.put("EMAIL", rs.getString("emp_email"));
				hmCandiInner.put("DOB", rs.getString("emp_date_of_birth"));
				if(rs.getString("candidate_joining_date") != null) {
				hmCandiInner.put("JOINING_DATE", uF.getDateFormat(rs.getString("candidate_joining_date"), DBDATE, CF.getStrReportDateFormat()));
				} else {
					hmCandiInner.put("JOINING_DATE", "-");
				}
				hmCandiInner.put("GENDER", rs.getString("emp_gender"));
				hmCandiInner.put("MARITAL_STATUS", rs.getString("marital_status"));
				hmCandiInner.put("OFFERED_CTC", rs.getString("ctc_offered"));
				hmCandiInner.put("OFFERED_ANNUAL_CTC", rs.getString("annual_ctc_offered"));
				
				hmCandiInfo.put(rs.getString("emp_per_id"), hmCandiInner);
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
		return hmCandiInfo;
	}

	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
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


	public String[] getIsDisplay() {
		return isDisplay;
	}


	public void setIsDisplay(String[] isDisplay) {
		this.isDisplay = isDisplay;
	}


	public String[] getEmp_salary_id() {
		return emp_salary_id;
	}


	public void setEmp_salary_id(String[] emp_salary_id) {
		this.emp_salary_id = emp_salary_id;
	}


	public String[] getSalary_type() {
		return salary_type;
	}


	public void setSalary_type(String[] salary_type) {
		this.salary_type = salary_type;
	}


	public String getCCID() {
		return CCID;
	}


	public void setCCID(String cCID) {
		CCID = cCID;
	}


	public String getStrUserType() {
		return strUserType;
	}


	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getCandidateID() {
		return candidateID;
	}

	public void setCandidateID(String candidateID) {
		this.candidateID = candidateID;
	}
	
	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getTableMode() {
		return tableMode;
	}

	public void setTableMode(String tableMode) {
		this.tableMode = tableMode;
	}

	public String getRenegotiateRemark() {
		return renegotiateRemark;
	}

	public void setRenegotiateRemark(String renegotiateRemark) {
		this.renegotiateRemark = renegotiateRemark;
	}
	
	public boolean getDisableSalaryStructure() {
		return disableSalaryStructure;
	}

	public void setDisableSalaryStructure(boolean disableSalaryStructure) {
		this.disableSalaryStructure = disableSalaryStructure;
	}

	public String getCandiApplicationId() {
		return candiApplicationId;
	}

	public void setCandiApplicationId(String candiApplicationId) {
		this.candiApplicationId = candiApplicationId;
	}

	public String getCtcAmt() {
		return ctcAmt;
	}

	public void setCtcAmt(String ctcAmt) {
		this.ctcAmt = ctcAmt;
	}
	
}