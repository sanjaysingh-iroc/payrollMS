package com.konnect.jpms.requsitions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetPerkLimit extends ActionSupport  implements ServletRequestAware, IStatements {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null; 
	String strSessionEmpId = null;
	 
	private CommonFunctions CF;

	String strMonth;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
				
		String strTypeId = request.getParameter("typeId");
//		System.out.println("strTypeId ===>> " + strTypeId);
		
		getPerkLimit(strTypeId);
		
		return SUCCESS;
	}
	
	
	public int getPerkLimit(String strTypeId){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int nRequisitionId=0;
		
		try{
			
			String strId = (String) request.getParameter("strId");
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from perk_details where perk_id =?");
			pst.setInt(1, uF.parseToInt(strTypeId));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			double dblLimit = 0.0d;
			String strPayCycle = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null; 
			while(rs.next()){
				dblLimit = rs.getDouble("max_amount");
				strPayCycle = rs.getString("perk_payment_cycle");
				
				strFinancialYearStart = uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, DATE_FORMAT);
				strFinancialYearEnd = uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, DATE_FORMAT);
			}
			rs.close();
			pst.close();
			
			
			StringBuilder sb = new StringBuilder();
			if(strPayCycle!=null && strPayCycle.equalsIgnoreCase("M")){
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select sum(perk_amount) as perk_amount from emp_perks where financial_year_start=? and financial_year_end=? " +
						"and emp_id=? and approval_1>=0 and approval_2>=0 and perk_type_id in (select perk_id from perk_details where financial_year_start=? and financial_year_end=?" +
						" and perk_payment_cycle='M' and perk_id = ?) and perk_month=? ");
				if(uF.parseToInt(strId) > 0){
					sbQuery.append(" and perks_id not in("+uF.parseToInt(strId)+") ");
				}				
				sbQuery.append(" group by emp_id, perk_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
				pst.setDate(4,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(5,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(6, uF.parseToInt(strTypeId));
				pst.setInt(7, uF.parseToInt(getStrMonth()));
//				System.out.println("pst M ==>"+pst);
				rs = pst.executeQuery();
				double dblAppliedLimit = 0.0d;
				while(rs.next()){
					dblAppliedLimit = uF.parseToDouble(rs.getString("perk_amount"));
				}
				rs.close();
				pst.close();
				double dblRemaining = dblLimit - dblAppliedLimit;
				
				sb.append("<div id=\"limit\" style=\"display:none\">"+dblRemaining+"</div>Your monthly limit is "+uF.formatIntoTwoDecimal(dblLimit)+".");
				sb.append(" You have applied "+uF.formatIntoTwoDecimal(dblAppliedLimit)+".");
				sb.append(" You can apply upto "+uF.formatIntoTwoDecimal(dblRemaining)+".");
			}else if(strPayCycle!=null && strPayCycle.equalsIgnoreCase("A")){
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select sum(perk_amount) as perk_amount from emp_perks where financial_year_start=? and financial_year_end=? " +
						"and emp_id=? and approval_1>=0 and approval_2>=0 and perk_type_id in (select perk_id from perk_details where financial_year_start=? and financial_year_end=?" +
						" and perk_payment_cycle='A' and perk_id = ?) ");
				if(uF.parseToInt(strId) > 0){
					sbQuery.append(" and perks_id not in("+uF.parseToInt(strId)+") ");
				}				
				sbQuery.append(" group by emp_id, perk_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
				pst.setDate(4,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(5,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(6, uF.parseToInt(strTypeId));
//				System.out.println("pst A ==>"+pst);
				rs = pst.executeQuery();
				double dblAppliedLimit = 0.0d;
				while(rs.next()){
					dblAppliedLimit = uF.parseToDouble(rs.getString("perk_amount"));
				}
				rs.close();
				pst.close();
				double dblRemaining = dblLimit - dblAppliedLimit;
				
				
				sb.append("<div id=\"limit\" style=\"display:none\">"+dblRemaining+"</div>Your annual limit is "+uF.formatIntoTwoDecimal(dblLimit)+".");
				sb.append(" You have applied "+uF.formatIntoTwoDecimal(dblAppliedLimit)+".");
				sb.append(" You can apply upto "+uF.formatIntoTwoDecimal(dblRemaining)+".");
			}
			
			request.setAttribute("PERK_LIMIT", sb.toString());
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return nRequisitionId;
	}
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}
	
}
