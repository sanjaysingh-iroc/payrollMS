package com.konnect.jpms.challan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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

public class EPFChallanDetailsView   extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	String printAction;
	String challanDate;
	String financialYear; 
	String strEmpId;
	String strMonth;
	String challanType;
	String payAmount;
	String f_org;
	String sbEmp;
	
	public String execute() throws Exception {
	//	System.out.println("in EPFChallanDetailsView");

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
	
	public void viewAmountPaid()
	{
//		System.out.println("in viewAmountPaid");
		
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
		double totalPaidamount=0;
		
		
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

		/*pst = con.prepareStatement("select distinct(emp_id) as emp_id,sum(amount) as amount from challan_details where financial_year_from_date=?" +
			" and financial_year_to_date=? and challan_type in ("+EMPLOYEE_EPF+","+EMPLOYER_EPF+") and is_paid=? and emp_id in (select emp_id " +
			" from employee_official_details where org_id=?) group by emp_id");
*/		
		StringBuilder sb=new StringBuilder();
		sb.append("select distinct(emp_id) as emp_id,sum(amount) as amount from challan_details where financial_year_from_date=?" +
			" and financial_year_to_date=? and challan_type in ("+EMPLOYEE_EPF+","+EMPLOYER_EPF+") and is_paid=?");
		if(sbEmp!=null && !sbEmp.equals("")){
			sb.append(" and emp_id in ("+sbEmp+")");
		}else{
			sb.append(" and emp_id in (0)");
		}
		sb.append(" group by emp_id");
		pst=con.prepareStatement(sb.toString());
		
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setBoolean(3, true);
		//pst.setInt(4, uF.parseToInt(getF_org()));
//		System.out.println("pst in viewAmountPaid=="+pst);
		rs = pst.executeQuery();
		List<String> empList = new ArrayList<String>();
		while(rs.next()) {
			
			totalPaidamount += uF.parseToDouble(rs.getString("amount"));
			Map<String,String> hmInner = new HashMap<String,String>();
			 hmInner.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("amount")));
			 hmInner.put("EMP_ID", rs.getString("emp_id"));
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
	public void viewchallanPrintedButNotPaid() {
		
//		System.out.println("in viewchallanPrintedButNotPaid");
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
		double totalunpaidamount=0;
		
		
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

		/*pst = con.prepareStatement("select distinct(emp_id) as emp_id,sum(amount) as amount from challan_details where financial_year_from_date=?" +
				" and financial_year_to_date=? and challan_type in ("+ EMPLOYEE_EPF + "," + EMPLOYER_EPF + ") and is_paid=?  and month='"+getStrMonth()+"' " +
				"and emp_id in (select emp_id from employee_official_details where org_id=?) group by emp_id");
*/		
		StringBuilder sb=new StringBuilder();
		sb.append("select distinct(emp_id) as emp_id,sum(amount) as amount from challan_details where financial_year_from_date=?" +
				" and financial_year_to_date=? and challan_type in ("+ EMPLOYEE_EPF + "," + EMPLOYER_EPF + ") and is_paid=?  and month='"+getStrMonth()+"'");
		
		if(sbEmp!=null && !sbEmp.equals("")){
			sb.append(" and emp_id in ("+sbEmp+")");
		}else{
			sb.append(" and emp_id in (0)");
		}
		sb.append(" group by emp_id");
		pst=con.prepareStatement(sb.toString());
		
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setBoolean(3,false);
		//pst.setInt(4, uF.parseToInt(getF_org()));
		
//		System.out.println("pst in printedNotPaid =====>"+pst);
		rs = pst.executeQuery();
		List<String> empList=new ArrayList<String>();
		while(rs.next())
		{
			totalunpaidamount+=rs.getDouble("amount");
			Map<String,String> hmInner = new HashMap<String,String>();
			 hmInner.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("amount")));
			 hmInner.put("EMP_ID",rs.getString("emp_id"));
			 empList.add(rs.getString("emp_id"));
			 hmMap.put(rs.getString("emp_id"), hmInner);
		}
        rs.close();
        pst.close();
		
//		System.out.println("totalunpaidamount=====>"+totalunpaidamount);
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
//		System.out.println("in viewchallanPrintedAmount");
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
		
		
		con = db.makeConnection(con);
		
		/*pst = con.prepareStatement("select sum(amount) as amount from challan_details where financial_year_from_date=?" +
		" and financial_year_to_date=? and challan_type in ("+EMPLOYER_ESI+","+EMPLOYEE_ESI+") and is_print=? and is_paid=? " +
				" and emp_id in (select emp_id from employee_official_details where org_id=?)");
		*/										
		
		StringBuilder sb=new StringBuilder();
		sb.append("select sum(amount) as amount from challan_details where financial_year_from_date=?" +
		" and financial_year_to_date=? and challan_type in ("+EMPLOYER_ESI+","+EMPLOYEE_ESI+") and is_print=? and is_paid=? ");
		if(sbEmp!=null && !sbEmp.equals("")){
			sb.append(" and emp_id in ("+sbEmp+")");
		}else{
			sb.append(" and emp_id in (0)");
		}
		pst=con.prepareStatement(sb.toString());
		
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setBoolean(3,true);
		pst.setBoolean(4,false);
		//pst.setInt(5, uF.parseToInt(getF_org()));
