package com.konnect.jpms.challan;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.export.payroll.ExcelSheetDesign;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ESICUpdateChallanData extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	
	String financialYear;
	String strEmpId;
	String[] strMonth;
	String totalMonths;
	String challanType;
	String strSubmit;
	String[] empIds;
	int totalTaxAmount;
	int totalNumOfEmployee;
	//double totalAmount;
	String paidDate;
	 String challanDate;
	 String challanNum;
	 String cheque_no;
	 String operation;
	 double totalContribution;
	double employerContribution;
	double employeeContribution;
//	String strFinancialYearStart;
	String sessionEmp_Id;
	String f_org;
	String bankName;
	String bankBranch;
	String paymentMode;
	
	String navigationId;
	String toPage;
	
	String f_strWLocation;
	String sbEmp;
	

	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		sessionEmp_Id=(String) session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("getOperation()=====>"+getOperation());
		if(getChallanDate()!=null && getOperation()!=null && getOperation().equalsIgnoreCase("del")){
			deleteChallanDetails(uF);
		}else if(getChallanDate()!=null && getChallanNum()!=null && getOperation().equalsIgnoreCase("update")){
			updateChallanNumber(uF);
		}else if(getOperation()!=null && getOperation().equalsIgnoreCase("insert")){
			insertUnpaidAmount(uF);
		}else if(getOperation()!=null && getOperation().equalsIgnoreCase("pdf")){
			viewESICChallanPdfReports(uF);
			generateNewESICChallanPdfReports(uF);
		}else if(getOperation()!=null && getOperation().equalsIgnoreCase("excel")){
			viewESICData(uF);
			generateESICExcel(uF);
		}
		
			return SUCCESS;
	}
	
