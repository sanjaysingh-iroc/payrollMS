package com.konnect.jpms.challan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class TDSChallanDetailsView extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	String printAction;
	String challanDate;
	String financialYear; 
	String strEmpId;
	String[] strMonth;
	String challanType;
	String payAmount;
	String emp_id;
	String f_org;
	String sbEmp;
	 
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		getOrgCurrencyDetails();
			
		if(getPrintAction()!=null && getPrintAction().equalsIgnoreCase("totalAmount"))
		{
			viewTotalAmount();
			return "totalamount";
		}else if(getPrintAction()!=null && getPrintAction().equalsIgnoreCase("printedAmount"))
		{
			viewchallanPrintedAmount();
			return "printedamount";
		}else if(getPrintAction()!=null && getPrintAction().equalsIgnoreCase("printedNotPaid"))
		{
			viewchallanPrintedButNotPaid();
			return "printedNotPaid";
		}else if(getPrintAction()!=null && getPrintAction().equalsIgnoreCase("payChallan"))
		{
			
			return "paychallan";
		}else if(getPrintAction()!=null && getPrintAction().equalsIgnoreCase("amountPaid"))
		{
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
					" and emp_id in (select emp_id from employee_official_details where org_id=?)");
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
			pst.setInt(2, TDS);
			//pst.setInt(3, uF.parseToInt(getF_org()));
//			System.out.println("pst in viewOtherCharges=======>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmTDSOtherCharge = new HashMap<String, String>();
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;  
			String months = "";
			while(rs.next()){
				hmTDSOtherCharge.put("UNDER_SECTION_234", uF.showData(rs.getString("under_section234"), "0"));
				hmTDSOtherCharge.put("INTEREST_AMT", uF.showData(rs.getString("interest_amt"), "0"));
				hmTDSOtherCharge.put("PENALTY_AMT", uF.showData(rs.getString("penalty_amt"), "0"));
				hmTDSOtherCharge.put("SURCHARGE", uF.showData(rs.getString("surcharge"), "0"));
				strFinancialYearStart = uF.getDateFormat(rs.getString("financial_year_from_date"), DBDATE, DATE_FORMAT);
				strFinancialYearEnd = uF.getDateFormat(rs.getString("financial_year_to_date"), DBDATE, DATE_FORMAT);
				months += rs.getString("month");
			}
	        rs.close();
	        pst.close();
	        
			String tempMonth = "0";
			String[] strMonths = months.split(",");
			Set<String> setMths = new HashSet<String>();
			for(int i = 0; i < strMonths.length; i++){
				if(uF.parseToInt(strMonths [i]) > 0){
					setMths.add(strMonths [i]);
				}
			}
			int k = 0;
			for(String a : setMths){
				if(k == 0){
					tempMonth = a;
				}else{
					tempMonth +=","+a;
				}
				k++;
			}
			
