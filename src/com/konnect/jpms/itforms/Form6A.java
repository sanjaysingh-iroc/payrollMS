package com.konnect.jpms.itforms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Form6A extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	String strSessionEmpId;
	
	String strSubmit;
	String financialYear;
	List<FillFinancialYears> financialYearList; 
	
	String f_org;
	String f_level;
	List<FillOrganisation> orgList;
	List<FillLevel> levelList;
	
	public String execute() throws Exception {
				
		request.setAttribute(PAGE, PForm6A);
		request.setAttribute(TITLE, TForm6A);
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(getF_org() == null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		viewForm6A(uF);
		
		return loadForm6A(uF);

	}
	
	
	public String loadForm6A(UtilityFunctions uF){
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		orgList = new FillOrganisation(request).fillOrganisation();
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("FINANCIALYEAR");
		String[] strFinancialYears = null;
		if (getFinancialYear() != null) {
			strFinancialYears = getFinancialYear().split("-");
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		} else {
			strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		}
		hmFilter.put("FINANCIALYEAR", uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=orgList.get(i).getOrgName();
					} else {
						strOrg+=", "+orgList.get(i).getOrgName();
					}
					k++;
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
			
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
		
		alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				if(getF_level().equals(levelList.get(i).getLevelId())) {
					strLevel=levelList.get(i).getLevelCodeName();
				}
			}
			if(strLevel!=null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Levels");
			}
		} else {
			hmFilter.put("LEVEL", "All Levels");
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	public String viewForm6A(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			Map<String, Map<String, String>> hmEmployeeDetails = new HashMap<String, Map<String, String>>();
			
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null; 

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
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("select * from org_details where org_id = ? ");
			pst.setInt(1, uF.parseToInt(getF_org()));
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
				hmOrg.put("ORG_ESTABLISH_CODE_NO", rs.getString("establish_code_no"));
			}
			rs.close();
			pst.close();  
			
			pst = con.prepareStatement("select * from employee_personal_details");
			rs = pst.executeQuery();
			while(rs.next()){
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("EPF_ACC_NO", rs.getString("emp_pf_no"));
			
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				hmInner.put("NAME", rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				
				hmEmployeeDetails.put(rs.getString("emp_per_id"), hmInner);
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end=? and org_id = ? and level_id=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_org()));
			pst.setInt(4, uF.parseToInt(getF_level()));
//			System.out.println("pst==="+pst);
			rs = pst.executeQuery();
			double dblEEPFContribution=0;
			double dblERPFContribution=0;
			double dblERPF_MAX_Contribution=0;
			double dblEEPDContribution=0;
			double dblEDLIContribution=0;
			double dblEEPFADMIN=0;
			double dblEDLIADMIN=0;
			String salary_head_id = null;
			while(rs.next()){
				dblEEPFContribution= rs.getDouble("eepf_contribution");
				dblERPF_MAX_Contribution = rs.getDouble("epf_max_limit");
				dblERPFContribution= rs.getDouble("erpf_contribution");
				dblEEPDContribution= rs.getDouble("erps_contribution");
				dblEDLIContribution= rs.getDouble("erdli_contribution");
				dblEEPFADMIN= rs.getDouble("pf_admin_charges");
				dblEDLIADMIN= rs.getDouble("edli_admin_charges");
				
				salary_head_id = rs.getString("salary_head_id");
			}
			rs.close();
			pst.close();
			
			
			double dblTotalContribution = dblERPFContribution + dblEEPDContribution + dblEDLIContribution + dblEEPFADMIN + dblEDLIADMIN;
			double dblDIFFContribution = dblEEPDContribution + dblEDLIContribution + dblEEPFADMIN + dblEDLIADMIN;
			
			request.setAttribute("TOTAL_PER", uF.formatIntoTwoDecimal(dblTotalContribution));
			request.setAttribute("EPF_PER", uF.formatIntoTwoDecimal(dblEEPFContribution));
			request.setAttribute("ERPF_PER", uF.formatIntoTwoDecimal(dblERPFContribution));
			request.setAttribute("ERPS_PER", uF.formatIntoTwoDecimal(dblEEPDContribution));
			request.setAttribute("DIFF_PER", uF.formatIntoTwoDecimal(dblDIFFContribution));
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution," +
					"sum(erps_contribution) as erps_contribution,sum(erdli_contribution) as erdli_contribution,sum(pf_admin_charges) as pf_admin_charges," +
					"sum(edli_admin_charges) as edli_admin_charges  from emp_epf_details where financial_year_start = ? and financial_year_end = ? " +
					"and emp_id in (");
			sbQuery.append("select emp_id from challan_details where financial_year_from_date = ? and financial_year_to_date = ? and challan_type=? ");
			sbQuery.append(" and  month in (");
            for(int i=1; i<=12; i++){
            	if(i==1){
            		sbQuery.append("'"+i+"'");
            	} else {
            		sbQuery.append(",'"+i+"'");
            	}
            }
            sbQuery.append(") ");
			sbQuery.append(" and is_paid=true and emp_id in (select eod.emp_id from employee_personal_details epd,employee_official_details eod " +
					"where eod.emp_id = epd.emp_per_id and org_id=? and eod.grade_id in (select grade_id from designation_details dd, level_details ld, " +
					"grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id =?))");
			sbQuery.append(") group by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, EMPLOYEE_EPF);
			pst.setInt(6, uF.parseToInt(getF_org()));
			pst.setInt(7, uF.parseToInt(getF_level()));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmDetails = new HashMap<String, Map<String, String>>();
			Map<String, String> hmDetailsTotal = new HashMap<String, String>();
			double dblTotalEmployeeAmount = 0;
			double dblTotalEmployerEPFAmount = 0;  
			double dblTotalEmployerEPSAmount = 0;
			while(rs.next()){
				Map<String, String> hmInner = (Map<String, String>)hmDetails.get(rs.getString("emp_id"));
				if(hmInner==null) hmInner = new HashMap<String, String>();

				hmInner.put("EMPLOYEE_SHARE", uF.formatIntoTwoDecimal(rs.getDouble("eepf_contribution")));
				dblTotalEmployeeAmount += rs.getDouble("eepf_contribution") ;
				hmDetailsTotal.put("TOTAL_EMPLOYEE_SHARE", uF.formatIntoTwoDecimal(dblTotalEmployeeAmount));
				
				double dblEPF = rs.getDouble("eepf_contribution");
				double dblERPF = rs.getDouble("erpf_contribution");  
				double dblEEPS = rs.getDouble("erps_contribution");
				double dblEDLI = rs.getDouble("erdli_contribution");
				double dblEPFADMIN = rs.getDouble("pf_admin_charges");
				double dblEDLADMIN = rs.getDouble("edli_admin_charges");
				
//				double dblEmployerShare = dblERPF + dblEEPS + dblEDLI + dblEPFADMIN + dblEDLADMIN;
				double dblEmployerShare = dblEPF - dblEEPS;
					
				hmInner.put("EMPLOYER_SHARE_EPF", uF.formatIntoTwoDecimal(dblEmployerShare));
				dblTotalEmployerEPFAmount += dblEmployerShare ;
				hmDetailsTotal.put("TOTAL_EMPLOYER_SHARE_EPF", uF.formatIntoTwoDecimal(dblTotalEmployerEPFAmount));
					
				hmInner.put("EMPLOYER_SHARE_EPS", uF.formatIntoTwoDecimal(dblEEPS));
				dblTotalEmployerEPSAmount += dblEEPS;
				hmDetailsTotal.put("TOTAL_EMPLOYER_SHARE_EPS", uF.formatIntoTwoDecimal(dblTotalEmployerEPSAmount));
				
				
				hmDetails.put(rs.getString("emp_id"), hmInner);
			}
			rs.close();
			pst.close(); 
			
			Map<String, String> hmEarningTotal = new HashMap<String, String>();
			if(salary_head_id!=null && !salary_head_id.equals("")){
				salary_head_id = salary_head_id.substring(0,salary_head_id.length()-1);
				
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(amount) as amount,emp_id from payroll_generation where financial_year_from_date = ? and financial_year_to_date = ? " +
						"and is_paid =true and salary_head_id in("+salary_head_id+") and emp_id in (");
				sbQuery.append("select emp_id from challan_details where financial_year_from_date = ? and financial_year_to_date = ? and challan_type=? ");
				sbQuery.append(" and  month in (");
	            for(int i=1; i<=12; i++){
	            	if(i==1){
	            		sbQuery.append("'"+i+"'");
	            	} else {
	            		sbQuery.append(",'"+i+"'");
	            	}
	            }
	            sbQuery.append(") ");
				sbQuery.append(" and is_paid=true and emp_id in (select eod.emp_id from employee_personal_details epd,employee_official_details eod " +
						"where eod.emp_id = epd.emp_per_id and org_id=? and eod.grade_id in (select grade_id from designation_details dd, level_details ld, " +
					"grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id =?))");
				sbQuery.append(")  group by emp_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, EMPLOYEE_EPF);
				pst.setInt(6, uF.parseToInt(getF_org()));
				pst.setInt(7, uF.parseToInt(getF_level()));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				
				double dblTotalEarningAmount = 0;
				while(rs.next()){
					hmEarningTotal.put(rs.getString("emp_id"), uF.formatIntoTwoDecimal(rs.getDouble("amount")));
					
					dblTotalEarningAmount += rs.getDouble("amount") ;
					hmDetailsTotal.put("TOTAL_EARNING", uF.formatIntoTwoDecimal(dblTotalEarningAmount));
					
				}
				rs.close();
				pst.close(); 
				
			}
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmDetails", hmDetails);
			request.setAttribute("hmEarningTotal", hmEarningTotal);
			request.setAttribute("hmDetailsTotal", hmDetailsTotal);
			request.setAttribute("hmEmployeeDetails", hmEmployeeDetails);
			request.setAttribute("hmOrg",hmOrg);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return SUCCESS;
		
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrSubmit() {
		return strSubmit;
	}

	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}


	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public String getF_org() {
		return f_org;
	}


	public void setF_org(String f_org) {
		this.f_org = f_org;
	}


	public List<FillOrganisation> getOrgList() {
		return orgList;
	}


	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}


	public String getF_level() {
		return f_level;
	}


	public void setF_level(String f_level) {
		this.f_level = f_level;
	}


	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

}
