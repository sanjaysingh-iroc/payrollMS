package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetCalculatedContributions extends ActionSupport implements ServletRequestAware, IStatements{

	private static final long serialVersionUID = 5947893602821384559L;

	private String strUserType;
	private String strSessionEmpId;
	
	HttpSession session; 
	public CommonFunctions CF; 
	private String empId;
	private String salHeadsAndAmt;
	private String contributeHeads;
	private String effectiveDate;
	private String CandID;
	private String recruitId;
	
	public String execute()	{
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		calculateContributions(uF);
		
		return SUCCESS;
	}

	
	private void calculateContributions(UtilityFunctions uF) {
		// TODO Auto-generated method stub
		
		List<String> alContriHeads = Arrays.asList(getContributeHeads().split(","));
		List<String> alSalHeadsAndAmt = Arrays.asList(getSalHeadsAndAmt().split(","));
		
//		System.out.println("alContriHeads ===>> " + alContriHeads);
//		System.out.println("alSalHeadsAndAmt ===>> " + alSalHeadsAndAmt);
		
		Map<String, String> hmTotal = new HashMap<String, String>();
		for(int i=0; alSalHeadsAndAmt != null && i<alSalHeadsAndAmt.size(); i++) {
			if(alSalHeadsAndAmt.get(i).length()>1) {
				String[] strTemp = alSalHeadsAndAmt.get(i).split("::::");
					hmTotal.put(strTemp[0], strTemp[1]);
			}
		}
		
		if(alContriHeads != null && alContriHeads.contains(EMPLOYER_EPF+"")) {
			double dblERPF = calculateERPF(uF, hmTotal, getEmpId(), getCandID());
//			System.out.println("dblERPF ===>> " + dblERPF);
			request.setAttribute("dblERPF", uF.formatIntoFourDecimalWithOutComma(dblERPF));
		}
		if(alContriHeads != null && alContriHeads.contains(EMPLOYER_ESI+"")) {
//			calculateERPF(uF, null, getEmpId());
		}
		if(alContriHeads != null && alContriHeads.contains(EMPLOYER_LWF+"")) {
//			calculateERPF(uF, null, getEmpId());
		}
	}


	public double calculateERPF(UtilityFunctions uF, Map<String, String> hmTotal, String strEmpId, String CandID) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		double dblEPS1 = 0;
		double dblEPS = 0;
		double dblEPF = 0;
		double dblEDLI = 0;
		
		double dblEPFAdmin = 0;
		double dblEDLIAdmin = 0;
		
		double dblTotalEPF = 0;
		double dblTotalEDLI = 0;
		
		Database db = null;
		try {
			if(con==null) {
				db = new Database();
				db.setRequest(request);
				con = db.makeConnection(con);
			}
			
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			
			String[] strFinancialYear = CF.getFinancialYear(con, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT), CF, uF);
			if(getEffectiveDate() != null && !getEffectiveDate().equals("")) {
				strFinancialYear = CF.getFinancialYear(con, getEffectiveDate(), CF, uF);
			}
			if(strFinancialYear!=null) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			if(uF.parseToInt(CandID)>0) {
				pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? and org_id in (select " +
					"org_id from recruitment_details where recruitment_id=?) and level_id in (select level_id from recruitment_details where recruitment_id=?)");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getRecruitId()));	
				pst.setInt(4, uF.parseToInt(getRecruitId()));
			} else {
				pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? " +
					"and org_id in (select org_id from employee_official_details where emp_id=?) and level_id in (select ld.level_id from grades_details gd, " +
					"level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  " +
					"and gd.grade_id in (select grade_id from employee_official_details where emp_id=? ))");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strEmpId));	
				pst.setInt(4, uF.parseToInt(strEmpId));
			}
			 
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			double dblERPFAmount = 0;
			double dblERPSAmount = 0;
			double dblERDLIAmount = 0;
			double dblPFAdminAmount = 0;
			double dblEDLIAdminAmount = 0;
			double dblEPFMaxAmount = 0;
			double dblEPRMaxAmount = 0;
			double dblEPSMaxAmount = 0;
			double dblEDLIMaxAmount = 0;
			String strSalaryHeads = null;
			
			boolean erpfContributionchbox = false;
			boolean erpsContributionchbox = false;
			boolean pfAdminChargeschbox = false;
			boolean edliAdminChargeschbox = false;
			boolean erdliContributionchbox = false;
			while(rs.next()) {   
				
				dblERPFAmount = rs.getDouble("erpf_contribution");
				dblERPSAmount = rs.getDouble("erps_contribution");
				dblERDLIAmount = rs.getDouble("erdli_contribution");
				dblPFAdminAmount = rs.getDouble("pf_admin_charges");
				dblEDLIAdminAmount = rs.getDouble("edli_admin_charges");
				
				dblEPRMaxAmount = rs.getDouble("erpf_max_limit");
				dblEPFMaxAmount = rs.getDouble("epf_max_limit");
				dblEPSMaxAmount = rs.getDouble("eps_max_limit");
				dblEDLIMaxAmount = rs.getDouble("edli_max_limit");
				
				strSalaryHeads = rs.getString("salary_head_id");
				
				
				erpfContributionchbox = rs.getBoolean("is_erpf_contribution");
				erpsContributionchbox = rs.getBoolean("is_erps_contribution");
				pfAdminChargeschbox = rs.getBoolean("is_pf_admin_charges");
				edliAdminChargeschbox = rs.getBoolean("is_edli_admin_charges");
				erdliContributionchbox = rs.getBoolean("is_erdli_contribution");
				
				strSalaryHeads = rs.getString("salary_head_id");
			}
			rs.close();
			pst.close();