//			System.out.println("tempMonth=====>"+tempMonth);
			/*pst = con.prepareStatement("select * from emp_tds_details where _month in ("+tempMonth+") and emp_id in " +
					"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
					" and entry_date=? and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=?)) " +
					" and financial_year_start=? and financial_year_end=? ");
		*/
			sb=new StringBuilder();
			sb.append("select * from emp_tds_details where _month in ("+tempMonth+") and emp_id in " +
					"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
					" and entry_date=? and is_paid=?");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+"))");
			}else{
				sb.append(" and emp_id in (0))");
			}
			sb.append(" and financial_year_start=? and financial_year_end=?");
			pst=con.prepareStatement(sb.toString());
			
			pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setBoolean(4, false);
		//	pst.setInt(5, uF.parseToInt(getF_org()));
			pst.setDate(5,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(6,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst new=====>"+pst);
//			System.out.println("pst 2 in viewOtherCharges=======>"+pst);

			rs = pst.executeQuery();
			double tds_amount=0;
			double edu_tax_amount=0;
			double std_tax_amount=0;
			while (rs.next()) {
				tds_amount += rs.getDouble("tds_amount");
				edu_tax_amount += rs.getDouble("edu_tax_amount");
				std_tax_amount += rs.getDouble("std_tax_amount");
			}
	        rs.close();
	        pst.close();
			
			double totalTDS=tds_amount+edu_tax_amount+std_tax_amount;
			
			hmTDSOtherCharge.put("INCOME_TAX", ""+uF.formatIntoTwoDecimalWithOutComma(Math.round(tds_amount)));
			hmTDSOtherCharge.put("EDU_CESS", ""+uF.formatIntoTwoDecimalWithOutComma(Math.round(edu_tax_amount)+Math.round(std_tax_amount)));
			hmTDSOtherCharge.put("TOTAL_TDS", ""+uF.formatIntoTwoDecimalWithOutComma(Math.round(totalTDS)));
			
			request.setAttribute("hmTDSOtherCharge",hmTDSOtherCharge);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	public void viewAmountPaid()
	{
//		System.out.println(" in viewAmountPaid");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String[] strPayCycleDates = null;
		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;
		int totalPaidamount=0;
		
		
		Map<String,Map<String,String>> hmMap = new HashMap<String,Map<String,String>>();
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
		
		
		con = db.makeConnection(con);
		Map hmEmpName = CF.getEmpNameMap(con,null, null);

		/*pst = con.prepareStatement("select amount,emp_id from challan_details where financial_year_from_date=?" +
				" and financial_year_to_date=? and challan_type=? and is_paid=? and emp_id in (select emp_id from employee_official_details where  org_id=?)");
		*/
		
		StringBuilder sb=new StringBuilder();
		sb.append("select amount,emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? and challan_type=? and is_paid=?");
		if(sbEmp!=null && !sbEmp.equals("")){
			sb.append(" and emp_id in ("+sbEmp+")");
		}else{
			sb.append(" and emp_id in (0)");
		}
		pst=con.prepareStatement(sb.toString());
		
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3,TDS);
		pst.setBoolean(4,true);
		//pst.setInt(5, uF.parseToInt(getF_org()));
//		System.out.println("pst in viewAmountPaid"+pst);
		rs = pst.executeQuery();
		List<String> empList=new ArrayList<String>();
		while(rs.next())
		{
			totalPaidamount+=(uF.parseToInt(rs.getString("amount")));
			Map<String,String> hmInner = new HashMap<String,String>();
			 hmInner.put("AMOUNT", rs.getString("amount"));
			 hmInner.put("EMP_ID",rs.getString("emp_id"));
			 empList.add(rs.getString("emp_id"));
			 hmMap.put(rs.getString("emp_id"), hmInner);
			 
		}
        rs.close();
        pst.close();
		
		
		
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
//		System.out.println(" viewchallanPrintedButNotPaid");
		
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
		
		
		con = db.makeConnection(con);
		
		Map hmEmpName = CF.getEmpNameMap(con,null, null);
		/*pst = con.prepareStatement("select amount,emp_id from challan_details where financial_year_from_date=?" +
				" and financial_year_to_date=? and challan_type=? and is_paid=? and emp_id in (select emp_id from employee_official_details where  org_id=?)");
		*/ 
		StringBuilder sb=new StringBuilder();
		sb.append("select amount,emp_id from challan_details where financial_year_from_date=?" +
				" and financial_year_to_date=? and challan_type=? and is_paid=?");
		if(sbEmp!=null && !sbEmp.equals("")){
			sb.append(" and emp_id in("+sbEmp+")");
		}else{
			sb.append(" and emp_id in (0)");
		}
		pst=con.prepareStatement(sb.toString());
		
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3,TDS);
		pst.setBoolean(4,false);
		//pst.setInt(5, uF.parseToInt(getF_org()));
//		System.out.println(" pst in viewchallanPrintedButNotPaid==>"+pst);
		rs = pst.executeQuery();
		
		List<String> empList=new ArrayList<String>();
		while(rs.next())
		{
			totalunpaidamount+=uF.parseToInt(rs.getString("amount"));
			Map<String,String> hmInner = new HashMap<String,String>();
			 hmInner.put("AMOUNT", rs.getString("amount"));
			 hmInner.put("EMP_ID",rs.getString("emp_id"));
			 empList.add(rs.getString("emp_id"));
			 hmMap.put(rs.getString("emp_id"), hmInner);
		}
        rs.close();
        pst.close();
		
		
		
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
		
//		System.out.println(" in viewchallanPrintedAmount");
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
			
			/*pst = con.prepareStatement("select sum(amount) as amount from challan_details where financial_year_from_date=?" +
			" and financial_year_to_date=? and challan_type=? and is_print=? and is_paid=? and emp_id in (select emp_id from employee_official_details where  org_id=?)");
		*/
			
			StringBuilder sb=new StringBuilder();
			sb.append("select sum(amount) as amount from challan_details where financial_year_from_date=?" +
			" and financial_year_to_date=? and challan_type=? and is_print=? and is_paid=?");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			pst=con.prepareStatement(sb.toString());
			
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3,TDS);
			pst.setBoolean(4,true);
			pst.setBoolean(5,false);
			//pst.setInt(6, uF.parseToInt(getF_org()));
