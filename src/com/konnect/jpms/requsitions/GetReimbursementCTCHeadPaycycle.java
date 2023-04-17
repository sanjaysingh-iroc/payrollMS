package com.konnect.jpms.requsitions;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetReimbursementCTCHeadPaycycle extends ActionSupport implements IStatements, ServletRequestAware {

	private static final long serialVersionUID = 6483180990145887248L;


	HttpSession session;
	private CommonFunctions CF;
	String strSessionEmpId;
	
	String financialYear;
	String reimbursementCTCHead;
	List<FillPayCycles> paycycleList;	
	String limitAmount;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		getGetPerkSalaryPaycycle(uF);
		
		return SUCCESS;		
	}
	
	private void getGetPerkSalaryPaycycle(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {				
				String[] strFinancialYear = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			con = db.makeConnection(con);
			
			String orgId = CF.getEmpOrgId(con, uF, strSessionEmpId);
			String levelId = CF.getEmpLevelId(con, strSessionEmpId);
			
			Calendar cal = GregorianCalendar.getInstance();
            cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "dd")));
            cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM")) - 1);
            cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
          
            if(uF.parseToInt(getReimbursementCTCHead()) > 0){
	            List<Date> alDate = new ArrayList<Date>();
				for (int i = 1; i <= 12; i++){
					int nMonthStart = cal.getActualMinimum(Calendar.DATE);
					int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
					int nMonth = (cal.get(Calendar.MONTH) + 1);
					
					String strDateStart =  (nMonthStart <10 ? "0"+nMonthStart : nMonthStart)+"/"+(nMonth <10 ? "0"+nMonth : nMonth)+"/"+cal.get(Calendar.YEAR);
					
					alDate.add(uF.getDateFormat(strDateStart, DATE_FORMAT));
					
					cal.add(Calendar.MONTH, 1);
				}
				Collections.reverse(alDate);
				
		        Date date2 = uF.getCurrentDate(CF.getStrTimeZone());
		        paycycleList = new ArrayList<FillPayCycles>();
				for(Date ad : alDate){
					String strDateStart = uF.getDateFormat(""+ad, DBDATE, DATE_FORMAT);
					String[] strPayCycleDates = CF.getPayCycleFromDate(con, strDateStart, CF.getStrTimeZone(), CF, orgId);
					
					Date date1 = uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT);
					
					if(date1.before(date2) || date1.equals(date2)){
						
						pst = con.prepareStatement("select * from reimbursement_head_details rhd,reimbursement_assign_head_details rsad " +
								"where rhd.reimbursement_head_id = rsad.reimbursement_head_id and rsad.status=true and rsad.trail_status=true " +
								"and rhd.reimbursement_head_id in (select reimbursement_head_id from reimbursement_head_amt_details where is_attachment=true " +
								"and financial_year_start=? and financial_year_end=?) and rsad.emp_id=? and rsad.level_id=? and rsad.org_id=? " +
								"and rsad.paycycle_from=? and rsad.paycycle_to=? and rsad.paycycle=? and rhd.reimbursement_head_id=? " +
								"and rhd.reimbursement_ctc_id in (select reimbursement_ctc_id from reimbursement_ctc_details where level_id=? and org_id=?)");
						pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setInt(3, uF.parseToInt(strSessionEmpId));
						pst.setInt(4, uF.parseToInt(levelId));
						pst.setInt(5, uF.parseToInt(orgId));
						pst.setDate(6, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
						pst.setDate(7, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
						pst.setInt(8, uF.parseToInt(strPayCycleDates[2]));
						pst.setInt(9, uF.parseToInt(getReimbursementCTCHead()));
						pst.setInt(10, uF.parseToInt(levelId));
						pst.setInt(11, uF.parseToInt(orgId));
						rs = pst.executeQuery();
						boolean flag = false;
						while(rs.next()){
							flag = true;
						} 
						rs.close();
						pst.close();
						
						if(flag){
							paycycleList.add(new FillPayCycles(strPayCycleDates[0]+"-"+strPayCycleDates[1]+"-"+strPayCycleDates[2], "Pay Cycle " + strPayCycleDates[2] + ", " + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat())));
						}
					} else {
						paycycleList.add(new FillPayCycles(strPayCycleDates[0]+"-"+strPayCycleDates[1]+"-"+strPayCycleDates[2], "Pay Cycle " + strPayCycleDates[2] + ", " + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat())));
					}
				}
				
				pst = con.prepareStatement("select * from reimbursement_head_amt_details rhad, reimbursement_head_details rhd " +
						"where rhad.reimbursement_head_id=rhd.reimbursement_head_id and rhd.reimbursement_head_id=? and rhad.financial_year_start=? " +
						"and rhad.financial_year_end=? and rhd.level_id=? and rhd.org_id=?");
				pst.setInt(1, uF.parseToInt(getReimbursementCTCHead()));
				pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(levelId));
				pst.setInt(5, uF.parseToInt(orgId));
				rs = pst.executeQuery();
				double reimHeadAmount = 0.0d;
				while(rs.next()){
					reimHeadAmount = uF.parseToDouble(rs.getString("amount"));
				} 
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from reimbursement_ctc_applied_details where emp_id=? and is_approved in (0,1) " +
						"and reimbursement_head_id=? and financial_year_start=? and financial_year_end=?");
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setInt(2, uF.parseToInt(getReimbursementCTCHead()));
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
				double reimHeadAppliedAmount = 0.0d;
				while(rs.next()){
					reimHeadAppliedAmount = uF.parseToDouble(rs.getString("applied_amount"));
				} 
				rs.close();
				pst.close();
				
				double dblAmount = reimHeadAmount - reimHeadAppliedAmount;
				
				setLimitAmount(""+(dblAmount > 0 ? dblAmount : 0));
				
            } else {
            	 paycycleList = new ArrayList<FillPayCycles>();
            }
			
			
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
		this.request=request;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public String getReimbursementCTCHead() {
		return reimbursementCTCHead;
	}

	public void setReimbursementCTCHead(String reimbursementCTCHead) {
		this.reimbursementCTCHead = reimbursementCTCHead;
	}

	public String getLimitAmount() {
		return limitAmount;
	}

	public void setLimitAmount(String limitAmount) {
		this.limitAmount = limitAmount;
	}
	
}