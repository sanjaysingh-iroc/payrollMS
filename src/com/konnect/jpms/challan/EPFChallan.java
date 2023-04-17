package com.konnect.jpms.challan;

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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.payroll.reports.EPFSalaryReport;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EPFChallan extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	String strBaseUserType = null;
	
	private String strLocation;
	private String strDepartment;
	private String strSbu;
	private String strLevel;
	private String strEmpType;
	
	String f_org;
	String financialYear;
	String strEmpId;
	String strMonth; 
	
	private String[] f_strWLocation;
	private String[] f_department;
	private String[] f_level;
	private String[] f_service;
	private String[] f_emptype;
	
	private List<FillWLocation> wLocationList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	private List<FillServices> serviceList;
	private List<FillEmploymentType> empTypeList;
	
	List<FillOrganisation> orgList;
	List<FillFinancialYears> financialYearList;
	List<FillMonth> monthList;
	
	UtilityFunctions uF = new UtilityFunctions();

	private static Logger log = Logger.getLogger(EPFSalaryReport.class);

	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		
		request.setAttribute(TITLE, "EPF Challan ");
		request.setAttribute(PAGE, "/jsp/challan/EPFChallan.jsp");
 
		if(uF.parseToInt(getF_org()) == 0) {
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(getStrLocation() != null && !getStrLocation().equals("") && !getStrLocation().equalsIgnoreCase("NULL")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("") && !getStrDepartment().equalsIgnoreCase("NULL")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		if(getStrSbu() != null && !getStrSbu().equals("") && !getStrSbu().equalsIgnoreCase("NULL")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		if(getStrLevel() != null && !getStrLevel().equals("") && !getStrLevel().equalsIgnoreCase("NULL")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		
		if(getStrEmpType() != null && !getStrEmpType().equals("") && !getStrEmpType().equalsIgnoreCase("NULL")) {
			setF_emptype(getStrEmpType().split(","));
		} else {
			setF_emptype(null);
		}
		
		if(getStrMonth()==null){
			setStrMonth("1");
		}
		
		loadEPFChallanReport();
		viewEPFChallanData();

		return LOAD;
	}
	
	
	public void viewEPFChallanData() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String[] strPayCycleDates = null;
		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;
		String months = "";
		String payMonts = "";
		String printdate = "";
		double amountprinted = 0;
		double paidAmountPrinted = 0;
		double paidamount = 0.0;
		double unpaidamount = 0;
		String totalAmountDue = "";

		Map hmMap = new HashMap();
		Map hmPrintTotal = new HashMap();
		Map hmPrintPaidTotal = new HashMap();
		List<String> dateList = new ArrayList<String>();
		List<String> paidDateList = new ArrayList<String>();

		String payYear = "";
		try {

			if (getFinancialYear() != null) {

				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			} else {

				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-"+ strPayCycleDates[1]);

				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			}
			
			request.setAttribute("financialYear", getFinancialYear());
			request.setAttribute("month", getStrMonth());
			
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
			
			payMonts = uF.getDateFormat(strDateStart, DATE_FORMAT, "MMMM - yyyy");	
			
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
			
			/*pst = con.prepareStatement("select sum(amount) as total from payroll_generation where financial_year_from_date=?"
							+ " and financial_year_to_date=? and month=? and salary_head_id in ("+EMPLOYEE_EPF+","+EMPLOYER_EPF+")  and amount>0 " +
									" and emp_id in (select emp_id from employee_official_details where org_id=?) ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3,uF.parseToInt(getStrMonth()));
			pst.setInt(4, uF.parseToInt(getF_org()));
			rs = pst.executeQuery();

			while (rs.next()) {
				totalAmountDue = rs.getString("total");
			}*/
			
			
			//************************************************
			
			
			StringBuilder sbQuery = new StringBuilder();
			
			sbQuery.append("select distinct emp_per_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
					" and  epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) ");
			if(strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strBaseUserType.equalsIgnoreCase(HOD))){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details " +
						"where (supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+" " +
						"or hod_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+"))");		
				if(getF_emptype()!=null && getF_emptype().length>0){
					sbQuery.append("and emp_id in (select emp_id from employee_official_details where emptype in ('"+StringUtils.join(getF_emptype(), "' ,'")+"') ");
				}
			} else {
				if(getF_level()!=null && getF_level().length>0){
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	            }
	            if(getF_department()!=null && getF_department().length>0){
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            
	            if(getF_service()!=null && getF_service().length>0){
	                sbQuery.append(" and (");
	                for(int i=0; i<getF_service().length; i++){
	                	sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
	                    
	                    if(i<getF_service().length-1){
	                        sbQuery.append(" OR "); 
	                    }
	                }
	                sbQuery.append(" ) ");
	            } 
	            
	            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            if(uF.parseToInt(getF_org())>0){
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
			}
			
        	if(getF_emptype()!=null && getF_emptype().length>0){
				sbQuery.append(" and eod.emptype in ('"+StringUtils.join(getF_emptype(), "' ,'")+"') ");
			}
            		 
            sbQuery.append(" order by emp_per_id");
			pst = con.prepareStatement(sbQuery.toString());
			
			pst.setDate(1, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
			//System.out.println("pst for filter===>"+pst);
			rs = pst.executeQuery();			
			
			StringBuilder sbEmp=null;
			while(rs.next()){
				
				if(sbEmp == null){
					sbEmp=new StringBuilder();
					sbEmp.append(rs.getString("emp_per_id"));
				}
				else{
					sbEmp.append(","+rs.getString("emp_per_id"));
				}
			}
			
			if(sbEmp!=null){
				sbEmp.toString();
			}else{
				sbEmp=null;
			}
		
			//System.out.println("sbEmp==>"+sbEmp);	
			
			String sbEmp1="";
			if(sbEmp!=null && !sbEmp.equals("")){
				sbEmp1=sbEmp.toString();
			}
			request.setAttribute("sbEmp1", sbEmp1);
			
			//*******************************************************
			
		sbQuery=new StringBuilder();
		sbQuery.append(" select sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution," +
				"sum(erps_contribution) as erps_contribution,sum(erdli_contribution) as erdli_contribution,sum(pf_admin_charges) as pf_admin_charges," +
				"sum(edli_admin_charges) as edli_admin_charges,sum(evpf_contribution) as evpf_contribution from emp_epf_details where financial_year_start=?"
				+ " and financial_year_end=? and _month=? ");
		if(sbEmp!=null && !sbEmp.equals("")){
			sbQuery.append(" and emp_id in ("+sbEmp+")");
		}else{
			sbQuery.append(" and emp_id in (0)");
		}
		
		/*else{
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where org_id="+uF.parseToInt(getF_org())+") ");
			}*/
		pst=con.prepareStatement(sbQuery.toString());
		
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3,uF.parseToInt(getStrMonth()));
		//pst.setInt(4, uF.parseToInt(getF_org()));
		//System.out.println("pst for totalamountdue==1"+pst);
		rs=pst.executeQuery();
		
			/*pst = con.prepareStatement("select sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution," +
					"sum(erps_contribution) as erps_contribution,sum(erdli_contribution) as erdli_contribution,sum(pf_admin_charges) as pf_admin_charges," +
					"sum(edli_admin_charges) as edli_admin_charges,sum(evpf_contribution) as evpf_contribution from emp_epf_details where financial_year_start=?"
					+ " and financial_year_end=? and _month=? and emp_id in (select emp_id from employee_official_details where org_id=?) ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3,uF.parseToInt(getStrMonth()));
			pst.setInt(4, uF.parseToInt(getF_org()));
			System.out.println("pst==1"+pst);
			rs = pst.executeQuery();*/
		
			while (rs.next()) {
				totalAmountDue = ""+ (rs.getDouble("eepf_contribution")+rs.getDouble("erpf_contribution")+rs.getDouble("erps_contribution")+rs.getDouble("erdli_contribution")+rs.getDouble("pf_admin_charges")+rs.getDouble("edli_admin_charges")+rs.getDouble("evpf_contribution"));
			}
	        rs.close();
	        pst.close();
			
	        
	        
			StringBuilder sb = new StringBuilder();
			sb.append("select distinct(paid_date) as paid_date,sum(amount) as amount from challan_details where financial_year_from_date=?"
					+ " and financial_year_to_date=? and challan_type in ("+ EMPLOYEE_EPF + "," + EMPLOYER_EPF + ") and is_paid=? and month=? " );
			
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
		
			sb.append(" group by paid_date, challan_no");
			
			pst = con.prepareStatement(sb.toString());
			
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setBoolean(3, true);
			pst.setString(4,getStrMonth());
			//pst.setInt(5, uF.parseToInt(getF_org()));
		//System.out.println("pst2 for Amount Paid===>"+pst);
			rs = pst.executeQuery();

			while (rs.next()) {
				paidamount += rs.getDouble("amount");
				paidDateList.add(rs.getString("paid_date"));
				paidAmountPrinted += rs.getDouble("amount");
			}
	        rs.close();
	        pst.close();
			
	        
	     //   System.out.println("paidDateList in java ==>"+paidDateList);
	      
			for (int i = 0; i < paidDateList.size(); i++) {
				/*pst = con.prepareStatement("select distinct(challan_no) as challan_no,sum(amount) as amount from challan_details where financial_year_from_date=?"
					+ " and financial_year_to_date=? and challan_type in ("+ EMPLOYEE_EPF + "," + EMPLOYER_EPF + ") and is_paid=? and paid_date=? " +
					" and emp_id in (select emp_id from employee_official_details where org_id=?) group by challan_no");
				*/
				
				sbQuery=new StringBuilder();
				sbQuery.append("select distinct(challan_no) as challan_no,sum(amount) as amount from challan_details where financial_year_from_date=?"
					+ " and financial_year_to_date=? and challan_type in ("+ EMPLOYEE_EPF + "," + EMPLOYER_EPF + ") and is_paid=? and paid_date=? " );
			
				if(sbEmp!=null && !sbEmp.equals("")){
					sbQuery.append(" and emp_id in ("+sbEmp+")");
				}else{
					sbQuery.append(" and emp_id in (0)");
				}
				
				sbQuery.append(" group by challan_no");
				pst=con.prepareStatement(sbQuery.toString());
				
					//"emp_id in (select emp_id from employee_official_details where org_id=?) group by challan_no");
				
				
				pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setBoolean(3, true);
				pst.setDate(4, uF.getDateFormat(paidDateList.get(i), DBDATE));
				//pst.setInt(5, uF.parseToInt(getF_org()));
		//		System.out.println("pst3 for challan no.===>"+pst);

				rs = pst.executeQuery();
				int count = 0;
				while (rs.next()) {
					/*hmPrintPaidTotal.put(paidDateList.get(i)+"_"+count,rs.getString("amount"));
					hmPrintPaidTotal.put(paidDateList.get(i)+"_"+count + "_CHALLANNUM",uF.showData(rs.getString("challan_no"),""));
					*/
					hmPrintPaidTotal.put(paidDateList.get(i)+"_"+i,rs.getString("amount"));
					hmPrintPaidTotal.put(paidDateList.get(i)+"_"+i + "_CHALLANNUM",uF.showData(rs.getString("challan_no"),""));
					
					count++;
				}
		        rs.close();
		        pst.close();
			}

			sb = new StringBuilder();
			sb.append("select distinct(entry_date) as entry_date,sum(amount) as amount  from challan_details where financial_year_from_date=?"
					+ " and financial_year_to_date=? and challan_type in ("+ EMPLOYEE_EPF + "," + EMPLOYER_EPF + ") and is_print=? and is_paid=? and month=? ");
			
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			
			sb.append(" group by entry_date");
			
			pst = con.prepareStatement(sb.toString());
			
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setBoolean(3, true);
			pst.setBoolean(4, false);
			pst.setString(5,getStrMonth());
			//pst.setInt(6, uF.parseToInt(getF_org()));  
	//		System.out.println("pst4 for amount printed but not paid===>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				dateList.add(rs.getString("entry_date"));
				amountprinted += rs.getDouble("amount");
			}
	        rs.close();
	        pst.close();

			for (int i = 0; i < dateList.size(); i++) {
				/*pst = con.prepareStatement("select sum(amount) as amount from challan_details where financial_year_from_date=?"
					+ " and financial_year_to_date=? and challan_type in" +" ("+ EMPLOYEE_EPF + "," + EMPLOYER_EPF + ") and is_print=? " +
					"and is_paid=? and entry_date=? and emp_id in (select emp_id from employee_official_details where org_id=?)");
				*/
				
				sb=new StringBuilder();
				sb.append("select sum(amount) as amount from challan_details where financial_year_from_date=?"
					+ " and financial_year_to_date=? and challan_type in" +" ("+ EMPLOYEE_EPF + "," + EMPLOYER_EPF + ") and is_print=? " +
					"and is_paid=? and entry_date=? ");
				
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
				
				pst=con.prepareStatement(sb.toString());
				pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setBoolean(3, true);
				pst.setBoolean(4, false);
				pst.setDate(5,uF.getDateFormat(dateList.get(i), DBDATE));
				//pst.setInt(6, uF.parseToInt(getF_org())); 
				//System.out.println("pst5===>"+pst);

				rs = pst.executeQuery();
				while (rs.next()) {
					hmPrintTotal.put(dateList.get(i), rs.getString("amount"));
				}
		        rs.close();
		        pst.close();
			}
			
//			System.out.println("strMonth=====>"+getStrMonth());

			StringBuilder sbQry = new StringBuilder();
			sbQry.append("select sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution," +
					"sum(erps_contribution) as erps_contribution,sum(erdli_contribution) as erdli_contribution,sum(pf_admin_charges) as pf_admin_charges," +
					"sum(edli_admin_charges) as edli_admin_charges,sum(evpf_contribution) as evpf_contribution " +
					"from emp_epf_details where financial_year_start=?"
					+ " and financial_year_end=? and _month=? and emp_id not in (select emp_id from challan_details where month=?");
			
			sbQry.append(" and challan_type in ("+ EMPLOYEE_EPF + "," + EMPLOYER_EPF + ") and financial_year_from_date=? and financial_year_to_date=?)");
			
			if(sbEmp!=null && !sbEmp.equals("")){
				sbQry.append(" and emp_id in ("+sbEmp+")");
			}else{
				sbQry.append(" and emp_id in (0)");
			}
			
			pst = con.prepareStatement(sbQry.toString()); 
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3,uF.parseToInt(getStrMonth()));
			pst.setString(4,getStrMonth()); 
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			//pst.setInt(7, uF.parseToInt(getF_org())); 
		//	System.out.println("pst6 for unpaidamount===>"+pst);
			rs = pst.executeQuery();

			while (rs.next()) {
				unpaidamount = (rs.getDouble("eepf_contribution")+rs.getDouble("erpf_contribution")+rs.getDouble("erps_contribution")+rs.getDouble("erdli_contribution")+rs.getDouble("pf_admin_charges")+rs.getDouble("edli_admin_charges")+rs.getDouble("evpf_contribution"));
			}
	        rs.close();
	        pst.close();
			
			payYear=uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy");
			
			request.setAttribute("hmPrintTotal", hmPrintTotal);
			request.setAttribute("hmPrintPaidTotal", hmPrintPaidTotal);
		//	System.out.println("hmPrintPaidTotal===>"+hmPrintPaidTotal);
			request.setAttribute("amountprinted", amountprinted + "");
			request.setAttribute("paidamount", paidamount+"");
			request.setAttribute("dateList", dateList);
			request.setAttribute("paidDateList", paidDateList);
			request.setAttribute("months", strMonth);
			request.setAttribute("payYear", payYear);
			request.setAttribute("payMonts", payMonts);
			request.setAttribute("unpaidamount", unpaidamount + "");
			request.setAttribute("totalAmountDue", totalAmountDue);

			getSelectedFilter(uF);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	
	public String loadEPFChallanReport() {

		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillMonth();
		orgList = new FillOrganisation(request).fillOrganisation();
		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		
		if(getF_org()==null && orgList!=null && orgList.size()>0){
			setF_org(orgList.get(0).getOrgId());
		}
		
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		empTypeList = new FillEmploymentType().fillEmploymentType(request);
	
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

		alFilter.add("LOCATION");
		if(getF_strWLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0;j<getF_strWLocation().length;j++) {
					if(getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
						if(k==0) {
							strLocation=wLocationList.get(i).getwLocationName();
						} else {
							strLocation+=", "+wLocationList.get(i).getwLocationName();
						}
						k++;
					}
				}
			}
			if(strLocation!=null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}
		
		alFilter.add("DEPARTMENT");
		if(getF_department()!=null) {
			String strDepartment="";
			int k=0;
			for(int i=0;departmentList!=null && i<departmentList.size();i++) {
				for(int j=0;j<getF_department().length;j++) {
					if(getF_department()[j].equals(departmentList.get(i).getDeptId())) {
						if(k==0) {
							strDepartment=departmentList.get(i).getDeptName();
						} else {
							strDepartment+=", "+departmentList.get(i).getDeptName();
						}
						k++;
					}
				}
			}
			if(strDepartment!=null && !strDepartment.equals("")) {
				hmFilter.put("DEPARTMENT", strDepartment);
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}
		
		alFilter.add("SERVICE");
		if(getF_service()!=null) {
			String strService="";
			int k=0;
			for(int i=0;serviceList!=null && i<serviceList.size();i++) {
				for(int j=0;j<getF_service().length;j++) {
					if(getF_service()[j].equals(serviceList.get(i).getServiceId())) {
						if(k==0) {
							strService=serviceList.get(i).getServiceName();
						} else {
							strService+=", "+serviceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if(strService!=null && !strService.equals("")) {
				hmFilter.put("SERVICE", strService);
			} else {
				hmFilter.put("SERVICE", "All SBUs");
			}
		} else {
			hmFilter.put("SERVICE", "All SBUs");
		}
		
		alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				for(int j=0;j<getF_level().length;j++) {
					if(getF_level()[j].equals(levelList.get(i).getLevelId())) {
						if(k==0) {
							strLevel=levelList.get(i).getLevelCodeName();
						} else {
							strLevel+=", "+levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if(strLevel!=null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Level's");
			}
		} else {
			hmFilter.put("LEVEL", "All Level's");
		}
		
		alFilter.add("EMPTYPE");
		if(getF_emptype()!=null) {
			String strEmpType="";
			int k=0;
			for(int i=0;empTypeList!=null && i<empTypeList.size();i++) {
				for(int j=0;j<getF_emptype().length;j++) {
					if(getF_emptype()[j].equals(empTypeList.get(i).getEmpTypeId())) {
						if(k==0) {
							strEmpType=empTypeList.get(i).getEmpTypeName();
						} else {
							strEmpType+=", "+empTypeList.get(i).getEmpTypeName();
						}
						k++;
					}
				}
			}
			if(strEmpType!=null && !strEmpType.equals("")) {
				hmFilter.put("EMPTYPE", strEmpType);
			} else {
				hmFilter.put("EMPTYPE", "All Employeetype's");
			}
		} else {
			hmFilter.put("EMPTYPE", "All Employeetype's");
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}


	public String getStrDepartment() {
		return strDepartment;
	}


	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}


	public String getStrSbu() {
		return strSbu;
	}


	public void setStrSbu(String strSbu) {
		this.strSbu = strSbu;
	}


	public String getStrLevel() {
		return strLevel;
	}


	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}


	public String getStrEmpType() {
		return strEmpType;
	}


	public void setStrEmpType(String strEmpType) {
		this.strEmpType = strEmpType;
	}


	public String[] getF_strWLocation() {
		return f_strWLocation;
	}


	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}


	public String[] getF_department() {
		return f_department;
	}


	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}


	public String[] getF_level() {
		return f_level;
	}


	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}


	public String[] getF_service() {
		return f_service;
	}


	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}


	public String[] getF_emptype() {
		return f_emptype;
	}


	public void setF_emptype(String[] f_emptype) {
		this.f_emptype = f_emptype;
	}


	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}


	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}


	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}


	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}


	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}


	public List<FillServices> getServiceList() {
		return serviceList;
	}


	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}


	public List<FillEmploymentType> getEmpTypeList() {
		return empTypeList;
	}


	public void setEmpTypeList(List<FillEmploymentType> empTypeList) {
		this.empTypeList = empTypeList;
	}

	public String getFinancialYear() {
		return financialYear;
	}
	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}
	public String getStrEmpId() {
		return strEmpId;
	}
	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}
	
	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}
	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}
	public List<FillMonth> getMonthList() {
		return monthList;
	}
	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public String getStrMonth() {
		return strMonth;
	}


	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}


	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}


	public String getF_org() {
		return f_org;
	}


	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getStrLocation() {
		return strLocation;
	}


	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}


}
