package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.payroll.ApprovePayroll;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Form24QReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	String strUserType = null;
	       
	CommonFunctions CF = null; 
	String profileEmpId;
	 
	String strD1 = null;
	String strD2 = null;
	String strPC = null;
	String f_org;
	 
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;

	String financialYear; 
	String paycycle;
	String strMonth;
	List<FillOrganisation> orgList;	
	
	String []f_strWLocation;
	String []f_level;
	String []f_department;
	String []f_service;
	
	List<FillPayCycles> paycycleList;
	List<FillFinancialYears> financialYearList;
	List<FillLevel> levelList;
	List<FillWLocation> wLocationList;
	
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	
	
	private static Logger log = Logger.getLogger(Form24QReport.class);
	
	public String execute() throws Exception {
		
		session = request.getSession();
		request.setAttribute(PAGE, "/jsp/reports/Form24QReport.jsp");
		request.setAttribute(TITLE, "Form 24Q Report");
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
			
		strEmpId =(String) session.getAttribute(EMPID);
		strUserType =(String) session.getAttribute(USERTYPE);
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		if(getF_org()==null || getF_org().trim().equals("")){
			setF_org((String)session.getAttribute(ORGID));
		}

		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		if(getStrSbu() != null && !getStrSbu().equals("")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		if(getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
 
		viewForm24QReport(uF);
		
		return loadForm24QReport(uF);

	}
	
	
	public String loadForm24QReport(UtilityFunctions uF){
		
		paycycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		getSelectedFilter(uF);
//		System.out.println("LOAD ============>> ");
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
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
public void viewForm24QReport(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			String[] strFianacialDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				strFianacialDates = getFinancialYear().split("-");
				strFinancialYearStart = strFianacialDates[0];
				strFinancialYearEnd = strFianacialDates[1];			
			} else {
				strFianacialDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFianacialDates[0] + "-" + strFianacialDates[1]);				
				strFinancialYearStart = strFianacialDates[0];
				strFinancialYearEnd = strFianacialDates[1];				 
			}
			
			con = db.makeConnection(con);
			
			String strOrgName = CF.getOrgNameById(con, getF_org());
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmEmpSlabType = CF.getEmpIncomeTaxSlabTypeMap(con, CF, strFinancialYearStart, strFinancialYearEnd);
			
			Map<String, String> hmSectionMap = CF.getSectionMap(con,strFinancialYearStart,strFinancialYearEnd);
			
			Map<String,String> hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
			Map<String, String> hmDept = CF.getDeptMap(con);
			Map<String, String> hmEmpAgeMap = CF.getEmpAgeMap(con, CF);
			Map<String, String> hmEmpGenderMap = CF.getEmpGenderMap(con);
			Map<String, String> hmEmpCodeMap = CF.getEmpCodeMap(con);
			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpLevelidMap = CF.getEmpLevelMap(con);

			Map hmEmpMertoMap = new HashMap();
			Map hmEmpWlocationMap = new HashMap();
			Map hmEmpStateMap = new HashMap();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
			Map<String, String> hmOrg = CF.getOrgDetails(con, uF, getF_org());
			
			Map<String,Map<String,String>> hmPrevEmpTds = CF.getPrevEmpTdsDetails(con,uF,strFinancialYearStart,strFinancialYearEnd);
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("SELECT eod.emp_id,epd.emp_fname, epd.emp_mname,epd.emp_lname,epd.empcode,eod.depart_id,epd.emp_pan_no,epd.employment_end_date,epd.joining_date FROM employee_official_details eod, employee_personal_details epd WHERE epd.emp_per_id=eod.emp_id  " +
				"and eod.emp_id in (select distinct emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date=?) and is_form16=true "); //select distinct emp_id from attendance_details where to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD') between ? and ?
//			sbQuery.append(" and eod.emp_id in (52) ");
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}			
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			
			if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            
            if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" eod.service_id like '%"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
			
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }            
			sbQuery.append(" order by epd.emp_fname"); 
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("=======0========"+new Date());
			//System.out.println(" pst=0=="+pst);
			rs = pst.executeQuery();
			
			Map<String, String> hmEmployeeMap = new LinkedHashMap<String, String>();
			Map<String, String> hmEmpJoiningDate = new HashMap<String, String>();
			Map<String, String> hmEmpCode = new HashMap<String, String>();
			Map<String, String> hmEmpDepartment = new HashMap<String, String>();
			Map<String, String> hmEmpPanNo = new HashMap<String, String>();
			Map<String, String> hmEmpEndDate = new HashMap<String, String>(); 
			
			StringBuilder sbEmp = null;
			while (rs.next()) {
				if (rs.getInt("emp_id") < 0) {
					continue;
				} 
				
				String strMiddleName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strMiddleName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmployeeMap.put(rs.getString("emp_id"), rs.getString("emp_fname") +strMiddleName+ " " + rs.getString("emp_lname"));
				hmEmpJoiningDate.put(rs.getString("emp_id"), uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				hmEmpCode.put(rs.getString("emp_id"), rs.getString("empcode"));
				hmEmpDepartment.put(rs.getString("emp_id"), rs.getString("depart_id"));
				hmEmpPanNo.put(rs.getString("emp_id"), uF.showData(rs.getString("emp_pan_no"), ""));
				hmEmpEndDate.put(rs.getString("emp_id"), uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT));
				if(sbEmp == null) {
					sbEmp = new StringBuilder();
					sbEmp.append(rs.getString("emp_id"));
				} else {
					sbEmp.append(","+rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
//			System.out.println("=======1========"+new Date());
			if(sbEmp == null) {
				sbEmp = new StringBuilder("0");
			}
//			System.out.println("sbEmp ===>> " + sbEmp.toString());
			
			Map<String, String> hmReimbursementAmt = new HashMap<String, String>();
			if(CF.getIsReceipt()){
				String[] firstArr = CF.getPayCycleFromDate(con, strFinancialYearStart, CF.getStrTimeZone(), CF, getF_org());
				String[] secondArr = null;
				if(uF.parseToInt(uF.getDateFormat(hmOrg.get("ORG_START_PAYCYCLE"), DATE_FORMAT, "dd")) > 1){
					secondArr = CF.getPrevPayCycleByOrg(con, strFinancialYearEnd, CF.getStrTimeZone(), CF, getF_org());
				} else {
					secondArr = CF.getPayCycleFromDate(con, strFinancialYearEnd, CF.getStrTimeZone(), CF, getF_org());
				}
				pst = con.prepareStatement("select emp_id,sum(reimbursement_amount) as reimbursement_amount from emp_reimbursement where approval_1 =1 " +
					" and ispaid=true and (ref_document is null or ref_document='' or upper(ref_document) ='NULL') and from_date>=? and to_date<=? " +
					" and emp_id in ("+sbEmp.toString()+") group by emp_id");
				pst.setDate(1, uF.getDateFormat(firstArr[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(secondArr[1], DATE_FORMAT));
//				pst.setInt(3, uF.parseToInt(getF_org()));
				rs = pst.executeQuery();
//				System.out.println("pst====>"+pst);
				while(rs.next()){
					hmReimbursementAmt.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("reimbursement_amount")));
				}
				rs.close();
				pst.close();
			}
			
			pst = con.prepareStatement("SELECT count(*) as cnt,emp_id FROM emp_family_members WHERE member_type='CHILD' group by emp_id");
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map<String, String> hmEmpChildCnt = new HashMap<String, String>();
			while(rs.next()){
				hmEmpChildCnt.put(rs.getString("emp_id"), rs.getString("cnt"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(*) as cnt,emp_id from (select distinct(paycycle),emp_id from payroll_generation pg " +
				"where financial_year_from_date=? and financial_year_to_date =? and is_paid = true " +
				"and salary_head_id not in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+") " +
				" and earning_deduction='E' group by paycycle,emp_id order by emp_id,paycycle) a group by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" 6 pst==>"+pst);
			Map<String, String> hmMonthPaid = new HashMap<String, String>();
			while(rs.next()){
				hmMonthPaid.put(rs.getString("emp_id"), rs.getString("cnt"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmMonthPaid==>"+hmMonthPaid);
			
			pst = con.prepareStatement("select distinct(pg.emp_id),pg.month,pg.year,pg.paycycle,pg.paid_days,pg.total_days from (select max(paycycle) as paycycle,emp_id from (select distinct(paycycle),emp_id from payroll_generation pg " +
				" where financial_year_from_date=? and financial_year_to_date =? and is_paid = true and salary_head_id not in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+") " +
				" and earning_deduction='E' group by paycycle,emp_id order by emp_id,paycycle) a group by emp_id ) a,payroll_generation pg where a.emp_id=pg.emp_id and a.paycycle=pg.paycycle and pg.emp_id in (select emp_per_id from employee_personal_details " +
				"where is_alive=false and employment_end_date between ? and ?) order by pg.emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println(" 1 pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmLastPaycycle = new HashMap<String, String>();
			while(rs.next()){
				hmLastPaycycle.put(rs.getString("emp_id"), rs.getString("emp_id"));
				hmLastPaycycle.put(rs.getString("emp_id")+"_MONTH", rs.getString("month"));
				hmLastPaycycle.put(rs.getString("emp_id")+"_YEAR", rs.getString("year"));
				hmLastPaycycle.put(rs.getString("emp_id")+"_PAYCYCLE", rs.getString("paycycle"));
				hmLastPaycycle.put(rs.getString("emp_id")+"_PAIDDAYS", rs.getString("paid_days"));
				hmLastPaycycle.put(rs.getString("emp_id")+"_TOTALDAYS", rs.getString("total_days"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmLastPaycycle==>"+hmLastPaycycle);
			
			pst = con.prepareStatement("select * from employee_personal_details where is_alive=false and employment_end_date between ? and ? order by emp_per_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println(" pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmExEmp = new HashMap<String, String>();
			while(rs.next()){
				hmExEmp.put(rs.getString("emp_per_id"), uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
//			System.out.println("hmExEmp==>"+hmExEmp);
			
			Map hmPayrollDetails = new HashMap();			
//			List al = new ArrayList();			
			
			Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1 ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println(" pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SERVICE_TAX", rs.getString("service_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_EDU_TAX", rs.getString("education_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_STD_TAX", rs.getString("standard_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_FLAT_TDS", rs.getString("flat_tds"));
				
				hmOtherTaxDetails.put(rs.getString("state_id")+"_MAX_TAX_INCOME", rs.getString("max_net_tax_income"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_REBATE_AMOUNT", rs.getString("rebate_amt"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SWACHHA_BHARAT_CESS", rs.getString("swachha_bharat_cess"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_KRISHI_KALYAN_CESS", rs.getString("krishi_kalyan_cess"));

				hmOtherTaxDetails.put(rs.getString("state_id")+"_CGST", rs.getString("cgst"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SGST", rs.getString("sgst"));
//				dblInvestmentExemption = 100000;
			}
			rs.close();
			pst.close();
//			System.out.println("=======2========"+new Date());
			
			Map<String, Map<String, String>> hmOtherSalaryHeadDetails = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmOtherAllowanceHeadDetails = new HashMap<String, Map<String, String>>();
// Created By Dattatray 8-06-2022 Note:  is_paid = true
			pst = con.prepareStatement("select sum(amount) as amount,salary_head_id,emp_id,earning_deduction from payroll_generation pg " +
				" where financial_year_from_date=? and financial_year_to_date =? and is_paid = true and salary_head_id not " +
				" in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+") " +
				" and emp_id in ("+sbEmp.toString()+") group by salary_head_id, emp_id,earning_deduction order by emp_id,earning_deduction desc");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println(" 2 pst==>"+pst);
			rs = pst.executeQuery();					
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			Map hmInner1 = new HashMap();
			Map<String, String> hmOtherSalInner = new HashMap<String, String>();
			Map<String, String> hmOtherAllowInner = new HashMap<String, String>();
			double dblGross = 0.0d;
			double dblOtherAllowTot = 0.0d;
			Map<String,String> hmLeaveEncashmet = new HashMap<String, String>();
			while(rs.next()){
				strEmpIdNew = rs.getString("emp_id");
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					hmInner1 = new HashMap();
					hmOtherSalInner = new HashMap<String, String>();
					hmOtherAllowInner = new HashMap<String, String>();
//					dblGross = 0.0d;
					dblOtherAllowTot = 0.0d;
					dblGross = uF.parseToDouble(hmReimbursementAmt.get(strEmpIdNew));
				}
								
				hmInner1.put(rs.getString("salary_head_id"), rs.getString("amount"));
				if(uF.parseToInt(rs.getString("salary_head_id")) == OVER_TIME || uF.parseToInt(rs.getString("salary_head_id")) == BONUS || uF.parseToInt(rs.getString("salary_head_id")) == INCENTIVES){
					hmOtherSalInner.put(rs.getString("salary_head_id"), rs.getString("amount"));
				} else if(uF.parseToInt(rs.getString("salary_head_id")) != BASIC && uF.parseToInt(rs.getString("salary_head_id")) != DA && uF.parseToInt(rs.getString("salary_head_id")) != HRA
					 && uF.parseToInt(rs.getString("salary_head_id")) != GRATUITY && uF.parseToInt(rs.getString("salary_head_id")) != LEAVE_ENCASHMENT) {
					if(rs.getString("earning_deduction").equalsIgnoreCase("E")) {
						dblOtherAllowTot += rs.getDouble("amount");
					}
					hmOtherAllowInner.put("OTHER_ALLOW_TOTAL", dblOtherAllowTot+"");
				}
				
				if(rs.getString("earning_deduction").equalsIgnoreCase("E")) {
					dblGross += rs.getDouble("amount");
				}
				hmInner1.put("GROSS", dblGross+"");
				hmPayrollDetails.put(strEmpIdNew, hmInner1);
				
				hmOtherSalaryHeadDetails.put(strEmpIdNew, hmOtherSalInner);
				hmOtherAllowanceHeadDetails.put(strEmpIdNew, hmOtherAllowInner);
				
				if(uF.parseToInt(rs.getString("salary_head_id")) == LEAVE_ENCASHMENT && hmExEmp.containsKey(rs.getString("emp_id"))){
					hmLeaveEncashmet.put(rs.getString("emp_id"), rs.getString("amount"));
				}
				
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
//			System.out.println("hmOtherSalaryHeadDetails ===>> " + hmOtherSalaryHeadDetails);
//			System.out.println("=======3========"+new Date());
//			System.out.println("hmPayrollDetails "+hmPayrollDetails.get("48"));
			
			pst = con.prepareStatement("select * from exemption_details where exemption_from=? and exemption_to=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map hmExemption = new HashMap();
			Map<String, Map<String, List<String>>> hmExemptionDataUnderSection = new HashMap<String, Map<String, List<String>>>();
			while(rs.next()) {
//				hmExemption.put(rs.getString("exemption_name"), rs.getString("exemption_limit"));
				hmExemption.put(rs.getString("salary_head_id"), rs.getString("exemption_limit"));
				Map<String, List<String>> hmExemptionData = hmExemptionDataUnderSection.get(rs.getString("under_section"));
				if(hmExemptionData == null) hmExemptionData = new LinkedHashMap<String, List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("exemption_id"));
				innerList.add(rs.getString("exemption_code"));
				innerList.add(rs.getString("exemption_name"));
				innerList.add(rs.getString("exemption_limit"));
				innerList.add(rs.getString("exemption_from"));
				innerList.add(rs.getString("exemption_to"));
				innerList.add(rs.getString("salary_head_id"));
				innerList.add(rs.getString("under_section"));
				innerList.add(rs.getString("slab_type")); //8
				hmExemptionData.put(rs.getString("exemption_id"), innerList);
				hmExemptionDataUnderSection.put(rs.getString("under_section"), hmExemptionData);
			}
			rs.close();
			pst.close();
//			System.out.println("=======4========"+new Date());
			
			
			pst = con.prepareStatement("select * from section_details where under_section in (8,9) and financial_year_start=? and financial_year_end=? ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst =======>> " + pst);
			List<List<String>> chapter1SectionList = new ArrayList<List<String>>();
			List<List<String>> chapter2SectionList = new ArrayList<List<String>>();
			Map<String, String> hmSectionPFApplicable = new HashMap<String, String>();
//			List<String> chapter1SectionList = new ArrayList<String>();
//			List<String> chapter2SectionList = new ArrayList<String>();
			while(rs.next()){
				if(uF.parseToInt(rs.getString("under_section"))==8) {
					List<String> innList = new ArrayList<String>();
					innList.add(rs.getString("section_id"));
					innList.add(rs.getString("slab_type"));
					chapter1SectionList.add(innList);
				} else {
					List<String> innList = new ArrayList<String>();
					innList.add(rs.getString("section_id"));
					innList.add(rs.getString("slab_type"));
					chapter2SectionList.add(innList);
				}
				hmSectionPFApplicable.put(rs.getString("section_id")+"_"+rs.getString("slab_type"), rs.getString("is_pf_applicable"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmSectionPFApplicable ===>> " + hmSectionPFApplicable);
			
			
			pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map hmHRAExemption = new HashMap();
			while(rs.next()){
				hmHRAExemption.put("CONDITION_1", rs.getString("condition1"));
				hmHRAExemption.put("CONDITION_2", rs.getString("condition2"));
				hmHRAExemption.put("CONDITION_3", rs.getString("condition3"));
				hmHRAExemption.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
			}
			rs.close();
			pst.close();
//			System.out.println("=======5========"+new Date());
			
			
			Map hmSectionLimitA = new HashMap();
			Map hmSectionLimitP = new HashMap();			
			Map hmSectionLimitEmp = new HashMap();
			Map<String, String> hmSectionAdjustedGrossIncomeLimitStatus = new HashMap<String, String>();
//			pst = con.prepareStatement(selectSection); 
			pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? order by section_code");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			while (rs.next()) {
				if(rs.getString("section_limit_type").equalsIgnoreCase("A")) {
					hmSectionLimitA.put(rs.getString("section_id")+"_"+rs.getString("slab_type"), rs.getString("section_exemption_limit"));
					hmSectionLimitA.put(rs.getString("section_id")+"_"+rs.getString("slab_type")+"_CEILING_AMT", rs.getBoolean("is_ceiling_applicable") ? rs.getDouble("ceiling_amount") : "0");
				} else {
					hmSectionLimitP.put(rs.getString("section_id")+"_"+rs.getString("slab_type"), rs.getString("section_exemption_limit"));
					hmSectionLimitP.put(rs.getString("section_id")+"_"+rs.getString("slab_type")+"_CEILING_AMT", rs.getBoolean("is_ceiling_applicable") ? rs.getDouble("ceiling_amount") : "0");
				}
				hmSectionAdjustedGrossIncomeLimitStatus.put(rs.getString("section_id")+"_"+rs.getString("slab_type"), rs.getString("is_adjusted_gross_income_limit"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmSectionLimitP ===>> " + hmSectionLimitP + " ---------- hmSectionLimitA ===>> " + hmSectionLimitA);
//			System.out.println("=======6========"+new Date());
			
			
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details id, section_details sd where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to=? and status = true and section_code in ('HRA') and trail_status = 1 group by emp_id ");
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details id, exemption_details ed where ed.salary_head_id=id.salary_head_id and id.fy_from=? " +
				" and id.fy_to=? and status=true and ed.salary_head_id=? and trail_status=1 and ed.exemption_from=? and ed.exemption_to=? and emp_id in ("+sbEmp.toString()+") group by emp_id ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, HRA);
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map hmRentPaid = new HashMap();
			while(rs.next()){
				hmRentPaid.put(rs.getString("emp_id"), rs.getString("amount_paid"));
			}
			rs.close();
			pst.close();
//			System.out.println("=======7========"+new Date());
//			System.out.println("hmRentPaid ===>> "+hmRentPaid);
			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, sd.slab_type, emp_id from investment_details id, section_details sd where sd.section_id=id.section_id " +
				"and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? and section_code not in ('HRA') " +
				"and isdisplay=true and parent_section=0 and under_section=8 and emp_id in ("+sbEmp.toString()+") group by emp_id, sd.section_id, sd.slab_type order by emp_id"); // and sd.section_id !=11 
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			System.out.println(" pst==>"+pst);
			Map hmInvestment = new HashMap();
			Map<String, String> hmEmpExemptionsCH1Map = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmpInvestment = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmpActualInvestment = new HashMap<String, Map<String, String>>();
			double dblInvestmentLimit = 0;
			double dblInvestmentCeilingLimit = 0;
			double dblInvestmentEmp = 0;
			while(rs.next()) {
				String strSectionId = rs.getString("section_id");
				String slabType = rs.getString("slab_type");
				double dblInvestment = rs.getDouble("amount_paid");
				if(uF.parseToInt(hmEmpSlabType.get(rs.getString("emp_id")))!= uF.parseToInt(slabType)) {
					continue;
				}
				if(hmSectionLimitA.containsKey(strSectionId+"_"+slabType)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType));
					dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType+"_CEILING_AMT"));
					if(dblInvestmentCeilingLimit>0) {
						dblInvestmentLimit = dblInvestmentCeilingLimit;
					}
				} else{
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType));
					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
					dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType+"_CEILING_AMT"));
					if(dblInvestmentCeilingLimit>0) {
						dblInvestmentLimit = dblInvestmentCeilingLimit;
					}
				}
				Map<String,String> hmInvest=hmEmpInvestment.get(rs.getString("emp_id"));
				if(hmInvest==null) hmInvest=new HashMap<String, String>();
				
				System.out.println("hmEmpExemptionsCH1Map.get(rs.getString(\"emp_id\")) "+hmEmpExemptionsCH1Map.get("48"));
				if(dblInvestment>=dblInvestmentLimit && dblInvestmentLimit>=0) {
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH1Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
					hmEmpExemptionsCH1Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
					hmInvest.put(strSectionId, ""+dblInvestmentLimit);
				} else{
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH1Map.get(rs.getString("emp_id"))) + dblInvestment;
					hmEmpExemptionsCH1Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
					hmInvest.put(strSectionId, ""+dblInvestment);
				}
				hmEmpInvestment.put(rs.getString("emp_id"), hmInvest);
				
				Map<String,String> hmActualInvest=hmEmpActualInvestment.get(rs.getString("emp_id"));
				if(hmActualInvest==null) hmActualInvest=new HashMap<String, String>();
				hmActualInvest.put(strSectionId, ""+dblInvestment);
				
				hmEmpActualInvestment.put(rs.getString("emp_id"), hmActualInvest);
				
				if(rs.getString("emp_id").equals("48")) {
					System.out.println("dblInvestment==="+dblInvestment);
					System.out.println("dblInvestmentLimit==="+dblInvestmentLimit);
					System.out.println("dblInvestmentEmp==="+dblInvestmentEmp);					
				}
			}
			rs.close();
			pst.close();
			
			
//			******************************* Code added on 02May2020 *******************
			Map<String, Map<String, Map<String, Map<String, String>>>> hmEmpIncludeSubSectionData = new HashMap<String, Map<String, Map<String, Map<String, String>>>>();
			pst = con.prepareStatement("select id.*, sd.section_id, sd.is_adjusted_gross_income_limit, sd.include_sub_section,sd.slab_type from investment_details id, section_details sd where sd.section_id=id.section_id and " +
				"id.fy_from=? and id.fy_to=? and status=true and trail_status=1 and sd.financial_year_start=? and sd.financial_year_end=? and section_code not in ('HRA') and isdisplay=true and parent_section>0 " +
				" and emp_id in ("+sbEmp.toString()+") and sub_section_no>0 and (is_adjusted_gross_income_limit is null or is_adjusted_gross_income_limit=false) and (include_sub_section is not null or include_sub_section !='') " + //and emp_id in ("+strEmpIds+") 
				"order by emp_id,sd.section_id,sd.slab_type"); //and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			while(rs.next()) {
				String strSectionId = rs.getString("section_id");
				String slabType = rs.getString("slab_type");
				if(uF.parseToInt(hmEmpSlabType.get(rs.getString("emp_id")))!= uF.parseToInt(slabType) && uF.parseToInt(slabType)!=2) {
					continue;
				}
				Map<String, Map<String, Map<String, String>>> hmIncludeSubSectionData = hmEmpIncludeSubSectionData.get(rs.getString("emp_id"));
				if(hmIncludeSubSectionData==null) hmIncludeSubSectionData = new HashMap<String, Map<String, Map<String, String>>>();
				
				Map<String, Map<String, String>> hmOuter = hmIncludeSubSectionData.get(strSectionId);
				if(hmOuter==null) hmOuter = new HashMap<String, Map<String, String>>();
				
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("SUB_SEC_NO", rs.getString("sub_section_no"));
				hmInner.put("SUB_SEC_AMT_PAID", rs.getString("amount_paid"));
				hmInner.put("SUB_SEC_LIMIT_TYPE", rs.getString("sub_section_limit_type"));
				hmInner.put("SUB_SEC_AMT", rs.getString("sub_section_amt"));
				hmOuter.put(rs.getString("sub_section_no"), hmInner);
				
				hmIncludeSubSectionData.put(strSectionId, hmOuter);
				
				hmEmpIncludeSubSectionData.put(rs.getString("emp_id"), hmIncludeSubSectionData);
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpIncludeSubSectionData ===>> " + hmEmpIncludeSubSectionData);
			
			Map<String, Map<String, String>> hmEmpSubSecMinusAmt = new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select id.*, sd.section_id, sd.is_adjusted_gross_income_limit, sd.include_sub_section,sd.slab_type from investment_details id, section_details sd where sd.section_id=id.section_id and " +
				"id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? and " +
				"section_code not in ('HRA')  and isdisplay=true and parent_section>0 and under_section=8 and emp_id in ("+sbEmp.toString()+") and sub_section_no>0 and " + //and emp_id in ("+strEmpIds+")
				"(is_adjusted_gross_income_limit is null or is_adjusted_gross_income_limit = false) order by emp_id,sd.section_id"); //and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			dblInvestmentLimit = 0;
			dblInvestmentEmp = 0;
			String oldSectionId = null;
			String newEmpId = null;
			String oldEmpId = null;
			while(rs.next()) {
				String strSectionId = rs.getString("section_id");
				String slabType = rs.getString("slab_type");
				if(uF.parseToInt(hmEmpSlabType.get(rs.getString("emp_id")))!= uF.parseToInt(slabType) && uF.parseToInt(slabType)!=2) {
					continue;
				}
				String strSubSecNo = rs.getString("sub_section_no");
				double dblInvestment = rs.getDouble("amount_paid");
				String strSubSecLimitType = rs.getString("sub_section_limit_type");
				double dblSubSecLimit = rs.getDouble("sub_section_amt");
				
				if(rs.getString("include_sub_section") != null && rs.getString("include_sub_section").length()>2) {
					List<String> al = Arrays.asList(rs.getString("include_sub_section").substring(1, rs.getString("include_sub_section").length()-1).split(","));
					Map<String, Map<String, Map<String, String>>> hmIncludeSubSectionData = hmEmpIncludeSubSectionData.get(rs.getString("emp_id"));
					if(hmIncludeSubSectionData==null) hmIncludeSubSectionData = new HashMap<String, Map<String, Map<String, String>>>();
					
					Map<String, Map<String, String>> hmOuter = hmIncludeSubSectionData.get(strSectionId);
					if(hmOuter==null) hmOuter = new HashMap<String, Map<String, String>>();
					
					double dblTotSubSecInvestment = 0;
					Iterator<String> itSubSec = hmOuter.keySet().iterator();
					while (itSubSec.hasNext()) {
						String strSubSecno = itSubSec.next();
						if(al.contains(strSubSecno)) {
							continue;
						}
						Map<String, String> hmInner = hmOuter.get(strSubSecno);
						dblTotSubSecInvestment += uF.parseToDouble(hmInner.get("SUB_SEC_AMT_PAID"));
					}
					if(al.contains(strSubSecNo) && dblTotSubSecInvestment>0) {
						continue;
					}
					
					newEmpId = rs.getString("emp_id");
					if(oldEmpId == null || uF.parseToInt(oldEmpId) != uF.parseToInt(newEmpId)) {
						oldSectionId = null;
					}
//					---------------
					Map<String,String> hmSubSecMinusAmt = hmEmpSubSecMinusAmt.get(rs.getString("emp_id"));
					if(hmSubSecMinusAmt==null) hmSubSecMinusAmt = new HashMap<String, String>();
					
					Map<String,String> hmInvest = hmEmpInvestment.get(rs.getString("emp_id"));
					if(hmInvest==null) hmInvest = new HashMap<String, String>();
					double dblSecInvestment =0;
					double dblSubSecMinusInvestment =0;
					if((oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) && hmInvest.keySet().contains(strSectionId)) {
						dblSecInvestment = 0;
						dblSubSecMinusInvestment = 0;
					} else {
						dblSecInvestment = uF.parseToDouble(hmInvest.get(strSectionId));
						dblSubSecMinusInvestment = uF.parseToDouble(hmSubSecMinusAmt.get(strSectionId));
					}
//					--------------------
					double dblSubSecMinActAmt=0;
					for(int i=0; i<al.size(); i++) {
						String strSubSNo = al.get(i);
						if(uF.parseToInt(strSubSNo)>0) {
							Map<String, String> hmInner = hmOuter.get(strSubSNo);
							double dblAppliedAmt = uF.parseToDouble(hmInner.get("SUB_SEC_AMT_PAID"));
							double dblSubSecLimitAmt = uF.parseToDouble(hmInner.get("SUB_SEC_AMT"));
							strSubSecLimitType = hmInner.get("SUB_SEC_LIMIT_TYPE");
							if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
								dblSubSecLimitAmt = (dblAppliedAmt * dblSubSecLimitAmt) / 100;
							}
							dblSubSecMinActAmt += Math.min(dblAppliedAmt, dblSubSecLimitAmt);
						}
					}
					
					if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
						dblSubSecLimit = (dblInvestment * dblSubSecLimit) / 100;
					}
					if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
						dblSecInvestment += dblSubSecLimit;
					} else {
						if(dblInvestment>0 && dblSubSecMinActAmt>0 && dblSubSecMinActAmt>dblSubSecMinusInvestment) {
							double dblMinAmt = dblSubSecMinActAmt - dblSubSecMinusInvestment;
							double dblAmt = dblSubSecLimit - dblInvestment;
							if(dblMinAmt>dblAmt) {
								dblSubSecMinusInvestment += dblAmt;
								dblSecInvestment += Math.min((dblInvestment + dblAmt), dblSubSecLimit);
							} else {
								dblSubSecMinusInvestment += dblMinAmt;
								dblSecInvestment += Math.min((dblInvestment + dblMinAmt), dblSubSecLimit);
							}
						} else {
							dblSecInvestment += dblInvestment;
						}
					}
//					System.out.println(strSectionId+" -- dblSecInvestment =====>> "  +dblSecInvestment);
					
					hmInvest.put(strSectionId, ""+dblSecInvestment);
					hmEmpInvestment.put(rs.getString("emp_id"), hmInvest);
					
					hmSubSecMinusAmt.put(strSectionId, ""+dblSubSecMinusInvestment);
					hmEmpSubSecMinusAmt.put(rs.getString("emp_id"), hmSubSecMinusAmt);
					
					if(oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) {
						oldSectionId = strSectionId;
					}
				} else {
					newEmpId = rs.getString("emp_id");
					if(oldEmpId == null || uF.parseToInt(oldEmpId) != uF.parseToInt(newEmpId)) {
						oldSectionId = null;
					}
					Map<String,String> hmInvest = hmEmpInvestment.get(rs.getString("emp_id"));
					if(hmInvest==null) hmInvest = new HashMap<String, String>();
					double dblSecInvestment =0;
					if((oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) && hmInvest.keySet().contains(strSectionId)) {
						dblSecInvestment = 0;
					} else {
						dblSecInvestment = uF.parseToDouble(hmInvest.get(strSectionId));
					}
					if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
						dblSubSecLimit = (dblInvestment * dblSubSecLimit) / 100;
					}
					if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
						dblSecInvestment += dblSubSecLimit;
					} else {
						dblSecInvestment += dblInvestment;
					}
					if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
						dblSecInvestment += dblSubSecLimit;
					} else {
						dblSecInvestment += dblInvestment;
					}
					hmInvest.put(strSectionId, ""+dblSecInvestment);
					hmEmpInvestment.put(rs.getString("emp_id"), hmInvest);
					
					if(oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) {
						oldSectionId = strSectionId;
					}
				}
				
				if(oldEmpId == null || uF.parseToInt(oldEmpId) != uF.parseToInt(newEmpId)) {
					oldEmpId = newEmpId;
				}
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpInvestment ==============================================>> " + hmEmpInvestment);
//			***************************************** code add end **************************
			
			
//			System.out.println("=======8========"+new Date());
//			System.out.println("hmEmpInvestment==="+hmEmpInvestment.get("654"));    
			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, sd.slab_type, emp_id from investment_details id, section_details sd " +
				"where sd.section_id=id.section_id and id.fy_from=? and id.fy_to=? and status=true and trail_status=1 and sd.financial_year_start=? " +
				"and sd.financial_year_end=? and section_code not in ('HRA') and isdisplay=true and parent_section=0 and under_section=9 and emp_id in ("+sbEmp.toString()+") group by emp_id, " +
				"sd.section_id, sd.slab_type order by emp_id "); // and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map<String, Map<String, String>> hmEmpInvestment1 = new HashMap<String, Map<String, String>>();
			Map<String, String> hmEmpExemptionsCH2Map = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmpActualInvestment1 = new HashMap<String, Map<String, String>>();
			
			dblInvestmentLimit = 0;
			dblInvestmentCeilingLimit = 0;
			dblInvestmentEmp = 0;
			
			while(rs.next()){

				String strSectionId = rs.getString("section_id");
				String slabType = rs.getString("slab_type");
				if(uF.parseToInt(hmEmpSlabType.get(rs.getString("emp_id")))!= uF.parseToInt(slabType) && uF.parseToInt(slabType)!=2) {
					continue;
				}
				double dblInvestment = rs.getDouble("amount_paid");
				
				if(hmSectionLimitA.containsKey(strSectionId+"_"+slabType)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType));
					dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType+"_CEILING_AMT"));
					if(dblInvestmentCeilingLimit>0) {
						dblInvestmentLimit = dblInvestmentCeilingLimit;
					}
				} else{
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType));
					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
					dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType+"_CEILING_AMT"));
					if(dblInvestmentCeilingLimit>0) {
						dblInvestmentLimit = dblInvestmentCeilingLimit;
					}
				}
				Map<String,String> hmInvest = hmEmpInvestment1.get(rs.getString("emp_id"));
				if(hmInvest==null) hmInvest = new HashMap<String, String>();
				
				if(dblInvestment>=dblInvestmentLimit && dblInvestmentLimit>=0) {
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
					hmEmpExemptionsCH2Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
					hmInvest.put(strSectionId, ""+dblInvestmentLimit);
				} else{
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestment;
					hmEmpExemptionsCH2Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
					hmInvest.put(strSectionId, ""+dblInvestment);
				}
				hmEmpInvestment1.put(rs.getString("emp_id"), hmInvest);
				
				Map<String,String> hmActualInvest = hmEmpActualInvestment1.get(rs.getString("emp_id"));
				if(hmActualInvest==null) hmActualInvest = new HashMap<String, String>();
				hmActualInvest.put(strSectionId, ""+dblInvestment);
				
				hmEmpActualInvestment1.put(rs.getString("emp_id"), hmActualInvest);
				
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpActualInvestment1 ===>> " + hmEmpActualInvestment1);
//			*************************** code added 02May2020 *******************************
			
			
			pst = con.prepareStatement("select id.*, sd.section_id, sd.slab_type, sd.is_adjusted_gross_income_limit, sd.include_sub_section from investment_details id, " +
				"section_details sd where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.financial_year_start=? " +
				"and sd.financial_year_end=? and section_code not in ('HRA')  and isdisplay=true and parent_section>0 and under_section=9 and emp_id in ("+sbEmp.toString()+") and sub_section_no>0 and " + //and emp_id in ("+strEmpIds+") 
				"(is_adjusted_gross_income_limit is null or is_adjusted_gross_income_limit = false) order by emp_id, sd.section_id"); //and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			dblInvestmentLimit = 0;
			dblInvestmentEmp = 0;
			oldSectionId = null;
			while(rs.next()) {
				String strSectionId = rs.getString("section_id");
				String slabType = rs.getString("slab_type");
				if(uF.parseToInt(hmEmpSlabType.get(rs.getString("emp_id")))!= uF.parseToInt(slabType) && uF.parseToInt(slabType)!=2) {
					continue;
				}
				String strSubSecNo = rs.getString("sub_section_no");
				double dblInvestment = rs.getDouble("amount_paid");
//				System.out.println("emp_id ============>> " + rs.getString("emp_id") + " -- dblInvestment ===>> " + dblInvestment+" -- strSectionId ===>> " + strSectionId +" -- strSubSecNo ===>> " + strSubSecNo);
				String strSubSecLimitType = rs.getString("sub_section_limit_type");
				double dblSubSecLimit = rs.getDouble("sub_section_amt");
				
				if(rs.getString("include_sub_section") != null && rs.getString("include_sub_section").length()>2) {
					List<String> al = Arrays.asList(rs.getString("include_sub_section").substring(1, rs.getString("include_sub_section").length()-1).split(","));
					Map<String, Map<String, Map<String, String>>> hmIncludeSubSectionData = hmEmpIncludeSubSectionData.get(rs.getString("emp_id"));
					if(hmIncludeSubSectionData==null) hmIncludeSubSectionData = new HashMap<String, Map<String, Map<String, String>>>();
					
					Map<String, Map<String, String>> hmOuter = hmIncludeSubSectionData.get(strSectionId);
					if(hmOuter==null) hmOuter = new HashMap<String, Map<String, String>>();
					
					double dblTotSubSecInvestment = 0;
					Iterator<String> itSubSec = hmOuter.keySet().iterator();
					while (itSubSec.hasNext()) {
						String strSubSecno = itSubSec.next();
						if(al.contains(strSubSecno)) {
							continue;
						}
						Map<String, String> hmInner = hmOuter.get(strSubSecno);
						dblTotSubSecInvestment += uF.parseToDouble(hmInner.get("SUB_SEC_AMT_PAID"));
					}
					if(al.contains(strSubSecNo) && dblTotSubSecInvestment>0) {
						continue;
					}
					
					newEmpId = rs.getString("emp_id");
					if(oldEmpId == null || uF.parseToInt(oldEmpId) != uF.parseToInt(newEmpId)) {
						oldSectionId = null;
					}
//						---------------
//					System.out.println("emp_id ============>> " + rs.getString("emp_id"));
					Map<String,String> hmSubSecMinusAmt = hmEmpSubSecMinusAmt.get(rs.getString("emp_id"));
					if(hmSubSecMinusAmt==null) hmSubSecMinusAmt = new HashMap<String, String>();
					
					Map<String,String> hmInvest = hmEmpInvestment1.get(rs.getString("emp_id"));
					if(hmInvest==null) hmInvest = new HashMap<String, String>();
//					System.out.println("emp_id ============>> " + rs.getString("emp_id") + " -- hmInvest =====>> " + hmInvest);
//					System.out.println("emp_id ============>> " + rs.getString("emp_id") + " -- oldSectionId =====>> " + oldSectionId+" -- strSectionId =====>> " + strSectionId);
					double dblSecInvestment=0;
					double dblSubSecMinusInvestment=0;
					if((oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) && hmInvest.keySet().contains(strSectionId)) {
						dblSecInvestment = 0;
						dblSubSecMinusInvestment=0;
					} else {
						dblSecInvestment = uF.parseToDouble(hmInvest.get(strSectionId));
						dblSubSecMinusInvestment = uF.parseToDouble(hmSubSecMinusAmt.get(strSectionId));
					}
//						--------------------
					
					double dblSubSecMinActAmt=0;
					for(int i=0; i<al.size(); i++) {
						String strSubSNo = al.get(i);
						if(uF.parseToInt(strSubSNo)>0) {
							Map<String, String> hmInner = hmOuter.get(strSubSNo);
							double dblAppliedAmt = uF.parseToDouble(hmInner.get("SUB_SEC_AMT_PAID"));
							double dblSubSecLimitAmt = uF.parseToDouble(hmInner.get("SUB_SEC_AMT"));
							strSubSecLimitType = hmInner.get("SUB_SEC_LIMIT_TYPE");
							if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
								dblSubSecLimitAmt = (dblAppliedAmt * dblSubSecLimitAmt) / 100;
							}
							dblSubSecMinActAmt += Math.min(dblAppliedAmt, dblSubSecLimitAmt);
						}
					}
					
					if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
						dblSubSecLimit = (dblInvestment * dblSubSecLimit) / 100;
					}
//					System.out.println("dblSubSecLimit ===>> " + dblSubSecLimit +" -- dblInvestment ===>> " + dblInvestment);
					
					if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
						dblSecInvestment += dblSubSecLimit;
					} else {
//						System.out.println("dblSubSecMinActAmt ===>> " + dblSubSecMinActAmt +" -- dblSubSecMinusInvestment ===>> " + dblSubSecMinusInvestment);
						if(dblInvestment>0 && dblSubSecMinActAmt>0 && dblSubSecMinActAmt>dblSubSecMinusInvestment) {
//							System.out.println("dblSubSecLimit =====================>>" + dblSubSecLimit);
							double dblMinAmt = dblSubSecMinActAmt - dblSubSecMinusInvestment;
							double dblAmt = dblSubSecLimit - dblInvestment;
							if(dblMinAmt>dblAmt) {
								dblSubSecMinusInvestment += dblAmt;
								dblSecInvestment += Math.min((dblInvestment + dblAmt), dblSubSecLimit);
							} else {
								dblSubSecMinusInvestment += dblMinAmt;
								dblSecInvestment += Math.min((dblInvestment + dblMinAmt), dblSubSecLimit);
							}
						} else {
							dblSecInvestment += dblInvestment;
						}
					}
//					System.out.println(strSectionId+" -- dblSecInvestment =====>> "  +dblSecInvestment);
					
					if(hmSectionLimitA.containsKey(strSectionId+"_"+slabType)) {
						dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType));
						dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType+"_CEILING_AMT"));
						if(dblInvestmentCeilingLimit>0) {
							dblInvestmentLimit = dblInvestmentCeilingLimit;
						}
					} else {
						dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType));
						dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
						dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType+"_CEILING_AMT"));
						if(dblInvestmentCeilingLimit>0) {
							dblInvestmentLimit = dblInvestmentCeilingLimit;
						}
					}
					
					if(dblSecInvestment>=dblInvestmentLimit && dblInvestmentLimit>=0) {
						hmInvest.put(strSectionId+"_"+slabType, ""+dblInvestmentLimit);
					} else {
						hmInvest.put(strSectionId+"_"+slabType, ""+dblSecInvestment);
					}
					hmEmpInvestment1.put(rs.getString("emp_id"), hmInvest);
					
					hmSubSecMinusAmt.put(strSectionId, ""+dblSubSecMinusInvestment);
					hmEmpSubSecMinusAmt.put(rs.getString("emp_id"), hmSubSecMinusAmt);
					
					if(oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) {
						oldSectionId = strSectionId;
					}
					
				} else {
				
					newEmpId = rs.getString("emp_id");
					if(oldEmpId == null || uF.parseToInt(oldEmpId) != uF.parseToInt(newEmpId)) {
						oldSectionId = null;
					}
					Map<String,String> hmInvest = hmEmpInvestment1.get(rs.getString("emp_id"));
					if(hmInvest==null) hmInvest = new HashMap<String, String>();
					double dblSecInvestment =0;
					if((oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) && hmInvest.keySet().contains(strSectionId)) {
						dblSecInvestment = 0;
					} else {
						dblSecInvestment = uF.parseToDouble(hmInvest.get(strSectionId));
					}
					if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
						dblSubSecLimit = (dblInvestment * dblSubSecLimit) / 100;
					}
					if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
						dblSecInvestment += dblSubSecLimit;
					} else {
						dblSecInvestment += dblInvestment;
					}
					if(hmSectionLimitA.containsKey(strSectionId+"_"+slabType)) {
						dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType));
						dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType+"_CEILING_AMT"));
						if(dblInvestmentCeilingLimit>0) {
							dblInvestmentLimit = dblInvestmentCeilingLimit;
						}
					} else {
						dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType));
						dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
						dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType+"_CEILING_AMT"));
						if(dblInvestmentCeilingLimit>0) {
							dblInvestmentLimit = dblInvestmentCeilingLimit;
						}
					}
					if(dblSecInvestment>=dblInvestmentLimit && dblInvestmentLimit>=0) {
						hmInvest.put(strSectionId, ""+dblInvestmentLimit);
					} else{
						hmInvest.put(strSectionId, ""+dblSecInvestment);
					}
					hmEmpInvestment1.put(rs.getString("emp_id"), hmInvest);
					
					if(oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) {
						oldSectionId = strSectionId;
					}
				}
				
				if(oldEmpId == null || uF.parseToInt(oldEmpId) != uF.parseToInt(newEmpId)) {
					oldEmpId = newEmpId;
				}
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpInvestment1 ===>> " + hmEmpInvestment1);
//				******************************** code added end ************************************
				
			
			
//			System.out.println("hmEmpInvestment1==="+hmEmpInvestment1.get("654"));
//			System.out.println("=======9========"+new Date());
			
//			System.out.println("=======11========"+new Date());
			
			pst = con.prepareStatement("select * from investment_details id, section_details sd where sd.section_id=id.section_id and id.fy_from=? and id.fy_to=? " +
				"and status=true and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? and section_code not in ('HRA') and isdisplay=true " +
				"and parent_section>0 and emp_id in ("+sbEmp.toString()+") order by emp_id"); //and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map<String, Map<String, List<Map<String, String>>>> hmEmpSubInvestment = new HashMap<String, Map<String, List<Map<String, String>>>>();
//			Map<String, List<Map<String, String>>> hmSubInvestment = new HashMap<String, List<Map<String, String>>>();	
			dblInvestmentLimit = 0;
			dblInvestmentEmp = 0;
			
			while(rs.next()) {
				String strSectionId = rs.getString("parent_section");
				String slabType = rs.getString("slab_type");
				if(uF.parseToInt(hmEmpSlabType.get(rs.getString("emp_id")))!= uF.parseToInt(slabType) && uF.parseToInt(slabType)!=2) {
					continue;
				}
				double dblInvestment = rs.getDouble("amount_paid");
				
				Map<String, List<Map<String, String>>> hmSubInvestment =hmEmpSubInvestment.get(rs.getString("emp_id"));
				if(hmSubInvestment ==null)hmSubInvestment = new HashMap<String, List<Map<String,String>>>();
				
				List<Map<String, String>> alSubInvestment =hmSubInvestment.get(rs.getString("parent_section"));
				if(alSubInvestment ==null)alSubInvestment = new ArrayList<Map<String, String>>();
				
				Map<String, String> hm = new HashMap<String, String>();
				hm.put("SECTION_ID", rs.getString("parent_section"));
				hm.put("SECTION_NAME", rs.getString("child_section"));
				hm.put("INVESTMENT_ID", rs.getString("investment_id"));
				hm.put("PAID_AMOUNT", ""+dblInvestment);
				hm.put("SUB_SEC_NO", rs.getString("sub_section_no"));
				hm.put("SUB_SECTION_AMOUNT", ""+rs.getDouble("sub_section_amt"));
				hm.put("SUB_SECTION_LIMIT_TYPE", rs.getString("sub_section_limit_type"));
				hm.put("STATUS", rs.getString("status"));
				
				alSubInvestment.add(hm);
				
				hmSubInvestment.put(rs.getString("parent_section"), alSubInvestment);
				hmEmpSubInvestment.put(rs.getString("emp_id"), hmSubInvestment);
			}
			rs.close();
			pst.close();
				
//			System.out.println("hmEmpSubInvestment==="+hmEmpSubInvestment.get("683"));
			
			/**
			 * HOME LOAN INTEREST EXEMPTION 
			 */

//			System.out.println("=======12========"+new Date());
			/**
			 * HOME LOAN INTEREST EXEMPTION 
			 */
			
			
			Map<String,String> hmEmpIncomeFromOtherSourcesMap = new HashMap<String,String>();
			Map<String,String> hmEmpLessIncomeFromOtherSourcesMap = new HashMap<String,String>();
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, sd.slab_type, emp_id from investment_details id, section_details sd " +
				"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and status = true and sd.section_id >=13 and sd.section_id <=17 " +
				"and parent_section = 0 and isdisplay=false and financial_year_start=? and financial_year_end=? and emp_id in ("+sbEmp.toString()+")" +
				" group by emp_id, sd.section_id, sd.slab_type order by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst ========>> " + pst);
			rs = pst.executeQuery();			
			double dblInvestmentIncomeSourcesEmp = 0;		
			while (rs.next()) {
				String slabType = rs.getString("slab_type");
				if(uF.parseToInt(hmEmpSlabType.get(rs.getString("emp_id")))!= uF.parseToInt(slabType) && uF.parseToInt(slabType)!=2) {
					continue;
				}
				
				double dblInvestment = rs.getDouble("amount_paid");
				dblInvestmentIncomeSourcesEmp = uF.parseToDouble(hmEmpIncomeFromOtherSourcesMap.get(rs.getString("emp_id"))) + dblInvestment;
				hmEmpIncomeFromOtherSourcesMap.put(rs.getString("emp_id"), dblInvestmentIncomeSourcesEmp+"");
				
				if(rs.getInt("section_id") == 15 || rs.getInt("section_id") == 16) {
					dblInvestmentIncomeSourcesEmp = uF.parseToDouble(hmEmpLessIncomeFromOtherSourcesMap.get(rs.getString("emp_id"))) + dblInvestment;
					hmEmpLessIncomeFromOtherSourcesMap.put(rs.getString("emp_id"), dblInvestmentIncomeSourcesEmp+"");
				}
			}
			rs.close();
			pst.close();
			
			
//			System.out.println("=======13========"+new Date());
			
//			Here is Under Section 10 & 16 Investment Detail Data Salary Headwise 
			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, ed.salary_head_id, ed.slab_type, emp_id from investment_details id, exemption_details ed " +
				"where ed.salary_head_id=id.salary_head_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and status = true and under_section in (4,5) " +
				"and exemption_from=? and exemption_to=? and id.salary_head_id>0 and id.parent_section=0 and emp_id in ("+sbEmp.toString()+")" +
				" group by emp_id, ed.salary_head_id,ed.slab_type order by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println(" ee pst========"+pst);
			rs = pst.executeQuery();			
			Map<String, Map<String, String>> hmUnderSection10_16Map = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmUnderSection10_16PaidMap = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				String strsalaryheadid = rs.getString("salary_head_id");
				String slabType = rs.getString("slab_type");
				if(uF.parseToInt(hmEmpSlabType.get(rs.getString("emp_id")))!= uF.parseToInt(slabType) && uF.parseToInt(slabType)!=2) {
					continue;
				}

				double dblInvestment = rs.getDouble("amount_paid");
				Map hmEmpPayrollDetailss = (Map)hmPayrollDetails.get(rs.getString("emp_id"));
				if(hmEmpPayrollDetailss == null) hmEmpPayrollDetailss = new HashMap();
				
				if(dblInvestment == 0) {
					dblInvestment = uF.parseToDouble((String)hmEmpPayrollDetailss.get(strsalaryheadid));
				}
				dblInvestmentLimit = uF.parseToDouble((String)hmExemption.get(strsalaryheadid));
				double dblSalHeadAmt = uF.parseToDouble((String)hmEmpPayrollDetailss.get(strsalaryheadid));
				if(dblInvestmentLimit >dblSalHeadAmt) {
					dblInvestmentLimit = dblSalHeadAmt;
				} else if(dblInvestmentLimit==0) {
					dblInvestmentLimit = dblInvestment;
				}
				Map<String, String> hmInner= (Map<String, String>)hmUnderSection10_16Map.get(rs.getString("emp_id"));
				if(hmInner==null) hmInner=new HashMap<String, String>();
				
				double dblAllowanceExempt = Math.min(dblInvestment, dblInvestmentLimit);
							
				hmInner.put(rs.getString("salary_head_id"), ""+dblAllowanceExempt);				
				hmUnderSection10_16Map.put(rs.getString("emp_id"), hmInner);
				
				Map<String, String> hmInner11 = (Map<String, String>)hmUnderSection10_16PaidMap.get(rs.getString("emp_id"));
				if(hmInner11 == null) hmInner11 = new HashMap<String, String>();
				
				hmInner11.put(rs.getString("salary_head_id"), ""+dblInvestment);				
				hmUnderSection10_16PaidMap.put(rs.getString("emp_id"), hmInner11);
			} 
			rs.close();
			pst.close();
//			System.out.println("hmUnderSection10_16PaidMap ===>> " + hmUnderSection10_16PaidMap);
//			System.out.println("hmUnderSection10_16Map ===>> " + hmUnderSection10_16Map);
			
			
//			System.out.println("=======13========"+new Date());
//			System.out.println("hmUnderSection10Map====="+hmUnderSection10Map);
//			System.out.println("hmUnderSection10PaidMap====="+hmUnderSection10PaidMap);
			
			
			Map<String, Map<String, Map<String, String>>> hmEmpSectionwiseSubSecAdjusted10PerLimitAmt = new HashMap<String, Map<String, Map<String,String>>>();
			Map hmTaxLiability = new HashMap();
			Set set = hmPayrollDetails.keySet();
			Iterator it = set.iterator();
			while(it.hasNext()){
				Map<String, String> hmUS10_16_SalHeadData = new HashMap<String, String>();
				String strEmpId = (String)it.next();
				String slabType = hmEmpSlabType.get(strEmpId);
				
				Map hmTaxInner = new HashMap();
				Map hmEmpPayrollDetails = (Map)hmPayrollDetails.get(strEmpId);
				if(hmEmpPayrollDetails == null) hmEmpPayrollDetails = new HashMap();
				
				String strLevel = CF.getEmpLevelId(con, strEmpId);
				int nEmpOrgId = uF.parseToInt(CF.getEmpOrgId(con, uF, strEmpId));
				
				
				pst = con.prepareStatement("select * from exemption_details where exemption_from = ? and exemption_to = ? and (slab_type=? or slab_type=2)");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(slabType));
//				System.out.println("pst========"+pst);
				rs = pst.executeQuery();			
				while (rs.next()) {
					String strsalaryheadid = rs.getString("salary_head_id");
					
					double dblInvestment = uF.parseToDouble((String)hmEmpPayrollDetails.get(strsalaryheadid));
					if(uF.parseToInt(strsalaryheadid) == LTA) {
						dblInvestmentLimit = dblInvestment;
					} else {
						dblInvestmentLimit = uF.parseToDouble((String)hmExemption.get(strsalaryheadid));
					}
					Map<String, String> hmInner = (Map<String, String>)hmUnderSection10_16Map.get(strEmpId);
					if(hmInner==null) hmInner = new HashMap<String, String>();
					
					double dblAllowanceExempt = Math.min(dblInvestment, dblInvestmentLimit);
					
					if(!hmInner.containsKey(rs.getString("salary_head_id"))) {
						hmInner.put(rs.getString("salary_head_id"), ""+dblAllowanceExempt);				
						hmUnderSection10_16Map.put(strEmpId, hmInner);
					}
					Map<String, String> hmInner11 = (Map<String, String>)hmUnderSection10_16PaidMap.get(strEmpId);
					if(hmInner11 == null) hmInner11 = new HashMap<String, String>();
					
					if(!hmInner.containsKey(rs.getString("salary_head_id"))) {
						hmInner11.put(rs.getString("salary_head_id"), ""+dblInvestment);				
						hmUnderSection10_16PaidMap.put(strEmpId, hmInner11);
					}
				} 
				rs.close();
				pst.close();
				
				
				Map<String, String> hmEmployerPF = new HashMap<String, String>();
				boolean IsAddEmployerPFInTDSCal = CF.getFeatureManagementStatus(request, uF, F_ADD_EMPLYOER_PF_IN_TDS_CALCLATION);
				if(IsAddEmployerPFInTDSCal){
					pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? "
						+ "and org_id in (select org_id from employee_official_details where emp_id=?) and level_id in (select ld.level_id from grades_details gd, "
						+ "level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  "
						+ "and gd.grade_id in (select grade_id from employee_official_details where emp_id=? ))");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(strEmpId));
					pst.setInt(4, uF.parseToInt(strEmpId));
					rs = pst.executeQuery();
					Map<String, String> hmEPFPolicy = new HashMap<String, String>(); 
					while (rs.next()) {
						hmEPFPolicy.put("IS_ERPF_CONTRIBUTION", rs.getString("is_erpf_contribution"));
						hmEPFPolicy.put("IS_ERPS_CONTRIBUTION", rs.getString("is_erps_contribution"));
						hmEPFPolicy.put("IS_PF_ADMIN_CHARGES", rs.getString("is_pf_admin_charges"));
						hmEPFPolicy.put("IS_EDLI_ADMIN_CHARGES", rs.getString("is_edli_admin_charges"));
						hmEPFPolicy.put("IS_ERDLI_CONTRIBUTION", rs.getString("is_erdli_contribution"));
					}
					rs.close();
					pst.close();
					
					
					pst = con.prepareStatement("select pg.emp_id,sum(erpf_contribution) as erpf_contribution,sum(erps_contribution) as erps_contribution," +
						"sum(erdli_contribution) as erdli_contribution,sum(pf_admin_charges) as pf_admin_charges,sum(edli_admin_charges) as edli_admin_charges " +
						"from emp_epf_details eed,payroll_generation pg where eed.emp_id=pg.emp_id and financial_year_start=? and financial_year_end=? " +
						"and _month=month and financial_year_from_date=? and financial_year_to_date=? and pg.salary_head_id=? and pg.emp_id=? group by pg.emp_id");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(5, EMPLOYEE_EPF);
					pst.setInt(6, uF.parseToInt(strEmpId));
					rs = pst.executeQuery();
//					System.out.println(" 3 pst==>"+pst);
			 		while(rs.next()){
						double dblERPF = hmEPFPolicy!=null && uF.parseToBoolean(hmEPFPolicy.get("IS_ERPF_CONTRIBUTION")) ? rs.getDouble("erpf_contribution") : 0.0d;
						dblERPF += hmEPFPolicy!=null && uF.parseToBoolean(hmEPFPolicy.get("IS_ERPS_CONTRIBUTION")) ? rs.getDouble("erps_contribution") : 0.0d;
						dblERPF += hmEPFPolicy!=null && uF.parseToBoolean(hmEPFPolicy.get("IS_ERDLI_CONTRIBUTION")) ? rs.getDouble("erdli_contribution") : 0.0d;
						dblERPF += hmEPFPolicy!=null && uF.parseToBoolean(hmEPFPolicy.get("IS_PF_ADMIN_CHARGES")) ? rs.getDouble("pf_admin_charges") : 0.0d;
						dblERPF += hmEPFPolicy!=null && uF.parseToBoolean(hmEPFPolicy.get("IS_EDLI_ADMIN_CHARGES")) ? rs.getDouble("edli_admin_charges") : 0.0d;
						
						hmEmployerPF.put(rs.getString("emp_id"), uF.formatIntoTwoDecimal(Math.round(dblERPF)));
					}
					rs.close();
					pst.close();
				}
				
				double dblPerkAlignTDSAmount = CF.getPerkAlignTDSAmount(con, CF,uF, uF.parseToInt(strEmpId), strFinancialYearStart, strFinancialYearEnd, nEmpOrgId, uF.parseToInt(strLevel));
				
//				double dblBasic = uF.parseToDouble((String)hmEmpPayrollDetails.get(BASIC+""));
//				double dblDA = uF.parseToDouble((String)hmEmpPayrollDetails.get(DA+""));
				
				String strTDSAmt = (String)hmSalaryHeadMap.get(TDS+"");
				
//				String strConveyanceAllowance = (String)hmSalaryHeadMap.get(CONVEYANCE_ALLOWANCE+"");
//				String strProfessionalTax = (String)hmSalaryHeadMap.get(PROFESSIONAL_TAX+"");
				
//				System.out.println(" uF.parseToDouble((String)hmEmpPayrollDetails.get(\"GROSS\")) ===>> " + uF.parseToDouble((String)hmEmpPayrollDetails.get("GROSS")));
				double dblGross1 = uF.parseToDouble((String)hmEmpPayrollDetails.get("GROSS"));
//				if(strEmpId!=null && strEmpId.equalsIgnoreCase("48")){
//					System.out.println(" ------------ dblGross1 ===>> " + dblGross1);
//				}
				dblGross1 = dblGross1 - dblPerkAlignTDSAmount;
				hmEmpPayrollDetails.put("GROSS",""+dblGross1);
//				if(strEmpId!=null && strEmpId.equalsIgnoreCase("48")){
//					System.out.println(" ------------ dblPerkAlignTDSAmount ===>> " + dblPerkAlignTDSAmount);
//					System.out.println(" ------------ dblGross2 ===>> " + dblGross1);
//				}
				
				
				/**
				 * PREV ORG GROSS AMOUNT AND TDS AMOUNT 
				 * */
				double dblPrevOrgGross = 0.0d;
				double dblPrevOrgTDSAmount = 0.0d;
				boolean isJoinDateBetween = uF.isDateBetween(uF.getDateFormatUtil(strFinancialYearStart, DATE_FORMAT), uF.getDateFormatUtil(strFinancialYearEnd, DATE_FORMAT), uF.getDateFormatUtil(hmEmpJoiningDate.get(strEmpId), DATE_FORMAT));
				Map<String, String> hmPrevOrgTDSDetails = new HashMap<String, String>();
				if(isJoinDateBetween) {
					pst = con.prepareStatement("select * from prev_earn_deduct_details where financial_start=? and financial_end=? and emp_id=?");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(strEmpId));
					rs = pst.executeQuery();
					if(rs.next()) {
						dblPrevOrgGross = rs.getDouble("gross_amount");
						dblPrevOrgTDSAmount = rs.getDouble("tds_amount");
						hmPrevOrgTDSDetails.put(strEmpId+"_GROSS_AMT", ""+rs.getDouble("gross_amount"));
						hmPrevOrgTDSDetails.put(strEmpId+"_TDS_AMT", ""+rs.getDouble("tds_amount"));
					}
					rs.close();
					pst.close();
				}
				request.setAttribute("hmPrevOrgTDSDetails", hmPrevOrgTDSDetails);
				hmTaxInner.put("hmPrevOrgTDSDetails", hmPrevOrgTDSDetails);
				
				
				
				double dblProfessionalTaxPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(PROFESSIONAL_TAX+""));
				double dblProfessionalTaxExemptLimit = uF.parseToDouble((String)hmExemption.get(PROFESSIONAL_TAX+""));
				double dblProfessionalTaxExempt = Math.min(dblProfessionalTaxPaid, dblProfessionalTaxExemptLimit);
				
//				if(strEmpId.equals("48")) {
//					System.out.println("hmEmpPayrollDetails : "+hmEmpPayrollDetails);
//					System.out.println("dblProfessionalTaxPaid : "+dblProfessionalTaxPaid);
//					System.out.println("dblProfessionalTaxExemptLimit : "+dblProfessionalTaxExemptLimit);
//					System.out.println("dblProfessionalTaxExempt : "+dblProfessionalTaxExempt);
//				}
				
				double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_EDU_TAX"));
				double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_STD_TAX"));
				double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_FLAT_TDS"));
				
				
				// Exemptions needs to worked out as other exemptions				
				
				double dblInvestment = uF.parseToDouble((String)hmInvestment.get(strEmpId));
				double dblEPFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(EMPLOYEE_EPF+""));
				
				double dblEPRFPaid = uF.parseToDouble(hmEmployerPF.get(strEmpId));
				if(hmEmpPayrollDetails.containsKey(EMPLOYER_EPF+"")){
					dblEPRFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(EMPLOYER_EPF+""));
				}
				
				double dblEPVFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(VOLUNTARY_EPF+""));
				
//				double dblChapterVIA1 = uF.parseToDouble(hmEmpExemptionsCH1Map.get(strEmpId));				
//				double dblChapterVIA2 = uF.parseToDouble(hmEmpExemptionsCH2Map.get(strEmpId));
				double dblEmpPF = dblEPFPaid + dblEPRFPaid + dblEPVFPaid;
				  
				 
				if(strEmpId.equals("460")){
//					System.out.println("dblInvestment==="+dblInvestment);
//					System.out.println("dblEPFPaid==="+dblEPFPaid);
//					System.out.println("dblEPRFPaid==="+dblEPRFPaid);
//					System.out.println("dblEPVFPaid==="+dblEPVFPaid);
//					System.out.println("dblInvestmentExempt==="+dblInvestmentExempt);
//					System.out.println("dblTotalInvestment===="+dblTotalInvestment); 
//					System.out.println("dblInvestmentExemption="+dblInvestmentExemption);
				}
				
				double dblConAllLimit = 0.0d;
				if(hmExEmp.containsKey(strEmpId)) {
					int nMonth = uF.parseToInt(hmMonthPaid.get(strEmpId));
					double dblConAmt = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) * (nMonth - 1);
					double dblTotalDaysAmt = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) / uF.parseToDouble(hmLastPaycycle.get(strEmpId+"_TOTALDAYS"));
					double dblPaidDaysAmt = dblTotalDaysAmt * uF.parseToDouble(hmLastPaycycle.get(strEmpId+"_PAIDDAYS"));
					if(uF.parseToDouble(hmLastPaycycle.get(strEmpId+"_PAIDDAYS")) > 15.0d) {
						pst = con.prepareStatement("select * from payroll_generation where emp_id=? and paycycle=? and salary_head_id=?");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setInt(2, uF.parseToInt(hmLastPaycycle.get(strEmpId+"_PAYCYCLE")));
						pst.setInt(3, CONVEYANCE_ALLOWANCE);
//							System.out.println(" 4 pst========"+pst);
						rs = pst.executeQuery();			
						double dblLastPaidConveyance = 0;		
						while (rs.next()) {
							dblLastPaidConveyance = rs.getDouble("amount");
						}
						rs.close();
						pst.close();
						
						if(dblLastPaidConveyance > (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12)) {
							dblLastPaidConveyance = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12);
						}
						
						dblPaidDaysAmt = dblLastPaidConveyance;
					}
					
					dblConAllLimit = dblConAmt + dblPaidDaysAmt;
				} else {
					dblConAllLimit = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) * uF.parseToInt(hmMonthPaid.get(strEmpId));
				}
				double dblConveyanceAllowanceLimit = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblConAllLimit));
				double dblConveyanceAllowancePaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(CONVEYANCE_ALLOWANCE+""));
				double dblConveyanceAllowanceExempt = Math.min(dblConveyanceAllowancePaid, dblConveyanceAllowanceLimit);
				hmUS10_16_SalHeadData.put(CONVEYANCE_ALLOWANCE+"_PAID", ""+dblConveyanceAllowancePaid);
				hmUS10_16_SalHeadData.put(CONVEYANCE_ALLOWANCE+"_EXEMPT", (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+"")) >0) ? ""+dblConveyanceAllowanceExempt : ""+dblConveyanceAllowancePaid);
				
				
				int nEmpChildCnt = uF.parseToInt(hmEmpChildCnt.get(strEmpId)) > 2 ? 2 : uF.parseToInt(hmEmpChildCnt.get(strEmpId));
				double dblEducationAllowanceLimit = ((uF.parseToDouble((String)hmExemption.get(EDUCATION_ALLOWANCE+""))/12) * nEmpChildCnt) * uF.parseToInt(hmMonthPaid.get(strEmpId));
				double dblEducationAllowancePaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(EDUCATION_ALLOWANCE+""));
				double dblEducationAllowanceExempt = Math.min(dblEducationAllowancePaid, dblEducationAllowanceLimit);
				hmUS10_16_SalHeadData.put(EDUCATION_ALLOWANCE+"_PAID", ""+dblEducationAllowancePaid);
				hmUS10_16_SalHeadData.put(EDUCATION_ALLOWANCE+"_EXEMPT", ""+dblEducationAllowanceExempt);
				
				
				String[] hraSalaryHeads = null;
				if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null) {
					hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
				}
				
				double dblHraSalHeadsAmount = 0;
				for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++) {
					dblHraSalHeadsAmount += uF.parseToDouble((String)hmEmpPayrollDetails.get(hraSalaryHeads[i]));
				}
				
				
				String strHRA = (String)hmEmpPayrollDetails.get(HRA+"");
				
				String strCondition1 = (String)hmHRAExemption.get("CONDITION_1");
				String strCondition2 = (String)hmHRAExemption.get("CONDITION_2");
				String strCondition3 = (String)hmHRAExemption.get("CONDITION_3");
				
				double dblCondition1 = uF.parseToDouble(strCondition1);
				double dblCondition2 = uF.parseToDouble(strCondition2);
				double dblCondition3 = uF.parseToDouble(strCondition3);
				
				double dblHRA1 = dblCondition1 * dblHraSalHeadsAmount / 100;
				double dblHRA2 = dblCondition2 * dblHraSalHeadsAmount / 100;
				double dblHRA3 = dblCondition3 * dblHraSalHeadsAmount / 100;
				
				double dblActualHRAPaid = uF.parseToDouble(strHRA);
				double dblActualRentPaid = uF.parseToDouble((String)hmRentPaid.get(strEmpId));
				
				double dblHRAExemption = Math.min(dblActualHRAPaid, dblActualRentPaid);
				boolean isMetro = uF.parseToBoolean((String)hmEmpMertoMap.get(strEmpId));
				double dblHRA = 0;
				if(isMetro) {
					if(dblHRA1<dblHRA2) {
						dblHRA = dblHRA1; 
						hmTaxInner.put("dblHRA", "Rent paid over "+dblCondition1+"% of salary");
					} else{
						dblHRA = dblHRA2;
						hmTaxInner.put("dblHRA", "");
						hmTaxInner.put("dblHRA", dblCondition2+"% of salary in metro cities");
					}
				} else{
					if(dblHRA1<dblHRA3) {
						dblHRA = dblHRA1;
						hmTaxInner.put("dblHRA", "");
						hmTaxInner.put("dblHRA", "Rent paid over "+dblCondition1+"% of salary");
					} else{
						dblHRA = dblHRA3;
						hmTaxInner.put("dblHRA", "");
						hmTaxInner.put("dblHRA", dblCondition3+"% of salary in other cities");
					}
				}
				if(strEmpId.equals("52")) {
//					System.out.println("dblActualHRAPaid==="+dblActualHRAPaid);
//					System.out.println("dblActualRentPaid==="+dblActualRentPaid);
//					System.out.println("dblHRAExemption==="+dblHRAExemption);
//					System.out.println("dblHRA==="+dblHRA);
				}
				
				dblHRAExemption = Math.min(dblHRAExemption, dblHRA);
				
				if(strEmpId.equals("52")) {
//					System.out.println("after dblHRAExemption==="+dblHRAExemption);
				}
				
				ApprovePayroll objApprovePayroll = new ApprovePayroll();
				objApprovePayroll.session = session;
				objApprovePayroll.request = request;
				objApprovePayroll.CF = CF;
				
				Map hmEmpRentPaidMap = objApprovePayroll.getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
//				double dblHRAExemptions = objApprovePayroll.getHRAExemptionCalculation(con, uF,null, hmEmpPayrollDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, 0.0, 0.0, hmEmpMertoMap, hmEmpRentPaidMap);
				double dblHRAExemptions = objApprovePayroll.getHRAExemptionCalculation(con, uF,null, hmEmpPayrollDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, dblActualHRAPaid, dblHraSalHeadsAmount, hmEmpMertoMap, hmEmpRentPaidMap);
				dblHRAExemption = dblHRAExemptions;
				hmUS10_16_SalHeadData.put(HRA+"_PAID", "");
				hmUS10_16_SalHeadData.put(HRA+"_EXEMPT", ""+dblHRAExemption);
				
				
				if(strEmpId.equals("52")) {
//					System.out.println("after after dblHRAExemption==="+dblHRAExemption);
//					System.out.println("dblHRAExemptions==="+dblHRAExemptions);
				}
				
				double dblIncomeFromOther = uF.parseToDouble(hmEmpIncomeFromOtherSourcesMap.get(strEmpId));
				double dblLessIncomeFromOther = uF.parseToDouble(hmEmpLessIncomeFromOtherSourcesMap.get(strEmpId));
				
				double dblUS10_16Exempt = 0.0d;
				Map<String, String> hmUS10Inner= (Map<String, String>)hmUnderSection10_16Map.get(strEmpId);
		        if(hmUS10Inner==null) hmUS10Inner=new HashMap<String, String>();
		        
		        Map<String, String> hmUS10InnerPaid= (Map<String, String>)hmUnderSection10_16PaidMap.get(strEmpId);
				if(hmUS10InnerPaid==null) hmUS10InnerPaid=new HashMap<String, String>();
				
				
				if(hmEmpPayrollDetails.containsKey(REIMBURSEMENT+"")) {
//					System.out.println("REIMBURSEMENT ===>> " + hmUS10InnerPaid.get(REIMBURSEMENT+""));
					hmUS10_16_SalHeadData.put(REIMBURSEMENT+"_PAID", hmUS10InnerPaid.get(REIMBURSEMENT+""));
					hmUS10_16_SalHeadData.put(REIMBURSEMENT+"_EXEMPT", hmUS10InnerPaid.get(REIMBURSEMENT+""));
				}
				
				Iterator<String> itUnderSection = hmExemptionDataUnderSection.keySet().iterator();
		        while (itUnderSection.hasNext()) {
		        	String underSectionId = itUnderSection.next();
			        Map<String, List<String>> hmUS10ExemptionData = hmExemptionDataUnderSection.get(underSectionId);
			        Iterator<String> itUS10Examption = hmUS10ExemptionData.keySet().iterator();
			        while (itUS10Examption.hasNext()) {
						String exemptionId = itUS10Examption.next();
						List<String> innerList = hmUS10ExemptionData.get(exemptionId);
						if(uF.parseToInt(innerList.get(8)) == uF.parseToInt(slabType) || uF.parseToInt(innerList.get(8)) == 2) {
							double dblAmtExempt = uF.parseToDouble(hmUS10Inner.get(innerList.get(6)));
							if(uF.parseToInt(innerList.get(6)) == HRA || uF.parseToInt(innerList.get(6)) == CONVEYANCE_ALLOWANCE || uF.parseToInt(innerList.get(6)) == EDUCATION_ALLOWANCE) {
								dblAmtExempt = uF.parseToDouble(hmUS10_16_SalHeadData.get(innerList.get(6)+"_EXEMPT"));
//								System.out.println("dblAmtExempt : "+dblAmtExempt);
							}
							if(uF.parseToInt(innerList.get(6)) == 0) {
								dblAmtExempt = uF.parseToDouble(innerList.get(3));
							}
							dblUS10_16Exempt += dblAmtExempt;
						}
			        }
		        }
		        
//		        System.out.println("dblUS10_16Exempt ===>> " + dblUS10_16Exempt);
		        double dblVIA2Exempt = 0.0d;
				double dblVIA1Exempt = 0.0d;
				double dblAddExemptInAdjustedGrossIncome = 0.0d;
				
				dblInvestmentLimit = 0;
//					double dblInvest = 0.0d;
				
//					Here we Calculate VI A1
				/**
				 * Change by RAHUL PATIL on 31Aug18 based on Crave Infotech Case found
				 * */
//					System.out.println("hmEmpInvestment(strEmpId) 55 ================>> " + hmEmpInvestment.get(strEmpId));
				Map<String,String> hmInvest = hmEmpInvestment.get(strEmpId);
				if(hmInvest == null) hmInvest = new HashMap<String, String>();
				Iterator<String> it1 = hmInvest.keySet().iterator();
				List<String> alSectionId = new ArrayList<String>();
				while(it1.hasNext()) {
					String strSectionId = it1.next();
					
					if(hmSectionLimitA.containsKey(strSectionId+"_"+slabType)) {
						dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType));
						double dblVIA1Invest = uF.parseToDouble(hmInvest.get(strSectionId));
						if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId+"_"+slabType))) {
							alSectionId.add(strSectionId);
							dblVIA1Invest += dblEmpPF;
						}
//						System.out.println("strSectionId ===>> " + strSectionId + " -- dblInvestmentLimit ===>> " + dblInvestmentLimit);
						if(dblInvestmentLimit>=0) {
							dblVIA1Invest = Math.min(dblVIA1Invest, dblInvestmentLimit);
						}
						if(uF.parseToBoolean(hmSectionAdjustedGrossIncomeLimitStatus.get(strSectionId+"_"+slabType))) {
							dblAddExemptInAdjustedGrossIncome += dblVIA1Invest;
						}
						dblVIA1Exempt += dblVIA1Invest;
					} else {
						dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType));
						double dblVIA1Invest = uF.parseToDouble(hmInvest.get(strSectionId));
						if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId+"_"+slabType))) {
							alSectionId.add(strSectionId);
							dblVIA1Invest += dblEmpPF;
						}
						dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
						if(dblInvestmentLimit>=0) {
							dblVIA1Invest = Math.min(dblVIA1Invest, dblInvestmentLimit);
						}
						if(uF.parseToBoolean(hmSectionAdjustedGrossIncomeLimitStatus.get(strSectionId+"_"+slabType))) {
							dblAddExemptInAdjustedGrossIncome += dblVIA1Invest;
						}
						dblVIA1Exempt += dblVIA1Invest;
					}
				
				}
//					Here we Calculate VI A2 
				/**
				 * Change by RAHUL PATIL on 31Aug18 based on Craveinfotech Case found
				 * */
//					System.out.println("hmEmpInvestment1.get(strEmpId) 55 ================>> " + hmEmpInvestment1.get(strEmpId));
				Map<String,String> hmInvest2 = hmEmpInvestment1.get(strEmpId);
				if(hmInvest2 == null) hmInvest2 = new HashMap<String, String>();
				Iterator<String> it2 = hmInvest2.keySet().iterator();
				while(it2.hasNext()) {
					String strSectionId = it2.next();
					if(uF.parseToBoolean(hmSectionAdjustedGrossIncomeLimitStatus.get(strSectionId+"_"+slabType))) {
						dblAddExemptInAdjustedGrossIncome += uF.parseToDouble(hmInvest2.get(strSectionId));
					}
					dblVIA2Exempt += uF.parseToDouble(hmInvest2.get(strSectionId));
				}
				
				
				double dblAdjustedGrossTotalIncome = (dblPrevOrgGross + dblGross1 + dblAddExemptInAdjustedGrossIncome + dblIncomeFromOther) - dblUS10_16Exempt - dblVIA1Exempt - dblVIA2Exempt - dblLessIncomeFromOther; //- dblHomeLoanTaxExempt
//				System.out.println("dblAdjustedGrossTotalIncome ===>> " + dblAdjustedGrossTotalIncome);
				request.setAttribute("dblAdjustedGrossTotalIncome", ""+dblAdjustedGrossTotalIncome);
				
				pst = con.prepareStatement("select * from section_details where financial_year_start=? and financial_year_end=? " +
					" and (slab_type=? or slab_type=2) and section_code not in ('HRA') and isdisplay=true and is_adjusted_gross_income_limit=true "); //and sd.section_id !=11
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(slabType));
//				System.out.println(" pst==>"+pst);
				rs = pst.executeQuery();
				dblInvestmentLimit = 0;
				dblInvestmentEmp = 0;
				Map<String, Map<String, String>> hmSectionwiseSubSecIsAdjustedStatus = new LinkedHashMap<String, Map<String, String>>();
				while(rs.next()) {
					Map<String, String> hmSubSecIsAdjustedStatus = new HashMap<String, String>();
					hmSubSecIsAdjustedStatus.put("UNDER_SECTION", rs.getString("under_section"));
					hmSubSecIsAdjustedStatus.put("SUB_SEC_1_IS_ADJUSTED_STATUS", rs.getString("sub_section_1_is_adjust_gross_income_limit"));
					hmSubSecIsAdjustedStatus.put("SUB_SEC_2_IS_ADJUSTED_STATUS", rs.getString("sub_section_2_is_adjust_gross_income_limit"));
					hmSubSecIsAdjustedStatus.put("SUB_SEC_3_IS_ADJUSTED_STATUS", rs.getString("sub_section_3_is_adjust_gross_income_limit"));
					hmSubSecIsAdjustedStatus.put("SUB_SEC_4_IS_ADJUSTED_STATUS", rs.getString("sub_section_4_is_adjust_gross_income_limit"));
					hmSubSecIsAdjustedStatus.put("SUB_SEC_5_IS_ADJUSTED_STATUS", rs.getString("sub_section_5_is_adjust_gross_income_limit"));
					
					hmSectionwiseSubSecIsAdjustedStatus.put(rs.getString("section_id"), hmSubSecIsAdjustedStatus);
				}
				rs.close();
				pst.close();
//				System.out.println(" hmSectionwiseSubSecIsAdjustedStatus ===>> " + hmSectionwiseSubSecIsAdjustedStatus);
				
				Map<String, List<Map<String, String>>> hmSubInvestment = hmEmpSubInvestment.get(strEmpId);
				if(hmSubInvestment ==null)hmSubInvestment = new HashMap<String, List<Map<String,String>>>();
				
				Map<String,String> hmInvest11 = new HashMap<String, String>();
				
				Iterator<String> itSubSec = hmSectionwiseSubSecIsAdjustedStatus.keySet().iterator();
				Map<String, Map<String, String>> hmSectionwiseSubSecAdjusted10PerLimitAmt = new HashMap<String, Map<String,String>>();
				while (itSubSec.hasNext()) {
					String strSectionId = itSubSec.next();
					int intUnderSec = 0;
					Map<String, String> hmSubSecIsAdjustedStatus = hmSectionwiseSubSecIsAdjustedStatus.get(strSectionId);
					double dblSecInvestment =0;
					if(hmInvest11.keySet().contains(strSectionId)) {
						dblSecInvestment = 0;
					} else {
						dblSecInvestment = uF.parseToDouble(hmInvest11.get(strSectionId));
					}
					List<Map<String, String>> alSubInvestment = hmSubInvestment.get(strSectionId);
					if(alSubInvestment ==null)alSubInvestment = new ArrayList<Map<String, String>>();
					Map<String, String> hmSubSecAdjusted10PerLimitAmt = hmSectionwiseSubSecAdjusted10PerLimitAmt.get(strSectionId);
					if(hmSubSecAdjusted10PerLimitAmt==null)hmSubSecAdjusted10PerLimitAmt = new HashMap<String, String>();
					
					for(int i=0; i<alSubInvestment.size(); i++) {
						Map<String, String> hm = alSubInvestment.get(i);
						boolean blnIsAdjustedLimit = false;
						intUnderSec = uF.parseToInt(hm.get("UNDER_SECTION"));
						dblInvestment = uF.parseToDouble(hm.get("PAID_AMOUNT"));
						double dblSubSecLimit = uF.parseToDouble(hm.get("SUB_SECTION_AMOUNT"));
						if(uF.parseToInt(hm.get("SUB_SEC_NO")) ==1 && uF.parseToBoolean(hmSubSecIsAdjustedStatus.get("SUB_SEC_1_IS_ADJUSTED_STATUS"))) {
							blnIsAdjustedLimit = true;
						} else if(uF.parseToInt(hm.get("SUB_SEC_NO")) ==2 && uF.parseToBoolean(hmSubSecIsAdjustedStatus.get("SUB_SEC_2_IS_ADJUSTED_STATUS"))) {
							blnIsAdjustedLimit = true;
						} else if(uF.parseToInt(hm.get("SUB_SEC_NO")) ==3 && uF.parseToBoolean(hmSubSecIsAdjustedStatus.get("SUB_SEC_3_IS_ADJUSTED_STATUS"))) {
							blnIsAdjustedLimit = true;
						} else if(uF.parseToInt(hm.get("SUB_SEC_NO")) ==4 && uF.parseToBoolean(hmSubSecIsAdjustedStatus.get("SUB_SEC_4_IS_ADJUSTED_STATUS"))) {
							blnIsAdjustedLimit = true;
						} else if(uF.parseToInt(hm.get("SUB_SEC_NO")) ==5 && uF.parseToBoolean(hmSubSecIsAdjustedStatus.get("SUB_SEC_5_IS_ADJUSTED_STATUS"))) {
							blnIsAdjustedLimit = true;
						}
						
						if(blnIsAdjustedLimit) {
							double dbl10PerOfAdjustedIncome = (dblAdjustedGrossTotalIncome * 10) / 100;
//							System.out.println(dblSubSecLimit+ " -- dbl10PerOfAdjustedIncome ===>> " + dbl10PerOfAdjustedIncome);
							if(hm.get("SUB_SECTION_LIMIT_TYPE") != null && hm.get("SUB_SECTION_LIMIT_TYPE").equals("%")) {
								dblSubSecLimit = (dbl10PerOfAdjustedIncome * dblSubSecLimit) / 100;
							}
//							System.out.println("dblSubSecLimit ===>> " +dblSubSecLimit +" -- dblInvestment ===>> " + dblInvestment);
							if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
								dblSecInvestment += dblSubSecLimit;
							} else {
								dblSecInvestment += dblInvestment;
							}
							hmSubSecAdjusted10PerLimitAmt.put(hm.get("SUB_SEC_NO"), ""+dblSubSecLimit);
						} else {
							if(hm.get("SUB_SECTION_LIMIT_TYPE") != null && hm.get("SUB_SECTION_LIMIT_TYPE").equals("%")) {
								dblSubSecLimit = (dblInvestment * dblSubSecLimit) / 100;
							}
							if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
								dblSecInvestment += dblSubSecLimit;
							} else {
								dblSecInvestment += dblInvestment;
							}
						}
					}
					
					hmSectionwiseSubSecAdjusted10PerLimitAmt.put(strSectionId, hmSubSecAdjusted10PerLimitAmt);
					
					hmInvest11.put(strSectionId, ""+dblSecInvestment);
					if(intUnderSec == 8) {
						hmEmpInvestment.put(rs.getString("emp_id"), hmInvest11);
					} else if(intUnderSec == 9) {
						hmEmpInvestment1.put(rs.getString("emp_id"), hmInvest11);
					}
					
				}
				hmEmpSectionwiseSubSecAdjusted10PerLimitAmt.put(strEmpId, hmSectionwiseSubSecAdjusted10PerLimitAmt);
				
//				request.setAttribute("hmSectionwiseSubSecAdjusted10PerLimitAmt", hmSectionwiseSubSecAdjusted10PerLimitAmt);
				
				double dblVIA1ExemptIsAdjustedLimit = 0.0d;
				
				Iterator<String> it11 = hmInvest11.keySet().iterator();
				while(it11.hasNext()) {
					String strSectionId = it11.next();
					
					if(hmSectionLimitA.containsKey(strSectionId+"_"+slabType)) {
						dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType));
						double dblVIA1Invest = uF.parseToDouble(hmInvest11.get(strSectionId));
						if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId+"_"+slabType))) {
							alSectionId.add(strSectionId);
							dblVIA1Invest += dblEmpPF;
						}
//						System.out.println("strSectionId ===>> " + strSectionId + " -- dblInvestmentLimit ===>> " + dblInvestmentLimit);
						if(dblInvestmentLimit>=0) {
							dblVIA1Invest = Math.min(dblVIA1Invest, dblInvestmentLimit);
						}
						dblVIA1ExemptIsAdjustedLimit += dblVIA1Invest;
					} else {
						dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType));
						double dblVIA1Invest = uF.parseToDouble(hmInvest11.get(strSectionId));
						if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId+"_"+slabType))) {
							alSectionId.add(strSectionId);
							dblVIA1Invest += dblEmpPF;
						}
						dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
						if(dblInvestmentLimit>=0) {
							dblVIA1Invest = Math.min(dblVIA1Invest, dblInvestmentLimit);
						}
						dblVIA1ExemptIsAdjustedLimit += dblVIA1Invest;
					}
				}
				if(alSectionId == null || alSectionId.size()==0) {
					dblVIA1Exempt += dblEmpPF;
				}
//				if(strEmpId!=null && strEmpId.equalsIgnoreCase("48")){
//					System.out.println("dblPrevOrgGross ===>> " + dblPrevOrgGross);
//					System.out.println("dblGross1 ===>> " + dblGross1);
//					System.out.println("dblAddExemptInAdjustedGrossIncome ===>> " + dblAddExemptInAdjustedGrossIncome);
//					System.out.println("dblIncomeFromOther ===>> " + dblIncomeFromOther);
//					System.out.println("dblUS10_16Exempt ===>> " + dblUS10_16Exempt);
//					System.out.println("dblVIA1Exempt ===>> " + dblVIA1Exempt);
//					System.out.println("dblVIA2Exempt ===>> " + dblVIA2Exempt);
//					System.out.println("dblVIA1ExemptIsAdjustedLimit ===>> " + dblVIA1ExemptIsAdjustedLimit);
//				}
//				double dblNetTaxableIncome = (dblPrevOrgGross + dblGross1 + dblAddExemptInAdjustedGrossIncome + dblIncomeFromOther)- dblUS10_16Exempt - dblVIA1Exempt - dblVIA2Exempt - dblVIA1ExemptIsAdjustedLimit; //- dblHomeLoanTaxExempt
				double dblNetTaxableIncome = dblGross1;//Created By dattatray 8-06-2022

//				if(strEmpId.equalsIgnoreCase("48")){
//					System.out.println(strEmpId+" dblNetTaxableIncome=====>"+dblNetTaxableIncome);  
//				}
				
				hmTaxInner.put("dblNetTaxableIncome", dblNetTaxableIncome+"");
				hmTaxInner.put("dblIncomeFromOther", dblIncomeFromOther+"");
				hmTaxInner.put("dblLessIncomeFromOther", dblLessIncomeFromOther+"");
				hmTaxInner.put("dblActualHRAPaid", dblActualHRAPaid+"");  
				hmTaxInner.put("dblActualRentPaid", dblActualRentPaid+"");
				hmTaxInner.put("dblHRA1", dblHRA+"");
				hmTaxInner.put("dblCondition1", dblCondition1+"");
				hmTaxInner.put("dblHRAExemption", dblHRAExemption+"");
				
				hmTaxInner.put("dblProfessionalTaxPaid", dblProfessionalTaxPaid+"");
				hmTaxInner.put("dblProfessionalTaxExempt", dblProfessionalTaxExempt+"");
				
				hmTaxInner.put("dblConveyanceAllowancePaid", dblConveyanceAllowancePaid+"");
				hmTaxInner.put("dblConveyanceAllowanceExempt", dblConveyanceAllowanceExempt+"");
				
				hmTaxInner.put("hmEmpExemptionsCH1Map", hmEmpExemptionsCH1Map.get(strEmpId) +"");
				hmTaxInner.put("hmEmpExemptionsCH2Map", hmEmpExemptionsCH2Map.get(strEmpId) +"");
				hmTaxInner.put(TDS, strTDSAmt);
				
				hmTaxInner.put("dblEducationAllowancePaid", dblEducationAllowancePaid+"");
				hmTaxInner.put("dblEducationAllowanceExempt", dblEducationAllowanceExempt+""); 
				
				
				hmTaxInner.put("dblEmpPF", dblEmpPF+""); 
				
				double TDSPayable = 0;
				if(strEmpId!=null && strEmpId.equalsIgnoreCase("52")) {
					
//					System.out.println("dblGross="+dblGross1);
//					System.out.println("dblProfessionalTaxExempt="+dblProfessionalTaxExempt);
//					System.out.println("dblHRAExemption="+dblHRAExemption);
//					System.out.println("dblConveyanceAllowanceExempt="+dblConveyanceAllowanceExempt); 
////					System.out.println("dblInvestment="+dblInvestmentExempt);
////					System.out.println("dblHomeLoanTaxExempt="+dblHomeLoanTaxExempt);
////					System.out.println("dblOtherExemptions="+dblOtherExemptions);
//					System.out.println("dblIncomeFromOther="+dblIncomeFromOther);
//					System.out.println("dblNetTaxableIncome="+dblNetTaxableIncome);    
//					System.out.println("dblCess1="+dblCess1);
//					System.out.println("dblCess2="+dblCess2);
//					System.out.println("strFinancialYearStart="+strFinancialYearStart);
//					System.out.println("strFinancialYearEnd="+strFinancialYearEnd);
//					System.out.println("Gender="+(String)hmEmpGenderMap.get(strEmpId));
//					System.out.println("Age="+(String)hmEmpAgeMap.get(strEmpId));					
//					System.out.println("dblEducationAllowancePaid="+dblEducationAllowancePaid);
//					System.out.println("dblEducationAllowanceExempt="+dblEducationAllowanceExempt);
					
//					TDSPayable = calculateTDS(dblNetTaxableIncome, dblCess1, dblCess2, strFinancialYearStart, strFinancialYearEnd, strEmpId, (String)hmEmpGenderMap.get(strEmpId), (String)hmEmpAgeMap.get(strEmpId));		
				}
				
//				String strSlabType = CF.getEmpIncomeTaxSlabType(con, CF, strEmpId, strFinancialYearStart, strFinancialYearEnd);
				
				TDSPayable = calculateTDS(con, dblNetTaxableIncome, dblCess1, dblCess2, strFinancialYearStart, strFinancialYearEnd, strEmpId, (String)hmEmpGenderMap.get(strEmpId), (String)hmEmpAgeMap.get(strEmpId), slabType);
				TDSPayable = TDSPayable - dblPrevOrgTDSAmount;
				
				if(strEmpId.equalsIgnoreCase("52")) {
//					System.out.println(strEmpId+" dblNetTaxableIncome=====>"+dblNetTaxableIncome);  
//					System.out.println(strEmpId+" TDSPayable=====>"+TDSPayable);  
				}
				
				double dblMaxTaxableIncome = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_MAX_TAX_INCOME"));
				double dblRebateAmt = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_REBATE_AMOUNT"));
				
				double dblRebate = 0;
				if(dblNetTaxableIncome <= dblMaxTaxableIncome && TDSPayable <= dblMaxTaxableIncome){
					if(TDSPayable>=dblRebateAmt){
						dblRebate = dblRebateAmt;
					}else if(TDSPayable > 0 && TDSPayable < dblRebateAmt){
						dblRebate = TDSPayable;
					}
				}
				
				double dblCess1Amount = (dblCess1 * (TDSPayable-dblRebate) / 100);
				double dblCess2Amount = (dblCess2 * (TDSPayable-dblRebate) / 100);
				
				if(strEmpId.equals("52")){
//					System.out.println(" TAX_LIABILITY==>"+TDSPayable);        
//					System.out.println(" CESS1==>"+dblCess1);
//					System.out.println(" CESS2==>"+dblCess2);
//					System.out.println(" CESS1_AMOUNT==>"+dblCess1Amount);
//					System.out.println(" CESS2_AMOUNT==>"+dblCess2Amount);
//					System.out.println(" dblRebate==>"+dblRebate);
//					System.out.println(" TOTAL_TAX_LIABILITY==>"+((TDSPayable-dblRebate) + dblCess1Amount+ dblCess2Amount));
				}
				
				hmTaxInner.put("TAX_LIABILITY", TDSPayable+"");
				hmTaxInner.put("CESS1", dblCess1+"");
				hmTaxInner.put("CESS2", dblCess2+"");
				hmTaxInner.put("CESS1_AMOUNT", dblCess1Amount+"");
				hmTaxInner.put("CESS2_AMOUNT", dblCess2Amount+"");
				hmTaxInner.put("TOTAL_TAX_LIABILITY", ((TDSPayable-dblRebate) + dblCess1Amount+ dblCess2Amount)+""); 
				hmTaxInner.put("TAX_REBATE", dblRebate+"");
				hmTaxLiability.put(strEmpId, hmTaxInner);
				
				
			}
			
			pst = con.prepareStatement("select sum(amount) as amount , emp_id from payroll_generation where salary_head_id = ? and financial_year_from_date=? and financial_year_to_date=? and is_paid = true group by emp_id");
			pst.setInt(1, TDS);
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" 5 pst==>"+pst);
//			System.out.println("9 date==>"+new Date());
			Map<String, String> hmPaidTdsMap = new HashMap<String, String>();
			while(rs.next()){
				hmPaidTdsMap.put(rs.getString("emp_id"), rs.getString("amount"));
			}
			rs.close();
			pst.close();
//			System.out.println("=======14========"+new Date());     
			
			
			Map<String, List<List<DataStyle>>> hmReportExport = new HashMap<String, List<List<DataStyle>>>();
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
//			getForm24QHeader(reportListExport, strFinancialYearStart, strFinancialYearEnd, uF);
			reportListExport = getForm24QHeaderSheet1(strOrgName, strFinancialYearStart, strFinancialYearEnd, uF);
			if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
			hmReportExport.put("1", reportListExport);
			
			reportListExport = getForm24QHeaderSheet2(strOrgName, strFinancialYearStart, strFinancialYearEnd, uF);
			if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
			hmReportExport.put("2", reportListExport);
			
			reportListExport = getForm24QHeaderSheet3(strOrgName, strFinancialYearStart, strFinancialYearEnd, uF);
			if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
			hmReportExport.put("3", reportListExport);
			
			reportListExport = getForm24QHeaderSheet4(strOrgName, strFinancialYearStart, strFinancialYearEnd, uF);
			if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
			hmReportExport.put("4", reportListExport);
			
			reportListExport = getForm24QHeaderSheet5(strOrgName, strFinancialYearStart, strFinancialYearEnd, uF);
			if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
			hmReportExport.put("5", reportListExport);
			
			reportListExport = getForm24QHeaderSheet6(strOrgName, strFinancialYearStart, strFinancialYearEnd, uF);
			if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
			hmReportExport.put("6", reportListExport);
			
			reportListExport = getForm24QHeaderSheet7(strOrgName, strFinancialYearStart, strFinancialYearEnd, uF);
			if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
			hmReportExport.put("7", reportListExport);
			
			reportListExport = getForm24QHeaderSheet8(strOrgName, strFinancialYearStart, strFinancialYearEnd, uF);
			if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
			hmReportExport.put("8", reportListExport);
			
			reportListExport = getForm24QHeaderSheet9(strOrgName, strFinancialYearStart, strFinancialYearEnd, uF);
			if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
			hmReportExport.put("9", reportListExport);
			
			reportListExport = getForm24QHeaderSheet10(strOrgName, strFinancialYearStart, strFinancialYearEnd, uF);
			if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
			hmReportExport.put("10", reportListExport);
			
			reportListExport = getForm24QHeaderSheet11(strOrgName, strFinancialYearStart, strFinancialYearEnd, uF);
			if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
			hmReportExport.put("11", reportListExport);
			
			reportListExport = getForm24QHeaderSheet12(strOrgName, strFinancialYearStart, strFinancialYearEnd, uF);
			if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
			hmReportExport.put("12", reportListExport);
			
			reportListExport = getForm24QHeaderSheet13(strOrgName, strFinancialYearStart, strFinancialYearEnd, uF);
			if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
			hmReportExport.put("13", reportListExport);
			
			reportListExport = getForm24QHeaderSheet14(strOrgName, strFinancialYearStart, strFinancialYearEnd, uF);
			if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
			hmReportExport.put("14", reportListExport);
			
			reportListExport = getForm24QHeaderSheet15(strOrgName, strFinancialYearStart, strFinancialYearEnd, uF);
			if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
			hmReportExport.put("15", reportListExport);
			
			reportListExport = getForm24QHeaderSheet16(strOrgName, strFinancialYearStart, strFinancialYearEnd, uF);
			if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
			hmReportExport.put("16", reportListExport);
			
			reportListExport = getForm24QHeaderSheet17(strOrgName, strFinancialYearStart, strFinancialYearEnd, uF);
			if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
			hmReportExport.put("17", reportListExport);
			
			
			
			Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
			Map<String, String> hmOrgName = CF.getOrgName(con);
			Iterator<String> it1 = hmEmployeeMap.keySet().iterator();
			List<Map<String, String>> reportList=new ArrayList<Map<String,String>>();
			int i=0;
			while(it1.hasNext()) {
				String strEmpId = (String)it1.next();
				String strEmpLevelId = hmEmpLevelidMap.get(strEmpId);
				String slabType = hmEmpSlabType.get(strEmpId);
				String strEmpName = (String)hmEmployeeMap.get(strEmpId);
				Map<String, String> hmSalaryName = CF.getSalaryHeadsMap(con, uF.parseToInt(strEmpLevelId));
				
				i++;
				
				Map<String, String> hmInner=new HashMap<String, String>();
				
				hmInner.put("SRNO", ""+i);
				
				hmInner.put("EMP_PAN_NO", hmEmpPanNo.get(strEmpId));
				
				hmInner.put("EMP_PAN_REF_NO", "");
				hmInner.put("EMP_ID", strEmpId);				
				
				hmInner.put("EMP_NAME", strEmpName);

				double dblYears = uF.parseToDouble(hmEmpAgeMap.get(strEmpId));
				String strEmpGender = hmEmpGenderMap.get(strEmpId);
				String typeOfDeductee = "Others";
				if(strEmpGender !=null && strEmpGender.equalsIgnoreCase("F")) {
					typeOfDeductee = "Women";
				}
				if(dblYears>=80) {
					typeOfDeductee = "Very Senior Citizen";
				} else if(dblYears<80 && dblYears>=60) {
					typeOfDeductee = "Senior Citizen";
				}
				hmInner.put("EMP_RESIDENT_AGE", typeOfDeductee);
				
				Map<String, String> hmEmpPayrollDetails = (Map<String, String>)hmPayrollDetails.get(strEmpId);
				if(hmEmpPayrollDetails==null)hmEmpPayrollDetails = new HashMap<String, String>();
				
				Map<String, String> hmEmpOtherSalHeadDetails = (Map<String, String>)hmOtherSalaryHeadDetails.get(strEmpId);
				if(hmEmpOtherSalHeadDetails==null)hmEmpOtherSalHeadDetails = new HashMap<String, String>();
				
				Map<String, String> hmEmpOtherAllowHeadDetails = (Map<String, String>)hmOtherAllowanceHeadDetails.get(strEmpId);
				if(hmEmpOtherAllowHeadDetails==null)hmEmpOtherAllowHeadDetails = new HashMap<String, String>();
//				System.out.println("hmTaxInner : "+hmTaxLiability.get("48"));
				Map hmTaxInner = (Map)hmTaxLiability.get(strEmpId);
				if(hmTaxInner==null)hmTaxInner = new HashMap();
				
				double dblProfessionalTaxExempt = uF.parseToDouble((String)hmTaxInner.get("dblProfessionalTaxExempt"));
				
				double dblIncomeTax = uF.parseToDouble((String)hmTaxInner.get("TAX_LIABILITY"));
				double dblTotIncomeTax = uF.parseToDouble((String)hmTaxInner.get("TOTAL_TAX_LIABILITY"));
				double dblRebate = uF.parseToDouble((String)hmTaxInner.get("TAX_REBATE"));
				dblIncomeTax = dblIncomeTax > 0 ? (Math.round(dblIncomeTax) - +Math.round(dblRebate)) : dblIncomeTax;
				
				double dblTDS = uF.parseToDouble((String)hmPaidTdsMap.get(strEmpId));
				
				
//				double dblInvestment = uF.parseToDouble((String)hmTaxInner.get("dblInvestment"));

				List<String> alUnderSection=new ArrayList<String>();
				Map<String,String> hmInvest=hmEmpInvestment.get(strEmpId);
				if(hmInvest==null) hmInvest = new HashMap<String, String>();
				System.out.println("hmEmpInvestment.get(strEmpId) : "+hmEmpInvestment.get("48"));
				Map<String,String> hmInvest1=hmEmpInvestment1.get(strEmpId);
				if(hmInvest1==null) hmInvest1 = new HashMap<String, String>();
				
				Map<String, Map<String, String>> hmSectionwiseSubSecAdjusted10PerLimitAmt = hmEmpSectionwiseSubSecAdjusted10PerLimitAmt.get(strEmpId);
				if(hmSectionwiseSubSecAdjusted10PerLimitAmt==null) hmSectionwiseSubSecAdjusted10PerLimitAmt = new HashMap<String, Map<String, String>>();
				
				Map<String, List<Map<String, String>>> hmSubInvestment = (Map<String, List<Map<String, String>>>) hmEmpSubInvestment.get(strEmpId);
		  		if(hmSubInvestment==null) hmSubInvestment= new HashMap<String, List<Map<String, String>>>();
		  		
				Map<String,String> hmAcutualInvest = hmEmpActualInvestment.get(strEmpId);
		        if(hmAcutualInvest==null) hmAcutualInvest = new HashMap<String, String>();
		        
		        Map<String,String> hmAcutualInvest1 = hmEmpActualInvestment1.get(strEmpId);
		        if(hmAcutualInvest1==null) hmAcutualInvest1 = new HashMap<String, String>();
		        		        
		        Map<String, String> hmSubSecAdjusted10PerLimitAmt = hmSectionwiseSubSecAdjusted10PerLimitAmt.get(SECTION_80C_AND_80CCC+"");
				if(hmSubSecAdjusted10PerLimitAmt==null) hmSubSecAdjusted10PerLimitAmt = new HashMap<String, String>();
				
		        double str80DAmt = 0.0d;
		        String str80DSectionAmt = uF.showData(hmAcutualInvest.get(SECTION_80D+""), "");
		        if(hmAcutualInvest1.containsKey(SECTION_80D+"")) {
		        	str80DSectionAmt = uF.showData(hmAcutualInvest1.get(SECTION_80D+""), "");
		        }
				if(hmSectionLimitA.containsKey(SECTION_80D+"_"+slabType)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(SECTION_80D+"_"+slabType));
				} else {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(SECTION_80D+"_"+slabType));
					double dblVIA1Invest = uF.parseToDouble(hmInvest.get(SECTION_80D+""));
					dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
				}
				
				str80DAmt = uF.parseToDouble(str80DSectionAmt);
		  		if(dblInvestmentLimit>0) {
		  			str80DAmt = Math.min(str80DAmt, dblInvestmentLimit);
		  		}
		        
				
		  		double tot80DSubSecAmtOfSection = 0;
//		  		double tot80DPaidAmt = 0;
		  		List<Map<String, String>> sub80DInvestList = (List<Map<String, String>>) hmSubInvestment.get(SECTION_80D+"");
//		  		System.out.println("sub80DInvestList ===>> " + sub80DInvestList);
				for (int j = 0; sub80DInvestList != null && j < sub80DInvestList.size(); j++) {
					Map<String, String> hm = (Map<String, String>) sub80DInvestList.get(j);
					String strSubSecNo = hm.get("SUB_SEC_NO");
					double dblSubSec10PerAdjLimitAmt = uF.parseToDouble(hmSubSecAdjusted10PerLimitAmt.get(strSubSecNo));
					double dblPaidAmt = uF.parseToDouble(uF.showData(hm.get("PAID_AMOUNT"), ""));
//					tot80DPaidAmt += dblPaidAmt;
					double dblSubSecAmt = uF.parseToDouble(uF.showData(hm.get("SUB_SECTION_AMOUNT"), ""));
					String strSubSecLimitType = uF.showData(hm.get("SUB_SECTION_LIMIT_TYPE"), "");
					if(dblSubSecAmt>0) {
						if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
							dblSubSecAmt = (dblPaidAmt * dblSubSecAmt) / 100;
						} else {
							dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
						}
						if(dblSubSec10PerAdjLimitAmt>0) {
							dblSubSecAmt = Math.min(dblSubSecAmt, dblSubSec10PerAdjLimitAmt);
						} else if(dblSubSec10PerAdjLimitAmt<0) {
							dblSubSecAmt = 0;
						}
					
					} else {
						dblSubSecAmt = dblPaidAmt;
						dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
						if(dblSubSec10PerAdjLimitAmt>0) {
							dblSubSecAmt = Math.min(dblSubSecAmt, dblSubSec10PerAdjLimitAmt);
						} else if(dblSubSec10PerAdjLimitAmt<0) {
							dblSubSecAmt = 0;
						}
					}
					tot80DSubSecAmtOfSection += dblSubSecAmt;
				}
		  		
//				if(tot80DPaidAmt==0) {
//					tot80DPaidAmt = uF.parseToDouble(str80DSectionAmt);
//				}
				
				if(tot80DSubSecAmtOfSection>0 && str80DAmt>tot80DSubSecAmtOfSection) {
					str80DAmt = tot80DSubSecAmtOfSection;
	  				if(dblInvestmentLimit>0) {
	  					str80DAmt = Math.min(str80DAmt, dblInvestmentLimit);
	  				}
	  			}
				
				
				 double str80EAmt = 0.0d;
		        String str80ESectionAmt = uF.showData(hmAcutualInvest.get(SECTION_80E+""), "");
		        if(hmAcutualInvest1.containsKey(SECTION_80E+"")) {
		        	str80ESectionAmt = uF.showData(hmAcutualInvest1.get(SECTION_80E+""), "");
		        }
				if(hmSectionLimitA.containsKey(SECTION_80E+"_"+slabType)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(SECTION_80E+"_"+slabType));
				} else {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(SECTION_80E+"_"+slabType));
					double dblVIA1Invest = uF.parseToDouble(hmInvest.get(SECTION_80E+""));
					dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
				}
				str80EAmt = uF.parseToDouble(str80ESectionAmt);
		  		if(dblInvestmentLimit>0) {
		  			str80EAmt = Math.min(str80EAmt, dblInvestmentLimit);
		  		}
				
		  		double tot80ESubSecAmtOfSection = 0;
		  		List<Map<String, String>> sub80EInvestList = (List<Map<String, String>>) hmSubInvestment.get(SECTION_80E+"");
//			  		System.out.println("sub80DInvestList ===>> " + sub80DInvestList);
				for (int j = 0; sub80EInvestList != null && j < sub80EInvestList.size(); j++) {
					Map<String, String> hm = (Map<String, String>) sub80EInvestList.get(j);
					String strSubSecNo = hm.get("SUB_SEC_NO");
					double dblSubSec10PerAdjLimitAmt = uF.parseToDouble(hmSubSecAdjusted10PerLimitAmt.get(strSubSecNo));
					double dblPaidAmt = uF.parseToDouble(uF.showData(hm.get("PAID_AMOUNT"), ""));
					double dblSubSecAmt = uF.parseToDouble(uF.showData(hm.get("SUB_SECTION_AMOUNT"), ""));
					String strSubSecLimitType = uF.showData(hm.get("SUB_SECTION_LIMIT_TYPE"), "");
					if(dblSubSecAmt>0) {
						if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
							dblSubSecAmt = (dblPaidAmt * dblSubSecAmt) / 100;
						} else {
							dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
						}
						if(dblSubSec10PerAdjLimitAmt>0) {
							dblSubSecAmt = Math.min(dblSubSecAmt, dblSubSec10PerAdjLimitAmt);
						} else if(dblSubSec10PerAdjLimitAmt<0) {
							dblSubSecAmt = 0;
						}
					
					} else {
						dblSubSecAmt = dblPaidAmt;
						dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
						if(dblSubSec10PerAdjLimitAmt>0) {
							dblSubSecAmt = Math.min(dblSubSecAmt, dblSubSec10PerAdjLimitAmt);
						} else if(dblSubSec10PerAdjLimitAmt<0) {
							dblSubSecAmt = 0;
						}
					}
					tot80ESubSecAmtOfSection += dblSubSecAmt;
				}
		  		
				if(tot80ESubSecAmtOfSection>0 && str80DAmt>tot80ESubSecAmtOfSection) {
					str80EAmt = tot80ESubSecAmtOfSection;
	  				if(dblInvestmentLimit>0) {
	  					str80EAmt = Math.min(str80EAmt, dblInvestmentLimit);
	  				}
	  			}
				
				
				
				double str80GAmt = 0.0d;
		        String str80GSectionAmt = uF.showData(hmAcutualInvest.get(SECTION_80G_GN+""), "");
		        if(strEmpId.equals("52")) {
//		  			System.out.println(" str80GSectionAmt ====>> " + str80GSectionAmt);
		  		}
		        if(hmAcutualInvest1.containsKey(SECTION_80G_GN+"")) {
		        	str80GSectionAmt = uF.showData(hmAcutualInvest1.get(SECTION_80G_GN+""), "");
		        }
		        if(strEmpId.equals("52")) {
//		  			System.out.println("1 str80GSectionAmt ====>> " + str80GSectionAmt);
		  		}
				if(hmSectionLimitA.containsKey(SECTION_80G_GN+"_"+slabType)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(SECTION_80G_GN+"_"+slabType));
				} else {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(SECTION_80G_GN+"_"+slabType));
					double dblVIA1Invest = uF.parseToDouble(hmInvest.get(SECTION_80G_GN+""));
					dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
					if(strEmpId.equals("52")) {
//			  			System.out.println("dblVIA1Invest ===>> " + dblVIA1Invest);
			  		}
				}
				if(strEmpId.equals("52")) {
//		  			System.out.println("dblInvestmentLimit ====>> " + dblInvestmentLimit);
		  		}
				str80GAmt = uF.parseToDouble(str80GSectionAmt);
		  		if(dblInvestmentLimit>0) {
		  			str80GAmt = Math.min(str80GAmt, dblInvestmentLimit);
		  		}
		  		if(strEmpId.equals("52")) {
//		  			System.out.println(" str80GAmt ====>> " + str80GAmt);
		  		}
		  		
				
		  		double tot80GSubSecAmtOfSection = 0;
		  		List<Map<String, String>> sub80GInvestList = (List<Map<String, String>>) hmSubInvestment.get(SECTION_80G_GN+"");
//				  		System.out.println("sub80DInvestList ===>> " + sub80DInvestList);
				for (int j = 0; sub80GInvestList != null && j < sub80GInvestList.size(); j++) {
					Map<String, String> hm = (Map<String, String>) sub80GInvestList.get(j);
					String strSubSecNo = hm.get("SUB_SEC_NO");
					double dblSubSec10PerAdjLimitAmt = uF.parseToDouble(hmSubSecAdjusted10PerLimitAmt.get(strSubSecNo));
					double dblPaidAmt = uF.parseToDouble(uF.showData(hm.get("PAID_AMOUNT"), ""));
					double dblSubSecAmt = uF.parseToDouble(uF.showData(hm.get("SUB_SECTION_AMOUNT"), ""));
					String strSubSecLimitType = uF.showData(hm.get("SUB_SECTION_LIMIT_TYPE"), "");
					if(dblSubSecAmt>0) {
						if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
							dblSubSecAmt = (dblPaidAmt * dblSubSecAmt) / 100;
						} else {
							dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
						}
						if(dblSubSec10PerAdjLimitAmt>0) {
							dblSubSecAmt = Math.min(dblSubSecAmt, dblSubSec10PerAdjLimitAmt);
						} else if(dblSubSec10PerAdjLimitAmt<0) {
							dblSubSecAmt = 0;
						}
					
					} else {
						dblSubSecAmt = dblPaidAmt;
						dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
						if(dblSubSec10PerAdjLimitAmt>0) {
							dblSubSecAmt = Math.min(dblSubSecAmt, dblSubSec10PerAdjLimitAmt);
						} else if(dblSubSec10PerAdjLimitAmt<0) {
							dblSubSecAmt = 0;
						}
					}
					tot80GSubSecAmtOfSection += dblSubSecAmt;
				}
				
				if(tot80GSubSecAmtOfSection>0 && str80DAmt>tot80GSubSecAmtOfSection) {
					str80GAmt = tot80GSubSecAmtOfSection;
	  				if(dblInvestmentLimit>0) {
	  					str80GAmt = Math.min(str80GAmt, dblInvestmentLimit);
	  				}
	  			}
				
				
				reportListExport = hmReportExport.get("3");
				if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
				List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle(uF.showData(hmEmpCodeMap.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(strEmpName, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(hmEmpPanNo.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(hmEmpDesigMap.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(strFinancialYearStart, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(strFinancialYearEnd, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(typeOfDeductee, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(uF.parseToDouble((String)hmExemption.get("0"))), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(dblProfessionalTaxExempt)+"", ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(str80DAmt), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(str80EAmt), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(str80GAmt), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(dblTotIncomeTax)+"", ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(dblRebate)+"", ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(dblTDS)+"", ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				
				reportListExport.add(alInnerExport);
				hmReportExport.put("3", reportListExport);

				
				reportListExport = hmReportExport.get("4");
				if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle(uF.showData(hmEmpCodeMap.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(strEmpName, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(hmEmpPanNo.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(uF.parseToDouble(hmEmpPayrollDetails.get(BASIC+""))), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(uF.parseToDouble(hmEmpPayrollDetails.get(DA+""))), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(uF.parseToDouble(hmEmpPayrollDetails.get(HRA+""))), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				
				reportListExport.add(alInnerExport);
				hmReportExport.put("4", reportListExport);
				
				
				Iterator<String> itOS = hmEmpOtherSalHeadDetails.keySet().iterator();
				int osCnt=0;
				while(itOS.hasNext()) {
					String salHeadId = itOS.next();
					if(uF.parseToDouble(hmEmpOtherSalHeadDetails.get(salHeadId))<=0) {
						continue;
					}
					reportListExport = hmReportExport.get("5");
					if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
					alInnerExport = new ArrayList<DataStyle>();
					if(osCnt==0) {
						alInnerExport.add(new DataStyle(uF.showData(hmEmpCodeMap.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle(strEmpName, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle(uF.showData(hmEmpPanNo.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					} else {
						alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					}
					alInnerExport.add(new DataStyle(uF.showData(hmSalaryName.get(salHeadId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(uF.parseToDouble(hmEmpOtherSalHeadDetails.get(salHeadId))), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					
					reportListExport.add(alInnerExport);
					hmReportExport.put("5", reportListExport);
					osCnt++;
				}
				
				reportListExport = hmReportExport.get("6");
				if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
				alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle(uF.showData(hmEmpCodeMap.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(strEmpName, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(hmEmpPanNo.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(uF.parseToDouble(hmEmpOtherAllowHeadDetails.get("OTHER_ALLOW_TOTAL"))), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				
				reportListExport.add(alInnerExport);
				hmReportExport.put("6", reportListExport);
				
				
				if(uF.parseToDouble(hmEmpPayrollDetails.get(GRATUITY+""))>0 || uF.parseToDouble(hmEmpPayrollDetails.get(LEAVE_ENCASHMENT+""))>0) {
					reportListExport = hmReportExport.get("10");
					if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
					alInnerExport = new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(uF.showData(hmEmpCodeMap.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(strEmpName, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(hmEmpPanNo.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(uF.parseToDouble(hmEmpPayrollDetails.get(GRATUITY+""))), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(uF.parseToDouble(hmEmpPayrollDetails.get(LEAVE_ENCASHMENT+""))), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					
					reportListExport.add(alInnerExport);
					hmReportExport.put("10", reportListExport);
				}
				
				
				double dblIncomeFromOther = uF.parseToDouble((String)hmTaxInner.get("dblIncomeFromOther"));
//				String strSectionIncomeFromOtherSourceAmt = uF.showData(hmInvest.get(SECTION_INCOME_FROM_OTHER_SOURCE+""), "");
				if(dblIncomeFromOther>0) {
					reportListExport = hmReportExport.get("13");
					if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
					alInnerExport = new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(uF.showData(hmEmpCodeMap.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(strEmpName, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(hmEmpPanNo.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(hmSectionMap.get(""+SECTION_INCOME_FROM_OTHER_SOURCE), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(dblIncomeFromOther), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));

					reportListExport.add(alInnerExport);
					hmReportExport.put("13", reportListExport);
				}
				
				double str80CAmt = 0.0d;
				String str80CSectionAmt = uF.showData(hmInvest.get(SECTION_80C_AND_80CCC+""), "");
				
				if(hmSectionLimitA.containsKey(SECTION_80C_AND_80CCC+"_"+slabType)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(SECTION_80C_AND_80CCC+"_"+slabType));
				} else {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(SECTION_80C_AND_80CCC+"_"+slabType));
					double dblVIA1Invest = uF.parseToDouble(hmInvest.get(SECTION_80C_AND_80CCC+""));
					dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
				}
				
				str80CAmt = uF.parseToDouble(str80CSectionAmt);
		  		if(dblInvestmentLimit>0) {
		  			str80CAmt = Math.min(str80CAmt, dblInvestmentLimit);
		  		}
		  		
//		  		*********************************** Sheet 16 Start ********************************************
				if(hmSectionMap.containsKey(SECTION_80C_AND_80CCC+"") && uF.parseToBoolean(hmSectionPFApplicable.get(SECTION_80C_AND_80CCC+"_"+slabType))){
					str80CAmt += uF.parseToDouble(""+hmTaxInner.get("dblEmpPF"));
			  		if(dblInvestmentLimit>0) {
			  			str80CAmt = Math.min(str80CAmt, dblInvestmentLimit);
			  		}
					reportListExport = hmReportExport.get("16");
					if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
					alInnerExport = new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(uF.showData(hmEmpCodeMap.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(strEmpName, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(hmEmpPanNo.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("PF", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(uF.parseToDouble(hmTaxInner.get("dblEmpPF")+"")), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					
					reportListExport.add(alInnerExport);
					hmReportExport.put("16", reportListExport);
				}
				
				double totSubSecAmtOfSection=0;
				int sub80CCnt=0;
				List<Map<String, String>> subInvestList = (List<Map<String, String>>) hmSubInvestment.get(SECTION_80C_AND_80CCC+"");
				for (int j = 0; subInvestList != null && j < subInvestList.size(); j++) {
					Map<String, String> hm = (Map<String, String>) subInvestList.get(j);
					String strSubSecNo = hm.get("SUB_SEC_NO");
					double dblSubSec10PerAdjLimitAmt = uF.parseToDouble(hmSubSecAdjusted10PerLimitAmt.get(strSubSecNo));
					double dblPaidAmt = uF.parseToDouble(uF.showData(hm.get("PAID_AMOUNT"), ""));
					double dblSubSecAmt = uF.parseToDouble(uF.showData(hm.get("SUB_SECTION_AMOUNT"), ""));
					String strSubSecLimitType = uF.showData(hm.get("SUB_SECTION_LIMIT_TYPE"), "");
					if(dblSubSecAmt>0) {
						if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
							dblSubSecAmt = (dblPaidAmt * dblSubSecAmt) / 100;
						} else {
							dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
						}
						if(dblSubSec10PerAdjLimitAmt>0) {
							dblSubSecAmt = Math.min(dblSubSecAmt, dblSubSec10PerAdjLimitAmt);
						} else if(dblSubSec10PerAdjLimitAmt<0) {
							dblSubSecAmt = 0;
						}
					
					} else {
						dblSubSecAmt = dblPaidAmt;
						dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
						if(dblSubSec10PerAdjLimitAmt>0) {
							dblSubSecAmt = Math.min(dblSubSecAmt, dblSubSec10PerAdjLimitAmt);
						} else if(dblSubSec10PerAdjLimitAmt<0) {
							dblSubSecAmt = 0;
						}
					}
					totSubSecAmtOfSection += dblSubSecAmt;
					
					reportListExport = hmReportExport.get("16");
					if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
					alInnerExport = new ArrayList<DataStyle>();
					if(sub80CCnt==0 && !uF.parseToBoolean(hmSectionPFApplicable.get(SECTION_80C_AND_80CCC+"_"+slabType))) {
						alInnerExport.add(new DataStyle(uF.showData(hmEmpCodeMap.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle(strEmpName, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle(uF.showData(hmEmpPanNo.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					} else {
						alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					}
					alInnerExport.add(new DataStyle(uF.showData(hm.get("SECTION_NAME"), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(dblPaidAmt), "0"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					
					reportListExport.add(alInnerExport);
					hmReportExport.put("16", reportListExport);
					sub80CCnt++;
				}
				
				if(totSubSecAmtOfSection>0 && str80CAmt>totSubSecAmtOfSection) {
					str80CAmt = totSubSecAmtOfSection;
	  				if(hmSectionMap.containsKey(SECTION_80C_AND_80CCC+"") && uF.parseToBoolean(hmSectionPFApplicable.get(SECTION_80C_AND_80CCC+"_"+slabType))) {
	  					str80CAmt += uF.parseToDouble(""+hmTaxInner.get("dblEmpPF"));
	  				}
	  				if(dblInvestmentLimit>0) {
	  					str80CAmt = Math.min(str80CAmt, dblInvestmentLimit);
	  				}
//					dblChapterVIA += str80CAmt;
	  			} else {
//		  			dblChapterVIA += str80CAmt;
		  		}
			
				if(strEmpId.equals("42") || strEmpId.equals("56")) {
//		  			System.out.println(strEmpId+ " -- subInvestList ===>>> " + subInvestList);
//		  			System.out.println(strEmpId+ " -- SECTION_80C_AND_80CCC ===>>> " + uF.parseToDouble(hmAcutualInvest.get(SECTION_80C_AND_80CCC+"")));
		  		}
				
				if(subInvestList==null && uF.parseToDouble(hmAcutualInvest.get(SECTION_80C_AND_80CCC+""))>0.0) {
					reportListExport = hmReportExport.get("16");
					if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
					alInnerExport = new ArrayList<DataStyle>();
					if(!uF.parseToBoolean(hmSectionPFApplicable.get(SECTION_80C_AND_80CCC+"_"+slabType))) {
						alInnerExport.add(new DataStyle(uF.showData(hmEmpCodeMap.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle(strEmpName, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle(uF.showData(hmEmpPanNo.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					} else {
						alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					}
					alInnerExport.add(new DataStyle(uF.showData(hmSectionMap.get(SECTION_80C_AND_80CCC+""), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(uF.parseToDouble(hmAcutualInvest.get(SECTION_80C_AND_80CCC+""))), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					
					reportListExport.add(alInnerExport);
					hmReportExport.put("16", reportListExport);
					if(strEmpId.equals("42") || strEmpId.equals("56")) {
//			  			System.out.println(strEmpId+ "in if -- subInvestList ===>>> " + subInvestList);
//			  			System.out.println(strEmpId+ "in if -- SECTION_80C_AND_80CCC ===>>> " + uF.parseToDouble(hmAcutualInvest.get(SECTION_80C_AND_80CCC+"")));
			  		}
				}
//				********************************* Sheet 16 end ********************************
				if(strEmpId.equals("42")) {
//		  			System.out.println(strEmpId+ " -- hmReportExport ===>>> " + hmReportExport);
		  		}
				
				double str80CCDAmt = 0.0d;
				String str80CCDSectionAmt = uF.showData(hmInvest.get(SECTION_80CCD+""), "");
				if(hmSectionLimitA.containsKey(SECTION_80CCD+"_"+slabType)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(SECTION_80CCD+"_"+slabType));
				} else {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(SECTION_80CCD+"_"+slabType));
					double dblVIA1Invest = uF.parseToDouble(hmInvest.get(SECTION_80CCD+""));
					dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
				}
				
				str80CCDAmt = uF.parseToDouble(str80CCDSectionAmt);
		  		if(dblInvestmentLimit>0) {
		  			str80CCDAmt = Math.min(str80CCDAmt, dblInvestmentLimit);
		  		}
		  		
		  		double tot80CCDSubSecAmtOfSection = 0;
		  		double tot80CCDPaidAmt = 0;
		  		if(strEmpId.equals("460")) {
//		  			System.out.println("hmSubInvestment ===>>> " + hmSubInvestment);
		  		}
		  		List<Map<String, String>> sub80CCDInvestList = (List<Map<String, String>>) hmSubInvestment.get(SECTION_80CCD+"");
		  		if(strEmpId.equals("460")) {
//					System.out.println("sub80CCDInvestList ===>>> " + sub80CCDInvestList);
				}
				for (int j = 0; sub80CCDInvestList != null && j < sub80CCDInvestList.size(); j++) {
					Map<String, String> hm = (Map<String, String>) sub80CCDInvestList.get(j);
					String strSubSecNo = hm.get("SUB_SEC_NO");
					double dblSubSec10PerAdjLimitAmt = uF.parseToDouble(hmSubSecAdjusted10PerLimitAmt.get(strSubSecNo));
					double dblPaidAmt = uF.parseToDouble(uF.showData(hm.get("PAID_AMOUNT"), ""));
					tot80CCDPaidAmt += dblPaidAmt;
					double dblSubSecAmt = uF.parseToDouble(uF.showData(hm.get("SUB_SECTION_AMOUNT"), ""));
					String strSubSecLimitType = uF.showData(hm.get("SUB_SECTION_LIMIT_TYPE"), "");
					if(dblSubSecAmt>0) {
						if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
							dblSubSecAmt = (dblPaidAmt * dblSubSecAmt) / 100;
						} else {
							dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
						}
						if(dblSubSec10PerAdjLimitAmt>0) {
							dblSubSecAmt = Math.min(dblSubSecAmt, dblSubSec10PerAdjLimitAmt);
						} else if(dblSubSec10PerAdjLimitAmt<0) {
							dblSubSecAmt = 0;
						}
					
					} else {
						dblSubSecAmt = dblPaidAmt;
						dblSubSecAmt = Math.min(dblPaidAmt, dblSubSecAmt);
						if(dblSubSec10PerAdjLimitAmt>0) {
							dblSubSecAmt = Math.min(dblSubSecAmt, dblSubSec10PerAdjLimitAmt);
						} else if(dblSubSec10PerAdjLimitAmt<0) {
							dblSubSecAmt = 0;
						}
					}
					tot80CCDSubSecAmtOfSection += dblSubSecAmt;
				}
		  		
				if(strEmpId.equals("460")) {
//					System.out.println("tot80CCDPaidAmt ===>>> " + tot80CCDPaidAmt +" -- str80CCDSectionAmt ===>> " + str80CCDSectionAmt);
//					System.out.println("tot80CCDSubSecAmtOfSection ===>> " + tot80CCDSubSecAmtOfSection + " -- str80CCDAmt ===>> " + str80CCDAmt);
				}
				
				if(tot80CCDPaidAmt==0) {
					tot80CCDPaidAmt = uF.parseToDouble(str80CCDSectionAmt);
				}
				
				if(tot80CCDSubSecAmtOfSection>0 && str80CCDAmt>tot80CCDSubSecAmtOfSection) {
					str80CCDAmt = tot80CCDSubSecAmtOfSection;
	  				if(dblInvestmentLimit>0) {
	  					str80CCDAmt = Math.min(str80CCDAmt, dblInvestmentLimit);
	  				}
	  			}
		  		
		  		
				if((hmSectionMap.containsKey(SECTION_80CCD+"") && tot80CCDPaidAmt>0) || (hmSectionMap.containsKey(SECTION_80C_AND_80CCC+"") && str80CAmt>0)) {
					reportListExport = hmReportExport.get("15");
					if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
					alInnerExport = new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(uF.showData(hmEmpCodeMap.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(strEmpName, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(hmEmpPanNo.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(str80CAmt), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(tot80CCDPaidAmt), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(str80CCDAmt), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					
					reportListExport.add(alInnerExport);
					hmReportExport.put("15", reportListExport);
				}
				
				
				if((hmSectionMap.containsKey(SECTION_HOME_LOAN_INTEREST+"") && uF.parseToDouble(hmAcutualInvest.get(SECTION_HOME_LOAN_INTEREST+""))>0)) {
					reportListExport = hmReportExport.get("17");
					if(reportListExport==null)reportListExport = new ArrayList<List<DataStyle>>();
					alInnerExport = new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(uF.showData(hmEmpCodeMap.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(strEmpName, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(hmEmpPanNo.get(strEmpId), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(uF.parseToDouble(hmAcutualInvest.get(SECTION_80DD+""))), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(uF.parseToDouble(hmAcutualInvest.get(SECTION_80DDB_G+""))), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(uF.parseToDouble(hmAcutualInvest.get(SECTION_HOME_LOAN_INTEREST+""))), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(uF.parseToDouble(hmAcutualInvest.get(SECTION_80EEA+""))), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(uF.parseToDouble(hmAcutualInvest.get(SECTION_80EEB+""))), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(uF.parseToDouble(hmAcutualInvest.get(SECTION_80GG+""))), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(uF.parseToDouble(hmAcutualInvest.get(SECTION_80GGA+""))), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(uF.parseToDouble(hmAcutualInvest.get(SECTION_80RRB+""))), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(uF.parseToDouble(hmAcutualInvest.get(SECTION_80QQB+""))), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(uF.formatIntoComma(uF.parseToDouble(hmAcutualInvest.get(SECTION_80U+""))), ""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					
					reportListExport.add(alInnerExport);
					hmReportExport.put("17", reportListExport);
				}
				
				
				
				
				String joiningDate = hmEmpJoiningDate.get(strEmpId);
				String empDateFrom = strFinancialYearStart;
				if(uF.getDateFormat(joiningDate, DATE_FORMAT).after(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT))){
					empDateFrom = joiningDate;
				}
				hmInner.put("EMP_DATE_FROM", empDateFrom);
//				alInnerExport.add(new DataStyle(empDateFrom,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				String endDate = hmEmpEndDate.get(strEmpId);
				String empDateUp = strFinancialYearEnd;
				if(endDate!=null && !endDate.equals("") && !endDate.equals("-") && uF.getDateFormat(endDate, DATE_FORMAT).before(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT))){
					empDateUp = endDate;
				}
				hmInner.put("EMP_DATE_UP", empDateUp);
//				alInnerExport.add(new DataStyle(empDateUp,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				
				
				double dblActGross = uF.parseToDouble((String)hmEmpPayrollDetails.get("GROSS") );
				double dblHRAExemption = uF.parseToDouble((String)hmTaxInner.get("dblHRAExemption"));
				double dblConveyanceAllowanceExempt = uF.parseToDouble((String)hmTaxInner.get("dblConveyanceAllowanceExempt"));
				double dblOtherExemption = uF.parseToDouble((String)hmTaxInner.get("dblOtherExemptions"));
				double dblMedicalAllowanceExempt =uF.parseToDouble((String)hmTaxInner.get("dblMedicalAllowanceExempt"));
				double dblEducationAllowanceExempt = uF.parseToDouble((String)hmTaxInner.get("dblEducationAllowanceExempt"));
				double dblLTAExempt = uF.parseToDouble((String)hmTaxInner.get("dblLTAExempt"));
//				double dblGross1 = Math.round(dblActGross) - Math.round(dblHRAExemption) - Math.round(dblConveyanceAllowanceExempt) - Math.round(dblOtherExemption) - Math.round(dblMedicalAllowanceExempt) - Math.round(dblEducationAllowanceExempt) - Math.round(dblLTAExempt);
				double dblNetTaxableIncome = uF.parseToDouble((String)hmTaxInner.get("dblNetTaxableIncome"));
				hmInner.put("CURRENT_TAXABLE_TAX_AMT", ""+Math.round(dblNetTaxableIncome)); 
//				alInnerExport.add(new DataStyle(""+Math.round(dblNetTaxableIncome),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				Map<String, String> hmPreTdsEmp=(Map<String, String>) hmPrevEmpTds.get(strEmpId);
				if(hmPreTdsEmp == null) hmPreTdsEmp= new HashMap<String, String>();
				double dblPreGross =  uF.parseToDouble((String)hmPreTdsEmp.get("PREV_TOTAL_EARN") );
				hmInner.put("PREVIOUS_TAXABLE_TAX_AMT", ""+Math.round(dblPreGross));
//				alInnerExport.add(new DataStyle(""+Math.round(dblPreGross),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblTotalAmtSalary = Math.round(dblNetTaxableIncome) + Math.round(dblPreGross);
				hmInner.put("TOTAL_AMT_SALARY", ""+Math.round(dblTotalAmtSalary));
//				alInnerExport.add(new DataStyle(""+Math.round(dblTotalAmtSalary),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				
				double dblStandardDeductionExempt = uF.parseToDouble((String)hmExemption.get(STANDARD_DEDUCATION+""));//Created By dattatray 16-06-2022
				double dblEntertainmentAllowance = dblStandardDeductionExempt;//Created By dattatray 16-06-2022
				hmInner.put("ENTERTAINMENT_ALLOWANCE", ""+Math.round(dblEntertainmentAllowance));
//				alInnerExport.add(new DataStyle(""+Math.round(dblEntertainmentAllowance),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				
				hmInner.put("PROFESSIONAL_TAX", ""+Math.round(dblProfessionalTaxExempt));
//				alInnerExport.add(new DataStyle(""+Math.round(dblProfessionalTaxExempt),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double incomeChargeableSalary = Math.round(dblTotalAmtSalary) - (Math.round(dblEntertainmentAllowance) +Math.round(dblProfessionalTaxExempt)); 
				hmInner.put("INCOME_CHARGE_SALARY", ""+Math.round(incomeChargeableSalary));
//				alInnerExport.add(new DataStyle(""+Math.round(incomeChargeableSalary),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				
				hmInner.put("INCOME_FROM_OTHER", (dblIncomeFromOther > 0 ? "-"+dblIncomeFromOther : ""+dblIncomeFromOther));
//				alInnerExport.add(new DataStyle((dblIncomeFromOther > 0 ? "-"+dblIncomeFromOther : ""+dblIncomeFromOther),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblGrossTotal=Math.round(incomeChargeableSalary)-Math.round(dblIncomeFromOther);
				hmInner.put("GROSS_TOTAL", ""+Math.round(dblGrossTotal));
//				alInnerExport.add(new DataStyle(""+Math.round(dblGrossTotal),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				//-------------------------------------End---------------------------------------------------------------
				
				
				double dblChapterVIA1=0.0d;			
				double dblChapterVIA2= 0.0d;
				double dblChapterVIAOther=0.0d;
				System.out.println(" -- chapter1SectionList ============================>>> " + chapter1SectionList + " -- hmInvest ===>> " + hmInvest); //"hmSectionMap ===>> " + hmSectionMap + 
				for(int a=0;chapter1SectionList!=null && a<chapter1SectionList.size();a++) {
					List<String> innnList = chapter1SectionList.get(a);
					String strSectionId = innnList.get(0);
					String strSlabType = innnList.get(1);
					if(uF.parseToInt(slabType)!= uF.parseToInt(strSlabType) && uF.parseToInt(strSlabType)!=2) {
						continue;
					}
					
					double strAmt = 0.0d;
					String strSectionAmt = uF.showData(hmInvest.get(strSectionId), "");
//			  		System.out.println(strSectionId + " ------------ strSectionAmt ===>> " + strSectionAmt);
					if(hmSectionLimitA.containsKey(strSectionId+"_"+slabType)) {
						
						dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType));
						dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType+"_CEILING_AMT"));
						System.out.println(strSectionId + "-- A ------------ dblInvestmentLimit ===>> " + dblInvestmentLimit+" --- dblInvestmentCeilingLimit ===>> " + dblInvestmentCeilingLimit);
						if(dblInvestmentCeilingLimit>0) {
							dblInvestmentLimit = dblInvestmentCeilingLimit;
						}
					} else {
						dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType));
						double dblVIA1Invest = uF.parseToDouble(hmInvest.get(strSectionId));
						dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
						dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType+"_CEILING_AMT"));
						System.out.println(strSectionId + "-- P ------------ dblInvestmentLimit ===>> " + dblInvestmentLimit+" --- dblInvestmentCeilingLimit ===>> " + dblInvestmentCeilingLimit);
						if(dblInvestmentCeilingLimit>0) {
							dblInvestmentLimit = dblInvestmentCeilingLimit;
						}
					}
				
			  		strAmt = uF.parseToDouble(strSectionAmt);
			  		if(dblInvestmentLimit>0) {
			  			strAmt = Math.min(strAmt, dblInvestmentLimit);
			  		}
//			  		System.out.println(strSectionId + " ------------ strAmt ===>> " + strAmt + " -- dblInvestmentLimit ===>> " + dblInvestmentLimit);
			  		if(uF.parseToInt(strSectionId)==SECTION_80C_AND_80CCC || uF.parseToInt(strSectionId)== SECTION_80CCD) {
			  			dblChapterVIA1+=strAmt;
			  		} else if(uF.parseToInt(strSectionId)== SECTION_80CCG) {
			  			dblChapterVIA2+=strAmt;
			  		} else {
			  			dblChapterVIAOther+=strAmt;
			  		}
				}
//				System.out.println("dblChapterVIA1 ============================>>> " + dblChapterVIA1);
				
//				double dblChapterVIA1 = dblChapterVIA;
				hmInner.put("CHAPTER_VI_A1", ""+Math.round(dblChapterVIA1));
//				alInnerExport.add(new DataStyle(""+Math.round(dblChapterVIA1),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				hmInner.put("CHAPTER_VI_A2", ""+Math.round(dblChapterVIA2));
				
				
				for(int a=0;chapter2SectionList!=null && a<chapter2SectionList.size();a++) {
					List<String> innnList = chapter2SectionList.get(a);
					String strSectionId = innnList.get(0);
					String strSlabType = innnList.get(1);
					if(uF.parseToInt(slabType)!= uF.parseToInt(strSlabType) && uF.parseToInt(strSlabType)!=2) {
						continue;
					}
					String strAmt = uF.showData(hmInvest1.get(strSectionId), "");
					dblChapterVIAOther+=uF.parseToDouble(strAmt);
				}
//				System.out.println("chapter2SectionList ===========================>> " + chapter2SectionList);
//				System.out.println("dblChapterVIAOther ===========================>> " + dblChapterVIAOther);
				hmInner.put("CHAPTER_VI_AOTHER", ""+Math.round(dblChapterVIAOther));
//				alInnerExport.add(new DataStyle(""+Math.round(dblChapterVIAOther),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				System.out.println("Emp Name ===========================>> "+strEmpName);
				System.out.println("Math.round(dblChapterVIA1) ===========================>> " + Math.round(dblChapterVIA1));
				System.out.println("Math.round(dblChapterVIA2) ===========================>> " + Math.round(dblChapterVIA2));
				System.out.println("Math.round(dblChapterVIAOther) ===========================>> " + Math.round(dblChapterVIAOther));
				double dblTotalAmtChapetrVIA=Math.round(dblChapterVIA1)+Math.round(dblChapterVIA2)+Math.round(dblChapterVIAOther);
				hmInner.put("TOTAL_CHAPTER_VIA", ""+Math.round(dblTotalAmtChapetrVIA));
//				alInnerExport.add(new DataStyle(""+Math.round(dblTotalAmtChapetrVIA),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblTotalTaxIncome=Math.round(dblGrossTotal) - Math.round(dblTotalAmtChapetrVIA);
				System.out.println("Math.round(dblGrossTotal)  : "+Math.round(dblGrossTotal) );
				System.out.println("Math.round(dblTotalAmtChapetrVIA)  : "+Math.round(dblTotalAmtChapetrVIA) );
				hmInner.put("TOTAL_TAX_INCOME", ""+Math.round(dblTotalTaxIncome));
//				alInnerExport.add(new DataStyle(""+Math.round(dblTotalTaxIncome),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				if("460".equalsIgnoreCase(strEmpId)){
//					System.out.println("(String)hmTaxInner.get(TAX_LIABILIT)====>"+(String)hmTaxInner.get("TAX_LIABILITY"));
				}
				
				hmInner.put("INCOME_TAX", ""+Math.round(dblIncomeTax));
//				alInnerExport.add(new DataStyle(""+Math.round(dblIncomeTax),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
								
				
				hmInner.put("SURCHARGE", "0");
//				alInnerExport.add(new DataStyle("0",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblEduCess=uF.parseToDouble((String)hmTaxInner.get("CESS2_AMOUNT")) + uF.parseToDouble((String)hmTaxInner.get("CESS1_AMOUNT")); 
				hmInner.put("EDU_CESS", ""+Math.round(dblEduCess));
//				alInnerExport.add(new DataStyle(""+Math.round(dblEduCess),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblTaxRelief=0.0d;
				hmInner.put("INCOME_TAX_RELIEF", ""+Math.round(dblTaxRelief));
//				alInnerExport.add(new DataStyle(""+Math.round(dblTaxRelief),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblTaxPayable=Math.round(dblIncomeTax)+ Math.round(dblEduCess);
				
				double dblNetTaxIncome=Math.round(dblTaxPayable) - Math.round(dblTaxRelief);
				hmInner.put("NET_TAX_INCOME", ""+Math.round(dblNetTaxIncome));
//				alInnerExport.add(new DataStyle(""+Math.round(dblNetTaxIncome),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				hmInner.put("TDS_AMT", ""+Math.round(dblTDS));
//				alInnerExport.add(new DataStyle(""+Math.round(dblTDS),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblPreEmpTDS = uF.parseToDouble((String)hmPreTdsEmp.get("PREV_TOTAL_DEDUCT") );
				hmInner.put("PREV_EMP_TDS_AMT", ""+Math.round(dblPreEmpTDS));
//				alInnerExport.add(new DataStyle(""+Math.round(dblPreEmpTDS),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblTotalTDS=Math.round(dblTDS) + Math.round(dblPreEmpTDS);
				hmInner.put("TOTAL_TDS", ""+Math.round(dblTotalTDS));
//				alInnerExport.add(new DataStyle(""+Math.round(dblTotalTDS),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblShortFallTDS=Math.round(dblNetTaxIncome) - Math.round(dblTotalTDS);
				//Crave Infotech & Consultancy Services Pvt Ltd. Muralidar Remane = 231, Kalpana Parekh=32, Nilesh Lohar=221
				if(uF.parseToInt(strEmpId) == 221 && hmOrgName.get(hmEmpOrgId.get(strEmpId)).equalsIgnoreCase("Crave Infotech & Consultancy Services Pvt Ltd.")) {
//					dblShortFallTDS = 0;
				}
				hmInner.put("SHORTFALL_TDS", ""+Math.round(dblShortFallTDS));
//				alInnerExport.add(new DataStyle(""+Math.round(dblShortFallTDS),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				hmInner.put("FURNISH_PAN", hmEmpPanNo.get(strEmpId)!=null ? "N" : "");
//				alInnerExport.add(new DataStyle((hmEmpPanNo.get(strEmpId)!=null ? "N" : ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				//-----------------------------End-------------------------------------------------
				
				hmInner.put("ERROR_DISCRIPTION", "");
//				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				reportList.add(hmInner);
//				reportListExport.add(alInnerExport);
			}

			request.setAttribute("reportList", reportList);
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmReportExport", hmReportExport);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
//		return SUCCESS;
	}
	
	
	
	
	/*public void viewForm24QReport(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			String[] strFianacialDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				strFianacialDates = getFinancialYear().split("-");
				strFinancialYearStart = strFianacialDates[0];
				strFinancialYearEnd = strFianacialDates[1];			
			} else {
				strFianacialDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFianacialDates[0] + "-" + strFianacialDates[1]);				
				strFinancialYearStart = strFianacialDates[0];
				strFinancialYearEnd = strFianacialDates[1];				 
			}
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmEmpSlabType = CF.getEmpIncomeTaxSlabTypeMap(con, CF, strFinancialYearStart, strFinancialYearEnd);
			
			Map<String, String> hmSectionMap = CF.getSectionMap(con,strFinancialYearStart,strFinancialYearEnd);
			
			Map<String,String> hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
			Map<String, String> hmDept =CF.getDeptMap(con);	
			Map<String, String> hmEmpAgeMap =CF.getEmpAgeMap(con, CF);
//			Map<String, String> hmEmpSlabMap = CF.getEmpSlabMap(con, CF);

			Map hmEmpMertoMap = new HashMap();
			Map hmEmpWlocationMap = new HashMap();
			Map hmEmpStateMap = new HashMap();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
			Map<String, String> hmOrg = CF.getOrgDetails(con, uF, getF_org());
			
			Map<String,Map<String,String>> hmPrevEmpTds = CF.getPrevEmpTdsDetails(con,uF,strFinancialYearStart,strFinancialYearEnd);
			
			Map hmEmpGenderMap = CF.getEmpGenderMap(con);
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("SELECT eod.emp_id,epd.emp_fname, epd.emp_mname,epd.emp_lname,epd.empcode,eod.depart_id,epd.emp_pan_no,epd.employment_end_date,epd.joining_date FROM employee_official_details eod, employee_personal_details epd WHERE epd.emp_per_id=eod.emp_id  " +
					"and eod.emp_id in (select distinct emp_id from attendance_details where to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD') between ? and ?) and is_form16=true ");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}			
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			
			if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            
            if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" eod.service_id like '%"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
			
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }            
			sbQuery.append(" order by epd.emp_fname"); 
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("=======0========"+new Date());
//			System.out.println("pst=0=="+pst);
			rs = pst.executeQuery();
			
			Map<String, String> hmEmployeeMap = new HashMap<String, String>();
			Map<String, String> hmEmpJoiningDate = new HashMap<String, String>();
			Map<String, String> hmEmpCode = new HashMap<String, String>();
			Map<String, String> hmEmpDepartment = new HashMap<String, String>();
			Map<String, String> hmEmpPanNo = new HashMap<String, String>();
			Map<String, String> hmEmpEndDate = new HashMap<String, String>(); 
			
			StringBuilder sbEmp = null;
			while (rs.next()) {
				if (rs.getInt("emp_id") < 0) {
					continue;
				} 
				
				String strMiddleName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strMiddleName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmployeeMap.put(rs.getString("emp_id"), rs.getString("emp_fname") +strMiddleName+ " " + rs.getString("emp_lname"));
				hmEmpJoiningDate.put(rs.getString("emp_id"), uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				hmEmpCode.put(rs.getString("emp_id"), rs.getString("empcode"));
				hmEmpDepartment.put(rs.getString("emp_id"), rs.getString("depart_id"));
				hmEmpPanNo.put(rs.getString("emp_id"), uF.showData(rs.getString("emp_pan_no"), ""));
				hmEmpEndDate.put(rs.getString("emp_id"), uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT));
				if(sbEmp == null) {
					sbEmp = new StringBuilder();
					sbEmp.append(rs.getString("emp_id"));
				} else {
					sbEmp.append(","+rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
//			System.out.println("=======1========"+new Date());
			
			pst = con.prepareStatement("SELECT count(*) as cnt,emp_id FROM emp_family_members WHERE member_type='CHILD' group by emp_id");
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map<String, String> hmEmpChildCnt = new HashMap<String, String>();
			while(rs.next()){
				hmEmpChildCnt.put(rs.getString("emp_id"), rs.getString("cnt"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(*) as cnt,emp_id from (select distinct(paycycle),emp_id from payroll_generation pg " +
				"where financial_year_from_date=? and financial_year_to_date =? and is_paid = true " +
				"and salary_head_id not in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+") " +
				" and earning_deduction='E' group by paycycle,emp_id order by emp_id,paycycle) a group by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map<String, String> hmMonthPaid = new HashMap<String, String>();
			while(rs.next()){
				hmMonthPaid.put(rs.getString("emp_id"), rs.getString("cnt"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmMonthPaid==>"+hmMonthPaid);
			
			pst = con.prepareStatement("select distinct(pg.emp_id),pg.month,pg.year,pg.paycycle,pg.paid_days,pg.total_days from (select max(paycycle) as paycycle,emp_id from (select distinct(paycycle),emp_id from payroll_generation pg " +
				" where financial_year_from_date=? and financial_year_to_date =? and is_paid = true and salary_head_id not in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+") " +
				" and earning_deduction='E' group by paycycle,emp_id order by emp_id,paycycle) a group by emp_id ) a,payroll_generation pg where a.emp_id=pg.emp_id and a.paycycle=pg.paycycle and pg.emp_id in (select emp_per_id from employee_personal_details " +
				"where is_alive=false and employment_end_date between ? and ?) order by pg.emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println(" pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmLastPaycycle = new HashMap<String, String>();
			while(rs.next()){
				hmLastPaycycle.put(rs.getString("emp_id"), rs.getString("emp_id"));
				hmLastPaycycle.put(rs.getString("emp_id")+"_MONTH", rs.getString("month"));
				hmLastPaycycle.put(rs.getString("emp_id")+"_YEAR", rs.getString("year"));
				hmLastPaycycle.put(rs.getString("emp_id")+"_PAYCYCLE", rs.getString("paycycle"));
				hmLastPaycycle.put(rs.getString("emp_id")+"_PAIDDAYS", rs.getString("paid_days"));
				hmLastPaycycle.put(rs.getString("emp_id")+"_TOTALDAYS", rs.getString("total_days"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmLastPaycycle==>"+hmLastPaycycle);
			
			pst = con.prepareStatement("select * from employee_personal_details where is_alive=false and employment_end_date between ? and ? order by emp_per_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println(" pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmExEmp = new HashMap<String, String>();
			while(rs.next()){
				hmExEmp.put(rs.getString("emp_per_id"), uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
//			System.out.println("hmExEmp==>"+hmExEmp);
			
			Map hmPayrollDetails = new HashMap();			
//			List al = new ArrayList();			
//			double dblInvestmentExemption = 0.0d;
			
			Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1 ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println(" pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SERVICE_TAX", rs.getString("service_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_EDU_TAX", rs.getString("education_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_STD_TAX", rs.getString("standard_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_FLAT_TDS", rs.getString("flat_tds"));
				
				hmOtherTaxDetails.put(rs.getString("state_id")+"_MAX_TAX_INCOME", rs.getString("max_net_tax_income"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_REBATE_AMOUNT", rs.getString("rebate_amt"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SWACHHA_BHARAT_CESS", rs.getString("swachha_bharat_cess"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_KRISHI_KALYAN_CESS", rs.getString("krishi_kalyan_cess"));

				hmOtherTaxDetails.put(rs.getString("state_id")+"_CGST", rs.getString("cgst"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SGST", rs.getString("sgst"));
//				dblInvestmentExemption = 100000;
			}
			rs.close();
			pst.close();
//			System.out.println("=======2========"+new Date());
			
			Map<String, String> hmReimbursementAmt = new HashMap<String, String>();
			if(CF.getIsReceipt()){
				String[] firstArr = CF.getPayCycleFromDate(con, strFinancialYearStart, CF.getStrTimeZone(), CF, getF_org());
				String[] secondArr = null;
				if(uF.parseToInt(uF.getDateFormat(hmOrg.get("ORG_START_PAYCYCLE"), DATE_FORMAT, "dd")) > 1){
					secondArr = CF.getPrevPayCycleByOrg(con, strFinancialYearEnd, CF.getStrTimeZone(), CF, getF_org());
				} else {
					secondArr = CF.getPayCycleFromDate(con, strFinancialYearEnd, CF.getStrTimeZone(), CF, getF_org());
				}
				pst = con.prepareStatement("select emp_id,sum(reimbursement_amount) as reimbursement_amount from emp_reimbursement where approval_1 =1 " +
					" and ispaid=true and (ref_document is null or ref_document='' or upper(ref_document) ='NULL') and from_date>=? and to_date<=? " +
					" and emp_id in (select emp_id from employee_official_details where org_id=?) group by emp_id");
				pst.setDate(1, uF.getDateFormat(firstArr[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(secondArr[1], DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getF_org()));
				rs = pst.executeQuery();
//				System.out.println("pst====>"+pst);
				while(rs.next()){
					hmReimbursementAmt.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("reimbursement_amount")));
				}
				rs.close();
				pst.close();
			}
//			System.out.println("hmReimbursementAmt====>"+hmReimbursementAmt);
			
			pst = con.prepareStatement("select sum(amount) as amount,salary_head_id,emp_id,earning_deduction from payroll_generation pg where financial_year_from_date=? and financial_year_to_date =? and is_paid = true and salary_head_id not in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+") group by salary_head_id, emp_id,earning_deduction order by emp_id,earning_deduction desc");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			System.out.println(" pst==>"+pst);
			rs = pst.executeQuery();					
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			Map hmInner1 = new HashMap();
			double dblGross = 0.0d;
			Map<String,String> hmLeaveEncashmet = new HashMap<String, String>();
			while(rs.next()){
				strEmpIdNew = rs.getString("emp_id");
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					hmInner1 = new HashMap();
//					dblGross = 0.0d;
					dblGross = uF.parseToDouble(hmReimbursementAmt.get(strEmpIdNew));
				}
								
				hmInner1.put(rs.getString("salary_head_id"), rs.getString("amount"));   
				if(rs.getString("earning_deduction").equalsIgnoreCase("E")){
					dblGross += rs.getDouble("amount");
				}
				hmInner1.put("GROSS", dblGross+"");
				hmPayrollDetails.put(strEmpIdNew, hmInner1);
				
				if(uF.parseToInt(rs.getString("salary_head_id")) == LEAVE_ENCASHMENT && hmExEmp.containsKey(rs.getString("emp_id"))){
					hmLeaveEncashmet.put(rs.getString("emp_id"), rs.getString("amount"));
				}
				
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
//			System.out.println("=======3========"+new Date());
			
			pst = con.prepareStatement("select * from exemption_details where exemption_from=? and exemption_to=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map hmExemption = new HashMap();
			Map<String, Map<String, List<String>>> hmExemptionDataUnderSection = new HashMap<String, Map<String, List<String>>>();
			while(rs.next()){
//				hmExemption.put(rs.getString("exemption_name"), rs.getString("exemption_limit"));
				hmExemption.put(rs.getString("salary_head_id"), rs.getString("exemption_limit"));
				Map<String, List<String>> hmExemptionData = hmExemptionDataUnderSection.get(rs.getString("under_section"));
				if(hmExemptionData == null) hmExemptionData = new LinkedHashMap<String, List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("exemption_id"));
				innerList.add(rs.getString("exemption_code"));
				innerList.add(rs.getString("exemption_name"));
				innerList.add(rs.getString("exemption_limit"));
				innerList.add(rs.getString("exemption_from"));
				innerList.add(rs.getString("exemption_to"));
				innerList.add(rs.getString("salary_head_id"));
				innerList.add(rs.getString("under_section"));
				innerList.add(rs.getString("slab_type")); //8
				hmExemptionData.put(rs.getString("exemption_id"), innerList);
				hmExemptionDataUnderSection.put(rs.getString("under_section"), hmExemptionData);
			}
			rs.close();
			pst.close();
//			System.out.println("=======4========"+new Date());
			
			
			
			pst = con.prepareStatement("select * from section_details where under_section in (8,9) and financial_year_start=? and financial_year_end=? ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			List<List<String>> chapter1SectionList = new ArrayList<List<String>>();
			List<List<String>> chapter2SectionList = new ArrayList<List<String>>();
			Map<String, String> hmSectionPFApplicable = new HashMap<String, String>();
//			List<String> chapter1SectionList = new ArrayList<String>();
//			List<String> chapter2SectionList = new ArrayList<String>();
			while(rs.next()){
				if(uF.parseToInt(rs.getString("under_section"))==8) {
					List<String> innList = new ArrayList<String>();
					innList.add(rs.getString("section_id"));
					innList.add(rs.getString("slab_type"));
					chapter1SectionList.add(innList);
				} else {
					List<String> innList = new ArrayList<String>();
					innList.add(rs.getString("section_id"));
					innList.add(rs.getString("slab_type"));
					chapter2SectionList.add(innList);
				}
				hmSectionPFApplicable.put(rs.getString("section_id")+"_"+rs.getString("slab_type"), rs.getString("is_pf_applicable"));
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map hmHRAExemption = new HashMap();
			while(rs.next()){
				hmHRAExemption.put("CONDITION_1", rs.getString("condition1"));
				hmHRAExemption.put("CONDITION_2", rs.getString("condition2"));
				hmHRAExemption.put("CONDITION_3", rs.getString("condition3"));
				hmHRAExemption.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
			}
			rs.close();
			pst.close();
//			System.out.println("=======5========"+new Date());
			
			
			Map hmSectionLimitA = new HashMap();
			Map hmSectionLimitP = new HashMap();			
			Map hmSectionLimitEmp = new HashMap();
			Map<String, String> hmSectionAdjustedGrossIncomeLimitStatus = new HashMap<String, String>();
//			pst = con.prepareStatement(selectSection); 
			pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? order by section_code");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			while (rs.next()) {
				if(rs.getString("section_limit_type").equalsIgnoreCase("A")) {
					hmSectionLimitA.put(rs.getString("section_id")+"_"+rs.getString("slab_type"), rs.getString("section_exemption_limit"));
					hmSectionLimitA.put(rs.getString("section_id")+"_"+rs.getString("slab_type")+"_CEILING_AMT", rs.getBoolean("is_ceiling_applicable") ? rs.getDouble("ceiling_amount") : "0");
				} else {
					hmSectionLimitP.put(rs.getString("section_id")+"_"+rs.getString("slab_type"), rs.getString("section_exemption_limit"));
					hmSectionLimitP.put(rs.getString("section_id")+"_"+rs.getString("slab_type")+"_CEILING_AMT", rs.getBoolean("is_ceiling_applicable") ? rs.getDouble("ceiling_amount") : "0");
				}
				hmSectionAdjustedGrossIncomeLimitStatus.put(rs.getString("section_id")+"_"+rs.getString("slab_type"), rs.getString("is_adjusted_gross_income_limit"));
			}
			rs.close();
			pst.close();
//			System.out.println("=======6========"+new Date());
			
			
//			dblInvestmentExemption = uF.parseToDouble(""+hmSectionLimitA.get("3"));
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details id, section_details sd where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to=? and status = true and section_code in ('HRA') and trail_status = 1 group by emp_id ");
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details id, exemption_details ed where ed.salary_head_id=id.salary_head_id and id.fy_from=? " +
				" and id.fy_to=? and status=true and ed.salary_head_id=? and trail_status=1 and ed.exemption_from=? and ed.exemption_to=? group by emp_id ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, HRA);
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map hmRentPaid = new HashMap();
			while(rs.next()){
				hmRentPaid.put(rs.getString("emp_id"), rs.getString("amount_paid"));
			}
			rs.close();
			pst.close();
//			System.out.println("=======7========"+new Date());
			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, sd.slab_type, emp_id from investment_details id, section_details sd where sd.section_id=id.section_id " +
				"and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? and section_code not in ('HRA') " +
				"and isdisplay=true and parent_section=0 and under_section=8 group by emp_id, sd.section_id, sd.slab_type order by emp_id"); // and sd.section_id !=11 
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map hmInvestment = new HashMap();
			Map<String, String> hmEmpExemptionsCH1Map = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmpInvestment = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmpActualInvestment = new HashMap<String, Map<String, String>>();
			double dblInvestmentLimit = 0;
			double dblInvestmentCeilingLimit = 0;
			double dblInvestmentEmp = 0;
			
			while(rs.next()) {
				String strSectionId = rs.getString("section_id");
				String slabType = rs.getString("slab_type");
				double dblInvestment = rs.getDouble("amount_paid");
				if(uF.parseToInt(hmEmpSlabType.get(rs.getString("emp_id")))!= uF.parseToInt(slabType)) {
					continue;
				}
				if(hmSectionLimitA.containsKey(strSectionId+"_"+slabType)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType));
					dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType+"_CEILING_AMT"));
					if(dblInvestmentCeilingLimit>0) {
						dblInvestmentLimit = dblInvestmentCeilingLimit;
					}
				} else{
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType));
					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
					dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType+"_CEILING_AMT"));
					if(dblInvestmentCeilingLimit>0) {
						dblInvestmentLimit = dblInvestmentCeilingLimit;
					}
				}
				Map<String,String> hmInvest=hmEmpInvestment.get(rs.getString("emp_id"));
				if(hmInvest==null) hmInvest=new HashMap<String, String>();
				
				if(dblInvestment>=dblInvestmentLimit && dblInvestmentLimit>=0) {
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH1Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
					hmEmpExemptionsCH1Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
					hmInvest.put(strSectionId, ""+dblInvestmentLimit);
				} else{
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH1Map.get(rs.getString("emp_id"))) + dblInvestment;
					hmEmpExemptionsCH1Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
					hmInvest.put(strSectionId, ""+dblInvestment);
				}
				hmEmpInvestment.put(rs.getString("emp_id"), hmInvest);
				
				Map<String,String> hmActualInvest=hmEmpActualInvestment.get(rs.getString("emp_id"));
				if(hmActualInvest==null) hmActualInvest=new HashMap<String, String>();
				hmActualInvest.put(strSectionId, ""+dblInvestment);
				
				hmEmpActualInvestment.put(rs.getString("emp_id"), hmActualInvest);
				
				if(rs.getString("emp_id").equals("618")) {
//					System.out.println("dblInvestment==="+dblInvestment);
//					System.out.println("dblInvestmentLimit==="+dblInvestmentLimit);
//					System.out.println("dblInvestmentEmp==="+dblInvestmentEmp);					
				}
			}
			rs.close();
			pst.close();
			
//			******************************* Code added on 02May2020 *******************
			Map<String, Map<String, Map<String, Map<String, String>>>> hmEmpIncludeSubSectionData = new HashMap<String, Map<String, Map<String, Map<String, String>>>>();
			pst = con.prepareStatement("select id.*, sd.section_id, sd.is_adjusted_gross_income_limit, sd.include_sub_section,sd.slab_type from investment_details id, section_details sd where sd.section_id=id.section_id and " +
				"id.fy_from=? and id.fy_to=? and status=true and trail_status=1 and sd.financial_year_start=? and sd.financial_year_end=? and section_code not in ('HRA')  and isdisplay=true and parent_section>0 " +
				"and sub_section_no>0 and (is_adjusted_gross_income_limit is null or is_adjusted_gross_income_limit=false) and (include_sub_section is not null or include_sub_section !='') " + //and emp_id in ("+strEmpIds+") 
				"order by emp_id,sd.section_id,sd.slab_type"); //and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			while(rs.next()) {
				String strSectionId = rs.getString("section_id");
				String slabType = rs.getString("slab_type");
				if(uF.parseToInt(hmEmpSlabType.get(rs.getString("emp_id")))!= uF.parseToInt(slabType) && uF.parseToInt(slabType)!=2) {
					continue;
				}
				Map<String, Map<String, Map<String, String>>> hmIncludeSubSectionData = hmEmpIncludeSubSectionData.get(rs.getString("emp_id"));
				if(hmIncludeSubSectionData==null) hmIncludeSubSectionData = new HashMap<String, Map<String, Map<String, String>>>();
				
				Map<String, Map<String, String>> hmOuter = hmIncludeSubSectionData.get(strSectionId);
				if(hmOuter==null) hmOuter = new HashMap<String, Map<String, String>>();
				
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("SUB_SEC_NO", rs.getString("sub_section_no"));
				hmInner.put("SUB_SEC_AMT_PAID", rs.getString("amount_paid"));
				hmInner.put("SUB_SEC_LIMIT_TYPE", rs.getString("sub_section_limit_type"));
				hmInner.put("SUB_SEC_AMT", rs.getString("sub_section_amt"));
				hmOuter.put(rs.getString("sub_section_no"), hmInner);
				
				hmIncludeSubSectionData.put(strSectionId, hmOuter);
				
				hmEmpIncludeSubSectionData.put(rs.getString("emp_id"), hmIncludeSubSectionData);
			}
			rs.close();
			pst.close();
			
			
			Map<String, Map<String, String>> hmEmpSubSecMinusAmt = new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select id.*, sd.section_id, sd.is_adjusted_gross_income_limit, sd.include_sub_section,sd.slab_type from investment_details id, section_details sd where sd.section_id=id.section_id and " +
				"id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? and " +
				"section_code not in ('HRA')  and isdisplay=true and parent_section>0 and under_section=8 and sub_section_no>0 and " + //and emp_id in ("+strEmpIds+")
				"(is_adjusted_gross_income_limit is null or is_adjusted_gross_income_limit = false) order by emp_id,sd.section_id"); //and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			dblInvestmentLimit = 0;
			dblInvestmentEmp = 0;
			String oldSectionId = null;
			String newEmpId = null;
			String oldEmpId = null;
			while(rs.next()) {
				String strSectionId = rs.getString("section_id");
				String slabType = rs.getString("slab_type");
				if(uF.parseToInt(hmEmpSlabType.get(rs.getString("emp_id")))!= uF.parseToInt(slabType) && uF.parseToInt(slabType)!=2) {
					continue;
				}
				String strSubSecNo = rs.getString("sub_section_no");
				double dblInvestment = rs.getDouble("amount_paid");
				String strSubSecLimitType = rs.getString("sub_section_limit_type");
				double dblSubSecLimit = rs.getDouble("sub_section_amt");
				
				if(rs.getString("include_sub_section") != null && rs.getString("include_sub_section").length()>2) {
					List<String> al = Arrays.asList(rs.getString("include_sub_section").substring(1, rs.getString("include_sub_section").length()-1).split(","));
					Map<String, Map<String, Map<String, String>>> hmIncludeSubSectionData = hmEmpIncludeSubSectionData.get(rs.getString("emp_id"));
					if(hmIncludeSubSectionData==null) hmIncludeSubSectionData = new HashMap<String, Map<String, Map<String, String>>>();
					
					Map<String, Map<String, String>> hmOuter = hmIncludeSubSectionData.get(strSectionId);
					if(hmOuter==null) hmOuter = new HashMap<String, Map<String, String>>();
					
					double dblTotSubSecInvestment = 0;
					Iterator<String> itSubSec = hmOuter.keySet().iterator();
					while (itSubSec.hasNext()) {
						String strSubSecno = itSubSec.next();
						if(al.contains(strSubSecno)) {
							continue;
						}
						Map<String, String> hmInner = hmOuter.get(strSubSecno);
						dblTotSubSecInvestment += uF.parseToDouble(hmInner.get("SUB_SEC_AMT_PAID"));
					}
					if(al.contains(strSubSecNo) && dblTotSubSecInvestment>0) {
						continue;
					}
					
					newEmpId = rs.getString("emp_id");
					if(oldEmpId == null || uF.parseToInt(oldEmpId) != uF.parseToInt(newEmpId)) {
						oldSectionId = null;
					}
//					---------------
					Map<String,String> hmSubSecMinusAmt = hmEmpSubSecMinusAmt.get(rs.getString("emp_id"));
					if(hmSubSecMinusAmt==null) hmSubSecMinusAmt = new HashMap<String, String>();
					
					Map<String,String> hmInvest = hmEmpInvestment.get(rs.getString("emp_id"));
					if(hmInvest==null) hmInvest = new HashMap<String, String>();
					double dblSecInvestment =0;
					double dblSubSecMinusInvestment =0;
					if((oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) && hmInvest.keySet().contains(strSectionId)) {
						dblSecInvestment = 0;
						dblSubSecMinusInvestment = 0;
					} else {
						dblSecInvestment = uF.parseToDouble(hmInvest.get(strSectionId));
						dblSubSecMinusInvestment = uF.parseToDouble(hmSubSecMinusAmt.get(strSectionId));
					}
//					--------------------
					
					double dblSubSecMinActAmt=0;
					for(int i=0; i<al.size(); i++) {
						String strSubSNo = al.get(i);
						if(uF.parseToInt(strSubSNo)>0) {
							Map<String, String> hmInner = hmOuter.get(strSubSNo);
							double dblAppliedAmt = uF.parseToDouble(hmInner.get("SUB_SEC_AMT_PAID"));
							double dblSubSecLimitAmt = uF.parseToDouble(hmInner.get("SUB_SEC_AMT"));
							strSubSecLimitType = hmInner.get("SUB_SEC_LIMIT_TYPE");
							if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
								dblSubSecLimitAmt = (dblAppliedAmt * dblSubSecLimitAmt) / 100;
							}
							dblSubSecMinActAmt += Math.min(dblAppliedAmt, dblSubSecLimitAmt);
						}
					}
					
					if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
						dblSubSecLimit = (dblInvestment * dblSubSecLimit) / 100;
					}
					if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
						dblSecInvestment += dblSubSecLimit;
					} else {
						if(dblInvestment>0 && dblSubSecMinActAmt>0 && dblSubSecMinActAmt>dblSubSecMinusInvestment) {
							double dblMinAmt = dblSubSecMinActAmt - dblSubSecMinusInvestment;
							double dblAmt = dblSubSecLimit - dblInvestment;
							if(dblMinAmt>dblAmt) {
								dblSubSecMinusInvestment += dblAmt;
								dblSecInvestment += Math.min((dblInvestment + dblAmt), dblSubSecLimit);
							} else {
								dblSubSecMinusInvestment += dblMinAmt;
								dblSecInvestment += Math.min((dblInvestment + dblMinAmt), dblSubSecLimit);
							}
						} else {
							dblSecInvestment += dblInvestment;
						}
					}
//					System.out.println(strSectionId+" -- dblSecInvestment =====>> "  +dblSecInvestment);
					
					hmInvest.put(strSectionId, ""+dblSecInvestment);
					hmEmpInvestment.put(rs.getString("emp_id"), hmInvest);
					
					hmSubSecMinusAmt.put(strSectionId, ""+dblSubSecMinusInvestment);
					hmEmpSubSecMinusAmt.put(rs.getString("emp_id"), hmSubSecMinusAmt);
					
					if(oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) {
						oldSectionId = strSectionId;
					}
					
				} else {
				
					newEmpId = rs.getString("emp_id");
					if(oldEmpId == null || uF.parseToInt(oldEmpId) != uF.parseToInt(newEmpId)) {
						oldSectionId = null;
					}
					
					Map<String,String> hmInvest = hmEmpInvestment.get(rs.getString("emp_id"));
					if(hmInvest==null) hmInvest = new HashMap<String, String>();
					double dblSecInvestment =0;
					if((oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) && hmInvest.keySet().contains(strSectionId)) {
						dblSecInvestment = 0;
					} else {
						dblSecInvestment = uF.parseToDouble(hmInvest.get(strSectionId));
					}
					if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
						dblSubSecLimit = (dblInvestment * dblSubSecLimit) / 100;
					}
					if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
						dblSecInvestment += dblSubSecLimit;
					} else {
						dblSecInvestment += dblInvestment;
					}
					if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
						dblSecInvestment += dblSubSecLimit;
					} else {
						dblSecInvestment += dblInvestment;
					}
					hmInvest.put(strSectionId, ""+dblSecInvestment);
					hmEmpInvestment.put(rs.getString("emp_id"), hmInvest);
					
					if(oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) {
						oldSectionId = strSectionId;
					}
				}
				
				if(oldEmpId == null || uF.parseToInt(oldEmpId) != uF.parseToInt(newEmpId)) {
					oldEmpId = newEmpId;
				}
			}
			rs.close();
			pst.close();
//			***************************************** code add end **************************
			
			
//			System.out.println("=======8========"+new Date());
//			System.out.println("hmEmpInvestment==="+hmEmpInvestment.get("654"));    
			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, sd.slab_type, emp_id from investment_details id, section_details sd where sd.section_id=id.section_id " +
				" and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? and section_code not in ('HRA') " +
				"and isdisplay=true and parent_section=0 and under_section=9 group by emp_id, sd.section_id, sd.slab_type order by emp_id "); // and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map<String, Map<String, String>> hmEmpInvestment1 = new HashMap<String, Map<String, String>>();
			Map<String, String> hmEmpExemptionsCH2Map = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmpActualInvestment1 = new HashMap<String, Map<String, String>>();
			
			dblInvestmentLimit = 0;
			dblInvestmentCeilingLimit = 0;
			dblInvestmentEmp = 0;
			
			while(rs.next()){

				String strSectionId = rs.getString("section_id");
				String slabType = rs.getString("slab_type");
				if(uF.parseToInt(hmEmpSlabType.get(rs.getString("emp_id")))!= uF.parseToInt(slabType) && uF.parseToInt(slabType)!=2) {
					continue;
				}
				double dblInvestment = rs.getDouble("amount_paid");
				
				if(hmSectionLimitA.containsKey(strSectionId+"_"+slabType)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType));
					dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType+"_CEILING_AMT"));
					if(dblInvestmentCeilingLimit>0) {
						dblInvestmentLimit = dblInvestmentCeilingLimit;
					}
				} else{
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType));
					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
					dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType+"_CEILING_AMT"));
					if(dblInvestmentCeilingLimit>0) {
						dblInvestmentLimit = dblInvestmentCeilingLimit;
					}
				}
				Map<String,String> hmInvest = hmEmpInvestment1.get(rs.getString("emp_id"));
				if(hmInvest==null) hmInvest = new HashMap<String, String>();
				
				if(dblInvestment>=dblInvestmentLimit && dblInvestmentLimit>=0) {
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
					hmEmpExemptionsCH2Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
					hmInvest.put(strSectionId, ""+dblInvestmentLimit);
				} else{
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestment;
					hmEmpExemptionsCH2Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
					hmInvest.put(strSectionId, ""+dblInvestment);
				}
				hmEmpInvestment1.put(rs.getString("emp_id"), hmInvest);
				
				Map<String,String> hmActualInvest = hmEmpActualInvestment1.get(rs.getString("emp_id"));
				if(hmActualInvest==null) hmActualInvest = new HashMap<String, String>();
				hmActualInvest.put(strSectionId, ""+dblInvestment);
				
				hmEmpActualInvestment1.put(rs.getString("emp_id"), hmActualInvest);
				
			}
			rs.close();
			pst.close();
			
//			*************************** code added 02May2020 *******************************
			
			pst = con.prepareStatement("select id.*, sd.section_id, sd.slab_type, sd.is_adjusted_gross_income_limit, sd.include_sub_section from investment_details id, section_details sd where sd.section_id=id.section_id and " +
					"id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? and " +
					"section_code not in ('HRA')  and isdisplay=true and parent_section>0 and under_section=9 and sub_section_no>0 and " + //and emp_id in ("+strEmpIds+") 
					"(is_adjusted_gross_income_limit is null or is_adjusted_gross_income_limit = false) order by emp_id, sd.section_id"); //and sd.section_id !=11
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
				dblInvestmentLimit = 0;
				dblInvestmentEmp = 0;
				oldSectionId = null;
				while(rs.next()) {
					String strSectionId = rs.getString("section_id");
					String slabType = rs.getString("slab_type");
					if(uF.parseToInt(hmEmpSlabType.get(rs.getString("emp_id")))!= uF.parseToInt(slabType) && uF.parseToInt(slabType)!=2) {
						continue;
					}
					String strSubSecNo = rs.getString("sub_section_no");
					double dblInvestment = rs.getDouble("amount_paid");
					System.out.println("emp_id ============>> " + rs.getString("emp_id") + " -- dblInvestment ===>> " + dblInvestment+" -- strSectionId ===>> " + strSectionId +" -- strSubSecNo ===>> " + strSubSecNo);
					String strSubSecLimitType = rs.getString("sub_section_limit_type");
					double dblSubSecLimit = rs.getDouble("sub_section_amt");
					
					if(rs.getString("include_sub_section") != null && rs.getString("include_sub_section").length()>2) {
						List<String> al = Arrays.asList(rs.getString("include_sub_section").substring(1, rs.getString("include_sub_section").length()-1).split(","));
						Map<String, Map<String, Map<String, String>>> hmIncludeSubSectionData = hmEmpIncludeSubSectionData.get(rs.getString("emp_id"));
						if(hmIncludeSubSectionData==null) hmIncludeSubSectionData = new HashMap<String, Map<String, Map<String, String>>>();
						
						Map<String, Map<String, String>> hmOuter = hmIncludeSubSectionData.get(strSectionId);
						if(hmOuter==null) hmOuter = new HashMap<String, Map<String, String>>();
						
						double dblTotSubSecInvestment = 0;
						Iterator<String> itSubSec = hmOuter.keySet().iterator();
						while (itSubSec.hasNext()) {
							String strSubSecno = itSubSec.next();
							if(al.contains(strSubSecno)) {
								continue;
							}
							Map<String, String> hmInner = hmOuter.get(strSubSecno);
							dblTotSubSecInvestment += uF.parseToDouble(hmInner.get("SUB_SEC_AMT_PAID"));
						}
						if(al.contains(strSubSecNo) && dblTotSubSecInvestment>0) {
							continue;
						}
						
						newEmpId = rs.getString("emp_id");
						if(oldEmpId == null || uF.parseToInt(oldEmpId) != uF.parseToInt(newEmpId)) {
							oldSectionId = null;
						}
//						---------------
						System.out.println("emp_id ============>> " + rs.getString("emp_id"));
						Map<String,String> hmSubSecMinusAmt = hmEmpSubSecMinusAmt.get(rs.getString("emp_id"));
						if(hmSubSecMinusAmt==null) hmSubSecMinusAmt = new HashMap<String, String>();
						
						Map<String,String> hmInvest = hmEmpInvestment1.get(rs.getString("emp_id"));
						if(hmInvest==null) hmInvest = new HashMap<String, String>();
						System.out.println("emp_id ============>> " + rs.getString("emp_id") + " -- hmInvest =====>> " + hmInvest);
						System.out.println("emp_id ============>> " + rs.getString("emp_id") + " -- oldSectionId =====>> " + oldSectionId+" -- strSectionId =====>> " + strSectionId);
						double dblSecInvestment=0;
						double dblSubSecMinusInvestment=0;
						if((oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) && hmInvest.keySet().contains(strSectionId)) {
							dblSecInvestment = 0;
							dblSubSecMinusInvestment=0;
						} else {
							dblSecInvestment = uF.parseToDouble(hmInvest.get(strSectionId));
							dblSubSecMinusInvestment = uF.parseToDouble(hmSubSecMinusAmt.get(strSectionId));
						}
//						--------------------
						
						double dblSubSecMinActAmt=0;
						for(int i=0; i<al.size(); i++) {
							String strSubSNo = al.get(i);
							if(uF.parseToInt(strSubSNo)>0) {
								Map<String, String> hmInner = hmOuter.get(strSubSNo);
								double dblAppliedAmt = uF.parseToDouble(hmInner.get("SUB_SEC_AMT_PAID"));
								double dblSubSecLimitAmt = uF.parseToDouble(hmInner.get("SUB_SEC_AMT"));
								strSubSecLimitType = hmInner.get("SUB_SEC_LIMIT_TYPE");
								if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
									dblSubSecLimitAmt = (dblAppliedAmt * dblSubSecLimitAmt) / 100;
								}
								dblSubSecMinActAmt += Math.min(dblAppliedAmt, dblSubSecLimitAmt);
							}
						}
						
						if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
							dblSubSecLimit = (dblInvestment * dblSubSecLimit) / 100;
						}
						System.out.println("dblSubSecLimit ===>> " + dblSubSecLimit +" -- dblInvestment ===>> " + dblInvestment);
						
						if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
							dblSecInvestment += dblSubSecLimit;
						} else {
							System.out.println("dblSubSecMinActAmt ===>> " + dblSubSecMinActAmt +" -- dblSubSecMinusInvestment ===>> " + dblSubSecMinusInvestment);
							if(dblInvestment>0 && dblSubSecMinActAmt>0 && dblSubSecMinActAmt>dblSubSecMinusInvestment) {
								System.out.println("dblSubSecLimit =====================>>" + dblSubSecLimit);
								double dblMinAmt = dblSubSecMinActAmt - dblSubSecMinusInvestment;
								double dblAmt = dblSubSecLimit - dblInvestment;
								if(dblMinAmt>dblAmt) {
									dblSubSecMinusInvestment += dblAmt;
									dblSecInvestment += Math.min((dblInvestment + dblAmt), dblSubSecLimit);
								} else {
									dblSubSecMinusInvestment += dblMinAmt;
									dblSecInvestment += Math.min((dblInvestment + dblMinAmt), dblSubSecLimit);
								}
							} else {
								dblSecInvestment += dblInvestment;
							}
						}
						System.out.println(strSectionId+" -- dblSecInvestment =====>> "  +dblSecInvestment);
						
						if(hmSectionLimitA.containsKey(strSectionId+"_"+slabType)) {
							dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType));
							dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType+"_CEILING_AMT"));
							if(dblInvestmentCeilingLimit>0) {
								dblInvestmentLimit = dblInvestmentCeilingLimit;
							}
						} else {
							dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType));
							dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
							dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType+"_CEILING_AMT"));
							if(dblInvestmentCeilingLimit>0) {
								dblInvestmentLimit = dblInvestmentCeilingLimit;
							}
						}
						
						if(dblSecInvestment>=dblInvestmentLimit && dblInvestmentLimit>=0) {
							hmInvest.put(strSectionId+"_"+slabType, ""+dblInvestmentLimit);
						} else {
							hmInvest.put(strSectionId+"_"+slabType, ""+dblSecInvestment);
						}
						hmEmpInvestment1.put(rs.getString("emp_id"), hmInvest);
						
						hmSubSecMinusAmt.put(strSectionId, ""+dblSubSecMinusInvestment);
						hmEmpSubSecMinusAmt.put(rs.getString("emp_id"), hmSubSecMinusAmt);
						
						if(oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) {
							oldSectionId = strSectionId;
						}
						
					} else {
					
						newEmpId = rs.getString("emp_id");
						if(oldEmpId == null || uF.parseToInt(oldEmpId) != uF.parseToInt(newEmpId)) {
							oldSectionId = null;
						}
						Map<String,String> hmInvest = hmEmpInvestment1.get(rs.getString("emp_id"));
						if(hmInvest==null) hmInvest = new HashMap<String, String>();
						double dblSecInvestment =0;
						if((oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) && hmInvest.keySet().contains(strSectionId)) {
							dblSecInvestment = 0;
						} else {
							dblSecInvestment = uF.parseToDouble(hmInvest.get(strSectionId));
						}
						if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
							dblSubSecLimit = (dblInvestment * dblSubSecLimit) / 100;
						}
						if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
							dblSecInvestment += dblSubSecLimit;
						} else {
							dblSecInvestment += dblInvestment;
						}
						if(hmSectionLimitA.containsKey(strSectionId+"_"+slabType)) {
							dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType));
							dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_"+slabType+"_CEILING_AMT"));
							if(dblInvestmentCeilingLimit>0) {
								dblInvestmentLimit = dblInvestmentCeilingLimit;
							}
						} else {
							dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType));
							dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
							dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType+"_CEILING_AMT"));
							if(dblInvestmentCeilingLimit>0) {
								dblInvestmentLimit = dblInvestmentCeilingLimit;
							}
						}
						if(dblSecInvestment>=dblInvestmentLimit && dblInvestmentLimit>=0) {
							hmInvest.put(strSectionId, ""+dblInvestmentLimit);
						} else{
							hmInvest.put(strSectionId, ""+dblSecInvestment);
						}
						hmEmpInvestment1.put(rs.getString("emp_id"), hmInvest);
						
						if(oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) {
							oldSectionId = strSectionId;
						}
					}
					
					if(oldEmpId == null || uF.parseToInt(oldEmpId) != uF.parseToInt(newEmpId)) {
						oldEmpId = newEmpId;
					}
				}
				rs.close();
				pst.close();
//				******************************** code added end ************************************
				
			
			
//			System.out.println("hmEmpInvestment1==="+hmEmpInvestment1.get("654"));
//			System.out.println("=======9========"+new Date());
			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
					"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.section_id=3 " +
					"and isdisplay=true and parent_section=0 and sd.financial_year_start=? and sd.financial_year_end=? group by emp_id, sd.section_id order by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map<String, String> hmEmp80C = new HashMap<String, String>();
			dblInvestmentLimit = 0;
			dblInvestmentEmp = 0;
			
			while(rs.next()){
				String strSectionId = rs.getString("section_id"); 
				double dblInvestment = rs.getDouble("amount_paid");
				
				if(hmSectionLimitA.containsKey(strSectionId)){
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
				}else{
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
				}
				
				
				if(dblInvestment>=dblInvestmentLimit){
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
					hmEmp80C.put(rs.getString("emp_id"), dblInvestmentLimit+"");
				}else{
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestment;
					hmEmp80C.put(rs.getString("emp_id"), dblInvestment+"");
				}
				
			}
			rs.close();
			pst.close();			
//			System.out.println("hmEmpInvestment1==="+hmEmpInvestment1.get("654"));
//			System.out.println("=======10========"+new Date());
			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
					"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to =? and status = true and trail_status = 1 and sd.section_id=1 " +
					"and isdisplay=true and parent_section=0 and sd.financial_year_start=? and sd.financial_year_end=? group by emp_id, sd.section_id order by emp_id ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map<String, String> hmEmp80D = new HashMap<String, String>();
			dblInvestmentLimit = 0;
			dblInvestmentEmp = 0;
			
			while(rs.next()){
				String strSectionId = rs.getString("section_id");
				double dblInvestment = rs.getDouble("amount_paid");
				
				if(hmSectionLimitA.containsKey(strSectionId)){
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
				}else{
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
				}
				
				
				if(dblInvestment>=dblInvestmentLimit){
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
					hmEmp80D.put(rs.getString("emp_id"), dblInvestmentLimit+"");
				}else{
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestment;
					hmEmp80D.put(rs.getString("emp_id"), dblInvestment+"");
				}
				
			}
			rs.close();
			pst.close();
//			System.out.println("=======11========"+new Date());
			
			pst = con.prepareStatement("select * from investment_details id, section_details sd where sd.section_id=id.section_id and id.fy_from=? and id.fy_to=? " +
				"and status=true and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? and section_code not in ('HRA') and isdisplay=true " +
				"and parent_section>0 order by emp_id"); //and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map<String, Map<String, List<Map<String, String>>>> hmEmpSubInvestment = new HashMap<String, Map<String, List<Map<String, String>>>>();
//			Map<String, List<Map<String, String>>> hmSubInvestment = new HashMap<String, List<Map<String, String>>>();	
			dblInvestmentLimit = 0;
			dblInvestmentEmp = 0;
			
			while(rs.next()) {
				String strSectionId = rs.getString("parent_section");
				String slabType = rs.getString("slab_type");
				if(uF.parseToInt(hmEmpSlabType.get(rs.getString("emp_id")))!= uF.parseToInt(slabType) && uF.parseToInt(slabType)!=2) {
					continue;
				}
				double dblInvestment = rs.getDouble("amount_paid");
				
				Map<String, List<Map<String, String>>> hmSubInvestment =hmEmpSubInvestment.get(rs.getString("emp_id"));
				if(hmSubInvestment ==null)hmSubInvestment = new HashMap<String, List<Map<String,String>>>();
				
				List<Map<String, String>> alSubInvestment =hmSubInvestment.get(rs.getString("parent_section"));
				if(alSubInvestment ==null)alSubInvestment = new ArrayList<Map<String, String>>();
				
				Map<String, String> hm = new HashMap<String, String>();
				hm.put("SECTION_ID", rs.getString("parent_section"));
				hm.put("SECTION_NAME", rs.getString("child_section"));
				hm.put("INVESTMENT_ID", rs.getString("investment_id"));
				hm.put("PAID_AMOUNT", ""+dblInvestment);
				hm.put("SUB_SEC_NO", rs.getString("sub_section_no"));
				hm.put("SUB_SECTION_AMOUNT", ""+rs.getDouble("sub_section_amt"));
				hm.put("SUB_SECTION_LIMIT_TYPE", rs.getString("sub_section_limit_type"));
				hm.put("STATUS", rs.getString("status"));
				
				alSubInvestment.add(hm);
				
				hmSubInvestment.put(rs.getString("parent_section"), alSubInvestment);
				hmEmpSubInvestment.put(rs.getString("emp_id"), hmSubInvestment);
			}
			rs.close();
			pst.close();
				
//			System.out.println("hmEmpSubInvestment==="+hmEmpSubInvestment.get("683"));
			
			
			
			*//**
			 * HOME LOAN INTEREST EXEMPTION 
			 *//*
			pst = con.prepareStatement("select * from section_details where section_id = 11 and financial_year_start=? and financial_year_end=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			double dblLoanExemptionLimit = 0;
			while (rs.next()) {
				dblLoanExemptionLimit = rs.getDouble("section_exemption_limit");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details where fy_from =? and fy_to =? and status = true" +
					" and trail_status = 1 and parent_section=0 and  section_id in (select section_id from section_details where section_id = 11 and financial_year_start=? " +
					"and financial_year_end=?) group by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map<String, String> hmEmpHomeLoanMap = new HashMap<String, String>();
			while (rs.next()) {
				
				if(uF.parseToDouble(rs.getString("amount_paid"))>dblLoanExemptionLimit){
					hmEmpHomeLoanMap.put(rs.getString("emp_id"), dblLoanExemptionLimit+"");
				}else{
					hmEmpHomeLoanMap.put(rs.getString("emp_id"), rs.getString("amount_paid"));
				}
			}
			rs.close();
			pst.close();
//			System.out.println("=======12========"+new Date());
			*//**
			 * HOME LOAN INTEREST EXEMPTION 
			 *//*
			
			
			Map<String,String> hmEmpIncomeFromOtherSourcesMap = new HashMap<String,String>();
			Map<String,String> hmEmpLessIncomeFromOtherSourcesMap = new HashMap<String,String>();
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, sd.slab_type, emp_id from investment_details id, section_details sd " +
				"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and status = true and sd.section_id >=13 and sd.section_id <=17 " +
				"and parent_section = 0 and isdisplay=false and financial_year_start=? and financial_year_end=? group by emp_id, sd.section_id, sd.slab_type order by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst ========>> " + pst);
			rs = pst.executeQuery();			
			double dblInvestmentIncomeSourcesEmp = 0;		
			while (rs.next()) {
				String slabType = rs.getString("slab_type");
				if(uF.parseToInt(hmEmpSlabType.get(rs.getString("emp_id")))!= uF.parseToInt(slabType) && uF.parseToInt(slabType)!=2) {
					continue;
				}
				
				double dblInvestment = rs.getDouble("amount_paid");
				dblInvestmentIncomeSourcesEmp = uF.parseToDouble(hmEmpIncomeFromOtherSourcesMap.get(rs.getString("emp_id"))) + dblInvestment;
				hmEmpIncomeFromOtherSourcesMap.put(rs.getString("emp_id"), dblInvestmentIncomeSourcesEmp+"");
				
				if(rs.getInt("section_id") == 15 || rs.getInt("section_id") == 16) {
					dblInvestmentIncomeSourcesEmp = uF.parseToDouble(hmEmpLessIncomeFromOtherSourcesMap.get(rs.getString("emp_id"))) + dblInvestment;
					hmEmpLessIncomeFromOtherSourcesMap.put(rs.getString("emp_id"), dblInvestmentIncomeSourcesEmp+"");
				}
			}
			rs.close();
			pst.close();
			
			
//			System.out.println("=======13========"+new Date());
			
//			Here is Under Section 10 & 16 Investment Detail Data Salary Headwise 
			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, ed.salary_head_id, ed.slab_type, emp_id from investment_details id, exemption_details ed " +
				"where ed.salary_head_id=id.salary_head_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and status = true and under_section in (4,5) " +
				"and exemption_from=? and exemption_to=? and id.salary_head_id>0 and id.parent_section=0 group by emp_id, ed.salary_head_id,ed.slab_type order by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst========"+pst);
			rs = pst.executeQuery();			
			Map<String, Map<String, String>> hmUnderSection10_16Map = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmUnderSection10_16PaidMap = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				String strsalaryheadid = rs.getString("salary_head_id");
				String slabType = rs.getString("slab_type");
				if(uF.parseToInt(hmEmpSlabType.get(rs.getString("emp_id")))!= uF.parseToInt(slabType) && uF.parseToInt(slabType)!=2) {
					continue;
				}

				double dblInvestment = rs.getDouble("amount_paid");
				Map hmEmpPayrollDetailss = (Map)hmPayrollDetails.get(rs.getString("emp_id"));
				if(hmEmpPayrollDetailss == null) hmEmpPayrollDetailss = new HashMap();
				
				if(dblInvestment == 0) {
					dblInvestment = uF.parseToDouble((String)hmEmpPayrollDetailss.get(strsalaryheadid));
				}
				dblInvestmentLimit = uF.parseToDouble((String)hmExemption.get(strsalaryheadid));
				double dblSalHeadAmt = uF.parseToDouble((String)hmEmpPayrollDetailss.get(strsalaryheadid));
				if(dblInvestmentLimit >dblSalHeadAmt) {
					dblInvestmentLimit = dblSalHeadAmt;
				} else if(dblInvestmentLimit==0) {
					dblInvestmentLimit = dblInvestment;
				}
				Map<String, String> hmInner= (Map<String, String>)hmUnderSection10_16Map.get(rs.getString("emp_id"));
				if(hmInner==null) hmInner=new HashMap<String, String>();
				
				double dblAllowanceExempt = Math.min(dblInvestment, dblInvestmentLimit);
							
				hmInner.put(rs.getString("salary_head_id"), ""+dblAllowanceExempt);				
				hmUnderSection10_16Map.put(rs.getString("emp_id"), hmInner);
				
				Map<String, String> hmInner11 = (Map<String, String>)hmUnderSection10_16PaidMap.get(rs.getString("emp_id"));
				if(hmInner11 == null) hmInner11 = new HashMap<String, String>();
				
				hmInner11.put(rs.getString("salary_head_id"), ""+dblInvestment);				
				hmUnderSection10_16PaidMap.put(rs.getString("emp_id"), hmInner11);
			} 
			rs.close();
			pst.close();
//			System.out.println("hmUnderSection10_16PaidMap ===>> " + hmUnderSection10_16PaidMap);
//			System.out.println("hmUnderSection10_16Map ===>> " + hmUnderSection10_16Map);
			
			
//			System.out.println("=======13========"+new Date());
//			System.out.println("hmUnderSection10Map====="+hmUnderSection10Map);
//			System.out.println("hmUnderSection10PaidMap====="+hmUnderSection10PaidMap);
			
			
			Map hmTaxLiability = new HashMap();
			Set set = hmPayrollDetails.keySet();
			Iterator it = set.iterator();
			while(it.hasNext()){
				Map<String, String> hmUS10_16_SalHeadData = new HashMap<String, String>();
				String strEmpId = (String)it.next();
				String slabType = hmEmpSlabType.get(strEmpId);
				
				Map hmTaxInner = new HashMap();
				Map hmEmpPayrollDetails = (Map)hmPayrollDetails.get(strEmpId);
				if(hmEmpPayrollDetails == null) hmEmpPayrollDetails = new HashMap();
				
				String strLevel = CF.getEmpLevelId(con, strEmpId);
				int nEmpOrgId = uF.parseToInt(CF.getEmpOrgId(con, uF, strEmpId));
				
				
				pst = con.prepareStatement("select * from exemption_details where exemption_from = ? and exemption_to = ? and (slab_type=? or slab_type=2)");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(slabType));
//				System.out.println("pst========"+pst);
				rs = pst.executeQuery();			
				while (rs.next()) {
					String strsalaryheadid = rs.getString("salary_head_id");
//					Map hmEmpPayrollDetails = (Map)hmPayrollDetails.get(strEmpId);
//					if(hmEmpPayrollDetails == null) hmEmpPayrollDetails = new HashMap();
					
					double dblInvestment = uF.parseToDouble((String)hmEmpPayrollDetails.get(strsalaryheadid));
					if(uF.parseToInt(strsalaryheadid) == LTA) {
						dblInvestmentLimit = dblInvestment;
					} else {
						dblInvestmentLimit = uF.parseToDouble((String)hmExemption.get(strsalaryheadid));
					}
					Map<String, String> hmInner = (Map<String, String>)hmUnderSection10_16Map.get(strEmpId);
					if(hmInner==null) hmInner = new HashMap<String, String>();
					
					double dblAllowanceExempt = Math.min(dblInvestment, dblInvestmentLimit);
					
					if(!hmInner.containsKey(rs.getString("salary_head_id"))) {
						hmInner.put(rs.getString("salary_head_id"), ""+dblAllowanceExempt);				
						hmUnderSection10_16Map.put(strEmpId, hmInner);
					}
					Map<String, String> hmInner11 = (Map<String, String>)hmUnderSection10_16PaidMap.get(strEmpId);
					if(hmInner11 == null) hmInner11 = new HashMap<String, String>();
					
					if(!hmInner.containsKey(rs.getString("salary_head_id"))) {
						hmInner11.put(rs.getString("salary_head_id"), ""+dblInvestment);				
						hmUnderSection10_16PaidMap.put(strEmpId, hmInner11);
					}
				} 
				rs.close();
				pst.close();
				
				
				Map<String, String> hmEmployerPF = new HashMap<String, String>();
				boolean IsAddEmployerPFInTDSCal = CF.getFeatureManagementStatus(request, uF, F_ADD_EMPLYOER_PF_IN_TDS_CALCLATION);
				if(IsAddEmployerPFInTDSCal){
					pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? "
						+ "and org_id in (select org_id from employee_official_details where emp_id=?) and level_id in (select ld.level_id from grades_details gd, "
						+ "level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  "
						+ "and gd.grade_id in (select grade_id from employee_official_details where emp_id=? ))");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(strEmpId));
					pst.setInt(4, uF.parseToInt(strEmpId));
					rs = pst.executeQuery();
					Map<String, String> hmEPFPolicy = new HashMap<String, String>(); 
					while (rs.next()) {
						hmEPFPolicy.put("IS_ERPF_CONTRIBUTION", rs.getString("is_erpf_contribution"));
						hmEPFPolicy.put("IS_ERPS_CONTRIBUTION", rs.getString("is_erps_contribution"));
						hmEPFPolicy.put("IS_PF_ADMIN_CHARGES", rs.getString("is_pf_admin_charges"));
						hmEPFPolicy.put("IS_EDLI_ADMIN_CHARGES", rs.getString("is_edli_admin_charges"));
						hmEPFPolicy.put("IS_ERDLI_CONTRIBUTION", rs.getString("is_erdli_contribution"));
					}
					rs.close();
					pst.close();
					
					
					pst = con.prepareStatement("select pg.emp_id,sum(erpf_contribution) as erpf_contribution,sum(erps_contribution) as erps_contribution," +
						"sum(erdli_contribution) as erdli_contribution,sum(pf_admin_charges) as pf_admin_charges,sum(edli_admin_charges) as edli_admin_charges " +
						"from emp_epf_details eed,payroll_generation pg where eed.emp_id=pg.emp_id and financial_year_start=? and financial_year_end=? " +
						"and _month=month and financial_year_from_date=? and financial_year_to_date=? and pg.salary_head_id=? and pg.emp_id=? group by pg.emp_id");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(5, EMPLOYEE_EPF);
					pst.setInt(6, uF.parseToInt(strEmpId));
					rs = pst.executeQuery();
	//				System.out.println(" pst==>"+pst);
			 		while(rs.next()){
						double dblERPF = hmEPFPolicy!=null && uF.parseToBoolean(hmEPFPolicy.get("IS_ERPF_CONTRIBUTION")) ? rs.getDouble("erpf_contribution") : 0.0d;
						dblERPF += hmEPFPolicy!=null && uF.parseToBoolean(hmEPFPolicy.get("IS_ERPS_CONTRIBUTION")) ? rs.getDouble("erps_contribution") : 0.0d;
						dblERPF += hmEPFPolicy!=null && uF.parseToBoolean(hmEPFPolicy.get("IS_ERDLI_CONTRIBUTION")) ? rs.getDouble("erdli_contribution") : 0.0d;
						dblERPF += hmEPFPolicy!=null && uF.parseToBoolean(hmEPFPolicy.get("IS_PF_ADMIN_CHARGES")) ? rs.getDouble("pf_admin_charges") : 0.0d;
						dblERPF += hmEPFPolicy!=null && uF.parseToBoolean(hmEPFPolicy.get("IS_EDLI_ADMIN_CHARGES")) ? rs.getDouble("edli_admin_charges") : 0.0d;
						
						hmEmployerPF.put(rs.getString("emp_id"), uF.formatIntoTwoDecimal(Math.round(dblERPF)));
					}
					rs.close();
					pst.close();
				}
				
				double dblPerkAlignTDSAmount = CF.getPerkAlignTDSAmount(con, CF,uF, uF.parseToInt(strEmpId), strFinancialYearStart, strFinancialYearEnd, nEmpOrgId, uF.parseToInt(strLevel));
				
				double dblBasic = uF.parseToDouble((String)hmEmpPayrollDetails.get(BASIC+""));
				double dblDA = uF.parseToDouble((String)hmEmpPayrollDetails.get(DA+""));
				
				String strTDSAmt = (String)hmSalaryHeadMap.get(TDS+"");
				
				String strConveyanceAllowance = (String)hmSalaryHeadMap.get(CONVEYANCE_ALLOWANCE+"");
				String strProfessionalTax = (String)hmSalaryHeadMap.get(PROFESSIONAL_TAX+"");
				
				double dblGross1 = uF.parseToDouble((String)hmEmpPayrollDetails.get("GROSS"));
				dblGross1 = dblGross1 - dblPerkAlignTDSAmount;
				hmEmpPayrollDetails.put("GROSS",""+dblGross1);
				
				
				
				*//**
				 * PREV ORG GROSS AMOUNT AND TDS AMOUNT 
				 * *//*
				double dblPrevOrgGross = 0.0d;
				double dblPrevOrgTDSAmount = 0.0d;
				boolean isJoinDateBetween = uF.isDateBetween(uF.getDateFormatUtil(strFinancialYearStart, DATE_FORMAT), uF.getDateFormatUtil(strFinancialYearEnd, DATE_FORMAT), uF.getDateFormatUtil(hmEmpJoiningDate.get(strEmpId), DATE_FORMAT));
				Map<String, String> hmPrevOrgTDSDetails = new HashMap<String, String>();
				if(isJoinDateBetween) {
					pst = con.prepareStatement("select * from prev_earn_deduct_details where financial_start=? and financial_end=? and emp_id=?");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(strEmpId));
					rs = pst.executeQuery();
					if(rs.next()) {
						dblPrevOrgGross = rs.getDouble("gross_amount");
						dblPrevOrgTDSAmount = rs.getDouble("tds_amount");
						hmPrevOrgTDSDetails.put(strEmpId+"_GROSS_AMT", ""+rs.getDouble("gross_amount"));
						hmPrevOrgTDSDetails.put(strEmpId+"_TDS_AMT", ""+rs.getDouble("tds_amount"));
					}
					rs.close();
					pst.close();
				}
				request.setAttribute("hmPrevOrgTDSDetails", hmPrevOrgTDSDetails);
				hmTaxInner.put("hmPrevOrgTDSDetails", hmPrevOrgTDSDetails);
				
				double dblProfessionalTaxPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(PROFESSIONAL_TAX+""));
//				double dblProfessionalTaxExemptLimit = uF.parseToDouble((String)hmExemption.get(strProfessionalTax));
				double dblProfessionalTaxExemptLimit = uF.parseToDouble((String)hmExemption.get(PROFESSIONAL_TAX+""));
				double dblProfessionalTaxExempt = Math.min(dblProfessionalTaxPaid, dblProfessionalTaxExemptLimit);
				
				double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_EDU_TAX"));
				double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_STD_TAX"));
				double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_FLAT_TDS"));
				
				
				// Exemptions needs to worked out as other exemptions				
				
//				double dblHomeLoanTaxExempt = uF.parseToDouble(hmEmpHomeLoanMap.get(strEmpId));
				
//				double dblOtherExemptions = 0;
//				Set setSalaryMap = hmSalaryHeadMap.keySet();
//				Iterator itSalaryMap = setSalaryMap.iterator();
//				while(itSalaryMap.hasNext()){
//					String strSalaryHead = (String)itSalaryMap.next();
//					String strSalaryHeadName = (String)hmSalaryHeadMap.get(strSalaryHead);
//					
//					if(uF.parseToInt(strSalaryHead) == PROFESSIONAL_TAX ||  uF.parseToInt(strSalaryHead) == CONVEYANCE_ALLOWANCE ||  uF.parseToInt(strSalaryHead) == EDUCATION_ALLOWANCE ||  uF.parseToInt(strSalaryHead) == MEDICAL_ALLOWANCE){
//						continue;
//					}
//					
//					double dblOtherExemptionsPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(strSalaryHead));
//					double dblOtherExemptionsExemptLimit = uF.parseToDouble((String)hmExemption.get(strSalaryHead));
//					double dblOtherExemptionsTaxExempt = Math.min(dblOtherExemptionsPaid, dblOtherExemptionsExemptLimit);
//					dblOtherExemptions += dblOtherExemptionsTaxExempt; 
//				}
//				dblOtherExemptions = uF.parseToDouble(hmLeaveEncashmet.get(strEmpId));
				
				double dblInvestment = uF.parseToDouble((String)hmInvestment.get(strEmpId));
//				double dblInvestment = uF.parseToDouble((String)hmEmpExemptionsMap.get(strEmpId));
				double dblEPFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(EMPLOYEE_EPF+""));
				
//				double dblEPRFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(EMPLOYER_EPF+""));
				double dblEPRFPaid = uF.parseToDouble(hmEmployerPF.get(strEmpId));
				if(hmEmpPayrollDetails.containsKey(EMPLOYER_EPF+"")){
					dblEPRFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(EMPLOYER_EPF+""));
				}
				
				double dblEPVFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(VOLUNTARY_EPF+""));
				
				double dblChapterVIA1=uF.parseToDouble(hmEmpExemptionsCH1Map.get(strEmpId));				
				double dblChapterVIA2=uF.parseToDouble(hmEmpExemptionsCH2Map.get(strEmpId));
//				double dbl80C=uF.parseToDouble(hmEmp80C.get(strEmpId));
//				double dbl80D=uF.parseToDouble(hmEmp80D.get(strEmpId));
//				double dblTotalInvestment =dblChapterVIA1+dblChapterVIA2;
//				double dblTotalInvestment =dblChapterVIA1+dblChapterVIA2 + dblEPFPaid;
//				double dblTotalInvestment =dbl80C + dblEPFPaid + dblEPRFPaid + dblEPVFPaid;
				double dblEmpPF =dblEPFPaid + dblEPRFPaid + dblEPVFPaid;
//				double dblTotalInvestment =0.0d; //dblInvestment + dblEPFPaid;  
//				double dblInvestmentExempt = Math.min(dblTotalInvestment, dblInvestmentExemption);
				  
				 
				if(strEmpId.equals("618")){
//					System.out.println("dblInvestment==="+dblInvestment);
//					System.out.println("dblEPFPaid==="+dblEPFPaid);
//					System.out.println("dblEPRFPaid==="+dblEPRFPaid);
//					System.out.println("dblEPVFPaid==="+dblEPVFPaid);
//					System.out.println("dblInvestmentExempt==="+dblInvestmentExempt);
//					System.out.println("dblTotalInvestment===="+dblTotalInvestment); 
//					System.out.println("dblInvestmentExemption="+dblInvestmentExemption);
				}
				
				double dblConAllLimit = 0.0d;
				if(hmExEmp.containsKey(strEmpId)) {
					int nMonth = uF.parseToInt(hmMonthPaid.get(strEmpId));
					double dblConAmt = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) * (nMonth - 1);
					double dblTotalDaysAmt = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) / uF.parseToDouble(hmLastPaycycle.get(strEmpId+"_TOTALDAYS"));
					double dblPaidDaysAmt = dblTotalDaysAmt * uF.parseToDouble(hmLastPaycycle.get(strEmpId+"_PAIDDAYS"));
					if(uF.parseToDouble(hmLastPaycycle.get(strEmpId+"_PAIDDAYS")) > 15.0d) {
						pst = con.prepareStatement("select * from payroll_generation where emp_id=? and paycycle=? and salary_head_id=?");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setInt(2, uF.parseToInt(hmLastPaycycle.get(strEmpId+"_PAYCYCLE")));
						pst.setInt(3, CONVEYANCE_ALLOWANCE);
//							System.out.println("pst========"+pst);
						rs = pst.executeQuery();			
						double dblLastPaidConveyance = 0;		
						while (rs.next()) {
							dblLastPaidConveyance = rs.getDouble("amount");
						}
						rs.close();
						pst.close();
						
						if(dblLastPaidConveyance > (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12)) {
							dblLastPaidConveyance = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12);
						}
						
						dblPaidDaysAmt = dblLastPaidConveyance;
					}
					
					dblConAllLimit = dblConAmt + dblPaidDaysAmt;
				} else {
					dblConAllLimit = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) * uF.parseToInt(hmMonthPaid.get(strEmpId));
				}
				double dblConveyanceAllowanceLimit = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblConAllLimit));
				double dblConveyanceAllowancePaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(CONVEYANCE_ALLOWANCE+""));
				double dblConveyanceAllowanceExempt = Math.min(dblConveyanceAllowancePaid, dblConveyanceAllowanceLimit);
				hmUS10_16_SalHeadData.put(CONVEYANCE_ALLOWANCE+"_PAID", ""+dblConveyanceAllowancePaid);
				hmUS10_16_SalHeadData.put(CONVEYANCE_ALLOWANCE+"_EXEMPT", (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+"")) >0) ? ""+dblConveyanceAllowanceExempt : ""+dblConveyanceAllowancePaid);
				
				
				int nEmpChildCnt = uF.parseToInt(hmEmpChildCnt.get(strEmpId)) > 2 ? 2 : uF.parseToInt(hmEmpChildCnt.get(strEmpId));
				double dblEducationAllowanceLimit = ((uF.parseToDouble((String)hmExemption.get(EDUCATION_ALLOWANCE+""))/12) * nEmpChildCnt) * uF.parseToInt(hmMonthPaid.get(strEmpId));
				double dblEducationAllowancePaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(EDUCATION_ALLOWANCE+""));
				double dblEducationAllowanceExempt = Math.min(dblEducationAllowancePaid, dblEducationAllowanceLimit);
				hmUS10_16_SalHeadData.put(EDUCATION_ALLOWANCE+"_PAID", ""+dblEducationAllowancePaid);
				hmUS10_16_SalHeadData.put(EDUCATION_ALLOWANCE+"_EXEMPT", ""+dblEducationAllowanceExempt);
				
				String[] hraSalaryHeads = null;
				if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null) {
					hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
				}
				
				double dblHraSalHeadsAmount = 0;
				for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++) {
					dblHraSalHeadsAmount += uF.parseToDouble((String)hmEmpPayrollDetails.get(hraSalaryHeads[i]));
				}
				
				
				String strHRA = (String)hmEmpPayrollDetails.get(HRA+"");
				
				String strCondition1 = (String)hmHRAExemption.get("CONDITION_1");
				String strCondition2 = (String)hmHRAExemption.get("CONDITION_2");
				String strCondition3 = (String)hmHRAExemption.get("CONDITION_3");
				
				double dblCondition1 = uF.parseToDouble(strCondition1);
				double dblCondition2 = uF.parseToDouble(strCondition2);
				double dblCondition3 = uF.parseToDouble(strCondition3);
				
				double dblHRA1 = dblCondition1 * dblHraSalHeadsAmount / 100;
				double dblHRA2 = dblCondition2 * dblHraSalHeadsAmount / 100;
				double dblHRA3 = dblCondition3 * dblHraSalHeadsAmount / 100;
				
				double dblActualHRAPaid = uF.parseToDouble(strHRA);
				double dblActualRentPaid = uF.parseToDouble((String)hmRentPaid.get(strEmpId));
				
				double dblHRAExemption = Math.min(dblActualHRAPaid, dblActualRentPaid);
				boolean isMetro = uF.parseToBoolean((String)hmEmpMertoMap.get(strEmpId));
				double dblHRA = 0;
				if(isMetro) {
					if(dblHRA1<dblHRA2) {
						dblHRA = dblHRA1; 
						hmTaxInner.put("dblHRA", "Rent paid over "+dblCondition1+"% of salary");
					} else{
						dblHRA = dblHRA2;
						hmTaxInner.put("dblHRA", "");
						hmTaxInner.put("dblHRA", dblCondition2+"% of salary in metro cities");
					}
				} else{
					if(dblHRA1<dblHRA3) {
						dblHRA = dblHRA1;
						hmTaxInner.put("dblHRA", "");
						hmTaxInner.put("dblHRA", "Rent paid over "+dblCondition1+"% of salary");
					} else{
						dblHRA = dblHRA3;
						hmTaxInner.put("dblHRA", "");
						hmTaxInner.put("dblHRA", dblCondition3+"% of salary in other cities");
					}
				}
				if(strEmpId.equals("618")) {
//					System.out.println("dblActualHRAPaid==="+dblActualHRAPaid);
//					System.out.println("dblActualRentPaid==="+dblActualRentPaid);
//					System.out.println("dblHRAExemption==="+dblHRAExemption);
//					System.out.println("dblHRA==="+dblHRA);
				}
				
				dblHRAExemption = Math.min(dblHRAExemption, dblHRA);
				
				if(strEmpId.equals("618")) {
//					System.out.println("after dblHRAExemption==="+dblHRAExemption);
				}
				
				ApprovePayroll objApprovePayroll = new ApprovePayroll();
				objApprovePayroll.session = session;
				objApprovePayroll.request = request;
				objApprovePayroll.CF = CF;
				
				Map hmEmpRentPaidMap = objApprovePayroll.getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
//				double dblHRAExemptions = objApprovePayroll.getHRAExemptionCalculation(con, uF,null, hmEmpPayrollDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, 0.0, 0.0, hmEmpMertoMap, hmEmpRentPaidMap);
				double dblHRAExemptions = objApprovePayroll.getHRAExemptionCalculation(con, uF,null, hmEmpPayrollDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, dblActualHRAPaid, dblHraSalHeadsAmount, hmEmpMertoMap, hmEmpRentPaidMap);
				dblHRAExemption = dblHRAExemptions;
				hmUS10_16_SalHeadData.put(HRA+"_PAID", "");
				hmUS10_16_SalHeadData.put(HRA+"_EXEMPT", ""+dblHRAExemption);
				
				
				if(strEmpId.equals("618")) {
//					System.out.println("after after dblHRAExemption==="+dblHRAExemption);
//					System.out.println("dblHRAExemptions==="+dblHRAExemptions);
				}
				
				double dblIncomeFromOther = uF.parseToDouble(hmEmpIncomeFromOtherSourcesMap.get(strEmpId));
				double dblLessIncomeFromOther = uF.parseToDouble(hmEmpLessIncomeFromOtherSourcesMap.get(strEmpId));
				
				double dblUS10_16Exempt = 0.0d;
				Map<String, String> hmUS10Inner= (Map<String, String>)hmUnderSection10_16Map.get(strEmpId);
		        if(hmUS10Inner==null) hmUS10Inner=new HashMap<String, String>();
		        
		        Map<String, String> hmUS10InnerPaid= (Map<String, String>)hmUnderSection10_16PaidMap.get(strEmpId);
				if(hmUS10InnerPaid==null) hmUS10InnerPaid=new HashMap<String, String>();
				
				
				if(hmEmpPayrollDetails.containsKey(REIMBURSEMENT+"")) {
//					System.out.println("REIMBURSEMENT ===>> " + hmUS10InnerPaid.get(REIMBURSEMENT+""));
					hmUS10_16_SalHeadData.put(REIMBURSEMENT+"_PAID", hmUS10InnerPaid.get(REIMBURSEMENT+""));
					hmUS10_16_SalHeadData.put(REIMBURSEMENT+"_EXEMPT", hmUS10InnerPaid.get(REIMBURSEMENT+""));
				}
				
				Iterator<String> itUnderSection = hmExemptionDataUnderSection.keySet().iterator();
		        while (itUnderSection.hasNext()) {
		        	String underSectionId = itUnderSection.next();
			        Map<String, List<String>> hmUS10ExemptionData = hmExemptionDataUnderSection.get(underSectionId);
			        Iterator<String> itUS10Examption = hmUS10ExemptionData.keySet().iterator();
			        while (itUS10Examption.hasNext()) {
						String exemptionId = itUS10Examption.next();
						List<String> innerList = hmUS10ExemptionData.get(exemptionId);
						if(uF.parseToInt(innerList.get(8)) == uF.parseToInt(slabType) || uF.parseToInt(innerList.get(8)) == 2) {
							double dblAmtExempt = uF.parseToDouble(hmUS10Inner.get(innerList.get(6)));
							if(uF.parseToInt(innerList.get(6)) == HRA || uF.parseToInt(innerList.get(6)) == CONVEYANCE_ALLOWANCE || uF.parseToInt(innerList.get(6)) == EDUCATION_ALLOWANCE) {
								dblAmtExempt = uF.parseToDouble(hmUS10_16_SalHeadData.get(innerList.get(6)+"_EXEMPT"));
							}
							if(uF.parseToInt(innerList.get(6)) == 0) {
								dblAmtExempt = uF.parseToDouble(innerList.get(3));
							}
							dblUS10_16Exempt += dblAmtExempt;
						}
			        }
		        }
		        
		        System.out.println("dblUS10_16Exempt ===>> " + dblUS10_16Exempt);
		        double dblVIA2Exempt = 0.0d;
				double dblVIA1Exempt = 0.0d;
				double dblAddExemptInAdjustedGrossIncome = 0.0d;
				
				dblInvestmentLimit = 0;
//					double dblInvest = 0.0d;
				
//					Here we Calculate VI A1
				*//**
				 * Change by RAHUL PATIL on 31Aug18 based on Crave Infotech Case found
				 * *//*
//					System.out.println("hmEmpInvestment(strEmpId) 55 ================>> " + hmEmpInvestment.get(strEmpId));
				Map<String,String> hmInvest = hmEmpInvestment.get(strEmpId);
				if(hmInvest == null) hmInvest = new HashMap<String, String>();
				Iterator<String> it1 = hmInvest.keySet().iterator();
				List<String> alSectionId = new ArrayList<String>();
				while(it1.hasNext()) {
					String strSectionId = it1.next();
					
					if(hmSectionLimitA.containsKey(strSectionId)) {
						dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
						double dblVIA1Invest = uF.parseToDouble(hmInvest.get(strSectionId));
						if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId+"_"+slabType))) {
							alSectionId.add(strSectionId);
							dblVIA1Invest += dblEmpPF;
						}
//						System.out.println("strSectionId ===>> " + strSectionId + " -- dblInvestmentLimit ===>> " + dblInvestmentLimit);
						if(dblInvestmentLimit>=0) {
							dblVIA1Invest = Math.min(dblVIA1Invest, dblInvestmentLimit);
						}
						if(uF.parseToBoolean(hmSectionAdjustedGrossIncomeLimitStatus.get(strSectionId+"_"+slabType))) {
							dblAddExemptInAdjustedGrossIncome += dblVIA1Invest;
						}
						dblVIA1Exempt += dblVIA1Invest;
					} else {
						dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_"+slabType));
						double dblVIA1Invest = uF.parseToDouble(hmInvest.get(strSectionId));
						if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId+"_"+slabType))) {
							alSectionId.add(strSectionId);
							dblVIA1Invest += dblEmpPF;
						}
						dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
						if(dblInvestmentLimit>=0) {
							dblVIA1Invest = Math.min(dblVIA1Invest, dblInvestmentLimit);
						}
						if(uF.parseToBoolean(hmSectionAdjustedGrossIncomeLimitStatus.get(strSectionId+"_"+slabType))) {
							dblAddExemptInAdjustedGrossIncome += dblVIA1Invest;
						}
						dblVIA1Exempt += dblVIA1Invest;
					}
				
				}
//					Here we Calculate VI A2 
				*//**
				 * Change by RAHUL PATIL on 31Aug18 based on Craveinfotech Case found
				 * *//*
//					System.out.println("hmEmpInvestment1.get(strEmpId) 55 ================>> " + hmEmpInvestment1.get(strEmpId));
				Map<String,String> hmInvest2 = hmEmpInvestment1.get(strEmpId);
				if(hmInvest2 == null) hmInvest2 = new HashMap<String, String>();
				Iterator<String> it2 = hmInvest2.keySet().iterator();
				while(it2.hasNext()) {
					String strSectionId = it2.next();
					if(uF.parseToBoolean(hmSectionAdjustedGrossIncomeLimitStatus.get(strSectionId+"_"+slabType))) {
						dblAddExemptInAdjustedGrossIncome += uF.parseToDouble(hmInvest2.get(strSectionId));
					}
					dblVIA2Exempt += uF.parseToDouble(hmInvest2.get(strSectionId));
				}
				
				
				double dblAdjustedGrossTotalIncome = (dblPrevOrgGross + dblGross1 + dblAddExemptInAdjustedGrossIncome + dblIncomeFromOther) - dblUS10_16Exempt - dblVIA1Exempt - dblVIA2Exempt - dblLessIncomeFromOther; //- dblHomeLoanTaxExempt
//				System.out.println("dblAdjustedGrossTotalIncome ===>> " + dblAdjustedGrossTotalIncome);
				request.setAttribute("dblAdjustedGrossTotalIncome", ""+dblAdjustedGrossTotalIncome);
				
				pst = con.prepareStatement("select * from section_details where financial_year_start=? and financial_year_end=? " +
					" and (slab_type=? or slab_type=2) and section_code not in ('HRA') and isdisplay=true and is_adjusted_gross_income_limit=true "); //and sd.section_id !=11
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(slabType));
//				System.out.println(" pst==>"+pst);
				rs = pst.executeQuery();
				dblInvestmentLimit = 0;
				dblInvestmentEmp = 0;
				Map<String, Map<String, String>> hmSectionwiseSubSecIsAdjustedStatus = new LinkedHashMap<String, Map<String, String>>();
				while(rs.next()) {
					Map<String, String> hmSubSecIsAdjustedStatus = new HashMap<String, String>();
					hmSubSecIsAdjustedStatus.put("UNDER_SECTION", rs.getString("under_section"));
					hmSubSecIsAdjustedStatus.put("SUB_SEC_1_IS_ADJUSTED_STATUS", rs.getString("sub_section_1_is_adjust_gross_income_limit"));
					hmSubSecIsAdjustedStatus.put("SUB_SEC_2_IS_ADJUSTED_STATUS", rs.getString("sub_section_2_is_adjust_gross_income_limit"));
					hmSubSecIsAdjustedStatus.put("SUB_SEC_3_IS_ADJUSTED_STATUS", rs.getString("sub_section_3_is_adjust_gross_income_limit"));
					hmSubSecIsAdjustedStatus.put("SUB_SEC_4_IS_ADJUSTED_STATUS", rs.getString("sub_section_4_is_adjust_gross_income_limit"));
					hmSubSecIsAdjustedStatus.put("SUB_SEC_5_IS_ADJUSTED_STATUS", rs.getString("sub_section_5_is_adjust_gross_income_limit"));
					
					hmSectionwiseSubSecIsAdjustedStatus.put(rs.getString("section_id"), hmSubSecIsAdjustedStatus);
				}
				rs.close();
				pst.close();
//				System.out.println(" hmSectionwiseSubSecIsAdjustedStatus ===>> " + hmSectionwiseSubSecIsAdjustedStatus);
				
				Map<String, List<Map<String, String>>> hmSubInvestment = hmEmpSubInvestment.get(strEmpId);
				if(hmSubInvestment ==null)hmSubInvestment = new HashMap<String, List<Map<String,String>>>();
				
				Map<String,String> hmInvest11 = new HashMap<String, String>();
				
				Iterator<String> itSubSec = hmSectionwiseSubSecIsAdjustedStatus.keySet().iterator();
				Map<String, Map<String, String>> hmSectionwiseSubSecAdjusted10PerLimitAmt = new HashMap<String, Map<String,String>>();
				while (itSubSec.hasNext()) {
					String strSectionId = itSubSec.next();
					int intUnderSec = 0;
					Map<String, String> hmSubSecIsAdjustedStatus = hmSectionwiseSubSecIsAdjustedStatus.get(strSectionId);
					double dblSecInvestment =0;
					if(hmInvest11.keySet().contains(strSectionId)) {
						dblSecInvestment = 0;
					} else {
						dblSecInvestment = uF.parseToDouble(hmInvest11.get(strSectionId));
					}
					List<Map<String, String>> alSubInvestment = hmSubInvestment.get(strSectionId);
					if(alSubInvestment ==null)alSubInvestment = new ArrayList<Map<String, String>>();
					Map<String, String> hmSubSecAdjusted10PerLimitAmt = hmSectionwiseSubSecAdjusted10PerLimitAmt.get(strSectionId);
					if(hmSubSecAdjusted10PerLimitAmt==null)hmSubSecAdjusted10PerLimitAmt = new HashMap<String, String>();
					
					for(int i=0; i<alSubInvestment.size(); i++) {
						Map<String, String> hm = alSubInvestment.get(i);
						boolean blnIsAdjustedLimit = false;
						intUnderSec = uF.parseToInt(hm.get("UNDER_SECTION"));
						dblInvestment = uF.parseToDouble(hm.get("PAID_AMOUNT"));
						double dblSubSecLimit = uF.parseToDouble(hm.get("SUB_SECTION_AMOUNT"));
						if(uF.parseToInt(hm.get("SUB_SEC_NO")) ==1 && uF.parseToBoolean(hmSubSecIsAdjustedStatus.get("SUB_SEC_1_IS_ADJUSTED_STATUS"))) {
							blnIsAdjustedLimit = true;
						} else if(uF.parseToInt(hm.get("SUB_SEC_NO")) ==2 && uF.parseToBoolean(hmSubSecIsAdjustedStatus.get("SUB_SEC_2_IS_ADJUSTED_STATUS"))) {
							blnIsAdjustedLimit = true;
						} else if(uF.parseToInt(hm.get("SUB_SEC_NO")) ==3 && uF.parseToBoolean(hmSubSecIsAdjustedStatus.get("SUB_SEC_3_IS_ADJUSTED_STATUS"))) {
							blnIsAdjustedLimit = true;
						} else if(uF.parseToInt(hm.get("SUB_SEC_NO")) ==4 && uF.parseToBoolean(hmSubSecIsAdjustedStatus.get("SUB_SEC_4_IS_ADJUSTED_STATUS"))) {
							blnIsAdjustedLimit = true;
						} else if(uF.parseToInt(hm.get("SUB_SEC_NO")) ==5 && uF.parseToBoolean(hmSubSecIsAdjustedStatus.get("SUB_SEC_5_IS_ADJUSTED_STATUS"))) {
							blnIsAdjustedLimit = true;
						}
						
						if(blnIsAdjustedLimit) {
							double dbl10PerOfAdjustedIncome = (dblAdjustedGrossTotalIncome * 10) / 100;
//							System.out.println(dblSubSecLimit+ " -- dbl10PerOfAdjustedIncome ===>> " + dbl10PerOfAdjustedIncome);
							if(hm.get("SUB_SECTION_LIMIT_TYPE") != null && hm.get("SUB_SECTION_LIMIT_TYPE").equals("%")) {
								dblSubSecLimit = (dbl10PerOfAdjustedIncome * dblSubSecLimit) / 100;
							}
//							System.out.println("dblSubSecLimit ===>> " +dblSubSecLimit +" -- dblInvestment ===>> " + dblInvestment);
							if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
								dblSecInvestment += dblSubSecLimit;
							} else {
								dblSecInvestment += dblInvestment;
							}
							hmSubSecAdjusted10PerLimitAmt.put(hm.get("SUB_SEC_NO"), ""+dblSubSecLimit);
						} else {
							if(hm.get("SUB_SECTION_LIMIT_TYPE") != null && hm.get("SUB_SECTION_LIMIT_TYPE").equals("%")) {
								dblSubSecLimit = (dblInvestment * dblSubSecLimit) / 100;
							}
							if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
								dblSecInvestment += dblSubSecLimit;
							} else {
								dblSecInvestment += dblInvestment;
							}
						}
					}
					
					hmSectionwiseSubSecAdjusted10PerLimitAmt.put(strSectionId, hmSubSecAdjusted10PerLimitAmt);
					
					hmInvest11.put(strSectionId, ""+dblSecInvestment);
					if(intUnderSec == 8) {
						hmEmpInvestment.put(rs.getString("emp_id"), hmInvest11);
					} else if(intUnderSec == 9) {
						hmEmpInvestment1.put(rs.getString("emp_id"), hmInvest11);
					}
					
				}
				request.setAttribute("hmSectionwiseSubSecAdjusted10PerLimitAmt", hmSectionwiseSubSecAdjusted10PerLimitAmt);
				
				double dblVIA1ExemptIsAdjustedLimit = 0.0d;
				
				Iterator<String> it11 = hmInvest11.keySet().iterator();
				while(it11.hasNext()) {
					String strSectionId = it11.next();
					
					if(hmSectionLimitA.containsKey(strSectionId)) {
						dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
						double dblVIA1Invest = uF.parseToDouble(hmInvest11.get(strSectionId));
						if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId))) {
							alSectionId.add(strSectionId);
							dblVIA1Invest += dblEmpPF;
						}
//						System.out.println("strSectionId ===>> " + strSectionId + " -- dblInvestmentLimit ===>> " + dblInvestmentLimit);
						if(dblInvestmentLimit>=0) {
							dblVIA1Invest = Math.min(dblVIA1Invest, dblInvestmentLimit);
						}
						dblVIA1ExemptIsAdjustedLimit += dblVIA1Invest;
					} else {
						dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
						double dblVIA1Invest = uF.parseToDouble(hmInvest11.get(strSectionId));
						if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId))) {
							alSectionId.add(strSectionId);
							dblVIA1Invest += dblEmpPF;
						}
						dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
						if(dblInvestmentLimit>=0) {
							dblVIA1Invest = Math.min(dblVIA1Invest, dblInvestmentLimit);
						}
						dblVIA1ExemptIsAdjustedLimit += dblVIA1Invest;
					}
				}
				if(alSectionId == null || alSectionId.size()==0) {
					dblVIA1Exempt += dblEmpPF;
				}
				
				System.out.println("dblVIA1ExemptIsAdjustedLimit ===>> " + dblVIA1ExemptIsAdjustedLimit);
				
				
				
				
				
				double dblConAllLimit = 0.0d;
				if(hmExEmp.containsKey(strEmpId)){
					int nMonth = uF.parseToInt(hmMonthPaid.get(strEmpId));
					double dblConAmt = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) * (nMonth - 1);
					double dblTotalDaysAmt = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) / uF.parseToDouble(hmLastPaycycle.get(strEmpId+"_TOTALDAYS"));
					double dblPaidDaysAmt = dblTotalDaysAmt * uF.parseToDouble(hmLastPaycycle.get(strEmpId+"_PAIDDAYS"));
					if(uF.parseToDouble(hmLastPaycycle.get(strEmpId+"_PAIDDAYS")) > 15.0d){
						pst = con.prepareStatement("select * from payroll_generation where emp_id=? " +
								"and paycycle=? and salary_head_id=?");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setInt(2, uF.parseToInt(hmLastPaycycle.get(strEmpId+"_PAYCYCLE")));
						pst.setInt(3, CONVEYANCE_ALLOWANCE);
//						System.out.println("pst========"+pst);
						rs = pst.executeQuery();			
						double dblLastPaidConveyance = 0;		
						while (rs.next()) {
							dblLastPaidConveyance = rs.getDouble("amount");
						}
						rs.close();
						pst.close();
						
						if(dblLastPaidConveyance > (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12)){
							dblLastPaidConveyance = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12);
						}
						
						dblPaidDaysAmt = dblLastPaidConveyance;
					}
//					System.out.println("dblPaidDaysAmt==>"+dblPaidDaysAmt);
					dblConAllLimit = dblConAmt + dblPaidDaysAmt;
//					System.out.println("dblConAllLimit==>"+dblConAllLimit);
				} else {
					dblConAllLimit = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) * uF.parseToInt(hmMonthPaid.get(strEmpId));
				}
				double dblConveyanceAllowanceLimit = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblConAllLimit));
//				double dblConveyanceAllowanceLimit = strEmpId.equals("924") ? 15071 : (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) * uF.parseToInt(hmMonthPaid.get(strEmpId));
				double dblConveyanceAllowancePaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(CONVEYANCE_ALLOWANCE+""));
//				System.out.println("dblConveyanceAllowancePaid==>"+dblConveyanceAllowancePaid);
				double dblConveyanceAllowanceExempt = Math.min(dblConveyanceAllowancePaid, dblConveyanceAllowanceLimit);
				hmUS10_16_SalHeadData.put(CONVEYANCE_ALLOWANCE+"_PAID", ""+dblConveyanceAllowancePaid);
				hmUS10_16_SalHeadData.put(CONVEYANCE_ALLOWANCE+"_EXEMPT", (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+"")) >0) ? ""+dblConveyanceAllowanceExempt : ""+dblConveyanceAllowancePaid);
				
				
//				System.out.println("dblConveyanceAllowanceExempt==>"+dblConveyanceAllowanceExempt);
				
				int nEmpChildCnt = uF.parseToInt(hmEmpChildCnt.get(strEmpId)) > 2 ? 2 : uF.parseToInt(hmEmpChildCnt.get(strEmpId));
				double dblEducationAllowanceLimit = ((uF.parseToDouble((String)hmExemption.get(EDUCATION_ALLOWANCE+""))/12) * nEmpChildCnt) * uF.parseToInt(hmMonthPaid.get(strEmpId));
				double dblEducationAllowancePaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(EDUCATION_ALLOWANCE+""));
				double dblEducationAllowanceExempt = Math.min(dblEducationAllowancePaid, dblEducationAllowanceLimit);
				
				
				String[] hraSalaryHeads = null;
				if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null){
					hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
				}
				
				double dblHraSalHeadsAmount = 0;
				for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++){
					dblHraSalHeadsAmount += uF.parseToDouble((String)hmEmpPayrollDetails.get(hraSalaryHeads[i]));
				}
				
				
//				String strHRA = (String)hmSalaryHeadMap.get(HRA+"");
				String strHRA = (String)hmEmpPayrollDetails.get(HRA+"");
				
				String strCondition1 = (String)hmHRAExemption.get("CONDITION_1");
				String strCondition2 = (String)hmHRAExemption.get("CONDITION_2");
				String strCondition3 = (String)hmHRAExemption.get("CONDITION_3");
				
				double dblCondition1 = uF.parseToDouble(strCondition1);
				double dblCondition2 = uF.parseToDouble(strCondition2);
				double dblCondition3 = uF.parseToDouble(strCondition3);
				
//				double dblHRA1 = dblCondition1 * (dblBasic + dblDA) / 100;
//				double dblHRA2 = dblCondition2 * (dblBasic + dblDA) / 100;
//				double dblHRA3 = dblCondition3 * (dblBasic + dblDA) / 100;
				double dblHRA1 = dblCondition1 * dblHraSalHeadsAmount / 100;
				double dblHRA2 = dblCondition2 * dblHraSalHeadsAmount / 100;
				double dblHRA3 = dblCondition3 * dblHraSalHeadsAmount / 100;
				
				double dblActualHRAPaid = uF.parseToDouble(strHRA);
				double dblActualRentPaid = uF.parseToDouble((String)hmRentPaid.get(strEmpId));
				
				double dblHRAExemption = Math.min(dblActualHRAPaid, dblActualRentPaid);
				boolean isMetro = uF.parseToBoolean((String)hmEmpMertoMap.get(strEmpId));
				double dblHRA = 0;
				if(isMetro){
					if(dblHRA1<dblHRA2){
						dblHRA = dblHRA1; 
						hmTaxInner.put("dblHRA", "Rent paid over "+dblCondition1+"% of salary");
					}else{
						dblHRA = dblHRA2;
						hmTaxInner.put("dblHRA", "");
						hmTaxInner.put("dblHRA", dblCondition2+"% of salary in metro cities");
					}
				}else{
					if(dblHRA1<dblHRA3){
						dblHRA = dblHRA1;
						hmTaxInner.put("dblHRA", "");
						hmTaxInner.put("dblHRA", "Rent paid over "+dblCondition1+"% of salary");
					}else{
						dblHRA = dblHRA3;
						hmTaxInner.put("dblHRA", "");
						hmTaxInner.put("dblHRA", dblCondition3+"% of salary in other cities");
					}
				}
				if(strEmpId.equals("618")){
//					System.out.println("dblActualHRAPaid==="+dblActualHRAPaid);
//					System.out.println("dblActualRentPaid==="+dblActualRentPaid);
//					System.out.println("dblHRAExemption==="+dblHRAExemption);
//					System.out.println("dblHRA==="+dblHRA);
				}
				
				dblHRAExemption = Math.min(dblHRAExemption, dblHRA);
				
				if(strEmpId.equals("618")){
//					System.out.println("after dblHRAExemption==="+dblHRAExemption);
				}
				
				ApprovePayroll objApprovePayroll = new ApprovePayroll();
				objApprovePayroll.setServletRequest(request);
				objApprovePayroll.session = session;
				objApprovePayroll.request = request;
				objApprovePayroll.CF = CF;
				
				Map hmEmpRentPaidMap = objApprovePayroll.getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
//				double dblHRAExemptions = objApprovePayroll.getHRAExemptionCalculation(con, uF,null, hmEmpPayrollDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, 0.0, 0.0, hmEmpMertoMap, hmEmpRentPaidMap);
				double dblHRAExemptions = objApprovePayroll.getHRAExemptionCalculation(con, uF,null, hmEmpPayrollDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, dblActualHRAPaid, dblHraSalHeadsAmount, hmEmpMertoMap, hmEmpRentPaidMap);
				dblHRAExemption = dblHRAExemptions;
				
				if(strEmpId.equals("618")){
//					System.out.println("after after dblHRAExemption==="+dblHRAExemption);
//					System.out.println("dblHRAExemptions==="+dblHRAExemptions);
				}
				
				double dblIncomeFromOther=uF.parseToDouble(hmEmpIncomeFromOtherSourcesMap.get(strEmpId));
				
				
//				Map<String, String> hmUS10Inner= (Map<String, String>)hmUnderSection10Map.get(strEmpId);
//				if(hmUS10Inner==null) hmUS10Inner=new HashMap<String, String>();
//				double dblMedicalAllowanceExempt =uF.parseToDouble((String)hmUS10Inner.get("Medical Allowance"));
				
				Map<String, String> hmUS10Inner= (Map<String, String>)hmUnderSection10Map.get(strEmpId);
				if(hmUS10Inner==null) hmUS10Inner=new HashMap<String, String>();
				double dblMedicalAllowanceExemptLimit =uF.parseToDouble((String)hmUS10Inner.get(""+MEDICAL_ALLOWANCE));
				
				Map<String, String> hmUS10InnerPaid= (Map<String, String>)hmUnderSection10PaidMap.get(strEmpId);
				if(hmUS10InnerPaid==null) hmUS10InnerPaid=new HashMap<String, String>();
				double dblMedicalAllowancePaid =uF.parseToDouble((String)hmUS10InnerPaid.get(""+MEDICAL_ALLOWANCE));
				double dblMedicalAllowanceExempt = Math.min(dblMedicalAllowancePaid, dblMedicalAllowanceExemptLimit);
				
				double dblLTAExemptLimit =uF.parseToDouble((String)hmUS10Inner.get(""+LTA));
				double dblLTAPaid =uF.parseToDouble((String)hmUS10InnerPaid.get(""+LTA));
				double dblLTAExempt = Math.min(dblLTAPaid, dblLTAExemptLimit);
				
				
//				double dblNetTaxableIncome = dblGross1 - dblProfessionalTaxExempt - dblHRAExemption - dblConveyanceAllowanceExempt - dblInvestment;
//				double dblNetTaxableIncome = dblGross1 - dblProfessionalTaxExempt - dblHRAExemption - dblConveyanceAllowanceExempt - dblInvestmentExempt;
				double dblNetTaxableIncome = dblGross1 - dblProfessionalTaxExempt - dblHRAExemption - dblConveyanceAllowanceExempt - dblInvestmentExempt -  dblHomeLoanTaxExempt - dbl80D - dblEducationAllowanceExempt - dblMedicalAllowanceExempt -dblLTAExempt - dblOtherExemptions;
//				double dblNetTaxableIncome = dblGross1 - dblProfessionalTaxExempt - dblHRAExemption - dblConveyanceAllowanceExempt - dblInvestmentExempt - dblOtherExemptions - dblHomeLoanTaxExempt - dblIncomeFromOther;
				
				
				double dblNetTaxableIncome = (dblPrevOrgGross + dblGross1 + dblAddExemptInAdjustedGrossIncome + dblIncomeFromOther)- dblUS10_16Exempt - dblVIA1Exempt - dblVIA2Exempt - dblVIA1ExemptIsAdjustedLimit; //- dblHomeLoanTaxExempt
				if(strEmpId.equalsIgnoreCase("618")){
//					System.out.println(strEmpId+" dblNetTaxableIncome=====>"+dblNetTaxableIncome);  
				}
				
				hmTaxInner.put("dblNetTaxableIncome", dblNetTaxableIncome+"");
				hmTaxInner.put("dblIncomeFromOther", dblIncomeFromOther+"");
				hmTaxInner.put("dblLessIncomeFromOther", dblLessIncomeFromOther+"");
				hmTaxInner.put("dblActualHRAPaid", dblActualHRAPaid+"");  
				hmTaxInner.put("dblActualRentPaid", dblActualRentPaid+"");
				hmTaxInner.put("dblHRA1", dblHRA+"");
				hmTaxInner.put("dblCondition1", dblCondition1+"");
				hmTaxInner.put("dblHRAExemption", dblHRAExemption+"");
				
				hmTaxInner.put("dblProfessionalTaxPaid", dblProfessionalTaxPaid+"");
				hmTaxInner.put("dblProfessionalTaxExempt", dblProfessionalTaxExempt+"");
				
				hmTaxInner.put("dblConveyanceAllowancePaid", dblConveyanceAllowancePaid+"");
				hmTaxInner.put("dblConveyanceAllowanceExempt", dblConveyanceAllowanceExempt+"");
				
//				hmTaxInner.put("dblOtherExemptions", dblOtherExemptions+"");
//				hmTaxInner.put("dblHomeLoanTaxExempt", dblHomeLoanTaxExempt+"");
				
//				hmTaxInner.put("dblInvestment", dblInvestmentExempt+"");
//				hmTaxInner.put("dblInvestment", hmEmpExemptionsMap.get(strEmpId) +"");
				hmTaxInner.put("hmEmpExemptionsCH1Map", hmEmpExemptionsCH1Map.get(strEmpId) +"");
				hmTaxInner.put("hmEmpExemptionsCH2Map", hmEmpExemptionsCH2Map.get(strEmpId) +"");
				hmTaxInner.put(TDS, strTDSAmt);
				
				hmTaxInner.put("dblEducationAllowancePaid", dblEducationAllowancePaid+"");
				hmTaxInner.put("dblEducationAllowanceExempt", dblEducationAllowanceExempt+""); 
				
//				hmTaxInner.put("dblMedicalAllowanceExempt", dblMedicalAllowanceExempt+""); 
//				hmTaxInner.put("dblMedicalAllowancePaid", dblMedicalAllowancePaid+"");
				
				hmTaxInner.put("dblEmpPF", dblEmpPF+""); 
				
//				hmTaxInner.put("dblLTAExempt", dblLTAExempt+"");
//				hmTaxInner.put("dblLTAPaid", dblLTAPaid+"");
				
				double TDSPayable = 0;
				if(strEmpId!=null && strEmpId.equalsIgnoreCase("618")){					
					
//					System.out.println("dblGross="+dblGross1);
//					System.out.println("dblProfessionalTaxExempt="+dblProfessionalTaxExempt);
//					System.out.println("dblHRAExemption="+dblHRAExemption);
//					System.out.println("dblConveyanceAllowanceExempt="+dblConveyanceAllowanceExempt); 
//					System.out.println("dblInvestment="+dblInvestmentExempt);
//					System.out.println("dblHomeLoanTaxExempt="+dblHomeLoanTaxExempt);
//					System.out.println("dblOtherExemptions="+dblOtherExemptions);
//					System.out.println("dblIncomeFromOther="+dblIncomeFromOther);
//					System.out.println("dblNetTaxableIncome="+dblNetTaxableIncome);    
//					System.out.println("dblCess1="+dblCess1);
//					System.out.println("dblCess2="+dblCess2);
//					System.out.println("strFinancialYearStart="+strFinancialYearStart);
//					System.out.println("strFinancialYearEnd="+strFinancialYearEnd);
//					System.out.println("Gender="+(String)hmEmpGenderMap.get(strEmpId));
//					System.out.println("Age="+(String)hmEmpAgeMap.get(strEmpId));					
//					System.out.println("dblEducationAllowancePaid="+dblEducationAllowancePaid);
//					System.out.println("dblEducationAllowanceExempt="+dblEducationAllowanceExempt);
					
//					TDSPayable = calculateTDS(dblNetTaxableIncome, dblCess1, dblCess2, strFinancialYearStart, strFinancialYearEnd, strEmpId, (String)hmEmpGenderMap.get(strEmpId), (String)hmEmpAgeMap.get(strEmpId));		
				}
				
				String strSlabType = CF.getEmpIncomeTaxSlabType(con, CF, strEmpId, strFinancialYearStart, strFinancialYearEnd);
				
				TDSPayable = calculateTDS(con, dblNetTaxableIncome, dblCess1, dblCess2, strFinancialYearStart, strFinancialYearEnd, strEmpId, (String)hmEmpGenderMap.get(strEmpId), (String)hmEmpAgeMap.get(strEmpId), strSlabType);
//				if(strEmpId.equalsIgnoreCase("924")){
//					System.out.println(strEmpId+" dblNetTaxableIncome=====>"+dblNetTaxableIncome);  
//					System.out.println(strEmpId+" TDSPayable=====>"+TDSPayable);  
//				} 
				
				double dblMaxTaxableIncome = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_MAX_TAX_INCOME"));
				double dblRebateAmt = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_REBATE_AMOUNT"));
				
				double dblRebate = 0;
				if(dblNetTaxableIncome <= dblMaxTaxableIncome && TDSPayable <= dblMaxTaxableIncome){
					if(TDSPayable>=dblRebateAmt){
						dblRebate = dblRebateAmt;
					}else if(TDSPayable > 0 && TDSPayable < dblRebateAmt){
						dblRebate = TDSPayable;
					}
				}
				
				double dblCess1Amount = (dblCess1 * (TDSPayable-dblRebate) / 100);
				double dblCess2Amount = (dblCess2 * (TDSPayable-dblRebate) / 100);
				
				if(strEmpId.equals("618")){
//					System.out.println(" TAX_LIABILITY==>"+TDSPayable);        
//					System.out.println(" CESS1==>"+dblCess1);
//					System.out.println(" CESS2==>"+dblCess2);
//					System.out.println(" CESS1_AMOUNT==>"+dblCess1Amount);
//					System.out.println(" CESS2_AMOUNT==>"+dblCess2Amount);
//					System.out.println(" dblRebate==>"+dblRebate);
//					System.out.println(" TOTAL_TAX_LIABILITY==>"+((TDSPayable-dblRebate) + dblCess1Amount+ dblCess2Amount));
				}
				
				hmTaxInner.put("TAX_LIABILITY", TDSPayable+"");
				hmTaxInner.put("CESS1", dblCess1+"");
				hmTaxInner.put("CESS2", dblCess2+"");
				hmTaxInner.put("CESS1_AMOUNT", dblCess1Amount+"");
				hmTaxInner.put("CESS2_AMOUNT", dblCess2Amount+"");
				hmTaxInner.put("TOTAL_TAX_LIABILITY", ((TDSPayable-dblRebate) + dblCess1Amount+ dblCess2Amount)+""); 
				hmTaxInner.put("TAX_REBATE", dblRebate+"");
				hmTaxLiability.put(strEmpId, hmTaxInner);
				
				
			}
			   
			
			
			pst = con.prepareStatement("select sum(amount) as amount , emp_id from payroll_generation where salary_head_id = ? and financial_year_from_date=? and financial_year_to_date=? and is_paid = true group by emp_id");
			pst.setInt(1, TDS);
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
//			System.out.println("9 date==>"+new Date());
			Map<String, String> hmPaidTdsMap = new HashMap<String, String>();
			while(rs.next()){
				hmPaidTdsMap.put(rs.getString("emp_id"), rs.getString("amount"));
			}
			rs.close();
			pst.close();
//			System.out.println("=======14========"+new Date());     
			
			
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
			getForm24QHeader(reportListExport,strFinancialYearStart,strFinancialYearEnd,uF);
			
			
			Iterator<String> it1 = hmEmployeeMap.keySet().iterator();
			List<Map<String, String>> reportList=new ArrayList<Map<String,String>>();
			int i=0;
			while(it1.hasNext()) {
				String strEmpId = (String)it1.next();
				if(uF.parseToInt(strEmpId) != 460) {
					continue;
				}
				String slabType = hmEmpSlabType.get(strEmpId);
				String strEmpName = (String)hmEmployeeMap.get(strEmpId);
				i++;
				
				Map<String, String> hmInner=new HashMap<String, String>();
				List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
				
				hmInner.put("SRNO", ""+i);
				alInnerExport.add(new DataStyle(""+i,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				hmInner.put("EMP_PAN_NO", hmEmpPanNo.get(strEmpId));
				alInnerExport.add(new DataStyle(hmEmpPanNo.get(strEmpId),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
				
				hmInner.put("EMP_PAN_REF_NO", "");
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
				
				hmInner.put("EMP_ID", strEmpId);				
				
				
				hmInner.put("EMP_NAME", strEmpName);
				alInnerExport.add(new DataStyle(strEmpName,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				
				
				double dblYears =uF.parseToDouble(hmEmpAgeMap.get(strEmpId));
				String residentAge="G";
				if(dblYears>=80){
					residentAge="O";
				}else if(dblYears>80 && dblYears<=60){
					residentAge="S";
				}
				hmInner.put("EMP_RESIDENT_AGE", residentAge);
				alInnerExport.add(new DataStyle(residentAge,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				String joiningDate=hmEmpJoiningDate.get(strEmpId);
				String empDateFrom=strFinancialYearStart;
				if(uF.getDateFormat(joiningDate, DATE_FORMAT).after(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT))){
					empDateFrom=joiningDate;
				}
				hmInner.put("EMP_DATE_FROM", empDateFrom);
				alInnerExport.add(new DataStyle(empDateFrom,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				String endDate=hmEmpEndDate.get(strEmpId);
				String empDateUp=strFinancialYearEnd;
				if(endDate!=null && !endDate.equals("") && !endDate.equals("-") && uF.getDateFormat(endDate, DATE_FORMAT).before(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT))){
					empDateUp=endDate;
				}
				hmInner.put("EMP_DATE_UP", empDateUp);
				alInnerExport.add(new DataStyle(empDateUp,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				Map hmTaxInner = (Map)hmTaxLiability.get(strEmpId);
				if(hmTaxInner==null)hmTaxInner=new HashMap();
				

				Map hmEmpPayrollDetails = (Map)hmPayrollDetails.get(strEmpId);
				if(hmEmpPayrollDetails==null)hmEmpPayrollDetails=new HashMap();
				
//				Map<String, String> hmUS10Inner= (Map<String, String>)hmUnderSection10Map.get(strEmpId);
//				if(hmUS10Inner==null) hmUS10Inner=new HashMap<String, String>();
				
//				if(strEmpId.equals("753")){
//					System.out.println("Gross====>"+hmEmpPayrollDetails.get("GROSS"));
//					System.out.println("Medical Allowance====>"+(String)hmUS10Inner.get("Medical Allowance"));
//					System.out.println("dblConveyanceAllowanceExempt====>"+(String)hmTaxInner.get("dblConveyanceAllowanceExempt"));
//					System.out.println("dblEducationAllowanceExempt====>"+(String)hmTaxInner.get("dblEducationAllowanceExempt"));
//				}
				double dblActGross = uF.parseToDouble((String)hmEmpPayrollDetails.get("GROSS") );
				double dblHRAExemption = uF.parseToDouble((String)hmTaxInner.get("dblHRAExemption"));
				double dblConveyanceAllowanceExempt = uF.parseToDouble((String)hmTaxInner.get("dblConveyanceAllowanceExempt"));
				double dblOtherExemption = uF.parseToDouble((String)hmTaxInner.get("dblOtherExemptions"));
				double dblMedicalAllowanceExempt =uF.parseToDouble((String)hmTaxInner.get("dblMedicalAllowanceExempt"));
				double dblEducationAllowanceExempt = uF.parseToDouble((String)hmTaxInner.get("dblEducationAllowanceExempt"));
				double dblLTAExempt = uF.parseToDouble((String)hmTaxInner.get("dblLTAExempt"));
//				double dblGross1 = Math.round(dblActGross) - Math.round(dblHRAExemption) - Math.round(dblConveyanceAllowanceExempt) - Math.round(dblOtherExemption) - Math.round(dblMedicalAllowanceExempt) - Math.round(dblEducationAllowanceExempt) - Math.round(dblLTAExempt);
				double dblNetTaxableIncome = uF.parseToDouble((String)hmTaxInner.get("dblNetTaxableIncome"));
				hmInner.put("CURRENT_TAXABLE_TAX_AMT", ""+Math.round(dblNetTaxableIncome)); 
				alInnerExport.add(new DataStyle(""+Math.round(dblNetTaxableIncome),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				Map<String, String> hmPreTdsEmp=(Map<String, String>) hmPrevEmpTds.get(strEmpId);
				if(hmPreTdsEmp == null) hmPreTdsEmp= new HashMap<String, String>();
				double dblPreGross =  uF.parseToDouble((String)hmPreTdsEmp.get("PREV_TOTAL_EARN") );
				hmInner.put("PREVIOUS_TAXABLE_TAX_AMT", ""+Math.round(dblPreGross));
				alInnerExport.add(new DataStyle(""+Math.round(dblPreGross),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblTotalAmtSalary = Math.round(dblNetTaxableIncome) + Math.round(dblPreGross);
				hmInner.put("TOTAL_AMT_SALARY", ""+Math.round(dblTotalAmtSalary));
				alInnerExport.add(new DataStyle(""+Math.round(dblTotalAmtSalary),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblEntertainmentAllowance=0.0d;
				hmInner.put("ENTERTAINMENT_ALLOWANCE", ""+Math.round(dblEntertainmentAllowance));
				alInnerExport.add(new DataStyle(""+Math.round(dblEntertainmentAllowance),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblProfessionalTaxExempt = uF.parseToDouble((String)hmTaxInner.get("dblProfessionalTaxExempt"));
				hmInner.put("PROFESSIONAL_TAX", ""+Math.round(dblProfessionalTaxExempt));
				alInnerExport.add(new DataStyle(""+Math.round(dblProfessionalTaxExempt),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double incomeChargeableSalary = Math.round(dblTotalAmtSalary) - (Math.round(dblEntertainmentAllowance) +Math.round(dblProfessionalTaxExempt)); 
				hmInner.put("INCOME_CHARGE_SALARY", ""+Math.round(incomeChargeableSalary));
				alInnerExport.add(new DataStyle(""+Math.round(incomeChargeableSalary),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblIncomeFromOther = uF.parseToDouble((String)hmTaxInner.get("dblIncomeFromOther"));
//				double dblIncomeFromOther = uF.parseToDouble((String)hmTaxInner.get("dblHomeLoanTaxExempt"));
				hmInner.put("INCOME_FROM_OTHER", (dblIncomeFromOther > 0 ? "-"+dblIncomeFromOther : ""+dblIncomeFromOther));
				alInnerExport.add(new DataStyle((dblIncomeFromOther > 0 ? "-"+dblIncomeFromOther : ""+dblIncomeFromOther),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
//				double dblGrossTotal=Math.round(incomeChargeableSalary)+Math.round(dblIncomeFromOther);
				double dblGrossTotal=Math.round(incomeChargeableSalary)-Math.round(dblIncomeFromOther);
				hmInner.put("GROSS_TOTAL", ""+Math.round(dblGrossTotal));
				alInnerExport.add(new DataStyle(""+Math.round(dblGrossTotal),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				//-------------------------------------End---------------------------------------------------------------
				
				double dblInvestment = uF.parseToDouble((String)hmTaxInner.get("dblInvestment"));

				double dblChapterVIA=0.0d;
				List<String> alUnderSection=new ArrayList<String>();
				Map<String,String> hmInvest=hmEmpInvestment.get(strEmpId);
				if(hmInvest==null) hmInvest = new HashMap<String, String>();
				for(int a=0;chapter1SectionList!=null && a<chapter1SectionList.size();a++){
					List<String> innnList = chapter1SectionList.get(a);
					String strSectionId = innnList.get(0);
					String strSlabType = innnList.get(1);
					if(uF.parseToInt(slabType)!= uF.parseToInt(strSlabType) && uF.parseToInt(strSlabType)!=2) {
						continue;
					}
					String strAmt = uF.showData(hmInvest.get(strSectionId), "");
			  		Map<String, List<Map<String, String>>> hmSubInvestment = (Map<String, List<Map<String, String>>>) hmEmpSubInvestment.get("" + strEmpId);
			  		if(hmSubInvestment==null) hmSubInvestment= new HashMap<String, List<Map<String, String>>>();
			  		List<Map<String, String>> subInvestList = (List<Map<String, String>>) hmSubInvestment.get("" +strSectionId);
			  		
			  		if(hmSectionMap.containsKey(strSectionId)){
			  			strAmt=""+Math.round(dblInvestment);
			  		}
			  		dblChapterVIA+=uF.parseToDouble(strAmt);
				}
			
				
				double dblChapterVIA1 = dblChapterVIA;
//				double dblChapterVIA1=uF.parseToDouble(hmEmpExemptionsCH1Map.get(strEmpId)) + Math.round(uF.parseToDouble(""+hmTaxInner.get("dblEmpPF")));
				hmInner.put("CHAPTER_VI_A1", ""+Math.round(dblChapterVIA1));
				alInnerExport.add(new DataStyle(""+Math.round(dblChapterVIA1),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
//				double dblChapterVIA2=uF.parseToDouble(hmEmpExemptionsCH2Map.get(strEmpId));
				double dblChapterVIA2= 0.0d;
				hmInner.put("CHAPTER_VI_A2", ""+Math.round(dblChapterVIA2));
				alInnerExport.add(new DataStyle(""+Math.round(dblChapterVIA2),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
//				double dblChapterVIAOther=uF.parseToDouble((String)hmTaxInner.get("dblHomeLoanTaxExempt")); 
				double dblChapterVIAOther=0.0d; 
				Map<String,String> hmInvest1=hmEmpInvestment1.get(strEmpId);
				if(hmInvest1==null) hmInvest1 = new HashMap<String, String>();
				
				for(int a=0;chapter2SectionList!=null && a<chapter2SectionList.size();a++) {
					List<String> innnList = chapter2SectionList.get(a);
					String strSectionId = innnList.get(0);
					String strSlabType = innnList.get(1);
					if(uF.parseToInt(slabType)!= uF.parseToInt(strSlabType) && uF.parseToInt(strSlabType)!=2) {
						continue;
					}
					String strAmt = uF.showData(hmInvest1.get(strSectionId), "");
					dblChapterVIAOther+=uF.parseToDouble(strAmt);
				}
				hmInner.put("CHAPTER_VI_AOTHER", ""+Math.round(dblChapterVIAOther));
				alInnerExport.add(new DataStyle(""+Math.round(dblChapterVIAOther),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblTotalAmtChapetrVIA=Math.round(dblChapterVIA1)+Math.round(dblChapterVIA2)+Math.round(dblChapterVIAOther);
				hmInner.put("TOTAL_CHAPTER_VIA", ""+Math.round(dblTotalAmtChapetrVIA));
				alInnerExport.add(new DataStyle(""+Math.round(dblTotalAmtChapetrVIA),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblTotalTaxIncome=Math.round(dblGrossTotal) - Math.round(dblTotalAmtChapetrVIA);
				hmInner.put("TOTAL_TAX_INCOME", ""+Math.round(dblTotalTaxIncome));
				alInnerExport.add(new DataStyle(""+Math.round(dblTotalTaxIncome),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				if("460".equalsIgnoreCase(strEmpId)){
//					System.out.println("(String)hmTaxInner.get(TAX_LIABILIT)====>"+(String)hmTaxInner.get("TAX_LIABILITY"));
				}
				
				double dblIncomeTax=uF.parseToDouble((String)hmTaxInner.get("TAX_LIABILITY"));
				double dblRebate = uF.parseToDouble((String)hmTaxInner.get("TAX_REBATE"));
				dblIncomeTax = dblIncomeTax > 0 ? (Math.round(dblIncomeTax) - +Math.round(dblRebate)) : dblIncomeTax;
				hmInner.put("INCOME_TAX", ""+Math.round(dblIncomeTax));
				alInnerExport.add(new DataStyle(""+Math.round(dblIncomeTax),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
								
				
				hmInner.put("SURCHARGE", "0");
				alInnerExport.add(new DataStyle("0",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblEduCess=uF.parseToDouble((String)hmTaxInner.get("CESS2_AMOUNT")) + uF.parseToDouble((String)hmTaxInner.get("CESS1_AMOUNT")); 
				hmInner.put("EDU_CESS", ""+Math.round(dblEduCess));
				alInnerExport.add(new DataStyle(""+Math.round(dblEduCess),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblTaxRelief=0.0d;
				hmInner.put("INCOME_TAX_RELIEF", ""+Math.round(dblTaxRelief));
				alInnerExport.add(new DataStyle(""+Math.round(dblTaxRelief),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblTaxPayable=Math.round(dblIncomeTax)+ Math.round(dblEduCess);
//				hmInner.put("TAX_PAYABLE", ""+Math.round(dblTaxPayable));
//				alInnerExport.add(new DataStyle(""+Math.round(dblTaxPayable),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblNetTaxIncome=Math.round(dblTaxPayable) - Math.round(dblTaxRelief);
				hmInner.put("NET_TAX_INCOME", ""+Math.round(dblNetTaxIncome));
				alInnerExport.add(new DataStyle(""+Math.round(dblNetTaxIncome),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblTDS = uF.parseToDouble((String)hmPaidTdsMap.get(strEmpId));
				hmInner.put("TDS_AMT", ""+Math.round(dblTDS));
				alInnerExport.add(new DataStyle(""+Math.round(dblTDS),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblPreEmpTDS = uF.parseToDouble((String)hmPreTdsEmp.get("PREV_TOTAL_DEDUCT") );
				hmInner.put("PREV_EMP_TDS_AMT", ""+Math.round(dblPreEmpTDS));
				alInnerExport.add(new DataStyle(""+Math.round(dblPreEmpTDS),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblTotalTDS=Math.round(dblTDS) + Math.round(dblPreEmpTDS);
				hmInner.put("TOTAL_TDS", ""+Math.round(dblTotalTDS));
				alInnerExport.add(new DataStyle(""+Math.round(dblTotalTDS),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				double dblShortFallTDS=Math.round(dblNetTaxIncome) - Math.round(dblTotalTDS);
				hmInner.put("SHORTFALL_TDS", ""+Math.round(dblShortFallTDS));
				alInnerExport.add(new DataStyle(""+Math.round(dblShortFallTDS),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				hmInner.put("FURNISH_PAN", hmEmpPanNo.get(strEmpId)!=null ? "N" : "");
				alInnerExport.add(new DataStyle((hmEmpPanNo.get(strEmpId)!=null ? "N" : ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				//Added by M@yuri.B---------------------------------------------------------------
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				//Added by M@yuri.B----------------------------------------------------------------------------------
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				//-----------------------------End-------------------------------------------------
				
				hmInner.put("ERROR_DISCRIPTION", "");
				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				reportList.add(hmInner);
				reportListExport.add(alInnerExport);
			}

			request.setAttribute("reportList", reportList);
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("reportListExport", reportListExport);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
//		return SUCCESS;
	}*/
	
//public String viewForm24QReport(UtilityFunctions uF){
//		
//		Connection con = null;
//		PreparedStatement pst=null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//
//		try {
//			
//			String[] strFianacialDates = null;
//			String strFinancialYearStart = null;
//			String strFinancialYearEnd = null;
//
//			if (getFinancialYear() != null) {				
//				strFianacialDates = getFinancialYear().split("-");
//				strFinancialYearStart = strFianacialDates[0];
//				strFinancialYearEnd = strFianacialDates[1];			
//			} else {				
//				strFianacialDates = new FillFinancialYears(request).fillLatestFinancialYears();
//				setFinancialYear(strFianacialDates[0] + "-" + strFianacialDates[1]);				
//				strFinancialYearStart = strFianacialDates[0];
//				strFinancialYearEnd = strFianacialDates[1];				 
//			}
//			
//			con = db.makeConnection(con);
//			
//			Map<String,String> hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
//			Map<String, String> hmDept =CF.getDeptMap(con);	
//			Map<String, String> hmEmpAgeMap =CF.getEmpAgeMap(con, CF);
//			Map hmEmpMertoMap = new HashMap();
//			Map hmEmpWlocationMap = new HashMap();
//			Map hmEmpStateMap = new HashMap();
//			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
//			
//			Map<String,Map<String,String>> hmPrevEmpTds = CF.getPrevEmpTdsDetails(con,uF,strFinancialYearStart,strFinancialYearEnd);
//			
//			Map hmEmpGenderMap = CF.getEmpGenderMap(con);
//			
//			StringBuilder sbQuery=new StringBuilder();
//			sbQuery.append("SELECT eod.emp_id,epd.emp_fname,epd.emp_lname,epd.empcode,eod.depart_id,epd.emp_pan_no,epd.employment_end_date,epd.joining_date FROM employee_official_details eod, employee_personal_details epd WHERE epd.emp_per_id=eod.emp_id  " +
//					"and eod.emp_id in (select distinct emp_id from attendance_details where to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD') between ? and ?) and is_form16=true ");
//			if(uF.parseToInt(getF_org())>0){
//				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
//			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
//				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
//			}			
//			if(getWLocation()!=null && getWLocation().length>0){
//                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getWLocation(), ",")+") ");
//            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
//				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//			}
//			
//			if(getF_department()!=null && getF_department().length>0){
//                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
//            }
//            
//            if(getF_service()!=null && getF_service().length>0){
//                sbQuery.append(" and (");
//                for(int i=0; i<getF_service().length; i++){
//                    sbQuery.append(" eod.service_id like '%"+getF_service()[i]+",%'");
//                    
//                    if(i<getF_service().length-1){
//                        sbQuery.append(" OR "); 
//                    }
//                }
//                sbQuery.append(" ) ");
//                
//            }
//			
//			if(getLevel()!=null && getLevel().length>0){
//                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getLevel(), ",")+") ) ");
//            }            
//			sbQuery.append(" order by epd.emp_fname"); 
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
////			System.out.println("=======0========"+new Date());
////			System.out.println("pst=0=="+pst);
//			rs = pst.executeQuery();
//			
//			Map<String, String> hmEmployeeMap = new HashMap<String, String>();
//			Map<String, String> hmEmpJoiningDate = new HashMap<String, String>();
//			Map<String, String> hmEmpCode = new HashMap<String, String>();
//			Map<String, String> hmEmpDepartment = new HashMap<String, String>();
//			Map<String, String> hmEmpPanNo = new HashMap<String, String>();
//			Map<String, String> hmEmpEndDate = new HashMap<String, String>(); 
//			while (rs.next()) {
//				if (rs.getInt("emp_id") < 0) {
//					continue;
//				} 
//				hmEmployeeMap.put(rs.getString("emp_id"), rs.getString("emp_fname") + " " + rs.getString("emp_lname"));
//				hmEmpJoiningDate.put(rs.getString("emp_id"), uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
//				hmEmpCode.put(rs.getString("emp_id"), rs.getString("empcode"));
//				hmEmpDepartment.put(rs.getString("emp_id"), rs.getString("depart_id"));
//				hmEmpPanNo.put(rs.getString("emp_id"), uF.showData(rs.getString("emp_pan_no"), ""));
//				hmEmpEndDate.put(rs.getString("emp_id"), uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT));
//			}
//			rs.close();
//			pst.close();
////			System.out.println("=======1========"+new Date());
//			
//			Map hmPayrollDetails = new HashMap();			
//			List al = new ArrayList();			
//			double dblInvestmentExemption = 0.0d;
//			
//			Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
//			pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1 ");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
////			System.out.println(" pst==>"+pst);
//			rs = pst.executeQuery();
//			while(rs.next()){
//				hmOtherTaxDetails.put(rs.getString("state_id")+"_SERVICE_TAX", rs.getString("service_tax"));
//				hmOtherTaxDetails.put(rs.getString("state_id")+"_EDU_TAX", rs.getString("education_tax"));
//				hmOtherTaxDetails.put(rs.getString("state_id")+"_STD_TAX", rs.getString("standard_tax"));
//				hmOtherTaxDetails.put(rs.getString("state_id")+"_FLAT_TDS", rs.getString("flat_tds"));
//				hmOtherTaxDetails.put(rs.getString("state_id")+"_SWACHHA_BHARAT_CESS", rs.getString("swachha_bharat_cess"));
//				
////				dblInvestmentExemption = 100000;
//			}
//			rs.close();
//			pst.close();
////			System.out.println("=======2========"+new Date());
//			
//			pst = con.prepareStatement("select sum(amount) as amount,salary_head_id,emp_id,earning_deduction from payroll_generation pg where financial_year_from_date=? and financial_year_to_date =? and is_paid = true and salary_head_id not in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+") group by salary_head_id, emp_id,earning_deduction order by emp_id,earning_deduction desc");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
////			System.out.println(" pst==>"+pst);
//			rs = pst.executeQuery();					
//			String strEmpIdNew = null;
//			String strEmpIdOld = null;
//			Map hmInner1 = new HashMap();
//			double dblGross = 0.0d;
//			
//			while(rs.next()){
//				strEmpIdNew = rs.getString("emp_id");
//				
//				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
//					hmInner1 = new HashMap();
//					dblGross = 0.0d;
//				}
//								
//				hmInner1.put(rs.getString("salary_head_id"), rs.getString("amount"));   
//				if(rs.getString("earning_deduction").equalsIgnoreCase("E")){
//					dblGross += rs.getDouble("amount");
//				}
//				hmInner1.put("GROSS", dblGross+"");
//				hmPayrollDetails.put(strEmpIdNew, hmInner1);
//				
//				strEmpIdOld = strEmpIdNew;
//			}
//			rs.close();
//			pst.close();
//			log.debug(" 1  hmPayrollDetails==>"+hmPayrollDetails);
////			System.out.println("=======3========"+new Date());
//			
//			pst = con.prepareStatement("select * from exemption_details where exemption_from=? and exemption_to=?");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
////			System.out.println(" pst==>"+pst);
//			Map hmExemption = new HashMap();
//			while(rs.next()){
////				hmExemption.put(rs.getString("exemption_name"), rs.getString("exemption_limit"));
//				hmExemption.put(rs.getString("salary_head_id"), rs.getString("exemption_limit"));
//			}
//			rs.close();
//			pst.close();
////			System.out.println("=======4========"+new Date());
//			
//			pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
////			System.out.println(" pst==>"+pst);
//			Map hmHRAExemption = new HashMap();
//			while(rs.next()){
//				hmHRAExemption.put("CONDITION_1", rs.getString("condition1"));
//				hmHRAExemption.put("CONDITION_2", rs.getString("condition2"));
//				hmHRAExemption.put("CONDITION_3", rs.getString("condition3"));
//				hmHRAExemption.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
//			}
//			rs.close();
//			pst.close();
////			System.out.println("=======5========"+new Date());
//			
//			Map hmSectionLimitA = new HashMap();
//			Map hmSectionLimitP = new HashMap();			
//			Map hmSectionLimitEmp = new HashMap();
//			
//			pst = con.prepareStatement(selectSection);
//			rs = pst.executeQuery();
////			System.out.println(" pst==>"+pst);
//			while (rs.next()) {
//				
//				if(rs.getString("section_limit_type").equalsIgnoreCase("A")){
//					hmSectionLimitA.put(rs.getString("section_id"), rs.getString("section_exemption_limit"));
//				}else{
//					hmSectionLimitP.put(rs.getString("section_id"), rs.getString("section_exemption_limit"));
//				}
//			}
//			rs.close();
//			pst.close();
////			System.out.println("=======6========"+new Date());
//			
//			
//			//pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details id, section_details sd where sd.section_id=id.section_id and fy_from = ? and fy_to=? and status = true and section_code in ('HRA') group by emp_id ");
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details id, section_details sd " +
//					"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to=? and sd.financial_year_start=? and sd.financial_year_end=? " +
//					"and status = true and section_code in ('HRA') and trail_status = 1 group by emp_id ");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
////			System.out.println(" pst==>"+pst);
//			Map hmRentPaid = new HashMap();
//			while(rs.next()){
//				hmRentPaid.put(rs.getString("emp_id"), rs.getString("amount_paid"));
//			}
//			rs.close();
//			pst.close();
////			System.out.println("=======7========"+new Date());
//			
////			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd  where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and section_code not in ('HRA') and sd.section_id not in (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%') and isdisplay=true and parent_section=0 group by emp_id, sd.section_id order by emp_id ");
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, " +
//					"section_details sd where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true " +
//					" and trail_status = 1 and section_code not in ('HRA') and sd.section_id not in (select section_id from section_details " +
//					"where upper(section_code) like '%HOME% %INTEREST%' and financial_year_start=? and financial_year_end=?) " +
//					"and isdisplay=true and parent_section=0 and under_section=8 and sd.financial_year_start=? and sd.financial_year_end=? group by emp_id, sd.section_id order by emp_id ");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
////			System.out.println(" pst==>"+pst);
//			Map hmInvestment = new HashMap();
//			Map<String, String> hmEmpExemptionsCH1Map = new HashMap<String, String>();
//			Map<String, Map<String, String>> hmEmpInvestment = new HashMap<String, Map<String, String>>();
//			
//			double dblInvestmentLimit = 0;
//			double dblInvestmentEmp = 0;
//			
//			while(rs.next()){
//				String strSectionId = rs.getString("section_id");
//				double dblInvestment = rs.getDouble("amount_paid");
//				
//				if(hmSectionLimitA.containsKey(strSectionId)){
//					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
//				}else{
//					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
//					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
//				}
//				Map<String,String> hmInvest=hmEmpInvestment.get(rs.getString("emp_id"));
//				if(hmInvest==null) hmInvest=new HashMap<String, String>();
//				
//				if(dblInvestment>=dblInvestmentLimit){
//					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH1Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
//					hmEmpExemptionsCH1Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
//					hmInvest.put(strSectionId, ""+dblInvestmentLimit);
//				}else{
//					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH1Map.get(rs.getString("emp_id"))) + dblInvestment;
//					hmEmpExemptionsCH1Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
//					hmInvest.put(strSectionId, ""+dblInvestment);
//				}
//				hmEmpInvestment.put(rs.getString("emp_id"), hmInvest);
//				
//				if(rs.getString("emp_id").equals("460")){
////					System.out.println("dblInvestment==="+dblInvestment);
////					System.out.println("dblInvestmentLimit==="+dblInvestmentLimit);
////					System.out.println("dblInvestmentEmp==="+dblInvestmentEmp);					
//				}
//			}	
//			rs.close();
//			pst.close();		
////			System.out.println("=======8========"+new Date());
////			System.out.println("hmEmpInvestment==="+hmEmpInvestment.get("460"));    
//			
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, " +
//					"section_details sd where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true " +
//					" and trail_status = 1 and section_code not in ('HRA') and sd.section_id not in (select section_id from section_details " +
//					"where upper(section_code) like '%HOME% %INTEREST%' and financial_year_start=? and financial_year_end=?) " +
//					"and sd.financial_year_start=? and sd.financial_year_end=? and isdisplay=true and parent_section=0 and under_section=9 group by emp_id, sd.section_id order by emp_id ");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
////			System.out.println(" pst==>"+pst);
//			Map<String, Map<String, String>> hmEmpInvestment1 = new HashMap<String, Map<String, String>>();
//			Map<String, String> hmEmpExemptionsCH2Map = new HashMap<String, String>();
//			dblInvestmentLimit = 0;
//			dblInvestmentEmp = 0;
//			
//			while(rs.next()){
//				String strSectionId = rs.getString("section_id");
//				double dblInvestment = rs.getDouble("amount_paid");
//				
//				if(hmSectionLimitA.containsKey(strSectionId)){
//					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
//				}else{
//					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
//					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
//				}
//				Map<String,String> hmInvest=hmEmpInvestment1.get(rs.getString("emp_id"));
//				if(hmInvest==null) hmInvest=new HashMap<String, String>();
//				
//				if(dblInvestment>=dblInvestmentLimit){
//					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
//					hmEmpExemptionsCH2Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
//					hmInvest.put(strSectionId, ""+dblInvestmentLimit);
//				}else{
//					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestment;
//					hmEmpExemptionsCH2Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
//					hmInvest.put(strSectionId, ""+dblInvestment);
//				}
//				hmEmpInvestment1.put(rs.getString("emp_id"), hmInvest);
//				
//				if(rs.getString("emp_id").equals("460")){
////					System.out.println("dblInvestment==="+dblInvestment);
////					System.out.println("dblInvestmentLimit==="+dblInvestmentLimit);
////					System.out.println("dblInvestmentEmp==="+dblInvestmentEmp);					
//				}
//			}	
//			rs.close();
//			pst.close();		
////			System.out.println("hmEmpInvestment1==="+hmEmpInvestment1.get("460"));
////			System.out.println("=======9========"+new Date());
//			
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, " +
//					"section_details sd where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true " +
//					" and trail_status = 1 and section_code in ('80C and 80CCC') and sd.financial_year_start=? and sd.financial_year_end=? " +
//					"and isdisplay=true and parent_section=0 group by emp_id, sd.section_id order by emp_id ");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
////			System.out.println(" pst==>"+pst);
//			Map<String, String> hmEmp80C = new HashMap<String, String>();
//			dblInvestmentLimit = 0;
//			dblInvestmentEmp = 0;
//			
//			while(rs.next()){
//				String strSectionId = rs.getString("section_id"); 
//				double dblInvestment = rs.getDouble("amount_paid");
//				
//				if(hmSectionLimitA.containsKey(strSectionId)){
//					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
//				}else{
//					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
//					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
//				}
//				
//				
//				if(dblInvestment>=dblInvestmentLimit){
//					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
//					hmEmp80C.put(rs.getString("emp_id"), dblInvestmentLimit+"");
//				}else{
//					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestment;
//					hmEmp80C.put(rs.getString("emp_id"), dblInvestment+"");
//				}
//				
//			}
//			rs.close();
//			pst.close();			
////			System.out.println("hmEmpInvestment1==="+hmEmpInvestment1.get("460"));
////			System.out.println("=======10========"+new Date());
//			
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, " +
//					"section_details sd where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true " +
//					" and trail_status = 1 and section_code in ('80D') and sd.financial_year_start=? and sd.financial_year_end=? " +
//					"and isdisplay=true and parent_section=0 group by emp_id, sd.section_id order by emp_id ");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
////			System.out.println(" pst==>"+pst);
//			Map<String, String> hmEmp80D = new HashMap<String, String>();
//			dblInvestmentLimit = 0;
//			dblInvestmentEmp = 0;
//			
//			while(rs.next()){
//				String strSectionId = rs.getString("section_id");
//				double dblInvestment = rs.getDouble("amount_paid");
//				
//				if(hmSectionLimitA.containsKey(strSectionId)){
//					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
//				}else{
//					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
//					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
//				}
//				
//				
//				if(dblInvestment>=dblInvestmentLimit){
//					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
//					hmEmp80D.put(rs.getString("emp_id"), dblInvestmentLimit+"");
//				}else{
//					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestment;
//					hmEmp80D.put(rs.getString("emp_id"), dblInvestment+"");
//				}
//				
//			}
//			rs.close();
//			pst.close();
////			System.out.println("=======11========"+new Date());
//			
//			
//			/**
//			 * HOME LOAN INTEREST EXEMPTION 
//			 */
////			pst = con.prepareStatement("select * from section_details where section_id = (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%') ");
//			pst = con.prepareStatement("select * from section_details where upper(section_code) like '%HOME% %INTEREST%' and financial_year_start=? and financial_year_end=?");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
////			System.out.println(" pst==>"+pst);
//			double dblLoanExemptionLimit = 0;
//			while (rs.next()) {
//				dblLoanExemptionLimit = rs.getDouble("section_exemption_limit");
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details where fy_from = ? and fy_to = ? " +
//					"and status = true and trail_status = 1 and  section_id = (select section_id from section_details where " +
//					"upper(section_code) like '%HOME% %INTEREST%' and financial_year_start=? and financial_year_end=?) group by emp_id");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
////			System.out.println(" pst==>"+pst);
//			Map<String, String> hmEmpHomeLoanMap = new HashMap<String, String>();
//			while (rs.next()) {
//				
//				if(uF.parseToDouble(rs.getString("amount_paid"))>dblLoanExemptionLimit){
//					hmEmpHomeLoanMap.put(rs.getString("emp_id"), dblLoanExemptionLimit+"");
//				}else{
//					hmEmpHomeLoanMap.put(rs.getString("emp_id"), rs.getString("amount_paid"));
//				}
//			}
//			rs.close();
//			pst.close();
////			System.out.println("=======12========"+new Date());
//			/**
//			 * HOME LOAN INTEREST EXEMPTION 
//			 */
//			
//			Map<String,String> hmEmpIncomeFromOtherSourcesMap = new HashMap<String,String>();			
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd  " +
//					"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and sd.financial_year_start=? and sd.financial_year_end=? " +
//					"and trail_status = 1 and status = true and section_code not in ('HRA') and sd.section_id not in (select section_id " +
//					"from section_details where upper(section_code) like '%HOME% %INTEREST%' and financial_year_start=? and financial_year_end=?) " +
//					"and isdisplay=false group by emp_id, sd.section_id order by emp_id ");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
////			System.out.println("pst========"+pst);
//			rs = pst.executeQuery();			
//			double dblInvestmentIncomeSourcesEmp = 0;		
//			while (rs.next()) {
//				String strSectionId = rs.getString("section_id");
//				double dblInvestment = rs.getDouble("amount_paid");
//				
//				dblInvestmentIncomeSourcesEmp = uF.parseToDouble(hmEmpIncomeFromOtherSourcesMap.get(rs.getString("emp_id"))) + dblInvestment;
//				hmEmpIncomeFromOtherSourcesMap.put(rs.getString("emp_id"), dblInvestmentIncomeSourcesEmp+"");
//			}
//			rs.close();
//			pst.close();
////			System.out.println("=======13========"+new Date());
//			
//			Map<String,String> hmEmpUnderSection10Map = new HashMap<String,String>();			
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id,sd.section_code from investment_details id, section_details sd " +
//					"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and status = true " +
//					"and section_code not in ('HRA') and under_section=4 and isdisplay=true and sd.financial_year_start=? and sd.financial_year_end=? " +
//					"group by emp_id, sd.section_id,sd.section_code order by emp_id ");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
////			System.out.println("pst========"+pst);
//			rs = pst.executeQuery();			
//			double dblUnderSection10Emp = 0;		
//			Map<String,Map<String,String>> hmUnderSection10Map = new HashMap<String,Map<String,String>>();
//			while (rs.next()) {
//				String strSectionId = rs.getString("section_id");
//				double dblInvestment = rs.getDouble("amount_paid");				
//				
//				if(hmSectionLimitA.containsKey(strSectionId)){
//					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
//				}else{
//					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
//					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
//				}
//				
//				Map<String, String> hmInner= (Map<String, String>)hmUnderSection10Map.get(rs.getString("emp_id"));
//				if(hmInner==null) hmInner=new HashMap<String, String>();
//				
//				double dblAllowanceExempt = Math.min(dblInvestment, dblInvestmentLimit);
//				
//				hmInner.put(rs.getString("section_code"), ""+dblAllowanceExempt);				
//				hmUnderSection10Map.put(rs.getString("emp_id"), hmInner);   
//				
//				dblUnderSection10Emp = uF.parseToDouble(hmEmpUnderSection10Map.get(rs.getString("emp_id"))) + dblAllowanceExempt;
//				hmEmpUnderSection10Map.put(rs.getString("emp_id"), dblInvestmentIncomeSourcesEmp+"");
//				  
//				
//			}
//			rs.close();
//			pst.close();
////			System.out.println("=======13========"+new Date());
////			System.out.println("hmUnderSection10Map====="+hmUnderSection10Map);
//			
//			Map hmTaxLiability = new HashMap();
//			Set set = hmPayrollDetails.keySet();
//			Iterator it = set.iterator();
//			while(it.hasNext()){
//				Map hmTaxInner = new HashMap();
//				String strEmpId = (String)it.next();
//				Map hmEmpPayrollDetails = (Map)hmPayrollDetails.get(strEmpId);
//				
//				double dblBasic = uF.parseToDouble((String)hmEmpPayrollDetails.get(BASIC+""));
//				double dblDA = uF.parseToDouble((String)hmEmpPayrollDetails.get(DA+""));
//				
//				String strTDSAmt = (String)hmSalaryHeadMap.get(TDS+"");
//				
//				String strConveyanceAllowance = (String)hmSalaryHeadMap.get(CONVEYANCE_ALLOWANCE+"");
//				String strProfessionalTax = (String)hmSalaryHeadMap.get(PROFESSIONAL_TAX+"");
//				
//				double dblGross1 = uF.parseToDouble((String)hmEmpPayrollDetails.get("GROSS") );
//				
//				double dblProfessionalTaxPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(PROFESSIONAL_TAX+""));
////				double dblProfessionalTaxExemptLimit = uF.parseToDouble((String)hmExemption.get(strProfessionalTax));
//				double dblProfessionalTaxExemptLimit = uF.parseToDouble((String)hmExemption.get(PROFESSIONAL_TAX+""));
//				double dblProfessionalTaxExempt = Math.min(dblProfessionalTaxPaid, dblProfessionalTaxExemptLimit);
//				
//				double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_EDU_TAX"));
//				double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_STD_TAX"));
//				double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_FLAT_TDS"));
//				
//				
//				
//				
//				
//				// Exemptions needs to worked out as other exemptions				
//				
//				   
//				double dblHomeLoanTaxExempt = uF.parseToDouble(hmEmpHomeLoanMap.get(strEmpId));
//				
//				
//					
//				double dblOtherExemptions = 0;
//				Set setSalaryMap = hmSalaryHeadMap.keySet();
//				Iterator itSalaryMap = setSalaryMap.iterator();
//				
//				while(itSalaryMap.hasNext()){
//					String strSalaryHead = (String)itSalaryMap.next();
//					String strSalaryHeadName = (String)hmSalaryHeadMap.get(strSalaryHead);
//					
//					if(uF.parseToInt(strSalaryHead) == PROFESSIONAL_TAX ||  uF.parseToInt(strSalaryHead) == CONVEYANCE_ALLOWANCE ||  uF.parseToInt(strSalaryHead) == EDUCATION_ALLOWANCE){
//						continue;
//					}
//					
//					double dblOtherExemptionsPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(strSalaryHead));
////					double dblOtherExemptionsExemptLimit = uF.parseToDouble((String)hmExemption.get(strSalaryHeadName));
//					double dblOtherExemptionsExemptLimit = uF.parseToDouble((String)hmExemption.get(strSalaryHead));
//					double dblOtherExemptionsTaxExempt = Math.min(dblOtherExemptionsPaid, dblOtherExemptionsExemptLimit);
//					
//					
//					dblOtherExemptions += dblOtherExemptionsTaxExempt; 
//					
//				}
//					
//				double dblInvestment = uF.parseToDouble((String)hmInvestment.get(strEmpId));
////				double dblInvestment = uF.parseToDouble((String)hmEmpExemptionsMap.get(strEmpId));
//				double dblEPFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(EMPLOYEE_EPF+""));
//				double dblEPRFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(EMPLOYER_EPF+""));
//				double dblEPVFPaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(VOLUNTARY_EPF+""));
//				
//				double dblChapterVIA1=uF.parseToDouble(hmEmpExemptionsCH1Map.get(strEmpId));				
//				double dblChapterVIA2=uF.parseToDouble(hmEmpExemptionsCH2Map.get(strEmpId));
//				double dbl80C=uF.parseToDouble(hmEmp80C.get(strEmpId));
//				double dbl80D=uF.parseToDouble(hmEmp80D.get(strEmpId));
////				double dblTotalInvestment =dblChapterVIA1+dblChapterVIA2;
////				double dblTotalInvestment =dblChapterVIA1+dblChapterVIA2 + dblEPFPaid;
//				double dblTotalInvestment =dbl80C + dblEPFPaid + dblEPRFPaid + dblEPVFPaid;
////				double dblTotalInvestment =0.0d; //dblInvestment + dblEPFPaid;  
//				double dblInvestmentExempt = Math.min(dblTotalInvestment, dblInvestmentExemption);
//				  
//				
//				if(strEmpId.equals("460")){
////					System.out.println("dblInvestment==="+dblInvestment);
////					System.out.println("dblEPFPaid==="+dblEPFPaid);
////					System.out.println("dblEPRFPaid==="+dblEPRFPaid);
////					System.out.println("dblEPVFPaid==="+dblEPVFPaid);
////					System.out.println("dblInvestmentExempt==="+dblInvestmentExempt);
////					System.out.println("dblTotalInvestment===="+dblTotalInvestment); 
////					System.out.println("dblInvestmentExemption="+dblInvestmentExemption);
//				}
//				
//				
//				
////				double dblConveyanceAllowanceLimit = uF.parseToDouble((String)hmExemption.get(strConveyanceAllowance));
//				double dblConveyanceAllowanceLimit = uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""));
//				double dblConveyanceAllowancePaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(CONVEYANCE_ALLOWANCE+""));
//				double dblConveyanceAllowanceExempt = Math.min(dblConveyanceAllowancePaid, dblConveyanceAllowanceLimit);
//				
////				String strEducationAllowance = (String)hmSalaryHeadMap.get("446");
////				double dblEducationAllowanceLimit = uF.parseToDouble((String)hmExemption.get(strEducationAllowance));
////				double dblEducationAllowancePaid = uF.parseToDouble((String)hmEmpPayrollDetails.get("446"));
////				double dblEducationAllowanceExempt = Math.min(dblEducationAllowancePaid, dblEducationAllowanceLimit);
//				
//
//				double dblEducationAllowanceLimit = uF.parseToDouble((String)hmExemption.get(EDUCATION_ALLOWANCE+""));
//				double dblEducationAllowancePaid = uF.parseToDouble((String)hmEmpPayrollDetails.get(EDUCATION_ALLOWANCE+""));
//				double dblEducationAllowanceExempt = Math.min(dblEducationAllowancePaid, dblEducationAllowanceLimit);
//				
//				String[] hraSalaryHeads = null;
//				if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null){
//					hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
//				}
//				
//				double dblHraSalHeadsAmount = 0;
//				for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++){
//					dblHraSalHeadsAmount += uF.parseToDouble((String)hmEmpPayrollDetails.get(hraSalaryHeads[i]));
//				}
//				
////				String strHRA = (String)hmSalaryHeadMap.get(HRA+"");
//				String strHRA = (String)hmEmpPayrollDetails.get(HRA+"");
//				
//				String strCondition1 = (String)hmHRAExemption.get("CONDITION_1");
//				String strCondition2 = (String)hmHRAExemption.get("CONDITION_2");
//				String strCondition3 = (String)hmHRAExemption.get("CONDITION_3");
//				
//				double dblCondition1 = uF.parseToDouble(strCondition1);
//				double dblCondition2 = uF.parseToDouble(strCondition2);
//				double dblCondition3 = uF.parseToDouble(strCondition3);
//				
////				double dblHRA1 = dblCondition1 * (dblBasic + dblDA) / 100;
////				double dblHRA2 = dblCondition2 * (dblBasic + dblDA) / 100;
////				double dblHRA3 = dblCondition3 * (dblBasic + dblDA) / 100;
//				double dblHRA1 = dblCondition1 * dblHraSalHeadsAmount / 100;
//				double dblHRA2 = dblCondition2 * dblHraSalHeadsAmount / 100;
//				double dblHRA3 = dblCondition3 * dblHraSalHeadsAmount / 100;
//				
//				double dblActualHRAPaid = uF.parseToDouble(strHRA);
//				double dblActualRentPaid = uF.parseToDouble((String)hmRentPaid.get(strEmpId));
//				
//				double dblHRAExemption = Math.min(dblActualHRAPaid, dblActualRentPaid);
//				boolean isMetro = uF.parseToBoolean((String)hmEmpMertoMap.get(strEmpId));
//				double dblHRA = 0;
//				if(isMetro){
//					if(dblHRA1<dblHRA2){
//						dblHRA = dblHRA1; 
//						hmTaxInner.put("dblHRA", "Rent paid over "+dblCondition1+"% of salary");
//					}else{
//						dblHRA = dblHRA2;
//						hmTaxInner.put("dblHRA", "");
//						hmTaxInner.put("dblHRA", dblCondition2+"% of salary in metro cities");
//					}
//				}else{
//					if(dblHRA1<dblHRA3){
//						dblHRA = dblHRA1;
//						hmTaxInner.put("dblHRA", "");
//						hmTaxInner.put("dblHRA", "Rent paid over "+dblCondition1+"% of salary");
//					}else{
//						dblHRA = dblHRA3;
//						hmTaxInner.put("dblHRA", "");
//						hmTaxInner.put("dblHRA", dblCondition3+"% of salary in other cities");
//					}
//				}
//				
//				dblHRAExemption = Math.min(dblHRAExemption, dblHRA);
//				
//				ApprovePayroll objApprovePayroll = new ApprovePayroll();
//				objApprovePayroll.setServletRequest(request);
//				Map hmEmpRentPaidMap = objApprovePayroll.getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
////				double dblHRAExemptions = objApprovePayroll.getHRAExemptionCalculation(con, uF,null, hmEmpPayrollDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, 0, 0, hmEmpMertoMap, hmEmpRentPaidMap);
//				double dblHRAExemptions = objApprovePayroll.getHRAExemptionCalculation(con, uF,null, hmEmpPayrollDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, dblActualHRAPaid, dblHraSalHeadsAmount, hmEmpMertoMap, hmEmpRentPaidMap);
//				dblHRAExemption = dblHRAExemptions;
//				
//				double dblIncomeFromOther=uF.parseToDouble(hmEmpIncomeFromOtherSourcesMap.get(strEmpId));
//				
//				Map<String, String> hmUS10Inner= (Map<String, String>)hmUnderSection10Map.get(strEmpId);
//				if(hmUS10Inner==null) hmUS10Inner=new HashMap<String, String>();
//				double dblMedicalAllowanceExempt =uF.parseToDouble((String)hmUS10Inner.get("Medical Allowance"));
//				
//				   
////				double dblNetTaxableIncome = dblGross1 - dblProfessionalTaxExempt - dblHRAExemption - dblConveyanceAllowanceExempt - dblInvestment;
////				double dblNetTaxableIncome = dblGross1 - dblProfessionalTaxExempt - dblHRAExemption - dblConveyanceAllowanceExempt - dblInvestmentExempt;
//				double dblNetTaxableIncome = dblGross1 - dblProfessionalTaxExempt - dblHRAExemption - dblConveyanceAllowanceExempt - dblInvestmentExempt -  dblHomeLoanTaxExempt - dbl80D - dblEducationAllowanceExempt-dblMedicalAllowanceExempt;
////				double dblNetTaxableIncome = dblGross1 - dblProfessionalTaxExempt - dblHRAExemption - dblConveyanceAllowanceExempt - dblInvestmentExempt - dblOtherExemptions - dblHomeLoanTaxExempt - dblIncomeFromOther;
//				
//				
//				hmTaxInner.put("dblIncomeFromOther", dblIncomeFromOther+"");  
//				hmTaxInner.put("dblActualHRAPaid", dblActualHRAPaid+"");  
//				hmTaxInner.put("dblActualRentPaid", dblActualRentPaid+"");
//				hmTaxInner.put("dblHRA1", dblHRA+"");
//				hmTaxInner.put("dblCondition1", dblCondition1+"");
//				hmTaxInner.put("dblHRAExemption", dblHRAExemption+"");
//				
//				hmTaxInner.put("dblProfessionalTaxPaid", dblProfessionalTaxPaid+"");
//				hmTaxInner.put("dblProfessionalTaxExempt", dblProfessionalTaxExempt+"");
//				
//				hmTaxInner.put("dblConveyanceAllowancePaid", dblConveyanceAllowancePaid+"");
//				hmTaxInner.put("dblConveyanceAllowanceExempt", dblConveyanceAllowanceExempt+"");
//				
//				hmTaxInner.put("dblOtherExemptions", dblOtherExemptions+"");
//				hmTaxInner.put("dblHomeLoanTaxExempt", dblHomeLoanTaxExempt+"");
//				
////				hmTaxInner.put("dblInvestment", dblInvestmentExempt+"");
////				hmTaxInner.put("dblInvestment", hmEmpExemptionsMap.get(strEmpId) +"");
//				hmTaxInner.put("hmEmpExemptionsCH1Map", hmEmpExemptionsCH1Map.get(strEmpId) +"");
//				hmTaxInner.put("hmEmpExemptionsCH2Map", hmEmpExemptionsCH2Map.get(strEmpId) +"");
//				hmTaxInner.put(TDS, strTDSAmt);
//				
//				hmTaxInner.put("dblEducationAllowancePaid", dblEducationAllowancePaid+"");
//				hmTaxInner.put("dblEducationAllowanceExempt", dblEducationAllowanceExempt+""); 
//
//				double TDSPayable = 0;
//				if(strEmpId!=null && strEmpId.equalsIgnoreCase("460")){					
//					
////					System.out.println("dblGross="+dblGross1);
////					System.out.println("dblProfessionalTaxExempt="+dblProfessionalTaxExempt);
////					System.out.println("dblHRAExemption="+dblHRAExemption);
////					System.out.println("dblConveyanceAllowanceExempt="+dblConveyanceAllowanceExempt); 
////					System.out.println("dblInvestment="+dblInvestmentExempt);
////					System.out.println("dblHomeLoanTaxExempt="+dblHomeLoanTaxExempt);
////					System.out.println("dblOtherExemptions="+dblOtherExemptions);
////					System.out.println("dblIncomeFromOther="+dblIncomeFromOther);
////					System.out.println("dblNetTaxableIncome="+dblNetTaxableIncome);    
////					System.out.println("dblCess1="+dblCess1);
////					System.out.println("dblCess2="+dblCess2);
////					System.out.println("strFinancialYearStart="+strFinancialYearStart);
////					System.out.println("strFinancialYearEnd="+strFinancialYearEnd);
////					System.out.println("Gender="+(String)hmEmpGenderMap.get(strEmpId));
////					System.out.println("Age="+(String)hmEmpAgeMap.get(strEmpId));					
////					System.out.println("dblEducationAllowancePaid="+dblEducationAllowancePaid);
////					System.out.println("dblEducationAllowanceExempt="+dblEducationAllowanceExempt);
//					
////					TDSPayable = calculateTDS(dblNetTaxableIncome, dblCess1, dblCess2, strFinancialYearStart, strFinancialYearEnd, strEmpId, (String)hmEmpGenderMap.get(strEmpId), (String)hmEmpAgeMap.get(strEmpId));		
//				}
//				
//				TDSPayable = calculateTDS(con, dblNetTaxableIncome, dblCess1, dblCess2, strFinancialYearStart, strFinancialYearEnd, strEmpId, (String)hmEmpGenderMap.get(strEmpId), (String)hmEmpAgeMap.get(strEmpId));
//				
//				double dblRebate = 0;
//				if(dblNetTaxableIncome<=500000 &&  TDSPayable<500000){
//					if(TDSPayable>=2000){
//						dblRebate = 2000;
//					}else if(TDSPayable>0 && TDSPayable<2000){
//						dblRebate = TDSPayable;
//					}
//				}
//				
//				double dblCess1Amount = (dblCess1 * (TDSPayable-dblRebate) / 100);
//				double dblCess2Amount = (dblCess2 * (TDSPayable-dblRebate) / 100);
//				
//				if(strEmpId.equals("460")){
////					System.out.println(" TAX_LIABILITY==>"+TDSPayable);
////					System.out.println(" CESS1==>"+dblCess1);
////					System.out.println(" CESS2==>"+dblCess2);
////					System.out.println(" CESS1_AMOUNT==>"+dblCess1Amount);
////					System.out.println(" CESS2_AMOUNT==>"+dblCess2Amount);
////					System.out.println(" TOTAL_TAX_LIABILITY==>"+(TDSPayable + dblCess1Amount+ dblCess2Amount));
//				}
//				
//				hmTaxInner.put("TAX_LIABILITY", TDSPayable+"");
//				hmTaxInner.put("CESS1", dblCess1+"");
//				hmTaxInner.put("CESS2", dblCess2+"");
//				hmTaxInner.put("CESS1_AMOUNT", dblCess1Amount+"");
//				hmTaxInner.put("CESS2_AMOUNT", dblCess2Amount+"");
//				hmTaxInner.put("TOTAL_TAX_LIABILITY", (TDSPayable + dblCess1Amount+ dblCess2Amount)+""); 
//				hmTaxInner.put("TAX_REBATE", dblRebate+"");
//				hmTaxLiability.put(strEmpId, hmTaxInner);
//				
//				
//			}
//			
//			
//			
//			pst = con.prepareStatement("select sum(amount) as amount , emp_id from payroll_generation where salary_head_id = ? and financial_year_from_date=? and financial_year_to_date=? and is_paid = true group by emp_id");
//			pst.setInt(1, TDS);
//			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
////			System.out.println(" pst==>"+pst);
////			System.out.println("9 date==>"+new Date());
//			Map<String, String> hmPaidTdsMap = new HashMap<String, String>();
//			while(rs.next()){
//				hmPaidTdsMap.put(rs.getString("emp_id"), rs.getString("amount"));
//			}
//			rs.close();
//			pst.close();
////			System.out.println("=======14========"+new Date());        
//			
//			
//			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
//			getForm24QHeader(reportListExport,strFinancialYearStart,strFinancialYearEnd,uF);
//			
//			
//			Iterator<String> it1 = hmEmployeeMap.keySet().iterator();
//			List<Map<String, String>> reportList=new ArrayList<Map<String,String>>();
//			int i=0;
//			while(it1.hasNext()){
//				String strEmpId = (String)it1.next();
//				String strEmpName = (String)hmEmployeeMap.get(strEmpId);
//				i++;
//				
//				Map<String, String> hmInner=new HashMap<String, String>();
//				List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
//				
//				hmInner.put("SRNO", ""+i);
//				alInnerExport.add(new DataStyle(""+i,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				hmInner.put("EMP_PAN_NO", hmEmpPanNo.get(strEmpId));
//				alInnerExport.add(new DataStyle(hmEmpPanNo.get(strEmpId),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
//				
//				hmInner.put("EMP_PAN_REF_NO", "");
//				alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
//				
//				hmInner.put("EMP_ID", strEmpId);				
//				
//				
//				hmInner.put("EMP_NAME", strEmpName);
//				alInnerExport.add(new DataStyle(strEmpName,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				
//				
//				double dblYears =uF.parseToDouble(hmEmpAgeMap.get(strEmpId));
//				String residentAge="G";
//				if(dblYears>=80){
//					residentAge="O";
//				}else if(dblYears>80 && dblYears<=60){
//					residentAge="S";
//				}
//				hmInner.put("EMP_RESIDENT_AGE", residentAge);
//				alInnerExport.add(new DataStyle(residentAge,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				String joiningDate=hmEmpJoiningDate.get(strEmpId);
//				String empDateFrom=strFinancialYearStart;
//				if(uF.getDateFormat(joiningDate, DATE_FORMAT).after(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT))){
//					empDateFrom=joiningDate;
//				}
//				hmInner.put("EMP_DATE_FROM", empDateFrom);
//				alInnerExport.add(new DataStyle(empDateFrom,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				String endDate=hmEmpEndDate.get(strEmpId);
//				String empDateUp=strFinancialYearEnd;
//				if(endDate!=null && !endDate.equals("") && !endDate.equals("-") && uF.getDateFormat(endDate, DATE_FORMAT).before(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT))){
//					empDateUp=endDate;
//				}
//				hmInner.put("EMP_DATE_UP", empDateUp);
//				alInnerExport.add(new DataStyle(empDateUp,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				Map hmTaxInner = (Map)hmTaxLiability.get(strEmpId);
//				if(hmTaxInner==null)hmTaxInner=new HashMap();
//				
//
//				Map hmEmpPayrollDetails = (Map)hmPayrollDetails.get(strEmpId);
//				if(hmEmpPayrollDetails==null)hmEmpPayrollDetails=new HashMap();
//				
//				Map<String, String> hmUS10Inner= (Map<String, String>)hmUnderSection10Map.get(strEmpId);
//				if(hmUS10Inner==null) hmUS10Inner=new HashMap<String, String>();
//				
//				if(strEmpId.equals("753")){
//					System.out.println("Gross====>"+hmEmpPayrollDetails.get("GROSS"));
//					System.out.println("Medical Allowance====>"+(String)hmUS10Inner.get("Medical Allowance"));
//					System.out.println("dblConveyanceAllowanceExempt====>"+(String)hmTaxInner.get("dblConveyanceAllowanceExempt"));
//					System.out.println("dblEducationAllowanceExempt====>"+(String)hmTaxInner.get("dblEducationAllowanceExempt"));
//				}
//				
//				double dblGross1 = uF.parseToDouble((String)hmEmpPayrollDetails.get("GROSS")) - (uF.parseToDouble((String)hmUS10Inner.get("Medical Allowance")) + uF.parseToDouble((String)hmTaxInner.get("dblConveyanceAllowanceExempt")) + uF.parseToDouble((String)hmTaxInner.get("dblEducationAllowanceExempt")) );		 		
//				hmInner.put("CURRENT_TAXABLE_TAX_AMT", ""+Math.round(dblGross1)); 
//				alInnerExport.add(new DataStyle(""+Math.round(dblGross1),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				Map<String, String> hmPreTdsEmp=(Map<String, String>) hmPrevEmpTds.get(strEmpId);
//				if(hmPreTdsEmp == null) hmPreTdsEmp= new HashMap<String, String>();
//				double dblPreGross =  uF.parseToDouble((String)hmPreTdsEmp.get("PREV_TOTAL_EARN") );
//				hmInner.put("PREVIOUS_TAXABLE_TAX_AMT", ""+Math.round(dblPreGross));
//				alInnerExport.add(new DataStyle(""+Math.round(dblPreGross),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				double dblTotalAmtSalary = Math.round(dblGross1) + Math.round(dblPreGross);
//				hmInner.put("TOTAL_AMT_SALARY", ""+Math.round(dblTotalAmtSalary));
//				alInnerExport.add(new DataStyle(""+Math.round(dblTotalAmtSalary),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				double dblEntertainmentAllowance=0.0d;
//				hmInner.put("ENTERTAINMENT_ALLOWANCE", ""+Math.round(dblEntertainmentAllowance));
//				alInnerExport.add(new DataStyle(""+Math.round(dblEntertainmentAllowance),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				double dblProfessionalTaxExempt = uF.parseToDouble((String)hmTaxInner.get("dblProfessionalTaxExempt"));
//				hmInner.put("PROFESSIONAL_TAX", ""+Math.round(dblProfessionalTaxExempt));
//				alInnerExport.add(new DataStyle(""+Math.round(dblProfessionalTaxExempt),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				double incomeChargeableSalary = Math.round(dblTotalAmtSalary) - (Math.round(dblEntertainmentAllowance) +Math.round(dblProfessionalTaxExempt)); 
//				hmInner.put("INCOME_CHARGE_SALARY", ""+Math.round(incomeChargeableSalary));
//				alInnerExport.add(new DataStyle(""+Math.round(incomeChargeableSalary),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
////				double dblIncomeFromOther=uF.parseToDouble((String)hmTaxInner.get("dblIncomeFromOther")) ; dblHomeLoanTaxExempt
//				double dblIncomeFromOther=uF.parseToDouble((String)hmTaxInner.get("dblHomeLoanTaxExempt")) ; 
//				hmInner.put("INCOME_FROM_OTHER", ""+dblIncomeFromOther);
//				alInnerExport.add(new DataStyle(""+dblIncomeFromOther,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				double dblGrossTotal=Math.round(incomeChargeableSalary)+Math.round(dblIncomeFromOther);
//				hmInner.put("GROSS_TOTAL", ""+Math.round(dblGrossTotal));
//				alInnerExport.add(new DataStyle(""+Math.round(dblGrossTotal),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				double dblChapterVIA1=uF.parseToDouble(hmEmpExemptionsCH1Map.get(strEmpId));
//				hmInner.put("CHAPTER_VI_A1", ""+Math.round(dblChapterVIA1));
//				alInnerExport.add(new DataStyle(""+Math.round(dblChapterVIA1),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				double dblChapterVIA2=uF.parseToDouble(hmEmpExemptionsCH2Map.get(strEmpId));
//				hmInner.put("CHAPTER_VI_A2", ""+Math.round(dblChapterVIA2));
//				alInnerExport.add(new DataStyle(""+Math.round(dblChapterVIA2),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
////				double dblChapterVIAOther=0.0d; dblOtherExemptions
//				double dblChapterVIAOther=uF.parseToDouble((String)hmTaxInner.get("dblOtherExemptions")); 
//				hmInner.put("CHAPTER_VI_AOTHER", ""+Math.round(dblChapterVIAOther));
//				alInnerExport.add(new DataStyle(""+Math.round(dblChapterVIAOther),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				double dblTotalAmtChapetrVIA=Math.round(dblChapterVIA1)+Math.round(dblChapterVIA2)+Math.round(dblChapterVIAOther);
//				hmInner.put("TOTAL_CHAPTER_VIA", ""+Math.round(dblTotalAmtChapetrVIA));
//				alInnerExport.add(new DataStyle(""+Math.round(dblTotalAmtChapetrVIA),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				double dblTotalTaxIncome=Math.round(dblGrossTotal) - Math.round(dblTotalAmtChapetrVIA);
//				hmInner.put("TOTAL_TAX_INCOME", ""+Math.round(dblTotalTaxIncome));
//				alInnerExport.add(new DataStyle(""+Math.round(dblTotalTaxIncome),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				if("460".equalsIgnoreCase(strEmpId)){
////					System.out.println("(String)hmTaxInner.get(TAX_LIABILIT)====>"+(String)hmTaxInner.get("TAX_LIABILITY"));
//				}
//				
//				double dblIncomeTax=uF.parseToDouble((String)hmTaxInner.get("TAX_LIABILITY"));
//				hmInner.put("INCOME_TAX", ""+Math.round(dblIncomeTax));
//				alInnerExport.add(new DataStyle(""+Math.round(dblIncomeTax),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
////				double dblRebate = 0;
////				if(dblIncomeTax<500000){
////					if(dblIncomeTax>=2000){
////						dblRebate = 2000;
////					}else if(dblIncomeTax>0 && dblIncomeTax<2000){
////						dblRebate = dblIncomeTax;
////					}
////				}
////				hmInner.put("TAX_REBATE", ""+Math.round(dblRebate));
//				
//				double dblRebate = uF.parseToDouble((String)hmTaxInner.get("TAX_REBATE"));
//				hmInner.put("TAX_REBATE", ""+Math.round(dblRebate));
//				alInnerExport.add(new DataStyle(""+Math.round(dblRebate),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				double dblEduCess=uF.parseToDouble((String)hmTaxInner.get("CESS2_AMOUNT")) + uF.parseToDouble((String)hmTaxInner.get("CESS1_AMOUNT")); 
//				hmInner.put("EDU_CESS", ""+Math.round(dblEduCess));
//				alInnerExport.add(new DataStyle(""+Math.round(dblEduCess),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				double dblTaxPayable=(Math.round(dblIncomeTax) - Math.round(dblRebate))+ Math.round(dblEduCess);
//				hmInner.put("TAX_PAYABLE", ""+Math.round(dblTaxPayable));
//				alInnerExport.add(new DataStyle(""+Math.round(dblTaxPayable),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				double dblTaxRelief=0.0d;
//				hmInner.put("INCOME_TAX_RELIEF", ""+Math.round(dblTaxRelief));
//				alInnerExport.add(new DataStyle(""+Math.round(dblTaxRelief),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				double dblNetTaxIncome=Math.round(dblTaxPayable) - Math.round(dblTaxRelief);
//				hmInner.put("NET_TAX_INCOME", ""+Math.round(dblNetTaxIncome));
//				alInnerExport.add(new DataStyle(""+Math.round(dblNetTaxIncome),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				double dblTDS = uF.parseToDouble((String)hmPaidTdsMap.get(strEmpId));
//				hmInner.put("TDS_AMT", ""+Math.round(dblTDS));
//				alInnerExport.add(new DataStyle(""+Math.round(dblTDS),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				double dblPreEmpTDS = uF.parseToDouble((String)hmPreTdsEmp.get("PREV_TOTAL_DEDUCT") );
//				hmInner.put("PREV_EMP_TDS_AMT", ""+Math.round(dblPreEmpTDS));
//				alInnerExport.add(new DataStyle(""+Math.round(dblPreEmpTDS),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				double dblTotalTDS=Math.round(dblTDS) + Math.round(dblPreEmpTDS);
//				hmInner.put("TOTAL_TDS", ""+Math.round(dblTotalTDS));
//				alInnerExport.add(new DataStyle(""+Math.round(dblTotalTDS),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				double dblShortFallTDS=Math.round(dblNetTaxIncome) - Math.round(dblTotalTDS);
//				hmInner.put("SHORTFALL_TDS", ""+Math.round(dblShortFallTDS));
//				alInnerExport.add(new DataStyle(""+Math.round(dblShortFallTDS),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				hmInner.put("FURNISH_PAN", hmEmpPanNo.get(strEmpId)!=null ? "No" : "");
//				alInnerExport.add(new DataStyle((hmEmpPanNo.get(strEmpId)!=null ? "No" : ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				reportList.add(hmInner);
//				reportListExport.add(alInnerExport);
//			}
//
//			request.setAttribute("reportList", reportList);
//			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
//			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
//			request.setAttribute("reportListExport", reportListExport);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}finally{
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		
//		
//		return SUCCESS;
//		
//	}
	
	private List<List<DataStyle>> getForm24QHeaderSheet1(String strOrgName, String strFinancialYearStart, String strFinancialYearEnd, UtilityFunctions uF) {
		
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		
		alInnerExport.add(new DataStyle("Form : 24Q", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Quarter : 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("F. Y.: "+uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy") +" - "+uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("File: "+uF.showData(strOrgName, "-"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		//----------------------------------End-------------------------------------------------------------
		
		reportListExport.add(alInnerExport);
		
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("ID No.", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Date of Challan", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Deposited - Tax", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("SC", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Health & Education Cess", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Interest", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Fee", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Other Amount", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Total Amount Deposited", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Book - Entry ?", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Challan / DDO Serial No.", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Bank Branch Code", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Section", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Type of Payment", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Interest allocated for the quarter", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Others allocated for the quarter", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		//----------------------------------End------------------------------------------
		
		reportListExport.add(alInnerExport);
		
		return reportListExport;
	}
	
	
	private List<List<DataStyle>> getForm24QHeaderSheet2(String strOrgName, String strFinancialYearStart, String strFinancialYearEnd, UtilityFunctions uF) {
		
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		
		alInnerExport.add(new DataStyle("Form : 24Q", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Quarter : 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("F. Y.: "+uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy") +" - "+uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("File: "+uF.showData(strOrgName, "-"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		reportListExport.add(alInnerExport);
		//----------------------------------End-------------------------------------------------------------
		
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Challan ID No. / details", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Employee ID", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Amount Paid / Credited", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Paid / Credited Date", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Deduction Date", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Deducted and deposited - Tax", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("SC", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Health & Education Cess", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Reason for Lower/Higher Deduction", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Certificate number u/s 197", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Section", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		reportListExport.add(alInnerExport);
		//----------------------------------End------------------------------------------
		
		return reportListExport;
	}
	
	
	private List<List<DataStyle>> getForm24QHeaderSheet3(String strOrgName, String strFinancialYearStart, String strFinancialYearEnd, UtilityFunctions uF) {
		
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		
		alInnerExport.add(new DataStyle("Form : 24Q", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Quarter : 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("F. Y.: "+uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy") +" - "+uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("File: "+uF.showData(strOrgName, "-"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End-------------------------------------------------------------
		
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Employee ID", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Designation", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Employment Period From", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Employment Period To", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Type of Deductee", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Salary u/s 17(1)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Perquisites u/s 17(2)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Profits in lieu of salary u/s 17(3)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Allowances exempt u/s 10", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Gross Salary from other employer", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Standard deduction u/s 16(ia)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Entertainment allowance u/s 16(ii)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Tax on employment u/s 16(iii)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Other Income/House property loss", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Details of Landlord/Lender", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Investment u/s 80C, CCC, CCD", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("80D - Medical insurance premia", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("80E - Interest on education loan repayment", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("80G - Donations", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Other Deductions U/C VI A", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Income Tax", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Rebate u/s 87A", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Relief U/S 89", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("TDS", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("TDS by other employers", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("TDS on Superannuation fund repayment", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End------------------------------------------
		
		return reportListExport;
	}
	
	
	private List<List<DataStyle>> getForm24QHeaderSheet4(String strOrgName, String strFinancialYearStart, String strFinancialYearEnd, UtilityFunctions uF) {
		
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		
		alInnerExport.add(new DataStyle("Form : 24Q", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Quarter : 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("F. Y.: "+uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy") +" - "+uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("File: "+uF.showData(strOrgName, "-"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End-------------------------------------------------------------
		
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Basic Salary", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Dearness allowance", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Other Remuneration treated as salary for HRA", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("House Rent Allowance", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End------------------------------------------
		
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Employee ID", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Received", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Received", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Exempt", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Exemption u/s 10 - clause", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Received", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Exempt", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Exemption u/s 10 - clause", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Other Salary", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Received", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Exempt", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Exemption u/s 10 - clause", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Other Allowances", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End------------------------------------------
		
		
		return reportListExport;
	}
	
	
	private List<List<DataStyle>> getForm24QHeaderSheet5(String strOrgName, String strFinancialYearStart, String strFinancialYearEnd, UtilityFunctions uF) {
		
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		
		alInnerExport.add(new DataStyle("Form : 24Q", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Quarter : 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("F. Y.: "+uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy") +" - "+uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("File: "+uF.showData(strOrgName, "-"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End-------------------------------------------------------------
		
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Employee ID", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Particulars", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Amount received", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Exempt", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Exemption u/s 10 - clause", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End------------------------------------------
		
		return reportListExport;
	}
	
	
	private List<List<DataStyle>> getForm24QHeaderSheet6(String strOrgName, String strFinancialYearStart, String strFinancialYearEnd, UtilityFunctions uF) {
		
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		
		alInnerExport.add(new DataStyle("Form : 24Q", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Quarter : 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("F. Y.: "+uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy") +" - "+uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("File: "+uF.showData(strOrgName, "-"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End-------------------------------------------------------------
		
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Employee ID", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Particulars", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Amount received", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Exempt", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Exemption u/s 10 - clause", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End------------------------------------------
		
		return reportListExport;
	}

	
	private List<List<DataStyle>> getForm24QHeaderSheet7(String strOrgName, String strFinancialYearStart, String strFinancialYearEnd, UtilityFunctions uF) {
		
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		
		alInnerExport.add(new DataStyle("Form : 24Q", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Quarter : 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("F. Y.: "+uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy") +" - "+uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("File: "+uF.showData(strOrgName, "-"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End-------------------------------------------------------------
		
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Employee ID", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Perquisites", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Value", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Recovered", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Exempt Perquisites", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End------------------------------------------
		
		return reportListExport;
	}
	
	
	private List<List<DataStyle>> getForm24QHeaderSheet8(String strOrgName, String strFinancialYearStart, String strFinancialYearEnd, UtilityFunctions uF) {
		
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		
		alInnerExport.add(new DataStyle("Form : 24Q", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Quarter : 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("F. Y.: "+uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy") +" - "+uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("File: "+uF.showData(strOrgName, "-"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End-------------------------------------------------------------
		
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Employee ID", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Exempt Perquisites - Sec. 10", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Amount", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Clause", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End------------------------------------------
		
		return reportListExport;
	}
	
	
	private List<List<DataStyle>> getForm24QHeaderSheet9(String strOrgName, String strFinancialYearStart, String strFinancialYearEnd, UtilityFunctions uF) {
		
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		
		alInnerExport.add(new DataStyle("Form : 24Q", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Quarter : 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("F. Y.: "+uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy") +" - "+uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("File: "+uF.showData(strOrgName, "-"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End-------------------------------------------------------------
		
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Employee ID", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Particulars", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Amount received", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Exempt", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Exemption u/s 10 - clause", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End------------------------------------------
		
		return reportListExport;
	}

	
	private List<List<DataStyle>> getForm24QHeaderSheet10(String strOrgName, String strFinancialYearStart, String strFinancialYearEnd, UtilityFunctions uF) {
		
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		
		alInnerExport.add(new DataStyle("Form : 24Q", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Quarter : 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("F. Y.: "+uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy") +" - "+uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("File: "+uF.showData(strOrgName, "-"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End-------------------------------------------------------------
		
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Employee ID", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Gratuity exempt u/s 10(10)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Commuted pension exempt u/s 10(10A)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Leave encashment exempt u/s 10(10AA)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("HRA exempt u/s 10(13A)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Leave travel concession exempt u/s 10(5)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End------------------------------------------
		
		return reportListExport;
	}
	
	
	private List<List<DataStyle>> getForm24QHeaderSheet11(String strOrgName, String strFinancialYearStart, String strFinancialYearEnd, UtilityFunctions uF) {
		
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		
		alInnerExport.add(new DataStyle("Form : 24Q", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Quarter : 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("F. Y.: "+uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy") +" - "+uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("File: "+uF.showData(strOrgName, "-"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End-------------------------------------------------------------
		
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Employee ID", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Other Allowances exempt u/s 10 Description", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Amount", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Clause", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End------------------------------------------
		
		return reportListExport;
	}
	
	
	private List<List<DataStyle>> getForm24QHeaderSheet12(String strOrgName, String strFinancialYearStart, String strFinancialYearEnd, UtilityFunctions uF) {
		
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		
		alInnerExport.add(new DataStyle("Form : 24Q", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Quarter : 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("F. Y.: "+uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy") +" - "+uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("File: "+uF.showData(strOrgName, "-"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End-------------------------------------------------------------
		
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Employee ID", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Other income", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Interest on savings bank account", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Interest from Bank/Post office", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("House Property income", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Interest on borrowed capital - self occupied property", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Loss from Let-out Property", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End------------------------------------------
		
		return reportListExport;
	}
	
	
	private List<List<DataStyle>> getForm24QHeaderSheet13(String strOrgName, String strFinancialYearStart, String strFinancialYearEnd, UtilityFunctions uF) {
		
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		
		alInnerExport.add(new DataStyle("Form : 24Q", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Quarter : 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("F. Y.: "+uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy") +" - "+uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("File: "+uF.showData(strOrgName, "-"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End-------------------------------------------------------------
		
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Employee ID", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Other income Description", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Amount", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End------------------------------------------
		
		return reportListExport;
	}
	
	
	private List<List<DataStyle>> getForm24QHeaderSheet14(String strOrgName, String strFinancialYearStart, String strFinancialYearEnd, UtilityFunctions uF) {
		
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		
		alInnerExport.add(new DataStyle("Form : 24Q", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Quarter : 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("F. Y.: "+uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy") +" - "+uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("File: "+uF.showData(strOrgName, "-"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End-------------------------------------------------------------
		
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Employee ID", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name of Landlord-1", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN of Landlord-1", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name of Landlord-2", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN of Landlord-2", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name of Landlord-3", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN of Landlord-3", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name of Landlord-4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN of Landlord-4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name of Lender-1", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN of Lender-1", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name of Lender-2", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN of Lender-2", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name of Lender-3", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN of Lender-3", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name of Lender-4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN of Lender-4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End------------------------------------------
		
		return reportListExport;
	}
	
	
	private List<List<DataStyle>> getForm24QHeaderSheet15(String strOrgName, String strFinancialYearStart, String strFinancialYearEnd, UtilityFunctions uF) {
		
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		
		alInnerExport.add(new DataStyle("Form : 24Q", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Quarter : 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("F. Y.: "+uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy") +" - "+uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("File: "+uF.showData(strOrgName, "-"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End-------------------------------------------------------------
		
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Employee ID", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Investment u/s 80C", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("80CCC: Contribution to pension fund", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("NPS - Employee's contribution", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Deduction u/s 80CCD(1)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("NPS Contribution by Employer u/s 80CCD(2)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Deduction u/s 80CCD(2)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End------------------------------------------
		
		return reportListExport;
	}
	
	
	private List<List<DataStyle>> getForm24QHeaderSheet16(String strOrgName, String strFinancialYearStart, String strFinancialYearEnd, UtilityFunctions uF) {
		
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		
		alInnerExport.add(new DataStyle("Form : 24Q", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Quarter : 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("F. Y.: "+uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy") +" - "+uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("File: "+uF.showData(strOrgName, "-"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End-------------------------------------------------------------
		
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Employee ID", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Investment u/s 80C - Description", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Amount", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End------------------------------------------
		
		return reportListExport;
	}

	
	private List<List<DataStyle>> getForm24QHeaderSheet17(String strOrgName, String strFinancialYearStart, String strFinancialYearEnd, UtilityFunctions uF) {
		
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		
		alInnerExport.add(new DataStyle("Form : 24Q", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Quarter : 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("F. Y.: "+uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy") +" - "+uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("File: "+uF.showData(strOrgName, "-"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End-------------------------------------------------------------
		
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Employee ID", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("80DD: Medical treatment of Handicapped", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("80DDB: Medical treatment of specified diseases", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("80EE: Interest on Housing Loan", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("80EEA: Interest on Housing loan", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("80EEB: Interest on Electric vehicle loan", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("80GG - Rent paid", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("80GGA: Donation for scientific research etc.", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("80RRB: Royalty on patents", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("80QQB: Royalty income of authors", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("80U: Income of person with disability", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name of the Superannuation fund", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Date from which contributed", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Date to which contributed", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Amount repaid", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Tax deducted on repayment", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		//----------------------------------End------------------------------------------
		
		return reportListExport;
	}


	
	
	private void getForm24QHeader(List<List<DataStyle>> reportListExport, String strFinancialYearStart, String strFinancialYearEnd, UtilityFunctions uF) {
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		
		alInnerExport.add(new DataStyle("Form 24Q during the financial year "+uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy") +" - "+uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Salary Details  Record No (Serial Number of Employee)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Permanent Account Number (PAN) of the Employee", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Pan Ref. No", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name of the Employee", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Category of Employee", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Date from Which Employeed with the current employer (dd/mm/yyyy)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Date upto Which Employeed with the current employer (dd/mm/yyyy)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Taxable amount on Which Tax Deducted By Current Employer", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Reported Taxable Amount by Previous Employer", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Total Amount of Salary (333+ 334)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Entertainment Allowance 16(ii)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Total Deduction u/s 16 (iii)- Professional Tax", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Income chargeable under the head Salaries (Column 335-(336+337))", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Income (including loss from house property) under any head other than income under the head \"salaries\" offered for TDS [section 192 (2B)]", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Gross Total Income (Total of Column 338 + 339)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		//----------------------------------End-------------------------------------------------------------
		
		alInnerExport.add(new DataStyle("Aggregate amount of deductions under sections 80C, 80CCC and 80CCD (Total to be limited to amount specified in section 80CCE)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("80CCG- Amount deductible under section 80CCG and the said amount should be <=25,000", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Amount Deductible under any other provision(s) of Chapter VI A", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Gross Total of 'Amount deductible under provisions of chapter VI-A' under  associated ' Salary Details  - Chapter VIA Detail ' (341+342)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Total Taxable Income (Column 340 - 343)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Income Tax on Total Income", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Surcharge", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Education Cess", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Income Tax Relief u/s 89 when salary etc is paid in arrear or advance", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Net Income Tax payable (345+346-347)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Total amount of tax deducted at source for the whole year", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Reported amount of tax deducted at source by previous employer(s)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Total amount of Tax Deducted for Whole year (Total of column 349 +350", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Shortfall in tax deduction (+)/Excess tax deduction(-) [348-351]", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Whether Tax Deducted at higher rate due to non furnishing of PAN by deductee", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		//Added by M@yuri.B  (Added column)
		alInnerExport.add(new DataStyle("Whether house rent allowance claim (aggregate payment) exceeds rupees one lakh during previous", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Count of PAN of the landlord", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN of landlord 1", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name of landlord 1", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN of landlord 2", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name of landlord 2", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN of landlord 3", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name of landlord 3", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("PAN of landlord 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name of landlord 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Whether Interest paid  to the lender under the head 'Income from house property'. (358)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Count of PAN of the lender", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("In case of deduction of interest under the head income from house property - PAN of lender 1", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("In case of deduction of interest under the head income from house property - Name of lender 1", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("In case of deduction of interest under the head income from house property - PAN of lender 2", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("In case of deduction of interest under the head income from house property - Name of lender 2", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("In case of deduction of interest under the head income from house property - PAN of lender 3", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("In case of deduction of interest under the head income from house property - Name of lender 3", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("In case of deduction of interest under the head income from house property - PAN of lender 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("In case of deduction of interest under the head income from house property - Name of lender 4", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		//Added by M@yuri.B  (Added column)
		alInnerExport.add(new DataStyle("Whether contributions paid by the trustees of an approved superannuation fund", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Name of the superannuation fund", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Date from which the employee has contributed to the superannuation fund", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Date to which the employee has contributed to the superannuation fund", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("The amount of contribution repaid on account of principal and interest from superannuation fund", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("The average rate of deduction of tax during the preceding three years", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("The amount of tax deducted on repayment of superannuation fund", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("Gross total income including contribution repaid on account of principal and interest from", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		//----------------------------------End-------------------------------------------------------------
		
		alInnerExport.add(new DataStyle("Error description", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		reportListExport.add(alInnerExport);
		
		alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("328", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("329", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("330", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("331", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("332", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("332", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("333", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("334", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("335", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("336", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("337", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("338", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("339", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("340", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		//----------------------------------End------------------------------------------
		
		alInnerExport.add(new DataStyle("341", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("341", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("342", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("343", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("344", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("345", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("346", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("347", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("348", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("349", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("350", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("351", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("352", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("353", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		
		//Added by M@yuri------------------------------------------------------------------------------
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		//Added by M@yuri.B-----------------------------------------------------------------
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		//-----------------------------End-------------------------------------------------------------
		alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		reportListExport.add(alInnerExport);
	}


	private double calculateTDS(Connection con, double dblTotalTaxableSalary, double dblCess1, double dblCess2, String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, String strGender, String strAge, String slabType) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		double dblTotalTDSPayable = 0.0d;
		
		try {
			
			int countBug = 0;
			double dblUpperDeductionSlabLimit = 0;
			double dblLowerDeductionSlabLimit = 0;
			double dblTotalNetTaxableSalary = 0; 
			do{
				
				pst = con.prepareStatement("select * from deduction_tax_details where age_from <= ? and age_to > ? and gender = ? and financial_year_from = ? and financial_year_to = ?  and _from <= ? and _to>? and slab_type = ?  order by _from limit 1");
				pst.setDouble(1, uF.parseToDouble(strAge));
				pst.setDouble(2, uF.parseToDouble(strAge));
				pst.setString(3, strGender);
				pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDouble(6, dblTotalTaxableSalary);
				pst.setDouble(7, dblUpperDeductionSlabLimit);
				pst.setInt(8,uF.parseToInt(slabType));

				rs = pst.executeQuery();
				if(strEmpId.equalsIgnoreCase("465")){
//					System.out.println("pst=====>"+pst);
				}
//				System.out.println("11 date==>"+new Date());  
				double dblDeductionAmount = 0;
				String strDeductionType = null;
				if(rs.next()){
					dblDeductionAmount = rs.getDouble("deduction_amount");
					strDeductionType = rs.getString("deduction_type");
					dblUpperDeductionSlabLimit = rs.getDouble("_to");
					dblLowerDeductionSlabLimit = rs.getDouble("_from");
				}
				rs.close();
				pst.close();
				
				if(countBug==0){
					dblTotalNetTaxableSalary = dblTotalTaxableSalary;
				}
				
				if(dblTotalTaxableSalary>=dblUpperDeductionSlabLimit){
					dblTotalTDSPayable += ((dblDeductionAmount /100) *  (dblUpperDeductionSlabLimit - dblLowerDeductionSlabLimit) );
					
//					System.out.println("=====IF=========");
//					
//					System.out.println("dblTotalTDSPayable=====>"+dblTotalTDSPayable);
//					System.out.println("dblDeductionAmount=====>"+dblDeductionAmount);
//					System.out.println("dblTotalNetTaxableSalary=====>"+dblTotalNetTaxableSalary);
					
				}else{
					dblTotalTDSPayable += ((dblDeductionAmount /100) *  dblTotalNetTaxableSalary );
				
//					System.out.println("=====ELSE=========");
//					
//					System.out.println("dblTotalTDSPayable=====>"+dblTotalTDSPayable);
//					System.out.println("dblDeductionAmount=====>"+dblDeductionAmount);
//					System.out.println("dblTotalNetTaxableSalary=====>"+dblTotalNetTaxableSalary);
					  
				}
				
				dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblUpperDeductionSlabLimit;
				
				

				if(countBug==15)break;		// in case of any bug, this condition is used to avoid any stoppage 
				countBug++;
				
			}while(dblTotalNetTaxableSalary>0);
			

			// Service tax + Education cess

			/*double dblCess = dblTotalTDSPayable * (dblCess1 / 100);
			dblCess += dblTotalTDSPayable * (dblCess2 / 100);

			dblTotalTDSPayable += dblCess;


			System.out.println("dblTotalGrossSalary=" + dblTotalTaxableSalary);
			System.out.println("dblTotalTDSPayable=" + dblTotalTDSPayable);
			System.out.println("dblTotalTDSPayable=" + dblTotalTDSPayable);
			System.out.println("=============" + strEmpId + "=================");
*/
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblTotalTDSPayable;

	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}


	public String getFinancialYear() {
		return financialYear;
	}


	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}


	public String getPaycycle() {
		return paycycle;
	}


	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}


	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}


	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
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


	public String[] getF_strWLocation() {
		return f_strWLocation;
	}


	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}


	public String[] getF_level() {
		return f_level;
	}


	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}


	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}


	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}


	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}


	public String[] getF_department() {
		return f_department;
	}


	public void setF_department(String []f_department) {
		this.f_department = f_department;
	}


	public String[] getF_service() {
		return f_service;
	}


	public void setF_service(String []f_service) {
		this.f_service = f_service;
	}


	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}


	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}


	public List<FillServices> getServiceList() {
		return serviceList;
	}


	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
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


	public String getStrLocation() {
		return strLocation;
	}


	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
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

}