//		System.out.println("pst 1 in viewchallanPrintedAmount==>"+pst);
		rs = pst.executeQuery();
		while(rs.next()){
			amountprinted=rs.getString("amount");
		}
        rs.close();
        pst.close();

		/*pst = con.prepareStatement("select emp_id,amount,entry_date from challan_details where financial_year_from_date=?" +
				" and financial_year_to_date=? and challan_type in ("+EMPLOYER_ESI+","+EMPLOYEE_ESI+") and is_print=? " +
						" and emp_id in (select emp_id from employee_official_details where org_id=?)");
		*/
		sb=new StringBuilder();
		sb.append(" select emp_id,amount,entry_date from challan_details where financial_year_from_date=?" +
				" and financial_year_to_date=? and challan_type in ("+EMPLOYER_ESI+","+EMPLOYEE_ESI+") and is_print=?");
		if(sbEmp!=null && !sbEmp.equals("")){
			sb.append(" and emp_id in ("+sbEmp+")");
		}else{
			sb.append(" and emp_id in (0)");
		}
		pst=con.prepareStatement(sb.toString());
		
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setBoolean(3,true);
		//pst.setInt(4, uF.parseToInt(getF_org()));
//		System.out.println(" pst 2 in viewchallanPrintedAmount"+pst);
		rs = pst.executeQuery();
		List<String> empList=new ArrayList<String>();
		while(rs.next()){
			Map<String,String> hmInner = new HashMap<String,String>();
			 hmInner.put("EMP_ID",rs.getString("emp_id"));
			 hmInner.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("amount")));
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
		
		double totalunpaidamount=0;
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
		
		StringBuilder sb = new StringBuilder();	
		/*sb.append("select distinct(emp_id),sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution," +
					"sum(erps_contribution) as erps_contribution,sum(erdli_contribution) as erdli_contribution,sum(pf_admin_charges) as pf_admin_charges," +
					"sum(edli_admin_charges) as edli_admin_charges,sum(evpf_contribution) as evpf_contribution,emp_fname, emp_lname " +
				" from emp_epf_details pg, employee_personal_details epd where epd.emp_per_id = pg.emp_id and _month=?" +
				" and financial_year_start=? and financial_year_end=? and pg.emp_id not in " +
				"(select emp_id from challan_details where month=?"); 
			
		
		sb.append(" and challan_type in ("+ EMPLOYEE_EPF + "," + EMPLOYER_EPF + ") and emp_id in (select emp_id from employee_official_details where org_id=?)" +
						" and financial_year_from_date=? and financial_year_to_date=?) " +
						" group by emp_id,emp_fname, emp_lname order by emp_fname, emp_lname");
		*/
		
		
		sb.append("select distinct(emp_id),sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution," +
				"sum(erps_contribution) as erps_contribution,sum(erdli_contribution) as erdli_contribution,sum(pf_admin_charges) as pf_admin_charges," +
				"sum(edli_admin_charges) as edli_admin_charges,sum(evpf_contribution) as evpf_contribution, emp_fname, emp_lname " +
				"from emp_epf_details pg, employee_personal_details epd where epd.emp_per_id = pg.emp_id  and  financial_year_start=?"
				+ " and financial_year_end=? and _month=? and emp_id not in (select emp_id from challan_details where month=?");
		
		sb.append(" and challan_type in ("+ EMPLOYEE_EPF + "," + EMPLOYER_EPF + ") and financial_year_from_date=? and financial_year_to_date=?)");
		
		if(sbEmp!=null && !sbEmp.equals("")){
			sb.append(" and emp_id in ("+sbEmp+")");
		}else{
			sb.append(" and emp_id in (0)");
		}
		
		sb.append(" group by emp_id,emp_fname, emp_lname order by emp_fname, emp_lname");
		
		/*sb.append(" and challan_type in ("+ EMPLOYEE_EPF + "," + EMPLOYER_EPF + ")");
		if(sbEmp!=null && !sbEmp.equals("")){
			sb.append(" and emp_id in ("+sbEmp+")");
		}else{
			sb.append(" and emp_id in (0)");
		}
		sb.append(" and financial_year_from_date=? and financial_year_to_date=?) " +
						" group by emp_id,emp_fname, emp_lname order by emp_fname, emp_lname");*/
		
		/*pst = con.prepareStatement(sb.toString());
		pst.setInt(1,uF.parseToInt(getStrMonth()));
		pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
		pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setString(4,getStrMonth());
		pst.setInt(5, uF.parseToInt(getF_org()));
		pst.setDate(6, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
		pst.setDate(7, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		*/
		
		
		
		pst = con.prepareStatement(sb.toString()); 
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3,uF.parseToInt(getStrMonth()));
		pst.setString(4,getStrMonth()); 
		pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//		System.out.println("pst in viewTotalAmount====>"+pst);
		rs = pst.executeQuery();
		
		List<String> empList=new ArrayList<String>();
		
		while(rs.next()){
			 Map<String,String> hmInner = new HashMap<String,String>();
			 double totalAmt = (rs.getDouble("eepf_contribution")+rs.getDouble("erpf_contribution")+rs.getDouble("erps_contribution")+rs.getDouble("erdli_contribution")+rs.getDouble("pf_admin_charges")+rs.getDouble("edli_admin_charges")+rs.getDouble("evpf_contribution"));
//			 hmInner.put("AMOUNT", ""+ Math.round((totalAmt)));
			 totalunpaidamount += totalAmt;
			 hmInner.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(totalAmt));
			 hmInner.put("EMP_ID",rs.getString("emp_id"));
			 empList.add(rs.getString("emp_id"));
			 hmMap.put(rs.getString("emp_id"), hmInner);
		}
        rs.close();
        pst.close();
		
		request.setAttribute("financialYear",getFinancialYear());
		request.setAttribute("months",getStrMonth());
		request.setAttribute("hmEmpName",hmEmpName);
		request.setAttribute("empList",empList);
		request.setAttribute("hmMap",hmMap);
		request.setAttribute("totalunpaidamount",""+totalunpaidamount);
		
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

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
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