private void generateESICExcel(UtilityFunctions uF) {
		
		try {

			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}
			Map<String,String> hmOrg = (Map<String, String>)request.getAttribute("hmOrg"); 
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			Map<String,String> hmEmployeeMap = (Map<String, String>)request.getAttribute("hmEmployeeMap"); 
			if(hmEmployeeMap == null) hmEmployeeMap = new LinkedHashMap<String, String>();
			Map<String,String> hmEmpEndDate = (Map<String, String>)request.getAttribute("hmEmpEndDate"); 
			if(hmEmpEndDate == null) hmEmpEndDate = new HashMap<String, String>();
			Map<String,String> hmEmpPaidDays = (Map<String, String>)request.getAttribute("hmEmpPaidDays"); 
			if(hmEmpPaidDays == null) hmEmpPaidDays = new HashMap<String, String>();
			Map<String,String> hmEmpStatus = (Map<String, String>)request.getAttribute("hmEmpStatus"); 
			if(hmEmpStatus == null) hmEmpStatus = new HashMap<String, String>();
			Map<String,String> hmEmpESICACNO = (Map<String, String>)request.getAttribute("hmEmpESICACNO"); 
			if(hmEmpESICACNO == null) hmEmpESICACNO = new HashMap<String, String>();
			Map<String, Map<String, String>> hmESICMap = (Map<String, Map<String, String>>)request.getAttribute("hmESICMap"); 
			if(hmESICMap == null) hmESICMap = new HashMap<String, Map<String, String>>(); 
			List<String> empLeaveList = (List<String>)request.getAttribute("empLeaveList");
			if(empLeaveList == null) empLeaveList = new ArrayList<String>();

			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("ESIC Format");			
			
			List<DataStyle> header=new ArrayList<DataStyle>();
			header.add(new DataStyle("IP Number (10 Digits)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("IP Name (Only alphabets and space)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("No of Days for which wages paid/payable during the month",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Total Monthly Wages",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Reason Code for Zero workings days(numeric only; provide 0 for all other reasons- Click on the link for reference)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Last Working Day(Format DD/MM/YYYY  or DD-MM-YYYY)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>(); 
			Iterator<String> it = hmEmployeeMap.keySet().iterator();
			while (it.hasNext()){
				String strEmpId = it.next();
				String strEmpName = hmEmployeeMap.get(strEmpId);
				
				Map<String,String> hmESICData = hmESICMap.get(strEmpId);
				if(hmESICData == null) hmESICData =new HashMap<String, String>();
				
				List<DataStyle> innerList=new ArrayList<DataStyle>();
				innerList.add(new DataStyle(uF.showData(hmEmpESICACNO.get(strEmpId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(strEmpName,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.showData(hmEmpPaidDays.get(strEmpId), "0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.showData(hmESICData.get("ESI_MAX_LIMIT"), "0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				String strReasoncode = "0";
				if(hmEmpStatus.get(strEmpId)!=null && hmEmpStatus.get(strEmpId).equalsIgnoreCase("TERMINATED")){
					strReasoncode = "8";
				} else if(hmEmpStatus.get(strEmpId)!=null && hmEmpStatus.get(strEmpId).equalsIgnoreCase(RETIRED)){
					strReasoncode = "3";
				} else if(hmEmpStatus.get(strEmpId)!=null && hmEmpStatus.get(strEmpId).equalsIgnoreCase("RESIGNED")){
					strReasoncode = "2";
				} else if (empLeaveList.contains(strEmpId)) {
					strReasoncode = "1";
				}
				innerList.add(new DataStyle(strReasoncode,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.showData(hmEmpEndDate.get(strEmpId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));

				reportData.add(innerList);
			}
			
			
			ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
			sheetDesign.generateDefualtExcelSheet(workbook,sheet,header,reportData);
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=ESIC_Excel.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
}
	private void viewESICData(UtilityFunctions uF) {
		
//		System.out.println(" viewESICData");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String[] strPayCycleDates = null;
		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;
		
		try{
			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {
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
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			
			pst = con.prepareStatement("select * from org_details where org_id = ? ");
			pst.setInt(1, uF.parseToInt(getF_org()));
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
				hmOrg.put("ORG_ESTABLISH_CODE", rs.getString("establish_code_no"));
			} 
	        rs.close();
	        pst.close(); 
			
			/*pst = con.prepareStatement("select distinct(month) from challan_details where entry_date=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") " +
					"and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?) " +
					"and financial_year_from_date=? and financial_year_to_date=? ");
*/			
			StringBuilder sb=new StringBuilder();
			sb.append(" select distinct(month) from challan_details where entry_date=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") " +
					"and is_paid=?");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			sb.append(" and financial_year_from_date=? and financial_year_to_date=?");
			pst=con.prepareStatement(sb.toString());
			
			pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setBoolean(2, false);
		//	pst.setInt(3, uF.parseToInt(getF_org()));
		//	pst.setInt(4, uF.parseToInt(getF_strWLocation()));
			pst.setDate(3,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println(" pst 1 in viewESICData==>"+pst);
			
			rs = pst.executeQuery();
//			System.out.println("pst===>"+pst);
			Set<String> monthList = new HashSet<String>();
			while (rs.next()) {
				String[] monthsArray = rs.getString("month").split(",");
				for (int i = 0; i < monthsArray.length; i++) {
						monthList.add(monthsArray[i]);
				}
			}
	        rs.close();
	        pst.close();

			String month = "";
			int i=0;
			for(String mnths : monthList){
				if(i == 0){
					month += mnths;
				} else {
					month += ","+mnths;
				}
			}
			
			/*pst = con.prepareStatement("select emp_id, sum(esi_max_limit) as esi_max_limit, sum(eesi_contribution) as eesi_contribution, " +
					"sum(ersi_contribution) as ersi_contribution  from emp_esi_details where _month in ("+month+") and emp_id in (select distinct(emp_id) as emp_id " +
					"from challan_details where entry_date=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") and is_paid=? " +
					"and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?) and financial_year_from_date=? " +
					"and financial_year_to_date=?) and financial_year_start=? and financial_year_end=? group by emp_id");
*/
			sb=new StringBuilder();
			sb.append("select emp_id, sum(esi_max_limit) as esi_max_limit, sum(eesi_contribution) as eesi_contribution, " +
					"sum(ersi_contribution) as ersi_contribution  from emp_esi_details where _month in ("+month+") and emp_id in (select distinct(emp_id) as emp_id " +
					"from challan_details where entry_date=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") and is_paid=? ");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			sb.append(" and financial_year_from_date=? and financial_year_to_date=?) and financial_year_start=? and financial_year_end=? group by emp_id");
			pst=con.prepareStatement(sb.toString());
			
			pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setBoolean(2, false);
			//pst.setInt(3, uF.parseToInt(getF_org()));
			//.setInt(4, uF.parseToInt(getF_strWLocation()));
			pst.setDate(3,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(5,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(6,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst new=====>"+pst);
//			System.out.println(" pst 2 in viewESICData==>"+pst);
			
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmESICMap = new HashMap<String, Map<String, String>>();
			Set<String> empSetList = new HashSet<String>();
			while (rs.next()) {
				Map<String,String> hmESICData = hmESICMap.get(rs.getString("emp_id"));
				if(hmESICData == null) hmESICData =new HashMap<String, String>();
				
				hmESICData.put("ESI_MAX_LIMIT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("esi_max_limit"))) );
				hmESICData.put("EMPLOYEE_CONTRIBUTION", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("eesi_contribution"))) );
				hmESICData.put("EMPLOYER_CONTRIBUTION", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("ersi_contribution"))) );
				
				hmESICMap.put(rs.getString("emp_id"), hmESICData);
				
				empSetList.add(rs.getString("emp_id"));
			}
	        rs.close();
	        pst.close();

			Map<String, String> hmEmployeeMap = new LinkedHashMap<String, String>();
			Map<String, String> hmEmpESICACNO = new HashMap<String, String>();
			Map<String, String> hmEmpDOB = new HashMap<String, String>();
			Map<String, String> hmEmpGender = new HashMap<String, String>();
			Map<String, String> hmEmpJoiningDate = new HashMap<String, String>();
			Map<String, String> hmEmpEndDate = new HashMap<String, String>(); 
			Map<String, String> hmEmpStatus = new HashMap<String, String>();
			/*pst = con.prepareStatement("SELECT * FROM employee_official_details eod, employee_personal_details epd WHERE epd.emp_per_id=eod.emp_id " +
					"and eod.emp_id in(select distinct(emp_id) as emp_id from challan_details where entry_date=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") and is_paid=? " +
					"and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?) and financial_year_from_date=? " +
					"and financial_year_to_date=?) order by emp_fname");
*/			
			sb=new StringBuilder();
			sb.append(" SELECT * FROM employee_official_details eod, employee_personal_details epd WHERE epd.emp_per_id=eod.emp_id " +
					"and eod.emp_id in(select distinct(emp_id) as emp_id from challan_details where entry_date=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") and is_paid=?");
			
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			sb.append(" and financial_year_from_date=? " +
					"and financial_year_to_date=?) order by emp_fname");
			pst=con.prepareStatement(sb.toString());
			
			pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setBoolean(2, false);
			//pst.setInt(3, uF.parseToInt(getF_org()));
			//pst.setInt(4, uF.parseToInt(getF_strWLocation()));
			pst.setDate(3,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println(" pst 3 in viewESICData==>"+pst);

			rs = pst.executeQuery();
			while (rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmployeeMap.put(rs.getString("emp_id"), rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				hmEmpESICACNO.put(rs.getString("emp_id"), rs.getString("emp_esic_no"));
				hmEmpDOB.put(rs.getString("emp_id"), uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT).equals("-") ? "" : uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT));
				hmEmpGender.put(rs.getString("emp_id"), rs.getString("emp_gender"));
				hmEmpJoiningDate.put(rs.getString("emp_id"), uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT).equals("-") ? "" : uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				hmEmpEndDate.put(rs.getString("emp_id"), uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT).equals("-") ? "" : uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT));
				hmEmpStatus.put(rs.getString("emp_id"), rs.getString("emp_status"));
			}
	        rs.close();
	        pst.close();
			
			/*pst = con.prepareStatement("select emp_id,paid_days from payroll_generation where emp_id in(select distinct(emp_id) as emp_id " +
					"from challan_details where entry_date=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") and is_paid=? " +
					"and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?) and financial_year_from_date=? " +
					"and financial_year_to_date=?)and financial_year_from_date=? and financial_year_to_date=? and month in ("+month+") " +
					"group by emp_id,paid_days order by emp_id,paid_days");
*/			
			
	        sb=new StringBuilder();
	        sb.append("select emp_id,paid_days from payroll_generation where emp_id in(select distinct(emp_id) as emp_id " +
					"from challan_details where entry_date=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") and is_paid=?");
	        
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			sb.append(" and financial_year_from_date=? " +
					"and financial_year_to_date=?)and financial_year_from_date=? and financial_year_to_date=? and month in ("+month+") " +
					"group by emp_id,paid_days order by emp_id,paid_days");
			
			pst=con.prepareStatement(sb.toString());
			pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setBoolean(2, false);
			//pst.setInt(3, uF.parseToInt(getF_org()));
			//pst.setInt(4, uF.parseToInt(getF_strWLocation()));
			pst.setDate(3,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
//			System.out.println(" pst 4 in viewESICData==>"+pst);

			rs = pst.executeQuery();
			Map<String, String> hmEmpPaidDays = new HashMap<String, String>();
			while(rs.next()){
				double strEmpPaidDays = uF.parseToDouble(rs.getString("paid_days"));
				hmEmpPaidDays.put(rs.getString("emp_id"), ""+((int)Math.ceil(strEmpPaidDays)));
			}
	        rs.close();
	        pst.close();
			
			/*pst = con.prepareStatement("select min(paid_from) as paid_from,max(paid_to) as paid_to from payroll_generation where " +
					"emp_id in(select distinct(emp_id) as emp_id from challan_details where entry_date=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") " +
					"and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?) and financial_year_from_date=? " +
					"and financial_year_to_date=?)and financial_year_from_date=? and financial_year_to_date=? and month in ("+month+")");
*/			
			sb=new StringBuilder();
			sb.append(" select min(paid_from) as paid_from,max(paid_to) as paid_to from payroll_generation where " +
					"emp_id in(select distinct(emp_id) as emp_id from challan_details where entry_date=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") " +
					"and is_paid=?");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			sb.append(" and financial_year_from_date=? " +
					"and financial_year_to_date=?)and financial_year_from_date=? and financial_year_to_date=? and month in ("+month+")");
	        pst=con.prepareStatement(sb.toString());
	        
			pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setBoolean(2, false);
			//pst.setInt(3, uF.parseToInt(getF_org()));
			//pst.setInt(4, uF.parseToInt(getF_strWLocation()));
			pst.setDate(3,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
//			System.out.println(" pst 5 in viewESICData==>"+pst);

			rs = pst.executeQuery();
			String strStartDate = null;
			String strEndDate = null;
			while(rs.next()){
				strStartDate = uF.getDateFormat(rs.getString("paid_from"), DBDATE, DATE_FORMAT);
				strEndDate = uF.getDateFormat(rs.getString("paid_to"), DBDATE, DATE_FORMAT);
			}
	        rs.close();
	        pst.close();
			
			List<String> empLeaveList = new ArrayList<String>();
			/*pst = con.prepareStatement("select emp_id from leave_application_register where _date between ? and ? " +
					"and emp_id in(select distinct(emp_id) as emp_id from challan_details where entry_date=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") " +
					"and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?) and financial_year_from_date=?" +
					" and financial_year_to_date=?) group by emp_id");
*/			
			sb=new StringBuilder();
			sb.append("select emp_id from leave_application_register where _date between ? and ? " +
					"and emp_id in(select distinct(emp_id) as emp_id from challan_details where entry_date=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") " +
					"and is_paid=?");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			sb.append(" and financial_year_from_date=?" +
					" and financial_year_to_date=?) group by emp_id");
			pst=con.prepareStatement(sb.toString());
			
			pst.setDate(1, uF.getDateFormat(strStartDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strEndDate, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setBoolean(4, false);
			//pst.setInt(5, uF.parseToInt(getF_org()));
			//pst.setInt(6, uF.parseToInt(getF_strWLocation()));
			pst.setDate(5,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(6,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst); 
//			System.out.println(" pst 6 in viewESICData==>"+pst);

			rs = pst.executeQuery();
			while (rs.next()){
				empLeaveList.add(rs.getString("emp_id"));
			}
	        rs.close();
	        pst.close();
			
			request.setAttribute("hmOrg", hmOrg);
			request.setAttribute("hmESICMap", hmESICMap); 
			request.setAttribute("hmEmployeeMap", hmEmployeeMap);
			request.setAttribute("hmEmpEndDate", hmEmpEndDate);
			request.setAttribute("hmEmpPaidDays", hmEmpPaidDays);
			request.setAttribute("hmEmpESICACNO", hmEmpESICACNO);
			request.setAttribute("hmEmpStatus", hmEmpStatus);
			request.setAttribute("empLeaveList", empLeaveList);
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	private void generateNewESICChallanPdfReports(UtilityFunctions uF) {
		
		try {
			
			List<Integer> alList=(List<Integer>)request.getAttribute("alList");
			Map<String,String> hmempcnt =(Map<String,String> )request.getAttribute("hmempcnt");
			Map<Integer,Map<String,String>> hmMap=(Map<Integer,Map<String,String>>)request.getAttribute("hmMap");
			if(hmMap==null)hmMap=new HashMap<Integer, Map<String,String>>();
			
			Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			Map<String, String> hmESIC = (Map<String, String>) request.getAttribute("hmESIC");
			if(hmESIC == null) hmESIC = new HashMap<String, String>();
			
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
	        
	        PdfPTable table = new PdfPTable(10);
			table.setWidthPercentage(100);        
	        
	        //New Row
			//Bank Copy
	        PdfPCell row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        
	        row1 =new PdfPCell(new Paragraph("Bank Copy\n\nEmployee's State Insurance Corporation\nState Bank of India",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
		    row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.RIGHT);        
		    row1.setPadding(2.5f);
		    table.addCell(row1);
		    
		    //Depositors copy
		    row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT );
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	       
	        row1 =new PdfPCell(new Paragraph("Depositors Copy\n\nEmployee's State Insurance Corporation\nState Bank of India",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
		    row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER);     
		    row1.setPadding(2.5f);
		    table.addCell(row1);
		    
		    //New Row
		    row1 =new PdfPCell(new Paragraph("(CHALLAN CAN BE SUBMITTED AT ANY SBI BRANCH)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);        
	       
	        row1 =new PdfPCell(new Paragraph("USE CBS SCREEN NO.8888\n\nFee Type 56",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("(CHALLAN CAN BE SUBMITTED AT ANY SBI BRANCH)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);        
	       
	        row1 =new PdfPCell(new Paragraph("USE CBS SCREEN NO.8888\n\nFee Type 56",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        //New Row
		    row1 =new PdfPCell(new Paragraph("Challan No.(Registration ID/Ref. No. in SBI CBS):",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setColspan(5);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Challan No.(Registration ID/Ref. No. in SBI CBS):",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setColspan(5);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        //New Row
		    row1 =new PdfPCell(new Paragraph("Party Code:  "+uF.showData(hmOrg.get("ORG_ESTABLISH_CODE"), ""),small)); 
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Challan Date:",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.getDateFormat(getChallanDate(),DBDATE,CF.getStrReportDateFormat()),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      
		    row1 =new PdfPCell(new Paragraph("Party Code:  "+uF.showData(hmOrg.get("ORG_ESTABLISH_CODE"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Challan Date:",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.getDateFormat(getChallanDate(),DBDATE,CF.getStrReportDateFormat()),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
		    row1 =new PdfPCell(new Paragraph("Name of Factory/Estt./Party:",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_NAME"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Name of Factory/Estt./Party:",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_NAME"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
		    row1 =new PdfPCell(new Paragraph("Address:",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_ADDRESS"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Address:",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_ADDRESS"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
		    row1 =new PdfPCell(new Paragraph("Mobile No:",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Mobile No:",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
		    row1 =new PdfPCell(new Paragraph("Mode of Payment: "+uF.showData(hmESIC.get("CHALLAN_MODE_DEPOSIT"),""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT | Rectangle.TOP);
	        row1.setColspan(5);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Mode of Payment: "+uF.showData(hmESIC.get("CHALLAN_MODE_DEPOSIT"),""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT | Rectangle.TOP);
	        row1.setColspan(5);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
		    row1 =new PdfPCell(new Paragraph("Cheque/DD/Ref No.: "+uF.showData(hmESIC.get("CHALLAN_CHEQUE_NO"),""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Dated : "+uF.showData(hmESIC.get("CHALLAN_PAID_DATE"),""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT | Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Cheque/DD/Ref No.: "+uF.showData(hmESIC.get("CHALLAN_CHEQUE_NO"),""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER | Rectangle.LEFT );
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Dated : "+uF.showData(hmESIC.get("CHALLAN_PAID_DATE"),""),small)); 
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Drawn on (Name of the Bank): "+uF.showData(hmESIC.get("CHALLAN_BANK_NAME"),"")+", "+uF.showData(hmESIC.get("CHALLAN_BRANCH_NAME"),""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.RIGHT);
	        row1.setColspan(5);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Drawn on (Name of the Bank): "+uF.showData(hmESIC.get("CHALLAN_BANK_NAME"),"")+", "+uF.showData(hmESIC.get("CHALLAN_BRANCH_NAME"),""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.LEFT);
	        row1.setColspan(5);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
		    row1 =new PdfPCell(new Paragraph("Remittance Details",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.TOP | Rectangle.RIGHT);
	        row1.setColspan(5);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Remittance Details",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.TOP | Rectangle.LEFT);
	        row1.setColspan(5);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
		    row1 =new PdfPCell(new Paragraph("Total",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
//	        row1 =new PdfPCell(new Paragraph(""+totalContribution,small));
	        row1 =new PdfPCell(new Paragraph(""+uF.formatIntoComma(totalContribution),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setColspan(4);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Type",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Amount",small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Periods",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
		    	        
	        row1 =new PdfPCell(new Paragraph("Total (in words) Rs. "+uF.digitsToWords((int)uF.parseToDouble(uF.formatIntoComma(totalContribution))),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.RIGHT);
	        row1.setColspan(5);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Employees Contribution",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+uF.formatIntoComma(employeeContribution),small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        
	      //New Row
		    row1 =new PdfPCell(new Paragraph("Denomination",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("1000 X      ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("=      ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Employers Contribution",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+uF.formatIntoComma(employerContribution),small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
		    row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("500 X      ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("=      ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
		    row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("100 X      ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("=      ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Total",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT|Rectangle.BOTTOM);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+uF.formatIntoComma(totalContribution),small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.BOTTOM);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        //New Row
		    row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("50 X      ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("=      ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Total(in words) Rs. ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.digitsToWords((int)uF.parseToDouble(uF.formatIntoComma(totalContribution))),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
		    row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("20 X      ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("=      ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
		    row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("10 X      ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("=      ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
		    row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("5 X      ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("=      ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
		    row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("2 X      ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("=      ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
		    row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("1 X      ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("=      ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
		    row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Total      ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("=      ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT|Rectangle.BOTTOM);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
		    row1 =new PdfPCell(new Paragraph("(For Bank's use)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	                
	        row1 =new PdfPCell(new Paragraph("_______________",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("(For Bank's use)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("_______________",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
		    row1 =new PdfPCell(new Paragraph("Deposited Date",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	                
	        row1 =new PdfPCell(new Paragraph("D D  M M  Y Y Y Y",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Deposited Date",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("D D  M M  Y Y Y Y",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        //New Row
		    row1 =new PdfPCell(new Paragraph("Journal No.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	                
	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Journal No.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
		    row1 =new PdfPCell(new Paragraph("Branch Stamp and Signature of Cashier\n\n\n\n",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setColspan(5);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	                
	        row1 =new PdfPCell(new Paragraph("Branch Stamp and Signature of Cashier\n\n\n\n",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setColspan(5);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
		    row1 =new PdfPCell(new Paragraph("Notes:\n1)No Charges/Commission to be charged from the depositor.\n2)Strike out the not applicable option.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM);
	        row1.setColspan(5);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	                
	        row1 =new PdfPCell(new Paragraph("Notes:\n1)No Charges/Commission to be charged from the depositor.\n2)Strike out the not applicable option.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
	        row1.setColspan(5);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        document.add(table);
	        
	        document.close();
	        
			String filename="ESICChallan_"+uF.getDateFormat(getChallanDate(),DBDATE, "MM")+"_"+uF.getDateFormat(getChallanDate(),DBDATE, "yyyy")+".pdf";
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename="+filename+"");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	
}
	public void deleteChallanDetails(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			con = db.makeConnection(con);
			/*pst = con.prepareStatement("delete from challan_details WHERE entry_date =? and is_paid=? " +
					" and challan_type in ("+EMPLOYER_ESI+","+EMPLOYEE_ESI+") and emp_id in (select emp_id " +
					"from employee_official_details where org_id=? and wlocation_id=?) ");
*/			
			
			StringBuilder sb=new StringBuilder();
			sb.append("delete from challan_details WHERE entry_date =? and is_paid=? " +
					" and challan_type in ("+EMPLOYER_ESI+","+EMPLOYEE_ESI+")");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			pst=con.prepareStatement(sb.toString());
			
			pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setBoolean(2,false);
			//pst.setInt(3, uF.parseToInt(getF_org()));
			//pst.setInt(4, uF.parseToInt(getF_strWLocation()));
//			System.out.println(" pst for delete challan==>"+pst);
			pst.executeUpdate();
	        pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		} 
		
	}
	public void updateChallanNumber(UtilityFunctions uF){
//		System.out.println("in updateChallanNumber");
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String[] strPayCycleDates = null;
		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;
		try{
			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {
				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			} else {
				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-"+ strPayCycleDates[1]);

				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];

			}
			
			con = db.makeConnection(con);
			/*pst = con.prepareStatement("UPDATE challan_details SET is_paid =?,paid_date=?,challan_no=?,cheque_no=?,added_by=?,bank_name=?," +
					"bank_branch=?,mode_tds_deposit=? WHERE financial_year_from_date=? and financial_year_to_date=? and entry_date =? " +
					"and challan_type in ("+EMPLOYER_ESI+","+EMPLOYEE_ESI+") and is_paid = false" +
					" and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?)");
		*/
			StringBuilder sb=new StringBuilder();
			sb.append("UPDATE challan_details SET is_paid =?,paid_date=?,challan_no=?,cheque_no=?,added_by=?,bank_name=?," +
					"bank_branch=?,mode_tds_deposit=? WHERE financial_year_from_date=? and financial_year_to_date=? and entry_date =? " +
					"and challan_type in ("+EMPLOYER_ESI+","+EMPLOYEE_ESI+") and is_paid = false");
			
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			pst=con.prepareStatement(sb.toString());
			
			pst.setBoolean(1,true);
			pst.setDate(2, uF.getDateFormat(getPaidDate(), DATE_FORMAT));
			pst.setString(3,getChallanNum());
			pst.setString(4,getCheque_no());
			pst.setInt(5,uF.parseToInt(sessionEmp_Id));
			pst.setString(6,getBankName());
			pst.setString(7,getBankBranch());
			pst.setString(8,getPaymentMode());
			pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(11, uF.getDateFormat(getChallanDate(), DBDATE));
			//pst.setInt(12, uF.parseToInt(getF_org()));
			//pst.setInt(13, uF.parseToInt(getF_strWLocation()));
//			System.out.println(" pst for update challan"+pst);
			pst.executeUpdate();
	        pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	public void insertUnpaidAmount(UtilityFunctions uF){
		
//		System.out.println("in insertUnpaidAmount function************");
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String[] strPayCycleDates = null;
		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;
		String months="";
		try{
			con = db.makeConnection(con);
			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {
				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			} else {
				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
				
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			}	
			
//			System.out.println("totalMonths=====>"+totalMonths);
			List<String> alMonthList = Arrays.asList(totalMonths.split(","));
			if(alMonthList == null) alMonthList = new ArrayList<String>();
//			System.out.println("getEmpIds==>"+getEmpIds());
			if(getEmpIds()!=null){
			for(int i=0; getEmpIds()!=null && i<getEmpIds().length;i++){
				double amount=0;
				for(int j=0;j<alMonthList.size();j++){
				
					pst = con.prepareStatement("select sum(ersi_contribution) as amount from emp_esi_details where financial_year_start=? and financial_year_end=? " +
							"and _month=? and emp_id=?");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3,uF.parseToInt(alMonthList.get(j)));
					pst.setInt(4,uF.parseToInt(getEmpIds()[i]));
	//				System.out.println("pst====>"+pst);
					rs = pst.executeQuery();
					while(rs.next()){
						amount=rs.getDouble("amount");
					}
			        rs.close();
			        pst.close();
						
					pst = con.prepareStatement("select * from challan_details where  financial_year_from_date=? and financial_year_to_date=? " +
							"and month like '%,"+alMonthList.get(j)+",%' and emp_id=? and challan_type=?");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3,uF.parseToInt(getEmpIds()[i]));
					pst.setInt(4,EMPLOYER_ESI);
//						System.out.println("insert pst er====>"+pst);
					rs = pst.executeQuery();
					boolean flag=false;
					while(rs.next()){
						flag = true;
					}
			        rs.close();
			        pst.close();
					
					if(amount > 0.0d && !flag){
						pst = con.prepareStatement("insert into challan_details(emp_id,amount,entry_date," +
								"is_print,financial_year_from_date,financial_year_to_date,month,challan_type) values(?,?,?,?,?,?,?,?)");
						pst.setInt(1,uF.parseToInt(getEmpIds()[i]));
						pst.setDouble(2,amount);
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setBoolean(4,true);
						pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setString(7,"," + alMonthList.get(j) + ","); 
						pst.setInt(8,EMPLOYER_ESI);
						pst.executeUpdate();
				        pst.close();
					}
				}
			}
		}	
		if(getEmpIds()!=null){
			for(int i=0;i<getEmpIds().length;i++){
				double amount=0;
				for(int j=0;j<alMonthList.size();j++){
					pst = con.prepareStatement("select sum(eesi_contribution) as amount from emp_esi_details where financial_year_start=? and financial_year_end=? " +
							"and  _month=? and emp_id=?");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3,uF.parseToInt(alMonthList.get(j)));
					pst.setInt(4,uF.parseToInt(getEmpIds()[i]));
//					System.out.println("pst 0 in insertUnpaidAmount==>"+pst);

					rs = pst.executeQuery();
					while(rs.next()){
						amount=rs.getDouble("amount");
					}
			        rs.close();
			        pst.close();
				
					pst = con.prepareStatement("select * from challan_details where  financial_year_from_date=? and financial_year_to_date=? " +
							"and month like '%,"+alMonthList.get(j)+",%' and emp_id=? and challan_type=?");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(getEmpIds()[i]));
					pst.setInt(4, EMPLOYEE_ESI);
//						System.out.println("insert pst====>"+pst);
//					System.out.println("pst 1 in insertUnpaidAmount==>"+pst);

					rs = pst.executeQuery();
					boolean flag=false;
					while(rs.next()){
						flag = true;
					}
			        rs.close();
			        pst.close();
					
					if(amount > 0.0d && !flag){
						pst = con.prepareStatement("insert into challan_details(emp_id,amount,entry_date," +
								"is_print,financial_year_from_date,financial_year_to_date,month,challan_type) values(?,?,?,?,?,?,?,?)");
						pst.setInt(1,uF.parseToInt(getEmpIds()[i]));
						pst.setDouble(2,amount);
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setBoolean(4,true);
						pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setString(7,"," + alMonthList.get(j) + ",");
						pst.setInt(8,EMPLOYEE_ESI);
//						System.out.println("pst 2 in insertUnpaidAmount==>"+pst);
						pst.executeUpdate();
				        pst.close();
					}
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
	
	public void viewESICChallanPdfReports(UtilityFunctions uF){
		
//		System.out.println("in viewESICChallanPdfReports");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String[] strPayCycleDates = null;
		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;
		
		try{
			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {
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
			
			pst = con.prepareStatement("select * from org_details where org_id = ? ");
			pst.setInt(1, uF.parseToInt(getF_org()));
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
				hmOrg.put("ORG_ESTABLISH_CODE", rs.getString("establish_code_no"));
			}
	        rs.close();
	        pst.close();  
			
			request.setAttribute("hmOrg", hmOrg);
			
		
			if (getChallanNum() != null) {
				/*pst = con.prepareStatement("select distinct(month) from challan_details where challan_no=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") " +
						"and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?) " +
						"and financial_year_from_date=? and financial_year_to_date=? ");
*/				
				StringBuilder sb=new StringBuilder();
				sb.append("select distinct(month) from challan_details where challan_no=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") " +
						"and is_paid=?");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
				sb.append(" and financial_year_from_date=? and financial_year_to_date=? ");
				pst=con.prepareStatement(sb.toString());
				
				pst.setString(1, getChallanNum());
				pst.setBoolean(2, true);
				//pst.setInt(3, uF.parseToInt(getF_org()));
				//pst.setInt(4, uF.parseToInt(getF_strWLocation()));
				pst.setDate(3,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				
//				System.out.println(" pst 1 in viewESICChallanPdfReports==>"+pst);
			} else {

				/*pst = con.prepareStatement("select distinct(month) from challan_details where entry_date=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") " +
						"and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?) " +
						"and financial_year_from_date=? and financial_year_to_date=? ");
*/				
				
				StringBuilder sb=new StringBuilder();
				sb.append("select distinct(month) from challan_details where entry_date=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") " +
						"and is_paid=?");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
				sb.append(" and financial_year_from_date=? and financial_year_to_date=?");
				pst=con.prepareStatement(sb.toString());
				
				pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
				pst.setBoolean(2, false);
				//pst.setInt(3, uF.parseToInt(getF_org()));
				//pst.setInt(4, uF.parseToInt(getF_strWLocation()));
				pst.setDate(3,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				
//				System.out.println(" pst 1 else in viewESICChallanPdfReports==>"+pst);

			}
			rs = pst.executeQuery();
//			System.out.println("pst * in viewESICChallanPdfReports ===>"+pst);
			List monthList = new ArrayList();
			while (rs.next()) {
				String[] monthsArray = rs.getString("month").split(",");
				for (int i = 0; i < monthsArray.length; i++) {
					if (!monthList.contains(monthsArray[i])) {
						monthList.add(monthsArray[i]);
					}
				}
			}
	        rs.close();
	        pst.close();

			String month = "";
			for (int i = 1; i < monthList.size(); i++) {
				if (i == monthList.size() - 1) {
					month += monthList.get(i);
				} else
					month += monthList.get(i) + ",";
			}
		
			totalNumOfEmployee=0;
		
			List empIdList = new ArrayList();
			if (getChallanNum() != null) {
				/*pst = con.prepareStatement("select distinct(emp_id) as emp_id,cheque_no from challan_details where challan_no=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") " +
						"and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?) " +
						"and financial_year_from_date=? and financial_year_to_date=?");
*/			
				StringBuilder sb=new StringBuilder();
				sb.append(" select distinct(emp_id) as emp_id,cheque_no from challan_details where challan_no=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") " +
						"and is_paid=?");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in(0)");
				}
				sb.append(" and financial_year_from_date=? and financial_year_to_date=?");
				pst=con.prepareStatement(sb.toString());
				
				pst.setString(1, getChallanNum());
				pst.setBoolean(2, true);
				//pst.setInt(3, uF.parseToInt(getF_org()));
				//pst.setInt(4, uF.parseToInt(getF_strWLocation()));
				pst.setDate(3,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				
//				System.out.println(" pst 2 in viewESICChallanPdfReports==>"+pst);

			} else {
				/*pst = con.prepareStatement("select distinct(emp_id) as emp_id,cheque_no from challan_details where entry_date=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") " +
						"and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?) " +
						"and financial_year_from_date=? and financial_year_to_date=?");
*/				
				StringBuilder sb=new StringBuilder();
				sb.append("select distinct(emp_id) as emp_id,cheque_no from challan_details where entry_date=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") " +
						"and is_paid=? ");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in(0)");
				}
				sb.append(" and financial_year_from_date=? and financial_year_to_date=?");
				pst=con.prepareStatement(sb.toString());
				
				pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
				pst.setBoolean(2, false);
				//pst.setInt(3, uF.parseToInt(getF_org()));
				//pst.setInt(4, uF.parseToInt(getF_strWLocation()));
				pst.setDate(3,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				
//				System.out.println(" pst 2 else in viewESICChallanPdfReports==>"+pst);	

			}
//			System.out.println(" pst *  in viewESICChallanPdfReports==>"+pst);
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			StringBuilder empid_sb=new StringBuilder(); 
			while (rs.next()) {
				totalNumOfEmployee = totalNumOfEmployee + 1;
				
				/*String[] arr = rs.getString("emp_id").split(",");
				
				for(int i=0; arr!=null && i<arr.length; i++){
					if(arr[i]!=null && arr[i].length()>0){
						empid_sb.append(arr[i]+",");
					}
				}*/
					setCheque_no(rs.getString("cheque_no"));
				
			}
	        rs.close();
	        pst.close();
			
	       // empid_sb.replace(0, empid_sb.length(), empid_sb.substring(0, empid_sb.length()-1));
			
//			pst = con.prepareStatement("select sum(amount) as amount from payroll_generation where financial_year_from_date=? and financial_year_to_date=? " +
//					"and month in ("+ month+ ") "+ "and earning_deduction=? and emp_id in("+ empid_sb + ")");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setString(3, "E");
////			System.out.println("pst new=====>"+pst);
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				totalAmount = rs.getDouble("amount");
//			}
//	        System.out.println("getChallanNum=====>"+getChallanNum());
	        Map<String, String> hmESIC = new HashMap<String, String>();
			if (getChallanNum() != null) {
				/*pst = con.prepareStatement("select * from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						"and challan_no=? and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?) " +
						"and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ")");
*/				
				StringBuilder sb=new StringBuilder();
				sb.append(" select * from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						"and challan_no=? and is_paid=?");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in(0)");
				}
				sb.append(" and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ")");
				pst=con.prepareStatement(sb.toString());
				
				pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(3, getChallanNum());
				pst.setBoolean(4, true);
				//pst.setInt(5, uF.parseToInt(getF_org()));
				//pst.setInt(6, uF.parseToInt(getF_strWLocation()));
//				System.out.println("pst new=====>"+pst);
				
				System.out.println(" pst 3 in viewESICChallanPdfReports==>"+pst);

				rs = pst.executeQuery();
				while (rs.next()) {
					setChallanDate(rs.getString("entry_date"));
					
					hmESIC.put("CHALLAN_CHEQUE_NO", uF.showData(rs.getString("cheque_no"), ""));
					hmESIC.put("CHALLAN_PAID_DATE", uF.getDateFormat(rs.getString("paid_date"), DBDATE, CF.getStrReportDateFormat()));
					hmESIC.put("CHALLAN_BANK_NAME", uF.showData(rs.getString("bank_name"), ""));
					hmESIC.put("CHALLAN_BRANCH_NAME", uF.showData(rs.getString("bank_branch"), ""));
					hmESIC.put("CHALLAN_MODE_DEPOSIT", uF.showData(rs.getString("mode_tds_deposit"), "")); 
				}
		        rs.close();
		        pst.close(); 
				
				
				/*pst = con.prepareStatement("select * from emp_esi_details where _month in ("+month+") and emp_id in " +
						"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						" and challan_no=? and is_paid=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") " +
						"and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?)) " +
						" and financial_year_start=? and financial_year_end=?");
*/				
		        
		        System.out.println("month===>"+month);
		        sb=new StringBuilder();
		        sb.append(" select * from emp_esi_details where _month in ("+month+") and emp_id in " +
						"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						" and challan_no=? and is_paid=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ")");
		        if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+"))");
				}else{
					sb.append(" and emp_id in(0))");
				}
		        sb.append(" and financial_year_start=? and financial_year_end=?");
		        pst=con.prepareStatement(sb.toString());
				
				pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(3, getChallanNum());
				pst.setBoolean(4, true);
				//pst.setInt(5, uF.parseToInt(getF_org()));
				//pst.setInt(6, uF.parseToInt(getF_strWLocation()));
				pst.setDate(5,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(6,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst new=====>"+pst);
				System.out.println(" pst 4 in viewESICChallanPdfReports==>"+pst);

				rs = pst.executeQuery();

				while (rs.next()) {
					employerContribution += rs.getDouble("ersi_contribution");
					employeeContribution += rs.getDouble("eesi_contribution");
				}
		        rs.close();
		        pst.close();
				
				
			} else {
				/*pst = con.prepareStatement("select * from emp_esi_details where _month in ("+month+") and emp_id in " +
						"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						" and entry_date=? and is_paid=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ") " +
						"and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?))" +
						" and financial_year_start=? and financial_year_end=?");
*/				
				StringBuilder sb=new StringBuilder();
				sb.append(" select * from emp_esi_details where _month in ("+month+") and emp_id in " +
						"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						" and entry_date=? and is_paid=? and challan_type in ("+ EMPLOYER_ESI+ ","+ EMPLOYEE_ESI+ ")");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+"))");
				}else{
					sb.append(" and emp_id in(0))");
				}
		        sb.append(" and financial_year_start=? and financial_year_end=?");
		        pst=con.prepareStatement(sb.toString());
				
				pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(getChallanDate(), DBDATE));
				pst.setBoolean(4, false);
			//	pst.setInt(5, uF.parseToInt(getF_org()));
			//	pst.setInt(6, uF.parseToInt(getF_strWLocation()));
				pst.setDate(5,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(6,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst new=====>"+pst);
				System.out.println(" pst 5 in viewESICChallanPdfReports==>"+pst);

				rs = pst.executeQuery();

				while (rs.next()) {
					employerContribution += rs.getDouble("ersi_contribution");
					employeeContribution += rs.getDouble("eesi_contribution");
				}
		        rs.close();
		        pst.close();
			}
		
			totalContribution = employeeContribution + employerContribution;
			
			request.setAttribute("hmESIC", hmESIC);
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String[] getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String[] strMonth) {
		this.strMonth = strMonth;
	}

	public String getTotalMonths() {
		return totalMonths;
	}

	public void setTotalMonths(String totalMonths) {
		this.totalMonths = totalMonths;
	}

	public String getChallanType() {
		return challanType;
	}

	public void setChallanType(String challanType) {
		this.challanType = challanType;
	}

	public String getStrSubmit() {
		return strSubmit;
	}

	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}

	public String[] getEmpIds() {
		return empIds;
	}

	public void setEmpIds(String[] empIds) {
		this.empIds = empIds;
	}

	public String getChallanDate() {
		return challanDate;
	}

	public void setChallanDate(String challanDate) {
		this.challanDate = challanDate;
	}

	public String getChallanNum() {
		return challanNum;
	}

	public void setChallanNum(String challanNum) {
		this.challanNum = challanNum;
	}

	public String getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(String paidDate) {
		this.paidDate = paidDate;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getCheque_no() {
		return cheque_no;
	}
	public void setCheque_no(String cheque_no) {
		this.cheque_no = cheque_no;
	}

	private HttpServletResponse response;
	private HttpServletRequest request;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;

	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getF_org() {
		return f_org;
	}
	public void setF_org(String f_org) {
		this.f_org = f_org;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankBranch() {
		return bankBranch;
	}
	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public String getNavigationId() {
		return navigationId;
	}
	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}
	public String getToPage() {
		return toPage;
	}
	public void setToPage(String toPage) {
		this.toPage = toPage;
	}
	public String getF_strWLocation() {
		return f_strWLocation;
	}
	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}
	public String getSbEmp() {
		return sbEmp;
	}

	public void setSbEmp(String sbEmp) {
		this.sbEmp = sbEmp;
	}
}