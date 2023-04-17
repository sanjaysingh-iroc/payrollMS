package com.konnect.jpms.itforms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Form5 extends ActionSupport implements ServletRequestAware, IStatements {

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
	String strMonth;
	
	String f_org;
	List<FillOrganisation> orgList;
	List<FillFinancialYears> financialYearList; 
	List<FillMonth> monthList;
	
	public String execute() throws Exception {
		
				
		request.setAttribute(PAGE, PForm5);
		request.setAttribute(TITLE, TForm5);
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		if(getStrMonth() == null){
			setStrMonth("1");
		}
		if(getF_org() == null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		viewForm5();
		
		return loadForm5();
	}
	
	
	public String loadForm5(){
		UtilityFunctions uF = new UtilityFunctions();
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		orgList = new FillOrganisation(request).fillOrganisation();
		monthList = new FillMonth().fillMonth();
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
		if(getF_org()!=null) {
			String strOrg="";
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					strOrg=orgList.get(i).getOrgName();
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
		
		alFilter.add("MONTH");
		if(getStrMonth()!=null) {
			String strMonth="";
			for(int i=0;monthList!=null && i<monthList.size();i++) {
				if(getStrMonth().equals(monthList.get(i).getMonthId())) {
					strMonth=monthList.get(i).getMonthName();
				}
			}
			if(strMonth!=null && !strMonth.equals("")) {
				hmFilter.put("MONTH", strMonth);
			} else {
				hmFilter.put("MONTH", "Select Month");
			}
		} else {
			hmFilter.put("MONTH", "Select Month");
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	public String viewForm5(){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			String strMonth = null;

			
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

			int nselectedMonth = uF.parseToInt(getStrMonth());
			int nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM"));
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
			if(nselectedMonth>=nFYSMonth){
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			}else{
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "yyyy")));
			}
			
			
			int nMonthStart = cal.getActualMinimum(Calendar.DATE);
			int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
			
			String strDateStart =  nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
			String strDateEnd =  nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
			
			strMonth = uF.getDateFormat(strDateStart, DATE_FORMAT, "MMMM - yyyy");
			
			
			
			List<List<String>> alList = new ArrayList<List<String>>();
			Map<String, String> hmEmpFamilyDetails = new HashMap<String, String>();
			Map<String, String> hmEmpPrevEmploymentDetails = new HashMap<String, String>();
			
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
			
			pst = con.prepareStatement("select * from emp_prev_employment where emp_id in (select epd.emp_per_id from employee_personal_details epd," +
					"employee_official_details eod  where epd.emp_per_id=eod.emp_id and epd.joining_date between ? and ? and eod.org_id=?) order by emp_id, to_date desc");
			pst.setDate(1, uF.getDateFormat(strDateStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDateEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_org()));
			rs= pst.executeQuery();
//			System.out.println("pst=== 1 ===>"+pst);
			String strEmpIdNew=null;
			String strEmpIdOld=null;
			while(rs.next()){
				strEmpIdNew = rs.getString("emp_id");
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					hmEmpPrevEmploymentDetails.put(rs.getString("emp_id"), rs.getString("to_date"));
				}
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from emp_family_members where emp_id in (select epd.emp_per_id from employee_personal_details epd," +
					"employee_official_details eod  where epd.emp_per_id=eod.emp_id and epd.joining_date between ? and ? and eod.org_id=?) order by emp_id");
			pst.setDate(1, uF.getDateFormat(strDateStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDateEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_org()));
			rs= pst.executeQuery();
//			System.out.println("pst=== 2 ===>"+pst);
			while(rs.next()){
				if(rs.getString("member_type")!=null && rs.getString("member_type").equalsIgnoreCase("SPOUSE")){
					hmEmpFamilyDetails.put(rs.getString("emp_id")+"_SPOUSE", rs.getString("member_name"));
				}else if(rs.getString("member_type")!=null && rs.getString("member_type").equalsIgnoreCase("FATHER")){
					hmEmpFamilyDetails.put(rs.getString("emp_id")+"_FATHER", rs.getString("member_name"));
				}				
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from employee_personal_details epd,employee_official_details eod " +
					"where epd.emp_per_id=eod.emp_id and epd.joining_date between ? and ? and eod.org_id=?");
			pst.setDate(1, uF.getDateFormat(strDateStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDateEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_org()));
//			System.out.println("pst====3====="+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				List<String> alInner = new ArrayList<String>();
				
				alInner.add(rs.getString("emp_pf_no"));
				
				String strMiddleName = "";
				
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strMiddleName = " "+rs.getString("emp_mname");
					}
				}
				
				alInner.add(rs.getString("emp_fname")+strMiddleName+" "+rs.getString("emp_lname"));
				if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("M")){
					alInner.add(hmEmpFamilyDetails.get(rs.getString("emp_per_id")+"_FATHER"));
				}else if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("F") && rs.getString("marital_status")!=null && rs.getString("marital_status").equalsIgnoreCase("M")){
					alInner.add(hmEmpFamilyDetails.get(rs.getString("emp_per_id")+"_SPOUSE"));
				}else{
					alInner.add(hmEmpFamilyDetails.get(rs.getString("emp_per_id")+"_FATHER"));
				}
				
				alInner.add(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(rs.getString("emp_gender"));
				alInner.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(uF.getDateFormat(hmEmpPrevEmploymentDetails.get(rs.getString("emp_per_id")), DBDATE, CF.getStrReportDateFormat()));
				alInner.add("");
				
				alList.add(alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmOrg", hmOrg);
			request.setAttribute("alList", alList);
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("strMonth", strMonth);
			
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


	public String getStrMonth() {
		return strMonth;
	}


	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}


	public List<FillMonth> getMonthList() {
		return monthList;
	}


	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
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

}