//			System.out.println("hmTotal=========>"+hmTotal);
//			System.out.println("strSalaryHeads=========>"+strSalaryHeads);
			
			String []arrSalaryHeads = null;
			if(strSalaryHeads!=null) {
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			double dblAmount = 0;
			double dblAmountERPF = 0;
			double dblAmountEEPF = 0;
			double dblAmountERPS = 0;
			double dblAmountERPS1 = 0;
			double dblAmountEREDLI = 0;
			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
			}
			
			
//			System.out.println("dblAmount===="+dblAmount); 
//			System.out.println("dblEPFMaxAmount===="+dblEPFMaxAmount);
			
			if(dblAmount>=dblEPRMaxAmount) {
				dblAmountERPF = dblEPRMaxAmount;
			} else {
				dblAmountERPF = dblAmount;
			}
			
			if(dblAmount>=dblEPFMaxAmount){
				dblAmountEEPF = dblEPFMaxAmount;
			} else {
				dblAmountEEPF = dblAmount;
			}
			
			
			dblAmountERPS1 = dblAmount;
			if(dblAmount>=dblEPSMaxAmount){
				dblAmountERPS = dblEPSMaxAmount;
			}else{
				dblAmountERPS = dblAmount;
			}
			
			if(dblAmount>=dblEDLIMaxAmount){
				dblAmountEREDLI = dblEDLIMaxAmount;
			}else{
				dblAmountEREDLI = dblAmount;
			}
			
			
			if(erpfContributionchbox) {
//				System.out.println("erpfContributionchbox====");
				dblEPF = (( dblERPFAmount * dblAmountERPF ) / 100);
			}
//				System.out.println("erpfContributionchbox====dblERPFAmount==>"+dblERPFAmount+"====dblAmountERPF==>"+dblAmountERPF+"====dblEPF==>"+dblEPF);
			if(erpsContributionchbox){
				dblEPS = (( dblERPSAmount * dblAmountERPS ) / 100);
				dblEPS1 = (( dblERPSAmount * dblAmountERPS1 ) / 100);
			}
//				System.out.println("erpsContributionchbox====dblERPSAmount==>"+dblERPSAmount+"====dblAmountERPS==>"+dblAmountERPS+"====dblEPF==>"+dblEPS);	
			if(erdliContributionchbox){
				dblEDLI = (( dblERDLIAmount * dblAmountEREDLI ) / 100);
			}
//				System.out.println("erdliContributionchbox====dblERDLIAmount==>"+dblERDLIAmount+"====dblAmountEREDLI==>"+dblAmountEREDLI+"====dblEDLI==>"+dblEDLI);
			
			if(edliAdminChargeschbox){
				dblEDLIAdmin = (( dblEDLIAdminAmount * dblAmountEREDLI ) / 100);
			}
//				System.out.println("edliAdminChargeschbox====dblEDLIAdminAmount==>"+dblEDLIAdminAmount+"====dblAmountEREDLI==>"+dblAmountEREDLI+"====dblEDLIAdmin==>"+dblEDLIAdmin);
			
			if(pfAdminChargeschbox){
				dblEPFAdmin = (( dblPFAdminAmount * dblAmountEEPF ) / 100);
			}
//				System.out.println("pfAdminChargeschbox====dblPFAdminAmount==>"+dblPFAdminAmount+"====dblAmountEEPF==>"+dblAmountEEPF+"====dblEPFAdmin==>"+dblEPFAdmin);

			if(CF.isEPF_Condition1()){
//				System.out.println("isEPF_Condition1====");
				dblEPF += dblEPS1 - dblEPS;
			}
			
			dblTotalEDLI = dblEDLI + dblEDLIAdmin;
			dblTotalEPF = dblEPF + dblEPS + dblEPFAdmin;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(db!=null) {
				db.closeConnection(con);
			}
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return (dblTotalEPF + dblTotalEDLI);
	}
	
	
	public HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getSalHeadsAndAmt() {
		return salHeadsAndAmt;
	}

	public void setSalHeadsAndAmt(String salHeadsAndAmt) {
		this.salHeadsAndAmt = salHeadsAndAmt;
	}

	public String getContributeHeads() {
		return contributeHeads;
	}

	public void setContributeHeads(String contributeHeads) {
		this.contributeHeads = contributeHeads;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getCandID() {
		return CandID;
	}

	public void setCandID(String candID) {
		CandID = candID;
	}

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

}