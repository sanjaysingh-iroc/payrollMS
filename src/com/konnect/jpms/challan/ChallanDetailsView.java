package com.konnect.jpms.challan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.payroll.reports.EPFSalaryReport;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ChallanDetailsView extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private String printAction;
	private String challanDate;
	private String financialYear; 
	private String strEmpId;
	private String[] strMonth;
	private String challanType; 
	private String payAmount;
	private String f_org;
//	private String f_strWLocation;
	private String state;
	
	private String sbEmp;


	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		getOrgCurrencyDetails();
		//System.out.println("getPrintAction()"+getPrintAction());
		
		//System.out.println("sbEmp in challanDetails==>"+sbEmp);
		
		if(getPrintAction()!=null && getPrintAction().equalsIgnoreCase("totalAmount")){
			viewTotalAmount();
			return "totalamount";
		}else if(getPrintAction()!=null && getPrintAction().equalsIgnoreCase("printedAmount")){
			viewchallanPrintedAmount();
			return "printedamount";
		}else if(getPrintAction()!=null && getPrintAction().equalsIgnoreCase("printedNotPaid")){
			viewchallanPrintedButNotPaid();
			return "printedNotPaid";
		}else if(getPrintAction()!=null && getPrintAction().equalsIgnoreCase("payChallan")){
			return "paychallan";
		}else if(getPrintAction()!=null && getPrintAction().equalsIgnoreCase("amountPaid")){
			viewAmountPaid();
			return "amountpaid";
		}else if(getPrintAction()!=null && getPrintAction().equalsIgnoreCase("otherCharges")){	
			viewOtherCharges();
			return "otherCharges";
		}else{
			return SUCCESS;
		}
	}
	
	private void getOrgCurrencyDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try{
			con = db.makeConnection(con);
			
			String strOrgCurrId = CF.getOrgCurrencyIdByOrg(con, getF_org());
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			String strCurrency = "";
			if(uF.parseToInt(strOrgCurrId) > 0){
				Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(strOrgCurrId);
				if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
				strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
			} 
			request.setAttribute("strCurrency",strCurrency);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void viewOtherCharges() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try{
			con = db.makeConnection(con);
			/*pst = con.prepareStatement("select * from challan_details WHERE entry_date =? and challan_type=? and is_paid = false" +
					" and emp_id in (select emp_id from employee_official_details where org_id=? " +
					"and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
					"state s where wi.wlocation_state_id = s.state_id and s.state_id=?))");
			*/
			
			
			StringBuilder sb=new StringBuilder();
			sb.append("select * from challan_details WHERE entry_date =? and challan_type=? and is_paid = false");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			pst=con.prepareStatement(sb.toString());
			
			pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setInt(2,PROFESSIONAL_TAX);
			
			//pst.setInt(3, uF.parseToInt(getF_org()));
			//pst.setInt(4, uF.parseToInt(getState()));
		//	System.out.println("pst1 for other charges in chalanDetails =======>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmPTOtherCharge = new HashMap<String, String>();
			while(rs.next()){
				hmPTOtherCharge.put("AMT_TAX", uF.showData(rs.getString("amt_tax"), "0"));
				hmPTOtherCharge.put("INTEREST_AMT", uF.showData(rs.getString("interest_amt"), "0"));
				hmPTOtherCharge.put("PENALTY_AMT", uF.showData(rs.getString("penalty_amt"), "0"));
				hmPTOtherCharge.put("COMPOSITION_MONEY", uF.showData(rs.getString("composition_money"), "0"));
				hmPTOtherCharge.put("FINE_AMT", uF.showData(rs.getString("fine_amt"), "0"));
				hmPTOtherCharge.put("FEES_AMT", uF.showData(rs.getString("fees_amt"), "0"));
				hmPTOtherCharge.put("ADVANCE_AMT", uF.showData(rs.getString("advance_amt"), "0"));
			}
            rs.close();
            pst.close();
			
			request.setAttribute("hmPTOtherCharge",hmPTOtherCharge);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	public void viewAmountPaid(){
	//	System.out.println("in viewAmountPaid===");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String[] strPayCycleDates = null;
		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;
		String months="";
		String payMonts="";
		int totalPaidamount=0;
		
		
		Map<String,Map<String,String>> hmMap = new HashMap<String,Map<String,String>>();
		int payYear=0;
		try{
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
		
		for(int i=0;i<getStrMonth().length;i++){
			if(i==0){		
				months=","+getStrMonth()[i];
			}else{
				months+=getStrMonth()[i]+",";
			}
		}
		con = db.makeConnection(con);
		
		Map hmEmpName = CF.getEmpNameMap(con,null, null);
		
		List<String> alMonthList = Arrays.asList(months.split(","));
		if(alMonthList == null) alMonthList = new ArrayList<String>();
		String strMonths = "";
		for (int i = 0; alMonthList != null && i < alMonthList.size(); i++) {
			if (i == 0) {
				strMonths=" and (";
			} else {
				strMonths+=" OR";
			}
			strMonths+=" month like '%," + alMonthList.get(i) + ",%'";
			if (i == alMonthList.size() - 1) {
				strMonths+=")";
			}
		}

		/*pst = con.prepareStatement("select amount,emp_id from challan_details where financial_year_from_date=?" +
				" and financial_year_to_date=? and challan_type=? and is_paid=? "+strMonths+" and emp_id in (select emp_id " +
				"from employee_official_details where  org_id=? and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
				"state s where wi.wlocation_state_id = s.state_id and s.state_id=?))");
		*/
		
		StringBuilder sb=new StringBuilder();
		sb.append("select amount,emp_id from challan_details where financial_year_from_date=?" +
				" and financial_year_to_date=? and challan_type=? and is_paid=? "+strMonths+"");
		if(sbEmp!=null && !sbEmp.equals("")){
			sb.append(" and emp_id in ("+sbEmp+")");
		}else{
			sb.append(" and emp_id in (0)");
		}
		pst=con.prepareStatement(sb.toString());
		
		
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3,PROFESSIONAL_TAX);
		pst.setBoolean(4,true);
		//pst.setInt(5, uF.parseToInt(getF_org()));
		//pst.setInt(6, uF.parseToInt(getState()));
	//	System.out.println("pst2 viewAmountPaid in chalanDetails =======>"+pst);

		rs = pst.executeQuery();
		
		List<String> empList=new ArrayList<String>();
		while(rs.next()){
			totalPaidamount +=(uF.parseToInt(rs.getString("amount")));
			Map<String,String> hmInner = new HashMap<String,String>();
			 hmInner.put("AMOUNT",uF.formatIntoComma(rs.getDouble("amount")));
			 hmInner.put("EMP_ID",rs.getString("emp_id"));
			 empList.add(rs.getString("emp_id"));
			 hmMap.put(rs.getString("emp_id"), hmInner);
			 
		}
        rs.close();
        pst.close();
		
	//	System.out.println("empList in viewAmountPaid"+empList);
		
		request.setAttribute("hmEmpName",hmEmpName);
		request.setAttribute("empList",empList);
		request.setAttribute("hmMap",hmMap);
		request.setAttribute("totalPaidamount",totalPaidamount+"");
		
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	public void viewchallanPrintedButNotPaid()
	{
		
	//	System.out.println("in viewchallanPrintedButNotPaid");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String[] strPayCycleDates = null;
		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;
		String months = null;
		String payMonts="";
		int totalunpaidamount=0;
		
		
		Map<String,Map<String,String>> hmMap = new HashMap<String,Map<String,String>>();
		int payYear=0;
		try{
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
		
		for(int i=0;i<getStrMonth().length;i++){
			if(i==0){		
				months=getStrMonth()[i];
			}else{
				months+=","+getStrMonth()[i];
			}
		
		}

		con = db.makeConnection(con);
		Map hmEmpName = CF.getEmpNameMap(con,null, null);
		
		List<String> alMonthList = Arrays.asList(months.split(","));
		if(alMonthList == null) alMonthList = new ArrayList<String>();
		String strMonths = "";
		for (int i = 0; alMonthList != null && i < alMonthList.size(); i++) {
			if (i == 0) {
				strMonths=" and (";
			} else {
				strMonths+=" OR";
			}
			strMonths+=" month like '%," + alMonthList.get(i) + ",%'";
			if (i == alMonthList.size() - 1) {
				strMonths+=")";
			}
		}

		/*pst = con.prepareStatement("select amount,emp_id from challan_details where financial_year_from_date=?" +
				" and financial_year_to_date=? and challan_type=? and is_paid=? "+strMonths+"  and emp_id in (select emp_id " +
				"from employee_official_details where org_id=? and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
				"state s where wi.wlocation_state_id = s.state_id and s.state_id=?))");
		*/
		StringBuilder sb=new StringBuilder();
		sb.append("select amount,emp_id from challan_details where financial_year_from_date=?" +
				" and financial_year_to_date=? and challan_type=? and is_paid=? "+strMonths+"");
		if(sbEmp!=null && !sbEmp.equals("")){
			sb.append(" and emp_id in ("+sbEmp+")");
		}else{
			sb.append(" and emp_id in (0)");
		}
		pst=con.prepareStatement(sb.toString());
		
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3,PROFESSIONAL_TAX);
		pst.setBoolean(4,false);
		//pst.setInt(5, uF.parseToInt(getF_org()));
		//pst.setInt(6, uF.parseToInt(getState()));
//		System.out.println("print not paid pst=====>"+pst);
//		System.out.println("pst3 vieChallanprintedButNotPaid in chalanDetails =======>"+pst);

		rs = pst.executeQuery();
		
		List<String> empList=new ArrayList<String>();
		while(rs.next())
		{
			totalunpaidamount+=uF.parseToInt(rs.getString("amount"));
			Map<String,String> hmInner = new HashMap<String,String>();
			 hmInner.put("AMOUNT",  uF.formatIntoComma(rs.getDouble("amount")));
			 hmInner.put("EMP_ID",rs.getString("emp_id"));
			 empList.add(rs.getString("emp_id"));
			 hmMap.put(rs.getString("emp_id"), hmInner);
		}
        rs.close();
        pst.close();

 //     System.out.println("empList in viewchallanPrintedButNotPaid" +empList); 
       
		request.setAttribute("totalunpaidamount",totalunpaidamount+"");
		request.setAttribute("hmEmpName",hmEmpName);
		request.setAttribute("empList",empList);
		request.setAttribute("hmMap",hmMap);
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	public void viewchallanPrintedAmount()
	{
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String[] strPayCycleDates = null;
		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;
		String months="";
		String payMonts="";
		String printdate="";
		String amountprinted="";
		Map hmEmpName = CF.getEmpNameMap(con,null, null);
		
		Map<String,Map<String,String>> hmMap = new HashMap<String,Map<String,String>>();
		int payYear=0;
		try{
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
		
		for(int i=0;i<getStrMonth().length;i++){
		
			months+=getStrMonth()[i]+",";
		
		}
		if(months.contains(",")){
			months=months.substring(0, months.length()-1);
		}
		con = db.makeConnection(con);
		
		List<String> alMonthList = Arrays.asList(months.split(","));
		if(alMonthList == null) alMonthList = new ArrayList<String>();
		String strMonths = "";
		for (int i = 0; alMonthList != null && i < alMonthList.size(); i++) {
			if (i == 0) {
				strMonths=" and (";
			} else {
				strMonths+=" OR";
			}
			strMonths+=" month like '%," + alMonthList.get(i) + ",%'";
			if (i == alMonthList.size() - 1) {
				strMonths+=")";
			}
		}
		
		/*pst = con.prepareStatement("select sum(amount) as amount from challan_details where financial_year_from_date=?" +
			" and financial_year_to_date=? and challan_type=? and is_print=? and is_paid=? "+strMonths+" " +
			"and emp_id in (select emp_id from employee_official_details where  org_id=? " +
			"and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
			"state s where wi.wlocation_state_id = s.state_id and s.state_id=?))");
		*/
		
		StringBuilder sb=new StringBuilder();
		sb.append("select sum(amount) as amount from challan_details where financial_year_from_date=?" +
			" and financial_year_to_date=? and challan_type=? and is_print=? and is_paid=? "+strMonths+" ");
		
		if(sbEmp!=null && !sbEmp.equals("")){
			sb.append(" and emp_id in ("+sbEmp+")");
		}else{
			sb.append(" and emp_id in (0)");
		}
		pst=con.prepareStatement(sb.toString());
		
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3,PROFESSIONAL_TAX);
		pst.setBoolean(4,true);
		pst.setBoolean(5,false);
		//pst.setInt(6, uF.parseToInt(getF_org()));
		//pst.setInt(7, uF.parseToInt(getState()));
//		System.out.println("pst4 viewchalanPrintedamount in chalanDetails =======>"+pst);

		rs = pst.executeQuery();
		while(rs.next()){
			amountprinted=rs.getString("amount");
		}
        rs.close();
        pst.close();
        
		/*pst = con.prepareStatement("select emp_id,amount,entry_date from challan_details where financial_year_from_date=?" +
				" and financial_year_to_date=? and challan_type=? and is_print=? "+strMonths+" and emp_id in (select emp_id from employee_official_details where org_id=?" +
				" and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
				"state s where wi.wlocation_state_id = s.state_id and s.state_id=?))");
	*/
		
		sb=new StringBuilder();
		sb.append("select emp_id,amount,entry_date from challan_details where financial_year_from_date=?" +
				" and financial_year_to_date=? and challan_type=? and is_print=? "+strMonths+"");
		if(sbEmp!=null && !sbEmp.equals("")){
			sb.append(" and emp_id in ("+sbEmp+")");
		}else{
			sb.append(" and emp_id in (0)");
		}
		pst=con.prepareStatement(sb.toString());
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3,PROFESSIONAL_TAX);
		pst.setBoolean(4,true);
		//pst.setInt(5, uF.parseToInt(getF_org()));
		//pst.setInt(6, uF.parseToInt(getState()));
//		System.out.println("pst5 in chalanDetails =======>"+pst);

		rs = pst.executeQuery();
		
		List<String> empList=new ArrayList<String>();
		while(rs.next())
		{
			Map<String,String> hmInner = new HashMap<String,String>();
			 hmInner.put("EMP_ID",rs.getString("emp_id"));
			 hmInner.put("AMOUNT",uF.formatIntoComma(rs.getDouble("amount")));
			 printdate=rs.getString("entry_date");
			 empList.add(rs.getString("emp_id"));
			 hmMap.put(rs.getString("emp_id"), hmInner);
		}
        rs.close();
        pst.close();
		
		request.setAttribute("financialYear",getFinancialYear());
		request.setAttribute("months",months);
		request.setAttribute("hmEmpName",hmEmpName);
		request.setAttribute("empList",empList);
		request.setAttribute("printdate",printdate);
		request.setAttribute("amountprinted",amountprinted);
		request.setAttribute("hmMap",hmMap);
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void viewTotalAmount()
	{
//		System.out.println("in viewTotalAmount");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String[] strPayCycleDates = null;
		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;
		String months="";
		String payMonts="";
		
		
		Map<String,Map<String,String>> hmMap = new HashMap<String,Map<String,String>>();
		int payYear=0;
		try{
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
		
		for(int i=0;i<getStrMonth().length;i++){
		
			months+=getStrMonth()[i]+",";
		
		}
		if(months.contains(",")){
			months=months.substring(0, months.length()-1);
		}
		con = db.makeConnection(con);
		
		Map hmEmpName = CF.getEmpNameMap(con,null, null);
		
		List<String> alMonthList = Arrays.asList(months.split(","));
		if(alMonthList == null) alMonthList = new ArrayList<String>();
		String strMonths = "";
		for (int i = 0; alMonthList != null && i < alMonthList.size(); i++) {
			if (i == 0) {
				strMonths=" and (";
			} else {
				strMonths+=" OR";
			}
			strMonths+=" month like '%," + alMonthList.get(i) + ",%'";
			if (i == alMonthList.size() - 1) {
				strMonths+=")";
			}
		}
		
		StringBuilder sb = new StringBuilder();	
		
		sb.append("select distinct(emp_id),sum(amount) as amount,emp_fname, emp_lname from payroll_generation pg, employee_personal_details epd where epd.emp_per_id = pg.emp_id and month in("+months+")" +
		" and financial_year_from_date=? and financial_year_to_date=? and amount>0 and pg.is_paid=true and pg.emp_id not in " +
		"(select emp_id from challan_details where "); 
		
//		
//		for (int i = 0; getStrMonth()!=null && i < getStrMonth().length; i++) {
//			if(i==0){
//				sb.append(" (");	
//			}else{
//				sb.append(" OR");
//			}
//			sb.append(" month like '%,"+getStrMonth()[i]+",%'");
//			if(i==getStrMonth().length-1){
//				sb.append(")");
//			}
//		}
		
		/*sb.append("  challan_type=? "+strMonths+" and financial_year_from_date=? and financial_year_to_date=?) and salary_head_id=? and emp_id in (select emp_id " +
				"from employee_official_details where  org_id=? and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
				"state s where wi.wlocation_state_id = s.state_id and s.state_id=?))" +
				" group by emp_id,emp_fname, emp_lname order by emp_fname, emp_lname");
		*/
		sb.append(" challan_type=? "+strMonths+" and financial_year_from_date=? and financial_year_to_date=?) and salary_head_id=?");
		
		if(sbEmp!=null && !sbEmp.equals("")){
			sb.append(" and emp_id in ("+sbEmp+")");
		}else{
			sb.append(" and emp_id in (0)");
		}
		sb.append(" group by emp_id,emp_fname, emp_lname order by emp_fname, emp_lname");
		
		pst = con.prepareStatement(sb.toString());
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3,PROFESSIONAL_TAX);
		pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
		pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(6,PROFESSIONAL_TAX);
		//pst.setInt(7, uF.parseToInt(getF_org()));
		//pst.setInt(8, uF.parseToInt(getState()));
	//	System.out.println("pst for total in ChallanDetails=======>>>>"+pst);
		
		rs = pst.executeQuery();
		
		List<String> empList=new ArrayList<String>();
		while(rs.next())
		{
			 Map<String,String> hmInner = new HashMap<String,String>();
			 hmInner.put("AMOUNT", uF.formatIntoComma(rs.getDouble("amount")));
			 hmInner.put("EMP_ID",rs.getString("emp_id"));
			 empList.add(rs.getString("emp_id"));
			 hmMap.put(rs.getString("emp_id"), hmInner);
		}
        rs.close();
        pst.close();
		
		request.setAttribute("financialYear",getFinancialYear());
		request.setAttribute("months",months);
		request.setAttribute("hmEmpName",hmEmpName);
		request.setAttribute("empList",empList);
		request.setAttribute("hmMap",hmMap);
		
	//	System.out.println("empList=="+empList);
	//	System.out.println("hmMap=="+hmMap);
		
		} catch (Exception e) {
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
	public String getChallanType() {
		return challanType;
	}
	public void setChallanType(String challanType) {
		this.challanType = challanType;
	}

	public String getChallanDate() {
		return challanDate;
	}

	public void setChallanDate(String challanDate) {
		this.challanDate = challanDate;
	}

	public String getPrintAction() {
		return printAction;
	}

	public void setPrintAction(String printAction) {
		this.printAction = printAction;
	}

	public String getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSbEmp() {
		return sbEmp;
	}

	public void setSbEmp(String sbEmp) {
		this.sbEmp = sbEmp;
	}
	
//	public String getF_strWLocation() {
//		return f_strWLocation;
//	}
//
//	public void setF_strWLocation(String f_strWLocation) {
//		this.f_strWLocation = f_strWLocation;
//	}
	
}