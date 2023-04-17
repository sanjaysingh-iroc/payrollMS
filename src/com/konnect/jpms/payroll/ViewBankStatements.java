package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class ViewBankStatements extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null; 
	
	
	private String strPaycycle;
	private String orgId;
	private String type;
	
	private String financialYear;
	private String strMonth;
	private String salaryHead;
	private String strStartDate;
	private String strEndDate;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		if(getType()!=null && getType().trim().equalsIgnoreCase("salary")){
			getSalaryBankStatements(uF);
		} else if(getType()!=null && getType().trim().equalsIgnoreCase("reimb")){
			getReimbursementBankStatements(uF);
		} else if(getType()!=null && getType().trim().equalsIgnoreCase("perk")){
			getPerkBankStatements(uF);
		} else if(getType()!=null && getType().trim().equalsIgnoreCase("lta") && uF.parseToInt(getSalaryHead()) > 0){
			getLTABankStatements(uF);
		} else if(getType()!=null && getType().trim().equalsIgnoreCase("gratuity")){
			getGratuityBankStatements(uF);
		} else if(getType()!=null && getType().trim().equalsIgnoreCase("reimbCTC")){
			getReimbursementCTCBankStatements(uF);
		}
		
		return SUCCESS;
	}
	
	private void getReimbursementCTCBankStatements(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String strPayCycleDates[]=null;
			
			if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("NULL")) {
				strPayCycleDates = getStrPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
				setStrPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			}
			
			con = db.makeConnection(con);
			
			String currId = CF.getOrgCurrencyIdByOrg(con, getOrgId());
			
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			Map<String, String> hmInnerCurrencyDetails = (Map<String, String>) hmCurrencyDetails.get(currId) ;
			if(hmInnerCurrencyDetails==null) hmInnerCurrencyDetails = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from reimbursement_ctc_pay rcp, payroll_bank_statement pbs where pbs.statement_id = rcp.statement_id " +
					"and rcp.paid_from=? and rcp.paid_to=? and rcp.emp_id in (select eod.emp_id from employee_personal_details epd," +
					" employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.org_id=?)");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getOrgId()));
			rs = pst.executeQuery();
			Map<String, String> hmIsCheque = new HashMap<String, String>();
			while(rs.next()){
				hmIsCheque.put(rs.getString("statement_id"), rs.getString("is_cheque"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select rcp.statement_id, statement_name, generated_date, payroll_amount from reimbursement_ctc_pay rcp, " +
					"payroll_bank_statement pbs where pbs.statement_id = rcp.statement_id and rcp.paid_from=? and rcp.paid_to=? " +
					"and rcp.emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod where " +
					"epd.emp_per_id=eod.emp_id and eod.org_id=?) group by rcp.statement_id, statement_name, generated_date, payroll_amount");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getOrgId()));
			rs = pst.executeQuery();
			List<List<String>> alOuter =  new ArrayList<List<String>>();
			int count=0;
			while(rs.next()){
				List<String> alInner =  new ArrayList<String>();
				
				StringBuilder sb = new StringBuilder();
				if(!uF.parseToBoolean(hmIsCheque.get(rs.getString("statement_id")))){
					sb.append("<div style=\"float: left;width:100%;\" id=\"myDiv_"+count+"\">");
					sb.append("<div style=\"padding:2px;width:100%;float:left;\"><div style=\"width:120px;float:left\">Cheque No. </div><input id=\"idchqno_"+count+"\" type=\"text\" style=\"width:100px\"></div>");
					sb.append("<div style=\"padding:2px;width:100%;float:left;\"><div style=\"width:120px;float:left\">Dated. </div><input type=\"text\" id=\"idchqdt_"+count+"\" name=\"chequeDate\"style=\"width:100px\"></div>");
					sb.append("<div style=\"padding:2px;width:100%;float:left;\"><div style=\"width:120px;float:left\"> &nbsp;</div><input type=\"button\" value=\"Save\" style=\"width:100px\" onclick=\"getContent('myDiv_"+count+"','UpdateChequeNo.action?chqno='+document.getElementById('idchqno_"+count+"').value+'&chqdt='+document.getElementById('idchqdt_"+count+"').value+'&stmtId="+rs.getString("statement_id")+"')\"></div>");
					sb.append("</div>");
				}
				alInner.add("<div style=\"float: left;width:100%\">Bank order generated on "+uF.getDateFormat(rs.getString("generated_date"), DBDATE, CF.getStrReportDateFormat())+" for "+uF.showData(hmInnerCurrencyDetails.get("SHORT_CURR"),"")+"&nbsp;<strong>"+Math.round(uF.parseToDouble(rs.getString("payroll_amount")))+"</strong></div>"+sb.toString()+"<a href=\"DownloadBankStatement.action?doc_id="+rs.getString("statement_id")+"\" target=\"_blank\"><i class=\"fa fa-file-o\" aria-hidden=\"true\" title=\"Download\"></i></a>");
				
				alOuter.add(alInner);
				count++;
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("alOuter", alOuter);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	private void getGratuityBankStatements(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			String currId = CF.getOrgCurrencyIdByOrg(con, orgId);
			
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			Map<String, String> hmInnerCurrencyDetails = (Map<String, String>) hmCurrencyDetails.get(currId) ;
			if(hmInnerCurrencyDetails==null) hmInnerCurrencyDetails = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from emp_gratuity_details egd, payroll_bank_statement pbs where pbs.statement_id = egd.statement_id " +
					"and egd.emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id=eod.emp_id and eod.org_id=?)");
			pst.setInt(1, uF.parseToInt(getOrgId()));
			rs = pst.executeQuery();
			Map<String, String> hmIsCheque = new HashMap<String, String>();
			while(rs.next()){
				hmIsCheque.put(rs.getString("statement_id"), rs.getString("is_cheque"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select egd.statement_id, statement_name, generated_date, payroll_amount from emp_gratuity_details egd, " +
					"payroll_bank_statement pbs where pbs.statement_id = egd.statement_id and egd.emp_id in (select eod.emp_id from " +
					"employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.org_id=?) " +
					"group by egd.statement_id, statement_name, generated_date, payroll_amount order by egd.statement_id desc");
			pst.setInt(1, uF.parseToInt(getOrgId()));
			rs = pst.executeQuery();
			List<List<String>> alOuter =  new ArrayList<List<String>>();
			int count=0;
			while(rs.next()){
				List<String> alInner =  new ArrayList<String>();
				
				StringBuilder sb = new StringBuilder();
				if(!uF.parseToBoolean(hmIsCheque.get(rs.getString("statement_id")))){
					sb.append("<div style=\"float: left;width:100%;\" id=\"myDiv_"+count+"\">");
					sb.append("<div style=\"padding:2px;width:100%;float:left;\"><div style=\"width:120px;float:left\">Cheque No. </div><input id=\"idchqno_"+count+"\" type=\"text\" style=\"width:100px\"></div>");
					sb.append("<div style=\"padding:2px;width:100%;float:left;\"><div style=\"width:120px;float:left\">Dated. </div><input type=\"text\" id=\"idchqdt_"+count+"\" name=\"chequeDate\"style=\"width:100px\"></div>");
					sb.append("<div style=\"padding:2px;width:100%;float:left;\"><div style=\"width:120px;float:left\"> &nbsp;</div><input type=\"button\" value=\"Save\" style=\"width:100px\" onclick=\"getContent('myDiv_"+count+"','UpdateChequeNo.action?chqno='+document.getElementById('idchqno_"+count+"').value+'&chqdt='+document.getElementById('idchqdt_"+count+"').value+'&stmtId="+rs.getString("statement_id")+"')\"></div>");
					sb.append("</div>");
				}
				alInner.add("<div style=\"float: left;width:100%\">Bank statement generated on "+uF.getDateFormat(rs.getString("generated_date"), DBDATE, CF.getStrReportDateFormat())+" for "+uF.showData(hmInnerCurrencyDetails.get("SHORT_CURR"),"")+"&nbsp;<strong>"+Math.round(uF.parseToDouble(rs.getString("payroll_amount")))+"</strong></div>"+sb.toString()+"<a href=\"DownloadBankStatement.action?doc_id="+rs.getString("statement_id")+"\" target=\"_blank\"><i class=\"fa fa-file-o\" aria-hidden=\"true\" title=\"Download\" style=\"float:right\"></i></a>");
				
				alOuter.add(alInner);
				count++;
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("alOuter", alOuter);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getLTABankStatements(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			if(getStrStartDate()!=null && (getStrStartDate().trim().equals("") || getStrStartDate().equalsIgnoreCase("NULL"))){
				setStrStartDate(null);
				setStrEndDate(null);
			}
			
			if(getStrEndDate()!=null && (getStrEndDate().trim().equals("") || getStrEndDate().equalsIgnoreCase("NULL"))){
				setStrStartDate(null);
				setStrEndDate(null);
			}
			
			if(getStrStartDate()==null && getStrEndDate()==null){
				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
				int nMaxDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				int nMinDate = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
				
				setStrStartDate(uF.getDateFormat(nMinDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
				setStrEndDate(uF.getDateFormat(nMaxDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
			}
			
			con = db.makeConnection(con);
			
			String currId = CF.getOrgCurrencyIdByOrg(con, getOrgId());
			
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			Map<String, String> hmInnerCurrencyDetails = (Map<String, String>) hmCurrencyDetails.get(currId) ;
			if(hmInnerCurrencyDetails==null) hmInnerCurrencyDetails = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from emp_lta_details eld, payroll_bank_statement pbs where pbs.statement_id = eld.statement_id " +
					"and eld.salary_head_id=? and eld.entry_date between ? and ? and eld.emp_id in (select eod.emp_id from employee_personal_details epd," +
					" employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.org_id=?)");
			pst.setInt(1, uF.parseToInt(getSalaryHead()));
			pst.setDate(2, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getOrgId()));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmIsCheque = new HashMap<String, String>();
			while(rs.next()){
				hmIsCheque.put(rs.getString("statement_id"), rs.getString("is_cheque"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select eld.statement_id, statement_name, generated_date, payroll_amount from emp_lta_details eld, " +
					"payroll_bank_statement pbs where pbs.statement_id = eld.statement_id and eld.salary_head_id=? and eld.entry_date between ? and ?  " +
					"and eld.emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod where " +
					"epd.emp_per_id=eod.emp_id and eod.org_id=?) group by eld.statement_id, statement_name, generated_date, payroll_amount");
			pst.setInt(1, uF.parseToInt(getSalaryHead()));
			pst.setDate(2, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getOrgId()));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			List<List<String>> alOuter =  new ArrayList<List<String>>();
			int count=0;
			while(rs.next()){
				List<String> alInner =  new ArrayList<String>();
				
				StringBuilder sb = new StringBuilder();
				if(!uF.parseToBoolean(hmIsCheque.get(rs.getString("statement_id")))){
					sb.append("<div style=\"float: left;width:100%;\" id=\"myDiv_"+count+"\">");
					sb.append("<div style=\"padding:2px;width:100%;float:left;\"><div style=\"width:120px;float:left\">Cheque No. </div><input id=\"idchqno_"+count+"\" type=\"text\" style=\"width:100px\"></div>");
					sb.append("<div style=\"padding:2px;width:100%;float:left;\"><div style=\"width:120px;float:left\">Dated. </div><input type=\"text\" id=\"idchqdt_"+count+"\" name=\"chequeDate\"style=\"width:100px\"></div>");
					sb.append("<div style=\"padding:2px;width:100%;float:left;\"><div style=\"width:120px;float:left\"> &nbsp;</div><input type=\"button\" value=\"Save\" class=\"btn btn-primary\" style=\"width:100px\" onclick=\"getContent('myDiv_"+count+"','UpdateChequeNo.action?chqno='+document.getElementById('idchqno_"+count+"').value+'&chqdt='+document.getElementById('idchqdt_"+count+"').value+'&stmtId="+rs.getString("statement_id")+"')\"></div>");
					sb.append("</div>");
				}
				alInner.add("<div style=\"float: left;width:100%\">Bank statement generated on "+uF.getDateFormat(rs.getString("generated_date"), DBDATE, CF.getStrReportDateFormat())+" for "+uF.showData(hmInnerCurrencyDetails.get("SHORT_CURR"),"")+"&nbsp;<strong>"+Math.round(uF.parseToDouble(rs.getString("payroll_amount")))+"</strong></div>"+sb.toString()+"<a href=\"DownloadBankStatement.action?doc_id="+rs.getString("statement_id")+"\" target=\"_blank\"><i class=\"fa fa-file-o\" aria-hidden=\"true\" title=\"Download\" style=\"float:right\"></i></a>");
				
				alOuter.add(alInner);
				count++;
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("alOuter", alOuter);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void getPerkBankStatements(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}
			
			con = db.makeConnection(con);
			
			String currId = CF.getOrgCurrencyIdByOrg(con, getOrgId());
			
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			Map<String, String> hmInnerCurrencyDetails = (Map<String, String>) hmCurrencyDetails.get(currId) ;
			if(hmInnerCurrencyDetails==null) hmInnerCurrencyDetails = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from emp_perks ep, payroll_bank_statement pbs where pbs.statement_id = ep.statement_id " +
					"and ep.financial_year_start =? and ep.financial_year_end=? and ep.perk_month=? and ep.emp_id in (select eod.emp_id from employee_personal_details epd," +
					" employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.org_id=?)");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrMonth()));
			pst.setInt(4, uF.parseToInt(getOrgId()));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmIsCheque = new HashMap<String, String>();
			while(rs.next()){
				hmIsCheque.put(rs.getString("statement_id"), rs.getString("is_cheque"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select ep.statement_id, statement_name, generated_date, payroll_amount from emp_perks ep, " +
					"payroll_bank_statement pbs where pbs.statement_id = ep.statement_id and ep.financial_year_start=? and ep.financial_year_end=? and ep.perk_month=?  " +
					"and ep.emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod where " +
					"epd.emp_per_id=eod.emp_id and eod.org_id=?) group by ep.statement_id, statement_name, generated_date, payroll_amount");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrMonth()));
			pst.setInt(4, uF.parseToInt(getOrgId()));
			rs = pst.executeQuery();
			List<List<String>> alOuter =  new ArrayList<List<String>>();
			int count=0;
			while(rs.next()){
				List<String> alInner =  new ArrayList<String>();
				
				StringBuilder sb = new StringBuilder();
				if(!uF.parseToBoolean(hmIsCheque.get(rs.getString("statement_id")))){
					sb.append("<div style=\"float: left;width:100%;\" id=\"myDiv_"+count+"\">");
					sb.append("<div style=\"padding:2px;width:100%;float:left;\"><div style=\"width:120px;float:left\">Cheque No. </div><input id=\"idchqno_"+count+"\" type=\"text\" style=\"width:100px\"></div>");
					sb.append("<div style=\"padding:2px;width:100%;float:left;\"><div style=\"width:120px;float:left\">Dated. </div><input type=\"text\" id=\"idchqdt_"+count+"\" name=\"chequeDate\"style=\"width:100px\"></div>");
					sb.append("<div style=\"padding:2px;width:100%;float:left;\"><div style=\"width:120px;float:left\"> &nbsp;</div><input type=\"button\" value=\"Save\" style=\"width:100px\" onclick=\"getContent('myDiv_"+count+"','UpdateChequeNo.action?chqno='+document.getElementById('idchqno_"+count+"').value+'&chqdt='+document.getElementById('idchqdt_"+count+"').value+'&stmtId="+rs.getString("statement_id")+"')\"></div>");
					sb.append("</div>");
				}
				alInner.add("<div style=\"float: left;width:100%\">Bank statement generated on "+uF.getDateFormat(rs.getString("generated_date"), DBDATE, CF.getStrReportDateFormat())+" for "+uF.showData(hmInnerCurrencyDetails.get("SHORT_CURR"),"")+"&nbsp;<strong>"+Math.round(uF.parseToDouble(rs.getString("payroll_amount")))+"</strong></div>"+sb.toString()+"<a href=\"DownloadBankStatement.action?doc_id="+rs.getString("statement_id")+"\" target=\"_blank\"><i class=\"fa fa-file-o\" aria-hidden=\"true\" title=\"Download\" style=\"float:right\"></i></a>");
				
				alOuter.add(alInner);
				count++;
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("alOuter", alOuter);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void getReimbursementBankStatements(UtilityFunctions uF){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String strPayCycleDates[]=null;
			
			if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("NULL")) {
				strPayCycleDates = getStrPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
				setStrPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			}
			
			con = db.makeConnection(con);
			
			String currId = CF.getOrgCurrencyIdByOrg(con, getOrgId());
			
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			Map<String, String> hmInnerCurrencyDetails = (Map<String, String>) hmCurrencyDetails.get(currId) ;
			if(hmInnerCurrencyDetails==null) hmInnerCurrencyDetails = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from emp_reimbursement er, payroll_bank_statement pbs where pbs.statement_id = er.statement_id " +
					"and er.from_date=? and er.to_date=? and er.emp_id in (select eod.emp_id from employee_personal_details epd," +
					" employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.org_id=?)");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getOrgId()));
			rs = pst.executeQuery();
			Map<String, String> hmIsCheque = new HashMap<String, String>();
			while(rs.next()){
				hmIsCheque.put(rs.getString("statement_id"), rs.getString("is_cheque"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select er.statement_id, statement_name, generated_date, payroll_amount from emp_reimbursement er, " +
					"payroll_bank_statement pbs where pbs.statement_id = er.statement_id and er.from_date=? and er.to_date=? " +
					"and er.emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod where " +
					"epd.emp_per_id=eod.emp_id and eod.org_id=?) group by er.statement_id, statement_name, generated_date, payroll_amount");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getOrgId()));
			rs = pst.executeQuery();
			List<List<String>> alOuter =  new ArrayList<List<String>>();
			int count=0;
			while(rs.next()){
				List<String> alInner =  new ArrayList<String>();
				
				StringBuilder sb = new StringBuilder();
				if(!uF.parseToBoolean(hmIsCheque.get(rs.getString("statement_id")))){
					sb.append("<div style=\"float: left;width:100%;\" id=\"myDiv_"+count+"\">");
					sb.append("<div style=\"padding:2px;width:100%;float:left;\"><div style=\"width:120px;float:left\">Cheque No. </div><input id=\"idchqno_"+count+"\" type=\"text\" style=\"width:100px\"></div>");
					sb.append("<div style=\"padding:2px;width:100%;float:left;\"><div style=\"width:120px;float:left\">Dated. </div><input type=\"text\" id=\"idchqdt_"+count+"\" name=\"chequeDate\"style=\"width:100px\"></div>");
					sb.append("<div style=\"padding:2px;width:100%;float:left;\"><div style=\"width:120px;float:left\"> &nbsp;</div><input type=\"button\" value=\"Save\" style=\"width:100px\" onclick=\"getContent('myDiv_"+count+"','UpdateChequeNo.action?chqno='+document.getElementById('idchqno_"+count+"').value+'&chqdt='+document.getElementById('idchqdt_"+count+"').value+'&stmtId="+rs.getString("statement_id")+"')\"></div>");
					sb.append("</div>");
				}
				alInner.add("<div style=\"float: left;width:100%\">Bank statement generated on "+uF.getDateFormat(rs.getString("generated_date"), DBDATE, CF.getStrReportDateFormat())+" for "+uF.showData(hmInnerCurrencyDetails.get("SHORT_CURR"),"")+"&nbsp;<strong>"+Math.round(uF.parseToDouble(rs.getString("payroll_amount")))+"</strong></div>"+sb.toString()+"<a href=\"DownloadBankStatement.action?doc_id="+rs.getString("statement_id")+"\" target=\"_blank\"><i class=\"fa fa-file-o\" aria-hidden=\"true\" title=\"Download\" style=\"float:right\"></i></a>");
				
				alOuter.add(alInner);
				count++;
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("alOuter", alOuter);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	public void getSalaryBankStatements(UtilityFunctions uF){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagSalBankUploader = uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_SALARY_BANK_UPLOADER));
			
			String strPayCycleDates[]=null;
			
			if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("NULL")) {
				strPayCycleDates = getStrPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
				setStrPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			}
			
			
			String currId = CF.getOrgCurrencyIdByOrg(con, getOrgId());
			
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			Map<String, String> hmInnerCurrencyDetails = (Map<String, String>) hmCurrencyDetails.get(currId) ;
			if(hmInnerCurrencyDetails==null) hmInnerCurrencyDetails = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from payroll_generation pg, payroll_bank_statement pbs where pbs.statement_id = pg.statement_id " +
					"and pg.paid_from=? and pg.paid_to=? and pg.paycycle=? and pg.emp_id in (select eod.emp_id from employee_personal_details epd," +
					" employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.org_id=?)");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
			pst.setInt(4, uF.parseToInt(getOrgId()));
			rs = pst.executeQuery();
			Map<String, String> hmIsCheque = new HashMap<String, String>();
			while(rs.next()){
				hmIsCheque.put(rs.getString("statement_id"), rs.getString("is_cheque"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select pg.statement_id, statement_name, generated_date, payroll_amount,pbs.bank_pay_type,statement_body_excel" +
				",bank_uploader_excel from payroll_generation pg, payroll_bank_statement pbs where pbs.statement_id = pg.statement_id and pg.paid_from=?" +
				" and pg.paid_to=? and pg.paycycle=? and pg.emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id=eod.emp_id and eod.org_id=?) group by pg.statement_id, statement_name, generated_date, payroll_amount," +
				"pbs.bank_pay_type,statement_body_excel,bank_uploader_excel");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
			pst.setInt(4, uF.parseToInt(getOrgId()));
			rs = pst.executeQuery();
			List<List<String>> alOuter =  new ArrayList<List<String>>();
			int count=0;
			while(rs.next()){
				List<String> alInner =  new ArrayList<String>();
				
				StringBuilder sb = new StringBuilder();
				if(!uF.parseToBoolean(hmIsCheque.get(rs.getString("statement_id")))){
					sb.append("<div style=\"float: left;width:100%;\" id=\"myDiv_"+count+"\">");
					sb.append("<div style=\"padding:2px;width:100%;float:left;\"><div style=\"width:120px;float:left\">Cheque No. </div><input id=\"idchqno_"+count+"\" type=\"text\" style=\"width:100px\"></div>");
					sb.append("<div style=\"padding:2px;width:100%;float:left;\"><div style=\"width:120px;float:left\">Dated. </div><input type=\"text\" id=\"idchqdt_"+count+"\" name=\"chequeDate\"style=\"width:100px\"></div>");
					sb.append("<div style=\"padding:2px;width:100%;float:left;\"><div style=\"width:120px;float:left\"> &nbsp;</div><input type=\"button\" value=\"Save\" style=\"width:100px\" onclick=\"getContent('myDiv_"+count+"','UpdateChequeNo.action?chqno='+document.getElementById('idchqno_"+count+"').value+'&chqdt='+document.getElementById('idchqdt_"+count+"').value+'&stmtId="+rs.getString("statement_id")+"')\"></div>");
					sb.append("</div>");
				}
				String strBankPayType = "Primary Account";
				if(uF.parseToInt(rs.getString("bank_pay_type")) == 2){
					strBankPayType = "Secondary Account";
				}
			
//				System.out.println("statement_body_excel ===>> " + rs.getString("statement_body_excel"));
				String pdfLink = "<a href=\"DownloadBankStatement.action?type=Pdf&doc_id="+rs.getString("statement_id")+"\" target=\"_blank\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\" title=\"Download\" style=\"float:right\"></i></a>";
				String excelLink = "";
				if(!flagSalBankUploader && rs.getString("statement_body_excel") !=null && !rs.getString("statement_body_excel").equals("")) {
					excelLink = "<a href=\"DownloadBankStatement.action?type=Excel&doc_id="+rs.getString("statement_id")+"\" target=\"_blank\"><i class=\"fa fa-file-excel-o\" aria-hidden=\"true\" title=\"Download\" style=\"float:right\"></i></a>";
				}
				if(flagSalBankUploader && rs.getString("bank_uploader_excel") !=null && !rs.getString("bank_uploader_excel").equals("")) {
					excelLink = "<a href=\"DownloadBankStatement.action?type=ExcelBU&doc_id="+rs.getString("statement_id")+"\" target=\"_blank\"><i class=\"fa fa-file-excel-o\" aria-hidden=\"true\" title=\"Download Bank Uploader\" style=\"float:right\"></i></a>";
				}
				alInner.add("<div style=\"float:left; width:100%\">Bank statement generated on "+uF.getDateFormat(rs.getString("generated_date"), DBDATE, CF.getStrReportDateFormat())+" from "+strBankPayType+" for "+uF.showData(hmInnerCurrencyDetails.get("SHORT_CURR"),"")+"&nbsp;<strong>"+Math.round(uF.parseToDouble(rs.getString("payroll_amount")))+"</strong></div>"+sb.toString()+""+pdfLink+excelLink);
				
				alOuter.add(alInner);
				count++;
			}
			rs.close();
			pst.close();
//			System.out.println("alOuter ===>> " + alOuter);
			
			request.setAttribute("alOuter", alOuter);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrPaycycle() {
		return strPaycycle;
	}

	public void setStrPaycycle(String strPaycycle) {
		this.strPaycycle = strPaycycle;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getSalaryHead() {
		return salaryHead;
	}

	public void setSalaryHead(String salaryHead) {
		this.salaryHead = salaryHead;
	}

	public String getStrStartDate() {
		return strStartDate;
	}

	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}

	public String getStrEndDate() {
		return strEndDate;
	}

	public void setStrEndDate(String strEndDate) {
		this.strEndDate = strEndDate;
	}
}