//			System.out.println("pst1 in viewchallanPrintedAmount"+pst);
			rs = pst.executeQuery();
			while(rs.next())
			{
				
				amountprinted=rs.getString("amount");
				
			}
	        rs.close();
	        pst.close();
	        
			/*pst = con.prepareStatement("select emp_id,amount,entry_date from challan_details where financial_year_from_date=?" +
					" and financial_year_to_date=? and challan_type=? and is_print=? and emp_id in (select emp_id from employee_official_details where  org_id=?)");
			*/
			sb=new StringBuilder();
			sb.append("select emp_id,amount,entry_date from challan_details where financial_year_from_date=?" +
					" and financial_year_to_date=? and challan_type=? and is_print=?");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			pst=con.prepareStatement(sb.toString());
			
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3,TDS);
			pst.setBoolean(4,true);
			//pst.setInt(5, uF.parseToInt(getF_org()));
//			System.out.println("pst2 in viewchallanPrintedAmount"+pst);
			rs = pst.executeQuery();
			
			List<String> empList=new ArrayList<String>();
			while(rs.next())
			{
				Map<String,String> hmInner = new HashMap<String,String>();
				 hmInner.put("EMP_ID",rs.getString("emp_id"));
				 hmInner.put("AMOUNT", rs.getString("amount"));
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
		
//		System.out.println("viewTotalAmount");
		
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
		double totalamountpaid=0;
		
		
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
		
//		System.out.println("months=====>"+months);
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
		
		con = db.makeConnection(con);
		Map hmEmpName = CF.getEmpNameMap(con,null, null);
		
		StringBuilder sb = new StringBuilder();	
		/*sb.append("select distinct(emp_id) as emp_id,sum(actual_tds_amount) as amount from emp_tds_details where" +
				" financial_year_start=? and financial_year_end=? and actual_tds_amount>0 and _month in("+months+") and emp_id not in (select emp_id" +
				" from challan_details where ");
*/
		
		
		
		
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
		
		
		
		
		/*sb.append(" challan_type=? "+strMonths+" and emp_id in (select emp_id from employee_official_details where  org_id=?) " +
				"and financial_year_from_date=? and financial_year_to_date=? ) group by emp_id"); 
		*/
	
		/*pst = con.prepareStatement(sb.toString());
		
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3, TDS);
		//pst.setInt(4, uF.parseToInt(getF_org()));
		pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
		pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
	*/
		
		
//**********************************************************************************
		
		sb.append("select distinct(emp_id) as emp_id,sum(actual_tds_amount) as amount " +
				"from emp_tds_details where financial_year_start=? and financial_year_end=? " +
				"and _month in("+months+") and actual_tds_amount>0 ");
		
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			sb.append(" and emp_id not in ");
		
			sb.append(" (select emp_id from challan_details where financial_year_from_date=?"
				+ " and financial_year_to_date=? and challan_type=? and is_paid=? ");

			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0) ");
			}
			
			sb.append(" "+strMonths+" group by paid_date, challan_no,emp_id) ");
		
		sb.append(" group by emp_id");
		
		pst=con.prepareStatement(sb.toString());
		
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(5, TDS);
		pst.setBoolean(6, true);
		
//		System.out.println("pst in viewTotalAmount=====>"+pst);
		
		rs = pst.executeQuery();
		
		List<String> empList=new ArrayList<String>();
		while(rs.next())
		{
			totalamountpaid+=uF.parseToDouble(rs.getString("amount"));
			 Map<String,String> hmInner = new HashMap<String,String>();
			 hmInner.put("AMOUNT", uF.formatIntoComma(uF.parseToDouble(rs.getString("amount"))));
			 hmInner.put("EMP_ID",rs.getString("emp_id"));
			 empList.add(rs.getString("emp_id"));
			 hmMap.put(rs.getString("emp_id"), hmInner);
		}
        rs.close();
        pst.close();
		
		
		request.setAttribute("totalamountpaid",uF.formatIntoComma(totalamountpaid));
		request.setAttribute("financialYear",getFinancialYear());
		request.setAttribute("months",months);
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

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}
	
	public String getSbEmp() {
		return sbEmp;
	}

	public void setSbEmp(String sbEmp) {
		this.sbEmp = sbEmp;
	}

	
}